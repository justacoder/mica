
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

import java.util.Enumeration;
import java.util.Hashtable;
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Tree; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiEditorPalette extends MiWidget implements MiiActionHandler, MiiActionTypes, MiiCommandHandler, MiiCommandNames, MiiPropertyTypes
	{
	public static final int		TREE_LIST_PALETTE_TYPE	= 1;
	public static final int		OVERLAY_PALETTE_TYPE	= 2;
	public static final int		COMBO_TABLE_PALETTE_TYPE= 3;

	public static String		PALETTE_STATUS_HELP_MESSAGE	= "Drag parts into editor (or from editor using <Shift> key)";

	public static final String	Mi_PALETTE_FIXED_SELECTOR_BUTTON_TYPE_NAME = "fixed-selector-button";

	private		int		paletteType;
	private		MiTreeList 	treeListPalette;
	private		MiComboBox	scrolledListPaletteComboBox;
	private		MiList 	scrolledListPalette;
	private		MiContainer 	paletteContainer;
	private		MiContainer 	palettes;
	private		boolean		editableLabels;
	private		boolean		visibleLabels		= true;
	private		boolean		comboTablePaletteHasLabels	= true;
	private		MiScrolledBox 	scrolledBox;
	private		Hashtable 	comboTablePalettes;
	private		Hashtable 	comboTableSubPaletteButtons;
	private		Strings 	fixedSelectorButtonNames;
	private		MiRowLayout 	comboTablePalettesLayout;
	private		MiDragAndDropBehavior dndBehavior;
	private		MiLayout 	comboTablePaletteLayout;
	private		int 		comboTablePaletteLayoutInsertPointAfterLastFixedSelectorButton;
	private		MiFont 		paletteLabelFont;
	private		MiSize 		standardSizeOfPaletteGraphics;
//	private		MiFont 		paletteSelectorFont;
	private		boolean 	comboBoxOnTopOfQuickSelectorButtons;


	public				MiEditorPalette()
		{
		this(TREE_LIST_PALETTE_TYPE);
		}
	public				MiEditorPalette(int paletteType)
		{
		dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));

		setPaletteType(paletteType);
		appendActionHandler(this, 
			Mi_ACTIONS_OF_PARTS_OF_OBSERVED | Mi_ACTIONS_OF_OBSERVED 
				| Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);
		}

	public		MiTreeList	getTreeListPalette()
		{
		return(treeListPalette);
		}
	public		Hashtable	getComboTablePalettes()
		{
		return(comboTablePalettes);
		}
	public		MiComboBox	getComboTableComboBox()
		{
		return(scrolledListPaletteComboBox);
		}
	public		MiTable[]		getComboTablePalettesArray()
		{
		MiTable[] tables = new MiTable[comboTablePalettes.size()];
		int i = 0;

		Enumeration paletteNames = comboTablePalettes.keys();
		while (paletteNames.hasMoreElements())
			{
			String paletteName = (String )paletteNames.nextElement();
			MiTable table = (MiTable )comboTablePalettes.get(paletteName);
			tables[i++] = table;
			}
		return(tables);
		}

	public		MiList		getOverlayPalette()
		{
		return(scrolledListPalette);
		}
	public		MiScrolledBox	getScrolledBox()
		{
		return(scrolledBox);
		}
	public		MiPart		getContentsBackground()
		{
		return(scrolledBox.getBox());
		}

	public		void		setLabelsAreEditable(boolean flag)
		{
		if (editableLabels == flag)
			return;

		editableLabels = flag;
		for (int i = 0; i < treeListPalette.getNumberOfItems(); ++i)
			{
			MiPart label = treeListPalette.getItem(i);
			((MiTextField )label).setIsEditable(flag);
			}
		}
	public		boolean		getLabelsAreEditable()
		{
		return(editableLabels);
		}
	public		void		setLabelFont(String font)
		{
		paletteLabelFont = new MiFont(font);
		}
	public		String		getLabelFont()
		{
		return(paletteLabelFont.getName());
		}

	public		void		setStandardSizeOfPaletteGraphics(MiSize size)
		{
		standardSizeOfPaletteGraphics = new MiSize(size);
		}
	public		MiSize		getStandardSizeOfPaletteGraphics()
		{
		return(standardSizeOfPaletteGraphics);
		}
