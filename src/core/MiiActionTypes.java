
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


/**----------------------------------------------------------------------------------------------
 * This interface specifies all the built-in types of MiiActions.
 * <p>
 * New MiiAction types can be defined at runtime by using the following
 * approach:
 * <p><pre>
 * protected static final int MY_EDIT_TEXT_ACTION_TYPE = MiActionManager.registerAction("myEditText");
 *
 * and this can be used thus:
 * 
 * miPart.appendActionHandler(myActionHandler, 
 *          new MiEvent(Mi_LEFT_MOUSE_DBLCLICK_EVENT), MY_EDIT_TEXT_ACTION_TYPE);
 *
 * </pre>
 * MiiActions are dispatched to their MiiActionHandlers in 4 distinct phases:
 * <p>
 * 1. Mi_REQUEST_ACTION_PHASE
 * <p>
 * In this phase, the action is sent to all MiiActionHandlers which have requested 
 * interest in this action during this phase. Each MiiActionHandler has the opportunity
 * to veto any further dispatch of the action.
 *
 * <p>
 * 2. Mi_EXECUTE_ACTION_PHASE
 * <p>
 * In this phase, the action is sent to all MiiActionHandlers which have requested 
 * interest in this action during this phase. Each MiiActionHandler can decide to
 * execute code either before or instead (this is supported only rarely by the
 * code that dispatches the MiiAction - for example the MiDragAndDropManager
 * or MiPart#copy) of the default code. 
 *
 * <p>
 * 3. Mi_COMMIT_ACTION_PHASE
 * <p>
 * In this phase, the action is sent to all MiiActionHandlers which have requested 
 * interest in this action during this phase. This is the typical phase during
 * which the MiiActionHandler executes some code in response to the action's occurance.
 *
 * <p>
 * 4. Mi_CANCEL_ACTION_PHASE
 * <p>
 * In this phase, the action is sent to all MiiActionHandlers which have requested 
 * interest in this action during this phase. This is the phsae that the MiiAction
 * enters if it was vetoed during the request phase. This allows all MiiActionHandlers
 * to free resources they may have allocated resources during the request phase.
 *
 * @see MiiAction
 * @see MiiActionHandler
 * @see MiPart#appendActionHandler
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiActionTypes
	{
	// ---------------------------------------------------------------
	// 4 bits for action phases... these values leave room for 1 << 27
	// action types.
	// ---------------------------------------------------------------
	int 	Mi_COMMIT_ACTION_PHASE			= (1 << 28);
	int	Mi_REQUEST_ACTION_PHASE			= (1 << 29);
	int	Mi_EXECUTE_ACTION_PHASE			= (1 << 30);
	int 	Mi_CANCEL_ACTION_PHASE			= (1 << 31);
	int	Mi_ACTION_PHASE_MASK			= (0xf << 28);

	int	Mi_ACTIONS_OF_PARTS_OF_OBSERVED		= (1 << 27);
	int	Mi_ACTIONS_OF_OBSERVED			= (1 << 26);

	int	Mi_ALL_ACTION_TYPES			= 1;

	int	Mi_CREATE_ACTION			= 2;
	int	Mi_DELETE_ACTION			= 3;
	int	Mi_COPY_ACTION				= 4;
	int	Mi_REPLACE_ACTION			= 5;
	int	Mi_REMOVE_FROM_CONTAINER_ACTION		= 6;
	int	Mi_ADD_TO_CONTAINER_ACTION		= 7;

	int	Mi_DRAG_AND_DROP_PICKUP_ACTION		= 8;
	int	Mi_DRAG_AND_DROP_MOVE_ACTION		= 9;
	int	Mi_DRAG_AND_DROP_ENTER_ACTION		= 10;
	int	Mi_DRAG_AND_DROP_EXIT_ACTION		= 11;
	int	Mi_DRAG_AND_DROP_PAUSE_ACTION		= 12;
	int	Mi_DRAG_AND_DROP_CONTINUE_ACTION	= 13;
	int	Mi_DRAG_AND_DROP_CANCEL_ACTION		= 14;
	int	Mi_DRAG_AND_DROP_COMMIT_ACTION		= 15;
	int	Mi_DRAG_AND_DROP_VETO_ACTION		= 16;

	int	Mi_SELECTED_ACTION			= 17;
	int	Mi_DESELECTED_ACTION			= 18;
	int	Mi_ACTIVATED_ACTION			= 19;
	int	Mi_SELECT_REPEATED_ACTION		= 20;
	int	Mi_GOT_MOUSE_FOCUS_ACTION		= 21;
	int	Mi_LOST_MOUSE_FOCUS_ACTION		= 22;
	int	Mi_GOT_KEYBOARD_FOCUS_ACTION		= 23;
	int	Mi_LOST_KEYBOARD_FOCUS_ACTION		= 24;
	int	Mi_GOT_ENTER_KEY_FOCUS_ACTION		= 25;
	int	Mi_LOST_ENTER_KEY_FOCUS_ACTION		= 26;
	int	Mi_MOUSE_ENTER_ACTION			= 27;
	int	Mi_MOUSE_EXIT_ACTION			= 28;
	int	Mi_INVISIBLE_ACTION			= 29;
	int	Mi_VISIBLE_ACTION			= 30;
	int	Mi_PART_SHOWING_ACTION			= 31;
	int	Mi_PART_NOT_SHOWING_ACTION		= 32;
	int	Mi_HIDDEN_ACTION			= 33;
	int	Mi_UNHIDDEN_ACTION			= 34;

	int	Mi_TEXT_CHANGE_ACTION			= 35;
	int	Mi_MENU_POPPED_UP_ACTION		= 36;
	int	Mi_MENU_POPPED_DOWN_ACTION		= 37;
	int	Mi_TABBED_FOLDER_OPENED_ACTION		= 38;
	int	Mi_TABBED_FOLDER_CLOSED_ACTION		= 39;
	int	Mi_INVALID_VALUE_ACTION			= 40;
	int	Mi_VALUE_CHANGED_ACTION			= 41;
	int	Mi_ENTER_KEY_ACTION			= 42;
	int	Mi_NODE_EXPANDED_ACTION			= 43;
	int	Mi_NODE_COLLAPSED_ACTION		= 44;

	int	Mi_ITEM_SELECTED_ACTION			= 45;
	int	Mi_ITEM_DESELECTED_ACTION		= 46;
	int	Mi_ITEM_BROWSED_ACTION			= 47;
	int	Mi_ITEM_DEBROWSED_ACTION		= 48;
	int	Mi_ITEM_ADDED_ACTION			= 49;
	int	Mi_ITEM_REMOVED_ACTION			= 50;
	int	Mi_ALL_ITEMS_SELECTED_ACTION		= 51;
	int	Mi_ALL_ITEMS_DESELECTED_ACTION		= 52;
	int	Mi_NO_ITEMS_SELECTED_ACTION		= 53;
	int	Mi_ONE_ITEM_SELECTED_ACTION		= 54;
	int	Mi_MANY_ITEMS_SELECTED_ACTION		= 55;
	int	Mi_ITEM_SCROLLED_ACTION			= 56;
	int	Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION	= 57;

	int	Mi_EDITOR_VIEWPORT_CHANGED_ACTION	= 58;
	int	Mi_EDITOR_WORLD_TRANSLATED_ACTION	= 59;
	int	Mi_EDITOR_WORLD_RESIZED_ACTION		= 60;
	int	Mi_EDITOR_DEVICE_TRANSLATED_ACTION	= 61;
	int	Mi_EDITOR_DEVICE_RESIZED_ACTION		= 62;
	int	Mi_EDITOR_UNIVERSE_RESIZED_ACTION	= 63;
	int	Mi_EDITOR_CONTENTS_GEOMETRY_CHANGED_ACTION= 64;
	int	Mi_EDITOR_LAYER_ADDED_ACTION		= 65;
	int	Mi_EDITOR_LAYER_REMOVED_ACTION		= 66;
	int	Mi_EDITOR_LAYER_ORDER_CHANGED_ACTION	= 67;
	int	Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION	= 68;

	int	Mi_WINDOW_CLOSE_ACTION			= 69;
	int	Mi_WINDOW_ICONIFY_ACTION		= 70;
	int	Mi_WINDOW_DEICONIFY_ACTION		= 71;
	int	Mi_WINDOW_OPEN_ACTION			= 72;
	int	Mi_WINDOW_OK_ACTION			= 73;
	int	Mi_WINDOW_CANCEL_ACTION			= 74;
	int	Mi_WINDOW_HELP_ACTION			= 75;
	int	Mi_WINDOW_FULLSCREEN_ACTION		= 76;
	int	Mi_WINDOW_NORMALSIZE_ACTION		= 77;
	int	Mi_WINDOW_DESTROY_ACTION		= 78;

	int	Mi_CLIPBOARD_NOW_HAS_DATA_ACTION	= 79;

	int	Mi_TRANSACTION_MANAGER_CHANGED_ACTION			= 80;
	int	Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION		= 81;
	int	Mi_TRANSACTION_MANAGER_EXECUTION_START_UNDO_ACTION	= 82;
	int	Mi_TRANSACTION_MANAGER_EXECUTION_END_UNDO_ACTION	= 83;
	int	Mi_TRANSACTION_MANAGER_EXECUTION_START_REDO_ACTION	= 84;
	int	Mi_TRANSACTION_MANAGER_EXECUTION_END_REDO_ACTION	= 85;
	int	Mi_TRANSACTION_MANAGER_EXECUTION_START_REPEAT_ACTION	= 86;
	int	Mi_TRANSACTION_MANAGER_EXECUTION_END_REPEAT_ACTION	= 87;

	int	Mi_DATA_IMPORT_ACTION			= 88;
	int	Mi_CONNECT_SOURCE_ACTION		= 89;
	int	Mi_CONNECT_DESTINATION_ACTION		= 90;
	int	Mi_CONNECT_ACTION			= 91;
	int	Mi_DISCONNECT_SOURCE_ACTION		= 92;
	int	Mi_DISCONNECT_DESTINATION_ACTION	= 93;
	int	Mi_DISCONNECT_ACTION			= 94;
	int	Mi_CREATED_CONNECTION_CONNECTION_POINT_ACTION	= 95;
	int	Mi_DELETED_CONNECTION_CONNECTION_POINT_ACTION	= 96;
	int	Mi_STATUS_BAR_FOCUS_CHANGED_ACTION	= 97;
	int	Mi_ICONIFY_ACTION			= 98;
	int	Mi_DEICONIFY_ACTION			= 99;
	int	Mi_GROUP_ACTION				= 100;
	int	Mi_UNGROUP_ACTION			= 101;
	int	Mi_FORMAT_ACTION			= 102;
	int	Mi_UNFORMAT_ACTION			= 103;

	int	Mi_GEOMETRY_CHANGE_ACTION		= 104;
	int	Mi_SIZE_CHANGE_ACTION			= 105;
	int	Mi_POSITION_CHANGE_ACTION		= 106;
	int	Mi_APPEARANCE_CHANGE_ACTION		= 107;
	int	Mi_DRAW_ACTION				= 108;

	int	Mi_DOCUMENT_CHANGE_ACTION		= 109;

	int	Mi_DECREASE_ACTION			= 110;
	int	Mi_INCREASE_ACTION			= 111;
	int	Mi_PROPERTY_CHANGE_ACTION		= 112;
	int	Mi_NAME_CHANGE_ACTION			= 113;

	int	Mi_DISPLAY_PROPERTIES_ACTION		= 114;

	int	Mi_INTERACTIVELY_MOVING_PART_ACTION		= 115;
	int	Mi_INTERACTIVELY_COMPLETED_MOVE_PART_ACTION	= 116;
	int	Mi_INTERACTIVELY_CANCELED_MOVE_PART_ACTION	= 117;
	int	Mi_INTERACTIVELY_FAILED_MOVE_PART_ACTION	= 118;

	int	Mi_INTERACTIVELY_CREATING_CONNECTION_ACTION		= 119;
	int	Mi_INTERACTIVELY_COMPLETED_CREATE_CONNECTION_ACTION 	= 120;
	int	Mi_INTERACTIVELY_CANCELED_CREATE_CONNECTION_ACTION 	= 121;
	int	Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION 	= 122;
	int	Mi_INTERACTIVELY_COMPLETED_RUBBER_STAMP_PART_ACTION 	= 123;
	int	Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION 	= 124;

	int	Mi_ACTION_TYPE_MASK			= 0x03ff;
	}

