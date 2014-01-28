
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
public class MiToggleButton extends MiButton 
	{
	public static final	String			Mi_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME 
							= "Mi_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME";
	private static		MiToggleButton		prototype;



	public				MiToggleButton()
		{
		super();
		setupMiToggleButton();
		}
	public				MiToggleButton(String text)
		{
		super(text);
		setupMiToggleButton();
		}
	public				MiToggleButton(MiPart obj)
		{
		super(obj);
		setupMiToggleButton();
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
	public		void		setValue(String value)
		{
		select(Utility.toBoolean(value));
		}
	public		String		getValue()
		{
		return(Utility.toString(isSelected()));
		}
	public		void		select(boolean flag)
		{
		if (isSelected() == flag)
			return;

		super.select(flag);
		if (!flag)
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	public		void		set(boolean flag)
		{
		select(flag);
		}
	public		boolean		isSet()
		{
		return(isSelected());
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
	public static	void		setPrototype(MiToggleButton p)
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
					 * Mi_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiToggleButton )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	public		void		setShape(int style)
		{
		// Diamonds look too small relative to rectangles...
		if (style == MiVisibleContainer.DIAMOND_SHAPE)
			{
			//if (getInsetMargins().equals(new MiMargins(6)))
				setInsetMargins(8);
			}
		else
			{
			//if (getInsetMargins().equals(new MiMargins(8)))
				setInsetMargins(6);
			}
		super.setShape(style);
		}
	protected	void		setupMiToggleButton()
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiToggleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			//setInsetMargins(6);
			appendEventHandler(new MiToggleEventHandler());
			}
		if (getClass().getName().equals("com.swfm.mica.MiToggleButton"))
			{
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			}
		}
	}

