
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
import java.io.*;
import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**----------------------------------------------------------------------------------------------
 * This class implements a tree list that displays a list of files
 * and that displays files that are images (.gif, .xpm, ...) with
 * their associated image as an icon next to the name.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDirectoryTreePalette extends MiEditorPalette implements MiiActionHandler
	{
	private		MiTreeList	treeList;
	private		MiScrolledBox 	scrolledBox;
	private		String 		rootDirectoryName;
	private		boolean 	listDirectories		= true;
	private		boolean 	listFiles		= true;
	private		MiPopulateDirectoryTreePalette populatePaletteAnimator;
	private		Strings	 	validExtensions;
	private		Strings	 	selectedPaths		= new Strings();
	private		MiTextField	directoryNameField;


					/**------------------------------------------------------
	 				 * Constructs a new MiDirectoryTreeList. The root directory
					 * is set to Mi_CURRENT_DIRECTORY.
					 *------------------------------------------------------*/
	public				MiDirectoryTreePalette()
		{
		this(MiSystem.getProperty(MiSystem.Mi_CURRENT_DIRECTORY));
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiDirectoryTreeList.
					 * @param rootDirectoryName	the top level to be displayed
					 *				in this list.
					 *------------------------------------------------------*/
	public				MiDirectoryTreePalette(String rootDirectoryName)
		{
		if (rootDirectoryName == null)
			rootDirectoryName = MiSystem.getProperty(MiSystem.Mi_CURRENT_DIRECTORY);
		setVisibleContainerAutomaticLayoutEnabled(false);

		directoryNameField = new MiTextField();
		directoryNameField.setNumDisplayedColumns(24);
		directoryNameField.setValue(rootDirectoryName);
		directoryNameField.setAcceptingEnterKeyFocus(true);
		directoryNameField.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		directoryNameField.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		insertPart(directoryNameField, 0);

		treeList = getTreeListPalette();
		treeList.appendActionHandler(this, Mi_NODE_EXPANDED_ACTION);
		treeList.appendActionHandler(this, Mi_ITEM_DESELECTED_ACTION);
		treeList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		//scrolledBox = new MiScrolledBox(treeList);
		//scrolledBox.getBox().setBackgroundColor(MiToolkit.darkColor);
		//appendPart(scrolledBox);

		MiColumnLayout layout = new MiColumnLayout();
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(1);
		setLayout(layout);

		this.rootDirectoryName = rootDirectoryName;
		}
	public		boolean		openFile(String filename)
		{
		setRootDirectory(filename);
		return(true);
		}
					/**------------------------------------------------------
	 				 * Sets the name of the top level directory to be displayed
                                         * in this list.
					 * @param rootDirectoryName	the name of the directory
					 *------------------------------------------------------*/
	public		void		setRootDirectory(String directory)
		{
		rootDirectoryName = directory;
		directoryNameField.setValue(rootDirectoryName);
		populateList();
		}
					/**------------------------------------------------------
	 				 * Gets the name of the top level directory to be displayed
                                         * in this list.
					 * @return 		the name of the directory
					 *------------------------------------------------------*/
	public		String		getRootDirectory()
		{
		return(rootDirectoryName);
		}
					/**------------------------------------------------------
	 				 * Gets the text field that displays the editable root directory.
					 * @return 		the text field
					 *------------------------------------------------------*/
	public		MiTextField	getDirectoryNameTextField()
		{
		return(directoryNameField);
		}
					/**------------------------------------------------------
	 				 * Gets the tree list associated with this widget.
					 * @return 		the tree list
					 *------------------------------------------------------*/
	public		MiTreeList	getTreeList()
		{
		return(treeList);
		}
					/**------------------------------------------------------
	 				 * Gets the scrolled box associated with this widget.
					 * @return 		the scrolled box
					 *------------------------------------------------------*/
	public		MiScrolledBox	getScrolledBox()
		{
		return(scrolledBox);
		}
					/**------------------------------------------------------
	 				 * Sets the types of files to display (directories and or
					 * ordinary files).
					 * @param directories		display directory names
					 * @param files			display file names
					 *------------------------------------------------------*/
	public		void		setTypeOfFilesToList(boolean directories, boolean files)
		{
		listDirectories = directories;
		listFiles = files;
		}
					/**------------------------------------------------------
	 				 * Adds the given filename extension to the list of extensions
					 * that will be used to filter _file_ names. For example "gif".
					 * "*" indicates all files. The default is no files.
					 * @param charactersFollowingLastDot  a file name extension
					 *------------------------------------------------------*/
	public		void		addValidExtension(String charactersFollowingLastDot)
		{
		if (validExtensions == null)
			validExtensions = new Strings();
		if (!validExtensions.contains("*." + charactersFollowingLastDot))
			validExtensions.addElement("*." + charactersFollowingLastDot);
		}
					/**------------------------------------------------------
	 				 * Removes the given filename extension from the list of 
					 * extensions that will be used to filter _file_ names. 
					 * @param charactersFollowingLastDot  a file name extension
					 *------------------------------------------------------*/
	public		void		removeValidExtension(String charactersFollowingLastDot)
		{
		validExtensions.removeElement("*." + charactersFollowingLastDot);
		}
					/**------------------------------------------------------
	 				 * Append the given name to the given directory.
					 * @param parentDirectory	the directory to add name to
					 * @param filename		the name to add
					 *------------------------------------------------------*/
	public		void		appendContentToTreeList(String parentDirectory, String filename)
		{
		filename = parentDirectory + File.separator + filename;
		File file = new File(filename);
		boolean isDirectory = file.isDirectory();
		if (isDirectory)
			{
			MiImage image = getImageForDirectory(filename);
			treeList.addItem(file.getName(), image, filename, parentDirectory, isDirectory);
			}
		else
			{
			try	{
				MiImage image = getImageForFilename(filename);
				treeList.addItem(file.getName(), image, filename, parentDirectory, isDirectory);
				}
			catch (Exception e)
				{
				MiText badFile = new MiText(filename);
				badFile.setColor(MiColorManager.red);
				badFile.setStatusHelpMessage("Error occured during load of this file");
				treeList.addItem(badFile, filename, parentDirectory, isDirectory);
				return;
				}
			}
		for (int i = 0; i < selectedPaths.size(); ++i)
			{
			if (filename.equals(selectedPaths.elementAt(i)))
				treeList.selectItemWithTag(selectedPaths.elementAt(i));
			}
		}
					/**------------------------------------------------------
	 				 * Gets the files in the given directory that have been 
					 * filtered as per addValidExtension().
					 * @param directoryName		the directory of filenames
					 * @return 			the filenames
					 *------------------------------------------------------*/
	public		Strings		getFileNames(String directoryName)
		{
		return(Utility.getDirectoryContents(directoryName, validExtensions, false, true));
		}
					/**------------------------------------------------------
	 				 * Sets the full path name of all selected files and directories
					 * @param paths			the paths
					 *------------------------------------------------------*/
	public		void		setSelectedPaths(Strings paths)
		{
		selectedPaths = new Strings(paths);
		treeList.deSelectAll();
		for (int i = 0; i < selectedPaths.size(); ++i)
			{
			if (treeList.getItemWithTag(selectedPaths.elementAt(i)) != null)
				treeList.selectItemWithTag(selectedPaths.elementAt(i));
			}
		}
					/**------------------------------------------------------
	 				 * Gets the full path name of all selected files and directories
					 * @return 			the paths
					 *------------------------------------------------------*/
	public		Strings		getSelectedPaths()
		{
		return(new Strings(selectedPaths));
		}
					/**------------------------------------------------------
	 				 * Rebuilds the entire list of directories and files.
					 *------------------------------------------------------*/
	public		void		populateList()
		{
		getTreeList().removeAllItems();
		populateList(rootDirectoryName);
		}
					/**------------------------------------------------------
	 				 * Rebuilds the list of files in the given directory.
					 * @param directoryName		the directory
					 *------------------------------------------------------*/
	public		void		populateList(String directoryName)
		{
		Strings files = Utility.getDirectoryContents(
			directoryName, validExtensions, listDirectories, listFiles);
		if (getRootWindow() == null)
			{
			for (int i = 0; i < files.size(); ++i)
				{
				appendContentToTreeList(directoryName, files.elementAt(i));
				}
			return;
			}
		if (populatePaletteAnimator != null)
			populatePaletteAnimator.abort();
		populatePaletteAnimator = new MiPopulateDirectoryTreePalette(this, files, directoryName);
		}
					/**------------------------------------------------------
	 				 * Aborts the rebuilding of any lists.
					 *------------------------------------------------------*/
	public		void		cancelPopulateList()
		{
		if (populatePaletteAnimator != null)
			populatePaletteAnimator.abort();
		}
					/**------------------------------------------------------
	 				 * Gets the image, if any, associated with the given filename.
					 * @param filename		the filename
					 *------------------------------------------------------*/
	protected	MiImage		getImageForFilename(String filename)
		{
		if ((filename.endsWith(".gif")) || (filename.endsWith(".jpg")) || (filename.endsWith(".xpm")))
			{
			MiImage image = new MiImage(filename, 20, 20, false);
			image.setIsDragAndDropSource(true);
			return(image);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the image, if any, associated with the given directory.
					 * @param directoryName		the directory name
					 *------------------------------------------------------*/
	protected	MiImage		getImageForDirectory(String directoryName)
		{
		return(null);
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are:
					 * <pre>
					 *    Mi_NODE_EXPANDED_ACTION
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if ((action.getActionSource() == directoryNameField)
			&& ((action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION))
			|| (action.hasActionType(Mi_ENTER_KEY_ACTION))))
			{
			if (!directoryNameField.getValue().equals(getRootDirectory()))
				{
				setRootDirectory(directoryNameField.getValue());
				}
			}
		else if (action.hasActionType(Mi_NODE_EXPANDED_ACTION))
			{
			String directoryTag = (String )action.getActionSystemInfo();
			if (treeList.getNumberOfChildren(directoryTag) == 0)
				{
				populateList(directoryTag);
				}
			}
		else if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			MiTableCell cell = (MiTableCell )action.getActionSystemInfo();
			if (!selectedPaths.contains((String )cell.getTag()))
				{
				selectedPaths.addElement((String )cell.getTag());
				dispatchAction(Mi_ITEM_SELECTED_ACTION, cell.getTag());
				}
			}
		else if (action.hasActionType(Mi_ITEM_DESELECTED_ACTION))
			{
			MiTableCell cell = (MiTableCell )action.getActionSystemInfo();
			selectedPaths.removeElement((String )cell.getTag());
			dispatchAction(Mi_ITEM_DESELECTED_ACTION, cell.getTag());
			}
		return(true);
		}
	}




/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a MiiSimpleAnimator that loads a MiDirectoryTreeList
 * a little at a time.
 *
 *----------------------------------------------------------------------------------------------*/
class MiPopulateDirectoryTreePalette implements MiiSimpleAnimator
	{
	private		Strings			files;
	private		MiDirectoryTreePalette	directoryTreePalette;
	private		String			directoryName;
	private		int			index;
	private		int			numToDisplayEachTick = 10;
	private		long			timeBetweenEachTick = 200;

					/**------------------------------------------------------
	 				 * Constructs a new MiPopulateDirectoryTreePalette.
					 * @param directoryTreePalette	the list to update
					 * @param files			the names of the files to add
					 * @param directoryName		the branch of the tree to add to
					 *------------------------------------------------------*/
	public				MiPopulateDirectoryTreePalette(
						MiDirectoryTreePalette directoryTreePalette, 
						Strings files, 
						String directoryName)
		{
		this.directoryTreePalette = directoryTreePalette;
		this.files = files;
if ((MiSystem.getProperty(MiSystem.Mi_LIMIT_TABLE_SIZES) != null)
	&& (Utility.toBoolean(MiSystem.getProperty(MiSystem.Mi_LIMIT_TABLE_SIZES))))
	{
	while (this.files.size() > 40)
		this.files.removeElementAt(this.files.size() - 1);
	}
		this.directoryName = directoryName;
		MiAnimationManager.addAnimator(directoryTreePalette, this);
		}
					/**------------------------------------------------------
	 				 * Sets the number of files to add to the directory each
					 * time this is called.
					 * @param numToDisplayEachTick	the number to add each time
					 *------------------------------------------------------*/
	public		void		setNumToDisplayEachTick(int numToDisplayEachTick)
		{
		this.numToDisplayEachTick = numToDisplayEachTick;
		}
					/**------------------------------------------------------
	 				 * Sets the time between adding files to the target directory.
					 * @param timeBetweenEachTick	the number of millisecs
					 *------------------------------------------------------*/
	public		void		setTimeBetweenEachTick(long timeBetweenEachTick)
		{
		this.timeBetweenEachTick = timeBetweenEachTick;
		}
					/**------------------------------------------------------
	 				 * Tells this to not add anything else at the next timer tick.
					 *------------------------------------------------------*/
	public		void		abort()
		{
		index = Integer.MAX_VALUE;
		}
					/**------------------------------------------------------
	 				 * Called periodically to add files to the target directory
					 * @return		the number of millisecs until next call
					 *------------------------------------------------------*/
	public		long		animate()
		{
		if (index >= files.size())
			{
			return(-1);
			}
		for (int i = index; (i < files.size()) && (i < index + numToDisplayEachTick); ++i)
			{
			directoryTreePalette.appendContentToTreeList(directoryName, files.elementAt(i));
			}
		index += numToDisplayEachTick;
		if (index >= files.size())
			{
			return(-1);
			}

		return(timeBetweenEachTick);
		}
	}
