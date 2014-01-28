
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
public class MiGeneralAttributesDialog extends MiNativeDialog implements MiiCommandNames, MiiCommandHandler, MiiActionHandler, MiiActionTypes, MiiDisplayNames
	{
	public static	String			Mi_OK_DEFAULTS_DISPLAY_NAME	= "OK System-Wide Defaults";
	public static	String			Mi_APPLY_DEFAULTS_DISPLAY_NAME	= "Apply System-Wide Defaults";
	public static	String			Mi_BORDER_TITLE_NAME		= "Attributes";

	private		MiGeneralAttributesPanel	propertiesPanel;
	private		MiOkCancelHelpButtons 		okCancelHelpButtons;
	private		MiAttributes			attributesToSet;


	public				MiGeneralAttributesDialog(MiNativeWindow parent, boolean modal)
		{
		//super(parent, Mi_BORDER_TITLE_NAME, new MiBounds(0,0,500,500), modal);
		super(parent, Mi_BORDER_TITLE_NAME, modal);
		setupMiGeneralAttributesDialog();
		}
/***
	public				MiGeneralAttributesDialog()
		{
		super(Mi_BORDER_TITLE_NAME);
		setupMiGeneralAttributesDialog();
		}
****/
	protected	void		setupMiGeneralAttributesDialog()
		{
		setDefaultCloseCommand(Mi_CANCEL_COMMAND_NAME);

		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setInsetMargins(6);
		setLayout(columnLayout);

		propertiesPanel = new MiGeneralAttributesPanel(0);
		propertiesPanel.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		//propertiesPanel.getTabbedFolder().setBorderLook(Mi_NONE);
		//propertiesPanel.getTabbedFolder().setBackgroundColor(Mi_TRANSPARENT_COLOR);
		appendPart(propertiesPanel);

		okCancelHelpButtons = new MiOkCancelHelpButtons(
						this,
						Mi_OK_DISPLAY_NAME, Mi_OK_COMMAND_NAME,
						Mi_APPLY_DISPLAY_NAME, Mi_APPLY_COMMAND_NAME,
						Mi_CANCEL_DISPLAY_NAME, Mi_CANCEL_COMMAND_NAME,
						null, null);

		appendPart(okCancelHelpButtons);

		if (isModal())
			okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setVisible(false);

		// Do not allow the window to be resized larger...
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(false));

		// Make sure that window device size is set BEFORE it becomes visible
		validateLayout();
		}
	public		void		setVisible(boolean flag)
		{
		super.setVisible(flag);

		if ((flag) && (attributesToSet != null))
			{
			setDisplayAttributes(attributesToSet);
			attributesToSet = null;
			}
		}

	public		void		setWhichAttributesToDisplay(int mask)
		{
		propertiesPanel.setWhichAttributesToDisplay(mask);
		}
	public		void		setDisplaySample(MiPart sample)
		{
		propertiesPanel.setDisplaySample(sample);
		}
	public		MiPart		getDisplaySample()
		{
		return(propertiesPanel.getDisplaySample());
		}

	public		MiAttributes	getDisplayAttributes()
		{
		if (attributesToSet != null)
			return(attributesToSet);
		return(propertiesPanel.getDisplayAttributes());
		}
	public		MiAttributes	getDisplayAttributes(MiAttributes atts)
		{
		// FIX: this should just apply attributesToSet to incoming atts based on attribute mask
		if (attributesToSet != null)
			return(attributesToSet);
		return(propertiesPanel.getDisplayAttributes(atts));
		}
	public		void		setDisplayAttributes(MiAttributes atts)
		{
		if (!isVisible())
			{
			attributesToSet = atts;
			return;
			}
		propertiesPanel.setDisplayAttributes(atts);
		okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setSensitive(false);
		okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setSensitive(false);
		}
	public		boolean		processAction(MiiAction action)
		{
		okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setSensitive(true);
		okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).requestEnterKeyFocus();
		okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setSensitive(true);
		return(true);
		}
	public		void		processCommand(String cmd)
		{
		if (cmd.equalsIgnoreCase(Mi_OK_COMMAND_NAME))
			{
			setVisible(false);
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else if (cmd.equalsIgnoreCase(Mi_APPLY_COMMAND_NAME))
			{
			okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setSensitive(false);
			okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setSensitive(false);
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else if (cmd.equalsIgnoreCase(Mi_CANCEL_COMMAND_NAME))
			{
			setVisible(false);
			}
		else
			{
			super.processCommand(cmd);
			}
		}
	}



