
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
public class MiCircleToggleButton extends MiToggleButton
	{
	public static final	String		Mi_CIRCLE_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME 
							= "Mi_CIRCLE_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME";
	private static		MiCircleToggleButton	prototype;
	public static final 	int		Mi_FILLED_WHEN_SELECTED	= 1;
	public static final 	int		Mi_DOTTED_WHEN_SELECTED	= 2;
	private			int		type;
	private			MiBounds	tmpBounds;


	public			MiCircleToggleButton()
		{
		this(Mi_FILLED_WHEN_SELECTED);
		}
	public			MiCircleToggleButton(int type)
		{
		super();

		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCircleToggleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			setInsetMargins(6);
			}

		setType(type);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
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
			return(new MiCircleToggleButton());

		return((MiCircleToggleButton )prototype.deepCopy());
		}
	public		void		setType(int type)
		{
		this.type = type;
		setNormalBorderLook(Mi_INDENTED_BORDER_LOOK);
		setKeyboardFocusBorderLook(Mi_INDENTED_BORDER_LOOK);
		setAcceptingMouseFocus(false);
		switch (type)
			{
			default:
			case Mi_FILLED_WHEN_SELECTED :
				setShape(CIRCLE_SHAPE);
				//setSensitiveDeSelectedBGColor(getSensitiveSelectedBGColor());
				setSelectedBackgroundColor(getToolkit().getTextFieldEditableBGColor());
				break;
			case Mi_DOTTED_WHEN_SELECTED :
				tmpBounds = new MiBounds();
				setShape(CIRCLE_SHAPE);
				//setSelectedBackgroundColor(getNormalBackgroundColor());
				//setNormalColor(getToolkit().getTextFieldEditableBGColor());
				setNormalBackgroundColor(getToolkit().getTextFieldEditableBGColor());
				setSelectedBackgroundColor(getToolkit().getTextFieldEditableBGColor());
				break;
			}
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
	public static	void		setPrototype(MiCircleToggleButton p)
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
					 * Mi_CIRCLE_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_CIRCLE_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiCircleToggleButton )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
		
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiCircleToggleButton obj = (MiCircleToggleButton )source;
		setType(obj.type);
		}
	public		void		select(boolean flag)
		{
		if (flag != isSelected())
			{
			super.select(flag);
			if (type == Mi_DOTTED_WHEN_SELECTED)
				invalidateArea();
			}
		}
	public		void		render(MiRenderer renderer)
		{
		super.render(renderer);

		if ((type == Mi_DOTTED_WHEN_SELECTED) && (isSelected()))
			{
			renderer.setBackgroundColor(getSelectedColor());
			renderer.setBorderLook(Mi_NONE);
			getBounds(tmpBounds);
			tmpBounds.setWidth(tmpBounds.getWidth()/3);
			tmpBounds.setHeight(tmpBounds.getHeight()/3);
			renderer.drawCircle(tmpBounds);
			renderer.setAttributes(null);
			}
		}
	}

