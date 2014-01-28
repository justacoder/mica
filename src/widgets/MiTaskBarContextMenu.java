
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
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTaskBarContextMenu extends MiEditorMenu implements MiiCommandNames, MiiMessages
	{


					/**------------------------------------------------------
	 				 * Constructs a new MiTaskBarContextMenu. 
					 * @param commandHandler where we are to send the commands
					 *			 this menu generates
					 *------------------------------------------------------*/
	public				MiTaskBarContextMenu(
						MiiCommandHandler commandHandler,
						MiiCommandManager commandManager,
						MiPart closeIcon,
						String id)
		{
		super("Options", commandManager);

		appendMenuItemCommand(		new MiMenuItem(closeIcon.copy(), "Close"),
					 	commandHandler, 
						Mi_CLOSE_DOCUMENT_COMMAND_NAME + id);
/*
		appendMenuItem(			new MiMenuItem("Move Taskbar Tab Right", moveRightIcon.copy()),
						commandHandler,
						Mi_MOVE_TASK_BAR_TAB_RIGHT_COMMAND_NAME + id);
		appendMenuItem(			new MiMenuItem("Move Taskbar Tab Left",	moveLeftIcon.copy()),
						commandHandler,
						Mi_MOVE_TASK_BAR_TAB_LEFT_COMMAND_NAME + id);
*/
		}
	}

