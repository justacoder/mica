
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
public class MiIExecuteCommand extends MiEventHandler
	{
	private		MiiCommandHandler	handler;
	private		String			arg;
	private		int			returnCode;


	protected			MiIExecuteCommand()
		{
		// Used for copies
		}

	public				MiIExecuteCommand(MiEvent trigger, MiiCommandHandler handler)
		{
		this(trigger, handler, null);
		}

	public				MiIExecuteCommand(MiEvent trigger, MiiCommandHandler handler, String arg)
		{
		this(trigger, handler, arg, Mi_PROPOGATE_EVENT);
		}
	public				MiIExecuteCommand(MiEvent trigger, MiiCommandHandler handler, String arg, int returnCode)
		{
		addEventToCommandTranslation(Mi_EXECUTE_COMMAND_NAME, trigger.type, trigger.key, trigger.modifiers);
		this.handler = handler;
		this.arg = arg;
		this.returnCode = returnCode;
		}
	public		MiiCommandHandler 	getCommandHandler()
		{
		return(handler);
		}
	public		void 		setCommandHandler(MiiCommandHandler m)
		{
		handler = m;
		}

	public		String 		getCommand()
		{
		return(arg);
		}
	public		void 		setCommand(String arg)
		{
		this.arg = arg;
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
		handler.processCommand(arg);
		return(returnCode);
		}
					/**------------------------------------------------------
	 				 * Returns a copy of this MiEventHandler.
	 				 * @return 	 	the copy
					 * @implements		MiiEventHandler#copy
					 *------------------------------------------------------*/
	public		MiiEventHandler	copy()
		{
		MiIExecuteCommand obj 	= (MiIExecuteCommand )super.copy();
		obj.handler		= handler;
		obj.arg			= arg;
		obj.returnCode		= returnCode;
		return(obj);
		}
					/**------------------------------------------------------
					 * Returns information about this MiEventHandler.
					 * @return		textual information (class name +
					 *			unique numerical id + [disabled])
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(super.toString() + "<ToExecute: " + handler + ".processCommand(" + arg + ")>");
		}
	}

