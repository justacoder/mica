
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
import java.awt.Font; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public interface MiiToolkit extends MiiLookProperties
	{
	String	Mi_NOT_A_VALID_PROPERTY_VALUE	= "Mi_NOT_A_VALID_PROPERTY_VALUE";

	int	Mi_ORDINARY_DIALOG_TYPE	= 0;
	int	Mi_WARNING_DIALOG_TYPE	= 1;
	int	Mi_HELP_DIALOG_TYPE	= 2;
	int	Mi_INFO_DIALOG_TYPE	= 3;
	int	Mi_WORKING_DIALOG_TYPE	= 4;
	int	Mi_QUERY_DIALOG_TYPE	= 5;
	int	Mi_ERROR_DIALOG_TYPE	= 6;


	String[]	dialogIconPropertyNames =
		{
		Mi_ORDINARY_DIALOG_ICON_NAME,
		Mi_WARNING_DIALOG_ICON_NAME,
		Mi_HELP_DIALOG_ICON_NAME,
		Mi_INFO_DIALOG_ICON_NAME,
		Mi_WORKING_DIALOG_ICON_NAME,
		Mi_QUERY_DIALOG_ICON_NAME,
		Mi_ERROR_DIALOG_ICON_NAME
		};

	String	Mi_ORDINARY_DIALOG_TEXT_ATTRS_NAME	= "MiOrdinaryDialogText";
	String	Mi_WARNING_DIALOG_TEXT_ATTRS_NAME	= "MiWarningDialogText";
	String	Mi_HELP_DIALOG_TEXT_ATTRS_NAME		= "MiHelpDialogText";
	String	Mi_INFO_DIALOG_TEXT_ATTRS_NAME		= "MiInfoDialogText";
	String	Mi_WORKING_DIALOG_TEXT_ATTRS_NAME	= "MiWorkingDialogText";
	String	Mi_QUERY_DIALOG_TEXT_ATTRS_NAME		= "MiQueryDialogText";
	String	Mi_ERROR_DIALOG_TEXT_ATTRS_NAME		= "MiErrorDialogText";

	String	Mi_ORDINARY_DIALOG_ATTRS_NAME		= "MiOrdinaryDialog";
	String	Mi_WARNING_DIALOG_ATTRS_NAME		= "MiWarningDialog";
	String	Mi_HELP_DIALOG_ATTRS_NAME		= "MiHelpDialog";
	String	Mi_INFO_DIALOG_ATTRS_NAME		= "MiInfoDialog";
	String	Mi_WORKING_DIALOG_ATTRS_NAME		= "MiWorkingDialog";
	String	Mi_QUERY_DIALOG_ATTRS_NAME		= "MiQueryDialog";
	String	Mi_ERROR_DIALOG_ATTRS_NAME		= "MiErrorDialog";

	String[]	dialogTextAttributesPropertyNames =
		{
		Mi_ORDINARY_DIALOG_TEXT_ATTRS_NAME,
		Mi_WARNING_DIALOG_TEXT_ATTRS_NAME,
		Mi_HELP_DIALOG_TEXT_ATTRS_NAME,
		Mi_INFO_DIALOG_TEXT_ATTRS_NAME,
		Mi_WORKING_DIALOG_TEXT_ATTRS_NAME,
		Mi_QUERY_DIALOG_TEXT_ATTRS_NAME,
		Mi_ERROR_DIALOG_TEXT_ATTRS_NAME
		};
	String[]	dialogAttributesPropertyNames =
		{
		Mi_ORDINARY_DIALOG_ATTRS_NAME,
		Mi_WARNING_DIALOG_ATTRS_NAME,
		Mi_HELP_DIALOG_ATTRS_NAME,
		Mi_INFO_DIALOG_ATTRS_NAME,
		Mi_WORKING_DIALOG_ATTRS_NAME,
		Mi_QUERY_DIALOG_ATTRS_NAME,
		Mi_ERROR_DIALOG_ATTRS_NAME
		};

	String		Mi_TOOLKIT_EDITABLE_BG_PROPERTY_NAME	= "MiToolkit.editableBackground";
	String		Mi_TOOLKIT_UNEDITABLE_BG_PROPERTY_NAME	= "MiToolkit.uneditableBackground";
	String		Mi_TOOLKIT_INDENTED_BG_PROPERTY_NAME	= "MiToolkit.indentedBackground";

	String		Mi_TOOLKIT_TEXT_SENSITIVE_PROPERTY_NAME	= "MiToolkit.textSensitive";
	String		Mi_TOOLKIT_TEXT_INSENSITIVE_PROPERTY_NAME= "MiToolkit.textInsensitive";
	String		Mi_TOOLKIT_TEXT_EDITABLE_PROPERTY_NAME	= "MiToolkit.textEditable";
	String		Mi_TOOLKIT_TEXT_INEDITABLE_PROPERTY_NAME= "MiToolkit.textIneditable";
	String		Mi_TOOLKIT_TEXT_SELECTED_PROPERTY_NAME	= "MiToolkit.textSelected";
	String		Mi_TOOLKIT_TEXT_PROPERTY_NAME		= "MiToolkit.text";

	String		Mi_TOOLKIT_WINDOW_TEXT_PROPERTY_NAME	= "MiToolkit.windowText";
	String		Mi_TOOLKIT_WINDOW_PROPERTY_NAME		= "MiToolkit.window";

	String		Mi_TOOLKIT_MENU_TEXT_PROPERTY_NAME	= "MiToolkit.menuText";
	String		Mi_TOOLKIT_MENU_PROPERTY_NAME		= "MiToolkit.menu";

	String		Mi_TOOLKIT_CELL_FOCUS_PROPERTY_NAME	= "MiToolkit.cellFocus";
	String		Mi_TOOLKIT_CELL_SELECTED_PROPERTY_NAME	= "MiToolkit.cellSelected";


	int		Mi_TOOLKIT_EDITABLE_BG_ATTRIBUTES	= 0;
	int		Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES	= 1;
	int		Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES	= 2;

	int		Mi_TOOLKIT_TEXT_SENSITIVE_ATTRIBUTES	= 3;
	int		Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES	= 4;
	int		Mi_TOOLKIT_TEXT_EDITABLE_ATTRIBUTES	= 5;
	int		Mi_TOOLKIT_TEXT_INEDITABLE_ATTRIBUTES	= 6;
	int		Mi_TOOLKIT_TEXT_SELECTED_ATTRIBUTES	= 7;
	int		Mi_TOOLKIT_TEXT_ATTRIBUTES		= 8;

	int		Mi_TOOLKIT_WINDOW_TEXT_ATTRIBUTES	= 9;
	int		Mi_TOOLKIT_WINDOW_ATTRIBUTES		= 10;

	int		Mi_TOOLKIT_MENU_TEXT_ATTRIBUTES		= 11;
	int		Mi_TOOLKIT_MENU_ATTRIBUTES		= 12;

	int		Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES	= 13;
	int		Mi_TOOLKIT_CELL_SELECTED_ATTRIBUTES	= 14;

	int		Mi_NUMBER_OF_TOOLKIT_ATTRIBUTES		= 15;

	String[]	toolkitAttributesPropertyNames =
		{
		Mi_TOOLKIT_EDITABLE_BG_PROPERTY_NAME	,
		Mi_TOOLKIT_UNEDITABLE_BG_PROPERTY_NAME	,
		Mi_TOOLKIT_INDENTED_BG_PROPERTY_NAME	,

		Mi_TOOLKIT_TEXT_SENSITIVE_PROPERTY_NAME	,
		Mi_TOOLKIT_TEXT_INSENSITIVE_PROPERTY_NAME,
		Mi_TOOLKIT_TEXT_EDITABLE_PROPERTY_NAME	,
		Mi_TOOLKIT_TEXT_INEDITABLE_PROPERTY_NAME,
		Mi_TOOLKIT_TEXT_SELECTED_PROPERTY_NAME	,
		Mi_TOOLKIT_TEXT_PROPERTY_NAME		,

		Mi_TOOLKIT_WINDOW_TEXT_PROPERTY_NAME	,
		Mi_TOOLKIT_WINDOW_PROPERTY_NAME		,

		Mi_TOOLKIT_MENU_TEXT_PROPERTY_NAME	,
		Mi_TOOLKIT_MENU_PROPERTY_NAME		,

		Mi_TOOLKIT_CELL_FOCUS_PROPERTY_NAME	,
		Mi_TOOLKIT_CELL_SELECTED_PROPERTY_NAME
		};
	}



