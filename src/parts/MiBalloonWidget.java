
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiBalloonWidget extends MiWidget
	{
	private		MiDistance	desiredDistanceFromTarget = 80;
	private		MiPart		contentContainer;
	private		MiList		textArea;
	private		MiRectangle	xSpacer;
	private		MiRectangle	ySpacer;
	private		MiColumnLayout 	columnLayout;
	private		MiPart		target;
	private		MiBalloon	shape;



	public				MiBalloonWidget(MiBounds balloonBounds)
		{
		setVisibleContainerAutomaticLayoutEnabled(false);
		xSpacer = new MiRectangle();
		xSpacer.setColor(Mi_TRANSPARENT_COLOR);

		ySpacer = new MiRectangle();
		ySpacer.setColor(Mi_TRANSPARENT_COLOR);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementVSizing(Mi_NONE);
		rowLayout.setElementHSizing(Mi_NONE);
		setLayout(rowLayout);

		columnLayout = new MiColumnLayout();
		columnLayout.setUniqueElementIndex(0);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		appendPart(columnLayout);

		textArea = new MiList();
		textArea.setFont(new MiFont("TimesRoman", MiFont.PLAIN, 14));
		textArea.getSortManager().setEnabled(false);
		textArea.setPreferredSize(new MiSize(500, 200));
		textArea.getSelectionManager().setBrowsable(false);
		textArea.getSelectionManager().setMaximumNumberSelected(0);
		MiScrolledBox scrolledBox = new MiScrolledBox(textArea);
		scrolledBox.getBox().setBackgroundColor(MiColorManager.transparent);
		scrolledBox.setBackgroundColor(MiColorManager.transparent);
		columnLayout.appendPart(scrolledBox);

		contentContainer = scrolledBox;
		}

	public		void		setDesiredDistanceFromTarget(MiDistance d)
		{
		desiredDistanceFromTarget = d;
		}
	public		MiDistance	getDesiredDistanceFromTarget()
		{
		return(desiredDistanceFromTarget);
		}

	public		MiList		getContentsList()
		{
		return(textArea);
		}
	public		MiPart		getContentContainer()
		{
		return(contentContainer);
		}
	public		void		setContentContainer(MiPart part)
		{
		contentContainer.replaceSelf(part);
		contentContainer = part;
		}
	public		void		setValue(String value)
		{
		setContents(new Strings(value));
		}
	public		String		getValue()
		{
		return(textArea.getValue());
		}
	public		void		setContents(Strings contents)
		{
		textArea.setContents(contents);
		}
	public		Strings		getContents()
		{
		return(textArea.getContents());
		}
	public		void		setSubject(MiPart target)
		{
		this.target = target;

		MiPolygon shape = makeShape(target);

		setSize(getPreferredSize(new MiSize()));
		setCenter(shape.getCenter());
		setShape(shape);

		validateLayout();
		}
	public		MiColumnLayout	getColumnLayout()
		{
		return(columnLayout);
		}
	protected	MiPolygon	makeShape(MiPart target)
		{
		MiWindow root = target.getRootWindow();
		MiBounds targetBounds = target.getBounds();
		MiBounds rootBounds = root.getWorldBounds();

		xSpacer.removeFromAllContainers();
		ySpacer.removeFromAllContainers();

		root.transformLocalWorldToRootWorld(targetBounds);
		MiBounds balloonBounds = new MiBounds(getPreferredSize(new MiSize()));

		if (shape == null)
			{
			shape = new MiBalloon(balloonBounds);
			}
		shape.setSubject(targetBounds, desiredDistanceFromTarget, rootBounds);

		MiMargins margins = shape.getBalloonContentInsetMargins();

		if (margins.top != 0)
			{
			ySpacer.setHeight(margins.top);
			columnLayout.insertPart(ySpacer, 0);
			columnLayout.setUniqueElementIndex(1);
			}
		if (margins.bottom != 0)
			{
			ySpacer.setHeight(margins.bottom);
			columnLayout.appendPart(ySpacer);
			columnLayout.setUniqueElementIndex(0);
			}
		if (margins.right != 0)
			{
			xSpacer.setWidth(margins.right);
			appendPart(xSpacer);
			}
		if (margins.left != 0)
			{
			xSpacer.setWidth(margins.left);
			insertPart(xSpacer, 0);
			}

		return(shape);
		}
	}
