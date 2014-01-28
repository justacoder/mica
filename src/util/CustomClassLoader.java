
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
import java.lang.ClassLoader;
import java.util.Hashtable;
import java.io.File;
import java.io.FileInputStream;
import com.swfm.mica.util.Strings;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class CustomClassLoader extends ClassLoader
	{
	private		Strings		searchPaths;
	private static	Hashtable 	cache 		= new Hashtable();


	public		Class		loadClass(String className, Strings searchPaths)
		{
		this.searchPaths = searchPaths;
		return(loadClass(className, false));
		}


	public synchronized Class 	loadClass(String name, boolean resolve)
		{
		Class	c;

		if ((c = getClass(name)) != null)
			{
			if (resolve)
				resolveClass(c);
			}

		if (c == null)
			System.out.println("+++ Failed to load class: " + name);
		return(c);
		}
	private		Class		getClass(String className)
		{
		Class	c;

		// Try local cache...
		c = (Class )cache.get(className);
		if (c == null)
			{
			// Try to return a system class...
			try	{
				c = findSystemClass(className); 
				}
			catch (Exception e)
				{
				}
			}
		if (c == null)
			{
			// Try to return a system class...
			try	{
				c = Class.forName(className);
				} 
			catch (Exception e)
				{
				}
			}
		if ((c == null) && (searchPaths != null))
			{
			String classFileName = className + ".class";
			for (int i = 0; i < searchPaths.size(); ++i)
				{
				String path = searchPaths.elementAt(i);
				try	{
					File file = new File(path + File.separator + classFileName);
					if (file.exists() && file.isFile())
						{
						byte[] buffer = new byte[(int )file.length()];
						FileInputStream stream = new FileInputStream(file);
						stream.read(buffer);
						c = defineClass(className, buffer, 0, buffer.length);
						break;
						}
					}
				catch (Exception e)
					{
					}
				}
			}
		if (c != null)
			cache.put(className, c);
		return(c);
		}
	}

