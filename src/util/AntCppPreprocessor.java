
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

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class AntCppPreprocessor implements AntiPreprocessor
	{
	protected	ArrayList	patterns = new ArrayList();
	protected	ArrayList	replacements = new ArrayList();

	public				AntCppPreprocessor()
		{
		}

	public		void		setArguments(String commandLine) throws Exception
		{
		Strings commands = new Strings();
		commands.appendDelimitedStrings(commandLine, " ");
		String[] args = commands.toArray();

		for (int i = 0; i < args.length; ++i)
			{
			if (args[i].equals("-f"))
				{
				String specFileName = args[i + 1];
				Strings specFile = new Strings();
				specFile.loadFile(specFileName);

				for (int j = 0; j < specFile.size(); ++j)
					{
					String line = specFile.elementAt(j);
					if (line.startsWith("#define"))
						{
						StringTokenizer t = new StringTokenizer(
							line.substring("#define".length()));
						if (t.hasMoreTokens())
							{
							String replacement = "";
							String pattern = t.nextToken().trim();
							if (t.hasMoreTokens())
								{
								replacement = t.nextToken().trim();
								}
							patterns.add(pattern);
							replacements.add(replacement);
							}
						}
					else if (line.startsWith("%s"))
						{
						char delimiter = line.charAt(2);
						StringTokenizer t = new StringTokenizer(
							line.substring("%s".length()), "" + delimiter);
						if (t.hasMoreTokens())
							{
							String replacement = "";
							String pattern = t.nextToken();
							if (t.hasMoreTokens())
								{
								replacement = t.nextToken();
								}
							pattern = Strings.replaceBackslashCodesWithChars(pattern);
							replacement = Strings.replaceBackslashCodesWithChars(replacement);
							patterns.add(pattern);
							replacements.add(replacement);
							}
						}
					}
				++i;
				}
			else
				{
				throw new Exception("AntCppPreprocessor - Unknown argument in commandline: " + args[i]);
				}
			}
		}
	public		void		process(String sourceFile, String targetFile) throws Exception
		{
		String content = loadTextFile(sourceFile);


		StringBuffer result = new StringBuffer(content);
		for (int i = 0; i < patterns.size(); ++i)
			{
			String pattern = (String )patterns.get(i);
			String replacement = (String )replacements.get(i);

//System.out.println("pattern= \"" + pattern + "\"");
//System.out.println("replacement= \"" + replacement + "\"");
			int index = 0;
			int bufferAdjustment = 0;
			while ((index = content.indexOf(pattern, index)) != -1)
				{
				result.replace(
					bufferAdjustment + index, 
					bufferAdjustment + index + pattern.length(), 
					replacement);
//System.out.println("result= " + result.substring(0, 100));

				bufferAdjustment += replacement.length() - pattern.length();
				index += pattern.length();
				}
			content = result.toString();
			}

		OutputStream stream = new FileOutputStream(targetFile);
		byte[] buffer = new byte[content.length()];
    		content.getBytes(0, content.length(), buffer, 0);
		stream.write(buffer);
		stream.close();
		}
	public 		String		loadTextFile(String filename) throws Exception
		{
		InputStream in;
		if (getClass().getClassLoader() == null) // JDK1.1 fix
			{
			in = getClass().getResourceAsStream("/" + filename);
			}
		else
			{
			in = getClass().getClassLoader().getResourceAsStream("/" + filename);
			}
		if (in == null)
			{
			File file = new File(filename);
			in = new FileInputStream(file);
			}
		byte[] bytes = new byte[in.available()];
		in.read(bytes);
		return(new String(bytes));
		}
	}



