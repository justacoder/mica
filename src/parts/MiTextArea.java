
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
public class MiTextArea extends MiTextField implements MiiActionHandler, MiiActionTypes, MiiCommandNames
	{
	private static final String	DEFAULT_PROMPT	= "Enter Text Here:";

	private		boolean		autoExpandAssociatedShape = true;
	private		boolean		wordWrap = true;
	private		MiPart		associatedShape;
	private		MiAttributes	promptAttributes;
	private		String		prompt;
	private		MiBounds	associatedShapeMinimumBounds;
	private		MiBounds	associatedShapeMaximumBounds;


/*****
	public				MiTextArea()
		{
		this(null, DEFAULT_PROMPT);
		}
	public				MiTextArea(MiPart associatedShape, MiMargins associatedShapeMargins, String prompt)
		{
		this.associatedShape = associatedShape;
		this.associatedShapeMargins = associatedShapeMargins;
		this.associatedShapeMinimumBounds = associatedShapeMinimumBounds;
		if (associatedShapeMinimumBounds == null)
			{
			associatedShapeMinimumBounds = associatedShape.getBounds();
			}
		this.prompt = prompt;
		build();
		}

	protected	void		build()
		{
		if (associatedShape != null)
			{
			updateTextAreaSizeBasedOnAssociatedShape();
			associatedShape.appendActionHandler(this, Mi_GEOMETRY_CHANGE_ACTION);
			appendActionHandler(this, Mi_TEXT_CHANGE_ACTION);
			}
		if (prompt != null)
			{
			setValue(prompt);
			}
		}
	protected	void		updateTextAreaSizeBasedOnAssociatedShape()
		{
		MiBounds b = associatedShape.getInnerBounds();
		if (associatedShapeMargins != null)
			{
			b.subtractMargins(associatedShapeMargins);
			}
		setBounds(b);
		}
	protected	void		updateAssociatedShapeSizeBasedOnTextAreaSize()
		{
		// Approximate a square shape for the text...
		MiBounds b = associatedShape.getInnerBounds();
		if (associatedShapeMargins != null)
			{
			b.subtractMargins(associatedShapeMargins);
			}
		int numCol = getNumDisplayedColumns();

...

		if ((b.isLargerSizeThan(associatedShapeMinimumBounds))
			&& ((associatedShapeMaximumBounds == null) || (b.isSmallerSizeThan(associatedShapeMaximumBounds))))
			{
			associatedShape.setBounds(b);
			}
		}
	// -----------------------------------------------------------------------
	//	Internal functionality 
	// -----------------------------------------------------------------------
	public		boolean		processAction(MiiAction action)
		{
		String text = textField.getValue();
		if (action.getActionSource() == okCancelHelpButtons.getButton(0))
			{
			if (restrictTypedEntryToListedSelections)
				{
				if (!Utility.isEmptyOrNull(text))
					{
					for (int i = 0; i < selections.size(); ++i)
						{
						// FIX: add caseless startsWith
						if (selections.elementAt(i).equals(text))
							{
							return(true);
							}
						}
					}
				return(false);
				}
			return(true);
			}
		if (action.hasActionType(Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			if (restrictTypedEntryToListedSelections)
				{
				if (!Utility.isEmptyOrNull(text))
					{
					boolean okSoFar = false;
					for (int i = 0; i < selections.size(); ++i)
						{
						// FIX: add caseless startsWith
						if (selections.elementAt(i).startsWith(text))
							{
							okSoFar = true;
							if (selections.elementAt(i).equals(text))
								{
								okCancelHelpButtons.getButton(0).setSensitive(true);
								okCancelHelpButtons.getButton(0).requestEnterKeyFocus();
								return(true);
								}
							}
						}
					if (!okSoFar)
						{
						action.veto();
						//okCancelHelpButtons.getButton(0).setSensitive(false);
						return(false);
						}
					}
				okCancelHelpButtons.getButton(0).setSensitive(false);
				}
			return(true);
			}
		if (action.hasActionType(Mi_TEXT_CHANGE_ACTION))
			{
			if ((restrictTypedEntryToNonEmptyText) && (!restrictTypedEntryToListedSelections))
				{
				if (!Utility.isEmptyOrNull(text))
					{
					okCancelHelpButtons.getButton(0).setSensitive(true);
					okCancelHelpButtons.getButton(0).requestEnterKeyFocus();
					}
				else
					{
					okCancelHelpButtons.getButton(0).setSensitive(false);
					}
				}
			if (scrollToTypedEntry)
				{
				if (Utility.isEmptyOrNull(text))
					{
					scrolledList.makeVisible(0, 0);
					}
				else
					{
					int numItems = scrolledList.getNumberOfItems();
					for (int i = 0; i < numItems; ++i)
						{
						if (scrolledList.getStringItem(i).compareTo(text) <= 0)
							scrolledList.makeVisible(i, 0);
						}
					}
				}
			return(true);
			}
		if ((action.getActionSource() == scrolledList) 
			&& (action.hasActionType(Mi_ITEM_SELECTED_ACTION)))
			{
			boolean oldFlag = scrollToTypedEntry;
			// Don't scroll list around when just selecting an item in the list...
			scrollToTypedEntry = false;
			textField.setValue(scrolledList.getValue());
			scrollToTypedEntry = oldFlag;
			}
		if ((action.getActionSource() == scrolledList) 
			&& (action.hasActionType(Mi_ACTIVATED_ACTION)))
			{
			if (dialog != null)
				dialog.setVisible(false);
			dispatchAction(Mi_ACTIVATED_ACTION);
			}
		return(true);
		}
****/
	}

