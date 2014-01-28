
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
public class MiTab extends MiPushButton
	{
	public static	String		Mi_TAB_LEFT_SLANT_ANGLE_NAME	= "Mi_TAB_LEFT_SLANT_ANGLE_NAME";
	public static	String		Mi_TAB_RIGHT_SLANT_ANGLE_NAME	= "Mi_TAB_RIGHT_SLANT_ANGLE_NAME";
	public static	String		Mi_TAB_PROTOTYPE_CLASS_NAME	= "Mi_TAB_PROTOTYPE_CLASS_NAME";
	public static	String		Mi_TAB_TOP_SHAPE_PROTOTYPE_CLASS_NAME	= "Mi_TAB_TOP_SHAPE_PROTOTYPE_CLASS_NAME";
	public static	String		Mi_TAB_BOTTOM_SHAPE_PROTOTYPE_CLASS_NAME	= "Mi_TAB_BOTTOM_SHAPE_PROTOTYPE_CLASS_NAME";
	public static	String		Mi_TAB_LEFT_SHAPE_PROTOTYPE_CLASS_NAME	= "Mi_TAB_LEFT_SHAPE_PROTOTYPE_CLASS_NAME";
	public static	String		Mi_TAB_RIGHT_SHAPE_PROTOTYPE_CLASS_NAME	= "Mi_TAB_RIGHT_SHAPE_PROTOTYPE_CLASS_NAME";
	public static	String		Mi_TAB_TOP_SHAPE_PROTOTYPE_ICON_NAME	= "Mi_TAB_TOP_SHAPE_PROTOTYPE_ICON_NAME";
	public static	String		Mi_TAB_BOTTOM_SHAPE_PROTOTYPE_ICON_NAME	= "Mi_TAB_BOTTOM_SHAPE_PROTOTYPE_ICON_NAME";
	public static	String		Mi_TAB_LEFT_SHAPE_PROTOTYPE_ICON_NAME	= "Mi_TAB_LEFT_SHAPE_PROTOTYPE_ICON_NAME";
	public static	String		Mi_TAB_RIGHT_SHAPE_PROTOTYPE_ICON_NAME	= "Mi_TAB_RIGHT_SHAPE_PROTOTYPE_ICON_NAME";

	private static	MiTab		prototype;
	private static	MiPart		topShapePrototype;
	private	static	MiPart		bottomShapePrototype;
	private	static	MiPart		leftShapePrototype;
	private	static	MiPart		rightShapePrototype;

	private		int		location;
	private		double		leftSlantAngle		= 60.0/180 * Math.PI;
	private	 	double		rightSlantAngle		= 60.0/180 * Math.PI;


	public				MiTab()
		{
		this(Mi_TOP);
		}
	public				MiTab(int location)
		{
		this.location = location;
		setupMiTab();
		}
					/**------------------------------------------------------
	 				 * Creates a new widget from the prototype. This is the
					 * factory pattern implementation for this widget. If the
					 * prototype is null, then the default contructor is used.
					 * @return 		the new widget
					 * @see 		#setPrototype
					 *------------------------------------------------------*/
	public static	MiWidget	create()
		{
		if (prototype == null)
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equals(Mi_TAB_LEFT_SLANT_ANGLE_NAME))
			setLeftSlantAngle(Utility.toDouble(value));
		else if (name.equals(Mi_TAB_RIGHT_SLANT_ANGLE_NAME))
			setRightSlantAngle(Utility.toDouble(value));
		else
			super.setPropertyValue(name, value);
		}
	public		void		setLeftSlantAngle(double d)
		{
		leftSlantAngle = d;
		invalidateLayout();
		}
	public		double		getLeftSlantAngle()
		{
		return(leftSlantAngle);
		}
	public 		void		setRightSlantAngle(double d)
		{
		rightSlantAngle = d;
		invalidateLayout();
		}
	public 		double		getRightSlantAngle()
		{
		return(rightSlantAngle);
		}

	public		void		setLocation(int location)
		{
		this.location = location;
		invalidateLayout();
		}
	public		int		getLocation()
		{
		return(location);
		}
	protected	void		doLayout()
		{
		MiPart shape = getShape();
		MiBounds bounds = getBounds();
		if (shape instanceof MiPolygon)
			{
			MiPolygon polygon = (MiPolygon )shape;
			MiCoord leftSlantWidth = bounds.getHeight() * Math.cos(leftSlantAngle);
			MiCoord rightSlantWidth = bounds.getHeight() * Math.cos(rightSlantAngle);
			switch (location)
				{
				case Mi_TOP :
					polygon.setPoint(0, bounds.xmin, bounds.ymin);
					polygon.setPoint(1, bounds.xmin + leftSlantWidth, bounds.ymax);
					polygon.setPoint(2, bounds.xmax - rightSlantWidth, bounds.ymax);
					polygon.setPoint(3, bounds.xmax, bounds.ymin);
					break;
				case Mi_BOTTOM :
					polygon.setPoint(0, bounds.xmin, bounds.ymax);
					polygon.setPoint(1, bounds.xmin + leftSlantWidth, bounds.ymin);
					polygon.setPoint(2, bounds.xmax - rightSlantWidth, bounds.ymin);
					polygon.setPoint(3, bounds.xmax, bounds.ymax);
					break;
				case Mi_TO_LEFT :
					polygon.setPoint(0, bounds.xmax, bounds.ymin);
					polygon.setPoint(1, bounds.xmin, bounds.ymin + leftSlantWidth);
					polygon.setPoint(2, bounds.xmin, bounds.ymax - rightSlantWidth);
					polygon.setPoint(3, bounds.xmax, bounds.ymax);
					break;
				case Mi_TO_RIGHT :
					polygon.setPoint(0, bounds.xmin, bounds.ymin);
					polygon.setPoint(1, bounds.xmax, bounds.ymin + leftSlantWidth);
					polygon.setPoint(2, bounds.xmax, bounds.ymax - rightSlantWidth);
					polygon.setPoint(3, bounds.xmin, bounds.ymax);
					break;
				}
			}
		super.doLayout();
		}
					/**------------------------------------------------------
	 				 * Sets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @param p 		the new prototype
					 * @see 		#getPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	void		setPrototype(MiTab p)
		{
		prototype = p;
		}
					/**------------------------------------------------------
	 				 * Gets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @return  		the prototype
					 * @see 		#setPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	MiWidget	getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_TEXTFIELD_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_TAB_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiTab )Utility.makeInstanceOfClass(prototypeClassName);
			}
		topShapePrototype = makePrototypeShape(Mi_TOP);
		bottomShapePrototype = makePrototypeShape(Mi_BOTTOM);
		leftShapePrototype = makePrototypeShape(Mi_TO_LEFT);
		rightShapePrototype = makePrototypeShape(Mi_TO_RIGHT);
		}
	protected static MiPart		makePrototypeShape(int location)
		{
		String prototypeShapeIconName = null;
		String prototypeShapeClassName = null;
		switch (location)
			{
			case Mi_TOP :
				prototypeShapeClassName 
					= MiSystem.getProperty(Mi_TAB_TOP_SHAPE_PROTOTYPE_CLASS_NAME);
				prototypeShapeIconName 
					= MiSystem.getProperty(Mi_TAB_TOP_SHAPE_PROTOTYPE_ICON_NAME);
				break;
			case Mi_BOTTOM :
				prototypeShapeClassName 
					= MiSystem.getProperty(Mi_TAB_BOTTOM_SHAPE_PROTOTYPE_CLASS_NAME);
				prototypeShapeIconName 
					= MiSystem.getProperty(Mi_TAB_BOTTOM_SHAPE_PROTOTYPE_ICON_NAME);
				break;
			case Mi_TO_LEFT :
				prototypeShapeClassName 
					= MiSystem.getProperty(Mi_TAB_LEFT_SHAPE_PROTOTYPE_CLASS_NAME);
				prototypeShapeIconName 
					= MiSystem.getProperty(Mi_TAB_LEFT_SHAPE_PROTOTYPE_ICON_NAME);
				break;
			case Mi_TO_RIGHT :
				prototypeShapeClassName 
					= MiSystem.getProperty(Mi_TAB_RIGHT_SHAPE_PROTOTYPE_CLASS_NAME);
				prototypeShapeIconName 
					= MiSystem.getProperty(Mi_TAB_RIGHT_SHAPE_PROTOTYPE_ICON_NAME);
				break;
			}
		if (prototypeShapeClassName != null)
			{
			return((MiPart )Utility.makeInstanceOfClass(prototypeShapeClassName));
			}
		if (prototypeShapeIconName != null)
			{
			return(new MiImage(prototypeShapeIconName));
			}
		MiPolygon polygon  = new MiPolygon();
		polygon.setClosed(false);
		polygon.appendPoint(new MiPoint(0, 0));
		polygon.appendPoint(new MiPoint(0.25, 0.5));
		polygon.appendPoint(new MiPoint(0.75, 0.5));
		polygon.appendPoint(new MiPoint(1.0, 0.0));
		return(polygon);
		}
	protected	void		setupMiTab()
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiTab");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			setDisplaysFocusBorder(false);
			setBorderLook(Mi_RAISED_BORDER_LOOK);
			setBackgroundColor(MiColorManager.veryVeryLightGray);
			setMargins(new MiMargins(0));
			setPropertyValues(MiSystem.getPropertiesForClass("MiTab"));
			}

		switch (location)
			{
			case Mi_TOP :
				setShape(topShapePrototype.copy());
				setInsetMargins(new MiMargins(12, 5, 12, 5));
				setMargins(new MiMargins(0));
				break;
			case Mi_BOTTOM :
				setShape(bottomShapePrototype.copy());
				setInsetMargins(new MiMargins(12, 5, 12, 5));
				setMargins(new MiMargins(0));
				break;
			case Mi_TO_LEFT :
				setShape(leftShapePrototype.copy());
				setInsetMargins(new MiMargins(4, 2, 0, 2));
				setMargins(new MiMargins(0));
				break;
			case Mi_TO_RIGHT :
				setShape(rightShapePrototype.copy());
				setInsetMargins(new MiMargins(0, 2, 4, 2));
				setMargins(new MiMargins(0));
				break;
			}
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	}

