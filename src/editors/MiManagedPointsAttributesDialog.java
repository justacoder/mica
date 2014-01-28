
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
public class MiManagedPointsAttributesDialog extends MiNativeWindow implements MiiCommandNames, MiiCommandHandler, MiiActionHandler, MiiActionTypes, MiiDisplayNames
	{
	public static	String			Mi_OK_DEFAULTS_DISPLAY_NAME	= "OK System-Wide Defaults";
	public static	String			Mi_APPLY_DEFAULTS_DISPLAY_NAME	= "Apply System-Wide Defaults";
	public static	String			Mi_BORDER_TITLE_NAME		= "Annotation/Connection Point Properties";

	private		MiParts				targets			= new MiParts();
	private		MiParts				targetsToSet;
	private		MiManagedPointsAttributesPanel	propertiesPanel;
	private		MiOkCancelHelpButtons 		okCancelHelpButtons;


	public				MiManagedPointsAttributesDialog()
		{
		super(Mi_BORDER_TITLE_NAME);

		setDefaultCloseCommand(Mi_CANCEL_COMMAND_NAME);

		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setInsetMargins(6);
		setLayout(columnLayout);

		propertiesPanel = new MiManagedPointsAttributesPanel(this);
		propertiesPanel.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		propertiesPanel.getTabbedFolder().setBorderLook(Mi_NONE);
		propertiesPanel.getTabbedFolder().setBackgroundColor(Mi_TRANSPARENT_COLOR);
		appendPart(propertiesPanel);

		okCancelHelpButtons = new MiOkCancelHelpButtons(
						this,
						Mi_OK_DISPLAY_NAME, Mi_OK_COMMAND_NAME,
						Mi_APPLY_DISPLAY_NAME, Mi_APPLY_COMMAND_NAME,
						Mi_CANCEL_DISPLAY_NAME, Mi_CANCEL_COMMAND_NAME,
						null, null);

		appendPart(okCancelHelpButtons);
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// Make sure that window device size is set BEFORE it becomes visible
		validateLayout();

		setTargetShapes(new MiParts());
		}
	public		void		setVisible(boolean flag)
		{
		super.setVisible(flag);

		if ((flag) && (targetsToSet != null))
			{
			setTargetShapes(targetsToSet);
			targetsToSet = null;
			}
		}
	public		void		setFrontFolder(int folderIndex)
		{
		getAccessLock();
		propertiesPanel.getTabbedFolder().setOpenFolder(folderIndex);
		freeAccessLock();
		}

	public		void		setTargetShape(MiPart target)
		{
		setTargetShapes(new MiParts(target));
		}
	protected	void		applyToTargetShapes()
		{
		for (int i = 0; i < targets.size(); ++i)
			propertiesPanel.applyToTargetShape(targets.elementAt(i));
		}
	protected	void		applyToGlobalDefaults()
		{
		propertiesPanel.applyToGlobalDefaults();
		}
	public		void		setTargetShapes(MiParts targetShapes)
		{
		if (!isVisible())
			{
			targetsToSet = new MiParts(targetShapes);
			return;
			}
		targets.removeAllElements();
		for (int i = 0; i < targetShapes.size(); ++i)
			{
			if (targetShapes.elementAt(i).getAnnotationPointManager() != null)
				targets.addElement(targetShapes.elementAt(i));
			}
		if (targets.size() > 0)
			{
			propertiesPanel.setTargetShape(targets.elementAt(0));
			okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setLabel(Mi_OK_DISPLAY_NAME);
			okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setLabel(Mi_APPLY_DISPLAY_NAME);
			}
		else
			{
			propertiesPanel.setTargetShape(null);
			okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setLabel(Mi_OK_DEFAULTS_DISPLAY_NAME);
			okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setLabel(Mi_APPLY_DEFAULTS_DISPLAY_NAME);
			}
			
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
			if (targets.size() == 0)
				applyToGlobalDefaults();
			else
				applyToTargetShapes();
			setVisible(false);
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else if (cmd.equalsIgnoreCase(Mi_APPLY_COMMAND_NAME))
			{
			okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setSensitive(false);
			okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setSensitive(false);
			if (targets.size() == 0)
				applyToGlobalDefaults();
			else
				applyToTargetShapes();
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



