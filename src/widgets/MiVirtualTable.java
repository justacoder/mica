
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
public class MiVirtualTable implements MiiScrollableData
	{
	private		MiTable		table;
	private		MiScrolledBox	scrolledBox;
	private		ArrayList	rows = new ArrayList();
	private		boolean		inRedisplay;
	private		boolean		isValidDisplay;
	private		boolean		autoRebuildEnabled = true;
	private		int		topVisibleRow = -1;
	private		int		totalNumberOfRows;
	private		int		numberOfVisibleRows;
	private		double		fractionallyVisibleRow;
	private		ArrayList	reporters = new ArrayList();
	private		ArrayList	filters = new ArrayList();
	private		ArrayList	highlighters = new ArrayList();
	private		ArrayList	orderers = new ArrayList();
	private		MiiVirtualTableRowSource	source;
	private		MiBounds	tmpBounds = new MiBounds();

	public				MiVirtualTable(MiTable table, MiScrolledBox scrolledBox, MiiVirtualTableRowSource dataSource)
		{
		this.table = table;
		this.scrolledBox = scrolledBox;
		this.source = dataSource;
		table.setSortManager(new MiVirtualTableSortManager(this));
		}
	public		void		addReporter(MiiVirtualTableReporter reporter)
		{
		reporters.add(reporter);
		}
	public		void		removeReporter(MiiVirtualTableReporter reporter)
		{
		reporters.remove(reporter);
		}
	public		void		addFilter(MiiVirtualTableFilter filter)
		{
		filters.add(filter);
		}
	public		void		removeFilter(MiiVirtualTableFilter filter)
		{
		filters.remove(filter);
		}
	public		void		addHighlighter(MiiVirtualTableHighlighter highlighter)
		{
		highlighters.add(highlighter);
		}
	public		void		removeHighlighter(MiiVirtualTableHighlighter highlighter)
		{
		highlighters.remove(highlighter);
		}
	public		void		removeAllHighlighters()
		{
		highlighters = new ArrayList();
		}
	public		void		addOrderer(MiiVirtualTableRowColumnOrderer orderer)
		{
		orderers.add(orderer);
		}
	public		void		removeOrderer(MiiVirtualTableRowColumnOrderer orderer)
		{
		orderers.remove(orderer);
		}
	public		void		removeAllOrderers()
		{
		orderers = new ArrayList();
		}
		
	public		MiTable		getTable()
		{
		return(table);
		}

	public		ArrayList	getRows()
		{
		return(rows);
		}
	public		void		setRows(ArrayList rows)
		{
		this.rows = rows;
		redisplay();
		}

	public		MiDistance	getEstimateOfColumnWidth(int columnNumber)
		{
		MiDistance maxWidth = 0;
		for (int i = 0; i < rows.size(); ++i)
			{
			MiTableCells cells = (MiTableCells )rows.get(i);
			MiTableCell cell = cells.elementAt(columnNumber);
			if (maxWidth < cell.getGraphics().getWidth())
				{
				maxWidth = cell.getGraphics().getWidth();
				}
			}
		maxWidth = Math.max(maxWidth, table.getCell(MiTable.ROW_HEADER_NUMBER, columnNumber).getGraphics().getWidth());
		return(maxWidth);
		}

	public		void		setTopVisibleRow(int row)
		{
//MiDebug.printStackTrace("setTopVisibleRow WAS=" +  topVisibleRow + ",now=" + row);
//MiDebug.println("TABLE HAS VALID LAYOUT=" + table.hasValidLayout());
		if ((topVisibleRow != row) || (!isValidDisplay))
			{
			topVisibleRow = row;
			//if (table.hasValidLayout())
				{
				redisplay();
				isValidDisplay = table.hasValidLayout();
				}
//MiDebug.println("getTopVisibleRow now=" + getTopVisibleRow());
			}
		}
	public		int		getTopVisibleRow()
		{
		return(topVisibleRow);
		}

	public		void		redisplay()
		{
		if (inRedisplay)
			{
			return;
			}
		// inRedisplay = true;

		table.getBackgroundManager().removeBackgroundOverlays();
		double normalizedHorizontalPosition = table.getNormalizedHorizontalPosition();
		table.removeAllCells();

		for (int i = 0; i < orderers.size(); ++i)
			{
			MiiVirtualTableRowColumnOrderer orderer = (MiiVirtualTableRowColumnOrderer )orderers.get(i);
			orderer.reorder(rows);
			}

		numberOfVisibleRows = 0;
//MiDebug.println("table.getTopVisibleRow()=" + table.getTopVisibleRow());
//MiDebug.println("this.topVisibleRow=" + getTopVisibleRow());
//MiDebug.println("number of rows=" + rows.size());

		MiDistance contentsBoundsHeight = table.getContentsBounds(new MiBounds()).getHeight();
		MiBounds rowBounds = new MiBounds();
		MiDistance rowSpacing = table.getAlleyVSpacing();
		rowSpacing += table.getTableWideDefaults().getInsetMargins().getHeight()/2;
		// rowSpacing += 1;
		// rowSpacing /= 2;
		fractionallyVisibleRow = 0.0;
		ArrayList rowsActuallyDisplayed = new ArrayList();
		for (int i = 0; i < rows.size() - topVisibleRow; ++i)
			{
			if (highlighters.size() > 0)
				{
				rowsActuallyDisplayed.add(rows.get(topVisibleRow + i));
				}

			MiTableCells cells = getTableRow(topVisibleRow + i, i, rowBounds);

//MiDebug.println("appending scells=" + cells);

			// table.appendRow(cells);
			table.appendNonOverlappingContentsCells(cells); // For speed

			++numberOfVisibleRows;

			contentsBoundsHeight -= rowBounds.getHeight();
			contentsBoundsHeight -= rowSpacing;
//MiDebug.println("rowSpacing=" + rowSpacing);
			if (contentsBoundsHeight < 0)
				{
				fractionallyVisibleRow = 0.9;
				break;
				}
			}
		table.setNormalizedHorizontalPosition(normalizedHorizontalPosition);

		for (int i = 0; i < highlighters.size(); ++i)
			{
			MiiVirtualTableHighlighter highlighter = (MiiVirtualTableHighlighter )highlighters.get(i);
			highlighter.highlight(rowsActuallyDisplayed);
			}
		// scrolledBox.updateScrollBars();
		inRedisplay = false;
		}
	public		void		setAutoRebuildEnabled(boolean flag)
		{
		autoRebuildEnabled = flag;
		}

	public		MiTableCells	getTableRow(int rowNumber, int targetDisplayRow, MiBounds rowBounds)
		{
		rowBounds.reverse();
		MiTableCells cells = (MiTableCells )rows.get(rowNumber);
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			cell.setRowNumber(targetDisplayRow);
			rowBounds.accumulateMaxWidthAndHeight(cell.getGraphics().getBounds(tmpBounds));
			}
		return(cells);
		}
	protected	void		rebuild()
		{
		if (!autoRebuildEnabled)
			{
			return;
			}
		topVisibleRow = -1;

		rows = new ArrayList();
		for (int i = 0; i < source.getNumberOfRows(); ++i)
			{
			rows.add(source.getRow(i));
			}
		ArrayList originalListOfRows = new ArrayList();
		originalListOfRows.addAll(rows);
		for (int i = 0; i < filters.size(); ++i)
			{
			((MiiVirtualTableFilter )filters.get(i)).filter(rows);
			}
		for (int i = 0; i < reporters.size(); ++i)
			{
			MiiVirtualTableReporter reporter = (MiiVirtualTableReporter )reporters.get(i);
			reporter.report(originalListOfRows, rows);
			}
		totalNumberOfRows = rows.size();
		table.setNormalizedHorizontalPosition(0.0);
		table.setNormalizedVerticalPosition(1.0);
		setTopVisibleRow(0);
		}
	/*************************************************************************
	*	Implements MiiScrollableData
	*************************************************************************/
					/**------------------------------------------------------
			 		 * Gets whether actions like scrollLineDown is handled
					 * in this interfaces implementation or whether these type
					 * of methods are merely to be notified of the scrolling
					 * calculated elsewhere and executed by calling
					 * scrollToNormalizedVerticalPosition.
			 		 * @return 	 	true if minor scrolling is implemented here
					 *------------------------------------------------------*/
	public		boolean		isHandlingScrollingDiscreteAmountsLocally()
		{
		return(true);
		}

					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * horizontal position of the data (0.0 is the left side
					 * and 1.0 is the right side).
	 				 * @return 	 	the horizontal position
					 *------------------------------------------------------*/
	public		double		getNormalizedHorizontalPosition()
		{
		return(table.getNormalizedHorizontalPosition());
		}


					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * vertical position of the data (0.0 is the left side
					 * and 1.0 is the right side).
	 				 * @return 	 	the vertical position
					 *------------------------------------------------------*/
	public		double		getNormalizedVerticalPosition()
		{
/*
		if (totalNumberOfRows == numberOfVisibleRows)
			{
			return(1.0);
			}
*/
/*
MiDebug.println("getNormalizedVerticalPosition totalNumberOfRows= " + totalNumberOfRows);
MiDebug.println("getNormalizedVerticalPosition numberOfVisibleRows= " + numberOfVisibleRows);
MiDebug.println("getNormalizedVerticalPosition totalNumberOfRows= " + totalNumberOfRows);
MiDebug.println("getNormalizedVerticalPosition getTopVisibleRow()= " + getTopVisibleRow());
MiDebug.println("getNormalizedVerticalPosition (((double )getTopVisibleRow())/(totalNumberOfRows - numberOfVisibleRows)) = " + (getTopVisibleRow()/(totalNumberOfRows - numberOfVisibleRows + fractionallyVisibleRow)));
MiDebug.println("getNormalizedVerticalPosition = " + Math.min(1.0 - (((double )getTopVisibleRow())/(totalNumberOfRows - numberOfVisibleRows + fractionallyVisibleRow)), 1.0));
*/
		return(Math.min(1.0 - (((double )getTopVisibleRow())/(totalNumberOfRows - numberOfVisibleRows + fractionallyVisibleRow)), 1.0));
		}


					/**------------------------------------------------------
			 		 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * horizontal size of the data (0.0 indicates none of the
					 * data is visible and 1.0 indicates all of the data's
					 * width is visible).
	 				 * @return 	 	the amount of data visible
					 *------------------------------------------------------*/
	public		double		getNormalizedHorizontalAmountVisible()
		{
		return(table.getNormalizedHorizontalAmountVisible());
		}


					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * vertical size of the data (0.0 indicates none of the
					 * data is visible and 1.0 indicates all of the data's
					 * height is visible).
	 				 * @return 	 	the amount of data visible
					 *------------------------------------------------------*/
	public		double		getNormalizedVerticalAmountVisible()
		{
//MiDebug.println("getNormalizedVerticalAmountVisible numberOfVisibleRows= " + numberOfVisibleRows);
//MiDebug.println("getNormalizedVerticalAmountVisible totalNumberOfRows= " + totalNumberOfRows);
/*
		if (totalNumberOfRows <= numberOfVisibleRows)
			{
			return(1.0);
			}
*/
//MiDebug.println("getNormalizedVerticalAmountVisible = " + ((double )numberOfVisibleRows - fractionallyVisibleRow)/(totalNumberOfRows));
		return(((double )numberOfVisibleRows - fractionallyVisibleRow)/totalNumberOfRows);
		}


					/**------------------------------------------------------
	 				 * Scrolls one line up (conversely, move the data one
					 * line down). If already at the top of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'line' is.
					 *------------------------------------------------------*/
	public		void		scrollLineUp()
		{
		int topVisibleRow = getTopVisibleRow();
		if (topVisibleRow > 0)
			{
			setTopVisibleRow(topVisibleRow - 1);
			}
		}


					/**------------------------------------------------------
	 				 * Scrolls one line down (conversely, move the data one
					 * line up). If already at the bottom of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'line' is.
					 *------------------------------------------------------*/
	public		void		scrollLineDown()
		{
		int topVisibleRow = getTopVisibleRow();
		if (topVisibleRow < totalNumberOfRows - numberOfVisibleRows + fractionallyVisibleRow)
			{
			setTopVisibleRow(topVisibleRow + 1);
			}
		}


					/**------------------------------------------------------
	 				 * Scrolls one line left (conversely, move the data one
					 * line right). If already at the left of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'line' is.
					 *------------------------------------------------------*/
	public		void		scrollLineLeft()
		{
		table.scrollLineLeft();
		}


					/**------------------------------------------------------
	 				 * Scrolls one line right (conversely, move the data one
					 * line left). If already at the right of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'line' is.
					 *------------------------------------------------------*/
	public		void		scrollLineRight()
		{
		table.scrollLineRight();
		}



					/**------------------------------------------------------
	 				 * Scrolls one chunk up (conversely, move the data one
					 * chunk down). If already at the top of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'chunk' is.
					 *------------------------------------------------------*/
	public		void		scrollChunkUp()
		{
		int topVisibleRow = getTopVisibleRow();
		if (topVisibleRow > 0)
			{
			setTopVisibleRow(Math.max(topVisibleRow - 4, 0));
			}
		}


					/**------------------------------------------------------
	 				 * Scrolls one chunk down (conversely, move the data one
					 * chunk up). If already at the bottom of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'chunk' is.
					 *------------------------------------------------------*/
	public		void		scrollChunkDown()
		{
		int topVisibleRow = getTopVisibleRow();
		if (topVisibleRow < totalNumberOfRows - numberOfVisibleRows + fractionallyVisibleRow)
			{
			setTopVisibleRow(Math.min(topVisibleRow + 4, totalNumberOfRows - numberOfVisibleRows));
			}
		}


					/**------------------------------------------------------
	 				 * Scrolls one chunk left (conversely, move the data one
					 * chunk right). If already at the left of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'chunk' is.
					 *------------------------------------------------------*/
	public		void		scrollChunkLeft()
		{
		table.scrollChunkLeft();
		}


					/**------------------------------------------------------
	 				 * Scrolls one chunk right (conversely, move the data one
					 * chunk left). If already at the right of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'chunk' is.
					 *------------------------------------------------------*/
	public		void		scrollChunkRight()
		{
		table.scrollChunkRight();
		}



					/**------------------------------------------------------
	 				 * Scrolls one page up (conversely, move the data one
					 * page down). If already at the top of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'page' is.
					 *------------------------------------------------------*/
	public		void		scrollPageUp()
		{
		int topVisibleRow = getTopVisibleRow();
		if (topVisibleRow > 0)
			{
			topVisibleRow -= numberOfVisibleRows;
			setTopVisibleRow(Math.max(topVisibleRow, 0));
			}
		}


					/**------------------------------------------------------
	 				 * Scrolls one page down (conversely, move the data one
					 * page up). If already at the bottom of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'page' is.
					 *------------------------------------------------------*/
	public		void		scrollPageDown()
		{
		int topVisibleRow = getTopVisibleRow();
		if (topVisibleRow + numberOfVisibleRows - fractionallyVisibleRow < totalNumberOfRows)
			{
			topVisibleRow += numberOfVisibleRows + 1;
			setTopVisibleRow(Math.min(topVisibleRow, totalNumberOfRows - numberOfVisibleRows + 1));
			}
		}


					/**------------------------------------------------------
	 				 * Scrolls one page left (conversely, move the data one
					 * page right). If already at the left of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'page' is.
					 *------------------------------------------------------*/
	public		void		scrollPageLeft()
		{
		table.scrollPageLeft();
		}


					/**------------------------------------------------------
	 				 * Scrolls one page right (conversely, move the data one
					 * page left). If already at the right of the data then
					 * this does nothing. It is up to the implementation of
					 * this interface to determine what a 'page' is.
					 *------------------------------------------------------*/
	public		void		scrollPageRight()
		{
		table.scrollPageRight();
		}



					/**------------------------------------------------------
	 				 * Scrolls to the top of the data.
					 *------------------------------------------------------*/
	public		void		scrollToTop()
		{
		setTopVisibleRow(0);
		}


					/**------------------------------------------------------
	 				 * Scrolls to the bottom of the data.
					 *------------------------------------------------------*/
	public		void		scrollToBottom()
		{
		setTopVisibleRow(totalNumberOfRows - numberOfVisibleRows + 1);
		}
					/**------------------------------------------------------
	 				 * Scrolls to the left side of the data.
					 *------------------------------------------------------*/
	public		void		scrollToLeftSide()
		{
		table.scrollToLeftSide();
		}


					/**------------------------------------------------------
	 				 * Scrolls to the right side of the data.
					 *------------------------------------------------------*/
	public		void		scrollToRightSide()
		{
		table.scrollToRightSide();
		}



					/**------------------------------------------------------
	 				 * Scrolls to the given normalized (between 0.0 and 1.0)
					 * horizontal position.
					 * @param normalizedPosition	the new horizontal position
					 *------------------------------------------------------*/
	public		void		scrollToNormalizedVerticalPosition(double normalizedPosition)
		{
//MiDebug.printStackTrace("scrollto vertical position: " + (int )((1.0 - normalizedPosition) * (totalNumberOfRows - numberOfVisibleRows)));
		setTopVisibleRow((int )((1.0 - normalizedPosition) * (totalNumberOfRows - numberOfVisibleRows + fractionallyVisibleRow) + 0.5));
		}



					/**------------------------------------------------------
	 				 * Scrolls to the given normalized (between 0.0 and 1.0)
					 * vertical position.
					 * @param normalizedPosition	the new vertical position
					 *------------------------------------------------------*/
	public		void		scrollToNormalizedHorizontalPosition(double normalizedPosition)
		{
//MiDebug.printStackTrace(this + " scrollToNormalizedHorizontalPosition=" + normalizedPosition);
		table.scrollToNormalizedHorizontalPosition(normalizedPosition);
		}


	}


