
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

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiBasicPageBorderGraphicsGenerator implements MiiPageBorderGraphicsGenerator, MiiNames // , MiiPropertyChangeHandler
	{
	public static final String	NAME		= "Basic";

	private		MiiModelEntity	properties;
	private		MiRectangle 	rect;



	public				MiBasicPageBorderGraphicsGenerator()
		{
		}

	public		MiPart		getPageBorderGraphics(MiDrawingPages drawingPages, MiDistance width, MiDistance height, int pageNumber)
		{
		rect = new MiRectangle(0, 0, width, height);
		rect.setPreferredSize(rect.getSize(new MiSize()));
		MiPropertyDescriptions propertyDescs = getModel().getPropertyDescriptions();
		for (int i = 0; i < propertyDescs.size(); ++i)
			{
			String propertyName = propertyDescs.elementAt(i).getName();
			String micaPropertyName = propertyName;
			if ((!Mi_BORDER_LOOK_ATT_NAME.equals(propertyName))
				&& (propertyName.toUpperCase().startsWith(MI_BORDER_ATT_NAME_PREFIX_NSP.toUpperCase())))
				{
				micaPropertyName = propertyName.substring(MI_BORDER_ATT_NAME_PREFIX_NSP.length());
				}
			rect.setPropertyValue(micaPropertyName, properties.getPropertyValue(propertyName));
			}
		rect.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		rect.setHasShadow(false);
		return(rect);
		}
	public		MiiModelEntity	getModel()
		{
		if (properties != null)
			{
			return(properties);
			}
		properties = new MiModelEntity();
//		properties.setIgnoreCase(true);
		properties.setName(NAME);
		properties.setPropertyValue(Mi_BORDER_LOOK_ATT_NAME, Mi_FLAT_BORDER_LOOK_NAME);
		properties.setPropertyValue(MI_BORDER_ATT_NAME_PREFIX + Mi_LINE_WIDTH_ATT_NAME, "0");
		properties.setPropertyValue(MI_BORDER_ATT_NAME_PREFIX + Mi_COLOR_ATT_NAME, "black");
		properties.setPropertyValue(MI_BORDER_ATT_NAME_PREFIX + Mi_VISIBLE_NAME, "true");
		properties.setPropertyValue(MI_BORDER_ATT_NAME_PREFIX + Mi_PRINTABLE_NAME, "false");

// MiDebug.println(MiModelEntity.dump(properties));

		MiPropertyDescriptions propertyDescriptions = new MiPropertyDescriptions(getClass().getName());

/*
		MiPropertyDescription desc = new MiPropertyDescription(
			Mi_NAME_NAME, MiiPropertyTypes.Mi_STRING_TYPE, NAME);
		desc.setEditable(false);
		propertyDescriptions.addElement(desc);
*/

		MiPropertyDescription desc = new MiPropertyDescription(
			MI_BORDER_ATT_NAME_PREFIX + Mi_VISIBLE_NAME, MiiPropertyTypes.Mi_BOOLEAN_TYPE, "true");
		propertyDescriptions.addElement(desc);

		desc = new MiPropertyDescription(
			MI_BORDER_ATT_NAME_PREFIX + Mi_PRINTABLE_NAME, MiiPropertyTypes.Mi_BOOLEAN_TYPE, "false");
		propertyDescriptions.addElement(desc);

		desc = new MiPropertyDescription(
			MI_BORDER_ATT_NAME_PREFIX + Mi_COLOR_ATT_NAME, MiiPropertyTypes.Mi_COLOR_TYPE, "black");
		propertyDescriptions.addElement(desc);

		desc = new MiPropertyDescription(
			MI_BORDER_ATT_NAME_PREFIX + Mi_LINE_WIDTH_ATT_NAME, MiiPropertyTypes.Mi_POSITIVE_DOUBLE_TYPE, "0");
		propertyDescriptions.addElement(desc);

/*
		desc = new MiPropertyDescription(
			Mi_BORDER_LOOK_ATT_NAME, new Strings(borderLookNames), "Mi_FLAT_BORDER_LOOK_NAME");
		propertyDescriptions.addElement(desc);
*/

		properties.setPropertyDescriptions(propertyDescriptions);


/*
		properties.appendPropertyChangeHandler(
				this, new Strings(MiiPropertyTypes.Mi_ANY_PROPERTY), 
				MiiModelEntity.Mi_MODEL_CHANGE_COMMIT_PHASE.getMask());
*/

		return(properties);
		}
					/**------------------------------------------------------
	 				 * Process the change of a property of this editor's
					 * model (which comprises the preferences).
					 * @param event		the change event
					 *------------------------------------------------------*/
/***
	public		void		processPropertyChange(MiPropertyChange event)
		{
		setPropertyValue(event.getPropertyName(), event.getPropertyValue());
		}

	protected	void		setPropertyValue(String name, String value)
		{
		if (rect != null)
			{
			String micaPropertyName = name;
			if ((!Mi_BORDER_LOOK_ATT_NAME.equals(name))
				&& (name.toUpperCase().startsWith(MI_BORDER_ATT_NAME_PREFIX_NSP.toUpperCase())))
				{
				micaPropertyName = name.substring(MI_BORDER_ATT_NAME_PREFIX_NSP.length());
				}
			rect.setPropertyValue(micaPropertyName, value);
			}
		}
*/
	}

