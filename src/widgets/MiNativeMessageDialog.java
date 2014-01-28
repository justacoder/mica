
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
public class MiNativeMessageDialog extends MiNativeDialog
	{
	public				MiNativeMessageDialog(
						MiPart		parent,
						String 		title,
						int 		dialogType,
						String 		msg,
						boolean 	modal,
						String 		btn1NameAndCommand,
						String 		btn2NameAndCommand,
						String 		btn3NameAndCommand,
						String 		btn4NameAndCommand,
						int 		defaultButtonNumber
						)
		{
		super(parent, title, modal);

		MiText text = new MiText(msg);
		MiToolkit.overrideAttributes(text, MiiToolkit.Mi_TOOLKIT_WINDOW_TEXT_ATTRIBUTES);

		new MiMessageDialogBuilder(this, this, dialogType, text, 
			btn1NameAndCommand,
			btn2NameAndCommand,
			btn3NameAndCommand,
			btn4NameAndCommand,
			defaultButtonNumber);
		}
	public				MiNativeMessageDialog(
						MiEditor	parent,
						String 		title,
						int 		dialogType,
						String 		msg,
						boolean 	modal,
						String 		btn1NameAndCommand,
						String 		btn2NameAndCommand,
						String 		btn3NameAndCommand,
						String 		btn4NameAndCommand,
						String 		btn5NameAndCommand,
						int 		defaultButtonNumber
						)
		{
		super(parent, title, modal);

		MiText text = new MiText(msg);
		MiToolkit.overrideAttributes(text, MiiToolkit.Mi_TOOLKIT_WINDOW_TEXT_ATTRIBUTES);

		new MiMessageDialogBuilder(this, this, dialogType, text, 
			btn1NameAndCommand,
			btn2NameAndCommand,
			btn3NameAndCommand,
			btn4NameAndCommand,
			btn5NameAndCommand,
			defaultButtonNumber);
		}
	public				MiNativeMessageDialog(
						MiEditor	parent,
						String 		title,
						int 		dialogType,
						MiPart 		msg,
						boolean 	modal,
						String 		btn1NameAndCommand,
						String 		btn2NameAndCommand,
						String 		btn3NameAndCommand,
						String 		btn4NameAndCommand,
						int 		defaultButtonNumber
						)
		{
		super(parent, title, modal);

		new MiMessageDialogBuilder(this, this, dialogType, msg, 
			btn1NameAndCommand,
			btn2NameAndCommand,
			btn3NameAndCommand,
			btn4NameAndCommand,
			defaultButtonNumber);
		}
	public				MiNativeMessageDialog(
						MiEditor	parent,
						String 		title,
						int 		dialogType,
						MiPart 		msg,
						boolean 	modal,
						String 		btn1NameAndCommand,
						String 		btn2NameAndCommand,
						String 		btn3NameAndCommand,
						String 		btn4NameAndCommand,
						String 		btn5NameAndCommand,
						int 		defaultButtonNumber
						)
		{
		super(parent, title, modal);

		new MiMessageDialogBuilder(this, this, dialogType, msg, 
			btn1NameAndCommand,
			btn2NameAndCommand,
			btn3NameAndCommand,
			btn4NameAndCommand,
			btn5NameAndCommand,
			defaultButtonNumber);
		}

	public				MiNativeMessageDialog(
						MiEditor	parent,
						int 		dialogType,
						String 		msg
						)
		{
		super(parent, "", true);
		new MiMessageDialogBuilder(this, this, dialogType, msg);
		}
	}

