
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
import com.swfm.mica.util.Utility;
import java.util.Vector;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiLayerTabs implements MiiActionHandler, MiiTypes, MiiActionTypes
	{
	private static final String Mi_LAYER_TABS_RESOURCE_NAME			= "MiLayerTabsResource";
	private static	MiPropertyDescriptions	propertyDescriptions;
	private		MiEditor	editor;
	private		MiTabs		layerTabs;
	private		boolean		nonCurrentLayersAreEditable;
	private		Vector		layers				= new Vector();

	public				MiLayerTabs(MiEditor editor, MiScrolledBox scrolledBox)
		{
		this.editor = editor;
		layerTabs = new MiTabs(Mi_BOTTOM);
		layerTabs.setSelectedTabMovedToFront(false);

		// ---------------------------------------------------------------
		// Make tabs have drag and droppable tabs so the user can move them and
		// therefore move layers around.
		// ---------------------------------------------------------------
		layerTabs.setIsDragAndDropTarget(true);
		layerTabs.appendActionHandler(this, 
			Mi_ACTIONS_OF_PARTS_OF_OBSERVED | Mi_ACTIONS_OF_OBSERVED 
				| Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);

		for (int i = 0; i < editor.getNumberOfLayers(); ++i)
			{
			appendTab(editor.getLayer(i));
			}

		scrolledBox.getHScrollBarBox().insertPart(layerTabs, 0);
		scrolledBox.getHScrollBarBox().setUniqueElementIndex(1);
		scrolledBox.getHScrollBarBox().setElementVJustification(Mi_TOP_JUSTIFIED);
		scrolledBox.setHScrollBarDisplayPolicy(Mi_DISPLAY_ALWAYS);
		scrolledBox.setVScrollBarDisplayPolicy(Mi_DISPLAY_ALWAYS);

		MiMargins margins = scrolledBox.getBox().getInsetMargins();
		margins.bottom = 0;
		scrolledBox.getBox().setInsetMargins(margins);

		margins = scrolledBox.getBox().getMargins(new MiMargins());
		margins.bottom = 0;
		scrolledBox.getBox().setMargins(margins);

		editor.appendActionHandler(this, Mi_EDITOR_LAYER_ADDED_ACTION);
		editor.appendActionHandler(this, Mi_EDITOR_LAYER_REMOVED_ACTION);
		editor.appendActionHandler(this, Mi_EDITOR_LAYER_ORDER_CHANGED_ACTION);
		editor.appendActionHandler(this, Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION);

		layerTabs.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		}
	public		MiTab		getTabForLayer(MiPart layer)
		{
		int num = layerTabs.getNumberOfTabs();
		for (int i = 0; i < num; ++i)
			{
			if (layerTabs.getTab(i).getResource(Mi_LAYER_TABS_RESOURCE_NAME) == layer)
				return(layerTabs.getTab(i));
			}
		return(null);
		}
	public		boolean		processAction(MiiAction action)
		{
		if ((action.getActionSource() == layerTabs)
			&& (action.hasActionType(Mi_VALUE_CHANGED_ACTION)))
			{
			int index = layerTabs.getSelectedTabIndex();
			MiPart layer = (MiPart )layerTabs.getSelectedTab().getResource(Mi_LAYER_TABS_RESOURCE_NAME);
			editor.setCurrentLayer(layer);
			}
		else if (action.getActionSource() == editor)
			{
			if (action.hasActionType(Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION))
				{
				MiPart layer = editor.getCurrentLayer();
				MiTab tab = getTabForLayer(layer);
				// If not the background layer...
				if (tab != null)
					layerTabs.setSelectedTab(layerTabs.getIndexOfTab(tab));
				}
			else if (action.hasActionType(Mi_EDITOR_LAYER_ADDED_ACTION))
				{
				MiPart layer = (MiPart )action.getActionSystemInfo();
				appendTab(layer);
				}
			else if (action.hasActionType(Mi_EDITOR_LAYER_REMOVED_ACTION))
				{
				MiPart layer = (MiPart )action.getActionSystemInfo();
				MiTab tab = getTabForLayer(layer);
				// If not a background layer or some other invisible layer...
				if (tab != null)
					layerTabs.removeTab(tab);
				}
			else if (action.hasActionType(Mi_EDITOR_LAYER_ORDER_CHANGED_ACTION))
				{
				MiParts tabs = new MiParts();
				int num = layerTabs.getNumberOfTabs();
				for (int i = 0; i < num; ++i)
					tabs.addElement(layerTabs.getTab(i));
				layerTabs.removeAllTabs();
				for (int i = 0; i < editor.getNumberOfLayers(); ++i)
					{
					MiPart layer = editor.getLayer(i);
					for (int j = 0; j < tabs.size(); ++j)
						{
						if (tabs.elementAt(j).getResource(
							Mi_LAYER_TABS_RESOURCE_NAME) == layer)
							{
							MiTab tab = (MiTab )tabs.elementAt(j);
							layerTabs.appendTab(tab);
							}
						}
					}
				}
			else if (action.hasActionType(Mi_NAME_CHANGE_ACTION))
				{
				MiPart layer = (MiPart )action.getActionSystemInfo();
				MiTab tab = getTabForLayer(layer);
				tab.setValue(layer.getName());
				}
			}
		else if (action.hasActionType(Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE))
			{
			// ---------------------------------------------------------------
			// Handle drops of the tab, moving it to the closest position.
			// ---------------------------------------------------------------
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();

			if (transferOp.getSource() instanceof MiTab)
				{
				MiTab dndTab = (MiTab )transferOp.getSource();
				MiPoint dndPt = transferOp.getLookTargetPosition();
				MiCoord leftX = 0;
				int targetTabNumber = layerTabs.getNumberOfTabs();
				if (layerTabs.getNumberOfTabs() > 0)
					{
					leftX = layerTabs.getTab(0).getCenterX();
					if (dndPt.x < leftX)
						targetTabNumber = 0;
					}
				for (int i = 1; i < layerTabs.getNumberOfTabs(); ++i)
					{
					MiCoord rightX = layerTabs.getTab(i).getCenterX();
					if ((dndPt.x > leftX) && (dndPt.x < rightX))
						{
						targetTabNumber = i;
						break;
						}
					leftX = rightX;
					}
				MiPart currentLayer = (MiPart )dndTab.getResource(Mi_LAYER_TABS_RESOURCE_NAME);
				int currentLayerNumber = editor.getIndexOfLayer(currentLayer);
				int currentTabNumber = layerTabs.getIndexOfTab(dndTab);

				if (currentTabNumber < targetTabNumber)
					--targetTabNumber;

				MiPart targetLayer = (MiPart )layerTabs.getTab(targetTabNumber)
							.getResource(Mi_LAYER_TABS_RESOURCE_NAME);
				int targetLayerNumber = editor.getIndexOfLayer(targetLayer);

				if (currentTabNumber != targetTabNumber)
					{
					MiPart layer = (MiPart )dndTab.getResource(Mi_LAYER_TABS_RESOURCE_NAME);
					editor.removeLayer(layer);
					editor.insertLayer(layer, targetLayerNumber);
					}
				}
			return(false);
			}
		return(true);
		}
	protected	void		appendTab(MiPart layer)
		{
		MiLayerAttributes layerAttributes = (MiLayerAttributes )layer.getResource(
			MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME);

		if ((layerAttributes != null) 
			&& (layerAttributes.getEditability() == MiLayerAttributes.Mi_LAYER_NEVER_EDITABLE))
			{
			return;
			}

		layer.appendActionHandler(this, Mi_NAME_CHANGE_ACTION);
		int tabNameNumber = layerTabs.getNumberOfTabs() + 1;
		while (layer.getName() == null)
			{
			String name = "Layer " + tabNameNumber++;
			String foundName = name;
			for (int i = 0; i < editor.getNumberOfLayers(); ++i)
				{
				String otherLayerName = editor.getLayer(i).getName();
				if (Utility.isEqualTo(name, otherLayerName))
					{
					foundName = null;
					break;
					}
				}
			if (foundName != null)
				layer.setName(foundName);
			}

		int indexOfTabForLayer = 0;
		for (int i = 0; i < editor.getNumberOfLayers(); ++i)
			{
			MiPart aLayer = editor.getLayer(i);
			if (aLayer == layer)
				break;

			MiLayerAttributes atts = (MiLayerAttributes )aLayer.getResource(
				MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME);
			if ((atts != null)
				&& (atts.getEditability() != MiLayerAttributes.Mi_LAYER_NEVER_EDITABLE))
				{
				++indexOfTabForLayer;
				}
			}

		MiTab tab = layerTabs.insertTab(layer.getName(), indexOfTabForLayer);

		// ---------------------------------------------------------------
		// Make tabs have drag and droppable tabs so the user can move them and
		// therefore move layers around.
		// ---------------------------------------------------------------
		tab.setIsDragAndDropSource(true);
		MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));
		dndBehavior.setDragsReferenceNotCopy(true);
		dndBehavior.setIsDefaultBehaviorForParts(false);
		dndBehavior.setSnapLookCenterToCursor(false);
		dndBehavior.setKeepLookCompletelyWithinRootWindow(false);
		dndBehavior.setValidTargets(new MiParts(layerTabs));
		tab.setDragAndDropBehavior(dndBehavior);

		tab.setResource(Mi_LAYER_TABS_RESOURCE_NAME, layer);
		tab.setContextMenu(new MiLayerTabContextMenu(layerAttributes, layerAttributes));
		layerAttributes.updateCommandManagerState();
		}

	}
