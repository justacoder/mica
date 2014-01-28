
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
import com.swfm.mica.util.Strings;
import java.awt.Color;
import java.util.Hashtable;
import com.swfm.mica.util.IntVector;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTableSelectionManager implements MiiBrowsableGrid, MiiActionTypes, MiiTypes, MiiLookProperties
	{
	public static final int 	Mi_ITEM_START_CHANGE_SELECTION_MULTIPLE_ITEMS_ACTION 	
						= MiActionManager.registerAction("Mi_ITEM_START_CHANGE_SELECTION_MULTIPLE_ITEMS_ACTION");
	public static final int 	Mi_ITEM_END_CHANGE_SELECTION_MULTIPLE_ITEMS_ACTION 	
						= MiActionManager.registerAction("Mi_ITEM_END_CHANGE_SELECTION_MULTIPLE_ITEMS_ACTION");

	public static final int		Mi_CELL_SELECTION_POLICY	= 1;
	public static final int		Mi_ROW_SELECTION_POLICY		= 2;
	public static final int		Mi_COLUMN_SELECTION_POLICY	= 3;
	private		int		selectionPolicy			= Mi_CELL_SELECTION_POLICY;

	private		boolean		isBrowsable			= true;
	private		boolean		userSelectTogglesSelectionState	= true;
	private		boolean		enableBrowseForegroundColor	= false;
	private		boolean		enableSelectionForegroundColor	= false;

	private		Color		selectionForegroundColor;
	private		Color		browseForegroundColor;

	private		MiPart		selectionBackgroundRect;
	private		MiPart		browseBackgroundRect;

	private		Object		currentBrowsedItemBackground;

	private		MiBounds	tmpBounds		= new MiBounds();
	private		int[]		tmpRowNumber		= new int[1];
	private		int[]		tmpColumnNumber		= new int[1];

	private		int		browsedItemRow		= -1;
	private		int		browsedItemColumn	= -1;

	private		MiTable		table;

	private		Hashtable	browseOverriddenColors	= new Hashtable(11);
	private		Hashtable	selectOverriddenColors	= new Hashtable(11);
	private		MiTableCells	selectedCells		= new MiTableCells(table);
	private		boolean		validCellLocations;

	private		int		minNumSelected		= 0;
	private		int		maxNumSelected		= 1;



	public				MiTableSelectionManager(MiTable table)
		{
		this.table = table;
		refreshLookAndFeel();
		}

	// ----------------------------------------------
	//	Configuration routines
	// ----------------------------------------------
	public		void		setSelectionPolicy(int policy)
		{
		selectionPolicy = policy;
		}
	public		int		getSelectionPolicy()
		{
		return(selectionPolicy);
		}

	public		void		setBrowsable(boolean flag)
		{
		isBrowsable = flag;
		}

	public		void		setBrowseForegroundColor(Color color)
		{
		browseForegroundColor = color;
		enableBrowseForegroundColor = true;
		}
	public		Color		getBrowseForegroundColor()
		{
		return(browseForegroundColor);
		}
	public		void		clearBrowseForegroundColor()
		{
		enableBrowseForegroundColor = false;
		}

	public		void		setSelectionForegroundColor(Color color)
		{
		selectionForegroundColor = color;
		enableSelectionForegroundColor = true;
		}
	public		Color		getSelectionForegroundColor()
		{
		return(selectionForegroundColor);
		}
	public		void		clearSelectionForegroundColor()
		{
		enableSelectionForegroundColor = false;
		}

	public		void		setBrowseBackgroundRect(MiPart rect)
		{
		browseBackgroundRect = rect;
		}
	public		MiPart		getBrowseBackgroundRect()
		{
		return(browseBackgroundRect);
		}

	public		void		setSelectionBackgroundRect(MiPart rect)
		{
		selectionBackgroundRect = rect;
		}
	public		MiPart		getSelectionBackgroundRect()
		{
		return(selectionBackgroundRect);
		}
	public		void		setMinimumNumberSelected(int min)
		{
		minNumSelected = min;
		}
	public		int		getMinimumNumberSelected()
		{
		return(minNumSelected);
		}
	public		void		setMaximumNumberSelected(int max)
		{
		maxNumSelected = max;
		}
	public		int		getMaximumNumberSelected()
		{
		return(maxNumSelected);
		}

	public		void		setUserSelectTogglesSelectionState(boolean flag)
		{
		userSelectTogglesSelectionState = flag;
		}
	public		boolean		getUserSelectTogglesSelectionState()
		{
		return(userSelectTogglesSelectionState);
		}

	// ----------------------------------------------
	//	Current state manipulation routines
	// ----------------------------------------------
	public		int		getBrowsedItemRow()
		{
		return(browsedItemRow);
		}
	public		int		getBrowsedItemColumn()
		{
		return(browsedItemColumn);
		}

	public		boolean		isBrowsedItem(int row, int column)
		{
		return((row == getBrowsedItemRow()) && (column == getBrowsedItemColumn()));
		}
	public		boolean		isSelectedItem(int row, int column)
		{
		for (int i = 0; i < selectedCells.size(); ++i)
			{
			MiTableCell cell = selectedCells.elementAt(i);
			if ((cell.getRowNumber() == row)
				&& (cell.getColumnNumber() == column))
				{
				return(true);
				}
			}
		return(false);
		}
	public		MiTableCell	getSelectedCell()
		{
		if (selectedCells.size() > 0)
			return(selectedCells.elementAt(0));
		return(null);
		}
	public		MiTableCells	getSelectedCells()
		{
		return(selectedCells);
		}
	public		int		getSelectedRow()
		{
		if (selectedCells.size() > 0)
			{
			return(selectedCells.elementAt(0).getRowNumber());
			}
		return(-1);
		}
	public		int		getSelectedColumn()
		{
		if (selectedCells.size() > 0)
			{
			return(selectedCells.elementAt(0).getColumnNumber());
			}
		return(-1);
		}
	public		boolean		getSelectedItem(int[] row, int[] column)
		{
		if (selectedCells.size() > 0)
			{
			row[0] = selectedCells.elementAt(0).getRowNumber();
			column[0] = selectedCells.elementAt(0).getColumnNumber();
			return(true);
			}
		return(false);
		}

	public		boolean		getSelectedItems(int[] rows, int[] columns)
		{
		int numSelItems = selectedCells.size();
		for (int i = 0; i < numSelItems; ++i)
			{
			MiTableCell cell = selectedCells.elementAt(i);
			rows[i] = cell.getRowNumber();
			columns[i] = cell.getColumnNumber();
			}
		return(numSelItems > 0);
		}

	public		int		getNumberOfSelectedItems()
		{
		return(selectedCells.size());
		}

	public		boolean		selectItem(MiTableCell cell)
		{
		return(selectItem(cell.rowNumber, cell.columnNumber));
		}
	public		boolean		selectItem(int row, int column)
		{
		if (!table.isSelectable(row, column))
			return(false);
		if (!table.isSelectable())
			return(false);

		if (isSelectedItem(row, column))
			return(true);

		MiTableCell cell = table.getCell(row, column);
		if (!table.dispatchActionRequest(Mi_ITEM_SELECTED_ACTION, cell))
			return(false);

		if (getNumberOfSelectedItems() >= getMaximumNumberSelected())
			{
			--minNumSelected;
			int numSelected = getNumberOfSelectedItems();
			int[] rows = new int[numSelected];
			int[] columns = new int[numSelected];
			getSelectedItems(rows, columns);
			for (int i = 0; i < numSelected; ++i)
				{
				deSelectItem(rows[i], columns[i]);
				if (getNumberOfSelectedItems() < getMaximumNumberSelected())
					break;
				}
			++minNumSelected;
			}
		if (getNumberOfSelectedItems() >= getMaximumNumberSelected())
			return(false);

		addSelectedRowColumn(row, column);

		setSelectedItemAppearance(row, column);
		
		table.dispatchAction(Mi_ITEM_SELECTED_ACTION, cell);
		table.dispatchAction(Mi_VALUE_CHANGED_ACTION);
		return(true);
		}
	protected	void		setSelectedItemAppearance(int row, int column)
		{
		MiGridBackgrounds backgrounds = table.getBackgroundManager();
		boolean isBrowsed = isBrowsedItem(row, column);

		if (selectionPolicy == Mi_CELL_SELECTION_POLICY)
			{
			backgrounds.removeCellBackgroundOverlay(row, column);
			backgrounds.appendCellBackgroundOverlay(selectionBackgroundRect, row, column);
			MiTableCell cell = table.getCell(row, column);
			if (cell.getGraphics() != null)
				cell.getGraphics().select(true);
			//if (!isBrowsed)
				{
				if (cell.getColor() != null) // Mi_TRANSPARENT_COLOR
					selectOverriddenColors.put(cell, cell.getColor());
				else
					selectOverriddenColors.remove(cell);
				}
			cell.setColor(selectionForegroundColor);
			}
		else if (selectionPolicy == Mi_ROW_SELECTION_POLICY)
			{
			backgrounds.appendRowBackgroundOverlay(selectionBackgroundRect, row);
			for (int i = 0; i < table.getNumberOfColumns(); ++i)
				{
				MiTableCell cell = table.getCell(row, i);
				// if (!isBrowsed)
					{
					if (cell.getColor() != null) // Mi_TRANSPARENT_COLOR
						selectOverriddenColors.put(cell, cell.getColor());
					else
						selectOverriddenColors.remove(cell);
					}
				cell.setColor(selectionForegroundColor);
				}
			}
		else if (selectionPolicy == Mi_COLUMN_SELECTION_POLICY)
			{
			backgrounds.appendColumnBackgroundOverlay(selectionBackgroundRect, column);
			for (int i = 0; i < table.getNumberOfRows(); ++i)
				{
				MiTableCell cell = table.getCell(i, column);
				// if (!isBrowsed)
					{
					if (cell.getColor() != null) // Mi_TRANSPARENT_COLOR
						selectOverriddenColors.put(cell, cell.getColor());
					else
						selectOverriddenColors.remove(cell);
					}
				cell.setColor(selectionForegroundColor);
				}
			}
		}
	public		void		toggleSelectItem(int row, int column)
		{
		if (isSelectedItem(row, column))
			{
			if (getNumberOfSelectedItems() > minNumSelected)
				deSelectItem(row, column);
			}
		else
			{
			selectItem(row, column);
			}
		}
	public		void		deSelectItem(int row, int column)
		{
		if (getNumberOfSelectedItems() <= getMinimumNumberSelected())
			return;

		if (!isSelectedItem(row, column))
			return;

		MiTableCell cell = table.getCell(row, column);
		if (!table.dispatchActionRequest(Mi_ITEM_DESELECTED_ACTION, cell))
			return;


		removeSelectedRowColumn(row, column);
		setDeselectedItemAppearance(row, column);

		table.dispatchAction(Mi_ITEM_DESELECTED_ACTION, cell);
		if (getNumberOfSelectedItems() == 0)
			table.dispatchAction(Mi_NO_ITEMS_SELECTED_ACTION);
		table.dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	protected	void		setDeselectedItemAppearance(int row, int column)
		{
		MiGridBackgrounds backgrounds = table.getBackgroundManager();
		boolean isBrowsed = isBrowsedItem(row, column);

		if (selectionPolicy == Mi_CELL_SELECTION_POLICY)
			{
			backgrounds.removeCellBackgroundOverlay(row, column);
			MiTableCell cell = table.getCell(row, column);
			if (cell.getGraphics() != null)
				cell.getGraphics().select(false);
			if (isBrowsed)
				{
				if (browseForegroundColor != null)
					{
					cell.setColor(browseForegroundColor);
					}
				}
			else
				{
				cell.setColor((Color )selectOverriddenColors.get(cell));
				}
			}
		else if (selectionPolicy == Mi_ROW_SELECTION_POLICY)
			{
			backgrounds.removeRowBackgroundOverlay(row);
			for (int i = 0; i < table.getNumberOfColumns(); ++i)
				{
				MiTableCell cell = table.getCell(row, i);
				if (isBrowsed)
					{
					if (browseForegroundColor != null)
						{
						cell.setColor(browseForegroundColor);
						}
					}
				else
					{
					cell.setColor((Color )selectOverriddenColors.get(cell));
					}
				}
			}
		else if (selectionPolicy == Mi_COLUMN_SELECTION_POLICY)
			{
			backgrounds.removeColumnBackgroundOverlay(column);
			for (int i = 0; i < table.getNumberOfRows(); ++i)
				{
				MiTableCell cell = table.getCell(i, column);
				if (isBrowsed)
					{
					if (browseForegroundColor != null)
						{
						cell.setColor(browseForegroundColor);
						}
					}
				else
					{
					cell.setColor((Color )selectOverriddenColors.get(cell));
					}
				}
			}
		}
	public		void		selectAll()
		{
		if (selectionPolicy == Mi_CELL_SELECTION_POLICY)
			{
			for (int row = 0; row < table.getNumberOfRows(); ++row)
				{
				for (int column = 0; column < table.getNumberOfColumns(); ++column)
					{
					selectItem(row, column);
					}
				}
			}
		else if (selectionPolicy == Mi_ROW_SELECTION_POLICY)
			{
			for (int row = 0; row < table.getNumberOfRows(); ++row)
				{
				selectItem(row, 0);
				}
			}
		else if (selectionPolicy == Mi_COLUMN_SELECTION_POLICY)
			{
			for (int column = 0; column < table.getNumberOfColumns(); ++column)
				{
				selectItem(0, column);
				}
			}
		}

	public		void		deSelectAll()
		{
		int numSelected = getNumberOfSelectedItems();
		int[] rows = new int[numSelected];
		int[] columns = new int[numSelected];
		if (getSelectedItems(rows, columns))
			{
			table.dispatchAction(Mi_ITEM_START_CHANGE_SELECTION_MULTIPLE_ITEMS_ACTION);
			for (int i = 0; i < rows.length; ++i)
				{
				deSelectItem(rows[i], columns[i]);
				}
			table.dispatchAction(Mi_ITEM_END_CHANGE_SELECTION_MULTIPLE_ITEMS_ACTION);
			}

		if ((getBrowsedItemRow() == -1) && (getBrowsedItemColumn() == -1))
			selectOverriddenColors.clear();

		table.dispatchAction(Mi_NO_ITEMS_SELECTED_ACTION);
		table.dispatchAction(Mi_ALL_ITEMS_DESELECTED_ACTION);
		}
	public		boolean		activateItem(int row, int column)
		{
//System.out.println("ACTIVATE ITEM: row =  " + row + ", col = " + column);
		if (!selectItem(row, column))
			return(false);
		table.dispatchAction(Mi_ACTIVATED_ACTION);
		return(true);
		}

	public		void		browseItem(int row, int column)
		{
//System.out.println("BROWSE ITEM: row = " + row + ", col = " + column);

		if (!table.isSensitive())
			return;

		if (!isBrowsable)
			{
			browsedItemRow = row;
			browsedItemColumn = column;
			return;
			}

		boolean selected = isSelectedItem(row, column);
		if ((row != getBrowsedItemRow()) || (column != getBrowsedItemColumn()))
			{
			MiPart cellGraphics;

			table.makeVisible(row, column);
			if ((getBrowsedItemRow() != -1) && (getBrowsedItemColumn() != -1))
				{
				deBrowseAll();
				}
			MiGridBackgrounds backgrounds = table.getBackgroundManager();
			if (selectionPolicy == Mi_CELL_SELECTION_POLICY)
				{
				if (table.isSelectable(row, column))
					{
					currentBrowsedItemBackground 
						= backgrounds.appendCellBackgroundOverlay(
						browseBackgroundRect, row, column); 	
					browsedItemRow = row;
					browsedItemColumn = column;
					MiTableCell cell = table.getCell(row, column);
					// if (!selected)
						{
						if (cell.getColor() != null) // Mi_TRANSPARENT_COLOR
							browseOverriddenColors.put(cell, cell.getColor());
						else
							browseOverriddenColors.remove(cell);
						}
//System.out.println("BROWSE ITEM visibley: row = " + row + ", col = " + column);
					if (browseForegroundColor != null)
						{
						cell.setColor(browseForegroundColor);
						}
					table.dispatchAction(Mi_ITEM_BROWSED_ACTION);
					}
				}
			else if (selectionPolicy == Mi_ROW_SELECTION_POLICY)
				{
				if (table.isSelectable(row, column))
					{
					currentBrowsedItemBackground 
						= backgrounds.appendRowBackgroundOverlay(
						browseBackgroundRect, row);
					for (int i = 0; i < table.getNumberOfColumns(); ++i)
						{
						MiTableCell cell = table.getCell(row, i);
						// if (!selected)
							{
							if (cell.getColor() != null) // Mi_TRANSPARENT_COLOR
								browseOverriddenColors.put(cell, cell.getColor());
							else
								browseOverriddenColors.remove(cell);
							}
						if (browseForegroundColor != null)
							{
							cell.setColor(browseForegroundColor);
							}
						}
					browsedItemRow = row;
					browsedItemColumn = column;
					table.dispatchAction(Mi_ITEM_BROWSED_ACTION);
					}
				}
			else if (selectionPolicy == Mi_COLUMN_SELECTION_POLICY)
				{
				if (table.isSelectable(row, column))
					{
					currentBrowsedItemBackground 
						= backgrounds.appendColumnBackgroundOverlay(
						browseBackgroundRect, column); 	
					for (int i = 0; i < table.getNumberOfRows(); ++i)
						{
						MiTableCell cell = table.getCell(i, column);
						// if (!selected)
							{
							if (cell.getColor() != null) // Mi_TRANSPARENT_COLOR
								browseOverriddenColors.put(cell, cell.getColor());
							else
								browseOverriddenColors.remove(cell);
							}
						if (browseForegroundColor != null)
							{
							cell.setColor(browseForegroundColor);
							}
						}
					browsedItemRow = row;
					browsedItemColumn = column;
					table.dispatchAction(Mi_ITEM_BROWSED_ACTION);
					}
				}
			}
		}
	protected	void		refreshLookAndFeel()
		{
		selectionBackgroundRect = new MiRectangle();
		MiToolkit.setAttributes(selectionBackgroundRect, MiiToolkit.Mi_TOOLKIT_CELL_SELECTED_ATTRIBUTES);

		browseBackgroundRect = new MiRectangle();
		MiToolkit.setAttributes(browseBackgroundRect, MiiToolkit.Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES);

		selectionForegroundColor= MiColorManager.white; // CELL_CONTENT_SELECTED_ATTS
		browseForegroundColor	= MiColorManager.black;
		}
	private		void		addSelectedRowColumn(int row, int column)
		{
		MiTableCell cell = table.getCell(row, column);
		if (cell != null)
			selectedCells.addElement(cell);
		}
	private		void		removeSelectedRowColumn(int row, int column)
		{
		for (int i = 0; i < selectedCells.size(); ++i)
			{
			if ((selectedCells.elementAt(i).getRowNumber() == row) 
				&& (selectedCells.elementAt(i).getColumnNumber() == column))
				{
				selectedCells.removeElementAt(i);
				return;
				}
			}
		}

	protected	void		invalidateCellLocations()
		{
		if (!validCellLocations)
			return;

		validCellLocations = false;
		deBrowseAll();
		for (int i = 0; i < selectedCells.size(); ++i)
			{
			MiTableCell cell = selectedCells.elementAt(i);
			setDeselectedItemAppearance(cell.getRowNumber(), cell.getColumnNumber());
			}
		}
	protected	void		validateCellLocations()
		{
		if (validCellLocations)
			return;

		validCellLocations = true;
		MiTableCells tableCells = table.getContentsCells();
		int numCellsThatWereSelected = selectedCells.size();
		MiTableCells deSelectedCells = new MiTableCells(table);
		for (int i = 0; i < selectedCells.size(); ++i)
			{
			MiTableCell cell = selectedCells.elementAt(i);
			if (tableCells.contains(cell))
				{
				setSelectedItemAppearance(cell.getRowNumber(), cell.getColumnNumber());
				}
			else
				{
				selectedCells.removeElementAt(i);
				deSelectedCells.addElement(cell);
				--i;
				}
			}
		for (int i = 0; i < deSelectedCells.size(); ++i)
			table.dispatchAction(Mi_ITEM_DESELECTED_ACTION, deSelectedCells.elementAt(i));

		if ((numCellsThatWereSelected > 0) && (getNumberOfSelectedItems() == 0))
			table.dispatchAction(Mi_NO_ITEMS_SELECTED_ACTION);
		}

	// -----------------------------------------------------------------
	// Implementation of MiiBrowsableGrid
	// -----------------------------------------------------------------
	public		boolean		isBrowsable()
		{
		return(isBrowsable);
		}
	public		boolean		isSensitive()
		{
		return(table.isSensitive());
		}

	public		void		browseItem(MiBounds cursor)
		{
		boolean grabbed = false;
		if (table.pickCell(cursor, tmpRowNumber, tmpColumnNumber))
			{
			browseItem(tmpRowNumber[0], tmpColumnNumber[0]);
			return;
			}
		if ((getBrowsedItemRow() == -1) || (getBrowsedItemColumn() == -1))
			{
			return;
			}
		if (!grabbed)
			{
			deBrowseAll();
			return;
			}

		MiBounds contentsBounds = table.getContentsBounds(tmpBounds);
		if (cursor.getYmax() < contentsBounds.getYmin())
			browseVerticalNextItem();
		else if (cursor.getYmin() > contentsBounds.getYmax())
			browseVerticalPreviousItem();
		else
			browseVisibleItemClosestTo(cursor);

		if (cursor.getXmax() < contentsBounds.getXmin())
			browseHorizontalNextItem();
		else if (cursor.getXmin() > contentsBounds.getXmax())
			browseHorizontalPreviousItem();
		else
			browseVisibleItemClosestTo(cursor);
		}
	public		void		browseVerticalPreviousItem()
		{
		int row = browsedItemRow;
		int column = browsedItemColumn;
		if (column == -1)
			column = 0;
		while (row > 0)
			{
			--row;
			if (table.isSelectable(row, column))
				{
				browseItem(row, column);
				return;
				}
			}
		}
	public		void		browseVerticalNextItem()
		{
		int row = browsedItemRow;
		int column = browsedItemColumn;
		if (column == -1)
			column = 0;
		while (row < table.getNumberOfRows() - 1)
			{
			++row;
			if (table.isSelectable(row, column))
				{
				browseItem(row, column);
				return;
				}
			}
		}
	public		void		browseHorizontalPreviousItem()
		{
		int column = browsedItemColumn;
		int row = browsedItemRow;
		if (row == -1)
			row = 0;
		while (column > 0)
			{
			--column;
			if (table.isSelectable(row, column))
				{
				browseItem(row, column);
				return;
				}
			}
		}
	public		void		browseHorizontalNextItem()
		{
		int column = browsedItemColumn;
		int row = browsedItemRow;
		if (row == -1)
			row = 0;
		while (column < table.getNumberOfColumns() - 1)
			{
			++column;
			if (table.isSelectable(row, column))
				{
				browseItem(row, column);
				return;
				}
			}
		}
	public		void		browseVerticalHomeItem()
		{
		// FIX:
		}
	public		void		browseVerticalEndItem()
		{
		}
	public		void		browseHorizontalHomeItem()
		{
		}
	public		void		browseHorizontalEndItem()
		{
		}
	public		void		deBrowseAll()
		{
//System.out.println("BROWSE NO ITEMS");
		if ((getBrowsedItemRow() != -1) && (getBrowsedItemColumn() != -1))
			{
			int row = getBrowsedItemRow();
			int column = getBrowsedItemColumn();
			boolean selected = isSelectedItem(row, column);

			table.getBackgroundManager().removeBackground(currentBrowsedItemBackground); 	
			if (selectionPolicy == Mi_CELL_SELECTION_POLICY)
				{
				MiTableCell cell = table.getCell(row, column);
				if (selected)
					cell.setColor(selectionForegroundColor);
				else
					cell.setColor((Color )browseOverriddenColors.get(cell));
				}
			else if (selectionPolicy == Mi_ROW_SELECTION_POLICY)
				{
				for (int i = 0; i < table.getNumberOfColumns(); ++i)
					{
					MiTableCell cell = table.getCell(row, i);
					if (selected)
						cell.setColor(selectionForegroundColor);
					else
						cell.setColor((Color )browseOverriddenColors.get(cell));
					}
				}
			else if (selectionPolicy == Mi_COLUMN_SELECTION_POLICY)
				{
				for (int i = 0; i < table.getNumberOfRows(); ++i)
					{
					MiTableCell cell = table.getCell(i, row);
					if (selected)
						cell.setColor(selectionForegroundColor);
					else
						cell.setColor((Color )browseOverriddenColors.get(cell));
					}
				}
			table.dispatchAction(Mi_ITEM_DEBROWSED_ACTION);
			}
		browsedItemRow = -1;
		browsedItemColumn = -1;

		browseOverriddenColors.clear();
		if (getNumberOfSelectedItems() == 0)
			selectOverriddenColors.clear();
		}

	public		void		selectBrowsedItem()
		{
		if ((getBrowsedItemRow() != -1) && (getBrowsedItemColumn() != -1))
			{
			if (userSelectTogglesSelectionState)
				toggleSelectItem(getBrowsedItemRow(), getBrowsedItemColumn());
			else
				selectItem(getBrowsedItemRow(), getBrowsedItemColumn());
			}
		}
	public		void		selectAdditionalItem(MiBounds cursor)
		{
		if ((table.pickCell(cursor, tmpRowNumber, tmpColumnNumber))
			|| (((tmpRowNumber[0] = getBrowsedItemRow()) != -1) 
			&& ((tmpColumnNumber[0] = getBrowsedItemColumn()) != -1)))
			{
			toggleSelectItem(tmpRowNumber[0], tmpColumnNumber[0]);
			}
		}
	public		void		selectInterveningItems(MiBounds cursor)
		{
		if ((table.pickCell(cursor, tmpRowNumber, tmpColumnNumber))
			|| (((tmpRowNumber[0] = getBrowsedItemRow()) != -1) 
			&& ((tmpColumnNumber[0] = getBrowsedItemColumn()) != -1)))
			{
			int[] rowNumber = new int[1];
			int[] columnNumber = new int[1];
/*
			if (getClosestSelectedItem(
				tmpRowNumber[0], tmpColumnNumber[0], 
				rowNumber, columnNumber))
*/
			if (selectedCells.size() > 0)
				{
				rowNumber[0] = selectedCells.lastElement().getRowNumber();
				columnNumber[0] = selectedCells.lastElement().getColumnNumber();
				int rowDirection = 1;
				int numRowsToSelect = tmpRowNumber[0] - rowNumber[0];
				if (numRowsToSelect < 0)
					{
					numRowsToSelect = -numRowsToSelect;
					rowDirection = -1;
					}
				else if (numRowsToSelect == 0)
					{
					numRowsToSelect = 1;
					rowDirection = 0;
					}
				int columnDirection = 1;
				int numColumnsToSelect = tmpColumnNumber[0] - columnNumber[0];
				if (numColumnsToSelect < 0)
					{
					numColumnsToSelect = -numColumnsToSelect;
					columnDirection = -1;
					}
				else if (numColumnsToSelect == 0)
					{
					numColumnsToSelect = 1;
					columnDirection = 0;
					}
				int column = columnNumber[0];

				if (((numColumnsToSelect > 0) && (numRowsToSelect > 0))
					&& ((numColumnsToSelect > 1) || (numRowsToSelect > 1)))
					{
					table.dispatchAction(Mi_ITEM_START_CHANGE_SELECTION_MULTIPLE_ITEMS_ACTION);
					}
				for (int i = 0; i < numColumnsToSelect; ++i)
					{
					int row = rowNumber[0];
					column += columnDirection;
					for (int j = 0; j < numRowsToSelect; ++j)
						{
						row += rowDirection;
						selectItem(row, column);
						}
					}
				if (((numColumnsToSelect > 0) && (numRowsToSelect > 0))
					&& ((numColumnsToSelect > 1) || (numRowsToSelect > 1)))
					{
					table.dispatchAction(Mi_ITEM_END_CHANGE_SELECTION_MULTIPLE_ITEMS_ACTION);
					}
				}
			}
		}
	public		boolean		toggleSelectItem(MiBounds cursor)
		{
		if ((table.pickCell(cursor, tmpRowNumber, tmpColumnNumber))
			|| (((tmpRowNumber[0] = getBrowsedItemRow()) != -1) 
			&& ((tmpColumnNumber[0] = getBrowsedItemColumn()) != -1)))
			{
			MiTableCell cell = table.getCell(tmpRowNumber[0], tmpColumnNumber[0]);
			if (!isSelectedItem(tmpRowNumber[0], tmpColumnNumber[0]))
				{
				if (!table.dispatchActionRequest(Mi_ITEM_SELECTED_ACTION, cell))
					return(false);
				}
			else
				{
				if (!table.dispatchActionRequest(Mi_ITEM_DESELECTED_ACTION, cell))
					return(false);
				}

			--minNumSelected;
			deSelectAll();
			++minNumSelected;

			toggleSelectItem(tmpRowNumber[0], tmpColumnNumber[0]);
			}
		return(true);
		}
	public		boolean		selectItem(MiBounds cursor)
		{
//MiDebug.println(this + " selectItem: " + cursor);
		if ((table.pickCell(cursor, tmpRowNumber, tmpColumnNumber))
			|| (((tmpRowNumber[0] = getBrowsedItemRow()) != -1) 
			&& ((tmpColumnNumber[0] = getBrowsedItemColumn()) != -1)))
			{
			if (!isSelectedItem(tmpRowNumber[0], tmpColumnNumber[0]))
				{
				MiTableCell cell = table.getCell(tmpRowNumber[0], tmpColumnNumber[0]);
				if (!table.dispatchActionRequest(Mi_ITEM_SELECTED_ACTION, cell))
					return(false);

				--minNumSelected;
				deSelectAll();
				++minNumSelected;
				}
/*** left mouse click calls toggle select...
			if (userSelectTogglesSelectionState)
				{
				toggleSelectItem(tmpRowNumber[0], tmpColumnNumber[0]);
				}
			else
****/
				{
				selectItem(tmpRowNumber[0], tmpColumnNumber[0]);
				}
			}
		return(true);
		}
	public		boolean		activateItem(MiBounds cursor)
		{
		if (table.pickCell(cursor, tmpRowNumber, tmpColumnNumber))
			activateItem(tmpRowNumber[0], tmpColumnNumber[0]);
		else if ((getBrowsedItemRow() != -1) && (getBrowsedItemColumn() != -1))
			activateItem(getBrowsedItemRow(), getBrowsedItemColumn());
		return(true);
		}
/*
	protected	boolean		getClosestSelectedItem(
						int rowNumber, int columnNumber, 
						int[] closestRow, int[] closestColumn)
		{
		int numSelected = getNumberOfSelectedItems();
		if (numSelected > 0)
			{
			int[] rows = new int[numSelected];
			int[] columns = new int[numSelected];
			int minDistance = Integer.MAX_VALUE;
			getSelectedItems(rows, columns);
			for (int i = 0; i < numSelected; ++i)
				{
				int getDistanceSquared = (rows[i] - rowNumber) * (rows[i] - rowNumber)
					+ (columns[i] - columnNumber) * (columns[i] - columnNumber);
				if (getDistanceSquared < minDistance)
					{
					getDistanceSquared = minDistance;
					closestRow[0] = rows[i];
					closestColumn[0] = columns[i];
					}
				}
			return(true);
			}
		return(false);
		}
*/

	// -----------------------------------------------------------------
	// End of Implementation of MiiBrowsableGrid
	// -----------------------------------------------------------------
	protected	void		browseVisibleItemClosestTo(MiBounds cursor)
		{
		int 		closestRow 	= 0;
		int 		closestCol 	= 0;
		MiDistance 	closestDistance = Mi_MAX_DISTANCE_VALUE;
		MiPoint 	connPt 		= new MiPoint();
		MiPoint 	cursorCenter 	= cursor.getCenter();
		int		leftVisibleColumn= table.getLeftVisibleColumn();
		int		visibleColumns	= table.getNumberOfVisibleColumns();
		int		topVisibleRow	= table.getTopVisibleRow();
		int		visibleRows	= table.getNumberOfVisibleRows();

		visibleColumns	= Math.min(leftVisibleColumn + visibleColumns, table.getNumberOfColumns());
		visibleRows	= Math.min(topVisibleRow + visibleRows, table.getNumberOfRows());

		for (int i = topVisibleRow; i < visibleRows; ++i)
			{
			for (int j = leftVisibleColumn; j < visibleColumns; ++j)
				{
				MiBounds b = table.getCellBounds(i, j, tmpBounds);
				b.getClosestCommonPoint(cursorCenter, connPt);
				double dist = cursorCenter.getDistanceSquared(connPt);
				if (dist < closestDistance)
					{
					closestRow = i;
					closestCol = j;
					closestDistance = dist;
					}
				}
			}
		if ((visibleColumns != 0) && (visibleRows != 0))
			browseItem(closestRow, closestCol);
		}
	}


