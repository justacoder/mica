
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
 * The commands that this class can execute on a target MiEditor are:
 * <pre>
 *    Mi_UNDO_COMMAND_NAME
 *    Mi_REDO_COMMAND_NAME
 *    Mi_CUT_COMMAND_NAME
 *    Mi_COPY_COMMAND_NAME
 *    Mi_PASTE_COMMAND_NAME
 *    Mi_DELETE_COMMAND_NAME
 *    Mi_SELECT_ALL_COMMAND_NAME
 *    Mi_DESELECT_ALL_COMMAND_NAME
 *    Mi_DUPLICATE_COMMAND_NAME
 * </pre>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditMenuCommands extends MiCommandHandler implements MiiActionHandler, MiiCommandNames, MiiActionTypes
	{
	private		boolean			updateUndoWidgetLabels;
	private		boolean			shapeSelected;
	private		MiiCommandManager 	manager;
	//private		MiDeletePartsCommand	deleteSelectedObjects	= new MiDeletePartsCommand();



					/**------------------------------------------------------
	 				 * Constructs a new MiEditMenuCommands which updates
					 * the undo and redo labels to indicate the action that
					 * the will undo/redo.
					 *------------------------------------------------------*/
	public				MiEditMenuCommands(MiiCommandManager manager)
		{
		this(true, manager);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEditMenuCommands which updates
					 * the undo and redo labels to indicate the action that
					 * will be undone/redone if the given 'updateUndoWidgetLabels'
					 * flag is set to true. This feature will not work but
					 * also will do no harm if this is used as a stand-alone
					 * command handler.
					 * @param updateUndoWidgetLabels true if updating the undo
					 * 				 and redo labels
					 *------------------------------------------------------*/
	public				MiEditMenuCommands(boolean updateUndoWidgetLabels, MiiCommandManager manager)
		{
		this.updateUndoWidgetLabels = updateUndoWidgetLabels;
		this.manager = manager;

		MiSystem.getClipBoard().appendActionHandler(
			this, Mi_CLIPBOARD_NOW_HAS_DATA_ACTION);

		MiSystem.getTransactionManager().appendActionHandler(
			this, Mi_TRANSACTION_MANAGER_CHANGED_ACTION);
		}
/*
	public		MiDeletePartsCommand getDeletePartsCommand()
		{
		return(deleteSelectedObjects);
		}
	public		void		setDeletePartsCommand(MiDeletePartsCommand command)
		{
		deleteSelectedObjects = command;
		deleteSelectedObjects.setTargetOfCommand((MiEditor )getTargetOfCommand());
		}
*/
					/**------------------------------------------------------
	 				 * Sets the target of the commands processed by this 
					 * MiiCommandHandler (i.e. what the commands are to act upon).
					 * The target must be an MiEditor.
					 * @param target 	the target MiEditor
					 * @see			MiCommandHandler#getTargetOfCommand
					 * @see			#processCommand
					 *------------------------------------------------------*/
	public		void		setTargetOfCommand(Object target)
		{
		if (((MiEditor )getTargetOfCommand()) != null)
			((MiEditor )getTargetOfCommand()).removeActionHandlers(this);

		MiEditor newEditor = (MiEditor )target;
		super.setTargetOfCommand(newEditor);
		//deleteSelectedObjects.setTargetOfCommand(getTargetOfCommand());

		newEditor.appendActionHandler(new MiAction(this, 
				Mi_NO_ITEMS_SELECTED_ACTION,
				Mi_ONE_ITEM_SELECTED_ACTION,
				Mi_MANY_ITEMS_SELECTED_ACTION));

		handleSelectionState(newEditor.getNumberOfPartsSelected());
		updateUndoRedoRepeatWidgetStates();
		}
					/**------------------------------------------------------
			 		 * Processes the given command.
					 * @command  		the command to execute
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		MiUtility.pushMouseAppearance(
			(MiPart )getTargetOfCommand(), MiiTypes.Mi_WAIT_CURSOR, "MiEditMenuCommands");

		if (command.equalsIgnoreCase(Mi_UNDO_COMMAND_NAME))
			{
			MiSystem.getTransactionManager().undoTransaction();
			}
		else if (command.equalsIgnoreCase(Mi_REDO_COMMAND_NAME))
			{
			if (MiSystem.getTransactionManager().hasTransactionsToRedo())
				MiSystem.getTransactionManager().redoTransaction();
			else
				MiSystem.getTransactionManager().repeatTransaction();
			}
		else if (command.equalsIgnoreCase(Mi_CUT_COMMAND_NAME))
			{
			MiSystem.getClipBoard().cutSelectionToClipBoard((MiEditor )getTargetOfCommand());
			}
		else if (command.equalsIgnoreCase(Mi_COPY_COMMAND_NAME))
			{
			MiSystem.getClipBoard().copySelectionToClipBoard((MiEditor )getTargetOfCommand());
			}
		else if (command.equalsIgnoreCase(Mi_PASTE_COMMAND_NAME))
			{
			MiSystem.getClipBoard().pasteFromClipBoard((MiEditor )getTargetOfCommand());
			}
		else if (command.equalsIgnoreCase(Mi_SELECT_ALL_COMMAND_NAME))
			{
			((MiEditor )getTargetOfCommand()).getSelectionManager().selectAll();
			}
		else if (command.equalsIgnoreCase(Mi_DESELECT_ALL_COMMAND_NAME))
			{
			((MiEditor )getTargetOfCommand()).getSelectionManager().deSelectAll();
			}
		else if (command.equalsIgnoreCase(Mi_DUPLICATE_COMMAND_NAME))
			{
			MiDeletePartsCommand deleteSelectedObjects = MiSystem.getCommandBuilder().getDeletePartsCommand();
			deleteSelectedObjects.setTargetOfCommand((MiEditor )getTargetOfCommand());
			deleteSelectedObjects.processCommand(Mi_DUPLICATE_COMMAND_NAME);
			}
		else if (command.equalsIgnoreCase(Mi_DELETE_COMMAND_NAME))
			{
			MiDeletePartsCommand deleteSelectedObjects = MiSystem.getCommandBuilder().getDeletePartsCommand();
			deleteSelectedObjects.setTargetOfCommand((MiEditor )getTargetOfCommand());
			deleteSelectedObjects.processCommand(Mi_DELETE_COMMAND_NAME);
			}
		MiUtility.popMouseAppearance((MiPart )getTargetOfCommand(), "MiEditMenuCommands");
		}
					/**------------------------------------------------------
	 				 * Processes the given action.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 			false if it is NOT OK to send
					 *			action to the next action handler
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_NO_ITEMS_SELECTED_ACTION))
			{
			handleSelectionState(0);
			}
		else if (action.hasActionType(Mi_ONE_ITEM_SELECTED_ACTION))
			{
			handleSelectionState(1);
			}
		else if (action.hasActionType(Mi_MANY_ITEMS_SELECTED_ACTION))
			{
			handleSelectionState(2);
			}
		else if (action.hasActionType(Mi_CLIPBOARD_NOW_HAS_DATA_ACTION))
			{
			manager.setCommandSensitivity(Mi_PASTE_COMMAND_NAME, true);
			}
		else if (action.hasActionType(Mi_TRANSACTION_MANAGER_CHANGED_ACTION))
			{
			updateUndoRedoRepeatWidgetStates();
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Updates the sensitivity and, if enabled, the label
					 * of the undo and redo command widgets.
					 *------------------------------------------------------*/
	protected	void		updateUndoRedoRepeatWidgetStates()
		{
		// -----------------------------------
		// If any UNDO transactions...
		// -----------------------------------
		if (MiSystem.getTransactionManager().hasTransactionsToUndo())
			{
			manager.setCommandSensitivity(Mi_UNDO_COMMAND_NAME, true);
			if (updateUndoWidgetLabels)
				{
				String name = MiSystem.getTransactionManager()
					.getNextTransactionToUndo().getName();
				name = MiSystem.getProperty(name, name);
				manager.setCommandLabel(Mi_UNDO_COMMAND_NAME, "Undo " + name);
				}
			}
		else
			{
			manager.setCommandSensitivity(Mi_UNDO_COMMAND_NAME, false);
			if (updateUndoWidgetLabels)
				{
				manager.setCommandLabel(Mi_UNDO_COMMAND_NAME, "Undo");
				}
			}
		// -----------------------------------
		// If any REDO transactions...
		// -----------------------------------
		if (MiSystem.getTransactionManager().hasTransactionsToRedo())
			{
			manager.setCommandSensitivity(Mi_REDO_COMMAND_NAME, true);
			if (updateUndoWidgetLabels)
				{
				String name = MiSystem.getTransactionManager()
					.getNextTransactionToRedo().getName();
				name = MiSystem.getProperty(name, name);
				manager.setCommandLabel(Mi_REDO_COMMAND_NAME, "Redo " + name);
				}
			}
		// -----------------------------------
		// else if any REPEAT transactions...
		// -----------------------------------
		else if (MiSystem.getTransactionManager().getNextTransactionToRepeat() != null)
			{
			manager.setCommandSensitivity(Mi_REDO_COMMAND_NAME, true);
			if (updateUndoWidgetLabels)
				{
				String name = MiSystem.getTransactionManager()
					.getNextTransactionToRepeat().getName();
				name = MiSystem.getProperty(name, name);
				manager.setCommandLabel(Mi_REDO_COMMAND_NAME, "Repeat " + name);
				}
			}
		else
			{
			manager.setCommandSensitivity(Mi_REDO_COMMAND_NAME, false);
			if (updateUndoWidgetLabels)
				{
				manager.setCommandLabel(Mi_REDO_COMMAND_NAME, "Redo");
				}
			}
		}
					/**------------------------------------------------------
	 				 * Updates the sensitivity of the command widgets based on
					 * the number of items selected in the target MiEditor.
					 * @param numSelected	the number of items selected
					 *------------------------------------------------------*/
	protected	void		handleSelectionState(int numSelected)
		{
		if (numSelected == 0)
			{
			shapeSelected = false;
			manager.setCommandSensitivity(Mi_CUT_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_COPY_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_DUPLICATE_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_DESELECT_ALL_COMMAND_NAME, false);
			}
		else
			{
			shapeSelected = true;
			manager.setCommandSensitivity(Mi_CUT_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_COPY_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_DUPLICATE_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_DESELECT_ALL_COMMAND_NAME, true);
			}
		if (MiSystem.getClipBoard().hasData())
			manager.setCommandSensitivity(Mi_PASTE_COMMAND_NAME, true);
		else
			manager.setCommandSensitivity(Mi_PASTE_COMMAND_NAME, false);
		}
	}

