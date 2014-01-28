
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
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiWhatsSelectedStatusField extends MiStatusBar implements MiiActionHandler, MiiActionTypes
	{
	private		MiWidget	field;
	private		MiEditor	selectionEditor;
	private 	int		WHAT_SELECTED_FIELD_INDEX	= 0;
	private 	String		NO_NAME_NAME			= "One shape";

	public  	String		SELECTED_OBJECTS_AREA_TOOL_HINT	= "Selected Shapes Information";
	public		String		NO_OBJECTS_SELECTED_MSG		= "No Shapes Selected";
	public		String		ONE_OBJECT_SELECTED_MSG		= "\"%s\" is Selected";
	public		String		MULTIPLE_OBJECTS_SELECTED_MSG	= "Multiple Shapes(%s) Selected";
	public		String		ALL_OBJECTS_SELECTED_MSG	= "All Shapes(%s) Selected";


	public				MiWhatsSelectedStatusField(MiEditor selectionEditor, String spec, int fieldIndex)
		{
		super(spec);
		WHAT_SELECTED_FIELD_INDEX = fieldIndex;
		setup(selectionEditor);
		}

	public				MiWhatsSelectedStatusField(MiEditor selectionEditor)
		{
		super(".30");
		setup(selectionEditor);
		}

	public		MiWidget	getField()
		{
		return(field);
		}
	public		void		setTargetEditor(MiEditor editor)
		{
		if (selectionEditor != null)
			{
			selectionEditor.removeActionHandlers(this);
			}
		selectionEditor = editor;

		selectionEditor.appendActionHandler(new MiAction(this, 
							Mi_ITEM_REMOVED_ACTION,
							Mi_ITEM_SELECTED_ACTION,
							Mi_ITEM_DESELECTED_ACTION));
		selectionEditor.appendActionHandler(this, Mi_ALL_ITEMS_SELECTED_ACTION);
		selectionEditor.appendActionHandler(this, Mi_ALL_ITEMS_DESELECTED_ACTION);
		selectionEditor.appendActionHandler(new MiAction(this,
							Mi_NO_ITEMS_SELECTED_ACTION, 
							Mi_ONE_ITEM_SELECTED_ACTION, 
							Mi_MANY_ITEMS_SELECTED_ACTION));

		updateNumSelectedStatus();
		}
	protected	void		setup(MiEditor selectionEditor)
		{
		setBorderLook(Mi_NONE);
		setInsetMargins(0);
		field = getField(WHAT_SELECTED_FIELD_INDEX);
		field.setToolHintMessage(SELECTED_OBJECTS_AREA_TOOL_HINT);

		setTargetEditor(selectionEditor);
		}
	public		boolean		processAction(MiiAction action)
		{
		if ((action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			|| (action.hasActionType(Mi_ITEM_DESELECTED_ACTION))
			|| (action.hasActionType(Mi_ITEM_REMOVED_ACTION))
			|| (action.hasActionType(Mi_ONE_ITEM_SELECTED_ACTION))
			|| (action.hasActionType(Mi_MANY_ITEMS_SELECTED_ACTION)))
			{
			updateNumSelectedStatus();
			}
		else if (action.hasActionType(Mi_ALL_ITEMS_SELECTED_ACTION))
			{
			field.setValue(Utility.sprintf(ALL_OBJECTS_SELECTED_MSG, 
				String.valueOf(selectionEditor.getCurrentLayer().getNumberOfParts())));
			}
		else if ((action.hasActionType(Mi_ALL_ITEMS_DESELECTED_ACTION))
			|| (action.hasActionType(Mi_NO_ITEMS_SELECTED_ACTION)))
			{
			field.setValue(NO_OBJECTS_SELECTED_MSG);
			}
		return(true);
		}

	protected	void		updateNumSelectedStatus()
		{
		MiParts selectedParts = selectionEditor.getSelectedParts(new MiParts());
		if (selectedParts.size() == 0)
			{
			field.setValue(NO_OBJECTS_SELECTED_MSG);
			}
		else if (selectedParts.size() == 1)
			{
			MiPart selectedObj = selectedParts.elementAt(0);
			field.setValue(Utility.sprintf(ONE_OBJECT_SELECTED_MSG, 
				selectedObj.getName() == null ? NO_NAME_NAME : selectedObj.getName()));
			}
		else if (selectedParts.size() == selectionEditor.getCurrentLayer().getNumberOfParts())
			{
			field.setValue(Utility.sprintf(ALL_OBJECTS_SELECTED_MSG, 
				String.valueOf(selectedParts.size())));
			}
		else
			{
			field.setValue(Utility.sprintf(MULTIPLE_OBJECTS_SELECTED_MSG, 
				String.valueOf(selectedParts.size())));
			}
		}


	}


