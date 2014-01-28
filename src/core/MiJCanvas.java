
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

import com.swfm.mica.util.Utility; 
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
import java.awt.Point;
import java.awt.Toolkit; 

//import java.awt.swing.JComponent;
//import com.sun.java.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JComponent;

import javax.swing.*;

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiJCanvas extends JPanel implements MiiCanvas
	{
	public static final String	Mi_NUM_SWING_EXTRA_PAINTS_PROPERTY_NAME	= "Mi_NUM_SWING_EXTRA_PAINTS";
	private		MiCanvas	miCanvas;
					// KLUDGE: Must specify some kind of damaged rectangle to request repaints
					// when we manage the actual list of damaged rects internally
	private		Rectangle	dummyInvalidAreaRectangle	= new Rectangle(0,0,1,1);
	private		Rectangle	bounds				= new Rectangle();

					/**------------------------------------------------------
	 				 * Constructs a new MiJCanvas. 
					 *------------------------------------------------------*/
	public				MiJCanvas()
		{
		setVisible(true);
		}
	public		boolean		isOpaque()
		{
		return(true);
		}
	public		boolean		isManagingFocus()
		{
		return(true);
		}
	public		boolean		isFocusTraversable()
		{
		return(true);
		}
	public		void		paintComponent(Graphics g)
		{
		Rectangle clip = g.getClipBounds();
//System.out.println("\n");
//System.out.println("clip = " + clip);
//System.out.println("dummyInvalidAreaRectangle = " + dummyInvalidAreaRectangle);
//System.out.println("RepaintManager.currentManager(this).getDirtyRegion(this) DIRTY =  " + RepaintManager.currentManager(this).getDirtyRegion(this));
//System.out.println("getBounds() = " + getBounds());


		if (!clip.equals(dummyInvalidAreaRectangle))
			{
			// ---------------------------------------------------------------------------
			// EXPOSE EVENT 
			// Swing blanks out background sometimes at left of canvas during an expose of
			// the right side, so we repaint whole canvas
			// ---------------------------------------------------------------------------
			g.setClip(0, 0, dummyInvalidAreaRectangle.width, dummyInvalidAreaRectangle.height);
			if (!miCanvas.paint(g))
				{
//MiDebug.println("***** Could not get lock to repaint, trying again later");
				super.repaint(clip);
				}
			}
		else
			{
			// ---------------------------------------------------------------------------
			// REPAINT DAMAGED AREAS EVENT 
			// ---------------------------------------------------------------------------
//System.out.println("Repaint damaged areas!!!!!!!!!!");
			miCanvas.update(g);
			}
		}

	public		void		setCanvas(MiCanvas canvas)
		{
		new MiAWTEventAdapter(canvas, this);
		setLayout(null);
		this.miCanvas = canvas;

		// -------------------------------------------------------------------------------
		// Swing expects us to completely render each and every area we tell it to repaint.
		// But since we don't want to do that, we keep a prepared buffer around and only
		// render what has changed and then blap the whole buffer into the Swing buffer.

		// An alternative to to repaint(area) an area that we have damaged and save this
		// area, as gotten from the SwingRepaintManager, as the dummy area. The dummy area
		// is used to differentiate between an expose event and a repaint-damaged-areas event.
		// The dummy area has to encompass all of the actual damaged areas because Swing
		// slips to whatever we specify as the damaged area.
		// -------------------------------------------------------------------------------
		canvas.setDoubleBuffered(true);
//canvas.setDoubleBuffered(false);
		canvas.getRenderer().setHasAWTClipRectBug(false);
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
***/
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
//System.out.println("set BOUNDS = " + deviceBounds);
//System.out.println("set BOUNDS insets = " + insets);
		setBounds(
			insets.left,
			insets.top,
			(int )(deviceBounds.xmax - deviceBounds.xmin),
			(int )(deviceBounds.ymax - deviceBounds.ymin), false);
		}
					/**------------------------------------------------------
	 				 * Returns the preferred size of this canvas. This 
					 * routine is in support of the Component API.
					 * @return		the preferred size
					 *------------------------------------------------------*/
	public 		Dimension 	getPreferredSize()
		{
//MiDebug.println("gggggggggggggggggggggggggget preferred size = " + miCanvas.getPreferredSize());
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
	public		void		requestRepaint()
		{
//MiDebug.println("REQUEST REPAINT: " +  dummyInvalidAreaRectangle);
//MiDebug.printStackTrace();
		super.repaint(dummyInvalidAreaRectangle);
		}
	public 		Rectangle 	getCanvasBounds()
		{
		Rectangle rect = super.getBounds();
		// ---------------------------------------------------------------------------
		// Apparently, a *feature* in Swing causes the repaint(rect) code to assume 
		// that the upper right is at 0,0 even though getBounds indicates otherwise. 
		// ---------------------------------------------------------------------------
//MiDebug.println("getCanvasBounds: " +rect);
		rect.x =0;
		rect.y =0;
		return(rect);
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
//System.out.println("SWING set bounds: " + x + ", " + y + ", " + width + ", " + height);
		setBounds(x, y, width, height, true);
		}
	private	synchronized void 	setBounds(int x, int y, int width, int height, boolean awtThread)
		{
		if (!miCanvas.hasBoundsChanged(x, y, width, height))
			{
			return;
			}
		if ((width == 0) || (height == 0))
			{
			return;
			}

//MiDebug.println("\nBEFORE setBounds getBounds() = " + getBounds());
		super.setBounds(x, y, width, height);

//		if (!awtThread)
//			{
//			invalidate();
//			}
//MiDebug.println("setBounds AWTThread = " + awtThread);
//MiDebug.println("setBounds = " + new MiBounds(x, y, width, height));
//MiDebug.println("setBounds getBounds() = " + getBounds());
//MiDebug.printStackTrace();

		Rectangle rect = getCanvasBounds();

		dummyInvalidAreaRectangle.x = rect.x;
		dummyInvalidAreaRectangle.y = rect.y;
		dummyInvalidAreaRectangle.width = rect.width - 1;
		dummyInvalidAreaRectangle.height = rect.height;

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		if (dummyInvalidAreaRectangle.height > size.height)
			{
			dummyInvalidAreaRectangle.height  = size.height;
			}
		if (dummyInvalidAreaRectangle.width > size.width)
			{
			dummyInvalidAreaRectangle.width  = size.width;
			}

		miCanvas.boundsHasChanged(rect, awtThread);
		}

	public		Insets		getContainerInsets()
		{
		Component c = this;
		while ((c != null) && (!(c instanceof Container)))
			c = c.getParent();
		return(c == null ? new Insets(0,0,0,0) : ((Container )c).getInsets());
		}
	}
