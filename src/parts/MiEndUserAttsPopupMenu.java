
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
public class MiEndUserAttsPopupMenu extends MiEditorMenu implements MiiCommandNames, MiiMessages
	{
	private	static final	String	Mi_POST_WINDOW_ATTRIBUTES_COMMAND_NAME 	= "postWindowAtts";
	private	static final	String	Mi_POST_WIDGET_ATTRIBUTES_COMMAND_NAME 	= "postWidgetAtts";
	private	static final	String	Mi_POST_PART_ATTRIBUTES_COMMAND_NAME 	= "postPartAtts";
	private	static final	String	Mi_POST_ADVANCED_ATTRIBUTES_COMMAND_NAME= "postAdvancedAtts";

	private		MiPart			subjectPart;
	private		MiPart			subjectWidget;
	private		MiPart			subjectWindow;
	private		MiiCommandManager manager;
	private		MiNativeWindow		advancedPropertiesDialog;
	private		MiShapeAttributesDialog	propertiesDialog;
	private		MiHierarchicalInspector	advancedPropertiesPanel;



	public				MiEndUserAttsPopupMenu(MiiCommandHandler handler, MiiCommandManager manager)
		{
		super("Examine/Change Appearance", manager);

		setCommandHandler(handler);

		this.manager = manager;

		appendMenuItem(		"Window properties...",	handler, 
								Mi_POST_WINDOW_ATTRIBUTES_COMMAND_NAME);
		appendMenuItem(		"Widget properties...",	handler, 
								Mi_POST_WIDGET_ATTRIBUTES_COMMAND_NAME);
		appendMenuItem(		"Part properties...", 	handler,
								Mi_POST_PART_ATTRIBUTES_COMMAND_NAME);
		appendSeparator();
		appendMenuItem(		"Advanced properties...",handler, 
								Mi_POST_ADVANCED_ATTRIBUTES_COMMAND_NAME);
/*
		setHelpMessages(Mi_CUT_COMMAND_NAME,
				Mi_CUT_STATUS_HELP_MSG,
				Mi_NO_CUT_STATUS_HELP_MSG,
				Mi_CUT_BALLOON_HELP_MSG);

*/
		}
	public		void		popup(MiPart assocTriggerObj, MiPoint pt)
		{
		// Get part under mouse.... desensitize options if not available...
		subjectPart = assocTriggerObj;
		if (subjectPart instanceof MiWindow)
			{
			manager.setCommandSensitivity(Mi_POST_WINDOW_ATTRIBUTES_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_POST_WIDGET_ATTRIBUTES_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_POST_PART_ATTRIBUTES_COMMAND_NAME, false);
			subjectWindow = subjectPart;
			}
		else if (subjectPart instanceof MiWidget)
			{
			manager.setCommandSensitivity(Mi_POST_WINDOW_ATTRIBUTES_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_POST_WIDGET_ATTRIBUTES_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_POST_PART_ATTRIBUTES_COMMAND_NAME, false);
			subjectWidget = subjectPart;
			subjectWindow = subjectPart.getContainingWindow();
			}
		else
			{
			manager.setCommandSensitivity(Mi_POST_WINDOW_ATTRIBUTES_COMMAND_NAME, true);
			manager.setCommandSensitivity(Mi_POST_WIDGET_ATTRIBUTES_COMMAND_NAME, false);
			manager.setCommandSensitivity(Mi_POST_PART_ATTRIBUTES_COMMAND_NAME, true);
			subjectWindow = subjectPart.getContainingWindow();
			MiPart part = subjectPart;
			while ((subjectWidget = part.getContainer(0)) != null)
				{
				if (subjectWidget instanceof MiWidget)
					{
					manager.setCommandSensitivity(Mi_POST_WIDGET_ATTRIBUTES_COMMAND_NAME, true);
					break;
					}
				}
			}
		
		super.popup(assocTriggerObj, pt);
		}
	public		void		processCommand(String cmd)
		{
		if (propertiesDialog == null)
			{
			propertiesDialog = new MiShapeAttributesDialog();

			advancedPropertiesDialog = new MiNativeWindow("Properties");
			advancedPropertiesPanel = new MiHierarchicalInspector(getEditor());
			advancedPropertiesDialog.appendPart(advancedPropertiesPanel);
			advancedPropertiesDialog.setViewportSizeLayout(
				new MiEditorViewportSizeIsOneToOneLayout(true));
			// Make sure that window device size is set BEFORE it becomes visible
			advancedPropertiesDialog.validateLayout();
			}

		if (cmd.equalsIgnoreCase(Mi_POST_WINDOW_ATTRIBUTES_COMMAND_NAME))
			{
			propertiesDialog.setTargetShape(subjectWindow);
			propertiesDialog.setVisible(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_POST_WIDGET_ATTRIBUTES_COMMAND_NAME))
			{
			propertiesDialog.setTargetShape(subjectWidget);
			propertiesDialog.setVisible(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_POST_PART_ATTRIBUTES_COMMAND_NAME))
			{
			propertiesDialog.setTargetShape(subjectPart);
			propertiesDialog.setVisible(true);
			}
		else if (cmd.equalsIgnoreCase(Mi_POST_ADVANCED_ATTRIBUTES_COMMAND_NAME))
			{
			advancedPropertiesPanel.setTargetShape(subjectPart);
			advancedPropertiesDialog.setVisible(true);
			}
		}

	public		String		getPreferences()
		{
		return(null);
		}
	public		void		setPreferences(String preferences)
		{
		}
	}
