
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
import com.swfm.mica.util.FastVector; 

//FIX: need  to store the index of each part in it's orig container so can undo more precisely

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGroupPartsCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames, MiiActionTypes
	{
	private		FastVector	partsInEachGroup	= new FastVector();
	private		MiParts 	groups			= new MiParts();
	private		boolean 	toGroup;


	public				MiGroupPartsCommand()
		{
		}
	public				MiGroupPartsCommand(MiEditor editor,
						FastVector partsInEachGroup, MiParts groups, boolean toGroup)
		{
		setTargetOfCommand(editor);
		this.partsInEachGroup.append(partsInEachGroup);
		this.groups.append(groups);
		this.toGroup = toGroup;
		}
	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
					// Groupable parts are returned, (non-groupable parts are removed)
	public		boolean		itemsAreGroupable(MiParts parts)
		{
		MiPart commonContainer = null;
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart part = parts.elementAt(i);

			if (commonContainer == null)
				commonContainer = part.getContainer(0);
			else if (commonContainer != part.getContainer(0))
				return(false);

/*
			if (!part.isGroupable())
				{
				parts.removeElementAt(i);
				--i;
				}
			else 
*/
			if ((part instanceof MiConnection)
				&& (!(parts.contains(((MiConnection )part).getSource()))
				|| (!parts.contains(((MiConnection )part).getDestination()))))
				{
				parts.removeElementAt(i);
				--i;
				}
			}
		return(parts.size() > 1);
		}
	public		void		processCommand(String arg)
		{
		MiEditor editor = (MiEditor )getTargetOfCommand();
		if (arg.equalsIgnoreCase(Mi_GROUP_COMMAND_NAME))
			{
			FastVector partsInEachGroup = new FastVector();
			MiParts selectedParts = editor.getSelectedParts(new MiParts());
			for (int i = 0; i < selectedParts.size(); ++i)
				{
/*
				if (!selectedParts.elementAt(i).isGroupable())
					{
					selectedParts.removeElementAt(i);
					--i;
					}
*/
				}
			if (selectedParts.size() > 1)
				{
				partsInEachGroup.addElement(selectedParts);
				processCommand(editor, partsInEachGroup, 
					new MiParts(makeGroup(selectedParts.elementAt(0))), true);
				}
			}
		else if (arg.equalsIgnoreCase(Mi_UNGROUP_COMMAND_NAME))
			{
			MiParts selectedParts = editor.getSelectedParts(new MiParts());
			for (int i = 0; i < selectedParts.size(); ++i)
				{
				if (!selectedParts.elementAt(i).isUngroupable())
					{
					selectedParts.removeElementAt(i);
					--i;
					}
				}
			processCommand(editor, new FastVector(), selectedParts, false);
			}
		}

	public		void		processCommand(MiEditor editor, 
						FastVector partsInEachGroup, MiParts groups, boolean toGroup)
		{
		doit(editor, partsInEachGroup, groups, toGroup);
		MiSystem.getTransactionManager().appendTransaction(
			new MiGroupPartsCommand(editor, partsInEachGroup, groups, toGroup));
		}
	protected	void		doit(	MiEditor editor, 
						FastVector partsInEachGroup, 
						MiParts groups, boolean toGroup)
		{
		if (toGroup)
			{
			MiParts newGroups = new MiParts();
			for (int i = 0 ; i < partsInEachGroup.size(); ++i)
				{
				MiPart group = doGroup(editor, 
					(MiParts )partsInEachGroup.elementAt(i), groups.elementAt(i));
				if (group != null)
					newGroups.addElement(group);
				}
			groups.removeAllElements();
			groups.append(newGroups);
			}
		else
			{
			partsInEachGroup.removeAllElements();
			for (int i = 0 ; i < groups.size(); ++i)
				{
				MiParts parts = new MiParts();
				doUnGroup(editor, parts, groups.elementAt(i));
				partsInEachGroup.addElement(parts);
				}
			}
		MiSelectionManager.notifyAboutNumberOfShapesSelected(editor);
		}
	protected	MiPart		makeGroup(MiPart sampleElement)
		{
		return(new MiContainer());
		}
	// Group can be null... in which case a MiContainer is created to be the group
	protected static MiPart		doGroup(MiEditor editor, MiParts parts, MiPart group)
		{
		MiPart obj;
		MiPart commonContainer = null;
		for (int i = 0; i < parts.size(); ++i)
			{
			obj = parts.elementAt(i);
			if (commonContainer == null)
				commonContainer = obj.getContainer(0);
			else if (commonContainer != obj.getContainer(0))
				{
				MiDebug.println("MiGroupPartsCommand" 
					+ ": Unable to group shapes because they are not in the same container");
				return(null);
				}
			}
		for (int i = 0; i < parts.size(); ++i)
			{
			obj = parts.elementAt(i);
			// Only group connections that connect 2 other selected items
			if ((!(obj instanceof MiConnection))
				|| ((parts.contains(((MiConnection )obj).getSource()))
				&& (parts.contains(((MiConnection )obj).getDestination()))))
				{
				if (obj.isSelected())
					editor.deSelect(obj);
				commonContainer.removePart(obj);
				group.appendPart(obj);
				}
			}
		if (group != null)
			{
			commonContainer.appendItem(group);
			editor.selectAdditional(group);
			group.dispatchAction(Mi_GROUP_ACTION);
			}
		return(group);
		} 
	protected static void		doUnGroup(MiEditor editor, MiParts parts, MiPart group)
		{
		parts.removeAllElements();
		if (group.getNumberOfParts() != 0)
			{
			MiPart layer = group.getContainer(0);
			while (group.getNumberOfParts() > 0)
				{
				MiPart part = group.getPart(0);
				group.removePart(0);
				layer.appendPart(part);
				editor.selectAdditional(part);
				parts.addElement(part);
				}
			//editor.deSelect(group);
			layer.removePart(group);
			group.dispatchAction(Mi_UNGROUP_ACTION);
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
		if (toGroup)
			return(Mi_GROUP_DISPLAY_NAME);
		else
			return(Mi_UNGROUP_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		if (toGroup)
			return(Mi_GROUP_COMMAND_NAME);
		else
			return(Mi_UNGROUP_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), partsInEachGroup, groups, toGroup);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), partsInEachGroup, groups, !toGroup);
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
		return(groups);
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

	public static 	MiPart		groupSelectedObjects(MiEditor editor)
		{
		MiParts selectedParts = editor.getSelectedParts(new MiParts());
		for (int i = 0; i < selectedParts.size(); ++i)
			{
/*
			if (!selectedParts.elementAt(i).isGroupable())
				{
				selectedParts.removeElementAt(i);
				--i;
				}
*/
			}
		if (selectedParts.size() > 1)
			{
			return(doGroup(editor, selectedParts, new MiContainer()));
			}
		return(null);
		}
	public static 	void		unGroupSelectedObjects(MiEditor editor)
		{
		MiParts selectedObjects = new MiParts();
		editor.getSelectedParts(selectedObjects);
		for (int i = 0; i < selectedObjects.size(); ++i)
			{
			MiPart group = selectedObjects.elementAt(i);
			if (group.getNumberOfParts() != 0)
				{
				doUnGroup(editor, new MiParts(), group);
				}
			}
		}
	}


