
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
 * This class implements a class browser that provides methods for
 * the user to explore directories on the disk and display their source
 * code as tree graphs.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelBrowser extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiModelBrowserWindow. Supported command line parameters
					 * are:
					 * -title		the window border title
					 * -file		the file to start with
					 * -help		prints a help message
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		if ((Utility.getCommandLineArgument(args, "?") != null)
			|| (Utility.getCommandLineArgument(args, "help") != null)
			|| (Utility.getCommandLineArgument(args, "-help") != null))
			{
			System.out.println(
			    "Usage: MiModelBrowser [-file <filename>] [-title <window title>]");
			System.exit(0);
			}
		new MiSystem(null);

		String title = Utility.getCommandLineArgument(args, "-title");
		String filename = Utility.getCommandLineArgument(args, "-file");
		MiModelBrowserWindow window = new MiModelBrowserWindow(
					title, new MiBounds(0.0, 0.0, 700.0, 500.0));
		window.setVisible(true);
		loadSpecifiedFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiModelBrowserWindow. Supported html file parameters are:
					 * title	the window border title
					 * file		the file to start with
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);

		String filename = getParameter("file");
		String title = getParameter("title");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiModelBrowserWindow window = new MiModelBrowserWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		loadSpecifiedFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * Loads the specified file, if any.
					 * @param window	the window
					 * @param filename	the name of the file or null
					 *------------------------------------------------------*/
	protected static void		loadSpecifiedFile(MiModelBrowserWindow window, String filename)
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
				window.freeAccessLock();
				}
			catch (Exception e)
				{
				System.out.println(
					"Usage: MiModelBrowser [-file <filename>] [-title <window title>]");
				System.exit(1);
				}
			}
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
class MiModelBrowserWindow extends MiGraphicsWindow implements MiiActionHandler, MiiActionTypes, MiiEventTypes, MiiDisplayNames, MiiModelChangeHandler, MiiModelTypes
	{
	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	
						= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	private static final String	Mi_MODEL_BROWSER_WINDOW_ABOUT_MSG
						= "Mi_MODEL_BROWSER_WINDOW_ABOUT_MSG";
	private static final String 	Mi_DIRECTORY_TREE_STATUS_MSG
						= "Mi_DIRECTORY_TREE_STATUS_MSG";
	private static final String	Mi_DATA_SOURCE_PALETTE_DEFINITION
						= "Mi_DATA_SOURCE_PALETTE_DEFINITION";
	private static final String	Mi_DATA_FORMAT_PALETTE_DEFINITION
						= "Mi_DATA_FORMAT_PALETTE_DEFINITION";
	private static final String	Mi_DATA_VIEW_PALETTE_DEFINITION
						= "Mi_DATA_VIEW_PALETTE_DEFINITION";
	private static final String	Mi_UPDATE_PIPELINE_DISPLAY_NAME
						= "Mi_UPDATE_PIPELINE_DISPLAY_NAME";
	private static final String	Mi_UPDATE_PIPELINE_ICON_NAME
						= "Mi_UPDATE_PIPELINE_ICON_NAME";
	private static final String	Mi_UPDATE_PIPELINE_STATUS_HELP_MSG
						= "Mi_UPDATE_PIPELINE_STATUS_HELP_MSG";
	private static final String	Mi_NO_UPDATE_PIPELINE_STATUS_HELP_MSG
						= "Mi_NO_UPDATE_PIPELINE_STATUS_HELP_MSG";
	private static final String	Mi_UPDATE_PIPELINE_BALLOON_HELP_MSG
						= "Mi_UPDATE_PIPELINE_BALLOON_HELP_MSG";

	private static final String	Mi_SPECIFY_DATA_SOURCE_DISPLAY_NAME
						= "Mi_SPECIFY_DATA_SOURCE_DISPLAY_NAME";
	private static final String	Mi_SPECIFY_DATA_SOURCE_ICON_NAME
						= "Mi_SPECIFY_DATA_SOURCE_ICON_NAME";
	private static final String	Mi_SPECIFY_DATA_SOURCE_STATUS_HELP_MSG
						= "Mi_SPECIFY_DATA_SOURCE_STATUS_HELP_MSG";
	private static final String	Mi_NO_SPECIFY_DATA_SOURCE_STATUS_HELP_MSG
						= "Mi_NO_SPECIFY_DATA_SOURCE_STATUS_HELP_MSG";
	private static final String	Mi_SPECIFY_DATA_SOURCE_BALLOON_HELP_MSG
						= "Mi_SPECIFY_DATA_SOURCE_BALLOON_HELP_MSG";

	private static final String	Mi_SPECIFY_DATA_FORMAT_DISPLAY_NAME
						= "Mi_SPECIFY_DATA_FORMAT_DISPLAY_NAME";
	private static final String	Mi_SPECIFY_DATA_FORMAT_ICON_NAME
						= "Mi_SPECIFY_DATA_FORMAT_ICON_NAME";
	private static final String	Mi_SPECIFY_DATA_FORMAT_STATUS_HELP_MSG
						= "Mi_SPECIFY_DATA_FORMAT_STATUS_HELP_MSG";
	private static final String	Mi_NO_SPECIFY_DATA_FORMAT_STATUS_HELP_MSG
						= "Mi_NO_SPECIFY_DATA_FORMAT_STATUS_HELP_MSG";
	private static final String	Mi_SPECIFY_DATA_FORMAT_BALLOON_HELP_MSG
						= "Mi_SPECIFY_DATA_FORMAT_BALLOON_HELP_MSG";

	private static final String	Mi_SPECIFY_DATA_MAP_DISPLAY_NAME
						= "Mi_SPECIFY_DATA_MAP_DISPLAY_NAME";
	private static final String	Mi_SPECIFY_DATA_MAP_ICON_NAME
						= "Mi_SPECIFY_DATA_MAP_ICON_NAME";
	private static final String	Mi_SPECIFY_DATA_MAP_STATUS_HELP_MSG
						= "Mi_SPECIFY_DATA_MAP_STATUS_HELP_MSG";
	private static final String	Mi_NO_DATA_MAP_STATUS_HELP_MSG
						= "Mi_NO_DATA_MAP_STATUS_HELP_MSG";
	private static final String	Mi_SPECIFY_DATA_MAP_BALLOON_HELP_MSG
						= "Mi_SPECIFY_DATA_MAP_BALLOON_HELP_MSG";
	private static final String	Mi_SPECIFY_DATA_VIEW_DISPLAY_NAME
						= "Mi_SPECIFY_DATA_VIEW_DISPLAY_NAME";
	private static final String	Mi_SPECIFY_DATA_VIEW_ICON_NAME
						= "Mi_SPECIFY_DATA_VIEW_ICON_NAME";
	private static final String	Mi_SPECIFY_DATA_VIEW_STATUS_HELP_MSG
						= "Mi_SPECIFY_DATA_VIEW_STATUS_HELP_MSG";
	private static final String	Mi_NO_SPECIFY_DATA_VIEW_MANAGER_STATUS_HELP_MSG
						= "Mi_NO_SPECIFY_DATA_VIEW_MANAGER_STATUS_HELP_MSG";
	private static final String	Mi_SPECIFY_DATA_VIEW_BALLOON_HELP_MSG
						= "Mi_SPECIFY_DATA_VIEW_BALLOON_HELP_MSG";

	private static final Pair[] properties = 
		{
		new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE	, "Structured-Data Browser"),
		new Pair( Mi_DIRECTORY_TREE_STATUS_MSG		, 
						"Select file whose data is to be browsed"),
		new Pair( Mi_DATA_SOURCE_PALETTE_DEFINITION	, "modelBrowserInputPalette.mica"),
		new Pair( Mi_DATA_FORMAT_PALETTE_DEFINITION	, "modelBrowserFormatPalette.mica"),
		new Pair( Mi_DATA_VIEW_PALETTE_DEFINITION	, "modelBrowserViewPalette.mica"),
		new Pair( Mi_UPDATE_PIPELINE_ICON_NAME		, "updatePipeline.gif"),
		new Pair( Mi_SPECIFY_DATA_SOURCE_ICON_NAME	, "specifyDataSource.gif"),
		new Pair( Mi_SPECIFY_DATA_FORMAT_ICON_NAME	, "specifyDataFormat.gif"),
		new Pair( Mi_SPECIFY_DATA_MAP_ICON_NAME		, "specifyDataMap.gif"),
		new Pair( Mi_SPECIFY_DATA_VIEW_ICON_NAME	, "specifyDataView.gif"),

		new Pair( Mi_UPDATE_PIPELINE_DISPLAY_NAME	, "Update"),
		new Pair( Mi_SPECIFY_DATA_SOURCE_DISPLAY_NAME	, "Specify Source"),
		new Pair( Mi_SPECIFY_DATA_FORMAT_DISPLAY_NAME	, "Specify Format"),
		new Pair( Mi_SPECIFY_DATA_MAP_DISPLAY_NAME	, "Specify Mapping"),
		new Pair( Mi_SPECIFY_DATA_VIEW_DISPLAY_NAME	, "Specify View"),

		new Pair( Mi_MODEL_BROWSER_WINDOW_ABOUT_MSG	, 
			"\n\nThis window contains a Java structured-data browser.\n"
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
	private static final String	Mi_UPDATE_PIPELINE_COMMAND_NAME		= "updatePipeline";
	private static final String	Mi_SPECIFY_DATA_SOURCE_COMMAND_NAME	= "specifyDataSource";
	private static final String	Mi_SPECIFY_DATA_FORMAT_COMMAND_NAME	= "specifyDataFormat";
	private static final String	Mi_SPECIFY_DATA_MAP_COMMAND_NAME	= "specifyDataMap";
	private static final String	Mi_SPECIFY_DATA_VIEW_COMMAND_NAME	= "specifyDataView";

	private static final MiToolAndMenuItemDefinition[] dataFlowToolBarDef =
		{
		new MiToolAndMenuItemDefinition(
				Mi_UPDATE_PIPELINE_DISPLAY_NAME,
				Mi_UPDATE_PIPELINE_ICON_NAME,
				Mi_UPDATE_PIPELINE_COMMAND_NAME,
				Mi_UPDATE_PIPELINE_STATUS_HELP_MSG,
				Mi_NO_UPDATE_PIPELINE_STATUS_HELP_MSG,
				Mi_UPDATE_PIPELINE_BALLOON_HELP_MSG),
		new MiToolAndMenuItemDefinition(),
		new MiToolAndMenuItemDefinition(
				Mi_SPECIFY_DATA_SOURCE_DISPLAY_NAME,
				Mi_SPECIFY_DATA_SOURCE_ICON_NAME,
				Mi_SPECIFY_DATA_SOURCE_COMMAND_NAME,
				Mi_SPECIFY_DATA_SOURCE_STATUS_HELP_MSG,
				Mi_NO_SPECIFY_DATA_SOURCE_STATUS_HELP_MSG,
				Mi_SPECIFY_DATA_SOURCE_BALLOON_HELP_MSG),
		new MiToolAndMenuItemDefinition(
				null, "${Mi_IMAGES_HOME}/right.gif", null, null, null, null, 
				new MiSize(16,16)),
		new MiToolAndMenuItemDefinition(
				Mi_SPECIFY_DATA_FORMAT_DISPLAY_NAME,
				Mi_SPECIFY_DATA_FORMAT_ICON_NAME,
				Mi_SPECIFY_DATA_FORMAT_COMMAND_NAME,
				Mi_SPECIFY_DATA_FORMAT_STATUS_HELP_MSG,
				Mi_NO_SPECIFY_DATA_FORMAT_STATUS_HELP_MSG,
				Mi_SPECIFY_DATA_FORMAT_BALLOON_HELP_MSG),
		new MiToolAndMenuItemDefinition(
				null, "${Mi_IMAGES_HOME}/right.gif", null, null, null, null, 
				new MiSize(16,16)),
		new MiToolAndMenuItemDefinition(
				Mi_SPECIFY_DATA_MAP_DISPLAY_NAME,
				Mi_SPECIFY_DATA_MAP_ICON_NAME,
				Mi_SPECIFY_DATA_MAP_COMMAND_NAME,
				Mi_SPECIFY_DATA_MAP_STATUS_HELP_MSG,
				Mi_NO_DATA_MAP_STATUS_HELP_MSG,
				Mi_SPECIFY_DATA_MAP_BALLOON_HELP_MSG),
		new MiToolAndMenuItemDefinition(
				null, "${Mi_IMAGES_HOME}/right.gif", null, null, null, null, 
				new MiSize(16,16)),
		new MiToolAndMenuItemDefinition(
				Mi_SPECIFY_DATA_VIEW_DISPLAY_NAME,
				Mi_SPECIFY_DATA_VIEW_ICON_NAME,
				Mi_SPECIFY_DATA_VIEW_COMMAND_NAME,
				Mi_SPECIFY_DATA_VIEW_STATUS_HELP_MSG,
				Mi_NO_SPECIFY_DATA_VIEW_MANAGER_STATUS_HELP_MSG,
				Mi_SPECIFY_DATA_VIEW_BALLOON_HELP_MSG),
		};
	// ---------------------------------------------------------------
	// Define the class variables this class will use.
	// ---------------------------------------------------------------
	private		MiDirectoryTreePalette	directoryTreePalette;
	private		MiiModelIOManager 	openIOManager;
	private		MiiModelIOFormatManager 	openIOFormatManager;
	private		MiiViewManager 		openViewManager;
	private		MiiModelEntity 		openModel;
	private		MiToolBar 		dataFlowToolBar;
	private		String			openFilename;
	private		MiDialogBoxTemplate	pipelinePaletteDialog;
	private		MiController		controller;
	private		MiiViewManager		dataSourcePalette;
	private		MiiViewManager		dataFormatPalette;
	private		MiiViewManager		dataMappingPalette;
	private		MiiViewManager		dataViewPalette;
	private		MiiModelIOManager	nextIOManager;
	private		MiiViewManager		nextViewManager;
	private		MiiModelIOFormatManager	nextIOFormatManager;


					/**------------------------------------------------------
	 				 * Constructs a new MiModelBrowserWindow and adds it to the given
					 * AWT container.
					 * @param awtContainer	the AWT container this is going into
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiModelBrowserWindow(Container awtContainer, String title, MiBounds screenSize)
		{
		super(MiUtility.getFrame(awtContainer), 
			title != null ? title : Mi_DEFAULT_WINDOW_BORDER_TITLE, screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiModelBrowserWindow and creates and adds it
					 * to a AWT Frame
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiModelBrowserWindow(String title, MiBounds screenSize)
		{
		super(title != null ? title : Mi_DEFAULT_WINDOW_BORDER_TITLE, screenSize);
		}
					/**------------------------------------------------------
	 				 * Customizes this window based on the default setup provided
					 * by the MiEditorWindow superclass.
					 *------------------------------------------------------*/
	protected	void		setup()
		{
		super.setup();

		directoryTreePalette.populateList();

		controller = new MiController();
		nextIOManager = new MiModelStreamIOManager();

		dataFlowToolBar = MiToolAndMenuItemDefinition.makeToolBar(
					this, this, dataFlowToolBarDef,  new MiSize(32, 32));

		getTopDockingPanel().appendPart(dataFlowToolBar);

		dataSourcePalette = makeDataSourcePalette(Mi_DATA_SOURCE_PALETTE_DEFINITION);
		dataSourcePalette.appendModelChangeHandler(this, 
					Mi_MODEL_CHANGE_COMMIT_PHASE_MASK);

		dataFormatPalette = makeDataFormatPalette(Mi_DATA_FORMAT_PALETTE_DEFINITION);
		dataFormatPalette.appendModelChangeHandler(this, 
					Mi_MODEL_CHANGE_COMMIT_PHASE_MASK);
/*****
		dataMappingPalette = makeDataMapPalette();
		dataMappingPalette.appendModelChangeHandler(this, 
					Mi_MODEL_CHANGE_COMMIT_PHASE_MASK);
*****/
		dataViewPalette = makeDataViewPalette(Mi_DATA_VIEW_PALETTE_DEFINITION);
		dataViewPalette.appendModelChangeHandler(this, 
					Mi_MODEL_CHANGE_COMMIT_PHASE_MASK);
		}
					/**------------------------------------------------------
	 				 * Makes the palette for the window. This is called by
					 * the MiEditorWindow superclass during setup (i.e. 
					 * buildEditorWindow()). Make it invisible to start with.
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultPalette()
		{
		directoryTreePalette = new MiDirectoryTreePalette();
		directoryTreePalette.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		directoryTreePalette.setTypeOfFilesToList(true, true);
		directoryTreePalette.addValidExtension("grml");
		directoryTreePalette.getTreeList().setStatusHelpMessage(Mi_DIRECTORY_TREE_STATUS_MSG);
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
		pushMouseAppearance(Mi_WAIT_CURSOR, "ModelBrowserCommand");
		if ((command.equals(Mi_DISPLAY_BIRDS_EYE_VIEW_COMMAND_NAME))
			|| (command.equals(Mi_HIDE_BIRDS_EYE_VIEW_COMMAND_NAME))
			|| (command.equals(Mi_SHOW_PALETTE_NAME))
			|| (command.equals(Mi_HIDE_PALETTE_COMMAND_NAME)))
			{
			super.processCommand(command);
			}

		if (command.equals(Mi_UPDATE_PIPELINE_COMMAND_NAME))
			{
			updatePipeline();
			}
		else if (command.equals(Mi_SPECIFY_DATA_SOURCE_COMMAND_NAME))
			{
			displayDataSourcePalette();
			}
		else if (command.equals(Mi_SPECIFY_DATA_FORMAT_COMMAND_NAME))
			{
			displayDataFormatPalette();
			}
		else if (command.equals(Mi_SPECIFY_DATA_MAP_COMMAND_NAME))
			{
			displayDataMappingPalette();
			}
		else if (command.equals(Mi_SPECIFY_DATA_VIEW_COMMAND_NAME))
			{
			displayViewManagerPalette();
			}
		popMouseAppearance("ModelBrowserCommand");
		}
	public		boolean		processAction(MiiAction action)
		{
		pushMouseAppearance(Mi_WAIT_CURSOR, "ModelBrowserAction");
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			Strings directories = directoryTreePalette.getSelectedPaths();
			displayModel(directories.elementAt(0));
			}
		popMouseAppearance("ModelBrowserAction");
		return(super.processAction(action));
		}
	public		boolean		openFile(String filename)
		{
		String directory = new File(filename).getParent();
		if (directory == null)
			directory = filename;
		
		directoryTreePalette.openFile(directory);
		directoryTreePalette.setSelectedPaths(new Strings(filename));
		displayModel(filename);
		return(true);
		}
	protected	void		displayModel(String filename)
		{
		super.close();
		openFilename = filename;
		updatePipeline();
		}


	protected	MiiViewManager	makeDataSourcePalette(String filename)
		{
		filename = MiSystem.getPropertyOrKey(filename);
		try	{
			MiiModelEntity model = new MiModelIndentedTreeIOFormatManager().load(
				new BufferedInputStream(new DataInputStream(new FileInputStream(filename)), 
				10000), filename);
			replaceClassNamesWithProperties(model);
			dataSourcePalette = new MiModelPropertyListViewManager(
				MiModelPropertyListViewManager.Mi_FILTER_AND_LIST_USING_TREE_LIST_STYLE);
			dataSourcePalette.setModel(model);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		return(dataSourcePalette);
		}
	protected	MiiViewManager	makeDataFormatPalette(String filename)
		{
		filename = MiSystem.getPropertyOrKey(filename);
		try	{
			MiiModelEntity model = new MiModelIndentedTreeIOFormatManager().load(
				new BufferedInputStream(new DataInputStream(new FileInputStream(filename)), 
				10000), filename);
			replaceClassNamesWithProperties(model);
			dataFormatPalette = new MiModelPropertyListViewManager(
				MiModelPropertyListViewManager.Mi_FILTER_AND_LIST_USING_TREE_LIST_STYLE);
			dataFormatPalette.setModel(model);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		return(dataFormatPalette);
		}
	protected	MiiViewManager	makeDataMapPalette()
		{
		return(null);
		}
	protected	MiiViewManager	makeDataViewPalette(String filename)
		{
		filename = MiSystem.getPropertyOrKey(filename);
		try	{
			MiiModelEntity model = new MiModelIndentedTreeIOFormatManager().load(
				new BufferedInputStream(new DataInputStream(new FileInputStream(filename)), 
				10000), filename);
			replaceClassNamesWithProperties(model);
			dataViewPalette = new MiModelPropertyListViewManager(
				MiModelPropertyListViewManager.Mi_FILTER_AND_LIST_USING_TREE_LIST_STYLE);
			dataViewPalette.setModel(model);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		return(dataViewPalette);
		}

	protected	void		makePaletteDialogBox()
		{
		if (pipelinePaletteDialog == null)
			pipelinePaletteDialog = new MiDialogBoxTemplate(this, "Palettes", false);
		pipelinePaletteDialog.setLayout(new MiColumnLayout());
		}
	protected	void		displayPaletteDialogBox(String title, MiiViewManager viewManager)
		{
		pipelinePaletteDialog.setTitle(title);
		pipelinePaletteDialog.getBox().removeAllParts();
System.out.println("SET VIEW DIALOG BOX = ======================================================= " + pipelinePaletteDialog);
		((MiModelPropertyViewManager )viewManager).setView(pipelinePaletteDialog);
		pipelinePaletteDialog.setVisible(true);
		}

	protected	void		displayDataSourcePalette()
		{
		makePaletteDialogBox();
		displayPaletteDialogBox("Data Input", dataSourcePalette);
		}
	protected	void		displayDataFormatPalette()
		{
		makePaletteDialogBox();
		displayPaletteDialogBox("Data Format", dataFormatPalette);
		}
	protected	void		displayDataMappingPalette()
		{
		makePaletteDialogBox();
		//displayPaletteDialogBox("Data Mapping", dataMappingPalette);
		}

	protected	void		displayViewManagerPalette()
		{
		makePaletteDialogBox();
		displayPaletteDialogBox("Data Views", dataViewPalette);
		}


	protected	void		updatePipeline()
		{
		try	{
			controller.setView(getEditor());
			if (openFilename == null)
				{
				MiToolkit.postErrorDialog(this, "No data file specified");
				return;
				}
			if (nextIOManager == null)
				{
				MiToolkit.postErrorDialog(this, "No data input manager specified");
				return;
				}
			if (nextIOFormatManager == null)
				{
				MiToolkit.postErrorDialog(this, "No data format manager specified");
				return;
				}
			if (nextViewManager == null)
				{
				MiToolkit.postErrorDialog(this, "No data view manager specified");
				return;
				}
			if (nextIOManager != openIOManager)
				{
				openIOManager = nextIOManager;
				controller.setIOManager(openIOManager);
				}
			((MiModelStreamIOManager )openIOManager).setFilename(openFilename);

			if (nextIOFormatManager != openIOFormatManager)
				{
				openIOFormatManager = nextIOFormatManager;
				controller.setIOFormatManager(openIOFormatManager);
				}

			if (nextViewManager != openViewManager)
				{
				openViewManager = nextViewManager;
				controller.setViewManager(openViewManager);
				}
			controller.load();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}

	public		void		processModelChange(MiModelChangeEvent event)
		{
		if (event.getParent() == dataSourcePalette)
			{
			nextIOManager = (MiiModelIOManager )event.getChild();
			}
		else if (event.getParent() == dataFormatPalette)
			{
			nextIOFormatManager = (MiiModelIOFormatManager )event.getChild();
			}
		else if (event.getParent() == dataViewPalette)
			{
System.out.println("event.getChild() = " + event.getChild());

			nextViewManager =  (MiiViewManager )event.getChild();
			}
		}
					// repalceListEntriesWithManagers
	protected	void		replaceClassNamesWithProperties(MiiModelEntity model)
		{
		MiModelEntityList list = model.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity child = list.elementAt(i);
System.out.println("child = " + child);
System.out.println("child.getType() = " + child.getType());
			if (child.getType() == Mi_COMMENT_TYPE)
				continue;

			if (child.getModelEntities().size() == 0)
				{
				String className = child.getPropertyValue("className");
				MiiModelEntity realChild 
					= (MiiModelEntity )Utility.makeInstanceOfClass(className);
System.out.println("realChild = " + realChild);
				if (realChild == null)
					{
System.out.println("\n\n\n**************************************************\n");
					MiDebug.println("Unable to make instance of class: " + className);
					continue;
					}
				Strings propertyNames = child.getPropertyNames();
				for (int j = 0; j < propertyNames.size(); ++j)
					{
					String name = propertyNames.elementAt(j);
					realChild.setPropertyValue(name, child.getPropertyValue(name));
					}
				child.replaceSelf(realChild);
				}
			else
				{
				replaceClassNamesWithProperties(child);
				}
			}
		}
/*****
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == dataViewPalette)
			{
			MiiModelEntity viewManagerSelected = (MiiModelEntity )action.getActionSystemInfo();
			openViewManager.setView(null);
			openViewManager = (MiiViewManager )Utility.makeInstanceOfClass(
						viewManagerSelected.getPropertyValue("className"));
			openViewManager.setModel(openModel);
			openViewManager.setView(getEditor());
			}
		else if (action.getActionSource() == ioFormatManagerPalette)
			{
			MiiModelEntity ioFormatManagerSelected = (MiiModelEntity )action.getActionSystemInfo();
			openIOFormatManager = (MiiModelIOFormatManager )Utility.makeInstanceOfClass(
						ioFormatManagerSelected.getPropertyValue("className"));
			// for all properties in ioFormatManagerSelected, 
			// apply to openIOFormatManager => inst of MiiModelEntity
			openModel = openIOFormatManager.load(
				new BufferedInputStream(Utility.openFile(openFilename), 10000));
			openViewManager.setModel(openModel);
			}
		return(true);
		}
****/
	}
