
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
public class MiIDragSelectedObjects extends MiIDragObjectUnderMouse
	{
	private		MiBounds	tmpBounds	= new MiBounds();
	private		MiParts		draggingConns	= new MiParts();
	private		boolean		toDragConnections;
	private		String		CONN_ORIG_IS_TRANSLATABLE = "MiIDragSelectedObjects.CONN_ORIG_IS_TRANSLATABLE";



	public				MiIDragSelectedObjects()
		{
		setEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		setEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, 
			Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		setEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		}

	public		void		setToDragConnections(boolean flag)
		{
		toDragConnections = flag;
		}
	public		boolean		isToDragConnections()
		{
		return(toDragConnections);
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
			return(pickup());
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (draggedObj == null)
				return(Mi_PROPOGATE_EVENT);
			// Do this invaldiate cause xlate parts does not invalidate area
			event.editor.invalidateArea(draggedObj.getDrawBounds(tmpBounds));
			drag();
			event.editor.invalidateArea(draggedObj.getDrawBounds(tmpBounds));
			return(Mi_CONSUME_EVENT);
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (draggedObj == null)
				return(Mi_PROPOGATE_EVENT);
		
			drop(event);

			reEnableDraggedConnectionLayouts(draggingConns);

			// Though we translate draggedObj, and therefore moves all the parts of the 
			// dragged Object, it does not invalidate these part's parent's bounds, 
			// so we need to update them manually
			for (int i = 0; i < draggedObj.getNumberOfParts(); ++i)
				{
				MiPart container = draggedObj.getPart(i).getContainer(0);
				if (!(container instanceof MiLayer))
					{
					container.invalidateLayout();
					// container = container.getContainer(0);
					}
				}
			draggedObj.removeAllParts();

			event.editor.removeItem(draggedObj);
			draggedObj = null;
			return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}
	protected	int		pickup()
		{
		if (draggedObj == null)
			{
			//MiPart obj;
			MiEditor editor = event.editor;

			draggedObj = new MiContainer();

//MiDebug.println(this + " look at returned objests...");

			MiParts selectedParts = editor.getSelectedParts(new MiParts());
			for (int i = 0; i < selectedParts.size(); ++i)
				{
				MiPart obj = selectedParts.get(i);
				
//MiDebug.println(this + " look at returned object" + obj);
				if (obj.isSelected() && (obj.isMovable()))
					{
					draggedObj.appendPart(obj);
					}
				if ((obj instanceof MiConnection) && (obj.isSelected()))
					{
					MiConnection conn = (MiConnection )obj;
					if (toDragConnections)
						{
						draggedObj.appendPart(conn);
						conn.setResource(CONN_ORIG_IS_TRANSLATABLE, new Boolean(conn.isTranslatable()));
						conn.setIsTranslatable(true);
						}
					if (conn.getSource() instanceof MiConnectionJunctionPoint)
						{
						if (!draggedObj.containsPart(conn.getSource()))
							{
							draggedObj.appendPart(conn.getSource());
							}
						}
					if (conn.getDestination() instanceof MiConnectionJunctionPoint)
						{
						if (!draggedObj.containsPart(conn.getDestination()))
							{
							draggedObj.appendPart(conn.getDestination());
							}
						}
					}
				}
			if (draggedObj.getNumberOfParts() == 0)
				{
				draggedObj = null;
				}

			if (draggedObj != null)
				{
				startDragOffsetFromCenter.set(
					event.worldPt.x - draggedObj.getCenterX(),
					event.worldPt.y - draggedObj.getCenterY());
				event.editor.getWorldBounds(originalWorld);
				draggedObj.getCenter(originalLocation);
				event.editor.prependGrabEventHandler(this);
				event.editor.appendItem(draggedObj);

				draggingConns = handleDraggingConnectionEndPoints(draggedObj);

				return(drag());
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	protected	void		doDrag(MiPart draggedObj, MiVector delta)
		{
		draggedObj.translate(delta);

		for (int i = 0; i < draggingConns.size(); ++i)
			{
			MiPart conn = draggingConns.elementAt(i);
			for (int j = 1; j < conn.getNumberOfPoints() - 1; ++j)
				{
				conn.translatePoint(j, delta.x, delta.y);
				}
			}
		draggedObj.validateLayout();
		}
	protected	void		drop(MiEvent event)
		{
		MiEditor editor = event.editor;

		editor.removeGrabEventHandler(this);

		MiVector amountTranslated = new MiVector(
				draggedObj.getCenterX() - originalLocation.x,
				draggedObj.getCenterY() - originalLocation.y);

		MiParts translatedParts = new MiParts();
		for (int i = 0; i < draggedObj.getNumberOfParts(); ++i)
			{
			translatedParts.addElement(draggedObj.getPart(i));
			if (draggedObj.getPart(i) instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )draggedObj.getPart(i);
				conn.setResource(CONN_ORIG_IS_TRANSLATABLE, new Boolean(conn.isTranslatable()));
				conn.setIsTranslatable(((Boolean )conn.getResource(CONN_ORIG_IS_TRANSLATABLE)).booleanValue());
				}
			}
		for (int i = 0; i < draggingConns.size(); ++i)
			{
			translatedParts.addElement(draggingConns.elementAt(i));
			}

		MiNestedTransaction nestedTransaction 
			= new MiNestedTransaction(MiiDisplayNames.Mi_TRANSLATE_DISPLAY_NAME);
		if (!originalWorld.equals(editor.getWorldBounds()))
			{
			nestedTransaction.addElement(
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
			nestedTransaction.addElement(
				new MiTranslatePartsCommand(editor, translatedParts, amountTranslated));
			MiSystem.getViewportTransactionManager().appendTransaction(
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
			}
		else
			{
			nestedTransaction.addElement(
				new MiTranslatePartsCommand(editor, translatedParts, amountTranslated));
			}

		MiSystem.getTransactionManager().appendTransaction(nestedTransaction);

		editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_MOVE_PART_ACTION, draggedObj);
		}

		
	protected	void		reEnableDraggedConnectionLayouts(MiParts draggingConns)
		{
		for (int i = 0; i < draggingConns.size(); ++i)
			{
			MiPart conn = draggingConns.elementAt(i);
			if (conn.getLayout() != null)
				{
				// Fix, some layouts maybe were disabled
				conn.getLayout().setEnabled(true);
				}
			}
		}
	protected	MiParts		handleDraggingConnectionEndPoints(MiPart draggedObj)
		{
		MiParts draggingConns = new MiParts();
		for (int i = 0; i < draggedObj.getNumberOfParts(); ++i)
			{
			MiPart node = draggedObj.getPart(i);
			for (int j = 0; j < node.getNumberOfConnections(); ++j)
				{
				MiConnection conn = node.getConnection(j);
				//if (conn.getNumberOfPoints() > 2) &&
				if (((conn.getSource() != null) 
						&& (draggedObj.containsPart(conn.getSource())))
					&& ((conn.getDestination() != null) 
						&& (draggedObj.containsPart(conn.getDestination()))))
					{
					if (!draggingConns.contains(conn))
						{
						draggingConns.addElement(conn);
						if (conn.getLayout() != null)
							{
							conn.getLayout().setEnabled(false);
							}
						}
					}
				}
			}
		return(draggingConns);
		}
	}

