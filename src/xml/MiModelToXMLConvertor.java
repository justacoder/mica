
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
import com.swfm.mica.*; 
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

/**----------------------------------------------------------------------------------------------
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelToXMLConvertor implements MiiPropertyTypes
	{
	private		boolean		canonical;
	private		boolean		savePropertyDescriptionsTopLevel;
	private		boolean		savePropertyDescriptionsSubLevels;
	private		boolean		outputingDefaultValuedProperties = true;
	private		Hashtable	aliases;
	private		Hashtable	propertiesToIgnoreHashtable = new Hashtable();
	private		Strings		propertiesToIgnore = new Strings();
	private		String		descAdditionalPersistDefaultValuePropertyName 
					= Mi_ADDITIONAL_PERSIST_DEFAULT_VALUE_OF_PROPERTY_NAME;


	public				MiModelToXMLConvertor()
		{
		}

	public		void		convert(MiiModelEntity document, OutputStream stream, String streamName, boolean canonical)
		{
		this.canonical = canonical;


		PrintWriter printWriter = new PrintWriter(stream);
		if (canonical)
			{
			printWriter.println("<?xml version = \"1.0\" encoding = \"ISO-8859-1\" ?>");
			}

		outputXML(document, printWriter, new StringIndentation(2), true);
		}

	public		void		setPropertiesToIgnore(Strings propertiesToIgnore)
		{
		this.propertiesToIgnore = propertiesToIgnore;
		for (int i = 0; i < propertiesToIgnore.size(); ++i)
			{
			propertiesToIgnoreHashtable.put(
				propertiesToIgnore.get(i), propertiesToIgnore.get(i));
			}
		}
	public		Strings		getPropertiesToIgnore()
		{
		return(propertiesToIgnore);
		}

	public		void		setOutputingDefaultValuedProperties(boolean flag)
		{
		outputingDefaultValuedProperties = flag;
		}
	public		boolean		getOutputingDefaultValuedProperties()
		{
		return(outputingDefaultValuedProperties);
		}
	public		void		setAliases(Hashtable aliases)
		{
		this.aliases = aliases;
		}
	public		Hashtable	getAliases()
		{
		return(aliases);
		}

	public		void		setSavePropertyDescriptions(boolean flag)
		{
		savePropertyDescriptionsTopLevel = flag;
		savePropertyDescriptionsSubLevels = flag;
		}
	public		boolean		getSavePropertyDescriptions()
		{
		return(savePropertyDescriptionsTopLevel);
		}
	public		void		setSavePropertyDescriptionsSubLevels(boolean flag)
		{
		savePropertyDescriptionsSubLevels = flag;
		}
	public		boolean		getSavePropertyDescriptionsSubLevels()
		{
		return(savePropertyDescriptionsSubLevels);
		}

	/**
	 * Outputs the given JNEObject as XML to the given outputStream.
	 *
	 * @param node			the data to convert to XML
	 * @param outputStream		the stream to send the XML to
	 * @param errorManager		handles any errors
	 * @param indent		the indentation
	 * @param topLevel		true if top level model entity we are converting
	 */
	protected 	 void		outputXML(
						MiiModelEntity entity,
						PrintWriter outputStream,
						StringIndentation indent,
						boolean topLevel)
		{
		MiPropertyDescriptions descs = entity.getPropertyDescriptions();

//MiDebug.println("Output XML: " + entity);
//MiDebug.println("Output XML: " + descs);
		if (entity.getType() == null)
			{
			throw new RuntimeException(
				"Unable to generate XML for Mica model entity because it has no type (which is used as the XML Enitity): " + MiModelEntity.dump(entity));
			}

		outputStream.print(indent.toString());
		outputStream.print('<');
		outputStream.print(handleSpecialCharactersInElementName(entity.getType().getName()));
		outputStream.print(">\n");

		indent.incIndent();

		Strings propertyNames = entity.getPropertyNames();
//MiDebug.println("Output XML: propertyNames" + propertyNames);
		for (int i = 0; i < propertyNames.size(); ++i)
			{
			String propertyName = propertyNames.elementAt(i);
			if (propertiesToIgnoreHashtable.get(propertyName) != null)
				{
				continue;
				}
			if (Mi_OUTPUT_PROPERTY_DESCRIPTIONS_TO_XML.equals(propertyName))
				{
				continue;
				}
			MiPropertyDescription desc = null;
			if (descs != null)
				{
				desc = descs.elementAt(propertyName);
				}

			String value = entity.getPropertyValue(propertyName);

			if ((value != null) && (value.trim().length() > 0) || ((desc != null) && (desc.isRequired())))
			    {
			    boolean outputTheValue = true;
			    if ((!outputingDefaultValuedProperties) && (desc != null) 
				&& (!"true".equalsIgnoreCase(
					(String )desc.getAdditionalProperty(descAdditionalPersistDefaultValuePropertyName))))
				{
				String defaultValue = desc.getDefaultValue();
				if (defaultValue != null)
					{
					if (desc.getUnits() != null)
						{
						try	{
							value = desc.getUnits().extractValue(value);
							} 
						catch (Throwable t) { }
						}
					if (defaultValue.equals(value))
						{
						outputTheValue = false;
						}
					else
						{
						if ((desc.getType() == Mi_DOUBLE_TYPE)
							|| (desc.getType() == Mi_POSITIVE_DOUBLE_TYPE)
							|| (desc.getType() == Mi_INTEGER_TYPE)
							|| (desc.getType() == Mi_POSITIVE_INTEGER_TYPE))
							{
							double val = Utility.toDouble(value);
							double defVal = Utility.toDouble(defaultValue);
							outputTheValue = (val != defVal);
							}
						}
					}
				}

/*
MiDebug.println("Output XML: propertyName" + propertyName);
if (desc != null)
{
MiDebug.println("Output XML: value" + value);
MiDebug.println("Output XML: desc.getDefaultValue()" + desc.getDefaultValue());
MiDebug.println("Output XML: outputTheValue" + outputTheValue);
}
*/
			    //if (((outputingDefaultValuedProperties) && (value != null) && (value.trim().length() > 0)) 
				//|| (desc == null) || (desc.getDefaultValue() != value) || (desc.isRequired()))
			    if (outputTheValue)
				{
				outputStream.print(indent.toString());
				outputStream.print('<');
				outputStream.print(handleSpecialCharactersInElementName(propertyName));
				outputStream.print(">");

				if ((descs != null) && (value != null) && (value.length() > 0))
					{
//MiDebug.println("Output XML: desc" + desc);
					if ((desc != null) && (desc.getUnits() != null))
						{
						try	{
//MiDebug.println("Output XML: value was " + value);
							String pvalue = desc.getUnits().convertInternalValueToPersistentUnits(value);

							// Equations may render a null value, in which case save the equation itself
							if (pvalue != null)
								{
								value = pvalue;
								}
//MiDebug.println("Output XML: value is now " + value);
							}
						catch (Exception e)
							{
							MiDebug.println("Failed to convert value \"" 
								+ entity.getPropertyValue(propertyName) 
								+ "\" of property: \""
								+ propertyName + "\" of entity: \"" 
								+ entity.getName() 
								+ "\" to internal units.");
							MiDebug.println(e.getMessage());
							MiDebug.printStackTrace(e);
							}
						}
					}

				outputStream.print(handleSpecialCharacters(value));
				outputStream.print("</");
				outputStream.print(handleSpecialCharactersInElementName(propertyName));
				outputStream.print(">\n");
				}
			    }
			}
		if (((savePropertyDescriptionsTopLevel) && (topLevel))
			|| ((savePropertyDescriptionsSubLevels) && (!topLevel))
			|| (entity.getPropertyValue(Mi_OUTPUT_PROPERTY_DESCRIPTIONS_TO_XML) != null))
			{
			for (int i = 0; ((descs != null) && (i < descs.size())); ++i)
				{
				MiPropertyDescription desc = descs.elementAt(i);

				outputStream.print(indent.toString());
				outputStream.print('<');
				outputStream.print(lookupAlias(Mi_PROPERTY_DESCRIPTION_TYPE_NAME));
				outputStream.print(">\n");
		
				indent.incIndent();

				outputProperty(outputStream, indent, 
					Mi_PROPERTY_NAME,
			 		desc.getName());

				if ((desc.getDisplayName() != null)
					&& ((desc.getName() == null) || (!desc.getName().equals(desc.getDisplayName()))))
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_DISPLAY_NAME, 	
						desc.getDisplayName());
					}

				outputProperty(outputStream, indent, 
					desc.getValidValuesAreSpecialCasesOnly() 
						? Mi_PROPERTY_INCLUDED_VALUES_NAME : Mi_PROPERTY_VALID_VALUES_NAME, 	
					desc.getValidValues() == null ? null : desc.getValidValues().getCommaDelimitedStrings());

				outputProperty(outputStream, indent, 
					Mi_PROPERTY_VALID_DISPLAY_VALUES_NAME, 	
					desc.getValidDisplayValues() == null ? null : desc.getValidDisplayValues().getCommaDelimitedStrings());

				outputProperty(outputStream, indent, 
					Mi_PROPERTY_TYPE_NAME, 		
					MiiPropertyTypes.propertyTypeNames[desc.getType()]);

				outputProperty(outputStream, indent, 
					Mi_PROPERTY_DEFAULT_VALUE_NAME, desc.getDefaultValue());

				if (desc.getMinimumValueIsSpecified())
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_MINIMUM_VALUE_NAME, 
						Utility.toShortString(desc.getMinimumValue()));
					}
				if (desc.getMaximumValueIsSpecified())
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_MAXIMUM_VALUE_NAME, 
						Utility.toShortString(desc.getMaximumValue()));
					}
				if (desc.getStepValueIsSpecified())
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_STEP_VALUE_NAME, 	
						Utility.toShortString(desc.getStepValue()));
					}
				if (desc.getIgnoreCase())
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_VALUES_IGNORE_CASE_NAME, 	
						true + "");
					}
				if (desc.getDisplayValuesIgnoreCase())
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_DISPLAY_VALUES_IGNORE_CASE_NAME, 	
						true + "");
					}
				if (!desc.isEditable(null))
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_EDITABLE_NAME, 	
						desc.isEditable(null) + "");
					}
				if (!desc.isViewable(null))
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_VIEWABLE_NAME, 	
						desc.isViewable(null) + "");
					}
				if (desc.isRequired())
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_REQUIRED_NAME, 	
						desc.isRequired() + "");
					}
				outputProperty(outputStream, indent, 
					Mi_PROPERTY_TOOL_HINT_NAME, 	
					desc.getToolHintHelp() == null ? null : desc.getToolHintHelp().getMessage());
				outputProperty(outputStream, indent, 
					Mi_PROPERTY_STATUS_HELP_NAME, 	
					desc.getStatusHelp() == null ? null : desc.getStatusHelp().getMessage());
				outputProperty(outputStream, indent, 
					Mi_PROPERTY_DIALOG_HELP_NAME, 	
					desc.getDialogHelp() == null ? null : desc.getDialogHelp().getMessage());

				if (desc.getExcludedValues() != null)
					{
					outputProperty(outputStream, indent, 
						Mi_PROPERTY_EXCLUDED_VALUES_NAME, 	
						desc.getExcludedValues().toCommaDelimitedString());
					}
				if (desc.getUnits() != null)
					{
					String className = desc.getUnits().getClass().getName();
					String unitsName = lookupAlias(className);

					outputProperty(outputStream, indent, 
						Mi_PROPERTY_UNITS_NAME,
					 	unitsName);
					}

				for (int j = 0; j < desc.getNumberOfAdditionalProperties(); ++j)
					{
					String name = desc.getNameOfAdditionalProperty(j);
					Object value = desc.getAdditionalProperty(name);
					
					outputProperty(outputStream, indent, name, value != null ? value.toString() : null);
					}

				indent.decIndent();
		
				outputStream.print(indent.toString());
				outputStream.print("</");
				outputStream.print(lookupAlias(Mi_PROPERTY_DESCRIPTION_TYPE_NAME));
				outputStream.print(">\n");
				}
			}

		MiModelEntityList list = entity.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity child = list.elementAt(i);
			outputXML(child, outputStream, indent, false);
			}

		indent.decIndent();

		outputStream.print(indent.toString());
		outputStream.print("</");
		outputStream.print(handleSpecialCharactersInElementName(entity.getType().getName()));
		outputStream.print(">\n");
		outputStream.flush();
		}
	protected	void		outputProperty(
						PrintWriter outputStream, 
						StringIndentation indent, 
						String name, 
						String value)
		{
		if (value != null)
			{
			outputStream.print(indent.toString());
			outputStream.print('<');
			outputStream.print(handleSpecialCharactersInElementName(name));
			outputStream.print(">");
			outputStream.print(handleSpecialCharacters(value));
			outputStream.print("</");
			outputStream.print(handleSpecialCharactersInElementName(name));
			outputStream.print(">\n");
			}
		}

	/**
	 * Gets a string which has the special characters in the given string replaced
	 * with the associated XML character sequences.
	 *
	 * @param s	the given string
	 * @return 	the (potentially) modified string
	 */
	protected 	 String		handleSpecialCharacters(String s)
		{
		StringBuffer str = new StringBuffer();

		int len = (s != null) ? s.length() : 0;
		for (int i = 0; i < len; i++)
			{
			char ch = s.charAt(i);
			switch (ch)
				{
				case '<':
					str.append("&lt;");
					break;
				case '>':
					str.append("&gt;");
					break;
				case '&':
					str.append("&amp;");
					break;
				case '"':
					str.append("&quot;");
					break;
				case '\r':
				case '\n':
					if (canonical)
						{
						str.append("&#");
						str.append(Integer.toString(ch));
						str.append(';');
						break;
						}
				// else, fall thru to default append char
				default:
					str.append(ch);
					break;

				}
			}
		return(str.toString());
		}
	protected	String		handleSpecialCharactersInElementName(String s)
		{
		StringBuffer str = new StringBuffer();

		int len = (s != null) ? s.length() : 0;
		for (int i = 0; i < len; i++)
			{
			char ch = s.charAt(i);
			switch (ch)
				{
				case '$':
					str.append("___dollar-sign___");
					break;
				// else, fall thru to default append char
				default:
					str.append(ch);
					break;

				}
			}
		return(str.toString());
		}
	protected	String		lookupAlias(String key)
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
	}

