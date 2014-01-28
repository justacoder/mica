
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
import com.swfm.mica.util.Utility;


/**
 * This class represents a rectangular area and is heavily used by Mica, typically
 * whereever the dimensions of an area is needed.
 * <p>
 * The bounds of this class can be in an uninitilized state (reversed) in
 * which case xmin < xmax and ymin < ymax. This can be tested for by calling
 * the method #isReversed.
 * <p>
 * For very time or memory sensitive applications, there is a explicitly-accessed
 * allocation pool to prevent the inevitable overhead of allocating and deallocating
 * many instances of MiBounds (see #newBounds).
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiBounds implements MiiTypes
	{
	protected	MiCoord		xmin;
	protected	MiCoord		ymin;
	protected	MiCoord		xmax;
	protected	MiCoord		ymax;
	private static	FastVector	freePool	= new FastVector(512);
	private static	int		maxReportedSize;
		

	public				MiBounds()
		{ 
		//if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_BASIC_ALLOCATIONS))
			//MiDebug.printStackTrace("MiDebug.TRACE_BASIC_ALLOCATIONS");

		xmin = Mi_MAX_COORD_VALUE;
		ymin = Mi_MAX_COORD_VALUE;
		xmax = Mi_MIN_COORD_VALUE;
		ymax = Mi_MIN_COORD_VALUE;
		}
	public				MiBounds(MiBounds bounds) 	
		{ 
		//if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_BASIC_ALLOCATIONS))
			//MiDebug.printStackTrace("MiDebug.TRACE_BASIC_ALLOCATIONS");

		xmin = bounds.xmin;
		ymin = bounds.ymin;
		xmax = bounds.xmax;
		ymax = bounds.ymax;
		}
	public				MiBounds(MiPoint point) 	
		{
		//if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_BASIC_ALLOCATIONS))
			//MiDebug.printStackTrace("MiDebug.TRACE_BASIC_ALLOCATIONS");

		setBounds(point.x, point.y, point.x, point.y); 
		}
	public				MiBounds(MiSize size) 	
		{
		//if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_BASIC_ALLOCATIONS))
			//MiDebug.printStackTrace("MiDebug.TRACE_BASIC_ALLOCATIONS");

		xmin = 0;
		ymin = 0;
		xmax = size.width;
		ymax = size.height;
		}
	public				MiBounds(MiCoord x1, MiCoord y1, MiCoord x2, MiCoord y2) 
		{
		//if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_BASIC_ALLOCATIONS))
			//MiDebug.printStackTrace("MiDebug.TRACE_BASIC_ALLOCATIONS");

		xmin = x1;
		ymin = y1;
		xmax = x2;
		ymax = y2;
		}

	public		void		zeroOut()
		{
		xmin = 0;
		ymin = 0;
		xmax = 0;
		ymax = 0;
		}

	public		MiCoord		getXmin()			{ return xmin; 		}
	public		MiCoord		getYmin()			{ return ymin; 		}
	public		MiCoord		getXmax()			{ return xmax; 		}
	public		MiCoord		getYmax()			{ return ymax; 		}

	public		void		setXmin(MiCoord xmin)		{ this.xmin = xmin; 	}
	public		void		setYmin(MiCoord ymin)		{ this.ymin = ymin; 	}
	public		void		setXmax(MiCoord xmax)		{ this.xmax = xmax; 	}
	public		void		setYmax(MiCoord ymax)		{ this.ymax = ymax; 	}

	public		void		translateXminTo(MiCoord xmin)	{ translate(xmin - this.xmin, 0);}
	public		void		translateYminTo(MiCoord ymin)	{ translate(0, ymin - this.ymin);}
	public		void		translateXmaxTo(MiCoord xmax)	{ translate(xmax - this.xmax, 0);}
	public		void		translateYmaxTo(MiCoord ymax)	{ translate(0, ymax - this.ymax);}

	public		void		translateXmin(MiDistance tx)	{ xmin += tx; }
	public		void		translateYmin(MiDistance ty)	{ ymin += ty; }
	public		void		translateXmax(MiDistance tx)	{ xmax += tx; }
	public		void		translateYmax(MiDistance ty)	{ ymax += ty; }

	public final	MiDistance	getWidth()			
		{ 
		return((xmax - xmin) > 0 ? xmax - xmin : 0);
		}
	public final	MiDistance	getHeight()			
		{ 
		return((ymax - ymin) > 0 ? ymax - ymin : 0);
		}
	public		void		setWidth(MiDistance w)		
		{ 
		if (isReversedWidth())
			{
			xmin = 0; 
			xmax = w;
			}
		else
			{
			MiCoord cx = (xmin + xmax)/2;
			xmin = cx - w/2;
			xmax = cx + w/2;
			}
	 	}
	public		void		addWidth(MiDistance w)
		{
		if (isReversedWidth())
			{
			xmin = 0; 
			xmax = w;
			}
		else
			{
			xmin -= w/2;
			xmax += w/2;
			}
		}
	public		void		subtractWidth(MiDistance w)
		{
		if (!isReversedWidth())
			{
			xmin += w/2;
			xmax -= w/2;
			}
		}
	public		void		setHeight(MiDistance h)
		{
		if (isReversedHeight())
			{
			ymin = 0; 
			ymax = h;
			}
		else
			{
			MiCoord cy = (ymin + ymax)/2;
			ymin = cy - h/2;
			ymax = cy + h/2;
			}
		}
	public		void		addHeight(MiDistance h)
		{
		if (isReversedHeight())
			{
			ymin = 0; 
			ymax = h;
			}
		else
			{
			ymin -= h/2;
			ymax += h/2;
			}
		}
	public		void		subtractHeight(MiDistance h)
		{
		if (!isReversedHeight())
			{
			ymin += h/2;
			ymax -= h/2;
			}
		}
	public		MiCoord		getCenterX()			{ return((xmin + xmax)/2); }
	public		MiCoord		getCenterY()			{ return((ymin + ymax)/2); }
	public		MiPoint		getCenter()			
		{ 
		return(new MiPoint((xmin + xmax)/2, (ymin + ymax)/2)); 
		}
	public		MiPoint		getCenter(MiPoint pt)
		{ 
		pt.x = (xmin + xmax)/2;
		pt.y = (ymin + ymax)/2;
		return(pt);
		}
	public		void		setCenter(MiCoord x, MiCoord y)
		{ 
		if (isReversedWidth())
			{
			xmin = x;
			xmax = x;
			setCenterY(y);
			}
		else if (isReversedHeight())
			{
			ymin = y;
			ymax = y;
			setCenterX(x);
			}
		else
			{
			translate(x - (xmin + xmax)/2, y - (ymin + ymax)/2);
			}
		}
	public		void		setCenter(MiPoint pt)
		{
		setCenter(pt.x, pt.y);
		}
	public		void		setCenterX(MiCoord x)
		{ 
		if (isReversedWidth())
			{
			xmin = x;
			xmax = x;
			}
		else
			{
			translate(x - (xmin + xmax)/2, 0);
			}
		}
	public		void		setCenterY(MiCoord y)
		{ 
		if (isReversedHeight())
			{
			ymin = y;
			ymax = y;
			}
		else
			{
			translate(0, y - (ymin + ymax)/2);
			}
		}
	public		MiPoint		getLLCorner()			{ return(new MiPoint(xmin, ymin));}
	public		MiPoint		getURCorner()			{ return(new MiPoint(xmax, ymax));}
	public		MiPoint		getLRCorner()			{ return(new MiPoint(xmax, ymin));}
	public		MiPoint		getULCorner()			{ return(new MiPoint(xmin, ymax));}
	public		MiPoint		getLLCorner(MiPoint ll)
		{
		ll.x = xmin;
		ll.y = ymin;
		return(ll);
		}
	public		MiPoint		getURCorner(MiPoint ur)
		{
		ur.x = xmax;
		ur.y = ymax;
		return(ur);
		}
	public		void		setLLCorner(MiPoint pt)		{ xmin = pt.x; ymin = pt.y; }
	public		void		setURCorner(MiPoint pt)		{ xmax = pt.x; ymax = pt.y; }

	public		void		translateLRCornerTo(MiPoint point)
		{
		translate(point.x - xmax, point.y - ymin);
		}
	public		void		translateLLCornerTo(MiPoint point)
		{
		translate(point.x - xmin, point.y - ymin);
		}
	public		void		translateURCornerTo(MiPoint point)
		{
		translate(point.x - xmax, point.y - ymax);
		}
	public		void		translateULCornerTo(MiPoint point)
		{
		translate(point.x - xmin, point.y - ymax);
		}
	public		void		translateCenterTo(MiPoint point)
		{
		translate(point.x - (xmin + xmax)/2, point.y - (ymin + ymax)/2);
		}

	public		MiBounds	translate(MiDistance tx, MiDistance ty)
		{
		if (isReversedWidth() || isReversedHeight())
			return(this);

		if ((tx > 0) && (xmax > Mi_MAX_COORD_VALUE - tx)
			|| (tx < 0) && (xmin < Mi_MIN_COORD_VALUE - tx)
			|| (ty > 0) && (ymax > Mi_MAX_COORD_VALUE - ty)
			|| (ty < 0) && (ymin < Mi_MIN_COORD_VALUE - ty))
			{
			return(this);
			}
		xmin += tx;
		ymin += ty;
		xmax += tx;
		ymax += ty;
		return(this);
		}
	public		MiBounds	translate(MiVector v)
		{
		return(translate(v.x, v.y));
		}

	public		boolean		coversMoreAreaThan(MiBounds other)
		{
		return(((double )getWidth() * getHeight()) > ((double )other.getWidth() * other.getHeight()));
		}

	public		void		setBounds(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
		}

	public		void		setBounds(MiPoint pt)
		{
		xmin = pt.x;
		ymin = pt.y;
		xmax = pt.x;
		ymax = pt.y;
		}
	public		void		setBounds(MiPoint ll, MiPoint ur)
		{
		if (ll.x < ur.x)
			{
			xmin = ll.x;
			xmax = ur.x;
			}
		else
			{
			xmin = ur.x;
			xmax = ll.x;
			}
		if (ll.y < ur.y)
			{
			ymin = ll.y;
			ymax = ur.y;
			}
		else
			{
			ymin = ur.y;
			ymax = ll.y;
			}
		}
		
	public		void		setBounds(MiBounds ext)
		{
		xmin = ext.xmin;
		ymin = ext.ymin;
		xmax = ext.xmax;
		ymax = ext.ymax;
		}
	public		void		setSize(MiDistance width, MiDistance height)
		{
		setWidth(width);
		setHeight(height);
		}
	public		void		setSize(MiBounds size)
		{
		setWidth(size.getWidth());
		setHeight(size.getHeight());
		}
	public		void		setSize(MiSize size)
		{
		MiCoord cx = (xmin + xmax)/2;
		xmin = cx - size.width/2;
		xmax = cx + size.width/2;
		MiCoord cy = (ymin + ymax)/2;
		ymin = cy - size.height/2;
		ymax = cy + size.height/2;
		}
	public		MiSize		getSize()
		{
		return(new MiSize(xmax - xmin, ymax - ymin));
		}

	public		MiBounds	copy(MiBounds ext)
		{
		xmin = ext.xmin;
		ymin = ext.ymin;
		xmax = ext.xmax;
		ymax = ext.ymax;
		return(this);
		}
		
	public		void		getBounds(MiBounds bounds)
		{
		bounds.xmin = xmin;
		bounds.ymin = ymin;
		bounds.xmax = xmax;
		bounds.ymax = ymax;
		}

	public		MiBounds	addMargins(MiMargins margin)
		{
		if (!isReversed())
			{
			xmin -= margin.left;
			ymin -= margin.bottom;
			xmax += margin.right;
			ymax += margin.top;
			}
		return(this);
		}

	public		MiBounds	addMargins(MiDistance left, MiDistance bottom, MiDistance right, MiDistance top)
		{
		if (!isReversed())
			{
			xmin -= left;
			ymin -= bottom;
			xmax += right;
			ymax += top;
			}
		return(this);
		}

	public		MiBounds	subtractMargins(MiMargins margin)
		{
		if (!isReversed())
			{
			xmin += margin.left;
			ymin += margin.bottom;
			xmax -= margin.right;
			ymax -= margin.top;
			}
		return(this);
		}

	public		MiBounds	addMargins(MiDistance margin)
		{
		if (!isReversed())
			{
			xmin -= margin;
			ymin -= margin;
			xmax += margin;
			ymax += margin;
			}
		return(this);
		}
	public		MiBounds	subtractMargins(MiDistance margin)
		{
		if (!isReversed())
			{
			xmin += margin;
			ymin += margin;
			xmax -= margin;
			ymax -= margin;
			}
		return(this);
		}


	// Leaves bounds unchanged
	// This says that two bounds with common edge are NOT overlapping
	// Will not work for single point bounds
	public		boolean		intersects(MiBounds bounds)
		{
		return(!((xmin >= bounds.xmax) || (ymin >= bounds.ymax)
			|| (xmax <= bounds.xmin) || (ymax <= bounds.ymin)));
		}

	public		boolean		intersects(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		return(!((this.xmin > xmax) || (this.ymin > ymax)
			|| (this.xmax < xmin) || (this.ymax < ymin)));
		}

	// Leaves bounds unchanged
	public		boolean		intersectsIncludingEdges(MiBounds bounds)
		{
		if ((xmin > bounds.getXmax()) || (ymin > bounds.getYmax())
			|| (xmax < bounds.getXmin()) || (ymax < bounds.getYmin()))
			{
			return(false);
			}
		return(true);
		}

	public		boolean		intersectsIncludingFuzzyEdges(MiBounds bounds)
		{
		if ((Utility.isGreaterThan(xmin, bounds.getXmax()))
			|| (Utility.isGreaterThan(ymin, bounds.getYmax()))
			|| (Utility.isGreaterThan(bounds.getXmin(), xmax))
			|| (Utility.isGreaterThan(bounds.getYmin(), ymax)))
			{
			return(false);
			}
		return(true);
		}

	public		boolean 	intersects(MiCoord x, MiCoord y)
		{
		if ((x > xmax) || (y > ymax) || (x < xmin) || (y < ymin))
			{
			return(false);
			}
		return(true);
		}

	public		boolean 	intersects(MiPoint point)
		{
		if ((point.x > xmax) || (point.y > ymax) || (point.x < xmin) || (point.y < ymin))
			{
			return(false);
			}
		return(true);
		}

	public		boolean 	intersectsLine(MiCoord x1, MiCoord y1, MiCoord x2, MiCoord y2)
		{
		if (x1 == x2)
			{
			if ((x1 < xmin) || (x1 > xmax))
				return(false);

			if (y1 < y2)
				return((y1 < ymax) && (y2 > ymin));

			return((y2 < ymax) && (y1 > ymin));
			}

		if (x1 > x2)
			{
			// Make x1 < x2.
			MiCoord tmp = x1;
			x1 = x2;
			x2 = tmp;
			tmp = y1;
			y1 = y2;
			y2 = tmp;
			}

		if (y1 == y2)
			{
			if ((y1 < ymin) || (y1 > ymax))
				return(false);

			if ((x1 > xmax) || (x2 < xmin))
				return(false);

			return(true);
			}

		// Reject if pick area does not intersect the extrema of the line.
		if ( (x2 < xmin) || (x1 > xmax)
			|| ((y1 > ymax) && (y2 > ymax))
			|| ((y1 < ymin) && (y2 < ymin)) )
			{
			return(false);
			}
	

		double slope = ((double )y2 - y1)/((double )x2 - x1);
		double b = y2 - x2 * slope;
		MiCoord y;
	
		y = slope * xmax + b + 0.5;
		if (y <= ymax)
			{
			if (y >= ymin)
				return(true);
			// was below ymin, if other point is above then intersected area.
			y = slope * xmin + b + 0.5;
			if (y >= ymin)
				return(true);
			return(false);
			}
		// Above intersection with xmax was above ymax, 
		// if below then it will have crossed thru area.
		y = slope * xmin + b + 0.5;
	
		if (y <= ymax)
			return(true);
	
		return(false);
		}
	public		boolean 	intersectsLine(MiCoord x1, MiCoord y1, MiCoord x2, MiCoord y2, MiDistance lWidth)
		{
		if (lWidth == 0)
			return(intersectsLine(x1, y1, x2, y2));

		MiDistance halfLWidth = lWidth/2;
		if (x1 == x2)
			{
			if (y1 < y2)
				return(intersects(x1 - halfLWidth, y1, x1 + halfLWidth, y2));
			return(intersects(x1 - halfLWidth, y2, x1 + halfLWidth, y1));
			}
		if (y1 == y2)
			{
			if (x1 < x2)
				return(intersects(x1, y1 - halfLWidth, x2, y1 + halfLWidth));
			return(intersects(x2, y1 - halfLWidth, x1, y1 + halfLWidth));
			}

		MiCoord[] xPts = new MiCoord[4];
		MiCoord[] yPts = new MiCoord[4];

		lWidth += xmax - xmin;

		double dx = x2 - x1;
		double dy = y2 - y1;
		double len = Math.sqrt(dx*dx + dy*dy);
		double tmp = lWidth/(2 * len);
		double ddy = -dx*tmp;
		double ddx = dy*tmp;

		xPts[0] = x1 + ddx;
		yPts[0] = y1 + ddy;
		xPts[1] = x1 - ddx;
		yPts[1] = y1 - ddy;
		xPts[2] = x2 - ddx;
		yPts[2] = y2 - ddy;
		xPts[3] = x2 + ddx;
		yPts[3] = y2 + ddy;
			
		MiCoord oldPtX = xPts[3];
		MiCoord oldPtY = yPts[3];
		MiCoord x = getCenterX();
		MiCoord y = getCenterY();
		boolean inside = false;

		for (int i = 0; i < 4; ++i)
			{
			MiCoord newPtX = xPts[i];
			MiCoord newPtY = yPts[i];
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

	public		boolean		intersectionWith(MiBounds bounds)
		{
		if (!intersects(bounds))
			{
			reverse();
			return(false);
			}

		if (bounds.xmin > xmin)
			xmin = bounds.xmin;
		if (bounds.ymin > ymin)
			ymin = bounds.ymin;
		if (bounds.xmax < xmax)
			xmax = bounds.xmax;
		if (bounds.ymax < ymax)
			ymax = bounds.ymax;

		return(true);
		}
	public		boolean		getIntersectionWithLine(
						MiPoint insidePoint, 
						MiPoint otherPoint, 
						MiPoint returnedIntersectionPoint)
		{
		if (otherPoint.x == insidePoint.x)
			{
			returnedIntersectionPoint.x = insidePoint.x;
			if (otherPoint.y > ymax)
				returnedIntersectionPoint.y = ymax;
			else
				returnedIntersectionPoint.y = ymin;
			return(true);
			}

		if (otherPoint.y == insidePoint.y)
			{
			returnedIntersectionPoint.y = insidePoint.y;
			if (otherPoint.x > xmax)
				returnedIntersectionPoint.x = xmax;
			else
				returnedIntersectionPoint.x = xmin;
			return(true);
			}

		if ((xmin == xmax) || (ymin == ymax))
			{
			returnedIntersectionPoint.x = xmin;
			returnedIntersectionPoint.y = ymin;
			return(true);
			}

		double slope = (otherPoint.y - insidePoint.y)/(otherPoint.x - insidePoint.x);
		double b = otherPoint.y - otherPoint.x * slope;
		double x, y;

		if (otherPoint.x < xmin)
			{
			// Try left side...
			y = slope * xmin + b;
			if ((y >= ymin) && (y <= ymax))
				{
				returnedIntersectionPoint.x = xmin;
				returnedIntersectionPoint.y = y;
				return(true);
				}
			}
		if (otherPoint.x > xmax)
			{
			// Try right side...
			y = slope * xmax + b;
			if ((y >= ymin) && (y <= ymax))
				{
				returnedIntersectionPoint.x = xmax;
				returnedIntersectionPoint.y = y;
				return(true);
				}
			}
		if (otherPoint.y < ymin)
			{
			// Try bottom side...
			x = (ymin - b)/slope;
			if ((x >= xmin) && (x <= xmax))
				{
				returnedIntersectionPoint.x = x;
				returnedIntersectionPoint.y = ymin;
				return(true);
				}
			}

		if (otherPoint.y > ymax)
			{
			// Try top side...
			x = (ymax - b)/slope;
			if ((x >= xmin) && (x <= xmax))
				{
				returnedIntersectionPoint.x = x;
				returnedIntersectionPoint.y = ymax;
				return(true);
				}
			}

		return(false);
		}
	public		MiBounds	union(MiBounds bounds)
		{
		if (bounds.xmin < xmin)
			xmin = bounds.xmin;
		if (bounds.ymin < ymin)
			ymin = bounds.ymin;
		if (bounds.xmax > xmax)
			xmax = bounds.xmax;
		if (bounds.ymax > ymax)
			ymax = bounds.ymax;
		return(this);
		}
	public		MiBounds	union(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		if (xmin < this.xmin)
			this.xmin = xmin;
		if (ymin < this.ymin)
			this.ymin = ymin;
		if (xmax > this.xmax)
			this.xmax = xmax;
		if (ymax > this.ymax)
			this.ymax = ymax;
		return(this);
		}
	public		MiBounds	union(MiCoord x, MiCoord y)
		{
		if (x < xmin)
			xmin = x;
		if (x > xmax)
			xmax = x;
		if (y < ymin)
			ymin = y;
		if (y > ymax)
			ymax = y;
		return(this);
		}
	public		MiBounds	union(MiPoint point)
		{
		if (point.x < xmin)
			xmin = point.x;
		if (point.x > xmax)
			xmax = point.x;
		if (point.y < ymin)
			ymin = point.y;
		if (point.y > ymax)
			ymax = point.y;
		return(this);
		}
	public		void		accumulateMaxWidthAndHeight(MiBounds bounds)
		{
		if (bounds.getWidth() > getWidth())
			setWidth(bounds.getWidth());
		if (bounds.getHeight() > getHeight())
			setHeight(bounds.getHeight());
		}
	public		void		accumulateMaxWidthAndHeight(MiSize size)
		{
		if (size.getWidth() > getWidth())
			setWidth(size.getWidth());
		if (size.getHeight() > getHeight())
			setHeight(size.getHeight());
		}
	public		MiBounds	expandIntoASquare()
		{
		if (xmax - xmin > ymax - ymin)
			setHeight(xmax - xmin);
		else
			setWidth(ymax - ymin);
		return(this);
		}
	public		MiBounds	shrinkIntoASquare()
		{
		if (xmax - xmin > ymax - ymin)
			setWidth(ymax - ymin);
		else
			setHeight(xmax - xmin);
		return(this);
		}
	public 		String 		toSizeString()
		{
		if (isReversed())
			return(getClass().getName() + "[REVERSED]");
		return(getClass().getName() + "[" + getWidth() + "x" + getHeight() + "]");
		}

	public 		String 		toString()
		{
		if (isReversed())
			return(MiDebug.getMicaClassName(this) + "[REVERSED]");
		return(MiDebug.getMicaClassName(this) 
			+ "[xmin=" + Utility.toShortString(xmin) 
			+ ",ymin=" + Utility.toShortString(ymin)
			+ ",xmax=" + Utility.toShortString(xmax)
			+ ",ymax=" + Utility.toShortString(ymax)
			+ "][" 
			+ Utility.toShortString(getWidth())
			+ "x"
			+ Utility.toShortString(getHeight())
			+ "](" 
			+ Utility.toShortString((xmin + xmax)/2)
			+ ","
			+ Utility.toShortString((ymin + ymax)/2)
			+ ")");
		}

	public		void		reverse()
		{
		xmin = Mi_MAX_COORD_VALUE;
		ymin = Mi_MAX_COORD_VALUE;
		xmax = Mi_MIN_COORD_VALUE;
		ymax = Mi_MIN_COORD_VALUE;
		}

	public		boolean		isReversed()
		{
		return((xmax - xmin < 0) || (ymax - ymin < 0));
		//return((Utility.isGreaterThan(xmin, xmax) || Utility.isGreaterThan(ymin, ymax)) ? true : false);
		}

	public		boolean		isReversedWidth()
		{
		return(xmax - xmin < 0);
		//return(Utility.isGreaterThan(xmin, xmax) ? true : false);
		}
	public		boolean		isReversedHeight()
		{
		return(ymax - ymin < 0);
		//return(Utility.isGreaterThan(ymin, ymax) ? true : false);
		}

	public		void		scale(MiScale scale)
		{
		scale(scale.x, scale.y);
		}
	public		void		scale(double scalex, double scaley)
		{
		MiDistance newwidth = getWidth() * scalex;
		MiDistance newheight = getHeight() * scaley;
		MiCoord cx = getCenterX();
		MiCoord cy = getCenterY();
	
		xmin = cx - newwidth/2;
		xmax = cx + newwidth - newwidth/2;
		ymin = cy - newheight/2;
		ymax = cy + newheight - newheight/2;
		}

	public final 	boolean		equals(MiBounds other)
		{
		if (Utility.areZero(
			xmin - other.xmin,
			ymin - other.ymin,
			xmax - other.xmax,
			ymax - other.ymax))
			{
			return(true);
			}
		return(false);
		}
	public		boolean		equals(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		if (Utility.areZero(
			xmin - this.xmin,
			ymin - this.ymin,
			xmax - this.xmax,
			ymax - this.ymax))
			{
			return(true);
			}
		return(false);
		}

	public		boolean		equalsSize(MiBounds other)
		{
		if (Utility.areZero(getWidth() - other.getWidth(), getHeight() - other.getHeight()))
			{
			return(true);
			}
		return(false);
		}

	public		boolean		equalsIntegerSize(MiBounds other)
		{
		if (((int )(getWidth() - other.getWidth()) == 0)
			&& ((int )(getHeight() - other.getHeight()) == 0))
			{
			return(true);
			}
		return(false);
		}

	public		boolean		isSmallerSizeThan(MiSize other)
		{
		if ((getWidth() < other.getWidth()) || (getHeight() < other.getHeight()))
			{
			return(true);
			}
		return(false);
		}

	public		boolean		isSmallerSizeThan(MiBounds other)
		{
		if ((getWidth() < other.getWidth()) || (getHeight() < other.getHeight()))
			{
			return(true);
			}
		return(false);
		}

	public		boolean		isLargerSizeThan(MiBounds other)
		{
		if ((getWidth() > other.getWidth()) || (getHeight() > other.getHeight()))
			{
			return(true);
			}
		return(false);
		}
	public		boolean		isLargerSizeThan(MiSize other)
		{
		if ((getWidth() > other.getWidth()) || (getHeight() > other.getHeight()))
			{
			return(true);
			}
		return(false);
		}

	public		boolean		contains(MiBounds part)
		{
		if ((part.xmin >= xmin)
			&& (part.ymin >= ymin)
			&& (part.xmax <= xmax)
			&& (part.ymax <= ymax))
			{
			return(true);
			}
		if (equals(part))
			return(true);
		return(false);
		}
	public		boolean		pointIsInside(MiPoint pt, MiDistance amountInside)
		{
		if ((pt.x > xmin + amountInside) && (pt.y > ymin + amountInside)
			&& (pt.x < xmax - amountInside) && (pt.y < ymax - amountInside))
			{
			return(true);
			}
		return(false);
		}
	public		boolean		isContainedIn(MiBounds container)
		{
		if ((xmin >= container.getXmin())
			&& (ymin >= container.getYmin())
			&& (xmax <= container.getXmax())
			&& (ymax <= container.getYmax()))
			{
			return(true);
			}
		if (equals(container))
			return(true);
		return(false);
		}
	public		void		positionInsideContainer(MiBounds container)
		{
		if (!isContainedIn(container))
			{
			MiDistance tx = 0;
			MiDistance ty = 0;
			if (xmin < container.xmin)
				tx = container.xmin - xmin;
			else if (xmax > container.xmax)
				tx = container.xmax - xmax;
			if (ymin < container.ymin)
				ty = container.ymin - ymin;
			else if (ymax > container.ymax)
				ty = container.ymax - ymax;
			translate(tx, ty);
			}
		}

	public		void		confineInsideContainer(MiBounds container)
		{
		if (!isContainedIn(container))
			{
			if (xmin < container.xmin)
				xmin = container.xmin;
			if (xmax > container.xmax)
				xmax = container.xmax;
			if (ymin < container.ymin)
				ymin = container.ymin;
			if (ymax > container.ymax)
				ymax = container.ymax;
			}
		}

	public		boolean		confineInsideContainer(MiBounds container, MiVector proposedDelta)
		{
		if (xmin + proposedDelta.x < container.xmin)
			proposedDelta.x = container.xmin - xmin;
		if (xmax + proposedDelta.x > container.xmax)
			proposedDelta.x = container.xmax - xmax;
		if (ymin + proposedDelta.y < container.ymin)
			proposedDelta.y = container.ymin - ymin;
		if (ymax + proposedDelta.y > container.ymax)
			proposedDelta.y = container.ymax - ymax;

		return(!proposedDelta.isZero());
		}

	public		void		assureMinsLessThanMaxs()
		{
		if (xmin > xmax)
			{
			MiCoord tmp = xmin;
			xmin = xmax;
			xmax = tmp;
			}
	
		if (ymin > ymax)
			{
			MiCoord tmp = ymin;
			ymin = ymax;
			ymax = tmp;
			}
		}
	public		boolean		validate()
		{
		if (isReversed())
			return(true);
		if (Utility.isGreaterThan(xmin, xmax) || Utility.isGreaterThan(ymin, ymax))
			return(false);
		return(true);
		}
			
	public		MiPoint		getLocationOfCommonPoint(int connPtNum, MiPoint point)
		{
		switch(connPtNum)
			{
			case Mi_CENTER_LOCATION :
				point.x = getCenterX();
				point.y = getCenterY();
				break;
			case Mi_LEFT_LOCATION :
				point.x = xmin;
				point.y = getCenterY();
				break;
			case Mi_RIGHT_LOCATION :
				point.x = xmax;
				point.y = getCenterY();
				break;
			case Mi_BOTTOM_LOCATION :
				point.x = getCenterX();
				point.y = ymin;
				break;
			case Mi_TOP_LOCATION :
				point.x = getCenterX();
				point.y = ymax;
				break;
			case Mi_UPPER_LEFT_LOCATION :
				point.x = xmin;
				point.y = ymax;
				break;
			case Mi_UPPER_RIGHT_LOCATION :
				point.x = xmax;
				point.y = ymax;
				break;
			case Mi_LOWER_LEFT_LOCATION :
				point.x = xmin;
				point.y = ymin;
				break;
			case Mi_LOWER_RIGHT_LOCATION :
				point.x = xmax;
				point.y = ymin;
				break;
			default:
				// This should have been overridden, default to something reasonable.
				point.x = getCenterX();
				point.y = getCenterY();
				break;
			}
		return(point);
		}
	public		int		getClosestCommonPoint(MiPoint location, MiPoint connPtLocation)
		{
		double 		mindist = 65535.0 * 65535.0;
		double		dist;
		MiPoint		point = new MiPoint();
		int 		connPtNum = 0;

		for (int i = Mi_MIN_COMMON_LOCATION; i <= Mi_MAX_COMMON_LOCATION; ++i)
			{
			getLocationOfCommonPoint(i, point);

			dist = point.getDistanceSquared(location);
			if (dist < mindist)
				{
				connPtLocation.x = point.x;
				connPtLocation.y = point.y;
				connPtNum = i;
				mindist = dist;
				}
			}
		return(connPtNum);
		}
	public		void		setLocationOfCommonPoint(int connPtNum, MiPoint point)
		{
		switch(connPtNum)
			{
			case Mi_CENTER_LOCATION :
				setCenter(point);
				break;
			case Mi_LEFT_LOCATION :
				xmin = point.x;
				break;
			case Mi_RIGHT_LOCATION :
				xmax = point.x;
				break;
			case Mi_BOTTOM_LOCATION :
				ymin = point.y;
				break;
			case Mi_TOP_LOCATION :
				ymax = point.y;
				break;
			case Mi_UPPER_LEFT_LOCATION :
				xmin = point.x;
				ymax = point.y;
				break;
			case Mi_UPPER_RIGHT_LOCATION :
				xmax = point.x;
				ymax = point.y;
				break;
			case Mi_LOWER_LEFT_LOCATION :
				xmin = point.x;
				ymin = point.y;
				break;
			case Mi_LOWER_RIGHT_LOCATION :
				xmax = point.x;
				ymin = point.y;
				break;
			default:
				// This should have been overridden, default to something reasonable.
				setCenter(point);
				break;
			}
		}
	public		void		translateLocationOfCommonPointTo(int connPtNum, MiPoint point)
		{
		switch(connPtNum)
			{
			case Mi_CENTER_LOCATION :
				translateCenterTo(point);
				break;
			case Mi_LEFT_LOCATION :
				translateXminTo(point.x);
				break;
			case Mi_RIGHT_LOCATION :
				translateXmaxTo(point.x);
				break;
			case Mi_BOTTOM_LOCATION :
				translateYminTo(point.x);
				break;
			case Mi_TOP_LOCATION :
				translateYmaxTo(point.x);
				break;
			case Mi_UPPER_LEFT_LOCATION :
				translateULCornerTo(point);
				break;
			case Mi_UPPER_RIGHT_LOCATION :
				translateURCornerTo(point);
				break;
			case Mi_LOWER_LEFT_LOCATION :
				translateLLCornerTo(point);
				break;
			case Mi_LOWER_RIGHT_LOCATION :
				translateLRCornerTo(point);
				break;
			default:
				// This should have been overridden, default to something reasonable.
				translateCenterTo(point);
				break;
			}
		}
	public		double		getDistanceSquaredToClosestEdge(MiPoint pt)
		{
		// Determine which of 9 sectors pt is in
		if (pt.x > xmax)
			{
			if (pt.y > ymax)
				return((pt.x - xmax) * (pt.x - xmax) + (pt.y - ymax) * (pt.y - ymax));
			if (pt.y < ymin)
				return((pt.x - xmax) * (pt.x - xmax) + (pt.y - ymin) * (pt.y - ymin));
			return((pt.x - xmax) * (pt.x - xmax));
			}
		if (pt.x < xmin)
			{
			if (pt.y > ymax)
				return((pt.x - xmin) * (pt.x - xmin) + (pt.y - ymax) * (pt.y - ymax));
			if (pt.y < ymin)
				return((pt.x - xmin) * (pt.x - xmin) + (pt.y - ymin) * (pt.y - ymin));
			return((pt.x - xmin) * (pt.x - xmin));
			}
		if (pt.y > ymax)
			return((pt.y - ymax) * (pt.y - ymax));
		if (pt.y < ymin)
			return((pt.y - ymin) * (pt.y - ymin));
		
		// Inside
		return(0);
		}
	protected static MiBounds	newBounds()
		{
		MiBounds b = null;
		synchronized (freePool)
			{
			if (freePool.size() > 0)
				{
				b = (MiBounds )freePool.lastElement();
				b.xmin = Mi_MAX_COORD_VALUE;
				b.ymin = Mi_MAX_COORD_VALUE;
				b.xmax = Mi_MIN_COORD_VALUE;
				b.ymax = Mi_MIN_COORD_VALUE;
				freePool.removeElementAt(freePool.size() - 1);
				}
			else
				{
				b = new MiBounds();
//System.out.println("alloc bounds");
				}
			}
		return(b);
		}

	protected static void		freeBounds(MiBounds b)
		{
		synchronized (freePool)
			{
			freePool.addElement(b);
/***
if (freePool.size() > maxReportedSize)
{
maxReportedSize = freePool.size();
System.out.println("Number of MiBounds: " + freePool.size());
}
****/
			}
		}

	}

