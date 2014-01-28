
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
public class MiNativeDialog extends MiNativeWindow implements MiiCommandHandler, MiiCommandNames
	{
	private		boolean		modal;
	private		int		prevMouseAppearance;
	private		MiNativeWindow	parent;
	private		String		returnMsg;
	private		Thread		waitingThread;
	private		MiPartModifierThread secondaryThread;;




	public				MiNativeDialog(MiPart parent, String title, boolean modal)
		{
		super((MiNativeWindow )parent.getContainingWindow(), title, new MiBounds(0,0,100,100), modal);

		if (parent.getContainingWindow() == null)
			{
			throw new IllegalArgumentException("Parent: " 
				+ parent + " of MiNativeDialog has not yet been assigned to a window");
			}
		this.parent = (MiNativeWindow )parent.getContainingWindow();
		this.modal = modal;
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(false));
		}

	public		void		isModal(boolean flag) 	
		{ 
		modal = flag; 	
		}
	public		boolean		isModal() 		
		{ 
		return(modal); 	
		}

	public		void		setVisible(boolean flag)
		{
		if (flag == super.isVisible())
			{
			// Don't set visible and cursor twice, because we dont set 
			// visible false twice and get a dangling cursor. 
			return;
			}
		super.setVisible(flag);
		if (flag)
			{
			if (modal)
				{
				// This causes the parent window to be blanked....
				// parent.getFrame().setEnabled(false);
				parent.getCanvas().setEventHandlingEnabled(false);
				prevMouseAppearance = parent.getMouseAppearance();
				parent.pushMouseAppearance(MiiTypes.Mi_WAIT_CURSOR, getClass().getName());
				}
			}
		else
			{
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

			if (modal)
				{
				parent.popMouseAppearance(getClass().getName());
				parent.getFrame().setEnabled(true);
				parent.getCanvas().setEventHandlingEnabled(true);
				}
			}
		}

	public 		String		popupAndWaitForClose()
		{
		setDefaultCloseCommand(Mi_CANCEL_COMMAND_NAME);
		waitingThread = Thread.currentThread();
		setVisible(true);
		while (isVisible() && (waitingThread != null))
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
					int numLocks = parent.getRootWindow().getCanvas()
						.freeAccessLocks(waitingThread);
					synchronized (waitingThread)
						{
						waitingThread.wait();
						}
					parent.getRootWindow().getCanvas().getAccessLocks(numLocks);
					}
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			}
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
			returnMsg = Mi_OK_COMMAND_NAME;
			setVisible(false);
			dispatchAction(Mi_WINDOW_OK_ACTION);
			}
		else if (arg.equalsIgnoreCase(Mi_CANCEL_COMMAND_NAME))
			{
			returnMsg = Mi_CANCEL_COMMAND_NAME;
			setVisible(false);
			dispatchAction(Mi_WINDOW_CANCEL_ACTION);
			}
		else if (arg.equalsIgnoreCase(Mi_HELP_COMMAND_NAME))
			{
			// WANTED 7-10-2001 dispatchAction(Mi_WINDOW_HELP_ACTION);
			dispatchAction(Mi_WINDOW_CANCEL_ACTION);
			}
		else
			{
			returnMsg = arg;
			// TO BE REMOVED 7-10-2001 
			setVisible(false);
			}
		}
	}


