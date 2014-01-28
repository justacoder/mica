
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
public abstract class MiManipulatableLayout extends MiLayout implements MiiActionHandler, MiiManipulatableLayout
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;
	protected	String		edgeConnectionType;
	private		boolean		layoutPartsPartRelations;
	protected	MiDistance	preferredEdgeLength		= 6;
	protected	MiDistance	connectionLength;
	protected	MiDistance	xConnectionLength;
	protected	MiDistance	yConnectionLength;
	private		boolean		layoutElementsBasedOnAverageElementSize;
	private		boolean		layoutElementsBasedOnSizeOfElementRadii	= true;
	protected	MiDistance	layoutElementRadius;
	protected	MiDistance	layoutElementWidth;
	protected	MiDistance	layoutElementHeight;
	protected	MiDistance	aveElementRadius;
	protected	MiDistance	maxElementRadius;
	protected	MiDistance	maxElementWidth;
	protected	MiDistance	maxElementHeight;
	private		int		minNumberOfTargetNodes 			= 6;
	private		boolean		manipulatable;
	protected	MiiAction	dragAndDropActionEvent;
	protected	MiConnection	prototypeEdge;
	protected	MiPart		prototypePlaceHolder;
	private		MiiLayoutManipulator layoutManipulator;
	private		MiBounds	tmpBounds;
	private		MiSize		tmpSize					= new MiSize();
	private static	MiiSelectionGraphics selectionGraphics			= new MiBoxSelectionGraphics();



	public				MiManipulatableLayout()
		{
		this(true, true);
		}
	public				MiManipulatableLayout(boolean manipulatable, boolean connectable)
		{
		if (!manipulatable)
			return;

		this.manipulatable = true;

		tmpBounds = new MiBounds();
		dragAndDropActionEvent = new MiAction(this, Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);
		setInsetMargins(4);
		setIsDragAndDropTarget(true);
		appendActionHandler(dragAndDropActionEvent);

		if (connectable)
			{
			setKeepConnectionsBelowNodes(true);
			MiConnection conn = new MiConnection();
			conn.setLineWidth(2);
			//conn.setSelectable(false);
			conn.setBackgroundColor(MiColorManager.black);
			setPrototypeEdge(conn);
			}

		MiPlaceHolder placeHolder = new MiPlaceHolder(makePlaceHolderShape());
		placeHolder.setIsDragAndDropTarget(true);
		//placeHolder.setSelectable(false);
		placeHolder.setBounds(new MiBounds(0,0,14,14));
		placeHolder.setBackgroundColor(MiColorManager.darkWhite);
		setPrototypePlaceHolder(placeHolder);
		setSelectionGraphics(selectionGraphics);
		}
	public static	void		setDefaultSelectionGraphics(MiiSelectionGraphics sg)
		{
		selectionGraphics = sg;
		}
	public static	MiiSelectionGraphics	getDefaultSelectionGraphics()
		{
		return(selectionGraphics);
		}
	protected	MiPart		makePlaceHolderShape()
		{
		return(new MiCircle());
		}
	public abstract	void		formatTarget(String edgeConnectionType);

	public		void		setTarget(MiPart c)
		{
		super.setTarget(c);
		if (c != null)
			{
			// Only for graph layouts like star graph etc...
			((MiContainer )c).setKeepConnectionsBelowNodes(getKeepConnectionsBelowNodes());
			}
		}
					/**------------------------------------------------------
					 * Gets the graphics to be used to highlight this MiPart
					 * when it is selected. If this is not null (the default)
					 * it overrides the selection graphics as specified in the
					 * MiSelectionManager.
					 * @return		the selection graphics
					 * @overrides		MiPart#getSelectionGraphics
					 *------------------------------------------------------*/
