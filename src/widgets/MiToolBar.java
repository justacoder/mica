
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
import java.util.Hashtable; 
import com.swfm.mica.util.Set; 
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 
import java.util.Enumeration; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiToolBar extends MiWidget implements MiiOrientablePart, MiiCommandHandler, MiiCommandNames
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private		MiiCommandManager	commandManager;


	public static final String		Mi_SHOW_BORDERS_COMMAND_NAME	= "showBorders";
	public static final String		Mi_HIDE_BORDERS_COMMAND_NAME	= "hideBorders";
	public static final String		Mi_FOCUS_BORDERS_COMMAND_NAME	= "focusBorders";

	public static final	String		SPACER_NAME			= "spacer-name";
	public static final	String		Mi_BUTTON_SELECTED_BACKGROUND_COLOR = "selected.backgroundColor";
	private 		MiDistance	SPACER_WIDTH			= 8;
	private			int		SPACER_HEIGHT			= 8;
	public static final	int		Mi_BORDERED_TOOLBAR_BUTTONS	= (1 << 8) + Mi_RAISED_BORDER_LOOK;
	public static final	int		Mi_FOCUS_BORDER_TOOLBAR_BUTTONS	= 1 << 9;
	public static final	int		Mi_LABLED_TOOLBAR_BUTTONS	= 1 << 10;
	public static final	int		Mi_FOCUS_SPECIAL_FOCUS_BORDER_TOOLBAR_BUTTONS	= 1 << 11;

	public static final	int		Mi_OLD_STYLE_TOOLBAR_BUTTONS	
							= Mi_BORDERED_TOOLBAR_BUTTONS;

	public static final	int		Mi_NEW_STYLE_TOOLBAR_BUTTONS
							= Mi_BORDERED_TOOLBAR_BUTTONS
							+ Mi_LABLED_TOOLBAR_BUTTONS;

	public static final	int		Mi_97_STYLE_TOOLBAR_BUTTONS
							= Mi_FOCUS_BORDER_TOOLBAR_BUTTONS 
							+ Mi_LABLED_TOOLBAR_BUTTONS;

	private			int		orientation;
	private			boolean		orientationFixed;
	private			MiEditor	editor;
	private			Set		toolItemCommands	= new Set();
	private			Hashtable	toolItemCommandNames	= new Hashtable();
	private			MiSize		defaultToolItemSize;

	private			MiRadioStateEnforcer	radioEnforcer;

	private			int		currentButtonStyle	= Mi_97_STYLE_TOOLBAR_BUTTONS;
	private			String		selectedButtonBGColor;
	private			int		specialFocusBorderLookStyle	= Mi_NONE;
	private			MiMargins	buttonInsetMargins	= new MiMargins(5, 2, 2, 5);
	private static final 	int		Mi_BORDER_LOOK_MASK	= 0xff;
	

	
					/**------------------------------------------------------
	 				 * Constructs a new MiToolBar. Used for the copy operation.
					 * @see			MiPart#copy
					 *------------------------------------------------------*/
	protected			MiToolBar()
		{
		// Used for copy operation
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiToolBar which will be the target of
					 * the functionality added to this toolbar.
					 * @param manager	the manager for all of the widget/command
					 *			pairs of the tool items
					 * @see 		#setEditor
					 *------------------------------------------------------*/
	public				MiToolBar(MiiCommandManager manager)
		{
		this(null, manager);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiToolBar which will be the target of
					 * the functionality added to this toolbar.
					 * @param editor	the target editor
					 * @param manager	the manager for all of the widget/command
					 *			pairs of the tool items
					 * @see 		#setEditor
					 *------------------------------------------------------*/
	public				MiToolBar(MiEditor editor, MiiCommandManager manager)
		{
		commandManager = manager;
		setup(editor);
		}

					/**------------------------------------------------------
	 				 * Gets the manager for all of the widget/command pairs of 
					 * the tool items. This is used to set state of the commands
					 * (sensitivity, on/off, contents, etc..). This is usually
					 * used when one does not know the window the toolbar is in
					 * (which is typically the command manager).
					 *
					 * @return 		the manager
					 *------------------------------------------------------*/
	public		MiiCommandManager getCommandManager()
		{
		return(commandManager);
		}
					/**------------------------------------------------------
	 				 * Sets the orientation of this toolbar (valid values are
					 * Mi_HORIZONTAL and Mi_VERTICAL).
					 * @param orientation	the new orientation
					 *------------------------------------------------------*/
	public		void		setOrientation(int orientation)
		{
		if (this.orientation != orientation)
			{
			this.orientation = orientation;
			MiPoint pt = getCenter();
			if (orientation == Mi_HORIZONTAL)
				{
				MiLayout layout = new MiRowLayout();
				layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
				layout.setElementVSizing(Mi_NONE);
				setLayout(layout);
				}
			else
				{
				MiLayout layout = new MiColumnLayout();
				layout.setElementVJustification(Mi_TOP_JUSTIFIED);
				layout.setElementHSizing(Mi_NONE);
				setLayout(layout);
				}
			setCenter(pt);
			}
		}
					/**------------------------------------------------------
	 				 * Gets whether the orientation is constant and can not be
					 * changed from it's current state.
					 * @return 		true if the orientation cannot be
					 *			changed.
					 * @implements		MiiOrientablePart#isOrientationFixed
					 *------------------------------------------------------*/
	public		boolean		isOrientationFixed()
		{
		return(orientationFixed);
		}
					/**------------------------------------------------------
	 				 * Sets whether the orientation is constant and can not be
					 * changed from it's current state.
					 * @param flag 		true if the orientation cannot be
					 *			changed.
					 * @implements		MiiOrientablePart#setOrientation
					 *------------------------------------------------------*/
	public		void		setOrientationFixed(boolean flag)
		{
		orientationFixed = flag;
		}
					/**------------------------------------------------------
	 				 * Gets the orientation of this toolbar (valid values are
					 * Mi_HORIZONTAL and Mi_VERTICAL).
					 * @return 		the current orientation
					 * @implements		MiiOrientablePart#getOrientation
					 *------------------------------------------------------*/
	public		int		getOrientation()
		{
		return(orientation);
		}
					/**------------------------------------------------------
					 * Changes to the next supported orientation.
					 * @implements		MiiOrientablePart#cycleOrientation
					 *------------------------------------------------------*/
	public		void		cycleOrientation()
		{
		if (orientation == Mi_HORIZONTAL)
			setOrientation(Mi_VERTICAL);
		else
			setOrientation(Mi_HORIZONTAL);
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
	 				 * Appends a space (i.e. an empty area) to the right of the 
					 * existing tool items already in this toolbar.
					 *------------------------------------------------------*/
	public		void		appendSpacer()
		{
		MiRectangle spacer = new MiRectangle();
		spacer.setName(SPACER_NAME);
		spacer.setWidth(SPACER_WIDTH);
		spacer.setHeight(SPACER_HEIGHT);
		spacer.setColor(Mi_TRANSPARENT_COLOR);
		appendPart(spacer);
		}
	public		void		setSpacerWidth(MiDistance width)
		{
		SPACER_WIDTH = width;
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if (SPACER_NAME.equals(getPart(i).getName()))
				{
				getPart(i).setWidth(width);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Appends a pushButton with the given image to this toolbar,
					 * assigning it the given tool hint message and having it
					 * call the given commandHandler with the given command when
					 * the button is pressed by the user.
					 * @param toolHint		the toolHint
					 * @param image			the appearance
					 * @param commandHandler	the command handler to call
					 * @param command		the command to send
					 * @return			the newly created tool item
					 *------------------------------------------------------*/
	public		MiWidget	appendToolItem(
						String toolHint, 
						MiPart image,
						MiiCommandHandler commandHandler, 
						String command)
		{
		MiPushButton button = new MiPushButton(image);
		button.setInsetMargins(1);
		button.setDisplaysFocusBorder(false);
		button.appendCommandHandler(commandHandler, command);
		button.setToolHintMessage(toolHint);
		button.setAcceptingEnterKeyFocus(false);
		appendPart(button);
		commandManager.registerCommandDependentWidget(button, command);
		toolItemCommandNames.put(command, button);
		toolItemCommands.addElement(commandHandler);
		applyButtonStyle(button);
		if ((button instanceof MiLabel) && (((MiLabel )button).getLabel() instanceof MiImage))
			{
			if (defaultToolItemSize != null)
				((MiLabel )button).getLabel().setSize(defaultToolItemSize);
			}
		return(button);
		}
					/**------------------------------------------------------
	 				 * Inserts a pushButton with the given image to this toolbar,
					 * assigning it the given tool hint message and having it
					 * call the given commandHandler with the given command when
					 * the button is pressed by the user.
					 * @param toolHint		the toolHint
					 * @param image			the appearance
					 * @param commandHandler	the command handler to call
					 * @param command		the command to send
					 * @param index			the index of the isertion point
					 * @return			the newly created tool item
					 *------------------------------------------------------*/
	public		MiWidget	insertToolItem(
						String toolHint, 
						MiPart image,
						MiiCommandHandler commandHandler, 
						String command,
						int index)
		{
		MiPushButton button = new MiPushButton(image);
		button.setInsetMargins(1);
		button.setDisplaysFocusBorder(false);
		button.appendCommandHandler(commandHandler, command);
		button.setToolHintMessage(toolHint);
		button.setAcceptingEnterKeyFocus(false);
		insertPart(button, index);
		commandManager.registerCommandDependentWidget(button, command);
		toolItemCommandNames.put(command, button);
		toolItemCommands.addElement(commandHandler);
		applyButtonStyle(button);
		if ((button instanceof MiLabel) && (((MiLabel )button).getLabel() instanceof MiImage))
			{
			if (defaultToolItemSize != null)
				((MiLabel )button).getLabel().setSize(defaultToolItemSize);
			}
		return(button);
		}
					/**------------------------------------------------------
	 				 * Appends the given widget to this toolbar, assigning it
					 * the given tool hint message and having it call the 
					 * given commandHandler with the given command when the
					 * button is pressed by the user.
					 * @param toolHint		the toolHint
					 * @param tool			the tool item
					 * @param commandHandler	the command handler to call
					 * @param command		the command to send
					 * @return			the newly created tool item
					 *------------------------------------------------------*/
	public		MiWidget	appendToolItem(
						String toolHint, 
						MiWidget tool,
						MiiCommandHandler commandHandler,
						String command)
		{
		// Use push button here to unify the potentially seperate parts 
		// in the given tool into 1 GUI widget look and feel
		MiPushButton button = new MiPushButton(tool);
		button.setInsetMargins(1);
		button.setDisplaysFocusBorder(false);
		button.setToolHintMessage(toolHint);
		button.setAcceptingEnterKeyFocus(false);
		appendPart(button);
		commandManager.registerCommandDependentWidget(tool, command);
		toolItemCommandNames.put(command, tool);
		toolItemCommandNames.put(command, button);
		toolItemCommands.addElement(commandHandler);
		applyButtonStyle(button);
		if ((button instanceof MiLabel) && (((MiLabel )button).getLabel() instanceof MiImage))
			{
			if (defaultToolItemSize != null)
				((MiLabel )button).getLabel().setSize(defaultToolItemSize);
			}
		return(button);
		}
					/**------------------------------------------------------
	 				 * Appends a toggleButton with the given image to this toolbar,
					 * assigning it the given tool hint message and having it
					 * call the given commandHandler with the given commands when
					 * the button is toggled on and off by the user.
					 * @param toolHint		the toolHint
					 * @param image			the appearance
					 * @param commandHandler	the command handler to call
					 * @param onCommand		the command to send when
					 *				'set'
					 * @param offCommand		the command to send when 
					 *				not 'set'
					 * @return			the newly created tool item
					 *------------------------------------------------------*/
	public		MiWidget	appendBooleanToolItem(
						String toolHint, 
						MiPart image,
						MiiCommandHandler commandHandler, 
						String onCommand,
						String offCommand)
		{
		MiToggleButton button = new MiToggleButton(image);
		if (radioEnforcer != null)
			button.setRadioStateEnforcer(radioEnforcer);

		button.setInsetMargins(1);
		button.setDisplaysFocusBorder(false);
		button.appendCommandHandler(commandHandler, onCommand, MiiActionTypes.Mi_SELECTED_ACTION);
		button.appendCommandHandler(commandHandler, offCommand, MiiActionTypes.Mi_DESELECTED_ACTION);
		commandManager.registerCommandDependentWidget(button, onCommand);
		button.setToolHintMessage(toolHint);
		button.setAcceptingEnterKeyFocus(false);
		appendPart(button);
		toolItemCommandNames.put(onCommand, button);
		toolItemCommandNames.put(offCommand, button);
		toolItemCommands.addElement(commandHandler);
		applyButtonStyle(button);
		if ((button instanceof MiLabel) && (((MiLabel )button).getLabel() instanceof MiImage))
			{
			if (defaultToolItemSize != null)
				((MiLabel )button).getLabel().setSize(defaultToolItemSize);
			}
		return(button);
		}
					/**------------------------------------------------------
	 				 * Sets the size of the tool items in the toolbar. Only sets
					 * the size of the images in widgets that are instanceof MiLabel.
					 * Assumes that the images are already loaded, otherwise the
					 * size of the images will be changed by the image loader.
					 * @param size		the new size of the tool items
					 *------------------------------------------------------*/
	public		void		setToolItemImageSizes(MiSize size)
		{
		if (size == null)
			{
			defaultToolItemSize = null;
			return;
			}
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiPart part = getPart(i);
			if ((part instanceof MiLabel) && (((MiLabel )part).getLabel() instanceof MiImage))
				{
				((MiLabel )part).getLabel().setSize(size);
				}
			}
		defaultToolItemSize = new MiSize(size);
		}
					/**------------------------------------------------------
	 				 * Sets the style of the tool item buttons. The style is
					 * a bit-masked integer that specifies a number of things
					 * aboyt the tool items appearance. Valid bits are:
					 *    Mi_LABLED_TOOLBAR_BUTTONS
					 *    Mi_BORDERED_TOOLBAR_BUTTONS
					 *    Mi_FOCUS_BORDER_TOOLBAR_BUTTONS
					 *    Mi_FOCUS_SPECIAL_FOCUS_BORDER_TOOLBAR_BUTTONS
					 * @param style		the new style of the tool items
					 *------------------------------------------------------*/
	public		void		setButtonStyle(int style)
		{
		// ---------------------------------------------------------------
		// Update the states of the options in the toolbar's background 
		// popup menu
		// ---------------------------------------------------------------
		if ((style & Mi_LABLED_TOOLBAR_BUTTONS) != 0)
			commandManager.setCommandState(Mi_SHOW_LABELS_COMMAND_NAME, true);
		else
			commandManager.setCommandState(Mi_SHOW_LABELS_COMMAND_NAME, false);

		if ((style & Mi_BORDERED_TOOLBAR_BUTTONS) != 0)
			commandManager.setCommandState(Mi_SHOW_BORDERS_COMMAND_NAME, true);
		else
			commandManager.setCommandState(Mi_SHOW_BORDERS_COMMAND_NAME, false);

		if (style == currentButtonStyle)
			{
			return;
			}

		currentButtonStyle = style;

		// ---------------------------------------------------------------
		// For each MiButton in the toolbar, update it's style.
		// ---------------------------------------------------------------
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiPart part = getPart(i);
			if (part instanceof MiButton)
				applyButtonStyle((MiButton )part);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the style of the tool item buttons. 
					 * @return 		the style of the tool items
					 * @see 		#setButtonStyle
					 *------------------------------------------------------*/
	public		int		getButtonStyle()
		{
		return(currentButtonStyle);
		}
					/**------------------------------------------------------
	 				 * Gets the tool item that generates the given command. 
					 * @param command 	the command
					 * @return 		the tool item
					 *------------------------------------------------------*/
	public		MiWidget	getToolItemWithCommand(String command)
		{
		return((MiWidget )toolItemCommandNames.get(command));
		}
					/**------------------------------------------------------
	 				 * Gets the tool item that generates the given command. 
					 * @param command 	the command
					 * @return 		the tool item
					 *------------------------------------------------------*/
	public		int		getIndexOfToolItemWithCommand(String command)
		{
		return(getIndexOfPart((MiWidget )toolItemCommandNames.get(command)));
		}
					/**------------------------------------------------------
	 				 * Gets the command of the given tool item. 
					 * @param toolItem 	the tool item
					 * @return 	 	the command
					 *------------------------------------------------------*/
	public		String		getCommandOfToolItem(MiWidget toolItem)
		{
		for (Enumeration e = toolItemCommandNames.keys(); e.hasMoreElements();)
			{
			String key = (String)e.nextElement();
			if (toolItemCommandNames.get(key) == toolItem)
				return(key);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets whether the given item is a 'spacer'.
					 * @param toolItem	an item in this toolbar
					 * @return		true if a spacer
					 *------------------------------------------------------*/
	public		boolean		isSpacer(MiPart toolItem)
		{
		return((toolItem instanceof MiRectangle)
			&& (toolItem.getColor() == Mi_TRANSPARENT_COLOR));
		}
					/**------------------------------------------------------
	 				 * Removes all tool items that generate the given command.
					 * @param command 	the command generated by the toolbar
					 *			item that we will remove
					 *------------------------------------------------------*/
	public		void		removeToolItem(String command)
		{
		for (int j = 0; j < getNumberOfParts(); ++j)
			{
			MiPart part = getPart(j);
			for (int i = 0; i < part.getNumberOfActionHandlers(); ++i)
				{
				MiiAction h = part.getActionHandler(i);
				if (h instanceof MiActionCallback) 
					{
					String cmd = ((MiActionCallback )h).getCommand();
					if ((cmd != null) && (command.equals(cmd)))
						{
						removePart(part);
						toolItemCommandNames.remove(command);
						--j;
						break;
						}
					}
				}
			}
		}
					/**------------------------------------------------------
	 				 * Removes all tool items that do not generate one of the 
					 * given commands.
					 * @param commandsToKeep the command generated by the toolbar
					 *			that we will keep
					 *------------------------------------------------------*/
	public		void		removeAllToolItemsWithCommandsExcept(Strings commandsToKeep)
		{
		for (int j = 0; j < getNumberOfParts(); ++j)
			{
			MiPart part = getPart(j);
			if (isSpacer(part))
				{
				removePart(part);
				--j;
				}
			else
				{
				for (int i = 0; i < part.getNumberOfActionHandlers(); ++i)
					{
					MiiAction h = part.getActionHandler(i);
					if (h instanceof MiActionCallback) 
						{
						String command = ((MiActionCallback )h).getCommand();
						if ((command != null) && (!commandsToKeep.contains(command)))
							{
							removePart(part);
							toolItemCommandNames.remove(command);
							--j;
							break;
							}
						}
					}
				}
			}
		}
					/**------------------------------------------------------
	 				 * Sets the the status bar messages (for both the sensitive
					 * and insensitive cases), and the balloon help message
					 * for the tool item that generates the given command.
					 * @param command  	the command
					 * @param availableStatusHelpMsg 
					 *			the status bar message for the tool
					 *			item when it is sensitive
					 * @param unAvailableStatusHelpMsg 
					 *			the status bar message for the tool
					 *			item when it is insensitive
					 * @param balloonHelpMsg the balloon help message 
					 *------------------------------------------------------*/
	public		void		setHelpMessages(String command,
							String availableStatusHelpMsg,
							String unAvailableStatusHelpMsg,
							String balloonHelpMsg)
		{
		MiWidget toolItem = getToolItemWithCommand(command);
		toolItem.setNormalStatusHelpMessage(availableStatusHelpMsg);
		toolItem.setInSensitiveStatusHelpMessage(unAvailableStatusHelpMsg);
		toolItem.setBalloonHelpMessage(balloonHelpMsg);
		}
					/**------------------------------------------------------
	 				 * Sets the editor to be targeted by the functionality
					 * of the tool items in this toolbar.
					 * @param editor	the editor to target
					 *------------------------------------------------------*/
	public 		void		setEditor(MiEditor editor)
		{
		for (int i = 0; i < toolItemCommands.size(); ++i)
			{
			Object handler = toolItemCommands.elementAt(i);
			if (handler instanceof MiCommandHandler)
				((MiCommandHandler )handler).setTargetOfCommand(editor);
			}
		this.editor = editor;
		}
					/**------------------------------------------------------
	 				 * Gets the editor to be targeted by the functionality
					 * of the tool items in this toolbar.
					 * @return 		the editor to target
					 *------------------------------------------------------*/
	public 		MiEditor	getEditor()
		{
		return(editor);
		}
					/**------------------------------------------------------
	 				 * Sets this toolbars initial appearance and drag and drop
					 * behavior.
					 * @param editor	the target editor
					 *------------------------------------------------------*/
	protected	void		setup(MiEditor editor)
		{
		this.editor = editor;

		// ---------------------------------------------------------------
		// Make this toolbar horizontal initially
		// ---------------------------------------------------------------
		orientation = Mi_HORIZONTAL;

		// ---------------------------------------------------------------
		// Make this toolbar size not depend on the size of the icons in the
		// toolbar.
		// ---------------------------------------------------------------
		setVisibleContainerAutomaticLayoutEnabled(false);
		setAlleySpacing(0);
		setBorderLook(Mi_GROOVE_BORDER_LOOK);

		// ---------------------------------------------------------------
		// Make the icons be added from the left to the right.
		// ---------------------------------------------------------------
		MiLayout layout = new MiRowLayout();
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		layout.setElementVSizing(Mi_NONE);
		setLayout(layout);

		// ---------------------------------------------------------------
		// Add the background menu that allows the user to customize the
		// appearance of this toolbar
		// ---------------------------------------------------------------
		setContextMenu(new MiToolbarBackgroundMenu(this, commandManager));

		setIsDragAndDropSource(true);

		// ---------------------------------------------------------------
		// Make this toolbar drag and droppable so the user can move it and
		// have it dock at any side of the window (see MiEditorWindow).
		// ---------------------------------------------------------------
		MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));
		dndBehavior.setDragsReferenceNotCopy(true);
		dndBehavior.setIsDefaultBehaviorForParts(false);
		dndBehavior.setSnapLookCenterToCursor(false);
		dndBehavior.setKeepLookCompletelyWithinRootWindow(false);
		setDragAndDropBehavior(dndBehavior);

		// ---------------------------------------------------------------
		// Initialize the button appearance.
		// ---------------------------------------------------------------
		setButtonStyle(currentButtonStyle);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
					/**------------------------------------------------------
	 				 * Applies the current button style to the given button.
					 * @param button 	the button to apply style to
					 *------------------------------------------------------*/
	protected	void		applyButtonStyle(MiWidget button)
		{
		if ((currentButtonStyle & Mi_LABLED_TOOLBAR_BUTTONS) == 0)
			{
			if (button.getNumberOfParts() > 1)
				{
				commandManager.unRegisterWidgetGeneratedCommand(
					button.getPart(1), null); 
				button.removePart(1);
				button.setLayout(null);
				}
			setAlleySpacing(0);
			button.setMargins(new MiMargins(0));
			button.setInsetMargins(buttonInsetMargins);
			button.getToolHintHelp(null).setEnabled(true);
			commandManager.setCommandState(Mi_SHOW_LABELS_COMMAND_NAME, false);
			}
		else if (button.getNumberOfParts() == 1)
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.setElementHSizing(Mi_NONE);
			button.setLayout(columnLayout);
			MiText label = new MiText(button.getToolHintHelp(null).getMessage());
			button.appendPart(label);
			commandManager.registerCommandDependentWidget(
				label, getCommandOfToolItem(button));
			if (!button.isSensitive())
				label.setSensitive(false);
			setAlleyHSpacing(4);
			button.setInsetMargins(4);
			button.setMargins(new MiMargins(0));
			button.getToolHintHelp(null).setEnabled(false);
			commandManager.setCommandState(Mi_SHOW_LABELS_COMMAND_NAME, true);
			}
		if ((currentButtonStyle & Mi_BORDERED_TOOLBAR_BUTTONS) != 0)
			button.setBorderLook(currentButtonStyle & Mi_BORDER_LOOK_MASK);
		else
			button.setBorderLook(Mi_NONE);

		if ((currentButtonStyle & Mi_FOCUS_BORDER_TOOLBAR_BUTTONS) != 0)
			{
			int borderLook = currentButtonStyle & Mi_BORDER_LOOK_MASK;
			button.setMouseFocusBorderLook(borderLook != 0 ? Mi_NONE : Mi_RAISED_BORDER_LOOK);
			// ?? button.setKeyboardFocusBorderLook(currentButtonStyle & Mi_BORDER_LOOK_MASK);

			boolean flag = (borderLook != 0);

			button.setMouseFocusAttributes(
				button.getMouseFocusAttributes().setHasBorderHilite(flag));
			button.setKeyboardFocusAttributes(
				button.getKeyboardFocusAttributes().setHasBorderHilite(flag));
			button.setEnterKeyFocusAttributes(
				button.getEnterKeyFocusAttributes().setHasBorderHilite(flag));
//			button.setSelectedAttributes(
//				button.getSelectedAttributes().setHasBorderHilite(flag));

			}
		else
			{
			button.setMouseFocusAttributes((MiAttributes )
				button.getMouseFocusAttributes().initializeAsOverrideAttributes(false));
			button.setKeyboardFocusAttributes((MiAttributes )
				button.getKeyboardFocusAttributes().initializeAsOverrideAttributes(false));
/*
			int borderLook = currentButtonStyle & Mi_BORDER_LOOK_MASK;
			button.setMouseFocusBorderLook(borderLook);
			button.setKeyboardFocusBorderLook(borderLook);
			button.setMouseFocusAttributes(
				button.getMouseFocusAttributes().setBorderLook(
					button.getSelectedAttributes().getBorderLook()));
*/
			button.setMouseFocusAttributes(
				button.getMouseFocusAttributes().setHasBorderHilite(false));
			button.setKeyboardFocusAttributes(
				button.getKeyboardFocusAttributes().setHasBorderHilite(false));
			button.setEnterKeyFocusAttributes(
				button.getEnterKeyFocusAttributes().setHasBorderHilite(false));
			button.setSelectedAttributes(
				button.getSelectedAttributes().setHasBorderHilite(false));
			}
		if ((currentButtonStyle & Mi_FOCUS_SPECIAL_FOCUS_BORDER_TOOLBAR_BUTTONS) != 0)
			{
			button.setMouseFocusBorderLook(specialFocusBorderLookStyle);

			boolean flag = true;

			//button.setMouseFocusAttributes(
				//button.getMouseFocusAttributes().setHasBorderHilite(flag));
			button.setKeyboardFocusAttributes(
				button.getKeyboardFocusAttributes().setHasBorderHilite(flag));
			button.setEnterKeyFocusAttributes(
				button.getEnterKeyFocusAttributes().setHasBorderHilite(flag));
			}
		else
			{
			button.setMouseFocusAttributes((MiAttributes )
				button.getMouseFocusAttributes().initializeAsOverrideAttributes(false));
			button.setKeyboardFocusAttributes((MiAttributes )
				button.getKeyboardFocusAttributes().initializeAsOverrideAttributes(false));

			button.setMouseFocusAttributes(
				button.getMouseFocusAttributes().setHasBorderHilite(false));
			button.setKeyboardFocusAttributes(
				button.getKeyboardFocusAttributes().setHasBorderHilite(false));
			button.setEnterKeyFocusAttributes(
				button.getEnterKeyFocusAttributes().setHasBorderHilite(false));
			button.setSelectedAttributes(
				button.getSelectedAttributes().setHasBorderHilite(false));
			}
		if (selectedButtonBGColor != null)
			{
			button.setSelectedBackgroundColor(MiColorManager.getColor(selectedButtonBGColor));
			}
		}
					/**------------------------------------------------------
					 * Process the given command. These commands are usually
					 * generated by the background menu of this toolbar and
					 * support the setting of the various button styles. Valid
					 * commands are:
					 *     Mi_SHOW_LABELS_COMMAND_NAME
					 *     Mi_HIDE_LABELS_COMMAND_NAME
					 *     Mi_SHOW_BORDERS_COMMAND_NAME
					 *     Mi_HIDE_BORDERS_COMMAND_NAME
					 * @param command	the command to process
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		if (command.equals(Mi_SHOW_LABELS_COMMAND_NAME))
			{
			if ((getButtonStyle() & Mi_LABLED_TOOLBAR_BUTTONS) == 0)
				{
				setButtonStyle(getButtonStyle() + Mi_LABLED_TOOLBAR_BUTTONS);
				}
			}
		else if (command.equals(Mi_HIDE_LABELS_COMMAND_NAME))
			{
			if ((getButtonStyle() & Mi_LABLED_TOOLBAR_BUTTONS) != 0)
				{
				setButtonStyle(getButtonStyle() & ~Mi_LABLED_TOOLBAR_BUTTONS);
				}
			}
		else if (command.equals(Mi_SHOW_BORDERS_COMMAND_NAME))
			{
			if ((getButtonStyle() & Mi_BORDERED_TOOLBAR_BUTTONS) == 0)
				{
				setButtonStyle(getButtonStyle() + Mi_BORDERED_TOOLBAR_BUTTONS);
				}
			}
		else if (command.equals(Mi_HIDE_BORDERS_COMMAND_NAME))
			{
			if ((getButtonStyle() & Mi_BORDERED_TOOLBAR_BUTTONS) != 0)
				{
				setButtonStyle(getButtonStyle() & ~Mi_BORDERED_TOOLBAR_BUTTONS);
				}
			}
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @param source	the part to copy
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);
		commandManager		= ((MiToolBar )source).commandManager;
		}

					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_SHOW_BORDERS_COMMAND_NAME))
			{
			boolean show = Utility.toBoolean(value);
			if (show)
				{
				if ((getButtonStyle() & Mi_BORDERED_TOOLBAR_BUTTONS) == 0)
					{
					setButtonStyle(getButtonStyle() + Mi_BORDERED_TOOLBAR_BUTTONS);
					}
				}
			else
				{
				if ((getButtonStyle() & Mi_BORDERED_TOOLBAR_BUTTONS) != 0)
					{
					setButtonStyle(getButtonStyle() & ~Mi_BORDERED_TOOLBAR_BUTTONS);
					}
				}
			}
		else if (name.equalsIgnoreCase(Mi_SHOW_LABELS_COMMAND_NAME))
			{
			boolean show = Utility.toBoolean(value);
			if (show)
				{
				if ((getButtonStyle() & Mi_LABLED_TOOLBAR_BUTTONS) == 0)
					{
					setButtonStyle(getButtonStyle() + Mi_LABLED_TOOLBAR_BUTTONS);
					}
				}
			else
				{
				if ((getButtonStyle() & Mi_LABLED_TOOLBAR_BUTTONS) != 0)
					{
					setButtonStyle(getButtonStyle() & ~Mi_LABLED_TOOLBAR_BUTTONS);
					}
				}
			}
		else if (name.equalsIgnoreCase(Mi_FOCUS_BORDERS_COMMAND_NAME))
			{
			if ((!"false".equalsIgnoreCase(value)) && (!"true".equalsIgnoreCase(value)))
				{
				specialFocusBorderLookStyle = MiSystem.getValueOfAttributeValueName(value);
				setButtonStyle(getButtonStyle() + Mi_FOCUS_SPECIAL_FOCUS_BORDER_TOOLBAR_BUTTONS);
				}
			else
				{
				boolean show = Utility.toBoolean(value);
				if (show)
					{
					if ((getButtonStyle() & Mi_FOCUS_BORDER_TOOLBAR_BUTTONS) == 0)
						{
						setButtonStyle(getButtonStyle() + Mi_FOCUS_BORDER_TOOLBAR_BUTTONS);
						}
					}
				else
					{
					if ((getButtonStyle() & Mi_FOCUS_BORDER_TOOLBAR_BUTTONS) != 0)
						{
						setButtonStyle(getButtonStyle() & ~Mi_FOCUS_BORDER_TOOLBAR_BUTTONS);
						}
					}
				}
			}
		else if (name.equalsIgnoreCase(Mi_BUTTON_SELECTED_BACKGROUND_COLOR))
			{
			selectedButtonBGColor = value;
			}
		else
			{
			super.setPropertyValue(name, value);
			}
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		if (name.equalsIgnoreCase(Mi_SHOW_BORDERS_COMMAND_NAME))
			{
			return("" + ((currentButtonStyle & Mi_BORDERED_TOOLBAR_BUTTONS) != 0));
			}
		else if (name.equalsIgnoreCase(Mi_SHOW_LABELS_COMMAND_NAME))
			{
			return("" +  ((currentButtonStyle & Mi_LABLED_TOOLBAR_BUTTONS) != 0));
			}
		else if (name.equalsIgnoreCase(Mi_FOCUS_BORDERS_COMMAND_NAME))
			{
			return("" +  ((currentButtonStyle & Mi_FOCUS_BORDER_TOOLBAR_BUTTONS) != 0));
			}
		else
			{
			return(super.getPropertyValue(name));
			}
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			{
			return(propertyDescriptions);
			}

		propertyDescriptions = new MiPropertyDescriptions(getClass().getName());

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SHOW_BORDERS_COMMAND_NAME, Mi_BOOLEAN_TYPE, "false"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SHOW_LABELS_COMMAND_NAME, Mi_BOOLEAN_TYPE, "false"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FOCUS_BORDERS_COMMAND_NAME, Mi_BOOLEAN_TYPE, "false"));

		return(propertyDescriptions);
		}


	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Framework
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 *----------------------------------------------------------------------------------------------*/
class MiToolbarBackgroundMenu extends MiEditorMenu implements MiiCommandNames, MiiMessages
	{
					/**------------------------------------------------------
	 				 * Constructs a new MiToolbarBackgroundMenu. 
					 * @param commandHandler where we are to send the commands
					 *			 this menu generates
					 *------------------------------------------------------*/
	public				MiToolbarBackgroundMenu(
						MiiCommandHandler commandHandler,
						MiiCommandManager commandManager)
		{
		super("Toolbar options", commandManager);
		appendBooleanMenuItem(	"Labels", 		commandHandler, 
								Mi_SHOW_LABELS_COMMAND_NAME,
								Mi_HIDE_LABELS_COMMAND_NAME);
		appendBooleanMenuItem(	"Borders", 		commandHandler,
								MiToolBar.Mi_SHOW_BORDERS_COMMAND_NAME,
								MiToolBar.Mi_HIDE_BORDERS_COMMAND_NAME);
		appendMenuItem(		"&Properties...", 	commandHandler,
								Mi_DISPLAY_PROPERTIES_COMMAND_NAME);

/*

		setHelpMessages(Mi_CUT_COMMAND_NAME,
				Mi_CUT_STATUS_HELP_MSG,
				Mi_NO_CUT_STATUS_HELP_MSG,
				Mi_CUT_BALLOON_HELP_MSG);

		setHelpMessages(Mi_COPY_COMMAND_NAME,
				Mi_COPY_STATUS_HELP_MSG,
				Mi_NO_COPY_STATUS_HELP_MSG,
				Mi_COPY_BALLOON_HELP_MSG);
*/
		}
	}


