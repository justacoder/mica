
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
public class MiUndirGraphLayout extends MiManipulatableLayout implements MiiSimpleAnimator
	{
	private		int		numNodes;
	private		MiPart[]	nodes;
	private		int		numConnections;
	private		int[]		srcs;
	private		int[]		dests;
	private		MiCoord[]	x;
	private		MiCoord[]	y;
	private		MiDistance[]	widths;
	private		MiDistance[]	heights;
	private		double[]	dx;
	private		double[]	dy;
	private		double		maxDx;
	private		double		maxDy;
	private		MiBounds	universe;

	private		int		maxNumIterations 		= 100;
	private		int		numAdjustmentsPerAnimation	= 4;
	private		int		maxNumAnimations;
	private		double		finishPerturbationX;
	private		double		finishPerturbationY;
	private		double		maxLengthPerturbation;
	private		double		minDesiredConnectionLengthSquared;
	private		double		maxDesiredConnectionLengthSquared;
	private		boolean		animate;
	private		MiPart		connectionLayer;


	public				MiUndirGraphLayout()
		{
		setKeepConnectionsBelowNodes(true);
		}

	public		void		setLayoutAnimated(boolean flag)
		{
		animate = flag;
		}
	public		boolean		getLayoutAnimated()
		{
		return(animate);
		}
	public		boolean		isAnimating()
		{
		return((animate) && (maxNumAnimations > 0));
		}
	public		void		setConnectionLayer(MiPart connLayer)
		{
		connectionLayer = connLayer;
		}
	public		MiPart		getConnectionLayer()
		{
		return(connectionLayer);
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
		MiPart part = getNode(index);
		index = getTarget().getIndexOfPart(part);
		getTarget().insertPart(node, index);
		if (part.getNumberOfConnections() > 0)
			{
			MiConnection conn = part.getConnection(0);
			MiPart src = conn.getSource();
			MiPart dest = conn.getSource();
			addConnection(src, node);
			addConnection(node, dest);
			conn.deleteSelf();
			}
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
		node.setCenter(getTarget().getCenter());
		MiPart part;
		if (index != -1)
			{
			part = getNode(index);
			index = getTarget().getIndexOfPart(part);
			if (index + 1 >= getTarget().getNumberOfParts())
				getTarget().appendPart(node);
			else
				getTarget().insertPart(node, index + 1);
			}
		else
			{
			if (getTarget().getNumberOfParts() == 0)
				return(new MiParts(node));
			part = getTarget().getPart(getTarget().getNumberOfParts() -1);
			getTarget().appendPart(node);
			}
		addConnection(part, node);
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
			{
			size.zeroOut();
			return;
			}

		calcSpacing();
		int numNodes = getNumberOfNodes();
		size.setSize(
			connectionLength * numNodes + 2 * maxElementRadius + getCellMargins().getWidth(),
			connectionLength * numNodes + 2 * maxElementRadius + getCellMargins().getHeight());
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
	 				 * This method implements the actual layout algorithm.
					 * @overrides		MiLayout#doLayout
					 *------------------------------------------------------*/
	protected	void		doLayout()
		{
		setup();
		setNodesToPreferredSizes();

		if (animate)
			{
			maxNumAnimations = maxNumIterations;
			MiAnimationManager.addAnimator(getTarget(), this);
			return;
			}
		for (int i = 0; i < maxNumIterations; ++i)
			{
			MiDistance dx = 0;
			MiDistance dy = 0;
			for (int j = 0; j < numAdjustmentsPerAnimation; ++j)
				{
				adjust();
				dx += maxDx;
				dy += maxDy;
				}
			if ((dx < finishPerturbationX) && (dy < finishPerturbationY))
				break;
			}
//System.out.println("maxDx = " + maxDx);
//System.out.println("finishPerturbationX = " + finishPerturbationX);
//System.out.println("maxDy = " + maxDy);
//System.out.println("finishPerturbationY = " + finishPerturbationY);

		place();
		}
	public		long		animate()
		{
		maxNumAnimations -= numAdjustmentsPerAnimation;
		if (maxNumAnimations < 0)
			return(-1);
		for (int i = 0; i < numAdjustmentsPerAnimation; ++i)
			adjust();
		// TEST boolean eEnabled = getTarget().getContainingEditor().getEditorLayoutInvalidationsEnabled();
		boolean tEnabled = getTarget().getInvalidLayoutNotificationsEnabled();
		// TEST getTarget().getContainingEditor().setEditorLayoutInvalidationsEnabled(false);
		getTarget().setInvalidLayoutNotificationsEnabled(false);
		place();
		// TEST getTarget().getContainingEditor().setEditorLayoutInvalidationsEnabled(eEnabled);
		getTarget().setInvalidLayoutNotificationsEnabled(tEnabled);
		if ((maxDx < finishPerturbationX) && (maxDy < finishPerturbationY))
			return(-1);
		return(100);
		}
	protected	void		place()
		{
		MiPoint center = getTarget().getCenter();
		for (int i = 0; i < numNodes; ++i)
			{
			nodes[i].setCenter(x[i], y[i]);
			}
		getTarget().refreshBounds();
		getTarget().setCenter(center);
		}

	protected	void		setup()
		{
		calcSpacing();

		int num = getTarget().getNumberOfParts();
		nodes = new MiPart[num];
		srcs = new int[num];
		dests = new int[num];
		x = new MiCoord[num];
		y = new MiCoord[num];
		dx = new double[num];
		dy = new double[num];
		widths = new MiDistance[num];
		heights = new MiDistance[num];
		numNodes = 0;
		numConnections = 0;
		maxDx = 0;
		maxDy = 0;
		// ---------------------------------------------------------------
		// If there is a containing editor, get it's universe so we can
		// make sure we don't perturb nodes outside of universe space.
		// ---------------------------------------------------------------
		universe = null;
		MiEditor editor = getTarget().getContainingEditor();
		if (editor != null)
			universe = editor.getUniverseBounds();

		for (int i = 0; i < getTarget().getNumberOfParts(); ++i)
			{
			MiPart obj = getTarget().getPart(i);
			if (!(obj instanceof MiConnection))
				{
				nodes[numNodes] = obj;
				widths[numNodes] = obj.getWidth();
				heights[numNodes] = obj.getHeight();
				x[numNodes] = obj.getCenterX();
				y[numNodes] = obj.getCenterY();
				dx[numNodes] = 0;
				dy[numNodes] = 0;
				++numNodes;
				}
			}
		maxDesiredConnectionLengthSquared = (connectionLength * 2) * (connectionLength * 2);
		minDesiredConnectionLengthSquared = (connectionLength * 1) * (connectionLength * 1);
		maxLengthPerturbation = aveElementRadius;
		finishPerturbationX = aveElementRadius/100;
		finishPerturbationY = aveElementRadius/100;

		MiPart connLayer = (connectionLayer == null ? getTarget() : connectionLayer);
		
		for (int i = 0; i < connLayer.getNumberOfParts(); ++i)
			{
			MiPart obj = connLayer.getPart(i);
			if (obj instanceof MiConnection)
				{
				int index;
				MiConnection conn = (MiConnection )obj;
				MiPart node = conn.getSource();
				// Handle cases where a connection goes inside a 'group'/container of nodes
				while (((index = getIndexOfNode(node)) == -1) && (node != null))
					node = node.getContainer(0);
				if (index != -1)
					{
					srcs[numConnections] = index;
					node = conn.getDestination();
					while (((index = getIndexOfNode(node)) == -1) && (node != null))
						node = node.getContainer(0);
					if (index != -1)
						{
						dests[numConnections] = index;
						++numConnections;
						}
					}
				}
			}
		}
	private		int		getIndexOfNode(MiPart obj)
		{
		for (int i = 0; i < numNodes; ++i)
			{
			if (nodes[i] == obj)
				return(i);
			}
		return(-1);
		}
	protected	void		adjust()
		{
		maxDx = 0;
		maxDy = 0;
		for (int i = 0; i < numConnections; ++i)
			{
			MiDistance ddx = x[dests[i]] - x[srcs[i]];
			MiDistance ddy = y[dests[i]] - y[srcs[i]];
			MiDistance length = Math.sqrt(ddx * ddx + ddy * ddy);
			if (length == 0)
				length = 0.5;
			double adjust = (connectionLength - length) / (length * 3);
			MiDistance tx = ddx * adjust;
			MiDistance ty = ddy * adjust;
			dx[srcs[i]] -= tx;
			dy[srcs[i]] -= ty;
			dx[dests[i]] += tx;
			dy[dests[i]] += ty;
			}
		for (int i = 0; i < numNodes; ++i)
			{
			double tx = 0;
			double ty = 0;
			for (int j = 0; j < numNodes; ++j)
				{
				if (i != j)
					{
					double ddx = x[i] - x[j];
					double ddy = y[i] - y[j];
					MiDistance getDistanceSquared = ddx * ddx + ddy * ddy;
					if (getDistanceSquared == 0)
						{
						tx += Math.random();
						ty += Math.random();
						}
					else if (getDistanceSquared < maxDesiredConnectionLengthSquared)
						{
						tx += ddx/getDistanceSquared;
						ty += ddy/getDistanceSquared;
						}
// use this for nodes that have no connections so they don;t just wander off
					else if (getDistanceSquared > maxDesiredConnectionLengthSquared)
						{
						tx -= ddx/getDistanceSquared;
						ty -= ddy/getDistanceSquared;
						}
					}
			    	}
			double correction = tx * tx + ty * ty;
			if (correction > 0)
				{
				correction = Math.sqrt(correction) / 2;
				dx[i] += tx/correction;
				dy[i] += ty/correction;
				}
			}
		for (int i = 0; i < numNodes; ++i)
			{
			x[i] += Math.max(-maxLengthPerturbation, Math.min(maxLengthPerturbation, dx[i]));
			y[i] += Math.max(-maxLengthPerturbation, Math.min(maxLengthPerturbation, dy[i]));

			if (universe != null)
				{
				if (x[i] < universe.xmin)
					x[i] = universe.xmin + widths[i];
				else if (x[i] > universe.xmax)
					x[i] = universe.xmax - widths[i];

				if (y[i] < universe.ymin)
					y[i] = universe.ymin + heights[i];
				else if (y[i] > universe.ymax)
					y[i] = universe.ymax - heights[i];
				}
			dx[i] /= 2;
			dy[i] /= 2;
			maxDx = Math.max(dx[i], maxDx);
			maxDy = Math.max(dy[i], maxDy);
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
		for (int i = 0; i < numNodes - 1; ++i)
			{
			int j = i + 1 + (int )(Math.random() * (numNodes - 1 - i - 1));
			if (i != j)
				addConnection(nodes.elementAt(i), nodes.elementAt(j));
			}
		for (int i = 0; i < numNodes; ++i)
			{
			MiPart srcNode = nodes.elementAt(i);
			if (srcNode.getNumberOfConnections() == 0)
				{
				int j = (int )(Math.random() * (numNodes - 1));
				if (i != j)
					addConnection(nodes.elementAt(i), nodes.elementAt(j));
				}
			MiParts path = new MiParts();
			for (int j = i + 1; j < numNodes; ++j)
				{
				// If there is no path between i and jth nodes then make one
				path.removeAllElements();
				if (!MiUtility.pathExists(srcNode, nodes.elementAt(j), path, false))
					addConnection(srcNode, nodes.elementAt(j));
				}
			}
		}
	}

