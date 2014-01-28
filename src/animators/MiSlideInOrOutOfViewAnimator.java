
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

public class MiSlideInOrOutOfViewAnimator extends MiPartAnimator implements MiiTypes
	{
	private		MiBounds	newBounds	= new MiBounds();
	private		MiBounds	begin;
	private		MiBounds	end;
	private		MiPoint		start;
	private		MiPoint		destination;
	private		MiReference	wrapper;
	private		MiPart		container;
	private		int		directionSpec;
	private		boolean		intoViewSpec;
	private		boolean		heightChanges;
	private		boolean		widthChanges;
	private		double		lastEndOfStep;

	public				MiSlideInOrOutOfViewAnimator(MiPart subject, int direction, boolean slideIntoView, double duration, double secondsBetweenTranslates)
		{
		super(subject, secondsBetweenTranslates);
		setDuration(duration);
		intoViewSpec = slideIntoView;
		directionSpec = direction;
		if (subject != null)
			setSubject(subject);
		}

	public		void		reverseDirection()
		{
		setIsAnimatingForward(!isAnimatingForward());
		setDuration(lastEndOfStep * getDuration());
		resetTimer();
		//intoView = !intoView;
		MiBounds b = end;
		end = begin;
		begin = b;
		MiPoint p = start;
		start = destination;
		destination = p;
		}
	public		void		setSubject(MiPart subject)
		{
		super.setSubject(subject);
		container = subject.getContainer(0);
		if (container == null)
			throw new IllegalArgumentException("Animated object: \"" + subject + "\" needs to be part of some graph.\n");
		wrapper = new MiReference(subject);
		if (!subject.isVisible())
			subject.setVisible(true);
		wrapper.validateLayout();
		begin = subject.getBounds();
		end = new MiBounds(begin);
		int direction = directionSpec;
		boolean intoView = intoViewSpec;
		if (!isAnimatingForward())
			{
			intoView = !intoViewSpec;
			switch (direction)
				{
				case Mi_DOWN :
					direction = Mi_UP;
					break;
				case Mi_UP :
					direction = Mi_DOWN;
					break;
				case Mi_LEFT :
					direction = Mi_LEFT;
					break;
				case Mi_RIGHT :
					direction = Mi_RIGHT;
					break;
				default:
					break;
				}
			}
		switch (direction)
			{
			case Mi_DOWN :
				if (intoView)
					begin.translate(0, begin.getHeight());
				else
					end.translate(0, -begin.getHeight());
				break;
			case Mi_UP :
				if (intoView)
					begin.translate(0, -begin.getHeight());
				else
					end.translate(0, begin.getHeight());
				break;
			case Mi_LEFT :
				if (intoView)
					begin.translate(begin.getHeight(), 0);
				else
					end.translate(-begin.getHeight(), 0);
				break;
			case Mi_RIGHT :
				if (intoView)
					begin.translate(-begin.getHeight(), 0);
				else
					end.translate(begin.getHeight(), 0);
				break;
			default:
				break;
			}
		if (intoView)
			wrapper.setClipBounds(end);
		else
			wrapper.setClipBounds(begin);
		wrapper.setBounds(begin);
		newBounds.copy(begin);
		container.setPart(wrapper, container.getIndexOfPart(subject));
		start = begin.getCenter();
		destination = end.getCenter();
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
		wrapper.setCenter(x, y);
		}
	public		void		end()
		{
		container.setPart(getSubject(), container.getIndexOfPart(wrapper));
		wrapper.removePart(getSubject());
		getSubject().setCenter(destination);
		super.end();
		}
	}

