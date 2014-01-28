
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
import com.swfm.mica.util.DoubleVector; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiLayoutPartsCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames, MiiTypes, MiiPartsDefines, MiiActionTypes
	{
	private static	MiRowLayout 	rowLayout		= new MiRowLayout();
	private static	MiColumnLayout 	columnLayout		= new MiColumnLayout();
	private static	MiGridLayout 	gridLayout		= new MiGridLayout();

	private		boolean 	selectNewlyFormattedGroup = true;
	private		String 		formatCommand;
	private		MiPart 		formattedContainer;
	private		MiiLayout 	originalFormattedContainerLayout;
	private		MiParts 	formattedParts		= new MiParts();
	private		FastVector	originalLayouts;
	private		FastVector	originalPositions;
	private		DoubleVector	originalHeights;
	private		DoubleVector	originalWidths;


	public				MiLayoutPartsCommand()
		{
		}

	public				MiLayoutPartsCommand(MiEditor editor,
						String formatCommand, 
						MiParts formattedParts)
		{
		setTargetOfCommand(editor);
		this.formattedParts.append(formattedParts);
		this.formatCommand = formatCommand;
		}
	public static	void		setPrototypeRowLayout(MiRowLayout layout)
		{
		rowLayout = layout;
		}
	public static	MiRowLayout	getPrototypeRowLayout()
		{
		return(rowLayout);
		}
	public static	void		setPrototypeColumnLayout(MiColumnLayout layout)
		{
		columnLayout = layout;
		}
	public static	MiColumnLayout	getPrototypeColumnLayout()
		{
		return(columnLayout);
		}
	public static	void		setPrototypeGridLayout(MiGridLayout layout)
		{
		gridLayout = layout;
		}
	public static	MiGridLayout	getPrototypeGridLayout()
		{
		return(gridLayout);
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
		MiLayoutPartsCommand cmd = new MiLayoutPartsCommand(editor, formatCommand, formattedParts);
		cmd.doit(false);
		MiSystem.getTransactionManager().appendTransaction(cmd);
		}
	protected	void		doit(boolean toUndo)
		{
		MiEditor editor = getEditor();
		if (!toUndo)
			{
			formattedContainer = format(formattedParts, formatCommand);
			if (formattedContainer != null)
				{
				formattedContainer.dispatchAction(Mi_FORMAT_ACTION);
				}
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
			if (originalHeights != null)
				{
				for (int i = 0; i < formattedParts.size(); ++i)
					{
					formattedParts.elementAt(i).setHeight(
						originalHeights.elementAt(i));
					}
				}
			if (originalWidths != null)
				{
				for (int i = 0; i < formattedParts.size(); ++i)
					{
					formattedParts.elementAt(i).setWidth(
						originalWidths.elementAt(i));
					}
				}
			if (formattedContainer != null)
				{
				if (originalFormattedContainerLayout != null)
					{
					formattedContainer.setLayout(originalFormattedContainerLayout);
					}
				else
					{
					while(formattedContainer.getNumberOfParts() > 0)
						{
						MiPart part = formattedContainer.getPart(0);
						formattedContainer.removePart(part);
						editor.appendItem(part);
						}
					editor.removeItem(formattedContainer);
					formattedContainer.dispatchAction(Mi_UNFORMAT_ACTION);
					}
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
		return(null);
		}
	
	public 	MiPart		format(MiParts parts, String command)
		{
		if (command.equalsIgnoreCase(Mi_ARRANGE_GRID_COMMAND_NAME))
			{
			getOriginalPositions(parts);
			if (parts.size() > 3)
				gridLayout.setNumberOfColumns((int )Math.sqrt(parts.size()));
			arrangeTargetParts(parts, gridLayout);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_ROW_COMMAND_NAME))
			{
			rowLayout.setElementVJustification(Mi_CENTER_JUSTIFIED);
			getOriginalPositions(parts);
			arrangeTargetParts(parts, rowLayout);
			}
		else if (command.equalsIgnoreCase(Mi_SAME_HEIGHT_COMMAND_NAME))
			{
			originalHeights = new DoubleVector();
			MiDistance height = Mi_MIN_DISTANCE_VALUE;
			for (int i = 0; i < parts.size(); ++i)
				{
				MiDistance partHeight = parts.elementAt(i).getHeight();
				originalHeights.addElement(partHeight);
				if (partHeight > height)
					height = partHeight;
				}
			for (int i = 0; i < parts.size(); ++i)
				{
				parts.elementAt(i).setHeight(height);
				}
			}
		else if (command.equalsIgnoreCase(Mi_ALIGN_BOTTOM_COMMAND_NAME))
			{
			MiCoord ymin = Mi_MAX_COORD_VALUE;
			originalPositions = new FastVector();
			for (int i = 0; i < parts.size(); ++i)
				{
				originalPositions.addElement(parts.elementAt(i).getCenter());
				if (parts.elementAt(i).getYmin() < ymin)
					ymin = parts.elementAt(i).getYmin();
				}
			for (int i = 0; i < parts.size(); ++i)
				{
				parts.elementAt(i).translate(0, ymin - parts.elementAt(i).getYmin());
				}
			snapPartsToGridIfNecessary(parts);
			}
		else if (command.equalsIgnoreCase(Mi_ALIGN_TOP_COMMAND_NAME))
			{
			MiCoord ymax = Mi_MIN_COORD_VALUE;
			originalPositions = new FastVector();
			for (int i = 0; i < parts.size(); ++i)
				{
				originalPositions.addElement(parts.elementAt(i).getCenter());
				if (parts.elementAt(i).getYmax() > ymax)
					ymax = parts.elementAt(i).getYmax();
				}
			for (int i = 0; i < parts.size(); ++i)
				{
				parts.elementAt(i).translate(0, ymax - parts.elementAt(i).getYmax());
				}
			snapPartsToGridIfNecessary(parts);
			}
		else if (command.equalsIgnoreCase(Mi_ARRANGE_COLUMN_COMMAND_NAME))
			{
			columnLayout.setElementHJustification(Mi_CENTER_JUSTIFIED);
			getOriginalPositions(parts);
			arrangeTargetParts(parts, columnLayout);
			}
		else if (command.equalsIgnoreCase(Mi_SAME_WIDTH_COMMAND_NAME))
			{
			MiDistance width = Mi_MIN_DISTANCE_VALUE;
			originalWidths = new DoubleVector();
			for (int i = 0; i < parts.size(); ++i)
				{
				MiDistance partWidth = parts.elementAt(i).getWidth();
				originalWidths.addElement(partWidth);
				if (partWidth > width)
					width = partWidth;
				}
			for (int i = 0; i < parts.size(); ++i)
				{
				parts.elementAt(i).setWidth(width);
				}
			}
		else if (command.equalsIgnoreCase(Mi_ALIGN_LEFT_COMMAND_NAME))
			{
			MiCoord xmin = Mi_MAX_COORD_VALUE;
			originalPositions = new FastVector();
			for (int i = 0; i < parts.size(); ++i)
				{
				originalPositions.addElement(parts.elementAt(i).getCenter());
				if (parts.elementAt(i).getXmin() < xmin)
					xmin = parts.elementAt(i).getXmin();
				}
			for (int i = 0; i < parts.size(); ++i)
				{
				parts.elementAt(i).translate(xmin - parts.elementAt(i).getXmin(), 0);
				}
			snapPartsToGridIfNecessary(parts);
			}
		else if (command.equalsIgnoreCase(Mi_ALIGN_RIGHT_COMMAND_NAME))
			{
			MiCoord xmax = Mi_MIN_COORD_VALUE;
			originalPositions = new FastVector();
			for (int i = 0; i < parts.size(); ++i)
				{
				originalPositions.addElement(parts.elementAt(i).getCenter());
				if (parts.elementAt(i).getXmax() > xmax)
					xmax = parts.elementAt(i).getXmax();
				}
			for (int i = 0; i < parts.size(); ++i)
				{
				parts.elementAt(i).translate(xmax - parts.elementAt(i).getXmax(), 0);
				}
			snapPartsToGridIfNecessary(parts);
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_GRID_COMMAND_NAME))
			{
			getOriginalPositions(parts);
			return(formatTargetParts(parts, (MiGridLayout )gridLayout.deepCopy()));
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_ROW_COMMAND_NAME))
			{
			getOriginalPositions(parts);
			getOriginalLayouts(parts);
			MiRowLayout layout = (MiRowLayout )rowLayout.deepCopy();
// Now use getPrototypeRowLayout().setElementVSizing(Mi_NONE);
			//layout.setElementVSizing(Mi_NONE);
			//layout.setAlleySpacing(2);
			return(formatTargetParts(parts, layout));
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_BJ_ROW_COMMAND_NAME))
			{
			getOriginalPositions(parts);
			getOriginalLayouts(parts);
			MiRowLayout layout = (MiRowLayout )rowLayout.deepCopy();
			layout.setElementVJustification(Mi_BOTTOM_JUSTIFIED);
			//layout.setAlleySpacing(2);
			return(formatTargetParts(parts, layout));
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_TJ_ROW_COMMAND_NAME))
			{
			getOriginalPositions(parts);
			getOriginalLayouts(parts);
			MiRowLayout layout = (MiRowLayout )rowLayout.deepCopy();
			layout.setElementVJustification(Mi_TOP_JUSTIFIED);
			//layout.setAlleySpacing(2);
			return(formatTargetParts(parts, layout));
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_COLUMN_COMMAND_NAME))
			{
			getOriginalPositions(parts);
			getOriginalLayouts(parts);
			MiColumnLayout layout = (MiColumnLayout )columnLayout.deepCopy();
			//layout.setElementHSizing(Mi_NONE);
			//layout.setAlleySpacing(2);
			return(formatTargetParts(parts, layout));
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_LJ_COLUMN_COMMAND_NAME))
			{
			getOriginalPositions(parts);
			getOriginalLayouts(parts);
			MiColumnLayout layout = (MiColumnLayout )columnLayout.deepCopy();
			layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
			//layout.setAlleySpacing(2);
			return(formatTargetParts(parts, layout));
			}
		else if (command.equalsIgnoreCase(Mi_FORMAT_RJ_COLUMN_COMMAND_NAME))
			{
			getOriginalPositions(parts);
			getOriginalLayouts(parts);
			MiColumnLayout layout = (MiColumnLayout )columnLayout.deepCopy();
			layout.setElementHJustification(Mi_RIGHT_JUSTIFIED);
			//layout.setAlleySpacing(2);
			return(formatTargetParts(parts, layout));
			}
		return(null);
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
					/**------------------------------------------------------
	 				 * Arranges the selected parts as per the given layout.
					 * @param layout 	specifies how to arrange the parts
					 *------------------------------------------------------*/
	protected  	void		arrangeTargetParts(MiParts parts, MiiLayout layout)
		{
		if (parts.size() > 1)
			{
			MiContainer container = new MiContainer();
			container.setBounds(getMaxSizeRequiredForContainer(parts, layout));

			// ---------------------------------------------------------------
			// Add parts to the container
			// ---------------------------------------------------------------
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart part = parts.elementAt(i);
				container.appendPart(part);
				}

			// ---------------------------------------------------------------
			// Do the layout...
			// ---------------------------------------------------------------
			container.setLayout(layout);
			if (layout instanceof MiGridLayout)
				{
				MiBounds b = container.getBounds();
				b.setSize(layout.getPreferredSize(new MiSize()));
				container.setBounds(b);
				}
			container.layoutParts();
			container.removeAllParts();
			container.setLayout(null);
			snapPartsToGridIfNecessary(parts);
			}
		else
			{
			if (!parts.elementAt(0).isUngroupable())
				return;
			parts.elementAt(0).setLayout(layout);
			layout.layoutParts();
			parts.elementAt(0).setLayout(null);
			}
		}
	protected	void		snapPartsToGridIfNecessary(MiParts parts)
		{
		if (getEditor().getSnapManager() != null)
			{
			MiEditor editor = getEditor();
			MiVector snapTranslation = new MiVector();
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart part = parts.elementAt(i);
				snapTranslation.set(0, 0);
				editor.snapMovingPart(part, snapTranslation);
				part.translate(snapTranslation);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Assigns the given layout to the selected parts.
					 * @param layout 	specifies how to arrange the parts
					 *------------------------------------------------------*/
	protected  MiPart		formatTargetParts(MiParts parts, MiiLayout layout)
		{
		MiEditor editor = getEditor();
		if (parts.size() > 1)
			{
			MiContainer container = new MiContainer();
			container.setBounds(getMaxSizeRequiredForContainer(parts, layout));
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart part = parts.elementAt(i);
				editor.deSelect(part);
				editor.getCurrentLayer().removePart(part);
				container.appendPart(part);
				}
			container.setLayout(layout);
			if (layout instanceof MiGridLayout)
				{
				MiBounds b = container.getBounds();
				b.setSize(layout.getPreferredSize(new MiSize()));
				container.setBounds(b);
				}
			layout.layoutParts();
			editor.getCurrentLayer().appendPart(container);
			if (selectNewlyFormattedGroup)
				{
				editor.select(container);
				}
			return(container);
			}
		else
			{
			if (!parts.elementAt(0).isUngroupable())
				{
				return(null);
				}

			MiContainer container = (MiContainer )parts.elementAt(0);

			originalFormattedContainerLayout = container.getLayout();

			MiParts subParts = new MiParts();
			for (int i = 0; i < container.getNumberOfParts(); ++i)
				{
				subParts.addElement(container.getPart(i));
				}
			container.setBounds(getMaxSizeRequiredForContainer(subParts, layout));
			container.setLayout(layout);
			layout.layoutParts();
			if (selectNewlyFormattedGroup)
				{
				editor.select(container);
				}
			return(container);
			}
		}
	protected	MiBounds	getMaxSizeRequiredForContainer(MiParts parts, MiiLayout layout)
		{
		// ---------------------------------------------------------------
		// Make sure the container is big enough to hold all of the parts
		// otherwise the layout will shrink them in order that they fit
		// ---------------------------------------------------------------
		MiSize prefSize = new MiSize();
		MiSize partSize = new MiSize();
		MiSize maxPartSize = new MiSize();
		MiBounds bounds = new MiBounds();
		MiBounds tmpBounds = new MiBounds();

		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart part = parts.elementAt(i);
			bounds.union(part.getBounds(tmpBounds));
			part.getPreferredSize(prefSize);
			part.getSize(partSize);
			prefSize.union(partSize);
			maxPartSize.union(prefSize);
			}

		MiLayout l = (MiLayout )layout;
		MiSize containerSize = new MiSize(
			parts.size() 
			* (maxPartSize.getWidth() + l.getAlleyHSpacing() + l.getCellMargins().getWidth()) 
			+ l.getInsetMargins().getWidth(),
			parts.size() 
			* (maxPartSize.getHeight() + l.getAlleyVSpacing() + l.getCellMargins().getHeight()) 
			+ l.getInsetMargins().getHeight());
		bounds.setSize(containerSize);
		return(bounds);
		}
	}


