
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
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiLineEndsRenderer extends MiPartRenderer implements MiiTypes, MiiAttributeTypes, MiiLineEndsRenderer
	{
	private		MiDevicePoint 	dBasePoint	= new MiDevicePoint();
	private		MiDevicePoint 	dEndPoint	= new MiDevicePoint();
	private		MiVector	tmpVector	= new MiVector();
	private		MiPoint		tmpPoint	= new MiPoint();
	private		MiDeviceVector	tmpDVector	= new MiDeviceVector();
	private		int[]		xPoints		= new int[5];
	private		int[]		yPoints		= new int[5];
	private		boolean		useTargetObjectAttributes = true;

	private static	boolean		stylesExtendsBeyondEndOfLine[] =
		{
		false,		// Mi_NONE
		false,		// Mi_FILLED_TRIANGLE_LINE_END_STYLE
		false,		// Mi_STROKED_ARROW_LINE_END_STYLE
		false,		// Mi_FILLED_ARROW_LINE_END_STYLE
		false,		// Mi_FILLED_CIRCLE_LINE_END_STYLE
		false,		// Mi_FILLED_SQUARE_LINE_END_STYLE
		false,		// Mi_TRIANGLE_VIA_LINE_END_STYLE
		false,		// Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE
		true,		// Mi_CIRCLE_VIA_LINE_END_STYLE
		true,		// Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE
		true,		// Mi_SQUARE_VIA_LINE_END_STYLE
		true,		// Mi_FILLED_SQUARE_VIA_LINE_END_STYLE
		false,		// Mi_TRIANGLE_LINE_END_STYLE
		false,		// Mi_CIRCLE_LINE_END_STYLE
		false,		// Mi_SQUARE_LINE_END_STYLE
		false,		// Mi_DIAMOND_LINE_END_STYLE
		false,		// Mi_FILLED_DIAMOND_LINE_END_STYLE
		false,		// Mi_3FEATHER_LINE_END_STYLE
		false,		// Mi_2FEATHER_LINE_END_STYLE
		};


	public				MiLineEndsRenderer()
		{
		}
	// If false, uses MiiPartRenderer's attributes to get color, default is true
	public		void		setUsingTargetObjectAttributes(boolean flag)
		{
		useTargetObjectAttributes = flag;
		}
			// Return true if bounds of point extend beyond point
	public		boolean		getLineEndBounds(MiPart obj, MiBounds b, boolean start)
		{
		if (!(obj instanceof MiMultiPointShape))
			return(false);

		MiAttributes	attributes 			= obj.getAttributes();
		boolean		changed 			= false;
		boolean		styleExtendsBeyondEndOfLine	= false;

		MiDistance size = 0;
		if (start)
			{
			int style = attributes.getLineStartStyle();
			if (style != Mi_NONE)
				{
				size = attributes.getLineStartSize();
				if (attributes.getLineEndsSizeFnOfLineWidth())
					size += attributes.getLineWidth();
			
				styleExtendsBeyondEndOfLine |= stylesExtendsBeyondEndOfLine[style];
				changed = true;
				obj.getPoint(0, tmpPoint);
				b.setBounds(tmpPoint);
				}
			}
		else
			{
			int style = attributes.getLineEndStyle();
			if (style != Mi_NONE)
				{
				size = attributes.getLineEndSize();
				if (attributes.getLineEndsSizeFnOfLineWidth())
					size += attributes.getLineWidth();
				
				styleExtendsBeyondEndOfLine |= stylesExtendsBeyondEndOfLine[style];
				changed = true;
				obj.getPoint(-1, tmpPoint);
				b.setBounds(tmpPoint);
				}
			}
		if (changed)
			{
			MiMultiPointShape shape = (MiMultiPointShape) obj;

			if (styleExtendsBeyondEndOfLine)
				{
				b.addMargins(size);
				}
			else if (shape.isVertical())
				{
				b.setXmin(b.getXmin() - size);
				b.setXmax(b.getXmax() + size);
				}
			else if (shape.isHorizontal())
				{
				b.setYmin(b.getYmin() - size);
				b.setYmax(b.getYmax() + size);
				}
			else
				{
				b.addMargins(size);
				}
			}

		return(changed);
		}

	public		boolean		getBounds(MiPart obj, MiBounds b)
		{
		if (!(obj instanceof MiMultiPointShape))
			return(false);

		MiDistance 	startSize			= 0;
		MiDistance 	endSize				= 0;
		MiAttributes	attributes 			= obj.getAttributes();
		boolean		changed 			= false;
		boolean		styleExtendsBeyondEndOfLine	= false;

		int style = attributes.getLineStartStyle();
		if (style != Mi_NONE)
			{
			startSize = attributes.getLineStartSize();
			if (attributes.getLineEndsSizeFnOfLineWidth())
				startSize += attributes.getLineWidth();
			
			styleExtendsBeyondEndOfLine |= stylesExtendsBeyondEndOfLine[style];
			changed = true;
			}
		style = attributes.getLineEndStyle();
		if (style != Mi_NONE)
			{
			endSize = attributes.getLineEndSize();
			if (attributes.getLineEndsSizeFnOfLineWidth())
				endSize += attributes.getLineWidth();
			
			styleExtendsBeyondEndOfLine |= stylesExtendsBeyondEndOfLine[style];
			changed = true;
			}
		if (changed)
			{
			MiMultiPointShape shape = (MiMultiPointShape) obj;
			MiDistance size = (startSize > endSize) ? startSize : endSize;
			// TEST size = size/2;

			if (styleExtendsBeyondEndOfLine)
				{
				b.addMargins(size);
				}
			else if (shape.isVertical())
				{
				b.setXmin(b.getXmin() - size);
				b.setXmax(b.getXmax() + size);
				}
			else if (shape.isHorizontal())
				{
				b.setYmin(b.getYmin() - size);
				b.setYmax(b.getYmax() + size);
				}
			else
				{
				b.addMargins(size);
				}
			}

		return(changed);
		}

	public		boolean		render(MiPart obj, MiRenderer renderer)
		{
		if (!enabled)
			return(true);

		if (!(obj instanceof MiMultiPointShape))
			return(true);

		MiDistance 	size;
		MiAttributes	attributes 	= obj.getAttributes();
		Graphics	g		= renderer.getWindowSystemRenderer();
		MiPoint		basePoint	= new MiPoint();
		MiPoint		endPoint	= new MiPoint();

// FIX: if line length < lineEndSize then exit

		int style = attributes.getLineStartStyle();
		int lwidth = renderer.getLineWidth();
		Color color;
		Color fillColor;
		if (useTargetObjectAttributes)
			color = attributes.getColor();
		else
			color = getAttributes().getColor();

		if (useTargetObjectAttributes)
			fillColor = attributes.getBackgroundColor();
		else
			fillColor = getAttributes().getBackgroundColor();

		if (style != Mi_NONE)
			{
			size = attributes.getLineStartSize();
			if (attributes.getLineEndsSizeFnOfLineWidth())
				size += attributes.getLineWidth();
			
			double exitAngle = obj.getPointExitAngle(0) + Math.PI;
			obj.getPoint(0, basePoint);
			calcNewLineEndPoint(renderer, exitAngle, style, size, basePoint);
			obj.getPoint(0, endPoint);
			renderLineEnd(renderer, exitAngle, style, size, basePoint, endPoint, lwidth, color, fillColor);
			}
		style = attributes.getLineEndStyle();
		if (style != Mi_NONE)
			{
			size = attributes.getLineEndSize();
			if (attributes.getLineEndsSizeFnOfLineWidth())
				size += attributes.getLineWidth();
			
			double entryAngle = obj.getPointEntryAngle(-1);
			obj.getPoint(-1, basePoint);
			calcNewLineEndPoint(renderer, entryAngle, style, size, basePoint);
			g.setColor(attributes.getColor());
			obj.getPoint(-1, endPoint);
			renderLineEnd(renderer, entryAngle, style, size, basePoint, endPoint, lwidth, color, fillColor);
			}
		return(true);
		}

					// Return true if associated line should be truncated at basePoint
	public		boolean		calcNewLineEndPoint(
						MiRenderer renderer, 
						double angle, 
						int style, 
						MiDistance size, 
						MiPoint basePoint)
		{
		boolean		truncateAssocLine	= false;
		double 		sinAngle 		= Math.sin(angle);
		double 		cosAngle 		= Math.cos(angle);
		MiDistance	length			= size;

		switch (style)
			{
			case	Mi_NONE 				:
				truncateAssocLine = false;
				break;

			case	Mi_TRIANGLE_VIA_LINE_END_STYLE 		:
			case	Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE 	:
				length = size;
				truncateAssocLine = false;
				break;

			case	Mi_CIRCLE_VIA_LINE_END_STYLE 		:
			case	Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE 	:
			case	Mi_SQUARE_VIA_LINE_END_STYLE 		:
			case	Mi_FILLED_SQUARE_VIA_LINE_END_STYLE 	:
				length = size/2;
				truncateAssocLine = false;
				break;

			case	Mi_STROKED_ARROW_LINE_END_STYLE 		:
			case	Mi_FILLED_ARROW_LINE_END_STYLE 		:
			case	Mi_3FEATHER_LINE_END_STYLE		:
				// LONG_length = 2 * size;
				length = size;
				truncateAssocLine = false;
				break;

			case	Mi_FILLED_TRIANGLE_LINE_END_STYLE 	:
			case	Mi_TRIANGLE_LINE_END_STYLE 		:
			case	Mi_2FEATHER_LINE_END_STYLE		:
				// LONG_length = 2 * size;
				length = size;
				truncateAssocLine = true;
				break;

			case	Mi_DIAMOND_LINE_END_STYLE		:
				length = 2 * size;
				truncateAssocLine = true;
				break;

			case	Mi_FILLED_DIAMOND_LINE_END_STYLE	:
				length = 2 * size;
				truncateAssocLine = true;
				break;

			case	Mi_FILLED_CIRCLE_LINE_END_STYLE 	:
			case	Mi_FILLED_SQUARE_LINE_END_STYLE 	:
				length = size;
				truncateAssocLine = false;
				break;

			case	Mi_CIRCLE_LINE_END_STYLE 		:
			case	Mi_SQUARE_LINE_END_STYLE 		:
				length = size;
				truncateAssocLine = true;
				break;
			}

		basePoint.x = basePoint.x - length * cosAngle;
		basePoint.y = basePoint.y - length * sinAngle;
		return(truncateAssocLine);
		}
	protected	void		renderLineEnd(
						MiRenderer renderer, 
						double angle, 
						int style, 
						MiDistance size, 
						MiPoint basePoint,
						MiPoint endPoint,
						int lwidth,
						Color color,
						Color fillColor)
		{
		int	dSize	= 0;
		int	dx	= 0;
		int	dy	= 0;
		int	dx1	= 0;
		int	dy1	= 0;
		int	dx2	= 0;
		int	dy2	= 0;

		renderer.getTransform().wtod(basePoint, dBasePoint);
		renderer.getTransform().wtod(endPoint, dEndPoint);

		int 	baseX 		= dBasePoint.x;
		int 	baseY 		= renderer.getYmax() - dBasePoint.y;
		int 	x 		= dEndPoint.x;
		int 	y 		= renderer.getYmax() - dEndPoint.y;

		Graphics g		= renderer.getWindowSystemRenderer();

		boolean	attributesOverridden = renderer.hasOverrideAttributes();

		if ((color != Mi_TRANSPARENT_COLOR) && (!attributesOverridden))
			g.setColor(color);
		if ((fillColor == Mi_TRANSPARENT_COLOR) && (!attributesOverridden))
			fillColor = color;

		switch (style)
			{
			case	Mi_TRIANGLE_LINE_END_STYLE 		:
			case	Mi_FILLED_TRIANGLE_LINE_END_STYLE 	:
			case	Mi_TRIANGLE_VIA_LINE_END_STYLE 		:
			case	Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE 	:
			case	Mi_CIRCLE_LINE_END_STYLE 		:
			case	Mi_CIRCLE_VIA_LINE_END_STYLE 		:
			case	Mi_FILLED_CIRCLE_LINE_END_STYLE 	:
			case	Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE 	:
			case	Mi_SQUARE_LINE_END_STYLE 		:
			case	Mi_FILLED_SQUARE_LINE_END_STYLE 	:
				{
				tmpVector.x = size/2 * Math.sin(angle);
				tmpVector.y = -size/2 * Math.cos(angle);
				renderer.getTransform().wtod(basePoint, tmpVector, tmpDVector);
				dx		= tmpDVector.x;
				dy		= tmpDVector.y;
		
				tmpVector.x = size;
				tmpVector.y = size;
				renderer.getTransform().wtod(basePoint, tmpVector, tmpDVector);
				dSize		= tmpDVector.x;

				break;
				}
			default :
				{
				double arrowPointyness = ((double )60)/180 * Math.PI;	// 60 degrees
				double arrowLength = size;
				double arrowShapeFactor = arrowLength/Math.cos(arrowPointyness/2);

				tmpVector.x = arrowShapeFactor * Math.cos(angle - arrowPointyness/2);
				tmpVector.y = arrowShapeFactor * Math.sin(angle - arrowPointyness/2);
				renderer.getTransform().wtod(basePoint, tmpVector, tmpDVector);

				dx1		= tmpDVector.x;
				dy1		= -tmpDVector.y;

				tmpVector.x = arrowShapeFactor * Math.cos(angle + arrowPointyness/2);
				tmpVector.y = arrowShapeFactor * Math.sin(angle + arrowPointyness/2);
				renderer.getTransform().wtod(basePoint, tmpVector, tmpDVector);

				dx2		= tmpDVector.x;
				dy2		= -tmpDVector.y;
				}
			}

/*
System.out.println("angle = " + angle * 180 / Math.PI);
System.out.println("size = " + size);
System.out.println("dsize = " + dSize);
System.out.println("dx = " + dx);
System.out.println("dy = " + dy);
System.out.println("dx1 = " + dx1);
System.out.println("dy1 = " + dy1);
System.out.println("dx2 = " + dx2);
System.out.println("dy2 = " + dy2);
*/

		switch (style)
			{
			case	Mi_NONE		 			:
			default:
				break;

			case	Mi_STROKED_ARROW_LINE_END_STYLE 		:

				if (color == Mi_TRANSPARENT_COLOR)
					break;

				if (lwidth < 2)
					{
					g.drawLine(x, y, x - dx1, y - dy1);
					g.drawLine(x, y, x - dx2, y - dy2);
					}
				else
					{
					renderer.setColor(color);
					renderer.setBackgroundColor(color);
					renderer.drawWideLine(x, y, x - dx1, y - dy1, lwidth);
					renderer.drawWideLine(x, y, x - dx2, y - dy2, lwidth);
					}
				break;

			case 	Mi_3FEATHER_LINE_END_STYLE		:
			case	Mi_2FEATHER_LINE_END_STYLE		:

				if (color == Mi_TRANSPARENT_COLOR)
					break;

				if (lwidth < 2)
					{
					g.drawLine(baseX, baseY, baseX + dx1, baseY + dy1);
					g.drawLine(baseX, baseY, baseX + dx2, baseY + dy2);
					}
				else
					{
					renderer.setColor(color);
					renderer.setBackgroundColor(color);
					renderer.drawWideLine(baseX, baseY, baseX + dx1, baseY + dy1, lwidth);
					renderer.drawWideLine(baseX, baseY, baseX + dx2, baseY + dy2, lwidth);
					}
				break;

			case	Mi_DIAMOND_LINE_END_STYLE		:
			case	Mi_FILLED_DIAMOND_LINE_END_STYLE	:
				xPoints[0] = baseX;
				xPoints[1] = baseX + dx1;
				xPoints[2] = x;
				xPoints[3] = baseX + dx2;
				xPoints[4] = baseX;

				yPoints[0] = baseY;
				yPoints[1] = baseY + dy1;
				yPoints[2] = y;
				yPoints[3] = baseY + dy2;
				yPoints[4] = baseY;

				if ((style == Mi_FILLED_DIAMOND_LINE_END_STYLE)
					&& (fillColor != Mi_TRANSPARENT_COLOR))
					{
					if (!attributesOverridden)
						g.setColor(fillColor);
					g.fillPolygon(xPoints, yPoints, 5);
					}
				
				if ((color != Mi_TRANSPARENT_COLOR)
					&& ((style != Mi_FILLED_DIAMOND_LINE_END_STYLE) 
						|| (!color.equals(fillColor))))
					{
					if (lwidth < 2)
						{
						if (!attributesOverridden)
							g.setColor(color);
						g.drawPolygon(xPoints, yPoints, 5);
						}
					else
						{
						renderer.setColor(color);
						renderer.setBackgroundColor(color);
						for (int i = 1; i < 5; ++i)
							{
							renderer.drawWideLine(
								xPoints[i - 1], 
								yPoints[i - 1], 
								xPoints[i], 
								yPoints[i], lwidth);
							}
						}
					}
				break;
		
			case	Mi_FILLED_ARROW_LINE_END_STYLE		:

				if (fillColor == Mi_TRANSPARENT_COLOR)
					break;

				xPoints[0] = x;
				xPoints[1] = x - dx1;
				xPoints[2] = x - dx2;
				xPoints[3] = xPoints[0];

				yPoints[0] = y;
				yPoints[1] = y - dy1;
				yPoints[2] = y - dy2;
				yPoints[3] = yPoints[0];

				if (!attributesOverridden)
					g.setColor(fillColor);
				g.fillPolygon(xPoints, yPoints, 4);
				break;
	
			case	Mi_TRIANGLE_LINE_END_STYLE 		:
			case	Mi_FILLED_TRIANGLE_LINE_END_STYLE 	:
			case	Mi_TRIANGLE_VIA_LINE_END_STYLE 		:
			case	Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE 	:
				xPoints[0] = baseX + dx;
				xPoints[1] = x;
				xPoints[2] = baseX - dx;
				xPoints[3] = xPoints[0];

				yPoints[0] = baseY - dy;
				yPoints[1] = y;
				yPoints[2] = baseY + dy;
				yPoints[3] = yPoints[0];


				if (((style == Mi_FILLED_TRIANGLE_LINE_END_STYLE)
					|| (style == Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE))
					&& (fillColor != Mi_TRANSPARENT_COLOR))
					{
					if (!attributesOverridden)
						g.setColor(fillColor);
					g.fillPolygon(xPoints, yPoints, 4);
					}

				if (color != Mi_TRANSPARENT_COLOR)
					{
					if (lwidth < 2)
						{
						if (!attributesOverridden)
							g.setColor(color);
						g.drawPolygon(xPoints, yPoints, 4);
						}
					else
						{
						renderer.setColor(color);
						renderer.setBackgroundColor(color);
						for (int i = 1; i < 4; ++i)
							{
							renderer.drawWideLine(
								xPoints[i - 1], 
								yPoints[i - 1], 
								xPoints[i], 
								yPoints[i], lwidth);
							}
						}
					}
				break;

			case	Mi_CIRCLE_LINE_END_STYLE 		:
			case	Mi_CIRCLE_VIA_LINE_END_STYLE 		:

				if (color != Mi_TRANSPARENT_COLOR)
					{
					g.drawOval(
						x - (x - baseX)/2 - dSize/2, y - (y - baseY)/2 - dSize/2, 
						dSize, dSize);
					}
				break;

			case	Mi_FILLED_CIRCLE_LINE_END_STYLE 	:
			case	Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE 	:

				if (fillColor != Mi_TRANSPARENT_COLOR)
					{
					if (!attributesOverridden)
						g.setColor(fillColor);
					g.fillOval(
						x - (x - baseX)/2 - dSize/2, y - (y - baseY)/2 - dSize/2, 
						dSize, dSize);
					}
				break;

			case	Mi_SQUARE_LINE_END_STYLE 		:
			case	Mi_FILLED_SQUARE_LINE_END_STYLE 	:
				xPoints[0] = x + dx;
				xPoints[1] = x - dx;
				xPoints[2] = baseX - dx;
				xPoints[3] = baseX + dx;
				xPoints[4] = xPoints[0];

				yPoints[0] = y - dy;
				yPoints[1] = y + dy;
				yPoints[2] = baseY + dy;
				yPoints[3] = baseY - dy;
				yPoints[4] = yPoints[0];

				if ((style == Mi_FILLED_SQUARE_LINE_END_STYLE)
					&& (fillColor != Mi_TRANSPARENT_COLOR))
					{
					if (!attributesOverridden)
						g.setColor(fillColor);
					g.fillPolygon(xPoints, yPoints, 5);
					}
				if (color != Mi_TRANSPARENT_COLOR)
					{
					if (lwidth < 2)
						{
						if (!attributesOverridden)
							g.setColor(color);
						g.drawPolygon(xPoints, yPoints, 5);
						}
					else
						{
						renderer.setColor(color);
						renderer.setBackgroundColor(color);
						for (int i = 1; i < 5; ++i)
							{
							renderer.drawWideLine(
								xPoints[i - 1], 
								yPoints[i - 1], 
								xPoints[i], 
								yPoints[i], lwidth);
							}
						}
					}
				break;

			case	Mi_SQUARE_VIA_LINE_END_STYLE 		:
			case	Mi_FILLED_SQUARE_VIA_LINE_END_STYLE 	:
				int tipX = x + (x - baseX);
				int tipY = y + (y - baseY);

				xPoints[0] = tipX + dx;
				xPoints[1] = tipX - dx;
				xPoints[2] = baseX - dx;
				xPoints[3] = baseX + dx;
				xPoints[4] = xPoints[0];

				yPoints[0] = tipY - dy;
				yPoints[1] = tipY + dy;
				yPoints[2] = baseY + dy;
				yPoints[3] = baseY - dy;
				yPoints[4] = yPoints[0];

				if ((style == Mi_FILLED_SQUARE_VIA_LINE_END_STYLE)
					&& (fillColor != Mi_TRANSPARENT_COLOR))
					{
					if (!attributesOverridden)
						g.setColor(fillColor);
					g.fillPolygon(xPoints, yPoints, 5);
					}

				if (color != Mi_TRANSPARENT_COLOR)
					{
					if (lwidth < 2)
						{
						if (!attributesOverridden)
							g.setColor(color);
						g.drawPolygon(xPoints, yPoints, 5);
						}
					else
						{
						renderer.setColor(color);
						renderer.setBackgroundColor(color);
						for (int i = 1; i < 5; ++i)
							{
							renderer.drawWideLine(
								xPoints[i - 1], 
								yPoints[i - 1], 
								xPoints[i], 
								yPoints[i], lwidth);
							}
						}
					}
				break;
			}
		}
	}

