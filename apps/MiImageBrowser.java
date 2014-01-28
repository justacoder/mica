
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



import com.swfm.mica.*; 
import java.io.*;
import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import java.awt.Container;
import java.applet.Applet;

/**----------------------------------------------------------------------------------------------
 * This class implements a image browser that provides methods for
 * the user to explore directories on the disk and display the files
 * that are images.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiImageBrowser extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiImageBrowserWindow. Supported command line parameters
					 * are:
					 * -title		the window border title
					 * -directory		the directory to start in
					 * -help		prints a help message
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		if ((Utility.hasCommandLineArgument(args, "?"))
			|| (Utility.hasCommandLineArgument(args, "help"))
			|| (Utility.hasCommandLineArgument(args, "-help")))
			{
			System.out.println(
			    "Usage: MiImageBrowser [-directory <directory name>] [-title <window title>]");
			System.exit(0);
			}
		new MiSystem(null);

		String title = Utility.getCommandLineArgument(args, "-title");
		String directory = Utility.getCommandLineArgument(args, "-directory");
		MiImageBrowserWindow window = new MiImageBrowserWindow(
					title, directory, new MiBounds(0.0, 0.0, 700.0, 500.0));
		window.setVisible(true);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiImageBrowserWindow. Supported html file parameters are:
					 * title	the window border title
					 * directory	the directory to start in
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);

		String directory = getParameter("directory");
		String title = getParameter("title");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiImageBrowserWindow window = new MiImageBrowserWindow(
			MiUtility.getFrame(this), title, directory, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a image browser window that provides methods for
 * the user to explore directories on the disk and display the files
 * that are images.
 *
 *----------------------------------------------------------------------------------------------*/
