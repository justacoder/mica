
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

/**----------------------------------------------------------------------------------------------
 * This class manages drag and drop functionality for a
 * window. The default behavior maps the following events
 * to drag and drop actions:
 * <p>
 * <pre>
 *
 * pickup:	Mi_LEFT_MOUSE_START_DRAG_EVENT + Mi_SHIFT_KEY_HELD_DOWN
 * drag:	Mi_LEFT_MOUSE_DRAG_EVENT + Mi_SHIFT_KEY_HELD_DOWN
 * cancel:	Mi_ESC_KEY + Mi_ANY_MODIFIERS_HELD_DOWN
 * drop:	Mi_LEFT_MOUSE_UP_EVENT + Mi_ANY_MODIFIERS_HELD_DOWN
 *
 * </pre>
 * <p>
 * The default behavior can be modified for the window by
 * using this manager's setDragAndDropBehavior() method.
 * The default behavior can be modified for any part or
 * container in the container-part hierarchy by using
 * the part or container's setDragAndDropBehavior() method.
 * <p>
 * When these events occur the part under the mouse is
 * examined to see if isDragAndDropSource() is true (for
 * the pickup) and if isDragAndDropTarget() during drag
 * and drop.
 * <p>
 * When the dragged part is picked up an action is sent to it of type: 
 * <p>
 *
 * 	Mi_DRAG_AND_DROP_PICKUP_ACTION, with systemInfo the
 * 	current MiDataTransferOperation information.
 *
 * <p>
 * Any action handlers can then generate what the normal
 * appearance of the dragged part is. 'Normal' is when there
 * are no other effects occuring.
 * <p>
 * When the dragged part is over a valid drop target an action is sent to
 * the drop target of type: 
 * <p>
 *
 * 	Mi_DRAG_AND_DROP_ENTER_ACTION, with systemInfo the
 * 	current MiDataTransferOperation information.
 *
 * <p>
 * Any action handlers can then generate what the dragOver
 * appearance of the dragged part is. 'DragOver' is when there
 * is a valid part underneath the dragged part and the dragged part
 * changes appearance in response to this condition. This method 
 * may also change the mouse cursor appearance at this time as well.
 * <p>
 * The action handlers can also take this opportunity to generate
 * the dragUnder appearance of the drop target. 'DragUnder' is when
 * there is a valid part underneath the dragged part and the part 
 * underneath changes appearance in response to this condition.
 * <p>
 * When the dragged part is no longer over a particular valid drop
 * target an action is sent to the old drop target of type: 
 * <p>
 *
 * 	Mi_DRAG_AND_DROP_EXIT_ACTION, with systemInfo the
 * 	current MiDataTransferOperation information.
 *
 * <p>
 * The action handlers can also take this opportunity to restore the
 * old drop target from it's dragUnder appearance and the dragged part
 * from it's dragOver appearance.
 * <p>
 * When the dragged part is paused an action is sent to the valid drop
 * target (if it exists otherwise it is sent to the dragged part) of type: 
 * <p>
 *
 * 	Mi_DRAG_AND_DROP_PAUSE_ACTION, with systemInfo the
 * 	current MiDataTransferOperation information.
 *
 * <p>
 * The action handlers can take this opportunity to generate 'scroll-under'
 * effects or to allow the dragged part to be shown in it's entirety when 
 * previously only an outline of the part was being drawn. When no longer
 * paused the dragged part an action will be sent to the corresponding part
 * of type:
 * <p>
 *
 * 	Mi_DRAG_AND_DROP_CONTINUE_ACTION, with systemInfo the
 * 	current MiDataTransferOperation information.
 *
 * <p>
 * If the drag and drop operation is canceled then an action is sent to the
 * valid drop target (if it exists otherwise it is sent to the dragged part)
 * of type: 
 * <p>
 *
 * 	Mi_DRAG_AND_DROP_CANCEL_ACTION, with systemInfo the
 * 	current MiDataTransferOperation information.
 *
 * <p>
 * If the drag and drop operation completes successfully then an action is sent
 * to the drop target of type: 
 * <p>
 *
 * 	Mi_DRAG_AND_DROP_COMMIT_ACTION, with systemInfo the
 * 	current MiDataTransferOperation information.
 *
 * <p>
 * The action handlers can take this opportunity to generate disolve effects.
 * <p>
 *
 *
 * Set the MiDebug.tracingMode to MiDebug.TRACE_DRAG_AND_DROP to
 * get debug information.
 *
 * @see		#setDragAndDropBehavior
 * @see		MiiDragAndDropBehavior
 * @see		MiDragAndDropBehavior
 * @see		MiAction#getActionSystemInfo
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDragAndDropManager implements MiiEventTypes
	{
	public static final String		Mi_DRAG_AND_DROP_THIS_INSTEAD = "Mi_DRAG_AND_DROP_THIS_INSTEAD";
	private		MiWindow		window;
	private		MiiDragAndDropBehavior	behavior;




					/**------------------------------------------------------
	 				 * Constructs a new MiDragAndDropManager for the given 
					 * window and initializes the default behavior.
					 * @param window	the window to manage
					 *------------------------------------------------------*/
	public				MiDragAndDropManager(MiWindow window)
		{
		this.window = window;
		window.insertEventHandler(new MiDragAndDropEventHandler(this), 0);

		MiDragAndDropBehavior behavior = new MiDragAndDropBehavior();
		this.behavior = behavior;

		behavior.setDragAndCopyPickUpEvent(
			new MiEvent(Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN));
		behavior.setDragAndCopyDragEvent(
			new MiEvent(Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN));
		behavior.setDragAndCopyCancelEvent(
			new MiEvent(Mi_KEY_EVENT, Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN));
		behavior.setDragAndCopyDropEvent(
			new MiEvent(Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN));
		}
					/**------------------------------------------------------
	 				 * Sets the default drag and drop behavior for the window.
					 * @param b		the new default behavior
					 *------------------------------------------------------*/
	public		void		setDragAndDropBehavior(MiiDragAndDropBehavior b)
		{
		behavior = b;
		}
					/**------------------------------------------------------
	 				 * Gets the default drag and drop behavior for the window.
					 * @return 		the default behavior
					 *------------------------------------------------------*/
	public		MiiDragAndDropBehavior	getDragAndDropBehavior()
		{
		return(behavior);
		}
	}

