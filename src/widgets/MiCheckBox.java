
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
public class MiCheckBox extends MiButton 
	{
	public static final	String		Mi_CHECKBOX_PROTOTYPE_CLASS_NAME 
							= "Mi_CHECKBOX_PROTOTYPE_CLASS_NAME";
	private static		MiCheckBox	prototype;



	public				MiCheckBox()
		{
		super(MiToolkit.getCheckMarkIcon().copy());
		setupMiCheckBox();
		}
					// For example: "x"
	public				MiCheckBox(String text)
		{
		super(text);
		setupMiCheckBox();
		}
	public				MiCheckBox(MiPart obj)
		{
		super(obj);
		setupMiCheckBox();
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
			return(new MiCheckBox());

		return((MiCheckBox )prototype.deepCopy());
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
		super.select(flag);
		if (getLabel() != null)
			{
			setInvalidLayoutNotificationsEnabled(false);
			getLabel().setVisible(isSelected());
			getLabel().setCenter(getCenter());
			getLabel().validateLayout();
			setInvalidLayoutNotificationsEnabled(true);
			}
		if (!flag)
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
/*
					// Allows label to extend outside of box
	public		void		setAttachedLabel(MiPart newLabel)
		{
		if (newLabel != null)
			{
			setLabel((MiPart )null);
			appendAttachment(newLabel, Mi_CENTER_LOCATION, null, 0);
			if (isSelected())
				newLabel.setVisible(true);
			else
				newLabel.setVisible(false);
			setInsetMargins(6);
			label = newLabel;
			}
		else
			{
			removeAttachment(label);
			setInsetMargins(1);
			setLabel("x");
			}
		}
*/
					/**------------------------------------------------------
	 				 * Sets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @param p 		the new prototype
					 * @see 		#getPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	void		setPrototype(MiCheckBox p)
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
					 * Mi_CHECKBOX_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_CHECKBOX_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiCheckBox )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
		
	protected	void		setupMiCheckBox()
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCheckBox");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			appendEventHandler(new MiToggleEventHandler());
			setNormalBackgroundColor(getToolkit().getTextFieldEditableBGColor());
			setSelectedBackgroundColor(getToolkit().getTextFieldEditableBGColor());
			setInSensitiveBackgroundColor(getToolkit().getTextFieldInEditableBGColor());
			setBorderLook(Mi_INDENTED_BORDER_LOOK);
			setInsetMargins(2);
			}

		if (getLabel() != null)
			getLabel().setVisible(false);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	}