/* removed this 7-11-2003 because sometimes we want to override this w/ a different color, for example, using an editor-wide selection graphics
	public		MiiSelectionGraphics	getSelectionGraphics()
		{
		return(new MiBoxSelectionGraphics());
		}
*/

					/**------------------------------------------------------
	 				 * Sets this layout's manipulator to use whenever a layout
					 * manipulator is needed.
	 				 * @param manipulator	the layout manipulator or null
					 * @see			MiPart#getLayoutManipulator
					 * @see			#getLayoutManipulator
					 * @see			#makeLayoutManipulator
					 *------------------------------------------------------*/
	public		void		setLayoutManipulator(MiiLayoutManipulator manipulator)
		{
		layoutManipulator = manipulator;
		}
					/**------------------------------------------------------
	 				 * Gets this MiPart's layout's manipulator, if any, that 
					 * has been assigned to this MiPart. 
	 				 * @return 		the layout manipulator or null
					 * @overrides		MiPart#getLayoutManipulator
					 * @see			#setLayoutManipulator
					 *------------------------------------------------------*/
	public		MiiLayoutManipulator getLayoutManipulator()
		{
		if (layoutManipulator != null)
			return(layoutManipulator);
		return(super.getLayoutManipulator());
		}
					/**------------------------------------------------------
		 			 * Gets an instance of MiiLayoutManipulator that is suitable
					 * for the end-user to interactively manipulate this layout.
					 * @return		the manipulator
					 * @see			MiiLayoutManipulator
					 * @overrides		MiPart#makeLayoutManipulator
					 * @see			#getLayoutManipulator
					 * @see			#setLayoutManipulator
					 * @implements		MiiManipulatableLayout#makeLayoutManipulator
					 *------------------------------------------------------*/
	public	MiiLayoutManipulator	makeLayoutManipulator()
		{
		if (layoutManipulator != null)
			return(layoutManipulator);
		return(new MiLayoutManipulator((MiiManipulatableLayout )this));
		}
					/**------------------------------------------------------
		 			 * Sets the prototype connection that will be copied when
					 * new edges are added to this graph layout.
					 * @param edge 		the prototype connection
					 * @see			#setEdgeConnectionType
					 * @see			#getPrototypeEdge
					 * @implements		MiiManipulatableLayout#setPrototypeEdge
					 *------------------------------------------------------*/
	public		void		setPrototypeEdge(MiConnection conn)
		{
		prototypeEdge = conn;
		prototypeEdge.setType(edgeConnectionType);
		}
					/**------------------------------------------------------
		 			 * Gets the prototype connection that will be copied when
					 * new edges are added to this graph layout.
					 * @returns 		the prototype connection
					 * @see			#setPrototypeEdge
					 * @implements		MiiManipulatableLayout#getPrototypeEdge
					 *------------------------------------------------------*/
	public		MiConnection	getPrototypeEdge()
		{
		return(prototypeEdge);
		}

	public		void		setPreferredEdgeLength(MiDistance length)
		{
		preferredEdgeLength = length;
		}
	public		MiDistance	getPreferredEdgeLength()
		{
		return(preferredEdgeLength);
		}
					/**------------------------------------------------------
	 				 * Sets the prototype place holder that will be copied when
					 * new place holders are added to this graph layout.
					 * @param edge 		the prototype place holder
					 * @see			#getPrototypePlaceHolder
					 * @implements		MiiManipulatableLayout#setPrototypePlaceHolder
					 *------------------------------------------------------*/
	public		void		setPrototypePlaceHolder(MiPart obj)
		{
		prototypePlaceHolder = obj;
		}
					/**------------------------------------------------------
		 			 * Gets the prototype place holder that will be copied when
					 * new edges are added to this graph layout.
					 * @returns 		the prototype place holder
					 * @see			#setPrototypePlaceHolder
					 * @implements		MiiManipulatableLayout#getPrototypePlaceHolder
					 *------------------------------------------------------*/
	public		MiPart		getPrototypePlaceHolder()
		{
		return(prototypePlaceHolder);
		}

					/**------------------------------------------------------
		 			 * Sets the type of connection that will be considered an
					 * edge in the graph layout. Nodes in the layout may have
					 * many connections, some of which should not be considered
					 * part of this graph. These connections are differentiated
					 * by their connection type.
					 * @param connectionType the type of the connections that
					 *			are edges in this graph layout
					 * @see			MiConnection#isType
					 * @see			MiConnection#getType
					 * @see			MiConnection#setType
					 * @implements		MiiManipulatableLayout#setEdgeConnectionType
					 *------------------------------------------------------*/
	public		void		setEdgeConnectionType(String type)
		{
		edgeConnectionType = type;
		if (prototypeEdge != null)
			prototypeEdge.setType(type);
		}
					/**------------------------------------------------------
	 				 * Gets the type of connection that will be considered an
					 * edge in the graph layout.
					 * @returns 		the type of the connections that
					 *			are edges in this graph layout
					 * @see			#setEdgeConnectionType
					 * @implements		MiiManipulatableLayout#getEdgeConnectionType
					 *------------------------------------------------------*/
	public		String		getEdgeConnectionType()
		{
		return(edgeConnectionType);
		}

	public		void		setMinimumNumberOfTargetNodes(int number)
		{
		minNumberOfTargetNodes = number;
		}
	public		int		getMinimumNumberOfTargetNodes()
		{
		return(minNumberOfTargetNodes);
		}
	public		void		setLayoutElementsBasedOnAverageElementSize(boolean flag)
		{
		layoutElementsBasedOnAverageElementSize = flag;
		}
	public		boolean		getLayoutElementsBasedOnAverageElementSize()
		{
		return(layoutElementsBasedOnAverageElementSize);
		}
	public		void		setLayoutElementsBasedOnSizeOfElementRadii(boolean flag)
		{
		layoutElementsBasedOnSizeOfElementRadii = flag;
		}
	public		boolean		getLayoutElementsBasedOnSizeOfElementRadii()
		{
		return(layoutElementsBasedOnSizeOfElementRadii);
		}

	public		void		setLayoutContainerPartRelations(boolean flag)
		{
		layoutPartsPartRelations = flag;
		}
	public		boolean		getLayoutContainerPartRelations()
		{
		return(layoutPartsPartRelations);
		}

	public	String[] 		getSupportedImportFormats()
		{
		String[] formats = new String[1];
		formats[0] = Mi_MiPART_FORMAT;
		return(formats);
		}

	public static 	boolean		attachManipulatorToTarget(MiPart target)
		{
		if (target.getAttachment("layoutManipulator") != null)
			return(false);

		MiiLayoutManipulator manipulator = target.makeLayoutManipulator();
		if (manipulator != null)
			{
			manipulator.attachToTarget("layoutManipulator");
			return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
		 			 * Gets the number of nodes in this graph layout.
					 * @return  		the number of nodes
					 * @see			#getNode
					 * @see			#getNodes
					 * @implements		MiiManipulatableLayout#getNumberOfNodes
					 *------------------------------------------------------*/
	public		int		getNumberOfNodes()
		{
		int num = 0;
		MiPart target = getTarget();
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			{
			if (!(target.getPart(i) instanceof MiConnection))
				++num;
			}
		return(num);
		}
					/**------------------------------------------------------
		 			 * Gets the node at the given index.
					 * @return  		the node or null
					 * @see			#getNumberOfNodes
					 * @see			#getNodes
					 * @implements		MiiManipulatableLayout#getNode
					 *------------------------------------------------------*/
	public		MiPart		getNode(int index)
		{
		int num = 0;
		MiPart target = getTarget();
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			{
			if (!(target.getPart(i) instanceof MiConnection))
				{
				if (num == index)
					{
					return(target.getPart(i));
					}
				++num;
				}
			}
		return(null);
		}
					/**------------------------------------------------------
		 			 * Gets the nodes of this graph layout.
					 * @return  		the nodes
					 * @see			#getNumberOfNodes
					 * @see			#getNode
					 * @implements		MiiManipulatableLayout#getNodes
					 *------------------------------------------------------*/
	public 		MiParts		getNodes(MiParts cells)
		{
		getNodesInTarget(getTarget(), cells);
		return(cells);
		}
					/**------------------------------------------------------
		 			 * Gets the bounds of the node at the given index which is
					 * the outer bounds of the node plus it's cell margins.
					 * @param index 	the index of the node to get the
					 *			'cell bounds' of.
					 * @param b  		the (returned) bounds
					 * @see			#getNode
					 * @implements		MiiManipulatableLayout#getNodeBounds
					 *------------------------------------------------------*/
	public		void		getNodeBounds(int index, MiBounds b)
		{
		MiPart node = getNode(index);
		node.getBounds(b);
		b.addMargins(getCellMargins());
		}
					/**------------------------------------------------------
					 * Gets whether the orientation can be changed.
					 * @return		true if the orientation cannot be
					 *			changed
					 * @implements		MiiOrientablePart#isOrientationFixed
					 *------------------------------------------------------*/
	public		boolean		isOrientationFixed()
		{
		return(true);
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
		return(Mi_HORIZONTAL);
		}
					/**------------------------------------------------------
					 * Changes to the next supported orientation.
					 * @implements		MiiOrientablePart#cycleOrientation
					 *------------------------------------------------------*/
	public		void		cycleOrientation()
		{
		}
					/**------------------------------------------------------
					 * Inserts the given node at the given index. 
					 * @param node		the node to insert
					 * @param index		the index of the node that the
					 *			given node will be inserted before
					 * @return		the list of nodes that were inserted
					 *			(the given node and any placeholders
					 *			that may have to be added).
					 *------------------------------------------------------*/
	public 		MiParts		insertNode(MiPart node, int index)
		{
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
					 *------------------------------------------------------*/
	public 		MiParts		appendNode(MiPart node, int index)
		{
		getTarget().appendPart(node);
		return(new MiParts(node));
		}
					/**------------------------------------------------------
					 * Deletes the node at the given index. If the node is
					 * not a MiPlaceHolder then it is replaced by one. In any
					 * case the node is deleted.
					 * @param index		the index of the node to delete
					 * @implements		MiiManipulatableLayout#deleteNode
					 *------------------------------------------------------*/
	public		void		deleteNode(int index)
		{
		if (index == -1)
			index = getNumberOfNodes() - 1;
		MiPart node = getNode(index);
		if (!(node instanceof MiPlaceHolder))
			node.replaceSelf(makePlaceHolder());
		node.deleteSelf();
		}
					/**------------------------------------------------------
					 * Removes the node at the given index. If the node is
					 * not a MiPlaceHolder then it is replaced by one. If the
					 * node is a MiPlaceHolder then it is deleted.
					 * @param index		the index of the node to remove
					 * @return 		the orientation
					 * @implements		MiiManipulatableLayout#removeNode
					 *------------------------------------------------------*/
	public		void		removeNode(int index)
		{
		MiPart node = getNode(index);
		if (!(node instanceof MiPlaceHolder))
			node.replaceSelf(makePlaceHolder());
		else
			node.deleteSelf();
		}
					/**------------------------------------------------------
		 			 * Gets the number of edges in this graph layout.
					 * @return  		the number of edges
					 * @see			#getEdge
					 * @implements		MiiManipulatableLayout#getNumberOfEdges
					 *------------------------------------------------------*/
	public		int		getNumberOfEdges()
		{
		int num = 0;
		MiPart target = getTarget();
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			{
			if ((target.getPart(i) instanceof MiConnection) 
				&& ((edgeConnectionType == null) 
				|| (((MiConnection )target.getPart(i)).isType(edgeConnectionType))))
				{
				++num;
				}
			}
		return(num);
		}
					/**------------------------------------------------------
		 			 * Gets the edge at the given index.
					 * @return  		the edge or null
					 * @see			#getNumberOfEdges
					 * @implements		MiiManipulatableLayout#getEdge
					 *------------------------------------------------------*/
	public		MiConnection	getEdge(int index)
		{
		int num = 0;
		MiPart target = getTarget();
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			{
			if ((target.getPart(i) instanceof MiConnection) 
				&& ((edgeConnectionType == null) 
				|| (((MiConnection )target.getPart(i)).isType(edgeConnectionType))))
				{
				if (num == index)
					return((MiConnection )target.getPart(i));
				++num;
				}
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Makes an edge (connection) for this layout. The registered
					 * prototype edge is copied to make a new edge. If no
					 * prototype edge has been registered then a generic
					 * MiConnection is created of the correct connectionType.
					 * @returns		a new connection
					 * @see			#setPrototypeEdge
					 * @see			#getPrototypeEdge
					 * @see			#setEdgeConnectionType
					 * @implements		MiiManipulatableLayout#makeEdge
					 *------------------------------------------------------*/
	public		MiConnection	makeEdge()
		{
		MiConnection conn;
		if (prototypeEdge != null)
			{
			conn = (MiConnection )prototypeEdge.copy();
			}
		else
			{
			conn = new MiConnection();
			conn.setType(edgeConnectionType);
			}
		return(conn);
		}
	protected	void		addConnection(MiPart prevNode, MiPart node)
		{
		MiConnection conn = makeEdge();
		conn.setSource(prevNode);
		conn.setDestination(node);
		getTarget().appendPart(conn);
		}
					/**------------------------------------------------------
		 			 * Makes an place holder (node) for this layout. The
					 * registered prototype place holder is copied to make a 
					 * new node. If no prototype place holder has been 
					 * registered then a generic MiPlaceHolder is created.
					 * @returns		a new place holder node
					 * @see			#setPrototypePlaceHolder
					 * @see			#getPrototypePlaceHolder
					 * @implements		MiiManipulatableLayout#makePlaceHolder
					 *------------------------------------------------------*/
	public		MiPart		makePlaceHolder()
		{
		MiPart placeHolder;
		if (prototypePlaceHolder != null)
			placeHolder = prototypePlaceHolder.copy();
		else
			placeHolder = new MiPlaceHolder();
		placeHolder.appendActionHandler(dragAndDropActionEvent);
		return(placeHolder);
		}
					/**------------------------------------------------------
					 * Gets the node at the given relative location to the node
					 * at the given index. Valid relative locations are:
					 *   Mi_ABOVE
					 *   Mi_BELOW
					 *   Mi_TO_RIGHT
					 *   Mi_TO_LEFT
					 *   Mi_TOP
					 *   Mi_BOTTOM
					 *   Mi_FAR_RIGHT
					 *   Mi_FAR_LEFT
					 * @param index		the index of the node that the
					 *			given node will be relative to
					 * @param relativeLocation the relative location of the
					 *			node to get
					 * @return		the node or null
					 * @implements		MiiManipulatableLayout#getNeighboringNode
					 *------------------------------------------------------*/
	public		int		getNeighboringNode(int index, int relativeLocation)
		{
		MiPart part = getNode(index);
		int numCells = getNumberOfNodes();
		MiDistance smallestDist = Mi_MAX_COORD_VALUE;
		int resultIndex = -1;
		switch (relativeLocation)
		    {
		    case Mi_ABOVE:
			{
			for (int i = 0; i < numCells; ++i)
				{
				getNode(i).getBounds(tmpBounds);
				MiDistance diff = tmpBounds.getCenterY() - part.getCenterY();
				MiDistance dist = tmpBounds.getCenter().getDistanceSquared(part.getCenter());
				if ((i != index) && (diff > 1) && (dist <= smallestDist))
					{
					smallestDist = dist;
					resultIndex = i;
					}
				}
			return(resultIndex);
			}
		    case Mi_BELOW:
			{
			for (int i = 0; i < numCells; ++i)
				{
				getNode(i).getBounds(tmpBounds);
				MiDistance diff = tmpBounds.getCenterY() - part.getCenterY();
				MiDistance dist = tmpBounds.getCenter().getDistanceSquared(part.getCenter());
				if ((i != index) && (diff < -1) && (dist <= smallestDist))
					{
					smallestDist = dist;
					resultIndex = i;
					}
				}
			return(resultIndex);
			}
		    case Mi_TO_LEFT:
			{
			for (int i = 0; i < numCells; ++i)
				{
				getNode(i).getBounds(tmpBounds);
				MiDistance diff = tmpBounds.getCenterX() - part.getCenterX();
				MiDistance dist = tmpBounds.getCenter().getDistanceSquared(part.getCenter());
				if ((i != index) && (diff < -1) && (dist <= smallestDist))
					{
					smallestDist = dist;
					resultIndex = i;
					}
				}
			return(resultIndex);
			}
		    case Mi_TO_RIGHT:
			{
			for (int i = 0; i < numCells; ++i)
				{
				getNode(i).getBounds(tmpBounds);
				MiDistance diff = tmpBounds.getCenterX() - part.getCenterX();
				MiDistance dist = tmpBounds.getCenter().getDistanceSquared(part.getCenter());
				if ((i != index) && (diff > 1) && (dist <= smallestDist))
					{
					smallestDist = dist;
					resultIndex = i;
					}
				}
			return(resultIndex);
			}
		    case Mi_TOP:
			{
			MiCoord currentY = MiBounds.Mi_MIN_COORD_VALUE;
			for (int i = 0; i < numCells; ++i)
				{
				part = getNode(i);
				if (part.getCenterY() > currentY)
					{
					currentY = part.getCenterY();
					resultIndex = i;
					}
				}
			return(resultIndex == index ? -1 : resultIndex);
			}
		    case Mi_BOTTOM:
			{
			MiCoord currentY = MiBounds.Mi_MAX_COORD_VALUE;
			for (int i = 0; i < numCells; ++i)
				{
				part = getNode(i);
				if (part.getCenterY() < currentY)
					{
					currentY = part.getCenterY();
					resultIndex = i;
					}
				}
			return(resultIndex == index ? -1 : resultIndex);
			}
		    case Mi_FAR_LEFT:
			{
			MiCoord currentX = MiBounds.Mi_MAX_COORD_VALUE;
			for (int i = 0; i < numCells; ++i)
				{
				part = getNode(i);
				if (part.getCenterX() < currentX)
					{
					currentX = part.getCenterX();
					resultIndex = i;
					}
				}
			return(resultIndex == index ? -1 : resultIndex);
			}
		    case Mi_FAR_RIGHT:
			{
			MiCoord currentX = MiBounds.Mi_MIN_COORD_VALUE;
			for (int i = 0; i < numCells; ++i)
				{
				part = getNode(i);
				if (part.getCenterX() > currentX)
					{
					currentX = part.getCenterX();
					resultIndex = i;
					}
				}
			return(resultIndex == index ? -1 : resultIndex);
			}
		    default:
			throw new IllegalArgumentException(this + ": Location parameter not valid: " + relativeLocation);
		    }
		}
					/**------------------------------------------------------
					 * Inserts the given node at the given relative location
					 * to the node at the given index. Valid relative locations
					 * are:
					 *   Mi_ABOVE
					 *   Mi_BELOW
					 *   Mi_TO_RIGHT
					 *   Mi_TO_LEFT
					 *   Mi_TOP
					 *   Mi_BOTTOM
					 *   Mi_FAR_RIGHT
					 *   Mi_FAR_LEFT
					 * @param node		the node to insert
					 * @param index		the index of the node that the
					 *			given node will be inserted relative
					 *			to (-1 indicates that this should append 
					 *			the given node after the last node)
					 * @param relativeLocation the relative location of the
					 *			insertion point
					 * @return		the list of nodes that were inserted
					 *			(the given node and any placeholders
					 *			that may have to be added).
					 * @implements		MiiManipulatableLayout#insertNeighboringNode
					 *------------------------------------------------------*/
	public		MiParts		insertNeighboringNode(MiPart node, int index, int relativeLocation)
		{
		if (index == -1)
			return(appendNode(node, getNumberOfNodes() - 1));
		return(insertNode(node, index));
		}
					/**------------------------------------------------------
					 * Appends the given node at the given relative location
					 * to the node at the given index. Valid relative locations
					 * are:
					 *   Mi_ABOVE
					 *   Mi_BELOW
					 *   Mi_TO_RIGHT
					 *   Mi_TO_LEFT
					 *   Mi_TOP
					 *   Mi_BOTTOM
					 *   Mi_FAR_RIGHT
					 *   Mi_FAR_LEFT
					 * @param node		the node to append
					 * @param index		the index of the node that the
					 *			given node will be appended relative
					 *			to
					 * @param relativeLocation the relative location of the
					 *			append point
					 * @return		the list of nodes that were appended
					 *			(the given node and any placeholders
					 *			that may have to be added).
					 * @implements		MiiManipulatableLayout#appendNeighboringNode
				 	 *------------------------------------------------------*/
	public		MiParts		appendNeighboringNode(MiPart node, int index, int relativeLocation)
		{
		if (index == -1)
			return(appendNode(node, getNumberOfNodes() - 1));
		return(appendNode(node, index));
		}



					/**------------------------------------------------------
		 			 * Creates any nodes need to have enough to make the layout
					 * recognizable, removes any edges currently existing between
					 * the nodes, and then creates edges that correspond to this
					 * type of layout. This method is only valid if the layout
					 * was constructed as a manipulatable layout. This is because
					 * placeHolders have no meaning unless they are manipulatable.
					 * @implements		MiiManipulatableLayout#formatEmptyTarget
					 *------------------------------------------------------*/
	public		void		formatEmptyTarget()
		{
		if (!manipulatable)
			{
			throw new IllegalArgumentException(
				"MICA: this method, formatEmptyTarget(),  should not be called for: " + this
				+ "\nIt was not constructed as a manipulatable layout");
			}

		MiPart target = getTarget();
		for (int i = target.getNumberOfParts(); i < minNumberOfTargetNodes; ++i)
			{
			MiPart placeHolder = makePlaceHolder();
			target.appendPart(placeHolder);
			}
		formatTarget(edgeConnectionType);
		}
	public		boolean		processAction(MiiAction action)
		{
		// Watch for drops on this layout 
		if ((action.hasActionType(Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE))
			// ...and drops on parts that are forwarded here...
			|| (action.hasActionType(
			Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
			{
			MiDataTransferOperation transferOp = (MiDataTransferOperation )action.getActionSystemInfo();
			MiPart obj = (MiPart )transferOp.getData();
			MiPart placeHolder;
			obj.setCenter(transferOp.getLookTargetPosition());
			if (transferOp.getTarget() == getTarget())
				{
				placeHolder = insertNode(makePlaceHolder(), 
						transferOp.getLookTargetPosition()).elementAt(0);
				}
			else if (transferOp.getTarget() instanceof MiPlaceHolder)
				{
				placeHolder = (MiPlaceHolder )transferOp.getTarget();
				}
			else
				{
				int status = transferOp.getTarget().dispatchAction(
					Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE, transferOp);
				if (status != Mi_PROPOGATE)
					return(false);
				placeHolder = insertNode(makePlaceHolder(),
						transferOp.getLookTargetPosition()).elementAt(0);
				}
			// Replace placeHolder with source
			MiReplacePartsCommand cmd = new MiReplacePartsCommand(
				getTarget().getContainingEditor(), 
				new MiParts(placeHolder), new MiParts(obj));
			MiSystem.getTransactionManager().appendTransaction(cmd);

			placeHolder.replaceSelf(obj);
			placeHolder.deleteSelf();
			return(false);
			}
		return(true);
		}
					/**------------------------------------------------------
					 * Inserts the given node at the given location. 
					 * @param node		the node to insert
					 * @param location	the location of the node that the
					 *			given node will be inserted before
					 * @return		the list of nodes that were inserted
					 *			(the given node and any placeholders
					 *			that may have to be added).
					 *------------------------------------------------------*/
	public		MiParts		insertNode(MiPart node, MiPoint location)
		{
		int 		numCells 	= getNumberOfNodes();
		MiBounds 	bounds		= new MiBounds();
		int		minIndex 	= -1;
		MiDistance	minDistSquared 	= Mi_MAX_COORD_VALUE;

		for (int i = 0; i < numCells; ++i)
			{
			getNodeBounds(i, bounds);
			if (bounds.intersects(location))
				{
				return(insertNeighboringNode(node, i, Mi_ABOVE));
				}
			else
				{
				MiDistance getDistanceSquared = location.getDistanceSquared(bounds.getCenter());
				if (getDistanceSquared < minDistSquared)
					{
					minDistSquared = getDistanceSquared;
					minIndex = i;
					}
				}
			}
		return(insertNeighboringNode(node, minIndex, Mi_ABOVE));
		}

					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_CONNECTION_ID_NAME))
			setEdgeConnectionType(value);
		else
			super.setPropertyValue(name, value);
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		if (name.equalsIgnoreCase(Mi_CONNECTION_ID_NAME))
			return(getEdgeConnectionType());
		return(super.getPropertyValue(name));
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = new MiPropertyDescriptions("MiManipulatableLayout", super.getPropertyDescriptions());

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_CONNECTION_ID_NAME, Mi_STRING_TYPE));
		if (manipulatable)
			propertyDescriptions.elementAt(Mi_INSET_MARGINS_NAME).setDefaultValue("4");
		return(propertyDescriptions);
		}
	protected	void		calcElementMaxSizes(MiParts list)
		{
		int numInLayout;
		MiSize size 		= new MiSize();
		MiSize maxSize 		= new MiSize();
		MiDistance totalWidth 	= 0;
		MiDistance totalHeight 	= 0;
		int totalNumNodes 	= 0;
		MiPart target 		= getTarget();

		if (list != null)
			numInLayout = list.size();
		else
			numInLayout = target.getNumberOfParts();
		for (int i = 0; i < numInLayout; ++i)
			{
			if (list != null)
				{
				list.elementAt(i).getPreferredSize(size);
				maxSize.accumulateMaxWidthAndHeight(size);
				totalWidth += size.width;
				totalHeight += size.height;
				++totalNumNodes;
				}
			else if (!(target.getPart(i) instanceof MiConnection))
				{
				target.getPart(i).getPreferredSize(size);
				maxSize.accumulateMaxWidthAndHeight(size);
				totalWidth += size.width;
				totalHeight += size.height;
				++totalNumNodes;
				}
			}
		maxElementWidth = maxSize.getWidth();
		maxElementHeight = maxSize.getHeight();
		maxElementRadius = Math.sqrt( 
			maxElementWidth * maxElementWidth + maxElementHeight * maxElementHeight)/2;
		aveElementRadius = Math.sqrt( 
			totalWidth * totalWidth + totalHeight * totalHeight)/(2 * totalNumNodes);

		if (layoutElementsBasedOnAverageElementSize)
			{
			layoutElementRadius = aveElementRadius;
			layoutElementWidth = totalWidth / totalNumNodes;
			layoutElementHeight = totalHeight / totalNumNodes;
			}
		else
			{
			layoutElementRadius = maxElementRadius;
			layoutElementWidth = maxElementWidth;
			layoutElementHeight = maxElementHeight;
			}
		}

	protected	void		setNodesToPreferredSizes()
		{
		MiPart target = getTarget();
		int num = target.getNumberOfParts();
		for (int i = 0; i < num; ++i)
			{
			MiPart part = target.getPart(i);
			if (!(part instanceof MiConnection))
				part.setSize(part.getPreferredSize(tmpSize));
			}
		}
	protected	MiConnection	getConnection(MiPart one, MiPart other)
		{
		for (int i = 0; i < one.getNumberOfConnections(); ++i)
			{
			MiConnection conn = one.getConnection(i);
			if ((conn.getOther(one) == other) && (conn.isType(edgeConnectionType)))
				{
				return(conn);
				}
			}
		return(null);
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @param source	the part to copy
					 * @overrides 		MiLayout#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiManipulatableLayout obj = (MiManipulatableLayout )source;

		setPreferredEdgeLength(obj.preferredEdgeLength);
		setEdgeConnectionType(obj.edgeConnectionType);
		setLayoutContainerPartRelations(obj.layoutPartsPartRelations);
		connectionLength = obj.connectionLength;
		maxElementRadius = obj.maxElementRadius;
		maxElementWidth = obj.maxElementWidth;
		maxElementHeight = obj.maxElementHeight;

		manipulatable = obj.manipulatable;

		if (obj.prototypeEdge != null)
			prototypeEdge = (MiConnection )obj.prototypeEdge.copy();
		else
			prototypeEdge = null;

		if (obj.prototypePlaceHolder != null)
			prototypePlaceHolder = (MiPlaceHolder )obj.prototypePlaceHolder.copy();
		else
			prototypePlaceHolder = null;


		if (manipulatable)
			{
			dragAndDropActionEvent = new MiAction(this, Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);
			appendActionHandler(dragAndDropActionEvent);
			}
		}
					/**------------------------------------------------------
	 				 * Makes and returns a copy of this MiPart and all of it's
					 * parts.
	 				 * @return 	 	the copy
					 * @see			#copy
					 * @overrides		MiContainer#deepCopy
					 *------------------------------------------------------*/
	public		MiPart		deepCopy()
		{
		MiManipulatableLayout copy = (MiManipulatableLayout )super.deepCopy();
		for (int i = 0; i < copy.getNumberOfParts(); ++i)
			{
			MiPart part = copy.getPart(i);
			for (int j = 0; j < part.getNumberOfActionHandlers(); ++j)
				{
				if (part.getActionHandler(j).getActionHandler() == this)
					part.getActionHandler(j).setActionHandler(copy);
				}
			}
		return(copy);
		}

	protected	void		calcPreferredSize(MiSize size)
		{
		calcMinimumSize(size);
		}
	protected	MiParts		getChildrenToBeLayedOut(MiPart root)
		{
		MiParts children = new MiParts();
		if (!layoutPartsPartRelations)
			{
			for (int i = 0; i < root.getNumberOfConnections(); ++i)
				{
				MiConnection conn = root.getConnection(i);
				if ((conn.getSource() == root) && (conn.getDestination() != null)
					&& ((edgeConnectionType == null) || (conn.isType(edgeConnectionType))))
					{
					children.addElement(conn.getDestination());
					}
				}
			}
		return(children);
		}
					/**------------------------------------------------------
	 				 * Scales the nodes that are part of this layout.
					 * @param center	the center of the scaling
					 * @param scale		the scale factors
					 * @overrides		MiContainer#scaleParts
					 *------------------------------------------------------*/
	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		super.scaleParts(center, scale);
		if (!layoutPartsPartRelations)
			{
			MiParts children = getChildrenToBeLayedOut(getTarget());
			for (int i = 0; i < children.size(); ++i)
				{
				children.elementAt(i).scale(center, scale);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Translates the nodes that are part of this layout.
					 * @param x		the x distance to translate
					 * @param y		the y distance to translate
					 * @overrides		MiContainer#translateParts
					 *------------------------------------------------------*/
	protected	void		translateParts(MiDistance x, MiDistance y)
		{
		super.translateParts(x, y);
		if (!layoutPartsPartRelations)
			{
			MiParts children = getChildrenToBeLayedOut(getTarget());
			for (int i = 0; i < children.size(); ++i)
				{
				children.elementAt(i).translate(x, y);
				}
			}
		}
					/**------------------------------------------------------
					 * Realculates the outer bounds of this MiPart. Override 
					 * this, if desired, as it implements the core 
					 * functionality. This implementation returns the
					 * recalculated bounds of the target plus the cell margins.
					 * @param newBounds	the (returned) outer bounds
					 * @overrides		MiContainer#reCalcBounds
					 *------------------------------------------------------*/
	protected	void		reCalcBounds(MiBounds b)
		{
		if (this == getTarget())
			super.reCalcBounds(b);
		else
			getTarget().reCalcBounds(b);
		b.addMargins(getCellMargins());
		}

	public static	MiParts		getNodesInTarget(MiPart target, MiParts nodes)
		{
		int numInTarget = target.getNumberOfParts();
		for (int i = 0; i < numInTarget; ++i)
			{
			MiPart obj = target.getPart(i);
			if (!(obj instanceof MiConnection))
				{
				nodes.addElement(obj);
				}
			}
		return(nodes);
		}
	public static	void		stripTargetOfConnections(MiPart target, String edgeConnectionType)
		{
		int numParts = target.getNumberOfParts();
		for (int i = numParts - 1; i >= 0; --i)
			{
			MiPart obj = target.getPart(i);
			if ((obj instanceof MiConnection) && (((MiConnection )obj).isType(edgeConnectionType)))
				{
				obj.deleteSelf();
				}
			}
		}
	}

