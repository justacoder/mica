
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
import com.swfm.mica.util.LongVector;
import java.text.DateFormat;
import java.io.File;
import java.util.Date;

/* Fix: tags in directory treelist should be full absolute paths... */

/**----------------------------------------------------------------------------------------------
 * This class creates a widget that allows a user to browse, find, modify and 
 * optionally choose a file.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiFileChooser extends MiWidget implements MiiActionHandler, MiiCommandHandler, MiiCommandNames, MiiMessages, MiiDisplayNames, MiiLookProperties
	{
	private 	String		Mi_MATCH_ALL_FILES			= "All Files";
	private 	String		Mi_MATCH_ALL_FILES_FORMAT_FILTER	= "All Files (*)";
	private 	String		CURRENT_FILE_NAME_FIELD_LABEL_NAME	= "Selection:";
	private		String		FAVORITES_FIELD_LABEL_NAME		= "Favorites:";
	private		String		Mi_FILE_FILTER_LABEL_NAME		= "File filter: ";
	private		String		Mi_FILE_FILTER_SAVE_AS_TYPE_LABEL_NAME	= "Save as type:";
	private		String		Mi_NUMBER_OF_FILES_LABEL		= " Files";
	private		String		Mi_SIZE_OF_FILES_LABEL			= " Bytes";
	private		String		Mi_NEWEST_FILE_LABEL			= "Newest: ";
	private		String		Mi_OLDEST_FILE_LABEL			= "Oldest: ";
	private		String		Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME	= "MiFileChooserFileNameTextField";
	public static final int		Mi_ACTIVATED_FILENAME_ACTION	= MiActionManager.registerAction(
										"ActivatedFileName");
	private		String		Mi_NEW_DIR_ROOT_NAME			= "new-directory";
	private		String		Mi_NEW_DIRECTORY_COMMAND_NAME		= "newDirectory";
	private		String		Mi_NEW_DIRECTORY_DISPLAY_NAME		= "New Directory";
	private		String		Mi_NEW_DIRECTORY_ICON_NAME		= "images/newFolder.gif";

	private		String		Mi_UP_A_DIRECTORY_COMMAND_NAME	= "UpDirectory";
	private		String		Mi_UP_A_DIRECTORY_DISPLAY_NAME	= "Up a directory";
	private		String		TOGGLE_VISIBLITY_OF_FAVORITES_COMMAND_NAME= "toggleVisibilityOfFavorites";

	private		String 		openPrompt 	= MiSystem.getProperty(
								Mi_CHOOSE_FILENAME_TO_OPEN_TO_MSG);
	private		String 		saveAsPrompt 	= MiSystem.getProperty(
								Mi_CHOOSE_FILENAME_TO_SAVE_TO_MSG);
	private		String 		defaultPrompt 	= "Files:";

	private		boolean		sortAlphabetically			= true;
	private		boolean		scrollToTypedEntry			= false;
	private		boolean		warnIfTypedEntryIsOneOfListedSelections	= false;
	private		boolean		restrictTypedEntryToListedSelections	= false;
	private		boolean		restrictTypedEntryToNonEmptyText	= true;
	private		boolean		initializedRootDirectoryDisplay		= false;

	private		boolean		searchForDirectoryNames;

	private		boolean		directoryNamesAreValidSelections;

	private		MiAttributes	promptAttributes;
	private		String		defaultSelection;
	private		int		defaultSelectionIndex			= -1;

	private		boolean		isBrowser;
	private		boolean		isActive;
	private		boolean		deSelectFilesWhenUserTypesInCurrentFileNameField = true;

	private		MiToolBar	toolBar;
	private		MiCommandManager commandManager;
	private		MiComboBox	rootDirectoryNameField;
	private		MiList		scrolledList;
	private		MiPart		fileNameLabel;
	private		MiLabel		filterFieldLabel;
	private		MiTextField	renameFileField;
	private		MiButton	favoritesPushButton;
	private		MiComboBox	fileNameFilterField;
	private		MiLabel		numberOfFilesDisplayedLabel;
	private		MiLabel		numberOfFilesTotalLabel;
	private		MiLabel		sizeOfFilesDisplayedLabel;
	private		MiLabel		sizeOfFilesTotalLabel;
	private		MiLabel		newestFileDisplayedLabel;
	private		MiLabel		oldestFileDisplayedLabel;

	private		MiNativeDialog	fileChooserDialog;
	private		MiNativeWindow	fileBrowserWindow;

	private		MiOkCancelHelpButtons	okCancelHelpButtons;

	private		MiiSystemIOManager	ioManager;
	private		MiSystemIOManagerFileFinder	fileFinder;
	private		MiSystemIOManagerFileFinder	directoryFinder;

	private		String		fileSearchSpec		= Mi_MATCH_ALL_FILES_FORMAT_FILTER;

	private		String		rootDirectory;

	private		MiComboBox	currentFileNameField;
	private		Strings		currentDirectoryNames;
	private		Strings		currentFilenames;
	private		String		currentDirectory;
	private		long		currentDirectoryNumberOfFiles;
	private		long		currentDirectorytotalSize;

	private		Strings		availableSaveFileFormatFilters;
	private		Strings		availableOpenFileFormatFilters;
	private		Strings		availableSaveFileFormatNames			= new Strings();
	private		Strings		availableSaveFileFormatExtensions		= new Strings();
	private		Strings		availableSaveFileFormatExtensionsIgnoreCases	= new Strings();
	private		Strings		availableSaveFileFormatFilterExtensions		= new Strings();
	private		Strings		availableOpenFileFormatNames			= new Strings();
	private		Strings		availableOpenFileFormatExtensions		= new Strings();
	private		Strings		availableOpenFileFormatExtensionsIgnoreCases	= new Strings();
	private		String		currentSaveFileFormat;
	private		String		requiredFileExtension;

	private		MiTreeList	directoryListingBox;
	private		MiScrolledBox	directoryListingScrolledBox;
	private		MiTable		fileListingBox;
	private		MiScrolledBox	fileListingScrolledBox;
	private		MiList		favoritesListBox;
	private		MiScrolledBox	favoritesListScrolledBox;

	private		Strings		directoriesVisited		= new Strings();
	private		Strings		rootDirectoriesVisited		= new Strings();
	private		int		currentDirectoryVisitedIndex		= -1;



	public				MiFileChooser(MiEditor parent)
		{
		this(parent, null);
		}
	public				MiFileChooser(MiEditor parent, String currentDirectory)
		{
		this(parent, currentDirectory, "File Chooser", true, false);
		}
	public				MiFileChooser(
						MiEditor parent, 
						String currentDirectory,
						String windowTitle, 
						boolean modal,
						boolean isBrowser)
		{
		this.isBrowser = isBrowser;

		ioManager = MiSystem.getIOManager();

		fileFinder = ioManager.getFileFinder();
		fileFinder.setFileTypesSearchSpec(MiSystemIOManagerFileFinder.Mi_MATCH_PLAIN_FILE_TYPES);

		directoryFinder = ioManager.getFileFinder();
		directoryFinder.setFileTypesSearchSpec(MiSystemIOManagerFileFinder.Mi_MATCH_DIRECTORY_FILE_TYPES);

		commandManager = new MiCommandManager();

		if (parent != null)
			{
			fileChooserDialog = new MiNativeDialog(parent, windowTitle, modal);
			fileBrowserWindow = fileChooserDialog;
			}
		else
			{
			fileBrowserWindow = new MiNativeWindow(windowTitle);
			}

		fileBrowserWindow.setViewportSizeLayout(
			new MiEditorViewportSizeIsOneToOneLayout(true));
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		fileBrowserWindow.setLayout(layout);

		buildBox(defaultPrompt);

		//if (fileChooserDialog != null)
			{
			okCancelHelpButtons = new MiOkCancelHelpButtons(
				fileChooserDialog,
				"OK", Mi_OK_COMMAND_NAME,
				"Cancel", Mi_CANCEL_COMMAND_NAME,
				"Help", Mi_HELP_COMMAND_NAME);
			appendPart(okCancelHelpButtons);
			okCancelHelpButtons.getButton(0).appendActionHandler(
				this, Mi_ACTIVATED_ACTION);
			okCancelHelpButtons.getButton(0).appendActionHandler(
				this, Mi_SELECTED_ACTION | Mi_REQUEST_ACTION_PHASE);
			}
		fileBrowserWindow.appendPart(this);
		fileBrowserWindow.setDefaultKeyboardFocus(currentFileNameField.getTextField());

		commandManager.setCommandSensitivity(Mi_PREVIOUS_COMMAND_NAME, false);
		commandManager.setCommandSensitivity(Mi_NEXT_COMMAND_NAME, false);
		commandManager.setCommandSensitivity(Mi_UP_A_DIRECTORY_COMMAND_NAME, false);
		if (currentDirectory == null)
			{
			currentDirectory = MiSystem.getProperty(MiSystem.Mi_CURRENT_DIRECTORY);
			}
		setRootDirectory(currentDirectory);
		setCurrentDirectory(currentDirectory);

		availableOpenFileFormatFilters = new Strings(Mi_MATCH_ALL_FILES_FORMAT_FILTER);
		availableSaveFileFormatFilters = new Strings(Mi_MATCH_ALL_FILES_FORMAT_FILTER);

		if (isBrowser)
			{
			favoritesPushButton.setVisible(false);
			favoritesListScrolledBox.setVisible(false);
			fileNameLabel.setVisible(false);
			currentFileNameField.setVisible(false);
			okCancelHelpButtons.setVisible(false);
			}
		else // isChooser
			{
			fileBrowserWindow.setDefaultCloseCommand(Mi_CANCEL_COMMAND_NAME);
			}
		}
	public		void		setAvailableSaveFileFormats(
						Strings availableSaveFileFormatNames, 
						Strings availableSaveFileFormatExtensions,
						Strings availableSaveFileFormatExtensionsIgnoreCases)
		{
		this.availableSaveFileFormatNames = availableSaveFileFormatNames;
		this.availableSaveFileFormatExtensions = availableSaveFileFormatExtensions;
		this.availableSaveFileFormatExtensionsIgnoreCases = availableSaveFileFormatExtensionsIgnoreCases;

		availableSaveFileFormatFilters = new Strings();
		for (int i = 0; i < availableSaveFileFormatNames.size(); ++i)
			{
			String extension = availableSaveFileFormatExtensions.get(i);
			extension = "*." + extension;
			this.availableSaveFileFormatFilterExtensions.add(extension);
			availableSaveFileFormatFilters.add(
				availableSaveFileFormatNames.get(i) + "(" + extension + ")");
			}
		availableSaveFileFormatFilters.add(Mi_MATCH_ALL_FILES_FORMAT_FILTER);

		if (availableSaveFileFormatNames.size() > 0)
			{
			currentSaveFileFormat = availableSaveFileFormatNames.get(0);
			}
		}
	public		Strings		getAvailableSaveFileFormatNames()
		{
		return(availableSaveFileFormatNames);
		}
	public		Strings		getAvailableSaveFileFormatExtensions()
		{
		return(availableSaveFileFormatExtensions);
		}
	public		String		getCurrentSaveFileFormat()
		{
		return(currentSaveFileFormat);
		}


	public		void		setAvailableOpenFileFormats(
						Strings availableOpenFileFormatNames, 
						Strings availableOpenFileFormatExtensions,
						Strings availableOpenFileFormatExtensionsIgnoreCases)
		{
		this.availableOpenFileFormatNames = availableOpenFileFormatNames;
		this.availableOpenFileFormatExtensions = new Strings(availableOpenFileFormatExtensions);
		this.availableOpenFileFormatExtensionsIgnoreCases = new Strings(availableOpenFileFormatExtensionsIgnoreCases);

		availableOpenFileFormatFilters = new Strings();
		for (int i = 0; i < availableOpenFileFormatNames.size(); ++i)
			{
			String extension = availableOpenFileFormatExtensions.get(i);
			if (extension.indexOf(',') != -1)
				{
				Strings tmp = new Strings();
				tmp.appendCommaDelimitedStrings(extension);
				for (int j = 0; j < tmp.size(); ++j)
					{
					tmp.setElementAt("*." + tmp.get(j), j);
					}
				extension = tmp.getCommaDelimitedStrings();
				}
			else
				{
				extension = "*." + extension;
				}

			this.availableOpenFileFormatExtensions.setElementAt(extension, i);

			availableOpenFileFormatFilters.add(
				availableOpenFileFormatNames.get(i) 
				+ "(" + availableOpenFileFormatExtensions.get(i) + ")");
			}
		availableOpenFileFormatFilters.add(Mi_MATCH_ALL_FILES_FORMAT_FILTER);
		}
	public		Strings		getAvailableOpenFileFormatNames()
		{
		return(availableOpenFileFormatNames);
		}
	public		Strings		getAvailableOpenFileFormatExtensions()
		{
		return(availableOpenFileFormatExtensions);
		}


	public		void		setRequiredFileExtension(String extension)
		{
		requiredFileExtension = extension;
		}
	public		String		getRequiredFileExtension()
		{
		return(requiredFileExtension);
		}
	public		String		getSelectedFilename()
		{
		String filename = currentFileNameField.getValue();
		if (filename != null)
			{
			if (filename.indexOf(File.separator) == -1)
				filename = currentDirectory + File.separator + filename;

			if ((requiredFileExtension != null) 
				&& (!filename.endsWith("." + requiredFileExtension)))
				{
				filename += "." + requiredFileExtension;
				}
			}
		return(filename);
		}
	public		void		setFilenameFilter(String filter)
		{
		if (Utility.isEmptyOrNull(filter))
			{
			filter = Mi_MATCH_ALL_FILES_FORMAT_FILTER;
			}
		if ((filter != null) && (filter.equals(fileSearchSpec)))
			{
			return;
			}

		fileSearchSpec = filter;
		if (Mi_MATCH_ALL_FILES_FORMAT_FILTER.equals(fileSearchSpec))
			{
			fileFinder.setFileNamesSearchSpec(
				MiSystemIOManagerFileFinder.Mi_MATCH_ALL_FILE_NAMES);
			}
		else
			{
			int index = availableOpenFileFormatFilters.indexOf(fileSearchSpec);
			if (index != -1)
				{
				fileFinder.setFileNamesSearchSpec(availableOpenFileFormatExtensions.get(index));
				fileFinder.setFileNamesSearchSpecIgnoreCase(
					Utility.toBoolean(availableOpenFileFormatExtensionsIgnoreCases.get(index)));
				}
			else if ((index = availableSaveFileFormatFilters.indexOf(fileSearchSpec)) != -1)
				{
				fileFinder.setFileNamesSearchSpec(availableSaveFileFormatFilterExtensions.get(index));
				fileFinder.setFileNamesSearchSpecIgnoreCase(
					Utility.toBoolean(availableOpenFileFormatExtensionsIgnoreCases.get(index)));
				}
			else
				{
				fileFinder.setFileNamesSearchSpec(fileSearchSpec);
				}
			}
		fileNameFilterField.setValue(filter);
		if (fileNameFilterField.getList().getIndexOfItem(filter) == -1)
			{
			fileNameFilterField.getList().appendItem(filter);
			}
		if (isActive)
			{
			// Refresh screen
			setFileListingBoxContents(currentDirectory);
			}
		}
	public		void		setCurrentDirectory(String directoryName)
		{
		setCurrentDirectory(directoryName, true);
		}
	public		void		setCurrentDirectory(String directoryName,
						boolean updateCurrentDirectoryVisitedList)
		{
		if ((currentDirectory != null) && (currentDirectory.equals(directoryName)))
			return;
		if (currentDirectory == directoryName)
			return;

		if (ioManager.isFile(directoryName))
			{
			setCurrentDirectory(currentDirectory);
			return;
			}

		currentDirectory = directoryName;

//		renameFileField.setSensitive(true);
		commandManager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, false);

		renameFileField.setResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, currentDirectory);
		String dirName = Utility.extractFilenameFromFilenamePath(currentDirectory);
		renameFileField.setValue(dirName);

		if (updateCurrentDirectoryVisitedList)
			{
			++currentDirectoryVisitedIndex;
			while (directoriesVisited.size() > currentDirectoryVisitedIndex)
				directoriesVisited.removeLastElement();
			directoriesVisited.addElement(currentDirectory);
			rootDirectoriesVisited.addElement(rootDirectory);

			updatePreviousAndNextCommandAvailability();
			}

		if (directoryListingBox.getItemWithTag(currentDirectory) != null)
			{
			directoryListingBox.selectItemWithTag(currentDirectory);
			}
		if (isActive)
			{
			setFileListingBoxContents(currentDirectory);
			}
		}
	public		void		setDirectoryNamesAreValidSelections(boolean flag)
		{
		directoryNamesAreValidSelections = flag;
		}
	public		boolean		getDirectoryNamesAreValidSelections()
		{
		return(directoryNamesAreValidSelections);
		}

	public		void		setRootDirectory(String directoryName)
		{
		if ((rootDirectory != null) && (rootDirectory.equals(directoryName)))
			return;
		if (rootDirectory == directoryName)
			return;

		if (ioManager.isFile(directoryName))
			{
			setRootDirectory(rootDirectory);
			return;
			}
		rootDirectory= directoryName;
		rootDirectoryNameField.setValue(directoryName);
		if (rootDirectoryNameField.getList().getIndexOfItem(directoryName) == -1)
			{
			((MiList )rootDirectoryNameField.getList()).insertItem(directoryName, 0);
			}
		if (isActive)
			{
			// Refresh screen
			setDirectoryListingBoxContents(rootDirectory);
			if (directoryListingBox.getItemWithTag(currentDirectory) != null)
				{
				directoryListingBox.selectItemWithTag(currentDirectory);
				}
			else
				{
				currentDirectory = rootDirectory;
				directoryListingBox.selectItemWithTag(rootDirectory);
				setFileListingBoxContents(rootDirectory);
				}
			}
		commandManager.setCommandSensitivity(Mi_UP_A_DIRECTORY_COMMAND_NAME, 
			getDirectoryAbove(directoryName) != null);
		}
	protected	String		getDirectoryAbove(String directoryName)
		{		
		int index = directoryName.lastIndexOf(File.separator);
		if (index == -1)
			{
			index = directoryName.lastIndexOf('/');
			}
		if (index != -1)
			{
			return(directoryName.substring(0, index));
			}
		return(null);
		//return(new File(directoryName).getParent());
		}
	public 		void		setVisible(boolean flag)
		{
		if (isBrowser)
			{
			if (flag)
				{
				isActive = true;
				initialize();
				fileBrowserWindow.setVisible(true);
				}
			else
				{
				isActive = false;
				fileBrowserWindow.setVisible(false);
				}
			}
		}
	public 		String		popupAndWaitForClose()
		{
		if (fileChooserDialog != null)
			{
			isActive = true;
			initialize();
			fileChooserDialog.setVisible(true);
			String button = fileChooserDialog.popupAndWaitForClose();
			isActive = false;
			if ((button != null) && (!button.equals(Mi_OK_COMMAND_NAME)))
				{
				return(null);
				}
			}
		String filename = getSelectedFilename();
		if (currentFileNameField.getList().getIndexOfItem(filename) == -1)
			((MiList )currentFileNameField.getList()).insertItem(filename, 0);

		return(filename);
		}
	public		String		getFileForOpening()
		{
		setDialogTitle(openPrompt);

		filterFieldLabel.setValue(Mi_FILE_FILTER_LABEL_NAME);

		fileNameFilterField.getList().setContents(availableOpenFileFormatFilters);
		fileNameFilterField.setValue(availableOpenFileFormatFilters.get(0));

		// ---------------------------------------------------------------
		// Automatically filter out the user's names that are not in the list
		// of possible selections.
		// ---------------------------------------------------------------
		setAutoRestrictingTypedEntryToListedSelections(true);

		setWarnIfTypedEntryIsOneOfListedSelections(false);

		// ---------------------------------------------------------------
		// Post the dialog and hang out until they are done.
		// ---------------------------------------------------------------
		String name = popupAndWaitForClose();

		// ---------------------------------------------------------------
		// The user canceled the dialog, return
		// ---------------------------------------------------------------
		if (name != null)
			name = name.trim();

		return(name);
		}
					/**------------------------------------------------------
					 * Get the name of the file to be chosen by the user. This 
					 * method displays a dialog box to the user which lists 
					 * the names currently in use and prompts them to enter a 
					 * new one.
					 * @return			the name chosen by the user
					 *------------------------------------------------------*/
	public		String		getFilenameForSavingTo()
		{
		setDialogTitle(saveAsPrompt);

		fileNameFilterField.getList().setContents(availableSaveFileFormatFilters);
		fileNameFilterField.setValue(availableSaveFileFormatFilters.get(0));

		filterFieldLabel.setValue(Mi_FILE_FILTER_SAVE_AS_TYPE_LABEL_NAME);
		//setPrompt(saveAsPrompt);

		// ---------------------------------------------------------------
		// Automatically filter out the user's names that are not in the list
		// of possible selections.
		// ---------------------------------------------------------------
		setAutoRestrictingTypedEntryToListedSelections(false);

		setWarnIfTypedEntryIsOneOfListedSelections(true);

		// ---------------------------------------------------------------
		// Post the dialog and hang out until they are done.
		// ---------------------------------------------------------------
		String result = popupAndWaitForClose();

		int index = availableSaveFileFormatFilters.indexOf(fileNameFilterField.getValue());
		if ((index == -1) || (index >= availableSaveFileFormatNames.size()))
			{
			currentSaveFileFormat = null;
			}
		else
			{
			currentSaveFileFormat = availableSaveFileFormatNames.get(index);
			}

		return(result);
		}
	// -----------------------------------------------------------------------
	//	Fields 
	// -----------------------------------------------------------------------
	public		void		setDialogTitle(String title)
		{
		fileBrowserWindow.setTitle(title);
		}
	public		String		getDialogTitle()
		{
		return(fileBrowserWindow.getTitle());
		}
	public		void		setFileNameLabel(MiPart fileNameLabel)
		{
		this.fileNameLabel.replaceSelf(fileNameLabel);
		this.fileNameLabel = fileNameLabel;
		}
	public		MiPart		getFileNameLabel()
		{
		return(fileNameLabel);
		}
	public		MiWidget	getEntryField()
		{
		return(currentFileNameField);
		}
	public		MiOkCancelHelpButtons	getOkCancelHelpButtons()
		{
		return(okCancelHelpButtons);
		}
	public		MiNativeWindow	getWindow()
		{
		return(fileBrowserWindow);
		}
	// -----------------------------------------------------------------------
	//	Control 
	// -----------------------------------------------------------------------
	protected	void		initialize()
		{
		if (!initializedRootDirectoryDisplay)
			{
			// FIX: Someday, rebuild whole directory with *same* open directories, if not deleted,
			// and show any new ones... For now, just update the directory list once per run.
			setDirectoryListingBoxContents(rootDirectory);
			if (directoryListingBox.getItemWithTag(currentDirectory) != null)
				directoryListingBox.selectItemWithTag(currentDirectory);
			
			initializedRootDirectoryDisplay = true;
			}
		setFileListingBoxContents(currentDirectory);

		currentFileNameField.setValue(null);

		fileListingBox.getSelectionManager().deSelectAll();

		if ((restrictTypedEntryToListedSelections)
			|| (restrictTypedEntryToNonEmptyText))
			{
			if (okCancelHelpButtons != null)
				okCancelHelpButtons.getButton(0).setSensitive(false);
			}
		currentFileNameField.setValue(defaultSelection);
		if (defaultSelection != null)
			{
			//scrolledList.selectItem(defaultSelection);
			}
		else if (defaultSelectionIndex != -1)
			{
			//scrolledList.selectItem(defaultSelectionIndex);
			}
		}
	public		void		setAutoSortingAlphabetically(boolean flag)
		{
		sortAlphabetically = flag;
		}
	public		boolean		isAutoSortingAlphabetically()
		{
		return(sortAlphabetically);
		}

	public		void		setAutoScrollingToTypedEntry(boolean flag)
		{
		scrollToTypedEntry = flag;
		}
	public		boolean		isAutoScrollingToTypedEntry()
		{
		return(scrollToTypedEntry);
		}
	public		void		setAutoRestrictingTypedEntryToListedSelections(boolean flag)
		{
		restrictTypedEntryToListedSelections = flag;
		}
	public		boolean		isAutoRestrictingTypedEntryToListedSelections()
		{
		return(restrictTypedEntryToListedSelections);
		}
	public		void		setAutoRestrictingTypedEntryToNonEmptyText(boolean flag)
		{
		restrictTypedEntryToNonEmptyText = flag;
		}
	public		boolean		isAutoRestrictingTypedEntryToNonEmptyText()
		{
		return(restrictTypedEntryToNonEmptyText);
		}
	public		void		setWarnIfTypedEntryIsOneOfListedSelections(boolean flag)
		{
		warnIfTypedEntryIsOneOfListedSelections = flag;
		}
	public		boolean		getWarnIfTypedEntryIsOneOfListedSelections()
		{
		return(warnIfTypedEntryIsOneOfListedSelections);
		}
	// -----------------------------------------------------------------------
	//	Internal functionality 
	// -----------------------------------------------------------------------
	public		void		processCommand(String command)
		{
		if (command.equals(Mi_NEXT_COMMAND_NAME))
			{
			if ((directoriesVisited.size() > 0) 
				&& (currentDirectoryVisitedIndex < directoriesVisited.size() - 1))
				{
				++currentDirectoryVisitedIndex;
				setRootDirectory(rootDirectoriesVisited.elementAt(currentDirectoryVisitedIndex));
				setCurrentDirectory(
					directoriesVisited.elementAt(currentDirectoryVisitedIndex) , false);
				}
			updatePreviousAndNextCommandAvailability();
			}
		else if (command.equals(Mi_PREVIOUS_COMMAND_NAME))
			{
			if ((directoriesVisited.size() > 0) && (currentDirectoryVisitedIndex > 0))
				{
				--currentDirectoryVisitedIndex;
				setRootDirectory(
					rootDirectoriesVisited.elementAt(currentDirectoryVisitedIndex));
				setCurrentDirectory(
					directoriesVisited.elementAt(currentDirectoryVisitedIndex), false);
				}
			updatePreviousAndNextCommandAvailability();
			}
		else if (command.equals(Mi_UP_A_DIRECTORY_COMMAND_NAME))
			{
			String above = getDirectoryAbove(rootDirectory);
			if (above != null)
				setRootDirectory(above);
			}
		else if (command.equals(TOGGLE_VISIBLITY_OF_FAVORITES_COMMAND_NAME))
			{
			favoritesListScrolledBox.setVisible(!favoritesListScrolledBox.isVisible());
			}
		else if (command.equals(Mi_NEW_DIRECTORY_COMMAND_NAME))
			{
			int iterations = 0;
			String newDirNameRoot = Mi_NEW_DIR_ROOT_NAME;
			while (ioManager.exists(currentDirectory + File.separator + newDirNameRoot))
				{
				newDirNameRoot = Mi_NEW_DIR_ROOT_NAME + iterations++;
				}
//MiDebug.println("currentDirectory=" + currentDirectory);
			String newDirectory = currentDirectory + File.separator + newDirNameRoot;
			newDirectory = Utility.replaceAll(newDirectory, File.separator + File.separator, File.separator);
			ioManager.mkdirs(newDirectory);
//MiDebug.println("directoryListingBox.getSelectedItemTag()=" + directoryListingBox.getSelectedItemTag());
			directoryListingBox.expand(directoryListingBox.getSelectedItemTag()); //4-3-2004
			setCurrentDirectory(newDirectory); // 3-29-2004 select new directory 
//MiDebug.println("now currentDirectory=" + currentDirectory);
			}
		else if (command.equals(Mi_DELETE_COMMAND_NAME))
			{
			int selectedRow = fileListingBox.getSelectionManager().getSelectedRow();
			MiTableCell selectedCell = fileListingBox.getCell(selectedRow, 0);
			String currentFilename = (String )selectedCell.getValue();
			String result = MiToolkit.postWarningDialog(
				fileBrowserWindow, 
				MiiMessages.Mi_ABOUT_TO_DELETE_TITLE_MSG, 
				Utility.sprintf(
					MiSystem.getProperty(Mi_DO_YOU_WANT_TO_DELETE_MSG), 
					currentFilename),
				false);
			if (result.equals(Mi_NOT_OK_COMMAND_NAME))
				return;

			ioManager.delete(currentDirectory + File.separator + currentFilename);
                        renameFileField.setResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, currentDirectory);
			String dirName = Utility.extractFilenameFromFilenamePath(currentDirectory);
			renameFileField.setValue(dirName);
			//renameFileField.setValue("");
			//renameFileField.setSensitive(false);
			fileListingBox.removeRow(selectedRow);
			commandManager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, false);
			currentFileNameField.setValue(null);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == directoryListingBox)
			{
			if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
				{
				String directoryName = (String )directoryListingBox.getSelectedItemTag();
				if ((currentDirectory == null) || (!currentDirectory.equals(directoryName)))
					{
					setCurrentDirectory(directoryName);	

					if (directoryNamesAreValidSelections)
						{
						boolean oldFlag = scrollToTypedEntry;
						// Don't scroll list around when just selecting an item in the list...
						scrollToTypedEntry = false;
						setCurrentNameFieldFromFileListBox(directoryName);
						scrollToTypedEntry = oldFlag;
						}
					}
				}
			else if (action.hasActionType(Mi_NODE_EXPANDED_ACTION))
				{
				String parentDirectoryToExpand = (String )action.getActionSystemInfo();
				Strings names = getDirectoryNames(parentDirectoryToExpand);
				directoryListingBox.removeItemWithTagsChildren(parentDirectoryToExpand);
				names.sortAlphabetically();
				for (int i = 0; i < names.size(); ++i)
					{
					String subDirectory = names.elementAt(i);
					MiText treeListItemName = new MiText(subDirectory);
					treeListItemName.setResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, 
						parentDirectoryToExpand + File.separator + subDirectory);
					treeListItemName.setIsDragAndDropSource(true);

//MiDebug.println("addItem with tag:  " + parentDirectoryToExpand + File.separator + subDirectory);
					directoryListingBox.addItem(
						treeListItemName,
						null,	// no image
						parentDirectoryToExpand + File.separator + subDirectory,
						parentDirectoryToExpand,
						true);
					}
				}
			}
		else if (action.hasActionType(Mi_ACTIVATED_FILENAME_ACTION))
			{
			String currentFilename = ((MiWidget )action.getActionSource()).getValue();
			currentFileNameField.setValue(currentFilename);
			if (validateOKdFileName())
				{
				if (fileChooserDialog != null)
					{
					fileChooserDialog.setVisible(false);
					}
				dispatchAction(Mi_ACTIVATED_ACTION);
				}
			}
		else if (action.getActionSource() == fileListingBox)
			{
			if (action.hasActionType(Mi_ACTIVATED_ACTION))
				{
				// Or execute the file...
				int selectedRow = fileListingBox.getSelectionManager().getSelectedRow();
				String currentFilename 
					= fileListingBox.getCell(selectedRow, 0).getValue();
				setCurrentNameFieldFromFileListBox(currentFilename);
				if (validateOKdFileName())
					{
					if (fileChooserDialog != null)
						{
						fileChooserDialog.setVisible(false);
						}
					dispatchAction(Mi_ACTIVATED_ACTION);
					}
				}
			else if (action.hasActionType(Mi_NO_ITEMS_SELECTED_ACTION))
				{
                                renameFileField.setResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, currentDirectory);
				String dirName = Utility.extractFilenameFromFilenamePath(currentDirectory);
				renameFileField.setValue(dirName);
				//renameFileField.setValue("");
				//renameFileField.setSensitive(false);
				commandManager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, false);
				}
			else if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
				{
				int selectedRow = fileListingBox.getSelectionManager().getSelectedRow();
				MiTableCell selectedCell = fileListingBox.getCell(selectedRow, 0);
				String currentFilename = (String )selectedCell.getValue();
                                renameFileField.setResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, currentFilename);
				renameFileField.setValue(currentFilename);
				// renameFileField.setSensitive(true);
				commandManager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, true);

				boolean oldFlag = scrollToTypedEntry;
				// Don't scroll list around when just selecting an item in the list...
				scrollToTypedEntry = false;
				setCurrentNameFieldFromFileListBox(currentFilename);
				scrollToTypedEntry = oldFlag;
				}
			}
		if (action.getActionSource() == favoritesListBox)
			{
			if (action.hasActionType(Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE))
				{
				MiDataTransferOperation transfer 
					= (MiDataTransferOperation )action.getActionSystemInfo();
				MiPart obj = (MiPart )transfer.getSource();
				String filename 
					= (String )obj.getResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME);
				if (filename == null)
					{
					action.veto();
					return(false);
					}
				MiBounds pickBounds = new MiBounds(transfer.getLookTargetPosition());
				pickBounds.setWidth(4);
				pickBounds.setHeight(4);
				MiTableCell cell = favoritesListBox.pickCell(pickBounds);
				// ---------------------------------------------------------------
				// Dropped somewhere between or after cells? If so, append the filename
				// ---------------------------------------------------------------
				if (cell == null)
					{
					favoritesListBox.appendItem(filename);
					return(false);
					}
				// ---------------------------------------------------------------
				// Get the palette row tag and the object that was dropped.
				// ---------------------------------------------------------------
				Object tag = cell.getTag();
				int index = favoritesListBox.getIndexOfTag(tag);
				favoritesListBox.insertItem(filename, index);
				// ---------------------------------------------------------------
				// Do not call doImport on target...
				// ---------------------------------------------------------------
				return(false);
				}
			else if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
				{
				String favoriteFileName = favoritesListBox.getValue();
				favoritesListBox.deSelectAll();
				if (ioManager.isFile(favoriteFileName))
					{
					boolean oldFlag = scrollToTypedEntry;
					// Don't scroll list around when just selecting an item in the list...
					scrollToTypedEntry = false;
					currentFileNameField.setValue(favoriteFileName);
					scrollToTypedEntry = oldFlag;
					}
				else
					{
					if (currentDirectoryNames.contains(getFileNameFromPath(favoriteFileName)))
						setCurrentDirectory(favoriteFileName);
					else
						setRootDirectory(favoriteFileName);
					}
				}
			}
		if ((okCancelHelpButtons != null) 
			&& (action.getActionSource() == okCancelHelpButtons.getButton(0)))
			{
			if (action.hasActionType(Mi_SELECTED_ACTION | Mi_REQUEST_ACTION_PHASE))
				{
				if (!validateOKdFileName())
					{
					action.veto();
					return(false);
					}
				// Trigger the operation...
				okCancelHelpButtons.getButton(0).dispatchAction(Mi_ACTIVATED_ACTION);
				// Don't leave the button in the selected state...
				action.veto();
				}
			return(true);
			}
		if ((action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION))
			&& (action.getActionSource() == renameFileField))
			{
			if (!isBrowser)
				okCancelHelpButtons.getPart(0).requestEnterKeyFocus();
			}
		if ((action.hasActionType(Mi_VALUE_CHANGED_ACTION))
			&& (action.getActionSource() == renameFileField))
			{
			String origName = Utility.extractFilenameFromFilenamePath(
				(String )renameFileField.getResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME));

			String newName = renameFileField.getValue();
			if (Utility.isEmptyOrNull(newName))
				{
				if (!fileNamesAreEqual(newName, origName))
					renameFileField.setValue(origName);
				return(true);
				}
			if ((fileNamesAreEqual(newName, origName))
				|| (fileNamesAreEqual(newName, Utility.extractFilenameFromFilenamePath(currentDirectory))))
				{
				return(true);
				}
			else
				{
				String file = currentDirectory + File.separator + origName;
				String toFile = currentDirectory + File.separator + newName;
				int selectedRow = fileListingBox.getSelectionManager().getSelectedRow();
				if (selectedRow == -1)
					{
					// Renaming directory...
					file = currentDirectory;
					toFile = Utility.extractPathFromFilenamePath(currentDirectory) + File.separator + newName;
					}
//MiDebug.println("newName = " + newName);
//MiDebug.println("toFile = " + toFile);
//MiDebug.println("currentDirectory = " + currentDirectory);
				if (ioManager.exists(toFile))
					{
					MiToolkit.postErrorDialog(
						fileBrowserWindow, 
						MiiMessages.Mi_NAME_ALREADY_IN_USE);
					}
				else if (!ioManager.renameTo(file, toFile))
					{
					MiToolkit.postErrorDialog(
						fileBrowserWindow, 
						"Name Change Failed");
					renameFileField.setValue(origName);
					}
				else
					{
//System.out.println("SUCCEEDED: " + toFile);
					renameFileField.setResource(
						Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, newName);
					if (selectedRow != -1)
						{
						MiTableCell selectedCell = fileListingBox.getCell(selectedRow, 0);
						selectedCell.setValue(newName);
						}
					else
						{
						selectedRow = directoryListingBox.getSelectionManager().getSelectedRow();
						String tag = (String )directoryListingBox.getTagItem(selectedRow);
//MiDebug.println("tag= " +tag);
//MiDebug.println("directoryListingBox.getSelectedItemTag()=" + directoryListingBox.getSelectedItemTag());
						directoryListingBox.setItemLabel(selectedRow, newName);
						int index = tag.lastIndexOf(File.separator);
/*
						if (index == -1)
							{
							index = tag.lastIndexOf('\\');
							}
						if (index == -1)
							{
							index = tag.lastIndexOf('/');
							}
*/
						if (index != -1)
							{
							tag = tag.substring(0, index + 1) + newName;
							}
						else
							{
							tag = newName;
							}
//MiDebug.println("setting tag= " +tag);
						directoryListingBox.setTagItem(selectedRow, tag);
//MiDebug.println("directoryListingBox.getTagItem(selectedRow)=" + directoryListingBox.getTagItem(selectedRow));
//MiDebug.println("directoryListingBox.getSelectedItemTag()=" + directoryListingBox.getSelectedItemTag());
						}
					setCurrentDirectory(toFile); // 3-29-2004
					}
				}
			return(true);
			}
		if ((action.hasActionType(Mi_VALUE_CHANGED_ACTION))
			&& (action.getActionSource() == fileNameFilterField.getTextField()))
			{
			String text = fileNameFilterField.getValue();
			if ((text != null) && (!text.equals(fileSearchSpec)))
				{
				if (Utility.isEmptyOrNull(text))
					{
					fileSearchSpec = Mi_MATCH_ALL_FILES;
					fileFinder.setFileNamesSearchSpec(
						MiSystemIOManagerFileFinder.Mi_MATCH_ALL_FILE_NAMES);
					fileNameFilterField.setValue(fileSearchSpec);
					}
				setFilenameFilter(text);
				}
			}
		if ((action.hasActionType(Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE))
			&& (action.getActionSource() == currentFileNameField.getTextField()))
			{
			if (restrictTypedEntryToListedSelections)
				{
				String text = currentFileNameField.getValue();
				if (text.startsWith(currentDirectory))
					text = text.substring(currentDirectory.length() + 1);

				if (!Utility.isEmptyOrNull(text))
					{
					boolean okSoFar = false;
					for (int i = 0; i < currentFilenames.size(); ++i)
						{
						// FIX: add caseless startsWith
						if (currentFilenames.elementAt(i).startsWith(text))
							{
							okSoFar = true;
							if (currentFilenames.elementAt(i).equals(text))
								{
								okCancelHelpButtons.getButton(0).setSensitive(true);
								okCancelHelpButtons.getButton(0).requestEnterKeyFocus();
								return(true);
								}
							}
						}
					if (!okSoFar)
						{
						action.veto();
						//okCancelHelpButtons.getButton(0).setSensitive(false);
						return(false);
						}
					}
				okCancelHelpButtons.getButton(0).setSensitive(false);
				}
			return(true);
			}
		if ((action.hasActionType(Mi_TEXT_CHANGE_ACTION))
			&& (action.getActionSource() == currentFileNameField.getTextField()))
			{
			if (deSelectFilesWhenUserTypesInCurrentFileNameField)
				{
				fileListingBox.getSelectionManager().deSelectAll();
				}
			String text = currentFileNameField.getValue();
			if (restrictTypedEntryToNonEmptyText)
				{
				if ((!Utility.isEmptyOrNull(text)) && (validateCurrentFileName()))
					{
					okCancelHelpButtons.getButton(0).setSensitive(true);
					okCancelHelpButtons.getButton(0).requestEnterKeyFocus();
					}
				else
					{
					okCancelHelpButtons.getButton(0).setSensitive(false);
					}
				}
			if (scrollToTypedEntry)
				{
				if (Utility.isEmptyOrNull(text))
					{
					fileListingBox.makeVisible(0, 0);
					}
				else
					{
					int numItems = fileListingBox.getNumberOfRows();
					for (int i = 0; i < numItems; ++i)
						{
						String tag = (String )fileListingBox.getCell(i, 0).getTag();
						if (tag.compareTo(text) <= 0)
							fileListingBox.makeVisible(i, 0);
						}
					}
				}
			currentFileNameField.setResource(
				Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, 
				currentDirectory + File.separator + text);
			return(true);
			}
		if ((action.hasActionType(Mi_VALUE_CHANGED_ACTION))
			&& (action.getActionSource() == rootDirectoryNameField.getTextField()))
			{
			String text = rootDirectoryNameField.getValue();
			setRootDirectory(text);
			return(true);
			}
		return(true);
		}
	protected	void		setCurrentNameFieldFromFileListBox(String name)
		{
		deSelectFilesWhenUserTypesInCurrentFileNameField = false;
		currentFileNameField.setValue(name);
		deSelectFilesWhenUserTypesInCurrentFileNameField = true;
		}
	public		Strings		getDirectoryNames(String parentDirectoryName)
		{
		if (!parentDirectoryName.endsWith(File.separator))
			parentDirectoryName += File.separator;

		try	{
			return(directoryFinder.find(parentDirectoryName));
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return(new Strings());
			}
		}
	public		Strings		getFilenames(String directoryName)
		{
		if (!directoryName.endsWith(File.separator))
			{
			directoryName += File.separator;
			}

		try	{
			Strings names = fileFinder.find(directoryName);

			currentDirectoryNumberOfFiles = fileFinder.getNumberOfFilesFound();
			currentDirectorytotalSize = fileFinder.getSizeOfFilesFound();
			return(names);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return(new Strings());
			}
		}

	protected	void		updatePreviousAndNextCommandAvailability()
		{
		if (currentDirectoryVisitedIndex < 1)
			commandManager.setCommandSensitivity(Mi_PREVIOUS_COMMAND_NAME, false);
		else
			commandManager.setCommandSensitivity(Mi_PREVIOUS_COMMAND_NAME, true);

		if (directoriesVisited.size() > currentDirectoryVisitedIndex + 1)
			commandManager.setCommandSensitivity(Mi_NEXT_COMMAND_NAME, true);
		else
			commandManager.setCommandSensitivity(Mi_NEXT_COMMAND_NAME, false);
		}
	protected	void		buildBox(String prompt)
		{
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(1);
		setLayout(layout);

		toolBar = new MiToolBar(commandManager);
		toolBar.setBorderLook(Mi_RAISED_BORDER_LOOK);

		toolBar.appendToolItem(	Mi_PREVIOUS_DISPLAY_NAME,
					new MiImage(Mi_PREVIOUS_ICON_NAME, true),
					this, 
					Mi_PREVIOUS_COMMAND_NAME);

		toolBar.appendToolItem(	Mi_NEXT_DISPLAY_NAME,
					new MiImage(Mi_NEXT_ICON_NAME, true),
					this, 
					Mi_NEXT_COMMAND_NAME);

		rootDirectoryNameField = new MiComboBox();
		rootDirectoryNameField.getTextField().setNumDisplayedColumns(40);
		rootDirectoryNameField.getTextField().getText().setNumDisplayedColumns(-1);
		rootDirectoryNameField.setRestrictingValuesToThoseInList(false);
		rootDirectoryNameField.getTextField().appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		
		toolBar.appendPart(rootDirectoryNameField);

		toolBar.appendToolItem(	Mi_UP_A_DIRECTORY_DISPLAY_NAME,
					new MiImage(Mi_UP_ICON_NAME, true),
					this, 
					Mi_UP_A_DIRECTORY_COMMAND_NAME);

		toolBar.setToolItemImageSizes(new MiSize(24, 24));
		((MiLayout )toolBar.getLayout()).setUniqueElementIndex(2);
		((MiLayout )toolBar.getLayout()).setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		appendPart(toolBar);
		

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementVSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(1);
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		appendPart(rowLayout);
			
		directoryListingBox = makeDirectoryListingBox();
		directoryListingBox.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		directoryListingBox.appendActionHandler(this, Mi_NODE_EXPANDED_ACTION);
		directoryListingBox.getSelectionManager().setMinimumNumberSelected(1);
		directoryListingScrolledBox = new MiScrolledBox(directoryListingBox);
		rowLayout.appendPart(directoryListingScrolledBox);

		MiToolBar fileToolBar = new MiToolBar(commandManager);
		fileToolBar.appendPart(new MiLabel("Rename:"));
		renameFileField = new MiTextField();
		renameFileField.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		renameFileField.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		renameFileField.setAcceptingEnterKeyFocus(true);
		((MiLayout )fileToolBar.getLayout()).setUniqueElementIndex(1);
		((MiLayout )fileToolBar.getLayout()).setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		fileToolBar.appendPart(renameFileField);
		fileToolBar.appendToolItem(
					Mi_DELETE_DISPLAY_NAME,
					new MiImage(Mi_DELETE_ICON_NAME, true),
					this, 
					Mi_DELETE_COMMAND_NAME);
		fileToolBar.appendToolItem(
					Mi_NEW_DIRECTORY_DISPLAY_NAME,
					new MiImage(Mi_NEW_DIRECTORY_ICON_NAME, true),
					this, 
					Mi_NEW_DIRECTORY_COMMAND_NAME);
		fileToolBar.processCommand(MiToolBar.Mi_HIDE_LABELS_COMMAND_NAME);
//		fileToolBar.setToolItemImageSizes(new MiSize(24, 24));

		fileListingBox = makeFileListingBox();
		fileListingBox.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		fileListingBox.appendActionHandler(this, Mi_NO_ITEMS_SELECTED_ACTION);
		fileListingBox.appendCommandHandler(this, Mi_DELETE_COMMAND_NAME, 
			new MiEvent(Mi_KEY_EVENT, Mi_DELETE_KEY, 0));
		fileListingBox.appendCommandHandler(this, Mi_NEW_DIRECTORY_COMMAND_NAME, 
			new MiEvent(Mi_KEY_EVENT, 'n', Mi_CONTROL_KEY_HELD_DOWN));
		fileListingBox.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		fileListingScrolledBox = new MiScrolledBox(fileListingBox);

		fileNameFilterField = new MiComboBox();
		fileNameFilterField.setRestrictingValuesToThoseInList(false);
		fileNameFilterField.getTextField().getText().setNumDisplayedColumns(-1);
		fileNameFilterField.getTextField().appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		fileNameFilterField.getList().getSortManager().setEnabled(false);
		fileNameFilterField.getList().appendItem(Mi_MATCH_ALL_FILES);

		MiRowLayout fileNameFilterFieldRowLayout = new MiRowLayout();
		filterFieldLabel = new MiLabel(Mi_FILE_FILTER_LABEL_NAME);
		fileNameFilterFieldRowLayout.appendPart(filterFieldLabel);
		fileNameFilterFieldRowLayout.appendPart(fileNameFilterField);
		fileNameFilterFieldRowLayout.setUniqueElementIndex(1);
		fileNameFilterFieldRowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);

		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementIndex(1);
		columnLayout.appendPart(fileToolBar);
		columnLayout.appendPart(fileListingScrolledBox);
		columnLayout.appendPart(fileNameFilterFieldRowLayout);

		rowLayout.appendPart(columnLayout);

		MiRowLayout selectionFieldRowLayout = new MiRowLayout();
		fileNameLabel = new MiLabel(CURRENT_FILE_NAME_FIELD_LABEL_NAME);

		currentFileNameField = new MiComboBox();
		currentFileNameField.setRestrictingValuesToThoseInList(false);
		currentFileNameField.getTextField().appendActionHandler(this, Mi_TEXT_CHANGE_ACTION 
					| Mi_COMMIT_ACTION_PHASE | Mi_REQUEST_ACTION_PHASE);
		currentFileNameField.getTextField().setIsDragAndDropSource(true);
		selectionFieldRowLayout.appendPart(fileNameLabel);
		selectionFieldRowLayout.appendPart(currentFileNameField);
		selectionFieldRowLayout.setUniqueElementIndex(1);
		selectionFieldRowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		appendPart(selectionFieldRowLayout);

		favoritesPushButton = new MiPushButton(FAVORITES_FIELD_LABEL_NAME);
		favoritesPushButton.appendCommandHandler(this, TOGGLE_VISIBLITY_OF_FAVORITES_COMMAND_NAME);
		((MiLabel )favoritesPushButton).setElementHJustification(Mi_LEFT_JUSTIFIED);
		appendPart(favoritesPushButton);

		favoritesListBox = new MiList();
		favoritesListBox.setMinimumNumberOfVisibleRows(6);
		favoritesListBox.setPreferredNumberOfVisibleRows(6);
		favoritesListBox.setMaximumNumberOfVisibleRows(6);
		favoritesListBox.appendActionHandler(this, Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);
		favoritesListBox.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		String[] dndImportFormat = new String[1];
		dndImportFormat[0] = Mi_STRING_FORMAT;
		favoritesListBox.setSupportedImportFormats(dndImportFormat);
		favoritesListBox.setIsDragAndDropTarget(true);
		favoritesListScrolledBox = new MiScrolledBox(favoritesListBox);
		appendPart(favoritesListScrolledBox);

