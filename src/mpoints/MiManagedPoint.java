
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
public class MiManagedPoint implements MiiTypes
	{
	private		int			pointNumber;
	private		MiScale			location;
	private		Object			tag;
	private		MiPart			look;
	private		MiiManagedPointRule	rule;
	private static	MiMargins		tmpMargins	= new MiMargins();
	private static	MiBounds		tmpBounds	= new MiBounds();
	private static	MiPoint			tmpPoint	= new MiPoint();



	public				MiManagedPoint()
		{
		this.pointNumber = Mi_CENTER_LOCATION;
		this.look = null;
		this.location = null;
		}
	public				MiManagedPoint(int pointNumber)
		{
		this.pointNumber = pointNumber;
		this.look = null;
		this.location = null;
		}
	public				MiManagedPoint(int pointNumber, MiPart look)
		{
		this.pointNumber = pointNumber;
		this.look = look;
		}
	public				MiManagedPoint(int pointNumber, MiPart look, MiScale location)
		{
		this.pointNumber = pointNumber;
		this.look = look;
		this.location = location;
		}
	public		void		setLocation(MiScale location)
		{
		this.location = location;
		pointNumber = Mi_MIN_CUSTOM_LOCATION;
		}
	public		MiScale		getLocation()
		{
		return(location);
		}
	public		void		setLook(MiPart part)
		{
		look = part;
		if (look != null)
			look.validateLayout();
		}
	public		MiPart		getLook()
		{
		return(look);
		}
	public		void		setTag(Object tag)
		{
		this.tag = tag;
		}
	public		Object		getTag()
		{
		return(tag);
		}
	public		void		setPointNumber(int number)
		{
		pointNumber = number;
		}
	public		int		getPointNumber()
		{
		return(pointNumber);
		}
	public		void		setRule(MiiManagedPointRule r)
		{
		rule = r;
		}
	public		MiiManagedPointRule getRule()
		{
		return(rule);
		}
	public		boolean		managesPointNumber(int pointNumber)
		{
		if ((this.pointNumber == Mi_ALL_LOCATIONS)
			&& (pointNumber >= Mi_MIN_COMMON_LOCATION) 
			&& (pointNumber <= Mi_MAX_COMMON_LOCATION))
			{
			return(true);
			}
		return(this.pointNumber == pointNumber);
		}
	public		int		getNumberOfPoints(MiPart target)
		{
		if (pointNumber == Mi_ALL_LOCATIONS)
			{
			if ((target instanceof MiMultiPointShape) || (target instanceof MiConnection))
				{
				return(target.getNumberOfPoints());
				}
			return(Mi_MAX_COMMON_LOCATION - Mi_MIN_COMMON_LOCATION + 1);
			}
		return(1);
		}
	public 		boolean		isVisible(MiPart target)
		{
		return(target.isShowing(null));
		}
	public 		MiPoint		getLocationOfPoint(MiPart target, int pointNumber, MiPoint pt)
		{
		if ((pointNumber >= Mi_MIN_BUILTIN_LOCATION) && (pointNumber <= Mi_MAX_BUILTIN_LOCATION))
			{
			tmpMargins.setMargins(10);
			tmpBounds.zeroOut();
			return(target.getRelativeLocation(pointNumber, tmpBounds, pt, tmpMargins));
			}
		if ((pointNumber >= Mi_MIN_CUSTOM_LOCATION) && (pointNumber <= Mi_MAX_CUSTOM_LOCATION))
			{
			pt.x = location.x * target.getWidth() + target.getCenterX();
			pt.y = location.y * target.getHeight() + target.getCenterY();
			}
		else 
			{
			target.getPoint(pointNumber, pt);
			}
		return(pt);
		}
	public		MiPoint		getPoint(MiPart target, int index, MiPoint pt)
		{
		if (pointNumber == Mi_ALL_LOCATIONS)
			{
			if ((target instanceof MiMultiPointShape) || (target instanceof MiConnection))
				{
				target.getPoint(index, pt);
				return(pt);
				}
			return(target.getBounds(tmpBounds)
				.getLocationOfCommonPoint(index + Mi_MIN_BUILTIN_LOCATION, pt));
			}
		if ((pointNumber >= Mi_MIN_BUILTIN_LOCATION) && (pointNumber <= Mi_MAX_BUILTIN_LOCATION))
			{
			tmpMargins.setMargins(10);
			tmpBounds.zeroOut();
			return(target.getRelativeLocation(pointNumber, tmpBounds, pt, tmpMargins));
			}
		if ((pointNumber >= Mi_MIN_CUSTOM_LOCATION) && (pointNumber <= Mi_MAX_CUSTOM_LOCATION))
			{
			pt.x = location.x * target.getWidth() + target.getCenterX();
			pt.y = location.y * target.getHeight() + target.getCenterY();
			}
		else 
			{
			target.getPoint(pointNumber, pt);
			}
		return(pt);
		}
	public		int		getPointNumber(MiPart target, int index, MiPoint pt)
		{
		if (pointNumber == Mi_ALL_LOCATIONS)
			{
			if ((target instanceof MiMultiPointShape) || (target instanceof MiConnection))
				{
				return(index);
				}
			return(index + Mi_MIN_COMMON_LOCATION);
			}
		return(pointNumber);
		}
	public		boolean		pick(MiManagedPointManager manager, MiPart part, MiBounds area)
		{
		int num = getNumberOfPoints(part);
		MiPart defaultLook = manager.getLook();
		for (int i = 0; i < num; ++i)
			{
			getPoint(part, i, tmpPoint);
			if (look != null)
				look.getBounds(tmpBounds);
			else
				defaultLook.getBounds(tmpBounds);

			tmpBounds.setCenter(tmpPoint);
			if (area.intersects(tmpBounds))
				return(true);
			}
		return(false);
		}
		
	public		MiManagedPoint	copy()
		{
		MiManagedPoint point = (MiManagedPoint )Utility.makeInstanceOfClass(getClass());
		point.pointNumber 	= pointNumber;
		point.location	 	= location;
		point.tag	 	= tag;
		point.look	 	= look;
		point.rule	 	= rule;
		return(point);
		}
	public		MiManagedPoint	deepCopy()
		{
		MiManagedPoint point = (MiManagedPoint )Utility.makeInstanceOfClass(getClass());
		point.pointNumber 	= pointNumber;
		point.tag	 	= tag;
		if (point.location != null)
			point.location	 = new MiScale(location);
		if (point.look != null)
			point.look	 = look.deepCopy();
		if (point.rule != null)
			point.rule	 = rule.copy();
		return(point);
		}
	public		String		toString(MiPart part)
		{
		String str = "";
		for (int j = 0; j < getNumberOfPoints(part); ++j)
			{
			MiPoint pt = new MiPoint();
			getPoint(part, j, pt);
			str += "[" + pt + "(#" + getPointNumber() + ")]";
			}
		return(str);
		}
	}
