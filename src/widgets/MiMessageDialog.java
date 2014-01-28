
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
public class MiMessageDialog extends MiDialog
	{
	public				MiMessageDialog(
						MiEditor	parent,
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

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);

		MiText text = new MiText(msg);
		MiToolkit.overrideAttributes(text, MiiToolkit.Mi_TOOLKIT_WINDOW_TEXT_ATTRIBUTES);

		new MiMessageDialogBuilder(this, this, dialogType, text, 
			btn1NameAndCommand,
			btn2NameAndCommand,
			btn3NameAndCommand,
			btn4NameAndCommand,
			defaultButtonNumber);

		MiBounds b = getDeviceBounds();
		b.setCenter(parent.getWorldBounds().getCenter());
		setDeviceBounds(b);
		}

	public				MiMessageDialog(
						MiEditor	parent,
						int 		dialogType,
						String 		msg
						)
		{
		super(parent, "", true);

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);

		new MiMessageDialogBuilder(this, this, dialogType, msg);

		MiBounds b = getDeviceBounds();
		b.setCenter(parent.getWorldBounds().getCenter());
		setDeviceBounds(b);
		}
	}

