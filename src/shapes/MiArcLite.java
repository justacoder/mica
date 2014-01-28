
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
public class MiArcLite extends MiLightweightShape
	{
	private		MiCoord		centerX;
	private		MiCoord		centerY;
	private		MiDistance	radius;
	private		double		startAngle;
	private		double		endAngle;

	public				MiArcLite()
		{
		}
	public	void			setCenter(MiPoint pt)
		{
		centerX = pt.x;
		centerY = pt.y;
		}
	public	void			setRadius(MiDistance r) 	{ radius = r; 		}
	public	MiDistance		getRadius()		 	{ return(radius); 	}

	public		void		setStartAngle(double degrees) 	{ startAngle = degrees;	}
	public		double		getStartAngle() 		{ return(startAngle); 	}
	public		void		setEndAngle(double degrees) 	{ endAngle = degrees; 	}
	public		double		getEndAngle() 			{ return(endAngle); 	}

	public		void		draw(MiRenderer renderer)
		{
		renderer.drawCircularArc(getBounds(tmpBounds), startAngle, endAngle);
		}
	public		MiBounds	getBounds(MiBounds pBounds)
		{
		pBounds.setWidth(radius * 2);
		pBounds.setHeight(radius * 2);
		pBounds.setCenter(centerX, centerY);
		return(pBounds);
		}
	}

