
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
 * This is the interface for objects that can be assigned to
 * any part or to the drag and drop manager. It allows any 
 * part or container to override what events (usually dragging
 * the mouse with a button held down) are involved in a drag
 * and drop operation. It also allows overriding what effects
 * are visible during drag and drop operations.
 *
 * Drag and drop operations are divided into two basic 
 * operations:
 *
 *	Drag and Copy		- which leaves the part that was
 *				'picked up' where it was after the
 *				operation and,
 *
 *	Drag and Cut		- which deletes the part that was
 *				'picked up' after the operation
 *				(i.e. it 'moves' the part).
 *
 * @see MiPart#setDragAndDropBehavior
 * @see MiPart#getDragAndDropBehavior
 * @see MiDragAndDropManager
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiDragAndDropBehavior
	{
	//void		setIsDefaultBehaviorForParts(boolean flag);
	boolean		isDefaultBehaviorForParts();
	//void		setDragsReferenceNotCopy(boolean flag);
	boolean		getDragsReferenceNotCopy();
	boolean		getSnapLookCenterToCursor();
	boolean		getKeepLookCompletelyWithinRootWindow();
	boolean		isPartDragAndDropSource(MiPart part);
	boolean		isPartDragAndDropTarget(MiPart part);
			/**------------------------------------------------------
	 		 * Gets whether, if the part with this behavior rejects a
			 * drop, the parent of the part can then be inquired as to
			 * whether it wants the drop.
			 * @return 		true if cannot drop on part(s) 
			 *			underneath the part assigned this 
			 * 			behavior
			 *------------------------------------------------------*/
	boolean		isOpaqueDragAndDropTarget();
// FIX: boolean isDragAndCopyDragEvent() to allow for multiple events for dnd

			/**------------------------------------------------------
	 		 * Gets the event that will 'pick up' a part to start a
			 * drag-and-copy operation.
			 * @return 		the pickup event
			 *------------------------------------------------------*/
	MiEvent		getDragAndCopyPickUpEvent();


			/**------------------------------------------------------
	 		 * Gets the event that will move a part during a
			 * drag-and-copy operation.
			 * @return		the drag event
			 *------------------------------------------------------*/
	MiEvent		getDragAndCopyDragEvent();


			/**------------------------------------------------------
	 		 * Gets the event that will cancel a drag-and-copy 
			 * operation.
			 * @return		the cancel event
			 *------------------------------------------------------*/
	MiEvent		getDragAndCopyCancelEvent();


			/**------------------------------------------------------
	 		 * Gets the event that will complete a drag-and-copy 
			 * operation.
			 * @return 		the drop event
			 *------------------------------------------------------*/
	MiEvent		getDragAndCopyDropEvent();
	

			/**------------------------------------------------------
	 		 * Gets the event that will 'pick up' a part to start a
			 * drag-and-cut operation.
			 * @return 		the pickup event
			 *------------------------------------------------------*/
	MiEvent		getDragAndCutPickUpEvent();


			/**------------------------------------------------------
	 		 * Gets the event that will move a part during a
			 * drag-and-cut operation.
			 * @return		the drag event
			 *------------------------------------------------------*/
	MiEvent		getDragAndCutDragEvent();


			/**------------------------------------------------------
	 		 * Gets the event that will cancel a drag-and-cut 
			 * operation.
			 * @return		the cancel event
			 *------------------------------------------------------*/
	MiEvent		getDragAndCutCancelEvent();


			/**------------------------------------------------------
	 		 * Gets the event that will complete a drag-and-cut 
			 * operation.
			 * @return 		the drop event
			 *------------------------------------------------------*/
	MiEvent		getDragAndCutDropEvent();


			/**------------------------------------------------------
	 		 * Sets the valid targets for drag and drop for parts with
			 * this behavior. If null (the default) then all targets
			 * are valid.
			 * @param targets	the valid targets or null
			 *------------------------------------------------------*/
	void		setValidTargets(MiParts targets);

			/**------------------------------------------------------
	 		 * Gets the valid targets for drag and drop for parts with
			 * this behavior. If null (the default) then all targets
			* are valid.
			 * @return 		the valid targets or null
			 *------------------------------------------------------*/
	MiParts		getValidTargets();

			/**------------------------------------------------------
	 		 * Gets the data to be exported during the drop operation
			 * for parts with this behavior. If null (the default) then
			 * the source's doExport method is used to get the data. 
			 * @return 		the export data or null
			 *------------------------------------------------------*/
	Object		getDataToExport();

			/**------------------------------------------------------
	 		 * Gets the appearance to be used during the drag operation
			 * for parts with this behavior. If null (the default) then
			 * the source's look is copied. 
			 * @return 		the look or null
			 *------------------------------------------------------*/
	MiPart		getDraggingLook();

	MiEditor	getTargetEditor();
	}



