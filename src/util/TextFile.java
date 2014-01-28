
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Toolkit                   *
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
import com.swfm.mica.util.IntVector;
import java.io.*;

/**
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class TextFile
	{
	private		int[]		lineOffsets;
	private		String		source;
	private		String		sourceFileName;

	public				TextFile(String filename) throws Exception
		{
		File file = new File(filename);
		if (!file.exists())
			{
			sourceFileName = filename;
			source = new String("");
			lineOffsets = new int[0];
			return;
			}
		load(filename, new DataInputStream(new FileInputStream(file)));
		}
	public				TextFile(String filename, InputStream stream) throws Exception
		{
		load(filename, stream);
		}
	protected	void		load(String filename, InputStream stream) throws Exception
		{
		sourceFileName = filename;

		byte[] 	buffer;
 
		buffer = new byte[stream.available()];
		stream.read(buffer);
		source = new String(buffer);

		if (source.length() == 0)
			{
			lineOffsets = new int[0];
			return;
			}

		IntVector tmpVec = new IntVector(100, 100);
		int i = -1;
		do
			{
			tmpVec.addElement(i);
			i = source.indexOf('\n', i + 1);
			} while (i >= 0);

		if (source.charAt(source.length() - 1) != '\n')
			tmpVec.addElement(source.length());

		lineOffsets = tmpVec.toArray();
		}
	public		String		getFilename()
		{
		return(sourceFileName);
		}

	public		int		getNumberOfLines()
		{
		return(lineOffsets.length == 0 ? 0 : lineOffsets.length - 1);
		}

					// lineNumber from 0
	public		String		getLine(int lineNumber)
		{
		if (source == null)
			return("No source file available.");
		return(source.substring(lineOffsets[lineNumber] + 1, lineOffsets[lineNumber + 1]));
		}

	public		String[]	getLines()
		{
		String[] lines = new String[getNumberOfLines()];
		for (int lineNumber = 0; lineNumber < getNumberOfLines(); ++lineNumber)
			{
			lines[lineNumber] = source.substring(
				lineOffsets[lineNumber] + 1, lineOffsets[lineNumber + 1]);
			}
		return(lines);
		}
	public		String		getText()
		{
		return(source);
		}
	public		Strings		getStrings()
		{
		Strings strings = new Strings();
		for (int lineNumber = 0; lineNumber < getNumberOfLines(); ++lineNumber)
			{
			strings.addElement(source.substring(
				lineOffsets[lineNumber] + 1, lineOffsets[lineNumber + 1]));
			}
		return(strings);
		}
	public		int		searchForward(String searchString, int startingLineNumber)
		{
		for (int i = startingLineNumber; i < getNumberOfLines(); ++i)
			{
			if (getLine(i).indexOf(searchString) != -1)
				{
				return(i);
				}
			}
		return(-1);
		}
	public		int		searchBackward(String searchString, int startingLineNumber)
		{
		for (int i = startingLineNumber; i >= 0; --i)
			{
			if (getLine(i).indexOf(searchString) != -1)
				{
				return(i);
				}
			}
		return(-1);
		}
	public		void		appendLine(String line)
		{
		if ((source.length() > 0) && (source.charAt(source.length() - 1) != '\n'))
			source = source + "\n" + line;
		else
			source = source + line;

		int[] offsets = new int[lineOffsets.length + 1];
		System.arraycopy(lineOffsets, 0, offsets, 0, lineOffsets.length);
		lineOffsets = offsets;
		lineOffsets[lineOffsets.length - 1] = source.length() - 1;
		}
	public		void		removeLine(int index)
		{
		int startOffset = lineOffsets[index];
		int[] offsets = new int[lineOffsets.length - 1];
		if (index < lineOffsets.length - 1)
			{
			int endOffset = lineOffsets[index + 1];
			source = source.substring(0, startOffset) + source.substring(endOffset);
			System.arraycopy(lineOffsets, 0, offsets, 0, index);
			System.arraycopy(lineOffsets, index + 1, offsets, index, lineOffsets.length - index - 1);
			}
		else
			{
			System.arraycopy(lineOffsets, 0, offsets, 0, lineOffsets.length - 1);
			source = source.substring(0, startOffset);
			}
		lineOffsets = offsets;
		}
	public		void		save() throws Exception
		{
		save(new PrintStream(new FileOutputStream(sourceFileName)));
		}
	public		void		save(OutputStream stream) throws Exception
		{
		byte[] buffer = new byte[source.length()];
    		source.getBytes(0, source.length(), buffer, 0);
		stream.write(buffer);
		}
	}

