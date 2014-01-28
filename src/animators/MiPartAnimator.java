
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
public abstract class MiPartAnimator implements MiiAnimatable
	{
	private		MiPart		subject;
	private		MiAnimator	animator;
	private		MiParts		subjectPath;
	private		MiAnimationManager manager;
	private		boolean		animateForward		= true;
	private		boolean		atEnd;
	private		boolean		scheduled;
	private		boolean		runOnlyWhenSubjectVisible;
	private static final int	MILLISECS_PER_SEC	= 1000;
	private		Thread		waitingThread;



	public				MiPartAnimator(MiPart obj)
		{
		this(obj, 0.10);
		}
	public				MiPartAnimator(MiPart obj, double secondsBetweenAnimations)
		{
		animator = new MiAnimator(this, (long )(secondsBetweenAnimations * MILLISECS_PER_SEC));
		subject = obj;
		}
	public		void		schedule()
		{
		manager = subject.getRootWindow().getCanvas().getAnimationManager();
		manager.addAnimator(animator);
		atEnd = false;
		scheduled = true;
		}
	public		void		unschedule()
		{
		// subject may have been removed from window.... manager = subject.getRootWindow().getCanvas().getAnimationManager();
		scheduled = false;
		manager.removeAnimator(animator);
		}
	public		boolean		isScheduled()
		{
		return(scheduled);
		}
	public		void		setPacer(MiiAnimatorPacer pacer)
		{
		animator.setPacer(pacer);
		}
	public		MiiAnimatorPacer getPacer()
		{
		return(animator.getPacer());
		}
	public		void		setIsAnimatingForward(boolean flag)
		{
		animateForward = flag;
		}
	public		boolean		isAnimatingForward()
		{
		return(animateForward);
		}
	public		void		setSubject(MiPart obj)
		{
		subject = obj;
		if (runOnlyWhenSubjectVisible)
			setOnlyRunWhenSubjectVisible(true);
		}
	public		MiPart	getSubject()
		{
		return(subject);
		}
	public		void		setEnabled(boolean flag)
		{
		animator.setEnabled(flag);
		}
	public		boolean		getEnabled()
		{
		return(animator.getEnabled());
		}
	public		void		setStepTime(double seconds)
		{
		animator.setStepTime((long )(seconds * MILLISECS_PER_SEC));
		}
	public		double		getStepTime()
		{
		return(animator.getStepTime()/MILLISECS_PER_SEC);
		}
	public		void		setDuration(double seconds)
		{
		animator.setDuration((long )(seconds * MILLISECS_PER_SEC));
		}
	public		double		getDuration()
		{
		return(animator.getDuration()/MILLISECS_PER_SEC);
		}
	public		void		setOnlyRunWhenSubjectVisible(boolean flag)
		{
		if ((flag) && (subject != null))
			MiUtility.getPath(subject.getRootWindow(), subject, subjectPath = new MiParts());
		runOnlyWhenSubjectVisible = flag;
		}
	public		boolean		getOnlyRunWhenSubjectVisible()
		{
		return(runOnlyWhenSubjectVisible);
		}
	public		void		resetStepTimer()
		{
		if (manager != null)
			manager.resetStepTimer(animator);
		}
	public		void		resetTimer()
		{
		if (manager != null)
			manager.resetTimer(animator);
		}

	protected	boolean		isSubjectVisible()
		{
		MiPart child = null;
		for (int i = 0; i < subjectPath.size(); ++i)
			{
			MiPart obj = subjectPath.elementAt(i);
			if (((obj != subject) && !obj.isVisible()) 
				|| ((obj instanceof MiEditor) && (child != null) 
				&& (!obj.getInnerBounds().intersects(child.getBounds()))))
				{
				return(false);
				}
			child = obj;
			}
		return(true);
		}
	public		void		scheduleAndWait()
		{
		Thread secondaryThread = null;
		waitingThread = Thread.currentThread();
		schedule();
		while(!atEnd)
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
		if (secondaryThread != null)
			secondaryThread.stop();
		}
	public		void			start()
		{
		}
	public		void			end()
		{
		atEnd = true;
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
			}
		}
	public		boolean			isAtEnd()
		{
		return(atEnd);
		}
	}

