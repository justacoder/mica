
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
public class MiFontPointSizeOptionMenu extends MiAttributeOptionMenu implements MiiActionHandler, MiiAttributeTypes
	{
	private		MiList 		scrolledList;
	private		int		MIN_FONT_POINT_SIZE	= 6;



	public				MiFontPointSizeOptionMenu()
		{
		this(true, true, true);
		}
	public				MiFontPointSizeOptionMenu(
						boolean displayAttributeIcon,
						boolean displayAttributeValueName,
						boolean displayMenuLauncherButton)
		{
		build(displayAttributeIcon, displayAttributeValueName, displayMenuLauncherButton);
		setInsetMargins(1);
		getAttributeValueNameField().setNumDisplayedColumns(3);
		getAttributeValueNameField().setInsetMargins(0);
		setValue(getFont().getPointSize() + "");
		}
	public		void		setFontPointSizeValue(int size)
		{
		setValue(size + "");
		}
	public		int		getFontPointSizeValue()
		{
		return(Utility.toInteger(getValue()));
		}
	public		void		setValue(String value)
		{
		if (Utility.isEmptyOrNull(value))
			return;

		if (!Utility.isInteger(value))
			return;

		int size = Utility.toInteger(value);
		if (size > MiFontManager.MAX_FONT_POINT_SIZE)
			size = MiFontManager.MAX_FONT_POINT_SIZE;
		else if (size < MIN_FONT_POINT_SIZE)
			size = MIN_FONT_POINT_SIZE;

		value = "" + size;
		if ((getAttributeValueNameField().getValue() != null)
			&& (getAttributeValueNameField().getValue().equalsIgnoreCase(value)))
			{
			return;
			}

		getAttributeValueNameField().setValue(value);
		scrolledList.setValue(value);
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	public		String		getValue()
		{
		return(getAttributeValueNameField().getValue());
		}
	protected	MiPart		makeAttributeIcon()
		{
		return(null);
		}
	protected	MiPart		makeAttributeDisplayField()
		{
		return(null);
		}
	protected	MiWidget	makeMenuContents()
		{
		scrolledList = new MiList(false);
		scrolledList.getTableHeaderAndFooterManager().setResizableColumns(false);
		scrolledList.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		scrolledList.setMinimumNumberOfVisibleRows(7);
		scrolledList.setPreferredNumberOfVisibleRows(7);
		scrolledList.setInsetMargins(0);
		scrolledList.getSortManager().setEnabled(false);

		MiScrolledBox scrolledBox = new MiScrolledBox(scrolledList);
		scrolledBox.setInsetMargins(0);

		scrolledBox.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		for (int i = 6; i < 12; ++i)
			{
			MiText text = new MiText("" + i);
			scrolledList.appendItem(text);
			}
		for (int i = 12; i < 22; i += 2)
			{
			MiText text = new MiText("" + i);
			scrolledList.appendItem(text);
			}
		for (int i = 24; i < 72; i += 8)
			{
			MiText text = new MiText("" + i);
			scrolledList.appendItem(text);
			}
		return(scrolledBox);
		}
	public		void		setContents(Strings contents)
		{
		scrolledList.setContents(contents);
		}
	public		Strings		getContents()
		{
		return(scrolledList.getContents());
		}
	protected 	void		cycleAttributeValueForward()
		{
		int size = getFontPointSizeValue();
		if (size >= 12)
			++size;
		++size;

		if (size > MiFontManager.MAX_FONT_POINT_SIZE)
			size = MiFontManager.MAX_FONT_POINT_SIZE;
		else if (size < MIN_FONT_POINT_SIZE)
			size = MIN_FONT_POINT_SIZE;
		setFontPointSizeValue(size);
		}
	protected 	void		cycleAttributeValueBackward()
		{
		int size = getFontPointSizeValue();
		if (size >= 12)
			--size;
		--size;

		if (size > MiFontManager.MAX_FONT_POINT_SIZE)
			size = MiFontManager.MAX_FONT_POINT_SIZE;
		else if (size < MIN_FONT_POINT_SIZE)
			size = MIN_FONT_POINT_SIZE;
		setFontPointSizeValue(size);
		}
	protected	boolean		updateValueFromPopupMenu(MiiAction action)
		{
		int size = Utility.toInteger(scrolledList.getSelectedItem());
		if (size != 0)
			setFontPointSizeValue(size);
		return(true);
		}
	}

