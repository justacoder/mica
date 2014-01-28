
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

import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Pair; 
import java.io.*;
import java.awt.Component;
import java.awt.Container;

/**----------------------------------------------------------------------------------------------
 * This class implements a help browser to be used to view help
 * files supplied with Mica.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiHelpWindow extends MiFileEditorWindow implements MiiDisplayNames, MiiLookProperties
	{
	// ---------------------------------------------------------------
	// Define the names of some commands
	// ---------------------------------------------------------------
	private static final String	Mi_CHANGED_HELP_VIEW_COMMAND_NAME	= "changedHelpView";
	private static final String	Mi_SEARCH_COMMAND_NAME			= "searchFoward";
	private static final String	Mi_SEARCH_BACKWARD_COMMAND_NAME		= "SearchBackward";
	private static final String	Mi_UPDATE_SEARCH_AVAILABLITY_COMMAND_NAME= "UpdateSearchAvailability";

	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_HELP_WINDOW_BORDER_TITLE
						= "Mi_HELP_WINDOW_BORDER_TITLE";
	private static final String	Mi_HELP_SEARCH_FIELD_LABEL_NAME
						= "Mi_HELP_SEARCH_FIELD_LABEL_NAME";
	private static final String	Mi_HELP_SEARCH_DISPLAY_NAME
						= "Mi_HELP_SEARCH_DISPLAY_NAME";
	private static final String	Mi_HELP_SEARCH_BACKWARD_DISPLAY_NAME
						= "Mi_HELP_SEARCH_BACKWARD_DISPLAY_NAME";

	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
		{
		new Pair( Mi_HELP_WINDOW_BORDER_TITLE		, "Help Window"),
		new Pair( Mi_HELP_SEARCH_FIELD_LABEL_NAME	, "Search:"),
		new Pair( Mi_HELP_SEARCH_DISPLAY_NAME		, "Search"),
		new Pair( Mi_HELP_SEARCH_BACKWARD_DISPLAY_NAME	, "Search"),
		};

	// ---------------------------------------------------------------
	// Register the default values for this applications properties.
	// ---------------------------------------------------------------
	static	{
		MiSystem.setApplicationDefaultProperties(properties);
		}

	// ---------------------------------------------------------------
	// Define the constants this class will use.
	// ---------------------------------------------------------------
	private static final String	Mi_HELP_DEFAULT_FILENAME		= "help.help";

	// ---------------------------------------------------------------
	// Define the class variables this class will use.
	// ---------------------------------------------------------------
	private		MiHelpViewer	helpViewer;
	private		MiComboBox	searchField;
	private		String		searchSpec;
	private		boolean		searchStoppedInTableOfContents;
	private		String		helpFile;
	private		boolean		helpFileIsLoaded;




	public				MiHelpWindow( 
						Container awtContainer, 
						String title, 
						MiBounds screenSize)
		{
		super(MiUtility.getFrame(awtContainer),
			title == null ? Mi_HELP_WINDOW_BORDER_TITLE : title, 
			screenSize,
			null,
			"*.help", Mi_HELP_DEFAULT_FILENAME, false);
		awtContainer.add("Center", (Component )getCanvas().getNativeComponent());
		setup();
		}
	public				MiHelpWindow(String title, MiBounds screenSize)
		{
		super(title == null ? Mi_HELP_WINDOW_BORDER_TITLE : title,
			 screenSize,
			"*.help", Mi_HELP_DEFAULT_FILENAME, false);
		setup();
		}
	public		void		setHelpFile(String filename)
		{
		helpFile = filename;
		helpFileIsLoaded = false;
		if (isVisible())
			{
			openFile(filename);
			helpFileIsLoaded = true;
			}
		}
	public		String		getHelpFile()
		{
		return(helpFile);
		}
	public		void		setVisible(boolean flag)
		{
		if ((flag) && (!isVisible()) && (helpFile != null))
			{
			openFile(helpFile);
			helpFileIsLoaded = true;
			}
		super.setVisible(flag);
		}

	public		void		setSearchPattern(String pattern)
		{
		if (searchField.getList().getIndexOfItem(pattern) == -1)
			((MiList )searchField.getList()).insertItem(pattern, 0);
		searchField.getList().setValue(pattern);
		helpViewer.setCurrentSectionIndex(0);
		processCommand(Mi_SEARCH_COMMAND_NAME);
		}
	protected 	void		setup()
		{
		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		MiEditorMenuBar menuBar = new MiEditorMenuBar();
		MiEditorMenu menu = new MiEditorMenu(Mi_FILE_MENU_DISPLAY_NAME, this);
		menu.appendMenuItem(Mi_QUIT_MENUITEM_DISPLAY_NAME, this, Mi_QUIT_COMMAND_NAME);
		menuBar.appendPulldownMenu(menu);

		MiToolBar toolBar = new MiToolBar(null, this);
		toolBar.setBorderLook(Mi_RAISED_BORDER_LOOK);

		toolBar.appendToolItem(	"Back",
					new MiImage(Mi_PREVIOUS_ICON_NAME),
					this, 
					Mi_PREVIOUS_COMMAND_NAME);

		toolBar.appendToolItem(	Mi_NEXT_DISPLAY_NAME,
					new MiImage(Mi_NEXT_ICON_NAME),
					this, 
					Mi_NEXT_COMMAND_NAME);

		toolBar.appendPart(new MiLabel(Mi_HELP_SEARCH_FIELD_LABEL_NAME));

		searchField = new MiComboBox();
		searchField.getTextField().setNumDisplayedColumns(40);

		searchField.setRestrictingValuesToThoseInList(false);
		searchField.getTextField().setMaxNumDisplayedRows(1);
		searchField.getTextField().appendCommandHandler(
			this, Mi_UPDATE_SEARCH_AVAILABLITY_COMMAND_NAME, Mi_TEXT_CHANGE_ACTION);
		searchField.getTextField().appendCommandHandler(
			this, Mi_SEARCH_COMMAND_NAME, Mi_ENTER_KEY_ACTION);
		((MiLayout )toolBar.getLayout()).setUniqueElementIndex(toolBar.getNumberOfParts());
		((MiLayout )toolBar.getLayout()).setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		toolBar.appendPart(searchField);

		toolBar.appendToolItem(	Mi_HELP_SEARCH_DISPLAY_NAME,
					new MiImage(Mi_DOWN_ICON_NAME, true),
					this, 
					Mi_SEARCH_COMMAND_NAME);

		toolBar.appendToolItem(	Mi_HELP_SEARCH_BACKWARD_DISPLAY_NAME,
					new MiImage(Mi_UP_ICON_NAME, true),
					this, 
					Mi_SEARCH_BACKWARD_COMMAND_NAME);

		toolBar.setToolItemImageSizes(new MiSize(24, 24));

		helpViewer = new MiHelpViewer();

		buildEditorWindow(true, false, menuBar, toolBar, null, helpViewer, null);

		setCommandSensitivity(Mi_PREVIOUS_COMMAND_NAME, false);
		setCommandSensitivity(Mi_NEXT_COMMAND_NAME, false);
		setCommandSensitivity(Mi_SEARCH_COMMAND_NAME, false);
		setCommandSensitivity(Mi_SEARCH_BACKWARD_COMMAND_NAME, false);

		helpViewer.appendCommandHandler(this, 
			Mi_CHANGED_HELP_VIEW_COMMAND_NAME, MiHelpViewer.Mi_HELP_VIEW_CHANGED_ACTION);
		}
					/**------------------------------------------------------
	 				 * Opens the default, no name, file that the user can
					 * edit in but which needs to be named before saving.
					 * @overrides		MiFileEditorWindow#defaultFileOpened
					 *------------------------------------------------------*/
	protected	void		defaultFileOpened()
		{
		}
					/**------------------------------------------------------
	 				 * Sets whether this window has changed. This is set to
					 * false by the MiFileEditorWindow after a save or open.
					 * @param flag		true if this has changed.
					 * @overrides		MiFileEditorWindow#setHasChanged
					 *------------------------------------------------------*/
	public		void		setHasChanged(boolean flag)
		{
		}
					/**------------------------------------------------------
	 				 * Gets whether this window has changed. This is checked
					 * by the MiFileEditorWindow before a open, close or new.
					 * @return 		true if this has changed.
					 * @overrides		MiFileEditorWindow#getHasChanged
					 *------------------------------------------------------*/
	protected	boolean		getHasChanged()
		{
		return(false);
		}
					/**------------------------------------------------------
	 				 * Saves the contents of the graphics editor.
					 * @param stream 	where to save the contents
					 * @overrides		MiFileEditorWindow#save
					 *------------------------------------------------------*/
	public		boolean		save(OutputStream stream, String streamName)
		{
		return(true);
		}
					/**------------------------------------------------------
	 				 * Loads the contents of the help viewer from the given
					 * stream. 
					 * @param stream 	where to get the contents
					 * @overrides		MiFileEditorWindow#load
					 *------------------------------------------------------*/
	public		void		load(BufferedInputStream inputStream, String streamName) throws IOException
		{
		setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		helpViewer.load(inputStream, streamName);
		helpViewer.setCurrentSectionIndex(0);
		setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		}
					/**------------------------------------------------------
	 				 * Processes the given command.
					 * @param command  	the command to execute
					 * @overrides		MiFileEditorWindow#processCommand
					 * @implements		MiiCommandHandler#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		if (command.equalsIgnoreCase(Mi_PREVIOUS_COMMAND_NAME))
			{
			helpViewer.moveCurrentlyViewedSection(false);
			}
		else if (command.equalsIgnoreCase(Mi_NEXT_COMMAND_NAME))
			{
			helpViewer.moveCurrentlyViewedSection(true);
			}
		else if (command.equalsIgnoreCase(Mi_CHANGED_HELP_VIEW_COMMAND_NAME))
			{
			setCommandSensitivity(Mi_PREVIOUS_COMMAND_NAME, 
				helpViewer.getPreviouslyViewedSection() != null);
			setCommandSensitivity(Mi_NEXT_COMMAND_NAME, 
				helpViewer.getNextViewedSection() != null);
			}
		else if (command.equalsIgnoreCase(Mi_UPDATE_SEARCH_AVAILABLITY_COMMAND_NAME))
			{
			String searchPattern = searchField.getValue().toLowerCase();
			boolean available = !Utility.isEmptyOrNull(searchPattern);
			setCommandSensitivity(Mi_SEARCH_COMMAND_NAME, available);
			setCommandSensitivity(Mi_SEARCH_BACKWARD_COMMAND_NAME, available);
			}
		else if (command.equalsIgnoreCase(Mi_SEARCH_COMMAND_NAME))
			{
			Strings contents = helpViewer.getContentsList();
			int startingSection = helpViewer.getCurrentSectionIndex();
			String searchPattern = searchField.getValue().toLowerCase();
			if (Utility.isEmptyOrNull(searchPattern))
				return;

			if (!searchPattern.equals(searchSpec))
				searchStoppedInTableOfContents = false;
			searchSpec = searchPattern;

			if (searchField.getList().getIndexOfItem(searchPattern) == -1)
				((MiList )searchField.getList()).insertItem(searchPattern, 0);

			setCommandSensitivity(Mi_SEARCH_BACKWARD_COMMAND_NAME, true);
			helpViewer.deHighlightAll();

			String content;
			int startingOffset = helpViewer.getCurrentSectionOffset();
			if (startingOffset == -1)
				{
				for (int i = startingSection + 1; i < contents.size(); ++i)
					{
					content = contents.elementAt(i).toLowerCase();
					if (content.indexOf(searchPattern) != -1)
						{
						helpViewer.setCurrentSection(contents.elementAt(i), 0, 0);
						searchStoppedInTableOfContents = true;
						return;
						}
					}
				}
			if (searchStoppedInTableOfContents)
				{
				startingSection = 0;
				startingOffset = -1;
				}
			++startingOffset;
			for (int i = startingSection; i < contents.size(); ++i)
				{
				content = helpViewer.getHelpText(i).toLowerCase();
				int index;
				if ((index = content.indexOf(searchPattern, startingOffset)) != -1)
					{
					helpViewer.setCurrentSection(
						contents.elementAt(i), index, searchPattern.length());
					searchStoppedInTableOfContents = false;
					return;
					}
				startingOffset = 0;
				}
			setCommandSensitivity(Mi_SEARCH_COMMAND_NAME, false);
			}
		else if (command.equalsIgnoreCase(Mi_SEARCH_BACKWARD_COMMAND_NAME))
			{
			Strings contents = helpViewer.getContentsList();
			int startingSection = helpViewer.getCurrentSectionIndex();
			String searchPattern = searchField.getValue().toLowerCase();
			if (Utility.isEmptyOrNull(searchPattern))
				return;

			if (!searchPattern.equals(searchSpec))
				searchStoppedInTableOfContents = false;
			searchSpec = searchPattern;

			if (searchField.getList().getIndexOfItem(searchPattern) == -1)
				((MiList )searchField.getList()).insertItem(searchPattern, 0);

			setCommandSensitivity(Mi_SEARCH_COMMAND_NAME, true);
			helpViewer.deHighlightAll();

			String content;
			int startingOffset = helpViewer.getCurrentSectionOffset();
			for (int i = startingSection; i >= 0; --i)
				{
				int index;
				content = helpViewer.getHelpText(i).toLowerCase();
				if (startingOffset == -1)
					startingOffset = content.length() - 1;
				else
					--startingOffset;
				if ((index = content.lastIndexOf(searchPattern, startingOffset)) != -1)
					{
					helpViewer.setCurrentSection(
						contents.elementAt(i), index, searchPattern.length());
					searchStoppedInTableOfContents = false;
					return;
					}
				startingOffset = -1;
				}
			if (!searchStoppedInTableOfContents)
				startingSection = contents.size();
			for (int i = startingSection - 1; i >= 0; --i)
				{
				content = contents.elementAt(i).toLowerCase();
				if (content.lastIndexOf(searchPattern) != -1)
					{
					helpViewer.setCurrentSection(contents.elementAt(i), 0, 0);
					searchStoppedInTableOfContents = true;
					return;
					}
				}
			setCommandSensitivity(Mi_SEARCH_BACKWARD_COMMAND_NAME, false);
			}
		else
			{
			super.processCommand(command);
			}
		}
	}


