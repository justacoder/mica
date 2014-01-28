
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
import com.swfm.mica.util.Strings; 

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.ParserFactory;

import java.io.InputStream;
import java.io.IOException;
import java.util.Hashtable;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelXMLSaxLoader implements DocumentHandler, ErrorHandler
	{
	private		MiiModelDocument	doc;
	private		MiModelEntityList	entityStack;
	private		MiiModelEntity		parent;
	private		Hashtable		registeredTypes;
	private		String			streamName;


	public				MiModelXMLSaxLoader(Hashtable registeredTypes)
		{
		this.registeredTypes = registeredTypes;
		}


	public 		MiiModelDocument load(
				InputSource inputSource, String streamName, Parser parser)
				throws IOException
		{
		this.streamName = streamName;
		try	{
			parser.setDocumentHandler(this);
			parser.setErrorHandler(this);
			parser.parse(inputSource);
			}
        	catch (Exception e)
			{
			e.printStackTrace();
			throw new IOException(e.getMessage());
			}
		return(doc);
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
//System.out.println("START DOC");
		doc = new MiModelDocument();
		parent = doc;
		entityStack = new MiModelEntityList();
		entityStack.addElement(doc);
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
			e.setPropertyValue("data", data);
			}
		assignTypeToEntity(e, target);
		parent.appendModelEntity(e);
		}
	public		void		startElement(String name, AttributeList attrs)
		{
		MiModelEntity entity = new MiModelEntity();
		assignTypeToEntity(entity, name);
		parent.appendModelEntity(entity);

		int attrCount = attrs.getLength();
		for (int i = 0; i < attrCount; i++)
			{
//System.out.println("attrs.getName = " + attrs.getName(i));
//System.out.println("attrs.getValue = " + attrs.getValue(i));

			entity.setPropertyValue(attrs.getName(i), attrs.getValue(i));
			}
		entityStack.addElement(entity);
		parent = entity;
		}

	public		void		endElement(String name)
		{
		MiiModelEntity entity = entityStack.elementAt(entityStack.size() - 1);
		entityStack.removeElementAt(entityStack.size() - 1);
		parent = entityStack.elementAt(entityStack.size() - 1);

		// XMI.reference is always a separate entity
		if (!name.equals("XMI.reference"))
			{
			if (entity.getNumberOfModelEntities() == 0)
				{
				Strings names = entity.getPropertyNames();
				if (names.size() == 0)
					{
					// Handle stuff like "<name></name>"
					parent.setPropertyValue(entity.getType().getName(), "");

					MiModelDOMSupport.setRecordThatPropertyIsReallyAnENTITY(
						parent, entity.getType().getName(), "");

					entity.removeSelf();
					}
				else if ((names.size() == 1) && (names.elementAt(0).equals("XMI.value")))
					{
					parent.setPropertyValue(
						entity.getType().getName(), entity.getPropertyValue("XMI.value"));

					MiModelDOMSupport.setRecordThatPropertyIsReallyAnENTITY(
						parent, entity.getType().getName(), "XMI.value");
					
					entity.removeSelf();
					}
				else if (((names.size() == 1) && (names.elementAt(0).equals("TMP.value")))
					// Fix bug that adds xml-space to Constraints in xml4j using SAX
					|| ((names.size() == 2) && (names.contains("TMP.value")) 
					&& (names.contains("xml-space"))))
					{
					parent.setPropertyValue(
						entity.getType().getName(), entity.getPropertyValue("TMP.value"));

					MiModelDOMSupport.setRecordThatPropertyIsReallyAnENTITY(
						parent, entity.getType().getName(), "");
					
					entity.removeSelf();
					}
				}
			}
		}

	public		void		characters(char ch[], int start, int length)
		{
		String str = new String(ch, start, length);
//System.out.println("--------> characters = " + str);
		if (str.length() > 0)
			{
			String was = parent.getPropertyValue("TMP.value");
			if (was != null)
				str = was + "\n" + str;
			parent.setPropertyValue("TMP.value", str);
//System.out.println("--------> characters NOW =  "+ str);
			}
		}

	public		void		ignorableWhitespace(char ch[], int start, int length)
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
	}

