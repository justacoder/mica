
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
public class MiCrossBarGraphLayout extends MiManipulatableLayout
	{
	private		int		orientation 		= Mi_HORIZONTAL;
	private		MiParts		sortedACells 		= new MiParts();
	private		MiParts		sortedBCells 		= new MiParts();


					/**------------------------------------------------------
					 * Constructs a new instance of MiCrossBarGraphLayout.
					 *------------------------------------------------------*/
	public				MiCrossBarGraphLayout()
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

		getTarget().invalidateLayout();
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
		insertANode(node, index);
		return(new MiParts(node));
		}
	public		void		insertANode(MiPart node, int index)
		{
		sortCells();
		MiParts cellsOnOtherSide;
		if (sortedACells.contains(getNode(index)))
			cellsOnOtherSide = sortedBCells;
		else
			cellsOnOtherSide = sortedACells;
		for (int i = 0; i < cellsOnOtherSide.size(); ++i)
			{
			addConnection(node, cellsOnOtherSide.elementAt(i));
			}
		node.setCenter(getTarget().getCenter());
		index = getTarget().getIndexOfPart(getNode(index));
		getTarget().insertPart(node, index);
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
		appendANode(node, index);
		return(new MiParts(node));
		}
	protected	void		appendANode(MiPart node, int index)
		{
		sortCells();
		MiParts cellsOnOtherSide;
		if (sortedACells.size() > sortedBCells.size())
			cellsOnOtherSide = sortedACells;
		else
			cellsOnOtherSide = sortedBCells;
		for (int i = 0; i < cellsOnOtherSide.size(); ++i)
			{
			addConnection(node, cellsOnOtherSide.elementAt(i));
			}
		node.setCenter(getTarget().getCenter());
		getTarget().appendPart(node);
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this MiPart.
	 				 * @param size		the (returned) minimum size
					 * @overrides		MiContainer#calcMinimumSize
					 * @see			MiPart#getMinimumSize
					 *------------------------------------------------------*/
	protected	void		calcMinimumSize(MiSize size)
		{
		if (getTarget().getNumberOfParts() > 1)
			{
			calcSpacing();
			sortCells();
			int numCells = Math.max(sortedACells.size(), sortedBCells.size());
			if (orientation == Mi_HORIZONTAL)
				{
				size.setSize(
					(maxElementWidth + getCellMargins().getWidth()) * numCells, 
					maxElementHeight + connectionLength);
				}
			else
				{
				size.setSize(
					maxElementWidth + connectionLength,
					(maxElementHeight + getCellMargins().getHeight()) * numCells);
				}
			}
		else if (getTarget().getNumberOfParts() == 1)
			{
			getTarget().getPart(0).getPreferredSize(size);
			}
		else
			{
			size.zeroOut();
			}
		}
					/**------------------------------------------------------
	 				 * This method implements the actual layout algorithm.
					 * @overrides		MiLayout#doLayout
					 *------------------------------------------------------*/
	protected	void		doLayout()
		{
		calcSpacing();
		setNodesToPreferredSizes();

		sortCells();

		MiBounds b = getTarget().getInnerBounds();
		MiBounds origOuterBounds = getTarget().getBounds(new MiBounds());
		MiSize prefSize = new MiSize();
		getPreferredSize(prefSize);
		MiDistance cx;
		MiDistance cy;
		int numInLayout = sortedACells.size();
		if (orientation == Mi_HORIZONTAL)
			{
			MiDistance margin = getCellMargins().getWidth();
			cx = b.getCenterX() - prefSize.getWidth()/2 + maxElementWidth/2;
			cy = b.getCenterY() - connectionLength/2;
			for (int i = 0; i < numInLayout; ++i)
				{
				sortedACells.elementAt(i).setCenter(cx, cy);
				cx += margin + maxElementWidth;
				}

			cx = b.getCenterX() - prefSize.getWidth()/2 + maxElementWidth/2;
			cy = b.getCenterY() + connectionLength/2;

			numInLayout = sortedBCells.size();
			for (int i = 0; i < numInLayout; ++i)
				{
				sortedBCells.elementAt(i).setCenter(cx, cy);
				cx += margin + maxElementWidth;
				}
			}
		else
			{
			MiDistance margin = getCellMargins().getHeight();
			cx = b.getCenterX() - connectionLength/2;
			cy = b.getCenterY() + prefSize.getHeight()/2 - maxElementHeight/2;
			for (int i = 0; i < numInLayout; ++i)
				{
				sortedACells.elementAt(i).setCenter(cx, cy);
				cy -= margin + maxElementHeight;
				}
			cx = b.getCenterX() + connectionLength/2;
			cy = b.getCenterY() + prefSize.getHeight()/2 - maxElementHeight/2;
			numInLayout = sortedBCells.size();
			for (int i = 0; i < numInLayout; ++i)
				{
				sortedBCells.elementAt(i).setCenter(cx, cy);
				cy -= margin + maxElementHeight;
				}
			}
		getTarget().setCenter(origOuterBounds.getCenter());
		}
	protected	void		sortCells()
		{
		sortedACells.removeAllElements();
		sortedBCells.removeAllElements();
		int numInTarget = getTarget().getNumberOfParts();
		for (int i = 0; i < numInTarget; ++i)
			{
			MiPart element = getTarget().getPart(i);
			if ((!(element instanceof MiConnection)) 
				&& (!sortedACells.contains(element))
				&& (!sortedBCells.contains(element)))
				{
				if (isConnectedToAnyACells(element))
					{
					sortedBCells.addElement(element);
					}
				else
					{
					sortedACells.addElement(element);
					}
				}
			}
		}
	protected	boolean		isConnectedToAnyACells(MiPart element)
		{
		for (int i = 0; i < element.getNumberOfConnections(); ++i)
			{
			MiConnection conn = element.getConnection(i);
			if ((conn.isType(edgeConnectionType)) 
				&& (sortedACells.contains(conn.getOther(element))))
				{
				return(true);
				}
			}
		return(false);
		}
					/**------------------------------------------------------
					 * Calculates the connection length to use.
					 *------------------------------------------------------*/
	protected	void		calcSpacing()
		{
		calcElementMaxSizes(null);
		if (orientation == Mi_HORIZONTAL)
			{
			connectionLength = Math.max(preferredEdgeLength, 
				2 * maxElementWidth + getCellMargins().getWidth());
			}
		else
			{
			connectionLength = Math.max(preferredEdgeLength, 
				2 * maxElementHeight + getCellMargins().getHeight());
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

		MiParts nodes = new MiParts();
		getNodesInTarget(getTarget(), nodes);

		int numNodes = nodes.size();
		for (int i = 0; i < (numNodes + 1)/2; ++i)
			{
			MiPart obj = nodes.elementAt(i);
			for (int j = (numNodes + 1)/2; j < numNodes; ++j)
				{
				addConnection(obj, nodes.elementAt(j));
				}
			}
		}


	}

