
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
import java.util.ArrayList;
import java.io.*;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class Strings
	{
	private		ArrayList	list = new ArrayList(2);

	public				Strings()
		{
		}
	public				Strings(Strings values)
		{
		if (values != null)
			{
			append(values);
			}
		}
	public				Strings(String values)
		{
		this(values, true);
		}
					/**------------------------------------------------------
					 * Creates an instance of the Strings class.
	 				 * @param values		the String(s) with which to
					 *				populate this. Null indicates
					 *				this will initially contain no 
					 *				elements
					 * @param valuesMayContainMoreThanOneString true if linefeeds
					 *				(and "\n"s) should cause the given
					 *				value to be parsed into multiple 
					 *				strings each of which is added as
					 *				an element to this Strings instance
					 *				Note that this can be a problem
					 *				on WINDOWS which may contain
					 *				\n in file path names.
					 *------------------------------------------------------*/
	public				Strings(String values, boolean valuesMayContainMoreThanOneString)
		{
		if (values != null)
			{
			if (valuesMayContainMoreThanOneString)
				appendLineFeedDelimitedLines(values);
			else
				list.add(values);
			}
		}
	public				Strings(String value1, String value2)
		{
		if (value1 != null)
			appendLineFeedDelimitedLines(value1);
		if (value2 != null)
			appendLineFeedDelimitedLines(value2);
		}
	public				Strings(String value1, String value2, String value3)
		{
		if (value1 != null)
			appendLineFeedDelimitedLines(value1);
		if (value2 != null)
			appendLineFeedDelimitedLines(value2);
		if (value3 != null)
			appendLineFeedDelimitedLines(value3);
		}
	public				Strings(String[] values)
		{
		if (values != null)
			{
			append(values);
			}
		}
	public		String		get(int index)
		{
		return((String )list.get(index));
		}
	public		String		elementAt(int index)
		{
		return((String )list.get(index));
		}
	public		void		set(int index, String str)
		{
		list.set(index, str);
		}
	public		void		setElementAt(String str, int index)
		{
		list.set(index, str);
		}
	public		void		add(int index, String str)
		{
		list.add(index, str);
		}
	public		void		insertElementAt(String str, int index)
		{
		if (index == list.size())
			{
			list.add(str);
			}
		else
			{
			list.add(index, str);
			}
		}
	public		void		add(String str)
		{
		list.add(str);
		}
	public		void		addElement(String str)
		{
		list.add(str);
		}
	public		void		addElement(Object obj)
		{
		list.add(obj == null ? "(null)" : obj.toString());
		}
	public 		boolean		removeElementAt(int index)
		{
		return(list.remove(index) != null);
		}
	public 		Object		remove(int index)
		{
		return(list.remove(index));
		}
	public 		boolean		removeAll(Strings other)
		{
		boolean removedSomething = false;
		for (int i = 0; i < other.size(); ++i)
			{
			list.remove(other.get(i));
			removedSomething = true;
			}
		return(removedSomething);
		}
	public 		boolean		removeElement(String toMatch)
		{
		for (int i = 0; i < size(); ++i)
			{
			if (elementAt(i).equals(toMatch))
				{
				list.remove(i);
				return(true);
				}
			}
		return(false);
		}
	public 		void		removeLastElement()
		{
		if (size() > 0)
			{
			removeElementAt(size() - 1);
			}
		}

	public 		void		removeDuplicates()
		{
		if (size() > 0)
			{
			for (int i = 0; i < size(); ++i)
				{
				String toMatch = elementAt(i);
				for (int j = i + 1; j < size(); ++j)
					{
					if (elementAt(j).equals(toMatch))
						{
						removeElementAt(j);
						--j;
						}
					}
				}
			}
		}

	public		String		lastElement()
		{
		return(elementAt(size() - 1));
		}

	public 		void		clear()
		{
		list.clear();
		}
	public 		void		removeAllElements()
		{
		list.clear();
		}
	public		int		size()
		{
		return(list.size());
		}

	public 		int		indexOf(String toMatch)
		{
		return(indexOf(toMatch, 0));
		}
	public 		int		indexOf(String toMatch, int fromIndex)
		{
		int num = size();
		for (int i = fromIndex; i < num; ++i)
			{
			if (elementAt(i).equals(toMatch))
				return(i);
			}
		return(-1);
		}
	public 		int		lastIndexOf(String toMatch)
		{
		int num = size();
		for (int i = num - 1; i >= 0; --i)
			{
			if (elementAt(i).equals(toMatch))
				return(i);
			}
		return(-1);
		}
	public		boolean		removeElementsInCommon(Strings others)
		{
		return(removeElementsInCommon(others, false));
		}
	public		boolean		removeElementsInCommon(Strings others, boolean ignoreCase)
		{
		boolean changed = false;
		for (int i = 0; i < others.size(); ++i)
			{
			String other = others.get(i);
			for (int j = 0; j < size(); ++j)
				{
				if ((ignoreCase) && (elementAt(j).equalsIgnoreCase(other)))
					{
					remove(j);
					--j;
					changed = true;
					}
				else if ((!ignoreCase) && (elementAt(j).equals(other)))
					{
					remove(j);
					--j;
					changed = true;
					}
				}
			}
		return(changed);
		}
	public		boolean		contains(String str)
		{
		if (indexOf(str) != -1)
			return(true);
		return(false);
		}
	public		boolean		contains(String str, boolean ignoreCase)
		{
		if (ignoreCase)
			{
			if (indexOfIgnoreCase(str) != -1)
				return(true);
			return(false);
			}
		else
			{
			if (indexOf(str) != -1)
				return(true);
			return(false);
			}
		}
	public		boolean		contains(Object obj)
		{
		return(contains(obj == null ? "(null)" : obj.toString()));
		}
	public 		int		indexOfIgnoreCase(String toMatch)
		{
		for (int i = 0; i < size(); ++i)
			{
			if (elementAt(i).equalsIgnoreCase(toMatch))
				return(i);
			}
		return(-1);
		}
	public 		int		startsWithIgnoreCase(String toMatch)
		{
		String toMatchUpperCase = toMatch.toUpperCase();
		for (int i = 0; i < size(); ++i)
			{
			if (elementAt(i).toUpperCase().startsWith(toMatchUpperCase))
				return(i);
			}
		return(-1);
		}
	public 		int		endsWithIgnoreCase(String toMatch)
		{
		String toMatchUpperCase = toMatch.toUpperCase();
		for (int i = 0; i < size(); ++i)
			{
			if (elementAt(i).toUpperCase().endsWith(toMatchUpperCase))
				return(i);
			}
		return(-1);
		}
	public		String[]	toArray()
		{
		String[] array = new String[size()];
		for (int i = 0; i < size(); ++i)
			{
			array[i] = elementAt(i);
			}
		return(array);
		}
	public		void		append(Strings strings)
		{
		for (int i = 0; i < strings.size(); ++i)
			{
			list.add(strings.elementAt(i));
			}
		}
	public		void		append(String[] srcArray)
		{
		for (int i = 0; i < srcArray.length; ++i)
			{
			list.add(srcArray[i]);
			}
		}
	public		int		hashCode()
		{
		return(super.hashCode());
		}
	public		boolean		equals(Object other)
		{
		if (other instanceof Strings)
			{
			Strings otherStrings = (Strings )other;
			if (size() != otherStrings.size())
				return(false);
			for (int i = 0; i < size(); ++i)
				{
				if (!elementAt(i).equals(otherStrings.elementAt(i)))
					return(false);
				}
			return(true);
			}
		return(false);
		}
	public		void		appendLineFeedDelimitedLines(String lines)
		{
		lines = replaceBackslashCodesWithChars(lines);
		StringTokenizer t = new StringTokenizer(lines, "\n", true);
		boolean lastTokenALineFeed = true;
		while (t.hasMoreTokens())
			{
			String token = t.nextToken();
			if (token.charAt(0) == '\n')
				{
				if (lastTokenALineFeed)
					addElement("");
				lastTokenALineFeed = true;
				}
			else
				{
				lastTokenALineFeed = false;
				addElement(token);
				}
			}
		}
	public		String		getLineFeedDelimitedLines()
		{
		StringBuffer string = new StringBuffer(1000);
		for (int i = 0; i < size(); ++i)
			{
			String str = elementAt(i);
			if (str != null)
				{
				string.append(str);
				}
			else
				{
				string.append("(null)");
				}
			if (i != size() - 1)
				{
				string.append('\n');
				}
			}
		return(string.toString());
		}
					// Note does NOT append "" for case of trailing delimiter: one,two,three,
					// Add a " " at end if you want behavior otherwise
	public		void		appendCommaDelimitedStrings(String lines)
		{
		appendDelimitedStrings(lines, ',');
		}
	public		String		toCommaDelimitedString()
		{
		return(getCommaDelimitedStrings());
		}
	public		String		getCommaDelimitedStrings()
		{
		String string = new String();
		for (int i = 0; i < size(); ++i)
			{
			string += elementAt(i);
			if (i != size() - 1)
				string += ",";
			}
		return(string);
		}
					// Note does NOT append "" for case of trailing delimiter: one,two,three,
					// Add a " " at end if you want behavior otherwise
	public		void		appendLineFeedAndDelimitedStrings(String lines, String delimeters, boolean keepQuotedSectionsInOneLine)
		{
		if (!keepQuotedSectionsInOneLine)
			{
			appendDelimitedStrings(lines, delimeters);
			return;
			}

		String line = "";
		char lookingForEndChar = '\0';
		StringTokenizer t = new StringTokenizer(lines, "\n\r\"\'" + delimeters, true);
		while (t.hasMoreTokens())
			{
			String token = t.nextToken().trim();
			if ("\n".equals(token) || ("\r".equals(token)))
				{
				if (lookingForEndChar != '\0')
					{
					line += token;
					}
				else
					{
					addElement(line);
					line = "";
					}
				}
			else if ("\"".equals(token))
				{
				if (lookingForEndChar == '\"')
					{
					lookingForEndChar = '\0';
					line += "\"";
					addElement(line);
					line = "";
					}
				else
					{
					lookingForEndChar = '\"';
					line += "\"";
					}
				}
			else if ("\'".equals(token))
				{
				if (lookingForEndChar == '\'')
					{
					lookingForEndChar = '\0';
					line += "\'";
					addElement(line);
					line = "";
					}
				else
					{
					lookingForEndChar = '\'';
					line += "\'";
					}
				}
			else
				{
				line += token;
				}
			}
		}
					// Note does NOT append "" for case of trailing delimiter: one,two,three,
					// Add a " " at end if you want behavior otherwise
	public		void		appendDelimitedStrings(String lines, String delimeters)
		{
		StringTokenizer t = new StringTokenizer(lines, "\n\r" + delimeters);
		while (t.hasMoreTokens())
			{
			addElement(t.nextToken().trim());
			}
		}
					// Note does NOT append "" for case of trailing delimiter: one,two,three,
					// Add a " " at end if you want behavior otherwise
	public		void		appendDelimitedStrings(String lines, char delimeter)
		{
		StringTokenizer t = new StringTokenizer(lines, "\n\r" + delimeter);
		while (t.hasMoreTokens())
			{
			addElement(t.nextToken().trim());
			}
		}
	/**
	 * processes ",a,," into { null, "a", null, null }
	 **/
	public		void		appendDelimitedStrings2(String lines, String delimeters)
		{
		delimeters = "\n\r" + delimeters;

		StringTokenizer t = new StringTokenizer(lines, delimeters, true);

		boolean lastTokenWasDelimeter = true;
		while (t.hasMoreTokens())
			{
			String token = t.nextToken().trim();
			if ((token.length() == 1) 
				&& (delimeters.indexOf(token.charAt(0)) != -1))
				{
				if (!Character.isWhitespace(token.charAt(0)))
					{
					if ((lastTokenWasDelimeter) || (!t.hasMoreTokens()))
						{
						addElement(null);
						}
					lastTokenWasDelimeter = true;
					}
				else
					{
					lastTokenWasDelimeter = false;
					}
				continue;
				}
			lastTokenWasDelimeter = false;
			addElement(token);
			}
		}
	public		String		getDelimitedStrings(String delimeter)
		{
		String string = new String();
		for (int i = 0; i < size(); ++i)
			{
			string += elementAt(i);
			if (i != size() - 1)
				string += delimeter;
			}
		return(string);
		}
	public		String		toString()
		{
		return(getLineFeedDelimitedLines());
		}
					// This duplicates Utility.replaceBackslashCodesWithChars in 
					// order that we can compile this class w/o need of another class
	public static	String		replaceBackslashCodesWithChars(String line)
		{
		int index;
		if ((index = line.indexOf('\\')) == -1)
			return(line);

		int lineLength = line.length();
		int loc = 0;
		StringBuffer result = new StringBuffer(lineLength);
		do	{
			if (index < lineLength - 1)
				{
				char ch = line.charAt(index + 1);
				if (ch == '\\')
					{
					if (index > loc)
						result.append(line.substring(loc, index));
					result.append("\\");
					loc = index + 2;
					}
				else if (ch == 'n')
					{
					if (index > loc)
						result.append(line.substring(loc, index));
					result.append("\n");
					loc = index + 2;
					}
				else if (ch == 't')
					{
					if (index > loc)
						result.append(line.substring(loc, index));
					result.append("\t");
					loc = index + 2;
					}
				else 
					{
					if (index > loc)
						result.append(line.substring(loc, index));
					result.append(ch);
					loc = index + 2;
					}
				}
			else
				{
				loc += 1;
				}
			} while ((index = line.indexOf('\\', loc)) != -1);

		if (loc < lineLength)
			result.append(line.substring(loc));
		return(result.toString());
		}
	public static	void		sortAlphabetically(String[] list)
		{
		boolean unsorted = true;

		while (unsorted)
			{
			unsorted = false;
			for (int i = 0; i < list.length - 1; ++i)
				{
				String str1 = list[i];
				if (str1.compareTo(list[i + 1]) > 0)
					{
					list[i] = list[i + 1];
					list[i + 1] = str1;
					unsorted = true;
					}
				}
			}
		}
		
	public		void		sortAlphabetically()
		{
		String[] array = toArray();
		sortAlphabetically(array);
		removeAllElements();
		append(array);
		}
		
	public		boolean		isAlphabetical()
		{
		for (int i = 1; i < size(); ++i)
			{
			if (elementAt(i).compareTo(elementAt(i - 1)) < 0)
				return(false);
			}
		return(true);
		}
	public static	void		sortAlphabeticallyIgnoreCase(String[] list)
		{
		boolean unsorted = true;

		while (unsorted)
			{
			unsorted = false;
			for (int i = 0; i < list.length - 1; ++i)
				{
				String str1 = list[i];
				if (str1.compareToIgnoreCase(list[i + 1]) > 0)
					{
					list[i] = list[i + 1];
					list[i + 1] = str1;
					unsorted = true;
					}
				}
			}
		}
	public		void		sortAlphabeticallyIgnoreCase()
		{
		String[] array = toArray();
		sortAlphabeticallyIgnoreCase(array);
		removeAllElements();
		append(array);
		}
		
	public		boolean		isAlphabeticalIgnoreCase()
		{
		for (int i = 1; i < size(); ++i)
			{
			if (elementAt(i).compareToIgnoreCase(elementAt(i - 1)) < 0)
				return(false);
			}
		return(true);
		}
	public		void		reverseListOrder()
		{
		for (int i = 0; i < size()/2; ++i)
			{
			String tmp = elementAt(i);
			setElementAt(elementAt(list.size() - i - 1), i);
			setElementAt(tmp, size() - i - 1);
			}
		}
	public 		void		loadFile(String filename) throws IOException
		{
		loadFile(new FileInputStream(filename));
		}

	public 		void		loadFile(InputStream inputStream) throws IOException
		{
		DataInputStream in = new DataInputStream(inputStream);

		String line;
		while ((line = in.readLine()) != null)
			{
			addElement(line);
			}
		}
	}

