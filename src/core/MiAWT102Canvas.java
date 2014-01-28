
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
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit; 


/**----------------------------------------------------------------------------------------------
 * This class serves as the interface (driver/wrapper) to the old 1.0.2 AWT graphics routines,
 * specifically the AWT Canvas widget. 
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiAWT102Canvas extends Canvas implements MiiCanvas
	{
	private		MiCanvas	miCanvas;
	private		boolean		initialized;
	private		int		lastX;
	private		int		lastY;

					/**------------------------------------------------------
	 				 * Constructs a new MiAWT102Canvas. 
					 *------------------------------------------------------*/
	public				MiAWT102Canvas()
		{
		}

	public		void		setCanvas(MiCanvas canvas)
		{
		this.miCanvas = canvas;
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
		MiUtility.getFrame(this).setCursor(appearance);
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
		return(MiUtility.getFrame(this).getCursorType());
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
					 * @param g		the awt.graphics renderer
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
		miCanvas.paint(g);
		}
					/**------------------------------------------------------
	 				 * Process the awt.event as per the awt.Component API.
					 * @param evt		the awt.Event wveent
					 *------------------------------------------------------*/
	public 		boolean 	handleEvent(Event evt) 
		{
		miCanvas.handleEvent(evt);
		return(true);
		}
					/**------------------------------------------------------
	 				 * Causes the canvas to be reshaped as per the
					 * awt.Component API.
					 * @param x		the x coordinate
					 * @param y		the y coordinate
					 * @param width		the width
					 * @param height	the height
					 *------------------------------------------------------*/
	public 		void 		setBounds(int x, int y, int width, int height)
		{
		setBounds(x, y, width, height, true);
		}
	public 		void 		reshape(int x, int y, int width, int height)
		{
		setBounds(x, y, width, height, true);
		}
	private		void 		setBounds(int x, int y, int width, int height, boolean awtThread)
		{
		if (!miCanvas.hasBoundsChanged(x, y, width, height))
			{
			return;
			}
		super.reshape(x, y, width, height);
		miCanvas.boundsHasChanged(bounds(), awtThread);
		}
	public		Rectangle	getCanvasBounds()
		{
		return(bounds());
		}
/***
	public		void		setBounds(Rectangle rect)
		{
		setBounds(rect.x, rect.y, rect.width, rect.height, false);
		}
***/

	public		Insets		getContainerInsets()
		{
		Component c = this;
		while ((c != null) && (!(c instanceof Container)))
			c = c.getParent();
		return(c == null ? new Insets(0,0,0,0) : ((Container )c).insets());
		}
	}

