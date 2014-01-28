
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public abstract class MiPropertyPanel extends MiWidget
	{
	public static final String	Mi_CHANGE_WHICH_OBJECT_INSPECTED_NAME	
						= "changeWhichObjectInspected";	
	public static final String	Mi_REFRESH_INSPECTED_OBJECT_DISPLAY_NAME	
						= "refreshInspectedObjectDisplay";	
	public static final int		Mi_CHANGE_WHICH_OBJECT_INSPECTED_ACTION
						= MiActionManager.registerAction(
							Mi_CHANGE_WHICH_OBJECT_INSPECTED_NAME);
	public static final int		Mi_REFRESH_INSPECTED_OBJECT_DISPLAY_ACTION
						= MiActionManager.registerAction(
							Mi_REFRESH_INSPECTED_OBJECT_DISPLAY_NAME);

	private		String			inspectedObject;
	private		Strings			inspectedObjects;
	private		MiPropertyWidgets	properties;


	public				MiPropertyPanel()
		{
		setStatusHelp(MiHelpInfo.ignoreThis);
		}

					// Return -1 if none or all
	public abstract int		getInspectedObjectIndex();
	protected abstract void		generatePanel();

	public		void		setPossibleInspectedObjects(Strings names)
		{
		inspectedObjects = names;
		}
	public		Strings		getPossibleInspectedObjects()
		{
		return(inspectedObjects);
		}
	public		void		setInspectedObject(String name)
		{
		inspectedObject = name;
		}
	public 		String		getInspectedObject()
		{
		return(inspectedObject);
		}

	public		MiPropertyWidgets	getPropertyWidgets()
		{
		return(properties);
		}
	public		void		setPropertyWidgets(MiPropertyWidgets widgets)
		{
		properties = widgets;
		properties.createWidgetsFromSpecification();
		generatePanel();
		}

	// -----------------------------------------------------------------------
	//	Control 
	// -----------------------------------------------------------------------
	public		void		open()
		{
		update();
		setVisible(true);
		}
	public		void		close()
		{
		setVisible(false);
		}
	public		void		update()
		{
		if (getInspectedObject() != null)
			dispatchAction(Mi_REFRESH_INSPECTED_OBJECT_DISPLAY_ACTION);
		}

	public		MiPropertyWidget	getPropertyWidget(String name)
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasName(name))
				return(properties.elementAt(i));
			}
		return(null);
		}

	public		MiPropertyWidget	getPropertyWidget(int index)
		{
		return(properties.elementAt(index));
		}

	// -----------------------------------------------------------------------
	//	Get and set property values of current inspectedObject
	// -----------------------------------------------------------------------
	public		void		setPropertyValues(Strings values)
		{
		for (int i = 0; i < values.size(); ++i)
			{
			setPropertyValue(values.elementAt(i));
			}
		}
	public		void		setPropertyValue(String nameAndValue)
		{
		int index = nameAndValue.indexOf('=');
		if (index == -1)
			throw new IllegalArgumentException("MiPropertyPanel.setPropertyValue - expected \'name=value\' format in: " + nameAndValue);
		String name = nameAndValue.substring(0, index);
		String value = nameAndValue.substring(index);
		setPropertyValue(name, value);
		}
	public		void		setPropertyValue(String name, String value)
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasName(name))
				properties.elementAt(i).setValue(value);
			}
		}
	public		void		setUndoablePropertyValue(String name, String value)
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasName(name))
				properties.elementAt(i).setUndoableValue(value);
			}
		}
	public		void		setPossiblePropertyValues(String name, Strings values)
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasName(name))
				properties.elementAt(i).setContents(values);
			}
		}
	public		String		getPropertyValue(String name)
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasName(name))
				{
				return(properties.elementAt(i).getValue());
				}
			}
		return(null);
		}
	public		boolean		hasProperty(String name)
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasName(name))
				return(true);
			}
		return(false);
		}
	public		Strings		getProperties()
		{
		Strings values = new Strings();
		for (int i = 0; i < properties.size(); ++i)
			{
			values.addElement(properties.elementAt(i).getName());
			}
		return(values);
		}
	public		Strings		getPropertyValues()
		{
		Strings values = new Strings();
		for (int i = 0; i < properties.size(); ++i)
			{
			MiPropertyWidget property = properties.elementAt(i);
			values.addElement(property.getName() + "=" + property.getValue());
			}
		return(values);
		}

	public		Strings		getChangedValues()
		{
		Strings values = new Strings();
		for (int i = 0; i < properties.size(); ++i)
			{
			MiPropertyWidget property = properties.elementAt(i);
			if (property.hasChanged())
				values.addElement(property.getName() + "=" + property.getValue());
			}
		return(values);
		}

	public		Strings		getChangedProperties()
		{
		Strings values = new Strings();
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasChanged())
				values.addElement(properties.elementAt(i).getName());
			}
		return(values);
		}

	public		boolean		hasChanged()
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasChanged())
				{
				return(true);
				}
			}
		return(false);
		}
	public		void		undo()
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			properties.elementAt(i).undo();
			}
		}
	public		void		copyWidgetValuesToRevertCache()
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			properties.elementAt(i).copyWidgetValueToRevertCache();
			}
		}
	public		boolean		hasValidationErrors()
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			if (properties.elementAt(i).hasValidationError())
				{
				return(true);
				}
			}
		return(false);
		}
	public		void		clearAllPropertyValidationErrors()
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			properties.elementAt(i).setHasValidationError(false);
			}
		}
	public 		void		setPropertyValidationError(
						String propertyName, 
						String errorMsg, 
						java.awt.Color validationErrorColor)
		{
		MiPropertyWidget widget = getPropertyWidget(propertyName);
		if (errorMsg == null)
			{
			widget.setHasValidationError(false);
			}
		else
			{
			widget.setHasValidationError(true);
			widget.getWidget().setBackgroundColor(validationErrorColor);
			MiAttributes atts = new MiAttributes();
			atts = atts.setBackgroundColor(validationErrorColor);
			widget.getWidget().setStatusHelp(new MiHelpInfo(errorMsg, atts));
			}
		}
	}

