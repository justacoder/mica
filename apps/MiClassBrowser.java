
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
import com.swfm.mica.util.Utility;
import java.io.File;
import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Strings;
import java.awt.Component;
import java.awt.Container;
import java.applet.Applet;

/**----------------------------------------------------------------------------------------------
 * This class implements a graphics editor using the Mica Graphics 
 * Framework. This graphics editor loads the source code found in
 * one of more specified directories and displays the graphics of
 * their class hierarchy.
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiClassBrowser extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiClassBrowserWindow. Supported command line parameters are:
					 * -file		load this file on startup
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String filename = Utility.getCommandLineArgument(args, "-file");
		String title = Utility.getCommandLineArgument(args, "-title");
		MiClassBrowserWindow window = new MiClassBrowserWindow(title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		loadSpecifiedGraphicsAndPalette(window, filename);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiClassBrowserWindow. Supported html file parameters are:
					 * file		load this file on startup
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);

		String filename = getParameter("file");
		String title = getParameter("title");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiClassBrowserWindow window = new MiClassBrowserWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		loadSpecifiedGraphicsAndPalette(window, filename);
		}
					/**------------------------------------------------------
	 				 * Loads the specified graphics file and palette, if any.
					 * @param window	the window
					 * @param filename	the name of the file that contains
					 *			graphics or null
					 *------------------------------------------------------*/
	protected static void		loadSpecifiedGraphicsAndPalette(
						MiClassBrowserWindow window, String filename)
		{
		if (filename != null)
			{
			try	{
				// ---------------------------------------------------------------
				// Get a lock for the window from this, the 'main' starter thread
				// ---------------------------------------------------------------
				window.getAccessLock();
				if (filename != null)
					window.openFile(filename);
				else
					window.openDefaultFile();
				window.freeAccessLock();
				}
			catch (Exception e)
				{
				e.printStackTrace();
				System.out.println(
					"Usage: MiClassBrowser [-file <filename>] [-title <window title>]");
				System.exit(1);
				}
			}
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a graphics editor using the Mica Graphics 
 * Framework. This graphics editor loads the source code found in
 * one of more specified directories and displays the graphics of
 * their class hierarchy.
 *
 *----------------------------------------------------------------------------------------------*/
class MiClassBrowserWindow extends MiGraphicsWindow
	{
	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	
						= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	private static final String	Mi_CLASS_BROWSER_WINDOW_ABOUT_MSG
						= "Mi_CLASS_BROWSER_WINDOW_ABOUT_MSG";
	private static final String 	Mi_DIRECTORY_TREE_STATUS_MSG
						= "Mi_DIRECTORY_TREE_STATUS_MSG";
	private static final Pair[] properties = 
		{
		new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE	, "Class Browser"),
		new Pair( Mi_DIRECTORY_TREE_STATUS_MSG		, 
						"Select directory(s) whose source code is to be graphed"),
		new Pair( Mi_CLASS_BROWSER_WINDOW_ABOUT_MSG	, 
			"\n\nThis window contains a Java source code class browser.\n"
			+ "Version 1.0      June 25, 1998")
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

	// ---------------------------------------------------------------
	// Define the class variables this class will use.
	// ---------------------------------------------------------------
	private		MiDirectoryTreePalette directoryTreePalette;
	private		String		fileExtension		= 	"java";
	private		String		outputFile		= 	"MiClassBrowser.tmp";
	private		Strings		directories		= 	new Strings();


					/**------------------------------------------------------
	 				 * Constructs a new MiClassBrowserWindow and adds it to the given
					 * AWT container.
					 * @param awtContainer	the AWT container this is going into
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiClassBrowserWindow(
						Container awtContainer, String title, MiBounds screenSize)
		{
		super(MiUtility.getFrame(awtContainer), 
			title != null ? title : Mi_DEFAULT_WINDOW_BORDER_TITLE, screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);
		awtContainer.add("Center", (Component )getCanvas().getNativeComponent());
		setupMiClassBrowser();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiClassBrowserWindow and creates and adds it
					 * to a AWT Frame
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiClassBrowserWindow(String title, MiBounds screenSize)
		{
		super(title != null ? title : Mi_DEFAULT_WINDOW_BORDER_TITLE, screenSize);
		setupMiClassBrowser();
		}
					/**------------------------------------------------------
	 				 * Override the super class setup so that we can do our
					 * own setup. When this method is called, none of the local
					 * class variables will have been created (i.e. directories
					 * will be equal to null).
					 *------------------------------------------------------*/
	protected	void		setup()
		{
		}
					/**------------------------------------------------------
	 				 * Customizes this window based on the default setup provided
					 * by the MiEditorWindow superclass.
					 *------------------------------------------------------*/
	protected	void		setupMiClassBrowser()
		{
		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// Let the MiEditorWindow superclass build the default window.
		// ---------------------------------------------------------------
		setBuildDockingPanels(true);
		buildEditorWindow();

		// ---------------------------------------------------------------
		// Make sure connections are underneath nodes at all times.
		// ---------------------------------------------------------------
		getEditor().setKeepConnectionsBelowNodes(true);

		// ---------------------------------------------------------------
		// Display the names of the objects under the mouse in the status
		// bar (good for when the objects are too small to otherwise identify).
		// ---------------------------------------------------------------
		getEditor().appendEventHandler(
			new MiSetStatusBarStateToNameOfObjectUnderMouse((MiEditorStatusBar )getStatusBar()));

		// ---------------------------------------------------------------
		// Add palette-specific options for the palette in the view menu.
		// ---------------------------------------------------------------
		MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);
		viewMenu.appendSeparator();
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_BIRDS_EYE_VIEW_MENUITEM_DISPLAY_NAME, this,
				Mi_DISPLAY_BIRDS_EYE_VIEW_COMMAND_NAME, Mi_HIDE_BIRDS_EYE_VIEW_COMMAND_NAME);
		viewMenu.appendSeparator();
		viewMenu.appendBooleanMenuItem(Mi_SHOW_PALETTE_NAME, this,
				Mi_SHOW_PALETTE_NAME, Mi_HIDE_PALETTE_COMMAND_NAME);

		// ---------------------------------------------------------------
		// There is not support for these ... yet ...
		// ---------------------------------------------------------------
		setCommandSensitivity(Mi_PRINT_SETUP_COMMAND_NAME, false);
		setCommandSensitivity(Mi_DISPLAY_PROPERTIES_COMMAND_NAME, false);
		setCommandSensitivity(Mi_DISPLAY_PREFERENCES_COMMAND_NAME, false);

		// ---------------------------------------------------------------
		// Setup the 'About' dialog box
		// ---------------------------------------------------------------
		MiSystem.getHelpManager().setAboutDialog(MiToolkit.createAboutDialog(this, 
				Mi_DEFAULT_WINDOW_BORDER_TITLE, 
				MiSystem.getCompanyLogo(), Mi_CLASS_BROWSER_WINDOW_ABOUT_MSG, false));

		// ---------------------------------------------------------------
		// Setup the 'Help' Window
		// ---------------------------------------------------------------
		MiHelpWindow helpWindow = new MiHelpWindow(null, new MiBounds(0, 0, 500, 500));
		MiSystem.getHelpManager().setHelpOnApplicationDialog(helpWindow);
		helpWindow.setDefaultCloseCommand(null);
		helpWindow.setHelpFile("${Mi_HOME}/apps/MiNetwork.help");

		openDefaultFile();

		MiEditingPermissions permissions = new MiEditingPermissions();
		permissions.setEditable(false);
		permissions.setCopyable(false);
		updateViewFromPermissions(permissions);
		}
					/**------------------------------------------------------
	 				 * Makes the palette for the window. This is called by
					 * the MiEditorWindow superclass during setup (i.e. 
					 * buildEditorWindow()). Make it invisible to start with.
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultPalette()
		{
		directoryTreePalette = new MiDirectoryTreePalette();
		directoryTreePalette.appendActionHandler(this, Mi_ITEM_DESELECTED_ACTION);
		directoryTreePalette.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		//directoryTreePalette.setVisible(false);
		directoryTreePalette.setTypeOfFilesToList(true, false);
		directoryTreePalette.getTreeList()
			.getSelectionManager().setMaximumNumberSelected(Integer.MAX_VALUE);
		directoryTreePalette.getTreeList().setStatusHelpMessage(
			Mi_DIRECTORY_TREE_STATUS_MSG);

		return(directoryTreePalette);
		}
					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are:
					 * <pre>
					 *    Mi_LOST_KEYBOARD_FOCUS_ACTION
					 *    Mi_ENTER_KEY_ACTION
					 *    Mi_DISPLAY_BIRDS_EYE_VIEW_COMMAND_NAME
					 *    Mi_HIDE_BIRDS_EYE_VIEW_COMMAND_NAME
					 *    Mi_SHOW_PALETTE_NAME
					 *    Mi_HIDE_PALETTE_COMMAND_NAME
					 *    Mi_ITEM_SELECTED_ACTION
					 *    Mi_ITEM_DESELECTED_ACTION
					 * </pre>
					 * @param command  	the command to execute
					 * @overrides		MiGraphicsWindow#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		pushMouseAppearance(Mi_WAIT_CURSOR, "ClassBrowserCommand");
		if ((command.equals(Mi_DISPLAY_BIRDS_EYE_VIEW_COMMAND_NAME))
			|| (command.equals(Mi_HIDE_BIRDS_EYE_VIEW_COMMAND_NAME))
			|| (command.equals(Mi_PRINT_COMMAND_NAME)))
			{
			super.processCommand(command);
			}
		popMouseAppearance("ClassBrowserCommand");
		}
	public		boolean		processAction(MiiAction action)
		{
		pushMouseAppearance(Mi_WAIT_CURSOR, "ClassBrowserAction");
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			directories = directoryTreePalette.getSelectedPaths();
			displayClassesInDirectories(directories);
			}
		else if (action.hasActionType(Mi_ITEM_DESELECTED_ACTION))
			{
			directories = directoryTreePalette.getSelectedPaths();
			displayClassesInDirectories(directories);
			}
		popMouseAppearance("ClassBrowserAction");
		return(super.processAction(action));
		}
					/**------------------------------------------------------
	 				 * Opens the default, no name, file that the user can
					 * edit in but which needs to be named before saving.
					 * In this case, open the current directory...
					 * @overrides		MiFileEditorWindow#openDefaultFile
					 * @see			MiFileEditorWindow#prepareToLoadDefaultFile
					 *------------------------------------------------------*/
	public		void		openDefaultFile()
		{
		String openFilename = MiSystem.getProperty("Mi_CURRENT_DIRECTORY");
		setOpenFilename(openFilename);
		prepareToLoadDefaultFile();
		openFile(openFilename);
		}

	public		boolean		openFile(String directoryName)
		{
		String rootDirectory = new File(directoryName).getParent();
		if (rootDirectory == null)
			rootDirectory = directoryName;
		
		directoryTreePalette.openFile(rootDirectory);
		directories.removeAllElements();
		directories.addElement(directoryName);
		directoryTreePalette.setSelectedPaths(directories);
		displayClassesInDirectories(directories);
		return(true);
		}
	protected	void		displayClassesInDirectories(Strings directories)
		{
		new SourceCodeClassHierarchyFileMaker(directories, outputFile, fileExtension);
		//super.close();
		super.openFile(outputFile);
		}
	}

