
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
public class MiControlPointManager extends MiManagedPointManager
	{
	private	static	MiControlPointRule	globalRule	= new MiControlPointRule();
	private	static	MiPart			globalLook	= new MiCircle(0,0,8);
	private static	boolean			globalVisiblity;
	private static	boolean			globallyHidden;


	public					MiControlPointManager()
		{
		}
	public static	void			setGlobalLook(MiPart look)
		{
		globalLook = look;
		if (globalLook != null)
			globalLook.validateLayout();
		}
	public static	MiPart			getGlobalLook()
		{
		return(globalLook);
		}
	public static	void			setGlobalRule(MiControlPointRule rule)
		{
		globalRule = rule;
		}
	public static	MiControlPointRule	getGlobalRule()
		{
		return(globalRule);
		}
	public static	void			setGloballyVisible(boolean flag)
		{
		globalVisiblity = flag;
		}
	public static	boolean			isGloballyVisible()
		{
		return(globalVisiblity);
		}
	public static	void			setGloballyHidden(boolean flag)
		{
		globallyHidden = flag;
		}
	public static	boolean			isGloballyHidden()
		{
		return(globallyHidden);
		}
	public		boolean			isVisible()
		{
		return(globalVisiblity && (isLocallyVisible()));
		}
	public		boolean			isHidden()
		{
		return(globallyHidden || (isLocallyHidden()));
		}
	public		MiPart			getLook()
		{
		if (getLocalLook() != null)
			return(getLocalLook());
		return(globalLook);
		}
	public		MiPart			getLook(MiManagedPoint point)
		{
		if (point.getLook() != null)
			return(point.getLook());
		if (getLocalLook() != null)
			return(getLocalLook());
		return(globalLook);
		}
	public		MiiManagedPointRule	getRule()
		{
		if (getLocalRule() != null)
			return(getLocalRule());
		return(globalRule);
		}
	public		MiControlPointRule	getRule(MiManagedPoint point)
		{
		if (point.getRule() != null)
			return((MiControlPointRule )point.getRule());
		if (getLocalRule() != null)
			return((MiControlPointRule )getLocalRule());
		return(globalRule);
		}

	public		MiManagedPointManager	getManager(MiPart part)
		{
		return(part.getControlPointManager());
		}
	public		MiManagedPointManager copy()
		{
		MiControlPointManager manager = new MiControlPointManager();
		manager.copy(this);
		return(manager);
		}
	}
