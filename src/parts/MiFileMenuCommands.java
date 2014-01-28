
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
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiFileMenuCommands extends MiCommandHandler implements MiiCommandNames
	{
	private		MiiCommandManager manager;
	private		MiiCommandHandler	application;

					/**------------------------------------------------------
	 				 * Constructs a new MiFileMenuCommands that will send the
					 * command it receives from the menu to the given command
					 * handler.
					 * @param application	the command handler
					 * @see 		MiFileMenu
					 *------------------------------------------------------*/
	public				MiFileMenuCommands(MiiCommandHandler application, MiiCommandManager manager)
		{
		this.application = application;
		this.manager = manager;
		}
	
					/**------------------------------------------------------
	 				 * Processes the given command by forwarding it to the
					 * command handler specified in the constructor.
					 * @param cmd		the comamnd to forward
					 *------------------------------------------------------*/
	public		void		processCommand(String cmd)
		{
		MiUtility.pushMouseAppearance(
			(MiPart )getTargetOfCommand(), MiiTypes.Mi_WAIT_CURSOR, "MiFileMenuCommands");

		application.processCommand(cmd);

		MiUtility.popMouseAppearance((MiPart )getTargetOfCommand(), "MiFileMenuCommands");
		}
	}