class MiLayerTabContextMenu extends MiEditorMenu implements MiiCommandNames, MiiMessages
	{
					/**------------------------------------------------------
	 				 * Constructs a new LayerTabContextMenu. 
					 * @param commandHandler where we are to send the commands
					 *			 this menu generates
					 *------------------------------------------------------*/
	public				MiLayerTabContextMenu(
						MiiCommandHandler commandHandler,
						MiiCommandManager commandManager)
		{
		super("Layer options", commandManager);

		appendBooleanMenuItem(	"Visible", 		commandHandler, 
								Mi_SHOW_COMMAND_NAME,
								Mi_HIDE_COMMAND_NAME);
		appendBooleanMenuItem(	"Editable", 		commandHandler, 
								MiLayerAttributes.Mi_EDITABLE_COMMAND_NAME,
								MiLayerAttributes.Mi_NOT_EDITABLE_COMMAND_NAME);
		appendBooleanMenuItem(	"Printable", 		commandHandler, 
								MiLayerAttributes.Mi_PRINTABLE_COMMAND_NAME,
								MiLayerAttributes.Mi_NOT_PRINTABLE_COMMAND_NAME);
/****
		appendBooleanMenuItem(	"Connect-to-able", 	commandHandler, 
								MiLayerAttributes.Mi_CONNECT_TO_ABLE_COMMAND_NAME,
								MiLayerAttributes.Mi_NOT_CONNECT_TO_ABLE_COMMAND_NAME);
		appendBooleanMenuItem(	"Snap-to-able", 	commandHandler, 
								MiLayerAttributes.Mi_SNAP_TO_ABLE_COMMAND_NAME,
								MiLayerAttributes.Mi_NOT_SNAP_TO_ABLE_COMMAND_NAME);
****/
		appendSeparator();
		appendMenuItem(		"Insert Layer Behind",	commandHandler,
								MiLayerAttributes.Mi_ADD_NEW_LAYER_BEHIND_COMMAND_NAME);
		appendMenuItem(		"Insert Layer In Front",commandHandler,
								MiLayerAttributes.Mi_ADD_NEW_LAYER_INFRONT_COMMAND_NAME);
		appendMenuItem(		"Bring Layer Forward",	commandHandler,
								Mi_BRING_FORWARD_COMMAND_NAME);
		appendMenuItem(		"Send Layer Backward",	commandHandler,
								Mi_SEND_BACKWARD_COMMAND_NAME);
		appendSeparator();
		appendMenuItem(		"Delete", 		commandHandler,
								Mi_DELETE_COMMAND_NAME);
		}
	}


