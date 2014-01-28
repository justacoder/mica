
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
 * Create a basic label widget (usually some kind of graphics 
 * inside of a rectangle). 
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiLabel extends MiWidget
	{
	public static final	String		Mi_LABEL_PROTOTYPE_CLASS_NAME 
							= "Mi_LABEL_PROTOTYPE_CLASS_NAME";
	private static		MiLabel		prototype;
	private			MiPart 		label;




					/**------------------------------------------------------
	 				 * Constructs a new MiLabel with no label.
					 *------------------------------------------------------*/
	public				MiLabel()
		{
		this((MiText )null);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiLabel with the given label text.
	 				 * @param text 		the label
					 *------------------------------------------------------*/
	public				MiLabel(String text)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiLabel");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			setNormalBackgroundColor(Mi_TRANSPARENT_COLOR);
			if (text != null)
				{
				label = new MiText(text);
				label.setFont(getFont());
				label.setColor(getColor());
				label.setName("label");
				appendPart(label);
				}
			}
		if (getClass().getName().equals("com.swfm.mica.MiLabel"))
			{
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			}
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiLabel with the given label graphics.
	 				 * @param obj 		the label
					 *------------------------------------------------------*/
	public				MiLabel(MiPart obj)
		{
		setNormalBackgroundColor(Mi_TRANSPARENT_COLOR);
		if (obj != null)
			{
			label = obj;
			label.setName("label");
			appendPart(label);
			}
		if (getClass().getName().equals("com.swfm.mica.MiLabel"))
			{
			refreshLookAndFeel();
			applyCustomLookAndFeel();
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
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}
					/**------------------------------------------------------
	 				 * Set the value of this widget, which is the label, to 
					 * the given text.
	 				 * @param text 		the new label
					 *------------------------------------------------------*/
	public		void		setValue(String text)
		{
		if (label != null)
			{
			// 12-30-2001, only apply to text or widgets, do not remove none-text/widget labels
			_setValueOfLabel(label, text);
			}
		else if (text != null)
			{
			MiPart label = new MiText(text);
			label.setFont(getFont());
			label.setColor(getColor());
			label.setName("label");
			setLabel(label);
			}
		}
					/**------------------------------------------------------
	 				 * Get the value of this widget, which is the label. If
					 * the label is textual then it is returned. If the label
					 * is another widget, it's value is returned. If the label
					 * is some other kind of graphics, null is returned.
	 				 * @return 		the label text.
					 *------------------------------------------------------*/
	public		String		getValue()
		{
		if (label instanceof MiWidget)
			return(((MiWidget )label).getValue());
		else if (label instanceof MiText)
			return(((MiText )label).getText());
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the label of this widget.
	 				 * @return 		the label
					 *------------------------------------------------------*/
	public		MiPart		getLabel()
		{
		return(label);
		}
					/**------------------------------------------------------
	 				 * Sets the label of this widget.
	 				 * @param text 		the new label
					 *------------------------------------------------------*/
	public		void		setLabel(String text)
		{
		setValue(text);
		}
					/**------------------------------------------------------
	 				 * Sets the label of this widget.
	 				 * @param obj 		the new label
					 *------------------------------------------------------*/
	public		void		setLabel(MiPart obj)
		{
		if (label == obj)
			return;

		if (obj != null)
			obj.setCenter(getCenter());

		if (label != null)
			removePart(label);
		label = obj;
		if (label != null)
			appendPart(label);
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
	public static	void		setPrototype(MiLabel p)
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
					 * Mi_LABEL_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_LABEL_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiLabel )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
					/**------------------------------------------------------
	 				 * Sets the sensitivity of this widget and it's label.
	 				 * @param flag 		true if this is to be sensitive
	 				 * @overrides 		MiPart#setSensitive
	 				 * @see 		#setLabelSensivity
					 *------------------------------------------------------*/
	public		void		setSensitive(boolean flag) 		
		{
		if (flag == isSensitive())
			return;

		super.setSensitive(flag);
		setLabelSensivity(flag);
		}
					/**------------------------------------------------------
	 				 * Sets the sensitivity of the label graphics of this label.
					 * Overrides this to do nothing if a special label has been
					 * assigned to represent the graphics when insensitive.
	 				 * @param flag 		true if this is to be sensitive
					 *------------------------------------------------------*/
	protected	void		setLabelSensivity(boolean flag)
		{
		if (label != null) //&& ((label instanceof MiText) || (label instanceof MiWidget)))
			{
			label.setSensitive(flag);
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

		MiLabel src 		= (MiLabel )source;
		if (src.label != null)
			label = src.label.copy();
		else
			label = null;
		}
					/**------------------------------------------------------
	 				 * Returns textual information about this label widget.
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(super.toString() + (label != null ? ("." + label.toString()) : ""));
		}
	protected	boolean		_setValueOfLabel(MiPart label, String text)
		{
		if (label instanceof MiText)
			{
			// FIX: update bounds? see MiStatusBarFocusStatusField
			((MiText )label).setText(text);
			return(true);
			}
		else if (label instanceof MiWidget)
			{
			// FIX: update bounds? see MiStatusBarFocusStatusField
			((MiWidget )label).setValue(text);
			return(true);
			}
		for (int i = 0; i < label.getNumberOfParts(); ++i)
			{
			MiPart part = label.getPart(i);
			if (_setValueOfLabel(part, text))
				{
				return(true);
				}
			}
		return(false);
		}
	}

