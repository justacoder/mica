
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
import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.CaselessKeyHashtable;
import com.swfm.mica.util.OrderedProperties;
import java.awt.Color;
import java.applet.Applet;
import java.applet.AudioClip;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.util.Vector;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.awt.Image;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSystem
	{
	public static final String		Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME = "MiDefaults";
	public static final String		Mi_DEFAULT_TOOLKIT_ATTRIBUTES_PROPERTY_NAME = "MiToolkit";

	public static final String		Mi_MICA_DEFAULT_PROPERTIES_FILENAME = "defaults.mica";
	public static final String		Mi_MICA_PROPERTIES_FILENAME 	= "properties.mica";
	public static final String		Mi_HOME 			= "Mi_HOME";
	public static final String		Mi_IMAGES_HOME 			= "Mi_IMAGES_HOME";
	public static final String		Mi_THEME_HOME 			= "Mi_THEME_HOME";
	public static final String		Mi_CURRENT_DIRECTORY 		= "Mi_CURRENT_DIRECTORY";
	public static final String		Mi_PALETTES_DIRECTORY 		= "Mi_PALETTES_DIRECTORY";
	public static final String		Mi_APPLET_OUTPUTSTREAM_ADDRESS 	= "Mi_APPLET_OUTPUTSTREAM_ADDRESS";
	public static final String		Mi_VERSION 			= "Mi_VERSION";
	public static final String		Mi_LIMIT_TABLE_SIZES 		= "Mi_LIMIT_TABLE_SIZES";

	private static	MiClipBoard		clipBoard;
	private static 	MiTransactionManager	transactionManager;
	private static 	MiTransactionManager	viewportManager;
	private static	MiPrint			printer;
	private static	MiHelpManager		helpManager;
	private static	MiThemeManager		themeManager;
	private static	MiCustomLookAndFeelManager customLookAndFeelManager;
	private static	MiParts			windows;
	private static	Applet			applet;
	private static	boolean			isApplet;
	private static	MiPart			companyLogo;
	private static	Properties		properties;
	private static	Properties		appDefaultProperties;
	private static	Properties		micaDefaultProperties;
	private static	boolean			isSun;
	private static	boolean			isMSWindows;
	private static	boolean			isMSWindowsNT;
	private static	boolean			isPC;
	private static	boolean			isJDK102;
	private static	boolean			isJDK115;
	private static	boolean			isJDK12;
	private static	MiJDKAPIComponentType versionOfAWTorSwingAPItoUse 
						= MiiTypes.Mi_AWT_1_0_2_HEAVYWEIGHT_COMPONENT_TYPE;
	private static	double			jdkVersion;
	private static	boolean			fileNamesIgnoreCase;
	private static	boolean			isThrowingExceptionsWhenPropertyChangeVetoed;
	private static	CaselessKeyHashtable	attributeValueNames;
	private static	CaselessKeyHashtable	propertyValueParts;
	private static	CaselessKeyHashtable	propertyValueColors;
	private static	CaselessKeyHashtable	propertyValueAttributes;
	private static	Hashtable		classProperties;
	private static	MiiSystemIOManager	ioManager;
	private static	MiiCommandBuilder	commandBuilder;


	public static	void			init()
		{
		new MiSystem(null, null);
		}
	public static	void			init(String[] args)
		{
		new MiSystem(null, args);
		}
	public static	void			init(Applet applet)
		{
		new MiSystem(applet, null);
		}
	public					MiSystem()
		{
		this(null, null);
		}
	public					MiSystem(Applet applet)
		{
		this(applet, null);
		}
	public					MiSystem(Applet applet, String[] args)
		{
		this.applet = applet;
		isApplet = (applet != null);

		commandBuilder = new MiCommandBuilder();

		properties = new Properties();
		loadCoreProperties();

		if (applet != null)
			{
			ioManager = new MiSystemAppletIOManager(applet);
			}
		else
			{
			ioManager = new MiSystemApplicationIOManager();
			try	{
				MiDebug.setLoggingFilename("dbg.mica");
				}
			catch (Exception e)
				{
				MiDebug.println("Unable to open logging file: dbg.mica");
				}
			}

		windows = new MiParts();

		propertyValueParts = new CaselessKeyHashtable(11);
		propertyValueColors = new CaselessKeyHashtable(11);
		propertyValueAttributes = new CaselessKeyHashtable(11);
		classProperties = new Hashtable();

		attributeValueNames = new CaselessKeyHashtable();
		for (int i = 0; i < MiiNames.attributeValueNames.length; ++i)
			{
			attributeValueNames.put(
				MiiNames.attributeValueNames[i].string, 
				new Integer(MiiNames.attributeValueNames[i].value));
			}

		micaDefaultProperties = properties;

		setProperties(MiiMessages.messagesProperties);
		setProperties(MiiLookProperties.lookProperties);
		setProperties(MiiDisplayNames.properties);
		setProperties(MiiPropertyTypes.validationErrorMsgs);

		isSun = "Solaris".equals(System.getProperty("os.name"));
		// Used to determine which bugs are present
		isMSWindowsNT = (System.getProperty("os.name").indexOf("NT") != -1);
		if (!isMSWindowsNT)
			isMSWindows = (System.getProperty("os.name").indexOf("Windows") != -1);
		// Used to determine which key is the accelerator key
		isPC = "x86".equals(System.getProperty("os.arch"));


		String version = System.getProperty("java.version");
		int majorVersion = Utility.toInteger(version.charAt(0) + "");
		version = version.substring(2);
		int minorVersion = Utility.toInteger(version);
		for (int i = 0; i < version.length(); ++i)
			{
			if (!Character.isDigit(version.charAt(i)))
				{
				minorVersion = Utility.toInteger(version.substring(0, i));
				break;
				}
			}
		jdkVersion = majorVersion + ((double )minorVersion)/10;

		if (jdkVersion < 1.1)
			versionOfAWTorSwingAPItoUse = MiiTypes.Mi_AWT_1_0_2_HEAVYWEIGHT_COMPONENT_TYPE;
		else
			versionOfAWTorSwingAPItoUse = MiiTypes.Mi_AWT_1_1_HEAVYWEIGHT_COMPONENT_TYPE;

/***
		else if ((jdkVersion >= 1.1) && (jdkVersion < 1.2))
			versionOfAWTorSwingAPItoUse = MiiTypes.Mi_AWT_1_1_HEAVYWEIGHT_COMPONENT_TYPE;
		else if (jdkVersion >= 1.2)
			versionOfAWTorSwingAPItoUse = MiiTypes.Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE;
***/

		MiSystem.loadPropertiesFile(MiSystem.Mi_MICA_DEFAULT_PROPERTIES_FILENAME);

		// Need a special area/class for kludges/fixes like this...:-)
		setProperty(Mi_LIMIT_TABLE_SIZES, "true");

		if (isMSWindowsNT || isMSWindows)
			fileNamesIgnoreCase = true;

		setProperty(Mi_VERSION, "1.3.2 April 15, 2003");


		appDefaultProperties = new Properties(micaDefaultProperties);
		properties = new Properties(appDefaultProperties);

		MiSystem.loadPropertiesFile(MiSystem.Mi_MICA_PROPERTIES_FILENAME);

		MiToolkit.initLookAndFeel();

		printer = new MiPrint();
		customLookAndFeelManager = new MiCustomLookAndFeelManager();
		for (int i = 0; i < MiiLookProperties.customLookAndFeelRegistrationTable.length; ++i)
			{
			Pair p = MiiLookProperties.customLookAndFeelRegistrationTable[i];
			customLookAndFeelManager.registerCustomLookAndFeel(p.name, p.value);
			}

		if (args != null)
			{
			for (int i = 0; i < args.length; ++i)
				{
				if ((args[i].startsWith("-D")) && (i < args.length - 1))
					{
					System.setProperty(args[i].substring(2), args[i + 1]);
					++i;
					}
				}
			}

		Enumeration enum = System.getProperties().propertyNames();
		while (enum.hasMoreElements())
			{
			String key = (String )enum.nextElement();
			if (key.equals(Mi_MICA_PROPERTIES_FILENAME))
				{
				MiSystem.loadPropertiesFile(System.getProperties().getProperty(key));
				}
			}

		processDefaultAttributesOverrideProperties();
		}
	protected static void		processDefaultAttributesOverrideProperties()
		{
		if (getPropertiesForClass(Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME).size() > 0)
			{
			MiAttributes defaults = getPropertyAttributes(Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME);
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
		if (getPropertiesForClass(Mi_DEFAULT_TOOLKIT_ATTRIBUTES_PROPERTY_NAME).size() > 0)
			{
			OrderedProperties properties = getPropertiesForClass(Mi_DEFAULT_TOOLKIT_ATTRIBUTES_PROPERTY_NAME);
//MiDebug.println("Mi_DEFAULT_TOOLKIT_ATTRIBUTES_PROPERTY_NAME=" + properties);
			for (int i = 0; i < properties.getKeys().size(); ++i)
				{
				String key = properties.getKeys().get(i);
				int index = key.indexOf('.');
				if (index != -1)
					{
					String propertyName = key.substring(0, index);
					String attName = key.substring(index + 1);
					String attValue = (String )properties.get(key);

					MiAttributes atts = MiToolkit.getToolkitAttributesForCategory("MiToolkit." + propertyName);
//MiDebug.println("propertyName=" + propertyName);
//MiDebug.println("atts=" + atts);
					if (atts != null)
						{
						atts = atts.setAttributeValue(attName, attValue);
//MiDebug.println("setting atts=" + atts);
//MiDebug.println("atts.getBackgroundColor()=" + atts.getBackgroundColor());
						MiToolkit.setToolkitAttributesForCategory("MiToolkit." + propertyName, atts);
						properties.remove(key);
						--i;
						}
					}
				}


			// MiAttributes defaults = getPropertyAttributes(Mi_DEFAULT_TOOLKIT_ATTRIBUTES_PROPERTY_NAME);
			MiAttributes defaults = new MiAttributes();
			for (int i = 0; i < properties.getKeys().size(); ++i)
				{
				String key = properties.getKeys().get(i);
				String value = properties.getProperty(key);
				defaults = defaults.setAttributeValue(key, value);
				}

			MiWidgetAttributes atts = MiToolkit.getStandardWidgetAttributes();
			atts.normalAttributes		= atts.normalAttributes.overrideFrom(defaults);
			atts.inSensitiveAttributes	= atts.inSensitiveAttributes.overrideFrom(defaults);
			atts.selectedAttributes		= atts.selectedAttributes.overrideFrom(defaults);
			atts.keyboardFocusAttributes	= atts.keyboardFocusAttributes.overrideFrom(defaults);
			atts.enterKeyFocusAttributes	= atts.enterKeyFocusAttributes.overrideFrom(defaults);
			atts.mouseFocusAttributes	= atts.mouseFocusAttributes.overrideFrom(defaults);
			MiToolkit.setStandardWidgetAttributes(atts);
			}
		}
	protected	void		loadCoreProperties()
		{
		if (isApplet)
			{
			URL codeBase = applet.getCodeBase();

			MiSystem.setProperty(MiSystem.Mi_CURRENT_DIRECTORY, codeBase.toString());

			MiSystem.setProperty(MiSystem.Mi_APPLET_OUTPUTSTREAM_ADDRESS,
				applet.getParameter(MiSystem.Mi_APPLET_OUTPUTSTREAM_ADDRESS));

			String home = applet.getParameter(MiSystem.Mi_HOME);
			MiSystem.setProperty(MiSystem.Mi_HOME, 
				(home == null ? MiSystem.getProperty(MiSystem.Mi_CURRENT_DIRECTORY) : home));

			MiSystem.setProperty(MiSystem.Mi_PALETTES_DIRECTORY, "palettes");
			MiSystem.setProperty(MiSystem.Mi_IMAGES_HOME, "images");
			}
		else
			{
			MiSystem.setProperty(MiSystem.Mi_CURRENT_DIRECTORY, System.getProperty("user.dir"));
			MiSystem.setProperty(MiSystem.Mi_PALETTES_DIRECTORY, System.getProperty("user.dir"));
			if (MiSystem.getProperty(MiSystem.Mi_HOME) == null)
				{
				if (System.getProperty(MiSystem.Mi_HOME) != null)
					{
					MiSystem.setProperty(MiSystem.Mi_HOME, System.getProperty(MiSystem.Mi_HOME));
					}
				else
					{
					MiSystem.setProperty(MiSystem.Mi_HOME, System.getProperty("user.dir"));
					}
				}

			if (MiSystem.getProperty(MiSystem.Mi_IMAGES_HOME) == null)
				{
				if (System.getProperty(MiSystem.Mi_IMAGES_HOME) != null)
					{
					MiSystem.setProperty(MiSystem.Mi_IMAGES_HOME, 
						System.getProperty(MiSystem.Mi_IMAGES_HOME));
					}
				else
					{
					MiSystem.setProperty(MiSystem.Mi_IMAGES_HOME, "${Mi_HOME}/images");
					}
				}
			}
		}
	/**
	 * Used to override the default undoable commands used by various parts of the system
	 **/
	public static	void		setCommandBuilder(MiiCommandBuilder builder)
		{
		commandBuilder = builder;
		}
	public static	MiiCommandBuilder getCommandBuilder()
		{
		return(commandBuilder);
		}
	public static	MiParts		getWindows()
		{
		return(windows);
		}
	protected static void		addWindow(MiNativeWindow window)
		{
		windows.addElement(window);
		}
	protected static void		removeWindow(MiNativeWindow window)
		{
		windows.removeElement(window);
		}
	public static	void		loadPropertiesFile(String filename)
		{
		InputStream inputStream = getResourceAsStream(filename);
		if (inputStream != null)
			setProperties(inputStream);
		}
	public static	MiiSystemIOManager getIOManager()
		{
		return(ioManager);
		}
	public static	void		setIOManager(MiiSystemIOManager manager)
		{
		ioManager = manager;
		}
	public static	InputStream	getResourceAsStream(String resourceName)
		{
		try	{
			return(ioManager.getInputResourceAsStream(resourceName));
			}
		catch (Exception e)
			{
			return(null);
			}
		}
	public static	boolean		isMSWindows()
		{
		return(isMSWindows);
		}
	public static	boolean		isPC()
		{
		return(isPC);
		}
	public static	MiJDKAPIComponentType getDefaultJDKAPIComponentType()
		{
		return(versionOfAWTorSwingAPItoUse);
		}
	public static	void		 setDefaultJDKAPIComponentType(MiJDKAPIComponentType version)
		{
		versionOfAWTorSwingAPItoUse = version;
		}
	public static	double		getJDKVersion()
		{
		return(jdkVersion);
		}
	public static	boolean		getFileNamesIgnoreCase()
		{
		return(fileNamesIgnoreCase);
		}

	public static	boolean		isThrowingExceptionsWhenPropertyChangeVetoed()
		{
		return(isThrowingExceptionsWhenPropertyChangeVetoed);
		}
	public static	void		setIsThrowingExceptionsWhenPropertyChangeVetoed(boolean flag)
		{
		isThrowingExceptionsWhenPropertyChangeVetoed = flag;
		}

	public static	boolean		isApplet()
		{
		return(isApplet);
		}

	public static	void		setCompanyLogo(MiPart logo)
		{
		companyLogo = logo;
		}
	public static	MiPart		getCompanyLogo()
		{
		if (companyLogo == null)
			{
			MiPart logo = new MiContainer();
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.setElementHSizing(MiiTypes.Mi_NONE);
			logo.setLayout(columnLayout);
			MiImage image = new MiImage(MiiLookProperties.Mi_COMPANY_ICON_NAME, true);
			// image.setSize(new MiSize(64, 64));
			logo.appendPart(image);
			MiPart label = new MiText(getProperty(MiiLookProperties.Mi_COMPANY_NAME));
			label.setFontBold(true);
			label.setFontItalic(true);
			label.setFontPointSize(20);
			logo.appendPart(label);
			companyLogo = logo;
			}
		return(companyLogo);
		}

	public static	MiClipBoard		getClipBoard()
		{
		if (clipBoard == null)
			clipBoard = new MiClipBoard();
		return(clipBoard);
		}

	public static	MiThemeManager		getThemeManager()
		{
		if (themeManager == null)
			themeManager = new MiThemeManager();
		return(themeManager);
		}
	public static	MiCustomLookAndFeelManager	getCustomLookAndFeelManager()
		{
		return(customLookAndFeelManager);
		}

	public static	MiTransactionManager	getTransactionManager()
		{
		if (transactionManager == null)
			transactionManager = new MiTransactionManager();
		return(transactionManager);
		}

	public static	MiTransactionManager	getViewportTransactionManager()
		{
		if (viewportManager == null)
			viewportManager = new MiTransactionManager();
		return(viewportManager);
		}

	public static	MiPrint			getPrinter()
		{
		return(printer);
		}

	public static	MiHelpManager		getHelpManager()
		{
		if (helpManager	== null)
			helpManager = new MiHelpManager();
		return(helpManager);
		}

	public static	int		getValueOfAttributeValueName(String valueName)
		{
		Integer intValue = (Integer )attributeValueNames.get(valueName);
		if (intValue == null)
			{
			throw new IllegalArgumentException(
				"Attribute value name is not a valid name of any known attribute value: "
				 + valueName);
			}
		return(intValue.intValue());
		}
	public static	String		getNameOfAttributeValue(
						String[] possibleAttributeValueNames, int attributeValue)
		{
		for (int i = 0; i < possibleAttributeValueNames.length; ++i)
			{
			Integer intValue = (Integer )attributeValueNames.get(possibleAttributeValueNames[i]);
			if (intValue == null)
				{
				throw new IllegalArgumentException(
					"Attribute value name is not a valid name of any known attribute value: " 
					+ possibleAttributeValueNames[i]);
				}
			if (intValue.intValue() == attributeValue)
				return(possibleAttributeValueNames[i]);
			}
		if (attributeValue == 0)
			{
			return(MiiTypes.Mi_NONE_NAME);
			}

		return(null);
		//throw new IllegalArgumentException(
			//"Attribute value has no known valid name: " + attributeValue);
		}
	public static	String		getProperty(String key)
		{
		String value = (String )properties.getProperty(key);
		if ((value != null) && (value.indexOf("${") != -1))
			{
			return(applyPropertyMacros(value));
			}
		if ((value == null) && (!isApplet))
			{
			return(System.getProperty(key));
			}
		return(value);
		}
	public static	String		getProperty(String key, String defaultValue)
		{
		String value = (String )properties.getProperty(key, defaultValue);
		if (value.indexOf("${") != -1)
			return(applyPropertyMacros(value));
		return(value);
		}
	public static	String		getPropertyOrKey(String key)
		{
		if (key == null)
			return(null);
		String value = (String )properties.getProperty(key);
		if (value == null)
			value = key;
		if (value.indexOf("${") != -1)
			return(applyPropertyMacros(value));
		return(value);
		}
	public static	void		removeProperty(String name)
		{
		properties.remove(name);
		}
	protected static String		applyPropertyMacros(String value)
		{
		int index;
		while ((index = value.indexOf("${")) != -1)
			{
			int endIndex = value.indexOf('}', index + 2);
			if (endIndex == -1)
				{
				if (MiDebug.debug)
					MiDebug.println("No closing parenthesis found for macro in value: \"" + value + "\"");
				return(value);
				}
			String macro = value.substring(index + 2, endIndex);
			String replacement = properties.getProperty(macro);
			if (Utility.isEmptyOrNull(replacement))
				{
				if (!isApplet)
					{
					replacement = System.getProperty(macro);
					}
				}

			if (Utility.isEmptyOrNull(replacement))
				{
				if (MiDebug.debug)
					{
					MiDebug.println("No substitution found for value macro: \"" 
						+ macro + "\" for property named: \"" + value + "\"");
					}
				value = value.substring(0, index) + value.substring(endIndex + 1);
				}
			else
				{
				value = value.substring(0, index) 
						+ replacement + value.substring(endIndex + 1);
				}
			}
		return(value);
		}
	public static	void		setProperty(String name, String value)
		{
		if (value != null)
			{
			properties.put(name, value);
			if (name.startsWith(Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME))
				{
				processDefaultAttributesOverrideProperties();
				}
			}
		else
			{
			properties.remove(name);
			}
		}
	public static	Properties	getProperties()
		{
		return(properties);
		}
	public static	OrderedProperties getPropertiesForClass(String className)
		{
		OrderedProperties p = (OrderedProperties )classProperties.get(className);
		if (p == null)
			{
			p = new OrderedProperties();
			classProperties.put(className, p);
			}
		else
			{
			p.clear();
			}

		String classNameSpec = className + ".";
		for (Enumeration e = properties.keys(); e.hasMoreElements();)
			{
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);
			if (key.startsWith(classNameSpec))
				{
				p.put(key.substring(classNameSpec.length()), value);
				}
			}
		return(p);
		}
	
	public static	void		setPropertyPart(String name, MiPart part)
		{
		propertyValueParts.put(name, part);
		}
	public static	MiPart		getPropertyPart(String name)
		{
		name = getProperty(name, name);
		MiPart part = (MiPart )propertyValueParts.get(name);
		if (part == null)
			{
			if ((name.endsWith(".gif"))
				|| (name.endsWith(".jpg"))
				|| (name.endsWith(".xpm")))
				{
				part = new MiImage(name);
				propertyValueParts.put(name, part);
				}
			else
				{
				//MiDebug.println("Unknown property: " + name);
				//part = new MiCircle(0,0,20,20);
				}
			}
		return(part);
		}
	public static 	void		setPropertyColor(String name, Color color)
		{
		propertyValueColors.put(name, color);
		}
	public static	Color		getPropertyColor(String name)
		{
		name = getProperty(name, name);
		Color color = (Color )propertyValueColors.get(name);
		if (color == null)
			{
			color = MiColorManager.getColor(name);
			propertyValueColors.put(name, color);
			}
		return(color);
		}
	public static	void		setPropertyAttributes(String name, MiAttributes atts)
		{
		propertyValueAttributes.put(name, atts);
		}
	public static	MiAttributes	getPropertyAttributes(String name)
		{
		name = getProperty(name, name);
		MiAttributes atts = (MiAttributes )propertyValueAttributes.get(name);
		if (atts == null)
			{
			atts = new MiAttributes();
			Properties properties = getPropertiesForClass(name);
			for (Enumeration e = properties.keys(); e.hasMoreElements();)
				{
				String key = (String)e.nextElement();
				String value = properties.getProperty(key);
				atts = atts.setAttributeValue(key, value);
				}
			propertyValueAttributes.put(name, atts);
			}
		return(atts);
		}

	protected static void		flushThemeProperties()
		{
		propertyValueAttributes.clear();
		classProperties.clear();
		}
	public static 	void		setProperties(Pair[] nameValuePairs)
		{
		boolean updateDefaultAttributes = false;
		for (int i = 0; i < nameValuePairs.length; ++i)
			{
			String value = nameValuePairs[i].value;
			if (value != null)
				{
				properties.put(nameValuePairs[i].name, value);
				if (nameValuePairs[i].name.startsWith(Mi_DEFAULT_ATTRIBUTES_PROPERTY_NAME))
					{
					updateDefaultAttributes = true;
					}
				}
			}
		if (updateDefaultAttributes)
			{
			processDefaultAttributesOverrideProperties();
			}
		}
	public static	void		setProperties(InputStream stream)
		{
		try	{
			properties.load(stream);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		// ---------------------------------------------------------------
		// This is BROKEN in Sun JDK 1.02: it includes everything after the name
		// as the value (including tabs and '='). So go thru and do a fix.
		// ---------------------------------------------------------------
		for (Enumeration e = properties.keys(); e.hasMoreElements();)
			{
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);

			if (Utility.isEmptyOrNull(value))
				continue;

			int length = value.length();
			int i = 0;
			char ch = value.charAt(i++);
			// Skip white space
			while (((ch == '\t') || (ch == ' ')) && (i < length))
				ch = value.charAt(i++);

			if (ch == '=')
				ch = value.charAt(i++);

			// Skip white space
			while (((ch == '\t') || (ch == ' ')) && (i < length))
				ch = value.charAt(i++);

			if ((i > 0) && (i < length))
				properties.put(key, value.substring(i - 1));
			else if (i == length)
				properties.remove(key);
			}
		}
	public static	Properties	getMicaDefaultProperties()
		{
		return(micaDefaultProperties);
		}
	protected static void		setMicaDefaultProperty(String name, String value)
		{
		micaDefaultProperties.put(name, value);
		}
	public static	Properties	getApplicationDefaultProperties()
		{
		return(appDefaultProperties);
		}
	public static	void		setApplicationDefaultProperties(Pair[] properties)
		{
		for (int i = 0; i < properties.length; ++i)
			appDefaultProperties.put(properties[i].name, properties[i].value);
		}
	public static	void		setApplicationDefaultProperty(String name, String value)
		{
		appDefaultProperties.put(name, value);
		}
	public static	void		applyClassPropertyValues(MiPart target, String className) 
		{
		Properties properties = getPropertiesForClass(className);
		for (Enumeration e = properties.keys(); e.hasMoreElements();)
			{
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);
			target.setPropertyValue(key, value);
			}
		}
	public static	void		applyClassPropertyValues(MiWidget target, String className) 
		{
		Properties properties = getPropertiesForClass(className);
		target.setPropertyValues(properties);
		}
	public static	AudioClip		getAudioClip(String filename)
		{
		if (applet == null)
			{
			createDummyApplet();
			}
		//return(applet.getAudioClip(applet.getCodeBase(), filename));
		URL url = null;
		try	{
			url = new URL("file:" + System.getProperty("user.dir") + File.separator);
			}
		catch (java.net.MalformedURLException e)
			{
			e.printStackTrace();
			}
		return(applet.getAudioClip(url, filename));
		}
