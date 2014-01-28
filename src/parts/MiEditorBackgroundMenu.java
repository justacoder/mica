
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
 * This class makes a 'background popup' menu that is suitable for adding to
 * graphics editors as their context menu. Each menu item has status bar and 
 * balloon help and most have accelerators and mnemonics.
 * <p>
 * When a menu item is selected by the user one of the following commands
 * are generated:
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
 * @see		MiiContextMenu
 * @see		MiPart#setContextMenu
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorBackgroundMenu extends MiEditorMenu implements MiiCommandNames, MiiMessages
	{
					/**------------------------------------------------------
	 				 * Constructs a new MiEditorBackgroundMenu, forwarding commands
					 * generated by this menu to an instance of 
					 * MiShapePopupMenuCommands.
					 * @see			MiEditorBackgroundMenuCommands
					 *------------------------------------------------------*/
	public				MiEditorBackgroundMenu(MiiCommandManager manager)
		{
		this(new MiEditorBackgroundMenuCommands(manager), manager);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEditorBackgroundMenu which will send commands
					 * generated by the menu to the given command handler.
					 * @param handler	the command handler
					 * @see 		MiEditorBackgroundMenuCommands
					 *------------------------------------------------------*/
	public				MiEditorBackgroundMenu(MiiCommandHandler handler, MiiCommandManager manager)
		{
		super("Editor Popup", manager);

		setCommandHandler(handler);

		appendMenuItem(		"Cu&t", 		handler, 
								Mi_CUT_COMMAND_NAME);
		appendMenuItem(		"&Copy", 		handler, 
								Mi_COPY_COMMAND_NAME);
		appendMenuItem(		"&Paste", 		handler, 
								Mi_PASTE_COMMAND_NAME);
		appendMenuItem(		"Dup&licate", 		handler, 
								Mi_DUPLICATE_COMMAND_NAME);
		appendSeparator();
		appendMenuItem(		"&Delete", 		handler, 
								Mi_DELETE_COMMAND_NAME);
		appendSeparator();
		appendMenuItem(		"Zoom &In",		handler, 
								Mi_ZOOM_IN_COMMAND_NAME);
		appendMenuItem(		"Zoom &Out",		handler, 
								Mi_ZOOM_OUT_COMMAND_NAME);
		appendSeparator();
		appendMenuItem(		"&Properties...", 	handler, 
								Mi_DISPLAY_PROPERTIES_COMMAND_NAME);


		setHelpMessages(Mi_CUT_COMMAND_NAME,
				Mi_CUT_STATUS_HELP_MSG,
				Mi_NO_CUT_STATUS_HELP_MSG,
				Mi_CUT_BALLOON_HELP_MSG);

		setHelpMessages(Mi_COPY_COMMAND_NAME,
				Mi_COPY_STATUS_HELP_MSG,
				Mi_NO_COPY_STATUS_HELP_MSG,
				Mi_COPY_BALLOON_HELP_MSG);

		setHelpMessages(Mi_PASTE_COMMAND_NAME,
				Mi_PASTE_STATUS_HELP_MSG,
				Mi_NO_PASTE_STATUS_HELP_MSG,
				Mi_PASTE_BALLOON_HELP_MSG);

		setHelpMessages(Mi_DUPLICATE_COMMAND_NAME,
				Mi_DUPLICATE_STATUS_HELP_MSG,
				Mi_NO_DUPLICATE_STATUS_HELP_MSG,
				Mi_DUPLICATE_BALLOON_HELP_MSG);

		setHelpMessages(Mi_DELETE_COMMAND_NAME,
				Mi_DELETE_STATUS_HELP_MSG,
				Mi_NO_DELETE_STATUS_HELP_MSG,
				Mi_DELETE_BALLOON_HELP_MSG);

		setHelpMessages(Mi_ZOOM_IN_COMMAND_NAME,
				Mi_ZOOM_IN_STATUS_HELP_MSG,
				Mi_NO_ZOOM_IN_STATUS_HELP_MSG,
				Mi_ZOOM_IN_BALLOON_HELP_MSG);

		setHelpMessages(Mi_ZOOM_OUT_COMMAND_NAME,
				Mi_ZOOM_OUT_STATUS_HELP_MSG,
				Mi_NO_ZOOM_OUT_STATUS_HELP_MSG,
				Mi_ZOOM_OUT_BALLOON_HELP_MSG);

		setHelpMessages(Mi_DISPLAY_PROPERTIES_COMMAND_NAME,
				Mi_DISPLAY_PROPERTIES_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_PROPERTIES_STATUS_HELP_MSG,
				Mi_DISPLAY_PROPERTIES_BALLOON_HELP_MSG);
		}
	}
