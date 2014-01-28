
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
public class MiDeviceBounds
	{
	public		int		xmin;
	public		int		ymin;
	public		int		xmax;
	public		int		ymax;



					/**------------------------------------------------------
	 				 * Constructs a new MiDeviceBounds. 
					 *------------------------------------------------------*/
	public				MiDeviceBounds()
		{
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiDeviceBounds. 
	 				 * @param xmin	 	the xmin component
	 				 * @param ymin	 	the ymin component
	 				 * @param xmax	 	the xmax component
	 				 * @param ymax	 	the ymax component
					 *------------------------------------------------------*/
	public				MiDeviceBounds(int xmin, int ymin, int xmax, int ymax)
		{
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
		}

	public		void		reverse()
		{
		xmin = Integer.MAX_VALUE;
		ymin = Integer.MAX_VALUE;
		xmax = Integer.MIN_VALUE;
		ymax = Integer.MIN_VALUE;
		}

	public		boolean		isReversed()
		{
		return((xmax - xmin < 0) || (ymax - ymin < 0));
		}

	public		int		getWidth()
		{
		return(xmax - xmin);
		}
	
	public		int		getHeight()
		{
		return(ymax - ymin);
		}
	
	public		void		copy(MiBounds b)
		{
		MiCoord t = b.xmin;
		xmin = (int )(t > 0 ? t + 0.5 : t - 0.5);
		t = b.ymin;
		ymin = (int )(t > 0 ? t + 0.5 : t - 0.5);
		t = b.xmax;
		xmax = (int )(t > 0 ? t + 0.5 : t - 0.5);
		t = b.ymax;
		ymax = (int )(t > 0 ? t + 0.5 : t - 0.5);
		}
					/**------------------------------------------------------
	 				 * Return text that describes this vector.
					 *------------------------------------------------------*/
	public 		String 		toString()
		{
		return(MiDebug.getMicaClassName(this) 
			+ "[xmin=" + xmin + ",ymin=" + ymin + ",xmax=" + xmax + ",ymax=" + ymax + "][" 
			+ getWidth() + "x" + getHeight() + "](" 
			+ ((xmin + xmax)/2) + "," + ((ymin + ymax)/2) + ")");
		}
	}

