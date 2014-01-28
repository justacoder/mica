
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
public class MiIMouseEnterAndExit extends MiEventMonitor
	{
	private		MiParts 	insideList 	= new MiParts();
	private		MiParts 	newInsideList	= new MiParts();
	private		MiEvent 	enterExitEvent 	= new MiEvent();

	public				MiIMouseEnterAndExit()
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
		enterExitEvent.copy(event);
		enterExitEvent.locationSpecific = false;
		if (isCommand(Mi_MOUSE_MOVED_COMMAND_NAME))
			{
			enterExitEvent.setType(Mi_MOUSE_ENTER_EVENT);
			MiParts targetPath = event.getTargetPath();
			MiParts targetList = event.getTargetList();
			newInsideList.removeAllElements();
			for (int i = 0; i < targetPath.size(); ++i)
				{
				// If an object under the mouse wasn't under the mouse before...
				MiPart obj = targetPath.elementAt(i);
//MiDebug.println("obj in path = "  + obj);
				// If is under mouse and is a parent of the
				// object under the mouse...
				if (targetList.indexOf(obj) != -1)
					{
//MiDebug.println("obj in under mouse is ENTERED = "  + obj);
					newInsideList.addElement(obj);
					// If not already entered ....
					if (insideList.indexOf(obj) == -1)
						{
						// SENDING ENTER TO obj
						enterExitEvent.handlerTargetType = Mi_MONITOR_EVENT_HANDLER_TYPE;
					    	obj.dispatchEvent(enterExitEvent);
						enterExitEvent.handlerTargetType = Mi_ORDINARY_EVENT_HANDLER_TYPE;
;
					    	obj.dispatchEvent(enterExitEvent);
						}
					}
				}
			enterExitEvent.setType(Mi_MOUSE_EXIT_EVENT);
			for (int i = 0; i < insideList.size(); ++i)
				{
				// If an object that was under the mouse isn't under the mouse now...
				if (newInsideList.indexOf(insideList.elementAt(i)) == -1)
					{
					// SENDING EXIT TO insideList.elementAt(i)
					enterExitEvent.handlerTargetType = Mi_MONITOR_EVENT_HANDLER_TYPE;
				    	insideList.elementAt(i).dispatchEvent(enterExitEvent);
					enterExitEvent.handlerTargetType = Mi_ORDINARY_EVENT_HANDLER_TYPE;
					insideList.elementAt(i).dispatchEvent(enterExitEvent);
					}
				}

			insideList.removeAllElements();
			insideList.append(newInsideList);
			}
		else if (isCommand(Mi_MOUSE_EXITED_WINDOW_COMMAND_NAME))
			{
			enterExitEvent.setType(Mi_MOUSE_EXIT_EVENT);
			for (int i = 0; i < insideList.size(); ++i)
				{
				// SENDING EXIT TO insideList.elementAt(i)
				enterExitEvent.handlerTargetType = Mi_MONITOR_EVENT_HANDLER_TYPE;
			    	insideList.elementAt(i).dispatchEvent(enterExitEvent);
				enterExitEvent.handlerTargetType = Mi_ORDINARY_EVENT_HANDLER_TYPE;
				insideList.elementAt(i).dispatchEvent(enterExitEvent);
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}

