
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
import com.swfm.mica.util.FastVector; 
import java.util.Hashtable; 
import java.awt.Color; 
import java.awt.Graphics; 
import java.awt.Toolkit; 
import java.awt.Image; 
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

/**
 * Constructs a renderer which generates a background 
 * gradient image effect when assigned to one or more 
 * parts using their setBackgroundRenderer() method.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiCustomGradientRenderer extends MiDeviceRenderer implements MiiTypes, MiiAttributeTypes
	{
	private 	int		DEFAULT_WIDTH			= 128;
	private 	int		DEFAULT_HEIGHT			= 128;
	private		Color		baseColor			= MiColorManager.veryDarkWhite;
	private		int		baseImageWidth			= DEFAULT_WIDTH;
	private		int		baseImageHeight			= DEFAULT_HEIGHT;
	private		int[]		pixels;
	private		Image		image;
	private		Hashtable	imageCache			= new Hashtable();
	private		boolean		gradientBaseColorEqualsPartBGColor	= true;
	private		MiiCustomGradientRendererPlugIn			plugIn = new MiCustomGradientRendererPlugIn1();



					/**------------------------------------------------------
					 * Constructs a renderer which generates a background 
					 * gradient image effect when assigned to one or more 
					 * parts using their setBackgroundRenderer() method.
					 * @see			MiPart#setBackgroundRenderer
					 * @see			setGradientBaseColorEqualsPartBGColor
					 *------------------------------------------------------*/
	public				MiCustomGradientRenderer()
		{
		}

	public		void		setBaseSize(int width, int height)
		{
		baseImageWidth = width;
		baseImageHeight = height;
		}
	public		int		getBaseWidth()
		{
		return(baseImageWidth);
		}
	public		int		getBaseHeight()
		{
		return(baseImageHeight);
		}
	
					/**------------------------------------------------------
					 * Sets the base color of this renderer. This is used in
					 * conjunction with light direction to generate a gradient.
					 * TRANSPARENT_COLOR as a background, then this renderer does
					 * nothing for that part.
					 * @param c		the color.
					 * @see			#setGradientBaseColorEqualsPartBGColor
					 * @see			#getGradientBaseColorEqualsPartBGColor
					 * @see			#getBaseColor
					 *------------------------------------------------------*/
	public		void		setBaseColor(Color c)
		{
		baseColor = c;
		}
					/**------------------------------------------------------
					 * Gets the base color of this renderer. This is used in
					 * conjunction with light direction to generate a gradient.
					 * TRANSPARENT_COLOR as a background, then this renderer does
					 * nothing for that part.
					 * @return		the color.
					 * @see			#setGradientBaseColorEqualsPartBGColor
					 * @see			#getGradientBaseColorEqualsPartBGColor
					 * @see			#setBaseColor
					 *------------------------------------------------------*/
	public		Color		getBaseColor()
		{
		return(baseColor);
		}

					/**------------------------------------------------------
					 * Sets whether this renderer looks at the background
					 * color of each part that is being renderered or whether
					 * this uses the assigned base color. This is true by default.
					 * In any case, if a part that is being rendered has 
					 * TRANSPARENT_COLOR as a background, then this renderer does
					 * nothing for that part.
					 * @param flag		true if the background color of the
					 *			part being rendered determine the base color.
					 * @see			#getGradientBaseColorEqualsPartBGColor
					 * @see			#setBaseColor
					 * @see			#getBaseColor
					 *------------------------------------------------------*/
	public		void		setGradientBaseColorEqualsPartBGColor(boolean flag)
		{
		gradientBaseColorEqualsPartBGColor = flag;
		}
					/**------------------------------------------------------
					 * Gets whether this renderer looks at the background
					 * color of each part that is being renderered or whether
					 * this uses the assigned base color. This is true by default.
					 * In any case, if a part that is being rendered has 
					 * TRANSPARENT_COLOR as a background, then this renderer does
					 * nothing for that part.
					 * @return		true if the background color of the
					 *			part being rendered determine the base color.
					 * @see			#setGradientBaseColorEqualsPartBGColor
					 * @see			#setBaseColor
					 * @see			#getBaseColor
					 *------------------------------------------------------*/
	public		boolean		getGradientBaseColorEqualsPartBGColor()
		{
		return(gradientBaseColorEqualsPartBGColor);
		}

	public		boolean		drawRect(Graphics g, MiAttributes atts, 
							int dxmin, int dymin, int dwidth, int dheight)
		{
		if (atts.objectAttributes[Mi_BACKGROUND_IMAGE] != null)
			{
			return(true);
			}

		//if (!ignoreAssignedToPartsBGColor)
			{
			Color bgColor = (Color )atts.objectAttributes[Mi_BACKGROUND_COLOR];
			if (bgColor == MiiTypes.Mi_TRANSPARENT_COLOR)
				return(true);
			if ((bgColor != baseColor) && (gradientBaseColorEqualsPartBGColor))
				{
				image = (Image )imageCache.get(bgColor);
				if (image == null)
					setBaseColor(bgColor);
				else
					baseColor = bgColor;
				}
			}
		if (baseColor == MiiTypes.Mi_TRANSPARENT_COLOR)
			return(true);

		if (image == null)
			{
			pixels = createPixelBuffer(baseImageWidth, baseImageHeight);
			image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(baseImageWidth, baseImageHeight,
						ColorModel.getRGBdefault(),
						pixels, 0, baseImageWidth));
			imageCache.put(baseColor, image);
			}
		g.drawImage(image, dxmin, dymin, dwidth, dheight, null);
		return(false);
		}

	public 		Image		createImage(int width, int height)
		{
		int[] pixels = createPixelBuffer(baseImageWidth, baseImageHeight);
		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(baseImageWidth, baseImageHeight,
						ColorModel.getRGBdefault(),
						pixels, 0, baseImageWidth));
		return(image);
		}
	protected	int[]		createPixelBuffer(int width, int height)
		{
		int[] pixelBuffer = new int[width * height];

		int index = 0;
		int[] rgba = new int[4];
		for (int y = 0; y < height; ++y)
			{
			for (int x = 0; x < width; ++x)
				{
				plugIn.getRGBA(x, y, width, height, baseColor, rgba);

				pixelBuffer[index++] = 
				  	(((rgba[0] << 16)
				  	+ (rgba[1] << 8)
				  	+ rgba[2]) & 0x00ffffff)
					+ (rgba[3] << 24);
				}
			}
		return(pixelBuffer);
		}
	}
