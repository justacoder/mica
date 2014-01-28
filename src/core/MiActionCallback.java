
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

/**----------------------------------------------------------------------------------------------
 * This class is a MiAction that is devoted to supporting the
 * assignment of callbacks to MiParts (see MiPart#appendCommandHandler).
 * It can, of course, be used any time it is desired that an
 * action should trigger a callback (i.e. a call to the processCommand
 * method of a MiiCommandHandler object).
 *
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiActionCallback extends MiAction implements MiiActionHandler, MiiActionTypes
	{
	private		MiiCommandHandler	commandHandler;
	private		String			command;
	private		boolean			returnCode;


					/**------------------------------------------------------
	 				 * Constructs a new MiActionCallback, that when assigned
					 * to a MiPart, will call the given command handler with 
					 * the given command whenever the MiPart dispatches a 
					 * Mi_ACTIVATED_ACTION.
					 * @param commandHandler the command handler to call
					 * @param command	the paramter to pass to the command
					 *			handler
					 *------------------------------------------------------*/
	public				MiActionCallback(
						MiiCommandHandler commandHandler, 
						String command)
		{
		this(commandHandler, command, Mi_ACTIVATED_ACTION, true);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiActionCallback, that when assigned
					 * to a MiPart, will call the given command handler with 
					 * the given command whenever the MiPart dispatches the 
					 * given action type.
					 * @param commandHandler the command handler to call
					 * @param command	the paramter to pass to the command handler
					 * @param actionType	the actionType that triggers the call
					 *------------------------------------------------------*/
	public				MiActionCallback(
						MiiCommandHandler commandHandler, 
						String command, 
						int actionType)
		{
		this(commandHandler, command, actionType, true);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiActionCallback, that when assigned
					 * to a MiPart, will call the given command handler with 
					 * the given command whenever the MiPart dispatches the 
					 * given action type.
					 * @param commandHandler the command handler to call
					 * @param command	the paramter to pass to the command handler
					 * @param actionType	the actionType that triggers the call
					 * @param returnCode	the return code
					 *------------------------------------------------------*/
	public				MiActionCallback(
						MiiCommandHandler commandHandler, 
						String command, 
						int actionType,
						boolean returnCode)
		{
		super(null, actionType);
		setActionHandler(this);
		this.commandHandler = commandHandler;
		this.command = command;
		this.returnCode = returnCode;
		}
					/**------------------------------------------------------
	 				 * Gets the command handler to call.
					 * @return		the command handler to call
					 *------------------------------------------------------*/
	public		MiiCommandHandler getCommandHandler()
		{
		return(commandHandler);
		}
					/**------------------------------------------------------
	 				 * Gets the command of the command handler to call.
					 * @return		the command of the command handler
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(command);
		}
					/**------------------------------------------------------
	 				 * Sets the code to return after the command handler has 
					 * been called. This is most useful, for example, when the
					 * trigger action is in the EXECUTE_PHASE and the command
					 * handler is to be totally responsable for executing the
					 * action (i.e. Mi_WINDOW_DESTROY_ACTION).
					 * @param flag		the return code
					 *------------------------------------------------------*/
	public		void		setReturnCode(boolean flag)
		{
		returnCode = flag;
		}
					/**------------------------------------------------------
	 				 * Gets the code to return after the command handler has 
					 * been called.
					 * @return 		the return code
					 *------------------------------------------------------*/
	public		boolean		getReturnCode()
		{
		return(returnCode);
		}
					/**------------------------------------------------------
	 				 * Processes the given action (probably _this_ MiAction).
					 * Default behavior is to call the previously registered 
					 * command handler. 
					 * @param action	the action to process
					 * @return 		the previously registered return
					 *			code.
					 *			true if it is OK to send
					 *			action to the next action handler
					 * 	 		false if it is NOT OK to send
					 *			action to the next action handler
					 * @see			#setReturnCode
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		commandHandler.processCommand(command);
		return(returnCode);
		}
					/**------------------------------------------------------
					 * Returns information about this class.
					 * @return		textual information (class name +
					 *			action type + command handler 
					 *			+ command)
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) +  ": " + super.toString()
			+ "->calls: " + commandHandler + "(" + command + ")");
		}
	}

