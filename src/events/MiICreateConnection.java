
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
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiICreateConnection extends MiEventHandler implements MiiManagedPointValidator, MiiActionTypes, MiiDisplayNames
	{
	public	static final	String		Mi_START_NEW_LINE_SEGMENT_COMMAND_NAME	= "startNewLineSeg";

	private		MiConnection		prototypeConnection 	= new MiConnection();
	private		MiConnection		currentPrototypeConnection;
	private		MiConnectionJunctionPoint prototypeConnectionJunctionPoint = new MiConnectionJunctionPoint();
	private		int			srcConnPt		= MiiTypes.Mi_CENTER_LOCATION;
	private		MiPart			srcObj;
	private		MiEditor		srcEditor;
	private		MiConnection		line;
	private		MiPoint			previousPoint		= new MiPoint();
	private		MiDistance		minDistancePerSegmentSquared= -1;
	private		MiPoint			currentPoint		= new MiPoint();
	private		MiPoint			closestPoint		= new MiPoint();
	private		int			numberOfPointToModify;
	private		MiClosestValidManagedPointFinder connPtFinder;
	private		MiConnectionOperation 	connectOp;
	private		boolean		 	selectNewlyCreatedConnections;
	private		boolean		 	canConnectToConnections;
	private		boolean		 	onlyStartableAtConnections;
	private		boolean			ignoreConnectionsFromAConnectionToItself;
	private		boolean			replaceSectionsWithAnyConnectionsFromAConnectionToItself;
	private		boolean			snappingToMajorGridOnly;
	private		MiAttributes		lineSnappedToConnectPointAttributes;
	private		MiAttributes		lineExtendingAttributes;
	private		MiManagedPointValidator	managedPointValidator	= new MiManagedPointValidator();
	private		MiPoint			tmpPoint 		= new MiPoint();
	private		MiVector		tmpVector 		= new MiVector();
	private		MiBounds		tmpBounds 		= new MiBounds();
	private		MiBounds		tmpPickBounds 		= new MiBounds();




	public				MiICreateConnection()
		{
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_MIDDLE_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_MIDDLE_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_MIDDLE_MOUSE_UP_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_CANCEL_COMMAND_NAME, 
					Mi_KEY_PRESS_EVENT, Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(Mi_START_NEW_LINE_SEGMENT_COMMAND_NAME, Mi_KEY_PRESS_EVENT, ' ', 0);
		addEventToCommandTranslation(Mi_START_NEW_LINE_SEGMENT_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		connPtFinder = new MiClosestValidManagedPointFinder(this);
		connectOp = new MiConnectionOperation(prototypeConnection, null, null);
		managedPointValidator.setConnectionOperation(connectOp);
		}
	public		void		setPrototype(MiConnection conn)
		{
		prototypeConnection = conn;
		connectOp.setConnection(conn);
		}
	public		MiConnection	getPrototype()
		{
		return(prototypeConnection);
		}
	public		void		setValidConnPointFinder(MiClosestValidManagedPointFinder f)
		{
		connPtFinder = f;
		}
	public		MiClosestValidManagedPointFinder	getValidConnPointFinder()
		{
		return(connPtFinder);
		}
	public		void		setSelectNewlyCreatedConnections(boolean flag)
		{
		selectNewlyCreatedConnections = flag;
		}
	public		boolean		getSelectNewlyCreatedConnections()
		{
		return(selectNewlyCreatedConnections);
		}
	// Set this to >0 if a sequence of small line segments is to be created, tracing
	// the path of the mouse as it is dragged around the editor
	public		void		setMinDistancePerSegment(MiDistance distance)
		{
		minDistancePerSegmentSquared = distance * distance;
		}
	public		MiDistance	getMinDistancePerSegment()
		{
		return(Math.sqrt(minDistancePerSegmentSquared));
		}
	public		void		setSnapToConnectionPointDistance(MiDistance d)
		{
		connPtFinder.setPickAreaSize(d);
		}
	public		MiDistance	getSnapToConnectionPointDistance()
		{
		return(connPtFinder.getPickAreaSize());
		}
	public		void		setLineSnappedToConnectPointAttributes(MiAttributes atts)
		{
		lineSnappedToConnectPointAttributes = atts;
		}
	public		MiAttributes	getLineSnappedToConnectPointAttributes()
		{
		return(lineSnappedToConnectPointAttributes);
		}
	public		void		setConnectableToConnections(boolean flag)
		{
		canConnectToConnections = flag;
		}
	public		boolean		getConnectableToConnections()
		{
		return(canConnectToConnections);
		}
	public		void		setOnlyStartableAtConnections(boolean flag)
		{
		onlyStartableAtConnections = flag;
		}
	public		boolean		getOnlyStartableAtConnections()
		{
		return(onlyStartableAtConnections);
		}
	public		void		setSnappingToMajorGridOnly(boolean flag)
		{
		snappingToMajorGridOnly = flag;
		}
	public		boolean		isSnappingToMajorGridOnly()
		{
		return(snappingToMajorGridOnly);
		}
	/**
	 * Default is 'false'
	 **/
	public		void		setIgnoreConnectionsFromAConnectionToItself(boolean flag)
		{
		ignoreConnectionsFromAConnectionToItself = flag;
		}
	public		boolean		getIgnoreConnectionsFromAConnectionToItself()
		{
		return(ignoreConnectionsFromAConnectionToItself);
		}
	/**
	 * Default is 'false'
	 **/
	public		void		setReplaceSectionsWithAnyConnectionsFromAConnectionToItself(boolean flag)
		{
		replaceSectionsWithAnyConnectionsFromAConnectionToItself = flag;
		}
	public		boolean		getReplaceSectionsWithAnyConnectionsFromAConnectionToItself()
		{
		return(replaceSectionsWithAnyConnectionsFromAConnectionToItself);
		}

	public		void		setConnectionConnectionPointLook(MiPart look)
		{
		MiConnectionJunctionPoint.setConnectionPointLook(look);
		}
	public		void		setPrototypeConnectionJunctionPoint(MiConnectionJunctionPoint pt)
		{
		prototypeConnectionJunctionPoint = pt;
		}
					/**------------------------------------------------------
	 				 * Processes the command generated from the current event.
					 * Both are stored in the MiEventHandler super class.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			MiEventHandler#isCommand
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		if (isCommand(Mi_PICK_UP_COMMAND_NAME))
			return(pickup());
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			return(drag());
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			return(drop());
		else if (isCommand(Mi_CANCEL_COMMAND_NAME))
			return(cancel());
		else if (isCommand(Mi_START_NEW_LINE_SEGMENT_COMMAND_NAME))
			return(startNewLineSegment());

		return(Mi_CONSUME_EVENT);
		}
	private		int		pickup()
		{
		MiPoint 		pt;
		int			connPt;

//MiDebug.println(this + "onlyStartableAtConnections= " + onlyStartableAtConnections);
		if (onlyStartableAtConnections)
			{
			if ((snappingToMajorGridOnly) && (event.editor.getSnapManager() != null))
				{
				event.editor.getSnapManager().snap(event.worldPt, true);
				}
			else
				{
				event.editor.snapMovingPoint(event.worldPt);
				}
			if (!findClosestConnectionLineSegment(connPtFinder, event))
				{
				return(Mi_PROPOGATE_EVENT);
				}
			}
		else if (!connPtFinder.findClosestManagedPoint(
			event.editor, null, 0, null, 0, null, event.worldPt, false, true))
			{
			if (canConnectToConnections)
				{
				if (!findClosestConnectionLineSegment(connPtFinder, event))
					{
					return(Mi_PROPOGATE_EVENT);
					}
				}
			else
				{
				return(Mi_PROPOGATE_EVENT);
				}
			}

		srcObj = connPtFinder.closestObject;
		srcConnPt = connPtFinder.closestConnPtID;
		event.worldPt.x = connPtFinder.closestConnPt.x;
		event.worldPt.y = connPtFinder.closestConnPt.y;
//MiDebug.println(this + "found connPtFinder = " + connPtFinder);
//MiDebug.println(this + "found connpt at= " + srcObj);

		srcEditor = event.editor;

		currentPrototypeConnection = getPrototypeConnectionToUse(srcObj, event);

		// -----------------------------------
		// Do actual initialization of line
		// -----------------------------------
		//line = currentPrototypeConnection.getGraphics().deepCopy();
		line = (MiConnection )currentPrototypeConnection.deepCopy();
if (!(srcObj instanceof MiConnection))
{
		line.setSourceConnPt(srcConnPt);
		line.setSource(srcObj);
}
		line.validateLayout();
		line.setInvalidLayoutNotificationsEnabled(false);
		line.setAttributes(currentPrototypeConnection.getAttributes());
		line.setPoint(0, connPtFinder.closestConnPt);
		line.setPoint(-1, connPtFinder.closestConnPt);
		lineExtendingAttributes = line.getAttributes();

		lineSnappedToConnectPointAttributes = lineExtendingAttributes;
		lineSnappedToConnectPointAttributes 
			= lineSnappedToConnectPointAttributes.setColor(MiColorManager.green);
		lineSnappedToConnectPointAttributes 
			= lineSnappedToConnectPointAttributes.setBackgroundColor(MiColorManager.green);
		lineSnappedToConnectPointAttributes 
			= lineSnappedToConnectPointAttributes.setLineWidth(2);

		numberOfPointToModify = -1;

		connectOp.setConnection(line);

		if (minDistancePerSegmentSquared > 0)
			numberOfPointToModify = 0;

		currentPoint.copy(connPtFinder.closestConnPt);

		event.editor.appendAttachment(line);

		event.editor.prependGrabEventHandler(this);

		return(Mi_CONSUME_EVENT);
		}
	private		int		cancel()
		{
		if (line == null)
			return(Mi_PROPOGATE_EVENT);

		connectOp.setSource(srcObj);
		connectOp.setSourceConnPt(srcConnPt);
		connectOp.setDestination(null);
		srcObj.dispatchAction(Mi_DISCONNECT_ACTION, connectOp);

		srcEditor.removeAttachment(line);
		line.deleteSelf();

		srcEditor.dispatchAction(Mi_INTERACTIVELY_CANCELED_CREATE_CONNECTION_ACTION, line);

		line = null;
		event.editor.removeGrabEventHandler(this);
		return(Mi_CONSUME_EVENT);
		}

	private		int		startNewLineSegment()
		{
		if (line == null)
			return(Mi_PROPOGATE_EVENT);

		line.getGraphics().appendPoint(line.getPoint(numberOfPointToModify));

		if (line.getLayout() instanceof MiLConnectionLayout)
			{
			((MiLConnectionLayout )line.getLayout()).setNumberOfFixedPoints(
				((MiLConnectionLayout )line.getLayout()).getNumberOfFixedPoints() + 1);
			}
		return(Mi_CONSUME_EVENT);
		}

	private		int		drag()
		{
		if (line == null)
			{
			return(Mi_PROPOGATE_EVENT);
			}

		if (connPtFinder.findClosestManagedPoint(srcEditor, srcObj, srcConnPt, null, 0, 
			line, event.worldPt, prototypeConnection.getAllowSameSourceAndDestination(), false))
			{
			event.worldPt.x = connPtFinder.closestConnPt.x;
			event.worldPt.y = connPtFinder.closestConnPt.y;
			line.setAttributes(lineSnappedToConnectPointAttributes);
			}
		else
			{
			if ((line.getResource(MiSnapManager.Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE) != null) 
				&& (event.editor.getSnapManager() != null))
				{
				event.editor.getSnapManager().snap(event.worldPt, true);
				}
			else
				{
				event.editor.snapMovingPoint(event.worldPt);
				}
			if (findClosestConnectionLineSegment(connPtFinder, event))
				{
				event.worldPt.x = connPtFinder.closestConnPt.x;
				event.worldPt.y = connPtFinder.closestConnPt.y;
				line.setAttributes(lineSnappedToConnectPointAttributes);
				}
			else
				{
				line.setAttributes(lineExtendingAttributes);
				}
			}

		if (minDistancePerSegmentSquared > 0)
			{
			event.getWorldPoint(currentPoint);
			line.getPoint(numberOfPointToModify, previousPoint);
			if (previousPoint.getDistanceSquared(currentPoint) >= minDistancePerSegmentSquared)
				{
				line.getGraphics().appendPoint(new MiPoint(currentPoint));
				++numberOfPointToModify;
				}
			}
		else
			{
			line.getPoint(numberOfPointToModify, tmpPoint);
			tmpVector.x = event.worldPt.x - tmpPoint.x;
			tmpVector.y = event.worldPt.y - tmpPoint.y;
			tmpBounds.setBounds(event.worldPt);
			tmpBounds.translate(-tmpVector.x, -tmpVector.y);
			boolean modifiedDelta = event.editor.autopanForMovingObj(tmpBounds, tmpVector);

			line.setPoint(numberOfPointToModify, event.worldPt);
			}

		srcEditor.dispatchAction(Mi_INTERACTIVELY_CREATING_CONNECTION_ACTION, line);

		return(Mi_CONSUME_EVENT);
		}

	private		int		drop()
		{
		if (line == null)
			return(Mi_PROPOGATE_EVENT);

		event.editor.removeGrabEventHandler(this);

		line.setAttributes(lineExtendingAttributes);

		MiPart deleteDestSegmentConnectedToConnJunctionPt = null;
		MiPart destObj = null;
		int destConnPt = -1;

		boolean foundPtToConnectTo 
			= connPtFinder.findClosestManagedPoint(srcEditor, srcObj, srcConnPt, null, 0, 
				line, event.worldPt, prototypeConnection.getAllowSameSourceAndDestination(), false);
		if (!foundPtToConnectTo)
			{
			if (canConnectToConnections)
				{
				foundPtToConnectTo = findClosestConnectionLineSegment(connPtFinder, event);
				}
			}

		if (!foundPtToConnectTo)
			{
			if (prototypeConnection.getConnectionsMustBeConnectedAtBothEnds())
				{
				srcEditor.removeAttachment(line);
				line.deleteSelf();

				srcEditor.dispatchAction(
					Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION, line);
			
				line = null;

				return(Mi_CONSUME_EVENT);
				}
			destConnPt = MiiTypes.Mi_CENTER_LOCATION;
			}

		destObj = connPtFinder.closestObject;
		destConnPt = connPtFinder.closestConnPtID;
		event.worldPt.x = connPtFinder.closestConnPt.x;
		event.worldPt.y = connPtFinder.closestConnPt.y;

//MiDebug.println(this + "DROP found connPtFinder = " + connPtFinder);

		srcEditor.removeAttachment(line);


		if (!line.getAllowSameSourceAndDestinationPosition())
			{
			if (Utility.isZero(line.getPoint(0).getDistanceSquared(
				line.getPoint(MiiTypes.Mi_LAST_POINT_NUMBER))))
				{
				line.deleteSelf();

				srcEditor.dispatchAction(Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION, line);
					
				line = null;
				return(Mi_CONSUME_EVENT);
				}
			}
			

		connectOp.setSourceConnPt(srcConnPt);
		connectOp.setSource(srcObj);
		connectOp.setDestinationConnPt(destConnPt);
		connectOp.setDestination(destObj);

		if (!line.getAllowMultipleConnectionsBetweenSameNodesAndConnPts())
			{
			for (int i = 0; i < srcObj.getNumberOfConnections(); ++i)
				{
				MiConnection existingConn = srcObj.getConnection(i);
				if ((existingConn != line) 
					&& (existingConn.getSourceConnPt() == srcConnPt)
					&& (existingConn.getDestination() == destObj) 
					&& (existingConn.getDestinationConnPt() == destConnPt))
					{
					line.deleteSelf();

					srcEditor.dispatchAction(Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION, line);
					
					line = null;
					return(Mi_CONSUME_EVENT);
					}
				}
			}

		if (srcObj.dispatchAction(
			Mi_CONNECT_ACTION | Mi_EXECUTE_ACTION_PHASE, connectOp)
			!= MiiTypes.Mi_PROPOGATE)
			{
			line.deleteSelf();

			srcEditor.dispatchAction(Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION, line);
					
			line = null;
			return(Mi_CONSUME_EVENT);
			}
		if ((destObj != null)
			&& (destObj.dispatchAction(
				Mi_CONNECT_ACTION | Mi_EXECUTE_ACTION_PHASE, connectOp)
				!= MiiTypes.Mi_PROPOGATE))
			{
			line.deleteSelf();

			srcEditor.dispatchAction(Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION, line);

			line = null;
			return(Mi_CONSUME_EVENT);
			}


		MiConnection conn = line;

		/* ------------------------------------------------------------------------------
		 * If this is a new connection between two connections, and the two connections are the same
		 * connection (i.e. this is a new connection going from one place in a pre-existing connection to another
		 * place in the same pre-existing connection)... THEN EITHER:
		 * 1. Ignore it
		 * 2. Replace the section of pre-existing connection with this new connection
		 * 3. Keep both
		 * ------------------------------------------------------------------------------*/
		if ((srcObj instanceof MiConnection) && (srcConnPt == -1) && (destObj == srcObj) && (destConnPt == -1))
			{
			if (ignoreConnectionsFromAConnectionToItself)
				{
				line.deleteSelf();

				srcEditor.dispatchAction(Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION, line);

				line = null;
				return(Mi_CONSUME_EVENT);
				}
			}

		String transactionName = MiSystem.getProperty(Mi_CREATE_DISPLAY_NAME);
		if ((conn.getName() != null) && (conn.getName().length() > 0))
			{
			transactionName += " " + conn.getName();
			}
		else
			{
			transactionName += " " + "Connection";
			}
		MiNestedTransaction nestedTransaction = new MiNestedTransaction(transactionName);
		MiSystem.getTransactionManager().startTransaction(nestedTransaction);

//MiDebug.println(this + "DROP 111 conn = " + conn);
//MiDebug.println(this + "DROP srcConnPt = " + srcConnPt);
		/* ------------------------------------------------------------------------------
		 * For either of the two endpoints connected to a connection with an connPt == -1
		 * 1. divide the part (a connection) into 2 pieces and (ONLY FOR CERTAIN APPLICATIONS)
		 * 2. create a connection point at the common ends of all 3 connections and 
		 * 3. attach the conn pt to a pixel-sized 'invisible' dummy part
		 * 4. make this all part of one (undoable) transaction (
		 * 	bisectLineAndAddConnPt, join2LinesAndRemoveConnPt)
		 * ------------------------------------------------------------------------------*/
		if ((srcObj instanceof MiConnection) && (srcConnPt == -1))
			{
//MiDebug.println(this + "DROP src is connection ");
			MiConnection srcIsConnection = (MiConnection )srcObj;
			MiPoint pt = conn.getPoint(0);
			MiConnectionJunctionPoint connConnPt = (MiConnectionJunctionPoint )prototypeConnectionJunctionPoint.copy();
			connConnPt.setCenter(pt);

			srcIsConnection.dispatchAction(Mi_CREATED_CONNECTION_CONNECTION_POINT_ACTION, connConnPt);

			MiConnection segment1 = (MiConnection )srcIsConnection.copy();
//segment1.copy(srcIsConnection);
			MiConnection segment2 = (MiConnection )srcIsConnection.copy();

			if (segment1.getLayout() != null)
				{
				segment1.getLayout().setEnabled(false);
				}
			if (segment2.getLayout() != null)
				{
				segment2.getLayout().setEnabled(false);
				}

//MiDebug.println(this + "DROP was segment1 = " + segment1);
//MiDebug.println(this + "DROP was segment2 = " + segment2);
			splitConnectionsIntoTwoAtPoint(segment1, segment2, new MiBounds(pt));

//MiDebug.println(this + "DROP aaaa segment1 = " + segment1);
//MiDebug.println(this + "DROP aaaa segment2 = " + segment2);

			segment1.setSourceConnPt(srcIsConnection.getSourceConnPt());
			segment1.setSource(srcIsConnection.getSource());
			segment1.setDestinationConnPt(connConnPt.getConnectionPointNumber());
			segment1.setDestination(connConnPt);

			segment2.setDestinationConnPt(srcIsConnection.getDestinationConnPt());
			segment2.setDestination(srcIsConnection.getDestination());
			segment2.setSourceConnPt(connConnPt.getConnectionPointNumber());
			segment2.setSource(connConnPt);

			if (segment1.getLayout() != null)
				{
				segment1.getLayout().setEnabled(true);
				}
			if (segment2.getLayout() != null)
				{
				segment2.getLayout().setEnabled(true);
				}

			srcEditor.appendItem(segment1);
			srcEditor.appendItem(segment2);
			// If application did not add connConnPt to editor...
			if (connConnPt.getNumberOfContainers() == 0)
				{
				srcEditor.appendItem(connConnPt);
				}
//MiDebug.println(this + "DROP bbbb segment1 = " + segment1);
//MiDebug.println(this + "DROP bbbb segment2 = " + segment2);

			
//nestedTransaction.addElement(
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(srcEditor, srcObj, true));
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(srcEditor, connConnPt, false));
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(srcEditor, segment1, false));
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(srcEditor, segment2, false));

			srcObj.dispatchAction(Mi_DELETE_ACTION);
			srcObj.removeSelf();

			conn.setSourceConnPt(connConnPt.getConnectionPointNumber());
			conn.setSource(connConnPt);
