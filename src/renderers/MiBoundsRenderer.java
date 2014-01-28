
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
import java.awt.Color;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiBoundsRenderer implements MiiPartRenderer
	{
	private		boolean		doNotRenderTargetParts = false;
	private		MiAttributes	geomBoundsAttributes;
	private		MiAttributes	drawBoundsAttributes;


	public				MiBoundsRenderer()
		{
		this(false);
		}
	public				MiBoundsRenderer(boolean doNotRenderTargetParts)
		{
		this(doNotRenderTargetParts, MiColorManager.red, MiColorManager.yellow);
		}
	public				MiBoundsRenderer(
						boolean doNotRenderTargetParts, 
						Color geomBoundsColor, 
						Color drawBoundsColor)
		{
		this.doNotRenderTargetParts = doNotRenderTargetParts;

		MiAttributes attributes = MiPart.getDefaultAttributes().copy();
		if (geomBoundsColor != null)
			geomBoundsAttributes = attributes.setColor(geomBoundsColor);
		if (drawBoundsColor != null)
			drawBoundsAttributes = attributes.setColor(drawBoundsColor);
//]FIX		attributes.setWriteMode(MiiTypes.Mi_XOR_WRITEMODE);
		}

	public				MiBoundsRenderer(
						boolean doNotRenderTargetParts, 
						MiAttributes geomBoundsAttributes, 
						MiAttributes drawBoundsAttributes)
		{
		this.doNotRenderTargetParts = doNotRenderTargetParts;

		this.geomBoundsAttributes = geomBoundsAttributes;
		this.drawBoundsAttributes = drawBoundsAttributes;
//]FIX		attributes.setWriteMode(MiiTypes.Mi_XOR_WRITEMODE);
		}


			// Return true if OK to render actual obj
	public		boolean		render(MiPart obj, MiRenderer renderer)
		{
		if (drawBoundsAttributes != null)
			{
			renderer.setAttributes(drawBoundsAttributes);
			renderer.drawRect(obj.getDrawBounds(new MiBounds()));
			}
		if (geomBoundsAttributes != null)
			{
			renderer.setAttributes(geomBoundsAttributes);
			renderer.drawRect(obj.getBounds());
			}

		return(!doNotRenderTargetParts);
		}
			// Return true if bounds of renderer are larger than obj bounds
	public		boolean		getBounds(MiPart obj, MiBounds b)
		{
		return(false);
		}
	}

