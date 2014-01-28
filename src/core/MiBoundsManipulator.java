
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
 * This class creates the 8 handles, arranged in a rectangle arround the bounds
 * of the subject (target) graphics part. It also manages the interactive behavior
 * of the handles, changing the size of the target part when the user drags on
 * the corresponding handles.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiBoundsManipulator extends MiManipulator implements MiiActionHandler
	{
	public static 	int		HANDLE_PAD_DEVICE_WIDTH	= 8;

	public static 	MiAttributes	globalAttributes;

	private		int		draggedPadLocation	= Mi_NONE;
	private		MiPart 		subjectObj;
	private		MiEditor 	containingEditor;
	private		MiDistance	handlePadWidth;
	private		MiBounds	subjectOriginalBounds	= new MiBounds();
	private		MiBounds	originalWorld		= new MiBounds();
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiVector	tmpVector		= new MiVector();
	private		MiSize		minSize			= new MiSize();
	private		MiSize		maxSize			= new MiSize();
	private		MiBounds	resizingBounds		= new MiBounds();
	private		MiPoint		cursor			= new MiPoint();
	protected	MiPoint		startDragOffsetFromCenter= new MiPoint();
	private		boolean		origNotificationsEnabled;


	static 	{
		globalAttributes = MiPart.getDefaultAttributes();
		globalAttributes = globalAttributes.setBackgroundColor("lightGray");
		globalAttributes = globalAttributes.setBorderLook(Mi_RAISED_BORDER_LOOK);
		}

	public				MiBoundsManipulator(MiPart subject)
		{
		subjectObj = subject;
		setAttributes(globalAttributes);
		setCopyable(false);
		setCopyableAsPartOfCopyable(false);
		setSelectable(false);
		handlePadWidth = HANDLE_PAD_DEVICE_WIDTH;
		refreshBounds();
		}
	public		MiiManipulator	create(MiPart subject)
		{
		return(new MiBoundsManipulator(subject));
		}
	public static	void		setGlobalHandleAttributes(MiAttributes atts)
		{
		globalAttributes = atts;
		}
	public static	MiAttributes	getGlobalHandleAttributes()
		{
		return(globalAttributes);
		}

	public		void		attachToTarget(String tag)
		{
		subjectObj.appendAttachment(this, Mi_NONE, tag, null);
		containingEditor = getContainingEditor();
		containingEditor.appendActionHandler(this, Mi_EDITOR_WORLD_RESIZED_ACTION);
		}
	public		void		removeFromTarget()
		{
		containingEditor.removeActionHandlers(this);
		subjectObj.removeAttachment(this);
		}
	public		MiPart		getSubject()
		{
		return(subjectObj);
		}
					// Zooming in and out requires that we reCalc the bounds of the handles
	public		boolean		processAction(MiiAction action)
		{
		invalidateLayout();
		return(true);
		}
	public		void	 	render(MiRenderer renderer)
		{
		if ((!isVisible()) || (subjectObj == null))
			return;

		reCalcBounds(null, renderer, tmpBounds);

		if (renderer.boundsClipped(tmpBounds))
			return;

		renderer.setAttributes(getAttributes());
		renderer.drawRect(
			tmpBounds.xmin, 
			tmpBounds.ymin, 
			tmpBounds.xmin + handlePadWidth, 
			tmpBounds.ymin + handlePadWidth);

		renderer.drawRect(
			tmpBounds.xmin,
			tmpBounds.getCenterY() - handlePadWidth/2,
			tmpBounds.xmin + handlePadWidth,
			tmpBounds.getCenterY() + handlePadWidth/2);

		renderer.drawRect(
			tmpBounds.xmin,
			tmpBounds.ymax - handlePadWidth,
			tmpBounds.xmin + handlePadWidth,
			tmpBounds.ymax);

		renderer.drawRect(
			tmpBounds.xmax - handlePadWidth,
			tmpBounds.ymin,
			tmpBounds.xmax,
			tmpBounds.ymin + handlePadWidth);

		renderer.drawRect(
			tmpBounds.xmax - handlePadWidth,
			tmpBounds.getCenterY() - handlePadWidth/2,
			tmpBounds.xmax,
			tmpBounds.getCenterY() + handlePadWidth/2);

		renderer.drawRect(
			tmpBounds.xmax - handlePadWidth,
			tmpBounds.ymax - handlePadWidth,
			tmpBounds.xmax,
			tmpBounds.ymax);

		renderer.drawRect(
			tmpBounds.getCenterX() - handlePadWidth/2, 
			tmpBounds.ymax - handlePadWidth,
			tmpBounds.getCenterX() + handlePadWidth/2,
			tmpBounds.ymax);

		renderer.drawRect(
			tmpBounds.getCenterX() - handlePadWidth/2, 
			tmpBounds.ymin,
			tmpBounds.getCenterX() + handlePadWidth/2, 
			tmpBounds.ymin + handlePadWidth);
		}

	public		void	 	reCalcBounds(MiBounds bounds)
		{
		reCalcBounds(getContainingEditor(), null, bounds);
		}
	protected	void	 	reCalcBounds(MiEditor editor, MiRenderer renderer, MiBounds bounds)
		{
		tmpBounds.setCenterX(subjectObj.getCenterX());
		tmpBounds.setCenterY(subjectObj.getCenterY());
		tmpBounds.setWidth(HANDLE_PAD_DEVICE_WIDTH);
		tmpBounds.setHeight(HANDLE_PAD_DEVICE_WIDTH);
		if (renderer != null)
			{
			renderer.getTransform().dtow(tmpBounds, tmpBounds);
			}
		else if ((editor != null) && (subjectObj != null))
			{
			editor.getViewport().getTransform().dtow(tmpBounds, tmpBounds);
			editor.transformToChildSpace(subjectObj, tmpBounds, tmpBounds);
			}
		handlePadWidth = tmpBounds.getWidth();

		if (subjectObj == null)
			{
			bounds.reverse();
			}
		else
			{
			if (!subjectObj.hasValidLayout())
				subjectObj.reCalcBounds(bounds);
			else
				subjectObj.getBounds(bounds);

			bounds.xmin -= handlePadWidth;
			bounds.ymin -= handlePadWidth;
			bounds.xmax += handlePadWidth;
			bounds.ymax += handlePadWidth;
			}
		}
	public		int	 	pickup(MiEvent event)
		{
		// Find control point rectangle under cursor...
		draggedPadLocation = getPadUnderCursor(event.worldMouseFootPrint);

		if (draggedPadLocation == Mi_NONE)
			{
			return(Mi_PROPOGATE_EVENT);
			}

		subjectObj.getBounds(subjectOriginalBounds);
		event.editor.getWorldBounds(originalWorld);
		startDragOffsetFromCenter.set(
			event.worldPt.x - subjectObj.getCenterX(),
			event.worldPt.y - subjectObj.getCenterY());

		origNotificationsEnabled = subjectObj.getOutgoingInvalidLayoutNotificationsEnabled();
		subjectObj.setOutgoingInvalidLayoutNotificationsEnabled(false);

		return(Mi_CONSUME_EVENT);
		}

	public		int 		drag(MiEvent event)
		{
		if (draggedPadLocation == Mi_NONE)
			return(Mi_PROPOGATE_EVENT);
	
		cursor.copy(event.worldPt);
		resizingBounds.copy(subjectOriginalBounds);

		if (draggedPadLocation != Mi_CENTER_LOCATION)
			{
			event.editor.snapMovingPoint(cursor);

			tmpVector.copy(event.worldVector);
			tmpVector.add(cursor.x - event.worldPt.x, cursor.y - event.worldPt.y);
			tmpBounds.setBounds(cursor);
			tmpBounds.translate(-tmpVector.x, -tmpVector.y);
			boolean modifiedDelta = event.editor.autopanForMovingObj(tmpBounds, tmpVector);
			if (modifiedDelta)
				{
				cursor.translate(
					tmpVector.x - cursor.x + event.worldPt.x,
					tmpVector.y - cursor.y + event.worldPt.y);
				}
			}

		subjectObj.getMinimumSize(minSize);

//System.out.println("111 minSize.width = " + minSize.width);
		if (subjectObj.getMinimumWidth() > minSize.getWidth())
			minSize.setWidth(subjectObj.getMinimumWidth());
		if (subjectObj.getMinimumHeight() > minSize.getHeight())
			minSize.setHeight(subjectObj.getMinimumHeight());

//System.out.println("222 minSize.width = " + minSize.width);
		maxSize.setWidth(subjectObj.getMaximumWidth());
		maxSize.setHeight(subjectObj.getMaximumHeight());
				
		switch (draggedPadLocation)
			{
			case Mi_LOWER_LEFT_LOCATION :
				resizingBounds.xmin = cursor.x;
				resizingBounds.ymin = cursor.y;
			
				resizingBounds.assureMinsLessThanMaxs();

				if (resizingBounds.getWidth() < minSize.width)
					resizingBounds.xmin = resizingBounds.xmax - minSize.width;
				if (resizingBounds.getHeight() < minSize.height)
					resizingBounds.ymin = resizingBounds.ymax - minSize.height;

				if (resizingBounds.getWidth() > maxSize.width)
					resizingBounds.xmin = resizingBounds.xmax - maxSize.width;
				if (resizingBounds.getHeight() > maxSize.height)
					resizingBounds.ymin = resizingBounds.ymax - maxSize.height;

				break;
			case Mi_LEFT_LOCATION :
				resizingBounds.xmin = cursor.x;

				resizingBounds.assureMinsLessThanMaxs();

				if (resizingBounds.getWidth() < minSize.width)
					resizingBounds.xmin = resizingBounds.xmax - minSize.width;

				if (resizingBounds.getHeight() > maxSize.height)
					resizingBounds.ymin = resizingBounds.ymax - maxSize.height;

				break;
			case Mi_UPPER_LEFT_LOCATION :
				resizingBounds.xmin = cursor.x;
				resizingBounds.ymax = cursor.y;

				resizingBounds.assureMinsLessThanMaxs();

				if (resizingBounds.getWidth() < minSize.width)
					resizingBounds.xmin = resizingBounds.xmax - minSize.width;
				if (resizingBounds.getHeight() < minSize.height)
					resizingBounds.ymax = resizingBounds.ymin + minSize.height;

				if (resizingBounds.getWidth() > maxSize.width)
					resizingBounds.xmin = resizingBounds.xmax - maxSize.width;
				if (resizingBounds.getHeight() > maxSize.height)
					resizingBounds.ymax = resizingBounds.ymax + maxSize.height;

				break;
			case Mi_LOWER_RIGHT_LOCATION :
				resizingBounds.xmax = cursor.x;
				resizingBounds.ymin = cursor.y;

				resizingBounds.assureMinsLessThanMaxs();

//System.out.println("minSize.width = " + minSize.width);
				if (resizingBounds.getWidth() < minSize.width)
					resizingBounds.xmax = resizingBounds.xmin + minSize.width;
				if (resizingBounds.getHeight() < minSize.height)
					resizingBounds.ymin = resizingBounds.ymax - minSize.height;
//System.out.println("resizingBounds.getWidth() = " + resizingBounds.getWidth());

				if (resizingBounds.getWidth() > maxSize.width)
					resizingBounds.xmax = resizingBounds.xmin + maxSize.width;
				if (resizingBounds.getHeight() > maxSize.height)
					resizingBounds.ymin = resizingBounds.ymax - maxSize.height;

				break;
			case Mi_RIGHT_LOCATION :
				resizingBounds.xmax = cursor.x;

				resizingBounds.assureMinsLessThanMaxs();

				if (resizingBounds.getWidth() < minSize.width)
					resizingBounds.xmax = resizingBounds.xmin + minSize.width;

				if (resizingBounds.getWidth() > maxSize.width)
					resizingBounds.xmax = resizingBounds.xmin + maxSize.width;

				break;
			case Mi_UPPER_RIGHT_LOCATION :
				resizingBounds.xmax = cursor.x;
				resizingBounds.ymax = cursor.y;

				resizingBounds.assureMinsLessThanMaxs();

				if (resizingBounds.getWidth() < minSize.width)
					resizingBounds.xmax = resizingBounds.xmin + minSize.width;
				if (resizingBounds.getHeight() < minSize.height)
					resizingBounds.ymax = resizingBounds.ymin + minSize.height;

				if (resizingBounds.getWidth() > maxSize.width)
					resizingBounds.xmax = resizingBounds.xmin + maxSize.width;
				if (resizingBounds.getHeight() > maxSize.height)
					resizingBounds.ymax = resizingBounds.ymin + maxSize.height;

				break;
			case Mi_TOP_LOCATION :
				resizingBounds.ymax = cursor.y;

				resizingBounds.assureMinsLessThanMaxs();

				if (resizingBounds.getHeight() < minSize.height)
					resizingBounds.ymax = resizingBounds.ymin + minSize.height;

				if (resizingBounds.getHeight() > maxSize.height)
					resizingBounds.ymax = resizingBounds.ymin + maxSize.height;

				break;
			case Mi_BOTTOM_LOCATION :
				resizingBounds.ymin = cursor.y;

				resizingBounds.assureMinsLessThanMaxs();

				if (resizingBounds.getHeight() < minSize.height)
					resizingBounds.ymin = resizingBounds.ymax - minSize.height;

				if (resizingBounds.getHeight() > maxSize.height)
					resizingBounds.ymin = resizingBounds.ymax - maxSize.height;

				break;
			case Mi_CENTER_LOCATION :
				if (subjectObj.isMovable())
					{
					// Translate and draw.
					tmpVector.x = event.worldPt.x 
						- (subjectObj.getCenterX() + startDragOffsetFromCenter.x);
					tmpVector.y = event.worldPt.y
						- (subjectObj.getCenterY() + startDragOffsetFromCenter.y);

					event.editor.snapMovingPart(subjectObj, tmpVector);

					boolean modifiedDelta = event.editor.autopanForMovingObj(
						subjectObj.getDrawBounds(tmpBounds), tmpVector);

					if (!tmpVector.isZero())
						subjectObj.translate(tmpVector);
					}
				break;
			case Mi_NONE :
			default:
				break;
			}
			
		if (draggedPadLocation != Mi_CENTER_LOCATION)
			{
			if ((subjectObj.hasFixedWidth()) && (subjectObj.hasFixedHeight()))
				return(Mi_CONSUME_EVENT);

			if (subjectObj.hasFixedWidth())
				{
				resizingBounds.xmin = subjectOriginalBounds.xmin;
				resizingBounds.xmax = subjectOriginalBounds.xmax;
				}
			if (subjectObj.hasFixedHeight())
				{
				resizingBounds.ymin = subjectOriginalBounds.ymin;
				resizingBounds.ymax = subjectOriginalBounds.ymax;
				}

			if (subjectObj.hasFixedAspectRatio())
				{
				if ((subjectObj.hasFixedWidth()) || (subjectObj.hasFixedHeight()))
					{
					resizingBounds.copy(subjectOriginalBounds);
					}
				else
					{
					double aspectRatio 
						= subjectOriginalBounds.getHeight()/subjectOriginalBounds.getWidth();

					MiCoord xmin = resizingBounds.getXmax() 
							- resizingBounds.getHeight()/aspectRatio;
					MiCoord ymin = resizingBounds.getYmax() 
							- resizingBounds.getWidth() * aspectRatio;
					MiCoord xmax = resizingBounds.getXmin() 
							+ resizingBounds.getHeight()/aspectRatio;
					MiCoord ymax = resizingBounds.getYmin() 
							+ resizingBounds.getWidth() * aspectRatio;

					switch (draggedPadLocation)
						{
						case Mi_LOWER_LEFT_LOCATION :
							// Change ymin to be greater/lesser than where the
							// users cursor put it in order to maintain aspect
							// ratio if the cursor.y lies within
							// the y bounds of the result. Otherwise modify the
							// xmin which will get the cursor on the bottom of 
							// the bounds.
							if (cursor.y >= ymin)
								resizingBounds.setYmin(ymin);
							else
								resizingBounds.setXmin(xmin);
							break;

						case Mi_LOWER_RIGHT_LOCATION :
							if (cursor.y >= ymin)
								resizingBounds.setYmin(ymin);
							else
								resizingBounds.setXmax(xmax);
							break;

						case Mi_UPPER_RIGHT_LOCATION :
							if (cursor.y <= ymax)
								resizingBounds.setYmax(ymax);
							else
								resizingBounds.setXmax(xmax);
							break;

						case Mi_UPPER_LEFT_LOCATION :
							if (cursor.y <= ymax)
								resizingBounds.setYmax(ymax);
							else
								resizingBounds.setXmin(xmin);
							break;
						case Mi_LEFT_LOCATION :
						case Mi_RIGHT_LOCATION :
							resizingBounds.setYmax(ymax);
							break;
						case Mi_TOP_LOCATION :
						case Mi_BOTTOM_LOCATION :
							resizingBounds.setXmax(xmax);
							break;
						}

//System.out.println("ORIG ASPCET RATIO = " + aspectRatio);
//System.out.println("Old Height = " + subjectOriginalBounds.getHeight());
//System.out.println("New Height = " + resizingBounds.getHeight());
//System.out.println("Old Width = " + subjectOriginalBounds.getWidth());
//System.out.println("New Width = " + resizingBounds.getWidth());
/****

					// ---------------------------------------------------------------
					// Minimize the amount we must correct the particular pad that the
					// user is dragging while still maintaining the aspect ratio
					// ---------------------------------------------------------------
					MiCoord xmin = resizingBounds.getXmax() 
							- resizingBounds.getHeight()/aspectRatio;
					MiCoord ymin = resizingBounds.getYmax() 
							- resizingBounds.getWidth() * aspectRatio;
					MiCoord xmax = resizingBounds.getXmin() 
							+ resizingBounds.getHeight()/aspectRatio;
					MiCoord ymax = resizingBounds.getYmin() 
							+ resizingBounds.getWidth() * aspectRatio;
					MiPoint proposedPadPt1 = new MiPoint();
					MiPoint proposedPadPt2 = new MiPoint();
					switch (draggedPadLocation)
						{
						case Mi_UPPER_LEFT_LOCATION :
							proposedPadPt1.set(xmin, resizingBounds.getYmax());
							proposedPadPt2.set(resizingBounds.getXmin(), ymax);
							if ((cursor.getDistanceSquared(proposedPadPt1))
								< (cursor.getDistanceSquared(proposedPadPt2)))
								{
								resizingBounds.setXmin(xmin);
								}
							else
								{
								resizingBounds.setYmax(ymax);
								}
							break;

						case Mi_LOWER_LEFT_LOCATION :
							proposedPadPt1.set(xmin, resizingBounds.getYmin());
							proposedPadPt2.set(resizingBounds.getXmin(), ymin);
							if ((cursor.getDistanceSquared(proposedPadPt1))
								< (cursor.getDistanceSquared(proposedPadPt2)))
								{
								resizingBounds.setXmin(xmin);
								}
							else
								{
								resizingBounds.setYmin(ymin);
								}
							break;

						case Mi_UPPER_RIGHT_LOCATION :
							proposedPadPt1.set(xmax, resizingBounds.getYmax());
							proposedPadPt2.set(resizingBounds.getXmax(), ymax);
							if ((cursor.getDistanceSquared(proposedPadPt1))
								< (cursor.getDistanceSquared(proposedPadPt2)))
								{
								resizingBounds.setXmax(xmax);
								}
							else
								{
								resizingBounds.setYmax(ymax);
								}
							break;

						case Mi_LOWER_RIGHT_LOCATION :
							proposedPadPt1.set(xmax, resizingBounds.getYmin());
							proposedPadPt2.set(resizingBounds.getXmax(), ymin);

System.out.println("Distance to new Xmax" + cursor.getDistanceSquared(proposedPadPt1));
System.out.println("Distance to new Ymin" + cursor.getDistanceSquared(proposedPadPt2));

							if ((cursor.getDistanceSquared(proposedPadPt1))
								< (cursor.getDistanceSquared(proposedPadPt2)))
								{
System.out.println("Modifying Xmax");
System.out.println("Xmax was" + resizingBounds.getXmax());
System.out.println("Modifying Xmax");
								resizingBounds.setXmax(xmax);
								}
							else
								{
System.out.println("Modifying YMIN");
								resizingBounds.setYmin(ymin);
								}
							break;
						case Mi_LEFT_LOCATION :
						case Mi_RIGHT_LOCATION :
							resizingBounds.setYmax(ymax);
							break;
						case Mi_TOP_LOCATION :
						case Mi_BOTTOM_LOCATION :
							resizingBounds.setXmax(xmax);
							break;
						}
****/

/****
					MiVector v = event.getWorldVector(new MiVector());
					// Arbitrarily slave the dimension with the smallest change to the other
					if ((Math.abs(v.y)) > (Math.abs(v.x)))
						{
System.out.println("Height has changed the most " + (resizingBounds.getHeight() - subjectOriginalBounds.getHeight()));
						// Height has changed the most. Modify xmin?
						if ((draggedPadLocation == Mi_UPPER_LEFT_LOCATION)
							|| (draggedPadLocation == Mi_LOWER_LEFT_LOCATION))
							{
							resizingBounds.setXmin(resizingBounds.getXmax() 
								- resizingBounds.getHeight()/aspectRatio);
							}
						else 
							{
							// No, modify xmax
							resizingBounds.setXmax(resizingBounds.getXmin() 
								+ resizingBounds.getHeight()/aspectRatio);
							}
						}
					// Width has changed more. Has either changed at all?
					else if (v.x != 0)
						{
						// Yes. Modify ymin?
						if ((draggedPadLocation == Mi_LOWER_LEFT_LOCATION)
							|| (draggedPadLocation == Mi_LOWER_RIGHT_LOCATION))
							{
							resizingBounds.setYmin(resizingBounds.getYmax() 
								- resizingBounds.getWidth() * aspectRatio);
							}
						else 
							{
							// No, modify ymax
							resizingBounds.setYmax(resizingBounds.getYmin() 
								+ resizingBounds.getWidth() * aspectRatio);
							}
						}
					if ((Math.abs(v.y)) > (Math.abs(v.x)))
						{
System.out.println("Height has changed the most " + (resizingBounds.getHeight() - subjectOriginalBounds.getHeight()));
						// Height has changed the most. Modify xmin?
						if ((draggedPadLocation == Mi_UPPER_LEFT_LOCATION)
							|| (draggedPadLocation == Mi_LOWER_LEFT_LOCATION))
							{
							resizingBounds.setXmin(resizingBounds.getXmax() 
								- resizingBounds.getHeight()/aspectRatio);
							}
						else 
							{
							// No, modify xmax
							resizingBounds.setXmax(resizingBounds.getXmin() 
								+ resizingBounds.getHeight()/aspectRatio);
							}
						}
					// Width has changed more. Has either changed at all?
					else if (v.x != 0)
						{
						// Yes. Modify ymin?
						if ((draggedPadLocation == Mi_LOWER_LEFT_LOCATION)
							|| (draggedPadLocation == Mi_LOWER_RIGHT_LOCATION))
							{
							resizingBounds.setYmin(resizingBounds.getYmax() 
								- resizingBounds.getWidth() * aspectRatio);
							}
						else 
							{
							// No, modify ymax
							resizingBounds.setYmax(resizingBounds.getYmin() 
								+ resizingBounds.getWidth() * aspectRatio);
							}
						}
****/
//System.out.println("NEW ASPCET RATIO = " + resizingBounds.getHeight()/resizingBounds.getWidth());
					}
				}

			// ---------------------------------------------------------------
			// If the height or width is zero, and the target has parts, then
			// make the height or width > 0, cause if the parts of the targets
			// are scaled to zero, they can never be expanded again (and scaleParts
			// will throw an exception). The programmer can avoid this kludge by
			// setting a minimumWidth and minimumHeight on the target (i.e. subjectObj).
			// ---------------------------------------------------------------
			if ((resizingBounds.getWidth() == 0) && (subjectObj.getNumberOfParts() > 0))
				resizingBounds.setWidth(0.001);
			if ((resizingBounds.getHeight() == 0) && (subjectObj.getNumberOfParts() > 0))
				resizingBounds.setHeight(0.001);

//System.out.println("FINAL ASPCET RATIO = " + resizingBounds.getHeight()/resizingBounds.getWidth());
//System.out.println("subjectObj.getBounds() = " + subjectObj.getBounds());
//System.out.println("FINAL resizingBounds.getWidth() = " + resizingBounds.getWidth());
//System.out.println("FINAL subjectObj = " + subjectObj);
			subjectObj.setBounds(resizingBounds);
//System.out.println("FINAL subjectObj.getBounds() = " + subjectObj.getBounds());
			}
	
		subjectObj.validateLayout();
		return(Mi_CONSUME_EVENT);
		}

	public		int	 	drop(MiEvent event)
		{
		if (draggedPadLocation == Mi_NONE)
			return(Mi_PROPOGATE_EVENT);

		MiVector amountTranslated = new MiVector(
				subjectObj.getCenterX() - subjectOriginalBounds.getCenterX(),
				subjectObj.getCenterY() - subjectOriginalBounds.getCenterY());

		MiEditor editor = event.editor;
		MiiTransaction transaction = new MiScalePartsCommand(
			editor, 
			new MiScale(
				subjectObj.getWidth()/subjectOriginalBounds.getWidth(),
				subjectObj.getHeight()/subjectOriginalBounds.getHeight()),
			subjectObj.getCenter(),
			amountTranslated,
			new MiParts(subjectObj),
			subjectOriginalBounds.getCenter());

		if (!originalWorld.equals(editor.getWorldBounds()))
			{
			MiSystem.getTransactionManager().appendTransaction(
				transaction.getName(),
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()),
				transaction);
			// Add this for the previousView and nextView command stack
			MiSystem.getViewportTransactionManager().appendTransaction(
				new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
			}
		else
			{
			MiSystem.getTransactionManager().appendTransaction(transaction);
			}
		draggedPadLocation = Mi_NONE;
		subjectObj.setOutgoingInvalidLayoutNotificationsEnabled(origNotificationsEnabled);
		return(Mi_CONSUME_EVENT);
		}

	private		int		getPadUnderCursor(MiBounds cursor)
		{
		if (!cursor.intersects(getBounds()))
			return(Mi_NONE);

		if (subjectObj.getBounds().contains(cursor))
			return(Mi_CENTER_LOCATION);

		int pad = Mi_NONE;
		MiBounds subjBounds = subjectObj.getBounds();
		MiCoord centerX = subjBounds.getCenterX();
		MiCoord centerY = subjBounds.getCenterY();
		if (cursor.xmax > subjBounds.xmax) 
			{
			if (cursor.ymax > subjBounds.ymax)
				pad = Mi_UPPER_RIGHT_LOCATION;
			else if (cursor.ymin < subjBounds.ymin)
				pad = Mi_LOWER_RIGHT_LOCATION;
			else if ((cursor.ymin < centerY + handlePadWidth/2)
				&& (cursor.ymax > centerY - handlePadWidth/2))
				{
				pad = Mi_RIGHT_LOCATION;
				}
			}
		else if (cursor.xmin < subjBounds.xmin) 
			{
			if (cursor.ymax > subjBounds.ymax)
				pad = Mi_UPPER_LEFT_LOCATION;
			else if (cursor.ymin < subjBounds.ymin)
				pad = Mi_LOWER_LEFT_LOCATION;
			else if ((cursor.ymin < centerY + handlePadWidth/2)
				&& (cursor.ymax > centerY - handlePadWidth/2))
				{
				pad = Mi_LEFT_LOCATION;
				}
			}
		else if ((cursor.ymin < subjBounds.ymin)
			&& ((cursor.xmin < centerX + handlePadWidth/2)
			&& (cursor.xmax > centerX - handlePadWidth/2)))
			{
			pad = Mi_BOTTOM_LOCATION;
			}
		else if ((cursor.ymax > subjBounds.ymax)
			&& ((cursor.xmin < centerX + handlePadWidth/2)
			&& (cursor.xmax > centerX - handlePadWidth/2)))
			{
			pad = Mi_TOP_LOCATION;
			}
		return(pad);
		}
	public		int 		getContextCursor(MiBounds cursor)
		{
		int location = getPadUnderCursor(cursor);
		int image = Mi_MOVE_CURSOR;
		switch (location)
			{
			case Mi_LOWER_LEFT_LOCATION :
				image = Mi_SW_RESIZE_CURSOR;
				break;
			case Mi_LEFT_LOCATION :
				image = Mi_W_RESIZE_CURSOR;
				break;
			case Mi_UPPER_LEFT_LOCATION :
				image = Mi_NW_RESIZE_CURSOR;
				break;
			case Mi_LOWER_RIGHT_LOCATION :
				image = Mi_SE_RESIZE_CURSOR;
				break;
			case Mi_RIGHT_LOCATION :
				image = Mi_E_RESIZE_CURSOR;
				break;
			case Mi_UPPER_RIGHT_LOCATION :
				image = Mi_NE_RESIZE_CURSOR;
				break;
			case Mi_TOP_LOCATION :
				image = Mi_N_RESIZE_CURSOR;
				break;
			case Mi_BOTTOM_LOCATION :
				image = Mi_S_RESIZE_CURSOR;
				break;
			case Mi_CENTER_LOCATION :
				image = Mi_MOVE_CURSOR;
				break;
			case Mi_NONE :
				break;
			default:
				break;
			}
		return(image);
		}
	}

