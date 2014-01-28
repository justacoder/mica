
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


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiComboPlusPropertyPanel extends MiPropertyPanel implements MiiCommandHandler
	{
	private		MiTable		table;
	private		MiComboBox	comboBox;


	public				MiComboPlusPropertyPanel()
		{
		setVisible(false);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);
		comboBox = new MiComboBox();
		comboBox.getList().appendCommandHandler(this, Mi_CHANGE_WHICH_OBJECT_INSPECTED_NAME, Mi_ITEM_SELECTED_ACTION);
		appendPart(comboBox);
		table = MiBasicPropertyPanel.makePropertyPanelTable(this, true);
		}
	public		void		setPossibleInspectedObjects(Strings names)
		{
		super.setPossibleInspectedObjects(names);
		comboBox.getList().removeAllItems();
		comboBox.getList().appendItems(names);
		}
	public		void		setInspectedObject(String name)
		{
		super.setInspectedObject(name);
		comboBox.getList().setValue(name);
		}
	public		int		getInspectedObjectIndex()
		{
		return(((MiList )comboBox.getList()).getSelectedItemIndex());
		}
	public		MiTable		getTable()
		{
		return(table);
		}
	protected	void		generatePanel()
		{
		MiBasicPropertyPanel.makePropertyPanelWidgets(this, table);
		}
	public		void		processCommand(String cmd)
		{
		if (cmd.equals(Mi_CHANGE_WHICH_OBJECT_INSPECTED_NAME))
			{
			if (!dispatchActionRequest(Mi_CHANGE_WHICH_OBJECT_INSPECTED_ACTION))
				{
				comboBox.getList().setValue(getInspectedObject());
				}
			else
				{
				super.setInspectedObject(comboBox.getList().getValue());
				dispatchAction(Mi_CHANGE_WHICH_OBJECT_INSPECTED_ACTION);
				}
			}
		}
	}

