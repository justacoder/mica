
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
import java.util.Enumeration;
import java.io.*;


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPDFDriver extends Graphics implements MiiTypes, MiiPrintDriver
	{
	private static	MiPropertyDescriptions		propertyDescriptions;

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
	private		int		dotsPerInch		= 300;
	private		double		threshold 		= 0.5;
	private		int		colorOutputScheme 	= COLOR_COLOR_SCHEME;

	private		PrintWriter 	outputStream;
	private		String		buffer;

	private		int		totalNumberOfPages;

	private		int		image_number		= 0;
	private		int		numberOfPDFObjects	= 1;
	private		int		currentPDFFileOffset	= 0;
	private		IntVector	pdfObjectOffsets	= new IntVector();
	private		IntVector	pageObjectNumbers	= new IntVector();

	private		Hashtable	fontResources		= new Hashtable();
	private		Hashtable	xObjectResources	= new Hashtable();
	private		Hashtable	xObjectDefinitions	= new Hashtable();

	private		int		streamStartOffset;
	private		int		streamLengthObjectNumber;
	private		int		xrefStartOffset;

	private		int		rootPagesObjectNumber;
	private		int		catalogObjectNumber;
	private		int		procSetObjectNumber;
	private		int		fontResourcesObjectNumber;
	private		int		xObjectResourcesObjectNumber;
	private		int		outlinesObjectNumber;

	private		boolean		aStreamIsOpen;
	private		boolean		aClipPathIsSpecified;

	private		int		currentWritemode	= Mi_COPY_WRITEMODE;
	private		Font		currentFont		= null;
	private		String		currentFontPsName	= null;
	private		Color		currentColor 		= Color.black;
	private		Color		currentBGColor		= null;
	private		int		currentLineWidth 	= 1;
	private		int		currentLineStyle 	= -1;
	private		Rectangle	currentClipRect 	= new Rectangle(
								0, 0, (int )(300 * 8.5), 300 * 11);
	private		Hashtable	javaToPSFontNameTable	= new Hashtable(11);


	private		MiMargins	minPrinterMarginInInches= new MiMargins(0.0, 0.0, 0.0, 0.0);
	private		MiMargins	margins			= new MiMargins(0.0, 0.0, 0.0, 0.0);
	private		MiBounds	renderingBounds 	= new MiBounds(); 
	private		MiBounds	contentBounds 		= new MiBounds(); 

	public				MiPDFDriver()
		{
		javaToPSFontNameTable.put("Symbol", "Symbol");
		javaToPSFontNameTable.put("Times", "Times");
		javaToPSFontNameTable.put("Helvetica", "Helvetica");
		javaToPSFontNameTable.put("Courier", "Courier");
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
    		MiPDFDriver pdf = new MiPDFDriver();
    		pdf.currentFont = currentFont;
		pdf.currentClipRect = currentClipRect;
		pdf.currentColor = currentColor;
    		return(pdf);
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
		aClipPathIsSpecified = false;
		aStreamIsOpen = false;
		image_number = 0;
		currentPDFFileOffset = 0;
		numberOfPDFObjects = 1;
		fontResources.clear();
		xObjectResources.clear();
		xObjectDefinitions.clear();
		pdfObjectOffsets.removeAllElements();
		pdfObjectOffsets.addElement(0);
		write_prolog();
		set_up_transformations();
		setColor(currentColor);
		setFont(currentFont);
		}

	public		void 		termin()
		{
		endStream();
		write_epilog();
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
		endStream();

		pageObjectNumbers.addElement(numberOfPDFObjects);
		println(makeObject() + " 0 obj");
		println("<<");
		println("/Type /Page");
		println("/Parent " + rootPagesObjectNumber + " 0 R");
		println("/Resources <</ProcSet " + procSetObjectNumber + " 0 R /Font " 
			+ fontResourcesObjectNumber + " 0 R /XObject " + xObjectResourcesObjectNumber + " 0 R>>");
		MiBounds bbox = getBoundingBox();
		println("/MediaBox [ " + ((int )bbox.xmin) + " " + ((int )bbox.ymin) + " " 
			+  ((int )bbox.getWidth()) + " " + ((int )bbox.getHeight()) + " ]");
		println("/Contents " + numberOfPDFObjects + " 0 R");
		println(">>");
		println("endobj");

		startStream();
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
	public 		void 		scale(double sx, double sy)
		{
		println(sx + " 0 0 " + sy + " 0 0 cm");
		}
	public 		void 		translate(double x, double y)
		{
		println("1 0 0 1 " + x + " " + y + " cm");
		}






	public 		void 		translate(int x, int y)
		{
		println("1 0 0 1 " + x + " " + y + " cm");
		}
	public 		Color 		getColor()
		{
		return(currentColor);
		}
	public		void 		setColor(Color color)
		{
		if (color != currentColor)
			{
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
	
			currentColor = color;
			if (currentWritemode == Mi_XOR_WRITEMODE)
				{
/////// 				color = color ^ currentBGColor;
				}
			if (colorOutputScheme == COLOR_COLOR_SCHEME)
				{
				if ((red == green) && (red == blue))
					{
					println(red/255.0 + " g");
					println(red/255.0 + " G");
					}
				else
					{
					println(red/255.0 + " " + green/255.0 + " " + blue/255.0 + " rg");
					println(red/255.0 + " " + green/255.0 + " " + blue/255.0 + " RG");
					}
				}
			else
				{
				double Y = 0.299 * red + 0.587 * green + 0.114 * blue;

				if (colorOutputScheme == BW_COLOR_SCHEME)
					{
					if (Y > (threshold * 256))
						Y = 255;
					else
						Y = 0;
					}
			
				println(((double )Y)/255 + " g");
				println(((double )Y)/255 + " G");
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
			if (fontResources.get(psFontName) == null)
				{
				fontResources.put(psFontName, new Integer(reserveObject()));
				}
			currentFontPsName = psFontName;
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
/****
	public 		Rectangle 	getClipRect()
		{
		return(currentClipRect);
		}
****/
	public 		Rectangle 	getClipBounds()
		{
		return(currentClipRect);
		}
	public		void		setClip(Shape shape)
		{
		// FIX: iterate through the path of the shape...
		Rectangle rect = shape.getBounds();
		setClip(rect.x, rect.y, rect.width, rect.height);
		}
	public		void 		setClip(int x, int y, int width, int height)
		{
		if (aClipPathIsSpecified)
			println("Q");
		println("q");
		aClipPathIsSpecified = true;
		y = flipY(y);
    		currentClipRect = new Rectangle(x, y, width, height);
    		print(x + " " + y + " ");
		print((x + width) + " " + y + " ");
		print((x + width) + " " + (y - height) + " ");
		print(x  + " " + (y - height));
		println(" m l l l W* n");
		}
	public		void 		clipRect(int x, int y, int width, int height)
		{
		if (aClipPathIsSpecified)
			println("Q");
		println("q");
		aClipPathIsSpecified = true;
		y = flipY(y);
    		currentClipRect = new Rectangle(x, y, width, height);
    		print(x + " " + y + " ");
		print((x + width) + " " + y + " ");
		print((x + width) + " " + (y - height) + " ");
		print(x  + " " + (y - height));
		println(" m l l l W* n");
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
			println(x2 + " " + y2 + " m " + x1 + " " + y1 + " l S");
			println(currentLineWidth + " w");
			}
		else
			{
			println(x2 + " " + y2 + " m " + x1 + " " + y1 + " l S");
			}
		}
	public		void 		fillRect(int x, int y, int width, int height)
		{
		y = flipY(y);
		int x2 = x + width;
		int y2 = y - height;
		println(x + " " + y + " " + x2 + " " + y + " " + x2
			+ " " + y2 + " " +  x + " " +  y2 + " m l l l b*");
		}
	public		void 		drawRect(int x, int y, int width, int height) 
		{
		y = flipY(y);
		int x2 = x + width;
		int y2 = y - height;
		println(x + " " + y + " " + x2 + " " + y 
			+ " " + x2 + " " + y2 + " " + x + " " + y2 + " m l l l s");
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
		}
	protected	void 		_drawRoundRect(int x, int y, int width, int height, 
						int arcWidth, int arcHeight,
						boolean filled)
		{
		y = flipY(y);

		// Top right
		drawArc(x + width - arcWidth, y - arcHeight, arcWidth, arcHeight, 0, 90, filled);

		// Top left
		drawArc(x + arcWidth, y - arcHeight, arcWidth, arcHeight, 90, 90, filled);

		// Lower left
		drawArc(x + arcWidth, y - height - arcHeight, arcWidth, arcHeight, 180, 90, filled);

		// Lower right
		drawArc(x + width - arcWidth, y - height - arcHeight, arcWidth, arcHeight, 270, 90, filled);


		if (filled)
			{
			// top
			println((x + arcWidth) + " " + y + " m " + (x + width - arcWidth) + " " + y + " l");
			// left
			println(x + " " + (y - arcHeight) + " l " + x + " " + (y - height + arcHeight) + " l");
			// bottom
			println((x + arcWidth) + " " + (y - height) + " l " + (x + width - arcWidth) + " " + (y - height) + " l");
			// right
			println((x + width) + " " + (y - arcHeight) + " l " + (x + width) + " " + (y - height + arcHeight) + " l");
			println("b*");
			}
		else
			{
			// top
			println((x + arcWidth) + " " + y + " m " + (x + width - arcWidth) + " " + y + " l S");
			// bottom
			println((x + arcWidth) + " " + (y - height) + " m " + (x + width - arcWidth) + " " + (y - height) + " l S");
			// left
			println(x + " " + (y - arcHeight) + " m " + x + " " + (y - height + arcHeight) + " l S");
			// right
			println((x + width) + " " + (y - arcHeight) + " m " + (x + width) + " " + (y - height + arcHeight) + " l S");
			}
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
	protected	void 		drawArc(int x, int y, int width, int height,
						int startAngle, int arcAngle, boolean filled)
		{
		}
	protected	void 		_drawArc(int x, int y, int width, int height,
						int startAngle, int arcAngle, boolean filled)
		{
		y = flipY(y);

		double cx = x + width * Math.cos(startAngle);
		double cy = y + height * Math.sin(startAngle);

		// Set the current point 
		println(cx + " " + cy + " m");

		// Approximate curve using a number of lines
		for (int angle = startAngle; angle < startAngle + arcAngle; ++angle)
			{
			cx = x + width * Math.cos(angle);
			cy = y + height * Math.sin(angle);
			println(((float) cx) + " " + ((float )cy) + " l");
			}

		if (filled)
			{
			println(x + " " + y + " l b*");
			}
		else
			{
			println("S");
			}
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
			println(x2 + " " + y2 + " l");
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
			println(x2 + " " + y2 + " l");
			}
		println("s");
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
			println(x2 + " " + y2 + " l");
			}
		println("b*");
		}
	public		void 		drawString(String str, int x, int y)
		{
		y = flipY(y);
		println("BT");
		println("/" + currentFontPsName + " " + currentFont.getSize() + " Tf");
		println(x + " " + y + " Td (" + str + ") Tj");
		println("ET");
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
		outputStream.flush();
		}

	protected	void		drawImage(Image image, int x, int y, int width, int height,
						ImageObserver observer, Color bgColor)
		{
		//if (image_number > 5)
			//return;
/*
		}
	protected	void		_drawImage(Image image, int x, int y, int width, int height,
						ImageObserver observer, Color bgColor)
		{
*/

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
		translate(x, y - height);

		// Map image to desired resolution 
    		scale(width, height);




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

			// Map unit square to device (turn upside down)
			// TEST println("[" + image_width + " 0 0 -" + image_height + " 0 " + image_height + "]");

			int tmp_image_number = image_number;
			for (int colorNum = 0; colorNum < colorsInImage.size(); ++colorNum)
				{
				int color = colorsInImage.elementAt(colorNum);
				setColor(new Color(color));
				println("/image" + tmp_image_number++ + " Do");
				}

			println("Q");

			for (int colorNum = 0; colorNum < colorsInImage.size(); ++colorNum)
				{
				// Color to set the pixels in the mask to
				int color = colorsInImage.elementAt(colorNum);

				int xObjectObjectNumber = reserveObject();

				xObjectResources.put("image" + image_number, new Integer(xObjectObjectNumber));
				StringBuffer xObjectDef = new StringBuffer(1000);

				xObjectDef.append(xObjectObjectNumber + " 0 obj\n");
				xObjectDef.append("<<\n");
				xObjectDef.append("/Type /XObject\n");
				xObjectDef.append("/Subtype /Image\n");
				xObjectDef.append("/Name /image" + image_number++ + "\n");
				xObjectDef.append("/Width " + image_width + "\n");
				xObjectDef.append("/Height " + image_height + "\n");
				xObjectDef.append("/Filter /ASCIIHexDecode\n");
				xObjectDef.append("/BitsPerComponent 1\n");
				xObjectDef.append("/ImageMask true\n");
				xObjectDef.append("/Length " + buffer.length*image_height + "\n");
				xObjectDef.append(">>\n");
				xObjectDef.append("stream\n");

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
						buffer[i] = hexDidget[colorMask.elementAt(i) ^ 0xf];
						}
					for (int i = colorMaskSize; i < buffer.length; ++i)
						buffer[i] = 'f';

					xObjectDef.append(buffer, 0, buffer.length);
					xObjectDef.append("\n");
					}
				xObjectDef.append("endstream\n");
				xObjectDef.append("endobj\n");
				xObjectDefinitions.put(new Integer(xObjectObjectNumber), new String(xObjectDef));
				}
			setColor(currentColor);
			}
		else
			{
			char[] buffer = new char[NUM_COLORS_IN_IMAGE_STRING_OUTPUT_BUFFER * 6];
			int offset = 0;
			int numColorsOutput = 0;

			println("/image" + image_number + " Do");
			println("Q");

			int xObjectObjectNumber = reserveObject();

			xObjectResources.put("image" + image_number, new Integer(xObjectObjectNumber));
			StringBuffer xObjectDef = new StringBuffer(1000);

			xObjectDef.append(xObjectObjectNumber + " 0 obj\n");
			xObjectDef.append("<<");
			xObjectDef.append("/Type /XObject");
			xObjectDef.append("/Subtype /Image");
			xObjectDef.append("/Name /image" + image_number++);
			xObjectDef.append("/Width " + image_width);
			xObjectDef.append("/Height " + image_height);
			xObjectDef.append("/Filter /ASCIIHexDecode");
			xObjectDef.append("/BitsPerComponent 8");
			xObjectDef.append("/ColorSpace DeviceRGB");
			xObjectDef.append("/Length " + image_width*image_height*6);
			xObjectDef.append(">>");
			xObjectDef.append("stream");

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
						xObjectDef.append(buffer, 0, offset);
						xObjectDef.append("\n");
						numColorsOutput = 0;
						offset = 0;
						}
					}
				}
			if (offset != 0)
				{
				xObjectDef.append(buffer, 0, offset);
				xObjectDef.append("\n");
				}

			xObjectDef.append("endstream\n");
			xObjectDef.append("endobj\n");
			xObjectDefinitions.put(new Integer(xObjectObjectNumber), new String(xObjectDef));
			}
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
		println("S");
		}
	private		void 			print(String str)
		{
		outputStream.print(str);
		currentPDFFileOffset += str.length();
		}

	private		void 			println(String str)
		{
		outputStream.println(str);
		currentPDFFileOffset += str.length();
		}

	private		void 			write_prolog()
		{
		println("%PDF-1.0");
		rootPagesObjectNumber = reserveObject();
		xObjectResourcesObjectNumber = reserveObject();
		fontResourcesObjectNumber = reserveObject();
		makeOutlinesObject();
		makeCatalogObject(rootPagesObjectNumber, outlinesObjectNumber);
		makeProcSetObject();
		newPage();
		}
	private		void 			write_epilog()
		{
		endStream();
		outputFontObjects();
		outputFontResources();
		outputXObjects();
		outputXObjectResources();
		outputPagesObject();
		outputXRefTable();
		outputTrailer();
		}
	private		MiBounds 		getBoundingBox()
		{
		MiBounds bbox = new MiBounds();
		reCalcRenderingBounds();
		bbox.xmin = (int )(renderingBounds.xmin * POINTS_PER_INCH/dotsPerInch);
		bbox.ymin = (int )(renderingBounds.ymin * POINTS_PER_INCH/dotsPerInch);
		bbox.xmax = (int )(renderingBounds.xmax * POINTS_PER_INCH/dotsPerInch);
		bbox.ymax = (int )(renderingBounds.ymax * POINTS_PER_INCH/dotsPerInch);
		return(bbox);
		}

	private		void 		set_up_transformations()
		{
		reCalcRenderingBounds();

		// Map image to desired resolution
		scale(((double )POINTS_PER_INCH)/dotsPerInch,
			((double )POINTS_PER_INCH)/dotsPerInch);

		if (paperOrientation == LANDSCAPE)
			{
			translate(pageHeight, 0);
			// Locate lower-left corner of image
			println("90 rotate");
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

	protected	void		makeCatalogObject(int pagesObjectNumber, int outlinesObjectNumber)
		{
		catalogObjectNumber = numberOfPDFObjects;
		println(makeObject() + " 0 obj");
		println("<<");
		println("/Type /Catalog");
		println("/Pages " + pagesObjectNumber + " 0 R");
		println("/Outlines " + outlinesObjectNumber + " 0 R");
		println(">>");
		println("endobj");
		}

	protected	void		makeOutlinesObject()
		{
		outlinesObjectNumber = numberOfPDFObjects;
		println(makeObject() + " 0 obj");
		println("<<");
		println("/Type /Outlines");
		println("/Count 0");
		println(">>");
		println("endobj");
		}

	protected	void		makeProcSetObject()
		{
		procSetObjectNumber = makeObject();
		println(procSetObjectNumber + " 0 obj");
		println("[ /PDF /Text /ImageB /ImageC ]");
		println("endobj");
		}
	protected	void		outputFontResources()
		{
		recordObjectOffset(fontResourcesObjectNumber, currentPDFFileOffset);
		println(fontResourcesObjectNumber + " 0 obj");
		println("<<");
		for (Enumeration e = fontResources.keys(); e.hasMoreElements();)
			{
			String resourceName = (String )e.nextElement();
			Integer objNumber = (Integer )fontResources.get(resourceName);
			println("/" + resourceName + " " + objNumber.intValue() + " 0 R");
			}
		println(">>");
		println("endobj");
		}
	protected	void		outputXObjectResources()
		{
		recordObjectOffset(xObjectResourcesObjectNumber, currentPDFFileOffset);
		println(xObjectResourcesObjectNumber + " 0 obj");
		println("<<");
		for (Enumeration e = xObjectResources.keys(); e.hasMoreElements();)
			{
			String resourceName = (String )e.nextElement();
			Integer objNumber = (Integer )xObjectResources.get(resourceName);
			println("/" + resourceName + " " + objNumber.intValue() + " 0 R");
			}
		println(">>");
		println("endobj");
		}
	protected	void		outputPagesObject()
		{
		recordObjectOffset(rootPagesObjectNumber, currentPDFFileOffset);
//System.out.println("pdfObjectOffsets.elementAt(rootPagesObjectNumber) = " + pdfObjectOffsets.elementAt(rootPagesObjectNumber));
		println(rootPagesObjectNumber + " 0 obj");
		println("<<");
		println("/Type /Pages");
		println("/Count " + pageObjectNumbers.size());
		print("/Kids [ ");
		for (int i = 0; i < pageObjectNumbers.size(); ++i)
			{
			print(pageObjectNumbers.elementAt(i) + " 0 R ");
			}
		println("]");
		println(">>");
		println("endobj");
		}

	protected	int		makeObject()
		{
		pdfObjectOffsets.addElement(currentPDFFileOffset);
		return(numberOfPDFObjects++);
		}
	protected	int		reserveObject()
		{
		pdfObjectOffsets.addElement(0);
		return(numberOfPDFObjects++);
		}
	protected	void		recordObjectOffset(int objectNumber, int offset)
		{
		pdfObjectOffsets.setElementAt(offset, objectNumber);
		}
	protected	void		outputFontObjects()
		{
		for (Enumeration e = fontResources.keys(); e.hasMoreElements();)
			{
			String psFontName = (String )e.nextElement();
			Integer objNumber = (Integer )fontResources.get(psFontName);

			recordObjectOffset(objNumber.intValue(), currentPDFFileOffset);
			println(objNumber.intValue() + " 0 obj");
			println("<<");
			println("/Type /Font");
			println("/Subtype /Type1");
			println("/Name /" + psFontName);
			println("/BaseFont /" + psFontName);
			println(">>");
			println("endobj");
			}
		}
	protected	void		outputXObjects()
		{
		for (Enumeration e = xObjectDefinitions.keys(); e.hasMoreElements();)
			{
			Integer xObjectNumber = (Integer )e.nextElement();
			String definition = (String )xObjectDefinitions.get(xObjectNumber);

			recordObjectOffset(xObjectNumber.intValue(), currentPDFFileOffset);
			print(definition);
			}
		}
	protected	void		outputTrailer()
		{
		println("trailer");
		println("<<");
		println("/Size " + numberOfPDFObjects);
		println("/Root " + catalogObjectNumber + " 0 R");
		println(">>");
		println("startxref");
		println("" + xrefStartOffset);
		println("%%EOF");
		}
	protected	void		outputXRefTable()
		{
		xrefStartOffset = currentPDFFileOffset;
		println("xref");
		println("0 " + numberOfPDFObjects);
		println("0000000000 65535 f");
		for (int i = 1; i < pdfObjectOffsets.size(); ++i)
			{
//System.out.println("pdfObjectOffsets.elementAt = " + pdfObjectOffsets.elementAt(i));
			String offset = "0000000000" + pdfObjectOffsets.elementAt(i);
			offset = offset.substring(offset.length() - 10);
			println(offset + " 00000 n");
			}
		}
	protected	void		startStream()
		{
		println(makeObject() + " 0 obj");
		streamLengthObjectNumber = makeObject();
		println("<</Length " + streamLengthObjectNumber + " 0 R>>");
		println("stream");
		streamStartOffset = currentPDFFileOffset;
		aStreamIsOpen = true;
		}
	protected	void		endStream()
		{
		if (aStreamIsOpen)
			{
			int streamSize = currentPDFFileOffset - streamStartOffset;
			println("endstream");
			println("endobj");
			recordObjectOffset(streamLengthObjectNumber, currentPDFFileOffset);
			println(streamLengthObjectNumber + " 0 obj");
			println("" + streamSize);
			println("endobj");
			aStreamIsOpen = false;
			}
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

