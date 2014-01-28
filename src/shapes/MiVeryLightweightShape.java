
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
import java.awt.Image; 
import java.util.Vector; 
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.FastVector; 


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * 		James Holmes
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiVeryLightweightShape extends MiPart
	{
	private static final int END_OF_BLOCK  			= 0;

	private static final int POINT  			= 1;
	private static final int LINE  				= 2;
	private static final int POLYLINE			= 3;
	private static final int RECTANGLE			= 4;
	private static final int ROUND_RECTANGLE		= 5;
	private static final int TEXT  				= 6;
	private static final int ELLIPSE 			= 7;
	private static final int ARC	 			= 8;
	private static final int POLYGON 			= 9;
	private static final int IMAGE				= 10;
	private static final int CIRCLE				= 11;

	private static final int COLOR				= 12;
	private static final int BACKGROUND_COLOR		= 13;
	private static final int TAG				= 14;

	private static final int NUMBER_OF_PRIMITIVE_TYPES	= 15;

	private static final int	primSizes[] =
		{
		1,		// END_OF_BLOCK
		3,		// POINT
		5,		// LINE
		0,		// POLYLINE
		5,		// RECTANGLE
		7,		// ROUND_RECTANLGE
		4,		// TEXT
		5,		// ELLIPSE
		7,		// ARC
		0,		// POLYGON
		6,		// IMAGE
		4,		// CIRCLE
		2,		// COLOR
		2,		// BACKGROUND_COLOR
		2,		// TAG
		};


	public		Color[]		colors;

	private		Vector		strings		= new Vector();
	private		Vector		images		= new Vector();
	private		int		blockSize	= 2048;
	private		FastVector	blocks		= new FastVector();
	private		MiCoordType[]	lastBlock 	= null;
	private		int		lastBlockIndex 	= 0;
	private		Vector		overrides	= new Vector();
	private		MiBounds	tmpBounds	= new MiBounds();


	public				MiVeryLightweightShape()
		{
		blocks.addElement(new MiCoordType[blockSize]);
		lastBlock 	= (MiCoordType[]) blocks.elementAt(0);
		lastBlock[lastBlockIndex] = END_OF_BLOCK;

		// Garantee that we have our own private copy.
		setAttributes(new MiAttributes(false));

		colors = MiColorManager.getColors();
		}

	public	void			render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		traverseList(renderer, null);
		}

	protected	void		traverseList(MiRenderer renderer, MiBounds bounds)
		{
		int	 	size = blocks.size();
		MiPoint 	pt0 = new MiPoint();
		MiPoint 	pt1 = new MiPoint();
		MiDistance 	radius;
		MiBounds 	tmpB = new MiBounds();

		int		code;
		int 		blockIndex;
		MiCoordType	start;
		MiCoordType	end;
		Image		image;
		String		str;
		int		numPoints;
		MiPoint 	pts[];
		boolean		colorOverridden = false;

		for (int blockNum = 0; blockNum < size; ++blockNum)
			{
			MiCoordType[] block = (MiCoordType[] )blocks.elementAt(blockNum);
			blockIndex = 0;
			code = (int )block[0];
			while (code != END_OF_BLOCK)
				{
				switch (code)
				    {
				case POINT :
					pt0.x = block[blockIndex + 1];
					pt0.y = block[blockIndex + 2];
					if (renderer != null)
						renderer.drawPoint(pt0);
					else
						bounds.union(pt0);
					break;

				case LINE :
					pt0.x = block[blockIndex + 1];
					pt0.y = block[blockIndex + 2];
					pt1.x = block[blockIndex + 3];
					pt1.y = block[blockIndex + 4];
					if (renderer != null)
						renderer.drawLine(pt0, pt1);
					else
						{
						bounds.union(pt0);
						bounds.union(pt0);
						}
					break;

				case POLYLINE :
					numPoints = (int )block[blockIndex + 1];
					pts = new MiPoint[numPoints];
					blockIndex += 2;
					for (int j = 0; j < numPoints; ++j)
						{
						pts[j] = new MiPoint(block[blockIndex],block[blockIndex + 1]);
						if (bounds != null)
							bounds.union(pts[j]);
						blockIndex += 2;
						}
					if (renderer != null)
						renderer.drawLines(pts);
					break;

				case RECTANGLE :
					tmpB.xmin = block[blockIndex + 1];
					tmpB.ymin = block[blockIndex + 2];
					tmpB.xmax = block[blockIndex + 3];
					tmpB.ymax = block[blockIndex + 4];
					if (renderer != null)
						renderer.drawRect(tmpB);
					else
						bounds.union(tmpB);
					break;

				case ROUND_RECTANGLE :
					tmpB.xmin = block[blockIndex + 1];
					tmpB.ymin = block[blockIndex + 2];
					tmpB.xmax = block[blockIndex + 3];
					tmpB.ymax = block[blockIndex + 4];
					if (renderer != null)
						renderer.drawRoundRect(tmpB, block[blockIndex + 5], block[blockIndex + 6]);
					else
						bounds.union(tmpB);
					break;

				case TEXT :
					pt0.x = block[blockIndex + 1];
					pt0.y = block[blockIndex + 2];
					str = (String )strings.elementAt((int )block[blockIndex + 3]);
					if (renderer != null)
						renderer.drawText(str, pt0);
					else
						bounds.union(pt0);
					break;

				case CIRCLE :
					radius = block[blockIndex + 3];
					tmpB.xmin = block[blockIndex + 1] - radius;
					tmpB.ymin = block[blockIndex + 2] - radius;
					tmpB.xmax = tmpB.xmin + radius + radius;
					tmpB.ymax = tmpB.ymin + radius + radius;
					if (renderer != null)
						renderer.drawCircle(tmpB);
					else
						bounds.union(tmpB);
					break;

				case ELLIPSE :
					tmpB.xmin = block[blockIndex + 1];
					tmpB.ymin = block[blockIndex + 2];
					tmpB.xmax = block[blockIndex + 3];
					tmpB.ymax = block[blockIndex + 4];
					if (renderer != null)
						renderer.drawEllipse(tmpB);
					else
						bounds.union(tmpB);
					break;

				case ARC :
					tmpB.xmin = block[blockIndex + 1];
					tmpB.ymin = block[blockIndex + 2];
					tmpB.xmax = block[blockIndex + 3];
					tmpB.ymax = block[blockIndex + 4];
					start = block[blockIndex + 5];
					end = block[blockIndex + 6];
					if (renderer != null)
						renderer.drawCircularArc(tmpB, start, end);
					else
						bounds.union(tmpB);
					break;

				case IMAGE :
					tmpB.xmin = block[blockIndex + 1];
					tmpB.ymin = block[blockIndex + 2];
					tmpB.xmax = block[blockIndex + 3];
					tmpB.ymax = block[blockIndex + 4];
					image = (Image )images.elementAt((int )block[blockIndex + 5]);
					if (renderer != null)
						renderer.drawImage(image, tmpB);
					else
						bounds.union(tmpB);
					break;

				case POLYGON :
					numPoints = (int )block[blockIndex + 1];
					pts = new MiPoint[numPoints];
					blockIndex += 2;
					for (int j = 0; j < numPoints; ++j)
						{
						pts[j] = new MiPoint(block[blockIndex],block[blockIndex + 1]);
						blockIndex += 2;
						if (bounds != null)
							bounds.union(pts[j]);
						}
					if (renderer != null)
						renderer.drawPolygon(pts, true /* closed */);
					break;

				case COLOR :
					if (renderer != null)
						renderer.setColor(colors[(int )block[blockIndex + 1]]);
					break;

				case BACKGROUND_COLOR :
					if (renderer != null)
						renderer.setBackgroundColor(colors[(int )block[blockIndex + 1]]);
					break;

				case TAG :
					//if (colorOverridden)
						//{
						//renderer.popOverrideColor();
						//colorOverridden = false;
						//}
					int tag = (int )block[blockIndex + 1];
					//int index = findColorOverrideForTag(tag);
					//if (index > -1)
						//{
						//renderer.pushOverrideColor(((MiColorOverride )overrides.elementAt(index)).color);
						//colorOverridden = true;
						//}
					break;
				    }

				blockIndex += primSizes[code];
				code = (int )block[blockIndex];
				}
			}
		}

	public	void			appendTag(int tag)
		{
		if (lastBlockIndex + primSizes[TAG] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = TAG;
		lastBlock[lastBlockIndex + 1] = tag;
		lastBlockIndex += primSizes[TAG];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}

	private	void			appendPoint(int code, MiPoint pt1)
		{
		if (lastBlockIndex + primSizes[POINT] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = POINT;
		lastBlock[lastBlockIndex + 1] = pt1.x;
		lastBlock[lastBlockIndex + 2] = pt1.y;
		lastBlockIndex += primSizes[POINT];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}
	public	void			appendLine(MiPoint pt1, MiPoint pt2)
		{
		append4PointPrim(LINE, pt1, pt2);
		}
	public	void			appendPolyline(MiPoint pts[])
		{
		if (lastBlockIndex + pts.length*2 + 2 >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex++] = POLYLINE;
		lastBlock[lastBlockIndex++] = pts.length;
		for (int i = 0; i < pts.length; ++i)
			{
			lastBlock[lastBlockIndex++] = pts[i].x;
			lastBlock[lastBlockIndex++] = pts[i].y;
			}
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}
	public	void			appendRectangle(MiBounds b)
		{
		append4PointPrim(RECTANGLE, b.getLLCorner(), b.getURCorner());
		}
	public	void			appendRoundRectangle(MiBounds b, MiDistance arcWidth, MiDistance arcHeight)
		{
		if (lastBlockIndex + primSizes[ROUND_RECTANGLE] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = ROUND_RECTANGLE;
		lastBlock[lastBlockIndex + 1] = b.xmin;
		lastBlock[lastBlockIndex + 2] = b.ymin;
		lastBlock[lastBlockIndex + 3] = b.xmax;
		lastBlock[lastBlockIndex + 4] = b.ymax;
		lastBlock[lastBlockIndex + 5] = arcWidth;
		lastBlock[lastBlockIndex + 6] = arcHeight;
		lastBlockIndex += primSizes[ROUND_RECTANGLE];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}

	private	void			appendText(MiPoint pt1, String str)
		{
		if (lastBlockIndex + primSizes[TEXT] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = TEXT;
		lastBlock[lastBlockIndex + 1] = pt1.x;
		lastBlock[lastBlockIndex + 2] = pt1.y;
		strings.addElement(str);
		lastBlock[lastBlockIndex + 3] = strings.size() - 1;
		lastBlockIndex += primSizes[TEXT];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}
	public	void			appendCircle(MiPoint pt1, MiDistance radius)
		{
		if (lastBlockIndex + primSizes[CIRCLE] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = CIRCLE;
		lastBlock[lastBlockIndex + 1] = pt1.x;
		lastBlock[lastBlockIndex + 2] = pt1.y;
		lastBlock[lastBlockIndex + 3] = radius;
		lastBlockIndex += primSizes[CIRCLE];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}
	public	void			appendEllipse(MiPoint pt1, MiDistance hRadius, MiDistance vRadius)
		{
		if (lastBlockIndex + primSizes[ELLIPSE] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = ELLIPSE;
		lastBlock[lastBlockIndex + 1] = pt1.x - hRadius;
		lastBlock[lastBlockIndex + 2] = pt1.y - vRadius;
		lastBlock[lastBlockIndex + 3] = pt1.x + hRadius;
		lastBlock[lastBlockIndex + 4] = pt1.y + vRadius;
		lastBlockIndex += primSizes[ELLIPSE];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}
	public	void			appendEllipse(MiBounds bounds)
		{
		if (lastBlockIndex + primSizes[ELLIPSE] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = ELLIPSE;
		lastBlock[lastBlockIndex + 1] = bounds.xmin;
		lastBlock[lastBlockIndex + 2] = bounds.ymin;
		lastBlock[lastBlockIndex + 3] = bounds.xmax;
		lastBlock[lastBlockIndex + 4] = bounds.ymax;
		lastBlockIndex += primSizes[ELLIPSE];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}
	private	void			appendArc(MiPoint pt1, MiDistance radius, MiCoordType startAngle, MiCoordType endAngle)
		{
		if (lastBlockIndex + primSizes[ARC] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = ARC;
		lastBlock[lastBlockIndex + 1] = pt1.x - radius;
		lastBlock[lastBlockIndex + 2] = pt1.y - radius;
		lastBlock[lastBlockIndex + 3] = pt1.x + radius;
		lastBlock[lastBlockIndex + 4] = pt1.y + radius;
		lastBlock[lastBlockIndex + 5] = startAngle;
		lastBlock[lastBlockIndex + 6] = endAngle;
		lastBlockIndex += primSizes[ARC];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}

	public	void			appendPolygon(MiPoint pts[])
		{
		if (lastBlockIndex + pts.length*2 + 2 >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex++] = POLYGON;
		lastBlock[lastBlockIndex++] = pts.length;
		for (int i = 0; i < pts.length; ++i)
			{
			lastBlock[lastBlockIndex++] = pts[i].x;
			lastBlock[lastBlockIndex++] = pts[i].y;
			}
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}
	public	void			appendImage(MiBounds bounds, Image image)
		{
		if (lastBlockIndex + primSizes[IMAGE] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = IMAGE;
		lastBlock[lastBlockIndex + 1] = bounds.xmin;
		lastBlock[lastBlockIndex + 2] = bounds.ymin;
		lastBlock[lastBlockIndex + 3] = bounds.xmax;
		lastBlock[lastBlockIndex + 4] = bounds.ymax;
		images.addElement(image);
		lastBlock[lastBlockIndex + 5] = images.size() - 1;
		lastBlockIndex += primSizes[IMAGE];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}

	public		int		getColorValue(Color c)
		{
		// colors[0] = transparent which is null
		if (c == null)
			return(0);

		for (int i = 1; i < colors.length; ++i)
			{
			if (colors[i].equals(c))
				return(i);
			}
		return(-1);
		}
	public	void			appendColor(Color color)
		{
		int index = getColorValue(color);
		if (index == -1)
			{
			index = colors.length;
			Color[] newColors = new Color[index + 1];
			System.arraycopy(colors, 0, newColors, 0, index + 1);
			newColors[index] = color;
			colors = newColors;
			}
		appendColor(index);
		}
	public	void			appendColor(int color)
		{
		if (lastBlockIndex + primSizes[COLOR] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = COLOR;
		lastBlock[lastBlockIndex + 1] = color;
		lastBlockIndex += primSizes[COLOR];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}

	public	void			appendBackgroundColor(Color color)
		{
		int index = getColorValue(color);
		if (index == -1)
			{
			index = colors.length;
			Color[] newColors = new Color[index + 1];
			System.arraycopy(colors, 0, newColors, 0, index + 1);
			newColors[index] = color;
			colors = newColors;
			}
		appendBackgroundColor(index);
		}
	public	void			appendBackgroundColor(int color)
		{
		if (lastBlockIndex + primSizes[BACKGROUND_COLOR] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = BACKGROUND_COLOR;
		lastBlock[lastBlockIndex + 1] = color;
		lastBlockIndex += primSizes[BACKGROUND_COLOR];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}

	public	void			overrideColorTaggedPrims(int tag, Color color)
		{
		overrides.addElement(new MiColorOverride(tag, color));
		}

	private	int			findColorOverrideForTag(int tag)
		{
		for (int i = 0; i < overrides.size(); ++i)
			{
			if (((MiColorOverride )overrides.elementAt(i)).tag == tag)
				return(i);
			}
		return(-1);
		}
	public	void			removeOverrideColorTaggedPrims(int tag)
		{
		int index = findColorOverrideForTag(tag);
		if (index > -1)
			{
			overrides.removeElementAt(index);
			}
		}

	public	void			setBlockSize(int size)
		{
		blockSize = size;
		blocks.removeAllElements();
		}
	public	void			removeTaggedPrims(int tag)
		{
		int size = blocks.size();
		int code;
		int blockIndex;
		for (int i = 0; i < size; ++i)
			{
			MiCoordType[] block = (MiCoordType[] )blocks.elementAt(i);
			blockIndex = 0;
			code = (int )block[0];
			while (code != END_OF_BLOCK)
				{
				if ((code == TAG) && (block[blockIndex + 1] == tag))
					{
					}
				else
					{
					blockIndex += primSizes[code];
					}

				code = (int )block[blockIndex];
				}
			}
		}
	public	boolean			findClosestTaggedPrim(int tag, MiBounds area, MiPoint closestPt)
		{
		return(false);
		}
	public	boolean			findClosestConnPtOfTaggedPrim(int[] tags, MiBounds area, MiPoint closestPt)
		{
		return(false);
		}
	public	boolean			getPickListOfTaggedPrimsInArea(int[] tags, MiBounds area, IntVector offsetsToPrims)
		{
		return(false);
		}
	public	boolean			getTagListOfPrimsInArea(MiBounds area, IntVector tagList)
		{
		return(false);
		}

	protected	void		calcMinimumSize(MiSize size)
		{
		reCalcBounds(tmpBounds);
		size.setSize(tmpBounds);
		}
	protected	void		calcPreferredSize(MiSize size)
		{
		reCalcBounds(tmpBounds);
		size.setSize(tmpBounds);
		}
	protected	void		reCalcBounds(MiBounds bounds)
		{
		bounds.reverse();
		traverseList(null, bounds);
		}
	public		void		invalidateNewArea(MiBounds area)
		{
		if (!getBounds(tmpBounds).contains(area))
			{
			refreshBounds();
			}
		invalidateArea(area);
		}
	private	void			append4PointPrim(int code, MiPoint pt1, MiPoint pt2)
		{
		if (lastBlockIndex + primSizes[code] >= blockSize)
			allocNewBlock();
		lastBlock[lastBlockIndex] = code;
		lastBlock[lastBlockIndex + 1] = pt1.x;
		lastBlock[lastBlockIndex + 2] = pt1.y;
		lastBlock[lastBlockIndex + 3] = pt2.x;
		lastBlock[lastBlockIndex + 4] = pt2.y;
		lastBlockIndex += primSizes[code];
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}

	private	void			allocNewBlock()
		{
		blocks.addElement(new MiCoordType[blockSize]);
		lastBlock = (MiCoordType[] )blocks.lastElement();
		lastBlockIndex = 0;
		lastBlock[lastBlockIndex] = END_OF_BLOCK;
		}
	}
class MiColorOverride
	{
	int	tag;
	Color	color;

	public			MiColorOverride(int tag, Color color)
		{
		this.tag = tag;
		this.color = color;
		}
	}


