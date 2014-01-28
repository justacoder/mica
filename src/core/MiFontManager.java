
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
import java.awt.Font; 
import java.awt.Toolkit; 
import java.util.Hashtable; 
import java.awt.FontMetrics; 
import java.awt.GraphicsEnvironment; 
import com.swfm.mica.util.Utility; 

/**----------------------------------------------------------------------------------------------
 * This class manages Fonts by caching MiFonts for reuse and providing
 * the #resizeFont method that allows the draw routines (MiRenderer) to
 * find the correct font to draw when the graphics are zoomed in or out.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiFontManager
	{
	private static	Hashtable	fontCache 		= new Hashtable();
	private static	Hashtable	fontMetricsHashtable 	= new Hashtable();
	public  static	int		MAX_FONT_POINT_SIZE	= 100;




					/**------------------------------------------------------
	 				 * Returns a font that is the same as the given font but
					 * whose width and height is as close as possible to the
					 * given width and height but no larger. Large heights are
					 * truncated to MAX_FONT_POINT_SIZE to prevent large 
					 * magnifications from consuming all of memory.
	 				 * @param font 		the source font
	 				 * @param maxWidth 	the maximum character width
	 				 * @param maxHeight 	the maximum character height
	 				 * @return  		the resized font
					 *------------------------------------------------------*/
	public	static	MiFont		resizeFont(MiFont font, int maxWidth, int maxHeight)
		{
		MiFont f;

		int pointSize = maxHeight;
		if (pointSize > MAX_FONT_POINT_SIZE)
			{
			MiDebug.println("Font size: " + pointSize 
				+ ", too large, using font size: " 
				+ MAX_FONT_POINT_SIZE);

			//MiDebug.printStackTrace();
			pointSize = MAX_FONT_POINT_SIZE;
			}

		String key = font.getName() + font.getStyle() + maxWidth + pointSize;
		if ((f = (MiFont )fontCache.get(key)) == null)
			{
			f = new MiFont(font.getName(), font.getStyle(), pointSize);

			while (f.getMaxCharWidth() > maxWidth)
				{
				--pointSize;
				if (pointSize < 3)
					return(null);
				f = new MiFont(font.getName(), font.getStyle(), pointSize);
				}
			while ((f.getMaxCharWidth() < maxWidth)
				&& (f.getMaxCharHeight() < maxHeight))
				{
				++pointSize;
				if (pointSize > MAX_FONT_POINT_SIZE)
					return(null);
				f = new MiFont(font.getName(), font.getStyle(), pointSize);
				}
			while (f.getMaxCharHeight() > maxHeight)
				{
				--pointSize;
				if (pointSize < 3)
					return(null);
				f = new MiFont(font.getName(), font.getStyle(), pointSize);
				}

			fontCache.put(key, f);
			}
		return(f);
		}

	public static	MiFont		findFont(String fullName)
		{
		// ---------------------------------------------------------------
		// Handle font family...
		// ---------------------------------------------------------------
		String name = fullName;
		int index = fullName.indexOf('.');
		if (index != -1)
			name = fullName.substring(0, index);
		fullName = fullName.substring(index + 1);

		// ---------------------------------------------------------------
		// Handle font size...
		// ---------------------------------------------------------------
		index = fullName.indexOf('.');
		int size = 12;
		if (fullName.length() > 0)
			{
			String sizeName = fullName;
			if (index != -1)
				sizeName = fullName.substring(0, index);
			fullName = fullName.substring(index + 1);
			size = Utility.toInteger(sizeName);
			}
	
		// ---------------------------------------------------------------
		// Handle font style...
		// ---------------------------------------------------------------
		int style = Font.PLAIN;
		if (fullName.length() > 0)
			{
			String styleName = fullName;
			if (Utility.indexOfIgnoreCase(styleName, "BOLD") != -1)
				style |= Font.BOLD;
			if (Utility.indexOfIgnoreCase(styleName, "ITALIC") != -1)
				style |= Font.ITALIC;
			if (Utility.indexOfIgnoreCase(styleName, "UNDERLINED") != -1)
				style |= MiFont.UNDERLINE;
			if (Utility.indexOfIgnoreCase(styleName, "STRIKEOUT") != -1)
				style |= MiFont.STRIKEOUT;
			}

		MiFont font = findFont(name, style, size);
		return(font);
		}
					/**------------------------------------------------------
	 				 * Returns a font from the internal cache, if found, or
					 * creates one and enters it into the internal cache, if
					 * not found.
	 				 * @param name 		the font family name
	 				 * @param style 	the font style
	 				 * @param size 		the font point size
	 				 * @return  		the font
					 *------------------------------------------------------*/
	public	static	MiFont		findFont(String name, int style, int size)
		{
		MiFont f;
		String key = name + "." + style + "." + size;
		if ((f = (MiFont )fontCache.get(key)) == null)
			{
			f = new MiFont(name, style, size);
			fontCache.put(key, f);
			}
		return(f);
		}

					/**------------------------------------------------------
	 				 * Returns a list of all font family names that this
					 * system supports.
	 				 * @return  		the array of font family names
					 *------------------------------------------------------*/
	public	static	String[]	getFontList()
		{
/* Takes too long to get all of the FontMetrics */
		if (MiSystem.getJDKVersion() >= 1.2)
			return(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		else
			return(Toolkit.getDefaultToolkit().getFontList());
		}

	public	static	FontMetrics	getFontMetrics(MiFont font)
		{
		String fontString = font.toString();
		FontMetrics fontMetrics = (FontMetrics )fontMetricsHashtable.get(fontString);
		if (fontMetrics == null)
			{
			fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
			fontMetricsHashtable.put(fontString, fontMetrics);
			}
		return(fontMetrics);
		}
		
	}