/*
	public		void		setSelectorFont(String font)
		{
		paletteSelectorFont = new MiFont(font);
		}
*/
	public		void		setLabelsAreVisible(boolean flag)
		{
		visibleLabels = flag;
		if (paletteType == TREE_LIST_PALETTE_TYPE)
			{
			for (int i = 0; i < treeListPalette.getNumberOfItems(); ++i)
				{
				MiPart label = treeListPalette.getItem(i);
				Object tag = treeListPalette.getTagItem(i);
				if (!treeListPalette.getItemCanHaveChildren(tag))
					label.setVisible(flag);
				}
			}
		else if ((paletteType == COMBO_TABLE_PALETTE_TYPE) && (comboTablePaletteHasLabels))
			{
			MiTable[] tables = getComboTablePalettesArray();
			for (int i = 0; i < tables.length; ++i)
				{	
				MiTable table = tables[i];
				MiTableCells cells = table.getContentsCells();
				for (int j = 0; j < cells.size(); ++j)
					{
//MiDebug.println("cells.elementAt(j)=" + cells.elementAt(j));
//MiDebug.println("cells.elementAt(j).getGraphics()=" + cells.elementAt(j).getGraphics());
					if (cells.elementAt(j).getGraphics() != null)
						{
						cells.elementAt(j).getGraphics().getPart(1).setVisible(flag);
						}
					}
				}
			}
		}
	public		boolean		getLabelsAreVisible()
		{
		return(visibleLabels);
		}
	public		boolean		saveFile(String filename)
		{
		return(true);
		}
	public		boolean		openFile(String filename)
		{
		return(true);
		}
	public		boolean		verifyLossOfAnyChanges()
		{
		return(true);
		}
	public		boolean		getHasChanged()
		{
		return(false);
		}
	public		void		processCommand(String command)
		{
		}

	public		void		setPaletteType(int paletteType)
		{
		this.paletteType = paletteType;

		removeAllParts();

		if (paletteType == TREE_LIST_PALETTE_TYPE)
			{
			treeListPalette = new MiTreeList(20, false);
			treeListPalette.setStatusHelpMessage(PALETTE_STATUS_HELP_MESSAGE);
			treeListPalette.setIsDragAndDropTarget(true);
			scrolledBox = new MiScrolledBox(treeListPalette);
			MiToolkit.setBackgroundAttributes(scrolledBox.getBox(), 
				MiiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
			treeListPalette.setDragAndDropBehavior(dndBehavior);
			appendPart(scrolledBox);
			}
		else if (paletteType == COMBO_TABLE_PALETTE_TYPE)
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
			columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			columnLayout.setUniqueElementIndex(-1);
			setLayout(columnLayout);

			comboTablePaletteLayout = this;

			if (comboBoxOnTopOfQuickSelectorButtons)
				{
				comboTablePaletteLayoutInsertPointAfterLastFixedSelectorButton = 1;
				}
			else // Below selector buttons
				{
				comboTablePaletteLayoutInsertPointAfterLastFixedSelectorButton = 0;
				}
			
			scrolledListPaletteComboBox = new MiComboBox();
			scrolledListPaletteComboBox.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
			appendPart(scrolledListPaletteComboBox);

			comboTablePalettes = new Hashtable();
			comboTableSubPaletteButtons = new Hashtable();
			fixedSelectorButtonNames = new Strings();
			MiTable dummyTable = new MiTable();
			scrolledBox = new MiScrolledBox(dummyTable);
			scrolledBox.setInsetMargins(0);
			scrolledBox.getBox().setInsetMargins(0);
			scrolledBox.getBox().setMargins(new MiMargins(0));
			MiToolkit.setBackgroundAttributes(scrolledBox.getBox(), 
				MiiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
			appendPart(scrolledBox);
			}
		else // paletteisGrid contrlled by option menu/combo box
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			setLayout(columnLayout);
			
			scrolledListPaletteComboBox = new MiComboBox();
			scrolledListPaletteComboBox.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
			appendPart(scrolledListPaletteComboBox);
			
			scrolledListPalette = new MiList(8, false);
			scrolledListPalette.setAlleySpacing(4);
			scrolledListPalette.getSelectionManager().setMaximumNumberSelected(0);
			scrolledListPalette.setDragAndDropBehavior(dndBehavior);
			scrolledListPalette.getTableWideDefaults().setHorizontalJustification(Mi_CENTER_JUSTIFIED);
			MiToolkit.setBackgroundAttributes(scrolledListPalette.getContentsBackground(), 
				MiiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
			appendPart(new MiScrolledBox(scrolledListPalette));
			}
		}

	public		void		setPalettes(MiContainer palettes)
		{
		this.palettes = palettes;
		if (paletteType == TREE_LIST_PALETTE_TYPE)
			{
			treeListPalette.removeAllItems();
			if (palettes == null)
				return;

			setPalettes(palettes, null);

			MiAttributes atts = treeListPalette.getAttributes();
			treeListPalette.setAttributes(
				atts.overrideFrom(palettes.getAttributes()));

			atts = treeListPalette.getContentsBackground().getAttributes();
			treeListPalette.getContentsBackground().setAttributes(
				atts.overrideFrom(palettes.getAttributes()));
			}
		else if (paletteType == COMBO_TABLE_PALETTE_TYPE)
			{
			scrolledListPaletteComboBox.setContents(new Strings());
			comboTablePalettes = new Hashtable();
			comboTableSubPaletteButtons = new Hashtable();
			fixedSelectorButtonNames = new Strings();
			if (palettes == null)
				return;

			setPalettes(palettes, null);
			if (scrolledListPaletteComboBox.getContents().size() < 24)
				{
				scrolledListPaletteComboBox.getList().setPreferredNumberOfVisibleRows(
					scrolledListPaletteComboBox.getContents().size());
				}
			}
		else 
			{
			for (int i = 0; i < palettes.getNumberOfParts(); ++i)
				{
				MiPart palette = palettes.getPart(i);
				scrolledListPaletteComboBox.getList().appendItem(palette.getName());
				}
			openScrolledListPalette(0);
			}
		}
	public		void		setPalettes(Tree palettes)
		{
		this.palettes = null;
		if (paletteType == TREE_LIST_PALETTE_TYPE)
			{
			treeListPalette.removeAllItems();
			if (palettes == null)
				return;

			setPalettes(palettes, null);
			}
		else if (paletteType == COMBO_TABLE_PALETTE_TYPE)
			{
			scrolledListPaletteComboBox.setContents(new Strings());
			comboTablePalettes = new Hashtable();
			comboTableSubPaletteButtons = new Hashtable();
			fixedSelectorButtonNames = new Strings();
			if (palettes == null)
				return;

			setPalettes(palettes, null);
			}
		else 
			{
			// NEEDS WORK: no nesting is supported...
			for (int i = 0; i < palettes.size(); ++i)
				{
				MiPart palette = (MiPart )palettes.elementAt(i).getData();
				scrolledListPaletteComboBox.getList().appendItem(palette.getName());
				}
			openScrolledListPalette(0);
			}
		}
	public		Tree		getPalettes()
		{
		Tree root = new Tree(treeListPalette);
		if (paletteType == TREE_LIST_PALETTE_TYPE)
			{
			for (int i = 0; i < treeListPalette.getNumberOfItems(); ++i)
				{
				// Tags are actually the MiParts
				Object tag = treeListPalette.getTagItem(i);
				Object parentTag = treeListPalette.getParentOfItem(tag);
				if (parentTag != null)
					{
					Tree parentTree = root.elementAt(parentTag);
					parentTree.addElement(new Tree(tag));
					}
				else
					{
					root.addElement(new Tree(tag));
					}
				}
			}
		return(root);
		}
	protected	void		setPalettes(Tree palettes, MiPart parent)
		{
		for (int i = 0; i < palettes.size(); ++i)
			{
			Tree node 		= palettes.elementAt(i);
			MiPart part 		= (MiPart )node.getData();
			String name 		= part.getName();
			boolean	isSubTree	= node.size() > 0;
			boolean expandTree	= false;
			if (isSubTree)
				{
				if ((part.getResource(Mi_PALETTE_TYPE_NAME) != null)
					&& (((String )part.getResource(Mi_PALETTE_TYPE_NAME))
					.equalsIgnoreCase(Mi_EXPAND_COMMAND_NAME)))
					{
					expandTree = true;
					}

				if (paletteType == TREE_LIST_PALETTE_TYPE)
					{
					treeListPalette.addItem(name, null, part, parent, true);
					if (!expandTree)
						treeListPalette.collapse(part);
					}
				else if (paletteType == COMBO_TABLE_PALETTE_TYPE)
					{
					scrolledListPaletteComboBox.getList().appendItem(name);
					}
				setPalettes(node, part);
				}
			else
				{
				part.setIsDragAndDropSource(true);
				part.setContextCursor(Mi_HAND_CURSOR);

				MiTextField textField = new MiTextField();
				textField.setBorderLook(Mi_NONE);
				textField.setNumDisplayedColumns(-1);
				textField.setBackgroundColor(Mi_TRANSPARENT_COLOR);
				textField.setSelectedBackgroundColor(MiWidget.getToolkit().getTextSensitiveColor());
				textField.setValue(name);
				textField.appendActionHandler(
					new MiAction(this, Mi_LOST_KEYBOARD_FOCUS_ACTION, part));

				textField.setIsEditable(editableLabels);
				textField.setVisible(visibleLabels);

				treeListPalette.addItem(textField, part, part, palettes, false);
				}
			}
		}
	protected	void		setPalettes(MiPart palettes, MiPart parent)
		{
		int numberOfCells = 0;
		for (int i = 0; i < palettes.getNumberOfParts(); ++i)
			{
			MiPart part 		= palettes.getPart(i);
			String name 		= part.getName();
			String	isSubTree	= (String )part.getResource(Mi_PALETTE_TYPE_NAME);
			boolean expandTree	= false;
//MiDebug.println("name=" + name + ", isSubTree=" + isSubTree);

			if ((paletteType == COMBO_TABLE_PALETTE_TYPE)
				&& (part.getResource(Mi_PALETTE_FIXED_SELECTOR_BUTTON_TYPE_NAME) != null))
				{
				// Then add a push button to entire palette to display this palette
				MiPushButton pb = new MiPushButton(name);
				pb.setName(name);
				pb.setDisplaysFocusBorder(false);
				fixedSelectorButtonNames.add(name);
/*
				if (paletteSelectorFont != null)
					{
					pb.getLabel().setFont(paletteSelectorFont);
MiDebug.println("part.getFont() = " + part.getFont());
					}
*/
				pb.getLabel().setFont(part.getFont());
				if (part.getColor() != Mi_TRANSPARENT_COLOR)
					{
					pb.setBackgroundColor(part.getColor());
					}
				pb.appendActionHandler(this, Mi_ACTIVATED_ACTION);
				comboTablePaletteLayout.insertPart(pb, comboTablePaletteLayoutInsertPointAfterLastFixedSelectorButton++);
				continue;
				}
			if (isSubTree != null)
				{
				if (isSubTree.equalsIgnoreCase(Mi_EXPAND_COMMAND_NAME))
					expandTree = true;

				if (paletteType == TREE_LIST_PALETTE_TYPE)
					{
					treeListPalette.addItem(name, null, part, parent, true);
					setPalettes(part, part);
					if (!expandTree)
						{
						treeListPalette.collapse(part);
						}
					}
				else if (paletteType == COMBO_TABLE_PALETTE_TYPE)
					{
//MiDebug.println("Adding table...");
					MiTable tablePalette = new MiTable();
					tablePalette.getTableHeaderAndFooterManager().setResizableRows(false);
					tablePalette.getTableHeaderAndFooterManager().setResizableColumns(false);
					tablePalette.getTableWideDefaults().getInsetMargins().setMargins(3, 3, 3, 3);
					tablePalette.getTableWideDefaults().setMargins(new MiMargins(0));
					tablePalette.setSectionMargins(new MiMargins(0));
					tablePalette.setMargins(new MiMargins(0));
					tablePalette.setInsetMargins(0);
					//tablePalette.setContentsMargins(new MiMargins(0));
					tablePalette.getContentsBackground().setBackgroundColor(Mi_TRANSPARENT_COLOR);
					//tablePalette.setBackgroundColor(Mi_TRANSPARENT_COLOR);
					tablePalette.setPreferredNumberOfVisibleRows(1);
					tablePalette.setPreferredNumberOfVisibleColumns(3);
					tablePalette.setTotalNumberOfColumns(3);
					tablePalette.setHasFixedTotalNumberOfColumns(true);
					tablePalette.setAlleySpacing(0);
					tablePalette.setStatusHelpMessage(PALETTE_STATUS_HELP_MESSAGE);
					tablePalette.setIsDragAndDropTarget(true);
					tablePalette.setDragAndDropBehavior(dndBehavior);
					tablePalette.getTableWideDefaults().setVerticalJustification(Mi_TOP_JUSTIFIED);
					tablePalette.getTableWideDefaults().setHorizontalJustification(Mi_CENTER_JUSTIFIED);
					tablePalette.getSelectionManager().setBrowsable(false);

					//MiAttributes atts = tablePalette.getAttributes();
					//tablePalette.setAttributes(atts.overrideFrom(part.getAttributes()));

					MiAttributes atts = tablePalette.getContentsBackground().getAttributes();
					tablePalette.getContentsBackground().setAttributes(atts.overrideFrom(part.getAttributes()));



					// If container was a palette too, 
//MiDebug.println("container was a palette? =" + (palettes.getResource(Mi_PALETTE_TYPE_NAME) != null));
					if (palettes.getResource(Mi_PALETTE_TYPE_NAME) != null)
						{
						//then add a push button to container palette to display this palette
						MiPushButton pb = new MiPushButton(name);
						pb.setName(name);
/*
						if (paletteSelectorFont != null)
							{
							pb.getLabel().setFont(paletteSelectorFont);
							}
*/
						pb.setDisplaysFocusBorder(false);
						pb.getLabel().setFont(part.getFont());
						if (part.getBackgroundColor() != Mi_TRANSPARENT_COLOR)
							{
							pb.setBackgroundColor(part.getBackgroundColor());
							}
						pb.appendActionHandler(this, Mi_ACTIVATED_ACTION);
						MiParts pushButtons = (MiParts )comboTableSubPaletteButtons.get(palettes.getName());
						if (pushButtons == null)
							{
							pushButtons = new MiParts();
							comboTableSubPaletteButtons.put(palettes.getName(), pushButtons);
							}
						pushButtons.add(pb);
						
						// and if it has no palette table yet, assign it this one...
//MiDebug.println("container palette name =" + palettes.getName());
//MiDebug.println("container has a table palette? =" + (comboTablePalettes.get(palettes.getName()) != null));
						if ((comboTablePalettes.get(palettes.getName()) == null)
							|| (((MiTable )comboTablePalettes.get(palettes.getName())).getNumberOfItems() == 0))
							{
//MiDebug.println("Assigning table to name=" + palettes.getName());
//MiDebug.println("Assigning table =" + tablePalette);
							comboTablePalettes.put(palettes.getName(), tablePalette);
							}
						}

//MiDebug.println("Assigning table to name=" + name);
//MiDebug.println("Assigning table =" + tablePalette);
					comboTablePalettes.put(name, tablePalette);

					setPalettes(part, part);

					Strings contents = scrolledListPaletteComboBox.getContents();
					contents.add(name);
					scrolledListPaletteComboBox.setContents(contents);
					}
				}
			else
				{
				palettes.removePart(part);
				--i;
				part.setIsDragAndDropSource(true);
				part.setContextCursor(Mi_HAND_CURSOR);

				MiTextField textField = new MiTextField();
				textField.setBorderLook(Mi_NONE);
				textField.setNumDisplayedColumns(-1);
				textField.setBackgroundColor(Mi_TRANSPARENT_COLOR);
				textField.setSelectedBackgroundColor(MiWidget.getToolkit().getTextSensitiveColor());
				textField.setValue(name);
				textField.appendActionHandler(
					new MiAction(this, Mi_LOST_KEYBOARD_FOCUS_ACTION, part));

				textField.setIsEditable(editableLabels);
				textField.setVisible(visibleLabels);
				if (paletteType == TREE_LIST_PALETTE_TYPE)
					{
					treeListPalette.addItem(textField, part, part, palettes, false);
					}
				else if (paletteType == COMBO_TABLE_PALETTE_TYPE)
					{
					int row = numberOfCells/3;
					int column = numberOfCells - row * 3;
					++numberOfCells;
					MiTable table = (MiTable )comboTablePalettes.get(palettes.getName());
					MiTableCell cell = null;
					if (standardSizeOfPaletteGraphics != null)
						{
						MiPart icon = part.deepCopy();
						icon.setSize(standardSizeOfPaletteGraphics);
						icon.setResource(MiDragAndDropManager.Mi_DRAG_AND_DROP_THIS_INSTEAD, part);
						part = icon;
						}
						
						
					if (comboTablePaletteHasLabels)
						{
						MiColumnLayout columnLayout = new MiColumnLayout();
						columnLayout.setElementHSizing(Mi_NONE);
						columnLayout.appendPart(part);
						MiText text = new MiText();
						text.setFontHorizontalJustification(Mi_CENTER_JUSTIFIED);
						text.setVisible(visibleLabels);
						String displayName = (String )part.getResource(Mi_PROPERTY_DISPLAY_NAME);
						displayName = Utility.replaceAll(displayName, " ", " \n");
						text.setValue(displayName);
						if (paletteLabelFont != null)
							{
							text.setFont(paletteLabelFont);
							}
						columnLayout.appendPart(text);
						columnLayout.setIsDragAndDropSource(true);
						columnLayout.setContextCursor(Mi_HAND_CURSOR);
						if (part.getResource(MiDragAndDropManager.Mi_DRAG_AND_DROP_THIS_INSTEAD) != null)
							{
							columnLayout.setResource(MiDragAndDropManager.Mi_DRAG_AND_DROP_THIS_INSTEAD, 
								part.getResource(MiDragAndDropManager.Mi_DRAG_AND_DROP_THIS_INSTEAD));
							}
						else
							{
							columnLayout.setResource(MiDragAndDropManager.Mi_DRAG_AND_DROP_THIS_INSTEAD, part);
							}
						columnLayout.setContextMenu(part.getContextMenu(null));
						cell = new MiTableCell(table, row, column, columnLayout);
						}
					else
						{
						cell = new MiTableCell(table, row, column, part);
						}
					cell.setTag(part);
					table.addCell(cell);
//MiDebug.println("Adding cell: " + cell);
//MiDebug.println("Assigning cell to table =" + table);
					}
				}
			}
		}
	protected	void		openScrolledListPalette(int index)
		{
		MiPart palette = palettes.getPart(index);
		scrolledListPalette.removeAllItems();
		for (int i = 0; i < palette.getNumberOfParts(); ++i)
			{
			MiPart part = palette.getPart(i);
			part.setIsDragAndDropSource(true);
			scrolledListPalette.appendItem(part);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if ((action.hasActionType(Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE))
			|| (action.hasActionType(
			Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
			{
			// ---------------------------------------------------------------
			// Handle drops of things into the palette
			// ---------------------------------------------------------------
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();
			doImport(transferOp);
			// ---------------------------------------------------------------
			// Don't let the default d&d handler do anything.
			// ---------------------------------------------------------------
			return(false);
			}
		if (action.hasActionType(Mi_VALUE_CHANGED_ACTION))
			{
			setComboTablePalette(scrolledListPaletteComboBox.getValue());
			}
		else if (action.hasActionType(Mi_ACTIVATED_ACTION))
			{
			scrolledListPaletteComboBox.setValue(action.getActionSource().getName());
			}
		return(true);
		}
	public		void		setComboTablePalette(String paletteName)
		{
//MiDebug.println("setComboTablePalette: " + paletteName);
//MiDebug.println("setComboTablePalette: " + comboTablePalettes.get(paletteName));
		scrolledBox.setScrollable(((MiTable )comboTablePalettes.get(paletteName)));
		scrolledBox.setSubject(((MiTable )comboTablePalettes.get(paletteName)));
		for (int i = comboTablePaletteLayoutInsertPointAfterLastFixedSelectorButton; 
			i < comboTablePaletteLayout.getNumberOfParts() 
				- 1 - (comboBoxOnTopOfQuickSelectorButtons ? 0 : 1); ++i)
			{
//MiDebug.println("REmvoing selctor button: " + comboTablePaletteLayout.getPart(i));
			comboTablePaletteLayout.removePart(i);
			}

		MiParts pushButtons = (MiParts )comboTableSubPaletteButtons.get(paletteName);
		for (int i = 0; (pushButtons != null) && (i < pushButtons.size()); ++i)
			{
			if (!fixedSelectorButtonNames.contains(pushButtons.get(i).getName()))
				{
				comboTablePaletteLayout.insertPart(
					pushButtons.get(i), comboTablePaletteLayoutInsertPointAfterLastFixedSelectorButton + i);
				}
			}
//MiDebug.dump(this);
		}
					/**------------------------------------------------------
	 				 * Import the data specified by the given data transfer
					 * operation. If the data is textual then a MiText shape
					 * is created and appended to this container. If the data
					 * is a MiPart then it is appended to this container.
					 * @param transfer	the data to import
					 * @overrides		MiContainer#doImport
					 *------------------------------------------------------*/
	public		void		doImport(MiDataTransferOperation transfer)
		{
		if (treeListPalette != null)
			{
			treeListPalette.doImport(transfer);
			setLabelsAreVisible(visibleLabels);
			}
		else if (scrolledListPaletteComboBox.getContents().size() > 0)
			{
			((MiTable )comboTablePalettes.get(scrolledListPaletteComboBox.getValue())).doImport(transfer);
			setLabelsAreVisible(visibleLabels);
			}
		}
	private		int		getSelectedItemIndex()
		{
		int[] row = new int[1];
		int[] column = new int[1];
		if (scrolledListPaletteComboBox.getList().getSelectionManager().getSelectedItem(row, column))
			return(row[0]);
		return(-1);
		}
	}

