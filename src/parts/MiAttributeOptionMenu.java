
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public abstract class MiAttributeOptionMenu extends MiWidget 
				implements MiiActionHandler, MiiCommandHandler, MiiCommandNames
	{
	private static final int DECORATION_WIDTH	= 10;
	private static final int DECORATION_HEIGHT	= 10;

	private		MiPart			attrIcon;
	private		MiTextField		attrNameField;
	private		MiPart			attrDisplayField;
	private		MiMenuLauncherButton	menuLauncherButton;
	private		MiPart 			decoration;
	private		MiMenu			menu;
	private		MiWidget		menuContents;
	private		boolean			clickCyclesAttrsNotPopupOptions 	= true;

	protected static final String		Mi_CYCLE_ATTRIBUTE_VALUE_FORWARD_COMMAND_NAME
							= "cycleAttrValueForward";
	protected static final String		Mi_CYCLE_ATTRIBUTE_VALUE_BACKWARD_COMMAND_NAME
							= "cycleAttrValueBackward";
	protected static final String		Mi_POPUP_OPTIONS_COMMAND_NAME 	= "popupOptions";


	public				MiAttributeOptionMenu()
		{
		}
	protected	void		build(
						boolean displayAttributeIcon,
						boolean displayAttributeValueName,
						boolean displayMenuLauncherButton)
		{
		setBorderLook(Mi_INDENTED_BORDER_LOOK);
		setVisibleContainerAutomaticLayoutEnabled(false);
		setInsetMargins(new MiMargins(6,1,1,1));

		menuContents = makeMenuContents();
		menu = new MiMenu(menuContents);
		menu.setInsetMargins(2);

		decoration = makeDecoration();
		menuLauncherButton = new MiMenuLauncherButton(menu, decoration);
		menuLauncherButton.setPopupLocation(this, Mi_LOWER_LEFT_LOCATION);
		menuLauncherButton.addPopupLocation(this, Mi_UPPER_LEFT_LOCATION, Mi_LOWER_LEFT_LOCATION);
		menuLauncherButton.setAcceptingEnterKeyFocus(false);
		menuLauncherButton.setAcceptingMouseFocus(false);
		menuLauncherButton.setInsetMargins(4);
		menuLauncherButton.setBackgroundColor(getBackgroundColor());
		
		attrNameField = new MiTextField(true);
		// Auto expand text field from 40 pixels up to 16 characters wide
		attrNameField.setNumDisplayedColumns(MiTextField.Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT);
		attrNameField.setMaxNumDisplayedColumns(16);
		attrNameField.setMinimumSize(new MiSize(40, 5));
		attrNameField.setBackgroundColor(MiColorManager.white);
		attrNameField.setBorderLook(Mi_NONE);
		attrNameField.setAcceptingMouseFocus(false);
		attrNameField.setMargins(new MiMargins(2,0,1,0));
		attrNameField.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);

		setBorderLook(Mi_INLINED_INDENTED_BORDER_LOOK);
		setNormalColor(getToolkit().getTextSensitiveColor());
		setInSensitiveColor(getToolkit().getTextInSensitiveColor());
		setNormalBackgroundColor(getToolkit().getTextFieldEditableBGColor());
		setInSensitiveBackgroundColor(getToolkit().getTextFieldInEditableBGColor());

		MiRowLayout layout = new MiRowLayout();
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		layout.setElementVJustification(Mi_CENTER_JUSTIFIED);
		layout.setElementVSizing(Mi_NONE);
		layout.setLastElementJustification(Mi_RIGHT_JUSTIFIED);

		setLayout(layout);
		attrDisplayField = makeAttributeDisplayField();

		attrIcon = makeAttributeIcon();
		if (attrIcon != null)
			{
			appendPart(attrIcon);
			}
		if (attrDisplayField != null)
			{
			attrDisplayField.appendCommandHandler(this, Mi_POPUP_OPTIONS_COMMAND_NAME, 
				new MiEvent(MiEvent.Mi_LEFT_MOUSE_DBLCLICK_EVENT, 0, 0));
			appendPart(attrDisplayField);
			}
		appendPart(attrNameField);
		appendPart(menuLauncherButton);

		if ((!displayAttributeIcon) && (attrIcon != null))
			attrIcon.setVisible(false);
		if (!displayAttributeValueName)
			attrNameField.setVisible(false);
		if (!displayMenuLauncherButton)
			menuLauncherButton.setVisible(false);

		if ((!displayAttributeValueName) || ((!displayAttributeIcon) && (attrIcon != null)))
			layout.setAlleyHSpacing(3);
		else
			layout.setAlleyHSpacing(4);

		MiComboBoxPopperKeyEventHandler handler = new MiComboBoxPopperKeyEventHandler();
		handler.setMenuLauncherButton(menuLauncherButton);

		if ((clickCyclesAttrsNotPopupOptions) && (attrDisplayField != null))
			{
			// Click on anywhere in the background of this option box cycles 
			// the attributes to the next value
			insertEventHandler(
				new MiIExecuteCommand(
					new MiEvent(Mi_LEFT_MOUSE_DOWN_EVENT, 0, 0), 
					this, 
					Mi_CYCLE_ATTRIBUTE_VALUE_FORWARD_COMMAND_NAME,
					Mi_CONSUME_EVENT), 
				0);
/*
			attrDisplayField.insertEventHandler(
				new MiIExecuteCommand(
					new MiEvent(Mi_LEFT_MOUSE_DOWN_EVENT, 0, 0), 
					this, 
					Mi_CYCLE_ATTRIBUTE_VALUE_FORWARD_COMMAND_NAME,
					Mi_CONSUME_EVENT), 
				0);
*/
			attrDisplayField.insertEventHandler(
				new MiIExecuteCommand(
					new MiEvent(Mi_KEY_EVENT, Mi_RIGHT_ARROW_KEY, 0), 
					this, 
					Mi_CYCLE_ATTRIBUTE_VALUE_FORWARD_COMMAND_NAME,
					Mi_CONSUME_EVENT), 
				0);
			attrDisplayField.insertEventHandler(
				new MiIExecuteCommand(
					new MiEvent(Mi_KEY_EVENT, Mi_LEFT_ARROW_KEY, 0), 
					this, 
					Mi_CYCLE_ATTRIBUTE_VALUE_BACKWARD_COMMAND_NAME,
					Mi_CONSUME_EVENT), 
				0);
			}
		else
			{
			handler.addEventToCommandTranslation(
				MiEventHandler.Mi_EXECUTE_COMMAND_NAME, MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
			}
		appendEventHandler(handler);
		}

	public		void		setContents(Strings contents)
		{
		menuContents.setContents(contents);
		}
	public		Strings		getContents()
		{
		return(menuContents.getContents());
		}



	protected abstract MiPart	makeAttributeIcon();
	protected abstract MiPart	makeAttributeDisplayField();
	protected abstract MiWidget	makeMenuContents();
	protected abstract void		cycleAttributeValueForward();
	protected abstract void		cycleAttributeValueBackward();
	protected abstract boolean	updateValueFromPopupMenu(MiiAction action);



	public		MiPart		getAttributeIconField()
		{
		return(attrIcon);
		}
	public		MiPart		getAttributeDisplayField()
		{
		return(attrDisplayField);
		}
	public		MiTextField	getAttributeValueNameField()
		{
		return(attrNameField);
		}
	public		MiMenu		getMenu()
		{
		return(menu);
		}
	public		MiWidget	getMenuContents()
		{
		return(menuContents);
		}
	public		MiMenuLauncherButton	getMenuLauncherButton()
		{
		return(menuLauncherButton);
		}

	public		void		setSensitive(boolean flag)
		{
		super.setSensitive(flag);
		if (menuLauncherButton != null)
			menuLauncherButton.setSensitive(flag);
		if (getAttributeValueNameField() != null)
			getAttributeValueNameField().setSensitive(flag);
		if (getAttributeIconField() != null)
			getAttributeIconField().setSensitive(flag);
		if (getAttributeDisplayField() != null)
			getAttributeDisplayField().setSensitive(flag);
		}
	public		boolean		processAction(MiiAction action)
		{
		if ((action.hasActionType(Mi_VALUE_CHANGED_ACTION))
			|| (action.hasActionType(Mi_ACTIVATED_ACTION)))
			{
			if (action.getActionSource() == attrNameField)
				{
				if ((getValue() == null) || (!getValue().equals(attrNameField.getValue())))
					{
					setValue(attrNameField.getValue());
					dispatchAction(Mi_VALUE_CHANGED_ACTION);
					}
				}
			else 
				{
				if (updateValueFromPopupMenu(action))
					dispatchAction(Mi_VALUE_CHANGED_ACTION);
				}
			menu.popdown();
			}
		return(true);
		}
	public		void		processCommand(String cmd)
		{
		if (cmd.equals(Mi_POPUP_OPTIONS_COMMAND_NAME))
			{
			cycleAttributeValueBackward();
			menuLauncherButton.select(true);
			return;
			}
		else if (cmd.equals(Mi_CYCLE_ATTRIBUTE_VALUE_FORWARD_COMMAND_NAME))
			{
			cycleAttributeValueForward();
			}
		else if (cmd.equals(Mi_CYCLE_ATTRIBUTE_VALUE_BACKWARD_COMMAND_NAME))
			{
			cycleAttributeValueBackward();
			}
		else if (cmd.equals(Mi_POPDOWN_COMMAND_NAME))
			{
			}
		menu.popdown();
		}
	private		MiPart	makeDecoration()
		{
		MiTriangle decoration = new MiTriangle();
		decoration.setBackgroundColor(MiColorManager.gray);
		decoration.setOrientation(Mi_DOWN);
		decoration.setWidth(DECORATION_WIDTH);
		decoration.setHeight(DECORATION_HEIGHT);
		return(decoration);
		}
	}