class MiImageBrowserWindow extends MiEditorWindow implements MiiActionHandler, MiiActionTypes, MiiEventTypes, MiiDisplayNames
	{
	// ---------------------------------------------------------------
	// Define the names of some commands
	// ---------------------------------------------------------------
	private static final String	Mi_DISPLAY_GIFS_COMMAND_NAME		= "displayGIFs";
	private static final String	Mi_DONT_DISPLAY_GIFS_COMMAND_NAME	= "dontDisplayGIFs";
	private static final String	Mi_DISPLAY_JPGS_COMMAND_NAME		= "displayJPGs";
	private static final String	Mi_DONT_DISPLAY_JPGS_COMMAND_NAME	= "dontDisplayJPGs";
	private static final String	Mi_DISPLAY_XPMS_COMMAND_NAME		= "displayXPMs";
	private static final String	Mi_DONT_DISPLAY_XPMS_COMMAND_NAME	= "dontDisplayXPMs";
	private static final String	Mi_VIEW_NAMED_THUMBNAILS_COMMAND_NAME 	= "thumbNailsAreNamed";
	private static final String	Mi_DONT_VIEW_NAMED_THUMBNAILS_COMMAND_NAME = "notThumbNailsAreNamed";

	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	
						= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	private static final String	Mi_DISPLAY_GIFS_NAME
						= "Mi_DISPLAY_GIFS_NAME";
	private static final String	Mi_DISPLAY_JPGS_NAME		
						= "Mi_DISPLAY_JPGS_NAME";
	private static final String	Mi_DISPLAY_XPMS_NAME		
						= "Mi_DISPLAY_XPMS_NAME";
	private static final String	Mi_VIEW_NAMED_THUMBNAILS_NAME		
						= "Mi_VIEW_NAMED_THUMBNAILS_NAME";
	private static final String	Mi_IMAGE_BROWSER_ABOUT_MSG
						= "Mi_IMAGE_BROWSER_ABOUT_MSG";



	private static final String	Mi_IM_IMAGE_FILENAME_RESOURCE_NAME = "imageFilename";
	private static final String	Mi_IM_IMAGE_RESOURCE_NAME 	= "image";


	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
		{
		new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE, "The Image Browser"),
		new Pair( Mi_DISPLAY_GIFS_NAME		, "Display GIF images"),
		new Pair( Mi_DISPLAY_JPGS_NAME		, "Display JPG images"),
		new Pair( Mi_DISPLAY_XPMS_NAME		, "Display XPM images"),
		new Pair( Mi_VIEW_NAMED_THUMBNAILS_NAME	, "Thumbnails are named"),
		new Pair( Mi_IMAGE_BROWSER_ABOUT_MSG	, 
			"\n\nThis browser is part of the Mica Graphics Appletcation Suite.\n\n"
			+ "This browser displays images found on the disk in a variety\n"
			+ "of methods. Select the name of an image to see it in it's native\n"
			+ "size, zoom in to see it magnified. Select a directory to see\n"
			+ "thumb-nails of all the images in the directory.\n\n\n"
			+ "Version 1.0      March 15, 1998")
	};

	// ---------------------------------------------------------------
	// Register the default values for this applications properties.
	// ---------------------------------------------------------------
	static	{
		MiSystem.setApplicationDefaultProperties(properties);
		}

	// ---------------------------------------------------------------
	// Define the class variables this class will use.
	// ---------------------------------------------------------------
	private		MiStatusBar	statusBar;
	private		String 		rootDirectoryName;
	private		MiImage		currentImage;
	private		String		currentImageFilename;
	private		String		currentFileOrDirectoryName;
	private		MiDirectoryTreeList directoryTreeList;
	private		boolean		thumbNailsAreNamed;
	private		boolean		tableIsBeingViewed;
	private		MiTable		table;
	private		MiPart		tablePanel;
	private		MiPart		editorPanel;
	private		MiTextField	directoryNameField;

					/**------------------------------------------------------
	 				 * Constructs a new MiImageBrowserWindow.
					 * @param awtContainer	the container to add this to
					 * @param title		the title of this window
					 * @param initialDirectory the starting directory
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiImageBrowserWindow(
						Container awtContainer, 
						String title, 
						String initialDirectory, 
						MiBounds screenSize)
		{
		super(MiUtility.getFrame(awtContainer), 
			title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);
		rootDirectoryName = initialDirectory;
		awtContainer.add("Center", (java.awt.Component )getCanvas().getNativeComponent());
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiImageBrowserWindow.
					 * @param title		the title of this window
					 * @param initialDirectory the starting directory
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiImageBrowserWindow(
						String title, 
						String initialDirectory, 
						MiBounds screenSize)
		{
		super(title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, screenSize);
		rootDirectoryName = initialDirectory;
		setup();
		}
					/**------------------------------------------------------
	 				 * Setup the window, creating the menubar, status bar,
					 * the table for the swatches... 
					 *------------------------------------------------------*/
	protected 	void		setup()
		{
		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// Let the MiEditorWindow superclass build the default window.
		// ---------------------------------------------------------------
		buildEditorWindow();

		getMenuBar().removePulldownMenu(Mi_EDIT_MENU_DISPLAY_NAME);
		getMenuBar().removePulldownMenu(Mi_SHAPE_MENU_DISPLAY_NAME);
		getMenuBar().removePulldownMenu(Mi_CONNECT_MENU_DISPLAY_NAME);
		getMenuBar().removePulldownMenu(Mi_TOOLS_MENU_DISPLAY_NAME);

		getMenuBar().getMenu(Mi_FILE_MENU_DISPLAY_NAME)
			.removeAllMenuItemsWithCommandsExcept(new Strings(Mi_QUIT_COMMAND_NAME));

		getMenuBar().getMenu(Mi_HELP_MENU_DISPLAY_NAME)
			.removeAllMenuItemsWithCommandsExcept(
				new Strings(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME));

		getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME)
			.removeMenuItemWithCommand(Mi_FORMAT_COMMAND_NAME);

		Strings commands = new Strings();
		commands.addElement(Mi_ZOOM_IN_COMMAND_NAME);
		commands.addElement(Mi_ZOOM_OUT_COMMAND_NAME);
		commands.addElement(Mi_VIEW_ALL_COMMAND_NAME);
		commands.addElement(Mi_VIEW_PREVIOUS_COMMAND_NAME);
		commands.addElement(Mi_VIEW_NEXT_COMMAND_NAME);
		getToolBar().removeAllToolItemsWithCommandsExcept(commands);

		getToolBar().appendToolItem(	Mi_STOP_DISPLAY_NAME,
						new MiImage("${Mi_IMAGES_HOME}/stop.gif", true),
						this, 
						Mi_STOP_COMMAND_NAME);

		MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);

		viewMenu.appendSeparator();
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_GIFS_NAME, this, 
							Mi_DISPLAY_GIFS_COMMAND_NAME,
							Mi_DONT_DISPLAY_GIFS_COMMAND_NAME);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_JPGS_NAME, this, 
							Mi_DISPLAY_JPGS_COMMAND_NAME,
							Mi_DONT_DISPLAY_JPGS_COMMAND_NAME);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_XPMS_NAME, this, 
							Mi_DISPLAY_XPMS_COMMAND_NAME,
							Mi_DONT_DISPLAY_XPMS_COMMAND_NAME);
		viewMenu.appendSeparator();

		viewMenu.appendBooleanMenuItem(Mi_VIEW_NAMED_THUMBNAILS_NAME, this, 
							Mi_VIEW_NAMED_THUMBNAILS_COMMAND_NAME,
							Mi_DONT_VIEW_NAMED_THUMBNAILS_COMMAND_NAME);

		setCommandState(Mi_DISPLAY_GIFS_COMMAND_NAME, true);
		setCommandState(Mi_DISPLAY_JPGS_COMMAND_NAME, true);
		setCommandState(Mi_DISPLAY_XPMS_COMMAND_NAME, true);


		getEditor().appendEventHandler(new MiIZoomAroundMouse());
		getEditor().appendEventHandler(new MiIPan());
		getEditor().setBackgroundColor(MiColorManager.veryLightGray);
		getEditor().setLayout(new MiSizeOnlyLayout());
		getEditor().appendCommandHandler(this, 
			Mi_CANCEL_COMMAND_NAME, new MiEvent(Mi_KEY_EVENT, Mi_ESC_KEY, 0));

		MiSystem.getHelpManager().setAboutDialog(MiToolkit.createAboutDialog(this, 
				getTitle(), MiSystem.getCompanyLogo(), Mi_IMAGE_BROWSER_ABOUT_MSG, false));

		editorPanel = getEditorPanel();
		directoryTreeList.populateList();
		}
					/**------------------------------------------------------
	 				 * Makes the status bar. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the status bar
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultStatusBar()
		{
		MiEditorStatusBar bar = new MiEditorStatusBar();
		statusBar = new MiStatusBar(".50\n.13\n.13\n.13");
		bar.appendPart(statusBar);

		bar.setOverlayStatusField(new MiStatusBarFocusStatusField(this));
		return(bar);
		}
					/**------------------------------------------------------
	 				 * Makes the palette. This is called from the super class's
					 * buildEditorWindow method. For this application this is the
					 * list of directories and files at the left of the window.
					 * @return		the file tree list
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultPalette()
		{
		if (rootDirectoryName == null)
			rootDirectoryName = MiSystem.getProperty(MiSystem.Mi_IMAGES_HOME);
		directoryTreeList = new MiDirectoryTreeList(rootDirectoryName);
		directoryTreeList.getTreeList().appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		MiColumnLayout layout = new MiColumnLayout();
		directoryNameField = new MiTextField();
		directoryNameField.setNumDisplayedColumns(24);
		directoryNameField.setValue(rootDirectoryName);
		directoryNameField.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		directoryNameField.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		layout.appendPart(directoryNameField);
		layout.appendPart(directoryTreeList);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(1);
		return(layout);
		}
					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are:
					 * <pre>
					 *    Mi_DISPLAY_GIFS_COMMAND_NAME
					 *    Mi_DONT_DISPLAY_GIFS_COMMAND_NAME
					 *    Mi_DISPLAY_JPGS_COMMAND_NAME
					 *    Mi_DONT_DISPLAY_JPGS_COMMAND_NAME
					 *    Mi_DISPLAY_XPMS_COMMAND_NAME
					 *    Mi_DONT_DISPLAY_XPMS_COMMAND_NAME
					 *    Mi_VIEW_NAMED_THUMBNAILS_COMMAND_NAME
					 *    Mi_DONT_VIEW_NAMED_THUMBNAILS_COMMAND_NAME
					 *    Mi_CANCEL_COMMAND_NAME
					 *    Mi_STOP_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 * @overrides		MiEditorWindow#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		if (command.equalsIgnoreCase(Mi_DISPLAY_GIFS_COMMAND_NAME))
			{
			directoryTreeList.addValidExtension("gif");
			directoryTreeList.populateList();
			}
		else if (command.equalsIgnoreCase(Mi_DONT_DISPLAY_GIFS_COMMAND_NAME))
			{
			directoryTreeList.removeValidExtension("gif");
			directoryTreeList.populateList();
			}
		else if (command.equalsIgnoreCase(Mi_DISPLAY_JPGS_COMMAND_NAME))
			{
			directoryTreeList.addValidExtension("jpg");
			directoryTreeList.populateList();
			}
		else if (command.equalsIgnoreCase(Mi_DONT_DISPLAY_JPGS_COMMAND_NAME))
			{
			directoryTreeList.removeValidExtension("jpg");
			directoryTreeList.populateList();
			}
		else if (command.equalsIgnoreCase(Mi_DISPLAY_XPMS_COMMAND_NAME))
			{
			directoryTreeList.addValidExtension("xpm");
			directoryTreeList.populateList();
			}
		else if (command.equalsIgnoreCase(Mi_DONT_DISPLAY_XPMS_COMMAND_NAME))
			{
			directoryTreeList.removeValidExtension("xpm");
			directoryTreeList.populateList();
			}
		else if (command.equalsIgnoreCase(Mi_VIEW_NAMED_THUMBNAILS_COMMAND_NAME))
			{
                        thumbNailsAreNamed = true;
			if (tableIsBeingViewed)
				setCurrentFileOrDirectoryName(currentFileOrDirectoryName);
			}
		else if (command.equalsIgnoreCase(Mi_DONT_VIEW_NAMED_THUMBNAILS_COMMAND_NAME))
			{
                        thumbNailsAreNamed = false;
			if (tableIsBeingViewed)
				setCurrentFileOrDirectoryName(currentFileOrDirectoryName);
			}
		else if ((command.equals(Mi_CANCEL_COMMAND_NAME)) || (command.equals(Mi_STOP_COMMAND_NAME)))
			{
			directoryTreeList.cancelPopulateList();
			}
		else
			{
			super.processCommand(command);
			}
		setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		}
					/**------------------------------------------------------
	 				 * Remove table and replace with editor that will display
					 * a single image.
					 *------------------------------------------------------*/
	protected	void		removeTable()
		{
		if (!tableIsBeingViewed)
			return;
		if (table != null)
			{
			int numCells = table.getNumberOfCells();
			for (int i = 0; i < numCells; ++i)
				table.getCell(i).setGraphics(null);

			setEditorPanel(editorPanel);
			}
		tableIsBeingViewed = false;
		}
					/**------------------------------------------------------
	 				 * Make table to replace editor that will display a number
					 * of thumb-nail images.
					 *------------------------------------------------------*/
	protected	void		makeTable()
		{
		if (tableIsBeingViewed)
			return;
		if (table == null)
			{
			table = new MiTable();
			table.getSelectionManager().setMaximumNumberSelected(0);
			table.setTotalNumberOfColumns(6);
			table.setMaximumNumberOfVisibleColumns(4);
			table.setPreferredNumberOfVisibleColumns(4);
			table.setHasFixedTotalNumberOfColumns(true);
			tablePanel = new MiScrolledBox(table);
			}
		table.removeAllCells();
		setEditorPanel(tablePanel);
		tableIsBeingViewed = true;
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are:
					 * <pre>
					 *    Mi_LOST_KEYBOARD_FOCUS_ACTION
					 *    Mi_ENTER_KEY_ACTION
					 *    Mi_DATA_IMPORT_ACTION
					 *    Mi_ITEM_SELECTED_ACTION
					 *    Mi_GOT_MOUSE_FOCUS_ACTION
					 *    Mi_LOST_MOUSE_FOCUS_ACTION
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if ((action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION))
			|| (action.hasActionType(Mi_ENTER_KEY_ACTION)))
			{
			if (!directoryNameField.getValue().equals(rootDirectoryName))
				{
				pushMouseAppearance(MiiTypes.Mi_WAIT_CURSOR, this + "");
				rootDirectoryName = directoryNameField.getValue();
				directoryTreeList.setRootDirectory(rootDirectoryName);
				directoryTreeList.populateList();
				popMouseAppearance(this + "");
				}
			}
		else if (action.hasActionType(Mi_DATA_IMPORT_ACTION))
			{
			MiDataTransferOperation transfer 
					= (MiDataTransferOperation )action.getActionSystemInfo();
			MiPart obj = (MiPart )transfer.getSource();
			String filename = (String )obj.getResource("filename");
			setCurrentImage(filename);
			}
		else if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			String filename = directoryTreeList.getTreeList().getValue();
			setCurrentFileOrDirectoryName(filename);
			}
		else if (action.hasActionType(Mi_GOT_MOUSE_FOCUS_ACTION))
			{
			MiPart cellGraphics = action.getActionSource();
			String filename = (String )cellGraphics.getResource(
				Mi_IM_IMAGE_FILENAME_RESOURCE_NAME);
			MiImage image = (MiImage )cellGraphics.getResource(
				Mi_IM_IMAGE_RESOURCE_NAME);
			updateStatusBar(filename, image);
			}
		else if (action.hasActionType(Mi_LOST_MOUSE_FOCUS_ACTION))
			{
			updateStatusBar(null, null);
			}

			
		return(true);
		}
					/**------------------------------------------------------
	 				 * Makes a label to use with thumb nails.
					 * @param text 		the thumb nail name.
					 * @return		the graphics label
					 *------------------------------------------------------*/
	protected	MiText		makeTextLabel(String text)
		{
		MiText t = new MiText(text);
		if (t.getFont() != getFont())
			t.setFont(getFont());
		if (t.getColor() != getColor())
			t.setColor(getColor());
		return(t);
		}
					/**------------------------------------------------------
	 				 * Makes a thumb nail image from the given filename.
					 * @param filename 	where the image is found
					 * @return		the graphics image
					 *------------------------------------------------------*/
	public		MiImage		getImageThumbNail(String filename)
		{
		return(getImage(filename, 48, 48));
		}
					/**------------------------------------------------------
	 				 * Makes a thumb nail image from the given filename with
					 * the given size.
					 * @param filename 	where the image is found
					 * @param width	 	the width of the image
					 * @param height	the height of the image
					 * @return		the graphics image
					 *------------------------------------------------------*/
	public		MiImage		getImage(String filename, int width, int height)
		{
		try	{
			return(new MiImage(filename, width, height, true));
			}
		catch (Exception e)
			{
			System.out.println("Unable to load image from filename: " + filename);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Sets the current image or directory of images to view
					 * and updates the graphics to display it/them.
					 * @param filename 	where the image(s) are found
					 *------------------------------------------------------*/
	protected	void		setCurrentFileOrDirectoryName(String filename)
		{
		currentFileOrDirectoryName = filename;

		File file = new File(filename);
		if (!(new File(filename).isDirectory()))
			{
			removeTable();
			setCurrentImage(filename);
			}
		else 
			{
			if ((new File(filename).isDirectory()))
				{
				makeTable();
				table.removeAllCells();
				Strings files = directoryTreeList.getFileNames(filename);
if ((MiSystem.getProperty(MiSystem.Mi_LIMIT_TABLE_SIZES) != null)
	&& (Utility.toBoolean(MiSystem.getProperty(MiSystem.Mi_LIMIT_TABLE_SIZES))))
	{
	while (files.size() > 200)
		files.removeElementAt(files.size() - 1);
	}
		
				MiParts cells = new MiParts();
				for (int i = 0; i < files.size(); ++i)
					{
					String imageFileName 
						= filename + File.separator + files.elementAt(i);
					MiImage image = getImageThumbNail(imageFileName);
					MiPart cellGraphics = image;
					if (!thumbNailsAreNamed)
						{
						cells.addElement(image);
						}
					else
						{
						MiColumnLayout layout = new MiColumnLayout();
						layout.setElementHSizing(Mi_NONE);
						layout.appendPart(image);
						layout.appendPart(makeTextLabel(files.elementAt(i)));
						cells.addElement(layout);
						cellGraphics = layout;
						}
					cellGraphics.setResource(
						Mi_IM_IMAGE_FILENAME_RESOURCE_NAME, imageFileName);
					cellGraphics.setResource(
						Mi_IM_IMAGE_RESOURCE_NAME, image);
					cellGraphics.appendActionHandler(
						this, Mi_GOT_MOUSE_FOCUS_ACTION);
					cellGraphics.appendActionHandler(
						this, Mi_LOST_MOUSE_FOCUS_ACTION);
					cellGraphics.setAcceptingMouseFocus(true);
					}
				table.appendItems(cells);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Sets the current image to view and updates the graphics 
					 * to display it.
					 * @param filename 	where the image is found
					 *------------------------------------------------------*/
	public		void		setCurrentImage(String filename)
		{
		MiImage newImage;
		try	{
			newImage = new MiImage(filename);
			}
		catch (Exception e)
			{
			System.out.println("Unable to load image from filename: " + filename);
			return;
			}
		if (currentImage != null)
			currentImage.deleteSelf();
		currentImage = newImage;
		currentImageFilename = filename;
		getEditor().setWorldBounds(getEditor().getUniverseBounds());
		currentImage.setCenter(getEditor().getWorldBounds().getCenter());
		getEditor().appendPart(currentImage);

		updateStatusBar(filename, currentImage);
		}
					/**------------------------------------------------------
	 				 * Updates the status bar to display the given image name
					 * and other info about the image like it's size, width and
					 * height.
					 * @param filename 	the name of the image
					 * @param image 	the image
					 *------------------------------------------------------*/
	protected	void		updateStatusBar(String filename, MiImage image)
		{
		if (filename != null)
			{
			File file = new File(filename);
			statusBar.setFieldValue(file.getName(), 0);
			statusBar.setFieldValue(new File(filename).length() + " bytes", 1);
			// If image loaded OK...
			if (image.getImage() != null)
				{
				statusBar.setFieldValue("Width: " + image.getImage().getWidth(null), 2);
				statusBar.setFieldValue("Height: " + image.getImage().getHeight(null), 3);
				}
			}
		else
			{
			statusBar.setFieldValue("", 0);
			statusBar.setFieldValue("", 1);
			statusBar.setFieldValue("", 2);
			statusBar.setFieldValue("", 3);
			}
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a MiiSimpleAnimator that loads a MiDirectoryTreeList
 * a little at a time.
 *
 *----------------------------------------------------------------------------------------------*/
class MiPopulateDirectoryTreeList implements MiiSimpleAnimator
	{
	private		Strings			files;
	private		MiDirectoryTreeList	directoryTreeList;
	private		String			directoryName;
	private		int			index;
	private		int			numToDisplayEachTick = 10;
	private		long			timeBetweenEachTick = 200;

					/**------------------------------------------------------
	 				 * Constructs a new MiPopulateDirectoryTreeList.
					 * @param directoryTreeList	the list to update
					 * @param files			the names of the files to add
					 * @param directoryName		the branch of the tree to add to
					 *------------------------------------------------------*/
	public				MiPopulateDirectoryTreeList(
						MiDirectoryTreeList directoryTreeList, 
						Strings files, 
						String directoryName)
		{
		this.directoryTreeList = directoryTreeList;
		this.files = files;
if ((MiSystem.getProperty(MiSystem.Mi_LIMIT_TABLE_SIZES) != null)
	&& (Utility.toBoolean(MiSystem.getProperty(MiSystem.Mi_LIMIT_TABLE_SIZES))))
	{
	while (this.files.size() > 40)
		this.files.removeElementAt(this.files.size() - 1);
	}
		this.directoryName = directoryName;
		MiAnimationManager.addAnimator(directoryTreeList, this);
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
			directoryTreeList.appendContentToTreeList(directoryName, files.elementAt(i));
			}
		index += numToDisplayEachTick;
		if (index >= files.size())
			return(-1);

		return(timeBetweenEachTick);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a tree list that displays a list of files
 * and that displays files that are images (.gif, .xpm, ...) with
 * their associated image as an icon next to the name.
 *
 *----------------------------------------------------------------------------------------------*/
class MiDirectoryTreeList extends MiWidget implements MiiActionHandler
	{
	private		MiTreeList	treeList;
	private		MiScrolledBox 	scrolledBox;
	private		String 		rootDirectoryName;
	private		boolean 	listDirectories		= true;
	private		boolean 	listFiles		= true;
	private		MiPopulateDirectoryTreeList populatePaletteAnimator;
	private		Strings	 	validExtensions		= new Strings();


					/**------------------------------------------------------
	 				 * Constructs a new MiDirectoryTreeList. The root directory
					 * is set to Mi_CURRENT_DIRECTORY.
					 *------------------------------------------------------*/
	public				MiDirectoryTreeList()
		{
		this(MiSystem.getProperty(MiSystem.Mi_CURRENT_DIRECTORY));
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiDirectoryTreeList.
					 * @param rootDirectoryName	the top level to be displayed
					 *				in this list.
					 *------------------------------------------------------*/
	public				MiDirectoryTreeList(String rootDirectoryName)
		{
		if (rootDirectoryName == null)
			rootDirectoryName = MiSystem.getProperty(MiSystem.Mi_CURRENT_DIRECTORY);
		this.rootDirectoryName = rootDirectoryName;
		setVisibleContainerAutomaticLayoutEnabled(false);
		treeList = new MiTreeList(28, false);
		treeList.appendActionHandler(this, Mi_NODE_EXPANDED_ACTION);
		scrolledBox = new MiScrolledBox(treeList);
		appendPart(scrolledBox);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);
		}
					/**------------------------------------------------------
	 				 * Sets the name of the top level directory to be displayed
                                         * in this list.
					 * @param rootDirectoryName	the name of the directory
					 *------------------------------------------------------*/
	public		void		setRootDirectory(String directory)
		{
		rootDirectoryName = directory;
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
			treeList.addItem(filename, image, filename, parentDirectory, isDirectory);
			}
		else
			{
			try	{
				MiImage image = getImageForFilename(filename);
				treeList.addItem(filename, image, filename, parentDirectory, isDirectory);
				}
			catch (Exception e)
				{
				MiText badFile = new MiText(filename);
				badFile.setColor(MiColorManager.red);
				badFile.setStatusHelpMessage("Error occured during load of this file");
				treeList.addItem(badFile, filename, parentDirectory, isDirectory);
				}
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
		if (populatePaletteAnimator != null)
			populatePaletteAnimator.abort();
		populatePaletteAnimator = new MiPopulateDirectoryTreeList(this, files, directoryName);
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
		MiImage image = new MiImage(filename, 20, 20, true);
		image.setIsDragAndDropSource(true);
		return(image);
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
		if (action.hasActionType(Mi_NODE_EXPANDED_ACTION))
			{
			String directoryTag = (String )action.getActionSystemInfo();
			if (treeList.getNumberOfChildren(directoryTag) == 0)
				{
				populateList(directoryTag);
				}
			}
		return(true);
		}
	}

