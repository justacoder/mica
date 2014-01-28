
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

/**----------------------------------------------------------------------------------------------
 * This class can execute commands that a standard 'view' menu might
 * generate. The commands apply to the target MiEditor (see 
 * setTargetOfCommand()). The commands that are handled are:
 * <p>
 * <pre>
 *    Mi_CUT_COMMAND_NAME
 *    Mi_COPY_COMMAND_NAME
 *    Mi_PASTE_COMMAND_NAME
 *    Mi_DUPLICATE_COMMAND_NAME
 *    Mi_DELETE_COMMAND_NAME
 *    Mi_ZOOM_IN_COMMAND_NAME
 *    Mi_ZOOM_OUT_COMMAND_NAME
 *    Mi_DISPLAY_PROPERTIES_COMMAND_NAME
 * </pre>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorBackgroundMenuCommands extends MiCommandHandler implements MiiActionHandler, MiiCommandNames, MiiActionTypes
	{
	private		MiiCommandManager manager;
	private		boolean		shapeSelected;
	private		MiEditor	editor;
	private	MiPanAndZoomCommand	panAndZoomCommand	= new MiPanAndZoomCommand();
	private	MiDeletePartsCommand	deleteSelectedObjects	= new MiDeletePartsCommand();
	private		MiAction	itemSelectedAction	= new MiAction(this, 
									Mi_NO_ITEMS_SELECTED_ACTION,
									Mi_ONE_ITEM_SELECTED_ACTION,
									Mi_MANY_ITEMS_SELECTED_ACTION);

					/**------------------------------------------------------
	 				 * Constructs a new MiEditorBackgroundMenuCommands.
					 * @see 		MiEditorBackgroundMenu
					 *------------------------------------------------------*/
	public				MiEditorBackgroundMenuCommands(MiiCommandManager manager)
		{
		this.manager = manager;
		MiSystem.getClipBoard().appendActionHandler(this, Mi_CLIPBOARD_NOW_HAS_DATA_ACTION);
		}
					/**------------------------------------------------------
	 				 * Sets the target of the commands processed by this 
					 * MiiCommandHandler (i.e. what the commands are to act upon).
					 * The target must be an MiEditor.
					 * @param target	the target MiEditor
					 * @see			MiCommandHandler#getTargetOfCommand
					 * @see			#processCommand
					 *------------------------------------------------------*/
	public		void		setTargetOfCommand(Object target)
		{
		if (editor == (MiEditor )target)
			{
			return;
			}

		if (editor != null)
			{
			editor.removeActionHandler(itemSelectedAction);
			}
		editor = (MiEditor )target;
		panAndZoomCommand.setTargetOfCommand(editor);
		super.setTargetOfCommand(editor);
		deleteSelectedObjects.setTargetOfCommand(getTargetOfCommand());
		editor.appendActionHandler(itemSelectedAction);

		handleSelectionState(editor.getNumberOfPartsSelected());
		}
					/**------------------------------------------------------
			 		 * Processes the given command.
					 * @command  		the command to execute
					 *------------------------------------------------------*/
	public		void		processCommand(String cmd)
		{
		MiUtility.pushMouseAppearance(
			editor, MiiTypes.Mi_WAIT_CURSOR, "MiEditorBackgroundMenuCommands");

		if (cmd.equalsIgnoreCase(Mi_ZOOM_IN_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_ZOOM_IN_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_ZOOM_OUT_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_ZOOM_OUT_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_CUT_COMMAND_NAME))
			{
			MiSystem.getClipBoard().cutSelectionToClipBoard(editor);
			}
		else if (cmd.equalsIgnoreCase(Mi_COPY_COMMAND_NAME))
			{
			MiSystem.getClipBoard().copySelectionToClipBoard(editor);
			}
		else if (cmd.equalsIgnoreCase(Mi_PASTE_COMMAND_NAME))
			{
			MiSystem.getClipBoard().pasteFromClipBoard(editor, 
				editor.getMousePosition().getCenter());
			}
		else if (cmd.equalsIgnoreCase(Mi_DUPLICATE_COMMAND_NAME))
			{
			deleteSelectedObjects.processCommand(Mi_DUPLICATE_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_DELETE_COMMAND_NAME))
			{
			deleteSelectedObjects.processCommand(MiDeletePartsCommand.Mi_DELETE_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_DISPLAY_PROPERTIES_COMMAND_NAME))
			{
			MiParts selectedParts = editor.getSelectedParts(new MiParts());
			if (selectedParts.size() > 0)
				selectedParts.elementAt(0).dispatchAction(Mi_DISPLAY_PROPERTIES_ACTION);
			}
		MiUtility.popMouseAppearance(editor, "MiEditorBackgroundMenuCommands");
		}
					/**------------------------------------------------------
	 				 * Processes the given action.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 			false if it is NOT OK to send
					 *			action to the next action handler
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction event)
		{
		if (event.hasActionType(Mi_NO_ITEMS_SELECTED_ACTION))
			{
			handleSelectionState(0);
			}
		else if (event.hasActionType(Mi_ONE_ITEM_SELECTED_ACTION))
			{
			handleSelectionState(1);
			}
		else if (event.hasActionType(Mi_MANY_ITEMS_SELECTED_ACTION))
			{
			handleSelectionState(2);
			}
		else if (event.hasActionType(Mi_CLIPBOARD_NOW_HAS_DATA_ACTION))
			{
			manager.setCommandSensitivity(Mi_PASTE_COMMAND_NAME, true);
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Updates the sensitivity of the widgets that generate
					 * some of the commands based on the given number of items
					 * currently selected in the target editor.
					 * @param num  		the number of items selected
					 *------------------------------------------------------*/
	protected	void		handleSelectionState(int num)
		{
		if (num == 0)
			{
			shapeSelected = false;
			manager.setCommandSensitivity(Mi_CUT_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_COPY_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_DUPLICATE_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, false);
/*** FIX: add this someday
			if (editor.getLocalActionsHandled().hasActionType(Mi_DISPLAY_PROPERTIES_ACTION))
				manager.setCommandSensitivity(Mi_DISPLAY_PROPERTIES_COMMAND_NAME, true);
			else
***/
				manager.setCommandSensitivity(Mi_DISPLAY_PROPERTIES_COMMAND_NAME, false);
			}
		else
			{
			shapeSelected = true;
			manager.setCommandSensitivity(Mi_CUT_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_COPY_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_DUPLICATE_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_DISPLAY_PROPERTIES_COMMAND_NAME, (num == 1));
			}
		if (MiSystem.getClipBoard().hasData())
			manager.setCommandSensitivity(Mi_PASTE_COMMAND_NAME, true);
		else
			manager.setCommandSensitivity(Mi_PASTE_COMMAND_NAME, false);
		}
	}

