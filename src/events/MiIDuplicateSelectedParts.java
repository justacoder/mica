
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

import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIDuplicateSelectedParts extends MiEventHandler implements MiiActionTypes
	{
	private		MiDeletePartsCommand	duplicateSelectedObjects	= new MiDeletePartsCommand();
	private		boolean			grabbed;
	private		String			numDuplicationsStr;
	private		int			maxNumDuplications		= 10;
	private		String			duplicateCommandName;
	private		String			defaultDuplicateCommandName = MiDeletePartsCommand.Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME;


	public				MiIDuplicateSelectedParts()
		{
		addEventToCommandTranslation(
			Mi_EXECUTE_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'd', Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_CANCEL_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(
			MiDeletePartsCommand.Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'h', Mi_NO_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(
			MiDeletePartsCommand.Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'v', Mi_NO_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(
			MiDeletePartsCommand.Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'd', Mi_NO_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(
			MiDeletePartsCommand.Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'r', Mi_NO_MODIFIERS_HELD_DOWN);
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
		if (isCommand(Mi_EXECUTE_COMMAND_NAME))
			{
			if (!grabbed)
				{
				event.editor.prependGrabEventHandler(this);
				grabbed = true;
				numDuplicationsStr = "0";
				duplicateCommandName = defaultDuplicateCommandName;
				}
			else
				{
				// ctrl-d ctrl-d
				processEvent(new MiEvent(Mi_KEY_PRESS_EVENT, '1', Mi_NO_MODIFIERS_HELD_DOWN));
				processEvent(new MiEvent(Mi_KEY_PRESS_EVENT, Mi_ENTER_KEY, Mi_NO_MODIFIERS_HELD_DOWN));
				}
			return(Mi_CONSUME_EVENT);
			}
		if (isCommand(MiDeletePartsCommand.Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME))
			{
			duplicateCommandName = MiDeletePartsCommand.Mi_DUPLICATE_COMMAND_NAME;
			}
		if (grabbed)
			{
			if (isCommand(MiDeletePartsCommand.Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME))
				{
				duplicateCommandName = MiDeletePartsCommand.Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME;
				}
			else if (isCommand(MiDeletePartsCommand.Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME))
				{
				duplicateCommandName = MiDeletePartsCommand.Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME;
				}
			else if (isCommand(MiDeletePartsCommand.Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME))
				{
				duplicateCommandName = MiDeletePartsCommand.Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME;
				}
			else if (isCommand(MiDeletePartsCommand.Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME))
				{
				duplicateCommandName = MiDeletePartsCommand.Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME;
				}
			else if (isCommand(Mi_CANCEL_COMMAND_NAME))
				{
				grabbed = false;
				event.editor.removeGrabEventHandler(this);
				}
			return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}
	public		int		processEvent(MiEvent event)
		{
		if (grabbed)
			{
//MiDebug.println(this + "processvent: " + event);
//MiDebug.println(this + "processvent: " + event.key);
//MiDebug.println(this + "processvent: Mi_ENTER_KEY" + Mi_ENTER_KEY);
			if (event.type == Mi_KEY_PRESS_EVENT)
				{
				if ((event.key >= '0') && (event.key <= '9'))
					{
//MiDebug.println(this + "processvent: numDuplicationsStr " + numDuplicationsStr);
//MiDebug.println(this + "processvent: appendimng " + (event.key - '0'));
					numDuplicationsStr += "" + (event.key - '0');
//MiDebug.println(this + "processvent: numDuplicationsStr " + numDuplicationsStr);
					}
				else if (event.key == Mi_ENTER_KEY)
					{
					int numDuplications = Utility.toInteger(numDuplicationsStr);
//MiDebug.println(this + "numDuplications=" + numDuplications);
					if (numDuplications > 0)
						{
						if (numDuplications > maxNumDuplications)
							{
							numDuplications = maxNumDuplications;
							}
						duplicateSelectedObjects.setTargetOfCommand(event.getEditor());
						duplicateSelectedObjects.processCommand(duplicateCommandName + "." + numDuplicationsStr);
						}
					grabbed = false;
					event.editor.removeGrabEventHandler(this);
					}
				else
					{
					return(super.processEvent(event));
					}
				return(Mi_CONSUME_EVENT);
				}	
			else if ((event.getType() != MiEvent.Mi_TIMER_TICK_EVENT)
				&& (event.getType() != MiEvent.Mi_IDLE_EVENT)
				&& (event.getType() != MiEvent.Mi_NO_LONGER_IDLE_EVENT)
				&& (event.getType() != MiEvent.Mi_KEY_RELEASE_EVENT))
				{
				grabbed = false;
				event.editor.removeGrabEventHandler(this);
				}
			}
		return(super.processEvent(event));
		}
	}

