
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
public class MiEllipse extends MiPart
	{
	private static	MiBounds	tmpBounds	= new MiBounds();



	public				MiEllipse()
		{
		}

	public				MiEllipse(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		replaceBounds(new MiBounds(xmin, ymin, xmax, ymax));
		}
	public				MiEllipse(MiBounds b)
		{
		replaceBounds(b);
		}

	public		boolean		getIntersectionWithLine(
						MiPoint insidePoint, 
						MiPoint otherPoint, 
						MiPoint returnedIntersectionPoint)
		{
		MiDistance dx = otherPoint.x - insidePoint.x;
		MiDistance dy = otherPoint.y - insidePoint.y;

		getBounds(tmpBounds);
		if ((dx == 0) || (dy == 0))
			{
			return(tmpBounds.getIntersectionWithLine(
				insidePoint, otherPoint, returnedIntersectionPoint));
			}

		MiCoord centerX = (tmpBounds.xmax + tmpBounds.xmin)/2;
		MiCoord centerY = (tmpBounds.ymax + tmpBounds.ymin)/2;

		/*
		FIX: Add code for arbitrary point inside: See pg 251. notebook 12/5/96
		* Assuming: ((insidePoint.x == centerX) && (insidePoint.y == centerY))
		*/
		MiDistance length = Math.sqrt(
				(otherPoint.x - centerX) * (otherPoint.x - centerX) + 
				(otherPoint.y - centerY) * (otherPoint.y - centerY));
		MiDistance xRadius = (tmpBounds.xmax - tmpBounds.xmin)/2;
		MiDistance yRadius = (tmpBounds.ymax - tmpBounds.ymin)/2;
		returnedIntersectionPoint.x = xRadius * (otherPoint.x - centerX)/length + centerX;
		returnedIntersectionPoint.y = yRadius * (otherPoint.y - centerY)/length + centerY;
		return(true);
		}
	public		boolean		pick(MiBounds area)
		{
		if (getNumberOfAttachments() > 0)
			{
			if (getAttachments().pickObject(area) != null)
				return(true);
			}
		// ---------------------------------------------------------------
		// Verify pick distance from center of ellipse is AOK
		// ---------------------------------------------------------------
		MiCoord centerX = getCenterX();
		MiCoord centerY = getCenterY();
		MiDistance distanceToArea = Math.sqrt(
				(area.getCenterX() - centerX) * (area.getCenterX() - centerX) + 
				(area.getCenterY() - centerY) * (area.getCenterY() - centerY));
		MiDistance xRadius = (tmpBounds.xmax - tmpBounds.xmin)/2;
		MiDistance yRadius = (tmpBounds.ymax - tmpBounds.ymin)/2;
		MiCoord ellipseX = xRadius * (area.getCenterX() - centerX)/distanceToArea + centerX;
		MiCoord ellipseY = yRadius * (area.getCenterY() - centerY)/distanceToArea + centerY;
		MiDistance distanceToEllipse = Math.sqrt(
				(ellipseX - centerX) * (ellipseX - centerX) + 
				(ellipseY - centerY) * (ellipseY - centerY));
		MiDistance pickHalfWidth = area.getWidth()/2;
		if ((distanceToArea - pickHalfWidth > distanceToEllipse) 
			|| ((distanceToArea + pickHalfWidth < distanceToEllipse) 
			&& (getBackgroundColor() == Mi_TRANSPARENT_COLOR)
			&& (!isPickableWhenTransparent())))
			{
			return(false);
			}
		return(true);
		}

	public		void		render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		renderer.drawEllipse(getBounds(tmpBounds));
		}
	}

