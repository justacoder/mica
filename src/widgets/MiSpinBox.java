
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSpinBox extends MiWidget implements MiiActionHandler, MiiCommandHandler
	{
	public final static 	String	Mi_INCREASE_COMMAND_NAME		= "Increase";
	public final static 	String	Mi_DECREASE_COMMAND_NAME		= "Decrease";
	public static final	String	Mi_SPINBOX_PROTOTYPE_CLASS_NAME 	= "Mi_SPINBOX_PROTOTYPE_CLASS_NAME";

	private static	MiSpinBox	prototype;
	private		MiTextField	textField;
	private		MiSpinButtons	spinButtons;
	private		MiiValueIterator valueIterator;


	public				MiSpinBox()
		{
		this(true);
		}
	public				MiSpinBox(boolean editable)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiSpinBox");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}
		setInsetMargins(1);

		setValueIterator(new MiIntegerValueIterator());

		MiRowLayout layout = new MiRowLayout();
		layout.setAlleyHSpacing(0);
		layout.setElementVSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);
		textField = new MiTextField();
		textField.setIsEditable(editable);
		textField.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		textField.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		textField.appendActionHandler(this, Mi_TEXT_CHANGE_ACTION);
		textField.setName("field");
		appendPart(textField);
		spinButtons = new MiSpinButtons(Mi_HORIZONTAL, Mi_VERTICAL);
		MiMargins margins = spinButtons.getMargins(new MiMargins());
		margins.left = 0;
		spinButtons.setMargins(margins);
		spinButtons.appendActionHandler(this, Mi_INCREASE_ACTION, Mi_DECREASE_ACTION);
		spinButtons.setName("buttons");
		appendPart(spinButtons);

		textField.insertEventHandler(new MiIExecuteCommand(
			new MiEvent(Mi_KEY_PRESS_EVENT, Mi_UP_ARROW_KEY, 0),
			this,
			Mi_INCREASE_COMMAND_NAME), 0);

		textField.insertEventHandler(new MiIExecuteCommand(
			new MiEvent(Mi_KEY_PRESS_EVENT, Mi_DOWN_ARROW_KEY, 0),
			this,
			Mi_DECREASE_COMMAND_NAME), 0);

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
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}

	public		void		setIsStringValue()
		{
		if (!(valueIterator instanceof MiStringValueIterator))
			{
			valueIterator = new MiStringValueIterator(new Strings());
			}
		}
	public		void		setRange(int minimum, int maximum, int increment)
		{
		valueIterator = new MiIntegerValueIterator(minimum, maximum, increment);
		}

	public		void		setRange(double minimum, double maximum, double increment)
		{
		valueIterator = new MiDoubleValueIterator(minimum, maximum, increment);
		}

	public		void		setMinimumValue(double minimum)
		{
		valueIterator.setMinimumValue(minimum);
		}
	public		double		getMinimumValue()
		{
		return(valueIterator.getMinimumValue());
		}
	public		void		setMaximumValue(double maximum)
		{
		valueIterator.setMaximumValue(maximum);
		}
	public		double		getMaximumValue()
		{
		return(valueIterator.getMaximumValue());
		}
	public		void		setStepValue(double step)
		{
		valueIterator.setStepValue(step);
		}
	public		double		getStepValue()
		{
		return(valueIterator.getStepValue());
		}
	public		void		setValue(String value)
		{
		textField.setValue(value);
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	public		String		getValue()
		{
		return(textField.getValue());
		}
	public		void		setContents(Strings contents)
		{
		valueIterator = new MiStringValueIterator(contents);
		}
	public		Strings		getContents()
		{
		return(valueIterator.getContents());
		}
	public		MiTextField	getTextField()
		{
		return(textField);
		}
	public		MiSpinButtons	getSpinButtons()
		{
		return(spinButtons);
		}
	public		void		setValueIterator(MiiValueIterator iterator)
		{
		valueIterator = iterator;
		}
	public		MiiValueIterator getValueIterator()
		{
		return(valueIterator);
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
	public static	void		setPrototype(MiSpinBox p)
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
					 * Mi_SPINBOX_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_SPINBOX_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiSpinBox )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	public		void		setSensitive(boolean flag)
		{
		super.setSensitive(flag);
		textField.setSensitive(flag);
		spinButtons.setSensitive(flag);
		}
	public		void		processCommand(String command)
		{
		if (command.equals(Mi_INCREASE_COMMAND_NAME))
			{
			String value = valueIterator.increaseValue(textField.getValue());
			if (value != null)
				setValue(value);
			}
		else if (command.equals(Mi_DECREASE_COMMAND_NAME))
			{
			String value = valueIterator.decreaseValue(textField.getValue());
			if (value != null)
				setValue(value);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if ((action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION))
			|| (action.hasActionType(Mi_ENTER_KEY_ACTION)))
			{
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		else if (action.hasActionType(Mi_TEXT_CHANGE_ACTION))
			{
			dispatchAction(Mi_TEXT_CHANGE_ACTION);
			}
		else if (action.hasActionType(Mi_INCREASE_ACTION))
			{
			String value = valueIterator.increaseValue(textField.getValue());
			if (value != null)
				setValue(value);
			}
		else if (action.hasActionType(Mi_DECREASE_ACTION))
			{
			String value = valueIterator.decreaseValue(textField.getValue());
			if (value != null)
				setValue(value);
			}
		return(true);
		}
	}
interface MiiValueIterator
	{
	// Return null if cannot increase value any more
	String		increaseValue(String value);

	// Return null if cannot decrease value any more
	String		decreaseValue(String value);

	void		setMinimumValue(double value);
	double		getMinimumValue();
	void		setMaximumValue(double value);
	double		getMaximumValue();
	void		setStepValue(double value);
	double		getStepValue();
	void		setContents(Strings contents);
	Strings		getContents();
	}
class MiIntegerValueIterator implements MiiValueIterator
	{
	private		int		minimum;
	private		int		maximum;
	private		int		increment;

	public				MiIntegerValueIterator()
		{
		this(0, Integer.MAX_VALUE, 1);
		}
	public				MiIntegerValueIterator(int minimum, int maximum, int increment)
		{
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
		}
	public		void		setMinimumValue(double min)
		{
		minimum = (int )min;
		}
	public		double		getMinimumValue()
		{
		return((double )minimum);
		}
	public		void		setMaximumValue(double max)
		{
		maximum = (int )max;
		}
	public		double		getMaximumValue()
		{
		return((double )maximum);
		}
	public		void		setStepValue(double step)
		{
		increment = (int )step;
		}
	public		double		getStepValue()
		{
		return((double )increment);
		}
	public		void		setContents(Strings contents)
		{
		}
	public		Strings		getContents()
		{
		Strings strings = new Strings();
		if ((maximum - minimum)/increment > 100)
			{
			strings.addElement("" + minimum);
			strings.addElement("" + (minimum + increment));
			strings.addElement("...");
			strings.addElement("" + maximum);
			}
		else
			{
			int value = minimum;
			while (value <= maximum)
				{
				strings.addElement("" + value);
				value += increment;
				}
			}
		return(strings);
		}
	public		String		increaseValue(String value)
		{
		if (Utility.isEmptyOrNull(value))
			return("0");

		int val = (int )Utility.toDouble(value);
		val += increment;
		if (val <= maximum)
			return("" + val);

		return(null);
		}
	public		String		decreaseValue(String value)
		{
		if (Utility.isEmptyOrNull(value))
			return("0");

		int val = (int )Utility.toDouble(value);
		val -= increment;
		if (val >= minimum)
			return("" + val);

		return(null);
		}
	}
class MiDoubleValueIterator implements MiiValueIterator
	{
	private		double		minimum;
	private		double		maximum;
	private		double		increment;

	public				MiDoubleValueIterator()
		{
		this(0, Double.MAX_VALUE, 1);
		}
	public				MiDoubleValueIterator(
						double minimum, double maximum, double increment)
		{
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
		}
	public		void		setMinimumValue(double min)
		{
		minimum = min;
		}
	public		double		getMinimumValue()
		{
		return(minimum);
		}
	public		void		setMaximumValue(double max)
		{
		maximum = max;
		}
	public		double		getMaximumValue()
		{
		return(maximum);
		}
	public		void		setStepValue(double step)
		{
		increment = step;
		}
	public		double		getStepValue()
		{
		return(increment);
		}
	public		void		setContents(Strings contents)
		{
		}
	public		Strings		getContents()
		{
		Strings strings = new Strings();
		if ((maximum - minimum)/increment > 100)
			{
			strings.addElement("" + minimum);
			strings.addElement("" + (minimum + increment));
			strings.addElement("...");
			strings.addElement("" + maximum);
			}
		else
			{
			double value = minimum;
			while (value <= maximum)
				{
				strings.addElement("" + value);
				value += increment;
				}
			}
		return(strings);
		}
	public		String		increaseValue(String value)
		{
		if (Utility.isEmptyOrNull(value))
			return("0");

		double val = Utility.toDouble(value);
		val += increment;
		if (val <= maximum)
			return("" + val);

		return(null);
		}
	public		String		decreaseValue(String value)
		{
		if (Utility.isEmptyOrNull(value))
			return("0");

		double val = Utility.toDouble(value);
		val -= increment;
		if (val >= minimum)
			return("" + val);

		return(null);
		}
	}
class MiStringValueIterator implements MiiValueIterator
	{
	private		Strings		contents;

	public				MiStringValueIterator(Strings contents)
		{
		this.contents = new Strings(contents);
		}
	public		void		setMinimumValue(double min)
		{
		}
	public		double		getMinimumValue()
		{
		return((double )0);
		}
	public		void		setMaximumValue(double max)
		{
		}
	public		double		getMaximumValue()
		{
		return((double )0);
		}
	public		void		setStepValue(double step)
		{
		}
	public		double		getStepValue()
		{
		return((double )1);
		}
	public		void		setContents(Strings contents)
		{
		this.contents.removeAllElements();
		this.contents.append(contents);
		}
	public		Strings		getContents()
		{
		return(contents);
		}
	public		String		increaseValue(String value)
		{
		int index = contents.indexOf(value);
		if (index == -1)
			return(contents.elementAt(0));
		else if (index < contents.size() - 1)
			return(contents.elementAt(index + 1));

		return(null);
		}
	public		String		decreaseValue(String value)
		{
		int index = contents.indexOf(value);
		if (index == -1)
			return(contents.elementAt(0));
		else if (index > 0)
			return(contents.elementAt(index - 1));

		return(null);
		}
	}


