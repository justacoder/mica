
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
import java.io.*;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiModelIndentedTreeIOFormatManager extends MiModelEntity implements MiiModelIOFormatManager
	{
	private		String		streamName;
	private		int		lineNumber;
	private		int		numSpacesPerTab	= 8;

	public				MiModelIndentedTreeIOFormatManager()
		{
		MiPropertyDescriptions propertyDescriptions 
						= new MiPropertyDescriptions(getClass().getName());
		//propertyDescriptions.addElement(
			//new MiPropertyDescription(Mi_FILENAME_NAME, Mi_FILENAME_TYPE, ""));
		setPropertyDescriptions(propertyDescriptions);
		}
					/**------------------------------------------------------
 					 * Creates, loads contents (the entities of type MiModelEntity)
					 * of, and returns a new MiiModelEntity from the given Stream.
					 * @param inputStream	the source of the data from which
					 *			the document contents will be
					 *			built.
					 * @param streamName	The name of the stream to be used
					 *			for error messages
					 * @return		the document
					 * @exception		IOException
					 *------------------------------------------------------*/
	public		MiiModelDocument load(InputStream inputStream, String streamName) throws IOException
		{
		MiModelEntity[] parents = new MiModelEntity[1000];
		this.streamName = streamName;
		String line;
		MiiModelDocument document = new MiModelDocument();
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		lineNumber = 1;
		while ((line = in.readLine()) != null)
			{
			int level = 0;
			for (int i = 0; i < line.length(); ++i)
				{
				if (line.charAt(i) == '\t')
					level += numSpacesPerTab;
				else if (line.charAt(i) == ' ')
					level += 1;
				else
					break;
				}
			line = line.trim();
			MiModelEntity entity;

			if ((Utility.isEmptyOrNull(line))
				|| (line.charAt(0) =='#') || (line.charAt(0) == '!'))
				{
				entity = new MiModelEntity();
				entity.setType(Mi_COMMENT_TYPE);
				entity.setPropertyValue(Mi_COMMENT_PROPERTY_NAME, line);
				}
			else 
				{
				if (!line.startsWith("name"))
					line = "name=" + line;
				entity = createModelEntityFromString(line, streamName, lineNumber);
				}
			entity.setLocation(streamName + "[" + lineNumber + "]");

			if (entity.getType() != Mi_COMMENT_TYPE)
				{
				for (int i = level + 1; i < parents.length; ++i)
					{
					parents[i] = null;
					}
				parents[level] = entity;
				}

			boolean foundParent = false;
			while (--level >= 0)
				{
				if (parents[level] != null)
					{
					parents[level].appendModelEntity(entity);
//System.out.println("PARENT: = " + parents[level]);
					foundParent = true;
					break;
					}
				}
			if (!foundParent)
				document.appendModelEntity(entity);
			++lineNumber;
			}
		MiModelIOFormatManager.postProcessesContainersAndConnections(document, true);
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
					 *------------------------------------------------------*/
	public		void		save(
						MiiModelEntity document, 
						OutputStream outputStream, 
						String header)
		{
		PrintWriter printStream = new PrintWriter(outputStream, true);

		if (header != null)
			{
			printStream.println(header);
			}
		saveEntities(printStream, "", document.getModelEntities());
		}
	protected	void		saveEntities(
						PrintWriter printStream,
						String indent,
						MiModelEntityList list)
		{
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			printStream.println(indent + createStringFromModelEntity(entity));
			if (entity.getModelEntities().size() > 0)
				saveEntities(printStream, "    ", entity.getModelEntities());
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
		return(MiModelIOFormatManager.makeModelEntityFromString(str, streamName, lineNumber));
		}
					/**------------------------------------------------------
	 				 * Creates a String describing the given MiiModelEntity.
					 * @param entity	the entity to convert to text.
					 * @return		the line of text
					 *------------------------------------------------------*/
	protected	String		createStringFromModelEntity(MiiModelEntity entity)
		{
		return(MiModelIOFormatManager.makeStringFromModelEntity(entity));
		}
	}


