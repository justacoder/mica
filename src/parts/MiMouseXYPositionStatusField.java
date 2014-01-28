
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
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiMouseXYPositionStatusField extends MiStatusBar
	{
	private		MiMouseLocationDisplayMonitor	mouseLocationMonitor;
	private		MiEditor		mouseLocationEditor;
	private		int			X_FIELD_INDEX			= 0;
	private 	int			Y_FIELD_INDEX			= 1;
	public  	String			MOUSE_X_POSITION_TOOL_HINT	= "Mouse Position";
	public  	String			MOUSE_Y_POSITION_TOOL_HINT	= "Mouse Position";


	public				MiMouseXYPositionStatusField(String spec, MiEditor mouseLocationEditor, int xFieldIndex, int yFieldIndex)
		{
		super(spec);
		setTargetEditor(mouseLocationEditor);
		X_FIELD_INDEX = xFieldIndex;
		X_FIELD_INDEX = yFieldIndex;
		setup();
		}
	public				MiMouseXYPositionStatusField(MiEditor mouseLocationEditor)
		{
		super(".11\n.11");
		setTargetEditor(mouseLocationEditor);
		setup();
		}

	public		void		setTargetEditor(MiEditor editor)
		{
		if (mouseLocationEditor != null)
			{
			mouseLocationEditor.removeEventHandler(mouseLocationMonitor);
			}
		mouseLocationEditor = editor;
		mouseLocationMonitor = new MiMouseLocationDisplayMonitor(getField(X_FIELD_INDEX), getField(Y_FIELD_INDEX));
		mouseLocationEditor.appendEventHandler(mouseLocationMonitor);
		}

	protected	void		setup()
		{
		setBorderLook(Mi_NONE);
		setInsetMargins(0);
		getField(X_FIELD_INDEX).setToolHintMessage(MOUSE_X_POSITION_TOOL_HINT);
		getField(Y_FIELD_INDEX).setToolHintMessage(MOUSE_Y_POSITION_TOOL_HINT);
		}
	public		void		setEnabled(boolean flag)
		{
		mouseLocationMonitor.setEnabled(flag);
		}
	public		void		deleteSelf()
		{
		if (mouseLocationEditor != null)
			mouseLocationEditor.removeEventHandler(mouseLocationMonitor);
		super.deleteSelf();
		}
	}
class MiMouseLocationDisplayMonitor extends MiEventMonitor
	{
	private		MiPoint		tmpPoint= new MiPoint();
	private		MiWidget	xPos;
	private		MiWidget	yPos;
	private		MiCoord		lastX	= MiiTypes.Mi_MAX_COORD_VALUE;
	private		MiCoord		lastY	= MiiTypes.Mi_MAX_COORD_VALUE;


	public				MiMouseLocationDisplayMonitor(MiWidget x, MiWidget y)
		{
		xPos = x;
		yPos = y;
		}
	public		int		processEvent(MiEvent event)
		{
		if ((isEnabled())
			&& (event.getType() != MiEvent.Mi_TIMER_TICK_EVENT)
			&& (event.getType() != MiEvent.Mi_IDLE_EVENT))
			{
			tmpPoint.copy(event.worldPt);
			MiPageManager pageManager = event.editor.getPageManager();
			String unitsName = "";
			if (pageManager != null)
				{
				pageManager.transformWorldPointToUnitsPoint(tmpPoint);
				unitsName = pageManager.getUnits().getAbbreviation();
				}
			

			if (tmpPoint.x != lastX)
				{
				lastX = tmpPoint.x;
				xPos.setValue("X: " + Utility.toShortString(tmpPoint.x) + " " + unitsName);
				}
			if (tmpPoint.y != lastY)
				{
				lastY = tmpPoint.y;
				yPos.setValue("Y: " + Utility.toShortString(tmpPoint.y) + " " + unitsName);
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}



