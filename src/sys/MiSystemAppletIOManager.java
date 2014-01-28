
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
public class MiSystemAppletIOManager extends MiSystemIOManager implements MiiSystemIOManager
	{
	private		Applet		applet;
	private		URL		codeBase;


	public				MiSystemAppletIOManager(Applet applet)
		{
		this.applet = applet;

		codeBase = applet.getCodeBase();
		}

	public 		InputStream	getInputResourceAsStream(String resourceName) throws Exception
		{
		InputStream inputStream = null;

		resourceName = resourceName.replace('\\', '/');
		if (resourceName.charAt(0) != '/')
			{
			resourceName = "/" + resourceName;
			}
		// ---------------------------------------------------------------
		// Try the codeBase
		// ---------------------------------------------------------------
		if (codeBase != null)
			{
			try	{
				URL url = new URL(codeBase, resourceName);

				inputStream = url.openConnection().getInputStream();
				if (inputStream != null)
					return(inputStream);
				}
			catch (Exception e)
				{
				//MiDebug.println("Error occured during open of input resource: " + resourceName);
				}
			}

		// ---------------------------------------------------------------
		// Try file in classpath (i.e. package)
		// ---------------------------------------------------------------
		try	{
			inputStream = MiSystem.class.getResourceAsStream(resourceName);
			if (inputStream != null)
				{
				return(inputStream);
				}
			}
		catch (Exception e)
			{
			//MiDebug.println("Error occured during open of input resource: " + resourceName);
			}
		// ---------------------------------------------------------------
		// Try the the raw resource name
		// ---------------------------------------------------------------
		try	{
			URL url = new URL(resourceName);

			inputStream = url.openConnection().getInputStream();
			if (inputStream != null)
				return(inputStream);
			}
		catch (Exception e)
			{
			// MiDebug.println("Error occured during open of input resource: " + resourceName);
			}

		MiDebug.printStackTrace("Error occured during open of input resource: " + resourceName);
		throw new Exception("Error occured during open of input resource: " + resourceName);
		}

	public 		OutputStream	getOutputResourceAsStream(String resourceName) throws Exception
		{
		String outputStreamAddress = MiSystem.getProperty(MiSystem.Mi_APPLET_OUTPUTSTREAM_ADDRESS);

		if (outputStreamAddress != null)
			{
			try	{
				URL url = new URL(outputStreamAddress + "/" + resourceName);
				OutputStream stream = url.openConnection().getOutputStream();
				if (stream == null)
					{
					MiDebug.printStackTrace("Unable to open output resource: " + resourceName);
					throw new Exception("Unable to open output resource: " + resourceName);
					}
				return(stream);
				}
			catch (Exception e)
				{
				MiDebug.println("Error occured during open of output resource: " + resourceName);
				MiDebug.printStackTrace(e);
				throw new Exception(e.getMessage());
				}
			}
		else
			{
			return(new DevNullOutputStream(resourceName));
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
			try	{
				resource = new URL(codeBase + resourceName);
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			}
		if (resource == null)
			{
			MiDebug.printStackTrace("Unable to open resource as URL: " + resourceName);
			throw new Exception("Unable to open resource as URL: " + resourceName);
			}
		return(resource);
		}
	public	MiSystemIOManagerFileFinder	getFileFinder()
		{
		return(new MiSystemIOManagerDummyNetFileFinder());
		}

	}
class MiSystemIOManagerDummyNetFileFinder extends MiSystemIOManagerFileFinder
	{
	public		Strings		find(String rootDirectoryName) throws Exception
		{
		return(new Strings());
		}
	}

class DevNullOutputStream extends FilterOutputStream
	{
	private		String		resourceName;

	public				DevNullOutputStream(String name)
		{
		super(new ByteArrayOutputStream(1));
		resourceName = name;
		}
	public		void		close()
		{
		}
	public		void		flush()
		{
		}
	public		void		write(byte[] buffer)
		{
		}
	public		void		write(byte[] buffer, int off, int len)
		{
		}
	public		void		write(int bite)
		{
		}
	}


