
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
public class MiArc extends MiPart
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private	static	MiBounds	tmpBounds	= new MiBounds();
	private		MiPoint		center 		= new MiPoint();
	private		MiDistance	radius 		= 1;
	private		double		startAngle 	= 0.0;
	private		double		sweptAngle	= 90.0;

	public				MiArc()
		{
		}

	public				MiArc(MiCoord x, MiCoord y, MiDistance radius, 
						double startAngleDegrees, double sweptAngleDegrees)
		{
		center.x = x;
		center.y = y;
		this.radius = radius;
		startAngle = startAngleDegrees;
		sweptAngle = sweptAngleDegrees;
		}

	public		void		setArcCenter(MiCoord x, MiCoord y)
		{
		center.x = x;
		center.y = y;
		refreshBounds();
		}
	public		MiPoint		getArcCenter()
		{
		return(new MiPoint(center.x, center.y));
		}
	public		MiCoord		getArcCenterX()
		{
		return(center.x);
		}
	public		MiCoord		getArcCenterY()
		{
		return(center.y);
		}
	public		void		setRadius(MiDistance radius)
		{
		this.radius = radius;
		refreshBounds();
		}
	public		MiDistance	getRadius()
		{
		return(radius);
		}
	public		void		setStartAngle(double degrees)
		{
		startAngle = degrees;
		refreshBounds();
		}
	public		double		getStartAngle()
		{
		return(startAngle);
		}
	public		void		setEndAngle(double degrees)
		{
		sweptAngle = degrees - startAngle;
		refreshBounds();
		}
	public		double		getEndAngle()
		{
		return(startAngle + sweptAngle);
		}
	public		void		setSweptAngle(double degrees)
		{
		sweptAngle = degrees;
		refreshBounds();
		}
	public		double		getSweptAngle()
		{
		return(sweptAngle);
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

		startAngle += 180/Math.PI * radians;
		}
					/**------------------------------------------------------
	 				 * Flips this MiPart about the axis specified by the given 
					 * number of radians about the given point.
	 				 * @param center	the center of flip
	 				 * @param radians	the angle of the axis of reflection
					 *------------------------------------------------------*/
	protected	void		doFlip(MiPoint center, double radians)
		{
		while (radians >= Math.PI*2)
			radians -= Math.PI*2;
		while (radians < 0)
			radians += Math.PI*2;

		invalidateArea();
		if (radians == 0)
			{
			// Swap Y
			this.center.y = center.y - (this.center.y - center.y);
			}
		else if (radians == Math.PI/2)
			{
			// Swap X
			this.center.x = center.x - (this.center.x - center.x);
			}
		else
			{
			doRotate(center, -radians);
			// Swap Y
			this.center.y = center.y - (this.center.y - center.y);
			doRotate(center, radians);
			}

		double flipAxisInDegrees = 180/Math.PI * radians;

		startAngle = flipAxisInDegrees + (flipAxisInDegrees - startAngle - sweptAngle);
		
		refreshBounds();
		invalidateLayout();
		}

	public		void		render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		tmpBounds.xmin = center.x - radius;
		tmpBounds.ymin = center.y - radius;
		tmpBounds.xmax = center.x + radius;
		tmpBounds.ymax = center.y + radius;
		renderer.drawCircularArc(tmpBounds, startAngle, sweptAngle);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_ARC_START_ANGLE_NAME))
			{
			setStartAngle(Utility.toDouble(value));
			}
		else if (name.equalsIgnoreCase(Mi_ARC_END_ANGLE_NAME))
			{
			setEndAngle(Utility.toDouble(value));
			}
		else if (name.equalsIgnoreCase(Mi_ARC_SWEPT_ANGLE_NAME))
			{
			setSweptAngle(Utility.toDouble(value));
			}
		else if (name.equalsIgnoreCase(Mi_ARC_CENTER_X_NAME))
			{
			setArcCenter(Utility.toDouble(value), center.y);
			}
		else if (name.equalsIgnoreCase(Mi_ARC_CENTER_Y_NAME))
			{
			setArcCenter(center.x, Utility.toDouble(value));
			}
		else if (name.equalsIgnoreCase(Mi_RADIUS_NAME))
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
		if (name.equalsIgnoreCase(Mi_ARC_START_ANGLE_NAME))
			{
			return(Utility.toShortString(startAngle));
			}
		else if (name.equalsIgnoreCase(Mi_ARC_END_ANGLE_NAME))
			{
			return(Utility.toShortString(startAngle + sweptAngle));
			}
		else if (name.equalsIgnoreCase(Mi_ARC_SWEPT_ANGLE_NAME))
			{
			return(Utility.toShortString(sweptAngle));
			}
		else if (name.equalsIgnoreCase(Mi_ARC_CENTER_X_NAME))
			{
			return(Utility.toShortString(center.x));
			}
		else if (name.equalsIgnoreCase(Mi_ARC_CENTER_Y_NAME))
			{
			return(Utility.toShortString(center.y));
			}
		else if (name.equalsIgnoreCase(Mi_RADIUS_NAME))
			{
			return(Utility.toShortString(radius));
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

		propertyDescriptions = new MiPropertyDescriptions("MiArc");

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ARC_START_ANGLE_NAME, Mi_DOUBLE_TYPE, "0"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ARC_END_ANGLE_NAME, Mi_DOUBLE_TYPE, "180"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ARC_SWEPT_ANGLE_NAME, Mi_DOUBLE_TYPE, "180"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ARC_CENTER_X_NAME, Mi_DOUBLE_TYPE, "0", Mi_X_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ARC_CENTER_Y_NAME, Mi_DOUBLE_TYPE, "0", Mi_Y_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_RADIUS_NAME, Mi_DOUBLE_TYPE, "20", Mi_X_COORD_TYPE));

		propertyDescriptions = new MiPropertyDescriptions(propertyDescriptions);
		propertyDescriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());

		return(propertyDescriptions);
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

		MiArc obj = (MiArc )source;

		center.x 		= obj.center.x;
		center.y 		= obj.center.y;
		radius 			= obj.radius;
		startAngle 		= obj.startAngle;
		sweptAngle		= obj.sweptAngle;
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
		radius = radius * (scale.x + scale.y)/2;
		}
					/**------------------------------------------------------
					 * Realculates the outer bounds of this MiPart. 
					 * @param bounds	the (returned) outer bounds
					 * @overrides		MiMultiPointShape#reCalcBounds
					 *------------------------------------------------------*/
	protected	void		reCalcBounds(MiBounds bounds)
		{
		double radius = getRadius();

		bounds.reverse();

		if (getBackgroundColor() != Mi_TRANSPARENT_COLOR)
			{
			bounds.union(center);
			}
		bounds.union(
			center.x + radius * Math.cos(startAngle * Math.PI/180),
			center.y + radius * Math.sin(startAngle * Math.PI/180));

		bounds.union(
			center.x + radius * Math.cos((startAngle + sweptAngle) * Math.PI/180),
			center.y + radius * Math.sin((startAngle + sweptAngle) * Math.PI/180));

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
					/**------------------------------------------------------
					 * Returns information about this MiPart.
					 * @return		textual information (class name +
					 *			unique numerical id + name)
					 *------------------------------------------------------*/
	public		String		toString()
		{
		String tmp = super.toString();
		tmp += "[center=" + center + ",radius=" + radius 
			+ ",startAngle=" + startAngle + ",swept=" + sweptAngle + "]";
		return(tmp);
		}
	}


