
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
 * This class is used to cause the current thread to wait until
 * a specified action occurs on a specified part.
 *
 * @see		MiPart#waitUntilRedrawn
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiWaitUntilActionHandler implements MiiActionHandler, MiiActionTypes
	{
	private		boolean		waitingForAction;
	private		MiPart		subject;
	private		Thread 		waitingThread;
	private		boolean 	allowPartModsAndUserEventHandling = true;
	private		boolean 	origEventHandlingEnabled;
	private		MiPartModifierThread secondaryThread;


					/**------------------------------------------------------
					 * Constructs a new MiWaitUntilActionHandler.
					 * @param subject	the part that will generate the 
					 *			action this will wait for (the
					 *			waitForAction(int action) must be
					 *			called for the waiting to begin.
					 * @see			#waitForAction
					 *------------------------------------------------------*/
	public				MiWaitUntilActionHandler(MiPart subject)
		{
		this.subject = subject;
		}
					/**------------------------------------------------------
					 * Constructs a new MiWaitUntilActionHandler. The current
					 * thread will wait before returning from this contructor.
					 * @param subject	the part that will generate the 
					 *			action this will wait for 
					 * @param action	the action type to wait for
					 *------------------------------------------------------*/
	public				MiWaitUntilActionHandler(MiPart subject, int action)
		{
		this(subject, action, true);
		}
					/**------------------------------------------------------
					 * Constructs a new MiWaitUntilActionHandler. The current
					 * thread will wait before returning from this contructor.
					 * If the 'allowPartModsAndUserEventHandling' parameter
					 * is true (and usually it will be), a new thread is created
					 * for the window containing the given subject so that
					 * normal activity can occur.
					 * @param subject	the part that will generate the 
					 *			action this will wait for 
					 * @param action	the action type to wait for
					 * @param allowPartModsAndUserEventHandling
					 *			true if the part can be modified
					 *			while this constructor waits
					 *------------------------------------------------------*/
	public				MiWaitUntilActionHandler(
						MiPart subject, 
						int action, 
						boolean allowPartModsAndUserEventHandling)
		{
		this.subject = subject;
		this.allowPartModsAndUserEventHandling = allowPartModsAndUserEventHandling;
		waitForAction(action);
		}
					/**------------------------------------------------------
					 * Waits for the given action to occur on the MiPart specified
					 * in the constructor. This method does not return until
					 * the action occurs.
					 * @param action	the action type to wait for
					 *------------------------------------------------------*/
	public		void		waitForAction(int action)
		{
		waitingForAction = true;
		subject.appendActionHandler(this, action);
		waitForAction();
		}
					/**------------------------------------------------------
					 * The action we are waiting for has occured. Restart the
					 * original waiting thread and the propogate the action to
					 * others who may want it.
					 * @return 	always true
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		actionOccured();
		return(true);
		}
					/**------------------------------------------------------
					 * Waits for the previously specified action to occur on 
					 * the previously specified MiPart.
					 *------------------------------------------------------*/
	protected	void		waitForAction()
		{
		waitingThread = Thread.currentThread();
		MiCanvas canvas = null;
		if (!allowPartModsAndUserEventHandling)
			{
			canvas = subject.getRootWindow().getCanvas();
			origEventHandlingEnabled = canvas.getEventDispatchingEnabled();
			}
		// ---------------------------------------------------------------
		// While we are still waiting for the action to occur...
		// ---------------------------------------------------------------
		while (waitingForAction)
			{
			try	{
				if (!allowPartModsAndUserEventHandling)
					{
					canvas.setEventDispatchingEnabled(false);
					}
				// ---------------------------------------------------------------
				// If the thread that we are causing to wait is a MiPartModifierThread
				// (which the threads created in MiCanvas are)...
				// ---------------------------------------------------------------
				if (waitingThread instanceof MiPartModifierThread)
					{
					MiPartModifierThread thread = (MiPartModifierThread )waitingThread;
					// ---------------------------------------------------------------
					// Create and start thread to temporarily take the place of the
					// waiting thread
					// ---------------------------------------------------------------
					if (secondaryThread == null)
						{
						secondaryThread = thread.makeNewRunningThread();
						}
					thread.waitThread();
					}
				else if (waitingThread != null)
					{
					synchronized (this)
						{
						wait();
						}
					}
				}
			catch (Exception e) {}
			}
		}
					/**------------------------------------------------------
					 * The action we are waiting for has occured. Restart the
					 * original waiting thread and kill thread that we may
					 * have started to temporarily takes it's place.
					 *------------------------------------------------------*/
	protected	void		actionOccured()
		{
		waitingForAction = false;
		if (waitingThread != null)
			{
			// ---------------------------------------------------------------
			// If this is a MiCanvas-created thread then have it notify all
			// waiting threads, in particular the original thread to wake it up
			// ---------------------------------------------------------------
			if (waitingThread instanceof MiPartModifierThread)
				{
				((MiPartModifierThread )waitingThread).notifyAllThreads();
				}
			else
				{
				synchronized (this)
					{
					notifyAll();
					}
				}
			// ---------------------------------------------------------------
			// Remove this action handler from the subject.
			// ---------------------------------------------------------------
			subject.removeActionHandlers(this);
			waitingThread = null;
			if (!allowPartModsAndUserEventHandling)
				{
				MiCanvas canvas = subject.getRootWindow().getCanvas();
				canvas.setEventDispatchingEnabled(origEventHandlingEnabled);
				}
			// ---------------------------------------------------------------
			// Stop and kill thread we created to temporarily take the place 
			// of the waiting thread
			// ---------------------------------------------------------------
			if (secondaryThread != null)
				{
				secondaryThread.stopThread();
				secondaryThread = null;
				}
			}
		}
	}


