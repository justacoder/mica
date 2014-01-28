
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
import com.swfm.mica.util.Utility; 
import java.awt.Color; 
import java.awt.Graphics; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSimpleGradientRenderer extends MiDeviceRenderer implements MiiTypes, MiiAttributeTypes
	{
	public static	int		MAX_NUMBER_OF_SHADES 		= 32;
	private		int		lightSourceDirection		= Mi_UPPER_LEFT_LOCATION;
	private		int		numberOfShades 			= 10;
	private		double		minBrightness			= 0.0;
	private		double		maxBrightness 			= 1.0;
	private		Color[] 	colors 				= new Color[MAX_NUMBER_OF_SHADES];



	public				MiSimpleGradientRenderer()
		{
		for (int i = 0; i < MAX_NUMBER_OF_SHADES; ++i)
			{
			colors[i] = new Color(0, 0, 0);
			}
		}
	public		void		setLightSourceDirection(int direction)
		{
		lightSourceDirection = direction;
		}
	public		int		getLightSourceDirection()
		{
		return(lightSourceDirection);
		}

	public		void		setNumberOfShades(int n)
		{
		if (n > MAX_NUMBER_OF_SHADES)
			n = MAX_NUMBER_OF_SHADES;
		else if (n < 2)
			n = 2;

		numberOfShades = n;
		}
	public		int		getNumberOfShades()
		{
		return(numberOfShades);
		}

	public		void		setMinimumBrightnessToUse(double normalizedBrightness)
		{
		if (normalizedBrightness > 1.0)
			normalizedBrightness = 1.0;
		else if (normalizedBrightness < 0.0)
			normalizedBrightness = 0.0;

		minBrightness = normalizedBrightness;
		}
	public		double		getMinimumBrightness()
		{
		return(minBrightness);
		}

	public		void		setMaximumBrightness(double normalizedBrightness)
		{
		if (normalizedBrightness > 1.0)
			normalizedBrightness = 1.0;
		else if (normalizedBrightness < 0.0)
			normalizedBrightness = 0.0;

		maxBrightness = normalizedBrightness;
		}
	public		double		getMaximumBrightnessToUse()
		{
		return(maxBrightness);
		}
	public		boolean		drawRect(Graphics g, MiAttributes atts, 
							int dxmin, int dymin, int dwidth, int dheight)
		{
		if (atts.objectAttributes[Mi_BACKGROUND_COLOR] == MiiTypes.Mi_TRANSPARENT_COLOR)
			return(true);

// get rif of ddx, ddy, dxend, dyend...
		long dx1, dy1, dx2, dy2, ddx, ddy, dxend, dyend;


		int dxmax = dxmin + dwidth;
		int dymax = dymin + dheight;
		long x1, y1, width, height;

		MiShades.getSimplyShadedColorGroup(
			(Color )atts.objectAttributes[Mi_BACKGROUND_COLOR], 
			colors, 
			numberOfShades, 
			minBrightness, 
			maxBrightness);

		int	i;
		long	xBandWidth = (dwidth << 16)/numberOfShades;
		long	yBandWidth = (dheight << 16)/numberOfShades;
		switch (lightSourceDirection)
			{
			case Mi_UPPER_LEFT_LOCATION	:
			case Mi_LOWER_RIGHT_LOCATION	:
				{
				x1 = dxmin << 16;
				y1 = (dymax << 16) - yBandWidth;
				width = (dwidth << 16);
				height = yBandWidth;
				for (i = 0; i < numberOfShades; ++i)
					{
					if (lightSourceDirection == Mi_UPPER_LEFT_LOCATION)
						g.setColor(colors[i]);
					else
						g.setColor(colors[numberOfShades - 1 - i]);
					if (i == numberOfShades - 1)
						{
						y1 = (dymin << 16);
						}
					g.fillRect((int )(x1 >> 16), (int )(y1 >> 16), (int )(width >> 16), (int )(yBandWidth >> 16));
					width -= xBandWidth;
					y1 -= yBandWidth;
					}

				x1 = (dxmax << 16) - xBandWidth;
				y1 = dymin << 16;
				height = (dheight << 16); // - yBandWidth;
				for (i = 0; i < numberOfShades; ++i)
					{
					if (lightSourceDirection == Mi_UPPER_LEFT_LOCATION)
						g.setColor(colors[i]);
					else
						g.setColor(colors[numberOfShades - 1 - i]);
					if (i == numberOfShades - 1)
						{
						x1 = dxmin << 16;
						}
					g.fillRect((int )(x1 >> 16), (int )(y1 >> 16), (int )(xBandWidth >> 16), (int )(height >> 16));
					height -= yBandWidth;
					x1 -= xBandWidth;
					}

				return(false);
				}

			case Mi_UPPER_RIGHT_LOCATION	:
			case Mi_LOWER_LEFT_LOCATION	:
				{
				dx1 = dxmin << 16;
				dy1 = dymin << 16;
				ddy = yBandWidth;
				ddx = xBandWidth;
				dx2 = dxmax << 16;
				dy2 = dy1 + ddy;
				dxend = dxmax << 16;
				dyend = dymax << 16;
				for (i = 0; i < numberOfShades; ++i)
					{
					if (lightSourceDirection == Mi_UPPER_RIGHT_LOCATION)
						g.setColor(colors[numberOfShades - 1 - i]);
					else
						g.setColor(colors[i]);
					if (i == numberOfShades - 1)
						{
						dy2 = dyend;
						}
					g.fillRect((int )(dx1 >> 16), (int )(dy1 >> 16), (int )(dx2 >> 16), (int )(dy2 >> 16));
					dx1 += ddx;
					dy1 += ddy;
					dy2 += ddy;
					}

				dx1 = dxmin << 16;
				dx2 = dx1 + ddx;
				dy2 = dymax << 16;
				dy1 = dymin << 16;
				for (i = 0; i < numberOfShades; ++i)
					{
					if (lightSourceDirection == Mi_UPPER_RIGHT_LOCATION)
						g.setColor(colors[numberOfShades - 1 - i]);
					else
						g.setColor(colors[i]);
					if (i == numberOfShades - 1)
						{
						dx2 = dxend;
						}
					g.fillRect((int )(dx1 >> 16), (int )(dy1 >> 16), (int )(dx2 >> 16), (int )(dy2 >> 16));
					dy1 += ddy;
					dx1 += ddx;
					dx2 += ddx;
					}

				return(false);
				}
			case Mi_CENTER_LOCATION	:
			case Mi_SURROUND_LOCATION	:
				{
				x1 = dxmin << 16;
				y1 = dymin << 16;
				long x2 = dxmax << 16;
				long y2 = dymax << 16;
				width = dwidth << 16;
				height = dheight << 16;
				for (i = 0; i < numberOfShades; ++i)
					{
					if (lightSourceDirection == Mi_SURROUND_LOCATION)
						g.setColor(colors[numberOfShades - 1 - i]);
					else
						g.setColor(colors[i]);
	
					int xl = (int )((x1 + xBandWidth) >> 16);
					int xr = (int )((x2 - xBandWidth) >> 16);
					int xwidth = xr - xl;
					g.fillRect((int )(x1 >> 16), (int )(y1 >> 16), xl, (int )(height >> 16));
					g.fillRect((int )(x1 >> 16), (int )(y1 >> 16), xr, (int )(height >> 16));
					g.fillRect(xl, (int )(y1 >> 16), xwidth, (int )(yBandWidth >> 16));
					g.fillRect(xl, (int )((y2 - yBandWidth) >> 16), xwidth, (int )(yBandWidth >> 16));
					x1 += xBandWidth;
					x2 -= xBandWidth;
					y1 += yBandWidth;
					y2 -= yBandWidth;
					height = y2 - y1;
					}
				return(false);
				}
			}

		return(false);
		}
	}
