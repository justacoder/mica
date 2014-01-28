
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
public abstract class MiRowColBaseLayout extends MiManipulatableLayout
	{
	private	static	MiMargins	tmpMargins 				= new MiMargins();

	protected	boolean		isColumn				= false;

	protected	MiDistance 	totalMinHorizontalSpaceOfElements;
	protected	MiDistance 	totalPrefHorizontalSpaceOfElements;

	protected	MiDistance 	totalMinVerticalSpaceOfElements;
	protected	MiDistance 	totalPrefVerticalSpaceOfElements;

	protected	MiSize		maxElementMinSize			= new MiSize();
	protected	MiSize		maxElementPrefSize			= new MiSize();

	protected	MiSize		minSize					= new MiSize();
	protected	MiSize		prefSize				= new MiSize();

	protected	MiSize		tmpSize					= new MiSize();

	protected	int		numberOfVisibleObjects;



	public				MiRowColBaseLayout()
		{
		this(false);
		}
	public				MiRowColBaseLayout(boolean manipulatable)
		{
		super(manipulatable, false);
		setMinimumNumberOfTargetNodes(3);
		// FIX: resolve differences between cell Margins and alley Margins
		setCellMargins(0);
		setLayoutContainerPartRelations(true);
		}

	protected	MiPart		makePlaceHolderShape()
		{
		return(new MiRectangle());
		}
	public		MiParts		insertPlaceHolder(int index)
		{
		MiPlaceHolder ph = (MiPlaceHolder )getPrototypePlaceHolder().copy();
		ph.setCenter(getTarget().getCenter());
		index = getTarget().getIndexOfPart(getNode(index));
		getTarget().insertPart(ph, index);
		return(new MiParts(ph));
		}
	public		MiParts		appendPlaceHolder(int index)
		{
		MiPlaceHolder ph = (MiPlaceHolder )getPrototypePlaceHolder().copy();
		ph.setCenter(getTarget().getCenter());
		getTarget().appendPart(ph);
		return(new MiParts(ph));
		}
	protected	void		calcMinimumSize(MiSize size)
		{
		calcMinAndPreferredSize();
		size.copy(minSize);
		}
	protected	void		calcPreferredSize(MiSize size)
		{
		calcMinAndPreferredSize();
		size.copy(prefSize);
		}

	protected	void		calcMinAndPreferredSize()
		{
		totalMinHorizontalSpaceOfElements 	= 0.0;
		totalPrefHorizontalSpaceOfElements 	= 0.0;

		totalMinVerticalSpaceOfElements 	= 0.0;
		totalPrefVerticalSpaceOfElements 	= 0.0;

		maxElementMinSize.zeroOut();
		maxElementPrefSize.zeroOut();

		minSize.zeroOut();
		prefSize.zeroOut();

		numberOfVisibleObjects = 0;

		MiPart 		target 		= getTarget();
		int 		num 		= target.getNumberOfParts();
		MiSize		size		= tmpSize;

		if (num == 0)
			return;

		for (int i = 0; i < num; ++i)
			{
			if (target.getPart(i).isVisible())
				{
				++numberOfVisibleObjects;

				target.getPart(i).getMinimumSize(size);
				if (maxElementMinSize.width < size.width)
					maxElementMinSize.width =  size.width;
				if (maxElementMinSize.height < size.height)
					maxElementMinSize.height =  size.height;
				totalMinHorizontalSpaceOfElements += size.width;
				totalMinVerticalSpaceOfElements += size.height;

				target.getPart(i).getPreferredSize(size);
				if (maxElementPrefSize.width < size.width)
					maxElementPrefSize.width =  size.width;
				if (maxElementPrefSize.height < size.height)
					maxElementPrefSize.height =  size.height;
				totalPrefHorizontalSpaceOfElements += size.width;
				totalPrefVerticalSpaceOfElements += size.height;
				}
			}
		if (getElementHSizing() == Mi_SAME_SIZE)
			{
			totalMinHorizontalSpaceOfElements = numberOfVisibleObjects * maxElementMinSize.width;
			totalPrefHorizontalSpaceOfElements = numberOfVisibleObjects * maxElementPrefSize.width;
			}
		if (getElementVSizing() == Mi_SAME_SIZE)
			{
			totalMinVerticalSpaceOfElements = numberOfVisibleObjects * maxElementMinSize.height;
			totalPrefVerticalSpaceOfElements = numberOfVisibleObjects * maxElementPrefSize.height;
			}

		if (isColumn)
			{
			minSize.width = maxElementMinSize.width;
			prefSize.width = maxElementPrefSize.width;

			minSize.height = totalMinVerticalSpaceOfElements 
				+ (numberOfVisibleObjects - 1) * getAlleyVSpacing();
			prefSize.height = totalPrefVerticalSpaceOfElements 
				+ (numberOfVisibleObjects - 1) * getAlleyVSpacing();
			}
		else
			{
			minSize.height = maxElementMinSize.height;
			prefSize.height = maxElementPrefSize.height;

			minSize.width = totalMinHorizontalSpaceOfElements 
				+ (numberOfVisibleObjects - 1) * getAlleyHSpacing();
			prefSize.width = totalPrefHorizontalSpaceOfElements 
				+ (numberOfVisibleObjects - 1) * getAlleyHSpacing();
			}
		minSize.addMargins(getInsetMargins(tmpMargins));
		prefSize.addMargins(getInsetMargins(tmpMargins));
		}
	public 		void		formatTarget(String connectionType)
		{
		}
	}

