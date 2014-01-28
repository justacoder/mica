
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
public class MiShapeAttributesDialog extends MiNativeWindow implements MiiShapeAttributesInspectorPanel, MiiCommandNames, MiiCommandHandler, MiiActionHandler, MiiActionTypes, MiiDisplayNames, MiiShapeAttributesPanel
	{
	public static	String			Mi_OK_DEFAULTS_DISPLAY_NAME	= "OK Defaults";
	public static	String			Mi_APPLY_DEFAULTS_DISPLAY_NAME	= "Apply Defaults";
	public static	String			BORDER_TITLE_NAME		= "Shape Properties";

	private		MiParts			targetsToSet;
	private		MiAttributes		attributesToDisplay;
	private		MiParts			targets			= new MiParts();
	private		MiAttributes		displayedAttributes	= new MiAttributes();
	private		MiShapeAttributesPanel	propertiesPanel;
	private		MiOkCancelHelpButtons 	okCancelHelpButtons;


	public				MiShapeAttributesDialog()
		{
		super(BORDER_TITLE_NAME);

		setDefaultCloseCommand(Mi_CANCEL_COMMAND_NAME);

		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setInsetMargins(6);
		setLayout(columnLayout);

		propertiesPanel = new MiShapeAttributesPanel();
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
		}
	public		void		setVisible(boolean flag)
		{
		super.setVisible(flag);

		if ((flag) && (targetsToSet != null))
			{
			setTargetShapes(targetsToSet);
			targetsToSet = null;
			}
		if ((flag) && (attributesToDisplay != null))
			{
			setDisplayedAttributes(attributesToDisplay);
			attributesToDisplay = null;
			}
		}

	public		void		setFrontFolder(int folderIndex)
		{
		getAccessLock();
		propertiesPanel.getTabbedFolder().setOpenFolder(folderIndex);
		freeAccessLock();
		}

	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		displayedAttributes = attributes;
		if (!isVisible())
			attributesToDisplay = displayedAttributes;
		else
			propertiesPanel.setDisplayedAttributes(attributes);
		}
	public		MiAttributes	getDisplayedAttributes()
		{
		return(displayedAttributes);
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
	public		void		setTargetShapes(MiParts targets)
		{
		if (!isVisible())
			{
			targetsToSet = new MiParts(targets);
			return;
			}
		this.targets.removeAllElements();
		this.targets.append(targets);
		if (targets.size() > 0)
			{
			propertiesPanel.setTargetShape(targets.elementAt(0));
			okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setLabel(Mi_OK_DISPLAY_NAME);
			okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setLabel(Mi_APPLY_DISPLAY_NAME);
			}
		else
			{
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
			applyToTargetShapes();
			displayedAttributes = propertiesPanel.getDisplayedAttributes(displayedAttributes);
			setVisible(false);
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else if (cmd.equalsIgnoreCase(Mi_APPLY_COMMAND_NAME))
			{
			okCancelHelpButtons.getButton(Mi_OK_COMMAND_NAME).setSensitive(false);
			okCancelHelpButtons.getButton(Mi_APPLY_COMMAND_NAME).setSensitive(false);
			applyToTargetShapes();
			displayedAttributes = propertiesPanel.getDisplayedAttributes(displayedAttributes);
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
	public		MiAttributes	getDisplayedAttributes(MiAttributes displayedAttributes)
		{
		displayedAttributes = propertiesPanel.getDisplayedAttributes(displayedAttributes);
		return(displayedAttributes);
		}
	}



