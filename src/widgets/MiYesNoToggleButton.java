
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
public class MiYesNoToggleButton extends MiToggleButton
	{
	public static final	String		Mi_YES_NO_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME 
							= "Mi_YES_NO_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME";
	private static		MiYesNoToggleButton	prototype;
	public static final 	int		Mi_SHOW_BOTH_YES_AND_NO			= 1;
	public static final 	int		Mi_SHOW_YES_AND_NO_AS_SEPARATE_BUTTONS	= 2;
	private			int		style;
	private			MiText		yesText;
	private			MiText		noText;
	private			MiButton	yesButton;
	private			MiButton	noButton;
	private			String		selectedTextColor			= "white";
	private			String		Mi_YES_COMMAND_NAME			= "Yes";
	private			String		Mi_NO_COMMAND_NAME			= "No";


	public				MiYesNoToggleButton()
		{
		this(Mi_SHOW_BOTH_YES_AND_NO + Mi_SHOW_YES_AND_NO_AS_SEPARATE_BUTTONS);
		}
	public				MiYesNoToggleButton(int style)
		{
		super();

		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiYesNoToggleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			setInsetMargins(0);
			setBorderLook(Mi_NONE);
			setSelectedBorderLook(Mi_NONE);
			setSelectedBackgroundColor(getNormalBackgroundColor());
			}

		yesText = new MiText(MiSystem.getProperty("Yes", "Yes"));
		noText = new MiText(MiSystem.getProperty("No", "No"));

		setStyle(style);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public		void		setStyle(int style)
		{	
		removeAllParts();
		this.style = style;
		if ((style & Mi_SHOW_BOTH_YES_AND_NO) != 0)
			{
			MiRowLayout rowLayout = new MiRowLayout();
			rowLayout.setAlleyHSpacing(0);
			rowLayout.setElementHJustification(Mi_JUSTIFIED);
			setLayout(rowLayout);
			if ((style & Mi_SHOW_YES_AND_NO_AS_SEPARATE_BUTTONS) != 0)
				{
				yesButton = new MiToggleButton(yesText);
				noButton = new MiToggleButton(noText);
				yesButton.setMargins(new MiMargins(0));
				noButton.setMargins(new MiMargins(0));
				yesButton.setInsetMargins(4);
				noButton.setInsetMargins(4);
				appendPart(yesButton);
				appendPart(noButton);
				MiRadioStateEnforcer radioStateEnforcer = new MiRadioStateEnforcer();
				radioStateEnforcer.setMinNumSelected(1);
				yesButton.setRadioStateEnforcer(radioStateEnforcer);
				noButton.setRadioStateEnforcer(radioStateEnforcer);
				yesButton.appendCommandHandler(this, Mi_YES_COMMAND_NAME, Mi_SELECTED_ACTION);
				noButton.appendCommandHandler(this, Mi_NO_COMMAND_NAME, Mi_SELECTED_ACTION);
				}
			else
				{
				appendPart(yesText);
				appendPart(new MiText("/"));
				appendPart(noText);
				}
			}
		else
			{
			setLabel(noText);
			}
		select(false);
		}

	public		void		invalidateLayout()
		{
		super.invalidateLayout();
		// Setting text to BOLD changes size and therefore causes relayouts so 
		// we do this to allow minor changes during font style changes.
		if (!getOutgoingInvalidLayoutNotificationsEnabled())
			{
			validateLayout();
			}
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
			return(new MiYesNoToggleButton());

		return((MiYesNoToggleButton )prototype.deepCopy());
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
	public static	void		setPrototype(MiYesNoToggleButton p)
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
					 * Mi_YES_NO_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_YES_NO_TOGGLEBUTTON_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiYesNoToggleButton )Utility.makeInstanceOfClass(prototypeClassName);
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

		MiYesNoToggleButton obj = (MiYesNoToggleButton )source;
		selectedTextColor	= obj.selectedTextColor;
		setStyle(obj.style);
		}
	public		void		select(boolean flag)
		{
		super.select(flag);
		if (isSelected())
			{
			if ((style & Mi_SHOW_BOTH_YES_AND_NO) != 0)
				{
				if ((style & Mi_SHOW_YES_AND_NO_AS_SEPARATE_BUTTONS) != 0)
					{
					yesButton.select(true);
					}
				yesText.setColor(selectedTextColor);
				noText.setColor(getColor());

				//setOutgoingInvalidLayoutNotificationsEnabled(false);
				//yesText.setFontBold(true);
				//noText.setFontBold(false);
				//setOutgoingInvalidLayoutNotificationsEnabled(true);
				}
			else
				{
				setLabel(yesText);
				}
			}
		else
			{
			if ((style & Mi_SHOW_BOTH_YES_AND_NO) != 0)
				{
				if ((style & Mi_SHOW_YES_AND_NO_AS_SEPARATE_BUTTONS) != 0)
					{
					noButton.select(true);
					}
				noText.setColor(selectedTextColor);
				yesText.setColor(getColor());
				//setOutgoingInvalidLayoutNotificationsEnabled(false);
				//noText.setFontBold(true);
				//yesText.setFontBold(false);
				//setOutgoingInvalidLayoutNotificationsEnabled(true);
				}
			else
				{
				setLabel(yesText);
				}
			}
		}
	public		void		processCommand(String command)
		{
		select(command.equalsIgnoreCase(Mi_YES_COMMAND_NAME));
		}
	}

