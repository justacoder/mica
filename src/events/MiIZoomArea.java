
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
public class MiIZoomArea extends MiEventHandler
	{
	public static final	String	Mi_ZOOMIN_PICK_UP_COMMAND_NAME	= "zoomInPickup";
	public static final	String	Mi_ZOOMIN_DRAG_EVENT_NAME	= "zoomInDrag";
	public static final	String	Mi_ZOOMIN_DROP_EVENT_NAME	= "zoomInDrop";
	public static final	String	Mi_ZOOMOUT_PICK_UP_COMMAND_NAME	= "zoomOutPickup";
	public static final	String	Mi_ZOOMOUT_DRAG_EVENT_NAME	= "zoomOutDrag";
	public static final	String	Mi_ZOOMOUT_DROP_EVENT_NAME	= "zoomOutDrop";
	private		MiBounds	originalWorld		= new MiBounds();

	private		MiIRubberbandBounds irect;
	private		MiRectangle	rect 			= new MiRectangle();


	public				MiIZoomArea()
		{
		addEventToCommandTranslation(Mi_ZOOMIN_PICK_UP_COMMAND_NAME, 
			Mi_MIDDLE_MOUSE_START_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_ZOOMIN_DRAG_EVENT_NAME, 
			Mi_MIDDLE_MOUSE_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_ZOOMIN_DROP_EVENT_NAME, 
			Mi_MIDDLE_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(Mi_ZOOMOUT_PICK_UP_COMMAND_NAME, 
			Mi_RIGHT_MOUSE_START_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_ZOOMOUT_DRAG_EVENT_NAME, 
			Mi_RIGHT_MOUSE_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_ZOOMOUT_DROP_EVENT_NAME, 
			Mi_RIGHT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);

		rect.setColor(MiColorManager.green);
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
		if ((isCommand(Mi_ZOOMIN_PICK_UP_COMMAND_NAME))
			|| (isCommand(Mi_ZOOMOUT_PICK_UP_COMMAND_NAME)))
			{
			event.editor.prependGrabEventHandler(this);
			event.editor.getWorldBounds(originalWorld);

			rect.setPoint(0, event.worldPt);
			rect.setPoint(1, event.worldPt);
			rect.setPickable(false);
			irect = new MiIRubberbandBounds();
			irect.setPart(rect);

			irect.processCommand(event, Mi_PICK_UP_COMMAND_NAME);
			}

		else if ((isCommand(Mi_ZOOMIN_DRAG_EVENT_NAME))
			|| (isCommand(Mi_ZOOMOUT_DRAG_EVENT_NAME)))
			{
			if (irect == null)
				return(Mi_PROPOGATE_EVENT);

			irect.processCommand(event, Mi_DRAG_COMMAND_NAME);
			}
		else if (isCommand(Mi_ZOOMIN_DROP_EVENT_NAME))
			{
			if (irect == null)
				return(Mi_PROPOGATE_EVENT);

			irect.processCommand(event, Mi_DROP_COMMAND_NAME);
			event.editor.removeGrabEventHandler(this);

			MiBounds world = rect.getBounds();
			event.editor.getViewport().confineProposedWorldToConstraints(world);
			event.editor.getViewport().enforceWorldAspectRatio(world);
			event.editor.setWorldBounds(world);

			MiSystem.getViewportTransactionManager().appendTransaction(
				new MiPanAndZoomCommand(event.editor, originalWorld, world));

			irect = null;
			}
		else if (isCommand(Mi_ZOOMOUT_DROP_EVENT_NAME))
			{
			if (irect == null)
				return(Mi_PROPOGATE_EVENT);

			irect.processCommand(event, Mi_DROP_COMMAND_NAME);
			event.editor.removeGrabEventHandler(this);

			MiBounds world = event.editor.getWorldBounds();
			MiBounds bounds = rect.getBounds();
			MiDistance width = world.getWidth() * world.getWidth()/bounds.getWidth();
			MiDistance height = world.getHeight() * world.getHeight()/bounds.getHeight();
	
			world.setWidth(width);
			world.setHeight(height);
			world.setCenter(bounds.getCenter());
			event.editor.setWorldBounds(world);

			MiSystem.getViewportTransactionManager().appendTransaction(
				new MiPanAndZoomCommand(event.editor, originalWorld, world));

			irect = null;
			}
		return(Mi_CONSUME_EVENT);
		}

	public		void		assignAttributesToRect(MiPart obj)
		{
		rect.setAttributes(obj.getAttributes());
		}
	}

