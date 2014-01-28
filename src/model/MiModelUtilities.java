
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
import java.util.ArrayList;
import java.util.Hashtable;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelUtilities implements MiiPropertyTypes
	{
	public static	MiiModelEntity	searchTreeForModelEntityWithPropertyValue(
						MiiModelEntity container, 
						String propertyName, 
						String propertyValue)
		{
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			String value = entity.getPropertyValue(propertyName);
			if (propertyValue.equals(value))
				return(entity);
			}
		return(null);
		}
	public static	MiiModelEntity	searchTreeForModelEntityWithPropertyValue(
						MiiModelEntity container, 
						String propertyName, 
						String propertyValue,
						boolean ignoreCase)
		{
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			String value = entity.getPropertyValue(propertyName);
			if (ignoreCase)
				{
				if (propertyValue.equalsIgnoreCase(value))
					return(entity);
				}
			else
				{
				if (propertyValue.equals(value))
					return(entity);
				}
			}
		return(null);
		}
	public static	MiModelEntityList searchTreeForModelEntitiesWithPropertyValue(
						MiiModelEntity container, 
						String propertyName, 
						String propertyValue)
		{
		MiModelEntityList results = new MiModelEntityList();
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			String value = entity.getPropertyValue(propertyName);
			if (propertyValue.equals(value))
				results.addElement(entity);
			}
		return(results);
		}
	public static	MiiModelEntity	searchChildrenForModelEntityWithPropertyValue(
						MiiModelEntity container, 
						String propertyName, 
						String propertyValue)
		{
		MiModelEntityList list = container.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String value = entity.getPropertyValue(propertyName);
			if (propertyValue.equals(value))
				return(entity);
			}
		return(null);
		}
	public static	MiiModelEntity	searchChildrenForModelEntityWithPropertyValueCaseDependentOnEntity(
						MiiModelEntity container, 
						String propertyName, 
						String propertyValue)
		{
		MiModelEntityList list = container.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String value = entity.getPropertyValue(propertyName);
			if (entity.getIgnoreCase())
				{
				if (propertyValue.equalsIgnoreCase(value))
					return(entity);
				}
			else
				{
				if (propertyValue.equals(value))
					return(entity);
				}
			}
		return(null);
		}
	public static	MiiModelEntity	searchChildrenForModelEntityWithPropertyValue(
						MiiModelEntity container, 
						String propertyName, 
						String propertyValue,
						boolean ignoreCase)
		{
		MiModelEntityList list = container.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String value = entity.getPropertyValue(propertyName);
			if (ignoreCase)
				{
				if (propertyValue.equalsIgnoreCase(value))
					return(entity);
				}
			else
				{
				if (propertyValue.equals(value))
					return(entity);
				}
			}
		return(null);
		}
	public static	MiModelEntityList searchChildrenForModelEntitiesWithPropertyValue(
						MiiModelEntity container, 
						String propertyName, 
						String propertyValue)
		{
		MiModelEntityList results = new MiModelEntityList();
		MiModelEntityList list = container.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String value = entity.getPropertyValue(propertyName);
			if (propertyValue.equals(value))
				results.addElement(entity);
			}
		return(results);
		}
	public static	MiiModelEntity	searchContainersForModelEntityWithPropertyValue(
						MiiModelEntity part, 
						String propertyName, 
						String propertyValue)
		{
		MiiModelEntity container = part;
		while ((container = container.getParent()) != null)
			{
			String value = container.getPropertyValue(propertyName);
			if (propertyValue.equals(value))
				return(container);
			}
		return(null);
		}
	public static	MiiModelEntity	searchChildrenForModelEntityWithType(
						MiiModelEntity container, 
						String type)
		{
		MiModelEntityList list = container.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			if (type.equals(entity.getType().getName()))
				return(entity);
			}
		return(null);
		}
	public static	MiModelEntityList searchChildrenForModelEntitiesWithType(
						MiiModelEntity container, 
						String type)
		{
		MiModelEntityList results = new MiModelEntityList();
		MiModelEntityList list = container.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			if (type.equals(entity.getType().getName()))
				results.addElement(entity);
			}
		return(results);
		}
	public static	MiiModelEntity	searchTreeForModelEntityWithType(
						MiiModelEntity container, 
						String type)
		{
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			if (type.equals(entity.getType().getName()))
				{
				return(entity);
				}
			}
		return(null);
		}
	public static	MiModelEntityList searchTreeForModelEntitiesWithType(
						MiiModelEntity container, 
						String type)
		{
		MiModelEntityList results = new MiModelEntityList();
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			if (type.equals(entity.getType().getName()))
				{
				results.addElement(entity);
				}
			}
		return(results);
		}
	private static 	Hashtable		standardPropertyDescriptionPropertyNames;

					/**------------------------------------------------------
	 				 * Converts the properties found in the given model entity to
					 * a MiPropertyDescription.
	 				 * @param entity	contains property value pairs
	 				 * @return 		the corresponding MiPropertyDescription
					 *------------------------------------------------------*/
	public static	MiPropertyDescription 	convertEntityToPropertyDescription(MiiModelEntity entity)
		{
		return(convertEntityToPropertyDescription(entity, null));
		}
	public static	MiPropertyDescription 	convertEntityToPropertyDescription(MiiModelEntity entity, Hashtable aliases)
		{
		if (standardPropertyDescriptionPropertyNames == null)
			{
			standardPropertyDescriptionPropertyNames = new Hashtable();

			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_DEFAULT_VALUE_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_MINIMUM_VALUE_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_MAXIMUM_VALUE_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_STEP_VALUE_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_REQUIRED_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_EDITABLE_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_VIEWABLE_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_DISPLAY_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_TOOL_HINT_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_STATUS_HELP_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_DIALOG_HELP_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_UNITS_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_VALUES_IGNORE_CASE_NAME, "false");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_EXCLUDED_VALUES_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_INCLUDED_VALUES_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_DISPLAY_PRIORITY_NAME, "true");
		
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_VALID_VALUES_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_VALID_DISPLAY_VALUES_NAME, "true");
			standardPropertyDescriptionPropertyNames.put(Mi_PROPERTY_TYPE_NAME, "true");
			}

		MiPropertyDescription desc = null;
		String propertyName = entity.getPropertyValue(Mi_PROPERTY_NAME);
		if (propertyName == null)
			{
			MiiModelEntity parent = entity.getParent();
			if ((parent == null) || (parent.getName() == null))
				{
				MiDebug.println("Error: " + entity.getLocation() 
					+ "A \'name\' must be specified or the container of this property description must have a name");
				return(null);
				}
			propertyName = parent.getName();
			}
		String values = entity.getPropertyValue(Mi_PROPERTY_VALID_VALUES_NAME);
		String type = entity.getPropertyValue(Mi_PROPERTY_TYPE_NAME);
		if (values != null)
			{
			Strings vals = new Strings();
			// Add " " to handle trailing comma (which implies a trailing "", for value lists that allow a blank)
			vals.appendCommaDelimitedStrings(values + " ");
			if ((values.length() > 0) && (values.charAt(0) == ','))
				{
				vals.insertElementAt("", 0);
				}
			desc = new MiPropertyDescription(propertyName, vals);
			}
		// 3-17-2004 allow boolean type with 2 values: true and false... else if (type != null)
		if (type != null)
			{
			int index = Utility.indexOfStringInArray(MiiPropertyTypes.propertyTypeNames, type, false);
			if (index != -1)
				{
				if (desc != null)
					{
					desc.setType(index);
					}
				else
					{
					desc = new MiPropertyDescription(propertyName, index);
					}
				}
			else
				{
				MiDebug.println("Error: " + entity.getLocation() + "Unknown type: " + type);
				return(null);
				}
			}
		else if (values == null)
			{
			MiDebug.println("Error: [" + entity.getLocation() + "]:"
				+ "Either \'values\' or \'type\' must be specified in an XML-specified property description");
MiDebug.println(MiModelEntity.dump(entity));
MiDebug.printStackTrace();
			return(null);
			}

		String defaultValue = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_DEFAULT_VALUE_NAME));
		String minValue = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_MINIMUM_VALUE_NAME));
		String maxValue = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_MAXIMUM_VALUE_NAME));
		String stepValue = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_STEP_VALUE_NAME));
		String requiredValue = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_REQUIRED_NAME));
		String ignoreCase = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_VALUES_IGNORE_CASE_NAME));
		String displayValuesIgnoreCase = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_DISPLAY_VALUES_IGNORE_CASE_NAME));
		String editable = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_EDITABLE_NAME));
		String viewable = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_VIEWABLE_NAME));
		String displayName = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_DISPLAY_NAME));
		String toolHint = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_TOOL_HINT_NAME));
		String statusHelp = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_STATUS_HELP_NAME));
		String dialogHelp = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_DIALOG_HELP_NAME));
		String units = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_UNITS_NAME));
		String excludedValueStr = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_EXCLUDED_VALUES_NAME));
		String includedValueStr = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_INCLUDED_VALUES_NAME));
		String validDisplayValuesStr = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_VALID_DISPLAY_VALUES_NAME));
		String displayPriorityStr = entity.getPropertyValue(lookupAlias(aliases, Mi_PROPERTY_DISPLAY_PRIORITY_NAME));

		if (defaultValue != null)
			{
			desc.setDefaultValue(defaultValue);
			}
		if (minValue != null)
			{
			desc.setMinimumValue(Utility.toDouble(minValue));
			}
		if (maxValue != null)
			{
			desc.setMaximumValue(Utility.toDouble(maxValue));
			}
		if (stepValue != null)
			{
			desc.setStepValue(Utility.toDouble(stepValue));
			}
		if (requiredValue != null)
			{
			desc.setRequired(Utility.toBoolean(requiredValue));
			}
		if (ignoreCase != null)
			{
			desc.setIgnoreCase(Utility.toBoolean(ignoreCase));
			}
		if (displayValuesIgnoreCase != null)
			{
			desc.setDisplayValuesIgnoreCase(Utility.toBoolean(displayValuesIgnoreCase));
			}
		if (editable != null)
			{
			desc.setEditable(Utility.toBoolean(editable));
			}
		if (viewable != null)
			{
			desc.setViewable(Utility.toBoolean(viewable));
			}
		if (statusHelp != null)
			{
			desc.setStatusHelpMessage(statusHelp);
			}
		if (toolHint != null)
			{
			desc.setToolHintMessage(toolHint);
			}
		if (dialogHelp != null)
			{
			desc.setDialogHelpMessage(dialogHelp);
			}
		if (displayName != null)
			{
			desc.setDisplayName(displayName);
			}
		if (excludedValueStr != null)
			{
			Strings vals = new Strings();
			// Add " " to handle trailing comma (which implies a trailing "", for value lists that allow a blank)
			vals.appendCommaDelimitedStrings(excludedValueStr + " ");
			desc.setExcludedValues(vals);
			}
		if (includedValueStr != null)
			{
			Strings vals = new Strings();
			// Add " " to handle trailing comma (which implies a trailing "", for value lists that allow a blank)
			vals.appendCommaDelimitedStrings(includedValueStr + " ");
			desc.setValidValues(vals);
			desc.setValidValuesAreSpecialCasesOnly(true);
			}
		if (validDisplayValuesStr != null)
			{
			Strings vals = new Strings();
			// Add " " to handle trailing comma (which implies a trailing "", for value lists that allow a blank)
			vals.appendCommaDelimitedStrings(validDisplayValuesStr + " ");
			desc.setValidDisplayValues(vals);
			}
		if (displayPriorityStr != null)
			{
			desc.setDisplayPriority(Utility.toInteger(displayPriorityStr));
			}
		if (units != null)
			{
			String theUnits = units;
			try	{
				String parm1 = null;
				String parm2 = null;
				String parm3 = null;
				int index = units.indexOf('(');
				if (index != -1)
					{
					String parms = units.substring(index + 1, units.indexOf(')'));
					units = units.substring(0, index);
					parm1 = parms.trim();
					index = parms.indexOf(',');
					if (index != -1)
						{
						parm1 = parms.substring(0, index).trim();
						parm2 = parms.substring(index + 1).trim();
						}
					index = parm2.indexOf(',');
					if (index != -1)
						{
						parm2 = parms.substring(0, index).trim();
						parm3 = parms.substring(index + 1).trim();
						}
					}
				units = lookupAlias(aliases, units);
				MiiUnits implementation = (MiiUnits )Utility.makeInstanceOfClass(units);
				if (parm1 != null)
					{
					implementation.setInternalUnits(parm1, true, " ");
					}
				if (parm2 != null)
					{
					implementation.setDisplayUnits(parm2, true, " ");
					}
				if (parm3 != null)
					{
					implementation.setPersistentUnits(parm2, true, " ");
					}
				desc.setUnits(implementation);
				}
			catch (Throwable e)
				{
				MiDebug.println("Error: " + entity.getLocation() 
					+ "Error in units specification: \"" 
					+ theUnits + "\" - " + e.getMessage());
				MiDebug.printStackTrace(e);
				}
			}

		Strings propertyNames = entity.getPropertyNames();
		for (int i = 0; i < propertyNames.size(); ++i)
			{
			String property = propertyNames.elementAt(i);
			if (standardPropertyDescriptionPropertyNames.get(property) == null)
				{
				desc.setAdditionalProperty(
					lookupAlias(aliases, property), entity.getPropertyValue(property));
				}
			}

		return(desc);
		}

					/**------------------------------------------------------
	 				 * Converts the properties found in the given model entity to
					 * a MiPropertyDescription.
	 				 * @param entity	contains property value pairs
	 				 * @return 		the corresponding MiPropertyDescription
					 *------------------------------------------------------*/
