
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

import com.swfm.mica.util.IntVector;
import java.text.AttributedCharacterIterator;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.util.Hashtable;
import java.io.*;


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPostScriptDriver extends Graphics implements MiiTypes, MiiPrintDriver
	{
	private static	MiPropertyDescriptions		propertyDescriptions;

	private	final	String		prolog[]		= 
		{
		"%%Title: Mica Graphics Framework",
		"%%Creator: Mica Graphics Framework",
		"%%Pages: (atend)",
		"%%EndComments",
		"/S /stroke load def",
		"/s /setgray load def",
		"/c /setrgbcolor load def",
		"/d /setdash load def",
		"/m /moveto load def",
		"/q /gsave load def",
		"/Q /grestore load def",
		"/l { lineto stroke } bind def",
		"/r { moveto lineto lineto lineto closepath stroke } bind def",
		"/R { moveto lineto lineto lineto closepath eofill } bind def",
		"/t { moveto show } bind def",
		"/clip { moveto lineto lineto lineto closepath eoclip newpath } bind def",
		"/w /setlinewidth load def",
		"%%EndProlog",
		"%%BeginSetup",
		"%%EndSetup"
		};

	private	static final char[]	hexDidget	
		= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private	static final int[]	bitMaskTable 			= { 8, 4, 2, 1 };

	private	final	int		GRAY_COLOR_SCHEME		= 1;
	private	final	int		BW_COLOR_SCHEME			= 2;
	private	final	int		COLOR_COLOR_SCHEME		= 3;
	private final   int		NUM_WRITES_BEFORE_STROKING	= 20;

	private final	int		PORTRAIT			= 1;
	private final	int		LANDSCAPE 			= 2;


	private final	double		POINTS_PER_INCH		= 72.0;

	private 	MiPaperSize	paperSize			= MiiPaperSize.standardLetter;
	private 	int		NUM_COLORS_IN_IMAGE_STRING_OUTPUT_BUFFER	= 16;

	private		int		pageHeight;
	private		int		paperOrientation 	= PORTRAIT;
	private		boolean		encapsulatedPSFormat 	= false;
	private		int		dotsPerInch		= 300;
	private		double		threshold 		= 0.5;
	private		int		colorOutputScheme 	= COLOR_COLOR_SCHEME;

	private		PrintWriter 	outputStream;
	private		String		buffer;

	private		int		image_number		= 0;

	private		int		currentPageNumber	= 1;
	private		int		totalNumberOfPages	= 1;

	private		int		currentWritemode	= Mi_COPY_WRITEMODE;
	private		Font		currentFont		= null;
	private		Color		currentColor 		= Color.black;
	private		Color		currentBGColor		= null;
	private		int		currentLineWidth 	= 2;
	private		int		currentLineStyle 	= -1;
	private		Rectangle	currentClipRect 	= new Rectangle(
								0, 0, (int )(300 * 8.5), 300 * 11);
	private		Hashtable	javaToPSFontNameTable	= new Hashtable(11);


	private		MiMargins	minPrinterMarginInInches= new MiMargins(0.375, 0.50, 0.375, 0.50);
	private		MiMargins	margins			= new MiMargins(0.0, 0.0, 0.0, 0.0);
	private		MiBounds	renderingBounds 	= new MiBounds(); 
	private		MiBounds	contentBounds 		= new MiBounds(); 

	public				MiPostScriptDriver()
		{
		javaToPSFontNameTable.put("Symbol", "Symbol");
		javaToPSFontNameTable.put("Times", "Times");
		javaToPSFontNameTable.put("Helvetica", "Helvetica");
		javaToPSFontNameTable.put("Courier", "Courier");
		javaToPSFontNameTable.put("Dialog", "Times");
		}
	public		Graphics	getGraphics()
		{
		return(this);
		}
	public		boolean		configure(
						String filename, 
						int dotsPerInch, 
						String standardPaperSize, 
						String portraitOrLandscape, 
						String colorOutputScheme,
						int totalNumberOfPages)
		{
		if (filename != null)
			{
			try	{
				outputStream = new PrintWriter(new FileOutputStream(filename));
				}
			catch (Exception e)
				{
				throw new RuntimeException(this + ": Unable to open file: \"" 
					+ filename + "\"" + e.getMessage());
				}
			}

		this.totalNumberOfPages = totalNumberOfPages;
		setDotsPerInch(dotsPerInch);
		setPageOrientation(portraitOrLandscape);
		setPaperSize(paperSize);
		setColorOutputScheme(colorOutputScheme);
		return(true);
		}

	public		Graphics	create()
		{
    		MiPostScriptDriver ps = new MiPostScriptDriver();
    		ps.currentFont = currentFont;
		ps.currentClipRect = currentClipRect;
		ps.currentColor = currentColor;
    		return(ps);
		}

	public		void		setEncapsulatedPSFormat(boolean flag)
		{
		encapsulatedPSFormat = flag;
		}

	public		MiBounds	getDeviceBounds()
		{
		reCalcRenderingBounds();
		return(new MiBounds(renderingBounds));
		}
	
	public		MiMargins 	getMargins()
		{
		return(margins);
		}
	public		void		init()
		{
		image_number = 0;
		write_prolog();
		writeBoundingBox();
		currentPageNumber = 1;
		println("%%Page: " + currentPageNumber + " " + totalNumberOfPages);
		println("q");
		println("newpath");
		println("2 setlinewidth");
		set_up_transformations();
		setColor(currentColor);
		setFont(currentFont);
		}

	public		void 		termin()
		{
		if (outputStream == null)
			return;

		stroke();
		write_epilog();
		outputStream.flush();
		if (outputStream != null)
			outputStream.close();
		outputStream = null;
		}

					// Necessary for EPS
	public		void 		setBoundsOfGraphicalContent(MiBounds bounds)
		{
		contentBounds.copy(bounds);
		}
	public		void		setPaperSize(MiPaperSize size)
		{
		paperSize = size;
		reCalcRenderingBounds();
		}
	public		MiPaperSize	getPaperSize()
		{
		return(paperSize);
		}
	public		void		setDotsPerInch(int dotsPerInch)
		{
		this.dotsPerInch = dotsPerInch;
		}

	public		boolean		setPageOrientation(String orientation)
		{
		if (orientation.equalsIgnoreCase(Mi_PORTRAIT_NAME))
			{
			paperOrientation = PORTRAIT;
			}
		else if (orientation.equalsIgnoreCase(Mi_LANDSCAPE_NAME))
			{
			paperOrientation = LANDSCAPE;
			}
		else
			{
			return(false);
			}
		reCalcRenderingBounds();
		return(true);
		}
	public		boolean 	setColorOutputScheme(String scheme)
		{
		if (scheme.equalsIgnoreCase("COLOR"))
			{
			colorOutputScheme = COLOR_COLOR_SCHEME;
			}
		else if (scheme.equalsIgnoreCase("BW"))
			{
			colorOutputScheme = BW_COLOR_SCHEME;
			}
		else if (scheme.equalsIgnoreCase("GREY"))
			{
			colorOutputScheme = GRAY_COLOR_SCHEME;
			}
		else
			{
			return(false);
			}
		return(true);
		}
	public		void 		newPage()
		{
		stroke();
		println("q\nshowpage\nQ");
		writeBoundingBox();
		println("%%Page: " + ++currentPageNumber + " " + totalNumberOfPages);
		}

	//--------------------------------------------------------------------------
	public		void 		setLineStyle(int style)
		{
		if (style != currentLineStyle)
			{
			stroke();
			if (style == Mi_SOLID_LINE_STYLE)
				{
				print("[] 0 d\n");
				}
			else if (style == Mi_DASHED_LINE_STYLE)
				{
				print("[3] 0 d\n");
				}
			else if (style == Mi_DOUBLE_DASHED_LINE_STYLE)
				{
				print("[2 1] 0 d\n");
				}
			else
				{
				return;
				}
			currentLineStyle = style;
			}
		}

	public		void		setLineWidth(int lwidth)
		{
		if (lwidth != currentLineWidth)
			{
			println((lwidth + 1) + " w");
			currentLineWidth = lwidth;
			}
		}






	public 		void 		translate(int x, int y)
		{
		println(x + " " + y + " translate");
		}
	public 		Color 		getColor()
		{
		return(currentColor);
		}
	public		void 		setColor(Color color)
		{
		if (color != currentColor)
			{
			int red, green, blue;
	
			currentColor = color;
			if (currentWritemode == Mi_XOR_WRITEMODE)
				{
/////// 				color = color ^ currentBGColor;
				}
			if (colorOutputScheme == COLOR_COLOR_SCHEME)
				{
				println(color.getRed()/255.0
					+ " " + color.getGreen()/255.0
					+ " " + color.getBlue()/255.0
    					+ " c");
				}
			else
				{
				double Y = 0.299 * color.getRed() 
					+ 0.587 * color.getGreen() 
					+ 0.114 * color.getBlue();

				if (colorOutputScheme == BW_COLOR_SCHEME)
					{
					if (Y > (threshold * 256))
						Y = 255;
					else
						Y = 0;
					}
			
				println(((double )Y)/255 + " s");
				}
			}
		}
	public		void 		setPaintMode()
		{
		currentWritemode = Mi_COPY_WRITEMODE;
		}
	public		void 		setXORMode(Color c1)
		{
		MiDebug.println(MiDebug.getMicaClassName(this) + ": setXORMode not supported");
		currentWritemode = Mi_XOR_WRITEMODE;
		}
	public 		Font 		getFont()
		{
		return(currentFont);
		}
	public		void 		setFont(Font font)
		{
		if (font != currentFont)
			{
			currentFont = font;
      			int javaFontStyle = font.getStyle();
      			String psFontName = getPSFontNameFromJavaFontName(font.getName(), javaFontStyle);

			if (psFontName == null)
				{
				psFontName = "Courier";
				}

			switch (javaFontStyle)
				{
				case Font.PLAIN:
					break;
				case Font.BOLD:
					psFontName += "-Bold";
					break;
				case Font.ITALIC:
					if (psFontName.startsWith("Times"))
						psFontName += "-Italic";
					else
						psFontName += "-Oblique";
					break;
				case (Font.ITALIC + Font.BOLD):
					if (psFontName.startsWith("Times"))
						psFontName += "-BoldItalic";
					else
						psFontName += "-BoldOblique";
					break;
				}
      			println("/" + psFontName + " findfont " + font.getSize() + " scalefont setfont");
			}
		}
	public 		FontMetrics 	getFontMetrics(Font f)
		{
		return(Toolkit.getDefaultToolkit().getFontMetrics(f));
		}
	public		Shape		getClip()
		{
		return(currentClipRect);
		}
	public 		Rectangle 	getClipBounds()
		{
		return(currentClipRect);
		}
/***
	public 		Rectangle 	getClipRect()
		{
		return(currentClipRect);
		}
****/
	public		void		setClip(Shape shape)
		{
		// FIX: iterate through the path of the shape...
		Rectangle rect = shape.getBounds();
		setClip(rect.x, rect.y, rect.width, rect.height);
		}
	public		void 		setClip(int x, int y, int width, int height)
		{
		y = flipY(y);
    		currentClipRect = new Rectangle(x, y, width, height);
    		println("initclip " + x + " " + y 
			+ " " + (x + width) + " " + y
    			+ " " + (x + width) + " " + (y - height) 
			+ " " + x  + " " + (y - height) + " clip");
		}
	public		void 		clipRect(int x, int y, int width, int height)
		{
		y = flipY(y);
    		currentClipRect = new Rectangle(x, y, width, height);
    		println("initclip " + x + " " + y 
			+ " " + (x + width) + " " + y
    			+ " " + (x + width) + " " + (y - height) 
			+ " " + x  + " " + (y - height) + " clip");
		}
	public 	void 		copyArea(int x, int y, int width, int height, int dx, int dy)
		{
		throw new RuntimeException("copyArea not supported");
		}
	public		void 		drawLine(int x1, int y1, int x2, int y2)
		{
		y1 = flipY(y1);
		y2 = flipY(y2);
		// Make sure 'points' show up
		if ((x1 == x2) && (y1 == y2) && (currentLineWidth < 1))
			{
			println("1 w");
			println(x2 + " " + y2 + " m " + x1 + " " + y1 + " l");
			println(currentLineWidth + " w");
			}
		else
			{
			println(x2 + " " + y2 + " m " + x1 + " " + y1 + " l");
			}
		}
	public		void 		fillRect(int x, int y, int width, int height)
		{
		y = flipY(y);
		int x2 = x + width;
		int y2 = y - height;
		println(x + " " + y + " " + x2 + " " + y + " " + x2
			+ " " + y2 + " " +  x + " " +  y2 + " R");
		}
	public		void 		drawRect(int x, int y, int width, int height) 
		{
		y = flipY(y);
		int x2 = x + width;
		int y2 = y - height;
		println(x + " " + y + " " + x2 + " " + y 
			+ " " + x2 + " " + y2 + " " + x + " " + y2 + " r");
		}
	public		void 		clearRect(int x, int y, int width, int height)
		{
		println("q");
		Color oldColor = currentColor;
		setColor(currentBGColor);
		fillRect(x, y, width, height);
		setColor(oldColor);
		println("Q");
		}
	public		void 		drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
		{
		drawRoundRect(x, y, width, height, arcWidth, arcHeight, false);
		}

	protected	void 		drawRoundRect(int x, int y, int width, int height, 
						int arcWidth, int arcHeight,
						boolean filled)
		{
		y = flipY(y);

		println((x + arcHeight) + " " + y + " m");

		// Top, left then right
		println((x + width) + " " + y + " " + (x + width) + " " + (y - height) + " " + arcHeight + " "
			+ "arcto 4 {pop} repeat");

    		// Right, top then bottom
		println((x + width) + " " + (y - height) + " " + x + " " + (y - height) + " " + arcHeight + " "
			+ "arcto 4 {pop} repeat");

		// top, left to right
		println(x + " " + (y - height) + " " + x + " " + y + " " + arcHeight + " "
			+ "arcto 4 {pop} repeat");

		// Left, top then bottom
		println(x + " " + y + " " +  (x + width) + " " + y + " " + arcHeight + " "
			+ "arcto 4 {pop} repeat");

		if (filled)
			println("eofill");
		else
			println("stroke");
		}
	public		void 		fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
		{
		drawRoundRect(x, y, width, height, arcWidth, arcHeight, true);
		}

	public		void 		drawOval(int x, int y, int width, int height)
		{
		drawArc(x, y, width, height, 0, 360, false);
		}
	public		void 		fillOval(int x, int y, int width, int height)
		{
		drawArc(x, y, width, height, 0, 360, true);
		}
	public		void 		drawArc(int x, int y, int width, int height,
						int startAngle, int arcAngle)
		{
		drawArc(x, y, width, height, startAngle, arcAngle, false);
		}
	public		void 		fillArc(int x, int y, int width, int height, 
						int startAngle, int arcAngle)
		{
		drawArc(x, y, width, height, startAngle, arcAngle, true);
		}
	/* See pg. 137, PostScript Language "Tutorial and Cookbook", Adobe Systems Inc, 1985 */
	protected	void 		drawArc(int x, int y, int width, int height,
						int startAngle, int arcAngle, boolean filled)
		{
		println("q");

		y = flipY(y);

		// Translate the current coordinate system to the center of the arc
		float cx = x + ((float)width)/2;
		float cy = y - ((float)height)/2;
		println(cx + " " + cy + " translate");

		// Scale the coordinate system
		float yscale = ((float)height)/(float)width;
    		println(1.0 + " " + yscale + " scale");

		if (filled)
			{
			println("0 0 m");
			}

		float endAngle = startAngle + arcAngle;
		println("0 0 " + ((float)width/2.0) + " " + startAngle + " " + endAngle + " arc");
		if (filled)
			{
			println("closepath eofill");
			}
		else
			{
			println("stroke");
			}
		println("Q");
		}
	public		void 		drawPolyline(int xPoints[], int yPoints[], int nPoints)
		{
		int x1 = xPoints[0];
		int y1 = flipY(yPoints[0]);
		println(x1 + " " + y1 + " m");
		for (int i = 1; i < nPoints; ++i)
			{
			int x2 = xPoints[i];
			int y2 = flipY(yPoints[i]);
			println(x2 + " " + y2 + " lineto");
			}
		println("S");
		}
	public		void 		drawPolygon(int xPoints[], int yPoints[], int nPoints)
		{
		int x1 = xPoints[0];
		int y1 = flipY(yPoints[0]);
		println(x1 + " " + y1 + " m");
		for (int i = 1; i < nPoints; ++i)
			{
			int x2 = xPoints[i];
			int y2 = flipY(yPoints[i]);
			println(x2 + " " + y2 + " lineto");
			}
		println("closepath S");
		}
	public		void 		fillPolygon(int xPoints[], int yPoints[], int nPoints)
		{
		int x1 = xPoints[0];
		int y1 = flipY(yPoints[0]);
		println(x1 + " " + y1 + " m");
		for (int i = 1; i < nPoints; ++i)
			{
			int x2 = xPoints[i];
			int y2 = flipY(yPoints[i]);
			println(x2 + " " + y2 + " lineto");
			}
		println("closepath fill");
		}
	public		void 		drawString(String str, int x, int y)
		{
		y = flipY(y);
		println("(" + str + ") " + x + " " + y + " t");
		}
	public		void		drawString(AttributedCharacterIterator iterator, int x, int y)
		{
		// FIX: To do
		}

	public		boolean 	drawImage(Image img, int x, int y, int width, int height, 
				      		ImageObserver observer)
		{
		drawImage(img, x, y, width, height, observer, null);
		return(true);
		}

	public 		boolean 	drawImage(Image img, int x, int y, ImageObserver observer)
		{
		drawImage(img, x, y, 0, 0, observer, null);
		return(true);
		}
	public 		boolean 	drawImage(Image img, int x, int y, 
						Color bgcolor, ImageObserver observer)
		{
		drawImage(img, x, y, 0, 0, observer, bgcolor);
		return(true);
		}
	public 		boolean 	drawImage(Image img, int x, int y, int width, int height, 
						Color bgcolor, ImageObserver observer)
		{
		drawImage(img, x, y, width, height, observer, bgcolor);
		return(true);
		}
	public		boolean		drawImage(Image img,
				      		int dx1, int dy1, int dx2, int dy2,
				      		int sx1, int sy1, int sx2, int sy2,
				      		ImageObserver observer)
		{
		// FIX: crop the source image and test for flipped images
		int x = Math.min(dx1, dx2);
		int y = Math.min(dy1, dy2);
		int width = Math.abs(dx2 - dx1);
		int height = Math.abs(dy2 - dy1);
		drawImage(img, x, y, width, height, observer);
		return(true);
		}
	public		boolean		drawImage(Image img,
					      int dx1, int dy1, int dx2, int dy2,
					      int sx1, int sy1, int sx2, int sy2,
					      Color bgcolor,
					      ImageObserver observer)
		{
		// FIX: crop the source image and test for flipped images
		int x = Math.min(dx1, dx2);
		int y = Math.min(dy1, dy2);
		int width = Math.abs(dx2 - dx1);
		int height = Math.abs(dy2 - dy1);
		drawImage(img, x, y, width, height, observer, bgcolor);
		return(true);
		}
	public 		void 		dispose()
		{
		termin();
		}

	protected	void		drawImage(Image image, int x, int y, int width, int height,
						ImageObserver observer, Color bgColor)
		{
		println("q");

		y = flipY(y);

		if ((width == 0) || (height == 0))
			{
			width = image.getWidth(observer);
			height = image.getHeight(observer);
			}
		int image_width = image.getWidth(null);
		int image_height = image.getHeight(null);

		int[] pixels = MiImageManager.getImagePixels(image, 0, 0, image_width, image_height);

		// Map image to desired resolution 
		println(x + " " +  (y - height) + " translate");

		// Map image to desired resolution 
		println(width + " " +  height + " scale");




//System.out.println("image_height = " + image_height);
//System.out.println("image_width = " + image_width);
//System.out.println("pixels.length = " + pixels.length);

		int index = 0;
		int image_fgColor = 0;
		int image_bgColor = 0;
		boolean transparentColorsFoundInImage = false;
		IntVector colorsInImage = null;
		if (bgColor != null)
			{
			// Assume this means that the image has only 2 colors...
            		image_fgColor = currentColor.getGreen() << 16 + currentColor.getBlue() << 8 
					+ currentColor.getRed();
            		image_bgColor = bgColor.getGreen() << 16 + bgColor.getBlue() << 8 
					+ bgColor.getRed();
			}
		else
			{
			colorsInImage = new IntVector();
			for (int row = 0; row < image_height; ++row)
				{
				for (int col = 0; col < image_width; ++col)
					{
					int	color = pixels[index++];
					// If more transparent than not....
					if ((color & 0xff000000) != 0xff000000)
						transparentColorsFoundInImage = true;
					else if (!colorsInImage.contains(color))
						colorsInImage.addElement(color);
					}
				}
			}
		if (transparentColorsFoundInImage)
			{
			char[] buffer = new char[((image_width + 3)/4 + 1)/2 * 2];
			IntVector colorMask = new IntVector(256);

			// String to hold image data
			println("/picstr" + image_number + " " + buffer.length/2 + " string def");

			for (int colorNum = 0; colorNum < colorsInImage.size(); ++colorNum)
				{
				// Color to set the pixels in the mask to
				int color = colorsInImage.elementAt(colorNum);
				setColor(new Color(color));

				// Dimensions of source image (true => 1 = paint)
				println(image_width + " " + image_height + " true");

				// Map unit square to device (turn upside down)
				println("[" + image_width + " 0 0 -" + image_height + " 0 " + image_height + "]");
				// Read image data from program file
				println("{currentfile picstr" + image_number + " readhexstring pop}");
				println("imagemask");

				index = 0;
				for (int row = 0; row < image_height; ++row)
					{
					colorMask.removeAllElements();
					for (int col = 0; col < image_width; ++col)
						{
						if (color == pixels[index++])
							{
							int charPosition = col/4;
							int bitPosition = col - charPosition * 4;
							while (colorMask.size() <= charPosition)
								colorMask.addElement(0);
							int currentBits = colorMask.elementAt(charPosition);
							colorMask.setElementAt(
								currentBits + bitMaskTable[bitPosition],
								charPosition);
							}
						}
					int colorMaskSize = colorMask.size();
					for (int i = 0; i < colorMaskSize; ++i)
						{
						buffer[i] = hexDidget[colorMask.elementAt(i)];
						}
					for (int i = colorMaskSize; i < buffer.length; ++i)
						buffer[i] = '0';

					println(new String(buffer, 0, buffer.length));
					}
				}
			setColor(currentColor);
			}
		else
			{
			char[] buffer = new char[NUM_COLORS_IN_IMAGE_STRING_OUTPUT_BUFFER * 6];
			int offset = 0;
			int numColorsOutput = 0;

			// String to hold image data
			println("/picstr" + image_number + " " + (image_width * 3) + " string def");

			// Dimensions of source image (depth = 8)
			println(image_width + " " + image_height + " 8");

			// Map unit square to device (turn upside down)
			println("[" + image_width + " 0 0 -" + image_height + " 0 " + image_height + "]");

			// Read image data from program file
			println("{currentfile picstr" + image_number + " readhexstring pop}");

			// Single data source, 3 colors
			println("false 3 colorimage");

			index = 0;
			for (int row = 0; row < image_height; ++row)
				{
				for (int col = 0; col < image_width; ++col)
					{
					int	color = pixels[index++];
					if (bgColor != null)
						{
						if (color == 1)
							color = image_fgColor;
						else
							color = image_bgColor;
						}
	
					buffer[offset++] = hexDidget[(color & 0xF0)     >>  4];
					buffer[offset++] = hexDidget[(color & 0xF)           ];
					buffer[offset++] = hexDidget[(color & 0xF000)   >> 12];
					buffer[offset++] = hexDidget[(color & 0xF00)    >>  8];
					buffer[offset++] = hexDidget[(color & 0xF00000) >> 20];
					buffer[offset++] = hexDidget[(color & 0xF0000)  >> 16];
	
					++numColorsOutput;
					if (numColorsOutput >= NUM_COLORS_IN_IMAGE_STRING_OUTPUT_BUFFER)
						{
						println(new String(buffer, 0, offset));
						numColorsOutput = 0;
						offset = 0;
						}
					}
				}
			if (offset != 0)
				println(new String(buffer, 0, offset));

			}
		println("Q");
		}
	//--------------------------------------------------------------------------
	private		void 		reCalcRenderingBounds()
		{
		pageHeight = (int )(dotsPerInch * paperSize.getSize().getHeight());

		MiSize pageSizeInInches = paperSize.getSize();
		if (paperOrientation == LANDSCAPE)
			{
			MiDistance tmp = pageSizeInInches.getWidth();
			pageSizeInInches.setWidth(pageSizeInInches.getHeight());
			pageSizeInInches.setHeight(tmp);
			}

		renderingBounds.xmin = (minPrinterMarginInInches.left + margins.left) * dotsPerInch;
		renderingBounds.ymin = (minPrinterMarginInInches.bottom + margins.bottom) * dotsPerInch;
		renderingBounds.xmax 
			= (pageSizeInInches.getWidth() - minPrinterMarginInInches.right - margins.right) 
			* dotsPerInch;
		renderingBounds.ymax 
			= (pageSizeInInches.getHeight() - minPrinterMarginInInches.top - margins.top) 
			* dotsPerInch;
		}

	private		void 			stroke()
		{
		outputStream.println("S");
		}
	private		void 			print(String str)
		{
		outputStream.print(str);
		}

	private		void 			println(String str)
		{
		outputStream.println(str);
		}

	private		void 			write_prolog()
		{
		if (encapsulatedPSFormat)
			println("%!PS-Adobe-3.0 EPSF-3.0");
		else
			println("%!PS-Adobe-1.0");

		for (int i = 0; i < prolog.length; ++i)
			{
			println(prolog[i]);
			}
		}
	private		void 			write_epilog()
		{
		println("Q");
		if (!encapsulatedPSFormat)
			println("showpage");
		}
	private		void 			writeBoundingBox()
		{
		int xmin = (int )(contentBounds.xmin * POINTS_PER_INCH/dotsPerInch);
		int ymin = (int )(contentBounds.ymin * POINTS_PER_INCH/dotsPerInch);
		int xmax = (int )(contentBounds.xmax * POINTS_PER_INCH/dotsPerInch);
		int ymax = (int )(contentBounds.ymax * POINTS_PER_INCH/dotsPerInch);

		println("%%BoundingBox: " + xmin + " " + ymin + " " + xmax + " " + ymax);
		}

	private		void 		set_up_transformations()
		{
		reCalcRenderingBounds();

		// Map image to desired resolution
		println(((double )POINTS_PER_INCH)/dotsPerInch + " " +
			((double )POINTS_PER_INCH)/dotsPerInch + " scale");

		if (!encapsulatedPSFormat)
			{
			if (paperOrientation == LANDSCAPE)
				{
				println(pageHeight + " 0 translate");
				// Locate lower-left corner of image
				println("90 rotate");
				}
			else
				{
				// Locate lower-left corner of image
				println("0 0 translate");
				}
			}
		}
	protected	String		getPSFontNameFromJavaFontName(String javaFontName, int javaFontStyle)
		{
		if (javaFontName.startsWith("Times"))
			{
			String psFontName = "Times";
			if (javaFontStyle == Font.PLAIN)
				psFontName += "-Roman";
			return(psFontName);
			}
		return((String )javaToPSFontNameTable.get(javaFontName));
		}
	protected	int		flipY(int y)
		{
		return(pageHeight - y);
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

		propertyDescriptions = new MiPropertyDescriptions();
		return(propertyDescriptions);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given
					 * value. 
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		throw new IllegalArgumentException(this + ": Unknown property: \"" + name + "\"");
		}

					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		throw new IllegalArgumentException(this + ": Unknown property: \"" + name + "\"");
		}
	}
class MiEncapsulatedPostScriptDriver extends MiPostScriptDriver
	{
	public				MiEncapsulatedPostScriptDriver()
		{
		setEncapsulatedPSFormat(true);
		}
	}

