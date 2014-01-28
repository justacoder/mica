
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

// Add support for multiple filters views (comboBoxes)
// FIX: add support for a filter to filter all children before the category filter
public class MiModelPropertyListViewManager extends MiModelPropertyViewManager implements MiiModelChangeHandler
	{
	public final static MiPropertyViewManagerStyle	Mi_FILTER_AND_LIST_USING_TREE_LIST_STYLE 
							= new MiPropertyViewManagerStyle(
							"filterAndListUsingTreeList");
	public final static MiPropertyViewManagerStyle	Mi_FILTER_AND_LIST_USING_COMBO_BOX_STYLE
							= new MiPropertyViewManagerStyle(
							"filterAndListUsingComboBox");
	public final static MiPropertyViewManagerStyle	Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_COMBO_BOX_STYLE
							= new MiPropertyViewManagerStyle(
							"filterUsingTabbedFolderListUsingComboBox");
	public final static MiPropertyViewManagerStyle	Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_SCROLLED_LIST_STYLE
							= new MiPropertyViewManagerStyle(
							"filterUsingTabbedFolderListUsingScrolledList");
	public final static MiPropertyViewManagerStyle	Mi_FILTER_USING_COMBO_BOX_LIST_USING_COMBO_BOX_STYLE
							= new MiPropertyViewManagerStyle(
							"filterUsingComboBoxListUsingComboBox");
	public final static MiPropertyViewManagerStyle	Mi_FILTER_USING_COMBO_BOX_LIST_USING_SCROLLED_LIST_STYLE
							= new MiPropertyViewManagerStyle(
							"filterUsingComboBoxListUsingScrolledList");
	public final static MiPropertyViewManagerStyle	Mi_LIST_USING_COMBO_BOX_STYLE
							= new MiPropertyViewManagerStyle(
							"listUsingComboBox");
	public final static MiPropertyViewManagerStyle	Mi_LIST_USING_SCROLLED_LIST_STYLE
							= new MiPropertyViewManagerStyle(
							"listUsingScrolledList");


	private		MiPropertyViewManagerStyle style;
	private		boolean			listIsEditable;
	private		MiiModelEntityCategorizingFilter categorizationFilter;
	private		MiModelEntityList[] 	listedEntities;
	private		Strings[] 		listedEntitiesNames;
	private		MiPart			mainPanel;
	private		MiParts			propertyPanels		= new MiParts();
	private		MiList[]		lists;
	private		MiList			list;
	private		MiTreeList		treeList;
	private		MiComboBox		comboBox;
	private		MiWidget		filter;
	private		MiiModelEntity		inspectedModel;
	private		MiTabbedFolder		tabbedFolder;
	private		MiComboBox[]		comboBoxes;
	private		MiPart			inspectedModelPanel;
	private		boolean			allModelsOfSameType;


	public				MiModelPropertyListViewManager()
		{
		this(Mi_FILTER_AND_LIST_USING_TREE_LIST_STYLE);
		}
	public				MiModelPropertyListViewManager(MiPropertyViewManagerStyle style)
		{
		super(Mi_MAKE_PROPERTY_PANEL_FOR_TOP_PROPERTY_CLASS_ONLY_STYLE);
		this.style = style;
		}

	protected	MiPart		buildMainPanel()
		{
		MiPart mainPanel = new MiContainer();
		MiiModelEntity model = getModel();
		propertyPanels = new MiParts();

		if (style == Mi_FILTER_AND_LIST_USING_TREE_LIST_STYLE) 
			{
			treeList = new MiTreeList(24, false);
			treeList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
			MiRowLayout rowLayout = new MiRowLayout();
			rowLayout.setUniqueElementIndex(0);
			rowLayout.setElementVSizing(Mi_EXPAND_TO_FILL);
			rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			mainPanel.setLayout(rowLayout);
			mainPanel.appendPart(new MiScrolledBox(treeList));
			inspectedModelPanel = buildPanels(model);
			mainPanel.appendPart(inspectedModelPanel);
			}
		else if (style == Mi_FILTER_AND_LIST_USING_COMBO_BOX_STYLE)
			{
			comboBox = new MiComboBox();
			treeList = new MiTreeList(24, false);
			comboBox.setList(treeList);
			comboBox.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.setUniqueElementIndex(1);
			columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
			columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			mainPanel.setLayout(columnLayout);
			mainPanel.appendPart(comboBox);
			inspectedModelPanel = buildPanels(model);
			mainPanel.appendPart(inspectedModelPanel);
			}
		else if (style == Mi_FILTER_USING_COMBO_BOX_LIST_USING_COMBO_BOX_STYLE)
			{
			MiComboBox comboBox = new MiComboBox();
			list = (MiList )comboBox.getList();
			filter = new MiComboBox();
			list.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
			filter.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
			MiColumnLayout columnLayout = new MiColumnLayout();
			mainPanel.setLayout(columnLayout);
			mainPanel.appendPart(filter);
			mainPanel.appendPart(comboBox);
			inspectedModelPanel = buildPanels(model);
			mainPanel.appendPart(inspectedModelPanel);
			}
		else if (style == Mi_FILTER_USING_COMBO_BOX_LIST_USING_SCROLLED_LIST_STYLE)
			{
			filter = new MiComboBox();
			list = new MiList(24, false);
			list.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
			filter.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
			MiRowLayout rowLayout = new MiRowLayout();
			mainPanel.setLayout(rowLayout);
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.appendPart(filter);
			columnLayout.appendPart(new MiScrolledBox(list));
			mainPanel.appendPart(columnLayout);
			inspectedModelPanel = buildPanels(model);
			mainPanel.appendPart(inspectedModelPanel);
			}
		else if (style == Mi_LIST_USING_COMBO_BOX_STYLE)
			{
			MiComboBox comboBox = new MiComboBox();
			list = (MiList )comboBox.getList();
			comboBox.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
			MiColumnLayout columnLayout = new MiColumnLayout();
			mainPanel.setLayout(columnLayout);
			mainPanel.appendPart(comboBox);
			inspectedModelPanel = buildPanels(model);
			mainPanel.appendPart(inspectedModelPanel);
			}
		else if (style == Mi_LIST_USING_SCROLLED_LIST_STYLE)
			{
			list = new MiList(24, false);
			list.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
			MiRowLayout rowLayout = new MiRowLayout();
			mainPanel.setLayout(rowLayout);
			mainPanel.appendPart(new MiScrolledBox(list));
			inspectedModelPanel = buildPanels(model);
			mainPanel.appendPart(inspectedModelPanel);
			}
		else if (style == Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_COMBO_BOX_STYLE)
			{
			tabbedFolder = new MiTabbedFolder();
			MiiModelEntityGroup group = categorizationFilter.getCategorizedGroups(model);
			int numGroups = group.sizeOfTree();
			lists = new MiList[numGroups];
			comboBoxes = new MiComboBox[numGroups];
			createTabbedFoldersUsingComboBoxes(tabbedFolder, group);
			MiColumnLayout columnLayout = new MiColumnLayout();
			mainPanel.setLayout(columnLayout);
			mainPanel.appendPart(tabbedFolder);
			}
		else if (style == Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_SCROLLED_LIST_STYLE)
			{
			tabbedFolder = new MiTabbedFolder();
			MiiModelEntityGroup group = categorizationFilter.getCategorizedGroups(model);
			int numGroups = group.sizeOfTree();
			lists = new MiList[numGroups];
			comboBoxes = new MiComboBox[numGroups];
			createTabbedFoldersUsingScrolledLists(tabbedFolder, group);
			MiColumnLayout columnLayout = new MiColumnLayout();
			mainPanel.setLayout(columnLayout);
			mainPanel.appendPart(tabbedFolder);
			}

		return(mainPanel);
		}
	protected	void		createTabbedFoldersUsingComboBoxes(
						MiTabbedFolder tabbedFolder, MiiModelEntityGroup group)
		{
		MiiModelEntityGroup[] groups = group.getSubGroups();
		for (int i = 0; i < groups.length; ++i)
			{
			MiPart folder;
			folder = tabbedFolder.appendFolder(groups[i].getName());
			MiColumnLayout columnLayout = new MiColumnLayout();
			folder.setLayout(columnLayout);
			if (groups[i].mayContainEntities())
				{
				MiComboBox comboBox = new MiComboBox();
				folder.appendPart(comboBox);
				lists[i] = (MiList )comboBox.getList();
				comboBoxes[i] = comboBox;
				if (groups[i].getModelEntities() != null)
					buildPanels(groups[i].getModelEntities().elementAt(0));
				}
			else
				{
				if (groups[i].getSubGroups() != null)
					createTabbedFoldersUsingComboBoxes(tabbedFolder, groups[i]);
				}
			}
		}
	protected	void		createTabbedFoldersUsingScrolledLists(
						MiTabbedFolder tabbedFolder, MiiModelEntityGroup group)
		{
		MiiModelEntityGroup[] groups = group.getSubGroups();
		for (int i = 0; i < groups.length; ++i)
			{
			MiPart folder;
			folder = tabbedFolder.appendFolder(groups[i].getName());
			MiColumnLayout columnLayout = new MiColumnLayout();
			folder.setLayout(columnLayout);
			if (groups[i].mayContainEntities())
				{
				MiList list = new MiList();
				folder.appendPart(new MiScrolledBox(list));
				lists[i] = list;
				if (groups[i].getModelEntities() != null)
					buildPanels(groups[i].getModelEntities().elementAt(0));
				}
			else
				{
				if (groups[i].getSubGroups() != null)
					createTabbedFoldersUsingScrolledLists(tabbedFolder, groups[i]);
				}
			}
		}
	protected	void		populateTreeListFromCategories(
						MiTreeList treeList, 
						MiiModelEntityGroup group, 
						Object parentTag, 
						boolean listEntitiesToo)
		{
		MiiModelEntityGroup[] groups = group.getSubGroups();
		for (int i = 0; i < groups.length; ++i)
			{
			String name = groups[i].getName();
			treeList.addItem(name, null, name, parentTag, groups[i].getSubGroups() != null);

			if ((listEntitiesToo) && (groups[i].getModelEntities() != null))
				{
				MiModelEntityList list = groups[i].getModelEntities();
				for (int j = 0; j < list.size(); ++j)
					{
					if (list.elementAt(j).getType() != Mi_COMMENT_TYPE)
						{
						String entityName = list.elementAt(j).getName();
						treeList.addItem(entityName, null, entityName, name, false);
						}
					}
				buildPanels(list.elementAt(0));
				}
			else
				{
				if (groups[i].getSubGroups() != null)
					{
					populateTreeListFromCategories(
						treeList, groups[i], name, listEntitiesToo);
					}
				}
			}
		}
	protected	void		populateTreeListFromModel(
						MiTreeList treeList, 
						MiiModelEntity model, 
						Object parentTag, 
						boolean listDeep)
		{
		MiModelEntityList list = model.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity child = list.elementAt(i);
			if (child.getType() != Mi_COMMENT_TYPE)
				{
				String name = child.getName();
				treeList.addItem(name, null, name, parentTag,
					listDeep && (child.getModelEntities().size() != 0));
				if (listDeep)
					populateTreeListFromModel(treeList, child, name, listDeep);
				}
			}
		}
	protected	void		buildListsContents()
		{
		if (categorizationFilter != null)
			{
			MiiModelEntityGroup group = categorizationFilter.getCategorizedGroups(getModel());
			int sizeOfTree = group.sizeOfTree();
			listedEntities = new MiModelEntityList[sizeOfTree];
			listedEntitiesNames = new Strings[sizeOfTree];
			populateListContents(listedEntities, listedEntitiesNames, group, 0);
			}
		else
			{
			listedEntities = new MiModelEntityList[1];
			listedEntitiesNames = new Strings[1];
			listedEntities[0] = new MiModelEntityList();
			Strings contents = new Strings();
			populateListContents(listedEntities[0], contents, getModel());
			listedEntitiesNames[0] = contents;
			}
		}
	private		void		populateListContents(
						MiModelEntityList listedEntities, 
						Strings listedEntitiesNames,
						MiiModelEntity model)
		{
		MiModelEntityList list = model.getModelEntities();
		for (int j = 0; j < list.size(); ++j)
			{
			MiiModelEntity entity = list.elementAt(j);
			if (entity.getType() != Mi_COMMENT_TYPE)
				{
				listedEntitiesNames.addElement(entity.getName());
				listedEntities.addElement(entity);
				if (entity.getModelEntities().size() > 0)
					populateListContents(listedEntities, listedEntitiesNames, entity);
				}
			}
		}

	private		int		populateListContents(
						MiModelEntityList[] listedEntities, 
						Strings[] listedEntitiesNames, 
						MiiModelEntityGroup group,
						int listContentsIndex)
		{
		MiiModelEntityGroup[] groups = group.getSubGroups();
		for (int i = 0; i < groups.length; ++i)
			{
			MiModelEntityList list = groups[i].getModelEntities();
			listedEntities[listContentsIndex] = new MiModelEntityList();
			Strings contents = new Strings();
			for (int j = 0; j < list.size(); ++j)
				{
				MiiModelEntity entity = list.elementAt(j);
				if (entity.getType() != Mi_COMMENT_TYPE)
					{
					contents.addElement(entity.getName());
					listedEntities[listContentsIndex].addElement(entity);
					}
				}
			listedEntitiesNames[listContentsIndex] = contents;

			++listContentsIndex;
			if (groups[i].getSubGroups() != null)
				{
				listContentsIndex = populateListContents(
							listedEntities, 
							listedEntitiesNames, 
							groups[i], 
							listContentsIndex);
				}
			}
		return(listContentsIndex);
		}
	protected	void		buildLists()
		{
		buildListsContents();

		if ((style == Mi_FILTER_AND_LIST_USING_TREE_LIST_STYLE) 
			|| (style == Mi_FILTER_AND_LIST_USING_COMBO_BOX_STYLE))
			{
			String selectedItem = treeList.getSelectedItem();
	
			treeList.removeAllItems();
			if (categorizationFilter != null)
				{
				populateTreeListFromCategories(
					treeList, 
					categorizationFilter.getCategorizedGroups(getModel()),
					null, 
					true);
				}
			else
				{
				populateTreeListFromModel(
					treeList,
					getModel(),
					null,
					true);
				}

			int index = -1;
			if ((index = treeList.getIndexOfItem(selectedItem)) != -1)
				treeList.selectItem(index);
			else if (treeList.getNumberOfItems() > 0)
				treeList.selectItem(0);
			}
		else if ((style == Mi_FILTER_USING_COMBO_BOX_LIST_USING_COMBO_BOX_STYLE) 
			|| (style == Mi_FILTER_USING_COMBO_BOX_LIST_USING_SCROLLED_LIST_STYLE))
			{
			String selectedItem = list.getSelectedItem();
			list.removeAllItems();
			String category = filter.getValue();
			int index = list.getIndexOfItem(category);
			Strings contents = listedEntitiesNames[index];
			list.setContents(contents);

			index = -1;
			if ((index = list.getIndexOfItem(selectedItem)) != -1)
				list.selectItem(index);
			else if (list.getNumberOfItems() > 0)
				list.selectItem(0);
			}
		else if((style == Mi_LIST_USING_COMBO_BOX_STYLE) 
			|| (style == Mi_LIST_USING_SCROLLED_LIST_STYLE))
			{
			String selectedItem = list.getSelectedItem();
			list.removeAllItems();
			list.setContents(listedEntitiesNames[0]);

			int index = -1;
			if ((index = list.getIndexOfItem(selectedItem)) != -1)
				list.selectItem(index);
			else if (list.getNumberOfItems() > 0)
				list.selectItem(0);
			}
		else if ((style == Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_COMBO_BOX_STYLE) 
			|| (style == Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_SCROLLED_LIST_STYLE))
			{
			for (int i = 0; i < lists.length; ++i)
				{
				lists[i].removeAllItems();
				lists[i].setContents(listedEntitiesNames[i]);
				lists[i].selectItem(0);
				}
			}
		}
	public		void		setEntityCategorizationFilter(
						MiiModelEntityCategorizingFilter categorizationFilter)
		{
		this.categorizationFilter = categorizationFilter;
		if ((style == Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_COMBO_BOX_STYLE)
			|| (style == Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_SCROLLED_LIST_STYLE))
			{
			setIsBuilt(false);
			}
		if (getModel() != null)
			setModel(getModel());
		}

	public		void		updateModel()
		{
		super.updateModel(inspectedModel);
		}

	protected	MiParts		getPropertyPanels()
		{
		return(propertyPanels);
		}
	public		MiiModelEntity	getInspectedModel()
		{
		return(inspectedModel);
		}
	public		MiParts		getInspectedPanels()
		{
		return(new MiParts(inspectedModelPanel));
		}
	public		void		setInspectedModel(MiiModelEntity model)
		{
		if (inspectedModel != null)
			inspectedModel.removePropertyChangeHandler(this);

		if ((inspectedModel == null)
			|| ((inspectedModel.getPropertyDescriptions() 
				!= model.getPropertyDescriptions())))
			{
			inspectedModel = model;
			MiPart oldInspectedModelPanel = inspectedModelPanel;
			inspectedModelPanel = buildPanels(inspectedModel);
			oldInspectedModelPanel.replaceSelf(inspectedModelPanel);
			}
		inspectedModel = model;
		updateView();
		setHasChanged(false);
		inspectedModel.appendPropertyChangeHandler(
			this, getDisplayedProperties(), MiiModelEntity.Mi_MODEL_CHANGE_COMMIT_PHASE.getMask());
		MiModelChangeEvent event 
			= new MiModelChangeEvent(this, inspectedModel, Mi_MODEL_SELECTED_EVENT_TYPE);
		dispatchModelChangeEvent(event);
		}
	public		boolean		processAction(MiiAction action)
		{
/*
		if (action.isPhase(Mi_REQUEST_ACTION_PHASE))
			{
			if (!verifyLossOfAnyChanges())
				action.veto();
			return(!action.isVetoed());
			}
*/
		if (action.getActionSource() == filter)
			{
			if (!allModelsOfSameType)
				setIsBuilt(false);
			setModel(inspectedModel);
			return(true);
			}
		if (action.getActionSource() == tabbedFolder)
			{
			int index = tabbedFolder.getOpenFolderIndex();
			setInspectedModel(listedEntities[index].elementAt(lists[index].getSelectedItemIndex()));
			return(true);
			}
		if (action.getActionSource() == treeList)
			{
			if (!allModelsOfSameType)
				setIsBuilt(false);
			setInspectedModel(listedEntities[0].elementAt(treeList.getSelectedItemIndex()));
			return(true);
			}
		if (action.getActionSource() == list)
			{
			if (!allModelsOfSameType)
				setIsBuilt(false);
			setInspectedModel(listedEntities[0].elementAt(list.getSelectedItemIndex()));
			return(true);
			}
		for (int i = 0; (lists != null) && (i < lists.length); ++i)
			{
			if (action.getActionSource() == lists[i])
				{
				if (!allModelsOfSameType)
					setIsBuilt(false);
				setInspectedModel(listedEntities[i].elementAt(
					lists[i].getSelectedItemIndex()));
				return(true);
				}
			}
		return(super.processAction(action));
		}
	public		void		processModelChange(MiModelChangeEvent event)
		{
		setModel(getModel());
		}
	}

