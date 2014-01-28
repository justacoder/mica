
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

import java.util.Vector; 
import java.io.*;
import com.swfm.mica.util.TextFile;
import com.swfm.mica.util.Utility;

// FIX: TO DO: break this up into 4 + 1 classes

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiHelpManager
	{
	public static final int		NO_HELP_AVAILABLE_ID 		= 0;
	private static final	String	NO_HELP_AVAILABLE_MESSAGE = "No Help Available";

	private		boolean		toolHintHelpEnabled 	= true;
	private		boolean		balloonHelpEnabled 	= true;
	private		boolean		statusHelpEnabled 	= true;
	private		boolean		dialogHelpEnabled 	= true;

	private		MiiHelpFile	toolHintHelpFile;
	private		MiiHelpFile	balloonHelpFile;
	private		MiiHelpFile	statusHelpFile;
	private		MiiHelpFile	dialogHelpFile;

	private		MiPart		aboutDialog;
	private		MiPart		helpOnApplicationDialog;

	private		String[]	noHelpAvailableMessage	= new String[4];

	public static final int		TOOLTIP_HELP_TYPE	= 0;
	public static final int		BALLOON_HELP_TYPE	= 1;
	public static final int		STATUS_HELP_TYPE	= 2;
	public static final int		DIALOG_HELP_TYPE	= 3;

	public static final int		SHOW_HELP_NOT_FOUND_MESSAGES_DEBUG_MODE		= 1;
	public static final int		SHOW_NAME_OF_OBJECT_WITHOUT_HELP_DEBUG_MODE	= 2;

	private		int	debugMode		= MiiTypes.Mi_NONE;
/*
	private		int	debugMode		= SHOW_HELP_NOT_FOUND_MESSAGES_DEBUG_MODE
							+ SHOW_NAME_OF_OBJECT_WITHOUT_HELP_DEBUG_MODE;

*/
	private		int	toolHintMethodology	= METHODOLOGY_SHOWING_HELPTEXT_ASSIGNED_TO_OBJECT;
	private		int	balloonMethodology	= METHODOLOGY_SHOWING_HELPTEXT_ASSIGNED_TO_OBJECT;
	private		int	statusMethodology	= METHODOLOGY_SHOWING_HELPTEXT_ASSIGNED_TO_OBJECT;
	private		int	dialogMethodology	= METHODOLOGY_SHOWING_HELPTEXT_ASSIGNED_TO_OBJECT;

public static final int		METHODOLOGY_SHOWING_HELPTEXT_ASSIGNED_TO_OBJECT	= 0;
public static final int		METHODOLOGY_SHOWING_ONE_MESSAGE_USING_OBJECT_NAME_AS_KEY_IN_FILE = 1;
public static final int		METHODOLOGY_SCROLLING_TO_MESSAGE_USING_OBJECT_NAME_AS_KEY_IN_FILE = 2;
public static final int		METHODOLOGY_SHOWING_ONE_MESSAGE_USING_OBJECT_HELPTEXT_AS_KEY_IN_FILE = 3;
public static final int		METHODOLOGY_SCROLLING_TO_MESSAGE_USING_OBJECT_HELPTEXT_AS_KEY_IN_FILE = 4;
public static final int		METHODOLOGY_USING_HELPTEXT_AS_FILENAME = 5;

	public				MiHelpManager()
		{
		noHelpAvailableMessage[TOOLTIP_HELP_TYPE] = NO_HELP_AVAILABLE_MESSAGE;
		noHelpAvailableMessage[BALLOON_HELP_TYPE] = NO_HELP_AVAILABLE_MESSAGE;
		noHelpAvailableMessage[STATUS_HELP_TYPE] = NO_HELP_AVAILABLE_MESSAGE;
		noHelpAvailableMessage[DIALOG_HELP_TYPE] = NO_HELP_AVAILABLE_MESSAGE;
		}

	public		void		setBalloonHelpEnabled(boolean flag)
		{
		balloonHelpEnabled = flag;
		}
	public		boolean		getBalloonHelpEnabled()
		{
		return(balloonHelpEnabled);
		}
	public		void		setToolHintHelpEnabled(boolean flag)
		{
		toolHintHelpEnabled = flag;
		}
	public		boolean		getToolHintHelpEnabled()
		{
		return(toolHintHelpEnabled);
		}
	public		void		setStatusHelpEnabled(boolean flag)
		{
		statusHelpEnabled = flag;
		}
	public		boolean		getStatusHelpEnabled()
		{
		return(statusHelpEnabled);
		}
	public		void		setDialogHelpEnabled(boolean flag)
		{
		dialogHelpEnabled = flag;
		}
	public		boolean		getDialogHelpEnabled()
		{
		return(dialogHelpEnabled);
		}
	public		void		setToolHintMethodology(int methodology)
		{
		toolHintMethodology = methodology;
		}
	public		int		getToolHintMethodology()
		{
		return(toolHintMethodology);
		}
	public		void		setStatusHelpMethodology(int methodology)
		{
		statusMethodology = methodology;
		}
	public		int		getStatusHelpMethodology()
		{
		return(statusMethodology);
		}
	public		void		setBalloonHelpMethodology(int methodology)
		{
		balloonMethodology = methodology;
		}
	public		int		getBalloonHelpMethodology()
		{
		return(balloonMethodology);
		}
	public		void		setDialogHelpMethodology(int methodology)
		{
		dialogMethodology = methodology;
		}
	public		int		getDialogHelpMethodology()
		{
		return(dialogMethodology);
		}

	public		void		setToolHintHelpFilename(String name)
		{ toolHintHelpFile = new MiDotKeyHelpFile(name, true); }

	public		void		setBallonHelpFilename(String name)
		{ balloonHelpFile = new MiDotKeyHelpFile(name, true); }

	public		void		setStatusHelpFilename(String name)
		{ statusHelpFile = new MiDotKeyHelpFile(name, true); }

	public		void		setDialogHelpFilename(String name)
		{ dialogHelpFile = new MiDotKeyHelpFile(name, true); }

	public		void		setToolHintHelpFile(MiiHelpFile file)
		{ toolHintHelpFile = file; }

	public		void		setBalloonHelpFile(MiiHelpFile file)
		{ balloonHelpFile = file; }

	public		void		setStatusHelpFile(MiiHelpFile file)
		{ statusHelpFile = file; }

	public		void		setDialogHelpFile(MiiHelpFile file)
		{ dialogHelpFile = file; }

	public		void		setNoToolHintHelpAvailableMessage(String msg)
		{ noHelpAvailableMessage[TOOLTIP_HELP_TYPE] = msg; }

	public		void		setNoBalloonHelpAvailableMessage(String msg)
		{ noHelpAvailableMessage[BALLOON_HELP_TYPE] = msg; }

	public		void		setNoStatusHelpAvailableMessage(String msg)
		{ noHelpAvailableMessage[STATUS_HELP_TYPE] = msg; }

	public		void		setNoDialogHelpAvailableMessage(String msg)
		{ noHelpAvailableMessage[DIALOG_HELP_TYPE] = msg; }



	public		String		getToolHintForObject(MiPart obj, MiPoint point)
		{
		if (!toolHintHelpEnabled)
			return(null);
		return(getHelpForObject(obj, TOOLTIP_HELP_TYPE, point));
		}

	public		String		getStatusHelpForObject(MiPart obj, MiPoint point)
		{
		if (!statusHelpEnabled)
			return(null);
		return(getHelpForObject(obj, STATUS_HELP_TYPE, point));
		}

	public		String		getBalloonHelpForObject(MiPart obj, MiPoint point)
		{
		if (!balloonHelpEnabled)
			return(null);
		return(getHelpForObject(obj, BALLOON_HELP_TYPE, point));
		}

	public		String		getDialogHelpForObject(MiPart obj, MiPoint point)
		{
		if (!dialogHelpEnabled)
			return(null);
		return(getHelpForObject(obj, DIALOG_HELP_TYPE, point));
		}
	public		void		setHelpOnApplicationDialog(MiPart dialog)
		{
		helpOnApplicationDialog = dialog;
		}
	public		MiPart		getHelpOnApplicationDialog()
		{
		return(helpOnApplicationDialog);
		}
	public		void		displayHelpOnApplication()
		{
		if (helpOnApplicationDialog != null)
			helpOnApplicationDialog.setVisible(true);
		}
	public		void		setAboutDialog(MiPart aboutDialog)
		{
		this.aboutDialog = aboutDialog;
		}
	public		MiPart		getAboutDialog()
		{
		return(aboutDialog);
		}
	public		void		displayAboutDialog()
		{
		if (aboutDialog != null)
			aboutDialog.setVisible(true);
		}
	private		boolean		helpTextIsNeeded(int methodology)
		{
		if ((methodology != METHODOLOGY_SCROLLING_TO_MESSAGE_USING_OBJECT_NAME_AS_KEY_IN_FILE)
		    && (methodology !=  METHODOLOGY_SHOWING_ONE_MESSAGE_USING_OBJECT_NAME_AS_KEY_IN_FILE))
			{
			return(true);
			}
		return(false);
		}
	private		String		getHelpForObject(MiPart obj, int helpType, MiPoint point)
		{
		boolean 	getHelpText 	= false;
		MiiHelpInfo	helpInfo	= null;
		MiiHelpFile	helpFile;
		String		helpText	= new String();
		int		methodology;

		switch (helpType)
			{
			case TOOLTIP_HELP_TYPE:
				helpFile = toolHintHelpFile;
				methodology = toolHintMethodology;
				getHelpText = helpTextIsNeeded(methodology);
				if (getHelpText)
					{
					helpInfo = obj.getToolHintHelp(point);
					if ((helpInfo != null) && (helpInfo.isEnabled()))
						helpText = helpInfo.getMessage();
					}
				break;
			case BALLOON_HELP_TYPE:
				helpFile = balloonHelpFile;
				methodology = balloonMethodology;
				getHelpText = helpTextIsNeeded(methodology);
				if (getHelpText)
					{
					helpInfo = obj.getBalloonHelp(point);
					if ((helpInfo != null) && (helpInfo.isEnabled()))
						helpText = helpInfo.getMessage();
					}
				break;
			case STATUS_HELP_TYPE:
				helpFile = statusHelpFile;
				methodology = statusMethodology;
				getHelpText = helpTextIsNeeded(methodology);
				if (getHelpText)
					{
					helpInfo = obj.getStatusHelp(point);
					if ((helpInfo != null) && (helpInfo.isEnabled()))
						helpText = helpInfo.getMessage();
					}
				break;
			case DIALOG_HELP_TYPE:
				helpFile = dialogHelpFile;
				methodology = dialogMethodology;
				getHelpText = helpTextIsNeeded(methodology);
				if (getHelpText)
					{
					helpInfo = obj.getDialogHelp(point);
					if ((helpInfo != null) && (helpInfo.isEnabled()))
						helpText = helpInfo.getMessage();
					}
				break;
			default:
				throw new IllegalArgumentException(this + ": Unknown help type: " + helpType);
				
			}
		if (getHelpText && (helpInfo == null))
			return(getDebugMessage(obj, helpType));

		switch (methodology)
			{
			case METHODOLOGY_SHOWING_HELPTEXT_ASSIGNED_TO_OBJECT:
				{
				return((helpText.length() == 0) ? null : helpText);
				}
			case METHODOLOGY_SHOWING_ONE_MESSAGE_USING_OBJECT_NAME_AS_KEY_IN_FILE:
				{
				String key = obj.getName();
				String s = helpFile.getMessageAssignedToKey(key);
				if (s != null)
					return(s);
				return(getDebugMessage(obj, helpType));
				}
			case METHODOLOGY_SCROLLING_TO_MESSAGE_USING_OBJECT_NAME_AS_KEY_IN_FILE:
				{
				MiDebug.printlnError("Scrolling text file NOT IMPLEMENTED");
				/*
				if (!helpViewer.scrollToMessageAssignedToKey(filename, obj.getName()))
					return(getDebugMessage(obj, helpType));
				return(null);
				*/
				}
			case METHODOLOGY_SHOWING_ONE_MESSAGE_USING_OBJECT_HELPTEXT_AS_KEY_IN_FILE:
				{
				String s = helpFile.getMessageAssignedToKey(helpText);
				return((s == null) ? getDebugMessage(obj, helpType) : s);
				}
			case METHODOLOGY_SCROLLING_TO_MESSAGE_USING_OBJECT_HELPTEXT_AS_KEY_IN_FILE:
				{
				MiDebug.printlnError("Scrolling text file NOT IMPLEMENTED");
				/*
				if (!helpViewer.scrollToMessageAssignedToKey(filename, helpText))
					return(getDebugMessage(obj, helpType));
				return(null);
				*/
				}
			case METHODOLOGY_USING_HELPTEXT_AS_FILENAME:
				{
				String msg = loadHelpTextFile(helpText);
				return((msg == null) ? getDebugMessage(obj, helpType) : msg);
				}
			}
		return((helpText.length() == 0) ? null : helpText);
		}
	private 	String		loadHelpTextFile(String filename)
		{
		BufferedReader file = Utility.openInputFile(filename);
		if (file == null)
			return(null);

		String line;
		String text = new String();
		while ((line = Utility.readLine(file)) != null)
			{
			text = text.concat(line);
			}
		return(text);
		}
	private		String		getDebugMessage(MiPart obj, int helpType)
		{
		if (debugMode == SHOW_HELP_NOT_FOUND_MESSAGES_DEBUG_MODE)
			return(noHelpAvailableMessage[helpType]);
		if (debugMode == SHOW_NAME_OF_OBJECT_WITHOUT_HELP_DEBUG_MODE)
			return(obj.toString());
		if (debugMode == SHOW_NAME_OF_OBJECT_WITHOUT_HELP_DEBUG_MODE
			+ SHOW_HELP_NOT_FOUND_MESSAGES_DEBUG_MODE)
			return(noHelpAvailableMessage[helpType] + " for: " + obj.toString());
		return(null);
		}
	}
