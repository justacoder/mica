
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
public class MiSystemIOManagerLocalFileFinder extends MiSystemIOManagerFileFinder implements FileFilter
	{
	private		boolean		listFiles;
	private		Strings		results;

	public		Strings		find(String rootDirectoryName) throws Exception
		{
		results = new Strings();
		init();
		File root = new File(rootDirectoryName);

		getList(root, results);
		return(results);
		}

	public		void		getList(File directory, Strings results) throws Exception
		{
		getList(directory, results, "");
		}
	public		void		getList(File directory, Strings results, String pathname) throws Exception
		{
		if ((getFileTypesSearchSpec() & Mi_MATCH_PLAIN_FILE_TYPES) != 0)
			{
			listFiles = true;
			File[] files = directory.listFiles(this);
			for (int i = 0; (files != null) && (i < files.length); ++i)
				{
				if ((getFilePathnamesReturnSpec() & Mi_RETURN_CANONICAL_PATHNAMES) != 0)
					{
					results.add(files[i].getCanonicalPath());
					}
				else if ((getFilePathnamesReturnSpec() & Mi_RETURN_RELATIVE_PATHNAMES) != 0)
					{
					results.add(pathname + files[i].getName());
					}
				else 
					{
					results.add(files[i].getName());
					}
				}
			listFiles = false;
			}
		if (((getFileTypesSearchSpec() & Mi_MATCH_DIRECTORY_FILE_TYPES) != 0)
			|| (getSearchSubdirectories()))
			{
			File[] directories = directory.listFiles(this);
			if (directories != null)
				{
				if ((getFileTypesSearchSpec() & Mi_MATCH_DIRECTORY_FILE_TYPES) != 0)
					{
					for (int i = 0; i < directories.length; ++i)
						{
						if ((getFilePathnamesReturnSpec() & Mi_RETURN_CANONICAL_PATHNAMES) != 0)
							{
							results.add(directories[i].getCanonicalPath());
							}
						else if ((getFilePathnamesReturnSpec() & Mi_RETURN_RELATIVE_PATHNAMES) != 0)
							{
							results.add(pathname + directories[i].getName());
							}
						else 
							{
							results.add(directories[i].getName());
							}
						}
					}
				if (getSearchSubdirectories())
					{
					for (int i = 0; i < directories.length; ++i)
						{
						getList(directories[i], results, directories[i].getName() + "/");
						}
					}
				}
			}
		}
	public		boolean		accept(File pathname)
		{
		if (listFiles)
			{
			if (pathname.isFile())
				{
				String fileSearchSpec = getFileNamesSearchSpec();
				Strings fileSearchSpecs = getFileNamesSearchSpecs();
				String filename = pathname.getName();
				if (getFileNamesSearchSpecIgnoreCase())
					{
					fileSearchSpecs = new Strings(fileSearchSpecs);
					for (int i = 0; i < fileSearchSpecs.size(); ++i)
						{
						fileSearchSpecs.set(i, fileSearchSpecs.get(i).toUpperCase());
						}
					filename = filename.toUpperCase();
					}
				if ((fileSearchSpec == Mi_MATCH_ALL_FILE_NAMES)
					|| (Utility.matchesWildCards(filename, fileSearchSpecs)))
					{
					setNumberOfFilesFound(getNumberOfFilesFound() + 1);
					setSizeOfFilesFound(getSizeOfFilesFound() + pathname.length());
					return(true);
					}
				}
			return(false);
			}
		else
			{
			if (pathname.isDirectory())
				{
				String fileSearchSpec = getFileNamesSearchSpec();
				Strings fileSearchSpecs = getFileNamesSearchSpecs();
				String filename = pathname.getName();
				if (getFileNamesSearchSpecIgnoreCase())
					{
					fileSearchSpecs = new Strings(fileSearchSpecs);
					for (int i = 0; i < fileSearchSpecs.size(); ++i)
						{
						fileSearchSpecs.set(i, fileSearchSpecs.get(i).toUpperCase());
						}
					filename = filename.toUpperCase();
					}
				if ((fileSearchSpec == Mi_MATCH_ALL_FILE_NAMES)
					|| (Utility.matchesWildCards(filename, fileSearchSpecs)))
					{
					return(true);
					}
				}
			return(false);
			}
		}
	}
	



