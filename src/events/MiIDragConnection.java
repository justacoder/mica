
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
 * This class allows a user to drag a connection like it was an ordinary part, 
 * disconnecting it from both endpoints and, potentially, connecting it to 
 * different ones when dropped,
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIDragConnection extends MiEventHandler implements MiiManagedPointValidator, MiiActionTypes
	{
	private		int			srcConnPt		= MiiTypes.Mi_CENTER_LOCATION;
	private		MiPart			srcObj;
	private		int			destConnPt		= MiiTypes.Mi_CENTER_LOCATION;
	private		MiPart			destObj;

	private		MiPart			origSrc;
	private		MiPart			origDest;
	private		int			origSrcConnPt;
	private		int			origDestConnPt;

	private		MiConnection		conn;

	private		MiVector		tmpVector		= new MiVector();
	private		MiBounds		tmpBounds		= new MiBounds();

	private		MiClosestValidManagedPointFinder connPtFinder;
	private		MiConnectionOperation 	connectOp;
	private		boolean		 	selectNewlyDraggedConnections;
	private		MiManagedPointValidator	managedPointValidator	= new MiManagedPointValidator();
	private		MiModifyConnectionCommand	undoableCommand;




	public				MiIDragConnection()
		{
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_CANCEL_COMMAND_NAME, 
					Mi_KEY_PRESS_EVENT, Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN);

		connPtFinder = new MiClosestValidManagedPointFinder(this);
		connectOp = new MiConnectionOperation(null, null, null);
		managedPointValidator.setConnectionOperation(connectOp);
		}
	public		void		setValidConnPointFinder(MiClosestValidManagedPointFinder f)
		{
		connPtFinder = f;
		}
	public		MiClosestValidManagedPointFinder	getValidConnPointFinder()
		{
		return(connPtFinder);
		}
	public		void		setSelectNewlyDraggedConnections(boolean flag)
		{
		selectNewlyDraggedConnections = flag;
		}
	public		boolean		getSelectNewlyDraggedConnections()
		{
		return(selectNewlyDraggedConnections);
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

		return(Mi_CONSUME_EVENT);
		}
	private		int		pickup()
		{
		if (conn != null)
			return(Mi_PROPOGATE_EVENT);

		MiParts targetPath = event.getTargetPath();
		int index = targetPath.indexOf(event.editor.getCurrentLayer());
		if (index > 0)
			{
			MiPart obj = targetPath.elementAt(index - 1);
			//if (obj.isMovable()) // FIX: We do not check for movable, cause MiDragObjectUnderMouse
			// does this to make sure it does not drag connections...as do a number of other eventhandlers
			if (obj instanceof MiConnection)
				{
				conn = (MiConnection )obj;
				}
			}

		if (conn == null)
			return(Mi_PROPOGATE_EVENT);

		//conn = (MiConnection )getTarget();

		if (!conn.pick(event.getMouseFootPrint(tmpBounds)))
			{
			conn = null;
			return(Mi_PROPOGATE_EVENT);
			}

		undoableCommand = new MiModifyConnectionCommand();
		undoableCommand.setConnectionBefore(conn);

		origSrc = conn.getSource();
		origSrcConnPt = conn.getSourceConnPt();
		origDest = conn.getDestination();
		origDestConnPt = conn.getDestinationConnPt();

		srcObj = null;
		destObj = null;

		conn.setSource(null);
		conn.setDestination(null);

		event.editor.prependGrabEventHandler(this);
		return(Mi_CONSUME_EVENT);
		}

	private		int		cancel()
		{
		if (conn == null)
			return(Mi_PROPOGATE_EVENT);

		restoreConnection();
		event.editor.removeGrabEventHandler(this);
		conn = null;
		return(Mi_CONSUME_EVENT);
		}

	private		int		drag()
		{
		if (conn == null)
			return(Mi_PROPOGATE_EVENT);

		conn.translate(event.getWorldVector(tmpVector));

		return(Mi_CONSUME_EVENT);
		}

	private		int		drop()
		{
		if (conn == null)
			return(Mi_PROPOGATE_EVENT);

		event.editor.removeGrabEventHandler(this);

		// Check to see how close the source side of the conn is to a valid conn pt
		MiPoint srcConnPtLocation = null;
		if (connPtFinder.findClosestManagedPoint(event.editor, null, 0, null, 0, null, 
			conn.getPoint(0), false, true))
			{
			srcObj = connPtFinder.closestObject;
			srcConnPt = connPtFinder.closestConnPtID;
			srcConnPtLocation = new MiPoint(connPtFinder.closestConnPt);
			}
		
		MiPoint destConnPtLocation = null;
		if (connPtFinder.findClosestManagedPoint(event.editor, null, 0, null, 0, null, 
			conn.getPoint(-1), false, false))
			{
			destObj = connPtFinder.closestObject;
			destConnPt = connPtFinder.closestConnPtID;
			destConnPtLocation = new MiPoint(connPtFinder.closestConnPt);
			}

		double srcDistSquared = MiiTypes.Mi_MAX_DISTANCE_VALUE;
		if (srcObj != null)
			srcDistSquared = srcConnPtLocation.getDistanceSquared(conn.getPoint(0));

		double destDistSquared = MiiTypes.Mi_MAX_DISTANCE_VALUE;
		if (destObj != null)
			destDistSquared = destConnPtLocation.getDistanceSquared(conn.getPoint(-1));

		if ((srcObj == null) && (destObj == null))
			{
			if (conn.getConnectionsMustBeConnectedAtBothEnds())
				{
				restoreConnection();
				conn = null;
				return(Mi_CONSUME_EVENT);
				}
			}
		else if (srcDistSquared < destDistSquared)
			{
			destObj = null;
			destConnPt = MiiTypes.Mi_CENTER_LOCATION;
			}
		else
			{
			srcObj = null;
			srcConnPt = MiiTypes.Mi_CENTER_LOCATION;
			}

		connectOp.setConnection(conn);
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

			if (selectNewlyDraggedConnections)
				event.editor.select(conn);

			conn = null;
			return(Mi_CONSUME_EVENT);
			}
		if ((destObj != null)
			&& (destObj.dispatchAction(
				Mi_CONNECT_ACTION | Mi_EXECUTE_ACTION_PHASE, connectOp)
				!= MiiTypes.Mi_PROPOGATE))
			{
			undoableCommand.setConnectionAfter(conn);
			undoableCommand.processModification(event.editor);

			if (selectNewlyDraggedConnections)
				event.editor.select(conn);

			conn = null;
			return(Mi_CONSUME_EVENT);
			}

		conn.setSource(srcObj);
		conn.setDestination(destObj);
		conn.setSourceConnPt(srcConnPt);
		conn.setDestinationConnPt(destConnPt);

		undoableCommand.setConnectionAfter(conn);
		undoableCommand.processModification(event.editor);

		if (selectNewlyDraggedConnections)
			event.editor.select(conn);

		conn = null;
		return(Mi_CONSUME_EVENT);
		}
	protected	void		restoreConnection()
		{
		conn.setSource(origSrc);
		conn.setSourceConnPt(origSrcConnPt);
		conn.setDestination(origDest);
		conn.setDestinationConnPt(origDestConnPt);
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

