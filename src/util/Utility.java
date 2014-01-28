
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
import java.util.Hashtable;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.CustomClassLoader;
import com.swfm.mica.util.CustomFileFilter;
import java.io.*;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class Utility
	{
	private	static 	CustomClassLoader	customClassLoader;

	//public static final double ERROR_DELTA_FACTOR = 0;
	public static final double ERROR_DELTA_FACTOR = 0.000001;
	private	static final char[]	hexDidget	
		= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public	static final boolean		isLessThan(double a, double b)
		{
		return((b - a > ERROR_DELTA_FACTOR) ? true : false);
		}
	public	static final boolean		isLessThanOrEqual(double a, double b)
		{
		return((b - a > -ERROR_DELTA_FACTOR) ? true : false);
		}
	public	static final boolean		isGreaterThan(double b, double a)
		{
		return((b - a > ERROR_DELTA_FACTOR) ? true : false);
		}
	public	static final boolean		isGreaterThanOrEqual(double b, double a)
		{
		return((b - a > -ERROR_DELTA_FACTOR) ? true : false);
		}
	public	static final boolean		isZero(double diff)
		{
		return(((diff > ERROR_DELTA_FACTOR) || (diff < -ERROR_DELTA_FACTOR)) ? false : true);
		}
	public	static final boolean		areZero(double diffA, double diffB)
		{
		if ((diffA > ERROR_DELTA_FACTOR) || (diffA < -ERROR_DELTA_FACTOR))
			return(false);
		return(((diffB > ERROR_DELTA_FACTOR) || (diffB < -ERROR_DELTA_FACTOR)) ? false : true);
		}
	public	static final boolean		areZero(double diffA, double diffB, double diffC, double diffD)
		{
		if ((diffA > ERROR_DELTA_FACTOR) || (diffA < -ERROR_DELTA_FACTOR))
			return(false);
		if ((diffB > ERROR_DELTA_FACTOR) || (diffB < -ERROR_DELTA_FACTOR))
			return(false);
		if ((diffC > ERROR_DELTA_FACTOR) || (diffC < -ERROR_DELTA_FACTOR))
			return(false);
		return(((diffD > ERROR_DELTA_FACTOR) || (diffD < -ERROR_DELTA_FACTOR)) ? false : true);
		}
	public	static final boolean		equals(double a, double b)
		{
		return(((a - b > ERROR_DELTA_FACTOR) || (a - b < -ERROR_DELTA_FACTOR)) ? false : true);
		}
	public	static final boolean		areEqual(double a, double b)
		{
		return(((a - b > ERROR_DELTA_FACTOR) || (a - b < -ERROR_DELTA_FACTOR)) ? false : true);
		}
	public	static final boolean		lessThan10PercentDiff(double a, double b)
		{
		double maxDiff = Math.abs(Math.max(a, b) * 0.10);
//System.out.println("maxDiff = " + maxDiff + ", a = " + a + ", b = " + b);
		if ((a - b > maxDiff) || (b - a > maxDiff))
			return(false);
		return(true);
		}

	public static	boolean		isBoolean(String value)
		{
		if (!isEmptyOrNull(value))
			{
			value = value.toLowerCase();
			if ((value.equals("true")) || (value.equals("false")))
				return(true);
			}
		return(false);
		}
	public static	boolean		toBoolean(String value)
		{
		return(Boolean.valueOf(value).booleanValue());
		}
	public static	String		toString(boolean value)
		{
		return(String.valueOf(value));
		}
	public static	String		toString(char value)
		{
		return(String.valueOf(value));
		}
	public static	boolean		isInteger(String value)
		{
		try
			{
			if (value.charAt(0) == '-')
				{
				Integer.parseInt(value.substring(1));
				}
			else
				{
				Integer.parseInt(value);
				}
			}
		catch (Exception e)
			{
			return(false);
			}
		return(true);
		}
	public static	int		toInteger(String value)
		{
		return(toInteger(value, 0));
		}
	public static	int		toInteger(String value, int defaultValue)
		{
		int intVal = defaultValue;
		try
			{
			if (value.charAt(0) == '-')
				{
				intVal = Integer.parseInt(value.substring(1));
				intVal = -intVal;
				}
			else
				{
				intVal = Integer.parseInt(value);
				}
			}
		catch (Exception e)
			{
			}
		return(intVal);
		}
	public static	String		toString(int value)
		{
		return(String.valueOf(value));
		}
	public static	long		toLong(String value)
		{
		return(Long.parseLong(value));
		}
	public static	String		toString(long value)
		{
		return(String.valueOf(value));
		}

	public static	float		toFloat(String value)
		{
		float floatVal = 0;
		try
			{
			if (value.charAt(0) == '-')
				{
				floatVal = Float.valueOf(value.substring(1)).floatValue();
				floatVal = -floatVal;
				}
			else
				{
				floatVal = Double.valueOf(value).floatValue();
				}
			}
		catch (Exception e)
			{
			}
		return(floatVal);
		}
	public static	String		toString(float value)
		{
		return(String.valueOf(value));
		}

	public static	double		toDouble(String value)
		{
		return(toDouble(value, 0.0));
		}
	public static	double		toDouble(String value, double defaultValue)
		{
		double doubleVal = defaultValue;
		try
			{
			if (value.charAt(0) == '-')
				{
				doubleVal = Double.valueOf(value.substring(1)).doubleValue();
				doubleVal = -doubleVal;
				}
			else
				{
				doubleVal = Double.valueOf(value).doubleValue();
				}
			}
		catch (Exception e)
			{
			}
		return(doubleVal);
		}
	public static	boolean		isDouble(String value)
		{
		try
			{
			if (value.charAt(0) == '-')
				{
				Double.valueOf(value.substring(1)).doubleValue();
				}
			else
				{
				Double.valueOf(value).doubleValue();
				}
			}
		catch (Exception e)
			{
			return(false);
			}
		return(true);
		}
	public static	String		toString(double value)
		{
		return(String.valueOf(value));
		}
	public static	String		toShortString(double value)
		{
		return(toShortString(value, 3));
		}
					// JDK 1.0.2 style strings 0.00000127654 => 0
	public static	String		toShortString(double value, int numberOfSignificantDigits)
		{
		if (value == 0.0)
			{
			return("0");
			}
		// Handle rounding
//System.out.println("toShortString: " + value);
		double power = Math.pow(10, numberOfSignificantDigits);
		if (value < 0)
			{
			value = -((double )Math.round(-value * power))/power;
			}
		else
			{
			value = ((double )Math.round(value * power))/power;
			}

		String exponentStr = "";
		String str = String.valueOf(value);
		int exponentIndex = str.indexOf("E");
		if (exponentIndex != -1)
			{
			exponentStr = str.substring(exponentIndex);
			int exponent = toInteger(exponentStr);
			if (exponent < -numberOfSignificantDigits)
				return("0");

			numberOfSignificantDigits = numberOfSignificantDigits - 2;
			str = str.substring(0, exponentIndex - 1);
			}
			
		int index = str.indexOf('.');
		if (index != -1)
			{
			++numberOfSignificantDigits;

			int len = str.length();
			if (index + numberOfSignificantDigits > len)
				index = len - numberOfSignificantDigits;

			str = str.substring(0, index + numberOfSignificantDigits);
		
			len = str.length();
			if ((str.charAt(len - 1) == '0') && (str.charAt(len - 2) == '.'))
				{
				str = str.substring(0, len - 2);
				}
			}
		if (str.equals("-0"))
			{
			return("0");
			}
//System.out.println("EXIT toShortString: " + str + exponentStr);
		return(str + exponentStr);
		}

					// JDK 1.0.2 style strings 0.00000127654 => 1.27654E-6, 300000000005 -> 3E10
	public static	String		toShortSignificantString(double value, int numberOfSignificantDigits)
		{
		if (value == 0.0)
			{
			return("0");
			}

		boolean negative = false;
		if (value < 0)
			{
			negative = true;
			value = -value;
			}
		int exp = 0;
		while (value < 1.0)
			{
			value *= 10.0;
			exp -= 1;
			}

		while (value >= 10.0)
			{
			value /= 10.0;
			exp += 1;
			}

		double power = Math.pow(10, numberOfSignificantDigits);
		value *= power;
		long val = Math.round(value);
		String str = toString(val);
		// Handle case where 9.999999 * power is converted into 10 * power, 
		// otherwise we remove an extra zero decreasing this by 10
		if (str.length() == numberOfSignificantDigits + 2)
			{
			++exp;
			}
		while ((str.length() > 1) && (str.charAt(str.length() - 1) == '0'))
			{
			str = str.substring(0, str.length() - 1);
			}
		if (str.length() > 1)
			{
			if (exp == 2)
				{
				if (str.length() >= 3)
					{
					str = str.substring(0, 3) + (str.length() == 3 ? "" : "." + str.substring(3));
					}
				else // if (str.length() == 2)
					{
					str = str + "0";
					}
				}
			else if (exp == 1)
				{
				str = str.substring(0, 2) + (str.length() == 2 ? "" : "." + str.substring(2));
				}
			else if (exp == -1)
				{
				str = "0." + str.substring(0);
				}
			else if (exp == 0)
				{
				str = str.substring(0, 1) + "." + str.substring(1);
				}
			else 
				{
				str = str.substring(0, 1) + "." + str.substring(1) + "e" + toString(exp);
				}
			}
		else
			{
			if (exp == 1)
				{
				str += "0";
				}
			else if (exp == 2)
				{
				str += "00";
				}
			else if (exp == -1)
				{
				str = "0." + str;
				}
			else if (exp == 0)
				{
				// str = str;
				}
			else 
				{
				str += "e" + toString(exp);
				}
			}
		if (negative)
			{
			str = "-" + str;
			}
		return(str);
		}


	public static	String		toCommaFormattedString(long value)
		{
		String str = new String();
		String digits;
		boolean hasDigits = false;
		if (value > 1000000000000000L)
			{
			str += String.valueOf(value/1000000000000000L);
			str += ",";
			hasDigits = true;
			}
		if (value > 1000000000000L)
			{
			digits = String.valueOf(value/1000000000000L);
			while ((hasDigits) && (digits.length() < 3))
				digits = "0" + digits;
			hasDigits = true;
			}
		if (value > 1000000000)
			{
			digits = String.valueOf(value/1000000000);
			while ((hasDigits) && (digits.length() < 3))
				digits = "0" + digits;
			str += digits + ",";
			hasDigits = true;
			}
		if (value > 1000000)
			{
			digits = String.valueOf((value % 1000000000)/1000000);
			while ((hasDigits) && (digits.length() < 3))
				digits = "0" + digits;
			str += digits + ",";
			hasDigits = true;
			}
		if (value > 1000)
			{
			digits = String.valueOf((value % 1000000)/1000);
			while ((hasDigits) && (digits.length() < 3))
				digits = "0" + digits;
			str += digits + ",";
			hasDigits = true;
			}
		digits = String.valueOf(value % 1000);
		while ((hasDigits) && (digits.length() < 3))
			digits = "0" + digits;
		str += digits;

		return(str);
		}

	public static	String		toCommaFormattedString(int value)
		{
		String str = new String();
		String digits;
		boolean hasDigits = false;
		if (value > 1000000000)
			{
			str += String.valueOf(value/1000000000);
			str += ",";
			hasDigits = true;
			}
		if (value > 1000000)
			{
			digits = String.valueOf((value % 1000000000)/1000000);
			while (hasDigits && (digits.length() < 3))
				digits = "0" + digits;
			str += digits + ",";
			hasDigits = true;
			}
		if (value > 1000)
			{
			digits = String.valueOf((value % 1000000)/1000);
			while ((hasDigits) && (digits.length() < 3))
				digits = "0" + digits;
			str += digits + ",";
			hasDigits = true;
			}
		digits = String.valueOf(value % 1000);
		while ((hasDigits) && (digits.length() < 3))
			digits = "0" + digits;
		str += digits;
		return(str);
		}

	public static	int		indexOfIgnoreCase(String str, String pattern)
		{
		String uStr = str.toUpperCase();
		String uPattern = pattern.toUpperCase();
		return(uStr.indexOf(uPattern));
		}
	public static	boolean		startsWithIgnoreCase(String str, String pattern)
		{
		String uStr = str.toUpperCase();
		String uPattern = pattern.toUpperCase();
		return(uStr.startsWith(uPattern));
		}
	public static	boolean		endsWithIgnoreCase(String str, String pattern)
		{
		String uStr = str.toUpperCase();
		String uPattern = pattern.toUpperCase();
		return(uStr.endsWith(uPattern));
		}
	public static	int		numOccurancesOf(String str, char ch)
		{
		return(numOccurancesOf(str, ch, 0, str.length()));
		}
	public static	int		numOccurancesOf(String str, char ch, int fromIndex, int endIndex)
		{
		int index;
		int num = 0;
		while ((index = str.indexOf(ch, fromIndex)) != -1)
			{
			fromIndex = index + 1;
			if (index >= endIndex)
				return(num);
			++num;
			}
		return(num);
		}

	public static	int		numOccurancesOf(String str, String pattern)
		{
		return(numOccurancesOf(str, pattern, 0, str.length()));
		}
	public static	int		numOccurancesOf(String str, String pattern, int fromIndex, int endIndex)
		{
		int index;
		int num = 0;
		while ((index = str.indexOf(pattern, fromIndex)) != -1)
			{
			fromIndex = index + 1;
			if (index >= endIndex)
				return(num);
			++num;
			}
		return(num);
		}

	public static	int		indexOfNthOccuranceOf(String str, char pattern, int nth)
		{
		return(indexOfNthOccuranceOf(str, pattern, 0, str.length(), nth));
		}
	public static	int		indexOfNthOccuranceOf(
						String str, char pattern, int fromIndex, int endIndex, int nth)
		{
		int index;
		int num = 0;
		while ((index = str.indexOf(pattern, fromIndex)) != -1)
			{
			fromIndex = index + 1;
			if (index >= endIndex)
				return(-1);
			++num;
			if (num == nth)
				return(index);
			}
		return(-1);
		}

	public static	String		sprintf(String format, String value1)
		{
		return(sprintf(format, value1, 1));
		}
	private static	String		_sprintf(String format, String value1)
		{
		int index = 0;
		String result = format;
		while (((index = result.indexOf("%s", index)) != -1)
			&& (((index == 0) || (result.charAt(index - 1) != '%'))))
			{
			result = result.substring(0, index) + value1 + result.substring(index + 2);
			}
		return(result);
		}
	// To support i18n format strings of form: "File open failed for: %1 using writemode: %2"
	// While ignoreing %%1, etc. If %argNum is not found, then %s is looked for by default
	public static	String		sprintf(String format, String value1, int argNum)
		{
		boolean foundMatch = false;
		int index = 0;
		String result = format;
		while (((index = result.indexOf("%" + argNum, index)) != -1)
			&& (((index == 0) || (result.charAt(index - 1) != '%'))))
			{
			result = result.substring(0, index) + value1 + result.substring(index + 2);
			foundMatch = true;
			}
		if (foundMatch)
			{
			return(result);
			}
		return(_sprintf(format, value1));
		}
	public static	String		sprintf(String format, String value1, String value2)
		{
		String result = sprintf(format, value1, 1);
		result = sprintf(result, value2, 2);
		return(result);
		}
	public static	String		sprintf(String format, String value1, String value2, String value3)
		{
		String result = sprintf(format, value1, 1);
		result = sprintf(result, value2, 2);
		result = sprintf(result, value3, 3);
		return(result);
		}
	public static	String		sprintf(String format, String value1, String value2, String value3, String value4)
		{
		String result = sprintf(format, value1, 1);
		result = sprintf(result, value2, 2);
		result = sprintf(result, value3, 3);
		result = sprintf(result, value4, 4);
		return(result);
		}
	public	static 		void		sortAlphabetically(String[] list)
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
		
	public	static 		void		sortAlphabetically(Strings list)
		{
		String[] array = list.toArray();
		sortAlphabetically(array);
		list.removeAllElements();
		list.append(array);
		}
		
	public	static 		boolean		isAlphabetical(Strings list)
		{
		for (int i = 1; i < list.size(); ++i)
			{
			if (list.elementAt(i).compareTo(list.elementAt(i - 1)) < 0)
				return(false);
			}
		return(true);
		}
	public	static 		void		sortAlphabeticallyIgnoreCase(String[] list)
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
		
	public	static 		void		sortAlphabeticallyIgnoreCase(Strings list)
		{
		String[] array = list.toArray();
		sortAlphabeticallyIgnoreCase(array);
		list.removeAllElements();
		list.append(array);
		}
		
	public	static 		boolean		isAlphabeticalIgnoreCase(Strings list)
		{
		for (int i = 1; i < list.size(); ++i)
			{
			if (list.elementAt(i).compareToIgnoreCase(list.elementAt(i - 1)) < 0)
				return(false);
			}
		return(true);
		}
	public	static 		void		reverseListOrder(Strings list)
		{
		for (int i = 0; i < list.size()/2; ++i)
			{
			String tmp = list.elementAt(i);
			list.setElementAt(list.elementAt(list.size() - i - 1), i);
			list.setElementAt(tmp, list.size() - i - 1);
			}
		}
	public	static 		void		sort(IntVector list)
		{
		boolean unsorted = true;

		int[] array = list.toArray();
		while (unsorted)
			{
			unsorted = false;
			for (int i = 0; i < array.length - 1; ++i)
				{
				if (array[i] > array[i + 1])
					{
					int value = array[i];
					array[i] = array[i + 1];
					array[i + 1] = value;
					unsorted = true;
					}
				}
			}
		list.removeAllElements();
		list.append(array);
		}
		
	public	static 		void		sort(LongVector list)
		{
		boolean unsorted = true;

		long[] array = list.toArray();
		while (unsorted)
			{
			unsorted = false;
			for (int i = 0; i < array.length - 1; ++i)
				{
				if (array[i] > array[i + 1])
					{
					long value = array[i];
					array[i] = array[i + 1];
					array[i + 1] = value;
					unsorted = true;
					}
				}
			}
		list.removeAllElements();
		list.append(array);
		}
		

	public static	boolean		hasCommandLineArgument(String[] args, String option)
		{
		for (int i = 0; i < args.length; ++i)
			{
			if (args[i].equals(option)) 
				{
				return(true);
				}
			}
		return(false);
		}
	public static	String		getCommandLineArgument(String[] args, String option)
		{
		for (int i = 0; i < args.length; ++i)
			{
			if (args[i].equals(option)) 
				{
				if (i < args.length - 1)
					return(args[i + 1].trim());
				return(null);
				}
			}
		return(null);
		}
	public	static void		parseCommandLine(String args[])
		{
		int numUnnamedParms = 0;
		for (int i = 0; i < args.length; ++i)
			{
			if (args[i].startsWith("-"))
				{
				String val;
				String var = args[i].substring(0, 2);
				if (i + 1 < args.length)
					val = args[i + 1].trim();
				else
					throw new IllegalArgumentException("No value found for switch: " + var);
				System.getProperties().put(var, val);
				++i;
				}
			else if (args[i].startsWith("-D"))
				{
				String val;
				String str = args[i].substring(2);
				StringTokenizer t = new StringTokenizer(str, "=");
				String var = t.nextToken().trim();
				if (t.hasMoreTokens())
					{
					val = t.nextToken().trim();
					System.getProperties().put(var, val);
					}
				else
					{
					System.getProperties().put(var, "");
					}
				}
			else 
				{
				System.getProperties().put(String.valueOf(numUnnamedParms++), args[i]);
				}
			}
		}
	public	static String		getCommandLineArgument(String name)
		{
		return(System.getProperty(name));
		}
	public	static String		getCommandLineArgument(int name)
		{
		return(System.getProperty(String.valueOf(name)));
		}
	public static	String		read1_0_2CompatableLine(DataInputStream file)
		{
		String line;
		try
			{
			line = file.readLine();
			}
		catch (IOException e)
			{
			return(null);
			}
		return(line);
		}
	public static	String		readLine(BufferedReader file)
		{
		String line;
		try
			{
			line = file.readLine();
			}
		catch (IOException e)
			{
			return(null);
			}
		return(line);
		}
					/**------------------------------------------------------
	 				 * Opens the given file for output.
					 * @param filename	the name of the file to open
					 *------------------------------------------------------*/
	public static	PrintWriter	openOutputFile(String filename)
		{
		FileOutputStream fout;
		try
			{
			fout =  new FileOutputStream(filename);
			}
		catch (IOException e)
			{
			System.out.println("Unable to open file: " + filename);
			return(null);
			}
		return(new PrintWriter(fout));
		}
	public static	PrintStream	open1_0_2CompatableOutputFile(String filename)
		{
		FileOutputStream fout;
		try
			{
			fout =  new FileOutputStream(filename);
			}
		catch (IOException e)
			{
			System.out.println("Unable to open file: " + filename);
			return(null);
			}
		return(new PrintStream(fout));
		}
	public static	DataInputStream	open1_0_2CompatableInputFile(String filename)
		{
		FileInputStream file;
		try
			{
			file =  new FileInputStream(filename);
			}
		catch (IOException e)
			{
			System.out.println("Unable to open file: " + filename);
			return(null);
			}
		return(new DataInputStream(file));
		}
	public static	BufferedReader	openInputFile(String filename)
		{
		InputStreamReader file;
		try
			{
			file =  new InputStreamReader(new FileInputStream(filename));
			}
		catch (IOException e)
			{
			System.out.println("Unable to open file: " + filename);
			return(null);
			}
		return(new BufferedReader(file));
		}
	public static	boolean		copyFile(String source, String destination)
		{
		FileInputStream src = null;
		FileOutputStream dest = null;
		long length = 0;
		try
			{
			src = new FileInputStream(source);
			File s = new File(source);
			length = s.length();
			}
		catch (IOException e)
			{
			System.out.println("Unable to open input file: " + source);
			return(false);
			}
		try
			{
			dest = new FileOutputStream(destination);
			}
		catch (IOException e)
			{
			System.out.println("Unable to open output file: " + destination);
			return(false);
			}
		try	{
			byte buffer[] = new byte[(int )length];
			src.read(buffer);
			dest.write(buffer);
			src.close();
			dest.close();
			}
		catch (IOException e)
			{
			System.out.println("Unable to copy file: " + source + " to " + destination);
			return(false);
			}
		return(true);
		}


	/** 
	 * This is in base 16, not the standard base 64, suitable for use with XML
	 * @see uudecode
	 **/
	public static	String		uuencode(byte[] byteArray)
		{
		char[] charArray = new char[2 * byteArray.length];
		for (int i = 0; i < byteArray.length; ++i)
			{
			charArray[2 * i] = hexDidget[(byteArray[i] >> 4) & 0xf];
			charArray[2 * i + 1] = hexDidget[byteArray[i] & 0xf];
			}
		return(new String(charArray));
		}
	static int[] hexCharToValueArray =
		{ 
		0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 	// 0 - 9
		0, 0, 0, 0, 0, 0, 0, 			// :, ;, <, =, >, ?, @, 
		10, 11, 12, 13, 14, 15,			// A - F
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // G - Z
		0, 0, 0, 0, 0, 0, 0,			// [, \, ], ^, _, ` 
		10, 11, 12, 13, 14, 15			// a - f
		};
		
		
	/** 
	 * This is in base 16, not the standard base 64, suitable for use with XML
	 * @see uuencode
	 **/
	public static	byte[]		uudecode(String charArray)
		{
		byte[] byteArray = new byte[charArray.length()/2];
		for (int i = 0; i < byteArray.length; ++i)
			{
			byteArray[i] = (byte )(hexCharToValueArray[charArray.charAt(2 * i) - '0'] * 16
				+ hexCharToValueArray[charArray.charAt(2 * i + 1) - '0']);
			}
		return(byteArray);
		}



	// name=value, name = value, name = value with spaces, name = value
	public static	void		parsePropertyString(Hashtable hashtable, String nameValuePairs)
		{
		StringTokenizer t = new StringTokenizer(nameValuePairs, "=,");
		while (t.hasMoreTokens())
			{
			String name = t.nextToken().trim();
			String value;
			if (t.hasMoreTokens())
				{
				value = t.nextToken().trim();
				hashtable.put(name, value);
				}
			else
				{
				hashtable.put(name, "");
				//throw new IllegalArgumentException("Utility.parsePropertyString - string: " + nameValuePairs + ", is missing value for: " + name);
				}
			}
		}
	public static 	int		Max3(int a1, int a2, int a3)
		{
		if (a1 > a2)
			{
			return ((a1 > a3) ? a1 : a3);
			}
		return((a2 > a3) ? a2 : a3);
		}
	public static	String		getIndentation(int length)
		{
		String str = "";
		while (length > 10)
			{
			str += "          ";
			length -= 10;
			}
		while (length-- > 0)
			{
			str += " ";
			}
		return(str);
		}
	public static	String		firstCharToUpperCase(String str)
		{
		if (str.length() > 0)
			{
			str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
			}
		return(str);
		}
	public static	String		replaceAllTabsWithSpaces(String line, int numSpacesPerTab)
		{
		if (line.indexOf('\t') == -1)
			return(line);

		int lineLength = line.length();
		StringBuffer result = new StringBuffer(lineLength);
		int loc = 0;
		int index;
		while ((index = line.indexOf('\t', loc)) != -1)
			{
			if (index > loc)
				result.append(line.substring(loc, index));
			loc = index + 1;
			do	{
				result.append(' ');
				++index;
				} while (index % 8 != 0);
			}
		if (loc < lineLength)
			result.append(line.substring(loc));
		return(result.toString());
		}
	public static	String		replaceAll(String line, String pattern, String replacement)
		{
		if (line.indexOf(pattern.charAt(0)) == -1)
			return(line);

		int index;
		if ((index = line.indexOf(pattern)) == -1)
			return(line);

		boolean faster = (pattern.indexOf(replacement) == -1) && ((replacement.length() != 0) || (pattern.length() == 1));
//System.out.println("\n\nline: \"" + line +"\"");
//System.out.println("repalcing: \"" + pattern +" \"");
//System.out.println("with: \"" + replacement + "\"");
//System.out.println("faster: " + faster);
		int patternLength = pattern.length();
		int replacementLength = replacement.length();
		do	{
			int lineLength = line.length();
			StringBuffer result = new StringBuffer(lineLength);
			int loc = 0;
			do	{
				if (index > loc)
					result.append(line.substring(loc, index));
				if (replacementLength > 0)
					result.append(replacement);
				loc = index + patternLength;
				} while ((index = line.indexOf(pattern, loc)) != -1);
				
			if (loc < lineLength)
				result.append(line.substring(loc));

			line = result.toString();
			} while ((!faster) && ((index = line.indexOf(pattern)) != -1));

//System.out.println("returning line: \"" + line +" \"");
		return(line);
		}
	public static	int		indexOfIthWord(String string, int ith)
		{
		StringTokenizer t = new StringTokenizer(string, " \t\n\r\"", true);
		int index = -1;
		boolean inQuotes = false;
		boolean inWord = false;
//System.out.println("string = " + string);
		for (int i = 0; i < string.length(); ++i)
			{
			char ch = string.charAt(i);
			if ((ch == ' ') || (ch == '\t') || (ch == '\n') || (ch == '\r'))
				{
				if (!inQuotes)
					{
					if (inWord)
						{
						inWord = false;
						}
					}
				}
			else if (ch == '\"')
				{
				inQuotes = !inQuotes;
				}
			else 
				{
				if (!inWord)
					{
					if (ith == 0)
						{
						return(i);
						}
					inWord = true;
					--ith;
					}
				}
			}
		return(-1);
		}
	public static	String		getIthWord(String string, int ith)
		{
		StringTokenizer t = new StringTokenizer(string, " \t\n\r\"", true);
		String word = null;
		boolean inQuotes = false;
//System.out.println("string = " + string);
		while (t.hasMoreTokens())
			{
			String token = t.nextToken();
//System.out.println("token = \"" + token + "\"");
//System.out.println("word = \"" + word + "\"");
//System.out.println("inQuotes = " + inQuotes);
//System.out.println("ith = " + ith);
			char ch = token.charAt(0);
			if ((inQuotes) && (ith == 0))
				{
				if (ch == '\"')
					{
					return(word);
					}
				word += token;
				}
			else if ((ch == ' ') || (ch == '\t') || (ch == '\n') || (ch == '\r'))
				{
				;
				}
			else if (ch == '\"')
				{
				inQuotes = !inQuotes;
				if (ith == 0)
					{
					if (!inQuotes)
						{
						return(word);
						}
					if (word == null)
						{
						word = "";
						}
					}
				else if (!inQuotes)
					{
					--ith;
					}
				}
			else
				{
				if (ith == 0)
					{
					word = token;
					return(word);
					}
				if ((!inQuotes) && (ch != '\"'))
					{
					--ith;
					}
				}
			}
		return(word);
		}
	public static	boolean		isEmptyOrNull(String string)
		{
		return((string == null) || (string.length() == 0));
		}
	public static	boolean		isEqualTo(String one, String other)
		{
		if (one == null)
			{
			return(other == null);
			}
		return((other != null) && (one.equals(other)));
		}
	public static	boolean		equals(String one, String other)
		{
		if (one == null)
			{
			return(other == null);
			}
		return((other != null) && (one.equals(other)));
		}
	public static	boolean		equalsIgnoreCaseAndSpaces(String one, String other)
		{
		if (one == null)
			return(other == null);
		if (other == null)
			return(false);
		one = Utility.replaceAll(one, " ", "");
		other = Utility.replaceAll(other, " ", "");
		return(one.equalsIgnoreCase(other));
		}
	public static	String		replaceBackslashCharsWithChars(String string)
		{
		int index;
		while ((index = string.indexOf("\\")) != -1)
			{
			if (index < string.length() - 1)
				{
				if (string.charAt(index + 1) == '\\')
					string = string.substring(0, index) + "\\" + string.substring(index + 2);
				else
					string = string.substring(0, index) + string.substring(index + 1);
				}
			}
		return(string);
		}
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
	public static 	String		toHexString(byte[] values)
		{
		StringBuffer buf = new StringBuffer(values.length * 2);
		buf.setLength(values.length * 2);
		int index = 0;
		int ptr = 0;
		while (index < values.length)
			{
			buf.setCharAt(ptr++, hexDidget[(values[index] & 0xf0) >> 4]);
			buf.setCharAt(ptr++, hexDidget[values[index] & 0x0f]);
			++index;
			}
		return(buf.toString());
		}
	public static 	String		toHexString(int val)
		{
		StringBuffer buf = new StringBuffer(10);
		buf.setLength(9);
		int index = 8;
		while (index >= 0)
			{
			buf.setCharAt(index--, hexDidget[val & 0x0f]);
			val >>= 4;
			buf.setCharAt(index--, hexDidget[val & 0x0f]);
			val >>= 4;
			buf.setCharAt(index--, hexDidget[val & 0x0f]);
			val >>= 4;
			buf.setCharAt(index--, hexDidget[val & 0x0f]);
			val >>= 4;
			if (index > 0)
				buf.setCharAt(index--, ' ');
			}
		return(buf.toString());
		}
	public static	double		getAngle(double dy, double dx)
		{
		if (dy == 0)
			{
			if (dx < 0)
				return(Math.PI);
			else
				return(0);
			}
		else if (dx == 0)
			{
			if (dy < 0)
				return(3 * Math.PI/2);
			else
				return(Math.PI/2);
			}
		double angle = Math.atan(dy/dx);
		if (dx < 0)
			angle = angle + Math.PI;

		if (angle < 0)
			angle += 2*Math.PI;
		return(angle);
		}
	public static	int		round(double value)
		{
		if (value > 0)
			return((int )(value + 0.5));
		return((int )(value - 0.5));
		}

	public static	int		indexOfStringInArray(
						String[] array, String value, boolean caseSensitive)
		{
		for (int i = 0; i < array.length; ++i)
			{
			if (value == null)
				{
				if (array[i] == null)
					return(i);
				}
			else if (array[i] != null)
				{
				if (caseSensitive)
					{
					if (array[i].equals(value))
						return(i);
					}
				else
					{
					if (array[i].equalsIgnoreCase(value))
						return(i);
					}
				}
			}
		return(-1);
		}
	public static	String		dump2DArray(int[] array, int width, int height)
		{
		java.util.Hashtable table = new java.util.Hashtable();
		StringBuffer strBuf = new StringBuffer(1000);
		int ch = 'a';
		for (int i = 0; i < height; ++i)
			{
			for (int j = 0; j < width; ++j)
				{
				String name;
				int cVal = array[i * width + j];
				if (cVal == 0)
					name = ".";
				else
					name = (String )table.get("" + cVal);
				if (name == null)
					{
					name = "" + ((char )ch);
					table.put(cVal + "", name);
					ch++;
					}
				strBuf.append(name);
				}
			strBuf.append("\n");
			}
		return(strBuf.toString());
		}
	public static	String		dump2DArray(int[] array, int width, int height, int numDigits)
		{
		StringBuffer strBuf = new StringBuffer(1000);
		for (int i = 0; i < height; ++i)
			{
			for (int j = 0; j < width; ++j)
				{
				int cVal = array[i * width + j];
				String name = "" + cVal;
				int len = name.length();
				if (len > numDigits)
					{
					name = name.substring(len - numDigits);
					}
				while (len < numDigits)
					{
					strBuf.append("0");
					++len;
					}
				strBuf.append(name);
				strBuf.append(",");
				}
			strBuf.append("\n");
			}
		return(strBuf.toString());
		}
	/*sm-----------------------------------------------------------*/
	/** Return true if given String contains any wild card characters
	 *  such as '*'.
	 */
	/*em-----------------------------------------------------------*/
	public static		boolean		hasWildCards(String name)
		{
		if (name.indexOf('*') != -1)
			return(true);
		return(false);
		}
	public static		boolean		matchesWildCards(String name, Strings searchSpecs)
		{
		for (int i = 0; i < searchSpecs.size(); ++i)
			{
			if (matchesWildCards(name, searchSpecs.get(i)))
				{
				return(true);
				}
			}
		return(false);
		}
	/*sm-----------------------------------------------------------*/
	/** Return true if given name matches the given search 
	 *  specification String.
	 */
	/*em-----------------------------------------------------------*/
	public static		boolean		matchesWildCards(String name, String searchSpec)
		{
		String wild;
		
		int wildIndex = searchSpec.indexOf('*');
		if (wildIndex == -1)
			{
			return(name.equals(searchSpec));
			}
		wild = searchSpec.substring(0, wildIndex);
		if (wildIndex != 0)
			{
			if (!name.startsWith(wild))
				return(false);
			name = name.substring(wild.length());
			}

		searchSpec = searchSpec.substring(wildIndex + 1);
		while (true)
			{
			if (searchSpec.equals("*"))
				return(true);

			wildIndex = searchSpec.indexOf('*');
			if (wildIndex == 0)
				{
				searchSpec = searchSpec.substring(1);
				}
			else if (wildIndex == -1)
				{
				if ((searchSpec.length() == 0) || (name.endsWith(searchSpec)))
					return(true);
				return(false);
				}
			else
				{
				wild = searchSpec.substring(0, wildIndex);
				searchSpec = searchSpec.substring(wildIndex);
				int wildLength = wild.length();
				boolean matchFound = false;
				for (int i = 0; ((i <= name.length() - wildLength) && (!matchFound)); ++i)
					{
					if (name.regionMatches(i, wild, 0, wildLength))
						{
						name = name.substring(i + wildLength);
						matchFound = true;
						}
					}
				if (!matchFound)
					return(false);
				}
			}
		}
	public static	Object		makeInstanceOfClass(String className)
		{
/*** This is not needed anymore?
		if (customClassLoader == null)
			customClassLoader = new CustomClassLoader();

		Class theClass = customClassLoader.loadClass(className, null);
***/
		Class theClass = getClass(className);
		if (theClass != null)
			{
			return(makeInstanceOfClass(theClass));
			}
		return(null);
		}
	public static	Object		makeInstanceOfClass(Class theClass)
		{
		try	{
			Object obj = theClass.newInstance();
			return(obj);
			}
		catch (java.lang.NoSuchMethodError e)
			{
			System.out.println(
				"Could not make instance (no public default constructor) for class: "
				 + theClass.getName());
			e.printStackTrace();
			}
		catch (java.lang.IllegalAccessException e)
			{
			System.out.println(
				"Could not make instance (no public default constructor or class not public) for class: "
				 + theClass.getName());
			e.printStackTrace();
			}
		catch (java.lang.InstantiationException e)
			{
			System.out.println(
				"Could not make instance (no public default constructor) for class: "
				 + theClass.getName());
			e.printStackTrace();
			}
		catch (Exception e)
			{
			System.out.println(
				"Could not make instance for class: "
				 + theClass.getName());
			e.printStackTrace();
			}
		return(null);
		}
	public static	Class		getClass(String className)
		{
/*** This is not needed anymore?
		if (customClassLoader == null)
			customClassLoader = new CustomClassLoader();

		return(customClassLoader.loadClass(className, null));
****/
		try	{
			return(Class.forName(className));
			}
		catch (java.lang.ClassNotFoundException e)
			{
			System.out.println(
				"Could not find class: " + className);
			e.printStackTrace();
			}
		return(null);
		}

					// Order:  baseClass ... leafClass, "Object" is ommited, only names are
					// returned, not the "com.swfm.mica", or "java.awt" qualifications.
	public static	Strings		getClassHierarchy(Class aClass)
		{
		Strings strings = new Strings();

		String className = aClass.getName();
		if (className.equals("java.lang.Object"))
			return(strings);

		int index = className.lastIndexOf('.');
		if (index != -1)
			className = className.substring(index + 1);
		strings.addElement(className);

		while ((aClass = aClass.getSuperclass()) != null)
			{
			className = aClass.getName();
			if (className.equals("java.lang.Object"))
				return(strings);
			index = className.lastIndexOf('.');
			if (index != -1)
				className = className.substring(index + 1);
			strings.insertElementAt(className, 0);
			}

		return(strings);
		}
					// Order:  baseClass ... leafClass, "Object" is ommited, full class names are
					// returned, i.e. the full "com.swfm.mica" qualifications.
	public static	Strings		getFullyQualifiedClassHierarchy(Class aClass)
		{
		Strings strings = new Strings();

		String className = aClass.getName();
		if (className.equals("java.lang.Object"))
			return(strings);

		strings.addElement(className);

		while ((aClass = aClass.getSuperclass()) != null)
			{
			className = aClass.getName();
			if (className.equals("java.lang.Object"))
				return(strings);
			strings.insertElementAt(className, 0);
			}

		return(strings);
		}
	public static	boolean		instanceOf(Class aClass, String className)
		{
		if (className.equals("Object"))
			return(true);

		return(_instanceOf(aClass, className));
		}

	public static	boolean		_instanceOf(Class aClass, String className)
		{
		if (aClass.getName().equals(className))
			return(true);

		Class superClass = aClass.getSuperclass();
		if (superClass != null)
			{
			String superName = superClass.getName();
			if ((!superName.equals("Object")) && (_instanceOf(superClass, className)))
				return(true);
			}
		Class[] interfaces = aClass.getInterfaces();
		for (int i = 0; i < interfaces.length; ++i)
			{
			Class interfaceClass = interfaces[i];
			String interfaceName = interfaceClass.getName();
			if ((!interfaceName.equals("Object")) && (_instanceOf(interfaceClass, className)))
				return(true);
			}
		return(false);
		}

					/**------------------------------------------------------
	 				 * Gets the name of the file from its pathname. For example
					 * returns "file.txt.tmp" when given "c:\stuff\file.txt.tmp".
					 * 
					 * @param filename 	the pathname of a file
					 *------------------------------------------------------*/
	public static	String		extractFilenameFromFilenamePath(String filename)
		{
		int index = filename.lastIndexOf('/');
		if (index != -1)
			{
			filename = filename.substring(index + 1);
			}
		index = filename.lastIndexOf('\\');
		if (index != -1)
			{
			filename = filename.substring(index + 1);
			}

		return(filename);
		}

					/**------------------------------------------------------
	 				 * Gets the path of the file from its pathname. For example
					 * returns "c:\stuff" when given "c:\stuff\file.txt".
					 * 
					 * @param filename 	the pathname of a file including last slash
					 *------------------------------------------------------*/
	public static	String		extractPathFromFilenamePath(String filename)
		{
		int lastForwardSlashIndex = filename.lastIndexOf('/');
		int lastBackwardsSlashIndex = filename.lastIndexOf('\\');
		if (lastForwardSlashIndex > lastBackwardsSlashIndex)
			{
			filename = filename.substring(0, lastForwardSlashIndex + 1);
			}
		else if (lastBackwardsSlashIndex > lastForwardSlashIndex)
			{
			filename = filename.substring(0, lastBackwardsSlashIndex + 1);
			}

		return(filename);
		}


	public static	String		portFileNameToCurrentPlatform(String filename)
		{
		// Need knowledge of all possible separators to do this correctly...
		if (File.separatorChar == '/')
			return(filename.replace('\\', '/'));
		else if (File.separatorChar == '\\')
			return(filename.replace('/', '\\'));
		return(filename);
		}
	public static	Strings		getFileNames(String directoryName, String namesToLookFor)
		{
		return(getDirectoryContents(directoryName, namesToLookFor, false, true));
		}
	public static	Strings		getDirectoryNames(String directoryName, String namesToLookFor)
		{
		return(getDirectoryContents(directoryName, namesToLookFor, true, false));
		}
	public static	Strings		getDirectoryContents(String directoryName, String namesToLookFor)
		{
		return(getDirectoryContents(directoryName, namesToLookFor, true, true));
		}

	public static	Strings		getDirectoryContents(String directoryName, String namesToLookFor,
						boolean acceptDirectories, boolean acceptFiles)
		{
		return(getDirectoryContents(
			directoryName, 
			namesToLookFor == null ? null : new Strings(namesToLookFor), 
			acceptDirectories, acceptFiles));
		}
	public static	Strings		getDirectoryContents(String directoryName, Strings namesToLookFor,
						boolean acceptDirectories, boolean acceptFiles)
		{
		if (!directoryName.endsWith(File.separator))
			directoryName += File.separator;
		File directory = new File(directoryName);
		if (!directory.exists())
			return(new Strings());
		String[] names = directory.list(
			new CustomFileFilter(namesToLookFor, acceptDirectories, acceptFiles));
		return(new Strings(names));
		}
	}