/**----------------------------------------------------------------------------------------------
 *
 * This event handler is assigned to the window managed
 * by a MiDragAndDropManager. This event handler does the
 * actual movement and animation of drag and drop operations.
 *
 *----------------------------------------------------------------------------------------------*/
class MiDragAndDropEventHandler extends MiEventHandler implements MiiTypes, MiiActionTypes, MiiDisplayNames
	{
	private		boolean			draggingAndCopying;
	private		boolean			draggingAndCutting;
	private		boolean			paused;
	private		boolean			canceled;
	private		boolean			referenceIsToOriginalGraphics;
	private		MiEvent			flipEvent = new MiEvent(Mi_KEY_PRESS_EVENT, 'f', Mi_CONTROL_KEY_HELD_DOWN);
	private		MiEvent			rotateEvent = new MiEvent(Mi_KEY_PRESS_EVENT, 'r', Mi_CONTROL_KEY_HELD_DOWN);
	private		MiEvent			pickupEvent;
	private		MiEvent			dragEvent;
	private		MiEvent			dropEvent;
	private		MiEvent			cancelEvent;
	private		MiEvent			preludeEvent;		// hold this event to prevent
									// keyboard focus from changing
									// when this event is a prelude to
									// a dnd pickup operation
	private		MiiDragAndDropBehavior	dndSourceBehavior;
	private		MiDragAndDropManager	dndManager;
	private		MiWindow 		rootWindow;
	private		MiDataTransferOperation	transferOp;
	private		MiPart			drappedPartNormalAppearance;
	private		MiPart			currentDropTarget;
	private		MiPart			sourcePart;
	private		MiPart			draggedPart;
	private		MiPoint			startingLocation;
	private		MiPoint			startDragOffsetFromCenter	= new MiPoint();
	private		boolean			snapObjectCenterToCursor	= true;
	private		boolean			glueObjectStartLocationToCursor	= true;
	private		boolean			keepObjectCompletelyWithinRootWindow= true;
	private		MiVector		delta			= new MiVector();
	private		MiBounds		tmpBounds		= new MiBounds();
	private		MiBounds		tmpCursorBounds		= new MiBounds();
	private		MiBounds		rootCursorBounds	= new MiBounds();



					/**------------------------------------------------------
	 				 * Constructs a new MiDragAndDropEventHandler for the
					 * given drag and drop manager.
					 * @param manager	the associated manager
					 *------------------------------------------------------*/
	public				MiDragAndDropEventHandler(MiDragAndDropManager manager)
		{
		dndManager = manager;
		setType(Mi_SHORT_CUT_EVENT_HANDLER_TYPE);
		setPositionDependent(false);
		}

					/**------------------------------------------------------
	 				 * Process the event, checking to see if it the drag-and-
					 * copy pickup event or the drag-and-cut pickup event or,
					 * we have already picked-up something if the given event
					 * is a drag, cancel or drop event.
					 *------------------------------------------------------*/
	public		int		processEvent(MiEvent event)
		{
		if ((event.getTargetPath().size() < 1) || (canceled))
			{
			return(Mi_PROPOGATE_EVENT);
			}

		// ------------------------------------------------------------------
		// If DnD is already in progress, continue directly with the method
		// that handles drag, drop and cancel events.
		// ------------------------------------------------------------------
		if (draggingAndCopying || draggingAndCutting)
			{
			return(doDragAndDrop(event));
			}

		referenceIsToOriginalGraphics = false;

		// ------------------------------------------------------------------
		// DnD is not in progress, find behavior that applies to the target
		// part underneath the mouse cursor. The target part is found by
		// examining the targetPath of the event to find the topmost part
		// that has isDragAndDropSource() true. The behavior is found by
		// examining the part and all it's containers to find the topmost
		// part that has a behavior that has a pickup event equal to the
		// incoming event.
		// If the icoming event is a 'prelude' event, i.e. it is the 
		// beginning of one of the pickup events found in one of the
		// behaviors, then it is saved. If the next event matches the pickup
		// event, this saved event is thrown away, otherwise the saved event
		// is dispatched before this 'next' event. All of this is to handle
		// the case of MOUSE_BUTTON_DOWN occuring before MOUSE_BUTTON_START_DRAG
		// and causing a change in keyboard focus, which is very undesirable 
		// for a drag-and-drop operation.
		// ------------------------------------------------------------------
		MiParts pickList = event.getTargetPath();
		for (int i = 0; i < pickList.size(); ++i)
			{
			MiPart part = pickList.elementAt(i);
			if (isDragAndDropSource(part) != null)
				{
				for (int j = i; j < pickList.size(); ++j)
					{
					part = pickList.elementAt(j);
					if (part.getDragAndDropBehavior() != null)
						{
						MiiDragAndDropBehavior behavior =
							part.getDragAndDropBehavior();

						if ((behavior.isDefaultBehaviorForParts()) || (i == j))
							{
							if (isPickupEvent(event, behavior))
								{
								return(doDragAndDrop(event));
								}
							if ((behavior.getDragAndCopyPickUpEvent() != null)
								&& (event.isPreludeToEvent(
									behavior.getDragAndCopyPickUpEvent())))
								{
								preludeEvent = new MiEvent(event);
								return(Mi_CONSUME_EVENT);
								}
							}
						}
					// Reached root of all objects we are handling here...?
					if (part == event.editor)
						break;
					}
				if (isPickupEvent(event, dndManager.getDragAndDropBehavior()))
					{
					return(doDragAndDrop(event));
					}
				if (event.isPreludeToEvent(
					dndManager.getDragAndDropBehavior().getDragAndCopyPickUpEvent()))
					{
					preludeEvent = new MiEvent(event);
					return(Mi_CONSUME_EVENT);
					}
				if (preludeEvent != null)
					{
					event.editor.getRootWindow().pushBackEvent(new MiEvent(event));
					event.copy(preludeEvent);
					preludeEvent = null;
					}
				return(Mi_PROPOGATE_EVENT);
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
					/**------------------------------------------------------
	 				 * Checks to see if the given part is a drag-and-drop
					 * source. By default this returns part.isDragAndDropSource().
					 * Override this for more advanced behaviors.	
					 * @param part		the part to test
					 * @return		the part or a container of the part 
					 *			that is a drag and drop source or null
					 *------------------------------------------------------*/
	protected	MiPart		isDragAndDropSource(MiPart part)
		{
		MiiDragAndDropBehavior sourcePartsDragAndDropBehavior = getDragAndDropBehavior(part);
		MiPart container = part;
		while (container != null)
			{
			if (sourcePartsDragAndDropBehavior != null)
				{
				if (sourcePartsDragAndDropBehavior.isPartDragAndDropSource(container)) 
					{
					return(container);
					}
				}
			else if ((container.isDragAndDropSource())
				&& (container.getEventHandlingEnabled())
				&& (!container.getEventHandlingDisabledByContainer()))
				{
				return(container);
				}
			if (container.getNumberOfContainers() > 0)
				{
				container = container.getContainer(0);
				}
			else
				{
				container = null;
				}
			}
		return(null);
		}
	protected	MiiDragAndDropBehavior	getDragAndDropBehavior(MiPart part)
		{
		MiPart container = part;
		while (container != null)
			{
			if (container.getDragAndDropBehavior() != null)
				{
				return(container.getDragAndDropBehavior());
				}

			if (container.getNumberOfContainers() > 0)
				{
				container = container.getContainer(0);
				}
			else
				{
				container = null;
				}
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Checks to see if the given event is a drag-and-copy
					 * pickup event or drag-and-cut pickup event of the given
					 * behavior.
					 * If so the class variables pickupEvent, dragEvent, 
					 * dropEvent, cancelEvent, dndSourceBehavior, 
					 * draggingAndCopying, and draggingAndCutting are set and
					 * preludeEvent is set to null.
					 *------------------------------------------------------*/
	protected	boolean		isPickupEvent(MiEvent event, MiiDragAndDropBehavior behavior)
		{
		pickupEvent = behavior.getDragAndCopyPickUpEvent();
		if ((pickupEvent != null) && (event.equals(pickupEvent)))
			{
			dragEvent = behavior.getDragAndCopyDragEvent();
			dropEvent = behavior.getDragAndCopyDropEvent();
			cancelEvent = behavior.getDragAndCopyCancelEvent();
			dndSourceBehavior = behavior;
			draggingAndCopying = true;
			preludeEvent = null;
			return(true);
			}
		pickupEvent = behavior.getDragAndCutPickUpEvent();
		if ((pickupEvent != null) && (event.equals(pickupEvent)))
			{
			dragEvent = behavior.getDragAndCutDragEvent();
			dropEvent = behavior.getDragAndCutDropEvent();
			cancelEvent = behavior.getDragAndCutCancelEvent();
			dndSourceBehavior = behavior;
			draggingAndCutting = true;
			preludeEvent = null;
			return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * This method implements the graphics animations, 
					 * validation checking, canceling, dropping, etc. of the
					 * drag-and-drop operation.
					 * @param event		the end-user event
					 *------------------------------------------------------*/
	public		int		doDragAndDrop(MiEvent event)
		{
		rootWindow = event.editor.getRootWindow();
		rootCursorBounds.setBounds(event.worldPt);

		// ----------------------------------------------------------
		//		P I C K U P
		// ----------------------------------------------------------
		if (event.equals(pickupEvent))
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
				MiDebug.println("Drag-And-Drop pickup event detected");

			if (draggedPart != null)
				return(Mi_PROPOGATE_EVENT);

			currentDropTarget		= null;
			paused = false;

			// -------------------------------------
			// Find object to pickup
			// -------------------------------------
			MiParts targetList = event.getTargetList();
			boolean foundDnDSource = false;
			for (int i = 0; i < targetList.size(); ++i)
				{
				draggedPart = targetList.elementAt(i);
				draggedPart = isDragAndDropSource(draggedPart);
				if (draggedPart != null)
					break;
				}
			if (draggedPart == null)
				{
				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
					MiDebug.println("Drag-And-Drop pickup event detected no-pickupable parts");

				draggedPart = null;
				transferOp = null;
				draggingAndCopying = false;
				draggingAndCutting = false;
				}
			else
				{
				// ---------------------------------------------------------------
				// Make a reference/copy to/of the source of the drag and drop operation.
				// ---------------------------------------------------------------
				currentDropTarget 	= null;
				MiEditor parentEditor 	= draggedPart.getContainingEditor();
				if (draggedPart.getResource(MiDragAndDropManager.Mi_DRAG_AND_DROP_THIS_INSTEAD) != null)
					{
					MiPart simulacrum  = (MiPart )draggedPart.getResource(MiDragAndDropManager.Mi_DRAG_AND_DROP_THIS_INSTEAD);
					simulacrum.setCenter(draggedPart.getCenter());
					draggedPart = simulacrum;
					}
				transferOp 		= new MiDataTransferOperation(draggedPart);
				sourcePart		= draggedPart;
				boolean	sourceIsSelected= draggedPart.isSelected();
				boolean	makeReference	= dndSourceBehavior.getDragsReferenceNotCopy();

//System.out.println("DnD Manager makeReference = " + makeReference);

				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
					MiDebug.println("Drag-And-Drop picked-up: " + draggedPart);

				if (dndSourceBehavior.getDataToExport() instanceof MiPart)
					transferOp.setData(((MiPart )dndSourceBehavior.getDataToExport()).deepCopy());
				else
					transferOp.setData(dndSourceBehavior.getDataToExport());

				if ((sourceIsSelected) && (!makeReference))
					{
					parentEditor.deSelect(sourcePart);
					}

				transferOp.setIsDragAndCut(draggingAndCutting);

				if (makeReference)
					{
					if (dndSourceBehavior.getDraggingLook() != null)
						{
						draggedPart = new MiReference(dndSourceBehavior.getDraggingLook());
						draggedPart.setCenter(sourcePart.getCenter());
						}
					else
						{
						draggedPart = new MiReference(sourcePart);
						}
					referenceIsToOriginalGraphics = true;
					}
				else
					{
					if (dndSourceBehavior.getDraggingLook() != null)
						{
						draggedPart = dndSourceBehavior.getDraggingLook().deepCopy();
						draggedPart.setCenter(sourcePart.getCenter());
						}
					else
						{
						draggedPart = sourcePart.deepCopy();
						}

					draggedPart.setSize(draggedPart.getPreferredSize(new MiSize()));
					}

				if ((sourceIsSelected) && (!makeReference))
					{
					parentEditor.select(sourcePart);
					}

				snapObjectCenterToCursor 
					= dndSourceBehavior.getSnapLookCenterToCursor();

				keepObjectCompletelyWithinRootWindow 
					= dndSourceBehavior.getKeepLookCompletelyWithinRootWindow();

				draggedPart.setPickable(false);

				MiBounds bounds = draggedPart.getBounds();
				rootWindow.transformFromChildSpace(sourcePart, bounds, bounds);
				startingLocation = bounds.getCenter();

				if ((dndSourceBehavior.getTargetEditor() == null) 
					|| (dndSourceBehavior.getTargetEditor().getRootWindow() == null))
					{
					// ---------------------------------------------------------------
					// Alter obj here to make it the same size and location in root window
					// as it was in source window
					// ---------------------------------------------------------------
					draggedPart.setBounds(bounds);
					}
				else 
					{
					// ---------------------------------------------------------------
					// Alter obj here to make it the same size as it will be when dropped 
					// in target editor
					// ---------------------------------------------------------------
//System.out.println("dndSourceBehavior. bounds = " + bounds);
					bounds = draggedPart.getBounds();
					dndSourceBehavior.getTargetEditor().transformLocalWorldToRootWorld(bounds);
//System.out.println("NOW. bounds = " + bounds);
					draggedPart.setSize(bounds.getSize());
					}

				dispatchAction(Mi_DRAG_AND_DROP_PICKUP_ACTION);
				if (draggedPart != transferOp.getLook())
					{
					if ((makeReference) && (draggedPart instanceof MiReference))
						{
						draggedPart.removeAllParts();
						}
					draggedPart = transferOp.getLook();
					}

				drappedPartNormalAppearance = draggedPart;

				startDragOffsetFromCenter.set(
					startingLocation.x - draggedPart.getCenterX(),
					startingLocation.y - draggedPart.getCenterY());

				// ---------------------------------------------------------------
				// Append dragged object to root window and make this grab events.
				// ---------------------------------------------------------------
				rootWindow.appendAttachment(draggedPart);
				rootWindow.setMouseAppearance(Mi_WAIT_CURSOR);
				rootWindow.prependGrabEventHandler(this);
				}
			return(Mi_CONSUME_EVENT);
			}
		// ----------------------------------------------------------
		//		D R A G
		// ----------------------------------------------------------
		else if (event.equals(dragEvent))
			{
			if (draggedPart == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			if (paused)
				{
				dispatchAction(Mi_DRAG_AND_DROP_CONTINUE_ACTION);
				paused = false;
				}

			// ---------------------------------------------------------------
			// If over object that is droponable by object
			// OF THIS TYPE then do nothing... else set
			// mouse cursor to skull and crossbones...
			// ---------------------------------------------------------------
			MiPart dropOnObj = null;
			MiParts targetList = event.getTargetList();
			for (int i = 0; i < targetList.size(); ++i)
				{
				MiPart target = targetList.elementAt(i);
				if (target.isDragAndDropTarget())
					{
					if (isValidDropTarget(rootWindow, target))
						{
						dropOnObj = target;
						break;
						}
					if ((target.getDragAndDropBehavior() != null)
						&& (target.getDragAndDropBehavior().isOpaqueDragAndDropTarget()))
						{
						break;
						}
					}
				}

			validateDropTarget(rootWindow, dropOnObj, dropOnObj != null);

			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
				MiDebug.println("Current target of Drag-And-Drop: " + currentDropTarget);

			// ---------------------------------------------------------------
			// Now move the dragged object.
			// ---------------------------------------------------------------
			delta.copy(event.worldVector);

			if (snapObjectCenterToCursor)
				{
				delta.x = event.worldPt.x - draggedPart.getCenterX();
				delta.y = event.worldPt.y - draggedPart.getCenterY();
				}
			else if (glueObjectStartLocationToCursor)
				{
				delta.x = event.worldPt.x - (draggedPart.getCenterX() + startDragOffsetFromCenter.x);
				delta.y = event.worldPt.y - (draggedPart.getCenterY() + startDragOffsetFromCenter.y);
				}


			boolean snappedToGrid = false;
			for (int i = 0; i < targetList.size(); ++i)
				{
				MiPart target = targetList.elementAt(i);
				if ((target.isDragAndDropTarget()) && (target instanceof MiEditor))
					{
					MiEditor targetEditor = (MiEditor )(MiEditor )target;
					if (targetEditor.getSnapManager() == null)
						break;

					MiBounds origBounds = draggedPart.getBounds();
					MiBounds bounds = new MiBounds(origBounds);
					MiVector origDelta = new MiVector(delta);

					targetEditor.transformRootWorldToLocalWorld(bounds);
					draggedPart.setBounds(bounds);

					
					bounds.setSize(Math.abs(delta.x), Math.abs(delta.y));
					targetEditor.transformRootWorldToLocalWorld(bounds);
					delta.x = bounds.getWidth();
					delta.y = bounds.getHeight();
					if (origDelta.x < 0)
						{
						delta.x = -delta.x;
						}
					if (origDelta.y < 0)
						{
						delta.y = -delta.y;
						}
					snappedToGrid = ((MiEditor )target).snapMovingPart(draggedPart, delta);
					if (snappedToGrid)
						{
						origDelta.copy(delta);

						bounds.setSize(Math.abs(delta.x), Math.abs(delta.y));
						targetEditor.transformLocalWorldToRootWorld(bounds);
						delta.x = bounds.getWidth();
						delta.y = bounds.getHeight();
						if (origDelta.x < 0)
							{
							delta.x = -delta.x;
							}
						if (origDelta.y < 0)
							{
							delta.y = -delta.y;
							}
						}
					else
						{
						delta.copy(origDelta);
						}

					draggedPart.setBounds(origBounds);
					break;
					}
				}

			boolean modifiedDelta = false;
			if (keepObjectCompletelyWithinRootWindow)
				{
				// 3-21-2004 bug w/ very large dnd parts: Cannot pan the root window... Need to see if target is MiiAutoPannable and pan that
				//modifiedDelta = event.editor.autopanForMovingObj(
							//draggedPart.getDrawBounds(tmpBounds), delta);
				}

			if ((!modifiedDelta) && (snapObjectCenterToCursor) && (!snappedToGrid))
				{
				draggedPart.setCenter(event.worldPt);
				}
			else if (!delta.isZero())
				{
				draggedPart.translate(delta);
				}

			dispatchAction(Mi_DRAG_AND_DROP_MOVE_ACTION);
			return(Mi_CONSUME_EVENT);
			}
		// ----------------------------------------------------------
		//		P A U S E
		// ----------------------------------------------------------
		else if (event.getType() == Mi_IDLE_EVENT)
			{
			if (draggedPart == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			dispatchAction(Mi_DRAG_AND_DROP_PAUSE_ACTION);
			paused = true;
			}
		// ----------------------------------------------------------
		//		C A N C E L
		// ----------------------------------------------------------
		else if (event.equals(cancelEvent))
			{
			if (draggedPart == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			if (paused)
				{
				dispatchAction(Mi_DRAG_AND_DROP_CONTINUE_ACTION);
				paused = false;
				}
			canceled = true;
			dispatchAction(Mi_DRAG_AND_DROP_CANCEL_ACTION);
			dispatchAction(Mi_DRAG_AND_DROP_EXIT_ACTION);
			// ---------------------------------------------------------------
			// Canceled operation. Animate the return of the dragged part to
			// it's original location.
			// ---------------------------------------------------------------
			MiTranslateAnimator animator = new MiTranslateAnimator(
							draggedPart, startingLocation, 1.5, 0.1);
			animator.setPacer(new MiSlowStartSlowStopPacer());
			animator.scheduleAndWait();
			rootWindow.removeAttachment(draggedPart);
			rootWindow.restoreNormalMouseAppearance();
			rootWindow.removeGrabEventHandler(this);
			// FIX? only do this is 'makeReference' is true
			if (draggedPart instanceof MiReference)
				{
//MiDebug.println(draggedPart + " dragged look is a reference, removing all parts");
				draggedPart.removeAllParts();
				}
			draggedPart = null;
			transferOp = null;
			draggingAndCopying = false;
			draggingAndCutting = false;
			currentDropTarget = null;
			canceled = false;
			return(Mi_CONSUME_EVENT);
			}
		// ----------------------------------------------------------
		//		R O T A T E
		// ----------------------------------------------------------
		else if (event.equals(rotateEvent))
			{
			if (draggedPart == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			((MiPart )transferOp.getData()).rotate(Math.PI/2);

			if (draggedPart instanceof MiReference)
				{
				if (referenceIsToOriginalGraphics)
					{
					MiPart originalReferencedPart = draggedPart.getPart(0);
					boolean flag = originalReferencedPart.isActionDispatchingEnabled();
					draggedPart.getPart(0).setActionDispatchingEnabled(false);
					draggedPart.getPart(0).replaceSelf(originalReferencedPart.deepCopy());
					originalReferencedPart.setActionDispatchingEnabled(flag);
					draggedPart.getPart(0).setActionDispatchingEnabled(flag);
					referenceIsToOriginalGraphics = false;
					}
				boolean usingRotationTransform = ((MiReference )draggedPart).getUseRotateTransform();
				((MiReference )draggedPart).setUseRotateTransform(false);
				((MiReference )draggedPart).rotate(Math.PI/2);
				((MiReference )draggedPart).setUseRotateTransform(usingRotationTransform);
				}
			else
				{
				draggedPart.rotate(Math.PI/2);
				}
			}
		// ----------------------------------------------------------
		//		F L I P
		// ----------------------------------------------------------
		else if (event.equals(flipEvent))
			{
			if (draggedPart == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			((MiPart )transferOp.getData()).flip(Math.PI/2 + ((MiPart )transferOp.getData()).getRotation());
			if (draggedPart instanceof MiReference)
				{
				if (referenceIsToOriginalGraphics)
					{
					MiPart originalReferencedPart = draggedPart.getPart(0);
					boolean flag = originalReferencedPart.isActionDispatchingEnabled();
					draggedPart.getPart(0).setActionDispatchingEnabled(false);
					draggedPart.getPart(0).replaceSelf(originalReferencedPart.deepCopy());
					originalReferencedPart.setActionDispatchingEnabled(flag);
					draggedPart.getPart(0).setActionDispatchingEnabled(flag);
					referenceIsToOriginalGraphics = false;
					}
				boolean usingRotationTransform = ((MiReference )draggedPart).getUseRotateTransform();
				((MiReference )draggedPart).setUseRotateTransform(false);
				((MiReference )draggedPart).flip(Math.PI/2 + draggedPart.getRotation());
				((MiReference )draggedPart).setUseRotateTransform(usingRotationTransform);
				}
			else
				{
				draggedPart.flip(Math.PI/2 + draggedPart.getRotation());
				}
			}
		// ----------------------------------------------------------
		//		D R O P
		// ----------------------------------------------------------
		else if (event.equals(dropEvent))
			{
			if (draggedPart == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			if (paused)
				{
				dispatchAction(Mi_DRAG_AND_DROP_CONTINUE_ACTION);
				paused = false;
				}
			// ---------------------------------------------------------------
			// If over object that is droponable by this object
			// then have it import the data from the source object
			// in a compatible format.
			// ---------------------------------------------------------------
			MiPart dropOnObj = null;
			MiParts targetList = event.getTargetList();
			for (int i = 0; i < targetList.size(); ++i)
				{
				MiPart target = targetList.elementAt(i);
				if (target.isDragAndDropTarget())
					{
					if (isValidDropTarget(rootWindow, target))
						{
						dropOnObj = target;
						break;
						}
					if ((target.getDragAndDropBehavior() != null)
						&& (target.getDragAndDropBehavior().isOpaqueDragAndDropTarget()))
						{
						break;
						}
					}
				}

			validateDropTarget(rootWindow, dropOnObj, dropOnObj != null);

			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
				MiDebug.println("Dropped on target of Drag-And-Drop: " + currentDropTarget);

			draggedPart.setPickable(true);
			int result = Mi_VETO;
			MiiTransaction transaction = null;
			if (currentDropTarget != null)
				{
				// FIX: Alter obj here to make it the same size in new editor
				// as it was in source editor if it is going to be copied directly
				// into the new editor.

				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
					MiDebug.println("Target is importing data of Drag-And-Drop");

				transaction = new MiNestedTransaction(Mi_DRAG_AND_DROP_DISPLAY_NAME);
				MiSystem.getTransactionManager().startTransaction(transaction);

				// ---------------------------------------------------------------
				// Send Mi_DATA_IMPORT_ACTION + Mi_EXECUTE_ACTION_PHASE action to 
				// target of drop to allow it, or one of it's containers, to have 
				// an actionHandler that implements the import action.
				// ---------------------------------------------------------------
				result = dispatchAction(Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);

				if (result == Mi_PROPOGATE)
					{
					// ---------------------------------------------------------------
					// There wasn't an action handler that wanted to do this, call
					// the target's doImport method instead.
					// ---------------------------------------------------------------
					if (MiDebug.debug 
						&& MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
						{
						MiDebug.println("Target of Drag-And-Drop has no "
							+ "action handler that implements data import, "
							+ "calling the target's doImport() method instead");
						}
					currentDropTarget.doImport(transferOp);
					}
				}

			if ((currentDropTarget == null) || (result == Mi_VETO))
				{
				// ---------------------------------------------------------------
				// Invalid drop operation or drop operation vetoed at last moment.
				// Animate the return of the dragged part to it's original 
				// location.
				// ---------------------------------------------------------------
				if (transaction != null)
					{
					MiSystem.getTransactionManager().rollbackTransaction();
					}

				MiTranslateAnimator animator = new MiTranslateAnimator(
							draggedPart, startingLocation, 1.5, 0.05);
				animator.setPacer(new MiSlowStartSlowStopPacer());
				animator.scheduleAndWait();

				dispatchAction(Mi_DRAG_AND_DROP_VETO_ACTION);
				}
			else // if (result != Mi_CONSUME)
				{
				dispatchAction(Mi_DRAG_AND_DROP_COMMIT_ACTION);
				dispatchAction(Mi_DATA_IMPORT_ACTION);
				dispatchAction(Mi_DRAG_AND_DROP_EXIT_ACTION);
				if ((draggingAndCutting) && (sourcePart.getContainingEditor() != null))
					{
					if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
						MiDebug.println("Cutting (removing) source of Drag-And-Drop from it's container");

					MiDeletePartsCommand deleteCmd = new MiDeletePartsCommand(
						sourcePart.getContainingEditor(), sourcePart, true);
					deleteCmd.doit(sourcePart.getContainingEditor(), true);
					deleteCmd.setName(MiiDisplayNames.Mi_CUT_DISPLAY_NAME);
					MiSystem.getTransactionManager().appendTransaction(deleteCmd);
					}
				if (transaction != null)
					{
					MiSystem.getTransactionManager().commitTransaction(transaction);
					}
				}
//MiDebug.println("" + MiSystem.getTransactionManager());
			rootWindow.removeAttachment(draggedPart);
			rootWindow.restoreNormalMouseAppearance();
			rootWindow.removeGrabEventHandler(this);

			// FIX? only do this is 'makeReference' is true
			if (draggedPart instanceof MiReference)
				{
//MiDebug.println(draggedPart + " dragged look is a reference, removing all parts");
				draggedPart.removeAllParts();
				}

			sourcePart = null;
			draggedPart = null;
			transferOp = null;
			draggingAndCopying = false;
			draggingAndCutting = false;
			currentDropTarget = null;
			return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}

					/**------------------------------------------------------
	 				 * This method determines if the gien part is a valid drop
					 * target and if so sets a number of class variables and
					 * also handles change of dragUnder and dragOver effects.
					 * @param rootWindow 		the window
					 * @param dropOnObj 		the target candidate
					 * @param isValid 		true if valid
					 *------------------------------------------------------*/
	protected	void		validateDropTarget(MiWindow rootWindow, MiPart dropOnObj, boolean isValid)
		{
		if (isValid)
			{
			rootWindow.setMouseAppearance(Mi_HAND_CURSOR);
			if (currentDropTarget != dropOnObj)
				{
				if (currentDropTarget != null)
					{
					setupTransferContext();
					currentDropTarget.dispatchAction(Mi_DRAG_AND_DROP_EXIT_ACTION, transferOp);
					draggedPart = drappedPartNormalAppearance;
					}
				currentDropTarget = dropOnObj;
				setupTransferContext();
				currentDropTarget.dispatchAction(Mi_DRAG_AND_DROP_ENTER_ACTION, transferOp);
				}
			if (draggedPart != transferOp.getLook())
				draggedPart = transferOp.getLook();
			}
		else
			{
			if (currentDropTarget != null)
				{
				setupTransferContext();
				currentDropTarget.dispatchAction(Mi_DRAG_AND_DROP_EXIT_ACTION, transferOp);
				draggedPart = drappedPartNormalAppearance;
				}
			currentDropTarget = null;
			rootWindow.setMouseAppearance(Mi_WAIT_CURSOR);
			}
		}
					/**------------------------------------------------------
	 				 * Determine whether the given part is a valid drop target.
					 * The process is as follows:
					 *
					 * 1. Does it's isDragAndDropTarget() method return true?
					 *
					 * 2. Does the dndSourceBehavior indicate that the drop target
					 * candidate is a valid target?
					 *
					 * 3. Is there a common data transfer format between the
					 * source and the candidate drop target?
					 *
					 * 4. Does the drop target OK (i.e. does not veto) a 
					 * Mi_DATA_IMPORT_ACTION request?
					 *
					 * 5. Does the drop target OK the call to it's
					 * supportsImportOfSpecificInstance() method?
					 *
					 * @param rootWindow	the window
					 * @param dropOnObj	the candidate drop target.
					 * @return 		true if a valid drop target
					 *------------------------------------------------------*/
	protected	boolean		isValidDropTarget(MiWindow rootWindow, MiPart dropOnObj)
		{
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
			MiDebug.println("Evaluating validity of target of Drag-And-Drop: " + dropOnObj);

		if ((dropOnObj != null) && (dropOnObj.isDragAndDropTarget()))
			{
			MiParts validTargets = dndSourceBehavior.getValidTargets();
			if ((validTargets != null) && (!validTargets.contains(dropOnObj)))
				{
				if (MiDebug.debug 
					&& MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
					{
					MiDebug.println("Target not valid: not found in specified"
						+ " drag-and-drop behavior valid target list");
					}
				return(false);
				}

			// ---------------------------------------------------------------
			// A target exists and support an import format that the dragged
			// object exports. Now set up a transfer operation object and
			// check if the target is also agreeable by sending it a 
			// Mi_DATA_IMPORT_ACTION request and calling it's 
			// supportsImportOfSpecificInstance method.
			// ---------------------------------------------------------------
			String format = MiDataTransferOperation.getCommonDataFormat(
				transferOp.getSource(), dropOnObj);
			if (format != null)
				{
				MiBounds bounds = draggedPart.getBounds();
				MiEditor dropOnEditor = dropOnObj.getContainingEditor();
				rootWindow.transformToOtherEditorSpace(dropOnEditor, bounds, bounds);
				transferOp.setDataFormat(format);

				transferOp.setLook(draggedPart);
				transferOp.setLookTargetPosition(bounds.getCenter());
				transferOp.setLookTargetBounds(bounds);
				transferOp.setTarget(dropOnObj);

				if ((dropOnObj.dispatchActionRequest(Mi_DATA_IMPORT_ACTION, transferOp))
					&& (dropOnObj.supportsImportOfSpecificInstance(transferOp)))
					{
					if (MiDebug.debug 
						&& MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
						{
						MiDebug.println("Target is valid");
						}
					return(true);
					}
				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
					MiDebug.println("Target not valid: vetoed Drag-And-Drop");
				return(false);
				}
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
				MiDebug.println("Target not valid: does not support a common data format");
			return(false);
			}

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
			MiDebug.println("Target not valid: does not support drag-and-drop");
		return(false);
		}
					/**------------------------------------------------------
	 				 * This method fills in the transfer context with current
					 * information.
					 * @param transferOp	the transfer operation context
					 *------------------------------------------------------*/
	protected	void		setupTransferContext()
		{
		draggedPart.getBounds(tmpBounds);
		tmpCursorBounds.copy(rootCursorBounds);
		if (currentDropTarget != null)
			{
			MiEditor dropOnEditor = currentDropTarget.getContainingEditor();
			// If currentDropTarget has not been replaced by the dnd...
			if (dropOnEditor != null)
				{
				rootWindow.transformToOtherEditorSpace(dropOnEditor, tmpBounds, tmpBounds);
				rootWindow.transformToOtherEditorSpace(
					dropOnEditor, tmpCursorBounds, tmpCursorBounds);
				}

			String format = MiDataTransferOperation.getCommonDataFormat(
				transferOp.getSource(), currentDropTarget);
			transferOp.setDataFormat(format);
			}
		transferOp.setLook(draggedPart);
		transferOp.setLookTargetPosition(tmpCursorBounds.getCenter());
		transferOp.setLookTargetBounds(tmpBounds);
		transferOp.setTarget(currentDropTarget);
		}

	protected	int		dispatchAction(int action)
		{
		int result;
		setupTransferContext();

//MiDebug.println("dispatchAction: " + action);
//MiDebug.println("dispatchAction: currentDropTarget " + currentDropTarget);
//MiDebug.println("dispatchAction: transferOp.getSource() " + transferOp.getSource());
		if (currentDropTarget != null)
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
				{
				MiDebug.println("Dispatching action: \"" + MiAction.getActionString(action)
					+ "\" to current drop target: " + currentDropTarget);
				}
			result = currentDropTarget.dispatchAction(action, transferOp);
			}
		else
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
				{
				MiDebug.println("Dispatching action: " + action 
					+ " to current drop source (no drop target found yet): " 
					+ transferOp.getSource());
				}
			result = transferOp.getSource().dispatchAction(action, transferOp);
			}
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_DRAG_AND_DROP))
			{
			MiDebug.println("Result of dispatched action: " + (result == Mi_PROPOGATE 
				? "Mi_PROPOGATE" : (result == Mi_VETO ? "Mi_VETO" : "Mi_CONSUME")));
			}
		return(result);
		}
	}

