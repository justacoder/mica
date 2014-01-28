
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
public class MiVirtualTableRowsMoveToTop implements MiiVirtualTableRowColumnOrderer
	{
	public final static int		EQUALS = 0;
	public final static int		STARTS_WITH = 1;

	private		MiTable		table;
	private		boolean		ignoreCase;
	private		int		compareMethod;
	private		int		columnNumberOfValues;
	private		String		toMatch;

	public				MiVirtualTableRowsMoveToTop(
						MiTable table, 
						int columnNumberOfValues, 
						int compareMethod,
						String toMatch)
		{
		this.table = table;
		this.columnNumberOfValues = columnNumberOfValues;
		this.toMatch = toMatch;
		this.compareMethod = compareMethod;
		}
	public		boolean		reorder(ArrayList rows)
		{
		boolean changed = false;
		int targetInsertionPoint = 0;
		for (int i = 0; i < rows.size(); ++i)
			{
			String value = ((MiTableCells )rows.get(i)).elementAt(columnNumberOfValues).getValue();
			if (value != null)
				{
				if (compareMethod == STARTS_WITH)
					{
					if (value.startsWith(toMatch))
						{
						Object row = rows.get(i);
						rows.remove(i);
						rows.add(targetInsertionPoint++, row);
						changed = true;
						}
					}
				else if (compareMethod == EQUALS)
					{
					if (value.equals(toMatch))
						{
						Object row = rows.get(i);
						rows.remove(i);
						rows.add(targetInsertionPoint++, row);
						changed = true;
						}
					}
				}
			}
		return(changed);
		}
	}

