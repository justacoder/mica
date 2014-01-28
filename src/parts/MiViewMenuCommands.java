
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
import java.util.Stack; 

/**----------------------------------------------------------------------------------------------
 * This class can execute commands that a standard 'view' menu might
 * generate. The commands apply to the target MiEditor (see 
 * setTargetOfCommand()). The commands that are handled are:
 * <p>
 * <pre>
 *    Mi_ZOOM_IN_COMMAND_NAME
 *    Mi_ZOOM_OUT_COMMAND_NAME
 *    Mi_VIEW_ALL_COMMAND_NAME
 *    Mi_VIEW_PREVIOUS_COMMAND_NAME
 *    Mi_VIEW_NEXT_COMMAND_NAME
 *    Mi_ZOOM_HOME_COMMAND_NAME
 *    Mi_ZOOM_TO_FIT_COMMAND_NAME
 *    Mi_ZOOM_TO_FIT_WIDTH_COMMAND_NAME
 *    Mi_ZOOM_TO_FIT_HEIGHT_COMMAND_NAME
 *    Mi_REDRAW_COMMAND_NAME
 *    Mi_DISPLAY_TOOL_BAR_COMMAND_NAME
 *    Mi_HIDE_TOOL_BAR_COMMAND_NAME
 *    Mi_DISPLAY_STATUS_BAR_COMMAND_NAME
 *    Mi_HIDE_STATUS_BAR_COMMAND_NAME
 * </pre>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiViewMenuCommands extends MiCommandHandler implements MiiCommandNames, MiiActionHandler, MiiActionTypes
	{
	private		MiiCommandManager	manager;
	private		MiEditor			editor;
	private		MiPart				toolbar;
	private		MiPart				statusBar;
	private		MiPanAndZoomCommand		panAndZoomCommand	= new MiPanAndZoomCommand();


					/**------------------------------------------------------
	 				 * Constructs a new MiViewMenuCommands.
					 * @see 		MiViewMenu
					 *------------------------------------------------------*/
	public				MiViewMenuCommands(MiiCommandManager manager)
		{
		this(manager, null, null);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiViewMenuCommands.
					 * @param toolbar	the toolbar that this will hide/show
					 * @param statusBar	the status bar that this will hide/show
					 * @see 		MiViewMenu
					 *------------------------------------------------------*/
	public				MiViewMenuCommands(MiiCommandManager manager,
						MiPart toolbar, MiPart statusBar)
		{
		this.manager = manager;
		this.toolbar = toolbar;
		this.statusBar = statusBar;
		MiSystem.getViewportTransactionManager().appendActionHandler(
			this, Mi_TRANSACTION_MANAGER_CHANGED_ACTION);
		initSensitivityStates();
		}
					/**------------------------------------------------------
	 				 * Sets the toolbar to hide/show.
					 * @param toolbar	the toolbar or null
					 *------------------------------------------------------*/
	public		void		setToolBar(MiPart toolbar)
		{
		this.toolbar = toolbar;
		initSensitivityStates();
		}
					/**------------------------------------------------------
	 				 * Sets the status bar to hide/show.
					 * @param statusBar	the status bar or null
					 *------------------------------------------------------*/
	public		void		setStatusBar(MiPart statusBar)
		{
		this.statusBar = statusBar;
		initSensitivityStates();
		}
					/**------------------------------------------------------
	 				 * Sets the target of the commands processed by this 
					 * MiiCommandHandler (i.e. what the commands are to act upon).
					 * The target must be an MiEditor.
					 * @param  		the target MiEditor
					 * @see			MiCommandHandler#getTargetOfCommand
					 * @see			#processCommand
					 *------------------------------------------------------*/
	public		void		setTargetOfCommand(Object newObject)
		{
		editor = (MiEditor )newObject;
		panAndZoomCommand.setTargetOfCommand(editor);
		super.setTargetOfCommand(editor);
		initSensitivityStates();
		}
					/**------------------------------------------------------
			 		 * Processes the given command.
					 * @command  		the command to execute
					 *------------------------------------------------------*/
	public		void		processCommand(String cmd)
		{
		MiUtility.pushMouseAppearance(
			(MiPart )getTargetOfCommand(), MiiTypes.Mi_WAIT_CURSOR, "MiViewMenuCommands");

		if (cmd.equalsIgnoreCase(Mi_ZOOM_IN_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_ZOOM_IN_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_ZOOM_OUT_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_ZOOM_OUT_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_VIEW_ALL_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_VIEW_ALL_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_VIEW_PREVIOUS_COMMAND_NAME))
			{
			if (MiSystem.getViewportTransactionManager().hasTransactionsToUndo())
				{
				MiSystem.getViewportTransactionManager().undoTransaction();
				}
			}
		else if (cmd.equalsIgnoreCase(Mi_VIEW_NEXT_COMMAND_NAME))
			{
			if (MiSystem.getViewportTransactionManager().hasTransactionsToRedo())
				{
				MiSystem.getViewportTransactionManager().redoTransaction();
				}
			}
		else if (cmd.equalsIgnoreCase(Mi_ZOOM_HOME_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_ZOOM_HOME_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_ZOOM_TO_FIT_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_ZOOM_TO_FIT_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_ZOOM_TO_FIT_WIDTH_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_ZOOM_TO_FIT_WIDTH_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_ZOOM_TO_FIT_HEIGHT_COMMAND_NAME))
			{
			panAndZoomCommand.processCommand(Mi_ZOOM_TO_FIT_HEIGHT_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(Mi_VIEW_REPEAT_COMMAND_NAME))
			{
			if (MiSystem.getViewportTransactionManager().hasTransactionsToRepeat())
				{
				MiSystem.getViewportTransactionManager().repeatTransaction();
				}
			}
		else if (cmd.equalsIgnoreCase(Mi_REDRAW_COMMAND_NAME))
			{
			editor.invalidateArea();
			}
		else if (cmd.equalsIgnoreCase(Mi_DISPLAY_TOOL_BAR_COMMAND_NAME))
			{
			if (toolbar != null)
				toolbar.setVisible(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_HIDE_TOOL_BAR_COMMAND_NAME))
			{
			if (toolbar != null)
				toolbar.setVisible(false);
			}
		else if (cmd.equalsIgnoreCase(Mi_DISPLAY_STATUS_BAR_COMMAND_NAME))
			{
			if (statusBar != null)
				statusBar.setVisible(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_HIDE_STATUS_BAR_COMMAND_NAME))
			{
			if (statusBar != null)
				statusBar.setVisible(false);
			}
		MiUtility.popMouseAppearance((MiPart )getTargetOfCommand(), "MiViewMenuCommands");
		}
					/**------------------------------------------------------
	 				 * Processes the given action.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 	 		false if it is NOT OK to send
					 *			action to the next action handler
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_TRANSACTION_MANAGER_CHANGED_ACTION))
			{
			manager.setCommandSensitivity(Mi_VIEW_PREVIOUS_COMMAND_NAME, 
				MiSystem.getViewportTransactionManager().hasTransactionsToUndo());
			manager.setCommandSensitivity(Mi_VIEW_NEXT_COMMAND_NAME, 
				MiSystem.getViewportTransactionManager().hasTransactionsToRedo());
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Initializes the widgets that correspond to the commands
					 * this clas processes.
					 *------------------------------------------------------*/
	protected	void		initSensitivityStates()
		{
		manager.setCommandSensitivity(Mi_VIEW_PREVIOUS_COMMAND_NAME, 
			MiSystem.getViewportTransactionManager().hasTransactionsToUndo());
		manager.setCommandSensitivity(Mi_VIEW_NEXT_COMMAND_NAME, 
			MiSystem.getViewportTransactionManager().hasTransactionsToRedo());

		if (toolbar == null)
			manager.setCommandSensitivity(Mi_DISPLAY_TOOL_BAR_COMMAND_NAME, false);
		else
			{
			manager.setCommandSensitivity(Mi_DISPLAY_TOOL_BAR_COMMAND_NAME, true);
			if (toolbar.isVisible())
				manager.setCommandState(Mi_DISPLAY_TOOL_BAR_COMMAND_NAME, true);
			else
				manager.setCommandState(Mi_DISPLAY_TOOL_BAR_COMMAND_NAME, false);
			}

		if (statusBar == null)
			manager.setCommandSensitivity(Mi_DISPLAY_STATUS_BAR_COMMAND_NAME, false);
		else
			{
			manager.setCommandSensitivity(Mi_DISPLAY_STATUS_BAR_COMMAND_NAME, true);
			if (statusBar.isVisible())
				manager.setCommandState(Mi_DISPLAY_STATUS_BAR_COMMAND_NAME, true);
			else
				manager.setCommandState(Mi_DISPLAY_STATUS_BAR_COMMAND_NAME, false);
			}
		}
	}

