
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiOutlineGraphLayout extends MiManipulatableLayout
	{
	private		MiDistance 	indentation;



					/**------------------------------------------------------
					 * Constructs a new instance of MiOutlineGraphLayout.
					 *------------------------------------------------------*/
	public				MiOutlineGraphLayout()
		{
		setKeepConnectionsBelowNodes(true);
		setAlleyHSpacing(20);
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
		MiPart prevNode = null;
		MiPart nextNode = null;
		int numNodes = getNumberOfNodes();
		if (index == -1)
			index = numNodes - 1;

		if (numNodes == 0)
			{
			}
		else if ((index != -1) && (index < numNodes))
			{
			nextNode = getNode(index);
			if (index > 0)
				prevNode = getParentNode(nextNode);
			}
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
		getTarget().appendPart(node);
		if (prevNode != null)
			addConnection(prevNode, node);
		if (nextNode != null)
			addConnection(node, nextNode);
		return(new MiParts(node));
		}
					/**------------------------------------------------------
					 * Examines the nodes in the given list of nodes and
					 * returns the parent node from it's position in the tree.
					 * @param node		the current node in the tree
					 * @return 		the parent node in the tree or null
					 *------------------------------------------------------*/
	protected	MiPart		getParentNode(MiPart node)
		{
		int numConns = node.getNumberOfConnections();
		for (int i = 0; i < numConns; ++i)
			{
			MiConnection conn = node.getConnection(i);
			if ((conn.isType(edgeConnectionType)) && (conn.getSource() != node))
				{
				return(conn.getSource());
				}
			}
		return(null);
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
		node.setCenter(getTarget().getCenter());
		int numNodes = getNumberOfNodes();
		if (index == -1)
			index = numNodes;

		if (numNodes == 0)
			{
			}
		else
			{
			addConnection(getNode(index), node);
			}
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
		if (getTarget().getNumberOfParts() == 0)
			size.zeroOut();
		else
			calcPrefSize(size);
		}
					/**------------------------------------------------------
	 				 * This method implements the actual layout algorithm.
					 * @overrides		MiLayout#doLayout
					 *------------------------------------------------------*/
	protected	void		doLayout()
		{
		setNodesToPreferredSizes();

		indentation = getAlleyHSpacing();
		MiParts roots = new MiParts();
		MiUtility.gatherRootNodes(getTarget(), roots);
		MiDistance leftX = getTarget().getXmin();
		MiCoord lastTopY = getTarget().getYmax();

		for (int i = 0; i < roots.size(); ++i)
			{
			// Place root...
			roots.elementAt(i).translate(
				leftX - roots.elementAt(i).getXmin(), 
				lastTopY - roots.elementAt(i).getYmax());
			lastTopY = roots.elementAt(i).getYmin() - getAlleyVSpacing();
			lastTopY = placeBranch(null, roots.elementAt(i), leftX + indentation, lastTopY);
			}
		}

	private		MiCoord		placeBranch(
						MiPart grandParent, 
						MiPart parent, 
						MiCoord leftX, 
						MiCoord lastTopY)
		{
		MiDistance tx, ty;
		for (int i = 0; i < parent.getNumberOfConnections(); ++i)
			{
			MiConnection conn = parent.getConnection(i);
			if ((conn.getOther(parent) != grandParent) && (conn.isType(edgeConnectionType)))
				{
				MiPart child = conn.getOther(parent);
				tx = leftX - child.getXmin();
				ty = lastTopY - child.getYmax();
				child.translate(tx, ty);
				lastTopY = child.getYmin() - getAlleyVSpacing();
				lastTopY = placeBranch(parent, child, leftX + indentation, lastTopY);
				if (conn.getGraphics() instanceof MiLine)
					{
					MiLine line = (MiLine )conn.getGraphics();
					line.setMaintainOrthogonality(false);
					while (line.getNumberOfPoints() > 3)
						line.removePoint(line.getNumberOfPoints() - 1);
					while (line.getNumberOfPoints() < 3)
						line.appendPoint(new MiPoint());
					line.setPoint(1, 
						new MiPoint(parent.getCenterX(), child.getCenterY()));
					line.setMaintainOrthogonality(true);
					}
				}
			}
		return(lastTopY);
		}
	protected	void		calcPrefSize(MiSize size)
		{
		indentation = getAlleyHSpacing();
		MiParts roots = new MiParts();
		MiUtility.gatherRootNodes(getTarget(), roots);
		MiDistance leftX = 0;
		MiCoord lastTopY = 0;
		MiBounds prefBounds = new MiBounds(0,0,0,0);

		for (int i = 0; i < roots.size(); ++i)
			{
			// Place root...
			roots.elementAt(i).getPreferredSize(size);
			lastTopY = lastTopY - size.getHeight() - getAlleyVSpacing();
			lastTopY = calcBranchPrefs(
				null, roots.elementAt(i), leftX + indentation, lastTopY, prefBounds);
			}
		size.setSize(prefBounds);
		}

	private		MiCoord		calcBranchPrefs(
						MiPart grandParent, 
						MiPart parent, 
						MiCoord leftX, 
						MiCoord lastTopY,
						MiBounds prefBounds)
		{
		MiDistance tx, ty;
		MiSize prefSize = new MiSize();
		for (int i = 0; i < parent.getNumberOfConnections(); ++i)
			{
			MiConnection conn = parent.getConnection(i);
			if ((conn.getOther(parent) != grandParent) && (conn.isType(edgeConnectionType)))
				{
				MiPart child = conn.getOther(parent);
				child.getPreferredSize(prefSize);
				prefBounds.union(
					leftX + prefSize.getWidth(), 
					lastTopY - prefSize.getHeight());
				lastTopY = lastTopY - prefSize.getHeight() - getAlleyVSpacing();
				lastTopY = calcBranchPrefs(
					parent, child, leftX + indentation, lastTopY, prefBounds);
				}
			}
		return(lastTopY);
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
		int naryTree = 2;
		int lastChild = 0;
		for (int i = 0; i < numNodes; ++i)
			{
			for (int j = lastChild + 1; (j < naryTree + lastChild + 1) && (j < numNodes); ++j)
				{
				addConnection(nodes.elementAt(i), nodes.elementAt(j));
				}
			lastChild = lastChild + naryTree;
			}
		}
	}

