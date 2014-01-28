
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

/**----------------------------------------------------------------------------------------------
 * A standard push button widget.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPushButton extends MiButton 
	{
	public static final	String			Mi_PUSHBUTTON_PROTOTYPE_CLASS_NAME 
							= "Mi_PUSHBUTTON_PROTOTYPE_CLASS_NAME";
	private static		MiPushButton		prototype;
	private			MiPushEventHandler	sensor;




					/**------------------------------------------------------
	 				 * Constructs a new instance of MiPushButton.
					 *------------------------------------------------------*/
	public				MiPushButton()
		{
		super();
		setupMiPushButton();
		}
					/**------------------------------------------------------
	 				 * Constructs a new instance of MiPushButton.
					 * @param text		the push button label
					 *------------------------------------------------------*/
	public				MiPushButton(String text)
		{
		super(text);
		setupMiPushButton();
		}
					/**------------------------------------------------------
	 				 * Constructs a new instance of MiPushButton.
					 * @param obj		the push button graphics
					 *------------------------------------------------------*/
	public				MiPushButton(MiPart obj)
		{
		super(obj);
		setupMiPushButton();
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
			return(new MiPushButton());

		return((MiPushButton )prototype.deepCopy());
		}
					/**------------------------------------------------------
	 				 * Specifies whether the Mi_SELECT_REPEATED_ACTION action 
					 * is to be repeatedly dispatched when this push button
					 * is held down.
					 * @param flag  	true if this should repeat
					 *------------------------------------------------------*/
	public		void		setRepeatSelectWhenHeld(boolean flag)
		{
		sensor.setRepeatSelectWhenHeld(flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether the Mi_SELECT_REPEATED_ACTION action 
					 * is to be repeatedly dispatched when this push button
					 * is held down.
					 * @return 	 	true if this should repeat
					 *------------------------------------------------------*/
	public		boolean		getRepeatSelectWhenHeld()
		{
		return(sensor.getRepeatSelectWhenHeld());
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
	public static	void		setPrototype(MiPushButton p)
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
					 * Mi_PUSHBUTTON_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_PUSHBUTTON_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiPushButton )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
					/**------------------------------------------------------
	 				 * Sets the attributes and event handlers from the prototype
					 * and assigns any custom styling.
					 *------------------------------------------------------*/
	protected	void		setupMiPushButton()
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiPushButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			sensor = (MiPushEventHandler )getEventHandlerWithClass("MiPushEventHandler");
			}
		else
			{
			setAcceptingEnterKeyFocus(true);
			sensor = new MiPushEventHandler();
			appendEventHandler(sensor);
			}

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	}
/****
	Rules and Assumptions:

	1. Modification of a super-classes prototype should not break sub-classes
	=> only assign the prototype to the instances of the class it is assigned to
	=> prototypes only available for concrete (non-abstract) classes

	2. If prototype is not null, then it's properties/attributes/eventhandlers 
	are used, otherwise the properties/attributes/eventhandlers of the code in
	the class itself and then any system-wide MiSystem properties are applied,
	then any class-specific MiSystem properties are applied. 

	3. In all cases any custom style is applied.

	Q1. Do custom styles really need to be class-specific?


*****/

