
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
 * This class of the MICA Graphics Framework provides a keyboard-driven 
 * interface to the debug tracing system built into Mica. The following
 * lists the key to trace-mode mappings. Multiple trace-modes may be in
 * effect at any one time. In the following, press ctrl-shift-d, then, 
 * releasing the ctrl and shift keys, press the next key, for example, '1'.
 * <p><pre>
 * Ctrl-Shift-e		Toggle all trace-modes on/off
 * Ctrl-Shift-d1	Toggle tracing of basic key and mouse event dispatching on/off
 * Ctrl-Shift-d2	Toggle tracing of keyboard focus on/off
 * Ctrl-Shift-d3	Toggle tracing of drag-and-drop on/off
 * Ctrl-Shift-d4	Toggle tracing of advanced key and mouse event dispatching on/off
 * Ctrl-Shift-d5	Toggle tracing of graphics selection on/off
 * Ctrl-Shift-d6	Toggle tracing of window sizing on/off
 * Ctrl-Shift-d7	Toggle tracing of MiAttribute, MiBounds, MiSize allocation on/off
 * Ctrl-Shift-d8	Toggle tracing of application-specific activities on/off
			(i.e. if (MiDebug.TRACE_CUSTOM_DEBUG_INFO) MiDebug.println(...);)
 * Ctrl-Shift-d9	Toggle tracing of drawing each part
 * Ctrl-Shift-d?	Prints this keyboard mapping to the console.
 * <p></pre>
 *
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiISetDebugTraceModes extends MiEventMonitor
	{
	private		boolean		enabled 				= false;
	public static final String	Mi_TOGGLE_EVENT_DEBUG_COMMAND_NAME	= "ToggleEventDebug";
	public static final String	Mi_TOGGLE_KEYFOCUS_DEBUG_COMMAND_NAME	= "ToggleKeyFocusDebug";
	public static final String	Mi_TOGGLE_DRAG_AND_DROP_DEBUG_COMMAND_NAME= "ToggleDragAndDropDebug";
	public static final String	Mi_TOGGLE_ADV_EVENT_DEBUG_COMMAND_NAME	= "ToggleAdvancedEventDebug";
	public static final String	Mi_TOGGLE_SELECTION_DEBUG_COMMAND_NAME	= "TogglePartSelectionDebug";
	public static final String	Mi_TOGGLE_WINDOW_SIZING_DEBUG_COMMAND_NAME= "ToggleWindowSizingDebug";
	public static final String	Mi_TOGGLE_CUSTOM_DEBUG_FLAG_COMMAND_NAME= "ToggleCustomDebugTrace";
	public static final String	Mi_TOGGLE_ALLOCATION_DEBUG_COMMAND_NAME= "ToggleAllocationDebug";
	public static final String	Mi_TOGGLE_DRAW_DEBUG_COMMAND_NAME	= "ToggleDrawDebug";
	public static final String	Mi_PRINT_TRACE_HELP_COMMAND_NAME	= "PrintTraceHelp";

	public		int		traceMode				= 0;
	public static	int		eventTraceModes		= MiDebug.TRACE_EVENT_TO_COMMAND_TRANSLATION;
	public static	int		keyFocusTraceModes	= MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING;
	public static	int		dragAndDropTraceModes	= MiDebug.TRACE_DRAG_AND_DROP;
	public static	int		advEventTraceModes	= MiDebug.TRACE_EVENT_DISPATCHING
								+ MiDebug.TRACE_SHORT_CUT_DISPATCHING
								+ MiDebug.TRACE_EVENT_HANDLER_GRABS
								+ MiDebug.TRACE_EVENT_TO_COMMAND_TRANSLATION
								+ MiDebug.TRACE_EVENT_INPUT;
	public static	int		partSelectionTraceModes	= MiDebug.TRACE_INTERACTIVE_SELECT;
	public static	int		windowResizingTraceModes= MiDebug.TRACE_WINDOW_AND_CANVAS_RESIZING;
	public static	int		basicAllocationTraceModes= MiDebug.TRACE_BASIC_ALLOCATIONS;
	public static	int		drawDebugTraceModes	= MiDebug.TRACE_DRAWING_OF_PARTS;
	public static	int		customDebugTraceModes	= MiDebug.TRACE_CUSTOM_DEBUG_INFO;




	public				MiISetDebugTraceModes()
		{
		addEventToCommandTranslation(
			Mi_TOGGLE_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'e', Mi_CONTROL_KEY_HELD_DOWN + Mi_SHIFT_KEY_HELD_DOWN);

		setDebugTraceBaseTriggerEvent(new MiEvent(Mi_KEY_RELEASE_EVENT, 'd', Mi_CONTROL_KEY_HELD_DOWN+Mi_SHIFT_KEY_HELD_DOWN, true));
		}
	public				MiISetDebugTraceModes(MiEvent toggleEvent, MiEvent baseTriggerEvent)
		{
		addEventToCommandTranslation(Mi_TOGGLE_COMMAND_NAME, toggleEvent);
		setDebugTraceBaseTriggerEvent(baseTriggerEvent);
		}
	public		void		setDebugTraceBaseTriggerEvent(MiEvent baseTriggerEvent)
		{
		setEventToCommandTranslation(
			Mi_TOGGLE_EVENT_DEBUG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '1', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_TOGGLE_KEYFOCUS_DEBUG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '2', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_TOGGLE_DRAG_AND_DROP_DEBUG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '3', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_TOGGLE_ADV_EVENT_DEBUG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '4', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_TOGGLE_SELECTION_DEBUG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '5', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_TOGGLE_WINDOW_SIZING_DEBUG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '6', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_TOGGLE_ALLOCATION_DEBUG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '7', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_TOGGLE_CUSTOM_DEBUG_FLAG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '8', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_TOGGLE_DRAW_DEBUG_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '9', Mi_ANY_MODIFIERS_HELD_DOWN, true));

		setEventToCommandTranslation(
			Mi_PRINT_TRACE_HELP_COMMAND_NAME, 
			baseTriggerEvent,
			new MiEvent(Mi_KEY_PRESS_EVENT, '?', Mi_ANY_MODIFIERS_HELD_DOWN, true));
		}

					/**------------------------------------------------------
	 				 * Processes the command generated from the current event.
					 * Both are stored in the MiEventHandler super class.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			MiEventHandler#isCommand
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		int toggleTraceMode = 0;

		if (isCommand(Mi_TOGGLE_COMMAND_NAME))
			{
			enabled = !enabled;
			}
		else if (isCommand(Mi_TOGGLE_EVENT_DEBUG_COMMAND_NAME))
			toggleTraceMode = eventTraceModes;
		else if (isCommand(Mi_TOGGLE_KEYFOCUS_DEBUG_COMMAND_NAME))
			toggleTraceMode = keyFocusTraceModes;
		else if (isCommand(Mi_TOGGLE_DRAG_AND_DROP_DEBUG_COMMAND_NAME))
			toggleTraceMode = dragAndDropTraceModes;
		else if (isCommand(Mi_TOGGLE_ADV_EVENT_DEBUG_COMMAND_NAME))
			toggleTraceMode = advEventTraceModes;
		else if (isCommand(Mi_TOGGLE_SELECTION_DEBUG_COMMAND_NAME))
			toggleTraceMode = partSelectionTraceModes;
		else if (isCommand(Mi_TOGGLE_WINDOW_SIZING_DEBUG_COMMAND_NAME))
			toggleTraceMode = windowResizingTraceModes;
		else if (isCommand(Mi_TOGGLE_ALLOCATION_DEBUG_COMMAND_NAME))
			toggleTraceMode = basicAllocationTraceModes;
		else if (isCommand(Mi_TOGGLE_DRAW_DEBUG_COMMAND_NAME))
			toggleTraceMode = drawDebugTraceModes;
		else if (isCommand(Mi_TOGGLE_CUSTOM_DEBUG_FLAG_COMMAND_NAME))
			toggleTraceMode = customDebugTraceModes;
		else if (isCommand(Mi_PRINT_TRACE_HELP_COMMAND_NAME))
			{
			MiDebug.println("1...Trace events\n2...Trace keyfocus\n3...Trace drag-and-drop\n4...Trace more events\n5...Trace selections\n6...Trace window sizing\n7...Trace memory\n8...Trace custom\n?...Help");
			}

		if (toggleTraceMode != 0)
			{
			if ((traceMode & toggleTraceMode) == toggleTraceMode)
				{
				traceMode = traceMode & ~toggleTraceMode;
				}
			else
				{
				traceMode = traceMode | toggleTraceMode;
				enabled = true;
				}
			}

		if (enabled)
			MiDebug.setTraceMode(traceMode);
		else
			MiDebug.setTraceMode(0);

		MiDebug.println("Debug trace mode is " + (enabled ? "Enabled" : "Not enabled")
			+ "[" + traceMode + "]");

		return(Mi_CONSUME_EVENT);
		}

	public		int		processEvent(MiEvent event)
		{
		if (enabled && 
			((traceMode & eventTraceModes) == eventTraceModes)
			&& ((event.type != MiEvent.Mi_TIMER_TICK_EVENT)
			&& (event.type != MiEvent.Mi_MOUSE_MOTION_EVENT)
			&& (event.type != MiEvent.Mi_IDLE_EVENT)))
			{
			MiDebug.println("Editor: \"" + event.editor + "\" is processing event: " + event.toString());
			}
		return(super.processEvent(event));
		}
	}

