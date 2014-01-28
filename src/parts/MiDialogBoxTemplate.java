
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
public class MiDialogBoxTemplate extends MiWidget implements MiiCommandHandler, MiiCommandNames, MiiDisplayNames
	{
	public static final int		Mi_DIALOG_BOX_COMMAND_ACTION
						= MiActionManager.registerAction("DialogBoxCommandAction");

	private		MiOkCancelHelpButtons 	okCancelHelpButtons;
	private		MiContainer		box;
	private		MiWindow		dialogBox;
	private		boolean			hasChanged;


	public				MiDialogBoxTemplate(MiEditor parent, String title, boolean modal)
		{
		this(new MiNativeDialog(parent, title, modal));
		}
	public				MiDialogBoxTemplate(MiWindow dialogBox)
		{
		this.dialogBox = dialogBox;
		if (dialogBox instanceof MiNativeWindow)
			((MiNativeWindow )dialogBox).setDefaultCloseCommand(Mi_HIDE_COMMAND_NAME);
		box = createDialogPanel();
		dialogBox.appendPart(this);
		}

	public		MiContainer	getBox()
		{
		return(box);
		}
	public		MiOkCancelHelpButtons	getOkCancelHelpButtons()
		{
		return(okCancelHelpButtons);
		}
	public		void		setTitle(String title)
		{
		dialogBox.setTitle(title);
		}
	public		void		setVisible(boolean flag)
		{
		if ((flag == isVisible()) && ((dialogBox == null) || (flag == dialogBox.isVisible())))
			return;

		super.setVisible(flag);
		if (dialogBox != null)
			dialogBox.setVisible(flag);
		processCommand(flag ? Mi_SHOW_COMMAND_NAME : Mi_DESTROY_WINDOW_COMMAND_NAME);
		setHasChanged(false);
		}
	protected	MiContainer	createDialogPanel()
		{
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementIndex(0);
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		setLayout(columnLayout);

		MiContainer box = new MiContainer();
		appendPart(box);

		okCancelHelpButtons = new MiOkCancelHelpButtons(
						this,
						Mi_OK_DISPLAY_NAME, Mi_OK_COMMAND_NAME,
						Mi_APPLY_DISPLAY_NAME, Mi_APPLY_COMMAND_NAME,
						Mi_UNDO_DISPLAY_NAME, Mi_UNDO_COMMAND_NAME,
						Mi_DEFAULTS_DISPLAY_NAME, Mi_DEFAULTS_COMMAND_NAME,
						Mi_CANCEL_DISPLAY_NAME, Mi_CANCEL_COMMAND_NAME);

		appendPart(okCancelHelpButtons);

		okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setSensitive(false);
		okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setSensitive(false);
		okCancelHelpButtons.getButton(Mi_UNDO_COMMAND_NAME).setSensitive(false);

		return(box);
		}
	public		void		processCommand(String command)
		{
		if (dispatchActionRequest(Mi_DIALOG_BOX_COMMAND_ACTION, command))
			{
			dispatchAction(Mi_DIALOG_BOX_COMMAND_ACTION, command);
			if (command.equals(Mi_OK_COMMAND_NAME))
				{
				setVisible(false);
				setHasChanged(false);
				}
			else if (command.equals(Mi_APPLY_COMMAND_NAME))
				{
				setHasChanged(false);
				}
			else if (command.equals(Mi_CANCEL_COMMAND_NAME))
				{
				setVisible(false);
				setHasChanged(false);
				}
			else if (command.equals(Mi_UNDO_COMMAND_NAME))
				{
				setHasChanged(false);
				}
			else if (command.equals(Mi_DEFAULTS_COMMAND_NAME))
				{
				//setHasChanged(true);
				}
			else if (command.equals(Mi_SHOW_COMMAND_NAME))
				{
				setVisible(true);
				}
			else if ((command.equals(Mi_HIDE_COMMAND_NAME))
				|| (command.equals(Mi_DESTROY_WINDOW_COMMAND_NAME)))
				{
				setVisible(false);
				}
			}
		}
	public		boolean		getHasChanged()
		{
		return(hasChanged);
		}
	public		void		setHasChanged(boolean flag)
		{
		if (flag)
			{
			if (!hasChanged)
				{
				okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setSensitive(true);
				okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).requestEnterKeyFocus();
				okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setSensitive(true);
				okCancelHelpButtons.getButton(Mi_UNDO_COMMAND_NAME).setSensitive(true);
				}	
			}
		else
			{
			if (hasChanged)
				{
				okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setSensitive(false);
				okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setSensitive(false);
				okCancelHelpButtons.getButton(Mi_UNDO_COMMAND_NAME).setSensitive(false);
				}
			}
		hasChanged = flag;
		}
	}

