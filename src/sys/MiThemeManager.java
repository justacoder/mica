
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
import com.swfm.mica.util.OrderedProperties;
import com.swfm.mica.util.FastVector;
import java.util.Enumeration; 
import java.util.Properties;
import java.util.Hashtable;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.awt.SystemColor;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */

public class MiThemeManager implements MiiToolkit
	{
	private		FastVector	originalThemeProperties = new FastVector();
	private		MiParts		preservedParts	 	= new MiParts();
	private		MiParts		preservedContents	= new MiParts();
	private		MiParts		extraAssociatedParts	 = new MiParts();
	private		boolean		partDefaultAttributesChanged;
	private		boolean		widgetDefaultAttributesChanged;
	private		MiAttributes	oldPartDefaultAtts;
	private		MiAttributes	newPartDefaultAtts;
	private		MiWidgetAttributes	oldWidgetDefaultAtts;
	private		MiWidgetAttributes	newWidgetDefaultAtts;
	private		Hashtable	results			= new Hashtable();
	private		MiParts		associatedParts		= new MiParts();




	public				MiThemeManager()
		{
		}

					// Preserve LAF of parts in list
	public		void		setPreservedParts(MiParts parts)
		{
		preservedParts.removeAllElements();
		preservedParts.append(parts);
		}
	public		MiParts		getPreservedParts()
		{
		return(new MiParts(preservedParts));
		}
					// Preserve LAF of contents of parts in list
	public		void		setPreservedContents(MiParts parts)
		{
		preservedContents.removeAllElements();
		preservedContents.append(parts);
		}
	public		MiParts		getPreservedContents()
		{
		return(new MiParts(preservedContents));
		}
					// Apply LAF to parts in list which are not found
					// in ordinary parts of, attachments of or associated parts of 
					// target windows
	public		void		setExtraAssociatedParts(MiParts parts)
		{
		extraAssociatedParts.removeAllElements();
		extraAssociatedParts.append(parts);
		}
	public		MiParts		getExtraAssociatedParts()
		{
		return(new MiParts(extraAssociatedParts));
		}
	public		void		applyTheme(String filename)
		{
		MiTheme theme = new MiTheme();
		theme.load(filename);
		applyTheme(theme);
		}
	public		void		applyTheme(OrderedProperties properties)
		{
		MiTheme theme = new MiTheme();
		theme.setSystemProperties(properties);
		applyTheme(theme);
		}
					// Input properties like: Mi_IMAGES_HOME
	public		void		applyNativeTheme(OrderedProperties properties)
		{
		properties.setProperty(Mi_TOOLKIT_WINDOW_PROPERTY_NAME + ".backgroundColor", 
						MiColorManager.getColorName(SystemColor.window));
		properties.setProperty(Mi_TOOLKIT_WINDOW_TEXT_PROPERTY_NAME + ".color", 
						MiColorManager.getColorName(SystemColor.windowText));
		properties.setProperty(Mi_TOOLKIT_MENU_PROPERTY_NAME + ".backgroundColor", 
						MiColorManager.getColorName(SystemColor.menu));
		properties.setProperty(Mi_TOOLKIT_MENU_TEXT_PROPERTY_NAME + ".color", 
						MiColorManager.getColorName(SystemColor.menuText));
		properties.setProperty(Mi_TOOLKIT_TEXT_PROPERTY_NAME + ".backgroundColor", 
						MiColorManager.getColorName(SystemColor.text));
		properties.setProperty(Mi_TOOLKIT_TEXT_PROPERTY_NAME + ".color", 
						MiColorManager.getColorName(SystemColor.textText));
		properties.setProperty(Mi_TOOLKIT_TEXT_SELECTED_PROPERTY_NAME + ".backgroundColor", 
						MiColorManager.getColorName(SystemColor.textHighlight));
		properties.setProperty(Mi_TOOLKIT_TEXT_SELECTED_PROPERTY_NAME + ".color", 
						MiColorManager.getColorName(SystemColor.textHighlightText));
		properties.setProperty(Mi_TOOLKIT_TEXT_INSENSITIVE_PROPERTY_NAME + ".color", 
						MiColorManager.getColorName(SystemColor.textInactiveText));

		properties.setProperty("MiToolkit.backgroundColor", 
						MiColorManager.getColorName(SystemColor.control));
		properties.setProperty("MiToolkit.whiteColor", 
						MiColorManager.getColorName(SystemColor.controlHighlight));
		properties.setProperty("MiToolkit.lightColor", 
						MiColorManager.getColorName(SystemColor.controlLtHighlight));
		properties.setProperty("MiToolkit.blackColor", 
						MiColorManager.getColorName(SystemColor.controlShadow));
		properties.setProperty("MiToolkit.darkColor", 
						MiColorManager.getColorName(SystemColor.controlDkShadow));
		properties.setProperty("MiScrollBar.backgroundColor", 
						MiColorManager.getColorName(SystemColor.scrollbar));
		properties.setProperty("MiScrollBar.selected.backgroundColor", 
						MiColorManager.getColorName(SystemColor.scrollbar));

//System.out.println("Native proeprties = "+ properties);
		applyTheme(properties);
		}


	public		void		applyTheme(MiTheme theme)
		{
		restoreOriginalTheme();
		MiSystem.flushThemeProperties();

		results.clear();

		oldPartDefaultAtts = MiPart.getDefaultAttributes();
		oldWidgetDefaultAtts = MiToolkit.getStandardWidgetAttributes();

		originalThemeProperties.addElement(new MiOriginalThemeProperty(
							MiAttributes.getDefaultAttributes(),
							oldPartDefaultAtts, oldWidgetDefaultAtts));

		OrderedProperties properties = theme.getSystemProperties();
		Strings names = properties.getKeys();
		String oldValue;
		for (int i = 0; i < names.size(); ++i)
			{
			String name = names.elementAt(i);
			if (name.startsWith(MiSystem.Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME))
				{
				oldValue = MiPart.getDefaultAttributes().getAttributeValue(
					name.substring(
					MiSystem.Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME.length() + 1));
				}
			else if (name.startsWith(MiToolkit.Mi_DEFAULT_WIDGET_ATTRIBUTES_PROPERTY_NAME))
				{
				oldValue = MiToolkit.getDefaultWidgetAttributeProperty(name);
				}
			else
				{
				oldValue = MiSystem.getProperty(name);
				}

			setPreviousThemeProperty(name, oldValue);

			MiSystem.setProperty(name, properties.getProperty(name));
			}

		refreshDefaultAttributesFromTheme();

		newPartDefaultAtts = MiPart.getDefaultAttributes();
		newWidgetDefaultAtts = MiToolkit.getStandardWidgetAttributes();

		partDefaultAttributesChanged = !newPartDefaultAtts.equals(oldPartDefaultAtts);
		widgetDefaultAttributesChanged = !newWidgetDefaultAtts.equals(oldWidgetDefaultAtts);

		associatedParts.removeAllElements();
		associatedParts.append(extraAssociatedParts);

		MiParts windows = MiSystem.getWindows();
		for (int i = 0; i < windows.size(); ++i)
			{
			if (!preservedParts.contains(windows.elementAt(i)))
				{
				windows.elementAt(i).invalidateArea();
				refreshFromTheTheme(windows.elementAt(i));
				}
			}
		for (int i = 0; i < associatedParts.size(); ++i)
			{
			refreshFromTheTheme(associatedParts.elementAt(i));
			}
		}
	public		void		applyThemeToPart(MiPart container)
		{
		// If any theme has been applied yet....
		if ((oldPartDefaultAtts != null) && (oldWidgetDefaultAtts != null))
			{
			newPartDefaultAtts = MiPart.getDefaultAttributes();
			newWidgetDefaultAtts = MiToolkit.getStandardWidgetAttributes();

			partDefaultAttributesChanged = !newPartDefaultAtts.equals(oldPartDefaultAtts);
			widgetDefaultAttributesChanged = !newWidgetDefaultAtts.equals(oldWidgetDefaultAtts);

			associatedParts.removeAllElements();

			refreshFromTheTheme(container);

			for (int i = 0; i < associatedParts.size(); ++i)
				{
				refreshFromTheTheme(associatedParts.elementAt(i));
				}
			}
		}
	protected	void		refreshDefaultAttributesFromTheme()
		{
		if (MiSystem.getPropertiesForClass(MiSystem.Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME).size() > 0)
			{
			MiAttributes defaults = MiSystem.getPropertyAttributes(
							MiSystem.Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME);
			MiAttributes.setDefaultAttributes(MiAttributes.getDefaultAttributes().overrideFrom(defaults));
			MiPart.setDefaultAttributes(MiPart.getDefaultAttributes().overrideFrom(defaults));

			MiWidgetAttributes atts = MiToolkit.getStandardWidgetAttributes();
			atts.normalAttributes		= atts.normalAttributes.overrideFrom(defaults);
			atts.inSensitiveAttributes	= atts.inSensitiveAttributes.overrideFrom(defaults);
			atts.selectedAttributes		= atts.selectedAttributes.overrideFrom(defaults);
			atts.keyboardFocusAttributes	= atts.keyboardFocusAttributes.overrideFrom(defaults);
			atts.enterKeyFocusAttributes	= atts.enterKeyFocusAttributes.overrideFrom(defaults);
			atts.mouseFocusAttributes	= atts.mouseFocusAttributes.overrideFrom(defaults);
			MiToolkit.setStandardWidgetAttributes(atts);
			}
		MiToolkit.refreshLookAndFeel();
		}
	protected	void		refreshFromTheTheme(MiPart container)
		{
		if (preservedParts.contains(container))
			return;

		// For each attribute, check to see if equal to "previous" default value
		// If so, set to new default value.
		if ((partDefaultAttributesChanged) && (!(container instanceof MiWidget)))
			{
			MiAttributes atts = container.getAttributes();
			MiAttributes result = (MiAttributes )results.get(atts);
			if (result == null)
				{
				result = atts.complexXor(oldPartDefaultAtts, newPartDefaultAtts);
				results.put(atts, result);
				}
			if (result != atts)
				{
				setPreviousThemeProperty(container, atts);
				container.setAttributes(result);
				}
			}
		if ((widgetDefaultAttributesChanged) && (container instanceof MiWidget))
			{
			MiWidget w = (MiWidget )container;

			MiWidgetAttributes atts = w.getWidgetAttributes();
			MiWidgetAttributes result = (MiWidgetAttributes )results.get(atts);
			if (result == null)
				{
				result = new MiWidgetAttributes();
				result.normalAttributes = atts.normalAttributes.complexXor(
								oldWidgetDefaultAtts.normalAttributes, 
								newWidgetDefaultAtts.normalAttributes);
				result.inSensitiveAttributes = atts.inSensitiveAttributes.complexXor(
								oldWidgetDefaultAtts.inSensitiveAttributes, 
								newWidgetDefaultAtts.inSensitiveAttributes);
				result.selectedAttributes = atts.selectedAttributes.complexXor(
								oldWidgetDefaultAtts.selectedAttributes, 
								newWidgetDefaultAtts.selectedAttributes);
				result.keyboardFocusAttributes = atts.keyboardFocusAttributes.complexXor(
								oldWidgetDefaultAtts.keyboardFocusAttributes, 
								newWidgetDefaultAtts.keyboardFocusAttributes);
				result.enterKeyFocusAttributes = atts.enterKeyFocusAttributes.complexXor(
								oldWidgetDefaultAtts.enterKeyFocusAttributes, 
								newWidgetDefaultAtts.enterKeyFocusAttributes);
				result.mouseFocusAttributes = atts.mouseFocusAttributes.complexXor(
								oldWidgetDefaultAtts.mouseFocusAttributes, 
								newWidgetDefaultAtts.mouseFocusAttributes);
				results.put(atts, result);
				}
			if (!results.equals(atts))
				{
				setPreviousThemeProperty(w, atts);
				w.setWidgetAttributes(result);
				}
			}

		container.refreshLookAndFeel();
		if (!preservedContents.contains(container))
			{
			for (int i = 0; i < container.getNumberOfParts(); ++i)
				refreshFromTheTheme(container.getPart(i));
			}
		for (int i = 0; i < container.getNumberOfAttachments(); ++i)
			refreshFromTheTheme(container.getAttachment(i));

		container.getAssociatedParts(associatedParts);
		}
	protected	void		savePreviousPropertyValues(
						MiPart part, String className, Properties propertyNames)
		{
		//className = className + ".";
		for (Enumeration e = propertyNames.keys(); e.hasMoreElements();)
			{
			String name = (String)e.nextElement();
			String value = part.getPropertyValue(name);
			setPreviousThemeProperty(part, name, value);
			}
		}
	public		void		setPreviousThemeProperty(String name, String value)
		{
		MiOriginalThemeProperty p = new MiOriginalThemeProperty(name, value);
		originalThemeProperties.addElement(p);
		}
	public		void		setPreviousThemeProperty(MiPart part, String name, String value)
		{
		MiOriginalThemeProperty p = new MiOriginalThemeProperty(part, name, value);
		originalThemeProperties.addElement(p);
		}
	public		void		setPreviousThemeProperty(MiPart part, MiAttributes atts)
		{
		MiOriginalThemeProperty p = new MiOriginalThemeProperty(part, atts);
		originalThemeProperties.addElement(p);
		}
	public		void		setPreviousThemeProperty(MiPart part, MiWidgetAttributes atts)
		{
		MiOriginalThemeProperty p = new MiOriginalThemeProperty(part, atts);
		originalThemeProperties.addElement(p);
		}

	public		void		restoreOriginalTheme()
		{
//System.out.println("RESTORE -----------------------------------" + originalThemeProperties.size());
		for (int i = originalThemeProperties.size() - 1; i >= 0; --i)
			{
			MiOriginalThemeProperty p 
				= (MiOriginalThemeProperty )originalThemeProperties.elementAt(i);
			p.restore();
			}
		originalThemeProperties.removeAllElements();
		}
	}
