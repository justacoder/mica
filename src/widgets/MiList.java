
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiList extends MiTable
	{
	private		MiTableCells 	tmpCells 			= new MiTableCells(this);
	private 	int		COLUMN_NUMBER			= 0;
	private 	int		numberOfVisibleCharactersWide	= 0;




	public				MiList()
		{
		this(false);
		}
	public				MiList(boolean multipleSelectionsAllowed)
		{
		this(-1 , multipleSelectionsAllowed);
		}
	public				MiList(
						int numVisibleCharactersWide,
						boolean multipleSelectionsAllowed)
		{
		this(numVisibleCharactersWide, -1, multipleSelectionsAllowed);
		}
	public				MiList(
						int numVisibleCharactersWide,
						int numVisibleRows,
						boolean multipleSelectionsAllowed)
		{
		if (numVisibleRows > 0)
			{
			setMinimumNumberOfVisibleRows(numVisibleRows);
			setPreferredNumberOfVisibleRows(numVisibleRows);
			}
		getTableLayout().setEmptyRowHeight(getFont().getMaxCharHeight());
		if (multipleSelectionsAllowed)
			{
			getSelectionManager().setMaximumNumberSelected(Integer.MAX_VALUE);
			}
		setPreferredNumberOfVisibleColumns(1);
		setMaximumNumberOfVisibleColumns(1);
		setTotalNumberOfColumns(1);
		setHasFixedTotalNumberOfColumns(true);
		MiTableCell columnDefaults = new MiTableCell(this, 0, 0);
		columnDefaults.setHorizontalJustification(Mi_LEFT_JUSTIFIED);
		setColumnDefaults(columnDefaults);
		if (numVisibleCharactersWide > 0)
			setNumberOfVisibleCharactersWide(numVisibleCharactersWide);
		getMadeColumnDefaults(COLUMN_NUMBER).setColumnHorizontalSizing(Mi_EXPAND_TO_FILL);
		getTableWideDefaults().insetMargins.setMargins(4, 0, 4, 0);
		getTableHeaderAndFooterManager().setResizableRows(false);
		getTableHeaderAndFooterManager().setMovableColumns(false);
		getSectionMargins().setMargins(0);
		getSortManager().setSortDirection(MiiTableSortMethod.Mi_FORWARD);

		MiToolkit.setBackgroundAttributes(this, 
			MiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);
		MiToolkit.setBackgroundAttributes(getContentsBackground(), 
			MiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);

		//OK But need non-transparent bg here to test... setContentsBackgroundBorder(null);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public		void		refreshLookAndFeel()
		{
		MiToolkit.setBackgroundAttributes(this, 
			MiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);
		MiToolkit.setBackgroundAttributes(getContentsBackground(), 
			MiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);
		super.refreshLookAndFeel();
		}
	public		void		setBackgroundColor(java.awt.Color c)
		{
		getContentsBackground().setBackgroundColor(c);
		super.setBackgroundColor(c);
		}
	public		void		setNumberOfVisibleCharactersWide(int numVisibleCharacters)
		{
		MiTableCell contentDefaults = getContentAreaDefaults();
		if (contentDefaults == null)
			{
			contentDefaults = new MiTableCell(this, 0, COLUMN_NUMBER);
			setContentAreaDefaults(contentDefaults);
			}

		contentDefaults.setFixedWidth(numVisibleCharacters * getFont().getAverageCharWidth());
		numberOfVisibleCharactersWide = numVisibleCharacters;
		}
	public		int		getNumberOfVisibleCharactersWide()
		{
		return(numberOfVisibleCharactersWide);
		}
	// --------------------------------------------------
	// 	ADDING List Items
	// --------------------------------------------------
	public		void		insertItem(String text, int index)
		{
		insertItem(makeMiText(text), index);
		}
	public		void		insertItem(MiPart obj, int index)
		{
		tmpCells.removeAllElements();
		insertRow(tmpCells.appendPart(obj), index);
		reSortList();
		}
	public		void		appendItem(String text)
		{
		appendItem(makeMiText(text));
		}
	public		void		appendItem(MiPart obj)
		{
		tmpCells.removeAllElements();
		appendRow(tmpCells.appendPart(obj));
		reSortList();
		}
	public		void		appendItems(Strings items)
		{
		super.appendItems(items);
		reSortList();
		}
	public		void		setItem(String text, int index)
		{
		setItem(makeMiText(text), index);
		}
	public		void		setItem(MiPart obj, int index)
		{
		tmpCells.removeAllElements();
		replaceRow(tmpCells.appendPart(obj), index);
		reSortList();
		}
	// --------------------------------------------------
	// 	REMOVING List Items
	// --------------------------------------------------
	public		void		removeAllItems()
		{
		removeAllCells();
		}
	public		void		removeItem(int index)
		{
		removeRow(index);
		}
	public		void		removeItem(String text)
		{
		removeRow(getIndexOfItem(text, false));
		}
	public		void		removeItem(Object tag)
		{
		removeRow(getIndexOfTag(tag));
		}
	public		void		removeItem(MiPart part)
		{
		removeRow(getIndexOfItem(part));
		}
	// --------------------------------------------------
	// 	INQUIRING List Items
	// --------------------------------------------------
	public		int		getIndexOfItem(MiPart obj)
		{
		MiTableCells cells = getContentsCells();
		for (int i = 0; i < cells.size(); ++i)
			{
			if (cells.elementAt(i).getGraphics() == obj)
				return(cells.elementAt(i).rowNumber);
			}
		return(-1);
		}
	public		void		setTag(String text, Object tag)
		{
		int index = getIndexOfItem(text, false);
		getCell(index, 0).setTag(tag);
		}
	public		void		setTag(int index, Object tag)
		{
		getCell(index, 0).setTag(tag);
		}
	public		Object		getTag(int index)
		{
		return(getCell(index, 0).getTag());
		}
	public		int		getIndexOfTag(Object tag)
		{
		MiTableCell cell = getCellWithTag(tag);
		if (cell != null)
			return(cell.getRowNumber());
		return(-1);
		}
	public		MiPart		getPartWithTag(Object tag)
		{
		return(getCell(getIndexOfTag(tag), 0).getGraphics());
		}
	public		String		getStringItem(int index)
		{
		return(getCell(index, 0).getValue());
		}
	public		Strings		getStringItems()
		{
		Strings strings = new Strings();
		for (int i = 0; i < getNumberOfItems(); ++i)
			strings.addElement(getCell(i, 0).getValue());
		return(strings);
		}
	public		MiPart		getPartItem(int index)
		{
		return(getCell(index, 0).getGraphics());
		}
	public		boolean		isTextItem(int index)
		{
		return((getPartItem(index) == null) || (getPartItem(index) instanceof MiText));
		}
	public		int		getIndexOfItem(String text)
		{
		return(getIndexOfItem(text, false));
		}
	public		int		getIndexOfItem(String text, boolean ignoreCase)
		{
		for (int i = 0; i < getNumberOfRows(); ++i)
			{
			if ((isTextItem(i)) && (ignoreCase 
				? getStringItem(i).equalsIgnoreCase(text) : getStringItem(i).equals(text)))
				{
				return(i);
				}
			}
		return(-1);
		}
	// --------------------------------------------------
	// 	INQUIRING Selected List Items
	// --------------------------------------------------
	public		int		getSelectedItemIndex()
		{
		int[] row = new int[1];
		int[] column = new int[1];
		if (getSelectionManager().getSelectedItem(row, column))
			return(row[0]);
		return(-1);
		}
	public		int		getBrowsedItemIndex()
		{
		return(getSelectionManager().getBrowsedItemRow());
		}
	public		int		getNumberOfSelectedItems()
		{
		return(getSelectionManager().getNumberOfSelectedItems());
		}
	public		int[]		getSelectedItemIndices()
		{
		int numItems = getNumberOfSelectedItems();
		int[] rows = new int[numItems];
		int[] columns = new int[numItems];
		if (getSelectionManager().getSelectedItems(rows, columns))
			return(rows);
		return(rows);
		}
	public		String[]	getSelectedItems()
		{
		int[] indices = getSelectedItemIndices();
		String[] array = new String[indices.length];
		for (int i = 0; i < indices.length; ++i)
			{
			array[i] = getCell(indices[i], 0).getValue();
			}
		return(array);
		}
	public		String		getSelectedItem()
		{
		int index;
		if ((index = getSelectedItemIndex()) != -1)
			return(getStringItem(index));
		return(null);
		}
	public		MiPart		getSelectedObjectItem()
		{
		int index;
		if ((index = getSelectedItemIndex()) != -1)
			return(getPartItem(index));
		return(null);
		}
	public		boolean		isSelectedItem(int index)
		{
		return(getSelectionManager().isSelectedItem(index, 0));
		}
	// --------------------------------------------------
	// 	SELECTING List Items
	// --------------------------------------------------
	public		void		toggleSelectItem(int index)
		{
		getSelectionManager().toggleSelectItem(index, 0);
		}
	public		void		selectItem(String item)
		{
		int index = getIndexOfItem(item);
		if (index != -1)
			selectItem(index);
		}
	public		boolean		activateItem(int index)
		{
		return(getSelectionManager().activateItem(index, 0));
		}
	public		boolean		selectItem(int index)
		{
		return(getSelectionManager().selectItem(index, 0));
		}
	public		void		deSelectItem(int index)
		{
		getSelectionManager().deSelectItem(index, 0);
		}
	public		void		deSelectAll()
		{
		getSelectionManager().deSelectAll();
		}
	// --------------------------------------------------
	// 	GENERAL List Functionality
	// --------------------------------------------------
	public		boolean		isSelectable(int index)
		{
		return(isSelectable(index, 0));
		}
	public		int		getNumberOfItems()
		{
		return(getNumberOfRows());
		}
	public		int		getSortDirection()
		{
		return(getSortManager().getSortDirection());
		}
	public		void		setSortDirection(int direction)
		{
		getSortManager().setSortDirection(direction);
		}

	public		void		makeVisible(int rowNumber)
		{
		makeVisible(rowNumber, 0);
		}

	public		void		setValue(String value)
		{
		selectItem(value);
		}
	public		String		getValue()
		{
		return(getSelectedItem());
		}
	public		void		setContents(Strings contents)
		{
		removeAllItems();
		appendItems(contents);
		}
	public		Strings		getContents()
		{
		Strings strings = new Strings();
		for (int i = 0; i < getNumberOfRows(); ++i)
			{
			strings.addElement(getCell(i, 0).getValue());
			}
		return(strings);
		}
	protected	MiText		makeMiText(String text)
		{
		MiText t = new MiText(text);
		if (t.getFont() != getFont())
			t.setFont(getFont());
		if (t.getColor() != getColor())
			t.setColor(getColor());
		return(t);
		}
	protected	void		reSortList()
		{
		getSortManager().sortColumn(0);
		}
					/**------------------------------------------------------
	 				 * Import the data specified by the given data transfer
					 * operation. If the data is textual then a MiText shape
					 * is created and appended to this container. If the data
					 * is a MiPart then it is appended to this container.
					 * @param transfer	the data to import
					 * @overrides		MiContainer#doImport
					 *------------------------------------------------------*/
	public		void		doImport(MiDataTransferOperation transfer)
		{
		MiPart obj = null;
		if (transfer.getDataFormat().equals(Mi_STRING_FORMAT))
			obj = new MiText((String )transfer.getData());
		else if (transfer.getDataFormat().equals(Mi_MiPART_FORMAT))
			obj = (MiPart )transfer.getData();
		else
			{
			throw new IllegalArgumentException(this 
				+ ": Unknown import format \"" + transfer.getDataFormat() + "\"");
			}

		MiBounds pickBounds = new MiBounds(transfer.getLookTargetPosition());
		pickBounds.setWidth(4);
		pickBounds.setHeight(4);
		MiTableCell cell = pickCell(pickBounds);

		// ---------------------------------------------------------------
		// Dropped somewhere between cells? If so, add to end of the palette
		// ---------------------------------------------------------------
		if (cell != null)
			{
			insertItem(obj, cell.getRowNumber() + 1);
			}
		else
			{
			appendItem(obj);
			}

		// ---------------------------------------------------------------
		// Add to list is undoable... FIX: need to combine with cut if
		// user is just moving an item in this list...
		// ---------------------------------------------------------------
		MiTableAddItemCommand cmd = new MiTableAddItemCommand(this, obj, true);
		MiSystem.getTransactionManager().appendTransaction(cmd);
		}

	public		String		toString()
		{
		StringBuffer str = new StringBuffer(super.toString());
		str.append('{');
		for (int i = 0; i < getNumberOfItems(); ++i)
			{
			str.append("   ");
			str.append(getItem(i).toString());
			str.append('\n');
			}
		str.append('}');
		str.append('\n');
		return(str.toString());
		}

	}

