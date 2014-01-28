
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
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.DoubleVector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiDockingPanel extends MiVisibleContainer
	{
	private		int		orientation;




	public				MiDockingPanel()
		{
		this(Mi_HORIZONTAL);
		}
	public				MiDockingPanel(int orientation)
		{
		this.orientation = orientation;
		setVisibleContainerAutomaticLayoutEnabled(false);

		if (orientation == Mi_HORIZONTAL)
			{
			MiColumnLayout layout = new MiColumnLayout();
			layout.setElementHSizing(Mi_EXPAND_TO_FILL);
			layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
			setLayout(layout);
			}
		else
			{
			MiRowLayout layout = new MiRowLayout();
			layout.setElementVSizing(Mi_EXPAND_TO_FILL);
			layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			layout.setElementVJustification(Mi_TOP_JUSTIFIED);
			setLayout(layout);
			}
		setInsetMargins(0);
		MiSystem.applyClassPropertyValues(this, "MiDockingPanel");
		}

	public		int		getOrientation()
		{
		return(orientation);
		}

	public		void		appendPart(MiPart newToolBar, MiPoint pt)
		{
		// If this is the first toolbar, then just add it
		if (getNumberOfParts() == 0)
			{
			appendPart(newToolBar);
			}
		else
			{
			if (newToolBar instanceof MiiOrientablePart)
				{
				((MiiOrientablePart )newToolBar).setOrientation(orientation);
				}
			// See if we can pack it in with the others close to the given location.
			if (orientation == Mi_HORIZONTAL)
				{
				// Determine which 'rows' the pt is closest to.
				IntVector rows = getClosestRows(pt);
				MiDistance newToolBarPrefWidth = newToolBar.getPreferredSize(new MiSize()).getWidth();
				MiSize rowPrefSize = new MiSize();
				MiBounds rowBounds = new MiBounds();
				for (int i = 0; i < rows.size(); ++i)
					{
					MiPart row = getPart(rows.elementAt(i));
					row.getPreferredSize(rowPrefSize);
					row.getBounds(rowBounds);
					MiDistance spareRoom = rowBounds.getWidth() - rowPrefSize.getWidth();
					if (spareRoom > newToolBarPrefWidth * 0.5)
						{
						// Found a 'row' to add the toolbar to, now find which
						// position within the row to add the new toolbar.
						for (int j = 0; j < row.getNumberOfParts(); ++j)
							{
							if (pt.x <= row.getPart(j).getCenterX())
								{
								// Found position!
								row.insertPart(newToolBar, j);
								return;
								}
							}
						row.appendPart(newToolBar);
						return;
						}
					}
				// No rows have the room... make new row:
				appendPart(newToolBar);
				}
			else
				{
				// Determine which 'columns' the pt is closest to.
				IntVector columns = getClosestColumns(pt);
				MiDistance newToolBarPrefHeight = newToolBar.getPreferredSize(new MiSize()).getHeight();
				MiSize columnPrefSize = new MiSize();
				MiBounds columnBounds = new MiBounds();
				for (int i = 0; i < columns.size(); ++i)
					{
					MiPart column = getPart(columns.elementAt(i));
					column.getPreferredSize(columnPrefSize);
					column.getBounds(columnBounds);
					MiDistance spareRoom = columnBounds.getWidth() - columnPrefSize.getWidth();
					if (spareRoom > newToolBarPrefHeight * .8)
						{
						// Found a 'column' to add the toolbar to, now find which
						// position within the column to add the new toolbar.
						for (int j = 0; j < column.getNumberOfParts(); ++j)
							{
							if (pt.y <= column.getPart(j).getCenterY())
								{
								// Found position!
								column.insertPart(newToolBar, j);
								return;
								}
							}
						column.appendPart(newToolBar);
						return;
						}
					}
				// No columns have the room... make new column:
				appendPart(newToolBar);
				}
			}
		}
	public		MiiLayout	getSection(int rowOrColumnNumber)
		{
		if (getNumberOfParts() > rowOrColumnNumber)
			return((MiiLayout )getPart(rowOrColumnNumber));
		return(null);
		}
	public		void		appendToSection(MiPart part, int rowOrColumnNumber)
		{
		if (part instanceof MiiOrientablePart)
			{
			((MiiOrientablePart )part).setOrientation(orientation);
			}
		getPart(rowOrColumnNumber).appendPart(part);
		}

	public		void		appendPart(MiPart part)
		{
		if (part instanceof MiiOrientablePart)
			{
			((MiiOrientablePart )part).setOrientation(orientation);
			}

		if (orientation == Mi_HORIZONTAL)
			{
			MiRowLayout rowLayout = new MiRowLayout();
			rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			super.appendPart(rowLayout);
			rowLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
			rowLayout.appendPart(part);
			}
		else
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			super.appendPart(columnLayout);
			columnLayout.setElementVJustification(Mi_TOP_JUSTIFIED);
			columnLayout.appendPart(part);
			}
		}
	protected	IntVector	getClosestRows(MiPoint pt)
		{
		IntVector closestRows = new IntVector();
		DoubleVector distVector = new DoubleVector();
		MiBounds tmpBounds = new MiBounds();
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			getPart(i).getBounds(tmpBounds);
			distVector.addElement(tmpBounds.getDistanceSquaredToClosestEdge(pt));
			}
		DoubleVector sortedVector = distVector.copy();
		sortedVector.sort();
		for (int i = 0; i < sortedVector.size(); ++i)
			{
			closestRows.addElement(distVector.indexOf(sortedVector.elementAt(i)));
			}
		return(closestRows);
		}
	protected	IntVector	getClosestColumns(MiPoint pt)
		{
		IntVector closestColumns = new IntVector();
		DoubleVector distVector = new DoubleVector();
		MiBounds tmpBounds = new MiBounds();
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			getPart(i).getBounds(tmpBounds);
			distVector.addElement(tmpBounds.getDistanceSquaredToClosestEdge(pt));
			}
		DoubleVector sortedVector = distVector.copy();
		sortedVector.sort();
		for (int i = 0; i < sortedVector.size(); ++i)
			{
			closestColumns.addElement(distVector.indexOf(sortedVector.elementAt(i)));
			}
		return(closestColumns);
		}
	}

