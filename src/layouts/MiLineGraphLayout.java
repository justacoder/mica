
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
public class MiLineGraphLayout extends MiManipulatableLayout
	{
	private		int		orientation 		= Mi_HORIZONTAL;
	private		MiParts		sortedNodes 		= new MiParts();


					/**------------------------------------------------------
					 * Constructs a new instance of MiLineGraphLayout.
					 *------------------------------------------------------*/
	public				MiLineGraphLayout()
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
		MiPart prevNode = null;
		MiPart nextNode = null;
		int numNodes = getNumberOfNodes();
		if (index > 0)
			prevNode = getNode(index - 1);
		else
			prevNode = null;

		if (index <= numNodes - 1)
			nextNode = getNode(index);
		else
			nextNode = null;

		
		if ((prevNode != null) && (nextNode != null))
			{
			// Delete conn between prev and next nodes...
			int numConns = prevNode.getNumberOfConnections();
			for (int i = 0; i < numConns; ++i)
				{
				if ((prevNode.getConnection(i).isType(edgeConnectionType))
					&& (prevNode.getConnection(i).getOther(prevNode) == nextNode))
					{
					prevNode.getConnection(i).deleteSelf();
					break;
					}
				}
			}
		node.setCenter(getTarget().getCenter());
		index = getTarget().getIndexOfPart(getNode(index));
		getTarget().insertPart(node, index);
		if (prevNode != null)
			addConnection(prevNode, node);
		if (nextNode != null)
			addConnection(node, nextNode);
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
		getTarget().appendPart(node);
		if (numNodes > 1)
			addConnection(getNode(numNodes - 2), node);
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
		MiPart cell = getNode(index);

		MiPart prevNode = null;
		MiPart nextNode = null;
		int numNodes = getNumberOfNodes();
		if (index > 0)
			prevNode = getNode(index - 1);
		else
			prevNode = null;

		if (index < numNodes - 1)
			nextNode = getNode(index + 1);
		else
			nextNode = null;

		cell.deleteSelf();
		if ((prevNode != null) && (nextNode != null))
			addConnection(prevNode, nextNode);
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
			int numNodes = getNumberOfNodes();
			if (orientation == Mi_HORIZONTAL)
				{
				size.setSize(
					connectionLength * (numNodes - 1) + maxElementWidth, 
					maxElementHeight);
				}
			else
				{
				size.setSize(
					maxElementWidth,
					connectionLength * (numNodes - 1) + maxElementHeight);
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

		sortNodes();
		MiBounds b = getTarget().getInnerBounds();
		MiSize prefSize = new MiSize();
		getPreferredSize(prefSize);
		MiDistance cx = b.getCenterX() - prefSize.getWidth()/2 + maxElementWidth/2;
		MiDistance cy = b.getCenterY() - prefSize.getHeight()/2 + maxElementHeight/2;
		if (orientation == Mi_HORIZONTAL)
			cy = b.getCenterY();
		else
			cx = b.getCenterX();
		int numInLayout = sortedNodes.size();
		for (int i = 0; i < numInLayout; ++i)
			{
			sortedNodes.elementAt(i).setCenter(cx, cy);
			if (orientation == Mi_HORIZONTAL)
				cx += connectionLength;
			else
				cy += connectionLength;
			}
		}
	protected	void		sortNodes()
		{
		sortedNodes.removeAllElements();
		int numInTarget = getTarget().getNumberOfParts();
		for (int i = 0; i < numInTarget; ++i)
			{
			MiPart element = getTarget().getPart(i);
			if ((!(element instanceof MiConnection)) && (!sortedNodes.contains(element)))
				{
				if (getNumConnectionsOfType(element, edgeConnectionType) < 2)
					{
					sortStringOfNodes(null, element);
					return;
					}
				}
			}
		}
	private		int		getNumConnectionsOfType(MiPart element, String connectionType)
		{
		int num = 0;
		int numConns = element.getNumberOfConnections();
		for (int i = 0; i < numConns; ++i)
			{
			if (element.getConnection(i).isType(edgeConnectionType))
				++num;
			}
		return(num);
		}
	protected	void		sortStringOfNodes(MiPart prevNode, MiPart node)
		{
		sortedNodes.addElement(node);
		for (int i = 0; i < node.getNumberOfConnections(); ++i)
			{
			MiConnection conn = node.getConnection(i);
			if ((conn.getOther(node) != prevNode)
				&& (conn.getDestination() != null)
				&& (conn.isType(edgeConnectionType)))
				{
				MiPart other = conn.getOther(node);
				if (!sortedNodes.contains(other))
					{
					sortStringOfNodes(node, other);
					return;
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
		connectionLength = preferredEdgeLength + layoutElementRadius * 2
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
		for (int i = 1; i < numNodes; ++i)
			{
			addConnection(nodes.elementAt(i - 1), nodes.elementAt(i));
			}
		}
	}