/*
	public static	void			playAudioClip(AudioClip clip)
		{
		applet.play(clip);
		}
	public static	void			loopAudioClip(AudioClip clip)
		{
		applet.loop(clip);
		}
	public static	void			stopAudioClip(AudioClip clip)
		{
		applet.stop(clip);
		}
*/

/*
	public static		Applet		getDummyApplet()
		{
		if (applet == null)
			createDummyApplet();
		return(applet);
		}
*/

	public static		Applet		getApplet()
		{
		if (applet == null)
			createDummyApplet();
		return(applet);
		}

	private static	void			createDummyApplet()
		{
        	java.awt.Frame frame = new java.awt.Frame();
		applet = new Applet();
        	frame.add(applet);
        	frame.addNotify();
	       	applet.addNotify();
        	applet.setStub(new MiDummyAppletStub());
		}
	}

class MiDummyAppletStub implements AppletStub, AppletContext
	{
	private		URL		documentBase;
	private		URL		codeBase;


	public				MiDummyAppletStub()
		{
		}

	public		boolean 	isActive()
		{
		return(true);
		}
	public		URL 		getDocumentBase()
		{
		if (documentBase == null)
			{
			try	{
				documentBase = new URL("file:" + System.getProperty("user.dir") + File.separator);
				}
			catch (java.net.MalformedURLException e)
				{
				e.printStackTrace();
				}
			}
		return(documentBase);
		}
	public		URL		getCodeBase()
		{
		if (codeBase == null)
			{
			try	{
				codeBase = new URL("file:" + System.getProperty("user.dir") + File.separator);
				}
			catch (java.net.MalformedURLException e)
				{
				e.printStackTrace();
				}
			}
		return(codeBase);
		}

	public		String 		getParameter(String name)
		{
		return(null);
		}

	public		AppletContext 	getAppletContext()
		{
		return(this);
		}

	public		void 		appletResize(int width, int height)
		{
		}

	public		AudioClip 	getAudioClip(URL url)
		{
		return(new sun.applet.AppletAudioClip(url));
		}

	public		Image 		getImage(URL url)
		{
		return(java.awt.Toolkit.getDefaultToolkit().getImage(url));
		}

	public		Applet 		getApplet(String name)
		{
		return(null);
		}

	public		Enumeration 	getApplets()
		{
		Vector applets = new Vector();
		applets.addElement(MiSystem.getApplet());
		return(applets.elements());
		}

	public		void 		showDocument(URL url)
		{
		}

	public 		void 		showDocument(URL url, String target)
		{
		}

	public		void 		showStatus(String status)
		{
		}

	// 3 methods added for JDK 1.4
	public void setStream(String key, InputStream stream)throws IOException
		{
		}
	public InputStream getStream(String key)
		{
		return(null);
		}

	public Iterator getStreamKeys()
		{
		return(null);
		}
	}

