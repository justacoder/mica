
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
public class MiListPlusPropertyPanel extends MiPropertyPanel implements MiiActionHandler
	{
	private		MiScrolledBox	scrollBox;
	private		MiTable		table;
	private		MiList	list;


	public				MiListPlusPropertyPanel()
		{
		MiRowLayout layout = new MiRowLayout();
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);
		list = new MiList();
		list.setMinimumNumberOfVisibleRows(18);
		list.getSelectionManager().setMinimumNumberSelected(1);
		list.appendActionHandler(this, 
			Mi_ITEM_SELECTED_ACTION | Mi_REQUEST_ACTION_PHASE | Mi_COMMIT_ACTION_PHASE);
		scrollBox = new MiScrolledBox(list);
		appendPart(scrollBox);

		table = MiBasicPropertyPanel.makePropertyPanelTable(this, true);
		}
	public		void		setPossibleInspectedObjects(Strings names)
		{
		super.setPossibleInspectedObjects(names);
		list.removeAllItems();
		list.appendItems(names);
		}
	public		void		setInspectedObject(String name)
		{
		super.setInspectedObject(name);
		list.setValue(name);
		}
	public		int		getInspectedObjectIndex()
		{
		return(list.getSelectedItemIndex());
		}
	public		MiTable		getTable()
		{
		return(table);
		}
	public		MiList		getList()
		{
		return(list);
		}
	protected	void		generatePanel()
		{
		MiBasicPropertyPanel.makePropertyPanelWidgets(this, table);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			if (!dispatchActionRequest(Mi_CHANGE_WHICH_OBJECT_INSPECTED_ACTION))
				{
				action.veto();
				return(false);
				}
			}
		else if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			super.setInspectedObject(list.getValue());
			dispatchAction(Mi_CHANGE_WHICH_OBJECT_INSPECTED_ACTION);
			}
		return(true);
		}
	}

