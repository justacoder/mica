
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
import java.util.Date;
import java.util.StringTokenizer;
import java.io.*;

/*
Add MiRandomModelDataIOFormatManager for black box testing
*/

/**----------------------------------------------------------------------------------------------
 * This class implements the MiiModelIOFormatManager interface.
 * It reads and writes MiiModelDocuments to and from Streams. The
 * format of the stream is lines of ASCII text, each line a seperate
 * entity. If the first character of a line is a '#' or a '!' it is
 * considered a comment. Entities are assumed to be sequences of 
 * name/value pairs. For example:
 *
 *     "name = entity1, color = red, type = node" 
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelIOFormatManager extends MiModelEntity implements MiiModelIOFormatManager, MiiModelTypes, MiiNames
	{
	private static final String	NAME_NAME	= "name";
	private		String		streamName;
	private		int		lineNumber;

					/**------------------------------------------------------
	 				 * Constructs a new MiModelIOFormatManager.
					 *------------------------------------------------------*/
	public 				MiModelIOFormatManager()
		{
		}
					/**------------------------------------------------------
 					 * Creates, loads contents (the entities of type MiModelEntity)
					 * of, and returns a new MiiModelDocument from the given Stream.
					 * @param inputStream	the source of the data from which
					 *			the document contents will be
					 *			built.
					 * @param streamName	The name of the stream to be used
					 *			for error messages
					 * @return		the document
					 * @exception		IOException
					 * @implements		MiiModelIOFormatManager#load
					 *------------------------------------------------------*/
	public 		MiiModelDocument load(InputStream inputStream, String streamName) throws IOException
		{
		this.streamName = streamName;
		String line;
		MiiModelDocument document = new MiModelDocument();

		// BROKEN jdk1.2beta2 (see MiModelFormattedIOManager.java for description)
		// BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

		DataInputStream in = new DataInputStream(inputStream);
		lineNumber = 1;
		while ((line = in.readLine()) != null)
			{
			MiModelEntity entity = createModelEntityFromString(line, streamName, lineNumber);
			document.appendModelEntity(entity);
			++lineNumber;
			}
		postProcessesContainersAndConnections(document, true);
		return(document);
		}
					/**------------------------------------------------------
 					 * Saves the contents (the entities of type MiModelEntity)
					 * of the given document to the given Stream.
					 * @param document	The document to save
					 * @param outputStream	the destination of the data built
					 *			from the document contents.
					 * @param header	The first line output that identifies
					 *			the format of the data
					 * @implements		MiiModelIOFormatManager#save
					 *------------------------------------------------------*/
	public		void		save(MiiModelEntity document, OutputStream outputStream, 
						String header)
		{
		PrintWriter printStream = new PrintWriter(outputStream, true);

		if (header != null)
			{
			printStream.println(header);
			}
		MiModelEntityList list = document.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			printStream.println(makeStringFromModelEntity(list.elementAt(i)));
			}
		}
					/**------------------------------------------------------
	 				 * Creates a MiModelEntity from the given line of text.
					 * The entity is assigned the given line number.
					 * @param str		the text describing the entity
					 * @param streamName	the name of the source of the
					 *			text to be used for error messages
					 * @param lineNumber	the line number of the entity
					 * @return		the entity
					 *------------------------------------------------------*/
	protected	MiModelEntity	createModelEntityFromString(String str, String streamName, int lineNumber)
		{
		return(makeModelEntityFromString(str, streamName, lineNumber));
		}

					/**------------------------------------------------------
	 				 * Creates a MiModelEntity from the given line of text.
					 * The entity is assigned the given line number. This
					 * method is static and publically available so that 
					 * it may be used to parse any line of form: 
					 * "<name> = <value>, <name> = <value>, ..."
					 * @param str		the text describing the entity
					 * @param streamName	the name of the source of the
					 *			text to be used for error messages
					 * @param lineNumber	the line number of the entity
					 * @return		the entity
					 *------------------------------------------------------*/
	public static	MiModelEntity	makeModelEntityFromString(String str, String streamName, int lineNumber)
		{
		MiModelEntity entity = new MiModelEntity();
		entity.setLocation(streamName + "[" + lineNumber + "]");

		if (str.length() == 0)
			{
			entity.setPropertyValue(Mi_COMMENT_PROPERTY_NAME, "#\n");
			entity.setType(Mi_COMMENT_TYPE);
			return(entity);
			}
		if ((str.charAt(0) =='#') || (str.charAt(0) == '!'))
			{
			entity.setPropertyValue(Mi_COMMENT_PROPERTY_NAME, str);
			entity.setType(Mi_COMMENT_TYPE);
			return(entity);
			}

		int 		index 		= 0;
		StringBuffer 	name		= new StringBuffer();
		StringBuffer 	value		= new StringBuffer();
		StringBuffer 	result		= name;
		int 		strLength	= str.length();
		char 		lookingForTerminatingChar = '\0';

		if ((strLength > 0) 
			&& (str.charAt(strLength - 1) == '\n')
			|| (str.charAt(strLength - 1) == '\r'))
			{
			--strLength;
			}

		while (index < strLength)
			{
			char ch = str.charAt(index++);
			if (ch == '\0')
				{
				_setValue(entity, name, value, streamName, lineNumber);
				return(entity);
				}
			if (ch == '=')
				{
				if (lookingForTerminatingChar == '\0')
					{
					result = value;
					}
				else
					{
					result.append(ch);
					}
				}
			else if (ch == ',')
				{
				if (lookingForTerminatingChar == '\0')
					{
					_setValue(entity, name, value, streamName, lineNumber);
					name = new StringBuffer();
					value = new StringBuffer();
					result = name;
					}
				else
					{
					result.append(ch);
					}
				}
			else if (ch == lookingForTerminatingChar)
				{
				lookingForTerminatingChar = '\0';
				}
			else if (ch == '#')
				{
				if (lookingForTerminatingChar == '\0')
					{
					_setValue(entity, name, value, streamName, lineNumber);
					return(entity);
					}
				else
					{
					result.append(ch);
					}
				}
			else if (ch == '"')
				{
				if (lookingForTerminatingChar == '\0')
					lookingForTerminatingChar = '"';
				else
					result.append(ch);
				}
			else if (ch == '\\')
				{
				if (index >= strLength)
					result.append(ch);
				else
					{
					ch = str.charAt(index++);
					if (ch == 'r')
						result.append('\r');
					else if (ch == 'n')
						result.append('\n');
					else if (ch == 't')
						result.append('\t');
					else 
						{
						result.append(ch);
						}
					}
				}
			else
				{
				result.append(ch);
				}
			}
		_setValue(entity, name, value, streamName, lineNumber);
		return(entity);
		}
					/**------------------------------------------------------
	 				 * Adds a name/value pair to the given entity. This checks
					 * to make sure that the value is valid (i.e. not "") and
					 * if not, generates an error message.
					 * @param entity	the entity
					 * @param name		the entity attribute name
					 * @param value		the entity attribute value
					 * @param streamName	where the entity came from
					 * @param lineNumber	what line number the enttiy came from
					 *------------------------------------------------------*/
	private static	void		_setValue(
						MiiModelEntity entity,
						StringBuffer name, 
						StringBuffer value,
						String streamName, 
						int lineNumber)
		{
		if (value.length() == 0)
			{
			MiDebug.println(streamName +"[" + lineNumber + "]"
				+ ": Attribute: " + name + " is being assigned no corresponding value");
			}
		String nameStr = name.toString().trim();
		String valueStr = value.toString().trim();

		if (nameStr.equalsIgnoreCase("name"))
			entity.setName(valueStr);

		if (valueStr.equals(Mi_NULL_VALUE_NAME))
			valueStr = null;
		entity.setPropertyValue(nameStr, valueStr);
		}
					/**------------------------------------------------------
	 				 * Creates a String describing the given MiiModelEntity.
					 * @param entity	the entity to convert to text.
					 * @return		the line of text
					 *------------------------------------------------------*/
	protected	String		createStringFromModelEntity(MiiModelEntity entity)
		{
		return(makeStringFromModelEntity(entity));
		}
					/**------------------------------------------------------
	 				 * Creates a String describing the given MiiModelEntity.
					 * @param entity	the entity to convert to text.
					 * @return		the line of text
					 *------------------------------------------------------*/
	public static	String		makeStringFromModelEntity(MiiModelEntity entity)
		{
		StringBuffer strBuf = new StringBuffer();

		if (entity.getType() == Mi_COMMENT_TYPE)
			{
			return(entity.getPropertyValue(Mi_COMMENT_PROPERTY_NAME));
			}

		// Write out name as the first parameter
		Strings names = entity.getPropertyNames();
		String name = entity.getName();
		boolean wroteKeyValuePair = false;
		if (name != null)
			{
			strBuf.append(NAME_NAME + "=" + name);
			wroteKeyValuePair = true;
			}

		for (int i = 0; i < names.size(); ++i)
			{
			String key = names.elementAt(i);
			if (key.equalsIgnoreCase(NAME_NAME))
				continue;

			if (wroteKeyValuePair)
				strBuf.append(", ");

			wroteKeyValuePair = true;

			strBuf.append(key);
			strBuf.append("=");
			String val = entity.getPropertyValue(key);
			val = Utility.replaceAll(val, "\t", "\\t");
			val = Utility.replaceAll(val, "\n", "\\n");
			if ((val.indexOf(' ') != -1) || (val.indexOf(',') != -1))
				{
				strBuf.append("\"");
				strBuf.append(val);
				strBuf.append("\"");
				}
			else
				{
				strBuf.append(val);
				}
			}
		return(strBuf.toString());
		}
	public static	void		postProcessesContainersAndConnections(
						MiiModelEntity document, 
						boolean createNodesAtEndsOfDanglingReferences)
		{
		// Look for type == Mi_CONNECTION_TYPE_NAME (and then Mi_CONNECTION_SOURCE_NAME 
		// and Mi_CONNECTION_DESTINATION_NAME properties)
		MiModelEntityList list = document.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String typeName = entity.getPropertyValue(Mi_TYPE_NAME);
			if (document.equals(Mi_CONNECTION_TYPE_NAME, typeName))
				{
				MiiModelRelation relation = new MiModelRelation();
				String sourceName = entity.getPropertyValue(Mi_CONNECTION_SOURCE_NAME);
				if (sourceName != null)
					{
					MiiModelEntity source = list.elementAt(sourceName);
					if (source != null)
						{
						relation.setSource(source);
						}
					else if (createNodesAtEndsOfDanglingReferences)
						{
						source = new MiModelEntity();
						document.appendModelEntity(source);
						source.setPropertyValue(Mi_NAME_NAME, sourceName);
						relation.setSource(source);
						}
					}
				String destName = entity.getPropertyValue(Mi_CONNECTION_DESTINATION_NAME);
				if (destName != null)
					{
					MiiModelEntity dest = list.elementAt(destName);
					if (dest != null)
						{
						relation.setDestination(dest);
						}
					else if (createNodesAtEndsOfDanglingReferences)
						{
						dest = new MiModelEntity();
						document.appendModelEntity(dest);
						dest.setPropertyValue(Mi_NAME_NAME, destName);
						relation.setDestination(dest);
						}
					}

				document.removeModelEntity(entity);
				document.appendModelEntity(relation);
				--i;
				relation.setName(entity.getName());
				relation.setLocation(entity.getLocation());
				}
			}

		// Look for Mi_CONNECTED_FROM_NAME and Mi_CONNECTED_TO_NAME
		list = document.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String fromNames = entity.getPropertyValue(Mi_CONNECTED_FROM_NAME);
			if (fromNames != null)
				{
				StringTokenizer t = new StringTokenizer(fromNames, "(),");
				while (t.hasMoreTokens())
					{
					String otherName = t.nextToken().trim();
					MiiModelEntity source = list.elementAt(otherName);
					if (source != null)
						{
						MiModelRelation relation = new MiModelRelation();
						relation.setSource(source);
						relation.setDestination(entity);
						relation.setName("Connect-to-" + entity.getName());
						relation.setLocation(entity.getLocation());
						document.appendModelEntity(relation);
						}
					else if (createNodesAtEndsOfDanglingReferences)
						{
						MiModelRelation relation = new MiModelRelation();
						source = new MiModelEntity();
						source.setPropertyValue(Mi_NAME_NAME, otherName);
						relation.setSource(source);
						relation.setDestination(entity);
						relation.setName("Connect-to-" + entity.getName());
						relation.setLocation(entity.getLocation());
						document.appendModelEntity(source);
						document.appendModelEntity(relation);
						}
					}
				}
			String toNames = entity.getPropertyValue(Mi_CONNECTED_TO_NAME);
			if (toNames != null)
				{
				StringTokenizer t = new StringTokenizer(toNames, "(),");
				while (t.hasMoreTokens())
					{
					String otherName = t.nextToken().trim();
					MiiModelEntity dest = list.elementAt(otherName);
					if (dest != null)
						{
						MiModelRelation relation = new MiModelRelation();
						relation.setDestination(dest);
						relation.setSource(entity);
						relation.setName("Connect-from-" + entity.getName());
						relation.setLocation(entity.getLocation());
						document.appendModelEntity(relation);
						}
					else if (createNodesAtEndsOfDanglingReferences)
						{
						MiModelRelation relation = new MiModelRelation();
						dest = new MiModelEntity();
						dest.setPropertyValue(Mi_NAME_NAME, otherName);
						relation.setSource(entity);
						relation.setDestination(dest);
						relation.setName("Connect-from-" + entity.getName());
						relation.setLocation(entity.getLocation());
						document.appendModelEntity(dest);
						document.appendModelEntity(relation);
						}
					}
				}
			}

		// Look for Mi_CONTAINER_NAME and ???Mi_CONTAINER_OF_NAME
		MiModelEntityTreeList deepList = new MiModelEntityTreeList(document);
		list = document.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String containerName = entity.getPropertyValue(Mi_CONTAINER_NAME);
			if (containerName != null)
				{
				MiiModelEntity container = deepList.elementAt(containerName);
				if (container != null)
					{
					document.removeModelEntity(entity);
					container.appendModelEntity(entity);
					--i;
					}
				else if ((createNodesAtEndsOfDanglingReferences) 
					&& (!document.equals(containerName, document.getName())))
					{
					container = new MiModelEntity();
					container.setPropertyValue(Mi_NAME_NAME, containerName);
					document.appendModelEntity(container);
					container.appendModelEntity(entity);
					}
				}
			}
		}
	}