/*** OLD
	public static	MiPropertyDescription 	convertEntityToPropertyDescription(MiiModelEntity entity)
		{
		MiPropertyDescription desc = null;
		String propertyName = entity.getPropertyValue("name");
		if (propertyName == null)
			{
			MiDebug.println("Error: " + entity.getLocation() 
				+ "A propertyName must be specified");
			return(null);
			}
		String values = entity.getPropertyValue("values");
		String defaultValue = entity.getPropertyValue("default");
		String type = entity.getPropertyValue("type");
		String minValue = entity.getPropertyValue("min");
		String maxValue = entity.getPropertyValue("max");
		String stepValue = entity.getPropertyValue("step");
		String requiredValue = entity.getPropertyValue("required");
		
		if (values != null)
			{
			Strings vals = new Strings();
			vals.appendCommaDelimitedStrings(values);
			desc = new MiPropertyDescription(propertyName, vals);
			}
		else if (type != null)
			{
			int index = Utility.indexOfStringInArray(MiiPropertyTypes.propertyTypeNames, type, false);
			if (index != -1)
				{
				desc = new MiPropertyDescription(propertyName, index);
				}
			else
				{
				MiDebug.println("Error: " + entity.getLocation() + "Unknown type: " + type);
				return(null);
				}
			}
		else
			{
			MiDebug.println("Error: [" + entity.getLocation() + "]:"
				+ "Either values or type must be specified");
			return(null);
			}
		if (defaultValue != null)
			{
			desc.setDefaultValue(defaultValue);
			}
		if (minValue != null)
			{
			desc.setMinimumValue(Utility.toDouble(minValue));
			}
		if (maxValue != null)
			{
			desc.setMaximumValue(Utility.toDouble(maxValue));
			}
		if (stepValue != null)
			{
			desc.setStepValue(Utility.toDouble(stepValue));
			}
		if (requiredValue != null)
			{
			desc.setRequired(Utility.toBoolean(requiredValue));
			}
		return(desc);
		}
***/
	public static	MiiModelEntity	convertPropertyDescriptionToModel(MiPropertyDescription desc)
		{
		return(convertPropertyDescriptionToModel(desc, null));
		}
	public static	MiiModelEntity	convertPropertyDescriptionToModel(MiPropertyDescription desc, Hashtable aliases)
		{
		MiiModelEntity model = new MiModelEntity();
		model.setType(MiModelType.getOrMakeType(Mi_PROPERTY_DESCRIPTION_TYPE_NAME));
		model.setName(desc.getName());

		if ((desc.getDisplayName() != null) && ((desc.getName() == null) || (!desc.getName().equals(desc.getDisplayName()))))
			{
			model.setPropertyValue(Mi_PROPERTY_DISPLAY_NAME, desc.getDisplayName());
			}

		if (desc.getValidValues() != null)
			{
			if (desc.getValidValuesAreSpecialCasesOnly())
				{
				model.setPropertyValue(Mi_PROPERTY_INCLUDED_VALUES_NAME, desc.getValidValues().getCommaDelimitedStrings());
				}
			else
				{
				model.setPropertyValue(Mi_PROPERTY_VALID_VALUES_NAME, desc.getValidValues().getCommaDelimitedStrings());
				}
			}

		model.setPropertyValue(Mi_PROPERTY_TYPE_NAME, MiiPropertyTypes.propertyTypeNames[desc.getType()]);

		model.setPropertyValue(Mi_PROPERTY_DEFAULT_VALUE_NAME, desc.getDefaultValue());

		if (desc.getMinimumValueIsSpecified())
			{
			model.setPropertyValue(
				Mi_PROPERTY_MINIMUM_VALUE_NAME, 
				Utility.toShortString(desc.getMinimumValue()));
			}
		if (desc.getMaximumValueIsSpecified())
			{
			model.setPropertyValue(
				Mi_PROPERTY_MAXIMUM_VALUE_NAME, 
				Utility.toShortString(desc.getMaximumValue()));
			}
		if (desc.getStepValueIsSpecified())
			{
			model.setPropertyValue(
				Mi_PROPERTY_STEP_VALUE_NAME, 	
				Utility.toShortString(desc.getStepValue()));
			}
		if (!desc.isEditable(null))
			{
			model.setPropertyValue(
				Mi_PROPERTY_EDITABLE_NAME, 	
				desc.isEditable(null) + "");
			}
		if (!desc.isViewable(null))
			{
			model.setPropertyValue(
				Mi_PROPERTY_VIEWABLE_NAME, 	
				desc.isViewable(null) + "");
			}
		if (desc.isRequired())
			{
			model.setPropertyValue(
				Mi_PROPERTY_REQUIRED_NAME, 	
				desc.isRequired() + "");
			}
		model.setPropertyValue(
			Mi_PROPERTY_TOOL_HINT_NAME, 	
			desc.getToolHintHelp() == null ? null : desc.getToolHintHelp().getMessage());
		model.setPropertyValue(
			Mi_PROPERTY_STATUS_HELP_NAME, 	
			desc.getStatusHelp() == null ? null : desc.getStatusHelp().getMessage());
		model.setPropertyValue(
			Mi_PROPERTY_DIALOG_HELP_NAME, 	
			desc.getDialogHelp() == null ? null : desc.getDialogHelp().getMessage());


		if (desc.getExcludedValues() != null)
			{
			model.setPropertyValue(
				Mi_PROPERTY_EXCLUDED_VALUES_NAME, 	
				desc.getExcludedValues().toCommaDelimitedString());
			}
		if (desc.getUnits() != null)
			{
			String className = desc.getUnits().getClass().getName();
			String unitsName = lookupAlias(aliases, className);

			model.setPropertyValue(
				Mi_PROPERTY_UNITS_NAME,
			 	unitsName);
			}

		for (int j = 0; j < desc.getNumberOfAdditionalProperties(); ++j)
			{
			String name = desc.getNameOfAdditionalProperty(j);
			Object value = desc.getAdditionalProperty(name);
			
			model.setPropertyValue(name, value != null ? value.toString() : null);
			}

		return(model);
		}
	protected static String		lookupAlias(Hashtable aliases, String key)
		{
		if ((aliases != null) && (key != null))
			{
			String alias = (String )aliases.get(key);
			if (alias != null)
				{
				return(alias);
				}
			}
		return(key);
		}


	public static	boolean	 	modifyTreeOfModelEntitiesTypes(
						MiiModelEntity container, 
						String oldType,
						String newType)
		{
		boolean madeChange = false;

		MiModelEntityList results = new MiModelEntityList();
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			if (oldType.equals(entity.getType().getName()))
				{
				entity.setType(MiModelType.getOrMakeType(newType));
				madeChange = true;
				}
			}
		return(madeChange);
		}
	/**
	 * Change all the oldPropertyNames in the tree to be the newPropertyName, with the same value
	 * If newPropertyName is null, then the properties are in effect removed
	 **/
	public static	boolean	 modifyTreeOfModelEntitiesPropertyNames(
						MiiModelEntity container, 
						String oldPropertyName,
						String newPropertyName)
		{
		boolean madeChange = false;

		MiModelEntityList results = new MiModelEntityList();
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			String value = entity.getPropertyValue(oldPropertyName);
			if (value != null)
				{
				entity.removeProperty(oldPropertyName);
				if (newPropertyName != null)
					{
					entity.setPropertyValue(newPropertyName, value);
					}
				madeChange = true;
				}
			}
		return(madeChange);
		}
	public static	boolean	 modifyTreeOfModelEntitiesPropertyNamesToLowerCase(
						MiiModelEntity container)
		{
		boolean madeChange = false;

		MiModelEntityList results = new MiModelEntityList();
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			Strings propertyNames = entity.getPropertyNames();
			for (int i = 0; i < propertyNames.size(); ++i)
				{
				String propertyName = propertyNames.get(i);
				if (!propertyName.toLowerCase().equals(propertyName))
					{
					String value = entity.getPropertyValue(propertyName);
					entity.removeProperty(propertyName);
					entity.setPropertyValue(propertyName.toLowerCase(), value);
				
					madeChange = true;
					}
				}
			}
		return(madeChange);
		}
	public static	boolean	 modifyTreeOfModelEntitiesValues(
						MiiModelEntity container, 
						String oldPropertyValue,
						String newPropertyValue)
		{
		boolean madeChange = false;

		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			Strings propertyNames = entity.getPropertyNames();
			for (int i = 0; i < propertyNames.size(); ++i)
				{
				int index = -1;
				String propertyName = propertyNames.get(i);
				String value = entity.getPropertyValue(propertyName);
				if (value != null)
					{
					if (value.equals(oldPropertyValue))
						{
						entity.setPropertyValue(propertyName, newPropertyValue);
						madeChange = true;
						}
					else if ((index = value.indexOf(oldPropertyValue)) != -1)
						{
						value = value.substring(0, index) + newPropertyValue 
							+ value.substring(index + oldPropertyValue.length());
						entity.setPropertyValue(propertyName, value);
						madeChange = true;
						}
					}
				}
			}
		return(madeChange);
		}
	public static	boolean	 modifyTreeOfModelEntitiesPropertyValues(
						MiiModelEntity container, 
						String propertyName,
						String newPropertyValue)
		{
		boolean madeChange = false;

		if (container.getPropertyNames().contains(propertyName))
			{
			container.setPropertyValue(propertyName, newPropertyValue);
			madeChange = true;
			}
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			if (entity.getPropertyNames().contains(propertyName))
				{
				entity.setPropertyValue(propertyName, newPropertyValue);
				madeChange = true;
				}
			}
		return(madeChange);
		}
	/**
	 * Change all properties in the given tree with the given proeprty name to full-fledge 
	 * entities in the tree with the entities having a type equal to the property name
	 * (i.e. promote the proerpties to first class entities).
	 **/
	public static	boolean	 modifyTreeOfModelEntitiesPropertiesToTypes(
						MiiModelEntity container, 
						String propertyName,
						String previousPropertyValuesNewPropertyName)
		{
		boolean madeChange = false;

		MiModelEntityList results = new MiModelEntityList();
		MiModelEntityTreeList list = new MiModelEntityTreeList(container);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			String value = entity.getPropertyValue(propertyName);
			if (value != null)
				{
				MiModelEntity newEntity = new MiModelEntity();
				newEntity.setType(MiModelType.getOrMakeType(propertyName));
				entity.appendModelEntity(newEntity);
				newEntity.setPropertyValue(previousPropertyValuesNewPropertyName, value);
				entity.removeProperty(propertyName);
				madeChange = true;
				}
			}
		return(madeChange);
		}

				/**
				 * @return	list of differences, if any, of form:
				 * "<type/type1> entity does not exist in target"
				 * "<type/type1> entity does not exist in source"
				 **/
				 // someday... "<type/type1/property> property is not equal to target type/type1/property"
	public static	Strings	compareTree(MiiModelEntity one, MiiModelEntity other)
		{
		Strings results = new Strings();
		compareTree(one, other, results, "", "");
		return(results);
		}
	private static	void	compareTree(MiiModelEntity one, MiiModelEntity other, Strings results, 
					String oneTreePath, String otherTreePath)
		{
		MiModelEntityList oneList = one.getModelEntities();
		MiModelEntityList otherList = other.getModelEntities();
		ArrayList matchedEntities = new ArrayList();

		for (int i = 0; i < oneList.size(); ++i)
			{
			MiiModelEntity oneChild = oneList.elementAt(i);
			for (int j = 0; j < otherList.size(); ++j)
				{
				MiiModelEntity otherChild = otherList.elementAt(j);
				if (((oneChild.getType() == otherChild.getType())
					|| ((oneChild.getType() != null)
						&& (oneChild.getType().equals(otherChild.getType()))))
					&& (((MiModelEntity )oneChild).equalsProperties(otherChild)))
					{
					compareTree(oneChild, otherChild, results, 
						oneTreePath + "/"
						+ (oneChild.getType() != null ? oneChild.getType().getName() : oneChild.getName()),
						otherTreePath + "/"
						+ (otherChild.getType() != null ? otherChild.getType().getName() : otherChild.getName()));
					matchedEntities.add(otherList.elementAt(j));
					matchedEntities.add(oneList.elementAt(i));
//MiDebug.println("Found matching entity: " + oneList.elementAt(i));
//MiDebug.println("with other entity: " + otherList.elementAt(j));
					//otherList.removeElementAt(j);
					//oneList.removeElementAt(i);
					//--i;
					break;
					}
				}
			}
//MiDebug.println("Looking at entities in" + one);
		for (int i = 0; i < oneList.size(); ++i)
			{
			MiiModelEntity oneChild = oneList.elementAt(i);
//MiDebug.println("Has matching entity? " + oneChild);
			if (!matchedEntities.contains(oneChild))
				{
//MiDebug.println("NOOOO");
				results.add(oneTreePath + "/" 
					+ (oneChild.getType() != null ? oneChild.getType().getName() : oneChild.getName()) 
					+ " entity does not exist in target");
				}
			}
//MiDebug.println("Looking at entities in" + other);
		for (int i = 0; i < otherList.size(); ++i)
			{
			MiiModelEntity otherChild = otherList.elementAt(i);
//MiDebug.println("Has matching entity? " + otherChild);
			if (!matchedEntities.contains(otherChild))
				{
//MiDebug.println("NOOOO");
				results.add(otherTreePath + "/" 
					+ (otherChild.getType() != null ? otherChild.getType().getName() : otherChild.getName()) 
					+ " entity does not exist in source");
				}
			}
		}

	public final static MiModelType	Mi_MODEL_ENTITY_DIFFERENCE_OPERATOR_TYPE 	= MiModelType.getOrMakeType("differences");

	final static MiModelType	Mi_CHANGE_PROPERTIES_OPERATION_TYPE 		= MiModelType.getOrMakeType("changeProperties");
	final static MiModelType	Mi_DUPLICATE_ENTITY_OPERATION_TYPE		= MiModelType.getOrMakeType("duplicateEntity");
	final static MiModelType	Mi_MOVE_ENTITY_POSITION_OPERATION_TYPE		= MiModelType.getOrMakeType("moveEntity");
	final static MiModelType	Mi_DELETE_ENTITY_OPERATION_TYPE			= MiModelType.getOrMakeType("deleteEntity");
	final static MiModelType	Mi_ADD_ENTITY_OPERATION_TYPE			= MiModelType.getOrMakeType("addEntity");

	final static String		Mi_OPERATION_SOURCE_POSITION_INDEX		= "srcPath";
	final static String		Mi_OPERATION_TARGET_POSITION_INDEX		= "targetPath";

	final static String		Mi_CHANGE_PROPERTIES_OPERATION_NULL_PROPERTY_VALUE	= "__NULL";

	public static	MiiModelEntity	getDifferencesBetweenTrees(MiiModelEntity one, MiiModelEntity other)
		{
		MiiModelEntity differences = new MiModelEntity();
		differences.setType(Mi_MODEL_ENTITY_DIFFERENCE_OPERATOR_TYPE);
		getDifferencesBetweenTrees(one, other, "", "", differences);
		return(differences);
		}
	private static	void	getDifferencesBetweenTrees(MiiModelEntity one, MiiModelEntity other, String positionInOneTree, String positionInOtherTree, MiiModelEntity differences)
		{
		if (!one.equalsProperties(other))
			{
			MiiModelEntity propertyChangeEntity = new MiModelEntity();
			propertyChangeEntity.setType(Mi_CHANGE_PROPERTIES_OPERATION_TYPE);
			//propertyChangeEntity.setPropertyValue(
				//Mi_OPERATION_SOURCE_POSITION_INDEX, positionInOneTree);
			propertyChangeEntity.setPropertyValue(
				Mi_OPERATION_TARGET_POSITION_INDEX, positionInOtherTree);

			Strings onePropertyNames = one.getPropertyNames();
			for (int i = 0 ; i < onePropertyNames.size(); ++i)
				{
				String propertyName = onePropertyNames.get(i);
				String oneValue = one.getPropertyValue(propertyName);
				String otherValue = other.getPropertyValue(propertyName);
				if (otherValue == null)
					{
					// Removed property...
					propertyChangeEntity.setPropertyValue(
						propertyName, Mi_CHANGE_PROPERTIES_OPERATION_NULL_PROPERTY_VALUE);
					}
				else if (!other.equals(oneValue, otherValue))
					{
MiDebug.println("oneValue=" + oneValue);
MiDebug.println("NOT EQUAL TO otherValue=" + otherValue);
					// Changed property...
					propertyChangeEntity.setPropertyValue(propertyName, otherValue);
					}
				}
			Strings otherPropertyNames = other.getPropertyNames();
			for (int i = 0 ; i < otherPropertyNames.size(); ++i)
				{
				String propertyName = otherPropertyNames.get(i);
				String oneValue = one.getPropertyValue(propertyName);
				String otherValue = other.getPropertyValue(propertyName);
				if ((oneValue == null) || (!oneValue.equals(otherValue)))
					{
					// Added property...
					propertyChangeEntity.setPropertyValue(propertyName, otherValue);
					}
				}
			differences.appendModelEntity(propertyChangeEntity);
			}

		MiModelEntityList oneList = one.getModelEntities();
		MiModelEntityList otherList = other.getModelEntities();

		MiModelEntityList matchedOnes = new MiModelEntityList();
		MiModelEntityList matchedOthers = new MiModelEntityList();

		for (int i = 0; i < oneList.size(); ++i)
			{
			MiiModelEntity oneChild = oneList.elementAt(i);

			boolean foundMatch = false;
			
			for (int j = 0; j < otherList.size(); ++j)
				{
				MiiModelEntity otherChild = otherList.elementAt(j);
MiDebug.println("Comparing oneChild: " + oneChild);
MiDebug.println("Comparing to      : " + otherChild);
MiDebug.println("(oneChild.getType().equals(otherChild.getType())) = " + (oneChild.getType().equals(otherChild.getType())));
MiDebug.println("(oneChild.getType().getValue() = " + oneChild.getType().getValue());
MiDebug.println("(otherChild.getType().getValue() = " + otherChild.getType().getValue());
MiDebug.println("(oneChild.getName().equals(otherChild.getName())) = " + (oneChild.getName().equals(otherChild.getName())));
				if ( ((oneChild.getType() == otherChild.getType())
					|| ((oneChild.getType() != null)
						&& (oneChild.getType().equals(otherChild.getType()))))
				&& ((oneChild.getName() == otherChild.getName())
					|| ((oneChild.getName() != null)
						&& (oneChild.getName().equals(otherChild.getName())))) )
					{
					String onePosition = positionInOneTree.length() > 0 ? positionInOneTree + "." + i : "" + i;
					String otherPosition = positionInOtherTree.length() > 0 ? positionInOtherTree + "." + j : "" + j;
MiDebug.println("EQUAL " );
MiDebug.println("onePosition= " + onePosition );
MiDebug.println("otherPosition =  " + otherPosition );
					// Same entity...
					if (matchedOthers.contains(otherChild))
						{
						// The target entity was already matched...
						continue;
						}

					if (foundMatch)
						{
						// Entity was copied....
						MiiModelEntity positionChangeEntity = new MiModelEntity();
						positionChangeEntity.setType(Mi_DUPLICATE_ENTITY_OPERATION_TYPE);
						positionChangeEntity.setPropertyValue(
							Mi_OPERATION_SOURCE_POSITION_INDEX, onePosition);
						positionChangeEntity.setPropertyValue(
							Mi_OPERATION_TARGET_POSITION_INDEX, otherPosition);

						differences.appendModelEntity(positionChangeEntity);
						}
					else if (i != j)
						{
						// Entity moved within list... or entities added?
						MiiModelEntity positionChangeEntity = new MiModelEntity();
						positionChangeEntity.setType(Mi_MOVE_ENTITY_POSITION_OPERATION_TYPE);
						positionChangeEntity.setPropertyValue(
							Mi_OPERATION_SOURCE_POSITION_INDEX, onePosition);
						positionChangeEntity.setPropertyValue(
							Mi_OPERATION_TARGET_POSITION_INDEX, otherPosition);

						differences.appendModelEntity(positionChangeEntity);
						}
					matchedOnes.addElement(oneChild);
					matchedOthers.addElement(otherChild);

					foundMatch = true;

					getDifferencesBetweenTrees(
						oneChild, 
						otherChild, 
						onePosition,
						otherPosition,
						differences);
					}
				}
			}

		MiModelEntityList unmatchedOnes = new MiModelEntityList();
		MiModelEntityList unmatchedOthers = new MiModelEntityList();

		Strings unmatchedOnesPositions = new Strings();
		Strings unmatchedOthersPositions = new Strings();

		for (int i = 0; i < oneList.size(); ++i)
			{
			MiiModelEntity oneChild = oneList.elementAt(i);
			if (!matchedOnes.contains(oneChild))
				{
				unmatchedOnes.addElement(oneChild);
				String onePosition = positionInOneTree.length() > 0 ? positionInOneTree + "." + i : "" + i;
				unmatchedOnesPositions.add(onePosition);
				}
			}
		for (int j = 0; j < otherList.size(); ++j)
			{
			MiiModelEntity otherChild = otherList.elementAt(j);
			if (!matchedOthers.contains(otherChild))
				{
				unmatchedOthers.addElement(otherChild);
				String otherPosition = positionInOtherTree.length() > 0 ? positionInOtherTree + "." + j : "" + j;
				unmatchedOthersPositions.add(otherPosition);
				}
			}

		for (int i = 0; i < unmatchedOnes.size(); ++i)
			{
			boolean failedUniqueMatch = false;

			MiiModelEntity oneChild = unmatchedOnes.elementAt(i);
			String onePosition = unmatchedOnesPositions.get(i);
			String otherPosition = null;

			// Entity deleted ...?
			// Try to see if we can match this entity's type uniquely with an other unmatched entities type,
			// and assume there was just a name change that prevented matching above

			// Assure no entities of same type in one list...
			for (int j = 0; j < unmatchedOnes.size(); ++j)
				{
				if (i != j)
					{
					MiiModelEntity anotherOneChild = unmatchedOnes.elementAt(j);
					if ( ((oneChild.getType() == anotherOneChild.getType())
						|| ((oneChild.getType() != null)
							&& (oneChild.getType().equals(anotherOneChild.getType())))) )
						{
						// Oops there is...
						failedUniqueMatch = true;
						break;
						}
					}
				}
						
			MiiModelEntity matchedOther = null;
			for (int j = 0; (!failedUniqueMatch) && (j < unmatchedOthers.size()); ++j)
				{
				MiiModelEntity otherChild = unmatchedOthers.elementAt(j);
				if ( ((oneChild.getType() == otherChild.getType())
					|| ((oneChild.getType() != null)
						&& (oneChild.getType().equals(otherChild.getType())))) )
					{
					if (matchedOther != null)
						{
						// Unable to match types uniquely, give up...
						failedUniqueMatch = true;
						break;
						}
					matchedOther = otherChild;
					otherPosition = unmatchedOthersPositions.get(j);
					}
				}

			if ((!failedUniqueMatch) && (matchedOther != null))
				{
				getDifferencesBetweenTrees(
					oneChild, 
					matchedOther, 
					onePosition,
					otherPosition,
					differences);

				unmatchedOnes.removeElement(oneChild);
				unmatchedOthers.removeElement(matchedOther);
				}
			else
				{
				// Entity deleted ...
MiDebug.println("src list found entity that was deleted: " + oneChild);
				MiiModelEntity deleteEntity = new MiModelEntity();
				deleteEntity.setType(Mi_DELETE_ENTITY_OPERATION_TYPE);
				deleteEntity.setPropertyValue(Mi_OPERATION_TARGET_POSITION_INDEX, onePosition);

				differences.appendModelEntity(deleteEntity);
				}
			}
		for (int i = 0; i < unmatchedOthers.size(); ++i)
			{
			MiiModelEntity otherChild = unmatchedOthers.elementAt(i);
			// Entity added ...
MiDebug.println("target list found entity that was addded: " + otherChild);
			MiiModelEntity addEntity = new MiModelEntity();
			addEntity.setType(Mi_ADD_ENTITY_OPERATION_TYPE);
			String otherPosition = unmatchedOthersPositions.get(i);
			addEntity.setPropertyValue(Mi_OPERATION_TARGET_POSITION_INDEX, otherPosition);

			addEntity.appendModelEntity(otherChild.deepCopy());

			differences.appendModelEntity(addEntity);
			}
		}
	public static	MiiModelEntity	applyDifferencesToTree(MiiModelEntity one, MiiModelEntity differences)
		{
		MiiModelEntity other = one.deepCopy();

		MiModelEntityList diffs = getDifferencesOfType(differences, Mi_DELETE_ENTITY_OPERATION_TYPE);
		sortDifferencesBaseOnTargetPositionInTree(diffs, false);
		executeDifferences(one, diffs, other);

		diffs = getDifferencesOfType(differences, Mi_ADD_ENTITY_OPERATION_TYPE);
		sortDifferencesBaseOnTargetPositionInTree(diffs, true);
		executeDifferences(one, diffs, other);

		diffs = getDifferencesOfType(differences, Mi_MOVE_ENTITY_POSITION_OPERATION_TYPE);
		sortDifferencesBaseOnTargetPositionInTree(diffs, true);
		executeDifferences(one, diffs, other);

		diffs = getDifferencesOfType(differences, Mi_DUPLICATE_ENTITY_OPERATION_TYPE);
		sortDifferencesBaseOnTargetPositionInTree(diffs, true);
		executeDifferences(one, diffs, other);

		diffs = getDifferencesOfType(differences, Mi_CHANGE_PROPERTIES_OPERATION_TYPE);
		sortDifferencesBaseOnTargetPositionInTree(diffs, true);
		executeDifferences(one, diffs, other);
		
		return(other);
		}

	private static MiModelEntityList getDifferencesOfType(MiiModelEntity differences, MiModelType type)
		{
		MiModelEntityList list = new MiModelEntityList();
		for (int i = 0; i < differences.getNumberOfModelEntities(); ++i)
			{
			if (differences.getModelEntity(i).getType() == type)
				{
				list.addElement(differences.getModelEntity(i));
				}
			}
		return(list);
		}
	private static void	 sortDifferencesBaseOnTargetPositionInTree(MiModelEntityList differences, boolean inOrder)
		{
		boolean unsorted = true;

		while (unsorted)
			{
			unsorted = false;
			for (int i = 0; i < differences.size(); ++i)
				{
				String prevTargetPositionIndex 
						= differences.elementAt(i).getPropertyValue(Mi_OPERATION_TARGET_POSITION_INDEX);
			
				for (int j = i + 1; j < differences.size(); ++j)
					{
					String targetPositionIndex 
						= differences.elementAt(j).getPropertyValue(Mi_OPERATION_TARGET_POSITION_INDEX);

					if (prevTargetPositionIndex.compareTo(targetPositionIndex) > 0)
						{
						unsorted = true;
						MiiModelEntity diff = differences.elementAt(i);
						differences.setElementAt(differences.elementAt(j), i);
						differences.setElementAt(diff, j);
						}
					}
				}
			}
		}
	private static	MiiModelEntity	getEntityFromTreePositionIndex(MiiModelEntity tree, String treePositionIndex)
		{
		if (treePositionIndex.length() == 0)
			{
			return(tree);
			}
		int indexOfDot = treePositionIndex.indexOf('.');
		if (indexOfDot != -1)
			{
			String branchIndexStr = treePositionIndex.substring(0, indexOfDot);
			int branchIndex = Utility.toInteger(branchIndexStr);
			treePositionIndex = treePositionIndex.substring(indexOfDot + 1);
			return(getEntityFromTreePositionIndex(tree.getModelEntity(branchIndex), treePositionIndex));
			}
		int branchIndex = Utility.toInteger(treePositionIndex);
		return(tree.getModelEntity(branchIndex));
		}

	private static	void		executeDifferences(MiiModelEntity one, MiModelEntityList differences, MiiModelEntity other)
		{
		for (int i = 0; i < differences.size(); ++i)
			{
			MiiModelEntity aDifference = differences.elementAt(i);

			MiiModelEntity source = null;
			MiiModelEntity target = null;
			String sourcePositionIndex = aDifference.getPropertyValue(Mi_OPERATION_SOURCE_POSITION_INDEX);
			String targetPositionIndex = aDifference.getPropertyValue(Mi_OPERATION_TARGET_POSITION_INDEX);
			if (sourcePositionIndex != null)
				{
				source = getEntityFromTreePositionIndex(one, sourcePositionIndex);
				}
			if (targetPositionIndex != null)
				{
				target = getEntityFromTreePositionIndex(other, targetPositionIndex);
				}
			MiModelType type = aDifference.getType();
			if (type == Mi_CHANGE_PROPERTIES_OPERATION_TYPE)
				{
				Strings propertyNames = aDifference.getPropertyNames();
				for (int j = 0; j < propertyNames.size(); ++j)
					{
					String name = propertyNames.get(j);
					String value = aDifference.getPropertyValue(name);
					if (Mi_CHANGE_PROPERTIES_OPERATION_NULL_PROPERTY_VALUE.equals(value))
						{
						value = null;
						}
					target.setPropertyValue(name, value);
					}
				}
			else if (type == Mi_DUPLICATE_ENTITY_OPERATION_TYPE)
				{
				target.replaceSelf(source);
				}
			else if (type == Mi_MOVE_ENTITY_POSITION_OPERATION_TYPE)
				{
				target.replaceSelf(source.deepCopy());
				}
			else if (type == Mi_DELETE_ENTITY_OPERATION_TYPE)
				{
				target.removeSelf();
				}
			else if (type == Mi_ADD_ENTITY_OPERATION_TYPE)
				{
				MiiModelEntity parent = target.getParent();
				int index = parent.getIndexOfModelEntity(target);
				parent.insertModelEntity(aDifference.getModelEntity(0), index);
				}
			else
				{
				throw new RuntimeException("Unknown type of difference operation found in apply differences: " + aDifference);
				}
			}
		}


	}