class MiBalloon extends MiPolygon
	{
	private		MiBounds	desiredBounds;
	private		MiMargins	margins		= new MiMargins();

	public				MiBalloon(MiBounds balloonBounds)
		{
		this.desiredBounds = balloonBounds;
		}
	public		MiMargins	getBalloonContentInsetMargins()
		{
		return(margins);
		}
	protected	void		buildBasicBalloon(MiBounds balloonBounds)
		{
		setNumberOfPoints(0);
		appendPoint(balloonBounds.getXmin(), balloonBounds.getYmin());
		appendPoint(balloonBounds.getXmax(), balloonBounds.getYmin());
		appendPoint(balloonBounds.getXmax(), balloonBounds.getYmax());
		appendPoint(balloonBounds.getXmin(), balloonBounds.getYmax());
		}
	public		void		setSubject(
						MiBounds targetBounds,
						MiDistance desiredDistanceFromTarget,
						MiBounds boundsBalloonRestrictedTo)
		{
		MiBounds balloonBounds = new MiBounds(desiredBounds);
	
		MiPoint targetCenter = targetBounds.getCenter();
		MiPoint pointOfContact = new MiPoint();

		margins.setMargins(0);

		// Find the area outside of target in which to put tour panel...
		int location = positionBoundsInsideRootNextToTarget(
						boundsBalloonRestrictedTo,
						targetBounds,
						desiredDistanceFromTarget,
						balloonBounds,
						pointOfContact);

//System.out.println("NOW balloonBounds = " + balloonBounds);

//System.out.println("location = " + location);

		switch (location)
			{
			case Mi_RIGHT_LOCATION		:
			case Mi_OUTSIDE_RIGHT_LOCATION	:
			case Mi_LEFT_LOCATION		:
			case Mi_OUTSIDE_LEFT_LOCATION	:

				if (pointOfContact.y - balloonBounds.getYmax() > desiredDistanceFromTarget)
					balloonBounds.translateYmaxTo(pointOfContact.y - desiredDistanceFromTarget);
				else if (balloonBounds.getYmin() - pointOfContact.y > desiredDistanceFromTarget)
					balloonBounds.translateYminTo(pointOfContact.y + desiredDistanceFromTarget);
	
				if (pointOfContact.y > balloonBounds.getYmax())
					{
					margins.top = pointOfContact.y - balloonBounds.getYmax();
					}
				else if (pointOfContact.y < balloonBounds.getYmin())
					{
					margins.bottom = balloonBounds.getYmin() - pointOfContact.y;
					}
				break;

			case Mi_TOP_LOCATION		:
			case Mi_OUTSIDE_TOP_LOCATION	:
			case Mi_BOTTOM_LOCATION		:
			case Mi_OUTSIDE_BOTTOM_LOCATION	:


				if (pointOfContact.x - balloonBounds.getXmax() > desiredDistanceFromTarget)
					balloonBounds.translateXmaxTo(pointOfContact.x - desiredDistanceFromTarget);
				else if (balloonBounds.getXmin() - pointOfContact.x > desiredDistanceFromTarget)
					balloonBounds.translateXminTo(pointOfContact.x + desiredDistanceFromTarget);

				if (pointOfContact.x > balloonBounds.getXmax())
					{
					margins.right = pointOfContact.x - balloonBounds.getXmax();
					}
				else if (pointOfContact.x < balloonBounds.getXmin())
					{
					margins.left = balloonBounds.getXmin() - pointOfContact.x;
					}

				break;

			}

		buildBasicBalloon(balloonBounds);

		// Tour panel location...
		switch (location)
			{
			case Mi_RIGHT_LOCATION		:
			case Mi_OUTSIDE_RIGHT_LOCATION	:

//System.out.println("RIGHT SIDE");
				margins.left = balloonBounds.getXmin() - pointOfContact.x;

				insertPoint(balloonBounds.getXmin(), 
					(balloonBounds.getYmax() + balloonBounds.getCenterY())/2, 4);
				insertPoint(pointOfContact, 5);
				insertPoint(balloonBounds.getXmin(), balloonBounds.getCenterY(), 6);
				break;

			case Mi_LEFT_LOCATION		:
			case Mi_OUTSIDE_LEFT_LOCATION	:

//System.out.println("LEFT SIDE");
				margins.right = pointOfContact.x - balloonBounds.getXmax();

				insertPoint(balloonBounds.getXmax(), balloonBounds.getCenterY(), 2);
				insertPoint(pointOfContact, 3);
				insertPoint(
					balloonBounds.getXmax(), 
					(balloonBounds.getYmax() + balloonBounds.getCenterY())/2, 4);
				break;

			case Mi_TOP_LOCATION		:
			case Mi_OUTSIDE_TOP_LOCATION	:

//System.out.println("TOP SIDE");
				margins.bottom = balloonBounds.getYmin() - pointOfContact.y;

				insertPoint(balloonBounds.getCenterX(), balloonBounds.getYmin(), 1);
				insertPoint(pointOfContact, 2);
				insertPoint(
					(balloonBounds.getXmax() + balloonBounds.getCenterX())/2,
					balloonBounds.getYmin(), 3);
				break;

			case Mi_BOTTOM_LOCATION		:
			case Mi_OUTSIDE_BOTTOM_LOCATION	:

//System.out.println("BOTTOM SIDE");
				margins.top = pointOfContact.y - balloonBounds.getYmax();

				insertPoint(
					(balloonBounds.getXmax() + balloonBounds.getCenterX())/2,
					balloonBounds.getYmax(), 3);
				insertPoint(pointOfContact, 4);
				insertPoint(balloonBounds.getCenterX(), balloonBounds.getYmax(), 5);
				break;
			}
		}
	protected	int		positionBoundsInsideRootNextToTarget(
						MiBounds rootBounds,
						MiBounds targetBounds,
						MiDistance desiredDistanceFromTarget,
						MiBounds boundsToPosition,
						MiPoint pointOfContact)
		{
		// Right side, totally outside target
		boundsToPosition.translateXminTo(targetBounds.getXmax() + desiredDistanceFromTarget);
		boundsToPosition.setCenterY(rootBounds.getCenterY());
		if (rootBounds.contains(boundsToPosition))
			{
			pointOfContact.x = targetBounds.getXmax();
			pointOfContact.y = targetBounds.getCenterY();
			return(Mi_OUTSIDE_RIGHT_LOCATION);
			}
//System.out.println("1");

		// Left side, totally outside target
		boundsToPosition.translateXmaxTo(targetBounds.getXmin() - desiredDistanceFromTarget);
		boundsToPosition.setCenterY(rootBounds.getCenterY());
		if (rootBounds.contains(boundsToPosition))
			{
			pointOfContact.x = targetBounds.getXmin();
			pointOfContact.y = targetBounds.getCenterY();
			return(Mi_OUTSIDE_LEFT_LOCATION);
			}
//System.out.println("2");
		
		// Top side, totally outside target
		boundsToPosition.translateYminTo(targetBounds.getYmax() + desiredDistanceFromTarget);
		boundsToPosition.setCenterX(rootBounds.getCenterX());
		if (rootBounds.contains(boundsToPosition))
			{
			pointOfContact.x = targetBounds.getCenterX();
			pointOfContact.y = targetBounds.getYmax();
			return(Mi_OUTSIDE_TOP_LOCATION);
			}
//System.out.println("3");
		
		// Bottom side, totally outside target
		boundsToPosition.translateYmaxTo(targetBounds.getYmin() - desiredDistanceFromTarget);
		boundsToPosition.setCenterX(rootBounds.getCenterX());
		if (rootBounds.contains(boundsToPosition))
			{
			pointOfContact.x = targetBounds.getCenterX();
			pointOfContact.y = targetBounds.getYmin();
			return(Mi_OUTSIDE_BOTTOM_LOCATION);
			}
//System.out.println("4");

		// Right side, overlapping target
		boundsToPosition.translateXminTo(targetBounds.getCenterX() + desiredDistanceFromTarget);
		boundsToPosition.setCenterY(rootBounds.getCenterY());
		if (rootBounds.contains(boundsToPosition))
			{
			pointOfContact.x = targetBounds.getCenterX();
			pointOfContact.y = targetBounds.getCenterY();
			return(Mi_RIGHT_LOCATION);
			}
//System.out.println("5");

		// Left side, overlapping target
		boundsToPosition.translateXmaxTo(targetBounds.getCenterX() - desiredDistanceFromTarget);
		boundsToPosition.setCenterY(rootBounds.getCenterY());
		if (rootBounds.contains(boundsToPosition))
			{
			pointOfContact.x = targetBounds.getCenterX();
			pointOfContact.y = targetBounds.getCenterY();
			return(Mi_LEFT_LOCATION);
			}
//System.out.println("6");
		
		// Top side, overlapping target
		boundsToPosition.translateYminTo(targetBounds.getCenterY() + desiredDistanceFromTarget);
		boundsToPosition.setCenterX(rootBounds.getCenterX());
		if (rootBounds.contains(boundsToPosition))
			{
			pointOfContact.x = targetBounds.getCenterX();
			pointOfContact.y = targetBounds.getCenterY();
			return(Mi_TOP_LOCATION);
			}
//System.out.println("7");
		
		// Bottom side, overlapping target
		boundsToPosition.translateYmaxTo(targetBounds.getCenterY() - desiredDistanceFromTarget);
		boundsToPosition.setCenterX(rootBounds.getCenterX());
		if (rootBounds.contains(boundsToPosition))
			{
			pointOfContact.x = targetBounds.getCenterX();
			pointOfContact.y = targetBounds.getCenterY();
			return(Mi_BOTTOM_LOCATION);
			}

System.out.println("FAILED TO POSITION TOUR STEP");
		pointOfContact.x = targetBounds.getCenterX();
		pointOfContact.y = targetBounds.getCenterY();
		return(Mi_BOTTOM_LOCATION);
		}
	}

