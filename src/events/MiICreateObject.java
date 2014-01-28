
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
public class MiICreateObject extends MiEventHandler implements MiiActionTypes
	{
	private		MiIRubberbandBounds 	irect;
	private		MiPart			prototypeShape 		= new MiRectangle();
	private		boolean			selectNewlyCreatedShape	= true;
	private		MiPart			rect;
	private		boolean			rubberStampMode;
	private		boolean			canResizeShapeInRubberStampMode = true;
	private		boolean			useDATA_IMPORT_forAddingStampedPartToEditor;


	public				MiICreateObject()
		{
		addEventToCommandTranslation(Mi_RUBBER_STAMP_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_CANCEL_COMMAND_NAME, 
						Mi_KEY_EVENT, Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN);
		}

	public		void		setInRubberStampMode(boolean flag)
		{
		rubberStampMode = flag;
		}
	public		boolean		isInRubberStampMode()
		{
		return(rubberStampMode);
		}
	public		void		setCanResizeShapeInRubberStampMode(boolean flag)
		{
		canResizeShapeInRubberStampMode = flag;
		}
	public		boolean		getCanResizeShapeInRubberStampMode()
		{
		return(canResizeShapeInRubberStampMode);
		}
	public		void		setUseDataImportActionforAddingStampedPartToEditor(boolean flag)
		{
		useDATA_IMPORT_forAddingStampedPartToEditor = flag;
		}
	public		boolean		getUseDataImportActionforAddingStampedPartToEditor()
		{
		return(useDATA_IMPORT_forAddingStampedPartToEditor);
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
			if ((!rubberStampMode) || (canResizeShapeInRubberStampMode))
				{
				event.editor.deSelectAll();
				event.editor.prependGrabEventHandler(this);

				rect = prototypeShape.deepCopy();
				if (rect.getNumberOfParts() == 0)
					{
					rect.setPoint(0, event.worldPt);
					rect.setPoint(1, event.worldPt);
					}
				else
					{
					rect.setCenter(event.worldPt);
					rect.setSize(0.001, 0.001);
					}
				irect = new MiIRubberbandBounds();
				irect.setPart(rect);

				irect.processCommand(event, Mi_PICK_UP_COMMAND_NAME);
				}
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (irect == null)
				return(Mi_PROPOGATE_EVENT);

			irect.processCommand(event, Mi_DRAG_COMMAND_NAME);
			}
		else if (isCommand(Mi_CANCEL_COMMAND_NAME))
			{
			if (irect == null)
				{
				if (rubberStampMode)
					setInRubberStampMode(false);
				return(Mi_PROPOGATE_EVENT);
				}

			irect.processCommand(event, Mi_DROP_COMMAND_NAME);
			event.editor.removeGrabEventHandler(this);
			irect = null;
			rect = null;
			return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (irect == null)
				return(Mi_PROPOGATE_EVENT);

			irect.processCommand(event, Mi_DROP_COMMAND_NAME);
			event.editor.removeGrabEventHandler(this);
			irect = null;
			event.editor.getCurrentLayer().appendPart(rect);
			if (selectNewlyCreatedShape)
				event.editor.select(rect);
			event.editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION, rect);
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(event.editor, rect, false));
			rect = null;
			}
		else if (isCommand(Mi_RUBBER_STAMP_COMMAND_NAME))
			{
			if ((irect != null) || (!rubberStampMode))
				return(Mi_PROPOGATE_EVENT);

			rect = prototypeShape.copy();
			rect.setVisible(true);
			rect.setCenter(event.getWorldPoint(new MiPoint()));

			MiNestedTransaction nestedTransaction 
				= new MiNestedTransaction("Create " + (rect.getName() != null ? rect.getName() : ""));
			MiSystem.getTransactionManager().startTransaction(nestedTransaction);

			if (!useDATA_IMPORT_forAddingStampedPartToEditor)
				{
				event.editor.getCurrentLayer().appendPart(rect);
				if (selectNewlyCreatedShape)
					event.editor.select(rect);
				event.editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_RUBBER_STAMP_PART_ACTION, rect);

				MiSystem.getTransactionManager().appendTransaction(new MiDeletePartsCommand(event.editor, rect, false));
				}
			else
				{
				MiDataTransferOperation transfer = new MiDataTransferOperation(rect);
				MiPoint targetPosition = rect.getCenter();
				transfer.setData(rect);
				transfer.setTarget(event.editor.getCurrentLayer());
				transfer.setLookTargetPosition(targetPosition);
				transfer.setLookTargetBounds(rect.getBounds());
				transfer.setDataFormat(MiiDragAndDropParticipant.Mi_MiPART_FORMAT);
				event.editor.doImport(transfer);
				event.editor.dispatchAction(Mi_DATA_IMPORT_ACTION, transfer);

				//MiSystem.getTransactionManager().appendTransaction(new MiDeletePartsCommand(event.editor, rect, false));
				}

			MiSystem.getTransactionManager().commitTransaction(nestedTransaction);
			rect = null;
			}

		return(Mi_CONSUME_EVENT);
		}

	public		void		setPrototypeShape(MiPart obj)
		{
		prototypeShape = obj;
		}
	public		MiPart		getPrototypeShape()
		{
		return(prototypeShape);
		}
	}

