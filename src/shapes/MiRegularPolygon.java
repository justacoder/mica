
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
public class MiRegularPolygon extends MiPolygon
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private static final int	Mi_DEFAULT_NUMBER_OF_SIDES 	= 5;
	public static final String	Mi_NUMBER_OF_SIDES_NAME 	= "numberOfSides";
	private		int		numberOfSides;

	public				MiRegularPolygon()
		{
		lineWidthDependant = true;
		setNumberOfSides(Mi_DEFAULT_NUMBER_OF_SIDES);
		}

	public		void		setNumberOfSides(int numSides)
		{
		while (getNumberOfPoints() < numSides)
			appendPoint(new MiPoint());
		while (getNumberOfPoints() > numSides)
			removePoint(0);

		MiDistance width = getBounds().getWidth();
		if (width == 0)
			width = 1;

		MiCoord cx = getCenterX();
		MiCoord cy = getCenterY();

		double angleIncrement = numSides > 1 ? 2 * Math.PI/numSides : 0;
		for (int i = 0; i < numSides; ++i)
			{
			setPoint(i, 
				cx + width/2 * Math.cos(angleIncrement * i),
				cy + width/2 * Math.sin(angleIncrement * i));
			}
		}
	public		int		getNumberOfSides()
		{
		return(numberOfSides);
		}
					/**------------------------------------------------------
					 * Sets the attribute with the given name to the given
					 * value. Valid names are found in the 
					 * MiiAttributeTypes.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_NUMBER_OF_SIDES_NAME))
			setNumberOfSides(Utility.toInteger(value));
		else
			super.setPropertyValue(name, value);
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
		if (name.equalsIgnoreCase(Mi_NUMBER_OF_SIDES_NAME))
			return("" + getNumberOfSides());

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

		propertyDescriptions = new MiPropertyDescriptions(super.getPropertyDescriptions());

		propertyDescriptions.addElement(new MiPropertyDescription(
			Mi_NUMBER_OF_SIDES_NAME, Mi_INTEGER_TYPE, "" + Mi_DEFAULT_NUMBER_OF_SIDES));
		return(propertyDescriptions);
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @param source	the part to copy
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);
		numberOfSides 	= ((MiRegularPolygon )source).numberOfSides;
		}

	}

