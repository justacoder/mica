
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
import java.awt.Frame; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Strings; 

/**----------------------------------------------------------------------------------------------
 * Palette, ...
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDockingPanelManager implements MiiTypes, MiiActionHandler, MiiActionTypes
	{
	public static final int		Mi_BOTTOM_DOCKING_PANEL_INDEX	= 0;
	public static final int		Mi_TOP_DOCKING_PANEL_INDEX	= 1;
	public static final int		Mi_LEFT_DOCKING_PANEL_INDEX	= 2;
	public static final int		Mi_RIGHT_DOCKING_PANEL_INDEX	= 3;

	private		MiDockingPanel[]	dockingPanels;
	private		MiPart			dockingPanelContainer;


	public				MiDockingPanelManager(
						MiPart dockingPanelContainer, 
						MiDockingPanel[] dockingPanels)
		{
		this.dockingPanels = dockingPanels;
		this.dockingPanelContainer = dockingPanelContainer;

		dockingPanelContainer.setIsDragAndDropTarget(true);

		dockingPanelContainer.appendActionHandler(this, 
			Mi_DATA_IMPORT_ACTION | Mi_REQUEST_ACTION_PHASE);
		dockingPanelContainer.appendActionHandler(this, 
			Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);

		}
					/**------------------------------------------------------
					 * Processes the given action.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 			false if it is NOT OK to send
					 *			action to the next action handler
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_DATA_IMPORT_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			// ---------------------------------------------------------------
			// Reject the dropping of the toolbar if there isn't a dockingPanel
			// nearby or if it is dropped next to the docking panel it is already
			// docked to. Reject all other parts that are dropped in the window.
			// Override this processAction to handle drops of other parts whose
			// drops should not be vetoed.
			// ---------------------------------------------------------------
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();

			if (!(transferOp.getSource() instanceof MiToolBar))
				{
				action.setVetoed(true);
				return(false);
				}
			MiDockingPanel dockingPanel 
				= getClosestPanel(dockingPanels, transferOp.getLookTargetPosition());

			if ((dockingPanel == null) 
				|| (dockingPanel.isContainerOf(transferOp.getSource())))
				{
				action.setVetoed(true);
				return(false);
				}
			}
		else if (action.hasActionType(Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE))
			{
			// ---------------------------------------------------------------
			// Handle drops of the toolbar, attaching  it to the closest docking panel.
			// ---------------------------------------------------------------
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();

			if (transferOp.getSource() instanceof MiToolBar)
				{
				MiDockingPanel panel = getClosestPanel(
					dockingPanels, transferOp.getLookTargetPosition());
				if (panel != null)
					{
					transferOp.getSource().removeSelf();
					panel.appendPart(
						transferOp.getSource(), 
						transferOp.getLookTargetPosition());
					transferOp.setTransferredData(transferOp.getSource());
					}
				}
			return(false);
			}
/***** This rotates the toolBar in it's origonal location, unless we make a copy, whcih is slow.
		if (action.hasActionType(Mi_DRAG_AND_DROP_MOVE_ACTION))
			{
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();

			// ---------------------------------------------------------------
			// Observe the toolbar as it is being moved, swapping its orientation
			// to/from horizontal/vertical as it gets closer to a particular 
			// docking panel.
			// ---------------------------------------------------------------
			if (transferOp.getSource() instanceof MiToolBar)
				{
				int orient;
				MiDockingPanel dockingPanel 
					= getClosestPanel(transferOp.getLookTargetPosition());
				if (dockingPanel != null)
					{
					orient = dockingPanel.getOrientation();

					MiPart draggedPart = transferOp.getLook();
					MiToolBar tb = null;
					if (draggedPart instanceof MiToolBar)
						{
						tb = (MiToolBar )draggedPart;
						}
					else if ((draggedPart instanceof MiReference) 
						&& (draggedPart.getPart(0) instanceof MiToolBar))
						{
						tb = (MiToolBar )draggedPart.getPart(0);
						}

					tb.setOrientation(orient);
					if (orient == Mi_HORIZONTAL)
						tb.setCenterY(transferOp.getLookTargetPosition().y);
					else
						tb.setCenterX(transferOp.getLookTargetPosition().x);
					}
				}
			}
*****/

		return(true);
		}
					/**------------------------------------------------------
					 * Gets the MiDockingPanel closest to the given point.
					 * Examine the default 4 docking panels, one on each side
					 * of the window.
					 * @param pt		the given point
					 * @return 		the closest docking panel or null
					 *------------------------------------------------------*/
	protected	MiDockingPanel	getClosestPanel(MiDockingPanel[] dockingPanels, MiPoint pt)
		{
		MiDistance dist;
		MiDistance minDistance = Mi_MAX_DISTANCE_VALUE;
		MiDockingPanel dockingPanel = null;

		if (dockingPanels[Mi_LEFT_DOCKING_PANEL_INDEX] != null)
			{
			MiDockingPanel leftDockingPanel = dockingPanels[Mi_LEFT_DOCKING_PANEL_INDEX];

			if (leftDockingPanel.getBounds().isReversed())
				dist = pt.x - dockingPanelContainer.getXmin();
			else
				dist = Math.sqrt(leftDockingPanel.getBounds().getDistanceSquaredToClosestEdge(pt));
			if (dist < 0)
				dist = -dist;
			if (dist < minDistance)
				{
				minDistance = dist;
				dockingPanel = leftDockingPanel;
				}
			}
		if (dockingPanels[Mi_TOP_DOCKING_PANEL_INDEX] != null)
			{
			MiDockingPanel topDockingPanel = dockingPanels[Mi_TOP_DOCKING_PANEL_INDEX];

			if (topDockingPanel.getBounds().isReversed())
				dist = pt.y - dockingPanelContainer.getYmax();
			else
				dist = Math.sqrt(topDockingPanel.getBounds().getDistanceSquaredToClosestEdge(pt));
			if (dist < 0)
				dist = -dist;
			if (dist < minDistance)
				{
				minDistance = dist;
				dockingPanel = topDockingPanel;
				}
			}
		if (dockingPanels[Mi_RIGHT_DOCKING_PANEL_INDEX] != null)
			{
			MiDockingPanel rightDockingPanel = dockingPanels[Mi_RIGHT_DOCKING_PANEL_INDEX];

			if (rightDockingPanel.getBounds().isReversed())
				dist = pt.x - dockingPanelContainer.getXmax();
			else
				dist = Math.sqrt(rightDockingPanel.getBounds().getDistanceSquaredToClosestEdge(pt));
			if (dist < 0)
				dist = -dist;
			if (dist < minDistance)
				{
				minDistance = dist;
				dockingPanel = rightDockingPanel;
				}
			}
		if (dockingPanels[Mi_BOTTOM_DOCKING_PANEL_INDEX] != null)
			{
			MiDockingPanel bottomDockingPanel = dockingPanels[Mi_BOTTOM_DOCKING_PANEL_INDEX];

			if (bottomDockingPanel.getBounds().isReversed())
				dist = pt.y - dockingPanelContainer.getYmin();
			else
				dist = Math.sqrt(bottomDockingPanel.getBounds().getDistanceSquaredToClosestEdge(pt));

			if (dist < 0)
				dist = -dist;
			if (dist < minDistance)
				{
				minDistance = dist;
				dockingPanel = bottomDockingPanel;
				}
			}
		return(dockingPanel);
		}
	}


