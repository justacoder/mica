
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
import com.swfm.mica.util.Strings; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiStatusBarFocusManager implements MiiActionTypes
	{
	private		MiPart		window;
	private		MiiHelpInfo	statusBarMessageInfo;
	private		MiPart		statusBarFocus;


	public				MiStatusBarFocusManager(MiPart window)
		{
		this.window = window;
		window.appendEventHandler(new MiStatusBarFocusMonitor(this));
		}
	public 		void		setStatusBarFocusMessage(MiiHelpInfo messageInfo)
		{
		statusBarFocus = null;
		if (messageInfo != MiHelpInfo.ignoreThis)
			{
			if (messageInfo == MiHelpInfo.noneForThis)
				{
				messageInfo = null;
				}

			statusBarMessageInfo = messageInfo;
			window.dispatchAction(Mi_STATUS_BAR_FOCUS_CHANGED_ACTION);
			}
		}
	public 		MiiHelpInfo	getStatusBarFocusMessage()
		{
		return(statusBarMessageInfo);
		}
	public 		MiPart		getStatusBarFocus()
		{
		return(statusBarFocus);
		}
	}

class MiStatusBarFocusMonitor extends MiEventMonitor
	{
	private		MiStatusBarFocusManager	focusManager;
	private		MiiHelpInfo	currentStatusHelpInfo;
	private		MiPoint		tmpPoint		= new MiPoint();



	public				MiStatusBarFocusMonitor(MiStatusBarFocusManager manager)
		{
		focusManager = manager;
		}

	public		int		processEvent(MiEvent event)
		{
		if ((event.getType() == MiEvent.Mi_TIMER_TICK_EVENT)
			|| (event.getType() == MiEvent.Mi_IDLE_EVENT))
			{
			return(Mi_PROPOGATE_EVENT);
			}
		if ((event.type == MiEvent.Mi_WINDOW_EXIT_EVENT)
			|| ((event.type == MiEvent.Mi_MOUSE_EXIT_EVENT) && (event.target == event.editor)))
			{
			if (currentStatusHelpInfo != null)
				{
				focusManager.setStatusBarFocusMessage(null);
				currentStatusHelpInfo = null;
				}
			return(Mi_PROPOGATE_EVENT);
			}
		MiParts targetPath = event.getTargetPath();
		for (int i = 0; i < targetPath.size(); ++i)
			{
			MiiHelpInfo helpInfo;
			MiPart obj = targetPath.elementAt(i);
			event.getWorldPoint(tmpPoint);
			helpInfo = obj.getStatusHelp(tmpPoint);
			if (helpInfo != null)
				{
				if (helpInfo == currentStatusHelpInfo)
					return(Mi_PROPOGATE_EVENT);

				currentStatusHelpInfo = helpInfo;
				focusManager.setStatusBarFocusMessage(helpInfo);
				return(Mi_PROPOGATE_EVENT);
				}
			}
		if (currentStatusHelpInfo != null)
			{
			focusManager.setStatusBarFocusMessage(null);
			currentStatusHelpInfo = null;
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}


