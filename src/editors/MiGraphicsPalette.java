
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
import java.io.*; 

/**----------------------------------------------------------------------------------------------
 * This class creates a graphics palette. This is a list of graphics that can
 * be dragged and dropped into the editor. It's contents is loaded 
 * from the Mica graphics files in the location specified by 
 * the setGraphicsPalettesDirectory() method (which by default is 
 * equal to ${Mi_HOME}/apps/palettes). The palette can be 
 * modified by drag and droping graphics from the graphics editor 
 * into the palette.
 * <p>
 * NOTE: this palette initially NOT visible - in order to reduce creation/startup time.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphicsPalette extends MiEditorPalette implements MiiCommandHandler, MiiMessages
	{
	private		String			openFilename;
	private		MiSelectionBox		paletteNameSelectionBox;
	private	 	MiComboBox		paletteList;
	private		MiiController		openDocumentManager;
	private		boolean			changeOpenFileToSavedAsFile;
	private		boolean			hasChanged;
	private		boolean			aPaletteIsOpen;
	private		String			graphicsPalettesDirectory;
	private		String			paletteFilenamePattern		= "*.pal";
	private		MiiController		defaultDocumentManager = new MiController();

					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsPalette.
					 *------------------------------------------------------*/
	public				MiGraphicsPalette()
		{
		MiColumnLayout layout = new MiColumnLayout();
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(1);
		setLayout(layout);

		getTreeListPalette().getSelectionManager().setBrowsable(false);

		graphicsPalettesDirectory = MiSystem.getPropertyOrKey("${Mi_PALETTES_DIRECTORY}");

		paletteList = new MiComboBox();
		paletteList.setMargins(new MiMargins(2));
		paletteList.appendCommandHandler(this, Mi_OPEN_COMMAND_NAME, Mi_VALUE_CHANGED_ACTION);
		insertPart(paletteList, 0);
		setVisible(false);
		}

					/**------------------------------------------------------
	 				 * Sets the directory in which to look for files *.pal
					 * that specify the palettes contents.
					 * @param directoryName		the directory of palettes
					 *------------------------------------------------------*/
	public		void		setGraphicsPalettesDirectory(String directoryName)
		{
		graphicsPalettesDirectory = MiSystem.getPropertyOrKey(directoryName);
		if (isVisible())
			{
			Strings names = getPaletteFilenames();
			paletteList.setVisible(names.size() > 1);
			paletteList.setContents(names);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the directory in which to look for files *.pal
					 * that specify the palettes contents.
					 * @return 			the directory of palettes
					 *------------------------------------------------------*/
	public		String		getGraphicsPalettesDirectory()
		{
		return(graphicsPalettesDirectory);
		}
					/**------------------------------------------------------
	 				 * Sets the default controller to use to load documents with.
					 * Note that this is copied before use because a document may
					 * specify it's own MiIOFormatManager and MiViewManagers. 
					 * @param controller	the default controller
					 *------------------------------------------------------*/
	public		void		 setDefaultController(MiiController controller)
		{
		defaultDocumentManager = controller;
		}
					/**------------------------------------------------------
	 				 * Gets the manager of the current open document.
					 * @return		the document manager
					 *------------------------------------------------------*/
	public		MiiController	 getOpenDocumentController()
		{
		return(openDocumentManager);
		}
					/**------------------------------------------------------
	 				 * Sets the pattern to use to find palettes in the palettes
					 * directory.
					 * @param pattern	the pattern (i.e. "*.pal")
					 *------------------------------------------------------*/
	public		void		setPaletteFilenamePattern(String pattern)
		{
		paletteFilenamePattern = pattern;
		}
					/**------------------------------------------------------
	 				 * Gets the pattern to use to find palettes in the palettes
					 * directory.
					 * @return 		the pattern
					 *------------------------------------------------------*/
	public		String		getPaletteFilenamePattern()
		{
		return(paletteFilenamePattern);
		}
					/**------------------------------------------------------
	 				 * Gets the combo box used to list palette names.
					 * @return 		the combo box
					 *------------------------------------------------------*/
	public		MiComboBox	getPaletteNameComboBox()
		{
		return(paletteList);
		}
					/**------------------------------------------------------
					 * This is called when this or some container is made visible
					 * or invisible, causing this part to also now be visible or
					 * invisible. Actions are generated depending on the new
					 * state of this MiPart. These action types are: 
					 * Mi_PART_SHOWING_ACTION and Mi_PART_NOT_SHOWING_ACTION.
					 *------------------------------------------------------*/
	protected	void		nowShowing(boolean flag)
		{
		super.nowShowing(flag);
		if ((flag) && (!aPaletteIsOpen))
			{
			// ComboBoxes automatically select the first item in the list
			Strings names = getPaletteFilenames();
			paletteList.setVisible(names.size() > 1);
			paletteList.setContents(names);
			}
		}	
					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are
					 * <pre>
					 *    Mi_OPEN_COMMAND_NAME
					 *    Mi_SAVE_COMMAND_NAME
					 *    Mi_SAVE_AS_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		if (command.equals(Mi_OPEN_COMMAND_NAME))
			{
			if (!paletteList.getValue().equals(openFilename))
				openFile(paletteList.getValue());
			}
		else if (command.equals(Mi_SAVE_COMMAND_NAME))
			{
			saveFile(openFilename);
			}
		else if (command.equals(Mi_SAVE_AS_COMMAND_NAME))
			{
			String newFilename = getNewPaletteFilenameFromUser();
			if (newFilename != null)
				{
				saveFile(newFilename);
				Strings names = getPaletteFilenames();
				paletteList.setVisible(names.size() > 1);
				paletteList.setContents(names);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Sets whether this window has changed. This is set to
					 * false after a save or open.
					 * @param flag		true if this has changed.
					 *------------------------------------------------------*/
	public		void		setHasChanged(boolean flag)
		{
		// FIX openDocumentManager.setHasChanged(flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether this window has changed. This is checked
					 * before a open, close or new.
					 * @return 		true if this has changed.
					 *------------------------------------------------------*/
	public		boolean		getHasChanged()
		{
		if (openDocumentManager != null)
			; // FIX return(openDocumentManager.getHasChanged());
		return(false);
		}
					/**------------------------------------------------------
	 				 * Gets the name of a file to which to save the current 
					 * palette.
					 * @return 		the name of a file to save the 
					 * 			palette to or null
					 *------------------------------------------------------*/
	protected	String		getNewPaletteFilenameFromUser()
		{
		if (paletteNameSelectionBox == null)
			{
			paletteNameSelectionBox = new MiSelectionBox(
				getContainingEditor(),
				Utility.sprintf(MiSystem.getProperty(Mi_OPEN_DIALOG_TITLE_MSG), "palette"),
				"", 
				true, 
				paletteList.getContents());
			}
		else
			{
			paletteNameSelectionBox.setPossibleSelections(paletteList.getContents());
			}
		paletteNameSelectionBox.setAutoRestrictingTypedEntryToListedSelections(false);
		String selection = paletteNameSelectionBox.popupAndWaitForClose();
		if (selection != null)
			selection = selection.trim();
		return(selection);
		}
					/**------------------------------------------------------
	 				 * Gets the list of names of palettes found in the palettes
					 * directory.
					 * @return 		the names of the files
					 *------------------------------------------------------*/
	protected	Strings		getPaletteFilenames()
		{
		if (MiSystem.isApplet())
			{
			return(new Strings(openFilename == null ? "" : openFilename));
			}
		return(Utility.getFileNames(graphicsPalettesDirectory, paletteFilenamePattern));
		}
					/**------------------------------------------------------
	 				 * Saves this palette to the given filename into the current
					 * graphics palettes directory.
					 * @param filename	the name of the file to save to
					 * @return 		true if sucessful
					 * @see 		#setGraphicsPalettesDirectory
					 *------------------------------------------------------*/
	public		boolean		saveFile(String filename)
		{
		MiUtility.pushMouseAppearance(this, MiiTypes.Mi_WAIT_CURSOR, "MiGraphicsPalette");

		String origFilename = filename;
		filename = graphicsPalettesDirectory + "/" + filename;
		filename = Utility.portFileNameToCurrentPlatform(filename);
		try	{
			MiModelStreamIOManager ioManager = new MiModelStreamIOManager();
			ioManager.setFilename(filename);
			openDocumentManager.setIOManager(ioManager);
			if (changeOpenFileToSavedAsFile)
				{
				openFilename = origFilename;
				paletteList.setValue(filename);
				}
			try	{
				openDocumentManager.save();
				}
			catch (IOException e)
				{
				MiUtility.popMouseAppearance(this, "MiGraphicsPalette");
				MiToolkit.postErrorDialog(getContainingEditor(), e.getMessage());
				return(false);
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			MiUtility.popMouseAppearance(this, "MiGraphicsPalette");
			MiToolkit.postErrorDialog(getContainingEditor(), "Save to " + filename + " failed\n");
			return(false);
			}
		MiUtility.popMouseAppearance(this, "MiGraphicsPalette");
		return(true);
		}
					/**------------------------------------------------------
	 				 * Loads this palette to from given filename. The filename
					 * is prepended with the current graphics palettes directory.
					 * The user is asked to confirm the loss of changes, if any.
					 * @param filename	the name of the file to load
					 * @return 		true if sucessful
					 * @see 		#setGraphicsPalettesDirectory
					 *------------------------------------------------------*/
	public		boolean		openFile(String filename)
		{
		if (!verifyLossOfAnyChanges())
			return(false);

		if (filename.equals(openFilename))
			return(true);

		MiUtility.pushMouseAppearance(this, MiiTypes.Mi_WAIT_CURSOR, "MiGraphicsPalette");

		paletteList.setValue(filename);
		if (!aPaletteIsOpen)
			{
			Strings names = getPaletteFilenames();
			paletteList.setVisible(names.size() > 1);
			paletteList.setContents(names);
			}

		String origFilename = filename;
		filename = graphicsPalettesDirectory + "/" + filename;
		filename = Utility.portFileNameToCurrentPlatform(filename);
		try	{
			MiModelStreamIOManager ioManager = new MiModelStreamIOManager();
			ioManager.setFilename(filename);
			if (openDocumentManager == null)
				{
				openDocumentManager = defaultDocumentManager.copy();
				openDocumentManager.setView(this);
				}
			openDocumentManager.setIOManager(ioManager);
			setPalettes((MiContainer )null);
			openFilename = null;
			try	{
				dispatchAction(MiFileEditorWindow.Mi_DOCUMENT_START_OPEN_ACTION);
				openDocumentManager.load();
				}
			catch (Exception e)
				{
				e.printStackTrace();
				MiUtility.popMouseAppearance(this, "MiGraphicsPalette");
				String msg = e.getMessage();
				if (Utility.isEmptyOrNull(msg))
					msg = "Loading of palette file \"" + filename + "\" failed\n";
				MiToolkit.postErrorDialog(getContainingEditor(), msg);
				dispatchAction(MiFileEditorWindow.Mi_DOCUMENT_FINISH_OPEN_ACTION);
				return(false);
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			MiUtility.popMouseAppearance(this, "MiGraphicsPalette");
			MiToolkit.postErrorDialog(
				getContainingEditor(), "Opening of palette file \"" + filename + "\" failed\n");
			return(false);
			}
		openFilename = origFilename;
		aPaletteIsOpen = true;
		MiUtility.popMouseAppearance(this, "MiGraphicsPalette");
		dispatchAction(MiFileEditorWindow.Mi_DOCUMENT_FINISH_OPEN_ACTION);
		return(true);
		}
					/**------------------------------------------------------
	 				 * Gets whether this window has changed. This is checked
					 * by the MiFileEditorWindow before a open, close or new.
					 * @return 		true if it is OK to overwrite any
					 *			changes the user has made
					 * @overrides		MiFileEditorWindow#verifyLossOfAnyChanges
					 *------------------------------------------------------*/
	public		boolean		verifyLossOfAnyChanges()
		{
		if (getHasChanged())
			{
			String result = MiToolkit.postSaveChangesDialog(
				getContainingEditor(), openFilename);
			if (result.equals(Mi_SAVE_COMMAND_NAME))
				saveFile(openFilename);
			else if (result.equals(Mi_CANCEL_COMMAND_NAME))
				return(false);
			}
		return(true);
		}
	}