/**
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.1
 * @module 	%M%
 * @language	Java (JDK 1.2)
 */
class StringIndentation
	{
	private		String		indentIncrementStr	= "";
	private		String		indentStr		= "";
	private		int		indentIncrement;


	/**
	 * Constructs a new StringIndentation.
	 */
	public				StringIndentation()
		{
		this(4);
		}
	/**
	 * Constructs a new StringIndentation.
	 *
	 * @param indentIncrement	the number of spaces per each indentation level
	 */
	public				StringIndentation(int indentIncrement)
		{
		this.indentIncrement = indentIncrement;

		StringBuffer indentBuf = new StringBuffer();
		for (int i = 0; i < indentIncrement; ++i)
			{
			indentBuf.append(" ");
			}
		indentIncrementStr = indentBuf.toString();
		}

	/**
	 * Returns a string representing the current indentation
	 *
	 * @return		the indentation
	 */
	public		String		toString()
		{
		return(indentStr);
		}

	/**
	 * Increases the level of indentation by one.
	 */
	public		void		incIndent()
		{
		indentStr += indentIncrementStr;
		}
	/**
	 * Decreases the level of indentation by one.
	 */
	public		void		decIndent()
		{
		if (indentStr.length() < indentIncrement)
			throw new RuntimeException("Unbalanced incrementation (too many decrements)");

		indentStr = indentStr.substring(0, indentStr.length() - indentIncrement);
		}
	}




