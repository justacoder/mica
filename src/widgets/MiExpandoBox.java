
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
public class MiExpandoBox extends MiWidget implements MiiActionHandler
	{
	public static final	String			Mi_EXPANDOBOX_PROTOTYPE_CLASS_NAME 
							= "Mi_EXPANDOBOX_PROTOTYPE_CLASS_NAME";
	private static		MiExpandoBox		prototype;
	private static final 	int 			DECORATION_WIDTH	= 8;
	private static final 	int 			DECORATION_HEIGHT	= 8;

	private		MiToggleButton	button;
	private		MiPart		box;
	private		MiRowLayout	buttonLayout;


	public				MiExpandoBox()
		{
		this("");
		}
	public				MiExpandoBox(String label)
		{
		this(new MiText(label));
		}

	public				MiExpandoBox(MiPart label)
		{
		this(label, new MiWidget());
		}
	public				MiExpandoBox(MiPart label, MiPart box)
		{
		this(null, label, box);
		}
	public				MiExpandoBox(MiPart icon, MiPart label, MiPart box)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiExpandoBox");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}

		MiColumnLayout layout = new MiColumnLayout();
		setLayout(layout);
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		setVisibleContainerAutomaticLayoutEnabled(false);
		setInsetMargins(0);
		setBorderLook(Mi_NONE);

		this.box = box;

		buttonLayout = new MiRowLayout();
		buttonLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		buttonLayout.setAlleyHSpacing(8);
		buttonLayout.setLastElementJustification(Mi_RIGHT_JUSTIFIED);
		if (icon != null)
			buttonLayout.appendPart(icon);
		buttonLayout.appendPart(label);

		button = new MiToggleButton(buttonLayout);
		button.setSelectedBackgroundColor(button.getNormalBackgroundColor());
		button.setSelectedBorderLook(Mi_RAISED_BORDER_LOOK);
		button.setElementSizing(Mi_EXPAND_TO_FILL);

		button.appendActionHandler(this, Mi_SELECTED_ACTION);
		button.appendActionHandler(this, Mi_DESELECTED_ACTION);
		box.setVisible(false);
		appendPart(button);
		appendPart(box);
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
	public static	MiExpandoBox	create()
		{
		if (prototype == null)
			return(new MiExpandoBox());

		return((MiExpandoBox )prototype.deepCopy());
		}
	public		MiPart		getBox()
		{
		return(box);
		}
	public		MiToggleButton	getButton()
		{
		return(button);
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
	public static	void		setPrototype(MiExpandoBox p)
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
	public static	MiExpandoBox	getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_EXPANDOBOX_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_EXPANDOBOX_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiExpandoBox )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
		
	public		void		setValue(String value)
		{
		button.setValue(value);
		}
	public		String		getValue()
		{
		return(button.getValue());
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiExpandoBox obj 	= (MiExpandoBox )source;
		button			= (MiToggleButton )getPart(0);
		box			= getPart(1);
		buttonLayout		= (MiRowLayout )button.getLabel();
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_SELECTED_ACTION))
			{
			box.setVisible(true);
			}
		if (action.hasActionType(Mi_DESELECTED_ACTION))
			{
			box.setVisible(false);
			}
		return(true);
		}
	}

