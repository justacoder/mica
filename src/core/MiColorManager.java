
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

import java.awt.Color; 
import java.util.Enumeration; 
import java.util.Hashtable; 
import java.util.Vector; 
import com.swfm.mica.util.CaselessKeyHashtable;

/**
 * This class manages colors, essentially mapping names of colors to RGB(A)
 * values.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiColorManager
	{
	public static final int		MAX_COLOR_RGB_VALUE	= 255;

					/**------------------------------------------------------
	 				 * Hash table of named colors.
					 *------------------------------------------------------*/
	private static 	CaselessKeyHashtable	nameToColorTable;
	private static 	Hashtable	colorToNameTable;

					/**------------------------------------------------------
	 				 * Table of colors.
					 *------------------------------------------------------*/
	private static	Color[]		colors;

					/**------------------------------------------------------
	 				 * Table of named colors.
					 *------------------------------------------------------*/
	private static	MiNamedColor[]	namedColors =
		{
		// Grays
		new MiNamedColor(	"black", 		Color.black),
		new MiNamedColor(	"darkGray", 		new Color(51, 51, 51)),
		new MiNamedColor(	"gray", 		new Color(102, 102, 102)),
		new MiNamedColor(	"lightGray", 		new Color(153, 153, 153)),
								// Not a browser safe color
		new MiNamedColor(	"veryLightGray",	 Color.lightGray),
		new MiNamedColor(	"veryVeryLightGray", 	new Color(204, 204, 204)),
								// Not a browser safe colors
		new MiNamedColor(	"windowsVeryDarkGray",	new Color(129, 120, 101)),
		new MiNamedColor(	"windowsDarkGray",	new Color(170, 163, 147)),
		new MiNamedColor(	"windowsGray",	 	new Color(212, 208, 200)),
		new MiNamedColor(	"windowsLightGray",	new Color(232, 230, 225)),

								// Not a browser safe color
		new MiNamedColor(	"veryDarkWhite",	 new Color(219, 219, 219)),
								// Not a browser safe color
		new MiNamedColor(	"darkWhite",	 	new Color(238, 238, 238)),
		new MiNamedColor(	"white", 		Color.white),

		// Greens
		new MiNamedColor(	"darkgreen",		new Color(0, 102, 0)),
		new MiNamedColor(	"darkgreen0",		new Color(0, 204, 0)),
		new MiNamedColor(	"green",		Color.green),
		new MiNamedColor(	"lightGreen",		new Color(153, 255, 204)),

		// Yellows
		new MiNamedColor(	"darkYellow",		new Color(204, 204, 0)),
		new MiNamedColor(	"yellow",		Color.yellow),

		// Blues
		new MiNamedColor(	"darkBlue", 		new Color(0,   0,   204)),
		new MiNamedColor(	"blue",			Color.blue),
		new MiNamedColor(	"lightBlue",		new Color(0,   153, 255)),
		new MiNamedColor(	"veryLightBlue",	new Color(102, 153, 255)),
		new MiNamedColor(	"veryVeryLightBlue",	new Color(153, 204, 255)),
		new MiNamedColor(	"darkCyan",		new Color(0,   204, 255)),
		new MiNamedColor(	"cyan",			Color.cyan),
		new MiNamedColor(	"lightCyan",		new Color(204, 255, 255)),

		// Violets/magentas
		new MiNamedColor(	"purple",		new Color(153, 0, 204)),
		new MiNamedColor(	"lightPurple",		new Color(153, 102, 204)),

		new MiNamedColor(	"violet",		new Color(204, 102, 255)),

		new MiNamedColor(	"magenta",		Color.magenta),

		// Reds
		new MiNamedColor(	"pink",			new Color(255, 153, 255)),
		new MiNamedColor(	"lightPink",		new Color(255, 204, 255)),

		new MiNamedColor(	"veryDarkBrown",	new Color(204, 51, 0)),
		new MiNamedColor(	"darkBrown",		new Color(204, 102, 153)),
		new MiNamedColor(	"brown",		new Color(204, 153, 51)),
		new MiNamedColor(	"lightBrown",		new Color(255, 204, 153)),

		new MiNamedColor(	"red",			Color.red),

		new MiNamedColor(	"orange",		new Color(255, 102, 51))
		};

					/**------------------------------------------------------
	 				 * Static contructor to create the named color hash table
					 *------------------------------------------------------*/
	static	{
		nameToColorTable = new CaselessKeyHashtable();
		colorToNameTable = new Hashtable();
		for (int i = 0; i < namedColors.length; ++i)
			{
			nameToColorTable.put(namedColors[i].name, namedColors[i].color);
			colorToNameTable.put(namedColors[i].color, namedColors[i].name);
			}
		}

					/**------------------------------------------------------
	 				 * Popular colors declared public for convienient access.
					 *------------------------------------------------------*/
	public static final Color	transparent 	= null;
	public static final Color	black 		= Color.black;
	public static final Color	white 		= Color.white;
	public static final Color	darkGray 	= new Color(51, 51, 51);
	public static final Color	gray		= new Color(102, 102, 102);
	public static final Color	lightGray 	= new Color(153, 153, 153);
							// Not a browser safe color
	public static final Color	veryLightGray 	= Color.lightGray;
	public static final Color	veryVeryLightGray = new Color(204, 204, 204);
							// Not a browser safe color
	public static final Color	veryDarkWhite 	= new Color(219, 219, 219);
	public static final Color	windowsGray	= new Color(212, 208, 200);

	public static final Color	darkWhite	= new Color(238, 238, 238);

	public static final Color	red 		= Color.red;
	public static final Color	green 		= Color.green;
	public static final Color	blue 		= Color.blue;
	public static final Color	cyan 		= Color.cyan;
	public static final Color	pink 		= new Color(255, 153, 255);
	public static final Color	yellow 		= Color.yellow;
	public static final Color	magenta 	= Color.magenta;
	public static final Color	orange 		= new Color(255, 102, 51);
	public static final Color	darkBlue 	= new Color(0, 0, 204);

	public				MiColorManager()
		{
		}

					/**------------------------------------------------------
	 				 * Returns the color corresponding to the given name.
					 * Valid names are "transparent" (i.e. 
					 * MiiTypes.Mi_TRANSPARENT_COLOR_NAME), hexidecimal rgb
					 * values (i.e. "0xff0000" or "#ff0000"), or an actual
					 * name (i.e. "red").
					 * @param name		the name of a color
					 * @return		the corresponding color
					 *------------------------------------------------------*/
	public	static Color		getColor(String name)
		{
		if (name == null)
			return(null);

		name = name.toLowerCase();
		if (name.equals(MiiTypes.Mi_TRANSPARENT_COLOR_NAME))
			{
			return(null);
			}
		if (name.equals("(null)") || (name.equals("null")))
			{
			return(null);
			}

		Color color = (Color )nameToColorTable.get(name);
		if (color != null)
			{
			return(color);
			}

		if (name.startsWith("0x"))
			{
			name = name.substring(2);
			}
		else if (name.startsWith("#"))
			{
			name = name.substring(1);
			}
		else
			{
			throw new IllegalArgumentException(
				"MiColorManager: No color found with name: \"" + name + "\"");
			}

		// Handle leading transparency value, if any
		boolean hasAlpha = false;
		if (name.length() == 8)
			{
			if (MiSystem.getJDKVersion() >= 1.2)
				hasAlpha = true;
			else
				name = name.substring(2);
			}
		if (hasAlpha)
			{
			if (name.charAt(0) > '7')
				{
				color = new Color(Integer.parseInt(name.substring(1), 16) 
					+ (Integer.parseInt(name.charAt(0) + "", 16) << 28), true);
				}
			else
				{
				color = new Color(Integer.parseInt(name, 16), true);
				}
			}
		else
			{
			color = new Color(Integer.parseInt(name, 16));
			}

		nameToColorTable.put(name, color);

		return(color);
		}
					/**------------------------------------------------------
	 				 * Returns the number of named colors.
					 * @return		the number of colors
					 *------------------------------------------------------*/
	public	static int		getNumberOfColors()
		{
		return(nameToColorTable.size());
		}
					/**------------------------------------------------------
	 				 * Returns the name of the given color. Valid colors are
					 * null (i.e. MiiTypes.Mi_TRANSPARENT_COLOR), a named
					 * color (i.e. "red"), or any other color (returns "#"
					 * + the hexidecimal RGB value).
					 * @param color		the color to get the name of
					 * @return		the textual name of the color
					 *------------------------------------------------------*/
	public	static String		getColorName(Color color)
		{
		if (color == null)
			return(MiiTypes.Mi_TRANSPARENT_COLOR_NAME);

		String name = (String )colorToNameTable.get(color);
		if (name != null)
			return(name);
		String value = Integer.toHexString(color.getRGB());
		if ((value.length() == 8) && (value.startsWith("ff")))
			{
			value = value.substring(2);
			}
		// Warning: Note that the same color starting with "0x" will not equal this one...
		return("#" + value);
		}
					/**------------------------------------------------------
	 				 * Returns an array of the names of all the named colors.
					 * @return		the array of color names
					 *------------------------------------------------------*/
	public	static String[]		getColorNames()
		{
		String[] names = new String[namedColors.length + 1];
		names[0] = MiiTypes.Mi_TRANSPARENT_COLOR_NAME;
		for (int i = 0; i < namedColors.length; ++i)
			names[i + 1] = namedColors[i].name;
		return(names);
		}
					/**------------------------------------------------------
	 				 * Returns an array of all the named colors.
					 * @return		the array of colors.
					 *------------------------------------------------------*/
	public	static Color[]		getColors()
		{
		if (colors == null)
			{
			colors = new Color[namedColors.length + 1];
			colors[0] = MiiTypes.Mi_TRANSPARENT_COLOR;
			for (int i = 0; i < namedColors.length; ++i)
				colors[i + 1] = namedColors[i].color;
			}
		return(colors);
		}
	}

class MiNamedColor
	{
			String		name;
			Color		color;


					/**------------------------------------------------------
	 				 * Constructs a new MiNamedColor. This class is used to
					 * store name, color pairs.
					 * @param name		the name of the color
					 * @param color		the color
					 *------------------------------------------------------*/
					MiNamedColor(String name, Color color)
		{
		this.name = name;
		this.color = color;
		}
	}

