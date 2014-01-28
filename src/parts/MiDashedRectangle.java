
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
import java.util.Hashtable;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDashedRectangle extends MiPart
	{
	private		int		dashLength = 6;
	private		int		spaceLength = 6;

	public				MiDashedRectangle()
		{
		}

	protected	void		renderToDevice(
						java.awt.Graphics graphics, 
						MiAttributes attributes, 
						MiBounds deviceBounds)
		{
		int xmin = (int )deviceBounds.getXmin();
		int ymin = (int )deviceBounds.getYmin();
		int xmax = (int )deviceBounds.getXmax();
		int ymax = (int )deviceBounds.getYmax();

		if (attributes.getBackgroundColor() != Mi_TRANSPARENT_COLOR)
			{
			graphics.setColor(attributes.getBackgroundColor());
			graphics.fillRect(xmin, ymin, xmax - xmin, ymax - ymin);
			}

		graphics.setColor(attributes.getColor());
		for (int x1 = xmin; x1 < xmax; x1 += dashLength + spaceLength)
			{
			int x2 = x1 + dashLength;
			if (x2 > xmax)
				x2 = xmax;
			graphics.drawLine(x1, ymax, x2, ymax);
			graphics.drawLine(x1, ymin, x2, ymin);
			}
		for (int y1 = ymin; y1 < ymax; y1 += dashLength + spaceLength)
			{
			int y2 = y1 + dashLength;
			if (y2 > ymax)
				y2 = ymax;
			graphics.drawLine(xmin, y1, xmin, y2);
			graphics.drawLine(xmax, y1, xmax, y2);
			}
		}
	}

class MiDashedLine extends MiMultiPointShape
	{
	private		MiPoint		tmpPoint = new MiPoint();
	private		int		dashLength = 6;
	private		int		spaceLength = 6;

	public				MiDashedLine()
		{
		}

	protected	void		renderToDevice(
						java.awt.Graphics graphics, 
						MiAttributes attributes, 
						MiBounds deviceBounds)
		{
		int x1 = (int )deviceBounds.getXmin();
		int y1 = (int )deviceBounds.getYmin();
		int x2 = (int )deviceBounds.getXmax();
		int y2 = (int )deviceBounds.getYmax();

		if (getPointY(0) - getPointY(1) > 0)
			{
			int tmp = x1;
			x1 = x2;
			x2 = tmp;
			
			tmp = y1;
			y1 = y2;
			y2 = tmp;
			}
			
		int dy = y2 - y1;
		if (y2 - y1 < 0)
			dy = y1 - y2;

		int dx = x2 - x1;
		if (x2 - x1 < 0)
			dx = x1 - x2;

		double delta = ((double )(y2 - y1))/(x2 - x1);
		double ddx = dashLength/delta;
		double ddy = delta * dashLength;

		int num = dy/dashLength;
		if (dx > dy)
			num = dx/dashLength;

		double px1 = x1;
		double py1 = y1;
		double px2 = x1;
		double py2 = y1;
		while (num-- > 0)
			{
			px1 = px2;
			px2 += ddx;
			py1 = py2;
			py2 += ddy;
			graphics.drawLine((int )px1, (int )py1, (int )px2, (int )py2);
			}
		px2 = x2;
		py2 = y2;
		px1 = px2 - ddx;
		py1 = py2 - ddy;
		graphics.drawLine((int )px1, (int )py1, (int )px2, (int )py2);
			
/**** SOON
		// Handle line width...
		if (attributes.getBackgroundColor() != Mi_TRANSPARENT_COLOR)
			{
			graphics.setColor(attributes.getBackgroundColor());
			graphics.fillRect(xmin, ymin, xmax - xmin, ymax - ymin);
			}
****/
		}
	}




