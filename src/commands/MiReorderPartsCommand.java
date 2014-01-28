
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
public class MiReorderPartsCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames
	{
	private static final int	BRING_TO_FRONT		= 1;
	private static final int	SEND_TO_BACK		= 2;
	private static final int	BRING_FORWARD		= 3;
	private static final int	SEND_BACKWARD		= 4;
	private 	int		howToReorder		= BRING_TO_FRONT;
	private		MiParts 	parts			= new MiParts();



	public				MiReorderPartsCommand()
		{
		}
	public				MiReorderPartsCommand(MiEditor editor, MiParts parts, int howToReorder)
		{
		setTargetOfCommand(editor);
		this.parts.append(parts);
		this.howToReorder = howToReorder;
		}
	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		processCommand(String arg)
		{
		MiEditor editor = getEditor();
		MiParts selectedParts = editor.getSelectedParts(new MiParts());
		if (selectedParts.size() > 0)
			{
			if (arg.equalsIgnoreCase(Mi_SEND_TO_BACK_COMMAND_NAME))
				processCommand(editor, selectedParts, SEND_TO_BACK);
			else if (arg.equalsIgnoreCase(Mi_BRING_TO_FRONT_COMMAND_NAME))
				processCommand(editor, selectedParts, BRING_TO_FRONT);
			else if (arg.equalsIgnoreCase(Mi_BRING_FORWARD_COMMAND_NAME))
				processCommand(editor, selectedParts, BRING_FORWARD);
			else if (arg.equalsIgnoreCase(Mi_SEND_BACKWARD_COMMAND_NAME))
				processCommand(editor, selectedParts, SEND_BACKWARD);
			}
		}
	public		void		processCommand(MiEditor editor, MiParts parts, int howToReorder)
		{
		doit(editor, parts, howToReorder);
		MiSystem.getTransactionManager().appendTransaction(
			new MiReorderPartsCommand(editor, parts, howToReorder));
		}
	protected	void		doit(MiEditor editor, MiParts parts, int howToReorder)
		{
		int index;
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart part = parts.elementAt(i);
			MiPart layer = part.getContainer(0);
			switch (howToReorder)
				{
				case BRING_TO_FRONT :
					layer.removePart(part);
					layer.appendPart(part);
					break;

				case SEND_TO_BACK :
					layer.removePart(part);
					layer.insertPart(part, 0);
					break;

				case BRING_FORWARD :
					index = layer.getIndexOfPart(part);
					if (index < layer.getNumberOfParts() - 1)
						{
						layer.removePart(part);
						layer.insertPart(part, index + 1);
						}
					break;

				case SEND_BACKWARD :
					index = layer.getIndexOfPart(part);
					if (index > 0)
						{
						layer.removePart(part);
						layer.insertPart(part, index - 1);
						}
					break;
				default:
					break;
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
		switch (howToReorder)
			{
			case BRING_TO_FRONT :
				return(Mi_BRING_TO_FRONT_DISPLAY_NAME);
			case SEND_TO_BACK :
				return(Mi_SEND_TO_BACK_DISPLAY_NAME);
			case BRING_FORWARD :
				return(Mi_BRING_FORWARD_DISPLAY_NAME);
			case SEND_BACKWARD :
				return(Mi_SEND_BACKWARD_DISPLAY_NAME);
			default:
				return(null);
			}
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		switch (howToReorder)
			{
			case BRING_TO_FRONT :
				return(Mi_BRING_TO_FRONT_COMMAND_NAME);
			case SEND_TO_BACK :
				return(Mi_SEND_TO_BACK_COMMAND_NAME);
			case BRING_FORWARD :
				return(Mi_BRING_FORWARD_COMMAND_NAME);
			case SEND_BACKWARD :
				return(Mi_SEND_BACKWARD_COMMAND_NAME);
			default:
				return(null);
			}
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), parts, howToReorder);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		switch (howToReorder)
			{
			case BRING_TO_FRONT :
				doit(getEditor(), parts, SEND_TO_BACK);
				break;
			case SEND_TO_BACK :
				doit(getEditor(), parts, BRING_TO_FRONT);
				break;
			case BRING_FORWARD :
				doit(getEditor(), parts, SEND_BACKWARD);
				break;
			case SEND_BACKWARD :
				doit(getEditor(), parts, BRING_FORWARD);
				break;
			default:
				break;
			}
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
		return(parts);
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

