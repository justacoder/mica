
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

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit; 


/**----------------------------------------------------------------------------------------------
 * This class serves as the interface (driver/wrapper) to the modern (post 1.0.2 AWT) 
 * graphics routines, specifically the AWT Canvas widget. 
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiAWTCanvas extends Canvas implements MiiCanvas
	{
	private		MiCanvas	miCanvas;
	private		boolean		initialized;
	private		int		lastX;
	private		int		lastY;

					/**------------------------------------------------------
	 				 * Constructs a new MiAWTCanvas. 
					 *------------------------------------------------------*/
	public				MiAWTCanvas()
		{
		}

	public		void		setCanvas(MiCanvas canvas)
		{
		this.miCanvas = canvas;
		new MiAWTEventAdapter(canvas, this);
		// JDK 1.1.6 still has this bug
		if (MiSystem.getJDKVersion() >= 1.2)
			canvas.getRenderer().setHasAWTClipRectBug(false);
		}
	public		boolean		isFocusTraversable()
		{
		return(true);
		}
					/**------------------------------------------------------
	 				 * Sets the appearance of the mouse cursor within the
					 * bounds of this canvas.
					 * @param appearance	the cursor appearance
					 *------------------------------------------------------*/
	public		void		setMouseAppearance(int appearance)
		{
		setCursor(Cursor.getPredefinedCursor(appearance));

/** does not work for dialog boxes whose containing frame is the parent window....
		Frame frame = MiUtility.getFrame(this);
		if (frame != null)
			{
			frame.setCursor(Cursor.getPredefinedCursor(appearance));
			}
/** Doesn't seem to work after a few times... JDK 1.3 Windows 2000
		else
			{
			setCursor(Cursor.getPredefinedCursor(appearance));
			}
**/
		Toolkit.getDefaultToolkit().sync();
		}
					/**------------------------------------------------------
	 				 * Returns the appearance of the mouse cursor in this 
					 * canvas.
					 * @return		the animation manager
					 * @see 		MiAnimationManager
					 *------------------------------------------------------*/
	public		int		getMouseAppearance()
		{
		return(getCursor().getType());
		// no frame if this has not been added to a frame yet... 
		// return(MiUtility.getFrame(this).getCursor().getType());
		}
					/**------------------------------------------------------
	 				 * Sets the bounds of the canvas, in pixels.
					 * @param deviceBounds	the bounds of this canvas
					 *------------------------------------------------------*/
	public 		void 		setCanvasBounds(MiBounds deviceBounds)
		{
		Insets insets = getContainerInsets();
		setBounds(
			insets.left, // + (int )deviceBounds.xmin, 
			insets.top, // + (int )deviceBounds.ymin,
			(int )(deviceBounds.xmax - deviceBounds.xmin),
			(int )(deviceBounds.ymax - deviceBounds.ymin), false);
		}
					/**------------------------------------------------------
	 				 * Returns the preferred size of this canvas. This 
					 * routine is in support of the Component API.
					 * @return		the preferred size
					 *------------------------------------------------------*/
	public		Dimension	getPreferredSize()
		{
		return(miCanvas.getPreferredSize());
		}

					/**------------------------------------------------------
	 				 * Returns the minimum size of this canvas. This 
					 * routine is in support of the Component API.
					 * @return		the minimum size
					 *------------------------------------------------------*/
	public		Dimension	getMinimumSize()
		{
		return(miCanvas.getMinimumSize());
		}
					/**------------------------------------------------------
	 				 * Causes the canvas to be redrawn as per the
					 * awt.Component API.
					 *------------------------------------------------------*/
	public		 void 		requestRepaint() 
		{
		super.repaint();
		}
					/**------------------------------------------------------
	 				 * Causes the canvas to be redrawn as per the
					 * awt.Component API.
					 * @param g		the awt.graphics renderer
					 *------------------------------------------------------*/
	public		 void 		update(Graphics g) 
		{
		miCanvas.update(g);
		}
					/**------------------------------------------------------
	 				 * Causes the canvas to be redrawn as per the
					 * awt.Component API.
					 * @param g		the awt.graphics renderer
					 *------------------------------------------------------*/
	public 		void 		paint(Graphics g)
		{
		if (!miCanvas.paint(g))
			{
			Rectangle clip = g.getClipBounds();
			super.repaint(clip.x, clip.y, clip.width, clip.height);
			}
		}
					/**------------------------------------------------------
	 				 * Causes the canvas to be reshaped as per the
					 * awt.Component API.
					 * @param x		the x coordinate
					 * @param y		the y coordinate
					 * @param width		the width
					 * @param height	the height
					 *------------------------------------------------------*/
	public synchronized void 	setBounds(int x, int y, int width, int height)
		{
		setBounds(x, y, width, height, true);
		}
	private		 void 		setBounds(int x, int y, int width, int height, boolean awtThread)
		{
		Rectangle bounds = getBounds();
		if (!miCanvas.hasBoundsChanged(x, y, width, height))
			{
			return;
			}
		super.setBounds(x, y, width, height);
		miCanvas.boundsHasChanged(getBounds(), awtThread);

		// MSWindows seems to need prompting to repaint the *whole* window after a resize.
		if (MiSystem.isMSWindows())
			{
			requestRepaint();
			}
		}

	public		Rectangle	getCanvasBounds()
		{
		return(getBounds());
		}

	public		Insets		getContainerInsets()
		{
		Component c = this;
		while ((c != null) && (!(c instanceof Container)))
			c = c.getParent();
		return(c == null ? new Insets(0,0,0,0) : ((Container )c).getInsets());
		}
	}

