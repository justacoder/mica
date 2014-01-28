
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
 * This class allows a user to drag a connection segment, maintaining the 
 * orthogonality of it's segments, and maintaining it's connectivity,
 * adding a segment if necessary.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIDragConnectionSegment extends MiEventHandler implements MiiActionTypes, MiiTypes
	{
	private		MiConnection		conn;

	private		MiVector		tmpVector		= new MiVector();
	private		MiPoint			tmpPoint		= new MiPoint();
	private		MiPoint			tmpPoint1		= new MiPoint();
	private		MiPoint			tmpPoint2		= new MiPoint();
	private		MiBounds		tmpBounds		= new MiBounds();
	private		int			segmentStartPointNumber;
	private		int			segmentEndPointNumber;
	private		int			maintainOrtho;

	private		boolean		 	selectNewlyDraggedConnections;
	private		boolean			disableLayoutDuringDrag	= true;
	private		boolean			originalLayoutNotifcationsEnabled;
	private		boolean			originalLayoutEnabled;
	private		MiModifyConnectionCommand	undoableCommand;




	public				MiIDragConnectionSegment()
		{
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_CANCEL_COMMAND_NAME, 
					Mi_KEY_PRESS_EVENT, Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN);
		}
	public		void		setSelectNewlyDraggedConnections(boolean flag)
		{
		selectNewlyDraggedConnections = flag;
		}
	public		boolean		getSelectNewlyDraggedConnections()
		{
		return(selectNewlyDraggedConnections);
		}
	/**
	 * default is 'true'
	 **/
	public		void		setDisableConnectionLayoutDuringDrag(boolean flag)
		{
		disableLayoutDuringDrag = flag;
		}
	public		boolean		getDisableConnectionLayoutDuringDrag()
		{
		return(disableLayoutDuringDrag);
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
//MiDebug.println(this + " look at obj: " + obj);
			//if (obj.isMovable()) // FIX: We do not check for movable, cause MiDragObjectUnderMouse
			// does this to make sure it does not drag connections...as do a number of other eventhandlers
			if (obj instanceof MiConnection)
				{
				conn = (MiConnection )obj;
//MiDebug.println(this + " foiund conn: " + conn);
				}
			}

		if (conn == null)
			{
			return(Mi_PROPOGATE_EVENT);
			}

		MiBounds area = event.getMouseFootPrint(tmpBounds);
		if (area.getWidth() < event.getEditor().getMinimumPickAreaSize())
			{
			area.setWidth(event.getEditor().getMinimumPickAreaSize());
			}
		if (area.getHeight() < event.getEditor().getMinimumPickAreaSize())
			{
			area.setHeight(event.getEditor().getMinimumPickAreaSize());
			}
//MiDebug.println(" now area : " + area);
		if (!conn.pick(area))
			{
			conn = null;
//MiDebug.println(this + " reject conn: " + conn);
			return(Mi_PROPOGATE_EVENT);
			}

		segmentStartPointNumber = Mi_INVALID_POINT_NUMBER;
		segmentEndPointNumber = Mi_INVALID_POINT_NUMBER;
		for (int i = 1; i < conn.getNumberOfPoints(); ++i)
			{
			conn.getPoint(i - 1, tmpPoint1);
			conn.getPoint(i, tmpPoint2) ;

			if (area.intersectsLine(
				tmpPoint1.x, tmpPoint1.y, tmpPoint2.x, tmpPoint2.y, conn.getLineWidth()))
				{
				segmentStartPointNumber = i - 1;
				segmentEndPointNumber = i;
				break;
				}
			}
		if (segmentStartPointNumber == Mi_INVALID_POINT_NUMBER)
			{
			conn = null;
//MiDebug.println(this + " reject conn invalid pt number: " + conn);
			return(Mi_PROPOGATE_EVENT);
			}


		// Insert new Connection Junction at point, and extend from it...

		undoableCommand = new MiModifyConnectionCommand();
		undoableCommand.setConnectionBefore(conn);

		if (disableLayoutDuringDrag)
			{
			originalLayoutNotifcationsEnabled = conn.getInvalidLayoutNotificationsEnabled();
			conn.setInvalidLayoutNotificationsEnabled(false);
			if (conn.getLayout() != null)
				{
				originalLayoutEnabled = conn.getLayout().isEnabled();
				conn.getLayout().setEnabled(false);
				}
			}

//MiDebug.println(this + "picking up segment with start point: " + segmentStartPointNumber + " , end point=" + segmentEndPointNumber);
//MiDebug.println("begin conn = " + conn);

		// Insert points/segments if trying to drag the first or last segment of a connected connection.
		if ((segmentStartPointNumber == 0) && (conn.getSource() != null))
			{
			conn.insertPoint(conn.getPoint(0), 1);
			segmentStartPointNumber = 1;
			++segmentEndPointNumber;
			}
		if (((segmentEndPointNumber == -1) || (segmentEndPointNumber >= conn.getNumberOfPoints() - 1))
			&& (conn.getDestination() != null))
			{
			conn.insertPoint(conn.getPoint(-1), conn.getNumberOfPoints() - 1);
			segmentEndPointNumber = conn.getNumberOfPoints() - 2;
			}
