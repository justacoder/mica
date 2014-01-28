
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Toolkit                   *
 ***************************************************************************
 * NOTICE: Permission to use, copy, modify and distribute this software    *
 * and its documentation, for commercial or non-commercial purposes, is    *
 * hereby granted provided that this notice appears in all copies.         *
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
import java.awt.Color; 

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDrawingAttributesToolBar extends MiToolBar implements MiiCommandNames, MiiActionHandler
	{
	public static final String	CYCLE_COLOR_COMMAND_NAME	= "cycleColor";
	public static final String	CYCLE_BG_COLOR_COMMAND_NAME	= "cycleBGColor";
	public static final String	CYCLE_LINE_WIDTH_COMMAND_NAME	= "cycleLineWidth";
	public static final String	CYCLE_LINE_END_COMMAND_NAME	= "cycleLineEnd";
	public static final String	CYCLE_BORDER_LOOK_COMMAND_NAME	= "cycleBorderLooks";
	public static final String	Mi_TEXT_BOLD_COMMAND_NAME	= "Bold";
	public static final String	Mi_TEXT_NO_BOLD_COMMAND_NAME	= "noBold";
	public static final String	Mi_TEXT_ITALIC_COMMAND_NAME	= "Italic";
	public static final String	Mi_TEXT_NO_ITALIC_COMMAND_NAME	= "noItalic";
	public static final String	Mi_TEXT_UNDERLINED_COMMAND_NAME	= "Underline";
	public static final String	Mi_TEXT_NO_UNDERLINED_COMMAND_NAME= "noUnderline";
	public static final String	Mi_TEXT_STRIKEOUT_COMMAND_NAME	= "StrikeOut";
	public static final String	Mi_TEXT_NO_STRIKEOUT_COMMAND_NAME= "noStrikeOut";
	private static final String	Mi_CHANGE_FONT_COMMAND_NAME	= "Text Font";
	private static final String	Mi_CHANGE_FONT_POINT_SIZE_COMMAND_NAME= "Text Size";


	private static final String	Mi_SHAPE_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME	
									= "shapeAttDialogChanged";

	private static final String	Mi_CHANGE_FONT_LABEL_NAME	= "Text Font";
	private static final String	Mi_CHANGE_FONT_POINT_SIZE_LABEL_NAME= "Text Size";
	private static final String	Mi_FONT_TOOL_ITEM_TOOLHINT_MSG
									= "Font";
	private static final String	Mi_FONT_POINT_SIZE_TOOL_ITEM_TOOLHINT_MSG
									= "Font Size";
	private static final String	Mi_COLOR_TOOL_ITEM_TOOLHINT_MSG
									= "Color";
	private static final String	Mi_BG_COLOR_TOOL_ITEM_TOOLHINT_MSG
									= "Fill Color";
	private static final String	Mi_LINE_WIDTH_TOOL_ITEM_TOOLHINT_MSG
									= "Line Width";
	private static final String	Mi_LINE_END_TOOL_ITEM_TOOLHINT_MSG
									= "Line Ends";
	private static final String	Mi_BORDER_LOOK_TOOL_ITEM_TOOLHINT_MSG
									= "Border Look";

	private static final String	Mi_TEXT_BOLD_LABEL_NAME			= "Bold";
	private static final String	Mi_TEXT_ITALIC_LABEL_NAME		= "Italic";
	private static final String	Mi_TEXT_UNDERLINED_LABEL_NAME		= "Underline";
	private static final String	Mi_TEXT_STRIKEOUT_LABEL_NAME		= "StrikeOut";


	private 	int		FONT_STYLE_BUTTON_SIZE		= 22;
	private 	MiFont		FONT_STYLE_BUTTON_FONT		= new MiFont("Arial");
	private 	MiSize		fontButtonSize			= new MiSize(26, 26);


	private		boolean			updatingToolBarAttributes;

	private		MiParts			selectedShapes		= new MiParts();

	private		MiAttributes		defaultAttributes	= new MiAttributes();

	private		MiiShapeAttributesInspectorPanel	shapeAttributesDialog;

	private		MiFontOptionMenu 	fontOptionMenu;
	private		MiFontPointSizeOptionMenu 	fontPointSizeOptionMenu;
	private		MiWidget		boldFontToggleButton;
	private		MiWidget		italicFontToggleButton;
	private		MiWidget		underLineFontToggleButton;
	private		MiWidget		strikeThruFontToggleButton;
	private		MiColorOptionMenu 	colorOptionMenu;
	private		MiColorOptionMenu 	bgColorOptionMenu;
	private		MiLineWidthOptionMenu 	lineWidthOptionMenu;
	private		MiLineEndsOptionMenu	lineEndsOptionMenu;
	private		MiBorderLookOptionMenu	borderLookOptionMenu;
	private		MiEditor		oldEditor;

	private		MiDrawingToolBarManager	toolBarManager;
	private		MiModifyAttributesOfPartsCommand	modifyAttributesOfPartsCommand;


	protected			MiDrawingAttributesToolBar()
		{
		// Used for copy operation.
		}
	public				MiDrawingAttributesToolBar(
						MiEditor editor, 
						MiiCommandManager manager)
		{
		super(editor, manager);
		modifyAttributesOfPartsCommand = new MiModifyAttributesOfPartsCommand();
		modifyAttributesOfPartsCommand.setTargetOfCommand(editor);
		setup();
		}

	public		MiPart		getDrawingAttributesButton(String drawModeCommand)
		{
		return(getToolItemWithCommand(drawModeCommand));
		}
	public		void		setToolBarManager(MiDrawingToolBarManager manager)
		{
		this.toolBarManager = manager;
		}

	public		void		setShapeAttributesDialog(MiiShapeAttributesInspectorPanel dialog)
		{
		if (shapeAttributesDialog != null)
			{
			((MiPart )shapeAttributesDialog).removeCommandHandler(this);
			((MiPart )shapeAttributesDialog).removeActionHandlers(this);
			}
		shapeAttributesDialog = dialog;
		shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);
		((MiPart )shapeAttributesDialog).appendCommandHandler(
			this, Mi_SHAPE_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		}
	public		void		setEditor(MiEditor editor)
		{
		super.setEditor(editor);

		assignToolBarHandlersToTargetEditor(editor);
		}

	protected	void		assignToolBarHandlersToTargetEditor(MiEditor newEditor)
		{
		if (newEditor == oldEditor)
			{
			return;
			}
		if (oldEditor != null)
			{
			oldEditor.removeActionHandlers(this);
			}
		newEditor.appendActionHandler(new MiAction(this, 
				Mi_NO_ITEMS_SELECTED_ACTION,
				Mi_ONE_ITEM_SELECTED_ACTION,
				Mi_MANY_ITEMS_SELECTED_ACTION,
				null));

		oldEditor = newEditor;
		}
	protected	void		setup()
		{
		processCommand(Mi_HIDE_LABELS_COMMAND_NAME);

		// getEditor().getSelectionManager().setSelectionGraphics(new MiManipulatorSelectionGraphics());

		fontOptionMenu = new MiFontOptionMenu();
		fontOptionMenu.appendCommandHandler(
			this, Mi_CHANGE_FONT_COMMAND_NAME, Mi_VALUE_CHANGED_ACTION);
		appendToolItem(
			Mi_FONT_TOOL_ITEM_TOOLHINT_MSG, fontOptionMenu, this, Mi_CHANGE_FONT_LABEL_NAME);

		fontPointSizeOptionMenu = new MiFontPointSizeOptionMenu();
		fontPointSizeOptionMenu.appendCommandHandler(
			this, Mi_CHANGE_FONT_POINT_SIZE_COMMAND_NAME, Mi_VALUE_CHANGED_ACTION);
		appendToolItem(
			Mi_FONT_POINT_SIZE_TOOL_ITEM_TOOLHINT_MSG, 
			fontPointSizeOptionMenu, this, Mi_CHANGE_FONT_POINT_SIZE_LABEL_NAME);

		appendSpacer();

		MiText boldFontText = new MiText("B");
		boldFontText.setFont(FONT_STYLE_BUTTON_FONT);
		boldFontText.setFontBold(true);
		boldFontText.setFontPointSize(FONT_STYLE_BUTTON_SIZE);
		boldFontToggleButton = appendBooleanToolItem(Mi_TEXT_BOLD_LABEL_NAME, 
								boldFontText, this, 
								Mi_TEXT_BOLD_COMMAND_NAME,
								Mi_TEXT_NO_BOLD_COMMAND_NAME);
		boldFontToggleButton.setContainerLayoutSpec(
			MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS_OR_OVERRIDDEN_PREFERRED_SIZE);
		boldFontToggleButton.setPreferredSize(fontButtonSize);
		boldFontToggleButton.setMinimumSize(fontButtonSize);


		//appendSpacer();

		MiText italicFontText = new MiText("I");
		italicFontText.setFont(FONT_STYLE_BUTTON_FONT);
		italicFontText.setFontBold(true);
		italicFontText.setFontPointSize(FONT_STYLE_BUTTON_SIZE);
		italicFontText.setFontItalic(true); // Causes this to be 4+ pixels larger than others
		italicFontToggleButton = appendBooleanToolItem(Mi_TEXT_ITALIC_LABEL_NAME, 
								italicFontText, this, 
								Mi_TEXT_ITALIC_COMMAND_NAME,
								Mi_TEXT_NO_ITALIC_COMMAND_NAME);
		italicFontToggleButton.setContainerLayoutSpec(
			MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS_OR_OVERRIDDEN_PREFERRED_SIZE);
		italicFontToggleButton.setPreferredSize(fontButtonSize);
		italicFontToggleButton.setMinimumSize(fontButtonSize);

		//appendSpacer();

		MiText underlineFontText = new MiText("U");
		underlineFontText.setFont(FONT_STYLE_BUTTON_FONT);
		underlineFontText.setFontBold(true);
		underlineFontText.setFont(underlineFontText.getFont().setUnderlined(true));
		underlineFontText.setFontPointSize(FONT_STYLE_BUTTON_SIZE);
		underLineFontToggleButton = appendBooleanToolItem(Mi_TEXT_UNDERLINED_LABEL_NAME, 
								underlineFontText, this, 
								Mi_TEXT_UNDERLINED_COMMAND_NAME,
								Mi_TEXT_NO_UNDERLINED_COMMAND_NAME);
		underLineFontToggleButton.setContainerLayoutSpec(
			MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS_OR_OVERRIDDEN_PREFERRED_SIZE);
		underLineFontToggleButton.setPreferredSize(fontButtonSize);
		underLineFontToggleButton.setMinimumSize(fontButtonSize);

		//appendSpacer();

		MiText strikeFontText = new MiText("S");
		strikeFontText.setFont(FONT_STYLE_BUTTON_FONT);
		strikeFontText.setFontBold(true);
		strikeFontText.setFont(strikeFontText.getFont().setStrikeOut(true));
		strikeFontText.setFontPointSize(FONT_STYLE_BUTTON_SIZE);
		strikeThruFontToggleButton = appendBooleanToolItem(Mi_TEXT_STRIKEOUT_LABEL_NAME, 
								strikeFontText, this, 
								Mi_TEXT_STRIKEOUT_COMMAND_NAME,
								Mi_TEXT_NO_STRIKEOUT_COMMAND_NAME);
		strikeThruFontToggleButton.setContainerLayoutSpec(
			MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS_OR_OVERRIDDEN_PREFERRED_SIZE);
		strikeThruFontToggleButton.setPreferredSize(fontButtonSize);
		strikeThruFontToggleButton.setMinimumSize(fontButtonSize);

		appendSpacer();

		colorOptionMenu = new MiColorOptionMenu(false, false, true, false, false);
		colorOptionMenu.appendCommandHandler(
			this, CYCLE_COLOR_COMMAND_NAME, Mi_VALUE_CHANGED_ACTION);
		appendToolItem(
			Mi_COLOR_TOOL_ITEM_TOOLHINT_MSG, colorOptionMenu, this, CYCLE_COLOR_COMMAND_NAME);

		bgColorOptionMenu = new MiColorOptionMenu(false, false, true, false, false);
		bgColorOptionMenu.appendCommandHandler(
			this, CYCLE_BG_COLOR_COMMAND_NAME, Mi_VALUE_CHANGED_ACTION);
		appendToolItem(
			Mi_BG_COLOR_TOOL_ITEM_TOOLHINT_MSG, 
			bgColorOptionMenu, 
			this, 
			CYCLE_BG_COLOR_COMMAND_NAME);

		lineWidthOptionMenu = new MiLineWidthOptionMenu();
		lineWidthOptionMenu.appendCommandHandler(
			this, CYCLE_LINE_WIDTH_COMMAND_NAME, Mi_VALUE_CHANGED_ACTION);
		appendToolItem(
			Mi_LINE_WIDTH_TOOL_ITEM_TOOLHINT_MSG, 
			lineWidthOptionMenu, 
			this, 
			CYCLE_LINE_WIDTH_COMMAND_NAME);

		lineEndsOptionMenu = new MiLineEndsOptionMenu();
		lineEndsOptionMenu.appendCommandHandler(
			this, CYCLE_LINE_END_COMMAND_NAME, Mi_VALUE_CHANGED_ACTION);
		appendToolItem(
			Mi_LINE_END_TOOL_ITEM_TOOLHINT_MSG, 
			lineEndsOptionMenu, 
			this, 
			CYCLE_LINE_END_COMMAND_NAME);

		borderLookOptionMenu = new MiBorderLookOptionMenu();
		borderLookOptionMenu.appendCommandHandler(
			this, CYCLE_BORDER_LOOK_COMMAND_NAME, Mi_VALUE_CHANGED_ACTION);
		appendToolItem(
			Mi_BORDER_LOOK_TOOL_ITEM_TOOLHINT_MSG, 
			borderLookOptionMenu, 
			this, 
			CYCLE_BORDER_LOOK_COMMAND_NAME);

		setToolItemImageSizes(new MiSize(24, 24));

		colorOptionMenu.setColorValue(defaultAttributes.getColor());
		bgColorOptionMenu.setColorValue(defaultAttributes.getBackgroundColor());
		lineWidthOptionMenu.setLineWidthValue(defaultAttributes.getLineWidth());

		assignToolBarHandlersToTargetEditor(getEditor());
		}

	public		void		processCommand(String cmd)
		{
//MiDebug.println(this + "processCommand: " + cmd);
		if (updatingToolBarAttributes)
			return;

		if (cmd.equalsIgnoreCase(CYCLE_COLOR_COMMAND_NAME))
			{
			Color curColor = colorOptionMenu.getColorValue();

			if (selectedShapes.size() == 0)
				{
				defaultAttributes = defaultAttributes.setColor(curColor);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);

				if (toolBarManager != null)
					toolBarManager.setNewShapeAttributes(defaultAttributes);
				}
			else
				{
				setSelectedShapesAttribute(Mi_COLOR_ATT_NAME, curColor);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					{
					shapeAttributesDialog.setDisplayedAttributes(
						selectedShapes.elementAt(0).getAttributes());
					}
				}
			}
		else if (cmd.equalsIgnoreCase(CYCLE_BG_COLOR_COMMAND_NAME))
			{
			Color curBGColor = bgColorOptionMenu.getColorValue();

			if (selectedShapes.size() == 0)
				{
				defaultAttributes = defaultAttributes.setBackgroundColor(curBGColor);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);

				if (toolBarManager != null)
					toolBarManager.setNewShapeAttributes(defaultAttributes);
				}
			else
				{
				setSelectedShapesAttribute(Mi_BACKGROUND_COLOR_ATT_NAME, curBGColor);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					{
					shapeAttributesDialog.setDisplayedAttributes(
						selectedShapes.elementAt(0).getAttributes());
					}
				}
			}
		else if (cmd.equalsIgnoreCase(CYCLE_LINE_WIDTH_COMMAND_NAME))
			{
			MiDistance curLineWidth = lineWidthOptionMenu.getLineWidthValue();

			if (selectedShapes.size() == 0)
				{
				defaultAttributes = defaultAttributes.setLineWidth(curLineWidth);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);

				if (toolBarManager != null)
					toolBarManager.setNewShapeAttributes(defaultAttributes);
				}
			else
				{
				setSelectedShapesAttribute(Mi_LINE_WIDTH_ATT_NAME, curLineWidth);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					{
					shapeAttributesDialog.setDisplayedAttributes(
						selectedShapes.elementAt(0).getAttributes());
					}
				}
			}
		else if (cmd.equalsIgnoreCase(CYCLE_LINE_END_COMMAND_NAME))
			{
			int lineStartStyle = lineEndsOptionMenu.getLineStartStyle();
			int lineEndStyle = lineEndsOptionMenu.getLineEndStyle();

			if (selectedShapes.size() == 0)
				{
				defaultAttributes = defaultAttributes.setLineStartStyle(lineStartStyle);
				defaultAttributes = defaultAttributes.setLineEndStyle(lineEndStyle);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);

				if (toolBarManager != null)
					toolBarManager.setNewShapeAttributes(defaultAttributes);
				}
			else
				{
				setSelectedShapesAttribute(Mi_LINE_START_STYLE_ATT_NAME, lineStartStyle);
				setSelectedShapesAttribute(Mi_LINE_END_STYLE_ATT_NAME, lineEndStyle);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					{
					shapeAttributesDialog.setDisplayedAttributes(
						selectedShapes.elementAt(0).getAttributes());
					}
				}
			}
		else if (cmd.equalsIgnoreCase(CYCLE_BORDER_LOOK_COMMAND_NAME))
			{
			int borderLook = borderLookOptionMenu.getBorderLookValue();

			if (selectedShapes.size() == 0)
				{
				defaultAttributes = defaultAttributes.setBorderLook(borderLook);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);

				if (toolBarManager != null)
					toolBarManager.setNewShapeAttributes(defaultAttributes);
				}
			else
				{
				setSelectedShapesAttribute(Mi_BORDER_LOOK_ATT_NAME, borderLook);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					{
					shapeAttributesDialog.setDisplayedAttributes(
						selectedShapes.elementAt(0).getAttributes());
					}
				}
			}
		else if (cmd.equalsIgnoreCase(Mi_TEXT_BOLD_COMMAND_NAME))
			{
			setFontStyle(Mi_FONT_BOLD_ATT_NAME, true);
			}
		else if (cmd.equalsIgnoreCase(Mi_TEXT_NO_BOLD_COMMAND_NAME))
			{
			setFontStyle(Mi_FONT_BOLD_ATT_NAME, false);
			}
		else if (cmd.equalsIgnoreCase(Mi_TEXT_ITALIC_COMMAND_NAME))
			{
			setFontStyle(Mi_FONT_ITALIC_ATT_NAME, true);
			}
		else if (cmd.equalsIgnoreCase(Mi_TEXT_NO_ITALIC_COMMAND_NAME))
			{
			setFontStyle(Mi_FONT_ITALIC_ATT_NAME, false);
			}
		else if (cmd.equalsIgnoreCase(Mi_TEXT_UNDERLINED_COMMAND_NAME))
			{
			setFontStyle(Mi_FONT_UNDERLINED_ATT_NAME, true);
			}
		else if (cmd.equalsIgnoreCase(Mi_TEXT_NO_UNDERLINED_COMMAND_NAME))
			{
			setFontStyle(Mi_FONT_UNDERLINED_ATT_NAME, false);
			}
		else if (cmd.equalsIgnoreCase(Mi_TEXT_STRIKEOUT_COMMAND_NAME))
			{
			setFontStyle(Mi_FONT_STRIKEOUT_ATT_NAME, true);
			}
		else if (cmd.equalsIgnoreCase(Mi_TEXT_NO_STRIKEOUT_COMMAND_NAME))
			{
			setFontStyle(Mi_FONT_STRIKEOUT_ATT_NAME, false);
			}
		else if (cmd.equalsIgnoreCase(Mi_CHANGE_FONT_COMMAND_NAME))
			{
			String fontName = fontOptionMenu.getValue();
			if (selectedShapes.size() == 0)
				{
				defaultAttributes = defaultAttributes.setFontName(fontName);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);

				if (toolBarManager != null)
					toolBarManager.setNewShapeAttributes(defaultAttributes);
				}
			else
				{
				setSelectedShapesAttribute(Mi_FONT_NAME_ATT_NAME, fontName);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					{
					shapeAttributesDialog.setDisplayedAttributes(
						selectedShapes.elementAt(0).getAttributes());
					}
				}
			}
		else if (cmd.equalsIgnoreCase(Mi_CHANGE_FONT_POINT_SIZE_COMMAND_NAME))
			{
			String fontSize = fontPointSizeOptionMenu.getValue();
			if (selectedShapes.size() == 0)
				{
				defaultAttributes 
					= defaultAttributes.setFontPointSize(Utility.toInteger(fontSize));
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);

				if (toolBarManager != null)
					toolBarManager.setNewShapeAttributes(defaultAttributes);
				}
			else
				{
				setSelectedShapesAttribute(Mi_FONT_SIZE_ATT_NAME, fontSize);
				if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					{
					shapeAttributesDialog.setDisplayedAttributes(
						selectedShapes.elementAt(0).getAttributes());
					}
				}
			}
		else if (cmd.equalsIgnoreCase(Mi_SHAPE_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME))
			{
			MiAttributes atts = shapeAttributesDialog.getDisplayedAttributes();
			if (selectedShapes.size() == 0)
				{
				defaultAttributes = atts;
				if (toolBarManager != null)
					toolBarManager.setNewShapeAttributes(defaultAttributes);
				}

			setSelectedShapesAttributes(atts);
			updateToolBarAttributes(atts);
			}
		else if (cmd.equalsIgnoreCase(MiDrawingToolBarManager.Mi_NEW_SHAPE_ATTRIBUTES_CHANGED_STATE_NAME))
			{
			if (toolBarManager != null)
				{
				MiAttributes atts = toolBarManager.getNewShapeAttributes();
				if (selectedShapes.size() == 0)
					{
					defaultAttributes = atts;
					}
				updateToolBarAttributes(atts);
				}
			}
		else 
			{
			super.processCommand(cmd);
			}
		}
	protected	void		setFontStyle(String styleName, boolean flag)
		{
		if (selectedShapes.size() == 0)
			{
			defaultAttributes = defaultAttributes.setAttributeValue(styleName, Utility.toString(flag));
			if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
				shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);

			if (toolBarManager != null)
				toolBarManager.setNewShapeAttributes(defaultAttributes);
			}
		else
			{
			setSelectedShapesAttribute(styleName, Utility.toString(flag));
			if ((shapeAttributesDialog != null) && (shapeAttributesDialog.isVisible()))
					{
					shapeAttributesDialog.setDisplayedAttributes(
						selectedShapes.elementAt(0).getAttributes());
					}
			}
		}

	protected	void		setSelectedShapesAttributes(MiAttributes atts)
		{
		modifyAttributesOfPartsCommand.setNewAttributes(atts);
		modifyAttributesOfPartsCommand.processCommand(Mi_MODIFY_ATTRIBUTES_COMMAND_NAME);
		if (toolBarManager != null)
			{
			toolBarManager.setNewShapeAttributes(atts);
			}
		}
	protected	void		setSelectedShapesAttribute(String name, String value)
		{
		MiParts parts = getEditor().getSelectedParts(new MiParts());
		modifyAttributesOfPartsCommand.modifyAttributes(getEditor(), name, value, parts);
		if (toolBarManager != null)
			{
			toolBarManager.setNewShapeAttributes(parts.get(0).getAttributes());
			}
		}
	protected	void		setSelectedShapesAttribute(String name, Object value)
		{
		MiParts parts = getEditor().getSelectedParts(new MiParts());
		modifyAttributesOfPartsCommand.modifyAttributes(getEditor(), name, value, parts);
		if (toolBarManager != null)
			{
			toolBarManager.setNewShapeAttributes(parts.get(0).getAttributes());
			}
		}
	protected	void		setSelectedShapesAttribute(String name, int value)
		{
		MiParts parts = getEditor().getSelectedParts(new MiParts());
		modifyAttributesOfPartsCommand.modifyAttributes(getEditor(), name, value, parts);
		if (toolBarManager != null)
			{
			toolBarManager.setNewShapeAttributes(parts.get(0).getAttributes());
			}
		}
	protected	void		setSelectedShapesAttribute(String name, double value)
		{
		MiParts parts = getEditor().getSelectedParts(new MiParts());
		modifyAttributesOfPartsCommand.modifyAttributes(getEditor(), name, value, parts);
		if (toolBarManager != null)
			{
			toolBarManager.setNewShapeAttributes(parts.get(0).getAttributes());
			}
		}
	protected	void		setSelectedShapesAttribute(String name, boolean value)
		{
		MiParts parts = getEditor().getSelectedParts(new MiParts());
		modifyAttributesOfPartsCommand.modifyAttributes(getEditor(), name, value, parts);
		if (toolBarManager != null)
			{
			toolBarManager.setNewShapeAttributes(parts.get(0).getAttributes());
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_NO_ITEMS_SELECTED_ACTION))
			{
			handleSelectionState(0);
			}
		else if (action.hasActionType(Mi_ONE_ITEM_SELECTED_ACTION))
			{
			handleSelectionState(1);
			}
		else if (action.hasActionType(Mi_MANY_ITEMS_SELECTED_ACTION))
			{
			handleSelectionState(2); // takes too long when selecting area containing many items, one at a time getEditor().getNumberOfPartsSelected());
			}
		return(true);
		}
	protected	void		updateToolBarAttributes(MiAttributes atts)
		{
		updatingToolBarAttributes = true;

		fontOptionMenu.setValue(atts.getFont().getName());
		fontPointSizeOptionMenu.setFontPointSizeValue(atts.getFontPointSize());
		boldFontToggleButton.setValue(atts.isFontBold() ? "true" : "false");
		italicFontToggleButton.setValue(atts.isFontItalic() ? "true" : "false");
		underLineFontToggleButton.setValue(atts.getFont().isUnderlined() ? "true" : "false");
		strikeThruFontToggleButton.setValue(atts.getFont().isStrikeOut() ? "true" : "false");
		colorOptionMenu.setColorValue(atts.getColor());
		bgColorOptionMenu.setColorValue(atts.getBackgroundColor());
		lineWidthOptionMenu.setLineWidthValue(atts.getLineWidth());
		lineEndsOptionMenu.setLineStartStyle(atts.getLineStartStyle());
		lineEndsOptionMenu.setLineEndStyle(atts.getLineEndStyle());
		borderLookOptionMenu.setBorderLookValue(atts.getBorderLook());

		updatingToolBarAttributes = false;
		}
	protected	void		handleSelectionState(int num)
		{
		selectedShapes.removeAllElements();
		if (num == 0)
			{
			if (shapeAttributesDialog != null)
				{
				shapeAttributesDialog.setDisplayedAttributes(defaultAttributes);
				shapeAttributesDialog.setTargetShapes(selectedShapes);
				}
			updateToolBarAttributes(defaultAttributes);
			}
		else
			{
			getEditor().getSelectedParts(selectedShapes);
			if (shapeAttributesDialog != null)
				{
				shapeAttributesDialog.setTargetShapes(selectedShapes);
				}
			updateToolBarAttributes(selectedShapes.elementAt(0).getAttributes());
			}
		}
	}
