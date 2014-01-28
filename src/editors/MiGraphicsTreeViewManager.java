
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

/**----------------------------------------------------------------------------------------------
 * This class provides a simple look and feel manager for basic tree
 * hierarchy diagrams.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphicsTreeViewManager
			extends MiGraphicsBaseViewManager 
			implements MiiViewManager, MiiTypes
	{
	private		MiDistance	alleyHSpacing 	= 50;
	private		MiDistance	alleyVSpacing 	= 10;
	private		String		connectionType 	= null;
	private		MiMargins	margins 	= new MiMargins(100);



					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsTreeViewManager.
					 *------------------------------------------------------*/
	public				MiGraphicsTreeViewManager()
		{
		}
					/**------------------------------------------------------
		 			 * Loads the target container with graphics as specified
					 * in the given document.
					 * @param document	the document
					 * @see 		MiGraphicsBaseViewManager#setView
					 * @implements	MiiViewManager#setModel
					 *------------------------------------------------------*/
	public		void		setModel(MiiModelEntity document)
		{
		MiTreeGraphLayout layout = new MiTreeGraphLayout(MiiTypes.Mi_HORIZONTAL);
		layout.setAlleyHSpacing(alleyHSpacing);
		layout.setAlleyVSpacing(alleyVSpacing);
		layout.setMargins(margins);

		MiPart targetContainer = target;
		if ((target instanceof MiEditor) && (((MiEditor )target).hasLayers()))
			targetContainer = ((MiEditor )target).getCurrentLayer();
			
		targetContainer.setLayout(layout);

		this.document = document;
		parts = new MiParts();
		createGraphics(document);

//MiDebug.traceActions(parts.elementAt(0), MiiActionTypes.Mi_GEOMETRY_CHANGE_ACTION,
//MiDebug.Mi_LOG_PRINT_CHANGE_EVENT | MiDebug.Mi_LOG_PRINT_STACK | MiDebug.Mi_LOG_PRINT_BOUNDS);


		for (int i = 0; i < parts.size(); ++i)
			{
			if (parts.elementAt(i).getNumberOfContainers() == 0)
				{
				targetContainer.appendPart(parts.elementAt(i));
				}
			}
		targetContainer.validateLayout();
		targetContainer.setLayout(new MiSizeOnlyLayout());
		}
					/**------------------------------------------------------
					 * Converts the graphics found in the target container to 
					 * a MiiModelDocument. This creates entities in the given 
					 * document from the graphics in the target.
					 * @return 		the document
					 * @see 		MiGraphicsBaseViewManager#setView
					 * @implements		MiiViewManager#getModel
					 *------------------------------------------------------*/
	public		MiiModelEntity	 getModel()
		{
		MiiModelEntity document = super.getModel();
		// ---------------------------------------------------------------
		// Remove all 'connectedFrom' attributes
		// ---------------------------------------------------------------
		MiModelEntityList list = new MiModelEntityList(document.getModelEntities(), false);
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			entity.removeProperty(Mi_CONNECTED_FROM_NAME);
			}
		// ---------------------------------------------------------------
		// Replace all connections with 'connectedFrom' attributes
		// ---------------------------------------------------------------
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			if (equals(entity.getPropertyValue(Mi_TYPE_NAME), Mi_CONNECTION_TYPE_NAME))
				{
				MiiModelEntity dest = document.getModelEntity(
						entity.getPropertyValue(Mi_CONNECTION_DESTINATION_NAME));
				String src = entity.getPropertyValue(Mi_CONNECTION_SOURCE_NAME);
				if (dest.getPropertyValue(Mi_CONNECTED_FROM_NAME) == null)
					{
					dest.setPropertyValue(Mi_CONNECTED_FROM_NAME, src);
					}
				else
					{
					dest.setPropertyValue(Mi_CONNECTED_FROM_NAME, 
						dest.getPropertyValue(Mi_CONNECTED_FROM_NAME) + "," + src);
					}
				document.removeModelEntity(entity);
				}
			}
		return(document);
		}
	}

