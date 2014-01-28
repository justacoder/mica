
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
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class Mi3PointArc extends MiMultiPointShape
	{
	private static	MiBounds	tmpBounds	= new MiBounds();
	private		MiDistance	radius;
	private		MiDistance	calculatedOrthoDistance;
	private		MiDistance	orthoDistance;	// the distance from the chord mid-point to the arc
	private		MiPoint		center		= new MiPoint();
	private		double		startAngle 	= 0.0;
	private		double		sweptAngle	= 90.0;
	private static	MiPoint		startPt		= new MiPoint();	// tmp
	private static	MiPoint		ctlPt		= new MiPoint();	// tmp
	private static	MiPoint		endPt		= new MiPoint();	// tmp
	private static	MiPoint		tmpPoint	= new MiPoint();



	public				Mi3PointArc()
		{
		appendPoint(new MiPoint());
		appendPoint(new MiPoint());
		appendPoint(new MiPoint());
		lineWidthDependant = true;
		}
	public				Mi3PointArc(MiPoint startPt, MiPoint ctlPt)
		{
		appendPoint(new MiPoint(startPt));
		appendPoint(new MiPoint((startPt.x + ctlPt.x)/2, (startPt.y + ctlPt.y)/2));
		appendPoint(new MiPoint(ctlPt));
		lineWidthDependant = true;
		}
	public				Mi3PointArc(MiCoord x1, MiCoord y1, MiCoord x2, MiCoord y2)
		{
		appendPoint(new MiPoint(x1, y1));
		appendPoint(new MiPoint((x1 + x2)/2, (y1 + y2)/2));
		appendPoint(new MiPoint(x2, y2));
		lineWidthDependant = true;
		}
					// This is not valid if getOrthoDistance() == 0
	public		double		getStartAngle()
		{
		return(startAngle);
		}
	public		double		getEndAngle()
		{
		return(startAngle + sweptAngle);
		}
					// This is not valid if getOrthoDistance() == 0
	public		double		getSweptAngle()
		{
		return(sweptAngle);
		}
	public		void		setOrthoDistance(MiDistance d)
		{
		orthoDistance = d;

		invalidateArea();
		refreshBounds();
		}
	public		MiDistance	getOrthoDistance()
		{
		return(orthoDistance);
		}
					/**------------------------------------------------------
					 * Get the angle of the shape as it exits the point with 
					 * the given number. Points are numbered from 0. 
					 * @param pointNum 	the number of the point
					 * @return		the angle in radians
					 *------------------------------------------------------*/
	public		double		getPointExitAngle(int pointNumber)
		{
		if (orthoDistance == 0)
			{
			return(super.getPointExitAngle(pointNumber));
			}
		double angle;
		if (pointNumber == 0)
			angle = (90 + startAngle) * Math.PI/180;
		else if (pointNumber == 1)
			angle = (90 + startAngle + sweptAngle/2) * Math.PI/180;
		else // if pointNumber == 2 || -1)
			angle = (90 + startAngle + sweptAngle) * Math.PI/180;
		if (sweptAngle < 0)
			angle += Math.PI;
		return(angle);
		}
					/**------------------------------------------------------
					 * Get the angle of the shape as it enters the point with 
					 * the given number. Points are numbered from 0. 
					 * Mi_LAST_POINT_NUMBER is also a valid point number.
					 * @param pointNum 	the number of the point
					 * @return		the angle in radians
					 *------------------------------------------------------*/
	public		double		getPointEntryAngle(int pointNumber)
		{
		if (orthoDistance == 0)
			{
			return(super.getPointEntryAngle(pointNumber));
			}
		return(getPointExitAngle(pointNumber));
		}
					/**------------------------------------------------------
	 				 * Rotates this MiPart the given number of radians about
					 * the given point.
	 				 * @param center	the center of rotation
	 				 * @param radians	the angle to rotate
					 *------------------------------------------------------*/
	protected	void		doRotate(MiPoint center, double radians)
		{
		if (Utility.isZero(radians))
			return;

		super.doRotate(center, radians);

		double sinR = Math.sin(radians);
		double cosR = Math.cos(radians);
		
		MiCoord x = this.center.x - center.x;
		MiCoord y = this.center.y - center.y;
		this.center.x = -sinR * y + cosR * x + center.x;
		this.center.y = sinR * x + cosR * y + center.y;
		}
					/**------------------------------------------------------
	 				 * Translates the parts of this MiPart by the given 
					 * distances. 
	 				 * @param tx	 	the x translation
	 				 * @param ty	 	the y translation
					 *------------------------------------------------------*/
	protected	void		translateParts(MiDistance tx, MiDistance ty)
		{
		super.translateParts(tx, ty);
		center.x += tx;
		center.y += ty;
		}
					/**------------------------------------------------------
	 				 * Scales the parts of this MiPart by the given scale
					 * factor.
	 				 * @param center 	the center of scaling
	 				 * @param scale	 	the scale factor
					 *------------------------------------------------------*/
	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		super.scaleParts(center, scale);
		this.center.x = (this.center.x - center.x) * scale.x + center.x;
		this.center.y = (this.center.y - center.y) * scale.y + center.y;
		}
	public		void		render(MiRenderer renderer)
		{
		if ((getLineStartStyle() != Mi_NONE) || (getLineEndStyle() != Mi_NONE))
			{
			MiLineEndsRenderer lineEndsRenderer = 
				(MiLineEndsRenderer )getAttributes().objectAttributes[Mi_LINE_ENDS_RENDERER];
			//if (lineEndsRenderer != null)
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
				if (orthoDistance == 0)
					{
					renderer.drawLines(xPoints, yPoints, numPoints);
					}
				else
					{
					tmpBounds.xmin = center.x - radius;
					tmpBounds.ymin = center.y - radius;
					tmpBounds.xmax = center.x + radius;
					tmpBounds.ymax = center.y + radius;
					renderer.drawCircularArc(tmpBounds, startAngle, sweptAngle);
					}
				xPoints[0] = startPointX;
				yPoints[0] = startPointY;
				xPoints[numPoints - 1] = endPointX;
				yPoints[numPoints - 1] = endPointY;
				lineEndsRenderer.render(this, renderer);
				}
			}
		else
			{
			renderer.setAttributes(getAttributes());
			if (orthoDistance == 0)
				{
				renderer.drawLines(xPoints, yPoints, getNumberOfPoints());
				}
			else
				{
				tmpBounds.xmin = center.x - radius;
				tmpBounds.ymin = center.y - radius;
				tmpBounds.xmax = center.x + radius;
				tmpBounds.ymax = center.y + radius;
				renderer.drawCircularArc(tmpBounds, startAngle, sweptAngle);
				}
			}
		}

					/**------------------------------------------------------
					 * Realculates the outer bounds of this MiPart. 
					 * @param bounds	the (returned) outer bounds
					 * @overrides		MiMultiPointShape#reCalcBounds
					 *------------------------------------------------------*/
	protected	void		reCalcBounds(MiBounds bounds)
		{
		if ((getNumberOfPoints() == 3) && (orthoDistance == 0))
			{
			orthoDistance = 0.00001;
			}

		if (calculatedOrthoDistance != orthoDistance)
			{
			reCalc3rdPtHandleLocation();
			calculatedOrthoDistance = orthoDistance;
			}
		reCalcArcFrom3Pts();
		
		if (orthoDistance <= 0.00002)
			{
			orthoDistance = 0;
			}

		super.reCalcBounds(bounds);
		if (orthoDistance == 0)
			{
			return;
			}

		double startAngle = this.startAngle;
		double endAngle = startAngle + sweptAngle;
		if (sweptAngle < 0)
			{
			double tmp = endAngle;
			endAngle = startAngle;
			startAngle = tmp;
			}
		while (startAngle < 0)
			{
			startAngle += 360;
			endAngle += 360;
			}

		MiDistance halfLineWidth = getLineWidth()/2;

		if ((startAngle <= 0) && (endAngle >= 0))
			bounds.union(center.x + radius + halfLineWidth, center.y);
		if ((startAngle <= 90) && (endAngle >= 90))
			bounds.union(center.x, center.y + radius + halfLineWidth);
		if ((startAngle <= 180) && (endAngle >= 180))
			bounds.union(center.x - radius - halfLineWidth, center.y);
		if ((startAngle <= 270) && (endAngle >= 270))
			bounds.union(center.x, center.y - radius - halfLineWidth);
		if ((startAngle <= 360) && (endAngle >= 360))
			bounds.union(center.x + radius + halfLineWidth, center.y);
		if ((startAngle <= 450) && (endAngle >= 450))
			bounds.union(center.x, center.y + radius + halfLineWidth);
		if ((startAngle <= 540) && (endAngle >= 540))
			bounds.union(center.x - radius - halfLineWidth, center.y);
		if ((startAngle <= 630) && (endAngle >= 630))
			bounds.union(center.x, center.y - radius - halfLineWidth);
		}
	public		MiPoint		getRelativeLocation(
						int pointNumber, 
						MiBounds boundsToPosition, 
						MiPoint pt, 
						MiMargins margins)
		{
		if (pointNumber == Mi_LINE_CENTER_LOCATION)
			{
			pt.set(xPoints[1], yPoints[1]);
			return(pt);
			}
		else if (pointNumber == Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION)
			{
			if ((Math.abs(xPoints[1] - center.x) > Math.abs(yPoints[1] - center.y)))
				{
				// Horizontal
				pt.x = xPoints[1];
				pt.y = yPoints[1] + boundsToPosition.getHeight()/2 + margins.bottom;
				}
			else
				{
				pt.x = xPoints[1] + boundsToPosition.getWidth()/2 + margins.left;
				pt.y = yPoints[1];
				}
			return(pt);
			}
		else if (pointNumber == Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION)
			{
			if ((Math.abs(xPoints[1] - center.x) > Math.abs(yPoints[1] - center.y)))
				{
				// Horizontal
				pt.x = xPoints[1];
				pt.y = yPoints[1] - boundsToPosition.getHeight()/2 - margins.top;
				}
			else
				{
				pt.x = xPoints[1] - boundsToPosition.getWidth()/2 - margins.right;
				pt.y = yPoints[1];
				}
			return(pt);
			}
		return(super.getRelativeLocation(pointNumber, boundsToPosition, pt, margins));
		}

	public		boolean		pick(MiBounds area)
		{
		if (getNumberOfAttachments() > 0)
			{
			if (getAttachments().pickObject(area) != null)
				return(true);
			}
		if (getLineWidth() != 0)
			{
			area = new MiBounds(area);
			area.addMargins(getLineWidth()/2);
			}

		if (!area.intersects(getDrawBounds(tmpBounds)))
			{
			return(false);
			}
		if (orthoDistance == 0)
			{
			return(area.intersectsLine(
				xPoints[0], yPoints[0], 
				xPoints[2], yPoints[2]));
			}

		// ---------------------------------------------------------------
		// Verify pick distance from center of arc is AOK
		// ---------------------------------------------------------------
		double distToArea = Math.sqrt(center.getDistanceSquared(area.getCenter(tmpPoint)));
		MiDistance pickHalfWidth = area.getWidth()/2;
		if ((distToArea - pickHalfWidth > radius) 
			|| ((distToArea + pickHalfWidth < radius) 
			&& (getBackgroundColor() == Mi_TRANSPARENT_COLOR)))
			{
			return(false);
			}
		// ---------------------------------------------------------------
		// Now verify angle to pick area is AOK
		// FIX: this does not take into account the width of the pick Area
		// ---------------------------------------------------------------
		double angle = Utility.getAngle(tmpPoint.y - center.y, tmpPoint.x - center.x) * 180/Math.PI;
		double startAngle = this.startAngle;
		double endAngle = startAngle + sweptAngle;
		if (sweptAngle < 0)
			{
			double tmp = endAngle;
			endAngle = startAngle;
			startAngle = tmp;
			}
		if (startAngle < 0)
			{
			startAngle += 360;
			endAngle += 360;
			}

		if ((angle >= startAngle) && (angle <= endAngle))
			{
			return(true);
			}
		angle += 360;
		if ((angle >= startAngle) && (angle <= endAngle))
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
	protected	void		reCalc3rdPtHandleLocation()
		{
		getPoint(0, startPt);
		getPoint(1, ctlPt);
		getPoint(2, endPt);

		tmpPoint.copy(ctlPt);

		if (orthoDistance == 0)
			{
			MiCoord midPointX = (endPt.x + startPt.x)/2;
			MiCoord midPointY = (endPt.y + startPt.y)/2;
			ctlPt.x = midPointX;
			ctlPt.y = midPointY;
			}
		// If horizontal line...
		else if (endPt.y - startPt.y == 0)
			{
			ctlPt.x = (endPt.x + startPt.x)/2;
			MiDistance radius = Math.abs(endPt.x - startPt.x)/2;
			if (ctlPt.y - startPt.y > radius)
				ctlPt.y = startPt.y + radius;
			else if (ctlPt.y - startPt.y < -radius)
				ctlPt.y = startPt.y - radius;

			orthoDistance = ctlPt.y - startPt.y;
			}
		// If vertical line...
		else if (endPt.x - startPt.x == 0)
			{
			ctlPt.y = (endPt.y + startPt.y)/2;
			MiDistance radius = Math.abs(endPt.y - startPt.y)/2;
			if (ctlPt.x - startPt.x > radius)
				ctlPt.x = startPt.x + radius;
			else if (ctlPt.x - startPt.x < -radius)
				ctlPt.x = startPt.x - radius;

			orthoDistance = ctlPt.x - startPt.x;
			}
		else
			{
			MiCoord midPointX = (endPt.x + startPt.x)/2;
			MiCoord midPointY = (endPt.y + startPt.y)/2;

			// Get slope of chord and normal to chord
			double m1 = (endPt.y - startPt.y)/(endPt.x - startPt.x);
			double m2 = -1/m1;

			MiCoord closestX = (m1 * ctlPt.x - ctlPt.y - m2 * midPointX + midPointY)/(m1 - m2);
			MiCoord closestY = m2 * (closestX - midPointX) + midPointY;
//System.out.println("m1 = " + m1);
//System.out.println("m2 = " + m2);
//System.out.println("midPointX = " + midPointX);
//System.out.println("midPointY = " + midPointY);
//System.out.println("ctlPt.x = " + ctlPt.x);
//System.out.println("ctlPt.y = " + ctlPt.y);
//System.out.println("closestX = " + closestX);
//System.out.println("closestY = " + closestY);
			MiDistance dist = Math.sqrt((closestY - midPointY) * (closestY - midPointY)
				+ (closestX - midPointX) * (closestX - midPointX));
			MiDistance maxRadius = Math.sqrt((endPt.x - midPointX) * (endPt.x - midPointX) 
				+ (endPt.y - midPointY) * (endPt.y - midPointY));
			//if (dist <= maxRadius)
			if (true)
				{
				ctlPt.x = closestX;
				ctlPt.y = closestY;
				orthoDistance = dist;
//System.out.println("dist = " + dist);
				}
			else
				{
				double normalAngle = Utility.getAngle(endPt.x - startPt.x, endPt.y - startPt.y);
//System.out.println("normalAngle = " + normalAngle);
//System.out.println("maxRadius * Math.cos(normalAngle) = " + maxRadius * Math.cos(normalAngle));
				MiCoord maxX = maxRadius * Math.cos(normalAngle) + midPointX;
				MiCoord maxY = maxRadius * Math.sin(normalAngle) + midPointY;
				ctlPt.x = maxX;
				ctlPt.y = maxY;
				orthoDistance = maxRadius;
//System.out.println("maxRadius = " + maxRadius);
				}
			}
		if (!tmpPoint.equals(ctlPt))
			{
//MiDebug.println(this + ".SETTING xPoints to" + ctlPt);
			xPoints[1] = ctlPt.x;
			yPoints[1] = ctlPt.y;
			}
		}
	protected	void		reCalcArcFrom3Pts()
		{
		getPoint(0, startPt);
		getPoint(1, ctlPt);
		getPoint(2, endPt);

		MiCoord midPointX = (endPt.x + startPt.x)/2;
		MiCoord midPointY = (endPt.y + startPt.y)/2;
		// Calc distance from midpoint of chord to control ctlPt on circle
		orthoDistance = Math.sqrt((ctlPt.x - midPointX) * (ctlPt.x - midPointX) 
			+ (ctlPt.y - midPointY) * (ctlPt.y - midPointY));
		
		// Calc distance from midpoint of chord to center of circle
		MiDistance halfCoordDistSquared = 
				((endPt.x - startPt.x) * (endPt.x - startPt.x) +
				(endPt.y - startPt.y) * (endPt.y - startPt.y))/4;
		if (orthoDistance == 0)
			{
			radius = Math.sqrt(halfCoordDistSquared);
			}
		else
			{
			radius = halfCoordDistSquared/(2 * orthoDistance) + orthoDistance/2;
			}

		//double midPointAngle = Utility.getAngle(midPointY - ctlPt.y, midPointX - ctlPt.x);
		//center.x = ctlPt.x + radius * Math.cos(midPointAngle);
		//center.y = ctlPt.y + radius * Math.sin(midPointAngle);

		double midPointAngle = Utility.getAngle(ctlPt.y - midPointY, ctlPt.x - midPointX);
		center.x = ctlPt.x - radius * Math.cos(midPointAngle);
		center.y = ctlPt.y - radius * Math.sin(midPointAngle);

		double endAngle = Utility.getAngle(endPt.y - center.y, endPt.x - center.x) * 180/Math.PI;
		startAngle = Utility.getAngle(startPt.y - center.y, startPt.x - center.x) * 180/Math.PI;
		sweptAngle = endAngle - startAngle;

//MiDebug.println(this + ",center=" + center + ", radius=" + radius + ",startAngle=" + startAngle + ", midPointAngle=" + midPointAngle + ",sweptAngle=" + sweptAngle);

		double startAngle2 = startAngle;
		while (startAngle2 < 0)
			startAngle2 += 360;
		while (startAngle2 >= 360)
			startAngle2 -= 360;

		midPointAngle *= 180/Math.PI;
		while (midPointAngle < 0)
			midPointAngle += 360;
		while (Utility.isGreaterThanOrEqual(midPointAngle, 360))
			midPointAngle -= 360;

		if (sweptAngle > 0)
			{
			if ((midPointAngle < startAngle2) || (midPointAngle > startAngle2 + sweptAngle))
				{
				while (sweptAngle > 0)
					sweptAngle -= 360;
				}
			}
		else
			{
			if ((midPointAngle > startAngle2) || (midPointAngle < startAngle2 + sweptAngle))
				{
				while (sweptAngle < 0)
					sweptAngle += 360;
				}
			}
//MiDebug.println(this + ",NOWWWW center=" + center + ", radius=" + radius + ",startAngle=" + startAngle + ", midPointAngle=" + midPointAngle + ", sweptAngle=" + sweptAngle);

/**** sweptAngle correction above takes care of this particular case of a swept angle very near 360 degrees
		if (orthoDistance < radius)
			{
			if (sweptAngle > 180)
				sweptAngle -= 360;
			else if (sweptAngle < -180)
				sweptAngle += 360;
			}
		else
			{
			if ((sweptAngle < 180) && (sweptAngle >= 0))
				sweptAngle -= 360;
			else if ((sweptAngle > -180) && (sweptAngle <= 0))
				sweptAngle += 360;
			}
*****/
		}
					/**------------------------------------------------------
	 				 * Copies the given MiPart. This MiPart will have the same
					 * attributes, bounds, resources, attachments, layouts,
					 * action handlers, and event handlers as the given MiPart. 
					 * @param source	the part to copy
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		Mi3PointArc obj = (Mi3PointArc )source;

		radius			= obj.radius;
		orthoDistance		= obj.orthoDistance;	
		center.x 		= obj.center.x;
		center.y 		= obj.center.y;
		startAngle 		= obj.startAngle;
		sweptAngle		= obj.sweptAngle;
		}
					/**------------------------------------------------------
					 * Returns information about this MiPart.
					 * @return		textual information (class name +
					 *			unique numerical id + name)
					 *------------------------------------------------------*/
	public		String		toString()
		{
		String tmp = super.toString();
		tmp += ("[orthoDistance=" + Utility.toShortString(orthoDistance) + "]");
		return(tmp);
		}
	}