//MiDebug.println("now conn = " + conn);

		maintainOrtho = Mi_NONE;
//MiDebug.println(this + "conn.getMaintainOrthogonality() = " + conn.getMaintainOrthogonality());
//MiDebug.println(this + "conn.getLayout() = " + conn.getLayout());
		if ((conn.getMaintainOrthogonality()) 
			|| (conn.getLayout() instanceof MiZConnectionLayout)
			|| (conn.getLayout() instanceof MiLConnectionLayout)
			|| (conn.getLayout() instanceof MiLConnectionLayout2))
			{
			if (com.swfm.mica.util.Utility.areEqual(
				conn.getPoint(segmentStartPointNumber).x, conn.getPoint(segmentEndPointNumber).x))
				{
				maintainOrtho = Mi_VERTICAL;
				}
			else if (com.swfm.mica.util.Utility.areEqual(
				conn.getPoint(segmentStartPointNumber).y, conn.getPoint(segmentEndPointNumber).y))
				{
				maintainOrtho = Mi_HORIZONTAL;
				}
			}

//MiDebug.println(this + "picked up segment with start point: " + segmentStartPointNumber + " , end point=" + segmentEndPointNumber);

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
			{
			return(Mi_PROPOGATE_EVENT);
			}

//MiDebug.println(this + "drag segment with start point: " + segmentStartPointNumber + " , end point=" + segmentEndPointNumber);
		MiPoint mouse = event.getWorldPoint(tmpPoint);

//MiDebug.println("mouse = " + mouse);
		conn.getPoint(segmentStartPointNumber, tmpPoint1);
		conn.getPoint(segmentEndPointNumber, tmpPoint2);

		MiPoint segmentStartPoint = tmpPoint1;
		MiPoint segmentEndPoint = tmpPoint2;
//MiDebug.println("segmentStartPoint = " + segmentStartPoint);
//MiDebug.println("conn = " + conn);
//MiDebug.println(this + "maintainOrtho = " + maintainOrtho);

		MiVector translation = tmpVector;
		if (maintainOrtho == Mi_VERTICAL)
			{
//MiDebug.println("Mi_VERTICAL ,  mouse.x = " + mouse.x  + ", segmentStartPoint.x = " + segmentStartPoint.x);
			translation.x = mouse.x - segmentStartPoint.x;
			translation.y = 0;
			}
		else if (maintainOrtho == Mi_HORIZONTAL)
			{
			translation.x = 0;
			translation.y = mouse.y - segmentStartPoint.y;
			}
		else
			{
			translation.x = mouse.x - segmentStartPoint.x;
			translation.y = mouse.y - segmentStartPoint.y;
			}
//MiDebug.println(this + "drag segment translation=" + translation);

		tmpBounds.reverse();
		tmpBounds.union(segmentStartPoint);
		tmpBounds.union(segmentEndPoint);

		boolean modifiedTranslation = event.editor.autopanForMovingObj(tmpBounds, translation);

//MiDebug.println(this + "FIANNLY drag segment translation=" + translation);
		segmentStartPoint.translate(translation);
		segmentEndPoint.translate(translation);
		
		if ((conn.getResource(MiSnapManager.Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE) != null) 
			&& (event.editor.getSnapManager() != null))
			{
			event.editor.getSnapManager().snap(segmentStartPoint, true);
			event.editor.getSnapManager().snap(segmentEndPoint, true);
			}
		else
			{
			event.getEditor().snapMovingPoint(segmentStartPoint);
			event.getEditor().snapMovingPoint(segmentEndPoint);
			}

//MiDebug.println(this + "FIANNLY drag segment segmentStartPoint=" + segmentStartPoint);
		conn.setPoint(segmentStartPointNumber, segmentStartPoint);
		conn.setPoint(segmentEndPointNumber, segmentEndPoint);
//MiDebug.println("FINALLY conn = " + conn);

		event.editor.dispatchAction(Mi_INTERACTIVELY_MOVING_PART_ACTION, conn);

		return(Mi_CONSUME_EVENT);
		}

	private		int		drop()
		{
		if (conn == null)
			return(Mi_PROPOGATE_EVENT);

		event.editor.removeGrabEventHandler(this);

		if (disableLayoutDuringDrag)
			{
			conn.setInvalidLayoutNotificationsEnabled(originalLayoutNotifcationsEnabled);
			if (conn.getLayout() != null)
				{
				conn.getLayout().setEnabled(originalLayoutEnabled);
				}
			}
		conn.invalidateLayout();
		conn.validateLayout();

		undoableCommand.setConnectionAfter(conn);
		undoableCommand.processModification(event.editor);

		if (selectNewlyDraggedConnections)
			{
			event.editor.select(conn);
			}

		event.editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_MOVE_PART_ACTION, conn);

		conn = null;
		return(Mi_CONSUME_EVENT);
		}
	protected	void		restoreConnection()
		{
		undoableCommand.undo();
		}
	}