//MiDebug.println(this + "DROP 111 conn = " + conn);
//MiDebug.println(this + "DROP 111 segment1 = " + segment1);
//MiDebug.println(this + "DROP 111 segment2 = " + segment2);
			if ((srcObj instanceof MiConnection) && (srcConnPt == -1) && (destObj == srcObj) && (destConnPt == -1))
				{
				// ----------------------------------------------------------------
				// Now, the dest is either in segment1 or segment2
				// ----------------------------------------------------------------
				pt = conn.getPoint(-1);
				// Don't really need this extra pick area distance....but...
				MiDistance pickAreaWidth = getSnapToConnectionPointDistance();
				tmpPickBounds.setXmin(pt.x - pickAreaWidth/2);
				tmpPickBounds.setYmin(pt.y - pickAreaWidth/2);
				tmpPickBounds.setXmax(pt.x + pickAreaWidth/2);
				tmpPickBounds.setYmax(pt.y + pickAreaWidth/2);

				if (segment1.pick(tmpPickBounds))
					{
					destObj = segment1;
					}
				else if (segment2.pick(tmpPickBounds))
					{
					destObj = segment2;
					}
				else
					{
//MiDebug.println(this + " ***Warning*** unable to find which connection segment to connect to");
					}

				if (replaceSectionsWithAnyConnectionsFromAConnectionToItself)
					{
					deleteDestSegmentConnectedToConnJunctionPt = connConnPt;
					}
				}
			}
		else
			{
			conn.setSourceConnPt(srcConnPt);
			conn.setSource(srcObj);
			}

		if ((destObj instanceof MiConnection) && (destConnPt == -1))
			{
			MiConnection destIsConnection = (MiConnection )destObj;
			MiPoint pt = conn.getPoint(-1);
			MiConnectionJunctionPoint connConnPt = (MiConnectionJunctionPoint )prototypeConnectionJunctionPoint.copy();
			connConnPt.setCenter(pt);
			//MiConnectionJunctionPoint connConnPt = new MiConnectionJunctionPoint(pt);

//MiDebug.println(this + "DROP destIsConnection = " + destIsConnection);
			destIsConnection.dispatchAction(Mi_CREATED_CONNECTION_CONNECTION_POINT_ACTION, connConnPt);

			MiConnection segment1 = (MiConnection )destIsConnection.copy();
			MiConnection segment2 = (MiConnection )destIsConnection.copy();

			if (segment1.getLayout() != null)
				{
				segment1.getLayout().setEnabled(false);
				}
			if (segment2.getLayout() != null)
				{
				segment2.getLayout().setEnabled(false);
				}

			splitConnectionsIntoTwoAtPoint(segment1, segment2, new MiBounds(pt));

			segment1.setSourceConnPt(destIsConnection.getSourceConnPt());
			segment1.setSource(destIsConnection.getSource());
			segment1.setDestinationConnPt(connConnPt.getConnectionPointNumber());
			segment1.setDestination(connConnPt);

			segment2.setDestinationConnPt(destIsConnection.getDestinationConnPt());
			segment2.setDestination(destIsConnection.getDestination());
			segment2.setSourceConnPt(connConnPt.getConnectionPointNumber());
			segment2.setSource(connConnPt);

			if (segment1.getLayout() != null)
				{
				segment1.getLayout().setEnabled(true);
				}
			if (segment2.getLayout() != null)
				{
				segment2.getLayout().setEnabled(true);
				}

			srcEditor.appendItem(segment1);
			srcEditor.appendItem(segment2);
			// If application did not add connConnPt to editor...
			if (connConnPt.getNumberOfContainers() == 0)
				{
				srcEditor.appendItem(connConnPt);
				}

			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(srcEditor, destObj, true));
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(srcEditor, connConnPt, false));
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(srcEditor, segment1, false));
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(srcEditor, segment2, false));

			destObj.dispatchAction(Mi_DELETE_ACTION);
			destObj.removeSelf();

			conn.setDestinationConnPt(connConnPt.getConnectionPointNumber());
			conn.setDestination(connConnPt);

			if (deleteDestSegmentConnectedToConnJunctionPt != null)
				{
				if (segment1.getSource() == deleteDestSegmentConnectedToConnJunctionPt)
					{
					segment1.dispatchAction(Mi_DELETE_ACTION);
					segment1.removeSelf();

					MiSystem.getTransactionManager().appendTransaction(
						new MiDeletePartsCommand(srcEditor, segment1, true));
					}
				else if (segment2.getDestination() == deleteDestSegmentConnectedToConnJunctionPt)
					{
					segment2.dispatchAction(Mi_DELETE_ACTION);
					segment2.removeSelf();

					MiSystem.getTransactionManager().appendTransaction(
						new MiDeletePartsCommand(srcEditor, segment2, true));
					}
				else
					{
//MiDebug.println(this + " ***Warning*** unable to find redundant connection segment to delete");
					}
				}
			}
		else
			{
//MiDebug.println(this + "DROP dest is pin " + destObj + ", destConnPt = " + destConnPt);
			conn.setDestinationConnPt(destConnPt);
			conn.setDestination(destObj);
//MiDebug.println(this + "DROP 222 conn = " + conn);
			}

		if (!srcEditor.getCurrentLayer().containsPart(conn))
			{
			srcEditor.appendItem(conn);
			}

		if (line.getLayout() instanceof MiLConnectionLayout)
			{
			((MiLConnectionLayout )line.getLayout()).setNumberOfFixedPoints(1);
			}

		line = null;

		if (selectNewlyCreatedConnections)
			srcEditor.select(conn);

		MiSystem.getTransactionManager().appendTransaction(
			new MiDeletePartsCommand(srcEditor, conn, false));

