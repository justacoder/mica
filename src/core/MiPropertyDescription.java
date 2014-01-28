
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

import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.OrderableHashtable; 
import java.util.Hashtable; 
import java.util.Vector; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPropertyDescription implements MiiTypes, MiiPropertyTypes, Cloneable
	{
	private		String		name;
	private		String		displayName;
	private		String		defaultValue;
	private		int		valueType;
	private		int		valueGeometricType;
	private		Strings		validValues;
	private		Strings		validDisplayValues;
	private		Strings		excludedValues;
	private		boolean		validValuesAreSuggestionsOnly;
	private		boolean		validValuesAreSpecialCasesOnly;
	private		boolean		minimumValueSpecified;
	private		boolean		maximumValueSpecified;
	private		boolean		stepValueSpecified;
	private		double		minimumValue;
	private		double		maximumValue;
	private		double		stepValue;
	private		MiWidget	customEditor;
	private		Vector		valueValidators;
	private		MiiUnits	units;
	private		boolean		requiresNonNullNonEmptyValue;
	private		boolean		valuesIgnoreCase;
	private		boolean		displayValuesIgnoreCase;
	private		boolean		editable		= true;
	private		boolean		viewable		= true;
	private		int		displayPriority;
	private		Strings		editingPermissionsClasses;
	private		Strings		viewingPermissionsClasses;
	private		MiiHelpInfo	toolHint;
	private		MiiHelpInfo	statusHelp;
	private		MiiHelpInfo	dialogHelp;
	private		OrderableHashtable	additionalProperties;


	public				MiPropertyDescription(String displayName, int type)
		{
		this.displayName = displayName;
		this.name = Utility.replaceAll(displayName, " ", "");
		setType(type);
		}
	public				MiPropertyDescription(String displayName, int type, String defaultValue)
		{
		this.displayName = displayName;
		this.name = Utility.replaceAll(displayName, " ", "");
		setType(type);
		this.defaultValue = defaultValue;
		}
	public				MiPropertyDescription(String displayName, int type, String defaultValue, int geometricType)
		{
		this.displayName = displayName;
		this.name = Utility.replaceAll(displayName, " ", "");
		setType(type);
		this.valueGeometricType = geometricType;
		this.defaultValue = defaultValue;
		}

	public				MiPropertyDescription(String displayName, Strings values)
		{
		this.displayName = displayName;
		this.name = Utility.replaceAll(displayName, " ", "");
		validValues = new Strings(values);
		valueType = Mi_STRING_TYPE;
		}
	public				MiPropertyDescription(String displayName, Strings values, String defaultValue)
		{
		this.displayName = displayName;
		this.name = Utility.replaceAll(displayName, " ", "");
		validValues = new Strings(values);
		valueType = Mi_STRING_TYPE;
		this.defaultValue = defaultValue;
		}

					// For Object types
	public				MiPropertyDescription(String displayName, String value)
		{
		this.displayName = displayName;
		this.name = Utility.replaceAll(displayName, " ", "");
		validValues = new Strings(value);
		valueType = Mi_OBJECT_TYPE;
		}


	public		void		setType(int type)
		{
		valueType = type;
		if (type == Mi_POSITIVE_INTEGER_TYPE)
			setMinimumValue(0.0);
		else if (type == Mi_POSITIVE_DOUBLE_TYPE)
			setMinimumValue(0.0);
		}
	public		int		getType()
		{
		return(valueType);
		}

	public		void		setGeometricType(int type)
		{
		valueGeometricType = type;
		}
	public		int		getGeometricType()
		{
		return(valueGeometricType);
		}

	public		void		setName(String n)
		{
		name = n;
		}
	public		String		getName()
		{
		return(name);
		}
	public		void		setDisplayName(String name)
		{
		displayName = name;
		}
	public		String		getDisplayName()
		{
		return(displayName);
		}
	public		MiPropertyDescription	setMinimumValue(double value)
		{
		minimumValue = value;
		minimumValueSpecified = true;
		return(this);
		}
	public		double		getMinimumValue()
		{
		return(minimumValue);
		}
	public		boolean		getMinimumValueIsSpecified()
		{
		return(minimumValueSpecified);
		}
	public		MiPropertyDescription	setMaximumValue(double value)
		{
		maximumValue = value;
		maximumValueSpecified = true;
		return(this);
		}
	public		double		getMaximumValue()
		{
		return(maximumValue);
		}
	public		boolean		getMaximumValueIsSpecified()
		{
		return(maximumValueSpecified);
		}
	public		MiPropertyDescription	setStepValue(double value)
		{
		stepValue = value;
		stepValueSpecified = true;
		return(this);
		}
	public		double		getStepValue()
		{
		return(stepValue);
		}
	public		boolean		getStepValueIsSpecified()
		{
		return(stepValueSpecified);
		}

	public		void		setDefaultValue(String value)
		{
		defaultValue = value;
		}
	public		String		getDefaultValue()
		{
		return(defaultValue);
		}
	public		void		setIgnoreCase(boolean flag)
		{
		valuesIgnoreCase = flag;
		}
	public		boolean		getIgnoreCase()
		{
		return(valuesIgnoreCase);
		}
	public		void		setDisplayValuesIgnoreCase(boolean flag)
		{
		displayValuesIgnoreCase = flag;
		}
	public		boolean		getDisplayValuesIgnoreCase()
		{
		return(displayValuesIgnoreCase);
		}
					/**------------------------------------------------------
	 				 * Specifies whether specified valid values are just 
					 * commonly used values within the overall constraints 
					 * of values for the property. False is the default.
					 * The corresponding widget would be a editable combobox
					 * with the valid values pre-populated.
	 				 * @param flag 		true if suggestions
					 * @see			#setValidValues
					 * @see			#setValidValuesAreSpecialCasesOnly
					 *------------------------------------------------------*/
	public		void		setValidValuesAreSuggestionsOnly(boolean flag)
		{
		validValuesAreSuggestionsOnly = flag;
		}
	public		boolean		getValidValuesAreSuggestionsOnly()
		{
		return(validValuesAreSuggestionsOnly);
		}
					/**------------------------------------------------------
	 				 * Specifies whether specified valid values are just 
					 * commonly used values possibly violating the overall 
					 * constraints of values for the property. False is the default.
					 * The corresponding widget would be a editable combobox
					 * with the valid values pre-populated.
	 				 * @param flag 		true if special cases
					 * @see			#setValidValues
					 * @see			#setValidValuesAreSuggestionsOnly
					 *------------------------------------------------------*/
	public		void		setValidValuesAreSpecialCasesOnly(boolean flag)
		{
		validValuesAreSpecialCasesOnly = flag;
		}
	public		boolean		getValidValuesAreSpecialCasesOnly()
		{
		return(validValuesAreSpecialCasesOnly);
		}
	public		void		setCustomEditor(MiWidget widget)
		{
		customEditor = widget;
		}
	public		MiWidget	getCustomEditor()
		{
		return(customEditor);
		}
	public		void		appendPropertyValueRule(MiiValueValidator rule)
		{
		if (valueValidators == null)
			valueValidators = new Vector();
		valueValidators.addElement(rule);
		}
	public		void		insertPropertyValueRule(MiiValueValidator rule, int index)
		{
		if (valueValidators == null)
			valueValidators = new Vector();
		valueValidators.insertElementAt(rule, 0);
		}
	public		void		removePropertyValueRule(MiiValueValidator rule)
		{
		valueValidators.removeElement(rule);
		if (valueValidators.size() == 0)
			valueValidators = null;
		}
	public		int		getNumberOfPropertyValidationRules()
		{
		if (valueValidators == null)
			return(0);
		return(valueValidators.size());
		}
	public		MiiValueValidator	getPropertyValueRule(int index)
		{
		return((MiiValueValidator )valueValidators.elementAt(index));
		}


	public		void		setUnits(MiiUnits units)
		{
		this.units = units;
		}
	public		MiiUnits	getUnits()
		{
		return(units);
		}

	public		void		setRequired(boolean flag)
		{
		requiresNonNullNonEmptyValue = flag;
		}
	public		boolean		isRequired()
		{
		return(requiresNonNullNonEmptyValue);
		}

	public		MiPropertyDescription	setViewable(boolean flag)
		{
		viewable = flag;
		return(this);
		}
	public		boolean		isViewable(String usersPermissionsClass)
		{
		if (usersPermissionsClass == null)
			return(viewable);
		if (editingPermissionsClasses == null)
			return(viewable);
		return(viewingPermissionsClasses.contains(usersPermissionsClass));
		}



	public		MiPropertyDescription	setEditable(boolean flag)
		{
		editable = flag;
		return(this);
		}
	public		boolean		isEditable(String usersPermissionsClass)
		{
		if (usersPermissionsClass == null)
			return(editable);
		if (editingPermissionsClasses == null)
			return(editable);
		return(editingPermissionsClasses.contains(usersPermissionsClass));
		}


	public		void		setDisplayPriority(int priority)
		{
		displayPriority = priority;
		}
	public		int		getDisplayPriority()
		{
		return(displayPriority);
		}


	public		void		setEditingPermissionsClasses(Strings names)
		{
		editingPermissionsClasses = new Strings(names);
		}
	public		Strings		getEditingPermissionsClasses()
		{
		return(new Strings(editingPermissionsClasses));
		}
	public		void		setViewingPermissionsClasses(Strings names)
		{
		viewingPermissionsClasses = new Strings(names);
		}
	public		Strings		getViewingPermissionsClasses()
		{
		return(new Strings(viewingPermissionsClasses));
		}
	public		void		setToolHintHelp(MiiHelpInfo help)
		{
		toolHint = help;
		}
	public		MiiHelpInfo	getToolHintHelp()
		{
		return(toolHint);
		}
	public		void		setToolHintMessage(String msg)
		{
		if (msg != null)
			toolHint = new MiHelpInfo(msg);
		else
			toolHint = null;
		}
	public		void		setStatusHelp(MiiHelpInfo help)
		{
		statusHelp = help;
		}
	public		void		setStatusHelpMessage(String msg)
		{
		if (msg != null)
			statusHelp = new MiHelpInfo(msg);
		else
			statusHelp = null;
		}
	public		MiiHelpInfo	getStatusHelp()
		{
		return(statusHelp);
		}
	public		void		setDialogHelp(MiiHelpInfo help)
		{
		dialogHelp = help;
		}
	public		void		setDialogHelpMessage(String msg)
		{
		if (msg != null)
			dialogHelp = new MiHelpInfo(msg);
		else
			dialogHelp = null;
		}
	public		MiiHelpInfo	getDialogHelp()
		{
		return(dialogHelp);
		}

	public		void		setAdditionalProperty(String name, Object value)
		{
		if (additionalProperties == null)
			{
			additionalProperties = new OrderableHashtable();
			}
		if (value == null)
			{
			additionalProperties.remove(name);
			}
		else
			{
			additionalProperties.put(name, value);
			}
		}

	public		int		getNumberOfAdditionalProperties()
		{
		return(additionalProperties == null ? 0 : additionalProperties.size());
		}
	public		String		getNameOfAdditionalProperty(int index)
		{
		return(additionalProperties == null ? null : additionalProperties.getKeys().get(index));
		}
	public		Object		getAdditionalProperty(String name)
		{
		if (additionalProperties == null)
			{
			return(null);
			}
		return(additionalProperties.get(name));
		}


	public		MiValueValidationError	validateValue(String value)
		{
		if (Utility.isEmptyOrNull(value))
			{
			if (!requiresNonNullNonEmptyValue)
				{
				return(null);
				}
			return(new MiValueValidationError(this,
				Mi_VALUE_MUST_BE_SPECIFIED_VALIDATION_ERROR, value));
			}

		if (units != null)
			{
			try	{
				value = units.extractInternalValue(value);
				}
			catch (Throwable e)
				{
				MiDebug.printStackTrace(e);
				return(new MiValueValidationError(e.getMessage()));
				}
			// An equation that did not resolve into a value... i.e.. has an unresolved variable
			if (value == null)
				{
				return(null);
				}
//MiDebug.println("extractInternalValue=" + value);
			}
		if (valueValidators != null)
			{
			for (int i = 0; i < valueValidators.size(); ++i)
				{
				MiiValueValidator rule = (MiiValueValidator )valueValidators.elementAt(i);
				MiValueValidationError error = rule.validateValue(value);
				if (error != null)
					return(error);
				}
			}
		if ((excludedValues != null) && (excludedValues.contains(value, valuesIgnoreCase)))
			{
			return(new MiValueValidationError(
				this, Mi_VALUE_IN_EXCLUDED_SET_VALIDATION_ERROR, value));
			}
		if (validValues != null) // || (validValuesAreSpecialCasesOnly))
			{
			if (validValues.contains(value, valuesIgnoreCase))
				{
				return(null);
				}
			}
		if ((validValues == null) || (validValuesAreSuggestionsOnly) || (validValuesAreSpecialCasesOnly))
			{
			switch (valueType)
				{
				case Mi_FONT_NAME_TYPE :
				case Mi_STRING_TYPE :
					return(null);
				case Mi_BOOLEAN_TYPE :
					if (!Utility.isBoolean(value))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_NOT_A_BOOLEAN_VALIDATION_ERROR, value));
						}
					return(null);
				case Mi_INTEGER_TYPE :
				case Mi_POSITIVE_INTEGER_TYPE :
					if (!Utility.isInteger(value))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_NOT_AN_INTEGER_VALIDATION_ERROR, value));
						}
					{
					int val = Utility.toInteger(value);
					if ((minimumValueSpecified) && (val < minimumValue))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR, value));
						}
					if ((maximumValueSpecified) && (val > maximumValue))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR, value));
						}
					if ((stepValue != 0) 
						&& (((val - minimumValue) % stepValue) == 0))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR, value));
						}
					}
					return(null);
				case Mi_DOUBLE_TYPE :
				case Mi_POSITIVE_DOUBLE_TYPE :
					if (!Utility.isDouble(value))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_NOT_A_DOUBLE_VALIDATION_ERROR, value));
						}
					{
					double val = Utility.toDouble(value);
					if ((minimumValueSpecified) && (val < minimumValue))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR, value));
						}
					if ((maximumValueSpecified) && (val > maximumValue))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR, value));
						}
					if ((stepValue != 0) 
						&& (((val - minimumValue) % stepValue) == 0))
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR, value));
						}
					}
					return(null);
				case Mi_COLOR_TYPE :
					{
					try	{
						MiColorManager.getColor(value);
						}
					catch (Throwable t)
						{
						return(new MiValueValidationError(this,
							Mi_VALUE_NOT_A_COLOR_VALIDATION_ERROR, value));
						}
					return(null);
					}
				case Mi_OBJECT_TYPE :
					{
					if (value == null)
						return(null);

					Class c = Utility.getClass(value);
					if (c != null)
						{
						for (int i = 0; i < validValues.size(); ++i)
							{
							if (Utility.instanceOf(
								c, validValues.elementAt(i)))
								{
								return(null);
								}
							}
						}
					if (c == null)
						{
						return(new MiValueValidationError(this,
							Mi_CLASS_NAME_VALUE_NOT_FOUND_ERROR, value));
						}
					return(new MiValueValidationError(this,
						Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR, value));
					}
				case Mi_FILE_NAME_TYPE:
				case Mi_TEXT_TYPE:
					{
					return(null);
					}
				default:
					{
					throw new IllegalArgumentException(
						this + ": Invalid type: " + valueType);
					}
				}
			}
		if (!validValues.contains(value, valuesIgnoreCase))
			{
			return(new MiValueValidationError(
				this, Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR, value));
			}
		return(null);
		}

	public		int		getNumberOfValidValues()
		{
		if (validValues == null)
			return(-1);
		return(validValues.size());
		}

	public		void		setValidValues(Strings validValues)
		{
		if (validValues == null)
			{
			this.validValues = null;
			}
		else
			{
			this.validValues = new Strings(validValues);
			}
		}
	public		Strings		getValidValues()
		{
		if (validValues == null)
			return(null);
		return(new Strings(validValues));
		}
	public		void		setValidDisplayValues(Strings validDisplayValues)
		{
		if (validDisplayValues == null)
			{
			this.validDisplayValues = null;
			}
		else
			{
			this.validDisplayValues = new Strings(validDisplayValues);
			}
		}
	public		Strings		getValidDisplayValues()
		{
		if (validDisplayValues == null)
			return(null);
		return(new Strings(validDisplayValues));
		}
	public		Strings		getExcludedValues()
		{
		if (excludedValues == null)
			return(null);
		return(new Strings(excludedValues));
		}
	public		void		setExcludedValues(Strings excludedValues)
		{
		if (excludedValues == null)
			{
			this.excludedValues = null;
			}
		else
			{
			this.excludedValues = new Strings(excludedValues);
			}
		}
	public		String		convertInternalValueToDisplayValue(String value)
		{
		if ((validValues != null) && (validDisplayValues != null))
			{
			int index = -1;
			if (valuesIgnoreCase)
				{
				index = validValues.indexOfIgnoreCase(value);
				}
			else
				{
				index = validValues.indexOf(value);
				}
			if ((index != -1) && (index < validDisplayValues.size()))
				{
				return(validDisplayValues.get(index));
				}
			// convert values of null and "" to display value for default-value
			if (((value == null) || (value.length() == 0)) && (!requiresNonNullNonEmptyValue))
				{
				index = validValues.indexOf(defaultValue);
				if ((index != -1) && (index < validDisplayValues.size()))
					{
					return(validDisplayValues.get(index));
					}
				}
			}
		return(value);
		}
	public		String		convertDisplayValueToInternalValue(String value)
		{
		if ((validValues != null) && (validDisplayValues != null))
			{
			int index = -1;
			if (displayValuesIgnoreCase)
				{
				index = validDisplayValues.indexOfIgnoreCase(value);
				}
			else
				{
				index = validDisplayValues.indexOf(value);
				}
			if ((index != -1) && (index < validValues.size()))
				{
				return(validValues.get(index));
				}
			}
		return(value);
		}
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) + hashCode() + ": " 
			+ name 
			+ ", type = " + propertyTypeNames[valueType]
			+ ", default value = " + defaultValue
			+ (validValues != null ? (", valid values = " + validValues) : ""));
		}
	public		String		generateRandomValue(boolean valid)
		{
		String value = null;
		if (valid)
			{
			value = generateRandomValidValue();
			if (units != null)
				{
				//?? value = units.convertInternalValueToDisplayUnits(value);
				}
			}
		return(value);
		}

	public		int		hashCode()
		{
		return(super.hashCode());
		}
	/**
	 * A fairly expensive operation.
	 */
	public		boolean		equals(Object otherDesc)
		{
		if (!(otherDesc instanceof MiPropertyDescription))
			{
			return(false);
			}
		MiPropertyDescription other = (MiPropertyDescription )otherDesc;
		return (Utility.equals(name, other.name)
			&& Utility.equals(displayName, other.displayName)
			&& Utility.equals(defaultValue, other.defaultValue)
			&& (valueType == other.valueType)
			&& (validValuesAreSuggestionsOnly == other.validValuesAreSuggestionsOnly)
			&& (validValuesAreSpecialCasesOnly == other.validValuesAreSpecialCasesOnly)
			&& (minimumValueSpecified == other.minimumValueSpecified)
			&& (maximumValueSpecified == other.maximumValueSpecified)
			&& (stepValueSpecified == other.stepValueSpecified)
			&& (minimumValue == other.minimumValue)
			&& (maximumValue == other.maximumValue)
			&& (stepValue == other.stepValue)
			&& (requiresNonNullNonEmptyValue == other.requiresNonNullNonEmptyValue)
			&& (valuesIgnoreCase == other.valuesIgnoreCase)
			&& (displayValuesIgnoreCase == other.displayValuesIgnoreCase)
			&& (editable == other.editable)
			&& (viewable == other.viewable)
			&& (displayPriority == other.displayPriority)
			&& ((validValues == other.validValues) 
				|| ((validValues != null) && (other.validValues != null) && (validValues.equals(other.validValues))))
			&& ((excludedValues == other.excludedValues) 
				|| ((excludedValues != null) && (other.excludedValues != null) && (excludedValues.equals(other.excludedValues))))
			&& ((editingPermissionsClasses == other.editingPermissionsClasses) 
				|| ((editingPermissionsClasses != null) && (other.editingPermissionsClasses != null) && (editingPermissionsClasses.equals(other.editingPermissionsClasses))))
			&& ((viewingPermissionsClasses == other.viewingPermissionsClasses) 
				|| ((viewingPermissionsClasses != null) && (other.viewingPermissionsClasses != null) && (viewingPermissionsClasses.equals(other.viewingPermissionsClasses))))
			&& ((valueValidators == other.valueValidators) 
				|| ((valueValidators != null) && (other.valueValidators != null) && (valueValidators.equals(other.valueValidators))))
			&& ((units == other.units) 
				|| ((units != null) && (other.units != null) && (units.equals(other.units))))
			&& ((toolHint == other.toolHint) 
				|| ((toolHint != null) && (other.toolHint != null) && (toolHint.equals(other.toolHint))))
			&& ((statusHelp == other.statusHelp) 
				|| ((statusHelp != null) && (other.statusHelp != null) && (statusHelp.equals(other.statusHelp))))
			&& ((dialogHelp == other.dialogHelp) 
				|| ((dialogHelp != null) && (other.dialogHelp != null) && (dialogHelp.equals(other.dialogHelp))))
			&& ((additionalProperties == other.additionalProperties) 
				|| ((additionalProperties != null) && (other.additionalProperties != null) && (additionalProperties.equals(other.additionalProperties))))
			&& ((customEditor == other.customEditor) 
				|| ((customEditor != null) && (other.customEditor != null) && (customEditor.equals(other.customEditor))))
			);
		}
	public		MiPropertyDescription	copy()
		{
		try	{
			MiPropertyDescription copy = (MiPropertyDescription )super.clone();
			return(copy);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		return(null);
		}
	protected	String		generateRandomValidValue()
		{
		if (!isRequired())
			{
			// Return a null value 10% of the time
			if (Math.random() < 0.10)
				{
				return(null);
				}
			}

		if (validValues != null)
			{
			String value = validValues.elementAt((int )(Math.random() * validValues.size()));

			if ((!validValuesAreSpecialCasesOnly) && (!validValuesAreSuggestionsOnly))
				{
				return(value);
				}

			// Return a special value 10% of the time
			if (Math.random() < 0.10)
				{
				return(value);
				}
			}

		switch (valueType)
			{
			case Mi_FONT_NAME_TYPE :
				return(MiFontManager.getFontList()
					[(int )(Math.random() * MiFontManager.getFontList().length)]);
			case Mi_STRING_TYPE :
				return("text" + Utility.toShortString(Math.random()));
			case Mi_BOOLEAN_TYPE :
				return((Math.random() > 0.5) ? "true" : "false");
			case Mi_INTEGER_TYPE :
			case Mi_POSITIVE_INTEGER_TYPE :
				{
				int val = 0;
				if (maximumValueSpecified)
					{
					if (stepValue != 0) 
						{
						val = (int )(((int )(Math.random() * (maximumValue - minimumValue)))
							/stepValue * stepValue);
						}
					else
						{
						val = (int )(Math.random() * (maximumValue - minimumValue));
						}
					val += minimumValue;
					}
				else
					{
					val = (int )(minimumValue + (int )(Math.random() 
						* (Integer.MAX_VALUE - minimumValue)));
					}
				if ((valueType == Mi_POSITIVE_INTEGER_TYPE) && (val < 0))
					{
					val = -val;
					}
				return("" + val);
				}
			case Mi_DOUBLE_TYPE :
			case Mi_POSITIVE_DOUBLE_TYPE :
				{
				double val = 0;
				if (maximumValueSpecified)
					{
					if (stepValue != 0) 
						{
						val = ((int )(Math.random() * (maximumValue - minimumValue)))
							/stepValue * stepValue;
						}
					else
						{
						val = Math.random() * (maximumValue - minimumValue);
						}
					val += minimumValue;
					}
				else
					{
					val = minimumValue + Math.random() * (Integer.MAX_VALUE - minimumValue);
					}
				if ((valueType == Mi_POSITIVE_DOUBLE_TYPE) && (val < 0))
					{
					val = -val;
					}
				return(Utility.toShortString(val));
				}
			case Mi_COLOR_TYPE :
				{
				String[] names = MiColorManager.getColorNames();
				return(names[((int )(Math.random() * names.length))]);
				}
			case Mi_OBJECT_TYPE :
				{
				return(null);
				}
			}
		return(null);
		}

	}

