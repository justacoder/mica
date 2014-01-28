
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
import java.awt.FontMetrics; 
import com.swfm.mica.util.Utility; 

/**----------------------------------------------------------------------------------------------
 * This class wraps the AWT Font class. This was done for both efficiency and
 * to protect Mica against changes to the AWT Font class.
 *
 * @see #MiFontManager
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiFont extends Font
	{
	public	static final int	PLAIN			= Font.PLAIN;
	public	static final int	BOLD			= Font.BOLD;
	public	static final int	ITALIC			= Font.ITALIC;
	public	static final int	UNDERLINE		= 1 << 22;
	public	static final int	STRIKEOUT		= 1 << 23;
	public	static final int	VALID_AWT_STYLES_MASK	= Font.ITALIC | Font.BOLD;

	private		FontMetrics	fontMetrics;
	private		int		fontHeight;
	private		int		fontDescent;
	private		int		averageCharWidth;
	private		int		maxCharWidth;
	private		int		maxCharHeight;
	private		boolean		underLined;
	private		boolean		strikeOut;
	private		MiFont		substitutingForFont;
	private		MiScale		substitutingForFontAtScale = new MiScale();



					/**------------------------------------------------------
	 				 * Constructs a new PLAIN, 12 pont MiFont of the given
					 * typeface family name.
	 				 * @param name 		the family name
					 *------------------------------------------------------*/
	public 				MiFont(String name)
		{
		this(name, PLAIN, 12);
		}

					/**------------------------------------------------------
	 				 * Constructs a new MiFont. The valid family names are
					 * found in MiFontManager.getFontList(). The valid styles
					 * are PLAIN, BOLD, ITALIC, UNDERLINE, STRIKEOUT in any
					 * combination (i.e. ITALIC + UNDERLINE). 
	 				 * @param name 		the family name
	 				 * @param style	 	the style of the font
	 				 * @param size	 	the size of the font in points
					 * @see MiFontManager#getFontList
					 *------------------------------------------------------*/
	public 				MiFont(String name, int style, int size)
		{
		super(name, style & VALID_AWT_STYLES_MASK, size);
		this.underLined = ((style & UNDERLINE) != 0);
		this.strikeOut = ((style & STRIKEOUT) != 0);
		initFontMetrics();
		}

					/**------------------------------------------------------
	 				 * Get the maximum width of all characters in this font.
	 				 * @return  		the maximum width
					 *------------------------------------------------------*/
	public 		int		getMaxCharWidth()
		{
		return(maxCharWidth);
		}

					/**------------------------------------------------------
	 				 * Get the average width of all characters in this font.
	 				 * @return  		the average width
					 *------------------------------------------------------*/
	public 		int		getAverageCharWidth()
		{
		return(averageCharWidth);
		}

					/**------------------------------------------------------
	 				 * Get the maximum height of all characters in this font.
	 				 * @return  		the maximum height
					 *------------------------------------------------------*/
	public 		int		getMaxCharHeight()
		{
		return(maxCharHeight);
		}

					/**------------------------------------------------------
	 				 * Get the width of the given character in this font.
	 				 * @param ch 		the character to get the width of
	 				 * @return  		the width of the character
					 *------------------------------------------------------*/
	public 		int		getWidth(char ch)
		{
		return(fontMetrics.charWidth(ch));
		}

					/**------------------------------------------------------
	 				 * Get the height of a line of characters in this font.
	 				 * @return  		the height of the character
					 *------------------------------------------------------*/
	public 		int		getHeight()
		{
		return(fontHeight);
		}

					/**------------------------------------------------------
	 				 * Get the size of the given character in this font.
	 				 * @param ch 		the character to get the height of
	 				 * @param size 		the (returned) size of the character
	 				 * @return  		the size of the character
					 *------------------------------------------------------*/
	public 		MiSize		getSize(char ch, MiSize size)
		{
		size.setWidth(fontMetrics.charWidth(ch));
		size.setHeight(maxCharHeight);
		return(size);
		}

					/**------------------------------------------------------
	 				 * Get the maximum decent of text in this font. The decent
					 * is the part of a character that extends below the 
					 * baseline (for example the characters j and q have a 
					 * non-zero decent). Returned decent is in pixels.
	 				 * @return  		the maximum decent of this font.
					 *------------------------------------------------------*/
	public 		int		getMaximumDescent()
		{
		return(fontDescent);
		}

					/**------------------------------------------------------
	 				 * Get the width of the given text string in this font.
					 * Returned width is in pixels.
	 				 * @return  		the width of the text string
					 *------------------------------------------------------*/
	public 		int		getWidth(String text)
		{
		if (text != null)
			{
			return(fontMetrics.stringWidth(text));
			}
		return(0);
		}

					/**------------------------------------------------------
	 				 * Get the size of the given text string in this font.
					 * Returned size is in pixels.
	 				 * @param size		the (returned) size of the text string
	 				 * @return  		the size of the text string
					 *------------------------------------------------------*/
	public 		MiSize		getSize(String text, MiSize size)
		{
		if (text != null)
			{
			size.setWidth(fontMetrics.stringWidth(text));
			size.setHeight(maxCharHeight);
			}
		else
			{
			size.zeroOut();
			}
		return(size);
		}

					/**------------------------------------------------------
	 				 * Get the size of the given text string in this font.
	 				 * @return  	the size of the text string
					 *------------------------------------------------------*/
	public 		MiSize		getSize(String text)
		{
		return(getSize(text, new MiSize()));
		}

					/**------------------------------------------------------
	 				 * Return the style of this font. Possible styles are:
					 * PLAIN, BOLD, ITALIC, UNDERLINE, STRIKEOUT in any
					 * combination (i.e. ITALIC + UNDERLINE). 
	 				 * @return  	the style
					 *------------------------------------------------------*/
	public		int		getStyle()
		{
		int style = super.getStyle();
		if (underLined)
			style |= UNDERLINE;
		if (strikeOut)
			style |= STRIKEOUT;
		return(style);
		}
					/**------------------------------------------------------
	 				 * Make and return a font exactly like this font but
					 * with a different type face. 
	 				 * @param name		the new font's type face.
	 				 * @return 		the new font
					 *------------------------------------------------------*/
	public		MiFont		setName(String name)
		{
		return(MiFontManager.findFont(name, getStyle(), getPointSize()));
		}

					/**------------------------------------------------------
	 				 * Return the name (type face) of this font.
	 				 * @return  	the font name
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(super.getName());
		}

	public		String		getFullName()
		{
		String fullName = super.getName() + "." + getSize();
		int style = getStyle();
		String styleName = "";
		if ((style & Font.BOLD) != 0)
			styleName += "+Bold";
		if ((style & Font.ITALIC) != 0)
			styleName += "+Italic";
		if (underLined)
			styleName += "+Underlined";
		if (strikeOut)
			styleName += "+StrikeOut";

		if (styleName.length() > 0)
			fullName += "." + styleName.substring(1);

		return(fullName);
		}

					/**------------------------------------------------------
	 				 * Make and return a font exactly like this font but
					 * with a different point size. 
	 				 * @param points	the returned font's point size.
	 				 * @return 		the new font
					 *------------------------------------------------------*/
	public		MiFont		setPointSize(int points)
		{
		return(MiFontManager.findFont(getName(), getStyle(), points));
		}

					/**------------------------------------------------------
	 				 * Return the point size of this font.
	 				 * @return  	the point size
					 *------------------------------------------------------*/
	public		int		getPointSize()
		{
		return(super.getSize());
		}

					/**------------------------------------------------------
	 				 * Make and return a font exactly like this font but
					 * with a different BOLD style. 
	 				 * @param flag 		whether the returned font will
					 *			have the BOLD style
	 				 * @return 		the new font
					 *------------------------------------------------------*/
	public		MiFont		setBold(boolean flag)
		{
		if (flag)
			return(MiFontManager.findFont(getName(), getStyle() | MiFont.BOLD, getSize()));
		else
			return(MiFontManager.findFont(getName(), getStyle() & ~MiFont.BOLD, getSize()));
		}

					/**------------------------------------------------------
	 				 * Return whether or not text drawn with this font is 
					 * bold.
	 				 * @return  	true is this font has the BOLD style
					 *------------------------------------------------------*/
	public		boolean		isBold()
		{
		return(super.isBold());
		}

					/**------------------------------------------------------
	 				 * Make and return a font exactly like this font but
					 * with a different ITALIC style. 
	 				 * @param flag 		whether the returned font will
					 *			have the ITALIC style
	 				 * @return 		the new font
					 *------------------------------------------------------*/
	public		MiFont		setItalic(boolean flag)
		{
		if (flag)
			return(MiFontManager.findFont(getName(), getStyle() | MiFont.ITALIC, getSize()));
		else
			return(MiFontManager.findFont(getName(), getStyle() & ~MiFont.ITALIC, getSize()));
		}

					/**------------------------------------------------------
	 				 * Return whether or not text drawn with this font is 
					 * italic.
	 				 * @return  	true is this font has the ITALIC style
					 *------------------------------------------------------*/
	public		boolean		isItalic()
		{
		return(super.isItalic());
		}

					/**------------------------------------------------------
	 				 * Make and return a font exactly like this font but
					 * with a different UNDERLINE style. 
	 				 * @param flag 		whether the returned font will
					 *			have the UNDERLINE style
	 				 * @return 		the new font
					 *------------------------------------------------------*/
	public		MiFont		setUnderlined(boolean flag)
		{
		MiFont font = new MiFont(getName(), getStyle(), getSize());
		font.underLined = flag;
		return(font);
		}

					/**------------------------------------------------------
	 				 * Return whether or not text drawn with this font is 
					 * underlined.
	 				 * @return  	true is this font has the UNDERLINE style
					 *------------------------------------------------------*/
	public		boolean		isUnderlined()
		{
		return(underLined);
		}

					/**------------------------------------------------------
	 				 * Make and return a font exactly like this font but
					 * with a different STRIKEOUT style. 
	 				 * @param flag 		whether the returned font will
					 *			have the STRIKEOUT style
	 				 * @return 		the new font
					 *------------------------------------------------------*/
	public		MiFont		setStrikeOut(boolean flag)
		{
		MiFont font = new MiFont(getName(), getStyle(), getSize());
		font.strikeOut = flag;
		return(font);
		}

					/**------------------------------------------------------
	 				 * Return whether or not text drawn with this font is 
					 * striked-out.
	 				 * @return  	true is this font has the STRIKEOUT style
					 *------------------------------------------------------*/
	public		boolean		isStrikeOut()
		{
		return(strikeOut);
		}


	public		int		hashCode()
		{
		int code = super.hashCode();
		code |= underLined ? UNDERLINE : 0;
		code |= strikeOut ? STRIKEOUT : 0;
		return(code);
		}
	public		boolean		equals(Object other)
		{
		if (other == this)
			{
			return(true);
			}
		if (!(other instanceof MiFont))
			{
			return(false);
			}

		if (!super.equals(other))
			{
			return(false);
			}

		MiFont f = (MiFont )other;
		return((f.underLined == underLined) && (f.strikeOut == strikeOut));
		}



	//***************************************************************************************
	// Private methods
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Returns whether or not this font is being drawn when
					 * the given font is drawn at the given scale.
	 				 * @param font 		the source font
	 				 * @param scale 	the scale factor
	 				 * @return	 	true if this font replaces given
					 *			font at the given scale.
					 *------------------------------------------------------*/
	protected	boolean		isSubstitutingForFont(MiFont font, MiScale scale)
		{
		return(((font == substitutingForFont) 
			|| (font.equals(substitutingForFont)))
		    && (substitutingForFontAtScale != null) 
			&& (scale.equals(substitutingForFontAtScale)));
		}

					/**------------------------------------------------------
	 				 * Specify that this font is being drawn when
					 * the given font is drawn at the given scale.
	 				 * @param font 		the source font
	 				 * @param scale 	the scale factor
					 *------------------------------------------------------*/
	protected	void		setSubstitutingForFont(MiFont font, MiScale scale)
		{
		substitutingForFont = font;
		substitutingForFontAtScale.copy(scale);
		}

	//***************************************************************************************
	// Private methods
	//***************************************************************************************

	private		void		initFontMetrics()
		{
		if (fontHeight == 0)
			{
			fontMetrics = MiFontManager.getFontMetrics(this);
			fontHeight = fontMetrics.getHeight();
			//if (MiSystem.getJDKVersion() >= 1.2)
				//fontHeight /= 2;

			fontDescent = fontMetrics.getMaxDescent();

			maxCharWidth = fontMetrics.getMaxAdvance();
			if (maxCharWidth == -1)
				{
				int[] widths = fontMetrics.getWidths();
				int maxWidth = 0;
				averageCharWidth = 0;
				for (int i = 0; i < widths.length; ++i)
					{
					if (maxWidth < widths[i])
						maxWidth = widths[i];
					averageCharWidth += widths[i];
					}
				if (maxCharWidth == -1)
					maxCharWidth = maxWidth;
				averageCharWidth /= 256;
				}

			averageCharWidth = fontMetrics.charWidth('S');

			maxCharHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
			}
		}
	}


