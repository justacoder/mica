
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

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGeneralAttributesPanel 
			extends MiContainer 
			implements MiiCommandHandler, MiiLookProperties
	{
	private	static final	String		UPDATE_FROM_PANEL_CMD_NAME
								= "updateFromPanel";

	private	static 	int			ALLEY_SPACING		= 20;
	private	static 	int			INSET_SPACING		= 20;

	public static final	int		ALL_BASIC_ATTRIBUTES_MASK	=
							Mi_COLOR_ATTRIBUTE_MASK_BIT
							| Mi_BACKGROUND_COLOR_ATTRIBUTE_MASK_BIT
							| Mi_LINE_WIDTH_ATTRIBUTE_MASK_BIT
							| Mi_WRITE_MODE_ATTRIBUTE_MASK_BIT
							| Mi_BORDER_LOOK_ATTRIBUTE_MASK_BIT
							| Mi_BORDER_COLORS_ATTRIBUTE_MASK_BIT
							| Mi_GRADIENT_STYLE_ATTRIBUTE_MASK_BIT
							| Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT
							| Mi_STATUS_HELP_ATTRIBUTE_MASK_BIT;

	public static final	int		ALL_TEXT_ATTRIBUTES_MASK	=
							Mi_FONT_ATTRIBUTE_MASK_BIT
							| Mi_FONT_SIZE_ATTRIBUTE_MASK_BIT
							| Mi_FONT_STYLE_ATTRIBUTE_MASK_BIT
							| Mi_FONT_JUSTIFICATION_ATTRIBUTE_MASK_BIT;

	public static final	int		ALL_SHADOW_ATTRIBUTES_MASK	=
							Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT
							| Mi_SHADOW_COLOR_ATTRIBUTE_MASK_BIT
							| Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT
							| Mi_SHADOW_LENGTH_ATTRIBUTE_MASK_BIT;

	public static final	int		ALL_LINE_ATTRIBUTES_MASK	=
							Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT;


	private		MiPart				basicAttributesArea;
	private		MiPart				textAttributesArea;
	private		MiPart				shadowAttributesArea;
	private		MiPart				lineAttributesArea;
	private		MiPart				sampleArea;
	private		MiPart				sampleLook;
	private		MiPart				defaultSampleLook;


	private		boolean				settingAttributes;
	private		boolean				buildingPanel;

	private		boolean				hasChanged;

	private		int				attributesToDisplayMask;
	private		MiParts[]			attributeWidgets;

	private		MiColorOptionMenu		colorOptionMenu;
	private		MiColorOptionMenu		fillColorOptionMenu;
	private		MiLineWidthOptionMenu		lineWidthOptionMenu;
	private		MiNumericTextField		sizeTextField;
	private		MiFontOptionMenu		fontOptionMenu;
	private		MiFontPointSizeOptionMenu	fontPointSizeOptionMenu;
	private		MiBorderLookOptionMenu		borderLookOptionMenu;
	private		MiToggleButton			fontBoldStyleToggleButton;
	private		MiToggleButton			fontItalicStyleToggleButton;
	private		MiToggleButton			fontUnderlinedStyleToggleButton;
	private		MiToggleButton			fontLeftJustifiedRadioButton;
	private		MiToggleButton			fontCenterJustifiedRadioButton;
	private		MiToggleButton			fontRightJustifiedRadioButton;
	private		MiToggleButton			fontJustifiedRadioButton;
	private		MiColorOptionMenu		shadowColorOptionMenu;
	private		MiSpinBox			shadowLength;
	private		MiToggleButton			shadow_lrDirToggle;
	private		MiToggleButton			shadow_llDirToggle;
	private		MiToggleButton			shadow_ulDirToggle;
	private		MiToggleButton			shadow_urDirToggle;
	private		MiWidget			noShadowToggle;
	private		MiWidget			basicShadowToggle;
	private		MiWidget			threeDShadowToggle;
	private		MiWidget			softShadowToggle;
	private		MiLineEndsOptionMenu		lineEndsOptionMenu;
	private		MiTextField			lineStartSize;
	private		MiTextField			lineEndSize;
	private		MiTextField			toolHintHelpMessage;
	private		MiTextField			statusBarHelpMessage;
	private		MiWidget			printableToggleButton;



	public				MiGeneralAttributesPanel(int attributesToDisplayMask)
		{
		this.attributesToDisplayMask = attributesToDisplayMask;
		buildingPanel = true;

		attributeWidgets = new MiParts[32];
		for (int i = 0; i < attributeWidgets.length; ++i)
			attributeWidgets[i] = new MiParts();

		MiColumnLayout mainLayout = new MiColumnLayout();
		mainLayout.setInsetMargins(INSET_SPACING);
		mainLayout.setElementHJustification(Mi_JUSTIFIED);
		mainLayout.setAlleyHSpacing(ALLEY_SPACING);
		setLayout(mainLayout);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementHJustification(Mi_JUSTIFIED);
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);
		appendPart(rowLayout);

		MiColumnLayout column1 = new MiColumnLayout();
		column1.setElementHSizing(Mi_EXPAND_TO_FILL);
		column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column1.setAlleyVSpacing(ALLEY_SPACING);
		rowLayout.appendPart(column1);
		MiColumnLayout column2 = new MiColumnLayout();
		column2.setElementHSizing(Mi_EXPAND_TO_FILL);
		column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column2.setAlleyVSpacing(ALLEY_SPACING);
		rowLayout.appendPart(column2);
		
		basicAttributesArea = makeBasicAttributesArea();
		textAttributesArea = makeTextAttributesArea();
		shadowAttributesArea = makeShadowAttributesArea();
		lineAttributesArea = makeLineAttributesArea();
		sampleArea = makeSampleLookArea();

		column1.appendPart(basicAttributesArea);
		column1.appendPart(textAttributesArea);
		column2.appendPart(shadowAttributesArea);
		column2.appendPart(lineAttributesArea);
		column2.appendPart(sampleArea);

		setWhichAttributesToDisplay(attributesToDisplayMask);

		buildingPanel = false;
		}
	//---------------------------------------------------------------
	public		void		setWhichAttributesToDisplay(int attributesToDisplayMask)
		{
		this.attributesToDisplayMask = attributesToDisplayMask;

		int mask = 1;
		for (int i = 0; i < 32; ++i)
			{
			MiParts widgets = attributeWidgets[i];
			boolean visible = ((attributesToDisplayMask & mask) != 0);
			for (int j = 0; j < widgets.size(); ++j)
				{
				widgets.elementAt(j).setVisible(visible);
				}
			mask <<= 1;
			}
		basicAttributesArea.setVisible(
			((attributesToDisplayMask & ALL_BASIC_ATTRIBUTES_MASK) != 0));
		textAttributesArea.setVisible(
			((attributesToDisplayMask & ALL_TEXT_ATTRIBUTES_MASK) != 0));
		shadowAttributesArea.setVisible(
			((attributesToDisplayMask & ALL_SHADOW_ATTRIBUTES_MASK) != 0));
		lineAttributesArea.setVisible(
			((attributesToDisplayMask & ALL_LINE_ATTRIBUTES_MASK) != 0));
		}
	//---------------------------------------------------------------
	public		void		setDisplaySample(MiPart sample)
		{
		if (sample == null)
			sample = defaultSampleLook;
		if (sample != sampleLook)
			{
			sample = sample.copy();
			sampleLook.replaceSelf(sample);
			sampleLook = sample;
			sampleLook.validateLayout();
			}
		}
	//---------------------------------------------------------------
	public		MiPart		getDisplaySample()
		{
		return(sampleLook);
		}
	//---------------------------------------------------------------
	public		void		setDisplayAttributes(MiAttributes atts)
		{
		settingAttributes = true;

		colorOptionMenu.setColorValue(atts.getColor());
		fillColorOptionMenu.setColorValue(atts.getBackgroundColor());
		lineWidthOptionMenu.setLineWidthValue(atts.getLineWidth());
		borderLookOptionMenu.setBorderLookValue(atts.getBorderLook());
		toolHintHelpMessage.setValue(atts.getToolHintHelp() == null 
			? "" : atts.getToolHintHelp().getMessage());
		statusBarHelpMessage.setValue(atts.getStatusHelp() == null 
			? "" : atts.getStatusHelp().getMessage());
		printableToggleButton.select(atts.isPrintable());

		fontOptionMenu.setFontValue(atts.getFont());
		fontPointSizeOptionMenu.setFontPointSizeValue(atts.getFontPointSize());
		fontBoldStyleToggleButton.select(atts.isFontBold());
		fontItalicStyleToggleButton.select(atts.isFontItalic());
		fontUnderlinedStyleToggleButton.select(atts.getFont().isUnderlined());

		switch (atts.getFontHorizontalJustification())
			{
			case  Mi_LEFT_JUSTIFIED		:
				fontLeftJustifiedRadioButton.select(true);
				break;
			case  Mi_CENTER_JUSTIFIED	:
			default				:
				fontCenterJustifiedRadioButton.select(true);
				break;
			case  Mi_RIGHT_JUSTIFIED	:
				fontRightJustifiedRadioButton.select(true);
				break;
			case  Mi_JUSTIFIED	:
				fontJustifiedRadioButton.select(true);
				break;
			}

		noShadowToggle.select(!atts.getHasShadow());
		basicShadowToggle.select(atts.getHasShadow());
		shadowColorOptionMenu.setColorValue(atts.getShadowColor());
		shadowLength.setValue("" + atts.getShadowLength());
		switch (atts.getShadowDirection())
			{
			case Mi_LOWER_LEFT_LOCATION	:
				shadow_llDirToggle.select(true);
				break;
			case Mi_LOWER_RIGHT_LOCATION	:
				shadow_lrDirToggle.select(true);
				break;
			case Mi_UPPER_LEFT_LOCATION	:
				shadow_ulDirToggle.select(true);
				break;
			case Mi_UPPER_RIGHT_LOCATION	:
				shadow_urDirToggle.select(true);
				break;
			}

		lineEndsOptionMenu.setLineStartStyle(atts.getLineStartStyle());
		lineStartSize.setValue("" + atts.getLineStartSize());

		//lineEndOptionMenu.setLineEndStyle(atts.getLineEndStyle());
		lineEndSize.setValue("" + atts.getLineEndSize());

		hasChanged = false;
		settingAttributes = false;
		updateSample();
		}
	//---------------------------------------------------------------
	public		MiAttributes	getDisplayAttributes()
		{
		MiAttributes atts = MiPart.getDefaultAttributes();
		return(getDisplayAttributes(atts));
		}
	//---------------------------------------------------------------
	public		MiAttributes	getDisplayAttributes(MiAttributes atts)
		{
		if ((attributesToDisplayMask & Mi_COLOR_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setColor(colorOptionMenu.getColorValue());

		if ((attributesToDisplayMask & Mi_BACKGROUND_COLOR_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setBackgroundColor(fillColorOptionMenu.getColorValue());

		if ((attributesToDisplayMask & Mi_LINE_WIDTH_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setLineWidth(lineWidthOptionMenu.getLineWidthValue());

		if ((attributesToDisplayMask & Mi_BORDER_LOOK_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setBorderLook(borderLookOptionMenu.getBorderLookValue());

		if ((attributesToDisplayMask & Mi_STATUS_HELP_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setStatusHelpMessage(statusBarHelpMessage.getValue());

		if ((attributesToDisplayMask & Mi_PRINTABLE_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setPrintable(printableToggleButton.isSelected());

		if ((attributesToDisplayMask & Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setToolHintMessage(toolHintHelpMessage.getValue());

		if ((attributesToDisplayMask & Mi_FONT_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setFont(fontOptionMenu.getFontValue());

		if ((attributesToDisplayMask & Mi_FONT_SIZE_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setFontPointSize(fontPointSizeOptionMenu.getFontPointSizeValue());

		if ((attributesToDisplayMask & Mi_FONT_STYLE_ATTRIBUTE_MASK_BIT) != 0)
			{
			atts = atts.setFontBold(fontBoldStyleToggleButton.isSelected());
			atts = atts.setFontItalic(fontItalicStyleToggleButton.isSelected());
			atts = atts.setFont(atts.getFont().setUnderlined(fontUnderlinedStyleToggleButton.isSelected()));
			}
		if ((attributesToDisplayMask & Mi_FONT_JUSTIFICATION_ATTRIBUTE_MASK_BIT) != 0)
			{
			if (fontLeftJustifiedRadioButton.isSelected())
				atts = atts.setFontHorizontalJustification(Mi_LEFT_JUSTIFIED);
			else if (fontCenterJustifiedRadioButton.isSelected())
				atts = atts.setFontHorizontalJustification(Mi_CENTER_JUSTIFIED);
			else if (fontRightJustifiedRadioButton.isSelected())
				atts = atts.setFontHorizontalJustification(Mi_RIGHT_JUSTIFIED);
			else if (fontJustifiedRadioButton.isSelected())
				atts = atts.setFontHorizontalJustification(Mi_JUSTIFIED);
			}

		if ((attributesToDisplayMask & Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT) != 0)
			{
			if (noShadowToggle.isSelected())
				atts = atts.setHasShadow(false);
			else
				atts = atts.setHasShadow(true);
			}
		if ((attributesToDisplayMask & Mi_SHADOW_COLOR_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setShadowColor(shadowColorOptionMenu.getColorValue());

		if ((attributesToDisplayMask & Mi_SHADOW_LENGTH_ATTRIBUTE_MASK_BIT) != 0)
			atts = atts.setShadowLength(Utility.toDouble(shadowLength.getValue()));

		if ((attributesToDisplayMask & Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT) != 0)
			{
			if (shadow_llDirToggle.isSelected())
				atts = atts.setShadowDirection(Mi_LOWER_LEFT_LOCATION);
			else if (shadow_lrDirToggle.isSelected())
				atts = atts.setShadowDirection(Mi_LOWER_RIGHT_LOCATION);
			else if (shadow_ulDirToggle.isSelected())
				atts = atts.setShadowDirection(Mi_UPPER_LEFT_LOCATION);
			else if (shadow_urDirToggle.isSelected())
				atts = atts.setShadowDirection(Mi_UPPER_RIGHT_LOCATION);
			}
		if ((attributesToDisplayMask & Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT) != 0)
			{
			atts = atts.setLineStartStyle(lineEndsOptionMenu.getLineStartStyle());
			atts = atts.setLineEndStyle(lineEndsOptionMenu.getLineEndStyle());
			atts = atts.setLineStartSize(Utility.toDouble(lineStartSize.getValue()));
			atts = atts.setLineEndSize(Utility.toDouble(lineEndSize.getValue()));
			}

		return(atts);
		}
	//---------------------------------------------------------------
	protected	MiPart		makeBasicAttributesArea()
		{
		MiBox box = new MiBox("Basic Attributes");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		colorOptionMenu = new MiColorOptionMenu();
		MiText colorLabel = new MiText("Color");
		colorOptionMenu.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(colorLabel);
		box.appendPart(colorOptionMenu);
		attributeWidgets[Mi_COLOR_ATTRIBUTE_MASK_BIT_NUMBER].addElement(colorLabel);
		attributeWidgets[Mi_COLOR_ATTRIBUTE_MASK_BIT_NUMBER].addElement(colorOptionMenu);

		fillColorOptionMenu = new MiColorOptionMenu();
		MiText fillColorLabel = new MiText("Fill Color");
		fillColorOptionMenu.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(fillColorLabel);
		box.appendPart(fillColorOptionMenu);
		attributeWidgets[Mi_BACKGROUND_COLOR_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fillColorLabel);
		attributeWidgets[Mi_BACKGROUND_COLOR_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fillColorOptionMenu);

		lineWidthOptionMenu = new MiLineWidthOptionMenu();
		MiText lineWidthLabel = new MiText("Line Width");
		lineWidthOptionMenu.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(lineWidthLabel);
		box.appendPart(lineWidthOptionMenu);
		attributeWidgets[Mi_LINE_WIDTH_ATTRIBUTE_MASK_BIT_NUMBER].addElement(lineWidthLabel);
		attributeWidgets[Mi_LINE_WIDTH_ATTRIBUTE_MASK_BIT_NUMBER].addElement(lineWidthOptionMenu);

		borderLookOptionMenu = new MiBorderLookOptionMenu();
		MiText borderLookLabel = new MiText("3D Style");
		borderLookOptionMenu.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(borderLookLabel);
		box.appendPart(borderLookOptionMenu);
		attributeWidgets[Mi_BORDER_LOOK_ATTRIBUTE_MASK_BIT_NUMBER].addElement(borderLookLabel);
		attributeWidgets[Mi_BORDER_LOOK_ATTRIBUTE_MASK_BIT_NUMBER].addElement(borderLookOptionMenu);

		printableToggleButton = new MiToggleButton();
		MiText printableLabel = new MiText("Printable");
		printableToggleButton.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(printableLabel);
		box.appendPart(printableToggleButton);
		attributeWidgets[Mi_PRINTABLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(printableLabel);
		attributeWidgets[Mi_PRINTABLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(printableToggleButton);

		statusBarHelpMessage = new MiTextField();
		statusBarHelpMessage.setNumDisplayedColumns(20);
		MiText statusHelpLabel = new MiText("Status Bar\nMessage");
		box.appendPart(statusHelpLabel);
		box.appendPart(statusBarHelpMessage);
		attributeWidgets[Mi_STATUS_HELP_ATTRIBUTE_MASK_BIT_NUMBER].addElement(statusBarHelpMessage);
		attributeWidgets[Mi_STATUS_HELP_ATTRIBUTE_MASK_BIT_NUMBER].addElement(statusHelpLabel);


		toolHintHelpMessage = new MiTextField();
		toolHintHelpMessage.setNumDisplayedColumns(20);
		MiText toolHintLabel = new MiText("Tool Hint\nMessage");
		box.appendPart(toolHintLabel);
		box.appendPart(toolHintHelpMessage);
		attributeWidgets[Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT_NUMBER].addElement(toolHintHelpMessage);
		attributeWidgets[Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT_NUMBER].addElement(toolHintLabel);

		return(box);
		}

	protected	MiPart		makeTextAttributesArea()
		{
		MiBox box = new MiBox("Text Attributes");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);


		fontOptionMenu = new MiFontOptionMenu();
		MiText fontLabel = new MiText("Text Font");
		fontOptionMenu.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(fontLabel);
		box.appendPart(fontOptionMenu);
		attributeWidgets[Mi_FONT_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fontLabel);
		attributeWidgets[Mi_FONT_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fontOptionMenu);


		fontPointSizeOptionMenu = new MiFontPointSizeOptionMenu();
		MiText fontPointSizeLabel = new MiText("Text Size");
		fontPointSizeOptionMenu.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(fontPointSizeLabel);
		box.appendPart(fontPointSizeOptionMenu);
		attributeWidgets[Mi_FONT_SIZE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fontPointSizeLabel);
		attributeWidgets[Mi_FONT_SIZE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fontPointSizeOptionMenu);


		MiRowLayout rowLayout = new MiRowLayout();


		fontBoldStyleToggleButton = new MiToggleButton("B");
		fontBoldStyleToggleButton.getLabel().setFontBold(true);
		fontItalicStyleToggleButton = new MiToggleButton("I");
		fontItalicStyleToggleButton.getLabel().setFontItalic(true);
		fontUnderlinedStyleToggleButton = new MiToggleButton("U");
		fontUnderlinedStyleToggleButton.getLabel().setFontUnderlined(true);
		MiText fontStyleLabel = new MiText("Text Style");
		fontBoldStyleToggleButton.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		fontItalicStyleToggleButton.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		fontUnderlinedStyleToggleButton.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(fontStyleLabel);
		rowLayout.appendPart(fontBoldStyleToggleButton);
		rowLayout.appendPart(fontItalicStyleToggleButton);
		rowLayout.appendPart(fontUnderlinedStyleToggleButton);
		attributeWidgets[Mi_FONT_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fontBoldStyleToggleButton);
		attributeWidgets[Mi_FONT_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fontItalicStyleToggleButton);
		attributeWidgets[Mi_FONT_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(fontUnderlinedStyleToggleButton);


		MiRectangle spacer = new MiRectangle(0,0,10,10);
		spacer.setColor(Mi_TRANSPARENT_COLOR);
		rowLayout.appendPart(spacer);

		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);

		fontLeftJustifiedRadioButton = new MiToggleButton(
			new MiImage(Mi_LEFT_JUSTIFIED_ICON_NAME));
		fontLeftJustifiedRadioButton.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		fontLeftJustifiedRadioButton.setRadioStateEnforcer(radioEnforcer);

		fontCenterJustifiedRadioButton = new MiToggleButton(
			new MiImage(Mi_CENTER_JUSTIFIED_ICON_NAME));
		fontCenterJustifiedRadioButton.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		fontCenterJustifiedRadioButton.setRadioStateEnforcer(radioEnforcer);

		fontRightJustifiedRadioButton = new MiToggleButton(
			new MiImage(Mi_RIGHT_JUSTIFIED_ICON_NAME));
		fontRightJustifiedRadioButton.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		fontRightJustifiedRadioButton.setRadioStateEnforcer(radioEnforcer);

		fontJustifiedRadioButton = new MiToggleButton(
			new MiImage(Mi_JUSTIFIED_ICON_NAME));
		fontJustifiedRadioButton.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		fontJustifiedRadioButton.setRadioStateEnforcer(radioEnforcer);

		rowLayout.appendPart(fontLeftJustifiedRadioButton);
		rowLayout.appendPart(fontCenterJustifiedRadioButton);
		rowLayout.appendPart(fontRightJustifiedRadioButton);
		rowLayout.appendPart(fontJustifiedRadioButton);

		attributeWidgets[Mi_FONT_JUSTIFICATION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(
			fontLeftJustifiedRadioButton);
		attributeWidgets[Mi_FONT_JUSTIFICATION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(
			fontCenterJustifiedRadioButton);
		attributeWidgets[Mi_FONT_JUSTIFICATION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(
			fontRightJustifiedRadioButton);
		attributeWidgets[Mi_FONT_JUSTIFICATION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(
			fontJustifiedRadioButton);


		box.appendPart(rowLayout);

		return(box);
		}

	protected	MiPart		makeShadowAttributesArea()
		{
		MiBox box = new MiBox("Shadow Attributes");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiRowLayout rowLayout = new MiRowLayout();

		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);


		MiPart icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setHasShadow(false);
		noShadowToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, rowLayout, radioEnforcer, icon, null);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		//icon.setShadowStyle(MiiShadowRenderer.Mi_BASIC_SHADOW_STYLE);
		icon.setHasShadow(true);
		basicShadowToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, rowLayout, radioEnforcer, icon, null);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		//icon.setShadowStyle(MiiShadowRenderer.Mi_THREE_D_SHADOW_STYLE);
		icon.setHasShadow(true);
		threeDShadowToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, rowLayout, radioEnforcer, icon, null);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setHasShadow(true);
		//icon.setShadowStyle(MiiShadowRenderer.Mi_SOFT_SHADOW_STYLE);
		softShadowToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, rowLayout, radioEnforcer, icon, null);

		MiText shadowStyleLabel = new MiText("Shadow Style");
		box.appendPart(shadowStyleLabel);
		box.appendPart(rowLayout);

		attributeWidgets[Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(rowLayout);
		attributeWidgets[Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadowStyleLabel);
		attributeWidgets[Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(noShadowToggle);
		attributeWidgets[Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(basicShadowToggle);
		attributeWidgets[Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(threeDShadowToggle);
		attributeWidgets[Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT_NUMBER].addElement(softShadowToggle);


		shadowColorOptionMenu = new MiColorOptionMenu();
		MiText shadowColorLabel = new MiText("Shadow Color");
		shadowColorOptionMenu.appendCommandHandler(
			this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(shadowColorLabel);
		box.appendPart(shadowColorOptionMenu);
		attributeWidgets[Mi_SHADOW_COLOR_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadowColorLabel);
		attributeWidgets[Mi_SHADOW_COLOR_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadowColorOptionMenu);



		rowLayout = new MiRowLayout();

		radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setShadowDirection(Mi_LOWER_RIGHT_LOCATION);
		icon.setHasShadow(true);
		shadow_lrDirToggle = new MiToggleButton();
		shadow_lrDirToggle.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME);
		shadow_lrDirToggle.setCellMargins(5);
		shadow_lrDirToggle.setRadioStateEnforcer(radioEnforcer);
		shadow_lrDirToggle.setSelectedBackgroundColor(MiColorManager.white);
		shadow_lrDirToggle.appendPart(icon);
		rowLayout.appendPart(shadow_lrDirToggle);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setShadowDirection(Mi_LOWER_LEFT_LOCATION);
		icon.setHasShadow(true);
		shadow_llDirToggle = new MiToggleButton();
		shadow_llDirToggle.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME);
		shadow_llDirToggle.setCellMargins(5);
		shadow_llDirToggle.setRadioStateEnforcer(radioEnforcer);
		shadow_llDirToggle.setSelectedBackgroundColor(MiColorManager.white);
		shadow_llDirToggle.appendPart(icon);
		rowLayout.appendPart(shadow_llDirToggle);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setShadowDirection(Mi_UPPER_LEFT_LOCATION);
		icon.setHasShadow(true);
		shadow_ulDirToggle = new MiToggleButton();
		shadow_ulDirToggle.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME);
		shadow_ulDirToggle.setCellMargins(5);
		shadow_ulDirToggle.setRadioStateEnforcer(radioEnforcer);
		shadow_ulDirToggle.setSelectedBackgroundColor(MiColorManager.white);
		shadow_ulDirToggle.appendPart(icon);
		rowLayout.appendPart(shadow_ulDirToggle);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setHasShadow(true);
		icon.setShadowDirection(Mi_UPPER_RIGHT_LOCATION);
		shadow_urDirToggle = new MiToggleButton();
		shadow_urDirToggle.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME);
		shadow_urDirToggle.setCellMargins(5);
		shadow_urDirToggle.setRadioStateEnforcer(radioEnforcer);
		shadow_urDirToggle.setSelectedBackgroundColor(MiColorManager.white);
		shadow_urDirToggle.appendPart(icon);
		rowLayout.appendPart(shadow_urDirToggle);

		MiText shadowDirectionLabel = new MiText("Shadow Direction");
		box.appendPart(shadowDirectionLabel);
		box.appendPart(rowLayout);
		attributeWidgets[Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadowDirectionLabel);
		attributeWidgets[Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(rowLayout);
		attributeWidgets[Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadow_llDirToggle);
		attributeWidgets[Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadow_lrDirToggle);
		attributeWidgets[Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadow_ulDirToggle);
		attributeWidgets[Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadow_urDirToggle);



		shadowLength = new MiSpinBox();
		shadowLength.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		shadowLength.setRange(1, 20, 1);
		MiText shadowLengthLabel = new MiText("Shadow Length");
		box.appendPart(shadowLengthLabel);
		box.appendPart(shadowLength);
		attributeWidgets[Mi_SHADOW_LENGTH_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadowLengthLabel);
		attributeWidgets[Mi_SHADOW_LENGTH_ATTRIBUTE_MASK_BIT_NUMBER].addElement(shadowLength);
		return(box);
		}
	//---------------------------------------------------------------
	protected	MiPart		makeLineAttributesArea()
		{
		MiBox box = new MiBox("Line Attributes");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		lineEndsOptionMenu = new MiLineEndsOptionMenu();
		MiText lineEndsLabel = new MiText("Line Ends");
		lineEndsOptionMenu.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(lineEndsLabel);
		box.appendPart(lineEndsOptionMenu);
		attributeWidgets[Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT_NUMBER].addElement(lineEndsLabel);
		attributeWidgets[Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT_NUMBER].addElement(lineEndsOptionMenu);

		lineStartSize = new MiNumericTextField();
		MiText lineStartSizeLabel = new MiText("Line Start Size");
		lineStartSize.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(lineStartSizeLabel);
		box.appendPart(lineStartSize);
		attributeWidgets[Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT_NUMBER].addElement(lineStartSizeLabel);
		attributeWidgets[Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT_NUMBER].addElement(lineStartSize);

		lineEndSize = new MiNumericTextField();
		MiText lineEndSizeLabel = new MiText("Line End Size");
		lineEndSize.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(lineEndSizeLabel);
		box.appendPart(lineEndSize);
		attributeWidgets[Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT_NUMBER].addElement(lineEndSizeLabel);
		attributeWidgets[Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT_NUMBER].addElement(lineEndSize);

		return(box);
		}
	//---------------------------------------------------------------
	protected	MiPart		makeSampleLookArea()
		{
		MiBox box = new MiBox("Sample Look");
		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(container,
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		container.setIncomingInvalidLayoutNotificationsEnabled(false);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementJustification(Mi_CENTER_JUSTIFIED);
		layout.setInsetMargins(14);
		container.setLayout(layout);
		sampleLook = new MiRectangle(0,0,75,50);
		defaultSampleLook = sampleLook;
		container.appendPart(sampleLook);
		box.appendPart(container);
		return(box);
		}
	//---------------------------------------------------------------
	protected	void		updateSample()
		{
		sampleLook.setAttributes(getDisplayAttributes());
		}
	public		void		processCommand(String cmd)
		{
		if ((buildingPanel) || (settingAttributes))
			return;

		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	}



