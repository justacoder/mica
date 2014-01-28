
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

package examples;

import com.swfm.mica.*; 

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a simple window with a drag-and-droppable list.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiExample5 implements MiiActionTypes, MiiCommandNames, MiiActionHandler, MiiEventTypes
	{
	private		MiList		list;
					/**------------------------------------------------------
	 				 * The entry point for all applications.
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);
		new MiExample5();
		}
					/**------------------------------------------------------
	 				 * Constructs a new instance MiExample5. 
					 *------------------------------------------------------*/
	public				MiExample5()
		{
		// ---------------------------------------------------------------
		// Create a window with "Hello World" in the border title.
		// ---------------------------------------------------------------
		MiNativeWindow window = new MiNativeWindow("Drag-and-droppable List");

		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		window.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// When the window is closed/disposed, exit the whole application
		// ---------------------------------------------------------------
		window.setDefaultCloseCommand(Mi_QUIT_COMMAND_NAME);

		// ---------------------------------------------------------------
		// Create a list widget that displays a number of lines of text
		// ---------------------------------------------------------------
		list = new MiList();
		MiScrolledBox scrolledBox = new MiScrolledBox(list);

		// ---------------------------------------------------------------
		// Turn off automatic sorting so that we can test interactive placement
		// ---------------------------------------------------------------
		list.getSortManager().setEnabled(false);

		// ---------------------------------------------------------------
		// Specify the events that will start and stop drag and drop copy 
		// and drag and drop cut
		// ---------------------------------------------------------------
		MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(Mi_LEFT_MOUSE_UP_EVENT, 0, 0));

		dndBehavior.setDragAndCutPickUpEvent(
			new MiEvent(Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN));
		dndBehavior.setDragAndCutDragEvent(
			new MiEvent(Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN));
		dndBehavior.setDragAndCutCancelEvent(
			new MiEvent(Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN));
		dndBehavior.setDragAndCutDropEvent(
			new MiEvent(Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN));

		list.setIsDragAndDropTarget(true);
		list.setDragAndDropBehavior(dndBehavior);
		list.appendActionHandler(this, 
			Mi_ACTIONS_OF_PARTS_OF_OBSERVED | Mi_ACTIONS_OF_OBSERVED 
				| Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);

		for (int i = 0; i < 10; ++i)
			{
			MiText text = new MiText("Sample text #" + i);
			text.setIsDragAndDropSource(true);
			list.appendItem(text);
			}

		// ---------------------------------------------------------------
		// Add the scrolled list to the window.
		// ---------------------------------------------------------------
		window.appendPart(scrolledBox);

		// ---------------------------------------------------------------
		// Make the window visible.
		// ---------------------------------------------------------------
		window.setVisible(true);
		}

	public		boolean		processAction(MiiAction action)
		{
		if ((action.hasActionType(Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE))
			|| (action.hasActionType(
			Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
			{
			// ---------------------------------------------------------------
			// Handle drops of things into the palette
			// ---------------------------------------------------------------
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();

			// ---------------------------------------------------------------
			// Just to the default action and copy and add the copy to the (sorted) list
			// ---------------------------------------------------------------
			list.doImport(transferOp);
	
			if (transferOp.isDragAndCut())
				list.removeItem(transferOp.getSource());

			// ---------------------------------------------------------------
			// Don't let the default d&d handler do anything.
			// ---------------------------------------------------------------
			return(false);
			}
		return(true);
		}
	}
