
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
public class MiCycleButton extends MiPushButton 
	{
	public static final	String		Mi_CYCLEBUTTON_PROTOTYPE_CLASS_NAME 
							= "Mi_CYCLEBUTTON_PROTOTYPE_CLASS_NAME";
	private static		MiCycleButton	prototype;
	private	static		MiPart		decorationIcon;
	private			int		currentValueIndex;
	private			MiPart		currentValue;
	private			MiParts		valueSet		= new MiParts();


	public				MiCycleButton()
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCycleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		setLabel(makeCycleButtonLabel(""));
		valueSet.addElement(new MiText(""));
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public				MiCycleButton(Strings values)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCycleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		setLabel(makeCycleButtonLabel(values.size() > 0 ? values.elementAt(0) : ""));
		setContents(values);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public				MiCycleButton(String value1, String value2)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCycleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		setLabel(makeCycleButtonLabel(value1));
		valueSet.addElement(new MiText(value1));
		valueSet.addElement(new MiText(value2));
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public				MiCycleButton(String value1, String value2, String value3)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCycleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		setLabel(makeCycleButtonLabel(value1));
		valueSet.addElement(new MiText(value1));
		valueSet.addElement(new MiText(value2));
		valueSet.addElement(new MiText(value3));
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public				MiCycleButton(MiParts parts)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCycleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		setLabel(makeCycleButtonLabel(parts.elementAt(0)));
		valueSet.append(parts);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public				MiCycleButton(MiPart value1, MiPart value2)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCycleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		setLabel(makeCycleButtonLabel(value1));
		valueSet.addElement(value1);
		valueSet.addElement(value2);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public				MiCycleButton(MiPart value1, MiPart value2, MiPart value3)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiCycleButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		setLabel(makeCycleButtonLabel(value1));
		valueSet.addElement(value1);
		valueSet.addElement(value2);
		valueSet.addElement(value3);
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
			return(new MiCycleButton());

		return((MiCycleButton )prototype.deepCopy());
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
	public static	void		setPrototype(MiCycleButton p)
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
					 * Mi_CYCLEBUTTON_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_CYCLEBUTTON_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiCycleButton )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
		
	public		void		select(boolean flag)
		{
		super.select(flag);
		if ((flag) && (flag == isSelected()))
			{
			int num = valueSet.size();
			if (currentValueIndex >= num - 1)
				currentValueIndex = 0;
			else
				++currentValueIndex;

			setValue(currentValueIndex);
			}
		}
	public		void		setValue(int index)
		{
		currentValueIndex = index;
		MiPart newValue = valueSet.elementAt(currentValueIndex);
		if (currentValue == newValue)
			return;
		newValue.setCenter(currentValue.getCenter());
		currentValue.replaceSelf(newValue);
		currentValue = newValue;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	public		void		setValue(String value)
		{
		for (int i = 0; i < valueSet.size(); ++i)
			{
			if ((valueSet.elementAt(i) instanceof MiText)
				&& (((MiText )valueSet.elementAt(i)).getText().equals(value)))
				{
				setValue(i);
				}
			else 
				{
				String name = valueSet.elementAt(i).getName();
				if ((name != null) && (name.equals(value)))
					setValue(i);
				}
			}
		}
	public		String		getValue()
		{
		if (valueSet.elementAt(currentValueIndex) instanceof MiText)
			return(((MiText )valueSet.elementAt(currentValueIndex)).getText());
		return(valueSet.elementAt(currentValueIndex).getName());
		}
	public		void		setContents(Strings contents)
		{
		valueSet.removeAllElements();
		if (contents.size() == 0)
			valueSet.addElement(new MiText(""));
		for (int i = 0; i < contents.size(); ++i)
			valueSet.addElement(new MiText(contents.elementAt(i)));
		setValue(0);
		}
	public		Strings		getContents()
		{
		Strings contents = new Strings();
		for (int i = 0; i < valueSet.size(); ++i)
			{
			if (valueSet.elementAt(i) instanceof MiText)
				{
				contents.addElement(((MiText )valueSet.elementAt(i)).getText());
				}
			else 
				{
				String name = valueSet.elementAt(i).getName();
				if (name != null)
					contents.addElement(name);
				else
					contents.addElement(valueSet.elementAt(i).toString());
				}
			}
		return(contents);
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiCycleButton obj 	= (MiCycleButton )source;

		if (obj.decorationIcon != null)
			decorationIcon = obj.decorationIcon.copy();
		else
			decorationIcon = null;
		}
	protected	MiPart		makeDecoration()
		{
		if (decorationIcon == null)
			{
			decorationIcon = new MiImage("cycleButton.gif", true);
			decorationIcon.setName("icon");
			}
		return(decorationIcon.copy());
		}
	protected	MiPart		makeCycleButtonLabel(MiPart obj)
		{
		MiRowLayout layout = new MiRowLayout();
		layout.appendPart(obj);
		layout.appendPart(makeDecoration());
		currentValue = obj;
		return(layout);
		}
	protected	MiPart		makeCycleButtonLabel(String text)
		{
		return(makeCycleButtonLabel(new MiText(text)));
		}
	}

