
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Toolkit                   *
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
import com.swfm.mica.util.Set;

/**
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiAnimationManager
	{
	private		long		EXPECTED_STEP_SIZE	= 30;
	private		FastVector	animators		= new FastVector();
	private		FastVector	simpleAnimators		= new FastVector();
	private		long[]		simpleAnimatorsNextTick = new long[10];
	private		long		lastStepEnd;
	private		Set		animatorsRunningWithAnotherThread = new Set();

	public	static	void		addAnimator(MiPart subject, MiiSimpleAnimator animator)
		{
		subject.getRootWindow().getCanvas().getAnimationManager().addAnimator(animator);
		}

	public	static	void		addAnimator(MiPart subject, MiiAnimator animator)
		{
		subject.getRootWindow().getCanvas().getAnimationManager().addAnimator(animator);
		}

	public	static	void		removeAnimator(MiPart subject, MiiSimpleAnimator animator)
		{
		subject.getRootWindow().getCanvas().getAnimationManager().removeAnimator(animator);
		}

	public	static	void		removeAnimator(MiPart subject, MiiAnimator animator)
		{
		subject.getRootWindow().getCanvas().getAnimationManager().removeAnimator(animator);
		}
	public	static	MiAnimationManager getAnimationManager(MiPart subject)
		{
		return(subject.getRootWindow().getCanvas().getAnimationManager());
		}

	public	synchronized	void		addAnimator(MiiSimpleAnimator animator)
		{
		simpleAnimators.addElement(animator);
		if (simpleAnimatorsNextTick.length < simpleAnimators.size())
			{
			long[] array = new long[simpleAnimators.size()];
			System.arraycopy(simpleAnimatorsNextTick, 0, array, 0, simpleAnimatorsNextTick.length);
			simpleAnimatorsNextTick = array;
			}
		simpleAnimatorsNextTick[simpleAnimators.size() - 1] = 0;
		}
	public	synchronized	void		removeAnimator(MiiSimpleAnimator animator)
		{
		int index = simpleAnimators.indexOf(animator);
		simpleAnimators.removeElement(animator);
		System.arraycopy(simpleAnimatorsNextTick, index + 1, simpleAnimatorsNextTick, index, simpleAnimators.size() - index);
		}
	public	synchronized	void		removeAnimator(MiiAnimator animator)
		{
		MiAnimatorEntry entry = getEntry(animator);
		animators.removeElement(entry);
		}
						// Restart from beginning immediately
	public			void		resetTimer(MiiAnimator animator)
		{
		MiAnimatorEntry entry = getEntry(animator);
		entry.timeStarted = 0;
		entry.lastStepTime = 0;
		}
						// Restart commence nextstep after a delay of one step
	public			void		resetStepTimer(MiiAnimator animator)
		{
		MiAnimatorEntry entry = getEntry(animator);
		entry.lastStepTime = System.currentTimeMillis();
		}
	private			MiAnimatorEntry	getEntry(MiiAnimator animator)
		{
		for (int i = 0; i < animators.size(); ++i)
			{
			MiAnimatorEntry entry = (MiAnimatorEntry )animators.elementAt(i);
			if (entry.animator == animator)
				{
				return(entry);
				}
			}
		return(null);
		}
	public	synchronized	void		addAnimator(MiiAnimator animator)
		{
		animators.addElement(new MiAnimatorEntry(animator));
		}
	public	synchronized	void		addAnimator(MiiAnimator animator, MiiAnimator next, int phase, double when)
		{
		animators.addElement(animator);
		if (animator.getStartTime() == 0)
			animator.start();
		}
// InvokeLater may want to have animations continue while it is working public synchronized	void		animate()
	public 		void		animate()
		{
		if ((animators.size() == 0) && (simpleAnimators.size() == 0))
			{
			return;
			}

		long currentStepStart = lastStepEnd;
		long currentTime = System.currentTimeMillis();
// Or use 1 or 1.5 here too
		long currentStepEnd = currentTime + 2 * MiCanvas.ANIMATIONS_PER_SECOND;
		lastStepEnd = currentStepEnd;

		if (simpleAnimators.size() != 0)
			{
			for (int i = 0; i < simpleAnimators.size(); ++i)
				{
				if (simpleAnimatorsNextTick[i] == 0)
					{
					simpleAnimatorsNextTick[i] = currentTime;
					}
				//while (simpleAnimatorsNextTick[i] < currentStepEnd)
				if (simpleAnimatorsNextTick[i] < currentStepEnd)
					{
					long nextTickDelta = 0;
					MiiSimpleAnimator animator = (MiiSimpleAnimator )simpleAnimators.elementAt(i);
					if (!animatorsRunningWithAnotherThread.contains(animator))
						{
						animatorsRunningWithAnotherThread.addElement(animator);
						nextTickDelta = animator.animate();
						animatorsRunningWithAnotherThread.removeElement(animator);
						}

					if (nextTickDelta >= 0)
					    {
					    simpleAnimatorsNextTick[i] += nextTickDelta;
					    }
					else
					    {
					    removeAnimator(animator);
					    --i;
					    break;
					    }
					}
					
				}
			}

		for (int i = 0; i < animators.size(); ++i)
			{
			MiAnimatorEntry entry = (MiAnimatorEntry )animators.elementAt(i);
			MiiAnimator 	animator = entry.animator;
			boolean		scheduleEnd = false;

			if (!animator.getEnabled())
				continue;

			if (entry.timeStarted == 0)
				{
				long animatorStartTime = animator.getStartTime();
				if (currentStepEnd > animatorStartTime)
					{
					animator.start();
					if (animatorStartTime > currentStepStart)
						entry.timeStarted = animatorStartTime;
					else
						entry.timeStarted = currentTime;
					currentStepStart = currentTime;
					entry.lastStepTime = 0;
					}
				}
			if (entry.timeStarted > 0)
				{
				long stepTime = animator.getStepTime();
				long duration = animator.getDuration();

				if (currentStepEnd > entry.lastStepTime + stepTime)
					{
					double relativeStepStart = ((double )(currentStepStart - entry.timeStarted))/duration;
					double relativeStepEnd = ((double )(currentStepEnd - entry.timeStarted))/duration;
					if (relativeStepEnd >= 1.0)
						{
						scheduleEnd = true;
						relativeStepEnd = 1.0;
						}
					if (animator.getPacer() != null)
						{
						MiiAnimatorPacer pacer = animator.getPacer();
						relativeStepStart = pacer.transform(relativeStepStart);
						relativeStepEnd = pacer.transform(relativeStepEnd);
						}
					animator.animate(relativeStepStart, relativeStepEnd);
					if (scheduleEnd)
						{
						// FIX: handle life cycle...
						animator.end();
						if (animators.removeElement(entry))
							--i;
						break;
						}
					// This method makes intervals between renders too small sometimes
					//entry.lastStepTime += (currentStepEnd - entry.lastStepTime)/stepTime * stepTime;
					entry.lastStepTime = currentStepEnd;
					}
				}
			}
//System.out.println("end animate: currentTime = " + System.currentTimeMillis());
		}
	public		String		toString()
		{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < simpleAnimators.size(); ++i)
			{
			buf.append(simpleAnimators.elementAt(i).toString());
			}
		for (int i = 0; i < animators.size(); ++i)
			{
			buf.append(animators.elementAt(i).toString());
			}
		return(buf.toString());
		}
	}
class MiAnimatorEntry
	{
//	Vector		dependantAnimators;
	Object		userInfo;
	MiiAnimator	animator;
	long		timeStarted;
	long		lastStepTime;

			MiAnimatorEntry(MiiAnimator a)
		{
		animator = a;
		}
	public		String		toString()
		{
		return(animator.toString());
		}
	}
