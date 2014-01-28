
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
import java.util.Hashtable;

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelTreeListViewManager extends MiModelEntity implements MiiViewManager, MiiModelChangeHandler, MiiPropertyChangeHandler
	{
	private		MiPart		view;
	private		MiTreeList	treeList;
	private		String		propertyToDisplayAsName;
	private		String		propertyName;
	private		Strings		propertyValues;
	private		MiiModelEntity	document;
	private		boolean		readOnly;
	private		Hashtable	tagForTypes;
	private		Hashtable	displayNamesForTypes;
	private		Strings[]	typeLevels;
	private		String		propertyNameOfTypes;
	private		Strings		excludedModelTypes = new Strings();
	private		int		id;
	private		boolean		createAsExpandedTree = true;
	private		boolean		automaticallyUpdatedWhenModelChangesEnabled = true;
	private		boolean		uniqueChildNamesOnly;
	private		String		excludedPropertyName;
	private		Strings		excludedPropertyValues = new Strings();


	public				MiModelTreeListViewManager()
		{
		this(false);
		}

	public				MiModelTreeListViewManager(boolean readOnly)
		{
		this.readOnly = readOnly;
		}

	public		void		setTypeLevelNames(
						String propertyNameToUseToGetType, 
						Strings[] typesForLevels,
						Hashtable displayNamesForTypes)
		{
		propertyNameOfTypes = propertyNameToUseToGetType;
		typeLevels = typesForLevels;
		tagForTypes = new Hashtable();
		this.displayNamesForTypes = displayNamesForTypes;
		}

	// If set this to false during many updates, set back to true and then re-call setModel with the model to refresh this list
	public		void		setAutomaticallyUpdatedWhenModelChangesEnabled(boolean flag)
		{
		automaticallyUpdatedWhenModelChangesEnabled = flag;
		}
	public		boolean		getAutomaticallyUpdatedWhenModelChangesEnabled()
		{
		return(automaticallyUpdatedWhenModelChangesEnabled);
		}

	public		void		setEntitiesToDisplay(String propertyName, Strings propertyValues)
		{
		this.propertyName = propertyName;
		this.propertyValues = propertyValues;
		}
	public		void		setDisplayingUniqueChildNamesOnly(boolean flag)
		{
		uniqueChildNamesOnly = flag;
		}
	public		boolean		isDisplayingUniqueChildNamesOnly()
		{
		return(uniqueChildNamesOnly);
		}
	public		void		setPropertyToDisplayAsName(String propertyToDisplayAsName)
		{
		this.propertyToDisplayAsName = propertyToDisplayAsName;
		}
	public		void		setExcludedPropertyValues(String propertyName, Strings propertyValues)
		{
		this.excludedPropertyName = propertyName;
		this.excludedPropertyValues = new Strings(propertyValues);
		}
	public		void		setExcludedModelTypes(Strings excludedModelTypes)
		{
		this.excludedModelTypes = new Strings(excludedModelTypes);
		}
	public		Strings		getExcludedModelTypes()
		{
		return(new Strings(excludedModelTypes));
		}
	public		void		setCreateAsExpandedTree(boolean createAsExpandedTree)
		{
		this.createAsExpandedTree = createAsExpandedTree;
		}
	public		boolean		getCreateAsExpandedTree()
		{
		return(createAsExpandedTree);
		}
					/**------------------------------------------------------
		 			 * Sets the view container. This container will be loaded
					 * with graphics by the #setModel command and will be
					 * converted to a MiiModelDocument by the #getModel 
					 * command.
					 * @param view		the view container
					 * @see 		#getView
					 *------------------------------------------------------*/
	public		void		setView(MiPart view)
		{
		this.view = view;
		if (view instanceof MiTreeList)
			treeList = (MiTreeList )view;
		else
			treeList = new MiTreeList();
		}
					/**------------------------------------------------------
		 			 * Gets the view container. 
					 *------------------------------------------------------*/
	public		MiPart		getView()
		{
		return(view);
		}
					/**------------------------------------------------------
		 			 * Loads the target view container with graphics representing
					 * the data in the given document.
					 * @param document	the document
					 * @see 		#setView
					 *------------------------------------------------------*/
	public		void		setModel(MiiModelEntity document)
		{
		if (this.document != null)
			{
			this.document.removeModelChangeHandler(this);
			this.document.removePropertyChangeHandler(this);
			MiModelEntityTreeList list = new MiModelEntityTreeList(this.document);
			MiiModelEntity entity;
			while ((entity = list.toNext()) != null)
				{
				entity.removeModelChangeHandler(this);
				entity.removePropertyChangeHandler(this);
				}
			}

		this.document = document;
		document.appendModelChangeHandler(this, Mi_MODEL_CHANGE_COMMIT_PHASE_MASK);
		treeList.removeAllItems();
		tagForTypes.clear();

//MiDebug.printStackTrace("set model=" + MiModelEntity.dump(document));
		addChild(null, document, true);

//////		populateTreeListFromModel(treeList, document, null, true);

		if (!createAsExpandedTree)
			{
			for (int i = 1; i < typeLevels.length; ++i)
				{
				Strings types = typeLevels[i];
				for (int j = 0; j < types.size(); ++j)
					{
					String type = types.get(j);
					Object tag = tagForTypes.get(type);
					if (tag != null)
						{
						treeList.collapse(tag);
						}
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Converts the graphics found in the target view container to 
					 * a MiiModelDocument.
					 * @return 		the document
					 * @see 		#setView
					 *------------------------------------------------------*/
	public		MiiModelEntity	getModel()
		{
		if (readOnly)
			return(document);
		return(document);
		}

	protected	void		populateTreeListFromModel(
						MiTreeList treeList, 
						MiiModelEntity model, 
						Object parentTag, 
						boolean listDeep)
		{
//System.out.println(">>>>>>populateTreeListFromModel");
//MiDebug.printStackTrace();
		MiModelEntityList list = model.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity child = list.elementAt(i);
			addChild(parentTag, child, listDeep);
			}
		}
	protected	void		addChild(Object parentTag, MiiModelEntity child, boolean listDeep)
		{
//MiDebug.println("excludedModelTypes=" + excludedModelTypes);
//MiDebug.println("child.getType().getName()=" + child.getType().getName());
		if ((child.getType() != Mi_COMMENT_TYPE)
			&& (!excludedModelTypes.contains(child.getType().getName()))
			&& ((propertyName == null)
			|| (propertyValues.contains(child.getPropertyValue(propertyName))))
			&& ((excludedPropertyName == null)
			|| (!excludedPropertyValues.contains(child.getPropertyValue(excludedPropertyName)))) )
			{
//MiDebug.println("to display!" + child);
			String name = child.getName();
			if ((name == null) || (name.length() == 0))
				{
				name = "<Anonymous>";
				}

			if (propertyToDisplayAsName != null)
				{
				name = child.getPropertyValue(propertyToDisplayAsName);
				}
//System.out.println(">>>>>>child to ADD to parent: " + parentTag);
//System.out.println(">>>>>>child to ADD: " + name);
//MiDebug.println(">>>>>>child to ADD: " + child);

			boolean isLeaf = true;
			if (listDeep && (child.getModelEntities().size() != 0))
				{
//MiDebug.println("propertyName=" + propertyName);
				MiModelEntityTreeList t_list = new MiModelEntityTreeList(child);
//MiDebug.println("propertyValues=" + propertyValues);
				for (int j = 0; j < t_list.size(); ++j)
					{
					MiiModelEntity e = t_list.elementAt(j);
					if ((e.getType() != Mi_COMMENT_TYPE)
						&& (!excludedModelTypes.contains(e.getType().getName()))
						&& ((propertyName == null)
						|| (propertyValues.contains(e.getPropertyValue(propertyName))))
						&& ((excludedPropertyName == null)
						|| (!excludedPropertyValues.contains(e.getPropertyValue(excludedPropertyName)))) )
						{
						isLeaf = false;
						break;
						}
					}
				}
			String type = null;
			if ((propertyNameOfTypes == null) && (child.getType() != null))
				{
				type = child.getType().getName();
				}
			else
				{
				type = child.getPropertyValue(propertyNameOfTypes);
				}
//MiDebug.println("propertyNameOfTypes=" + propertyNameOfTypes);
//MiDebug.println("type=" + type);
			if (type != null)
				{
				Object tag = getParentTagForType(treeList, type, parentTag);

				boolean addItem = true;
				if (uniqueChildNamesOnly)
					{
					for (int j = 0; j < treeList.getNumberOfChildren(tag); ++j)
						{
						if (name.equals(treeList.getStringWithTag(treeList.getChild(tag, j))))
							{
							addItem = false;
							break;
							}
						}
					}
				if (addItem)
					{
					treeList.addItem(name, null, child, tag, !isLeaf);

					// Child may have been copied from another child that had change handlers...
					// So remove them if so...
					child.removePropertyChangeHandler(this);
					child.appendPropertyChangeHandler(this, 
						new Strings(propertyToDisplayAsName == null ? "name" : propertyToDisplayAsName), 
						Mi_MODEL_CHANGE_COMMIT_PHASE_MASK);

					child.removeModelChangeHandler(this);
					child.appendModelChangeHandler(this, 
						Mi_MODEL_CHANGE_COMMIT_PHASE_MASK);
					}

				}
			if (!isLeaf)
				populateTreeListFromModel(treeList, child, child, listDeep);
			}
		else
			{
			if (listDeep)
				populateTreeListFromModel(treeList, child, parentTag, listDeep);
			}
		}
	protected	Object		getParentTagForType(
						MiTreeList treeList, String type, Object prospectiveParentTag)
		{
//MiDebug.println("getParentTagForType: " + type);
		if (typeLevels == null)
			return(prospectiveParentTag);

		for (int i = 0; i < typeLevels.length; ++i)
			{
//MiDebug.println("getParentTagForType: " + i);
			if ((typeLevels[i].contains(type)) || (typeLevels[i].contains("*")))
				{
				Object tag = tagForTypes.get(type);
//MiDebug.println("getParentTagForType: tag= " + tag);
				if (tag == null)
					{
					tag = new Integer(id++); //?

					String typeName = type;
					if (displayNamesForTypes != null)
						typeName = (String )displayNamesForTypes.get(type);
//MiDebug.println("getParentTagForType: displayNamesForTypes= " + displayNamesForTypes);
//MiDebug.println("getParentTagForType: typeName= " + typeName);
					if (typeName == null)
						typeName = type;

					treeList.addItem(typeName, null, tag, prospectiveParentTag, true);

					// Enhance appearance of categories
					MiPart label = treeList.getLabelWithTag(tag);
					label.setFontBold(true);
					label.setFontPointSize(14);
					label.setSelectable(false);
					// Save tag
					tagForTypes.put(type, tag);
					}
				// null out levels below
				for (int j = i + 1; j < typeLevels.length; ++j)
					{
					Strings typesForLevel = typeLevels[j];
					for (int k = 0; k < typesForLevel.size(); ++k)
						tagForTypes.remove(typesForLevel.elementAt(k));
					}
//MiDebug.println("getParentTagForType: return= " + tag);
				return(tag);
				}
			}
//MiDebug.println("getParentTagForType: return hmmmmm= " + prospectiveParentTag);
		return(prospectiveParentTag);
		}
	public		void		processModelChange(MiModelChangeEvent event)
		{
		if (!automaticallyUpdatedWhenModelChangesEnabled)
			{
			return;
			}
		if (event.hasEventType(Mi_MODEL_ADDED_EVENT_TYPE))
			{
			MiiModelEntity parentTag = event.getParent();
			MiiModelEntity child = event.getChild();
//MiDebug.println(">>>>>>child was ADDED to parent: " + parentTag);
//MiDebug.println(">>>>>>child was ADDED TAG: " + child);
			addChild(parentTag, child, true);
			}
		else if (event.hasEventType(Mi_MODEL_REMOVED_EVENT_TYPE))
			{
			if (treeList.getItemWithTag(event.getChild()) != null)
				{
//MiDebug.println(">>>>>>child was REMOVED TAG: " + event.getChild());

				treeList.removeItemWithTag(event.getChild());
				event.getChild().removePropertyChangeHandler(this);
				event.getChild().removeModelChangeHandler(this);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Process the change of a name of one of the displayed
					 * models.
					 * @param event		the change event
					 *------------------------------------------------------*/
	public		void		processPropertyChange(MiPropertyChange event)
		{
		if (!automaticallyUpdatedWhenModelChangesEnabled)
			{
			return;
			}
//MiDebug.println(">>>>>>event.getSource TAG = " + event.getSource());
//MiDebug.println(">>>>>>event.getSource.name = " + event.getSource().getName());
//MiDebug.printStackTrace();
		// If item is being displayed and is not a non-unique or other type of non-dispalyed item...
		if (treeList.getItemWithTag(event.getSource()) != null)
			{
			treeList.setItemLabel(event.getSource(), 
				propertyToDisplayAsName == null 
				? event.getSource().getName() 
				: event.getSource().getPropertyValue(propertyToDisplayAsName));
			}
		}
	}


