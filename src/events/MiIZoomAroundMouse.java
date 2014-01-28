
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIZoomAroundMouse extends MiEventHandler
	{
	public static	double		DEFAULT_ZOOM_FACTOR		= 1.5;
	public static	double		DEFAULT_MAX_ZOOM_MAGNIFICATION	= 10;
	public static	boolean		DEFAULT_CENTER_WORLD_AT_POINT_OF_MOUSE_CLICK;

	private		double		zoomFactor			= DEFAULT_ZOOM_FACTOR;
	private		double		maxZoomMagnification		= DEFAULT_MAX_ZOOM_MAGNIFICATION;
	private		MiBounds	originalWorld			= new MiBounds();
	private		boolean		centerWorldAtPointOfMouseClick	= DEFAULT_CENTER_WORLD_AT_POINT_OF_MOUSE_CLICK;



	public				MiIZoomAroundMouse()
		{
		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_DBLCLICK_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_TRIPLECLICK_EVENT, 0, 0);

		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_CLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_DBLCLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_TRIPLECLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);

		// Supports click on MS Mouse scroll button
		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_CLICK_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_DBLCLICK_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_ZOOM_IN_COMMAND_NAME, Mi_MIDDLE_MOUSE_TRIPLECLICK_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);

		addEventToCommandTranslation(
			Mi_ZOOM_OUT_COMMAND_NAME, Mi_RIGHT_MOUSE_CLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_ZOOM_OUT_COMMAND_NAME, Mi_RIGHT_MOUSE_DBLCLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_ZOOM_OUT_COMMAND_NAME, Mi_RIGHT_MOUSE_TRIPLECLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);

		addEventToCommandTranslation(
			Mi_ZOOM_OUT_COMMAND_NAME, Mi_MIDDLE_PLUS_RIGHT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_ZOOM_OUT_COMMAND_NAME, Mi_MIDDLE_PLUS_RIGHT_MOUSE_DBLCLICK_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_ZOOM_OUT_COMMAND_NAME, Mi_MIDDLE_PLUS_RIGHT_MOUSE_TRIPLECLICK_EVENT, 0, 0);
		}

	public		void		setCenteringWorldAtPointOfMouseClick(boolean flag)
		{
		centerWorldAtPointOfMouseClick = flag;
		}
	public		boolean		getCenteringWorldAtPointOfMouseClick()
		{
		return(centerWorldAtPointOfMouseClick);
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
		MiEditor editor = event.editor;
		boolean zoomin = isCommand(Mi_ZOOM_IN_COMMAND_NAME);

		editor.getWorldBounds(originalWorld);
		MiBounds newBounds = new MiBounds();
		if (!zoom(editor, event.worldPt, newBounds, zoomin, zoomFactor, maxZoomMagnification,
			centerWorldAtPointOfMouseClick))
			{
			return(Mi_PROPOGATE_EVENT);
			}

		editor.setWorldBounds(newBounds);
		MiSystem.getViewportTransactionManager().appendTransaction(
			new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));


		return(Mi_CONSUME_EVENT);
		}
	public static 		boolean 	zoom(
							MiEditor editor, 
							MiPoint worldPt, 
							MiBounds newBounds, 
							boolean zoomIn, 
							double zoomFactor, 
							double maxZoomMagnification,
							boolean centerWorldAtPointOfMouseClick)
		{
		MiBounds world = editor.getWorldBounds();
		newBounds.copy(world);

		newBounds.setCenter(worldPt.x, worldPt.y);

		if (zoomIn)
			{
			newBounds.scale(1/zoomFactor, 1/zoomFactor);
			if (!centerWorldAtPointOfMouseClick)
				{
				newBounds.translate(
					worldPt.x - (worldPt.x - world.xmin)/zoomFactor - newBounds.xmin,
					worldPt.y - (worldPt.y - world.ymin)/zoomFactor - newBounds.ymin);
				}
			}
		else
			{
			newBounds.scale(zoomFactor, zoomFactor);
			if (!centerWorldAtPointOfMouseClick)
				{
				newBounds.translate(
					worldPt.x - (worldPt.x - world.xmin) * zoomFactor - newBounds.xmin,
					worldPt.y - (worldPt.y - world.ymin) * zoomFactor - newBounds.ymin);
				}
			}

		if ((world.getWidth()/newBounds.getWidth() > maxZoomMagnification)
			|| (world.getHeight()/newBounds.getHeight() > maxZoomMagnification))
			{
			return(false);
			}
	
		editor.getViewport().confineProposedWorldToConstraints(newBounds);
		return(!newBounds.equals(world));
		}

	private	static	int 		find_universeAtAnIntegralZoomLevel(
						MiEditor editor, 
						boolean returnNumZoomLevelsInCurrent_notNewLargerUniverse, 
						MiBounds world,
						MiBounds newUniverse,
						double zoomFactorBetweenLevels)
		{
		MiCoord 	xmin, ymin, xmax, ymax;
		MiCoord 	curleft, curright, curtop, curbottom;
		MiCoord		curxpos;
		MiCoord		curypos;
		MiBounds	universe;
		int		numZoomLevels = 0;
		int 		numZoomLevelsInNewLargerUniverse = 0;

		universe = editor.getUniverseBounds();

		// Center world space in universe.
		world.setCenter(universe.getCenterX(), universe.getCenterY());
		xmin = world.xmin;
		ymin = world.ymin;
		xmax = world.xmax;
		ymax = world.ymax;

		while ((xmin > universe.xmin) 
			|| (ymin > universe.ymin) 
			|| (xmax < universe.xmax) 
			|| (ymax < universe.ymax))
			{
			curxpos = (xmin + xmax)/2;
			curypos = (ymin + ymax)/2;

			curleft = curxpos - xmin;
			curright = xmax - curxpos;
			curbottom = curypos - ymin;
			curtop = ymax - curypos;

			curleft = curleft * zoomFactorBetweenLevels;
			curright = curright * zoomFactorBetweenLevels;
			curtop = curtop * zoomFactorBetweenLevels;
			curbottom = curbottom * zoomFactorBetweenLevels;

			xmin = curxpos - curleft;
			ymin = curypos - curbottom; 
			xmax = curxpos + curright; 
			ymax = curypos + curtop;

			// If new world size is still within universe, then we have another zoomlevel
			if ((xmin >= universe.xmin) 
				&& (ymin >= universe.ymin) 
				&& (xmax <= universe.xmax) 
				&& (ymax <= universe.ymax))
				{
				++numZoomLevels;
				}
			++numZoomLevelsInNewLargerUniverse;
			}
	
		newUniverse.setXmin(xmin);
		newUniverse.setYmin(ymin);
		newUniverse.setXmax(xmax);
		newUniverse.setYmax(ymax);

		if (returnNumZoomLevelsInCurrent_notNewLargerUniverse)
			return(numZoomLevels);
		return(numZoomLevelsInNewLargerUniverse);
		}
					/*sf------------------------------------------------------
					// Zoom out until we get a world that >= universe
					// then set universe to be equal to this world.
					// Return # of zoom levels between zoomed all the way out 
					// (all of universe visible) and current zoom level.
					/ef------------------------------------------------------*/
	public	static	int 		adjustUniverseToBeAnIntegralZoomLevel(MiEditor editor, double zoomFactorBetweenLevels)
		{
		MiBounds	newUniverse 	=	new MiBounds();
		int numZoomLevels = find_universeAtAnIntegralZoomLevel(editor, true, editor.getWorldBounds(), newUniverse, zoomFactorBetweenLevels);
		editor.setUniverseBounds(newUniverse);
		return(numZoomLevels);
		}


					/*sf------------------------------------------------------
					// Set amount of magnification between each zoom level.
					/ef------------------------------------------------------*/
	public 		void		setZoomFactor(double zf)
		{
		zoomFactor = zf;
		}

					/*sf------------------------------------------------------
					// Return amount of magnification between each zoom level.
					/ef------------------------------------------------------*/
	public 		double		getZoomFactor()
		{
		return(zoomFactor);
		}

					/*sf------------------------------------------------------
					// Specifiy maximum scale/magnification allowed
					// between zoomed all the way out (all of universe visible)
					// and zoomed all the way in (maximum zoom level).
					/ef------------------------------------------------------*/
	public 		void		setMaxZoomMagnification(double mag)
		{
		maxZoomMagnification = mag;
		}

					/*sf------------------------------------------------------
					// Return maximum scale/magnification allowed
					// between zoomed all the way out (all of universe visible)
					// and zoomed all the way in (maximum zoom level).
					/ef------------------------------------------------------*/
	public 		double		getMaxZoomMagnification()
		{
		return(maxZoomMagnification);
		}
	}


