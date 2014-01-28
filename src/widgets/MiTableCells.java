
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
import com.swfm.mica.util.TypedVector; 
import com.swfm.mica.util.Strings;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTableCells extends TypedVector
	{
	private		MiTable		table;




	public				MiTableCells(MiTable table)
		{
		this.table = table;
		}
	public		void		setVisible(boolean flag)
		{
		for (int i = 0; i < size(); ++i)
			{
			elementAt(i).setVisible(flag);
			}
		}
	public		MiTable		getTable()
		{
		return(table);
		}
	public		MiTableCell	getLastCell()
		{
		MiTableCell maxCell	= null;
		int maxRowNumber 	= -1;
		int maxColumnNumber 	= -1;
		for (int i = 0; i < size(); ++i)
			{
			MiTableCell cell = elementAt(i);
			// If non-empty cell and...
			if ((!cell.isEmpty())
				&& ((cell.rowNumber > maxRowNumber)
				|| 
				((cell.rowNumber == maxRowNumber)
				&& (cell.columnNumber > maxColumnNumber))))
				{
				maxCell = cell;
				maxRowNumber = cell.rowNumber;
				maxColumnNumber = cell.columnNumber;
				}
			}
		return(maxCell);
		}
	public		MiTableCells	appendPart(MiPart obj)
		{
		MiTableCell cell = new MiTableCell(table, 0, 0, obj);
		cell.setTag(obj);
		addElement(cell);
		return(this);
		}
	public		MiTableCells	appendCells(Strings cells)
		{
		for (int i = 0; i < cells.size(); ++i)
			{
			appendPart(new MiText(cells.elementAt(i)));
			}
		return(this);
		}
	public		MiTableCell	elementAt(int index)
		{
		return((MiTableCell )vector.elementAt(index));
		}
	public		MiTableCell	lastElement()
		{
		return((MiTableCell )vector.lastElement());
		}
	public		void		addElement(MiTableCell obj)
		{
		vector.addElement(obj);
		}
	public		void		insertElementAt(MiTableCell obj, int index)
		{
		vector.insertElementAt(obj, index);
		}
	public		boolean		removeElement(MiTableCell obj)
		{
		return(vector.removeElement(obj));
		}
	public		int		indexOf(MiTableCell obj)
		{
		return(vector.indexOf(obj));
		}
	public		boolean		contains(MiTableCell obj)
		{
		return(vector.contains(obj));
		}
	}

