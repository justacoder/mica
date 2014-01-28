
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
import java.util.Enumeration; 
import java.util.Properties; 
import com.swfm.mica.util.Strings; 

/**----------------------------------------------------------------------------------------------
 * This is the super class of all widgets in the Mica User Interface
 * Toolkit.
 *
 * When isAutoAttributesEnabled() is true, each MiWidget has a 
 * seperate instance of MiAttributes for each of 6 possible states 
 * the widget might be in:
 *
 *	normal
 *	selected
 *	enterKeyFocus
 *	keyboardFocus
 *	mouseFocus
 *	inSensitive		
 *
 * The widgets gets it's resultant attributes from (1) overriding from
 * the normal attributes and then overriding from the attributes assigned
 * to the current states of the widget in the above order.
 *
 * For example: if the widget is not sensitive and has mouse focus, then
 * to the current widget attributes are applied: 
 * 
 *   atts.overrideFrom(normalAttributes) 
 *   atts.overrideFrom(mouseFocusAttributes) 
 *   atts.overrideFrom(inSensitiveAttributes) 
 * 
 * @see		MiToolkit
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiWidget extends MiVisibleContainer
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private static	MiToolkit		toolkit 				= new MiToolkit();

	private 	boolean			isDesignTime;
	private 	boolean			autoStateAttributeUpdatesEnabled 	= true;
	private		MiRadioStateEnforcer	radioStateEnforcer;

	private		MiAttributes		normalAttributes;
	private		MiAttributes		inSensitiveAttributes;
	private		MiAttributes		selectedAttributes;
	private		MiAttributes		keyboardFocusAttributes;
	private		MiAttributes		enterKeyFocusAttributes;
	private		MiAttributes		mouseFocusAttributes;


					/**------------------------------------------------------
	 				 * Constructs a new MiWidget. Attributes are assigned to
					 * this widget by the MiToolkit. These attributes are:
					 *   MiToolkit.widgetNormalAttributes
        				 *   MiToolkit.widgetMouseFocusAttributes
        				 *   MiToolkit.widgetKeyboardFocusAttributes
        				 *   MiToolkit.widgetEnterKeyFocusAttributes
        				 *   MiToolkit.widgetSelectedAttributes
        				 *   MiToolkit.widgetInSensitiveAttributes
					 *------------------------------------------------------*/
	public				MiWidget()
		{
		toolkit.applyStandardWidgetAttributesToWidget(this);
		}


	//***************************************************************************************
	// Get and Set state attributes as a group
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the set of attributes that this widget has.
					 * @return		the set of attributes
					 *------------------------------------------------------*/
	public		MiWidgetAttributes getWidgetAttributes()
		{
		MiWidgetAttributes atts = new MiWidgetAttributes();
		atts.normalAttributes		= normalAttributes;
		atts.inSensitiveAttributes	= inSensitiveAttributes;
		atts.selectedAttributes		= selectedAttributes;
		atts.keyboardFocusAttributes	= keyboardFocusAttributes;
		atts.enterKeyFocusAttributes	= enterKeyFocusAttributes;
		atts.mouseFocusAttributes	= mouseFocusAttributes;

		return(atts);
		}
					/**------------------------------------------------------
	 				 * Set the set of attributes that this widget has.
					 * @param atts		the set of attributes
					 *------------------------------------------------------*/
	public		void		setWidgetAttributes(MiWidgetAttributes atts)
		{
		normalAttributes	= atts.normalAttributes;
		inSensitiveAttributes	= atts.inSensitiveAttributes;
		selectedAttributes	= atts.selectedAttributes;
		keyboardFocusAttributes	= atts.keyboardFocusAttributes;
		enterKeyFocusAttributes	= atts.enterKeyFocusAttributes;
		mouseFocusAttributes	= atts.mouseFocusAttributes;

		super.setAttributes((MiAttributes )getAttributes().initializeAsOverrideAttributes(false));
		updateAttributes();
		}
					/**------------------------------------------------------
	 				 * Sets the set of attributes for this widget by assigning
					 * the single MiAttributes to be used for all of the states
					 * this widget can have.
					 * @param atts		the attributes
					 *------------------------------------------------------*/
	public		void		setWidgetAttributes(MiAttributes atts)
		{
		normalAttributes	= atts;
		inSensitiveAttributes	= atts;
		selectedAttributes	= atts;
		keyboardFocusAttributes	= atts;
		enterKeyFocusAttributes	= atts;
		mouseFocusAttributes	= atts;

		super.setAttributes((MiAttributes )getAttributes().initializeAsOverrideAttributes(false));
		updateAttributes();
		}

	//***************************************************************************************
	// Get and Set state attributes individually
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the attributes that this MiWidget uses when it is
					 * in the normal state.
					 * @return 		the normal state attributes
					 *------------------------------------------------------*/
	public		MiAttributes	getNormalAttributes()
		{
		return(normalAttributes);
		}
					/**------------------------------------------------------
					 * Gets the attributes that this MiWidget uses when it is
					 * sensitive.
					 * @return 		the insensitive attributes
					 *------------------------------------------------------*/
	public		MiAttributes	getInSensitiveAttributes()
		{
		return(inSensitiveAttributes);
		}
					/**------------------------------------------------------
					 * Gets the attributes that this MiWidget uses when it is
					 * selected.
					 * @return 		the selected attributes
					 *------------------------------------------------------*/
	public		MiAttributes	getSelectedAttributes()
		{
		return(selectedAttributes);
		}
					/**------------------------------------------------------
					 * Gets the attributes that this MiWidget uses when it
					 * has the keyboard focus.
					 * @return 		the keyboard focus attributes
					 *------------------------------------------------------*/
	public		MiAttributes	getKeyboardFocusAttributes()
		{
		return(keyboardFocusAttributes);
		}
					/**------------------------------------------------------
					 * Gets the attributes that this MiWidget uses when it
					 * has the enter key focus.
					 * @return 		the enter key focus attributes
					 *------------------------------------------------------*/
	public		MiAttributes	getEnterKeyFocusAttributes()
		{
		return(enterKeyFocusAttributes);
		}
					/**------------------------------------------------------
					 * Gets the attributes that this MiWidget uses when it
					 * has the mouse focus.
					 * @return 		the mouse focus attributes
					 *------------------------------------------------------*/
	public		MiAttributes	getMouseFocusAttributes()
		{
		return(mouseFocusAttributes);
		}

					/**------------------------------------------------------
					 * Sets the attributes that this MiWidget uses when it
					 * is in the normal state.
					 * @param atts 		the normal state attributes
					 *------------------------------------------------------*/
	public		void		setNormalAttributes(MiAttributes atts)
		{
		normalAttributes = atts;
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Sets the attributes that this MiWidget uses when it
					 * is in sensitive.
					 * @param atts 		the insensitive attributes
					 *------------------------------------------------------*/
	public		void		setInSensitiveAttributes(MiAttributes atts)
		{
		inSensitiveAttributes = atts;
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Sets the attributes that this MiWidget uses when it
					 * is selected.
					 * @param atts 		the selected attributes
					 *------------------------------------------------------*/
	public		void		setSelectedAttributes(MiAttributes atts)
		{
		selectedAttributes = atts;
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Sets the attributes that this MiWidget uses when it
					 * has the keyboard focus.
					 * @param atts 		the keyboard focus attributes
					 *------------------------------------------------------*/
	public		void		setKeyboardFocusAttributes(MiAttributes atts)
		{
		keyboardFocusAttributes = atts;
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Sets the attributes that this MiWidget uses when it
					 * has the enter key focus.
					 * @param atts 		the enter key attributes
					 *------------------------------------------------------*/
	public		void		setEnterKeyFocusAttributes(MiAttributes atts)
		{
		enterKeyFocusAttributes = atts;
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Sets the attributes that this MiWidget uses when it
					 * has the mouse focus.
					 * @param atts 		the mouse focus attributes
					 *------------------------------------------------------*/
	public		void		setMouseFocusAttributes(MiAttributes atts)
		{
		mouseFocusAttributes = atts;
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Get the widget toolkit.
					 * @return		the global toolkit
					 *------------------------------------------------------*/
	public static	MiToolkit	getToolkit()
		{
		return(toolkit);
		}
					/**------------------------------------------------------
					 * Get whether this widget is being designed and not 
					 * running.
					 * @return		true if it is not runtime
					 *------------------------------------------------------*/
	public		boolean		isDesignTime()
		{
		return(isDesignTime);
		}
					/**------------------------------------------------------
					 * Get whether this widget has it's attributes modified
					 * automatically when it changes state.
					 * @return		true if attributes are state dependant
					 *------------------------------------------------------*/
	public		boolean		isAutoAttributesEnabled()
		{
		return(autoStateAttributeUpdatesEnabled);
		}
					/**------------------------------------------------------
					 * Set whether this widget has it's attributes modified
					 * automatically when it changes state.
					 * @param flag		true if attributes are state dependant
					 *------------------------------------------------------*/
	public		void		setAutoAttributesEnabled(boolean flag)
		{
		autoStateAttributeUpdatesEnabled = flag;
		updateAttributes();
		}


	//***************************************************************************************
	// Convience attribute routines
	//***************************************************************************************

	//***************************************************************************************
	// Color
	//***************************************************************************************

					/**------------------------------------------------------
					 * Set the foreground color the widget is to have when it 
					 * is in the normal state.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setNormalColor(Color c)
		{
		normalAttributes = normalAttributes.setColor(c);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the foreground color the widget is to have when it 
					 * is insensitive.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setInSensitiveColor(Color c)
		{
		inSensitiveAttributes = inSensitiveAttributes.setColor(c);
		if (!isSensitive())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the foreground color the widget is to have when it 
					 * is selected.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setSelectedColor(Color c)
		{
		selectedAttributes = selectedAttributes.setColor(c);
		if (isSelected())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the foreground color the widget is to have when it 
					 * has the keyboard focus.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setKeyboardFocusColor(Color c)
		{
		keyboardFocusAttributes = keyboardFocusAttributes.setColor(c);
		if (hasKeyboardFocus())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the foreground color the widget is to have when it 
					 * has the enter key focus.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setEnterKeyFocusColor(Color c)
		{
		enterKeyFocusAttributes = enterKeyFocusAttributes.setColor(c);
		if (hasEnterKeyFocus())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the foreground color the widget is to have when it 
					 * has the mouse focus.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setMouseFocusColor(Color c)
		{
		mouseFocusAttributes = mouseFocusAttributes.setColor(c);
		if (hasMouseFocus())
			updateAttributes();
		}

	//***************************************************************************************
	// Background Color
	//***************************************************************************************

					/**------------------------------------------------------
					 * Set the background color the widget is to have when it 
					 * is in the normal state.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setNormalBackgroundColor(Color c)
		{
		normalAttributes = normalAttributes.setBackgroundColor(c);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the background color the widget is to have when it 
					 * is insensitive.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setInSensitiveBackgroundColor(Color c)
		{
		inSensitiveAttributes = inSensitiveAttributes.setBackgroundColor(c);
		if (!isSensitive())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the background color the widget is to have when it 
					 * is selected.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setSelectedBackgroundColor(Color c)
		{
		selectedAttributes = selectedAttributes.setBackgroundColor(c);
		if (isSelected())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the background color the widget is to have when it 
					 * has the keyboard focus.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setKeyboardFocusBackgroundColor(Color c)
		{
		keyboardFocusAttributes = keyboardFocusAttributes.setBackgroundColor(c);
		if (hasKeyboardFocus())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the background color the widget is to have when it 
					 * has the enter key focus.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setEnterKeyFocusBackgroundColor(Color c)
		{
		enterKeyFocusAttributes = enterKeyFocusAttributes.setBackgroundColor(c);
		if (hasEnterKeyFocus())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the background color the widget is to have when it 
					 * has the mouse focus.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setMouseFocusBackgroundColor(Color c)
		{
		mouseFocusAttributes = mouseFocusAttributes.setBackgroundColor(c);
		if (hasMouseFocus())
			updateAttributes();
		}

	//***************************************************************************************
	// Border Look
	//***************************************************************************************

					/**------------------------------------------------------
					 * Set the border look the widget is to have when it 
					 * is in the normal state.
					 * @param look		the border look
					 *------------------------------------------------------*/
	public		void		setNormalBorderLook(int look)
		{
		normalAttributes = normalAttributes.setBorderLook(look);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the border look the widget is to have when it 
					 * is in the normal state.
					 * @param look		the border look
					 *------------------------------------------------------*/
	public		void		setInSensitiveBorderLook(int look)
		{
		inSensitiveAttributes = inSensitiveAttributes.setBorderLook(look);
		if (!isSensitive())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the border look the widget is to have when it 
					 * is selected.
					 * @param look		the border look
					 *------------------------------------------------------*/
	public		void		setSelectedBorderLook(int look)
		{
		selectedAttributes = selectedAttributes.setBorderLook(look);
		if (isSelected())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the border look the widget is to have when it 
					 * has the keyboard focus.
					 * @param look		the border look
					 *------------------------------------------------------*/
	public		void		setKeyboardFocusBorderLook(int look)
		{
		keyboardFocusAttributes = keyboardFocusAttributes.setBorderLook(look);
		if (hasKeyboardFocus())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the border look the widget is to have when it 
					 * has the enter key focus.
					 * @param look		the border look
					 *------------------------------------------------------*/
	public		void		setEnterKeyFocusBorderLook(int look)
		{
		enterKeyFocusAttributes = enterKeyFocusAttributes.setBorderLook(look);
		if (hasEnterKeyFocus())
			updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the border look the widget is to have when it 
					 * has the mouse focus.
					 * @param look		the border look
					 *------------------------------------------------------*/
	public		void		setMouseFocusBorderLook(int look)
		{
		mouseFocusAttributes = mouseFocusAttributes.setBorderLook(look);
		if (hasMouseFocus())
			updateAttributes();
		}

	//***************************************************************************************
	// Status help msgs
	//***************************************************************************************

					/**------------------------------------------------------
					 * Set the status bar message the widget is to have when it 
					 * is in the normal state.
					 * @param msg		the message or null
					 *------------------------------------------------------*/
	public		void		setNormalStatusHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		normalAttributes = normalAttributes.setStatusHelpMessage(msg);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the status bar message the widget is to have when it 
					 * is insensitive.
					 * @param msg		the message or null
					 *------------------------------------------------------*/
	public		void		setInSensitiveStatusHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		inSensitiveAttributes = inSensitiveAttributes.setStatusHelpMessage(msg);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the status bar message the widget is to have when it 
					 * is selected.
					 * @param msg		the message or null
					 *------------------------------------------------------*/
	public		void		setSelectedStatusHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		selectedAttributes = selectedAttributes.setStatusHelpMessage(msg);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the status bar message the widget is to have when it 
					 * has the keyboard focus.
					 * @param msg		the message or null
					 *------------------------------------------------------*/
	public		void		setKeyboardFocusStatusHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		keyboardFocusAttributes = keyboardFocusAttributes.setStatusHelpMessage(msg);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the status bar message the widget is to have when it 
					 * has the enter key focus.
					 * @param msg		the message or null
					 *------------------------------------------------------*/
	public		void		setEnterKeyFocusStatusHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		enterKeyFocusAttributes = enterKeyFocusAttributes.setStatusHelpMessage(msg);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Set the status bar message the widget is to have when it 
					 * has the mouse focus.
					 * @param msg		the message or null
					 *------------------------------------------------------*/
	public		void		setMouseFocusStatusHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		mouseFocusAttributes = mouseFocusAttributes.setStatusHelpMessage(msg);
		updateAttributes();
		}


	//***************************************************************************************
	// Temp set attributes, implement standard attribute api
	//***************************************************************************************

					/**------------------------------------------------------
					 * Set the foreground color the widget is to have now until
					 * it changes state again.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setCurrentColor(Color c)
		{
		MiAttributes atts = getAttributes().setBackgroundColor(c);
		atts = (MiAttributes )atts.setIsInheritedAttribute(Mi_COLOR, true);
		super.setAttributes(atts);
		}
					/**------------------------------------------------------
					 * Set the background color the widget is to have now until
					 * it changes state again.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setCurrentBackgroundColor(Color c)
		{
		MiAttributes atts = getAttributes().setBackgroundColor(c);
		atts = (MiAttributes )atts.setIsInheritedAttribute(Mi_BACKGROUND_COLOR, true);
		super.setAttributes(atts);
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiWidget obj 				= (MiWidget )source;
		isDesignTime				= obj.isDesignTime;
		autoStateAttributeUpdatesEnabled	= obj.autoStateAttributeUpdatesEnabled;
		normalAttributes			= obj.normalAttributes;
		inSensitiveAttributes			= obj.inSensitiveAttributes;
		selectedAttributes			= obj.selectedAttributes;
		keyboardFocusAttributes			= obj.keyboardFocusAttributes;
		enterKeyFocusAttributes			= obj.enterKeyFocusAttributes;
		mouseFocusAttributes			= obj.mouseFocusAttributes;
		}


	//***************************************************************************************
	// Attribute access routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Get the foreground color the widget is to have when it 
					 * is in the normal state.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getNormalColor() 		
		{ return(normalAttributes.getColor()); 				}
					/**------------------------------------------------------
					 * Get the foreground color the widget is to have when it 
					 * is insensitive.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getInSensitiveColor()
		{ return(inSensitiveAttributes.getColor()); 			}
					/**------------------------------------------------------
					 * Get the foreground color the widget is to have when it 
					 * is selected.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getSelectedColor()
		{ return(selectedAttributes.getColor()); 			}
					/**------------------------------------------------------
					 * Get the foreground color the widget is to have when it 
					 * has the keyboard focus.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getKeyboardFocusColor()
		{ return(keyboardFocusAttributes.getColor()); 			}
					/**------------------------------------------------------
					 * Get the foreground color the widget is to have when it 
					 * has the enter key focus.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getEnterKeyFocusColor() 		
		{ return(enterKeyFocusAttributes.getColor()); 			}
					/**------------------------------------------------------
					 * Get the foreground color the widget is to have when it 
					 * has the mouse focus.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getMouseFocusColor() 		
		{ return(mouseFocusAttributes.getColor()); 			}


					/**------------------------------------------------------
					 * Get the background color the widget is to have when it 
					 * is in the normal state.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getNormalBackgroundColor() 		
		{ return(normalAttributes.getBackgroundColor()); 		}
					/**------------------------------------------------------
					 * Get the background color the widget is to have when it 
					 * is insensitive.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getInSensitiveBackgroundColor() 		
		{ return(inSensitiveAttributes.getBackgroundColor()); 		}
					/**------------------------------------------------------
					 * Get the background color the widget is to have when it 
					 * is selected.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getSelectedBackgroundColor() 		
		{ return(selectedAttributes.getBackgroundColor()); 		}
					/**------------------------------------------------------
					 * Get the background color the widget is to have when it 
					 * has the keyboard focus.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getKeyboardFocusBackgroundColor() 		
		{ return(keyboardFocusAttributes.getBackgroundColor()); 	}
					/**------------------------------------------------------
					 * Get the background color the widget is to have when it 
					 * has the enter key focus.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getEnterKeyFocusBackgroundColor() 		
		{ return(enterKeyFocusAttributes.getBackgroundColor()); 	}
					/**------------------------------------------------------
					 * Get the background color the widget is to have when it 
					 * has the mouse focus.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getMouseFocusBackgroundColor()
		{ return(mouseFocusAttributes.getBackgroundColor()); 		}


					/**------------------------------------------------------
					 * Get the border look the widget is to have when it 
					 * is in the normal state.
					 * @return		the border look
					 *------------------------------------------------------*/
	public		int		getNormalBorderLook()
		{ return(normalAttributes.getBorderLook()); 			}
					/**------------------------------------------------------
					 * Get the border look the widget is to have when it 
					 * is in the normal state.
					 * @return		the border look
					 *------------------------------------------------------*/
	public		int		getInSensitiveBorderLook()
		{ return(inSensitiveAttributes.getBorderLook()); 		}
					/**------------------------------------------------------
					 * Get the border look the widget is to have when it 
					 * is selected.
					 * @return		the border look
					 *------------------------------------------------------*/
	public		int		getSelectedBorderLook() 		
		{ return(selectedAttributes.getBorderLook()); 			}
					/**------------------------------------------------------
					 * Get the border look the widget is to have when it 
					 * has the keyboard focus.
					 * @return		the border look
					 *------------------------------------------------------*/
	public		int		getKeyboardFocusBorderLook()
		{ return(keyboardFocusAttributes.getBorderLook()); 		}
					/**------------------------------------------------------
					 * Get the border look the widget is to have when it 
					 * has the enter key focus.
					 * @return		the border look
					 *------------------------------------------------------*/
	public		int		getEnterKeyFocusBorderLook()
		{ return(enterKeyFocusAttributes.getBorderLook()); 		}
					/**------------------------------------------------------
					 * Get the border look the widget is to have when it 
					 * has the mouse focus.
					 * @return		the border look
					 *------------------------------------------------------*/
	public		int		getMouseFocusBorderLook() 		
		{ return(mouseFocusAttributes.getBorderLook()); 		}


					/**------------------------------------------------------
					 * Get the status bar message the widget is to have when it 
					 * is in the normal state.
					 * @return		the message or null
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getNormalStatusHelp()
		{ return(normalAttributes.getStatusHelp()); 			}
					/**------------------------------------------------------
					 * Get the status bar message the widget is to have when it 
					 * is insensitive.
					 * @return		the message or null
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getInSensitiveStatusHelp()
		{ return(inSensitiveAttributes.getStatusHelp()); 		}
					/**------------------------------------------------------
					 * Get the status bar message the widget is to have when it 
					 * is selected.
					 * @return		the message or null
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getSelectedStatusHelp()
		{ return(selectedAttributes.getStatusHelp()); 			}
					/**------------------------------------------------------
					 * Get the status bar message the widget is to have when it 
					 * has the keyboard focus.
					 * @return		the message or null
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getKeyboardFocusStatusHelp()
		{ return(keyboardFocusAttributes.getStatusHelp()); 		}
					/**------------------------------------------------------
					 * Get the status bar message the widget is to have when it 
					 * has the enter key focus.
					 * @return		the message or null
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getEnterKeyFocusStatusHelp()
		{ return(enterKeyFocusAttributes.getStatusHelp()); 		}
					/**------------------------------------------------------
					 * Get the status bar message the widget is to have when it 
					 * has the mouse focus.
					 * @return		the message or null
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getMouseFocusStatusHelp()
		{ return(mouseFocusAttributes.getStatusHelp()); 		}


	//***************************************************************************************
	// State transition routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Update the attributes of this widget while setting it's
					 * sensitivity.
					 * @param flag		true if sensitive
					 * @overrides		MiPart#setSensitive
					 *------------------------------------------------------*/
	public		void		setSensitive(boolean flag) 		
		{
		if (flag == isSensitive())
			return;

		super.setSensitive(flag);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Update the attributes of this widget while setting it's
					 * selected state. Before the selection state is changed
					 * the radioStateEnforcer, if any, is checked to verify
					 * the change.
					 * @param flag		true if selected
					 * @overrides		MiPart#select
					 * @see			MiWidget#setRadioStateEnforcer
					 *------------------------------------------------------*/
	public		void		select(boolean flag)
		{
		if (flag == isSelected())
			return;

		if (radioStateEnforcer != null)
			{
			if (!radioStateEnforcer.verifyProposedStateChange(this, flag))
				return;
			}

		super.select(flag);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Update the attributes of this widget while setting it's
					 * keyboard focus state. 
					 * @param flag		true if to have keyboard focus
					 * @overrides		MiPart#setKeyboardFocus
					 *------------------------------------------------------*/
	public		void		setKeyboardFocus(boolean flag)
		{
		if (flag == hasKeyboardFocus())
			return;

		super.setKeyboardFocus(flag);
		if (flag == hasKeyboardFocus())
			{
			updateAttributes();
			}
		}
					/**------------------------------------------------------
					 * Update the attributes of this widget while setting it's
					 * enter key focus state. 
					 * @param flag		true if to have enter key focus
					 * @overrides		MiPart#setEnterKeyFocus
					 *------------------------------------------------------*/
	public		void		setEnterKeyFocus(boolean flag)
		{
		if (flag == hasEnterKeyFocus())
			return;

		super.setEnterKeyFocus(flag);
		if (flag == hasEnterKeyFocus())
			{
			updateAttributes();
			}
		}
					/**------------------------------------------------------
					 * Update the attributes of this widget while setting it's
					 * mouse focus state. 
					 * @param flag		true if to have mouse focus
					 * @overrides		MiPart#setMouseFocus
					 *------------------------------------------------------*/
	public		void		setMouseFocus(boolean flag)
		{
		if (flag == hasMouseFocus())
			return;

		super.setMouseFocus(flag);
		if (flag == hasMouseFocus())
			{
			updateAttributes();
			}
		}
					/**------------------------------------------------------
					 * Update the attributes of this widget to reflect the
					 * it's current state. The way this works is:
					 *	1. any change made to the current attributes is now
					 *	made to the normalAttributes
					 *	2. starting with the normalAttributes:
					 *	depending of the current state (selected, 
					 *	enter-key-focus, keyboard-focus, mouse-focus,
					 *	and sensitivity, the corresponding state-attributes
					 *	are applied.
					 *	3. the attributes are then cleared of any override
					 *	flags so that any changes made to the attributes
					 *	will be copied to the normalAttributes next time
					 *	this method is executed (which is every time the
					 *	state changes).
					 *	4. the attributes are then applied to this MiPart.
					 *------------------------------------------------------*/
	protected	void		updateAttributes()
		{
		if (!autoStateAttributeUpdatesEnabled)
			return;

		normalAttributes = (MiAttributes )normalAttributes.overrideFromPermanent2(getAttributes());

		MiAttributes atts = normalAttributes;

		if (isSelected())
			atts = atts.overrideFrom(selectedAttributes);
		if (hasEnterKeyFocus())
			atts = atts.overrideFrom(enterKeyFocusAttributes);
		if (hasKeyboardFocus())
			atts = atts.overrideFrom(keyboardFocusAttributes);
		if (hasMouseFocus())
			atts = atts.overrideFrom(mouseFocusAttributes);
		if (!isSensitive())
			atts = atts.overrideFrom(inSensitiveAttributes);

		atts = (MiAttributes )atts.initializeAsOverrideAttributes(false);
		super.setAttributes(atts);
		}

					/**------------------------------------------------------
					 * Sets the attributes the widget is to have in the
					 * normal state. So that when, say, setColor is called
					 * on a widget, this color will be used when the widget
					 * is in the 'normal' state.
					 * @param atts		the attributes
					 * @see			MiPart#setAttributes
					 * @overrides		MiPart#setAttributes
					 *------------------------------------------------------*/
	public		void		setAttributes(MiAttributes atts)
		{
		if (normalAttributes != null)
			normalAttributes = (MiAttributes )normalAttributes.overrideFromPermanent2(atts);
		super.setAttributes(atts);
		}

	//***************************************************************************************
	// Widget base-class routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Sets the value this widget displays. Override this, if 
					 * desired, as it implements the core functionality. 
					 * Most widgets do.
					 * @param value		the value in the form of a text
					 *			string
					 *------------------------------------------------------*/
	public		void		setValue(String value)
		{
		}
					/**------------------------------------------------------
					 * Gets the value this widget displays. Override this, if 
					 * desired, as it implements the core functionality. 
					 * Most widgets do.
					 * @return 		the value in the form of a text
					 *			string
					 *------------------------------------------------------*/
	public		String		getValue()
		{
		return("");
		}
					/**------------------------------------------------------
					 * Sets the contents (list of values) this widget displays.
					 * Override this, if desired, as it implements the core 
					 * functionality. Most major widgets do.
					 * @param contents	the contents in the form of a list
					 *			of text strings
					 *------------------------------------------------------*/
	public		void		setContents(Strings contents)
		{
		}
					/**------------------------------------------------------
					 * Gets the contents (list of values) this widget displays.
					 * Override this, if desired, as it implements the core 
					 * functionality. Most major widgets do.
					 * @return		the contents in the form of a list
					 *			of text strings
					 *------------------------------------------------------*/
	public		Strings		getContents()
		{
		return(new Strings());
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * This method supports the use of property names of form:
					 *   selected.backgroundColor
					 * in order to specify the values of attributes for this
					 * widget on a state by state basis.
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		MiWidgetAttributes atts = getWidgetAttributes();
		if (MiToolkit.setWidgetAttributeProperty(atts, name, value))
			{
			setWidgetAttributes(atts);
			updateAttributes();
			return;
			}

		if (name.equalsIgnoreCase(Mi_VALUE_NAME))
			{
			setValue(value);
			}
		else if (name.equalsIgnoreCase(Mi_CONTENTS_NAME))
			{
			setContents(new Strings(value));
			}
		else
			{
			super.setPropertyValue(name, value);
			if (normalAttributes.hasAttribute(name))
				{
				normalAttributes.setAttributeValue(name, value);
				}
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
		String value = MiToolkit.getWidgetAttributeProperty(getWidgetAttributes(), name);
		if (!MiiToolkit.Mi_NOT_A_VALID_PROPERTY_VALUE.equals(value))
			return(value);

		if (name.equalsIgnoreCase(Mi_VALUE_NAME))
			return(getValue());
		else if (name.equalsIgnoreCase(Mi_CONTENTS_NAME))
			return(getContents().getDelimitedStrings("\\n"));
		else 
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

		propertyDescriptions = new MiPropertyDescriptions("MiWidget", super.getPropertyDescriptions());
		propertyDescriptions.setDisplayName("Widget Properties");

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_VALUE_NAME, Mi_STRING_TYPE, ""));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_CONTENTS_NAME, Mi_STRING_TYPE, ""));

		return(propertyDescriptions);
		}

	public		void		setPropertyValues(Properties properties)
		{
		properties = (Properties )properties.clone();
		MiWidgetAttributes atts = getWidgetAttributes();
		properties = MiToolkit.setWidgetAttributeProperties(atts, properties);
		setWidgetAttributes(atts);
		// ---------------------------------------------------------------
		// Set properties that were not assigned to one of the attribute bundles.
		// ---------------------------------------------------------------
		super.setPropertyValues(properties);
		updateAttributes();
		}
					/**------------------------------------------------------
					 * Sets the radio state enforcer for this widget. Enforcers
					 * are shared between groups of widgets whose selection
					 * states affect each other.
					 * @param enforcer	the enforcer.
					 * @see			MiRadioStateEnforcer
					 *------------------------------------------------------*/
	public		void		setRadioStateEnforcer(MiRadioStateEnforcer enforcer)
		{
		radioStateEnforcer = enforcer;
		}
					/**------------------------------------------------------
					 * Gets the radio state enforcer for this widget. Enforcers
					 * are shared between groups of widgets whose selection
					 * states affect each other.
					 * @param enforcer	the enforcer.
					 * @see			MiRadioStateEnforcer
					 *------------------------------------------------------*/
	public		MiRadioStateEnforcer getRadioStateEnforcer()
		{
		return(radioStateEnforcer);
		}
					/**------------------------------------------------------
					 * Exports data as specified.
					 * @param format	specifies the format of the data
					 *			this MiPart is to export.
					 * @return		The exported data.
					 * @overrides		MiPart#doExport
					 *------------------------------------------------------*/
	public		Object 		doExport(String format)
		{
		if (format.equals(Mi_STRING_FORMAT))
			return(getValue());
		return(super.doExport(format));
		}
	}

