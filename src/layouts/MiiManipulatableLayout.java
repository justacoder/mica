
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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiManipulatableLayout extends MiiLayout, MiiOrientablePart
	{
				/**------------------------------------------------------
	 			 * Gets an instance of MiiLayoutManipulator that is suitable
				 * for the end-user to interactively manipulate this layout.
				 * @return		the manipulator
				 * @see			MiiLayoutManipulator
				 *------------------------------------------------------*/
	MiiLayoutManipulator	makeLayoutManipulator();


				/**------------------------------------------------------
	 			 * Creates any nodes need to have enough to make the layout
				 * recognizable, removes any edges currently existing between
				 * the nodes, and then creates edges that correspond to this
				 * type of layout.
				 *------------------------------------------------------*/
	void			formatEmptyTarget();


				/**------------------------------------------------------
	 			 * Gets the number of nodes in this graph layout.
				 * @return  		the number of nodes
				 * @see			#getNode
				 * @see			#getNodes
				 *------------------------------------------------------*/
	int			getNumberOfNodes();


				/**------------------------------------------------------
	 			 * Gets the node at the given index.
				 * @return  		the node or null
				 * @see			#getNumberOfNodes
				 * @see			#getNodes
				 *------------------------------------------------------*/
	MiPart			getNode(int index);


				/**------------------------------------------------------
	 			 * Gets the nodes of this graph layout.
				 * @return  		the nodes
				 * @see			#getNumberOfNodes
				 * @see			#getNode
				 *------------------------------------------------------*/
	MiParts			getNodes(MiParts cells);


				/**------------------------------------------------------
	 			 * Gets the bounds of the node at the given index which is
				 * the outer bounds of the node plus it's cell margins.
				 * @param index 	the index of the node to get the
				 *			'cell bounds' of.
				 * @param b  		the (returned) bounds
				 * @see			#getNode
				 *------------------------------------------------------*/
	void			getNodeBounds(int index , MiBounds b);


				/**------------------------------------------------------
	 			 * Gets the number of edges in this graph layout.
				 * @return  		the number of edges
				 * @see			#getEdge
				 *------------------------------------------------------*/
	int			getNumberOfEdges();


				/**------------------------------------------------------
	 			 * Gets the edge at the given index.
				 * @return  		the edge or null
				 * @see			#getNumberOfEdges
				 *------------------------------------------------------*/
	MiConnection		getEdge(int index);


				/**------------------------------------------------------
	 			 * Sets the prototype place holder that will be copied when
				 * new place holders are added to this graph layout.
				 * @param edge 		the prototype place holder
				 * @see			#getPrototypePlaceHolder
				 *------------------------------------------------------*/
	void			setPrototypePlaceHolder(MiPart placeHolder);


				/**------------------------------------------------------
	 			 * Gets the prototype place holder that will be copied when
				 * new edges are added to this graph layout.
				 * @returns 		the prototype place holder
				 * @see			#setPrototypePlaceHolder
				 *------------------------------------------------------*/
	MiPart			getPrototypePlaceHolder();


				/**------------------------------------------------------
	 			 * Sets the prototype connection that will be copied when
				 * new edges are added to this graph layout.
				 * @param edge 		the prototype connection
				 * @see			#setEdgeConnectionType
				 * @see			#getPrototypeEdge
				 *------------------------------------------------------*/
	void			setPrototypeEdge(MiConnection edge);


				/**------------------------------------------------------
	 			 * Gets the prototype connection that will be copied when
				 * new edges are added to this graph layout.
				 * @returns 		the prototype connection
				 * @see			#setPrototypeEdge
				 *------------------------------------------------------*/
	MiConnection		getPrototypeEdge();


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
				 *------------------------------------------------------*/
	void			setEdgeConnectionType(String connectionType);


				/**------------------------------------------------------
	 			 * Gets the type of connection that will be considered an
				 * edge in the graph layout.
				 * @returns 		the type of the connections that
				 *			are edges in this graph layout
				 * @see			#setEdgeConnectionType
				 *------------------------------------------------------*/
	String			getEdgeConnectionType();

				/**------------------------------------------------------
	 			 * Makes an place holder (node) for this layout. The
				 * registered prototype place holder is copied to make a 
				 * new node. If no prototype place holder has been 
				 * registered then a generic MiPlaceHolder is created.
				 * @returns		a new place holder node
				 * @see			#setPrototypePlaceHolder
				 * @see			#getPrototypePlaceHolder
				 *------------------------------------------------------*/
	MiPart			makePlaceHolder();


				/**------------------------------------------------------
	 			 * Makes an edge (connection) for this layout. The registered
				 * prototype edge is copied to make a new edge. If no
				 * prototype edge has been registered then a generic
				 * MiConnection is created of the correct connectionType.
				 * @returns		a new connection
				 * @see			#setPrototypeEdge
				 * @see			#getPrototypeEdge
				 * @see			#setEdgeConnectionType
				 *------------------------------------------------------*/
	MiConnection		makeEdge();


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
				 *			to
				 * @param relativeLocation the relative location of the
				 *			insertion point
				 * @return		the list of nodes that were inserted
				 *			(the given node and any placeholders
				 *			that may have to be added).
				 *------------------------------------------------------*/
	MiParts			insertNeighboringNode(MiPart node, int index, int relativeLocation);


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
				 *------------------------------------------------------*/
	MiParts			appendNeighboringNode(MiPart node, int index, int relativeLocation);


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
				 *------------------------------------------------------*/
	int			getNeighboringNode(int index, int relativeLocation);


				/**------------------------------------------------------
				 * Deletes the node at the given index. If the node is
				 * not a MiPlaceHolder then it is replaced by one. In any
				 * case the node is deleted.
				 * @param index		the index of the node to delete
				 *------------------------------------------------------*/
	void			deleteNode(int index);


				/**------------------------------------------------------
				 * Removes the node at the given index. If the node is
				 * not a MiPlaceHolder then it is replaced by one. If the
				 * node is a MiPlaceHolder then it is deleted.
				 * @param index		the index of the node to remove
				 * @return 		the orientation
				 *------------------------------------------------------*/
	void			removeNode(int index);
	}


