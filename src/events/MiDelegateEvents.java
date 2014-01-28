
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
public class MiDelegateEvents extends MiEventHandler
	{
	private		MiPart		delegatee;
	private		int		delegateOnlyThisEventType	= Mi_ALL_EVENT_TYPES;


					/**------------------------------------------------------
	 				 * Constructs a new MiDelegateEvents. This event 
					 * handler, when assigned to a MiPart, forwards all events
					 * encountered to the delegatee MiPart. 
					 * @see 		#setDelegatee
					 *------------------------------------------------------*/
	public				MiDelegateEvents()
		{
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiDelegateEvents. This event 
					 * handler, when assigned to a MiPart, forwards all events
					 * encountered to the delegatee MiPart.
					 * @param delegatee	the part to forward events to.
					 *------------------------------------------------------*/
	public				MiDelegateEvents(MiPart delegatee)
		{
		this(delegatee, Mi_ALL_EVENT_TYPES);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiDelegateEvents. This event 
					 * handler, when assigned to a MiPart, forwards all events
					 * encountered to the delegatee MiPart.
					 * @param delegatee			the part to forward events to.
					 * @param delegateOnlyThisEventType	the type of the events to
					 *					delegate (default value
					 *					is Mi_ALL_EVENT_TYPES).
					 *------------------------------------------------------*/
	public				MiDelegateEvents(MiPart delegatee, int delegateOnlyThisEventType)
		{
		this.delegatee = delegatee;
		this.delegateOnlyThisEventType = delegateOnlyThisEventType;
		}
					/**------------------------------------------------------
					 * Specifies the MiPart to delegate events to.
					 * @param delegatee	the part to foward events to
					 *------------------------------------------------------*/
	public		void		setDelegatee(MiPart delegatee)
		{
		this.delegatee = delegatee;
		}
					/**------------------------------------------------------
					 * Returns the MiPart to delegate events to.
					 * @return 		the part to foward events to
					 *------------------------------------------------------*/
	public		MiPart		getDelegatee()
		{
		return(delegatee);
		}
					/**------------------------------------------------------
	 				 * Processes the given event. The event is forwarded to 
					 * the delegatee and the status returned by the delegatee 
					 * is in turn returned from this method.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @overrides		MiEventHandler#processEvent
					 *------------------------------------------------------*/
	public		int		processEvent(MiEvent event)
		{
		if ((isEnabled()) && (delegatee != null)
			&& ((delegateOnlyThisEventType == Mi_ALL_EVENT_TYPES)
			|| (event.type == delegateOnlyThisEventType)))
			{
			return(delegatee.dispatchEvent(event));
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}

