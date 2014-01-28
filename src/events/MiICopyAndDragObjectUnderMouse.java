
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
public class MiICopyAndDragObjectUnderMouse extends MiIDragObjectUnderMouse
	{
	public				MiICopyAndDragObjectUnderMouse()
		{
		setEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		setEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, 
			Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		setEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		}

	public		int		pickup()
		{
		if (draggedObj != null)
			{
			return(Mi_PROPOGATE_EVENT);
			}
		MiParts targetPath = event.getTargetPath();
		int index = targetPath.indexOf(event.editor.getCurrentLayer());
		if (index > 0)
			{
			MiPart obj = targetPath.elementAt(index - 1);
			if ((obj.isMovable()) && (obj.isCopyable()))
				{
				draggedObj = obj.deepCopy();
				draggedObj.select(false);
				event.editor.appendItem(draggedObj);
				notificationsEnabled 
					= draggedObj.getOutgoingInvalidLayoutNotificationsEnabled();
				draggedObj.setOutgoingInvalidLayoutNotificationsEnabled(false);
				event.editor.prependGrabEventHandler(this);
				if (getGlueObjectStartLocationToCursor())
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
	}

