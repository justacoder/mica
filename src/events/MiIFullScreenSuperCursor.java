
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
import java.util.ArrayList; 
import com.swfm.mica.util.IntVector; 

/**
 * This class provides the full screen cursor and adds additional functionalty.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIFullScreenSuperCursor extends MiEventMonitor implements MiiTypes
	{
	public static final String	Mi_MOVE_LEFT_COMMAND_NAME	= "MoveLeft";
	public static final String	Mi_MOVE_RIGHT_COMMAND_NAME	= "MoveRight";
	public static final String	Mi_MOVE_DOWN_COMMAND_NAME	= "MoveDown";
	public static final String	Mi_MOVE_UP_COMMAND_NAME		= "MoveUp";

	private		MiDistance	MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH	= 0.000001;
	private		MiPart		prototypeLine 		= new MiLine();
	private		MiPart		prototypeBullsEye 	= new MiCircle(new MiBounds(0, 0, 20, 20));
	private		MiPart		cursor;
	private		MiPart		topLine;
	private		MiPart		bottomLine;
	private		MiPart		leftLine;
	private		MiPart		rightLine;
	private		MiPart		bullsEye;
	private		boolean		visible;
	private		boolean		cursorIsAttached;
	private		MiPoint		cursorPt		= new MiPoint();
	private		MiBounds	editorWorld		= new MiBounds();
	private		MiBounds	cursorWorldBounds	= new MiBounds();
	private		ArrayList	showOnlyDuringTheseEventStates;
	private		ArrayList	hideDuringTheseEventStates;

	private		MiParts		cursorAffordanceParts	= new MiParts();
	private		MiAttributes	attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint = new MiAttributes().setLineWidth(2);
	private		MiPart		prototypeAlignedPointLocatorGraphics;
	private		MiPoint		tmpPoint		= new MiPoint();
	private		MiPoint		tmpPoint2		= new MiPoint();
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiEvent		event;
	private		MiEditor	editor;
	private		MiEvent		tmpEvent		= new MiEvent(0, 0, 0, true);

	private		String			direction;
	private		MiVector		delta 			= new MiVector();
	private		MiVector		deviceDelta 		= new MiVector(10, 10);
	private		MiPoint			devicePt 		= new MiPoint();

	public				MiIFullScreenSuperCursor()
		{
		init();
		}
	public				MiIFullScreenSuperCursor(Color color)
		{
		prototypeLine.setColor(color);
		prototypeBullsEye.setColor(color);
		init();
		}
	public				MiIFullScreenSuperCursor(MiPart prototypeLine, MiPart prototypeBullsEye)
		{
		this.prototypeLine = prototypeLine;
		this.prototypeBullsEye = prototypeBullsEye;
		init();
		}

	public		void		setPrototypeLine(MiPart line)
		{
		this.prototypeLine = line;
		}
	public		MiPart		getPrototypeLine()
		{
		return(prototypeLine);
		}
	public		void		setPrototypeBullsEye(MiPart bullsEye)
		{
		this.prototypeBullsEye = bullsEye;
		}
	public		MiPart		getPrototypeBullsEye()
		{
		return(prototypeBullsEye);
		}

	public		void		setShowOnlyDuringTheseEventStates(ArrayList eventMouseAndModifierButtonStates)
		{
		this.showOnlyDuringTheseEventStates = eventMouseAndModifierButtonStates;
		}
	public		ArrayList	getShowOnlyDuringTheseEventStates()
		{
		return(showOnlyDuringTheseEventStates);
		}
	public		void		setHideDuringTheseEventStates(ArrayList eventMouseAndModifierButtonStates)
		{
		this.hideDuringTheseEventStates = eventMouseAndModifierButtonStates;
		}
	public		ArrayList	getHideDuringTheseEventStates()
		{
		return(hideDuringTheseEventStates);
		}
	

	protected	void		init()
		{
		addEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_LEFT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_RIGHT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_DOWN_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_DOWN_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_UP_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, Mi_UP_ARROW_KEY, 0);

		addEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, 
			Mi_KEY_RELEASE_EVENT, Mi_LEFT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_RIGHT_COMMAND_NAME, 
			Mi_KEY_RELEASE_EVENT, Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_DOWN_COMMAND_NAME, 
			Mi_KEY_RELEASE_EVENT, Mi_DOWN_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_MOVE_UP_COMMAND_NAME, 
			Mi_KEY_RELEASE_EVENT, Mi_UP_ARROW_KEY, 0);


		prototypeAlignedPointLocatorGraphics = new MiCircle(3);
		prototypeAlignedPointLocatorGraphics.setBackgroundColor("green");

		attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint 
			= attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint.setColor("green");
		attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint
			= attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint.setBackgroundColor("green");

		cursor = makeCursor();
		setVisible(false);
		}

	public		int		processEvent(MiEvent event)
		{
		this.event = event;
		this.editor = event.editor;

		if ((event.type == Mi_WINDOW_EXIT_EVENT) || (event.type == Mi_MOUSE_EXIT_EVENT))
			{
			setVisible(false);
			}
		else if ((event.type != Mi_IDLE_EVENT) 
			&& (event.type != Mi_TIMER_TICK_EVENT)
			&& (event.type != Mi_NO_LONGER_IDLE_EVENT))
			{
//MiDebug.println("event=" + event);
			boolean makeVisible = true;
			if (showOnlyDuringTheseEventStates != null)
				{
				for (int i = 0; i < showOnlyDuringTheseEventStates.size(); ++i)
					{
					makeVisible = false;
//MiDebug.println("makeVisible=" + makeVisible);
					MiEvent validEvent = (MiEvent )showOnlyDuringTheseEventStates.get(i);
					if (event.equalsEventType(validEvent))
/*
					if (((validEvent.getModifiers() == event.getModifiers())
						|| (validEvent.getModifiers() == Mi_ANY_MODIFIERS_HELD_DOWN))
						&& (validEvent.getMouseButtonState() == event.getMouseButtonState()))
*/
						{
						makeVisible = true;
//MiDebug.println("exit makeVisible=" + makeVisible);
						break;
						}
					}
				}
			if ((visible) && (!makeVisible))
				{
//MiDebug.println("getEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, tmpEvent)=" + getEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, tmpEvent));
//MiDebug.println("event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, tmpEvent))="+event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, tmpEvent)));

				if (event.getType() == Mi_KEY_RELEASE_EVENT)
					{
					return(Mi_PROPOGATE_EVENT);
					}

				if ((event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, tmpEvent)))
					|| (event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_RIGHT_COMMAND_NAME, tmpEvent)))
					|| (event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_DOWN_COMMAND_NAME, tmpEvent)))
					|| (event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_UP_COMMAND_NAME, tmpEvent))))
					{
					MiEditor editor = event.getEditor();
					delta.x = 0;
					delta.y = 0;
					if ((editor.getSnapManager() != null)
						&& (editor.getSnapManager().getGrid() != null))
						{
						MiDrawingGrid drawingGrid = editor.getSnapManager().getGrid();
						MiSize size = drawingGrid.getGridSize();
		
						// To do: add finer movements if moved parts have snapPoints
						if (event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_LEFT_COMMAND_NAME, tmpEvent)))
							{
							delta.x = -size.getWidth();
							}
						else if (event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_RIGHT_COMMAND_NAME, tmpEvent)))
							{
							delta.x = +size.getWidth();
							}
						else if (event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_DOWN_COMMAND_NAME, tmpEvent)))
							{
							delta.y = -size.getHeight();
							}
						else if (event.equalsEventType(getEventToCommandTranslation(Mi_MOVE_UP_COMMAND_NAME, tmpEvent)))
							{
							delta.y = +size.getHeight();
							}
						}
					else
						{
						// Move 10 pixels, no matter the zoom level
						editor.getViewport().getTransform().dtow(
							event.getDevicePoint(devicePt), deviceDelta, delta);
						}
	
					tmpPoint.copy(cursorPt);
					tmpPoint.translate(delta);
					event.editor.snapMovingPoint(tmpPoint);

					//??event.editor.snapMovingPart(line, delta);
	
					//??boolean modifiedDelta = event.editor.autopanForMovingObj(line.getDrawBounds(tmpBounds), delta);
				
					event.setWorldPoint(tmpPoint);

					makeVisible = true;
					}
				}
