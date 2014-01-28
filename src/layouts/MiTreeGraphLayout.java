
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

// To be added: connection styles, 
// To be added: option to squeeze height when layout is horizontal to minimize space
/**----------------------------------------------------------------------------------------------
 * This class arranges the nodes in an associated target container into 
 * a graphical 'tree' (like an organization chart). This supports targets
 * with multiple graphs (i.e. multiple roots), and graphs with nodes that 
 * have multiple parents.
 * <p> 
 * The layout can have either a horizontal or vertical orientation. A
 * horizontal orientation starts with the root nodes on the left and
 * the child nodes fan out to the right. A vertical orientation puts
 * the root nodes at the top and the children fan out downwards. 
 * <p>
 * The nodes can be justfied within their columns (horizontal orientations)
 * or rows (vertical orientations). See MiLayout#setElementHJustification
 * and MiLayout#setElementVJustification.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiTreeGraphLayout extends MiManipulatableLayout implements MiiOrientablePart
	{
	private		int		orientation;
	private		MiBounds[]	rowDimensions;
	private		MiParts[]	rowParts;
	private		MiParts[]	rowPartsParent;
	private		MiParts		roots				= new MiParts();
	private		MiBounds	childBounds			= new MiBounds();
	private		MiBounds	tmpBounds			= new MiBounds();



					/**------------------------------------------------------
	 				 * Constructs a new MiTreeGraphLayout with a horizontal
					 * orientation.
					 *------------------------------------------------------*/
	public				MiTreeGraphLayout()
		{
		this(Mi_HORIZONTAL);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiTreeGraphLayout with the given
					 * orientation.
					 * @param orientation	either Mi_HORIZONTAL or Mi_VERTICAL
					 *------------------------------------------------------*/
	public				MiTreeGraphLayout(int orientation)
		{
		setKeepConnectionsBelowNodes(true);
		this.orientation = orientation;
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
		getTarget().invalidateLayout();
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
		orientation = (orientation == Mi_HORIZONTAL ? Mi_VERTICAL : Mi_HORIZONTAL);
		getTarget().invalidateLayout();
		}
					/**------------------------------------------------------
	 				 * This method implements the actual layout algorithm.
					 * @overrides		MiLayout#doLayout
					 *------------------------------------------------------*/
	protected	void		doLayout()
		{
		// ---------------------------------------------------------------
		// Save the original bounds of the target so we know where to position
		// the tree after it is laid out.
		// ---------------------------------------------------------------
		MiBounds originalTargetBounds = getTarget().getInnerBounds();

		setNodesToPreferredSizes();

		// ---------------------------------------------------------------
		// Get the list of root nodes for all of the trees in the graph
		// ---------------------------------------------------------------
		MiUtility.gatherRootNodes(getTarget(), roots);
		int maxDepth = MiUtility.getMaxDepth(roots);

		// ---------------------------------------------------------------
		// Create the lists that will be used to hold the nodes, node parents
		// and dimensions at each depth.
		// ---------------------------------------------------------------
		rowParts = new MiParts[maxDepth];
		rowPartsParent = new MiParts[maxDepth];
		rowDimensions = new MiBounds[maxDepth];
		for (int i = 0; i < rowParts.length; ++i)
			{
			rowParts[i] = new MiParts();
			rowPartsParent[i] = new MiParts();
			rowDimensions[i] = new MiBounds();
			}

		// ---------------------------------------------------------------
		// Populate the lists created above with the nodes and dimensions
		// ---------------------------------------------------------------
		for (int i = 0; i < roots.size(); ++i)
			preprocessBranch(null, roots.elementAt(i), 0);

		// ---------------------------------------------------------------
		// Set the positions of each of the dimensions saved in the list
		// This will assure that the 'rows' are correctly aligned.
		// ---------------------------------------------------------------
		MiCoord x = getTarget().getXmin() + getInsetMargins().getLeft();
		MiCoord y = getTarget().getYmax() - getInsetMargins().getTop();
		for (int i = 0; i < maxDepth; ++i)
			{
			if (orientation == Mi_VERTICAL)
				{
				rowDimensions[i].setCenterY(y + rowDimensions[i].getHeight()/2);
				y -= rowDimensions[i].getHeight() + getAlleyVSpacing()
					+ preferredEdgeLength;
				}
			else
				{
				rowDimensions[i].setCenterX(x + rowDimensions[i].getWidth()/2);
				x += rowDimensions[i].getWidth() + getAlleyHSpacing()
					+ preferredEdgeLength;
				}
			}
				
		// ---------------------------------------------------------------
		// Place the roots of each of the trees
		// ---------------------------------------------------------------
		placeRoots();

		// ---------------------------------------------------------------
		// Place each of the trees nodes.
		// ---------------------------------------------------------------
		for (int rootNodeIndex = 0; rootNodeIndex < rowParts[0].size(); ++rootNodeIndex)
			{
			placeChildrenUnderParent(rowParts[0].elementAt(rootNodeIndex), 0);
			}

		// ---------------------------------------------------------------
		// Make sure that the children of siblings do not overlap eachother
		// ---------------------------------------------------------------
		shoveChildrenOfParentApartIfRequired();

		// ---------------------------------------------------------------
		// Make sure parents have their children beneath them
		// ---------------------------------------------------------------
		positionChildrenBelowParent();

		// ---------------------------------------------------------------
		// Fixup any problems caused to children by the shoving of their parents
		// ---------------------------------------------------------------
		shoveChildrenOfParentApartIfRequired();

		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		applyJustification();

		// ---------------------------------------------------------------
		// Place the connections as per the requested style.
		// ---------------------------------------------------------------
		fixupConnections();

		// ---------------------------------------------------------------
		// Position all the trees as oringinally specified by the caller's
		// positioning of the target bounds. First get the current bounds
		// of the trees, the find out how much to move them by, and then
		// move them.
		// ---------------------------------------------------------------
		MiDistance tx = 0;
		MiDistance ty = 0;
		MiBounds nodesBounds = new MiBounds();
		for (int i = 0;  i < getTarget().getNumberOfParts(); ++i)
			{
			MiPart part = getTarget().getPart(i);
			if (part.isVisible())
				nodesBounds.union(part.getBounds(tmpBounds));
			}

		if (orientation == Mi_VERTICAL)
			{
			tx = originalTargetBounds.getCenterX() - nodesBounds.getCenterX();
			ty = originalTargetBounds.getYmax() + getInsetMargins().getTop() 
				- nodesBounds.getYmax();
			}
		else
			{
			tx = originalTargetBounds.getXmin() + getInsetMargins().getLeft() 
				- nodesBounds.getXmin();
			ty = originalTargetBounds.getCenterY() - nodesBounds.getCenterY();
			}

		for (int i = 0;  i < getTarget().getNumberOfParts(); ++i)
			{
			MiPart part = getTarget().getPart(i);
			if (part.isVisible())
				part.translate(tx, ty);
			}
		}
					/**------------------------------------------------------
					 * Populate the lists the nodes and dimensions of each row
					 * depth. Do this for the given parent node and all of it's
					 * children.
					 * @param grandParent 	the parent of the given parent
					 * @param parent	the parent who we will put into
					 *			the list of nodes at the given depth
					 * @param depth		the depth of the parent node
					 * @implements		MiiOrientablePart#getOrientation
					 *------------------------------------------------------*/
	protected	void		preprocessBranch(MiPart grandParent, MiPart parent, int depth)
		{
		// ---------------------------------------------------------------
		// Make sure we only enter nodes (i.e. parent) that have not already
		// been enteted into the rowParts array as a child of some other
		// grandparent.
		// ---------------------------------------------------------------
		for (int i = depth; i < rowParts.length; ++i)
			{
			if (rowParts[i].contains(parent))
				break;
			if (i == rowParts.length - 1)
				{
				rowParts[depth].addElement(parent);
				rowPartsParent[depth].addElement(grandParent);
				}
			}
		parent.getBounds(childBounds);
		rowDimensions[depth].accumulateMaxWidthAndHeight(childBounds);

		for (int i = 0; i < parent.getNumberOfConnections(); ++i)
			{
			MiConnection conn = parent.getConnection(i);
			if (conn.isType(edgeConnectionType) && (conn.getSource() == parent))
				{
				MiPart child = conn.getDestination();
				if (child != null)
					{
					preprocessBranch(parent, child, depth + 1);
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Places the root nodes of each tree. The nodes are
					 * correctly placed with respect to their immediate parent
					 * but may overlap the children of a parent's sibling's 
					 * child.
					 * @see			#shoveChildrenOfParentApartIfRequired
					 *------------------------------------------------------*/
	protected	void		placeRoots()
		{
		int numRootNodes = rowParts[0].size();
		if (orientation == Mi_VERTICAL)
			{
			MiCoord x = getTarget().getXmin() + getInsetMargins().getLeft();
			MiCoord y = rowDimensions[0].getCenterY();
			for (int rootNodeIndex = 0; rootNodeIndex < numRootNodes; ++rootNodeIndex)
				{
				MiPart root = rowParts[0].elementAt(rootNodeIndex);
				root.setCenter(x, y);
				x += root.getWidth() + getAlleyHSpacing();
				}
			}
		else
			{
			MiCoord x = rowDimensions[0].getCenterX();
			MiCoord y = getTarget().getYmax() - getInsetMargins().getTop();
			for (int rootNodeIndex = 0; rootNodeIndex < numRootNodes; ++rootNodeIndex)
				{
				MiPart root = rowParts[0].elementAt(rootNodeIndex);
				root.setCenter(x, y);
				y -= root.getHeight() + getAlleyVSpacing();
				}
			}
		}
					/**------------------------------------------------------
					 * Places each branch of nodes in the tree. The nodes are
					 * correctly placed with respect to their immediate parent
					 * but may overlap the children of a parent's sibling's 
					 * child.
					 * @see			#shoveChildrenOfParentApartIfRequired
					 *------------------------------------------------------*/
	protected	void		placeChildrenUnderParent(MiPart parent, int parentDepth)
		{
		int depth = parentDepth + 1;
		if (depth >= rowParts.length)
			return;
		MiDistance 	totalWidth 	= 0;
		MiDistance 	totalHeight 	= 0;
		MiCoord 	x 		= 0;
		MiCoord 	y 		= 0;
		int 		numChildren 	= 0;
		MiDistance 	alleyHSpacing 	= getAlleyHSpacing();
		MiDistance 	alleyVSpacing 	= getAlleyVSpacing();
		for (int i = 0; i < rowParts[depth].size(); ++i)
			{
			if (rowPartsParent[depth].elementAt(i) == parent)
				{
				MiPart child = rowParts[depth].elementAt(i);
				totalWidth += child.getWidth();
				totalHeight += child.getHeight();
				++numChildren;
				}
			}
		if (orientation == Mi_VERTICAL)
			{
			x = parent.getCenterX() - totalWidth/2 - (numChildren - 1) * alleyHSpacing/2;
			y = rowDimensions[depth].getCenterY();
			}
		else
			{
			x = rowDimensions[depth].getCenterX();
			y = parent.getCenterY() + totalHeight/2 + (numChildren - 1) * alleyVSpacing/2;
			}
		for (int i = 0; i < rowParts[depth].size(); ++i)
			{
			if (rowPartsParent[depth].elementAt(i) == parent)
				{
				MiPart child = rowParts[depth].elementAt(i);
				if (orientation == Mi_VERTICAL)
					{
					child.setCenter(x + child.getWidth()/2, y);
					x += alleyHSpacing + child.getWidth();
					}
				else
					{
					child.setCenter(x, y - child.getHeight()/2);
					y -= alleyVSpacing + child.getHeight();
					}
				placeChildrenUnderParent(child, depth);
				}
			}
		}
					/**------------------------------------------------------
					 * Makes sure no children at a given depth overlap each
					 * other and if they do, this moves the children and all
					 * of their children out of the way.
					 *------------------------------------------------------*/
	protected	void		shoveChildrenOfParentApartIfRequired()
		{
		MiDistance tx = 0;
		MiDistance ty = 0;
		MiDistance childrenTx = 0;
		MiDistance childrenTy = 0;
		MiBounds leftTopPartBounds = new MiBounds();
		MiBounds rightBottomPartBounds = new MiBounds();
		for (int depth = rowParts.length - 1; depth >= 0; --depth)
			{
			MiPart leftTopPart = rowParts[depth].elementAt(0);
			leftTopPart.getBounds(leftTopPartBounds);
			for (int i = 1; i < rowParts[depth].size(); ++i)
				{
				MiPart rightBottomPart = rowParts[depth].elementAt(i);
				rightBottomPart.getBounds(rightBottomPartBounds);
				rightBottomPartBounds.addMargins(getAlleyMargins());
				getBoundsOfChildren(rightBottomPart, depth, childBounds);
				boolean centerAboveChildren = false;
				// ---------------------------------------------------------------
				// Calculate the translations required to:
				// 1. Make this current node not overlap the previous one
				// 2. Position this current node directly 'above' it's children
				// Use the maximum of the two translations. If necessary how much
				// we need to translate the children to keep them below the parent.
				// ---------------------------------------------------------------
				if (orientation == Mi_VERTICAL)
					{
					tx = leftTopPartBounds.xmax - rightBottomPartBounds.xmin;
					childrenTx = 0;
					if (!childBounds.isReversed())
						{
						if (tx < childBounds.getCenterX() 
							- rightBottomPartBounds.getCenterX())
							{
					    		tx = childBounds.getCenterX() 
								- rightBottomPartBounds.getCenterX();
							centerAboveChildren = true;
							}
						else
							{
							childrenTx = rightBottomPartBounds.getCenterX() + tx
								- childBounds.getCenterX();
							}
						}
					}
				else
					{
					ty = leftTopPartBounds.ymin - rightBottomPartBounds.ymax;
					childrenTy = 0;
					if (!childBounds.isReversed())
						{
						if (ty > childBounds.getCenterY()
							- rightBottomPartBounds.getCenterY())
							{
					    		ty = childBounds.getCenterY() 
								- rightBottomPartBounds.getCenterY();
							centerAboveChildren = true;
							}
						else
							{
							childrenTy = rightBottomPartBounds.getCenterY() + ty
								- childBounds.getCenterY();
							}
						}
					}
				if ((tx > 0) || (ty < 0))
					{
					rightBottomPart.translate(tx, ty);
					if ((!centerAboveChildren)
						&& ((childrenTx > 0) || (childrenTy < 0)))
						{
						translateChildrenOfParent(
							rightBottomPart, depth, childrenTx, childrenTy);
						}
					rightBottomPartBounds.translate(tx, ty);
					}
				leftTopPartBounds.copy(rightBottomPartBounds);
				leftTopPart = rightBottomPart;
				}
			}
		}
					/**------------------------------------------------------
					 * Makes sure that the children of every node are directly
					 * 'under' their parent.
					 *------------------------------------------------------*/
	protected	void		positionChildrenBelowParent()
		{
		MiDistance tx = 0;
		MiDistance ty = 0;
		MiPoint parentCenterPt = new MiPoint();
		for (int depth = 1; depth < rowParts.length - 1; ++depth)
			{
			for (int i = 0; i < rowParts[depth].size(); ++i)
				{
				MiPart parent = rowPartsParent[depth].elementAt(i);
				parent.getCenter(parentCenterPt);
				getBoundsOfChildren(parent, depth, childBounds);
				if (orientation == Mi_VERTICAL)
					{
					tx = childBounds.getCenterX() - parentCenterPt.x;
					}
				else
					{
					ty = childBounds.getCenterY() - parentCenterPt.y;
					}
				if ((tx > 0) || (ty < 0))
					{
					translateChildrenOfParent(parent, depth, tx, ty);
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Translate all children of the given parent by the given
					 * amounts.
					 * @param parent	the parent
					 * @param parentDepth	the depth of the parent
					 * @param tx		the translation
					 * @param ty		the translation
					 *------------------------------------------------------*/
	protected	void		translateChildrenOfParent(
						MiPart parent, int parentDepth, 
						MiDistance tx, MiDistance ty)
		{
		if (parentDepth + 1 >= rowParts.length)
			return;

		for (int i = 0; i < rowParts[parentDepth + 1].size(); ++i)
			{
			if (rowPartsParent[parentDepth + 1].elementAt(i) == parent)
				{
				MiPart child = rowParts[parentDepth + 1].elementAt(i);
				child.translate(tx, ty);
				translateChildrenOfParent(child, parentDepth + 1, tx, ty);
				}
			}
		}
					/**------------------------------------------------------
					 * Gets the bounds of the first generation of children
					 * of the given parent.
					 * @param parent	the parent
					 * @param parentDepth	the depth of the parent
					 * @param bounds	the (returned) bounds of the children
					 *------------------------------------------------------*/
	protected	void		getBoundsOfChildren(MiPart parent, int parentDepth, MiBounds bounds)
		{
		bounds.reverse();
		if (parentDepth >= rowParts.length - 1)
			return;

		for (int i = 0; i < rowParts[parentDepth + 1].size(); ++i)
			{
			if (rowPartsParent[parentDepth + 1].elementAt(i) == parent)
				{
				bounds.union(rowParts[parentDepth + 1].elementAt(i).getBounds(tmpBounds));
				}
			}
		}
					/**------------------------------------------------------
					 * Applies the current element justifications to all of the
					 * nodes in the graph.
					 *------------------------------------------------------*/
	protected	void		applyJustification()
		{
		int elementHJustification = getElementHJustification();
		int elementVJustification = getElementVJustification();
		for (int depth = 0; depth < rowParts.length; ++depth)
			{
			for (int i = 0; i < rowParts[depth].size(); ++i)
				{
				MiPart part = rowParts[depth].elementAt(i);

				if (orientation == Mi_VERTICAL)
					{
					if (elementVJustification == Mi_BOTTOM_JUSTIFIED)
						{
						part.setYmin(rowDimensions[depth].getYmin());
						}
					else if (elementVJustification == Mi_TOP_JUSTIFIED)
						{
						part.setYmax(rowDimensions[depth].getYmax());
						}
					else 
						{
						part.setCenterY(rowDimensions[depth].getCenterY());
						if (elementVJustification == Mi_JUSTIFIED)
							part.setHeight(rowDimensions[depth].getHeight());
						}
					}
				else
					{
					if (elementHJustification == Mi_LEFT_JUSTIFIED)
						{
						part.setXmin(rowDimensions[depth].getXmin());
						}
					else if (elementHJustification == Mi_RIGHT_JUSTIFIED)
						{
						part.setXmax(rowDimensions[depth].getXmax());
						}
					else 
						{
						part.setCenterX(rowDimensions[depth].getCenterX());
						if (elementHJustification == Mi_JUSTIFIED)
							part.setWidth(rowDimensions[depth].getWidth());
						}
					}
				}
			}
		}

					/**------------------------------------------------------
					 * Applies the given connection style to the connections
					 * in the tree after all of the nodes have been placed.
					 *------------------------------------------------------*/
	protected	void		fixupConnections()
		{
		for (int i = 0; i < getTarget().getNumberOfParts(); ++i)
			{
			if (getTarget().getPart(i) instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )getTarget().getPart(i);
				if (orientation == Mi_HORIZONTAL)
					{
					conn.setSourceConnPt(Mi_RIGHT_LOCATION);
					conn.setDestinationConnPt(Mi_LEFT_LOCATION);
					}
				else
					{
					conn.setSourceConnPt(Mi_BOTTOM_LOCATION);
					conn.setDestinationConnPt(Mi_TOP_LOCATION);
					}
				}
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
		for (int i = 0; i < part.getNumberOfConnections(); ++i)
			{
			if (part.getConnection(i).getSource() != part)
				{
				addConnection(part.getConnection(i).getSource(), node);
				addConnection(node, part);
				part.getConnection(i).deleteSelf();
				return(new MiParts(node));
				}
			}
		addConnection(node, part);
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
			part = getTarget().getPart(getTarget().getNumberOfParts() - 1);
			getTarget().appendPart(node);
			}
		addConnection(part, node);
		return(new MiParts(node));
		}
					/**------------------------------------------------------
					 * Calculates the connection length to use. <NOT USED>
					 *------------------------------------------------------*/
	protected	void		calcSpacing()
		{
		calcElementMaxSizes(null);
		connectionLength = preferredEdgeLength + layoutElementRadius * 2
			+ getCellMargins().getWidth();
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

		if (!getTarget().hasValidLayout())
			getTarget().validateLayout();
		size.setSize(getBounds());
		}
/****
		calcSpacing();
		MiUtility.gatherRootNodes(getTarget(), roots);
		int depth = MiUtility.getMaxDepth(roots);
		int breadth = getNumberOfNodes() - depth + 1; // FIX: calc this
		if (orientation == Mi_HORIZONTAL)
			{
			size.setSize(
				connectionLength * depth + maxElementRadius * 2 + getCellMargins().getWidth(),
				connectionLength * breadth + maxElementRadius * 2 + getCellMargins().getHeight());
			}
		else
			{
			size.setSize(
				connectionLength * breadth + maxElementRadius * 2 + getCellMargins().getWidth(),
				connectionLength * depth + maxElementRadius * 2 + getCellMargins().getHeight());
			}
		}
*****/
	}

