
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
public class MiTransactionCommandAdapter implements MiiCommandHandler, MiiTransaction
	{
	private		MiiCommandHandler 	handler;
	private		String		name;
	private		String		redoCommand;
	private		String		undoCommand;
	private		String		repeatCommand;



	public				MiTransactionCommandAdapter(
						MiiCommandHandler handler,
						String name,
						String redoCommand,
						String undoCommand,
						String repeatCommand)
		{
		this.handler		= handler;
		this.name		= name;
		this.redoCommand	= redoCommand;
		this.undoCommand	= undoCommand;
		this.repeatCommand	= repeatCommand;
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
		return(name);
		}
	public		void		processCommand(String cmd)
		{
		handler.processCommand(cmd);
		}
	public		void		redo()
		{
		handler.processCommand(redoCommand);
		}
	public		void		undo()
		{
		handler.processCommand(undoCommand);
		}
	public		void		repeat()
		{
		handler.processCommand(repeatCommand);
		}
	public		boolean		isUndoable()
		{
		return(undoCommand != null);
		}
	public		boolean		isRepeatable()
		{
		return(repeatCommand != null);
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		return(null);
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

