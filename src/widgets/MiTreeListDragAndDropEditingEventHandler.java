
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
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiTreeListDragAndDropEditingEventHandler extends MiEventHandler implements MiiTypes
	{
	public static final String	START_MOVE_ROW_COMMAND_NAME	= "StartMoveRow";
	public static final String	MOVE_ROW_COMMAND_NAME		= "MoveRow";
	public static final String	END_MOVE_ROW_COMMAND_NAME	= "EndMoveRow";
	public static final String	CANCEL_MOVE_ROW_COMMAND_NAME	= "CancelMoveRow";

	public static final int		CANCEL_OPERATION			= 0;
	public static final int		PASTE_AS_CHILD_OF_TARGET_OPERATION	= 1;
	public static final int		PASTE_AS_SIBLING_OF_TARGET_OPERATION	= 2;

	private		MiTreeList	treeList;
	private		int		movingRow			= -1;
	private		Object		movingRowTag;
	private		MiPart		movingRowImage			= null;
	private		MiPoint		movingImageCenterOffset		= new MiPoint();
	private		MiBounds	tmpBounds			= new MiBounds();


	public				MiTreeListDragAndDropEditingEventHandler()
		{
		addEventToCommandTranslation(START_MOVE_ROW_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(MOVE_ROW_COMMAND_NAME, 
			Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(END_MOVE_ROW_COMMAND_NAME, 
			Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(CANCEL_MOVE_ROW_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN);
		}
					/**------------------------------------------------------
	 				 * Sets the MiPart that this is assigned to. This is set
					 * when this is assigned to a part.
					 * @param part		the target part
					 * @implements		MiiEventHandler#setTarget
					 * @overrides		MiEventHandler#setTarget
					 *------------------------------------------------------*/
	public		void		setTarget(MiPart part)
		{
		treeList = (MiTreeList )part;
		}
	public		int		processCommand()
		{
		MiBounds cursorArea = event.getMouseFootPrint(new MiBounds());
		cursorArea.setWidth(cursorArea.getWidth() * 2);
		cursorArea.setHeight(cursorArea.getHeight() * 2);
		MiTableCell cell = treeList.pickCellIncludingHeadersAndFooters(cursorArea);
		if ((movingRow == -1) 
			&& ((cell == null)
			|| (cell.rowNumber == MiTable.ROW_HEADER_NUMBER)
			|| (cell.rowNumber == MiTable.ROW_FOOTER_NUMBER)))
			{
			return(Mi_PROPOGATE_EVENT);
			}

		// ---------------------------------------
		//	Sorting Rows and Columns
		// ---------------------------------------
		if (isCommand(START_MOVE_ROW_COMMAND_NAME))
			{
			if (movingRow != -1)
				{
				return(Mi_PROPOGATE_EVENT);
				}

			if ((cell.rowNumber >= 0)
				&& (cell.rowNumber != MiTable.ROW_HEADER_NUMBER)
				&& (cell.rowNumber != MiTable.ROW_FOOTER_NUMBER))
				{
				movingRow = cell.rowNumber;
				movingRowImage = getMovingRowImage(movingRow, event.getWorldPoint(new MiPoint()));
				movingImageCenterOffset.x = movingRowImage.getCenterX() - event.worldPt.x;
				movingImageCenterOffset.y = movingRowImage.getCenterY() - event.worldPt.y;
				treeList.appendAttachment(movingRowImage);
				event.editor.prependGrabEventHandler(this);
				}
			if (movingRow != -1)
				{
				return(Mi_CONSUME_EVENT);
				}
			}
		else if (isCommand(MOVE_ROW_COMMAND_NAME))
			{
			if (movingRow != -1)
				{
				movingRowImage.setCenter(
					event.worldPt.x + movingImageCenterOffset.x,
					event.worldPt.y + movingImageCenterOffset.y);
				return(Mi_CONSUME_EVENT);
				}
			}
		else if (isCommand(CANCEL_MOVE_ROW_COMMAND_NAME))
			{
			if (movingRow != -1)
				{
				movingRow = -1;
				treeList.removeAttachment(movingRowImage);
				movingRowImage = null;
				event.editor.removeGrabEventHandler(this);
				return(Mi_CONSUME_EVENT);
				}
			}
		else if (isCommand(END_MOVE_ROW_COMMAND_NAME))
			{
			if (movingRow != -1)
				{
				cell = treeList.pickCell(cursorArea);
				if ((cell != null) && (cell.rowNumber != movingRow)
					&& (cell.rowNumber != MiTable.ROW_HEADER_NUMBER)
					&& (cell.rowNumber != MiTable.ROW_FOOTER_NUMBER))
					{
					Object movedRowTag = treeList.getTagItem(movingRow);
					Object targetRowTag = treeList.getTagItem(cell.rowNumber);
					int operation = getWhichTreeListSubtreeCutAndPasteOperation(movedRowTag, targetRowTag);
					if (operation == CANCEL_OPERATION)
						{
						}
					else if (operation == PASTE_AS_CHILD_OF_TARGET_OPERATION)
						{
						MiiTreeListEntry data = treeList.cutItemWithTag(movedRowTag);
						treeList.pasteItemWithTag(targetRowTag, data);
						}
					else if (operation == PASTE_AS_SIBLING_OF_TARGET_OPERATION)
						{
						MiiTreeListEntry data = treeList.cutItemWithTag(movedRowTag);
						treeList.pasteItemWithTag(treeList.getParentOfItem(targetRowTag), data);
						}
					}
				movingRow = -1;
				treeList.removeAttachment(movingRowImage);
				movingRowImage = null;
				event.editor.removeGrabEventHandler(this);
				return(Mi_CONSUME_EVENT);
				}
			}

		return(Mi_CONSUME_EVENT);
		}
					/**------------------------------------------------------
	 				 * Gets the operation to performed for the given, particular,
					 * subtrees. Overrides this for particular applications.
					 * Options are:
					 *
					 * CANCEL_OPERATION
					 * PASTE_AS_CHILD_OF_TARGET_OPERATION
					 * PASTE_AS_SIBLING_OF_TARGET_OPERATION
					 *
					 * @param movedRowTag	the tag of the moved treeList row (column 0) 
					 * @param targetRowTag	the tag of the target treeList row (column 0)
					 *------------------------------------------------------*/
	public		int		getWhichTreeListSubtreeCutAndPasteOperation(Object movedRowTag, Object targetRowTag)
		{
		return(PASTE_AS_CHILD_OF_TARGET_OPERATION);
		}
	protected	MiPart		getMovingRowImage(int rowNumber, MiPoint pickupPt)
		{
		Object tag = treeList.getTagItem(rowNumber);

		MiPart movingRowImage = treeList.getItemWithTag(tag).deepCopy();
		
		movingRowImage.setHasShadow(true);

		movingRowImage.setCenter(pickupPt);

		return(movingRowImage);
		}

/*
	protected	MiPart		getMovingRowImage(int rowNumber, MiPoint pt)
		{
		MiPart movingRowImage = null;

		tmpBounds.reverse();
		if (treeList.getCell(movingRow, MiTable.COLUMN_HEADER_NUMBER) != null)
			{
			treeList.getTableLayout().getCellBounds(
				movingRow, MiTable.COLUMN_HEADER_NUMBER, tmpBounds);
			}
		if (treeList.getCell(movingRow, MiTable.COLUMN_FOOTER_NUMBER) != null)
			{
			tmpBounds.union(treeList.getTableLayout().getCellBounds(
				movingRow, MiTable.COLUMN_FOOTER_NUMBER, new MiBounds()));
			}
		tmpBounds.union(treeList.getRowBounds(movingRow, 0, -1, new MiBounds()));

		movingRowImage = new MiImage(treeList.makeImageFromArea(tmpBounds));
		movingRowImage.setCenter(tmpBounds.getCenter());

		movingRowImage.setHasShadow(true);
		return(movingRowImage);
		}
*/
	}

