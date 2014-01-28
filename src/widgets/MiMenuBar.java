
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
public class MiMenuBar extends MiWidget implements MiiBrowsableGrid, MiiActionHandler
	{
	public static final	String		Mi_MENUBAR_PROTOTYPE_CLASS_NAME 
							= "Mi_MENUBAR_PROTOTYPE_CLASS_NAME";
	public static final 	String		HELP_PULLDOWN_NAME = "help";
	private static	MiMenuBar		prototype;
	private		MiMenuLauncherButton	helpMenuLauncher;
	private		MiRowLayout		layout;
	private		MiRadioStateEnforcer	radioEnforcer;
	private		MiMenuBarEventHandler	menuBarEventHandler;


	public				MiMenuBar()
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiMenuBar");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			setBorderLook(Mi_RAISED_BORDER_LOOK);
			}

		setVisibleContainerAutomaticLayoutEnabled(false);

		layout = new MiRowLayout();
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		layout.setAlleyHSpacing(12);
		setLayout(layout);
		radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(0);
		radioEnforcer.setTarget(this);
		menuBarEventHandler = new MiMenuBarEventHandler(this);

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
	public		MiMenu		appendPulldownMenu(String name)
		{
		MiMenu m = new MiMenu();
		appendPulldownMenu(m, name);
		return(m);
		}
	public		void		appendPulldownMenu(MiMenu m, String name)
		{
		MiMenuLauncherButton launcher = new MiMenuLauncherButton(m, name);
		launcher.setDisplaysFocusBorder(false);
		if (helpMenuLauncher != null)
			insertPart(launcher, getNumberOfParts() - 1);
		else
			appendPart(launcher);
		setupMenuLauncher(launcher, name);
		}

	public		void		insertPulldownMenu(MiMenu m, String name, int index)
		{
		MiMenuLauncherButton launcher = new MiMenuLauncherButton(m, name);
		launcher.setDisplaysFocusBorder(false);
		insertPart(launcher, index);
		setupMenuLauncher(launcher, name);
		}

	protected	void		setupMenuLauncher(MiMenuLauncherButton launcher, String name)
		{
		launcher.setAcceptingMouseFocus(false);
		launcher.setAcceptingKeyboardFocus(false);
		launcher.setDisplaysFocusBorder(false);
		launcher.setRadioStateEnforcer(radioEnforcer);
		launcher.setBorderLook(Mi_NONE);
		launcher.setSelectedBorderLook(Mi_RAISED_BORDER_LOOK);
		launcher.setSelectedBackgroundColor(Mi_TRANSPARENT_COLOR);
		launcher.setColor(MiColorManager.transparent);
		if (name.toLowerCase().indexOf(HELP_PULLDOWN_NAME) != -1)
			{
			helpMenuLauncher = launcher;
			layout.setLastElementJustification(Mi_RIGHT_JUSTIFIED);
			}
		launcher.appendActionHandler(
			new MiAction(this, Mi_MENU_POPPED_UP_ACTION, Mi_MENU_POPPED_DOWN_ACTION));
		}

	public		void		removePulldownMenu(String menuName)
		{
		removePulldownMenu(getPulldownMenu(menuName));
		}
	public		void		removePulldownMenu(MiMenu menu)
		{
		MiMenuLauncherButton launcher = getMenuLauncherButton(menu);
		removePart(launcher);
		if (launcher == helpMenuLauncher)
			{
			helpMenuLauncher = null;
			layout.setLastElementJustification(Mi_NONE);
			}
		launcher.removeActionHandlers(this);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_MENU_POPPED_UP_ACTION))
			{
			getContainingWindow().prependGrabEventHandler(menuBarEventHandler);
			}
		else if (action.hasActionType(Mi_MENU_POPPED_DOWN_ACTION))
			{
			getContainingWindow().removeGrabEventHandler(menuBarEventHandler);
			}
		return(true);
		}
	public		MiMenuLauncherButton	getMenuLauncherButton(String name)
		{
		return((MiMenuLauncherButton )getPart(name));
		}
	public		MiMenuLauncherButton	getMenuLauncherButton(MiMenu menu)
		{
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if (((MiMenuLauncherButton )getPart(i)).getMenu() == menu)
				{
				return((MiMenuLauncherButton )getPart(i));
				}
			}
		return(null);
		}
	public		MiMenu		getPulldownMenu(String name)
		{
		MiMenuLauncherButton launcher = (MiMenuLauncherButton )getPart(name);
		if (launcher != null)
			return((MiMenu )launcher.getMenu());
		return(null);
		}
	public		boolean		isBrowsable()
		{
		return(true);
		}
	public		void		browseItem(MiBounds cursor)
		{
		MiPart obj;
		if (((obj = pickObject(cursor)) != null) && (!obj.isSelected()))
			obj.select(true);
		}
	public		void		selectBrowsedItem()
		{
		}
	public		boolean		selectItem(MiBounds cursor)
		{
		MiPart obj;
		if (((obj = pickObject(cursor)) != null) && (!obj.isSelected()))
			obj.select(true);
		return(true);
		}
	public		boolean		toggleSelectItem(MiBounds cursor)
		{
		return(selectItem(cursor));
		}
	public		boolean		activateItem(MiBounds cursor)
		{
		return(false);
		}
	public		void		deBrowseAll()
		{
		}
	public		void		browseVerticalPreviousItem()
		{
		}
	public		void		browseVerticalNextItem()
		{
		}
	public		void		browseHorizontalPreviousItem()
		{
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if (getPart(i).isSelected())
				{
				do	{
					i = ((i == 0) ? getNumberOfParts() - 1 : i - 1);
					} while (!getPart(i).isVisible());
				getPart(i).select(true);
				return;
				}
			}
		}
	public		void		browseHorizontalNextItem()
		{
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if (getPart(i).isSelected())
				{
				do	{
					i = ((i >= getNumberOfParts() - 1) ? 0 : i + 1);
					} while (!getPart(i).isVisible());
				getPart(i).select(true);
				return;
				}
			}
		}
	public		void		browseVerticalHomeItem()
		{
		}
	public		void		browseVerticalEndItem()
		{
		}
	public		void		browseHorizontalHomeItem()
		{
		}
	public		void		browseHorizontalEndItem()
		{
		}
	public		void		deSelectAll()
		{
		}
	public		void		selectAdditionalItem(MiBounds cursor)
		{
		}
	public		void		selectInterveningItems(MiBounds cursor)
		{
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
	public static	void		setPrototype(MiMenuBar p)
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
					 * Mi_MENUBAR_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_MENUBAR_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiMenuBar )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	}

