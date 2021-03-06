
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
import com.swfm.mica.util.Pair; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public interface MiiDisplayNames extends MiiMessages
	{
	// These names are displayed to the user in, for example, the 'edit.undo' menu.

	String		Mi_BRING_TO_FRONT_DISPLAY_NAME		= "Mi_BRING_TO_FRONT_DISPLAY_NAME";
	String		Mi_SEND_TO_BACK_DISPLAY_NAME		= "Mi_SEND_TO_BACK_DISPLAY_NAME";
	String		Mi_BRING_FORWARD_DISPLAY_NAME		= "Mi_BRING_FORWARD_DISPLAY_NAME";
	String		Mi_SEND_BACKWARD_DISPLAY_NAME		= "Mi_SEND_BACKWARD_DISPLAY_NAME";

	String		Mi_DELETE_DISPLAY_NAME			= "Mi_DELETE_DISPLAY_NAME";
	String		Mi_CREATE_DISPLAY_NAME			= "Mi_CREATE_DISPLAY_NAME";
	String		Mi_DUPLICATE_DISPLAY_NAME		= "Mi_DUPLICATE_DISPLAY_NAME";
	String		Mi_GROUP_DISPLAY_NAME			= "Mi_GROUP_DISPLAY_NAME";
	String		Mi_UNGROUP_DISPLAY_NAME			= "Mi_UNGROUP_DISPLAY_NAME";

	String		Mi_ICONIFY_DISPLAY_NAME			= "Mi_ICONIFY_DISPLAY_NAME";
	String		Mi_DEICONIFY_DISPLAY_NAME		= "Mi_DEICONIFY_DISPLAY_NAME";
	String		Mi_ZOOM_DISPLAY_NAME			= "Mi_ZOOM_DISPLAY_NAME";
	String		Mi_PAN_DISPLAY_NAME			= "Mi_PAN_DISPLAY_NAME";
	String		Mi_SELECT_DISPLAY_NAME			= "Mi_SELECT_DISPLAY_NAME";
	String		Mi_DESELECT_DISPLAY_NAME		= "Mi_DESELECT_DISPLAY_NAME";
	String		Mi_TRANSLATE_DISPLAY_NAME		= "Mi_TRANSLATE_DISPLAY_NAME";
	String		Mi_ROTATE_DISPLAY_NAME			= "Mi_ROTATE_DISPLAY_NAME";
	String		Mi_FLIP_DISPLAY_NAME			= "Mi_FLIP_DISPLAY_NAME";
	String		Mi_SCALE_DISPLAY_NAME			= "Mi_SCALE_DISPLAY_NAME";

	String		Mi_SAVE_DISPLAY_NAME			= "Mi_SAVE_DISPLAY_NAME";
	String		Mi_PRINT_DISPLAY_NAME			= "Mi_PRINT_DISPLAY_NAME";

	String		Mi_UNDO_DISPLAY_NAME			= "Mi_UNDO_DISPLAY_NAME";
	String		Mi_REDO_DISPLAY_NAME			= "Mi_REDO_DISPLAY_NAME";
	String		Mi_CUT_DISPLAY_NAME			= "Mi_CUT_DISPLAY_NAME";
	String		Mi_COPY_DISPLAY_NAME			= "Mi_COPY_DISPLAY_NAME";
	String		Mi_PASTE_DISPLAY_NAME			= "Mi_PASTE_DISPLAY_NAME";
	String		Mi_DEFAULTS_DISPLAY_NAME		= "Mi_DEFAULTS_DISPLAY_NAME";

	String		Mi_CONNECT_DISPLAY_NAME			= "Mi_CONNECT_DISPLAY_NAME";
	String		Mi_DISCONNECT_DISPLAY_NAME		= "Mi_DISCONNECT_DISPLAY_NAME";
	String		Mi_COLLAPSE_DISPLAY_NAME		= "Mi_COLLAPSE_DISPLAY_NAME";
	String		Mi_EXPAND_DISPLAY_NAME			= "Mi_EXPAND_DISPLAY_NAME";

	String		Mi_OK_DISPLAY_NAME			= "Mi_OK_DISPLAY_NAME";
	String		Mi_APPLY_DISPLAY_NAME			= "Mi_APPLY_DISPLAY_NAME";
	String		Mi_CANCEL_DISPLAY_NAME			= "Mi_CANCEL_DISPLAY_NAME";
	String		Mi_HELP_DISPLAY_NAME			= "Mi_HELP_DISPLAY_NAME";

	String		Mi_STOP_DISPLAY_NAME			= "Mi_STOP_DISPLAY_NAME";

	String		Mi_DRAG_AND_DROP_DISPLAY_NAME		= "Mi_DRAG_AND_DROP_DISPLAY_NAME";

	String		Mi_MODIFY_ATTRIBUTES_DISPLAY_NAME	= "Mi_MODIFY_ATTRIBUTES_DISPLAY_NAME";
	String		Mi_MODIFY_TEXT_DISPLAY_NAME		= "Mi_MODIFY_TEXT_DISPLAY_NAME";
	String		Mi_MODIFY_CONNECTION_DISPLAY_NAME	= "Mi_MODIFY_CONNECTION_DISPLAY_NAME";

	String		Mi_PREVIOUS_DISPLAY_NAME		= "Mi_PREVIOUS_DISPLAY_NAME";
	String		Mi_NEXT_DISPLAY_NAME			= "Mi_NEXT_DISPLAY_NAME";

	String		Mi_FILE_MENU_DISPLAY_NAME		= "Mi_FILE_MENU_DISPLAY_NAME";
	String		Mi_EDIT_MENU_DISPLAY_NAME		= "Mi_EDIT_MENU_DISPLAY_NAME";
	String		Mi_VIEW_MENU_DISPLAY_NAME		= "Mi_VIEW_MENU_DISPLAY_NAME";
	String		Mi_SHAPE_MENU_DISPLAY_NAME		= "Mi_SHAPE_MENU_DISPLAY_NAME";
	String		Mi_CONNECT_MENU_DISPLAY_NAME		= "Mi_CONNECT_MENU_DISPLAY_NAME";
	String		Mi_FORMAT_MENU_DISPLAY_NAME		= "Mi_FORMAT_MENU_DISPLAY_NAME";
	String		Mi_TOOLS_MENU_DISPLAY_NAME		= "Mi_TOOLS_MENU_DISPLAY_NAME";
	String		Mi_HELP_MENU_DISPLAY_NAME		= "Mi_HELP_MENU_DISPLAY_NAME";
	String		Mi_GRAPH_MENU_DISPLAY_NAME		= "Mi_GRAPH_MENU_DISPLAY_NAME";
	String		Mi_LAYOUT_MENU_DISPLAY_NAME		= "Mi_LAYOUT_MENU_DISPLAY_NAME";

	String		Mi_ZOOM_IN_DISPLAY_NAME			= "Mi_ZOOM_IN_DISPLAY_NAME";
	String		Mi_ZOOM_OUT_DISPLAY_NAME		= "Mi_ZOOM_OUT_DISPLAY_NAME";
	String		Mi_VIEW_ALL_DISPLAY_NAME		= "Mi_VIEW_ALL_DISPLAY_NAME";
	String		Mi_VIEW_PREVIOUS_DISPLAY_NAME		= "Mi_VIEW_PREVIOUS_DISPLAY_NAME";
	String		Mi_VIEW_NEXT_DISPLAY_NAME		= "Mi_VIEW_NEXT_DISPLAY_NAME";
	String		Mi_VIEW_REPEAT_DISPLAY_NAME		= "Mi_VIEW_REPEAT_DISPLAY_NAME";
	String		Mi_ZOOM_HOME_DISPLAY_NAME		= "Mi_ZOOM_HOME_DISPLAY_NAME";

	String		Mi_ZOOM_TO_FIT_CONTENT_DISPLAY_NAME		= "Mi_ZOOM_TO_FIT_CONTENT_DISPLAY_NAME";
	String		Mi_ZOOM_TO_FIT_CONTENT_WIDTH_DISPLAY_NAME	= "Mi_ZOOM_TO_FIT_CONTENT_WIDTH_DISPLAY_NAME";
	String		Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_DISPLAY_NAME	= "Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_DISPLAY_NAME";

	String		Mi_SCALE_TO_FIT_PAGE_DISPLAY_NAME	= "Mi_SCALE_TO_FIT_PAGE_DISPLAY_NAME";
	String		Mi_MOVE_TO_FIT_PAGE_DISPLAY_NAME	= "Mi_MOVE_TO_FIT_PAGE_DISPLAY_NAME";
	String		Mi_ADJUST_PAGES_TO_FIT_DISPLAY_NAME	= "Mi_ADJUST_PAGES_TO_FIT_DISPLAY_NAME";

	String		Mi_NEW_MENUITEM_DISPLAY_NAME		= "Mi_NEW_MENUITEM_DISPLAY_NAME";
	String		Mi_OPEN_MENUITEM_DISPLAY_NAME		= "Mi_OPEN_MENUITEM_DISPLAY_NAME";
	String		Mi_SAVE_MENUITEM_DISPLAY_NAME		= "Mi_SAVE_MENUITEM_DISPLAY_NAME";
	String		Mi_SAVE_AS_MENUITEM_DISPLAY_NAME	= "Mi_SAVE_AS_MENUITEM_DISPLAY_NAME";
	String		Mi_CLOSE_MENUITEM_DISPLAY_NAME		= "Mi_CLOSE_MENUITEM_DISPLAY_NAME";
	String		Mi_PREFERENCES_MENUITEM_DISPLAY_NAME	= "Mi_PREFERENCES_MENUITEM_DISPLAY_NAME";
	String		Mi_PRINT_SETUP_MENUITEM_DISPLAY_NAME	= "Mi_PRINT_SETUP_MENUITEM_DISPLAY_NAME";
	String		Mi_PRINT_MENUITEM_DISPLAY_NAME		= "Mi_PRINT_MENUITEM_DISPLAY_NAME";
	String		Mi_QUIT_MENUITEM_DISPLAY_NAME		= "Mi_QUIT_MENUITEM_DISPLAY_NAME";

	String		Mi_UNDO_MENUITEM_DISPLAY_NAME		= "Mi_UNDO_MENUITEM_DISPLAY_NAME";
	String		Mi_REDO_MENUITEM_DISPLAY_NAME		= "Mi_REDO_MENUITEM_DISPLAY_NAME";
	String		Mi_CUT_MENUITEM_DISPLAY_NAME		= "Mi_CUT_MENUITEM_DISPLAY_NAME";
	String		Mi_COPY_MENUITEM_DISPLAY_NAME		= "Mi_COPY_MENUITEM_DISPLAY_NAME";
	String		Mi_PASTE_MENUITEM_DISPLAY_NAME		= "Mi_PASTE_MENUITEM_DISPLAY_NAME";
	String		Mi_DELETE_MENUITEM_DISPLAY_NAME		= "Mi_DELETE_MENUITEM_DISPLAY_NAME";
	String		Mi_SELECT_ALL_MENUITEM_DISPLAY_NAME	= "Mi_SELECT_ALL_MENUITEM_DISPLAY_NAME";
	String		Mi_DESELECT_ALL_MENUITEM_DISPLAY_NAME	= "Mi_DESELECT_ALL_MENUITEM_DISPLAY_NAME";
	String		Mi_DUPLICATE_MENUITEM_DISPLAY_NAME	= "Mi_DUPLICATE_MENUITEM_DISPLAY_NAME";

String	Mi_ZOOM_IN_MENUITEM_DISPLAY_NAME		= "Mi_ZOOM_IN_MENUITEM_DISPLAY_NAME";
String	Mi_ZOOM_OUT_MENUITEM_DISPLAY_NAME		= "Mi_ZOOM_OUT_MENUITEM_DISPLAY_NAME";
String	Mi_VIEW_ALL_MENUITEM_DISPLAY_NAME		= "Mi_VIEW_ALL_MENUITEM_DISPLAY_NAME";
String	Mi_VIEW_PREVIOUS_MENUITEM_DISPLAY_NAME		= "Mi_VIEW_PREVIOUS_MENUITEM_DISPLAY_NAME";
String	Mi_VIEW_NEXT_MENUITEM_DISPLAY_NAME		= "Mi_VIEW_NEXT_MENUITEM_DISPLAY_NAME";

String	Mi_ZOOM_HOME_MENUITEM_DISPLAY_NAME		= "Mi_ZOOM_HOME_MENUITEM_DISPLAY_NAME";
String	Mi_ZOOM_TO_FIT_MENUITEM_DISPLAY_NAME		= "Mi_ZOOM_TO_FIT_MENUITEM_DISPLAY_NAME";
String	Mi_ZOOM_TO_FIT_WIDTH_MENUITEM_DISPLAY_NAME	= "Mi_ZOOM_TO_FIT_WIDTH_MENUITEM_DISPLAY_NAME";
String	Mi_ZOOM_TO_FIT_HEIGHT_MENUITEM_DISPLAY_NAME	= "Mi_ZOOM_TO_FIT_HEIGHT_MENUITEM_DISPLAY_NAME";

String	Mi_ZOOM_TO_FIT_CONTENT_MENUITEM_DISPLAY_NAME		= "Mi_ZOOM_TO_FIT_CONTENT_MENUITEM_DISPLAY_NAME";
String	Mi_ZOOM_TO_FIT_CONTENT_WIDTH_MENUITEM_DISPLAY_NAME	= "Mi_ZOOM_TO_FIT_CONTENT_WIDTH_MENUITEM_DISPLAY_NAME";
String	Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_MENUITEM_DISPLAY_NAME	= "Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_MENUITEM_DISPLAY_NAME";


String	Mi_REDRAW_MENUITEM_DISPLAY_NAME			= "Mi_REDRAW_MENUITEM_DISPLAY_NAME";
String	Mi_DISPLAY_TOOL_BAR_MENUITEM_DISPLAY_NAME	= "Mi_DISPLAY_TOOL_BAR_MENUITEM_DISPLAY_NAME";
String	Mi_DISPLAY_STATUS_BAR_MENUITEM_DISPLAY_NAME	= "Mi_DISPLAY_STATUS_BAR_MENUITEM_DISPLAY_NAME";
String	Mi_DISPLAY_BIRDS_EYE_VIEW_MENUITEM_DISPLAY_NAME	= "Mi_DISPLAY_BIRDS_EYE_VIEW_MENUITEM_DISPLAY_NAME";

String	Mi_GROUP_MENUITEM_DISPLAY_NAME			= "Mi_GROUP_MENUITEM_DISPLAY_NAME";
String	Mi_UNGROUP_MENUITEM_DISPLAY_NAME		= "Mi_UNGROUP_MENUITEM_DISPLAY_NAME";
String	Mi_ICONIFY_MENUITEM_DISPLAY_NAME		= "Mi_ICONIFY_MENUITEM_DISPLAY_NAME";
String	Mi_DEICONIFY_MENUITEM_DISPLAY_NAME		= "Mi_DEICONIFY_MENUITEM_DISPLAY_NAME";
String	Mi_BRING_TO_FRONT_MENUITEM_DISPLAY_NAME		= "Mi_BRING_TO_FRONT_MENUITEM_DISPLAY_NAME";
String	Mi_SEND_TO_BACK_MENUITEM_DISPLAY_NAME		= "Mi_SEND_TO_BACK_MENUITEM_DISPLAY_NAME";
String	Mi_BRING_FOWARD_MENUITEM_DISPLAY_NAME		= "Mi_BRING_FOWARD_MENUITEM_DISPLAY_NAME";
String	Mi_SEND_BACKWARD_MENUITEM_DISPLAY_NAME		= "Mi_SEND_BACKWARD_MENUITEM_DISPLAY_NAME";
String	Mi_DISPLAY_OPTIONS_MENUITEM_DISPLAY_NAME	= "Mi_DISPLAY_OPTIONS_MENUITEM_DISPLAY_NAME";

String	Mi_CONNECT_MENUITEM_DISPLAY_NAME		= "Mi_CONNECT_MENUITEM_DISPLAY_NAME";
String	Mi_DISCONNECT_MENUITEM_DISPLAY_NAME		= "Mi_DISCONNECT_MENUITEM_DISPLAY_NAME";
String	Mi_COLLAPSE_MENUITEM_DISPLAY_NAME		= "Mi_COLLAPSE_MENUITEM_DISPLAY_NAME";
String	Mi_EXPAND_MENUITEM_DISPLAY_NAME			= "Mi_EXPAND_MENUITEM_DISPLAY_NAME";

String	Mi_FORMAT_MENUITEM_DISPLAY_NAME			= "Mi_FORMAT_MENUITEM_DISPLAY_NAME";
String	Mi_EXPAND_EDITING_AREA_MENUITEM_DISPLAY_NAME	= "Mi_EXPAND_EDITING_AREA_MENUITEM_DISPLAY_NAME";
String	Mi_SHRINK_EDITING_AREA_MENUITEM_DISPLAY_NAME	= "Mi_SHRINK_EDITING_AREA_MENUITEM_DISPLAY_NAME";
String	Mi_DISPLAY_ABOUT_DIALOG_MENUITEM_DISPLAY_NAME	= "Mi_DISPLAY_ABOUT_DIALOG_MENUITEM_DISPLAY_NAME";
String	Mi_HELP_ON_APPLICATION_MENUITEM_DISPLAY_NAME	= "Mi_HELP_ON_APPLICATION_MENUITEM_DISPLAY_NAME";
String	Mi_ENABLE_TOOLHINT_HELP_MENUITEM_DISPLAY_NAME	= "Mi_ENABLE_TOOLHINT_HELP_MENUITEM_DISPLAY_NAME";
								

String	Mi_ARRANGE_GRID_DISPLAY_NAME		= "Mi_ARRANGE_GRID_DISPLAY_NAME";
String	Mi_ARRANGE_ROW_DISPLAY_NAME 		= "Mi_ARRANGE_ROW_DISPLAY_NAME";
String	Mi_ARRANGE_COLUMN_DISPLAY_NAME 		= "Mi_ARRANGE_COLUMN_DISPLAY_NAME";
String	Mi_SAME_WIDTH_DISPLAY_NAME 		= "Mi_SAME_WIDTH_DISPLAY_NAME";
String	Mi_SAME_HEIGHT_DISPLAY_NAME 		= "Mi_SAME_HEIGHT_DISPLAY_NAME";
String	Mi_ALIGN_BOTTOM_DISPLAY_NAME 		= "Mi_ALIGN_BOTTOM_DISPLAY_NAME";
String	Mi_ALIGN_TOP_DISPLAY_NAME 		= "Mi_ALIGN_TOP_DISPLAY_NAME";
String	Mi_ALIGN_LEFT_DISPLAY_NAME 		= "Mi_ALIGN_LEFT_DISPLAY_NAME";
String	Mi_ALIGN_RIGHT_DISPLAY_NAME 		= "Mi_ALIGN_RIGHT_DISPLAY_NAME";
String	Mi_FORMAT_GRID_DISPLAY_NAME 		= "Mi_FORMAT_GRID_DISPLAY_NAME";
String	Mi_FORMAT_ROW_DISPLAY_NAME 		= "Mi_FORMAT_ROW_DISPLAY_NAME";
String	Mi_FORMAT_BJ_ROW_DISPLAY_NAME 		= "Mi_FORMAT_BJ_ROW_DISPLAY_NAME";
String	Mi_FORMAT_TJ_ROW_DISPLAY_NAME 		= "Mi_FORMAT_TJ_ROW_DISPLAY_NAME";
String	Mi_FORMAT_COLUMN_DISPLAY_NAME 		= "Mi_FORMAT_COLUMN_DISPLAY_NAME";
String	Mi_FORMAT_LJ_COLUMN_DISPLAY_NAME	= "Mi_FORMAT_LJ_COLUMN_DISPLAY_NAME";
String	Mi_FORMAT_RJ_COLUMN_DISPLAY_NAME	= "Mi_FORMAT_RJ_COLUMN_DISPLAY_NAME";
String	Mi_REMOVE_FORMAT_DISPLAY_NAME 		= "Mi_REMOVE_FORMAT_DISPLAY_NAME";


String	Mi_ARRANGE_GRID_STATUS_HELP_MSG		= "Mi_ARRANGE_GRID_STATUS_HELP_MSG";
String	Mi_ARRANGE_ROW_STATUS_HELP_MSG 		= "Mi_ARRANGE_ROW_STATUS_HELP_MSG";
String	Mi_ARRANGE_COLUMN_STATUS_HELP_MSG 	= "Mi_ARRANGE_COLUMN_STATUS_HELP_MSG";
String	Mi_SAME_WIDTH_STATUS_HELP_MSG 		= "Mi_SAME_WIDTH_STATUS_HELP_MSG";
String	Mi_SAME_HEIGHT_STATUS_HELP_MSG 		= "Mi_SAME_HEIGHT_STATUS_HELP_MSG";
String	Mi_ALIGN_BOTTOM_STATUS_HELP_MSG 	= "Mi_ALIGN_BOTTOM_STATUS_HELP_MSG";
String	Mi_ALIGN_TOP_STATUS_HELP_MSG 		= "Mi_ALIGN_TOP_STATUS_HELP_MSG";
String	Mi_ALIGN_LEFT_STATUS_HELP_MSG 		= "Mi_ALIGN_LEFT_STATUS_HELP_MSG";
String	Mi_ALIGN_RIGHT_STATUS_HELP_MSG 		= "Mi_ALIGN_RIGHT_STATUS_HELP_MSG";
String	Mi_FORMAT_GRID_STATUS_HELP_MSG 		= "Mi_FORMAT_GRID_STATUS_HELP_MSG";
String	Mi_FORMAT_ROW_STATUS_HELP_MSG 		= "Mi_FORMAT_ROW_STATUS_HELP_MSG";
String	Mi_FORMAT_BJ_ROW_STATUS_HELP_MSG 	= "Mi_FORMAT_BJ_ROW_STATUS_HELP_MSG";
String	Mi_FORMAT_TJ_ROW_STATUS_HELP_MSG 	= "Mi_FORMAT_TJ_ROW_STATUS_HELP_MSG";
String	Mi_FORMAT_COLUMN_STATUS_HELP_MSG 	= "Mi_FORMAT_COLUMN_STATUS_HELP_MSG";
String	Mi_FORMAT_LJ_COLUMN_STATUS_HELP_MSG	= "Mi_FORMAT_LJ_COLUMN_STATUS_HELP_MSG";
String	Mi_FORMAT_RJ_COLUMN_STATUS_HELP_MSG 	="Mi_FORMAT_RJ_COLUMN_STATUS_HELP_MSG";
String	Mi_REMOVE_FORMAT_STATUS_HELP_MSG 	= "Mi_REMOVE_FORMAT_STATUS_HELP_MSG";


String	Mi_NO_ARRANGE_GRID_STATUS_HELP_MSG	= "Mi_NO_ARRANGE_GRID_STATUS_HELP_MSG";
String	Mi_NO_ARRANGE_ROW_STATUS_HELP_MSG 	= "Mi_NO_ARRANGE_ROW_STATUS_HELP_MSG";
String	Mi_NO_ARRANGE_COLUMN_STATUS_HELP_MSG 	= "Mi_NO_ARRANGE_COLUMN_STATUS_HELP_MSG";
String	Mi_NO_SAME_WIDTH_STATUS_HELP_MSG 	= "Mi_NO_SAME_WIDTH_STATUS_HELP_MSG";
String	Mi_NO_SAME_HEIGHT_STATUS_HELP_MSG 	= "Mi_NO_SAME_HEIGHT_STATUS_HELP_MSG";
String	Mi_NO_ALIGN_BOTTOM_STATUS_HELP_MSG 	= "Mi_NO_ALIGN_BOTTOM_STATUS_HELP_MSG";
String	Mi_NO_ALIGN_TOP_STATUS_HELP_MSG 	= "Mi_NO_ALIGN_TOP_STATUS_HELP_MSG";
String	Mi_NO_ALIGN_LEFT_STATUS_HELP_MSG 	= "Mi_NO_ALIGN_LEFT_STATUS_HELP_MSG";
String	Mi_NO_ALIGN_RIGHT_STATUS_HELP_MSG 	= "Mi_NO_ALIGN_RIGHT_STATUS_HELP_MSG";
String	Mi_NO_FORMAT_GRID_STATUS_HELP_MSG 	= "Mi_NO_FORMAT_GRID_STATUS_HELP_MSG";
String	Mi_NO_FORMAT_ROW_STATUS_HELP_MSG 	= "Mi_NO_FORMAT_ROW_STATUS_HELP_MSG";
String	Mi_NO_FORMAT_BJ_ROW_STATUS_HELP_MSG 	= "Mi_NO_FORMAT_BJ_ROW_STATUS_HELP_MSG";
String	Mi_NO_FORMAT_TJ_ROW_STATUS_HELP_MSG 	= "Mi_NO_FORMAT_TJ_ROW_STATUS_HELP_MSG";
String	Mi_NO_FORMAT_COLUMN_STATUS_HELP_MSG 	= "Mi_NO_FORMAT_COLUMN_STATUS_HELP_MSG";
String	Mi_NO_FORMAT_LJ_COLUMN_STATUS_HELP_MSG 	= "Mi_NO_FORMAT_LJ_COLUMN_STATUS_HELP_MSG";
String	Mi_NO_FORMAT_RJ_COLUMN_STATUS_HELP_MSG 	="Mi_NO_FORMAT_RJ_COLUMN_STATUS_HELP_MSG";
String	Mi_NO_REMOVE_FORMAT_STATUS_HELP_MSG	= "Mi_NO_REMOVE_FORMAT_STATUS_HELP_MSG";

String	Mi_SCALE_TO_FIT_PAGE_STATUS_HELP_NAME	= "Mi_SCALE_TO_FIT_PAGE_STATUS_HELP_NAME";
String	Mi_MOVE_TO_FIT_PAGE_STATUS_HELP_NAME	= "Mi_MOVE_TO_FIT_PAGE_STATUS_HELP_NAME";
String	Mi_ADJUST_PAGES_TO_FIT_STATUS_HELP_NAME	= "Mi_ADJUST_PAGES_TO_FIT_STATUS_HELP_NAME";



String	Mi_ARRANGE_GRID_BALLOON_HELP_MSG	= "Mi_ARRANGE_GRID_BALLOON_HELP_MSG";
String	Mi_ARRANGE_ROW_BALLOON_HELP_MSG 	= "Mi_ARRANGE_ROW_BALLOON_HELP_MSG";
String	Mi_ARRANGE_COLUMN_BALLOON_HELP_MSG 	= "Mi_ARRANGE_COLUMN_BALLOON_HELP_MSG";
String	Mi_SAME_WIDTH_BALLOON_HELP_MSG 		= "Mi_SAME_WIDTH_BALLOON_HELP_MSG";
String	Mi_SAME_HEIGHT_BALLOON_HELP_MSG 	= "Mi_SAME_HEIGHT_BALLOON_HELP_MSG";
String	Mi_ALIGN_BOTTOM_BALLOON_HELP_MSG 	= "Mi_ALIGN_BOTTOM_BALLOON_HELP_MSG";
String	Mi_ALIGN_TOP_BALLOON_HELP_MSG 		= "Mi_ALIGN_TOP_BALLOON_HELP_MSG";
String	Mi_ALIGN_LEFT_BALLOON_HELP_MSG 		= "Mi_ALIGN_LEFT_BALLOON_HELP_MSG";
String	Mi_ALIGN_RIGHT_BALLOON_HELP_MSG 	= "Mi_ALIGN_RIGHT_BALLOON_HELP_MSG";
String	Mi_FORMAT_GRID_BALLOON_HELP_MSG 	= "Mi_FORMAT_GRID_BALLOON_HELP_MSG";
String	Mi_FORMAT_ROW_BALLOON_HELP_MSG 		= "Mi_FORMAT_ROW_BALLOON_HELP_MSG";
String	Mi_FORMAT_BJ_ROW_BALLOON_HELP_MSG 	= "Mi_FORMAT_BJ_ROW_BALLOON_HELP_MSG";
String	Mi_FORMAT_TJ_ROW_BALLOON_HELP_MSG 	= "Mi_FORMAT_TJ_ROW_BALLOON_HELP_MSG";
String	Mi_FORMAT_COLUMN_BALLOON_HELP_MSG 	= "Mi_FORMAT_COLUMN_BALLOON_HELP_MSG";
String	Mi_FORMAT_LJ_COLUMN_BALLOON_HELP_MSG 	= "Mi_FORMAT_LJ_COLUMN_BALLOON_HELP_MSG";
String	Mi_FORMAT_RJ_COLUMN_BALLOON_HELP_MSG 	="Mi_FORMAT_RJ_COLUMN_BALLOON_HELP_MSG";
String	Mi_REMOVE_FORMAT_BALLOON_HELP_MSG 	= "Mi_REMOVE_FORMAT_BALLOON_HELP_MSG";



Pair[]	properties =
{
new Pair(Mi_BRING_TO_FRONT_DISPLAY_NAME,	"Bring to Front"),
new Pair(Mi_SEND_TO_BACK_DISPLAY_NAME,		"Send to Back"),
new Pair(Mi_BRING_FORWARD_DISPLAY_NAME,		"Bring Forward"),
new Pair(Mi_SEND_BACKWARD_DISPLAY_NAME,		"Send Backward"),

new Pair(Mi_DELETE_DISPLAY_NAME,		"Delete"),
new Pair(Mi_CREATE_DISPLAY_NAME,		"Create"),
new Pair(Mi_DUPLICATE_DISPLAY_NAME,		"Duplicate"),
new Pair(Mi_GROUP_DISPLAY_NAME,			"Group"),
new Pair(Mi_UNGROUP_DISPLAY_NAME,		"Ungroup"),

new Pair(Mi_ICONIFY_DISPLAY_NAME,		"Iconify"),
new Pair(Mi_DEICONIFY_DISPLAY_NAME,		"De-iconify"),
new Pair(Mi_ZOOM_DISPLAY_NAME,			"Zoom"),
new Pair(Mi_PAN_DISPLAY_NAME,			"Pan"),
new Pair(Mi_SELECT_DISPLAY_NAME,		"Select"),
new Pair(Mi_DESELECT_DISPLAY_NAME,		"Deselect"),
new Pair(Mi_TRANSLATE_DISPLAY_NAME,		"Move"),
new Pair(Mi_ROTATE_DISPLAY_NAME,		"Rotate"),
new Pair(Mi_FLIP_DISPLAY_NAME,			"Flip"),
new Pair(Mi_SCALE_DISPLAY_NAME,			"Resize"),

new Pair(Mi_SAVE_DISPLAY_NAME,			"Save"),
new Pair(Mi_PRINT_DISPLAY_NAME,			"Print"),

new Pair(Mi_UNDO_DISPLAY_NAME,			"Undo"),
new Pair(Mi_REDO_DISPLAY_NAME,			"Redo"),
new Pair(Mi_CUT_DISPLAY_NAME,			"Cut"),
new Pair(Mi_COPY_DISPLAY_NAME,			"Copy"),
new Pair(Mi_PASTE_DISPLAY_NAME,			"Paste"),
new Pair(Mi_DEFAULTS_DISPLAY_NAME,		"Defaults"),

new Pair(Mi_CONNECT_DISPLAY_NAME,		"Connect"),
new Pair(Mi_DISCONNECT_DISPLAY_NAME,		"Disconnect"),
new Pair(Mi_COLLAPSE_DISPLAY_NAME,		"Collapse"),
new Pair(Mi_EXPAND_DISPLAY_NAME,		"Expand"),

new Pair(Mi_OK_DISPLAY_NAME,			"Ok"),
new Pair(Mi_APPLY_DISPLAY_NAME,			"Apply"),
new Pair(Mi_CANCEL_DISPLAY_NAME,		"Cancel"),
new Pair(Mi_HELP_DISPLAY_NAME,			"Help"),

new Pair(Mi_STOP_DISPLAY_NAME,			"Stop"),
new Pair(Mi_DRAG_AND_DROP_DISPLAY_NAME,		"Drag and drop"),

new Pair(Mi_MODIFY_ATTRIBUTES_DISPLAY_NAME,	"Attribute Change"),
new Pair(Mi_MODIFY_TEXT_DISPLAY_NAME,		"Text Change"),
new Pair(Mi_MODIFY_CONNECTION_DISPLAY_NAME,	"Connection Change"),

new Pair(Mi_PREVIOUS_DISPLAY_NAME,		"Previous"),
new Pair(Mi_NEXT_DISPLAY_NAME,			"Next"),

new Pair(Mi_FILE_MENU_DISPLAY_NAME,		"&File"),
new Pair(Mi_EDIT_MENU_DISPLAY_NAME,		"&Edit"),
new Pair(Mi_VIEW_MENU_DISPLAY_NAME,		"&View"),
new Pair(Mi_SHAPE_MENU_DISPLAY_NAME,		"&Shape"),
new Pair(Mi_CONNECT_MENU_DISPLAY_NAME,		"&Connect"),
new Pair(Mi_FORMAT_MENU_DISPLAY_NAME,		"F&ormat"),
new Pair(Mi_TOOLS_MENU_DISPLAY_NAME,		"&Tools"),
new Pair(Mi_HELP_MENU_DISPLAY_NAME,		"&Help"),
new Pair(Mi_GRAPH_MENU_DISPLAY_NAME,		"&Graph"),
new Pair(Mi_LAYOUT_MENU_DISPLAY_NAME,		"&Layout"),

new Pair(Mi_ZOOM_IN_DISPLAY_NAME,		"Zoom-In"),
new Pair(Mi_ZOOM_OUT_DISPLAY_NAME,		"Zoom-Out"),
new Pair(Mi_VIEW_ALL_DISPLAY_NAME,		"View-All"),
new Pair(Mi_VIEW_PREVIOUS_DISPLAY_NAME,		"Previous-View"),
new Pair(Mi_VIEW_NEXT_DISPLAY_NAME,		"Next-View"),
new Pair(Mi_VIEW_REPEAT_DISPLAY_NAME,		"View-Repeat"),
new Pair(Mi_ZOOM_HOME_DISPLAY_NAME,		"Zoom-Home"),
new Pair(Mi_ZOOM_TO_FIT_CONTENT_DISPLAY_NAME,	"Zoom-To-Fit"),
new Pair(Mi_ZOOM_TO_FIT_CONTENT_WIDTH_DISPLAY_NAME,	"Zoom-To-Fit-Width"),
new Pair(Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_DISPLAY_NAME,	"Zoom-To-Fit-Height"),

new Pair(Mi_SCALE_TO_FIT_PAGE_DISPLAY_NAME,	"Scale to Fit Page"),
new Pair(Mi_MOVE_TO_FIT_PAGE_DISPLAY_NAME,	"Move to Page Center"),
new Pair(Mi_ADJUST_PAGES_TO_FIT_DISPLAY_NAME,	"Adjust Page(s) to Fit"),


new Pair(Mi_NEW_MENUITEM_DISPLAY_NAME,		"&New...\tCtrl+N"),
new Pair(Mi_OPEN_MENUITEM_DISPLAY_NAME,		"&Open...\tCtrl+O"),
new Pair(Mi_SAVE_MENUITEM_DISPLAY_NAME,		"&Save\tCtrl+S"),
new Pair(Mi_SAVE_AS_MENUITEM_DISPLAY_NAME,	"Save &As..."),
new Pair(Mi_CLOSE_MENUITEM_DISPLAY_NAME,	"&Close"),
new Pair(Mi_PREFERENCES_MENUITEM_DISPLAY_NAME,	"Preferences"),
new Pair(Mi_PRINT_SETUP_MENUITEM_DISPLAY_NAME,	"Print Setup..."),
new Pair(Mi_PRINT_MENUITEM_DISPLAY_NAME,	"&Print\tCtrl+P"),
new Pair(Mi_QUIT_MENUITEM_DISPLAY_NAME,		"&Quit\tCtrl+Q"),

new Pair(Mi_UNDO_MENUITEM_DISPLAY_NAME,		"&Undo\tCtrl+Z"),
new Pair(Mi_REDO_MENUITEM_DISPLAY_NAME,		"&Redo\tCtrl+Y"),
new Pair(Mi_CUT_MENUITEM_DISPLAY_NAME,		"Cu&t\tCtrl+X"),
new Pair(Mi_COPY_MENUITEM_DISPLAY_NAME,		"&Copy\tCtrl+C"),
new Pair(Mi_PASTE_MENUITEM_DISPLAY_NAME,	"&Paste\tCtrl+V"),
new Pair(Mi_DELETE_MENUITEM_DISPLAY_NAME,	"&Delete\tdelete"),
new Pair(Mi_SELECT_ALL_MENUITEM_DISPLAY_NAME,	"&Select All\tCtrl+A"),
new Pair(Mi_DESELECT_ALL_MENUITEM_DISPLAY_NAME,	"Deselect &All\tEsc"),
new Pair(Mi_DUPLICATE_MENUITEM_DISPLAY_NAME,	"Dup&licate"),

//MS Mouse middle button click uses ctrl-btn2click, so change this to shift
new Pair(Mi_ZOOM_IN_MENUITEM_DISPLAY_NAME,		"Zoom &In\tShift<Btn2Click>"),
new Pair(Mi_ZOOM_OUT_MENUITEM_DISPLAY_NAME,		"Zoom &Out\tShift+<Btn3Click>"),
new Pair(Mi_VIEW_ALL_MENUITEM_DISPLAY_NAME,		"View &All\tCtrl+W"),
new Pair(Mi_VIEW_PREVIOUS_MENUITEM_DISPLAY_NAME,	"View &Previous"),
new Pair(Mi_VIEW_NEXT_MENUITEM_DISPLAY_NAME,		"View &Next"),

new Pair(Mi_ZOOM_HOME_MENUITEM_DISPLAY_NAME,		"Zoom to be &Readable"),
new Pair(Mi_ZOOM_TO_FIT_MENUITEM_DISPLAY_NAME,		"Zoom to See Page"),
new Pair(Mi_ZOOM_TO_FIT_WIDTH_MENUITEM_DISPLAY_NAME,	"Zoom to See Page &Width"),
new Pair(Mi_ZOOM_TO_FIT_HEIGHT_MENUITEM_DISPLAY_NAME,	"Zoom to See Page &Height"),

new Pair(Mi_ZOOM_TO_FIT_CONTENT_MENUITEM_DISPLAY_NAME,		"Zoom to Fit Content\tCtrl+J"),
new Pair(Mi_ZOOM_TO_FIT_CONTENT_WIDTH_MENUITEM_DISPLAY_NAME,	"Zoom to Fit Content Width"),
new Pair(Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_MENUITEM_DISPLAY_NAME,	"Zoom to Fit Content Height"),

new Pair(Mi_REDRAW_MENUITEM_DISPLAY_NAME,		"&Redraw\tCtrl+L"),
new Pair(Mi_DISPLAY_TOOL_BAR_MENUITEM_DISPLAY_NAME,	"&Toolbar"),
new Pair(Mi_DISPLAY_STATUS_BAR_MENUITEM_DISPLAY_NAME,	"&Status Bar"),
new Pair(Mi_DISPLAY_BIRDS_EYE_VIEW_MENUITEM_DISPLAY_NAME,"&Birds Eye View"),

new Pair(Mi_GROUP_MENUITEM_DISPLAY_NAME,		"&Group\tCtrl+G"),
new Pair(Mi_UNGROUP_MENUITEM_DISPLAY_NAME,		"&Ungroup\tCtrl+U"),
new Pair(Mi_ICONIFY_MENUITEM_DISPLAY_NAME,		"&Iconify\tCtrl+I"),
new Pair(Mi_DEICONIFY_MENUITEM_DISPLAY_NAME,		"D&eIconify\tCtrl+E"),
new Pair(Mi_BRING_TO_FRONT_MENUITEM_DISPLAY_NAME,	"Bring to &Front\tCtrl+F"),
new Pair(Mi_SEND_TO_BACK_MENUITEM_DISPLAY_NAME,		"Send to &Back\tCtrl+B"),
new Pair(Mi_BRING_FOWARD_MENUITEM_DISPLAY_NAME,		"Bring Forward"),
new Pair(Mi_SEND_BACKWARD_MENUITEM_DISPLAY_NAME,	"Send Backward"),

new Pair(Mi_DISPLAY_OPTIONS_MENUITEM_DISPLAY_NAME,	"&Options..."),
new Pair(Mi_CONNECT_MENUITEM_DISPLAY_NAME,		"Connect"),
new Pair(Mi_DISCONNECT_MENUITEM_DISPLAY_NAME,		"Disconnec&t"),
new Pair(Mi_COLLAPSE_MENUITEM_DISPLAY_NAME,		"Collapse"),
new Pair(Mi_EXPAND_MENUITEM_DISPLAY_NAME,		"Expand"),

new Pair(Mi_FORMAT_MENUITEM_DISPLAY_NAME,		"Format"),
new Pair(Mi_EXPAND_EDITING_AREA_MENUITEM_DISPLAY_NAME,	"Expand Editing Area"),
new Pair(Mi_SHRINK_EDITING_AREA_MENUITEM_DISPLAY_NAME,	"Shrink Editing Area"),

new Pair(Mi_DISPLAY_ABOUT_DIALOG_MENUITEM_DISPLAY_NAME, "About..."),
new Pair(Mi_HELP_ON_APPLICATION_MENUITEM_DISPLAY_NAME, 	"Help Topics..."),
new Pair(Mi_ENABLE_TOOLHINT_HELP_MENUITEM_DISPLAY_NAME, "Tool Hints"),

new Pair(Mi_ARRANGE_GRID_DISPLAY_NAME, 			"Arrange Grid"),
new Pair(Mi_ARRANGE_ROW_DISPLAY_NAME, 			"Arrange Row"),
new Pair(Mi_ARRANGE_COLUMN_DISPLAY_NAME, 		"Arrange Column"),
new Pair(Mi_SAME_WIDTH_DISPLAY_NAME, 			"Same Width"), 
new Pair(Mi_SAME_HEIGHT_DISPLAY_NAME, 			"Same Height"),
new Pair(Mi_ALIGN_BOTTOM_DISPLAY_NAME, 			"Align Bottoms"),
new Pair(Mi_ALIGN_TOP_DISPLAY_NAME, 			"Align Tops"), 
new Pair(Mi_ALIGN_LEFT_DISPLAY_NAME, 			"Align Lefts"),
new Pair(Mi_ALIGN_RIGHT_DISPLAY_NAME, 			"Align Rights"),
new Pair(Mi_FORMAT_GRID_DISPLAY_NAME, 			"Format Grid"),
new Pair(Mi_FORMAT_ROW_DISPLAY_NAME, 			"Format Row"),
new Pair(Mi_FORMAT_BJ_ROW_DISPLAY_NAME, 		"Format Bottom-Justified Row"),
new Pair(Mi_FORMAT_TJ_ROW_DISPLAY_NAME, 		"Format Top-Justified Row"), 
new Pair(Mi_FORMAT_COLUMN_DISPLAY_NAME, 		"Format Column"), 
new Pair(Mi_FORMAT_LJ_COLUMN_DISPLAY_NAME, 		"Format Left-Justified Column"),
new Pair(Mi_FORMAT_RJ_COLUMN_DISPLAY_NAME, 		"Format Right-Justified Column"),
new Pair(Mi_REMOVE_FORMAT_DISPLAY_NAME, 		"Remove Formatting"),

new Pair(Mi_ARRANGE_GRID_STATUS_HELP_MSG,		"Move selected items into a grid"),
new Pair(Mi_ARRANGE_ROW_STATUS_HELP_MSG, 		"Move selected items into a row"),
new Pair(Mi_ARRANGE_COLUMN_STATUS_HELP_MSG, 		"Move selected items into a column"),
new Pair(Mi_SAME_WIDTH_STATUS_HELP_MSG, 		"Set all selected items to the same width"),
new Pair(Mi_SAME_HEIGHT_STATUS_HELP_MSG, 		"Set all selected items to the same height"),
new Pair(Mi_ALIGN_BOTTOM_STATUS_HELP_MSG, 		"Align the bottoms of all selected items"),
new Pair(Mi_ALIGN_TOP_STATUS_HELP_MSG, 			"Align the tops of all selected items"),
new Pair(Mi_ALIGN_LEFT_STATUS_HELP_MSG, 		"Align the lefts of all selected items"),
new Pair(Mi_ALIGN_RIGHT_STATUS_HELP_MSG, 		"Align the rights of all selected items"),
new Pair(Mi_FORMAT_GRID_STATUS_HELP_MSG, 		"Group all selected items into a grid"),
new Pair(Mi_FORMAT_ROW_STATUS_HELP_MSG, 		"Group all selected items into a row"),
new Pair(Mi_FORMAT_BJ_ROW_STATUS_HELP_MSG,		"Group all selected items into a row"),
new Pair(Mi_FORMAT_TJ_ROW_STATUS_HELP_MSG, 		"Group all selected items into a row"),
new Pair(Mi_FORMAT_COLUMN_STATUS_HELP_MSG, 		"Group all selected items into a column"),
new Pair(Mi_FORMAT_LJ_COLUMN_STATUS_HELP_MSG,		"Group all selected items into a column"),
new Pair(Mi_FORMAT_RJ_COLUMN_STATUS_HELP_MSG,		"Group all selected items into a column"),
new Pair(Mi_REMOVE_FORMAT_STATUS_HELP_MSG, 		"Remove formatting from (ungroup) all selected items"),


new Pair(Mi_NO_ARRANGE_GRID_STATUS_HELP_MSG,		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_ARRANGE_ROW_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_ARRANGE_COLUMN_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_SAME_WIDTH_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_SAME_HEIGHT_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_ALIGN_BOTTOM_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_ALIGN_TOP_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_ALIGN_LEFT_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_ALIGN_RIGHT_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_FORMAT_GRID_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_FORMAT_ROW_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_FORMAT_BJ_ROW_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_FORMAT_TJ_ROW_STATUS_HELP_MSG,		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_FORMAT_COLUMN_STATUS_HELP_MSG, 		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_FORMAT_LJ_COLUMN_STATUS_HELP_MSG,	Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_FORMAT_RJ_COLUMN_STATUS_HELP_MSG,	Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),
new Pair(Mi_NO_REMOVE_FORMAT_STATUS_HELP_MSG,		Mi_CMD_NOT_AVAILABLE_NOTHING_SELECTED_MSG),

new Pair(Mi_ARRANGE_GRID_BALLOON_HELP_MSG,		null),
new Pair(Mi_ARRANGE_ROW_BALLOON_HELP_MSG, 		null),
new Pair(Mi_ARRANGE_COLUMN_BALLOON_HELP_MSG, 		null),
new Pair(Mi_SAME_WIDTH_BALLOON_HELP_MSG, 		null),
new Pair(Mi_SAME_HEIGHT_BALLOON_HELP_MSG, 		null),
new Pair(Mi_ALIGN_BOTTOM_BALLOON_HELP_MSG, 		null),
new Pair(Mi_ALIGN_TOP_BALLOON_HELP_MSG, 		null),
new Pair(Mi_ALIGN_LEFT_BALLOON_HELP_MSG, 		null),
new Pair(Mi_ALIGN_RIGHT_BALLOON_HELP_MSG, 		null),
new Pair(Mi_FORMAT_GRID_BALLOON_HELP_MSG, 		null),
new Pair(Mi_FORMAT_ROW_BALLOON_HELP_MSG, 		null),
new Pair(Mi_FORMAT_BJ_ROW_BALLOON_HELP_MSG, 		null),
new Pair(Mi_FORMAT_TJ_ROW_BALLOON_HELP_MSG, 		null),
new Pair(Mi_FORMAT_COLUMN_BALLOON_HELP_MSG, 		null),
new Pair(Mi_FORMAT_LJ_COLUMN_BALLOON_HELP_MSG,		null),
new Pair(Mi_FORMAT_RJ_COLUMN_BALLOON_HELP_MSG,		null),
new Pair(Mi_REMOVE_FORMAT_BALLOON_HELP_MSG, 		null),

new Pair(Mi_SCALE_TO_FIT_PAGE_STATUS_HELP_NAME,		"Resizes and moves graphics to fit printed page(s)"),
new Pair(Mi_MOVE_TO_FIT_PAGE_STATUS_HELP_NAME,		"Moves graphics to be centered on printed page(s)"),
new Pair(Mi_ADJUST_PAGES_TO_FIT_STATUS_HELP_NAME,	"Adds/removes printed pages to contain graphics")
};

	}

