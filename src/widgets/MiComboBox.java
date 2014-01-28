
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiComboBox extends MiWidget implements MiiActionHandler
	{
	public static final	String			Mi_COMBOBOX_PROTOTYPE_CLASS_NAME 
							= "Mi_COMBOBOX_PROTOTYPE_CLASS_NAME";
	private static		MiComboBox		prototype;
	private static final 	int DECORATION_WIDTH	= 10;
	private static final	int DECORATION_HEIGHT	= 10;

	private		MiTextField		textField;
	private		MiMenuLauncherButton	menuLauncherButton;
	private		MiMenu			menu;
	private		MiTable			list;
	private		MiScrolledBox		scrolledBox;
	private		boolean			restrictingValuesToThoseInList 	= true;
	private		boolean			restrictionIgnoresCase		= true;
	private		boolean			restrictionWarnsOnly		= true;



	public				MiComboBox()
		{
		this(true);
		}
	public				MiComboBox(boolean textFieldSensitive)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiComboBox");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			// FIX add: setTextFieldSensitive(textFieldSensitive) ...
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}
		
		setBorderLook(Mi_INDENTED_BORDER_LOOK);
		setInsetMargins(1);

		MiPart decoration = makeDecoration(10, 6);
		decoration.setName("icon");
		menuLauncherButton = new MiMenuLauncherButton();
		menuLauncherButton.setLabel(decoration);
		menuLauncherButton.setPopupLocation(this, Mi_LOWER_LEFT_LOCATION);
		menuLauncherButton.addPopupLocation(this, Mi_UPPER_LEFT_LOCATION, Mi_LOWER_LEFT_LOCATION);
		menuLauncherButton.setAcceptingKeyboardFocus(false);
		menuLauncherButton.setAcceptingMouseFocus(false);
		menuLauncherButton.setBackgroundColor(getBackgroundColor());
		menuLauncherButton.setBorderLook(Mi_OUTLINED_RAISED_BORDER_LOOK);
		menuLauncherButton.setInsetMargins(3);
		menuLauncherButton.setContextCursor(Mi_STANDARD_CURSOR);
		menuLauncherButton.setName("button");

		MiList list = new MiList(false);
		list.getTableHeaderAndFooterManager().setResizableColumns(false);
		list.setName("list");
		setList(list);

		MiComboBoxPopperKeyEventHandler popupKeyHandler;
		popupKeyHandler = new MiComboBoxPopperKeyEventHandler();
		popupKeyHandler.setMenuLauncherButton(menuLauncherButton);

		textField = new MiTextField(true);
		textField.setName("field");
		textField.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION | Mi_REQUEST_ACTION_PHASE);
		textField.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		textField.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		textField.appendActionHandler(this, Mi_TEXT_CHANGE_ACTION);
		setBorderLook(Mi_INLINED_INDENTED_BORDER_LOOK);
		setNormalColor(getToolkit().getTextSensitiveColor());
		setInSensitiveColor(getToolkit().getTextInSensitiveColor());
		setNormalBackgroundColor(getToolkit().getTextFieldEditableBGColor());
		setInSensitiveBackgroundColor(getToolkit().getTextFieldInEditableBGColor());

		textField.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		textField.setBorderLook(Mi_NONE);
		textField.setInsetMargins(0);
		textField.setAcceptingMouseFocus(false);
		MiRowLayout layout = new MiRowLayout();
		layout.setAlleyHSpacing(0);
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		layout.setLastElementJustification(Mi_RIGHT_JUSTIFIED);
		layout.setElementVSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);
		appendPart(textField);
		appendPart(menuLauncherButton);

		if (!textFieldSensitive)
			{
			popupKeyHandler.addEventToCommandTranslation(
				MiEventHandler.Mi_EXECUTE_COMMAND_NAME, MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
			}
		textField.insertEventHandler(popupKeyHandler, 0);
		appendEventHandler(popupKeyHandler);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
					/**------------------------------------------------------
	 				 * Creates a new widget from the prototype. This is the
					 * factory pattern implementation for this widget. If the
					 * prototype is null, then the default contructor is used.
					 * @return 		the new widget
					 * @see 		#setPrototype
					 *------------------------------------------------------*/
	public static	MiComboBox	create()
		{
		if (prototype == null)
			return(new MiComboBox());

		return((MiComboBox )prototype.deepCopy());
		}
	public		MiTable		getList()
		{
		return(list);
		}
	public		void		setList(MiTable list)
		{
		if (this.list != null)
			this.list.removeActionHandlers(this);

		this.list = list;
		list.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		list.setMinimumNumberOfVisibleRows(3);
		list.setPreferredNumberOfVisibleRows(7);
		list.setInsetMargins(0);
		MiToolkit.setBackgroundAttributes(list.getContentsBackground(), 
			MiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);
		list.getSelectionManager().setMinimumNumberSelected(1);
		// Do not allow interactive resizing of scrolled list cause not usefull and
		// when menu pops down the TableHeaderAndFooterManager is left grabbing the window
		list.getTableHeaderAndFooterManager().setEnabled(false);

		scrolledBox = new MiScrolledBox(list);
		scrolledBox.setInsetMargins(0);

		if (menu != null)
			menu.removeActionHandlers(this);

		menu = new MiMenu(scrolledBox);
		menu.setInsetMargins(2);
		menu.insertActionHandler(this, Mi_VISIBLE_ACTION, 0);

		menuLauncherButton.setMenu(menu);
		}
	public		MiTextField	getTextField()
		{
		return(textField);
		}
	public		MiMenu		getMenu()
		{
		return(menu);
		}
	public		void		setSensitive(boolean flag)
		{
		super.setSensitive(flag);
		textField.setSensitive(flag);
		menuLauncherButton.setSensitive(flag);
		}
	public		void		setValue(String value)
		{
		if (value != null)
			{
			value = MiSystem.getProperty(value, value);
			}

		if (Utility.isEqualTo(value, textField.getValue()))
			{
			return;
			}

		if (restrictingValuesToThoseInList)
			{
			if ((list.getIndexOfItem(value, restrictionIgnoresCase) == -1)
				&& (list.getNumberOfItems() > 0))
				{
				if (restrictionWarnsOnly)
					{
					textField.setValue(value);
					list.getSelectionManager().deSelectAll();
					}
				else
					{
					throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
						+ ": Rejecting value: \"" + value 
							+ "\" because it is not present in list"
						+ " (see MiComboBox.setRestrictingValuesToThoseInList()).\n"
						+ "Values present in list are: " + getContents());
					}
				}
			}
		textField.setValue(value);
		if (!list.getContents().contains(value))
			list.getSelectionManager().deSelectAll();
		else
			list.setValue(textField.getValue());
		}
	public		String		getValue()
		{
		return(textField.getValue());
		}
	public		void		setContents(Strings contents)
		{
		list.setContents(contents);
		String value = textField.getValue();
		if (restrictingValuesToThoseInList)
			{
			if ((!contents.contains(value)) && (contents.size() > 0))
				{
				textField.setValue(contents.elementAt(0));
				list.setValue(textField.getValue());
				}
			}
		else 
			{
			if (!list.getContents().contains(value))
				list.getSelectionManager().deSelectAll();
			else
				list.setValue(textField.getValue());
			}
		}
	public		Strings		getContents()
		{
		return(list.getContents());
		}
	public		void		setRestrictingValuesToThoseInList(boolean flag)
		{
		restrictingValuesToThoseInList = flag;
		if (flag)
			{
			list.getSelectionManager().setMinimumNumberSelected(1);
			}
		else
			{
			list.getSelectionManager().setMinimumNumberSelected(0);
			}
		}
	public		boolean		isRestrictingValuesToThoseInList()
		{
		return(restrictingValuesToThoseInList);
		}
	public		void		setRestrictionWarnsOnly(boolean flag)
		{
		restrictionWarnsOnly = flag;
		if (flag)
			{
			restrictingValuesToThoseInList = true;
			}
		}
	public		boolean		isRestrictionWarnsOnly()
		{
		return(restrictionWarnsOnly);
		}
	public		void		setRestrictionIgnoresCase(boolean flag)
		{
		restrictionIgnoresCase = flag;
		}
	public		boolean		getRestrictionIgnoresCase()
		{
		return(restrictionIgnoresCase);
		}
					/**------------------------------------------------------
	 				 * Sets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @param p 		the new prototype
					 * @see 		#getPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	void		setPrototype(MiComboBox p)
		{
		prototype = p;
		}
					/**------------------------------------------------------
	 				 * Gets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @return  		the prototype
					 * @see 		#setPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	MiComboBox	getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_COMBOBOX_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_COMBOBOX_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiComboBox )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
		
	public		boolean		processAction(MiiAction action)
		{
//MiDebug.println(this + " proecssAction: " + action);
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			textField.setValue(list.getSelectedItem());
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			menu.popdown();
			}
		else if (action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			if (restrictingValuesToThoseInList)
				{
				String value = textField.getValue();
				if ((list.getIndexOfItem(value) == -1)
					&& (list.getNumberOfItems() > 0))
					{
					if (restrictionWarnsOnly)
						{
						dispatchAction(Mi_INVALID_VALUE_ACTION);
						}
					else
						{
						action.veto();
						dispatchAction(Mi_INVALID_VALUE_ACTION);
						if (MiDebug.debug 
							&& MiDebug.isTracing(null, 
								MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
							{
							MiDebug.println(MiDebug.getMicaClassName(this) 
								+ ": Rejecting loss of keyboard focus because "
								+ "isRestrictingValuesToThoseInList() == true.");
							}
						return(true);
						}
					}
				}
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else if ((action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION))
			|| (action.hasActionType(Mi_ENTER_KEY_ACTION)))
			{
			dispatchAction(Mi_VALUE_CHANGED_ACTION);

			if (!list.getContents().contains(textField.getValue()))
				list.getSelectionManager().deSelectAll();
			else
				list.setValue(textField.getValue());
			}
		else if (action.hasActionType(Mi_TEXT_CHANGE_ACTION))
			{
			dispatchAction(Mi_TEXT_CHANGE_ACTION);
			}
		else if (action.hasActionType(Mi_VISIBLE_ACTION))
			{
			MiSize prefSize = menu.getPreferredSize(new MiSize());
			if (prefSize.getWidth() < getWidth() - getMargins(new MiMargins()).getWidth())
				{
				prefSize.setWidth(getWidth() - getMargins(new MiMargins()).getWidth());
				menu.setPreferredSize(prefSize);
				menu.setSize(prefSize);
				menu.validateLayout();
				}
			}
		return(true);
		}
	public static	MiPart		makeDecoration()
		{
		return(makeDecoration(DECORATION_WIDTH, DECORATION_HEIGHT));
		}
	public static	MiPart		makeDecoration(MiDistance width, MiDistance height)
		{
		MiTriangle decoration = new MiTriangle();
		decoration.setBackgroundColor(MiColorManager.gray);
		decoration.setOrientation(Mi_DOWN);
		decoration.setWidth(width);
		decoration.setHeight(height);
		return(decoration);
		}
	public		MiPart		copy()
		{
		MiComboBox obj = (MiComboBox )super.copy();

		obj.textField				= obj.getTextField();
		obj.menuLauncherButton			= (MiMenuLauncherButton )obj.getPart(1);
		obj.menu				= obj.getMenu();
		obj.scrolledBox				= (MiScrolledBox )
							obj.menuLauncherButton.getMenu().getMenu();
		obj.list				= obj.getList();

		MiiEventHandler handler = obj.getEventHandlerWithClass("MiComboBoxPopperKeyEventHandler");
		((MiComboBoxPopperKeyEventHandler )handler).setMenuLauncherButton(obj.menuLauncherButton);
		handler = obj.textField.getEventHandlerWithClass("MiComboBoxPopperKeyEventHandler");
		((MiComboBoxPopperKeyEventHandler )handler).setMenuLauncherButton(obj.menuLauncherButton);

		return(obj);
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

		MiComboBox obj = (MiComboBox )source;
		restrictingValuesToThoseInList 	= obj.restrictingValuesToThoseInList;
		restrictionIgnoresCase		= obj.restrictionIgnoresCase;
		restrictionWarnsOnly		= obj.restrictionWarnsOnly;
		}
	}

