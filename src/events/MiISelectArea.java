
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
public class MiISelectArea extends MiEventHandler
	{
	private		MiIRubberbandBounds 	irect;
	private		MiRectangle		rect = new MiRectangle();
	private		boolean			selectOnlyObjectsCompletelyContainedWithinArea;
	private		MiBounds		tmpBounds = new MiBounds();



	public				MiISelectArea()
		{
		addEventToCommandTranslation(
			Mi_DESELECT_AND_PICKUP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);

		rect.setColor(MiColorManager.red);
		}

	public		void		setSelectOnlyObjectsCompletelyContainedWithinArea(boolean flag)
		{
		selectOnlyObjectsCompletelyContainedWithinArea = flag;
		}
	public		boolean		getSelectOnlyObjectsCompletelyContainedWithinArea()
		{
		return(selectOnlyObjectsCompletelyContainedWithinArea);
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
		if ((isCommand(Mi_PICK_UP_COMMAND_NAME))
			|| (isCommand(Mi_DESELECT_AND_PICKUP_COMMAND_NAME)))
			{
			if ((irect != null)
				|| ((event.getTargetList().elementAt(0) != event.editor)
				&& (!event.editor.isLayer(event.getTargetList().elementAt(0)))))
				{
				return(Mi_PROPOGATE_EVENT);
				}

			if (isCommand(Mi_DESELECT_AND_PICKUP_COMMAND_NAME))
				{
				event.editor.deSelectAll();
				}

			event.editor.prependGrabEventHandler(this);

			rect.setPoint(0, event.worldPt);
			rect.setPoint(1, event.worldPt);
			rect.setPickable(false);
			irect = new MiIRubberbandBounds();
			irect.setPart(rect);

			irect.processCommand(event, Mi_PICK_UP_COMMAND_NAME);
			}

		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (irect == null)
				return(Mi_PROPOGATE_EVENT);

			irect.processCommand(event, Mi_DRAG_COMMAND_NAME);
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (irect == null)
				return(Mi_PROPOGATE_EVENT);

			irect.processCommand(event, Mi_DROP_COMMAND_NAME);
			event.editor.removeGrabEventHandler(this);
			irect = null;

			MiPart obj;
			MiiSelectionManager selectManager = event.editor.getSelectionManager();
			MiBounds bounds	= rect.getBounds();
			MiEditorIterator iterator = new MiEditorIterator(event.editor);
			iterator.setIterateIntoPartsOfParts(false);
			iterator.setIterateThroughAllLayers(false);

			MiParts parts = new MiParts();
			while ((obj = iterator.getNext()) != null)
				{
				if (obj.isSelectable())
					{
					if (selectOnlyObjectsCompletelyContainedWithinArea)
						{
						if (obj.getBounds(tmpBounds).isContainedIn(bounds))
							{
							// too slow selectManager.selectAdditionalObject(obj);
							parts.add(obj);
							}
						}
					else
						{
						if (obj.getBounds(tmpBounds).intersects(bounds))
							{
							// too slow selectManager.selectAdditionalObject(obj);
							parts.add(obj);
							}
						}
					}
				}
			selectManager.select(parts);
			}
		return(Mi_CONSUME_EVENT);
		}

	public		void		assignAttributesToRect(MiPart obj)
		{
		rect.setAttributes(obj.getAttributes());
		}
	}

