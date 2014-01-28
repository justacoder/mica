
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
public class MiAutoAdjustShapeAroundTextActionHandler implements MiiActionHandler, MiiActionTypes, MiiTypes
	{
	private		MiBounds	textBounds 	= new MiBounds();
	private		MiBounds	shapeBounds 	= new MiBounds();
	private		MiSize		globalMinimimSize = new MiSize(10, 10);
	private		MiSize		minimimSize 	= new MiSize();
	private		boolean		shrinkToFit 	= true;
	private		MiMargins	amountToAdjustPerIteration 	= new MiMargins(5);

	public				MiAutoAdjustShapeAroundTextActionHandler()
		{
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_TEXT_CHANGE_ACTION))
			{
			MiPart text = action.getActionSource();
			MiPart shape = text.getContainer(0);
//MiDebug.println(this + "text= " + text + ",shape = " + shape);
			if ((shape == null) || (shape instanceof MiLayer) || (shape instanceof MiEditor))
				{
				return(true);
				}
			if (shape instanceof MiAttachments)
				{
				shape = shape.getContainer(0);
				}
			shape.getMinimumSize(minimimSize);
//MiDebug.println(this + "shape = " + shape);
			if (minimimSize.isSmallerSizeThan(globalMinimimSize))
				{
				minimimSize.copy(globalMinimimSize);
				}

			text.getBounds(textBounds);

			boolean textIntersectsEnclosingShape = true;
			boolean expandingShapeToFit = true;
			while (textIntersectsEnclosingShape)
				{
				textIntersectsEnclosingShape = false;

				shape.getBounds(shapeBounds);

				if (shape.getNumberOfParts() == 0)
					{
					if (((shape.isPickableWhenTransparent()) 
						|| (shape.getBackgroundColor() != Mi_TRANSPARENT_COLOR))
						&& (textBounds.isLargerSizeThan(shape.getBounds())))
						{
						textIntersectsEnclosingShape = true;
						}
					}
				else
					{
					for (int i = 0; i < shape.getNumberOfParts(); ++i)
						{
						MiPart prim = shape.getPart(i);
//MiDebug.println("prim= " + prim);
//MiDebug.println("prim.isPickableWhenTransparent()= " + prim.isPickableWhenTransparent());
						if (prim != text)
							{
							boolean pickableWhenTransparent = prim.isPickableWhenTransparent();
							prim.setPickableWhenTransparent(false);
							Color backgroundColor = prim.getBackgroundColor();
							prim.setBackgroundColor(Mi_TRANSPARENT_COLOR);
//MiDebug.println("textBounds= " + textBounds);
							if (prim.pick(textBounds))
								{
								prim.setPickableWhenTransparent(pickableWhenTransparent);
								prim.setBackgroundColor(backgroundColor);
								textIntersectsEnclosingShape = true;
								break;
								}
							prim.setPickableWhenTransparent(pickableWhenTransparent);
							prim.setBackgroundColor(backgroundColor);
							}
						}
					}
//MiDebug.println("textIntersectsEnclosingShape= " + textIntersectsEnclosingShape);
				if (textIntersectsEnclosingShape)
					{
//MiDebug.println("expandingShapeToFit= " + expandingShapeToFit);
					if (expandingShapeToFit)
						{
						// Assume center justified for now...
						shape.setBounds(shapeBounds.addMargins(amountToAdjustPerIteration));
						}
					else
						{
						shape.setBounds(shapeBounds.addMargins(amountToAdjustPerIteration));
						// done
						break;
						}
					}
				else 
					{
//MiDebug.println("shrinkToFit= " + shrinkToFit);
					if (shrinkToFit)
						{
						shapeBounds.subtractMargins(amountToAdjustPerIteration);
//MiDebug.println("shapeBounds= " + shapeBounds);
//MiDebug.println("globalMinimimSize= " + globalMinimimSize);
//MiDebug.println("(shapeBounds.isSmallerSizeThan(globalMinimimSize))= " + (shapeBounds.isSmallerSizeThan(globalMinimimSize)));
						if (shapeBounds.isSmallerSizeThan(globalMinimimSize))
							{
							// done
							break;
							}
//MiDebug.println("shrink" );
						shape.setBounds(shapeBounds);
						textIntersectsEnclosingShape = true;
						expandingShapeToFit = false;
						}
					else
						{
						// done
						break;
						}
					}
//shape.invalidateArea();
//MiDebug.println("shapeBounds= " + shapeBounds);
				}
			}
		return(true);
		}
	}

