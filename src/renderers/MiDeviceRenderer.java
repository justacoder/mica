
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
import java.awt.Graphics; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
class MiDeviceRenderer implements MiiDeviceRenderer
	{
			// Return true if bounds of renderer extend outside obj bounds
	public		boolean		getBounds(MiAttributes attributes,
						MiBounds drawBounds, MiBounds renderedBounds)
		{
		return(false);
		}
	public		boolean		drawLine(Graphics g, MiAttributes attributes,
			int x1, int y1, int x2, int y2)
		{
		return(true);
		}

	public		boolean		drawRoundRect(Graphics g, MiAttributes attributes,
			int xmin, int ymin, int width, int height, int arcWidth, int arcHeight)
		{
		return(true);
		}

	public		boolean		drawPolygon(Graphics g, MiAttributes attributes,
			int[] xpts, int[] ypts, int numPts)
		{
		return(true);
		}

	public		boolean		drawOval(Graphics g, MiAttributes attributes,
			int xmin, int ymin, int width, int height)
		{
		return(true);
		}

	public		boolean		drawText(Graphics g, MiAttributes attributes,
			String text, int x, int y)
		{
		return(true);
		}

	public		boolean		drawRect(Graphics g, MiAttributes atts, 
							int dxmin, int dymin, int dwidth, int dheight)
		{
		return(true);
		}
	}

