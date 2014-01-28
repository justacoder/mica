
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiWidgetAttributes
	{
	public static final String		Mi_NORMAL_ATTRIBUTES_NAME		= "normal.";
	public static final String		Mi_INSENSITIVE_ATTRIBUTES_NAME		= "insensitive.";
	public static final String		Mi_SELECTED_ATTRIBUTES_NAME		= "selected.";
	public static final String		Mi_KEYBOARD_FOCUS_ATTRIBUTES_NAME	= "keyboardFocus.";
	public static final String		Mi_ENTERKEY_FOCUS_ATTRIBUTES_NAME	= "enterKeyFocus.";
	public static final String		Mi_MOUSE_FOCUS_ATTRIBUTES_NAME		= "mouseFocus.";

	// -----------------------------------------------------------------------
	// The table of widget attribute names
	// -----------------------------------------------------------------------
	public static final String[]	widgetStateAttributeNameTable =
		{
		Mi_NORMAL_ATTRIBUTES_NAME,
		Mi_INSENSITIVE_ATTRIBUTES_NAME,
		Mi_SELECTED_ATTRIBUTES_NAME,
		Mi_KEYBOARD_FOCUS_ATTRIBUTES_NAME,
		Mi_ENTERKEY_FOCUS_ATTRIBUTES_NAME,
		Mi_MOUSE_FOCUS_ATTRIBUTES_NAME
		};

	public		MiAttributes		normalAttributes;
	public		MiAttributes		inSensitiveAttributes;
	public		MiAttributes		selectedAttributes;
	public		MiAttributes		keyboardFocusAttributes;
	public		MiAttributes		enterKeyFocusAttributes;
	public		MiAttributes		mouseFocusAttributes;


	public					MiWidgetAttributes()
		{
		}

						// Same order as MiiNames.widgetStateAttributeNameTable
	public		MiAttributes		getAttributes(int index)
		{
		switch (index)
			{
			case 0 :
				return(normalAttributes);
			case 1 :
				return(inSensitiveAttributes);
			case 2 :
				return(selectedAttributes);
			case 3 :
				return(keyboardFocusAttributes);
			case 4 :
				return(enterKeyFocusAttributes);
			}
		return(mouseFocusAttributes);
		}
						// Same order as MiiNames.widgetStateAttributeNameTable
	public		void			setAttributes(MiAttributes atts, int index)
		{
		switch (index)
			{
			case 0 :
				normalAttributes = atts;
				break;
			case 1 :
				inSensitiveAttributes = atts;
				break;
			case 2 :
				selectedAttributes = atts;
				break;
			case 3 :
				keyboardFocusAttributes = atts;
				break;
			case 4 :
				enterKeyFocusAttributes = atts;
				break;
			case 5 :
				mouseFocusAttributes = atts;
				break;
			}
		}
	public		boolean		equals(Object other)
		{
		if (!(other instanceof MiWidgetAttributes))
			return(false);

		MiWidgetAttributes atts = (MiWidgetAttributes )other;
		return(
			normalAttributes.equals(atts.normalAttributes)
		&&	inSensitiveAttributes.equals(atts.inSensitiveAttributes)
		&&	selectedAttributes.equals(atts.selectedAttributes)
		&&	keyboardFocusAttributes.equals(atts.keyboardFocusAttributes)
		&&	enterKeyFocusAttributes.equals(atts.enterKeyFocusAttributes)
		&&	mouseFocusAttributes.equals(atts.mouseFocusAttributes));
		}
					/**------------------------------------------------------
	 				 * Gets the hopefully unique value identifying this 
					 * MiWidgetAttributes based on the values of the attributes.
					 * @return  		the hash code for this object
					 * @overrides		java.lang.Object#hashCode
					 *------------------------------------------------------*/
	public 		int 		hashCode()
		{
		return(
			normalAttributes.hashCode()
			+ (inSensitiveAttributes.hashCode() << 1)
			+ (selectedAttributes.hashCode() << 2)
			+ (keyboardFocusAttributes.hashCode() << 3)
			+ (enterKeyFocusAttributes.hashCode() << 4)
			+ (mouseFocusAttributes.hashCode() << 5));
		}
	}

