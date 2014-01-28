
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
import java.util.StringTokenizer;
import java.io.DataInputStream;
import java.util.Vector;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class Preprocessor
	{
	public static void main(String args[])
		{
		Utility.parseCommandLine(args);
		new Preprocessor();
		}

	public				Preprocessor()
		{
		String specFile = Utility.getCommandLineArgument("-f");
		TextFile spec;
		try	
			{
			spec = new TextFile(specFile);
			}
		catch (Exception e)
			{
			System.out.println("Preprocessor: Unable to open spec file: " + specFile);
			return;
			}
		Pair[] defines = getDefinesSpecified(spec);
		String filename = Utility.getCommandLineArgument(0);
		TextFile contents;
		try
			{
			contents = new TextFile(filename);
			}
		catch (Exception e)
			{
			System.out.println("Preprocessor: Unable to open file: " + filename);
			return;
			}
		int len = contents.getNumberOfLines();
		byte buffer[] = new byte[4000];
		for (int i = 0; i < len; ++i)
			{
			String line = contents.getLine(i);
//System.out.println("SOURCE LINE: " + line);
			for (int j = 0; j < defines.length; ++j)
				{
				line = Utility.replaceAll(line, defines[j].name, defines[j].value);
				}
//System.out.println("OUTPUT LINE: " + line);
			//System.out.println(line);
			int slen = line.length();
			line.getBytes(0, slen, buffer, 0);
			try
				{
				System.out.write(buffer, 0, slen);
				System.out.print("\n");
				}
			catch (Exception e)
				{
				System.out.println("Preprocessor: Unable to write to file: " + filename);
				return;
				}
			}
		}
	private		Pair[]		getDefinesSpecified(TextFile spec)
		{
		Vector vector = new Vector();
		int len = spec.getNumberOfLines();
		for (int i = 0; i < len; ++i)
			{
			String orig = null;
			String replacement;
			String line = spec.getLine(i);
			if (line.startsWith("#define"))
				{
				StringTokenizer t = new StringTokenizer(line.substring("#define".length()));
				if (t.hasMoreTokens())
					{
					replacement = "";
					orig = t.nextToken().trim();
					if (t.hasMoreTokens())
						{
						replacement = t.nextToken().trim();
						}
					vector.addElement(new Pair(orig, replacement));
					}
				}
			}
		Pair[] defines = new Pair[vector.size()];
		for (int i = 0; i < vector.size(); ++i)
			{
			defines[i] = (Pair )vector.elementAt(i);
			}
		return(defines);
		}
	}

