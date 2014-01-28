
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiCycleColorsAnimator extends MiPartAnimator
	{
	private		boolean		forward 		= true;
	private		boolean		cycleFillColor 	= true;

	public				MiCycleColorsAnimator(MiPart subjects)
		{
		this(subjects, true, true);
		}
	public				MiCycleColorsAnimator(MiPart subject, boolean forward, boolean cycleFillColor)
		{
		super(subject);
		this.forward = forward;
		this.cycleFillColor = cycleFillColor;
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

	private		void		cycleEditorContents()
		{
		MiContainerIterator iterator = new MiContainerIterator(getSubject(), forward, false, false);
		Color color;
		MiPart obj;
		MiPart firstObj;
		Color lastColor;
		if (forward)
			firstObj = iterator.getNext();
		else
			firstObj = iterator.getPrevious();

		if (firstObj == null)
			return;

		if (cycleFillColor)
			color = firstObj.getBackgroundColor();
		else
			color = firstObj.getColor();

		if (forward)
			{
			while ((obj = iterator.getNext()) != null)
				{
				if (cycleFillColor)
					{
					lastColor = obj.getBackgroundColor();
					obj.setBackgroundColor(color);
					}
				else
					{
					lastColor = obj.getColor();
					obj.setColor(color);
					}
				color = lastColor;
				}
			}
		else
			{
			while ((obj = iterator.getPrevious()) != null)
				{
				if (cycleFillColor)
					{
					lastColor = obj.getBackgroundColor();
					obj.setBackgroundColor(color);
					}
				else
					{
					lastColor = obj.getColor();
					obj.setColor(color);
					}
				color = lastColor;
				}
			}
		if (cycleFillColor)
			firstObj.setBackgroundColor(color);
		else
			firstObj.setColor(color);
		}
	private		void		cycleObjectContents()
		{
		Color color;
		MiPart obj;
		MiPart firstObj;
		MiPart container = getSubject();
		Color lastColor;
		if (forward)
			firstObj = container.getPart(0);
		else
			firstObj = container.getPart(container.getNumberOfParts() - 1);

		if (firstObj == null)
			return;

		if (cycleFillColor)
			color = firstObj.getBackgroundColor();
		else
			color = firstObj.getColor();

		if (forward)
			{
			for (int i = 0; i < container.getNumberOfParts(); ++i)
				{
				obj = container.getPart(i);
				if (cycleFillColor)
					{
					lastColor = obj.getBackgroundColor();
					obj.setBackgroundColor(color);
					}
				else
					{
					lastColor = obj.getColor();
					obj.setColor(color);
					}
				color = lastColor;
				}
			}
		else
			{
			for (int i = container.getNumberOfParts() - 1; i >= 0; --i)
				{
				obj = container.getPart(i);
				if (cycleFillColor)
					{
					lastColor = obj.getBackgroundColor();
					obj.setBackgroundColor(color);
					}
				else
					{
					lastColor = obj.getColor();
					obj.setColor(color);
					}
				color = lastColor;
				}
			}
		if (cycleFillColor)
			firstObj.setBackgroundColor(color);
		else
			firstObj.setColor(color);
		}

	}

