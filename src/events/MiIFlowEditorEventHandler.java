
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
public class MiIFlowEditorEventHandler extends MiEventHandler
	{
	private		MiiFlowEditor	textEditor;
	private		MiPoint		tmpPoint			= new MiPoint();
	private		MiPoint		startPos;
	private		boolean		selectAllUponKeyboardFocus	= false; // true;
	private		boolean		selectEntireTextAsPartInEditor;
	private		boolean		mustDoubleClickToEdit;
	private		boolean		hasKeyboardFocus;

	public				MiIFlowEditorEventHandler()
		{
		}
	public				MiIFlowEditorEventHandler(MiiFlowEditor textEditor)
		{
		this.textEditor = textEditor;
		}
	public		void		setTextEditor(MiiFlowEditor textEditor)
		{
		this.textEditor = textEditor;
		}
	public		void		setSelectAllUponKeyboardFocus(boolean flag)
		{
		selectAllUponKeyboardFocus = flag;
		}
	public		boolean		getSelectAllUponKeyboardFocus()
		{
		return(selectAllUponKeyboardFocus);
		}
	public		void		setSelectEntireTextAsPartInEditor(boolean flag)
		{
		selectEntireTextAsPartInEditor = flag;
		}
	public		boolean		getSelectEntireTextAsPartInEditor()
		{
		return(selectEntireTextAsPartInEditor);
		}
	public		void		setMustDoubleClickToEdit(boolean flag)
		{
		mustDoubleClickToEdit = flag;
		}
	public		boolean		getMustDoubleClickToEdit()
		{
		return(mustDoubleClickToEdit);
		}
	public		void		setCursorPosition(MiPoint pt)
		{
		textEditor.setCursorPosition(pt);
		}
	public		void		updateCursorPosition()
		{
		textEditor.setCursorPosition(textEditor.getCursorPosition());
		}

	public		void		setKeyboardFocus(boolean flag)
		{
		if ((!mustDoubleClickToEdit) || textEditor.isEmpty() || (!flag))
			{
			textEditor.makeCursorVisible(flag);
			if (flag && selectAllUponKeyboardFocus)
				textEditor.selectAll();
			else
				textEditor.deSelectAll();

			textEditor.moveCursorPositionToRowEnd();
			hasKeyboardFocus = flag;
			}
		}
	public		boolean		hasKeyboardFocus()
		{
		return(hasKeyboardFocus);
		}
	public		void		setCursorPosition(int pos)
		{
		textEditor.setCursorPosition(pos);
		}
	public		int		getCursorPosition()
		{
		return(textEditor.getCursorPosition());
		}
	public		int		processEvent(MiEvent event)
		{
		if (event.type == Mi_LEFT_MOUSE_DBLCLICK_EVENT)
			{
			if ((mustDoubleClickToEdit) && (!hasKeyboardFocus))
				{
				if (event.getMovedKeyboardFocusTo() == null)
					{
					textEditor.deSelectAll();
					}
				textEditor.setCursorPosition(event.worldPt);
				mustDoubleClickToEdit = false;
				setKeyboardFocus(true);
				mustDoubleClickToEdit = true;
				}
			else
				{
				textEditor.selectWordAtCursor();
				}
			}
		else if (!hasKeyboardFocus)
			{
			return(Mi_PROPOGATE_EVENT);
			}
		else if (event.type == Mi_LEFT_MOUSE_DOWN_EVENT)
			{
			 if (hasKeyboardFocus)
				{
				if (event.getMovedKeyboardFocusTo() == null)
					{
					textEditor.deSelectAll();
					}
				textEditor.setCursorPosition(event.worldPt);
				}
			if (selectEntireTextAsPartInEditor)
				{
				textEditor.select(true);
				}
			}
		else if (event.type == Mi_LEFT_MOUSE_CLICK_EVENT)
			{
			textEditor.deSelectAll();
			}
		else if (event.type == Mi_LEFT_MOUSE_UP_EVENT)
			{
			if (startPos != null)
				{
				startPos = null;
				event.editor.removeGrabEventHandler(this);
				}
			}
		else if (event.type == Mi_LEFT_MOUSE_TRIPLECLICK_EVENT)
			{
			textEditor.selectParagraphAtCursor();
			}
		else if (event.type == Mi_LEFT_MOUSE_START_DRAG_EVENT)
			{
			if (hasKeyboardFocus)
				{
				// Assure that all grab events are in the target's coordinate space. 12-15-2002
				setObject(getTarget());
				startPos = new MiPoint(event.worldPt);
				textEditor.deSelectAll();
				textEditor.setCursorPosition(event.worldPt);
				event.editor.prependGrabEventHandler(this);
				}
			else
				{
				return(Mi_PROPOGATE_EVENT);
				}
			}
		else if (event.type == Mi_LEFT_MOUSE_DRAG_EVENT)
			{
			if (startPos != null)
				{
				textEditor.setCursorPosition(event.getWorldPoint(tmpPoint));
				textEditor.selectItems(new MiPoint(startPos), event.getWorldPoint(tmpPoint));
				}
			}
		else if ((event.type == Mi_KEY_EVENT) && (hasKeyboardFocus))
			{
			switch (event.key)
				{
				case Mi_HOME_KEY:
					textEditor.deSelectAll();
					textEditor.moveCursorPositionToRowStart();
					break;
				case Mi_LEFT_ARROW_KEY:
					textEditor.deSelectAll();
					textEditor.moveCursorPositionLeft();
					break;
				case Mi_RIGHT_ARROW_KEY:
					textEditor.deSelectAll();
					textEditor.moveCursorPositionRight();
					break;
				case Mi_UP_ARROW_KEY:
					if (!textEditor.supportsMultipleLines())
						return(Mi_PROPOGATE_EVENT);
					textEditor.deSelectAll();
					textEditor.moveCursorPositionUp();
					break;
				case Mi_DOWN_ARROW_KEY:
					if (!textEditor.supportsMultipleLines())
						return(Mi_PROPOGATE_EVENT);
					textEditor.deSelectAll();
					textEditor.moveCursorPositionDown();
					break;
				case Mi_END_KEY:
					textEditor.deSelectAll();
					textEditor.moveCursorPositionToRowEnd();
					break;
				case Mi_DELETE_KEY:
					if (textEditor.hasSelectedItems())
						textEditor.deleteSelectedItems();
					else
						textEditor.deleteItemAtCursor();
					break;
				case Mi_BACKSPACE_KEY:
					if (textEditor.hasSelectedItems())
						textEditor.deleteSelectedItems();
					else
						textEditor.deleteItemToLeftOfCursor();
					break;
				case Mi_ENTER_KEY:
					if (textEditor.supportsMultipleLines())
						{
						textEditor.insertEndOfParagraphAtCursor();
						textEditor.moveCursorPositionDown();
						}
					else
						{
						textEditor.broadcastEnterKeyAction();
						}
					textEditor.deSelectAll();
					textEditor.moveCursorPositionToRowStart();
					break;
				case Mi_ESC_KEY:
					textEditor.deSelectAll();
					break;
				default:
					{
					int ch = event.key;
					if ((ch >= ' ') && (ch <= '~'))
						{
						textEditor.insertItemAtCursor((char )ch);
						textEditor.moveCursorPositionRight();
						}
					else // FIX, check if target wants tab key, ctl keys, ...
						{
						return(Mi_PROPOGATE_EVENT);
						}
					}
				}
			}
		else
			{
			return(Mi_PROPOGATE_EVENT);
			}
		return(Mi_CONSUME_EVENT);
		}
	}

