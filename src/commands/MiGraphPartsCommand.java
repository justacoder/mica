
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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphPartsCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames, MiiTypes, MiiPartsDefines
	{
	private		MiTreeGraphLayout	treeGraphLayout		= new MiTreeGraphLayout();
	private		MiOutlineGraphLayout	outlineGraphLayout	= new MiOutlineGraphLayout();
	private		MiUndirGraphLayout	undirGraphLayout	= new MiUndirGraphLayout();
	private		MiStarGraphLayout	starGraphLayout		= new MiStarGraphLayout();
	private		MiRingGraphLayout	ringGraphLayout		= new MiRingGraphLayout();
	private		MiLineGraphLayout	lineGraphLayout		= new MiLineGraphLayout();
	private		MiOmegaGraphLayout	omegaGraphLayout	= new MiOmegaGraphLayout();
	private		MiCrossBarGraphLayout	crossBarGraphLayout	= new MiCrossBarGraphLayout();
	private		Mi2DMeshGraphLayout	twoDMeshGraphLayout	= new Mi2DMeshGraphLayout();

	private		String 		formatCommand;
	private		MiPart 		formattedContainer;
	private		MiParts 	formattedParts			= new MiParts();
	private		MiParts 	formatCreatedPlaceHolders	= new MiParts();
	private		FastVector	originalLayouts;
	private		FastVector	originalPositions;


	public				MiGraphPartsCommand()
		{
		}

	public				MiGraphPartsCommand(MiEditor editor,
						String formatCommand, 
						MiParts formattedParts)
		{
		setTargetOfCommand(editor);
		this.formattedParts.append(formattedParts);
		this.formatCommand = formatCommand;
		}
	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		processCommand(String command)
		{
		processCommand(getEditor(), command, getEditor().getSelectedParts(new MiParts()));
		}

	public		void		processCommand(MiEditor editor, 
						String formatCommand,
						MiParts formattedParts)
		{
		MiGraphPartsCommand cmd = new MiGraphPartsCommand(editor, formatCommand, formattedParts);
		cmd.doit(false);
		MiSystem.getTransactionManager().appendTransaction(cmd);
		}
	protected	void		doit(boolean toUndo)
		{
		MiEditor editor = getEditor();
		if (!toUndo)
			{
			formattedContainer = format(formattedParts, formatCommand, formatCreatedPlaceHolders);
			}
		else
			{
			if (originalLayouts != null)
				{
				for (int i = 0; i < formattedParts.size(); ++i)
					{
					formattedParts.elementAt(i).setLayout(
						(MiiLayout )originalLayouts.elementAt(i));
					}
				}
			if (originalPositions != null)
				{
				for (int i = 0; i < formattedParts.size(); ++i)
					{
					formattedParts.elementAt(i).setCenter(
						(MiPoint )originalPositions.elementAt(i));
					}
				}
			for (int i = 0; i < formatCreatedPlaceHolders.size(); ++i)
				{
				formatCreatedPlaceHolders.elementAt(i).removeSelf();
				}
			if (formattedContainer != null)
				{
				while(formattedContainer.getNumberOfParts() > 0)
					{
					MiPart part = formattedContainer.getPart(0);
					formattedContainer.removePart(part);
					editor.appendItem(part);
					}
				editor.removeItem(formattedContainer);
				}
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
		return(MiSystem.getProperty(formatCommand, formatCommand));
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(formatCommand);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(false);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(true);
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
		return(formattedParts);
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		return(formatCreatedPlaceHolders);
		}
	public 		MiPart		format(MiParts parts, String command, MiParts placeHoldersMade)
		{
		MiPart c = null;
		placeHoldersMade.removeAllElements();
		if (command.equalsIgnoreCase(Mi_ARRANGE_TREE_COMMAND_NAME))
			{
			arrangeTargetParts(parts, treeGraphLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_NETWORK_COMMAND_NAME))
			{
			arrangeTargetParts(parts, undirGraphLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_OUTLINE_COMMAND_NAME))
			{
			arrangeTargetParts(parts, outlineGraphLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_STAR_COMMAND_NAME))
			{
			arrangeTargetParts(parts, starGraphLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_RING_COMMAND_NAME))
			{
			arrangeTargetParts(parts, ringGraphLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_CROSSBAR_COMMAND_NAME))
			{
			arrangeTargetParts(parts, crossBarGraphLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_LINE_COMMAND_NAME))
			{
			arrangeTargetParts(parts, lineGraphLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_OMEGA_COMMAND_NAME))
			{
			arrangeTargetParts(parts, omegaGraphLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_2DMESH_COMMAND_NAME))
			{
			arrangeTargetParts(parts, twoDMeshGraphLayout);
			}

		else if (command.equalsIgnoreCase(Mi_BUILD_TREE_COMMAND_NAME))
			{
			buildTargetParts(parts, treeGraphLayout, placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_BUILD_NETWORK_COMMAND_NAME))
			{
			buildTargetParts(parts, undirGraphLayout, placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_BUILD_OUTLINE_COMMAND_NAME))
			{
			buildTargetParts(parts, outlineGraphLayout, placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_BUILD_STAR_COMMAND_NAME))
			{
			buildTargetParts(parts, starGraphLayout, placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_BUILD_RING_COMMAND_NAME))
			{
			buildTargetParts(parts, ringGraphLayout, placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_BUILD_CROSSBAR_COMMAND_NAME))
			{
			buildTargetParts(parts, crossBarGraphLayout, placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_BUILD_LINE_COMMAND_NAME))
			{
			buildTargetParts(parts, lineGraphLayout, placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_BUILD_OMEGA_COMMAND_NAME))
			{
			buildTargetParts(parts, omegaGraphLayout, placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_BUILD_2DMESH_COMMAND_NAME))
			{
			buildTargetParts(parts, twoDMeshGraphLayout, placeHoldersMade);
			}

		else if (command.equalsIgnoreCase(Mi_FORMAT_TREE_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new MiTreeGraphLayout(), placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_NETWORK_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new MiUndirGraphLayout(), placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_OUTLINE_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new MiOutlineGraphLayout(), placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_STAR_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new MiStarGraphLayout(), placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_RING_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new MiRingGraphLayout(), placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_CROSSBAR_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new MiCrossBarGraphLayout(), placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_LINE_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new MiLineGraphLayout(), placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_OMEGA_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new MiOmegaGraphLayout(), placeHoldersMade);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_2DMESH_COMMAND_NAME))
			{
			c = formatTargetParts(parts, new Mi2DMeshGraphLayout(), placeHoldersMade);
			}
		return(c);
		}
					/**------------------------------------------------------
	 				 * Arranges the selected parts as per the given layout.
					 * @param layout 	specifies how to arrange the parts
					 *------------------------------------------------------*/
	protected	void		arrangeTargetParts(MiParts parts, MiiLayout layout)
		{
		getOriginalPositions(parts);
		if (parts.size() > 1)
			{
			MiContainer container = new MiContainer();
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart part = parts.elementAt(i);
				container.appendPart(part);
				}
			container.setLayout(layout);
			container.layoutParts();
			container.removeAllParts();
			}
		else
			{
			getOriginalLayouts(parts);
			if (parts.elementAt(0).isUngroupable())
				return;
			parts.elementAt(0).setLayout(layout);
			layout.layoutParts();
			parts.elementAt(0).setLayout(null);
			}
		}
					/**------------------------------------------------------
	 				 * Arranges the selected parts as per the given layout and
					 * creates any connections needed, as also specified by
					 * the given layout.
					 * @param layout 	specifies how to arrange the parts
					 *------------------------------------------------------*/
	protected	void		buildTargetParts(MiParts parts, 
						MiiManipulatableLayout layout, MiParts placeHoldersMade)
		{
		MiEditor editor = getEditor();
		getOriginalPositions(parts);
		if (parts.size() > 1)
			{
			MiContainer container = new MiContainer();
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart part = parts.elementAt(i);
				editor.deSelect(part);
				editor.getCurrentLayer().removePart(part);
				container.appendPart(part);
				}
			container.setLayout(layout);
			layout.formatEmptyTarget();
			layout.layoutParts();
			for (int i = 0; i < container.getNumberOfParts(); ++i)
				{
				editor.getCurrentLayer().appendPart(container.getPart(i));
				if (container.getPart(i) instanceof MiPlaceHolder)
					placeHoldersMade.addElement(container.getPart(i));
				}
			container.removeAllParts();
			}
		else
			{
			getOriginalLayouts(parts);
			if (parts.elementAt(0).isUngroupable())
				return;
			parts.elementAt(0).setLayout(layout);
			layout.formatEmptyTarget();
			layout.layoutParts();
			parts.elementAt(0).setLayout(null);
			}
		}
					/**------------------------------------------------------
	 				 * Assigns the given layout to the selected parts and lays
					 * them out and creates any connections needed, as specified
					 * by the given layout.
					 * @param layout 	specifies how to arrange the parts
					 *------------------------------------------------------*/
	protected	MiPart		formatTargetParts(MiParts parts, 
						MiiManipulatableLayout layout, MiParts placeHoldersMade)
		{
		MiEditor editor = getEditor();
		getOriginalPositions(parts);
		if (parts.size() > 1)
			{
			MiContainer container = new MiContainer();
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart part = parts.elementAt(i);
				editor.deSelect(part);
				editor.getCurrentLayer().removePart(part);
				container.appendPart(part);
				}
			container.setLayout(layout);
			container.appendEventHandler(new MiManipulatorTargetEventHandler());
			layout.formatEmptyTarget();
			layout.layoutParts();
			editor.getCurrentLayer().appendPart(container);
			for (int i = 0; i < container.getNumberOfParts(); ++i)
				{
				if (container.getPart(i) instanceof MiPlaceHolder)
					placeHoldersMade.addElement(container.getPart(i));
				}
			return(container);
			}
		else
			{
			getOriginalLayouts(parts);
			if (parts.elementAt(0).isUngroupable())
				return(null);
			parts.elementAt(0).setLayout(layout);
			parts.elementAt(0).appendEventHandler(new MiManipulatorTargetEventHandler());
			layout.formatEmptyTarget();
			layout.layoutParts();
			return(parts.elementAt(0));
			}
		}
					/**------------------------------------------------------
	 				 * Gets the center positions of all of the given parts
					 * and saves them into the originalPositions list.
					 * @param parts		the parts
					 *------------------------------------------------------*/
	protected  	void		getOriginalPositions(MiParts parts)
		{
		originalPositions = new FastVector();
		for (int i = 0; i < parts.size(); ++i)
			{
			originalPositions.addElement(parts.elementAt(i).getCenter());
			}
		}
					/**------------------------------------------------------
	 				 * Gets the layouts of all of the given parts and saves
					 * them into the originalLayouts list.
					 * @param parts		the parts
					 *------------------------------------------------------*/
	protected  	void		getOriginalLayouts(MiParts parts)
		{
		originalLayouts = new FastVector();
		for (int i = 0; i < parts.size(); ++i)
			{
			originalLayouts.addElement(parts.elementAt(i).getLayout());
			}
		}
	}


