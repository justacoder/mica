
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
public class MiPolygon extends MiMultiPointShape
	{
	private		boolean 	closed		= true;
	private static	MiBounds 	tmpBounds 	= new MiBounds();


	public				MiPolygon()
		{
		lineWidthDependant = true;
		}

	public		void		setClosed(boolean flag)
		{
		closed = flag;
		}
	public		boolean		getClosed()
		{
		return(closed);
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @param source	the part to copy
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);
		closed 			= ((MiPolygon )source).closed;
		}

	public		void		render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		renderer.drawPolygon(xPoints, yPoints, closed);
		}


	public		boolean		pick(MiBounds area)
		{
		MiBounds bounds = getBounds(tmpBounds);
		if (!bounds.intersects(area))
			return(false);

		if (area.contains(bounds))
			return(true);

		if (getNumberOfAttachments() > 0)
			{
			if (getAttachments().pick(area))
				return(true);
			}

		int numPoints = getNumberOfPoints();
		if (numPoints < 3)
			return(false);

		if ((!isPickableWhenTransparent()) && (getBackgroundColor() == Mi_TRANSPARENT_COLOR))
			{
			if (getLineWidth() == 0)
				{
				for (int i = 1; i < numPoints; ++i)
					{
					if (area.intersectsLine(
						xPoints[i - 1], yPoints[i - 1], 
						xPoints[i], yPoints[i]))
						{
						return(true);
						}
					}
				return(false);
				}
	
			for (int i = 1; i < numPoints; ++i)
				{
				if (area.intersectsLine(
					xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i], getLineWidth()))
					{
					return(true);
					}
				}
			return(false);
			}

		/*
		* Based on an algorithm by Bob Stein
		* (http://www.gcomm.com/develop/testpoly.c)
		* See Linux Journal, Issue #35, March 1997 pg. 67 - 72
		* If polygon fails to intersect pick area center, should also check to see if any
		* side of the area (represented as a line) crosses any line of the polygon.
		*/
		MiCoord oldPtX = xPoints[numPoints - 1];
		MiCoord oldPtY = yPoints[numPoints - 1];
		MiCoord x = area.getCenterX();
		MiCoord y = area.getCenterY();
		MiCoord x1, y1, x2, y2;
		boolean inside = false;

		for (int i = 0; i < numPoints; ++i)
			{
			MiCoord newPtX = xPoints[i];
			MiCoord newPtY = yPoints[i];
			if (newPtX > oldPtX)
				{
				x1 = oldPtX;
				y1 = oldPtY;
				x2 = newPtX;
				y2 = newPtY;
				}
			else
				{
				x1 = newPtX;
				y1 = newPtY;
				x2 = oldPtX;
				y2 = oldPtY;
				}
			/* edge open at one end */
			if (((newPtX < x) == (x <= oldPtX))
				&& ((y - y1) * (x2 - x1) < (y2 - y1) * (x - x1)))
				{
				inside = !inside;
				}
			oldPtX = newPtX;
			oldPtY = newPtY;
			}
		return(inside);
		}
	}

