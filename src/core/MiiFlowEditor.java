
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
public interface MiiFlowEditor
	{
	void		makeCursorVisible(boolean flag);

	void		select(boolean flag);
	void		selectAll();
	void		deSelectAll();
	void		selectWordAtCursor();
	void		selectParagraphAtCursor();
	void		selectItems(MiPoint startPos, MiPoint endPos);
	boolean		hasSelectedItems();

	boolean		supportsMultipleLines();
	boolean		isEmpty();
	void		broadcastEnterKeyAction();

	void		setCursorPosition(MiPoint pos);
	void		setCursorPosition(int pos);
	int		getCursorPosition();
	void		moveCursorPositionToRowStart();
	void		moveCursorPositionLeft();
	void		moveCursorPositionRight();
	void		moveCursorPositionUp();
	void		moveCursorPositionDown();
	void		moveCursorPositionToRowEnd();

	void		deleteSelectedItems();
	void		deleteItemAtCursor();
	void		deleteItemToLeftOfCursor();
	void		deleteItem(int pos);

	void		insertEndOfParagraphAtCursor();
	void		insertItemAtCursor(char ch);
	void		replaceItemAtCursor(char ch);
	void		insertItem(char ch, int pos);
	void		replaceItem(char ch, int pos);
	}

