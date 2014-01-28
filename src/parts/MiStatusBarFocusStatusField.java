
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
public class MiStatusBarFocusStatusField extends MiStatusBar implements MiiActionHandler, MiiActionTypes
	{
	private		MiEditor	statusEditor;
	private		MiWidget	status;
	private		String		currentStatus;
	private		MiAttributes	origStatusFieldAttributes;
	private		MiWidgetAttributes	origStatusFieldWidgetAttributes;
	private 	int		STATUS_FIELD_INDEX			= 0;
	public  	String		STATUS_MESSAGE_AREA_TOOL_HINT		= "Status Information";



	public				MiStatusBarFocusStatusField(MiEditor statusEditor, String spec, int fieldIndex)
		{
		super(spec);
		STATUS_FIELD_INDEX = fieldIndex;
		this.statusEditor = statusEditor;
		setup();
		}

	public				MiStatusBarFocusStatusField(MiEditor statusEditor)
		{
		super(".50");
		this.statusEditor = statusEditor;
		setup();
		}

	protected	void		setup()
		{
		setBorderLook(Mi_NONE);
		setInsetMargins(0);
		status = getField(STATUS_FIELD_INDEX);
		status.setToolHintMessage(STATUS_MESSAGE_AREA_TOOL_HINT);

		MiWindow window = statusEditor.getContainingWindow();
		if (window.getStatusBarFocusManager() == null)
			window.setStatusBarFocusManager(new MiStatusBarFocusManager(window));
		window.appendActionHandler(this, Mi_STATUS_BAR_FOCUS_CHANGED_ACTION);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_STATUS_BAR_FOCUS_CHANGED_ACTION))
			{
			MiWindow window = action.getActionSource().getContainingWindow();
			MiiHelpInfo helpInfo = window.getStatusBarFocusManager().getStatusBarFocusMessage();
			if ((helpInfo != null) && (helpInfo.getAttributes() != null))
				{
				if (origStatusFieldAttributes == null)
					{
					origStatusFieldAttributes = status.getAttributes();
					origStatusFieldWidgetAttributes = status.getWidgetAttributes();
					}
				status.setAttributes(status.getAttributes().overrideFrom(helpInfo.getAttributes()));
				}
			else if ((helpInfo == null) || (helpInfo.getAttributes() == null))
				{
				if (origStatusFieldAttributes != null)
					{
					status.setAttributes(origStatusFieldAttributes);
					status.setWidgetAttributes(origStatusFieldWidgetAttributes);
					origStatusFieldAttributes = null;
					origStatusFieldWidgetAttributes = null;
					}
				}
			if (helpInfo != null)
				{
				setStatus(helpInfo.getMessage());
				}
			else
				{
				setStatus(null);
				}
			}
		return(true);
		}

	public		void		setStatus(String message)
		{
if ((message != null) && (message.indexOf('\n') != -1))
{
MiDebug.println("Warning: status bar message has a line feed... not supported at this time");
// See MiLabel.setValue
}
		status.setValue(message);
		if ((currentStatus == null) && (message != null))
			{
			dispatchAction(Mi_SELECTED_ACTION);
			}
		else if ((currentStatus != null) && (message == null))
			{
			dispatchAction(Mi_DESELECTED_ACTION);
			}
		currentStatus = message;
		}
	}

