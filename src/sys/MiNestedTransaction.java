
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
import com.swfm.mica.util.TypedVector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiNestedTransaction extends TypedVector implements MiiTransaction
	{
	private		boolean 	prohibitingNestedTransactions;
	private		String 		name;
	private static	int 		lastId;
	private		int 		id;

	public				MiNestedTransaction(String name)
		{
		this.name = name;
if (name == null)
MiDebug.printStackTrace("Transaction has no name! " + this);
		this.id = ++lastId;
		}

	public				MiNestedTransaction(String name,
						MiiTransaction t1)
		{
if (name == null)
MiDebug.printStackTrace("Transaction has no name! " + this);
		this.name = name;
		this.id = ++lastId;
		addElement(t1);
		}

	public				MiNestedTransaction(String name,
						MiiTransaction t1,
						MiiTransaction t2)
		{
if (name == null)
MiDebug.printStackTrace("Transaction has no name! " + this);
		this.name = name;
		this.id = ++lastId;
		addElement(t1);
		addElement(t2);
		}
	public				MiNestedTransaction(String name,
						MiiTransaction t1,
						MiiTransaction t2,
						MiiTransaction t3)
		{
if (name == null)
MiDebug.printStackTrace("Transaction has no name! " + this);
		this.name = name;
		this.id = ++lastId;
		addElement(t1);
		addElement(t2);
		addElement(t3);
		}
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
		return(null);
		}
	public		void		redo()
		{
		for (int i = 0; i < size(); ++i)
			elementAt(i).redo();
		}
	public		void		undo()
		{
//MiDebug.printStackTrace(this + "Undoing elements");
		for (int i = size() - 1; i >= 0; --i)
			{
//MiDebug.println("Undoing element: " + i + " element =  " + elementAt(i));
			elementAt(i).undo();
			}
		}
	public		void		repeat()
		{
		for (int i = 0; i < size(); ++i)
			elementAt(i).repeat();
		}
	public		boolean		isUndoable()
		{
		for (int i = 0; i < size(); ++i)
			{
			if (!elementAt(i).isUndoable())
				return(false);
			}
		return(true);
		}
	public		boolean		isRepeatable()
		{
		for (int i = 0; i < size(); ++i)
			{
			if (!elementAt(i).isRepeatable())
				return(false);
			}
		return(true);
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		MiParts parts = new MiParts();
		for (int i = 0; i < size(); ++i)
			{
			parts.append(elementAt(i).getTargets());
			}
		return(parts);
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		MiParts parts = new MiParts();
		for (int i = 0; i < size(); ++i)
			{
			parts.append(elementAt(i).getSources());
			}
		return(parts);
		}
	public		MiiTransaction	elementAt(int index)
		{
		return((MiiTransaction )vector.elementAt(index));
		}
	public		MiiTransaction	lastElement()
		{
		return((MiiTransaction )vector.lastElement());
		}
	public		void		setProhibitingNestedTransactions(boolean flag)
		{
		prohibitingNestedTransactions = flag;
		}
	public		boolean		isProhibitingNestedTransactions()
		{
		return(prohibitingNestedTransactions);
		}
	public		void		addElement(MiiTransaction obj)
		{
		if (!prohibitingNestedTransactions)
			{
			if (vector.contains(obj))
				{
				throw new IllegalArgumentException("Adding transaction: " + obj  + " TWICE to nested transaction: " +this);
				}
			vector.addElement(obj);
			}
		}
	public		void		insertElementAt(MiiTransaction obj, int index)
		{
		if (!prohibitingNestedTransactions)
			{
			if (vector.contains(obj))
				{
				throw new IllegalArgumentException("Adding transaction: " + obj  + " TWICE to nested transaction: " +this);
				}
			vector.insertElementAt(obj, index);
			}
		}
	public		boolean		removeElement(MiiTransaction obj)
		{
		return(vector.removeElement(obj));
		}
	public		int		indexOf(MiiTransaction obj)
		{
		return(vector.indexOf(obj));
		}
	public		boolean		contains(MiiTransaction obj)
		{
		return(vector.contains(obj));
		}
	public		MiiTransaction[] toArray()
		{
		return((MiiTransaction[] )vector.toArray());
		}
	public		String		toString()
		{
		return(getName() + "[" + id + "]" + ":\n" + super.toString());
		}
	}

