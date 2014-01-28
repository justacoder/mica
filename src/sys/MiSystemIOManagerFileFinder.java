
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
import java.io.File;
import java.io.FileFilter;


/**----------------------------------------------------------------------------------------------
 * This class creates a widget that allows a user to browse, find, modify and 
 * optionally choose a file.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiSystemIOManagerFileFinder
	{
	public static final String	Mi_MATCH_ALL_FILE_NAMES		= null;

	public static final int		Mi_MATCH_PLAIN_FILE_TYPES	= 1;
	public static final int		Mi_MATCH_DIRECTORY_FILE_TYPES	= 2;

	public static final int		Mi_RETURN_CANONICAL_PATHNAMES	= 1;
	public static final int		Mi_RETURN_RELATIVE_PATHNAMES	= 2;
	public static final int		Mi_RETURN_FILENAMES		= 3;

	private		String		fileNamesSearchSpec 	= Mi_MATCH_ALL_FILE_NAMES;
	private		Strings		fileNamesSearchSpecs 	= new Strings(Mi_MATCH_ALL_FILE_NAMES);
	private		int		fileTypesSearchSpec	= Mi_MATCH_PLAIN_FILE_TYPES 
								+ Mi_MATCH_DIRECTORY_FILE_TYPES;
	private		int		filePathnamesReturnSpec  = Mi_RETURN_RELATIVE_PATHNAMES;
	private		boolean		searchSubdirectories	= false;
	private		boolean		ignoreCase		= false;
	private		long		sizeOfFilesFound;
	private		int		numberOfFilesFound;


	public abstract	Strings		find(String rootDirectoryName) throws Exception;


	public		void		setFileNamesSearchSpec(String spec)
		{
		fileNamesSearchSpec = spec;
		fileNamesSearchSpecs = new Strings();
		if (spec != null)
			{
			fileNamesSearchSpecs.appendCommaDelimitedStrings(spec);
			}
		}
	public		void		setFileNamesSearchSpecIgnoreCase(boolean ignoreCase)
		{
		this.ignoreCase = ignoreCase;
		}
	public		boolean		getFileNamesSearchSpecIgnoreCase()
		{
		return(ignoreCase);
		}
	public		String		getFileNamesSearchSpec()
		{
		return(fileNamesSearchSpec);
		}
	public		Strings		getFileNamesSearchSpecs()
		{
		return(fileNamesSearchSpecs);
		}

	public		void		setFileTypesSearchSpec(int spec)
		{
		fileTypesSearchSpec = spec;
		}
	public		int		getFileTypesSearchSpec()
		{
		return(fileTypesSearchSpec);
		}

	public		void		setFilePathnamesReturnSpec(int spec)
		{
		filePathnamesReturnSpec = spec;
		}
	public		int		getFilePathnamesReturnSpec()
		{
		return(filePathnamesReturnSpec);
		}

	public		void		setNumberOfFilesFound(int num)
		{
		numberOfFilesFound = num;
		}
	public		int		getNumberOfFilesFound()
		{
		return(numberOfFilesFound);
		}

	public		void		setSizeOfFilesFound(long size)
		{
		sizeOfFilesFound = size;
		}
	public		long		getSizeOfFilesFound()
		{
		return(sizeOfFilesFound);
		}

	public		void		setSearchSubdirectories(boolean flag)
		{
		searchSubdirectories = flag;
		}
	public		boolean		getSearchSubdirectories()
		{
		return(searchSubdirectories);
		}

	protected	void		init()
		{
		sizeOfFilesFound = 0;
		numberOfFilesFound = 0;
		}
	}
