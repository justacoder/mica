
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
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiHierarchicalInspector extends MiVisibleContainer implements MiiActionHandler, MiiActionTypes
	{
	private		MiTreeList		treeList;
	private		MiTabbedFolder		tabbedFolder;
	private		MiBasicPropertyPanel	attributePanel;
	private		MiBasicPropertyPanel	eventHandlerPanel;
	private		MiBasicPropertyPanel	actionPanel;
	private		MiVisibleContainer	iconField;
	private		MiTextField		nameField;
	private		MiLabel			classField;
	private		MiVisibleContainer	currentPartFields;



	public				MiHierarchicalInspector(MiEditor editor)
		{
		MiColumnLayout columnLayout = new MiColumnLayout();
		setLayout(columnLayout);

		currentPartFields = new MiVisibleContainer();
		currentPartFields.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		appendPart(currentPartFields);
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementVSizing(Mi_NONE);
		currentPartFields.setLayout(rowLayout);

		iconField = new MiVisibleContainer();
		iconField.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		nameField = new MiTextField();
		nameField.setNumDisplayedColumns(30);
		classField = new MiLabel();
		classField.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		currentPartFields.appendPart(new MiText("Appearance:"));
		currentPartFields.appendPart(iconField);
		currentPartFields.appendPart(new MiText("   Name:"));
		currentPartFields.appendPart(nameField);
		currentPartFields.appendPart(new MiText("   Class:"));
		currentPartFields.appendPart(classField);
		

		rowLayout = new MiRowLayout();
		appendPart(rowLayout);

		treeList = new MiTreeList(28, false);
		treeList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		MiScrolledBox scrolledBox = new MiScrolledBox(treeList);
		rowLayout.appendPart(scrolledBox);

		tabbedFolder = new MiTabbedFolder();
		rowLayout.appendPart(tabbedFolder);

		tabbedFolder.appendFolder("Attributes");
		tabbedFolder.appendFolder("Event Handlers");
		tabbedFolder.appendFolder("Actions");

		attributePanel = new MiBasicPropertyPanel(true);
		tabbedFolder.getFolderContents(0).appendPart(attributePanel);
		eventHandlerPanel = new MiBasicPropertyPanel(true);
		tabbedFolder.getFolderContents(1).appendPart(eventHandlerPanel);
		actionPanel = new MiBasicPropertyPanel(true);
		tabbedFolder.getFolderContents(2).appendPart(actionPanel);
		
		String[] names = MiiNames.attributeNames;
		MiPropertyWidgets	attPanelWidgets = new MiPropertyWidgets();

		// NAME
		MiPropertyWidget pWidget = new MiPropertyWidget("Name", ""); 
		pWidget.setNumDisplayedColumns(30);
		attPanelWidgets.appendPropertyWidget(pWidget);
		// ATTRIBUTES
		for (int i = 0; i < names.length; ++i)
			{
			pWidget = new MiPropertyWidget(names[i], ""); 
			pWidget.setNumDisplayedColumns(30);
			attPanelWidgets.appendPropertyWidget(pWidget);
			}
		attributePanel.setPropertyWidgets(attPanelWidgets);
		attributePanel.open();

		setHierarchicalParent(editor);
		}
	public		void		setHierarchicalParent(MiPart parent)
		{
		for (int i = 0; i < parent.getNumberOfParts(); ++i)
			{
			MiPart part = parent.getPart(i);
			boolean hasChildren = parent.getNumberOfParts() > 0 ? true : false;
			String name = part.getName();
			if (Utility.isEmptyOrNull(name))
				name = part.toString();
			treeList.addItem(name, null, part, parent, hasChildren);
			setHierarchicalParent(part);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			MiPart part = (MiPart )treeList.getSelectedObjectItem();
			setTarget(part);
			}
		return(true);
		}
	public		void		setTargetShape(MiPart part)
		{
		MiPropertyWidgets attPanelWidgets = new MiPropertyWidgets();
		String[] names = MiiNames.attributeNames;
		attributePanel.setPropertyValue("Name", part.getName());
		for (int i = 0; i < names.length; ++i)
			{
			String value = part.getAttributeValue(names[i]); 
			attributePanel.setPropertyValue(names[i], value);
			}
		iconField.removeAllParts(); 
		iconField.appendPart(new MiReference(part)); 
		iconField.setSize(new MiSize(20,20));
		nameField.setValue(part.getName());
		classField.setValue(part.getClass().getName());
		}
	}

