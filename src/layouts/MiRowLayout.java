
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
public class MiRowLayout extends MiRowColBaseLayout
	{
	private		MiSize		preferredSize 	= new MiSize();
	private		MiSize		minSize 	= new MiSize();
	private		MiSize		prefSize 	= new MiSize();
	private	static	MiMargins	tmpMargins 	= new MiMargins();



	public				MiRowLayout()
		{
		this(false);
		}
	public				MiRowLayout(boolean manipulatable)
		{
		super(manipulatable);
		setElementHSizing(Mi_NONE);
		}

	public		void		doLayout()
		{
		MiSize		maxElementSize;
		MiDistance	totalHorizontalSpaceOfElements;
		MiDistance	horizontalEmptySpaceToFill = 0;
		boolean		useMinimumSize 		= false;
		MiPart		target 			= getTarget();
		MiDistance	alleyHSpacing 		= getAlleyHSpacing();

		getPreferredSize(preferredSize);

		int 		num 			= numberOfVisibleObjects;
		MiBounds	targetBounds		= MiBounds.newBounds();
		MiBounds	objBounds		= MiBounds.newBounds();

		target.getInnerBounds(targetBounds);
		if (targetBounds.isReversed())
			targetBounds.setBounds(0,0,1,1);

//if ("horizontal sash container".equals(getName()))
//{
//MiDebug.println("\n" + this + ", target = " + target);
//MiDebug.println(this + ", target bounds = " + targetBounds);
//MiDebug.println(this + ", preferredSize " + preferredSize);
//}

		if (targetBounds.getWidth() < preferredSize.getWidth())
			{
			maxElementSize = maxElementMinSize;
			if (maxElementSize.getHeight() == 0)
				maxElementSize.setHeight(maxElementPrefSize.getHeight());
			if (maxElementSize.getWidth() == 0)
				maxElementSize.setWidth(maxElementPrefSize.getWidth());
			totalHorizontalSpaceOfElements = totalMinHorizontalSpaceOfElements;
			useMinimumSize = true;
//MiDebug.println(this + ", useMinimumSize ++++ " + useMinimumSize);
			}
		else 
			{
			maxElementSize = maxElementPrefSize;
			totalHorizontalSpaceOfElements = totalPrefHorizontalSpaceOfElements;
			}

		targetBounds.subtractMargins(getInsetMargins(tmpMargins));
		horizontalEmptySpaceToFill = targetBounds.getWidth() - totalHorizontalSpaceOfElements;

		if (getElementVSizing() == Mi_EXPAND_TO_FILL)
			{
			maxElementSize.setHeight(targetBounds.getHeight());
			}

		MiCoord 	x = targetBounds.xmin;
		MiDistance 	hSpacing = alleyHSpacing;
		if (getElementHJustification() == Mi_JUSTIFIED)
			{
			hSpacing = (targetBounds.getWidth() 
				- totalHorizontalSpaceOfElements)/(num + 1);
			if (hSpacing < alleyHSpacing)
				hSpacing = alleyHSpacing;
			else
				x = targetBounds.xmin + hSpacing;
			}
		else if ((getElementHJustification() == Mi_CENTER_JUSTIFIED)
			&& (getElementHSizing() != Mi_EXPAND_TO_FILL)
			&& (getUniqueElementSizing() != Mi_EXPAND_TO_FILL))
			{
			if ((horizontalEmptySpaceToFill > 0) && (!useMinimumSize))
				x = targetBounds.xmin + (horizontalEmptySpaceToFill - (num - 1) * hSpacing)/2;
			}
		else if ((getElementHJustification() == Mi_RIGHT_JUSTIFIED)
			&& (getElementHSizing() != Mi_EXPAND_TO_FILL)
			&& (getUniqueElementSizing() != Mi_EXPAND_TO_FILL))
			{
			if ((horizontalEmptySpaceToFill > 0) && (!useMinimumSize))
				x = targetBounds.xmin + horizontalEmptySpaceToFill - (num - 1) * hSpacing;
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
				if (preferredSize.getWidth() > totalHorizontalSpaceOfElements)
					{
					prefSize.setWidth(minSize.getWidth() 
						+ (prefSize.getWidth() - minSize.getWidth())
						* horizontalEmptySpaceToFill
						/(preferredSize.getWidth() - totalHorizontalSpaceOfElements));

					}
				else
					{
					prefSize.setWidth(minSize.getWidth()); 
					}
				}

//MiDebug.println("preSize.getWidth() = " + prefSize.getWidth());
//MiDebug.println("useMinimumSize = " + useMinimumSize);

			b.setSize(prefSize);

			if ((getElementHSizing() == Mi_SAME_SIZE)) 
// 4-19-2002 What is this is not the largest element?				|| (getElementHSizing() == Mi_EXPAND_TO_FILL))
				{
				b.setWidth(maxElementSize.getWidth());
				}
			if ((getElementVSizing() == Mi_SAME_SIZE) 
				|| (getElementVSizing() == Mi_EXPAND_TO_FILL))
				{
				b.setHeight(maxElementSize.getHeight());
				}

			if (getElementHJustification() != Mi_NONE)
				{
				if ((getUniqueElementSizing() == Mi_EXPAND_TO_FILL)
					&& ((index - 1 == getUniqueElementIndex()) 
					|| ((getUniqueElementIndex() == -1) && (i == num - 1))))
					{
					if (!useMinimumSize)
						{
						b.setWidth(b.getWidth() + targetBounds.getWidth() 
							- totalHorizontalSpaceOfElements 
							- (num - 1) * hSpacing);
						}
					if ((getElementVJustification() == Mi_CENTER_JUSTIFIED) // 4-29-2004 allow for expansion in h dir only for h scrollbar + layer tabs in MiDiagram
						&& (b.getHeight() < targetBounds.getHeight()))
						{
						b.setHeight(targetBounds.getHeight());
						}
					b.setCenterX(x + b.getWidth()/2);
					}
				else if ((i == num - 1) 
					&& (getLastElementJustification() == Mi_RIGHT_JUSTIFIED)
					&& (getElementHJustification() == Mi_LEFT_JUSTIFIED))
					{
					b.setCenterX(Math.max(targetBounds.xmax
						- b.getWidth()/2, x + b.getWidth()/2));
					}
				else
					{
					b.setCenterX(x + b.getWidth()/2);
					}
				}

			switch (getElementVJustification())
				{
				case Mi_CENTER_JUSTIFIED :
					b.setCenterY(targetBounds.getCenterY());
					break;
				case Mi_BOTTOM_JUSTIFIED :
					b.translate(0, targetBounds.ymin - b.ymin);
					break;
				case Mi_TOP_JUSTIFIED :
					b.translate(0, targetBounds.ymax - b.ymax);
					break;
				}


			if (b.getHeight() > targetBounds.getHeight())
				{
				b.setHeight(targetBounds.getHeight());
				}

			// In case preferred size == 0 but the object has its own size 
			// - we do not want to override and set a scale factor to 0
			if (b.getHeight() == 0)
				{
				b.setHeight(obj.getHeight());
				}
			if (b.getWidth() == 0)
				{
				b.setWidth(obj.getWidth());
				}


//if (target instanceof MiTreeListEntryGraphics)//  || (obj instanceof MiDrawingShapeToolBar))
//if ((target.getNumberOfContainers() > 0) && (target.getContainer(0) instanceof MiTabs))
//if ((target.getNumberOfParts() > 0) && (target.getPart(0) instanceof MiTabs))
//if ("horizontal sash container".equals(getName()))
//{
//MiDebug.println("\n********\n" + this + "target = " + target);
//MiDebug.println(this + "ROW setBounds obj: " + obj + " to " + b);
//MiDebug.println(this + "layout target bounds = " + targetBounds);
//MiDebug.println(this + "layout preferredSize =" + preferredSize);
//MiDebug.println(this + "useMinimumSize = " + useMinimumSize);
//MiDebug.println(this + "obj = " + obj);
//MiDebug.println(this + "obj had bounds: " + obj.getBounds());
//MiDebug.println(this + "obj had preferred size: " + obj.getPreferredSize(new MiSize()));
//MiDebug.println(this + "will now set obj bounds to: " + b);
//MiDebug.dump(obj);
//}

			obj.setBounds(b);


			x += obj.getBounds(b).getWidth() + hSpacing;
			}
		MiBounds.freeBounds(targetBounds);
		MiBounds.freeBounds(objBounds);
		}
	public		String		toString()
		{
		String str = super.toString();

		String vSizing = MiSystem.getNameOfAttributeValue(MiiNames.elementSizingNames, getElementVSizing());
		String hSizing = MiSystem.getNameOfAttributeValue(MiiNames.elementSizingNames, getElementHSizing());
		String vJustification = MiSystem.getNameOfAttributeValue(MiiNames.verticalJustificationNames, getElementVJustification());
		String hJustification = MiSystem.getNameOfAttributeValue(MiiNames.horizontalJustificationNames, getElementHJustification());
		if (vSizing == null)
			{
			vSizing = "invalid";
			}
		if (hSizing == null)
			{
			hSizing = "invalid";
			}
		if (vJustification == null)
			{
			vJustification = "invalid";
			}
		if (hJustification == null)
			{
			hJustification = "invalid";
			}

		str += "[Vsizing=" + vSizing + ",Hsizing=" + hSizing + ",vJustification=" + vJustification + ",hJustification=" + hJustification + "]";
		return(str);
		}
	}

