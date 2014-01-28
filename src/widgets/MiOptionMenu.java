
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

// FIX: TO DO: automatically remove current item from list of other items...

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiOptionMenu extends MiMenuLauncherButton implements MiiActionHandler
	{
	public static final	String			Mi_OPTIONMENU_PROTOTYPE_CLASS_NAME 
							= "Mi_OPTIONMENU_PROTOTYPE_CLASS_NAME";
	private static		MiOptionMenu		prototype;
	public static final String	SELECTED_ITEM_INDEX	= "SelectedItemIndex";
	private		MiStandardMenu	menu;
	private		boolean		autoUpdateMenuLauncherButton = false;
	private		MiRowLayout	layout;
	private		int		selectedItemIndex;
	private		MiMenuItem 	selectedItem;
	private		MiPart 		decoration;
	private		MiPart		label;
	private		int		MAX_PREFERRED_WIDTH	= 150;
	

	public				MiOptionMenu()
		{
		this(makeDecoration());
		}
	public				MiOptionMenu(MiPart decoration)
		{
		super(new MiMenu(new MiStandardMenu()));
		menu = (MiStandardMenu )super.getMenu().getMenu();
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiOptionMenu");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}
		setBorderLook(Mi_RAISED_BORDER_LOOK);
		layout = new MiRowLayout();
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		layout.setElementVSizing(Mi_NONE);
		layout.setAlleyHSpacing(8);
		setSelectedBorderLook(Mi_RAISED_BORDER_LOOK);
		setSelectedBackgroundColor(getNormalBackgroundColor());

		setPopupLocation(this, Mi_LOWER_LEFT_LOCATION);
		addPopupLocation(this, Mi_UPPER_LEFT_LOCATION, Mi_LOWER_LEFT_LOCATION);

		this.decoration = decoration;
		appendPart(decoration);

		layout.setLastElementJustification(Mi_RIGHT_JUSTIFIED);
		setLayout(layout);
		setIsAutoUpdatingMenuLauncherButton(true);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public static	MiPart		makeDecoration()
		{
		MiRectangle decoration = new MiRectangle();
		decoration.setBorderLook(Mi_RAISED_BORDER_LOOK);
		// decoration.setBackgroundColor(getBackgroundColor());
		decoration.setWidth(10);
		decoration.setHeight(4);
		return(decoration);
		}

					/**------------------------------------------------------
	 				 * Creates a new widget from the prototype. This is the
					 * factory pattern implementation for this widget. If the
					 * prototype is null, then the default contructor is used.
					 * @return 		the new widget
					 * @see 		#setPrototype
					 *------------------------------------------------------*/
	public static	MiWidget	create()
		{
		if (prototype == null)
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}
	public		void		appendItem(MiMenuItem item)
		{
		if (selectedItem == null)
			{
			selectedItem = item;
			selectedItemIndex = getNumberOfItems();
			}
		item.appendActionHandler(
			new MiAction(this, Mi_SELECTED_ACTION, String.valueOf(menu.getNumberOfItems())));
		menu.appendItem(item);

/****
		MiSize menuPrefSize = getMenu().getPreferredSize(new MiSize());
		MiSize prefSize = getPreferredSize(new MiSize());
		if ((menuPrefSize.getWidth() > prefSize.getWidth() - getMargins(new MiMargins()).getWidth() + 8 + decoration.getWidth())
			&& (prefSize.getWidth() < MAX_PREFERRED_WIDTH))
			{
			prefSize.setWidth(menuPrefSize.getWidth() - getMargins(new MiMargins()).getWidth() + 8 + decoration.getWidth());
			setPreferredSize(prefSize);
			}
*****/
		}

	public		void		setSelectedItem(MiMenuItem item)
		{
		item.select(true);
		selectedItem = item;
		selectedItemIndex = getIndexOfItem(selectedItem);
		updateMenuLanucherButtonLabel();
		}

	public		int		getIndexOfItem(MiMenuItem item)
		{
		return(menu.getIndexOfItem(item));
		}
	public		void		setSelectedItem(String item)
		{
		for (int i = 0; i < menu.getNumberOfItems(); ++i)
			{
			String t = menu.getMenuItem(i).getLabelText();
			if ((t != null) && (t.equals(item)))
				{
				setSelectedItem(menu.getMenuItem(i));
				return;
				}
			}
		MiDebug.printlnError(this + ".setSelectedItem(), item \"" + item + "\" not found in option menu.");
for (int i = 0; i < menu.getNumberOfItems(); ++i)
	{
	String t = menu.getMenuItem(i).getLabelText();
	MiDebug.println("item #: " + i + " = " + t);
	}
MiDebug.printStackTrace();

		}
	public		void		setSelectedItem(int itemIndex)
		{
		setSelectedItem(menu.getMenuItem(itemIndex));
		}
	
	public		void		setValue(String value)
		{
		setSelectedItem(value);
		}
	public		String		getValue()
		{
		return(selectedItem.getLabelText());
		}
	public		void		setContents(Strings contents)
		{
		removeAllItems();
		appendItems(contents);
		}
	public		Strings		getContents()
		{
		Strings contents = new Strings();
		for (int i = 0; i < menu.getNumberOfItems(); ++i)
			{
			MiMenuItem item = menu.getMenuItem(i);
			contents.add(item.getValue());
			}
		return(contents);
		}

	public		void		removeAllItems()
		{
		menu.removeAllItems();
		}
	public		void		appendItems(Strings items)
		{
		for (int i = 0; i < items.size(); ++i)
			appendItem(items.elementAt(i));
		}
	public		void		appendItems(String items)
		{
		appendItems(new Strings(items));
		}
	public		void		appendItem(String item)
		{
		appendItem(new MiMenuItem(item));
		if ((autoUpdateMenuLauncherButton) && (menu.getNumberOfItems() == 1))
			setSelectedItem(menu.getMenuItem(0));
		}

	public		String		getSelectedItem()
		{
		return((selectedItem == null) ? null : selectedItem.getLabelText());
		}

	public		int		getSelectedItemIndex()
		{
		return((selectedItem == null) ? -1 : selectedItemIndex);
		}

	public		MiMenuItem	getSelectedMenuItem()
		{
		return(selectedItem);
		}

	public		void		setLabelObject(MiPart obj)
		{
		label = obj;
		}
	public		void		setIsAutoUpdatingMenuLauncherButton(boolean flag)
		{
		if (flag == autoUpdateMenuLauncherButton)
			return;

		if (flag)
			{
			label = new MiText("default value");
			insertPart(label, 0);
			}
		else if (label != null)
			{
			removePart(0);
			label = null;
			}

		autoUpdateMenuLauncherButton = flag;
		}
	public		boolean		isAutoUpdatingMenuLauncherButton()
		{
		return(autoUpdateMenuLauncherButton);
		}
	public		boolean		processAction(MiiAction action)
		{
//MiDebug.println(this + " processAction: " + action);
		if (action.hasActionType(Mi_SELECTED_ACTION))
			{
			if (action.getActionSource() instanceof MiMenuItem)
				processCommand((String )action.getActionUserInfo());
			}
		return(super.processAction(action));
		}
	public		void		processCommand(String indexOfChosen)
		{
//MiDebug.println(this + " processCommand: " + indexOfChosen);
		if (Utility.isInteger(indexOfChosen))
			{
			if (selectedItemIndex != Integer.parseInt(indexOfChosen))
				{
				selectedItemIndex = Integer.parseInt(indexOfChosen);
				selectedItem = menu.getMenuItem(selectedItemIndex);
				updateMenuLanucherButtonLabel();
////MiDebug.println(this + " dispatchAction: " + Mi_SELECTED_ACTION);
				dispatchAction(Mi_SELECTED_ACTION);
				dispatchAction(Mi_VALUE_CHANGED_ACTION);
				}
			}
		else
			{
			super.processCommand(indexOfChosen);
			}
		}
	protected	void		updateMenuLanucherButtonLabel()
		{
		if (autoUpdateMenuLauncherButton)
			{
			if (label instanceof MiText)
				((MiText )label).setText(selectedItem.getLabelText());
			else if (label instanceof MiWidget)
				((MiWidget )label).setValue(selectedItem.getLabelText());
			// FIX: Someday use references to do above for graphics...
			// ie..layout.setPart(selectedItem.getGraphics(), 0); 
			// in order to display selected item's icon as well as text
			}
		else
			{
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
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
	public static	void		setPrototype(MiOptionMenu p)
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
	public static	MiWidget	getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_OPTIONMENU_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_OPTIONMENU_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiOptionMenu )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	}

