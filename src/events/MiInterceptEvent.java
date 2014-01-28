
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
public class MiInterceptEvent extends MiEventHandler
	{
	private		int 		consumeOrPropogate;


					/**------------------------------------------------------
	 				 * Constructs a new MiInterceptEvent. This event handler,
					 * when inserted in a list of event handlers assigned to a
					 * MiPart, can either 1) prevent the event from reaching
					 * any following event handlers, 2) prevent the event from
					 * reaching any following event handlers assigned to the
					 * MiPart or 3) do nothing; all depending on the value of
					 * the given consumeOrPropogate parameter. Respective
					 * values are:
					 *    Mi_CONSUME_EVENT
					 *    Mi_IGNORE_THIS_PART
					 *    Mi_PROPOGATE_EVENT
					 * @param eventToIntercept	the event to look for
					 * @param consumeOrPropogate	what to do with the event
					 *------------------------------------------------------*/
	public				MiInterceptEvent(
						MiEvent eventToIntercept, 
						int consumeOrPropogate)
		{
		this.consumeOrPropogate = consumeOrPropogate;
		addEventToCommandTranslation(Mi_EXECUTE_COMMAND_NAME, eventToIntercept);
		}
	public		MiiEventHandler	copy()
		{
		MiInterceptEvent obj = (MiInterceptEvent )super.copy();
		obj.consumeOrPropogate = consumeOrPropogate;
		return(obj);
		}

					/**------------------------------------------------------
	 				 * Processes the stored command generated from the stored
					 * event.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		return(consumeOrPropogate);
		}
	}

