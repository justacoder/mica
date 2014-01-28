
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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;
import org.w3c.dom.ProcessingInstruction;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
public class MiModelDOMSupport implements MiiModelTypes
	{
	private static		boolean		canonical = true;
	private static		Hashtable	registeredTypes;

	public static final String 	Mi_MICA_SPECIFIC_PROCESSING_INSTRUCTION_TYPE_NAME = "mica.";
	public static final String 	Mi_MICA_SPECIFIC_PROPERTY_PREFIX = "Mi_";
	public static final String 	Mi_PROPERTY_IS_TRIVIAL_ELEMENT = "propertyIsElement:";
	public static final String 	Mi_PROPERTY_ORDER_FOR_ELEMENT = "propertyOrder:";

	public static	MiiModelDocument	convertDOMToMiiModelDocument(
							Hashtable incomingRegisteredTypes, Document dom)
		{
		registeredTypes = incomingRegisteredTypes;


		// Iterate over children of this document
		int numNodes = 0;
		NodeList nodes = dom.getChildNodes();
		if (nodes != null)
			numNodes = nodes.getLength();

		MiiModelDocument document = new MiModelDocument();
		for (int i = 0; i < numNodes; i++)
			{
			Node node = nodes.item(i);
//System.out.println("node = " + node);
			switch (node.getNodeType())
				{
				case Node.DOCUMENT_NODE:
					insertDocumentNode(node, document);
					break;
				case Node.ELEMENT_NODE:
					insertElementNode(node, document);
					break;
				default: 
					break;
				}
			}

		// Update this to use XLink when the XLink working draft becomes a W3C recommendation
/***
		MiModelEntityTreeList list = new MiModelEntityTreeList(document);
		MiiModelEntity entity;
		while ((entity = list.toNext()) != null)
			{
			if ("XMI.reference".equals(entity.getType().getName()))
				{
				String targetID = entity.getPropertyValue("target");
				if (targetID != null)
					{
					MiiModelEntity target 
						= MiModelUtilities.searchTreeForModelEntityWithPropertyValue(
							document, "XMI.id", targetID);
					if (target != null)
						{
						MiModelRelation relation = new MiModelRelation(
							entity.getParent().getParent(), target);
						assignTypeToEntity(relation,
							entity.getParent().getType().getName() + ".reference");
						document.appendModelEntity(relation);
						}
					}
				}
			}
***/

		return(document);
		}
	private static	void		insertDocumentNode(Node node, MiiModelEntity parent)
		{
		}
	private	static	void		insertElementNode(Node node, MiiModelEntity parent)
		{
		MiModelEntity entity = new MiModelEntity();
		assignTypeToEntity(entity, node.getNodeName());
		parent.appendModelEntity(entity);

		NamedNodeMap attrs = node.getAttributes();
		int attrCount = 0;
		if (attrs != null)
			attrCount = attrs.getLength();

		for (int i = 0; i < attrCount; i++)
			{
			Node attr = attrs.item(i);
//System.out.println("attr = " + attr);
			entity.setPropertyValue(attr.getNodeName(), attr.getNodeValue());
			//setPreserveOrderOfProperties(entity, attr.getNodeName());
			}

		// Get children nodes
		NodeList children = node.getChildNodes();
		int numChildren = 0;
		if (children != null)
			numChildren = children.getLength();

		for (int i = 0; i < numChildren; i++)
			{
			Node child = children.item(i);

//System.out.println("child = " + child);
			switch (child.getNodeType())
				{
				case Node.TEXT_NODE:
					//insertTextNode(child, entity);
//System.out.println("text = " + child);
					break;
				case Node.PROCESSING_INSTRUCTION_NODE:
					String data = ((ProcessingInstruction )child).getData();
					String target = ((ProcessingInstruction )child).getTarget();//child.getNodeName()
					MiiModelEntity e;
					if (target.startsWith(Mi_MICA_SPECIFIC_PROCESSING_INSTRUCTION_TYPE_NAME))
						{
						e = MiModelIOFormatManager.makeModelEntityFromString(
								data, entity.getName(), 0);
						}
					else
						{
						e = new MiModelEntity();
						e.setPropertyValue("data", data);
						}
					assignTypeToEntity(e, target);
					entity.appendModelEntity(e);
					break;
				case Node.ELEMENT_NODE:
					/*
					if (child.getNodeName() is an attribute (of node?) and it has a 
					child node of type TEXT_NODE, then assign attr to node
					*/
					NodeList childs = child.getChildNodes();
					NamedNodeMap atts = child.getAttributes();
//System.out.println("element = " + child);

					// Force XMI.reference to always be a separate entity
					if (child.getNodeName().equals("XMI.reference"))
						{
						insertElementNode(child, entity);
						}
					else if ((childs != null)
						&& (childs.getLength() == 1) 
						&& (childs.item(0).getNodeType() == Node.TEXT_NODE))
						{
//System.out.println("1 value = " + childs.item(0).getNodeValue());
						String value = childs.item(0).getNodeValue();
						// Empty elements like <xxx>
						// 		</xxx>
						// create values with crlf and alot of spaces...
						if (value != null) 
							value = value.trim();

//System.out.println("2 value = " + value);
						entity.setPropertyValue(child.getNodeName(), value);
						setRecordThatPropertyIsReallyAnENTITY(
							entity, child.getNodeName(), "");
						}
					else if (((childs == null) || (childs.getLength() == 0))
						&& ((atts != null) && (atts.getLength() == 1)
						&& (atts.item(0).getNodeName().equals("XMI.value"))))
						{
//System.out.println("3 value = " + atts.item(0).getNodeValue());
						entity.setPropertyValue(
							child.getNodeName(), atts.item(0).getNodeValue());

						setRecordThatPropertyIsReallyAnENTITY(
							entity, child.getNodeName(), "XMI.value");
						}
					else if (((childs == null) || (childs.getLength() == 0))
						&& ((atts == null) || (atts.getLength() == 0)))
						{
						// Handle stuff like "<name></name>"
						entity.setPropertyValue(child.getNodeName(), "");
						setRecordThatPropertyIsReallyAnENTITY(
							entity, child.getNodeName(), "");
						}
					else
						{
						insertElementNode(child, entity);
						}
					break;
				case Node.COMMENT_NODE:
//System.out.println("comment = " + child);
					MiModelEntity kid = new MiModelEntity();
					assignTypeToEntity(kid, "XML.comment");
					kid.setPropertyValue(Mi_COMMENT_PROPERTY_NAME, ((Comment )child).getData());
					entity.appendModelEntity(kid);
					break;
				default:
					break;
				}
			}
		}

	protected static void		assignTypeToEntity(MiiModelEntity entity, String name)
		{
		assignTypeToEntity(entity, name, registeredTypes);
		}
	public static	void		assignTypeToEntity(
						MiiModelEntity entity, 
						String name, 
						Hashtable registeredTypes)
		{
		MiModelType type = (MiModelType )registeredTypes.get(name);
		if (type == null)
			{
			type = MiModelType.getOrMakeType(name);
			registeredTypes.put(name, type);
			}
		entity.setType(type);
		}
	// Used to convert from trivial elements to MiiModelEntity attributes and back
	// For example: setRecordThatPropertyIsReallyAnENTITY(entity, "visibility", "XMI.value")
	public static 	void		setRecordThatPropertyIsReallyAnENTITY(MiiModelEntity entity,
						String trivialEntityName, String xmiAttributeName)
		{
		Hashtable h = (Hashtable )
			entity.getUserData().getResource(
				Mi_PROPERTY_IS_TRIVIAL_ELEMENT);

		if (h == null)
			{
			h = new Hashtable(5);
			entity.getUserData().setResource(
				Mi_PROPERTY_IS_TRIVIAL_ELEMENT, h);
			}
		h.put(trivialEntityName, xmiAttributeName);
		}
	public static	Document	convertMiiModelEntityToDOM(MiiModelEntity doc, Document dom)
		{
		Node node = dom.createElement("XMI");
		dom.appendChild(node);

		convertToDOM(doc, node, dom);
		return(dom);
		}

	protected static void		convertToDOM(MiiModelEntity doc, Node parent, Document dom)
		{
		MiModelEntityList list = doc.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			Element node;
			MiiModelEntity entity = list.elementAt(i);
			String type = entity.getType().getName();
			if ("XML.comment".equals(type))
				{
				Comment comment = dom.createComment(entity.getPropertyValue(Mi_COMMENT_PROPERTY_NAME));
				parent.appendChild(comment);
				}
			else
				{
				if (type == null)
					{
					System.out.println("NO TYPE for entity = " + entity);
					continue;
					}
				if (type.startsWith(Mi_MICA_SPECIFIC_PROCESSING_INSTRUCTION_TYPE_NAME))
					{
//System.out.println("Convert to DOM Mi_MICA_SPECIFIC_PROCESSING_INSTRUCTION_TYPE_NAM");

					String data = MiModelIOFormatManager.makeStringFromModelEntity(entity);
					Node pi = dom.createProcessingInstruction(
						type,
						data);
					parent.appendChild(pi);
					continue;
					}


				node = dom.createElement(type);
System.out.println("Appending node" + node);
System.out.println("Appending node to parent " + parent);
				parent.appendChild(node);

				Hashtable h = (Hashtable )entity.getUserData().getResource(
					Mi_PROPERTY_IS_TRIVIAL_ELEMENT);

				Strings names = entity.getPropertyNames();
				for (int j = 0; j < names.size(); ++j)
					{
					if (names.elementAt(j).startsWith(Mi_MICA_SPECIFIC_PROPERTY_PREFIX))
						{
						names.removeElementAt(j);
						--j;
						}
					}
//System.out.println("names NOW = " + names);
				for (int j = 0; (names != null) && (j < names.size()); ++j)
					{
					String name = names.elementAt(j);
					String childElementAttributeName;
					if ((h != null) && ((childElementAttributeName = (String )h.get(name)) != null))
						{
						if (childElementAttributeName.length() > 0)
							{
//System.out.println("CREATE CHIKLD = " + childElementAttributeName);
							Element child = dom.createElement(name);
							child.setAttribute(childElementAttributeName,
								entity.getPropertyValue(name));
							node.appendChild(child);
							}
						else
							{
							Element child = dom.createElement(name);
							node.appendChild(child);
							Text t_child = dom.createTextNode(entity.getPropertyValue(name));
							child.appendChild(t_child);
							}
						}
					else 
						{
						node.setAttribute(name, entity.getPropertyValue(name));
						}
					}

				convertToDOM(entity, node, dom);
				}
			}
		}
	public static	void		printDOMInXMLFormat(Node node, PrintWriter outputStream)
		{
		printDOMInXMLFormat(node, outputStream, 0);
		}
	protected static void		printDOMInXMLFormat(Node node, PrintWriter outputStream, int indent)
		{
		int type = node.getNodeType();
		boolean outputEndOfElement = true;
		boolean outputEndOfElementAfterCRLF = true;
		switch (type)
			{
			case Node.DOCUMENT_NODE:
				if (canonical)
					{
					outputStream.println("<?xml version = \"1.0\" encoding = \"ISO-8859-1\" ?>");
					outputStream.print("<!DOCTYPE XMI SYSTEM \"uml.dtd\" >");
					}
				printDOMInXMLFormat(((Document)node).getDocumentElement(), outputStream, indent);
				outputStream.print('\n');
				outputStream.flush();
				break;

			case Node.ELEMENT_NODE:
				{
				outputStream.print('\n');
				outputStream.print(Utility.getIndentation(indent));
				outputStream.print('<');
				outputStream.print(node.getNodeName());
				Attr attrs[] = sortAttributes(node.getAttributes());
				for (int i = 0; i < attrs.length; i++)
					{
					Attr attr = attrs[i];
					outputStream.print(' ');
					outputStream.print(attr.getNodeName());
					outputStream.print("=\"");
					outputStream.print(handleSpecialCharacters(attr.getNodeValue()));
					outputStream.print('"');
					}
				NodeList children = node.getChildNodes();
				if (children != null)
					{
					int len = children.getLength();
					if (len == 0)
						{
						outputStream.print("/>");
						outputEndOfElement = false;
						}
					else if ((len == 1) 
						&& ((children.item(0).getNodeType() == Node.TEXT_NODE)
						|| (children.item(0).getNodeType() == Node.CDATA_SECTION_NODE)))
						{
						outputStream.print('>');
						outputEndOfElementAfterCRLF = false;
						}
					else
						{
						outputStream.print('>');
						}
					for (int i = 0; i < len; i++)
						printDOMInXMLFormat(children.item(i), outputStream, indent + 2);
					}
				break;
				}
			case Node.ENTITY_REFERENCE_NODE:
				{
				if (canonical)
					{
					NodeList children = node.getChildNodes();
					if (children != null)
						{
						int num = children.getLength();
						for (int i = 0; i < num; i++)
							printDOMInXMLFormat(children.item(i), outputStream, indent + 2);
						}
					}
				else
					{
					outputStream.print('&');
					outputStream.print(node.getNodeName());
					outputStream.print(';');
					}
                		break;
				}

			case Node.CDATA_SECTION_NODE: 
			case Node.TEXT_NODE:
				outputStream.print(handleSpecialCharacters(node.getNodeValue()));
				break;

			case Node.COMMENT_NODE:
				outputStream.print('\n');
				outputStream.print(Utility.getIndentation(indent));
				outputStream.print("<!--");
				outputStream.print(handleSpecialCharacters(node.getNodeValue()));
				outputStream.print("-->");
				break;


			case Node.PROCESSING_INSTRUCTION_NODE:
				{
				outputStream.print('\n');
				outputStream.print(Utility.getIndentation(indent));
				outputStream.print("<?");
				outputStream.print(node.getNodeName());
				String data = node.getNodeValue();
				if (data != null && data.length() > 0)
					{
					outputStream.print(' ');
					outputStream.print(data);
					}
				outputStream.print("?>");
				break;
				}
			}

		if ((type == Node.ELEMENT_NODE) && (outputEndOfElement))
			{
			if (outputEndOfElementAfterCRLF)
				{
				outputStream.print('\n');
				outputStream.print(Utility.getIndentation(indent));
				}
			outputStream.print("</");
			outputStream.print(node.getNodeName());
			outputStream.print('>');
			}
		}

	protected static String		handleSpecialCharacters(String s)
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

	protected static Attr[]		sortAttributes(NamedNodeMap attrs)
		{
		int len = (attrs != null) ? attrs.getLength() : 0;
		Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++)
			{
            		array[i] = (Attr)attrs.item(i);
			}
		for (int i = 0; i < len - 1; i++)
			{
			String name  = array[i].getNodeName();
			int    index = i;
			for (int j = i + 1; j < len; j++)
				{
				String curName = array[j].getNodeName();
				if (curName.compareTo(name) < 0)
					{
					name  = curName;
					index = j;
					}
				}
			if (index != i) 
				{
				Attr temp    = array[i];
				array[i]     = array[index];
				array[index] = temp;
				}
			}
		return(array);
		}
	}

