
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
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public interface MiiSystemIOManager
	{
	InputStream	getInputResourceAsStream (String resourceName) throws Exception;
	InputStream	getInputResourceAsStream (URL resource) throws Exception;
	OutputStream	getOutputResourceAsStream (String resourceName) throws Exception;
	OutputStream	getOutputResourceAsStream (URL resource) throws Exception;

	BufferedReader	getInputTextResourceAsStream (String resourceName) throws Exception;
	PrintWriter	getOutputTextResourceAsStream (String resourceName) throws Exception;
	String		getInputTextResource (String resourceName) throws Exception;

	URL		getResourceAsURL (String resourceName) throws Exception;

	boolean		copyTo (String resourceName, String targetResourceName) throws Exception;
	boolean		copyTo (URL resource, URL targetResource) throws Exception;

	boolean		exists (String resourceName);
	boolean		exists (URL resource);

	boolean		renameTo (String resourceName, String targetResourceName);
	boolean		renameTo (URL resource, URL targetResource);

	void		delete (String resourceName);
	void		delete (URL resource);

	long		lastModified (String resourceName);
	long		lastModified (URL resource);

	long		sizeOf (String resourceName);
	long		sizeOf (URL resource);

	boolean		isFile (String resourceName);
	boolean		isFile (URL resource);

	boolean		mkdirs (String resourceName);

	MiSystemIOManagerFileFinder getFileFinder();
	}

