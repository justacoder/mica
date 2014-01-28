
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
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiRotatePartsCommand extends MiCommandHandler implements MiiTransaction, MiiDisplayNames, MiiCommandNames, MiiNames
	{
	private		double		radians;
	private		MiParts		rotatedObjects;
	private		ArrayList	snapTranslations;


	public				MiRotatePartsCommand()
		{
		}

	public				MiRotatePartsCommand(MiEditor editor, MiPart rotatedObject, double radians)
		{
		this(editor, new MiParts(rotatedObject), radians);
		}

	public				MiRotatePartsCommand(MiEditor editor, MiParts rotatedObjects, double radians)
		{
		this(editor, rotatedObjects, radians, null);
		}
	public				MiRotatePartsCommand(MiEditor editor, MiParts rotatedObjects, double radians, ArrayList snapTranslations)
		{
		setTargetOfCommand(editor);
		this.radians = radians;
		this.rotatedObjects = rotatedObjects;
		this.snapTranslations = snapTranslations;
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		double		getRadians()
		{
		return(radians);
		}
	public		void		processCommand(String cmd)
		{
		processCommand(getEditor(), rotatedObjects, radians);
		}

	public		void		processCommand(MiEditor editor, MiParts rotatedObjects, double radians)
		{
		doit(editor, rotatedObjects, radians, true);
		MiSystem.getTransactionManager().appendTransaction(
			new MiRotatePartsCommand(editor, rotatedObjects, radians, snapTranslations));
		}
	protected	void		doit(MiEditor editor, MiParts rotatedObjects, double radians, boolean reallyDoIt)
		{
		MiPoint rotationPt = new MiPoint();
		for (int i = 0; i < rotatedObjects.size(); ++i)
			{
			MiPart part = rotatedObjects.elementAt(i);
			String rotationPoint = (String )part.getResource(Mi_ROTATION_POINT_PART_NAME_AND_POINT_NUMBER);
			if (rotationPoint != null)
				{
				int dotIndex = rotationPoint.lastIndexOf('.');
				String rotationPartName = rotationPoint.substring(0, dotIndex);
				int rotationPointNum = Integer.parseInt(rotationPoint.substring(dotIndex + 1));
				MiPart rotationPart = part.isContainerOf(rotationPartName);
				rotationPart.getPoint(rotationPointNum, rotationPt);
				part.rotate(rotationPt, radians);
				}
			else
				{
				part.rotate(radians);
				}
			}
		if ((reallyDoIt) && (rotatedObjects.size() > 0))
			{
			snapTranslations = new ArrayList();
			
			for (int i = 0; i < rotatedObjects.size(); ++i)
				{
				MiVector snapTranslation = new MiVector();
				editor.snapMovingPart(rotatedObjects.elementAt(i), snapTranslation);
				rotatedObjects.elementAt(i).translate(snapTranslation);
				snapTranslations.add(snapTranslation);
				}
			}
		else if ((!reallyDoIt) && (snapTranslations != null))
			{
			for (int i = 0; i < rotatedObjects.size(); ++i)
				{
				MiVector snapTranslation = (MiVector )snapTranslations.get(i);
				rotatedObjects.elementAt(i).translate(-snapTranslation.x, -snapTranslation.y);
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
		return(Mi_ROTATE_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_ROTATE_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), rotatedObjects, radians, true);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), rotatedObjects, -radians, false);
		} 
					/**------------------------------------------------------
					 * Repeats this transaction. This re-applies the changes 
					 * encapsulated by this transaction. For example, a 
					 * rotateon of a shape can be repeated in order to move 
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
		return(new MiParts(rotatedObjects));
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

