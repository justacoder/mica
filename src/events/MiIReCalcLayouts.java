
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
public class MiIReCalcLayouts extends MiEventHandler
	{
	private		MiReCalcLayouts	reCalcR;

	public				MiIReCalcLayouts()
		{
		addEventToCommandTranslation(
			Mi_EXECUTE_COMMAND_NAME, Mi_KEY_PRESS_EVENT, 'l', Mi_CONTROL_KEY_HELD_DOWN + Mi_SHIFT_KEY_HELD_DOWN);
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
		if (reCalcR == null)
			{
			reCalcR = new MiReCalcLayouts();
			}
		reCalcR.setTargetOfCommand(event.editor.getRootWindow());
		reCalcR.processCommand(null);
		return(Mi_CONSUME_EVENT);
		}
	}

