
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
import com.swfm.mica.util.FastVector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiShortCutHandler extends MiEventHandler
	{
	private		FastVector		hotKeys = new FastVector();


	public					MiShortCutHandler()
		{
		setType(Mi_SHORT_CUT_EVENT_HANDLER_TYPE);
		setPositionDependent(false);
		}

	public					MiShortCutHandler(MiEvent key, MiiCommandHandler method, String arg)
		{
		hotKeys.addElement(new MiShortCut(key, method, arg));
		setType(Mi_SHORT_CUT_EVENT_HANDLER_TYPE);
		setPositionDependent(false);
		}
	public		MiiEventHandler	copy()
		{
		MiShortCutHandler obj = (MiShortCutHandler )super.copy();
		obj.hotKeys = hotKeys.copy();
		return(obj);
		}
	public		void			addShortCut(MiEvent key, MiiCommandHandler method, String arg)
		{
		hotKeys.addElement(new MiShortCut(key, method, arg));
		}
	public		void			removeAllShortCuts()
		{
		hotKeys.removeAllElements();
		}
	public		int			processEvent(MiEvent event)
		{
		int size = hotKeys.size();
		for (int i = 0; i < size; ++i)
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_SHORT_CUT_DISPATCHING))
				{
				System.out.println("About to compare event: " + event 
					+ " to short cut event: " 
					+ ((MiShortCut )hotKeys.elementAt(i)).key);
				}
			if (event.equalsEventType(((MiShortCut )hotKeys.elementAt(i)).key))
				{
				MiShortCut shortCut = (MiShortCut )hotKeys.elementAt(i);
				if (MiDebug.debug 
					&& MiDebug.isTracing(null, MiDebug.TRACE_SHORT_CUT_DISPATCHING))
					{
					System.out.println("Short cut executing method: " 
						+ shortCut.method + ", with argument " + shortCut.arg);
					}
				shortCut.method.processCommand(shortCut.arg);
				return(Mi_CONSUME_EVENT);
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	public		MiEvent[]	getRequestedEvents()
		{
		MiEvent[] events = new MiEvent[hotKeys.size()];
		for (int i = 0; i < hotKeys.size(); ++i)
			{
			events[i] = ((MiShortCut )hotKeys.elementAt(i)).key;
			}
		return(events);
		}
	public		String[]	getRequestedCommands()
		{
		String[] cmds = new String[hotKeys.size()];
		for (int i = 0; i < hotKeys.size(); ++i)
			{
			cmds[i] = ((MiShortCut )hotKeys.elementAt(i)).method 
					+ "(" + ((MiShortCut )hotKeys.elementAt(i)).arg + ")";
			}
		return(cmds);
		}
	public		String		toString()
		{
		if (MiDebug.debug 
			&& MiDebug.isTracing(null, MiDebug.TRACE_SHORT_CUT_DISPATCHING))
			{
			String str = super.toString() + "\n";
			for (int i = 0; i < hotKeys.size(); ++i)
				{
				MiShortCut cut = (MiShortCut )hotKeys.elementAt(i);
				str += "    [" + MiEvent.eventToString(cut.key) + "->" 
					+ cut.arg + "->" + cut.method + "]\n";
				}
			return(str);
			}
		else
			{
			return(super.toString());
			}
		}
	}
class MiShortCut
	{
	MiEvent		key;
	MiiCommandHandler	method;
	String		arg;

			MiShortCut(MiEvent key, MiiCommandHandler method, String arg)
		{
		this.key = key;
		this.method = method;
		this.arg = arg;
		}
	}


