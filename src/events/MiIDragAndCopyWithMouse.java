
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Framework                 *
 ***************************************************************************
 * NOTICE: Permission to use, copy, and modify this software and its       *
 * documentation is hereby granted provided that this notice appears in    *
 * all copies.                                                             *
 *                                                                         *
 * Permission to distribute un-modified copies of this software and its    *
 * documentation is hereby granted provided that no fee is charged and     *
 * that this notice appears in all copies.                                 *
 *                                                                         *
 * SOFTWARE FARM MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE          *
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING, BUT  *
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR  *
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SOFTWARE FARM SHALL NOT BE   *
 * LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR       *
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE, MODIFICATION OR           *
 * DISTRIBUTION OF THIS SOFTWARE OR ITS DERIVATIVES.                       *
 *                                                                         *
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND      *
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,        *
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS.                                *
 *                                                                         *
 ***************************************************************************
 *   Copyright (c) 1997-2004 Software Farm, Inc.  All Rights Reserved.     *
 ***************************************************************************
 */

package com.swfm.mica;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIDragAndCopyWithMouse extends MiEventHandler
	{
	public static final	String	Mi_COPY_PICK_UP_COMMAND_NAME		= "copyPickUp";
	public static final	String	Mi_ORTHO_PICK_UP_COMMAND_NAME		= "orthoPickUp";

	protected	MiPart		draggedObj 			= null;
	private		boolean		snapObjectCenterToCursor 	= false;
	private		boolean		glueObjectStartLocationToCursor = true;
	private		boolean		selectObjectWhenDropped 	= true;
	private		MiPoint		startDragOffsetFromCenter	= new MiPoint();
	private		MiPoint		pickUpPoint			= new MiPoint();
	private		boolean		notificationsEnabled;
	private		boolean		moveInOrthogonalDirectionsOnly;
	private		boolean		createdDraggedObj;
	private		MiPoint		originalLocation 		= new MiPoint();
	private		MiBounds	originalWorld 			= new MiBounds();
	private		MiBounds	tmpBounds			= new MiBounds();





	public				MiIDragAndCopyWithMouse()
		{
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_ORTHO_PICK_UP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_COPY_PICK_UP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, 
			Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		}

	public		void		setGlueObjectStartLocationToCursor(boolean flag)
		{
		glueObjectStartLocationToCursor = flag;
		}
	public		boolean		getGlueObjectStartLocationToCursor()
		{
		return(glueObjectStartLocationToCursor);
		}

	public		void		setSelectObjectWhenDropped(boolean flag)
		{
		selectObjectWhenDropped = flag;
		}
	public		boolean		getSelectObjectWhenDropped()
		{
		return(selectObjectWhenDropped);
		}
/*
If non-shifted mouse down in non-seleted obj, deselect all
ctl-click || shift-click to select multiple items
drag selcted item drags all selected items
ctl drag copies
shift-drag selectsArea if outside selected items else drags items (in ortho directions from orig location)
ctl-drag selectsArea if outside selected items else COPIES items
dragged item becomes selected after drop
*/

					/**------------------------------------------------------
	 				 * Processes the command generated from the current event.
					 * Both are stored in the MiEventHandler super class.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			MiEventHandler#isCommand
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		MiEditor editor = event.editor;
		if ((isCommand(Mi_PICK_UP_COMMAND_NAME))
			|| (isCommand(Mi_ORTHO_PICK_UP_COMMAND_NAME))
			|| (isCommand(Mi_COPY_PICK_UP_COMMAND_NAME)))
			{
			if (draggedObj == null)
				{
				createdDraggedObj = false;
				moveInOrthogonalDirectionsOnly = false;

				MiParts targetPath = event.getTargetPath();
				int index = targetPath.indexOf(editor.getCurrentLayer());
				if (index > 0)
					{
					MiPart obj = targetPath.elementAt(index - 1);
					if (obj.isMovable())
						{
						if (obj.isSelected())
							{
							draggedObj = pickUpSelectedObjects(editor,
									isCommand(Mi_COPY_PICK_UP_COMMAND_NAME));
							}
						else
							{
							if (isCommand(Mi_COPY_PICK_UP_COMMAND_NAME))
								{
								draggedObj = obj.deepCopy();
								createdDraggedObj = true;
								editor.appendPart(draggedObj);
								}
							else
								{
								draggedObj = obj;
								}
							}
						}
					}
				if (draggedObj != null)
					{
					editor.getWorldBounds(originalWorld);
					draggedObj.getCenter(originalLocation);

					notificationsEnabled = draggedObj.getInvalidLayoutNotificationsEnabled();
					draggedObj.setInvalidLayoutNotificationsEnabled(false);
					editor.prependGrabEventHandler(this);
					if (glueObjectStartLocationToCursor)
						{
						startDragOffsetFromCenter.set(
							event.worldPt.x - draggedObj.getCenterX(),
							event.worldPt.y - draggedObj.getCenterY());
						}
					if (isCommand(Mi_ORTHO_PICK_UP_COMMAND_NAME))
						{
						moveInOrthogonalDirectionsOnly = true;
						pickUpPoint.copy(event.worldPt);
						}
					return(drag());
					}
				else if (isCommand(Mi_PICK_UP_COMMAND_NAME))
					{
					MiDeSelectAll.processCommand(editor, null);
					}
				}
			return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			return(drag());
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (draggedObj == null)
				return(Mi_PROPOGATE_EVENT);

			editor.removeGrabEventHandler(this);
			if (selectObjectWhenDropped)
				editor.select(draggedObj);

			MiVector amountTranslated = new MiVector(
				draggedObj.getCenterX() - originalLocation.x,
				draggedObj.getCenterY() - originalLocation.y);

			if (!originalWorld.equals(editor.getWorldBounds()))
				{
				if (createdDraggedObj)
				    {
				    MiSystem.getTransactionManager().appendTransaction(
					MiiDisplayNames.Mi_TRANSLATE_DISPLAY_NAME,
					new MiDeletePartsCommand(editor, draggedObj, false),
					new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()),
					new MiTranslatePartsCommand(editor, draggedObj, amountTranslated));
				    }
				else
				    {
				    MiSystem.getTransactionManager().appendTransaction(
					MiiDisplayNames.Mi_TRANSLATE_DISPLAY_NAME,
					new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()),
					new MiTranslatePartsCommand(editor, draggedObj, amountTranslated));
				    }
				MiSystem.getViewportTransactionManager().appendTransaction(
					new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
				}
			else
				{
				MiSystem.getTransactionManager().appendTransaction(
					new MiTranslatePartsCommand(editor, draggedObj, amountTranslated));
				}
			draggedObj.setInvalidLayoutNotificationsEnabled(notificationsEnabled);
			draggedObj = null;
			return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}


	protected	int		drag()
		{
		MiEditor	editor = event.editor;

		if (draggedObj == null)
			{
			return(Mi_PROPOGATE_EVENT);
			}

//System.out.println("DRAGGING OBJECT: " + draggedObj);

		// Do this invaldiate cause xlate parts does not invalidate area
		event.editor.invalidateArea(draggedObj.getDrawBounds(tmpBounds));

		MiVector delta = new MiVector(event.worldVector);
		if (snapObjectCenterToCursor)
			{
			delta.x = event.worldPt.x - draggedObj.getCenterX();
			delta.y = event.worldPt.y - draggedObj.getCenterY();
			}
		else if (glueObjectStartLocationToCursor)
			{
			delta.x = event.worldPt.x - (draggedObj.getCenterX() + startDragOffsetFromCenter.x);
			delta.y = event.worldPt.y - (draggedObj.getCenterY() + startDragOffsetFromCenter.y);
			}

		boolean modifiedDelta = editor.autopanForMovingObj(draggedObj.getDrawBounds(tmpBounds), delta);

		if ((!modifiedDelta) && (snapObjectCenterToCursor))
			{
			draggedObj.setCenter(event.worldPt);
			}
		else if ((!delta.isZero()) && (moveInOrthogonalDirectionsOnly))
			{
			if (Math.abs(delta.x + draggedObj.getCenterX() - pickUpPoint.x) 
				> Math.abs(delta.y + draggedObj.getCenterY() - pickUpPoint.y))
				{
				delta.y = 0;
				draggedObj.setCenterY(pickUpPoint.y);
				}
			else
				{
				delta.x = 0;
				draggedObj.setCenterX(pickUpPoint.x);
				}
			draggedObj.translate(delta);
			}
		else if (!delta.isZero())
			{
			draggedObj.translate(delta);
			}

		event.editor.invalidateArea(draggedObj.getDrawBounds(tmpBounds));

		return(Mi_CONSUME_EVENT);
		}


	protected	MiPart		pickUpSelectedObjects(MiEditor editor, boolean copy)
		{
		MiParts selectedObjects = new MiParts();
		editor.getSelectedParts(selectedObjects);
		MiPart container = null;

		for (int i = 0; i < selectedObjects.size(); ++i)
			{
			MiPart obj = selectedObjects.elementAt(i);
			if (obj.isMovable())
				{
				if (container == null)
					container = new MiContainer();

				if (copy)
					{
					MiPart part = obj.deepCopy();
					event.editor.appendPart(part);
					container.appendPart(part);
					}
				else
					{
					container.appendPart(obj);
					}
				}
			}
		if ((copy) && (container != null))
			{
			createdDraggedObj = true;
			}
		return(container);
		}
	}


