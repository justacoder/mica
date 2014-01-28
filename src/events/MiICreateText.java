
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
public class MiICreateText extends MiEventHandler implements MiiActionHandler, MiiActionTypes, MiiTypes
	{
	private		MiText		currentlyEditedText;
	private		MiPart		prototypeShape 			= new MiText();
	public static final String	AddTextEventName		= "addText";
	public static final String	AddTextToSelectedShapeEventName	= "addTextToSelectedShape";
	public static final String	AddTextToSelectedShapeAttachmentTag	= "MiICreateText.attachment";




	public				MiICreateText()
		{
		//addEventToCommandTranslation(AddTextEventName, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		((MiText )prototypeShape).setSelectEntireTextAsPartInEditor(true);
		((MiText )prototypeShape).setMustDoubleClickToEdit(true);
		}
	public		void		setPrototypeShape(MiPart obj)
		{
		prototypeShape = obj;
		}
	public		MiPart		getPrototypeShape()
		{
		return(prototypeShape);
		}
	public		MiiEventHandler	copy()
		{
		MiICreateText c = (MiICreateText )super.copy();
		c.prototypeShape = prototypeShape;
		return(c);
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
		if (isCommand(AddTextEventName))
			{
			MiText text = (MiText )prototypeShape.copy();
			text.setCenter(event.getWorldPoint(new MiPoint()));
			event.editor.getCurrentLayer().appendPart(text);
			text.setIsEditable(true);
			currentlyEditedText = text;
			text.requestKeyboardFocus();
			text.requestEnterKeyFocus();
			text.setNumDisplayedRows(10);
			}
		return(Mi_CONSUME_EVENT);
		}

	public		int		processEvent(MiEvent event)
		{
//MiDebug.println(this + "processEvent: " + event);
		if ((event.type == MiEvent.Mi_LEFT_MOUSE_DBLCLICK_EVENT) || (event.type == MiEvent.Mi_KEY_EVENT))
			{
//MiDebug.println(this + "currentlyEditedText: " + currentlyEditedText);
			// If already adding text to selected shape, then nothing to do here...
			if ((currentlyEditedText != null) 
				|| ((event.type == MiEvent.Mi_KEY_EVENT) && (event.key < ' ') || (event.key > 'z')))
				{
//MiDebug.println(this + "currentlyEditedText: " + currentlyEditedText);
				return(Mi_PROPOGATE_EVENT);
				}

			//currentlyEditedText = null;

			if (event.type == MiEvent.Mi_KEY_EVENT)
				{
				int status = handleAnnotationPointEvent(event);
				if (status == Mi_CONSUME_EVENT)
					return(status);
				}

//MiDebug.println(this + "processEvent2 : " + event);
			MiParts selectedParts = event.editor.getAFewSelectedParts(new MiParts());
			MiParts targetList = event.getTargetList();
			MiText text = null;
//MiDebug.println(this + "targetList : " + targetList);
			// Target lis tmay not have selected shape under mouse because it finds its
			// attachment boundsmaniplutor attachment first and so is ignored...
			if ((selectedParts.size() == 1) && (!targetList.contains(selectedParts.get(0))))
				{
				targetList = new MiParts(targetList);
				targetList.insertElementAt(selectedParts.get(0), 0);
				}
			for (int i = 0; i < targetList.size(); ++i)
				{
				MiPart part = targetList.elementAt(i);
				if (part == event.editor.getCurrentLayer())
					{
					break;
					}
//MiDebug.println(this + "selectedParts : " + selectedParts);
//MiDebug.println(this + "selectedParts.size() : " + selectedParts.size());
				if ((!part.isSelected()) || (part.isSelected() && (selectedParts.size() == 1)))
					{
//MiDebug.println(this + "processEvent4 : " + part);
//MiDebug.println(this + "processEvent4  part.isSelected(): " + part.isSelected());
					if ((part instanceof MiText) && (((MiText )part).isEditable()))
						{
						text = (MiText )part;
						}
//MiDebug.println(this + "processEvent4a : " + text);
					if (text == null)
						{
						for (int j = 0; j < part.getNumberOfParts(); ++j)
							{
//MiDebug.println(this + "processEvent4a ?: " + part.getPart(j));

							if ((part.getPart(j) instanceof MiText) 
								&& (((MiText )part.getPart(j)).isEditable()))
								{
								text = (MiText )part.getPart(j);
								break;
								}
							}
						}
//MiDebug.println(this + "processEvent4b : " + text);
					if (text == null)
						{
						for (int j = 0; j < part.getNumberOfAttachments(); ++j)
							{
//MiDebug.println(this + "processEvent4b : part.getAttachment(j) = " + part.getAttachment(j));
							if ((part.getAttachment(j) instanceof MiText) 
								&& (((MiText )part.getAttachment(j)).isEditable()))
								{
								text = (MiText )part.getAttachment(j);
								//??text.setAttributes(prototypeShape.getAttributes());
								break;
								}
							}
						}
//MiDebug.println(this + "processEvent4c : " + text);
					if (text == null)
						{
//MiDebug.println(this + "processEvent4c : look for anno pt in part: " +part);
						// Look for AnnotationPoints...in the part and it's sub-parts
						if (findAnnotationPointToEditInSelectedShape(part, event) == Mi_CONSUME_EVENT)
							{
//MiDebug.println(this + "processEvent4c : foudn anno pt in part: " +part);
							return(Mi_CONSUME_EVENT);
							}
						}
//MiDebug.println(this + "processEvent4d : " + text);
					if ((text == null) && (part.isSelected()))
						{
						text = (MiText )prototypeShape.copy();
//MiDebug.println(this + "processEvent4d : useproto of selected part: " +part);
						text.setCenter(event.getWorldPoint(new MiPoint()));
						part.appendAttachment(
							text, MiiTypes.Mi_CENTER_LOCATION, AddTextToSelectedShapeAttachmentTag, null);
						event.editor.select(text);
						}
					}
				if (text != null)
					{
					break;
					}
				}
				
//MiDebug.println(this + "processEvent4e : " + text);
			if (text == null)
				{
				text = (MiText )prototypeShape.copy();
				text.setCenter(event.getWorldPoint(new MiPoint()));
//MiDebug.println(this + "processEvent4e proto of no selected part setCenter() : " + text.getCenter());
				//text.setNumDisplayedRows(10);
				event.editor.appendItem(text);
				event.editor.select(text);
				}
//MiDebug.println(this + "processEvent5 : " + text);
			text.setIsEditable(true);
			currentlyEditedText = text;
//MiDebug.println(this + "currentlyEditedText: " + currentlyEditedText);
			text.requestKeyboardFocus();
			text.requestEnterKeyFocus();
//MiDebug.println(this + "text.getInteractiveEditor(): " + text.getInteractiveEditor());
			text.getInteractiveEditor().setCursorPosition(event.getWorldPoint(new MiPoint()));

			event.editor.select(text);

			boolean mustDblClick = text.getInteractiveEditor().getMustDoubleClickToEdit();
			text.getInteractiveEditor().setMustDoubleClickToEdit(false);
			text.getInteractiveEditor().setKeyboardFocus(true);
			text.getInteractiveEditor().setMustDoubleClickToEdit(mustDblClick);

			if (event.type == MiEvent.Mi_KEY_EVENT)
				{
				text.setText(((char )event.key) + "");
				text.getInteractiveEditor().setCursorPosition(1);
				}
			text.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
//MiDebug.println(this + "222 processEvent4d text.getFenter() : " + text.getCenter());
			return(Mi_CONSUME_EVENT);
			}
		return(super.processEvent(event));
		}

	protected	int		findAnnotationPointToEditInSelectedShape(MiPart part, MiEvent event)
		{
//MiDebug.println(this + "findAnnotationPointToEditInSelectedShape " );
		MiAnnotationPointManager manager = part.getAnnotationPointManager();
		if (manager != null)
			{
if (manager.getManagedPoints() == null)
{
System.out.println("manager.getManagedPoints() == null");
System.out.println("manager = " + manager);
System.out.println("part = " + part);
}
			MiManagedPoint managedPoint 
				= manager.getManagedPoints().getManagedPoint(MiiTypes.Mi_CENTER_LOCATION);
			if ((managedPoint == null) && (manager.getManagedPoints().size() > 0))
				{
				managedPoint = manager.getManagedPoints().elementAt(0);
				}
			if (managedPoint != null)
				{
				if (handleStartEditTextAtManagedPoint(part, managedPoint, event)
					 == Mi_CONSUME_EVENT)
					{
					return(Mi_CONSUME_EVENT);
					}
				}
			}
		for (int i = 0; i < part.getNumberOfParts(); ++i)
			{
			if (findAnnotationPointToEditInSelectedShape(part.getPart(i), event)
				 == Mi_CONSUME_EVENT)
				return(Mi_CONSUME_EVENT);
			}

		return(Mi_PROPOGATE_EVENT);
		}
	protected	int		handleAnnotationPointEvent(MiEvent event)
		{
//MiDebug.println(this + "handleAnnotationPointEvent " );
		MiParts targetList = event.getTargetList();
		for (int i = 0; i < targetList.size(); ++i)
			{
			MiPart annoGr = targetList.elementAt(i);
//MiDebug.println(this + "handleAnnotationPointEvent annoGr=" + annoGr );
			MiManagedPoint managedPoint = (MiManagedPoint )
				annoGr.getResource(MiManagedPointManager.Mi_MANAGED_POINT_RESOURCE_NAME);
			MiManagedPointManager manager = (MiManagedPointManager )
				annoGr.getResource(MiManagedPointManager.Mi_MANAGED_POINT_MANAGER_RESOURCE_NAME);

			if ((managedPoint != null) && (manager instanceof MiAnnotationPointManager))
				{
				MiPart part = annoGr.getContainer(0);
				if (handleStartEditTextAtManagedPoint(part, managedPoint, event) == Mi_CONSUME_EVENT)
					{
/*
					if ((!part.isSelected()) && (part.isSelectable()))
						{
						event.editor.selectObj(part);
						}
*/
					return(Mi_CONSUME_EVENT);
					}
				}
			if (annoGr.getAnnotationPointManager() != null)
				{
				manager = annoGr.getAnnotationPointManager();
				if (handleStartEditTextAtManagedPoint(annoGr, null, event) == Mi_CONSUME_EVENT)
					{
/*
					if ((!annoGr.isSelected()) && (annoGr.isSelectable()))
						{
						event.editor.selectObj(part);
						}
*/
					return(Mi_CONSUME_EVENT);
					}
				}
			}
		return(Mi_PROPOGATE_EVENT);
		//return(Mi_IGNORE_THIS_PART);
		}
	protected	int		handleStartEditTextAtManagedPoint(
						MiPart part, MiManagedPoint managedPoint, MiEvent event)
		{
//MiDebug.println(this + "handleStartEditTextAtManagedPoint " );
		MiText text = null;
/****????
		if ((location >= Mi_MIN_CUSTOM_LOCATION)
			&& (location <= Mi_MAX_CUSTOM_LOCATION))
			{
			}
		else
*****/
			{
			MiAnnotationPointManager manager = part.getAnnotationPointManager();
			if (managedPoint == null)
				{
				MiPoint eventPt = event.getWorldPoint(new MiPoint());
				MiManagedPoint closestAnnoPt = null;
				double closestDistance = Double.MAX_VALUE;
				MiPoint pt = new MiPoint();
				MiManagedPoints managedPoints = manager.getManagedPoints();
				for (int i = 0; i < managedPoints.size(); ++i)
					{
					managedPoint = managedPoints.elementAt(i);
					for (int j = 0; j < managedPoint.getNumberOfPoints(part); ++j)
						{
						managedPoint.getPoint(part, j, pt);
						if (pt.getDistanceSquared(eventPt) < closestDistance)
							{
							closestDistance = pt.getDistanceSquared(eventPt);
							closestAnnoPt = managedPoint;
							}
						}
					}
				managedPoint = closestAnnoPt;
				}
			int location = managedPoint.getPointNumber();
//MiDebug.println("Foudn anno at location: " + location);
			text = (MiText )part.getAttachment("" + location);
			if (text == null)
				{
				MiAnnotationPointRule rule = (MiAnnotationPointRule )managedPoint.getRule();
				if (rule == null)
					{
					rule = (MiAnnotationPointRule )manager.getRule();
					}

				text = (MiText )prototypeShape.copy();
				text.setCenter(managedPoint.getLocationOfPoint(part, location, new MiPoint()));
//MiDebug.println("Foudn anno at point: " + managedPoint.getLocationOfPoint(part, location, new MiPoint()));

				//text.setAttributes(rule.getContentAttributes());
				text.setAttributes(prototypeShape.getAttributes().overrideFrom(rule.getContentAttributes()));

				if ((event.type != MiEvent.Mi_KEY_EVENT) && (rule.getPrompt() != null))
					{
					text.setText(rule.getPrompt());
					}

/*
				// make annotation invisible as long as there is 'real' text
				// If really want to do this, and make anno graphics
				// invisible if there is an anno, then:
				// Need to create a whole new manager here cause this set
				// Mi_CENTER_LOCATION invisible which was ALL center locations
				// cause all shapes were using this manager
				int index = manager.getManagedPoints().indexOf(managedPoint);
				managedPoint = managedPoint.copy();
				MiPart look = manager.getLook();
				if (look == null)
					look = managedPoint.getLook();

				managedPoint.setLook(look.deepCopy());
				manager.getManagedPoints().setElementAt(managedPoint, index);
				managedPoint.getLook().setVisible(false);
****/

				part.invalidateArea();
/***
				if ((location >= Mi_MIN_CUSTOM_LOCATION) && (location <= Mi_MAX_CUSTOM_LOCATION))
					{
					part.appendAttachment(text);
					part.getAttachments().setAttachmentTag(text, "" + location);
					}
				else
***/
					{
					part.appendAttachment(text, location, "" + location, null);
					}
				}
			text.setIsEditable(true);
			currentlyEditedText = text;
			text.requestKeyboardFocus();
			text.requestEnterKeyFocus();
			text.setNumDisplayedRows(10);
			if (event.type == MiEvent.Mi_KEY_EVENT)
				{
				text.setText(((char )event.key) + "");
				text.getInteractiveEditor().setCursorPosition(1);
				}
			text.appendActionHandler(new MiAction(
				this, Mi_LOST_KEYBOARD_FOCUS_ACTION, managedPoint));
			return(Mi_CONSUME_EVENT);
			}
//???		return(Mi_PROPOGATE_EVENT);
		}
	public		boolean		processAction(MiiAction action)
		{
		// Remove text areas that have no text in them...and restore annotation...
		MiText editedText = (MiText )action.getActionSource();
//MiDebug.println("editedText now completed: " + editedText);
		if (editedText.getText().length() == 0)
			{
			editedText.getContainer(0).getContainer(0).invalidateArea();
			editedText.removeSelf();
			// If we do this above...:
			// make annotation invisible as long as there is 'real' text
			// If really want to do this, and make anno graphics
			if ((action.getActionUserInfo() instanceof MiManagedPoint)
				&& (((MiManagedPoint )action.getActionUserInfo()).getLook() != null))
				{
				((MiManagedPoint )action.getActionUserInfo()).getLook().setVisible(true);
				}
			}
		else
			{
			editedText.getContainingEditor().dispatchAction(
				Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION, editedText);
			editedText.getContainingEditor().deSelect(editedText);
			}
		editedText.removeActionHandlers(this);
		currentlyEditedText = null;
		return(true);
		}

	}
