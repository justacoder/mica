
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
public class MiGradientRenderer extends MiDeviceRenderer implements MiiTypes, MiiAttributeTypes
	{
	private 	int		DEFAULT_WIDTH			= 128;
	private 	int		DEFAULT_HEIGHT			= 128;
	private		int		transparency 			= (255 << 24);
	private		int		lightSourceLocation		= Mi_UPPER_LEFT_LOCATION;
	private		Color		baseColor			= MiColorManager.veryDarkWhite;
	private		Color		upperLeftColor			= MiColorManager.darkWhite;
	private		Color		upperRightColor			= MiColorManager.veryDarkWhite;
	private		Color		lowerLeftColor			= MiColorManager.veryDarkWhite;
	private		Color		lowerRightColor			= MiColorManager.gray;
	private		int		baseImageWidth			= DEFAULT_WIDTH;
	private		int		baseImageHeight			= DEFAULT_HEIGHT;
	private		int[]		pixels;
	private		Image		image;
	private		double		darkerBrighterFactor		= 0.7;
	private		Hashtable	imageCache			= new Hashtable();
	//private		boolean		ignoreAssignedToPartsBGColor;
	private		boolean		gradientBaseColorEqualsPartBGColor	= true;




					/**------------------------------------------------------
					 * Constructs a renderer which generates a background 
					 * gradient image effect when assigned to one or more 
					 * parts using their setBackgroundRenderer() method.
					 * @see			MiPart#setBackgroundRenderer
					 * @see			setGradientBaseColorEqualsPartBGColor
					 *------------------------------------------------------*/
	public				MiGradientRenderer()
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
		reCalcCornerColors();
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
/****
	public		void		setIgnoreAssignedToPartsBackgroundColor(boolean flag)
		{
		ignoreAssignedToPartsBGColor = flag;
		}
	public		boolean		getIgnoreAssignedToPartsBackgroundColor()
		{
		return(ignoreAssignedToPartsBGColor);
		}
****/
	public		void		setLightSourceLocation(int location)
		{
		lightSourceLocation = location;
		reCalcCornerColors();
		}
	public		int		getLightSourceLocation()
		{
		return(lightSourceLocation);
		}
	protected	void		reCalcCornerColors()
		{
		if (baseColor == MiiTypes.Mi_TRANSPARENT_COLOR)
			return;

		// Black does not get "darker or lighter" in JDK 1.0.2
		if (baseColor.equals(MiColorManager.black))
			baseColor = MiColorManager.darkGray;

		Color c = baseColor;
		Color brighter = brighter(c);
		Color darker = darker(c);

		switch (lightSourceLocation)
			{
			case Mi_CENTER_LOCATION		:
				break;
			case Mi_LEFT_LOCATION		:
				upperLeftColor = brighter;
				upperRightColor = darker;
				lowerLeftColor = brighter;
				lowerRightColor = darker;
				break;
			case Mi_RIGHT_LOCATION		:
				upperLeftColor = darker;
				upperRightColor = brighter;
				lowerLeftColor = darker;
				lowerRightColor = brighter;
				break;
			case Mi_BOTTOM_LOCATION		:
				upperLeftColor = darker;
				upperRightColor = darker;
				lowerLeftColor = brighter;
				lowerRightColor = brighter;
				break;
			case Mi_TOP_LOCATION		:
				upperLeftColor = brighter;
				upperRightColor = brighter;
				lowerLeftColor = darker;
				lowerRightColor = darker;
				break;
			case Mi_LOWER_LEFT_LOCATION	:
				upperLeftColor = c;
				upperRightColor = darker;
				lowerLeftColor = brighter;
				lowerRightColor = c;
				break;
			case Mi_LOWER_RIGHT_LOCATION	:
				upperLeftColor = darker;
				upperRightColor = c;
				lowerLeftColor = c;
				lowerRightColor = brighter;
				break;
			case Mi_UPPER_LEFT_LOCATION	:
				upperLeftColor = brighter;
				upperRightColor = c;
				lowerLeftColor = c;
				lowerRightColor = darker;
				break;
			case Mi_UPPER_RIGHT_LOCATION	:
				upperLeftColor = c;
				upperRightColor = brighter;
				lowerLeftColor = darker;
				lowerRightColor = c;
				break;
			case Mi_SURROUND_LOCATION	:
				break;
			}
		}

	/**
	 * @param factor (default value = 0.7) to use to calc darker and brighter colors from base color
	 **/
	public		void		setDarkerBrighterFactor(double factor)
		{
		if (factor < 0.1)
			{
			factor = 0.1;
			}
		else if (factor > 0.9)
			{
			factor = 0.9;
			}
		darkerBrighterFactor = factor;
		reCalcCornerColors();
		}
	public		double		getDarkerBrighterFactor()
		{
		return(darkerBrighterFactor);
		}

	public		Color		brighter(Color c)
		{
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();

		if (red < 10)
			{
			red = 10;
			}
		if (green < 10)
			{
			green = 10;
			}
		if (blue < 10)
			{
			blue = 10;
			}

		return(new Color(
			Math.min((int )(red/darkerBrighterFactor), 255), 
			Math.min((int )(green/darkerBrighterFactor), 255), 
			Math.min((int )(blue/darkerBrighterFactor), 255)));
		}
	public		Color		darker(Color c)
		{
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();

		return(new Color(
			(int )(red * darkerBrighterFactor), 
			(int )(green * darkerBrighterFactor), 
			(int )(blue * darkerBrighterFactor)));
		}

	public		void		setUpperLeftColor(Color c)
		{
		upperLeftColor = c;
		}
	public		Color		getUpperLeftColor()
		{
		return(upperLeftColor);
		}
	public		void		setUpperRightColor(Color c)
		{
		upperRightColor = c;
		}
	public		Color		getUpperRightColor()
		{
		return(upperRightColor);
		}
	public		void		setLowerLeftColor(Color c)
		{
		lowerLeftColor = c;
		}
	public		Color		getLowerLeftColor()
		{
		return(lowerLeftColor);
		}
	public		void		setLowerRightColor(Color c)
		{
		lowerRightColor = c;
		}
	public		Color		getLowerRightColor()
		{
		return(lowerRightColor);
		}
	public		void		setTransparency(double t)
		{
		transparency = ((int )(t * 255)) << 24;
		}
	public		double		getTransparency()
		{
		return(((double )(transparency >> 24))/255);
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

		int leftSideRedColorHeight = lowerLeftColor.getRed() - upperLeftColor.getRed();
		int rightSideRedColorHeight = lowerRightColor.getRed() - upperRightColor.getRed();
		int leftSideGreenColorHeight = lowerLeftColor.getGreen() - upperLeftColor.getGreen();
		int rightSideGreenColorHeight = lowerRightColor.getGreen() - upperRightColor.getGreen();
		int leftSideBlueColorHeight = lowerLeftColor.getBlue() - upperLeftColor.getBlue();
		int rightSideBlueColorHeight = lowerRightColor.getBlue() - upperRightColor.getBlue();

		int upperLeftRedColor = upperLeftColor.getRed();
		int upperLeftGreenColor = upperLeftColor.getGreen();
		int upperLeftBlueColor = upperLeftColor.getBlue();

		int upperRightRedColor = upperRightColor.getRed();
		int upperRightGreenColor = upperRightColor.getGreen();
		int upperRightBlueColor = upperRightColor.getBlue();

		int index = 0;
		for (int y = 0; y < height; ++y)
			{
			double scaleY = ((double )y)/height;
			int leftRedColor = (int )(scaleY * leftSideRedColorHeight) + upperLeftRedColor;
			int leftGreenColor = (int )(scaleY * leftSideGreenColorHeight) + upperLeftGreenColor;
			int leftBlueColor = (int )(scaleY * leftSideBlueColorHeight) + upperLeftBlueColor;

			int rightRedColor = (int )(scaleY * rightSideRedColorHeight) + upperRightRedColor;
			int rightGreenColor = (int )(scaleY * rightSideGreenColorHeight) + upperRightGreenColor;
			int rightBlueColor = (int )(scaleY * rightSideBlueColorHeight) + upperRightBlueColor;

			int rowRedColorWidth = rightRedColor - leftRedColor;
			int rowGreenColorWidth = rightGreenColor - leftGreenColor;
			int rowBlueColorWidth = rightBlueColor - leftBlueColor;

			for (int x = 0; x < width; ++x)
				{
				double scale = ((double )x)/width;
				pixelBuffer[index++] = 
				  	(((((int )(scale * rowRedColorWidth) + leftRedColor) << 16)
				  	+ (((int )(scale * rowGreenColorWidth) + leftGreenColor) << 8)
				  	+ ((int )(scale * rowBlueColorWidth) + leftBlueColor)) &0x00ffffff)
				  	+ transparency;
				}
			}
		return(pixelBuffer);
		}
	}

