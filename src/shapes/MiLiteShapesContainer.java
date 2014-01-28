
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
import com.swfm.mica.util.FastVector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiLiteShapesContainer extends MiPart
	{
	private	FastVector		prims 	= new FastVector();
	private	MiBounds 		tmp 	= new MiBounds();

	public				MiLiteShapesContainer()
		{
		}

	//---------------------------------------
	// Draw management
	// -------------------------------------
	public	void			render(MiRenderer renderer)
		{
		renderer.setAttributes(getAttributes());
		for (int i = 0; i < prims.size(); ++i)
			{
			((MiLightweightShape )prims.elementAt(i)).draw(renderer);
			}
		}

	//---------------------------------------
	// Bounds management
	// -------------------------------------
	protected 	void		reCalcBounds(MiBounds bounds)
		{
		bounds.reverse();
		for (int i = 0; i < prims.size(); ++i)
			{
			((MiLightweightShape )prims.elementAt(i)).getBounds(tmp);
			bounds.union(tmp);
			}
		}
	
	//---------------------------------------
	// Pick management
	// -------------------------------------
	public	boolean			pick(MiBounds area)
		{
		for (int i = 0; i < prims.size(); ++i)
			{
			if (((MiLightweightShape )prims.elementAt(i)).pick(area))
				return(true);
			}
		return(false);
		}

	//---------------------------------------
	// Primitive management
	// -------------------------------------
	public	int			getNumberOfShapes()
		{
		return(prims.size());
		}
	public	MiLightweightShape 	getShape(int index)
		{
		return((MiLightweightShape )prims.elementAt(index));
		}
	public	void			appendShape(MiLightweightShape prim)
		{
		prims.addElement(prim);
		invalidateArea(prim.getBounds(tmp));
		refreshBounds();
		}
	public	void			insertShape(MiLightweightShape prim, int index)
		{
		prims.insertElementAt(prim, index);
		invalidateArea(prim.getBounds(tmp));
		refreshBounds();
		}
	public	void			removeShape(int index)
		{
		invalidateArea(((MiLightweightShape )prims.elementAt(index)).getBounds(tmp));
		prims.removeElementAt(index);
		refreshBounds();
		}
	public	void			removeAllShapes()
		{
		invalidateArea();
		prims.removeAllElements();
		refreshBounds();
		}
	}

