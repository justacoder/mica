
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
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.FastVector; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTreeListExpandCollapseButton extends MiPart
	{
	private		boolean		expanded;
	private		MiDistance	boxWidth		= 8;
	private		MiDistance	boxHeight		= 8;
	private		MiAttributes	boxAttributes;
	private		MiAttributes	signAttributes;


	
	public				MiTreeListExpandCollapseButton(boolean expanded)
		{
		this.expanded = expanded;

		boxAttributes = new MiAttributes(false);
		boxAttributes.setStaticColor(MiColorManager.black);
		boxAttributes.setStaticBackgroundColor(Mi_TRANSPARENT_COLOR);
		boxAttributes.setStaticBorderLook(Mi_FLAT_BORDER_LOOK);

		signAttributes = new MiAttributes(false);
		signAttributes.setStaticColor(MiColorManager.black);
		signAttributes.setStaticBorderLook(Mi_FLAT_BORDER_LOOK);

		replaceBounds(new MiBounds(0, 0, boxWidth, boxHeight));
		}

	public		void		setBoxAttributes(MiAttributes atts)
		{
		boxAttributes = atts;
		}
	public		MiAttributes	getBoxAttributes()
		{
		return(boxAttributes);
		}
	public		void		setSignAttributes(MiAttributes atts)
		{
		signAttributes = atts;
		}
	public		MiAttributes	getSignAttributes()
		{
		return(signAttributes);
		}

	public		void		render(MiRenderer renderer)
		{
		MiDistance boxHalfWidth = boxWidth/2;
		MiDistance boxHalfHeight = boxHeight/2;
		MiCoord x = getCenterX();
		MiCoord y = getCenterY();

		renderer.setAttributes(boxAttributes);
		renderer.drawRect(
			x - boxHalfWidth, y - boxHalfHeight, 
			x + boxHalfWidth, y + boxHalfHeight);
		renderer.setAttributes(signAttributes);
		renderer.drawLine(x - boxHalfWidth, y, x + boxHalfWidth, y);
		if (!expanded)
			renderer.drawLine(x, y - boxHalfHeight, x, y + boxHalfHeight);
		}
	}


