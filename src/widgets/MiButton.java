
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
 * This abstract class is the base class for all sorts of 
 * button widgets.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiButton extends MiLabel implements MiiCommandHandler, MiiCommandNames
	{
	public static final String	Mi_FOCUS_LABEL_PROPERTY_NAME		= "focusLabel";
	public static final String	Mi_SELECTED_LABEL_PROPERTY_NAME		= "selectedLabel";
	public static final String	Mi_INSENSITIVE_LABEL_PROPERTY_NAME	= "insensitiveLabel";
	public static final String	Mi_LABEL_PROPERTY_NAME			= "label";

	private		MiPart		focusLabel;
	private		MiPart		selectedLabel;
	private		MiPart		normalLabel;
	private	 	MiPart		insensitiveLabel;



					/**------------------------------------------------------
	 				 * Constructs a new MiButton. 
					 *------------------------------------------------------*/
	public				MiButton()
		{
		super();
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiButton with the given text label.
					 *------------------------------------------------------*/
	public				MiButton(String text)
		{
		super(text);
		text = MiSystem.getProperty(text, text);
		if ((text.indexOf('&') != -1) || (text.indexOf('\t') != -1))
			setShortCutAndLabel(text);
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiButton with the given graphics label.
					 *------------------------------------------------------*/
	public				MiButton(MiPart obj)
		{
		super(obj);
		setup();
		}


					/**------------------------------------------------------
	 				 * Set the value of this widget, which is the label, to 
					 * the given text. The text is parsed in order to 
					 * assign any short cuts specified in the label.
					 * The given text is of form: 
					 * 	labe&l\tCtrl+C
					 * where the optional '&' indicates that the character
					 * following is to be a short cut (when widget is visible)
					 * and the '\t' indicates the following event is to be a
					 * short cut.
	 				 * @param text 		the new label
					 *------------------------------------------------------*/
	public		void		setValue(String text)
		{
		text = MiSystem.getProperty(text, text);
		if ((text.indexOf('&') != -1) || (text.indexOf('\t') != -1))
			setShortCutAndLabel(text);
		else
			super.setValue(text);
		}
		// FIX: need a method to remove short cuts
					/**------------------------------------------------------
	 				 * Add a short cut to this button. The end-user can use
					 * this short cut to select this button and thereby
					 * invoke any this button's action handlers.
	 				 * @param event 	the short cut
					 *------------------------------------------------------*/
	public		void		appendShortCut(MiEvent event)
		{
		appendEventHandler(new MiShortCutHandler(event, this, Mi_EXECUTE_SHORT_CUT_COMMAND_NAME));
		}
					/**------------------------------------------------------
	 				 * Sets the label of this widget.
	 				 * @param obj 		the new label
					 *------------------------------------------------------*/
	public		void		setLabel(MiPart obj)
		{
		super.setLabel(obj);
		}
					/**------------------------------------------------------
	 				 * Sets the label to be displayed when this button is 
					 * sensitive, not selected and with neither mouse or 
					 * keyboard focus.
	 				 * @param label 	the normal-state label
					 *------------------------------------------------------*/
	public		void		setNormalLabel(String label)
		{
		normalLabel = new MiText(label);
		normalLabel.setAttributes(getAttributes());
		synchonizeLabelToState();
		}
					/**------------------------------------------------------
	 				 * Sets the label to be displayed when this button is 
					 * sensitive, not selected and with neither mouse or 
					 * keyboard focus.
	 				 * @param label 	the normal-state label
					 *------------------------------------------------------*/
	public		void		setNormalLabel(MiPart label)
		{
		normalLabel = label;
		}
					/**------------------------------------------------------
	 				 * Gets the label to be displayed when this button is 
					 * sensitive, not selected and with neither mouse or 
					 * keyboard focus.
	 				 * @return 	 	the normal-state label
					 *------------------------------------------------------*/
	public		MiPart		getNormalLabel() 			
		{
		return(normalLabel);
		}
					/**------------------------------------------------------
	 				 * Sets the label to be displayed when this button is
					 * selected.
	 				 * @param label 	the selected-state label
					 *------------------------------------------------------*/
	public		void		setSelectedLabel(String label)
		{
		selectedLabel = new MiText(label);
		selectedLabel.setAttributes(getAttributes());
		synchonizeLabelToState();
		}
					/**------------------------------------------------------
	 				 * Sets the label to be displayed when this button is
					 * selected.
	 				 * @param label 	the selected-state label
					 *------------------------------------------------------*/
	public		void		setSelectedLabel(MiPart label) 		
		{
		selectedLabel = label;
		}
					/**------------------------------------------------------
	 				 * Gets the label to be displayed when this button is
					 * selected.
	 				 * @return 	 	the selected-state label
					 *------------------------------------------------------*/
	public		MiPart		getSelectedLabel() 			
		{
		return(selectedLabel);
		}
					/**------------------------------------------------------
	 				 * Sets the label to be displayed when this button is not
					 * sensitive.
	 				 * @param label 	the insensitive-state label
					 *------------------------------------------------------*/
	public		void		setInsensitiveLabel(String label)
		{
		insensitiveLabel = new MiText(label);
		insensitiveLabel.setAttributes(getAttributes());
		synchonizeLabelToState();
		}
					/**------------------------------------------------------
	 				 * Sets the label to be displayed when this button is not
					 * sensitive.
	 				 * @param label 	the insensitive-state label
					 *------------------------------------------------------*/
	public		void		setInsensitiveLabel(MiPart label) 	
		{
		insensitiveLabel = label;
		}
					/**------------------------------------------------------
	 				 * Gets the label to be displayed when this button is not
					 * sensitive.
	 				 * @return 	 	the insensitive-state label
					 *------------------------------------------------------*/
	public		MiPart		getInsensitiveLabel()		
		{
		return(insensitiveLabel);
		}
					/**------------------------------------------------------
	 				 * Sets the label to be displayed when this button has
					 * keyboard and or mouse focus.
	 				 * @param label 	the focus-state label
					 *------------------------------------------------------*/
	public		void		setFocusLabel(String label)
		{
		focusLabel = new MiText(label);
		focusLabel.setAttributes(getAttributes());
		synchonizeLabelToState();
		}
					/**------------------------------------------------------
	 				 * Sets the label to be displayed when this button has
					 * keyboard and or mouse focus.
	 				 * @param label 	the focus-state label
					 *------------------------------------------------------*/
	public		void		setFocusLabel(MiPart label)		
		{
		focusLabel = label;
		}
					/**------------------------------------------------------
	 				 * Gets the label to be displayed when this button has
					 * keyboard and or mouse focus.
	 				 * @return 	 	the focus-state label
					 *------------------------------------------------------*/
	public		MiPart		getFocusLabel() 			
		{
		return(focusLabel);
		}

					/**------------------------------------------------------
	 				 * Sets the button's selected state. Updates the label to
					 * the corresponding special appearance, if any.
	 				 * @param flag 		true if this is selected
	 				 * @overrides 		MiPart#select
					 *------------------------------------------------------*/
	public		void		select(boolean flag)
		{
		if (isSelected() == flag)
			return;

		super.select(flag);
		synchonizeLabelToState();
		if (flag)
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}

					/**------------------------------------------------------
	 				 * Sets the buttons mouse focus state. Updates the label
					 * to the corresponding special appearance, if any.
	 				 * @param flag 		true if this is selected
	 				 * @overrides 		MiPart#setMouseFocus
					 *------------------------------------------------------*/
	public		void		setMouseFocus(boolean flag)
		{
		super.setMouseFocus(flag);
		synchonizeLabelToState();
		}
					/**------------------------------------------------------
	 				 * Sets the buttons keyboard focus state. Updates the label
					 * to the corresponding special appearance, if any.
	 				 * @param flag 		true if this is selected
	 				 * @overrides 		MiPart#setKeyboardFocus
					 *------------------------------------------------------*/
	public		void		setKeyboardFocus(boolean flag)
		{
		super.setKeyboardFocus(flag);
		synchonizeLabelToState();
		}
					/**------------------------------------------------------
	 				 * Executes/selects this button. This is called when the
					 * end-user uses the keybaord or mouse to processCommand a short
					 * cut.
	 				 * @param arg 		the command argument
	 				 * @implements 		MiiCommandHandler
					 *------------------------------------------------------*/
	public		void		processCommand(String arg)
		{
		if (arg.equals(Mi_EXECUTE_SHORT_CUT_COMMAND_NAME))
			{
			select(true);
			dispatchAction(Mi_ACTIVATED_ACTION);
			}
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given
					 * value. If no such property is found then sets the attribute
					 * with the given name to the given value. Valid attribute 
					 * names are found in the MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (Utility.startsWithIgnoreCase(name, Mi_FOCUS_LABEL_PROPERTY_NAME))
			{
			if ((value == null) || (Mi_NULL_VALUE_NAME.equals(value)))
				{
				focusLabel = null;
				return;
				}

			if (focusLabel == null)
				focusLabel = new MiLabel();
			String attName = name.substring(Mi_FOCUS_LABEL_PROPERTY_NAME.length() + 1);
			focusLabel.setPropertyValue(attName, value);
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_SELECTED_LABEL_PROPERTY_NAME))
			{
			if ((value == null) || (Mi_NULL_VALUE_NAME.equals(value)))
				{
				selectedLabel = null;
				return;
				}

			if (selectedLabel == null)
				selectedLabel = new MiLabel();
			String attName = name.substring(Mi_SELECTED_LABEL_PROPERTY_NAME.length() + 1);
			selectedLabel.setPropertyValue(attName, value);
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_LABEL_PROPERTY_NAME))
			{
			if ((value == null) || (Mi_NULL_VALUE_NAME.equals(value)))
				{
				normalLabel = null;
				return;
				}

			String attName = name.substring(Mi_LABEL_PROPERTY_NAME.length() + 1);
			if (normalLabel != null)
				{
				normalLabel.setPropertyValue(attName, value);
				}
			else
				{
				setPropertyValue(attName, value);
				}
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_INSENSITIVE_LABEL_PROPERTY_NAME))
			{
			if ((value == null) || (Mi_NULL_VALUE_NAME.equals(value)))
				{
				insensitiveLabel = null;
				return;
				}

			if (insensitiveLabel == null)
				insensitiveLabel = new MiLabel();
			String attName = name.substring(Mi_INSENSITIVE_LABEL_PROPERTY_NAME.length() + 1);
			insensitiveLabel.setPropertyValue(attName, value);
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
		if (Utility.startsWithIgnoreCase(name, Mi_FOCUS_LABEL_PROPERTY_NAME))
			{
			MiPart tmp = focusLabel;
			if (tmp == null)
				tmp = new MiLabel();
			String attName = name.substring(Mi_FOCUS_LABEL_PROPERTY_NAME.length() + 1);
			return(tmp.getPropertyValue(attName));
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_SELECTED_LABEL_PROPERTY_NAME))
			{
			MiPart tmp = selectedLabel;
			if (tmp == null)
				tmp = new MiLabel();
			String attName = name.substring(Mi_SELECTED_LABEL_PROPERTY_NAME.length() + 1);
			return(tmp.getPropertyValue(attName));
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_LABEL_PROPERTY_NAME))
			{
			MiPart tmp = normalLabel;
			if (tmp == null)
				tmp = new MiLabel();
			String attName = name.substring(Mi_LABEL_PROPERTY_NAME.length() + 1);
			return(tmp.getPropertyValue(attName));
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_INSENSITIVE_LABEL_PROPERTY_NAME))
			{
			MiPart tmp = insensitiveLabel;
			if (tmp == null)
				tmp = new MiLabel();
			String attName = name.substring(Mi_INSENSITIVE_LABEL_PROPERTY_NAME.length() + 1);
			return(tmp.getPropertyValue(attName));
			}
		else
			{
			return(super.getPropertyValue(name));
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

		MiButton src = (MiButton )source;

		if (src.focusLabel != null)
			focusLabel = src.focusLabel.copy();
		else
			focusLabel = null;

		if (src.selectedLabel != null)
			selectedLabel = src.selectedLabel.copy();
		else
			selectedLabel = null;

		if (src.normalLabel != null)
			normalLabel = src.normalLabel.copy();
		else
			normalLabel = null;

		if (src.insensitiveLabel != null)
			insensitiveLabel = src.insensitiveLabel.copy();
		else
			insensitiveLabel = null;
		}

	//***************************************************************************************
	// Protected methods
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Assures that the correct state-dependent label is 
					 * displayed.
					 *------------------------------------------------------*/
	protected 	void		synchonizeLabelToState()
		{
		if (!isSensitive())
			{
			if ((insensitiveLabel != null) && (getLabel() != insensitiveLabel))
				setLabel(insensitiveLabel);
			}
		else if (isSelected())
			{
			if ((selectedLabel != null) && (getLabel() != selectedLabel))
				setLabel(selectedLabel);
			}
		else if ((!isSelected()) && (normalLabel != null))
			{
			if (getLabel() != normalLabel)
				setLabel(normalLabel);
			}
		else if (hasMouseFocus() || hasKeyboardFocus())
			{
			if ((focusLabel != null) && (getLabel() != focusLabel))
				setLabel(focusLabel);
			}
		}
					/**------------------------------------------------------
	 				 * Sets the sensitivity of the label graphics of this button.
					 * This does nothing if a special label has been
					 * assigned to represent the graphics when insensitive.
	 				 * @param flag 		true if this is to be sensitive
	 				 * @overrides 		MiLabel#setLabelSensivity
	 				 * @see 		#setInsensitiveLabel
					 *------------------------------------------------------*/
	protected	void		setLabelSensivity(boolean flag)
		{
		if (insensitiveLabel == null)
			super.setLabelSensivity(flag);
		}

	//***************************************************************************************
	// Private methods
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Initialize this button.
					 *------------------------------------------------------*/
	private		void		setup()
		{
		setDisplaysFocusBorder(true);
		setAcceptingKeyboardFocus(true);
		setAcceptingMouseFocus(true);
		setBorderLook(Mi_RAISED_BORDER_LOOK);
		setSelectedBorderLook(Mi_INDENTED_BORDER_LOOK);

		if (getClass().getName().equals("com.swfm.mica.MiButton"))
			{
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			}
		}
					/**------------------------------------------------------
	 				 * Sets the label of and assigns any short cuts to this 
					 * button, as specified by the given text.
					 * @param text		the new label
					 *------------------------------------------------------*/
	private		void		setShortCutAndLabel(String text)
		{
		// FIX: remove any previous shortCuts
		MiText textObj = MiUtility.assignShortCutFromLabel(this, this, text);
		textObj.setName(getLabel() != null ? getLabel().getName() : null);
		setLabel(textObj);
		}
	}

