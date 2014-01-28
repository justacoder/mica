
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

// For menus, lists
/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiBrowsableGridEventHandler extends MiEventHandler implements MiiSimpleAnimator, MiiCommandNames, MiiEventTypes
	{
	private		boolean			grabbed 		= false;
	private		boolean			animating 		= false;
	private		MiiBrowsableGrid	grid;
	private		MiEditor		containingEditor;

	public final static String	ActivateEventName		= "activate";
	public final static String	BrowseEventName			= "browse";
	public final static String	EndBrowseEventName		= "endBrowse";
	public final static String	DragBrowseEventName		= "dragBrowse";
	public final static String	StartDragBrowseEventName	= "startDragBrowse";
	public final static String	SelectToggleEventName		= "selectToggle";
	public final static String	SelectBrowsedEventName		= "selectBrowsed";
	public final static String	SelectAdditionalEventName	= "selectAdditional";
	public final static String	SelectInterveningEventName	= "selectIntervening";

	public final static String	BrowseVerticalNextEventName 	= "browseNextVert";
	public final static String	BrowseVerticalPreviousEventName	= "browsePrevVert";
	public final static String	BrowseHorizontalNextEventName 	= "browseNextHoriz";
	public final static String	BrowseHorizontalPreviousEventName= "browsePrevHoriz";



	public					MiBrowsableGridEventHandler()
		{
		addEventToCommandTranslation(BrowseEventName, Mi_LEFT_MOUSE_DOWN_EVENT, 0, 0);
		addEventToCommandTranslation(SelectToggleEventName, Mi_LEFT_MOUSE_CLICK_EVENT, 0,0);
		addEventToCommandTranslation(ActivateEventName, Mi_LEFT_MOUSE_DBLCLICK_EVENT, 0,0);
		addEventToCommandTranslation(SelectToggleEventName, Mi_LEFT_MOUSE_UP_EVENT, 0,0);
		addEventToCommandTranslation(StartDragBrowseEventName, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(DragBrowseEventName, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);

		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_RIGHT_MOUSE_DOWN_EVENT, 0, 0);
// 7-9-2002 changes made to right button to support context menus in windows explorer like interfaces
// BUT with menu popping up on right-mouse-down, not just rightmouse click		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_RIGHT_MOUSE_CLICK_EVENT, 0,0);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_RIGHT_MOUSE_UP_EVENT, 0,0);
		addEventToCommandTranslation(StartDragBrowseEventName, Mi_RIGHT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(DragBrowseEventName, Mi_RIGHT_MOUSE_DRAG_EVENT, 0, 0);

		addEventToCommandTranslation(SelectBrowsedEventName, Mi_KEY_EVENT, Mi_SPACE_KEY, 0);
		addEventToCommandTranslation(SelectBrowsedEventName, Mi_KEY_EVENT, Mi_RETURN_KEY, 0);
		addEventToCommandTranslation(SelectAdditionalEventName, Mi_LEFT_MOUSE_CLICK_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(SelectInterveningEventName, Mi_LEFT_MOUSE_CLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(BrowseEventName, Mi_MOUSE_MOTION_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(EndBrowseEventName, Mi_MOUSE_EXIT_EVENT, 0, 0);
		addEventToCommandTranslation(BrowseVerticalNextEventName, Mi_KEY_EVENT, Mi_DOWN_ARROW_KEY, 0);
		addEventToCommandTranslation(BrowseVerticalPreviousEventName, Mi_KEY_EVENT, Mi_UP_ARROW_KEY, 0);
		addEventToCommandTranslation(BrowseHorizontalNextEventName, Mi_KEY_EVENT, Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(BrowseHorizontalPreviousEventName, Mi_KEY_EVENT, Mi_LEFT_ARROW_KEY, 0);
		addEventToCommandTranslation(Mi_DESELECT_ALL_COMMAND_NAME, Mi_KEY_EVENT, Mi_ESC_KEY, 0);
		}
	public		void			setBrowsableGrid(MiiBrowsableGrid grid)
		{
		this.grid = grid;
		}
	public		MiiBrowsableGrid	getBrowsableGrid()
		{
		if (grid != null)
			return(grid);
		return((MiiBrowsableGrid )getTarget());
		}
	public		int			processCommand()
		{
//MiDebug.println(this + "processCommand: " + getCommand());
		MiiBrowsableGrid widget = getBrowsableGrid();
//MiDebug.println(this + "MiiBrowsableGrid target: " + widget);
		if (!widget.isSensitive())
			{
			return(Mi_PROPOGATE_EVENT);
			}

/* FIX: fix this somehow, MiiBrowsableGrid needs a isDesignTime method...?
		if (widget.isDesignTime())
			{
			return(Mi_PROPOGATE_EVENT);
			}
*/

		if ((isCommand(StartDragBrowseEventName)) || (isCommand(DragBrowseEventName)))
			{
			if (!widget.isBrowsable())
				return(Mi_PROPOGATE_EVENT);

			if (!grabbed)
				{
				event.editor.prependGrabEventHandler(this);
				grabbed = true;
				if (!animating)
					{
					containingEditor = event.editor;
					MiAnimationManager.addAnimator(containingEditor, this);
					animating = true;
					}
				}
			else 
				{
				widget.browseItem(event.worldMouseFootPrint);
				}
			}
		else if (isCommand(BrowseEventName))
			{
			if (!widget.isBrowsable())
				return(Mi_PROPOGATE_EVENT);

			widget.browseItem(event.worldMouseFootPrint);
			}
		else if (isCommand(EndBrowseEventName))
			{
			if (!widget.isBrowsable())
				return(Mi_PROPOGATE_EVENT);

			if (!grabbed)
				widget.deBrowseAll();
			}
		else if (isCommand(BrowseVerticalNextEventName))
			{
			if (!widget.isBrowsable())
				return(Mi_PROPOGATE_EVENT);

			widget.browseVerticalNextItem();
			}
		else if (isCommand(BrowseVerticalPreviousEventName))
			{
			if (!widget.isBrowsable())
				return(Mi_PROPOGATE_EVENT);

			widget.browseVerticalPreviousItem();
			}
		else if (isCommand(BrowseHorizontalNextEventName))
			{
			if (!widget.isBrowsable())
				return(Mi_PROPOGATE_EVENT);

			widget.browseHorizontalNextItem();
			}
		else if (isCommand(BrowseHorizontalPreviousEventName))
			{
			if (!widget.isBrowsable())
				return(Mi_PROPOGATE_EVENT);

			widget.browseHorizontalPreviousItem();
			}
		else if (isCommand(SelectBrowsedEventName))
			{
			widget.selectBrowsedItem();
			}
		else if (isCommand(SelectAdditionalEventName))
			{
			widget.selectAdditionalItem(event.worldMouseFootPrint);
			}
		else if (isCommand(SelectInterveningEventName))
			{
			widget.selectInterveningItems(event.worldMouseFootPrint);
			}
		else if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			if (grabbed)
				event.editor.removeGrabEventHandler(this);
			if (animating)
				MiAnimationManager.removeAnimator(containingEditor, this);
			grabbed = false;
			animating = false;
			if (!widget.selectItem(event.worldMouseFootPrint))
				return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(SelectToggleEventName))
			{
			if (grabbed)
				event.editor.removeGrabEventHandler(this);
			if (animating)
				MiAnimationManager.removeAnimator(containingEditor, this);
			grabbed = false;
			animating = false;
			if (!widget.toggleSelectItem(event.worldMouseFootPrint))
				return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(ActivateEventName))
			{
			if (grabbed)
				event.editor.removeGrabEventHandler(this);
			if (animating)
				MiAnimationManager.removeAnimator(containingEditor, this);
			grabbed = false;
			animating = false;
			if (!widget.activateItem(event.worldMouseFootPrint))
				return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(Mi_DESELECT_ALL_COMMAND_NAME))
			{
			widget.deSelectAll();
			}
		return(Mi_PROPOGATE_EVENT);
// 7-9-2002 to enable context menus in treelists		return(Mi_CONSUME_EVENT);
		}
	public		String		toString()
		{
		return(getBrowsableGrid().toString() + "." + super.toString());
		}
	public		long		animate()
		{
		getBrowsableGrid().browseItem(containingEditor.getMousePosition());
		return(100);
		}
	}


