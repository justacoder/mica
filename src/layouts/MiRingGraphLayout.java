
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
public class MiRingGraphLayout extends MiManipulatableLayout
	{
	private		MiParts		sortedNodes = new MiParts();


					
					/**------------------------------------------------------
					 * Constructs a new instance of MiRingGraphLayout.
					 *------------------------------------------------------*/
	public				MiRingGraphLayout()
		{
		setKeepConnectionsBelowNodes(true);
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
		int numCells = getNumberOfNodes();
		if (index > 0)
			prevNode = getNode(index - 1);
		else
			prevNode = getNode(numCells - 1);

		if (index <= numCells - 1)
			nextNode = getNode(index);
		else
			nextNode = getNode(0);

		
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
		node.setCenter(getTarget().getCenter());
		index = getTarget().getIndexOfPart(getNode(index));
		getTarget().insertPart(node, index);
		addConnection(prevNode, node);
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
		return(insertNode(node, 0));
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
		MiPart cell = getNode(index);
		MiPart prevNode = null;
		MiPart nextNode = null;
		int numCells = getNumberOfNodes();
		if (index > 0)
			prevNode = getNode(index - 1);
		else
			prevNode = getNode(numCells - 1);

		if (index < numCells - 1)
			nextNode = getNode(index + 1);
		else
			nextNode = getNode(0);

		cell.deleteSelf();
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
		MiParts nodes = new MiParts();
		getNodesInTarget(getTarget(), nodes);
		if (nodes.size() > 1)
			{
			calcSpacing();
			size.setSize(
				2 * (connectionLength + maxElementRadius), 
				2 * (connectionLength + maxElementRadius));
			}
		else if (nodes.size() == 1)
			{
			nodes.elementAt(0).getMinimumSize(size);
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
		MiParts nodes = new MiParts();
		getNodesInTarget(getTarget(), nodes);
		calcSpacing();
		setNodesToPreferredSizes();

		MiBounds b = getTarget().getInnerBounds();
		MiDistance cx = b.getCenterX();
		MiDistance cy = b.getCenterY();
		int numInLayout = nodes.size();
		double angle = 0;
		double angleIncrement = numInLayout > 1 ? 2 * Math.PI/numInLayout : 0;
		sortNodes(nodes, sortedNodes);
		for (int i = 0; i < sortedNodes.size(); ++i)
			{
			sortedNodes.elementAt(i).setCenter( 
					cx + connectionLength * Math.cos(angle),
					cy + connectionLength * Math.sin(angle));
			angle += angleIncrement;
			}
		for (int i = 0; i < nodes.size() - sortedNodes.size(); ++i)
			{
			int index = 0;
			for (int j = 0; j < nodes.size(); ++j)
				{
				if (!sortedNodes.contains(nodes.elementAt(j)))
					{
					if (index == i)
						{
						nodes.elementAt(j).setCenter( 
						cx + connectionLength * Math.cos(angle),
						cy + connectionLength * Math.sin(angle));
						angle += angleIncrement;
						}
					++index;
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Examines the nodes in the given list of nodes and
					 * sorts them in order of their position on the ring
					 * based on their connectivity and returns this list
					 * in the given sorted nodes parameter.
					 * @param nodes		the list of nodes
					 * @param sortedNodes	the (returned) sorted list of nodes
					 *------------------------------------------------------*/
	protected	void		sortNodes(MiParts nodes, MiParts sortedNodes)
		{
		sortedNodes.removeAllElements();
		if (nodes.size() > 0)
			{
			MiPart node = nodes.elementAt(0);
			MiPart startNode = node;
			MiPart prevNode = null;
			while (true)
				{
				sortedNodes.addElement(node);
				MiPart nextNode = getNextNode(prevNode, node);
				if ((nextNode == null) || (nextNode == startNode)
					// ---------------------------------------------------------------
					// Add this to prevent an infinite loop traversing badly
					// formed rings
					// ---------------------------------------------------------------
					|| (sortedNodes.size() == nodes.size()))
					{
					return;
					}
				prevNode = node;
				node = nextNode;
				}
			}
		}
					/**------------------------------------------------------
					 * Examines the nodes in the given list of nodes and
					 * returns the next node it's position on the ring.
					 * @param prevNode	the previous node in the ring or null
					 * @param node		the current node in the ring
					 * @return 		the next node in the ring or null
					 *------------------------------------------------------*/
	protected	MiPart		getNextNode(MiPart prevNode, MiPart node)
		{
		int numConns = node.getNumberOfConnections();
		for (int i = 0; i < numConns; ++i)
			{
			MiConnection conn = node.getConnection(i);
			if ((conn.isType(edgeConnectionType)) && (conn.getOther(node) != prevNode))
				{
				return(conn.getOther(node));
				}
			}
		return(null);
		}
					/**------------------------------------------------------
					 * Calculates the connection length to use.
					 *------------------------------------------------------*/
	protected	void		calcSpacing()
		{
		calcElementMaxSizes(null);
		int numCells = getNumberOfNodes();
		MiDistance circumference 
			= (numCells  - 1) * (maxElementRadius * 2 + getCellMargins().getWidth());
		MiDistance radius1 = circumference/(2 * Math.PI);
		MiDistance radius2 = preferredEdgeLength + 2 * maxElementRadius;
		connectionLength = Math.max(radius1, radius2);
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
			if (i == numNodes - 1)
				{
				addConnection(nodes.elementAt(i), nodes.elementAt(0));
				}
			}
		}
	}

