
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
import java.awt.Color; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSwatchesColorPalette extends MiWidget implements MiiColorPalette, MiiActionHandler, MiiActionTypes, MiiCommandHandler, MiiCommandNames
	{
	private		MiParts		colorSwatches		= new MiParts();
	private		MiSize		swatchSize;
	private		MiBox		swatchesBox;
	private		MiLabel		swatchesLabel;
	private		int		numberOfRows;
	private		int		numberOfColumns;
	private		int		selectedColorIndex	= -1;
	private		Color		selectedColor		= null;
	private		MiGridLayout 	grid;
	private		MiPart	 	hiliteAttachment;
	private		boolean	 	setSelectedSwatchDispatchesValueChangedAction	= true;
	private		boolean	 	arrowKeysSelectAsWellAsBrowseSwatches		= true;
	


	public				MiSwatchesColorPalette()
		{
		this(4, 9);
		}
	public				MiSwatchesColorPalette(MiSize swatchSize)
		{
		this(4, 9, swatchSize, MiColorManager.getColors());
		}
	public				MiSwatchesColorPalette(int rows, int columns)
		{
		this(rows, columns, new MiSize(30, 30));
		}
	public				MiSwatchesColorPalette(int rows, int columns, MiSize swatchSize)
		{
		this(rows, columns, swatchSize, MiColorManager.getColors());
		}
	public				MiSwatchesColorPalette(int rows, int columns, MiSize swatchSize, Color[] colors)
		{
		this.swatchSize = swatchSize;
		build(rows, columns);
		setSwatchColors(colors);
		packLayout();
		setSelection(MiColorManager.white);
		}

	public		void		setArrowKeysSelectAsWellAsBrowseSwatches(boolean flag)
		{
		arrowKeysSelectAsWellAsBrowseSwatches = flag;
		}
	public		boolean		getArrowKeysSelectAsWellAsBrowseSwatches()
		{
		return(arrowKeysSelectAsWellAsBrowseSwatches);
		}

	public		MiPart		getPalette()
		{
		return(this);
		}
	public		void		setSwatchSpacing(MiDistance spacing)
		{
		grid.setGridSpacing(spacing);
		}
	public		void		setSwatchSizes(MiSize size)
		{
		swatchSize = size;
		MiBounds b = new MiBounds(size);
		for (int i = 0; i < colorSwatches.size(); ++i)
			{
			colorSwatches.elementAt(i).setBounds(b);
			}
		}
	public		void		setSwatchColors(Color[] colors)
		{
		for (int i = 0; i < colorSwatches.size(); ++i)
			{
			if (i >= colors.length)
				break;

			colorSwatches.elementAt(i).setBackgroundColor(colors[i]);
			if (colors[i] == null)
				{
				MiPart swatch = colorSwatches.elementAt(i);
				MiBounds b = swatch.getInnerBounds();
				swatch.appendAttachment(new MiLine(b.getLLCorner(), b.getURCorner()),
					Mi_CENTER_LOCATION, null, null);
				swatch.appendAttachment(new MiLine(b.getLRCorner(), b.getULCorner()),
					Mi_CENTER_LOCATION, null, null);
				}
			String name = MiColorManager.getColorName(colors[i]);
			if (name != null)
				colorSwatches.elementAt(i).setToolHintMessage(name);
			}
		setSelection(selectedColor);
		}
	public		void		addColorSwatch(Color color)
		{
		MiRectangle rect = new MiRectangle(new MiBounds(swatchSize));
		rect.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		rect.setBackgroundColor(color);
		colorSwatches.addElement(rect);
		grid.appendPart(rect);
		}
	public		void		validateLayout()
		{
		super.validateLayout();
		if (selectedColorIndex != -1)
			hiliteAttachment.setCenter(colorSwatches.elementAt(selectedColorIndex).getCenter());
		}

	public		void		removeColorSwatch(Color color)
		{
		if (selectedColorIndex != -1)
			{
			grid.removePart(colorSwatches.elementAt(selectedColorIndex));
			colorSwatches.removeElementAt(selectedColorIndex);
			selectedColorIndex = -1;
			hiliteAttachment.setVisible(false);
			}
		}
	public		String		getValue()
		{
		return(MiColorManager.getColorName(selectedColor));
		}
	public		void		setValue(String value)
		{
		setSelection(MiColorManager.getColor(value));
		}

	public		Color		getSelection()
		{
		return(selectedColor);
		}
	public		void		setSelection(Color color)
		{
		if (selectedColor == color)
			return;

		selectedColor = color;
		for (int i = 0; i < colorSwatches.size(); ++i)
			{
			Color swatchColor = colorSwatches.elementAt(i).getBackgroundColor();
			if (((swatchColor != null) && (color != null) && (swatchColor.equals(color)))
				|| ((swatchColor == null) && (color == null)))
				{
				setSelectedSwatch(i);
				return;
				}
			}
		setSelectedSwatch(-1);
		}
	public		void		setSelectedSwatch(int index)
		{
		if (selectedColorIndex == index)
			return;

		if (selectedColorIndex != -1)
			{
			hiliteAttachment.setVisible(false);
			}
		selectedColorIndex = index;
		if (selectedColorIndex != -1)
			{
			hiliteAttachment.setVisible(true);
			hiliteAttachment.setCenter(colorSwatches.elementAt(index).getCenter());
			selectedColor = colorSwatches.elementAt(index).getBackgroundColor();
			}
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	public		int		getSelectedSwatch()
		{
		return(selectedColorIndex);
		}
	public		int		getNumberOfSwatches()
		{
		return(colorSwatches.size());
		}

	protected	void		build(int rows, int columns)
		{
		setBorderLook(Mi_NONE);

		MiColumnLayout layout = new MiColumnLayout();
		setLayout(layout);

		numberOfRows = rows;
		numberOfColumns = columns;

		swatchesBox = new MiBox();
		swatchesBox.setInsetMargins(2);
		swatchesBox.setBorderLook(Mi_NONE);
		swatchesBox.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		swatchesBox.appendActionHandler(
			new MiAction(this, 0), new MiEvent(MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0));
		swatchesBox.appendEventHandler(
				new MiIExecuteCommand(
					new MiEvent(Mi_KEY_EVENT, Mi_UP_ARROW_KEY, 0),
					this, 
					Mi_UP_COMMAND_NAME,
					Mi_CONSUME_EVENT));
		swatchesBox.appendEventHandler(
				new MiIExecuteCommand(
					new MiEvent(Mi_KEY_EVENT, Mi_DOWN_ARROW_KEY, 0),
					this, 
					Mi_DOWN_COMMAND_NAME,
					Mi_CONSUME_EVENT));
		swatchesBox.appendEventHandler(
				new MiIExecuteCommand(
					new MiEvent(Mi_KEY_EVENT, Mi_LEFT_ARROW_KEY, 0),
					this, 
					Mi_LEFT_COMMAND_NAME,
					Mi_CONSUME_EVENT));
		swatchesBox.appendEventHandler(
				new MiIExecuteCommand(
					new MiEvent(Mi_KEY_EVENT, Mi_RIGHT_ARROW_KEY, 0),
					this, 
					Mi_RIGHT_COMMAND_NAME,
					Mi_CONSUME_EVENT));

		appendPart(swatchesBox);

		MiColumnLayout column = new MiColumnLayout();
		swatchesBox.setLayout(column);

		//swatchesLabel = new MiLabel(COLOR_SWATCHES_LABEL_NAME);
		//swatchesBox.appendPart(swatchesLabel);

		hiliteAttachment = new MiRectangle();
		swatchesBox.appendAttachment(hiliteAttachment);
		hiliteAttachment.setLineWidth(3);
		//hiliteAttachment.setSize(swatchSize);
		hiliteAttachment.setWidth(swatchSize.getWidth() + 3);
		hiliteAttachment.setHeight(swatchSize.getHeight() + 3);
		hiliteAttachment.setVisible(false);

		grid = new MiGridLayout();
		grid.setNumberOfColumns(columns);
		grid.setAlleySpacing(3);
		MiBounds swatchBounds = new MiBounds(swatchSize);
		for (int i = 0; i < columns; ++i)
			{
			for (int j = 0; j < rows; ++j)
				{
				MiRectangle rect = new MiRectangle(swatchBounds);
				rect.setBorderLook(Mi_INDENTED_BORDER_LOOK);
				colorSwatches.addElement(rect);
				grid.appendPart(rect);
				}
			}

		swatchesBox.appendPart(grid);
		}
	public		void		processCommand(String command)
		{
		int selectedRow = selectedColorIndex/numberOfColumns;
		int selectedColumn = selectedColorIndex - selectedRow * numberOfColumns;
		if (command.equals(Mi_UP_COMMAND_NAME))
			{
			if (selectedRow > 0)
				--selectedRow;
			}
		else if (command.equals(Mi_DOWN_COMMAND_NAME))
			{
			if (selectedRow < numberOfRows)
				++selectedRow;
			}
		else if (command.equals(Mi_LEFT_COMMAND_NAME))
			{
			if (selectedColumn > 0)
				--selectedColumn;
			}
		else if (command.equals(Mi_RIGHT_COMMAND_NAME))
			{
			if (selectedColumn < numberOfColumns)
				++selectedColumn;
			}
		int colorIndex = selectedRow * numberOfColumns + selectedColumn;
		if ((colorIndex != selectedColorIndex) && (colorIndex < colorSwatches.size()))
			{
			if (!arrowKeysSelectAsWellAsBrowseSwatches)
				setSelectedSwatchDispatchesValueChangedAction = false;

			setSelectedSwatch(colorIndex);

			setSelectedSwatchDispatchesValueChangedAction = true;
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == swatchesBox)
			{
			MiBounds mouseCursor = getContainingEditor().getMousePosition(new MiBounds());
			for (int i = 0; i < colorSwatches.size(); ++i)
				{
				if (colorSwatches.elementAt(i).pick(mouseCursor))
					{
					//selectedColor = colorSwatches.elementAt(i).getBackgroundColor();
					setSelectedSwatch(i);
					return(true);
					}
				}
			}
		return(true);
		}
	}


