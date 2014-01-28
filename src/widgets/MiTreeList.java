
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
import com.swfm.mica.util.FastVector; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTreeList extends MiTable
	{
	private		MiTableCells 	tmpCells 			= new MiTableCells(this);
	private		MiBounds 	tmpBounds 			= new MiBounds();

	private		MiDistance	expandButtonMargins		= 5;
	private		MiDistance	indentation			= 0; // 10;
	private		MiDistance	labelIndentation		= 0; // 10;
	private		MiDistance	iconMargins			= 0;
	private		MiDistance	noIconOffset			= 5;
	private 	int		numberOfVisibleCharactersWide	= 0;

	private		MiPart		expandButton;
	private		MiPart		collapseButton;
	private		MiTreeListEntry	previousEntries[]		= new MiTreeListEntry[0];

	private		MiAttributes	lineAttributes;
	private		boolean		cacheCollapsedChildren		= true;
	private		MiTreeListEntry	rootEntry			= new MiTreeListEntry();

	protected static final String	Mi_LIST_VIEW_RESOURCE_NAME	= "listViewResource";
	private	static final int	TREE_LIST_COLUMN		= 0;



	public				MiTreeList()
		{
		this(false);
		}
	public				MiTreeList(boolean multipleSelectionsAllowed)
		{
		this(-1, false);
		}
	public				MiTreeList(
						int numVisibleCharactersWide, 
						boolean multipleSelectionsAllowed)
		{
		this(numVisibleCharactersWide, -1, multipleSelectionsAllowed);
		}
	public				MiTreeList(
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

		MiTableCell columnDefaults = new MiTableCell(this, 0, TREE_LIST_COLUMN);
		columnDefaults.setHorizontalJustification(Mi_LEFT_JUSTIFIED);
		setColumnDefaults(columnDefaults);

		MiTableCell contentDefaults = getContentAreaDefaults();
		if (contentDefaults == null)
			{
			contentDefaults = new MiTableCell(this, 0, TREE_LIST_COLUMN);
			setContentAreaDefaults(contentDefaults);
			}

		if (numVisibleCharactersWide > 0)
			{
			setNumberOfVisibleCharactersWide(numVisibleCharactersWide);
			}

		MiToolkit.setBackgroundAttributes(this, 
			MiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);
		MiToolkit.setBackgroundAttributes(getContentsBackground(), 
			MiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);

		getTableWideDefaults().insetMargins.setMargins(0);
		getTableHeaderAndFooterManager().setResizableRows(false);
		getTableHeaderAndFooterManager().setMovableColumns(false);
		getSectionMargins().setMargins(0);
		getContentsBackground().setBorderLook(Mi_NONE);
		//OK But need non-transparent bg here to test... setContentsBackgroundBorder(null);

		lineAttributes = getAttributes().copy();
		//lineAttributes = lineAttributes.setColor(MiColorManager.gray);
		//lineAttributes = lineAttributes.setBorderLook(Mi_FLAT_BORDER_LOOK);

		expandButton = new MiTreeListExpandCollapseButton(true);
		collapseButton = new MiTreeListExpandCollapseButton(false);
		//labelIndentation = expandButton.getWidth() + expandButtonMargins * 2;
		labelIndentation = expandButton.getWidth() + expandButtonMargins;
		indentation = expandButton.getWidth()/2 + expandButtonMargins;

		setCellMargins(4);

		insertEventHandler(new MiTreeListExpandEventHandler(this), 0);
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
		contentDefaults.setFixedWidth(numVisibleCharacters * getFont().getAverageCharWidth());
		numberOfVisibleCharactersWide = numVisibleCharacters;
		}
	public		int		getNumberOfVisibleCharactersWide()
		{
		return(numberOfVisibleCharactersWide);
		}
	public		void		setValue(String value)
		{
		selectItem(value);
		}
	public		String		getValue()
		{
		return(getSelectedItem());
		}
					/**------------------------------------------------------
					 * Sets contents items by parsing strings of form:
					 * <p><pre>
					 * parent 
					 * \tchild1
					 * \tchild1
					 * \t\tgrandchild
					 * </pre>
					 *------------------------------------------------------*/
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
	public		void		appendItem(String item)
		{
		addItem(item, null, item, null, false);
		}
					/**------------------------------------------------------
					 * Appends items by parsing strings of form:
					 * <p><pre>
					 * parent 
					 * \tchild1
					 * \tchild1
					 * \t\tgrandchild
					 * </pre>
					 *------------------------------------------------------*/
	public		void		appendItems(Strings items)
		{
		String parentItem = null;
		Strings parentItems = new Strings();
		for (int i = 0; i < items.size(); ++i)
			{
			String item = items.elementAt(i);
			int numTabs = Utility.numOccurancesOf(item, '\t', 0, item.length());
			int childNumTabs = 0;
			if (i + 1 < items.size())
				{
				childNumTabs = Utility.numOccurancesOf(
					items.elementAt(i + 1), '\t', 0, items.elementAt(i + 1).length());
				}
			boolean hasChildren = childNumTabs > numTabs;
			if ((numTabs > 0) && (numTabs - 1 < parentItems.size()))
				parentItem = parentItems.elementAt(numTabs - 1);
			else
				parentItem = null;

			String displayItem = item;
			while (displayItem.charAt(0) == '\t')
				{
				displayItem = displayItem.substring(1);
				}
			addItem(displayItem, null, item, parentItem, hasChildren);

			if (numTabs < parentItems.size())
				parentItems.setElementAt(item, numTabs);
			else
				parentItems.addElement(item);
			}
		}
	public		int		getNumberOfItems()
		{
		return(getNumberOfRows());
		}
	public		MiPart		getItem(int index)
		{
		return(((MiTreeListEntryGraphics )getCell(index, 0).getGraphics()).getLabel());
		}
	// --------------------------------------------------
	// 	List Appearance Options
	// --------------------------------------------------
	public		void		setLineAttributes(MiAttributes atts)
		{
		lineAttributes = atts;
		}
	public		MiAttributes	getLineAttributes()
		{
		return(lineAttributes);
		}
	public		void		setIndentation(MiDistance width) // setIndentationInCharacterWidths
		{
		indentation = width;
		}
	public		void		setLabelIndentation(MiDistance width)
		{
		labelIndentation = width;
		}

	public		void		setExpandButton(MiPart button)
		{
		expandButton = button;
		labelIndentation = expandButton.getWidth() + expandButtonMargins;
		indentation = expandButton.getWidth()/2 + expandButtonMargins;
		invalidateArea();
		}
	public		MiPart		getExpandButton()
		{
		return(expandButton);
		}

	public		void		setCollapseButton(MiPart button)
		{
		collapseButton = button;
		invalidateArea();
		}
	public		MiPart		getCollapseButton()
		{
		return(collapseButton);
		}

	// --------------------------------------------------
	// 	ADDING List Items
	// --------------------------------------------------
	public		void		addItem(
						String label, 
						MiPart image, 
						Object tag, 
						Object parentsTag, 
						boolean canHaveChildren)
		{
		addItem(makeMiText(label), image, tag, parentsTag, canHaveChildren);
		}
	public		void		addItem(
						MiPart label, 
						Object tag, 
						Object parentsTag, 
						boolean canHaveChildren)
		{
		addItem(label, null, tag, parentsTag, canHaveChildren);
		}
	public		void		addItem(
						MiPart label, 
						MiPart image, 
						Object tag, 
						Object parentsTag, 
						boolean canHaveChildren)
		{
		addItem(label, image, tag, parentsTag, canHaveChildren, new MiTableCells(this));
		}
	public		void		addItem(
						MiPart label, 
						MiPart image, 
						Object tag, 
						Object parentsTag, 
						boolean canHaveChildren, 
						MiTableCells cells)
		{
		MiTreeListEntry 	parentEntry 	= getTreeListEntryWithTag(parentsTag);
		boolean	hasParent = (parentEntry != null) && (parentsTag != null);

		MiTreeListEntryGraphics graphics = new MiTreeListEntryGraphics();

		if (parentEntry == null)
			parentEntry = rootEntry;

		MiTreeListEntry entry = new MiTreeListEntry(graphics, parentEntry, canHaveChildren);
		entry.icon = image;

		if (image != null)
			graphics.setIcon(image);
		graphics.setLabel(label);

		int index = (parentEntry == rootEntry ? 0 : parentEntry.cell.rowNumber);
		int rowNumber = index + parentEntry.getNumberOfProgeny();
		MiTableCell cell = new MiTableCell(this, rowNumber, 0, graphics);
		cells.insertElementAt(cell, 0);
		entry.cell = cell;
		entry.cells = cells;
		cell.tag = tag;
		cell.setMargins(new MiMargins((parentEntry.level) * indentation + labelIndentation + getCellMargins().left, 0, 0, 0));

		if (hasParent)
			{
			if (rowNumber > getTotalNumberOfRows())
				appendRow(cells);
			else
				insertRow(cells, rowNumber);
			}
		else
			{
			appendRow(cells);
			}

		if (entry.level > previousEntries.length - 1)
			previousEntries = new MiTreeListEntry[entry.level + 1];

		dispatchAction(Mi_ITEM_ADDED_ACTION, tag);
		}
	public		void		setItemLabel(int index, String label)
		{
		MiTableCell cell = getCell(index, 0);
		MiTreeListEntry entry = ((MiTreeListEntry)cell.getGraphics().getResource(Mi_LIST_VIEW_RESOURCE_NAME));
		entry.obj.setLabel(makeMiText(label));
		}
	public		void		setItemLabel(Object tag, String label)
		{
		setItemLabel(tag, makeMiText(label));
		}
	public		void		setItemLabel(Object tag, MiPart label)
		{
		MiTreeListEntry entry = getTreeListEntryWithTag(tag);
		entry.obj.setLabel(label);
		}
	public		void		setTagItem(int index, Object tag)
		{
		getCell(index, 0).setTag(tag);
		}

	// --------------------------------------------------
	// 	Manipulating List Items
	// --------------------------------------------------

					// Along with all of item's subtree
	public		MiiTreeListEntry cutItemWithTag(Object tag)
		{
		MiTreeListEntry entry = getTreeListEntryWithTag(tag);
		entry.updateCellRecordKeeping(this);
//MiDebug.println("cut tag = " + tag);
//MiDebug.println("cut entry = " + entry);
//MiDebug.println("entry.cells = " + entry.cells);
//MiDebug.println("entry.cells.size() = " + entry.cells.size());
		removeSubtreeRows(entry);
		dispatchAction(Mi_ITEM_REMOVED_ACTION, tag);
		entry.removeSelf();
		invalidateLayout();
		return(entry);
		}

					// Not including all of item's subtree
	public		MiiTreeListEntry copyItemWithTag(Object tag)
		{
		MiTreeListEntry entry = getTreeListEntryWithTag(tag);
		entry.updateCellRecordKeeping(this);
		return(entry.copy());
		}

					// Along with all of item's subtree as child of target
					// Caller must fixup the possiblly duplicate tags
	public		void		pasteItemWithTag(Object targetTag, MiiTreeListEntry itemEntry)
		{
		MiTreeListEntry targetEntry = getTreeListEntryWithTag(targetTag);
		MiTreeListEntry entry = (MiTreeListEntry )itemEntry;
//MiDebug.println("paste entry = " + entry);
//MiDebug.println("entry.cells = " + entry.cells);
//MiDebug.println("entry.cells.size() = " + entry.cells.size());
		targetEntry.addChild(entry);
		insertSubtreeRows(entry, targetEntry.cells.elementAt(0).getRowNumber() + 1);
		previousEntries = new MiTreeListEntry[getNumberOfRows()];
		}

	private		void		removeSubtreeRows(MiTreeListEntry entry)
		{
		int rowNumberToRemove = entry.cells.elementAt(0).getRowNumber();
		for (int i = 0; i <= entry.getNumberOfProgeny(); ++i)
			{
			removeRow(rowNumberToRemove);
			}
		previousEntries = new MiTreeListEntry[getNumberOfRows()];
		}
	private		int		insertSubtreeRows(MiTreeListEntry entry, int rowNumber)
		{
		insertRow(entry.cells, rowNumber);
		entry.cell.setMargins(new MiMargins((entry.level) * indentation + labelIndentation + getCellMargins().left, 0, 0, 0));
		for (int i = 0; i < entry.getNumberOfChildren(); ++i)
			{
			MiTreeListEntry child = entry.getChild(i);
			insertSubtreeRows(child, ++rowNumber);
			}
		return(rowNumber);
		}
	// --------------------------------------------------
	// 	Removeing List Items
	// --------------------------------------------------
					// And all of item's subtree
	public		void		removeItemWithTag(Object tag)
		{
		MiTreeListEntry entry = getTreeListEntryWithTag(tag);
		removeSubtree(entry);
		}

	public		void		removeItemWithTagsChildren(Object tag)
		{
		MiTreeListEntry entry = getTreeListEntryWithTag(tag);
		while (entry.getNumberOfChildren() > 0)
			{
			MiTreeListEntry child = entry.getChild(0);
			removeSubtree(child);
			}
		}
		
	private		void		removeSubtree(MiTreeListEntry entry)
		{
		while (entry.getNumberOfChildren() > 0)
			{
			MiTreeListEntry child = entry.getChild(0);
			removeSubtree(child);
			}
		Object tag = entry.cells.elementAt(0).tag;
		removeRow(entry.cells.elementAt(0).getRowNumber());
		dispatchAction(Mi_ITEM_REMOVED_ACTION, tag);
		entry.removeSelf();
		}
	public		void		removeAllItems()
		{
		removeAllCells();
		rootEntry.removeAllChildren();
		}
	// --------------------------------------------------
	// 	Expanding/Collapsing List Items
	// --------------------------------------------------
	public		void		expand(Object tag)
		{
		expand(getTreeListEntryWithTag(tag));
		}
	public		void		collapse(Object tag)
		{
		collapse(getTreeListEntryWithTag(tag));
		}
	public		boolean		isCollapsed(Object tag)
		{
		return(!getTreeListEntryWithTag(tag).expanded);
		}
	public		void		collapseAll()
		{
		collapseExpandAll(true);
		}
	public		void		expandAll()
		{
		collapseExpandAll(false);
		}
	public		void		collapseExpandAll(boolean toCollapse)
		{
		for (int i = 0; i < getNumberOfItems(); ++i)
			{
			Object tag = getTagItem(i);
			if (getNumberOfChildren(tag) != 0)
				{
				if (toCollapse)
					{
					if (!isCollapsed(tag))
						{
						collapse(tag);
						}
					}
				else
					{
					if (isCollapsed(tag))
						{
						expand(tag);
						}
					}
				}
			}
/***
		for (int i = 0; i < getNumberOfItems(); ++i)
			{
			Object tag = getTagItem(i);
			if ((getNumberOfChildren(tag) == 0)
				&& (!getItemCanHaveChildren(tag)))
				{
				Object parentTag = getParentOfItem(tag);
				if (toCollapse)
					{
					if (!isCollapsed(parentTag))
						{
						collapse(parentTag);
						}
					}
				else
					{
					if (isCollapsed(parentTag))
						{
						expand(parentTag);
						}
					}
				}
			}
***/
		}

	// --------------------------------------------------
	// 	Expanding/Collapsing List Items Implementation
	// --------------------------------------------------
	private		void		expand(MiTreeListEntry entry)
		{
		entry.expanded = true;
		invalidateArea(getRowBounds(entry.cell.rowNumber, 0, -1, tmpBounds));
		if (cacheCollapsedChildren)
			{
			int largestRowNumberOfExpandedCell = addSubTreeToList(entry);
		
			if (largestRowNumberOfExpandedCell >= 0)
				{
				invalidateArea();
				validateLayout();
				makeVisible(largestRowNumberOfExpandedCell, 0);
				makeVisible(entry.cell.rowNumber, 0);
				}
			}
		dispatchAction(Mi_NODE_EXPANDED_ACTION, entry.cell.tag);
		}
	private		int		addSubTreeToList(MiTreeListEntry entry)
		{
		int largestRowNumberOfExpandedCell = -1;

		if ((entry.children != null) && (entry.expanded))
			{
			for (int i = 0; i < entry.children.size(); ++i)
				{
				MiTreeListEntry child = (MiTreeListEntry )entry.children.elementAt(i);
				MiTableCells cells = getRowCells(child.cell.getRowNumber());
				cells.setVisible(true);
				largestRowNumberOfExpandedCell = Math.max(largestRowNumberOfExpandedCell, child.cell.rowNumber);
				if (child.expanded)
					{
					largestRowNumberOfExpandedCell = Math.max(largestRowNumberOfExpandedCell, addSubTreeToList(child));
					}
				}
			}
		return(largestRowNumberOfExpandedCell);
		}
	private		void		collapse(MiTreeListEntry entry)
		{
		entry.expanded = false;
		invalidateArea(getRowBounds(entry.cell.rowNumber, 0, -1, tmpBounds));
		if (entry.children != null)
			removeSubTreeFromList(entry);
		invalidateArea();
		dispatchAction(Mi_NODE_COLLAPSED_ACTION, entry.cell.tag);
		}
	private		void		removeSubTreeFromList(MiTreeListEntry entry)
		{
		for (int i = 0; i < entry.children.size(); ++i)
			{
			MiTreeListEntry child = (MiTreeListEntry )entry.children.elementAt(i);
			//child.cells.setVisible(false);
			MiTableCells cells = getRowCells(child.cell.getRowNumber());
			cells.setVisible(false);
			if ((child.children != null) && (child.children.size() > 0) && (child.expanded))
				removeSubTreeFromList(child);
			}
		if (!cacheCollapsedChildren)
			removeSubtree(entry);
		}
	// --------------------------------------------------
	// 	Utility Routines
	// --------------------------------------------------
	public		MiPart		getIconWithTag(Object tag)
		{
		MiTreeListEntryGraphics entry = (MiTreeListEntryGraphics )getItemWithTag(tag);
		return(entry != null ? entry.getIcon() : null);
		}
	public		void		setIconWithTag(Object tag, MiPart icon)
		{
		MiTreeListEntryGraphics entry = (MiTreeListEntryGraphics )getItemWithTag(tag);
		entry.setIcon(icon);
		}
	public		MiPart		getLabelWithTag(Object tag)
		{
		MiTreeListEntryGraphics entry = (MiTreeListEntryGraphics )getItemWithTag(tag);
		return(entry != null ? entry.getLabel() : null);
		}
	public		String		getStringWithTag(Object tag)
		{
		MiPart label = getLabelWithTag(tag);
		if (label == null)
			return(null);
		if (label instanceof MiWidget)
			return(((MiWidget )label).getValue());
		if (label instanceof MiText)
			return(((MiText )label).getText());
		return(label.toString());
		}

	public		MiTreeListEntryGraphics	getItemWithTag(Object tag)
		{
		MiTreeListEntry entry = getTreeListEntryWithTag(tag);
		return((entry == null) ? null : entry.obj);
		}
	public		void		changeTagOfItem(Object oldTag, Object newTag)
		{
		MiTreeListEntry entry = getTreeListEntryWithTag(oldTag);
		entry.cell.tag = newTag;
		}
	public		boolean		getItemCanHaveChildren(Object tag)
		{
		MiTreeListEntry	entry = getTreeListEntryWithTag(tag);
		return(entry.canHaveChildren);
		}

	public		int		getNumberOfChildren(Object tag)
		{
		MiTreeListEntry	entry = getTreeListEntryWithTag(tag);
		if (entry.children != null)
			return(entry.children.size());
		return(0);
		}
	public		Object		getChild(Object tag, int index)
		{
		MiTreeListEntry	entry = getTreeListEntryWithTag(tag);
		if (entry.children != null)
			return(entry.getChild(index).cell.getTag());
		return(null);
		}
	public		Object		getParentOfItem(Object tag)
		{
		MiTreeListEntry entry = getTreeListEntryWithTag(tag);
		// Keep an eye out for the root entry...
		if ((entry != null) && (entry.parent != null) && (entry.parent.cell != null))
			return(entry.parent.cell.tag);
		return(null);
		}

	// --------------------------------------------------
	// 	INQUIRING List Items
	// --------------------------------------------------
	public		int		getIndexOfItem(Object tag)
		{
		MiTableCells cells = getContentsCells();
		for (int i = 0; i < cells.size(); ++i)
			{
			if (cells.elementAt(i).getTag() == tag)
				return(cells.elementAt(i).rowNumber);
			}
		return(-1);
		}
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
	public		String		getStringItem(int index)
		{
		MiPart label = ((MiTreeListEntryGraphics )getCell(index, 0).getGraphics()).getLabel();
		if (label == null)
			return(null);
		if (label instanceof MiWidget)
			return(((MiWidget )label).getValue());
		if (label instanceof MiText)
			return(((MiText )label).getText());
		return(label.toString());
		}
	public		MiTreeListEntryGraphics	getPartItem(int index)
		{
		return((MiTreeListEntryGraphics )getCell(index, 0).getGraphics());
		}
	public		Object		getTagItem(int index)
		{
		return(getCell(index, 0).getTag());
		}
	public		boolean		isTextItem(int index)
		{
		return(((MiTreeListEntryGraphics )getCell(index, 0).getGraphics()).getLabel() 
			instanceof MiText);
		}
	public		int		getIndexOfItem(String text)
		{
		for (int i = 0; i < getNumberOfRows(); ++i)
			{
			if ((isTextItem(i)) && (getStringItem(i).equals(text)))
				return(i);
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
	public		int		getNumberOfSelectedItems()
		{
		return(getSelectionManager().getNumberOfSelectedItems());
		}
	public		MiPart		getSelectedObjectItem()
		{
		int index;
		if ((index = getSelectedItemIndex()) != -1)
			return(getPartItem(index));
		return(null);
		}
	public		String		getSelectedItem()
		{
		int index;
		if ((index = getSelectedItemIndex()) != -1)
			return(getStringItem(index));
		return(null);
		}
	public		Object		getSelectedItemTag()
		{
		MiTableCell cell =  getSelectionManager().getSelectedCell();
		if (cell != null)
			return(cell.tag);
		return(null);
		}
	public		int[]		getSelectedItemIndexes()
		{
		int num = getNumberOfSelectedItems();
		int[] rows = new int[num];
		int[] columns = new int[num];
		getSelectionManager().getSelectedItems(rows, columns);
		return(rows);
		}
	public		Strings		getSelectedItems()
		{
		Strings items = new Strings();
		int num = getNumberOfSelectedItems();
		int[] rows = new int[num];
		int[] columns = new int[num];
		getSelectionManager().getSelectedItems(rows, columns);
		for (int i = 0; i < getNumberOfSelectedItems(); ++i)
			{
			items.addElement(getStringItem(rows[i]));
			}
		return(items);
		}
	public		Object[]	getSelectedItemsTags()
		{
		Object[] tags = new Object[getNumberOfSelectedItems()];
		MiTableCells cells =  getSelectionManager().getSelectedCells();
		for (int i = 0; i < cells.size(); ++i)
			tags[i] = cells.elementAt(i).tag;
		return(tags);
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
	public		void		selectItemWithTag(Object tag)
		{
		MiTableCell cell = getCellWithTag(tag);
		getSelectionManager().selectItem(cell);
		}
	public		void		toggleSelectItemWithTag(Object tag)
		{
		MiTableCell cell = getCellWithTag(tag);
		getSelectionManager().toggleSelectItem(cell.rowNumber, cell.columnNumber);
		}
	public		void		activateItemWithTag(Object tag)
		{
		MiTableCell cell = getCellWithTag(tag);
		getSelectionManager().activateItem(cell.rowNumber, cell.columnNumber);
		}

	// --------------------------------------------------
	// 	Support for MiTreeListExpandEventHandler
	// --------------------------------------------------
	protected	MiTreeListEntry	getEntryAtLocation(MiBounds area)
		{
		MiTableCell cell = pickCell(area);
		if ((cell != null) && (cell.getGraphics() != null))
			{
			return((MiTreeListEntry)cell.getGraphics().getResource(Mi_LIST_VIEW_RESOURCE_NAME));
			}
		return(null);
		}
	protected	boolean		toggleNodeExpansionAtExpandCollapseBox(MiBounds area)
		{
		MiTreeListEntry entry = getEntryAtLocation(area);
		if ((entry != null) && (entry.canHaveChildren)
			&& ((area.getXmin() < entry.obj.getXmin()) 
			&& (area.getXmin() > entry.obj.getXmin() - labelIndentation - expandButton.getWidth())))
			{
			toggleExansion(entry);
			return(true);
			}
		return(false);
		}
	private		void		toggleExansion(MiTreeListEntry entry)
		{
		if (entry.expanded)
			collapse(entry);
		else if (entry.canHaveChildren)
			expand(entry);
		}
	protected	boolean		toggleNodeExpansionAtLocation(MiBounds area)
		{
		if (toggleNodeExpansionAtExpandCollapseBox(area))
			return(true);
		MiTreeListEntry entry = getEntryAtLocation(area);
		if ((entry != null) && (entry.canHaveChildren))
			{
			toggleExansion(entry);
			return(true);
			}
		return(false);
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
		Object parentTag = null;
		if (cell != null)
			{
			parentTag = cell.getTag();
			if (!getItemCanHaveChildren(parentTag))
				parentTag = getParentOfItem(parentTag);
			}

		// ---------------------------------------------------------------
		// Add the transfered object to the correct sub tree, if any.
		// ---------------------------------------------------------------
		addItem(obj.getName(), obj, obj, parentTag, false);

		// ---------------------------------------------------------------
		// Add to tree list is undoable... FIX: need to combine with cut if
		// user is just moving an item in this list...
		// ---------------------------------------------------------------
		MiTableAddItemCommand cmd = new MiTableAddItemCommand(this, obj, true);
		MiSystem.getTransactionManager().appendTransaction(cmd);
		}
	public		void		renderOverlay(MiRenderer renderer)
		{
		super.renderOverlay(renderer);

		if (!getContentsBounds(tmpBounds).intersectionWith(renderer.getClipBounds()))
			{
			return;
			}

		if (getNumberOfRows() == 0)
			{
			return;
			}
		renderer.setClipBounds(tmpBounds);

		MiTreeListEntry 	previousEntry 	= null;
		MiTreeListEntry		entry 		= rootEntry;

		getRowBounds(0, TREE_LIST_COLUMN, TREE_LIST_COLUMN, tmpBounds);

		MiCoord 		listXmin 	= tmpBounds.getXmin()
								+ getTableWideDefaults().insetMargins.left;
		MiCoord 		listYmax 	= tmpBounds.getYmax();
		MiCoord 		xmin;
		MiCoord 		ymin 		= 0;
		MiCoord 		xmax;
		MiCoord 		ymax;
		MiVector		translation	= getCellToActualCoordTranslation(new MiVector());

		int 			lastLevel 	= -1;
		MiDistance 		boxHalfWidth 	= expandButton.getWidth()/2;
		MiDistance 		boxHalfHeight 	= expandButton.getHeight()/2;
		boolean			isRootEntry 	= false;
		boolean			renderLinesExtendingDownOutOfBox = false;


		for (int i = 0; i < previousEntries.length; ++i)
			previousEntries[i] = null;

		while ((entry = entry.getNext()) != null)
			{
			int lastVisibleRow = getTopVisibleRow() + getNumberOfVisibleRows() + 1;
			MiTableCell cell = entry.cell;
			isRootEntry = false;

			if ((cell.isVisible()) && (cell.rowNumber >= getTopVisibleRow()))
				{
				if (cell.rowNumber > lastVisibleRow)
					{
					if (previousEntry == null)
						return;

					renderLinesExtendingDownOutOfBox = true;
					if (lastLevel == -1)
						lastLevel = previousEntry.level;

					if ((entry.level > lastLevel)
						|| (entry.parent != previousEntries[entry.level].parent))
						{
						continue;
						}
					}

/*
				xmax = listXmin + entry.level * indentation;
				xmin = xmax - indentation + noIconOffset;
*/
				xmax = listXmin + (entry.level > 0 ? ((entry.level - 1) * indentation) : 0) + 3*labelIndentation/4;
				xmin = xmax - 3*labelIndentation/4 + noIconOffset;
					//+ (entry.icon == null 
					//? noIconOffset : iconMargins + entry.icon.getWidth()/2);

				ymin = translation.y + cell.bounds.getCenterY();

				if ((previousEntry == null) || (previousEntry.level > entry.level))
					{
					MiTreeListEntry topEntry = previousEntries[entry.level];
					if (topEntry == null)
						{
						ymax = listYmax;
						if (entry == rootEntry.getNext())
							isRootEntry = true;
						}
					else
						{
						ymax = topEntry.obj.getCenterY() - boxHalfHeight;
						ymax += translation.y;
						}
					}
				else
					{
					ymax = previousEntry.obj.getCenterY();
					if (previousEntry.canHaveChildren)
						{
						if (entry.level == previousEntry.level)
							ymax -= boxHalfHeight;
						else if (previousEntry.icon != null)
							ymax -= previousEntry.icon.getHeight()/2;
						else
							ymax = previousEntry.obj.getYmin();
						}
					ymax += translation.y;
					}


				renderer.setAttributes(lineAttributes);
				if (renderLinesExtendingDownOutOfBox)
					{
					renderer.drawLine(xmin, ymax, xmin, ymin);
					lastLevel = entry.level - 1;
					if (lastLevel < 1)
						return;
					}
				else if (!entry.canHaveChildren)
					{
					renderer.drawLine(xmin, ymax, xmin, ymin);
					renderer.drawLine(xmin, ymin, xmax, ymin);
					}
				else
					{
					if (!isRootEntry)
						renderer.drawLine(xmin, ymax, xmin, ymin + boxHalfWidth);
					renderer.drawLine(xmin + boxHalfWidth, ymin, xmax, ymin);
					if (entry.expanded)
						{
						expandButton.setCenter(xmin, ymin);
						expandButton.render(renderer);
						}
					else
						{
						collapseButton.setCenter(xmin, ymin);
						collapseButton.render(renderer);
						}
					}
				previousEntry = entry;
				}
			previousEntries[entry.level] = entry;
			}
		}
	// --------------------------------------------------
	// 	Utility Routines Implementation
	// --------------------------------------------------
	private		MiTreeListEntry	getTreeListEntryWithTag(Object tag)
		{
		if (tag == null)
			return(rootEntry);

		MiTableCell cell = getCellWithTag(tag);
		if (cell != null)
			return((MiTreeListEntry)cell.getGraphics().getResource(Mi_LIST_VIEW_RESOURCE_NAME));
		return(null);
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
	}

class MiTreeListEntry implements MiiTreeListEntry
	{
	MiTreeListEntry	parent;
	FastVector	children;
	int		level;
	boolean		canHaveChildren;
	boolean		expanded;
	boolean		isRoot;
	MiTreeListEntryGraphics		obj;
	MiPart		icon;
	MiTableCell	cell;
	MiTableCells	cells;




			MiTreeListEntry()
		{
		isRoot = true;
		}
			MiTreeListEntry(MiTreeListEntryGraphics obj, MiTreeListEntry parent, boolean canHaveChildren)
		{
		this.obj = obj;
		obj.setResource(MiTreeList.Mi_LIST_VIEW_RESOURCE_NAME, this);
		this.parent = parent;
		if (parent != null)
			level = parent.level + 1;
		this.canHaveChildren = canHaveChildren;
		if (parent != null)
			parent.addChild(this);
		}

	public		void			setTag(Object tag)
		{
		cell.setTag(tag);
		}
	public		Object			getTag()
		{
		return(cell.getTag());
		}

	public		MiTreeListEntry		getNext()
		{
		if ((children != null) && (children.size() > 0))
			return((MiTreeListEntry )children.elementAt(0));

		MiTreeListEntry p = parent;
		MiTreeListEntry child = this;
		MiTreeListEntry next;
		while (p != null)
			{
			if ((next = p.getNextSibling(child)) != null)
				return(next);
			child = p;
			p = child.parent;
			}
		return(null);
		}

	public		int			getNumberOfProgeny()
		{
		int num = 0;
		if (children == null)
			return(0);
		for (int i = 0; i < children.size(); ++i)
			{
			num += ((MiTreeListEntry )children.elementAt(i)).getNumberOfProgeny();
			}
		num += children.size();
		return(num);
		}

	public		MiTreeListEntry		getPreviousSibling()
		{
		if (parent != null)
			return(parent.getPreviousSibling(this));
		return(null);
		}
	public		MiTreeListEntry		getPreviousSibling(MiTreeListEntry sibling)
		{
		if (children != null)
			{
			for (int i = 0; i < children.size(); ++i)
				{
				if (children.elementAt(i) == sibling)
					{
					if (i > 0) 
						return((MiTreeListEntry)children.elementAt(i - 1));
					return(null);
					}
				}
			}
		return(null);
		}
	public		MiTreeListEntry		getNextSibling()
		{
		if (parent != null)
			return(parent.getNextSibling(this));
		return(null);
		}
	public		MiTreeListEntry		getNextSibling(MiTreeListEntry sibling)
		{
		if (children != null)
			{
			for (int i = children.size() - 1; i >= 0; --i)
				{
				if (children.elementAt(i) == sibling)
					{
					if (i < children.size() - 1) 
						return((MiTreeListEntry)children.elementAt(i + 1));
					return(null);
					}
				}
			}
		return(null);
		}
	public		void			addChild(MiTreeListEntry child)
		{
		if (children == null)
			children = new FastVector();
		children.addElement(child);
		canHaveChildren = true;
		expanded = true;
		child.parent = this;
		child.setLevel(level + 1);
		
		}
	private		void			setLevel(int level)
		{	
		this.level = level;
		for (int i = 0; i < getNumberOfChildren(); ++i)
			{
			getChild(i).setLevel(level + 1);
			}
		}

	public		int			getNumberOfChildren()
		{
		return(children == null ? 0 : children.size());
		}
	public		MiTreeListEntry		getChild(int index)
		{
		return((MiTreeListEntry )children.elementAt(index));
		}
	public		MiTreeListEntry		getParent()
		{
		return(parent);
		}
	public		void			removeChild(MiTreeListEntry entry)
		{
		children.removeElement(entry);
		}
	
	public		void			removeSelf()
		{
		parent.children.removeElement(this);
		if (parent.children.size() == 0)
			{
			parent.children = null;
			parent.canHaveChildren = false;
			}
		parent = null;
		}
	
	public		void			removeAllChildren()
		{
		if (children != null)
			children.removeAllElements();
		children = null;
		}

	public		void			updateCellRecordKeeping(MiTable table)
		{
		cells = table.getRowCells(cell.getRowNumber());
		cells.removeElement(cell);
		cells.insertElementAt(cell, 0);
		}

	public		MiiTreeListEntry	copy()
		{
		MiTreeListEntry theCopy = new MiTreeListEntry();
		theCopy.isRoot = isRoot;
		theCopy.level = level;
		theCopy.obj = new MiTreeListEntryGraphics();
		if (obj.getIcon() != null)
			{
			theCopy.obj.setIcon(obj.getIcon().deepCopy());
			}
		if (obj.getLabel() != null)
			{
			theCopy.obj.setLabel(obj.getLabel().deepCopy());
			}
		theCopy.icon = theCopy.obj.getIcon();
		theCopy.cells = new MiTableCells(cells.getTable());
		for (int i = 0; i < cells.size(); ++i)
			{
			theCopy.cells.addElement(cells.elementAt(i).copy());
			}
		theCopy.cell = cells.elementAt(0);
		theCopy.cell.setGraphics(theCopy.obj);
		theCopy.obj.setResource(MiTreeList.Mi_LIST_VIEW_RESOURCE_NAME, theCopy);

		return(theCopy);
		}

	}

class MiTreeListExpandEventHandler extends MiEventHandler
	{
	private		MiTreeList	tree;
	public				MiTreeListExpandEventHandler(MiTreeList treeList)
		{
		tree = treeList;
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0,0);
		addEventToCommandTranslation(Mi_TOGGLE_COMMAND_NAME, MiEvent.Mi_LEFT_MOUSE_DBLCLICK_EVENT, 0,0);
		}
	public		int		processCommand()
		{
		if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			if (tree.toggleNodeExpansionAtExpandCollapseBox(event.worldMouseFootPrint))
				return(Mi_CONSUME_EVENT);
			return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(Mi_TOGGLE_COMMAND_NAME))
			{
			if (tree.toggleNodeExpansionAtLocation(event.worldMouseFootPrint))
				return(Mi_CONSUME_EVENT);
			return(Mi_PROPOGATE_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}

