
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
 * This class implements the MiiDragAndDropBehavior interface.
 * At the present time it only supports the specification of
 * the events involved. Common effects will implemented in a
 * future release.
 * <p>
 * For example, if it so desired that a treeList widget is to be
 * a drag and drop source and that the end-user should not have to
 * hold down the shift key while dragging items out of the treelist
 * then one might have:
 *
 * <pre>
 *	// Assume that the items in the tree list will have called
 *	// their setIsDragAndDropSource(true) methods...
 *
 *	MiTreeList treeList = new MiTreeList(28, false);
 *
 *	MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
 *
 *	dndBehavior.setDragAndCopyPickUpEvent(
 *		new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
 *	dndBehavior.setDragAndCopyDragEvent(
 *		new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
 *	dndBehavior.setDragAndCopyCancelEvent(
 *		new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
 *	dndBehavior.setDragAndCopyDropEvent(
 *		new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));
 *
 *	treeList.setDragAndDropBehavior(dndBehavior);
 *
 * </pre>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDragAndDropBehavior implements MiiDragAndDropBehavior
	{
	private		MiEvent		dragAndCopyPickUpEvent;
	private		MiEvent		dragAndCopyDragEvent;
	private		MiEvent		dragAndCopyCancelEvent;
	private		MiEvent		dragAndCopyDropEvent;
	
	private		MiEvent		dragAndCutPickUpEvent;
	private		MiEvent		dragAndCutDragEvent;
	private		MiEvent		dragAndCutCancelEvent;
	private		MiEvent		dragAndCutDropEvent;

	private		boolean		defaultBehaviorForParts	= true;
	private		boolean		dragReferenceNotCopy	= true;
	private		boolean		snapLookCenterToCursor	= true;
	private		boolean		keepLookCompletelyWithinRootWindow= true;
	private		boolean		isOpaqueDragAndDropTarget;

	private		MiParts		validTargets;
	private		Object		dataToExport;
	private		MiPart		draggingLook;
	private		MiEditor	targetEditor;

	private		int		dragEffect;
	private		int		dragOverEffect;
	private		int		dragUnderEffect;
	private		int		dropEffect;


					/**------------------------------------------------------
	 				 * Constructs a new MiDragAndDropBehavior. 
					 *------------------------------------------------------*/
	public				MiDragAndDropBehavior()
		{
		}

	public		void		setIsDefaultBehaviorForParts(boolean flag)
		{
		defaultBehaviorForParts = flag;
		}
	public		boolean		isDefaultBehaviorForParts()
		{
		return(defaultBehaviorForParts);
		}

	public		void		setDragsReferenceNotCopy(boolean flag)
		{
		dragReferenceNotCopy = flag;
		}
	public		boolean		getDragsReferenceNotCopy()
		{
		return(dragReferenceNotCopy);
		}
	public		void		setSnapLookCenterToCursor(boolean flag)
		{
		snapLookCenterToCursor = flag;
		}
	public		boolean		getSnapLookCenterToCursor()
		{
		return(snapLookCenterToCursor);
		}
	public		void		setKeepLookCompletelyWithinRootWindow(boolean flag)
		{
		keepLookCompletelyWithinRootWindow = flag;
		}
	public		boolean		getKeepLookCompletelyWithinRootWindow()
		{
		return(keepLookCompletelyWithinRootWindow);
		}
	public		boolean		isPartDragAndDropSource(MiPart part)
		{
		return((part.isDragAndDropSource())
			&& (part.getEventHandlingEnabled())
			&& (!part.getEventHandlingDisabledByContainer()));
		}
	public		boolean		isPartDragAndDropTarget(MiPart part)
		{
		return(part.isDragAndDropTarget());
		}
	public		void		setValidTargets(MiParts targets)
		{
		validTargets = new MiParts(targets);
		}
	public		MiParts		getValidTargets()
		{
		return(validTargets == null ? null : new MiParts(validTargets));
		}
	public		void		setDataToExport(Object data)
		{
		dataToExport = data;
		}
	public		Object		getDataToExport()
		{
		return(dataToExport);
		}
	public		void		setDraggingLook(MiPart look)
		{
		draggingLook = look;
		}
	public		MiPart		getDraggingLook()
		{
		return(draggingLook);
		}
	// Causes dragged part to be scaled to size it will be if dropped on target
	public		void		setTargetEditor(MiEditor editor)
		{
		targetEditor = editor;
		}
	public		MiEditor	getTargetEditor()
		{
		return(targetEditor);
		}
					/**------------------------------------------------------
	 				 * Gets whether, if the part with this behavior rejects a
					 * drop, the parent of the part can then be inquired as to
					 * whether it wants the drop. False is the default value.
					 * @return 		true if cannot drop on part(s) 
					 *			underneath the part assigned this 
					 * 			behavior
					 * @see			setIsOpaqueDragAndDropTarget
					 * @implements		MiiDragAndDropBehavior#isOpaqueDragAndDropTarget
					 *------------------------------------------------------*/
	public		boolean		isOpaqueDragAndDropTarget()
		{
		return(isOpaqueDragAndDropTarget);
		}
					/**------------------------------------------------------
	 				 * Sets whether, if the part with this behavior rejects a
					 * drop, the parent of the part can then be inquired as to
					 * whether it wants the drop.
					 * @param flag 		true if cannot drop on part(s) 
					 *			underneath the part assigned this 
					 * 			behavior
					 * @see			isOpaqueDragAndDropTarget
					 *------------------------------------------------------*/
	public		void		setIsOpaqueDragAndDropTarget(boolean flag)
		{
		isOpaqueDragAndDropTarget = flag;
		}
					/**------------------------------------------------------
	 				 * Sets the event that will 'pick up' a part to start a
			 		 * drag-and-copy operation. A null indicates that all
			 		 * the drag-and-copy events are unspecified.
					 * @param event		the pickup event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		void		setDragAndCopyPickUpEvent(MiEvent event)
		{
		dragAndCopyPickUpEvent = new MiEvent(event);
		}
					/**------------------------------------------------------
	 				 * Sets the event that will move a part during a
					 * drag-and-copy operation.
					 * @param event		the drag event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		void		setDragAndCopyDragEvent(MiEvent event)
		{
		dragAndCopyDragEvent = new MiEvent(event);
		}
					/**------------------------------------------------------
	 				 * Sets the event that will cancel a drag-and-copy 
					 * operation.
					 * @param event		the cancel event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		void		setDragAndCopyCancelEvent(MiEvent event)
		{
		dragAndCopyCancelEvent = new MiEvent(event);
		}
					/**------------------------------------------------------
	 				 * Sets the event that will complete a drag-and-copy 
					 * operation.
					 * @param event		the drop event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		void		setDragAndCopyDropEvent(MiEvent event)
		{
		dragAndCopyDropEvent = new MiEvent(event);
		}
	
					/**------------------------------------------------------
	 				 * Gets the event that will 'pick up' a part to start a
					 * drag-and-copy operation.
					 * @return 		the pickup event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		MiEvent		getDragAndCopyPickUpEvent()
		{
		return(dragAndCopyPickUpEvent);
		}
					/**------------------------------------------------------
	 				 * Gets the event that will move a part during a
					 * drag-and-copy operation.
					 * @return		the drag event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		MiEvent		getDragAndCopyDragEvent()
		{
		return(dragAndCopyDragEvent);
		}
					/**------------------------------------------------------
	 				 * Gets the event that will cancel a drag-and-copy 
					 * operation.
					 * @return		the cancel event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		MiEvent		getDragAndCopyCancelEvent()
		{
		return(dragAndCopyCancelEvent);
		}
					/**------------------------------------------------------
	 				 * Gets the event that will complete a drag-and-copy 
					 * operation.
					 * @return 		the drop event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		MiEvent		getDragAndCopyDropEvent()
		{
		return(dragAndCopyDropEvent);
		}

					/**------------------------------------------------------
	 				 * Sets the event that will 'pick up' a part to start a
			 		 * drag-and-cut operation. A null indicates that all
			 		 * the drag-and-cut events are unspecified.
					 * @param event		the pickup event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		void		setDragAndCutPickUpEvent(MiEvent event)
		{
		dragAndCutPickUpEvent = new MiEvent(event);
		}
					/**------------------------------------------------------
	 				 * Sets the event that will move a part during a
					 * drag-and-cut operation.
					 * @param event		the drag event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		void		setDragAndCutDragEvent(MiEvent event)
		{
		dragAndCutDragEvent = new MiEvent(event);
		}
					/**------------------------------------------------------
	 				 * Sets the event that will cancel a drag-and-cut 
					 * operation.
					 * @param event		the cancel event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		void		setDragAndCutCancelEvent(MiEvent event)
		{
		dragAndCutCancelEvent = new MiEvent(event);
		}
					/**------------------------------------------------------
	 				 * Sets the event that will complete a drag-and-cut 
					 * operation.
					 * @param event		the drop event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		void		setDragAndCutDropEvent(MiEvent event)
		{
		dragAndCutDropEvent = new MiEvent(event);
		}
	
					/**------------------------------------------------------
	 				 * Gets the event that will 'pick up' a part to start a
					 * drag-and-cut operation.
					 * @return 		the pickup event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		MiEvent		getDragAndCutPickUpEvent()
		{
		return(dragAndCutPickUpEvent);
		}
					/**------------------------------------------------------
	 				 * Gets the event that will move a part during a
					 * drag-and-cut operation.
					 * @return		the drag event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		MiEvent		getDragAndCutDragEvent()
		{
		return(dragAndCutDragEvent);
		}
					/**------------------------------------------------------
	 				 * Gets the event that will cancel a drag-and-cut 
					 * operation.
					 * @return		the cancel event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		MiEvent		getDragAndCutCancelEvent()
		{
		return(dragAndCutCancelEvent);
		}
					/**------------------------------------------------------
	 				 * Gets the event that will complete a drag-and-cut 
					 * operation.
					 * @return 		the drop event
					 * @implements 		MiiDragAndDropBehavior
					 *------------------------------------------------------*/
	public		MiEvent		getDragAndCutDropEvent()
		{
		return(dragAndCutDropEvent);
		}
	}

