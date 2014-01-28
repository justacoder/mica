
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
import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.Utility;
import java.util.StringTokenizer;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public abstract class MiMultiPointShape extends MiPart
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;

	protected 	MiCoord[]	xPoints 		= new MiCoord[2];
	protected 	MiCoord[]	yPoints 		= new MiCoord[2];
	private		int		numberOfPoints		= 0;
	private		boolean		maintainOrthogonality;
	protected 	boolean		lineWidthDependant;


	//***************************************************************************************
	// Point management
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the number of points that define the shape of 
					 * this MiPart. The default is 2, the lower left and upper
					 * right corners.
					 * @return 		the number of points
					 *------------------------------------------------------*/
	public		int		getNumberOfPoints()
		{
		return(numberOfPoints);
		}
	public		void		setNumberOfPoints(int number)
		{
		if (number > xPoints.length)
			{
			xPoints = new MiCoord[number];
			yPoints = new MiCoord[number];
			}
		numberOfPoints = number;
		invalidateArea();
		refreshBounds();
		}
					/**------------------------------------------------------
					 * Sets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number. 
					 * @param pointNum 	the number of the point
					 * @param x 		the new x coordinate of the point
					 * @param y 		the new y coordinate of the point
					 *------------------------------------------------------*/
	public		void		setPoint(int pointNum, MiCoord x, MiCoord y)
		{
		if (pointNum == Mi_LAST_POINT_NUMBER)
			pointNum = numberOfPoints - 1;

		translatePoint(pointNum, x - xPoints[pointNum], y - yPoints[pointNum]);
		}
					/**------------------------------------------------------
					 * Moves the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number. 
					 * @param pointNum 	the number of the point
					 * @param tx 		the new x translation of the point
					 * @param ty 		the new y translation of the point
					 *------------------------------------------------------*/
	public		void		translatePoint(int pointNum, MiCoord tx, MiCoord ty)
		{
		if ((tx == 0) && (ty == 0))
			return;

		if (pointNum == Mi_LAST_POINT_NUMBER)
			pointNum = numberOfPoints - 1;

		if (maintainOrthogonality)
			//|| (maintainOrthogonalityOfTranslatedPoints))
			{
			boolean xPrevIsOrtho = false;
			boolean xNextIsOrtho = false;
			boolean yPrevIsOrtho = false;
			boolean yNextIsOrtho = false;
			MiCoord thePtX = xPoints[pointNum];
			MiCoord thePtY = yPoints[pointNum];
			if ((pointNum > 0) && (thePtX == xPoints[pointNum - 1]))
				xPrevIsOrtho = true;
			else if ((pointNum < numberOfPoints - 1) && (thePtX == xPoints[pointNum + 1]))
				xNextIsOrtho = true;
			if ((pointNum > 0) && (thePtY == yPoints[pointNum - 1]))
				yPrevIsOrtho = true;
			else if ((pointNum < numberOfPoints - 1) && (thePtY == yPoints[pointNum + 1]))
				yNextIsOrtho = true;

/*
			if (maintainOrthogonalityOfTranslatedPoints)
				{
				if ((!xPrevIsOrtho) && (!xNextIsOrtho))
					points[pointNum].x += tx;
				if ((!yPrevIsOrtho) && (!yNextIsOrtho))
					points[pointNum].y += ty;
				}
			else
*/
				{
				if (xPrevIsOrtho)
					xPoints[pointNum - 1] += tx;
				if (xNextIsOrtho)
					xPoints[pointNum + 1] += tx;
				if (yPrevIsOrtho)
					yPoints[pointNum - 1] += ty;
				if (yNextIsOrtho)
					yPoints[pointNum + 1] += ty;

				xPoints[pointNum] += tx;
				yPoints[pointNum] += ty;
				}
			}
		else
			{
			xPoints[pointNum] += tx;
			yPoints[pointNum] += ty;
			}

		// Invalidate incase bounds did not change, still want to draw visual change
		invalidateArea();
		refreshBounds();
		}
					/**------------------------------------------------------
					 * Gets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 * @return 	 	the coordinates of the point
					 *------------------------------------------------------*/
	public		MiPoint		getPoint(int pointNum)
		{
		if (pointNum == Mi_LAST_POINT_NUMBER)
			pointNum = numberOfPoints - 1;
		return(new MiPoint(xPoints[pointNum], yPoints[pointNum]));
		}
					/**------------------------------------------------------
					 * Gets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 * @return 	 	the X coordinate of the point
					 *------------------------------------------------------*/
	public		MiCoord		getPointX(int pointNum)
		{
		if (pointNum == Mi_LAST_POINT_NUMBER)
			pointNum = numberOfPoints - 1;
		return(xPoints[pointNum]);
		}
					/**------------------------------------------------------
					 * Gets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 * @return 	 	the Y coordinate of the point
					 *------------------------------------------------------*/
	public		MiCoord		getPointY(int pointNum)
		{
		if (pointNum == Mi_LAST_POINT_NUMBER)
			pointNum = numberOfPoints - 1;
		return(yPoints[pointNum]);
		}
					/**------------------------------------------------------
					 * Gets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 * @param point	 	the (returned) coordinates of the
					 *			point
					 *------------------------------------------------------*/
	public		void		getPoint(int pointNum, MiPoint point)
		{
		if (pointNum == Mi_LAST_POINT_NUMBER)
			pointNum = numberOfPoints - 1;
		point.set(xPoints[pointNum], yPoints[pointNum]);
		}
					/**------------------------------------------------------
					 * Append another point to the points that define the
					 * shape of this MiPart. Override this, if desired, as it 
					 * implements the core functionality.
					 * @param x	 	the x coord of the point to be appended
					 * @param y	 	the y coord of the point to be appended
					 * @overrides		MiPart#appendPoint
					 *------------------------------------------------------*/
	public		void		appendPoint(MiCoord x, MiCoord y)
		{
		if (numberOfPoints == xPoints.length)
			{
			MiCoord[] xNewArray = new MiCoord[numberOfPoints + 1];
			System.arraycopy(xPoints, 0, xNewArray, 0, numberOfPoints);
			xNewArray[numberOfPoints] = x;
			xPoints = xNewArray;

			MiCoord[] yNewArray = new MiCoord[numberOfPoints + 1];
			System.arraycopy(yPoints, 0, yNewArray, 0, numberOfPoints);
			yNewArray[numberOfPoints] = y;
			yPoints = yNewArray;
			}
		else
			{
			xPoints[numberOfPoints] = x;
			yPoints[numberOfPoints] = y;
			}
		++numberOfPoints;
		unionBounds(x, y);
		invalidateLayout();
		invalidateArea();
		}
					/**------------------------------------------------------
					 * Insert another point to the points that define the
					 * shape of this MiPart. Override this, if desired, as it 
					 * implements the core functionality.
					 * @param x	 	the x coord of the point to be appended
					 * @param y	 	the y coord of the point to be appended
					 * @param index	 	the index of the point to insert
					 *			this new point before
					 * @overrides		MiPart#insertPoint
					 *------------------------------------------------------*/
	public		void		insertPoint(MiCoord x, MiCoord y, int pointNum)
		{
		if (pointNum == Mi_LAST_POINT_NUMBER)
			{
			pointNum = numberOfPoints - 1;
			}

		if (numberOfPoints == xPoints.length)
			{
			MiCoord[] xNewArray = new MiCoord[xPoints.length + 1];
			System.arraycopy(xPoints, 0, xNewArray, 0, xPoints.length);
			xPoints = xNewArray;
			}
		System.arraycopy(xPoints, pointNum, xPoints, pointNum + 1, xPoints.length - pointNum - 1);
		xPoints[pointNum] = x;

		if (numberOfPoints == yPoints.length)
			{
			MiCoord[] yNewArray = new MiCoord[yPoints.length + 1];
			System.arraycopy(yPoints, 0, yNewArray, 0, yPoints.length);
			yPoints = yNewArray;
			}
		System.arraycopy(yPoints, pointNum, yPoints, pointNum + 1, yPoints.length - pointNum - 1);
		yPoints[pointNum] = y;

		++numberOfPoints;

		unionBounds(x, y);
		invalidateLayout();
		invalidateArea();
		}
					/**------------------------------------------------------
					 * Remove the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 *------------------------------------------------------*/
	public		void		removePoint(int pointNum)
		{
		if (pointNum == Mi_LAST_POINT_NUMBER)
			pointNum = numberOfPoints - 1;

		System.arraycopy(xPoints, pointNum + 1, xPoints, pointNum, xPoints.length - pointNum - 1);
		System.arraycopy(yPoints, pointNum + 1, yPoints, pointNum, yPoints.length - pointNum - 1);

		--numberOfPoints;

		invalidateArea();
		refreshBounds();
		}
					/**------------------------------------------------------
					 * Get the angle of the shape as it exits the point with 
					 * the given number. Points are numbered from 0. 
					 * @param pointNum 	the number of the point
					 * @return		the angle in radians
					 *------------------------------------------------------*/
	public		double		getPointExitAngle(int pointNumber)
		{
		if ((pointNumber + 1 >= numberOfPoints) || (pointNumber == Mi_LAST_POINT_NUMBER))
			return(1.0);
		return(Utility.getAngle(
			yPoints[pointNumber + 1] - yPoints[pointNumber],
			xPoints[pointNumber + 1] - xPoints[pointNumber]));
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
		if (pointNumber == 0)
			return(1.0);

		if (pointNumber == Mi_LAST_POINT_NUMBER)
			pointNumber = numberOfPoints - 1;
		
		return(Utility.getAngle(
			yPoints[pointNumber] - yPoints[pointNumber - 1],
			xPoints[pointNumber] - xPoints[pointNumber - 1]));
		}
	public		void		appendPoints(MiCoord[] xPts, MiCoord[] yPts, int numPoints)
		{
		MiCoord[] xNewArray = new MiCoord[xPoints.length + numPoints];
		System.arraycopy(xPoints, 0, xNewArray, 0, xPoints.length);
		System.arraycopy(xPts, 0, xNewArray, xPoints.length, numPoints);
		xPoints = xNewArray;

		MiCoord[] yNewArray = new MiCoord[yPoints.length + numPoints];
		System.arraycopy(yPoints, 0, yNewArray, 0, yPoints.length);
		System.arraycopy(yPts, 0, yNewArray, yPoints.length, numPoints);
		yPoints = yNewArray;

		numberOfPoints += numPoints;

		for (int i = 0; i < numPoints; ++i)
			{
			unionBounds(xPts[i], yPts[i]);
			}
		invalidateLayout();
		invalidateArea();
		}

	//***************************************************************************************
	// Geometry management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Specifies whether the points in this shape should remain
					 * in their horizontal and vertical positionings. This will
					 * translate the neighbors of a translated point to maintain
					 * orthogonality.
	 				 * @param flag		true if translatePoint will maintain
					 *			orthogonality
					 *------------------------------------------------------*/
	public		void		setMaintainOrthogonality(boolean flag)
		{
		maintainOrthogonality = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether the points in this shape should remain
					 * in their horizontal and vertical positionings. This will
					 * translate the neighbors of a translated point to maintain
					 * orthogonality.
	 				 * @return 		true if translatePoint will maintain
					 *			orthogonality
					 *------------------------------------------------------*/
	public		boolean		getMaintainOrthogonality()
		{
		return(maintainOrthogonality);
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

		invalidateArea();
		double sinR = Math.sin(radians);
		double cosR = Math.cos(radians);
		for (int i = 0; i < getNumberOfPoints(); ++i)
			{
			MiCoord x = xPoints[i] - center.x;
			MiCoord y = yPoints[i] - center.y;
			xPoints[i] = -sinR * y + cosR * x + center.x;
			yPoints[i] = sinR * x + cosR * y + center.y;
			}
		refreshBounds();
		invalidateLayout();
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
			for (int i = 0; i < getNumberOfPoints(); ++i)
				{
				yPoints[i] = center.y - (yPoints[i] - center.y);
				}
			}
		else if (radians == Math.PI/2)
			{
			// Swap X
			for (int i = 0; i < getNumberOfPoints(); ++i)
				{
				xPoints[i] = center.x - (xPoints[i] - center.x);
				}
			}
		else
			{
			doRotate(center, -radians);
			// Swap Y
			for (int i = 0; i < getNumberOfPoints(); ++i)
				{
				yPoints[i] = center.y - (yPoints[i] - center.y);
				}
			doRotate(center, radians);
			}

		refreshBounds();
		invalidateLayout();
		}

					/**------------------------------------------------------
	 				 * Translates the parts of this MiPart by the given 
					 * distances. 
	 				 * @param tx	 	the x translation
	 				 * @param ty	 	the y translation
					 *------------------------------------------------------*/
	protected	void		translateParts(MiDistance tx, MiDistance ty)
		{
		for (int i = 0; i < numberOfPoints; ++i)
			{
			xPoints[i] += tx;
			yPoints[i] += ty;
			}
		}
					/**------------------------------------------------------
	 				 * Scales the parts of this MiPart by the given scale
					 * factor.
	 				 * @param center 	the center of scaling
	 				 * @param scale	 	the scale factor
					 *------------------------------------------------------*/
	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		if ((scale.x <= 0) || (scale.y <= 0))
			{
			throw new IllegalArgumentException(this 
				+ ": Scale factor less than or equal to zero: " + scale);
			}

		if (hasFixedAspectRatio())
			{
			scale = new MiScale(scale);
			if (scale.x < scale.y)
				scale.y = scale.x;
			else
				scale.x = scale.y;
			}

		for (int i = 0; i < numberOfPoints; ++i)
			{
			xPoints[i] = (xPoints[i] - center.x) * scale.x + center.x;
			yPoints[i] = (yPoints[i] - center.y) * scale.y + center.y;
			}
		}

	//***************************************************************************************
	// Bounds management
	//***************************************************************************************

	//Called twice during interactive move, once when setPoint called, once when validateLayout
					/**------------------------------------------------------
					 * Realculates the outer bounds of this MiPart. Override 
					 * this, if desired, as it implements the core 
					 * functionality. 
					 * @param bounds	the (returned) outer bounds
					 *------------------------------------------------------*/
	protected 	void		reCalcBounds(MiBounds bounds)
		{
		bounds.reverse();

		MiDistance lineWidth = getLineWidth();
		if ((!lineWidthDependant) || (lineWidth == 0))
			{
			for (int i = 0; i < numberOfPoints; ++i)
				{
				bounds.union(xPoints[i], yPoints[i]);
				}
			}
		else
			{
			boolean vertical = true;
			boolean horizontal = true;

			for (int i = 0; i < numberOfPoints; ++i)
				{
				bounds.union(xPoints[i], yPoints[i]);
				if (i > 0)
					{
					if (vertical && (xPoints[i] != xPoints[i - 1]))
						vertical = false;
					if (horizontal && (yPoints[i] != yPoints[i - 1]))
						horizontal = false;
					}
				}

			lineWidth = lineWidth/2;
			if (horizontal)
				bounds.addMargins(0, lineWidth, 0, lineWidth);
			else if (vertical)
				bounds.addMargins(lineWidth, 0, lineWidth, 0);
			else
				// Gross generalization...
				bounds.addMargins(lineWidth);
			}
		}

	public		boolean		isVertical()
		{
		for (int i = 1; i < numberOfPoints; ++i)
			{
			if (xPoints[i] != xPoints[i - 1])
				return(false);
			}
		return(true);
		}

	public		boolean		isHorizontal()
		{
		for (int i = 1; i < numberOfPoints; ++i)
			{
			if (yPoints[i] != yPoints[i - 1])
				return(false);
			}
		return(true);
		}

					/**------------------------------------------------------
	 				 * Makes a manipulator for this MiPart. Override this,
                                         * if desired, as it implements the core functionality.
					 * The default behavior is to return an instance of the
					 * MiBoundsManipulator class.
	 				 * @return 		the manipulator
					 *------------------------------------------------------*/
	public	MiiManipulator		makeManipulator()
		{
		return(new MiMultiPointManipulator(this));
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

		MiMultiPointShape obj 	= (MiMultiPointShape )source;

		xPoints 		= (MiCoord[] )obj.xPoints.clone();
		yPoints 		= (MiCoord[] )obj.yPoints.clone();
		maintainOrthogonality 	= obj.maintainOrthogonality;
		lineWidthDependant 	= obj.lineWidthDependant;
		numberOfPoints 		= obj.numberOfPoints;
		}
					/**------------------------------------------------------
					 * Returns information about this MiPart.
					 * @return		textual information (class name +
					 *			unique numerical id + name)
					 *------------------------------------------------------*/
	public		String		toString()
		{
		String tmp = super.toString();
		tmp += ": " + numberOfPoints + " Points: ";
		for (int i = 0; i < numberOfPoints; ++i)
			{
			if (i != 0)
				tmp = tmp.concat(", ");
			tmp = tmp.concat(
				"[x=" + Utility.toShortString(xPoints[i]) 
				+ ",y=" + Utility.toShortString(yPoints[i]) + "]");
			}
		if (maintainOrthogonality)
			{
			tmp += "[maintainOrthogonality]";
			}
		return(tmp);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_POINTS_NAME))
			{
			StringTokenizer t = new StringTokenizer(value, " ,\t");
			setNumberOfPoints(0);
			while (t.hasMoreElements())
				{
				String xStr = (String )t.nextElement();
				String yStr = (String )t.nextElement();
				MiCoord x = Utility.toDouble(xStr);
				MiCoord y = Utility.toDouble(yStr);
				appendPoint(x, y);
				}
			}
		else if ((name.length() > 1) && (Character.toUpperCase(name.charAt(0)) == 'X'))
			{
			String pointNumStr = name.substring(1);
			if (!Utility.isInteger(pointNumStr))
				{
				super.setPropertyValue(name, value);
				}
			else
				{
				int pointNum = Utility.toInteger(pointNumStr) - 1;
				MiCoord x = Utility.toDouble(value);
				MiCoord y = getPointY(pointNum);
				if (pointNum + 1 > numberOfPoints)
					{
					setNumberOfPoints(pointNum + 1);
					}
				setPoint(pointNum, x, y);
				}
			}
		else if ((name.length() > 1) && (Character.toUpperCase(name.charAt(0)) == 'Y'))
			{
			String pointNumStr = name.substring(1);
			if (!Utility.isInteger(pointNumStr))
				{
				super.setPropertyValue(name, value);
				}
			else
				{
				int pointNum = Utility.toInteger(pointNumStr) - 1;
				MiCoord x = getPointX(pointNum);
				MiCoord y = Utility.toDouble(value);
				if (pointNum + 1 > numberOfPoints)
					{
					setNumberOfPoints(pointNum + 1);
					}
				setPoint(pointNum, x, y);
				}
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
		if (name.equalsIgnoreCase(Mi_POINTS_NAME))
			{
			String str = new String();
			for (int i = 0; i < numberOfPoints; ++i)
				{
				if (i > 0)
					{
					str += ",";
					}
				str += Utility.toShortString(xPoints[i]);
				str += ",";
				str += Utility.toShortString(yPoints[i]);
				}
			return(str);
			}
		else if ((name.length() > 1) && (Character.toUpperCase(name.charAt(0)) == 'X'))
			{
			String pointNumStr = name.substring(1);
			if (!Utility.isInteger(pointNumStr))
				{
				return(super.getPropertyValue(name));
				}
			else
				{
				int pointNum = Utility.toInteger(pointNumStr) - 1;
				return(Utility.toShortString(xPoints[pointNum]));
				}
			}
		else if ((name.length() > 1) && (Character.toUpperCase(name.charAt(0)) == 'Y'))
			{
			String pointNumStr = name.substring(1);
			if (!Utility.isInteger(pointNumStr))
				{
				return(super.getPropertyValue(name));
				}
			else
				{
				int pointNum = Utility.toInteger(pointNumStr) - 1;
				return(Utility.toShortString(yPoints[pointNum]));
				}
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

		propertyDescriptions = new MiPropertyDescriptions("MiMultiPointShape");

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_POINTS_NAME, Mi_STRING_TYPE, "", Mi_XY_COORD_ARRAY_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription("X1", Mi_DOUBLE_TYPE, "0", Mi_X_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription("Y1", Mi_DOUBLE_TYPE, "0", Mi_Y_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription("X2", Mi_DOUBLE_TYPE, "0", Mi_X_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription("Y2", Mi_DOUBLE_TYPE, "0", Mi_Y_COORD_TYPE));

		propertyDescriptions = new MiPropertyDescriptions(propertyDescriptions);
		propertyDescriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());

		return(propertyDescriptions);
		}
	}

