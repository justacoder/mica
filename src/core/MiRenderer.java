
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
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import com.swfm.mica.util.FastVector;
import java.util.Stack;
import java.awt.Rectangle;
import com.swfm.mica.util.CacheVector;


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiRenderer implements MiiTypes, MiiAttributeTypes
	{
	private	final static int 		MIN_DRAWN_FONT_HEIGHT = 6;
	private	final static int 		MIN_DRAWN_FONT_WIDTH = 3;
	private		Graphics		g;
	private		Image			offScreenBuffer;
	private		Rectangle		canvasSize;

	private		boolean			hasAWTClipRectBug= true;

	private		boolean			doubleBuffering;
	private		boolean			isPrinterRenderer;
	private		Graphics		screenRenderer;
	private		Graphics		windowSystemRenderer;
	private		MiCanvas		canvas;


	private		FastVector		filters;


						// Current draw attributes
	private		MiAttributes		attributes	= new MiAttributes();

			// Used to override ALL attributes. Can be used to emulate
			// graphics libaries that embed attributes in the graph 
			// instead of assigning attributes to individual objects.
	private		Stack			overrides	= new Stack();

	private		MiTransforms		transforms 	= new MiTransforms();

	private		MiDevicePoint 		dpt0 		= new MiDevicePoint();
	private		MiDevicePoint 		dpt1 		= new MiDevicePoint();
	private		MiPoint 		wpt0 		= new MiPoint();
	private		MiPoint 		wpt1 		= new MiPoint();
	private		MiVector 		wVector		= new MiVector();
	private		MiDeviceVector 		dVector		= new MiDeviceVector();
	private		MiBounds 		wBnds 		= new MiBounds();
	private		MiDeviceBounds 		dBnds 		= new MiDeviceBounds();

	private		int 			ymax 		= 0;
	private		MiDoubleBuffers		doubleBuffers	= new MiDoubleBuffers();

	// Attributes here can be overridden .. they are NOT the current pen value 
	// at the java.graphics level
	private		Color			curColor	= null;
	private		Color			curBGColor	= null;
	private		Color			curXORColor	= null;
	private		MiiDeviceRenderer	curBGRenderer;
	private		Image			curBGImage	= null;
	private		Image			curBGTile	= null;
	private		int			curWriteMode	= -1;
	private		int			curBorderLook	= Mi_FLAT_BORDER_LOOK;
	private		int			curLineStyle	= -1;
	private		MiFont			curFont		= null;
	private		boolean			curFilled;
	private		MiDistance		curLineWidth	= 0;
	private		int[]			fiveXPoints	= new int[5];
	private		int[]			fiveYPoints	= new int[5];
	private		int[]			tmpXPoints	= new int[20];
	private		int[]			tmpYPoints	= new int[20];
	private		MiScale			tmpScale	= new MiScale();
	private		boolean			useSetClip;
	private		boolean			useCanvasDrawNow;
//	private		boolean			drawImageFlushingRect = true;
	private		Rectangle		outerClipBounds	= new Rectangle(0,0,
									Integer.MAX_VALUE,
									Integer.MAX_VALUE);




	public				MiRenderer()
		{
		if (MiSystem.getJDKVersion() >= 1.2)
			{
			useSetClip = true;
			useCanvasDrawNow = true;
			}
/*
		if (MiSystem.getJDKVersion() >= 1.4)
			{
			drawImageFlushingRect = false;
			}
*/
		}

	public		MiRenderer	copy()
		{
		MiRenderer renderer 		= new MiRenderer();
		renderer.ymax 			= ymax;
		renderer.g			= getGraphics();
		renderer.offScreenBuffer	= offScreenBuffer;
		renderer.canvasSize		= canvasSize;
		renderer.doubleBuffering	= doubleBuffering;
		renderer.screenRenderer		= screenRenderer;
		renderer.windowSystemRenderer	= windowSystemRenderer;
		renderer.canvas			= canvas;
		renderer.transforms.setIndentX(transforms.getIndentX());
		renderer.resetAllAttributes();
		return(renderer);
		}
					// Used in conjuction with 'isPrintable' attribute
	public		void		setIsPrinterRenderer(boolean flag)
		{
		isPrinterRenderer = flag;
		}
	public		boolean		isPrinterRenderer()
		{
		return(isPrinterRenderer);
		}

	public	void			setIndentX(int indent)
		{
		transforms.setIndentX(indent);
		}
	public		int		getIndentX()
		{
		return(transforms.getIndentX());
		}
					// Used to compensate for upside down window coords
	public	void			setYmax(int ymax)
		{
		this.ymax = ymax;
		}

	public	MiiTransform		getTransform()
		{
		return(transforms);
		}
	public	void			pushTransform(MiiTransform t)
		{
		transforms.pushTransform(t);
		}
	public	void			popTransform()
		{
		transforms.popTransform();
		}
	public	void			clearTransforms()
		{
		transforms.clearTransforms();
		}
	public		void		setHasAWTClipRectBug(boolean flag)
		{
		hasAWTClipRectBug = flag;
		}
	public		boolean		getHasAWTClipRectBug()
		{
		return(hasAWTClipRectBug);
		}
	public	MiBounds		getClipBounds()
		{
		return(new MiBounds(transforms.getClipBounds()));
		}
	public	MiBounds		getClipBounds(MiBounds b)
		{
		return(transforms.getClipBounds(b));
		}
	public	void			setClipBounds(MiBounds clip)
		{
		transforms.setClipBounds(clip);
		transforms.wtod(clip, dBnds);

		setClipBounds(	dBnds.xmin, 
				ymax - dBnds.ymax, 
				dBnds.xmax - dBnds.xmin + 1, 
				dBnds.ymax - dBnds.ymin + 1);
		}

	protected	void		setClipBounds(int x, int y, int width, int height)
		{
		// To fix awt clip rect bug, alloc new graphics handle
		if ((hasAWTClipRectBug) && (g != null) && (!isPrinterRenderer))
			{
			g.dispose();
			g = null;
			}
		
		if (g == null)
			{
			if ((doubleBuffering) && (offScreenBuffer != null))
				{
				g = offScreenBuffer.getGraphics();
				}
			else if (canvas != null)
				{
				g = canvas.getGraphics();
				}
			updateWriteMode();
			}

		if (x < outerClipBounds.x)
			x = outerClipBounds.x;
		if (x + width > outerClipBounds.x + outerClipBounds.width)
			width = outerClipBounds.x + outerClipBounds.width - x;

		if (y < outerClipBounds.y)
			y = outerClipBounds.y;
		if (y + height > outerClipBounds.y + outerClipBounds.height)
			height = outerClipBounds.y + outerClipBounds.height - y;

		if (width < 0)
			width = 0;
		if (height < 0)
			height = 0;

		if (useSetClip)
			{
			g.setClip(x, y, width, height);
			}
		else
			{
			g.clipRect(x, y, width, height);
			}
		}
	protected	Rectangle	pushClipBounds(int x, int y, int width, int height)
		{
		Rectangle clip = g.getClipBounds();
		if (x < clip.x)
			x = clip.x;
		if (x + width > clip.x + clip.width)
			width = clip.x + clip.width - x;

		if (y < clip.y)
			y = clip.y;
		if (y + height > clip.y + clip.height)
			height = clip.y + clip.height - y;

		if (width < 0)
			width = 0;
		if (height < 0)
			height = 0;

		setClipBounds(x, y, width, height);
		return(clip);
		}
	protected	void		popClipBounds(Rectangle clip)
		{
		setClipBounds(clip.x, clip.y, clip.width, clip.height);
		}
	public	void			clearClipBounds()
		{
		transforms.clearClipBounds();
/*
		//g.clipRect(canvasSize.x, canvasSize.y, canvasSize.width, canvasSize.height);
		if (doubleBuffering)
			g = offScreenBuffer.getGraphics();
		else
			g = canvas.getGraphics();
*/
		}
	public	boolean			boundsClipped(MiBounds bounds)
		{
		return(transforms.boundsClipped(bounds));
		}
	public	void			flush()
		{
		if (doubleBuffering)
			screenRenderer.drawImage(offScreenBuffer, 0, 0, null);
		}
	public	void			flush(MiBounds bounds)
		{
		if (doubleBuffering)
			{
			wpt0.x = bounds.xmin;
			wpt0.y = bounds.ymin;
			transforms.wtod(wpt0, dpt0);
			wpt1.x = bounds.xmax;
			wpt1.y = bounds.ymax;
			transforms.wtod(wpt1, dpt1);
// Messes up Swing which seems to look at the clip bounds and do the wrong thing... screenRenderer.clipRect(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x + 1, dpt1.y - dpt0.y + 1);
			screenRenderer.drawImage(offScreenBuffer, 0, 0, null);
			}
		}
/*****
	public		void		drawNow(MiPart part, MiBounds b)
		{
		if (useCanvasDrawNow)
			{
			transforms.wtod(b, dBnds);
			tmpRect.x = dBnds.xmin;
			tmpRect.y = dBnds.ymin;
			tmpRect.width = dBnds.xmax - dBnds.xmin;
			tmpRect.height = dBnds.ymax - dBnds.ymin;
			canvas.drawNow(tmpRect);
			}
		else
			{
			part.doRender(renderer);
			}
		}
****/
	public		void		pushFilter(MiiViewFilter filter)
		{
		if (filters == null)
			filters = new FastVector();
		filters.addElement(filter);
		}
	public		void		popFilter()
		{
		filters.removeElementAt(filters.size() - 1);
		}
	public		MiPart		filterPart(MiPart part)
		{
		if (filters != null)
			{
			for (int i = 0; i < filters.size(); ++i)
				{
				part = ((MiiViewFilter )filters.elementAt(i)).accept(part);
				if (part == null)
					return(null);
				}
			}
		return(part);
		}
	// -----------------------------------
	// Off Screen Buffering renderers
	// -----------------------------------
	public	void			setSingleBufferedScreen()
		{
		doubleBuffering = false;
		g = screenRenderer;
		if (g != null)
			updateWriteMode();
		}
	public	void			setDoubleBufferedScreen(Image offScreenBuffer)
		{
		this.offScreenBuffer = offScreenBuffer;
		doubleBuffering = true;
		if (g != null)
			g.dispose();
		g = offScreenBuffer.getGraphics();
		updateWriteMode();
		}
	public	boolean			isDoubleBuffered()
		{
		return(doubleBuffering);
		}
	public	void			setCanvas(MiCanvas canvas)
		{
		this.canvas = canvas;
		g = screenRenderer;
		if (g != null)
			updateWriteMode();
		}
	public	Graphics	getGraphics()
		{
		return(g);
		}
	public		void		setGraphics(Graphics g)
		{
		if (!doubleBuffering)
			this.g = g;
		screenRenderer = g;
		outerClipBounds = g.getClipBounds();
		}
	public		MiCanvas	getCanvas()
		{
		return(canvas);
		}
					// FIX: Make this part of transforms - no separate 'y = ymax - y' step.
	public		int		getYmax()
		{
		return(ymax);
		}
	public		Graphics	getWindowSystemRenderer()
		{
		if (windowSystemRenderer == null)
			{
			return(g);
			}
		return(windowSystemRenderer);
		}
	public		void		setWindowSystemRenderer(Graphics graphics)
		{
		screenRenderer = graphics;
		windowSystemRenderer = graphics;
		}
	public	void			setCanvasSize(Rectangle r)
		{
		canvasSize = r;
		}
	public	Image			makeDoubleBuffer(MiBounds bounds)
		{
		transforms.wtod(bounds, bounds);
		return(canvas.createImage((int )(bounds.getWidth() + 1.5), (int )(bounds.getHeight() + 1.5)));
		}
	public	void			pushDoubleBuffer(Image image, MiBounds bounds)
		{
		MiDoubleBufferSpec spec = (MiDoubleBufferSpec )doubleBuffers.useTheNextElement();

		spec.g = g;
		spec.image = image;
		spec.offScreenBuffer = offScreenBuffer;
		spec.doubleBuffering = doubleBuffering;
		spec.screenRenderer = screenRenderer;
		spec.ymax = ymax;
		getClipBounds(spec.clipBounds);

		doubleBuffering = true;
		offScreenBuffer = image;
		g = image.getGraphics();
		updateWriteMode();

		wpt0.x = bounds.xmin;
		wpt0.y = bounds.ymin;
		transforms.wtod(wpt0, dpt0);
		wpt1.x = bounds.xmax;
		wpt1.y = bounds.ymax;
		transforms.wtod(wpt1, dpt1);
		transforms.getDeviceTranslation(spec.translation);
		//spec.translation.y = dpt0.y;
		ymax = dpt1.y - dpt0.y;
		//transforms.translate(spec.translation);
		transforms.setDeviceTranslation(
			new MiVector(-dpt0.x + spec.translation.x, -dpt0.y + spec.translation.y));

		setClipBounds(bounds);
		}
	public	void			popDoubleBuffer(Image image)
		{
		MiDoubleBufferSpec spec = (MiDoubleBufferSpec )doubleBuffers.theElementAt(doubleBuffers.size() - 1);
		if (spec.image != image)
			throw new IllegalArgumentException("MiContext.popDoubleBuffer, buffer not on top of stack");

		g.dispose();
		g = spec.g;
		offScreenBuffer = spec.offScreenBuffer;
		doubleBuffering = spec.doubleBuffering;
		screenRenderer = spec.screenRenderer;

		ymax = spec.ymax;
		//spec.translation.x = -spec.translation.x;
		//spec.translation.y = -spec.translation.y;
		//transforms.translate(spec.translation);
		transforms.setDeviceTranslation(spec.translation);
		setClipBounds(spec.clipBounds);

		updateWriteMode();

		spec.image = null;

		doubleBuffers.removeElementAt(doubleBuffers.size() - 1);
		}

	// -----------------------------------
	// Attribute override routines
	// -----------------------------------
	public	void			pushOverrideAttributes(MiAttributes atts)
		{
		overrides.push(atts);
		if (overrides.size() == 1)
			attributes = atts;
		else
			attributes = (MiAttributes )attributes.overrideFrom(atts);
		}
	public	void			popOverrideAttributes()
		{
		overrides.pop();
		attributes = MiPart.getDefaultAttributes();
		if (overrides.size() != 0)
			{
			for (int i = 0; i < overrides.size(); ++i)
				{
				attributes = (MiAttributes )attributes.overrideFrom((MiAttributes )overrides.elementAt(i));
				}
			}
		}
	public		boolean		hasOverrideAttributes()
		{
		return(overrides.size() > 0);
		}

	// -----------------------------------
	// Attribute routines
	// -----------------------------------
	public	void			setAttributes(MiAttributes atts)
		{
		if (attributes == atts)
			return;

		if (atts == null)
			{
			attributes = null;
			return;
			}

		if (overrides.size() != 0)
			{
			attributes = atts;
			atts = (MiAttributes )atts.overrideFrom((MiAttributes )overrides.lastElement());
			}
		else
			{
			attributes = atts;
			}
		setAllAttributes(atts);
		}

	private	void			resetAllAttributes()
		{
		curColor = null;
		curBGColor = null;
		curWriteMode = -1;
		curLineStyle = -1;
		curFont = null;
		curLineWidth = -1;
		curBorderLook = Mi_FLAT_BORDER_LOOK;
		curFilled = false;
		attributes = null;
		}
	private	void			setAllAttributes(MiAttributes atts)
		{
		curBGRenderer = (MiiDeviceRenderer )atts.objectAttributes[Mi_BACKGROUND_RENDERER];
		curBGTile = (Image )atts.objectAttributes[Mi_BACKGROUND_TILE];
		curBGImage = (Image )atts.objectAttributes[Mi_BACKGROUND_IMAGE];

		curColor = (Color )atts.objectAttributes[Mi_COLOR];
		curBGColor = (Color )atts.objectAttributes[Mi_BACKGROUND_COLOR];

		if (curWriteMode != (int )atts.intAttributes[Mi_WRITE_MODE - Mi_START_INTEGER_INDEX])
			{
			curWriteMode = (int )atts.intAttributes[Mi_WRITE_MODE - Mi_START_INTEGER_INDEX];
			if (curWriteMode == Mi_XOR_WRITEMODE)
				{
				curXORColor = (Color )atts.objectAttributes[Mi_XOR_COLOR];
				g.setXORMode(curXORColor);
				}
			else
				{
				g.setPaintMode();
				}
			}
		curFont = (MiFont )atts.objectAttributes[Mi_FONT];
		curBorderLook = (int )atts.intAttributes[Mi_BORDER_LOOK - Mi_START_INTEGER_INDEX];
		curLineWidth = (MiDistance )atts.doubleAttributes[Mi_LINE_WIDTH - Mi_START_DOUBLE_INDEX];
		curLineStyle = (int )atts.intAttributes[Mi_LINE_STYLE - Mi_START_INTEGER_INDEX];
		curFilled = atts.booleanAttributes[Mi_FILLED - Mi_START_BOOLEAN_INDEX];
		if (curFilled)
			{
			curBGColor = curColor;
			}
		}

	private		void		updateWriteMode()
		{
		if (curWriteMode == Mi_XOR_WRITEMODE)
			{
			g.setXORMode(curXORColor);
			}
		else
			{
			g.setPaintMode();
			}
		}
	public	void			setBorderLook(int look)
		{
		curBorderLook = look;
		}
	public	void			setColor(Color c)
		{
		if (overrides.size() == 0)
			curColor = c;
		}
	public	void			setBackgroundColor(Color c)
		{
		if (overrides.size() == 0)
			curBGColor = c;
		}
	public	void			setFont(MiFont f)
		{
		if (curFont != f)
			{
			curFont = f;
			//g.setFont(f);
			//curFontMetrics = g.getFontMetrics(curFont);
			//curFontHeight = curFontMetrics.getHeight();
			}
		}
	public	void			setLineWidth(MiDistance lWidth)
		{
		curLineWidth = lWidth;
		}
	protected	int		getLineWidth()
		{
		wBnds.setBounds(0,0,curLineWidth, curLineWidth);
		transforms.wtod(wBnds, wBnds);
		return((int )(wBnds.getWidth() + 0.5));
		}
	public	void			setLineStyle(int lStyle)
		{
		curLineStyle = lStyle;
		}
	public		int		getWriteMode()
		{
		return(curWriteMode);
		}
	public		void		setWriteMode(int wmode)
		{
		if (curWriteMode != wmode)
			{
			curWriteMode = wmode;
			if (wmode == Mi_XOR_WRITEMODE)
				{
				g.setXORMode(curXORColor);
				}
			else
				{
				g.setPaintMode();
				}
			}
		}

	// -----------------------------------
	// Graphics drawing routines
	// -----------------------------------
	public	void			drawLine(MiPoint pt1, MiPoint pt2)
		{
		drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
		}

	public	void			drawLine(MiCoord x1, MiCoord y1, MiCoord x2, MiCoord y2)
		{
		if (curColor == null)
			return;
		wpt0.x = x1;
		wpt0.y = y1;
		wpt1.x = x2;
		wpt1.y = y2;
		transforms.wtod(wpt0, dpt0);
		transforms.wtod(wpt1, dpt1);
		g.setColor(curColor);
		int lwidth = 0;
		if (curLineWidth !=0)
			lwidth = getLineWidth();
		drawLine(dpt0.x, dpt0.y, dpt1.x, dpt1.y, lwidth);
		}
	public	void			drawLines(MiPoint points[])
		{
		if (curColor == null)
			return;
		transforms.wtod(points[0], dpt0);
		g.setColor(curColor);
		int lwidth = 0;
		if (curLineWidth !=0)
			lwidth = getLineWidth();
		if (points.length == 2)
			{
			transforms.wtod(points[1], dpt1);
			drawLine(dpt0.x, dpt0.y, dpt1.x, dpt1.y, lwidth);
			return;
			}
		for (int i = 1; i < points.length; ++i)
			{
			transforms.wtod(points[i], dpt1);
			drawLine(dpt0.x, dpt0.y, dpt1.x, dpt1.y, lwidth);
			dpt0.x = dpt1.x;
			dpt0.y = dpt1.y;
			}
		}
	public	void			drawLines(MiCoord[] xPoints, MiCoord[] yPoints, int numPoints)
		{
		if (curColor == null)
			return;

		wpt0.x = xPoints[0];
		wpt0.y = yPoints[0];
		transforms.wtod(wpt0, dpt0);
		g.setColor(curColor);
		int lwidth = 0;
		if (curLineWidth !=0)
			lwidth = getLineWidth();
		if (numPoints == 2)
			{
			wpt0.x = xPoints[1];
			wpt0.y = yPoints[1];
			transforms.wtod(wpt0, dpt1);
			drawLine(dpt0.x, dpt0.y, dpt1.x, dpt1.y, lwidth);
			return;
			}
		for (int i = 1; i < numPoints; ++i)
			{
			wpt0.x = xPoints[i];
			wpt0.y = yPoints[i];
			transforms.wtod(wpt0, dpt1);
			drawLine(dpt0.x, dpt0.y, dpt1.x, dpt1.y, lwidth);
			dpt0.x = dpt1.x;
			dpt0.y = dpt1.y;
			}
		}
	public	void			drawLines(FastVector points)
		{
		if (curColor == null)
			return;
		transforms.wtod((MiPoint )points.elementAt(0), dpt0);
		g.setColor(curColor);
		int lwidth = 0;
		if (curLineWidth !=0)
			lwidth = getLineWidth();
		if (points.size() == 2)
			{
			transforms.wtod((MiPoint )points.elementAt(1), dpt1);
			drawLine(dpt0.x, dpt0.y, dpt1.x, dpt1.y, lwidth);
			return;
			}
		for (int i = 1; i < points.size(); ++i)
			{
			transforms.wtod((MiPoint )points.elementAt(i), dpt1);
			drawLine(dpt0.x, dpt0.y, dpt1.x, dpt1.y, lwidth);
			dpt0.x = dpt1.x;
			dpt0.y = dpt1.y;
			}
		}
	public	void		drawLine(int x1, int y1, int x2, int y2, int lwidth)
		{
		if (curLineStyle == Mi_DASHED_LINE_STYLE)
			{
			drawDashedLine(x1, y1, x2, y2, 6, 6);
			}
		else if (curLineStyle == Mi_SPARSE_DOTTED_LINE_STYLE)
			{
			drawDashedLine(x1, y1, x2, y2, 1, 4);
			}
		else if (curLineStyle == Mi_DOTTED_LINE_STYLE)
			{
			drawDashedLine(x1, y1, x2, y2, 0, 2);
			}
		else if (lwidth > 1)
			{
			drawWideLine(x1, ymax - y1, x2, ymax - y2, lwidth);
			}
		else if ((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK)
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
				.drawLine(g, attributes, x1, ymax - y1, x2, ymax - y2);
			}
		else
			{
			g.drawLine(x1, ymax - y1, x2, ymax - y2);
			}
		}
	private		void		drawDashedLine(int x1, int y1, int x2, int y2, int dashLength, int spaceLength)
		{
		//int dashLength = 6;
		//int spaceLength = 6;

//MiDebug.println("drawDashedLine: x1=" + x1 + ",y1=" + y1 + ",x2= "+ x2 + ",y2=" + y2);

		y1 = ymax - y1;
		y2 = ymax - y2;

		int dx = x2 - x1;
		int dy = y2 - y1;
		int num;
		double ddx_dash;
		double ddx_space;
		double ddy_dash;
		double ddy_space;

		if ((dx == 0) && (dy == 0))
			{
			return;
			}
		if (dx == 0)
			{
			ddx_dash = 0;
			ddx_space = 0;
			ddy_dash = dashLength;
			ddy_space = spaceLength;
			num = dy/(dashLength + spaceLength);
			}
		else if (dy == 0)
			{
			ddx_dash = dashLength;
			ddx_space = spaceLength;
			ddy_dash = 0;
			ddy_space = 0;
			num = dx/(dashLength + spaceLength);
			}
		if (dx < 0)
			dx = -dx;
		if (dy < 0)
			dy = -dy;

		if (dy > dx)
			{
			ddy_dash = dashLength;
			ddy_space = spaceLength;
			num = dy/(dashLength + spaceLength);
			ddx_dash = ((double )dx) * dashLength/dy;
			ddx_space = ((double )dx) * spaceLength/dy;
			}
		else
			{
			ddx_dash = dashLength;
			ddx_space = spaceLength;
			num = dx/(dashLength + spaceLength);
			ddy_dash = ((double )dy) * dashLength/dx;
			ddy_space = ((double )dy) * spaceLength/dx;
			}

		if (y2 - y1 < 0)
			{
			ddy_dash = -ddy_dash;
			ddy_space = -ddy_space;
			}

		if (x2 - x1 < 0)
			{
			ddx_dash = -ddx_dash;
			ddx_space = -ddx_space;
			}

		double px1 = x1;
		double py1 = y1;
		double px2 = x1;
		double py2 = y1;
		boolean draw = true;
		while (num-- > 0)
			{
			px1 = px2;
			px2 += ddx_dash;
			py1 = py2;
			py2 += ddy_dash;
			g.drawLine((int )px1, (int )py1, (int )px2, (int )py2);
			px2 += ddx_space;
			py2 += ddy_space;
			}

		// Handle the last few pixels so that the line has some 'on' pixels at the last point.
		px2 = x2;
		py2 = y2;
		px1 = px2 - ddx_dash;
		py1 = py2 - ddy_dash;

		// Handle very short lines
		if ((ddx_dash > 0) && (px1 < x1))
			px1 = x1;
		else if ((ddx_dash < 0) && (px1 > x1))
			px1 = x1;
		if ((ddy_dash > 0) && (py1 < y1))
			py1 = y1;
		else if ((ddy_dash < 0) && (py1 > y1))
			py1 = y1;

		g.drawLine((int )px1, (int )py1, (int )px2, (int )py2);
		}
					// Assumes y is already adjusted to upside down coordinates
					// Assumes lwidth > 1, otherwise this is invisible as JDK does 
					// not draw 1 pixel wide polygons
	public	void			drawWideLine(int x1, int y1, int x2, int y2, int lwidth)
		{
		// Don't let the rect or polygon draw it's border lines wide...
		double origLineWidth = curLineWidth;
		curLineWidth = 0;
		Color origCurBGColor = curBGColor;

		if (x1 == x2)
			{
			if (y2 > y1)
				drawRect(x1 - lwidth/2, y1, lwidth - 1, y2 - y1);
			else
				drawRect(x1 - lwidth/2, y2, lwidth - 1, y1 - y2);
			}
		else if (y1 == y2)
			{
			if (x2 > x1)
				drawRect(x1, y1 - lwidth/2, x2 - x1, lwidth - 1);
			else
				drawRect(x2, y1 - lwidth/2, x1 - x2, lwidth - 1);
			}
		else
			{
			int dx = x2 - x1;
			int dy = y2 - y1;
			double len = Math.sqrt(dx*dx + dy*dy);
			double tmp = lwidth/(2 * len);
			double ddy = -dx*tmp;
			double ddx = dy*tmp;

			if (ddx > 0)
				ddx += 0.5;
			else
				ddx -= 0.5;

			if (ddy > 0)
				ddy += 0.5;
			else
				ddy -= 0.5;

			int vDist = (int )ddy;
			int hDist = (int )ddx;

			fiveXPoints[0] = x1 + hDist;
			fiveYPoints[0] = y1 + vDist;
			fiveXPoints[1] = x1 - hDist;
			fiveYPoints[1] = y1 - vDist;
			fiveXPoints[2] = x2 - hDist;
			fiveYPoints[2] = y2 - vDist;
			fiveXPoints[3] = x2 + hDist;
			fiveYPoints[3] = y2 + vDist;
			fiveXPoints[4] = fiveXPoints[0];
			fiveYPoints[4] = fiveYPoints[0];
			
			drawPolygon(fiveXPoints, fiveYPoints, 5);
			}
		curLineWidth = origLineWidth;
		}
	public	void			drawPoint(MiPoint point)
		{
		if (curColor == null)
			return;
		transforms.wtod(point, dpt0);
		g.setColor(curColor);
		//g.drawLine(dpt0.x, ymax - dpt0.y, dpt0.x + 1, ymax - dpt0.y);
		g.drawLine(dpt0.x, ymax - dpt0.y, dpt0.x, ymax - dpt0.y);
		}
	public	void			drawPoints(FastVector points)
		{
		if (curColor == null)
			return;
		g.setColor(curColor);
		for (int i = 0; i < points.size(); ++i)
			{
			transforms.wtod((MiPoint )points.elementAt(i), dpt0);
			g.drawLine(dpt0.x, ymax - dpt0.y, dpt0.x + 1, ymax - dpt0.y);
			}
		}
	public	void			drawPoints(MiCoord[] xPoints, MiCoord[] yPoints)
		{
		if (curColor == null)
			return;
		g.setColor(curColor);
		for (int i = 0; i < xPoints.length; ++i)
			{
			wpt0.x = xPoints[i];
			wpt0.y = yPoints[i];
			transforms.wtod(wpt0, dpt0);
			g.drawLine(dpt0.x, ymax - dpt0.y, dpt0.x + 1, ymax - dpt0.y);
			}
		}
	public	void			drawPoints(MiPoint points[])
		{
		if (curColor == null)
			return;
		g.setColor(curColor);
		for (int i = 0; i < points.length; ++i)
			{
			transforms.wtod(points[i], dpt0);
			g.drawLine(dpt0.x, ymax - dpt0.y, dpt0.x + 1, ymax - dpt0.y);
			}
		}
	public	Image			makeImageFromArea(MiBounds bounds)
		{
		Image srcImage;
		if (doubleBuffering)
			{
			srcImage = offScreenBuffer;
			}
		else
			{
			transforms.wtod(bounds, dBnds);
			srcImage = canvas.createImage(
				(int )(dBnds.getWidth() + 0.5), (int )(dBnds.getHeight() + 0.5));
			}
		return(makeImageFromArea(srcImage, bounds));
		}
	public	Image			makeImageFromArea(Image srcImage, MiBounds bounds)
		{
		wpt0.x = bounds.xmin;
		wpt0.y = bounds.ymin;
		transforms.wtod(wpt0, dpt0);

		wpt1.x = bounds.xmax;
		wpt1.y = bounds.ymax;
		transforms.wtod(wpt1, dpt1);

		transforms.getTotalDeviceTranslation(wVector);
		Image image = MiImageManager.getImageSubArea(
			srcImage, 
			dpt0.x + ((int )wVector.x), 
			srcImage.getHeight(null) + ((int )wVector.y) - dpt1.y, 
			dpt1.x - dpt0.x, dpt1.y - dpt0.y);

		return(image);
		}
	public	void			moveImageArea(int xmin, int ymin, int xmax, int ymax, int dx, int dy)
		{
//System.out.println("xmin = " + xmin + ", ymin = " + ymin + ", xmax = " + xmax + ", ymax = " + ymax + ", dx = " + dx + ", dy = " + dy);
		g.copyArea(xmin, this.ymax - ymax, xmax - xmin, ymax - ymin, dx, -dy);
		}
	public	void			moveImageArea(MiBounds bounds, MiDistance dx, MiDistance dy)
		{
		wpt0.x = bounds.xmin;
                wpt0.y = bounds.ymin;
                wVector.x = dx;
                wVector.y = dy;
                transforms.wtod(wpt0, wVector, dVector);
                MiDeviceDistance devX = dVector.x;
                MiDeviceDistance devY = dVector.y;
 
                wpt0.x = bounds.xmin;
                wpt0.y = bounds.ymin;
                transforms.wtod(wpt0, dpt0);
 
                wpt1.x = bounds.xmax;
                wpt1.y = bounds.ymax;
                transforms.wtod(wpt1, dpt1);
 
		if ((devX != 0) || (devY != 0))
                	g.copyArea(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y, devX, -devY);
		}
	public	void			drawImage(Image image, MiBounds bounds)
		{
		drawImage(image, bounds, null);
		}
	public	void			drawImage(Image image, MiBounds bounds, ImageObserver observer)
		{
		if (curColor == null)
			return;

		wpt0.x = bounds.xmin;
		wpt0.y = bounds.ymin;
		transforms.wtod(wpt0, dpt0);

		wpt1.x = bounds.xmax;
		wpt1.y = bounds.ymax;
		transforms.wtod(wpt1, dpt1);

		int x = dpt0.x;
		int y = ymax - dpt1.y;
		int width = dpt1.x - dpt0.x;
		int height = dpt1.y - dpt0.y;

		// -----------------------------------------------------------------
		// AWT bug: single buffered mode clip bounds bug: try MiExample2 w/o 
		// this 'fix'. Unable to determine how to reproduce in any suscinct way.
		// 
		// Draw this rect to flush/reset/fix AWT image clip bounds that is 
		// often not correct in single buffered mode. JDK 1.3 Windows2000
		// -----------------------------------------------------------------
//		if (drawImageFlushingRect)
			{
			g.drawRect(x + width/2, y + height/2, 1, 1);
			}

		g.drawImage(image, x, y, width, height, observer);

		if ((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK)
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			++dpt0.y;
			--dpt1.x;
			((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
				.drawRect(g, attributes, 
					dpt0.x, ymax - dpt1.y, 
					dpt1.x - dpt0.x + 1, dpt1.y - dpt0.y + 1);
			}
		if (((curColor != curBGColor) && (curColor != null))
			&& (curBorderLook == Mi_FLAT_BORDER_LOOK))
			{
			g.setColor(curColor);
			g.drawRect(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x + 1, dpt1.y - dpt0.y + 1);
			}
		}
	public	void			drawText(String text, MiPoint lowerLeftBaseline)
		{
		g.setColor(curColor);
		g.setFont(curFont);
		transforms.wtod(lowerLeftBaseline, dpt0);
		g.drawString(text, dpt0.x, ymax - dpt0.y);
		}
	private static final int 	OFF_BY_ONE	= 0;
	public	MiFont			findFontForScale(MiFont font, MiFont lastDrawnFont)
		{
		MiScale scale = transforms.getScale(tmpScale);
		if ((lastDrawnFont != null) && (lastDrawnFont.isSubstitutingForFont(font, scale)))
			return(lastDrawnFont);

		int fontHeight = font.getMaxCharHeight();
		int fontWidth = font.getMaxCharWidth();

		// If device is smaller than world, be conservative about 
		// the font size so we dont leave pixels around
		if ((scale.x < 1.0) || (scale.y < 1.0))
			{
			--fontHeight;
			--fontWidth;
			}

		int height = (int )(fontHeight/scale.y + 0.5);
		int width = (int )(fontWidth/scale.x + 0.5);

		if ((height < MIN_DRAWN_FONT_HEIGHT) || (width < MIN_DRAWN_FONT_WIDTH))
			{
			return(null);
			}
		if ((width != fontWidth) || (height != fontHeight))
			{
			MiFont newFont = MiFontManager.resizeFont(font, width, height);
			if (newFont != null)
				{
				newFont.setSubstitutingForFont(font, scale);
				}
			return(newFont);
			}
		return(font);
		}
	public	void			drawText(String text, MiBounds bounds, MiFont font, 
						int selectionStart, int selectionEnd, 
						Color selectionColor, Color selectionBGColor)
		{
		if ((curColor == null) || (text == null))
			return;

		if (curBGColor != Mi_TRANSPARENT_COLOR)
			{
			int blook = curBorderLook;
			curBorderLook = Mi_NONE;
			drawFillRect(bounds);
			curBorderLook = blook;
			}

		bounds.getLLCorner(wpt0);
		transforms.wtod(wpt0, dpt0);
		g.setColor(curColor);
		if (font == null)
			{
			// Greek with line
			bounds.getURCorner(wpt1);
			transforms.wtod(wpt1, dpt1);
			g.drawLine(
				dpt0.x, ymax - (dpt1.y + dpt0.y)/2, 
				dpt1.x, ymax - (dpt1.y + dpt0.y)/2);
			return;
			}
		g.setFont(font);
		int descent = font.getMaximumDescent();
		int xOffset = 0;
		char buffer[] = new char[1];
		int charHeight = font.getMaxCharHeight();
		for (int i = 0; i < text.length(); ++i)
			{
			buffer[0] = text.charAt(i);
			int charWidth = font.getWidth(buffer[0]);
			if ((i >= selectionStart) && (i <= selectionEnd - 1))
				{
				g.setColor(selectionBGColor);
				g.fillRect(
					dpt0.x + xOffset, 
					ymax - dpt0.y - charHeight, 
					charWidth, 
					charHeight);
				g.setColor(selectionColor);
				}
			else if (i == selectionEnd)
				g.setColor(curColor);
			if ((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK)
				&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
				{
				String chStr = new String(buffer);
				((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
					.drawText(g, attributes, chStr, 
					dpt0.x + xOffset, ymax - dpt0.y - descent);
				}
			else
				{
				g.drawChars(buffer, 0, 1, dpt0.x + xOffset, ymax - dpt0.y - descent);
				}
			xOffset += charWidth;
			}
		g.setColor(curColor);
		}
	public	void			drawText(String text, MiBounds bounds, MiFont font)
		{
		if (curBGColor != Mi_TRANSPARENT_COLOR)
			{
			int blook = curBorderLook;
			curBorderLook = Mi_NONE;
			drawFillRect(bounds);
			curBorderLook = blook;
			}

		if ((curColor == null) || (text == null))
			return;

		bounds.getLLCorner(wpt0);
		transforms.wtod(wpt0, dpt0);
		g.setColor(curColor);
		if (font == null)
			{
			// Greek with line
			bounds.getURCorner(wpt1);
			transforms.wtod(wpt1, dpt1);
			g.drawLine(
				dpt0.x, ymax - (dpt1.y + dpt0.y)/2, 
				dpt1.x, ymax - (dpt1.y + dpt0.y)/2);
			return;
			}
		g.setFont(font);
		int descent = font.getMaximumDescent();
		if ((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK)
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
				.drawText(g, attributes, text, dpt0.x, ymax - dpt0.y - descent);
			}
		else
			{
			g.drawString(text, dpt0.x, ymax - dpt0.y - descent);
			}
		}
	public	void			drawRect(MiBounds bounds)
		{
		transforms.wtod(bounds, dBnds);
		drawRect(dBnds.xmin, ymax - dBnds.ymax, dBnds.xmax - dBnds.xmin, dBnds.ymax - dBnds.ymin);
		}
	public	void			drawRect(int x, int y, int width, int height)
		{
		if ((curBGRenderer == null) || curBGRenderer.drawRect(g, attributes, x, y, width, height))
			{
			if (curBGImage != null)
				{
				g.drawImage(curBGImage, x, y, width, height, null);
				}
			else if (curBGColor != null)
				{
				g.setColor(curBGColor);
				g.fillRect(x, y, width + 1, height + 1);
				}
			if (curBGTile != null)
				{
				drawBGTile(curBGTile, x, y, width, height);
				}
			}
		if ((((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK))
			|| (attributes.booleanAttributes[Mi_HAS_BORDER_HILITE - Mi_START_BOOLEAN_INDEX]))
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
				.drawRect(g, attributes, x, y, width, height);
			}
		if (((curColor != curBGColor) && (curColor != null))
			&& (curBorderLook == Mi_FLAT_BORDER_LOOK))
			{
			g.setColor(curColor);
			int lwidth = 0;
			if (curLineWidth !=0)
				{
				lwidth = getLineWidth();
				}

			if (curLineStyle != Mi_NONE)
				{
				y = ymax - y;
				height = -height;
				drawLine(x, y, x + width, y, lwidth);
				drawLine(x + width, y, x + width, y + height, lwidth);
				drawLine(x + width, y + height, x, y + height, lwidth);
				drawLine(x, y + height, x, y, lwidth);
				}
			else
				{
				g.drawRect(x, y, width, height);
				if (curLineWidth !=0)
					{
					while (lwidth-- > 0)
						{
						g.drawRect(x + lwidth, y + lwidth, width - 2*lwidth, height - 2*lwidth);
						}
					}
				}
			}
		}
	public	void			drawFillRect(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		wBnds.xmin = xmin;
		wBnds.ymin = ymin;
		wBnds.xmax = xmax;
		wBnds.ymax = ymax;
		drawFillRect(wBnds);
		}
	public	void			drawFillRect(MiBounds bounds)
		{
		transforms.wtod(bounds, dBnds);

		int xmin = dBnds.xmin;
		int ymin = ymax - dBnds.ymax;
		int width = dBnds.xmax - dBnds.xmin;
		int height = dBnds.ymax - dBnds.ymin;

		if ((curBGRenderer == null)
			|| curBGRenderer.drawRect(g, attributes, xmin, ymin, width, height))
			{
			if (curBGImage != null)
				{
				// Fix for bug in AWT, draw a point to flush clip bounds
				g.drawRect(xmin + width/2, ymin + height/2, 1, 1);
				g.drawImage(curBGImage, xmin, ymin, width, height, null);
				}
			else if (curBGTile != null)
				{
				if (curBGColor != null)
					{
					g.setColor(curBGColor);
					g.fillRect(xmin, ymin, width + 1, height + 1);
					}
				drawBGTile(curBGTile, xmin, ymin, width, height);
				}
			else if (curBGColor != null)
				{
				g.setColor(curBGColor);
				g.fillRect(xmin, ymin, width + 1, height + 1);
				}
			}
		}
	protected	void		drawBGTile(Image tile, int x, int y, int width, int height)
		{
		int tileWidth = tile.getWidth(null);
		int tileHeight = tile.getHeight(null);
		int numAcross = (width + tileWidth)/(tileWidth + 1);
		int numDown = (height + tileHeight)/(tileHeight + 1);
		int topY = y;

		Rectangle clip = pushClipBounds(x, y, width, height);

//System.out.println("numAcross = " + numAcross);
//System.out.println("numDown = " + numDown);
//System.out.println("g.getClip() = " + g.getClip());
//System.out.println("OLD.getClip() = " + clip);

		for (int i = 0; i < numAcross; ++i)
			{
			y = topY;
			for (int j = 0; j < numDown; ++j)
				{
//System.out.println("x = " + x);
//System.out.println("y = " + y);
//System.out.println("tileWidth = " + tileWidth);
//System.out.println("tileHeight = " + tileHeight);
				g.drawImage(tile, x, y, null);
				y += tileHeight + 1;
				}
			x += tileWidth + 1;
			}

		popClipBounds(clip);
		}
	public	void			drawRect(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		wBnds.xmin = xmin;
		wBnds.ymin = ymin;
		wBnds.xmax = xmax;
		wBnds.ymax = ymax;
		drawRect(wBnds);
		}
	public	void			drawCircle(MiBounds bounds)
		{
		if (curColor == null)
			return;
		bounds.getLLCorner(wpt0);
		bounds.getURCorner(wpt1);
		transforms.wtod(wpt0, dpt0);
		transforms.wtod(wpt1, dpt1);

		if (curBGColor != null)
			{
			g.setColor(curBGColor);
			g.fillOval(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y);
			}
		if ((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK)
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
				.drawOval(g, attributes, dpt0.x, ymax - dpt1.y, 
					dpt1.x - dpt0.x, dpt1.y - dpt0.y);
			}
		if (((curColor != curBGColor) && (curColor != null))
			&& (curBorderLook == Mi_FLAT_BORDER_LOOK))
			{
			g.setColor(curColor);
			g.drawOval(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y);
			if (curLineWidth !=0)
				{
				int x = dpt0.x;
				int y = ymax - dpt1.y;
				int width = dpt1.x - dpt0.x;
				int height = dpt1.y - dpt0.y;
				int lwidth = getLineWidth();
				while (lwidth-- > 0)
					g.drawOval(x + lwidth, y + lwidth, width - 2*lwidth, height - 2*lwidth);
				}
			}
		}
	public	void			drawEllipse(MiBounds bounds)
		{
		bounds.getLLCorner(wpt0);
		bounds.getURCorner(wpt1);
		transforms.wtod(wpt0, dpt0);
		transforms.wtod(wpt1, dpt1);

		if (curBGColor != null)
			{
			g.setColor(curBGColor);
			g.fillOval(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y);
			}
		if ((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK)
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
				.drawOval(g, attributes, dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y);
			}
		if (((curColor != curBGColor) && (curColor != null))
			&& (curBorderLook == Mi_FLAT_BORDER_LOOK))
			{
			g.setColor(curColor);
			g.drawOval(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y);
			if (curLineWidth !=0)
				{
				int x = dpt0.x;
				int y = ymax - dpt1.y;
				int width = dpt1.x - dpt0.x;
				int height = dpt1.y - dpt0.y;
				int lwidth = getLineWidth();
				while (lwidth-- > 0)
					g.drawOval(x + lwidth, y + lwidth, width - 2*lwidth, height - 2*lwidth);
				}
			}
		}
	public	void			drawPolygon(FastVector points)
		{
		boolean closed = true;
		int numPts = points.size();
		if (numPts != 0)
			{
			int index = (closed ? 1 : 0);
			int xpts[] = new int[numPts + index];
			int ypts[] = new int[numPts + index];
			for (int i = 0; i < numPts; ++i)
				{
				transforms.wtod((MiPoint )points.elementAt(i), dpt0);
				xpts[i] = dpt0.x;
				ypts[i] = ymax - dpt0.y;
				}
			if (closed)
				{
				xpts[numPts] = xpts[0];
				ypts[numPts] = ypts[0];
				++numPts;
				}
			drawPolygon(xpts, ypts, numPts);
			}
		}
	public		void		drawPolygon(MiPoint points[], boolean closed)
		{
		int numPts = points.length;
		if (numPts != 0)
			{
			int index = (closed ? 1 : 0);
			if (numPts + index > tmpXPoints.length)
				{
				tmpXPoints = new int[numPts + index];
				tmpYPoints = new int[numPts + index];
				}
			int xpts[] = tmpXPoints;
			int ypts[] = tmpYPoints;
			for (int i = 0; i < numPts; ++i)
				{
				transforms.wtod(points[i], dpt0);
				xpts[i] = dpt0.x;
				ypts[i] = ymax - dpt0.y;
				}
			if (closed)
				{
				xpts[numPts] = xpts[0];
				ypts[numPts] = ypts[0];
				++numPts;
				}
			drawPolygon(xpts, ypts, numPts);
			}
		}

	public		void		drawPolygon(MiCoord[] xPoints, MiCoord[] yPoints, boolean closed)
		{
		int numPts = xPoints.length;
		if (numPts != 0)
			{
			int index = (closed ? 1 : 0);
			if (numPts + index > tmpXPoints.length)
				{
				tmpXPoints = new int[numPts + index];
				tmpYPoints = new int[numPts + index];
				}
			int xpts[] = tmpXPoints;
			int ypts[] = tmpYPoints;
			for (int i = 0; i < numPts; ++i)
				{
				wpt0.x = xPoints[i];
				wpt0.y = yPoints[i];

				transforms.wtod(wpt0, dpt0);
				xpts[i] = dpt0.x;
				ypts[i] = ymax - dpt0.y;
				}
			if (closed)
				{
				xpts[numPts] = xpts[0];
				ypts[numPts] = ypts[0];
				++numPts;
				}
			drawPolygon(xpts, ypts, numPts);
			int lwidth = getLineWidth();
			if (lwidth > 1)
				{
				int borderLook = curBorderLook;
				Color bgColor = curBGColor;
				curBorderLook = Mi_NONE;
				curBGColor = curColor;
				for (int i = 1; i < numPts; ++i)
					{
					drawWideLine(xpts[i - 1], ypts[i - 1], xpts[i], ypts[i], lwidth);
					}
				curBorderLook = borderLook;
				curBGColor = bgColor;
				}
			}
		}

	public	void			drawPolygon(int[] xpts, int[] ypts, int numPts)
		{
		if (curBGColor != null)
			{
			g.setColor(curBGColor);
			g.fillPolygon(xpts, ypts, numPts);
			}
		if ((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK)
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
				.drawPolygon(g, attributes, xpts, ypts, numPts);
			}
		if (((curColor != curBGColor) && (curColor != null))
			&& (curBorderLook == Mi_FLAT_BORDER_LOOK))
			{
			g.setColor(curColor);
			g.drawPolyline(xpts, ypts, numPts);
			}
		}
	public	void			drawCircularArc(
						MiBounds bounds, 
						double startAngle,
						double angleSwept)
		{
		bounds.getLLCorner(wpt0);
		bounds.getURCorner(wpt1);
		transforms.wtod(wpt0, dpt0);
		transforms.wtod(wpt1, dpt1);

		// We do not implement thick arcs at this time...
		if ((curBGColor != null) && (curLineWidth == 0))
			{
			g.setColor(curBGColor);
			g.fillArc(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y, 
				(int )startAngle, (int )angleSwept);
			}

		// We do not implement thick arcs at this time...
		//if ((((curColor != curBGColor) || (curLineWidth == 0)) && (curColor != null))
			//&& (curBorderLook == Mi_FLAT_BORDER_LOOK))

		if ((curColor != null) && (curBorderLook != Mi_NONE))
			{
			g.setColor(curColor);
			g.drawArc(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y, 
				(int )startAngle, (int )angleSwept);
			}
		}
	public	void			drawEllipticalArc(MiBounds bounds, 
						double startAngle,
						double angleSwept)
		{
		bounds.getLLCorner(wpt0);
		bounds.getURCorner(wpt1);
		transforms.wtod(wpt0, dpt0);
		transforms.wtod(wpt1, dpt1);

		if ((curBGColor != null) && (curLineWidth == 0))
			{
			g.setColor(curBGColor);
			g.fillArc(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y, 
				(int )startAngle, (int )angleSwept);
			}
		if ((((curColor != curBGColor) || (curLineWidth == 0)) && (curColor != null))
			//&& (curBorderLook == Mi_FLAT_BORDER_LOOK))
			&& (curBorderLook != Mi_NONE))
			{
			g.setColor(curColor);
			g.drawArc(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y, 
				(int )startAngle, (int )angleSwept);
			}
		}
	public	void			drawRoundRect(
						MiBounds bounds,
						MiDistance arcWidth, 
						MiDistance arcHeight)
		{
		bounds.getLLCorner(wpt0);
		bounds.getURCorner(wpt1);
		transforms.wtod(wpt0, dpt0);
		transforms.wtod(wpt1, dpt1);

		wBnds.setBounds(0,0, arcWidth, arcHeight);
		transforms.wtod(wBnds, wBnds);
		int dArcWidth = (int )(wBnds.getWidth() + 0.5);
		int dArcHeight = (int )(wBnds.getHeight() + 0.5);


		if (curBGColor != null)
			{
			g.setColor(curBGColor);
			g.fillRoundRect(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y, 
				dArcWidth, dArcHeight);
			}
		if ((curBorderLook != Mi_NONE) && (curBorderLook != Mi_FLAT_BORDER_LOOK)
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			((MiiDeviceRenderer )attributes.objectAttributes[Mi_BORDER_RENDERER])
				.drawRoundRect(g, attributes, dpt0.x, ymax - dpt1.y, 
					dpt1.x - dpt0.x, dpt1.y - dpt0.y, dArcWidth, dArcHeight);
			}
		if (((curColor != curBGColor) && (curColor != null)) && (curBorderLook == Mi_FLAT_BORDER_LOOK))
			{
			g.setColor(curColor);
			g.drawRoundRect(dpt0.x, ymax - dpt1.y, dpt1.x - dpt0.x, dpt1.y - dpt0.y,
				dArcWidth, dArcHeight);
			if (curLineWidth !=0)
				{
				int x = dpt0.x;
				int y = ymax - dpt1.y;
				int width = dpt1.x - dpt0.x;
				int height = dpt1.y - dpt0.y;
				int lwidth = getLineWidth();
				while (lwidth-- > 0)
					{
					g.drawRoundRect(x + lwidth, y + lwidth, 
						width - 2*lwidth, height - 2*lwidth, 
						dArcWidth, dArcHeight);
					}
				}
			}
		}
/** Never gets called in 1.2.2
	public 	boolean 		imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
		{
System.out.println(":Image: " + img + " update: infoflags = " + infoflags + " w,h = " + width + ", " + height);
		if ((infoflags & SOMEBITS) != 0)
			{
System.out.println("SOMEBITS");
			g.drawImage(img, x, y, width, height, null);
			return(true);
			}
		return(true);
		}
***/
	}
class MiDoubleBuffers extends CacheVector
	{
	public 		Object		makeAnElement()
		{
		return(new MiDoubleBufferSpec());
		}
	}
class MiDoubleBufferSpec
	{
	MiBounds		clipBounds	= new MiBounds();
	Graphics		g;
	Graphics		screenRenderer;
	Image			offScreenBuffer;
	boolean			doubleBuffering;
	MiVector		translation	= new MiVector();
	Image			image;
	int			ymax;
	}