//MiDebug.println(this + "DROP 253535 conn = " + conn);
//MiDebug.dump(conn);
		MiSystem.getTransactionManager().commitTransaction(nestedTransaction);

//MiDebug.println(this + "DROP 333 conn = " + conn);
		srcEditor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_CREATE_CONNECTION_ACTION, conn);

//MiDebug.println(this + "DROP 444 conn = " + conn);
		return(Mi_CONSUME_EVENT);
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
	protected	MiConnection	getPrototypeConnectionToUse(MiPart target, MiEvent event)
		{
		return(prototypeConnection);
		}

	protected	boolean		findClosestConnectionLineSegment(
						MiClosestValidManagedPointFinder connPtFinder, 
						MiEvent event)
		{
		MiPoint mousePt = event.worldPt;
		MiDistance pickAreaWidth = getSnapToConnectionPointDistance();
		tmpPickBounds.setXmin(mousePt.x - pickAreaWidth/2);
		tmpPickBounds.setYmin(mousePt.y - pickAreaWidth/2);
		tmpPickBounds.setXmax(mousePt.x + pickAreaWidth/2);
		tmpPickBounds.setYmax(mousePt.y + pickAreaWidth/2);

		MiEditor editor = event.getEditor();
		for (int i = 0; i < editor.getCurrentLayer().getNumberOfParts(); ++i)
			{
			MiPart part = editor.getCurrentLayer().getPart(i);
			if (part instanceof MiConnection)
				{
				if (!part.getBounds(tmpBounds).intersects(tmpPickBounds))
					{
					continue;
					}

				part.getPoint(0, previousPoint);
				for (int j = 1; j < part.getNumberOfPoints(); ++j)
					{
					part.getPoint(j, currentPoint);
				
					// Find point on line whose normal line intersects the mouse pt.
					MiDistance distance = findClosestPointOnLineToAPoint(
						previousPoint, currentPoint, mousePt, closestPoint);
//System.out.println("distance = " + distance);
					if (distance < pickAreaWidth)
						{
						connPtFinder.closestObject = part;
						connPtFinder.closestConnPtID = -1;
						connPtFinder.closestConnPt.x = closestPoint.x;
						connPtFinder.closestConnPt.y = closestPoint.y;
//System.out.println("*Found* a line close by!!!!");
						return(true);
						}
					previousPoint.copy(currentPoint);
					}
				}
			}
//System.out.println("No line close by");
		return(false);
		}
	public		MiDistance 	findClosestPointOnLineToAPoint(
				MiPoint linePt1, MiPoint linePt2, MiPoint aPoint, MiPoint closestPtOnLine)
		{
		if (Utility.equals(linePt2.y, linePt1.y))
			{
			// Horizontal line
			MiCoord xmin = Math.min(linePt1.x, linePt2.x);
			MiCoord xmax = Math.max(linePt1.x, linePt2.x);
			if (aPoint.x < xmin)
				{
				closestPtOnLine.x = xmin;
				closestPtOnLine.y = linePt1.y;
				return(Math.sqrt((aPoint.x - xmin) * (aPoint.x - xmin) 
					+ (aPoint.y - linePt1.y) * (aPoint.y - linePt1.y)));
				}
			else if (aPoint.x > xmax)
				{
				closestPtOnLine.x = xmax;
				closestPtOnLine.y = linePt1.y;
				return(Math.sqrt((aPoint.x - xmax) * (aPoint.x - xmax) 
					+ (aPoint.y - linePt1.y) * (aPoint.y - linePt1.y)));
				}
			closestPtOnLine.x = aPoint.x;
			closestPtOnLine.y = linePt1.y;
			return(Math.abs(aPoint.y - linePt1.y));
			}
		if (Utility.equals(linePt2.x, linePt1.x))
			{
			// Vertical line
			MiCoord ymin = Math.min(linePt1.y, linePt2.y);
			MiCoord ymax = Math.max(linePt1.y, linePt2.y);
			if (aPoint.y < ymin)
				{
				closestPtOnLine.y = ymin;
				closestPtOnLine.x = linePt1.x;
				return(Math.sqrt((aPoint.y - ymin) * (aPoint.y - ymin) 
					+ (aPoint.x - linePt1.x) * (aPoint.x - linePt1.x)));
				}
			else if (aPoint.y > ymax)
				{
				closestPtOnLine.y = ymax;
				closestPtOnLine.x = linePt1.x;
				return(Math.sqrt((aPoint.y - ymax) * (aPoint.y - ymax) 
					+ (aPoint.x - linePt1.x) * (aPoint.x - linePt1.x)));
				}
			closestPtOnLine.y = aPoint.y;
			closestPtOnLine.x = linePt1.x;
			return(Math.abs(aPoint.x - linePt1.x));
			}

		// Based on a line being represented by y = mx + b. m is the slope
		double slopeOfLine = (linePt2.y - linePt1.y)/(linePt2.x - linePt1.x);
		MiDistance lineB = linePt1.y - linePt1.x * slopeOfLine;
		MiDistance orthogonalLineB = aPoint.y - aPoint.x / slopeOfLine;
		closestPtOnLine.x = (orthogonalLineB - lineB)/(slopeOfLine - 1/slopeOfLine);
		closestPtOnLine.y = slopeOfLine * closestPtOnLine.x + lineB;

		return(Math.sqrt(
			(aPoint.x - closestPtOnLine.x) * (aPoint.x - closestPtOnLine.x)
			 + (aPoint.y - closestPtOnLine.y) * (aPoint.y - closestPtOnLine.y)));
		}

	/**
	 * Assumes both connections follow same, full path upon entry. Assumes any connection layouts not enabled.
	 */
	public static	void		splitConnectionsIntoTwoAtPoint(MiConnection startOfPath, MiConnection restOfPath, MiBounds pointToSplitAt)
		{
		if (pointToSplitAt.getWidth() == 0)
			{
			pointToSplitAt.setSize(4, 4);
			}

//MiDebug.println("\nORIGINAL startOfPath = " + startOfPath);
//MiDebug.println("restOfPath = " + restOfPath);
//MiDebug.println("pointToSplitAt = " + pointToSplitAt);
		MiPoint pt1 = new MiPoint();
		MiPoint pt2 = new MiPoint();
		int numPointsOfStartOfPathToRemove = 0;
		for (int i = 1; i < restOfPath.getNumberOfPoints() - 1; ++i)
			{
			restOfPath.getPoint(i - 1, pt1);
			restOfPath.getPoint(i, pt2);
			if (pointToSplitAt.intersectsLine(pt1.x, pt1.y, pt2.x, pt2.y))
				{
				for (int j = 0; j < numPointsOfStartOfPathToRemove; ++j)
					{
					restOfPath.removePoint(1);
					}

				numPointsOfStartOfPathToRemove = startOfPath.getNumberOfPoints() - 1 - i;
//MiDebug.println("numPointsOfStartOfPathToRemove = " + numPointsOfStartOfPathToRemove);
				for (int j = 0; j < numPointsOfStartOfPathToRemove; ++j)
					{
					startOfPath.removePoint(i);
					}
				break;
				}

			++numPointsOfStartOfPathToRemove;
			}
//MiDebug.println("NOW startOfPath = " + startOfPath);
//MiDebug.println("restOfPath = " + restOfPath);
		}
					/**------------------------------------------------------
	 				 * Returns a copy of this MiEventHandler.
	 				 * @return 	 	the copy
					 * @implements		MiiEventHandler#copy
					 *------------------------------------------------------*/
	public		MiiEventHandler	copy()
		{
		MiICreateConnection handler = (MiICreateConnection )super.copy();
		handler.getValidConnPointFinder().setMethodology(getValidConnPointFinder().getMethodology());
		return(handler);
		}
		
	}
