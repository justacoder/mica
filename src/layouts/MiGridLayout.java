
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


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiGridLayout extends MiManipulatableLayout
	{
	private	static	MiMargins	tmpMargins 		= new MiMargins();

	private		int		gridHSizing		= Mi_NONE;
	private		int		gridVSizing		= Mi_SAME_SIZE;
	private		MiDistance	gridHSpacing		= 0;
	private		MiDistance	gridVSpacing		= 0;
	private		int		gridHJustification	= Mi_JUSTIFIED;
	private		int		gridVJustification	= Mi_JUSTIFIED;

	protected	int		numRows;
	protected	int		numColumns		= 3;

	private		MiBounds	minSize			= new MiBounds();
	private		MiBounds	prefSize		= new MiBounds();

	private		MiBounds	gridCellBounds		= new MiBounds();
	private		MiBounds	objBounds		= new MiBounds();
	private		MiSize		objSize			= new MiSize();

	private		int		totalRows		= 0;
	private		int		totalColumns		= 0;
	private		boolean		calcingSizeOfTotalGridCellsFlag;
	private		MiBounds	defaultGridCellSize	= new MiBounds(0, 0,
									10 * getFont().getMaxCharWidth(),
									getFont().getMaxCharHeight());

	private		boolean		filledRowByRow		= true;

	private		MiCoord[]	rowTopSideLocations		= new MiCoord[0];
	private		MiCoord[]	rowBottomSideLocations		= new MiCoord[0];
	private		MiCoord[]	columnLeftSideLocations		= new MiCoord[0];
	private		MiCoord[]	columnRightSideLocations	= new MiCoord[0];

	private		MiDistance[] 	maxMinHeightOfARowElement	;
	private		MiDistance[] 	maxMinWidthOfAColumnElement	;
	private		MiDistance[]	maxPrefHeightOfARowElement	;
	private		MiDistance[] 	maxPrefWidthOfAColumnElement	;

	private		MiDistance 	hSpacing;
	private		MiDistance 	vSpacing;
	private		MiDistance 	gHSpacing;
	private		MiDistance 	gVSpacing;

	private		MiParts 	partsToPlace	= new MiParts();



	public				MiGridLayout()
		{
		this(false);
		}
	public				MiGridLayout(boolean manipulatable)
		{
		super(manipulatable, false);
		// FIX: resolve differences between cell Margins and alley Margins
		setCellMargins(0);
		setLayoutContainerPartRelations(true);
		setElementHSizing(Mi_NONE);
		setElementVSizing(Mi_NONE);
		}

	public		void		setGridHSizing(int s) 		
		{ 
		gridHSizing = s;
		invalidateTargetLayout();
		}
	public		int		getGridHSizing()
		{ 
		return(gridHSizing);
		}

	public		void		setGridVSizing(int s)
		{ 
		gridVSizing = s;
		invalidateTargetLayout();
		}
	public		int		getGridVSizing()
		{
		return(gridVSizing);
		}

	public		void		setGridSizing(int s)
		{ 
		gridVSizing = s; 
		gridHSizing =s;
		invalidateTargetLayout();
		}

	public		void		setGridHJustification(int s)
		{
		gridHJustification = s;
		invalidateTargetLayout();
		}
	public		int		getGridHJustification()
		{
		return(gridHJustification);
		}

	public		void		setGridVJustification(int s)
		{
		gridVJustification = s;
		invalidateTargetLayout();
		}
	public		int		getGridVJustification()
		{
		return(gridVJustification);
		}

	public		void		setGridJustification(int s)
		{
		gridVJustification = s;
		gridHJustification = s;
		invalidateTargetLayout();
		}

	public		void		setGridHSpacing(MiDistance d) 
		{
		gridHSpacing = d;
		invalidateTargetLayout();
		}
	public		MiDistance	getGridHSpacing()
		{
		return(gridHSpacing);
		}

	public		void		setGridVSpacing(MiDistance d) 
		{
		gridVSpacing = d;
		invalidateTargetLayout();
		}
	public		MiDistance	getGridVSpacing()
		{
		return(gridVSpacing);
		}

	public		void		setGridSpacing(MiDistance d)
		{
		setGridHSpacing(d);
		setGridVSpacing(d);
		}
	public		void		setGridSpacing(MiDeviceDistance d)
		{
		setGridHSpacing(d);
		setGridVSpacing(d);
		}

	public		void		setDefaultGridCellSize(MiBounds size) 		
		{ 
		defaultGridCellSize = size;
		}
	public		MiBounds	getDefaultGridCellSize()
		{
		return(defaultGridCellSize);
		}
	public		void		setNumberOfRows(int r) 		
		{ 
		numRows = r; 
		filledRowByRow = false;	
		invalidateTargetLayout();
		}
	public		int		getNumberOfRows()
		{
		return(numRows);
		}

	public		void		setNumberOfColumns(int c) 		
		{ 
		numColumns = c; 
		filledRowByRow = true;	
		invalidateTargetLayout();
		}
	public		int		getNumberOfColumns()
		{ 
		return(numColumns);
		}

	public		void		setTotalNumberOfRows(int r) 		
		{ 
		if (r > totalRows)
			{
			totalRows = r;
			//calcSizeOfTotalGridCells(totalRows, totalColumns);
			}
		totalRows = r; 
		}
	public		int		getTotalNumberOfRows()
		{
		return(totalRows);
		}

	public		void		setTotalNumberOfColumns(int c) 		
		{ 
		if (c > totalColumns)
			{
			totalColumns = c; 
			//calcSizeOfTotalGridCells(totalRows, totalColumns);
			}
		totalColumns = c; 
		}
	public		int		getTotalNumberOfColumns()
		{ 
		return(totalColumns);
		}

	protected	MiPart		makePlaceHolderShape()
		{
		return(new MiRectangle());
		}
	public		MiParts	insertPlaceHolder(int index)
		{
		MiPlaceHolder ph = (MiPlaceHolder )getPrototypePlaceHolder().copy();
		ph.setCenter(getTarget().getCenter());
		index = getTarget().getIndexOfPart(getNode(index));
		getTarget().insertPart(ph, index);
		return(new MiParts(ph));
		}
	public		MiParts	appendPlaceHolder(int index)
		{
		MiPlaceHolder ph = (MiPlaceHolder )getPrototypePlaceHolder().copy();
		ph.setCenter(getTarget().getCenter());
		getTarget().appendPart(ph);
		return(new MiParts(ph));
		}
	public		void		translate(MiDistance x, MiDistance y)
		{
		MiBounds b = getBounds();
		super.translate(x, y);
		MiBounds bounds = getBounds();
		MiDistance tx = bounds.xmin - b.xmin;
		MiDistance ty = bounds.ymin - b.ymin;
		for (int i = 0; i < rowTopSideLocations.length; ++i)
			{
			rowTopSideLocations[i] += ty;
			rowBottomSideLocations[i] += ty;
			}
		for (int i = 0; i < columnLeftSideLocations.length; ++i)
			{
			columnLeftSideLocations[i] += tx;
			columnRightSideLocations[i] += tx;
			}
		}
	protected	void		calcPreferredSize(MiSize size)
		{
		calcMinAndPreferredSize();
		size.setSize(prefSize);
		}

	protected	void		calcMinimumSize(MiSize size)
		{
		calcMinAndPreferredSize();
		size.setSize(minSize);
		}

	private		void		calcMinAndPreferredSize()
		{
		partsToPlace.removeAllElements();
		for (int i = 0; i < getTarget().getNumberOfParts(); ++i)
			{
			if (getTarget().getPart(i).isVisible())
				partsToPlace.addElement(getTarget().getPart(i));
			}

		int numObjects = partsToPlace.size();

		if (filledRowByRow)
			numRows = (numObjects + numColumns - 1)/numColumns;
		else
			numColumns = (numObjects + numRows - 1)/numRows;
			
		maxMinHeightOfARowElement 		= new MiDistance[numRows];
		maxMinWidthOfAColumnElement 		= new MiDistance[numColumns];
		maxPrefHeightOfARowElement 		= new MiDistance[numRows];
		maxPrefWidthOfAColumnElement 		= new MiDistance[numColumns];

		MiDistance maxMinWidthAllColumns 	= 0;
		MiDistance maxMinHeightAllRows 		= 0;
		MiDistance maxPrefWidthAllColumns 	= 0;
		MiDistance maxPrefHeightAllRows 	= 0;

		MiSize size = new MiSize();
		for (int row = 0; row < numRows; ++row)
		    {
		    for (int column = 0; column < numColumns; ++column)
			{
			int objIndex;
			if (filledRowByRow)
				objIndex = row * numColumns + column;
			else
				objIndex = column * numRows + row;

			if (objIndex >= numObjects)
				break;

			MiPart obj = partsToPlace.elementAt(objIndex);
			if (!obj.isVisible())
				continue;

			obj.getMinimumSize(size);
			if (maxMinWidthOfAColumnElement[column] < size.width)
				maxMinWidthOfAColumnElement[column] = size.width;
			if (maxMinHeightOfARowElement[row] < size.height)
				maxMinHeightOfARowElement[row] = size.height;
			if (maxMinWidthAllColumns < size.width)
				maxMinWidthAllColumns = size.width;
			if (maxMinHeightAllRows < size.height)
				maxMinHeightAllRows = size.height;

			obj.getPreferredSize(size);
			if (maxPrefWidthOfAColumnElement[column] < size.width)
				maxPrefWidthOfAColumnElement[column] = size.width;
			if (maxPrefHeightOfARowElement[row] < size.height)
				maxPrefHeightOfARowElement[row] = size.height;
			if (maxPrefWidthAllColumns < size.width)
				maxPrefWidthAllColumns = size.width;
			if (maxPrefHeightAllRows < size.height)
				maxPrefHeightAllRows = size.height;

			}
		    }

		if (gridHSizing == Mi_SAME_SIZE)
			{
			for (int i = 0; i < numColumns; ++i)
				{
				maxMinWidthOfAColumnElement[i] = maxMinWidthAllColumns;
				maxPrefWidthOfAColumnElement[i] = maxPrefWidthAllColumns;
				}
			}
		if (gridVSizing == Mi_SAME_SIZE)
			{
			for (int i = 0; i < numRows; ++i)
				{
				maxMinHeightOfARowElement[i] = maxMinHeightAllRows;
				maxPrefHeightOfARowElement[i] = maxPrefHeightAllRows;
				}
			}

		MiDistance horizontalMinSizeOfGrid = 0;
		MiDistance horizontalPrefSizeOfGrid = 0;
		for (int i = 0; i < numColumns; ++i)
			{
			horizontalMinSizeOfGrid += maxMinWidthOfAColumnElement[i];
			horizontalPrefSizeOfGrid += maxPrefWidthOfAColumnElement[i];
			}
		MiDistance verticalMinSizeOfGrid = 0;
		MiDistance verticalPrefSizeOfGrid = 0;
		for (int i = 0; i < numRows; ++i)
			{
			verticalMinSizeOfGrid += maxMinHeightOfARowElement[i];
			verticalPrefSizeOfGrid += maxPrefHeightOfARowElement[i];
			}

		// Adjust for margins.
		MiDistance alleyHSpacing = getAlleyHSpacing();
		MiDistance alleyVSpacing = getAlleyVSpacing();
		MiDistance gHSpacing = gridHSpacing;
		MiDistance gVSpacing = gridVSpacing;
		horizontalMinSizeOfGrid += alleyHSpacing * (numColumns * 2) + gHSpacing * (numColumns - 1);
		verticalMinSizeOfGrid += alleyVSpacing * (numRows * 2) + gVSpacing * (numRows - 1);
		horizontalPrefSizeOfGrid += alleyHSpacing * (numColumns * 2) +gHSpacing * (numColumns - 1);
		verticalPrefSizeOfGrid += alleyVSpacing * (numRows * 2) + gVSpacing * (numRows - 1);

		minSize.setWidth(horizontalMinSizeOfGrid);
		minSize.setHeight(verticalMinSizeOfGrid);
		prefSize.setWidth(horizontalPrefSizeOfGrid);
		prefSize.setHeight(verticalPrefSizeOfGrid);

		minSize.addMargins(getInsetMargins(tmpMargins));
		prefSize.addMargins(getInsetMargins(tmpMargins));
		}

	public		void		validateLayout()
		{
		super.validateLayout();
		//calcSizeOfTotalGridCells(totalRows, totalColumns);
		}


	public		void		doLayout()
		{
		double 		xReductionFactor = 1;
		double 		yReductionFactor = 1;
		MiDistance 	horizontalSizeOfGrid = 0;
		MiDistance 	verticalSizeOfGrid = 0;
		MiBounds	targetBounds;
		MiDistance[]	maxHeightOfARowElement;
		MiDistance[]	maxWidthOfAColumnElement;
		boolean		usingMinimumSizes = false;


		calcMinAndPreferredSize();

		MiSize		minimumSize	= new MiSize();
		MiSize		preferredSize	= new MiSize();

		getMinimumSize(minimumSize);
		getPreferredSize(preferredSize);

		hSpacing 			= getAlleyHSpacing();
		vSpacing 			= getAlleyVSpacing();
		gHSpacing 			= gridHSpacing;
		gVSpacing 			= gridVSpacing;

		rowTopSideLocations 		= new MiCoord[Math.max(totalRows, numRows)];
		rowBottomSideLocations 		= new MiCoord[Math.max(totalRows, numRows)];
		columnLeftSideLocations 	= new MiCoord[Math.max(totalColumns, numColumns)];
		columnRightSideLocations 	= new MiCoord[Math.max(totalColumns, numColumns)];

		int numObjects = partsToPlace.size();

		targetBounds = getTarget().getInnerBounds();
		if (targetBounds.isReversed())
			targetBounds.setBounds(0,0,1,1);

		if ((Utility.isLessThan(targetBounds.getWidth(), preferredSize.getWidth()))
			|| (Utility.isLessThan(targetBounds.getHeight(), preferredSize.getHeight())))
			{
//MiDebug.println("targetBounds = " + targetBounds);
//MiDebug.println("preferredSize = " + preferredSize);
			maxWidthOfAColumnElement = maxMinWidthOfAColumnElement;
			for (int i = 0; i < maxWidthOfAColumnElement.length; ++i)
				{
				if (maxWidthOfAColumnElement[i] == 0)
					maxWidthOfAColumnElement[i] = targetBounds.getWidth()/numColumns;
				}

			maxHeightOfARowElement = maxMinHeightOfARowElement;
			for (int i = 0; i < maxHeightOfARowElement.length; ++i)
				{
				if (maxHeightOfARowElement[i] == 0)
					maxHeightOfARowElement[i] = targetBounds.getHeight()/numRows;
				}

			//horizontalSizeOfGrid = minimumSize.getWidth();
			//if (horizontalSizeOfGrid == 0)
				horizontalSizeOfGrid = targetBounds.getWidth();

			//verticalSizeOfGrid = minimumSize.getHeight();
			//if (verticalSizeOfGrid == 0)
				verticalSizeOfGrid = targetBounds.getHeight();

			xReductionFactor = targetBounds.getWidth()/preferredSize.getWidth();
			if (xReductionFactor > 1.0)
				xReductionFactor = 1.0;
			yReductionFactor = targetBounds.getHeight()/preferredSize.getHeight();
			if (yReductionFactor > 1.0)
				yReductionFactor = 1.0;

			usingMinimumSizes = true;
			}
		else 
			{
			maxWidthOfAColumnElement = maxPrefWidthOfAColumnElement;
			maxHeightOfARowElement = maxPrefHeightOfARowElement;
			horizontalSizeOfGrid = preferredSize.getWidth();
			verticalSizeOfGrid = preferredSize.getHeight();
			}
		// All parts are invisible
		if ((maxHeightOfARowElement.length == 0) || (maxWidthOfAColumnElement.length == 0))
			{
			return;
			}

		targetBounds.subtractMargins(getInsetMargins(tmpMargins));
		if ((gridHJustification == Mi_TOP_JUSTIFIED)
			|| (gridHJustification == Mi_BOTTOM_JUSTIFIED))
			{
			}
		else if (gridHJustification == Mi_JUSTIFIED)
			{
			if (numObjects > 0)
				{
				hSpacing += (targetBounds.getWidth() - horizontalSizeOfGrid)/(numObjects * 2);
				if (hSpacing < getAlleyHSpacing())
					hSpacing = getAlleyHSpacing();
				}
			}
		if ((gridVJustification == Mi_TOP_JUSTIFIED)
			|| (gridVJustification == Mi_BOTTOM_JUSTIFIED))
			{
			}
		else if (getElementVJustification() == Mi_JUSTIFIED)
			{
			if (numObjects > 0)
				{
				vSpacing+=(targetBounds.getHeight() - verticalSizeOfGrid)/(numObjects * 2);
				if (vSpacing < getAlleyVSpacing())
					vSpacing = getAlleyVSpacing();
				}
			}

		// gridCellBounds is the inner cell bounds..
		gridCellBounds.setBounds(
			targetBounds.xmin + hSpacing, 
			targetBounds.ymax - maxHeightOfARowElement[0] - vSpacing, 
			targetBounds.xmin + maxWidthOfAColumnElement[0] + hSpacing, 
			targetBounds.ymax - vSpacing);


//System.out.println("target = " + getTarget());
//System.out.println("target.getBounds() = " + getTarget().getBounds());
//System.out.println("target bounds = " + targetBounds);
//System.out.println("hSpacing = " + hSpacing);
//System.out.println("preferredSize = " + preferredSize);
//System.out.println("GRID bounds = " + getBounds());
//System.out.println("gridCellBounds = " + gridCellBounds + ", maxWidthOfAColumnElement[0] = " + maxWidthOfAColumnElement[0]);


//FIX: // FIX: prob with any outer bounds is no-one can getBounds of this and see it
		// or rather reCalcBounds ...
//		bounds = targetBounds;

		for (int row = 0; row < numRows; ++row)
			{
			rowBottomSideLocations[row] = gridCellBounds.ymin - vSpacing;
			rowTopSideLocations[row] = gridCellBounds.ymax + vSpacing;

			for (int column = 0; column < numColumns; ++column)
				{
				int objIndex;
				if (filledRowByRow)
					objIndex = row * numColumns + column;
				else
					objIndex = column * numRows + row;

				if (objIndex >= numObjects)
					break;

				MiPart obj = partsToPlace.elementAt(objIndex);
				if (!obj.isVisible())
					continue;

				if (usingMinimumSizes)
					obj.getMinimumSize(objSize);
				else
					obj.getPreferredSize(objSize);

				if ((usingMinimumSizes) && ((objSize.getWidth() == 0) || (objSize.getHeight() == 0)))
					{
					obj.getPreferredSize(objSize);
					objSize.setWidth(objSize.getWidth() * xReductionFactor);
					objSize.setHeight(objSize.getHeight() * yReductionFactor);
					}

				objBounds.setSize(objSize);
//System.out.println("usingMinimumSizes = " + usingMinimumSizes);
//System.out.println("objSize = " + objSize);

				columnLeftSideLocations[column] = gridCellBounds.xmin - hSpacing;
				columnRightSideLocations[column] = gridCellBounds.xmax + hSpacing;

				if ((getElementHSizing() == Mi_SAME_SIZE) 
					|| (getElementHSizing() == Mi_EXPAND_TO_FILL))
					{
					objBounds.setWidth(gridCellBounds.getWidth());
					}
				if ((getElementVSizing() == Mi_SAME_SIZE) 
					|| (getElementVSizing() == Mi_EXPAND_TO_FILL))
					{
					objBounds.setHeight(gridCellBounds.getHeight());
					}

				switch (getElementHJustification())
					{
					case Mi_CENTER_JUSTIFIED :
						objBounds.setCenterX(gridCellBounds.getCenterX());
						break;
					case Mi_LEFT_JUSTIFIED :
						objBounds.setCenterX(gridCellBounds.xmin + objBounds.getWidth()/2);
						break;
					case Mi_RIGHT_JUSTIFIED :
						objBounds.setCenterX(gridCellBounds.xmax - objBounds.getWidth()/2);
						break;
					}
				switch (getElementVJustification())
					{
					case Mi_CENTER_JUSTIFIED :
						objBounds.setCenterY(gridCellBounds.getCenterY());
						break;
					case Mi_BOTTOM_JUSTIFIED :
						objBounds.setCenterY(gridCellBounds.ymin + objBounds.getHeight()/2);
						break;
					case Mi_TOP_JUSTIFIED :
						objBounds.setCenterY(gridCellBounds.ymax - objBounds.getHeight()/2);
						break;
					}

//System.out.println("GRID - --- RESIZING OBJ: " + obj + " to: " + objBounds);
//System.out.println("GRID - --- GRID CELL BNS = " + gridCellBounds);

				obj.setBounds(objBounds);

//System.out.println("GRID - --- RESIZED OBJ: to: " + obj.getBounds());
				
				if (column < numColumns - 1)
					{
					gridCellBounds.xmin = gridCellBounds.xmax + 2 * hSpacing + gHSpacing;
					gridCellBounds.xmax = gridCellBounds.xmin + maxWidthOfAColumnElement[column + 1];
					}
				}
			if (row < numRows - 1)
				{
				gridCellBounds.xmin = targetBounds.xmin + hSpacing;
				gridCellBounds.xmax = gridCellBounds.xmin + maxWidthOfAColumnElement[0];
				gridCellBounds.ymax = gridCellBounds.ymin - vSpacing * 2 - gVSpacing;
				gridCellBounds.ymin = gridCellBounds.ymax - maxHeightOfARowElement[row + 1];
				}
			}

		}
	public 		void		formatTarget(String connectionType)
		{
		}
	}

