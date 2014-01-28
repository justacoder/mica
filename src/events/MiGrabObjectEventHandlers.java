
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

// GrabHandlerGroup
// GrabbedObjectEventHandler
// GrabbedObjectEventDispatcher
/**----------------------------------------------------------------------------------------------
 * This class supports the 'grab' of an object, which is actually
 * a grab of all of the object's event handlers at once.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGrabObjectEventHandlers extends MiEventHandler
	{
	private		MiPart	subject;

	public				MiGrabObjectEventHandlers(MiPart object)
		{
		subject = object;
		setObject(object);
		setTarget(object);
		setPositionDependent(false);
		}

	public		int		processEvent(MiEvent event)
		{
		int type = event.handlerTargetType;
		boolean locSpecific = event.locationSpecific;

		if ((event.type == Mi_KEY_PRESS_EVENT) || (event.type == Mi_KEY_RELEASE_EVENT))
			{
			event.locationSpecific = false;
			}
		event.handlerTargetType = Mi_SHORT_CUT_EVENT_HANDLER_TYPE;
		int status = processEvent(subject, event);
		event.locationSpecific = locSpecific;
		if (status != Mi_CONSUME_EVENT)
			{
			event.handlerTargetType = Mi_ORDINARY_EVENT_HANDLER_TYPE;
			if ((!event.locationSpecific) || (subject.pick(event.worldMouseFootPrint)))
				status = processEvent(subject, event);
			}
		event.handlerTargetType = type;
		return(status);
		}
	public	int			processEvent(MiPart subject, MiEvent event)
		{
		if ((!subject.isVisible()) && (event.locationSpecific))
			return(Mi_PROPOGATE_EVENT);

		MiEvent localEvent = event;
		if ((subject.getTransform() != null) && (!(subject instanceof MiEditor)))
			{
			event = event.transform(subject.getTransform());
			}

		for (int i = subject.getNumberOfParts(); i > 0; --i)
			{
			MiPart obj = subject.getPart(i - 1);
			if ((!event.locationSpecific) || (obj.pick(event.worldMouseFootPrint)))
				{
				event.target = obj;
				if (processEvent(obj, event) == Mi_CONSUME_EVENT)
					{
					event.target = null;
					return(Mi_CONSUME_EVENT);
					}
				}
			}
		localEvent.target = subject;
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_HANDLER_GRABS))
			{
			MiDebug.println("Dispatching event: " + localEvent + " to grab object: " + subject);
			}
		int status = subject.dispatchEvent(localEvent);
		localEvent.target = null;
		return(status);
		}

	public		String		toString()
		{
		return(super.toString() + ", object: " + subject);
		}
	}

