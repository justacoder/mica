
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Toolkit                   *
 ***************************************************************************
 * NOTICE: Permission to use, copy, modify and distribute this software    *
 * and its documentation, for commercial or non-commercial purposes, is    *
 * hereby granted provided that this notice appears in all copies.         *
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

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDrawingVisibilityToolBar extends MiToolBar implements MiiCommandNames, MiiPropertyChangeHandler
	{
	public static final int 	Mi_RULERS_SHOWN_ACTION	
						= MiActionManager.registerAction("Mi_RULERS_SHOWN_ACTION");
	public static final int 	Mi_RULERS_HIDDEN_ACTION 	
						= MiActionManager.registerAction("Mi_RULERS_HIDDEN_ACTION");
	public static final int 	Mi_GRID_SHOWN_AND_ENABLED_ACTION	
						= MiActionManager.registerAction("Mi_GRID_SHOWN_AND_ENABLED_ACTION");
	public static final int 	Mi_GRID_HIDDEN_AND_DISABLED_ACTION 	
						= MiActionManager.registerAction("Mi_GRID_HIDDEN_AND_DISABLED_ACTION");
	public static final int 	Mi_GRID_SHOWN_ACTION	
						= MiActionManager.registerAction("Mi_GRID_SHOWN_ACTION");
	public static final int 	Mi_GRID_HIDDEN_ACTION 	
						= MiActionManager.registerAction("Mi_GRID_HIDDEN_ACTION");
	public static final int 	Mi_PAGE_SHOWN_ACTION	
						= MiActionManager.registerAction("Mi_PAGE_SHOWN_ACTION");
	public static final int 	Mi_PAGE_HIDDEN_ACTION 	
						= MiActionManager.registerAction("Mi_PAGE_HIDDEN_ACTION");

	public static 	String		DISPLAY_RULERS_STATUS_MSG		
						= "Displays/hides the rulers";
	public static 	String		DISPLAY_RULERS_DISABLED_STATUS_MSG	
						= "Displays/hides the rulers";
	public static 	String		DISPLAY_PAGE_STATUS_MSG			
						= "Displays/hides the (printable) page background";
	public static 	String		DISPLAY_PAGE_DISABLED_STATUS_MSG	
						= "Displays/hides the (printable) page background";
	public static 	String		DISPLAY_GRID_PTS_STATUS_MSG
						= "Displays/hides the grid points";
	public static 	String		DISPLAY_GRID_PTS_DISABLED_STATUS_MSG
						= "Displays/hides the grid points";
	public static 	String		ENABLE_GRID_PTS_STATUS_MSG
						= "Enables/disables the snapability of grid points";
	public static 	String		ENABLE_GRID_PTS_DISABLED_STATUS_MSG
						= "Enables/disables the snapability of grid points";
	public static 	String		DISPLAY_CONN_PTS_STATUS_MSG
						= "Displays/hides all connection points";
	public static 	String		DISPLAY_CONN_PTS_DISABLED_STATUS_MSG
						= "Displays/hides all connection points";
	public static 	String		DISPLAY_ANNO_PTS_STATUS_MSG
						= "Displays/hides all annotation points";
	public static 	String		DISPLAY_ANNO_PTS_DISABLED_STATUS_MSG
						= "Displays/hides all annotation points";


	public static final String	DISPLAY_RULERS_COMMAND_NAME		= "displayRulers";
	public static final String	HIDE_RULERS_COMMAND_NAME		= "hideRulers";

	public static final String	DISPLAY_PAGE_COMMAND_NAME		= "displayPage";
	public static final String	HIDE_PAGE_COMMAND_NAME			= "hidePage";

	public static final String	DISPLAY_GRID_PTS_COMMAND_NAME		= "displayGridPts";
	public static final String	HIDE_GRID_PTS_COMMAND_NAME		= "hideGridPts";

	public static final String	ENABLE_GRID_PTS_COMMAND_NAME		= "enableGridPts";
	public static final String	DISABLE_GRID_PTS_COMMAND_NAME		= "disableGridPts";

	public static final String	DISPLAY_CONN_PTS_COMMAND_NAME		= "displayConnPts";
	public static final String	HIDE_CONN_PTS_COMMAND_NAME		= "hideConnPts";

	public static final String	DISPLAY_ANNO_PTS_COMMAND_NAME		= "displayAnnoPts";
	public static final String	HIDE_ANNO_PTS_COMMAND_NAME		= "hideAnnoPts";

	public static final String	RULERS_VISIBILITY_CHANGED_CMD_NAME	= "rulerVisibilityChanged";

	public static final String	Mi_CONNECTION_POINT_VISIBILITY_CHANGED_CMD_NAME	= "connPtVisibilityChanged";
	public static final String	Mi_ANNOTATION_POINT_VISIBILITY_CHANGED_CMD_NAME	= "annoPtVisibilityChanged";

	public static final String	Mi_MANAGED_POINTS_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME	
									= "managedPointAttDialogChanged";

	private		MiDrawingToolBarManager		toolBarManager;
	private		MiManagedPointsAttributesDialog	managedPointsAttributesDialog;

	private		MiPageManager	pageManager;
	private		MiRulerBox	rulerBox;

	private		MiWidget	rulerToggleButton;
	private		MiWidget	pageVisibleToggleButton;
	private		MiWidget	gridVisibilityToggleButton;
	private		MiWidget	gridVisibleAndEnabledToggleButton;
	private		MiWidget	connPointToggleButton;
	private		MiWidget	annoPointToggleButton;


	protected			MiDrawingVisibilityToolBar()
		{
		// Used for copy operation.
		}
	public				MiDrawingVisibilityToolBar(
						MiEditor editor, 
						MiiCommandManager manager)
		{
		super(editor, manager);
		setup();
		}
	public		void		setPageManager(MiPageManager pageManager)
		{
		this.pageManager = pageManager;
		if (pageManager != null)
			{
			pageManager.appendPropertyChangeHandler(
				this, new Strings(Mi_ANY_PROPERTY), 
				MiiModelEntity.Mi_MODEL_CHANGE_COMMIT_PHASE.getMask());

			pageVisibleToggleButton.setVisible(pageManager.getDrawingPages() != null);
			if (pageVisibleToggleButton.isVisible())
				pageVisibleToggleButton.select(pageManager.getDrawingPages().isVisible());

			gridVisibilityToggleButton.setVisible(pageManager.getDrawingGrid() != null);
			if (gridVisibilityToggleButton.isVisible())
				gridVisibilityToggleButton.select(pageManager.getDrawingGrid().isVisible());

			gridVisibleAndEnabledToggleButton.setVisible(pageManager.getSnapManager() != null);
			if (gridVisibleAndEnabledToggleButton.isVisible())
				gridVisibleAndEnabledToggleButton.select(pageManager.getSnapManager().isEnabled());
			}
		else
			{
			gridVisibilityToggleButton.setVisible(false);
			gridVisibleAndEnabledToggleButton.setVisible(false);
			}
		}
	// FIX: do this by name in a Strings()
	public		void		setEnabledButtons(
						boolean pageVisibleToggleButtonFlag,
						boolean gridVisibilityToggleButtonFlag,
						boolean gridVisibleAndEnabledToggleButtonFlag)
		{
		pageVisibleToggleButton.setVisible(pageVisibleToggleButtonFlag);
		gridVisibilityToggleButton.setVisible(gridVisibilityToggleButtonFlag);
		gridVisibleAndEnabledToggleButton.setVisible(gridVisibleAndEnabledToggleButtonFlag);
		}
					
/**
	public		void		setDrawingGrid(MiDrawingGrid drawingGrid)
		{
		this.drawingGrid = drawingGrid;
		if (drawingGrid != null)
			{
			gridVisibilityToggleButton.setVisible(true);
			drawingGrid.appendCommandHandler(this,
			 	GRID_VISIBILITY_CHANGED_CMD_NAME, Mi_VISIBLE_ACTION);
			gridToggleButton.select(drawingGrid.isVisible());
			}
		else
			{
			gridToggleButton.setVisible(false);
			}
		}
	public		MiDrawingGrid	getDrawingGrid()
		{
		return(drawingGrid);
		}
***/
	public		MiPart		getGridToggleButton()
		{
		return(gridVisibilityToggleButton);
		}
	public		MiPart		getRulerToggleButton()
		{
		return(rulerToggleButton);
		}
	public		MiPart		getConnPointToggleButton()
		{
		return(connPointToggleButton);
		}
	public		MiPart		getAnnoPointToggleButton()
		{
		return(annoPointToggleButton);
		}
	public		void		setRulerBox(MiRulerBox rulerBox)
		{
		this.rulerBox = rulerBox;
		if (rulerBox != null)
			{
			rulerToggleButton.setVisible(true);

			rulerToggleButton.select(rulerBox.getHorizontalRuler().isVisible());
			rulerBox.getHorizontalRuler().appendCommandHandler(this,
			 	RULERS_VISIBILITY_CHANGED_CMD_NAME, Mi_VISIBLE_ACTION);
			rulerBox.getHorizontalRuler().appendCommandHandler(this,
			 	RULERS_VISIBILITY_CHANGED_CMD_NAME, Mi_INVISIBLE_ACTION);
			}
		else
			{
			rulerToggleButton.setVisible(false);
			}
		}
	public		MiRulerBox	getRulerBox()
		{
		return(rulerBox);
		}

	public		void		setToolBarManager(MiDrawingToolBarManager manager)
		{
		this.toolBarManager = manager;
		}
	public		void		setManagedPointsAttributesDialog(MiManagedPointsAttributesDialog dialog)
		{
		managedPointsAttributesDialog = dialog;
		managedPointsAttributesDialog.appendCommandHandler(
			this, Mi_MANAGED_POINTS_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		}

	protected	void		setup()
		{
		processCommand(Mi_HIDE_LABELS_COMMAND_NAME);

		MiConnectionPointManager.setGloballyHidden(false);
		MiAnnotationPointManager.setGloballyHidden(false);

		MiPart pageVisibilityImage = new MiImage("${Mi_IMAGES_HOME}/pageVisibility.xpm", true);
		MiPart gridPtVisibilityImage = new MiImage("${Mi_IMAGES_HOME}/gridPointVisibility.xpm", true);
		MiPart connPtVisibilityImage = new MiImage("${Mi_IMAGES_HOME}/connPointVisibility.xpm", true);
		MiPart annoPtVisibilityImage = new MiImage("${Mi_IMAGES_HOME}/annoPointVisibility.xpm", true);
		MiPart rulersVisibilityImage = new MiImage("${Mi_IMAGES_HOME}/rulersVisibility.xpm", true);

		appendSpacer();
		rulerToggleButton = appendBooleanToolItem("Display Rulers", 
								rulersVisibilityImage, this, 
								DISPLAY_RULERS_COMMAND_NAME,
								HIDE_RULERS_COMMAND_NAME);
		pageVisibleToggleButton = appendBooleanToolItem("Display Page", 
								pageVisibilityImage, this, 
								DISPLAY_PAGE_COMMAND_NAME,
								HIDE_PAGE_COMMAND_NAME);
		gridVisibilityToggleButton = appendBooleanToolItem("Display Grid", 
								gridPtVisibilityImage, this, 
								DISPLAY_GRID_PTS_COMMAND_NAME,
								HIDE_GRID_PTS_COMMAND_NAME);
		gridVisibleAndEnabledToggleButton = appendBooleanToolItem("Enable Snap Grid", 
								gridPtVisibilityImage.copy(), this, 
								ENABLE_GRID_PTS_COMMAND_NAME,
								DISABLE_GRID_PTS_COMMAND_NAME);
		connPointToggleButton = appendBooleanToolItem("Display Connection Points", 
								connPtVisibilityImage, this, 
								DISPLAY_CONN_PTS_COMMAND_NAME,
								HIDE_CONN_PTS_COMMAND_NAME);
		annoPointToggleButton = appendBooleanToolItem("Display Annotation Points", 
								annoPtVisibilityImage, this, 
								DISPLAY_ANNO_PTS_COMMAND_NAME,
								HIDE_ANNO_PTS_COMMAND_NAME);
		appendSpacer();


		setHelpMessages(DISPLAY_RULERS_COMMAND_NAME,
			DISPLAY_RULERS_STATUS_MSG,
			DISPLAY_RULERS_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(DISPLAY_PAGE_COMMAND_NAME,
			DISPLAY_PAGE_STATUS_MSG,
			DISPLAY_PAGE_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(DISPLAY_GRID_PTS_COMMAND_NAME,
			DISPLAY_GRID_PTS_STATUS_MSG,
			DISPLAY_GRID_PTS_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(ENABLE_GRID_PTS_COMMAND_NAME,
			ENABLE_GRID_PTS_STATUS_MSG,
			ENABLE_GRID_PTS_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(DISPLAY_CONN_PTS_COMMAND_NAME,
			DISPLAY_CONN_PTS_STATUS_MSG,
			DISPLAY_CONN_PTS_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(DISPLAY_ANNO_PTS_COMMAND_NAME,
			DISPLAY_ANNO_PTS_STATUS_MSG,
			DISPLAY_ANNO_PTS_DISABLED_STATUS_MSG,
			null);


		setToolItemImageSizes(new MiSize(24, 24));

		connPointToggleButton.select(!MiConnectionPointManager.isGloballyHidden());
		annoPointToggleButton.select(!MiAnnotationPointManager.isGloballyHidden());
		}

					/**------------------------------------------------------
	 				 * Process the change of a property of this editor's
					 * model (which comprises the preferences).
					 * @param event		the change event
					 *------------------------------------------------------*/
	public		void		processPropertyChange(MiPropertyChange event)
		{
		if (event.getPropertyName().equals(MiDrawingGrid.Mi_GRID_VISIBLE_NAME_NSP))
			{
			gridVisibilityToggleButton.select(Utility.toBoolean(event.getPropertyValue()));
			}
		else if (event.getPropertyName().equals(MiPageManager.Mi_SNAP_TO_GRID_ENABLED_NAME))
			{
			gridVisibleAndEnabledToggleButton.select(Utility.toBoolean(event.getPropertyValue()));
			}
		else if (event.getPropertyName().equals(MiPageManager.Mi_VISIBLE_NAME))
			{
			pageVisibleToggleButton.select(Utility.toBoolean(event.getPropertyValue()));
			}
		}
	public		void		processCommand(String cmd)
		{
//MiDebug.println(this + " processcmd: " + cmd);
		if (cmd.equalsIgnoreCase(DISPLAY_RULERS_COMMAND_NAME))
			{
			rulerBox.setPropertyValue(Mi_VISIBLE_NAME, "true");
			dispatchAction(Mi_RULERS_SHOWN_ACTION);
			}
		else if (cmd.equalsIgnoreCase(HIDE_RULERS_COMMAND_NAME))
			{
			rulerBox.setPropertyValue(Mi_VISIBLE_NAME, "false");
			dispatchAction(Mi_RULERS_HIDDEN_ACTION);
			}
		else if (cmd.equalsIgnoreCase(RULERS_VISIBILITY_CHANGED_CMD_NAME))
			{
			rulerToggleButton.select(rulerBox.getHorizontalRuler().isVisible());
			}
		else if (cmd.equalsIgnoreCase(DISPLAY_PAGE_COMMAND_NAME))
			{
			pageManager.setPropertyValue(Mi_VISIBLE_NAME, "true");
			dispatchAction(Mi_PAGE_SHOWN_ACTION);
			}
		else if (cmd.equalsIgnoreCase(HIDE_PAGE_COMMAND_NAME))
			{
			pageManager.setPropertyValue(Mi_VISIBLE_NAME, "false");
			dispatchAction(Mi_PAGE_HIDDEN_ACTION);
			}
		else if (cmd.equalsIgnoreCase(DISPLAY_GRID_PTS_COMMAND_NAME))
			{
			pageManager.getDrawingGrid().setVisible(true);
			//pageManager.setPropertyValue(MiDrawingGrid.Mi_GRID_VISIBLE_NAME_NSP, "true");
			dispatchAction(Mi_GRID_SHOWN_ACTION);
			}
		else if (cmd.equalsIgnoreCase(HIDE_GRID_PTS_COMMAND_NAME))
			{
			pageManager.getDrawingGrid().setVisible(false);
			//pageManager.setPropertyValue(MiDrawingGrid.Mi_GRID_VISIBLE_NAME_NSP, "false");
			dispatchAction(Mi_GRID_HIDDEN_ACTION);
			}
		else if (cmd.equalsIgnoreCase(ENABLE_GRID_PTS_COMMAND_NAME))
			{
			pageManager.getSnapManager().setEnabled(true);
			pageManager.setPropertyValue(MiDrawingGrid.Mi_GRID_VISIBLE_NAME_NSP, "true");
			dispatchAction(Mi_GRID_SHOWN_AND_ENABLED_ACTION);
			}
		else if (cmd.equalsIgnoreCase(DISABLE_GRID_PTS_COMMAND_NAME))
			{
			pageManager.getSnapManager().setEnabled(false);
			pageManager.setPropertyValue(MiDrawingGrid.Mi_GRID_VISIBLE_NAME_NSP, "false");
			dispatchAction(Mi_GRID_HIDDEN_AND_DISABLED_ACTION);
			}
		else if (cmd.equalsIgnoreCase(DISPLAY_CONN_PTS_COMMAND_NAME))
			{
			MiConnectionPointManager.setGloballyHidden(false);
			invalidateAllPartsWithConnectionPoints(getEditor().getRootWindow());
			}
		else if (cmd.equalsIgnoreCase(HIDE_CONN_PTS_COMMAND_NAME))
			{
			MiConnectionPointManager.setGloballyHidden(true);
			invalidateAllPartsWithConnectionPoints(getEditor().getRootWindow());
			}
		else if (cmd.equalsIgnoreCase(Mi_CONNECTION_POINT_VISIBILITY_CHANGED_CMD_NAME))
			{
			connPointToggleButton.select(!MiConnectionPointManager.isGloballyHidden());
			}
		else if (cmd.equalsIgnoreCase(DISPLAY_ANNO_PTS_COMMAND_NAME))
			{
			MiAnnotationPointManager.setGloballyHidden(false);
			invalidateAllPartsWithAnnotationPoints(getEditor().getRootWindow());
			}
		else if (cmd.equalsIgnoreCase(HIDE_ANNO_PTS_COMMAND_NAME))
			{
			MiAnnotationPointManager.setGloballyHidden(true);
			invalidateAllPartsWithAnnotationPoints(getEditor().getRootWindow());
			}
		else if (cmd.equalsIgnoreCase(Mi_ANNOTATION_POINT_VISIBILITY_CHANGED_CMD_NAME))
			{
			connPointToggleButton.select(!MiAnnotationPointManager.isGloballyHidden());
			}
		else 
			{
			super.processCommand(cmd);
			}
		}
	protected	void		invalidateAllPartsWithConnectionPoints(MiPart container)
		{
		// Tell all parts to recalc their drawBounds
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if (part.getConnectionPointManager() != null)
				{
				part.invalidateLayout();
				// Invalidate area because just hiding conn pts instead of making 
				// them invisible does not change bounds
				part.invalidateArea();
				}
			invalidateAllPartsWithConnectionPoints(part);
			}
		}
	protected	void		invalidateAllPartsWithAnnotationPoints(MiPart container)
		{
		// Tell all parts to recalc their drawBounds
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if (part.getAnnotationPointManager() != null)
				{
				// Invalidate area because just hiding conn pts instead of making 
				// them invisible does not change bounds
				part.invalidateArea();
				part.invalidateLayout();
				}
			invalidateAllPartsWithAnnotationPoints(part);
			}
		}
	}

