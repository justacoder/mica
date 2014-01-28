
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiGridBackgrounds implements MiiTypes
	{
	private		FastVector		overlayColumnBackgrounds	= new FastVector();
	private		FastVector		generalColumnBackgrounds	= new FastVector();
	private		FastVector		overlayRowBackgrounds		= new FastVector();
	private		FastVector		generalRowBackgrounds		= new FastVector();
	private		FastVector		overlayCellBackgrounds		= new FastVector();
	private		FastVector		generalCellBackgrounds		= new FastVector();

	private		MiAttributes		generalCellAttributes;

	private		FastVector		innerHLines			= new FastVector();
	private		FastVector		innerVLines			= new FastVector();
	private		MiiBackgroundableGrid	grid;
	private		MiBounds		tmpBounds = new MiBounds();
	private		int[]			visibleRows;
	private		int[]			visibleColumns;



	public				MiGridBackgrounds(MiiBackgroundableGrid grid)
		{
		this.grid = grid;
		}

	public		void		setCellAttributes(MiAttributes atts)
		{
		for (int i = 0; i < generalCellBackgrounds.size(); ++i)
			{
			MiGridBackground bg = (MiGridBackground )generalCellBackgrounds.elementAt(i);
			bg.graphics.setAttributes(bg.graphics.getAttributes().overrideFrom(atts));
			}
		generalCellAttributes = atts;
		}
	public		MiAttributes	getCellAttributes()
		{
		return(generalCellAttributes == null ? new MiAttributes() : generalCellAttributes);
		}
/*
	public		MiParts		getAssociatedParts(MiParts parts)
		{
		for (int i = 0; i < generalCellBackgrounds.size(); ++i)
			{
			MiGridBackground bg = (MiGridBackground )generalCellBackgrounds.elementAt(i);
			parts.addElement(bg.graphics);
			}
		return(parts);
		}
***/
	public		Object		appendCellBackgrounds(MiPart rect)
		{
		MiGridBackground bg = new MiGridBackground();
		bg.graphics = rect;
		generalCellBackgrounds.addElement(bg);
		grid.invalidateArea();
		return(bg);
		}
	public		Object		appendCellBackgroundOverlay(MiPart rect, int rowNum, int columnNum)
		{
		MiGridBackground bg = new MiGridBackground();
		bg.graphics = rect;
		bg.number = rowNum;
		bg.startGrid = columnNum;
		bg.endGrid = columnNum;
		overlayCellBackgrounds.addElement(bg);
		grid.invalidateArea(grid.getCellBounds(rowNum, columnNum, tmpBounds));
		return(bg);
		}
	public		Object		appendRowBackgrounds(MiPart rect)
		{
		return(appendRowRepeatingBackground(rect, 0, 0, -1, 1, false));
		}
	public		Object		appendColumnBackgrounds(MiPart rect)
		{
		return(appendColumnRepeatingBackground(rect, 0, 0, -1, 1, false));
		}
	public		Object		appendRowBackground(MiPart rect, int rowNum)
		{
		return(appendRowRepeatingBackground(rect, rowNum, 0, -1, 0, false));
		}
	public		Object		appendColumnBackground(MiPart rect, int columnNum)
		{
		return(appendColumnRepeatingBackground(rect, columnNum, 0, -1, 0, false));
		}
	public		Object		appendRowBackgroundOverlay(MiPart rect, int rowNum)
		{
		return(appendRowRepeatingBackground(rect, rowNum, 0, -1, 0, true));
		}
	public		Object		appendColumnBackgroundOverlay(MiPart rect, int columnNum)
		{
		return(appendColumnRepeatingBackground(rect, columnNum, 0, -1, 0, true));
		}
	public		Object		appendColumnBackground(
						MiPart rect, int columnNum, int startRow, int endRow)
		{
		return(appendColumnRepeatingBackground(rect, columnNum, startRow, endRow, 0, false));
		}
	public		Object		appendRowBackground(
						MiPart rect, int rowNum, int startColumn, int endColumn)
		{
		return(appendRowRepeatingBackground(rect, rowNum, startColumn, endColumn, 0, false));
		}
	public		Object		appendRowRepeatingBackground(
						MiPart rect, 
						int rowNum, 
						int startColumn, 
						int endColumn, 
						int repeatEveryNumberOfRows,
						boolean isOverlay)
		{
		MiGridBackground bg = new MiGridBackground();
		bg.graphics = rect;
		bg.number = rowNum;
		bg.orientation = Mi_HORIZONTAL;
		bg.startGrid = startColumn;
		bg.endGrid = endColumn;
		bg.repeatEvery = repeatEveryNumberOfRows;
		if (isOverlay)
			overlayRowBackgrounds.addElement(bg);
		else
			generalRowBackgrounds.addElement(bg);
		if (repeatEveryNumberOfRows == 0)
			grid.invalidateArea(grid.getRowBounds(rowNum, startColumn, endColumn, tmpBounds));
		else
			grid.invalidateArea();
		return(bg);
		}

	public		Object		appendColumnRepeatingBackground(
						MiPart rect, 
						int columnNum, 
						int startRow, 
						int endRow, 
						int repeatEveryNumberOfColumns,
						boolean isOverlay)
		{
		MiGridBackground bg = new MiGridBackground();
		bg.graphics = rect;
		bg.number = columnNum;
		bg.orientation = Mi_VERTICAL;
		bg.startGrid = startRow;
		bg.endGrid = endRow;
		bg.repeatEvery = repeatEveryNumberOfColumns;
		if (isOverlay)
			overlayColumnBackgrounds.addElement(bg);
		else
			generalColumnBackgrounds.addElement(bg);
		if (repeatEveryNumberOfColumns == 0)
			grid.invalidateArea(grid.getColumnBounds(columnNum, startRow, endRow, tmpBounds));
		else
			grid.invalidateArea();
		return(bg);
		}
	public		Object		appendInnerHorizontalLines(MiPart line)
		{
		return(appendInnerHorizontalLines(line, 0, 0, -1, 1));
		}
	public		Object		appendInnerHorizontalLines(
						MiPart line,
						int rowNum, 
						int startColumn, 
						int endColumn, 
						int repeatEveryNumberOfRows)
		{
		MiGridBackground bg = new MiGridBackground();
		bg.graphics = line;
		bg.number = rowNum;
		bg.orientation = Mi_HORIZONTAL;
		bg.startGrid = startColumn;
		bg.endGrid = endColumn;
		bg.repeatEvery = repeatEveryNumberOfRows;
		innerHLines.addElement(bg);
		if (repeatEveryNumberOfRows == 0)
			grid.invalidateArea(grid.getColumnBounds(rowNum, startColumn, endColumn, tmpBounds));
		else
			grid.invalidateArea();
		return(bg);
		}
	public		Object		appendInnerVerticalLines(MiPart line)
		{
		return(appendInnerVerticalLines(line, 0, 0, -1, 1));
		}
	public		Object		appendInnerVerticalLines(
						MiPart line,
						int columnNum, 
						int startRow, 
						int endRow, 
						int repeatEveryNumberOfColumns)
		{
		MiGridBackground bg = new MiGridBackground();
		bg.graphics = line;
		bg.number = columnNum;
		bg.orientation = Mi_VERTICAL;
		bg.startGrid = startRow;
		bg.endGrid = endRow;
		bg.repeatEvery = repeatEveryNumberOfColumns;
		innerVLines.addElement(bg);
		if (repeatEveryNumberOfColumns == 0)
			grid.invalidateArea(grid.getColumnBounds(columnNum, startRow, endRow, tmpBounds));
		else
			grid.invalidateArea();
		return(bg);
		}
	public		void		removeBackground(Object background)
		{
		MiGridBackground bg = (MiGridBackground )background;
		if (bg.orientation == Mi_NONE)
			{
			overlayCellBackgrounds.removeElement(background);
			generalCellBackgrounds.removeElement(background);
			grid.getCellBounds(bg.number, bg.startGrid, tmpBounds);
			}
		else if (bg.orientation == Mi_VERTICAL)
			{
			overlayColumnBackgrounds.removeElement(background);
			generalColumnBackgrounds.removeElement(background);
			grid.getColumnBounds(bg.number, bg.startGrid, bg.endGrid, tmpBounds);
			}
		else // if (bg.orientation == Mi_HORIZONTAL)
			{
			overlayRowBackgrounds.removeElement(background);
			generalRowBackgrounds.removeElement(background);
			grid.getRowBounds(bg.number, bg.startGrid, bg.endGrid, tmpBounds);
			}
		grid.invalidateArea(tmpBounds);
		}
	public		void		removeBackgrounds()
		{
		generalRowBackgrounds.removeAllElements();
		generalColumnBackgrounds.removeAllElements();
		generalCellBackgrounds.removeAllElements();
		grid.invalidateArea();
		}
	public		void		removeBackgroundOverlays()
		{
		overlayRowBackgrounds.removeAllElements();
		overlayColumnBackgrounds.removeAllElements();
		overlayCellBackgrounds.removeAllElements();
		grid.invalidateArea();
		}
	public		void		removeCellBackgroundOverlay(int rowNum, int columnNum)
		{
		boolean removedSomething = false;
		for (int i = 0; i < overlayCellBackgrounds.size(); ++i)
			{
			if (((MiGridBackground )overlayCellBackgrounds.elementAt(i)).isAssignedTo(rowNum, columnNum))
				{
				removedSomething = true;
				overlayCellBackgrounds.removeElementAt(i);
				--i;
				}
			}

		if (removedSomething)
			grid.invalidateArea(grid.getCellBounds(rowNum, columnNum, tmpBounds));
		}
	public		void		removeRowBackgroundOverlay(int rowNum)
		{
		for (int i = 0; i < overlayRowBackgrounds.size(); ++i)
			{
			if (((MiGridBackground )overlayRowBackgrounds.elementAt(i)).number == rowNum)
				{
				overlayRowBackgrounds.removeElementAt(i);
				--i;
				}
			}
		grid.invalidateArea(grid.getRowBounds(rowNum, 0, -1, tmpBounds));
		}
	public		void		removeColumnBackgroundOverlay(int columnNum)
		{
		for (int i = 0; i < overlayColumnBackgrounds.size(); ++i)
			{
			if (((MiGridBackground )overlayColumnBackgrounds.elementAt(i)).number == columnNum)
				{
				overlayColumnBackgrounds.removeElementAt(i);
				--i;
				}
			}
		grid.invalidateArea(grid.getColumnBounds(columnNum, 0, -1, tmpBounds));
		}

	public		void		render(MiRenderer renderer)
		{
		render(renderer, grid.getVisibleRows(), grid.getVisibleColumns());
		}
	public		void		render(MiRenderer renderer, int[] visibleRows, int[] visibleColumns)
		{
		this.visibleRows = visibleRows;
		this.visibleColumns = visibleColumns;

		// Priority order, 1st to last: general: cell, row, column, overlay: column, row, cell
		if (generalCellBackgrounds.size() > 0)
			drawCellBackgrounds(renderer, generalCellBackgrounds);

		if (generalRowBackgrounds.size() > 0)
			drawRowBackgrounds(renderer, generalRowBackgrounds);

		if (generalColumnBackgrounds.size() > 0)
			drawColumnBackgrounds(renderer, generalColumnBackgrounds);

		if (overlayColumnBackgrounds.size() > 0)
			drawColumnBackgrounds(renderer, overlayColumnBackgrounds);

		if (overlayRowBackgrounds.size() > 0)
			drawRowBackgrounds(renderer, overlayRowBackgrounds);

		if (overlayCellBackgrounds.size() > 0)
			drawCellBackgrounds(renderer, overlayCellBackgrounds);

		if (innerHLines.size() > 0)
			drawInnerHLines(renderer);

		if (innerVLines.size() > 0)
			drawInnerVLines(renderer);
		}

	protected	void		drawRowBackgrounds(MiRenderer renderer, FastVector rowBackgrounds)
		{
		for (int index = 0; index < visibleRows.length; ++index)
			{
			int row = visibleRows[index];
			if (row == -1)
				continue;

			for (int i = 0; i < rowBackgrounds.size(); ++i)
				{
				MiGridBackground gb = (MiGridBackground )rowBackgrounds.elementAt(i);
				for (int j = 0; j < visibleColumns.length; ++j)
					{
					int column = visibleColumns[j];
					if (column == -1)
						continue;
					if (gb.isAssignedTo(row, column))
						{
						grid.getRowBounds(row, gb.startGrid, gb.endGrid, tmpBounds);
						if (!tmpBounds.isReversed())
							{
							gb.graphics.setBounds(tmpBounds);
							gb.graphics.draw(renderer);
							}
						break;
						}
					}
				}
			}
		}
	protected	void		drawColumnBackgrounds(
						MiRenderer renderer, FastVector columnBackgrounds)
		{
		for (int index = 0; index < visibleColumns.length; ++index)
			{
			int column = visibleColumns[index];
			if (column == -1)
				continue;

			for (int i = 0; i < columnBackgrounds.size(); ++i)
				{
				MiGridBackground gb = (MiGridBackground )columnBackgrounds.elementAt(i);
				for (int j = 0; j < visibleRows.length; ++j)
					{
					int row = visibleRows[j];
					if (row == -1)
						continue;
					if (gb.isAssignedTo(row, column))
						{
						grid.getColumnBounds(column, 
							gb.startGrid, gb.endGrid, tmpBounds);
						if (!tmpBounds.isReversed())
							{
							gb.graphics.setBounds(tmpBounds);
							gb.graphics.draw(renderer);
							}
						}
					}
				}
			}
		}
	protected	void		drawCellBackgrounds(MiRenderer renderer, FastVector cellBackgrounds)
		{
		for (int row = 0; row < visibleRows.length; ++row)
			{
			if (visibleRows[row] == -1)
				continue;

			for (int column = 0; column < visibleColumns.length; ++column)
				{
				if (visibleColumns[column] == -1)
					continue;
				for (int i = 0; i < cellBackgrounds.size(); ++i)
					{
					MiGridBackground gb = (MiGridBackground )cellBackgrounds.elementAt(i);

					if ((cellBackgrounds == generalCellBackgrounds)
						|| (grid.isCellIntersectingRowColumn(
							gb.number, gb.startGrid, 
							visibleRows[row], visibleColumns[column])))
						{
						grid.getCellBounds(
							visibleRows[row], visibleColumns[column], tmpBounds);
						if ((!tmpBounds.isReversed())
							&& (!renderer.boundsClipped(tmpBounds)))
							{
							gb.graphics.setBounds(tmpBounds);
							gb.graphics.draw(renderer);
							}
						}
					}
				}
			}
		}
	protected	void		drawInnerHLines(MiRenderer renderer)
		{
		for (int index = 0; index < visibleRows.length - 1; ++index)
			{
			int row = visibleRows[index];
			if (row == -1)
				continue;

			int underRow = visibleRows[index + 1];
			if (underRow == -1)
				continue;

			for (int i = 0; i < innerHLines.size(); ++i)
				{
				MiGridBackground gb = (MiGridBackground )innerHLines.elementAt(i);
				if (gb.isAssignedTo(row))
					{
					grid.getRowBounds(row, gb.startGrid, gb.endGrid, tmpBounds);
					if (!tmpBounds.isReversed())
						{
						MiCoord x1 = tmpBounds.xmin;
						MiCoord x2 = tmpBounds.xmax;
						MiCoord y = tmpBounds.ymin;

						grid.getRowBounds(underRow, 0, -1, tmpBounds);
						if (!tmpBounds.isReversed())
							{
							y = (y + tmpBounds.ymax)/2;
							gb.graphics.setPoint(0, x1, y);
							gb.graphics.setPoint(1, x2, y);
							gb.graphics.draw(renderer);
							}
						}
					}
				}
			}
		}
	protected	void		drawInnerVLines(MiRenderer renderer)
		{
		for (int index = 0; index < visibleColumns.length - 1; ++index)
			{
			int column = visibleColumns[index];
			if (column == -1)
				continue;

			int rightColumn = visibleColumns[index + 1];
			if (rightColumn == -1)
				continue;

			for (int i = 0; i < innerVLines.size(); ++i)
				{
				MiGridBackground gb = (MiGridBackground )innerVLines.elementAt(i);
				if (gb.isAssignedTo(column))
					{
					grid.getColumnBounds(column, gb.startGrid, gb.endGrid, tmpBounds);
					if (!tmpBounds.isReversed())
						{
						MiCoord y1 = tmpBounds.ymin;
						MiCoord y2 = tmpBounds.ymax;
						MiCoord x = tmpBounds.xmax;

						grid.getColumnBounds(rightColumn, 0, -1, tmpBounds);
						if (!tmpBounds.isReversed())
							{
							x = (x + tmpBounds.xmin)/2;
							gb.graphics.setPoint(0, x, y1);
							gb.graphics.setPoint(1, x, y2);
							gb.graphics.draw(renderer);
							}
						}
					}
				}
			}
		}
	// FIX: implement
	protected	void		drawBorderLines(MiRenderer renderer)
		{
		}
	}
class MiGridBackground
	{
	MiPart		graphics;
	int		orientation		= MiiTypes.Mi_NONE;
	int		number;			// row or column number
	int		startGrid 		= 0;
	int		endGrid 		= -1;	// -1 is last
	int		repeatEvery		= 0;

	boolean		isAssignedTo(int num)
		{
		if (num == number)
			return(true);

		if (repeatEvery == 1)
			return(true);

		if ((repeatEvery > 0) && (num > number))
			{
			return(((num - number) % repeatEvery) == 0);
			}
		return(false);
		}
	boolean		isAssignedTo(int num, int otherNum)
		{
		if (!((otherNum >= startGrid) && ((endGrid == -1) || (otherNum <= endGrid))))
			return(false);

		return(isAssignedTo(num));
		}
	}



