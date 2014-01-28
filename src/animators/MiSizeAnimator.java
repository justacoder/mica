
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
public class MiSizeAnimator extends MiPartAnimator
	{
	private		MiBounds	newBounds	= new MiBounds();
	private		MiBounds	begin;
	private		MiBounds	end;
	private		MiPoint		start;
	private		MiPoint		destination;
	private		MiReference	wrapper;
	private		MiPart		container;
	private		boolean		intoView;
	private		boolean		heightChanges;
	private		boolean		widthChanges;
	private		double		lastEndOfStep;

	public				MiSizeAnimator(MiPart subject, MiBounds begin, MiBounds end, boolean intoView, double duration, double secondsBetweenTranslates)
		{
		super(subject, secondsBetweenTranslates);
		setDuration(duration);
		this.intoView = intoView;
		this.end = end;
		this.begin = begin;
		setup();
		}

	public		void		reverseDirection()
		{
		setDuration(lastEndOfStep * getDuration());
		resetTimer();
		this.intoView = !intoView;
		MiBounds b = end;
		end = begin;
		begin = b;
		}
	public		void		setup()
		{
		MiPart subject = getSubject();
		container = subject.getContainer(0);
		if (container == null)
			throw new IllegalArgumentException("Animated object: \"" + subject + "\" needs to be part of some graph.\n");
		wrapper = new MiReference(subject);
		if (intoView)
			wrapper.setClipBounds(end);
		else
			wrapper.setClipBounds(begin);
		subject.setVisible(true);
		wrapper.validateLayout();
		wrapper.setBounds(begin);
		newBounds.copy(begin);
		container.setPart(wrapper, container.getIndexOfPart(subject));
		start = begin.getCenter();
		destination = end.getCenter();
		widthChanges = end.getWidth() != begin.getWidth();
		heightChanges = end.getHeight() != begin.getHeight();
		if (subject.isDoubleBuffered())
			{
			subject.preRenderToDoubleBuffer(wrapper.getContainingWindow().getCanvas().getRenderer());
			}
		}

	public		void		start()
		{
		}
	public		void		animate(double startOfStep, double endOfStep)
		{
		lastEndOfStep = endOfStep;

		// Don't draw twice near end...
		if ((endOfStep >= 0.99) && (!getPacer().hasFollowThrough()))
			return;

		MiCoord x = start.x + (destination.x  - start.x) * endOfStep;
		MiCoord y = start.y + (destination.y  - start.y) * endOfStep;
		if ((widthChanges) || (heightChanges))
			{
			if (widthChanges)
				{
				MiDistance width = begin.getWidth() + (end.getWidth() - begin.getWidth()) * endOfStep;
				newBounds.setWidth(width);
				}
			if (heightChanges)
				{
				MiDistance height = begin.getHeight() + (end.getHeight() - begin.getHeight()) * endOfStep;
				newBounds.setHeight(height);
				}
			newBounds.setCenter(x, y);
			wrapper.setBounds(newBounds);
			}
		else
			{
			wrapper.setCenter(x, y);
			}
		}
	public		void		end()
		{
		container.setPart(getSubject(), container.getIndexOfPart(wrapper));
		wrapper.removePart(getSubject());
		getSubject().setBounds(end);
		if (!intoView)
			getSubject().setVisible(false);
		super.end();
		}
	}

