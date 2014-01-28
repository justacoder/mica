
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
public class MiICreateMultiPointObject extends MiEventHandler implements MiiActionTypes
	{
	public static final String	Mi_MOVE_LEFT_COMMAND_NAME	= "MoveLeft";
	public static final String	Mi_MOVE_RIGHT_COMMAND_NAME	= "MoveRight";
	public static final String	Mi_MOVE_DOWN_COMMAND_NAME	= "MoveDown";
	public static final String	Mi_MOVE_UP_COMMAND_NAME		= "MoveUp";

	private		MiPart			prototypeShape 		= new MiLine();
	private		MiVector		delta 			= new MiVector();
	private		MiVector		deviceDelta 		= new MiVector(10, 10);
	private		MiPoint			devicePt 		= new MiPoint();
	private		MiPoint			tmpPoint 		= new MiPoint();
	private		boolean			selectNewlyCreatedShape	= true;
	private		MiIRubberbandPoint 	iline;
	private		MiPart			line;
	private		boolean			rubberStampMode;
	private		String			direction;



	public				MiICreateMultiPointObject()
		{
		addEventToCommandTranslation(Mi_RUBBER_STAMP_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		addEventToCommandTranslation(
			MiIRubberbandPoint.Mi_APPEND_POINT_COMMAND_NAME, Mi_KEY_EVENT, ' ', 0);
		addEventToCommandTranslation(
			Mi_CANCEL_COMMAND_NAME, Mi_KEY_EVENT, Mi_ESC_KEY, 0);

		addEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_LEFT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_RIGHT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_DOWN_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_DOWN_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_UP_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_UP_ARROW_KEY, 0);
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
			while (line.getNumberOfPoints() < 2)
				line.appendPoint(new MiPoint());

			MiPoint startPoint = event.getWorldPoint(new MiPoint());
			event.editor.snapMovingPoint(startPoint);
			line.setPoint(0, startPoint);
			line.setPoint(-1, startPoint);

			iline = new MiIRubberbandPoint();
			iline.setPart(line);
			iline.setNumberOfPointToModify(-1);

			iline.processCommand(event, Mi_PICK_UP_COMMAND_NAME);
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (iline == null)
				return(Mi_PROPOGATE_EVENT);

			iline.processCommand(event, Mi_DRAG_COMMAND_NAME);
			}
		else if (isCommand(MiIRubberbandPoint.Mi_APPEND_POINT_COMMAND_NAME))
			{
			if (iline == null)
				return(Mi_PROPOGATE_EVENT);

			iline.processCommand(event, MiIRubberbandPoint.Mi_APPEND_POINT_COMMAND_NAME);
			}
		else if (isCommand(Mi_CANCEL_COMMAND_NAME))
			{
			if (iline == null)
				return(Mi_PROPOGATE_EVENT);

			iline.processCommand(event, Mi_DROP_COMMAND_NAME);
			event.editor.removeGrabEventHandler(this);
			iline = null;
			line = null;
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (iline == null)
				return(Mi_PROPOGATE_EVENT);

			iline.processCommand(event, Mi_DROP_COMMAND_NAME);
			event.editor.removeGrabEventHandler(this);
			iline = null;
			event.editor.getCurrentLayer().appendPart(line);
			if (selectNewlyCreatedShape)
				event.editor.select(line);
			event.editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION, line);
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(event.editor, line, false));
			line = null;
			}
		else if (isCommand(Mi_RUBBER_STAMP_COMMAND_NAME))
			{
			if ((iline != null) || (!rubberStampMode))
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
		else if ((isCommand(Mi_MOVE_LEFT_COMMAND_NAME))
			|| (isCommand(Mi_MOVE_RIGHT_COMMAND_NAME))
			|| (isCommand(Mi_MOVE_DOWN_COMMAND_NAME))
			|| (isCommand(Mi_MOVE_UP_COMMAND_NAME)))
			{
			if (iline == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}

			direction = getCommand();
			MiEditor editor = event.getEditor();
			delta.x = 0;
			delta.y = 0;
			if ((editor.getSnapManager() != null)
				&& (editor.getSnapManager().getGrid() != null))
				{
				MiDrawingGrid drawingGrid = editor.getSnapManager().getGrid();
				MiSize size = drawingGrid.getGridSize();

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
	
			line.getPoint(-1, tmpPoint);
			tmpPoint.translate(delta);
			event.editor.snapMovingPoint(tmpPoint);

			//??event.editor.snapMovingPart(line, delta);

			//??boolean modifiedDelta = event.editor.autopanForMovingObj(line.getDrawBounds(tmpBounds), delta);

			
			event.setWorldPoint(tmpPoint);
			iline.processCommand(event, Mi_DRAG_COMMAND_NAME);
			}
		return(Mi_CONSUME_EVENT);
		}

	}
