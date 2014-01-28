
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
public class MiIPan extends MiEventHandler
	{
	private		boolean		dragging 			= false;
	private		boolean		dragViewportNotBackground 	= true;
	private		MiBounds	originalWorld			= new MiBounds();


	public				MiIPan()
		{
		addEventToCommandTranslation(
			Mi_PICK_UP_COMMAND_NAME, Mi_RIGHT_MOUSE_START_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_DRAG_COMMAND_NAME, Mi_RIGHT_MOUSE_DRAG_EVENT, 0,  Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_DROP_COMMAND_NAME, Mi_RIGHT_MOUSE_UP_EVENT, 0,  Mi_ANY_MODIFIERS_HELD_DOWN);
		}

	public		void		setDragViewportNotBackground(boolean flag)
		{
		dragViewportNotBackground = flag;
		}
	public		boolean		getDragViewportNotBackground()
		{
		return(dragViewportNotBackground);
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
		MiEditor editor = event.editor;
		if (isCommand(Mi_PICK_UP_COMMAND_NAME))
			{
			// If re-entering window on X Windows...
			if (dragging)
				return(Mi_PROPOGATE_EVENT);
			dragging = true;
			editor.prependGrabEventHandler(this);
			editor.getWorldBounds(originalWorld);
			return(Mi_CONSUME_EVENT);
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (!dragging)
				return(Mi_PROPOGATE_EVENT);

			MiBounds bounds = editor.getWorldBounds();
			if (dragViewportNotBackground)
				bounds.translate(event.worldVector.x, event.worldVector.y);
			else
				bounds.translate(-event.worldVector.x, -event.worldVector.y);
			editor.setWorldBounds(bounds);
			return(Mi_CONSUME_EVENT);
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (!dragging)
				return(Mi_PROPOGATE_EVENT);

			editor.removeGrabEventHandler(this);
			MiSystem.getViewportTransactionManager().appendTransaction(
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
			dragging = false;
			return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}

