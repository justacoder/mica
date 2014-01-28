
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
 *    Mi_GROUP_COMMAND_NAME
 *    Mi_UNGROUP_COMMAND_NAME
 *    Mi_GROUP_AND_ICONIFY_COMMAND_NAME
 *    Mi_DEICONIFY_COMMAND_NAME
 *    Mi_BRING_TO_FRONT_COMMAND_NAME
 *    Mi_SEND_TO_BACK_COMMAND_NAME
 *    Mi_BRING_FORWARD_COMMAND_NAME
 *    Mi_SEND_BACKWARD_COMMAND_NAME
 * </pre>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiShapeMenuCommands extends MiCommandHandler implements MiiActionHandler, MiiCommandNames, MiiActionTypes
	{
	private		MiiCommandManager	manager;
	private		boolean			shapeSelected;
	private		MiReorderPartsCommand	reorderSelectedObjects	
							= new MiReorderPartsCommand();
	private		MiGroupPartsCommand	groupSelectedObjects	
							= new MiGroupPartsCommand();
	private		MiIconifyPartsCommand 	iconifyDeiconifySelectedObjects	
							= new MiIconifyPartsCommand();



					/**------------------------------------------------------
	 				 * Constructs a new MiShapeMenuCommands.
					 * @see 		MiShapeMenu
					 *------------------------------------------------------*/
	public				MiShapeMenuCommands(MiiCommandManager manager)
		{
		this.manager = manager;
		}

	public		MiReorderPartsCommand getReorderPartsCommand()
		{
		return(reorderSelectedObjects);
		}
	public		void		setReorderPartsCommand(MiReorderPartsCommand command)
		{
		reorderSelectedObjects = command;
		reorderSelectedObjects.setTargetOfCommand((MiEditor )getTargetOfCommand());
		}
	public		MiGroupPartsCommand getGroupPartsCommand()
		{
		return(groupSelectedObjects);
		}
	public		void		setGroupPartsCommand(MiGroupPartsCommand command)
		{
		groupSelectedObjects = command;
		groupSelectedObjects.setTargetOfCommand((MiEditor )getTargetOfCommand());
		}
	public		MiIconifyPartsCommand getIconifyPartsCommand()
		{
		return(iconifyDeiconifySelectedObjects);
		}
	public		void		setIconifyPartsCommand(MiIconifyPartsCommand command)
		{
		iconifyDeiconifySelectedObjects = command;
		iconifyDeiconifySelectedObjects.setTargetOfCommand((MiEditor )getTargetOfCommand());
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
		reorderSelectedObjects.setTargetOfCommand(newEditor);
		iconifyDeiconifySelectedObjects.setTargetOfCommand(newEditor);

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
			(MiPart )getTargetOfCommand(), MiiTypes.Mi_WAIT_CURSOR, "MiShapeMenuCommands");

		if (cmd.equalsIgnoreCase(Mi_BRING_TO_FRONT_COMMAND_NAME))
			{
			reorderSelectedObjects.processCommand(Mi_BRING_TO_FRONT_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_SEND_TO_BACK_COMMAND_NAME))
			{
			reorderSelectedObjects.processCommand(Mi_SEND_TO_BACK_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_BRING_FORWARD_COMMAND_NAME))
			{
			reorderSelectedObjects.processCommand(Mi_BRING_FORWARD_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_SEND_BACKWARD_COMMAND_NAME))
			{
			reorderSelectedObjects.processCommand(Mi_SEND_BACKWARD_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_GROUP_COMMAND_NAME))
			{
			groupSelectedObjects.processCommand(Mi_GROUP_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_UNGROUP_COMMAND_NAME))
			{
			groupSelectedObjects.processCommand(Mi_UNGROUP_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_GROUP_AND_ICONIFY_COMMAND_NAME))
			{
			iconifyDeiconifySelectedObjects.processCommand(Mi_GROUP_AND_ICONIFY_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_DEICONIFY_COMMAND_NAME))
			{
			iconifyDeiconifySelectedObjects.processCommand(Mi_DEICONIFY_COMMAND_NAME);
			}
		MiUtility.popMouseAppearance((MiPart )getTargetOfCommand(), "MiShapeMenuCommands");
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
			manager.setCommandSensitivity(Mi_BRING_TO_FRONT_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_SEND_TO_BACK_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_BRING_FORWARD_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_SEND_BACKWARD_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_GROUP_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_UNGROUP_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_GROUP_AND_ICONIFY_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_DEICONIFY_COMMAND_NAME, false);
			}
		else
			{
			shapeSelected = true;
			manager.setCommandSensitivity(Mi_BRING_TO_FRONT_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_SEND_TO_BACK_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_BRING_FORWARD_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_SEND_BACKWARD_COMMAND_NAME, true);

			boolean canUnGroup = false;
			boolean canIconify = false;
			boolean canDeIconify = false;
			MiParts selectedParts = ((MiEditor )getTargetOfCommand()).getSelectedParts(new MiParts());

			for (int i = 0; i < selectedParts.size(); ++i)
				{
				if ((!canUnGroup)
				    && (selectedParts.elementAt(i).isUngroupable()))
				    {
				    canUnGroup = true;
				    }
				if ((!canIconify)
				    && (!iconifyDeiconifySelectedObjects.isIconification(selectedParts.elementAt(i))))
				    {
				    canIconify = true;
				    }
				if ((!canDeIconify) 
				    && (iconifyDeiconifySelectedObjects.isIconification(selectedParts.elementAt(i))))
				    {
				    canDeIconify = true;
				    }
				}
			manager.setCommandSensitivity(Mi_GROUP_COMMAND_NAME, 
				(num > 1) && groupSelectedObjects.itemsAreGroupable(selectedParts));
			manager.setCommandSensitivity(Mi_UNGROUP_COMMAND_NAME, canUnGroup && (!canDeIconify));
			manager.setCommandSensitivity(Mi_GROUP_AND_ICONIFY_COMMAND_NAME, canIconify);
			manager.setCommandSensitivity(Mi_DEICONIFY_COMMAND_NAME, canDeIconify);
			}
		}
	}

