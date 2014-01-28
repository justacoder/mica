
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
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.CaselessKeyHashtable;

import java.awt.Color; 
import java.awt.Image; 
import java.awt.Font; 

/**----------------------------------------------------------------------------------------------
 * Instances of this class contain a number of attributes (about 64 at
 * this writing). There are a large number of methods to get and set 
 * the values of these attributes.
 * <p>
 * Instances can be mutable or immutable (the default). If immutable,
 * then values of attributes cannot be set directly. Instead, the
 * methods to set the values of attributes return a new instance of
 * MiAttributes, a copy of the original but with a particular attribute
 * value modified.
 * <p>
 * In order to set the value of a _mutable_ attribute use the setStaticXXX
 * methods.
 * <p>
 * A number of the advanced methods use attribute 'indexes'. See the
 * MiiAttributeTypes file for a list of attribute indexes.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiAttributes extends MiGeneralAttributes implements MiiTypes, MiiAttributeTypes, MiiPropertyTypes, MiiNames
	{
//	private static	MiFont 		defaultFont	= new MiFont("TimesRoman", MiFont.PLAIN, 12);
//	public static	MiFont 		defaultFont	= new MiFont("Courier", MiFont.PLAIN, 12);
//	public static	MiFont 		defaultFont	= new MiFont("serif", MiFont.PLAIN, 12);
//	public static	MiFont 		defaultFont	= new MiFont("Monospaced", MiFont.PLAIN, 12);
//	public static	MiFont 		defaultFont	= new MiFont("SansSerif", MiFont.PLAIN, 12);
	private static	MiFont 		defaultFont	= new MiFont("Dialog", MiFont.PLAIN, 12);


	private static	int			numCreated			= 0;
	private	static	MiAttributeCache 	cache 				= new MiAttributeCache();
	private static	MiAttributes		defaultAttributes		= new MiAttributes(0);
	private	static	MiLineEndsRenderer	defaultLineEndsRenderer;
	private	static	MiBorderLookRenderer	defaultBorderRenderer;
	private	static	MiiShadowRenderer	defaultShadowRenderer;
	private	static	CaselessKeyHashtable	attributeNames;
	private	static	CaselessKeyHashtable	fontAttributeNames;
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private	static	MiChangedAttributes	changedAttributes		= new MiChangedAttributes();
	private static	MiAttributes 		complexXorResult 		= new MiAttributes(false);

	private static final int		Mi_FONT_NAME			= 0;
	private static final int		Mi_FONT_SIZE			= 1;
	private static final int		Mi_FONT_BOLD			= 2;
	private static final int		Mi_FONT_ITALIC			= 3;
	private static final int		Mi_FONT_UNDERLINED		= 4;
	private static final int		Mi_FONT_STRIKEOUT		= 5;


					/**------------------------------------------------------
	 				 * Constructs a new immutable MiAttributes.
					 *------------------------------------------------------*/
	public				MiAttributes()
		{
		this(true);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiAttributes.
					 * @param isImmutable	true if immutable
					 *------------------------------------------------------*/
	public				MiAttributes(boolean isImmutable)
		{
		super(isImmutable,
			Mi_NUMBER_OF_ATTRIBUTES, 
			Mi_START_OBJECT_INDEX, Mi_NUM_OBJECT_INDEXES,
			Mi_START_INTEGER_INDEX, Mi_NUM_INTEGER_INDEXES,
			Mi_START_DOUBLE_INDEX, Mi_NUM_DOUBLE_INDEXES,
			Mi_START_BOOLEAN_INDEX, Mi_NUM_BOOLEAN_INDEXES);

		directCopy(defaultAttributes);
		}
					/**------------------------------------------------------
	 				 * For debugging, return the number and a description of
					 * all of the attributes created so far.
					 * @return 		a textual description of the
					 *			attribute cache
					 * @overrides		MiGeneralAttributes#cacheToString
					 *------------------------------------------------------*/
	public		String		cacheToString()
		{
		return("Number of graphics attributes created: " + numCreated
			+ "\nNumber of unique graphics attributes cached:" 
			+ getCache().getNumberOfCachedAttributes()
			+ "\nCache = " + getCache().toString());
		}
	public static	void		setDefaultAttributes(MiAttributes atts)
		{
		defaultAttributes = atts;
		}
	public static	MiAttributes	getDefaultAttributes()
		{
		return(defaultAttributes);
		}
					/**------------------------------------------------------
	 				 * Gets the index of the attribute with the given name.
					 * @return 		the index or -1, if no such attribute
					 * @overrides		MiGeneralAttributes#getIndexOfAttributeName
					 *------------------------------------------------------*/
	public		int 		getIndexOfAttributeName(String name)
		{
		if (attributeNames == null)
			{
			buildSpacelessAttributeNames();
			}
		Integer index = (Integer )attributeNames.get(name);
		if (index != null)
			{
			return(index.intValue());
			}
		return(-1);
		}
	protected	void		buildSpacelessAttributeNames()
		{
		attributeNames = new CaselessKeyHashtable();
		for (int i = 0; i < MiiNames.attributeNames.length; ++i)
			{
			// Add the name both with and without spaces...
			String str = MiiNames.attributeNames[i];
			Integer attIndex = new Integer(i);
			attributeNames.put(str, attIndex);
			if (str.indexOf(' ') != -1)
				attributeNames.put(Utility.replaceAll(str, " ", ""), attIndex);
			}
		fontAttributeNames = new CaselessKeyHashtable();
		String str = Mi_FONT_NAME_ATT_NAME;
		Integer attIndex = new Integer(Mi_FONT_NAME);
		fontAttributeNames.put(str, attIndex);
		if (str.indexOf(' ') != -1)
			fontAttributeNames.put(Utility.replaceAll(str, " ", ""), attIndex);

		str = Mi_FONT_SIZE_ATT_NAME;
		attIndex = new Integer(Mi_FONT_SIZE);
		fontAttributeNames.put(str, attIndex);
		if (str.indexOf(' ') != -1)
			fontAttributeNames.put(Utility.replaceAll(str, " ", ""), attIndex);

		str = Mi_FONT_BOLD_ATT_NAME;
		attIndex = new Integer(Mi_FONT_BOLD);
		fontAttributeNames.put(str, attIndex);
		if (str.indexOf(' ') != -1)
			fontAttributeNames.put(Utility.replaceAll(str, " ", ""), attIndex);

		str = Mi_FONT_ITALIC_ATT_NAME;
		attIndex = new Integer(Mi_FONT_ITALIC);
		fontAttributeNames.put(str, attIndex);
		if (str.indexOf(' ') != -1)
			fontAttributeNames.put(Utility.replaceAll(str, " ", ""), attIndex);

		str = Mi_FONT_UNDERLINED_ATT_NAME;
		attIndex = new Integer(Mi_FONT_UNDERLINED);
		fontAttributeNames.put(str, attIndex);
		if (str.indexOf(' ') != -1)
			fontAttributeNames.put(Utility.replaceAll(str, " ", ""), attIndex);

		str = Mi_FONT_STRIKEOUT_ATT_NAME;
		attIndex = new Integer(Mi_FONT_STRIKEOUT);
		fontAttributeNames.put(str, attIndex);
		if (str.indexOf(' ') != -1)
			fontAttributeNames.put(Utility.replaceAll(str, " ", ""), attIndex);
		}
					/**------------------------------------------------------
	 				 * Gets the name of the attribute specified by the given
					 * index. This name may have spaces in it.
					 * @param index 	the index of an attribute
					 * @return 		the name of the attribute
					 * @overrides		MiGeneralAttributes#getNameOfAttribute
					 *------------------------------------------------------*/
	public		String		getNameOfAttribute(int index)
		{
		return(MiiNames.attributeNames[index]);
		}
					/**------------------------------------------------------
	 				 * Gets whether an attribute with the given name exists.
					 * @return 		true if such an attribute exists.
					 *------------------------------------------------------*/
	public		boolean		hasAttribute(String name)
		{
		if (super.hasAttribute(name))
			return(true);

		if ((Mi_FONT_NAME_ATT_NAME.equalsIgnoreCase(name))
			|| (Mi_FONT_SIZE_ATT_NAME.equalsIgnoreCase(name))
			|| (Mi_FONT_BOLD_ATT_NAME.equalsIgnoreCase(name))
			|| (Mi_FONT_ITALIC_ATT_NAME.equalsIgnoreCase(name))
			|| (Mi_FONT_UNDERLINED_ATT_NAME.equalsIgnoreCase(name))
			|| (Mi_FONT_STRIKEOUT_ATT_NAME.equalsIgnoreCase(name)))
			{
			return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Makes and returns a copy of this MiAttributes.
					 * @return 		the copy
					 *------------------------------------------------------*/
	public		MiAttributes	copy()
		{
		return((MiAttributes )makeCopy());
		}

	//***************************************************************************************
	// Set attribute on MUTABLE MiAttributes
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Sets the attribute specified by the given index to the
					 * specified value. This method, like all setStaticXXX
					 * methods, are to be used with mutable MiAttributes only.
					 * @param which		the index of the attribute
					 * @param color		the new value of the attribute
					 * @exception		IllegalArgumentException invalid
					 *			color attribute index
					 * @exception		RuntimeException attempt to modify
					 *			immutable attributes
					 *------------------------------------------------------*/
	public		void		setStaticAttribute(int which, Color color)
		{
		if ((which < Mi_START_COLOR_INDEX) || (which > Mi_START_COLOR_INDEX + Mi_NUM_COLOR_INDEXES - 1))
			{
			throw new IllegalArgumentException("Invalid Color attribute index");
			}
		if (isImmutable())
			{
			throw new RuntimeException("Attempt to modify immutable attributes");
			}
		super.setStaticAttribute(which, color);
		}
					/**------------------------------------------------------
	 				 * Gets the value of the attribute specified by the given
					 * index.
					 * @param index 	the index of an attribute
					 * @return 		the value of the attribute
					 *------------------------------------------------------*/
	public		Color		getColorAttribute(int which)
		{
		return((Color )getAttribute(which));
		}
					/**------------------------------------------------------
	 				 * Sets the attribute specified by the given index to the
					 * specified value. This method, like all setStaticXXX
					 * methods, are to be used with mutable MiAttributes only.
					 * @param which		the index of the attribute
					 * @param image		the new value of the attribute
					 * @exception		IllegalArgumentException invalid
					 *			image attribute index
					 * @exception		RuntimeException attempt to modify
					 *			immutable attributes
					 *------------------------------------------------------*/
	public		void		setStaticAttribute(int which, Image image)
		{
		if ((which < Mi_START_IMAGE_INDEX) || (which >= Mi_START_IMAGE_INDEX + Mi_NUM_IMAGE_INDEXES - 1))
			{
			throw new IllegalArgumentException("Invalid Color attribute index");
			}
		if (isImmutable())
			{
			throw new RuntimeException("Attempt to modify immutable attributes");
			}
		super.setStaticAttribute(which, image);
		}
					/**------------------------------------------------------
	 				 * Gets the value of the attribute specified by the given
					 * index.
					 * @param index 	the index of an attribute
					 * @return 		the value of the attribute
					 *------------------------------------------------------*/
	public		Image		getImageAttribute(int which)
		{
		return((Image )getAttribute(which));
		}

	//***************************************************************************************
	// Set attribute on the given MiParts by assigning a new MiAttributes to the object.
	//***************************************************************************************

	public		void		setAttributeForObject(MiPart part, int which, Object object)
		{
		MiAttributes atts = (MiAttributes )getModifiedAttributes(which, object);
		part.setAttributes(atts);
		}
	public		void		setAttributeForObject(MiPart part, int which, int value)
		{
		MiAttributes atts = (MiAttributes )getModifiedAttributes(which, value);
		part.setAttributes(atts);
		}
	public		void		setAttributeForObject(MiPart part, int which, double value)
		{
		MiAttributes atts = (MiAttributes )getModifiedAttributes(which, value);
		part.setAttributes(atts);
		}
	public		void		setAttributeForObject(MiPart part, int which, boolean value)
		{
		MiAttributes atts = (MiAttributes )getModifiedAttributes(which, value);
		part.setAttributes(atts);
		}
	public		void		setAttributeForObject(MiPart part, String name, String value)
		{
		MiAttributes atts = (MiAttributes )getModifiedAttributes(name, value);
		part.setAttributes(atts);
		}
	public		void		setAttributeValue(MiPart part, String name, Object value)
		{
		int index = getIndexOfAttribute(name);
		MiAttributes atts = (MiAttributes )getModifiedAttributes(index, value);
		part.setAttributes(atts);
		}
	public		void		setAttributeValue(MiPart part, String name, int value)
		{
		int index = getIndexOfAttribute(name);
		MiAttributes atts = (MiAttributes )getModifiedAttributes(index, value);
		part.setAttributes(atts);
		}
	public		void		setAttributeValue(MiPart part, String name, double value)
		{
		int index = getIndexOfAttribute(name);
		MiAttributes atts = (MiAttributes )getModifiedAttributes(index, value);
		part.setAttributes(atts);
		}
	public		void		setAttributeValue(MiPart part, String name, boolean value)
		{
		MiAttributes atts;
		if (name.equals(Mi_FONT_BOLD_ATT_NAME))
			{
			atts = setFont(((MiFont )objectAttributes[Mi_FONT]).setBold(value));
			}
		else if (name.equals(Mi_FONT_ITALIC_ATT_NAME))
			{
			atts = setFont(((MiFont )objectAttributes[Mi_FONT]).setItalic(value));
			}
		else if (name.equals(Mi_FONT_UNDERLINED_ATT_NAME))
			{
			atts = setFont(((MiFont )objectAttributes[Mi_FONT]).setUnderlined(value));
			}
		else if (name.equals(Mi_FONT_STRIKEOUT_ATT_NAME))
			{
			atts = setFont(((MiFont )objectAttributes[Mi_FONT]).setStrikeOut(value));
			}
		else
			{
			int index = getIndexOfAttribute(name);
			atts = (MiAttributes )getModifiedAttributes(index, value);
			}
		part.setAttributes(atts);
		}
	public		void		setAttributeValue(MiPart part, String name, String value)
		{
		MiAttributes atts = setAttributeValue(name, value);
		part.setAttributes(atts);
		}
	public		MiAttributes	setAttributeValue(String name, String value)
		{
		if ((value != null) 
			&& ((Mi_NULL_VALUE_NAME.equalsIgnoreCase(value)) || ("null".equalsIgnoreCase(value))))
			{
			value = null;
			}
		MiAttributes atts;
		int index = -1;
		if (attributeNames == null)
			{
			buildSpacelessAttributeNames();
			}
		Integer indexInt = (Integer )fontAttributeNames.get(name);
		if (indexInt != null)
			{
			index = indexInt.intValue();

			if (index == Mi_FONT_NAME)
				{
				return(setFont(((MiFont )objectAttributes[Mi_FONT]).setName(value)));
				}
			if (index == Mi_FONT_SIZE)
				{
				return(setFont(((MiFont )objectAttributes[Mi_FONT]).setPointSize(
					Utility.toInteger(value))));
				}
			if (index == Mi_FONT_BOLD)
				{
				return(setFont(((MiFont )objectAttributes[Mi_FONT]).setBold(
					Utility.toBoolean(value))));
				}
			if (index == Mi_FONT_ITALIC)
				{
				return(setFont(((MiFont )objectAttributes[Mi_FONT]).setItalic(
					Utility.toBoolean(value))));
				}
			if (index == Mi_FONT_UNDERLINED)
				{
				return(setFont(((MiFont )objectAttributes[Mi_FONT]).setUnderlined(
					Utility.toBoolean(value))));
				}
			if (index == Mi_FONT_STRIKEOUT)
				{
				return(setFont(((MiFont )objectAttributes[Mi_FONT]).setStrikeOut(
					Utility.toBoolean(value))));
				}
			}

		indexInt = (Integer )attributeNames.get(name);
		if (indexInt != null)
			index = indexInt.intValue();
		else
			throw new IllegalArgumentException("\"" + name + "\" is not a valid name of an attribute");

		if (index == Mi_FONT)
			{
			return(setFont(MiFontManager.findFont(value)));
			}
		if ((index >= Mi_START_COLOR_INDEX) && (index < Mi_START_COLOR_INDEX + Mi_NUM_COLOR_INDEXES))
			{
			atts = (MiAttributes )getModifiedAttributes(index, MiColorManager.getColor(value));
			return(atts);
			}

		if ((index == Mi_BACKGROUND_IMAGE) || (index == Mi_BACKGROUND_TILE))
			{
			if (value == null)
				{
				atts = (MiAttributes )getModifiedAttributes(index, null);
				}
			else
				{
				MiImage image = new MiImage(value);
				atts = (MiAttributes )getModifiedAttributes(index, image.getImage());
				}
			return(atts);
			}

		if ((index == Mi_TOOL_HINT_HELP) || (index == Mi_BALLOON_HELP)
			|| (index == Mi_STATUS_HELP) || (index == Mi_DIALOG_HELP))
			{
// ?????? why not? 9-8-2003 MiDebug.printStackTrace("ERROR - Setting help on attributes no longer supported");
			atts = (MiAttributes )getModifiedAttributes(index, value != null ? new MiHelpInfo(value) : null);
			}
		else
			{
			atts = (MiAttributes )getModifiedAttributes(name, value);
			}
			
		return(atts);
		}
					/**------------------------------------------------------
	 				 * Gets the value of the attribute with the given name
					 * as a text string. This will be object.toString() for
					 * object-value attributes, MiiTypes.Mi_NULL_VALUE_NAME
					 * for null valued object-value attributes, otherwise it
					 * will be the value in text format.
					 * @return 		the value as a string
					 * @overrides 		MiGeneralAttributes#getAttributeValue
					 *------------------------------------------------------*/
	public		String		getAttributeValue(String name)
		{
		int index = -1;
		if (fontAttributeNames == null)
			{
			buildSpacelessAttributeNames();
			}
		Integer indexInt = (Integer )fontAttributeNames.get(name);
		if (indexInt != null)
			{
			index = indexInt.intValue();
			if (index == Mi_FONT_NAME)
				return(((MiFont )objectAttributes[Mi_FONT]).getName());
			if (index == Mi_FONT_SIZE)
				return("" + ((MiFont )objectAttributes[Mi_FONT]).getPointSize());
			if (index == Mi_FONT_BOLD)
				return("" + ((MiFont )objectAttributes[Mi_FONT]).isBold());
			if (index == Mi_FONT_ITALIC)
				return("" + ((MiFont )objectAttributes[Mi_FONT]).isItalic());
			if (index == Mi_FONT_UNDERLINED)
				return("" + ((MiFont )objectAttributes[Mi_FONT]).isUnderlined());
			if (index == Mi_FONT_STRIKEOUT)
				return("" + ((MiFont )objectAttributes[Mi_FONT]).isStrikeOut());
			}

		index = getIndexOfAttribute(name);
		if (index == Mi_FONT)
			return(((MiFont )objectAttributes[Mi_FONT]).getFullName());

		if (index < Mi_START_INTEGER_INDEX)
			{
			if ((index >= Mi_START_COLOR_INDEX) 
				&& (index < Mi_START_COLOR_INDEX + Mi_NUM_COLOR_INDEXES))
				{
				return(MiColorManager.getColorName((Color )objectAttributes[index]));
				}
			if (objectAttributes[index] == null)
				return(Mi_NULL_VALUE_NAME);

			if ((index >= Mi_START_IMAGE_INDEX) 
				&& (index < Mi_START_IMAGE_INDEX + Mi_NUM_IMAGE_INDEXES))
				{
				String filename = null; // FIX! Make this a MiImage! and uniquely identify for undo
							// ((MiImage )objectAttributes[index]).getFilename();
				return((filename != null) ? filename : Mi_NULL_VALUE_NAME); // objectAttributes[index].toString());
				}
			return(objectAttributes[index].toString());
			}
		if (index < Mi_START_DOUBLE_INDEX)
			{
			MiPropertyDescriptions propertyDescriptions = getPropertyDescriptions();
			MiPropertyDescription propertyDescription = propertyDescriptions.elementAt(name);
			if (propertyDescription.getNumberOfValidValues() <= 0)
				return(Utility.toString(intAttributes[index - Mi_START_INTEGER_INDEX]));
			String valueName = MiSystem.getNameOfAttributeValue(
				propertyDescription.getValidValues().toArray(), 
				intAttributes[index - Mi_START_INTEGER_INDEX]);
			if (valueName == null)
				{
				throw new IllegalArgumentException(
					"Attribute: " + name + " has no known valid name for value: "
					 + intAttributes[index - Mi_START_INTEGER_INDEX] + "\n"
					+ "Valid names are: " + propertyDescription.getValidValues());
				}
			return(valueName);
			}
		if (index < Mi_START_BOOLEAN_INDEX)
			return(Utility.toString(doubleAttributes[index - Mi_START_DOUBLE_INDEX]));

		return(Utility.toString(booleanAttributes[index - Mi_START_BOOLEAN_INDEX]));
		}

	//***************************************************************************************
	// Overriding these methods so the return type is of MiAttributes
	//***************************************************************************************

	public		MiAttributes		overrideFrom(MiAttributes from)
		{
		return((MiAttributes )super.overrideFrom(from));
		}
	public		MiAttributes		inheritFrom(MiAttributes from)
		{
		return((MiAttributes )super.inheritFrom(from));
		}
	public		MiAttributes		inheritFromAll(MiAttributes from)
		{
		return((MiAttributes )super.inheritFromAll(from));
		}
					/**------------------------------------------------------
	 				 * Copies the value of the specific attribute specified
					 * by the given index from the 'from' attributes and
					 * returns the resultant attributes.
					 * @param from		the attributes from which we will
					 *			get new value of an attribute
					 * @param which		the index of the attribute to copy
					 * @return 		the resultant attributes
					 *------------------------------------------------------*/
	public		MiAttributes	copyAttribute(MiAttributes from, int which)
		{
		return((MiAttributes )super.copyAttribute(from, which));
		}


	//***************************************************************************************
	// Convenience routines for MUTABLE MiAttributes
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Sets the foreground color attribute to the given value.
					 * This method, like all setStaticXXX methods, are to be 
					 * used with mutable MiAttributes only.
					 * @param color		the new value of the attribute
					 * @exception		RuntimeException attempt to modify
					 *			immutable attributes
					 *------------------------------------------------------*/
	public		void		setStaticColor(Color c)
		{
		if (isImmutable())
			{
			throw new RuntimeException("Attempt to modify immutable attributes");
			}
		objectAttributes[Mi_COLOR] = c;
		assignedAttributes[Mi_COLOR] = true;
		}
					/**------------------------------------------------------
	 				 * Sets the background color attribute to the given value.
					 * This method, like all setStaticXXX methods, are to be 
					 * used with mutable MiAttributes only.
					 * @param color		the new value of the attribute
					 * @exception		RuntimeException attempt to modify
					 *			immutable attributes
					 *------------------------------------------------------*/
	public		void		setStaticBackgroundColor(Color c)
		{
		if (isImmutable())
			{
			throw new RuntimeException("Attempt to modify immutable attributes");
			}
		objectAttributes[Mi_BACKGROUND_COLOR]  = c;
		assignedAttributes[Mi_BACKGROUND_COLOR] = true;
		}
					/**------------------------------------------------------
	 				 * Sets the border look attribute to the given value.
					 * This method, like all setStaticXXX methods, are to be 
					 * used with mutable MiAttributes only.
					 * @param color		the new value of the attribute
					 * @exception		RuntimeException attempt to modify
					 *			immutable attributes
					 *------------------------------------------------------*/
	public		void		setStaticBorderLook(int look)
		{
		if (isImmutable())
			{
			throw new RuntimeException("Attempt to modify immutable attributes");
			}
		intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX] = look;
		assignedAttributes[Mi_BORDER_LOOK] = true;
		}


	//***************************************************************************************
	// Convenience routines for setting and getting immutable MiAttributes
	//***************************************************************************************

	public		boolean			isDeletable()
		{ return(getBooleanAttribute(Mi_DELETABLE)); 				}
	public		boolean			isMovable()
		{ return(getBooleanAttribute(Mi_MOVABLE)); 				}
	public		boolean			isCopyable()
		{ return(getBooleanAttribute(Mi_COPYABLE)); 				}
	public		boolean			isCopyableAsPartOfCopyable()
		{ return(getBooleanAttribute(Mi_COPYABLE_AS_PART_OF_COPYABLE)); 	}
	public		boolean			isSelectable()
		{ return(getBooleanAttribute(Mi_SELECTABLE)); 				}
	public		boolean			isUngroupable()
		{ return(getBooleanAttribute(Mi_UNGROUPABLE)); 				}
	public		boolean			hasFixedWidth()
		{ return(getBooleanAttribute(Mi_FIXED_WIDTH)); 				}
	public		boolean			hasFixedHeight()
		{ return(getBooleanAttribute(Mi_FIXED_HEIGHT)); 			}
	public		boolean			hasFixedAspectRatio()
		{ return(getBooleanAttribute(Mi_FIXED_ASPECT_RATIO)); 			}
	public		boolean			isPickable()
		{ return(getBooleanAttribute(Mi_PICKABLE)); 				}
	public		boolean			isAcceptingMouseFocus()
		{ return(getBooleanAttribute(Mi_ACCEPTS_MOUSE_FOCUS)); 			}
	public		boolean			isAcceptingKeyboardFocus()
		{ return(getBooleanAttribute(Mi_ACCEPTS_KEYBOARD_FOCUS)); 		}
	public		boolean			isAcceptingEnterKeyFocus()
		{ return(getBooleanAttribute(Mi_ACCEPTS_ENTER_KEY_FOCUS)); 		}
	public		boolean			isAcceptingTabKeys()
		{ return(getBooleanAttribute(Mi_ACCEPTS_TAB_KEYS)); 			}
	public		boolean			isPrintable()
		{ return(getBooleanAttribute(Mi_PRINTABLE)); 				}
	public		boolean			isSavable()
		{ return(getBooleanAttribute(Mi_SAVABLE)); 				}
	public		boolean			isSnappable()
		{ return(getBooleanAttribute(Mi_SNAPPABLE)); 				}

	public		MiAttributes		setDeletable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_DELETABLE, b)); 	}
	public		MiAttributes		setMovable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_MOVABLE, b)); 		}
	public		MiAttributes		setCopyable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_COPYABLE, b)); 	}
	public		MiAttributes		setCopyableAsPartOfCopyable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_COPYABLE_AS_PART_OF_COPYABLE, b)); 	}
	public		MiAttributes		setSelectable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_SELECTABLE, b)); 	}
	public		MiAttributes		setFixedWidth(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_FIXED_WIDTH, b)); 	}
	public		MiAttributes		setFixedHeight(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_FIXED_HEIGHT, b)); 	}
	public		MiAttributes		setFixedAspectRatio(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_FIXED_ASPECT_RATIO, b)); }
	public		MiAttributes		setPickable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_PICKABLE, b)); 	}
	public		MiAttributes		setAcceptingMouseFocus(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_ACCEPTS_MOUSE_FOCUS, b)); }
	public		MiAttributes		setAcceptingKeyboardFocus(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_ACCEPTS_KEYBOARD_FOCUS, b)); }
	public		MiAttributes		setAcceptingEnterKeyFocus(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_ACCEPTS_ENTER_KEY_FOCUS, b)); }
	public		MiAttributes		setAcceptingTabKeys(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_ACCEPTS_TAB_KEYS, b)); }
	public		MiAttributes		setPrintable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_PRINTABLE, b)); 	}
	public		MiAttributes		setSavable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_SAVABLE, b)); 		}
	public		MiAttributes		setSnappable(boolean b)
		{ return((MiAttributes )getModifiedAttributes(Mi_SNAPPABLE, b)); 		}

	public		Color		getColor()
		{ return((Color )objectAttributes[Mi_COLOR]);				}
	public		Color		getBackgroundColor()
		{ return((Color )objectAttributes[Mi_BACKGROUND_COLOR]);		}
	public		Image		getBackgroundImage()
		{ return(getImageAttribute(Mi_BACKGROUND_IMAGE)); 			}
	public		Color		getWhiteColor()
		{ return((Color )objectAttributes[Mi_WHITE_COLOR]);			}
	public		Color		getLightColor()
		{ return((Color )objectAttributes[Mi_LIGHT_COLOR]);			}
	public		Color		getDarkColor()
		{ return((Color )objectAttributes[Mi_DARK_COLOR]);			}
	public		Color		getBlackColor()
		{ return((Color )objectAttributes[Mi_BLACK_COLOR]);			}
	public		Color		getXorColor()
		{ return(getColorAttribute(Mi_XOR_COLOR)); 				}

	public		Image		getBackgroundTile()
		{ return(getImageAttribute(Mi_BACKGROUND_TILE)); 			}
	public		MiDistance	getLineWidth()
		{ return(getDoubleAttribute(Mi_LINE_WIDTH)); 				}
	public		int		getLineStyle()
		{ return(getIntegerAttribute(Mi_LINE_STYLE)); 				}
	public		int		getLineStartStyle()
		{ return(getIntegerAttribute(Mi_LINE_START_STYLE)); 			}
	public		int		getLineEndStyle()
		{ return(getIntegerAttribute(Mi_LINE_END_STYLE));			}
	public		MiDistance	getLineStartSize()
		{ return(getDoubleAttribute(Mi_LINE_START_SIZE)); 			}
	public		MiDistance	getLineEndSize()
		{ return(getDoubleAttribute(Mi_LINE_END_SIZE));				}
	public		boolean		getLineEndsSizeFnOfLineWidth()
		{ return(getBooleanAttribute(Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH));	}
	public		MiFont		getFont()
		{ return((MiFont )getAttribute(Mi_FONT)); 				}
	public		int		getWriteMode()
		{ return(getIntegerAttribute(Mi_WRITE_MODE)); 				}

	public		MiiHelpInfo	getToolHintHelp()
		{return((MiiHelpInfo )getAttribute(Mi_TOOL_HINT_HELP)); 		}
	public		MiiHelpInfo	getBalloonHelp()
		{return((MiiHelpInfo )getAttribute(Mi_BALLOON_HELP)); 			}
	public		MiiHelpInfo	getStatusHelp()
		{return((MiiHelpInfo )getAttribute(Mi_STATUS_HELP)); 			}
	public		MiiHelpInfo	getDialogHelp()
		{return((MiiHelpInfo )getAttribute(Mi_DIALOG_HELP)); 			}
	public		MiiContextMenu	getContextMenu()	
		{ return((MiiContextMenu )getAttribute(Mi_CONTEXT_MENU)); 		}
	public		int		getContextCursor()	
		{ return(getIntegerAttribute(Mi_CONTEXT_CURSOR)); 			}

	public		MiDistance	getMinimumWidth()
		{ return(getDoubleAttribute(Mi_MINIMUM_WIDTH)); 			}
	public		MiDistance	getMinimumHeight()
		{ return(getDoubleAttribute(Mi_MINIMUM_HEIGHT)); 			}
	public		MiDistance	getMaximumWidth()
		{ return(getDoubleAttribute(Mi_MAXIMUM_WIDTH)); 			}
	public		MiDistance	getMaximumHeight()
		{ return(getDoubleAttribute(Mi_MAXIMUM_HEIGHT)); 			}

	public		int		getBorderLook()
		{ return(intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX]);	}
	public		boolean		getHasBorderHilite()
		{ return(getBooleanAttribute(Mi_HAS_BORDER_HILITE)); 			}
	public		Color		getBorderHiliteColor()
		{ return(getColorAttribute(Mi_BORDER_HILITE_COLOR)); 			}
	public		MiDistance	getBorderHiliteWidth()
		{ return(getDoubleAttribute(Mi_BORDER_HILITE_WIDTH)); 			}
	public		boolean		getHasShadow()
		{ return(getBooleanAttribute(Mi_HAS_SHADOW)); 				}
	public		Color		getShadowColor()
		{ return(getColorAttribute(Mi_SHADOW_COLOR)); 				}
	public		MiDistance	getShadowLength()
		{ return(getDoubleAttribute(Mi_SHADOW_LENGTH)); 			}
	public		int		getShadowDirection()
		{ return(getIntegerAttribute(Mi_SHADOW_DIRECTION)); 			}

	public		MiiPartRenderer	getBeforeRenderer()
		{ return((MiiPartRenderer )getAttribute(Mi_BEFORE_RENDERER)); 		}
	public		MiiPartRenderer	getAfterRenderer()
		{ return((MiiPartRenderer )getAttribute(Mi_AFTER_RENDERER)); 		}
	public		MiiShadowRenderer		getShadowRenderer()
		{ return((MiiShadowRenderer )getAttribute(Mi_SHADOW_RENDERER)); 	}
	public		MiiLineEndsRenderer		getLineEndsRenderer()
		{ return((MiiLineEndsRenderer )getAttribute(Mi_LINE_ENDS_RENDERER)); 	}

	public		MiiDeviceRenderer getBackgroundRenderer()
		{ return((MiiDeviceRenderer )getAttribute(Mi_BACKGROUND_RENDERER)); 	}
	public		MiiDeviceRenderer getBorderRenderer()
		{ return((MiiDeviceRenderer )getAttribute(Mi_BORDER_RENDERER)); 	}
	public		MiPartAnimator	getVisibilityAnimator() 
		{ return((MiPartAnimator )getAttribute(Mi_VISIBILITY_ANIMATOR)); 	}


	public		MiAttributes		setColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_COLOR, c)); 		}
	public		MiAttributes		setColor(String name)
		{ return(setColor(MiColorManager.getColor(name))); 				}
	public		MiAttributes		setBackgroundColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_BACKGROUND_COLOR, c));	}
	public		MiAttributes		setBackgroundColor(String name)
		{ return(setBackgroundColor(MiColorManager.getColor(name))); 		}
	public		MiAttributes		setWhiteColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_WHITE_COLOR, c));	}
	public		MiAttributes		setLightColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_LIGHT_COLOR, c));	}
	public		MiAttributes		setDarkColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_DARK_COLOR, c));	}
	public		MiAttributes		setBlackColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_BLACK_COLOR, c));	}
	public		MiAttributes		setXorColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_XOR_COLOR, c));		}

	public		MiAttributes		setBackgroundImage(Image i)
		{ return((MiAttributes )getModifiedAttributes(Mi_BACKGROUND_IMAGE, i));	}
	public		MiAttributes		setBackgroundTile(Image i)
		{ return((MiAttributes )getModifiedAttributes(Mi_BACKGROUND_TILE, i));	}
	public		MiAttributes		setLineWidth(MiDistance w)
		{ return((MiAttributes )getModifiedAttributes(Mi_LINE_WIDTH, w));	}
	public		MiAttributes		setLineStyle(int style)
		{ return((MiAttributes )getModifiedAttributes(Mi_LINE_STYLE, style));	}
	public		MiAttributes		setLineStartStyle(int style)
		{ return((MiAttributes )getModifiedAttributes(Mi_LINE_START_STYLE, style));}
	public		MiAttributes		setLineEndStyle(int style)
		{ return((MiAttributes )getModifiedAttributes(Mi_LINE_END_STYLE, style));}
	public		MiAttributes		setLineStartSize(MiDistance size)
		{ return((MiAttributes )getModifiedAttributes(Mi_LINE_START_SIZE, size));}
	public		MiAttributes		setLineEndSize(MiDistance size)
		{ return((MiAttributes )getModifiedAttributes(Mi_LINE_END_SIZE, size));}
	public		MiAttributes		setLineEndsSizeFnOfLineWidth(boolean flag)
		{ return((MiAttributes )getModifiedAttributes(Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH, flag));}
	public		MiAttributes		setFont(MiFont f) 
		{ return((MiAttributes )getModifiedAttributes(Mi_FONT, f));		}
	public		MiAttributes		setWriteMode(int wmode)
		{ return((MiAttributes )getModifiedAttributes(Mi_WRITE_MODE, wmode));	}
	public		MiAttributes		setContextMenu(MiiContextMenu menu)
		{ return((MiAttributes )getModifiedAttributes(Mi_CONTEXT_MENU, menu)); 	}
	public		MiAttributes		setContextCursor(int cursor)
		{ return((MiAttributes )getModifiedAttributes(Mi_CONTEXT_CURSOR, cursor)); }

	public		MiAttributes		setMinimumWidth(MiDistance width)
		{ return((MiAttributes )getModifiedAttributes(Mi_MINIMUM_WIDTH, width)); }
	public		MiAttributes		setMinimumHeight(MiDistance height)
		{ return((MiAttributes )getModifiedAttributes(Mi_MINIMUM_HEIGHT, height)); }
	public		MiAttributes		setMaximumWidth(MiDistance width)
		{ return((MiAttributes )getModifiedAttributes(Mi_MAXIMUM_WIDTH, width)); }
	public		MiAttributes		setMaximumHeight(MiDistance height)
		{ return((MiAttributes )getModifiedAttributes(Mi_MAXIMUM_HEIGHT, height)); }


	public		MiAttributes		setToolHintMessage(String msg)
		{
		return((MiAttributes )getModifiedAttributes(Mi_TOOL_HINT_HELP, 
				(msg != null) ? new MiHelpInfo(msg) : null));
		}
	public		MiAttributes		setBalloonHelpMessage(String msg)
		{
		return((MiAttributes )getModifiedAttributes(Mi_BALLOON_HELP, 
				(msg != null) ? new MiHelpInfo(msg) : null));
		}
	public		MiAttributes		setStatusHelpMessage(String msg)
		{
		return((MiAttributes )getModifiedAttributes(Mi_STATUS_HELP, 
				(msg != null) ? new MiHelpInfo(msg) : null));
		}
	public		MiAttributes		setDialogHelpMessage(String msg)
		{
		return((MiAttributes )getModifiedAttributes(Mi_DIALOG_HELP, 
				(msg != null) ? new MiHelpInfo(msg) : null));
		}
	public		MiAttributes		setToolHintHelp(MiiHelpInfo msg)
		{return((MiAttributes )getModifiedAttributes(Mi_TOOL_HINT_HELP, msg)); 	}
	public		MiAttributes		setBalloonHelp(MiiHelpInfo msg)
		{return((MiAttributes )getModifiedAttributes(Mi_BALLOON_HELP, msg)); 		}
	public		MiAttributes		setStatusHelp(MiiHelpInfo msg)
		{return((MiAttributes )getModifiedAttributes(Mi_STATUS_HELP, msg)); 		}
	public		MiAttributes		setDialogHelp(MiiHelpInfo msg)
		{return((MiAttributes )getModifiedAttributes(Mi_DIALOG_HELP, msg)); 		}

	public		MiAttributes		setBorderLook(int look)
		{ return((MiAttributes )getModifiedAttributes(Mi_BORDER_LOOK, look));		}
	public		MiAttributes		setHasBorderHilite(boolean flag)
		{ return((MiAttributes )getModifiedAttributes(Mi_HAS_BORDER_HILITE, flag)); 	}
	public		MiAttributes		setBorderHiliteColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_BORDER_HILITE_COLOR, c));	}
	public		MiAttributes		setBorderHiliteWidth(MiDistance w)
		{ return((MiAttributes )getModifiedAttributes(Mi_BORDER_HILITE_WIDTH, w)); 	}
	public		MiAttributes		setHasShadow(boolean flag)
		{ return((MiAttributes )getModifiedAttributes(Mi_HAS_SHADOW, flag)); 		}
	public		MiAttributes		setShadowColor(Color c)
		{ return((MiAttributes )getModifiedAttributes(Mi_SHADOW_COLOR, c));		}
	public		MiAttributes		setShadowLength(MiDistance w)
		{ return((MiAttributes )getModifiedAttributes(Mi_SHADOW_LENGTH, w));	 	}
	public		MiAttributes		setShadowDirection(int d)
		{ return((MiAttributes )getModifiedAttributes(Mi_SHADOW_DIRECTION, d)); 	}


	public		MiAttributes		setBeforeRenderer(MiiPartRenderer r)
		{ return((MiAttributes )getModifiedAttributes(Mi_BEFORE_RENDERER, r));		}
	public		MiAttributes		setAfterRenderer(MiiPartRenderer r)
		{ return((MiAttributes )getModifiedAttributes(Mi_AFTER_RENDERER, r)); 		}
	public		MiAttributes		setShadowRenderer(MiiShadowRenderer r)
		{ return((MiAttributes )getModifiedAttributes(Mi_SHADOW_RENDERER, r)); 		}
	public		MiAttributes		setLineEndsRenderer(MiiLineEndsRenderer r)
		{ return((MiAttributes )getModifiedAttributes(Mi_LINE_ENDS_RENDERER, r));	}

	public		MiAttributes		setBackgroundRenderer(MiiDeviceRenderer r)
		{ return((MiAttributes )getModifiedAttributes(Mi_BACKGROUND_RENDERER, r)); 	}
	public		MiAttributes		setBorderRenderer(MiiDeviceRenderer r) 
		{ return((MiAttributes )getModifiedAttributes(Mi_BORDER_RENDERER, r)); 		}
	public		MiAttributes		setVisibilityAnimator(MiPartAnimator animator) 
		{ return((MiAttributes )getModifiedAttributes(Mi_VISIBILITY_ANIMATOR, animator)); }


	public		String			getFontName()
		{ return(getFont().getName()); 			}
	public		MiAttributes		setFontName(String name)
		{ return(setFont(getFont().setName(name))); 	}
	public		boolean			isFontBold()
		{ return(getFont().isBold()); 				}
	public		MiAttributes		setFontBold(boolean flag)
		{ return(setFont(getFont().setBold(flag))); 		}
	public		boolean			isFontItalic()
		{ return(getFont().isItalic()); 			}
	public		MiAttributes		setFontItalic(boolean flag)
		{ return(setFont(getFont().setItalic(flag))); 		}
	public		int			getFontPointSize()
		{ return(getFont().getPointSize()); 			}
	public		MiAttributes		setFontPointSize(int size)
		{ return(setFont(getFont().setPointSize(size)));	}
	public		int			getFontHorizontalJustification()
		{ return(getIntegerAttribute(Mi_FONT_HORIZONTAL_JUSTIFICATION)); }
	public		MiAttributes		setFontHorizontalJustification(int j)
		{ return((MiAttributes )getModifiedAttributes(Mi_FONT_HORIZONTAL_JUSTIFICATION, j)); }



					/**------------------------------------------------------
	 				 * Gets whether any geometric attributes are different 
					 * between this and the given 'other' attributes.
					 * @param other 	the other attributes to compare
					 *			this to
					 * @return 		true if any one of the geometric
					 *			attributes has changed
					 * @see			MiiAttributeTypes#geometricAttributesTable
					 *------------------------------------------------------*/
	public		boolean		isGeometricChange(MiAttributes other)
		{
		return(isChange(other, geometricAttributesTable));
		}
					/**------------------------------------------------------
	 				 * Gets whether any appearance attributes are different 
					 * between this and the given 'other' attributes. Appearance
					 * attributes are attributes that only affect the visual
					 * appearance of a MiPart they are assigned to.
					 * @param other 	the other attributes to compare
					 *			this to
					 * @return 		true if any one of the appearance
					 *			attributes has changed
					 * @see			MiiAttributeTypes#appearanceAttributesTable
					 *------------------------------------------------------*/
	public		boolean		isAppearanceChange(MiAttributes other)
		{
		return(isChange(other, appearanceAttributesTable));
		}
					/**------------------------------------------------------
	 				 * Gets whether the attributes specified by the given table
					 * are different between this and the given 'other' attributes.
					 * Geometric attributes are attributes that only affect the 
					 * size of a MiPart they are assigned to.
					 * @param other 	the other attributes to compare
					 *			this to
					 * @param attributeChangeTable
					 *		 	an array of indexes of the attributes
					 *			that will be compared
					 *			this to
					 * @return 		true if any one of the attributes
					 *			specified has changed
					 *------------------------------------------------------*/
	protected	boolean		isChange(MiAttributes other, int[] attributeChangeTable)
		{
		int[] table = attributeChangeTable;
		int length = table.length;

		for (int i = 0; i < length; ++i)
			{
			int index = table[i];
			if (index >= Mi_START_BOOLEAN_INDEX)
				{
				if (booleanAttributes[index - Mi_START_BOOLEAN_INDEX] 
					!= other.booleanAttributes[index - Mi_START_BOOLEAN_INDEX])
					{
//System.out.println("boolean change: " + (index - Mi_START_BOOLEAN_INDEX));
					return(true);
					}
				}
			else if (index >= Mi_START_DOUBLE_INDEX)
				{
				if (doubleAttributes[index - Mi_START_DOUBLE_INDEX] 
					!= other.doubleAttributes[index - Mi_START_DOUBLE_INDEX])
					{
//System.out.println("double change: " + (index - Mi_START_DOUBLE_INDEX));
					return(true);
					}
				}
			else if (index >= Mi_START_INTEGER_INDEX)
				{
				if (intAttributes[index - Mi_START_INTEGER_INDEX] 
					!= other.intAttributes[index - Mi_START_INTEGER_INDEX])
					{
//System.out.println("int change: " + (index - Mi_START_INTEGER_INDEX));
					return(true);
					}
				}
			else
				{
				if (objectAttributes[index] != other.objectAttributes[index])
					{
//System.out.println("object change: " + (index));
					return(true);
					}
				}
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Gets whether the attributes specified by the given table
					 * are different between this and the given 'other' attributes.
					 * Geometric attributes are attributes that only affect the 
					 * size of a MiPart they are assigned to.
					 * @param other 	the other attributes to compare
					 *			this to
					 * @param attributeChangeTable
					 *		 	an array of indexes of the attributes
					 *			that will be compared
					 *			this to
					 * @return 		true if any one of the attributes
					 *			specified has changed
					 *------------------------------------------------------*/
	protected	MiChangedAttributes	getChangedAttributes(MiAttributes oldAtts)
		{
		int numChanges = 0;

		changedAttributes.oldAttributes = oldAtts;
		changedAttributes.newAttributes = this;
		for (int index = 0; index < Mi_NUMBER_OF_ATTRIBUTES; ++index)
			{
			if (index < Mi_START_INTEGER_INDEX)
				{
				if (objectAttributes[index] != oldAtts.objectAttributes[index])
					{
					changedAttributes.changedAttributeIndexes[numChanges++] = index;
					changedAttributes.changedAttributeEvents[index].setPropertyValue(
						objectAttributes[index] == null 
						? Mi_NULL_VALUE_NAME : objectAttributes[index].toString());
					changedAttributes.changedAttributeEvents[index].setOldPropertyValue(
						oldAtts.objectAttributes[index] == null
						? Mi_NULL_VALUE_NAME : oldAtts.objectAttributes[index].toString());
					}
				}
			else if (index < Mi_START_DOUBLE_INDEX)
				{
				if (intAttributes[index - Mi_START_INTEGER_INDEX] 
					!= oldAtts.intAttributes[index - Mi_START_INTEGER_INDEX])
					{
					changedAttributes.changedAttributeIndexes[numChanges++] = index;
					changedAttributes.changedAttributeEvents[index].setPropertyValue(
						intAttributes[index - Mi_START_INTEGER_INDEX] + "");
					changedAttributes.changedAttributeEvents[index].setOldPropertyValue(
						oldAtts.intAttributes[index - Mi_START_INTEGER_INDEX] + "");
					}
				}
			else if (index < Mi_START_BOOLEAN_INDEX)
				{
				if (doubleAttributes[index - Mi_START_DOUBLE_INDEX] 
					!= oldAtts.doubleAttributes[index - Mi_START_DOUBLE_INDEX])
					{
					changedAttributes.changedAttributeIndexes[numChanges++] = index;
					changedAttributes.changedAttributeEvents[index].setPropertyValue(
						doubleAttributes[index - Mi_START_DOUBLE_INDEX] + "");
					changedAttributes.changedAttributeEvents[index].setOldPropertyValue(
						oldAtts.doubleAttributes[index - Mi_START_DOUBLE_INDEX] + "");
					}
				}
			else
				{
				if (booleanAttributes[index - Mi_START_BOOLEAN_INDEX] 
					!= oldAtts.booleanAttributes[index - Mi_START_BOOLEAN_INDEX])
					{
					changedAttributes.changedAttributeIndexes[numChanges++] = index;
					changedAttributes.changedAttributeEvents[index].setPropertyValue(
						booleanAttributes[index - Mi_START_BOOLEAN_INDEX] 
						? Mi_TRUE_NAME : Mi_FALSE_NAME);
					changedAttributes.changedAttributeEvents[index].setOldPropertyValue(
						oldAtts.booleanAttributes[index - Mi_START_BOOLEAN_INDEX] 
						? Mi_TRUE_NAME : Mi_FALSE_NAME);
					}
				}
			}
		return(changedAttributes);
		}
					// for each att == mask.att, replace with paint.att
	protected	MiAttributes	complexXor(MiAttributes mask, MiAttributes paint)
		{
		if (this.equals(mask))
			return(paint);

		complexXorResult.directCopy(this);
		for (int index = 0; index < Mi_NUMBER_OF_ATTRIBUTES; ++index)
			{
			if (index < Mi_START_INTEGER_INDEX)
				{
				if (objectAttributes[index] == mask.objectAttributes[index])
					{
					complexXorResult.objectAttributes[index] = paint.objectAttributes[index];
					}
				}
			else if (index < Mi_START_DOUBLE_INDEX)
				{
				if (intAttributes[index - Mi_START_INTEGER_INDEX] 
					== mask.intAttributes[index - Mi_START_INTEGER_INDEX])
					{
					complexXorResult.intAttributes[index - Mi_START_INTEGER_INDEX] 
						= paint.intAttributes[index - Mi_START_INTEGER_INDEX];
					}
				}
			else if (index < Mi_START_BOOLEAN_INDEX)
				{
				if (doubleAttributes[index - Mi_START_DOUBLE_INDEX] 
					== mask.doubleAttributes[index - Mi_START_DOUBLE_INDEX])
					{
					complexXorResult.doubleAttributes[index - Mi_START_DOUBLE_INDEX] 
						= paint.doubleAttributes[index - Mi_START_DOUBLE_INDEX];
					}
				}
			else
				{
				if (booleanAttributes[index - Mi_START_BOOLEAN_INDEX] 
					== mask.booleanAttributes[index - Mi_START_BOOLEAN_INDEX])
					{
					complexXorResult.booleanAttributes[index - Mi_START_BOOLEAN_INDEX] 
						= paint.booleanAttributes[index - Mi_START_BOOLEAN_INDEX];
					}
				}
			}
		complexXorResult.setIsImmutable(true);
		MiAttributes atts = (MiAttributes )complexXorResult.getImmutableCopyFromCache();
		complexXorResult.setIsImmutable(false);
		return(atts);
		}
					/**------------------------------------------------------
	 				 * Gets the number of MiAttributes created and increases
					 * the number at the same time.
					 * @return 		the number of MiAttributes created
					 * @overrides 		MiGeneralAttributes#getAndIncNumCreated
					 *------------------------------------------------------*/
	protected	int		getAndIncNumCreated()
		{
		return(numCreated++);
		}
					/**------------------------------------------------------
	 				 * Gets the cache that stores reusable, immutable 
					 * MiAttributes.
					 * @return 		the attribute cache
					 * @overrides 		MiGeneralAttributes#getCache
					 *------------------------------------------------------*/
	protected	MiAttributeCache getCache()
		{
		return(cache);
		}
					/**------------------------------------------------------
	 				 * Makes the default attributes whose values are copied
					 * into all newly created attributes.
					 * @param makeDefaultAttributes not used
					 *------------------------------------------------------*/
	private				MiAttributes(int makeDefaultAttributes)
		{
		super(
			Mi_NUMBER_OF_ATTRIBUTES, 
			Mi_START_OBJECT_INDEX, Mi_NUM_OBJECT_INDEXES,
			Mi_START_INTEGER_INDEX, Mi_NUM_INTEGER_INDEXES,
			Mi_START_DOUBLE_INDEX, Mi_NUM_DOUBLE_INDEXES,
			Mi_START_BOOLEAN_INDEX, Mi_NUM_BOOLEAN_INDEXES);

		// Bug in 1.2.1 makes standard fonts twice as tall as they should be
		if (MiSystem.getJDKVersion() >= 1.2)
			defaultFont = new MiFont("Courier", MiFont.PLAIN, 12);

		super.setStaticAttribute(Mi_FONT, defaultFont);


		super.setStaticAttribute(Mi_COLOR, MiColorManager.black);
		super.setStaticAttribute(Mi_BACKGROUND_COLOR, Mi_TRANSPARENT_COLOR);
		super.setStaticAttribute(Mi_LIGHT_COLOR, MiColorManager.darkWhite);
		super.setStaticAttribute(Mi_WHITE_COLOR, MiColorManager.white);
		super.setStaticAttribute(Mi_DARK_COLOR, MiColorManager.gray);
		super.setStaticAttribute(Mi_BLACK_COLOR, MiColorManager.darkGray);
		super.setStaticAttribute(Mi_XOR_COLOR, MiColorManager.red);
		super.setStaticAttribute(Mi_BORDER_HILITE_COLOR, MiColorManager.black);
		super.setStaticAttribute(Mi_SHADOW_COLOR, MiColorManager.lightGray);

		super.setStaticAttribute(Mi_BORDER_LOOK, Mi_FLAT_BORDER_LOOK);
		super.setStaticAttribute(Mi_BORDER_HILITE_WIDTH, 2.0);
		super.setStaticAttribute(Mi_SHADOW_LENGTH, 4.0);
		super.setStaticAttribute(Mi_SHADOW_DIRECTION, Mi_LOWER_RIGHT_LOCATION);
		super.setStaticAttribute(Mi_FONT_HORIZONTAL_JUSTIFICATION, Mi_LEFT_JUSTIFIED);
		super.setStaticAttribute(Mi_FONT_VERTICAL_JUSTIFICATION, Mi_CENTER_JUSTIFIED);
		super.setStaticAttribute(Mi_CONTEXT_CURSOR, Mi_DEFAULT_CURSOR);

		super.setStaticAttribute(Mi_DELETABLE, true);
		super.setStaticAttribute(Mi_MOVABLE, true);
		super.setStaticAttribute(Mi_COPYABLE, true);
		super.setStaticAttribute(Mi_COPYABLE_AS_PART_OF_COPYABLE, true);
		super.setStaticAttribute(Mi_SELECTABLE, true);
		super.setStaticAttribute(Mi_PICKABLE, true);
		super.setStaticAttribute(Mi_UNGROUPABLE, true);
		super.setStaticAttribute(Mi_CONNECTABLE, true);
		super.setStaticAttribute(Mi_ACCEPTS_TAB_KEYS, true);
		super.setStaticAttribute(Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH, true);
		super.setStaticAttribute(Mi_PRINTABLE, true);
		super.setStaticAttribute(Mi_SAVABLE, true);
		super.setStaticAttribute(Mi_PICKABLE_WHEN_TRANSPARENT, true);
		super.setStaticAttribute(Mi_SNAPPABLE, true);

		super.setStaticAttribute(Mi_MAXIMUM_WIDTH, Mi_MAX_DISTANCE_VALUE);
		super.setStaticAttribute(Mi_MAXIMUM_HEIGHT, Mi_MAX_DISTANCE_VALUE);

		super.setStaticAttribute(Mi_LINE_START_SIZE, 10.0);
		super.setStaticAttribute(Mi_LINE_END_SIZE, 10.0);

		defaultBorderRenderer = new MiBorderLookRenderer();
		super.setStaticAttribute(Mi_BORDER_RENDERER, defaultBorderRenderer);

		// Need to do this in order because LineEndsRenderer wants to make an MiAttributes
		defaultAttributes = this;
		defaultLineEndsRenderer = new MiLineEndsRenderer();
		super.setStaticAttribute(Mi_LINE_ENDS_RENDERER, defaultLineEndsRenderer);

		defaultShadowRenderer = new MiShadowRenderer();
		super.setStaticAttribute(Mi_SHADOW_RENDERER, defaultShadowRenderer);

		initializeAsInheritedAttributes();
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

		propertyDescriptions = new MiPropertyDescriptions("Basic Attribute Properties");

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BACKGROUND_IMAGE_ATT_NAME, "Image"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BACKGROUND_TILE_ATT_NAME, "Image"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FONT_NAME_ATT_NAME, Mi_STRING_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FONT_SIZE_ATT_NAME, Mi_POSITIVE_INTEGER_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FONT_BOLD_ATT_NAME, Mi_BOOLEAN_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FONT_ITALIC_ATT_NAME, Mi_BOOLEAN_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FONT_UNDERLINED_ATT_NAME, Mi_BOOLEAN_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FONT_STRIKEOUT_ATT_NAME, Mi_BOOLEAN_TYPE));


/*
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_TOOL_HINT_HELP_ATT_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BALLOON_HELP_ATT_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_STATUS_HELP_ATT_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_DIALOG_HELP_ATT_NAME, Mi_STRING_TYPE));
*/

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SHADOW_RENDERER_ATT_NAME, "MiiShadowRenderer"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BEFORE_RENDERER_ATT_NAME, "MiiPartRenderer"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_AFTER_RENDERER_ATT_NAME, "MiiPartRenderer"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LINE_ENDS_RENDERER_ATT_NAME, "MiiLineEndsRenderer"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BACKGROUND_RENDERER_ATT_NAME, "MiiDeviceRenderer"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BORDER_RENDERER_ATT_NAME, "MiiDeviceRenderer"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_VISIBILITY_ANIMATOR_ATT_NAME, "MiPartAnimator"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_CONTEXT_MENU_NAME, "MiiContextMenu"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_COLOR_ATT_NAME, Mi_COLOR_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BACKGROUND_COLOR_ATT_NAME, Mi_COLOR_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LIGHT_COLOR_ATT_NAME, Mi_COLOR_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_WHITE_COLOR_ATT_NAME, Mi_COLOR_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_DARK_COLOR_ATT_NAME, Mi_COLOR_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BLACK_COLOR_ATT_NAME, Mi_COLOR_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_XOR_COLOR_ATT_NAME, Mi_COLOR_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BORDER_HILITE_COLOR_ATT_NAME, Mi_COLOR_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SHADOW_COLOR_ATT_NAME, Mi_COLOR_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BORDER_LOOK_ATT_NAME, 
				new Strings(MiiNames.borderLookNames)));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LINE_STYLE_ATT_NAME, 
				new Strings(MiiNames.lineStyleNames)));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LINE_START_STYLE_ATT_NAME, 
				new Strings(MiiNames.lineEndStyleNames)));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LINE_END_STYLE_ATT_NAME, 
				new Strings(MiiNames.lineEndStyleNames)));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_WRITE_MODE_ATT_NAME, 
				new Strings(MiiNames.writeModeNames)));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_CONTEXT_CURSOR_ATT_NAME, 
				new Strings(MiiNames.cursorNames)));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ATTRIBUTE_LOCK_MASK_ATT_NAME, Mi_INTEGER_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ATTRIBUTE_PUBLIC_MASK_ATT_NAME, Mi_INTEGER_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SHADOW_DIRECTION_ATT_NAME, 
				new Strings(MiiNames.locationNames)));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SHADOW_STYLE_ATT_NAME, Mi_INTEGER_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FONT_HORIZONTAL_JUSTIFICATION_ATT_NAME, 
				new Strings(MiiNames.horizontalJustificationNames)));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FONT_VERTICAL_JUSTIFICATION_ATT_NAME, 
				new Strings(MiiNames.verticalJustificationNames)));


		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_MINIMUM_WIDTH_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_MINIMUM_HEIGHT_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_MAXIMUM_WIDTH_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_MAXIMUM_HEIGHT_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BORDER_HILITE_WIDTH_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SHADOW_LENGTH_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LINE_WIDTH_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LINE_START_SIZE_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LINE_END_SIZE_ATT_NAME, Mi_POSITIVE_DOUBLE_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_DELETABLE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_MOVABLE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_COPYABLE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_COPYABLE_AS_PART_OF_COPYABLE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SELECTABLE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FIXED_WIDTH_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FIXED_WIDTH_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FIXED_HEIGHT_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FIXED_ASPECT_RATIO_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PICKABLE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_UNGROUPABLE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SNAPPABLE_ATT_NAME, Mi_BOOLEAN_TYPE));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_CONNECTABLE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_HIDDEN_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_DRAG_AND_DROP_SOURCE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_DRAG_AND_DROP_TARGET_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ACCEPTS_MOUSE_FOCUS_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ACCEPTS_KEYBOARD_FOCUS_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ACCEPTS_ENTER_KEY_FOCUS_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ACCEPTS_TAB_KEYS_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_HAS_BORDER_HILITE_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_HAS_SHADOW_ATT_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH_ATT_NAME, 
				Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PICKABLE_WHEN_TRANSPARENT_ATT_NAME, 
				Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_FILLED_ATT_NAME, 
				Mi_BOOLEAN_TYPE));

		for (int i = 0; i  < propertyDescriptions.size(); ++i)
			{
			MiPropertyDescription desc = propertyDescriptions.elementAt(i);
			String defaultValue = defaultAttributes.getAttributeValue(desc.getName());
			desc.setDefaultValue(defaultValue);
			}
		return(propertyDescriptions);
		}
	}
class MiChangedAttributes implements MiiAttributeTypes
	{
	protected	MiAttributes		oldAttributes;
	protected	MiAttributes		newAttributes;
	protected	int[]			changedAttributeIndexes;
	protected static MiPropertyChange[]	changedAttributeEvents;

	public					MiChangedAttributes()
		{
		changedAttributeIndexes = new int[Mi_NUMBER_OF_ATTRIBUTES];
		}

	static	{
		changedAttributeEvents = new MiPropertyChange[Mi_NUMBER_OF_ATTRIBUTES];
		for (int i = 0; i < Mi_NUMBER_OF_ATTRIBUTES; ++i)
			{
			changedAttributeEvents[i] = new MiPropertyChange(null, MiiNames.attributeNames[i] , null, null);
			}
		}
	}

