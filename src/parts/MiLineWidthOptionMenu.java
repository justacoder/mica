
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
public class MiLineWidthOptionMenu extends MiAttributeOptionMenu implements MiiActionHandler
	{
	private static final int 	MAX_DISPLAY_LINE_WIDTH	= 12;
	private		int[]		lineWidths 		= {0, 1, 2, 3, 4, 5, 8, 10, 12};
	private		int		maxLineWidth		= 100;
	private		MiDistance	currentLineWidth	= 0;


	public				MiLineWidthOptionMenu()
		{
		this(true, true, true);
		}
	public				MiLineWidthOptionMenu(
						boolean displayAttributeIcon,
						boolean displayAttributeValueName,
						boolean displayMenuLauncherButton)
		{
		build(displayAttributeIcon, displayAttributeValueName, displayMenuLauncherButton);
		getAttributeValueNameField().setNumDisplayedColumns(3);
		getAttributeValueNameField().setInsetMargins(0);
		setValue("0");
		}
	public		void		setLineWidthValue(MiDistance value)
		{
		setValue("" + value);
		}
	public		MiDistance	getLineWidthValue()
		{
		return(Utility.toDouble(getValue()));
		}
	public		void		setValue(String value)
		{
//MiDebug.println(this + " SET VALUE: " + value);
		if (Utility.isEmptyOrNull(value))
			value = "0";
		if (Utility.isDouble(value))
			{
			MiDistance lWidth = Utility.toDouble(value);
			if (lWidth > maxLineWidth)
				{
				lWidth = maxLineWidth;
				value = "" + lWidth;
				}

			if ((getAttributeValueNameField().getValue().equals(value))
				&& (currentLineWidth == lWidth))
				{
				return;
				}

			value = Utility.toShortString(lWidth);

			currentLineWidth = lWidth;

			if (lWidth >= MAX_DISPLAY_LINE_WIDTH)
				lWidth = MAX_DISPLAY_LINE_WIDTH;

			getAttributeDisplayField().setLineWidth(lWidth);
//MiDebug.println(this + " SET Line Width: " + lWidth);

			getAttributeValueNameField().setValue(value);

			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else
			{
			getAttributeValueNameField().setValue(getValue());
			}
		}
	public		String		getValue()
		{
		return(Utility.toShortString(currentLineWidth));
		}
	protected	MiPart		makeAttributeIcon()
		{
		return(null);
		}
	protected	MiPart		makeAttributeDisplayField()
		{
		MiLine line = new MiLine(0,0,40,0);
		line.setInvalidLayoutNotificationsEnabled(false);
		line.setBackgroundColor(MiColorManager.black);
		line.setBorderLook(Mi_NONE);
		return(line);
		}
	protected	MiWidget	makeMenuContents()
		{
		MiStandardMenu lineWidthMenu = new MiStandardMenu();
		lineWidthMenu.setEachRowHasSameHeight(false);
		lineWidthMenu.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		for (int i = 0; i < lineWidths.length; ++i)
			{
			MiLine line = new MiLine(0, 0, 40, 0);
			line.setBackgroundColor(MiColorManager.black);
			line.setLineWidth(lineWidths[i]);
			lineWidthMenu.appendItem(new MiMenuItem(line, lineWidths[i] + " pt"));
			}
		return(lineWidthMenu);
		}
	protected 	void		cycleAttributeValueBackward()
		{
		for (int i = lineWidths.length - 1; i >= 0; --i)
			{
			if (getLineWidthValue() > lineWidths[i])
				{
				setLineWidthValue(lineWidths[i]);
				return;
				}
			}
		setLineWidthValue(lineWidths[lineWidths.length - 1]);
		}
	protected 	void		cycleAttributeValueForward()
		{
		for (int i = 0; i < lineWidths.length; ++i)
			{
			if (getLineWidthValue() < lineWidths[i])
				{
				setLineWidthValue(lineWidths[i]);
				return;
				}
			}
		setLineWidthValue(lineWidths[0]);
		}
	protected	boolean		updateValueFromPopupMenu(MiiAction action)
		{
		MiMenuItem menuItem = (MiMenuItem )action.getActionSystemInfo();
		int index = ((MiStandardMenu )getMenuContents()).getIndexOfItem(menuItem);
		setValue("" + lineWidths[index]);
		return(true);
		}
	}

