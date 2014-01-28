
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
public class MiVirtualTableRowHighlighter implements MiiVirtualTableHighlighter
	{
	public final static int		EQUALS = 0;
	public final static int		STARTS_WITH = 1;
	public final static int		ENDS_WITH = 2;

	private		MiTable		table;
	private		int		columnNumberOfValues;
	private		String		toMatch;
	private		int		compareMethod;
	private		String		toMatch2;
	private		int		compareMethod2;
	private		MiPart		rowBackgroundRectangle;

	public				MiVirtualTableRowHighlighter(
						MiTable table, 
						int columnNumberOfValues, 
						int compareMethod, 
						String toMatch, 
						int compareMethod2, 
						String toMatch2, 
						MiPart rowBackgroundRectangle)
		{
		this(table, columnNumberOfValues, compareMethod, toMatch, rowBackgroundRectangle);
		this.toMatch2 = toMatch2;
		this.compareMethod2 = compareMethod2;
		}
	public				MiVirtualTableRowHighlighter(
						MiTable table, 
						int columnNumberOfValues, 
						int compareMethod, 
						String toMatch, 
						MiPart rowBackgroundRectangle)
		{
		this.table = table;
		this.columnNumberOfValues = columnNumberOfValues;
		this.toMatch = toMatch;
		this.compareMethod = compareMethod;
		this.rowBackgroundRectangle = rowBackgroundRectangle;
		}
	public		boolean		highlight(ArrayList rows)
		{
		boolean changed = false;
		for (int i = 0; i < rows.size(); ++i)
			{
			String value = ((MiTableCells )rows.get(i)).elementAt(columnNumberOfValues).getValue();
			boolean appendOverlay = false;
			if (value != null)
				{
				switch (compareMethod)
					{
					case STARTS_WITH :
						appendOverlay = value.startsWith(toMatch);
						break;
					case EQUALS :
						appendOverlay = value.equals(toMatch);
						break;
					case ENDS_WITH :
						appendOverlay = value.endsWith(toMatch);
						break;
					}

				if ((appendOverlay) && (toMatch2 != null))
					{
					switch (compareMethod2)
						{
						case STARTS_WITH :
							appendOverlay = value.startsWith(toMatch2);
							break;
						case EQUALS :
							appendOverlay = value.equals(toMatch2);
							break;
						case ENDS_WITH :
							appendOverlay = value.endsWith(toMatch2);
							break;
						}
					}
				if (appendOverlay)
					{
					table.getBackgroundManager().appendRowBackgroundOverlay(rowBackgroundRectangle, i);
					changed = true;
					}
				}
			}
		return(changed);
		}
	}

