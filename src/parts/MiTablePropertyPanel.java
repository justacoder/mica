
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

import java.util.Vector;
import com.swfm.mica.util.Strings;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTablePropertyPanel extends MiPropertyPanel
	{
	private		MiTable		table;
	private		Strings		tableHeaders = new Strings();


	public				MiTablePropertyPanel()
		{
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);
		table = new MiTable();
		appendPart(new MiScrolledBox(table));
		}
	public		int		getInspectedObjectIndex()
		{
		return(-1);
		}
	public		MiTable		getTable()
		{
		return(table);
		}
	public		void		setTableHeaders(Strings names)
		{
		tableHeaders = new Strings(names);
		}

	protected	void		generatePanel()
		{
		table.removeAllCellsAndHeadersAndFooterCells();
		// ---------------------------------------------------------------
		// Row Headers (object property names)
		// ---------------------------------------------------------------
		if (tableHeaders.size() > 0)
			{
			table.setTotalNumberOfColumns(tableHeaders.size());
			table.setHasFixedTotalNumberOfColumns(true);
			table.setMinimumNumberOfVisibleColumns(tableHeaders.size());
			table.setMaximumNumberOfVisibleColumns(tableHeaders.size());
			// Set up column headers
			for (int j = 0; j < tableHeaders.size(); ++j)
				{
				table.addCell(MiTable.ROW_HEADER_NUMBER, j, tableHeaders.elementAt(j));
				}
			}
		// ---------------------------------------------------------------
		// Contents
		// ---------------------------------------------------------------
		MiParts widgets = new MiParts();
		for (int i = 0; i < getPropertyWidgets().size(); ++i)
			{
			widgets.addElement(getPropertyWidgets().elementAt(i).getWidget());
			}
		table.appendItems(widgets);
		// ---------------------------------------------------------------
		// Column Headers (object names)
		// ---------------------------------------------------------------
		Strings objects = getPossibleInspectedObjects();
		for (int i = 0; i < objects.size(); ++i)
			{
			table.addCell(i, MiTable.COLUMN_HEADER_NUMBER, objects.elementAt(i));
			}
		}
	}

