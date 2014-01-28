
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

import java.util.ArrayList;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiFlipPartsCommand extends MiCommandHandler implements MiiTransaction, MiiDisplayNames, MiiCommandNames, MiiNames
	{
	private		double		radians;
	private		MiParts		flippedObjects;
	private		ArrayList	snapTranslations;



	public				MiFlipPartsCommand()
		{
		}

	public				MiFlipPartsCommand(MiEditor editor, MiPart flippedObject, double radians)
		{
		setTargetOfCommand(editor);
		this.radians = radians;
		this.flippedObjects = new MiParts(flippedObject);
		}

	public				MiFlipPartsCommand(MiEditor editor, MiParts flippedObjects, double radians)
		{
		setTargetOfCommand(editor);
		this.radians = radians;
		this.flippedObjects = flippedObjects;
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		processCommand(String cmd)
		{
		processCommand(getEditor(), flippedObjects, radians);
		}

	public		void		processCommand(MiEditor editor, MiParts flippedObjects, double radians)
		{
		doit(editor, flippedObjects, radians, true);
		MiSystem.getTransactionManager().appendTransaction(
			new MiFlipPartsCommand(editor, flippedObjects, radians));
		}
	protected	void		doit(MiEditor editor, MiParts flippedObjects, double radians, boolean reallyDoIt)
		{
		MiPoint rotationPt = new MiPoint();
		for (int i = 0; i < flippedObjects.size(); ++i)
			{
			MiPart part = flippedObjects.elementAt(i);
			String rotationPoint = (String )part.getResource(Mi_ROTATION_POINT_PART_NAME_AND_POINT_NUMBER);
			if (rotationPoint != null)
				{
				int dotIndex = rotationPoint.lastIndexOf('.');
				String rotationPartName = rotationPoint.substring(0, dotIndex);
				int rotationPointNum = Integer.parseInt(rotationPoint.substring(dotIndex + 1));
				MiPart rotationPart = part.isContainerOf(rotationPartName);
				rotationPart.getPoint(rotationPointNum, rotationPt);
				part.flip(rotationPt, radians);
				}
			else
				{
				part.flip(radians);
				}
			}
		if ((reallyDoIt) && (flippedObjects.size() > 0))
			{
			snapTranslations = new ArrayList();
			
			for (int i = 0; i < flippedObjects.size(); ++i)
				{
				MiVector snapTranslation = new MiVector();
				editor.snapMovingPart(flippedObjects.elementAt(i), snapTranslation);
				flippedObjects.elementAt(i).translate(snapTranslation);
				snapTranslations.add(snapTranslation);
				}
			}
		else if ((!reallyDoIt) && (snapTranslations != null))
			{
			for (int i = 0; i < flippedObjects.size(); ++i)
				{
				MiVector snapTranslation = (MiVector )snapTranslations.get(i);
				flippedObjects.elementAt(i).translate(-snapTranslation.x, -snapTranslation.y);
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
		return(Mi_FLIP_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_FLIP_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), flippedObjects, radians, true);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), flippedObjects, -radians, false);
		} 
					/**------------------------------------------------------
					 * Repeats this transaction. This re-applies the changes 
					 * encapsulated by this transaction. For example, a 
					 * flippedn of a shape can be repeated in order to move 
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
		return(new MiParts(flippedObjects));
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