//MiDebug.println(this + " makeVisible: " +makeVisible);
			if (hideDuringTheseEventStates != null)
				{
				for (int i = 0; i < hideDuringTheseEventStates.size(); ++i)
					{
					MiEvent validEvent = (MiEvent )hideDuringTheseEventStates.get(i);
					if (((validEvent.getModifiers() == event.getModifiers())
						|| (validEvent.getModifiers() == Mi_ANY_MODIFIERS_HELD_DOWN))
						&& (validEvent.getMouseButtonState() == event.getMouseButtonState()))
						{
						makeVisible = false;
						break;
						}
					}
				}
				
//MiDebug.println(this + " 2222 makeVisible: " +makeVisible);

			if (makeVisible)
				{
				if (!cursorIsAttached)
					{
					event.editor.appendAttachment(cursor);
					cursorIsAttached = true;
					}

				removeAffordancesFromCursor();

				event.editor.getWorldBounds(editorWorld);
				if ((!event.worldPt.equals(cursorPt)) 
					|| (!editorWorld.equals(cursorWorldBounds)))
					{
					cursorPt.copy(event.worldPt);
					cursorWorldBounds.copy(editorWorld);

//MiDebug.println(this + " moveddddd: " +cursorPt);
					editor.snapMovingPoint(cursorPt);

//MiDebug.println(this + " snap moveddddd: " +cursorPt);
					setCursor(cursorPt, cursorWorldBounds);

					MiPart partUnderCursor = getPartUnderCursor(cursorPt);
					if (partUnderCursor != null)
						{
						cursorAffordanceParts = new MiParts();

						int pointNumberOfPartUnderCursor = getPointNumberOfPartUnderCursor(partUnderCursor, cursorPt);
						if (pointNumberOfPartUnderCursor != Mi_INVALID_POINT_NUMBER)
							{
							MiParts listOfParts = new MiParts();
							IntVector listOfTheirPointNumbers = new IntVector();

							getOtherPointsAtSameY(
								cursorPt,
								pointNumberOfPartUnderCursor,
								partUnderCursor, 
								listOfParts, 
								listOfTheirPointNumbers);

							if (listOfParts.size() > 0)
								{
								addAffordancesToCursorToDisplayOtherPointsAtSameY(
									cursorPt, 
									listOfParts, 
									listOfTheirPointNumbers);
								}

							listOfParts = new MiParts();
							listOfTheirPointNumbers = new IntVector();

							getOtherPointsAtSameX(
								cursorPt,
								pointNumberOfPartUnderCursor,
								partUnderCursor, 
								listOfParts, 
								listOfTheirPointNumbers);

							if (listOfParts.size() > 0)
								{
								addAffordancesToCursorToDisplayOtherPointsAtSameX(
									cursorPt, 
									listOfParts, 
									listOfTheirPointNumbers);
								}
							}
						}
					if (!visible)
						{
						setVisible(true);
						}
					}
				}
			else
				{
				setVisible(false);
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	protected	MiPart		makeCursor()
		{
		cursor = new MiContainer();
		topLine = prototypeLine.copy();
		bottomLine = prototypeLine.copy();
		leftLine = prototypeLine.copy();
		rightLine = prototypeLine.copy();
		if (prototypeBullsEye != null)
			bullsEye = prototypeBullsEye.copy();
		cursor.appendPart(topLine);
		cursor.appendPart(bottomLine);
		cursor.appendPart(leftLine);
		cursor.appendPart(rightLine);
		if (prototypeBullsEye != null)
			cursor.appendPart(bullsEye);

		cursor.setPickable(false);
		cursor.setSelectable(false);
		return(cursor);
		}

	protected	void		setCursor(MiPoint pt, MiBounds bounds)
		{
		topLine.setPoint(0, pt.x, pt.y);
		topLine.setPoint(1, pt.x, bounds.ymax);
		bottomLine.setPoint(0, pt.x, pt.y);
		bottomLine.setPoint(1, pt.x, bounds.ymin);
		leftLine.setPoint(0, pt.x, pt.y);
		leftLine.setPoint(1, bounds.xmin, pt.y);
		rightLine.setPoint(0, pt.x, pt.y);
		rightLine.setPoint(1, bounds.xmax, pt.y);
		if (prototypeBullsEye != null)
			bullsEye.setCenter(pt);
		}

	public		void		setVisible(boolean flag)
		{
		topLine.setVisible(flag);
		bottomLine.setVisible(flag);
		leftLine.setVisible(flag);
		rightLine.setVisible(flag);
		if (prototypeBullsEye != null)
			bullsEye.setVisible(flag);

		visible = flag;
		if (!flag)
			{
			removeAffordancesFromCursor();
			}
		}
	protected	MiPart		getPartUnderCursor(MiPoint cursorPt)
		{
		MiParts targetList = event.getTargetList();
		int indexOfCurrentLayer = targetList.indexOf(editor.getCurrentLayer());
		if (indexOfCurrentLayer > 0)
			{
			return(targetList.get(indexOfCurrentLayer - 1));
			}
		MiBounds pickArea = event.getMouseFootPrint(tmpBounds);
		for (int i = 0; i < editor.getNumberOfAttachments(); ++i)
			{
			if ((editor.getAttachment(i).isPickable()) && (editor.getAttachment(i).pick(pickArea)))
				{
				return(editor.getAttachment(i));
				}
			}
		return(null);
		}

	protected	int 		getPointNumberOfPartUnderCursor(MiPart partUnderCursor, MiPoint cursorPt)
		{
		MiBounds pickArea = event.getMouseFootPrint(tmpBounds);
		for (int i = 0; i <  partUnderCursor.getNumberOfPoints(); ++i)
			{
			partUnderCursor.getPoint(i, tmpPoint);
			if (pickArea.intersects(tmpPoint))
				{
				return(i);
				}
			}
		return(Mi_INVALID_POINT_NUMBER);
		}
	protected	void		getOtherPointsAtSameY(
						MiPoint cursorPt,
						int pointNumberOfPartUnderCursor,
						MiPart partUnderCursor, 
						MiParts returnedListOfParts, 
						IntVector returnedListOfTheirPointNumbers)
		{
		partUnderCursor.getPoint(pointNumberOfPartUnderCursor, tmpPoint2);
		MiPoint pointUnderCursor = tmpPoint2;

//MiDebug.println("partUnderCursor=" + partUnderCursor);
		for (int i = 0; i < editor.getNumberOfItems(); ++i)
			{
			MiPart item = editor.getItem(i);
			for (int j = 0; j < item.getNumberOfPoints(); ++j)
				{
				if (item == partUnderCursor) 
					{
					// Ignore same part that is under cursor... until later
					continue;
					}
				item.getPoint(j, tmpPoint);
				MiDistance dy = tmpPoint.y - pointUnderCursor.y;
//MiDebug.println("Looking at item: " + item);
//MiDebug.println("Comparing pt nums: " + j + " and " + pointNumberOfPartUnderCursor);
//MiDebug.println("tmpPoint.y = " + tmpPoint.y);
//MiDebug.println("pointUnderCursor.y = " + pointUnderCursor.y);
//MiDebug.println("dy = " + dy);
				if ((dy < MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH) && (dy > -MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH))
					{
					returnedListOfParts.add(item);
					returnedListOfTheirPointNumbers.add(j);
					}
				}
			}
		for (int j = 0; j < partUnderCursor.getNumberOfPoints(); ++j)
			{
			if ((j >= pointNumberOfPartUnderCursor - 1)
				&& (j <= pointNumberOfPartUnderCursor + 1))
				{
				// Ignore neighboring points of same point that is under cursor...
				continue;
				}
			partUnderCursor.getPoint(j, tmpPoint);
			MiDistance dy = tmpPoint.y - pointUnderCursor.y;
//MiDebug.println("Comparing pt nums: " + j + " and " + pointNumberOfPartUnderCursor);
//MiDebug.println("tmpPoint.y = " + tmpPoint.y);
//MiDebug.println("pointUnderCursor.y = " + pointUnderCursor.y);
//MiDebug.println("dy = " + dy);
			if ((dy < MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH) && (dy > -MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH))
				{
				returnedListOfParts.add(partUnderCursor);
				returnedListOfTheirPointNumbers.add(j);
//MiDebug.println("ADDED");
				}
			}
		}
	protected	void		getOtherPointsAtSameX(
						MiPoint cursorPt,
						int pointNumberOfPartUnderCursor,
						MiPart partUnderCursor, 
						MiParts returnedListOfParts, 
						IntVector returnedListOfTheirPointNumbers)
		{
		partUnderCursor.getPoint(pointNumberOfPartUnderCursor, tmpPoint2);
		MiPoint pointUnderCursor = tmpPoint2;

		for (int i = 0; i < editor.getNumberOfItems(); ++i)
			{
			MiPart item = editor.getItem(i);
			for (int j = 0; j < item.getNumberOfPoints(); ++j)
				{
				if (item == partUnderCursor) 
					{
					// Ignore same part that is under cursor... until later
					continue;
					}
				item.getPoint(j, tmpPoint);
				MiDistance dy = tmpPoint.x - pointUnderCursor.x;
				if ((dy < MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH) && (dy > -MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH))
					{
					returnedListOfParts.add(item);
					returnedListOfTheirPointNumbers.add(j);
					}
				}
			}
		for (int j = 0; j < partUnderCursor.getNumberOfPoints(); ++j)
			{
			if ((j >= pointNumberOfPartUnderCursor - 1)
				&& (j <= pointNumberOfPartUnderCursor + 1))
				{
				// Ignore neighboring points of same point that is under cursor...
				continue;
				}
			partUnderCursor.getPoint(j, tmpPoint);
			MiDistance dx = tmpPoint.x - pointUnderCursor.x;
			if ((dx < MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH) && (dx > -MAX_DISTANCE_BETWEEN_POINTS_FOR_A_MATCH))
				{
				returnedListOfParts.add(partUnderCursor);
				returnedListOfTheirPointNumbers.add(j);
				}
			}
		}

	protected	void		addAffordancesToCursorToDisplayOtherPointsAtSameY(
						MiPoint cursorPt, 
						MiParts listOfParts, 
						IntVector listOfTheirPointNumbers)
		{
		leftLine.setAttributes(attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint);
		rightLine.setAttributes(attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint);

		for (int i = 0; i < listOfParts.size(); ++i)
			{
			MiPart otherPart = listOfParts.get(i);
			MiPart otherPointLocatorGraphics = prototypeAlignedPointLocatorGraphics.deepCopy();
			otherPart.getPoint(listOfTheirPointNumbers.elementAt(i), tmpPoint);
			otherPointLocatorGraphics.setCenter(tmpPoint);
			cursor.appendPart(otherPointLocatorGraphics);
			cursorAffordanceParts.add(otherPointLocatorGraphics);
			}
		}
	protected	void		addAffordancesToCursorToDisplayOtherPointsAtSameX(
						MiPoint cursorPt, 
						MiParts listOfParts, 
						IntVector listOfTheirPointNumbers)
		{
		bottomLine.setAttributes(attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint);
		topLine.setAttributes(attributesOfCursorLinesWhenAlignedWithAnotherPartsPoint);

		for (int i = 0; i < listOfParts.size(); ++i)
			{
			MiPart otherPart = listOfParts.get(i);
			MiPart otherPointLocatorGraphics = prototypeAlignedPointLocatorGraphics.deepCopy();
			otherPart.getPoint(listOfTheirPointNumbers.elementAt(i), tmpPoint);
			otherPointLocatorGraphics.setCenter(tmpPoint);
			cursor.appendPart(otherPointLocatorGraphics);
			cursorAffordanceParts.add(otherPointLocatorGraphics);
			}
		}
	protected	void		removeAffordancesFromCursor()
		{
		leftLine.setAttributes(prototypeLine.getAttributes());
		rightLine.setAttributes(prototypeLine.getAttributes());
		bottomLine.setAttributes(prototypeLine.getAttributes());
		topLine.setAttributes(prototypeLine.getAttributes());

		for (int i = 0; i < cursorAffordanceParts.size(); ++i)
			{
			cursorAffordanceParts.get(i).setVisible(false);
			cursor.removePart(cursorAffordanceParts.get(i));
			}
		cursorAffordanceParts.removeAllElements();
		}
	}

