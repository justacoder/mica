
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**----------------------------------------------------------------------------------------------
 * This is the interface for classes that support the reading and
 * writing of MiiModelDocuments from and to Streams. The format of
 * data in the stream is totally up to the implementer of this 
 * interface. See the MiModelInputOutput class for a sample
 * implementation.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiModelIOFormatManager extends MiiModelEntity
	{
				/**------------------------------------------------------
 				 * Creates, loads contents (the entities of type MiModelEntity)
				 * of, and returns a new MiiModelDocument from the given Stream.
				 *
				 * @param inputStream	the source of the data from which
				 *			the document contents will be
				 *			built.
				 * @param streamName	The name of the stream to be used
				 *			for error messages
				 * @return		the document
				 * @exception		IOException
				 *------------------------------------------------------*/
	MiiModelDocument	load(InputStream inputStream, String streamName) throws IOException;


				/**------------------------------------------------------
 				 * Saves the contents (the entities of type MiModelEntity)
				 * of the given document to the given Stream.
				 *
				 * @param document	The document to save
				 * @param outputStream	the destination of the data built
				 *			from the document contents.
				 * @param header	The first line output that identifies
				 *			the format of the data
				 *------------------------------------------------------*/
	void			save(MiiModelEntity document, OutputStream outputStream, String header);
	}

