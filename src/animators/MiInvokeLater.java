
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
public class MiInvokeLater implements MiiSimpleAnimator
	{
	private		MiPart 		window;
	private		MiiInvokable 	target; 
	private		Object 		data;
	private		long 		microsecondsUntilInvocation;
	private		boolean 	firstTime = true;


	public				MiInvokeLater(
						MiPart window, 
						MiiInvokable target)
		{
		this(window, target, null, 0);
		}
	public				MiInvokeLater(
						MiPart window, 
						MiiInvokable target, 
						Object data, 
						long microsecondsUntilInvocation)
		{
		this.window = window;
		this.target = target;
		this.data = data;
		this.microsecondsUntilInvocation = microsecondsUntilInvocation;

		MiAnimationManager.addAnimator(window, this);
		}
					/**------------------------------------------------------
				 	 * Performs some task.
					 * @return	number of microseconds until next call to 
					 *		animate(). Return -1 for 'forever'.
					 *------------------------------------------------------*/
	public		long		animate()
		{
		if ((firstTime) && (microsecondsUntilInvocation > 0))
			{
			firstTime = false;
			return(microsecondsUntilInvocation);
			}
		return(target.invocation(data));
		}
	}

