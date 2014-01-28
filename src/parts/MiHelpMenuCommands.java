
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
 * This class can execute commands that a standard 'help' menu might
 * generate. The commands apply to the target MiEditor (see 
 * setTargetOfCommand()). The commands that are handled are:
 * <p>
 * <pre>
 *    Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME
 *    Mi_HELP_ON_APPLICATION_COMMAND_NAME
 *    Mi_ENABLE_BALLOON_HELP_COMMAND_NAME
 *    Mi_DISABLE_BALLOON_HELP_COMMAND_NAME
 *    Mi_ENABLE_TOOLHINT_HELP_COMMAND_NAME
 *    Mi_DISABLE_TOOLHINT_HELP_COMMAND_NAME
 * </pre>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiHelpMenuCommands extends MiCommandHandler implements MiiCommandNames
	{
	private		MiiCommandManager manager;


					/**------------------------------------------------------
	 				 * Constructs a new MiHelpMenuCommands.
					 * @see 		MiHelpMenu
					 *------------------------------------------------------*/
	public				MiHelpMenuCommands(MiiCommandManager manager)
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
		super.setTargetOfCommand(target);
		updateStates();
		}
					/**------------------------------------------------------
			 		 * Processes the given command.
					 * @command  		the command to execute
					 *------------------------------------------------------*/
	public		void		processCommand(String cmd)
		{
		MiUtility.pushMouseAppearance(
			(MiPart )getTargetOfCommand(), MiiTypes.Mi_WAIT_CURSOR, "MiHelpMenuCommands");

		if (cmd.equalsIgnoreCase(Mi_ENABLE_BALLOON_HELP_COMMAND_NAME))
			{
			MiSystem.getHelpManager().setBalloonHelpEnabled(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_DISABLE_BALLOON_HELP_COMMAND_NAME))
			{
			MiSystem.getHelpManager().setBalloonHelpEnabled(false);
			}
		else if (cmd.equalsIgnoreCase(Mi_ENABLE_TOOLHINT_HELP_COMMAND_NAME))
			{
			MiSystem.getHelpManager().setToolHintHelpEnabled(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_DISABLE_TOOLHINT_HELP_COMMAND_NAME))
			{
			MiSystem.getHelpManager().setToolHintHelpEnabled(false);
			}
		else if (cmd.equalsIgnoreCase(Mi_ENABLE_STATUS_BAR_COMMAND_NAME))
			{
			MiSystem.getHelpManager().setStatusHelpEnabled(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_DISABLE_STATUS_BAR_COMMAND_NAME))
			{
			MiSystem.getHelpManager().setStatusHelpEnabled(false);
			}
		else if (cmd.equalsIgnoreCase(Mi_HELP_ON_WINDOW_COMMAND_NAME))
			{
			MiPart obj = (MiPart )getTargetOfCommand();
			String msg = MiSystem.getHelpManager().getDialogHelpForObject(obj, null);
			MiNativeDialog dialog = new MiNativeMessageDialog(
				obj.getRootWindow(), MiiToolkit.Mi_HELP_DIALOG_TYPE, msg);
			dialog.setVisible(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_HELP_ON_APPLICATION_COMMAND_NAME))
			{
			MiSystem.getHelpManager().displayHelpOnApplication();
			}
		else if (cmd.equalsIgnoreCase(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME))
			{
			MiSystem.getHelpManager().displayAboutDialog();
			}
		MiUtility.popMouseAppearance((MiPart )getTargetOfCommand(), "MiHelpMenuCommands");
		}
					/**------------------------------------------------------
	 				 * Updates the sensitivity of the widgets that generate
					 * some of the commands based on the type of help enabled
					 * in the Mica help manager.
					 * @see			MiSystem#getHelpManager
					 * @see			MiHelpManager
					 *------------------------------------------------------*/
	public		void		updateStates()
		{
		manager.setCommandState(Mi_ENABLE_BALLOON_HELP_COMMAND_NAME, 
			MiSystem.getHelpManager().getBalloonHelpEnabled());
		manager.setCommandState(Mi_ENABLE_TOOLHINT_HELP_COMMAND_NAME, 
			MiSystem.getHelpManager().getToolHintHelpEnabled());
		manager.setCommandState(Mi_ENABLE_STATUS_BAR_COMMAND_NAME, 
			MiSystem.getHelpManager().getStatusHelpEnabled());
		}
	}

