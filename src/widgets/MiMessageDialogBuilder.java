
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
public class MiMessageDialogBuilder implements MiiCommandNames, MiiTypes, MiiToolkit
	{
	public static 	String		DEFAULT_WARNING_DIALOG_TITLE	= "Warning";
	public static 	String		DEFAULT_HELP_DIALOG_TITLE	= "Help";
	public static 	String		DEFAULT_INFO_DIALOG_TITLE	= "Information";
	public static 	String		DEFAULT_WORKING_DIALOG_TITLE	= "Working";
	public static 	String		DEFAULT_QUERY_DIALOG_TITLE	= "Question";
	public static 	String		DEFAULT_ERROR_DIALOG_TITLE	= "Error";

	protected	MiWindow	dialog;
	protected	MiiCommandHandler	method;
	protected	int		dialogType;
	protected	MiPart		msg;
	protected	int		defaultButtonNumber;

	private		String		btn1NameAndCommand = Mi_OK_COMMAND_NAME;
	private		String		btn2NameAndCommand = Mi_APPLY_COMMAND_NAME;
	private		String		btn3NameAndCommand = Mi_CANCEL_COMMAND_NAME;
	private		String		btn4NameAndCommand = Mi_HELP_COMMAND_NAME;
	private		String		btn5NameAndCommand = null;

	protected 			MiMessageDialogBuilder(
						MiWindow	dialog,
						MiiCommandHandler	method,
						int 		dialogType,
						MiPart 		msg,
						String 		btn1NameAndCommand,
						String 		btn2NameAndCommand,
						String 		btn3NameAndCommand,
						String 		btn4NameAndCommand,
						int 		defaultButtonNumber
						)
		{
		this(
			dialog,
			method,
			dialogType,
			msg,
			btn1NameAndCommand,
			btn2NameAndCommand,
			btn3NameAndCommand,
			btn4NameAndCommand,
			null,
			defaultButtonNumber);
		}

	protected 			MiMessageDialogBuilder(
						MiWindow	dialog,
						MiiCommandHandler	method,
						int 		dialogType,
						MiPart 		msg,
						String 		btn1NameAndCommand,
						String 		btn2NameAndCommand,
						String 		btn3NameAndCommand,
						String 		btn4NameAndCommand,
						String 		btn5NameAndCommand,
						int 		defaultButtonNumber
						)
		{
		this.dialog 		= dialog;
		this.method 		= method;
		this.dialogType 	= dialogType;
		this.msg	 	= msg;
		this.btn1NameAndCommand	= btn1NameAndCommand;
		this.btn2NameAndCommand	= btn2NameAndCommand;
		this.btn3NameAndCommand	= btn3NameAndCommand;
		this.btn4NameAndCommand	= btn4NameAndCommand;
		this.btn5NameAndCommand	= btn5NameAndCommand;
		this.defaultButtonNumber= defaultButtonNumber;
		buildDialog();
		}

	protected 			MiMessageDialogBuilder(
						MiWindow	dialog,
						MiiCommandHandler	method,
						int 		dialogType,
						String 		msg)
		{
		this.dialog 		= dialog;
		this.method 		= method;
		this.dialogType 	= dialogType;
		this.msg	 	= new MiText(msg);

		switch (dialogType)
			{
			case Mi_WARNING_DIALOG_TYPE	:
				btn2NameAndCommand = null;
				dialog.setTitle(DEFAULT_WARNING_DIALOG_TITLE);
				break;
			case Mi_HELP_DIALOG_TYPE	:
				btn2NameAndCommand = null;
				btn3NameAndCommand = null;
				btn4NameAndCommand = null;
				dialog.setTitle(DEFAULT_HELP_DIALOG_TITLE);
				break;
			case Mi_INFO_DIALOG_TYPE	:
				btn2NameAndCommand = null;
				btn3NameAndCommand = null;
				dialog.setTitle(DEFAULT_INFO_DIALOG_TITLE);
				break;
			case Mi_WORKING_DIALOG_TYPE	:
				btn2NameAndCommand = null;
				btn3NameAndCommand = null;
				dialog.setTitle(DEFAULT_WORKING_DIALOG_TITLE);
				break;
			case Mi_QUERY_DIALOG_TYPE	:
				btn2NameAndCommand = null;
				dialog.setTitle(DEFAULT_QUERY_DIALOG_TITLE);
				break;
			case Mi_ERROR_DIALOG_TYPE	:
				btn2NameAndCommand = null;
				btn3NameAndCommand = null;
				dialog.setTitle(DEFAULT_ERROR_DIALOG_TITLE);
				break;
			case Mi_ORDINARY_DIALOG_TYPE	:
				btn2NameAndCommand = null;
				btn3NameAndCommand = null;
				dialog.setTitle("");
				break;
			default:
				throw new IllegalArgumentException(this + ": Unknow dialog type: " + dialogType);
			}
		buildDialog();
		}
	private		void		buildDialog()
		{
		MiPart image = MiSystem.getPropertyPart(MiiToolkit.dialogIconPropertyNames[dialogType]);

		MiAttributes bgAtts = MiToolkit.getAttributes(Mi_TOOLKIT_WINDOW_ATTRIBUTES);
		bgAtts = bgAtts.overrideFrom(
				MiSystem.getPropertyAttributes(dialogAttributesPropertyNames[dialogType]));

		MiAttributes textAtts = MiToolkit.getAttributes(Mi_TOOLKIT_WINDOW_TEXT_ATTRIBUTES);
		textAtts = textAtts.overrideFrom(
				MiSystem.getPropertyAttributes(dialogTextAttributesPropertyNames[dialogType]));
		msg.setAttributes(msg.getAttributes().overrideFrom(textAtts));

		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		dialog.setLayout(columnLayout);

		MiVisibleContainer box = new MiVisibleContainer();
		box.setInsetMargins(7);
		box.setAttributes(bgAtts);
		dialog.appendPart(box);

		MiColumnLayout column = new MiColumnLayout();
		box.setLayout(column);

		MiRowLayout row = new MiRowLayout();
		row.setInsetMargins(15);
		row.setAlleySpacing(15);
		row.setElementVSizing(Mi_NONE);
		row.setElementHJustification(Mi_LEFT_JUSTIFIED);
		if (image != null)
			row.appendPart(image);
		row.appendPart(msg);
		box.appendPart(row);

		MiOkCancelHelpButtons okCancelHelpBtns = new MiOkCancelHelpButtons(
								method,
								btn1NameAndCommand, btn1NameAndCommand,
								btn2NameAndCommand, btn2NameAndCommand,
								btn3NameAndCommand, btn3NameAndCommand,
								btn4NameAndCommand, btn4NameAndCommand,
								btn5NameAndCommand, btn5NameAndCommand);

		box.appendPart(okCancelHelpBtns);
		for (int i = 0; i < 5; ++i)
			{
			MiPart button = okCancelHelpBtns.getButton(i);
			if (button != null)
				button.setAttributes(button.getAttributes().overrideFrom(bgAtts));
			}
		dialog.requestKeyboardFocus(okCancelHelpBtns.getButton(defaultButtonNumber));
		dialog.setDefaultEnterKeyFocus(okCancelHelpBtns.getButton(defaultButtonNumber));
		if (bgAtts.getBackgroundColor() != Mi_TRANSPARENT_COLOR)
			{
			Color darker = bgAtts.getBackgroundColor().darker();
			for (int i = 0; i < 5; ++i)
				{
				if (okCancelHelpBtns.getButton(i) != null)
					okCancelHelpBtns.getButton(i).setSelectedBackgroundColor(darker);
				}
			}

		dialog.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// Make sure that window device size is set BEFORE it becomes visible
		dialog.validateLayout();
		}
	}

