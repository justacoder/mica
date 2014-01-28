
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
import java.awt.Frame; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiDialog extends MiInternalWindow implements MiiCommandHandler, MiiCommandNames
	{
	private		boolean		modal;
	private		int		prevMouseAppearance;
	private		MiEditor	parent;
	private		String		returnMsg;
	private		Thread		waitingThread;
	private		MiPartModifierThread secondaryThread;




	public				MiDialog(MiEditor parent, String title, boolean modal)
		{
		super(parent, title, new MiBounds(0,0,100,100), modal);
		this.parent = parent;
		this.modal = modal;
		}

	public		void		isModal(boolean flag) 	{ modal = flag; 	}
	public		boolean		isModal() 		{ return(modal); 	}

	public		void		setVisible(boolean flag)
		{
		if (flag == isVisible())
			return;

		super.setVisible(flag);
		if (flag)
			{
			if (modal)
				{
				parent.setEventHandlingEnabled(false);
				// FIX: need a setSensitive for whole window to make it look gray as well
				prevMouseAppearance = parent.getContextCursor(null);
				parent.pushMouseAppearance(Mi_WAIT_CURSOR, toString());
				parent.setContextCursor(Mi_WAIT_CURSOR);
				setContextCursor(Mi_DEFAULT_CURSOR);
				}
			}
		else
			{
			if (modal)
				{
				parent.setContextCursor(prevMouseAppearance);
				parent.popMouseAppearance(toString());
				parent.setEventHandlingEnabled(true);
				}
			if (waitingThread != null)
				{
				if (waitingThread instanceof MiPartModifierThread)
					{
					((MiPartModifierThread )waitingThread).notifyAllThreads();
					}
				else
					{
					synchronized(waitingThread)
						{
						waitingThread.notify();
						}
					}
				waitingThread = null;
				if (secondaryThread != null)
					{
					secondaryThread.stopThread();
					secondaryThread = null;
					}
				}
			}
		}
	public 		String		popupAndWaitForClose()
		{
		waitingThread = Thread.currentThread();
		getIconifyButton().setSensitive(false);
		setVisible(true);
		while(isVisible())
			{
			try	{
				if (waitingThread instanceof MiPartModifierThread)
					{
					MiPartModifierThread thread = (MiPartModifierThread )waitingThread;
					if (secondaryThread == null) 
						{
						secondaryThread = thread.makeNewRunningThread();
						}
					thread.waitThread();
					}
				else
					{
					waitingThread.wait();
					}
				}
			catch (Exception e) {}
			}
		getIconifyButton().setSensitive(true);
		return(returnMsg);
		}

	public		String		getResult()
		{
		return(returnMsg);
		}

	public	 	void		processCommand(String arg)
		{
		if (arg.equalsIgnoreCase(Mi_OK_COMMAND_NAME))
			{
			if (!dispatchActionRequest(Mi_WINDOW_OK_ACTION))
				return;
			returnMsg = Mi_OK_COMMAND_NAME;
			setVisible(false);
			dispatchAction(Mi_WINDOW_OK_ACTION);
			}
		else if (arg.equalsIgnoreCase(Mi_CANCEL_COMMAND_NAME))
			{
			if (!dispatchActionRequest(Mi_WINDOW_CANCEL_ACTION))
				return;
			returnMsg = Mi_CANCEL_COMMAND_NAME;
			setVisible(false);
			dispatchAction(Mi_WINDOW_CANCEL_ACTION);
			}
		else if ((arg.equalsIgnoreCase(Mi_CLOSE_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_ICONIFY_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_DEICONIFY_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_TOGGLE_FULLSCREEN_COMMAND_NAME)))
			{
			super.processCommand(arg);
			return;
			}
		}
	}

