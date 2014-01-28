
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
import java.awt.Color; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiColorOptionMenu extends MiAttributeOptionMenu implements MiiActionHandler, MiiCommandHandler
	{
	public static final String TRANSPARENT_COLOR_COMMAND_NAME	= "transparentColorCmd";
	public static final String BRIGHTER_COLOR_COMMAND_NAME		= "brighterColorCmd";
	public static final String DARKER_COLOR_COMMAND_NAME		= "darkerColorCmd";
	public static final String MORE_COLORS_COMMAND_NAME		= "moreColorsCmd";

	private		MiSwatchesColorPalette	colorSwatches;
	private		MiPushButton		brighterColorPB;
	private		MiPushButton		darkerColorPB;
	private		MiPushButton		transparentColorPB;
	private		MiPushButton		moreColorsPB;
	private		Color			initialColor		= MiColorManager.blue;
	private		Color			colorValue;
	private		String			colorValueName;
	private		MiColorChooser		colorChooser;
	private		MiVisibleContainer	colorSwatchField;
	private		MiContainer		transparentCross;



	public				MiColorOptionMenu()
		{
		this(true, true, true, true, true);
		}
	public				MiColorOptionMenu(
						boolean brighterDarkerTransparentOptions, 
						boolean moreColorsOption, 
						boolean displayAttributeIcon,
						boolean displayAttributeValueName,
						boolean displayMenuLauncherButton)
		{
		build(displayAttributeIcon, displayAttributeValueName, displayMenuLauncherButton);
		if ((!displayAttributeValueName) && (!displayMenuLauncherButton))
			{
			setInsetMargins(1);
			colorSwatchField.setPreferredSize(new MiSize(24, 24));
			}
		else if (colorSwatchField != null)
			{
			colorSwatchField.setPreferredSize(new MiSize(30,16));
			colorSwatchField.setMargins(new MiMargins(2, 2, 0, 2));
			}

		if (!brighterDarkerTransparentOptions)
			{
			brighterColorPB.setVisible(false);
			darkerColorPB.setVisible(false);
			transparentColorPB.setVisible(false);
			}
		if (!moreColorsOption)
			{
			moreColorsPB.setVisible(false);
			}
		setColorValue(initialColor);
		}
	public		MiWidget	getBrighterColorPushButton()
		{
		return(brighterColorPB);
		}
	public		MiWidget	getDarkerColorPushButton()
		{
		return(darkerColorPB);
		}
	public		MiWidget	getMoreColorsPushButton()
		{
		return(moreColorsPB);
		}
	public		MiSwatchesColorPalette	getColorSwatches()
		{
		return(colorSwatches);
		}
	public		void		setColorValue(Color value)
		{
		if ((value == null) && (colorValue == null))
			{
			return;
			}
		else if ((value != null) && (colorValue != null) && (colorValue.equals(value)))
			{
			return;
			}

		colorValue = value;
		colorValueName = MiColorManager.getColorName(value);
		getAttributeValueNameField().setValue(colorValueName);
		getAttributeDisplayField().setBackgroundColor(value);
		if (value == null)
			{
			brighterColorPB.setSensitive(false);
			darkerColorPB.setSensitive(false);
			transparentCross.setVisible(true);
			}
		else
			{
			brighterColorPB.setSensitive(true);
			darkerColorPB.setSensitive(true);
			transparentCross.setVisible(false);
			}
		colorSwatches.setSelection(value);
		}
	public		Color		getColorValue()
		{
		return(colorValue);
		}
	public		void		setValue(String value)
		{
		try	{
			setColorValue(MiColorManager.getColor(value));
			}
		catch (IllegalArgumentException e)
			{
			setColorValue(colorValue);
			}
		}
	public		String		getValue()
		{
		return(colorValueName);
		}
	public		void		setContents(Strings contents)
		{
		colorSwatches.setContents(contents);
		}
	public		Strings		getContents()
		{
		return(colorSwatches.getContents());
		}
	protected	MiPart		makeAttributeIcon()
		{
		return(null);
		}
	protected	MiPart		makeAttributeDisplayField()
		{
		colorSwatchField = new MiVisibleContainer();
		colorSwatchField.setPreferredSize(new MiSize(30,20));
		colorSwatchField.setBorderLook(Mi_FLAT_BORDER_LOOK);
		colorSwatchField.setColor(Mi_TRANSPARENT_COLOR);
		transparentCross = new MiContainer();
		transparentCross.appendPart(new MiLine(0,0,10,10));
		transparentCross.appendPart(new MiLine(0,10,10,0));
		transparentCross.setInvalidLayoutNotificationsEnabled(false); // causes probs after ctrl-shift-L 12-30-2001
		transparentCross.setInvalidLayoutNotificationsEnabled(true);
		colorSwatchField.appendPart(transparentCross);
		return(colorSwatchField);
		}
	protected	MiWidget	makeMenuContents()
		{
		MiWidget container = new MiWidget();
		container.setInsetMargins(0);
		MiColumnLayout columnLayout = new MiColumnLayout();
		container.setLayout(columnLayout);

		colorSwatches = new MiSwatchesColorPalette(new MiSize(12, 12));
		colorSwatches.setInsetMargins(0);
		colorSwatches.setArrowKeysSelectAsWellAsBrowseSwatches(false);
		colorSwatches.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		container.appendPart(colorSwatches);

		brighterColorPB = new MiPushButton("Brighter");
		brighterColorPB.appendCommandHandler(this, BRIGHTER_COLOR_COMMAND_NAME);
		container.appendPart(brighterColorPB);

		darkerColorPB = new MiPushButton("Darker");
		darkerColorPB.appendCommandHandler(this, DARKER_COLOR_COMMAND_NAME);
		container.appendPart(darkerColorPB);

		transparentColorPB = new MiPushButton("Transparent");
		transparentColorPB.appendCommandHandler(this, TRANSPARENT_COLOR_COMMAND_NAME);
		container.appendPart(transparentColorPB);

		moreColorsPB = new MiPushButton("More...");
		moreColorsPB.appendCommandHandler(this, MORE_COLORS_COMMAND_NAME);
		container.appendPart(moreColorsPB);

		return(container);
		}
	protected 	void		cycleAttributeValueForward()
		{
		int index = colorSwatches.getSelectedSwatch() + 1;
		colorSwatches.setSelectedSwatch(index < colorSwatches.getNumberOfSwatches() ? index : 0);
		}
	protected 	void		cycleAttributeValueBackward()
		{
		int index = colorSwatches.getSelectedSwatch() - 1;
		colorSwatches.setSelectedSwatch(index < 0 ? colorSwatches.getNumberOfSwatches() - 1 : index);
		}
	protected	boolean		updateValueFromPopupMenu(MiiAction action)
		{
		setColorValue(colorSwatches.getSelection());
		return(true);
		}
	public		void		processCommand(String cmd)
		{
		if (cmd.equals(BRIGHTER_COLOR_COMMAND_NAME))
			{
			setColorValue(getColorValue().brighter());
			// Popdown menu even if color does not change...
			super.processCommand(Mi_POPDOWN_COMMAND_NAME);
			}
		else if (cmd.equals(DARKER_COLOR_COMMAND_NAME))
			{
			setColorValue(getColorValue().darker());
			super.processCommand(Mi_POPDOWN_COMMAND_NAME);
			}
		else if (cmd.equals(TRANSPARENT_COLOR_COMMAND_NAME))
			{
			setColorValue(null);
			super.processCommand(Mi_POPDOWN_COMMAND_NAME);
			}
		else if (cmd.equals(MORE_COLORS_COMMAND_NAME))
			{
			if (colorChooser == null)
				colorChooser = new MiColorChooser(getContainingEditor());
			Color c = colorChooser.popupAndWaitForClose();
			if (c != null)
				setColorValue(c);
			}
		else
			{
			super.processCommand(cmd);
			}
		}
	}

