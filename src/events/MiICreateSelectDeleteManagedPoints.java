
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
import java.util.Vector;
import java.awt.Color;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiICreateSelectDeleteManagedPoints extends MiEventHandler 
					implements MiiCommandNames, MiiTypes, MiiActionTypes, MiiManagedPointTypes
	{
	private		MiManagedPointManager	pointManagerKind 	= new MiConnectionPointManager();
	private		MiManagedPoint		prototypePoint;
	private		boolean			selectNewlyCreatedPoint	= true;
	private		MiParts			targets			= new MiParts();
	private		MiParts			normalLooks		= new MiParts();
	private		MiParts			selectedLooks		= new MiParts();
	private		Vector			selectedPointManagers	= new Vector();
	private		MiManagedPoints		selectedPoints		= new MiManagedPoints();
	private		Color			selectedColor		= MiColorManager.red;
	private		double			minSnapDistanceSquared	= 10*10;


	public				MiICreateSelectDeleteManagedPoints()
		{
		addEventToCommandTranslation(Mi_CREATE_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DELETE_COMMAND_NAME, Mi_KEY_EVENT, Mi_DELETE_KEY, 0);
		addEventToCommandTranslation(Mi_DESELECT_ALL_COMMAND_NAME, Mi_KEY_EVENT, Mi_ESC_KEY, 0);
		}

	public		void		setPointManagerKind(MiManagedPointManager manager)
		{
		pointManagerKind = manager;
		}
	public		MiManagedPointManager	getPointManagerKind()
		{
		return(pointManagerKind);
		}
	public		void		setPrototypePoint(MiManagedPoint pt)
		{
		prototypePoint = pt;
		}
	public		MiManagedPoint	getPrototypePoint()
		{
		return(prototypePoint);
		}
	public		void		setSelectNewlyCreatedPoint(boolean flag)
		{
		selectNewlyCreatedPoint = flag;
		}
	public		boolean		getSelectNewlyCreatedPoint()
		{
		return(selectNewlyCreatedPoint);
		}

	public		MiManagedPoints	getSelectedManagedPoints()
		{
		return(selectedPoints);
		}

	public		void		selectPoints(MiPart target, 
						MiManagedPointManager manager, MiManagedPoints managedPoints)
		{
		for (int i = 0; i < managedPoints.size(); ++i)
			{
			selectPoint(target, manager, managedPoints.elementAt(i));
			}
		}
	public		void		selectPoint(MiPart target, 
						MiManagedPointManager manager, MiManagedPoint managedPoint)
		{
		if (!manager.isMutable())
			{
			int index = manager.getManagedPoints().indexOf(managedPoint);
			manager = manager.copy();
			manager.setIsMutable(true);
			MiManagedPointManager.setManager(target, manager);
			managedPoint = manager.getManagedPoints().elementAt(index);
			}

		selectedPointManagers.addElement(manager);
		selectedPoints.addElement(managedPoint);
		MiPart look = managedPoint.getLook();
		normalLooks.addElement(look);
		if (look == null)
			look = manager.getLook();
		targets.addElement(target);
		MiPart selectedLook = createSelectedLookFromNormalLook(look);

		managedPoint.setLook(selectedLook);
		selectedLooks.addElement(selectedLook);
		target.invalidateArea();
		target.dispatchAction(Mi_MANAGED_POINT_SELECTED_ACTION);
		}
	public		void		deSelectPoint(MiManagedPoint managedPoint)
		{
		int index = selectedPoints.indexOf(managedPoint);

		if ((managedPoint.getLook() == selectedLooks.elementAt(index))
			&& (managedPoint.getLook().getAttributes() == selectedLooks.elementAt(index).getAttributes()))
			{
			managedPoint.setLook(normalLooks.elementAt(index));
			}
		normalLooks.removeElementAt(index);
		selectedLooks.removeElementAt(index);
		selectedPoints.removeElementAt(index);
		selectedPointManagers.removeElementAt(index);
		targets.elementAt(index).invalidateArea();
		targets.elementAt(index).dispatchAction(Mi_MANAGED_POINT_DESELECTED_ACTION);
		targets.removeElementAt(index);
		}
	public		void		deSelectAll()
		{
		while (selectedPoints.size() > 0)
			{
			deSelectPoint(selectedPoints.elementAt(0));
			}
		}
					/**------------------------------------------------------
	 				 * Processes the command generated from the current event.
					 * Both are stored in the MiEventHandler super class.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			MiEventHandler#isCommand
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		if (isCommand(Mi_CREATE_COMMAND_NAME))
			{
			MiPart target = null;
			MiParts targetPath = event.getTargetPath();
			MiManagedPointManager manager = null;
			for (int i = 0; i < targetPath.size(); ++i)
				{
				manager = MiManagedPointManager.getManager(
						targetPath.elementAt(i), pointManagerKind);
				if (manager != null)
					{
					target = targetPath.elementAt(i);
					break;
					}
				}
			if (target == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}

			MiManagedPoint managedPoint = manager.pick(target, event.getMouseFootPrint(new MiBounds()));
			if (managedPoint != null)
				{
				// Select the point
				if (selectedPoints.contains(managedPoint))
					deSelectPoint(managedPoint);
				else
					selectPoint(target, manager, managedPoint);
				return(Mi_CONSUME_EVENT);
				}

			// Create new ManagedPoint ...
			if (prototypePoint == null)
				{
				managedPoint = new MiManagedPoint();
				managedPoint.setLook(pointManagerKind.getLook());
				managedPoint.setRule(pointManagerKind.getRule());
				}
			else
				{
				managedPoint = prototypePoint.copy();
				}
			// Find closest standard location or snap to grid....
			MiPoint mouseLocation = new MiPoint();
			event.getWorldPoint(mouseLocation);

			MiPoint commonPtLocation = new MiPoint();
			MiPoint snapPoint = new MiPoint(mouseLocation);
			MiScale location = new MiScale();

			int connPtNumber = MiManagedPointManager.getClosestCommonPoint(
				target, mouseLocation, commonPtLocation);

			double commonPtDist = mouseLocation.getDistanceSquared(commonPtLocation);
			double snapPtDist = Mi_MAX_DISTANCE_VALUE;
			if (event.editor.snapMovingPoint(snapPoint))
				{
				snapPtDist = mouseLocation.getDistanceSquared(snapPoint);
				}

			if ((snapPtDist < commonPtDist) && (snapPtDist < minSnapDistanceSquared))
				{
				location.x = (snapPoint.x - target.getCenterX())/target.getWidth();
				location.y = (snapPoint.y - target.getCenterY())/target.getHeight();
				managedPoint.setLocation(location);
//System.out.println("\n\nSNAP POINT\n\n");
				}
			else if ((commonPtDist < snapPtDist) && (commonPtDist < minSnapDistanceSquared))
				{
				managedPoint.setPointNumber(connPtNumber);
//System.out.println("\n\nCOMMON POINT\n\n");
				}
			else
				{
				location.x = (mouseLocation.x - target.getCenterX())/target.getWidth();
				location.y = (mouseLocation.y - target.getCenterY())/target.getHeight();
				managedPoint.setLocation(location);
//System.out.println("\n\nLOCATION POINT\n\n");
				}
			if (manager.getLocalPoints() == null)
				manager.setLocalPoints(new MiManagedPoints());
			manager.getLocalPoints().addElement(managedPoint);
			target.invalidateArea();
			target.invalidateLayout();
			target.dispatchAction(Mi_MANAGED_POINT_ADDED_ACTION);
			return(Mi_CONSUME_EVENT);
			}
		else if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			MiPart target = null;
			MiParts targetPath = event.getTargetPath();
			MiManagedPointManager manager = null;
			for (int i = 0; i < targetPath.size(); ++i)
				{
				manager = MiManagedPointManager.getManager(
						targetPath.elementAt(i), pointManagerKind);
				if (manager != null)
					{
					target = targetPath.elementAt(i);
					break;
					}
				}
			if (target == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			MiManagedPoint managedPoint = manager.pick(target, event.getMouseFootPrint(new MiBounds()));
			if (managedPoint == null)
				{
				return(Mi_PROPOGATE_EVENT);
				}
			if (selectedPoints.contains(managedPoint))
				deSelectPoint(managedPoint);
			else
				selectPoint(target, manager, managedPoint);
			}
		else if (isCommand(Mi_DESELECT_ALL_COMMAND_NAME))
			{
			if (selectedPoints.size() == 0)
				return(Mi_PROPOGATE_EVENT);

			deSelectAll();
			}
		else if (isCommand(Mi_DELETE_COMMAND_NAME))
			{
			if (selectedPoints.size() == 0)
				return(Mi_PROPOGATE_EVENT);

			for (int i = 0; i < selectedPoints.size(); ++i)
				{
				MiManagedPointManager manager 
					= (MiManagedPointManager )selectedPointManagers.elementAt(i);
				manager.getLocalPoints().removeElement(selectedPoints.elementAt(i));

				targets.elementAt(i).invalidateArea();
				targets.elementAt(i).invalidateLayout();
				targets.elementAt(i).dispatchAction(Mi_MANAGED_POINT_REMOVED_ACTION);
				}
			selectedPointManagers.removeAllElements();
			selectedPoints.removeAllElements();
			normalLooks.removeAllElements();
			selectedLooks.removeAllElements();
			targets.removeAllElements();
			}
		return(Mi_CONSUME_EVENT);
		}
	protected	MiPart		createSelectedLookFromNormalLook(MiPart normalLook)
		{
		MiPart selectedLook = normalLook.deepCopy();
		selectedLook.setColor(selectedColor);
		for (int i = 0; i < selectedLook.getNumberOfParts(); ++i)
			selectedLook.getPart(i).setColor(selectedColor);
		return(selectedLook);
		}
	}

