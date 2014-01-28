
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
import com.swfm.mica.util.OrderedProperties;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTextFieldEditor implements MiiFlowEditor, MiiActionHandler
	{
	private		int		numberOfBlankCharsAtRightEndOfText;
	private		int		selectionStart;
	private		int		selectionEnd;
	private		MiText		textObj;
	private		MiPart		cursor;
	private		int		cursorPosition;
	private		boolean		cursorHasBeenAttachedToTextObj;
	private		MiBlinkAnimator	animator;

	public				MiTextFieldEditor(MiText t)
		{
		textObj = t;
		cursor = makeCursor(textObj.getFont().getHeight());
		cursor.setContextCursor(MiiTypes.Mi_TEXT_CURSOR);
		cursor.setVisible(false);
		if (animator == null)
			{
			textObj.appendAttachment(cursor);
			animator = new MiBlinkAnimator(cursor, 0.5);
			textObj.removeAttachment(cursor);
			}
		setCursorPosition(0);
		}
	public		void		broadcastEnterKeyAction()
		{
		textObj.dispatchAction(MiiActionTypes.Mi_ENTER_KEY_ACTION);
		}

	public		boolean		isEmpty()
		{
		return((textObj.getValue() == null) || (textObj.getValue().length() == 0));
		}

	protected	MiPart		makeCursor(MiDistance textHeight)
		{
		//return(new MiLine(0, -textHeight/4, 0, textHeight));
		MiRectangle cursor = new MiRectangle(0, -textHeight/4, 1, textHeight);
		cursor.setBackgroundColor(MiColorManager.black);
		return(cursor);
		}

	protected	void		refreshLookAndFeel()
		{
		String duration = MiSystem.getProperty("MiTextCursorBlinkRate");
		if (duration != null)
			animator.setStepTime(Utility.toDouble(duration));
		else
			animator.setStepTime(0.5);

		OrderedProperties properties = MiSystem.getPropertiesForClass("MiTextCursor");
		if (properties.size() > 0)
			{
			Strings keys = properties.getKeys();
			for (int i = 0; i < keys.size(); ++i)
				{
				String name = keys.elementAt(i);
				cursor.setPropertyValue(name, properties.getProperty(name));
				}
			}
		else
			{
			cursor.setAttributes(new MiAttributes());
			cursor.setBackgroundColor(MiColorManager.black);
			}
		}

	public		void		makeCursorVisible(boolean flag)
		{
		if (flag)
			{
			// If this
			if (textObj.getRootWindow() == null)
				return;

			if (!cursorHasBeenAttachedToTextObj)
				{
				cursorHasBeenAttachedToTextObj = true;
				
				refreshLookAndFeel();

				textObj.appendAttachment(cursor);
				if (animator.getSubject() != cursor)
					{
					animator.setSubject(cursor);
					}
				animator.schedule();
				// Keep our cursor position valid when someone 
				// chanegs the text underneath us...
				textObj.appendActionHandler(this, MiiActionTypes.Mi_TEXT_CHANGE_ACTION);
				}
			}
		else 
			{
			if (cursorHasBeenAttachedToTextObj)
				{
				cursorHasBeenAttachedToTextObj = false;
				animator.unschedule();
				textObj.removeAttachment(cursor);
				textObj.removeActionHandlers(this);
				}
			}
		animator.setEnabled(flag);
		cursor.setVisible(flag);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(MiiActionTypes.Mi_TEXT_CHANGE_ACTION))
			{
			// FIX: when an external process changes all of the text then position
			// cursor at beginning...
			initializeCursorPosition();
			deSelectAll();
			}
		return(true);
		}

	public		void		initializeCursorPosition()
		{
		if (textObj.getText() == null)
			setCursorPosition(0);
		else if (cursorPosition > textObj.getText().length())
			setCursorPosition(textObj.getText().length());
		}

	public		void		select(boolean flag)
		{
		if (flag)
			{
			textObj.getContainingEditor().select(textObj);
			}
		else
			{
			textObj.getContainingEditor().deSelect(textObj);
			}
		}

	public		void		deSelectAll()
		{
		textObj.setSelection(-1, -1);
		}

	public		void		selectAll()
		{
		if (textObj.getText().length() > 0)
			textObj.setSelection(0, textObj.getText().length());
		else
			deSelectAll();
		}

	public		void		selectParagraphAtCursor()
		{
		textObj.setSelection(0, textObj.getText().length());
		}

	public		void		selectWordAtCursor()
		{
		String text = textObj.getText();
		if (text.length() == 0)
			return;

		int selectionStart = cursorPosition;
		int selectionEnd = cursorPosition + 1;
		int pos = cursorPosition > text.length() - 1 ? text.length() - 1 : cursorPosition;

		for (int i = pos; i >= 0; --i)
			{
			if (!isWhiteSpace(text.charAt(i)))
				selectionStart = i;
			else
				break;
			}
		for (int i = pos; i < text.length(); ++i)
			{
			if (!isWhiteSpace(text.charAt(i)))
				selectionEnd = i;
			else
				break;
			}
		textObj.setSelection(selectionStart, selectionEnd + 1);
		}

	public		void		selectItems(MiPoint pt1, MiPoint pt2)
		{
		int startPos = convertLocationToCursorPosition(pt1);
		int endPos = convertLocationToCursorPosition(pt2);
		if ((startPos != -1) && (endPos != -1))
			{
			startPos = convertCursorPositionToCharPosition(startPos);
			boolean atEnd = isCursorPositionAtEndOfRow(endPos);
			endPos = convertCursorPositionToCharPosition(endPos);
			if (atEnd)
				++endPos;
			textObj.setSelection(Math.min(startPos, endPos), Math.max(startPos, endPos));
			}
		else
			{
			deSelectAll();
			}
		}

	public		boolean		hasSelectedItems()
		{
		return((textObj.getSelectionStart() < 0) ? false : true);
		}

	public		boolean		supportsMultipleLines()
		{
		return((textObj.getNumDisplayedRows() != 1) ? true : false);
		}
	public		void		setCursorPosition(MiPoint pt)
		{
		int position = convertLocationToCursorPosition(pt);
		if (position != -1)
			setCursorPosition(position);
		}
	public		void		updateCursorPosition()
		{
		setCursorPosition(cursorPosition);
		}
	public		void		setCursorPosition(int pos)
		{
		animator.setEnabled(false);
		cursor.setVisible(true);
		MiBounds b;
		if (textObj.getText().length() == 0)
			{
			// FIX - innerbounds of empty obj is undefined
			b = textObj.getInnerBounds(); 
			cursor.setCenter(b.xmin, b.getCenterY());
			}
		else
			{
			MiDistance tx = 0;
			makePositionVisible(pos);
/*
			if (textObj.getFirstDisplayedColumn() != 0)
				{
				if (pos == textObj.getFirstDisplayedColumn())
					{
					tx = textObj.getItemBounds(
						textObj.getFirstDisplayedColumn() - 1).getXmax()
						- textObj.getItemBounds(0).getXmin();
					}
				else
					{
					tx = textObj.getItemBounds(
						textObj.getFirstDisplayedColumn()).getXmin()
						- textObj.getItemBounds(0).getXmin();
					}
				}
*/
			// Check to see if we are to put cursor at right of last character
			b = getBoundsOfCharAtCursorPosition(pos);
//MiDebug.println("bounds of char at " + pos + " = " + b);
			if (isCursorPositionAtEndOfRow(pos))
                                {
                                cursor.setCenter(b.xmax - tx, b.getCenterY());
                                }
			else
				{
//MiDebug.println("CURSOR set xmin = " + (b.xmin - tx));
//MiDebug.println("textObj.getBounds()= " + textObj.getBounds());

				cursor.setCenter(b.xmin - tx, b.getCenterY());
				}
			}
		cursorPosition = pos;

if ((cursorPosition < 0) || (cursorPosition > textObj.getText().length()))
{
throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
	+ ": attempt to set cursor position to invalid location: " + cursorPosition
	+ "\nMaximum allowable position is: " + textObj.getText().length());
}

		if (animator.isScheduled())
			animator.resetStepTimer();
		animator.setEnabled(true);
		}
	public		int		getCursorPosition()
		{
		return(cursorPosition);
		}
	public		boolean		hasMultipleRows()
		{
		return(textObj.getNumberOfRows() > 1);
		}
	public		void		makeMaximumAmountVisible()
		{
		// Check to see if whole string is visible ...
		if ((textObj.getNumDisplayedColumns() < 0)
			|| (textObj.getFirstDisplayedColumn() == 0))
			{
			return;
			}

		if ((textObj.getText() != null) 
			&& (textObj.getText().length() <= textObj.getNumDisplayedColumns()))
			{
			textObj.setFirstDisplayedColumn(0);
			}
		}
	public		void		makePositionVisible(int pos)
		{
//MiDebug.println("makePositionVisible: " + pos);
		// Check to see if whole string is visible ...
		if (textObj.getNumDisplayedColumns() < 0)
			{
			if (pos < textObj.getFirstDisplayedColumn())
				{
				textObj.setFirstDisplayedColumn(pos);
				}
			else if (textObj.getContainer(0) instanceof MiVisibleContainer)
				{
				MiBounds innerBoundsOfContainer = textObj.getContainer(0).getInnerBounds();
//MiDebug.println("cusror pos: " + pos);
//MiDebug.println("makePositionVisible:innerBoundsOfContainer " + innerBoundsOfContainer);
//MiDebug.println("textObj.getContainer(0) " + textObj.getContainer(0));
//MiDebug.println("text " + textObj);
//MiDebug.dump(textObj.getContainer(0));
//dumpCharPositions();
				MiBounds b = getBoundsOfCharAtCursorPosition(pos);
				//b.subtractMargins(0.01); // deal with floating pt errors
				int firstDisplayColumn = textObj.getFirstDisplayedColumn();
				//b.translate(-(getBoundsOfCharAtCursorPosition(firstDisplayColumn).getXmin() 
					//- getBoundsOfCharAtCursorPosition(0).getXmin()), 0);
				int maxTrys = 100;
				while (maxTrys-- > 0)
					{
//MiDebug.println("makePositionVisible:getBoundsOfCharAtCursorPosition " + b);
//MiDebug.println("makePositionVisible:innerBoundsOfContainer " + innerBoundsOfContainer);
					if (innerBoundsOfContainer.contains(b))
						{
						break;
						}
					if (b.xmin < innerBoundsOfContainer.xmin)
						{
						// Cursor is to left... handle bizarre case where letter hangs off the left side of the field
						if (firstDisplayColumn == 0)
							{
							break;
							}
						b.translate(getBoundsOfCharAtCursorPosition(--firstDisplayColumn).getWidth(), 0);
						}
					else if (firstDisplayColumn >= textObj.getValue().length() - 2)
						{
						break;
						}
					else
						{
						b.translate(-getBoundsOfCharAtCursorPosition(firstDisplayColumn++).getWidth(), 0);
						}
					}
if (maxTrys == 0)
MiDebug.println("MiTextFieldEditor: maxTrys == 0");
//MiDebug.println("setFirstDisplayedColumn=" + firstDisplayColumn);
				if (firstDisplayColumn != textObj.getFirstDisplayedColumn())
					{
					textObj.setFirstDisplayedColumn(firstDisplayColumn);
					//setCursorPosition(pos);
					}
				}
			return;
			}
		if (pos < textObj.getFirstDisplayedColumn())
			{
			textObj.setFirstDisplayedColumn(pos);
			}
		else if ((textObj.getNumDisplayedColumns() == 1)
			&& (pos > textObj.getFirstDisplayedColumn()
				+ textObj.getNumDisplayedColumns() - numberOfBlankCharsAtRightEndOfText))
			{
			textObj.setFirstDisplayedColumn(pos - textObj.getNumDisplayedColumns() + 1);
			}
		}
	public		void		moveCursorPositionToRowStart()
		{
		setCursorPosition(getPostionAtStartOfRow(getRowOfPosition(cursorPosition)));
		}
	public		void		moveCursorPositionLeft()
		{
		if (cursorPosition > 0)
			{
			setCursorPosition(cursorPosition - 1);
			}
		}
	public		void		moveCursorPositionRight()
		{
		if (cursorPosition < textObj.getText().length())
			{
			setCursorPosition(cursorPosition + 1);
			}
		}
	public		void		moveCursorPositionUp()
		{
		int currentRow = getRowOfPosition(cursorPosition);
		if (currentRow > 0)
			{
			int currentColumn  = getColumnOfPosition(cursorPosition);
			if (currentColumn > textObj.getRowLength(currentRow - 1))
				currentColumn = textObj.getRowLength(currentRow - 1);
			setCursorPosition(getPosition(currentRow - 1, currentColumn));
			}
		}
	public		void		moveCursorPositionDown()
		{
		int currentRow = getRowOfPosition(cursorPosition);
		if (currentRow < textObj.getNumberOfRows() - 1)
			{
			int currentColumn  = getColumnOfPosition(cursorPosition);
			if (currentColumn > textObj.getRowLength(currentRow + 1))
				currentColumn = textObj.getRowLength(currentRow + 1);
			setCursorPosition(getPosition(currentRow + 1, currentColumn));
			}
		}
	protected	boolean		isCursorPositionAtEndOfRow(int pos)
		{
		int row = getRowOfPosition(pos);
		if ((textObj.getRowLength(row) > 0) && (pos == getPostionAtEndOfRow(row)))
			return(true);
		return(false);
		}
	protected	MiBounds	getBoundsOfCharAtCursorPosition(int pos)
		{
		return(textObj.getItemBounds(convertCursorPositionToCharPosition(pos)));
		}
	protected	int		convertCursorPositionToCharPosition(int pos)
		{
		return(pos);
		}
	protected	int		getPostionAtStartOfRow(int row)
		{
		int pos = 0;
		for (int i = 0; i < row; ++i)
			{
			pos += textObj.getRowLength(i) + (textObj.isRowLineWrapped(i) ? 0 : 1);
			}
		return(pos);
		}
	protected	int		getPostionAtEndOfRow(int row)
		{
		// The text is empty
		if (row >= textObj.getNumberOfRows())
			return(0);

		int pos = 0;
		for (int i = 0; i <= row; ++i)
			{
			pos += textObj.getRowLength(i) + (textObj.isRowLineWrapped(i) ? 0 : 1);
			}
		// Should not -1 here if lineWrapped(row) but no way to represent 
		// last position on an auto-wrapped line
		return(pos == 0 ? pos : pos - 1);
		}
	protected	int		getRowOfPosition(int pos)
		{
		int numberOfRows = textObj.getNumberOfRows();
		for (int i = 0; i < numberOfRows; ++i)
			{
			pos -= textObj.getRowLength(i) + (textObj.isRowLineWrapped(i) ? 0 : 1);
			if (pos < 0)
				return(i);
			}
		return(0);
		}
	protected	int		getColumnOfPosition(int pos)
		{
		int numberOfRows = textObj.getNumberOfRows();
		for (int i = 0; i < numberOfRows; ++i)
			{
			int rowLength = textObj.getRowLength(i) + (textObj.isRowLineWrapped(i) ? 0 : 1);
			if (pos < rowLength)
				return(pos);
			pos -= rowLength;
			}
		return(0);
		}
	protected	int	getPosition(int row, int column)
		{
		int numberOfRows = textObj.getNumberOfRows();
		int pos = 0;
		for (int i = 0; i < row; ++i)
			{
			pos += textObj.getRowLength(i) + (textObj.isRowLineWrapped(i) ? 0 : 1);
			}
		return(pos + column);
		}
	public		void		moveCursorPositionToRowEnd()
		{
		setCursorPosition(getPostionAtEndOfRow(getRowOfPosition(cursorPosition)));
		}

	private		int		convertLocationToCursorPosition(MiPoint pt)
		{
		if (textObj.getFirstDisplayedColumn() != 0)
			{
			pt.x += textObj.getItemBounds(textObj.getFirstDisplayedColumn()).getXmin()
					- textObj.getItemBounds(0).getXmin();
			}

		return(textObj.convertLocationToPosition(pt));
		}
	
	public		void		deleteSelectedItems()
		{
		if (hasSelectedItems())
			{
			textObj.setText(deleteCharsAtPositions(textObj.getText(),
				textObj.getSelectionStart(), textObj.getSelectionEnd()));

			if (textObj.getSelectionStart() < cursorPosition)
				{
				setCursorPosition(cursorPosition 
					- Math.min(cursorPosition - textObj.getSelectionStart(),
					textObj.getSelectionEnd() - textObj.getSelectionStart()));
				}
			else
				{
				setCursorPosition(cursorPosition);
				}
			deSelectAll();
			}
		}

	public		void		deleteItemAtCursor()
		{
		deleteItem(cursorPosition);
		}
	public		void		deleteItemToLeftOfCursor()
		{
		if (cursorPosition > 0)
			{
			deleteItem(cursorPosition - 1);
			}
		}
	public		void		deleteItem(int pos)
		{
		if (hasSelectedItems())
			{
			pos = textObj.getSelectionStart();
			deleteSelectedItems();
			makeMaximumAmountVisible();
			setCursorPosition(pos);
			}
		if (pos < textObj.getText().length())
			{
			String text = textObj.getText();
			textObj.setText(deleteCharAtPosition(text, pos));
			makeMaximumAmountVisible();
			setCursorPosition(pos);
			}
		}
	public		void		insertEndOfParagraphAtCursor()
		{
		insertItemAtCursor('\n');
		}
	public		void		insertItemAtCursor(char ch)
		{
		if ((!hasSelectedItems())
			&& (textObj.getMaxNumCharacters() != -1)
			&& (textObj.getText().length() >= textObj.getMaxNumCharacters()))
			{
			return;
			}
		insertItem(ch, cursorPosition);
		}
	public		void		replaceItemAtCursor(char ch)
		{
		replaceItem(ch, cursorPosition);
		}
	public		void		insertItem(char ch, int pos)
		{
		if (hasSelectedItems())
			{
			pos = textObj.getSelectionStart();
			deleteSelectedItems();
			setCursorPosition(pos);
			}
		String text = textObj.getText();
		if ((textObj.getMaxNumCharacters() != -1)
			&& (text.length() >= textObj.getMaxNumCharacters()))
			{
			return;
			}
		textObj.setText(insertCharAtPosition(text, ch, pos));
		}
	public		void		replaceItem(char ch, int pos)
		{
		if (hasSelectedItems())
			{
			pos = textObj.getSelectionStart();
			deleteSelectedItems();
			setCursorPosition(pos);
			}
		textObj.setText(replaceCharAtPosition(textObj.getText(), ch, pos));
		}
	protected	void		reFormat()
		{
		}
	protected	boolean		isWhiteSpace(char ch)
		{
		if ((ch == ' ') || (ch == '\t'))
			return(true);
		return(false);
		}


	protected	String		insertCharAtPosition(String text, char ch, int pos)
		{
		return(text.substring(0, pos) + ch + text.substring(pos));
		}
	protected	String		replaceCharAtPosition(String text, char ch, int pos)
		{
		return(text.substring(0, pos) + ch + text.substring(pos + 1));
		}
	protected	String		deleteCharAtPosition(String text, int pos)
		{
		return(text.substring(0, pos) + text.substring(pos + 1));
		}
	protected	String		deleteCharsAtPositions(String text, int start, int end)
		{
		String result = "";
		if (start > 0)
			result = text.substring(0, textObj.getSelectionStart());
		if (end <= text.length() - 1)
			result += text.substring(end);
		return(result);
		}
	protected	void		dumpCharPositions()
		{
		int yyy = textObj.getFirstDisplayedColumn();
		for (int i = 0; i < textObj.getText().length() - 2; ++i)
			{
			textObj.setFirstDisplayedColumn(i);
			for (int j = i; j < textObj.getText().length(); ++j)
				{
				MiDebug.println("first displayed column = " + i + ", char["+ j + "] bounds = " 
					+ getBoundsOfCharAtCursorPosition(j));
				}
			}
		textObj.setFirstDisplayedColumn(yyy);
		}
	}

