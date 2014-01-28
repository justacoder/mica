
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
import java.awt.Image; 
import java.awt.Toolkit; 
import java.awt.image.PixelGrabber; 
import java.awt.image.ImageObserver; 
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

/**
 * This is much too slow to be usable...
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTransparencyAnimator extends MiPartAnimator
	{
	private		double		startTransparency;
	private		double		endTransparency;
	private		double		lastEndOfStep;
	private		boolean		overrodeDoubleBuffering;

	public				MiTransparencyAnimator(MiPart subject, double startTransparency, double endTransparency, double duration, double secondsBetweenTranslates)
		{
		super(subject, secondsBetweenTranslates);
		setDuration(duration);
		this.startTransparency = startTransparency;
		this.endTransparency = endTransparency;
		if (subject != null)
			setSubject(subject);
		}

	public		void		reverseDirection()
		{
		setDuration(lastEndOfStep * getDuration());
		resetTimer();
		double t = endTransparency;
		endTransparency = startTransparency;
		startTransparency = t;
		}
	public		void		setSubject(MiPart subject)
		{
		super.setSubject(subject);
		if (!subject.isDoubleBuffered())
			{
			overrodeDoubleBuffering = true;
			subject.setDoubleBuffered(true);
			}
		subject.preRenderToDoubleBuffer(subject.getRootWindow().getCanvas().getRenderer());
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

		double transparency;
		if (isAnimatingForward())
			transparency = (endTransparency - startTransparency) * endOfStep + startTransparency;
		else
			transparency = (startTransparency - endTransparency) * endOfStep + endTransparency;
			
		Image image = getSubject().getDoubleBuffer();
		image = setTransparency(image, transparency);
		getSubject().setDoubleBuffer(image);
		}

	public		Image		setTransparency(Image image, double transparency)
		{
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int[] origPixels = new int[width * height];
		PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, origPixels, 0, width);
		try 	{
			pg.grabPixels();
			} 
		catch (InterruptedException e)
			{
			System.out.println(this + " interrupted waiting for pixels!");
			return(null);
			}
		if ((pg.status() & ImageObserver.ABORT) != 0)
			{
			System.out.println(this + " image fetch aborted or errored");
			return(null);
			}
		int alpha = ((int )(transparency * 255)) << 24;
		int index = 0;
		int[] pixels = new int[width * height];
		for (int j = 0; j < height; j++)
			{
			for (int i = 0; i < width; i++)
				{
				pixels[index] = (origPixels[index] & 0x00ffffff) + alpha;
				++index;
				}
			}
		MemoryImageSource mis = new MemoryImageSource(
			width, height, ColorModel.getRGBdefault(), pixels, 0, width);

		image = Toolkit.getDefaultToolkit().createImage(mis);
		return(image);
		}

	public		void		end()
		{
		if ((overrodeDoubleBuffering) && (endTransparency == 1.0))
			{
			getSubject().setDoubleBuffered(false);
			}
		else
			{
			Image image = getSubject().getDoubleBuffer();
			image = setTransparency(image, endTransparency);
			getSubject().setDoubleBuffer(image);
			}
		super.end();
		}
	}
