
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
public class MiIRubberbandBounds extends MiEventHandler
	{
	private		boolean		snapStartPt	= true;
	private		boolean		origOptimizeInvalidAreasEnabled;
	private		MiPoint		startPt;
	private		MiPart		obj;
	private		MiBounds	newBounds	= new MiBounds();
	private		MiPoint		newPosition	= new MiPoint();
	private		MiBounds	lastPosition	= new MiBounds();
	private		MiVector	delta		= new MiVector();



	public				MiIRubberbandBounds()
		{
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
		if (obj == null)
			return(Mi_PROPOGATE_EVENT);

		if (isCommand(Mi_PICK_UP_COMMAND_NAME))
			{
			startPt = new MiPoint(event.worldPt);
			if (snapStartPt)
				event.editor.snapMovingPoint(startPt);
			event.editor.appendAttachment(obj);
			obj.getContainer(0).refreshBounds();
			origOptimizeInvalidAreasEnabled 
				= event.editor.getDrawManager().getOptimizeInvalidAreasEnabled();
			event.editor.getDrawManager().setOptimizeInvalidAreasEnabled(false);
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			newPosition.copy(event.worldPt);
			event.editor.snapMovingPoint(newPosition);

			delta.copy(event.worldVector);
			delta.add(newPosition.x - event.worldPt.x, newPosition.y - event.worldPt.y);
			lastPosition.setBounds(newPosition);
			lastPosition.translate(-delta.x, -delta.y);
			boolean modifiedDelta = event.editor.autopanForMovingObj(lastPosition, delta);

			lastPosition.translate(delta);

			newBounds.setBounds(
				startPt.x, startPt.y, 
				lastPosition.getCenterX(), lastPosition.getCenterY());
			newBounds.assureMinsLessThanMaxs();

			// Do not allow zero sized bounds...
			if ((newBounds.getWidth() > 0) && (newBounds.getHeight() > 0)
				&& (!obj.getBounds(lastPosition).equals(newBounds)))
				{
				obj.setBounds(newBounds);
				obj.getContainer(0).setInvalidAreaNotificationsEnabled(false);
				obj.getContainer(0).refreshBounds();
				obj.getContainer(0).validateLayout();
				obj.getContainer(0).setInvalidAreaNotificationsEnabled(true);
				obj.optimizedInvalidateDrawBoundsArea();
				}
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			event.editor.getDrawManager().setOptimizeInvalidAreasEnabled(
				origOptimizeInvalidAreasEnabled);
			event.editor.removeAttachment(obj);
			obj = null;
			}
		return(Mi_CONSUME_EVENT);
		}
	public		void		setPart(MiPart o)
		{
		obj = o;
		}
	}

