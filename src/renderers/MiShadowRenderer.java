
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
import java.awt.Color; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiShadowRenderer extends MiPartRenderer implements MiiShadowRenderer, MiiAttributeTypes, MiiTypes
	{
	private		MiBounds	tmpBounds	= new MiBounds();
	private		MiVector	tmpVector	= new MiVector();


	public				MiShadowRenderer()
		{
		attributes.initializeAsInheritedAttributes();
		attributes.setBackgroundRenderer(null);
		attributes.setBackgroundImage(null);
		attributes.setBackgroundTile(null);
		attributes.setBorderLook(Mi_FLAT_BORDER_LOOK);
		}
	public		boolean		render(MiPart obj, MiRenderer renderer)
		{
		if (!enabled)
			return(true);

		MiPart shadowShape = obj.getShadowShape();
		if (shadowShape == noShadowShape)
			return(true);

		// ---------------------------------------------------------------
		// Make this local on the stack because this renderer might be recursively
		// called cause it may be used by many, if not all, parts.
		// ---------------------------------------------------------------
		MiVector locationVector = new MiVector();
		getLocationVector(obj, locationVector);

		Color shadowColor = obj.getShadowColor();
		MiDistance shadowLength = obj.getShadowLength();
		int shadowDirection = obj.getShadowDirection();

		attributes.setColor(shadowColor);
		if (obj.getBackgroundColor() != Mi_TRANSPARENT_COLOR)
			attributes.setBackgroundColor(shadowColor);
		else
			attributes.setBackgroundColor(Mi_TRANSPARENT_COLOR);

		if (shadowShape != null)
			{
			locationVector.x = -locationVector.x;
			locationVector.y = -locationVector.y;
			if (obj instanceof MiImage)
				attributes.setBackgroundColor(shadowColor);
			shadowShape.setAttributes(attributes);
			obj.getBounds(tmpBounds);
			if (obj instanceof MiReference)
				obj.getTransform().dtow(tmpBounds, tmpBounds);
			shadowShape.setBounds(tmpBounds);
			shadowShape.translate(locationVector);
			shadowShape.render(renderer);
			return(true);
			}
		if (obj.getNumberOfParts() != 0)
			{
			for (int i = 0; i < obj.getNumberOfParts(); ++i)
				{
				// FIX: save these in a list on the local stack so can restore after rendering
				obj.getPart(i).setHasShadow(true);
				obj.getPart(i).setShadowLength(shadowLength);
				obj.getPart(i).setShadowColor(shadowColor);
				obj.getPart(i).setShadowDirection(shadowDirection);
				}
			obj.render(renderer);
			for (int i = 0; i < obj.getNumberOfParts(); ++i)
				{
				obj.getPart(i).setHasShadow(false);
				}
			return(true);
			}

		locationVector.x = -locationVector.x;
		locationVector.y = -locationVector.y;
		renderer.getTransform().translate(locationVector);
		renderer.pushOverrideAttributes(attributes);
		obj.render(renderer);
		renderer.popOverrideAttributes();
		locationVector.x = -locationVector.x;
		locationVector.y = -locationVector.y;
		renderer.getTransform().translate(locationVector);

		return(true);
		}
	protected	void		getLocationVector(MiPart part, MiVector vector)
		{
		int shadowDirection = part.getShadowDirection();
		MiDistance shadowLength = part.getShadowLength();
		switch (shadowDirection)
			{
			case Mi_LOWER_RIGHT_LOCATION 	:
			default:
				vector.set(-shadowLength, shadowLength);
				break;
			case Mi_LOWER_LEFT_LOCATION 	:
				vector.set(shadowLength, shadowLength);
				break;
			case Mi_UPPER_RIGHT_LOCATION	:
				vector.set(-shadowLength, -shadowLength);
				break;
			case Mi_UPPER_LEFT_LOCATION	:
				vector.set(shadowLength, -shadowLength);
				break;
			}
		}
			// Return true if bounds of renderer are larger than obj bounds
	public		boolean		getBounds(MiPart obj, MiBounds b)
		{
		obj.getBounds(b);
		getLocationVector(obj, tmpVector);
		if (tmpVector.x < 0)
			b.xmax -= tmpVector.x;
		else
			b.xmin -= tmpVector.x;
		if (tmpVector.y < 0)
			b.ymax -= tmpVector.y;
		else
			b.ymin -= tmpVector.y;
		return(true);
		}
	}


