
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiEditorStatusBar extends MiWidget implements MiiActionHandler, MiiActionTypes
	{
	private		MiPart		overlayStatusField;
	private		MiWhatsSelectedStatusField	whatsSelectedStatusField;
	private		MiStatusBar	stateField;
	private		MiRowLayout 	rowLayout;


	public				MiEditorStatusBar()
		{
		setup();
		}
	public				MiEditorStatusBar(MiEditor editor)
		{
		this(editor, editor, editor, editor);
		}
	public				MiEditorStatusBar(
						MiEditor statusEditor, 
						MiEditor selectionEditor, 
						MiEditor scaledEditor, 
						MiEditor mouseLocationEditor)
		{
		this(statusEditor, selectionEditor, scaledEditor, mouseLocationEditor, true);
		}
	public				MiEditorStatusBar(
						MiEditor statusEditor, 
						MiEditor selectionEditor, 
						MiEditor scaledEditor,
						MiEditor mouseLocationEditor,
						boolean overlayStatusBarFocusField)
		{
		setup();
		setStateField(50);
		if (overlayStatusBarFocusField)
			{
			setOverlayStatusField(new MiStatusBarFocusStatusField(statusEditor));
			}
		whatsSelectedStatusField = new MiWhatsSelectedStatusField(selectionEditor);
		appendPart(whatsSelectedStatusField);
		appendPart(new MiSystemResourcesStatusField(statusEditor.getAnimationManager()));
		appendPart(new MiMagnificationStatusField(scaledEditor));
		appendPart(new MiMouseXYPositionStatusField(mouseLocationEditor));
		}

	public		void		setStateField(int characterLength)
		{
		stateField = new MiBasicStatusField("." + characterLength);
		appendPart(stateField);
		}
	public		MiStatusBar	getStateField()
		{
		return(stateField);
		}
	public		MiWhatsSelectedStatusField	getWhatsSelectedField()
		{
		return(whatsSelectedStatusField);
		}
	protected	void		setup()
		{
		setBorderLook(Mi_RAISED_BORDER_LOOK);
		rowLayout = new MiRowLayout();
		setLayout(rowLayout);
		rowLayout.setAlleyHSpacing(2);
		rowLayout.setInsetMargins(0);
		setInsetMargins(2);
		setVisibleContainerAutomaticLayoutEnabled(false);
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(0);
		}

	public		void		setState(String msg)
		{
		stateField.setValue(msg);
		}
	public		void		setTargetEditor(MiEditor editor)
		{
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if (getPart(i) instanceof MiStatusBar)
				((MiStatusBar )getPart(i)).setTargetEditor(editor);
			}
		}

	/**
	*  Hide/show other fields when overlay field is shown/hidden
	*  Could expand this functionality to include a number
	*  of overlays (lists of fields)
	* @param 		probably a MiStatusBarFocusStatusField
	*/
	public		void		setOverlayStatusField(MiPart field)
		{
		if (overlayStatusField != null)
			{
			removePart(overlayStatusField);
			overlayStatusField.removeActionHandlers(this);
			}
		overlayStatusField = field;
		if (field == null)
			return;

		overlayStatusField = field;
		insertPart(overlayStatusField, 0);
		overlayStatusField.appendActionHandler(this, Mi_SELECTED_ACTION);
		overlayStatusField.appendActionHandler(this, Mi_DESELECTED_ACTION);
		overlayStatusField.setVisible(false);
		// Allow the 'state field' to expand to fill editor width
		rowLayout.setUniqueElementIndex(1);
		}

	public		boolean		processAction(MiiAction action)
		{
		if ((action.hasActionType(Mi_SELECTED_ACTION))
			&& (action.getActionSource() == overlayStatusField))
			{
			setVisiblityOfUnderlayFields(false);
			}
		else if ((action.hasActionType(Mi_DESELECTED_ACTION))
			&& (action.getActionSource() == overlayStatusField))
			{
			setVisiblityOfUnderlayFields(true);
			}
		return(true);
		}

	protected	void		setVisiblityOfUnderlayFields(boolean flag)
		{
		setInvalidLayoutNotificationsEnabled(false);
		if (!flag)
			{
			MiSize size = new MiSize();
			getPreferredSize(size);
			setPreferredSize(size);
			}
		else
			{
			setPreferredSize(null);
			}
		int numFields = getNumberOfParts();
		for (int i = 0; i < numFields; ++i)
			{
			if (getPart(i) != overlayStatusField)
				getPart(i).setVisible(flag);
			}
		overlayStatusField.setVisible(!flag);
		if (flag && (getIndexOfPart(overlayStatusField) == 0))
			{
			rowLayout.setUniqueElementIndex(1);
			}
		else
			{
			rowLayout.setUniqueElementIndex(0);
			}
		layoutParts();
		validateParts();
		setInvalidLayoutNotificationsEnabled(true);
		}
	}

