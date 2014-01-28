
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


package com.swfm.mica.util;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.util.*;

import java.io.*;
import java.util.*;

/**
 */
public class AntPreprocessor extends org.apache.tools.ant.taskdefs.Copy
	{
	protected	String		preprocessorClassName;
	protected	String		preprocessorArguments;
	protected	AntiPreprocessor preprocessor;

	/**
	 * Sets the preprocessor classname.
	 */
	public 		void 		setClassName(String className)
		{
		this.preprocessorClassName = className;
		try	{
			preprocessor = (AntiPreprocessor )Class.forName(preprocessorClassName).newInstance();
			}
		catch (Exception e)
			{
			String msg = "Failed to load preprocessor " + preprocessorClassName
				+ ". Either class not found, or class not of correct type.";
			throw new BuildException(msg, e, location);
			}
		}

	

	/**
	 * Sets the preprocessor classname.
	 */
	public 		void 		setArguments(String arguments)
		{
		this.preprocessorArguments = arguments;
		}

	/**
	 * Actually does the preprocessing.
	 * This is a good method for subclasses to override.
	 */
	protected 	void 		doFileOperations()
		{
        	if (fileCopyMap.size() > 0)
			{
			log("preprocessing " + fileCopyMap.size() + 
                		" file" + (fileCopyMap.size() == 1 ? "" : "s") + 
                		" to " + destDir.getAbsolutePath() );

			Enumeration e = fileCopyMap.keys();
			try	{
				preprocessor.setArguments(preprocessorArguments);
				}
			catch (Exception ex)
				{
				String msg = "Invalid arguments to preprocessor " + preprocessorArguments;
				throw new BuildException(msg, ex, location);
				}
			while (e.hasMoreElements())
				{
				String fromFile = (String) e.nextElement();
				String toFile = (String) fileCopyMap.get(fromFile);
	
				if( fromFile.equals( toFile ) )
					{
					log("Skipping self-copy of " + fromFile, verbosity);
					continue;
					}

                		try 	{
                    			log("Preprocessing " + fromFile + " to " + toFile, verbosity);
                    			System.out.println("Preprocessing " + fromFile + " to " + toFile);

					preprocessor.process(fromFile, toFile);
					}
				catch (Exception ex)
					{
					String msg = "Failed to preprocess " + fromFile + " to " + toFile
						+ " due to " + ex.getMessage();
					throw new BuildException(msg, ex, location);
					}
				}
			}
		}
	}

