
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiMenuItem extends MiLabel
	{
					// calls select() or deselect() on widget to toggle state 
					// whenever this is selected
	public static final int		Mi_BINARY_STATE	= 1;
					// calls select() on widget whenever this is selected
	public static final int		Mi_N_ARY_STATE	= 2;

	private		String		label;
	private		String		accelText;
	private		MiEvent		accelEvent;
	private		MiEvent		mnemonicEvent;
	private		char		mnemonic;
	private		int		mnemonicIndex;

	private		MiPart		iconGr;
	private		MiPart		labelGr;
	private		MiPart		separatorGr;
	private		MiPart		accelGr;
	private		boolean 	isSeparator;
	private		boolean 	isCascadeMenu;
	private		int		multiState	= Mi_NONE;




					// To support copy operation
	protected			MiMenuItem()
		{
		this(null, null, null);
		}
	public				MiMenuItem(MiPart icon)
		{
		this(icon, null, null);
		}

	public				MiMenuItem(MiPart icon, String labelSpec)
		{
		this(icon, null, labelSpec);
		}
	public				MiMenuItem(MiPart icon, MiWidget widget, String labelSpec)
		{
		setVisibleContainerAutomaticLayoutEnabled(false);
		if ((labelSpec != null) 
			&& ((labelSpec.startsWith("---")) || (labelSpec.equalsIgnoreCase("Separator"))))
			{
			separatorGr = new MiLine(0.0, 0.0, 100.0, 0.0);
			separatorGr.setBorderLook(Mi_RIDGE_BORDER_LOOK);
			separatorGr.setColor(MiColorManager.black);
			appendPart(separatorGr);
			setInsetMargins(new MiMargins(0, 1, 0, 1));
			setSelectable(false);
			setSensitive(false);
			isSeparator = true;
			setStatusHelp(MiHelpInfo.ignoreThis);
			}
		else
			{
			setContents(icon, widget, labelSpec);
			}
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public				MiMenuItem(String labelSpec)
		{
		this(null, null, labelSpec);
		}
	public				MiMenuItem(MiMenu cascadingMenu, String label)
		{
		setVisibleContainerAutomaticLayoutEnabled(false);
		MiTriangle decoration = new MiTriangle();
		decoration.setBackgroundColor(MiColorManager.gray);
		decoration.setOrientation(Mi_RIGHT);
		decoration.setWidth(10);
		decoration.setHeight(8);
		MiMenuLauncherButton menuLauncherButton = new MiMenuLauncherButton(cascadingMenu, label);
		menuLauncherButton.setBorderLook(Mi_NONE);
		menuLauncherButton.setSelectedBorderLook(Mi_NONE);
		menuLauncherButton.setPickableWhenTransparent(true);
		menuLauncherButton.setSelectedBackgroundColor(menuLauncherButton.getNormalBackgroundColor());
		menuLauncherButton.setElementHJustification(Mi_LEFT_JUSTIFIED);
		MiMargins insetMargins = menuLauncherButton.getInsetMargins();
		insetMargins.left = 0;
		menuLauncherButton.setInsetMargins(0);
		menuLauncherButton.setMargins(new MiMargins(0));
		menuLauncherButton.setPopupLocation(this, Mi_UPPER_RIGHT_LOCATION);
		//menuLauncherButton.setVisibleContainerAutomaticLayoutEnabled(false);
		menuLauncherButton.setDisplaysFocusBorder(false);
		menuLauncherButton.setMouseFocusAttributes(menuLauncherButton.getMouseFocusAttributes().setHasBorderHilite(false));
		menuLauncherButton.setKeyboardFocusAttributes(menuLauncherButton.getKeyboardFocusAttributes().setHasBorderHilite(false));
		menuLauncherButton.insertEventHandler(
			new MiInterceptEvent(new MiEvent(Mi_LEFT_MOUSE_CLICK_EVENT), Mi_CONSUME_EVENT), 0);

		//setSelectable(false);
		//setSelectedAttributes(menuLauncherButton.getSelectedAttributes().setBackgroundColor(Mi_TRANSPARENT_COLOR));
		appendEventHandler(new MiDelegateEvents(menuLauncherButton));
		setContents(null, menuLauncherButton, null);
		accelGr = decoration;
		appendPart(accelGr);
		isCascadeMenu = true;

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public		void		setBackgroundColor(java.awt.Color c)
		{
		MiDebug.printStackTrace(this + "setBackgroundColor:" + c);
		super.setBackgroundColor(c);
		}
	public		boolean		isSeparator()
		{
		return(isSeparator);
		}
	public		boolean		isCascadeMenu()
		{
		return(isCascadeMenu);
		}
	public		void		setMultiState(int state)
		{
		multiState = state;
		}
	public		void		select(boolean flag)
		{
		if (!isSensitive())
			return;

		if ((multiState == Mi_BINARY_STATE) && (labelGr != null))
			{
			flag = !labelGr.isSelected();
			setSelectedBackgroundColor(getNormalBackgroundColor());
			}

		super.select(flag);
		if (flag == isSelected())
			{
			if (labelGr != null)
				{
				labelGr.select(flag);
				}

			if (multiState == Mi_BINARY_STATE)
				{
				if ((labelGr == null) || (labelGr instanceof MiText))
					{
					if (flag)
						setBorderLook(Mi_INDENTED_BORDER_LOOK);
					else
						setBorderLook(Mi_NONE);
					}
				if (flag)
					dispatchAction(Mi_ACTIVATED_ACTION);
				}
			else if ((flag) && (multiState == Mi_NONE))
				{
				dispatchAction(Mi_ACTIVATED_ACTION);
				// If was desensitized...
				if (!isSensitive())
					{
					setSensitive(true);
					select(false);
					setSensitive(false);
					}
				else
					{
					select(false);
					}
				}
			}
		}
			// Parse string of form: "&Open\t^O" to get accel and mnemonic
	private		void		parseLabelSpec(String labelSpec)
		{
		String spec = labelSpec;
		int startIndex = 0;
		while ((mnemonicIndex = spec.indexOf('&', startIndex)) != -1)
			{
			if ((mnemonicIndex == 0) || (spec.charAt(mnemonicIndex - 1) != '\\'))
				{
				mnemonicEvent = new MiEvent();
				mnemonic = spec.charAt(mnemonicIndex + 1);
				mnemonicEvent.modifiers = MiEvent.Mi_ANY_MODIFIERS_HELD_DOWN;
				mnemonicEvent.type = MiEvent.Mi_KEY_EVENT;
				mnemonicEvent.key = mnemonic;
				String tmp = new String();
				if (mnemonicIndex > 0)
					tmp = spec.substring(0, mnemonicIndex);
				spec = tmp.concat(spec.substring(mnemonicIndex + 1));
				break;
				}
			spec = spec.substring(0, mnemonicIndex - 1) + spec.substring(mnemonicIndex);
			startIndex = mnemonicIndex;
			}
		int index = 0;
		startIndex = 0;
		while ((index = spec.indexOf('\t', startIndex)) != -1)
			{
			if ((index == 0) || (spec.charAt(index - 1) != '\\'))
				{
				accelEvent = new MiEvent();
				accelText = spec.substring(index + 1);
				spec = spec.substring(0, index);
				if (!MiEvent.stringToEvent(accelText, accelEvent))
					MiDebug.printlnError("MiMenuItem: Unable to generate event from text: \"" + accelText + "\" in menu item: " + labelSpec);

				if (accelText.toUpperCase().indexOf("SHIFT") == -1)
					{
					accelEvent.modifiers &= ~MiEvent.Mi_SHIFT_KEY_HELD_DOWN;
					}
				break;
				}
			spec = spec.substring(0, index - 1) + spec.substring(index);
			startIndex = index;
			}
		label = spec;
		}

	protected	MiMenuLauncherButton getCascadeMenuLauncherButton()
		{
		if (isCascadeMenu)
			return((MiMenuLauncherButton )labelGr);
		return(null);
		}
	public		void		setContents(MiPart icon, MiWidget widget, String labelSpec)
		{
		if (labelSpec != null)
			labelSpec = MiSystem.getProperty(labelSpec, labelSpec);

		setColor(MiColorManager.transparent);
		setBorderLook(Mi_NONE);

		if (labelSpec != null)
			parseLabelSpec(labelSpec);
		if (icon != null)
			{
			appendPart(icon);
			iconGr = icon;
			}
		if ((label != null) && (widget == null))
			{
			MiText textGr = makeMiText(label);
			textGr.setName("label");
			if (mnemonic != '\0')
				textGr.setUnderlineLetter(mnemonicIndex);
			setName(label);
			labelGr = textGr;
			appendPart(labelGr);
			}
		if (widget != null)
			{
			labelGr = widget;
			labelGr.setName("label");
			appendPart(labelGr);
			}
		if (accelText != null)
			{
			accelGr = makeMiText(accelText);
			accelGr.setName("accelerator-text");
			appendPart(accelGr);
			}

		}
	public		MiPart		getGraphics()
		{
		return(this);
		}
	public		MiPart		getIconGraphics()
		{
		return(iconGr);
		}
	public		void		setIconGraphics(MiPart icon)
		{
		if (iconGr != null)
			removePart(iconGr);
	
		iconGr = icon;

		if (iconGr != null)
			appendPart(iconGr);
		}
	public		MiPart		getLabelGraphics()
		{
		return(labelGr);
		}
	public		MiPart		getAcceleratorGraphics()
		{
		return(accelGr);
		}
	public		MiPart		getSeparatorGraphics()
		{
		return(separatorGr);
		}
	public		String		getValue()
		{
		return(getLabelText());
		}
	public		void		setValue(String label)
		{
		setLabel(label);
		}
	public		String		getLabelText()
		{
		if (labelGr == null)
			return(null);
		if ((labelGr instanceof MiLabeledWidget) && (((MiLabeledWidget )labelGr).getLabel() != null))
			return(((MiLabeledWidget )labelGr).getLabel().getText());
		if (!(labelGr instanceof MiText))
			return(null);
		return(((MiText )labelGr).getText());
		}
	public		void		setLabel(String label)
		{
		if ((labelGr == null) || (!(labelGr instanceof MiText)))
			labelGr = makeMiText(label);
		else
			((MiText )labelGr).setText(label);
		}
	public		String		getAcceleratorText()
		{
		if (accelGr != null)
			{
			if (accelGr instanceof MiText)
				return(((MiText )accelGr).getText());
			if (accelGr instanceof MiWidget)
				return(((MiWidget )accelGr).getValue());
			}
		return(null);
		}
	public		MiEvent		getAcceleratorEvent()
		{
		return(accelEvent);
		}
	public		MiEvent		getMnemonicEvent()
		{
		return(mnemonicEvent);
		}
	public		void		setSensitive(boolean flag) 		
		{
		super.setSensitive(flag);
		if (iconGr != null)
			iconGr.setSensitive(flag);
		if (labelGr != null)
			labelGr.setSensitive(flag);
		if (accelGr != null)
			accelGr.setSensitive(flag);
		}
	protected	MiText		makeMiText(String string)
		{
		MiText text = new MiText(string);
		MiToolkit.setAttributes(text, MiiToolkit.Mi_TOOLKIT_MENU_TEXT_ATTRIBUTES);
		return(text);
		}
	public		void		refreshLookAndFeel()
		{
		if (labelGr instanceof MiText)
			{
			MiToolkit.setAttributes(labelGr, MiiToolkit.Mi_TOOLKIT_MENU_TEXT_ATTRIBUTES);
			}
		else if ((labelGr instanceof MiLabeledWidget) 
			&& (((MiLabeledWidget )labelGr).getLabel() instanceof MiText))
			{
			MiToolkit.setAttributes(((MiLabeledWidget )labelGr).getLabel(), 
				MiiToolkit.Mi_TOOLKIT_MENU_TEXT_ATTRIBUTES);
			}
		if (accelGr instanceof MiText)
			{
			MiToolkit.setAttributes(accelGr, MiiToolkit.Mi_TOOLKIT_MENU_TEXT_ATTRIBUTES);
			}

		super.refreshLookAndFeel();
		}
	public		String		toString()
		{
		return(super.toString() + "[" + label + "]");
		}
	}

