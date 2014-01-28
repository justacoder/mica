
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
class MiPushEventHandler extends MiTwoStateWidgetEventHandler
	{
	public			MiPushEventHandler()
		{
		super(true, false, false);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_LEFT_MOUSE_DOWN_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_LEFT_MOUSE_DBLCLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_LEFT_MOUSE_TRIPLECLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_WINDOW_EXIT_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_WINDOW_ENTER_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_KEY_PRESS_EVENT, ' ', 0);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_KEY_RELEASE_EVENT, ' ', 0);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_KEY_PRESS_EVENT, Mi_ENTER_KEY, 0);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_KEY_RELEASE_EVENT, Mi_ENTER_KEY, 0);
		}
	}

