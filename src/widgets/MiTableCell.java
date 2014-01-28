
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
import java.awt.Color;

// Depreciate strValue, and attributes as too much functionality is not
// possible otherwise or add a getGraphicsAttributes and use that instead!

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTableCell implements MiiTypes
	{
	protected	int		rowNumber;
	protected	int		columnNumber;
	protected	int		numberOfRows		= 1;
	protected	int		numberOfColumns		= 1;

					// Of contents...
	protected	int		hJustification		= Mi_NONE;
	protected	int		vJustification		= Mi_NONE;
	protected	int		hSizing			= Mi_NONE;
	protected	int		vSizing			= Mi_NONE;

					// Of cell..
	protected	MiMargins	insetMargins;
	protected	MiMargins	margins;
	protected	MiBounds	bounds;
	protected	MiBounds	innerBounds;

	// Only used for row and column defaults
	protected	MiDistance	fixedWidth;
	protected	MiDistance	fixedHeight;
	protected	int		columnHSizing		= Mi_NONE;
	protected	int		rowVSizing		= Mi_NONE;

	protected	String		strValue;
	protected	MiAttributes	attributes;
	private		MiPart		graphics;
	private		MiTable		table;
	protected	Object		tag;
	private		boolean		visible			= true;
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiSize		tmpSize			= new MiSize();



	public				MiTableCell(MiTable table, int row, int column)
		{
		this(table, row, column, null);
		}
	public				MiTableCell(MiTable table, int row, int column, MiPart graphics)
		{
		this.table = table;
		rowNumber = row;
		columnNumber = column;
		this.graphics = graphics;
/*
		if (graphics != null)
			{
			table.getAppropriateContainer(rowNumber, columnNumber).appendPart(graphics);
			}
*/
		attributes = MiPart.getDefaultAttributes();
		}

	public		void		setTag(Object tag)
		{
		this.tag = tag;
		}
	public		Object		getTag()
		{
		return(tag);
		}

	public		boolean		isEmpty()
		{
		return((graphics == null) && (strValue == null));
		}
					// Visibility of whole cell
	public		boolean		isVisible()
		{
		return(visible);
		}
	public		void		setVisible(boolean flag)
		{
		visible = flag;

		// Graphics may be a table (i.e. subwindow which is registered with Draw Manager) 
		// which will get drawn regardless of whether this cell is visible or not...
		if (graphics != null)
			{
			graphics.setVisible(flag);
			}
		table.invalidateLayout();
		}

	public		void		setRowNumber(int rowNumber)
		{
		this.rowNumber = rowNumber;
		}
	public		int		getRowNumber()
		{
		return(rowNumber);
		}
	public		void		setColumnNumber(int columnNumber)
		{
		this.columnNumber = columnNumber;
		}
	public		int		getColumnNumber()
		{
		return(columnNumber);
		}

	public		void		setNumberOfRows(int numberOfRows)
		{
		this.numberOfRows = numberOfRows;
		}
	public		int		getNumberOfRows()
		{
		return(numberOfRows);
		}
	public		void		setNumberOfColumns(int numberOfColumns)
		{
		this.numberOfColumns = numberOfColumns;
		}
	public		int		getNumberOfColumns()
		{
		return(numberOfColumns);
		}

	public		void		setHorizontalJustification(int justification)
		{
		hJustification = justification;
		table.invalidateLayout();
		}
	public		int		getHorizontalJustification()
		{
		return(hJustification);
		}
	public		void		setVerticalJustification(int justification)
		{
		vJustification = justification;
		table.invalidateLayout();
		}
	public		int		getVerticalJustification()
		{
		return(vJustification);
		}

	public		void		setHorizontalSizing(int sizing)
		{
		hSizing = sizing;
		table.invalidateLayout();
		}
	public		int		getHorizontalSizing()
		{
		return(hSizing);
		}
	public		void		setVerticalSizing(int sizing)
		{
		vSizing = sizing;
		table.invalidateLayout();
		}
	public		int		getVerticalSizing()
		{
		return(vSizing);
		}

	public		void		setInsetMargins(MiMargins margins)
		{
		insetMargins = margins;
		table.invalidateLayout();
		}
	public		MiMargins	getInsetMargins()
		{
		return(insetMargins);
		}

	public		void		setMargins(MiMargins margins)
		{
		this.margins = margins;
		table.invalidateLayout();
		}
	public		MiMargins	getMargins()
		{
		return(margins);
		}

					/**------------------------------------------------------
					 * Sets the width of the column this cell represents. 
					 * Used by table row and column defaults only. If the desire is
					 * to not let the user resize this width, then set this cell's 
					 * attributes by setFixedWidth(true);
					 * @param width		the width
					 * @see			#getFixedWidth
					 * @see			#setFixedHeight
					 *------------------------------------------------------*/
	public		void		setFixedWidth(MiDistance width)
		{
		fixedWidth = width;
		table.invalidateLayout();
		}
					/**------------------------------------------------------
					 * Gets the width of the column this cell represents. 
					 * Used by table row and column defaults only. If the desire is
					 * to not let the user resize this width, then set this cell's 
					 * attributes by setFixedWidth(true);
					 * @return		the fixed width or 0
					 * @see			#getFixedWidth
					 * @see			#setFixedHeight
					 *------------------------------------------------------*/
	public		MiDistance	getFixedWidth()
		{
		return(fixedWidth);
		}
					/**------------------------------------------------------
					 * Sets the height of the column this cell represents. 
					 * Used by table row and column defaults only. If the desire is
					 * to not let the user resize this height, then set this cell's 
					 * attributes by setFixedHeight(true);
					 * @param width		the height
					 * @see			#setFixedWidth
					 * @see			#getFixedHeight
					 *------------------------------------------------------*/
	public		void		setFixedHeight(MiDistance height)
		{
		fixedHeight = height;
		table.invalidateLayout();
		}
					/**------------------------------------------------------
					 * Gets the height of the column this cell represents. 
					 * Used by table row and column defaults only. If the desire is
					 * to not let the user resize this height, then set this cell's 
					 * attributes by setFixedHeight(true);
					 * @return		the fixed height or 0 (the default)
					 * @see			#setFixedWidth
					 * @see			#setFixedHeight
					 *------------------------------------------------------*/
	public		MiDistance	getFixedHeight()
		{
		return(fixedHeight);
		}

	public		void		setRowVerticalSizing(int sizing)
		{
		rowVSizing = sizing;
		}
	public		int		getRowVerticalSizing()
		{
		return(rowVSizing);
		}

	public		void		setColumnHorizontalSizing(int sizing)
		{
		columnHSizing = sizing;
		}
	public		int		getColumnHorizontalSizing()
		{
		return(columnHSizing);
		}

	public		MiBounds	getNodeBounds(MiBounds b)
		{
		b.copy(bounds);
		return(b);
		}

	public		MiiContextMenu	getContextMenu(MiBounds area)
		{
		if ((graphics != null) && (graphics.getContextMenu(area) != null))
			{
			return(graphics.getContextMenu(area));
			}
		return(attributes.getContextMenu());
		}
	public		void		setStatusHelp(MiiHelpInfo helpInfo)
		{
		this.attributes = attributes.setStatusHelp(helpInfo);
		}
	public		MiiHelpInfo	getStatusHelp(MiPoint point)
		{
		return(attributes.getStatusHelp());
		}
	public		void		setToolHintHelp(MiiHelpInfo helpInfo)
		{
		this.attributes = attributes.setToolHintHelp(helpInfo);
		}
	public		MiiHelpInfo	getToolHintHelp(MiPoint point)
		{
		return(attributes.getToolHintHelp());
		}
					// When assigned a string value this is used
					// (otherwise uses table's attributes)
					// Also used for statusHelp
	public		void		setAttributes(MiAttributes attributes)
		{
		this.attributes = attributes;
		table.invalidateLayout();
		}
	public		MiAttributes	getAttributes()
		{
		return(attributes);
		}
	public		void		setColor(Color color)
		{
		if (graphics != null)
			graphics.setColor(color);
		else
			attributes = attributes.setColor(color);
		}
	public		Color		getColor()
		{
		if (graphics != null)
			return(graphics.getColor());
		return(attributes.getColor());
		}
	public		void		setGraphics(MiPart part)
		{
		if (graphics == part)
			return;

		if (graphics != null)
			{
			if (part != null)
				{
				graphics.replaceSelf(part);
				}
			else
				{
				graphics.removeSelf();
				}
			graphics = part;
			}
		else
			{
			graphics = part;
			if (graphics != null)
				{
				table.getAppropriateContainer(rowNumber, columnNumber).appendPart(graphics);
				}
			}

/***
		MiContainer container = table.getAppropriateContainer(rowNumber, columnNumber);
		if (graphics != null)
			container.removePart(graphics);
		graphics = part;
		if (graphics != null)
			container.appendPart(graphics);
***/
		}
	public		MiPart		getGraphics()
		{
		return(graphics);
		}

	public		boolean		isSelectable()
		{
		if (graphics != null)
			{
			return(graphics.isSelectable() && graphics.isSensitive());
			}
		if (strValue != null)
			{
			return(true);
			}
		return(false); // (true); 8-27-2001 Do not want cells from 'createCellsToFillInAnyGapsCreatedByNewCell' to be selectable
		}

					/**
					 * This method sets the value of Text, Widgets and pure Strings.
					 * Warning, right and center justfication does not work for pure 
					 * String cells, only graphics cells.
					 **/
	public		void		setValue(String value)
		{
		if ((strValue == value) || ((strValue != null) && (strValue.equals(value))))
			return;

		if (graphics != null)
			{
			if (graphics instanceof MiWidget)
				((MiWidget )graphics).setValue(value);
			if (graphics instanceof MiText)
				((MiText )graphics).setText(value);
			}
		strValue = value;
		table.invalidateLayout();
		}
	public		String		getValue()
		{
		if (graphics != null)
			{
			if (graphics instanceof MiWidget)
				return(((MiWidget )graphics).getValue());
			if (graphics instanceof MiText)
				return(((MiText )graphics).getText());
			return(graphics.toString());
			}
		return(strValue);
		}


	public		MiDistance	getPreferredWidth()
		{
		return(getPreferredSize(tmpSize).getWidth());
		}
	public		MiDistance	getPreferredHeight()
		{
		return(getPreferredSize(tmpSize).getHeight());
		}

					// Of graphics...
	public		MiSize		getPreferredSize(MiSize size)
		{
		if (graphics != null)
			{
			graphics.getPreferredSize(size);
			}
		else if (attributes != null)
			{
			attributes.getFont().getSize(strValue, size);
			}
		else 
			{
			table.getAttributes().getFont().getSize(strValue, size);
			}
		if (insetMargins != null)
			size.addMargins(insetMargins);
		if (margins != null)
			size.addMargins(margins);

		return(size);
		}
	public		void		draw(MiRenderer renderer)
		{
		if (!isVisible())
			return;

		if (graphics != null)
			{
			graphics.draw(renderer);
			}
		else if (strValue != null)
			{
			renderer.setAttributes(attributes);
			renderer.drawText(strValue, innerBounds, attributes.getFont());
			}
		}

	protected	void		layoutParts()
		{
		MiTableCell defaults;

		int h_sizing = hSizing;
		if (h_sizing == Mi_NONE)
			{
			defaults = table.getRowDefaults(rowNumber);
			if (defaults != null)
				h_sizing = defaults.hSizing;
			}
		if (h_sizing == Mi_NONE)
			{
			defaults = table.getColumnDefaults(columnNumber);
			if (defaults != null)
				h_sizing = defaults.hSizing;
			}
		if (h_sizing == Mi_NONE)
			{
			defaults = table.getTableWideDefaults();
			if (defaults != null)
				h_sizing = defaults.hSizing;
			}

		int v_sizing = vSizing;
		if (v_sizing == Mi_NONE)
			{
			defaults = table.getRowDefaults(rowNumber);
			if (defaults != null)
				v_sizing = defaults.vSizing;
			}
		if (v_sizing == Mi_NONE)
			{
			defaults = table.getColumnDefaults(columnNumber);
			if (defaults != null)
				v_sizing = defaults.vSizing;
			}
		if (v_sizing == Mi_NONE)
			{
			defaults = table.getTableWideDefaults();
			if (defaults != null)
				v_sizing = defaults.vSizing;
			}


		int h_justification = hJustification;
		if (h_justification == Mi_NONE)
			{
			defaults = table.getRowDefaults(rowNumber);
			if (defaults != null)
				h_justification = defaults.hJustification;
			}
		if (h_justification == Mi_NONE)
			{
			defaults = table.getColumnDefaults(columnNumber);
			if (defaults != null)
				h_justification = defaults.hJustification;
			}
		if (h_justification == Mi_NONE)
			{
			defaults = table.getTableWideDefaults();
			if (defaults != null)
				h_justification = defaults.hJustification;
			}

		int v_justification = vJustification;
		if (v_justification == Mi_NONE)
			{
			defaults = table.getRowDefaults(rowNumber);
			if (defaults != null)
				v_justification = defaults.vJustification;
			}
		if (v_justification == Mi_NONE)
			{
			defaults = table.getColumnDefaults(columnNumber);
			if (defaults != null)
				v_justification = defaults.vJustification;
			}
		if (v_justification == Mi_NONE)
			{
			defaults = table.getTableWideDefaults();
			if (defaults != null)
				v_justification = defaults.vJustification;
			}

		layoutParts(h_sizing, v_sizing, h_justification, v_justification);
		}

	protected	void		layoutParts(int h_sizing, int v_sizing, int h_justification, int v_justification)
		{
		if (graphics != null)
			graphics.getPreferredSize(tmpSize);
		else if (strValue != null)
			attributes.getFont().getSize(strValue, tmpSize);
		else
			tmpSize.zeroOut();

		MiBounds cellBounds = MiBounds.newBounds();
		cellBounds.setSize(tmpSize);
		MiBounds theInnerBounds = tmpBounds;
		theInnerBounds.copy(innerBounds);
		if (margins != null)
			theInnerBounds.subtractMargins(margins);
		cellBounds.setCenterX(theInnerBounds.getCenterX());
		cellBounds.setCenterY(theInnerBounds.getCenterY());
		switch (h_sizing)
			{
			case Mi_EXPAND_TO_FILL :
				cellBounds.setWidth(theInnerBounds.getWidth());
				break;
			case Mi_SAME_SIZE :
				cellBounds.setWidth(theInnerBounds.getWidth());
				break;
			case Mi_NONE :
			default :
				break;
			}

		switch (v_sizing)
			{
			case Mi_EXPAND_TO_FILL :
				cellBounds.setHeight(theInnerBounds.getHeight());
				break;
			case Mi_SAME_SIZE :
				cellBounds.setHeight(theInnerBounds.getHeight());
				break;
			case Mi_NONE :
			default :
				break;
			}
		switch (h_justification)
			{
			case Mi_JUSTIFIED :
			case Mi_CENTER_JUSTIFIED :
			case Mi_NONE :
				cellBounds.setCenterX(theInnerBounds.getCenterX());
				break;
			case Mi_LEFT_JUSTIFIED :
				cellBounds.translateXminTo(theInnerBounds.getXmin());
				break;
			case Mi_RIGHT_JUSTIFIED :
				cellBounds.translateXmaxTo(theInnerBounds.getXmax());
				break;
			}

		switch (v_justification)
			{
			case Mi_JUSTIFIED :
			case Mi_CENTER_JUSTIFIED :
			case Mi_NONE :
				cellBounds.setCenterY(theInnerBounds.getCenterY());
				break;
			case Mi_BOTTOM_JUSTIFIED :
				cellBounds.translateYminTo(theInnerBounds.getYmin());
				break;
			case Mi_TOP_JUSTIFIED :
				cellBounds.translateYmaxTo(theInnerBounds.getYmax());
				break;
			}

		if (graphics != null)
			{
			graphics.setBounds(cellBounds);
			if (!graphics.hasValidLayout())
				{
				graphics.validateLayout();
				}
			}
		else
			{
			innerBounds.copy(cellBounds);
			}
		MiBounds.freeBounds(cellBounds);
		}
	public		MiTableCell	copy()
		{
		MiTableCell c = new MiTableCell(table, rowNumber, columnNumber);
		c.rowNumber 		= rowNumber;
		c.columnNumber		= columnNumber;
		c.numberOfRows		= numberOfRows;
		c.numberOfColumns	= numberOfColumns;

					// Of contents...
		c.hJustification	= hJustification;
		c.vJustification	= vJustification;
		c.hSizing		= hSizing;
		c.vSizing		= vSizing;

					// Of cell..
		c.insetMargins		= insetMargins != null ? new MiMargins(insetMargins) : null;
		c.margins		= margins != null ? new MiMargins(margins) : null;
		c.bounds		= bounds != null ? new MiBounds(bounds) : null;
		c.innerBounds		= innerBounds != null ? new MiBounds(innerBounds) : null;

		// Only used for row and column defaults
		c.fixedWidth		= fixedWidth;
		c.fixedHeight		= fixedHeight;
		c.columnHSizing		= columnHSizing;
		c.rowVSizing		= rowVSizing;

		c.strValue		= strValue;
		c.attributes		= attributes;
		c.graphics		= graphics != null ? graphics.deepCopy() : null;
		c.table			= table;
		c.tag			= tag;
		c.visible		= visible;

		return(c);
		}



	public		String		toString()
		{
		String str = new String(
			getClass().getName() + ".[" + rowNumber + "," + columnNumber + "]." + hashCode() + ".");
		if (graphics != null)
			{
			str += graphics.toString();
			}
		else
			{
			str += strValue;
			}
		if (!isVisible())
			{
			str += "[NOT VISIBLE]";
			}
		return(str);
		}
	}

