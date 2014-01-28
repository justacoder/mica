
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
import java.awt.Image;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiComboList extends MiWidget implements MiiActionHandler
	{
	private		MiTextField	textField;
	private		MiList		scrolledList;
	private		boolean		restrictingValuesToThoseInList = true;



	public				MiComboList()
		{
		MiColumnLayout layout = new MiColumnLayout();
		layout.setAlleyVSpacing(1);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);

		textField = new MiTextField();
		textField.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION | Mi_REQUEST_ACTION_PHASE);
		textField.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		textField.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		textField.appendActionHandler(this, Mi_TEXT_CHANGE_ACTION);
		scrolledList = new MiList();
		scrolledList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		scrolledList.setInsetMargins(0);
		scrolledList.setNormalBackgroundColor(getToolkit().getTextFieldEditableBGColor());
		scrolledList.setInSensitiveBackgroundColor(getToolkit().getTextFieldInEditableBGColor());

		appendPart(textField);
		appendPart(new MiScrolledBox(scrolledList));
		}
	public		MiList	getList()
		{
		return(scrolledList);
		}
	public		MiTextField	getTextField()
		{
		return(textField);
		}
	public		void		setSensitive(boolean flag)
		{
		super.setSensitive(flag);
		textField.setSensitive(flag);
		scrolledList.setSensitive(flag);
		}
	public		void		setValue(String value)
		{
		if (restrictingValuesToThoseInList)
			{
			if ((scrolledList.getIndexOfItem(value) == -1)
				&& (scrolledList.getNumberOfItems() > 0))
				{
				return;
				}
			}
		textField.setValue(value);
		scrolledList.setValue(value);
		}
	public		String		getValue()
		{
		return(textField.getValue());
		}
	public		void		setContents(Strings contents)
		{
		scrolledList.setContents(contents);
		if (restrictingValuesToThoseInList)
			restrictValuesToThoseInList();
		}
	public		Strings		getContents()
		{
		return(scrolledList.getContents());
		}
	public		void		setRestrictingValuesToThoseInList(boolean flag)
		{
		restrictingValuesToThoseInList = flag;
		}
	public		boolean		isRestrictingValuesToThoseInList()
		{
		return(restrictingValuesToThoseInList);
		}
	protected	void		restrictValuesToThoseInList()
		{
		String value = textField.getValue();
		if ((scrolledList.getIndexOfItem(value) == -1) && (scrolledList.getNumberOfItems() > 0))
			textField.setValue(scrolledList.getStringItem(0));
		}
			
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			textField.setValue(scrolledList.getSelectedItem());
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else if (action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			if (restrictingValuesToThoseInList)
				{
				String value = textField.getValue();
				if ((scrolledList.getIndexOfItem(value) == -1)
					&& (scrolledList.getNumberOfItems() > 0))
					{
					action.veto();
					dispatchAction(Mi_INVALID_VALUE_ACTION);
					//textField.getContainingWindow().requestKeyboardFocus(textField);
					return(true);
					}
				}
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else if ((action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION))
			|| (action.hasActionType(Mi_ENTER_KEY_ACTION)))
			{
			dispatchAction(Mi_VALUE_CHANGED_ACTION);

			if (scrolledList.getIndexOfItem(textField.getValue()) == -1)
				scrolledList.getSelectionManager().deSelectAll();
			else
				scrolledList.setValue(textField.getValue());
			}
		else if (action.hasActionType(Mi_TEXT_CHANGE_ACTION))
			{
			dispatchAction(Mi_TEXT_CHANGE_ACTION);
			}
		return(true);
		}
	}


