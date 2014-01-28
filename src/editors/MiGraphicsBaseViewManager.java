
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
import com.swfm.mica.util.CaselessKeyHashtable;
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Tree; 
import com.swfm.mica.util.Utility; 
import java.util.StringTokenizer; 
import java.util.Date; 
import java.util.Enumeration; 
import java.util.Hashtable; 

/**----------------------------------------------------------------------------------------------
 * This class implements the MiiViewManager interface.
 * It supports a wide range of graphics typically used in graphics editors
 * and palettes. It assumes that the precise type of the target will be taken
 * into account target by a subclass of this abstract class.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiGraphicsBaseViewManager 
			extends MiViewManager 
			implements MiiViewManager, MiiTypes, MiiCommandNames, MiiModelTypes,
			MiiActionHandler, MiiActionTypes
	{
	protected	MiiModelEntity	theDocumentEntity;
	protected	MiEditorPalette	editorPalette;
	protected	MiEditor	editor;
	protected	MiPart		target;
	protected	MiContainer	subPalettes;
	protected	CaselessKeyHashtable semanticsTable	= new CaselessKeyHashtable();
	private		Hashtable 	namePartHashtable 	= new Hashtable();
	private 	MiViewManagerPostGraphicsCreationHandler 	postCreationHandler;

					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsBaseViewManager.
					 *------------------------------------------------------*/
	public				MiGraphicsBaseViewManager()
		{
		}
	public		void		setPostGraphicsCreationHandler(MiViewManagerPostGraphicsCreationHandler handler)
		{
		postCreationHandler = handler;
		}
	public		MiViewManagerPostGraphicsCreationHandler	getPostGraphicsCreationHandler()
		{
		return(postCreationHandler);
		}
					/**------------------------------------------------------
		 			 * Sets the target container. This container will be loaded
					 * with graphics by the #setModel command and will be
					 * converted to a MiiModelEntity by the #getModel 
					 * command. The target must be an instanceof MiEditor.
					 * @param target	the target container
					 * @see 		#getView
					 *------------------------------------------------------*/
	public		void		setView(MiPart target)
		{
//System.out.println(this + ": set view: " + target);
//MiDebug.printStackTrace();
		if (this.target != null)
			{
			this.target.removeActionHandlers(this);
			MiSystem.getTransactionManager().removeActionHandlers(this);
			}
		this.target = target;
		if (target instanceof MiEditor)
			{
			editor = (MiEditor )target;
			target.appendActionHandler(this, Mi_DATA_IMPORT_ACTION);
			target.appendActionHandler(this, Mi_DATA_IMPORT_ACTION 
					| Mi_EXECUTE_ACTION_PHASE);
			target.appendActionHandler(this, Mi_DATA_IMPORT_ACTION 
					| Mi_EXECUTE_ACTION_PHASE | Mi_ACTIONS_OF_PARTS_OF_OBSERVED);
			target.appendActionHandler(this, Mi_DELETE_ACTION 
					| Mi_ACTIONS_OF_PARTS_OF_OBSERVED);
			MiSystem.getTransactionManager().appendActionHandler(this, 
				MiiActionTypes.Mi_TRANSACTION_MANAGER_CHANGED_ACTION);
			MiSystem.getTransactionManager().appendActionHandler(this, 
				MiiActionTypes.Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION);
			}
		}
					/**------------------------------------------------------
		 			 * Gets the target container. 
					 * @implements	MiiViewManager#getView
					 *------------------------------------------------------*/
	public		MiPart		getView()
		{
		return(target);
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are:
					 * <pre>
					 *    Mi_TRANSACTION_MANAGER_CHANGED_ACTION
					 *    Mi_DATA_IMPORT_ACTION
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_TRANSACTION_MANAGER_CHANGED_ACTION))
			{
			setHasChanged(true);
			}
		else if (action.hasActionType(Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION))
			{
			// ---------------------------------------------------------------
			// Go through all transactions, (FIX: just look at new ones?) and
			// for each one create and insert a new transaction that will 
			// (undo/redo) the corresponding transaction to the model document
			// (i.e. if the transaction represents a graphics being deleted, create
			// one that deletes the corresponding entity from the document).
			// ---------------------------------------------------------------
			MiNestedTransaction newTransactions 
				= (MiNestedTransaction )action.getActionSystemInfo();
			for (int i = 0; i < newTransactions.size(); ++i)
				{
				MiiTransaction t = (MiiTransaction )newTransactions.elementAt(i);
				String command = t.getCommand();
				if (command.equals(Mi_DELETE_COMMAND_NAME))
					{
					newTransactions.insertElementAt(new MiSimpleTransaction(
						document, Mi_DELETE_COMMAND_NAME, 
						t.getTargets(), null),
						i + 1);
					++i;
					}
				else if (command.equals(Mi_REPLACE_COMMAND_NAME))
					{
					for (int j = 0; j < t.getSources().size(); ++j)
						{
						MiPart theReplacement = t.getSources().elementAt(j);
						MiiModelEntity entity = getEntityForPart(theReplacement);
						if (entity != null)
							{
							processNewEntityAddedByUserToDocument(
								entity, theReplacement);
							}
						}
					newTransactions.insertElementAt(new MiSimpleTransaction(
						document, Mi_REPLACE_COMMAND_NAME,
						t.getTargets(), t.getSources()),
						i + 1);
					++i;
					}
				else if (command.equals(Mi_CREATE_COMMAND_NAME))
					{
					newTransactions.insertElementAt(new MiSimpleTransaction(
						document, Mi_CREATE_COMMAND_NAME,
						t.getTargets(), null), 
						i + 1);
					++i;
					}
				}
			}
		else if (action.hasActionType(
			Mi_DELETE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			{
			MiPart theDeleted = action.getActionSource();
			MiiModelEntity entity = getEntityForPart(theDeleted);
			if (entity != null)
				document.removeModelEntity(entity);
			}
		else if ((action.hasActionType(
			Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE))
			|| (action.hasActionType(
			Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
			{
			MiDataTransferOperation transferOp 
				= (MiDataTransferOperation )action.getActionSystemInfo();
			MiPart newDroppedPart = (MiPart )transferOp.getData();
			newDroppedPart.setCenter(transferOp.getLookTargetPosition());
			MiiModelEntity entity = getEntityForPart(newDroppedPart);
			if (entity != null)
				{
				processNewEntityAddedByUserToDocument(entity, newDroppedPart);
				transferOp.setData(newDroppedPart);
				}
			}
		else if (action.hasActionType(Mi_DATA_IMPORT_ACTION))
			{
			MiDataTransferOperation transferOp 
				= (MiDataTransferOperation )action.getActionSystemInfo();
			MiPart newDroppedPart = (MiPart )transferOp.getTransferredData();
			MiDeletePartsCommand cmd = new MiDeletePartsCommand(
				(MiEditor )getView(), newDroppedPart, false);
			MiSystem.getTransactionManager().appendTransaction(cmd);
			}
		return(true);
		}
					/**------------------------------------------------------
		 			 * Handles the creation of a new entity in response to a
					 * drag and drop, clipboard paste or other user action that
					 * caused the given part to be added to the target.
					 * @param entity	the entity of the part the given part
					 *			was copied from
					 * @param part		the given part just added to the target
					 *------------------------------------------------------*/
	protected	void		processNewEntityAddedByUserToDocument(
						MiiModelEntity entity, MiPart part)
		{
		MiiModelEntity newEntity = document.createModelEntity();
		newEntity.copy(entity);

		// ---------------------------------------------------------------
		// If a layout template is being dropped in the editor, change it's
		// type to just be the layout, not a template.
		// ---------------------------------------------------------------
		String type = entity.getPropertyValue(Mi_TYPE_NAME);
		if ((type != null) && (type.toLowerCase().endsWith(Mi_LAYOUT_TEMPLATE_NAME)))
			{
			newEntity.setPropertyValue(Mi_TYPE_NAME, type.substring(
				0, type.length() - Mi_LAYOUT_TEMPLATE_NAME.length()));
			}

		part.setResource(Mi_SIMPLE_ENTITY_RESOURCE_NAME, newEntity);
		document.appendModelEntity(newEntity);
		part.setName(entity.getName());

		if (part instanceof MiGraphicsNode)
			{
			if (target instanceof MiEditorPalette)
				((MiGraphicsNode )part).setEditable(false);
			else
				((MiGraphicsNode )part).setEditable(true);
			}
		}
					/**------------------------------------------------------
		 			 * Creates graphics for the entities in the given document.
					 * Populates the 'parts' and the 'subPalettes' lists with 
					 * the graphics objects created. This is typically called
					 * by #setModel.
					 * @param document	the document to generate graphics for
					 *------------------------------------------------------*/
	protected	void		createGraphics(MiiModelEntity document)
		{
		MiPart part = null;
		// ---------------------------------------------------------------
		// Look for an entity that represents the document as a whole
		// ---------------------------------------------------------------
		MiModelEntityTreeList list = new MiModelEntityTreeList(document);
		MiiModelEntity entity;

		while ((entity = list.toNext()) != null)
			{
			String type = entity.getPropertyValue(Mi_TYPE_NAME);
			String value;
			if (entity instanceof MiiModelRelation)
				{
				}
			else if ((type != null) && (type.equals(Mi_DOCUMENT_TYPE_NAME)))
				{
				value = entity.getPropertyValue(Mi_IGNORING_CASE_NAME);
				if (value != null)
					setIgnoreCase(Utility.toBoolean(value));
				document.setIgnoreCase(getIgnoreCase());
				
/*
				value = entity.getPropertyValue(Mi_READ_ONLY_NAME);
				if (value != null)
					document.setReadOnly(Utility.toBoolean(value));

				value = entity.getPropertyValue(Mi_VIEW_ONLY_NAME);
				if (value != null)
					document.setViewOnly(Utility.toBoolean(value));
*/

				value = entity.getPropertyValue(Mi_TITLE_NAME);
				document.setTitle(value);

				theDocumentEntity = entity;
				break;
				}
			}
		// ---------------------------------------------------------------
		// Look for all attribute and semantic entities
		// ---------------------------------------------------------------
		list.reset();
		while ((entity = list.toNext()) != null)
			{
			String type = entity.getPropertyValue(Mi_TYPE_NAME);
			if (entity instanceof MiiModelRelation)
				{
				}
			else if ((type != null) && (type.equalsIgnoreCase(Mi_ATTRIBUTES_TYPE_NAME)))
				{
				MiAttributes attributes = new MiAttributes();
				attributes = applyAttributesToAttributes(attributes, entity);
				attributesTable.put(entity.getName(), attributes);
				}
			else if ((type != null) && (type.equalsIgnoreCase(Mi_SEMANTICS_TYPE_NAME)))
				{
				entity.setPropertyValue(Mi_TYPE_NAME, null);
				entity.setPropertyValue(Mi_NAME_NAME, null);
				semanticsTable.put(entity.getName(), entity);
				}
			}
		// ---------------------------------------------------------------
		// Look for all nodes and container types
		// ---------------------------------------------------------------
		list.reset();
		while ((entity = list.toNext()) != null)
			{
			String type = entity.getPropertyValue(Mi_TYPE_NAME);
//MiDebug.println("loading entity : " + entity);
//MiDebug.println("loading type : " + type);
			if (entity instanceof MiiModelRelation)
				{
				}
			else if ((type == null) || (type.equalsIgnoreCase(Mi_NODE_TYPE_NAME)))
				{
				if (entity.getType() != Mi_COMMENT_TYPE) 
					{
					if (type == null)
						continue; // Creates nodes w. reversed bounds, but harmless: entity.setPropertyValue(Mi_TYPE_NAME, Mi_NODE_TYPE_NAME);
					part = createNodeFromEntity(entity);
					applyAttributesToGraphic(part, entity);
					applyLayoutToGraphic(part, entity);
					parts.addElement(part);
					}
				}
			else if (type.equals(Mi_DOCUMENT_TYPE_NAME))
				;
			else if (type.equalsIgnoreCase(Mi_CONNECTION_TYPE_NAME))
				;
			else if (type.equalsIgnoreCase(Mi_ATTRIBUTES_TYPE_NAME))
				;
			else if (type.equalsIgnoreCase(Mi_SEMANTICS_TYPE_NAME))
				;
			else if (type.equalsIgnoreCase(Mi_CONTAINER_TYPE_NAME))
				{
				part = new MiVisibleContainer();
				applyAttributesToGraphic(part, entity);
				applyLayoutToGraphic(part, entity);
				parts.addElement(part);
				}
			else if (type.equalsIgnoreCase(Mi_PALETTE_TYPE_NAME))
				{
				part = new MiContainer();
				applyAttributesToGraphic(part, entity);
				String collapsedAttr = entity.getPropertyValue(Mi_COLLAPSE_COMMAND_NAME);
				boolean expanded = false;
				if (collapsedAttr != null)
					expanded = !Utility.toBoolean(collapsedAttr);
				String expandedAttr = entity.getPropertyValue(Mi_EXPAND_COMMAND_NAME);
				if (expandedAttr != null)
					expanded = Utility.toBoolean(expandedAttr);
					
				part.setResource(Mi_PALETTE_TYPE_NAME, 
					expanded ? Mi_EXPAND_COMMAND_NAME : Mi_COLLAPSE_COMMAND_NAME);

				if (subPalettes == null)
					subPalettes = new MiContainer();

				// If the top level palette... set the palette attributes
				//if (entity.getPropertyValue(Mi_CONTAINER_NAME) == null)
				if (entity.getParent() == document)
					subPalettes.setAttributes(part.getAttributes());

				subPalettes.appendPart(part);
				}
			else if (type.equalsIgnoreCase(Mi_EDITOR_TYPE_NAME))
				{
				applyAttributesToGraphic(editor, entity);
				applyLayoutToGraphic(editor, entity);
				}
			else if (type.equalsIgnoreCase(Mi_LAYER_TYPE_NAME))
				{
				// ---------------------------------------------------------------
				// See if the later is already present (usually the default layer 1)
				// ---------------------------------------------------------------
				String newLayerName = entity.getName();
				boolean layerAlreadyPresent = false;
				part = null;
				if (editor.hasLayers())
					{
					int numLayers = editor.getNumberOfLayers();
					for (int i = 0; ((!layerAlreadyPresent) && (i < numLayers)); ++i)
						{
						part = editor.getLayer(i);
						String layerName = part.getName();
						if (equals(layerName, newLayerName))
							layerAlreadyPresent = true;
						}
					}
				// ---------------------------------------------------------------
				// No layer by this name, make one.
				// ---------------------------------------------------------------
				if (!layerAlreadyPresent)
					{
					part = createLayer(entity);
					}
				applyAttributesToGraphic(part, entity);
				applyLayoutToGraphic(part, entity);
				editor.setHasLayers(true);
				if (!layerAlreadyPresent)
					editor.appendLayer(part);
				parts.addElement(part);
				}
			else if ((part = createShape(type)) != null)
				{
				applyAttributesToGraphic(part, entity);
				applyLayoutToGraphic(part, entity);
				parts.addElement(part);
				}
			else
				{
				printWarning(entity, "No type found with name: " + type);
				}
			}
		// ---------------------------------------------------------------
		// Look for all connections
		// ---------------------------------------------------------------
		list.reset();
		while ((entity = list.toNext()) != null)
			{
			if (entity instanceof MiiModelRelation)
				{
				MiiModelRelation relation = (MiiModelRelation )entity;
				part = createConnectionFromEntity(relation);
				if (part != null)
					parts.addElement(part);
				}
			}

		// ---------------------------------------------------------------
		// Look for all parts of containers and add them to their containers
		// ---------------------------------------------------------------
		list.reset();
		while ((entity = list.toNext()) != null)
			{
			//String containerName = entity.getPropertyValue(Mi_CONTAINER_NAME);
			String containerName = entity.getParent().getName();
			if (entity instanceof MiiModelRelation)
				{
				}
			else if ((entity.getParent() != document) && (containerName != null))
				{ 
				String entityName = entity.getName();
				MiPart container = null;

				if (subPalettes != null)
					container = subPalettes.isContainerOf(containerName);
				if (container == null)
					container = findPartWithName(parts, containerName);
				if ((container == null) 
					&& (equals(containerName, editor.getName())))
					container = editor;

				part = findPartWithName(parts, entityName);
				if ((part == null) && (subPalettes != null))
					part = subPalettes.isContainerOf(entityName);

				if ((container != null) && (part != null) && (!(part instanceof MiLayer)))
					{
					part.removeFromAllContainers();
					container.appendItem(part);
					}

				if (container == null)
					printWarning(entity, "No container found with name: " +containerName);
				if (part == null)
					printWarning(entity, "No entity found with name: " + entityName);
				}
			}
		if (postCreationHandler != null)
			{
			postCreationHandler.notifyHandlers(parts, subPalettes);
			}
		}   
					/**------------------------------------------------------
					 * Converts the graphics found in the target container to 
					 * a MiiModelEntity. This creates entities in the given 
					 * document from the graphics in the target.
					 * @return 		the document
					 * @see 		#setView
					 * @implements		MiiViewManager#getModel
					 *------------------------------------------------------*/
	public		MiiModelEntity	 getModel()
		{
		if (document == null)
			document = new MiModelDocument();
		document.removeAllModelEntities();

		semanticsTable.clear();
		namePartHashtable.clear();

		// ---------------------------------------------------------------
		// Update the attributes of the document entity
		// ---------------------------------------------------------------
		MiiModelEntity entity = theDocumentEntity;
		if (entity == null)
			entity = document.createModelEntity();

// Version, Vendor, Creator, class of IOFormatManager
		entity.setPropertyValue(Mi_TYPE_NAME, Mi_DOCUMENT_TYPE_NAME);
		entity.setPropertyValue(Mi_MODIFICATION_DATE_NAME, new Date().toString());
		entity.setPropertyValue(Mi_IGNORING_CASE_NAME, Utility.toString(document.getIgnoreCase()));
		//entity.setPropertyValue(Mi_READ_ONLY_NAME, Utility.toString(document.isReadOnly()));
		//entity.setPropertyValue(Mi_VIEW_ONLY_NAME, Utility.toString(document.isViewOnly()));
		entity.setPropertyValue(Mi_TITLE_NAME, document.getTitle());
		document.appendModelEntity(entity);

		// ---------------------------------------------------------------
		// Create all of the attribute entities that were referenced in the
		// document when we loaded it
		// ---------------------------------------------------------------
		for (Enumeration e = attributesTable.keys(); e.hasMoreElements();)
			{
			String key = (String)e.nextElement();
			entity = createEntityForAttributes(key, (MiAttributes )attributesTable.get(key));
			document.appendModelEntity(entity);
			}
		// ---------------------------------------------------------------
		// If we are converting the editor graphics to entities...
		// ---------------------------------------------------------------
		if (editor != null)
			{
			// ---------------------------------------------------------------
			// Add all of the entities that the graphics in the target are
			// representing as well as creating entitities for graphics that do 
			// not yet have an entity and need one. Then set the values of all 
			// of the entity references that the entities may have.
			// ---------------------------------------------------------------
			generateUniqueNamesForGraphicsParts(editor);
			addEntitiesToDocument(editor);
			addReferencesToEntities(editor);
			}
		Tree palettes = null;
		if (editorPalette != null)
			{
			palettes = editorPalette.getPalettes();

			// ---------------------------------------------------------------
			// Add all of the entities that the graphics in the target are
			// representing as well as creating entitities for graphics that do 
			// not yet have an entity and need one. Then set the values of all 
			// of the entity references that the entities may have.
			// ---------------------------------------------------------------
			for (int i = 0; i < palettes.size(); ++i)
				addEntitiesToDocument(palettes.elementAt(i));
			for (int i = 0; i < palettes.size(); ++i)
				addReferencesToEntities(palettes.elementAt(i));
			}
		// ---------------------------------------------------------------
		// Make sure that all of the semantics that are referenced by the
		// entities in the document are also added to the document.
		// ---------------------------------------------------------------
		for (Enumeration e = semanticsTable.keys(); e.hasMoreElements();)
			{
			MiiModelEntity semantics = (MiiModelEntity )e.nextElement();
			document.appendModelEntity(semantics);
			}
		return(document);
		}
	protected	void		generateUniqueNamesForGraphicsParts(MiPart container)
		{
		getNamesForGraphicsParts(container, true, 0, namePartHashtable);
		getNamesForGraphicsParts(container, false, 0, namePartHashtable);
		}
	protected	int		getNamesForGraphicsParts(
						MiPart container, 
						boolean partsThatHaveNamesFlag, 
						int uniqueSuffixIndex, 
						java.util.Hashtable namePartHashtable)
		{
		if (!container.isSavable())
			return(uniqueSuffixIndex);

		String 		name = container.getName();

		if ((partsThatHaveNamesFlag) && (name != null))
			{
			if (namePartHashtable.get(name) != null)
				{
				uniqueSuffixIndex = 0;
				while (true)
					{
					String n = name + uniqueSuffixIndex++;
					if (namePartHashtable.get(n) == null)
						{
						name = n;
						break;
						}
					}
				container.setName(name);
				}
			namePartHashtable.put(name, container);
			}
		else if ((!partsThatHaveNamesFlag) && (name == null))
			{
			name = "name";
			while (true)
				{
				String n = name + uniqueSuffixIndex++;
				if (namePartHashtable.get(n) == null)
					{
					name = n;
					break;
					}
				}
			container.setName(name);
			namePartHashtable.put(name, container);
			}
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			uniqueSuffixIndex = getNamesForGraphicsParts(container.getPart(i), 
				partsThatHaveNamesFlag, uniqueSuffixIndex, namePartHashtable);
			}

		return(uniqueSuffixIndex);
		}
					/**------------------------------------------------------
		 			 * Creates entities in the given document from the graphics
					 * in the given tree. Trees are typically used with palettes.
					 * @param tree 		the tree containing MiParts
					 *------------------------------------------------------*/
	protected	void		addEntitiesToDocument(Tree tree)
		{
		MiPart container = (MiPart )tree.getData();
		MiiModelEntity entity = (MiiModelEntity )container.getResource(
			Mi_SIMPLE_ENTITY_RESOURCE_NAME);
		// ---------------------------------------------------------------
		// If the graphics object does not yet have an associated entity
		// and we are to create such entities...
		// ---------------------------------------------------------------
		if (entity == null)
			{
			entity = document.createModelEntity();
			if (container.getName() != null)
				{
				entity.setPropertyValue(Mi_NAME_NAME, container.getName());
				entity.setName(container.getName());
				}
			container.setResource(Mi_SIMPLE_ENTITY_RESOURCE_NAME, entity);
			//document.assignUniqueNameToEntity(entity, MiDebug.getMicaClassName(container));
			applyTypeToEntity(container, entity);
			applyAttributesToEntity(container, entity);
			document.appendModelEntity(entity);
			}
		// ---------------------------------------------------------------
		// ..else if there is an associated entity, and this is the first pass...
		// ---------------------------------------------------------------
		else
			{
			applyTypeToEntity(container, entity);
			applyAttributesToEntity(container, entity);
			document.appendModelEntity(entity);
			}
		// ---------------------------------------------------------------
		// Create entities for the parts of this tree that need them.
		// ---------------------------------------------------------------
		for (int i = 0; i < tree.size(); ++i)
			{
			addEntitiesToDocument(tree.elementAt(i));
			}
		}
					/**------------------------------------------------------
		 			 * Creates entities in the given document from the graphics
					 * in the given container. 
					 * @param container 	the container of graphics parts
					 *------------------------------------------------------*/
	protected	void		addEntitiesToDocument(MiPart container)
		{
		if (!container.isSavable())
			return;

		MiiModelEntity entity = (MiiModelEntity )container.getResource(
			Mi_SIMPLE_ENTITY_RESOURCE_NAME);
		// ---------------------------------------------------------------
		// If the graphics object does not yet have an associated entity
		// and we are to create such entities...
		// ---------------------------------------------------------------
		if (entity == null)
			{
			entity = document.createModelEntity();
			if (container.getName() != null)
				{
				entity.setPropertyValue(Mi_NAME_NAME, container.getName());
				entity.setName(container.getName());
				}
			container.setResource(Mi_SIMPLE_ENTITY_RESOURCE_NAME, entity);
			//document.assignUniqueNameToEntity(entity, MiDebug.getMicaClassName(container));
			applyTypeToEntity(container, entity);
			applyAttributesToEntity(container, entity);
			document.appendModelEntity(entity);
			}
		// ---------------------------------------------------------------
		// ..else if there is an associated entity, and this is the first pass...
		// ---------------------------------------------------------------
		else
			{
			applyTypeToEntity(container, entity);
			applyAttributesToEntity(container, entity);
			document.appendModelEntity(entity);
			}

		// ---------------------------------------------------------------
		// If the graphics object is a node or some other ungroupable
		// graphics object, do not create entities for it's sub-parts.
		// ---------------------------------------------------------------
		if (((entity != null) && (entity.getPropertyValue(Mi_TYPE_NAME).equals(Mi_NODE_TYPE_NAME)))
			|| (!container.isUngroupable()) || (container instanceof MiWidget))
			{
			return;
			}
		// ---------------------------------------------------------------
		// Create entities for the parts of this graphics object that need them.
		// ---------------------------------------------------------------
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			addEntitiesToDocument(container.getPart(i));
			}
		}
					/**------------------------------------------------------
		 			 * Adds attributes to entities that reference other enttiies.
					 * These are attributes like Mi_CONTAINER_NAME, Mi_LAYOUT_NAME,
					 * Mi_CONNECTION_SOURCE_NAME, Mi_CONNECTION_DESTINATION_NAME.
					 * @param tree 		the tree containing entities
					 *------------------------------------------------------*/
	protected	void		addReferencesToEntities(Tree tree)
		{
		MiPart container = (MiPart )tree.getData();
		MiiModelEntity entity = (MiiModelEntity )container.getResource(
			Mi_SIMPLE_ENTITY_RESOURCE_NAME);
		applyLayoutToEntity(container, entity);
		MiiModelEntity parentEntity;
		if ((tree.getParent() != null) 
			&& ((parentEntity = getEntityForPart((MiPart )tree.getParent().getData())) != null))
			{
			entity.setPropertyValue(Mi_CONTAINER_NAME, parentEntity.getName());
			}
		else
			{
			// Top level palette...
			entity.setPropertyValue(Mi_CONTAINER_NAME, null);
			}
		if (container instanceof MiConnection)
			applyEndPointsToConnectionEntity((MiConnection )container, entity);

		if (!entity.getPropertyValue(Mi_TYPE_NAME).equals(Mi_NODE_TYPE_NAME))
			applyLayoutToEntity(container, entity);

		for (int i = 0; i < tree.size(); ++i)
			addReferencesToEntities(tree.elementAt(i));
		}
					/**------------------------------------------------------
		 			 * Adds attributes to entities that reference other enttiies.
					 * These are attributes like Mi_CONTAINER_NAME, Mi_LAYOUT_NAME,
					 * Mi_CONNECTION_SOURCE_NAME, Mi_CONNECTION_DESTINATION_NAME.
					 * @param container 	the container containing entities
					 *------------------------------------------------------*/
	protected	void		addReferencesToEntities(MiPart container)
		{
		if (!container.isSavable())
			return;

		MiiModelEntity entity = (MiiModelEntity )container.getResource(
			Mi_SIMPLE_ENTITY_RESOURCE_NAME);

		if (entity == null)
			{
			MiDebug.println("No entity found for graphics part: " + container);
			return;
			}

		applyContainerToEntity(container, entity);
		if (container instanceof MiConnection)
			applyEndPointsToConnectionEntity((MiConnection )container, entity);

		if ((entity != null) && (entity.getPropertyValue(Mi_TYPE_NAME).equals(Mi_NODE_TYPE_NAME)))
			return;
		applyLayoutToEntity(container, entity);
		if ((!container.isUngroupable()) || (container instanceof MiWidget))
			return;
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			addReferencesToEntities(container.getPart(i));
		}
					/**------------------------------------------------------
		 			 * Determines and assigns the type to the given entity (i.e.
					 * the value of the attribute Mi_TYPE_NAME). If the entity 
					 * already has a type then nothing is done.
					 * @param part	 	the graphics of the entity
					 * @param entity	the entity to assign a type to
					 *------------------------------------------------------*/
	protected	void		applyTypeToEntity(MiPart part, MiiModelEntity entity)
		{
		// Return is entity already has a type
		if (entity.getPropertyValue(Mi_TYPE_NAME) != null)
			return;

		if (part.getResource(Mi_PALETTE_TYPE_NAME) != null)
			entity.setPropertyValue(Mi_TYPE_NAME, Mi_PALETTE_TYPE_NAME);
		else if (part instanceof MiEditor)
			entity.setPropertyValue(Mi_TYPE_NAME, Mi_EDITOR_TYPE_NAME);
		else if ((editor.isLayer(part)) || (part instanceof MiLayer))
			entity.setPropertyValue(Mi_TYPE_NAME, Mi_LAYER_TYPE_NAME);
		// All widgets satisfy this: else if (part instanceof MiVisibleContainer)
			//entity.setPropertyValue(Mi_TYPE_NAME, Mi_CONTAINER_TYPE_NAME);
		else if (part instanceof MiiLayout)
			entity.setPropertyValue(Mi_TYPE_NAME, getTypeOfLayout((MiiLayout )part) 
				+ (layoutIsATemplate((MiiLayout )part) ? Mi_LAYOUT_TEMPLATE_NAME : ""));
		else if (part instanceof MiConnection)
			entity.setPropertyValue(Mi_TYPE_NAME, Mi_CONNECTION_TYPE_NAME);
		else if (part instanceof MiPlaceHolder)
			entity.setPropertyValue(Mi_TYPE_NAME, Mi_PLACE_HOLDER_TYPE_NAME);
		else if (part instanceof MiGraphicsNode)
			entity.setPropertyValue(Mi_TYPE_NAME, Mi_NODE_TYPE_NAME);
		else 
			{
			String shapeType = getShapeType(part);
			if (shapeType != null)
				{
				entity.setPropertyValue(Mi_TYPE_NAME, shapeType);
				}
			else
				{
				entity.setPropertyValue(Mi_TYPE_NAME, part.getClass().getName());
				}
			}
		}
	protected	boolean		layoutIsATemplate(MiiLayout layout)
		{
		if (layout instanceof MiManipulatableLayout)
			{
			MiManipulatableLayout manLayout = (MiManipulatableLayout )layout;
			if (manLayout.getMinimumNumberOfTargetNodes() == manLayout.getNumberOfNodes())
				{
				for (int i = 0; i < manLayout.getNumberOfNodes(); ++i)
					{
					if (!(manLayout.getNode(i) instanceof MiPlaceHolder))
						return(false);
					}
				return(true);
				}
			}
		return(false);
		}
					/**------------------------------------------------------
		 			 * Determines and assigns the layout to the given entity (i.e.
					 * the value of the attribute Mi_LAYOUT_NAME). 
					 * @param part	 	the graphics of the entity that may
					 *			have a layout
					 * @param entity	the entity to assign a layout 
					 *			attribute to
					 *------------------------------------------------------*/
	protected	void		applyLayoutToEntity(MiPart part, MiiModelEntity entity)
		{
		if (part.getLayout() != null)
			{
			MiiModelEntity layoutEntity 
				= (MiiModelEntity )((MiLayout )part.getLayout()).getResource(
					Mi_SIMPLE_ENTITY_RESOURCE_NAME);
			if (layoutEntity != null)
				entity.setPropertyValue(Mi_LAYOUT_NAME, layoutEntity.getName());
			else
				entity.setPropertyValue(Mi_LAYOUT_NAME, getTypeOfLayout(part.getLayout()));
			}
		}
					/**------------------------------------------------------
		 			 * Determines and assigns the container to the given entity (i.e.
					 * the value of the attribute Mi_CONTAINER_NAME). 
					 * @param part	 	the graphics of the entity that may
					 *			have a container that is not the
					 *			editor or palette
					 * @param entity	the entity to assign a container 
					 *			attribute to
					 *------------------------------------------------------*/
	protected	void		applyContainerToEntity(MiPart part, MiiModelEntity entity)
		{
		if ((!(part instanceof MiEditor)) && (!(part instanceof MiEditorPalette)))
			{
			MiiModelEntity containerEntity 
				= (MiiModelEntity )part.getContainer(0).getResource(
					Mi_SIMPLE_ENTITY_RESOURCE_NAME);
			if (containerEntity == null)
				{
				MiDebug.println("No entity assigned to container: " 
					+ part.getContainer(0) + " of part: " + part);
				}
			else
				{
				entity.setPropertyValue(Mi_CONTAINER_NAME, containerEntity.getName());
				}
			}
		}
					/**------------------------------------------------------
		 			 * Determines and assigns the connection end-points to the 
					 * given entity (i.e. the value of the Mi_CONNECTION_SOURCE_NAME
					 * and Mi_CONNECTION_DESTINATION_NAME attributes). 
					 * @param conn	 	the graphics connection
					 * @param entity	the entity to assign a endpoints to 
					 *------------------------------------------------------*/
	protected	void		applyEndPointsToConnectionEntity(
						MiConnection conn, MiiModelEntity entity)
		{
		entity.setPropertyValue(Mi_CONNECTION_SOURCE_NAME, 
			getEntityForPart(conn.getSource()).getName());
		entity.setPropertyValue(Mi_CONNECTION_DESTINATION_NAME, 
			getEntityForPart(conn.getDestination()).getName());
		}
					/**------------------------------------------------------
		 			 * Creates a entity that represents a graphics attributes
					 * object (i.e. an enttiy of type Mi_ATTRIBUTES_TYPE_NAME).
					 * @param nameOfAtts	the name the new entity
					 * @param atts		the graphics attributes object
					 *------------------------------------------------------*/
	protected	MiiModelEntity	createEntityForAttributes(String nameOfAtts, MiAttributes atts)
		{
		MiiModelEntity entity = theDocumentEntity.createModelEntity();
		entity.setPropertyValue(Mi_TYPE_NAME, Mi_ATTRIBUTES_TYPE_NAME);
		entity.setName(nameOfAtts);
		MiPropertyDescriptions propertyDescriptions = atts.getPropertyDescriptions();
		for (int i = 0; i < propertyDescriptions.size(); ++i)
			{
			MiPropertyDescription propertyDescription = propertyDescriptions.elementAt(i);
			String name = propertyDescription.getName();
			String currentValue = atts.getAttributeValue(name);
			String defaultValue  = propertyDescription.getDefaultValue();
			if ((currentValue != null) && (defaultValue != null))
				{
				if (!currentValue.equalsIgnoreCase(defaultValue))
					entity.setPropertyValue(name, currentValue);
				}
			}
		return(entity);
		}
					/**------------------------------------------------------
		 			 * Gets the entity that represents the given graphics object.
					 * @param part		the graphics part
					 * @return 		the corresponding entity or null
					 *------------------------------------------------------*/
	protected	MiiModelEntity	getEntityForPart(MiPart part)
		{
		return((MiiModelEntity )part.getResource(Mi_SIMPLE_ENTITY_RESOURCE_NAME));
		}
					/**------------------------------------------------------
		 			 * Gets the type of the given graphics layout.
					 * @param layout	the graphics layout
					 * @return 		the corresponding type or class name
					 *------------------------------------------------------*/
	protected	String		getTypeOfLayout(MiiLayout layout)
		{
		if (layout instanceof MiRowLayout)
			return(Mi_ROW_LAYOUT_TYPE_NAME);
		if (layout instanceof MiColumnLayout)
			return(Mi_COLUMN_LAYOUT_TYPE_NAME);
		if (layout instanceof MiGridLayout)
			return(Mi_GRID_LAYOUT_TYPE_NAME);
		if (layout instanceof MiStarGraphLayout)
			return(Mi_STAR_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof MiUndirGraphLayout)
			return(Mi_UNDIRECTED_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof MiOutlineGraphLayout)
			return(Mi_OUTLINE_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof MiTreeGraphLayout)
			return(Mi_TREE_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof MiRingGraphLayout)
			return(Mi_RING_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof Mi2DMeshGraphLayout)
			return(Mi_2DMESH_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof MiLineGraphLayout)
			return(Mi_LINE_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof MiCrossBarGraphLayout)
			return(Mi_CROSSBAR_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof MiOmegaGraphLayout)
			return(Mi_OMEGA_GRAPH_LAYOUT_TYPE_NAME);
		if (layout instanceof MiSizeOnlyLayout)
			return(Mi_SIZE_ONLY_LAYOUT_TYPE_NAME);

//System.out.println("No name found for MiiLayout: " + layout);
		return(layout.getClass().getName());
		}
					/**------------------------------------------------------
		 			 * Determines and assigns the the values of the attributes
					 * of the given graphics part (that are not equal to their
					 * default attribute values) to the given entity.
					 * @param part	 	the graphics part
					 * @param entity	the entity to assign attributes to
					 *------------------------------------------------------*/
	protected	void		applyAttributesToEntity(MiPart part, MiiModelEntity entity)
		{
		MiPropertyDescriptions propertyDescriptions = part.getPropertyDescriptions();

		boolean isConnection = part instanceof MiConnection;
		boolean isEditor = part instanceof MiEditor;

		try	{
			MiiModelEntity semantics = (MiiModelEntity )part.getResource(Mi_SEMANTICS_TYPE_NAME);
			if (semantics != null)
				{
				String semanticsName = semantics.getName();
				if (semanticsName == null)
					{
					semanticsName = part + ".semantics";
					}
				String name = semanticsName;
				int i = 0;
				while (namePartHashtable.get(name) != null)
					{
					name = semanticsName + i;
					++i;
					}

				namePartHashtable.put(name, part);
				semanticsName = name;
				semantics.setPropertyValue(Mi_TYPE_NAME, Mi_SEMANTICS_TYPE_NAME);
				semantics.setPropertyValue(Mi_NAME_NAME, semanticsName);

				semanticsTable.put(semantics, semanticsName);
				entity.setPropertyValue(Mi_SEMANTICS_TYPE_NAME, semanticsName);
				}
			}
		catch (Exception e)
			{
			}

		// ---------------------------------------------------------------
		// Set the Mi_VALUE_NAME attribute if this is a widget or text shape
		// (this can be removed when these get these as properties)
		// ---------------------------------------------------------------
		if (part instanceof MiWidget)
			entity.setPropertyValue(Mi_VALUE_NAME, ((MiWidget )part).getValue());
		else if (part instanceof MiText)
			entity.setPropertyValue(Mi_VALUE_NAME, ((MiText )part).getText());

		// ---------------------------------------------------------------
		// For all properties of the graphics part, if the value of the
		// property is not equal to the default value, then add this property
		// and it's value as attributes to the entity.
		// ---------------------------------------------------------------
		for (int i = 0; i < propertyDescriptions.size(); ++i)
			{
			MiPropertyDescription propertyDescription = propertyDescriptions.elementAt(i);
			if (!propertyDescription.isEditable(null))
				continue;

			String name = propertyDescription.getName();
			// ---------------------------------------------------------------
			// If this is a connection, ignore a number of properties that are
			// not important.
			// ---------------------------------------------------------------
			if (name.equalsIgnoreCase(Mi_NAME_NAME))
				continue;

			if ((isConnection) 
				&& ((name.equals(Mi_X_COORD_NAME)) || (name.equals(Mi_Y_COORD_NAME))
				|| (name.equals(Mi_WIDTH_NAME)) || (name.equals(Mi_HEIGHT_NAME))
				|| (name.equals(Mi_MOVABLE_ATT_NAME))))
				{
				continue;
				}
			// ---------------------------------------------------------------
			// If this is an editor, ignore the background menu
			// ---------------------------------------------------------------
			if ((isEditor) && (name.equalsIgnoreCase(Mi_CONTEXT_MENU_NAME)))
				{
				continue;
				}
			// ---------------------------------------------------------------
			// If this is a node, ignore the width and height
			// ---------------------------------------------------------------
			if ((part instanceof MiGraphicsNode) 
				&& (name.equals(Mi_WIDTH_NAME) || name.equals(Mi_HEIGHT_NAME)))
				{
				continue;
				}
			// ---------------------------------------------------------------
			// Get the value of the property and compare it against the default
			// value, assigning it to the entity if different.
			// ---------------------------------------------------------------
			String currentValue = part.getPropertyValue(name);
			String defaultValue  = propertyDescription.getDefaultValue();
			if (currentValue != null)
				{
				if (!currentValue.equalsIgnoreCase(defaultValue))
					entity.setPropertyValue(name, currentValue);
				}
			}
		}
					/**------------------------------------------------------
					 * Makes connections to/from part from/to others.
					 * @param part	 	the src or dest of the connections
					 * @param others	a comma separated list of names of
					 *			other entities
					 * @param fromPartToOthers true if the given part is the
					 *			source of all the conenctions
					 *------------------------------------------------------*/
	protected	void		makeConnectionsToOthers(
						MiPart part, String others, boolean fromPartToOthers)
		{
		StringTokenizer t = new StringTokenizer(others, "(),");
		while (t.hasMoreTokens())
			{
			String otherName = t.nextToken().trim();
			MiPart other = findPartWithName(parts, otherName);
			if (other == null)
				{
				MiiModelEntity e = theDocumentEntity.createModelEntity();
				e.setPropertyValue("name", otherName);
				other = createNodeFromEntity(e);
				applyAttributesToGraphic(other, e);
				parts.addElement(other);
				}
			MiConnection conn;
			if (fromPartToOthers)
				conn = new MiConnection(part, other);
			else
				conn = new MiConnection(other, part);

			conn.setConnectionsMustBeConnectedAtBothEnds(true);
			parts.addElement(conn);
			}
		}
	protected	void		applyAttributesToGraphic(MiPart part, MiiModelEntity entity)
		{
		super.applyAttributesToGraphic(part, entity);
		String semanticsName = entity.getPropertyValue(Mi_SEMANTICS_TYPE_NAME);
		if (semanticsName != null)
			part.setResource(Mi_SEMANTICS_TYPE_NAME, semanticsTable.get(semanticsName));
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Framework
 *
 *----------------------------------------------------------------------------------------------*/
class MiSimpleTransaction extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames, MiiTypes, MiiNames
	{
	private		String 		command;
	private		MiParts 	targetParts		= new MiParts();
	private		MiParts 	sourceParts		= new MiParts();


	public				MiSimpleTransaction()
		{
		}

	public				MiSimpleTransaction(
						MiiModelEntity document,
						String command,
						MiParts targetParts,
						MiParts sourceParts)
		{
		setTargetOfCommand(document);
		this.command = command;
		if (targetParts != null)
			this.targetParts.append(targetParts);
		if (sourceParts != null)
			this.sourceParts.append(sourceParts);
		}
	public		MiiModelEntity	getModel()
		{
		return((MiiModelEntity )getTargetOfCommand());
		}
	public		void		processCommand(String command)
		{
		processCommand(getModel(), command, targetParts, sourceParts);
		}

	public		void		processCommand(
						MiiModelEntity document,
						String command,
						MiParts targetParts,
						MiParts sourceParts)
		{
		MiSimpleTransaction cmd = new MiSimpleTransaction(
			document, command, targetParts, sourceParts);
		cmd.doit(false);
		MiSystem.getTransactionManager().appendTransaction(cmd);
		}
	protected	void		doit(boolean toUndo)
		{
		MiiModelEntity document = getModel();
		if (!toUndo)
			{
			if (command.equals(Mi_DELETE_COMMAND_NAME))
				{
				for (int i = 0; i < targetParts.size(); ++i)
					{
					MiiModelEntity removedEntity 
						= getEntityForPart(targetParts.elementAt(i));
					if (removedEntity != null)
						document.removeModelEntity(removedEntity);
					}
				}
			else if (command.equals(Mi_REPLACE_COMMAND_NAME))
				{
				for (int i = 0; i < targetParts.size(); ++i)
					{
					MiiModelEntity replacedEntity 
						= getEntityForPart(targetParts.elementAt(i));
					MiiModelEntity replacingEntity 
						= getEntityForPart(sourceParts.elementAt(i));
					if (replacedEntity != null)
						document.removeModelEntity(replacedEntity);
					if (replacingEntity != null)
						document.appendModelEntity(replacingEntity);
					}
				}
			else if (command.equals(Mi_CREATE_COMMAND_NAME))
				{
				for (int i = 0; i < targetParts.size(); ++i)
					{
					MiiModelEntity createdEntity 
						= getEntityForPart(targetParts.elementAt(i));
					if (createdEntity != null)
						document.appendModelEntity(createdEntity);
					}
				}
			}
		else
			{
			if (command.equals(Mi_DELETE_COMMAND_NAME))
				{
				for (int i = 0; i < targetParts.size(); ++i)
					{
					MiiModelEntity removedEntity 
						= getEntityForPart(targetParts.elementAt(i));
					if (removedEntity != null)
						document.appendModelEntity(removedEntity);
					}
				}
			else if (command.equals(Mi_REPLACE_COMMAND_NAME))
				{
				for (int i = 0; i < targetParts.size(); ++i)
					{
					MiiModelEntity replacedEntity 
						= getEntityForPart(targetParts.elementAt(i));
					MiiModelEntity replacingEntity 
						= getEntityForPart(sourceParts.elementAt(i));
					if (replacingEntity != null)
						document.removeModelEntity(replacingEntity);
					if (replacedEntity != null)
						document.appendModelEntity(replacedEntity);
					}
				}
			else if (command.equals(Mi_CREATE_COMMAND_NAME))
				{
				for (int i = 0; i < targetParts.size(); ++i)
					{
					MiiModelEntity createdEntity 
						= getEntityForPart(targetParts.elementAt(i));
					if (createdEntity != null)
						document.removeModelEntity(createdEntity);
					}
				}
			}
		}
					/**------------------------------------------------------
		 			 * Gets the entity that represents the given graphics object.
					 * @param part		the graphics part
					 * @return 		the corresponding entity or null
					 *------------------------------------------------------*/
	protected	MiiModelEntity	getEntityForPart(MiPart part)
		{
		return((MiiModelEntity )part.getResource(Mi_SIMPLE_ENTITY_RESOURCE_NAME));
		}
					/**------------------------------------------------------
					 * Gets the name of this transaction. This name is often 
					 * displayed, for example, in the menubar's edit pulldown
					 * menu.
					 * @return		the name of this transaction.
					 * @implements		MiiTransaction#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(MiSystem.getProperty(command, command));
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return("MiSimpleTransaction+" + command);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(false);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(true);
		} 
					/**------------------------------------------------------
					 * Repeats this transaction. This re-applies the changes 
					 * encapsulated by this transaction. For example, a 
					 * translation of a shape can be repeated in order to move 
					 * it further.
					 * @implements		MiiTransaction#repeat
					 *------------------------------------------------------*/
	public		void		repeat()
		{
		} 
					/**------------------------------------------------------
					 * Gets whether this transaction is undoable.
					 * @returns		true if undoable.
					 * @implements		MiiTransaction#isUndoable
					 *------------------------------------------------------*/
	public		boolean		isUndoable()
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Gets whether this transaction is repeatable. If repeatable
					 * then calling this transaction's repeat() method is permitted.
					 * @returns		true if repeatable.
					 * @implements		MiiTransaction#isRepeatable
					 *------------------------------------------------------*/
	public		boolean		isRepeatable()
		{
		return(false);
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		return(targetParts);
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		return(sourceParts);
		}
	}

