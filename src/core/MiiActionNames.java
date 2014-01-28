
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
 * This interface assigns names to action types. Used for debugging/logging
 * though could also be used by a parser of a textual description (XML?) of
 * the action/action handler network.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiActionNames extends MiiActionTypes
	{
	// ---------------------------------------------------------------
	// The names of the action phases.
	// ---------------------------------------------------------------
	String 	Mi_COMMIT_ACTION_PHASE_NAME			= "commit";
	String	Mi_REQUEST_ACTION_PHASE_NAME			= "request";
	String	Mi_EXECUTE_ACTION_PHASE_NAME			= "execute";
	String 	Mi_CANCEL_ACTION_PHASE_NAME			= "cancel";

	String	Mi_ACTIONS_OF_PARTS_OF_OBSERVED_NAME		= "actionsOfParts";
	String	Mi_ACTIONS_OF_OBSERVED_NAME			= "actionsOfObserved";

	// ---------------------------------------------------------------
	// Table for action control bit names
	// ---------------------------------------------------------------
	String[] actionBitMaskNames =
		{
	 	Mi_COMMIT_ACTION_PHASE_NAME,
		Mi_REQUEST_ACTION_PHASE_NAME,
		Mi_EXECUTE_ACTION_PHASE_NAME,
	 	Mi_CANCEL_ACTION_PHASE_NAME,

		Mi_ACTIONS_OF_PARTS_OF_OBSERVED_NAME,
		Mi_ACTIONS_OF_OBSERVED_NAME
		};

	// ---------------------------------------------------------------
	// Table for action control bits
	// ---------------------------------------------------------------
	int[] actionBitMasks =
		{
	 	Mi_COMMIT_ACTION_PHASE,
		Mi_REQUEST_ACTION_PHASE,
		Mi_EXECUTE_ACTION_PHASE,
	 	Mi_CANCEL_ACTION_PHASE,

		Mi_ACTIONS_OF_PARTS_OF_OBSERVED,
		Mi_ACTIONS_OF_OBSERVED
		};

	// ---------------------------------------------------------------
	// The names of special action types.
	// ---------------------------------------------------------------
	String	Mi_ALL_ACTION_TYPES_NAME			= "All actions";

	// ---------------------------------------------------------------
	// The names of action types.
	// ---------------------------------------------------------------
	String	Mi_CREATE_ACTION_NAME				= "Create";
	String	Mi_DELETE_ACTION_NAME				= "Delete";
	String	Mi_COPY_ACTION_NAME				= "Copy";
	String	Mi_REPLACE_ACTION_NAME				= "Replace";
	String	Mi_REMOVE_FROM_CONTAINER_ACTION			= "RemoveFromContainer";
	String	Mi_ADD_TO_CONTAINER_ACTION			= "AddToContainer";

	String	Mi_DRAG_AND_DROP_PICKUP_ACTION_NAME		= "DragAndDropPickup";
	String	Mi_DRAG_AND_DROP_MOVE_ACTION_NAME		= "DragAndDropMove";
	String	Mi_DRAG_AND_DROP_ENTER_ACTION_NAME		= "DragAndDropEnter";
	String	Mi_DRAG_AND_DROP_EXIT_ACTION_NAME		= "DragAndDropExit";
	String	Mi_DRAG_AND_DROP_PAUSE_ACTION_NAME		= "DragAndDropPause";
	String	Mi_DRAG_AND_DROP_CONTINUE_ACTION_NAME		= "DragAndDropContinue";
	String	Mi_DRAG_AND_DROP_CANCEL_ACTION_NAME		= "DragAndDropCancel";
	String	Mi_DRAG_AND_DROP_COMMIT_ACTION_NAME		= "DragAndDropCommit";
	String	Mi_DRAG_AND_DROP_VETO_ACTION_NAME		= "DragAndDropVeto";



	String	Mi_SELECTED_ACTION_NAME				= "Selected";
	String	Mi_DESELECTED_ACTION_NAME			= "DeSelected";
	String	Mi_ACTIVATED_ACTION_NAME			= "Activated";
	String	Mi_SELECT_REPEATED_ACTION_NAME			= "Repeat select";
	String	Mi_GOT_MOUSE_FOCUS_ACTION_NAME			= "Mouse focus";
	String	Mi_LOST_MOUSE_FOCUS_ACTION_NAME			= "Lost mouse focus";
	String	Mi_GOT_KEYBOARD_FOCUS_ACTION_NAME		= "Keyboard focus";
	String	Mi_LOST_KEYBOARD_FOCUS_ACTION_NAME		= "Lost keyboard focus";
	String	Mi_GOT_ENTER_KEY_FOCUS_ACTION_NAME		= "Enter key focus";
	String	Mi_LOST_ENTER_KEY_FOCUS_ACTION_NAME		= "Lost Enter key focus";
	String	Mi_MOUSE_ENTER_ACTION_NAME			= "Mouse enter";
	String	Mi_MOUSE_EXIT_ACTION_NAME			= "Mouse exit";
	String	Mi_INVISIBLE_ACTION_NAME			= "Invisible";
	String	Mi_VISIBLE_ACTION_NAME				= "Visible";
	String	Mi_PART_SHOWING_ACTION_NAME			= "Part showing";
	String	Mi_PART_NOT_SHOWING_ACTION_NAME			= "Part not showing";
	String	Mi_HIDDEN_ACTION_NAME				= "Hidden";
	String	Mi_UNHIDDEN_ACTION_NAME				= "Unhidden";

	String	Mi_TEXT_CHANGE_ACTION_NAME			= "Text change";
	String	Mi_MENU_POPPED_UP_ACTION_NAME			= "Menu popup";
	String	Mi_MENU_POPPED_DOWN_ACTION_NAME			= "Menu close";
	String	Mi_TABBED_FOLDER_OPENED_ACTION_NAME		= "Folder open"; 
	String	Mi_TABBED_FOLDER_CLOSED_ACTION_NAME		= "Folder close";
	String	Mi_INVALID_VALUE_ACTION_NAME			= "Invalid value";
	String	Mi_VALUE_CHANGED_ACTION_NAME			= "Value changed";
	String	Mi_NODE_EXPANDED_ACTION_NAME			= "Node expanded";
	String	Mi_NODE_COLLAPSED_ACTION_NAME			= "Node collapsed";
	String	Mi_ENTER_KEY_ACTION_NAME			= "Enter key";

	String	Mi_ITEM_SELECTED_ACTION_NAME			= "Item selected";
	String	Mi_ITEM_DESELECTED_ACTION_NAME			= "Item deSelected";
	String	Mi_ITEM_BROWSED_ACTION_NAME			= "Item browsed";
	String	Mi_ITEM_DEBROWSED_ACTION_NAME			= "Item deBrowsed";
	String	Mi_ITEM_ADDED_ACTION_NAME			= "Item added";
	String	Mi_ITEM_REMOVED_ACTION_NAME			= "Item deleted";
	String	Mi_ALL_ITEMS_SELECTED_ACTION_NAME		= "Item all selected";
	String	Mi_ALL_ITEMS_DESELECTED_ACTION_NAME		= "Item all deSelected";
	String	Mi_NO_ITEMS_SELECTED_ACTION_NAME		= "Item none selected";
	String	Mi_ONE_ITEM_SELECTED_ACTION_NAME		= "Item one selected";
	String	Mi_MANY_ITEMS_SELECTED_ACTION_NAME		= "Item many selected";
	String	Mi_ITEM_SCROLLED_ACTION_NAME			= "Item scrolled";
	String	Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION_NAME	= "Content scrolled and magnified";

	String	Mi_EDITOR_VIEWPORT_CHANGED_ACTION_NAME		= "Viewport changed";
	String	Mi_EDITOR_WORLD_TRANSLATED_ACTION_NAME		= "Viewport panned";
	String	Mi_EDITOR_WORLD_RESIZED_ACTION_NAME		= "Viewport zoomed";
	String	Mi_EDITOR_DEVICE_TRANSLATED_ACTION_NAME		= "Editor moved";
	String	Mi_EDITOR_DEVICE_RESIZED_ACTION_NAME		= "Editor resized";
	String	Mi_EDITOR_UNIVERSE_RESIZED_ACTION_NAME		= "Viewport universe resized";
	String	Mi_EDITOR_CONTENTS_GEOMETRY_CHANGED_ACTION_NAME= "Editor contents geometry changed";
	String	Mi_EDITOR_LAYER_ADDED_ACTION_NAME		= "Layer added";
	String	Mi_EDITOR_LAYER_REMOVED_ACTION_NAME		= "Layer removed";
	String	Mi_EDITOR_LAYER_ORDER_CHANGED_ACTION_NAME	= "Layer order changed";
	String	Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION_NAME	= "Current layer changed ";

	String	Mi_WINDOW_CLOSE_ACTION_NAME			= "Window close";
	String	Mi_WINDOW_ICONIFY_ACTION_NAME			= "Window iconify";
	String	Mi_WINDOW_DEICONIFY_ACTION_NAME			= "Window deIconify";
	String	Mi_WINDOW_OPEN_ACTION_NAME			= "Window open";
	String	Mi_WINDOW_OK_ACTION_NAME			= "Window ok";
	String	Mi_WINDOW_CANCEL_ACTION_NAME			= "Window cancel";
	String	Mi_WINDOW_HELP_ACTION_NAME			= "Window help";
	String	Mi_WINDOW_FULLSCREEN_ACTION_NAME		= "Window fullscreen";
	String	Mi_WINDOW_NORMALSIZE_ACTION_NAME		= "Window normalsize";
	String	Mi_WINDOW_DESTROY_ACTION_NAME			= "Window destroy";

	String	Mi_CLIPBOARD_NOW_HAS_DATA_ACTION_NAME		= "Clipboard has data";

	String	Mi_TRANSACTION_MANAGER_CHANGED_ACTION_NAME	= "Transaction manager changed";
	String	Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION_NAME 	= "Transaction created";
	String	Mi_TRANSACTION_MANAGER_EXECUTION_START_UNDO_ACTION_NAME	= "Transaction start undo";
	String	Mi_TRANSACTION_MANAGER_EXECUTION_END_UNDO_ACTION_NAME	= "Transaction end undo";
	String	Mi_TRANSACTION_MANAGER_EXECUTION_START_REDO_ACTION_NAME	= "Transaction start redo";
	String	Mi_TRANSACTION_MANAGER_EXECUTION_END_REDO_ACTION_NAME	= "Transaction end redo";
	String	Mi_TRANSACTION_MANAGER_EXECUTION_START_REPEAT_ACTION_NAME= "Transaction start repeat";
	String	Mi_TRANSACTION_MANAGER_EXECUTION_END_REPEAT_ACTION_NAME	= "Transaction end repeat";

	String	Mi_DATA_IMPORT_ACTION_NAME			= "Data import";
	String	Mi_CONNECT_SOURCE_ACTION_NAME			= "Connect to source";
	String	Mi_CONNECT_DESTINATION_ACTION_NAME		= "Connect to destination";
	String	Mi_CONNECT_ACTION_NAME				= "Connect";
	String	Mi_DISCONNECT_SOURCE_ACTION_NAME		= "Disconnect source";
	String	Mi_DISCONNECT_DESTINATION_ACTION_NAME		= "Disconnect destination";
	String	Mi_DISCONNECT_ACTION_NAME			= "Disconnect";
	String	Mi_CREATED_CONNECTION_CONNECTION_POINT_ACTION_NAME = "Create Connection To Connection Point";
	String	Mi_DELETED_CONNECTION_CONNECTION_POINT_ACTION_NAME = "Delete Connection To Connection Point";
	String	Mi_STATUS_BAR_FOCUS_CHANGED_ACTION_NAME		= "StatusBar focus change";
	String	Mi_ICONIFY_ACTION_NAME				= "Iconify";
	String	Mi_DEICONIFY_ACTION_NAME			= "DeIconify";
	String	Mi_GROUP_ACTION_NAME				= "Group";
	String	Mi_UNGROUP_ACTION_NAME				= "UnGroup";
	String	Mi_FORMAT_ACTION_NAME				= "Format";
	String	Mi_UNFORMAT_ACTION_NAME				= "UnFormat";

	String	Mi_GEOMETRY_CHANGE_ACTION_NAME			= "GeometryChange";
	String	Mi_SIZE_CHANGE_ACTION_NAME			= "SizeChange";
	String	Mi_POSITION_CHANGE_ACTION_NAME			= "PositionChange";
	String	Mi_APPEARANCE_CHANGE_ACTION_NAME		= "AppearanceChange";
	String	Mi_DRAW_ACTION_NAME				= "Draw";

	String	Mi_DOCUMENT_CHANGE_ACTION_NAME			= "DocumentChange";

	String	Mi_DECREASE_ACTION_NAME				= "Decrease";
	String	Mi_INCREASE_ACTION_NAME				= "Increase";
	String	Mi_PROPERTY_CHANGE_ACTION_NAME			= "PropertyChange";
	String	Mi_NAME_CHANGE_ACTION_NAME			= "NameChange";

	String	Mi_DISPLAY_PROPERTIES_ACTION_NAME		= "DisplayProperties";

	String	Mi_INTERACTIVELY_MOVING_PART_ACTION_NAME	= "MovingPart";
	String	Mi_INTERACTIVELY_COMPLETED_MOVE_PART_ACTION_NAME= "CompletedMovePart";
	String	Mi_INTERACTIVELY_CANCELED_MOVE_PART_ACTION_NAME	= "CanceledMovePart";
	String	Mi_INTERACTIVELY_FAILED_MOVE_PART_ACTION_NAME	= "FailedMovePart";

	String	Mi_INTERACTIVELY_CREATING_CONNECTION_ACTION_NAME	= "CreatingConnection";
	String	Mi_INTERACTIVELY_COMPLETED_CREATE_CONNECTION_ACTION_NAME = "CompletedCreateConnection";
	String	Mi_INTERACTIVELY_CANCELED_CREATE_CONNECTION_ACTION_NAME = "CanceledCreateConnection";
	String	Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION_NAME = "FailedCreateConnection";
	String	Mi_INTERACTIVELY_COMPLETED_RUBBER_STAMP_PART_ACTION_NAME ="InteractivelyCompletedRubberStampPart";
	String	Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION_NAME = "InteractivelyCompletedDrawNewPart";

	// ---------------------------------------------------------------
	// The array of the names of action phases.
	// ---------------------------------------------------------------
	String[]	actionPhaseNames		=
		{
	 	Mi_COMMIT_ACTION_PHASE_NAME,
		Mi_REQUEST_ACTION_PHASE_NAME,
		Mi_EXECUTE_ACTION_PHASE_NAME,
	 	Mi_CANCEL_ACTION_PHASE_NAME
		};

	// ---------------------------------------------------------------
	// The array of the names of all built-in action types.
	// ---------------------------------------------------------------
	String[]	actionNames			=
		{
		MiiTypes.Mi_NONE_NAME,
		Mi_ALL_ACTION_TYPES_NAME,

		Mi_CREATE_ACTION_NAME,
		Mi_DELETE_ACTION_NAME,
		Mi_COPY_ACTION_NAME,
		Mi_REPLACE_ACTION_NAME,
		Mi_REMOVE_FROM_CONTAINER_ACTION,
		Mi_ADD_TO_CONTAINER_ACTION,

		Mi_DRAG_AND_DROP_PICKUP_ACTION_NAME,
		Mi_DRAG_AND_DROP_MOVE_ACTION_NAME,
		Mi_DRAG_AND_DROP_ENTER_ACTION_NAME,
		Mi_DRAG_AND_DROP_EXIT_ACTION_NAME,
		Mi_DRAG_AND_DROP_PAUSE_ACTION_NAME,
		Mi_DRAG_AND_DROP_CONTINUE_ACTION_NAME,
		Mi_DRAG_AND_DROP_CANCEL_ACTION_NAME,
		Mi_DRAG_AND_DROP_COMMIT_ACTION_NAME,
		Mi_DRAG_AND_DROP_VETO_ACTION_NAME,

		Mi_SELECTED_ACTION_NAME,
		Mi_DESELECTED_ACTION_NAME,
		Mi_ACTIVATED_ACTION_NAME,
		Mi_SELECT_REPEATED_ACTION_NAME,
		Mi_GOT_MOUSE_FOCUS_ACTION_NAME,
		Mi_LOST_MOUSE_FOCUS_ACTION_NAME,
		Mi_GOT_KEYBOARD_FOCUS_ACTION_NAME,
		Mi_LOST_KEYBOARD_FOCUS_ACTION_NAME,
		Mi_GOT_ENTER_KEY_FOCUS_ACTION_NAME,
		Mi_LOST_ENTER_KEY_FOCUS_ACTION_NAME,
		Mi_MOUSE_ENTER_ACTION_NAME,
		Mi_MOUSE_EXIT_ACTION_NAME,
		Mi_INVISIBLE_ACTION_NAME,
		Mi_VISIBLE_ACTION_NAME,
		Mi_PART_SHOWING_ACTION_NAME,
		Mi_PART_NOT_SHOWING_ACTION_NAME,
		Mi_HIDDEN_ACTION_NAME,
		Mi_UNHIDDEN_ACTION_NAME,

		Mi_TEXT_CHANGE_ACTION_NAME,
		Mi_MENU_POPPED_UP_ACTION_NAME,
		Mi_MENU_POPPED_DOWN_ACTION_NAME,
		Mi_TABBED_FOLDER_OPENED_ACTION_NAME,
		Mi_TABBED_FOLDER_CLOSED_ACTION_NAME,
		Mi_INVALID_VALUE_ACTION_NAME,
		Mi_VALUE_CHANGED_ACTION_NAME,
		Mi_ENTER_KEY_ACTION_NAME,
		Mi_NODE_EXPANDED_ACTION_NAME,
		Mi_NODE_COLLAPSED_ACTION_NAME,

		Mi_ITEM_SELECTED_ACTION_NAME,
		Mi_ITEM_DESELECTED_ACTION_NAME,
		Mi_ITEM_BROWSED_ACTION_NAME,
		Mi_ITEM_DEBROWSED_ACTION_NAME,
		Mi_ITEM_ADDED_ACTION_NAME,
		Mi_ITEM_REMOVED_ACTION_NAME,
		Mi_ALL_ITEMS_SELECTED_ACTION_NAME,
		Mi_ALL_ITEMS_DESELECTED_ACTION_NAME,
		Mi_NO_ITEMS_SELECTED_ACTION_NAME,
		Mi_ONE_ITEM_SELECTED_ACTION_NAME,
		Mi_MANY_ITEMS_SELECTED_ACTION_NAME,
		Mi_ITEM_SCROLLED_ACTION_NAME,
		Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION_NAME,

		Mi_EDITOR_VIEWPORT_CHANGED_ACTION_NAME,
		Mi_EDITOR_WORLD_TRANSLATED_ACTION_NAME,
		Mi_EDITOR_WORLD_RESIZED_ACTION_NAME,
		Mi_EDITOR_DEVICE_TRANSLATED_ACTION_NAME,
		Mi_EDITOR_DEVICE_RESIZED_ACTION_NAME,
		Mi_EDITOR_UNIVERSE_RESIZED_ACTION_NAME,
		Mi_EDITOR_CONTENTS_GEOMETRY_CHANGED_ACTION_NAME,
		Mi_EDITOR_LAYER_ADDED_ACTION_NAME,
		Mi_EDITOR_LAYER_REMOVED_ACTION_NAME,
		Mi_EDITOR_LAYER_ORDER_CHANGED_ACTION_NAME,
		Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION_NAME,

		Mi_WINDOW_CLOSE_ACTION_NAME,
		Mi_WINDOW_ICONIFY_ACTION_NAME,
		Mi_WINDOW_DEICONIFY_ACTION_NAME,
		Mi_WINDOW_OPEN_ACTION_NAME,
		Mi_WINDOW_OK_ACTION_NAME,
		Mi_WINDOW_CANCEL_ACTION_NAME,
		Mi_WINDOW_HELP_ACTION_NAME,
		Mi_WINDOW_FULLSCREEN_ACTION_NAME,
		Mi_WINDOW_NORMALSIZE_ACTION_NAME,
		Mi_WINDOW_DESTROY_ACTION_NAME,

		Mi_CLIPBOARD_NOW_HAS_DATA_ACTION_NAME,
		Mi_TRANSACTION_MANAGER_CHANGED_ACTION_NAME,
		Mi_TRANSACTION_MANAGER_NEW_TRANSACTION_ACTION_NAME,
		Mi_TRANSACTION_MANAGER_EXECUTION_START_UNDO_ACTION_NAME,
		Mi_TRANSACTION_MANAGER_EXECUTION_END_UNDO_ACTION_NAME,
		Mi_TRANSACTION_MANAGER_EXECUTION_START_REDO_ACTION_NAME,
		Mi_TRANSACTION_MANAGER_EXECUTION_END_REDO_ACTION_NAME,
		Mi_TRANSACTION_MANAGER_EXECUTION_START_REPEAT_ACTION_NAME,
		Mi_TRANSACTION_MANAGER_EXECUTION_END_REPEAT_ACTION_NAME,
		Mi_DATA_IMPORT_ACTION_NAME,
		Mi_CONNECT_SOURCE_ACTION_NAME,
		Mi_CONNECT_DESTINATION_ACTION_NAME,
		Mi_CONNECT_ACTION_NAME,
		Mi_DISCONNECT_SOURCE_ACTION_NAME,
		Mi_DISCONNECT_DESTINATION_ACTION_NAME,
		Mi_DISCONNECT_ACTION_NAME,
		Mi_CREATED_CONNECTION_CONNECTION_POINT_ACTION_NAME,
		Mi_DELETED_CONNECTION_CONNECTION_POINT_ACTION_NAME,
		Mi_STATUS_BAR_FOCUS_CHANGED_ACTION_NAME,
		Mi_ICONIFY_ACTION_NAME,
		Mi_DEICONIFY_ACTION_NAME,
		Mi_GROUP_ACTION_NAME,
		Mi_UNGROUP_ACTION_NAME,
		Mi_FORMAT_ACTION_NAME,
		Mi_UNFORMAT_ACTION_NAME,

		Mi_GEOMETRY_CHANGE_ACTION_NAME,
		Mi_SIZE_CHANGE_ACTION_NAME,
		Mi_POSITION_CHANGE_ACTION_NAME,
		Mi_APPEARANCE_CHANGE_ACTION_NAME,
		Mi_DRAW_ACTION_NAME,

		Mi_DOCUMENT_CHANGE_ACTION_NAME,
		Mi_DECREASE_ACTION_NAME,
		Mi_INCREASE_ACTION_NAME,
		Mi_PROPERTY_CHANGE_ACTION_NAME,
		Mi_NAME_CHANGE_ACTION_NAME,

		Mi_DISPLAY_PROPERTIES_ACTION_NAME,

		Mi_INTERACTIVELY_MOVING_PART_ACTION_NAME		,
		Mi_INTERACTIVELY_COMPLETED_MOVE_PART_ACTION_NAME	,
		Mi_INTERACTIVELY_CANCELED_MOVE_PART_ACTION_NAME		,
		Mi_INTERACTIVELY_FAILED_MOVE_PART_ACTION_NAME		,

		Mi_INTERACTIVELY_CREATING_CONNECTION_ACTION_NAME	,
		Mi_INTERACTIVELY_COMPLETED_CREATE_CONNECTION_ACTION_NAME,
		Mi_INTERACTIVELY_CANCELED_CREATE_CONNECTION_ACTION_NAME ,
		Mi_INTERACTIVELY_FAILED_CREATE_CONNECTION_ACTION_NAME 	,
		Mi_INTERACTIVELY_COMPLETED_RUBBER_STAMP_PART_ACTION_NAME,
		Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION_NAME,
		};
	}

