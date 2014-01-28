
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
import com.swfm.mica.util.Utility; 

/**
 * This class represents any and all connections in Mica. Connections are
 * usually represented by a line (or lines), possibly connected at one or
 * both ends to another graphics part. This connection will automatically
 * update itself when any part it is connected to moves, changes size, or
 * is deleted.
 * <p>
 * The ends of a connection connect (source or destination) to a MiPart 
 * and a particular connection point of that MiPart. Some connection points
 * are simple locations (such as Mi_CENTER_LOCATION, which is the default) 
 * and some are special locations managed by the MiConnectionPointManager 
 * of the MiPart.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiConnection extends MiPart implements MiiActionHandler
	{
	private	static	MiConnectionPointManager connectionPointManagerKind = new MiConnectionPointManager();
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private		String		type;
	private		MiPart		graphics 			= new MiLine();
	private		MiPart		source;
	private		MiPart		destination;
	//private		MiPart		sourceAnchor;
	//private		MiPart		destinationAnchor;
	private		int		srcConnPt			= Mi_CENTER_LOCATION;
	private		int		destConnPt			= Mi_CENTER_LOCATION;
	private		boolean		endPointsMayBeInDifferentEditors;
	private		boolean		truncateLineAtEndPointPartBoundries = true;
	private		boolean		refreshingEndPoints;

	private		boolean		mustBeConnectedAtBothEnds;
	private		boolean		isValidConnectionSource;
	private		boolean		isValidConnectionDestination;
	private		boolean		allowSameSourceAndDestination;
	private		boolean		allowMultipleConnectionsBetweenSameNodesAndConnPts = true;
	private		boolean		allowSameSourceAndDestinationPosition;
	private		boolean		moveAllPointsWhenConnectedToJustOneMovingNode;
	private		boolean		maintainOrthogonality;
	private		boolean		translatable;

	private		MiPoint		tmpPoint			= new MiPoint();
	private		MiPoint		tmpPoint2			= new MiPoint();
	private		MiPoint		tmpPoint3			= new MiPoint();
	private		MiBounds	tmpBounds			= new MiBounds();
	private		MiEditor	thisEditor;
	private		MiEditor	destEditor;
	private		MiEditor	srcEditor;




					/**------------------------------------------------------
	 				 * Constructs a new MiConnection of type: null.
					 *------------------------------------------------------*/
	public				MiConnection()
		{
		setInvalidLayoutNotificationsEnabled(false);
		setMovable(false);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiConnection of type: null between the 
					 * given source and destination part center points.
					 * @param src		the connection source
					 * @param dest		the connection destination
					 *------------------------------------------------------*/
	public				MiConnection(MiPart src, MiPart dest)
		{
		this(src, Mi_CENTER_LOCATION, dest, Mi_CENTER_LOCATION);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiConnection of type: null between the 
					 * given source and destination parts at the given connection
					 * points.
					 * @param src		the connection source
					 * @param srcConnPt	the source connection point to use
					 * @param dest		the connection destination
					 * @param destConnPt	the source connection point to use
					 *------------------------------------------------------*/
	public				MiConnection(MiPart src, int srcConnPt, MiPart dest, int destConnPt)
		{
		setInvalidLayoutNotificationsEnabled(false);
		setMovable(false);
		this.srcConnPt = srcConnPt;
		this.destConnPt = destConnPt;
		setSource(src);
		setDestination(dest);
		}

/*
	public		void		setSourceAnchor(MiPart anchor)
		{
		sourceAnchor = anchor;
		}
	public		void		setDesinationAnchor(MiPart anchor)
		{
		destinationAnchor = anchor;
		}
*/
					/**------------------------------------------------------
	 				 * Sets the part that this connection is coming from.
					 * @param src		the source part or null
					 *------------------------------------------------------*/
	public		void		setSource(MiPart src)
		{
		if (src == source)
			return;

		if (source != null)
			{
			source.removeTheConnection(this);
			if (srcConnPt == Mi_DEFAULT_LOCATION)
				source.removeActionHandlers(this);

			dispatchAction(Mi_DISCONNECT_SOURCE_ACTION);
			}
		if ((!allowSameSourceAndDestination) && ((src == destination) && (src != null)))
			{
			source = null;
			MiDebug.println(this + ": Connecting object: <" + src + "> to itself\n");
			return;
			}

		if (srcEditor != null)
			srcEditor.removeActionHandlers(this);

		if ((src == null) && (source != null) && (destination != null))
			dispatchAction(Mi_DISCONNECT_ACTION);

		source = src;
		srcEditor = null;
		if (source != null)
			{
			source.appendConnection(this);
			if (srcConnPt == Mi_DEFAULT_LOCATION)
				{
				source.appendActionHandler(this, 
					Mi_GEOMETRY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED);
				}
			dispatchAction(Mi_CONNECT_SOURCE_ACTION);
			refreshEndPoints();
			}
		if ((source != null) && (destination != null))
			dispatchAction(Mi_CONNECT_ACTION);
		}
					/**------------------------------------------------------
	 				 * Sets the part that this connection is going to.
					 * @param dest		the destination part or null
					 *------------------------------------------------------*/
	public		void		setDestination(MiPart dest)
		{
		if (dest == destination)
			return;

		if (destination != null)
			{
			destination.removeTheConnection(this);
			if (destConnPt == Mi_DEFAULT_LOCATION)
				destination.removeActionHandlers(this);

			dispatchAction(Mi_DISCONNECT_DESTINATION_ACTION);
			}
		if ((!allowSameSourceAndDestination) && ((dest == source) && (dest != null)))
			{
			destination = null;
			MiDebug.println(this + ": Connecting object: <" + dest + "> to itself\n");
			return;
			}
			

		if (destEditor != null)
			destEditor.removeActionHandlers(this);

		if ((dest == null) && (destination != null) && (source != null))
			dispatchAction(Mi_DISCONNECT_ACTION);

		destEditor = null;
		destination = dest;
		if (destination != null)
			{
			destination.appendConnection(this);
			if (destConnPt == Mi_DEFAULT_LOCATION)
				{
				destination.appendActionHandler(this, 
					Mi_GEOMETRY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED);
				}
			dispatchAction(Mi_CONNECT_DESTINATION_ACTION);
			refreshEndPoints();
			}
		if ((source != null) && (destination != null))
			dispatchAction(Mi_CONNECT_ACTION);
		}

	public		void		removeConnectionTo(MiPart obj)
		{
		if (obj == destination)
			setDestination(null);
		if (obj == source)
			setSource(null);
		}
					/**------------------------------------------------------
	 				 * Gets whether this connection is of the given type.
					 * @param t		the type to compare to or null
					 * @return		true if this connection is of type: t
					 *------------------------------------------------------*/
	public		boolean		isType(String t)
		{
		return(type == null ? (t == null) : (t == null) ? false : t.equals(type));
		}
					/**------------------------------------------------------
	 				 * Sets the type of this connection.
					 * @param t		the type or null
					 *------------------------------------------------------*/
	public		void		setType(String t)
		{
		type = t;
		}
					/**------------------------------------------------------
	 				 * Gets the type of this connection.
					 * @return 		the type or null
					 *------------------------------------------------------*/
	public		String		getType()
		{
		return(type);
		}
					/**------------------------------------------------------
	 				 * Gets the source of this connection.
					 * @return 		the source or null
					 *------------------------------------------------------*/
	public		MiPart		getSource()
		{
		return(source);
		}
					/**------------------------------------------------------
	 				 * Gets the destination of this connection.
					 * @return 		the destination or null
					 *------------------------------------------------------*/
	public		MiPart		getDestination()
		{
		return(destination);
		}
	public		void		setIsTranslatable(boolean flag)
		{
		translatable = flag;
		}
	public		boolean		isTranslatable()
		{
		return(translatable);
		}
					/**------------------------------------------------------
	 				 * Sets the location within the source to connect to. Valid
					 * connection points:
					 *  Mi_CENTER_LOCATION (the default)
					 *  Mi_LEFT_LOCATION
					 *  Mi_RIGHT_LOCATION
					 *  Mi_BOTTOM_LOCATION
					 *  Mi_TOP_LOCATION
					 *  Mi_LOWER_LEFT_LOCATION
					 *  Mi_LOWER_RIGHT_LOCATION
					 *  Mi_UPPER_LEFT_LOCATION
					 *  Mi_UPPER_RIGHT_LOCATION
					 *  Mi_OUTSIDE_LEFT_LOCATION
					 *  Mi_OUTSIDE_RIGHT_LOCATION
					 *  Mi_OUTSIDE_BOTTOM_LOCATION
					 *  Mi_OUTSIDE_TOP_LOCATION
					 *  Mi_WNW_LOCATION
					 *  Mi_WSW_LOCATION
					 *  Mi_ENE_LOCATION
					 *  Mi_ESE_LOCATION
					 *  Mi_NWN_LOCATION
					 *  Mi_NEN_LOCATION
					 *  Mi_SWS_LOCATION
					 *  Mi_SES_LOCATION
					 *  Mi_SURROUND_LOCATION
					 *  Mi_DEFAULT_LOCATION	
					 * The special location, Mi_DEFAULT_LOCATION, Specifies that
					 * this connection is to look at the connection manager of
					 * the source to see what to connect to, which may actually
					 * be to a part within the source part.
					 *
					 * @param pt		the connection pt
					 *------------------------------------------------------*/
	public		void		setSourceConnPt(int pt)
		{
		if (pt == srcConnPt)
			return;

		if ((srcConnPt == Mi_DEFAULT_LOCATION) && (source != null))
			source.removeActionHandlers(this);

		srcConnPt = pt; 

		// ---------------------------------------------------------------
		// Attach this as an action handler so that we will be informed when
		// the source, or any part in the source, changes location
		// so that we will update the graphics of this connection.
		// ---------------------------------------------------------------
		if ((srcConnPt == Mi_DEFAULT_LOCATION) && (source != null))
			{
			source.appendActionHandler(this, 
				Mi_GEOMETRY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED);
			}
		refreshEndPoints();
		}
					/**------------------------------------------------------
	 				 * Gets the location within the source to connect to.
					 * @return		the connection pt
					 * @see			#setSourceConnPt
					 *------------------------------------------------------*/
	public		int		getSourceConnPt()
		{
		return(srcConnPt);
		}
					/**------------------------------------------------------
	 				 * Sets the location within the destination to connect to. Valid
					 * connection points:
					 *  Mi_CENTER_LOCATION (the default)
					 *  Mi_LEFT_LOCATION
					 *  Mi_RIGHT_LOCATION
					 *  Mi_BOTTOM_LOCATION
					 *  Mi_TOP_LOCATION
					 *  Mi_LOWER_LEFT_LOCATION
					 *  Mi_LOWER_RIGHT_LOCATION
					 *  Mi_UPPER_LEFT_LOCATION
					 *  Mi_UPPER_RIGHT_LOCATION
					 *  Mi_OUTSIDE_LEFT_LOCATION
					 *  Mi_OUTSIDE_RIGHT_LOCATION
					 *  Mi_OUTSIDE_BOTTOM_LOCATION
					 *  Mi_OUTSIDE_TOP_LOCATION
					 *  Mi_WNW_LOCATION
					 *  Mi_WSW_LOCATION
					 *  Mi_ENE_LOCATION
					 *  Mi_ESE_LOCATION
					 *  Mi_NWN_LOCATION
					 *  Mi_NEN_LOCATION
					 *  Mi_SWS_LOCATION
					 *  Mi_SES_LOCATION
					 *  Mi_SURROUND_LOCATION
					 *  Mi_DEFAULT_LOCATION	
					 * The special location, Mi_DEFAULT_LOCATION, Specifies that
					 * this connection is to look at the connection manager of
					 * the destination to see what to connect to, which may actually
					 * be to a part within the destination part.
					 *
					 * @param pt		the connection pt
					 *------------------------------------------------------*/
	public		void		setDestinationConnPt(int pt)
		{
		if (pt == destConnPt)
			return;

		if ((destConnPt == Mi_DEFAULT_LOCATION) && (destination != null))
			destination.removeActionHandlers(this);

		destConnPt = pt; 

		// ---------------------------------------------------------------
		// Attach this as an action handler so that we will be informed when
		// the destination, or any part in the destination, changes location
		// so that we will update the graphics of this connection.
		// ---------------------------------------------------------------
		if ((destConnPt == Mi_DEFAULT_LOCATION) && (destination != null))
			{
			destination.appendActionHandler(this, 
				Mi_GEOMETRY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED);
			}
		refreshEndPoints();
		}
					/**------------------------------------------------------
	 				 * Gets the location within the destination to connect to.
					 * @return		the connection pt
					 * @see			#setDestinationConnPt
					 *------------------------------------------------------*/
	public		int		getDestinationConnPt()
		{
		return(destConnPt);
		}
					/**------------------------------------------------------
	 				 * Sets the part that is to represent the graphics of this
					 * connection (the default is MiLine).
					 * @param g		the part to draw between the source
					 *			and destination parts
					 *------------------------------------------------------*/
	public		void		setGraphics(MiPart g)
		{
		g.setPoint(0, graphics.getPoint(0));
		g.setPoint(-1, graphics.getPoint(-1));
		graphics = g;
		if (graphics instanceof MiMultiPointShape)
			{
			((MiMultiPointShape )graphics).setMaintainOrthogonality(getMaintainOrthogonality());
			}
		refreshEndPoints();
		invalidateArea();
		}
					/**------------------------------------------------------
	 				 * Gets the part that is to represent the graphics of this
					 * connection (the default is MiLine).
					 * @return 		the part to draw between the source
					 *			and destination parts
					 *------------------------------------------------------*/
	public		MiPart		getGraphics()
		{
		return(graphics);
		}
					/**------------------------------------------------------
	 				 * Sets whether that the source and destination parts may
					 * be in different editors (with different transforms).
					 * @param flag		true if in different editors
					 *------------------------------------------------------*/
	public		void		setEndPointsMayBeInDifferentEditors(boolean flag)
		{
		endPointsMayBeInDifferentEditors = flag;
		setDeeplyInvalidateAreas(flag);
		graphics.setDeeplyInvalidateAreas(flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether that the source and destination parts may
					 * be in different editors (with different transforms).
					 * @param flag		true if in different editors
					 *------------------------------------------------------*/
	public		boolean		getEndPointsMayBeInDifferentEditors()
		{
		return(endPointsMayBeInDifferentEditors);
		}
					/**------------------------------------------------------
	 				 * Sets whether that the graphics depicting this connection
					 * (typically a MiLine) should be truncated at the boundries
					 * of the soruce and destination parts. Alternatives are
					 * to just draw the connection on top of the part or to
					 * call MiContainer#setKeepConnectionsBelowNodes(true) on 
					 * the container of the end points and this connection.
					 * @param flag		true if to truncate
					 *------------------------------------------------------*/
	public		void		setTruncateLineAtEndPointPartBoundries(boolean flag)
		{
		truncateLineAtEndPointPartBoundries = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether that the graphics depicting this connection
					 * (typically a MiLine) should be truncated at the boundries
					 * of the soruce and destination parts. 
					 * @return 		true if to truncate
					 * @see			#setTruncateLineAtEndPointPartBoundries
					 *------------------------------------------------------*/
	public		boolean		getTruncateLineAtEndPointPartBoundries()
		{
		return(truncateLineAtEndPointPartBoundries);
		}
	public		void		setIsValidConnectionSource(boolean flag)
		{
		isValidConnectionSource = flag;
		}
					/**------------------------------------------------------
					 * Gets whether this connection is a valid source of the
					 * given connection. By default this returns false to prevent
					 * connection connecting to other connections.
					 *------------------------------------------------------*/
	public		boolean		isValidConnectionSource(MiConnectionOperation connectOp)
		{
		return(isValidConnectionSource);
		}
	public		void		setIsValidConnectionDestination(boolean flag)
		{
		isValidConnectionDestination = flag;
		}
					/**------------------------------------------------------
	 				 * Specifies whether the points in this shape should remain
					 * in their horizontal and vertical positionings. This will
					 * translate the neighbors of a translated point to maintain
					 * orthogonality.
	 				 * @param flag		true if translatePoint will maintain
					 *			orthogonality
					 *------------------------------------------------------*/
	public		void		setMaintainOrthogonality(boolean flag)
		{
		maintainOrthogonality = flag;
		if (graphics instanceof MiMultiPointShape)
			{
			((MiMultiPointShape )graphics).setMaintainOrthogonality(getMaintainOrthogonality());
			}
		}
					/**------------------------------------------------------
	 				 * Gets whether the points in this shape should remain
					 * in their horizontal and vertical positionings. This will
					 * translate the neighbors of a translated point to maintain
					 * orthogonality.
	 				 * @return 		true if translatePoint will maintain
					 *			orthogonality
					 *------------------------------------------------------*/
	public		boolean		getMaintainOrthogonality()
		{
		return(maintainOrthogonality);
		}
					/**------------------------------------------------------
					 * Gets whether this connection is a valid destination of the
					 * given connection. By default this returns false to prevent
					 * connection connecting to other connections.
					 *------------------------------------------------------*/
	public		boolean		isValidConnectionDestination(MiConnectionOperation connectOp)
		{
		return(isValidConnectionDestination);
		}
	public		void		setConnectionPointManager(MiConnectionPointManager m)
		{
		if ((m != null) && (getConnectionPointManager() == null))
			{
			if ((!isValidConnectionSource) && (!isValidConnectionDestination))
				{
				isValidConnectionSource = true;
				isValidConnectionDestination = true;
				}
			}
		super.setConnectionPointManager(m);
		}
			
	public		void		setConnectionsMustBeConnectedAtBothEnds(boolean flag)
		{
		mustBeConnectedAtBothEnds = flag;
		}
	public		boolean		getConnectionsMustBeConnectedAtBothEnds()
		{
		return(mustBeConnectedAtBothEnds);
		}

					/**
					 * Allows connections duplicating others, ignore differences in paths
					 * Responsibility of manipulators to check this and enforce this
					 * True by default
					 */
	public		void		setAllowMultipleConnectionsBetweenSameNodesAndConnPts(boolean flag)
		{
		allowMultipleConnectionsBetweenSameNodesAndConnPts = flag;
		}
	public		boolean		getAllowMultipleConnectionsBetweenSameNodesAndConnPts()
		{
		return(allowMultipleConnectionsBetweenSameNodesAndConnPts);
		}
	public		void		setAllowSameSourceAndDestination(boolean flag)
		{
		allowSameSourceAndDestination = flag;
		}
	public		boolean		getAllowSameSourceAndDestination()
		{
		return(allowSameSourceAndDestination);
		}
					/**
					 * Allows zero length connections and 'closed loop' connections.
					 * Responsibility of manipulators to check this and enforce this
					 * True by default
					 */
	public		void		setAllowSameSourceAndDestinationPosition(boolean flag)
		{
		allowSameSourceAndDestinationPosition = flag;
		}
	public		boolean		getAllowSameSourceAndDestinationPosition()
		{
		return(allowSameSourceAndDestinationPosition);
		}
	public		void		setMoveAllPointsWhenConnectedToJustOneMovingNode(boolean flag)
		{
		moveAllPointsWhenConnectedToJustOneMovingNode = flag;
		}
	public		boolean		getMoveAllPointsWhenConnectedToJustOneMovingNode()
		{
		return(moveAllPointsWhenConnectedToJustOneMovingNode);
		}
					/**------------------------------------------------------
	 				 * Gets the end point at the otehr side from the given end
					 * point (i.e. source or destination point).
					 * @param one 		either the source or destination
					 * @return		the other end point
					 *------------------------------------------------------*/
	public		MiPart		getOther(MiPart one)
		{
		if (one == source)
			return(destination);
		if (one == destination)
			return(source);
		
		throw new IllegalArgumentException(this 
			+ ": getOther parameter: <" + one + "> not found at either end of connection");
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
			setType(value);
		else if (name.equalsIgnoreCase(Mi_SOURCE_CONNECTION_POINT_NAME))
			setSourceConnPt(Utility.toInteger(value));
		else if (name.equalsIgnoreCase(Mi_DESTINATION_CONNECTION_POINT_NAME))
			setDestinationConnPt(Utility.toInteger(value));
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
			return(getType());
		else if (name.equalsIgnoreCase(Mi_SOURCE_CONNECTION_POINT_NAME))
			return("" + getSourceConnPt());
		else if (name.equalsIgnoreCase(Mi_DESTINATION_CONNECTION_POINT_NAME))
			return("" + getDestinationConnPt());
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

		propertyDescriptions = new MiPropertyDescriptions(super.getPropertyDescriptions());

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_CONNECTION_ID_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(
				Mi_SOURCE_CONNECTION_POINT_NAME, Mi_INTEGER_TYPE, "" + Mi_CENTER_LOCATION));
		propertyDescriptions.addElement(
			new MiPropertyDescription(
				Mi_DESTINATION_CONNECTION_POINT_NAME, Mi_INTEGER_TYPE, "" + Mi_CENTER_LOCATION));
		return(propertyDescriptions);
		}
					/**------------------------------------------------------
	 				 * Removes this MiPart from all of it's containers, parts
					 * and connections after doing the same to all of this 
					 * MiPart's parts. A Mi_DELETE_ACTION is generated and 
					 * dispatched to this MiPart immediately before it is
					 * deleted.
					 * @overrides		MiPart#deleteSelf
					 *------------------------------------------------------*/
	public		void		deleteSelf()
		{
		if (source != null)
			source.removeConnection(this);
		if (destination != null)
			destination.removeConnection(this);

		super.deleteSelf();
		}
					/**------------------------------------------------------
	 				 * Removes this MiPart from all of it's containers and
					 * detaches this MiPart from all of it's connections, who
					 * are also detached from their endpoints and from their
					 * containers.
					 *------------------------------------------------------*/
	public		void		removeSelf()
		{
		if (source != null)
			source.removeConnection(this);
		if (destination != null)
			destination.removeConnection(this);

		super.removeSelf();
		}
					/**------------------------------------------------------
	 				 * Replaces this MiPart with the given MiPart. This 
					 * includes putting the given part in the same location,
					 * at the same index in all of it's containers, and to take
					 * it's place in all of it's connections. A Mi_REPLACE_ACTION
                                         * is generated and dispatched to this MiPart (not the
					 * given part).
	 				 * @param other  	the part that will replace this one
					 *------------------------------------------------------*/
	public		void		replaceSelf(MiPart other)
		{
		if (other instanceof MiConnection)
			{
			MiConnection conn = (MiConnection )other;
			conn.setSource(source);
			conn.setDestination(destination);
			conn.setSourceConnPt(srcConnPt);
			conn.setDestinationConnPt(destConnPt);
			}
		super.replaceSelf(other);
		}
					/**------------------------------------------------------
	 				 * Draws this connection.
	 				 * @param renderer 	the renderer to use for drawing
					 *------------------------------------------------------*/
	public		void		render(MiRenderer renderer)
		{
		//4-29-01 if (((source != null) && (destination != null)) || (!mustBeConnectedAtBothEnds))
			{
			graphics.setAttributes(getAttributes());
			graphics.draw(renderer);
			}
		}
					/**------------------------------------------------------
	 				 * Gets whether the given area intersects the bounds of
					 * this MiPart.
	 				 * @param area	 	the area
	 				 * @return		true if the given area overlaps
					 *			the bounds of this MiPart.
					 *------------------------------------------------------*/
	public		boolean		pick(MiBounds area)
		{
		if (((source != null) && (destination != null)) || (!mustBeConnectedAtBothEnds))
			{
			if ((getAttachments() != null) && (getAttachments().pickObject(area) != null))
				return(true);
			graphics.setAttributes(getAttributes());
			return(graphics.pick(area));
			}
		return(false);
		}
					/**------------------------------------------------------
		 			 * Processes the given action.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 			false if it is NOT OK to send
					 *			action to the next action handler
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (moveAllPointsWhenConnectedToJustOneMovingNode)
			moveAllPointsWhenConnectedToJustOneMovingNode();

		refreshEndPoints();
		return(true);
		}
					/**------------------------------------------------------
	 				 * Called by the end point MiParts when they have moved
					 * or been resized so that this connection can reposition
					 * iself if necessary.
					 * @param node		the end point part that changed
					 *------------------------------------------------------*/
	public		void		nodeGeometryChanged(MiPart node)
		{
		if (moveAllPointsWhenConnectedToJustOneMovingNode)
			moveAllPointsWhenConnectedToJustOneMovingNode();

		refreshEndPoints();
		}
					/**------------------------------------------------------
					 * Updates the points(s) of this connection's graphics.
					 *------------------------------------------------------*/
	protected	void		refreshEndPoints()
		{
		if (refreshingEndPoints)
			return;

		refreshingEndPoints = true;

		layoutParts();
		refreshBounds();
		if (getAttachments() != null)
			{
			MiSize tmpSize = MiSize.newSize();
			tmpBounds.setSize(getAttachments().getPreferredSize(tmpSize));
			getAttachments().setBounds(tmpBounds);
			MiSize.freeSize(tmpSize);
			getAttachments().invalidateLayout();
			getAttachments().validateLayout();
			refreshBounds();
			}
		refreshingEndPoints = false;
		}
					/**------------------------------------------------------
	 				 * Gets the outer bounds of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
	 				 * @param b		the (returned) outer bounds
	 				 * @return 		the outer bounds
	 				 * @overrides 		MiPart#getBounds
					 *------------------------------------------------------*/
	public		MiBounds	getBounds(MiBounds b)
		{
		if (endPointsMayBeInDifferentEditors)
			{
			if ((super.getBounds(b).isReversed()) && (srcEditor != null))
				refreshEndPoints();
			}
		return(super.getBounds(b));
		}

					/**------------------------------------------------------
					 * Get the draw bounds of this MiPart. The draw bounds is
					 * the outer bounds plus the bounds of any shadows, 
					 * attachments, and margins.
					 * @param b 		the (returned) draw bounds
					 * @return 		the draw bounds
	 				 * @overrides 		MiPart#getDrawBounds
					 *------------------------------------------------------*/
	public		MiBounds	getDrawBounds(MiBounds b)
		{
		if (endPointsMayBeInDifferentEditors)
			{
			if ((super.getDrawBounds(b).isReversed()) && (srcEditor != null))
				refreshEndPoints();
			}
		return(super.getDrawBounds(b));
		}
	protected	void		moveAllPointsWhenConnectedToJustOneMovingNode()
		{
		if (((source == null) || (destination == null) 
			|| ((source == destination) && (source != null)))
			&& (!endPointsMayBeInDifferentEditors))
			{
			MiDistance tx = 0;
			MiDistance ty = 0;
			if (source != null)
				{
				// Source moved...
				MiManagedPointManager.getLocationOfManagedPoint(
					source, srcConnPt, tmpPoint,
					connectionPointManagerKind);

				graphics.getPoint(0, tmpPoint2);
				tx = tmpPoint.x - tmpPoint2.x;
				ty = tmpPoint.y - tmpPoint2.y;
				}
			else // if (destination != null)
				{
				MiManagedPointManager.getLocationOfManagedPoint(
					destination, destConnPt, tmpPoint,
					connectionPointManagerKind);

				graphics.getPoint(-1, tmpPoint2);
				tx = tmpPoint.x - tmpPoint2.x;
				ty = tmpPoint.y - tmpPoint2.y;
				}
			if ((tx != 0) || (ty != 0))
				{
				for (int i = 0; i < graphics.getNumberOfPoints(); ++i)
					{
					graphics.translatePoint(i, tx, ty);
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Realculates the outer bounds of this MiPart. Override 
					 * this, if desired, as it implements the core 
					 * functionality. The default implementation just returns
					 * the outer bounds.
					 * @param b 		the (returned) outer bounds
	 				 * @overrides 		MiPart#reCalcBounds
					 *------------------------------------------------------*/
	protected	void		reCalcBounds(MiBounds b)
		{
		graphics.setAttributes(getAttributes());
		if (((source != null) && (destination != null)) || (!mustBeConnectedAtBothEnds))
			{
			if (!endPointsMayBeInDifferentEditors)
				{
				if (source != null)
					{
					MiManagedPointManager.getLocationOfManagedPoint(
						source, srcConnPt, tmpPoint,
						connectionPointManagerKind);

					graphics.setPoint(0, tmpPoint);
					}
				if (destination != null)
					{
					MiManagedPointManager.getLocationOfManagedPoint(
						destination, destConnPt, tmpPoint,
						connectionPointManagerKind);

					graphics.setPoint(-1, tmpPoint);
					}

				if (truncateLineAtEndPointPartBoundries)
					{
					if (source != null)
						{
						graphics.getPoint(0, tmpPoint);
						if ((srcConnPt == Mi_CENTER_LOCATION)
							|| ((srcConnPt == Mi_DEFAULT_LOCATION) 
							&& (source.getBounds(tmpBounds).pointIsInside(
								tmpPoint, 1))))
							{
							graphics.getPoint(1, tmpPoint2);
							source.getIntersectionWithLine(
								tmpPoint, tmpPoint2, tmpPoint3);
							graphics.setPoint(0, tmpPoint3);
							}
						}
					if (destination != null)
						{
						graphics.getPoint(-1, tmpPoint);
						if ((destConnPt == Mi_CENTER_LOCATION)
							|| ((destConnPt == Mi_DEFAULT_LOCATION) 
							&& (destination.getBounds(tmpBounds).pointIsInside(
								tmpPoint, 1))))
							{
							graphics.getPoint(
								graphics.getNumberOfPoints() - 2, tmpPoint2);
							destination.getIntersectionWithLine(
								tmpPoint, tmpPoint2, tmpPoint3);
							graphics.setPoint(-1, tmpPoint3);
							}
						}
					}
				if ((getLineStartStyle() != Mi_NONE) || (getLineEndStyle() != Mi_NONE))
					{
					graphics.refreshBounds();
					// FIX: should this really be the bounds, and not the draw bounds?
					// This is to account for the size of any special line ends
					graphics.getDrawBounds(b);
					}
				else
					{
					graphics.reCalcBounds(b);
					}
				return;
				}

			if ((srcEditor == null) || (destEditor == null) || (thisEditor == null))
				{
				srcEditor = source.getContainingEditor();
				destEditor = destination.getContainingEditor();
				thisEditor = getContainingEditor();

				if ((srcEditor == null) || (destEditor == null) || (thisEditor == null))
					return;

				if ((srcEditor != destEditor) || (srcEditor != thisEditor))
					{
					if (srcEditor != thisEditor)
						{
						srcEditor.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
						srcEditor.appendActionHandler(this, Mi_GEOMETRY_CHANGE_ACTION);
						MiEditor tmpEditor = srcEditor;
						while ((tmpEditor.getNumberOfContainers() > 0)
							&& ((tmpEditor 
							= tmpEditor.getContainer(0).getContainingEditor()))
							!= thisEditor)
							{
							if (tmpEditor == null)
								break;
							tmpEditor.appendActionHandler(this, 
								Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
							tmpEditor.appendActionHandler(this, 
								Mi_GEOMETRY_CHANGE_ACTION);
							}
						}
					if ((destEditor != srcEditor) && (destEditor != thisEditor))
						{
						destEditor.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
						destEditor.appendActionHandler(this, Mi_GEOMETRY_CHANGE_ACTION);
						}
					}
				}
			MiManagedPointManager.getLocationOfManagedPoint(
							source, srcConnPt, tmpPoint,
							connectionPointManagerKind);
			if (srcEditor != thisEditor)
				{
				tmpBounds.setBounds(tmpPoint);
				srcEditor.transformToOtherEditorSpace(thisEditor, tmpBounds, tmpBounds);
				tmpBounds.getCenter(tmpPoint);
				}
			graphics.setPoint(0, tmpPoint);

			MiManagedPointManager.getLocationOfManagedPoint(
							destination, destConnPt, tmpPoint,
							connectionPointManagerKind);
			if (destEditor != thisEditor)
				{
				tmpBounds.setBounds(tmpPoint);
				destEditor.transformToOtherEditorSpace(thisEditor, tmpBounds, tmpBounds);
				tmpBounds.getCenter(tmpPoint);
				}
			graphics.setPoint(-1, tmpPoint);
			if (truncateLineAtEndPointPartBoundries)
				{
				if ((thisEditor == srcEditor) && (thisEditor == destEditor))
					{
					graphics.getPoint(0, tmpPoint);
					graphics.getPoint(1, tmpPoint2);
					source.getIntersectionWithLine(tmpPoint, tmpPoint2, tmpPoint3);
					graphics.setPoint(0, tmpPoint3);

					graphics.getPoint(-1, tmpPoint);
					graphics.getPoint(getNumberOfPoints() - 2, tmpPoint2);
					destination.getIntersectionWithLine(tmpPoint, tmpPoint2, tmpPoint3);
					graphics.setPoint(-1, tmpPoint3);
					}
				else 
					{
					// -------------------------------------------
					// Transform first point to srcEditor space
					// -------------------------------------------
					graphics.getPoint(0, tmpPoint);
					tmpBounds.setBounds(tmpPoint);
					thisEditor.transformToOtherEditorSpace(
						srcEditor, tmpBounds, tmpBounds);
					tmpBounds.getCenter(tmpPoint);

					// -------------------------------------------
					// Transform second point to srcEditor space
					// -------------------------------------------
					graphics.getPoint(1, tmpPoint2);
					tmpBounds.setBounds(tmpPoint2);
					thisEditor.transformToOtherEditorSpace(
						srcEditor, tmpBounds, tmpBounds);
					tmpBounds.getCenter(tmpPoint2);

					// -------------------------------------------
					// Get intersection with endPoint's extrema
					// -------------------------------------------
					source.getIntersectionWithLine(tmpPoint, tmpPoint2, tmpPoint3);

					// -------------------------------------------
					// Transform 1st point back to thisEditor space
					// and assign to line
					// -------------------------------------------
					tmpBounds.setBounds(tmpPoint3);
					srcEditor.transformToOtherEditorSpace(
						thisEditor, tmpBounds, tmpBounds);
					tmpBounds.getCenter(tmpPoint);
					graphics.setPoint(0, tmpPoint);



					// -------------------------------------------
					// Transform last point to destEditor space
					// -------------------------------------------
					graphics.getPoint(-1, tmpPoint);
					tmpBounds.setBounds(tmpPoint);
					thisEditor.transformToOtherEditorSpace(
						destEditor, tmpBounds, tmpBounds);
					tmpBounds.getCenter(tmpPoint);

					// -------------------------------------------
					// Transform second point to srcEditor space
					// -------------------------------------------
					graphics.getPoint(getNumberOfPoints() - 2, tmpPoint2);
					tmpBounds.setBounds(tmpPoint2);
					thisEditor.transformToOtherEditorSpace(
						destEditor, tmpBounds, tmpBounds);
					tmpBounds.getCenter(tmpPoint2);

					// -------------------------------------------
					// Get intersection with endPoint's extrema
					// -------------------------------------------
					destination.getIntersectionWithLine(tmpPoint, tmpPoint2, tmpPoint3);

					// -------------------------------------------
					// Transform last point back to thisEditor space
					// and assign to line
					// -------------------------------------------
					tmpBounds.setBounds(tmpPoint3);
					destEditor.transformToOtherEditorSpace(
						thisEditor, tmpBounds, tmpBounds);
					tmpBounds.getCenter(tmpPoint);
					graphics.setPoint(-1, tmpPoint);
					}
				}
			graphics.refreshBounds();
			// FIX: should this really be the bounds, and not the draw bounds?
			graphics.getDrawBounds(b);
			}
		else // Want some good bounds during interactive create, 4-29-01: if (!mustBeConnectedAtBothEnds)
			{
			graphics.refreshBounds();
			graphics.getDrawBounds(b);
			}
		}
					/**------------------------------------------------------
					 * Gets the shape of any shadow. Used by the shadow 
					 * renderers. This method returns MiiShadowRenderer.noShadowShape
					 * because when the shape associated with this is rendered
					 * _it_ will draw the shadow.
					 * @return		the shape
					 * @overrides		MiPart#getShadowShape
					 *------------------------------------------------------*/
	public		MiPart		getShadowShape()
		{
		return(MiiShadowRenderer.noShadowShape);
		}
					/**------------------------------------------------------
	 				 * Makes a manipulator for this MiPart. Override this,
                                         * if desired, as it implements the core functionality.
					 * The default behavior is to return an instance of the
					 * MiConnectionPointManipulator class.
	 				 * @return 		the manipulator
	 				 * @overrides 		MiPart#makeManipulator
					 *------------------------------------------------------*/
	public		MiiManipulator	makeManipulator()
		{
		if (getPrototypeManipulator() != null)
			{
			return(getPrototypeManipulator().create(this));
			}
		return(new MiConnectionPointManipulator(this));
		}
					/**------------------------------------------------------
					 * Gets the number of points that define the shape of 
					 * this MiPart. The default is 2, the lower left and upper
					 * right corners.
					 * This is here for the use of MiMultiPointManipulator.
					 * @return 		the number of points
					 *------------------------------------------------------*/
	public		int		getNumberOfPoints()
		{
		return(graphics.getNumberOfPoints());
		}
					/**------------------------------------------------------
					 * Sets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number. 
					 * This is here for the use of MiMultiPointManipulator.
					 * @param pointNum 	the number of the point
					 * @param x 		the new x coordinate of the point
					 * @param y 		the new y coordinate of the point
					 *------------------------------------------------------*/
	public		void		setPoint(int pointNum, MiCoord x, MiCoord y)
		{
		invalidateArea();
		graphics.setPoint(pointNum, x, y);
		refreshEndPoints();
		}
	public		void		translatePoint(int pointNum, MiCoord tx, MiCoord ty)
		{
		invalidateArea();
		graphics.translatePoint(pointNum, tx, ty);
		refreshEndPoints();
		}
					/**------------------------------------------------------
					 * Append another point to the points that define the
					 * shape of this MiPart. Override this, if desired, as it 
					 * implements the core functionality.
					 * @param x	 	the x coord of the point to be appended
					 * @param y	 	the y coord of the point to be appended
					 *------------------------------------------------------*/
	public		void		appendPoint(MiCoord x, MiCoord y)
		{
		invalidateArea();
		graphics.appendPoint(x, y);
		refreshEndPoints();
		}
					/**------------------------------------------------------
					 * Remove the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 *------------------------------------------------------*/
	public		void		removePoint(int pointNum)
		{
		invalidateArea();
		graphics.removePoint(pointNum);
		refreshEndPoints();
		}
					/**------------------------------------------------------
					 * Insert another point to the points that define the
					 * shape of this MiPart. Override this, if desired, as it 
					 * implements the core functionality.
					 * @param x	 	the x coord of the point to be appended
					 * @param y	 	the y coord of the point to be appended
					 * @param index	 	the index of the point to insert
					 *			this new point before
					 *------------------------------------------------------*/
	public		void		insertPoint(MiCoord x, MiCoord y, int index)
		{
		invalidateArea();
		graphics.insertPoint(x, y, index);
		refreshEndPoints();
		}
					/**------------------------------------------------------
					 * Gets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * This is here for the use of MiMultiPointManipulator.
					 * @param pointNum 	the number of the point
					 * @param point	 	the (returned) coordinates of the
					 *			point
					 *------------------------------------------------------*/
	public		void		getPoint(int pointNum, MiPoint point)
		{
		graphics.getPoint(pointNum, point);
		}
	public		void		translatePart(MiDistance x, MiDistance y)
		{
		if (Utility.areZero(x, y))
			return;

		if ((translatable) && ((source != null) || (destination != null)))
			{
			int startPt = 0;
			int endPt = graphics.getNumberOfPoints();
			if (source != null)
				{
				startPt += 1;
				}
			if (destination != null)
				{
				endPt -= 1;
				}
			for (int i = startPt; i < endPt; ++i)
				{
				graphics.translatePoint(i, x, y);
				}
			refreshBounds();
			geometryChanged();
			return;
			}

		graphics.translate(x, y);
		super.translate(x, y);
		}
	public		void		translate(MiDistance x, MiDistance y)
		{
		if (Utility.areZero(x, y))
			return;

		if ((source != null) || (destination != null))
			{
			if (!translatable)
				{
				return;
				}
			int startPt = 0;
			int endPt = graphics.getNumberOfPoints();
			if (source != null)
				{
				startPt += 1;
				}
			if (destination != null)
				{
				endPt -= 1;
				}
			for (int i = startPt; i < endPt; ++i)
				{
				graphics.translatePoint(i, x, y);
				}
			refreshBounds();
			geometryChanged();
			return;
			}


		graphics.translate(x, y);
		super.translate(x, y);
		}
					/**------------------------------------------------------
	 				 * Translate this MiPart as a part of a translated 
					 * container and all of the parts of this MiPart as well.
					 * Because the container has been officially translated
					 * this part does not need to invalidate areas or layouts
					 * (unless the layout is !isIndependantOfTargetPosition()).
	 				 * @param tx	 	the x translation
	 				 * @param ty	 	the y translation
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
					 *------------------------------------------------------*/
/**
	protected	void		translatePart(MiDistance tx, MiDistance ty)
		{
		if ((tx == 0) && (ty == 0))
			return;

		graphics.translatePart(tx, ty);
		super.translatePart(tx, ty);
		}
***/


					/**------------------------------------------------------
					 * Returns information about this MiConnection.
					 * @return		textual information (class name +
					 *			unique numerical id + name
					 *			+ source[connPt] + dest[connPt])
					 *------------------------------------------------------*/
	public		String		toString()
		{
		String srcConnPtName = null;
		if (srcConnPt != Mi_CENTER_LOCATION)
			{
			if ((srcConnPt >= Mi_MIN_COMMON_LOCATION) && (srcConnPt <= Mi_SES_LOCATION))
				srcConnPtName = MiiNames.locationNames[srcConnPt - Mi_MIN_COMMON_LOCATION];
			else if ((srcConnPt >= Mi_MIN_CUSTOM_LOCATION) && (srcConnPt <= Mi_MAX_CUSTOM_LOCATION))
				srcConnPtName = "Custom Point #" + srcConnPt;
			else
				srcConnPtName = "Point #" + srcConnPt;
			}

		String destConnPtName = null;
		if (destConnPt != Mi_CENTER_LOCATION)
			{
			if ((destConnPt >= Mi_MIN_COMMON_LOCATION) && (destConnPt <= Mi_SES_LOCATION))
				destConnPtName = MiiNames.locationNames[destConnPt - Mi_MIN_COMMON_LOCATION];
			else if ((destConnPt >= Mi_MIN_CUSTOM_LOCATION) && (destConnPt <= Mi_MAX_CUSTOM_LOCATION))
				destConnPtName = "Custom Point #" + destConnPt;
			else
				destConnPtName = "Point #" + destConnPt;
			}

		return(super.toString() 
			+ "[source = " + source 
			+ (srcConnPt != Mi_CENTER_LOCATION ? ("[" + srcConnPtName + "]") : "") 
			+ ", destination = " + destination 
			+ (destConnPt != Mi_CENTER_LOCATION ? ("[" + destConnPtName + "]") : "") + "]"
			+ (type != null ? ("(Type: " + type) : ""))
			+ "(graphics = " + graphics + ")";
		}
					/**------------------------------------------------------
	 				 * Copies the given MiPart. This MiPart will have the same
					 * attributes, bounds, resources, attachments, layouts,
					 * action handlers, and event handlers as the given MiPart. 
	 				 * @param obj 	 	the part to copy
					 * @see			MiPart#copy
					 * @see			MiPart#deepCopy
					 *------------------------------------------------------*/
	public		void		copy(MiPart obj)
		{
		super.copy(obj);

		MiConnection conn = (MiConnection )obj;
		graphics 				= conn.graphics.copy();
		srcConnPt				= conn.srcConnPt;
		destConnPt				= conn.destConnPt;
		type					= conn.type;
		endPointsMayBeInDifferentEditors	= conn.endPointsMayBeInDifferentEditors;
		truncateLineAtEndPointPartBoundries	= conn.truncateLineAtEndPointPartBoundries;
		moveAllPointsWhenConnectedToJustOneMovingNode = conn.moveAllPointsWhenConnectedToJustOneMovingNode;
		mustBeConnectedAtBothEnds		= conn.mustBeConnectedAtBothEnds;
		isValidConnectionSource			= conn.isValidConnectionSource;
		isValidConnectionDestination		= conn.isValidConnectionDestination;
		allowSameSourceAndDestination		= conn.allowSameSourceAndDestination;
		maintainOrthogonality 			= conn.maintainOrthogonality;
		allowMultipleConnectionsBetweenSameNodesAndConnPts = conn.allowMultipleConnectionsBetweenSameNodesAndConnPts;
		allowSameSourceAndDestinationPosition	= conn.allowSameSourceAndDestinationPosition;
		}
	}
class MiConnectionPointManipulator extends MiMultiPointManipulator implements MiiManagedPointValidator
	{
	private		MiConnection			conn;
	private		MiClosestValidManagedPointFinder 	connPtFinder;
	private		MiConnectionOperation 		connectOp;
	private		boolean		 		selectNewlyMovedConnections;
	private		MiPart				origSrcObj;
	private		MiPart				origDestObj;
	private		int				origSrcConnPt;
	private		int				origDestConnPt;
	private		boolean				movingSourcePoint;
	private		boolean				movingIntermediatePoint;
	private		MiPoint				tmpPoint1		= new MiPoint();
	private		MiPoint				tmpPoint2		= new MiPoint();
	private		MiManagedPointValidator		managedPointValidator	= new MiManagedPointValidator();
	private		MiModifyConnectionCommand	undoableCommand;
	private		int				draggedPointNumber	= Mi_INVALID_POINT_NUMBER;
	private		int				movingPointConstraint;
	private		MiPoint				connPtLocation		= new MiPoint();


	public				MiConnectionPointManipulator(MiPart subject)
		{
		super(subject);
		conn = (MiConnection )subject;

/* FIX: Add special event handler...
		addEventToCommandTranslation(Mi_CANCEL_COMMAND_NAME, 
					Mi_KEY_PRESS_EVENT, Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(StartNewLineSegmentEventName, Mi_KEY_PRESS_EVENT, ' ', 0);
*/

		connPtFinder = new MiClosestValidManagedPointFinder(this);
		connectOp = new MiConnectionOperation(conn, null, null);
		managedPointValidator.setConnectionOperation(connectOp);
		}

	public		MiiManipulator	create(MiPart subject)
		{
		return(new MiConnectionPointManipulator(subject));
		}
	public		void		setValidConnPointFinder(MiClosestValidManagedPointFinder f)
		{
		connPtFinder = f;
		}
	public		MiClosestValidManagedPointFinder	getValidConnPointFinder()
		{
		return(connPtFinder);
		}
	public		void		setSelectNewlyMovedConnections(boolean flag)
		{
		selectNewlyMovedConnections = flag;
		}
	public		boolean		getSelectNewlyMovedConnections()
		{
		return(selectNewlyMovedConnections);
		}

	public		int	 	pickup(MiEvent event)
		{
		int status = super.pickup(event);
		draggedPointNumber = getDraggedPointNumber();

		if (draggedPointNumber != Mi_INVALID_POINT_NUMBER)
			{
			undoableCommand = new MiModifyConnectionCommand();
			undoableCommand.setConnectionBefore(conn);
			if (draggedPointNumber == 0)
				{
				origSrcObj = conn.getSource();
				origSrcConnPt = conn.getSourceConnPt();
				conn.setSource(null);
				movingSourcePoint = true;
				}
			else if ((draggedPointNumber == -1) || (draggedPointNumber == conn.getNumberOfPoints() - 1))
				{
				origDestObj = conn.getDestination();
				origDestConnPt = conn.getDestinationConnPt();
				conn.setDestination(null);
				movingSourcePoint = false;
				}
			else
				{
				movingIntermediatePoint = true;
				if ((draggedPointNumber & Mi_MOVING_POINT_HORIZONTALLY_ONLY_MASK) != 0)
					{
					movingPointConstraint = Mi_HORIZONTAL;
					draggedPointNumber &= ~Mi_MOVING_POINT_HORIZONTALLY_ONLY_MASK;
					}
				else if ((draggedPointNumber & Mi_MOVING_POINT_VERTICALLY_ONLY_MASK) != 0)
					{
					movingPointConstraint = Mi_VERTICAL;
					draggedPointNumber &= ~Mi_MOVING_POINT_VERTICALLY_ONLY_MASK;
					}
				}
			}
		return(status);
		}
	public		int		drag(MiEvent event)
		{
		if (draggedPointNumber == Mi_INVALID_POINT_NUMBER)
			return(Mi_PROPOGATE_EVENT);

		connPtLocation.copy(event.worldPt);
		if ((conn.getResource(MiSnapManager.Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE) != null) 
			&& (event.editor.getSnapManager() != null))
			{
			event.editor.getSnapManager().snap(connPtLocation, true);
			}
		else
			{
			event.editor.snapMovingPoint(connPtLocation);
			}

		if (!movingIntermediatePoint)
			{
			if (connPtFinder.findClosestManagedPoint(event.editor, 
					conn.getSource(), conn.getSourceConnPt(), 
					conn.getDestination(), conn.getDestinationConnPt(), 
					conn, connPtLocation,
					conn.getAllowSameSourceAndDestination(), movingSourcePoint))
				{
				connPtLocation.x = connPtFinder.closestConnPt.x;
				connPtLocation.y = connPtFinder.closestConnPt.y;
				}
			}
			

		//if (conn.getLayout().isMaintainingOrthogonality()
		if (conn.getLayout() instanceof MiZConnectionLayout)
			{
			updateOrthogonalConnection(connPtLocation);
			return(Mi_CONSUME_EVENT);
			}

		if (movingPointConstraint == Mi_HORIZONTAL)
			{
			conn.getPoint(draggedPointNumber, tmpPoint1);
			connPtLocation.y = tmpPoint1.y;
			}
		else if (movingPointConstraint == Mi_VERTICAL)
			{
			conn.getPoint(draggedPointNumber, tmpPoint1);
			connPtLocation.x = tmpPoint1.x;
			}
		conn.setPoint(draggedPointNumber, connPtLocation);
		return(Mi_CONSUME_EVENT);
		}
	public		int		drop(MiEvent event)
		{
		if (draggedPointNumber == Mi_INVALID_POINT_NUMBER)
			return(Mi_PROPOGATE_EVENT);

		connPtLocation.copy(event.worldPt);
		if ((conn.getResource(MiSnapManager.Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE) != null) 
			&& (event.editor.getSnapManager() != null))
			{
			event.editor.getSnapManager().snap(connPtLocation, true);
			}
		else
			{
			event.editor.snapMovingPoint(connPtLocation);
			}

		if (movingIntermediatePoint)
			{
			if (conn.getLayout() instanceof MiZConnectionLayout)
				{
				updateOrthogonalConnection(connPtLocation);
			
				undoableCommand.setConnectionAfter(conn);
				undoableCommand.processModification(event.editor);
				return(Mi_CONSUME_EVENT);
				}
			if (movingPointConstraint == Mi_HORIZONTAL)
				{
				conn.getPoint(draggedPointNumber, tmpPoint1);
				connPtLocation.y = tmpPoint1.y;
				}
			else if (movingPointConstraint == Mi_VERTICAL)
				{
				conn.getPoint(draggedPointNumber, tmpPoint1);
				connPtLocation.x = tmpPoint1.x;
				}
			conn.setPoint(draggedPointNumber, connPtLocation);

			undoableCommand.setConnectionAfter(conn);
			undoableCommand.processModification(event.editor);

			if (selectNewlyMovedConnections)
				event.editor.select(conn);
			return(Mi_CONSUME_EVENT);
			}

		if (!connPtFinder.findClosestManagedPoint(event.editor, 
					conn.getSource(), conn.getSourceConnPt(), 
					conn.getDestination(), conn.getDestinationConnPt(),
					conn, connPtLocation,
					conn.getAllowSameSourceAndDestination(), movingSourcePoint))
			{
			if (conn.getConnectionsMustBeConnectedAtBothEnds())
				restoreConnection();
			setDraggedPointNumber(Mi_INVALID_POINT_NUMBER);
			return(Mi_CONSUME_EVENT);
			}

		connPtLocation.x = connPtFinder.closestConnPt.x;
		connPtLocation.y = connPtFinder.closestConnPt.y;

		setDraggedPointNumber(-1);

		MiPart srcObj;
		MiPart destObj;
		int srcConnPt;
		int destConnPt;
		if (movingSourcePoint)
			{
			srcObj = connPtFinder.closestObject;
			srcConnPt = connPtFinder.closestConnPtID;
			destObj = conn.getDestination();
			destConnPt = conn.getDestinationConnPt();
			}
		else
			{
			srcObj = conn.getSource();
			srcConnPt = conn.getSourceConnPt();
			destObj = connPtFinder.closestObject;
			destConnPt = connPtFinder.closestConnPtID;
			}

		connectOp.setSource(srcObj);
		connectOp.setDestination(destObj);
		connectOp.setSourceConnPt(srcConnPt);
		connectOp.setDestinationConnPt(destConnPt);

		if ((srcObj != null)
			&& (srcObj.dispatchAction(
				Mi_CONNECT_ACTION | Mi_EXECUTE_ACTION_PHASE, connectOp)
				!= MiiTypes.Mi_PROPOGATE))
			{
			undoableCommand.setConnectionAfter(conn);
			undoableCommand.processModification(event.editor);

			return(Mi_CONSUME_EVENT);
			}
		if ((destObj != null)
			&& (destObj.dispatchAction(
				Mi_CONNECT_ACTION | Mi_EXECUTE_ACTION_PHASE, connectOp)
				!= MiiTypes.Mi_PROPOGATE))
			{
			undoableCommand.setConnectionAfter(conn);
			undoableCommand.processModification(event.editor);

			return(Mi_CONSUME_EVENT);
			}

		conn.setSource(srcObj);
		conn.setDestination(destObj);
		conn.setSourceConnPt(srcConnPt);
		conn.setDestinationConnPt(destConnPt);

		undoableCommand.setConnectionAfter(conn);
		undoableCommand.processModification(event.editor);

		if (selectNewlyMovedConnections)
			event.editor.select(conn);

		return(Mi_CONSUME_EVENT);
		}
	protected	void		updateOrthogonalConnection(MiPoint worldPt)
		{
		int ptNum = draggedPointNumber;
		if (ptNum == -1)
			{
			ptNum = conn.getNumberOfPoints() - 1;
			}
			
//System.out.println("ptNum  = " + ptNum);
		conn.getPoint(ptNum, tmpPoint1);

		MiPoint movingPt = tmpPoint1;
		MiDistance tx = worldPt.x - movingPt.x;
		MiDistance ty = worldPt.y - movingPt.y;

		if (movingPointConstraint == Mi_HORIZONTAL)
			{
			ty = 0;
			}
		else if (movingPointConstraint == Mi_VERTICAL)
			{
			tx = 0;
			}

		// See if must change neighboring pts:
		if (ptNum > (conn.getSource() != null ? 1 : 0))
			{
			conn.getPoint(ptNum - 1, tmpPoint2);
			MiPoint neighborPt = tmpPoint2;
			if (movingPt.x == neighborPt.x)
				{
				conn.getGraphics().translatePoint(ptNum, tx, 0);
				conn.getGraphics().translatePoint(ptNum - 1, tx, 0);
				}
			else if (movingPt.y == neighborPt.y)
				{
				conn.getGraphics().translatePoint(ptNum, 0, ty);
				conn.getGraphics().translatePoint(ptNum - 1, 0, ty);
				}
			}
		if (ptNum < (conn.getNumberOfPoints() - (conn.getDestination() != null ? 2 : 1)))
			{
			conn.getPoint(ptNum + 1, tmpPoint2);
			MiPoint neighborPt = tmpPoint2;
			if (movingPt.x == neighborPt.x)
				{
				conn.getGraphics().translatePoint(ptNum, tx, 0);
				conn.getGraphics().translatePoint(ptNum + 1, tx, 0);
				}
			else if (movingPt.y == neighborPt.y)
				{
				conn.getGraphics().translatePoint(ptNum, 0, ty);
				conn.getGraphics().translatePoint(ptNum + 1, 0, ty);
				}
			}
		if ((ptNum == 0) || (ptNum == conn.getNumberOfPoints() - 1))
			{
			conn.setPoint(draggedPointNumber, worldPt);
			}
		else
			{
			conn.refreshEndPoints();
			}
		}
	protected	void	restoreConnection()
		{
		if (origSrcObj != null)
			{
			conn.setSource(origSrcObj);
			conn.setSourceConnPt(origSrcConnPt);
			}
		else 
			{
			conn.setDestination(origDestObj);
			conn.setDestinationConnPt(origDestConnPt);
			}
		}
	public		boolean		isValidConnectionSource(MiPart src, MiPart dest)
		{
		return(managedPointValidator.isValidConnectionSource(src, dest));
		}
	public		boolean		isValidConnectionSource(MiPart src, int srcConnPt, 
								MiPart dest, int destConnPt)
		{
		return(managedPointValidator.isValidConnectionSource(src, srcConnPt, dest, destConnPt));
		}

	public		boolean		isValidConnectionDestination(MiPart src, MiPart dest)
		{
		return(managedPointValidator.isValidConnectionDestination(src, dest));
		}
	public		boolean		isValidConnectionDestination(MiPart src, int srcConnPt, 
								MiPart dest, int destConnPt)
		{
		return(managedPointValidator.isValidConnectionDestination(src, srcConnPt, dest, destConnPt));
		}
	}

