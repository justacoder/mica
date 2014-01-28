
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

/**----------------------------------------------------------------------------------------------
 * This class implements the MiiViewManager interface
 * and supports the loading and saving of graphics palettes to and from
 * MiiModelDocuments. The target must be an instance of MiEditorPalette.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPaletteViewManager 
			extends MiGraphicsBaseViewManager 
			implements MiiViewManager, MiiTypes, MiiCommandNames,
			MiiActionHandler, MiiActionTypes
	{
	private static	MiViewManagerPostGraphicsCreationHandler 	postCreationHandler;




					/**------------------------------------------------------
	 				 * Constructs a new MiPaletteViewManager.
					 *------------------------------------------------------*/
	public				MiPaletteViewManager()
		{
		setPostGraphicsCreationHandler(postCreationHandler);
		}
	public static	void		setDefaultPostGraphicsCreationHandler(MiViewManagerPostGraphicsCreationHandler handler)
		{
		postCreationHandler = handler;
		}
	public static	MiViewManagerPostGraphicsCreationHandler	getDefaultPostGraphicsCreationHandler()
		{
		return(postCreationHandler);
		}
					/**------------------------------------------------------
		 			 * Sets the target container. This container will be loaded
					 * with graphics by the #setModel command and will be
					 * converted to a MiiModelEntity by the #getModel 
					 * command. The target must be an instanceof MiEditorPalette.
					 * @param target	the target container
					 * @implements	MiiViewManager#setView
					 * @see 	MiiViewManager#getView
					 *------------------------------------------------------*/
	public		void		setView(MiPart target)
		{
		if (this.target != null)
			this.target.removeActionHandlers(this);
		this.target = target;
		editorPalette = (MiEditorPalette )target;
		if (target != null)
			target.appendActionHandler(this, Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);
		}
					/**------------------------------------------------------
		 			 * Loads the target container with graphics as specified
					 * in the given document.
					 * @param document	the document
					 * @implements		MiiViewManager#setModel
					 * @see 		#setView
					 *------------------------------------------------------*/
	public		void		setModel(MiiModelEntity document)
		{
		subPalettes = null;
		editorPalette.setPalettes((MiContainer )null);
		semanticsTable.removeAllElements();
		attributesTable.removeAllElements();

		this.document = document;
		parts = new MiParts();
		createGraphics(document);
		if (!document.getEditingPermissions().isEditable())
			editorPalette.setLabelsAreEditable(false);
		else
			editorPalette.setLabelsAreEditable(true);

		editorPalette.setPalettes(subPalettes);
		//editorPalette.setContents(tree);
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
		MiiModelEntity document = super.getModel();
		MiModelEntityList list = document.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			entity.removeProperty(Mi_X_COORD_NAME);
			entity.removeProperty(Mi_Y_COORD_NAME);
			entity.removeProperty(Mi_WIDTH_NAME);
			entity.removeProperty(Mi_HEIGHT_NAME);
			entity.removeProperty(Mi_DRAG_AND_DROP_SOURCE_ATT_NAME);
			}
		return(document);
		}
	}

