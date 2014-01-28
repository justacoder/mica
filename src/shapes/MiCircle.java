
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
public class MiCircle extends MiPart
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private static	MiBounds	tmpBounds	= new MiBounds();


	public				MiCircle()
		{
		this(0, 0, 1);
		}

	public				MiCircle(MiDistance radius)
		{
		this(0, 0, radius);
		}
	public				MiCircle(MiCoord x, MiCoord y, MiDistance radius)
		{
		replaceBounds(new MiBounds(x - radius, y - radius, x + radius, y + radius));
		}
	public				MiCircle(MiBounds b)
		{
		replaceBounds(b);
		}

	public		void		setRadius(MiDistance r) 	
		{ 
		setWidth(2 * r);
		setHeight(2 * r);
		}
	public		MiDistance	getRadius()
	 	{ 
		return(getWidth()/2);
	 	}

	public		boolean		getIntersectionWithLine(
						MiPoint insidePoint, 
						MiPoint otherPoint, 
						MiPoint returnedIntersectionPoint)
		{
		MiDistance dx = otherPoint.x - insidePoint.x;
		MiDistance dy = otherPoint.y - insidePoint.y;
		getBounds(tmpBounds).shrinkIntoASquare();

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
/*

		double angle = com.swfm.mica.util.Utility.getAngle(dy, dx);

		returnedIntersectionPoint.x = centerX
			+ (tmpBounds.xmax - tmpBounds.xmin)/2 * Math.cos(angle);
		returnedIntersectionPoint.y = centerY
			+ (tmpBounds.ymax - tmpBounds.ymin)/2 * Math.sin(angle);
		return(true);
*/

		}

	public		void		render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		renderer.drawCircle(getBounds(tmpBounds).shrinkIntoASquare());
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_RADIUS_NAME))
			{
			setRadius(Utility.toDouble(value));
			}
		else
			{
			super.setPropertyValue(name, value);
			}
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		if (name.equalsIgnoreCase(Mi_RADIUS_NAME))
			{
			return(Utility.toShortString(getRadius()));
			}
		return(super.getPropertyValue(name));
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = new MiPropertyDescriptions("MiCircle");

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_RADIUS_NAME, Mi_DOUBLE_TYPE, "20", Mi_X_COORD_TYPE));

		propertyDescriptions = new MiPropertyDescriptions(propertyDescriptions);
		propertyDescriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());

		return(propertyDescriptions);
		}
	}

