
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
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.DoubleVector; 
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**
 * This is the default sort manager for all table columns. However, to
 * assign a subclass of this sorter to a column, one woudl do the following:
 *
 * int columnNumber;
 * table.getSortManager().setColumnSortMethod(columnNumber, new MiAlphabeticalSortMethod());
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */

public class MiAlphabeticalSortMethod implements MiiTableSortMethod
	{
	private		boolean		ignoreCase;

	public				MiAlphabeticalSortMethod()
		{
		}

	public				MiAlphabeticalSortMethod(boolean ignoreCase)
		{
		this.ignoreCase = ignoreCase;
		}

	public		boolean		sort(Strings values, int[] sortedPositions, int sortMethod)
		{
		if (sortMethod == MiiTypes.Mi_NONE)
			return(false);

		for (int i = 0; i < values.size(); ++i)
			{
			String indexStr = "00000" + i;
			while (indexStr.length() > 6)
				{
				indexStr = indexStr.substring(1);
				}
			values.setElementAt(values.elementAt(i) + "=" + indexStr, i);
			}

		if (ignoreCase)
			{
			if (Utility.isAlphabeticalIgnoreCase(values))
				{
				if (sortMethod == Mi_FORWARD)
					return(false);
				if (sortMethod == Mi_TOGGLE)
					Utility.reverseListOrder(values);
				}
			else
				{
				Utility.sortAlphabeticallyIgnoreCase(values);
				if (sortMethod == Mi_BACKWARD)
					Utility.reverseListOrder(values);
				}
			}
		else
			{
			if (Utility.isAlphabetical(values))
				{
				if (sortMethod == Mi_FORWARD)
					return(false);
				if (sortMethod == Mi_TOGGLE)
					Utility.reverseListOrder(values);
				}
			else
				{
				Utility.sortAlphabetically(values);
				if (sortMethod == Mi_BACKWARD)
					Utility.reverseListOrder(values);
				}
			}
		int numValues = values.size();
		for (int i = 0; i < numValues; ++i)
			{
			String val = values.elementAt(i);
			sortedPositions[i] = Utility.toInteger(val.substring(val.indexOf('=') + 1));
			}
		return(true);
		}
	}


