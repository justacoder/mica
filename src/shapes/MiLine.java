
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
public class MiLine extends MiMultiPointShape
	{
	private static	MiBounds	tmpBounds	= new MiBounds();
	private static	MiPoint		tmpPoint	= new MiPoint();

	public				MiLine()
		{
		appendPoint(0, 0);
		appendPoint(0, 0);
		lineWidthDependant = true;
		}
	public				MiLine(MiPoint pt1, MiPoint pt2)
		{
		appendPoint(pt1.x, pt1.y);
		appendPoint(pt2.x, pt2.y);
		lineWidthDependant = true;
		}
	public				MiLine(MiCoord x1, MiCoord y1, MiCoord x2, MiCoord y2)
		{
		appendPoint(x1, y1);
		appendPoint(x2, y2);
		lineWidthDependant = true;
		}
	public		void		render(MiRenderer renderer)
		{
		if (getNumberOfPoints() < 2)
			{
			return;
			}
		if ((getLineStartStyle() != Mi_NONE) || (getLineEndStyle() != Mi_NONE))
			{
			MiLineEndsRenderer lineEndsRenderer = 
				(MiLineEndsRenderer )getAttributes().objectAttributes[Mi_LINE_ENDS_RENDERER];
			if (lineEndsRenderer != null)
				{
				MiDistance size;
				double 	angle;
				int 	numPoints	= getNumberOfPoints();
				MiCoord startPointX	= xPoints[0];
				MiCoord startPointY	= yPoints[0];
				MiCoord endPointX	= xPoints[numPoints - 1];
				MiCoord endPointY	= yPoints[numPoints - 1];
				MiPoint basePoint	= tmpPoint;

				int style = getLineStartStyle();
				if (style != Mi_NONE)
					{
					angle = getPointExitAngle(0) + Math.PI;
					size = getLineStartSize();
					basePoint.set(startPointX, startPointY);
					if (lineEndsRenderer.calcNewLineEndPoint(
						renderer, angle, style, size, basePoint))
						{
						xPoints[0] = basePoint.x;
						yPoints[0] = basePoint.y;
						}
					}
				style = getLineEndStyle();
				if (style != Mi_NONE)
					{
					angle = getPointEntryAngle(-1);
					size = getLineEndSize();
					basePoint.set(endPointX, endPointY);
					if (lineEndsRenderer.calcNewLineEndPoint(
						renderer, angle, style, size, basePoint))
						{
						xPoints[numPoints - 1] = basePoint.x;
						yPoints[numPoints - 1] = basePoint.y;
						}
					}
				renderer.setAttributes(getAttributes());
				renderer.drawLines(xPoints, yPoints, numPoints);
				xPoints[0] = startPointX;
				yPoints[0] = startPointY;
				xPoints[numPoints - 1] = endPointX;
				yPoints[numPoints - 1] = endPointY;
				lineEndsRenderer.render(this, renderer);
				}
			else
				{
				renderer.setAttributes(getAttributes());
				renderer.drawLines(xPoints, yPoints, getNumberOfPoints());
				}
			}
		else
			{
			renderer.setAttributes(getAttributes());
			renderer.drawLines(xPoints, yPoints, getNumberOfPoints());
			}
		}

	public		boolean		pick(MiBounds area)
		{
		if (getNumberOfAttachments() > 0)
			{
			if (getAttachments().pickObject(area) != null)
				return(true);
			}

		getBounds(tmpBounds);
		if (!tmpBounds.intersects(area))
			return(false);

		int numberOfPoints = getNumberOfPoints();
		if (getLineWidth() == 0)
			{
			for (int i = 1; i < numberOfPoints; ++i)
				{
				if (area.intersectsLine(
					xPoints[i - 1], yPoints[i - 1], 
					xPoints[i], yPoints[i]))
					{
					return(true);
					}
				}
			MiLineEndsRenderer lineEndsRenderer = 
				(MiLineEndsRenderer )getAttributes().objectAttributes[Mi_LINE_ENDS_RENDERER];

			if ((lineEndsRenderer.getLineEndBounds(this, tmpBounds, true))
				&& (area.intersects(tmpBounds)))
				{
				return(true);
				}
				
			if ((lineEndsRenderer.getLineEndBounds(this, tmpBounds, false))
				&& (area.intersects(tmpBounds)))
				{
				return(true);
				}
				
			return(false);
			}

		for (int i = 1; i < numberOfPoints; ++i)
			{
			if (area.intersectsLine(
				xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i], getLineWidth()))
				{
				return(true);
				}
			}
		MiLineEndsRenderer lineEndsRenderer = 
			(MiLineEndsRenderer )getAttributes().objectAttributes[Mi_LINE_ENDS_RENDERER];

		if ((lineEndsRenderer.getLineEndBounds(this, tmpBounds, true))
			&& (area.intersects(tmpBounds)))
			{
			return(true);
			}
				
		if ((lineEndsRenderer.getLineEndBounds(this, tmpBounds, false))
			&& (area.intersects(tmpBounds)))
			{
			return(true);
			}
		return(false);
		}
	protected 	void		calcPreferredSize(MiSize size)
		{
		super.calcPreferredSize(size);
		if (getAttributes().objectAttributes[Mi_LINE_ENDS_RENDERER] != null)
			{
			getBounds(tmpBounds);
			if (((MiiPartRenderer )getAttributes()
				.objectAttributes[Mi_LINE_ENDS_RENDERER]).getBounds(this, tmpBounds))
				{
				size.union(tmpBounds);
				}
			}
		}
	}

