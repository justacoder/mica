
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
import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.CaselessKeyHashtable;
import com.swfm.mica.util.OrderedProperties;
import java.awt.Color;
import java.applet.Applet;
import java.applet.AudioClip;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.util.Vector;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.awt.Image;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSystemApplicationIOManager extends MiSystemIOManager implements MiiSystemIOManager
	{
	public		boolean		renameTo(String resourceName, String targetResourceName)
		{
		File src = new File(resourceName);
		if (!src.renameTo(new File(targetResourceName)))
			{
			MiDebug.println("Unable to rename: \"" + resourceName 
				+ "\" to \"" + targetResourceName + "\""
				+ "(Perhaps the source file is open or no longer exists?)");
			return(false);
			}
		return(true);
		}
	public		void		delete(String resourceName)
		{
		new File(resourceName).delete();
		}
	public		long		lastModified(String resourceName)
		{
		return(new File(resourceName).lastModified());
		}
	public		boolean		exists(String resourceName)
		{
		return(new File(resourceName).exists());
		}
	public		boolean		isFile(String resourceName)
		{
		return(new File(resourceName).isFile());
		}
	public 		InputStream	getInputResourceAsStream(String resourceName) throws Exception
		{
		// 9-1-2003 put there here instead of after try current directory so that 
		// items in jar files will be seen before items on disk.
		// ---------------------------------------------------------------
		// Try file in classpath (i.e. package)
		// ---------------------------------------------------------------
		try	{
			String name = resourceName.replace('\\', '/');
			if (name.charAt(0) != '/')
				{
				name = "/" + name;
				}
			InputStream inputStream = MiSystem.class.getResourceAsStream(name);
			if (inputStream != null)
				{
				return(inputStream);
				}
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during open of input resource: " + resourceName);
			}

		// ---------------------------------------------------------------
		// Try the Mica home directory
		// ---------------------------------------------------------------
		String appHome = System.getProperty("Mi_HOME");
		if (appHome != null)
			{
			File file = new File(appHome + File.separator + resourceName);
			try	{
				if (file.exists() && (file.isFile()))
					{
					return(new FileInputStream(file));
					}
				}
			catch (Exception e)
				{
				MiDebug.println("Error occured during open of input resource: " + file);
				}
			}

		// ---------------------------------------------------------------
		// Override with a file in the user's home directory
		// ---------------------------------------------------------------
		String userHome = System.getProperty("user.home");
		File file = new File(userHome + File.separator + resourceName);
		try	{
			if (file.exists() && (file.isFile()))
				{
				return(new FileInputStream(file));
				}
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during open of input resource: " + file);
			}

		// ---------------------------------------------------------------
		// Try current directory
		// ---------------------------------------------------------------
		String currentDirectory = MiSystem.getProperty(MiSystem.Mi_CURRENT_DIRECTORY);
		file = new File(currentDirectory + File.separator + resourceName);
		try	{
			if (file.exists() && (file.isFile()))
				{
				return(new FileInputStream(file));
				}
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during open of input resource: " + file);
			}

		// ---------------------------------------------------------------
		// Try just opening a normal file...
		// ---------------------------------------------------------------
		file = new File(resourceName);
		try	{
			if (file.exists() && (file.isFile()))
				{
				return(new FileInputStream(file));
				}
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during open of input resource: " + file);
			}

		//MiDebug.println("Unable to open input resource: " + resourceName);
		//MiDebug.printStackTrace("Unable to open input resource: " + resourceName);
		throw new Exception("Unable to open input resource: \"" + resourceName + "\"");
		}
	public 		OutputStream	getOutputResourceAsStream(String resourceName) throws Exception
		{
//MiDebug.printStackTrace("getOutputResourceAsStream: " + resourceName);
		try	{
			return(new FileOutputStream(resourceName));
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during open of output resource: " + resourceName);
			MiDebug.printStackTrace(e);
			throw new Exception(e.getMessage());
			}
		}
	public 		URL		getResourceAsURL(String resourceName) throws Exception
		{
		URL resource = null;

		resourceName = resourceName.replace('\\', '/');
		if (resourceName.charAt(0) != '/')
			{
			resourceName = "/" + resourceName;
			}

		if (getClass().getClassLoader() != null)
			{
			resource = getClass().getClassLoader().getResource(resourceName);
			}
		if (resource == null)
			{
			resource = getClass().getResource(resourceName);
			}
		if (resource == null)
			{
			//MiDebug.println("Unable to open resource: " + resourceName);
			//MiDebug.printStackTrace("Unable to open resource: " + resourceName);
			throw new Exception("Unable to open resource: " + resourceName);
			}
		return(resource);
		}

	public		long		sizeOf(String resourceName)
		{
		File file = new File(resourceName);
		return(file.length());
		}
	public		boolean		mkdirs(String resourceName)
		{
		File file = new File(resourceName);
		return(file.mkdirs());
		}
	public	MiSystemIOManagerFileFinder	getFileFinder()
		{
		return(new MiSystemIOManagerLocalFileFinder());
		}
	}

