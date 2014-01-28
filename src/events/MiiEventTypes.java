
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
import java.awt.Event; 
import com.swfm.mica.util.StringIntPair; 

/**----------------------------------------------------------------------------------------------
 * This interface defines all of the event types used in Mica's
 * MiEvent class.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiEventTypes
	{
	/**------------------------------------------------------
	 * Valid values for event handler processEvent() return codes.
	 *------------------------------------------------------*/
	int	Mi_PROPOGATE_EVENT		= 	0;
	int	Mi_CONSUME_EVENT		= 	1;
	int	Mi_IGNORE_THIS_PART		= 	2;

	/**------------------------------------------------------
	 * Types of event handlers. MiEvents target specific types
	 * of event handlers.
	 *------------------------------------------------------*/
	int	Mi_ORDINARY_EVENT_HANDLER_TYPE	= 	0;
	int	Mi_SHORT_CUT_EVENT_HANDLER_TYPE	= 	1;
	int	Mi_MONITOR_EVENT_HANDLER_TYPE	= 	2;
	int	Mi_GRAB_EVENT_HANDLER_TYPE	= 	3;

	/**------------------------------------------------------
	 * Event handler type names
	 *------------------------------------------------------*/
	String	Mi_UNKNOWN_EVENT_HANDLER_TYPE_NAME 	=	"Unknown";
	String	Mi_ORDINARY_EVENT_HANDLER_TYPE_NAME 	=	"Ordinary";
	String	Mi_SHORT_CUT_EVENT_HANDLER_TYPE_NAME 	=	"ShortCut";
	String	Mi_MONITOR_EVENT_HANDLER_TYPE_NAME 	=	"Monitor";
	String	Mi_GRAB_EVENT_HANDLER_TYPE_NAME 	=	"Grab";

	/**------------------------------------------------------
	 * The bits in an event's modifiers field.
	 *------------------------------------------------------*/
	int	Mi_NO_MODIFIERS_HELD_DOWN		=	0;
	int	Mi_SHIFT_KEY_HELD_DOWN			=	1;
	int	Mi_CONTROL_KEY_HELD_DOWN		=	2;
	int	Mi_ALT_KEY_HELD_DOWN			=	4;
	int	Mi_META_KEY_HELD_DOWN			=	8;
	int	Mi_LOCK_KEY_HELD_DOWN			=	16;
	int	Mi_ANY_MODIFIERS_HELD_DOWN		=	31;


	/**------------------------------------------------------
	 * The bits in an event mouse state. 
	 *------------------------------------------------------*/
	int	Mi_LEFT_MOUSE_BUTTON_HELD_DOWN		=	32;
	int	Mi_MIDDLE_MOUSE_BUTTON_HELD_DOWN	=	64;
	int	Mi_RIGHT_MOUSE_BUTTON_HELD_DOWN		=	128;
	int	Mi_MIDDLE_PLUS_RIGHT_MOUSE_BUTTON_HELD_DOWN	=	256;

	/**------------------------------------------------------
	 * The possible values of an event's key field. 
	 *------------------------------------------------------*/
	int	Mi_BACKSPACE_KEY		=	8;
	int	Mi_SPACE_KEY			=	' ';
	int	Mi_TAB_KEY			=	9;
	int	Mi_LINE_FEED_KEY		=	10;
	int	Mi_ENTER_KEY			=	13;
	int	Mi_RETURN_KEY			=	13;
	int	Mi_ESC_KEY			=	0x1b;
	int	Mi_DELETE_KEY			=	127;
	int	Mi_INSERT_KEY			=	126;
	int	Mi_F1_KEY			=	Event.F1;
	int	Mi_F2_KEY			=	Event.F2;
	int	Mi_F3_KEY			=	Event.F3;
	int	Mi_F4_KEY			=	Event.F4;
	int	Mi_F5_KEY			=	Event.F5;
	int	Mi_F6_KEY			=	Event.F6;
	int	Mi_F7_KEY			=	Event.F7;
	int	Mi_F8_KEY			=	Event.F8;
	int	Mi_F9_KEY			=	Event.F9;
	int	Mi_F10_KEY			=	Event.F10;
	int	Mi_F11_KEY			=	Event.F11;
	int	Mi_F12_KEY			=	Event.F12;

	int	Mi_UP_ARROW_KEY			=	Event.UP;
	int	Mi_DOWN_ARROW_KEY		=	Event.DOWN;
	int	Mi_RIGHT_ARROW_KEY		=	Event.RIGHT;
	int	Mi_LEFT_ARROW_KEY		=	Event.LEFT;
	int	Mi_HOME_KEY			=	Event.HOME;
	int	Mi_END_KEY			=	Event.END;
	int	Mi_PAGE_UP_KEY			=	Event.PGUP;
	int	Mi_PAGE_DOWN_KEY		=	Event.PGDN;

	/**------------------------------------------------------
	 * The possible values of an event's type field. 
	 *------------------------------------------------------*/
	int	NOOP_EVENT				=	0;

	int	Mi_LEFT_MOUSE_DOWN_EVENT		=	1;
	int	Mi_LEFT_MOUSE_UP_EVENT			=	3;
	int	Mi_LEFT_MOUSE_CLICK_EVENT		=	4;
	int	Mi_LEFT_MOUSE_DRAG_EVENT		=	5;
	int	Mi_LEFT_MOUSE_START_DRAG_EVENT		=	6;
	int	Mi_LEFT_MOUSE_DBLCLICK_EVENT		=	7;
	int	Mi_LEFT_MOUSE_TRIPLECLICK_EVENT		=	8;

	int	Mi_MIDDLE_MOUSE_DOWN_EVENT		=	11;
	int	Mi_MIDDLE_MOUSE_UP_EVENT		=	13;
	int	Mi_MIDDLE_MOUSE_CLICK_EVENT		=	14;
	int	Mi_MIDDLE_MOUSE_DRAG_EVENT		=	15;
	int	Mi_MIDDLE_MOUSE_START_DRAG_EVENT	=	16;
	int	Mi_MIDDLE_MOUSE_DBLCLICK_EVENT		=	17;
	int	Mi_MIDDLE_MOUSE_TRIPLECLICK_EVENT	=	18;

	int	Mi_RIGHT_MOUSE_DOWN_EVENT		=	21;
	int	Mi_RIGHT_MOUSE_UP_EVENT			=	22;
	int	Mi_RIGHT_MOUSE_CLICK_EVENT		=	23;
	int	Mi_RIGHT_MOUSE_DRAG_EVENT		=	24;
	int	Mi_RIGHT_MOUSE_START_DRAG_EVENT		=	25;
	int	Mi_RIGHT_MOUSE_DBLCLICK_EVENT		=	26;
	int	Mi_RIGHT_MOUSE_TRIPLECLICK_EVENT	=	27;

	int	Mi_MIDDLE_PLUS_RIGHT_MOUSE_DOWN_EVENT		=	30;
	int	Mi_MIDDLE_PLUS_RIGHT_MOUSE_UP_EVENT		=	31;
	int	Mi_MIDDLE_PLUS_RIGHT_MOUSE_CLICK_EVENT		=	32;
	int	Mi_MIDDLE_PLUS_RIGHT_MOUSE_DRAG_EVENT		=	33;
	int	Mi_MIDDLE_PLUS_RIGHT_MOUSE_START_DRAG_EVENT	=	34;
	int	Mi_MIDDLE_PLUS_RIGHT_MOUSE_DBLCLICK_EVENT	=	35;
	int	Mi_MIDDLE_PLUS_RIGHT_MOUSE_TRIPLECLICK_EVENT	=	36;

	int	Mi_KEY_PRESS_EVENT			=	39;
	int	Mi_KEY_EVENT				=	Mi_KEY_PRESS_EVENT;
	int	Mi_KEY_RELEASE_EVENT			=	40;
	int	Mi_MOUSE_MOTION_EVENT			=	41;
	int	Mi_WINDOW_ENTER_EVENT			=	42;
	int	Mi_WINDOW_EXIT_EVENT			=	43;
	int	Mi_MOUSE_ENTER_EVENT			=	44;
	int	Mi_MOUSE_EXIT_EVENT			=	45;

	int	Mi_WINDOW_REPAINT_EVENT			=	46;
	int	Mi_WINDOW_EXPOSE_EVENT			=	47;
	int	Mi_WINDOW_RESIZE_EVENT			=	48;
	int	Mi_WINDOW_DESTROY_EVENT			=	49;

	int	Mi_SCROLL_LINE_UP_EVENT			= 	50;
	int	Mi_SCROLL_LINE_DOWN_EVENT		= 	51;
	int	Mi_SCROLL_CHUNK_UP_EVENT		= 	52;
	int	Mi_SCROLL_CHUNK_DOWN_EVENT		= 	53;
	int	Mi_SCROLL_PAGE_UP_EVENT			= 	54;
	int	Mi_SCROLL_PAGE_DOWN_EVENT		= 	55;
	int	Mi_SCROLL_TO_TOP_EVENT			= 	56;
	int	Mi_SCROLL_TO_BOTTOM_EVENT		= 	57;
	int	Mi_SCROLL_TO_VERTICAL_LOCATION_EVENT	= 	58;

	int	Mi_SCROLL_LINE_RIGHT_EVENT		= 	60;
	int	Mi_SCROLL_LINE_LEFT_EVENT		= 	61;
	int	Mi_SCROLL_CHUNK_RIGHT_EVENT		= 	62;
	int	Mi_SCROLL_CHUNK_LEFT_EVENT		= 	63;
	int	Mi_SCROLL_PAGE_RIGHT_EVENT		= 	64;
	int	Mi_SCROLL_PAGE_LEFT_EVENT		= 	65;
	int	Mi_SCROLL_TO_RIGHTSIDE_EVENT		= 	66;
	int	Mi_SCROLL_TO_LEFTSIDE_EVENT		= 	67;
	int	Mi_SCROLL_TO_HORIZONTAL_LOCATION_EVENT	= 	68;

	int	Mi_TIMER_TICK_EVENT			=	201;
	int	Mi_IDLE_EVENT				=	202;
	int	Mi_NO_LONGER_IDLE_EVENT			=	203;

	int	Mi_ALL_EVENT_TYPES			=	255;


	/**------------------------------------------------------
	 * Maps modifier values to/from text strings (names).
	 *------------------------------------------------------*/
	StringIntPair[]		stringToModifierMap =
		{
		new StringIntPair(	"Shift",		Mi_SHIFT_KEY_HELD_DOWN),
		new StringIntPair(	"CapsLock", 		Mi_LOCK_KEY_HELD_DOWN),
		new StringIntPair(	"Ctrl",			Mi_CONTROL_KEY_HELD_DOWN),
		new StringIntPair(	"^",			Mi_CONTROL_KEY_HELD_DOWN),
		new StringIntPair(	"Alt",			Mi_ALT_KEY_HELD_DOWN),
		new StringIntPair(	"Meta",			Mi_META_KEY_HELD_DOWN),
		new StringIntPair(	"Btn1HeldDown",		Mi_LEFT_MOUSE_BUTTON_HELD_DOWN),
		new StringIntPair(	"Btn2HeldDown",		Mi_MIDDLE_MOUSE_BUTTON_HELD_DOWN),
		new StringIntPair(	"Btn3HeldDown",		Mi_RIGHT_MOUSE_BUTTON_HELD_DOWN),
		new StringIntPair(	"AnyModifier",		Mi_ANY_MODIFIERS_HELD_DOWN),
		new StringIntPair(	"Any",			Mi_ANY_MODIFIERS_HELD_DOWN)
		};

	/**------------------------------------------------------
	* Maps mouse event types to/from text strings (names).
	*------------------------------------------------------*/
	StringIntPair[]		stringToMouseEventMap =
		{
		new StringIntPair(	"<Btn1Down>", 		Mi_LEFT_MOUSE_DOWN_EVENT),
		new StringIntPair(	"<Btn1Up>", 		Mi_LEFT_MOUSE_UP_EVENT),
		new StringIntPair(	"<Btn1Click>",		Mi_LEFT_MOUSE_CLICK_EVENT),
		new StringIntPair(	"<Btn1Drag>",		Mi_LEFT_MOUSE_DRAG_EVENT),
		new StringIntPair(	"<Btn1StartDrag>",	Mi_LEFT_MOUSE_START_DRAG_EVENT),
		new StringIntPair(	"<Btn1DblClick>",	Mi_LEFT_MOUSE_DBLCLICK_EVENT),
		new StringIntPair(	"<Btn1TripleClick>",	Mi_LEFT_MOUSE_TRIPLECLICK_EVENT),
	
		new StringIntPair(	"<Btn2Down>", 		Mi_MIDDLE_MOUSE_DOWN_EVENT),
		new StringIntPair(	"<Btn2Up>", 		Mi_MIDDLE_MOUSE_UP_EVENT),
		new StringIntPair(	"<Btn2Click>",		Mi_MIDDLE_MOUSE_CLICK_EVENT),
		new StringIntPair(	"<Btn2Drag>",		Mi_MIDDLE_MOUSE_DRAG_EVENT),
		new StringIntPair(	"<Btn2StartDrag>",	Mi_MIDDLE_MOUSE_START_DRAG_EVENT),
		new StringIntPair(	"<Btn2DblClick>",	Mi_MIDDLE_MOUSE_DBLCLICK_EVENT),
		new StringIntPair(	"<Btn2TripleClick>",	Mi_MIDDLE_MOUSE_TRIPLECLICK_EVENT),
	
		new StringIntPair(	"<Btn3Down>", 		Mi_RIGHT_MOUSE_DOWN_EVENT),
		new StringIntPair(	"<Btn3Up>", 		Mi_RIGHT_MOUSE_UP_EVENT),
		new StringIntPair(	"<Btn3Click>",		Mi_RIGHT_MOUSE_CLICK_EVENT),
		new StringIntPair(	"<Btn3Drag>",		Mi_RIGHT_MOUSE_DRAG_EVENT),
		new StringIntPair(	"<Btn3StartDrag>",	Mi_RIGHT_MOUSE_START_DRAG_EVENT),
		new StringIntPair(	"<Btn3DblClick>",	Mi_RIGHT_MOUSE_DBLCLICK_EVENT),
		new StringIntPair(	"<Btn3TripleClick>",	Mi_RIGHT_MOUSE_TRIPLECLICK_EVENT),
	
		new StringIntPair(	"<Btn2+3Down>", 	Mi_MIDDLE_PLUS_RIGHT_MOUSE_DOWN_EVENT),
		new StringIntPair(	"<Btn2+3Up>", 		Mi_MIDDLE_PLUS_RIGHT_MOUSE_UP_EVENT),
		new StringIntPair(	"<Btn2+3Click>",	Mi_MIDDLE_PLUS_RIGHT_MOUSE_CLICK_EVENT),
		new StringIntPair(	"<Btn2+3Drag>",		Mi_MIDDLE_PLUS_RIGHT_MOUSE_DRAG_EVENT),
		new StringIntPair(	"<Btn2+3StartDrag>",	Mi_MIDDLE_PLUS_RIGHT_MOUSE_START_DRAG_EVENT),
		new StringIntPair(	"<Btn2+3DblClick>",	Mi_MIDDLE_PLUS_RIGHT_MOUSE_DBLCLICK_EVENT),
		new StringIntPair(	"<Btn2+3TripleClick>",	Mi_MIDDLE_PLUS_RIGHT_MOUSE_TRIPLECLICK_EVENT),
	
		new StringIntPair(	"<Motion>",		Mi_MOUSE_MOTION_EVENT),
		new StringIntPair(	"<WinRepaint>",		Mi_WINDOW_REPAINT_EVENT),
		new StringIntPair(	"<WinExit>",		Mi_WINDOW_EXIT_EVENT),
		new StringIntPair(	"<WinEnter>",		Mi_WINDOW_ENTER_EVENT),
		new StringIntPair(	"<MouseEnter>",		Mi_MOUSE_ENTER_EVENT),
		new StringIntPair(	"<MouseExit>",		Mi_MOUSE_EXIT_EVENT),
		new StringIntPair(	"<WinResize>",		Mi_WINDOW_RESIZE_EVENT),
		new StringIntPair(	"<WinExpose>",		Mi_WINDOW_EXPOSE_EVENT),
		new StringIntPair(	"<WinDestroyed>",	Mi_WINDOW_DESTROY_EVENT),
		new StringIntPair(	"<TimerTick>",		Mi_TIMER_TICK_EVENT),
		new StringIntPair(	"<Idle>",		Mi_IDLE_EVENT),
		new StringIntPair(	"<NoLongerIdle>",	Mi_NO_LONGER_IDLE_EVENT),
		new StringIntPair(	"<keyPress>",		Mi_KEY_PRESS_EVENT),
		new StringIntPair(	"<keyRelease>",		Mi_KEY_RELEASE_EVENT),

		new StringIntPair(	"<scrollLineUp>",	Mi_SCROLL_LINE_UP_EVENT),
		new StringIntPair(	"<scrollLineDown>",	Mi_SCROLL_LINE_DOWN_EVENT),
		new StringIntPair(	"<scrollChunkUp>",	Mi_SCROLL_CHUNK_UP_EVENT),
		new StringIntPair(	"<scrollChunkDown>",	Mi_SCROLL_CHUNK_DOWN_EVENT),
		new StringIntPair(	"<scrollPageUp>",	Mi_SCROLL_PAGE_UP_EVENT),
		new StringIntPair(	"<scrollPageDown>",	Mi_SCROLL_PAGE_DOWN_EVENT),
		new StringIntPair(	"<scrollToTop>",	Mi_SCROLL_TO_TOP_EVENT),
		new StringIntPair(	"<scrollToBottom>",	Mi_SCROLL_TO_BOTTOM_EVENT),
		new StringIntPair(	"<scrollToVerticalPos>",Mi_SCROLL_TO_VERTICAL_LOCATION_EVENT),

		new StringIntPair(	"<scrollLineRight>",	Mi_SCROLL_LINE_RIGHT_EVENT),
		new StringIntPair(	"<scrollLineLeft>",	Mi_SCROLL_LINE_LEFT_EVENT),
		new StringIntPair(	"<scrollChunkRight>",	Mi_SCROLL_CHUNK_RIGHT_EVENT),
		new StringIntPair(	"<scrollChunkLeft>",	Mi_SCROLL_CHUNK_LEFT_EVENT),
		new StringIntPair(	"<scrollPageRight>",	Mi_SCROLL_PAGE_RIGHT_EVENT),
		new StringIntPair(	"<scrollPageLeft>",	Mi_SCROLL_PAGE_LEFT_EVENT),
		new StringIntPair(	"<scrollToRightSide>",	Mi_SCROLL_TO_RIGHTSIDE_EVENT),
		new StringIntPair(	"<scrollToLeftSide>",	Mi_SCROLL_TO_LEFTSIDE_EVENT),
		new StringIntPair(	"<scrollToHorizontalPos>",Mi_SCROLL_TO_HORIZONTAL_LOCATION_EVENT)
		};

	/**------------------------------------------------------
	* Maps key event types to/from text strings (names).
	*------------------------------------------------------*/
	StringIntPair[]		stringToKeyEventMap =
		{
		new StringIntPair(	"LeftArrow", 		Mi_LEFT_ARROW_KEY),
		new StringIntPair(	"RightArrow",		Mi_RIGHT_ARROW_KEY),
		new StringIntPair(	"UpArrow",		Mi_UP_ARROW_KEY),
		new StringIntPair(	"DownArrow",		Mi_DOWN_ARROW_KEY),
		new StringIntPair(	"Enter",		Mi_ENTER_KEY),
		new StringIntPair(	"Home",			Mi_HOME_KEY),
		new StringIntPair(	"End",			Mi_END_KEY),
		new StringIntPair(	"PgDn",			Mi_PAGE_DOWN_KEY),
		new StringIntPair(	"PgUp",			Mi_PAGE_UP_KEY),
		new StringIntPair(	"Delete",		Mi_DELETE_KEY),
		new StringIntPair(	"Esc",			Mi_ESC_KEY),
		new StringIntPair(	"Space",		Mi_SPACE_KEY),
		new StringIntPair(	"BackSpace",		Mi_BACKSPACE_KEY),
		new StringIntPair(	"Tab",			Mi_TAB_KEY),
		new StringIntPair(	"F1",			Mi_F1_KEY),
		new StringIntPair(	"F2",			Mi_F2_KEY),
		new StringIntPair(	"F3",			Mi_F3_KEY),
		new StringIntPair(	"F4",			Mi_F4_KEY),
		new StringIntPair(	"F5",			Mi_F5_KEY),
		new StringIntPair(	"F6",			Mi_F6_KEY),
		new StringIntPair(	"F7",			Mi_F7_KEY),
		new StringIntPair(	"F8",			Mi_F8_KEY),
		new StringIntPair(	"F9",			Mi_F9_KEY),
		new StringIntPair(	"F10",			Mi_F10_KEY),
		new StringIntPair(	"F11",			Mi_F11_KEY),
		new StringIntPair(	"F12",			Mi_F12_KEY)
		};

	}


