
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTriangle extends MiMultiPointShape
	{
	public				MiTriangle(int orientation)
		{
		this();
		setOrientation(orientation);
		}
	public				MiTriangle()
		{
		appendPoint(0.0, 0.0);
		appendPoint(1.0, 0.0);
		appendPoint(0.5, 1.0);
		}
					// Counter-clockwise is required for indented/raised styles to work
	public				MiTriangle(MiPoint pt1, MiPoint pt2, MiPoint pt3)
		{
		appendPoint(pt1);
		appendPoint(pt2);
		appendPoint(pt3);
		}
	public	void			render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		renderer.drawPolygon(xPoints, yPoints, true);
		}
	public	void			setOrientation(int value)
		{
		MiBounds bounds = getBounds();
		if (value == Mi_UP)
			{
			setPoint(0, bounds.getCenterX(), bounds.ymax);
			setPoint(1, bounds.xmin, bounds.ymin);
			setPoint(2, bounds.xmax, bounds.ymin);
			}
		else if (value == Mi_DOWN)
			{
			setPoint(0, bounds.getCenterX(), bounds.ymin);
			setPoint(1, bounds.xmax, bounds.ymax);
			setPoint(2, bounds.xmin, bounds.ymax);
			}
		else if (value == Mi_RIGHT)
			{
			setPoint(0, bounds.xmax, bounds.getCenterY());
			setPoint(1, bounds.xmin, bounds.ymax);
			setPoint(2, bounds.xmin, bounds.ymin);
			}
		else if (value == Mi_LEFT)
			{
			setPoint(0, bounds.xmin, bounds.getCenterY());
			setPoint(1, bounds.xmax, bounds.ymin);
			setPoint(2, bounds.xmax, bounds.ymax);
			}
		}
	}


