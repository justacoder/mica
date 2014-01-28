
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
public class MiIMouseFocus extends MiEventMonitor
	{
	private		MiPart	armedObj;

	public				MiIMouseFocus()
		{
		addEventToCommandTranslation(Mi_MOUSE_MOVED_COMMAND_NAME, Mi_MOUSE_MOTION_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_MOUSE_EXITED_WINDOW_COMMAND_NAME, Mi_WINDOW_EXIT_EVENT, 0, 0);
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
		if (isCommand(Mi_MOUSE_MOVED_COMMAND_NAME))
			return(motion(event));
		else if (isCommand(Mi_MOUSE_EXITED_WINDOW_COMMAND_NAME))
			return(windowExit(event));
		return(Mi_PROPOGATE_EVENT);
		}

	public		int		motion(MiEvent event)
		{
		MiPart obj = null;

		MiParts targetPath = event.getTargetPath();
		MiParts targetList = event.getTargetList();
		for (int i = 0; i < targetPath.size(); ++i)
			{
			// If an object under the mouse wasn't under the mouse before...
			MiPart part = targetPath.elementAt(i);
//MiDebug.println("part in path = "  + part);
			// If is under mouse and is a parent of the
			// object under the mouse...
			if ((targetList.indexOf(part) != -1) &&
				(part.isAcceptingMouseFocus()))
				{
//MiDebug.println("part gets MOUSE FOCUS = "  + part);
				obj = part;
				break;
				}
			// FIX: there shouldbe a better way to specify that parts behind
			// a part sometimes do and sometimes don't want mouse focus. Use
			// an unrelated opaqueRectangle flag?
			// Don't set focus to things behind a heavy-duty part...
			if (part.getDrawManager() != null)
				{
				break;
				}
			}

		if ((obj != null) && (obj == armedObj))
			{
			// Found a candidate, if same as current, exit.
			return(Mi_PROPOGATE_EVENT);
			}

		// Different or no object found to arm. Disarm old one.
		if (armedObj != null)
			{
			//MiDebug.println("Removing mouse focus from: " + armedObj);
			armedObj.setMouseFocus(false);
			armedObj = null;
			}

		// If an object was found to arm, arm it.
		if (obj != null)
			{
			armedObj = obj;
			//MiDebug.println("Assigning mouse focus to: " + armedObj);
			armedObj.setMouseFocus(true);
			}

		return(Mi_PROPOGATE_EVENT);
		}
	public		int	 	windowExit(MiEvent event)
		{
		if (armedObj != null)
			{
			//MiDebug.println("Removing mouse focus from: " + armedObj);
			armedObj.setMouseFocus(false);
			armedObj = null;
			}

		return(Mi_PROPOGATE_EVENT);
		}
	}


