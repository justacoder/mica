
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

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiTableHeaderAndFooterManager extends MiEventHandler implements MiiTypes
	{
	public static 	 double		RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH	= 10;

	public static final String	SELECT_ROW_OR_COLUMN_COMMAND_NAME	= "SelectRowOrColumn";

	public static final String	TOGGLE_SORT_ORDER_COMMAND_NAME		= "ToggleSortOrder";

	public static final String	START_MOVE_ROW_COLUMN_COMMAND_NAME	= "StartMoveRowOrColumn";
	public static final String	MOVE_ROW_COLUMN_COMMAND_NAME		= "MoveRowOrColumn";
	public static final String	END_MOVE_ROW_COLUMN_COMMAND_NAME	= "EndMoveRowOrColumn";

	public static final String	START_RESIZE_ROW_COLUMN_COMMAND_NAME	= "StartResizeRowOrColumn";
	public static final String	RESIZE_ROW_COLUMN_COMMAND_NAME		= "ResizeRowOrColumn";
	public static final String	END_RESIZE_ROW_COLUMN_COMMAND_NAME	= "EndResizeRowOrColumn";

	private		MiTable		table;
	private		int		resizingRow			= -1;
	private		int		resizingColumn			= -1;
	private		int		movingRow			= -1;
	private		int		movingColumn			= -1;
	private		MiImage		movingRowImage			= null;
	private		MiImage		movingColumnImage		= null;
	private		MiPoint		movingImageCenterOffset		= new MiPoint();
	private		MiBounds	tmpBounds			= new MiBounds();

	private		boolean	 	resizableRows			= true;
	private		boolean	 	resizableColumns		= true;
	private		boolean	 	movableRows			= true;
	private		boolean	 	movableColumns			= true;
	private		boolean	 	sortableRows			= true;
	private		boolean	 	sortableColumns			= true;



	public				MiTableHeaderAndFooterManager()
		{
		addEventToCommandTranslation(TOGGLE_SORT_ORDER_COMMAND_NAME, 
			Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);

		addEventToCommandTranslation(START_MOVE_ROW_COLUMN_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(MOVE_ROW_COLUMN_COMMAND_NAME, 
			Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(END_MOVE_ROW_COLUMN_COMMAND_NAME, 
			Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);

		addEventToCommandTranslation(START_RESIZE_ROW_COLUMN_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(RESIZE_ROW_COLUMN_COMMAND_NAME, 
			Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(END_RESIZE_ROW_COLUMN_COMMAND_NAME, 
			Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		}
					/**------------------------------------------------------
	 				 * Sets the MiPart that this is assigned to. This is set
					 * when this is assigned to a part.
					 * @param part		the target part
					 * @implements		MiiEventHandler#setTarget
					 * @overrides		MiEventHandler#setTarget
					 *------------------------------------------------------*/
	public		void		setTarget(MiPart part)
		{
		table = (MiTable )part;
		}
	public		void		setResizableRows(boolean flag)
		{
		resizableRows = flag;
		}
	public		boolean		getResizableRows()
		{
		return(resizableRows);
		}

	public		void		setResizableColumns(boolean flag)
		{
		resizableColumns = flag;
		}
	public		boolean		getResizableColumns()
		{
		return(resizableColumns);
		}

	public		void		setMovableRows(boolean flag)
		{
		movableRows = flag;
		}
	public		boolean		getMovableRows()
		{
		return(movableRows);
		}

	public		void		setMovableColumns(boolean flag)
		{
		movableColumns = flag;
		}
	public		boolean		getMovableColumns()
		{
		return(movableColumns);
		}

	public		void		setSortableRows(boolean flag)
		{
		sortableRows = flag;
		}
	public		boolean		getSortableRows()
		{
		return(sortableRows);
		}

	public		void		setSortableColumns(boolean flag)
		{
		sortableColumns = flag;
		}
	public		boolean		getSortableColumns()
		{
		return(sortableColumns);
		}

	public		int		getContextCursor(MiBounds area)	
		{
		if (!isEnabled())
			return(Mi_DEFAULT_CURSOR);

		area = new MiBounds(area);
		area.setWidth(area.getWidth() * 2);
		area.setHeight(area.getHeight() * 2);
		if ((resizableColumns) && (getColumnToResize(area) != -1))
			{
//MiDebug.println("resizable columns");
			return(Mi_E_RESIZE_CURSOR);
			}

		if ((resizableRows) && (getRowToResize(area) != -1))
			{
//MiDebug.println("resizable rows");
			return(Mi_S_RESIZE_CURSOR);
			}

		if ((movableRows) || (movableColumns))
			{
			MiTableCell cell 
				= table.pickCellIncludingHeadersAndFooters(area);
			if (cell == null)
				return(Mi_DEFAULT_CURSOR);

			if ((movableRows)
				&& ((cell.columnNumber == MiTable.COLUMN_HEADER_NUMBER)
				|| (cell.columnNumber == MiTable.COLUMN_FOOTER_NUMBER)))
				{
				return(Mi_MOVE_CURSOR);
				}

			if ((movableColumns)
				&& ((cell.rowNumber == MiTable.ROW_HEADER_NUMBER)
				|| (cell.rowNumber == MiTable.ROW_FOOTER_NUMBER)))
				{
				return(Mi_MOVE_CURSOR);
				}
			}

		return(Mi_DEFAULT_CURSOR);
		}
	protected	int		getRowToResize(MiBounds area)
		{
		if (area.getWidth() < RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH)
			{
			area.setWidth(RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH);
			}
		if (area.getHeight() < RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH)
			{
			area.setHeight(RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH);
			}
		// Check if area overlaps a column (right-side) edge
		for (int i = 0; i < table.getNumberOfVisibleRows() - 1; ++i)
			{
			table.getRowBounds(i + table.getTopVisibleRow(), 0, -1, tmpBounds);
			if ((area.ymin < tmpBounds.ymin) && (area.ymax > tmpBounds.ymin))
				{
				int row = table.getTopVisibleRow() + i;
				MiTableCell defaults = table.getRowDefaults(row);
				if ((defaults != null) && (defaults.getAttributes().hasFixedHeight()))
					{
					return(-1);
					}
				return(row);
				}
			}
		return(-1);
		}
	protected	int		getColumnToResize(MiBounds area)
		{
		if (area.getWidth() < RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH)
			{
			area.setWidth(RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH);
			}
		if (area.getHeight() < RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH)
			{
			area.setHeight(RESIZABLE_ROW_AND_COLUMN_ZONE_WIDTH);
			}
//MiDebug.println("getColumnToResize: " + area);
		// Check if area overlaps a column (right-side) edge
		for (int i = 0; i < table.getNumberOfVisibleColumns() - 1; ++i)
			{
			table.getColumnBounds(i + table.getLeftVisibleColumn(), 0, -1, tmpBounds);
//MiDebug.println("Look at column # : " + i + table.getLeftVisibleColumn());
//MiDebug.println("with bounds = " + tmpBounds);
			if ((area.xmin < tmpBounds.xmax) && (area.xmax > tmpBounds.xmax))
				{
				int column = table.getLeftVisibleColumn() + i;
				MiTableCell defaults = table.getColumnDefaults(column);
				if ((defaults != null) && (defaults.getAttributes().hasFixedWidth()))
					{
					return(-1);
					}
//MiDebug.println("return column = " + column);
				return(column);
				}
			}
		return(-1);
		}
	public		int		processCommand()
		{
		//MiBounds cursorArea = event.worldMouseFootPrint;
		MiBounds cursorArea = event.getMouseFootPrint(new MiBounds());
		cursorArea.setWidth(cursorArea.getWidth() * 2);
		cursorArea.setHeight(cursorArea.getHeight() * 2);
		MiTableCell cell = table.pickCellIncludingHeadersAndFooters(cursorArea);
		if ((movingRow == -1) && (movingColumn == -1)
			&& (resizingRow == -1) && (resizingColumn == -1)
			&& (cell == null))
			{
			return(Mi_PROPOGATE_EVENT);
			}
		// Handle case where the events that trigger row/column move 
		// and row/column resize are the same
		if (isCommand(START_MOVE_ROW_COLUMN_COMMAND_NAME))
			{
			int cursorAppearance = getContextCursor(cursorArea);
			if ((movingRow != -1) || (movingColumn != -1))
				{
				return(Mi_PROPOGATE_EVENT);
				}
			if ((cursorAppearance == Mi_E_RESIZE_CURSOR)
				|| (cursorAppearance == Mi_S_RESIZE_CURSOR))
				{
				setCommand(START_RESIZE_ROW_COLUMN_COMMAND_NAME);
				}
			else if (cursorAppearance == Mi_DEFAULT_CURSOR)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			}
		else if ((isCommand(MOVE_ROW_COLUMN_COMMAND_NAME))
			&& ((resizingRow != -1) || (resizingColumn != -1)))
			{
			setCommand(RESIZE_ROW_COLUMN_COMMAND_NAME);
			}

//MiDebug.println("cmd = " + getCommand());
		// ---------------------------------------
		//	Resizing Rows and Columns
		// ---------------------------------------
		if (isCommand(START_RESIZE_ROW_COLUMN_COMMAND_NAME))
			{
			if ((resizingRow != -1) || (resizingColumn != -1))
				return(Mi_PROPOGATE_EVENT);

			if (resizableRows)
				{
				resizingRow = getRowToResize(cursorArea);
				}
			if ((resizingRow == -1) && (resizableColumns))
				{
				resizingColumn = getColumnToResize(cursorArea);
				}
			if ((resizingRow != -1) || (resizingColumn != -1))
				{
				event.editor.prependGrabEventHandler(this);
				return(Mi_CONSUME_EVENT);
				}
			}
		else if ((isCommand(RESIZE_ROW_COLUMN_COMMAND_NAME))
			|| (isCommand(MOVE_ROW_COLUMN_COMMAND_NAME)))
			{
			if (resizingRow != -1)
				{
				table.getRowBounds(resizingRow, 0, -1, tmpBounds);
				tmpBounds.setYmin(event.worldPt.y);
				if (tmpBounds.getHeight() < table.getMinimumRowHeight())
					tmpBounds.setHeight(table.getMinimumRowHeight());
				table.setRowHeight(resizingRow, tmpBounds.getHeight());
				return(Mi_CONSUME_EVENT);
				}
			else if (resizingColumn != -1)
				{
				table.getColumnBounds(resizingColumn, 0, -1, tmpBounds);
				tmpBounds.setXmax(event.worldPt.x);
//MiDebug.println("\ntable.getMinimumColumnWidth() = " + table.getMinimumColumnWidth());
//MiDebug.println("tmpBounds.getWidth() = " + tmpBounds.getWidth());
//MiDebug.println("table.getColumnWidth(resizingColumn() = " + table.getColumnWidth(resizingColumn));
				if (tmpBounds.getWidth() < table.getMinimumColumnWidth())
					tmpBounds.setWidth(table.getMinimumColumnWidth());
				table.setColumnWidth(resizingColumn, tmpBounds.getWidth());
//MiDebug.println("NOW table.getColumnWidth(resizingColumn() = " + table.getColumnWidth(resizingColumn));
				return(Mi_CONSUME_EVENT);
				}
			}
		else if ((isCommand(END_RESIZE_ROW_COLUMN_COMMAND_NAME))
			|| (isCommand(END_MOVE_ROW_COLUMN_COMMAND_NAME)))
			{
			if ((resizingRow != -1) || (resizingColumn != -1))
				{
				resizingRow = -1;
				resizingColumn = -1;
				event.editor.removeGrabEventHandler(this);
				return(Mi_CONSUME_EVENT);
				}
			}


		if ((movingRow == -1) && (movingColumn == -1)
			&& ((cell == null) || (!eventIsOverHeaderOrFooterCell(cell))))
			{
			return(Mi_PROPOGATE_EVENT);
			}

		// ---------------------------------------
		//	Moving Rows and Columns
		// ---------------------------------------
		if (isCommand(START_MOVE_ROW_COLUMN_COMMAND_NAME))
			{
			if ((movingRow != -1) || (movingColumn != -1))
				return(Mi_PROPOGATE_EVENT);

			if ((cell.columnNumber == MiTable.COLUMN_HEADER_NUMBER)
				|| (cell.columnNumber == MiTable.COLUMN_FOOTER_NUMBER))
				{
				movingRow = cell.rowNumber;
				tmpBounds.reverse();
				if (table.getCell(movingRow, MiTable.COLUMN_HEADER_NUMBER) != null)
					{
					table.getTableLayout().getCellBounds(
						movingRow, MiTable.COLUMN_HEADER_NUMBER, tmpBounds);
					}
				if (table.getCell(movingRow, MiTable.COLUMN_FOOTER_NUMBER) != null)
					{
					tmpBounds.union(table.getTableLayout().getCellBounds(
						movingRow, MiTable.COLUMN_FOOTER_NUMBER, new MiBounds()));
					}
				tmpBounds.union(table.getRowBounds(movingRow, 0, -1, new MiBounds()));

				movingRowImage = new MiImage(table.makeImageFromArea(tmpBounds));
				movingRowImage.setCenter(tmpBounds.getCenter());

				movingRowImage.setHasShadow(true);
				movingImageCenterOffset.x = tmpBounds.getCenterX() - event.worldPt.x;
				movingImageCenterOffset.y = tmpBounds.getCenterY() - event.worldPt.y;
				table.appendAttachment(movingRowImage);
				event.editor.prependGrabEventHandler(this);
				}
			else if ((cell.rowNumber == MiTable.ROW_HEADER_NUMBER)
				|| (cell.rowNumber == MiTable.ROW_FOOTER_NUMBER))
				{
				movingColumn = cell.columnNumber;

				tmpBounds.reverse();
				if (table.getCell(MiTable.ROW_HEADER_NUMBER, movingColumn) != null)
					{
					table.getTableLayout().getCellBounds(
						MiTable.ROW_HEADER_NUMBER, movingColumn, tmpBounds);
					}
			
				if (table.getCell(MiTable.ROW_FOOTER_NUMBER, movingColumn) != null)
					{
					tmpBounds.union(table.getTableLayout().getCellBounds(
						MiTable.ROW_FOOTER_NUMBER, movingColumn, new MiBounds()));
					}
				tmpBounds.union(table.getColumnBounds(movingColumn, 0, -1, new MiBounds()));

				movingColumnImage = new MiImage(table.makeImageFromArea(tmpBounds));
				movingColumnImage.setCenter(tmpBounds.getCenter());

				movingColumnImage.setHasShadow(true);
				movingImageCenterOffset.x = tmpBounds.getCenterX() - event.worldPt.x;
				movingImageCenterOffset.y = tmpBounds.getCenterY() - event.worldPt.y;
				table.appendAttachment(movingColumnImage);
				event.editor.prependGrabEventHandler(this);
				}
			if ((movingRow != -1) || (movingColumn != -1))
				return(Mi_CONSUME_EVENT);
			}
		else if (isCommand(MOVE_ROW_COLUMN_COMMAND_NAME))
			{
			if (movingRow != -1)
				{
				movingRowImage.setCenter(
					event.worldPt.x + movingImageCenterOffset.x,
					event.worldPt.y + movingImageCenterOffset.y);
				return(Mi_CONSUME_EVENT);
				}
			else if (movingColumn != -1)
				{
				movingColumnImage.setCenter(
					event.worldPt.x + movingImageCenterOffset.x,
					event.worldPt.y + movingImageCenterOffset.y);

				return(Mi_CONSUME_EVENT);
				}
			}
		else if (isCommand(END_MOVE_ROW_COLUMN_COMMAND_NAME))
			{
			if (movingRow != -1)
				{
				cell = table.pickCellIncludingHeadersAndFooters(cursorArea);
				if ((cell != null) && (cell.rowNumber != movingRow))
					{
					table.moveRow(movingRow, cell.rowNumber);
					}
				movingRow = -1;
				table.removeAttachment(movingRowImage);
				movingRowImage = null;
				event.editor.removeGrabEventHandler(this);
				return(Mi_CONSUME_EVENT);
				}
			if (movingColumn != -1)
				{
				cell = table.pickCellIncludingHeadersAndFooters(cursorArea);
				if ((cell != null) && (cell.columnNumber != movingColumn))
					{
					table.moveColumn(movingColumn, cell.columnNumber);
					}
				movingColumn = -1;
				table.removeAttachment(movingColumnImage);
				movingColumnImage = null;
				event.editor.removeGrabEventHandler(this);
				return(Mi_CONSUME_EVENT);
				}
			}


		// ---------------------------------------
		//	Sorting Rows and Columns
		// ---------------------------------------
		if (isCommand(TOGGLE_SORT_ORDER_COMMAND_NAME))
			{
			if (table.getSortManager() == null)
				return(Mi_PROPOGATE_EVENT);

			if (sortableColumns &&
				(cell.columnNumber == MiTable.COLUMN_HEADER_NUMBER)
				|| (cell.columnNumber == MiTable.COLUMN_FOOTER_NUMBER))
				{
				table.getSortManager().sort(cell.rowNumber, false, MiiTableSortMethod.Mi_TOGGLE);
				}
			else if (sortableRows
				&& (cell.rowNumber == MiTable.ROW_HEADER_NUMBER)
				|| (cell.rowNumber == MiTable.ROW_FOOTER_NUMBER))
				{
				table.getSortManager().sort(cell.columnNumber, true, MiiTableSortMethod.Mi_TOGGLE);
				}
			
			}
		return(Mi_CONSUME_EVENT);
		}
	protected	boolean		eventIsOverHeaderOrFooterCell(MiTableCell cell)
		{
		if ((cell.columnNumber == MiTable.COLUMN_HEADER_NUMBER)
			|| (cell.columnNumber == MiTable.COLUMN_FOOTER_NUMBER)
			|| (cell.rowNumber == MiTable.ROW_HEADER_NUMBER)
			|| (cell.rowNumber == MiTable.ROW_FOOTER_NUMBER))
			{
			return(true);
			}
		return(false);
		}
	}

