
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
public class MiStarGraphLayout extends MiManipulatableLayout
	{
	private		MiPart		assignedCenterCell;
	private		MiPart		centerCell;
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiSize		tmpSize			= new MiSize();
	private		boolean		hasVisibleCenterCell	= true;
	private		double		initialAngle		= 0;




					/**------------------------------------------------------
					 * Constructs a new instance of MiStarGraphLayout.
					 *------------------------------------------------------*/
	public				MiStarGraphLayout()
		{
		setKeepConnectionsBelowNodes(true);
		}
	public		void		setHasVisibleCenterCell(boolean flag)
		{
		hasVisibleCenterCell = flag;
		if (centerCell != null)
			centerCell.setVisible(hasVisibleCenterCell);
		}
	public		boolean		getHasVisibleCenterCell()
		{
		return(hasVisibleCenterCell);
		}

	public		void		setCenterCell(MiPart cell)
		{
		assignedCenterCell = cell;
		updateCenterCell(cell);
		}
	public		MiPart		getCenterCell()
		{
		if (centerCell == null)
			calcStarCenterCell();
		return(centerCell);
		}

	public		void		setInitialAngle(double angle)
		{
		initialAngle = angle;
		}
	public		double		getInitialAngle()
		{
		return(initialAngle);
		}
					/**------------------------------------------------------
					 * Inserts the given node at the given index. 
					 * @param node		the node to insert
					 * @param index		the index of the node that the
					 *			given node will be inserted before
					 * @return		the list of nodes that were inserted
					 *			(the given node and any placeholders
					 *			that may have to be added).
					 * @overrides		MiManipulatableLayout#insertNode
					 *------------------------------------------------------*/
	public		MiParts		insertNode(MiPart node, int index)
		{
		node.setCenter(getTarget().getCenter());
		index = getTarget().getIndexOfPart(getNode(index));
		getTarget().insertPart(node, index);
		addConnection(getCenterCell(), node);
		return(new MiParts(node));
		}
					/**------------------------------------------------------
					 * Appends the given node after the given index. 
					 * @param node		the node to append
					 * @param index		the index of the node that the
					 *			given node will be appended after
					 *			(-1 indicates that this should append 
					 *			the given node after the last node)
					 * @return		the list of nodes that were inserted
					 *			(the given node and any placeholders
					 *			that may have to be added).
					 * @overrides		MiManipulatableLayout#appendNode
					 *------------------------------------------------------*/
	public		MiParts		appendNode(MiPart node, int index)
		{
		int numNodes = getNumberOfNodes();
		if ((index != -1) && (index < numNodes - 1))
			{
			return(insertNode(node, index + 1));
			}
		node.setCenter(getTarget().getCenter());
		getTarget().appendPart(node);
		addConnection(getCenterCell(), node);
		return(new MiParts(node));
		}
					/**------------------------------------------------------
					 * Deletes the node at the given index. If the node is
					 * not a MiPlaceHolder then it is replaced by one. In any
					 * case the node is deleted. 
					 * @param index		the index of the node to delete
					 * @overrides		MiManipulatableLayout#deleteNode
					 *------------------------------------------------------*/
	public		void		deleteNode(int index)
		{
		if (index == -1)
			index = getNumberOfNodes() - 1;
		if (centerCell != getNode(index))
			getNode(index).deleteSelf();
		}

	protected	void		reCalcBounds(MiBounds b)
		{
		super.reCalcBounds(b);
		MiSize preferredSize = getPreferredSize(new MiSize());
		if (preferredSize.width > b.getWidth())
			b.setWidth(preferredSize.width);
		if (preferredSize.height > b.getHeight())
			b.setHeight(preferredSize.height);
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

		MiBounds graphSize = new MiBounds();
		doLayout(true, graphSize);
		size.setSize(graphSize);
		}
	protected	void		calcStarCenterCell()
		{
		if (assignedCenterCell != null)
			{
			updateCenterCell(assignedCenterCell);
			return;
			}

		int centerCellIndex = 0;
		int maxNumConns = 0;
		MiParts connections = new MiParts();
		for (int i = 0; i < getTarget().getNumberOfParts(); ++i)
			{
			MiPart element = getTarget().getPart(i);
			if (!(element instanceof MiConnection))
				{
				int num = 0;
				for (int j = 0; j < element.getNumberOfConnections(); ++j)
					{
					MiConnection conn = element.getConnection(j);
					if ((conn.getSource() == element) && (conn.getDestination() != null)
						&& (conn.isType(edgeConnectionType)))
						{
						++num;
						}
					}
				if (num > maxNumConns)
					{
					maxNumConns = num;
					centerCellIndex = i;
					}
				}
			}
		updateCenterCell(getTarget().getPart(centerCellIndex));
		}
	protected	void		updateCenterCell(MiPart cell)
		{
		if (cell == centerCell)
			return;

		if (centerCell != null)
			centerCell.setVisible(true);
		centerCell = cell;
		if (centerCell != null)
			centerCell.setVisible(hasVisibleCenterCell);
		}
					/**------------------------------------------------------
	 				 * This method implements the actual layout algorithm.
					 * @overrides		MiLayout#doLayout
					 *------------------------------------------------------*/
	protected	void		doLayout()
		{
		doLayout(false, null);
		}
					/**------------------------------------------------------
	 				 * This method implements the actual layout algorithm or
					 * merely calculates the size.
					 * @param calcSizeOnly	true if only calculate what the size
					 *			will be after a layout
					 * @param graphBounds	if calcSizeOnly is true then this
					 *			is returned with the size
					 * @overrides		MiLayout#doLayout
					 *------------------------------------------------------*/
	protected	void		doLayout(boolean calcSizeOnly, MiBounds graphBounds)
		{
//System.out.println("Star Layout: calcSizeOnly = " + calcSizeOnly + ", graphBounds = " + graphBounds);

		// TEST for sparse networks want to be able to indicate which one (of two) is centerCell = null;
		calcStarCenterCell();
		calcSpacing();

		if (!calcSizeOnly)
			setNodesToPreferredSizes();

		MiBounds origOuterBounds = getTarget().getBounds(new MiBounds());
		MiBounds b = getTarget().getInnerBounds(tmpBounds);

		MiDistance cx = b.getCenterX();
		MiDistance cy = b.getCenterY();
		int numInLayout = getNumberOfNodes();
		double angle = initialAngle;
		double angleIncrement = numInLayout > 1 ? 2 * Math.PI/(numInLayout - 1) : 0;
		for (int i = 0; i < numInLayout; ++i)
			{
			MiPart cell = getNode(i);
			if ((!cell.isVisible()) && (cell != centerCell))
				continue;
			if (cell == centerCell)
				{
				if (calcSizeOnly)
					{
					b.setSize(cell.getPreferredSize(tmpSize));
					b.setCenter(cx, cy);
					graphBounds.union(b);
					}
				else
					{
					cell.setCenter(cx, cy);
					}
				}
			else
				{
				if (calcSizeOnly)
					{
					b.setSize(cell.getPreferredSize(tmpSize));
					b.setCenter( 
						cx + xConnectionLength * Math.cos(angle),
						cy + yConnectionLength * Math.sin(angle));
					graphBounds.union(b);
					}
				else
					{
					cell.setCenter( 
						cx + xConnectionLength * Math.cos(angle),
						cy + yConnectionLength * Math.sin(angle));
					}
				angle += angleIncrement;
				}
			}
		if (!calcSizeOnly)
			{
			getTarget().refreshBounds();
			getTarget().setXmin(origOuterBounds.getXmin());
			getTarget().setYmin(origOuterBounds.getYmin());
			}
		}
					/**------------------------------------------------------
					 * Calculates the connection length to use.
					 *------------------------------------------------------*/
	protected	void		calcSpacing()
		{
		MiDistance circumference;
		int numCells = getNumberOfNodes();

		calcElementMaxSizes(null);
		if (getLayoutElementsBasedOnSizeOfElementRadii())
			{
			circumference = (numCells  - 1) * (layoutElementRadius * 2 + getCellMargins().getWidth());
			MiDistance radius1 = circumference/(2 * Math.PI);
			MiDistance radius2 = preferredEdgeLength + 2 * layoutElementRadius;
			connectionLength = Math.max(radius1, radius2);
			xConnectionLength = connectionLength;
			yConnectionLength = connectionLength;
			}
		else
			{
			circumference = (numCells - 1) * (layoutElementWidth + getCellMargins().getWidth());
			xConnectionLength = Math.max(
				preferredEdgeLength + layoutElementWidth,
				circumference/(2 * Math.PI));
			circumference = (numCells - 1) * (layoutElementHeight + getCellMargins().getHeight());
			yConnectionLength = Math.max(
				preferredEdgeLength + layoutElementHeight,
				circumference/(2 * Math.PI));
			}
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

		MiParts nodes = getNodesInTarget(getTarget(), new MiParts());
		int numNodes = nodes.size();
		MiPart centerCell = null;

		if (numNodes == 0)
			return;

		if ((hasVisibleCenterCell) && (numNodes != 0))
			{
			centerCell = nodes.elementAt(0);
			}
		else if ((numNodes == 0) || (!(nodes.elementAt(0) instanceof MiPlaceHolder))
			|| (nodes.elementAt(0).isVisible()))
			{
			centerCell = makePlaceHolder();
			if (!hasVisibleCenterCell)
				centerCell.setVisible(false);
			getTarget().insertPart(centerCell, 0);
			nodes.insertElementAt(centerCell, 0);
			++numNodes;
			}
		else
			{
			centerCell = nodes.elementAt(0);
			}
		MiConnection conn;
		for (int i = 1; i < numNodes; ++i)
			{
			addConnection(centerCell, nodes.elementAt(i));
			}
		}
	}

