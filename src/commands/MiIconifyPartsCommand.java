
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
// If allowed visible attachments of invisible objects then could just set container to invisible
public class MiIconifyPartsCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames, MiiActionTypes
	{
	private		MiParts 	groups			= new MiParts();
	private		MiParts 	icons			= new MiParts();
	private		boolean 	toIconify;
	private	static	MiPart		prototypeIcon;
	private		boolean		iconifyOnlySinglySelectedGroups;
	private final static String	Mi_ICONIC_REPRESENTATION_INFO = "miIconifried";
	private final static String	Mi_ICONIC_REPRESENTATION = "miIconifriedIcon";


	public				MiIconifyPartsCommand()
		{
		this(prototypeIcon);
		}
	public				MiIconifyPartsCommand(MiPart icon)
		{
		setPrototypeIcon(icon);
		}

	public				MiIconifyPartsCommand(
						MiEditor editor, 
						MiParts groups, MiParts icons, boolean toIconify)
		{
		setTargetOfCommand(editor);
		this.groups.append(groups);
		this.icons.append(icons);
		this.toIconify = toIconify;
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public static	void		setPrototypeIcon(MiPart icon)
		{
		prototypeIcon = icon;
		}
	public static	MiPart		getPrototypeIcon()
		{
		if (prototypeIcon == null)
			prototypeIcon = new MiImage(MiiLookProperties.Mi_ICONIZED_PARTS_ICON_NAME);
		return(prototypeIcon);
		}
	public		MiPart		makeIconicRepresentation(MiPart container)
		{
		return(getPrototypeIcon().copy());
		}

	public		void		processCommand(String cmd)
		{
		MiPart group;
		MiEditor editor = getEditor();
		MiParts selectedObjects = editor.getSelectedParts(new MiParts());
		MiParts groups = new MiParts();
		MiParts icons = new MiParts();
		if (cmd.equalsIgnoreCase(Mi_GROUP_AND_ICONIFY_COMMAND_NAME)) 
			{	
			if ((selectedObjects.size() > 1) && (!iconifyOnlySinglySelectedGroups))
				{
				group = MiGroupPartsCommand.groupSelectedObjects(editor);
				//editor.appendPart(group);
				groups.addElement(group);
				icons.addElement(makeIconicRepresentation(group));
				processCommand(editor, groups, icons, true);
				}
			else if (selectedObjects.size() == 1)
				{
				group = selectedObjects.elementAt(0);
				if (((!(group instanceof MiConnection)) && (group.getNumberOfParts() > 1))
					|| (group instanceof MiInternalWindow) || (group instanceof MiWindowBorder))
					{
					groups.addElement(group);
					MiPart icon = (MiPart )group.getResource(Mi_ICONIC_REPRESENTATION);
					if (icon == null)
						icon = makeIconicRepresentation(group);
					icons.addElement(icon);
					processCommand(editor, groups, icons, true);
					}
				}
			}
		else if (cmd.equalsIgnoreCase(Mi_DEICONIFY_COMMAND_NAME))
			{	
			editor.getSelectedParts(selectedObjects);
			if (selectedObjects.size() > 0)
				{
				for (int i = 0; i < selectedObjects.size(); ++i)
					{
					MiPart icon = selectedObjects.elementAt(i);
					if ((icon.getResource(Mi_ICONIC_REPRESENTATION_INFO) != null)
						|| (icon.getResource(
						MiInternalWindow.Mi_ICONIC_REPRESENTATION_WINDOW) != null))
						{
						icons.addElement(icon);
						}
					}
				if (icons.size() > 0)
					processCommand(editor, groups, icons, false);
				}
			}
		}
	public		void		processCommand(MiEditor editor, MiParts groups, MiParts icons, boolean toIconify)
		{
		doit(editor, groups, icons, toIconify);
		MiSystem.getTransactionManager().appendTransaction(
			new MiIconifyPartsCommand(editor, groups, icons, toIconify));
		}
	protected	void		doit(MiEditor editor, MiParts groups, MiParts icons, boolean toIconify)
		{
		if (toIconify)
			{
			for (int i = 0; i < groups.size(); ++i)
				iconify(getEditor(), groups.elementAt(i), icons.elementAt(i));
			}
		else
			{
			groups.removeAllElements();
			for (int i = 0; i < icons.size(); ++i)
				groups.addElement(deIconify(getEditor(), icons.elementAt(i)));
			}
		MiSelectionManager.notifyAboutNumberOfShapesSelected(editor);
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
		if (toIconify)
			return(Mi_ICONIFY_DISPLAY_NAME);
		else
			return(Mi_DEICONIFY_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		if (toIconify)
			return(Mi_ICONIFY_COMMAND_NAME);
		else
			return(Mi_DEICONIFY_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), groups, icons, toIconify);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), groups, icons, !toIconify);
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
		return(icons);
		}
	
	public static	void		iconify(MiEditor editor, MiPart container, MiPart icon)
		{
		if (container instanceof MiInternalWindow)
			{
			((MiInternalWindow )container).iconify();
			}
		else if (container instanceof MiWindowBorder)
			{
			((MiInternalWindow)((MiWindowBorder )container).getSubject()).iconify();
			}
		else if (container.getResource(Mi_ICONIC_REPRESENTATION_INFO) == null)
			{
			if (!container.dispatchActionRequest(Mi_ICONIFY_ACTION))
				return;

			MiPoint center = container.getCenter();
			MiPositionRestorer restorer = new MiPositionRestorer(center);
			container.setResource(Mi_ICONIC_REPRESENTATION, icon);
			icon.setResource(Mi_ICONIC_REPRESENTATION_INFO, restorer);

			editor.deSelect(container);
			for (int i = 0; i < container.getNumberOfParts(); ++i)
				{
				restorer.savePosition(container.getPart(i));
				container.getPart(i).setCenter(center);
				}
			container.replaceSelf(icon);
			icon.setCenter(center);
			container.setVisible(false);
			icon.appendAttachment(container);
			container.dispatchAction(Mi_ICONIFY_ACTION, icon);
			}
		
		}
	public static	MiPart		deIconify(MiEditor editor, MiPart icon)
		{
		MiInternalWindow window = 
			(MiInternalWindow)icon.getResource(MiInternalWindow.Mi_ICONIC_REPRESENTATION_WINDOW);
		if (window != null)
			{
			window.deIconify();
			return(null);
			}
			
		MiPositionRestorer restorer = 
			(MiPositionRestorer)icon.getResource(Mi_ICONIC_REPRESENTATION_INFO);
		if (restorer != null)
			{
			MiPart container = icon.getAttachment(0);
			if (!container.dispatchActionRequest(Mi_DEICONIFY_ACTION))
				return(container);

			editor.deSelect(icon);
			MiPoint center = icon.getCenter();
			icon.removeAttachment(container);
			restorer.restorePositions(center);
			container.setVisible(true);
			icon.replaceSelf(container);
			icon.deleteSelf();
			container.dispatchAction(Mi_DEICONIFY_ACTION, icon);
			return(container);
			}
		return(null);
		}
	public static	boolean		isIconification(MiPart icon)
		{
		return((icon.getResource(Mi_ICONIC_REPRESENTATION_INFO) != null)
			|| (icon.getResource(MiInternalWindow.Mi_ICONIC_REPRESENTATION_WINDOW) != null));
		}
	public static	MiInternalWindow getWindowFromIcon(MiPart icon)
		{
		return((MiInternalWindow)icon.getResource(MiInternalWindow.Mi_ICONIC_REPRESENTATION_WINDOW));
		}
	}
class MiPositionRestorer
	{
	MiPoint		center		= new MiPoint();
	MiParts		parts		= new MiParts();
	FastVector	positions	= new FastVector();

					MiPositionRestorer(MiPoint origCenter)
		{
		center.copy(origCenter);
		}
			void		savePosition(MiPart part)
		{
		parts.addElement(part);
		positions.addElement(part.getCenter());
		}

			void		restorePositions(MiPoint newCenter)
		{
		for (int i = 0; i < positions.size(); ++i)
			{
			((MiPoint )positions.elementAt(i)).x += newCenter.x - center.x;
			((MiPoint )positions.elementAt(i)).y += newCenter.y - center.y;
			}
		for (int i = 0; i < parts.size(); ++i)
			{
			parts.elementAt(i).setCenter((MiPoint )positions.elementAt(i));
			}
		}
	}
