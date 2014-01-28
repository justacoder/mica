
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
import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.Utility;
import java.awt.Component; 
import java.awt.Frame; 
import java.util.HashMap; 
import java.util.StringTokenizer; 

/**----------------------------------------------------------------------------------------------
 * This file contains various, potentially useful, methods.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiUtility implements MiiCommandNames, MiiTypes, MiiNames, MiiPropertyTypes
	{
	private static 	MiBounds	tmpBounds	= new MiBounds();
	private static	MiPoint		tmpPoint	= new MiPoint();
	private static	MiPoint		tmpPoint1	= new MiPoint();
	private	static	MiPoint		tmpPoint2	= new MiPoint();

					/**------------------------------------------------------
					 * Gets the intersection of two lines or returns false
					 * if there is no intersection. The lines are defined
					 * by points (p1, p2) and (q1, q2) and are treated as
					 * infinitely long (i.e. the point of intersection may
					 * not lie on the line segments defined by the given points).
					 * @param p1		the start point of line 1
					 * @param p2		the end point of line 1
					 * @param q1		the start point of line 2
					 * @param q1		the end point of line 2
					 * @param intersection	the (returned) point of intersection
					 * @return		true if there is an intersection
					 *------------------------------------------------------*/
	public static 	boolean		getIntersectionOfTwoLines(
						MiPoint p1, MiPoint p2, 
						MiPoint q1, MiPoint q2, 
						MiPoint intersection)
		{
		double mp;
		double mq;
		double bp;
		double bq;
		if (p2.x == p1.x)
			{
			if (q2.x == q1.x)
				return(false);
			intersection.x = p1.x;
			mq = (q2.y - q1.y)/(q2.x - q1.x);
			bq = q1.y - mq * q1.x;
			intersection.y = mq * intersection.x + bq;
			return(true);
			}
		if (q2.x == q1.x)
			{
			if (p2.x == p1.x)
				return(false);
			intersection.x = q1.x;
			mp = (p2.y - p1.y)/(p2.x - p1.x);
			bp = p1.y - mp * p1.x;
			intersection.y = mp * intersection.x + bp;
			return(true);
			}
			
		mp = (p2.y - p1.y)/(p2.x - p1.x);
		mq = (q2.y - q1.y)/(q2.x - q1.x);
		bp = p1.y - mp * p1.x;
		bq = q1.y - mq * q1.x;
		if ((mp == mq) && (bp != bq))
			return(false);

		intersection.x = (bp - bq)/(mq - mp);
		intersection.y = mp * intersection.x + bp;
		return(true);
		}
					/**------------------------------------------------------
					 * Gets the intersection of two lines or returns false
					 * if there is no intersection. The lines are defined
					 * by points (p1, p2) and (q1, q2) and are treatd as line 
					 * (i.e. the point of intersection will lie on both line 
					 * segments).
					 * @param p1		the start point of line 1
					 * @param p2		the end point of line 1
					 * @param q1		the start point of line 2
					 * @param q1		the end point of line 2
					 * @param intersection	the (returned) point of intersection
					 * @return		true if there is an intersection
					 *------------------------------------------------------*/
	public static 	boolean		getIntersectionOfTwoLineSegments(
						MiPoint p1, MiPoint p2, 
						MiPoint q1, MiPoint q2, 
						MiPoint intersection)
		{
		if (!getIntersectionOfTwoLines(p1, p2, q1, q2, intersection))
			{
			return(false);
			}

		tmpBounds.setBounds(
			p1.x < p2.x ? p1.x : p2.x,
			p1.y < p2.y ? p1.y : p2.y,
			p1.x > p2.x ? p1.x : p2.x,
			p1.y > p2.y ? p1.y : p2.y);
	
		if (!tmpBounds.intersects(intersection))
			{
			return(false);
			}

		tmpBounds.setBounds(
			q1.x < q2.x ? q1.x : q2.x,
			q1.y < q2.y ? q1.y : q2.y,
			q1.x > q2.x ? q1.x : q2.x,
			q1.y > q2.y ? q1.y : q2.y);
	
		return(tmpBounds.intersects(intersection));
		}
					/**------------------------------------------------------
					 * Gets the MiPart that is a container of both of the
					 * given MiParts.
					 * @param child1	the given MiPart
					 * @param child2	the given MiPart
					 * @return		the common container or null
					 *------------------------------------------------------*/
	public static	MiPart		getCommonContainer(MiPart child1, MiPart child2)
		{
		MiPart commonContainer = checkForContainer(child1, child2);
		if (commonContainer != null)
			return(commonContainer);

		commonContainer = checkForContainer(child2, child1);
		return(commonContainer);
		}
					/**------------------------------------------------------
					 * Gets the container of or equal to the given container
					 * that contains the given part.
					 * @param container	the given container
					 * @param part		the given part
					 * @return		the common container or null
					 *------------------------------------------------------*/
	private	static	MiPart		checkForContainer(MiPart container, MiPart part)
		{
		if (container.isContainerOf(part))
			return(container);

		for (int i = 0; i < container.getNumberOfContainers(); ++i)
			{
			MiPart c = checkForContainer(container.getContainer(i), part);
			if (c != null)
				return(c);
			}
		return(null);
		}
					/**------------------------------------------------------
					 * Gets the children of the given MiPart. A child is 
					 * defined as the MiPart at the other side (source)
					 * of a connection that is connected to the given MiPart.
					 * @param parent	the given MiPart
					 * @return		the list of parent MiParts
					 *------------------------------------------------------*/
	public static	MiParts		getParents(MiPart child)
		{
		MiParts parents = new MiParts();
		for (int i = 0; i < child.getNumberOfConnections(); ++i)
			{
			if (child.getConnection(i).getDestination() == child)
				{
				if (child.getConnection(i).getSource() != null)
					parents.addElement(child.getConnection(i).getSource());
				}
			}
		return(parents);
		}
					/**------------------------------------------------------
					 * Gets the children of the given MiPart. A child is 
					 * defined as the MiPart at the other side (destination)
					 * of a connection that is connected to the given MiPart.
					 * @param parent	the given MiPart
					 * @return		the list of child MiParts
					 *------------------------------------------------------*/
	public static	MiParts		getChildren(MiPart parent)
		{
		MiParts children = new MiParts();
		for (int i = 0; i < parent.getNumberOfConnections(); ++i)
			{
			if (parent.getConnection(i).getSource() == parent)
				{
				if (parent.getConnection(i).getDestination() != null)
					children.addElement(parent.getConnection(i).getDestination());
				}
			}
		return(children);
		}
					/**------------------------------------------------------
					 * Gets whether a path (a sequence of connections) exist
					 * between the two given MiParts. If so, the path is
					 * appended to the given path and true is returned. The
					 * type of search is specified by the depthfirstSearch
					 * parameter.
					 * @param src		the start of the path
					 * @param dest		the end of the path
					 * @param path		the (returned) path
					 * @param depthfirstSearch 
					 *			true if depth first, false if
					 *			breadth first search is to be
					 *			performed
					 * @return		true if a path exists
					 *------------------------------------------------------*/
	public static	boolean		pathExists(
						MiPart src, 
						MiPart dest, 
						MiParts path, 
						boolean depthfirstSearch)
		{
		for (int i = 0; i < src.getNumberOfConnections(); ++i)
			{
			MiConnection conn = src.getConnection(i);
			MiPart other = conn.getOther(src);
			if (other == dest)
				return(true);
			if (depthfirstSearch)
				{
				if (!path.contains(other))
					{
					path.addElement(other);
					if (pathExists(other, dest, path, depthfirstSearch))
						return(true);
					path.removeElementAt(path.size() -1);
					}
				}
			}
		if (!depthfirstSearch)
			{
			for (int i = 0; i < src.getNumberOfConnections(); ++i)
				{
				MiConnection conn = src.getConnection(i);
				MiPart other = conn.getOther(src);
				if (!path.contains(other))
					{
					path.addElement(other);
					if (pathExists(other, dest, path, depthfirstSearch))
						return(true);
					path.removeElementAt(path.size() -1);
					}
				}
			}	
		return(false);
		}
					/**------------------------------------------------------
					 * Gets the maximum depth of a node in the graph with the
					 * given root nodes (i.e. get the maximum number of MiParts
					 * between any root MiPart and any other connected MiPart).
					 * Roots are MiParts that have no connection that they are
					 * not the source of. This is useful when laying out trees
					 * and one needs to know the number of levels in the tree.
					 * @param roots		the list of root nodes
					 * @return		the maximum number of levels in
					 *			all of the trees defined by the
					 *			connections of the root nodes
					 *------------------------------------------------------*/
	public static	int		getMaxDepth(MiParts roots)
		{
		int maxDepth = 0;
		for (int i = 0; i < roots.size(); ++i)
			maxDepth = Math.max(maxDepth, getMaxDepth(roots.elementAt(i)));
		return(maxDepth);
		}
					/**------------------------------------------------------
					 * Gets the maximum depth of the given node in it's graph
					 * of connections (i.e. get the maximum number of MiParts
					 * between any the given MiPart and all of it's root 
					 * MiParts. Roots are MiParts that have no connection that
					 * they are not the source of (i.e. they are the start of
					 * a directed graph/tree). 
					 * @param part		the part
					 * @return		the maximum number of levels to
					 *			any root in the graph of connections
					 *			that the MiPart has
					 *------------------------------------------------------*/
	public static	int		getMaxDepth(MiPart part)
		{
		MiParts children = getChildren(part);
		int maxDepth = 0;
		int currentDepth = 0;
		for (int i = 0; i < children.size(); ++i)
			{
			currentDepth = getMaxDepth(children.elementAt(i));
			if (currentDepth > maxDepth)
				maxDepth = currentDepth;
			}
		return(maxDepth + 1);
		}
					/**------------------------------------------------------
					 * Gets the list of root nodes for all of the nodes in
					 * the given container. 
					 * @param container	the container of the parts
					 * @param roots		the (returned) roots of the graph
					 *------------------------------------------------------*/
	public static	void		gatherRootNodes(MiPart container, MiParts roots)
		{
		roots.removeAllElements();
		
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart node = container.getPart(i);
			if (!(node instanceof MiConnection))
				{
				boolean nodeIsARoot = true;
				for (int j = 0; j < node.getNumberOfConnections(); ++j)
					{
					MiConnection conn = node.getConnection(j);
					if (conn.getDestination() == node)
						{
						nodeIsARoot = false;
						break;
						}
					}
				if (nodeIsARoot)
					roots.addElement(node);
				}
			}
		}
					/**------------------------------------------------------
					 * Gets whether the given MiPart's event handlers are 
					 * interested in the given event.
					 * @param event		the event
					 * @return		true if one of the event handlers
					 *			are interested
					 *------------------------------------------------------*/
	public static	boolean		isLocallyRequestingEventType(MiPart part, MiEvent event)
		{
		MiEvent[] events = part.getLocallyRequestedEventTypes();
		if (events != null)
			{
			for (int i = 0; i < events.length; ++i)
				{
				if (events[i].equalsEventType(event))
					return(true);
				}
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Get a renderer to use to draw using the coordinates and
					 * transforms compatible with the MiPart.
					 * @param part 		A part
					 * @return 		A renderer
					 *------------------------------------------------------*/
	public static	MiRenderer	getRenderer(MiPart part)
		{
		MiWindow root = part.getRootWindow();
		MiRenderer renderer = root.getCanvas().getRenderer();
		renderer = renderer.copy();
		if (part != root)
			{
			FastVector transforms = new FastVector();
			MiUtility.getTransformsAlongPath(root, part, transforms);
			for (int i = transforms.size() - 1; i >= 0; --i)
				{
				renderer.pushTransform((MiiTransform )transforms.elementAt(i));
				}
			renderer.setClipBounds(part.getContainingEditor().getWorldBounds());
			}
		return(renderer);
		}
					/**------------------------------------------------------
	 				 * Get the list of MiiTranforms in the container-part 
					 * hierarchy between and including the given parent and 
					 * given child. The order of the transforms is child-most
					 * transform (element #0) to parent-most transform.
					 * @param parent 	the given parent
					 * @param child 	the given child
					 * @param transforms 	the (returned) list of transforms
					 * @return		true if there is such a path
					 * @see			MiUtility#getPath
					 *------------------------------------------------------*/
	public static	boolean		getTransformsAlongPath(
						MiPart parent, MiPart child, FastVector transforms)
		{
		if (parent == null)
			parent = child.getRootWindow();
		if (child.getTransform() != null)
			transforms.addElement(child.getTransform());

		while ((child.getNumberOfContainers() > 0) && (child = child.getContainer(0)) != null)
			{
			if (child.getTransform() != null) 
				{
				transforms.addElement(child.getTransform());
				}
			if (child == parent)
				{
				return(true);
				}
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Get the list of MiiTranforms in the container-part 
					 * hierarchy between and including the given parent and 
					 * given child. The order of the transforms is child-most
					 * transform (element #0) to parent-most transform.
					 * @param parent 	the given parent
					 * @param child 	the given child
					 * @return		the transforms condensed into a
					 *			MiTransforms object
					 * @see			MiUtility#getPath
					 *------------------------------------------------------*/
	public static	MiTransforms	getTransformsAlongPath(MiPart parent, MiPart child)
		{
		FastVector transformList = new FastVector();
		if (!getTransformsAlongPath(parent, child, transformList))
			return(null);
		MiTransforms transforms = new MiTransforms();
		for (int i = transformList.size() - 1; i >= 0; --i)
			transforms.pushTransform((MiiTransform )transformList.elementAt(i));
		return(transforms);
		}
					/**------------------------------------------------------
	 				 * Get the list of MiParts (path) in the container-part 
					 * hierarchy between and including the given parent and 
					 * given child. The order of the path is child (element
					 * #0) to parent. If the parent is null then the root
					 * window of the child is used as the parent.
					 * @param parent 	the given parent
					 * @param child 	the given child
					 * @param path 		the (returned) list of parts (path)
					 * @return		true if there is such a path
					 * @see			MiUtility#getTransformsAlongPath
					 *------------------------------------------------------*/
	public static	boolean		getPath(MiPart parent, MiPart child, MiParts path)
		{
		path.addElement(child);
		if (parent == null)
			parent = child.getRootWindow();
		while ((child.getNumberOfContainers() > 0) && (child = child.getContainer(0)) != null)
			{
			path.addElement(child);
			if (child == parent)
				return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Gets the Frame that contains the component.
					 * @param component	the component
					 * @return		the Frame or null, if none
					 *------------------------------------------------------*/
	public static	Frame		getFrame(Component applet)
		{
		if (applet instanceof Frame)
			{
			return((Frame )applet);
			}

		Component c = applet;
		while ((c = c.getParent()) != null)
			{
			if (c instanceof Frame)
				return((Frame )c);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the part in the given container or in one of the 
					 * parts of this container that has the given name.
	 				 * @param container 	the container of the parts to check
	 				 * @param name	 	the given name
					 * @return		the part with the given name or null
					 *------------------------------------------------------*/
	public static	MiPart		getDeepPart(MiPart container, String name)
		{
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			String partName = part.getName();
			if ((partName != null) && (partName.equals(name)))
				return(part);
			part = getDeepPart(part, name);
			if (part != null)
				return(part);
			}
		return(null);
		}
	public static	void		getBoundsOfTree(MiPart root, String connectionType, MiBounds bounds)
		{
		bounds.reverse();
		getBoundsOfBranch(root, connectionType, bounds);
		}
	private static	void		getBoundsOfBranch(
						MiPart parent, String connectionType, MiBounds bounds)
		{
		MiBounds tmpBounds = new MiBounds();
		for (int i = 0; i < parent.getNumberOfConnections(); ++i)
			{
			MiConnection conn = parent.getConnection(i);
			if (conn.isType(connectionType) && (conn.getSource() == parent))
				{
				MiPart child = conn.getDestination();
				if (child != null)
					{
					bounds.union(child.getBounds(tmpBounds));
					getBoundsOfBranch(child, connectionType, bounds);
					}
				}
			}
		}
	public static	void		translateTree(
						MiPart parent, 
						String connectionType, 
						MiDistance tx, 
						MiDistance ty)
		{
		for (int i = 0; i < parent.getNumberOfConnections(); ++i)
			{
			MiConnection conn = parent.getConnection(i);
			if (conn.isType(connectionType) && (conn.getSource() == parent))
				{
				MiPart child = conn.getDestination();
				if (child != null)
					{
					child.translate(tx, ty);
					translateTree(child, connectionType, tx, ty);
					}
				}
			}
		}
	public static 	MiParts		makeCopyOfNetwork(MiParts network)
		{
		HashMap mapOfOldToNewObjects = new HashMap();
		MiParts copyOfNetwork = new MiParts();
		for (int i = 0; i < network.size(); ++i)
			{
			MiPart part = network.elementAt(i);
			if (!(part instanceof MiConnection))
				{
				MiPart newPart = part.deepCopy();
				copyOfNetwork.addElement(newPart);
				mapOfOldToNewObjects.put(part, newPart);
				}
			}
		// Look for MiConnectionJunctionPoints because they are not selectable...
		for (int i = 0; i < network.size(); ++i)
			{
			MiPart part = network.elementAt(i);
			if (part instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )part;
				if (conn.getSource() instanceof MiConnectionJunctionPoint)
					{
					MiPart connConnPt = conn.getSource();
					if ((mapOfOldToNewObjects.get(connConnPt) == null)
						&& (connConnPt.getNumberOfConnections() > 1))
						{
						int numConnsToConnConnPtToMakeCopyOf = 0;
						for (int j = 0; j < connConnPt.getNumberOfConnections(); ++j)
							{
							MiPart con = connConnPt.getConnection(j);
							if (network.contains(conn))
								{
								++numConnsToConnConnPtToMakeCopyOf;
								}
							}
						if (numConnsToConnConnPtToMakeCopyOf > 1)
							{
							MiPart newCCPt = connConnPt.deepCopy();
							copyOfNetwork.addElement(newCCPt);
							mapOfOldToNewObjects.put(connConnPt, newCCPt);
							}
						}
					}
				if (conn.getDestination() instanceof MiConnectionJunctionPoint)
					{
					MiPart connConnPt = conn.getDestination();
					if ((mapOfOldToNewObjects.get(connConnPt) == null)
						&& (connConnPt.getNumberOfConnections() > 1))
						{
						int numConnsToConnConnPtToMakeCopyOf = 0;
						for (int j = 0; j < connConnPt.getNumberOfConnections(); ++j)
							{
							MiPart con = connConnPt.getConnection(j);
							if (network.contains(conn))
								{
								++numConnsToConnConnPtToMakeCopyOf;
								}
							}
						if (numConnsToConnConnPtToMakeCopyOf > 1)
							{
							MiPart newCCPt = connConnPt.deepCopy();
							copyOfNetwork.addElement(newCCPt);
							mapOfOldToNewObjects.put(connConnPt, newCCPt);
							}
						}
					}
				}
			}
		for (int i = 0; i < network.size(); ++i)
			{
			MiPart part = network.elementAt(i);
			if (part instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )part;
				if ((conn.getConnectionsMustBeConnectedAtBothEnds())
					&& ((conn.getSource() == null) 
						|| (mapOfOldToNewObjects.get(conn.getSource()) == null)
					|| (conn.getDestination() == null) 
						|| (mapOfOldToNewObjects.get(conn.getDestination()) == null)))
					{
					continue;
					}

				MiConnection newConn = (MiConnection )conn.deepCopy();

				MiPart oldSource = conn.getSource();
				if (oldSource != null)
					{
					MiPart newSource = (MiPart )mapOfOldToNewObjects.get(conn.getSource());
					if (newSource != null)
						{
						newConn.setSourceConnPt(conn.getSourceConnPt());
						newConn.setSource(newSource);
						}
					}

				MiPart oldDestination = conn.getDestination();
				if (oldDestination != null)
					{
					MiPart newDestination = (MiPart )mapOfOldToNewObjects.get(conn.getDestination());
					if (newDestination != null)
						{
						newConn.setDestinationConnPt(conn.getDestinationConnPt());
						newConn.setDestination(newDestination);
						}
					}
				MiiLayout layout = newConn.getLayout();
				newConn.setLayout(null);
				newConn.setLayout(layout);
				newConn.validateLayout();
				copyOfNetwork.addElement(newConn);
				}
			}
		return(copyOfNetwork);
		}



					/**------------------------------------------------------
	 				 * Parses the given text and assigns any short cut it
					 * specifies to the given widget. The short cut calls the
					 * given method. The given text is of form: 
					 * 	labe&l\tCtrl+C
					 * where the optional '&' indicates that the character
					 * following is to be a short cut (when widget is visible)
					 * and the '\t' indicates the following event is to be a
					 * short cut. If either '&' or '\t' is proceeded by a 
					 * backslash then the character is ignored (i.e. "\\&"
					 * is replaced by "&").
	 				 * @param widget	The widget to assign any short
					 *			cuts to.
	 				 * @param method	The method to call when the short
					 *			cut is activated by the end-user.
	 				 * @param text		The string to parse looking for
					 *			short cuts.
	 				 * @return 	 	the text left over after removing
					 *			any specified short cuts
					 *------------------------------------------------------*/
	public static	MiText		assignShortCutFromLabel(
						MiPart widget, MiiCommandHandler method, String text)
		{
		int index;
		String labelSpec = text;
		MiText textObj = new MiText(text);
		int startIndex = 0;
		while ((index = text.indexOf('&', startIndex)) != -1)
			{
			if ((index == 0) || (text.charAt(index - 1) != '\\'))
				{
				MiEvent accelEvent = new MiEvent();
				String tmp = text.toLowerCase();
				accelEvent.key = tmp.charAt(index + 1);
				accelEvent.type = MiEvent.Mi_KEY_EVENT;
				accelEvent.modifiers = MiToolkit.acceleratorModifier;
				widget.appendEventHandler(new MiShortCutHandler(
					accelEvent, method, Mi_EXECUTE_SHORT_CUT_COMMAND_NAME));
				text = text.substring(0, index).concat(text.substring(index + 1));
				textObj.setUnderlineLetter(index);
				break;
				}
			text = text.substring(0, index - 1) + text.substring(index);
			startIndex = index;
			}
		startIndex = 0;
		while ((index = text.indexOf('\t', startIndex)) != -1)
			{
			if ((index == 0) || (text.charAt(index - 1) != '\\'))
				{
				MiEvent accelEvent = new MiEvent();
				String accelText = text.substring(index + 1);
				text = text.substring(0, index);
				if (!MiEvent.stringToEvent(accelText, accelEvent))
					{
					MiDebug.printlnError("MiButton: Unable to generate event from text:"
						+ " \"" + accelText + "\" in label: " + labelSpec);
					}
				if (accelText.toUpperCase().indexOf("SHIFT") == -1)
					{
					accelEvent.modifiers &= ~MiEvent.Mi_SHIFT_KEY_HELD_DOWN;
					}
				widget.appendEventHandler(
					new MiShortCutHandler(
						accelEvent, method, Mi_EXECUTE_SHORT_CUT_COMMAND_NAME));
				break;
				}
			text = text.substring(0, index - 1) + text.substring(index);
			startIndex = index;
			}
		textObj.setText(text);
		return(textObj);
		}
					/**------------------------------------------------------
	 				 * Sets the mouse appearance. 
					 * @param part		a part in the root window in which
					 *			this sets the mouse appearance or
					 *			null
					 * @param appearance	the mouse cursor appearance
					 * @param identifier	identifies the current position in
					 *			the mouse appearance stack
					 * @see			MiEditor#pushMouseAppearance
					 *------------------------------------------------------*/
	public static	void		pushMouseAppearance(MiPart part, int appearance, String identifier)
		{
		if (part != null)
			{
			MiEditor rootWindow = part.getRootWindow();
			if (rootWindow != null)
				{
				rootWindow.pushMouseAppearance(appearance, identifier);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Restores the mouse appearance to the appearance of the
					 * mouse before the corresponding call to #pushMouseAppearance
					 * which used this identifier.
					 * @param part		a part in the root window in which
					 *			this restores the mouse appearance
					 *			or null
					 * @param identifier	identifies a position in the mouse 
					 *			appearance stack
					 * @see			MiEditor#popMouseAppearance
					 *------------------------------------------------------*/
	public static	void		popMouseAppearance(MiPart part, String identifier)
		{
		if (part != null)
			{
			MiEditor rootWindow = part.getRootWindow();
			if (rootWindow != null)
				{
				rootWindow.popMouseAppearance(identifier);
				}
			}
		}
	public static	MiParts		getAllParts(MiPart container, MiParts parts)
		{
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			parts.addElement(container.getPart(i));
			getAllParts(container.getPart(i), parts);
			}
		return(parts);
		}
	public static	MiParts		getActualShapesToApplyAttributeChangeTo(MiParts parts)
		{
		MiParts expandedListOfParts = new MiParts();
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.get(i);
			getActualShapesToApplyAttributeChangeTo(obj, expandedListOfParts);
			}
		return(expandedListOfParts);
		}
	public static	void		getActualShapesToApplyAttributeChangeTo(MiPart target, MiParts expandedListOfParts)
		{
		// If target is just a grouping and has no attributes itself...
		if (((target instanceof MiLayout) || (target instanceof MiContainer)) && (!(target instanceof MiVisibleContainer)))
			{
			for (int i = 0; i < target.getNumberOfParts(); ++i)
				{
				getActualShapesToApplyAttributeChangeTo(target.getPart(i), expandedListOfParts);
				}
			}
		for (int i = 0; i < target.getNumberOfAttachments(); ++i)
			{
			getActualShapesToApplyAttributeChangeTo(target.getAttachment(i), expandedListOfParts);
			}
		if (target.getResource(MiiSelectionGraphics.SELECTION_GRAPHICS_GRAPHICS) == null)
			{
			if (!expandedListOfParts.contains(target))
				{
				expandedListOfParts.add(target);
				}
			}
		}

	public static	MiPart		getManipulatableContainerOfPart(MiEditor editor, MiPart part)
		{
		MiEditorFilter filter = editor.getFilter();
		if (filter != null)
			{
			MiPart parent = part;
			MiPart obj = part;
			while ((obj = filter.accept(obj)) == null)
				{
				parent = parent.getContainer(0);
				if (parent == editor)
					return(part);
				obj = parent;
				}
			return(obj);
			}
		if (editor.hasLayers())
			{
			MiPart parent = part;
			while (!editor.isLayer(parent))
				{
				part = parent;
				parent = parent.getContainer(0);
				if (parent == editor)
					return(part);
				}
			return(part);
			}
		MiPart parent = part;
		while (parent != editor)
			{
			part = parent;
			parent = parent.getContainer(0);
			}
		return(part);
		}
	/**
	 * @param spec of form: "subPartName.subPartPointNumber"
	 * @return true if such a part with such a point is found
	 **/
	public static		boolean		getPointFromPartNamePointNumberSpec(MiPart part, String spec, MiPoint returnedPoint)
		{
		int dotIndex = spec.lastIndexOf('.');
		String referencePartName = spec.substring(0, dotIndex);
		int referencePointNum = Integer.parseInt(spec.substring(dotIndex + 1));
		MiPart referencePart = part.isContainerOf(referencePartName);
		if (referencePart == null)
			{
			return(false);
			}
		referencePart.getPoint(referencePointNum, returnedPoint);
		return(true);
		}

	public static 	MiPoint		getLocationWithRespectToPart(
						MiPart target, 
						int location, 
						MiBounds boundsToPosition,	// toPositionWithRespectToTarget, 
						MiPoint pt,
						MiMargins margins)
		{
		MiBounds targetBounds = target.getBounds(tmpBounds);
		if (target instanceof MiEditor)
			targetBounds = ((MiEditor )target).getWorldBounds(tmpBounds);

		MiDistance partHalfWidth = boundsToPosition.getWidth()/2;
		MiDistance partHalfHeight = boundsToPosition.getHeight()/2;

		if ((location >= Mi_MIN_BUILTIN_LINE_LOCATION) && (location <= Mi_MAX_BUILTIN_LINE_LOCATION))
			{
			getLocationOfLineLocation(target, location, boundsToPosition, pt, margins);
			return(pt);
			}

		switch (location)
			{
			case Mi_CENTER_LOCATION :
				targetBounds.getCenter(pt);
				break;
		
			case Mi_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin(), 
					targetBounds.getCenterY());
				break;
		
			case Mi_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax(), 
					targetBounds.getCenterY());
				break;
		
			case Mi_BOTTOM_LOCATION :
				pt.set(
					targetBounds.getCenterX(),
					targetBounds.getYmin());
				break;
		
			case Mi_TOP_LOCATION :
				pt.set(
					targetBounds.getCenterX(),
					targetBounds.getYmax());
				break;

			case Mi_LOWER_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin(),
					targetBounds.getYmin());
				break;
			case Mi_LOWER_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax(),
					targetBounds.getYmin());
				break;
			case Mi_UPPER_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin(),
					targetBounds.getYmax());
				break;
			case Mi_UPPER_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax(),
					targetBounds.getYmax());
				break;
			case Mi_START_LOCATION :
				if (target.getNumberOfPoints() > 0)
					{
					target.getPoint(0, pt);
					}
				break;
			case Mi_END_LOCATION :
				if (target.getNumberOfPoints() > 0)
					{
					target.getPoint(-1, pt);
					}
				break;
			case Mi_OUTSIDE_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin() - margins.right - partHalfWidth, 
					targetBounds.getCenterY() + margins.bottom);
				break;
		
			case Mi_OUTSIDE_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax() + margins.left + partHalfWidth, 
					targetBounds.getCenterY() + margins.bottom);
				break;
		
			case Mi_OUTSIDE_BOTTOM_LOCATION :
				pt.set(
					targetBounds.getCenterX() + margins.left,
					targetBounds.getYmin() - margins.top - partHalfHeight);
				break;
		
			case Mi_OUTSIDE_TOP_LOCATION :
				pt.set(
					targetBounds.getCenterX() + margins.left,
					targetBounds.getYmax() + margins.bottom + partHalfHeight);
				break;

			case Mi_OUTSIDE_LOWER_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin() - boundsToPosition.getWidth()/2 - margins.right,
					targetBounds.getYmin() - boundsToPosition.getHeight()/2 - margins.top);
				break;
			case Mi_OUTSIDE_LOWER_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax() + boundsToPosition.getWidth()/2 + margins.left,
					targetBounds.getYmin() - boundsToPosition.getHeight()/2 - margins.top);
				break;
			case Mi_OUTSIDE_UPPER_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin() - boundsToPosition.getWidth()/2 - margins.right,
					targetBounds.getYmax() + boundsToPosition.getHeight()/2 + margins.bottom);
				break;
			case Mi_OUTSIDE_UPPER_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax() + boundsToPosition.getWidth()/2 + margins.left,
					targetBounds.getYmax() + boundsToPosition.getHeight()/2 + margins.bottom);
				break;
			case Mi_INSIDE_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin() + boundsToPosition.getWidth()/2 + margins.bottom, 
					targetBounds.getCenterY());
				break;
		
			case Mi_INSIDE_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax() - boundsToPosition.getWidth()/2 - margins.top, 
					targetBounds.getCenterY());
				break;
		
			case Mi_INSIDE_BOTTOM_LOCATION :
				pt.set(
					targetBounds.getCenterX(),
					targetBounds.getYmin() + boundsToPosition.getHeight()/2 + margins.bottom);
				break;
		
			case Mi_INSIDE_TOP_LOCATION :
				pt.set(
					targetBounds.getCenterX(),
					targetBounds.getYmax() - boundsToPosition.getHeight()/2 - margins.top);
				break;

			case Mi_INSIDE_LOWER_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin() + boundsToPosition.getWidth()/2 + margins.left,
					targetBounds.getYmin() + boundsToPosition.getHeight()/2 + margins.bottom);
				break;
			case Mi_INSIDE_LOWER_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax() - boundsToPosition.getWidth()/2 - margins.right,
					targetBounds.getYmin() + boundsToPosition.getHeight()/2 + margins.bottom);
				break;
			case Mi_INSIDE_UPPER_LEFT_LOCATION :
				pt.set(
					targetBounds.getXmin() + boundsToPosition.getWidth()/2 + margins.left,
					targetBounds.getYmax() - boundsToPosition.getHeight()/2 - margins.top);
				break;
			case Mi_INSIDE_UPPER_RIGHT_LOCATION :
				pt.set(
					targetBounds.getXmax() - boundsToPosition.getWidth()/2 - margins.right,
					targetBounds.getYmax() - boundsToPosition.getHeight()/2 - margins.top);
				break;
			case Mi_WNW_LOCATION				:
				pt.set(
					targetBounds.getXmin(),
					(targetBounds.getCenterY() + targetBounds.getYmax())/2);
				break;
			case Mi_WSW_LOCATION				:
				pt.set(
					targetBounds.getXmin(),
					(targetBounds.getCenterY() + targetBounds.getYmin())/2);
				break;
			case Mi_ENE_LOCATION				:
				pt.set(
					targetBounds.getXmax(),
					(targetBounds.getCenterY() + targetBounds.getYmax())/2);
				break;
			case Mi_ESE_LOCATION				:
				pt.set(
					targetBounds.getXmax(),
					(targetBounds.getCenterY() + targetBounds.getYmin())/2);
				break;
			case Mi_NWN_LOCATION				:
				pt.set(
					(targetBounds.getXmin() + targetBounds.getCenterX())/2,
					targetBounds.getYmax());
				break;
			case Mi_NEN_LOCATION				:
				pt.set(
					(targetBounds.getXmax() + targetBounds.getCenterX())/2,
					targetBounds.getYmax());
				break;
			case Mi_SWS_LOCATION				:
				pt.set(
					(targetBounds.getXmin() + targetBounds.getCenterX())/2,
					targetBounds.getYmin());
				break;
			case Mi_SES_LOCATION				:
				pt.set(
					(targetBounds.getXmax() + targetBounds.getCenterX())/2,
					targetBounds.getYmin());
				break;

			default:
				MiDebug.printStackTrace("Unknown location: " + location);
				break;

			}
		boundsToPosition.setCenter(pt);
		return(pt);
		}
	public static 	MiPoint		getLocationOfLineLocation(
						MiPart target, 
						int location, 
						MiBounds boundsToPosition, 
						MiPoint pt,
						MiMargins margins)
		{
		int segmentStartPoint;
		int segmentEndPoint;
		MiDistance partHalfWidth = boundsToPosition.getWidth()/2;
		MiDistance partHalfHeight = boundsToPosition.getHeight()/2;

		if (target.getNumberOfPoints() < 2)
			{
			if (target.getNumberOfPoints() == 1)
				target.getPoint(0, pt);
			else
				pt.set(0, 0);
			return(pt);
			}
		switch (location)
			{
			case Mi_LINE_CENTER_LOCATION			:
				// Get middle line segment...
				segmentEndPoint = 1;
				if (target.getNumberOfPoints() > 2)
					segmentEndPoint = target.getNumberOfPoints()/2;
				segmentStartPoint = segmentEndPoint - 1;
				target.getPoint(segmentStartPoint, tmpPoint1);
				target.getPoint(segmentEndPoint, tmpPoint2);
				pt.set((tmpPoint1.x + tmpPoint2.x)/2, (tmpPoint1.y + tmpPoint2.y)/2);
				break;
			case Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION	:
				segmentEndPoint = 1;
				if (target.getNumberOfPoints() > 2)
					segmentEndPoint = target.getNumberOfPoints()/2;
				segmentStartPoint = segmentEndPoint - 1;
				target.getPoint(segmentStartPoint, tmpPoint1);
				target.getPoint(segmentEndPoint, tmpPoint2);
				if ((Math.abs(tmpPoint2.x - tmpPoint1.x) > Math.abs(tmpPoint2.y - tmpPoint1.y)))
					{
					// Horizontal
					pt.set(
						(tmpPoint1.x + tmpPoint2.x)/2,
						(tmpPoint1.y + tmpPoint2.y)/2 + margins.bottom + partHalfHeight);
					}
				else
					{
					pt.set(
						(tmpPoint1.x + tmpPoint2.x)/2 + margins.bottom + partHalfWidth,
						(tmpPoint1.y + tmpPoint2.y)/2);
					}
				break;
			case Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION	:
				segmentEndPoint = 1;
				if (target.getNumberOfPoints() > 2)
					segmentEndPoint = target.getNumberOfPoints()/2;
				segmentStartPoint = segmentEndPoint - 1;
				target.getPoint(segmentStartPoint, tmpPoint1);
				target.getPoint(segmentEndPoint, tmpPoint2);
				if ((Math.abs(tmpPoint2.x - tmpPoint1.x) > Math.abs(tmpPoint2.y - tmpPoint1.y)))
					{
					// Horizontal
					pt.set(
						(tmpPoint1.x + tmpPoint2.x)/2,
						(tmpPoint1.y + tmpPoint2.y)/2 - margins.bottom - partHalfHeight);
					}
				else
					{
					pt.set(
						(tmpPoint1.x + tmpPoint2.x)/2 - margins.bottom - partHalfWidth,
						(tmpPoint1.y + tmpPoint2.y)/2);
					}
				break;
			case Mi_LINE_AT_START_LOCATION			:
				target.getPoint(0, pt);
				break;
			case Mi_LINE_START_LOCATION			:
				target.getPoint(0, tmpPoint1);
				target.getPoint(1, tmpPoint2);
				if ((Math.abs(tmpPoint2.x - tmpPoint1.x) > Math.abs(tmpPoint2.y - tmpPoint1.y)))
					{
					// Horizontal
					if (tmpPoint2.x - tmpPoint1.x > 0)
						{
						// To right
						pt.set(
							tmpPoint1.x + margins.left + partHalfWidth,
							tmpPoint1.y);
						}
					else
						{
						pt.set(
							tmpPoint1.x - margins.left - partHalfWidth,
							tmpPoint1.y);
						}
					}
				else
					{
					if (tmpPoint2.y - tmpPoint1.y > 0)
						{
						// Going up
						pt.set(
							tmpPoint1.x,
							tmpPoint1.y + margins.left + partHalfHeight);
						}
					else
						{
						pt.set(
							tmpPoint1.x,
							tmpPoint1.y - margins.left - partHalfHeight);
						}
					}
				break;
			case Mi_LINE_START_TOP_OR_RIGHT_LOCATION	:
				target.getPoint(0, tmpPoint1);
				target.getPoint(1, tmpPoint2);
				if ((Math.abs(tmpPoint2.x - tmpPoint1.x) > Math.abs(tmpPoint2.y - tmpPoint1.y)))
					{
					// Horizontal
					if (tmpPoint2.x - tmpPoint1.x > 0)
						{
						// To right
						pt.set(
							tmpPoint1.x + margins.left + partHalfWidth,
							tmpPoint1.y + margins.bottom + partHalfHeight);
						}
					else
						{
						// To left
						pt.set(
							tmpPoint1.x - margins.left - partHalfWidth,
							tmpPoint1.y + margins.bottom + partHalfHeight);
						}
						
					}
				else
					{
					if (tmpPoint2.y - tmpPoint1.y > 0)
						{
						// To top
						pt.set(
							tmpPoint1.x + margins.bottom + partHalfWidth,
							tmpPoint1.y + margins.left + partHalfHeight);
						}
					else
						{
						pt.set(
							tmpPoint1.x + margins.bottom + partHalfWidth,
							tmpPoint1.y - margins.left - partHalfHeight);
						}
					}
				break;
			case Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION	:
				target.getPoint(0, tmpPoint1);
				target.getPoint(1, tmpPoint2);
				if ((Math.abs(tmpPoint2.x - tmpPoint1.x) > Math.abs(tmpPoint2.y - tmpPoint1.y)))
					{
					// Horizontal
					if (tmpPoint2.x - tmpPoint1.x > 0)
						{
						// To right
						pt.set(
							tmpPoint1.x + margins.left + partHalfWidth,
							tmpPoint1.y - margins.bottom - partHalfHeight);
						}
					else
						{
						pt.set(
							tmpPoint1.x - margins.left - partHalfWidth,
							tmpPoint1.y - margins.bottom - partHalfHeight);
						}
					}
				else
					{
					if (tmpPoint2.y - tmpPoint1.y > 0)
						{
						// To top
						pt.set(
							tmpPoint1.x - margins.bottom - partHalfWidth,
							tmpPoint1.y + margins.left + partHalfHeight);
						}
					else
						{
						pt.set(
							tmpPoint1.x - margins.bottom - partHalfWidth,
							tmpPoint1.y - margins.left - partHalfHeight);
						}
					}
				break;
			case Mi_LINE_AT_END_LOCATION			:
				target.getPoint(-1, pt);
				break;
			case Mi_LINE_END_LOCATION			:
				target.getPoint(-1, tmpPoint1);
				target.getPoint(target.getNumberOfPoints() - 2, tmpPoint2);
				if ((Math.abs(tmpPoint2.x - tmpPoint1.x) > Math.abs(tmpPoint2.y - tmpPoint1.y)))
					{
					// Horizontal
					if (tmpPoint2.x - tmpPoint1.x > 0)
						{
						// To right
						pt.set(
							tmpPoint1.x + margins.left + partHalfWidth,
							tmpPoint1.y);
						}
					else
						{
						pt.set(
							tmpPoint1.x - margins.left - partHalfWidth,
							tmpPoint1.y);
						}
					}
				else
					{
					if (tmpPoint2.y - tmpPoint1.y > 0)
						{
						// Going up
						pt.set(
							tmpPoint1.x,
							tmpPoint1.y + margins.left + partHalfHeight);
						}
					else
						{
						pt.set(
							tmpPoint1.x,
							tmpPoint1.y - margins.left - partHalfHeight);
						}
					}
				break;
			case Mi_LINE_END_TOP_OR_RIGHT_LOCATION	:
				target.getPoint(-1, tmpPoint1);
				target.getPoint(target.getNumberOfPoints() - 2, tmpPoint2);
				if ((Math.abs(tmpPoint2.x - tmpPoint1.x) > Math.abs(tmpPoint2.y - tmpPoint1.y)))
					{
					// Horizontal
					if (tmpPoint2.x - tmpPoint1.x > 0)
						{
						// To right
						pt.set(
							tmpPoint1.x + margins.left + partHalfWidth,
							tmpPoint1.y + margins.bottom + partHalfHeight);
						}
					else
						{
						// To left
						pt.set(
							tmpPoint1.x - margins.left - partHalfWidth,
							tmpPoint1.y + margins.bottom + partHalfHeight);
						}
						
					}
				else
					{
					if (tmpPoint2.y - tmpPoint1.y > 0)
						{
						// To top
						pt.set(
							tmpPoint1.x + margins.bottom + partHalfWidth,
							tmpPoint1.y + margins.left + partHalfHeight);
						}
					else
						{
						pt.set(
							tmpPoint1.x + margins.bottom + partHalfWidth,
							tmpPoint1.y - margins.left - partHalfHeight);
						}
					}
				break;
			case Mi_LINE_START_TOP_LOCATION	:
				{
				target.getPoint(0, tmpPoint1);
				target.getPoint(1, tmpPoint2);
				double theta = Utility.getAngle(tmpPoint2.y - tmpPoint1.y, tmpPoint2.x - tmpPoint1.x);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				MiCoord dx = margins.left * cos - margins.bottom * sin;
				MiCoord dy = margins.left * sin + margins.bottom * cos;
//System.out.println("Mi_LINE_START_TOP_LOCATION");
//System.out.println("theta = " + theta);
//System.out.println("dx = " + dx);
//System.out.println("dy = " + dy);
				if ((theta > 3*Math.PI/4) && (theta <= 7*Math.PI/4))
					dy -= partHalfHeight;
				else
					dy += partHalfHeight;
//System.out.println("NOW dy = " + dy);

				if ((theta > Math.PI/3) && (theta <= 4*Math.PI/3))
					dx -= partHalfWidth;
				else
					dx += partHalfWidth;

				pt.set(tmpPoint1.x + dx, tmpPoint1.y + dy);
				break;
				}
			case Mi_LINE_START_BOTTOM_LOCATION	:
				{
				target.getPoint(-1, tmpPoint1);
				target.getPoint(target.getNumberOfPoints() - 2, tmpPoint2);
				double theta = Utility.getAngle(tmpPoint1.y - tmpPoint2.y, tmpPoint1.x - tmpPoint2.x);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				MiCoord dx = -(margins.left + partHalfWidth) * cos
						- (margins.bottom + partHalfHeight) * sin;
				MiCoord dy = -(margins.left + partHalfWidth) * sin
						+ (margins.bottom + partHalfHeight) * cos;
				pt.set(tmpPoint1.x - dx, tmpPoint1.y - dy);
				break;
				}
			case Mi_LINE_END_TOP_LOCATION	:
				{
				target.getPoint(-1, tmpPoint1);
				target.getPoint(target.getNumberOfPoints() - 2, tmpPoint2);
				double theta = Utility.getAngle(tmpPoint1.y - tmpPoint2.y, tmpPoint1.x - tmpPoint2.x);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				MiCoord dx = -margins.left * cos - margins.bottom * sin;
				MiCoord dy = -margins.left * sin + margins.bottom * cos;
//System.out.println("Mi_LINE_END_TOP_LOCATION");
//System.out.println("dx = " + dx);
//System.out.println("dy = " + dy);
//System.out.println("theta = " + theta);
				if ((theta > Math.PI/3) && (theta <= 4*Math.PI/3))
					dy -= partHalfHeight;
				else
					dy += partHalfHeight;
//System.out.println("NOW dy = " + dy);

				if ((theta > 3*Math.PI/4) && (theta <= 7*Math.PI/4))
					dx += partHalfWidth;
				else
					dx -= partHalfWidth;

				pt.set(tmpPoint1.x + dx, tmpPoint1.y + dy);
				break;
				}
			case Mi_LINE_END_BOTTOM_LOCATION	:
				{
				target.getPoint(-1, tmpPoint1);
				target.getPoint(target.getNumberOfPoints() - 2, tmpPoint2);
				double theta = Utility.getAngle(tmpPoint1.y - tmpPoint2.y, tmpPoint1.x - tmpPoint2.x);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				MiCoord dx = (margins.left + partHalfWidth) * cos
						- (margins.bottom + partHalfHeight) * sin;
				MiCoord dy = (margins.left + partHalfWidth) * sin
						+ (margins.bottom + partHalfHeight) * cos;
				pt.set(tmpPoint1.x - dx, tmpPoint1.y - dy);
				break;
				}
			case Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION	:
				target.getPoint(-1, tmpPoint1);
				target.getPoint(target.getNumberOfPoints() - 2, tmpPoint2);
				if ((Math.abs(tmpPoint2.x - tmpPoint1.x) > Math.abs(tmpPoint2.y - tmpPoint1.y)))
					{
					// Horizontal
					if (tmpPoint2.x - tmpPoint1.x > 0)
						{
						// To right
						pt.set(
							tmpPoint1.x + margins.left + partHalfWidth,
							tmpPoint1.y - margins.bottom - partHalfHeight);
						}
					else
						{
						pt.set(
							tmpPoint1.x - margins.left - partHalfWidth,
							tmpPoint1.y - margins.bottom - partHalfHeight);
						}
					}
				else
					{
					if (tmpPoint2.y - tmpPoint1.y > 0)
						{
						// To top
						pt.set(
							tmpPoint1.x - margins.bottom - partHalfWidth,
							tmpPoint1.y + margins.left + partHalfHeight);
						}
					else
						{
						pt.set(
							tmpPoint1.x - margins.bottom - partHalfWidth,
							tmpPoint1.y - margins.left - partHalfHeight);
						}
					}
				break;
			}
		return(pt);
		}
	public static	int		rotateAndFlipExitDirectionMask(int mask, double radians, boolean flipped)
		{
		if ((mask == 0) || (mask == Mi_ALL_DIRECTIONS) || ((radians == 0) && (!flipped)))
			{
			return(mask);
			}
		int newMask = 0;
		if (flipped)
			{
			// Assume flip around vertical axis...
			if ((mask & Mi_LEFT) != 0)
				{
				newMask |= Mi_RIGHT;
				}
			if ((mask & Mi_RIGHT) != 0)
				{
				newMask |= Mi_LEFT;
				}
			if ((mask & Mi_UP) != 0)
				{
				newMask |= Mi_UP;
				}
			if ((mask & Mi_DOWN) != 0)
				{
				newMask |= Mi_DOWN;
				}
			}
		else
			{
			newMask = mask;
			}
		if (radians != 0)
			{
			while (radians >= Math.PI*2)
				radians -= Math.PI*2;
			while (radians < 0)
				radians += Math.PI*2;

			if ((radians > Math.PI/4) && (radians <= 3*Math.PI/4))
				{
				if ((mask & Mi_DOWN) != 0)
					{
					newMask |= Mi_RIGHT;
					}
				if ((mask & Mi_UP) != 0)
					{
					newMask |= Mi_LEFT;
					}
				if ((mask & Mi_LEFT) != 0)
					{
					newMask |= Mi_DOWN;
					}
				if ((mask & Mi_RIGHT) != 0)
					{
					newMask |= Mi_UP;
					}
				}
			else if ((radians > 3*Math.PI/4) && (radians <= 5*Math.PI/4))
				{
				if ((mask & Mi_DOWN) != 0)
					{
					newMask |= Mi_UP;
					}
				if ((mask & Mi_UP) != 0)
					{
					newMask |= Mi_DOWN;
					}
				if ((mask & Mi_LEFT) != 0)
					{
					newMask |= Mi_RIGHT;
					}
				if ((mask & Mi_RIGHT) != 0)
					{
					newMask |= Mi_LEFT;
					}
				}
			else if ((radians > 5*Math.PI/4) && (radians < 7*Math.PI/4))
				{
				if ((mask & Mi_DOWN) != 0)
					{
					newMask |= Mi_LEFT;
					}
				if ((mask & Mi_UP) != 0)
					{
					newMask |= Mi_RIGHT;
					}
				if ((mask & Mi_LEFT) != 0)
					{
					newMask |= Mi_UP;
					}
				if ((mask & Mi_RIGHT) != 0)
					{
					newMask |= Mi_DOWN;
					}
				}
			}
		return(newMask);
		}
	public static	void		rotate(MiPoint pt, MiPoint centerOfRotation, double radians)
		{
		while (radians >= Math.PI*2)
			radians -= Math.PI*2;
		while (radians < 0)
			radians += Math.PI*2;

		if (Utility.isZero(radians))
			return;

		MiCoord x = pt.x;
		MiCoord y = pt.y;

		if (radians == Math.PI/2)
			{
			pt.x = centerOfRotation.x - (y - centerOfRotation.y);
			pt.y = centerOfRotation.y + (x - centerOfRotation.x);
			}
		else if (radians == Math.PI)
			{
			pt.x = centerOfRotation.x - (x - centerOfRotation.x);
			pt.y = centerOfRotation.y - (y - centerOfRotation.y);
			}
		else if (radians == 3 * Math.PI/2)
			{
			pt.x = centerOfRotation.x + (y - centerOfRotation.y);
			pt.y = centerOfRotation.y - (x - centerOfRotation.x);
			}
		else
			{
			double sinR = Math.sin(radians);
			double cosR = Math.cos(radians);

			x = x - centerOfRotation.x;
			y = y - centerOfRotation.y;
			pt.x = -sinR * y + cosR * x + centerOfRotation.x;
			pt.y = sinR * x + cosR * y + centerOfRotation.y;
			}
		}

	public static	MiPart		createShape(String name)
		{
		return(createShape(name, true));
		}
	public static	MiPart		createShape(String name, boolean ignoreCase)
		{
		MiPart shape = null;
		if (equals(Mi_RECTANGLE_TYPE_NAME, name, ignoreCase))
			shape =new MiRectangle();
		else if (equals(Mi_POLYGON_TYPE_NAME, name, ignoreCase))
			shape = new MiPolygon();
		else if (equals(Mi_ROUND_RECTANGLE_TYPE_NAME, name, ignoreCase))
			shape = new MiRoundRectangle();
		else if (equals(Mi_REGULAR_POLYGON_TYPE_NAME, name, ignoreCase))
			shape = new MiRegularPolygon();
		else if (equals(Mi_CIRCLE_TYPE_NAME, name, ignoreCase))
			shape = new MiCircle();
		else if (equals(Mi_LINE_TYPE_NAME, name, ignoreCase))
			shape = new MiLine();
		else if (equals(Mi_IMAGE_TYPE_NAME, name, ignoreCase))
			shape = new MiImage();
		else if (equals(Mi_ELLIPSE_TYPE_NAME, name, ignoreCase))
			shape = new MiEllipse();
		else if (equals(Mi_ARC_TYPE_NAME, name, ignoreCase))
			shape = new MiArc();
		else if (equals(Mi_ELLIPTICAL_ARC_TYPE_NAME, name, ignoreCase))
			shape = new MiEllipticalArc();
		else if (equals(Mi_3_PT_ARC_TYPE_NAME, name, ignoreCase))
			shape = new Mi3PointArc();
		else if (equals(Mi_TEXT_TYPE_NAME, name, ignoreCase))
			shape = new MiText();
		else if (equals(Mi_TRIANGLE_TYPE_NAME, name, ignoreCase))
			shape = new MiTriangle();
		else if (equals(Mi_CONTAINER_TYPE_NAME, name, ignoreCase))
			shape = new MiContainer();
		else if (equals(Mi_PLACE_HOLDER_TYPE_NAME, name, ignoreCase))
			{
			shape = new MiPlaceHolder(new MiCircle());
			shape.setIsDragAndDropTarget(true);
			shape.setBackgroundColor(MiColorManager.darkWhite);
			}
		if (shape != null)
			{
			if (!(shape instanceof MiMultiPointShape))
				shape.setBounds(new MiBounds(0,0,14,14));
			return(shape);
			}

		MiiLayout layout = createLayout(name, true, false);
		if ((layout != null) && (layout instanceof MiPart))
			{
			return((MiPart )layout);
			}

		Object obj = Utility.makeInstanceOfClass(name);
		if (obj instanceof MiPart)
			{
			return((MiPart )obj);
			}
		return(null);
		}
					/**------------------------------------------------------
					 * Gets a layout object of the given type name.
					 * @param name		the name of a layout
					 * @param ignoreCase	
					 * @param manipulatable	true if this should be a manipulatable
					 *			layout template, if the layout is an
					 *			instance of MiManipulatableLayout.
					 * @return		the layout or null
					 *------------------------------------------------------*/
	public static	MiiLayout	createLayout(String name, boolean ignoreCase, boolean manipulatable)
		{
		MiiLayout layout = null;

		boolean isTemplate = false;
		if (name.toLowerCase().endsWith(Mi_LAYOUT_TEMPLATE_NAME))
			{
			name = name.substring(0, name.length() - Mi_LAYOUT_TEMPLATE_NAME.length());
			isTemplate = true;
			}

		if (equals(Mi_ROW_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiRowLayout(manipulatable);
		else if (equals(Mi_COLUMN_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiColumnLayout(manipulatable);
		else if (equals(Mi_GRID_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiGridLayout(manipulatable);
		else if (equals(Mi_STAR_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiStarGraphLayout();
		else if (equals(Mi_UNDIRECTED_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiUndirGraphLayout();
		else if (equals(Mi_OUTLINE_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiOutlineGraphLayout();
		else if (equals(Mi_TREE_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiTreeGraphLayout();
		else if (equals(Mi_RING_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiRingGraphLayout();
		else if (equals(Mi_2DMESH_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new Mi2DMeshGraphLayout();
		else if (equals(Mi_LINE_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiLineGraphLayout();
		else if (equals(Mi_CROSSBAR_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiCrossBarGraphLayout();
		else if (equals(Mi_OMEGA_GRAPH_LAYOUT_TYPE_NAME, name, ignoreCase))
			layout = new MiOmegaGraphLayout();
		else if ((equals(Mi_SIZE_ONLY_LAYOUT_TYPE_NAME, name, ignoreCase)) 
			|| (equals(Mi_NONE_NAME, name, ignoreCase)))
			{
			layout = new MiSizeOnlyLayout();
			}
/* This fails if, say, we have name= "com.swfm.mica.MiCircle"
		else
			{
			layout = (MiiLayout )Utility.makeInstanceOfClass(name);
			}
*/

		if ((layout != null) && (manipulatable) && (layout instanceof MiManipulatableLayout))
			{
			MiManipulatableLayout manipulatableLayout = (MiManipulatableLayout )layout;
			manipulatableLayout.appendEventHandler(new MiManipulatorTargetEventHandler());

			// To allow connections in networks that are connected together to be differentiated
			// from each other, set the type of the connections, if there are connections, to an
			// unique value.
			manipulatableLayout.setEdgeConnectionType(Integer.toHexString(layout.hashCode()));
			if (isTemplate)
				manipulatableLayout.formatEmptyTarget();
			}

		return(layout);
		}
	public static	boolean		partAndContainersAreVisible(MiPart part)
		{
		if (part.isVisible())
			{
			if ((part.getContainer(0) != null) && (part.getRootWindow() != null))
				{
				return(partAndContainersAreVisible(part.getContainer(0)));
				}
			return((part instanceof MiWindow) && (((MiWindow )part).isRootWindow()));
			}
		return(false);
		}

	public static	boolean		equals(String  one, String other, boolean ignoreCase)
		{
		if (one == null)
			{
			return(other == null);
			}
		if (other != null)
			{
			if (ignoreCase)
				return(one.equalsIgnoreCase(other));
			return(one.equals(other));
			}
		return(false);
		}
	}

