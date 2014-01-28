
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
public class MiICreateSmoothMultiPointObject extends MiICreateMultiPointObject
	{
	private		MiPart			prototypeShape 		= new MiLine();
	private		MiPoint			previousPoint		= new MiPoint();
	private		boolean			selectNewlyCreatedShape	= true;
	private		boolean			rubberStampMode;
	private		MiPart			line;
	private		int			pointNumber;
	private		MiDistance		minDistancePerSegmentInDevice = 10;
	private		MiDistance		minDistancePerSegmentSquared= 10;
	private		MiBounds		tmpBounds		= new MiBounds();
	private		MiPoint			currentPoint		= new MiPoint();



	public				MiICreateSmoothMultiPointObject()
		{
		addEventToCommandTranslation(Mi_RUBBER_STAMP_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_CANCEL_COMMAND_NAME, Mi_KEY_EVENT, Mi_ESC_KEY, 0);
		}

	public		void		setPrototypeShape(MiPart obj)
		{
		prototypeShape = obj;
		}
	public		MiPart		getPrototypeShape()
		{
		return(prototypeShape);
		}
	public		void		setInRubberStampMode(boolean flag)
		{
		rubberStampMode = flag;
		}
	public		boolean		isInRubberStampMode()
		{
		return(rubberStampMode);
		}
	public		void		setSelectNewlyCreatedShape(boolean flag)
		{
		selectNewlyCreatedShape = flag;
		}
	public		boolean		getSelectNewlyCreatedShape()
		{
		return(selectNewlyCreatedShape);
		}
	/**
	 * @param distance 	min distance between points in device coordinates (pixels)
	 *			allows user to zoom in for finer work
	 **/
	public		void		setMinDistancePerSegment(MiDistance distance)
		{
		minDistancePerSegmentInDevice = distance;
		}
	public		MiDistance	getMinDistancePerSegment()
		{
		return(minDistancePerSegmentInDevice);
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
			{
			event.editor.prependGrabEventHandler(this);

			line = prototypeShape.deepCopy();
			// Polygons have no points to start with
			line.setNumberOfPoints(1);
			event.editor.getCurrentLayer().appendPart(line);

			event.getWorldPoint(currentPoint);
			event.editor.snapMovingPoint(currentPoint);
			line.setPoint(0, currentPoint);

			pointNumber = 0;
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (line == null)
				return(Mi_PROPOGATE_EVENT);

			tmpBounds.setBounds(0, 0, minDistancePerSegmentInDevice, minDistancePerSegmentInDevice);
			event.getEditor().transformRootWorldToLocalWorld(tmpBounds);
//MiDebug.println("event.getEditor().getViewport()=" + event.getEditor().getViewport());
//MiDebug.println("minDistancePerSegmentInDevice=" + minDistancePerSegmentInDevice);
//MiDebug.println("minDistancePerSegmentInWorld=" + tmpBounds.getWidth());

			minDistancePerSegmentSquared = tmpBounds.getWidth() * tmpBounds.getWidth();
			event.getWorldPoint(currentPoint);
			line.getPoint(pointNumber, previousPoint);
			if (previousPoint.getDistanceSquared(currentPoint) >= minDistancePerSegmentSquared)
				{
				line.appendPoint(new MiPoint(currentPoint));
				++pointNumber;
				}
			}
		else if (isCommand(Mi_CANCEL_COMMAND_NAME))
			{
			if (line == null)
				return(Mi_PROPOGATE_EVENT);

			event.editor.getCurrentLayer().removePart(line);
			event.editor.removeGrabEventHandler(this);
			line = null;
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (line == null)
				return(Mi_PROPOGATE_EVENT);

			event.editor.removeGrabEventHandler(this);
			if (selectNewlyCreatedShape)
				event.editor.select(line);
			event.editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION, line);
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(event.editor, line, false));
			line = null;
			}
		else if (isCommand(Mi_RUBBER_STAMP_COMMAND_NAME))
			{
			if ((line != null) || (!rubberStampMode))
				return(Mi_PROPOGATE_EVENT);

			line = prototypeShape.copy();
			line.setCenter(event.getWorldPoint(new MiPoint()));
			event.editor.getCurrentLayer().appendPart(line);
			if (selectNewlyCreatedShape)
				event.editor.select(line);
			event.editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_RUBBER_STAMP_PART_ACTION, line);
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(event.editor, line, false));
			line = null;
			}
		return(Mi_CONSUME_EVENT);
		}
	}


