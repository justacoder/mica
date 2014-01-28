
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
import java.awt.Graphics;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiBorderLookRenderer extends MiDeviceRenderer implements MiiTypes, MiiAttributeTypes
	{
	private		int[]		localXPts	= new int[100];
	private		int[]		localYPts	= new int[100];

			// Return true if bounds of renderer extend outside given bounds
	public		boolean		getBounds(MiAttributes attributes, 
						MiBounds drawBounds, MiBounds renderedBounds)
		{
		boolean hasBorderHilite 
			= attributes.booleanAttributes[Mi_HAS_BORDER_HILITE - Mi_START_BOOLEAN_INDEX];
		if (hasBorderHilite)
			{
			MiDistance borderWidth 
			    = attributes.doubleAttributes[Mi_BORDER_HILITE_WIDTH - Mi_START_DOUBLE_INDEX];
			renderedBounds.copy(drawBounds);
			renderedBounds.addMargins(borderWidth);
			return(true);
			}
		return(false);
		}

	public		boolean		drawLine(Graphics g, MiAttributes attributes,
							int x1, int y1, int x2, int y2)
		{
		int borderLook = (int )attributes.intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX];

		Color lightColor = (Color )attributes.objectAttributes[Mi_LIGHT_COLOR];
		Color whiteColor = (Color )attributes.objectAttributes[Mi_WHITE_COLOR];
		Color darkColor = (Color )attributes.objectAttributes[Mi_DARK_COLOR];
		Color blackColor = (Color )attributes.objectAttributes[Mi_BLACK_COLOR];
		if ((x1 == x2)
			|| (Math.abs(y2 - y1) > Math.abs(x2 - x1)))
			{
			if (borderLook == Mi_RIDGE_BORDER_LOOK)
				{
				g.setColor(darkColor);
				g.drawLine(x1, y1, x2, y2);
				g.setColor(whiteColor);
				g.drawLine(x1 - 1, y1, x2 - 1, y2);
				return(true);
				}
			if (borderLook == Mi_RAISED_BORDER_LOOK)
				{
				g.setColor(darkColor);
				g.drawLine(x1 + 1, y1, x2 + 1, y2);
				g.setColor(blackColor);
				g.drawLine(x1, y1, x2, y2);
				g.setColor(whiteColor);
				g.drawLine(x1 - 1, y1, x2 - 1, y2);
				g.setColor(lightColor);
				g.drawLine(x1 - 2, y1, x2 - 2, y2);
				return(true);
				}
			if (borderLook == Mi_GROOVE_BORDER_LOOK)
				{
				g.setColor(whiteColor);
				g.drawLine(x1, y1, x2, y2);
				g.setColor(darkColor);
				g.drawLine(x1 - 1, y1, x2 - 1, y2);
				return(true);
				}
			if (borderLook == Mi_INDENTED_BORDER_LOOK)
				{
				g.setColor(lightColor);
				g.drawLine(x1 + 1, y1, x2 + 1, y2);
				g.setColor(whiteColor);
				g.drawLine(x1, y1, x2, y2);
				g.setColor(blackColor);
				g.drawLine(x1 - 1, y1, x2 - 1, y2);
				g.setColor(darkColor);
				g.drawLine(x1 - 2, y1, x2 - 2, y2);
				return(true);
				}
			g.drawLine(x1, y1, x2, y2);
			return(true);
			}
		else
			{
			if (borderLook == Mi_RIDGE_BORDER_LOOK)
				{
				g.setColor(whiteColor);
				g.drawLine(x1, y1, x2, y2);
				g.setColor(darkColor);
				g.drawLine(x1, y1 + 1, x2, y2 + 1);
				return(true);
				}
			if (borderLook == Mi_RAISED_BORDER_LOOK)
				{
				g.setColor(lightColor);
				g.drawLine(x1, y1 - 1, x2, y2 - 1);
				g.setColor(whiteColor);
				g.drawLine(x1, y1, x2, y2);
				g.setColor(blackColor);
				g.drawLine(x1, y1 + 1, x2, y2 + 1);
				g.setColor(darkColor);
				g.drawLine(x1, y1 + 2, x2, y2 + 2);
				return(true);
				}
			if (borderLook == Mi_GROOVE_BORDER_LOOK)
				{
				g.setColor(darkColor);
				g.drawLine(x1, y1, x2, y2);
				g.setColor(whiteColor);
				g.drawLine(x1, y1 + 1, x2, y2 + 1);
				return(true);
				}
			if (borderLook == Mi_INDENTED_BORDER_LOOK)
				{
				g.setColor(darkColor);
				g.drawLine(x1, y1 - 1, x2, y2 - 1);
				g.setColor(blackColor);
				g.drawLine(x1, y1, x2, y2);
				g.setColor(whiteColor);
				g.drawLine(x1, y1 + 1, x2, y2 + 1);
				g.setColor(lightColor);
				g.drawLine(x1, y1 + 2, x2, y2 + 2);
				return(true);
				}
			g.drawLine(x1, y1, x2, y2);
			}
		return(true);
		}
	public		boolean		drawRect(Graphics g, MiAttributes attributes,
							int xmin, int ymin, int width, int height)
		{
		int borderLook = (int )attributes.intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX];

		int xmax = xmin + width;
		int ymax = ymin + height;
		
		Color colorlight = (Color )attributes.objectAttributes[Mi_LIGHT_COLOR];
		Color colorwhite = (Color )attributes.objectAttributes[Mi_WHITE_COLOR];
		Color colordark = (Color )attributes.objectAttributes[Mi_DARK_COLOR];
		Color colorblack = (Color )attributes.objectAttributes[Mi_BLACK_COLOR];

		boolean hasBorderHilite = attributes.booleanAttributes[Mi_HAS_BORDER_HILITE - Mi_START_BOOLEAN_INDEX];
		if (borderLook == Mi_GROOVE_BORDER_LOOK)
			{
			g.setColor(colorwhite);
			g.drawRect(xmin + 1, ymin + 1, width - 1, height - 1);
			g.setColor(colordark);
			g.drawRect(xmin, ymin, width - 1, height - 1);
			if (hasBorderHilite)
				drawBorderHiliteRect(g, attributes, xmin, ymin, width, height);
			return(true);
			}

		if (borderLook == Mi_RIDGE_BORDER_LOOK)
			{
			g.setColor(colordark);
			g.drawRect(xmin + 1, ymin + 1, width - 1, height - 1);
			g.setColor(colorwhite);
			g.drawRect(xmin, ymin, width - 1, height - 1);
			if (hasBorderHilite)
				drawBorderHiliteRect(g, attributes, xmin, ymin, width, height);
			return(true);
			}
		else if ((borderLook == Mi_OUTLINED_INDENTED_BORDER_LOOK)
			|| (borderLook == Mi_OUTLINED_RAISED_BORDER_LOOK))
			{
/*
			++xmin;
			++ymin;
			--xmax;
			--xmax;
*/
			}

		if ((borderLook == Mi_INDENTED_BORDER_LOOK)
			|| (borderLook == Mi_INLINED_INDENTED_BORDER_LOOK)
			|| (borderLook == Mi_OUTLINED_INDENTED_BORDER_LOOK))
			{
			/* right */
			g.setColor(colorlight);
			g.drawLine(xmax - 1, ymin + 1, xmax - 1, ymax - 1);
			g.setColor(colorwhite);
			g.drawLine(xmax, ymin, xmax, ymax);
			/* top */
			g.setColor(colordark);
			g.drawLine(xmin + 1, ymin + 1, xmax - 1, ymin + 1);
			g.drawLine(xmin, ymin, xmax, ymin);
			/* bottom */
			g.setColor(colorlight);
			g.drawLine(xmin + 1, ymax - 1, xmax - 1, ymax - 1);
			g.setColor(colorwhite);
			g.drawLine(xmin, ymax, xmax, ymax);
			/* left */
			g.setColor(colordark);
			g.drawLine(xmin + 1, ymax - 1, xmin + 1, ymin + 1);
			g.drawLine(xmin, ymax, xmin, ymin);
			}
		else if ((borderLook == Mi_SQUARE_RAISED_BORDER_LOOK)
			|| (borderLook == Mi_OUTLINED_RAISED_BORDER_LOOK))
			{
			/* right */
			g.setColor(colordark);
			g.drawLine(xmax - 1, ymin + 1, xmax - 1, ymax - 1);
			g.setColor(colorblack);
			g.drawLine(xmax, ymin, xmax, ymax);
			/* top */
			g.setColor(colorwhite);
			g.drawLine(xmin, ymin, xmax, ymin);
			g.setColor(colorlight);
			g.drawLine(xmin + 1, ymin + 1, xmax - 1, ymin + 1);
			/* bottom */
			g.setColor(colordark);
			g.drawLine(xmin + 1, ymax - 1, xmax - 1, ymax - 1);
			g.setColor(colorblack);
			g.drawLine(xmin, ymax, xmax, ymax);
			/* left */
			g.setColor(colorwhite);
			g.drawLine(xmin, ymax, xmin, ymin);
			g.setColor(colorlight);
			g.drawLine(xmin + 1, ymax - 1, xmin + 1, ymin + 1);
			}
		else if (borderLook == Mi_RAISED_BORDER_LOOK)
			{
			/* right */
			g.setColor(colordark);
			g.drawLine(xmax - 1, ymin + 1, xmax - 1, ymax - 1);
			g.setColor(colorblack);
			g.drawLine(xmax, ymin + 1, xmax, ymax - 1);
/*
				// lower left corner lowlight
				g.setColor(colordark);
				g.drawLine(xmin + 1, ymax - 1, xmin + 2, ymax - 2);
*/
			/* top */
			g.setColor(colorwhite);
			g.drawLine(xmin + 1, ymin, xmax - 1, ymin);
			g.setColor(colorlight);
			g.drawLine(xmin + 1, ymin + 1, xmax - 1, ymin + 1);
/*
				// top right corner lowlight
				g.setColor(colordark);
				g.drawLine(xmax - 1, ymin + 1, xmax - 2, ymin + 2);
*/
			/* bottom */
			g.setColor(colordark);
			g.drawLine(xmin + 1, ymax - 1, xmax - 1, ymax - 1);
			g.setColor(colorblack);
			g.drawLine(xmin + 1, ymax, xmax - 1, ymax);

				// bottom right corner lowlight
				g.setColor(colorblack);
				g.drawLine(xmax - 1, ymax - 1, xmax - 1, ymax - 1);

			/* left */
			g.setColor(colorwhite);
			g.drawLine(xmin, ymax - 1, xmin, ymin + 1);
			g.setColor(colorlight);
			g.drawLine(xmin + 1, ymax - 1, xmin + 1, ymin + 1);

				// top left corner highlight
				g.setColor(colorwhite);
				g.drawLine(xmin + 1, ymin + 1, xmin + 1, ymin + 1);
			}
		if ((borderLook == Mi_INLINED_INDENTED_BORDER_LOOK)
			|| (borderLook == Mi_INLINED_RAISED_BORDER_LOOK))
			{
			//g.setColor(colordark);
			g.setColor(colorlight);
			g.drawRect(xmin + 2, ymin + 2, xmax - xmin - 4, ymax - ymin - 4);
			}
		else if ((borderLook == Mi_OUTLINED_INDENTED_BORDER_LOOK)
			|| (borderLook == Mi_OUTLINED_RAISED_BORDER_LOOK))
			{
			g.setColor(colordark);
			g.drawRect(xmin - 1, ymin - 1, xmax - xmin + 2, ymax - ymin + 2);
			}
		if (hasBorderHilite)
			drawBorderHiliteRect(g, attributes, xmin, ymin, width, height);
		return(true);
		}

	public		boolean		drawRoundRect(Graphics g, MiAttributes attributes,
						int xmin, int ymin, int width, int height, 
						int arcWidth, int arcHeight)
		{
		int borderLook = (int )attributes.intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX];

		Color colorlight = (Color )attributes.objectAttributes[Mi_LIGHT_COLOR];
		Color colorwhite = (Color )attributes.objectAttributes[Mi_WHITE_COLOR];
		Color colordark = (Color )attributes.objectAttributes[Mi_DARK_COLOR];
		Color colorblack = (Color )attributes.objectAttributes[Mi_BLACK_COLOR];

		if (borderLook == Mi_GROOVE_BORDER_LOOK)
			{
			g.setColor(colordark);
			g.drawRoundRect(xmin, ymin, width - 1, height - 1, arcWidth, arcHeight);
			g.setColor(colorwhite);
			g.drawRoundRect(xmin + 1, ymin + 1, width - 1, height - 1, arcWidth, arcHeight);
			return(true);
			}

		if (borderLook == Mi_RIDGE_BORDER_LOOK)
			{
			g.setColor(colorwhite);
			g.drawRoundRect(xmin, ymin, width - 1, height - 1, arcWidth, arcHeight);
			g.setColor(colordark);
			g.drawRoundRect(xmin + 1, ymin + 1, width - 1, height - 1, arcWidth, arcHeight);
			return(true);
			}
		int xmax = xmin + width;
		int ymax = ymin + height;
		int arcH = arcHeight/2;
		int arcW = arcWidth/2;

		if (borderLook == Mi_INDENTED_BORDER_LOOK)
			{
			/* right and right bottom arc */
			g.setColor(colorlight);
			g.drawLine(xmax - 1, ymin + 1 + arcH, xmax - 1, ymax - 1 - arcH);
			g.drawArc(xmax - arcWidth - 1, ymax - 1 - arcHeight, arcWidth, arcHeight, -90, 90);
			g.setColor(colorwhite);
			g.drawLine(xmax, ymin + arcH, xmax, ymax - arcH);
			g.drawArc(xmax - arcWidth, ymax - arcHeight, arcWidth, arcHeight, -90, 90);

			/* top and right top arc */
			g.setColor(colordark);
			g.drawLine(xmin + 1 + arcW, ymin + 1, xmax - 1 - arcW, ymin + 1);
			g.drawLine(xmin + arcW, ymin, xmax - arcW, ymin);
			g.drawArc(xmax - arcWidth - 1, ymin + 1, arcWidth, arcHeight, 0, 90);
			g.drawArc(xmax - arcWidth, ymin, arcWidth, arcHeight, 0, 90);

			/* bottom  and left bottom arc */
			g.setColor(colorlight);
			g.drawLine(xmin + 1 + arcW, ymax - 1, xmax - 1 - arcW, ymax - 1);
			g.drawArc(xmin + 1, ymax - 1 - arcHeight, arcWidth, arcHeight, 180, 90);
			g.setColor(colorwhite);
			g.drawLine(xmin + arcW, ymax, xmax - arcW, ymax);
			g.drawArc(xmin, ymax - arcHeight, arcWidth, arcHeight, 180, 90);

			/* left and left top arc */
			g.setColor(colordark);
			g.drawLine(xmin + 1, ymax - 1 - arcH, xmin + 1, ymin + 1 + arcH);
			g.drawLine(xmin, ymax - arcH, xmin, ymin + arcH);
			g.drawArc(xmin + 1, ymin + 1, arcWidth, arcHeight, 90, 90);
			g.drawArc(xmin, ymin, arcWidth, arcHeight, 90, 90);
			}
		else
			{
			/* right and right bottom arc */
			g.setColor(colordark);
			g.drawLine(xmax - 1, ymin + 1 + arcH, xmax - 1, ymax - 1 - arcH);
			g.drawArc(xmax - arcWidth - 1, ymax - 1 - arcHeight, arcWidth, arcHeight, -90, 90);
			g.setColor(colorblack);
			g.drawLine(xmax, ymin + arcH, xmax, ymax - arcH);
			g.drawArc(xmax - arcWidth, ymax - arcHeight, arcWidth, arcHeight, -90, 90);
			/* top and right top arc */
			g.setColor(colorwhite);
			g.drawLine(xmin + arcW, ymin, xmax - arcW, ymin);
			g.drawArc(xmax - arcWidth, ymin, arcWidth, arcHeight, 0, 90);
			/* bottom  and left bottom arc */
			g.setColor(colordark);
			g.drawLine(xmin + 1 + arcW, ymax - 1, xmax - 1 - arcW, ymax - 1);
			g.drawArc(xmin + 1, ymax - 1 - arcHeight, arcWidth, arcHeight, 180, 90);
			g.setColor(colorblack);
			g.drawLine(xmin + arcW, ymax, xmax - arcW, ymax);
			g.drawArc(xmin, ymax - arcHeight, arcWidth, arcHeight, 180, 90);
			/* left and left top arc */
			g.setColor(colorwhite);
			g.drawLine(xmin, ymax - arcH, xmin, ymin + arcH);
			g.drawArc(xmin, ymin, arcWidth, arcHeight, 90, 90);
			}
		return(true);
		}
	public		boolean		drawPolygon(Graphics g, MiAttributes attributes,
							int[] xpts, int[] ypts, int numPts)
		{
		int borderLook = (int )attributes.intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX];

		Color colorlight = (Color )attributes.objectAttributes[Mi_LIGHT_COLOR];
		Color colorwhite = (Color )attributes.objectAttributes[Mi_WHITE_COLOR];
		Color colordark = (Color )attributes.objectAttributes[Mi_DARK_COLOR];
		Color colorblack = (Color )attributes.objectAttributes[Mi_BLACK_COLOR];

		if ((borderLook == Mi_RIDGE_BORDER_LOOK) || (borderLook == Mi_GROOVE_BORDER_LOOK))
			{
			int xmin = Integer.MAX_VALUE;
			int xmax = Integer.MIN_VALUE;
			int ymin = Integer.MAX_VALUE;
			int ymax = Integer.MIN_VALUE;
			for (int i = 0; i < numPts; ++i)
				{
				if (xpts[i] > xmax)
					xmax = xpts[i];
				else if (xpts[i] < xmin)
					xmin = xpts[i];

				if (ypts[i] > ymax)
					ymax = ypts[i];
				else if (ypts[i] < ymin)
					ymin = ypts[i];
				}
			for (int i = 0; i < numPts; ++i)
				{
				localXPts[i] = xpts[i];
				localYPts[i] = ypts[i];
				if (xpts[i] < xmax)
					++localXPts[i];
				if (ypts[i] < ymax)
					++localYPts[i];
				}
			if (borderLook == Mi_RIDGE_BORDER_LOOK)
				g.setColor(colordark);
			else
				g.setColor(colorwhite);

			g.drawPolyline(localXPts, localYPts, numPts);

			for (int i = 0; i < numPts; ++i)
				{
				localXPts[i] = xpts[i];
				localYPts[i] = ypts[i];
				if (xpts[i] > xmin)
					--localXPts[i];
				if (ypts[i] > ymin)
					--localYPts[i];
				}
			if (borderLook == Mi_RIDGE_BORDER_LOOK)
				g.setColor(colorwhite);
			else
				g.setColor(colordark);

			g.drawPolyline(localXPts, localYPts, numPts);
			return(true);
			}
		if (borderLook == Mi_INDENTED_BORDER_LOOK)
			{
			Color tmp = colorlight;
			colorlight = colordark;
			colordark = tmp;
			tmp = colorblack;
			colorblack = colorwhite;
			colorwhite = tmp;
			}

		for (int i = 1; i < numPts; ++i)
			{
			boolean varyX = true;
			boolean varyY = true;
			int dx = xpts[i] - xpts[i - 1];
			int dy = ypts[i] - ypts[i - 1];
			int absDx = Math.abs(dx);
			int absDy = Math.abs(dy);
			if (dx != 0)
				{
				double slope = absDy/absDx;
				if (slope > 2)
					varyY = false;
				else if (slope < 0.5)
					varyX = false;
				}
			else
				{
				varyY = false;
				}
					
			// Check if 'top'
			if (((dx < 0) && (absDx > absDy)) || ((dy >= 0) && (absDy >= absDx)))
				{
				g.setColor(colorlight);
				if (!varyY)
					g.drawLine(xpts[i - 1] + 1, ypts[i - 1], xpts[i] + 1, ypts[i]);
				else if (!varyX)
					g.drawLine(xpts[i - 1], ypts[i - 1] + 1, xpts[i], ypts[i] + 1);
				else if (dx > 0)
					g.drawLine(xpts[i - 1] + 1, ypts[i - 1] - 1, xpts[i] + 1, ypts[i] - 1);
				else
					g.drawLine(xpts[i - 1] + 1, ypts[i - 1] + 1, xpts[i] + 1, ypts[i] + 1);
				g.setColor(colorwhite);
				g.drawLine(xpts[i - 1], ypts[i - 1], xpts[i], ypts[i]);
				}
			else
				{
				g.setColor(colordark);
				if (!varyY)
					g.drawLine(xpts[i - 1] - 1, ypts[i - 1], xpts[i] - 1, ypts[i]);
				else if (!varyX)
					g.drawLine(xpts[i - 1], ypts[i - 1] - 1, xpts[i], ypts[i] - 1);
				else if (dx > 0)
					g.drawLine(xpts[i - 1] - 1, ypts[i - 1] - 1, xpts[i] - 1, ypts[i] - 1);
				else
					g.drawLine(xpts[i - 1] - 1, ypts[i - 1] + 1, xpts[i] - 1, ypts[i] + 1);
				g.setColor(colorblack);
				g.drawLine(xpts[i - 1], ypts[i - 1], xpts[i], ypts[i]);
				}
			}
		return(true);
		}
	public		boolean		drawOval(Graphics g, MiAttributes attributes,
							int xmin, int ymin, int width, int height)
		{
		int borderLook = (int )attributes.intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX];

		Color colorlight = (Color )attributes.objectAttributes[Mi_LIGHT_COLOR];
		Color colorwhite = (Color )attributes.objectAttributes[Mi_WHITE_COLOR];
		Color colordark = (Color )attributes.objectAttributes[Mi_DARK_COLOR];
		Color colorblack = (Color )attributes.objectAttributes[Mi_BLACK_COLOR];

		if (borderLook == Mi_GROOVE_BORDER_LOOK)
			{
			g.setColor(colordark);
			g.drawOval(xmin, ymin, width - 1, height - 1);
			g.setColor(colorwhite);
			g.drawOval(xmin + 1, ymin + 1, width - 1, height - 1);
			return(true);
			}

		if (borderLook == Mi_RIDGE_BORDER_LOOK)
			{
			g.setColor(colorwhite);
			g.drawOval(xmin, ymin, width - 1, height - 1);
			g.setColor(colordark);
			g.drawOval(xmin + 1, ymin + 1, width - 1, height - 1);
			return(true);
			}

		if (borderLook == Mi_INDENTED_BORDER_LOOK)
			{
			/* right */
			g.setColor(colorlight);
			g.drawArc(xmin + 1, ymin + 1, width - 2, height - 2, 45, -90);
			g.setColor(colorwhite);
			g.drawArc(xmin, ymin, width, height, 45, -90);
			/* top */
			g.setColor(colordark);
			g.drawArc(xmin + 1, ymin + 1, width - 2, height - 2, 45, 90);
			g.drawArc(xmin, ymin, width, height, 45, 90);
			/* bottom */
			g.setColor(colorlight);
			g.drawArc(xmin + 1, ymin + 1, width - 2, height - 2, -135, 90);
			g.setColor(colorwhite);
			g.drawArc(xmin, ymin, width, height, 135, 90);
			/* left */
			g.setColor(colordark);
			g.drawArc(xmin + 1, ymin + 1, width - 2, height - 2, 135, 90);
			g.drawArc(xmin, ymin, width, height, 135, 90);
			}
		else 
			{
			/* right */
			g.setColor(colordark);
			g.drawArc(xmin + 1, ymin + 1, width - 2, height - 2, -45, 90);
			g.setColor(colorblack);
			g.drawArc(xmin, ymin, width, height, -45, 90);
			/* top */
			g.setColor(colorwhite);
			g.drawArc(xmin, ymin, width, height, 45, 90);
			/* bottom */
			g.setColor(colordark);
			g.drawArc(xmin + 1, ymin + 1, width - 2, height - 2, -135, 90);
			g.setColor(colorblack);
			g.drawArc(xmin, ymin, width, height, -135, 90);
			/* left */
			g.setColor(colorwhite);
			g.drawArc(xmin, ymin, width, height, 135, 90);
			}
		return(true);
		}



	public		boolean		drawText(Graphics g, MiAttributes attributes,
							String text, int x, int y)
		{
		int borderLook = (int )attributes.intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX];

		Color whiteColor = (Color )attributes.objectAttributes[Mi_WHITE_COLOR];
		if ((borderLook == Mi_RIDGE_BORDER_LOOK)
			|| (borderLook == Mi_RAISED_BORDER_LOOK))
			{
			g.setColor(whiteColor);
			g.drawString(text, x, y);
			g.setColor(attributes.getColor());
			g.drawString(text, x + 1, y + 1);
			return(true);
			}
		if ((borderLook == Mi_GROOVE_BORDER_LOOK)
			|| (borderLook == Mi_INDENTED_BORDER_LOOK))
			{
			g.setColor(whiteColor);
			g.drawString(text, x + 1, y + 1);
			g.setColor(attributes.getColor());
			g.drawString(text, x, y);
			return(true);
			}
		g.drawString(text, x, y);
		return(true);
		}
	private		void		drawBorderHiliteRect(Graphics g, MiAttributes attributes,
						int xmin, int ymin, int width, int height)
		{
		Color c = (Color )attributes.objectAttributes[Mi_BORDER_HILITE_COLOR];
		g.setColor(c);
		MiDistance borderWidth = attributes.doubleAttributes[Mi_BORDER_HILITE_WIDTH - Mi_START_DOUBLE_INDEX];
		int lwidth = 0;
		if (borderWidth != 0)
			{
			lwidth = (int )borderWidth;
			}

		for (int i = 0; i < lwidth; ++i)
			{
			g.drawRect(xmin - 1 - i, ymin - 1 - i, width + 2 + 2*i, height + 2 + 2*i);
			}
		}
	}

