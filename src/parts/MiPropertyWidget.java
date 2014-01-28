
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
public class MiPropertyWidget
	{
	public static final String	Mi_TEXTFIELD_WIDGET_TYPE_NAME	= "TextField";
	public static final String	Mi_COMBOBOX_WIDGET_TYPE_NAME	= "ComboBox";
	public static final String	Mi_OPTIONMENU_WIDGET_TYPE_NAME	= "OptionMenu";
	public static final String	Mi_LABEL_WIDGET_TYPE_NAME	= "Label";
	public static final String	Mi_SCROLLEDLIST_WIDGET_TYPE_NAME= "ScrolledList";
	public static final String	Mi_TEXTAREA_WIDGET_TYPE_NAME	= "TextArea";

	private		String			name;
	private		String			displayName;
	private		String			defaultValue	= "";
	private		String			widgetType;
	private		String			statusHelpMsg;
	private		String			dialogHelpMsg;
	private		Strings			possibleValues;
	private		boolean			readOnly;
	private		int			numDisplayedColumns;
	private		int			maxNumColumns;
	private		String			value;	// Revert value, undoValue, lastCommitedValue
	private		MiWidget		widget;
	private		MiWidgetAttributes	originalWidgetAttributes;
	private		MiAttributes		originalAttributes;
	private		boolean			hasValidationError;


	public				MiPropertyWidget(String name, String defaultValue)
		{
		this(name, defaultValue, Mi_TEXTFIELD_WIDGET_TYPE_NAME, null, false);
		}
	public				MiPropertyWidget(String name, MiWidget widget)
		{
		this.name = name;
		this.displayName = name;
		this.widget= widget;
		}
	public				MiPropertyWidget(String name, String defaultValue, String widgetType, Strings possibleValues, boolean readOnly)
		{
		this.name = name;
		this.displayName = name;
		this.defaultValue = defaultValue;
		this.widgetType = widgetType;
		this.possibleValues = possibleValues;
		this.readOnly = readOnly;
		}
	public		String		getName()
		{
		return(name);
		}
	public		String		getDefaultValue()
		{
		return(defaultValue);
		}
	public		void		setDisplayName(String displayName)
		{
		this.displayName = displayName;
		}
	public		String		getDisplayName()
		{
		return(displayName);
		}
	public		void		setNumDisplayedColumns(int num)
		{
		numDisplayedColumns = num;
		}
	public		int		getNumDisplayedColumns()
		{
		return(numDisplayedColumns);
		}
	public		void		setMaxNumColumns(int num)
		{
		maxNumColumns = num;
		}
	public		int		getMaxNumColumns()
		{
		return(maxNumColumns);
		}
	public		void		setPossibleValues(Strings values)
		{
		if ((widgetType.equalsIgnoreCase(Mi_COMBOBOX_WIDGET_TYPE_NAME))
			|| (widgetType.equalsIgnoreCase(Mi_OPTIONMENU_WIDGET_TYPE_NAME))
			|| (widgetType.equalsIgnoreCase(Mi_SCROLLEDLIST_WIDGET_TYPE_NAME)))
			{
			if (widget != null)
				widget.setContents(values);
			else
				possibleValues = values;
			}
		}
	public		void		setSensitive(boolean flag)
		{
		readOnly = !flag;
		if (widget != null)
			widget.setSensitive(flag);
		}
	public		void		setStatusHelpMessage(String msg)
		{
		if (widget != null)
			widget.setStatusHelpMessage(msg);
		else
			statusHelpMsg = msg;
		}
	public		void		setDialogHelpMessage(String msg)
		{
		if (widget != null)
			widget.setDialogHelpMessage(msg);
		else
			dialogHelpMsg = msg;
		}
	public		MiWidget	makeWidget()
		{
		if ((widgetType == null)
			|| (widgetType.equalsIgnoreCase(Mi_TEXTFIELD_WIDGET_TYPE_NAME)))
			{
			widget = new MiTextField();
			if (numDisplayedColumns != 0)
				((MiTextField )widget).setNumDisplayedColumns(numDisplayedColumns);
			if (maxNumColumns != 0)
				((MiTextField )widget).setMaxNumCharacters(maxNumColumns);
			}
		else if (widgetType.equalsIgnoreCase(Mi_LABEL_WIDGET_TYPE_NAME))
			{
			widget = new MiLabel();
			}
		else if (widgetType.equalsIgnoreCase(Mi_COMBOBOX_WIDGET_TYPE_NAME))
			{
			MiComboBox combo = new MiComboBox();
			if (possibleValues != null)
				combo.getList().appendItems(possibleValues);
			widget = combo;
			}
		else if (widgetType.equalsIgnoreCase(Mi_OPTIONMENU_WIDGET_TYPE_NAME))
			{
			MiOptionMenu menu = new MiOptionMenu();
			if (possibleValues != null)
				menu.appendItems(possibleValues);
			widget = menu;
			}
		else if (widgetType.equalsIgnoreCase(Mi_SCROLLEDLIST_WIDGET_TYPE_NAME))
			{
			MiList list = new MiList(4, false);
			MiScrolledBox box = new MiScrolledBox(list);
			box.setVScrollBarDisplayPolicy(MiiTypes.Mi_DISPLAY_ALWAYS);
			if (possibleValues != null)
				list.appendItems(possibleValues);
			widget = box;
			}
		else
			{
			widget = new MiTextField();
			}

		if (readOnly)
			widget.setSensitive(false);
		if (defaultValue != null)
			widget.setValue(defaultValue);
		if (dialogHelpMsg != null)
			widget.setDialogHelpMessage(dialogHelpMsg);
		if (statusHelpMsg != null)
			widget.setStatusHelpMessage(statusHelpMsg);
		else
			widget.setStatusHelp(MiHelpInfo.noneForThis);

		return(widget);
		}
	public		boolean		hasName(String name)
		{
		return(this.name.equalsIgnoreCase(name));
		}
	public		void		setUndoableValue(String value)
		{
		if (!(widget instanceof MiPushButton))
			{
			widget.setValue(value);
			}
		}
	public		void		setValue(String value)
		{
		if (!(widget instanceof MiPushButton))
			{
			widget.setValue(value);
			// Also set the 'undo' value
			this.value = value;
			}
		}
	public		String		getValue()
		{
		return(widget.getValue());
		}
	public		String		getRevertValue()
		{
		return(value);
		}
	public		MiWidget	getWidget()
		{
		return(widget);
		}
	public		void		setContents(Strings values)
		{
		widget.setContents(values);
		}
	public		boolean		hasChanged()
		{
		if (Utility.isEmptyOrNull(widget.getValue()) || (Utility.isEmptyOrNull(value)))
			{
			if (Utility.isEmptyOrNull(widget.getValue()) && (Utility.isEmptyOrNull(value)))
				return(false);
			else
				return(true);
			}
		return(!widget.getValue().equals(value));
		}
	public		void		undo()	// undo
		{
		widget.setValue(value);
		//setHasValidationError(false);
		}
	public		void		copyWidgetValueToRevertCache() // commit
		{
		value = widget.getValue();
		}
	public		void		setHasValidationError(boolean flag)
		{
		if ((flag) && (!hasValidationError))
			{
			originalAttributes = widget.getAttributes();
			originalWidgetAttributes = widget.getWidgetAttributes();
			}
		else if ((!flag) && (hasValidationError))
			{
			widget.setAttributes(originalAttributes);
			widget.setWidgetAttributes(originalWidgetAttributes);
			}
		hasValidationError = flag;
		}
	public		boolean		hasValidationError()
		{
		return(hasValidationError);
		}
	public		String		toString()
		{
		return(super.toString() + "[" + name + "=" + getValue() 
			+ "<revertValue = " + value + ">}");
		}
	
	}


