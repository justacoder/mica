
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
public class MiBasicPropertyPanel extends MiPropertyPanel
	{
	private		MiTable		table;
	private		MiScrolledBox	scrolledBox;


	public				MiBasicPropertyPanel(boolean scrollable)
		{
		this(scrollable, 1);
		}
	public				MiBasicPropertyPanel(boolean scrollable, int numberOfColumns)
		{
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);
		MiParts scrolledBoxReturn = new MiParts();
		table = makePropertyPanelTable(this, scrollable, numberOfColumns, scrolledBoxReturn);
		scrolledBox = scrolledBoxReturn.size() > 0 ? (MiScrolledBox )scrolledBoxReturn.get(0) : null;
		}

	public		int		getInspectedObjectIndex()
		{
		return(-1);
		}
	public		MiTable		getTable()
		{
		return(table);
		}
	public		MiScrolledBox	getScrolledBox()
		{
		return(scrolledBox);
		}
	protected	void		generatePanel()
		{
		makePropertyPanelWidgets(this, table);
		}

	public static	MiTable		makePropertyPanelTable(MiPart container, boolean scrollable)
		{
		return(makePropertyPanelTable(container, scrollable, 1));
		}
	public static	MiTable		makePropertyPanelTable(
						MiPart container, boolean scrollable, int numberOfColumns)
		{
		return(makePropertyPanelTable(container, scrollable, numberOfColumns));
		}
	public static	MiTable		makePropertyPanelTable(
						MiPart container, boolean scrollable, int numberOfColumns, MiParts scrolledBoxReturn)
		{
		MiTable table = new MiTable();
		table.getTableWideDefaults().setHorizontalJustification(Mi_LEFT_JUSTIFIED);
		table.getTableWideDefaults().setHorizontalSizing(Mi_SAME_SIZE);
		table.setTotalNumberOfColumns(numberOfColumns * 2);
		table.setHasFixedTotalNumberOfColumns(true);
		table.setMinimumNumberOfVisibleColumns(numberOfColumns * 2);
		table.setMaximumNumberOfVisibleColumns(numberOfColumns * 2);
		table.getSectionMargins().setMargins(0);
		table.getTableWideDefaults().insetMargins.setMargins(4, 2, 4, 2);
		table.getMadeColumnDefaults(1).setColumnHorizontalSizing(Mi_EXPAND_TO_FILL);
		MiRectangle cellBackgroundRect = new MiRectangle();
		cellBackgroundRect.setBorderLook(Mi_RAISED_BORDER_LOOK);
		table.getBackgroundManager().appendCellBackgrounds(cellBackgroundRect);
		table.setPropertyValue(MiTable.Mi_CELL_BG_PROPERTY_NAME + ".backgroundColor", 
			MiColorManager.getColorName(table.getBackgroundColor()));
		table.getSelectionManager().setMaximumNumberSelected(0);
		table.getSelectionManager().setBrowsable(false);
		if (scrollable)
			{
			MiScrolledBox scrolledBox = new MiScrolledBox(table);
			container.appendPart(scrolledBox);
			scrolledBoxReturn.add(scrolledBox);
			}
		else
			{
			table.setPreferredNumberOfVisibleRows(-1);
			container.appendPart(table);
			}
		return(table);
		}
	public static	void		makePropertyPanelWidgets(MiPropertyPanel panel, MiTable table)
		{
		table.removeAllCells();
		MiPropertyWidgets properties = panel.getPropertyWidgets();

		if (properties.size() < table.getPreferredNumberOfVisibleRows())
			{
			table.setPreferredNumberOfVisibleRows(properties.size());
			}

		MiParts parts = new MiParts();
		for (int i = 0; i < properties.size(); ++i)
			{
			MiText label = new MiText(properties.elementAt(i).getDisplayName());
			MiWidget widget = properties.elementAt(i).getWidget();
			label.setStatusHelp(widget.getStatusHelp(widget.getCenter()));
			label.setDialogHelp(widget.getDialogHelp(null));
			parts.addElement(label);
			parts.addElement(widget);
			}
		table.appendItems(parts);
		}
	}

