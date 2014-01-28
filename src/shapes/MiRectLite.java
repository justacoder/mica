
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
public class MiRectLite extends MiLightweightShape
	{
	private		MiCoord		xmin;
	private		MiCoord		ymin;
	private		MiCoord		xmax;
	private		MiCoord		ymax;

	public				MiRectLite()
		{
		}
	public				MiRectLite(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
		}
	public		void		draw(MiRenderer renderer)
		{
		tmpBounds.xmin = xmin;
		tmpBounds.ymin = ymin;
		tmpBounds.xmax = xmax;
		tmpBounds.ymax = ymax;
		renderer.drawRect(tmpBounds);
		}
	public		void		setBounds(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
		}
	public		boolean		pick(MiBounds area)
		{
		tmpBounds.xmin = xmin;
		tmpBounds.ymin = ymin;
		tmpBounds.xmax = xmax;
		tmpBounds.ymax = ymax;
		if (area.intersects(tmpBounds))
			return(true);
		return(false);
		}
	public		MiBounds	getBounds(MiBounds pBounds)
		{
		pBounds.xmin = xmin;
		pBounds.ymin = ymin;
		pBounds.xmax = xmax;
		pBounds.ymax = ymax;
		return(pBounds);
		}
	}

