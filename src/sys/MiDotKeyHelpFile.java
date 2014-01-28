
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

import java.io.*;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDotKeyHelpFile implements MiiHelpFile
	{
	private		String		filename;
	private		Strings	helpTextFile;
	private		boolean		cache 		= false;

	public				MiDotKeyHelpFile(String filename, boolean cacheFile)
		{
		setFilename(filename);
		keepFileInMemoryHint(cacheFile);
		}

	public		void		setFilename(String filename)
		{
		this.filename = filename;
		if ((cache) && (filename != null))
			{
			loadFile(filename);
			}
		}

	public		void		keepFileInMemoryHint(boolean flag)
		{
		cache = flag;
		if ((cache) && (filename != null))
			{
			loadFile(filename);
			}
		}
	public		String		getMessageAssignedToKey(String key)
		{
		String dotKey = "." + key;
		StringBuffer text = new StringBuffer(1000);
		if (!cache)
			{
			BufferedReader file = null;
			try	{
				file = MiSystem.getIOManager().getInputTextResourceAsStream(filename);
				}
			catch (Exception e)
				{
				throw new IllegalArgumentException(this + ": Help file not found: " + filename);
				}
			String line;
			while ((line = Utility.readLine(file)) != null)
				{
				if (line.startsWith(dotKey))
					{
					while ((line = Utility.readLine(file)) != null)
						{
						if ((line.length() > 0) && (line.charAt(0) == '.'))
							return(text.toString());
						text = text.append(line);
						}
					}
				}
			}
		else
			{
			for (int i = 0; i < helpTextFile.size(); ++i)
				{
				String line = helpTextFile.get(i);
				if (line.startsWith(dotKey))
					{
					for (int j = i + 1; j < helpTextFile.size(); ++j)
						{
						line = helpTextFile.get(j);
						if ((line.length() > 0) && (line.charAt(0) == '.'))
							return(text.toString());
						text = text.append(line + "\n");
						}
					return(text.toString());
					}
				}
			}
		if (text.length() == 0)
			return(null);
		return(text.toString());
		}
	protected	void		loadFile(String filename)
		{
		try	{
			InputStream in = MiSystem.getIOManager().getInputResourceAsStream(filename);
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			String str = new String(bytes);
			helpTextFile = new Strings(str);
			}
		catch (Exception e)
			{
			throw new IllegalArgumentException(this + ": Help file not found: " + filename);
			}
		}
	}