favoritesListScrolledBox.setVisible(false);
favoritesPushButton.setVisible(false);
		}

	protected	MiTreeList	makeDirectoryListingBox()
		{
		MiTreeList treeList = new MiTreeList(24, false);
		MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));
		treeList.setDragAndDropBehavior(dndBehavior);

		return(treeList);
		}
	protected	MiTable		makeFileListingBox()
		{
		MiTable table = new MiTable();
		MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));
		table.setDragAndDropBehavior(dndBehavior);

		table.getSortManager().setColumnSortMethod(2, new MiNumericalSortMethod());
		table.getSortManager().setColumnSortMethod(3, new MiDateSortMethod());
		table.setInsetMargins(new MiMargins(4, 2, 4, 2));

		MiLabel label;
		label = new MiLabel("Name");
		label.setBorderLook(Mi_RAISED_BORDER_LOOK);
		table.addCell(MiTable.ROW_HEADER_NUMBER, 0, label);
		label = new MiLabel("Type");
		label.setBorderLook(Mi_RAISED_BORDER_LOOK);
		table.addCell(MiTable.ROW_HEADER_NUMBER, 1, label);
		label = new MiLabel("Size");
		label.setBorderLook(Mi_RAISED_BORDER_LOOK);
		table.addCell(MiTable.ROW_HEADER_NUMBER, 2, label);
		label = new MiLabel("Last Modified");
		label.setBorderLook(Mi_RAISED_BORDER_LOOK);
		table.addCell(MiTable.ROW_HEADER_NUMBER, 3, label);
		table.getRowHeaderBackground().setBorderLook(Mi_NONE);
		table.getRowHeaderBackground().setBackgroundColor(Mi_TRANSPARENT_COLOR);
		table.getRowFooterBackground().setBorderLook(Mi_NONE);
		table.getRowFooterBackground().setBackgroundColor(Mi_TRANSPARENT_COLOR);

		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
