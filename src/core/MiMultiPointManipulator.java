
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
public class MiMultiPointManipulator extends MiManipulator implements MiiActionHandler
	{
	public static 	int		HANDLE_PAD_DEVICE_WIDTH			= 8;
	protected static int		Mi_MOVING_POINT_HORIZONTALLY_ONLY_MASK 	= 0x100000;
	protected static int		Mi_MOVING_POINT_VERTICALLY_ONLY_MASK 	= 0x200000;

	public static 	MiAttributes	globalAttributes;

	private		MiEditor 	subjectEditor;
	private		MiPart 		subjectObj;
	private		int		draggedPtNum		= Mi_INVALID_POINT_NUMBER;
	private		MiDistance 	handlePadWidth;
	private		MiPoint		originalPoint		= new MiPoint();
	private		MiBounds	originalWorld		= new MiBounds();
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiPoint		tmpPoint		= new MiPoint();
	private		MiVector	tmpVector		= new MiVector();
	private		boolean		origNotificationsEnabled;

	static 	{
		globalAttributes = MiPart.getDefaultAttributes();
		globalAttributes = globalAttributes.setBackgroundColor("lightGray");
		globalAttributes = globalAttributes.setBorderLook(Mi_RAISED_BORDER_LOOK);
		}

	public				MiMultiPointManipulator(MiPart subject)
		{
		subjectObj = subject;
		setAttributes(globalAttributes);
		setCopyable(false);
		setCopyableAsPartOfCopyable(false);
		handlePadWidth = HANDLE_PAD_DEVICE_WIDTH;
		refreshBounds();
		}

	public		MiiManipulator create(MiPart subject)
		{
		return(new MiMultiPointManipulator(subject));
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
		subjectEditor = getContainingEditor();
		subjectEditor.appendActionHandler(this, Mi_EDITOR_WORLD_RESIZED_ACTION);
		}
	public		void		removeFromTarget()
		{
		subjectEditor.removeActionHandlers(this);
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
	public		boolean		pick(MiBounds area)
		{
		for (int i = 0; i < subjectObj.getNumberOfPoints(); ++i)
			{
			subjectObj.getPoint(i, tmpPoint);
			tmpBounds.setBounds(
				tmpPoint.x - handlePadWidth/2, tmpPoint.y - handlePadWidth/2,
				tmpPoint.x + handlePadWidth/2, tmpPoint.y + handlePadWidth/2);

			if (area.intersects(tmpBounds))
				return(true);
			}
		return(false);
		}

	public		void	 	draw(MiRenderer renderer)
		{
		if ((!isVisible()) || (subjectObj == null))
			return;

		reCalcBounds(null, renderer, tmpBounds);

		if (renderer.boundsClipped(tmpBounds))
			return;

		renderer.setAttributes(getAttributes());

		for (int i = 0; i < subjectObj.getNumberOfPoints(); ++i)
			{
			subjectObj.getPoint(i, tmpPoint);
			renderer.drawRect(
				tmpPoint.x - handlePadWidth/2, tmpPoint.y - handlePadWidth/2,
				tmpPoint.x + handlePadWidth/2, tmpPoint.y + handlePadWidth/2);
			}
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

			bounds.xmin -= handlePadWidth/2;
			bounds.ymin -= handlePadWidth/2;
			bounds.xmax += handlePadWidth/2;
			bounds.ymax += handlePadWidth/2;
			}
		}


	public		int	 	pickup(MiEvent event)
		{
		// Find control point rectangle under cursor...
		draggedPtNum = Mi_INVALID_POINT_NUMBER;
		MiBounds cursor = event.worldMouseFootPrint;
		for (int i = 0; i < subjectObj.getNumberOfPoints(); ++i)
			{
			subjectObj.getPoint(i, tmpPoint);
			if (cursor.intersects(new MiBounds(
				tmpPoint.x - handlePadWidth/2, tmpPoint.y - handlePadWidth/2,
				tmpPoint.x + handlePadWidth/2, tmpPoint.y + handlePadWidth/2)))
				{
				if (i == subjectObj.getNumberOfPoints() - 1)
					i = -1;
				draggedPtNum = i;

				origNotificationsEnabled
					= subjectObj.getOutgoingInvalidLayoutNotificationsEnabled();
				subjectObj.setOutgoingInvalidLayoutNotificationsEnabled(false);

				subjectObj.getPoint(draggedPtNum, originalPoint);
				event.editor.getWorldBounds(originalWorld);
				return(Mi_CONSUME_EVENT);
				}
			}
		// Now look for an edge itself under cursor, to allow the moving of 
		// edges by dragging on the edges themselves
		
		return(Mi_PROPOGATE_EVENT);
		}

	public		int 		drag(MiEvent event)
		{
		if (draggedPtNum == Mi_INVALID_POINT_NUMBER)
			return(Mi_PROPOGATE_EVENT);
	
		// Snap
		tmpPoint.copy(event.worldPt);
		event.editor.snapMovingPoint(tmpPoint);

		// Autopan
		tmpVector.copy(event.worldVector);
		tmpVector.add(tmpPoint.x - event.worldPt.x, tmpPoint.y - event.worldPt.y);
		tmpBounds.setBounds(tmpPoint);
		tmpBounds.translate(-tmpVector.x, -tmpVector.y);
		boolean modifiedDelta = event.editor.autopanForMovingObj(tmpBounds, tmpVector);
		if (modifiedDelta)
			{
			tmpPoint.translate(
				tmpVector.x - tmpPoint.x + event.worldPt.x,
				tmpVector.y - tmpPoint.y + event.worldPt.y);
			}

		// Set point
		subjectObj.setPoint(draggedPtNum, tmpPoint);
		subjectObj.validateLayout();
		return(Mi_CONSUME_EVENT);
		}

	public		int	 	drop(MiEvent event)
		{
		if (draggedPtNum == Mi_INVALID_POINT_NUMBER)
			return(Mi_PROPOGATE_EVENT);
	
		MiVector amountTranslated = new MiVector(
				subjectObj.getPoint(draggedPtNum).x - originalPoint.x,
				subjectObj.getPoint(draggedPtNum).y - originalPoint.y);

		MiEditor editor = event.editor;
		MiiTransaction transaction = new MiTranslatePointOfPartsCommand(
							editor, 
							subjectObj,
							amountTranslated,
							draggedPtNum);

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
		draggedPtNum = Mi_INVALID_POINT_NUMBER;
		subjectObj.setOutgoingInvalidLayoutNotificationsEnabled(origNotificationsEnabled);
		return(Mi_CONSUME_EVENT);
		}

	public		int 		getContextCursor(MiBounds cursor)
		{
		return(Mi_MOVE_CURSOR);
		}
	protected	void		setDraggedPointNumber(int num)
		{
		draggedPtNum = num;
		}
	protected	int		getDraggedPointNumber()
		{
		return(draggedPtNum);
		}
	}

