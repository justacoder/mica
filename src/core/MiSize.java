
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


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSize
	{
	protected	MiDistance	width		= 0;
	protected	MiDistance	height		= 0;
	private static	FastVector	freePool	= new FastVector();
	private static	int		maxReportedSize;


	public				MiSize()
		{
		}
	public				MiSize(MiDistance width, MiDistance height)
		{
		this.width = width;
		this.height = height;
		}
	public				MiSize(MiDistance widthAndHeight)
		{
		this.width = widthAndHeight;
		this.height = widthAndHeight;
		}
	public				MiSize(MiSize size)
		{
		width = size.width;
		height = size.height;
		}
	public				MiSize(MiBounds b)
		{
		width = b.getWidth();
		height = b.getHeight();
		}
	public		MiDistance	getWidth()
		{
		return(width);
		}
	public		void		setWidth(MiDistance w)
		{
		width = w;
		}
	public		void		addWidth(MiDistance w)
		{
		width += w;
		}
	public		void		subtractWidth(MiDistance w)
		{
		width -= w;
		}
	public		MiDistance	getHeight()
		{
		return(height);
		}
	public		void		setHeight(MiDistance h)
		{
		height = h;
		}
	public		void		addHeight(MiDistance h)
		{
		height += h;
		}
	public		void		subtractHeight(MiDistance h)
		{
		height -= h;
		}
	public		void		zeroOut()
		{
		reset();
		}
	public		void		reset()
		{
		width = 0;
		height = 0;
		}
	public		void		copy(MiSize size)
		{
		width = size.width;
		height = size.height;
		}
	public		MiSize		copy()
		{
		return(new MiSize(width, height));
		}
	public		boolean		equals(MiSize other)
		{
		if ((other != null) && (com.swfm.mica.util.Utility.areZero(width - other.width, height - other.height)))
			{
			return(true);
			}
		return(false);
		}
	public		boolean		isSmallerSizeThan(MiSize other)
		{
		return((width < other.width) || (height < other.height));
		}
	public		void		setSize(MiBounds bounds)
		{
		width = bounds.getWidth();
		height = bounds.getHeight();
		}
	public		void		union(MiSize size)
		{
		if (size.width > width)
			width = size.width;
		if (size.height > height)
			height = size.height;
		}
	public		void		union(MiBounds bounds)
		{
		if (bounds.getWidth() > width)
			width = bounds.getWidth();
		if (bounds.getHeight() > height)
			height = bounds.getHeight();
		}
	public		MiSize		addMargins(MiMargins margin)
		{
		width += margin.left + margin.right;
		height += margin.bottom + margin.top;
		return(this);
		}
	public		void		addMargins(MiDistance distance)
		{
		width += distance;
		height += distance;
		}
	public		MiSize		subtractMargins(MiMargins m)
		{
		width -= m.left + m.right;
		height -= m.bottom + m.top;
		return(this);
		}
	public		void		setSize(MiDistance width, MiDistance height)
		{
		this.width = width;
		this.height = height;
		}
	public		void		accumulateMaxWidthAndHeight(MiSize size)
		{
		if (size.width > width)
			width = size.width;
		if (size.height > height)
			height = size.height;
		}
	public		void		accumulateMaxWidthAndHeight(MiBounds bounds)
		{
		if (bounds.getWidth() > width)
			width = bounds.getWidth();
		if (bounds.getHeight() > height)
			height = bounds.getHeight();
		}
	public		MiScale		fitPreservingAspectRatio(MiSize container)
		{
		MiScale scale = new MiScale(container.width/width, container.height/height);
		if (scale.x < scale.y)
			{
			scale.y = scale.x;
			}
		else
			{
			scale.x = scale.y;
			}
		return(scale);
		}
	public		String		toString()
		{
		return("[" + width + "x" + height + "]");
		}
	public static 	MiSize		newSize()
		{
		MiSize size = null;
		synchronized (freePool)
			{
			if (freePool.size() > 0)
				{
				size = (MiSize )freePool.lastElement();
				size.width = 0;
				size.height = 0;
				freePool.removeElementAt(freePool.size() - 1);
				}
			else
				{
				size = new MiSize();
//System.out.println("alloc size");
				}
			}
		return(size);
		}

	public static 	void		freeSize(MiSize size)
		{
		synchronized (freePool)
			{
			if (size == null)
				throw new IllegalArgumentException("Freeing NULL MiSize");
			freePool.addElement(size);
			}
		}

	}

