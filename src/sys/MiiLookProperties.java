
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
public interface MiiLookProperties
	{
	String		Mi_ICONIZED_WINDOW_ICON_NAME	= "MiIconizedWindowIcon";
	String		Mi_ICONIZED_PARTS_ICON_NAME	= "MiIconizedPartsIcon";

	String		Mi_ORDINARY_DIALOG_ICON_NAME	= "MiOrdinaryDialogIcon";
	String		Mi_WARNING_DIALOG_ICON_NAME 	= "MiWarningDialogIcon";
	String		Mi_HELP_DIALOG_ICON_NAME 	= "MiHelpDialogIcon";
	String		Mi_INFO_DIALOG_ICON_NAME 	= "MiInfoDialogIcon";
	String		Mi_WORKING_DIALOG_ICON_NAME 	= "MiWorkingDialogIcon";
	String		Mi_QUERY_DIALOG_ICON_NAME 	= "MiQueryDialogIcon";
	String		Mi_ERROR_DIALOG_ICON_NAME 	= "MiErrorDialogIcon";

	String		Mi_CHECK_MARK_ICON_NAME 	= "MiCheckMarkIcon";

	String		Mi_COMPANY_ICON_NAME		= "MiCompanyIcon";
	String		Mi_COMPANY_NAME			= "MiCompanyName";

	String		Mi_SAVE_ICON_NAME		= "MiSaveIcon";
	String		Mi_PRINT_ICON_NAME		= "MiPrintIcon";
	String		Mi_UNDO_ICON_NAME		= "MiUndoIcon";
	String		Mi_REDO_ICON_NAME		= "MiRedoIcon";
	String		Mi_CUT_ICON_NAME		= "MiCutIcon";
	String		Mi_COPY_ICON_NAME		= "MiCopyIcon";
	String		Mi_PASTE_ICON_NAME		= "MiPasteIcon";
	String		Mi_ZOOM_IN_ICON_NAME		= "MiZoomInIcon";
	String		Mi_ZOOM_OUT_ICON_NAME		= "MiZoomOutIcon";
	String		Mi_VIEW_ALL_ICON_NAME		= "MiViewAllIcon";
	String		Mi_VIEW_FIT_ICON_NAME		= "MiViewFitIcon";
	String		Mi_VIEW_PREVIOUS_ICON_NAME	= "MiViewPreviousIcon";
	String		Mi_VIEW_NEXT_ICON_NAME		= "MiViewNextIcon";
	String		Mi_ZOOM_HOME_ICON_NAME		= "MiZoomHomeIcon";
	String		Mi_PREVIOUS_ICON_NAME		= "MiPreviousIcon";
	String		Mi_NEXT_ICON_NAME		= "MiNextIcon";
	String		Mi_UP_ICON_NAME			= "MiUpIcon";
	String		Mi_DOWN_ICON_NAME		= "MiDownIcon";
	String		Mi_DELETE_ICON_NAME		= "MiDeleteIcon";

	String		Mi_OPEN_FOLDER_ICON_NAME	= "MiOpenFolderIcon";
	String		Mi_CLOSED_FOLDER_ICON_NAME	= "MiClosedFolderIcon";

	String		Mi_LEFT_JUSTIFIED_ICON_NAME	= "MiLeftJustifiedIcon";
	String		Mi_CENTER_JUSTIFIED_ICON_NAME	= "MiCenterJustifiedIcon";
	String		Mi_RIGHT_JUSTIFIED_ICON_NAME	= "MiRightJustifiedIcon";
	String		Mi_JUSTIFIED_ICON_NAME		= "MiJustifiedIcon";

	// Feel properties
	String		Mi_PUSHBUTTON_AUTO_REPEAT_START_DELAY_NAME = "MiPushbuttonAutoRepeatStartDelay";
	String		Mi_PUSHBUTTON_AUTO_REPEAT_INTERVAL_NAME	= "MiPushbuttonAutoRepeatInterval";

	Pair[]	lookProperties =
		{
		new Pair(	Mi_ICONIZED_WINDOW_ICON_NAME 	, "${Mi_IMAGES_HOME}/windowIcon.gif"),
		new Pair(	Mi_ICONIZED_PARTS_ICON_NAME 	, "${Mi_IMAGES_HOME}/iconified.gif"),

		new Pair(	Mi_WARNING_DIALOG_ICON_NAME 	, "${Mi_IMAGES_HOME}/warningDialog.gif"),
		new Pair(	Mi_HELP_DIALOG_ICON_NAME 	, "${Mi_IMAGES_HOME}/helpDialog.gif"),
		new Pair(	Mi_INFO_DIALOG_ICON_NAME 	, "${Mi_IMAGES_HOME}/infoDialog.gif"),
		new Pair(	Mi_WORKING_DIALOG_ICON_NAME 	, "${Mi_IMAGES_HOME}/workingDialog.gif"),
		new Pair(	Mi_QUERY_DIALOG_ICON_NAME 	, "${Mi_IMAGES_HOME}/queryDialog.gif"),
		new Pair(	Mi_ERROR_DIALOG_ICON_NAME 	, "${Mi_IMAGES_HOME}/errorDialog.gif"),

		new Pair(	Mi_CHECK_MARK_ICON_NAME 	, "${Mi_IMAGES_HOME}/checkMark.xpm"),

		new Pair(	Mi_SAVE_ICON_NAME 		, "${Mi_IMAGES_HOME}/save.gif"),
		new Pair(	Mi_PRINT_ICON_NAME		, "${Mi_IMAGES_HOME}/print.gif"),
		new Pair(	Mi_UNDO_ICON_NAME		, "${Mi_IMAGES_HOME}/undo.gif"),
		new Pair(	Mi_REDO_ICON_NAME		, "${Mi_IMAGES_HOME}/redo.gif"),
		new Pair(	Mi_CUT_ICON_NAME		, "${Mi_IMAGES_HOME}/cut.gif"),
		new Pair(	Mi_COPY_ICON_NAME		, "${Mi_IMAGES_HOME}/copy.gif"),
		new Pair(	Mi_PASTE_ICON_NAME		, "${Mi_IMAGES_HOME}/paste.gif"),
		new Pair(	Mi_ZOOM_IN_ICON_NAME		, "${Mi_IMAGES_HOME}/zoomIn.gif"),
		new Pair(	Mi_ZOOM_OUT_ICON_NAME		, "${Mi_IMAGES_HOME}/zoomOut.gif"),
		new Pair(	Mi_VIEW_ALL_ICON_NAME		, "${Mi_IMAGES_HOME}/viewAll.gif"),
		new Pair(	Mi_VIEW_FIT_ICON_NAME		, "${Mi_IMAGES_HOME}/viewFit.gif"),
		new Pair(	Mi_VIEW_PREVIOUS_ICON_NAME	, "${Mi_IMAGES_HOME}/viewPrevious.gif"),
		new Pair(	Mi_VIEW_NEXT_ICON_NAME		, "${Mi_IMAGES_HOME}/viewNext.gif"),
		new Pair(	Mi_ZOOM_HOME_ICON_NAME		, "${Mi_IMAGES_HOME}/zoomHome.gif"),

		new Pair(	Mi_PREVIOUS_ICON_NAME		, "${Mi_IMAGES_HOME}/previous.gif"),
		new Pair(	Mi_NEXT_ICON_NAME		, "${Mi_IMAGES_HOME}/next.gif"),
		new Pair(	Mi_UP_ICON_NAME			, "${Mi_IMAGES_HOME}/up.gif"),
		new Pair(	Mi_DOWN_ICON_NAME		, "${Mi_IMAGES_HOME}/down.gif"),

		new Pair(	Mi_DELETE_ICON_NAME		, "${Mi_IMAGES_HOME}/delete.gif"),
		new Pair(	Mi_OPEN_FOLDER_ICON_NAME	, "${Mi_IMAGES_HOME}/openFolder.gif"),
		new Pair(	Mi_CLOSED_FOLDER_ICON_NAME	, "${Mi_IMAGES_HOME}/closedFolder.gif"),

		new Pair(	Mi_LEFT_JUSTIFIED_ICON_NAME	, "${Mi_IMAGES_HOME}/leftJustified.xpm"),
		new Pair(	Mi_CENTER_JUSTIFIED_ICON_NAME	, "${Mi_IMAGES_HOME}/centerJustified.xpm"),
		new Pair(	Mi_RIGHT_JUSTIFIED_ICON_NAME	, "${Mi_IMAGES_HOME}/rightJustified.xpm"),
		new Pair(	Mi_JUSTIFIED_ICON_NAME		, "${Mi_IMAGES_HOME}/justified.xpm"),

		new Pair(	Mi_COMPANY_ICON_NAME		, "${Mi_IMAGES_HOME}/swfm.gif"),
		new Pair(	Mi_COMPANY_NAME			, "Software Farm, Inc."),

		new Pair(	Mi_PUSHBUTTON_AUTO_REPEAT_START_DELAY_NAME, "300"),
		new Pair(	Mi_PUSHBUTTON_AUTO_REPEAT_INTERVAL_NAME	, "100")
		};
	Pair[]	customLookAndFeelRegistrationTable =
		{
		new Pair(	"Sound",			"com.swfm.mica.MiCustomLookAndFeelSound")
		};

	}


