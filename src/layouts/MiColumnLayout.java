
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
import com.swfm.mica.*; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiColumnLayout extends MiRowColBaseLayout
	{
	private		MiSize		preferredSize 	= new MiSize();
	private		MiSize		minSize 	= new MiSize();
	private		MiSize		prefSize 	= new MiSize();
	private	static	MiMargins	tmpMargins 	= new MiMargins();



	public				MiColumnLayout()
		{
		this(false);
		}

	public				MiColumnLayout(boolean manipulatable)
		{
		super(manipulatable);
		setElementVSizing(Mi_NONE);
		isColumn = true;
		}

	public		void		doLayout()
		{
		MiSize		maxElementSize;
		MiDistance	totalVerticalSpaceOfElements;
		MiDistance	verticalEmptySpaceToFill = 0;
		boolean		useMinimumSize 	= false;
		MiPart		target 		= getTarget();
		MiDistance	alleyVSpacing 	= getAlleyVSpacing();

		MiBounds	targetBounds	= MiBounds.newBounds();
		MiBounds	objBounds	= MiBounds.newBounds();

		getPreferredSize(preferredSize);

		int 		num = numberOfVisibleObjects;

		target.getInnerBounds(targetBounds);
		if (targetBounds.isReversed())
			targetBounds.setBounds(0,0,1,1);

//if ("testLayout".equals(getName()))
//{
//MiDebug.println("\n" + this + "target = " + target);
//MiDebug.println(this + "target bounds = " + targetBounds);
//MiDebug.println(this + "preferredSize " + preferredSize);
//}

		if (targetBounds.getHeight() < preferredSize.getHeight())
			{
//{
//MiDebug.println("\n" + this + "target = " + target);
//MiDebug.println(this + "target bounds = " + targetBounds);
//MiDebug.println(this + "preferredSize " + preferredSize);
//MiDebug.println(this + " ******** TOO SMALL ********* ");
//}

			maxElementSize = maxElementMinSize;
			if (maxElementSize.getHeight() == 0)
				maxElementSize.setHeight(maxElementPrefSize.getHeight());
			if (maxElementSize.getWidth() == 0)
				maxElementSize.setWidth(maxElementPrefSize.getWidth());
			totalVerticalSpaceOfElements = totalMinVerticalSpaceOfElements;
			useMinimumSize = true;
			}
		else 
			{
			maxElementSize = maxElementPrefSize;
			totalVerticalSpaceOfElements = totalPrefVerticalSpaceOfElements;
			}

		targetBounds.subtractMargins(getInsetMargins(tmpMargins));
		verticalEmptySpaceToFill = targetBounds.getHeight() - totalVerticalSpaceOfElements;

		if (getElementHSizing() == Mi_EXPAND_TO_FILL)
			{
			maxElementSize.setWidth(targetBounds.getWidth());
			}

		MiCoord 	y = targetBounds.ymax;
		MiDistance 	vSpacing = alleyVSpacing;
		if (getElementVJustification() == Mi_JUSTIFIED)
			{
			vSpacing = (targetBounds.getHeight() - 
				totalVerticalSpaceOfElements)/(num + 1);
			if (vSpacing < alleyVSpacing)
				vSpacing = alleyVSpacing;
			else
				y = targetBounds.ymax - vSpacing;
			}
		else if ((getElementVJustification() == Mi_CENTER_JUSTIFIED)
			&& (getElementVSizing() != Mi_EXPAND_TO_FILL)
			&& (getUniqueElementSizing() != Mi_EXPAND_TO_FILL))
			{
			if ((verticalEmptySpaceToFill > 0) && (!useMinimumSize))
				y = targetBounds.ymax - (verticalEmptySpaceToFill - (num - 1) * vSpacing)/2;
			}
		else if ((getElementVJustification() == Mi_BOTTOM_JUSTIFIED)
			&& (getElementVSizing() != Mi_EXPAND_TO_FILL)
			&& (getUniqueElementSizing() != Mi_EXPAND_TO_FILL))
			{
			if ((verticalEmptySpaceToFill > 0) && (!useMinimumSize))
				y = targetBounds.ymax - verticalEmptySpaceToFill - (num - 1) * vSpacing;
			}

		int index = 0;
		MiBounds b = objBounds;
		for (int i = 0; i < num; ++i)
			{
			MiPart obj;
			do 	{
				obj = target.getPart(index++);
				} while (!obj.isVisible());

			obj.getPreferredSize(prefSize);
			if (useMinimumSize)
				{
				obj.getMinimumSize(minSize);
				if (preferredSize.getHeight() > totalVerticalSpaceOfElements)
					{
//MiDebug.println("prefSize.getHeight()=" + prefSize.getHeight());
//MiDebug.println("minSize.getHeight()=" + minSize.getHeight());
//MiDebug.println("preferredSize.getHeight()=" + preferredSize.getHeight());
//MiDebug.println("totalVerticalSpaceOfElements=" + totalVerticalSpaceOfElements);
//MiDebug.println("verticalEmptySpaceToFill=" + verticalEmptySpaceToFill);
//MiDebug.println("(preferredSize.getHeight() - totalVerticalSpaceOfElements)=" + (preferredSize.getHeight() - totalVerticalSpaceOfElements));
//MiDebug.println("verticalEmptySpaceToFill/(preferredSize.getHeight() - totalVerticalSpaceOfElements)=" 
//+verticalEmptySpaceToFill/(preferredSize.getHeight() - totalVerticalSpaceOfElements));


					prefSize.setHeight(minSize.getHeight() 
						+ (prefSize.getHeight() - minSize.getHeight())
						* verticalEmptySpaceToFill
						/(preferredSize.getHeight() - totalVerticalSpaceOfElements));
//MiDebug.println("maxElementSize.getHeight()=" + maxElementSize.getHeight());
//MiDebug.println("OOOKKKK prefSize.getHeight()=" + prefSize.getHeight());
					}
				else
					{
					prefSize.setHeight(minSize.getHeight()); 
					}
				}

			b.setSize(prefSize);

			if ((getElementHSizing() == Mi_SAME_SIZE) 
				|| (getElementHSizing() == Mi_EXPAND_TO_FILL))
				{
				if (b.getWidth() < maxElementSize.getWidth())
					b.setWidth(maxElementSize.getWidth());
				}
			if ((getElementVSizing() == Mi_SAME_SIZE))
// 4-19-2002 What if this element is not largest element?|| (getElementVSizing() == Mi_EXPAND_TO_FILL))
				{
				if (b.getHeight() < maxElementSize.getHeight())
					b.setHeight(maxElementSize.getHeight());
				}


			if (getElementVJustification() != Mi_NONE)
				{
				if ((getUniqueElementSizing() == Mi_EXPAND_TO_FILL)
					&& ((index - 1 == getUniqueElementIndex()) 
					|| ((getUniqueElementIndex() == -1) && (i == num - 1))))
					{
					if (!useMinimumSize)
						{
						b.setHeight(b.getHeight() + targetBounds.getHeight() 
							- totalVerticalSpaceOfElements 
							- (num - 1) * vSpacing);
						}
					if (b.getWidth() < targetBounds.getWidth())
						{
						b.setWidth(targetBounds.getWidth());
						}
					b.setCenterY(y - b.getHeight()/2);
					}
				else if ((i == num - 1) 
					&& (getLastElementJustification() == Mi_BOTTOM_JUSTIFIED)
					&& (getElementVJustification() == Mi_TOP_JUSTIFIED))
					{
					b.setCenterY(Math.min(targetBounds.ymin
						+ b.getHeight()/2, y - b.getHeight()/2));
					}
				else
					{
					b.setCenterY(y - b.getHeight()/2);
					}
				}
			switch (getElementHJustification())
				{
				case Mi_CENTER_JUSTIFIED :
					b.setCenterX(targetBounds.getCenterX());
					break;
				case Mi_LEFT_JUSTIFIED :
					b.translate(targetBounds.xmin - b.xmin, 0);
					break;
				case Mi_RIGHT_JUSTIFIED :
					b.translate(targetBounds.xmax - b.xmax, 0);
					break;
				}

		if (b.getWidth() > targetBounds.getWidth())
			b.setWidth(targetBounds.getWidth());
			
//if ((obj.getName() != null) && (obj.getName().startsWith("badContainer")))
//if (obj instanceof MiScrolledBox)
//if (target instanceof MiGraphicsWindow)
//if (("aaaaaa".equals(getName())) || ("xxxxxx".equals(getName())))
//{
//MiDebug.println("\n********\n" + this + "target = " + target);
//MiDebug.println(this + ": layout target bounds = " + targetBounds);
//MiDebug.println(this + ": layout preferredSize =" + preferredSize);
//MiDebug.println(this + ": layout totalVerticalSpaceOfElements =" + totalVerticalSpaceOfElements);
//MiDebug.println(this + ": useMinimumSize = " + useMinimumSize);
//MiDebug.println(this + ": obj = " + obj);
//MiDebug.println(this + ": obj had bounds: " + obj.getBounds());
//MiDebug.println(this + ": obj had preferred size: " + obj.getPreferredSize(new MiSize()));
//MiDebug.println(this + ": obj had minimum size: " + obj.getMinimumSize(new MiSize()));
//MiDebug.println(this + ": will now set obj bounds to: " + b);
//MiDebug.println("drawBounds of Obj = " + obj.getDrawBounds(new MiBounds()));
//MiDebug.dump(obj);
//}


			obj.setBounds(b);


			y -= obj.getBounds(b).getHeight() + vSpacing;
			}
		MiBounds.freeBounds(targetBounds);
		MiBounds.freeBounds(objBounds);
		}
	}