interface MiiCustomGradientRendererPlugIn
	{
	public		void		getRGBA(int x, int y, int width, int height, Color baseColor, int[] rgba);
	}
/**
 * This creates a vertical or horizontal, one or two-sided smooth bevel. 
 * One of the problems not addressed is that as the color rools off towards 
 * black and white, there is usually much less distance to go to get to pure 
 * white, so the dark-rolloff of the bevel looks more prominent
**/
class MiCustomGradientRendererPlugIn1 implements MiiCustomGradientRendererPlugIn, MiiTypes, MiiAttributeTypes
	{
	private		int		lightSourceLocation		= Mi_TOP_LOCATION;
	private		double		flatness	= 0.7;		// percentage of middle that is baseColor
	private		double		roundness	= 1.0;		// 1 is a perfect sin curve rolloff, 0 is no rolloff, >1 is steeper roolloff

	public		void		getRGBA(int x, int y, int width, int height, Color baseColor, int[] rgba)
		{
		int red = baseColor.getRed();
		int green = baseColor.getGreen();
		int blue = baseColor.getBlue();
		int transparency = baseColor.getAlpha();
		
		rgba[0] = red;
		rgba[1] = green;
		rgba[2] = blue;
		rgba[3] = transparency;

//MiDebug.println("basic rgb = " + rgba[0] + ", " + rgba[1] + ", " + rgba[2] + " , " + rgba[3]);
		boolean locationIsXDependent = false;
		boolean locationIsLowValued = false;
		double normalizedLocationFromEdge = 0.0;

		if ((lightSourceLocation == Mi_RIGHT_LOCATION)
			|| (lightSourceLocation == Mi_LEFT_LOCATION))
			{
			locationIsXDependent = true;
			double xNormalizedLocation = x/width;
			if (xNormalizedLocation < (1 - flatness)/2)
				{
				normalizedLocationFromEdge = xNormalizedLocation;
				locationIsLowValued = false;
				}
			else if (xNormalizedLocation > 1 - (1 - flatness)/2)
				{
				normalizedLocationFromEdge = 1 - xNormalizedLocation;
				locationIsLowValued = true;
				}
			else
				{
				return;
				}
			}
		else if ((lightSourceLocation == Mi_TOP_LOCATION)
			|| (lightSourceLocation == Mi_BOTTOM_LOCATION))
			{
			locationIsXDependent = false;
			double yNormalizedLocation = ((double )y)/height;
			if (yNormalizedLocation < (1 - flatness)/2)
				{
				normalizedLocationFromEdge = yNormalizedLocation;
				locationIsLowValued = false;
				}
			else if (yNormalizedLocation > 1 - (1 - flatness)/2)
				{
				normalizedLocationFromEdge = 1 - yNormalizedLocation;
				locationIsLowValued = true;
				}
			else
				{
				return;
				}
			}
//MiDebug.println("x = " + x);
//MiDebug.println("y = " + y);
//MiDebug.println("width = " + width);
//MiDebug.println("height = " + height);
//MiDebug.println("locationIsXDependent = " + locationIsXDependent);
//MiDebug.println("lightSourceLocation = " + lightSourceLocation);
//MiDebug.println("normalizedLocationFromEdge = " + normalizedLocationFromEdge);
//MiDebug.println("locationIsLowValued = " + locationIsLowValued);

		if (  (((lightSourceLocation == Mi_TOP_LOCATION) && (locationIsLowValued))
			|| ((lightSourceLocation == Mi_BOTTOM_LOCATION) && (!locationIsLowValued)))
		|| (((lightSourceLocation == Mi_RIGHT_LOCATION) && (locationIsLowValued))
			|| ((lightSourceLocation == Mi_LEFT_LOCATION) && (!locationIsLowValued))) )
			{
			// rolloff toward black...
			double rollOff = getRollOff((1 - flatness)/2 - normalizedLocationFromEdge);
			rgba[0] = (int )(rollOff * red);
			rgba[1] = (int )(rollOff * green);
			rgba[2] = (int )(rollOff * blue);
//MiDebug.println("rollOff = " + rollOff);
//MiDebug.println("red = " + red);
//MiDebug.println("(int )(rollOff * red) = " + (int )(rollOff * red));
//MiDebug.println("new rgb 111 = " + rgba[0] + ", " + rgba[1] + ", " + rgba[2] + " , " + rgba[3]);
			}
		else if (  (((lightSourceLocation == Mi_TOP_LOCATION) && (!locationIsLowValued))
			|| ((lightSourceLocation == Mi_BOTTOM_LOCATION) && (locationIsLowValued)))
		|| (((lightSourceLocation == Mi_RIGHT_LOCATION) && (!locationIsLowValued))
			|| ((lightSourceLocation == Mi_LEFT_LOCATION) && (locationIsLowValued))) )
			{
			// rolloff toward white...
			double rollOff = 1 - getRollOff((1 - flatness)/2 - normalizedLocationFromEdge);
//MiDebug.println("rollOff = " + rollOff);
			rgba[0] = (int )(red + rollOff * (255 - red));
			rgba[1] = (int )(green + rollOff * (255 - green));
			rgba[2] = (int )(blue + rollOff * (255 - blue));
//MiDebug.println("new rgb 222 = " + rgba[0] + ", " + rgba[1] + ", " + rgba[2] + " , " + rgba[3]);
			}
		rgba[0] = Math.min(rgba[0], 255);
		rgba[1] = Math.min(rgba[1], 255);
		rgba[2] = Math.min(rgba[2], 255);
		}
	// From 1 to zero: 1 at flatness, 0 at edge
	protected	double		getRollOff(double distanceFromFlatness)
		{	
//MiDebug.println("distanceFromFlatness = " + distanceFromFlatness);
//MiDebug.println("raw getRollOff = " + (1 - Math.sin(2 * distanceFromFlatness/(1 - flatness) * Math.PI/2) * roundness));
		return(1 - Math.sin(2 * distanceFromFlatness/(1 - flatness) * Math.PI/2) * roundness);
		}
	}

