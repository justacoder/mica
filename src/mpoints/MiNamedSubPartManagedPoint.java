
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
public class MiNamedSubPartManagedPoint extends MiManagedPoint
	{
	private		String		nameOfPartWithPoint;
	private		int		customManagedPointNumber;

	public				MiNamedSubPartManagedPoint()
		{
		}
	public				MiNamedSubPartManagedPoint(
						int pointNumber, 
						String nameOfPartWithPoint, 
						int customManagedPointNumber)
		{
		super(pointNumber);
		this.nameOfPartWithPoint = nameOfPartWithPoint;
		this.customManagedPointNumber = customManagedPointNumber;
		}
	public				MiNamedSubPartManagedPoint(
						int pointNumber, 
						MiPart look, 
						String nameOfPartWithPoint,
						int customManagedPointNumber)
		{
		super(pointNumber, look);
		this.nameOfPartWithPoint = nameOfPartWithPoint;
		this.customManagedPointNumber = customManagedPointNumber;
		}
	public				MiNamedSubPartManagedPoint(
						int pointNumber, 
						MiPart look, 
						MiScale location, 
						String nameOfPartWithPoint,
						int customManagedPointNumber)
		{
		super(pointNumber, look, location);
		this.nameOfPartWithPoint = nameOfPartWithPoint;
		this.customManagedPointNumber = customManagedPointNumber;
		}

	public		String		getNameOfPartWithPoint()
		{
		return(nameOfPartWithPoint);
		}
	public		int		getPointNumberOfPartWithPoint()
		{
		return(super.getPointNumber());
		}

	public 		boolean		isVisible(MiPart target)
		{
		MiPart targetPart = target.isContainerOfWithAttachments(nameOfPartWithPoint);
		if (targetPart == null)
			{
			targetPart = target;
			}

		return(targetPart.isShowing(null));
		}
	public 		MiPoint		getLocationOfPoint(MiPart target, int pointNumber, MiPoint pt)
		{
		MiPart targetPart = target.isContainerOfWithAttachments(nameOfPartWithPoint);
		if (targetPart == null)
			{
			if (nameOfPartWithPoint.equals(target.getName()))
				{
				targetPart = target;
				}
			else
				{
				//MiDebug.println(MiDebug.getMicaClassName(this) 
					//+ ":Warning - target \"" + target + "\" has no part with name \"" 
					//+ nameOfPartWithPoint);
				return(target.getCenter());
				}
			}
		pt = super.getLocationOfPoint(targetPart, super.getPointNumber(), pt);
		if (target instanceof MiReference)
			{
			((MiReference )target).getTransform().wtod(pt, pt);
			}
		return(pt);
		}
	public		int		getNumberOfPoints(MiPart target)
		{
		return(1);
		}
	public		int		getPointNumber(MiPart target, int index, MiPoint pt)
		{
		return(customManagedPointNumber);
		}
	public		MiPoint		getPoint(MiPart target, int index, MiPoint pt)
		{
		MiPart targetPart = target.isContainerOfWithAttachments(nameOfPartWithPoint);
		if (targetPart == null)
			{
			if (nameOfPartWithPoint.equals(target.getName()))
				{
				targetPart = target;
				}
			else
				{
				//MiDebug.println(MiDebug.getMicaClassName(this) 
					//+ ":Warning - target \"" + target + "\" has no part with name \"" 
					//+ nameOfPartWithPoint);
				return(target.getCenter());
				}
			}
		pt = super.getPoint(targetPart, index, pt);
		if (target instanceof MiReference)
			{
			((MiReference )target).getTransform().wtod(pt, pt);
			}
		return(pt);
		}
	public		boolean		managesPointNumber(int pointNumber)
		{
		return(pointNumber == customManagedPointNumber);
		}
	public		int		getPointNumber()
		{
		return(customManagedPointNumber);
		}
	public		MiManagedPoint	copy()
		{
		MiNamedSubPartManagedPoint point = (MiNamedSubPartManagedPoint )super.copy();
		point.nameOfPartWithPoint = nameOfPartWithPoint;
		point.customManagedPointNumber = customManagedPointNumber;
		return(point);
		}
	public		MiManagedPoint	deepCopy()
		{
		MiNamedSubPartManagedPoint point = (MiNamedSubPartManagedPoint )super.deepCopy();
		point.nameOfPartWithPoint = nameOfPartWithPoint;
		point.customManagedPointNumber = customManagedPointNumber;
		return(point);
		}
	public		String		toString(MiPart part)
		{
		String str = "";
		for (int j = 0; j < getNumberOfPoints(part); ++j)
			{
			MiPoint pt = new MiPoint();
			getPoint(part, j, pt);
			str += nameOfPartWithPoint + "[" + getPointNumberOfPartWithPoint() 
				+ "][" + pt + "(#" + getPointNumber() + ")]";
			}
		return(str);
		}
	}



