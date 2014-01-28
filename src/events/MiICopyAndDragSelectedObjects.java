
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
public class MiICopyAndDragSelectedObjects extends MiIDragSelectedObjects
	{
	public				MiICopyAndDragSelectedObjects()
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
		if (draggedObj == null)
			{
			MiEditor editor = event.editor;
			MiPart group = null;
			MiParts selectedParts = editor.getSelectedParts(new MiParts());

			for (int i = 0; i < selectedParts.size(); ++i)
				{
				MiPart part = selectedParts.elementAt(i);
				if (part.isCopyable())
					{
					if (group == null)
						group = new MiContainer();

					MiPart copy = part.deepCopy();
					copy.select(false);
					group.appendPart(copy);
					}
				}
			if (group != null)
				{
				draggedObj = group;
				editor.appendItem(group);
				startDragOffsetFromCenter.set(
					event.worldPt.x - draggedObj.getCenterX(),
					event.worldPt.y - draggedObj.getCenterY());
				editor.getWorldBounds(originalWorld);
				draggedObj.getCenter(originalLocation);
				editor.prependGrabEventHandler(this);
				}
			return(drag());
			}
		return(Mi_PROPOGATE_EVENT);
		}
	protected	void		drop(MiEvent event)
		{
		MiEditor editor = event.editor;

		editor.removeGrabEventHandler(this);

		MiVector amountTranslated = new MiVector(
				draggedObj.getCenterX() - originalLocation.x,
				draggedObj.getCenterY() - originalLocation.y);

		MiParts parts = new MiParts();
		for (int i = 0; i < draggedObj.getNumberOfParts(); ++i)
			{
			parts.addElement(draggedObj.getPart(i));
			}

		for (int i = 0; i < draggedObj.getNumberOfParts(); ++i)
			{
			editor.appendItem(draggedObj.getPart(i));
			}
		draggedObj.removeAllParts();
	
		if (!originalWorld.equals(editor.getWorldBounds()))
			{
			MiSystem.getTransactionManager().appendTransaction(
				MiiDisplayNames.Mi_TRANSLATE_DISPLAY_NAME,
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()),
				new MiDeletePartsCommand(editor, parts, false));
			MiSystem.getViewportTransactionManager().appendTransaction(
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
			}
		else
			{
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(editor, parts, false));
			}
		}

	}