class MiTheme
	{
	private		String		name;
	private		OrderedProperties properties = new OrderedProperties();

	public				MiTheme()
		{
		}
	public		OrderedProperties getSystemProperties()
		{
		return(properties);
		}
	public		void 		setSystemProperties(OrderedProperties p)
		{
		properties = p;
		}
	public		void		load(String filename)
		{
		name = filename;
		File file = new File(filename);
		try	{
			if (file.exists() && (file.isFile()))
				load(new FileInputStream(file));
			}
		catch (Exception e)
			{
			MiDebug.println("Error occured during read of properties file: " 
				+ file.getPath());
			}
		}
	public		void		load(InputStream stream)
		{
		try	{
			properties.load(stream);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	}
class MiOriginalThemeProperty
	{
	private		MiPart		target;
	private		String		propertyName;
	private		String		propertyValue;
	private		MiAttributes 	attDefaults;
	private		MiAttributes 	partDefaults;
	private		MiWidgetAttributes widgetDefaults;
	private		MiAttributes 	partAtts;
	private		MiWidgetAttributes widgetAtts;


	public				MiOriginalThemeProperty(
						MiPart target, String propertyName, String propertyValue)
		{
		this.target = target;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
//System.out.println("SAVE target = " + target);
//System.out.println("propertyName = " + propertyName);
//System.out.println("propertyValue = " + propertyValue);
		}
	public				MiOriginalThemeProperty(String propertyName, String propertyValue)
		{
//System.out.println("SAVE pure property--------");
//System.out.println("propertyName = " + propertyName);
//System.out.println("propertyValue = " + propertyValue);
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
		}
	public				MiOriginalThemeProperty(
						MiAttributes attDefaults,
						MiAttributes partDefaults,
						MiWidgetAttributes widgetDefaults)
		{
		this.attDefaults = attDefaults;
		this.partDefaults = partDefaults;
		this.widgetDefaults = widgetDefaults;
		}
	public				MiOriginalThemeProperty(
						MiPart target, MiAttributes partAtts)
		{
		this.target = target;
		this.partAtts = partAtts;
		}
	public				MiOriginalThemeProperty(
						MiPart target, MiWidgetAttributes widgetAtts)
		{
		this.target = target;
		this.widgetAtts = widgetAtts;
		}

	public		void		restore()
		{
		if (target != null)
			{
			if (partAtts != null)
				target.setAttributes(partAtts);
			else if (widgetAtts != null)
				((MiWidget )target).setWidgetAttributes(widgetAtts);
			else
				target.setPropertyValue(propertyName, propertyValue);
//System.out.println("RESTORE Table PROPERTY------" + target);
//System.out.println("propertyName = " + propertyName);
//System.out.println("propertyValue = " + propertyValue);
			}
		else if (attDefaults != null)
			{
			MiAttributes.setDefaultAttributes(attDefaults);
			MiPart.setDefaultAttributes(partDefaults);
			MiToolkit.setStandardWidgetAttributes(widgetDefaults);
			}
		else
			{
//System.out.println("RESTORE PURE PROPERTY------");
//System.out.println("propertyName = " + propertyName);
//System.out.println("propertyValue = " + propertyValue);
			if (propertyValue == null)
				MiSystem.removeProperty(propertyName);
			else
				MiSystem.setProperty(propertyName, propertyValue);
			}
		}
	}


