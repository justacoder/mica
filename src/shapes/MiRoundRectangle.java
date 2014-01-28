
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
public class MiRoundRectangle extends MiPart
	{
	private		double		arcWidthPercentage	= 0.3;
	private		double		arcHeightPercentage	= 0.3;
	private	static	MiBounds	tmpBounds		= new MiBounds();



	public				MiRoundRectangle()
		{
		}

	public				MiRoundRectangle(MiBounds b)
		{
		replaceBounds(b);
		}

	public				MiRoundRectangle(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		tmpBounds.setBounds(xmin, ymin, xmax, ymax);
		replaceBounds(tmpBounds);
		}

	public		double		getArcWidthPercentage()		
		{ 
		return(arcWidthPercentage); 
		}
	public		void		setArcWidthPercentage(double percentageOfMaximum)
		{
		arcWidthPercentage = percentageOfMaximum;
		}

	public		double		getArcHeightPercentage()	
		{ 
		return(arcHeightPercentage); 
		}
	public		void		setArcHeightPercentage(double percentageOfMaximum)
		{
		arcHeightPercentage = percentageOfMaximum;
		}

	public		void		render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		MiBounds bounds = getBounds(tmpBounds);
		MiDistance arcWidth = bounds.getWidth() * arcWidthPercentage;
		MiDistance arcHeight = bounds.getHeight() * arcHeightPercentage;
		renderer.drawRoundRect(bounds, arcWidth, arcHeight);
		}
	}


