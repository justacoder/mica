
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
public class MiTableAddItemCommand implements MiiTransaction
	{
	public static String		Mi_ADD_ITEM_TO_TABLE_DISPLAY_NAME = "Add Item";
	public static String		Mi_REMOVE_ITEM_TO_TABLE_DISPLAY_NAME = "Remove Item";
	public static String		Mi_ADD_ITEM_TO_TABLE_COMMAND_NAME = "AddItem";

	private		String		name		= Mi_ADD_ITEM_TO_TABLE_DISPLAY_NAME;
	private		MiTable		table;
	private		MiPart 		itemPart;
	private		int 		index;
	private		MiPart		icon;
	private		Object		tag;
	private		Object		parentsTag; 
	private		boolean		canHaveChildren;
	private		boolean		added;


	public				MiTableAddItemCommand(MiTable table, Object tag, boolean added)
		{
		this.table = table;
		this.added = added;
		if (table instanceof MiTreeList)
			{
			MiTreeList treeList = (MiTreeList )table;
			this.tag = tag;
			icon = treeList.getIconWithTag(tag);
			itemPart = treeList.getLabelWithTag(tag);
			parentsTag = treeList.getParentOfItem(tag);
			canHaveChildren = treeList.getItemCanHaveChildren(tag);
			}
		else if (table instanceof MiList)
			{
			MiList list = (MiList )table;
			this.tag = tag;
			index = list.getIndexOfTag(tag);
			itemPart = list.getPartItem(index);
			}
		if (!added)
			setName(Mi_REMOVE_ITEM_TO_TABLE_DISPLAY_NAME);
		}
	public		void		setName(String name)
		{
		this.name = name;
		}

	protected	void		doit(boolean reallyDoIt)
		{
		if (!added)
			reallyDoIt = !reallyDoIt;
		if (table instanceof MiTreeList)
			{
			MiTreeList treeList = (MiTreeList )table;

			if (reallyDoIt)
				{
				treeList.addItem(
						itemPart,
						icon, 
						tag, 
						parentsTag, 
						canHaveChildren);
				}
			else
				{
				treeList.removeItemWithTag(tag);
				}
			}
		else if (table instanceof MiList)
			{
			MiList list = (MiList )table;

			if (reallyDoIt)
				{
				list.insertItem(itemPart, index);
				}
			else
				{
				list.removeItem(index);
				}
			}
		}
					/**------------------------------------------------------
					 * Gets the name of this transaction. This name is often 
					 * displayed, for example, in the menubar's edit pulldown
					 * menu.
					 * @return		the name of this transaction.
					 * @implements		MiiTransaction#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(name);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_ADD_ITEM_TO_TABLE_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(true);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(false);
		} 
					/**------------------------------------------------------
					 * Repeats this transaction. This re-applies the changes 
					 * encapsulated by this transaction. For example, a 
					 * translation of a shape can be repeated in order to move 
					 * it further.
					 * @implements		MiiTransaction#repeat
					 *------------------------------------------------------*/
	public		void		repeat()
		{
		} 
					/**------------------------------------------------------
					 * Gets whether this transaction is undoable.
					 * @returns		true if undoable.
					 * @implements		MiiTransaction#isUndoable
					 *------------------------------------------------------*/
	public		boolean		isUndoable()
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Gets whether this transaction is repeatable. If repeatable
					 * then calling this transaction's repeat() method is permitted.
					 * @returns		true if repeatable.
					 * @implements		MiiTransaction#isRepeatable
					 *------------------------------------------------------*/
	public		boolean		isRepeatable()
		{
		return(false);
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		if (itemPart != null)
			return(new MiParts(itemPart));
		return(new MiParts());
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		return(null);
		}
	}




