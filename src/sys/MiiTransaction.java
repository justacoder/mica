
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
public interface MiiTransaction
	{
			/**------------------------------------------------------
			 * Gets the name of this transaction. This name is often 
			 * displayed, for example, in the menubar's edit pulldown
			 * menu.
			 * @return		the name of this transaction.
			 *------------------------------------------------------*/
	String		getName();


			/**------------------------------------------------------
			 * Gets the command perfromed by this transaction. This name
			 * is often found in the MiiCommandNames file.
			 * @return		the command of this transaction.
			 * @implements		MiiTransaction#getCommand
			 *------------------------------------------------------*/
	String		getCommand();


			/**------------------------------------------------------
			 * Redoes this transaction. This is only valid after an undo.
			 * This redoes the changes encapsulated by this transaction
			 * that were undone by the undo() method.
			 *------------------------------------------------------*/
	void		redo();


			/**------------------------------------------------------
			 * Undoes this transaction. This undoes any changes that 
			 * were made by the changes encapsulated by this transaction.
			 *------------------------------------------------------*/
	void		undo();


			/**------------------------------------------------------
			 * Repeats this transaction. This re-applies the changes 
			 * encapsulated by this transaction. For example, a 
			 * translation of a shape can be repeated in order to move 
			 * it further.
			 *------------------------------------------------------*/
	void		repeat();


			/**------------------------------------------------------
			 * Gets whether this transaction is undoable.
			 * @returns		true if undoable.
			 *------------------------------------------------------*/
	boolean		isUndoable();


			/**------------------------------------------------------
			 * Gets whether this transaction is repeatable. If repeatable
			 * then calling this transaction's repeat() method is permitted.
			 * @returns		true if repeatable.
			 *------------------------------------------------------*/
	boolean		isRepeatable();


			/**------------------------------------------------------
			 * Gets the targets of this transaction.
			 * @returns		the parts affected by this transaction
			 * @implements		MiiTransaction#getTargets
			 *------------------------------------------------------*/
	MiParts		getTargets();


			/**------------------------------------------------------
			 * Gets the parts used by this transaction.
			 * @returns		the parts used by this transaction
			 * @implements		MiiTransaction#getSources
			 *------------------------------------------------------*/
	MiParts		getSources();
	}

