
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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiDragAndDropParticipant
	{
	static public final 	String		Mi_MiPART_FORMAT	= "MiPartFormat";
	static public final 	String		Mi_STRING_FORMAT	= "String";



			/**------------------------------------------------------
	 		 * Gets whether this is a object that can be 'picked up'
			 * to start a drag and drop operation.
			 * @return		true if this is a possible source
			 *			of drag-and-drop operations
			 *------------------------------------------------------*/
	boolean		isDragAndDropSource();


			/**------------------------------------------------------
	 		 * Gets whether this is a object that can be 'dropped on'
			 * to finish a drag and drop operation.
			 * @return		true if this is a possible target
			 *			of drag-and-drop operations
			 *------------------------------------------------------*/
	boolean		isDragAndDropTarget();


			/**------------------------------------------------------
	 		 * Import the data as specified by the given data transfer
			 * operation instance.
			 * @param transfer	contains data and format information.
			 *------------------------------------------------------*/
	void		doImport(MiDataTransferOperation transfer);


			/**------------------------------------------------------
	 		 * Export the data in the format as specified.
			 * @param format	the name of the format
			 *------------------------------------------------------*/
	Object 		doExport(String format);


			/**------------------------------------------------------
	 		 * Gets whether this class is able to import the actual
			 * data as specified by the given transfer operation. This
			 * is useful when the class accepts only certain kinds of
			 * data in a particular format.
			 * @param transfer	contains data and format information.
			 *------------------------------------------------------*/
	boolean 	supportsImportOfSpecificInstance(MiDataTransferOperation transfer);


			/**------------------------------------------------------
	 		 * Gets a list of the names of data formats that can be
			 * imported.
			 * @return 		array of names of data formats
			 *------------------------------------------------------*/
	String[] 	getSupportedImportFormats();


			/**------------------------------------------------------
	 		 * Gets a list of the names of data formats that can be
			 * exported.
			 * @return 		array of names of data formats
			 *------------------------------------------------------*/
	String[] 	getSupportedExportFormats();
	}


