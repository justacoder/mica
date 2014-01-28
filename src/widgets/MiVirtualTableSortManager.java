
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
import com.swfm.mica.util.Strings;

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
public class MiVirtualTableSortManager extends MiTableSortManager
	{
	private		MiVirtualTable	virtualTable;


	public				MiVirtualTableSortManager(MiVirtualTable virtualTable)
		{
		super(virtualTable.getTable());
		this.virtualTable = virtualTable;
		}
	public		void		sort(MiiTableSortMethod sortMethod, int columnNumber, boolean isColumn, int direction)
		{
		if (!getEnabled())
			{
			return;
			}
		if (!isColumn)
			{
			super.sort(columnNumber, isColumn, direction);
			return;
			}

		ArrayList rows = virtualTable.getRows();

		Strings values = new Strings();
		for (int i = 0; i < rows.size(); ++i)
			{
			values.add(((MiTableCells )rows.get(i)).elementAt(columnNumber).getValue());
			}

		int numValues = values.size();
		int[] indices = new int[numValues];
		boolean changed = sortMethod.sort(values, indices, direction);

		for (int i = 0; i < numValues; ++i)
			{
			int prevLocation = indices[i];
			if (prevLocation != i)
				{
				Object row_i = rows.get(i);
				Object row_prev = rows.get(prevLocation);
				rows.set(i, row_prev);
				rows.set(prevLocation, row_i);

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
		virtualTable.setRows(rows);
		}
	}

