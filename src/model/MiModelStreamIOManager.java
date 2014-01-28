
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
import com.swfm.mica.util.Utility; 
import java.io.*;

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelStreamIOManager extends MiModelEntity implements MiiModelIOManager
	{
	private		String			filename;
	private		BufferedInputStream	inputStream;
	private		OutputStream		outputStream;


	public				MiModelStreamIOManager()
		{
		}
	public		void		setFilename(String filename)
		{
		this.filename = filename;
		setName(filename);
		inputStream = null;
		outputStream = null;
		}
	public		String		getFilename()
		{
		return(filename);
		}

	public		void		setOutputStream(OutputStream stream, String name)
		{
		outputStream = stream;
		filename = name;
		setName(filename);
		}
	public		void		setInputStream(BufferedInputStream stream, String name)
		{
		inputStream = stream;
		filename = name;
		setName(filename);
		}
	public		BufferedInputStream	getInputStream() throws Exception
		{
		if (inputStream != null)
			return(inputStream);
//MiDebug.println(this + "Creating new input stream for : " + filename + ". Who closes it?");
		return(new BufferedInputStream(MiSystem.getIOManager().getInputResourceAsStream(filename)));
		}
	public		OutputStream		getOutputStream() throws Exception
		{
		if (outputStream != null)
			return(outputStream);
//MiDebug.println(this + "Creating new output stream for : " + filename + ". Who closes it?");
		return(MiSystem.getIOManager().getOutputResourceAsStream(filename));
		}
	}


