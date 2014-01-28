
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

import java.awt.Color;

// Like orig StarTrek lites across bottom of main viewer
/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPropogateColorAnimator extends MiPartAnimator
	{
	private		boolean			forward;
	private		boolean			cycleFillColor;
	private		Color			color;
	private		Color 			lastColor;
	private		MiPart		lastObj;
	private		int			index;
	private		MiContainerIterator	iterator;

	public				MiPropogateColorAnimator(MiPart subjects, Color color)
		{
		this(subjects, true, true, color);
		}
	public				MiPropogateColorAnimator(MiPart subjects, boolean forward, boolean cycleFillColor, Color color)
		{
		super(subjects);
		this.forward = forward;
		this.cycleFillColor = cycleFillColor;
		this.color = color;
		}
	public		void		start()
		{
		lastObj = null;
		}

	public		void		animate(double startOfStep, double endOfStep)
		{
		if (getSubject() instanceof MiEditor)
			{
			cycleEditorContents();
			}
		else
			{
			cycleObjectContents();
			}
		}

	public		void		cycleEditorContents()
		{
		if (getSubject().getNumberOfParts() == 0)
			return;

		if (iterator == null)
			iterator = new MiContainerIterator(getSubject(), forward, false, false);
		if (lastObj != null)
			{
			if (cycleFillColor)
				lastObj.setBackgroundColor(lastColor);
			else
				lastObj.setColor(lastColor);
			}
		if (forward)
			lastObj = iterator.getNext();
		else
			lastObj = iterator.getPrevious();
				
		if (lastObj == null)
			{
			iterator = null;
			cycleEditorContents();
			}
		if (cycleFillColor)
			{
			lastColor = lastObj.getBackgroundColor();
			lastObj.setBackgroundColor(color);
			}
		else
			{
			lastColor = lastObj.getColor();
			lastObj.setColor(color);
			}
		}
	public		void		cycleObjectContents()
		{
		if (getSubject().getNumberOfParts() == 0)
			return;

		MiPart container = getSubject();
		if (lastObj != null)
			{
			if (cycleFillColor)
				lastObj.setBackgroundColor(lastColor);
			else
				lastObj.setColor(lastColor);
			}
		if (forward)
			{
			++index;
			if (index > container.getNumberOfParts() - 1)
				index = 0;
			}
		else
			{
			--index;
			if (index < 0)
				index = container.getNumberOfParts() - 1;
			}
		lastObj = container.getPart(index);
		if (cycleFillColor)
			{
			lastColor = lastObj.getBackgroundColor();
			lastObj.setBackgroundColor(color);
			}
		else
			{
			lastColor = lastObj.getColor();
			lastObj.setColor(color);
			}
		}
	}

