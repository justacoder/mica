
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIFullScreenCursor extends MiEventMonitor implements MiiTypes
	{
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



	public				MiIFullScreenCursor()
		{
		init();
		}
	public				MiIFullScreenCursor(Color color)
		{
		prototypeLine.setColor(color);
		prototypeBullsEye.setColor(color);
		init();
		}
	public				MiIFullScreenCursor(MiPart prototypeLine, MiPart prototypeBullsEye)
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
		cursor = makeCursor();
		setVisible(false);
		}

	public		int		processEvent(MiEvent event)
		{
		if ((event.type == Mi_WINDOW_EXIT_EVENT) || (event.type == Mi_MOUSE_EXIT_EVENT))
			{
			setVisible(false);
			visible = false;
			}
		else if ((event.type != Mi_IDLE_EVENT) && (event.type != Mi_TIMER_TICK_EVENT))
			{
			if (!cursorIsAttached)
				{
				event.editor.appendAttachment(cursor);
				cursorIsAttached = true;
				}

			boolean makeVisible = true;
			if (showOnlyDuringTheseEventStates != null)
				{
				for (int i = 0; i < showOnlyDuringTheseEventStates.size(); ++i)
					{
					makeVisible = false;
					MiEvent validEvent = (MiEvent )showOnlyDuringTheseEventStates.get(i);
					if (((validEvent.getModifiers() == event.getModifiers())
						|| (validEvent.getModifiers() == Mi_ANY_MODIFIERS_HELD_DOWN))
						&& (validEvent.getMouseButtonState() == event.getMouseButtonState()))
						{
						makeVisible = true;
						break;
						}
					}
				}
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
					

			if (makeVisible)
				{
				event.editor.getWorldBounds(editorWorld);
				if ((!event.worldPt.equals(cursorPt)) 
					|| (!editorWorld.equals(cursorWorldBounds)))
					{
					cursorPt.copy(event.worldPt);
					cursorWorldBounds.copy(editorWorld);
					setCursor(cursorPt, cursorWorldBounds);
					if (!visible)
						{
						setVisible(true);
						visible = true;
						}
					}
				}
			else
				{
				setVisible(false);
				visible = false;
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
		}
	}

