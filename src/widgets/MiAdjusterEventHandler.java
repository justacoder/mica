
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

// For scrollbars background, sliders, interactive meters
/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiAdjusterEventHandler extends MiEventHandler
	{
	public static final	String		IncOneChunkEventName	= "incChunk";
	public static final	String		DecOneChunkEventName	= "decChunk";
	public static final	String		IncOnePageEventName	= "incPage";
	public static final	String		DecOnePageEventName	= "decPage";
	public static final	String		IncToMaxEventName	= "toMax";
	public static final	String		DecToMinEventName	= "toMin";
	public static final	String		MoveChunkName		= "moveChunk";
	public static final	String		DoubleMoveChunkName	= "doubleMoveChunk";
	public static final	String		TripleMoveChunkName	= "tripleMoveChunk";

	public					MiAdjusterEventHandler()
		{
		addEventToCommandTranslation(Mi_EXECUTE_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_EXECUTE_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(MoveChunkName, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(DoubleMoveChunkName, Mi_LEFT_MOUSE_DBLCLICK_EVENT, 0, 0);
		addEventToCommandTranslation(TripleMoveChunkName, Mi_LEFT_MOUSE_TRIPLECLICK_EVENT, 0, 0);
		addEventToCommandTranslation(IncOneChunkEventName, Mi_KEY_PRESS_EVENT, Mi_UP_ARROW_KEY, 0);
		addEventToCommandTranslation(DecOneChunkEventName, Mi_KEY_PRESS_EVENT, Mi_DOWN_ARROW_KEY, 0);
		addEventToCommandTranslation(IncOneChunkEventName, Mi_KEY_PRESS_EVENT, Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(DecOneChunkEventName, Mi_KEY_PRESS_EVENT, Mi_LEFT_ARROW_KEY, 0);
		addEventToCommandTranslation(IncOnePageEventName, Mi_KEY_PRESS_EVENT, Mi_PAGE_UP_KEY, 0);
		addEventToCommandTranslation(DecOnePageEventName, Mi_KEY_PRESS_EVENT, Mi_PAGE_DOWN_KEY, 0);
		addEventToCommandTranslation(IncToMaxEventName, Mi_KEY_PRESS_EVENT, Mi_UP_ARROW_KEY, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(DecToMinEventName, Mi_KEY_PRESS_EVENT, Mi_DOWN_ARROW_KEY, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(IncToMaxEventName, Mi_KEY_PRESS_EVENT, Mi_RIGHT_ARROW_KEY, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(DecToMinEventName, Mi_KEY_PRESS_EVENT, Mi_LEFT_ARROW_KEY, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(IncToMaxEventName, Mi_KEY_PRESS_EVENT, Mi_END_KEY, 0);
		addEventToCommandTranslation(DecToMinEventName, Mi_KEY_PRESS_EVENT, Mi_HOME_KEY, 0);
		}
	public		int		processCommand()
		{
		MiiAdjuster widget = (MiiAdjuster )event.getTarget();
/*
		if (widget.isDesignTime())
			{
			return(Mi_PROPOGATE_EVENT);
			}
*/

		if (isCommand(Mi_EXECUTE_COMMAND_NAME))
			{
			widget.setValueFromLocation(event.worldPt);
			}
		else if (isCommand(MoveChunkName))
			{
			widget.moveOneChunkTowardsLocation(event.worldPt);
			}
		else if (isCommand(DoubleMoveChunkName))
			{
			widget.moveOneChunkTowardsLocation(event.worldPt);
			widget.moveOneChunkTowardsLocation(event.worldPt);
			}
		else if (isCommand(TripleMoveChunkName))
			{
			widget.moveOneChunkTowardsLocation(event.worldPt);
			widget.moveOneChunkTowardsLocation(event.worldPt);
			widget.moveOneChunkTowardsLocation(event.worldPt);
			}
		else if (isCommand(IncOneChunkEventName))
			{
			widget.increaseOneChunk();
			}
		else if (isCommand(DecOneChunkEventName))
			{
			widget.decreaseOneChunk();
			}
		else if (isCommand(IncOnePageEventName))
			{
			widget.increaseOnePage();
			}
		else if (isCommand(DecOnePageEventName))
			{
			widget.decreaseOnePage();
			}
		else if (isCommand(IncToMaxEventName))
			{
			widget.increaseToMaximum();
			}
		else if (isCommand(DecToMinEventName))
			{
			widget.decreaseToMinimum();
			}
		return(Mi_CONSUME_EVENT);
		}
	}


