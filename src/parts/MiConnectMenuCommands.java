
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
 * This class can execute commands that a standard 'connect' menu might
 * generate. The commands apply to the target MiEditor (see 
 * setTargetOfCommand()). The commands that are handled are:
 * <p>
 * <pre>
 *    Mi_CONNECT_COMMAND_NAME
 *    Mi_DISCONNECT_COMMAND_NAME
 *    Mi_COLLAPSE_COMMAND_NAME
 *    Mi_EXPAND_COMMAND_NAME
 * </pre>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiConnectMenuCommands extends MiCommandHandler implements MiiActionHandler, MiiCommandNames, MiiActionTypes
	{
	private		MiiCommandManager manager;
	private	boolean		shapeSelected;
	private	MiDeletePartsCommand	
				deleteSelectedObjects	= new MiDeletePartsCommand();
	private	MiCollapseExpandSelectedObjects
				collapseExpandSelectedObjects	= new MiCollapseExpandSelectedObjects();


					/**------------------------------------------------------
	 				 * Constructs a new MiConnectMenuCommands.
					 * @param application	the command handler
					 * @see 		MiConnectMenu
					 *------------------------------------------------------*/
	public				MiConnectMenuCommands(MiiCommandManager manager)
		{
		this.manager = manager;
		collapseExpandSelectedObjects.setTypeOfRelationWithSelectedObjects(
			MiCollapseExpandSelectedObjects.USE_ALL_CONNECTED_TO_RELATIONS);
		collapseExpandSelectedObjects.setDistanceToSelectedObjects(
			MiCollapseExpandSelectedObjects.DO_ALL_CHILDREN);
		}
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
		deleteSelectedObjects.setTargetOfCommand(getTargetOfCommand());
		collapseExpandSelectedObjects.setTargetOfCommand(getTargetOfCommand());

		newEditor.appendActionHandler(new MiAction(this, 
				Mi_NO_ITEMS_SELECTED_ACTION, 
				Mi_ONE_ITEM_SELECTED_ACTION, 
				Mi_MANY_ITEMS_SELECTED_ACTION));


		handleSelectionState(newEditor.getNumberOfPartsSelected());
		}
					/**------------------------------------------------------
			 		 * Processes the given command.
					 * @command  		the command to execute
					 *------------------------------------------------------*/
	public		void		processCommand(String cmd)
		{
		MiUtility.pushMouseAppearance(
			(MiPart )getTargetOfCommand(), MiiTypes.Mi_WAIT_CURSOR, "MiConnectMenuCommands");

		if (cmd.equalsIgnoreCase(Mi_CONNECT_COMMAND_NAME))
			{
			deleteSelectedObjects.processCommand(Mi_CONNECT_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_DISCONNECT_COMMAND_NAME))
			{
			deleteSelectedObjects.processCommand(Mi_DISCONNECT_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_COLLAPSE_COMMAND_NAME))
			{
			collapseExpandSelectedObjects.processCommand(Mi_COLLAPSE_COMMAND_NAME);
			handleSelectionState(1);
			}
		else if (cmd.equalsIgnoreCase(Mi_EXPAND_COMMAND_NAME))
			{
			collapseExpandSelectedObjects.processCommand(Mi_EXPAND_COMMAND_NAME);
			handleSelectionState(1);
			}
		MiUtility.popMouseAppearance((MiPart )getTargetOfCommand(), "MiConnectMenuCommands");
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
			manager.setCommandSensitivity(Mi_CONNECT_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_DISCONNECT_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_COLLAPSE_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_EXPAND_COMMAND_NAME, false);
			}
		else
			{
			shapeSelected = true;
			manager.setCommandSensitivity(Mi_CONNECT_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_DISCONNECT_COMMAND_NAME, true);
			if (num > 1)
				manager.setCommandSensitivity(Mi_CONNECT_COMMAND_NAME, true);

			boolean canCollapse = false;
			boolean canExpand = false;
			MiParts selectedParts 
				= ((MiEditor )getTargetOfCommand()).getSelectedParts(new MiParts());
			for (int i = 0; i < selectedParts.size(); ++i)
				{
				if ((!canCollapse)
				    && (!MiCollapseExpandSelectedObjects.isCollapsed(
								selectedParts.elementAt(i))))
				    {
				    canCollapse = true;
				    }
				if ((!canExpand) 
				    && (MiCollapseExpandSelectedObjects.isCollapsed(
								selectedParts.elementAt(i))))
				    {
				    canExpand = true;
				    }
				}
			manager.setCommandSensitivity(Mi_COLLAPSE_COMMAND_NAME, canCollapse);
			manager.setCommandSensitivity(Mi_EXPAND_COMMAND_NAME, canExpand);
			}
		}
	}

