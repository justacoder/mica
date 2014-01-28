
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
import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import java.awt.Color;
import java.util.Vector;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiLayerManager extends MiModelEntity implements MiiActionHandler, MiiTypes, MiiActionTypes
	{
	private		MiEditor	editor;
	private		MiPart		backgroundLayer;
	private		MiPart		overlayLayer;
	private		boolean		keepConnectionsBelowNodes;

	public				MiLayerManager(MiEditor editor)
		{
		this.editor = editor;
		editor.setHasLayers(true);
		for (int i = 0; i < editor.getNumberOfLayers(); ++i)
			{
			MiPart layer = editor.getLayer(i);
			MiLayerAttributes layerAttributes = new MiLayerAttributes(layer);
			appendModelEntity(layerAttributes);
			}
		editor.appendActionHandler(this, Mi_EDITOR_LAYER_ADDED_ACTION);
		editor.appendActionHandler(this, Mi_EDITOR_LAYER_REMOVED_ACTION);
		editor.appendActionHandler(this, Mi_EDITOR_LAYER_ORDER_CHANGED_ACTION);
		editor.appendActionHandler(this, Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION);
		}

	public		void		appendNewLayer()
		{
		MiContainer layer = new MiLayer();
		layer.setKeepConnectionsBelowNodes(keepConnectionsBelowNodes);
		layer.setLayout(new MiSizeOnlyLayout());
		//layer.setName("Layer " + editor.getNumberOfLayers());
		editor.appendLayer(layer);
		}
	protected	void		setBackgroundLayer(MiPart layer)
		{
		backgroundLayer = layer;
		}
	protected		MiPart		getBackgroundLayer()
		{
		return(backgroundLayer);
		}
	protected		void		setOverlayLayer(MiPart layer)
		{
		overlayLayer = layer;
		}
	protected		MiPart		getOverlayLayer()
		{
		return(overlayLayer);
		}
	public		MiLayerAttributes getLayerAttributes(int index)
		{
		MiPart layer = editor.getLayer(index);
		return((MiLayerAttributes )layer.getResource(MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME));
		}
					/**------------------------------------------------------
	 				 * Sets whether the order of the MiParts in the layers
					 * are kept such that MiConnections are at the begining
					 * of the list. This is useful when and one does not want
					 * connections to be drawn in front of the nodes.
					 * @param flag		true if connections to be drawn 
					 *			first
					 * @see		MiConnection#setTruncateLineAtEndPointPartBoundries
					 * @see		MiContainer#setKeepConnectionsBelowNodes
					 * @see		#getKeepConnectionsBelowNodes
					 *------------------------------------------------------*/
	public		void		setKeepConnectionsBelowNodes(boolean flag)
		{
		keepConnectionsBelowNodes = flag;
		for (int i = 0; i < editor.getNumberOfLayers(); ++i)
			{
			if (editor.getLayer(i) instanceof MiContainer)
				{
				((MiContainer )editor.getLayer(i)).setKeepConnectionsBelowNodes(flag);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Gets whether the connections are to be drawn before
					 * (behind) nodes.
					 * @return 		true if connections to be drawn 
					 *			first
					 * @see		#setKeepConnectionsBelowNodes
					 * @see		MiContainer#setKeepConnectionsBelowNodes
					 *------------------------------------------------------*/
	public		boolean		getKeepConnectionsBelowNodes()
		{
		return(keepConnectionsBelowNodes);
		}
	public		boolean		processAction(MiiAction action)
		{
		MiPart layer = (MiPart )action.getActionSystemInfo();
		if (action.hasActionType(Mi_EDITOR_LAYER_ADDED_ACTION))
			{
			appendModelEntity(new MiLayerAttributes(layer));
			updateLayerAttributesState();
			}

		else if (action.hasActionType(Mi_EDITOR_LAYER_REMOVED_ACTION))
			{
			MiLayerAttributes layerAttributes = (MiLayerAttributes )layer.getResource(
				MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME);
			if (layerAttributes != null)
				{
				removeModelEntity(layerAttributes);
				}
			updateLayerAttributesState();
			}
		else if (action.hasActionType(Mi_EDITOR_LAYER_ORDER_CHANGED_ACTION))
			{
			removeAllModelEntities();
			for (int i = 0; i < editor.getNumberOfLayers(); ++i)
				{
				layer = editor.getLayer(i);
				MiLayerAttributes layerAttributes = (MiLayerAttributes )layer.getResource(
					MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME);
				if (layerAttributes == null)
					{
					layerAttributes = new MiLayerAttributes(layer);
					}
				appendModelEntity(layerAttributes);
				}
			updateLayerAttributesState();
			}
		else if (action.hasActionType(Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION))
			{
			MiPart currentLayer = editor.getCurrentLayer();
			MiModelEntityList list = getModelEntities();
			for (int i = 0; i < list.size(); ++i)
				{
				MiLayerAttributes atts = (MiLayerAttributes )list.elementAt(i);
				if (atts.getLayer() == currentLayer)
					atts.setCurrent(true);
				else if (atts.isCurrent())
					atts.setCurrent(false);
				}
			updateLayerAttributesState();
			}
		return(true);
		}
	// Do not allow the one and only layer to be deleted.
	protected	void		updateLayerAttributesState()
		{
		int numberOfDeletableLayers = 0;
		for (int i = 0; i < editor.getNumberOfLayers(); ++i)
			{
			MiPart layer = editor.getLayer(i);
			MiLayerAttributes layerAttributes = (MiLayerAttributes )layer.getResource(
				MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME);
			if ((layerAttributes != null) 
				&& (layerAttributes.getEditability() != MiLayerAttributes.Mi_LAYER_NEVER_EDITABLE))
				{
				++numberOfDeletableLayers;
				}
			}

		boolean deletable = numberOfDeletableLayers > 1;
		for (int i = 0; i < editor.getNumberOfLayers(); ++i)
			{
			MiPart layer = editor.getLayer(i);
			MiLayerAttributes layerAttributes = (MiLayerAttributes )layer.getResource(
				MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME);
			if (layerAttributes != null)
				{
				layerAttributes.setDeletable(deletable);
				layerAttributes.updateCommandManagerState();
				}
			}
		}
	}