class MiMenuBarEventHandler extends MiEventHandler
	{
	private		MiMenuBar		widget;
	public final static String		BrowseEventName		= "browse";
	public final static String		BrowseNextEventName 	= "browseNext";
	public final static String		BrowsePreviousEventName	= "browsePrev";

	public					MiMenuBarEventHandler(MiMenuBar widget)
		{
		addEventToCommandTranslation(BrowseEventName, MiEvent.Mi_MOUSE_MOTION_EVENT, 0, 0);
		addEventToCommandTranslation(BrowseEventName, MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(BrowseNextEventName, MiEvent.Mi_KEY_EVENT, MiEvent.Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(BrowsePreviousEventName, MiEvent.Mi_KEY_EVENT, MiEvent.Mi_LEFT_ARROW_KEY, 0);
		this.widget = widget;
		}
	public		int			processCommand()
		{
/* FIX: fix this somehow, MiiBrowsableGrid needs a isDesignTime method...?
		if (widget.isDesignTime())
			{
			return(PROPOGATE_EVENT);
			}
*/

		if (isCommand(BrowseEventName))
			{
			if (widget.pick(event.worldMouseFootPrint))
				widget.browseItem(event.worldMouseFootPrint);
			else 
				return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			if (widget.pick(event.worldMouseFootPrint))
				widget.selectItem(event.worldMouseFootPrint);
			else 
				return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(BrowseNextEventName))
			{
			widget.browseHorizontalNextItem();
			}
		else if (isCommand(BrowsePreviousEventName))
			{
			widget.browseHorizontalPreviousItem();
			}
		else
			{
			return(Mi_PROPOGATE_EVENT);
			}
		return(Mi_CONSUME_EVENT);
		}
	public		String		toString()
		{
		return(widget.toString() + "." + super.toString());
		}
	}


