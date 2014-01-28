
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
import com.swfm.mica.util.Strings; 

/**----------------------------------------------------------------------------------------------
 * This class implements a menu that can be targeted at an MiEditor
 * (i.e. the menu's functionality acts upon the MiEditor or it's
 * contents).
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorMenu implements MiiContextMenu, MiiDisplayNames, MiiCommandNames
	{
	private		MiEditor		editor;
	private		MiMenu			menu;
	private		MiStandardMenu		standardMenu;
	private		MiRadioStateEnforcer	radioEnforcer;
	private		MiiCommandManager	commandManager;
	private		MiiCommandHandler	commandHandler;
	
					/**------------------------------------------------------
	 				 * Constructs a new MiEditorMenu with the given name. If 
					 * this menu is assigned to a menubar then this name is
					 * used as the menu launcher button's label.
					 * @param name		the name of the menu
					 * @param manager	the manager responsible for handling
					 *			menu item command availabilities and
					 *			states (this is usually a MiEditorWindow).
					 *------------------------------------------------------*/
	public				MiEditorMenu(String name, MiiCommandManager manager)
		{
		commandManager = manager;
		standardMenu = new MiStandardMenu();
		menu = new MiMenu(standardMenu);
		menu.setName(name);
		}
					/**------------------------------------------------------
	 				 * Gets the menu this MiEditorMenu uses.
					 * @return 		the menu
					 *------------------------------------------------------*/
	public		MiMenu		getMenu()
		{
		return(menu);
		}
					/**------------------------------------------------------
	 				 * Gets the menu contents this MiEditorMenu uses.
					 * @return 		the standard menu
					 *------------------------------------------------------*/
	public		MiStandardMenu	getMenuContents()
		{
		return(standardMenu);
		}
					/**------------------------------------------------------
	 				 * Gets the command manager that this MiEditorMenu uses and
					 * which manages the sensitivity and state of the menu's items.
					 * @return 		the command manager
					 *------------------------------------------------------*/
	public		MiiCommandManager getCommandManager()
		{
		return(commandManager);
		}
					/**------------------------------------------------------
	 				 * Sets the command handler that the default menuItems use
					 * (if any) which handles most or all of the menuItem's
					 * specific functionality. This is set by some built-in menus
					 * which use one command handler to handle all user actions.
					 * @param handler 		the command handler
					 *------------------------------------------------------*/
	public		void		setCommandHandler(MiiCommandHandler handler)
		{
		commandHandler = handler;
		}
					/**------------------------------------------------------
	 				 * Gets the command handler that the default menuItems use
					 * (if any) which handles most or all of the menuItem's
					 * specific functionality.
					 * @return 		the command handler
					 *------------------------------------------------------*/
	public		MiiCommandHandler getCommandHandler()
		{
		return(commandHandler);
		}
					/**------------------------------------------------------
	 				 * Specifies that any tool items added are to be radio
					 * buttons. One and only one can be selected (set) at one
					 * time.
					 *------------------------------------------------------*/
	public		void		startRadioButtonSection()
		{
		startRadioButtonSection(1);
		}
					/**------------------------------------------------------
	 				 * Specifies that any tool items added are to be radio
					 * buttons. Only one can be selected (set) at one time.
					 * @param minNumberSelected	the number that must be
					 *				selected at all times
					 *------------------------------------------------------*/
	public		void		startRadioButtonSection(int minNumberSelected)
		{
		radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(minNumberSelected);
		}
					/**------------------------------------------------------
	 				 * Specifies that any tool items added are no longer to
					 * be considered radio buttons.
					 *------------------------------------------------------*/
	public		void		endRadioButtonSection()
		{
		radioEnforcer = null;
		}
					/**------------------------------------------------------
	 				 * Gets the index of the menuItem that generates the given
					 * command.
					 * @param command		the command of a menu item
					 *------------------------------------------------------*/
	public		int		getIndexOfMenuItem(String command)
		{
		return(standardMenu.getIndexOfItem(getMenuItemWithCommand(command)));
		}
					/**------------------------------------------------------
	 				 * Appends a seperator (horizontal line) under the bottommost
					 * item in the menu.	
					 * @return		the separator
					 *------------------------------------------------------*/
	public		MiMenuItem	appendSeparator()
		{
		MiMenuItem separator = new MiMenuItem("----");
		standardMenu.appendItem(separator);
		return(separator);
		}
					/**------------------------------------------------------
	 				 * Inserts a seperator (horizontal line) at the given index.
					 * @param index		the insertion point
					 * @return		the separator
					 *------------------------------------------------------*/
	public		MiMenuItem	insertSeparator(int index)
		{
		MiMenuItem separator = new MiMenuItem("----");
		standardMenu.insertItem(separator, index);
		return(separator);
		}
					/**------------------------------------------------------
	 				 * Appends a menu item below the bottommost item in the menu.
					 * @param itemName	the displayed text of the menu item
					 * 			and the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is activated
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	appendMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler)
		{
		MiMenuItem menuItem = buildMenuItem(itemName, commandHandler, itemName);
		standardMenu.appendItem(menuItem);
		return(menuItem);
		}
					/**------------------------------------------------------
	 				 * Appends a menu item below the bottommost item in the menu.
					 * @param itemName	the displayed text of the menu item
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @param command	the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is activated
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	appendMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler, 
						String command)
		{
		MiMenuItem menuItem = buildMenuItem(itemName, commandHandler, command);
		standardMenu.appendItem(menuItem);
		return(menuItem);
		}
					/**------------------------------------------------------
	 				 * Appends a menu item below the bottommost item in the menu.
					 * @param itemName	the displayed text of the menu item
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @param command	the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is activated
					 * @param availableStatusHelpMsg
					 *			the status message when this item
					 *			is 'sensitive'
					 * @param unAvailableStatusHelpMsg
					 *			the status message when this item
					 *			is 'insensitive'
					 * @param balloonHelpMsg the balloon help message for item
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	appendMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler, 
						String command,
						String availableStatusHelpMsg,
						String unAvailableStatusHelpMsg,
						String balloonHelpMsg)
		{
		MiMenuItem menuItem = buildMenuItem(itemName, commandHandler, command);
		menuItem.setNormalStatusHelpMessage(availableStatusHelpMsg);
		menuItem.setInSensitiveStatusHelpMessage(unAvailableStatusHelpMsg);
		menuItem.setBalloonHelpMessage(balloonHelpMsg);
		standardMenu.appendItem(menuItem);
		return(menuItem);
		}
					/**------------------------------------------------------
	 				 * Inserts a menu item at the menu item that generates the
					 * given 'insertBeforeThisCommand' command.
					 * @param itemName	the displayed text of the menu item
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @param command	the command that is sent to the 
					 *			commandHandler when the menuitem is 
					 *			activated
					 * @param insertBeforeThisCommand
					 *			the command generated by the menu
					 *			item at the insertion point
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	insertMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler, 
						String command,
						String insertBeforeThisCommand)
		{
		int index = standardMenu.getIndexOfItem(getMenuItemWithCommand(insertBeforeThisCommand));
		return(insertMenuItem(itemName, commandHandler, command, index));
		}
					/**------------------------------------------------------
	 				 * Inserts a menu item at the given index.
					 * @param itemName	the displayed text of the menu item
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @param command	the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is activated
					 * @param index		the index of the insertion point
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	insertMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler, 
						String command,
						int index)
		{
		MiMenuItem menuItem = buildMenuItem(itemName, commandHandler, command);
		standardMenu.insertItem(menuItem, index);
		return(menuItem);
		}
					/**------------------------------------------------------
	 				 * Appends a two-state menu item below the bottommost item
					 * in the menu.
					 * @param itemName	the displayed text of the menu item
					 * 			and the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is 'set'. Mi_NOOP_COMMAND_NAME is
					 *			send to the commandHandler when the
					 *			menu item is 'unset'.
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	appendBooleanMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler)
		{
		MiMenuItem menuItem = buildBooleanMenuItem(
				itemName, commandHandler, itemName, Mi_NOOP_COMMAND_NAME);
		standardMenu.appendItem(menuItem);
		return(menuItem);
		}
					/**------------------------------------------------------
	 				 * Appends a two-state menu item below the bottommost item
					 * in the menu.
					 * @param itemName	the displayed text of the menu item
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @param onCommand	the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is 'set'
					 * @param offCommand	the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is not 'set'
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	appendBooleanMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler, 
						String onCommand,
						String offCommand)
		{
		MiMenuItem menuItem = buildBooleanMenuItem(itemName, commandHandler, onCommand, offCommand);
		standardMenu.appendItem(menuItem);
		return(menuItem);
		}
					/**------------------------------------------------------
	 				 * Inserts a two-state menu item at the menu item that 
					 * generates the given 'insertBeforeThisCommand' command.
					 * @param itemName	the displayed text of the menu item
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @param onCommand	the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is 'set'
					 * @param offCommand	the command that is sent to the 
					 *			commandHandler when the menuitem 
					 *			is not 'set'
					 * @param insertBeforeThisCommand
					 *			the command generated by the menu
					 *			item at the insertion point
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	insertBooleanMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler, 
						String onCommand,
						String offCommand,
						String insertBeforeThisCommand)
		{
		MiMenuItem menuItem = buildBooleanMenuItem(
			itemName, commandHandler, onCommand, offCommand);
		int index = standardMenu.getIndexOfItem(getMenuItemWithCommand(insertBeforeThisCommand));
		standardMenu.insertItem(menuItem, index);
		return(menuItem);
		}
					/**------------------------------------------------------
	 				 * Removes the menu item that generates the given command.
					 * @param commandName	the command generated by the menu 
					 *			item to remove.
					 *------------------------------------------------------*/
	public		void		removeMenuItemWithCommand(String command)
		{
		standardMenu.removeItem(getMenuItemWithCommand(command));
		}
					/**------------------------------------------------------
	 				 * Removes all menu items.
					 *------------------------------------------------------*/
	public		void		removeAllMenuItems()
		{
		standardMenu.removeAllItems();
		}
					/**------------------------------------------------------
	 				 * Removes all menu items that do not generate one of the 
					 * given commands.
					 * @param commandsToKeep the command generated by the menu 
					 *			that we will keep
					 *------------------------------------------------------*/
	public		void		removeAllMenuItemsWithCommandsExcept(Strings commandsToKeep)
		{
		for (int j = 0; j < standardMenu.getNumberOfItems(); ++j)
			{
			MiMenuItem  menuItem = standardMenu.getMenuItem(j);
			if (menuItem.isSeparator())
				{
				standardMenu.removeItem(menuItem);
				--j;
				}
			else
				{
				for (int i = 0; i < menuItem.getNumberOfActionHandlers(); ++i)
					{
					MiiAction h = menuItem.getActionHandler(i);
					if (h instanceof MiActionCallback) 
						{
						String command = ((MiActionCallback )h).getCommand();
						if ((command != null) && (!commandsToKeep.contains(command)))
							{
							standardMenu.removeItem(menuItem);
							--j;
							break;
							}
						}
					}
				}
			}
		}
	public		void		removeAllAdjacentSeparators()
		{
		standardMenu.removeAllAdjacentSeparators();
		}
					/**------------------------------------------------------
	 				 * Append the given commandHandler with the given command to
					 * the given menu item.
					 * @param menuItem	the menu item
					 * @param commandHandler the command handler to append
					 * @param command	the command  to be sent to the command
					 *			handler
					 *------------------------------------------------------*/
	public		void		appendMenuItemCommand(
						MiMenuItem menuItem, 
						MiiCommandHandler commandHandler, 
						String command)
		{
		MiActionCallback callback = new MiActionCallback(commandHandler, command);
		menuItem.appendActionHandler(callback);
		commandManager.registerCommandDependentWidget(menuItem, command);
		standardMenu.appendItem(menuItem);
		}
					/**------------------------------------------------------
	 				 * Remove all commandHandlers from the given menu item.
					 * @param menuItem	the menu item
					 *------------------------------------------------------*/
	public		void		removeCommandHandlers(MiMenuItem menuItem)
		{
		for (int i = 0; i < menuItem.getNumberOfActionHandlers(); ++i)
			{
			MiiAction h = menuItem.getActionHandler(i);
			if (h instanceof MiActionCallback) 
					//&& (((MiActionCallback )h).getCommandHandler()
					//instanceof MiiCommandManager))
				{
				menuItem.removeActionHandler(i);
				--i;
				}
			}
		}
					/**------------------------------------------------------
	 				 * Append given commandHandler to the given menu item.
					 * @param menuItem	the menuItem
					 * @param command	the command a menuitem will send to the handler
					 * @param comamndHandler the handler
					 *------------------------------------------------------*/
	public		void		appendCommandHandler(MiMenuItem menuItem, String command, MiiCommandHandler handler)
		{
		MiActionCallback callback = new MiActionCallback(handler, command);
		menuItem.appendActionHandler(callback);
		commandManager.registerCommandDependentWidget(menuItem, command);
		}
					/**------------------------------------------------------
	 				 * Sets the name of the menu this MiEditorMenu uses. If
					 * this menu is assigned to a menubar then this name is
					 * used as the menu launcher button's label.
					 * @param name 		the menu name
					 *------------------------------------------------------*/
	public		void		setName(String name)
		{
		menu.setName(name);
		}
					/**------------------------------------------------------
	 				 * Gets the MiMenuItem that generates the given command.
					 * @param menuItemCommand the command generated by the 
					 * 			MiMenuItem
					 * @return 		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	getMenuItemWithCommand(String menuItemCommand)
		{
		for (int j = 0; j < standardMenu.getNumberOfItems(); ++j)
			{
			MiMenuItem  menuItem = standardMenu.getMenuItem(j);
			for (int i = 0; i < menuItem.getNumberOfActionHandlers(); ++i)
				{
				MiiAction h = menuItem.getActionHandler(i);
				if ((h instanceof MiActionCallback) 
					&& (((MiActionCallback )h).getCommand() != null)
					&& (((MiActionCallback )h).getCommand().equals(menuItemCommand)))
					{
					return(menuItem);
					}
				}
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the MiMenuItem that displays the given label.
					 * @param menuItemLabel	the label displayed by the 
					 * 			MiMenuItem to get
					 * @return 		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	getMenuItemWithLabel(String menuItemLabel)
		{
		for (int i = 0; i < standardMenu.getNumberOfItems(); ++i)
			{
			String value = standardMenu.getMenuItem(i).getValue();
			if ((!Utility.isEmptyOrNull(value)) && (value.equals(menuItemLabel)))
				return(standardMenu.getMenuItem(i));
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Sets the label of the MiMenuItem that generates the given
					 * command.
					 * @param command	the command generated by the
					 *			menu item
					 * @param label		the new label for the menu item
					 *------------------------------------------------------*/
	public		void		setMenuItemLabel(String command, String label)
		{
		getMenuItemWithCommand(command).setLabel(label);
		}
					/**------------------------------------------------------
	 				 * Sets the help messages to be associated with the MiMenuItem
					 * that generates the given command.
					 * @param command	the command generated by the
					 *			menu item to assign help to
					 * @param availableStatusHelpMsg
					 *			the new status bar help message
					 * @param unAvailableStatusHelpMsg
					 *			the new status bar help message
					 *			for when the menu item is desensitized
					 * @param balloonHelpMsg
					 *			the new balloon help message
					 *------------------------------------------------------*/
	public		void		setHelpMessages(String command,
							String availableStatusHelpMsg,
							String unAvailableStatusHelpMsg,
							String balloonHelpMsg)
		{
		MiMenuItem menuItem = getMenuItemWithCommand(command);
		menuItem.setNormalStatusHelpMessage(availableStatusHelpMsg);
		menuItem.setInSensitiveStatusHelpMessage(unAvailableStatusHelpMsg);
		menuItem.setBalloonHelpMessage(balloonHelpMsg);
		}
					/**------------------------------------------------------
	 				 * Sets the editor to be targeted by the functionality
					 * of this menu.
					 * @param editor	the editor to target
					 *------------------------------------------------------*/
	public 		void		setEditor(MiEditor editor)
		{
		for (int j = 0; j < standardMenu.getNumberOfItems(); ++j)
			{
			MiMenuItem menuItem = standardMenu.getMenuItem(j);
			for (int i = 0; i < menuItem.getNumberOfActionHandlers(); ++i)
				{
				MiiAction h = menuItem.getActionHandler(i);
				if ((h instanceof MiActionCallback) 
					&& (((MiActionCallback )h).getCommandHandler()
						instanceof MiiTargetableCommandHandler))
					{
					((MiiTargetableCommandHandler )((MiActionCallback )h)
						.getCommandHandler()).setTargetOfCommand(editor);
					}
				}
			}
		this.editor = editor;
		}
					/**------------------------------------------------------
	 				 * Gets the editor to be targeted by the functionality
					 * of this menu.
					 * @return 		the editor to target
					 *------------------------------------------------------*/
	public 		MiEditor	getEditor()
		{
		return(editor);
		}

	//***************************************************************************************
	// MiiContextMenu interface
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Sets whether to select the target of this menu when the
					 * menu is popped up.
					 * @param flag 		true if we are to select the shape
					 *			that this menu is targeting when
					 *			this menu is popped up 
					 * @implements 		MiiContextMenu#setToSelectAttributedShape
					 *------------------------------------------------------*/
	public		void		setToSelectAttributedShape(boolean flag)
		{
		}
					/**------------------------------------------------------
	 				 * Gets whether to select the target of this menu when the
					 * menu is popped up.
					 * @return 		true if we are to select the shape
					 *			that this menu is targeting when
					 *			this menu is popped up 
					 * @implements 		MiiContextMenu#getToSelectAttributedShape
					 *------------------------------------------------------*/
	public		boolean		getToSelectAttributedShape()
		{
		return(false);
		}
					/**------------------------------------------------------
	 				 * Pops up this menu with the given trigger object as the
					 * target.
					 * @param assocTriggerObj the target of this menu
					 * @param pt		the location to pop up this menu
					 * @implements 		MiiContextMenu#popup
					 *------------------------------------------------------*/
	public		void		popup(MiPart assocTriggerObj, MiPoint pt)
		{
		getMenu().popup(assocTriggerObj, pt);
		}
					/**------------------------------------------------------
	 				 * Pops down this menu.
					 * @implements 		MiiContextMenu#popdown
					 *------------------------------------------------------*/
	public		void		popdown()
		{
		getMenu().popdown();
		}
					/**------------------------------------------------------
	 				 * Gets the menu this MiEditorMenu uses.
					 * @return 		the menu
					 * @implements 		MiiContextMenu#getMenuGraphics
					 *------------------------------------------------------*/
	public		MiPart		getMenuGraphics()
		{
		return(menu);
		}

	//***************************************************************************************
	// Menu builder routines
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Builds a menu item for this menu.
					 * @param itemName	the displayed text of the menu item
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @param command	the command that is sent to the 
					 *			commandHandler when the menuitem is 
					 *			activated
					 * @return		the menu item
					 *------------------------------------------------------*/
	protected	MiMenuItem	buildMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler, 
						String command)
		{
		MiMenuItem menuItem = new MiMenuItem(itemName);
		MiActionCallback callback = new MiActionCallback(commandHandler, command);
		menuItem.appendActionHandler(callback);
		commandManager.registerCommandDependentWidget(menuItem, command);

		return(menuItem);
		}
					/**------------------------------------------------------
	 				 * Builds a tow-state menu item for this menu.
					 * @param itemName	the displayed text of the menu item
					 * @param commandHandler the command handler to call when the
					 *			menuitem is activated by the user
					 * @param onCommand	the command that is sent to the
					 *			commandHandler
					 *			when the menuitem is 'set'
					 * @param offCommand	the command that is sent to the 
					 *			commandHandler when the menuitem is not
					 *			'set'
					 * @return		the menu item
					 *------------------------------------------------------*/
	public		MiMenuItem	buildBooleanMenuItem(
						String itemName, 
						MiiCommandHandler commandHandler, 
						String onCommand,
						String offCommand)
		{
		// Strip off mnemonic and accelerator specifications...
		String labelName = MiSystem.getProperty(itemName, itemName);
		//String labelName = Utility.replaceAll(itemName, "&", "");
		if (labelName.indexOf('\t') != -1)
			labelName = labelName.substring(0, labelName.indexOf('\t'));

		MiToggleButton toggleButton = new MiToggleButton();
		toggleButton.setSelectable(false);
		if (radioEnforcer != null)
			{
			toggleButton.setRadioStateEnforcer(radioEnforcer);
			toggleButton.setShape(MiToolkit.radioButtonShape);
			}
		MiLabeledWidget labeledWidget = new MiLabeledWidget(toggleButton, labelName);
		labeledWidget.setBorderHiliteWidth(0);
		labeledWidget.setInsetMargins(0);
		labeledWidget.setBackgroundColor(MiiTypes.Mi_TRANSPARENT_COLOR);
		MiMenuItem menuItem = new MiMenuItem(null, labeledWidget, itemName);
		commandManager.registerCommandDependentWidget(menuItem, onCommand);
		menuItem.setMultiState(MiMenuItem.Mi_BINARY_STATE);
		MiActionCallback callback = new MiActionCallback(commandHandler, onCommand);
		
		menuItem.appendActionHandler(callback);
		callback = new MiActionCallback(
			commandHandler, offCommand, MiiActionTypes.Mi_DESELECTED_ACTION);
		
		menuItem.appendActionHandler(callback);
		return(menuItem);
		}
	}


