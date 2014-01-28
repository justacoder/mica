
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

import java.util.HashMap;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTranslatePartsCommand extends MiCommandHandler implements MiiTransaction, MiiDisplayNames, MiiCommandNames
	{
	private		MiVector	translation			= new MiVector();
	private		MiParts		translatedObjects;


	public				MiTranslatePartsCommand()
		{
		}

	public				MiTranslatePartsCommand(
						MiEditor editor, MiPart translatedObject, MiVector translation)
		{
		this(editor, new MiParts(translatedObject), translation);
		}

	public				MiTranslatePartsCommand(
						MiEditor editor, MiParts translatedObjects, MiVector translation)
		{
		setTargetOfCommand(editor);
		this.translation.copy(translation);
		this.translatedObjects = translatedObjects;
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		setTranslation(MiVector v)
		{
		translation.copy(v);
		}
	public		MiVector	getTranslation()
		{
		return(new MiVector(translation));
		}
	public		void		processCommand(String cmd)
		{
		processCommand(getEditor(), translatedObjects, translation);
		}

	public		void		processCommand(MiEditor editor, MiParts translatedObjects, MiVector translation)
		{
		doit(editor, translatedObjects, translation);
		MiSystem.getTransactionManager().appendTransaction(
			new MiTranslatePartsCommand(editor, translatedObjects, translation));
		}
	public		void		doit(MiEditor editor, MiParts parts, MiVector translation)
		{
		HashMap hashMap = new HashMap();
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if ((obj instanceof MiConnection) && (obj.getLayout() != null))
				{
				hashMap.put(obj,new Boolean(obj.getLayout().isEnabled()));
				obj.getLayout().setEnabled(false);
				}
			}
			
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if (obj instanceof MiConnection)
				{
				int startIndex = 1;
				if (((MiConnection )obj).getSource() == null)
					{
					startIndex = 0;
					}
				int endIndex = obj.getNumberOfPoints() - 1;
				if (((MiConnection )obj).getDestination() == null)
					{
					endIndex += 1;
					}
				for (int j = startIndex; j < endIndex; ++j)
					{
					obj.translatePoint(j, translation.x, translation.y);
					}
				}
			else
				{
				obj.translate(translation.x, translation.y);
				}
			}
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if ((obj instanceof MiConnection) && (obj.getLayout() != null))
				{
				obj.getLayout().setEnabled(((Boolean )hashMap.get(obj)).booleanValue());
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
		return(Mi_TRANSLATE_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_TRANSLATE_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), translatedObjects, translation);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		MiVector translation = new MiVector(this.translation);
		translation.x = -translation.x;
		translation.y = -translation.y;
		doit(getEditor(), translatedObjects, translation);
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
		processCommand(null);
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
		return(true);
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		return(new MiParts(translatedObjects));
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

