
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
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPaletteBackgroundMenu extends MiEditorMenu 
		implements MiiCommandNames, MiiActionTypes, MiiCommandHandler, MiiActionHandler
	{
	private		MiGraphicsPalette	palette;
	private		MiiCommandManager	commandManager;

					/**------------------------------------------------------
	 				 * Constructs a new MiPaletteBackgroundMenu. 
					 * @param palette 	the palette
					 *------------------------------------------------------*/
	public				MiPaletteBackgroundMenu(MiGraphicsPalette palette)
		{
		super("Palette Options", new MiCommandManager());

		setCommandHandler(this);

		commandManager = getCommandManager();
		this.palette = palette;

/*
		appendMenuItem(		"Cu&t", 		this, 
								Mi_CUT_COMMAND_NAME);
		appendMenuItem(		"&Copy", 		this, 
								Mi_COPY_COMMAND_NAME);
		appendMenuItem(		"&Paste", 		this, 
								Mi_PASTE_COMMAND_NAME);
		appendSeparator();
*/
		appendMenuItem(		"&Delete", 		this, 
								Mi_DELETE_COMMAND_NAME);
		appendSeparator();
		appendBooleanMenuItem(	"Labels", 		this, 
								MiEditorPalette.Mi_SHOW_LABELS_COMMAND_NAME,
								MiEditorPalette.Mi_HIDE_LABELS_COMMAND_NAME);
/*
		appendMenuItem(		"&Properties...", 	this,
								Mi_DISPLAY_PROPERTIES_COMMAND_NAME);


		setHelpMessages(Mi_COPY_COMMAND_NAME,
				Mi_COPY_STATUS_HELP_MSG,
				Mi_NO_COPY_STATUS_HELP_MSG,
				Mi_COPY_BALLOON_HELP_MSG);

*/
		commandManager.setCommandState(Mi_SHOW_LABELS_COMMAND_NAME, palette.getLabelsAreVisible());
		setItemsSelected(false);

		palette.getTreeListPalette().appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		palette.getTreeListPalette().appendActionHandler(this, Mi_NO_ITEMS_SELECTED_ACTION);
		}
	protected	void		setItemsSelected(boolean flag)
		{
		commandManager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, flag);
		//commandManager.setCommandSensitivity(Mi_CUT_COMMAND_NAME, flag);
		//commandManager.setCommandSensitivity(Mi_COPY_COMMAND_NAME, flag);
		//commandManager.setCommandSensitivity(Mi_PASTE_COMMAND_NAME, !flag);
		//commandManager.setCommandSensitivity(Mi_DISPLAY_PROPERTIES_COMMAND_NAME, flag);
		}
					/**------------------------------------------------------
	 				 * Process the given action.
					 * Actions handled:
					 *	Mi_ITEM_SELECTED_ACTION
					 *	Mi_NO_ITEMS_SELECTED_ACTION
					 * @param action	the action to process.
					 * @return 		true if can propogate the action to
					 *			other handlers.
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			setItemsSelected(true);
			}
		else if (action.hasActionType(Mi_NO_ITEMS_SELECTED_ACTION))
			{
			setItemsSelected(false);
			}
		return(true);
		}

					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are
					 * <pre>
					 *    Mi_CUT_COMMAND_NAME
					 *    Mi_COPY_COMMAND_NAME
					 *    Mi_PASTE_COMMAND_NAME
					 *    Mi_DELETE_COMMAND_NAME
					 *    Mi_SHOW_LABELS_COMMAND_NAME
					 *    Mi_HIDE_LABELS_COMMAND_NAME
					 *    Mi_DISPLAY_PROPERTIES_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 * @implements 		MiiCommandHandler#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		if (command.equals(Mi_DELETE_COMMAND_NAME))
			{
			MiTreeList treeList = palette.getTreeListPalette();
			Object[] tags = treeList.getSelectedItemsTags();
			while (tags.length > 0)
				{
				MiTableAddItemCommand cmd = new MiTableAddItemCommand(treeList, tags[0], false);
				treeList.removeItemWithTag(tags[0]);
				MiSystem.getTransactionManager().appendTransaction(cmd);
				tags = treeList.getSelectedItemsTags();
				}
			}
		else if (command.equals(Mi_SHOW_LABELS_COMMAND_NAME))
			{
			palette.setLabelsAreVisible(true);
			}
		else if (command.equals(Mi_HIDE_LABELS_COMMAND_NAME))
			{
			palette.setLabelsAreVisible(false);
			}
		}

	}


