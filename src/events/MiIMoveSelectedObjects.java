
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
public class MiIMoveSelectedObjects extends MiIDragSelectedObjects
	{
	public static final String	Mi_MOVE_LEFT_COMMAND_NAME	= "MoveLeft";
	public static final String	Mi_MOVE_RIGHT_COMMAND_NAME	= "MoveRight";
	public static final String	Mi_MOVE_DOWN_COMMAND_NAME	= "MoveDown";
	public static final String	Mi_MOVE_UP_COMMAND_NAME		= "MoveUp";

	private		MiBounds	tmpBounds	= new MiBounds();
	private		MiVector	delta 		= new MiVector();
	private		MiVector	deviceDelta 	= new MiVector(10, 10);
	private		MiPoint		devicePt 	= new MiPoint();
	private		String		direction;



	public				MiIMoveSelectedObjects()
		{
		removeAllEventToCommandTranslations();

		addEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_LEFT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_RIGHT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_DOWN_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_DOWN_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_UP_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_UP_ARROW_KEY, 0);

		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
			Mi_KEY_RELEASE_EVENT, Mi_LEFT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
			Mi_KEY_RELEASE_EVENT, Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
			Mi_KEY_RELEASE_EVENT, Mi_DOWN_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
			Mi_KEY_RELEASE_EVENT, Mi_UP_ARROW_KEY, 0);

		addEventToCommandTranslation(Mi_REPEAT_COMMAND_NAME, 
			Mi_IDLE_EVENT, 0, 0);

		setToDragConnections(true);
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
		if ((isCommand(Mi_MOVE_LEFT_COMMAND_NAME))
			|| (isCommand(Mi_MOVE_RIGHT_COMMAND_NAME))
			|| (isCommand(Mi_MOVE_DOWN_COMMAND_NAME))
			|| (isCommand(Mi_MOVE_UP_COMMAND_NAME)))
			{
			direction = getCommand();
			if (draggedObj == null)
				{
				return(super.pickup());
				}
			return(drag());
			}
		if (draggedObj == null)
			{
			return(Mi_PROPOGATE_EVENT);
			}
		if (isCommand(Mi_REPEAT_COMMAND_NAME))
			{
			return(drag());
			}
		return(super.processCommand());
		}
	protected	int		drag()
		{
		if (draggedObj == null)
			{
			return(Mi_PROPOGATE_EVENT);
			}

		MiEditor editor = event.getEditor();
		delta.x = 0;
		delta.y = 0;
		if ((editor.getSnapManager() != null)
			&& (editor.getSnapManager().getGrid() != null))
			{
			MiDrawingGrid drawingGrid = editor.getSnapManager().getGrid();
			MiSize size = drawingGrid.getGridSize();
			for (int i = 0; i < draggedObj.getNumberOfParts(); ++i)
				{
				MiPart part = draggedObj.getPart(i);

				if (part.getResource(MiSnapManager.Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE) != null)
					{
					size = drawingGrid.getMajorGridSize();
					break;
					}
				}
			if (editor.getSnapManager().isJustSnappingToMajorGrid())
				{
				size = drawingGrid.getMajorGridSize();
				}

			// To do: add finer movements if moved parts have snapPoints
			if (direction.equals(Mi_MOVE_LEFT_COMMAND_NAME))
				{
				delta.x = -size.getWidth();
				}
			else if (direction.equals(Mi_MOVE_RIGHT_COMMAND_NAME))
				{
				delta.x = +size.getWidth();
				}
			else if (direction.equals(Mi_MOVE_DOWN_COMMAND_NAME))
				{
				delta.y = -size.getHeight();
				}
			else if (direction.equals(Mi_MOVE_UP_COMMAND_NAME))
				{
				delta.y = +size.getHeight();
				}
			}
		else
			{
			// Move 10 pixels, no matter the zoom level
			editor.getViewport().getTransform().dtow(
				event.getDevicePoint(devicePt), deviceDelta, delta);
			}

//MiDebug.println(this + " delta before snap=" + delta);

		event.editor.snapMovingPart(draggedObj, delta);

//MiDebug.println(this + " delta after snap=" + delta);

		boolean modifiedDelta 
			= event.editor.autopanForMovingObj(draggedObj.getDrawBounds(tmpBounds), delta);

//MiDebug.println(this + " delta now=" + delta);
		draggedObj.translate(delta);

		draggedObj.validateLayout();
		return(Mi_CONSUME_EVENT);
		}
	}

