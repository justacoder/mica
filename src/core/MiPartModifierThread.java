
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
import com.swfm.mica.util.Set ;
import com.swfm.mica.util.IntVector;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPartModifierThread extends Thread
	{
	// Home/spawning canvas
	private		MiCanvas	canvas;
	// Canvases for which this thread has a lock.
	private		Set		canvases	= new Set();
	private		boolean		stopped;




					/**------------------------------------------------------
	 				 * Constructs a new MiPartModifierThread for the given
					 * canvas.
					 * @param canvas	the canvas this thread 	will run 
					 *			on.
					 *------------------------------------------------------*/
	public				MiPartModifierThread(MiCanvas canvas)
		{
		super(canvas);
		this.canvas = canvas;
		canvases.addElement(canvas);
		}
					/**------------------------------------------------------
	 				 * Gets the canvas this thread runs on.
					 * @return 		the associated canvas
					 *------------------------------------------------------*/
	public		MiCanvas	getCanvas()
		{
		return(canvas);
		}

					/**------------------------------------------------------
	 				 * Specifies that this thread has a lock on the given 
					 * canvas. This is required because this thread may have
					 * a lock on other canvases in addition to the one it was
					 * spawned from.
					 * @param canvas 	the associated canvas
					 *------------------------------------------------------*/
	protected	void		lockedCanvas(MiCanvas canvas)
		{
		canvases.addElement(canvas);
		}
					/**------------------------------------------------------
	 				 * Specifies that this thread does not have a lock on the
					 * given canvas. 
					 * @param canvas 	the associated canvas
					 *------------------------------------------------------*/
	protected	void		unlockedCanvas(MiCanvas canvas)
		{
		canvases.removeElement(canvas);
		}
					/**------------------------------------------------------
	 				 * Frees all access locks the calling thread has on the
					 * associated canvas, wait on the canvas for a notify
					 * event, and then get all the access locks back again,
					 * and return. This is used to allow another thread access
					 * to the associated canvas, which then does it's thing,
					 * and when done calls notifyAllThreads to notify
					 * threads like this one that they can start running
					 * again.
					 * @see 		MiCanvas
					 * @see 		#notifyAllThreads
					 * @see 		MiCanvas#notifySelf
					 *------------------------------------------------------*/
	public		void		waitThread()
		{
		IntVector numAccessLocks = new IntVector();
		Set lockedCanvases = new Set();
		lockedCanvases.append(canvases);
		for (int i = 0; i < lockedCanvases.size(); ++i)
			{
			numAccessLocks.addElement(
				((MiCanvas )lockedCanvases.elementAt(i)).freeAccessLocks(Thread.currentThread()));
			}
		try	{
			synchronized (canvas)
				{
				canvas.wait();
				}
			}
		catch (Exception e) {e.printStackTrace();}
		for (int i = 0; i < lockedCanvases.size(); ++i)
			{
			((MiCanvas )lockedCanvases.elementAt(i)).getAccessLocks(numAccessLocks.elementAt(i));
			}
		}

					/**------------------------------------------------------
	 				 * Frees all access locks the calling thread has on the
					 * associated canvas, and the stops the calling thread.
					 * @see 		MiCanvas
					 * @see 		#waitThread
					 *------------------------------------------------------*/
	public		void		stopThread()
		{
		canvas.freeAccessLocks(this);
		stopped = true;
/*
		if (MiSystem.getJDKVersion() < 1.4)
			{
			stop();
			}
*/
		}
	public		boolean		isStopped()
		{
		return(stopped);
		}

					/**------------------------------------------------------
	 				 * Convience method that calls the associated canvas's
					 * makeNewRunningThread method.
					 * @return 		The new thread
					 * @see 		MiCanvas#makeNewRunningThread
					 *------------------------------------------------------*/
	public		MiPartModifierThread makeNewRunningThread()
		{
		return(canvas.makeNewRunningThread());
		}
					/**------------------------------------------------------
	 				 * Convience method that calls the associated canvas's
					 * notifySelf method.
					 * @see 		MiCanvas#notifySelf
					 *------------------------------------------------------*/
	public		void		notifyAllThreads()
		{
		canvas.notifySelf();
		}
					
	}