// FIX: put in MiColorManager?
class MiShades
	{
	public static final	int		MAX_NUMBER_OF_CACHED_SHADINGS	= 32;
	private	static		FastVector	shadedColorGroupCache		= new FastVector();
	private	static		int		MAX_BRIGHTNESS			= 233;
	private	static		int		MIN_BRIGHTNESS			= 96;
	private	static final	int		MAX_RGB_COLOR_VALUE		= 255;
	


	public				MiShades()
		{
		}

	public	static	void 		getSimplyShadedColorGroup(
						Color color, 
						Color[] shadedColors, 
						int numberOfShades, 
						double minBrightness, 
						double maxBrightness)
		{ 
		if (lookupCachedSimplyShadedColorGroup(color, shadedColors, numberOfShades, minBrightness, maxBrightness))
			{
			return;
			}

		int red, green, blue;
		int newred, newgreen, newblue;
		red = color.getRed();
		green = color.getGreen();
		blue = color.getBlue();

		if (numberOfShades <= 1)
			numberOfShades = 2;

		int maxRGB = Utility.Max3(red, green, blue);

		for (int i = 0; i < numberOfShades; ++i)
			{
			double adjustedColorShadeFactor = 
				((MAX_BRIGHTNESS - MIN_BRIGHTNESS)/((double )maxRGB))
				* (i)/((double )(numberOfShades - 1)) 
				+ MIN_BRIGHTNESS/((double )maxRGB);

			adjustedColorShadeFactor =
				adjustedColorShadeFactor * (maxBrightness - minBrightness) + minBrightness;

			newred = (int )(adjustedColorShadeFactor * red);
			newgreen = (int )(adjustedColorShadeFactor * green);
			newblue = (int )(adjustedColorShadeFactor * blue);

			if (newred > MAX_RGB_COLOR_VALUE)
				newred = MAX_RGB_COLOR_VALUE;
			if (newgreen > MAX_RGB_COLOR_VALUE)
				newgreen = MAX_RGB_COLOR_VALUE;
			if (newblue > MAX_RGB_COLOR_VALUE)
				newblue = MAX_RGB_COLOR_VALUE;

			shadedColors[i] = new Color(newred, newgreen, newblue);
			}

		addSimpleShadedColorToCacheGroup(color, shadedColors, numberOfShades, minBrightness, maxBrightness);
		}

	public	static	boolean 		lookupCachedSimplyShadedColorGroup(
							Color color, 
							Color[] shadedColors, 
							int numberOfShades, 
							double minBrightness, 
							double maxBrightness)
		{
		int numInCache = shadedColorGroupCache.size();
		for (int i = 0; i < numInCache; ++i)
			{
			MiShadedColorGroup grp = (MiShadedColorGroup )shadedColorGroupCache.elementAt(i);
			if ((grp.seedColor == color)
			 	&& (grp.numberOfShades == numberOfShades)
			 	&& (grp.minBrightness == minBrightness)
			 	&& (grp.maxBrightness == maxBrightness))
				{
				for (int j = 0; j < numberOfShades; ++j)
					{
					shadedColors[j] = grp.colors[j];
					}
				return(true);
				}
			}
		return(false);
		}

	public	static	void 		addSimpleShadedColorToCacheGroup(
						Color color, 
						Color[] shadedColors, 
						int numberOfShades, 
						double minBrightness, 
						double maxBrightness)
		{
		if (shadedColorGroupCache.size() < MAX_NUMBER_OF_CACHED_SHADINGS)
			{
			MiShadedColorGroup grp = new MiShadedColorGroup();
			shadedColorGroupCache.addElement(grp);
			grp.seedColor = color;
			grp.numberOfShades = numberOfShades;
			grp.minBrightness = minBrightness;
			grp.maxBrightness = maxBrightness;
	
			grp.colors = new Color[numberOfShades];
			for (int i = 0; i < numberOfShades; ++i)
				{
				grp.colors[i] = shadedColors[i];
				}
			}
		}
	}
class MiShadedColorGroup
	{
	Color		seedColor;
	int		numberOfShades;
	double		minBrightness;
	double		maxBrightness;
	Color[]	colors;
	}




