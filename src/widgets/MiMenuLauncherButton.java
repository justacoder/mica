
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
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.FastVector; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiMenuLauncherButton extends MiButton implements MiiActionHandler, MiiCommandHandler
	{
	public static final	String			Mi_MENU_LAUNCHER_BUTTON_PROTOTYPE_CLASS_NAME 
							= "Mi_MENU_LAUNCHER_BUTTON_PROTOTYPE_CLASS_NAME";
	private static	MiMenuLauncherButton		prototype;
	private		MiMenu				menu;
	private		MiMenuLauncherButtonEventHandler sensor;
	private		MiPart				popupLocationSubject	= this;
	private		FastVector			popupLocations		= new FastVector();
	private		IntVector			popupLocationSubjectPts	= new IntVector();
	private		IntVector			popupLocationMenuPts	= new IntVector();
	private		MiGrabObjectEventHandlers 	menuLauncherEventHandlers;
	private		MiShortCutHandler 		menuEventHandler;

	public				MiMenuLauncherButton()
		{
		setupMiMenuLauncherButton();
		}
	public				MiMenuLauncherButton(MiMenu menu)
		{
		setMenu(menu);
		setupMiMenuLauncherButton();
		}
	public				MiMenuLauncherButton(MiMenu menu, String text)
		{
		super(text);
		setName(text);
		setMenu(menu);
		setupMiMenuLauncherButton();
		}
	public				MiMenuLauncherButton(MiMenu menu, MiPart obj)
		{
		super(obj);
		setMenu(menu);
		setupMiMenuLauncherButton();
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
	public		void		setPopupLocation(MiPart subject, int subjectPt)
		{
		popupLocationSubjectPts.removeAllElements();
		popupLocationMenuPts.removeAllElements();
		addPopupLocation(subject, subjectPt, Mi_UPPER_LEFT_LOCATION);
		}
	public		void		addPopupLocation(MiPart subject, int subjectPt, int menuPt)
		{
		popupLocationSubject = subject;
		popupLocationSubjectPts.addElement(subjectPt);
		popupLocationMenuPts.addElement(menuPt);
		}
	protected	void		calcPopupLocations()
		{
		MiBounds subjectBounds = popupLocationSubject.getBounds();
		subjectBounds.subtractMargins(popupLocationSubject.getMargins(new MiMargins()));
		subjectBounds.addMargins(1);
		popupLocations.removeAllElements();
		for (int i = 0; i < popupLocationSubjectPts.size(); ++i)
			{
			popupLocations.addElement(
				subjectBounds.getLocationOfCommonPoint(
					popupLocationSubjectPts.elementAt(i), new MiPoint()));
			}
		}
	public		void		setMenu(MiMenu menu)
		{
		if (this.menu != null)
			this.menu.removeActionHandlers(this);

		this.menu = menu;

		menu.appendActionHandler(this, Mi_MENU_POPPED_UP_ACTION);
		menu.appendActionHandler(this, Mi_MENU_POPPED_DOWN_ACTION);
		if (menu.getAccelerators() != null)
			appendEventHandler(menu.getAccelerators());
		}
	public		MiMenu		getMenu()	
		{ 
		return(menu);		
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
	public static	void		setPrototype(MiMenuLauncherButton p)
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
					 * Mi_MENU_LAUNCHER_BUTTON_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_MENU_LAUNCHER_BUTTON_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiMenuLauncherButton )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	private		void		setupMiMenuLauncherButton()
		{
		setPopupLocation(this, Mi_LOWER_LEFT_LOCATION);

		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiMenuLauncherButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			menuEventHandler 	= (MiShortCutHandler )
							getEventHandlerWithClass("MiShortCutHandler");
			sensor 			= (MiMenuLauncherButtonEventHandler )
							getEventHandlerWithClass("MiMenuLauncherButtonEventHandler");
			menuLauncherEventHandlers = (MiGrabObjectEventHandlers )
							getEventHandlerWithClass("MiGrabObjectEventHandlers");
			menuEventHandler 	= (MiShortCutHandler )
							getEventHandlerWithClass("MiShortCutHandler");
			}
		else
			{
			menuEventHandler = new MiShortCutHandler();
			sensor = new MiMenuLauncherButtonEventHandler();
			appendEventHandler(sensor);
			menuLauncherEventHandlers = new MiGrabObjectEventHandlers(this);
			menuEventHandler.addShortCut(
				new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0), this, 
				Mi_POPDOWN_COMMAND_NAME);
			menuEventHandler.addShortCut(
				new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0), this, 
				Mi_POPDOWN_COMMAND_NAME);
			menuEventHandler.addShortCut(
				new MiEvent(MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0), this, 
				Mi_POPDOWN_COMMAND_NAME);
			}
		refreshLookAndFeel();
		applyCustomLookAndFeel();
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

		MiMenuLauncherButton obj 	= (MiMenuLauncherButton )source;

		popupLocationSubject		= obj.popupLocationSubject;
		setName(obj.getName());
		setMenu((MiMenu )obj.menu.deepCopy());
		popupLocationSubjectPts.removeAllElements();
		popupLocationMenuPts.removeAllElements();
		popupLocationSubjectPts.append(obj.popupLocationSubjectPts);
		popupLocationMenuPts.append(obj.popupLocationMenuPts);
		}


	public		void		select(boolean flag)
		{
		MiEditor parentEd = getContainingEditor();
		// Clicking on a edge of a cascade menu button calls this after the menu closes
		if (parentEd == null)
			{
			return;
			}

		// If empty (empty menus are hard to close and look funny)...
		if ((!isSelected()) && (menu.getItem(0).getNumberOfItems() == 0))
			{
			return;
			}
		if ((flag) && (!isSelected()))
			{
			super.select(true);
			parentEd.prependGrabEventHandler(menuEventHandler);
			parentEd.prependGrabEventHandler(menuLauncherEventHandlers);
			dispatchAction(Mi_MENU_POPPED_UP_ACTION);
			calcPopupLocations();
			menu.popup(popupLocationSubject, popupLocations, popupLocationMenuPts);
			}
		else if ((flag) && (isSelected()))
			{
			menu.raise();
			}
		else if ((!flag) && (isSelected()))
			{
			super.select(false);
			menu.popdown();
			parentEd.removeGrabEventHandler(menuLauncherEventHandlers);
			parentEd.removeGrabEventHandler(menuEventHandler);
			dispatchAction(Mi_MENU_POPPED_DOWN_ACTION);
			}
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_MENU_POPPED_UP_ACTION))
			{
			if (!isSelected())
				select(true);
			}
		else if (action.hasActionType(Mi_MENU_POPPED_DOWN_ACTION))
			{
			if (isSelected())
				select(false);
			}
		return(true);
		}
	public		void		processCommand(String arg)
		{
		if (arg.equalsIgnoreCase(Mi_POPDOWN_COMMAND_NAME))
			{
			//if (!isTearOffMenu())
				select(false);
			}
		else
			{
			super.processCommand(arg);
			}
		}
	public		MiParts		getAssociatedParts(MiParts parts)
		{
		if (menu.getContainer(0) == null)
			 parts.addElement(menu);
		return(super.getAssociatedParts(parts));
		}
	}


class MiMenuLauncherButtonEventHandler extends MiTwoStateWidgetEventHandler
	{
	public			MiMenuLauncherButtonEventHandler()
		{
		super(false, false, true);
		addEventToCommandTranslation(Mi_TOGGLE_COMMAND_NAME, Mi_LEFT_MOUSE_DOWN_EVENT, 0,0);
		addEventToCommandTranslation(Mi_NOOP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0,0);
		addEventToCommandTranslation(Mi_NOOP_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0,0);
		addEventToCommandTranslation(Mi_TOGGLE_COMMAND_NAME, Mi_KEY_PRESS_EVENT, Mi_ENTER_KEY, 0);
		addEventToCommandTranslation(Mi_TOGGLE_COMMAND_NAME, Mi_KEY_PRESS_EVENT, Mi_SPACE_KEY, 0);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_KEY_PRESS_EVENT, Mi_DOWN_ARROW_KEY,0);
		}
	}

