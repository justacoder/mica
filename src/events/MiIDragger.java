
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
class MiIDragger extends MiEventHandler implements MiiTypes
	{
	private		boolean		grabbed 			= false;
	private		int		movingOrthogonally 		= -1;
	private		int		orthoMovementEventModifiers 	= 0;
	private		MiCoord		fixedCoord;




	public				MiIDragger()
		{
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0,0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		}
	public 		void		setOrthogonalMovementEventModifiers(int modifiers)
		{
		int mask = 1;

		orthoMovementEventModifiers = modifiers;
		while (mask <= orthoMovementEventModifiers)
			{
			if ((orthoMovementEventModifiers & mask) != 0)
				{
				addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, 
					Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, mask);
				addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, 
					Mi_LEFT_MOUSE_DRAG_EVENT, 0, mask);
				addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
					Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
				}
			mask <<= 1;
			}
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
		int status = Mi_PROPOGATE_EVENT;
		MiiDraggable draggable = (MiiDraggable )getTarget();
		if (isCommand(Mi_PICK_UP_COMMAND_NAME))
			{
			if (!grabbed)
				{
				status = draggable.pickup(event);
				if (status == Mi_CONSUME_EVENT)
					{
					event.editor.prependGrabEventHandler(this);
					grabbed = true;
					}
				}
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (grabbed)
				{
				event = handleOrthogonalMovements(event);
				status = draggable.drag(event);
				}
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (grabbed)
				{
				status = draggable.drop(event);
				event.editor.removeGrabEventHandler(this);
				grabbed = false;
				movingOrthogonally = -1;
				}
			}
		return(status);
		}
	protected	MiEvent		handleOrthogonalMovements(MiEvent event)
		{
		if ((orthoMovementEventModifiers == 0)
			|| ((event.getModifiers() & orthoMovementEventModifiers) == 0))
			{
			return(event);
			}

		if (movingOrthogonally == -1)
			{
			if ((Math.abs(event.worldVector.x)) > (Math.abs(event.worldVector.y)))
				{
				movingOrthogonally = Mi_HORIZONTAL;
				fixedCoord = event.worldPt.y;
				}
			else
				{
				movingOrthogonally = Mi_VERTICAL;
				fixedCoord = event.worldPt.x;
				}
			}
		event = new MiEvent(event);
		if (movingOrthogonally == Mi_HORIZONTAL)
			{
			event.worldPt.y = fixedCoord;
			event.worldVector.y = 0;
			}
		else 
			{
			event.worldPt.x = fixedCoord;
			event.worldVector.x = 0;
			}
		return(event);
		}
	}

