
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
public class MiBorderLookOptionMenu extends MiAttributeOptionMenu implements MiiActionHandler, MiiAttributeTypes
	{
	public				MiBorderLookOptionMenu()
		{
		this(true, true, true);
		}
	public				MiBorderLookOptionMenu(
						boolean displayAttributeIcon,
						boolean displayAttributeValueName,
						boolean displayMenuLauncherButton)
		{
		build(displayAttributeIcon, false, displayMenuLauncherButton);
		setBackgroundColor(MiColorManager.veryDarkWhite);
		}
	public		void		setBorderLookValue(int value)
		{
		setValue(borderLookNames[value]);
		}
	public		int		getBorderLookValue()
		{
		return(getAttributeDisplayField().getBorderLook());
		}
	public		void		setValue(String value)
		{
		int borderLook;
		if ((borderLook = getBorderLookFromName(value)) != -1)
			{
			if (getAttributeDisplayField().getBorderLook() == borderLook)
				{
				return;
				}

			getAttributeDisplayField().setBorderLook(borderLook);
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else
			{
			// ERROR...
			}
		}
	public		String		getValue()
		{
		return(getNameOfBorderLook(getAttributeDisplayField().getBorderLook()));
		}
	public static	int		getBorderLookFromName(String name)
		{
		for (int i = 0; i < borderLookNames.length; ++i)
			{
			if (name.equalsIgnoreCase(borderLookNames[i]))
				return(borderLooks[i]);
			}
		return(-1);
		}
	public static	String		getNameOfBorderLook(int look)
		{
		return(borderLookNames[look]);
		}
	protected	MiPart		makeAttributeIcon()
		{
		return(null);
		}
	protected	MiPart		makeAttributeDisplayField()
		{
		MiRectangle rect = new MiRectangle(0,0,40,10);
		rect.setBorderLook(Mi_FLAT_BORDER_LOOK);
		return(rect);
		}
	protected	MiWidget	makeMenuContents()
		{
		MiStandardMenu borderLookMenu = new MiStandardMenu();
		borderLookMenu.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		for (int i = 0; i < borderLooks.length; ++i)
			{
			MiRectangle rect = new MiRectangle(0, 0, 40, 15);
			rect.setBorderLook(borderLooks[i]);
			borderLookMenu.appendItem(new MiMenuItem(rect));
			}
		return(borderLookMenu);
		}
	protected 	void		cycleAttributeValueForward()
		{
		int borderLook = getAttributeDisplayField().getBorderLook();
		++borderLook;
		if (borderLook >= MiiAttributeTypes.borderLooks.length)
			borderLook = Mi_NONE;
		setBorderLookValue(borderLook);
		}
	protected 	void		cycleAttributeValueBackward()
		{
		int borderLook = getAttributeDisplayField().getBorderLook();
		--borderLook;
		if (borderLook < 0)
			borderLook = MiiAttributeTypes.borderLooks.length - 1;
		setBorderLookValue(borderLook);
		}
	protected	boolean		updateValueFromPopupMenu(MiiAction action)
		{
		MiMenuItem menuItem = (MiMenuItem )action.getActionSystemInfo();
		int index = ((MiStandardMenu )getMenuContents()).getIndexOfItem(menuItem);
		setValue("" + borderLookNames[index]);
		return(true);
		}
	}

