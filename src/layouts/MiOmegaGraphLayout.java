
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
public class MiOmegaGraphLayout extends MiManipulatableLayout
	{
					/**------------------------------------------------------
					 * Constructs a new instance of MiOmegaGraphLayout.
					 *------------------------------------------------------*/
	public				MiOmegaGraphLayout()
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
		MiParts cells = new MiParts();
		getNodesInTarget(getTarget(), cells);
		for (int i = 0; i < cells.size(); ++i)
			{
			addConnection(node, cells.elementAt(i));
			}
		node.setCenter(getTarget().getCenter());
		index = getTarget().getIndexOfPart(getNode(index));
		getTarget().insertPart(node, index);
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
		MiParts cells = new MiParts();
		getNodesInTarget(getTarget(), cells);
		for (int i = 0; i < cells.size(); ++i)
			{
			addConnection(node, cells.elementAt(i));
			}
		node.setCenter(getTarget().getCenter());
		getTarget().appendPart(node);
		return(new MiParts(node));
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this MiPart.
	 				 * @param size		the (returned) minimum size
					 * @overrides		MiContainer#calcMinimumSize
					 * @see			MiPart#getMinimumSize
					 *------------------------------------------------------*/
	protected	void		calcMinimumSize(MiSize size)
		{
		if (getNumberOfNodes() > 1)
			{
			calcSpacing();
			size.setSize(
				(connectionLength + maxElementRadius) * 2, 
				(connectionLength + maxElementRadius) * 2);
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

		MiParts nodes = new MiParts();
		getNodesInTarget(getTarget(), nodes);
		MiBounds b = getTarget().getInnerBounds();
		MiDistance cx = b.getCenterX();
		MiDistance cy = b.getCenterY();
		int numInLayout = nodes.size();
		double angle = 0;
		double angleIncrement = numInLayout > 1 ? 2 * Math.PI/numInLayout : 0;
		for (int i = 0; i < numInLayout; ++i)
			{
			nodes.elementAt(i).setCenter( 
				cx + connectionLength * Math.cos(angle),
				cy + connectionLength * Math.sin(angle));
			angle += angleIncrement;
			}
		}
					/**------------------------------------------------------
					 * Calculates the connection length to use.
					 *------------------------------------------------------*/
	protected	void		calcSpacing()
		{
		calcElementMaxSizes(null);
		int numCells = getNumberOfNodes();
		MiDistance circumference = (numCells  - 1) * (maxElementRadius * 2 + getCellMargins().getWidth());
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
		for (int i = 0; i < numNodes - 1; ++i)
			{
			MiPart obj = nodes.elementAt(i);
			for (int j = i + 1; j < numNodes; ++j)
				{
				addConnection(obj, nodes.elementAt(j));
				}
			}
		}

	}

