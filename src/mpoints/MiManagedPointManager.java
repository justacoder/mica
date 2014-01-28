
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
import com.swfm.mica.util.Strings; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public abstract class MiManagedPointManager extends MiPartRenderer implements MiiTypes, MiiNames
	{
	public static final String	Mi_MANAGED_POINT_RESOURCE_NAME		= "managedPoint";
	public static final String	Mi_MANAGED_POINT_MANAGER_RESOURCE_NAME	= "managedPointManager";
	public static final String	Mi_CROSS_TYPE_NAME			= "cross";
	public static final String	Mi_CUSTOM_TYPE_NAME			= "custom";
	private static final String	Mi_NAMED_POINT_STYLED_LOOK_RESOURCE_NAME= "ManagedPointLookName";

	private		MiiManagedPointRule	localRule;
	private		MiPart		localLook;
	private		boolean		locallyVisible		= true;
	private		boolean		locallyHidden;
	private		boolean		locallyAlwaysVisible;
	private		boolean		mutable;
	private		boolean		enabled			= true;
	private		MiManagedPoints	localPoints;

	private	static	MiMargins	tmpMargins		= new MiMargins();
	private	static	MiBounds	tmpBounds		= new MiBounds();
	private		MiBounds	tmpBounds1		= new MiBounds();
	private		MiBounds	tmpBounds2		= new MiBounds();
	private		MiPoint		tmpPoint		= new MiPoint();


	public				MiManagedPointManager()
		{
		}

	public abstract	MiManagedPointManager	getManager(MiPart part);
	public abstract	MiiManagedPointRule	getRule();
	public abstract	MiPart			getLook();
	public abstract	boolean			isVisible();
	public abstract	boolean			isHidden();
	public abstract	MiManagedPointManager 	copy();

	public static	MiManagedPointManager	getManager(MiPart part, MiManagedPointManager kind)
		{
		return(kind.getManager(part));
		}
	public static	void			setManager(MiPart part, MiManagedPointManager kind)
		{
		if (kind instanceof MiConnectionPointManager)
			part.setConnectionPointManager((MiConnectionPointManager )kind);
		else if (kind instanceof MiAnnotationPointManager)
			part.setAnnotationPointManager((MiAnnotationPointManager )kind);
		else if (kind instanceof MiControlPointManager)
			part.setControlPointManager((MiControlPointManager )kind);
		else if (kind instanceof MiSnapPointManager)
			part.setSnapPointManager((MiSnapPointManager )kind);
		}

	public		void		setLocalRule(MiiManagedPointRule rule)
		{
		localRule = rule;
		}
	public		MiiManagedPointRule	getLocalRule()
		{
		return(localRule);
		}
	public		void		setLocalLook(MiPart look)
		{
		localLook = look;
		if (localLook != null)
			localLook.validateLayout();
		}
	public		MiPart		getLocalLook()
		{
		return(localLook);
		}

	public		void		setEnabled(boolean flag)
		{
		enabled = flag;
		}
	public		boolean		isEnabled()
		{
		return(enabled);
		}

	public		MiManagedPoints	getLocalPoints()
		{
		return(localPoints);
		}
	public		void		setLocalPoints(MiManagedPoints p)
		{
		localPoints = p;
		}
	public		MiManagedPoints	getManagedPoints()
		{
		return(localPoints);
		}
	public		void		setManagedPoints(MiManagedPoints points)
		{
		localPoints = points;
		}

	public		void		setLocallyVisible(boolean flag)
		{
		locallyVisible = flag;
		// FIX: keep list of parts this is assigned to? so can notify them?
		}
	public		boolean		isLocallyVisible()
		{
		return(locallyVisible);
		}
	public		void		setLocallyAlwaysVisible(boolean flag)
		{
		locallyAlwaysVisible = flag;
		// FIX: keep list of parts this is assigned to? so can notify them?
		}
	public		boolean		isLocallyAlwaysVisible()
		{
		return(locallyAlwaysVisible);
		}
	public		void		setLocallyHidden(boolean flag)
		{
		locallyHidden = flag;
		// FIX: keep list of parts this is assigned to? so can notify them?
		}
	public		boolean		isLocallyHidden()
		{
		return(locallyHidden);
		}

	public		MiManagedPoint	getManagedPoint(int pointLocation)
		{
		MiManagedPoints points = getLocalPoints();
		if (points == null)
			return(null);
		return(points.getManagedPoint(pointLocation));
		}
	public		void		removeManagedPoint(int pointLocation)
		{
		MiManagedPoints points = getLocalPoints();
		if (points != null)
			points.removeManagedPoint(pointLocation);
		}
	public		void		appendManagedPoint(int pointLocation)
		{
		MiManagedPoints points = getLocalPoints();
		if (points == null)
			{
			points = new MiManagedPoints();
			setLocalPoints(points);
			}
		points.addElement(new MiManagedPoint(pointLocation));
		}
	public		void		appendManagedPoint(MiManagedPoint managedPoint)
		{
		MiManagedPoints points = getLocalPoints();
		if (points == null)
			{
			points = new MiManagedPoints();
			setLocalPoints(points);
			}
		points.addElement(managedPoint);
		}
	public		void		copy(MiManagedPointManager src)
		{
		if (src.localPoints != null)
			localPoints = new MiManagedPoints(src.localPoints);

		if (src.localRule != null)
			localRule 	= src.localRule.copy();
		if (src.localLook != null)
			localLook 	= src.localLook.deepCopy();

		locallyVisible		= src.locallyVisible;
		locallyHidden		= src.locallyHidden;
		locallyAlwaysVisible	= src.locallyAlwaysVisible;
		mutable			= src.mutable;
		enabled			= src.enabled;
		}
	public		void		setIsMutable(boolean flag)
		{
		mutable = flag;
		}
	public		boolean		isMutable()
		{
		return(mutable);
		}
	// -------------------------------------------------------
	//	General/Default appearance of connection points
	// ---------------------------------------------------------
	public static	Strings		getStyledLooks()
		{
		Strings styles = new Strings();
		styles.addElement(Mi_RECTANGLE_TYPE_NAME);
		styles.addElement(Mi_CROSS_TYPE_NAME);
		styles.addElement(Mi_CIRCLE_TYPE_NAME);
		styles.addElement(Mi_CUSTOM_TYPE_NAME);
		return(styles);
		}
	public static	MiPart		makeStyledLook(String style)
		{
		MiPart look = null;
		if (style.equalsIgnoreCase(Mi_RECTANGLE_TYPE_NAME))
			{
			look = new MiRectangle(0, 0, 6, 6);
			}
		else if (style.equalsIgnoreCase(Mi_CROSS_TYPE_NAME))
			{
			look = new MiContainer();
			MiLine line = new MiLine(0, 0, 6, 6);
			look.appendPart(line);
			line = new MiLine(0, 6, 6, 0);
			look.appendPart(line);
			}
		else if (style.equalsIgnoreCase(Mi_CIRCLE_TYPE_NAME))
			{
			look = new MiCircle(0, 0, 6);
			}
		else
			{
			throw new IllegalArgumentException("Mica.MiManagedPointManager: "
				+ "No look with style: " + style);
			}
		look.setResource(Mi_NAMED_POINT_STYLED_LOOK_RESOURCE_NAME, style);
		return(look);
		}
	public static	String		getStyledLookName(MiPart part)
		{
		String name = (String )part.getResource(Mi_NAMED_POINT_STYLED_LOOK_RESOURCE_NAME);
		return(name == null ? Mi_CUSTOM_TYPE_NAME : name);
		}
	public		MiPart		pickPart(MiPart target, MiBounds area)
		{
		if (isVisible())
			{
			MiManagedPoints points = localPoints;
			if (points == null)
				points = getManagedPoints();

			if ((points == null) || ((getLook() == null) && (!points.hasGraphicsToDraw())))
				{
				return(null);
				}

			MiPoint pt 		= tmpPoint;
			MiPart 	assocGraphics 	= getLook();
			MiPart 	graphics;
			for (int i = 0; i < points.size(); ++i)
				{
				MiManagedPoint managedPt = points.elementAt(i);
				if (!managedPt.isVisible(target)) // 1-1-2004
					{
					continue;
					}
				for (int j = 0; j < managedPt.getNumberOfPoints(target); ++j)
					{
					managedPt.getPoint(target, j, pt);
					if (managedPt.getLook() != null)
						graphics = managedPt.getLook();
					else
						graphics = assocGraphics;

					graphics.setCenter(pt);
					if (graphics.pick(area))
						return(graphics);
					}
				}
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the list of parts, including this MiPart, whose 
					 * bounds intersects the given area. The list is sorted 
					 * from topmost to bottommost part.
	 				 * @param area	 	the area
	 				 * @param list		the (returned) list of parts
					 *------------------------------------------------------*/
	public		void		pickDeepList(MiPart part, MiBounds area, MiParts list)
		{
		if (isVisible())
			{
			MiManagedPoints points = localPoints;
			if (points == null)
				points = getManagedPoints();

			if ((points == null) || ((getLook() == null) && (!points.hasGraphicsToDraw())))
				{
				return;
				}

			MiPoint pt 		= tmpPoint;
			MiBounds b 		= part.getBounds(tmpBounds1);
			MiPart 	assocGraphics 	= getLook();
			MiPart 	graphics;
			for (int i = 0; i < points.size(); ++i)
				{
				MiManagedPoint managedPt = points.elementAt(i);
				if (!managedPt.isVisible(part)) // 1-1-2004
					{
					continue;
					}
				for (int j = 0; j < managedPt.getNumberOfPoints(part); ++j)
					{
					managedPt.getPoint(part, j, pt);
					if (managedPt.getLook() != null)
						graphics = managedPt.getLook();
					else
						graphics = assocGraphics;

					graphics.setCenter(pt);
					if (graphics.pick(area))
						{
						graphics.setContainer(part);
						graphics.setOutgoingInvalidLayoutNotificationsEnabled(false);
						graphics.setInvalidAreaNotificationsEnabled(false);
						graphics.setResource(Mi_MANAGED_POINT_RESOURCE_NAME, managedPt);
						graphics.setResource(Mi_MANAGED_POINT_MANAGER_RESOURCE_NAME, this);
						list.addElement(graphics);
						}
					}
				}
			}
		}
	public		MiManagedPoint	pick(MiPart part, MiBounds area)
		{
		MiManagedPoints points = getManagedPoints();
		for (int i = 0; (points != null) && (i < points.size()); ++i)
			{
			MiManagedPoint managedPt = points.elementAt(i);
			if (managedPt.pick(this, part, area))
				return(managedPt);
			}
		return(null);
		}

	// -------------------------------------------------------
	//	This MiManagedPointManagers routines
	// ---------------------------------------------------------
	public		int		getClosestManagedPoint(
						MiPart part, 
						MiPoint location, 
						MiiManagedPointValidator validator, 
						MiPart srcPart, int srcConnPtNumber,
						MiPart destPart, int destConnPtNumber,
						MiPart ignoreObj,
						boolean allowSameSrcAndDest, 
						boolean findSrcObj,
						MiManagedPointSearchResults results)
		{
		if ((part == ignoreObj)
			|| (!enabled)
			|| ((findSrcObj) && (!allowSameSrcAndDest) && (part == destPart))
			|| ((!findSrcObj) && (!allowSameSrcAndDest) && (part == srcPart)))
			{
			return(-1);
			}

		double 		closestDist 	= Mi_MAX_DISTANCE_VALUE;
		MiPoint 	pt 		= new MiPoint();
		int		closestPtNum	= -1;
		MiPoint 	connPtLocation 	= new MiPoint();
		double		dist;

		MiManagedPoints points = getManagedPoints();
		for (int i = 0; (points != null) && (i < points.size()); ++i)
			{
			MiManagedPoint managedPt = points.elementAt(i);
			MiiManagedPointRule rule = managedPt.getRule();

			if (rule instanceof MiConnectionPointRule)
				{
				MiConnectionPointRule connPtRule = (MiConnectionPointRule )rule;

				if ((findSrcObj) && (!connPtRule.validConnectionSource))
					continue;
				if ((!findSrcObj) && (!connPtRule.validConnectionDestination))
					continue;
				}

			int connPt = managedPt.getPointNumber();
			if (validator != null)
				{
				if (((findSrcObj) 
					&& (!validator.isValidConnectionSource(
						part, connPt, destPart, destConnPtNumber)))
				|| ((!findSrcObj) 
					&& (!validator.isValidConnectionDestination(
						srcPart, srcConnPtNumber, part, connPt))))
					{
					continue;
					}
				}
			for (int j = 0; j < managedPt.getNumberOfPoints(part); ++j)
				{
				managedPt.getPoint(part, j, pt);
				int ptNum = managedPt.getPointNumber(part, j, pt);
				dist = location.getDistanceSquared(pt);

				if (((part != srcPart) || (ptNum != srcConnPtNumber))
					&& ((part != destPart) || (ptNum != destConnPtNumber))
					&& (dist < closestDist))
					{
					closestDist = dist;
					closestPtNum = managedPt.getPointNumber(part, j, pt);
					connPtLocation.x = pt.x;
					connPtLocation.y = pt.y;
					}
				}
			}
		if (closestPtNum != -1)
			{
			results.closestDistSquared = closestDist;
			results.closestObject = part;
			results.closestConnPtLocation.x = connPtLocation.x;
			results.closestConnPtLocation.y = connPtLocation.y;
			results.closestConnPtNumber = closestPtNum;
			}
//System.out.println("getClosestManagedPoint = " + closestPtNum);
		return(closestPtNum);
		}
	public		MiPoint		getCustomManagedPoint(MiPart part, int pointNumber, MiPoint point)
		{
		MiManagedPoints points = getManagedPoints();
		for (int i = 0; (points != null) && (i < points.size()); ++i)
			{
			MiManagedPoint managedPt = points.elementAt(i);
			if (managedPt.getPointNumber() == pointNumber)
				{
				point.x = managedPt.getLocation().x * part.getWidth() + part.getCenterX();
				point.y = managedPt.getLocation().y * part.getHeight() + part.getCenterY();
				return(point);
				}
			}
		throw new IllegalArgumentException(this + ": " + part 
			+ ": Part has no connection point numbered: " + pointNumber);
		}
	// -------------------------------------------------------
	//	System-wide/global routines
	// ---------------------------------------------------------
	public static	MiPoint		getLocationOfCommonPoint(MiPart part, int pointNumber, MiPoint point)
		{
		// If represents a point in a polyline-like object...
		if ((pointNumber >= Mi_MIN_CUSTOM_LOCATION) && (pointNumber <= Mi_MAX_CUSTOM_LOCATION))
			{
			// FIX: implement this
			point.copy(part.getCenter());
			}
		else if ((pointNumber >= Mi_MIN_COMMON_LOCATION) && (pointNumber <= Mi_MAX_COMMON_LOCATION))
			{
			return(part.getBounds().getLocationOfCommonPoint(pointNumber, point));
			}
		part.getPoint(pointNumber, point);
		return(point);
		}
					// Returns conn point number and connPtLocation
	public static	int		getClosestCommonPoint(MiPart part, MiPoint location, MiPoint connPtLocation)
		{
		double 	closestDist 	= Mi_MAX_DISTANCE_VALUE;
		MiPoint	pt		= new MiPoint();
		MiPart 	closestObj;

		int closestConnPt = part.getBounds().getClosestCommonPoint(location, connPtLocation);
		closestDist = location.getDistanceSquared(connPtLocation);
		for (int i = 0; i < part.getNumberOfPoints(); ++i)
			{
			part.getPoint(i, pt);
			double dist = location.getDistanceSquared(pt);

			if (dist < closestDist)
				{
				closestDist = dist;
				closestConnPt = i;
				}
			}
//System.out.println("getClosestCommonPt = " + closestConnPt);
		return(closestConnPt);
		}

	public static	MiPart		getDefaultManagedPoint(	
						MiPart part, 	
						int[] connPt,
						MiManagedPointManager pointManagerKind)
		{
		MiManagedPoints points = null;
		MiManagedPointManager connPtManager = pointManagerKind.getManager(part);
		if (connPtManager != null)
			{
			points = connPtManager.getManagedPoints();
			}
		if (points != null)
			{
			MiManagedPoint connPoint = points.elementAt(0);
			if (connPoint.getPointNumber() != -1)
				{
				connPt[0] = connPoint.getPointNumber();
				return(part);
				}
			}
		for (int i = 0; i < part.getNumberOfParts(); ++i)
			{
			MiPart node = getDefaultManagedPoint(part.getPart(i), connPt, pointManagerKind);
			if (node != null)
				return(node);
			}
		return(null);
		}

	public static	MiPoint		getLocationOfDefaultManagedPoint(	
						MiPart part, 	
						MiPoint point,
						MiManagedPointManager pointManagerKind)
		{
		MiPoint location = getLocationOfDefaultManagedPointIncludingParts(
					part, point, pointManagerKind);
		if (location != null)
			{
			point.copy(location);
			}
		else
			{
			point.x = part.getCenterX();
			point.y = part.getCenterY();
			}
		return(point);
		}
	 public static	MiPoint		getLocationOfDefaultManagedPointIncludingParts(
						MiPart part, 
						MiPoint point,
						MiManagedPointManager pointManagerKind)
		{
		MiManagedPoints points = null;
		MiManagedPointManager connPtManager = pointManagerKind.getManager(part);
		if (connPtManager != null)
			{
			points = connPtManager.getManagedPoints();
			}
		if ((points != null) && (points.size() > 0))
			{
			MiManagedPoint managedPoint = points.elementAt(0);
			if (managedPoint.getLocation() != null)
				{
				point.x = managedPoint.getLocation().x * part.getWidth() + part.getCenterX();
				point.y = managedPoint.getLocation().y * part.getHeight() + part.getCenterY();
				return(point);
				}
			return(getLocationOfManagedPoint(part, managedPoint.getPointNumber(), point, pointManagerKind));
			}
		for (int i = 0; i < part.getNumberOfParts(); ++i)
			{
			MiPoint location = getLocationOfDefaultManagedPointIncludingParts(
						part.getPart(i), point, pointManagerKind);
			if (location != null)
				return(location);
			}
		return(null);
		}
	public static	MiManagedPoint	getManagedPoint(	
						MiPart part, 	
						int pointNumber, 
						MiManagedPointManager pointManagerKind)
		{
		MiManagedPoints points = null;
		MiManagedPointManager connPtManager = pointManagerKind.getManager(part);
		if (connPtManager != null)
			{
			points = connPtManager.getManagedPoints();
			}
		if (points != null)
			{
			return(points.getManagedPoint(pointNumber));
			}
		return(null);
		}
	public static	MiPoint		getLocationOfManagedPoint(	
						MiPart part, 	
						int pointNumber, 
						MiPoint point,
						MiManagedPointManager pointManagerKind)
		{
		MiManagedPoints points = null;
		MiManagedPointManager connPtManager = pointManagerKind.getManager(part);
		if (connPtManager != null)
			{
			points = connPtManager.getManagedPoints();
			}
		if (points != null)
			{
			MiManagedPoint managedPoint = points.getManagedPoint(pointNumber);
			if (managedPoint != null)
				{
				managedPoint.getLocationOfPoint(part, pointNumber, point);
				return(point);
				}
			}
		if (pointNumber == Mi_DEFAULT_LOCATION)
			return(getLocationOfDefaultManagedPoint(part, point, pointManagerKind));

		// If represents a point in a polyline-like object...
		if ((pointNumber >= Mi_MIN_CUSTOM_LOCATION) && (pointNumber <= Mi_MAX_CUSTOM_LOCATION))
			{
			if (pointManagerKind.getManager(part) != null)
				pointManagerKind.getManager(part).getCustomManagedPoint(part, pointNumber, point);
			else
				throw new IllegalArgumentException("No Connection Point Manager for: " + part);
			return(point);
			}
		tmpMargins.setMargins(10);
		tmpBounds.zeroOut();
		return(part.getRelativeLocation(pointNumber, tmpBounds, point, tmpMargins));
		}

	public static	void		getClosestManagedPointIncludingAllParts(
						MiPart container, 
						MiPoint location, 
						MiiManagedPointValidator validator,
						MiPart srcObj, int srcConnPtNumber,
						MiPart destObj, int destConnPtNumber,
						MiPart ignoreObj,
						boolean allowSameSrcAndDest, boolean findSrcObj,
						MiManagedPointSearchResults results,
						MiManagedPointManager pointManagerKind)
		{
		MiManagedPointManager man = pointManagerKind.getManager(container);
		MiPoint foundConnPtLocation = new MiPoint();
		if (man != null)
			{
			man.getClosestManagedPoint(
				container, location, validator, 
				srcObj, srcConnPtNumber, 
				destObj, destConnPtNumber, 
				ignoreObj,
				allowSameSrcAndDest,
				findSrcObj,
				results);
			}
		MiManagedPointSearchResults partResults = new MiManagedPointSearchResults();
		MiPoint transformedLocation = new MiPoint();
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			partResults.init();

			if (part.getTransform() != null)
				part.getTransform().dtow(location, transformedLocation);
			else
				transformedLocation.copy(location);

			getClosestManagedPointIncludingAllParts(
					part, transformedLocation, validator, 
					srcObj, srcConnPtNumber, 
					destObj, destConnPtNumber, 
					ignoreObj,
					allowSameSrcAndDest, findSrcObj,
					partResults,
					pointManagerKind);

			if (partResults.closestObject != null)
				{
				if (part.getTransform() != null)
					{
					part.getTransform().wtod(
						partResults.closestConnPtLocation, 
						partResults.closestConnPtLocation);
					//TEST partResults.closestDistSquared = part.getTransform().wtodX(
						//partResults.closestDistSquared);
					}
				if (partResults.closestDistSquared < results.closestDistSquared)
				    {
				    results.closestDistSquared = partResults.closestDistSquared;
				    results.closestObject = partResults.closestObject;
				    results.closestConnPtLocation.x = partResults.closestConnPtLocation.x;
				    results.closestConnPtLocation.y = partResults.closestConnPtLocation.y;
				    results.closestConnPtNumber = partResults.closestConnPtNumber;
				    }
				}
			}
		}
	// -------------------------------------------------------
	//	PartRenderer Implementation
	// ---------------------------------------------------------
			// Return true if bounds of renderer are larger than part bounds
	public		boolean		getBounds(MiPart part, MiBounds b)
		{
		MiManagedPoints points = localPoints;
		if (points == null)
			points = getManagedPoints();
		if ((!isVisible()) || (points == null) 
			|| ((getLook() == null) && (!points.hasGraphicsToDraw())))
			{
			return(false);
			}

		MiBounds 	newBounds		= tmpBounds1;
		MiPoint 	pt 			= tmpPoint;
		MiPart 		assocGraphics	 	= getLook();
		MiBounds 	assocGraphicsBounds 	= null;

		newBounds.copy(b);

		if (assocGraphics != null)
			assocGraphicsBounds = assocGraphics.getBounds();

		for (int i = 0; i < points.size(); ++i)
			{
			MiManagedPoint managedPt = points.elementAt(i);
			for (int j = 0; j < managedPt.getNumberOfPoints(part); ++j)
				{
				managedPt.getPoint(part, j, pt);
				unionWithPointBounds(managedPt, newBounds, assocGraphicsBounds, pt);
				}
			}
		if (b.equals(newBounds))
			return(false);
		b.copy(newBounds);
		return(true);
		}
	protected	void		unionWithPointBounds(
						MiManagedPoint managedPoint, 
						MiBounds newBounds, 
						MiBounds assocGraphicsBounds, 
						MiPoint pt)
		{
		if (managedPoint.getLook() != null)
			{
			managedPoint.getLook().getBounds(tmpBounds2);
			tmpBounds2.setCenter(pt);
			newBounds.union(tmpBounds2);
			}
		else if (assocGraphicsBounds != null)
			{
			assocGraphicsBounds.setCenter(pt);
			newBounds.union(assocGraphicsBounds);
			}
		else
			{
			newBounds.union(pt);
			}
		}

			// Return true if OK to render actual part
	public 		boolean		render(MiPart part, MiRenderer renderer)
		{
//MiDebug.println(this + "rendering managed point for : " + part);
		MiManagedPoints points = localPoints;
		if (points == null)
			points = getManagedPoints();

		if ((!isVisible()) || (isHidden()) || (points == null)
			|| ((getLook() == null) && (!points.hasGraphicsToDraw())))
			{
			return(true);
			}

		MiPoint pt 		= tmpPoint;
		MiPart 	assocGraphics 	= getLook();
		for (int i = 0; i < points.size(); ++i)
			{
			MiManagedPoint managedPt = points.elementAt(i);
			if (!managedPt.isVisible(part)) // 1-1-2004
				{
				continue;
				}
//System.out.println("managedPt = " + managedPt);
//System.out.println("managedPt.getNumberOfPoints(part) = " + managedPt.getNumberOfPoints(part));
			for (int j = 0; j < managedPt.getNumberOfPoints(part); ++j)
				{
				managedPt.getPoint(part, j, pt);
				if (managedPt.getLook() != null)
					{
//MiDebug.dump(managedPt.getLook());
//System.out.println("managedPt.getLook().getBackgroundColor() = " + managedPt.getLook().getBackgroundColor());
					managedPt.getLook().setCenter(pt);
					managedPt.getLook().draw(renderer);
					}
				else if (assocGraphics != null)
					{
//System.out.println("ASSSOC.getLook().getBackgroundColor() = " + assocGraphics.getBackgroundColor());
					assocGraphics.setCenter(pt);
					assocGraphics.draw(renderer);
					}
				}
			}
		return(true);
		}
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) + "@" + hashCode() + ":" 
			+ ((isVisible() && (getLook().isVisible())) ? "" : "(NOT Visible)")
			+ ((!isHidden() && (!getLook().isHidden())) ? "" : "(Hidden)")
			+ ((isEnabled()) ? "" : "(NOT Enabled)")
			+ "<" + (getManagedPoints() == null ? "0" : "" 
				+ getManagedPoints().size()) + " Points>, <"
			+ "Rule=" + getRule() + ">, <"
			+ "Look=" + getLook().toString() + ">");
		}
	public		String		getManagedPointsDebugString(MiPart part)
		{
		String str = "";
		for (int i = 0; (getManagedPoints() != null) && (i < getManagedPoints().size()); ++i)
			{
			MiManagedPoint managedPt = getManagedPoints().elementAt(i);
			str += managedPt.toString(part);
/**
			for (int j = 0; j < managedPt.getNumberOfPoints(part); ++j)
				{
				MiPoint pt = new MiPoint();
				managedPt.getPoint(part, j, pt);
				str += "[" + pt + "(#" + managedPt.getPointNumber() + ")]";
				}
**/
			}
		return(str);
		}
	}

