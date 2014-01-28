
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
import com.swfm.mica.util.FastVector; 
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.Strings;


/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiTableSortManager
	{
	private		MiTable	 	table;
	private		MiiTableSortMethod 	sortMethod;
	private		boolean	 	enabled			= true;
	private		int	 	sortDirection		= MiiTableSortMethod.Mi_FORWARD;
	private		IntVector	sortMethodColumns	= new IntVector();
	private		FastVector	columnSortMethods	= new FastVector();

	
	public				MiTableSortManager(MiTable table)
		{
		this.table = table;
		sortMethod = new MiAlphabeticalSortMethod();
		}

	public		void		setEnabled(boolean flag)
		{
		enabled = flag;
		}
	public		boolean		getEnabled()
		{
		return(enabled);
		}

	public		void		setSortMethod(MiiTableSortMethod method)
		{
		sortMethod = method;
		}
	public		MiiTableSortMethod	getSortMethod()
		{
		return(sortMethod);
		}
	public		void		setColumnSortMethod(int columnNumber, MiiTableSortMethod method)
		{
		int index = sortMethodColumns.indexOf(columnNumber);
		if (index != -1)
			{
			sortMethodColumns.removeElementAt(index);
			columnSortMethods.removeElementAt(index);
			}
		if (method != null)
			{
			sortMethodColumns.addElement(columnNumber);
			columnSortMethods.addElement(method);
			}
		}
	public		MiiTableSortMethod	getColumnSortMethod(int columnNumber)
		{
		int index = sortMethodColumns.indexOf(columnNumber);
		if (index != -1)
			{
			return((MiiTableSortMethod )columnSortMethods.elementAt(index));
			}
		return(null);
		}

	public		void		setSortDirection(int direction)
		{
		sortDirection = direction;
		}
	public		int		getSortDirection()
		{
		return(sortDirection);
		}

	public		void		sortRow(int rowNumber)
		{
		sort(rowNumber, false, sortDirection);
		}
	public		void		sortColumn(int columnNumber)
		{
		sort(columnNumber, true, sortDirection);
		}
	public		void		sort(int columnNumber, boolean isColumn, int direction)
		{
		MiiTableSortMethod sortMethod = this.sortMethod;
		int index;
		if ((isColumn) && ((index = sortMethodColumns.indexOf(columnNumber)) != -1))
			{
			sortMethod = (MiiTableSortMethod )columnSortMethods.elementAt(index);
			}
		sort(sortMethod, columnNumber, isColumn, direction);
		}

	public		void		sort(MiiTableSortMethod sortMethod, int columnNumber, boolean isColumn, int direction)
		{
		if (!enabled)
			return;

		Strings values;
		if (isColumn)
			values = table.getColumnValues(columnNumber);
		else
			values = table.getRowValues(columnNumber);

		int numValues = values.size();
		int[] indices = new int[numValues];
		boolean changed = sortMethod.sort(values, indices, direction);

		for (int i = 0; i < numValues; ++i)
			{
			int prevLocation = indices[i];
			if (prevLocation != i)
				{
				if (isColumn)
					table.switchRows(i, prevLocation);
				else
					table.switchColumns(i, prevLocation);

				// Fixup the location of where the row/column that was swapped with now is.
				for (int j = 0; j < indices.length; ++j)
					{
					if (indices[j] == i)
						{
						indices[j] = prevLocation;
						break;
						}
					}
				// Keep the already placed row/column position up to date so that
				// it does not get found by the above loop in future swaps.
				indices[i] = i;
				}
			}
		}

	}


