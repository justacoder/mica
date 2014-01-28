
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
public class MiCurrentTimeStatusField extends MiStatusBar implements MiiSimpleAnimator
	{
	private		MiWidget	field;
	private		String		CURRENT_TIME_AREA_TOOL_HINT	= "Current time";
	private		int		secondsBetweenExaminingCurrentTime	= 1;
	private 	int		TIME_FIELD_INDEX			= 0;
	private 	boolean		appendAMAndPM;


	public				MiCurrentTimeStatusField(MiAnimationManager aMan,
									String spec, int fieldIndex,
									boolean appendAMAndPM)
		{
		super(spec);
		TIME_FIELD_INDEX = fieldIndex;
		this.appendAMAndPM = appendAMAndPM;
		setup(aMan);
		}

	public				MiCurrentTimeStatusField(MiAnimationManager aMan)
		{
		this(aMan, true);
		}

	public				MiCurrentTimeStatusField(MiAnimationManager aMan, boolean appendAMAndPM)
		{
		super("." + (appendAMAndPM ? 11 : 8));
		this.appendAMAndPM = appendAMAndPM;
		setup(aMan);
		}

	protected	void		setup(MiAnimationManager aMan)
		{
		setBorderLook(Mi_NONE);
		setInsetMargins(0);
		field = getField(TIME_FIELD_INDEX);
		field.setToolHintMessage(CURRENT_TIME_AREA_TOOL_HINT);

		updateCurrentTime();
		if (aMan != null)
			aMan.addAnimator(this);
		}

	protected	void		updateCurrentTime()
		{
		java.util.Date date = new java.util.Date();
		int hoursValue = date.getHours();
		String ampmStr = "";
		if (appendAMAndPM)
			{
			if (hoursValue > 12)
				{
				hoursValue -= 12;
				ampmStr = " pm";
				}
			else
				{
				ampmStr = " am";
				}
			}

		String hours = hoursValue + "";
		if (hours.length() < 2)
			hours = " " + hours;
		String minutes = date.getMinutes() + "";
		if (minutes.length() < 2)
			minutes = "0" + minutes;
		String seconds = date.getSeconds() + "";
		if (seconds.length() < 2)
			seconds = "0" + seconds;
		field.setValue(hours + ":" + minutes + ":" + seconds + ampmStr);
		}
	public		long		animate()
		{
		updateCurrentTime();
		return(secondsBetweenExaminingCurrentTime * 1000);
		}
	}

