
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class Mi2DMeshGraphLayout extends MiManipulatableLayout
	{
	private		int		orientation 		= Mi_HORIZONTAL;
	private		int[]	 	rowColumnPositions;
	private		MiPart[]	rowColumnPositionsParts;
	private		int		numberOfRows;
	private		int		numberOfColumns;


					/**------------------------------------------------------
					 * Constructs a new instance of Mi2DMeshGraphLayout.
					 *------------------------------------------------------*/
	public				Mi2DMeshGraphLayout()
		{
		setKeepConnectionsBelowNodes(true);
		}
					/**------------------------------------------------------
					 * Gets whether the orientation can be changed.
					 * @return		true if the orientation cannot be
					 *			changed
					 * @implements		MiiOrientablePart#isOrientationFixed
					 *------------------------------------------------------*/
	public		boolean		isOrientationFixed()
		{
		return(false);
		}
					/**------------------------------------------------------
					 * Sets the orientation. Typically the valid values are:
					 *  Mi_HORIZONTAL
					 *  Mi_VERTICAL
					 * However, the implementation is free to define it's own,
					 * custom orientations.
					 * @param orientation	the orientation
					 * @implements		MiiOrientablePart#setOrientation
					 *------------------------------------------------------*/
	public		void		setOrientation(int orientation)
		{
		this.orientation = orientation;
		}
					/**------------------------------------------------------
					 * Gets the orientation. Typically the valid values are:
					 *  Mi_HORIZONTAL
					 *  Mi_VERTICAL
					 * However, the implementation is free to define it's own,
					 * custom orientations.
					 * @return 		the orientation
					 * @implements		MiiOrientablePart#getOrientation
					 *------------------------------------------------------*/
	public		int		getOrientation()
		{
		return(orientation);
		}
					/**------------------------------------------------------
					 * Changes to the next supported orientation.
					 * @implements		MiiOrientablePart#cycleOrientation
					 *------------------------------------------------------*/
	public		void		cycleOrientation()
		{
		if (orientation == Mi_HORIZONTAL)
			orientation = Mi_VERTICAL;
		else
			orientation = Mi_HORIZONTAL;

		MiParts nodes = new MiParts();
		getNodesInTarget(getTarget(), nodes);
		int numNodes = nodes.size();
		if (orientation == Mi_VERTICAL)
			{
			numberOfColumns = (int )(numNodes/Math.sqrt(numNodes));
			numberOfRows = (numNodes + numberOfColumns - 1)/numberOfColumns;
			}
		else
			{
			numberOfRows = (int )(numNodes/Math.sqrt(numNodes));
			numberOfColumns = (numNodes + numberOfRows - 1)/numberOfRows;
			}
		
		getTarget().invalidateLayout();
		}


	public		MiParts		insertNeighboringNode(MiPart node, int index, int relativeLocation)
		{
		if (index == -1)
			index = getNumberOfNodes() - 1;
		switch (relativeLocation)
			{
			case Mi_ABOVE:
				{
				int rowNum = (rowColumnPositions[index] & 0x7fff0000) >> 16;
				return(addNewRowAt(rowNum));
				}
			case Mi_TO_LEFT:
				{
				int colNum = (rowColumnPositions[index] & 0x7fff);
				return(addNewColumnAt(colNum));
				}
			case Mi_BELOW:
				{
				if (index != -1)
					{
					int rowNum = (rowColumnPositions[index] & 0x7fff0000) >> 16;
					return(addNewRowAt(rowNum + 1));
					}
				return(addNewRowAt(numberOfRows + 1));
				}
			case Mi_TO_RIGHT:
				{
				if (index != -1)
					{
					int colNum = (rowColumnPositions[index] & 0x7fff);
					return(addNewColumnAt(colNum + 1));
					}
				return(addNewColumnAt(numberOfRows + 1));
				}
			}
		return (null);
		}
					/**------------------------------------------------------
					 * Deletes the node at the given index. If the node is
					 * not a MiPlaceHolder then it is replaced by one. In any
					 * case the node is deleted. 
					 * In order to preserve the topology a whole row is deleted
					 * (as opposed to a whole column)
					 * @param index		the index of the node to delete
					 * @overrides		MiManipulatableLayout#deleteNode
					 *------------------------------------------------------*/
	public		void		deleteNode(int index)
		{
		if (index == -1)
			index = getNumberOfNodes() - 1;
		int rowNum = (rowColumnPositions[index] & 0x7fff0000) >> 16;
		deleteRow(rowNum);
		}

	public		MiParts		addNewRowAt(int rowNum)
		{
		// For every item currently in the row
		MiPart prevPh = null;
		MiParts placeHolders = new MiParts();
		MiPart target = getTarget();
		for (int colNum = 1; colNum <= numberOfColumns; ++colNum)
			{
			MiPart ph = makePlaceHolder();
			ph.setCenter(target.getCenter());
			int belowItemIndex = -1;
			if (rowNum == numberOfRows + 1)
		    		belowItemIndex = getNodeAtRowColumn(rowNum - 1, colNum);
			else
		    		belowItemIndex = getNodeAtRowColumn(rowNum, colNum);
			MiPart belowItem = target.getPart(belowItemIndex);
		    	if (rowNum < numberOfRows + 1)
				{
				if (rowNum > 1)
					{
					// ... find the nodes to the top (if any)
					int aboveItemIndex = getNodeAtRowColumn(rowNum - 1, colNum);
					if (aboveItemIndex != -1)
						{
						MiPart aboveItem = target.getPart(aboveItemIndex);
						if (prevPh != null)
							target.insertPart(ph, target.getIndexOfPart(prevPh) + 1);
						else
							target.insertPart(ph, belowItemIndex);

						// ... remove the connection between the two ...
						MiConnection conn;
						conn = getConnection(aboveItem, belowItem);
						conn.deleteSelf();
							
						// .. and make new connections with new placeHolder
						addConnection(aboveItem, ph);
						addConnection(ph, belowItem);
						}
					}
				else // Inserting above top row...
					{
					if (colNum == 1)
						target.insertPart(ph, 0);
					else
						target.insertPart(ph, target.getIndexOfPart(prevPh) + 1);
					addConnection(ph, belowItem);
					}
				}
			else // appending row
				{
				target.appendPart(ph);
				addConnection(belowItem, ph);
				}
			if (prevPh != null)
				{
				addConnection(prevPh, ph);
				}
			prevPh = ph;
			placeHolders.addElement(ph);
			}
		++numberOfRows;
		return(placeHolders);
		}

	public		MiParts		addNewColumnAt(int colNum)
		{
		// For every item currently in the col
		MiPart prevPh = null;
		MiParts placeHolders = new MiParts();
		MiPart target = getTarget();
		for (int rowNum = 1; rowNum <= numberOfRows; ++rowNum)
			{
			MiPart ph = makePlaceHolder();
			ph.setCenter(target.getCenter());
		   	if (colNum < numberOfColumns + 1)
				{
		    		int rightItemIndex = getNodeAtRowColumn(rowNum, colNum);
				MiPart rightItem = target.getPart(rightItemIndex);
				target.insertPart(ph, rightItemIndex);

				if (colNum > 1)
					{
					// ... find the nodes to the left (if any)
					int leftItemIndex = getNodeAtRowColumn(rowNum, colNum - 1);
					if (leftItemIndex != -1)
						{
						MiPart leftItem = target.getPart(leftItemIndex);

						// ... remove the connection between the two ...
						MiConnection conn;
						conn = getConnection(leftItem, rightItem);
						conn.deleteSelf();
							
						// .. and make new connections with new placeHolder
						addConnection(leftItem, ph);
						addConnection(ph, rightItem);
						}
					}
				else // Inserting before left column...
					{
					addConnection(ph, rightItem);
					}
				}
			else // appending col
				{
				int leftItemIndex = getNodeAtRowColumn(rowNum, colNum - 1);
				MiPart leftItem = target.getPart(leftItemIndex);
				if (rowNum != numberOfRows)
					{
					target.insertPart(ph, leftItemIndex + 1);
					}
				else
					{
					target.appendPart(ph);
					}
				addConnection(leftItem, ph);
				}
			if (prevPh != null)
				{
				addConnection(prevPh, ph);
				}
			prevPh = ph;
			placeHolders.addElement(ph);
			}
		++numberOfColumns;
		return(placeHolders);
		}


	protected	int		getNodeAtRowColumn(int rowNum, int colNum)
		{
		int rowcol = (rowNum << 16) + colNum;
		for (int i = 0; i < rowColumnPositions.length; ++i)
			{
			if (rowColumnPositions[i] == rowcol)
				{
				return(getTarget().getIndexOfPart(rowColumnPositionsParts[i]));
				}
			}
		return(-1);
		}

	public		void		deleteRow(int rowNum)
		{
		// For every item currently in the row
		MiParts itemsToDelete = new MiParts();
		MiPart target = getTarget();
		for (int colNum = 1; colNum <= numberOfColumns; ++colNum)
			{
			int origRowItemIndex = getNodeAtRowColumn(rowNum, colNum);
			if (origRowItemIndex != -1)
				{
				MiPart toDelete = target.getPart(origRowItemIndex);
				if ((rowNum > 1) && (rowNum < numberOfRows))
					{
					// ... find the nodes to the top and bottom (if any)
					int aboveItemIndex = getNodeAtRowColumn(rowNum - 1, colNum);
					int belowItemIndex = getNodeAtRowColumn(rowNum + 1, colNum);
					if ((aboveItemIndex != -1) && (belowItemIndex != -1))
						{
						// ... and connect the two ...
						addConnection(target.getPart(aboveItemIndex), target.getPart(belowItemIndex));
						}
					}
				itemsToDelete.addElement(toDelete);
				}
			}
		for (int i = 0; i < itemsToDelete.size(); ++i)
			{
			itemsToDelete.elementAt(i).deleteSelf();
			}
		--numberOfRows;
		}

	public		void		deleteColumn(int colNum)
		{
		// For every item currently in the row
		MiParts itemsToDelete = new MiParts();
		MiPart target = getTarget();
		for (int rowNum = 1; rowNum <= numberOfRows; ++rowNum)
			{
			int origColumnItemIndex = getNodeAtRowColumn(rowNum, colNum);
			if (origColumnItemIndex != -1)
				{
				MiPart toDelete = target.getPart(origColumnItemIndex);
				if ((colNum > 1) && (colNum < numberOfColumns))
					{
					// ... find the nodes to the left and right (if any)
					int leftItemIndex = getNodeAtRowColumn(rowNum, colNum - 1);
					int rightItemIndex = getNodeAtRowColumn(rowNum, colNum + 1);
					if ((leftItemIndex != -1) && (rightItemIndex != -1))
						{
						// ... and connect the two ...
						addConnection(target.getPart(leftItemIndex), target.getPart(rightItemIndex));
						}
					}
				itemsToDelete.addElement(toDelete);
				}
			}
		for (int i = 0; i < itemsToDelete.size(); ++i)
			{
			itemsToDelete.elementAt(i).deleteSelf();
			}
		--numberOfColumns;
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this MiPart.
	 				 * @param size		the (returned) minimum size
					 * @overrides		MiContainer#calcMinimumSize
					 * @see			MiPart#getMinimumSize
					 *------------------------------------------------------*/
	protected	void		calcMinimumSize(MiSize size)
		{
		if (getTarget().getNumberOfParts() == 0)
			{
			size.zeroOut();
			return;
			}

		calcRowColumnPositions();
		calcSpacing();
		if (connectionLength != 0)
			{
			size.setSize(
				connectionLength * (numberOfColumns - 1) + maxElementWidth, 
				connectionLength * (numberOfRows - 1) + maxElementHeight);
			}
		else
			{
			size.setSize(
				maxElementWidth + (maxElementWidth + getCellMargins().getWidth()) * (numberOfColumns - 1),
				maxElementHeight + (maxElementHeight + getCellMargins().getHeight()) * (numberOfRows - 1));
			}
		}
	protected	void		calcRowColumnPositions()
		{
		MiParts nodes = new MiParts();
		getNodesInTarget(getTarget(), nodes);
		int numNodes = nodes.size();
		rowColumnPositions = new int[numNodes];
		rowColumnPositionsParts = new MiPart[numNodes];
		int index = 0;
		if (orientation == Mi_HORIZONTAL)
			{
			for (int row = 1; row <= numberOfRows; ++row)
				{
				for (int column = 1; column <= numberOfColumns; ++column)
					{
					rowColumnPositionsParts[index] = nodes.elementAt(index);
					rowColumnPositions[index] = (row << 16) + column;
					++index;
					if (index >= numNodes)
						return;
					}
				}
			}
		else
			{
			for (int column = 1; column <= numberOfColumns; ++column)
				{
				for (int row = 1; row <= numberOfRows; ++row)
					{
					rowColumnPositionsParts[index] = nodes.elementAt(index);
					rowColumnPositions[index] = (row << 16) + column;
					++index;
					if (index >= numNodes)
						return;
					}
				}
			}
		}
					/**------------------------------------------------------
	 				 * This method implements the actual layout algorithm.
					 * @overrides		MiLayout#doLayout
					 *------------------------------------------------------*/
	protected	void		doLayout()
		{
		if ((numberOfRows == 0) || (numberOfColumns == 0))
			calcNumberOfRowsAndColumns(getNumberOfNodes());

		calcRowColumnPositions();
		calcSpacing();
		setNodesToPreferredSizes();

		MiBounds b = getTarget().getInnerBounds();
		MiCoord xmin = b.getXmin();
		MiCoord ymax = b.getYmax();
		MiMargins cellMargins = getCellMargins();
		MiDistance marginWidth = cellMargins.getWidth();
		MiDistance marginHeight = cellMargins.getWidth();

		MiParts nodes = new MiParts();
		getNodesInTarget(getTarget(), nodes);

		int numInLayout = rowColumnPositions.length;
		for (int i = 0; i < numInLayout; ++i)
			{
			if (rowColumnPositions[i] != 0)
				{
				int rowNum = (rowColumnPositions[i] & 0x7fff0000) >> 16;
				int colNum = rowColumnPositions[i] & 0x00007fff;
				if (connectionLength == 0)
					{
					nodes.elementAt(i).setCenter( 
						xmin + cellMargins.left + maxElementWidth/2 + 
							(marginWidth + maxElementWidth) * (colNum - 1),
						ymax - (cellMargins.top + maxElementHeight/2 
							+ (marginHeight + maxElementHeight) * (rowNum - 1)));
					}
				else
					{
					nodes.elementAt(i).setCenter( 
						xmin + cellMargins.left + maxElementWidth/2
							+ connectionLength * (colNum - 1),
						ymax - (cellMargins.top + maxElementHeight/2
							+ connectionLength * (rowNum - 1)));
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Calculates the connection length to use.
					 *------------------------------------------------------*/
	protected	void		calcSpacing()
		{
		calcElementMaxSizes(null);
		connectionLength = preferredEdgeLength + 2 * layoutElementRadius
			+ getCellMargins().getWidth();
		}
					/**------------------------------------------------------
					 * Remove all of the connections and create a random
					 * network of connections of the nodes in the target.
					 * @param connectionType the type of connection that
					 *			 defines the graph
					 *------------------------------------------------------*/
	public 		void		formatTarget(String connectionType)
		{
		stripTargetOfConnections(getTarget(), connectionType);

		MiParts nodes = new MiParts();
		getNodesInTarget(getTarget(), nodes);

		int numNodes = nodes.size();
		if (numNodes == 0)
			return;

		if (calcNumberOfRowsAndColumns(numNodes))
			{
			nodes.removeAllElements();
			getNodesInTarget(getTarget(), nodes);
			numNodes = nodes.size();
			}

		for (int i = 0; i < numNodes; ++i)
			{
			MiPart obj = nodes.elementAt(i);

			// Connect to node below
			if (i + numberOfColumns < numNodes)
				{
				addConnection(obj, nodes.elementAt(i + numberOfColumns));
				}
			// Connect to node to right
			if ((i % numberOfColumns != numberOfColumns - 1) && (i < numNodes - 1))
				{
				addConnection(obj, nodes.elementAt(i + 1));
				}
			}
		}
					// Return true if added nodes to the target
	protected	boolean		calcNumberOfRowsAndColumns(int numNodes)
		{
		if (orientation == Mi_VERTICAL)
			{
			numberOfColumns = (int )(numNodes/Math.sqrt(numNodes));
			numberOfRows = (numNodes + numberOfColumns - 1)/numberOfColumns;
			}
		else
			{
			numberOfRows = (int )(numNodes/Math.sqrt(numNodes));
			numberOfColumns = (numNodes + numberOfRows - 1)/numberOfRows;
			}

		// Assure that the mesh is filled out into a rectangular shape
		if (numberOfRows * numberOfColumns > numNodes)
			{
			for (int i = 0; i < numberOfRows * numberOfColumns - numNodes; ++i)
				getTarget().appendPart(makePlaceHolder());
			return(true);
			}
		return(false);
		}
	}