/*** REMOVED 7-13-2002
		numberOfFilesDisplayedLabel = new MiLabel("0" + Mi_NUMBER_OF_FILES_LABEL);
		numberOfFilesDisplayedLabel.setBorderLook(Mi_RAISED_BORDER_LOOK);

		numberOfFilesTotalLabel = new MiLabel("0" + Mi_NUMBER_OF_FILES_LABEL);
		numberOfFilesTotalLabel.setBorderLook(Mi_RAISED_BORDER_LOOK);

		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.appendPart(numberOfFilesDisplayedLabel);
		columnLayout.appendPart(numberOfFilesTotalLabel);
		table.addCell(MiTable.ROW_FOOTER_NUMBER, 0, columnLayout);
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		sizeOfFilesDisplayedLabel = new MiLabel("0" + Mi_SIZE_OF_FILES_LABEL);
		sizeOfFilesDisplayedLabel.setBorderLook(Mi_RAISED_BORDER_LOOK);

		sizeOfFilesTotalLabel = new MiLabel("0" + Mi_SIZE_OF_FILES_LABEL);
		sizeOfFilesTotalLabel.setBorderLook(Mi_RAISED_BORDER_LOOK);

		columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.appendPart(sizeOfFilesDisplayedLabel);
		columnLayout.appendPart(sizeOfFilesTotalLabel);
		table.addCell(MiTable.ROW_FOOTER_NUMBER, 1, columnLayout);
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		newestFileDisplayedLabel = new MiLabel(Mi_NEWEST_FILE_LABEL + "?");
		newestFileDisplayedLabel.setBorderLook(Mi_RAISED_BORDER_LOOK);

		oldestFileDisplayedLabel = new MiLabel(Mi_OLDEST_FILE_LABEL + "?");
		oldestFileDisplayedLabel.setBorderLook(Mi_RAISED_BORDER_LOOK);

		columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.appendPart(newestFileDisplayedLabel);
		columnLayout.appendPart(oldestFileDisplayedLabel);
		table.addCell(MiTable.ROW_FOOTER_NUMBER, 2, columnLayout);

******/
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		table.getTableWideDefaults().setHorizontalJustification(Mi_LEFT_JUSTIFIED);
		table.getTableWideDefaults().setHorizontalSizing(Mi_SAME_SIZE);
		table.getMadeColumnDefaults(0).setColumnHorizontalSizing(Mi_EXPAND_TO_FILL);
		table.getMadeColumnDefaults(1).setHorizontalJustification(Mi_RIGHT_JUSTIFIED);
		table.setPreferredSize(new MiSize(400,300));
		table.setTotalNumberOfColumns(4);
		table.setHasFixedTotalNumberOfColumns(true);
		//table.getTableHeaderAndFooterManager().setEnabled(false);
		table.getTableHeaderAndFooterManager().setMovableRows(false);
		table.getTableHeaderAndFooterManager().setResizableRows(false);
		table.getTableHeaderAndFooterManager().setMovableColumns(false);

		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		table.setMinimumNumberOfVisibleRows(6);
		table.setPreferredNumberOfVisibleRows(12);
		table.setMaximumNumberOfVisibleRows(12);


		table.setHasFixedTotalNumberOfColumns(true);
		table.setMinimumNumberOfVisibleColumns(4);
		table.setMaximumNumberOfVisibleColumns(4);
		table.getSectionMargins().setMargins(0);
		table.getTableWideDefaults().insetMargins.setMargins(8, 0, 8, 0);
		table.setAlleyVSpacing(0);
