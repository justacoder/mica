
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
import java.util.HashMap;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiScalePartsCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames
	{
	private		MiBounds	currentBounds		= new MiBounds();
	private		MiBounds	desiredBounds		= new MiBounds();
	private		MiScale		scale			= new MiScale();
	private		MiPoint		center			= new MiPoint();
	private		MiPoint		oldCenter		= new MiPoint();
	private		MiVector	translation		= new MiVector();
	private		MiParts		parts			= new MiParts();
	private		boolean		preserveSizeOfParts;


	public				MiScalePartsCommand()
		{
		}

	protected			MiScalePartsCommand(MiEditor editor,
						MiScale scale, MiPoint center, MiVector translation,
						MiParts parts, MiPoint oldCenter)
		{
		this(editor, scale, center, translation, parts, oldCenter, false);
		}
	protected			MiScalePartsCommand(MiEditor editor,
						MiScale scale, MiPoint center, MiVector translation,
						MiParts parts, MiPoint oldCenter, boolean preserveSizeOfParts)
		{
		setTargetOfCommand(editor);
		this.scale.copy(scale);
		this.center.copy(center);
		this.translation.copy(translation);
		this.oldCenter.copy(oldCenter);
		this.parts.append(parts);
		this.preserveSizeOfParts = preserveSizeOfParts;
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		setPreserveSizeOfParts(boolean flag)
		{
		preserveSizeOfParts = flag;
		}
	public		boolean		getPreserveSizeOfParts()
		{
		return(preserveSizeOfParts);
		}
	public		void		setDesiredBounds(MiBounds b)
		{
		desiredBounds.copy(b);
		}
	public		MiBounds	getDesiredBounds()
		{
		return(new MiBounds(desiredBounds));
		}
	public		void		processCommand(String arg)
		{
		if ((arg != null)
			&& (arg.equalsIgnoreCase(Mi_SCALE_TO_FIT_PAGE_COMMAND_NAME)))
			{
			MiEditor editor = getEditor();
			MiBounds tmpBounds = new MiBounds();
			currentBounds.reverse();
			parts.removeAllElements();
			if (!editor.hasLayers())
				{
				editor.reCalcBoundsOfContents(currentBounds);
				for (int i = 0; i < editor.getNumberOfItems(); ++i)
					{
					MiPart obj = editor.getItem(i);
					if (obj.isVisible())
						{
						obj.getBounds(tmpBounds);
						parts.addElement(obj);
						currentBounds.union(tmpBounds);
						}
					}
				}
			else
				{
				for (int i = 0; i < editor.getNumberOfLayers(); ++i)
					{
					MiPart layer = editor.getLayer(i);
					if (!layer.isSavable())
						continue;
					for (int j = 0; j < layer.getNumberOfItems(); ++j)
						{
						MiPart obj = layer.getItem(j);
						parts.addElement(obj);
						if (obj.isVisible())
							{
							obj.getBounds(tmpBounds);
							currentBounds.union(tmpBounds);
							}
						}
					}
				}

			if (currentBounds.equals(desiredBounds))
				return;

			translation.x = desiredBounds.getCenterX() - currentBounds.getCenterX();
			translation.y = desiredBounds.getCenterY() - currentBounds.getCenterY();
			center.x = desiredBounds.getCenterX();
			center.y = desiredBounds.getCenterY();
			scale.x = desiredBounds.getWidth()/currentBounds.getWidth();
			scale.y = desiredBounds.getHeight()/currentBounds.getHeight();
			oldCenter.x = currentBounds.getCenterX();
			oldCenter.y = currentBounds.getCenterY();

			// Maintain aspect ratio...
			if (scale.x != scale.y)
				{
				if (scale.x < scale.y)
					scale.y = scale.x;
				else
					scale.x = scale.y;
				}
			}
		else
			{
			throw new IllegalArgumentException(this + ": Unknown argument to processCommand: " + arg);
			}
		processCommand(getEditor(), scale, center, translation, parts, oldCenter, preserveSizeOfParts);
		}
	public		void		processCommand(MiEditor editor, 
						MiScale scale, MiPoint center, MiVector translation, 
						MiParts parts, MiPoint oldCenter, boolean preserveSizeOfParts)
		{
		doit(editor, scale, center, translation);
		MiSystem.getTransactionManager().appendTransaction(
			new MiScalePartsCommand(editor, scale, center, 
				translation, parts, oldCenter, preserveSizeOfParts));
		}
	protected	void		doit(MiEditor editor, 
						MiScale scale, MiPoint center, MiVector translation)
		{
		MiPoint tmpPoint = new MiPoint();
		HashMap originalLayouts = new HashMap();
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if (obj instanceof MiConnection)
				{
				MiiLayout layout = obj.getLayout();
				obj.setLayout(null);
				originalLayouts.put(obj, layout);
				}
			}
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if (obj instanceof MiConnection)
				{
				for (int j = 1; j < obj.getNumberOfPoints() - 1; ++j)
					{
					obj.getPoint(j, tmpPoint);
					tmpPoint.translate(translation.x, translation.y);
					tmpPoint.scale(center, scale);
					obj.setPoint(j, tmpPoint);
					}
				}
			else
				{
				obj.translate(translation.x, translation.y);
				if (preserveSizeOfParts)
					{
					obj.getCenter(tmpPoint);
					tmpPoint.scale(center, scale);
					obj.setCenter(tmpPoint);
					}
				else
					{
					obj.scale(center, scale);
					}
				}
			}
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if (obj instanceof MiConnection)
				{
				MiLayout layout = (MiLayout )originalLayouts.get(obj);
				obj.setLayout(layout);
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
		if (scale.isIdentity())
			return(Mi_TRANSLATE_DISPLAY_NAME);
		return(Mi_SCALE_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_SCALE_TO_FIT_PAGE_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), scale, center, translation);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		MiScale oldScale = new MiScale(1/scale.x, 1/scale.y);
		MiVector oldTranslation = new MiVector(-translation.x, -translation.y);
		doit(getEditor(), oldScale, oldCenter, oldTranslation);
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
		return(new MiParts(parts));
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

