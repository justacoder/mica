
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
public class MiIconWithOptionMenu extends MiWidget implements MiiActionHandler
	{
	public static final	String			Mi_ICONWITHOPTIONMENU_PROTOTYPE_CLASS_NAME 
								= "Mi_ICONWITHOPTIONMENU_PROTOTYPE_CLASS_NAME";
	private static		MiIconWithOptionMenu	prototype;
	private			MiOptionMenu		om;
	

	public				MiIconWithOptionMenu()
		{
		this(new MiImage("tmp.gif"));
		}
	public				MiIconWithOptionMenu(MiPart icon)
		{
		this(icon, MiComboBox.makeDecoration(7, 7));
		}
	public				MiIconWithOptionMenu(MiPart icon, MiPart menuPoppingDecoration)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiIconWithOptionMenu");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}

		//setBorderLook(Mi_FLAT_BORDER_LOOK);
		setBorderLook(Mi_NONE);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementVSizing(Mi_NONE);
		rowLayout.setElementVJustification(Mi_BOTTOM_JUSTIFIED);
		rowLayout.setAlleyHSpacing(0);
		setLayout(rowLayout);

		appendPart(icon);
		insertEventHandler(new MiIconWithMenuCycleThruOptionsEventHandler(), 0);

		om = new MiOptionMenu(menuPoppingDecoration);
		om.setIsAutoUpdatingMenuLauncherButton(false);
		om.getMenu().getMenu().appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		om.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		om.setBorderLook(Mi_NONE);
		om.setDisplaysFocusBorder(false);
		om.setMouseFocusAttributes(getMouseFocusAttributes().setHasBorderHilite(false));
		om.setKeyboardFocusAttributes(getMouseFocusAttributes().setHasBorderHilite(false));
		appendPart(om);

		setSelectedBackgroundColor(getNormalBackgroundColor());

		setContents(new Strings());

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
	public static	MiWidget	create()
		{
		if (prototype == null)
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}
	public		MiOptionMenu	getOptionMenu()
		{
		return(om);
		}
	public		void		appendItem(MiMenuItem item)
		{
		om.appendItem(item);
		}

	public		void		setSelectedItem(MiMenuItem item)
		{
		om.setSelectedItem(item);
		}

	public		int		getIndexOfItem(MiMenuItem item)
		{
		return(om.getIndexOfItem(item));
		}
	public		void		setSelectedItem(String item)
		{
		om.setSelectedItem(item);
		}
	public		void		setSelectedItem(int itemIndex)
		{
		om.setSelectedItem(itemIndex);
		}
	
	public		void		setValue(String value)
		{
		om.setValue(value);
		}
	public		String		getValue()
		{
		return(om.getValue());
		}
	public		void		setContents(Strings contents)
		{
		om.setContents(contents);
		}
	public		Strings		getContents()
		{
		return(om.getContents());
		}

	public		void		removeAllItems()
		{
		om.removeAllItems();
		}
	public		void		appendItems(Strings items)
		{
		om.appendItems(items);
		}
	public		void		appendItems(String items)
		{
		om.appendItems(items);
		}
	public		void		appendItem(String item)
		{
		om.appendItem(item);
		}

	public		String		getSelectedItem()
		{
		return(om.getSelectedItem());
		}

	public		int		getSelectedItemIndex()
		{
		return(om.getSelectedItemIndex());
		}

	public		MiMenuItem	getSelectedMenuItem()
		{
		return(om.getSelectedMenuItem());
		}
	public		void		popup()
		{
		om.select(true);
		}
	public		void		cycleToNextOption()
		{
		Strings contents = getContents();
//MiDebug.println(this + "cycleToNextOption:contents= " + contents );
		if ((contents == null) || (contents.size() == 0))
			{
			return;
			}

//MiDebug.println(this + "cycleToNextOption");
		if (contents.size() == 1)
			{
			// Reiterate value
//MiDebug.println(this + "reiterate");
			processAction(new MiAction(this, Mi_VALUE_CHANGED_ACTION));
			return;
			}

		int index = getSelectedItemIndex();
		if (index < contents.size() - 1)
			{
//MiDebug.println("setSelectedItem: " + (index + 1));
			setSelectedItem(index + 1);
			}
		else
			{
//MiDebug.println("setSelectedItem: 0");
			setSelectedItem(0);
			}
		}
	public		void		cycleToPreviousOption()
		{
		Strings contents = getContents();
		if ((contents == null) || (contents.size() == 0))
			{
			return;
			}

		if (contents.size() == 1)
			{
			// Reiterate value
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			return;
			}

		int index = getSelectedItemIndex();
		if (index > 0)
			{
			setSelectedItem(index - 1);
			}
		else
			{
			setSelectedItem(contents.size() - 1);
			}
		}

	public		boolean		processAction(MiiAction action)
		{
//MiDebug.println(this + " process action: " + action);
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			processAction(new MiAction(this, Mi_VALUE_CHANGED_ACTION));
			}
		else if (action.hasActionType(Mi_VALUE_CHANGED_ACTION))
			{
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		return(true);
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
	public static	void		setPrototype(MiIconWithOptionMenu p)
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
		String prototypeClassName = MiSystem.getProperty(Mi_ICONWITHOPTIONMENU_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiIconWithOptionMenu )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	}
class MiIconWithMenuCycleThruOptionsEventHandler extends MiEventHandler
	{
	public static final String	Mi_CYCLE_TO_NEXT_OPTION_COMMAND_NAME = "cycleToNextOption";
	public static final String	Mi_CYCLE_TO_PREVIOUS_OPTION_COMMAND_NAME = "cycleToPreviousOption";
	public static final String	Mi_POPUP_OPTION_MENU_COMMAND_NAME = "popupOptionMenu";

	public				MiIconWithMenuCycleThruOptionsEventHandler()
		{
		addEventToCommandTranslation(
			Mi_CYCLE_TO_NEXT_OPTION_COMMAND_NAME, Mi_LEFT_MOUSE_DOWN_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_CYCLE_TO_PREVIOUS_OPTION_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_POPUP_OPTION_MENU_COMMAND_NAME, Mi_RIGHT_MOUSE_DOWN_EVENT, 0, 0);
		}

	public		int		processCommand()
		{
//MiDebug.println(this + " processCommand: " + getCommand());
		if (isCommand(Mi_CYCLE_TO_NEXT_OPTION_COMMAND_NAME))
			{
//MiDebug.println(this + " cycleToNextOption " );
			((MiIconWithOptionMenu )getTarget()).cycleToNextOption();
			}
		else if (isCommand(Mi_CYCLE_TO_PREVIOUS_OPTION_COMMAND_NAME))
			{
			((MiIconWithOptionMenu )getTarget()).cycleToPreviousOption();
			}
		else 
			{
			((MiIconWithOptionMenu )getTarget()).popup();
			}
		return(Mi_CONSUME_EVENT);
		}
	}