/*
		table.getTableWideDefaults().margins = new MiMargins(0);
		table.setContentsMargins(new MiMargins(0));
		table.setCellMargins(0);
*/


		MiRectangle rect = new MiRectangle();
		rect.setBorderLook(Mi_RAISED_BORDER_LOOK);

		table.getSelectionManager().setSelectionPolicy(
			MiTableSelectionManager.Mi_ROW_SELECTION_POLICY);
		return(table);
		}
	protected	void		setDirectoryListingBoxContents(String directory)
		{
		directoryListingBox.removeAllItems();

		MiText treeListItemName = new MiText(directory);
		treeListItemName.setResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, directory);
		treeListItemName.setIsDragAndDropSource(true);

		directoryListingBox.addItem(
			treeListItemName,
			null, // no image
			directory,
			null,
			true);
		currentDirectoryNames = getDirectoryNames(directory);
		for (int i = 0; i < currentDirectoryNames.size(); ++i)
			{
			String subDirectory = currentDirectoryNames.elementAt(i);
			treeListItemName = new MiText(subDirectory);
			treeListItemName.setResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, 
				directory + File.separator + subDirectory);
			treeListItemName.setIsDragAndDropSource(true);

			directoryListingBox.addItem(
				treeListItemName,
				null, // no image
				directory + File.separator + subDirectory,
				directory,
				true);

			if ((currentDirectory != null) 
				&& (currentDirectoryNames.elementAt(i).equals(currentDirectory)))
				{
				directoryListingBox.expand(currentDirectory);
				}
			}
		}
	protected	void		setFileListingBoxContents(String directory)
		{
		// ---------------------------------------------------------------
		// Clear out table as if we had no files...
		// ---------------------------------------------------------------
		fileListingBox.removeAllCells();
/*** REMOVED 7-13-2002
		numberOfFilesDisplayedLabel.setValue("0" + Mi_NUMBER_OF_FILES_LABEL);
		numberOfFilesTotalLabel.setValue("0" + Mi_NUMBER_OF_FILES_LABEL);
		sizeOfFilesDisplayedLabel.setValue("0" + Mi_SIZE_OF_FILES_LABEL);
		sizeOfFilesTotalLabel.setValue("0" + Mi_SIZE_OF_FILES_LABEL);
		newestFileDisplayedLabel.setValue(Mi_NEWEST_FILE_LABEL + "?");
		oldestFileDisplayedLabel.setValue(Mi_OLDEST_FILE_LABEL + "?");
****/

		//renameFileField.setResource(Mi_FILE_CHOOSER_FILENAME_RESOURCE_NAME, "");
		//renameFileField.setValue("");
		//renameFileField.setSensitive(false);
		commandManager.setCommandSensitivity(Mi_DELETE_COMMAND_NAME, false);

		if (directory == null)
			{
			currentFilenames = new Strings();
			return;
			}
		currentFilenames = getFilenames(directory);

if ((MiSystem.getProperty(MiSystem.Mi_LIMIT_TABLE_SIZES) != null)
	&& (Utility.toBoolean(MiSystem.getProperty(MiSystem.Mi_LIMIT_TABLE_SIZES))))
	{
	while (currentFilenames.size() > 50)
		currentFilenames.removeElementAt(currentFilenames.size() - 1);
	}
	

		Strings sizes = new Strings();
		long currentSize = 0;
		int maxSizeStringLength = 0;
		// Right justify sizes here, which have to be done here, not in table, 
		// because table cells only justfiy their graphics, not their Strings
		for (int i = 0; i < currentFilenames.size(); ++i)
			{
			String filename = currentFilenames.elementAt(i);
			String pathname = directory + File.separator + filename;
			long size = ioManager.sizeOf(pathname);

			currentSize += size;

			sizes.addElement("" + size);
			if (maxSizeStringLength < sizes.get(i).length())
				{
				maxSizeStringLength = sizes.get(i).length();
				}
			}
		for (int i = 0; i < sizes.size(); ++i)
			{
			String size = sizes.get(i);
			while (size.length() < maxSizeStringLength)
				{
				size = " " + size;
				}
			sizes.setElementAt(size, i);
			}
		
		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		Strings contents = new Strings();
		for (int i = 0; i < currentFilenames.size(); ++i)
			{
			String filename = currentFilenames.elementAt(i);
			String extension = "";
			int index = filename.lastIndexOf('.');
			if (index != -1)
				{
				extension = filename.substring(index + 1);
				}
			String pathname = directory + File.separator + filename;
			long size = ioManager.sizeOf(pathname);
			long date = ioManager.lastModified(pathname);
			
			currentSize += size;

			contents.addElement(filename);
			contents.addElement(extension);
			contents.addElement(sizes.elementAt(i));
			contents.addElement(dateFormat.format(new Date(date)));
			}
		fileListingBox.appendItems(contents);

		MiFont font = fileListingBox.getFont().setName("Courier");
		for (int i = 2; i < fileListingBox.getNumberOfCells(); i += 4)
			{
			MiTableCell cell = fileListingBox.getCell(i);
			cell.setAttributes(cell.getAttributes().setFont(font));
			}


/**** REMOVED 7-13-2002
		numberOfFilesDisplayedLabel.setValue(currentFilenames.size() + Mi_NUMBER_OF_FILES_LABEL);
		numberOfFilesTotalLabel.setValue(currentDirectoryNumberOfFiles + Mi_NUMBER_OF_FILES_LABEL);
		sizeOfFilesDisplayedLabel.setValue(currentSize + Mi_SIZE_OF_FILES_LABEL);
		sizeOfFilesTotalLabel.setValue(currentDirectorytotalSize + Mi_SIZE_OF_FILES_LABEL);
		newestFileDisplayedLabel.setValue(Mi_NEWEST_FILE_LABEL + "?");
		oldestFileDisplayedLabel.setValue(Mi_OLDEST_FILE_LABEL + "?");
****/
		}
	public		boolean		validateOKdFileName()
		{
		if (!validateCurrentFileName())
			return(false);

		if (warnIfTypedEntryIsOneOfListedSelections)
			{
			String currentFileName = currentFileNameField.getValue();
			if ((requiredFileExtension != null)
				&& (!currentFileName.endsWith("." + requiredFileExtension)))
				{
				currentFileName += "." + requiredFileExtension;
				}
			else
				{
				int index = availableSaveFileFormatFilters.indexOf(fileNameFilterField.getValue());
				if ((index != -1) && (index < availableSaveFileFormatNames.size()))
					{
					String extension = availableSaveFileFormatFilterExtensions.get(index);
					// remove "*."
					extension = extension.substring("*.".length());
					if ((currentFileName != null) && (!currentFileName.endsWith("." + extension)))
						{
						currentFileName += "." + extension;
						setCurrentNameFieldFromFileListBox(currentFileName);
						}
					}
				}

			if (ioManager.exists(currentDirectory + File.separator + currentFileName))
				{
				String result = MiToolkit.postWarningDialog(
					fileBrowserWindow, 
					Mi_NAME_ALREADY_IN_USE, 
					Utility.sprintf(
						MiSystem.getProperty(Mi_DO_YOU_WANT_TO_OVERWRITE_MSG), 
						currentFileName),
					true);

				// ---------------------------------------------------------------
				// The user has canceled the whole operation...
				// ---------------------------------------------------------------
				if (result.equals(Mi_CANCEL_COMMAND_NAME))
					{
					currentFileNameField.setValue(null);
					return(false);
					}

				// ---------------------------------------------------------------
				// The user does not want to overwrite the old file.
				// ---------------------------------------------------------------
				if (result.equals(Mi_NOT_OK_COMMAND_NAME))
					{
					// Clear out field so that OK button will get desensitized and 
					// lose grab handler which otherwise stays and all events go to OK button
					currentFileNameField.setValue(null);
					return(false);
					}
				}
			}
		return(true);
		}
	public		boolean		validateCurrentFileName()
		{
		if (restrictTypedEntryToListedSelections)
			{
			String text = currentFileNameField.getValue();
			if (!Utility.isEmptyOrNull(text))
				{
				if (text.startsWith(currentDirectory))
					text = text.substring(currentDirectory.length() + 1);

				if (currentFilenames.contains(text))
					{
					return(true);
					}
				if ((requiredFileExtension != null)
					&& (currentFilenames.contains(text + "." + requiredFileExtension)))
					{
					return(true);
					}
				}
			return(false);
			}
		return(true);
		}
	public		String		getDirectoryNameFromPath(String path)
		{
		int index = path.lastIndexOf(File.separator);
		if (index > 0)
			return(path.substring(0, index));
		return(path);
		}
	public		String		getFileNameFromPath(String path)
		{
		int index = path.lastIndexOf(File.separator);
		if (index >= 0)
			return(path.substring(index + 1));
		return(path);
		}
	public		boolean		fileNamesAreEqual(String one, String other)
		{
		if (one == other)
			return(true);

		if (one != null)
			{
			if (MiSystem.getFileNamesIgnoreCase())
				return(one.equalsIgnoreCase(other));
			return(one.equals(other));
			}
		return(other == null);
		}
	}
