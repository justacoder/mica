
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


import java.util.ArrayList;
import com.swfm.mica.util.IntVector;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**----------------------------------------------------------------------------------------------
 * This class supports the 'virtual' table, which is called virtual by virtue of it building
 * only the visible rows of the table at one time - instead of the whole table at once - which
 * scales better and is more efficient, though less flexible.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiVirtualTableRowsFilter implements MiiVirtualTableFilter
	{
	public final static int		EQUALS = 0;
	public final static int		STARTS_WITH = 1;
	public final static int		CONTAINS = 2;

	private		boolean		ignoreCase;
	private		int		columnNumberOfValues;
	private		Strings		valuesToExclude = new Strings();
	private		Strings		valuesToInclude = new Strings();
	private		IntVector	compareMethods = new IntVector();

	
	public				MiVirtualTableRowsFilter(int columnNumberOfValuesToFilter)
		{
		this.columnNumberOfValues = columnNumberOfValuesToFilter;
		}
	public		void		addIncludeRowsWithThisValue(String value, int compareMethod)
		{
		valuesToInclude.add(value);
		compareMethods.addElement(compareMethod);
		}
	public		void		removeAllIncludeRowsWithThisValue()
		{
		valuesToInclude.clear();
		compareMethods.removeAllElements();
		}
	public		void		addExcludeRowsWithThisValue(String value)
		{
		valuesToExclude.add(value);
		valuesToExclude.removeDuplicates();
		}
	public		void		removeExcludeRowsWithThisValue(String value)
		{
		valuesToExclude.removeElement(value);
		}
	public		void		setIgnoreCase(boolean flag)
		{
		ignoreCase = flag;
		}
	public		boolean		filter(ArrayList rows)
		{
//MiDebug.println(this + "valuesToInclude=" + valuesToInclude);
		if ((valuesToExclude.size() == 0) && (valuesToInclude.size() == 0))
			{
			return(false);
			}

//MiDebug.println("valuesToExclude=" + valuesToExclude);
//MiDebug.println("valuesToInclude=" + valuesToInclude);
//MiDebug.println("columnNumberOfValues=" + columnNumberOfValues);
		boolean changed = false;
		for (int i = 0; i < rows.size(); ++i)
			{
//MiDebug.println("((MiTableCells )rows.get(i)).elementAt(columnNumberOfValues).getValue()=" + ((MiTableCells )rows.get(i)).elementAt(columnNumberOfValues).getValue());
			if (ignoreCase)
				{
				if (valuesToExclude.indexOfIgnoreCase(((MiTableCells )rows.get(i)).elementAt(columnNumberOfValues).getValue()) != -1)
					{
					rows.remove(i);
//MiDebug.println("REMOVE");
					--i;
					changed = true;
					continue;
					}
				}
			else if (valuesToExclude.contains(((MiTableCells )rows.get(i)).elementAt(columnNumberOfValues).getValue()))
				{
				rows.remove(i);
//MiDebug.println("REMOVE");
				--i;
				changed = true;
				continue;
				}

			boolean includeRow = true;
			if (valuesToInclude.size() > 0)
				{
				includeRow = false;
				}
			for (int j = 0; j < valuesToInclude.size(); ++j)
				{
				String toMatch = valuesToInclude.get(j);
				int compareMethod = compareMethods.elementAt(j);
				String value = ((MiTableCells )rows.get(i)).elementAt(columnNumberOfValues).getValue();
//MiDebug.println("value=" + value);
				if (value != null)
					{
					if (compareMethod == STARTS_WITH)
						{
						if (ignoreCase)
							{
							if (Utility.startsWithIgnoreCase(value, toMatch))
								{
								includeRow = true;
								}
							}
						else if (value.startsWith(toMatch))
							{
							includeRow = true;
							}
						}
					else if (compareMethod == EQUALS)
						{
						if (ignoreCase)
							{
							if (value.equalsIgnoreCase(toMatch))
								{
								includeRow = true;
								}
							}
						else if (value.equals(toMatch))
							{
							includeRow = true;
							}
						}
					else if (compareMethod == CONTAINS)
						{
						if (ignoreCase)
							{
							if (value.toUpperCase().indexOf(toMatch.toUpperCase()) != -1)
								{
								includeRow = true;
								}
							}
						else if (value.indexOf(toMatch) != -1)
							{
							includeRow = true;
							}
						}
					}
				}
			if (!includeRow)
				{
//MiDebug.println("REMOVE");
				rows.remove(i);
				--i;
				changed = true;
				}
			}
//if (valuesToExclude.size() > 3)
//MiDebug.println("rows now =" + rows);
		return(changed);
		}
	}

