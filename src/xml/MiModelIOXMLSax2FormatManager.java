
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


/**----------------------------------------------------------------------------------------------
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelIOXMLSax2FormatManager extends MiModelEntity implements MiiModelIOFormatManager
	{
	private		boolean			validateXML;

					/**
					 * Constructs a new MiModelIOXMLSax2FormatManager.
					 *
					 * @param validateXML	true if the parser should 
					 *                      validate the XML against a DTD
					 */
	public				MiModelIOXMLSax2FormatManager(boolean validateXML)
		{
		this.validateXML = validateXML;
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
					 *------------------------------------------------------*/
	public		MiiModelDocument load(InputStream inputStream, String streamName)
		{
		MiModelFromXMLSax2Convertor convertor = new MiModelFromXMLSax2Convertor(validateXML);
		MiiModelDocument document = convertor.convert(inputStream, streamName);
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
	public		void		save(MiiModelEntity document, OutputStream outputStream, String header)
		{
		boolean canonical = false;
		MiModelToXMLConvertor convertor = new MiModelToXMLConvertor();
		if (header != null)
			{
			new PrintWriter(outputStream).println(header);
			}
		convertor.convert(document, outputStream, header, canonical);
		}
	}
