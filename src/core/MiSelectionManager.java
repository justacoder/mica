
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
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiSelectionManager implements MiiSelectionManager, MiiActionTypes
	{
	private		boolean			keepOneSelectedAtAllTimes= false;
	private		boolean			manyCanBeSelectedAtOneTime= false;
	private		MiiSelectionGraphics	selectionGraphics 	= new MiBoxSelectionGraphics();
	private		MiEditor		editor;



					/**------------------------------------------------------
					 * Contructs a new MiSelectionManager.
					 *------------------------------------------------------*/
	public				MiSelectionManager(MiEditor editor)
		{
		this.editor = editor;
		}
					/**------------------------------------------------------
					 * Sets whether to assure that AT LEAST one item is always
					 * selected by preventing the deselection of an item if it
					 * is the only selected item. It does NOT select an item
					 * if none are currently selected.
					 * @param flag		true if one item is always selected
					 *------------------------------------------------------*/
	public		void		setKeepOneSelectedAtAllTimes(boolean flag)
		{
		keepOneSelectedAtAllTimes = flag;
		}
					/**------------------------------------------------------
					 * Gets whether to assure that AT LEAST one item is selected.
					 *------------------------------------------------------*/
	public		boolean		getKeepOneSelectedAtAllTimes()
		{
		return(keepOneSelectedAtAllTimes);
		}
					/**------------------------------------------------------
					 * Sets whether to assure that AT MOST one item is selected
					 * at any one time by deselecting the currently selected
					 * item if a new item is being selected. This can be 
					 * temporarily avoided by using the selectAdditionalObject
					 * method. 
					 * @param flag		true if at most one item is to be
					 *			selected
					 *------------------------------------------------------*/
	public		void		setManyCanBeSelectedAtOneTime(boolean flag)
		{
		manyCanBeSelectedAtOneTime = flag;
		}
					/**------------------------------------------------------
					 * Gets whether to assure that AT MOST one item is selected.
					 *------------------------------------------------------*/
	public		boolean		getManyCanBeSelectedAtOneTime()
		{
		return(manyCanBeSelectedAtOneTime);
		}
					/**------------------------------------------------------
					 * Sets the class that will handle the change in appearance
					 * of selected items.
					 * @param graphics	the class that will decide how to
					 *			display selected items
					 * @implements		MiiSelectionManager
					 *------------------------------------------------------*/
	public		void		setSelectionGraphics(MiiSelectionGraphics graphics)
		{
		selectionGraphics = graphics;
		}
					/**------------------------------------------------------
				 	 * Gets the class that will handle the change in appearance
					 * of selected items.
				 	 * @return 		the class that will decide how to
					 *			display selected items
					 * @implements		MiiSelectionManager
				 	 *------------------------------------------------------*/
	public		MiiSelectionGraphics	getSelectionGraphics()
		{
		return(selectionGraphics);
		}
					/**------------------------------------------------------
			 	 	 * Selects all the items in the editor.
				 	 * @return		true if something new was selected
					 * @implements		MiiSelectionManager
			 	 	 *------------------------------------------------------*/
	public 		boolean		selectAll()
		{
		MiPart obj;
		boolean	selectedSomething = false;

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
			MiDebug.println("SelectAll");

		MiiIterator iterator = editor.getIterator();
		while ((obj = iterator.getNext()) != null)
			{
			if ((!obj.isSelected()) && (obj.isSelectable()))
				{
				obj.select(true);
				if (obj.getSelectionGraphics() != null)
					obj.getSelectionGraphics().select(editor, obj, true);
				else
					selectionGraphics.select(editor, obj, true);
				selectedSomething = true;
				}
			}
		if (selectedSomething)
			{
			editor.dispatchAction(Mi_ALL_ITEMS_SELECTED_ACTION);
			notifyAboutNumberOfShapesSelected(editor);
			}
		return(selectedSomething);
		}
					/**------------------------------------------------------
			 	 	 * Deselects all the items in the editor.
				 	 * @return		true if something was deselected
					 * @implements		MiiSelectionManager
			 	 	 *------------------------------------------------------*/
	public 		boolean		deSelectAll()
		{
		return(deSelectAll(true));
		}
	private		boolean		deSelectAll(boolean notifyAboutNumberOfShapesSelected)
		{
		MiPart obj;
		boolean	deSelectedSomething = false;
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
			{
			MiDebug.println("DeSelectAll");
			MiDebug.printStackTrace("DeSelectAll");
			}

		MiiIterator iterator = editor.getIterator();
		while ((obj = iterator.getNext()) != null)
			{
			if (obj.isSelected())
				{
				obj.select(false);
				if (obj.getSelectionGraphics() != null)
					obj.getSelectionGraphics().select(editor, obj, false);
				else
					selectionGraphics.select(editor, obj, false);
				deSelectedSomething = true;
				}
			}
		if (deSelectedSomething)
			{
			if (notifyAboutNumberOfShapesSelected)
				{
				editor.dispatchAction(Mi_ALL_ITEMS_DESELECTED_ACTION);
				notifyAboutNumberOfShapesSelected(editor);
				}
			}
		return(deSelectedSomething);
		}
					/**------------------------------------------------------
			 	 	 * Selects the given item.
				 	 * @param part		the item to select
					 * @implements		MiiSelectionManager
			 	 	 *------------------------------------------------------*/
	public		void		selectObject(MiPart obj)
		{
		if (!obj.isSelected())
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
				MiDebug.println("Select: " + obj);

			if (!manyCanBeSelectedAtOneTime)
				deSelectAll(false);

			if (obj.getSelectionGraphics() != null)
				obj.getSelectionGraphics().select(editor, obj, true);
			else
				selectionGraphics.select(editor, obj, true);
			obj.select(true);
			editor.dispatchAction(Mi_ITEM_SELECTED_ACTION, obj);
			}
		notifyAboutNumberOfShapesSelected(editor);
		}
					/**------------------------------------------------------
			 	 	 * Selects the given item. No other items are deselected.
				 	 * @param part		the item to select
					 * @implements		MiiSelectionManager
			 	 	 *------------------------------------------------------*/
	public		void		selectAdditionalObject(MiPart obj)
		{
		if (!obj.isSelected())
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
				MiDebug.println("Select additional part: " + obj);

			if (obj.getSelectionGraphics() != null)
				obj.getSelectionGraphics().select(editor, obj, true);
			else
				selectionGraphics.select(editor, obj, true);
			obj.select(true);
			editor.dispatchAction(Mi_ITEM_SELECTED_ACTION, obj);
			notifyAboutNumberOfShapesSelected(editor);
			}
		}
					/**------------------------------------------------------
			 	 	 * Deselects the given item.
				 	 * @param part		the item to deselect
					 * @implements		MiiSelectionManager
			 	 	 *------------------------------------------------------*/
	public		void		deSelectObject(MiPart obj)
		{
		if (obj.isSelected())
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
				{
				MiDebug.println("DesSelect part: " + obj);
				MiDebug.printStackTrace();
				}

			obj.select(false);
			if (obj.getSelectionGraphics() != null)
				obj.getSelectionGraphics().select(editor, obj, false);
			else
				selectionGraphics.select(editor, obj, false);
			editor.dispatchAction(Mi_ITEM_DESELECTED_ACTION, obj);
			notifyAboutNumberOfShapesSelected(editor);
			}
		}

					/**------------------------------------------------------
			 	 	 * Selects all the items in the given list.
				 	 * @return		true if something new was selected
				 	 * @param parts		the items to select
					 * @implements		MiiSelectionManager
			 	 	 *------------------------------------------------------*/
	public 		boolean		select(MiParts parts)
		{
		boolean	selectedSomething = false;

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
			MiDebug.println("Select(parts)");

		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.get(i);
			if ((!obj.isSelected()) && (obj.isSelectable()))
				{
				obj.select(true);
				if (obj.getSelectionGraphics() != null)
					obj.getSelectionGraphics().select(editor, obj, true);
				else
					selectionGraphics.select(editor, obj, true);
				selectedSomething = true;
				}
			}
		if (selectedSomething)
			{
			notifyAboutNumberOfShapesSelected(editor);
			}
		return(selectedSomething);
		}
					/**------------------------------------------------------
			 	 	 * Calculates and then dispatches the appropriate action
					 * indicating how many items are selected in the given 	
					 * editor.
				 	 * @param editor	the editor
			 	 	 *------------------------------------------------------*/
	public static	void		notifyAboutNumberOfShapesSelected(MiEditor editor)
		{
		int num = editor.getNumberOfPartsSelected();

		if (num == 0)
			editor.dispatchAction(Mi_NO_ITEMS_SELECTED_ACTION);
		else if (num == 1)
			editor.dispatchAction(Mi_ONE_ITEM_SELECTED_ACTION);
		else
			editor.dispatchAction(Mi_MANY_ITEMS_SELECTED_ACTION);
		}
	}

