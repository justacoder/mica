
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

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiCanvas
	{
	void		setCanvas(MiCanvas canvas);

			/**------------------------------------------------------
	 		 * Sets the appearance of the mouse cursor within the
			 * bounds of this canvas.
			 * @param appearance	the cursor appearance
			 *------------------------------------------------------*/
	void		setMouseAppearance(int appearance);

			/**------------------------------------------------------
			 * Returns the appearance of the mouse cursor in this 
			 * canvas.
			 * @return		the animation manager
			 * @see 		MiAnimationManager
			 *------------------------------------------------------*/
	int		getMouseAppearance();

			/**------------------------------------------------------
			 * Sets the bounds of the canvas, in pixels.
			 * @param deviceBounds	the bounds of this canvas
			 *------------------------------------------------------*/
	void 		setCanvasBounds(MiBounds deviceBounds);

			/**------------------------------------------------------
	 		 * Get the bounds of the canvas in pixels.
			 * @return		the bounds of this canvas
			 *------------------------------------------------------*/
	Rectangle	getCanvasBounds();

			/**------------------------------------------------------
	 		 * Returns the preferred size of this canvas. This 
			 * routine is in support of the Component API.
			 * @return		the preferred size
			 *------------------------------------------------------*/
	Dimension	getPreferredSize();

			/**------------------------------------------------------
			 * Returns the minimum size of this canvas. This 
			 * routine is in support of the Component API.
			 * @return		the minimum size
			 *------------------------------------------------------*/
	Dimension	getMinimumSize();

			/**------------------------------------------------------
			 * Process the awt.event as per the awt.Component API.
			 * @param evt		the awt.Event event
			 *------------------------------------------------------*/
	boolean 	handleEvent(Event evt);

			/**------------------------------------------------------
			 * Causes the canvas to be redrawn as per the
			 * awt.Component API.
			 * @param g		the awt.graphics renderer
			 *------------------------------------------------------*/
	void 		update(Graphics g);

			/**------------------------------------------------------
	 		 * Causes the canvas to be redrawn as per the
			 * awt.Component API.
			 * @param g		the awt.graphics renderer
			 *------------------------------------------------------*/
	void 		paint(Graphics g);

	void 		requestRepaint();

			/**------------------------------------------------------
			 * Causes the canvas to be reshaped as per the
			 * awt.Component API.
			 * @param x		the x coordinate
			 * @param y		the y coordinate
			 * @param width		the width
			 * @param height	the height
			 *------------------------------------------------------*/
	void 		setBounds(int x, int y, int width, int height);

	Insets		getContainerInsets();

	Graphics	getGraphics();

	Image		createImage(int width, int height);
	}


