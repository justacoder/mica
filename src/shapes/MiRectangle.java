
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
 * Linewidth needs to be scaled when rectangle is scaled...
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiRectangle extends MiPart
	{
	private static	MiBounds	tmpBounds	= new MiBounds();
	private static	MiBounds	tmpBounds1	= new MiBounds();


	public				MiRectangle()
		{
		tmpBounds.setBounds(0,0,1,1);
		replaceBounds(tmpBounds);
		}

	public				MiRectangle(MiBounds b)
		{
		replaceBounds(b);
		}

	public				MiRectangle(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		tmpBounds.setBounds(xmin, ymin, xmax, ymax);
		replaceBounds(tmpBounds);
		}

	public		boolean		pick(MiBounds area)
		{
		if ((isPickableWhenTransparent()) || (getBackgroundColor() != Mi_TRANSPARENT_COLOR))
			return(super.pick(area));

		if (getNumberOfAttachments() > 0)
			{
			if (getAttachments().pickObject(area) != null)
				return(true);
			}

		getBounds(tmpBounds);
		if (!tmpBounds.intersects(area))
			return(false);

		if (getLineWidth() == 0)
			{
			if (area.intersectsLine(tmpBounds.xmin, tmpBounds.ymin, tmpBounds.xmax, tmpBounds.ymin))
				return(true);
			if (area.intersectsLine(tmpBounds.xmax, tmpBounds.ymin, tmpBounds.xmax, tmpBounds.ymax))
				return(true);
			if (area.intersectsLine(tmpBounds.xmin, tmpBounds.ymax, tmpBounds.xmax, tmpBounds.ymax))
				return(true);
			if (area.intersectsLine(tmpBounds.xmin, tmpBounds.ymin, tmpBounds.xmin, tmpBounds.ymax))
				return(true);
			return(false);
			}
		MiDistance lineWidth = getLineWidth();
		tmpBounds1.setBounds(tmpBounds.xmin, tmpBounds.ymin, tmpBounds.xmax, tmpBounds.ymin + lineWidth);
		if (area.intersects(tmpBounds1))
			return(true);
		tmpBounds1.setBounds(tmpBounds.xmax - lineWidth, tmpBounds.ymin, tmpBounds.xmax, tmpBounds.ymax);
		if (area.intersects(tmpBounds1))
			return(true);
		tmpBounds1.setBounds(tmpBounds.xmin, tmpBounds.ymax - lineWidth, tmpBounds.xmax, tmpBounds.ymax);
		if (area.intersects(tmpBounds1))
			return(true);
		tmpBounds1.setBounds(tmpBounds.xmin, tmpBounds.ymin, tmpBounds.xmin + lineWidth, tmpBounds.ymax);
		if (area.intersects(tmpBounds1))
			return(true);

		return(false);
		}
	public		void		render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		renderer.drawRect(getBounds(tmpBounds));
		}

	public		void		optimizedInvalidateDrawBoundsArea()
		{
		if ((getBackgroundColor() != Mi_TRANSPARENT_COLOR)
			|| (getBackgroundImage() != null)
			|| (getLineWidth() != 0)
			|| (getWidth() <= 10) || (getHeight() <= 10))
			{
			super.optimizedInvalidateDrawBoundsArea();
			return;
			}
		getBounds(tmpBounds);
		getDrawBounds(tmpBounds1);
		if (!tmpBounds.equals(tmpBounds1))
			{
			super.optimizedInvalidateDrawBoundsArea();
			return;
			}
		optimizedInvalidateArea();
		}
	public		void		optimizedInvalidateArea()
		{
		if ((getBackgroundColor() != Mi_TRANSPARENT_COLOR)
			|| (getBackgroundImage() != null)
			|| (getLineWidth() != 0)
			|| (getWidth() <= 10) || (getHeight() <= 10))
			{
			super.optimizedInvalidateArea();
			return;
			}

		getBounds(tmpBounds);

		tmpBounds1.setBounds(tmpBounds.xmin, tmpBounds.ymin - 1, tmpBounds.xmax, tmpBounds.ymin + 1);
		invalidateArea(tmpBounds1);

		tmpBounds1.setBounds(tmpBounds.xmax - 1, tmpBounds.ymin + 2, tmpBounds.xmax + 1, tmpBounds.ymax - 2);
		invalidateArea(tmpBounds1);

		tmpBounds1.setBounds(tmpBounds.xmin, tmpBounds.ymax - 1, tmpBounds.xmax, tmpBounds.ymax + 1);
		invalidateArea(tmpBounds1);

		tmpBounds1.setBounds(tmpBounds.xmin - 1, tmpBounds.ymin + 2, tmpBounds.xmin + 1, tmpBounds.ymax - 2);
		invalidateArea(tmpBounds1);
		}
	}

