
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
import com.swfm.mica.util.TypedVector;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiManagedPoints extends TypedVector implements MiiTypes
	{
	private		int			numCustomLocations	= 0;
	private		boolean			hasGraphicsToDraw;
	private		MiiManagedPointRule	defaultRule;
	private		MiPart			defaultLook;


	public				MiManagedPoints()
		{
		}
	public				MiManagedPoints(MiManagedPoints points)
		{
		for (int i = 0; i < points.size(); ++i)
			addElement(points.elementAt(i).copy());
		numCustomLocations += points.numCustomLocations;
		hasGraphicsToDraw |= points.hasGraphicsToDraw;
		}
	public				MiManagedPoints(MiManagedPoint pt)
		{
		addElement(pt);
		}
	public		MiManagedPoints	copy()
		{
		return(new MiManagedPoints(this));
		}
	public		void		setDefaultRule(MiiManagedPointRule rule)
		{
		defaultRule = rule;
		}
	public		MiiManagedPointRule	getDefaultRule()
		{
		return(defaultRule);
		}
	public		void		setDefaultLook(MiPart look)
		{
		defaultLook = look;
		}
	public		MiPart		getDefaultLook()
		{
		return(defaultLook);
		}
	public		boolean		hasGraphicsToDraw()
		{
		return(hasGraphicsToDraw);
		}
	public		MiManagedPoint	elementAt(int index)
		{
		return((MiManagedPoint )vector.elementAt(index));
		}
	public		MiManagedPoint	lastElement()
		{
		return((MiManagedPoint )vector.lastElement());
		}
	public		void		addElement(MiManagedPoint pt)
		{
		vector.addElement(pt);
		if (pt.getLook() != null)
			hasGraphicsToDraw = true;
		}
	public		void		insertElementAt(MiManagedPoint pt, int index)
		{
		vector.insertElementAt(pt, index);
		if (pt.getLook() != null)
			hasGraphicsToDraw = true;
		}
	public		void		setElementAt(MiManagedPoint pt, int index)
		{
		vector.setElementAt(pt, index);
		if (pt.getLook() != null)
			hasGraphicsToDraw = true;
		else
			reCalcHasGraphics();
		}
	public		boolean		removeElement(MiManagedPoint pt)
		{
		boolean flag = vector.removeElement(pt);
		reCalcHasGraphics();
		return(flag);
		}
	public		int		indexOf(MiManagedPoint pt)
		{
		return(vector.indexOf(pt));
		}
	public		boolean		contains(MiManagedPoint pt)
		{
		return(vector.contains(pt));
		}
	public		MiManagedPoint[]	toArray()
		{
		MiManagedPoint[] arrayCopy = new MiManagedPoint[vector.size()];
		System.arraycopy(vector.toArray(), 0, arrayCopy, 0, vector.size());
		return(arrayCopy);
		}

	// -------------------------------------------------------
	//	Adding/Removing managed points
	// ---------------------------------------------------------
	// Accepts Mi_CENTER_LOCATION etc... or number of a point, presumably of a multipointShape with
	// Mi_LAST_POINT being the last point, FIX: Add -2 being all points...
	public		void		appendManagedPoint(int pointLocation)
		{
		addElement(new MiManagedPoint(pointLocation));
		}
	public		void		removeManagedPoint(int pointLocation)
		{
		removeElement(getNonNullManagedPoint(pointLocation));
		}

	public		void		appendManagedPoint(int pointLocation, MiPart assocGraphics)
		{
		addElement(new MiManagedPoint(pointLocation, assocGraphics));
		if (assocGraphics != null)
			hasGraphicsToDraw = true;
		}
	public		int		appendManagedPoint(MiScale pt)
		{
		addElement(new MiManagedPoint(Mi_MIN_CUSTOM_LOCATION + numCustomLocations++, null, pt));
		return(Mi_MIN_CUSTOM_LOCATION + numCustomLocations - 1);
		}
	public		int		appendManagedPoint(MiScale pt, MiPart assocGraphics)
		{
		addElement(new MiManagedPoint(Mi_MIN_CUSTOM_LOCATION + numCustomLocations++, assocGraphics, pt));
		if (assocGraphics != null)
			hasGraphicsToDraw = true;
		return(Mi_MIN_CUSTOM_LOCATION + numCustomLocations - 1);
		}
	public		void		removeManagedPoint(MiPoint pt)
		{
		for (int i = 0; i < size(); ++i)
			{
			MiManagedPoint managedPoint = elementAt(i);
			if (managedPoint.getLocation().equals(pt))
				{
				removeElementAt(i);
				reCalcHasGraphics();
				return;
				}
			}
		}
	public		MiManagedPoint	getManagedPoint(int pointLocation)
		{
		for (int i = 0; i < size(); ++i)
			{
			if (elementAt(i).managesPointNumber(pointLocation))
				return(elementAt(i));
			}
		return(null);
		}
	protected	MiManagedPoint	getNonNullManagedPoint(int pointLocation)
		{
		MiManagedPoint managedPoint = getManagedPoint(pointLocation);
		if (managedPoint == null)
			throw new IllegalArgumentException("No managed point at location number: " + pointLocation);
		return(managedPoint);
		}
	// -------------------------------------------------------
	//	Connection points properties
	// ---------------------------------------------------------
	public		void		setTag(int pointLocation, Object tag)
		{
		getNonNullManagedPoint(pointLocation).setTag(tag);
		}
	public		Object		getTag(int pointLocation)
		{
		return(getNonNullManagedPoint(pointLocation).getTag());
		}

	protected	void		reCalcHasGraphics()
		{
		for (int i = 0; i < size(); ++i)
			{
			if (elementAt(i).getLook() != null)
				{
				hasGraphicsToDraw = true;
				return;
				}
			}
		hasGraphicsToDraw = false;
		}

	}

