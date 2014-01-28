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
import com.swfm.mica.util.FastVector; 
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.Strings;


import com.swfm.mica.util.Utility;

//for MiTableHeaderAndFooterEventManager
//import java.awt.Image;

// TO DO: when table contents is empty, look at column headers, footers, row headers, footers
// to get size and layout
// TO DO: when columns are swapped, swap sortMethods in the sort manager
/**
 * getColumnDefaults(columnNumber).setStatusHelp(hlepInfo);
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTable extends MiWidget implements MiiScrollableData, MiiBackgroundableGrid, MiiAdjustableGrid
	{
	public static final String	Mi_CELL_BG_PROPERTY_NAME		= "cell";
	public static final String	Mi_ROW_HEADER_BG_PROPERTY_NAME		= "rowHeader";
	public static final String	Mi_COLUMN_HEADER_BG_PROPERTY_NAME	= "columnHeader";
	public static final String	Mi_ROW_FOOTER_BG_PROPERTY_NAME		= "rowFooter";
	public static final String	Mi_COLUMN_FOOTER_BG_PROPERTY_NAME	= "columnFooter";
	public static final String	Mi_CONTENTS_BG_PROPERTY_NAME		= "contentBackground";

	private		MiTableCells	rowHeaderCells			= new MiTableCells(this);
	private		MiTableCells	rowFooterCells			= new MiTableCells(this);
	private		MiTableCells	columnHeaderCells		= new MiTableCells(this);
	private		MiTableCells	columnFooterCells		= new MiTableCells(this);

	private		MiTableCells	contentsCells			= new MiTableCells(this);

	private		MiReference	rowHeaderContainer		= new MiReference();
	private		MiReference	rowFooterContainer		= new MiReference();
	private		MiReference	columnHeaderContainer		= new MiReference();
	private		MiReference	columnFooterContainer		= new MiReference();

	private		MiReference	contentsContainer		= new MiReference();

	private		MiTableCell	tableContentAreaDefaults;
	private		MiTableCell	tableWideCellDefaults;
	private		MiTableCells	rowDefaults			= new MiTableCells(this);
	private		MiTableCells	columnDefaults			= new MiTableCells(this);
	private		MiDistance	minimumRowHeight		= 5;
	private		MiDistance	minimumColumnWidth		= 5;

	private		int		minNumberOfVisibleRows		= 3;
	private		int		prefNumberOfVisibleRows		= 6;
	private		int		maxNumberOfVisibleRows		= -1;

	private		int		minNumberOfVisibleColumns	= 1;
	private		int		prefNumberOfVisibleColumns	= 6;
	private		int		maxNumberOfVisibleColumns	= -1;

	private		int		totalNumberOfRows		= 0;
	private		int		totalNumberOfColumns		= 0;

	private		boolean		fixedTotalNumberOfRows;
	private		boolean		fixedTotalNumberOfColumns;

	private		int		numberOfVisibleRows		= 1;
	private		int		numberOfVisibleColumns		= 1;

	private		int		topVisibleRow			= 0;
	private		int		leftVisibleColumn		= 0;

	private		int		topLoadedRow			= 0;
	private		int		leftLoadedColumn		= 0;
	private		int		numberOfLoadedRows		= 0;
	private		int		numberOfLoadedColumns		= 0;

	private		MiPart	 	contentsBackground;

	private		MiMargins	sectionMargins			= new MiMargins(8);
	private		MiMargins	contentsMargins			= new MiMargins(4);
	private		MiMargins	rowHeaderMargins		= new MiMargins();
	private		MiMargins	rowFooterMargins		= new MiMargins();
	private		MiMargins	columnHeaderMargins		= new MiMargins();
	private		MiMargins	columnFooterMargins		= new MiMargins();

	private		MiPart		rowHeaderBackground;
	private		MiPart		rowFooterBackground;
	private		MiPart		columnHeaderBackground;
	private		MiPart		columnFooterBackground;

	private		MiPart		ulQuadrantBackground;
	private		MiPart		urQuadrantBackground;
	private		MiPart		llQuadrantBackground;
	private		MiPart		lrQuadrantBackground;

	private		MiTableCell	ulQuadrantCell;
	private		MiTableCell	urQuadrantCell;
	private		MiTableCell	llQuadrantCell;
	private		MiTableCell	lrQuadrantCell;

	private		MiTableSortManager		tableSortManager;
	private		MiTableHeaderAndFooterManager	tableHeaderAndFooterManager;
	private		MiTableSelectionManager 	tableSelectionManager;
	private		MiGridBackgrounds 		backgroundManager;
	private		MiTableLayout			tableLayout;

	private		MiBounds	tmpBounds			= new MiBounds();
	private		MiBounds	tmpBounds2			= new MiBounds();

	public static final int		COLUMN_HEADER_NUMBER		= -12;
	public static final int		COLUMN_FOOTER_NUMBER		= -13;
	public static final int		ROW_HEADER_NUMBER		= -14;
	public static final int		ROW_FOOTER_NUMBER		= -15;

	private static final int[]	ROW_HEADER_ARRAY		= {ROW_HEADER_NUMBER};
	private static final int[]	ROW_FOOTER_ARRAY		= {ROW_FOOTER_NUMBER};
	private static final int[]	COLUMN_HEADER_ARRAY		= {COLUMN_HEADER_NUMBER};
	private static final int[]	COLUMN_FOOTER_ARRAY		= {COLUMN_FOOTER_NUMBER};

	public static final int		Mi_PIXEL_BY_PIXEL_POLICY	= 1;
	public static final int		Mi_LINE_BY_LINE_POLICY		= 2;
	public static final int		Mi_PAGE_BY_PAGE_POLICY		= 3;
	private		int		hAlignmentPolicy		= Mi_PIXEL_BY_PIXEL_POLICY;
	private		int		vAlignmentPolicy		= Mi_PIXEL_BY_PIXEL_POLICY;
	private		int		hAnimationScrollPolicy		= Mi_PIXEL_BY_PIXEL_POLICY;
	private		int		vAnimationScrollPolicy		= Mi_PIXEL_BY_PIXEL_POLICY;
	private		boolean		hDoubleBufferedScroll		= true;
	private		boolean		vDoubleBufferedScroll		= true;

	private		boolean		fillingInCellGaps;
	private		boolean		doNotScrollWhenRender;
	private		boolean		topGravitatesToCellBoundry	= true;
	private		boolean		leftGravitatesToCellBoundry	= true;
	private		double		percentToScrollWhenTopDoesNotGravitateToCellBoundry	= 0.05;
	private		double		percentToScrollWhenLeftDoesNotGravitateToCellBoundry	= 0.05;

	private		int		scrollPolicy			= 1;
	private		MiDistance	leftVisibleColumnXOffset;
	private		MiDistance	topVisibleRowYOffset;
	private		MiDistance	scrollX;
	private		MiDistance	scrollY;
	private		MiDistance	toScrollX;
	private		MiDistance	toScrollY;

	// Used for background renderer
	private		int[]		visibleRows			= new int[0];
	private		int[]		visibleColumns			= new int[0];

	private		double		normalizedVerticalPosition	= 1.0;
	private		double		normalizedHorizontalPosition	= 0.0;

	private		MiVector 	zeroVector 			= new MiVector();




	public				MiTable()
		{
		setIsOpaqueRectangle(true);

		setVisibleContainerAutomaticLayoutEnabled(false);

		backgroundManager = new MiGridBackgrounds(this);

		tableLayout = new MiTableLayout();
		setLayout(tableLayout);

		contentsBackground = new MiRectangle();
		MiToolkit.setBackgroundAttributes(contentsBackground, 
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		contentsBackground.setInvalidLayoutNotificationsEnabled(false);
		contentsBackground.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		contentsBackground.setName(Mi_CONTENTS_BG_PROPERTY_NAME);
		appendPart(contentsBackground);

		rowHeaderBackground = contentsBackground.copy();
		rowHeaderBackground.setInvalidLayoutNotificationsEnabled(false);
		rowHeaderBackground.setName(Mi_ROW_HEADER_BG_PROPERTY_NAME);
		appendPart(rowHeaderBackground);

		rowFooterBackground = contentsBackground.copy();
		rowFooterBackground.setInvalidLayoutNotificationsEnabled(false);
		rowFooterBackground.setName(Mi_ROW_FOOTER_BG_PROPERTY_NAME);
		appendPart(rowFooterBackground);

		columnHeaderBackground = contentsBackground.copy();
		columnHeaderBackground.setInvalidLayoutNotificationsEnabled(false);
		columnHeaderBackground.setName(Mi_COLUMN_HEADER_BG_PROPERTY_NAME);
		appendPart(columnHeaderBackground);

		columnFooterBackground = contentsBackground.copy();
		columnFooterBackground.setInvalidLayoutNotificationsEnabled(false);
		columnFooterBackground.setName(Mi_COLUMN_FOOTER_BG_PROPERTY_NAME);
		appendPart(columnFooterBackground);

		tableWideCellDefaults = new MiTableCell(this, 0, 0);
		tableWideCellDefaults.insetMargins = new MiMargins(8);
		tableWideCellDefaults.hJustification = Mi_CENTER_JUSTIFIED;
		tableWideCellDefaults.vJustification = Mi_CENTER_JUSTIFIED;

		rowHeaderContainer.setLayout(new MiSizeOnlyLayout());
		rowFooterContainer.setLayout(new MiSizeOnlyLayout());
		columnHeaderContainer.setLayout(new MiSizeOnlyLayout());
		columnFooterContainer.setLayout(new MiSizeOnlyLayout());
		contentsContainer.setLayout(new MiSizeOnlyLayout());


		appendPart(rowHeaderContainer);
		appendPart(rowFooterContainer);
		appendPart(columnHeaderContainer);
		appendPart(columnFooterContainer);
		appendPart(contentsContainer);

		updateVisibleRowsArray();
		updateVisibleColumnsArray();

		tableSelectionManager = new MiTableSelectionManager(this);
		tableSortManager = new MiTableSortManager(this);
		MiBrowsableGridEventHandler browseEventHandler = new MiBrowsableGridEventHandler();
		appendEventHandler(browseEventHandler);
		browseEventHandler.setBrowsableGrid(tableSelectionManager);
		tableHeaderAndFooterManager = new MiTableHeaderAndFooterManager();
		insertEventHandler(tableHeaderAndFooterManager, 0);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}

	protected	void		reCalcDrawBounds(MiBounds bounds)
		{
		getBounds(bounds);
		}

	public		MiTableCell	getCellWithTag(Object tag)
		{
		MiTableCell cell = getCellWithTag(contentsCells, tag);
		if (cell != null)
			return(cell);
		cell = getCellWithTag(columnHeaderCells, tag);
		if (cell != null)
			return(cell);
		cell = getCellWithTag(columnFooterCells, tag);
		return(cell);
		}
	protected	MiTableCell	getCellWithTag(MiTableCells cells, Object tag)
		{
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			if ((cell.tag == tag) || ((cell.tag != null) && (cell.tag.equals(tag))))
				return(cell);
			}
		return(null);
		}
	public		void		setSelectionManager(MiTableSelectionManager manager)
		{
		MiiEventHandler handler = getEventHandlerWithClass("MiBrowsableGridEventHandler");
		tableSelectionManager = manager;
		((MiBrowsableGridEventHandler )handler).setBrowsableGrid(tableSelectionManager);
		}
	public		MiTableSelectionManager	getSelectionManager()
		{
		return(tableSelectionManager);
		}
	public		void		setBackgroundManager(MiGridBackgrounds backgrounds)
		{
		backgroundManager = backgrounds;
		}
	public		MiGridBackgrounds getBackgroundManager()
		{
		return(backgroundManager);
		}

	public		void		setSortManager(MiTableSortManager manager)
		{
		tableSortManager = manager;
		}
	public		MiTableSortManager	getSortManager()
		{
		return(tableSortManager);
		}

	public		void		setTableLayout(MiTableLayout layout)
		{
		tableLayout = layout;
		setLayout(tableLayout);
		}
	public		MiTableLayout	getTableLayout()
		{
		return(tableLayout);
		}

	public		void 		setTableHeaderAndFooterManager(MiTableHeaderAndFooterManager manager)
		{
		tableHeaderAndFooterManager = manager;
		}
	public		MiTableHeaderAndFooterManager getTableHeaderAndFooterManager()
		{
		return(tableHeaderAndFooterManager);
		}

	public		void		setContentsBackground(MiPart border)
		{
		contentsBackground = border;
		contentsBackground.setInvalidLayoutNotificationsEnabled(false);
		invalidateArea();
		}
	public		MiPart		getContentsBackground()
		{
		return(contentsBackground);
		}
/*
	public		void		setBackgroundColor(java.awt.Color c)
		{
		super.setBackgroundColor(c);
		contentsBackground.setBackgroundColor(c);
		}
*/
	public		void		setRowHeaderBackground(MiPart border)
		{
		rowHeaderBackground = border;
		rowHeaderBackground.setInvalidLayoutNotificationsEnabled(false);
		invalidateArea();
		}
	public		MiPart		getRowHeaderBackground()
		{
		return(rowHeaderBackground);
		}
	public		void		setRowFooterBackground(MiPart border)
		{
		rowFooterBackground = border;
		rowFooterBackground.setInvalidLayoutNotificationsEnabled(false);
		invalidateArea();
		}
	public		MiPart		getRowFooterBackground()
		{
		return(rowFooterBackground);
		}
	public		void		setColumnHeaderBackground(MiPart border)
		{
		columnHeaderBackground = border;
		columnHeaderBackground.setInvalidLayoutNotificationsEnabled(false);
		invalidateArea();
		}
	public		MiPart		getColumnHeaderBackground()
		{
		return(columnHeaderBackground);
		}
	public		void		setColumnFooterBackground(MiPart border)
		{
		columnFooterBackground = border;
		columnFooterBackground.setInvalidLayoutNotificationsEnabled(false);
		invalidateArea();
		}
	public		MiPart		getColumnFooterBackground()
		{
		return(columnFooterBackground);
		}


					/**
					 * Gets the margins that surround the whole table, outside of any headers or footers
					 **/
	public		MiMargins	getSectionMargins()
		{
		return(sectionMargins);
		}
					/**
					 * Sets the margins that surround the whole table, outside of any headers or footers
					 **/
	public		void		setSectionMargins(MiMargins m)
		{
		sectionMargins = m;
		invalidateLayout();
		}
	public		void		setHeaderAndFooterMargins(MiMargins m)
		{
		rowHeaderMargins.copy(m);
		rowFooterMargins.copy(m);
		columnHeaderMargins.copy(m);
		columnFooterMargins.copy(m);
		invalidateLayout();
		}
					/**
					 * Sets the margins that surround the table contents, 
					 * the rows and columns excluding of any headers or footers
					 **/
	public		void		setContentsMargins(MiMargins m)
		{
		contentsMargins.copy(m);
		rowHeaderMargins.left = contentsMargins.left;
		rowHeaderMargins.right = contentsMargins.right;
		rowFooterMargins.left = contentsMargins.left;
		rowFooterMargins.right = contentsMargins.right;
		columnHeaderMargins.top = contentsMargins.top;
		columnHeaderMargins.bottom = contentsMargins.bottom;
		columnFooterMargins.top = contentsMargins.top;
		columnFooterMargins.bottom = contentsMargins.bottom;
		}
					/**
					 * Gets the margins that surround the table contents, 
					 * the rows and columns excluding of any headers or footers
					 **/
	public		MiMargins	getContentsMargins()
		{
		return(contentsMargins);
		}
	public		void		setRowHeaderMargins(MiMargins m)
		{
		rowHeaderMargins.copy(m);
		rowHeaderMargins.left = contentsMargins.left;
		rowHeaderMargins.right = contentsMargins.right;
		}
	public		MiMargins	getRowHeaderMargins()
		{
		return(rowHeaderMargins);
		}
	public		void		setRowFooterMargins(MiMargins m)
		{
		rowFooterMargins.copy(m);
		rowFooterMargins.left = contentsMargins.left;
		rowFooterMargins.right = contentsMargins.right;
		}
	public		MiMargins	getRowFooterMargins()
		{
		return(rowFooterMargins);
		}
	public		void		setColumnHeaderMargins(MiMargins m)
		{
		columnHeaderMargins.copy(m);
		columnHeaderMargins.top = contentsMargins.top;
		columnHeaderMargins.bottom = contentsMargins.bottom;
		}
	public		MiMargins	getColumnHeaderMargins()
		{
		return(columnHeaderMargins);
		}
	public		void		setColumnFooterMargins(MiMargins m)
		{
		columnFooterMargins.copy(m);
		columnFooterMargins.top = contentsMargins.top;
		columnFooterMargins.bottom = contentsMargins.bottom;
		}
	public		MiMargins	getColumnFooterMargins()
		{
		return(columnFooterMargins);
		}

	public		void		setTopGravitatesToCellBoundry(boolean flag)
		{
		topGravitatesToCellBoundry = flag;
		}
	public		boolean		getTopGravitatesToCellBoundry()
		{
		return(topGravitatesToCellBoundry);
		}

	public		void		setPercentToScrollWhenTopDoesNotGravitateToCellBoundry(double normalizedAmount)
		{
		percentToScrollWhenTopDoesNotGravitateToCellBoundry = normalizedAmount;
		}
	public		double		getPercentToScrollWhenTopDoesNotGravitateToCellBoundry()
		{
		return(percentToScrollWhenTopDoesNotGravitateToCellBoundry);
		}

	public		void		setLeftGravitatesToCellBoundry(boolean flag)
		{
		leftGravitatesToCellBoundry = flag;
		}
	public		boolean		getLeftGravitatesToCellBoundry()
		{
		return(leftGravitatesToCellBoundry);
		}

	protected	MiCoord		getLeftVisibleColumnXOffset()
		{
		return(leftVisibleColumnXOffset);
		}
	protected	MiCoord		getTopVisibleRowYOffset()
		{
		return(topVisibleRowYOffset);
		}
	public		int		getTopVisibleRow()
		{
		return(topVisibleRow);
		}
	public		void		setTopVisibleRow(int number)
		{
		if ((topVisibleRow != number) || (scrollY != 0))
			{
			int oldTopVisibleRow = topVisibleRow;
			MiDistance oldScrollY = scrollY;
//MiDebug.printStackTrace(this + "setTopVisibleRow:" + number);
//MiDebug.println("normalizedVerticalPosition was =" + normalizedVerticalPosition);

			topVisibleRow = number;
			scrollY = 0;

//MiDebug.println("topVisibleRowYOffset was=" + topVisibleRowYOffset);
			topVisibleRowYOffset 
				= tableLayout.getSingleRowBounds(topVisibleRow, 0, 0, tmpBounds).getYmax();
			topVisibleRowYOffset 
				-= tableLayout.getSingleRowBounds(topLoadedRow, 0, 0, tmpBounds).getYmax();
//MiDebug.println("topVisibleRowYOffset now=" + topVisibleRowYOffset);
			updateForNewLayoutPosition();

			MiBounds b = new MiBounds(getContentsBounds(tmpBounds));

			b.addMargins(contentsMargins);
			b.union(columnHeaderBackground.getBounds(tmpBounds));
			b.union(columnFooterBackground.getBounds(tmpBounds));
			super.invalidateArea(b);
//MiDebug.println("invalidateArea =" + b);

 			normalizedVerticalPosition = getNormalizedVerticalPosition(topVisibleRow, 0);

//MiDebug.println("normalizedVerticalPosition =" + normalizedVerticalPosition);
			if (vDoubleBufferedScroll)
				{
				toScrollY += getChangeInVerticalOffset(
						oldTopVisibleRow, 
						oldScrollY, 
						topVisibleRow, 
						scrollY);
				if (toScrollY != 0)
					getDrawManager().setTargetIsManuallyScrolling(true);
				}
			dispatchAction(Mi_ITEM_SCROLLED_ACTION);
			}
		}
	public		int		getLeftVisibleColumn()
		{
		return(leftVisibleColumn);
		}
	public		void		setLeftVisibleColumn(int number)
		{
		if ((leftVisibleColumn != number) || (scrollX != 0))
			{
			int oldLeftVisibleColumn = leftVisibleColumn;
			MiDistance oldScrollX = scrollX;

			leftVisibleColumn = number;
			scrollX = 0;
		
			leftVisibleColumnXOffset 
				= tableLayout.getSingleColumnBounds(leftVisibleColumn, 0, 0, tmpBounds).getXmin();
			leftVisibleColumnXOffset 
				-= tableLayout.getSingleColumnBounds(leftLoadedColumn, 0, 0, tmpBounds).getXmin();
			updateForNewLayoutPosition();

			MiBounds b = new MiBounds(getContentsBounds(tmpBounds));

			b.addMargins(contentsMargins);
			b.union(rowHeaderBackground.getBounds(tmpBounds));
			b.union(rowFooterBackground.getBounds(tmpBounds));
			super.invalidateArea(b);

 			normalizedHorizontalPosition = getNormalizedHorizontalPosition(leftVisibleColumn, 0);

			if (hDoubleBufferedScroll)
				{
				toScrollX += getChangeInHorizontalOffset(
						oldLeftVisibleColumn, 
						oldScrollX, 
						leftVisibleColumn, 
						scrollX);
				if (toScrollX != 0)
					getDrawManager().setTargetIsManuallyScrolling(true);
				}
			dispatchAction(Mi_ITEM_SCROLLED_ACTION);
			}
		}

	public		void		makeVisible(int rowNumber, int columnNumber)
		{
		if (numberOfLoadedRows == 0)
			return;

		MiTableCell cell = getCell(rowNumber, columnNumber);
		if (rowNumber + cell.numberOfRows - 1 < topVisibleRow)
			{
			setTopVisibleRow(rowNumber);
			}
		else if (rowNumber > topVisibleRow + numberOfVisibleRows)
			{
			int topVisibleRowToDisplay = getNumberOfVisibleRowAtTopIfBottomRowIsGivenRowNumber(rowNumber);
			if (topVisibleRowToDisplay < 0)
				{
				topVisibleRowToDisplay = 0;
				}
			setTopVisibleRow(topVisibleRowToDisplay);
			//setTopVisibleRow(rowNumber - numberOfVisibleRowsAbove > 0 
				//? rowNumber - numberOfVisibleRowsAbove : 0);
			}

		if (columnNumber + cell.numberOfColumns - 1 < leftVisibleColumn)
			{
			setLeftVisibleColumn(columnNumber);
			}
		else if (columnNumber > leftVisibleColumn + numberOfVisibleColumns)
			{
			setLeftVisibleColumn(columnNumber - numberOfVisibleColumns >= 0
				? columnNumber - numberOfVisibleColumns + 1 : 0);
			}
		}

	protected	int		getTopLoadedRow()
		{
		return(topLoadedRow);
		}
	protected	int		getNumberOfLoadedRows()
		{
		return(numberOfLoadedRows);
		}
	protected	int		getLeftLoadedColumn()
		{
		return(leftLoadedColumn);
		}
	protected	int		getNumberOfLoadedColumns()
		{
		return(numberOfLoadedColumns);
		}

	public		int		getNumberOfCells()
		{
		return(contentsCells.size());
		}
	public		MiTableCell	getCell(int index)
		{
		return(contentsCells.elementAt(index));
		}
	public		int		getNumberOfRows()
		{
		return(totalNumberOfRows);
		}
	public		int		getNumberOfColumns()
		{
		return(totalNumberOfColumns);
		}
	public		void		setTotalNumberOfRows(int numberOfRows)
		{
		totalNumberOfRows = numberOfRows;
		}
	public		int		getTotalNumberOfRows()
		{
		return(totalNumberOfRows);
		}
	public		void		setTotalNumberOfColumns(int numberOfColumns)
		{
		totalNumberOfColumns = numberOfColumns;
		}
	public		int		getTotalNumberOfColumns()
		{
		return(totalNumberOfColumns);
		}
	/**
	* Specifies whether or not there are a fixed number of rows. This is
	* useful if the caller just wants to add and remove items as if they
	* were a linear sequence (i.e. using API methods such as appendItem 
	* and removeAllCellsAndHeadersAndFooterCells).
	*/
	public		void		setHasFixedTotalNumberOfRows(boolean flag)
		{
		fixedTotalNumberOfRows = flag;
		}
	public		boolean		getHasFixedTotalNumberOfRows()
		{
		return(fixedTotalNumberOfRows);
		}
	/**
	* Specifies whether or not there are a fixed number of columns. This is
	* useful if the caller just wants to add and remove items as if they
	* were a linear sequence (i.e. using API methods such as appendItem 
	* and removeAllCellsAndHeadersAndFooterCells).
	*/
	public		void		setHasFixedTotalNumberOfColumns(boolean flag)
		{
		fixedTotalNumberOfColumns = flag;
		}
	public		boolean		getHasFixedTotalNumberOfColumns()
		{
		return(fixedTotalNumberOfColumns);
		}

	public		int		getNumberOfVisibleRows()
		{
		return(numberOfVisibleRows);
		}

	public		int		getNumberOfVisibleColumns()
		{
		return(numberOfVisibleColumns);
		}
	public		void		setMinimumRowHeight(MiDistance height)
		{
		minimumRowHeight = height;
		}
	public		MiDistance	getMinimumRowHeight()
		{
		return(minimumRowHeight);
		}
	public		void		setMinimumColumnWidth(MiDistance width)
		{
		minimumColumnWidth = width;
		}
	public		MiDistance	getMinimumColumnWidth()
		{
		return(minimumColumnWidth);
		}
	public		void		setRowHeight(int rowNumber, MiDistance height)
		{
		MiTableCell cell = getRowDefaults(rowNumber);
		if ((cell != null) && (cell.numberOfRows == 1))
			{
			cell.fixedHeight = height;
			}
		else
			{
			cell = new MiTableCell(this, rowNumber, 0);
			cell.fixedHeight = height;
			rowDefaults.insertElementAt(cell, 0);
			}
		invalidateLayout();
		}
	public		void		setColumnWidth(int columnNumber, MiDistance width)
		{
		MiTableCell cell = getColumnDefaults(columnNumber);
		if ((cell != null) && (cell.numberOfColumns == 1))
			{
			cell.fixedWidth = width;
			}
		else
			{
			cell = new MiTableCell(this, 0, columnNumber);
			cell.fixedWidth = width;
			columnDefaults.insertElementAt(cell, 0);
			}
		invalidateLayout();
		}
	public		MiDistance	getColumnWidth(int columnNumber)
		{
		MiTableCell cell = getColumnDefaults(columnNumber);
		if ((cell != null) && (cell.numberOfColumns == 1))
			{
			return(cell.fixedWidth);
			}
		return(-1);
		}
	// -------------------------------------------------------
	//	Adding and Removing Cells
	// -------------------------------------------------------
	public		void		insertRow(MiTableCells cells, int rowNumber)
		{
		tableSelectionManager.invalidateCellLocations();
		if (rowNumber < totalNumberOfRows)
			{
			++totalNumberOfRows;
			numberOfLoadedRows = totalNumberOfRows;
			}

		for (int i = 0; i < contentsCells.size(); ++i)
			{
			if (contentsCells.elementAt(i).rowNumber >= rowNumber)
				contentsCells.elementAt(i).rowNumber += 1;
			}
		for (int i = 0; i < rowHeaderCells.size(); ++i)
			{
			if (rowHeaderCells.elementAt(i).rowNumber >= rowNumber)
				rowHeaderCells.elementAt(i).rowNumber += 1;
			}
		for (int i = 0; i < rowFooterCells.size(); ++i)
			{
			if (rowFooterCells.elementAt(i).rowNumber >= rowNumber)
				rowFooterCells.elementAt(i).rowNumber += 1;
			}
		for (int i = 0; i < cells.size(); ++i)
			{
			cells.elementAt(i).rowNumber = rowNumber;
			addCell(cells.elementAt(i));
			}

		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}
	public		void		insertColumn(MiTableCells cells, int columnNumber)
		{
		tableSelectionManager.invalidateCellLocations();
		if (columnNumber < totalNumberOfColumns)
			{
			++totalNumberOfColumns;
			numberOfLoadedColumns = totalNumberOfColumns;
			}

		for (int i = 0; i < contentsCells.size(); ++i)
			{
			if (contentsCells.elementAt(i).columnNumber >= columnNumber)
				contentsCells.elementAt(i).columnNumber += 1;
			}
		for (int i = 0; i < columnHeaderCells.size(); ++i)
			{
			if (columnHeaderCells.elementAt(i).columnNumber >= columnNumber)
				columnHeaderCells.elementAt(i).columnNumber += 1;
			}
		for (int i = 0; i < columnFooterCells.size(); ++i)
			{
			if (columnFooterCells.elementAt(i).columnNumber >= columnNumber)
				columnFooterCells.elementAt(i).columnNumber += 1;
			}
		for (int i = 0; i < cells.size(); ++i)
			{
			cells.elementAt(i).columnNumber = columnNumber;
			addCell(cells.elementAt(i));
			}
		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}
	public		void		appendRow(MiTableCells cells)
		{
		int newRowNumber = totalNumberOfRows;
		for (int i = 0; i < cells.size(); ++i)
			{
			cells.elementAt(i).rowNumber = newRowNumber;
			addCell(cells.elementAt(i));
			}
		invalidateLayout();
		}
	public		void		appendColumn(MiTableCells cells)
		{
		int newColumnNumber = totalNumberOfColumns;
		for (int i = 0; i < cells.size(); ++i)
			{
			cells.elementAt(i).columnNumber = newColumnNumber;
			addCell(cells.elementAt(i));
			}
		invalidateLayout();
		}

	public		void		removeAllCells()
		{
		tableSelectionManager.invalidateCellLocations();
		removeGraphicsOfCells(contentsCells);
		contentsCells.removeAllElements();

		numberOfLoadedRows = 0;
		numberOfLoadedColumns = 0;

		for (int i = 0; i < rowHeaderCells.size(); ++i)
			{
			numberOfLoadedColumns = Math.max(numberOfLoadedColumns, rowHeaderCells.elementAt(i).getColumnNumber() + 1);
			}
		for (int i = 0; i < rowFooterCells.size(); ++i)
			{
			numberOfLoadedColumns = Math.max(numberOfLoadedColumns, rowFooterCells.elementAt(i).getColumnNumber() + 1);
			}

		for (int i = 0; i < columnHeaderCells.size(); ++i)
			{
			numberOfLoadedRows = Math.max(numberOfLoadedRows, columnHeaderCells.elementAt(i).getRowNumber() + 1);
			}
		for (int i = 0; i < columnFooterCells.size(); ++i)
			{
			numberOfLoadedRows = Math.max(numberOfLoadedRows, columnFooterCells.elementAt(i).getRowNumber() + 1);
			}

		if (!fixedTotalNumberOfRows)
			totalNumberOfRows = 0;
		if (!fixedTotalNumberOfColumns)
			totalNumberOfColumns = 0;

		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}

	public		void		removeAllCellsAndHeadersAndFooterCells()
		{
		removeAllRowHeaders();
		removeAllRowFooters();
		removeAllColumnHeaders();
		removeAllColumnFooters();
		removeAllCells();
		}

	public		void		removeAllRowHeaders()
		{
		removeGraphicsOfCells(rowHeaderCells);
		rowHeaderCells.removeAllElements();
		}
	public		void		removeAllRowFooters()
		{
		removeGraphicsOfCells(rowFooterCells);
		rowFooterCells.removeAllElements();
		}
	public		void		removeAllColumnHeaders()
		{
		removeGraphicsOfCells(columnHeaderCells);
		columnHeaderCells.removeAllElements();
		}
	public		void		removeAllColumnFooters()
		{
		removeGraphicsOfCells(columnFooterCells);
		columnFooterCells.removeAllElements();
		}
	protected	void		removeGraphicsOfCells(MiTableCells cells)
		{
		for (int i = 0; i < cells.size(); ++i)
			{
			if (cells.elementAt(i).getGraphics() != null)
				cells.elementAt(i).getGraphics().removeSelf();
			}
		}

	public		void		replaceRow(MiTableCells cells, int rowNumber)
		{
		removeRow(rowNumber);
		insertRow(cells, rowNumber);
		}
	public		void		replaceColumn(MiTableCells cells, int columnNumber)
		{
		removeColumn(columnNumber);
		insertColumn(cells, columnNumber);
		}
	public		void		setRowVisibility(int rowNumber, boolean isVisible)
		{
		getMadeRowDefaults(rowNumber).setVisible(isVisible);
		invalidateLayout();
		}
	public		void		setColumnVisibility(int columnNumber, boolean isVisible)
		{
		getMadeColumnDefaults(columnNumber).setVisible(isVisible);
		invalidateLayout();
		}
	public		MiTableCells	getRowCells(int rowNumber)
		{
		MiTableCells cells = new MiTableCells(this);
		for (int i = contentsCells.size() - 1; i >= 0; --i)
			{
			if (contentsCells.elementAt(i).rowNumber == rowNumber)
				cells.addElement(contentsCells.elementAt(i));
			}
		for (int i = columnHeaderCells.size() - 1; i >= 0; --i)
			{
			if (columnHeaderCells.elementAt(i).rowNumber == rowNumber)
				cells.addElement(columnHeaderCells.elementAt(i));
			}
		for (int i = columnFooterCells.size() - 1; i >= 0; --i)
			{
			if (columnFooterCells.elementAt(i).rowNumber == rowNumber)
				cells.addElement(columnFooterCells.elementAt(i));
			}
		return(cells);
		}
	public		MiTableCells	getColumnCells(int columnNumber)
		{
		MiTableCells cells = new MiTableCells(this);
		for (int i = contentsCells.size() - 1; i >= 0; --i)
			{
			if (contentsCells.elementAt(i).columnNumber == columnNumber)
				cells.addElement(contentsCells.elementAt(i));
			}
		for (int i = rowHeaderCells.size() - 1; i >= 0; --i)
			{
			if (rowHeaderCells.elementAt(i).columnNumber == columnNumber)
				cells.addElement(rowHeaderCells.elementAt(i));
			}
		for (int i = rowFooterCells.size() - 1; i >= 0; --i)
			{
			if (rowFooterCells.elementAt(i).columnNumber == columnNumber)
				cells.addElement(rowFooterCells.elementAt(i));
			}
		return(cells);
		}
	public		void		removeRow(int rowNumber)
		{
		tableSelectionManager.invalidateCellLocations();
		for (int i = contentsCells.size() - 1; i >= 0; --i)
			{
			if (contentsCells.elementAt(i).rowNumber == rowNumber)
				{
				if (contentsCells.elementAt(i).getGraphics() != null)
					contentsContainer.removePart(contentsCells.elementAt(i).getGraphics());
				contentsCells.removeElementAt(i);
				}
			else if (contentsCells.elementAt(i).rowNumber > rowNumber)
				{
				--contentsCells.elementAt(i).rowNumber;
				}
			}
		for (int i = columnHeaderCells.size() - 1; i >= 0; --i)
			{
			if (columnHeaderCells.elementAt(i).rowNumber == rowNumber)
				{
				//if (columnHeaderCells.elementAt(i).getGraphics() != null)
					//columnHeaderContainer.removePart(columnHeaderCells.elementAt(i).getGraphics());
				columnHeaderCells.removeElementAt(i);
				}
			else if (columnHeaderCells.elementAt(i).rowNumber > rowNumber)
				{
				--columnHeaderCells.elementAt(i).rowNumber;
				}
			}
		for (int i = columnFooterCells.size() - 1; i >= 0; --i)
			{
			if (columnFooterCells.elementAt(i).rowNumber == rowNumber)
				{
				if (columnFooterCells.elementAt(i).getGraphics() != null)
					columnFooterContainer.removePart(columnFooterCells.elementAt(i).getGraphics());
				columnFooterCells.removeElementAt(i);
				}
			else if (columnFooterCells.elementAt(i).rowNumber > rowNumber)
				{
				--columnFooterCells.elementAt(i).rowNumber;
				}
			}
		if (rowNumber < totalNumberOfRows)
			{
			--totalNumberOfRows;
			--numberOfLoadedRows;
			}
		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}
	public		void		removeColumn(int columnNumber)
		{
		tableSelectionManager.invalidateCellLocations();
		for (int i = contentsCells.size() - 1; i >= 0; --i)
			{
			if (contentsCells.elementAt(i).columnNumber == columnNumber)
				{
				if (contentsCells.elementAt(i).getGraphics() != null)
					contentsContainer.removePart(contentsCells.elementAt(i).getGraphics());
				contentsCells.removeElementAt(i);
				}
			else if (contentsCells.elementAt(i).columnNumber > columnNumber)
				{
				--contentsCells.elementAt(i).columnNumber;
				}
			}
		for (int i = rowHeaderCells.size() - 1; i >= 0; --i)
			{
			if (rowHeaderCells.elementAt(i).columnNumber == columnNumber)
				{
				if (rowHeaderCells.elementAt(i).getGraphics() != null)
					rowHeaderContainer.removePart(rowHeaderCells.elementAt(i).getGraphics());
				rowHeaderCells.removeElementAt(i);
				}
			else if (rowHeaderCells.elementAt(i).columnNumber > columnNumber)
				{
				--rowHeaderCells.elementAt(i).columnNumber;
				}
			}
		for (int i = rowFooterCells.size() - 1; i >= 0; --i)
			{
			if (rowFooterCells.elementAt(i).columnNumber == columnNumber)
				{
				if (rowFooterCells.elementAt(i).getGraphics() != null)
					rowFooterContainer.removePart(rowFooterCells.elementAt(i).getGraphics());
				rowFooterCells.removeElementAt(i);
				}
			else if (rowFooterCells.elementAt(i).columnNumber > columnNumber)
				{
				--rowFooterCells.elementAt(i).columnNumber;
				}
			}
		if (columnNumber < totalNumberOfColumns)
			{
			--totalNumberOfColumns;
			--numberOfLoadedColumns;
			}
		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}

	public		MiTableCell	addCell(int rowNumber, int columnNumber, MiPart part)
		{
		MiTableCell cell = getCell(rowNumber, columnNumber);
//if (cell != null)
		if (cell == null)
			{
			cell = addCell(rowNumber, columnNumber);
			}
		cell.setGraphics(part);
		invalidateLayout();
		return(cell);
		}
	public		MiTableCell	addCell(int rowNumber, int columnNumber, String value)
		{
		MiTableCell cell = getCell(rowNumber, columnNumber);
		if (cell == null)
			{
			cell = addCell(rowNumber, columnNumber);
			}
		cell.setValue(value);
		invalidateLayout();
		return(cell);
		}

	public		MiTableCell	getCell(int rowNumber, int columnNumber)
		{
		MiTableCells cells = getAppropriateListOfCells(rowNumber, columnNumber);
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			if (((cell.rowNumber <= rowNumber) 
				&& (cell.rowNumber + cell.numberOfRows  - 1 >= rowNumber))
			&& ((cell.columnNumber <= columnNumber) 
				&& (cell.columnNumber + cell.numberOfColumns - 1  >= columnNumber)))
				{
				return(cell);
				}
			}
		return(null);
		}
	public		MiTableCells	getContentsCells()
		{
		return(contentsCells);
		}
	public		void		addCell(MiTableCell cell)
		{
//MiDebug.println(this + "addCell: " +cell);
		tableSelectionManager.invalidateCellLocations();

		int rowNumber = cell.rowNumber;
		int columnNumber = cell.columnNumber;

		if ((cell.getGraphics() != null) && (cell.getGraphics().getContainer(0) == null))
			{
			getAppropriateContainer(rowNumber, columnNumber).appendPart(cell.getGraphics());
			}

		MiTableCells cells = getAppropriateListOfCells(rowNumber, columnNumber);
		cells.addElement(cell);

		if (rowNumber + 1 > totalNumberOfRows)
			{
			totalNumberOfRows = rowNumber + 1;
			}
		if (columnNumber + 1 > totalNumberOfColumns)
			{
			totalNumberOfColumns = columnNumber + 1;
			}

		numberOfLoadedRows = totalNumberOfRows;
		numberOfLoadedColumns = totalNumberOfColumns;

		createCellsToFillInAnyGapsCreatedByNewCell();
		removeNodesThatAreNowOverlappingBecauseOfNewCell(cell);

		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		if (isVisibleCell(rowNumber, columnNumber))
			invalidatePreferredSize();
		}
	/**
	 * For speed
	 **/
	public		void		appendNonOverlappingContentsCells(MiTableCells cells)
		{
		//removeCellsAddedToFillInAnyGapsCreatedByNewCell();

		boolean visibleCells = false;
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			int rowNumber = cell.rowNumber;
			int columnNumber = cell.columnNumber;
			contentsCells.addElement(cell);

			// If not just text but requires interactivity, we have to add graphics to table
			if ((cell.getGraphics() != null) && (cell.getGraphics().getContainer(0) == null)
				&& (cell.getGraphics() instanceof MiWidget))
				{
				contentsContainer.appendPart(cell.getGraphics());
				}
			if (rowNumber + 1 > totalNumberOfRows)
				{
				totalNumberOfRows = rowNumber + 1;
				}
			if (columnNumber + 1 > totalNumberOfColumns)
				{
				totalNumberOfColumns = columnNumber + 1;
				}
			visibleCells = visibleCells || isVisibleCell(rowNumber, columnNumber);
			}

		numberOfLoadedRows = totalNumberOfRows;
		numberOfLoadedColumns = totalNumberOfColumns;

		// faster createCellsToFillInAnyGapsCreatedByNewCell();

		invalidateLayout();
		if (visibleCells)
			{
			invalidatePreferredSize();
			}
		}
					// Invalidate all of the preferred sizes of all
					// containers, cause not only the layout of this
					// part needs recalculating, but the preferred size
					// has changed too.
	protected	void		invalidatePreferredSize()
		{
		invalidateLayout();
/*
		MiPart container = this;
		while (container.getNumberOfContainers() > 0)
			{
			container = container.getContainer(0);
			if (container instanceof MiEditor)
				return;
			container.invalidateLayout();
			}
*/
		}
	public		boolean		isVisibleCell(int rowNumber, int columnNumber)
		{
		// If the table is not displayed yet, then assume that the preferred
		// number of columns and rows will be visible.
		if ((numberOfVisibleRows == 0) || (numberOfVisibleColumns == 0))
			{
			return((rowNumber >= topVisibleRow) 
				&& (rowNumber < topVisibleRow + prefNumberOfVisibleRows)
				&& (columnNumber >= leftVisibleColumn) 
				&& (columnNumber < leftVisibleColumn + prefNumberOfVisibleColumns));
			}
		else
			{
			return((rowNumber >= topVisibleRow) 
				&& (rowNumber < topVisibleRow + numberOfVisibleRows)
				&& (columnNumber >= leftVisibleColumn) 
				&& (columnNumber < leftVisibleColumn + numberOfVisibleColumns));
			}
		}
	protected	MiTableCell	addCell(int rowNumber, int columnNumber)
		{
		MiTableCell cell = new MiTableCell(this, rowNumber, columnNumber);
		addCell(cell);
		return(cell);
		}
	
	public		String		getSelectedItem()
		{
		MiTableCell cell = tableSelectionManager.getSelectedCell();
		if (cell != null)
			return(cell.getValue());
		return(null);
		}
	public		int		getIndexOfItem(String value)
		{
		return(getIndexOfItem(value, false));
		}
	public		int		getIndexOfItem(String value, boolean ignoreCase)
		{
		for (int i = 0; i < contentsCells.size(); ++i)
			{
			String cellValue = contentsCells.elementAt(i).getValue();
			if (ignoreCase)
				{
				if (value.equalsIgnoreCase(cellValue))
					return(i);
				}
			else
				{
				if (value.equals(cellValue))
					return(i);
				}
			}
		return(-1);
		}
	public		int		getNumberOfItems()
		{
		return(contentsCells.size());
		}
	public		MiPart		getItem(int index)
		{
		MiPart part = contentsCells.elementAt(index).getGraphics();
		if (part == null)
			{
			part = new MiText(contentsCells.elementAt(index).getValue());
			}
		return(part);
		}
	public		void		appendItem(String text)
		{
		appendItem(text, null);
		}
	public		void		appendItem(MiPart graphics)
		{
		appendItem(null, graphics);
		}
	public		void		setContents(Strings strings)
		{
		removeAllItems();
		appendItems(null, strings);
		}
	public		void		removeAllItems()
		{
		removeAllCells();
		}
	public		void		appendItems(Strings strings)
		{
		appendItems(null, strings);
		}
	public		void		appendItems(MiParts parts)
		{
		appendItems(parts, null);
		}
	protected	void		appendItems(MiParts parts, Strings strings)
		{
		MiTableCells cells = new MiTableCells(this);
		MiTableCell cell;

		// Remove padding cells that filled in the gaps to make a complete last row...
		for (int i = contentsCells.size() - 1; i >= 0; --i)
			{
			if (contentsCells.elementAt(i).isEmpty())
				{
				contentsCells.removeElementAt(i);
				}
			}
		MiTableCell lastCell = contentsCells.getLastCell();
		int lastRow;
		int lastColumn;
		if (lastCell == null)
			{
			if (fixedTotalNumberOfColumns)
				lastRow = 0;
			else
				lastRow = -1;
			if (fixedTotalNumberOfRows)
				lastColumn = 0;
			else
				lastColumn = -1;
			}
		else
			{
			lastRow = lastCell.rowNumber;
			lastColumn = lastCell.columnNumber;
			}
		int size;
		if (parts != null)
			size = parts.size();
		else
			size = strings.size();

		if (fixedTotalNumberOfColumns && (totalNumberOfColumns == 0))
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
				+ ": getTotalNumberOfColumns() must be > 0 if\n"
				+ "getHasFixedTotalNumberOfColumns() is true");
			}

		if (fixedTotalNumberOfRows && (totalNumberOfRows == 0))
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
				+ ": getTotalNumberOfRows() must be > 0 if\n"
				+ "getHasFixedTotalNumberOfRows() is true");
			}

		for (int i = 0; i < size; ++i)
			{
			if (fixedTotalNumberOfColumns)
				{
				if (lastColumn + 1 >= totalNumberOfColumns)
					{
					++lastRow;
					lastColumn = -1;
					}
				++lastColumn;
				cell = new MiTableCell(this, lastRow, lastColumn);
				}
			else // if (fixedNumberOfRows > 0)
				{
				if (lastRow + 1 >= totalNumberOfRows)
					{
					++lastColumn;
					lastRow = -1;
					}
				++lastRow;
				cell = new MiTableCell(this, lastRow, lastColumn);
				}

			if (parts != null)
				cell.setGraphics(parts.elementAt(i));
			else
				cell.setValue(strings.elementAt(i));
				//cell.setValue(MiSystem.getProperty(strings.elementAt(i),strings.elementAt(i)));
			cells.addElement(cell);
			}
		appendNonOverlappingContentsCells(cells);
		createCellsToFillInAnyGapsCreatedByNewCell();
		}
	protected	void		appendItem(String text, MiPart graphics)
		{
		MiTableCell cell;
		MiTableCell lastCell = contentsCells.getLastCell();

		if (lastCell == null)
			{
			cell = new MiTableCell(this, 0, 0);
			}
		else if (fixedTotalNumberOfColumns)
			{
			if (lastCell.columnNumber + 1 >= totalNumberOfColumns)
				cell = new MiTableCell(this, lastCell.rowNumber + 1, 0);
			else
				cell = new MiTableCell(this, lastCell.rowNumber, lastCell.columnNumber + 1);
			}
		else // if (fixedNumberOfRows > 0)
			{
			if (lastCell.rowNumber + 1 >= totalNumberOfRows)
				cell = new MiTableCell(this, 0, lastCell.columnNumber + 1);
			else
				cell = new MiTableCell(this, lastCell.rowNumber + 1, lastCell.columnNumber);
			}

		if (text != null)
			cell.setValue(text);
		else
			cell.setGraphics(graphics);

		addCell(cell);
		}
	/**
	 * Can be called in conjunction with (after) appendNonOverlappingContentsCells for slower but more flexible
	 * and still fairly fast adding of cells
	 **/
	public		void		createCellsToFillInAnyGapsCreatedByNewCell()
		{
		if (fillingInCellGaps)
			return;
		fillingInCellGaps = true;
		if (hasRowHeaders())
			{
			for (int columnNumber = 0; columnNumber < totalNumberOfColumns; ++columnNumber)
				{
				if (getCell(ROW_HEADER_NUMBER, columnNumber) == null)
					addCell(ROW_HEADER_NUMBER, columnNumber);
				}
			}
		if (hasRowFooters())
			{
			for (int columnNumber = 0; columnNumber < totalNumberOfColumns; ++columnNumber)
				{
				if (getCell(ROW_FOOTER_NUMBER, columnNumber) == null)
					addCell(ROW_FOOTER_NUMBER, columnNumber);
				}
			}
		if (hasColumnHeaders())
			{
			for (int rowNumber = 0; rowNumber < totalNumberOfRows; ++rowNumber)
				{
				if (getCell(rowNumber, COLUMN_HEADER_NUMBER) == null)
					addCell(rowNumber, COLUMN_HEADER_NUMBER);
				}
			}
		if (hasColumnFooters())
			{
			for (int rowNumber = 0; rowNumber < totalNumberOfRows; ++rowNumber)
				{
				if (getCell(rowNumber, COLUMN_FOOTER_NUMBER) == null)
					addCell(rowNumber, COLUMN_FOOTER_NUMBER);
				}
			}
		for (int rowNumber = 0; rowNumber < totalNumberOfRows; ++rowNumber)
			{
			for (int columnNumber = 0; columnNumber < totalNumberOfColumns; ++columnNumber)
				{
				if (getCell(rowNumber, columnNumber) == null)
					addCell(rowNumber, columnNumber);
				}
			}
		fillingInCellGaps = false;
		}
	/**
	 * Can be called in conjunction with (before) appendNonOverlappingContentsCells for slower but more flexible
	 * and still fairly fast adding of cells
	 **/
	public		void		removeCellsAddedToFillInAnyGapsCreatedByNewCell()
		{
		// Remove padding cells that filled in the gaps to make a complete last row...
		for (int i = contentsCells.size() - 1; i >= 0; --i)
			{
			if (contentsCells.elementAt(i).getGraphics() == null)
				{
				contentsCells.removeElementAt(i);
				}
			}
		}
	protected	void		removeNodesThatAreNowOverlappingBecauseOfNewCell(MiTableCell newCell)
		{
		removeNodesOverlappingCell(rowHeaderCells, newCell);
		removeNodesOverlappingCell(rowFooterCells, newCell);
		removeNodesOverlappingCell(columnHeaderCells, newCell);
		removeNodesOverlappingCell(columnFooterCells, newCell);
		removeNodesOverlappingCell(contentsCells, newCell);
		}
	protected	void		removeNodesOverlappingCell(MiTableCells cells, MiTableCell cell)
		{
		removeNodesStartingInRange(
			cells,
			cell,
			cell.rowNumber,
			cell.numberOfRows,
			cell.columnNumber,
			cell.numberOfColumns);
		}
	protected	void		removeNodesStartingInRange(
						MiTableCells cells,
						MiTableCell ignore,
						int startRowNumber, int numberOfRows, 
						int startColumnNumber, int numberOfColumns)
		{
		tableSelectionManager.invalidateCellLocations();
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			if ((cell != ignore)
				&& (cell.rowNumber >= startRowNumber) 
				&& (cell.rowNumber < startRowNumber + numberOfRows)
				&& (cell.columnNumber >= startColumnNumber) 
				&& (cell.columnNumber < startColumnNumber + numberOfColumns))
				{
				cells.removeElementAt(i);
				--i;
				}
			}
		tableSelectionManager.validateCellLocations();
		}
	protected	MiTableCells	getAppropriateListOfCells(int rowNumber, int columnNumber)
		{
		MiTableCells cells;

		if (rowNumber == ROW_HEADER_NUMBER)
			cells = rowHeaderCells;
		else if (rowNumber == ROW_FOOTER_NUMBER)
			cells = rowFooterCells;
		else if (columnNumber == COLUMN_HEADER_NUMBER)
			cells = columnHeaderCells;
		else if (columnNumber == COLUMN_FOOTER_NUMBER)
			cells = columnFooterCells;
		else
			cells = contentsCells;
		return(cells);
		}

	protected	MiContainer	getAppropriateContainer(int rowNumber, int columnNumber)
		{
		MiContainer container;

		if (rowNumber == ROW_HEADER_NUMBER)
			container = rowHeaderContainer;
		else if (rowNumber == ROW_FOOTER_NUMBER)
			container = rowFooterContainer;
		else if (columnNumber == COLUMN_HEADER_NUMBER)
			container = columnHeaderContainer;
		else if (columnNumber == COLUMN_FOOTER_NUMBER)
			container = columnFooterContainer;
		else
			container = contentsContainer;

		return(container);
		}


	public		MiTableCell	getTableWideDefaults()
		{
		return(tableWideCellDefaults);
		}
	public		void		setTableWideDefaults(MiTableCell cell)
		{
		tableWideCellDefaults = cell;
		}

	public		boolean		hasRowHeaders()
		{
		return(rowHeaderCells.size() > 0);
		}
	public		boolean		hasRowFooters()
		{
		return(rowFooterCells.size() > 0);
		}
	public		boolean		hasColumnHeaders()
		{
		return(columnHeaderCells.size() > 0);
		}
	public		boolean		hasColumnFooters()
		{
		return(columnFooterCells.size() > 0);
		}

	public		MiTableCell	getMadeRowDefaults(int rowNumber)
		{
		MiTableCell cell = getRowDefaults(rowNumber);
		if (cell == null)
			{
			cell = new MiTableCell(this, rowNumber, 0);
			rowDefaults.addElement(cell);
			}
		return(cell);
		}
	public		MiTableCell	getMadeColumnDefaults(int columnNumber)
		{
		MiTableCell cell = getColumnDefaults(columnNumber);
		if (cell == null)
			{
			cell = new MiTableCell(this, 0, columnNumber);
			columnDefaults.addElement(cell);
			}
		return(cell);
		}
	public		void		setContentAreaDefaults(MiTableCell cell)
		{
		tableContentAreaDefaults = cell; 
		invalidateLayout();
		}
	public		MiTableCell	getContentAreaDefaults()
		{
		return(tableContentAreaDefaults);
		}
	public		void		setRowDefaults(MiTableCell cell)
		{
		MiTableCell defaultsCell = getRowDefaults(cell.rowNumber);
		if ((defaultsCell != null) && (defaultsCell.numberOfRows == cell.numberOfRows))
			rowDefaults.removeElement(defaultsCell);
		rowDefaults.insertElementAt(cell, 0);
		}
	public		MiTableCell	getRowDefaults(int rowNumber)
		{
		for (int i = 0; i < rowDefaults.size(); ++i)
			{
			MiTableCell cell = rowDefaults.elementAt(i);
			if ((cell.rowNumber <= rowNumber) 
				&& (cell.rowNumber + cell.numberOfRows - 1 >= rowNumber))
				{
				return(cell);
				}
			}
		return(null);
		}

	public		void		setColumnDefaults(MiTableCell cell)
		{
		MiTableCell defaultsCell = getColumnDefaults(cell.columnNumber);
		if ((defaultsCell != null) && (defaultsCell.numberOfColumns == cell.numberOfColumns))
			columnDefaults.removeElement(defaultsCell);
		columnDefaults.insertElementAt(cell, 0);
		}
	public		MiTableCell	getColumnDefaults(int columnNumber)
		{
		for (int i = 0; i < columnDefaults.size(); ++i)
			{
			MiTableCell cell = columnDefaults.elementAt(i);
			if ((cell.columnNumber <= columnNumber) 
				&& (cell.columnNumber + cell.numberOfColumns  - 1 >= columnNumber))
				{
				return(cell);
				}
			}
		return(null);
		}


	public		void		setMinimumNumberOfVisibleRows(int number)
		{
		minNumberOfVisibleRows = number;
		if (number > maxNumberOfVisibleRows)
			maxNumberOfVisibleRows = number;
		if (number > prefNumberOfVisibleRows)
			prefNumberOfVisibleRows = number;
		invalidateLayout();
		}
	public		int		getMinimumNumberOfVisibleRows()
		{
		return(minNumberOfVisibleRows);
		}

	public		void		setPreferredNumberOfVisibleRows(int number)
		{
		prefNumberOfVisibleRows = number;
		if (number > maxNumberOfVisibleRows)
			maxNumberOfVisibleRows = number;
		if (number < minNumberOfVisibleRows)
			{
			if (number < 0)
				minNumberOfVisibleRows = 1;
			else
				minNumberOfVisibleRows = number;
			}
		invalidateLayout();
		invalidatePreferredSize();
		}
	public		int		getPreferredNumberOfVisibleRows()
		{
		return(prefNumberOfVisibleRows);
		}

	public		void		setMaximumNumberOfVisibleRows(int number)
		{
		maxNumberOfVisibleRows = number;
		if (number > 0)
			{
			if (number < minNumberOfVisibleRows)
				minNumberOfVisibleRows = number;
			if (number < prefNumberOfVisibleRows)
				prefNumberOfVisibleRows = number;
			}
		invalidateLayout();
		invalidatePreferredSize();
		}
	public		int		getMaximumNumberOfVisibleRows()
		{
		return(maxNumberOfVisibleRows);
		}



	public		void		setMinimumNumberOfVisibleColumns(int number)
		{
		minNumberOfVisibleColumns = number;
		if (number > maxNumberOfVisibleColumns)
			maxNumberOfVisibleColumns = number;
		if (number > prefNumberOfVisibleColumns)
			prefNumberOfVisibleColumns = number;
		}
	public		int		getMinimumNumberOfVisibleColumns()
		{
		return(minNumberOfVisibleColumns);
		}

	public		void		setPreferredNumberOfVisibleColumns(int number)
		{
		prefNumberOfVisibleColumns = number;
		if (number > maxNumberOfVisibleColumns)
			maxNumberOfVisibleColumns = number;
		if (number < minNumberOfVisibleColumns)
			minNumberOfVisibleColumns = number;
		invalidateLayout();
		invalidatePreferredSize();
		}
	public		int		getPreferredNumberOfVisibleColumns()
		{
		return(prefNumberOfVisibleColumns);
		}

	public		void		setMaximumNumberOfVisibleColumns(int number)
		{
		maxNumberOfVisibleColumns = number;
		if (number < minNumberOfVisibleColumns)
			minNumberOfVisibleColumns = number;
		if (number < prefNumberOfVisibleColumns)
			prefNumberOfVisibleColumns = number;
		invalidateLayout();
		invalidatePreferredSize();
		}
	public		int		getMaximumNumberOfVisibleColumns()
		{
		return(maxNumberOfVisibleColumns);
		}

	public		void		setHorizontalScrollAlignmentPolicy(int policy)
		{
		hAlignmentPolicy = policy;
		}
	public		int		getHorizontalScrollAlignmentPolicy()
		{
		return(hAlignmentPolicy);
		}

	public		void		setVerticalScrollAlignmentPolicy(int policy)
		{
		vAlignmentPolicy = policy;
		}
	public		int		getVerticalScrollAlignmentPolicy()
		{
		return(vAlignmentPolicy);
		}

	public		MiTableCell	pickCell(MiBounds pickArea)
		{
		return(pickCell(contentsCells, pickArea));
		}
	public		MiTableCell	pickCellIncludingHeadersAndFooters(MiBounds pickArea)
		{
		MiTableCell cell = pickCell(contentsCells, pickArea);
		if (cell != null)
			return(cell);
		cell = pickCell(rowHeaderCells, pickArea);
		if (cell != null)
			return(cell);
		cell = pickCell(rowFooterCells, pickArea);
		if (cell != null)
			return(cell);
		cell = pickCell(columnHeaderCells, pickArea);
		if (cell != null)
			return(cell);
		cell = pickCell(columnFooterCells, pickArea);
		return(cell);
		}

	public static	MiTable		generateTableFromHTML(MiiModelEntity html)
		{
		MiModelUtilities.modifyTreeOfModelEntitiesPropertyNamesToLowerCase(html);
		MiModelUtilities.modifyTreeOfModelEntitiesPropertiesToTypes(html, "tr", "value");
		MiModelUtilities.modifyTreeOfModelEntitiesPropertiesToTypes(html, "td", "value");
		MiModelUtilities.modifyTreeOfModelEntitiesPropertiesToTypes(html, "th", "value");

		MiTable table = new MiTable();
		table.getTableWideDefaults().setHorizontalJustification(Mi_LEFT_JUSTIFIED);
		table.getTableWideDefaults().setHorizontalSizing(Mi_SAME_SIZE);
		table.getTableWideDefaults().setVerticalSizing(Mi_SAME_SIZE);
		table.getSectionMargins().setMargins(0);
		//table.getTableWideDefaults().insetMargins.setMargins(4, 2, 4, 2);
		table.getTableWideDefaults().insetMargins.setMargins(0, 0, 0, 0);
		table.setAlleyHSpacing(0);
		table.setAlleyVSpacing(0);
		table.getMadeColumnDefaults(1).setColumnHorizontalSizing(Mi_EXPAND_TO_FILL);
		table.getSelectionManager().setMaximumNumberSelected(0);
		table.getSelectionManager().setBrowsable(false);
		table.contentsBackground.setBorderLook(Mi_NONE);
		table.setMargins(new MiMargins(0));
		table.setName("html-generated-table");

		String bgcolor = html.getPropertyValue("bgcolor");
		if (bgcolor != null)
			{
			table.setBackgroundColor(bgcolor);
			table.setBorderLook(Mi_FLAT_BORDER_LOOK);
			table.contentsBackground.setBackgroundColor(Mi_TRANSPARENT_COLOR);
			java.awt.Color bgc = MiColorManager.getColor(bgcolor);
			if ((bgc == null) || (bgc.getAlpha() != 255))
				{
				// 100% transparent or partially transparent
				table.setIsOpaqueRectangle(false);
				}
			}

		MiFont font = table.getFont();
		MiiModelEntity fontSpec = MiModelUtilities.searchChildrenForModelEntityWithType(html, "font");
		if (fontSpec != null)
			{
			font = getFontFromHTML(fontSpec, table, font);
//MiDebug.println("now table font = " + font);
			}

		int numberOfRows = 0;
		for (int i = 0; i < html.getNumberOfModelEntities(); ++i)
			{	
			MiiModelEntity rowSpec = html.getModelEntity(i);
			if ("TR".equalsIgnoreCase(rowSpec.getType().getName()))
				{
				int numberOfColumns = 0;
				MiTableCells cells = new MiTableCells(table);

				++numberOfRows;
				for (int j = 0; j < rowSpec.getNumberOfModelEntities(); ++j)
					{
					MiiModelEntity cellSpec = rowSpec.getModelEntity(j);
					if ((!"TH".equalsIgnoreCase(cellSpec.getType().getName()))
						&& (!"TD".equalsIgnoreCase(cellSpec.getType().getName())))
						{
						continue;
						}

//MiDebug.println("cellSpec = " + MiModelEntity.dump(cellSpec));

					MiTableCell cell = new MiTableCell(table, i, j);
					cell.setRowNumber(numberOfRows);
					cell.setColumnNumber(numberOfColumns);

					MiLabel label = new MiLabel();
					label.setFont(font);
					label.setBorderLook(Mi_FLAT_BORDER_LOOK);
					label.setValue("");
					label.setElementHJustification(Mi_LEFT_JUSTIFIED);
					label.getLabel().setFontHorizontalJustification(Mi_LEFT_JUSTIFIED);

					cell.setGraphics(label);

					String border = html.getPropertyValue("border");
					if (border != null)
						{
						label.setLineWidth(Utility.toInteger(border));
						}
					String value = cellSpec.getPropertyValue(Mi_RAW_XML_ELEMENT_CDATA);
					if (value != null)
						{
						label.setValue(value);
						}
					String colSpan = cellSpec.getPropertyValue("colspan");
					if (colSpan != null)
						{
						cell.setNumberOfColumns(Utility.toInteger(colSpan));
						numberOfColumns += cell.getNumberOfColumns() - 1;
						}
					String rowSpan = cellSpec.getPropertyValue("rowspan");
					if (rowSpan != null)
						{
						cell.setNumberOfRows(Utility.toInteger(rowSpan));
						}
					bgcolor = cellSpec.getPropertyValue("bgcolor");
					if (bgcolor != null)
						{
						label.setBackgroundColor(bgcolor);
						}
					if ("TH".equalsIgnoreCase(cellSpec.getType().getName()))
						{
						label.setFontBold(true);
						}
					if (cellSpec.getNumberOfModelEntities() > 0) 
						{
						if (cellSpec.getModelEntity(0).getType().getName().equals("img"))
							{
							label.setLabel(new MiImage(cellSpec.getModelEntity(0).getPropertyValue("src")));
							}
						else if (cellSpec.getModelEntity(0).getType().getName().equals("font"))
							{
							getFontFromHTML(cellSpec.getModelEntity(0), label, label.getFont());
							}
						}

//MiDebug.println("cell = " + cell);
					cells.addElement(cell);
					++numberOfColumns;
					}
//MiDebug.println("numberOfColumns = " + numberOfColumns);
				if ((numberOfColumns != 0) && (	table.getTotalNumberOfColumns() == 0))
					{
//MiDebug.println("SETTING numberOfColumns = " + numberOfColumns);
					table.setTotalNumberOfColumns(numberOfColumns);
					table.setHasFixedTotalNumberOfColumns(true);
					table.setMinimumNumberOfVisibleColumns(numberOfColumns);
					table.setMaximumNumberOfVisibleColumns(numberOfColumns);
					}
				table.appendRow(cells);
				}
			}
//MiDebug.println("SETTING numberOfRows = " + numberOfRows);
		table.setMinimumNumberOfVisibleRows(numberOfRows);
		table.setMaximumNumberOfVisibleRows(numberOfRows);
		table.setPreferredNumberOfVisibleRows(numberOfRows);
		table.setSize(table.getPreferredSize(new MiSize()));
		table.validateLayout();

		return(table);
		}
	protected static MiFont 	getFontFromHTML(MiiModelEntity html, MiWidget label, MiFont defaultFont)
		{
/*
		MiiModelEntity fontSpec = MiModelUtilities.searchChildrenForModelEntityWithType(html, "font");
		if (fontSpec == null)
			{
			return(defaultFont);
			}
*/

		MiFont font = defaultFont;

//MiDebug.println("default font = " + font);
		String name = html.getPropertyValue("face");
		if (name != null)
			{
			font = font.setName(name);
			}

/*
		String color = html.getPropertyValue("color");
		if (color != null)
			{
			font = font.setColor(color);
			}
*/
			
		String pointSize = html.getPropertyValue("point-size");
		if (pointSize != null)
			{
			font = font.setPointSize(Utility.toInteger(pointSize));
			}
//MiDebug.println("now font = " + font);
			
		String size = html.getPropertyValue("size");
		if (size != null)
			{
			font = font.setPointSize(defaultFont.getPointSize() + Utility.toInteger(size));
			}
			
		String weight = html.getPropertyValue("weight");
		if (weight != null)
			{
			font = font.setBold(Utility.toInteger(weight) > 500 ? true : false);
			}
		String text = html.getPropertyValue(Mi_RAW_XML_ELEMENT_CDATA);
		if (text != null)
			{
			label.setValue(label.getValue() != null ? label.getValue() + text : text);
			}

		if ((html.getNumberOfModelEntities() == 1) && (html.getModelEntity(0).getType().getName().equalsIgnoreCase("Strong")))
			{
			font = getFontFromHTML(html.getModelEntity(0), label, font);
			font = font.setBold(true);
			}
		if ((html.getNumberOfModelEntities() == 1) && (html.getModelEntity(0).getType().getName().equalsIgnoreCase("em")))
			{
			font = getFontFromHTML(html.getModelEntity(0), label, font);
			font = font.setItalic(true);
			}

		label.setFont(font);
		if ((label instanceof MiLabel) && (((MiLabel )label).getLabel() != null))
			{
			((MiLabel )label).getLabel().setFont(font);
			}
			
//MiDebug.println("finally font = " + font);
		return(font);
		}


	protected	MiTableCell	pickCell(MiTableCells cells, MiBounds pickArea)
		{
		if (cells == contentsCells)
			{
			getContentsBounds(tmpBounds);
			if (!tmpBounds.intersectionWith(pickArea))
				return(null);
			}
		tmpBounds.copy(pickArea);

		MiDistance tx = 0;
		MiDistance ty = 0;
		if ((cells == contentsCells) || (cells == rowHeaderCells) || (cells == rowFooterCells))
			tx = getLeftVisibleColumnXOffset() + scrollX;
		if ((cells == contentsCells) || (cells == columnHeaderCells) || (cells == columnFooterCells))
			ty = getTopVisibleRowYOffset() - scrollY;
		tmpBounds.translate(tx, ty);

		//for (int i = 0; i < cells.size(); ++i)
		// Pick backwards, so top to bottom, to be compatible with pickDeepList
		for (int i = cells.size() - 1; i >= 0; --i)
			{
			MiTableCell cell = cells.elementAt(i);
//MiDebug.println("cell = " + cell);
//MiDebug.println("cell.bounds = " + cell.bounds);
			if (cell.isVisible() && (cell.bounds != null)
				&& tmpBounds.intersects(cell.bounds)) // && (cell.getGraphics() != null))
				{
//MiDebug.println("picked Cell = " + cell);
				return(cell);
				}
			}
		return(null);
		}
	public		boolean		pickCell(MiBounds pickArea, int[] rowNumber, int[] columnNumber)
		{
		MiTableCell cell = pickCell(pickArea);
		if (cell != null)
			{
			rowNumber[0] = cell.rowNumber;
			columnNumber[0] = cell.columnNumber;
			return(true);
			}
		return(false);
		}
	public		Strings		getRowValues(int rowNumber)
		{
		Strings values = new Strings();
		for (int i = leftLoadedColumn; i < numberOfLoadedColumns; ++i)
			{
			MiTableCell cell = getCell(rowNumber, i);
			values.addElement(cell.getValue());
			}
		return(values);
		}
	public		Strings		getColumnValues(int columnNumber)
		{
		Strings values = new Strings();
		for (int i = topLoadedRow; i < numberOfLoadedRows; ++i)
			{
			MiTableCell cell = getCell(i, columnNumber);
			values.addElement(cell.getValue());
			}
		return(values);
		}

	public		void		moveRow(int fromRowNumber, int toRowNumber)
		{
		tableSelectionManager.invalidateCellLocations();
		moveRow(contentsCells, fromRowNumber, toRowNumber);
		moveRow(columnHeaderCells, fromRowNumber, toRowNumber);
		moveRow(columnFooterCells, fromRowNumber, toRowNumber);
		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}
	protected	void		moveRow(MiTableCells cells, int fromRowNumber, int toRowNumber)
		{
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			if (cell.rowNumber == fromRowNumber)
				cell.rowNumber = toRowNumber;
			else if ((cell.rowNumber > fromRowNumber) && (cell.rowNumber <= toRowNumber))
				cell.rowNumber -= 1;
			else if ((cell.rowNumber < fromRowNumber) && (cell.rowNumber >= toRowNumber))
				cell.rowNumber += 1;
			}
		}
	public		void		moveColumn(int fromColumnNumber, int toColumnNumber)
		{
		tableSelectionManager.invalidateCellLocations();
		moveColumn(contentsCells, fromColumnNumber, toColumnNumber);
		moveColumn(rowHeaderCells, fromColumnNumber, toColumnNumber);
		moveColumn(rowFooterCells, fromColumnNumber, toColumnNumber);
		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}
	protected	void		moveColumn(MiTableCells cells, int fromColumnNumber, int toColumnNumber)
		{
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			if (cell.columnNumber == fromColumnNumber)
				{
				cell.columnNumber = toColumnNumber;
				}
			else if ((cell.columnNumber > fromColumnNumber) 
				&& (cell.columnNumber <= toColumnNumber))
				{
				cell.columnNumber -= 1;
				}
			else if ((cell.columnNumber < fromColumnNumber) 
				&& (cell.columnNumber >= toColumnNumber))
				{
				cell.columnNumber += 1;
				}
			}
		}
	public		void		switchRows(int oneRowNumber, int otherRowNumber)
		{
		tableSelectionManager.invalidateCellLocations();
		switchRows(contentsCells, oneRowNumber, otherRowNumber);
		switchRows(columnHeaderCells, oneRowNumber, otherRowNumber);
		switchRows(columnFooterCells, oneRowNumber, otherRowNumber);
		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}
	protected	void		switchRows(MiTableCells cells, int oneRowNumber, int otherRowNumber)
		{
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			if (cell.rowNumber == oneRowNumber)
				cell.rowNumber = otherRowNumber;
			else if (cell.rowNumber == otherRowNumber)
				cell.rowNumber = oneRowNumber;
			}
		}

	public		void		switchColumns(int oneColumnNumber, int otherColumnNumber)
		{
		tableSelectionManager.invalidateCellLocations();
		switchColumns(contentsCells, oneColumnNumber, otherColumnNumber);
		switchColumns(rowHeaderCells, oneColumnNumber, otherColumnNumber);
		switchColumns(rowFooterCells, oneColumnNumber, otherColumnNumber);
		tableSelectionManager.validateCellLocations();
		invalidateLayout();
		}
	public		void		switchColumns(MiTableCells cells, int oneColumnNumber, int otherColumnNumber)
		{
		for (int i = 0; i < cells.size(); ++i)
			{
			MiTableCell cell = cells.elementAt(i);
			if (cell.columnNumber == oneColumnNumber)
				cell.columnNumber = otherColumnNumber;
			else if (cell.columnNumber == otherColumnNumber)
				cell.columnNumber = oneColumnNumber;
			}
		}
	public		boolean		isSelectable(int rowNumber, int columnNumber)
		{
		return(getCell(rowNumber, columnNumber).isSelectable());
		}
					/**------------------------------------------------------
					 * Gets the context cursor assigned to the given point of
					 * this MiPart. If none, then returns the status help,
					 * if any, assigned to this MiPart as a whole.
					 * @param point		the location of interest
					 * @return		the status help
					 * @see			MiPart#setContextCursor
					 * @overrides		MiPart#getStatusHelp
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getStatusHelp(MiPoint point)	
		{
		if (point != null)
			{
			MiTableCell cell= pickCellIncludingHeadersAndFooters(new MiBounds(point));
			if (cell != null)
				{
				if (cell.getStatusHelp(point) != null)
					return(cell.getStatusHelp(point));
				int rowNumber = cell.getRowNumber();
				int columnNumber = cell.getColumnNumber();

				cell = getColumnDefaults(columnNumber);
				if ((cell != null) && (cell.getStatusHelp(point) != null))
					return(cell.getStatusHelp(point));

				cell = getRowDefaults(rowNumber);
				if ((cell != null) && (cell.getStatusHelp(point) != null))
					return(cell.getStatusHelp(point));
				}
			}
		return(super.getStatusHelp(point));
		}

					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * This method supports the use of property names of form:
					 *   selected.backgroundColor
					 * in order to specify the values of attributes for this
					 * widget on a state by state basis.
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (Utility.startsWithIgnoreCase(name, Mi_CELL_BG_PROPERTY_NAME))
			{
			backgroundManager.setCellAttributes(
				backgroundManager.getCellAttributes().setAttributeValue(
				name.substring(Mi_CELL_BG_PROPERTY_NAME.length() + 1), value));
			}
		else
			{
			super.setPropertyValue(name, value);
			}
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		if (Utility.startsWithIgnoreCase(name, Mi_CELL_BG_PROPERTY_NAME))
			{
			return(backgroundManager.getCellAttributes().getAttributeValue(
				name.substring(Mi_CELL_BG_PROPERTY_NAME.length() + 1)));
			}
		else
			{
			return(super.getPropertyValue(name));
			}
		}
	public		void		refreshLookAndFeel()
		{
		tableSelectionManager.refreshLookAndFeel();
		String className = MiDebug.getMicaClassName(this);
		if (className.equals("MiTable"))
			{
			MiToolkit.setBackgroundAttributes(contentsBackground, 
				MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
			MiToolkit.setBackgroundAttributes(rowHeaderBackground, 
				MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
			MiToolkit.setBackgroundAttributes(columnHeaderBackground, 
				MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
			MiToolkit.setBackgroundAttributes(rowFooterBackground, 
				MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
			MiToolkit.setBackgroundAttributes(columnFooterBackground, 
				MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
			}
		super.refreshLookAndFeel();
		}
	public		void		render(MiRenderer renderer)
		{
		MiBounds clip 			= MiBounds.newBounds();
		MiBounds origClipBounds 	= renderer.getClipBounds(MiBounds.newBounds());
		MiBounds masterClip 		= renderer.getClipBounds(MiBounds.newBounds());
		MiVector baseTranslation 	= renderer.getTransform().getWorldTranslation(new MiVector());
		MiBounds exposedScollXBounds	= null;
		MiBounds exposedScollYBounds	= null;


//MiDebug.println("\n\n\n\n");
//MiDebug.println(this + ": masterClip = " + masterClip);
//MiDebug.println(this + ": renderer.isPrinterRenderer() = " + renderer.isPrinterRenderer());
//MiDebug.printStackTrace(this + "");
//MiDebug.println(this + ": number of cells = " + contentsCells.size());
//MiDebug.println("toScrollY = " + toScrollY);
//MiDebug.println("scrollY = " + scrollY);
//MiDebug.println("doNotScrollWhenRender = " + doNotScrollWhenRender);
//System.out.println("toScrollX = " + toScrollX);
//System.out.println("scrollX = " + scrollX);
//System.out.println("topVisibleRow = " + topVisibleRow);
//System.out.println("leftVisibleColumn = " + leftVisibleColumn);
//System.out.println("\n\n\n\n");


		if ((vDoubleBufferedScroll) && (toScrollY != 0) && (!doNotScrollWhenRender))
			{
			// Move row topRow + scrolledRows up into topRow's position
			MiBounds b = getContentsBounds(MiBounds.newBounds());
			MiCoord ymin = b.ymin;
			MiCoord ymax = b.ymax;
			b.union(tableLayout.getColumnHeaderBounds(tmpBounds));
			b.union(tableLayout.getColumnFooterBounds(tmpBounds));
			b.ymin = ymin;
			b.ymax = ymax;

			if (Math.abs(toScrollY) < b.getHeight())
				{
				if (toScrollY > 0)
					{
					b.ymax -= toScrollY;
					renderer.moveImageArea(b, 0, toScrollY);
					b.ymax = b.ymin + toScrollY;
					}
				else
					{
					b.ymin -= toScrollY;
					renderer.moveImageArea(b, 0, toScrollY);
					b.ymin = b.ymax + toScrollY;
					}
				masterClip.copy(b);
				renderer.setClipBounds(b);
				exposedScollYBounds = MiBounds.newBounds();
				exposedScollYBounds.copy(b);
				}
			toScrollY = 0;
			MiBounds.freeBounds(b);
			}
		if ((hDoubleBufferedScroll) && (toScrollX != 0) && (!doNotScrollWhenRender))
			{
			toScrollX = -toScrollX;
			MiBounds b = getContentsBounds(MiBounds.newBounds());
			MiCoord xmin = b.xmin;
			MiCoord xmax = b.xmax;
			b.union(tableLayout.getRowHeaderBounds(tmpBounds));
			b.union(tableLayout.getRowFooterBounds(tmpBounds));
			b.xmin = xmin;
			b.xmax = xmax;

			if (Math.abs(toScrollX) < b.getWidth())
				{
				if (toScrollX > 0)
					{
					b.xmax -= toScrollX;
					renderer.moveImageArea(b, toScrollX, 0);
					b.xmax = b.xmin + toScrollX;
					}
				else
					{
					b.xmin -= toScrollX;
					renderer.moveImageArea(b, toScrollX, 0);
					b.xmin = b.xmax + toScrollX;
					}
				masterClip.copy(b);
				renderer.setClipBounds(b);
				exposedScollXBounds = MiBounds.newBounds();
				exposedScollXBounds.copy(b);
				}
			toScrollX = 0;
			MiBounds.freeBounds(b);
			}

		rowHeaderContainer.setInvalidAreaNotificationsEnabled(false);
		rowHeaderContainer.setHidden(true);
		rowFooterContainer.setInvalidAreaNotificationsEnabled(false);
		rowFooterContainer.setHidden(true);
		columnHeaderContainer.setInvalidAreaNotificationsEnabled(false);
		columnHeaderContainer.setHidden(true);
		columnFooterContainer.setInvalidAreaNotificationsEnabled(false);
		columnFooterContainer.setHidden(true);
		contentsContainer.setInvalidAreaNotificationsEnabled(false);
		contentsContainer.setHidden(true);

		contentsBackground.setInvalidAreaNotificationsEnabled(false);
		contentsBackground.setHidden(true);
		rowHeaderBackground.setInvalidAreaNotificationsEnabled(false);
		rowHeaderBackground.setHidden(true);
		rowFooterBackground.setInvalidAreaNotificationsEnabled(false);
		rowFooterBackground.setHidden(true);
		columnHeaderBackground.setInvalidAreaNotificationsEnabled(false);
		columnHeaderBackground.setHidden(true);
		columnFooterBackground.setInvalidAreaNotificationsEnabled(false);
		columnFooterBackground.setHidden(true);

		// Print background...
		super.render(renderer);


		rowHeaderContainer.setHidden(false);
		rowHeaderContainer.setInvalidAreaNotificationsEnabled(true);
		rowFooterContainer.setHidden(false);
		rowFooterContainer.setInvalidAreaNotificationsEnabled(true);
		columnHeaderContainer.setHidden(false);
		columnHeaderContainer.setInvalidAreaNotificationsEnabled(true);
		columnFooterContainer.setHidden(false);
		columnFooterContainer.setInvalidAreaNotificationsEnabled(true);
		contentsContainer.setHidden(false);
		contentsContainer.setInvalidAreaNotificationsEnabled(true);

		contentsBackground.setHidden(false);
		contentsBackground.setInvalidAreaNotificationsEnabled(true);
		rowHeaderBackground.setHidden(false);
		rowHeaderBackground.setInvalidAreaNotificationsEnabled(true);
		rowFooterBackground.setHidden(false);
		rowFooterBackground.setInvalidAreaNotificationsEnabled(true);
		columnHeaderBackground.setHidden(false);
		columnHeaderBackground.setInvalidAreaNotificationsEnabled(true);
		columnFooterBackground.setHidden(false);
		columnFooterBackground.setInvalidAreaNotificationsEnabled(true);

		// ---------------------------------------
		// 	Draw borders
		// ---------------------------------------
		MiBounds contentsBounds = getContentsBounds(MiBounds.newBounds());
//MiDebug.println("contentsBounds=" + contentsBounds);

		boolean roomForContents = !contentsBounds.isReversed();
		if ((contentsBackground != null) && (roomForContents))
			{
			tmpBounds.copy(contentsBounds);
			contentsBackground.setBounds(tmpBounds.addMargins(contentsMargins));
			contentsBackground.draw(renderer);
			}
		
		MiBounds rowHeaderBounds = tableLayout.getRowHeaderBounds(MiBounds.newBounds());
		if ((rowHeaderBackground != null) && (hasRowHeaders()))
			{
			tmpBounds.copy(rowHeaderBounds);
			rowHeaderBackground.setBounds(tmpBounds.addMargins(rowHeaderMargins));
			rowHeaderBackground.draw(renderer);
			}

		MiBounds rowFooterBounds = tableLayout.getRowFooterBounds(MiBounds.newBounds());
		if ((rowFooterBackground != null) && (hasRowFooters()))
			{
			tmpBounds.copy(rowFooterBounds);
			rowFooterBackground.setBounds(tmpBounds.addMargins(rowFooterMargins));
			rowFooterBackground.draw(renderer);
			}
		
		MiBounds columnHeaderBounds = tableLayout.getColumnHeaderBounds(MiBounds.newBounds());
		if ((columnHeaderBackground != null) && (hasColumnHeaders()))
			{
			tmpBounds.copy(columnHeaderBounds);
			columnHeaderBackground.setBounds(tmpBounds.addMargins(columnHeaderMargins));
			columnHeaderBackground.draw(renderer);
			}
		
		MiBounds columnFooterBounds = tableLayout.getColumnFooterBounds(MiBounds.newBounds());
		if ((columnFooterBackground != null) && (hasColumnFooters()))
			{
			tmpBounds.copy(columnFooterBounds);
			columnFooterBackground.setBounds(tmpBounds.addMargins(columnFooterMargins));
			columnFooterBackground.draw(renderer);
			}
		
		// ---------------------------------------
		// 	Draw Contents
		// ---------------------------------------
		MiVector vector = new MiVector(
			getLeftVisibleColumnXOffset() + scrollX, getTopVisibleRowYOffset() - scrollY);
		MiVector cVector = new MiVector(-vector.x, -vector.y);
//MiDebug.println("getTopVisibleRowYOffset=" + getTopVisibleRowYOffset());
//MiDebug.println("scrollY="+ scrollY);
//MiDebug.println("getTopVisibleRowYOffset()=" + getTopVisibleRowYOffset());
//MiDebug.println("cVector=" + cVector);
//MiDebug.println("roomForContents=" + roomForContents);

		contentsContainer.setVisible(roomForContents);
		if (roomForContents)
			{
			contentsContainer.getTransform().setDeviceTranslation(zeroVector);
			contentsContainer.getTransform().setWorldTranslation(vector);
			contentsContainer.setInvalidLayoutNotificationsEnabled(false);
			contentsContainer.refreshBounds();
			contentsContainer.setInvalidLayoutNotificationsEnabled(true);

			// Do this here instead of in MiDrawManager because we do our own scrolling here
			if (exposedScollXBounds != null)
				{
				getDrawManager().invalidateBackToFront(exposedScollXBounds);
				MiBounds.freeBounds(exposedScollXBounds);
				}
			if (exposedScollYBounds != null)
				{
				getDrawManager().invalidateBackToFront(exposedScollYBounds);
				MiBounds.freeBounds(exposedScollYBounds);
				}


			if (contentsBounds.intersectionWith(masterClip))
				{
				renderer.setClipBounds(contentsBounds);
				backgroundManager.render(renderer);

				contentsBounds.translate(vector);
				renderer.getTransform().setWorldTranslation(cVector);

				MiTableCells cells = contentsCells;
				for (int i = 0; i < cells.size(); ++i)
					{
					MiTableCell cell = cells.elementAt(i);

//MiDebug.println(this + "drawing cell:  " + cell);

if ((cell == null) || (cell.innerBounds == null))
{
MiDebug.println("table = " + this);
MiDebug.println("contents cell = " + cell);
MiDebug.println("cell.bounds = " + cell.bounds);
MiDebug.println("cell.innerBounds = " + cell.innerBounds);
MiDebug.println("cell index = " + i);
MiDebug.println("hasValidLayout() = " + hasValidLayout());
MiDebug.println("DID YOU ACCIDENTALLY removeAllParts on this MiTable instead of removeAllCells? " + this);
//MiDebug.dump(getRootWindow());
continue;
}
//MiDebug.println("cell.isVisible() " + cell.isVisible());
					if (cell.isVisible())
						{
						clip.copy(contentsBounds);
//MiDebug.println("clip = " + clip);
//MiDebug.println("cell.innerBounds = " + cell.innerBounds);
//MiDebug.println("clip.intersectionWith(cell.innerBounds) " + clip.intersectionWith(cell.innerBounds));
						if (clip.intersectionWith(cell.innerBounds))
							{
							renderer.setClipBounds(clip);
//MiDebug.println("calling draw cell: " + cell);
//MiDebug.println(" renderer.getTransform(): " + renderer.getTransform());
							cell.draw(renderer);
							}
						}
					}
				}
			}
		// ---------------------------------------
		// 	Draw Row Headers
		// ---------------------------------------
		if (hasRowHeaders())
			{
//System.out.println("ROW HEADERS");

			vector.set(getLeftVisibleColumnXOffset() + scrollX, 0);

			cVector.set(-vector.x, -vector.y);
			rowHeaderContainer.getTransform().setDeviceTranslation(zeroVector);
			rowHeaderContainer.getTransform().setWorldTranslation(vector);
			rowHeaderContainer.setInvalidLayoutNotificationsEnabled(false);
			rowHeaderContainer.refreshBounds();
			rowHeaderContainer.setInvalidLayoutNotificationsEnabled(true);


			if (rowHeaderBounds.intersectionWith(masterClip))
				{
				renderer.getTransform().setWorldTranslation(zeroVector);
				renderer.setClipBounds(rowHeaderBounds);
				backgroundManager.render(renderer, ROW_HEADER_ARRAY, getVisibleColumns());

				rowHeaderBounds.translate(vector);
				renderer.getTransform().setWorldTranslation(cVector);

				for (int i = 0; i < rowHeaderCells.size(); ++i)
					{
					MiTableCell cell = rowHeaderCells.elementAt(i);
					if (cell.isVisible())
						{
						clip.copy(rowHeaderBounds);
if (cell.innerBounds == null)
MiDebug.println("CELL HAS NO INNER BOUNDS!: " + cell);
						if (clip.intersectionWith(cell.innerBounds))
							{
							renderer.setClipBounds(clip);
							cell.draw(renderer);
							}
						}
					}
				}
			}
		// ---------------------------------------
		// 	Draw Row Footers
		// ---------------------------------------
		if (hasRowFooters())
			{
//System.out.println("ROW FOOTERS");
			vector.set(getLeftVisibleColumnXOffset() + scrollX, 0);
			cVector.set(-vector.x, -vector.y);
			rowFooterContainer.getTransform().setDeviceTranslation(zeroVector);
			rowFooterContainer.getTransform().setWorldTranslation(vector);
			rowFooterContainer.setInvalidLayoutNotificationsEnabled(false);
			rowFooterContainer.refreshBounds();
			rowFooterContainer.setInvalidLayoutNotificationsEnabled(true);

			if (rowFooterBounds.intersectionWith(masterClip))
				{
				renderer.getTransform().setWorldTranslation(zeroVector);
				renderer.setClipBounds(rowFooterBounds);
				backgroundManager.render(renderer, ROW_FOOTER_ARRAY, getVisibleColumns());

				rowFooterBounds.translate(vector);
				renderer.getTransform().setWorldTranslation(cVector);

				for (int i = 0; i < rowFooterCells.size(); ++i)
					{
					MiTableCell cell = rowFooterCells.elementAt(i);
					if (cell.isVisible())
						{
						clip.copy(rowFooterBounds);
						if (clip.intersectionWith(cell.innerBounds))
							{
							renderer.setClipBounds(clip);
							cell.draw(renderer);
							}
						}
					}
				}
			}
		// ---------------------------------------
		// 	Draw Column Headers
		// ---------------------------------------
		if (hasColumnHeaders())
			{
//System.out.println("COLUMN HEADERS");
			vector.set(0, getTopVisibleRowYOffset() - scrollY);
			cVector.set(-vector.x, -vector.y);
			columnHeaderContainer.getTransform().setDeviceTranslation(zeroVector);
			columnHeaderContainer.getTransform().setWorldTranslation(vector);
			columnHeaderContainer.setInvalidLayoutNotificationsEnabled(false);
			columnHeaderContainer.refreshBounds();
			columnHeaderContainer.setInvalidLayoutNotificationsEnabled(true);

			if (columnHeaderBounds.intersectionWith(masterClip))
				{
				renderer.getTransform().setWorldTranslation(zeroVector);
				renderer.setClipBounds(columnHeaderBounds);
				backgroundManager.render(renderer, getVisibleRows(), COLUMN_HEADER_ARRAY);

				columnHeaderBounds.translate(vector);
				renderer.getTransform().setWorldTranslation(cVector);

				for (int i = 0; i < columnHeaderCells.size(); ++i)
					{
					MiTableCell cell = columnHeaderCells.elementAt(i);
					if (cell.isVisible())
						{
						clip.copy(columnHeaderBounds);
						if (clip.intersectionWith(cell.innerBounds))
							{
							renderer.setClipBounds(clip);
							cell.draw(renderer);
							}
						}
					}
				}
			}
		// ---------------------------------------
		// 	Draw Column Footers
		// ---------------------------------------
		if (hasColumnFooters())
			{
//System.out.println("COLUMN FOOTERS");
			vector.set(0, getTopVisibleRowYOffset() - scrollY);
			cVector.set(-vector.x, -vector.y);
			columnFooterContainer.getTransform().setDeviceTranslation(zeroVector);
			columnFooterContainer.getTransform().setWorldTranslation(vector);
			columnFooterContainer.setInvalidLayoutNotificationsEnabled(false);
			columnFooterContainer.refreshBounds();
			columnFooterContainer.setInvalidLayoutNotificationsEnabled(true);

			if (columnFooterBounds.intersectionWith(masterClip))
				{
				renderer.getTransform().setWorldTranslation(zeroVector);
				renderer.setClipBounds(columnFooterBounds);
				backgroundManager.render(renderer, getVisibleRows(), COLUMN_FOOTER_ARRAY);

				columnFooterBounds.translate(vector);
				renderer.getTransform().setWorldTranslation(cVector);

				for (int i = 0; i < columnFooterCells.size(); ++i)
					{
					MiTableCell cell = columnFooterCells.elementAt(i);
					if (cell.isVisible())
						{
						clip.copy(columnFooterBounds);
						if (clip.intersectionWith(cell.innerBounds))
							{
							renderer.setClipBounds(clip);
							cell.draw(renderer);
							}
						}
					}
				}
			}
//System.out.println("EXIT base Translation= " + baseTranslation);
		renderer.getTransform().setWorldTranslation(baseTranslation);
		renderer.setClipBounds(masterClip);
		renderOverlay(renderer);
		renderer.setClipBounds(origClipBounds);
		MiBounds.freeBounds(clip);
		MiBounds.freeBounds(origClipBounds);
		MiBounds.freeBounds(masterClip);
		MiBounds.freeBounds(contentsBounds);
		MiBounds.freeBounds(rowFooterBounds);
		MiBounds.freeBounds(rowHeaderBounds);
		MiBounds.freeBounds(columnFooterBounds);
		MiBounds.freeBounds(columnHeaderBounds);

		doNotScrollWhenRender = false;
		toScrollY = 0;
		toScrollX = 0;
		}
	public		void		renderOverlay(MiRenderer renderer)
		{
		}

	// -----------------------------------------------------------------
	// Implementation of MiiBackgroundableGrid
	// -----------------------------------------------------------------
	public		MiBounds	getRowBounds(
						int rowNumber, 
						int startColumn, 
						int endColumn, 
						MiBounds bounds)
		{
		tableLayout.getRowBounds(rowNumber, startColumn, endColumn, bounds);
		translateBoundsForMiiBackgroundableGrid(rowNumber, startColumn, bounds);
		return(bounds);
		}
	public		MiBounds	getColumnBounds(
						int columnNumber, 
						int startRow, 
						int endRow, 
						MiBounds bounds)
		{
		tableLayout.getColumnBounds(columnNumber, startRow, endRow, bounds);
		translateBoundsForMiiBackgroundableGrid(startRow, columnNumber, bounds);
		return(bounds);
		}
	public		MiBounds	getCellBounds(int rowNumber, int columnNumber, MiBounds bounds)
		{
		tableLayout.getCellBounds(rowNumber, columnNumber, bounds);
		translateBoundsForMiiBackgroundableGrid(rowNumber, columnNumber, bounds);
		return(bounds);
		}
	private		void		translateBoundsForMiiBackgroundableGrid(
						int rowNumber, int columnNumber, MiBounds bounds)
		{
		if (rowNumber < 0)
			{
			bounds.translate(-getLeftVisibleColumnXOffset() - scrollX, 0);
			}
		else if (columnNumber < 0)
			{
			bounds.translate(0, -getTopVisibleRowYOffset() + scrollY);
			}
		else
			{
			bounds.translate(
				-getLeftVisibleColumnXOffset() - scrollX, 
				-getTopVisibleRowYOffset() + scrollY);
			}
		}
	protected	MiVector	getCellToActualCoordTranslation(MiVector vector)
		{
		vector.x = -getLeftVisibleColumnXOffset() - scrollX;
		vector.y = -getTopVisibleRowYOffset() + scrollY;
		return(vector);
		}
	public		int[]		getVisibleRows()
		{
		return(visibleRows);
		}
	public		int[]		getVisibleColumns()
		{
		return(visibleColumns);
		}
	public		boolean		isCellIntersectingRowColumn(
						int cellRowNum, int cellColumnNum, 
						int rowNum, int columnNum)
		{
		MiTableCell cell = getCell(cellRowNum, cellColumnNum);
		if (((cell.rowNumber <= rowNum) 
				&& (cell.rowNumber + cell.numberOfRows > rowNum))
			&& ((cell.columnNumber <= columnNum) 
				&& (cell.columnNumber + cell.numberOfColumns > columnNum)))
			{
			return(true);
			}
		return(false);
		}

	public		MiiContextMenu	getContextMenu(MiBounds area)	
		{
//MiDebug.println("getContextMenu: " + area);
		if (area != null)
			{
			MiTableCell cell = pickCell(area);
//MiDebug.println("getContextMenu: cell= " + cell);
			if (cell != null)
				{
				if (cell.getContextMenu(area) != null)
					return(cell.getContextMenu(area));
				int rowNumber = cell.getRowNumber();
				int columnNumber = cell.getColumnNumber();

				cell = getColumnDefaults(columnNumber);
				if ((cell != null) && (cell.getContextMenu(area) != null))
					return(cell.getContextMenu(area));

				cell = getRowDefaults(rowNumber);
				if ((cell != null) && (cell.getContextMenu(area) != null))
					return(cell.getContextMenu(area));
				}
			}
		return(super.getContextMenu(area));
		}
	public		MiiHelpInfo	getToolHintHelp(MiPoint point)	
		{
		if (point != null)
			{
			tmpBounds2.setBounds(point);
			MiTableCell cell = pickCell(tmpBounds2);
			if (cell != null)
				{
				if (cell.getToolHintHelp(point) != null)
					return(cell.getToolHintHelp(point));
				int rowNumber = cell.getRowNumber();
				int columnNumber = cell.getColumnNumber();

				cell = getColumnDefaults(columnNumber);
				if ((cell != null) && (cell.getToolHintHelp(point) != null))
					return(cell.getToolHintHelp(point));

				cell = getRowDefaults(rowNumber);
				if ((cell != null) && (cell.getToolHintHelp(point) != null))
					return(cell.getToolHintHelp(point));
				}
			}
		return(super.getToolHintHelp(point));
		}

	public		void		invalidateArea()
		{
		doNotScrollWhenRender = true;
		super.invalidateArea();
		}
	public		void		invalidateArea(MiBounds area)
		{
		doNotScrollWhenRender = true;
		super.invalidateArea(area);
		}
	protected	void		updateForNewLayoutPosition()
		{
		numberOfVisibleRows = calcNumberOfVisibleRows();
		numberOfVisibleColumns = calcNumberOfVisibleColumns();
		updateVisibleRowsArray();
		updateVisibleColumnsArray();
		}
	protected	void		updateForNewLayout()
		{
		numberOfVisibleRows = calcNumberOfVisibleRows();
		if (calcNormalizedVerticalAmountVisible() >= 1.0)
			{
			// Force display of top of contents
			toScrollY = 0;
			scrollY = 0;
			topVisibleRow = 0;
			topVisibleRowYOffset = 0;
 			normalizedVerticalPosition = 1.0;
			}
		else
			{
 			double position = getNormalizedVerticalPosition(topVisibleRow, scrollY);
 			if (position != normalizedVerticalPosition)
				{
				normalizedVerticalPosition = position;
				dispatchAction(Mi_ITEM_SCROLLED_ACTION);
				}
			}

		numberOfVisibleColumns = calcNumberOfVisibleColumns();
		if (calcNormalizedHorizontalAmountVisible() >= 1.0)
			{
			// Force display of left side of contents
			toScrollX = 0;
			scrollX = 0;
			leftVisibleColumn = 0;
			leftVisibleColumnXOffset = 0;
 			normalizedHorizontalPosition = 0.0;
			}
		else
			{
 			double position = getNormalizedHorizontalPosition(leftVisibleColumn, scrollX);
 			if (position != normalizedHorizontalPosition)
				{
				normalizedHorizontalPosition = position;
				dispatchAction(Mi_ITEM_SCROLLED_ACTION);
				}
			}

		updateVisibleRowsArray();
		updateVisibleColumnsArray();
		}
	protected	void		updateVisibleRowsArray()
		{
		int numberOfVisibleRows = getNumberOfVisibleRows();
		if (visibleRows.length != numberOfVisibleRows)
			visibleRows = new int[numberOfVisibleRows];

		for (int i = 0; i < numberOfVisibleRows; ++i)
			visibleRows[i] = topVisibleRow + i; 
		}

	protected	void		updateVisibleColumnsArray()
		{
		if (visibleColumns.length != numberOfVisibleColumns)
			visibleColumns = new int[numberOfVisibleColumns];

		for (int i = 0; i < numberOfVisibleColumns; ++i)
			visibleColumns[i] = leftVisibleColumn + i; 
		}
	
	// -----------------------------------------------------------------
	// Implementation of MiiAdjustableGrid
	// -----------------------------------------------------------------

	public		boolean		useNormalizedPositionForContinuousHorizontalAdjustment()
		{
		return(hAlignmentPolicy == Mi_PIXEL_BY_PIXEL_POLICY);
		}
	public		boolean		useNormalizedPositionForContinuousVerticalAdjustment()
		{
		return(true);
		//return(vAlignmentPolicy == Mi_PIXEL_BY_PIXEL_POLICY);
		}
	public		void		setNormalizedHorizontalPosition(double position)
		{
		if (position == normalizedHorizontalPosition)
			return;

		MiDistance	oldToScrollX 	= toScrollX;
		int[]		columnNumber 	= new int[1];
		MiDistance[]	columnOffset	= new MiDistance[1];

 		getColumnAndHorizontalOffset(position, columnNumber, columnOffset);
		MiDistance 	newToScrollX 	= getChangeInHorizontalOffset(
							leftVisibleColumn, scrollX, 
							columnNumber[0], columnOffset[0]);

		boolean changedColumn = false;

		normalizedHorizontalPosition = position;
		if (hAlignmentPolicy == Mi_PIXEL_BY_PIXEL_POLICY)
			{
			if (columnNumber[0] != leftVisibleColumn)
				{
				setLeftVisibleColumn(columnNumber[0]);
				changedColumn = true;
				}
			scrollX = columnOffset[0];
			numberOfVisibleColumns = calcNumberOfVisibleColumns();
			}
		else if (hAlignmentPolicy == Mi_LINE_BY_LINE_POLICY)
			{
			MiDistance xOffset = columnOffset[0];
			if (xOffset != 0)
				{
				if ((xOffset > tableLayout.getSingleColumnBounds(
						columnNumber[0], 0, 0, tmpBounds).getWidth()/2)
					&& (columnNumber[0] < getLeftColumnOfLastVisibleScreen()))
					{
					setLeftVisibleColumn(columnNumber[0] + 1);
					newToScrollX = toScrollX - oldToScrollX;
					changedColumn = true;
					}
				else
					{
					newToScrollX -= xOffset;
					}
				}
			if ((!changedColumn) && (columnNumber[0] != leftVisibleColumn))
				{
				setLeftVisibleColumn(columnNumber[0]);
				changedColumn = true;
				}
			}

		updateVisibleColumnsArray();

		if (hDoubleBufferedScroll)
			{
			toScrollX = oldToScrollX + newToScrollX;
			if (toScrollX != 0)
				getDrawManager().setTargetIsManuallyScrolling(true);
			}
		if ((!changedColumn) && (oldToScrollX + newToScrollX != 0))
			{
			MiBounds b = getContentsBounds(MiBounds.newBounds());
			b.addMargins(contentsMargins);
			b.union(rowHeaderBackground.getBounds(tmpBounds));
			b.union(rowFooterBackground.getBounds(tmpBounds));
			super.invalidateArea(b);
			MiBounds.freeBounds(b);
			}

		if ((!changedColumn) && (newToScrollX != 0))
			{
			dispatchAction(Mi_ITEM_SCROLLED_ACTION);
			}
		}
	protected	void		setScrollX(int scroll)
		{
		toScrollX = scroll - scrollX;

		scrollX = scroll;
		if (toScrollX != 0)
			getDrawManager().setTargetIsManuallyScrolling(true);

		MiBounds b = getContentsBounds(MiBounds.newBounds());
		b.addMargins(contentsMargins);
		b.union(rowHeaderBackground.getBounds(tmpBounds));
		b.union(rowFooterBackground.getBounds(tmpBounds));
		super.invalidateArea(b);
		MiBounds.freeBounds(b);

		dispatchAction(Mi_ITEM_SCROLLED_ACTION);
		}
	protected	void		setScrollY(int scroll)
		{
		toScrollY = scroll - scrollY;

		scrollY = scroll;
		if (toScrollY != 0)
			getDrawManager().setTargetIsManuallyScrolling(true);

		MiBounds b = getContentsBounds(MiBounds.newBounds());
		b.addMargins(contentsMargins);
		b.union(rowHeaderBackground.getBounds(tmpBounds));
		b.union(rowFooterBackground.getBounds(tmpBounds));
		super.invalidateArea(b);
		MiBounds.freeBounds(b);

		dispatchAction(Mi_ITEM_SCROLLED_ACTION);
		}
	public		void		setNormalizedVerticalPosition(double position)
		{
		if (position == normalizedVerticalPosition)
			return;

		MiDistance	oldToScrollY 	= toScrollY;
		int[]		rowNumber 	= new int[1];
		MiDistance[]	rowOffset	= new MiDistance[1];

 		getRowAndVerticalOffset(position, rowNumber, rowOffset);
		MiDistance 	newToScrollY 	= getChangeInVerticalOffset(
							topVisibleRow, scrollY, 
							rowNumber[0], rowOffset[0]);


		boolean changedRow = false;

		normalizedVerticalPosition = position;
		if (vAlignmentPolicy == Mi_PIXEL_BY_PIXEL_POLICY)
			{
			if (rowNumber[0] != topVisibleRow)
				{
				setTopVisibleRow(rowNumber[0]);
				changedRow = true;
				}
			scrollY = rowOffset[0];
			numberOfVisibleRows = calcNumberOfVisibleRows();
			}
		else if (vAlignmentPolicy == Mi_LINE_BY_LINE_POLICY)
			{
			MiDistance yOffset = rowOffset[0];
			if (yOffset != 0)
				{
				if ((yOffset > tableLayout.getSingleRowBounds(
						rowNumber[0], 0, 0, tmpBounds).getHeight()/2)
					&& (rowNumber[0] < getTopRowOfLastVisibleScreen()))
					{
					setTopVisibleRow(rowNumber[0] + 1);
					newToScrollY = toScrollY - oldToScrollY;
					changedRow = true;
					}
				else
					{
					newToScrollY -= yOffset;
					}
				}
			if ((!changedRow) && (rowNumber[0] != topVisibleRow))
				{
				setTopVisibleRow(rowNumber[0]);
				changedRow = true;
				}
			}

		updateVisibleRowsArray();

		if (vDoubleBufferedScroll)
			{
			toScrollY = oldToScrollY + newToScrollY;
			if (toScrollY != 0)
				getDrawManager().setTargetIsManuallyScrolling(true);
			}
		if ((!changedRow) && (oldToScrollY + newToScrollY != 0))
			{
			MiBounds b = getContentsBounds(MiBounds.newBounds());
			b.addMargins(contentsMargins);
			b.union(columnHeaderBackground.getBounds(tmpBounds));
			b.union(columnFooterBackground.getBounds(tmpBounds));
			super.invalidateArea(b);
			MiBounds.freeBounds(b);
			}
		if ((!changedRow) && (newToScrollY != 0))
			{
			dispatchAction(Mi_ITEM_SCROLLED_ACTION);
			}
		}
	// -----------------------------------------------------------------
	// Implementation of MiiScrollableData
	// -----------------------------------------------------------------
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
		return(false);
		}

					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * horizontal position of the data (0.0 is the left side
					 * and 1.0 is the right side).
	 				 * @return 	 	the horizontal position
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		double		getNormalizedHorizontalPosition()
		{
		return(normalizedHorizontalPosition);
		}

					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * vertical position of the data (0.0 is the bottom side
					 * and 1.0 is the top side).
	 				 * @return 	 	the vertical position
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		double		getNormalizedVerticalPosition()
		{
		return(normalizedVerticalPosition);
		}

					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * horizontal size of the data (0.0 indicates none of the
					 * data is visible and 1.0 indicates all of the data's
					 * width is visible).
	 				 * @return 	 	the amount of data visible
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		double		getNormalizedHorizontalAmountVisible()
		{
		if (!hasValidLayout())
			validateLayout();

		return(calcNormalizedHorizontalAmountVisible());
		}

	protected	double		calcNormalizedHorizontalAmountVisible()
		{
		tableLayout.getTotalContentsBounds(tmpBounds);
		if (tmpBounds.isReversed())
			return(1.0);
		MiDistance width = tmpBounds.getWidth();
		getContentsBounds(tmpBounds);
		return(tmpBounds.getWidth()/width);
		}
					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * vertical size of the data (0.0 indicates none of the
					 * data is visible and 1.0 indicates all of the data's
					 * height is visible).
	 				 * @return 	 	the amount of data visible
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		double		getNormalizedVerticalAmountVisible()
		{
		if (!hasValidLayout())
			validateLayout();

		return(calcNormalizedVerticalAmountVisible());
		}
	protected	double		calcNormalizedVerticalAmountVisible()
		{
		tableLayout.getTotalContentsBounds(tmpBounds);
		if (tmpBounds.isReversed())
			return(1.0);
		MiDistance height = tmpBounds.getHeight();
		getContentsBounds(tmpBounds);
		return(tmpBounds.getHeight()/height);
		}
					/**------------------------------------------------------
	 				 * Scrolls one line up (conversely, move the data one
					 * line down). If already at the top of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollLineUp()
		{
		if ((getNumberOfRows() == 1) || (useNormalizedPositionForContinuousVerticalAdjustment()))
			{
			setNormalizedVerticalPosition(
				Math.min(normalizedVerticalPosition + percentToScrollWhenTopDoesNotGravitateToCellBoundry, 1.0));
			}
		else
			{
			int index = getTopVisibleRow();
			if (index > 0)
				setTopVisibleRow(index - 1);
			else if (scrollY != 0)
				setScrollY(0);
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one line down (conversely, move the data one
					 * line up). If already at the bottom of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollLineDown()
		{
		if ((getNumberOfRows() == 1) || (useNormalizedPositionForContinuousVerticalAdjustment()))
			{
			setNormalizedVerticalPosition(
				Math.max(normalizedVerticalPosition - percentToScrollWhenTopDoesNotGravitateToCellBoundry, 0.0));
			}
		else
			{
			int index = getTopVisibleRow();
			if (index < getTopRowOfLastVisibleScreen())
				{
				setTopVisibleRow(index + 1);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one line left (conversely, move the data one
					 * line right). If already at the left of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollLineLeft()
		{
		if (getNumberOfColumns() == 1)
			{
			double position = getNormalizedHorizontalPosition();
			if (position > 0.0)
				setNormalizedHorizontalPosition(Math.max(position - 0.10, 0.0));
			}
		else
			{
			int index = getLeftVisibleColumn();
			if (index > 0)
				{
				setLeftVisibleColumn(index - 1);
				normalizedHorizontalPosition = ((double )(index - 1))/(getNumberOfColumns() - 1);
				}
			else if (scrollY != 0)
				{
				setScrollY(0);
				normalizedHorizontalPosition = getNormalizedHorizontalPosition(getLeftVisibleColumn(), 0.0);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one line right (conversely, move the data one
					 * line left). If already at the right of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollLineRight()
		{
		if (getNumberOfColumns() == 1)
			{
			double position = getNormalizedHorizontalPosition();
			if (position < 1.0)
				setNormalizedHorizontalPosition(Math.min(position + 0.10, 1.0));
			}
		else
			{
			int index = getLeftVisibleColumn();
			if (index <= getNumberOfColumns() - getNumberOfVisibleColumns())
				{
				setLeftVisibleColumn(index + 1);
				normalizedHorizontalPosition = ((double )(index))/(getNumberOfColumns() - getNumberOfVisibleColumns());
				if (normalizedHorizontalPosition > 1.0)
					{
					normalizedHorizontalPosition = 1.0;
					}
				}

			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one chunk up (conversely, move the data one
					 * chunk down). If already at the top of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollChunkUp()
		{
		if ((getNumberOfRows() == 1) || (useNormalizedPositionForContinuousVerticalAdjustment()))
			{
			setNormalizedVerticalPosition(
				Math.min(normalizedVerticalPosition + 
					Math.max(5 * percentToScrollWhenTopDoesNotGravitateToCellBoundry, 0.10), 1.0));
			}
		else
			{
			int index = getTopVisibleRow();
			int numRows = getNumberOfVisibleRows();
			if (index > 0)
				{
				if ((index = index - numRows/2) < 0)
					index = 0;
				setTopVisibleRow(index);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one chunk down (conversely, move the data one
					 * chunk up). If already at the bottom of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollChunkDown()
		{
		if ((getNumberOfRows() == 1) || (useNormalizedPositionForContinuousVerticalAdjustment()))
			{
			setNormalizedVerticalPosition(
				Math.max(normalizedVerticalPosition - 
					Math.max(5 * percentToScrollWhenTopDoesNotGravitateToCellBoundry, 0.10), 0.0));
			}
		else
			{
			int index = getTopVisibleRow();
			int numRows = getNumberOfVisibleRows();

			if (index < getNumberOfRows() - getNumberOfVisibleRows())
				{
				if ((index = index + numRows/2) > getNumberOfRows() - getNumberOfVisibleRows())
					index = getNumberOfRows() - getNumberOfVisibleRows();
				setTopVisibleRow(index);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one chunk left (conversely, move the data one
					 * chunk right). If already at the left of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollChunkLeft()
		{
		if (getNumberOfColumns() == 1)
			{
			setNormalizedHorizontalPosition(
				Math.max(normalizedHorizontalPosition - 
					Math.max(5 * percentToScrollWhenLeftDoesNotGravitateToCellBoundry, 0.10), 0.0));
			}
		else
			{
			int index = getLeftVisibleColumn();
			if (index > 0)
				{
				int numColumns = getNumberOfVisibleColumns();
				if ((numColumns >= 4) && (index - numColumns/4 >= 0))
					{
					index = index - numColumns/4;
					}
				else
					{
					index -= 1;
					}
				setLeftVisibleColumn(index);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one chunk right (conversely, move the data one
					 * chunk left). If already at the right of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollChunkRight()
		{
		if (getNumberOfColumns() == 1)
			{
			setNormalizedHorizontalPosition(
				Math.min(normalizedHorizontalPosition + 
					Math.max(5 * percentToScrollWhenLeftDoesNotGravitateToCellBoundry, 0.10), 1.0));
			}
		else
			{
			int index = getLeftVisibleColumn();
			int numColumns = getNumberOfVisibleColumns();

			if (index < getNumberOfColumns()) // 4-14-2004  - getNumberOfVisibleColumns() - 1)
				{
/*
				if ((index = index + numColumns/2) > getNumberOfColumns() - getNumberOfVisibleColumns())
					index = getNumberOfColumns() - getNumberOfVisibleColumns();
*/
				if ((numColumns >= 4) && (index + numColumns/4 < getNumberOfColumns()))
					{
					index = index + numColumns/4;
					}
				else
					{
					++index;
					}
				setLeftVisibleColumn(index);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one page up (conversely, move the data one
					 * page down). If already at the top of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollPageUp()
		{
		int index = getTopVisibleRow();
		int numRows = getNumberOfVisibleRows();
		if (index > 0)
			{
			if ((index = index - numRows) < 0)
				index = 0;
			setTopVisibleRow(index);
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one page down (conversely, move the data one
					 * page up). If already at the bottom of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollPageDown()
		{
		int index = getTopVisibleRow();
		int numRows = getNumberOfVisibleRows();

		if (index < getNumberOfRows() - getNumberOfVisibleRows())
			{
			if ((index = index + numRows) > getNumberOfRows() - getNumberOfVisibleRows())
				index = getNumberOfRows() - getNumberOfVisibleRows();
			setTopVisibleRow(index);
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one page left (conversely, move the data one
					 * page right). If already at the left of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollPageLeft()
		{
		int index = getLeftVisibleColumn();
		int numColumns = getNumberOfVisibleColumns();
		if (index > 0)
			{
			if ((index = index - numColumns) < 0)
				index = 0;
			setLeftVisibleColumn(index);
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls one page right (conversely, move the data one
					 * page left). If already at the right of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollPageRight()
		{
		int index = getLeftVisibleColumn();
		int numColumns = getNumberOfVisibleColumns();

		if (index < getNumberOfColumns() - getNumberOfVisibleColumns() - 1)
			{
			if ((index = index + numColumns) > getNumberOfColumns() - getNumberOfVisibleColumns())
				index = getNumberOfColumns() - getNumberOfVisibleColumns();
			setLeftVisibleColumn(index);
			}
		}
					/**------------------------------------------------------
	 				 * Scrolls to the top of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToTop()
		{
		if (getTopVisibleRow() != 0)
			setTopVisibleRow(0);
		}
					/**------------------------------------------------------
	 				 * Scrolls to the bottom of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToBottom()
		{
		if (getTopVisibleRow() < getNumberOfRows() - getNumberOfVisibleRows())
			setTopVisibleRow(getNumberOfRows() - getNumberOfVisibleRows());
		}
					/**------------------------------------------------------
	 				 * Scrolls to the left side of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToLeftSide()
		{
		if (getLeftVisibleColumn() != 0)
			setLeftVisibleColumn(0);
		}
					/**------------------------------------------------------
	 				 * Scrolls to the right side of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToRightSide()
		{
		if (getLeftVisibleColumn() < getNumberOfColumns() - getNumberOfVisibleColumns())
			setLeftVisibleColumn(getNumberOfColumns() - getNumberOfVisibleColumns());
		}
					/**------------------------------------------------------
	 				 * Scrolls to the top of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToNormalizedVerticalPosition(double normalizedLocation)
		{
		if ((getNumberOfRows() == 1) || (useNormalizedPositionForContinuousVerticalAdjustment()))
			setNormalizedVerticalPosition(normalizedLocation);
		else
			setTopVisibleRow((int )((getNumberOfRows() - getNumberOfVisibleRows() + 0.9999) * (1 - normalizedLocation)));
		}
					/**------------------------------------------------------
	 				 * Scrolls to the bottom of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToNormalizedHorizontalPosition(double normalizedLocation)
		{
		if ((getNumberOfColumns() == 1) || (useNormalizedPositionForContinuousHorizontalAdjustment()))
			setNormalizedHorizontalPosition(normalizedLocation);
		else
			setLeftVisibleColumn((int )((getNumberOfColumns() - getNumberOfVisibleColumns() + 0.9999) * (normalizedLocation)));
		}
	public		MiBounds	getContentsBounds(MiBounds b)
		{
		getInnerBounds(b);
		b.ymax -= tableLayout.getRowHeaderHeight();
		b.ymin += tableLayout.getRowFooterHeight();
		b.xmin += tableLayout.getColumnHeaderWidth();
		b.xmax -= tableLayout.getColumnFooterWidth();

		b.subtractMargins(sectionMargins);
		b.subtractMargins(contentsMargins);
		b.subtractHeight(rowHeaderMargins.getHeight());
		b.subtractHeight(rowFooterMargins.getHeight());
		b.subtractWidth(columnHeaderMargins.getWidth());
		b.subtractWidth(columnFooterMargins.getWidth());
		return(b);
		}
 	protected	double		getNormalizedVerticalPosition(int rowNumber, MiDistance scrollY)
		{
		if ((rowNumber == 0) && ((scrollY == 0) || (topGravitatesToCellBoundry)))
			{
			return(1.0);
			}
/*
		if (!hasValidLayout())
			{
			// Update total contents bounds if tree list expanded/ collapsed
		// infinite loop, caller to validate layout
			validateLayout();
			}
*/
		MiDistance unviewableAmount 
			= tableLayout.getTotalContentsBounds(tmpBounds).getHeight() 
				- getContentsBounds(tmpBounds).getHeight();

		if (unviewableAmount <= 0)
			{
			return(1.0);
			}
		MiCoord rowYmax = tableLayout.getSingleRowBounds(rowNumber, 0, 0, tmpBounds).getYmax() - scrollY;
		return(1 + rowYmax/unviewableAmount);
		}
 	protected	double		getNormalizedHorizontalPosition(int columnNumber, MiDistance scrollX)
		{
		if ((columnNumber == 0) && ((scrollX == 0) || (leftGravitatesToCellBoundry)))
			{
			return(0.0);
			}
			
		MiDistance distance = tableLayout.getTotalContentsBounds(tmpBounds).getWidth() 
			- getContentsBounds(tmpBounds).getWidth();

		if (distance <= 0)
			{
			return(0.0);
			}

		MiCoord columnXmin 
			= tableLayout.getSingleColumnBounds(columnNumber, 0, 0, tmpBounds).getXmin() + scrollX;
		return(columnXmin/distance);
		}
	public		int		getTopRowOfLastVisibleScreen()
		{
		MiDistance visibleHeight = getContentsBounds(tmpBounds).getHeight();
		int topRowOfLastVisibleScreen = 0;

		MiDistance height = 0;
		MiDistance alleyVSpacing = getAlleyVSpacing();
		for (int i = totalNumberOfRows - 1; i >= 0; --i)
			{
			MiDistance rowHeight = tableLayout.getSingleRowBounds(i, 0, 0, tmpBounds).getHeight();
			height += rowHeight;
			if (height > visibleHeight)
				{
				topRowOfLastVisibleScreen = i + 1;
				// If last row is really large...
				if (topRowOfLastVisibleScreen == totalNumberOfRows)
					topRowOfLastVisibleScreen -= 1;
				break;
				}
			if (rowHeight != 0)
				height += alleyVSpacing;
			}
		return(topRowOfLastVisibleScreen);
		}
	public		int 		getLeftColumnOfLastVisibleScreen()
		{
		MiDistance visibleWidth = getContentsBounds(tmpBounds).getWidth();
		int leftColumnOfLastVisibleScreen = 0;

		MiDistance width = 0;
		MiDistance alleyHSpacing = getAlleyHSpacing();
		for (int i = totalNumberOfColumns - 1; i >= 0; --i)
			{
			MiDistance colWidth = tableLayout.getSingleColumnBounds(i, 0, 0, tmpBounds).getWidth();
			width += colWidth;
			if (width > visibleWidth)
				{
				leftColumnOfLastVisibleScreen = i + 1;
				// If last column is really large...
				if (leftColumnOfLastVisibleScreen == totalNumberOfColumns)
					leftColumnOfLastVisibleScreen -= 1;
				break;
				}
			if (colWidth != 0)
				width += alleyHSpacing;
			}
		return(leftColumnOfLastVisibleScreen);
		}

	public		int		calcNumberOfVisibleRows()
		{
		if (totalNumberOfRows == 0)
			return(0);

		MiDistance visibleHeight = getContentsBounds(tmpBounds).getHeight();
		int numVisible = totalNumberOfRows;

		MiDistance height = -scrollY;
		MiDistance alleyVSpacing = getAlleyVSpacing();
		for (int i = topVisibleRow; i < totalNumberOfRows; ++i)
			{
			MiDistance rowHeight 
				= tableLayout.getSingleRowBounds(i, 0, 0, tmpBounds).getHeight();
			if (rowHeight > 0)
				{
				height += rowHeight;
				if (height >= visibleHeight)
					{
					numVisible = i - topVisibleRow + 1;
					if ((vAlignmentPolicy == Mi_LINE_BY_LINE_POLICY) && (scrollY > 0))
						numVisible -= 1;
	
					// If top row is really large...
					if (numVisible <= 0)
						numVisible = 1;
					break;
					}
				height += alleyVSpacing;
				}
			}
		return(numVisible);
		}
	public		int		getNumberOfVisibleRowAtTopIfBottomRowIsGivenRowNumber(int rowNumber)
		{
		if (totalNumberOfRows == 0)
			return(0);

		MiDistance visibleHeight = getContentsBounds(tmpBounds).getHeight();
		int topRow = rowNumber;

		MiDistance alleyVSpacing = getAlleyVSpacing();
		MiDistance height = alleyVSpacing;
		for (int i = rowNumber; i >= 0; --i)
			{
			MiDistance rowHeight 
				= tableLayout.getSingleRowBounds(i, 0, 0, tmpBounds).getHeight();
			if (rowHeight > 0)
				{
				height += rowHeight;
				if (height >= visibleHeight)
					{
					if (height > visibleHeight - alleyVSpacing)
						{
						++topRow;
						}
					break;
					}
				height += alleyVSpacing;
				}
			--topRow;
			}
		return(topRow);
		}

 	protected	void		getRowAndVerticalOffset(
						double position, 
						int[] rowNumber, 
						MiDistance[] rowOffset)
		{
		int topRowOfLastVisibleScreen = getTopRowOfLastVisibleScreen();

		MiDistance totalHeight = tableLayout.getTotalContentsBounds(tmpBounds).getHeight();
		MiDistance visibleHeight = getContentsBounds(tmpBounds).getHeight();

		MiCoord tableYmax = 0;
		MiCoord lastRowYmax = 0;
		MiDistance distanceFromTop = (1.0 - position) * (totalHeight - visibleHeight);

		if (topRowOfLastVisibleScreen == 0)
			{
			rowNumber[0] = 0;
			if (distanceFromTop > 0)
				rowOffset[0] = distanceFromTop;
			else
				rowOffset[0] = 0;
			return;
			}
		rowNumber[0] = -1;
		rowOffset[0] = 0;
		for (int i = 0; i <= topRowOfLastVisibleScreen; ++i)
			{
			MiCoord rowYmax = tableLayout.getSingleRowBounds(i, 0, 0, tmpBounds).getYmax();
			if (distanceFromTop < tableYmax - rowYmax)
				{
				if (i == 0)
					{
					lastRowYmax = rowYmax;
					i = 1;
					}
				rowOffset[0] = distanceFromTop - (tableYmax - lastRowYmax);
				rowOffset[0] = Math.round(rowOffset[0]);
				rowNumber[0] = i - 1;
				break;
				}
			lastRowYmax = rowYmax;
			}
		if (rowNumber[0] == -1)
			{
			rowNumber[0] = topRowOfLastVisibleScreen;
			rowOffset[0] = 0;
			}
		}

	protected	MiDistance	getChangeInVerticalOffset(
						int oldRowNumber, 
						MiDistance oldOffset, 
						int newRowNumber, 
						MiDistance newOffset)
		{
		MiCoord oldRowYmax = tableLayout.getSingleRowBounds(oldRowNumber, 0,0,tmpBounds).getYmax();
		MiCoord newRowYmax = tableLayout.getSingleRowBounds(newRowNumber, 0,0,tmpBounds).getYmax();
		return(Math.round(oldRowYmax - newRowYmax + newOffset - oldOffset));
		}
	public		int		getContextCursor(MiBounds area)	
		{
		int cursor = tableHeaderAndFooterManager.getContextCursor(area);
		if (cursor != Mi_DEFAULT_CURSOR)
			return(cursor);
		return(super.getContextCursor(area));
		}
	public		int		calcNumberOfVisibleColumns()
		{
		if (totalNumberOfColumns == 0)
			return(0);

		MiDistance visibleWidth = getContentsBounds(tmpBounds).getWidth();
		int numVisible = totalNumberOfColumns;

		MiDistance width = -scrollX;
		MiDistance alleyHSpacing = getAlleyHSpacing();
		for (int i = leftVisibleColumn; i < totalNumberOfColumns; ++i)
			{
			MiDistance columnWidth 
				= tableLayout.getSingleColumnBounds(i, 0, 0, tmpBounds).getWidth();
			if (columnWidth > 0)
				{
				width += columnWidth;
				if (width >= visibleWidth)
					{
					numVisible = i - leftVisibleColumn + 1;
					if ((hAlignmentPolicy == Mi_LINE_BY_LINE_POLICY) && (scrollX > 0))
						numVisible -= 1;

					// If top row is really large...
					if (numVisible <= 0)
						numVisible = 1;
					break;
					}
				width += alleyHSpacing;
				}
			}
		return(numVisible);
		}
 	protected	void		getColumnAndHorizontalOffset(
						double position, 
						int[] columnNumber, 
						MiDistance[] columnOffset)
		{
		int leftColumnOfLastVisibleScreen = getLeftColumnOfLastVisibleScreen();

		MiDistance totalWidth = tableLayout.getTotalContentsBounds(tmpBounds).getWidth();
		MiDistance visibleWidth = getContentsBounds(tmpBounds).getWidth();

		MiCoord tableXmin = 0;
		MiCoord lastColumnXmin = 0;
		MiDistance distanceFromLeft = position * (totalWidth - visibleWidth);

		if (leftColumnOfLastVisibleScreen == 0)
			{
			columnNumber[0] = 0;
			columnOffset[0] = distanceFromLeft;
			return;
			}
		columnNumber[0] = -1;
		columnOffset[0] = 0;
		for (int i = 0; i <= leftColumnOfLastVisibleScreen; ++i)
			{
			MiCoord colXmin = tableLayout.getSingleColumnBounds(i, 0, 0, tmpBounds).getXmin();
			if (distanceFromLeft < colXmin - tableXmin)
				{
				if (i == 0)
					{
					lastColumnXmin = colXmin;
					i = 1;
					}
				columnOffset[0] = distanceFromLeft - (lastColumnXmin - tableXmin);
				columnOffset[0] = Math.round(columnOffset[0]);
				columnNumber[0] = i - 1;
				break;
				}
			lastColumnXmin = colXmin;
			}
		if (columnNumber[0] == -1)
			{
			columnNumber[0] = leftColumnOfLastVisibleScreen;
			columnOffset[0] = 0;
			}
		}

	protected	MiDistance	getChangeInHorizontalOffset(
						int oldColumnNumber, 
						MiDistance oldOffset, 
						int newColumnNumber, 
						MiDistance newOffset)
		{
		MiCoord oldColumnXmin = tableLayout.getSingleColumnBounds(
							oldColumnNumber, 0, 0, tmpBounds).getXmin();
		MiCoord newColumnXmin = tableLayout.getSingleColumnBounds(
							newColumnNumber, 0, 0, tmpBounds).getXmin();
		return(Math.round(newColumnXmin - oldColumnXmin + newOffset - oldOffset));
		}
	}


class MiTableLayout extends MiLayout
	{
	private		MiDistance	estimatedEmptyRowHeight		= 20;
	private		MiDistance	estimatedEmptyColumnWidth	= 40;

	private		MiTable		table;

	private		int		startRow;
	private		int		endRow;
	private		int		startColumn;
	private		int		endColumn;

	private		int		numberOfRows;
	private		int		numberOfColumns;

	private		MiDistance[]	rowHeights;
	private		MiDistance[]	columnWidths;

	private		MiDistance	rowHeaderHeight;
	private		MiDistance	rowFooterHeight;
	private		MiDistance	columnHeaderWidth;
	private		MiDistance	columnFooterWidth;

	private		MiBounds[]	cellBounds;

	private		MiBounds[]	rowHeaderBounds;
	private		MiBounds[]	rowFooterBounds;
	private		MiBounds[]	columnHeaderBounds;
	private		MiBounds[]	columnFooterBounds;



	public				MiTableLayout()
		{
		}
					/**------------------------------------------------------
	 				 * Sets the target to lay out. 
	 				 * @param part		the target MiTable to lay out	
	 				 * @overrides 		MiLayout#setTarget
					 *------------------------------------------------------*/
	public		void		setTarget(MiPart target)
		{
		this.table = (MiTable )target;
		super.setTarget(target);
		}

	/**
	* When all rows are empty this value is used to calculate the preferred 
	* height (by multiplying the table's preferredNumberOfDisplayedRows times
	* this value). When all rows are not empty the height of the first row
	* is used for this calculation.
	*/
 	public		void		setEmptyRowHeight(MiDistance height)
		{
		estimatedEmptyRowHeight = height;
		}
	public		MiDistance	getEmptyRowHeight()
		{
		return(estimatedEmptyRowHeight);
		}

	/**
	* When all columns are empty this value is used to calculate the preferred 
	* width (by multiplying the table's preferredNumberOfDisplayedColumns times
	* this value). When all columns are not empty the width of the first column
	* is used for this calculation.
	*/
 	public		void		setEmptyColumnWidth(MiDistance width)
		{
		estimatedEmptyColumnWidth = width;
		}
	public		MiDistance	getEmptyColumnWidth()
		{
		return(estimatedEmptyColumnWidth);
		}

	public		MiBounds	getSingleRowBounds(int rowNumber, int firstColumn, int lastColumn, MiBounds bounds)
		{
		if (lastColumn == -1)
			lastColumn = numberOfColumns - 1;

		if ((rowNumber == MiTable.ROW_HEADER_NUMBER) || (rowNumber == MiTable.ROW_FOOTER_NUMBER))
			{
			return(getRowBounds(rowNumber, firstColumn, lastColumn, bounds));
			}
		if ((cellBounds == null) || (cellBounds.length == 0))
			{
			bounds.zeroOut();
			return(bounds);
			}

		// ---------------------------------------------------------------
		// If rowNumber is too big cause this has not been laid out since
		// rows and or columns have been added, then do the best we can
		// ---------------------------------------------------------------
		if (rowNumber >= numberOfRows)
			rowNumber = numberOfRows - 1;

		MiCoord ymin = -rowHeights[0];
		for (int i = 0; i < rowNumber; ++i)
			{
			if (rowHeights[i + 1] != 0)
				ymin -= rowHeights[i + 1] + table.getAlleyVSpacing();
			}

		int rowOffset = (rowNumber - startRow) * numberOfColumns;
		bounds.setBounds(
			cellBounds[rowOffset + firstColumn - startColumn].xmin, 
			ymin, 
			cellBounds[rowOffset + lastColumn - startColumn].xmax, 
			ymin + rowHeights[rowNumber]);

		return(bounds);
		}
	public		MiBounds	getSingleColumnBounds(int columnNumber, int firstRow, int lastRow, MiBounds bounds)
		{
		if (lastRow == -1)
			lastRow = numberOfRows - 1;

		if ((columnNumber == MiTable.COLUMN_HEADER_NUMBER) 
			|| (columnNumber == MiTable.COLUMN_FOOTER_NUMBER))
			{
			return(getColumnBounds(columnNumber, firstRow, lastRow, bounds));
			}
		if ((cellBounds == null) || (cellBounds.length == 0))
			{
			bounds.zeroOut();
			return(bounds);
			}

		// ---------------------------------------------------------------
		// If rowNumber is too big cause this has not been laid out since
		// rows and or columns have been added, then do the best we can
		// ---------------------------------------------------------------
		if (columnNumber >= numberOfColumns)
			columnNumber = numberOfColumns - 1;

		MiCoord xmax = columnWidths[0];
		for (int i = 0; i < columnNumber; ++i)
			{
			if (columnWidths[i + 1] != 0)
				xmax += columnWidths[i + 1] + table.getAlleyHSpacing();
			}

		int columnOffset = columnNumber - startColumn;
		bounds.setBounds(
			xmax - columnWidths[columnNumber], 
			cellBounds[(firstRow - startRow) * numberOfColumns + columnOffset].ymin, 
			xmax, 
			cellBounds[(lastRow - startRow) * numberOfColumns + columnOffset].ymax); 
		return(bounds);
		}
	public		MiBounds	getRowBounds(int rowNumber, int firstColumn, int lastColumn, MiBounds bounds)
		{
		try	{
			if (lastColumn == -1)
				lastColumn = numberOfColumns - 1;

			if (rowNumber == MiTable.ROW_HEADER_NUMBER)
				{
				bounds.copy(rowHeaderBounds[firstColumn - startColumn]);
				if (firstColumn != lastColumn)
					bounds.union(rowHeaderBounds[lastColumn - startColumn]);
				}
			else if (rowNumber == MiTable.ROW_FOOTER_NUMBER)
				{
				bounds.copy(rowFooterBounds[firstColumn - startColumn]);
				if (firstColumn != lastColumn)
					bounds.union(rowFooterBounds[lastColumn - startColumn]);
				}
			else if (firstColumn == MiTable.COLUMN_HEADER_NUMBER)
				{
				bounds.copy(columnHeaderBounds[rowNumber - startRow]);
				}
			else if (firstColumn == MiTable.COLUMN_FOOTER_NUMBER)
				{
				bounds.copy(columnFooterBounds[rowNumber - startRow]);
				}
			else
				{
				int rowOffset = (rowNumber - startRow) * numberOfColumns;
				bounds.copy(cellBounds[rowOffset + firstColumn - startColumn]);
				if (firstColumn != lastColumn)
					bounds.union(cellBounds[rowOffset + lastColumn - startColumn]);
				}
			}
		catch (Exception e)
			{
			// Not layout'd yet...
			bounds.reverse();
			}
		return(bounds);
		}
	public		MiBounds	getColumnBounds(int columnNumber, int firstRow, int lastRow, MiBounds bounds)
		{
		try	{
			if (lastRow == -1)
				lastRow = numberOfRows - 1;

			if (columnNumber == MiTable.COLUMN_HEADER_NUMBER)
				{
				bounds.copy(columnHeaderBounds[firstRow - startRow]);
				if (firstRow != lastRow)
					bounds.union(columnHeaderBounds[lastRow - startRow]);
				}
			else if (columnNumber == MiTable.COLUMN_FOOTER_NUMBER)
				{
				bounds.copy(columnFooterBounds[firstRow - startRow]);
				if (firstRow != lastRow)
					bounds.union(columnFooterBounds[lastRow - startRow]);
				}
			else if (firstRow == MiTable.ROW_HEADER_NUMBER)
				{
				bounds.copy(rowHeaderBounds[columnNumber - startColumn]);
				}
			else if (firstRow == MiTable.ROW_FOOTER_NUMBER)
				{
				bounds.copy(rowFooterBounds[columnNumber - startColumn]);
				}
			else
				{
				int columnOffset = columnNumber - startColumn;
				bounds.copy(cellBounds[(firstRow - startRow) * numberOfColumns + columnOffset]);
				if (firstRow != lastRow)
					{
					bounds.union(cellBounds
						[(lastRow - startRow) * numberOfColumns + columnOffset]);
					}
				}
			}
		catch (Exception e)
			{
			// Not layout'd yet...
			bounds.reverse();
			}
		return(bounds);
		}
	public		MiBounds	getCellBounds(int rowNumber, int columnNumber, MiBounds bounds)
		{
		try	{
			if (rowNumber == MiTable.ROW_HEADER_NUMBER)
				{
				bounds.copy(rowHeaderBounds[columnNumber - startColumn]);
				}
			else if (rowNumber == MiTable.ROW_FOOTER_NUMBER)
				{
				bounds.copy(rowFooterBounds[columnNumber - startColumn]);
				}
			else if (columnNumber == MiTable.COLUMN_HEADER_NUMBER)
				{
				bounds.copy(columnHeaderBounds[rowNumber - startRow]);
				}
			else if (columnNumber == MiTable.COLUMN_FOOTER_NUMBER)
				{
				bounds.copy(columnFooterBounds[rowNumber - startRow]);
				}
			else
				{
				bounds.copy(cellBounds[
					(rowNumber - startRow) * numberOfColumns 
					+ columnNumber - startColumn]);
				}
			}
		catch (Exception e)
			{
			// Not layout'd yet...
			bounds.reverse();
			}
		return(bounds);
		}
	protected	MiBounds	getTotalContentsBounds(MiBounds b)
		{
		if ((numberOfRows == 0) || (numberOfColumns == 0) || (cellBounds == null)
			|| (cellBounds.length < (numberOfRows - 1) * numberOfColumns + 1))
			{
			b.reverse();
			}
		else
			{
			b.xmin = cellBounds[0].xmin;
			b.ymax = cellBounds[0].ymax;
			b.xmax = cellBounds[numberOfColumns - 1].xmax;
			b.ymin = cellBounds[(numberOfRows - 1) * numberOfColumns].ymin;
			}
		return(b);
		}

	protected	MiDistance	getRowHeaderHeight()
		{
		return(rowHeaderHeight);
		}
	protected	MiDistance	getRowFooterHeight()
		{
		return(rowFooterHeight);
		}
	protected	MiDistance	getColumnHeaderWidth()
		{
		return(columnHeaderWidth);
		}
	protected	MiDistance	getColumnFooterWidth()
		{
		return(columnFooterWidth);
		}
	public		boolean		isIndependantOfTargetPosition()
		{
		return(false);
		}
	protected	void		calcMinimumSize(MiSize size)
		{
		calcLayoutQuantities();

		calcPreferredSizeOfContents(size, true);

		size.setWidth(size.getWidth() + columnHeaderWidth + columnFooterWidth);
		size.setHeight(size.getHeight() + rowHeaderHeight + rowFooterHeight);
		size.addMargins(table.getSectionMargins());
		size.addMargins(table.getContentsMargins());
		size.addHeight(table.getRowHeaderMargins().getHeight());
		size.addHeight(table.getRowFooterMargins().getHeight());
		size.addWidth(table.getColumnHeaderMargins().getWidth());
		size.addWidth(table.getColumnFooterMargins().getWidth());
		}

	protected	void		calcPreferredSize(MiSize size)
		{
//MiDebug.println(this + "MiTableLayout.calcPreferredSize");
		//calcLayoutQuantities();

		calcPreferredSizeOfContents(size, false);
//MiDebug.println(this + "MiTableLayout.calcPreferredSize  of contents = " + size);
		size.setWidth(size.getWidth() + columnHeaderWidth + columnFooterWidth);
		size.setHeight(size.getHeight() + rowHeaderHeight + rowFooterHeight);
		size.addMargins(table.getSectionMargins());
		size.addMargins(table.getContentsMargins());
		size.addHeight(table.getRowHeaderMargins().getHeight());
		size.addHeight(table.getRowFooterMargins().getHeight());
		size.addWidth(table.getColumnHeaderMargins().getWidth());
		size.addWidth(table.getColumnFooterMargins().getWidth());
//MiDebug.println(this + "rowHeaderHeight = " + rowHeaderHeight);
//MiDebug.println(this + "rowFooterHeight = " + rowFooterHeight);
//MiDebug.println(this + "table.getSectionMargins() = " + table.getSectionMargins());
//MiDebug.println(this + "table.getContentsMargins() = " + table.getContentsMargins());
//MiDebug.println(this + "table.getRowHeaderMargins().getHeight() = " + table.getRowHeaderMargins().getHeight());
//MiDebug.println(this + "table.getRowFooterMargins().getHeight() = " + table.getRowFooterMargins().getHeight());
//MiDebug.println(this + "(for table=" + table + ")MiTableLayout.calcPreferredSize = " + size);
		}

	protected	void		calcPreferredSizeOfContents(MiSize size, boolean calcMinimumSize)
		{
		calcLayoutQuantities();

		int 		startCol = table.getLeftVisibleColumn() - startColumn;
		int 		endCol;
		MiDistance 	width = 0;
		MiDistance 	estimatedWidth;
		MiDistance 	alleyHSpacing = table.getAlleyHSpacing();

		int lastLoadedColumn = table.getLeftLoadedColumn() + table.getNumberOfLoadedColumns();

		if (calcMinimumSize)
			endCol = startCol + table.getMinimumNumberOfVisibleColumns();
		else if (table.getPreferredNumberOfVisibleColumns() == -1)
			endCol = table.getNumberOfLoadedColumns();
		else
			endCol = startCol + table.getPreferredNumberOfVisibleColumns();

		if ((table.getMaximumNumberOfVisibleColumns() != -1)
			&& (endCol - startCol > table.getMaximumNumberOfVisibleColumns()))
			{
			endCol = startCol + table.getMaximumNumberOfVisibleColumns();
			}

		if (table.getNumberOfLoadedColumns() == 0)
			estimatedWidth = estimatedEmptyColumnWidth;
		else
			estimatedWidth = columnWidths[0];

		if (endCol > lastLoadedColumn)
			{
			width += (estimatedWidth + alleyHSpacing) * (endCol - lastLoadedColumn);
			endCol = lastLoadedColumn;
			}

		for (int i = startCol; i < endCol; ++i)
			{
			// If the column is visible...
			if (columnWidths[i] > 0)
				width += columnWidths[i] + alleyHSpacing;
			else
				width += estimatedWidth + alleyHSpacing;
			}
		width -= alleyHSpacing;


		int 		firstRow = table.getTopVisibleRow() - startRow;
		MiDistance 	height = 0;
		MiDistance 	estimatedHeight;
		int 		endRow;
		MiDistance 	alleyVSpacing = table.getAlleyVSpacing();

		int lastLoadedRow = table.getTopLoadedRow() + table.getNumberOfLoadedRows();
		if (calcMinimumSize)
			endRow = firstRow + table.getMinimumNumberOfVisibleRows();
		else if (table.getPreferredNumberOfVisibleRows() == -1)
			endRow = lastLoadedRow;
		else
			endRow = firstRow + table.getPreferredNumberOfVisibleRows();

		if ((table.getMaximumNumberOfVisibleRows() != -1)
			&& (endRow - firstRow > table.getMaximumNumberOfVisibleRows()))
			{
			endRow = firstRow + table.getMaximumNumberOfVisibleRows();
			}


		if (table.getNumberOfLoadedRows() == 0)
			estimatedHeight = estimatedEmptyRowHeight;
		else
			estimatedHeight = rowHeights[0];

//MiDebug.println(this + "For table: " + table);
//MiDebug.println(this + "table.getPreferredNumberOfVisibleRows()=" + table.getPreferredNumberOfVisibleRows());

//MiDebug.println(this + "MiTableLayout.calcPreferredSize estimated Height = " + estimatedHeight);
//MiDebug.println(this + "MiTableLayout.calcPreferredSize estimated Height estimatedEmptyRowHeight = " + estimatedEmptyRowHeight);

		if (endRow > lastLoadedRow)
			{
			if (endRow - table.getNumberOfLoadedRows() > 0)
				{
				height += (estimatedHeight + alleyVSpacing)
						* (endRow - table.getNumberOfLoadedRows());
				}
			endRow = lastLoadedRow;
			}
//MiDebug.println(this + "MiTableLayout.calcPreferredSize initial height = " + height);
//MiDebug.println(this + "MiTableLayout.calcPreferredSize num rows = " + (endRow - firstRow));

		for (int i = firstRow; i < endRow; ++i)
			{
			// If the row is visible...
			if (rowHeights[i] > 0)
				height += rowHeights[i] + alleyVSpacing;
			else
				height += estimatedHeight + alleyVSpacing;
			}
		height -= alleyVSpacing;
		size.setSize(width, height);
//MiDebug.println(this + "(for table=" + table + ")MiTableLayout.calcPreferredSize now height = " + height);

		MiTableCell defaults = table.getContentAreaDefaults();
		if ((defaults != null) && (defaults.getFixedHeight() > 0))
			size.setHeight(defaults.getFixedHeight());
		if ((defaults != null) && (defaults.getFixedWidth() > 0))
			size.setWidth(defaults.getFixedWidth());
		}

	protected	MiBounds	getRowHeaderBounds(MiBounds b)
		{
		table.getInnerBounds(b);
		b.ymax = b.ymax - table.getRowHeaderMargins().top;
		b.ymin = b.ymax - rowHeaderHeight;
		b.xmin += columnHeaderWidth + table.getColumnHeaderMargins().getWidth()
			+ table.getSectionMargins().left;
		b.xmax -= columnFooterWidth + table.getColumnFooterMargins().getWidth()
			+ table.getSectionMargins().right;
		return(b);
		}

	protected	MiBounds	getRowFooterBounds(MiBounds b)
		{
		table.getInnerBounds(b);
		b.ymin = b.ymin + table.getRowFooterMargins().bottom;
		b.ymax = b.ymin + rowFooterHeight;
		b.xmin += columnHeaderWidth + table.getColumnHeaderMargins().getWidth()
			+ table.getSectionMargins().left;
		b.xmax -= columnFooterWidth + table.getColumnFooterMargins().getWidth()
			+ table.getSectionMargins().right;
		return(b);
		}

	protected	MiBounds	getColumnHeaderBounds(MiBounds b)
		{
		table.getInnerBounds(b);
		b.xmin = b.xmin + table.getColumnHeaderMargins().left;
		b.xmax = b.xmin + columnHeaderWidth;
		b.ymin += rowFooterHeight + table.getRowFooterMargins().getHeight()
			+ table.getSectionMargins().bottom; // + table.getContentsMargins().bottom;
		b.ymax -= rowHeaderHeight + table.getRowHeaderMargins().getHeight()
			+ table.getSectionMargins().top; // + table.getContentsMargins().top;
		return(b);
		}

	protected	MiBounds	getColumnFooterBounds(MiBounds b)
		{
		table.getInnerBounds(b);
		b.xmax = b.xmax - table.getColumnHeaderMargins().right;
		b.xmin = b.xmax - columnFooterWidth;
		b.ymin += rowFooterHeight + table.getRowFooterMargins().getHeight()
			+ table.getSectionMargins().bottom; // + table.getContentsMargins().left;
		b.ymax -= rowHeaderHeight + table.getRowHeaderMargins().getHeight()
			+ table.getSectionMargins().top ; //+ table.getContentsMargins().right;
		return(b);
		}


	protected	void		calcLayoutQuantities()
		{
		startRow = table.getTopLoadedRow();
		endRow = startRow + table.getNumberOfLoadedRows();
		startColumn = table.getLeftLoadedColumn();
		endColumn = startColumn + table.getNumberOfLoadedColumns();

		numberOfRows = endRow - startRow;
		numberOfColumns = endColumn - startColumn;

		rowHeights = new MiDistance[numberOfRows];
		columnWidths = new MiDistance[numberOfColumns];

		// -------------------------------------
		// Determine row and column sizes
		// -------------------------------------
		for (int rowNum = 0; rowNum < numberOfRows; ++rowNum)
			{
			rowHeights[rowNum] = calcHeightOfRow(rowNum);
//MiDebug.println("rowHeights[0] = " + rowHeights[0]);
			}

		for (int colNum = 0; colNum < numberOfColumns; ++colNum)
			{
			columnWidths[colNum] = calcWidthOfColumn(colNum);
			}

		// -------------------------------------
		// Set size and position of headers and footers
		// -------------------------------------
		rowHeaderHeight = 0;
		rowFooterHeight = 0;
		columnHeaderWidth = 0;
		columnFooterWidth = 0;
		if (table.hasRowHeaders())
			rowHeaderHeight = calcHeightOfRow(MiTable.ROW_HEADER_NUMBER);
		if (table.hasRowFooters())
			rowFooterHeight = calcHeightOfRow(MiTable.ROW_FOOTER_NUMBER);
		if (table.hasColumnHeaders())
			columnHeaderWidth = calcWidthOfColumn(MiTable.COLUMN_HEADER_NUMBER);
		if (table.hasColumnFooters())
			columnFooterWidth = calcWidthOfColumn(MiTable.COLUMN_FOOTER_NUMBER);
		}




	protected	void		doLayout()
		{
//MiDebug.printStackTrace(this + " doLayout");
//MiDebug.println("\n\n\nMiTableLAyout : " + table + " ---- do layout\n\n\n");
//MiDebug.println("numberOfRows = " + numberOfRows);
//System.out.println("numberOfColumns = " + numberOfColumns);
//System.out.println("Inner Bounds = " + table.getInnerBounds());

		MiBounds targetBounds = table.getInnerBounds();


		calcLayoutQuantities();

		MiMargins 	contentsMargins 	= table.getContentsMargins();
		MiMargins 	sectionMargins 		= table.getSectionMargins();
		MiBounds 	contentsBounds 		= table.getContentsBounds(new MiBounds());
		MiMargins 	rowHeaderMargins 	= table.getRowHeaderMargins();
		MiMargins 	rowFooterMargins 	= table.getRowFooterMargins();
		MiMargins 	columnHeaderMargins 	= table.getColumnHeaderMargins();
		MiMargins 	columnFooterMargins 	= table.getColumnFooterMargins();

		MiDistance 	alleyHSpacing 		= table.getAlleyHSpacing();
		MiDistance 	alleyVSpacing 		= table.getAlleyVSpacing();

		// -------------------------------------
		// Calc cell sizes
		// -------------------------------------
		MiTableCell cell;
		cellBounds = allocBoundsArray(cellBounds, numberOfRows * numberOfColumns);
		MiCoord x = 0;
		MiCoord y = targetBounds.getYmax()
				- rowHeaderHeight 
				- rowHeaderMargins.getHeight()
				- contentsMargins.top
				- sectionMargins.top;

		MiCoord xLeft = targetBounds.getXmin()
				+ columnHeaderWidth 
				+ columnHeaderMargins.getWidth()
				+ contentsMargins.left 
				+ sectionMargins.left;
		int index = 0;
		for (int rowNum = 0; rowNum < numberOfRows; ++rowNum)
			{
			x = xLeft;
			for (int colNum = 0; colNum < numberOfColumns; ++colNum)
				{
				cellBounds[index++].setBounds(
					x, 
					y - rowHeights[rowNum],
					x + columnWidths[colNum],
					y);
				if (columnWidths[colNum] > 0)
					x += columnWidths[colNum] + alleyHSpacing;
				}
			if (rowHeights[rowNum] > 0)
				y -= rowHeights[rowNum] + alleyVSpacing;
			}

		// -------------------------------------
		// Adjust for any rows or columns with sizing set to EXPAND_TO_FILL
		// -------------------------------------
		adjustRowAndColumnSetToExpandToFill(x - alleyHSpacing, y + alleyVSpacing, contentsBounds);

		// -------------------------------------
		// Set size and position of cells
		// -------------------------------------
		MiTableCell lastCellLayouted = null;
		for (int rowNum = startRow; rowNum < endRow; ++rowNum)
			{
			for (int colNum = startColumn; colNum < endColumn; ++colNum)
				{
				cell = table.getCell(rowNum, colNum);
				if (cell != lastCellLayouted)
					{
					layoutCell(cell);
					lastCellLayouted = cell;
					}
				}
			}
		// -------------------------------------
		// Calc Row header and footer cell sizes
		// -------------------------------------
		index = 0;
		if ((table.hasRowHeaders()) || (table.hasRowFooters()))
			{
			rowHeaderBounds = allocBoundsArray(rowHeaderBounds, numberOfColumns);
			rowFooterBounds = allocBoundsArray(rowFooterBounds, numberOfColumns);

			x = targetBounds.getXmin()
				+ columnHeaderWidth 
				+ columnHeaderMargins.getWidth()
				//+ contentsMargins.left 
				+ sectionMargins.left;

			MiCoord headerY = targetBounds.getYmax() - rowHeaderMargins.top;
			MiCoord footerY = targetBounds.getYmax() 
					- rowHeaderHeight 
					- contentsBounds.getHeight() 
					- sectionMargins.getHeight() 
					- contentsMargins.getHeight() 
					- rowHeaderMargins.getHeight() 
					- rowFooterMargins.top;
 
			for (int colNum = 0; colNum < numberOfColumns; ++colNum)
				{
				rowHeaderBounds[index].setBounds(
					x, 
					headerY - rowHeaderHeight, 
					x + columnWidths[colNum],
					headerY);

				rowFooterBounds[index].setBounds(
					x, 
					footerY - rowFooterHeight, 
					x + columnWidths[colNum],
					footerY);

				if (columnWidths[colNum] > 0)
					x += columnWidths[colNum] + alleyHSpacing;
				if (colNum == numberOfColumns - 1)
					{
					rowHeaderBounds[index].xmax += contentsMargins.right;
					rowFooterBounds[index].xmax += contentsMargins.right;
					}
				++index;
				}
			// -------------------------------------
			// Set size and position of cells
			// -------------------------------------
			for (int colNum = 0; colNum < numberOfColumns; ++colNum)
				{
				if (table.hasRowHeaders())
					layoutCell(table.getCell(MiTable.ROW_HEADER_NUMBER, startColumn + colNum));
				if (table.hasRowFooters())
					layoutCell(table.getCell(MiTable.ROW_FOOTER_NUMBER, startColumn + colNum));
				}
			}

		// -------------------------------------
		// Calc Column header and footer cell sizes
		// -------------------------------------
		index = 0;
		if ((table.hasColumnHeaders()) || (table.hasColumnFooters()))
			{
			columnHeaderBounds = allocBoundsArray(columnHeaderBounds, numberOfRows);
			columnFooterBounds = allocBoundsArray(columnFooterBounds, numberOfRows);

			y = targetBounds.getYmax() 
				- rowHeaderHeight 
				- rowHeaderMargins.getHeight()
				//- contentsMargins.top
				- sectionMargins.top;

			MiCoord headerX = targetBounds.getXmin()
				+ columnHeaderMargins.left;

			MiCoord footerX = targetBounds.getXmin()
				+ columnHeaderWidth
				+ contentsMargins.getWidth()
				+ contentsBounds.getWidth()
				+ sectionMargins.getWidth()
				+ columnHeaderMargins.getWidth()
				+ columnFooterMargins.left;

			for (int rowNum = 0; rowNum < numberOfRows; ++rowNum)
				{
				columnHeaderBounds[index].setBounds(
					headerX, 
					y - rowHeights[rowNum],
					headerX + columnHeaderWidth,
					y);

				columnFooterBounds[index].setBounds(
					footerX, 
					y - rowHeights[rowNum],
					footerX + columnFooterWidth,
					y);

				if (rowHeights[rowNum] > 0)
					y -= rowHeights[rowNum] + alleyVSpacing;
				if (rowNum == numberOfRows - 1)
					{
					rowHeaderBounds[index].ymin -= contentsMargins.bottom;
					rowFooterBounds[index].ymin -= contentsMargins.bottom;
					}
				++index;
				}
			// -------------------------------------
			// Set size and position of cells
			// -------------------------------------
			for (int rowNum = 0; rowNum < numberOfRows; ++rowNum)
				{
				if (table.hasColumnHeaders())
					layoutCell(table.getCell(rowNum + startRow, MiTable.COLUMN_HEADER_NUMBER));
				if (table.hasColumnFooters())
					layoutCell(table.getCell(rowNum + startRow, MiTable.COLUMN_FOOTER_NUMBER));
				}
			}
		//table.refreshBounds();
		table.invalidateArea(table.getDrawBounds(new MiBounds()));
		table.updateForNewLayout();
//MiDebug.println("done with layout");
		}
	protected	MiBounds[]	allocBoundsArray(MiBounds[] array, int newLength)
		{
		if ((array != null) && (array.length != newLength))
			{
			for (int i = 0; i < array.length; ++i)
				MiBounds.freeBounds(array[i]);
			array = null;
			}
		if (array == null)
			{
			array = new MiBounds[newLength];
			for (int i = 0; i < newLength; ++i)
				array[i] = MiBounds.newBounds();
			}
		return(array);
		}

	protected	void		layoutCell(MiTableCell cell)
		{
if (cell == null)
{
MiDebug.println("Error: This Table has null cell: " + table);
}

		if (cell == null)
			return;


		int 		rowNum = cell.rowNumber;
		int 		colNum = cell.columnNumber;
		MiBounds[] 	cellBoundsArray;
		int		index;
		boolean		onlyRowExpansionAllowed		= false;
		boolean		onlyColumnExpansionAllowed	= false;


		if (rowNum == MiTable.ROW_HEADER_NUMBER)
			{
			index = colNum - startColumn;
			cellBoundsArray = rowHeaderBounds;
			onlyRowExpansionAllowed = true;
			}
		else if (rowNum == MiTable.ROW_FOOTER_NUMBER)
			{
			index = colNum - startColumn;
			cellBoundsArray = rowFooterBounds;
			onlyRowExpansionAllowed = true;
			}
		else if (colNum == MiTable.COLUMN_HEADER_NUMBER)
			{
			index = rowNum - startRow;
			cellBoundsArray = columnHeaderBounds;
			onlyColumnExpansionAllowed = true;
			}
		else if (colNum == MiTable.COLUMN_FOOTER_NUMBER)
			{
			index = rowNum - startRow;
			cellBoundsArray = columnFooterBounds;
			onlyColumnExpansionAllowed = true;
			}
		else
			{
			index = rowNum * numberOfColumns + colNum;
			cellBoundsArray = cellBounds;
			}

		MiBounds tmpCellBounds = cellBoundsArray[index];

		if ((cell.numberOfRows > 1) && (onlyRowExpansionAllowed))
			{
			for (int i = 0; i < cell.numberOfRows - 1; ++i)
				tmpCellBounds.union(cellBoundsArray[index + i + 1]);
			for (int i = 0; i < cell.numberOfRows - 1; ++i)
				cellBoundsArray[index + i + 1] = tmpCellBounds;
			}
		else if ((cell.numberOfColumns > 1) && (onlyColumnExpansionAllowed))
			{
			for (int i = 0; i < cell.numberOfColumns - 1; ++i)
				tmpCellBounds.union(cellBoundsArray[index + i + 1]);
			for (int i = 0; i < cell.numberOfColumns - 1; ++i)
				cellBoundsArray[index + i + 1] = tmpCellBounds;
			}
		else
			{
			if (cell.numberOfRows > 1)
				{
				for (int i = 0; i < cell.numberOfRows - 1; ++i)
					tmpCellBounds.union(cellBoundsArray[index + (i+1) * numberOfColumns]);
//				for (int i = 0; i < cell.numberOfRows - 1; ++i)
//					cellBoundsArray[index + (i+1) * numberOfColumns] = tmpCellBounds;
				}
			if (cell.numberOfColumns > 1)
				{
				for (int i = 0; i < cell.numberOfColumns - 1; ++i)
					tmpCellBounds.union(cellBoundsArray[index + i + 1]);
//12-29-2002			for (int i = 0; i < cell.numberOfColumns - 1; ++i)
//					cellBoundsArray[index + i + 1] = tmpCellBounds;
				}
			}

		MiMargins margins = cell.insetMargins;
		MiTableCell defaults;
		if ((margins == null) && ((defaults = table.getColumnDefaults(colNum)) != null))
			margins = defaults.insetMargins;
		if ((margins == null) && ((defaults = table.getRowDefaults(rowNum)) != null))
			margins = defaults.insetMargins;
		if ((margins == null) && ((defaults = table.getTableWideDefaults()) != null)
			&& (rowNum != MiTable.ROW_HEADER_NUMBER)
			&& (rowNum != MiTable.ROW_FOOTER_NUMBER)
			&& (colNum != MiTable.COLUMN_HEADER_NUMBER)
			&& (colNum != MiTable.COLUMN_FOOTER_NUMBER))
			{
			margins = defaults.insetMargins;
			}

		if (cell.bounds == null)
			cell.bounds = new MiBounds();
		if (cell.innerBounds == null)
			cell.innerBounds = new MiBounds();

		cell.bounds.copy(tmpCellBounds);
		cell.innerBounds.copy(tmpCellBounds);
		if (margins != null)
			cell.innerBounds.subtractMargins(margins);

		// -------------------------------------
		// Adjust each cell graphics to appropriate place within cell...
		// -------------------------------------
		cell.layoutParts();
		}

	protected	MiDistance		calcHeightOfRow(int rowNum)
		{
		MiTableCell cell;
		MiDistance height = 0;
		MiDistance cellPrefHeight = 0;
		MiTableCell rowDefaults = table.getRowDefaults(rowNum);
		MiTableCell tableDefaults = table.getTableWideDefaults();

		if ((rowDefaults != null) && (!rowDefaults.isVisible()))
			{
			}
		else if ((rowDefaults != null) && (rowDefaults.fixedHeight > 0))
			{
			height = rowDefaults.fixedHeight;
			}
		else if ((tableDefaults != null) && (tableDefaults.fixedHeight > 0))
			{
			height = tableDefaults.fixedHeight;
			}
		else
			{
			for (int colNum = startColumn; colNum < endColumn; ++colNum)
				{
				cell = table.getCell(rowNum, colNum);
if (cell == null)
{
System.out.println("CELL not found at row = "  +rowNum + ", colNum = " + colNum);
System.out.println("endRow = "  +endRow);
System.out.println("endColumn = "  +endColumn);
continue;
}

				if (cell.isVisible())
					{
					if (cell.getNumberOfRows() > 1)
						{
						cellPrefHeight = calcCellPreferredHeightForSpecificRow(cell, rowNum, rowHeights);
						}
					else
						{
						cellPrefHeight = cell.getPreferredHeight();
						}

					if (height < cellPrefHeight)
						{
						height = cellPrefHeight;
						}
					}
				}
			cell = table.getCell(rowNum, MiTable.COLUMN_HEADER_NUMBER);
			if ((cell != null) && (cell.isVisible())
				&& ((cellPrefHeight = calcCellPreferredHeightForSpecificRow(cell, rowNum, rowHeights)) > height))
				{
				height = cellPrefHeight;
				}

			cell = table.getCell(rowNum, MiTable.COLUMN_FOOTER_NUMBER);
			if ((cell != null) && (cell.isVisible())
				&& ((cellPrefHeight = calcCellPreferredHeightForSpecificRow(cell, rowNum, rowHeights)) > height))
				{
				height = cellPrefHeight;
				}

// FIX: this needs to use the default margins iff cell has no margins
			if (height > 0)
				{
				if ((rowDefaults != null) && (rowDefaults.insetMargins != null))
					height += rowDefaults.insetMargins.getHeight();
				else if ((tableDefaults != null) && (tableDefaults.insetMargins != null))
					height += tableDefaults.insetMargins.getHeight();
				}
			}
		return(height);
		}

	protected	MiDistance		calcWidthOfColumn(int colNum)
		{
		MiTableCell cell;
		MiDistance width = 0;
		MiDistance cellPrefWidth = 0;
		MiTableCell colDefaults = table.getColumnDefaults(colNum);
		MiTableCell tableDefaults = table.getTableWideDefaults();

		if ((colDefaults != null) && (!colDefaults.isVisible()))
			{
			}
		else if ((colDefaults != null) && (colDefaults.fixedWidth > 0))
			{
			width = colDefaults.fixedWidth;
			}
		else if ((tableDefaults != null) && (tableDefaults.fixedWidth > 0))
			{
			width = tableDefaults.fixedWidth;
			}
		else
			{
			for (int rowNum = startRow; rowNum < endRow; ++rowNum)
				{
				cell = table.getCell(rowNum, colNum);
if (cell == null)
{
System.out.println("NO CELL FOUND AT: " + rowNum + ", " + colNum);
continue;
}

				if (cell.isVisible())
					{
					if (cell.getNumberOfColumns() > 1)
						{
						cellPrefWidth = calcCellPreferredWidthForSpecificColumn(cell, colNum, columnWidths);
						}
					else
						{
						cellPrefWidth = cell.getPreferredWidth();
						}

					if (width < cellPrefWidth)
						{
						width = cellPrefWidth;
						}
					}
				}
			cell = table.getCell(MiTable.ROW_HEADER_NUMBER, colNum);
			if ((cell != null) && (cell.isVisible()) 
				&& ((cellPrefWidth = calcCellPreferredWidthForSpecificColumn(cell, colNum, columnWidths)) > width))
				{
				width = cellPrefWidth;
				}

			cell = table.getCell(MiTable.ROW_FOOTER_NUMBER, colNum);
			if ((cell != null) && (cell.isVisible())
				&& ((cellPrefWidth = calcCellPreferredWidthForSpecificColumn(cell, colNum, columnWidths)) > width))
				{
				width = cellPrefWidth;
				}

			if (width > 0)
				{
				if ((colDefaults != null) && (colDefaults.insetMargins != null))
					width += colDefaults.insetMargins.getWidth();
				else if ((tableDefaults != null) && (tableDefaults.insetMargins != null))
					width += tableDefaults.insetMargins.getWidth();
				}
			}
		return(width);
		}
	protected	MiDistance		calcCellPreferredHeightForSpecificRow(MiTableCell cell, int rowNum, MiDistance[] rowHeights)
		{
		int numCellRows = cell.getNumberOfRows();
		if (numCellRows < 2)
			{
			return(cell.getPreferredHeight());
			}
		int cellStartRow = cell.getRowNumber();

		if (rowNum < cellStartRow + numCellRows - 1)
			{
			return(0);
			}

		MiDistance previousCellHeights = 0;
		for (int i = cellStartRow; i < cellStartRow + numCellRows - 1; ++i)
			{
			previousCellHeights += rowHeights[i];
			}
			
		return(cell.getPreferredHeight() - previousCellHeights);
		}
	protected	MiDistance		calcCellPreferredWidthForSpecificColumn(MiTableCell cell, int colNum, MiDistance[] columnWidths)
		{
		int numCellColumns = cell.getNumberOfColumns();
		if (numCellColumns < 2)
			{
			return(cell.getPreferredWidth());
			}
		int cellStartColumn = cell.getColumnNumber();

		if (colNum < cellStartColumn + numCellColumns - 1)
			{
			return(0);
			}

		MiDistance previousCellWidths = 0;
		for (int i = cellStartColumn; i < cellStartColumn + numCellColumns - 1; ++i)
			{
			previousCellWidths += columnWidths[i];
			}
			
		return(cell.getPreferredWidth() - previousCellWidths);
		}
	protected	void		adjustRowAndColumnSetToExpandToFill(MiCoord x, MiCoord y, MiBounds contentsBounds)
		{
		MiTableCell defaults;
		int index;
		if (y > contentsBounds.getYmin())
			{
			for (int rowNum = 0; rowNum < numberOfRows; ++rowNum)
				{
				defaults = table.getRowDefaults(rowNum);
				if ((defaults != null) 
					&& (defaults.rowVSizing == Mi_EXPAND_TO_FILL) && (defaults.fixedHeight == 0))
					{
					index = rowNum * numberOfColumns;
					MiDistance ty = y - contentsBounds.getYmin();
					rowHeights[rowNum] += ty;
					for (int colNum = 0; colNum < numberOfColumns; ++colNum)
						{
						cellBounds[index++].ymin -= ty;
						}
					++rowNum;
					for (; rowNum < numberOfRows; ++rowNum)
						{
						for (int colNum = 0; colNum < numberOfColumns; ++colNum)
							{
							cellBounds[index].ymax -= ty;
							cellBounds[index++].ymin -= ty;
							}
						}
					break;
					}
				}
			}

		if (x < contentsBounds.getXmax())
			{
			for (int colNum = 0; colNum < numberOfColumns; ++colNum)
				{
				defaults = table.getColumnDefaults(colNum);
				if ((defaults != null) 
					&& (defaults.columnHSizing == Mi_EXPAND_TO_FILL) && (defaults.fixedWidth == 0))
					{
					index = colNum;
					MiDistance tx = contentsBounds.getXmax() - x;
					columnWidths[colNum] += tx;
					for (int rowNum = 0; rowNum < numberOfRows; ++rowNum)
						{
						cellBounds[index].xmax += tx;
						index += numberOfColumns;
						}
					++colNum;
					for (; colNum < numberOfColumns; ++colNum)
						{
						index = colNum;
						for (int rowNum = 0; rowNum < numberOfRows; ++rowNum)
							{
							cellBounds[index].xmin += tx;
							cellBounds[index].xmax += tx;
							index += numberOfColumns;
							}
						}
					break;
					}
				}
			}
		}
	}

interface MiiBackgroundableGrid
	{
	void		invalidateArea();
	void		invalidateArea(MiBounds area);
	MiBounds	getRowBounds(int rowNum, int startColumn, int endColumn, MiBounds bounds);
	MiBounds	getColumnBounds(int columnNum, int startRow, int endRow, MiBounds bounds);
	MiBounds	getCellBounds(int rowNum, int columnNum, MiBounds bounds);
	int[]		getVisibleRows();
	int[]		getVisibleColumns();
	boolean		isCellIntersectingRowColumn(
				int cellRowNum, int cellColumnNum, int rowNum, int columnNum);
	}


