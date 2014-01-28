
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiFontChooser extends MiWidget implements MiiActionHandler, MiiActionTypes, MiiCommandNames
	{
	private		MiFont		defaultFont;

	private		MiPart		titleLabel;
	private		MiList	typeFaceList;
	private		MiList	pointSizeList;
	private		MiToggleButton	boldToggleBtn;
	private		MiLabel		boldLabel;
	private		MiToggleButton	italicsToggleBtn;
	private		MiLabel		italicsLabel;
	private		MiLabel		displayField;
	private		MiTextField	typeFaceField;
	private		MiTextField	pointSizeField;
	private		MiNativeDialog	dialog;
	private		MiOkCancelHelpButtons	okCancelHelpButtons;



	public				MiFontChooser(MiEditor parent, String title, boolean modal)
		{
		this(parent, title, modal, MiFontManager.getFontList());
		}
	public				MiFontChooser(MiEditor parent, String title, boolean modal, String[] fonts)
		{
		buildBox(title);

		setBorderLook(Mi_RAISED_BORDER_LOOK);

		dialog = new MiNativeDialog(parent, title, modal);

		okCancelHelpButtons = new MiOkCancelHelpButtons(dialog,
			"OK", Mi_OK_COMMAND_NAME,
			"Cancel", Mi_CANCEL_COMMAND_NAME,
			"Help", Mi_HELP_COMMAND_NAME);
		appendPart(okCancelHelpButtons);

		setup(fonts);
		dialog.appendPart(this);
		}
	public				MiFontChooser(String title)
		{
		buildBox(title);
		setup(MiFontManager.getFontList());
		}


	protected	void		setup(String[] fonts)
		{
		typeFaceList.appendItems(new Strings(fonts));
		typeFaceList.selectItem(fonts[0]);

		for (int i = 6; i < 16; ++i)
			pointSizeList.appendItem(Utility.toString(i));

		pointSizeList.appendItem("18");
		pointSizeList.appendItem("20");
		pointSizeList.appendItem("24");
		pointSizeList.appendItem("28");
		pointSizeList.appendItem("32");
		pointSizeList.appendItem("36");

		pointSizeList.selectItem("12");
		}

	public 		MiFont		popupAndWaitForClose()
		{
		if (dialog != null)
			{
			String button = dialog.popupAndWaitForClose();
			if ((button != null) && (!button.equals(Mi_OK_COMMAND_NAME)))
				{
				return(null);
				}
			}
		return(getSelection());
		}
	// -----------------------------------------------------------------------
	//	Data set 
	// -----------------------------------------------------------------------
	public		void		setDefaultSelection(MiFont font)
		{
		defaultFont = font;
		if (font != null)
			{
			typeFaceList.selectItem(font.getName());
			pointSizeList.selectItem(Utility.toString(font.getSize()));
			boldToggleBtn.select(font.isBold());
			italicsToggleBtn.select(font.isItalic());
			displayField.setFont(font);
			}
		}
	public 		MiFont		getDefaultSelection()
		{
		return(defaultFont);
		}
	public 		MiFont 		getSelection()
		{
		int size = Utility.toInteger(pointSizeList.getSelectedItem());
		int style = MiFont.PLAIN;
		if (boldToggleBtn.isSelected())
			style |= MiFont.BOLD;
		if (italicsToggleBtn.isSelected())
			style |= MiFont.ITALIC;
		String typeFace = typeFaceList.getSelectedItem();
		if ((typeFace == null) || (size < 1))
			return(null);
			
		MiFont font = MiFontManager.findFont(typeFace, style, size);
		return(font);
		}

	// -----------------------------------------------------------------------
	//	Fields 
	// -----------------------------------------------------------------------
	public		MiPart		getTitleLabel()
		{
		return(titleLabel);
		}
	public		void		setTitleLabel(MiPart title)
		{
		this.titleLabel.replaceSelf(titleLabel);
		this.titleLabel = titleLabel;
		}
	public		MiNativeDialog	getDialog()
		{
		return(dialog);
		}
	// -----------------------------------------------------------------------
	//	Control 
	// -----------------------------------------------------------------------
	public		void		setVisible(boolean flag)
		{
		super.setVisible(flag);
		if (dialog != null)
			dialog.setVisible(flag);
		}
	// -----------------------------------------------------------------------
	//	Internal functionality 
	// -----------------------------------------------------------------------
	public		boolean		processAction(MiiAction action)
		{
		// FIX: getLabel here only cause attribute inheritance not fully implemented yet..
		MiFont font = getSelection();
		if (font != null)
			displayField.getLabel().setFont(font);
		return(true);
		}
	protected	void		buildBox(String title)
		{
		MiColumnLayout layout = new MiColumnLayout();
		setLayout(layout);
			
		titleLabel = new MiLabel(title);
		appendPart(titleLabel);

		MiRowLayout row = new MiRowLayout();
		appendPart(row);
		
		MiColumnLayout column = new MiColumnLayout();
		row.appendPart(column);

		// --------------------------
		//	Type Face Column
		// --------------------------
		typeFaceField = new MiTextField();
		column.appendPart(typeFaceField);
		
		typeFaceList = new MiList();
		//typeFaceList.setSortAlgorithm(MiList.Mi_ALPHABETICAL_SORT);
		typeFaceList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		column.appendPart(new MiScrolledBox(typeFaceList));
		
		// --------------------------
		//	Point Size Column
		// --------------------------
		column = new MiColumnLayout();
		row.appendPart(column);

		pointSizeField = new MiTextField();
		pointSizeField.setNumDisplayedColumns(2);
		column.appendPart(pointSizeField);
		
		pointSizeList = new MiList();
		pointSizeList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		column.appendPart(new MiScrolledBox(pointSizeList));
		
		// --------------------------
		//	Bold/Italics Face Column
		// --------------------------
		MiGridLayout grid = new MiGridLayout();
		grid.setNumberOfColumns(2);
		row.appendPart(grid);

		boldToggleBtn = new MiToggleButton();
		boldToggleBtn.appendActionHandler(this, Mi_SELECTED_ACTION, Mi_DESELECTED_ACTION);
		boldLabel = new MiLabel("Bold");
		boldLabel.getLabel().setFontBold(true);
		grid.appendPart(boldLabel);
		grid.appendPart(boldToggleBtn);

		italicsToggleBtn = new MiToggleButton();
		italicsToggleBtn.appendActionHandler(this, Mi_SELECTED_ACTION, Mi_DESELECTED_ACTION);
		italicsLabel = new MiLabel("Italic");
		italicsLabel.getLabel().setFontItalic(true);
		grid.appendPart(italicsLabel);
		grid.appendPart(italicsToggleBtn);

		MiTable table = new MiTable();
		table.setMaximumNumberOfVisibleRows(1);
		table.setMaximumNumberOfVisibleColumns(1);
		displayField = new MiLabel("abcdefghijklmnopqrstuvwxyz\nABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789\n!@#$%^&*()_+|-=\\~;:[]{}\"\'\"<>/?");
		table.appendItem(displayField);
		appendPart(new MiScrolledBox(table));
		}
	}

