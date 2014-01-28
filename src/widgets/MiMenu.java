
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiMenu extends MiWidget implements MiiActionHandler, MiiCommandHandler, MiiCommandNames
	{
	private static	MiDistance[]			popupLocationMenuPtAdjustmentsX	= 
		{
		0,	//Mi_CENTER_LOCATION
		-2,	//Mi_LEFT_LOCATION
		2,	//Mi_RIGHT_LOCATION
		0,	//Mi_BOTTOM_LOCATION
		0,	//Mi_TOP_LOCATION
		-2,	//Mi_LOWER_LEFT_LOCATION
		2,	//Mi_LOWER_RIGHT_LOCATION	
		-2,	//Mi_UPPER_LEFT_LOCATION
		2,	//Mi_UPPER_RIGHT_LOCATION	
		};

	private static	MiDistance[]			popupLocationMenuPtAdjustmentsY	= 
		{
		0,	//Mi_CENTER_LOCATION
		0,	//Mi_LEFT_LOCATION
		0,	//Mi_RIGHT_LOCATION
		-2,	//Mi_BOTTOM_LOCATION
		2,	//Mi_TOP_LOCATION
		-2,	//Mi_LOWER_LEFT_LOCATION
		-2,	//Mi_LOWER_RIGHT_LOCATION	
		2,	//Mi_UPPER_LEFT_LOCATION
		2,	//Mi_UPPER_RIGHT_LOCATION	
		};

	private		FastVector			popupLocationSubjectPts	= new FastVector();
	private		IntVector			popupLocationMenuPts	= new IntVector();
	private		MiPart				menu;
	private		MiPart				assocTriggerSubject;
	private		MiWindow			parentWin;
//	private		MiShortCutHandler 		menuEventHandler	= new MiShortCutHandler();
	private		MiShortCutHandler 		accelerators;
	private		boolean				isTearOffMenu;
	private		boolean				inPositionMenuNearPreferredPointMethod;
	private		MiGrabObjectEventHandlers 	menuGrabEventHandlers;




	public				MiMenu()
		{
		this(new MiStandardMenu());
		}
	public				MiMenu(MiPart menu)
		{
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);

		setVisible(false);
		setIsOpaqueRectangle(true);
		setContextCursor(Mi_STANDARD_CURSOR);

		this.menu = menu;
		appendPart(menu);
		appendActionHandler(this, Mi_GEOMETRY_CHANGE_ACTION);
		appendActionHandler(this, Mi_VISIBLE_ACTION + Mi_REQUEST_ACTION_PHASE);
		menu.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		menuGrabEventHandlers = new MiGrabObjectEventHandlers(this);
		menuGrabEventHandlers.setObject(this);

		setBorderLook(Mi_RAISED_BORDER_LOOK);
		MiToolkit.overrideAttributes(this, MiToolkit.Mi_TOOLKIT_MENU_ATTRIBUTES);

		setContainerLayoutSpec(MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS);

		if (menu instanceof MiStandardMenu)
			accelerators = ((MiStandardMenu )menu).getAccelerators();
		// Don't repaint menu when enter with mouse
		setMouseFocusAttributes(getMouseFocusAttributes().setHasBorderHilite(false));
		setVisible(false);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}

	public		MiPart		getMenu()	
		{ 
		return(menu);		
		}

	public		void		setIsTearOffMenu(boolean flag)
		{
		isTearOffMenu = flag;
		}
	public		boolean		isTearOffMenu()
		{
		return(isTearOffMenu);
		}

	public		MiShortCutHandler	getAccelerators()
		{
		return(accelerators);
		}

	public		void		popup(MiPart assocTriggerSubject, MiPoint pt)
		{
		FastVector popupLocationSubjectPts = new FastVector();
		popupLocationSubjectPts.addElement(pt);
		IntVector popupLocationMenuPts = new IntVector();
		popupLocationMenuPts.addElement(Mi_UPPER_LEFT_LOCATION);
		popup(assocTriggerSubject, popupLocationSubjectPts, popupLocationMenuPts);
		}
	public		void		popup(MiPart assocTriggerSubject, 
						FastVector popupLocationSubjectPts, 
						IntVector popupLocationMenuPts)
		{
		// If not empty (empty menus are hard to close and look funny)...
		if (menu.getNumberOfItems() > 0)
			{
			this.assocTriggerSubject = assocTriggerSubject;
			parentWin = assocTriggerSubject.getContainingWindow();
			this.popupLocationSubjectPts = popupLocationSubjectPts;
			this.popupLocationMenuPts = popupLocationMenuPts;
			if (!isVisible())
				{
				if (!hasValidLayout())
					{
					setSize(getPreferredSize(new MiSize()));
					validateLayout();
					}
				parentWin.appendAttachment(this);
				parentWin.prependGrabEventHandler(menuGrabEventHandlers);
				setVisible(true);
				dispatchAction(Mi_MENU_POPPED_UP_ACTION);
				}
			else
				{
				raise();
				}
			}
		}

	public		void		raise()
		{
		parentWin.removeAttachment(this);
		parentWin.appendAttachment(this);
		}
	public		void		popdown()
		{
		if (isVisible())
			{
			parentWin.removeGrabEventHandler(menuGrabEventHandlers);
			setVisible(false);
			// ---------------------------------------------------------------
			// When popdown the menu using the keyboard, the menu does not 'lose
			// mouse focus' and so does not know to deBrowseAll
			// ---------------------------------------------------------------
			dispatchAction(Mi_MENU_POPPED_DOWN_ACTION);
			parentWin.removeAttachment(this);
// 7-12-2002 on fast machines, the redraw happens immediately, so this locks up the window until another draw event happens (usually popping another window on top, and then popping this board again)..... parentWin.waitUntilRedrawn();
			}
		}
	public		void		processCommand(String arg)
		{
		if (arg.equalsIgnoreCase(Mi_POPDOWN_COMMAND_NAME))
			{
			if (!isTearOffMenu())
				popdown();
			}
		}
	private		void		positionMenuNearPreferredPoint(
						FastVector popupLocationSubjectPts, 
						IntVector popupLocationMenuPts)
		{
		if (inPositionMenuNearPreferredPointMethod)
			return;
		inPositionMenuNearPreferredPointMethod = true;

		MiBounds menuBounds = getBounds();
		MiBounds tmpBounds = new MiBounds();
		MiBounds subjectBounds = new MiBounds();
		MiBounds defaultMenuBounds = new MiBounds();
		MiPoint menuPt = new MiPoint();
		for (int i = 0; i < popupLocationSubjectPts.size(); ++i)
			{
			subjectBounds.setBounds((MiPoint )popupLocationSubjectPts.elementAt(i));

			if (assocTriggerSubject != parentWin)
				{
				MiUtility.getTransformsAlongPath(parentWin, assocTriggerSubject)
					.wtod(subjectBounds, subjectBounds);
				parentWin.getTransform().dtow(subjectBounds, subjectBounds);
				}

			tmpBounds.copy(menuBounds);
			tmpBounds.getLocationOfCommonPoint(popupLocationMenuPts.elementAt(i), menuPt);

			if ((popupLocationMenuPts.elementAt(i) - Mi_MIN_BUILTIN_LOCATION >= 0)
				&& (popupLocationMenuPts.elementAt(i) - Mi_MIN_BUILTIN_LOCATION 
					< popupLocationMenuPtAdjustmentsX.length))
				{
				menuPt.x += popupLocationMenuPtAdjustmentsX[
					popupLocationMenuPts.elementAt(i) - Mi_MIN_BUILTIN_LOCATION];
				menuPt.y += popupLocationMenuPtAdjustmentsY[
					popupLocationMenuPts.elementAt(i) - Mi_MIN_BUILTIN_LOCATION];
				}

			MiDistance tx = subjectBounds.getCenterX() - menuPt.x;
			MiDistance ty = subjectBounds.getCenterY() - menuPt.y;
			tmpBounds.translate(tx, ty);
			if (i == 0)
				defaultMenuBounds.copy(tmpBounds);
			if (parentWin.getInnerBounds().contains(tmpBounds))
				{
				translate(tx, ty);
				inPositionMenuNearPreferredPointMethod = false;
				return;
				}
			}
		defaultMenuBounds.positionInsideContainer(parentWin.getInnerBounds());
		translate(defaultMenuBounds.getXmin() - getXmin(), defaultMenuBounds.getYmin() - getYmin());
		inPositionMenuNearPreferredPointMethod = false;
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			if (!isTearOffMenu())
				popdown();
			}
		else if (action.hasActionType(Mi_VISIBLE_ACTION + Mi_REQUEST_ACTION_PHASE))
			{
			// Position menu before made visible so there isn't a 'flash' of undrawing in
			// some random part of the screen
			positionMenuNearPreferredPoint(popupLocationSubjectPts, popupLocationMenuPts);
			}
		else if (action.hasActionType(Mi_GEOMETRY_CHANGE_ACTION))
			{
			// If the menu changed size, make sure it is still correctly positioned
			if (isVisible())
				positionMenuNearPreferredPoint(popupLocationSubjectPts, popupLocationMenuPts);
			}
		return(true);
		}
	}

