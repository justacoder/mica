
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
public abstract class MiSystemIOManager implements MiiSystemIOManager
	{
	public 		InputStream	getInputResourceAsStream(URL resourceName) throws Exception
		{
		try	{
			return(resourceName.openConnection().getInputStream());
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during open of input resource: " + resourceName);
			MiDebug.printStackTrace(e);
			throw new Exception(e.getMessage());
			}
		}
	public 		OutputStream	getOutputResourceAsStream(URL resourceName) throws Exception
		{
		try	{
			return(resourceName.openConnection().getOutputStream());
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during open of output resource: " + resourceName);
			MiDebug.printStackTrace(e);
			throw new Exception(e.getMessage());
			}
		}
	public		BufferedReader	getInputTextResourceAsStream(String resourceName) throws Exception
		{
		InputStream inputStream = getInputResourceAsStream(resourceName);
		if (inputStream != null)
			{
			return(new BufferedReader(new InputStreamReader(inputStream)));
			}
		return(null);
		}
	public		PrintWriter	getOutputTextResourceAsStream(String resourceName) throws Exception
		{
		return(new PrintWriter(getOutputResourceAsStream(resourceName)));
		}
	public		String		getInputTextResource(String resourceName) throws Exception
		{
		InputStream in = getInputResourceAsStream(resourceName);
		try	{
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			return(new String(bytes));
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during open of input text resource: " + resourceName);
			MiDebug.printStackTrace(e);
			throw new Exception(e.getMessage());
			}
		}
	public		boolean		exists(String resourceName)
		{
		try	{
			return(exists(getResourceAsURL(resourceName)));
			}
		catch (Exception e)
			{
			return(false);
			}
		}
	public		boolean		exists(URL resource)
		{
		try	{
			InputStream in = resource.openStream();
			if (in != null)
				{
				in.close();
				return(true);
				}
			}
		catch (Exception e)
			{
			}
		return(false);
		}
	public		boolean		copyTo(String resource, String targetResource) throws Exception
		{
		InputStream in = getInputResourceAsStream(resource);
		OutputStream out = getOutputResourceAsStream(targetResource);
		try	{
			byte buffer[] = new byte[(int )in.available()];
			in.read(buffer);
			out.write(buffer);
			in.close();
			out.close();
			return(true);
			}
		catch (IOException e)
			{
			MiDebug.println("Unable to copy file: " + resource + " to " + targetResource);
			MiDebug.printStackTrace(e);
			throw new Exception(e.getMessage());
			}
		}
	public		boolean		copyTo(URL resource, URL targetResource) throws Exception
		{
		InputStream in = getInputResourceAsStream(resource);
		OutputStream out = getOutputResourceAsStream(targetResource);
		try	{
			byte buffer[] = new byte[(int )in.available()];
			in.read(buffer);
			out.write(buffer);
			in.close();
			out.close();
			return(true);
			}
		catch (IOException e)
			{
			MiDebug.println("Unable to copy file: " + resource + " to " + targetResource);
			MiDebug.printStackTrace(e);
			throw new Exception(e.getMessage());
			}
		}

	public		boolean		renameTo(String resourceName, String targetResourceName)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: renameTo: " + resourceName + " to " + targetResourceName);
		return(false);
		}
	public		boolean		renameTo(URL resource, URL targetResource)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: renameTo: " + resource + " to " + targetResource);
		return(false);
		}
	public		void		delete(String resourceName)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: delete: " + resourceName);
		}
	public		void		delete(URL resource)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: delete: " + resource);
		}

	public		long		lastModified(URL resource)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: lastModified: " + resource);
		return(0);
		}
	public		long		lastModified(String resourceName)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: lastModified: " + resourceName);
		return(0);
		}
	public		boolean		isFile(String resourceName)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: isFile: " + resourceName);
		return(false);
		}
	public		boolean		isFile(URL resource)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: isFile: " + resource);
		return(false);
		}
	public		long		sizeOf(String resourceName)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: sizeOf: " + resourceName);
		return(0);
		}
	public		long		sizeOf(URL resource)
		{
		try	{
			InputStream in = getInputResourceAsStream(resource);
			return(in.available());
			}
		catch (Exception e)
			{
			MiDebug.println("Unable to get sizeOf resource: " + resource);
			MiDebug.printStackTrace(e);
			return(0);
			}
		}
	public		boolean		mkdirs (String resourceName)
		{
		MiDebug.printStackTrace("UNIMPLEMENTED: mkdirs: " + resourceName);
		return(false);
		}
	}

