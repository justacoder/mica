
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
public class MiAnimator implements MiiAnimator
	{
	private		MiiAnimatable		animated;
	private		MiiAnimatorPacer	pacer 		= new MiLinearPacer();
	private		long			startTime;
	private		long			stepTime;
	private		long			duration;
	private		int			lifeCycle;
	private		boolean			enabled		= true;

	public					MiAnimator(MiiAnimatable subject, long stepTime)
		{
		this(subject, 0, stepTime, Long.MAX_VALUE);
		}
	public					MiAnimator(MiiAnimatable subject, long stepTime, long duration)
		{
		this(subject, 0, stepTime, duration);
		}
	public					MiAnimator(MiiAnimatable subject, long startTime, long stepTime, long duration)
		{
		this(subject, startTime, stepTime, duration, Mi_LIFE_CYCLE_STOP_AFTER_END);
		}
	public					MiAnimator(MiiAnimatable subject, long startTime, long stepTime, long duration, int lifeCycle)
		{
		this.animated = subject;
		this.startTime = startTime;
		this.stepTime = stepTime;
		this.duration = duration;
		this.lifeCycle = lifeCycle;
		}
	public		long			getStartTime()
		{
		return(startTime);
		}
	public		long			getStepTime()
		{
		return(stepTime);
		}
	public		void			setStepTime(long stepTime)
		{
		this.stepTime = stepTime;
		}
	public		long			getDuration()
		{
		return(duration);
		}
	public		void			setDuration(long millisecs)
		{
		duration = millisecs;
		}
	public		int			getLifeCycle()
		{
		return(lifeCycle);
		}
	public		MiiAnimatorPacer 	getPacer()
		{
		return(pacer);
		}
	public		void	 		setPacer(MiiAnimatorPacer pacer)
		{
		this.pacer = pacer;
		}
	
	public		void			start()
		{
		animated.start();
		}
	public		void			animate(double startOfStep, double endOfStep)
		{
		animated.animate(startOfStep, endOfStep);
		}
	public		void			end()
		{
		animated.end();
		}
	public		void			setEnabled(boolean flag)
		{
		enabled = flag;
		}
	public		boolean			getEnabled()
		{
		return(enabled);
		}
	}

