
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
public class MiMultiLineSingleButtonDialogBox extends MiNativeDialog implements MiiActionTypes, MiiActionHandler, MiiMessages, MiiToolkit
	{
	public				MiMultiLineSingleButtonDialogBox(MiPart window, String message, int dialogType)
		{
		this(window, message, dialogType, -1);
		}
	public				MiMultiLineSingleButtonDialogBox(
						MiPart window, 
						String message, 
						int dialogType, 
						MiDistance wrapLineWidth)
		{
		this(window, message, dialogType, -1, wrapLineWidth);
		}
	public				MiMultiLineSingleButtonDialogBox(
						MiPart window, 
						String message, 
						int dialogType, 
						int wrapLineLength)
		{
		this(window, message, dialogType, wrapLineLength, -1);
		}
	protected			MiMultiLineSingleButtonDialogBox(
						MiPart window, 
						String message, 
						int dialogType, 
						int wrapLineLength, 
						MiDistance wrapLineWidth)
		{
		super(window, "Warning", true);

		MiPart image = MiSystem.getPropertyPart(MiiToolkit.dialogIconPropertyNames[dialogType]);

		MiColumnLayout dialogLayout = new MiColumnLayout();
		dialogLayout.setElementHSizing(Mi_NONE);
		dialogLayout.setElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(dialogLayout);

		int maxLineLength = 0;
		Strings lines = new Strings(message);
//MiDebug.println("lines.size()=" + lines.size());
		if (lines.size() <= 10)
			{
			for (int i = 0; i < lines.size(); ++i)
				{
				if (maxLineLength < lines.get(i).length())
					{
					maxLineLength = lines.get(i).length();
					}
				}
			}
		MiPushButton close = new MiPushButton(Mi_DIALOG_OK_LABEL);
		close.setInsetMargins(new MiMargins(14, 4, 14, 4));
		close.appendActionHandler(this, Mi_ACTIVATED_ACTION);
//MiDebug.println("maxLineLength=" + maxLineLength);
//MiDebug.println("wrapLineLength=" + wrapLineLength);

		if (wrapLineLength > 0)
			{
			maxLineLength = wrapLineLength;
			}
//MiDebug.println("now maxLineLength=" + maxLineLength);

		if ((lines.size() <= 5) && (maxLineLength < 120))
			{
			MiText text = new MiText(message);
			MiAttributes textAtts = MiToolkit.getAttributes(Mi_TOOLKIT_WINDOW_TEXT_ATTRIBUTES);
			text.setAttributes(text.getAttributes().overrideFrom(textAtts));
			if (wrapLineLength > 0)
				{
				text.setWordWrapEnabled(true);
				text.setNumDisplayedColumns(wrapLineLength);
				}
			else if (wrapLineWidth > 0)
				{
				text.setWordWrapEnabled(true);
				text.setPageWidth(wrapLineWidth);
				}

			MiRowLayout row = new MiRowLayout();
			row.setInsetMargins(new MiMargins(10, 0, 10, 10));
			row.setAlleySpacing(15);
			row.setElementVSizing(Mi_NONE);
			row.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			row.setUniqueElementIndex(1);
			row.setElementHJustification(Mi_LEFT_JUSTIFIED);
			row.appendPart(image);
			row.appendPart(text);
			appendPart(row);
			dialogLayout.setElementSizing(Mi_NONE);
			dialogLayout.setInsetMargins(new MiMargins(0, 15, 0, 0));
			appendPart(close);
			}
		else
			{
			MiVisibleContainer box = new MiVisibleContainer();
			box.setInsetMargins(7);
			appendPart(box);

			MiColumnLayout column = new MiColumnLayout();
			column.setElementHSizing(Mi_NONE);
			box.setLayout(column);

			MiRowLayout row = new MiRowLayout();
			row.setInsetMargins(new MiMargins(10, 5, 10, 10));
			row.setAlleySpacing(15);
			row.setElementVSizing(Mi_NONE);
			row.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			row.setUniqueElementIndex(1);
			row.setElementHJustification(Mi_LEFT_JUSTIFIED);
			row.appendPart(image);
			box.appendPart(row);


			if ((lines.size() < 10) && ((maxLineLength < 120) || (wrapLineWidth > 0)))
				{
				MiText text = new MiText(message);
				MiAttributes textAtts = MiToolkit.getAttributes(Mi_TOOLKIT_WINDOW_TEXT_ATTRIBUTES);
				text.setAttributes(text.getAttributes().overrideFrom(textAtts));
				if (wrapLineLength > 0)
					{
					text.setWordWrapEnabled(true);
					text.setNumDisplayedColumns(wrapLineLength);
					}
				else if (wrapLineWidth > 0)
					{
					text.setWordWrapEnabled(true);
					text.setPageWidth(wrapLineWidth);
					}
				
				row.appendPart(text);
				}
			else
				{
				MiList warningsList = new MiList();
				warningsList.getSelectionManager().setMinimumNumberSelected(0);
				warningsList.getSelectionManager().setMaximumNumberSelected(0);
				warningsList.setMinimumNumberOfVisibleRows(1);
				warningsList.setPreferredNumberOfVisibleRows(10);
				warningsList.setMaximumNumberOfVisibleRows(24);
				warningsList.setNumberOfVisibleCharactersWide(80);
				warningsList.getSortManager().setEnabled(false);
				warningsList.setBackgroundColor(getBackgroundColor());

				MiScrolledBox scrolledBox = new MiScrolledBox(warningsList);
				row.appendPart(scrolledBox);

				warningsList.setContents(lines);
				warningsList.setBackgroundColor("veryDarkWhite");
				}

			box.appendPart(close);
			}

		close.setEnterKeyFocus(true);

		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// Make sure that window device size is set BEFORE it becomes visible
		validateLayout();
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ACTIVATED_ACTION))
			{
			processCommand(Mi_OK_COMMAND_NAME);
			}
		return(true);
		}
	}

