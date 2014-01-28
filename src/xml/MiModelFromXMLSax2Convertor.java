
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.HashMap;

// JAXP packages
import javax.xml.parsers.*;
import org.xml.sax.helpers.*;

import org.xml.sax.Attributes;
import org.xml.sax.AttributeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.ParserFactory;


/**----------------------------------------------------------------------------------------------
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelFromXMLSax2Convertor implements ContentHandler, ErrorHandler, MiiPropertyTypes
	{
	private		MiiModelDocument	doc;
	private		MiModelEntityList	entityStack;
	private		MiiModelEntity		parent;
	private		Hashtable		registeredTypes;
	private		String			streamName;
	private		Hashtable		aliases;
	private		int			approximateLineNumber;
	private		boolean			validateXML;
	private		Strings			doNotCollapseTheseSimpleElementsIntoAttributes = new Strings();
//	private		Strings			standardDescriptionPropertyAliases;
	private		HashMap			elementToElementValueMap = new HashMap();

	/**
	 * Constructs a new MiModelXMLSax2Loader.
	 *
	 * @param validateXML	true if the parser should validate the XML against a DTD
	 */
	public				MiModelFromXMLSax2Convertor(boolean validateXML)
		{
		this(new Hashtable(), validateXML);
		}
	public				MiModelFromXMLSax2Convertor(Hashtable registeredTypes, boolean validateXML)
		{
		this.registeredTypes = registeredTypes;
		this.validateXML = validateXML;
//		standardDescriptionPropertyAliases = buildStandardDescriptionPropertyAliases();
		}

	/**
	 * Set to null if want to not collapse any elements into attributes
	 **/
	public		void		setDoNotCollapseTheseSimpleElementsIntoAttributes(Strings types)
		{
		doNotCollapseTheseSimpleElementsIntoAttributes = new Strings(types);
		}
	public		Strings		getDoNotCollapseTheseSimpleElementsIntoAttributes()
		{
		return(doNotCollapseTheseSimpleElementsIntoAttributes);
		}
	/**
	 * Loads the XML file at the given inputStream and creates and returns
	 * a new MiiModelEntity representation of the XML file contents.
	 *
	 * @param inputStream	the XML file
	 * @param streamName	the name of the XML file
	 */
	public		MiiModelDocument convert(InputStream inputStream, String streamName)
		{
		this.streamName = streamName;
		approximateLineNumber = 0;

		// Create a JAXP SAXParserFactory and configure it
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(validateXML);

		XMLReader xmlReader = null;
		try 	{
			// Create a JAXP SAXParser
			SAXParser saxParser = spf.newSAXParser();

			// Get the encapsulated SAX XMLReader
			xmlReader = saxParser.getXMLReader();

			// Set the ContentHandler of the XMLReader
			xmlReader.setContentHandler(this);

			// Set an ErrorHandler before parsing
			xmlReader.setErrorHandler(this);

			InputSource inputSource = new InputSource(inputStream);
			inputSource.setSystemId(streamName);

			// TSll the XMLReader to parse the XML document
			xmlReader.parse(inputSource);

//			inputStream.close();

			if (doc.getNumberOfModelEntities() == 1)
				{
				MiModelDocument doc2 = new MiModelDocument();
				MiiModelEntity entity = doc.getModelEntity(0);
				doc.removeModelEntity(entity);
				doc2.deepCopy(entity);
				doc = null;
				return(doc2);
				}
			return(doc);
			}
		catch (Exception e)
			{
			MiDebug.println("MiModelFromXMLSax2Convertor.load:"
				+ "Unable to load and parse: " + streamName);
			//MiDebug.printStackTrace(e);
			return(null);
			}
		}

	public		void		setAliases(Hashtable aliases)
		{
		this.aliases = aliases;
//		standardDescriptionPropertyAliases = buildStandardDescriptionPropertyAliases();
		}
	public		Hashtable	getAliases()
		{
		return(aliases);
		}


	protected	void		assignTypeToEntity(MiiModelEntity entity, String name)
		{
		MiModelType type = (MiModelType )registeredTypes.get(name);
		if (type == null)
			{
			type = MiModelType.getOrMakeType(name);
			registeredTypes.put(name, type);
			}
		entity.setType(type);
		}
	public		void		setDocumentLocator(Locator locator)
		{
		}
	public		void		startDocument()
		{
		doc = new MiModelDocument();
		
		if (streamName != null)
			{
			assignTypeToEntity(doc, streamName);
			}
		else
			{
			assignTypeToEntity(doc, "Unknown type of document");
			}

		parent = doc;
		entityStack = new MiModelEntityList();
		entityStack.addElement(doc);

		elementToElementValueMap.clear();
		}
	public		void		endDocument()
		{
		}

	public		void		processingInstruction (String target, String data)
		{
		MiiModelEntity e;
		if (target.startsWith(MiModelDOMSupport.Mi_MICA_SPECIFIC_PROCESSING_INSTRUCTION_TYPE_NAME))
			{
			try	{
				e = MiModelIOFormatManager.makeModelEntityFromString(
					data, parent.getType().getName(), 0);
				}
        		catch (Exception ex)
				{
				ex.printStackTrace();
				return;
				}
			}
		else
			{
			e = new MiModelEntity();
			e.setLocation(streamName + "[" + ++approximateLineNumber + "]");
			e.setPropertyValue("data", data);
			}
		assignTypeToEntity(e, target);
		parent.appendModelEntity(e);
		}
	public		void		startElement(String namespaceURI, String localName, 
						String name, Attributes attrs)
		{
		name = namespaceURI + name;

		MiModelEntity entity = new MiModelEntity();
		entity.setLocation(streamName + "[" + ++approximateLineNumber + "]");
		assignTypeToEntity(entity, name);
		parent.appendModelEntity(entity);

		int attrCount = attrs.getLength();
		for (int i = 0; i < attrCount; i++)
			{
			entity.setPropertyValue(handleSpecialCharactersInElementName(attrs.getQName(i)), attrs.getValue(i));
			}
		entityStack.addElement(entity);
		parent = entity;
		}

	public		void		endElement(String namespaceURI, String localName, String name)
		{
		MiiModelEntity entity = entityStack.elementAt(entityStack.size() - 1);
		entityStack.removeElementAt(entityStack.size() - 1);
		parent = entityStack.elementAt(entityStack.size() - 1);


		if (Mi_PROPERTY_DESCRIPTION_TYPE_NAME.equals(lookupAlias(entity.getType().getName())))
			{
			try	{
				MiPropertyDescription desc = MiModelUtilities.convertEntityToPropertyDescription(entity, aliases);
				MiPropertyDescriptions descriptions = entity.getParent().getPropertyDescriptions();
				if (descriptions == null)
					{
					descriptions = new MiPropertyDescriptions();
					entity.getParent().setPropertyDescriptions(descriptions);
					}
				if (desc != null)
					{
					descriptions.addElement(desc);
					}
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			entity.removeSelf();
			}
		else if ((entity.getNumberOfModelEntities() == 0) 
			&& (entity.getPropertyDescriptions() == null)
			&& (entity.getPropertyNames().size() == 0)
			&& (doNotCollapseTheseSimpleElementsIntoAttributes != null)
			&& (doNotCollapseTheseSimpleElementsIntoAttributes.indexOfIgnoreCase(entity.getType().getName()) == -1))
			{
			// Handle stuff like "<name></name>"
			String value = "";

//MiDebug.println("entity = " + entity);
//MiDebug.println("entity.getPropertyNames() = " + entity.getPropertyNames());
			StringBuffer valBuffer = (StringBuffer )elementToElementValueMap.get(entity);
			if (valBuffer != null)
				{
				// Handle stuff like "<name>xyz</name>"
				value = valBuffer.toString();
				}

			String parentPropertyName = entity.getType().getName();
			String parentPropertyValue = parent.getPropertyValue(parentPropertyName);
			if (parentPropertyValue != null)
				{
				value = parentPropertyValue + ", " + value;
				}
			parent.setPropertyValue(handleSpecialCharactersInElementName(parentPropertyName), value);

			entity.removeSelf();
			}
		else if (elementToElementValueMap.get(entity) != null)
			{
			StringBuffer valBuffer = (StringBuffer )elementToElementValueMap.get(entity);
			String value = valBuffer.toString();
			if (value.trim().length() > 0)
				{
				entity.setPropertyValue(Mi_RAW_XML_ELEMENT_CDATA, value);
				++approximateLineNumber;
				}
			}
		else
			{
			++approximateLineNumber;
			}
		elementToElementValueMap.remove(entity);
		}

	public		void		characters(char ch[], int start, int length)
		{
		// String str = new String(ch, start, length).trim();
		String str = new String(ch, start, length);

		if (str.length() > 0)
			{
//MiDebug.println("ccccharaters = " + str);
//MiDebug.println("parent = " + parent);
			StringBuffer was = (StringBuffer )elementToElementValueMap.get(parent);
			if (was != null)
				{
				was.append(str);
				}
			else
				{
				was = new StringBuffer(100);
				was.append(str);
				}
			elementToElementValueMap.put(parent, was);
			}
		}

	public		void		ignorableWhitespace(char ch[], int start, int length)
		{
		}


	/**
	 * A SAX 2 interface method.
	 */
	public		void		skippedEntity(String name) throws SAXException
		{
		}

	/**
	 * A SAX 2 interface method.
	 */
	public		void		endPrefixMapping(String prefix) throws SAXException
		{
		}

	/**
	 * A SAX 2 interface method.
	 */
	public		void		startPrefixMapping(String prefix, String uri) throws SAXException
		{
		}

	public		void		warning(SAXParseException ex)
		{
		System.err.println("*Warning* "+ streamName 
			+ "[" + getLocationString(ex) + "]: "+ ex.getMessage());
		}
	public		void		error(SAXParseException ex)
		{
		System.err.println("*Error* "+ streamName 
			+ "[" + getLocationString(ex) + "]: "+ ex.getMessage());
		}

	public		void		fatalError(SAXParseException ex) throws SAXException
		{
		System.err.println("*Fatal Error* " + streamName 
			+ "[" + getLocationString(ex) + "]: "+ ex.getMessage());
		throw ex;
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
	private		String		getLocationString(SAXParseException ex)
		{
		StringBuffer str = new StringBuffer();

		String systemId = ex.getSystemId();
		if (systemId != null)
			{
			int index = systemId.lastIndexOf('/');
			if (index != -1) 
				systemId = systemId.substring(index + 1);
			str.append(systemId);
			}
		str.append(ex.getLineNumber());
		str.append(':');
		str.append(ex.getColumnNumber());

		return(str.toString());
		}
	protected	String		handleSpecialCharactersInElementName(String s)
		{
		return(Utility.replaceAll(s, "___dollar-sign___", "$"));
		}
					/**------------------------------------------------------
	 				 * Converts the properties found in the given model entity to
					 * a MiPropertyDescription.
	 				 * @param entity	contains property value pairs
	 				 * @return 		the corresponding MiPropertyDescription
					 *------------------------------------------------------*/
/*** in MiModelUtilities
	private		MiPropertyDescription 	convertEntityToPropertyDescription(MiiModelEntity entity)
		{
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
				+ "Either \'values\' or \'type\' must be specified in an XML-specified property description");
MiDebug.println(MiModelEntity.dump(entity));
MiDebug.printStackTrace();
			return(null);
			}

		String defaultValue = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_DEFAULT_VALUE_NAME));
		String minValue = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_MINIMUM_VALUE_NAME));
		String maxValue = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_MAXIMUM_VALUE_NAME));
		String stepValue = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_STEP_VALUE_NAME));
		String requiredValue = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_REQUIRED_NAME));
		String editable = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_EDITABLE_NAME));
		String viewable = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_VIEWABLE_NAME));
		String displayName = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_DISPLAY_NAME));
		String toolHint = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_TOOL_HINT_NAME));
		String statusHelp = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_STATUS_HELP_NAME));
		String dialogHelp = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_DIALOG_HELP_NAME));
		String units = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_UNITS_NAME));
		String excludedValueStr = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_EXCLUDED_VALUES_NAME));
		String includedValueStr = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_INCLUDED_VALUES_NAME));
		String displayPriorityStr = entity.getPropertyValue(lookupAlias(Mi_PROPERTY_DISPLAY_PRIORITY_NAME));

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
				units = lookupAlias(units);
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
			if (!standardDescriptionPropertyAliases.contains(property))
				{
				desc.setAdditionalProperty(
					lookupAlias(property), entity.getPropertyValue(property));
				}
			}

		return(desc);
		}

***/

/***
	protected	Strings		buildStandardDescriptionPropertyAliases()
		{
		Strings standards = new Strings();

		standards.add(lookupAlias(Mi_PROPERTY_DEFAULT_VALUE_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_MINIMUM_VALUE_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_MAXIMUM_VALUE_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_STEP_VALUE_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_REQUIRED_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_EDITABLE_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_VIEWABLE_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_DISPLAY_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_TOOL_HINT_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_STATUS_HELP_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_DIALOG_HELP_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_UNITS_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_EXCLUDED_VALUES_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_INCLUDED_VALUES_NAME));
		standards.add(lookupAlias(Mi_PROPERTY_DISPLAY_PRIORITY_NAME));
		
		standards.add(Mi_PROPERTY_NAME);
		standards.add(Mi_PROPERTY_VALID_VALUES_NAME);
		standards.add(Mi_PROPERTY_TYPE_NAME);

		return(standards);
		}

***/
	}

