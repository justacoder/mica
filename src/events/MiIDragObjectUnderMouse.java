
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
public class MiIDragObjectUnderMouse extends MiEventHandler implements MiiActionTypes
	{
	protected	MiPart		draggedObj 			= null;
	protected	MiPoint		originalLocation 		= new MiPoint();
	protected	MiBounds	originalWorld 			= new MiBounds();
	protected	MiPoint		startDragOffsetFromCenter	= new MiPoint();
	protected	boolean		notificationsEnabled;
	private		boolean		snapObjectCenterToCursor 	= false;
	private		boolean		glueObjectStartLocationToCursor = true;
	private		MiVector	delta 				= new MiVector();
	private		MiBounds	tmpBounds			= new MiBounds();
	private		MiSize		tmpSize				= new MiSize();



	public				MiIDragObjectUnderMouse()
		{
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		}

	public		void		setSnapObjectCenterToCursor(boolean flag)
		{
		snapObjectCenterToCursor = flag;
		}
	public		boolean		getSnapObjectCenterToCursor()
		{
		return(snapObjectCenterToCursor);
		}
	public		void		setGlueObjectStartLocationToCursor(boolean flag)
		{
		glueObjectStartLocationToCursor = flag;
		}
	public		boolean		getGlueObjectStartLocationToCursor()
		{
		return(glueObjectStartLocationToCursor);
		}

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
		if (isCommand(Mi_PICK_UP_COMMAND_NAME))
			{
			return(pickup());
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			return(drag());
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (draggedObj == null)
				return(Mi_PROPOGATE_EVENT);
		
			drop(event);
			draggedObj.setOutgoingInvalidLayoutNotificationsEnabled(notificationsEnabled);
			draggedObj = null;
			return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}
	protected	int		pickup()
		{
		if (draggedObj != null)
			{
			return(Mi_PROPOGATE_EVENT);
			}
		MiParts targetPath = event.getTargetPath();
		int index = targetPath.indexOf(event.editor.getCurrentLayer());
//MiDebug.println("targetPath.indexOf(event.editor.getCurrentLayer()) = " + index);
		if (index > 0)
			{
			MiPart obj = targetPath.elementAt(index - 1);
//MiDebug.println("is obj movable? " + obj);
			if (obj.isMovable())
				{
//MiDebug.println("yes");
				draggedObj = obj;
				notificationsEnabled 
					= draggedObj.getOutgoingInvalidLayoutNotificationsEnabled();
				draggedObj.setOutgoingInvalidLayoutNotificationsEnabled(false);
				event.editor.prependGrabEventHandler(this);
				if (glueObjectStartLocationToCursor)
					{
					startDragOffsetFromCenter.set(
						event.worldPt.x - draggedObj.getCenterX(),
						event.worldPt.y - draggedObj.getCenterY());
					}
				event.editor.getWorldBounds(originalWorld);
				draggedObj.getCenter(originalLocation);
				return(drag());
				}
			}
		draggedObj = null;
		return(Mi_PROPOGATE_EVENT);
		}
	protected	int		drag()
		{
		if (draggedObj == null)
			{
			return(Mi_PROPOGATE_EVENT);
			}

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
		else
			{
			delta.copy(event.worldVector);
			}
//MiDebug.println("move part delta: " + delta);

		event.editor.snapMovingPart(draggedObj, delta);

//MiDebug.println("snapped delta: " + delta);
		boolean modifiedDelta 
			= event.editor.autopanForMovingObj(draggedObj.getDrawBounds(tmpBounds), delta);

//MiDebug.println("panned delta: " + delta);
		if ((!modifiedDelta) && (snapObjectCenterToCursor))
			{
			delta.x = event.worldPt.x - draggedObj.getCenterX();
			delta.y = event.worldPt.y - draggedObj.getCenterY();
			}

//MiDebug.println("NOW delta: " + delta);
		if (!delta.isZero())
			{
			event.editor.snapMovingPart(draggedObj, delta);

			doDrag(draggedObj, delta);
			}

		// 8-18-98 TEST draggedObj.setSize(draggedObj.getPreferredSize(tmpSize));
		draggedObj.validateLayout();

		event.editor.dispatchAction(Mi_INTERACTIVELY_MOVING_PART_ACTION, draggedObj);

		return(Mi_CONSUME_EVENT);
		}
	protected	void		doDrag(MiPart draggedObj, MiVector delta)
		{
		draggedObj.translate(delta);
		}
	protected	void		drop(MiEvent event)
		{
		MiEditor editor = event.editor;

		editor.removeGrabEventHandler(this);

		MiVector amountTranslated = new MiVector(
				draggedObj.getCenterX() - originalLocation.x,
				draggedObj.getCenterY() - originalLocation.y);

		MiNestedTransaction nestedTransaction 
			= new MiNestedTransaction(MiiDisplayNames.Mi_TRANSLATE_DISPLAY_NAME);
		MiSystem.getTransactionManager().startTransaction(nestedTransaction);

		if (!originalWorld.equals(editor.getWorldBounds()))
			{
			nestedTransaction.addElement(
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
			nestedTransaction.addElement(
				new MiTranslatePartsCommand(editor, draggedObj, amountTranslated));
			MiSystem.getViewportTransactionManager().appendTransaction(
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
			}
		else
			{
			nestedTransaction.addElement(
				new MiTranslatePartsCommand(editor, draggedObj, amountTranslated));
			}

		editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_MOVE_PART_ACTION, draggedObj);

		MiSystem.getTransactionManager().commitTransaction(nestedTransaction);
		}
	}

