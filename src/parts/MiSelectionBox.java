
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
public class MiSelectionBox extends MiWidget implements MiiActionHandler, MiiActionTypes, MiiCommandNames
	{
	private		boolean		sortAlphabetically			= true;
	private		boolean		scrollToTypedEntry			= true;
	private		boolean		restrictTypedEntryToListedSelections	= true;
	private		boolean		restrictTypedEntryToNonEmptyText	= true;

	private		MiAttributes	promptAttributes;
	private		String		defaultSelection;
	private		int		defaultSelectionIndex			= -1;
	private		Strings		selections;

	private static final String	ENTRY_LABEL_NAME	= "Selection:";

	private		MiPart		promptLabel;
	private		MiList		scrolledList;
	private		MiPart		entryLabel;
	private		MiTextField	textField;
	private		MiNativeDialog	dialog;
	private		MiOkCancelHelpButtons	okCancelHelpButtons;


	public				MiSelectionBox(MiEditor parent, String windowTitle, String prompt, boolean modal, Strings possibleSelections)
		{
		this(parent, windowTitle, prompt, modal);
		setPossibleSelections(possibleSelections);
		}
	public				MiSelectionBox(MiEditor parent, String windowTitle, String prompt, boolean modal)
		{
		buildBox(prompt);
		dialog = new MiNativeDialog(parent, windowTitle, modal);

		okCancelHelpButtons = new MiOkCancelHelpButtons(dialog,
			"OK", Mi_OK_COMMAND_NAME,
			"Cancel", Mi_CANCEL_COMMAND_NAME,
			"Help", Mi_HELP_COMMAND_NAME);
		appendPart(okCancelHelpButtons);
		okCancelHelpButtons.getButton(0).appendActionHandler(this, Mi_ACTIVATED_ACTION);

		dialog.appendPart(this);
		dialog.setDefaultKeyboardFocus(textField);
		}

	public				MiSelectionBox(String prompt)
		{
		buildBox(prompt);
		}

	public 		String		popupAndWaitForClose()
		{
		if (dialog != null)
			{
			initializeSelectionState();
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
	public		void		setPossibleSelections(Strings names)
		{
		selections = names;
		scrolledList.setContents(names);
		}
	public		Strings		getPossibleSelections()
		{
		return(selections);
		}
	public		void		setDefaultSelectionIndex(int index)
		{
		defaultSelectionIndex = index;
		}
	public		int		getDefaultSelectionIndex()
		{
		return(defaultSelectionIndex);
		}
	public		void		setDefaultSelection(String name)
		{
		defaultSelection = name;
		scrolledList.selectItem(name);
		textField.setValue(name);
		}
	public 		String		getDefaultSelection()
		{
		return(defaultSelection);
		}
	public 		String 		getSelection()
		{
		return(textField.getValue());
		}

	// -----------------------------------------------------------------------
	//	Fields 
	// -----------------------------------------------------------------------
	public		void		setDialogTitle(String title)
		{
		dialog.setTitle(title);
		}
	public		String		getDialogTitle()
		{
		return(dialog.getTitle());
		}
	public		void		setPrompt(String prompt)
		{
		if (promptLabel instanceof MiWidget)
			((MiWidget )promptLabel).setValue(prompt);
		}
	public		String		getPrompt()
		{
		if (promptLabel instanceof MiWidget)
			return(((MiWidget )promptLabel).getValue());
		return(null);
		}
	public		void		setPromptLabel(MiPart prompt)
		{
		this.promptLabel.replaceSelf(promptLabel);
		this.promptLabel = promptLabel;
		}
	public		MiPart		getPromptLabel()
		{
		return(promptLabel);
		}
	public		void		setList(MiList list)
		{
		scrolledList.replaceSelf(list);
		scrolledList = list;
		}
	public		MiList		getList()
		{
		return(scrolledList);
		}
	public		void		setEntryLabel(MiPart entryLabel)
		{
		this.entryLabel.replaceSelf(entryLabel);
		this.entryLabel = entryLabel;
		}
	public		MiPart		getEntryLabel()
		{
		return(entryLabel);
		}
	public		void		setEntryField(MiTextField entryField)
		{
		textField.replaceSelf(entryField);
		textField = entryField;
		}
	public		MiPart		getEntryField()
		{
		return(textField);
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
		if (flag)
			initializeSelectionState();
		}
	protected	void		initializeSelectionState()
		{
		if ((restrictTypedEntryToListedSelections)
			|| (restrictTypedEntryToNonEmptyText))
			{
			okCancelHelpButtons.getButton(0).setSensitive(false);
			}
		textField.setValue("");
		if (defaultSelection != null)
			{
			scrolledList.selectItem(defaultSelection);
			}
		else if (defaultSelectionIndex != -1)
			{
			scrolledList.selectItem(defaultSelectionIndex);
			}
		}
	public		void		setAutoSortingAlphabetically(boolean flag)
		{
		sortAlphabetically = flag;
		}
	public		boolean		isAutoSortingAlphabetically()
		{
		return(sortAlphabetically);
		}

	public		void		setAutoScrollingToTypedEntry(boolean flag)
		{
		scrollToTypedEntry = flag;
		}
	public		boolean		isAutoScrollingToTypedEntry()
		{
		return(scrollToTypedEntry);
		}
	public		void		setAutoRestrictingTypedEntryToListedSelections(boolean flag)
		{
		restrictTypedEntryToListedSelections = flag;
		}
	public		boolean		isAutoRestrictingTypedEntryToListedSelections()
		{
		return(restrictTypedEntryToListedSelections);
		}
	public		void		setAutoRestrictingTypedEntryToNonEmptyText(boolean flag)
		{
		restrictTypedEntryToNonEmptyText = flag;
		}
	public		boolean		isAutoRestrictingTypedEntryToNonEmptyText()
		{
		return(restrictTypedEntryToNonEmptyText);
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
	protected	void		buildBox(String prompt)
		{
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		setLayout(layout);
			
		promptLabel = new MiLabel(prompt);
		promptLabel.setFontBold(true);
		appendPart(promptLabel);

		scrolledList = new MiList();
		scrolledList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		scrolledList.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		appendPart(new MiScrolledBox(scrolledList));
		
		entryLabel = new MiLabel(ENTRY_LABEL_NAME);
		appendPart(entryLabel);

		textField = new MiTextField();
		textField.appendActionHandler(this, Mi_TEXT_CHANGE_ACTION 
					| Mi_COMMIT_ACTION_PHASE | Mi_REQUEST_ACTION_PHASE);
		appendPart(textField);
		}
	}

