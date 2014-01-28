
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
public class MiFontOptionMenu extends MiAttributeOptionMenu implements MiiActionHandler, MiiAttributeTypes
	{
	private		String[] 	listOfFonts;
	private		boolean 	displayFontNamesInTheirFont = false; // true takes ~8 seconds on a 3Ghz Intel Windows box




	public				MiFontOptionMenu()
		{
		this(true, true, true);
		}
	public				MiFontOptionMenu(
						boolean displayAttributeIcon,
						boolean displayAttributeValueName,
						boolean displayMenuLauncherButton)
		{
		listOfFonts = MiFontManager.getFontList();
		build(displayAttributeIcon, displayAttributeValueName, displayMenuLauncherButton);
		getAttributeDisplayField().setVisible(false);
		setInsetMargins(1);
		getAttributeValueNameField().setInsetMargins(0);
		setValue(getFont().getName());
		}
	public		void		setDisplayFontNamesInTheirFont(boolean flag)
		{
		displayFontNamesInTheirFont = flag;
		}
	public		boolean		getDisplayFontNamesInTheirFont()
		{
		return(displayFontNamesInTheirFont);
		}
	public		void		setFontValue(MiFont font)
		{
		setValue(font.getName());
		}
	public		MiFont		getFontValue()
		{
		return(getAttributeDisplayField().getFont());
		}
	public		void		setValue(String value)
		{
		if (Utility.isEmptyOrNull(value))
			return;

		if (Utility.indexOfStringInArray(MiFontManager.getFontList(), value, true) == -1)
			return;

		if (getAttributeDisplayField().getFont().getName().equalsIgnoreCase(value)
			&& (getAttributeValueNameField().getValue() != null)
			&& (getAttributeValueNameField().getValue().equalsIgnoreCase(value)))
			{
			return;
			}

		// Update font of text that displays the font name, but do not let it cause a window repaint
		getAttributeDisplayField().setOutgoingInvalidLayoutNotificationsEnabled(false);
		getAttributeDisplayField().setFont(getAttributeDisplayField().getFont().setName(value));
		getAttributeDisplayField().validateLayout();
		getAttributeDisplayField().setOutgoingInvalidLayoutNotificationsEnabled(true);

		getAttributeValueNameField().setValue(value);
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	public		String		getValue()
		{
		return(getAttributeDisplayField().getFont().getName());
		}
	protected	MiPart		makeAttributeIcon()
		{
		return(null);
		}
	protected	MiPart		makeAttributeDisplayField()
		{
		MiText text = new MiText("Abc");
		return(text);
		}
	protected	MiWidget	makeMenuContents()
		{
		if (listOfFonts.length < 8)
			{
			MiStandardMenu fontMenu = new MiStandardMenu();
			fontMenu.appendActionHandler(this, Mi_ACTIVATED_ACTION);
			for (int i = 0; i < listOfFonts.length; ++i)
				{
				MiText text = new MiText(listOfFonts[i]);
				text.setFont(new MiFont(listOfFonts[i]));
				fontMenu.appendItem(new MiMenuItem(text));
				}
			return(fontMenu);
			}
		else
			{
			MiList list = new MiList();
			MiScrolledBox scrolledBox = new MiScrolledBox(list);
			list.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
			if (displayFontNamesInTheirFont)
				{
				for (int i = 0; i < listOfFonts.length; ++i)
					{
					MiText text = new MiText(listOfFonts[i]);
					text.setFont(new MiFont(listOfFonts[i]));
					list.appendItem(text);
					}
				}
			else
				{
				list.setContents(new Strings(listOfFonts));
				}
			return(scrolledBox);
			}
		}
	protected 	void		cycleAttributeValueForward()
		{
		String fontName = getAttributeDisplayField().getFont().getName();
		int index = Utility.indexOfStringInArray(listOfFonts, fontName, true);
		++index;
		if (index >= listOfFonts.length)
			index = 0;
		setValue(listOfFonts[index]);
		}
	protected 	void		cycleAttributeValueBackward()
		{
		String fontName = getAttributeDisplayField().getFont().getName();
		int index = Utility.indexOfStringInArray(listOfFonts, fontName, true);
		--index;
		if (index < 0)
			index = listOfFonts.length - 1;
		setValue(listOfFonts[index]);
		}
	protected	boolean		updateValueFromPopupMenu(MiiAction action)
		{
		if (action.getActionSystemInfo() instanceof MiMenuItem)
			{
			MiMenuItem menuItem = (MiMenuItem )action.getActionSystemInfo();
			int index = ((MiStandardMenu )getMenuContents()).getIndexOfItem(menuItem);
			setValue(listOfFonts[index]);
			return(true);
			}
		// else if (action.getActionSystemInfo() instanceof MiList)
			{
			MiList list = (MiList )action.getActionSource();
			String font = list.getSelectedItem();
			setValue(font);
			return(true);
			}
		}
		
	}

