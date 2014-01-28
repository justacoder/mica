
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
import com.swfm.mica.util.IntVector;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiLineEndsOptionMenu extends MiAttributeOptionMenu implements MiiActionHandler, MiiAttributeTypes
	{
	private		boolean			displayLineCombinedStartAndEndStyles;
	private		boolean			displayLineEndStyles;
	private		boolean			displayLineStartStyles;
	private		int			numberOfStyles;
	private		int			lineStartStyle		= Mi_NONE;
	private		int			lineEndStyle		= Mi_NONE;
	private		IntVector		displayedLineStartStyles= new IntVector();
	private		IntVector		displayedLineEndStyles	= new IntVector();




	public				MiLineEndsOptionMenu()
		{
		this(3, true, true, true, true, true, true);
		}
	public				MiLineEndsOptionMenu(boolean lineStartsNotLineEnds)
		{
		this(lineEndStyles.length, lineStartsNotLineEnds, !lineStartsNotLineEnds, 
			false, true, true, true);
		}
	public				MiLineEndsOptionMenu(
						int numberOfStyles, 
						boolean lineStartStyles, 
						boolean lineEndStyles, 
						boolean combinedStartAndEndStyles,
						boolean displayAttributeIcon,
						boolean displayAttributeValueName,
						boolean displayMenuLauncherButton)
		{
		this.numberOfStyles = numberOfStyles;
		displayLineEndStyles = lineEndStyles;
		displayLineStartStyles = lineStartStyles;
		displayLineCombinedStartAndEndStyles = combinedStartAndEndStyles;

		build(displayAttributeIcon, false, displayMenuLauncherButton);
		}
	public		void		setLineStartStyle(int style)
		{
		getAttributeDisplayField().setLineStartStyle(style);
		if (style != lineStartStyle)
			{
			lineStartStyle = style;
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		}
	public		int		getLineStartStyle()
		{
		return(lineStartStyle);
		}
	public		void		setLineEndStyle(int style)
		{
		getAttributeDisplayField().setLineEndStyle(style);
		if (style != lineEndStyle)
			{
			lineEndStyle = style;
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		}
	public		int		getLineEndStyle()
		{
		return(lineEndStyle);
		}
	public		void		setValue(String value)
		{
		int lineEndValue;
		if ((lineEndValue = getLineEndStyleFromName(value)) != -1)
			{
			if (getAttributeDisplayField().getLineEndStyle() == lineEndValue)
				{
				return;
				}

			setLineEndStyle(lineEndValue);
			}
		else
			{
			// ERROR...
			}
		}
	// FIX: this only returns line END style.. what about start style?
	public		String		getValue()
		{
		return(getNameOfLineEndStyle(getAttributeDisplayField().getLineEndStyle()));
		}
	public static	int		getLineEndStyleFromName(String name)
		{
		for (int i = 0; i < lineEndStyleNames.length; ++i)
			{
			if (name.equalsIgnoreCase(lineEndStyleNames[i]))
				return(lineEndStyles[i]);
			}
		return(-1);
		}
	public static	String		getNameOfLineEndStyle(int endStyle)
		{
		return(lineEndStyleNames[endStyle]);
		}
	protected	MiPart		makeAttributeIcon()
		{
		return(null);
		}
	protected	MiPart		makeAttributeDisplayField()
		{
		MiLine line = new MiLine(0,0,40,0);
		line.setBackgroundColor(MiColorManager.black);
		line.setBorderLook(Mi_NONE);
		return(line);
		}
	protected	MiWidget	makeMenuContents()
		{
		MiStandardMenu lineEndsMenu = new MiStandardMenu();
		lineEndsMenu.appendActionHandler(this, Mi_ACTIVATED_ACTION);

		MiLine line = new MiLine(0, 0, 40, 0);
		lineEndsMenu.appendItem(new MiMenuItem(line));

		displayedLineStartStyles.addElement(line.getLineStartStyle());
		displayedLineEndStyles.addElement(line.getLineEndStyle());

		if (displayLineStartStyles)
			{
			for (int i = 0; i < numberOfStyles; ++i)
				{
				line = new MiLine(0, 0, 40, 0);
				line.setLineStartStyle(lineEndStyles[i + 1]);
				displayedLineStartStyles.addElement(line.getLineStartStyle());
				displayedLineEndStyles.addElement(line.getLineEndStyle());
				lineEndsMenu.appendItem(new MiMenuItem(line));
				}
			}
		if (displayLineEndStyles)
			{
			for (int i = 0; i < numberOfStyles; ++i)
				{
				line = new MiLine(0, 0, 40, 0);
				line.setLineEndStyle(lineEndStyles[i + 1]);
				displayedLineStartStyles.addElement(line.getLineStartStyle());
				displayedLineEndStyles.addElement(line.getLineEndStyle());
				lineEndsMenu.appendItem(new MiMenuItem(line));
				}
			}
		if (displayLineCombinedStartAndEndStyles)
			{
			for (int i = 0; i < numberOfStyles; ++i)
				{
				line = new MiLine(0, 0, 40, 0);
				line.setLineStartStyle(lineEndStyles[i + 1]);
				line.setLineEndStyle(lineEndStyles[i + 1]);
				displayedLineStartStyles.addElement(line.getLineStartStyle());
				displayedLineEndStyles.addElement(line.getLineEndStyle());
				lineEndsMenu.appendItem(new MiMenuItem(line));
				}
			}
		return(lineEndsMenu);
		}
	protected 	void		cycleAttributeValueForward()
		{
		for (int i = 0; i < displayedLineStartStyles.size(); ++i)
			{
			if ((lineStartStyle == displayedLineStartStyles.elementAt(i))
				&& (lineEndStyle == displayedLineEndStyles.elementAt(i)))
				{
				if (i == displayedLineStartStyles.size() - 1)
					i = 0;
				else
					++i;

				setLineStartStyle(displayedLineStartStyles.elementAt(i));
				setLineEndStyle(displayedLineEndStyles.elementAt(i));
				return;
				}
			}
		}
	protected 	void		cycleAttributeValueBackward()
		{
		for (int i = 0; i < displayedLineStartStyles.size(); ++i)
			{
			if ((lineStartStyle == displayedLineStartStyles.elementAt(i))
				&& (lineEndStyle == displayedLineEndStyles.elementAt(i)))
				{
				if (i == 0)
					i = displayedLineStartStyles.size() - 1;
				else
					--i;

				setLineStartStyle(displayedLineStartStyles.elementAt(i));
				setLineEndStyle(displayedLineEndStyles.elementAt(i));
				return;
				}
			}
		}
	protected	boolean		updateValueFromPopupMenu(MiiAction action)
		{
		MiMenuItem menuItem = (MiMenuItem )action.getActionSystemInfo();
		int index = ((MiStandardMenu )getMenuContents()).getIndexOfItem(menuItem);
		setLineStartStyle(displayedLineStartStyles.elementAt(index));
		setLineEndStyle(displayedLineEndStyles.elementAt(index));
		return(true);
		}
	}

