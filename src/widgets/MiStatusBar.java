
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
public class MiStatusBar extends MiWidget
	{
	private		MiRowLayout 	layout 		= new MiRowLayout();
	private		MiParts 	fields 		= new MiParts();
	private		Strings 	specs;


	/*
	*
	* Spec of form: label.20\n.60\nlabel\n where #s are widths in num characters.
	* Specifications specify width of the fields. They are of form: 
	* label.20\n.m60\n.+m30\nlabel\n 
	* where the width of the field is set to the length of any labels in
	* addition to any numbers (preceeded by a '.') which indicate number 
	* of (m: maximum, else average) character widths, all in the current font
	* A plus sign indicates the field is user editable.
	*/
	public				MiStatusBar(String specification)
		{
		specs = new Strings(specification);
		setBorderLook(Mi_RAISED_BORDER_LOOK);
		setLayout(layout);
		layout.setAlleyHSpacing(5);
		layout.setInsetMargins(3);
		setInsetMargins(2);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		int[] numCharsSpecified = new int[1];
		boolean[] fieldIsEditable = new boolean[1];

		for (int i = 0; i < specs.size(); ++i)
			{
			String spec = specs.elementAt(i);
			MiDistance width = parseSpec(spec, numCharsSpecified, fieldIsEditable);

			if (numCharsSpecified[0] != 0)
				{
				MiWidget field = null;
				if (fieldIsEditable[0])
					{
					MiTextField tf = new MiTextField("");
					tf.setNumDisplayedColumns(numCharsSpecified[0]);
					field = tf;
					}
				else
					{
					MiLabel lbl = new MiLabel("");
					lbl.getLabel().setInvalidLayoutNotificationsEnabled(false);
					lbl.getLabel().setFont(getFont());
					field = lbl;
					}
				field.setContainerLayoutSpec(Mi_NONE);
				field.setElementHJustification(Mi_LEFT_JUSTIFIED);

				// Textfields know what size they need to be based on their 
				// number of displayed columns
				if (!(field instanceof MiTextField))
					{
					MiSize size = new MiSize(
						numCharsSpecified[0] * getFont().getAverageCharWidth(),
						getFont().getMaxCharHeight());
					size.addMargins(field.getTotalMargins());

					field.setPreferredSize(size);
					}

				field.setBorderLook(Mi_INDENTED_BORDER_LOOK);
				MiToolkit.setBackgroundAttributes(field, 
						MiiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);
				field.setPropertyValues(MiSystem.getPropertiesForClass("MiStatusBarField"));

				appendPart(field);
				fields.addElement(field);
				}
			}
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}

	public		void		setAttributes(MiAttributes atts)
		{
		MiFont font = getFont();
		super.setAttributes(atts);
		if ((getFont() != font) && (fields != null))
			updateFieldSizesForNewFont();
		}
	public		void		setFieldValue(String value, int which)
		{
		((MiWidget )fields.elementAt(which)).setValue(value);
		}
	public		String		getFieldValue(int which)
		{
		return(((MiWidget )fields.elementAt(which)).getValue());
		}
	public		void		setField(MiWidget what, int which)
		{
		setPart(what, which);
		}
	public		void		appendField(MiWidget what, int characterWidth)
		{
		appendPart(what);
		MiSize size = new MiSize(
			characterWidth * getFont().getAverageCharWidth(), getFont().getMaxCharHeight());
		size.addMargins(what.getTotalMargins());
		what.setPreferredSize(size);
		}
	public		MiWidget	getField(int index)
		{
		return((MiWidget )fields.elementAt(index));
		}
	public		int		getNumberOfFields()
		{
		return(fields.size());
		}
	public		void		setValue(String value)
		{
		((MiWidget )getPart(0)).setValue(value);
		}
	public		String		getValue()
		{
		return(((MiWidget )getPart(0)).getValue());
		}
	public		MiLayout	getStatusBarLayout()
		{
		return(layout);
		}
	public		void		setTargetEditor(MiEditor editor)
		{
		}
	public		void		refreshLookAndFeel()
		{
		super.refreshLookAndFeel();
		for (int i = 0; i < fields.size(); ++i)
			{
			MiWidget field = (MiWidget )fields.elementAt(i);
			MiToolkit.setBackgroundAttributes(field, 
					MiiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);
			field.setPropertyValues(MiSystem.getPropertiesForClass("MiStatusBarField"));
			}
		}
	protected	void		updateFieldSizesForNewFont()
		{
		int[] numCharsSpecified = new int[1];
		boolean[] fieldIsEditable = new boolean[1];
		for (int i = 0; i < fields.size(); ++i)
			{
			String spec = specs.elementAt(i);
			MiWidget field = (MiWidget )fields.elementAt(i);
			MiDistance width = parseSpec(spec, numCharsSpecified, fieldIsEditable);
			MiSize size = new MiSize(width, getFont().getMaxCharHeight());
			size.addMargins(field.getTotalMargins());
			field.setPreferredSize(size);
			}
		}

	protected	MiDistance	parseSpec(String spec, int[] numCharsSpecified, boolean[] fieldIsEditable)
		{
		int index;
		while (((index = spec.indexOf('.')) != -1) 
			&& ((index > 0) && (spec.charAt(index - 1) == '\\')))
			{
			}
		String label = null;
		int numChars = 0;
		boolean isEditable = false;
		MiDistance width = 0;
		boolean isMultipleOfMaxFontWidth = false;
		if (index != -1)
			{
			if (index > 0)
				{
				label = spec.substring(0, index);
				}
			if (index < spec.length() - 1)
				{
				if (spec.charAt(index + 1) == '+')
					{
					isEditable = true;
					++index;
					}
				if (spec.charAt(index + 1) == 'm')
					{
					isMultipleOfMaxFontWidth = true;
					++index;
					}
				numChars = Utility.toInteger(spec.substring(index + 1));
				}
			}
		else
			{
			label = spec;
			}
		if (label != null)
			{
			width += getFont().getWidth(label);
			numChars += label.length();
			}
		width += numChars * (isMultipleOfMaxFontWidth ? getFont().getMaxCharWidth() :
			getFont().getAverageCharWidth());

		numCharsSpecified[0] = numChars;
		fieldIsEditable[0] = isEditable;
		return(width);
		}
	}

