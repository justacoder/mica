
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
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Strings; 
import java.io.*;
import java.awt.Frame; 


/**----------------------------------------------------------------------------------------------
 * Palette, ...
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiFileEditorWindow extends MiEditorWindow implements MiiSimpleAnimator
	{
	public static final int 	Mi_DOCUMENT_OPEN_ACTION
						= MiActionManager.registerAction("documentOpen");
	public static final int 	Mi_DOCUMENT_START_OPEN_ACTION
						= MiActionManager.registerAction("documentStartOpen");
	public static final int 	Mi_DOCUMENT_CANCELLED_OPEN_ACTION
						= MiActionManager.registerAction("documentCancelledOpen");
	public static final int 	Mi_DOCUMENT_FINISH_OPEN_ACTION
						= MiActionManager.registerAction("documentFinishOpen");
	public static final int 	Mi_DOCUMENT_FAILED_OPEN_ACTION
						= MiActionManager.registerAction("documentFailedOpen");
	public static final int 	Mi_DOCUMENT_CLOSE_ACTION
						= MiActionManager.registerAction("documentClose");
	public static final int 	Mi_DOCUMENT_NEW_ACTION
						= MiActionManager.registerAction("documentNew");
	public static final int 	Mi_DOCUMENT_SAVE_ACTION
						= MiActionManager.registerAction("documentSave");
	public static final int 	Mi_DOCUMENT_START_SAVE_ACTION
						= MiActionManager.registerAction("documentStartSave");
	public static final int 	Mi_DOCUMENT_FINISH_SAVE_ACTION
						= MiActionManager.registerAction("documentFinishSave");
	public static final int 	Mi_DOCUMENT_FAILED_SAVE_ACTION
						= MiActionManager.registerAction("documentFailedSave");
	public static final int 	Mi_DOCUMENT_FINISH_AUTO_BACKUP_ACTION
						= MiActionManager.registerAction("documentFinishAutoBackup");
	public static final int 	Mi_DOCUMENT_FAILED_AUTO_BACKUP_ACTION
						= MiActionManager.registerAction("documentFailedAutoBackup");
	public static final int 	Mi_DOCUMENT_DEFAULT_FILE_START_OPEN_ACTION
						= MiActionManager.registerAction("defaultDocumentStartOpen");
	public static final int 	Mi_DOCUMENT_DEFAULT_FILE_OPENED_ACTION
						= MiActionManager.registerAction("defaultDocumentOpened");
	public static final int 	Mi_DOCUMENT_CHANGED_OR_UNCHANGED_ACTION
						= MiActionManager.registerAction("documentChangedOrUnchanged");
	public static final int 	Mi_DOCUMENT_RESTORED_FROM_BACKUP_ACTION
						= MiActionManager.registerAction("restoredFromBackup");
	public static final int 	Mi_DOCUMENT_VIEW_CHANGED_ACTION
						= MiActionManager.registerAction("documentViewChanged");

	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	public static final String	Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_BORDER_TITLE 
						= "Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_BORDER_TITLE";
	public static final String	Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_MSG
						= "Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_MSG";
	public static final String	Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_YES
						= "Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_YES";
	public static final String	Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_NO
						= "Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_NO";


	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
		{
		new Pair( Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_BORDER_TITLE, 
					"Recover Lost Changes From Backup?"),
		new Pair( Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_MSG, 
					"A more recent backup was found for the file:\n"
					+ "\" %1 \".\n\n"
					+ "Do you want to initiate automatic recovery\n"
					+ "from the backup?"),
		new Pair( Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_YES, 
					"Yes"),
		new Pair( Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_NO,
					"No"),
		};

	// ---------------------------------------------------------------
	// Register the default values for this applications properties.
	// ---------------------------------------------------------------
	static	{
		MiSystem.setApplicationDefaultProperties(properties);
		}



	private		MiFileChooser	fileChooser;
	private		String		openFilename;
	private		String		openingFilename;
	private		String		filenameFilter;
	private		String		defaultFilename;
	private		String		defaultWindowTitle;
	private		String		openFileTitle;
	private		Strings		availableSaveFileFormatNames			= new Strings();
	private		Strings		availableSaveFileFormatExtensions		= new Strings();
	private		Strings		availableSaveFileFormatExtensionsIgnoreCases	= new Strings();
	private		Strings		availableOpenFileFormatNames			= new Strings();
	private		Strings		availableOpenFileFormatExtensions		= new Strings();
	private		Strings		availableOpenFileFormatExtensionsIgnoreCases	= new Strings();
	private		String		requiredFileExtension;
	private		boolean		changeOpenFileToSavedAsFile;
	private		boolean		openFilenameIsReadOnly;
	private		boolean		warnIfSaveToExistingFile;
	private		String		printFilenameFilter;
	private		String		requiredPrintFileExtension;
	private		String		printFormat;
	private		boolean		changingAutobackupFrequency;
	private		boolean		printUsingJDK			= true;
	private		boolean		useDefaultFilenameRootAsDefaultFile = true;
	private		boolean		supportsMultipleOpenDocuments;
	private		int		autobackupFrequency		= -1;
	private		String		backupFilenameTemplate		= "backup"; // <filename>.backup.<extension>
//	private		String		backupFilename;
	private		String		defaultDirectory;
	private		MiFileEditorWindowDocumentView 	defaultView;
	private		MiFileEditorWindowDocumentView 	currentView;
	private		boolean		autobackupEnabled 		= true;


					/**------------------------------------------------------
	 				 * Constructs a new MiEditorWindow, creates a new AWT Frame,
					 * and adds this MiEditorWindow, with the given title and
					 * size, to the AWT Frame.
					 * @param title		the window border text
					 * @param screenSize	the size of the window in pixels
					 *------------------------------------------------------*/
	public				MiFileEditorWindow(
						String title, 
						MiBounds screenSize,
						String requiredFileExtension, 
						String defaultFilename, 
						boolean changeOpenFileToSavedAsFile)
		{
		super(title, screenSize);
		initializeFileEditorWindow(requiredFileExtension, defaultFilename, title,
				changeOpenFileToSavedAsFile);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiFileEditorWindow. The caller must then
					 * add this to their Swing or AWT container. For example,
					 * if nativeComponentType == Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE:
					 * <p>
					 * MiFileEditorWindow gw = new MiFileEditorWindow(
					 *	MiUtility.getFrame(jComponent), null,
					 *	new MiBounds(0,0,400,400),
					 *	Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE);
					 * jComponent.add("Center", gw.getSwingComponent());
					 * <p>
					 * or, if adding to a JFrame:
					 * <p>
					 * jFrame.getContentPane().add("Center", gw.getSwingComponent());
					 * <p>
					 * And if nativeComponentType == Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE:
					 * <p>
					 * MiFileEditorWindow gw = new MiFileEditorWindow(
					 *	MiUtility.getFrame(awtContainer), null, 
					 *	new MiBounds(0,0,400,400),
					 *	Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);
					 * awtContainer.add("Center", gw.getAWTComponent());
					 * <p>
					 * @param frame		the parent frame
					 * @param title		the window border text
					 * @param screenSize	the size of the window in pixels
					 * @param nativeComponentType	the type of the component to
					 *			use: Mi_AWT_1_0_2_HEAVYWEIGHT_COMPONENT_TYPE
					 *			or Mi_AWT_1_1_HEAVYWEIGHT_COMPONENT_TYPE
					 *			or Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE
					 *			or Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE
					 *			or null (to use the default)
					 * @see			MiSystem#getDefaultJDKAPIComponentType
					 *------------------------------------------------------*/
	public				MiFileEditorWindow(
						Frame frame,
						String title,
						MiBounds screenSize,
						MiJDKAPIComponentType nativeComponentType,
						String requiredFileExtension, 
						String defaultFilename, 
						boolean changeOpenFileToSavedAsFile)
		{
		super(frame, title, screenSize, nativeComponentType);
		initializeFileEditorWindow(requiredFileExtension, defaultFilename, title,
				changeOpenFileToSavedAsFile);
		}

	private		void		initializeFileEditorWindow(
						String requiredFileExtension, 
						String defaultFilename, 
						String defaultWindowTitle,
						boolean changeOpenFileToSavedAsFile)
		{
		defaultView = new MiFileEditorWindowDocumentView();
		currentView = defaultView;

		this.requiredFileExtension 
			= MiSystem.getProperty(requiredFileExtension, requiredFileExtension);
		if (requiredFileExtension != null)
			filenameFilter = "*." + this.requiredFileExtension;
		this.defaultFilename = MiSystem.getProperty(defaultFilename, defaultFilename);
		this.defaultWindowTitle = MiSystem.getProperty(defaultWindowTitle, defaultWindowTitle);
		this.changeOpenFileToSavedAsFile = changeOpenFileToSavedAsFile;
		}
	public		String		getDefaultWindowTitle()
		{
		return(defaultWindowTitle);
		}
	public		void		setCurrentView(MiFileEditorWindowDocumentView view)
		{
		this.currentView = view;
		if (view.getContainer() == null)
			{
			defaultView.appendSubView(view);
			}
		openFilename = view.getOpenFilename();
		openFileTitle = view.getWindowBorderTitle();
		setOpenFilenameReadOnly(view.isOpenFilenameReadOnly());
//MiDebug.println("setCurrentView..................view.getWindowBorderTitle() = " + view.getWindowBorderTitle());
		updateTitle();
		String backupFilename = view.getBackupFilename();
		//setOpenFilename(view.getOpenFilename());
		setEditor(view.getEditor());
		setCommandSensitivity(Mi_SAVE_COMMAND_NAME, view.getEditor().isSavable() && (!view.isOpenFilenameReadOnly()));
		setCommandSensitivity(Mi_SAVE_AS_COMMAND_NAME, view.getEditor().isSavable());
		dispatchAction(Mi_DOCUMENT_VIEW_CHANGED_ACTION, view);
		}
/* Use getCurrentView().removeSelf() and then setCurrentView, otherwise null currentView causes problems.
	public		void		removeCurrentView()
		{
		MiFileEditorWindowDocumentView view = currentView;
		MiFileEditorWindowDocumentView container = view.getContainer();
		if (container != null)
			{
			container.removeSubView(view);
MiDebug.println("Number of subviews left = " + container.getNumberOfSubViews());
for (int i = 0; i < container.getNumberOfSubViews(); ++i)
MiDebug.println(i + "=" + container.getSubView(i));
			if (container.getNumberOfSubViews() > 0)
				{
				view = container.getSubView(0);
MiDebug.println("container.getSubView(0) = " + container.getSubView(0));
				setCurrentView(view);
				}
			else if (container != defaultView)
				{
MiDebug.println("defaultView = " + defaultView);
MiDebug.println("container = " + container);
				view = container;
				setCurrentView(view);
				}
			else
				{
				currentView = null;
				}
			}
		if (currentView == null)
			{
MiDebug.println("Warning - current view is null");
			}
		}
*/
	public		void		pushCurrentView(MiFileEditorWindowDocumentView view)
		{
		this.currentView.appendSubView(view);
		//this.currentView.setChangedSinceBackup(changedSinceBackup);
		//this.currentView.setChangedSinceSave(changedSinceSave);
		this.currentView.setBackupFilename(currentView.getBackupFilename());
		setCurrentView(view);
		}
	public		void		popCurrentView()
		{
		MiFileEditorWindowDocumentView view = currentView.getContainer();
		view.removeSubView(currentView);
		setCurrentView(view);
		}
	public		MiFileEditorWindowDocumentView	getCurrentView()
		{
		return(currentView);
		}
	public		String		getOpenFilename()
		{
		return(openFilename);
		}
	public		String		getOpeningFilename()
		{
		return(openingFilename);
		}
	public		void		setSupportsMultipleOpenDocuments(boolean flag)
		{
		supportsMultipleOpenDocuments = flag;
		if (flag)
			{
			setDefaultCloseCommand(null);
			appendActionHandler(this, Mi_WINDOW_DESTROY_ACTION | Mi_REQUEST_ACTION_PHASE);
			}
		else
			{
			setDefaultCloseCommand(Mi_QUIT_COMMAND_NAME);
			}
		}
	public		boolean		getSupportsMultipleOpenDocuments()
		{
		return(supportsMultipleOpenDocuments);
		}
	public		void		setOpenFilename(String filename)
		{
		openFilename = filename;
//MiDebug.printStackTrace("Set open filename: " + filename);
		currentView.setOpenFilename(filename);
		updateTitle();
		String backupFilename = calcBackupFilename(openFilename);
		currentView.setBackupFilename(backupFilename);
		}

	public		void		setOpenFilenameReadOnly(boolean flag)
		{
//MiDebug.printStackTrace(this + " setOpenFilenameReadOnly: " + flag);
		openFilenameIsReadOnly = flag;
		if (!flag)
			setCommandSensitivity(Mi_SAVE_COMMAND_NAME, false);
		updateTitle();
		}
	public		void		setRequiredFileExtension(String extension)
		{
		requiredFileExtension = extension;
		if (fileChooser != null)
			fileChooser.setRequiredFileExtension(requiredFileExtension);
		if (requiredFileExtension != null)
			filenameFilter = "*." + requiredFileExtension;
		else
			filenameFilter = "*";
		}
	public		String		getRequiredFileExtension()
		{
		return(requiredFileExtension);
		}
	public		void		setRequiredPrintFileExtension(String extension)
		{
		requiredPrintFileExtension = extension;
		if (requiredPrintFileExtension != null)
			printFilenameFilter = "*." + this.requiredPrintFileExtension;
		else
			printFilenameFilter = "*";
		}
	public		String		getRequiredPrintFileExtension()
		{
		return(requiredPrintFileExtension);
		}

	public		void		setAvailableOpenFileFormats(
						Strings availableOpenFileFormatNames, 
						Strings availableOpenFileFormatExtensions,
						Strings availableOpenFileFormatExtensionsIgnoreCases)

		{
		this.availableOpenFileFormatNames = availableOpenFileFormatNames;
		this.availableOpenFileFormatExtensions = availableOpenFileFormatExtensions;
		this.availableOpenFileFormatExtensionsIgnoreCases = availableOpenFileFormatExtensionsIgnoreCases;
		if (fileChooser != null)
			{
			fileChooser.setAvailableOpenFileFormats(
						availableOpenFileFormatNames, 
						availableOpenFileFormatExtensions,
						availableOpenFileFormatExtensionsIgnoreCases);
			}
		}
	public		Strings		getAvailableOpenFileFormatNames()
		{
		return(availableOpenFileFormatNames);
		}
	public		Strings		getAvailableOpenFileFormatExtensions()
		{
		return(availableOpenFileFormatExtensions);
		}
	public		Strings		getAvailableOpenFileFormatExtensionsIgnoreCases()
		{
		return(availableOpenFileFormatExtensionsIgnoreCases);
		}

	public		void		setAvailableSaveFileFormats(
						Strings availableSaveFileFormatNames, 
						Strings availableSaveFileFormatExtensions,
						Strings availableSaveFileFormatExtensionsIgnoreCases)

		{
		this.availableSaveFileFormatNames = availableSaveFileFormatNames;
		this.availableSaveFileFormatExtensions = availableSaveFileFormatExtensions;
		this.availableSaveFileFormatExtensionsIgnoreCases = availableSaveFileFormatExtensionsIgnoreCases;
		if (fileChooser != null)
			{
			fileChooser.setAvailableSaveFileFormats(
						availableSaveFileFormatNames, 
						availableSaveFileFormatExtensions,
						availableSaveFileFormatExtensionsIgnoreCases);
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
	public		Strings		getAvailableSaveFileFormatExtensionsIgnoreCases()
		{
		return(availableSaveFileFormatExtensionsIgnoreCases);
		}
	public		String		getCurrentSaveFileFormat()
		{
		if (fileChooser != null)
			{
			return(fileChooser.getCurrentSaveFileFormat());
			}
		return(null);
		}

	public		void		setOpenAndSaveAsDefaultDirectory(String directory)
		{
		if (fileChooser != null)
			{
			fileChooser.setRootDirectory(directory);
			fileChooser.setCurrentDirectory(directory);
			}
		defaultDirectory = directory;
		}
	public		String		getOpenAndSaveAsDefaultDirectory()
		{
		return(defaultDirectory);
		}

	public		void		setCommandSensitivity(String command, boolean flag)
		{
		if (command.equals(Mi_SAVE_COMMAND_NAME) 
			&& ((openFilenameIsReadOnly) || 
			//((openFilename != null) && (openFilename.equals(defaultFilename))))
			(currentView.isOpenFilenameReadOnly()))
			&& (flag))
			{
			return;
			}
		super.setCommandSensitivity(command, flag);
		}

	public		void		setOpenFileTitle(String title)
		{
		openFileTitle = title;
		updateTitle();
		}

	public		void		setPrintUsingJDK(boolean flag)
		{
		printUsingJDK = flag;
		}
	public		boolean		getPrintUsingJDK()
		{
		return(printUsingJDK);
		}
	public		void		setWillChangeOpenFileToSavedAsFile(boolean flag)
		{
		changeOpenFileToSavedAsFile = flag;
		}
	public		boolean		getWillChangeOpenFileToSavedAsFile()
		{
		return(changeOpenFileToSavedAsFile);
		}
	public		void		setDefaultFilenameRoot(String name)
		{
		defaultFilename = name;
		}
	public		void		setUseDefaultFilenameRootAsDefaultFile(boolean flag)
		{
		this.useDefaultFilenameRootAsDefaultFile = flag;
		}
	public		boolean		getUseDefaultFilenameRootAsDefaultFile()
		{
		return(useDefaultFilenameRootAsDefaultFile);
		}
	public		String		getDefaultFilenameRoot()
		{
		return(defaultFilename);
		}
	public		void		setWarnIfSaveToExistingFile(boolean flag)
		{
		warnIfSaveToExistingFile = flag;
		}
	public		boolean		getWarnIfSaveToExistingFile()
		{
		return(warnIfSaveToExistingFile);
		}
					/**------------------------------------------------------
	 				 * Sets the format of the print-to-file format when 
					 * getPrintUsingJDK() is false. Possible values are:
					 * "PDF", "PS".
					 * @param fmt		the format
					 * @see			#getPrintUsingJDK
					 *------------------------------------------------------*/
	public		void		setPrintFileFormat(String fmt)
		{
		printFormat = fmt;
		}
	public		String		getPrintFileFormat()
		{
		return(printFormat);
		}
					/**------------------------------------------------------
	 				 * Opens the default, no name, file that the user can
					 * edit in but which needs to be named before saving.
					 * @see			MiFileEditorWindow#defaultFileOpened
					 *------------------------------------------------------*/
	public		void		openDefaultFile()
		{
		String filename = null;
		if (useDefaultFilenameRootAsDefaultFile)
			{
			filename = defaultFilename;
			}
		else
			{
			filename = getNameOfFileWhichDoesNotExist(defaultFilename);
			}

		dispatchAction(Mi_DOCUMENT_DEFAULT_FILE_START_OPEN_ACTION, filename);
		prepareToLoadDefaultFile();
		openFile(filename);
		defaultFileOpened();
		dispatchAction(Mi_DOCUMENT_DEFAULT_FILE_OPENED_ACTION);
		}
	public		void		openNewFile(String filename)
		{
		if (supportsMultipleOpenDocuments)
			{
			dispatchAction(Mi_DOCUMENT_NEW_ACTION, filename);
			}
		openFile(filename);
		}

					/**------------------------------------------------------
	 				 * Processes the given command.
					 * @param command  	the command to execute
					 * @overrides		MiEditorWindow#processCommand
					 * @implements		MiiCommandHandler#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		if (command.equals(Mi_SAVE_COMMAND_NAME))
			{
			save();
			}
		else if (command.equals(Mi_OPEN_COMMAND_NAME))
			{
			if (!supportsMultipleOpenDocuments)
				{
				if (!verifyLossOfAnyChanges())
					{
					return;
					}
				}

			MiFileChooser chooser = currentView.getOpenFileChooser();
			if (chooser == null)
				{
				if (fileChooser == null)
					{
					fileChooser = new MiFileChooser(this, defaultDirectory);
					fileChooser.setFilenameFilter(filenameFilter);
					fileChooser.setRequiredFileExtension(requiredFileExtension);
					fileChooser.setAvailableOpenFileFormats(
						availableOpenFileFormatNames, 
						availableOpenFileFormatExtensions,
						availableOpenFileFormatExtensionsIgnoreCases);
					fileChooser.setAvailableSaveFileFormats(
						availableSaveFileFormatNames, 
						availableSaveFileFormatExtensions,
						availableSaveFileFormatExtensionsIgnoreCases);
					}
				chooser = fileChooser;
				}
			String name = chooser.getFileForOpening();
			if (!Utility.isEmptyOrNull(name))
				{
				openNewFile(name);
				}
			}
		else if (command.equals(Mi_SAVE_AS_COMMAND_NAME))
			{
			saveAs();
			}
		else if ((command.equals(Mi_NEW_COMMAND_NAME))
			|| (command.equals(Mi_CLOSE_COMMAND_NAME)))
			{
			if ((supportsMultipleOpenDocuments) && (command.equals(Mi_NEW_COMMAND_NAME)))
				{
				dispatchAction(Mi_DOCUMENT_NEW_ACTION);
				openDefaultFile();
				setOpenFilename(openFilename);
				setHasChanged(false);
				return;
				}
				
			if (!verifyLossOfAnyChanges())
				{
				return;
				}
			if (supportsMultipleOpenDocuments)
				{
				close();
				}
			else
				{
				close();

				openDefaultFile();
				setOpenFilename(openFilename);
				setHasChanged(false);
				}
			}
		else if (command.equalsIgnoreCase(Mi_QUIT_COMMAND_NAME))
			{
			if (supportsMultipleOpenDocuments)
				{
				if (!verifyLossOfAnyChangesAllViews())
					{
					return;
					}
				}
			else if (!verifyLossOfAnyChanges())
				{
				return;
				}

			super.processCommand(command);
			}
		else
			{
			super.processCommand(command);
			}
		}

	public		void		openTheDefaultFile()
		{
		openDefaultFile();
		setOpenFilename(openFilename);
		setHasChanged(false);
		}
	

	public		String		getNameOfFileWhichDoesNotExist(String defaultName)
		{
		File file = new File(defaultName); 

		String uniqueName = defaultName;

		// This whole method should probably be in MiiSystemIOManager
		if (MiSystem.isApplet())
			{
			return(uniqueName);
			}
		int numberOfTrys = 0;
		while (file.exists())
			{
			int index = defaultName.indexOf('.');
			if (index != -1)
				{
				uniqueName = defaultName.substring(0, index) 
					+ numberOfTrys + defaultName.substring(index);
				}
			else
				{
				uniqueName = defaultName + numberOfTrys;
				}

			file = new File(uniqueName);
			++numberOfTrys;
			}
		return(uniqueName);
		}
	public		boolean		save()
		{
		return(saveFile(openFilename));
		}
	public		boolean		saveFile(String filename)
		{
		return(saveFile(currentView, filename));
		}
	public		boolean		saveFile(MiFileEditorWindowDocumentView view, String filename)
		{
		OutputStream outputStream = null;
		try	{
			pushMouseAppearance(Mi_WAIT_CURSOR, "MiFileEditorWindow");

			filename = MiSystem.getProperty(filename, filename);
			if (warnIfSaveToExistingFile)
				{
				if (MiSystem.getIOManager().exists(filename))
					{
					String result = MiToolkit.postWarningDialog(
						this, 
						Mi_NAME_ALREADY_IN_USE, 
						Utility.sprintf(
							MiSystem.getProperty(Mi_DO_YOU_WANT_TO_OVERWRITE_MSG), 
							filename),
						true);

					// ---------------------------------------------------------------
					// The user has canceled the whole operation...
					// ---------------------------------------------------------------
					if (result.equals(Mi_CANCEL_COMMAND_NAME))
						{
						popMouseAppearance("MiFileEditorWindow");
						return(false);
						}

					// ---------------------------------------------------------------
					// The user does not want to overwrite the old file.
					// ---------------------------------------------------------------
					if (result.equals(Mi_NOT_OK_COMMAND_NAME))
						{
						popMouseAppearance("MiFileEditorWindow");
						return(false);
						}
					}
				}
			view.setSaveAsFilename(filename);
//MiDebug.println("Mi_DOCUMENT_START_SAVE_ACTION: " + filename);
			if (!dispatchActionRequest(Mi_DOCUMENT_START_SAVE_ACTION, view))
				{
				popMouseAppearance("MiFileEditorWindow");
				return(true);
				}
			outputStream = MiSystem.getIOManager().getOutputResourceAsStream(filename);

			boolean changedFilename = !openFilename.equals(filename);
			if ((changeOpenFileToSavedAsFile) && (changedFilename))
				{
				if (view == currentView)
					{
					openFilename = filename;
					setOpenFilename(openFilename);
					}
				else
					{
					view.setOpenFilename(filename);
					}
				}
			if (!save(view, outputStream, filename))
				{
				throw new Exception("save failed");
				}
			outputStream.close();

			if (!changedFilename)
				{
				String backupFilename = view.getBackupFilename();
				if (backupFilename != null)
					{
					setBackupFileAsNotRestored(backupFilename);
					}
				}

			if (view == currentView)
				{
				setHasChanged(false);
				}
			else
				{
				view.setChangedSinceBackup(false);
				view.setChangedSinceSave(false);
				}
			dispatchAction(Mi_DOCUMENT_FINISH_SAVE_ACTION, view);
			dispatchAction(Mi_DOCUMENT_SAVE_ACTION, view);
			popMouseAppearance("MiFileEditorWindow");
			}
		catch (Exception e)
			{
			MiDebug.printStackTrace(e);
			if (outputStream != null)
				{
				try	{
					outputStream.close();
					}
				catch (Throwable t)
					{
					}
				}
			MiToolkit.postErrorDialog(this, "Save to " + filename + " failed\n");
			dispatchAction(Mi_DOCUMENT_FAILED_SAVE_ACTION, view);
			popMouseAppearance("MiFileEditorWindow");
			return(false);
			}
		return(true);
		}
	public		boolean		openFile(String filename)
		{
//MiDebug.println(this + " openFile: " + filename);
		boolean loadedFromBackup = false;
		try	{
			pushMouseAppearance(Mi_WAIT_CURSOR, "MiFileEditorWindow");
			if (!supportsMultipleOpenDocuments)
				{
				close();
				}
			filename = MiSystem.getProperty(filename, filename);
//MiDebug.println(this + " openFile: " + filename);
			//String backupFilename = currentView.getBackupFilename();
//MiDebug.println(this + " backupFilename: " + backupFilename);
			//7-29-2003 backupfilename associated with view may not be correct for new filename if (backupFilename == null)
				//{
				String backupFilename = calcBackupFilename(filename);
				currentView.setBackupFilename(backupFilename);
				//}
//MiDebug.println(this + " backupFilename: " + backupFilename);

			openingFilename = filename;


			MiiSystemIOManager ioManager = MiSystem.getIOManager();
			if ((!backupFilename.equals(filename))
				&& (ioManager.exists(backupFilename))
				&& (ioManager.isFile(backupFilename))
				&& ((!ioManager.exists(filename))
				|| ((ioManager.lastModified(backupFilename)) 
					> ioManager.lastModified(filename))))

				{
				String answer = Mi_OK_COMMAND_NAME;
				if (ioManager.exists(filename))
					{
//MiDebug.println(this + "filename = " + filename);
//MiDebug.println(this + "backupFilename = " + backupFilename);
					answer = MiToolkit.postQueryDialog(this, 
						MiSystem.getProperty(
							Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_BORDER_TITLE), 
						Utility.sprintf(
							MiSystem.getProperty(
								Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_MSG), 
							filename),
						MiSystem.getProperty(Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_YES), 
						MiSystem.getProperty(Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_NO));
					}

				if (Mi_OK_COMMAND_NAME.equals(answer))
					{
					try	{
						if (!dispatchActionRequest(Mi_DOCUMENT_START_OPEN_ACTION, backupFilename))
							{
							dispatchAction(Mi_DOCUMENT_CANCELLED_OPEN_ACTION);
							popMouseAppearance("MiFileEditorWindow");
							return(true);
							}
						BufferedInputStream backupInputStream 
							= new BufferedInputStream(
								ioManager.getInputResourceAsStream(backupFilename));

						load(backupInputStream, backupFilename);
						loadedFromBackup = true;
						backupInputStream.close();
						}
					catch (Exception e)
						{
						e.printStackTrace();
						}
					}
				else
					{
					// If user indicates they do NOT want to restore, make
					// sure we do not ask them again.
					if (ioManager.exists(backupFilename))
						{
						setBackupFileAsNotRestored(backupFilename);
						}
					}
				}

			if (!loadedFromBackup)
				{
				if (!dispatchActionRequest(Mi_DOCUMENT_START_OPEN_ACTION, filename))
					{
MiDebug.println(this + " Load file cancelled by implementation: " + filename);
					dispatchAction(Mi_DOCUMENT_CANCELLED_OPEN_ACTION);
					popMouseAppearance("MiFileEditorWindow");
					return(true);
					}
				BufferedInputStream inputStream 
					= new BufferedInputStream(ioManager.getInputResourceAsStream(filename));
				load(inputStream, filename);
				inputStream.close();
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			MiToolkit.postErrorDialog(this, "Opening of " + filename + " failed\n");
			dispatchAction(Mi_DOCUMENT_FAILED_OPEN_ACTION);
			popMouseAppearance("MiFileEditorWindow");
			return(false);
			}
		openFilename = filename;
		setOpenFilename(openFilename);
		MiSystem.getTransactionManager().removeAllTransactions();
		setCommandSensitivity(Mi_SAVE_COMMAND_NAME, false);
		setHasChanged(loadedFromBackup);
		dispatchAction(Mi_DOCUMENT_FINISH_OPEN_ACTION);
		if (loadedFromBackup)
			{
			dispatchAction(Mi_DOCUMENT_RESTORED_FROM_BACKUP_ACTION, openFilename);
			}
		dispatchAction(Mi_DOCUMENT_OPEN_ACTION);
		popMouseAppearance("MiFileEditorWindow");
		return(true);
		}
	public		boolean		openAdditionalFile(String filename)
		{
		boolean loadedFromBackup = false;
		try	{
			pushMouseAppearance(Mi_WAIT_CURSOR, "MiFileEditorWindow");

			filename = MiSystem.getProperty(filename, filename);
			String backupFilename = calcBackupFilename(filename);
			currentView.setBackupFilename(backupFilename);

			openingFilename = filename;

			MiiSystemIOManager ioManager = MiSystem.getIOManager();
			if ((!backupFilename.equals(filename))
				&& (ioManager.exists(backupFilename))
				&& (ioManager.isFile(backupFilename))
				&& ((!ioManager.exists(filename))
				|| ((ioManager.lastModified(backupFilename)) 
					> ioManager.lastModified(filename))))
				{
				String answer = Mi_OK_COMMAND_NAME;
				if (ioManager.exists(filename))
					{
					answer = MiToolkit.postQueryDialog(this, 
						MiSystem.getProperty(
							Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_BORDER_TITLE), 
						Utility.sprintf(
							MiSystem.getProperty(
								Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_MSG), 
							filename),
						MiSystem.getProperty(Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_YES), 
						MiSystem.getProperty(Mi_RECOVER_LOST_CHANGES_FROM_BACKUP_WINDOW_NO));
					}

				if (Mi_OK_COMMAND_NAME.equals(answer))
					{
					try	{
						if (!dispatchActionRequest(Mi_DOCUMENT_START_OPEN_ACTION, backupFilename))
							{
							dispatchAction(Mi_DOCUMENT_CANCELLED_OPEN_ACTION);
							popMouseAppearance("MiFileEditorWindow");
							return(true);
							}
						BufferedInputStream backupInputStream 
							= new BufferedInputStream(
								ioManager.getInputResourceAsStream(backupFilename));

						load(backupInputStream, backupFilename);
						loadedFromBackup = true;
						backupInputStream.close();
						}
					catch (Exception e)
						{
						e.printStackTrace();
						}
					}
				else
					{
					// If user indicates they do NOT want to restore, make
					// sure we do not ask them again.
					if (ioManager.exists(backupFilename))
						{
						setBackupFileAsNotRestored(backupFilename);
						}
					}
				}

			if (!loadedFromBackup)
				{
				if (!dispatchActionRequest(Mi_DOCUMENT_START_OPEN_ACTION, filename))
					{
					dispatchAction(Mi_DOCUMENT_CANCELLED_OPEN_ACTION);
					popMouseAppearance("MiFileEditorWindow");
					return(true);
					}
					
				BufferedInputStream inputStream 
					= new BufferedInputStream(ioManager.getInputResourceAsStream(filename));
				load(inputStream, filename);
				inputStream.close();
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			MiToolkit.postErrorDialog(this, "Opening of " + filename + " failed\n");
			dispatchAction(Mi_DOCUMENT_FINISH_OPEN_ACTION);
			popMouseAppearance("MiFileEditorWindow");
			return(false);
			}
		openFilename = filename;
		setOpenFilename(openFilename);
		MiSystem.getTransactionManager().removeAllTransactions();
		setCommandSensitivity(Mi_SAVE_COMMAND_NAME, false);
		setHasChanged(loadedFromBackup);
		dispatchAction(Mi_DOCUMENT_FINISH_OPEN_ACTION);
		dispatchAction(Mi_DOCUMENT_OPEN_ACTION);
		popMouseAppearance("MiFileEditorWindow");
		return(true);
		}

	public		MiFileEditorWindowDocumentView		getChangedViewsSinceLastBackup()
		{
		MiFileEditorWindowDocumentView view = currentView;
		while (view.getContainer() != null)
			{	
			view = view.getContainer();
			}
		return(getChangedViewAndAllSubviewsSinceLastBackup(view));
		}

	protected	MiFileEditorWindowDocumentView		getChangedViewAndAllSubviewsSinceLastBackup(
									MiFileEditorWindowDocumentView view)
		{
		if ((view.isChangedSinceBackup()) && (view.getEditor().isSavable()))
			{
			return(view);
			}
		for (int i = 0; i < view.getNumberOfSubViews(); ++i)
			{
			if (getChangedViewAndAllSubviewsSinceLastBackup(view.getSubView(i)) != null)
				{
				return(view.getSubView(i));
				}
			}
		return(null);
		}

	public		MiFileEditorWindowDocumentView		getChangedViews()
		{
		MiFileEditorWindowDocumentView view = currentView;
		while (view.getContainer() != null)
			{	
			view = view.getContainer();
			}
		return(getChangedViewAndAllSubviews(view));
		}

	protected	MiFileEditorWindowDocumentView		getChangedViewAndAllSubviews(MiFileEditorWindowDocumentView view)
		{
//MiDebug.println("View: " + view);
//MiDebug.println("View: view.isChangedSinceSave()" + view.isChangedSinceSave());
//MiDebug.println("View: view.getEditor().isSavable()()" + view.getEditor().isSavable());
		if ((view.isChangedSinceSave()) && (view.getEditor().isSavable()))
			{
			return(view);
			}
		for (int i = 0; i < view.getNumberOfSubViews(); ++i)
			{
			if (getChangedViewAndAllSubviews(view.getSubView(i)) != null)
				{
				return(view.getSubView(i));
				}
			}
		return(null);
		}

	public		boolean		verifyLossOfAnyChangesAllViews()
		{
		MiFileEditorWindowDocumentView view = null;
		while ((view = getChangedViews()) != null)
			{
//MiDebug.println("Changed View: " + view);
			String filename = view.getOpenFilename();
			String result = MiToolkit.postSaveChangesAllViewsDialog(this, filename);
			if (result.equals(Mi_SAVE_COMMAND_NAME))
				{
				if ((filename.equals(defaultFilename)) || (view.isOpenFilenameReadOnly()))
					{
					if (!saveAs(view))
						{
						return(false);
						}
					}
				else
					{
					saveFile(view, filename);
					}
				view.setChangedSinceBackup(false);
				view.setChangedSinceSave(false);
				}
			else if (result.equals(Mi_NO_SAVE_ALL_COMMAND_NAME))
				{
				return(true);
				}
			else if (result.equals(Mi_SAVE_ALL_COMMAND_NAME))
				{
				do	{
					filename = view.getOpenFilename();
					if (filename.equals(defaultFilename))
						{
						if (!saveAs(view))
							{
							return(false);
							}
						}
					else
						{
						saveFile(view, filename);
						}
			
					view.setChangedSinceBackup(false);
					view.setChangedSinceSave(false);
					} while ((view = getChangedViews()) != null);

				return(true);
				}
			else if (result.equals(Mi_CANCEL_COMMAND_NAME))
				{
				return(false);
				}
			else	// NO, just lose changes and try next file
				{
				MiiSystemIOManager ioManager = MiSystem.getIOManager();
				// If user indicates they do NOT want to restore, make
				// sure we do not ask them again.
				String backupFilename = calcBackupFilename(filename);
				if (ioManager.exists(backupFilename))
					{
					setBackupFileAsNotRestored(backupFilename);
					}
				view.setChangedSinceBackup(false);
				view.setChangedSinceSave(false);
				}
			}
		return(true);
		}
	public		void		setBackupFileAsNotRestored(String backupFilename)
		{
		MiiSystemIOManager ioManager = MiSystem.getIOManager();
		if (ioManager.exists(backupFilename))
			{
			String notRestoredFilename = backupFilename + ".not-restored";
			ioManager.delete(notRestoredFilename);
			ioManager.renameTo(backupFilename, notRestoredFilename);
			}
		}
	public		boolean		verifyLossOfAnyChanges()
		{
		if ((openFilename != null) && (getHasChanged()))
			{
			String result = MiToolkit.postSaveChangesDialog(this, openFilename);
			if (result.equals(Mi_SAVE_COMMAND_NAME))
				{
				if ((openFilename.equals(defaultFilename))
					|| ((useDefaultFilenameRootAsDefaultFile)
						&& (openFilename.startsWith(defaultFilename)))
					|| (currentView.isOpenFilenameReadOnly()))
					{
					if (!saveAs())
						{
						return(false);
						}
					}
				else
					{
					saveFile(openFilename);
					}
				}
			else if (result.equals(Mi_CANCEL_COMMAND_NAME))
				{
				return(false);
				}
			else	// NO, just lose changes and exit
				{
				MiiSystemIOManager ioManager = MiSystem.getIOManager();
				// If user indicates they do NOT want to restore, make
				// sure we do not ask them again.
				String backupFilename = calcBackupFilename(openFilename);
				if (ioManager.exists(backupFilename))
					{
					String notRestoredFilename = backupFilename + ".not-restored";
					ioManager.delete(notRestoredFilename);
					ioManager.renameTo(backupFilename, notRestoredFilename);
					}
				}
			}
		return(true);
		}
	public		void		print()
		{
		if (!printUsingJDK)
			printAs();
		else
			printUsingJDK();
		}
	public		boolean		printFile(String filename)
		{
		try	{
			if (printUsingJDK)
				{
				printUsingJDK();
				}
			else
				{
				filename = MiSystem.getProperty(filename, filename);
				MiPrint printCommand = new MiPrint();
				if (printFormat.equalsIgnoreCase("PDF"))
					printCommand.setPrintDriver(new MiPDFDriver());
				else // if (printFormat.equalsIgnoreCase("PS"))
					printCommand.setPrintDriver(new MiPostScriptDriver());
				printCommand.setFilename(filename);
				printCommand.setTargetOfCommand(getEditor());
				printCommand.processCommand(null);
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			MiToolkit.postErrorDialog(this, "Print to " + filename + " failed\n");
			return(false);
			}
		return(true);
		}
	public		void		setAutomaticBackupFilenameTemplate(String backupFilenameTemplate)
		{
		this.backupFilenameTemplate = backupFilenameTemplate;
		}
	public		String		getAutomaticBackupFilenameTemplate()
		{
		return(backupFilenameTemplate);
		}
	public		void		setAutomaticBackupFilename(String backupFilename)
		{
		// this.backupFilename = backupFilename;
		currentView.setBackupFilename(backupFilename);
		}
	public		String		getAutomaticBackupFilename()
		{
		return(currentView.getBackupFilename());
		}
	public		void		setAutomaticBackupFrequency(int seconds)
		{
		if (autobackupFrequency > 0)
			{
			getAnimationManager().removeAnimator(this);
			}

		autobackupFrequency = seconds;
		changingAutobackupFrequency = true;

		if (seconds > 0)
			{
			getAnimationManager().addAnimator(this);
			}
		}
	public		int		getAutomaticBackupFrequency()
		{
		return(autobackupFrequency);
		}
	public		void		setAutomaticBackupEnabled(boolean flag)
		{
		autobackupEnabled = flag;
		}
	public		boolean		isAutomaticBackupEnabled()
		{
		return(autobackupEnabled);
		}
	public		long		animate()
		{
		if ((!changingAutobackupFrequency) && (autobackupEnabled))
			{
			backupAllViews();
			}
		changingAutobackupFrequency = false;
		return(autobackupFrequency * 1000);
		}
	public		void		backupAllViews()
		{
		MiFileEditorWindowDocumentView view = currentView;
//MiDebug.println(this + ".backupAllViews view = " + view);
		while (view.getContainer() != null)
			{	
			view = view.getContainer();
			}
//MiDebug.println("NOW backupAllViews view = " + view);

		boolean[] aBackupWasAttempted = new boolean[1];
		Strings namesOfFailedBackups = new Strings();
		boolean success = saveBackup(view, aBackupWasAttempted, namesOfFailedBackups);
		if (aBackupWasAttempted[0])
			{
//MiDebug.println("namesOfFailedBackups = " + namesOfFailedBackups);
			setCommandSensitivity(Mi_SAVE_COMMAND_NAME, true);
			currentView.setChangedSinceSave(true);
			currentView.setChangedSinceBackup(false);
			if (!success)
				{
				dispatchAction(Mi_DOCUMENT_FAILED_AUTO_BACKUP_ACTION, 
					namesOfFailedBackups.getCommaDelimitedStrings());
				}
			else
				{
				dispatchAction(Mi_DOCUMENT_FINISH_AUTO_BACKUP_ACTION);
				}
			}
		}
	protected	boolean		saveBackup(MiFileEditorWindowDocumentView view, boolean[] aBackupWasAttempted, Strings namesOfFailedBackups)
		{
		boolean success = true;
//MiDebug.println("Looking to see if we should backup View: " + view);
//MiDebug.println("view.isChangedSinceBackup() = " + view.isChangedSinceBackup());
//MiDebug.println("view.getEditor().isSavable() = " + view.getEditor().isSavable());
		if ((view.isChangedSinceBackup()) && (view.getEditor().isSavable()))
			{
			boolean orig_changeOpenFileToSavedAsFile = changeOpenFileToSavedAsFile;
			changeOpenFileToSavedAsFile = false;
			aBackupWasAttempted[0] = true;
//MiDebug.println("SAVING View: " + view);
			boolean origWarnFlag = getWarnIfSaveToExistingFile();
			setWarnIfSaveToExistingFile(false);
MiDebug.println("SAVING View to file: " + view.getBackupFilename());
			if (!saveFile(view, view.getBackupFilename()))
				{
				setWarnIfSaveToExistingFile(origWarnFlag);
				MiToolkit.postErrorDialog(this, 
					"Automatic save to " + view.getBackupFilename() + " failed\n");
				MiDebug.println("save to backup file \"" + view.getBackupFilename() + "\" failed");
				changeOpenFileToSavedAsFile = orig_changeOpenFileToSavedAsFile;
				view.setChangedSinceBackup(false);
				view.setChangedSinceSave(false);
				dispatchAction(Mi_DOCUMENT_FAILED_AUTO_BACKUP_ACTION, view);
				namesOfFailedBackups.add(view.getBackupFilename());
//MiDebug.println("namesOfFailedBackups = " + namesOfFailedBackups);
				return(false);
				}
			setWarnIfSaveToExistingFile(origWarnFlag);
			changeOpenFileToSavedAsFile = orig_changeOpenFileToSavedAsFile;
			// If there are multiple views in this 'perspective' this will NOT mark them as backed up!
			view.setChangedSinceBackup(false);
			view.setChangedSinceSave(true);
			dispatchAction(Mi_DOCUMENT_FINISH_AUTO_BACKUP_ACTION, view);
			}
		for (int i = 0; i < view.getNumberOfSubViews(); ++i)
			{
			if (!saveBackup(view.getSubView(i), aBackupWasAttempted, namesOfFailedBackups))
				{
				success = false;
				}
			}
		return(success);
		}
/*
	protected	void		saveBackup()
		{
		if ((changedSinceBackup) && (getHasChanged()))
			{
			boolean orig_changeOpenFileToSavedAsFile = changeOpenFileToSavedAsFile;
			changeOpenFileToSavedAsFile = false;
			if (!saveFile(null, backupFilename))
				{
				MiToolkit.postErrorDialog(this, 
					"Automatic save to " + backupFilename + " failed\n");
				MiDebug.println("save to backup file \"" + backupFilename + "\" failed");
				changeOpenFileToSavedAsFile = orig_changeOpenFileToSavedAsFile;
				setHasChanged(true);
				changedSinceBackup = false;
				return;
				}
			changeOpenFileToSavedAsFile = orig_changeOpenFileToSavedAsFile;
			setHasChanged(true);
			changedSinceBackup = false;
			dispatchAction(Mi_DOCUMENT_FINISH_AUTO_BACKUP_ACTION, backupFilename);
			}
		}
*/
	public		String		calcBackupFilename(String filename)
		{
		int index = filename.lastIndexOf('.');
		String backupFilename = filename + "." + backupFilenameTemplate;
		if (index != -1)
			{
			backupFilename = filename.substring(0, index) 
				+ "." + backupFilenameTemplate + "." + filename.substring(index + 1);
			}
		return(backupFilename);
		}
					/**------------------------------------------------------
	 				 * Closes the currently open file.
					 *------------------------------------------------------*/
	protected	void		close()
		{
		dispatchAction(Mi_DOCUMENT_CLOSE_ACTION);
		}
	// Return true if actually saved and not cancelled
	protected	boolean		saveAs(MiFileEditorWindowDocumentView view)
		{
		MiFileChooser chooser = view.getSaveAsFileChooser();
		if (chooser == null)
			{
			if (fileChooser == null)
				{
				fileChooser = new MiFileChooser(this, defaultDirectory);
				fileChooser.setFilenameFilter(filenameFilter);
				fileChooser.setRequiredFileExtension(requiredFileExtension);
				fileChooser.setAvailableOpenFileFormats(
						availableOpenFileFormatNames, 
						availableOpenFileFormatExtensions,
						availableOpenFileFormatExtensionsIgnoreCases);
				fileChooser.setAvailableSaveFileFormats(
						availableSaveFileFormatNames, 
						availableSaveFileFormatExtensions,
						availableSaveFileFormatExtensionsIgnoreCases);
				}
			chooser = fileChooser;
			}
		String name = chooser.getFilenameForSavingTo();
		if (!Utility.isEmptyOrNull(name))
			{
			saveFile(view, name);
			return(true);
			}
		return(false);
		}
	// Return true if actually saved and not cancelled
	protected	boolean		saveAs()
		{
		return(saveAs(currentView));
		}
	protected	void		printUsingJDK()
		{
		// if (MiSystem.getJDKVersion() < 1.2)
			{
			MiPrint printCommand = new MiPrint();
			printCommand.setPrintDriver(new MiJDKPrintDriver(getFrame()));
			printCommand.setFilename(null);
			printCommand.setTargetOfCommand(getEditor());
			printCommand.processCommand(null);
			}
		}
	protected	void		printAs()
		{
		if (fileChooser == null)
			{
			fileChooser = new MiFileChooser(this, defaultDirectory);
			fileChooser.setFilenameFilter(printFilenameFilter);
			fileChooser.setRequiredFileExtension(requiredPrintFileExtension);
			fileChooser.setAvailableOpenFileFormats(
						availableOpenFileFormatNames, 
						availableOpenFileFormatExtensions,
						availableOpenFileFormatExtensionsIgnoreCases);
			fileChooser.setAvailableSaveFileFormats(
						availableSaveFileFormatNames, 
						availableSaveFileFormatExtensions,
						availableSaveFileFormatExtensionsIgnoreCases);
			}
		String name = fileChooser.getFilenameForSavingTo();
		if (!Utility.isEmptyOrNull(name))
			printFile(name);
		}
	protected	void		updateTitle()
		{
//MiDebug.printStackTrace("updateTitle..................openFileTitle = " + openFileTitle);
		String readOnly = openFilenameIsReadOnly ? "<ReadOnly>" : "";
		if (openFileTitle != null)
			{
			setTitle(defaultWindowTitle + ": " + openFileTitle + readOnly);
			}
		else
			{
			setTitle(defaultWindowTitle + ": " + openFilename + readOnly);
			}
		}

	public		void		setHasChanged(boolean flag)
		{
		currentView.setChangedSinceBackup(flag);
		currentView.setChangedSinceSave(flag);
		setCommandSensitivity(Mi_SAVE_COMMAND_NAME, flag);
		dispatchAction(Mi_DOCUMENT_CHANGED_OR_UNCHANGED_ACTION, new Boolean(flag));
		}
	
					/**------------------------------------------------------
	 				 * Sets the editor that the menubar, statusbar and toolbar
					 * and (3rd button) background menu act on and report about.
					 * Needs work to support multiple simulatneous editors in 
					 * this one window. Note: this editor is not added to the
					 * MiEditorWindow by this method.
					 * @param editor 	the new editor (of type MiGraphicsEditor)
					 * @see			MiEditorWindow#getEditor
					 * @see			#getEditor
					 * @overrides		MiEditorWindow#getEditor
					 *------------------------------------------------------*/
	public		void		setEditor(MiEditor editor)
		{
		super.setEditor(editor);
		currentView.setEditor(editor);
		}
	/** by default, no multiple view support is required **/
	protected 	boolean		save(MiFileEditorWindowDocumentView view, OutputStream stream, String streamName)
		{
		return(save(stream, streamName));
		}
					/**------------------------------------------------------
					 * Processes the given action.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 			false if it is NOT OK to send
					 *			action to the next action handler
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_WINDOW_DESTROY_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			if (supportsMultipleOpenDocuments)
				{
				if (!verifyLossOfAnyChangesAllViews())
					{
					action.veto();
					return(false);
					}
				}
/*** done in processCommand
			else
				{
				if (!verifyLossOfAnyChanges())
					{
					action.veto();
					return(false);
					}
				}
***/
			}
		return(super.processAction(action));
		}
		
	protected 	 void		prepareToLoadDefaultFile()
		{
		}
	protected 	 void		defaultFileOpened()
		{
		}

	protected 	boolean		getHasChanged()
		{
		return(currentView.isChangedSinceSave());
		}
	public abstract	boolean		save(OutputStream stream, String streamName);
	protected abstract void		load(BufferedInputStream stream, String streamName) throws IOException;
	}

