
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
public interface MiiCommandNames
	{
	String		Mi_UNDO_COMMAND_NAME 			= "Undo";
	String		Mi_REDO_COMMAND_NAME 			= "Redo";
	String		Mi_REPEAT_COMMAND_NAME 			= "Repeat";
	String		Mi_CUT_COMMAND_NAME 			= "Cut";
	String		Mi_COPY_COMMAND_NAME 			= "Copy";
	String		Mi_PASTE_COMMAND_NAME 			= "Paste";
	String		Mi_DELETE_COMMAND_NAME 			= "Delete";
	String		Mi_CLEAR_COMMAND_NAME 			= "Clear";
	String		Mi_CREATE_COMMAND_NAME 			= "Create";
	String		Mi_NEW_COMMAND_NAME			= "New";
	String		Mi_OPEN_COMMAND_NAME			= "Open";
	String		Mi_SAVE_COMMAND_NAME			= "Save";
	String		Mi_SAVE_ALL_COMMAND_NAME		= "SaveAll";
	String		Mi_NO_SAVE_ALL_COMMAND_NAME		= "NoSaveAll";
	String		Mi_SAVE_AS_COMMAND_NAME			= "SaveAs";
	String		Mi_CLOSE_COMMAND_NAME			= "Close";
	String		Mi_PRINT_SETUP_COMMAND_NAME		= "PrintSetup";
	String		Mi_PRINT_COMMAND_NAME			= "Print";
	String		Mi_QUIT_COMMAND_NAME			= "Quit";
	String		Mi_EXIT_COMMAND_NAME			= "Exit";
	String		Mi_CANCEL_COMMAND_NAME			= "Cancel";
	String		Mi_DEFAULTS_COMMAND_NAME		= "Defaults";
	String		Mi_OK_COMMAND_NAME			= "OK";
	String		Mi_APPLY_COMMAND_NAME			= "Apply";
	String		Mi_HELP_COMMAND_NAME			= "Help";
	String		Mi_CONTINUE_COMMAND_NAME		= "Continue";
	String		Mi_SELECT_COMMAND_NAME			= "Select";
	String		Mi_SELECT_ALL_COMMAND_NAME		= "SelectAll";
	String		Mi_SELECT_ADDITIONAL_COMMAND_NAME	= "SelectAdditional";
	String		Mi_DESELECT_COMMAND_NAME		= "DeSelect";
	String		Mi_DESELECT_ALL_COMMAND_NAME		= "DeSelectAll";
	String		Mi_ZOOM_IN_COMMAND_NAME			= "ZoomIn";
	String		Mi_ZOOM_OUT_COMMAND_NAME		= "ZoomOut";
	String		Mi_PREVIOUS_COMMAND_NAME		= "Previous";
	String		Mi_NEXT_COMMAND_NAME			= "Next";
	String		Mi_DONE_COMMAND_NAME			= "Done";
	String		Mi_VIEW_ALL_COMMAND_NAME		= "ViewAll";
	String		Mi_VIEW_PREVIOUS_COMMAND_NAME		= "ViewPrevious";
	String		Mi_VIEW_NEXT_COMMAND_NAME		= "ViewNext";
	String		Mi_VIEW_REPEAT_COMMAND_NAME		= "ViewRepeat";

	String		Mi_ZOOM_HOME_COMMAND_NAME		= "ZoomHome";
	String		Mi_ZOOM_TO_FIT_COMMAND_NAME		= "ZoomToFit";
	String		Mi_ZOOM_TO_FIT_WIDTH_COMMAND_NAME	= "ZoomToFitWidth";
	String		Mi_ZOOM_TO_FIT_HEIGHT_COMMAND_NAME	= "ZoomToFitHeight";
	String		Mi_ZOOM_TO_FIT_CONTENT_COMMAND_NAME		= "ZoomToFitContent";
	String		Mi_ZOOM_TO_FIT_CONTENT_WIDTH_COMMAND_NAME	= "ZoomToFitContentWidth";
	String		Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_COMMAND_NAME	= "ZoomToFitContentHeight";

	String		Mi_SCALE_TO_FIT_PAGE_COMMAND_NAME	= "ScaleToFitPage";
	String		Mi_MOVE_TO_FIT_PAGE_COMMAND_NAME	= "MoveToFitPage";
	String		Mi_ADJUST_PAGES_TO_FIT_COMMAND_NAME	= "AdjustPagesToFit";

	String		Mi_BRING_TO_FRONT_COMMAND_NAME		= "bringToFront";
	String		Mi_SEND_TO_BACK_COMMAND_NAME		= "sendToBack";
	String		Mi_BRING_FORWARD_COMMAND_NAME		= "bringForward";
	String		Mi_SEND_BACKWARD_COMMAND_NAME		= "sendBackward";
	String		Mi_DUPLICATE_COMMAND_NAME		= "Duplicate";
	String		Mi_CONNECT_COMMAND_NAME			= "Connect";
	String		Mi_DISCONNECT_COMMAND_NAME		= "Disconnect";
	String		Mi_GROUP_COMMAND_NAME			= "Group";
	String		Mi_UNGROUP_COMMAND_NAME			= "Ungroup";
	String		Mi_COLLAPSE_COMMAND_NAME		= "Collapse";
	String		Mi_EXPAND_COMMAND_NAME			= "Expand";
	String		Mi_ICONIFY_COMMAND_NAME			= "Iconify";
	String		Mi_GROUP_AND_ICONIFY_COMMAND_NAME	= "Group+Iconify";
	String		Mi_DEICONIFY_COMMAND_NAME		= "DeIconify";
	String		Mi_REDRAW_COMMAND_NAME			= "Redraw";
	String		Mi_FORMAT_COMMAND_NAME			= "Format";
	String		Mi_RUBBER_STAMP_COMMAND_NAME		= "RubberStamp";
	String		Mi_EDIT_COMMAND_NAME			= "Edit";

	String		Mi_ZOOM_COMMAND_NAME			= "Zoom";
	String		Mi_PAN_COMMAND_NAME			= "Pan";
	String		Mi_TRANSLATE_COMMAND_NAME		= "Translate";
	String		Mi_ROTATE_COMMAND_NAME			= "Rotate";
	String		Mi_FLIP_COMMAND_NAME			= "Flip";
	String		Mi_SCALE_COMMAND_NAME			= "Scale";
	String		Mi_REPLACE_COMMAND_NAME			= "Replace";

	String		Mi_DESELECT_AND_PICKUP_COMMAND_NAME	= "DeSelectAndPickup";
	String		Mi_PICK_UP_COMMAND_NAME			= "Pickup";
	String		Mi_DRAG_COMMAND_NAME			= "Drag";
	String		Mi_DROP_COMMAND_NAME			= "Drop";
	String		Mi_EXECUTE_COMMAND_NAME			= "Execute";
	String		Mi_START_COMMAND_NAME			= "Start";
	String		Mi_STOP_COMMAND_NAME			= "Stop";
	String		Mi_TOGGLE_COMMAND_NAME			= "Toggle";
	String		Mi_NOT_OK_COMMAND_NAME			= "NotOK";

	String		Mi_HIDE_COMMAND_NAME			= "Hide";
	String		Mi_SHOW_COMMAND_NAME			= "Show";
	String		Mi_SHOW_LABELS_COMMAND_NAME		= "showLabels";
	String		Mi_HIDE_LABELS_COMMAND_NAME		= "hideLabels";

	String		Mi_MODIFY_ATTRIBUTES_COMMAND_NAME	= "ModifyAttributes";
	String		Mi_MODIFY_TEXT_COMMAND_NAME		= "ModifyText";
	String		Mi_MODIFY_CONNECTION_COMMAND_NAME	= "ModifyConnection";

	String		Mi_MOUSE_MOVED_COMMAND_NAME		= "MouseMotion";
	String		Mi_MOUSE_EXITED_WINDOW_COMMAND_NAME	= "MouseWindowExit";

	String		Mi_POPDOWN_COMMAND_NAME			= "Popdown";
	String		Mi_POPUP_COMMAND_NAME			= "Popup";

	String		Mi_DESTROY_WINDOW_COMMAND_NAME		= "DestroyWindow";
	String		Mi_CLOSE_WINDOW_COMMAND_NAME		= "CloseWindow";
	String		Mi_OPEN_WINDOW_COMMAND_NAME		= "OpenWindow";
	String		Mi_ICONIFY_WINDOW_COMMAND_NAME		= "IconifyWindow";
	String		Mi_DEICONIFY_WINDOW_COMMAND_NAME	= "DeIconifyWindow";

	String		Mi_CLOSE_DOCUMENT_COMMAND_NAME		= "CloseDocument";

	String		Mi_EXECUTE_SHORT_CUT_COMMAND_NAME 	= "executeShortCut";

	String		Mi_NOOP_COMMAND_NAME			= "Noop";

	String		Mi_SET_COLOR_COMMAND_NAME		= "SetColor";
	String		Mi_SET_BACKGROUND_COLOR_COMMAND_NAME	= "SetBackgroundColor";

	String		Mi_EXPAND_EDITING_AREA_COMMAND_NAME	= "ExpandEditingArea";
	String		Mi_SHRINK_EDITING_AREA_COMMAND_NAME	= "ShrinkEditingArea";

	String		Mi_DISPLAY_PROPERTIES_COMMAND_NAME	= "DisplayProperties";
	String		Mi_DISPLAY_OPTIONS_COMMAND_NAME		= "DisplayOptions";
	String		Mi_DISPLAY_PREFERENCES_COMMAND_NAME	= "DisplayPreferences";
	String		Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME	= "About";
	String		Mi_DISPLAY_TOOL_BAR_COMMAND_NAME	= "DisplayToolBar";
	String		Mi_HIDE_TOOL_BAR_COMMAND_NAME		= "HideToolBar";
	String		Mi_DISPLAY_STATUS_BAR_COMMAND_NAME	= "DisplayStatusBar";
	String		Mi_HIDE_STATUS_BAR_COMMAND_NAME		= "HideStatusBar";
	String		Mi_DISPLAY_BIRDS_EYE_VIEW_COMMAND_NAME	= "DisplayBirdsEyeView";
	String		Mi_HIDE_BIRDS_EYE_VIEW_COMMAND_NAME	= "HideBirdsEyeView";

	String		Mi_ENABLE_BALLOON_HELP_COMMAND_NAME	= "EnableBalloonHelp";
	String		Mi_DISABLE_BALLOON_HELP_COMMAND_NAME	= "DisableBalloonHelp";
	String		Mi_ENABLE_TOOLHINT_HELP_COMMAND_NAME	= "EnableToolhintHelp";
	String		Mi_DISABLE_TOOLHINT_HELP_COMMAND_NAME	= "DisableToolhintHelp";
	String		Mi_ENABLE_STATUS_BAR_COMMAND_NAME	= "EnableStatusBar";
	String		Mi_DISABLE_STATUS_BAR_COMMAND_NAME	= "DisableStatusBar";

	String		Mi_HELP_ON_APPLICATION_COMMAND_NAME	= "HelpOnApplication";
	String		Mi_HELP_ON_WINDOW_COMMAND_NAME		= "HelpOnWindow";

	String		Mi_TOGGLE_FULLSCREEN_COMMAND_NAME	= "ToggleFullScreen";

	String		Mi_UP_COMMAND_NAME			= "up";
	String		Mi_DOWN_COMMAND_NAME			= "down";
	String		Mi_LEFT_COMMAND_NAME			= "left";
	String		Mi_RIGHT_COMMAND_NAME			= "right";

	String		Mi_OBSERVED_HAS_CHANGED_COMMAND_NAME	= "observerChanged";
	}

