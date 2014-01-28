
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
 * This class can execute commands that a standard 'graph' menu might
 * generate. The commands apply to the target MiEditor (see 
 * setTargetOfCommand()). The commands that are handled are:
 * <p>
 * <pre>
 *    (Modifies the locations of  the selected items 
 *     in the target editor)
 *    Mi_ARRANGE_TREE_COMMAND_NAME
 *    Mi_ARRANGE_NETWORK_COMMAND_NAME
 *    Mi_ARRANGE_OUTLINE_COMMAND_NAME
 *    Mi_ARRANGE_STAR_COMMAND_NAME
 *    Mi_ARRANGE_RING_COMMAND_NAME
 *    Mi_ARRANGE_CROSSBAR_COMMAND_NAME
 *    Mi_ARRANGE_LINE_COMMAND_NAME
 *    Mi_ARRANGE_OMEGA_COMMAND_NAME
 *    Mi_ARRANGE_2DMESH_COMMAND_NAME
 *    
 *    (Modifies the locations of the selected items 
 *     in the target editor and creates any connections
 *     required by the layout)
 *    Mi_BUILD_TREE_COMMAND_NAME
 *    Mi_BUILD_NETWORK_COMMAND_NAME
 *    Mi_BUILD_OUTLINE_COMMAND_NAME
 *    Mi_BUILD_STAR_COMMAND_NAME
 *    Mi_BUILD_RING_COMMAND_NAME
 *    Mi_BUILD_CROSSBAR_COMMAND_NAME
 *    Mi_BUILD_LINE_COMMAND_NAME
 *    Mi_BUILD_OMEGA_COMMAND_NAME
 *    Mi_BUILD_2DMESH_COMMAND_NAME
 *    
 *    (Assigns a layout to the selected items in the 
 *     target editor and creates any connections
 *     required by the layout)
 *    Mi_FORMAT_TREE_COMMAND_NAME
 *    Mi_FORMAT_NETWORK_COMMAND_NAME
 *    Mi_FORMAT_OUTLINE_COMMAND_NAME
 *    Mi_FORMAT_STAR_COMMAND_NAME
 *    Mi_FORMAT_RING_COMMAND_NAME
 *    Mi_FORMAT_CROSSBAR_COMMAND_NAME
 *    Mi_FORMAT_LINE_COMMAND_NAME
 *    Mi_FORMAT_OMEGA_COMMAND_NAME
 *    Mi_FORMAT_2DMESH_COMMAND_NAME

 *    Mi_REMOVE_FORMAT_COMMAND_NAME
 * </pre>
 * <p>
 * If there are greater than one selected parts in the target editor, then
 * the layout is applied to these (each selected part is a node). If there
 * is only one selected part in the editor, and it's isUngroupable attribute
 * is true, then the parts inside the container are treated as the nodes/elements
 * in the layout.
 *
 * @see     	MiGraphMenu
 * @see     	MiLayoutMenuCommands
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphMenuCommands extends MiCommandHandler implements MiiActionHandler, MiiCommandNames, MiiActionTypes, MiiPartsDefines
	{
	private		MiiCommandManager 	manager;
	private		MiGraphPartsCommand	graphSelectedObjects	= new MiGraphPartsCommand();
	private		MiGroupPartsCommand	groupSelectedObjects	= new MiGroupPartsCommand();


					/**------------------------------------------------------
	 				 * Constructs a new MiGraphMenuCommands.
					 * @see 		MiGraphMenu
					 *------------------------------------------------------*/
	public				MiGraphMenuCommands(MiiCommandManager manager)
		{
		this.manager = manager;
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
		if (((MiEditor )getTargetOfCommand()) != null)
			((MiEditor )getTargetOfCommand()).removeActionHandlers(this);

		MiEditor newEditor = (MiEditor )target;
		super.setTargetOfCommand(newEditor);

		groupSelectedObjects.setTargetOfCommand(newEditor);
		graphSelectedObjects.setTargetOfCommand(newEditor);

		newEditor.appendActionHandler(new MiAction(this, 
				Mi_NO_ITEMS_SELECTED_ACTION,
				Mi_ONE_ITEM_SELECTED_ACTION,
				Mi_MANY_ITEMS_SELECTED_ACTION));

		handleSelectionState(newEditor.getNumberOfPartsSelected());
		}
					/**------------------------------------------------------
			 		 * Processes the given command.
					 * @param command  	the command to execute
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		MiUtility.pushMouseAppearance(
			(MiPart )getTargetOfCommand(), MiiTypes.Mi_WAIT_CURSOR, "MiGraphMenuCommands");

		if (command.equalsIgnoreCase(Mi_REMOVE_FORMAT_COMMAND_NAME))
			{
			groupSelectedObjects.processCommand(Mi_UNGROUP_COMMAND_NAME);
			}
		else
			{
			graphSelectedObjects.processCommand(command);
			}
		MiUtility.popMouseAppearance((MiPart )getTargetOfCommand(), "MiGraphMenuCommands");
		}
					/**------------------------------------------------------
	 				 * Processes the given action.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 	 		false if it is NOT OK to send
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
	 				 * Gets whether the selected parts in the target MiEditor
					 * can be assigned a layout. This will be true if there is
					 * more than one selected part or it the selected part is
					 * is a container.
					 * @return 		true if the selected parts can be
					 *			assigned a layout
					 *------------------------------------------------------*/
	protected	boolean		isTargetPartLayoutable()
		{
		MiEditor editor = (MiEditor )getTargetOfCommand();
		MiParts selectedParts = editor.getSelectedParts(new MiParts());
		return((selectedParts.size() > 1)
			|| ((selectedParts.elementAt(0) instanceof MiContainer)
				&& (!selectedParts.elementAt(0).isUngroupable())));
		}
					/**------------------------------------------------------
	 				 * Updates the sensitivity of the widgets that generate
					 * some of the commands based on the given number of items
					 * currently selected in the target editor.
					 * @param num  		the number of items selected
					 *------------------------------------------------------*/
	protected	void		handleSelectionState(int num)
		{
		boolean enabled;
		if ((num == 0) || (!isTargetPartLayoutable()))
			{
			enabled = false;
			}
		else
			{
			enabled = true;
			}

		manager.setCommandSensitivity(Mi_ARRANGE_TREE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_ARRANGE_NETWORK_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_ARRANGE_OUTLINE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_ARRANGE_STAR_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_ARRANGE_RING_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_ARRANGE_CROSSBAR_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_ARRANGE_LINE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_ARRANGE_OMEGA_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_ARRANGE_2DMESH_COMMAND_NAME, enabled);

		manager.setCommandSensitivity(Mi_BUILD_TREE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_BUILD_NETWORK_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_BUILD_OUTLINE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_BUILD_STAR_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_BUILD_RING_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_BUILD_CROSSBAR_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_BUILD_LINE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_BUILD_OMEGA_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_BUILD_2DMESH_COMMAND_NAME, enabled);

		manager.setCommandSensitivity(Mi_FORMAT_TREE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_FORMAT_NETWORK_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_FORMAT_OUTLINE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_FORMAT_STAR_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_FORMAT_RING_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_FORMAT_CROSSBAR_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_FORMAT_LINE_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_FORMAT_OMEGA_COMMAND_NAME, enabled);
		manager.setCommandSensitivity(Mi_FORMAT_2DMESH_COMMAND_NAME, enabled);

		manager.setCommandSensitivity(Mi_REMOVE_FORMAT_COMMAND_NAME, enabled);
		}
	}

