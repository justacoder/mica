
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
import java.awt.Color; 
import java.util.Vector; 
import java.util.StringTokenizer; 
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Pair;
import java.awt.Container;
import java.applet.Applet;

/*
To do:

	ADD: an option for each rule:
		Include symetricX4 rules (i.e. rule is really 4 rules, 
			each a mirror reflection of another about the center cell)
		Include symetricX8 rules
	ADD: don't care cells
	ADD: something cells (but not nothing)
	ADD: two step rules (3 grids: if a occured then b occured then c. 
		This allows motion and other behavior to be specified)

	USE: pixmap (Image) to render with so can support pixel sized grids
*/

/**----------------------------------------------------------------------------------------------
 * This class implements a game that allows the user to specify cell-based
 * rewrite rules and then animate the result.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiLife extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiLifeWindow. Supported command line parameters
					 * are:
					 * -file		load this file on startup
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String filename = Utility.getCommandLineArgument(args, "-file");
		String title = Utility.getCommandLineArgument(args, "-title");
		MiLifeWindow window = new MiLifeWindow(title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		loadSpecifiedFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiLifeWindow. Supported html file parameters are:
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
		MiLifeWindow window = new MiLifeWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		loadSpecifiedFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * Loads the specified file, if any.
					 * @param window	the window
					 * @param filename	the name of the file or null
					 *------------------------------------------------------*/
	protected static void		loadSpecifiedFile(MiLifeWindow window, String filename)
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
					"Usage: MiLife [-file <filename>] [-title <window title>]");
				System.exit(1);
				}
			}
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements the window of a game that allows the user 
 * to specify cell-based rewrite rules and then animate the result.
 *
 *----------------------------------------------------------------------------------------------*/
class MiLifeWindow extends MiFileEditorWindow implements MiiAnimatable, MiiDisplayNames, MiiNames
	{
	// ---------------------------------------------------------------
	// Define the names of some commands
	// ---------------------------------------------------------------
	private static final String	DISPLAY_GRID_LINES_COMMAND_NAME		= "DisplayGridLines";
	private static final String	HIDE_GRID_LINES_COMMAND_NAME		= "HideGridLines";

	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	
						= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	private static final String	Mi_LIFE_INCREASE_NUMBER_OF_CELLS_NAME
					= "Mi_LIFE_INCREASE_NUMBER_OF_CELLS_MENUITEM_NAME";
	private static final String	Mi_LIFE_REDUCE_NUMBER_OF_CELLS_NAME
					= " Mi_LIFE_REDUCE_NUMBER_OF_CELLS_MENUITEM_NAME";
	private static final String	Mi_LIFE_INCREASE_NUMBER_OF_RULES_NAME
					= "Mi_LIFE_INCREASE_NUMBER_OF_RULES_MENUITEM_NAME";
	private static final String	Mi_LIFE_REDUCE_NUMBER_OF_RULES_NAME
					= "Mi_LIFE_REDUCE_NUMBER_OF_RULES_MENUITEM_NAME";
	private static final String	Mi_LIFE_INCREASE_NUMBER_OF_RULE_CELLS_NAME
					= "Mi_LIFE_INCREASE_NUMBER_OF_RULE_CELLS_MENUITEM_NAME";
	private static final String	Mi_LIFE_REDUCE_NUMBER_OF_RULE_CELLS_NAME
					= "Mi_LIFE_REDUCE_NUMBER_OF_RULE_CELLS_MENUITEM_NAME";
	private static final String	Mi_LIFE_SET_BACKGROUND_COLOR_NAME
					= "Mi_LIFE_SET_BACKGROUND_COLOR_MENUITEM_NAME";
	private static final String	Mi_LIFE_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME	
					= "Mi_LIFE_SET_BACKGROUND_COLOR_TO_PEN_COLOR_MENUITEM_NAME";
	private static final String	Mi_LIFE_DISPLAY_GRID_LINES_NAME
					= "Mi_LIFE_DISPLAY_GRID_LINES_MENUITEM_NAME";
	private static final String	Mi_LIFE_APPLY_RULES_IN_PARALLEL_NAME
					= "Mi_LIFE_APPLY_RULES_IN_PARALLEL_MENUITEM_NAME";
	private static final String	Mi_LIFE_APPLY_RULES_SERIALLY_NAME
					= "Mi_LIFE_APPLY_RULES_SERIALLY_MENUITEM_NAME";
	private static final String	Mi_LIFE_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME
					= "Mi_LIFE_VALIDATE_RULES_ONCE_EACH_GENERATION_MENUITEM_NAME";
	private static final String	Mi_LIFE_VALIDATE_RULES_EACH_USE_NAME
					= "Mi_LIFE_VALIDATE_RULES_EACH_USE_MENUITEM_NAME";
	private static final String	Mi_LIFE_ABOUT_MSG
					= "Mi_LIFE_ABOUT_MSG";
						

	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
	{
	new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE,			"(Re)writing Life"),
	new Pair( Mi_LIFE_INCREASE_NUMBER_OF_CELLS_NAME, 		"More display cells"),
	new Pair( Mi_LIFE_REDUCE_NUMBER_OF_CELLS_NAME, 			"Fewer display cells"),
	new Pair( Mi_LIFE_INCREASE_NUMBER_OF_RULES_NAME, 		"More rules"),
	new Pair( Mi_LIFE_REDUCE_NUMBER_OF_RULES_NAME, 			"Fewer rules"),
	new Pair( Mi_LIFE_INCREASE_NUMBER_OF_RULE_CELLS_NAME,		"More rule cells"),
	new Pair( Mi_LIFE_REDUCE_NUMBER_OF_RULE_CELLS_NAME, 		"Fewer rule cells"),
	new Pair( Mi_LIFE_SET_BACKGROUND_COLOR_NAME, 			"Set background color..."),
	new Pair( Mi_LIFE_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME, 	"Set background color to pen color"),
	new Pair( Mi_LIFE_DISPLAY_GRID_LINES_NAME, 			"Display grid lines"),
	new Pair( Mi_LIFE_APPLY_RULES_IN_PARALLEL_NAME, 		"Apply rules in parallel"),
	new Pair( Mi_LIFE_APPLY_RULES_SERIALLY_NAME, 			"Apply rules serially"),
	new Pair( Mi_LIFE_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME,   "Validate rules once each generation"),
	new Pair( Mi_LIFE_VALIDATE_RULES_EACH_USE_NAME, 		"Validate rules each use"),
	new Pair( Mi_LIFE_ABOUT_MSG,
		"\n\nThis entertainment program is part of the Mica Appletcation Suite.\n\n"
		+ "This version of the classic life program uses graphical rewrite rules\n"
		+ "to generate interesting animated visual effects.\n\n\n"
		+ "Version 1.0      March 3, 1998")
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
	public static final String	Mi_RULE_TYPE_NAME			= "Rule";
	private static final String	Mi_NUMBER_OF_ROWS			= "numberOfRows";
	private static final String	Mi_NUMBER_OF_COLUMNS			= "numberOfColumns";
	private static final String	Mi_APPLY_RULES_IN_PARALLEL_NAME		= "applyRulesInParallel";
	private static final String	Mi_VALIDATE_RULES_EACH_GENERATION_NAME	= "validateRulesOnceEachGeneration";

	private static final String	Mi_LIFE_FILE_HEADER_MSG		= "#MiLife version 1.0";
	private static final String	Mi_LIFE_DEFAULT_FILENAME	= "mica.life";

	private static final int	DEFAULT_NUMBER_OF_ROW_CELLS	= 20;
	private static final int	DEFAULT_NUMBER_OF_COLUMN_CELLS	= 20;


	// ---------------------------------------------------------------
	// Define the class variables this class will use.
	// ---------------------------------------------------------------
	private		Vector		rules				= new Vector();
	private		MiColorGrid	colorGrid;
	private		MiColorTable	colorTable			= new MiColorTable();
	private		MiPlayerPanel	playerPanel;
	private		MiSwatchesColorPalette 	colorPalette;
	private		byte[]		displayGridArray;
	private		byte[]		workingGridArray;
	private		int		numRows				= DEFAULT_NUMBER_OF_ROW_CELLS;
	private		int		numColumns			= DEFAULT_NUMBER_OF_COLUMN_CELLS;
	private		Color		penColor			= MiColorManager.red;
	private		Color		backgroundColor			= MiColorManager.white;
	private		byte		backgroundColorByte;
	private		MiAnimator	animator;
	private		MiEditor	editor;
	private		String		playerPanelState		= "";
	private		MiList 		palette;
	private		boolean 	hasChanged;
	private		boolean 	playingBackward;
	private		boolean 	applyRulesInParallel;
	private		boolean 	validateRulesOnlyOnceEachGeneration;
	private		MiWidget	numGenerationsDisplayField;
	private		int		numGenerations;
	private		MiWidget	numGridCellsDisplayField;
	private		MiColorChooser	colorChooser;




					/**------------------------------------------------------
	 				 * Constructs a new MiLifeWindow.
					 * @param awtContainer	the container to add this to
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiLifeWindow( 
						Container awtContainer, 
						String title, 
						MiBounds screenSize)
		{
		super(MiUtility.getFrame(awtContainer),
			title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, 
			screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE,
			"life", Mi_LIFE_DEFAULT_FILENAME, false);

		awtContainer.add("Center", (java.awt.Component )getCanvas().getNativeComponent());
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiLifeWindow.
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiLifeWindow(String title, MiBounds screenSize)
		{
		super(title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title,
			 screenSize,
			"life", Mi_LIFE_DEFAULT_FILENAME, false);
		setup();
		}
					/**------------------------------------------------------
	 				 * Setup the window, creating the menubar, status bar,
					 * palette for the rules, the grid for the cells... 
					 *------------------------------------------------------*/
	protected 	void		setup()
		{
		MiSystem.loadPropertiesFile("MiLife.mica");

		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// Let the MiEditorWindow superclass build the default window.
		// ---------------------------------------------------------------
		buildEditorWindow(true, true, false, true, true, true, null, null, null, null, null);

		// ---------------------------------------------------------------
		// Make the entire window double buffered...
		// ---------------------------------------------------------------
		getRootWindow().getCanvas().setDoubleBuffered(true);
		editor = getEditor();

		// ---------------------------------------------------------------
		// Add interactivity to the display grid...
		// ---------------------------------------------------------------
		editor.appendEventHandler(new MiIDragBackgroundPan());
		editor.appendEventHandler(new MiIZoomAroundMouse());
		editor.appendEventHandler(new MiIPan());
		editor.appendEventHandler(new MiIJumpPan());
		editor.appendEventHandler(new MiIZoomArea());
		editor.setBackgroundColor(MiColorManager.lightGray);

		setup2();
		}
					/**------------------------------------------------------
	 				 * Makes the palette. This is called from the super class's
					 * buildEditorWindow method. For this application this is the
					 * list of rules.
					 * @return		the rule list
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultPalette()
		{
		palette = new MiList(16, false);
		palette.setAlleyVSpacing(4);
		palette.setMinimumNumberOfVisibleRows(4);
		palette.setPreferredNumberOfVisibleRows(4);
		palette.setNumberOfVisibleCharactersWide(0);
		palette.getSelectionManager().setBrowsable(false);
		palette.getSelectionManager().setMaximumNumberSelected(0);
		addRule();
		addRule();
		addRule();
		return(new MiScrolledBox(palette));
		}
					/**------------------------------------------------------
	 				 * Makes the main panel. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the main panel
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultEditorPanel()
		{
		MiPart panel = super.makeDefaultEditorPanel();
		getEditor().setPreferredSize(new MiSize(400, 400));
		MiColumnLayout layout = new MiColumnLayout();
		layout.appendPart(panel);
		playerPanel = new MiPlayerPanel();
		playerPanel.appendActionHandler(this, MiPlayerPanel.Mi_PLAYER_PANEL_ACTION);
		layout.appendPart(playerPanel);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		return(layout);
		}
					/**------------------------------------------------------
	 				 * Makes the status bar. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the status bar
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultStatusBar()
		{
		MiEditorStatusBar statusBar = new MiEditorStatusBar();
		statusBar.setOverlayStatusField(new MiStatusBarFocusStatusField(this, ".30", 0));
		MiStatusBar gridSizeBar = new MiBasicStatusField(".20\n.18");
		numGenerationsDisplayField = gridSizeBar.getField(0);
		displayNumberOfGenerations();
		numGridCellsDisplayField = gridSizeBar.getField(1);
		displayNumberOfGridCells();
		statusBar.appendPart(gridSizeBar);
		statusBar.appendPart(new MiMagnificationStatusField(getEditor()));
		return(statusBar);
		}
					/**------------------------------------------------------
	 				 * This method is called to create the graphics for this
					 * MiWindow when this window is first created. This 
					 * approach to delaying the creation of graphics until
					 * after the window is created is optional. This method
					 * builds the display grid and customizes the menubar.
					 * @overrides		MiEditorWindow#createGraphicsContents
					 *------------------------------------------------------*/
	public		 void		setup2()
		{
		colorPalette = new MiSwatchesColorPalette(
					1, MiColorManager.getNumberOfColors(), new MiSize(16,16));
		colorPalette.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		colorPalette.setSelection(penColor);
		appendPart(colorPalette);

		workingGridArray = new byte[DEFAULT_NUMBER_OF_ROW_CELLS * DEFAULT_NUMBER_OF_COLUMN_CELLS];

		colorGrid = new MiColorGrid(DEFAULT_NUMBER_OF_ROW_CELLS, DEFAULT_NUMBER_OF_COLUMN_CELLS);
		colorGrid.setColorTable(colorTable);
		setGridBackgroundColor(MiColorManager.white);
		displayGridArray = colorGrid.getGrid();
		colorGrid.setIsDrawingSeparatorLines(true);
		colorGrid.appendEventHandler(new MiGridEventHandler(colorGrid, this));
		colorGrid.setBounds(new MiBounds(0, 0, 500, 500));
		editor.setWorldBounds(new MiBounds(0, 0, 500, 500));
		editor.setUniverseBounds(new MiBounds(0, 0, 500, 500));
		editor.appendPart(colorGrid);

		animator = new MiAnimator(this, 100);
		animator.setEnabled(false);
		MiAnimationManager.addAnimator(this, animator);

		MiEditorMenu formatMenu = getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME);
		formatMenu.appendSeparator();
		formatMenu.appendMenuItem(Mi_LIFE_INCREASE_NUMBER_OF_CELLS_NAME, this);
		formatMenu.appendMenuItem(Mi_LIFE_REDUCE_NUMBER_OF_CELLS_NAME, this);
		formatMenu.appendSeparator();
		formatMenu.appendMenuItem(Mi_LIFE_INCREASE_NUMBER_OF_RULES_NAME, this);
		formatMenu.appendMenuItem(Mi_LIFE_REDUCE_NUMBER_OF_RULES_NAME, this);
		formatMenu.appendSeparator();
		formatMenu.appendMenuItem(Mi_LIFE_INCREASE_NUMBER_OF_RULE_CELLS_NAME, this);
		formatMenu.appendMenuItem(Mi_LIFE_REDUCE_NUMBER_OF_RULE_CELLS_NAME, this);
		formatMenu.appendSeparator();
		formatMenu.appendMenuItem(Mi_LIFE_SET_BACKGROUND_COLOR_NAME, this);
		formatMenu.appendMenuItem(Mi_LIFE_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME, this);
		formatMenu.appendMenuItem(Mi_CLEAR_COMMAND_NAME, this);
		

		MiEditorMenu optionMenu = new MiEditorMenu("&Options", this);
		optionMenu.appendBooleanMenuItem(Mi_LIFE_DISPLAY_GRID_LINES_NAME, this, 
				DISPLAY_GRID_LINES_COMMAND_NAME, HIDE_GRID_LINES_COMMAND_NAME);
		optionMenu.getMenuItemWithCommand(DISPLAY_GRID_LINES_COMMAND_NAME).select(true);
		optionMenu.appendSeparator();

		optionMenu.startRadioButtonSection();
		optionMenu.appendBooleanMenuItem(Mi_LIFE_APPLY_RULES_IN_PARALLEL_NAME, this);
		optionMenu.appendBooleanMenuItem(Mi_LIFE_APPLY_RULES_SERIALLY_NAME, this);
		optionMenu.getMenuItemWithCommand(Mi_LIFE_APPLY_RULES_SERIALLY_NAME).select(true);
		optionMenu.endRadioButtonSection();

		optionMenu.appendSeparator();
		optionMenu.startRadioButtonSection();
		optionMenu.appendBooleanMenuItem(Mi_LIFE_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME, this);
		optionMenu.appendBooleanMenuItem(Mi_LIFE_VALIDATE_RULES_EACH_USE_NAME, this);
		optionMenu.getMenuItemWithCommand(Mi_LIFE_VALIDATE_RULES_EACH_USE_NAME).select(true);
		optionMenu.endRadioButtonSection();
		getMenuBar().appendPulldownMenu(optionMenu);


		getMenuBarButton(getMenuBar().getMenu(Mi_TOOLS_MENU_DISPLAY_NAME)).setVisible(false);
		getMenuBarButton(getMenuBar().getMenu(Mi_SHAPE_MENU_DISPLAY_NAME)).setVisible(false);
		getMenuBarButton(getMenuBar().getMenu(Mi_CONNECT_MENU_DISPLAY_NAME)).setVisible(false);
		getMenuBarButton(getMenuBar().getMenu(Mi_EDIT_MENU_DISPLAY_NAME)).setVisible(false);

		getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME)
			.removeMenuItemWithCommand(Mi_FORMAT_COMMAND_NAME);

		MiEditorMenu menu = getMenuBar().getMenu(Mi_FILE_MENU_DISPLAY_NAME);
		menu.removeAllMenuItemsWithCommandsExcept(new Strings(
				Mi_NEW_COMMAND_NAME
			 + "\n" + Mi_QUIT_COMMAND_NAME
			 + "\n" + Mi_CLOSE_COMMAND_NAME
			 + "\n" + Mi_SAVE_COMMAND_NAME
			 + "\n" + Mi_OPEN_COMMAND_NAME
			 + "\n" + Mi_SAVE_AS_COMMAND_NAME));

		getMenuBar().removePulldownMenu(Mi_HELP_MENU_DISPLAY_NAME);
		menu = new MiHelpMenu(this, this);
		menu.removeAllMenuItemsWithCommandsExcept(new Strings(
					Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME));
		getMenuBar().appendPulldownMenu(menu);

		setHasChanged(false);
		}
					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are:
					 * <pre>
					 *    Mi_LIFE_INCREASE_NUMBER_OF_CELLS_NAME
					 *    Mi_LIFE_REDUCE_NUMBER_OF_CELLS_NAME
					 *    Mi_LIFE_INCREASE_NUMBER_OF_RULE_CELLS_NAME
					 *    Mi_LIFE_REDUCE_NUMBER_OF_RULE_CELLS_NAME
					 *    Mi_LIFE_INCREASE_NUMBER_OF_RULES_NAME
					 *    Mi_LIFE_REDUCE_NUMBER_OF_RULES_NAME
					 *    Mi_LIFE_SET_BACKGROUND_COLOR_NAME
					 *    Mi_LIFE_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME
					 *    DISPLAY_GRID_LINES_COMMAND_NAME
					 *    HIDE_GRID_LINES_COMMAND_NAME
					 *    Mi_CLEAR_COMMAND_NAME
					 *    Mi_LIFE_APPLY_RULES_IN_PARALLEL_NAME
					 *    Mi_LIFE_APPLY_RULES_SERIALLY_NAME
					 *    Mi_LIFE_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME
					 *    Mi_LIFE_VALIDATE_RULES_EACH_USE_NAME
					 *    Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 * @overrides		MiEditorWindow#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		if (command.equalsIgnoreCase(Mi_LIFE_INCREASE_NUMBER_OF_CELLS_NAME))
			{
			numRows *= 2;
			numColumns *= 2;
			setNumberOfRowsAndColumns(numRows, numColumns);
			}
		else if (command.equalsIgnoreCase(Mi_LIFE_REDUCE_NUMBER_OF_CELLS_NAME))
			{
			if ((numRows > DEFAULT_NUMBER_OF_ROW_CELLS) && (numColumns >= DEFAULT_NUMBER_OF_COLUMN_CELLS))
				{
				numRows /= 2;
				numColumns /= 2;
				setNumberOfRowsAndColumns(numRows, numColumns);
				}
			}
		else if (command.equalsIgnoreCase(Mi_LIFE_INCREASE_NUMBER_OF_RULE_CELLS_NAME))
			{
			++MiRule.RULE_ROW_SIZE;
			++MiRule.RULE_COLUMN_SIZE;
			changeNumberOfGridsPerRule(MiRule.RULE_ROW_SIZE, MiRule.RULE_COLUMN_SIZE);
			setHasChanged(true);
			}
		else if (command.equalsIgnoreCase(Mi_LIFE_REDUCE_NUMBER_OF_RULE_CELLS_NAME))
			{
			if ((MiRule.RULE_ROW_SIZE > 3) && (MiRule.RULE_COLUMN_SIZE > 3))
				{
				MiRule.RULE_ROW_SIZE /= 2;
				MiRule.RULE_COLUMN_SIZE /= 2;
				changeNumberOfGridsPerRule(MiRule.RULE_ROW_SIZE, MiRule.RULE_COLUMN_SIZE);
				}
			setHasChanged(true);
			}
		else if (command.equalsIgnoreCase(Mi_LIFE_INCREASE_NUMBER_OF_RULES_NAME))
			{
			addRule();
			}
		else if (command.equalsIgnoreCase(Mi_LIFE_REDUCE_NUMBER_OF_RULES_NAME))
			{
			if (palette.getNumberOfItems() > 1)
				{
				palette.removeItem(palette.getNumberOfItems() - 1);
				rules.removeElementAt(rules.size() - 1);
				}
			}
		else if (command.equalsIgnoreCase(Mi_LIFE_SET_BACKGROUND_COLOR_NAME))
			{
			if (colorChooser == null)
				colorChooser = new MiColorChooser(this);
			Color c = colorChooser.popupAndWaitForClose();
			if (c != null)
				{
				colorTable.addColorIfNotPresent(c);
				setGridBackgroundColor(c);
				}
			setHasChanged(true);
			}
		else if (command.equalsIgnoreCase(Mi_LIFE_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME))
			{
			setGridBackgroundColor(getPenColor());
			}
		else if (command.equalsIgnoreCase(DISPLAY_GRID_LINES_COMMAND_NAME))
			{
			colorGrid.setIsDrawingSeparatorLines(true);
			colorGrid.invalidateArea();
			setHasChanged(true);
			}
		else if (command.equalsIgnoreCase(HIDE_GRID_LINES_COMMAND_NAME))
			{
			colorGrid.setIsDrawingSeparatorLines(false);
			colorGrid.invalidateArea();
			setHasChanged(true);
			}
		else if (command.equalsIgnoreCase(Mi_CLEAR_COMMAND_NAME))
			{
			colorGrid.clearGrid(backgroundColorByte);
			numGenerations = 0;
			displayNumberOfGenerations();
			}
		else if (command.startsWith(Mi_LIFE_APPLY_RULES_IN_PARALLEL_NAME))
			{
			applyRulesInParallel = true;
			setHasChanged(true);
			}
		else if (command.startsWith(Mi_LIFE_APPLY_RULES_SERIALLY_NAME))
			{
			setHasChanged(true);
			applyRulesInParallel = false;
			setHasChanged(true);
			}
		else if (command.startsWith(Mi_LIFE_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME))
			{
			validateRulesOnlyOnceEachGeneration = true;
			setHasChanged(true);
			}
		else if (command.startsWith(Mi_LIFE_VALIDATE_RULES_EACH_USE_NAME))
			{
			validateRulesOnlyOnceEachGeneration = false;
			setHasChanged(true);
			}
		else if (command.startsWith(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME))
			{
			MiToolkit.postAboutDialog(
				this, getDefaultWindowTitle(), 
				MiSystem.getCompanyLogo(), Mi_LIFE_ABOUT_MSG, false);
			}
		else
			{
			super.processCommand(command);
			}
		setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		}
	protected	void		prepareToLoadDefaultFile()
		{
		}
					/**------------------------------------------------------
	 				 * Opens the default, no name, file that the user can
					 * edit in but which needs to be named before saving.
					 * @overrides		MiFileEditorWindow#defaultFileOpened
					 *------------------------------------------------------*/
	protected	void		defaultFileOpened()
		{
		colorGrid.clearGrid(backgroundColorByte);
		for (int i = 0; i < rules.size(); ++i)
			{
			MiRule rule = (MiRule )rules.elementAt(i);
			rule.clear(backgroundColorByte);
			}
		numGenerations = 0;
		displayNumberOfGenerations();
		}
					/**------------------------------------------------------
	 				 * Sets whether this window has changed. This is set to
					 * false by the MiFileEditorWindow after a save or open.
					 * @param flag		true if this has changed.
					 * @overrides		MiFileEditorWindow#setHasChanged
					 *------------------------------------------------------*/
	public		void		setHasChanged(boolean flag)
		{
		hasChanged = flag;
		setCommandSensitivity(Mi_SAVE_COMMAND_NAME, flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether this window has changed. This is checked
					 * by the MiFileEditorWindow before a open, close or new.
					 * @return 		true if this has changed.
					 * @overrides		MiFileEditorWindow#getHasChanged
					 *------------------------------------------------------*/
	protected	boolean		getHasChanged()
		{
		return(hasChanged);
		}
					/**------------------------------------------------------
	 				 * Saves the contents of the graphics editor.
					 * @param stream 	where to save the contents
					 * @overrides		MiFileEditorWindow#save
					 *------------------------------------------------------*/
	public		boolean		save(OutputStream stream, String streamName)
		{
		setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		MiiModelDocument document = new MiModelDocument();

		MiiModelEntity entity = document.createModelEntity();

		entity.setPropertyValue(Mi_TYPE_NAME, Mi_DOCUMENT_TYPE_NAME);
		entity.setPropertyValue(Mi_NUMBER_OF_ROWS, "" + numRows);
		entity.setPropertyValue(Mi_NUMBER_OF_COLUMNS, "" + numColumns);
		entity.setPropertyValue(Mi_BACKGROUND_COLOR_ATT_NAME, 
			MiColorManager.getColorName(backgroundColor));
		entity.setPropertyValue(Mi_APPLY_RULES_IN_PARALLEL_NAME, 
			Utility.toString(applyRulesInParallel));
		entity.setPropertyValue(Mi_VALIDATE_RULES_EACH_GENERATION_NAME, 
			Utility.toString(validateRulesOnlyOnceEachGeneration));
		document.appendModelEntity(entity);

		for (int i = 0; i < rules.size(); ++i)
			{
			MiRule rule = (MiRule )rules.elementAt(i);
			if (!rule.isEmpty())
				{
				entity = rule.toEntity(colorTable);
				document.appendModelEntity(entity);
				}
			}
		MiModelIOFormatManager ioFormatManager = new MiModelIOFormatManager();
		ioFormatManager.save(document, stream, Mi_LIFE_FILE_HEADER_MSG);
		setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		return(true);
		}
					/**------------------------------------------------------
	 				 * Loads the contents of the graphics editor from the given
					 * stream. The first line of the stream is examined to 
					 * determine the format of the file.
					 * @param stream 	where to get the contents
					 * @overrides		MiFileEditorWindow#load
					 *------------------------------------------------------*/
	public		void		load(BufferedInputStream inputStream, String streamName) throws IOException
		{
		setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		MiModelIOFormatManager ioFormatManager = new MiModelIOFormatManager();
		MiiModelDocument document = ioFormatManager.load(inputStream, streamName);
		MiModelEntityList list = document.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			if (entity.getType() == MiiModelTypes.Mi_COMMENT_TYPE)
				{
				}
			else if (document.equals(entity.getPropertyValue(Mi_TYPE_NAME), Mi_RULE_TYPE_NAME))
				{
				MiRule rule = addRule();
				rule.fromEntity(entity, colorTable);
				}
			else if (document.equals(entity.getPropertyValue(Mi_TYPE_NAME), Mi_DOCUMENT_TYPE_NAME))
				{
				setNumberOfRowsAndColumns(
					Utility.toInteger(entity.getPropertyValue(Mi_NUMBER_OF_ROWS)),
					Utility.toInteger(entity.getPropertyValue(Mi_NUMBER_OF_COLUMNS)));

				Color bgColor = MiColorManager.getColor(
					entity.getPropertyValue(Mi_BACKGROUND_COLOR_ATT_NAME));
				setGridBackgroundColor(bgColor);

				applyRulesInParallel = Utility.toBoolean(
					entity.getPropertyValue(Mi_APPLY_RULES_IN_PARALLEL_NAME));
				setCommandState(Mi_LIFE_APPLY_RULES_IN_PARALLEL_NAME, 
					applyRulesInParallel);

				validateRulesOnlyOnceEachGeneration = Utility.toBoolean(
					entity.getPropertyValue(Mi_VALIDATE_RULES_EACH_GENERATION_NAME));
				setCommandState(Mi_LIFE_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME, 
					validateRulesOnlyOnceEachGeneration);
				}
			}
		setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		}
					/**------------------------------------------------------
	 				 * Displays the current number of generations in the status
					 * bar.
					 *------------------------------------------------------*/
	protected	void		displayNumberOfGenerations()
		{
		numGenerationsDisplayField.setValue("Generation: " + numGenerations);
		}
					/**------------------------------------------------------
	 				 * Displays the current number of grid cells in the status
					 * bar.
					 *------------------------------------------------------*/
	protected	void		displayNumberOfGridCells()
		{
		numGridCellsDisplayField.setValue("Cells: " + numColumns + " x " + numRows);
		}
					/**------------------------------------------------------
	 				 * Gets the color table that maps grid byte values to colors.
					 * @return		the color table
					 *------------------------------------------------------*/
	public		MiColorTable	getColorTable()
		{
		return(colorTable);
		}
					/**------------------------------------------------------
	 				 * Closes the currently open file.
					 * @overrides		MiFileEditorWindow#close
					 *------------------------------------------------------*/
	public		void		close()
		{
		rules.removeAllElements();
		palette.removeAllItems();
		}
					/**------------------------------------------------------
	 				 * Adds a empty rule.
					 * @returns		the rule that was added
					 *------------------------------------------------------*/
	private		MiRule		addRule()
		{
		MiRule rule = new MiRule(this);
		rules.addElement(rule);
		palette.appendItem(rule.getGraphics());
		rule.clear(backgroundColorByte);
		return(rule);
		}
					/**------------------------------------------------------
	 				 * Sets the number of cells on the main grid to the given number
					 * of rows and columns.
					 * @param rows		the new number of rows
					 * @param columns	the new number of columns
					 *------------------------------------------------------*/
	protected	void		setNumberOfRowsAndColumns(int numRows, int numColumns)
		{
		colorGrid.setNumberOfRowsAndColumns(numRows, numColumns);
		displayGridArray = colorGrid.getGrid();
		workingGridArray = new byte[numRows * numColumns];
		colorGrid.clearGrid(backgroundColorByte);
		displayNumberOfGridCells();
		setHasChanged(true);
		}
					/**------------------------------------------------------
	 				 * Changes the number of grids per rule to the given number
					 * of rows and columns.
					 * @param rows		the new number of rows
					 * @param columns	the new number of columns
					 *------------------------------------------------------*/
	private		void		changeNumberOfGridsPerRule(int rows,int columns)
		{
		for (int i = 0; i < rules.size(); ++i)
			{
			((MiRule )rules.elementAt(i)).setNumberOfRowsAndColumns(rows, columns);
			((MiRule )rules.elementAt(i)).clear(backgroundColorByte);
			}
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are:
					 * <pre>
					 *    MiPlayerPanel.Mi_PLAYER_PANEL_ACTION
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		playingBackward = false;
		if (action.getActionSource() == colorPalette)
			{
			penColor = colorPalette.getSelection();
			}
		else if (action.hasActionType(MiPlayerPanel.Mi_PLAYER_PANEL_ACTION))
			{
			String state = playerPanel.getState();
			if ((state.equalsIgnoreCase(MiPlayerPanel.PREVIOUS_TRACK))
				|| (state.equalsIgnoreCase(MiPlayerPanel.NEXT_TRACK)))
				{
				animator.setEnabled(false);
				colorGrid.clearGrid(backgroundColorByte);
				numGenerations = 0;
				displayNumberOfGenerations();
				}
			else if (state.equalsIgnoreCase(MiPlayerPanel.FAST_REWIND))
				{
				playingBackward = true;
				animator.setEnabled(true);
				}
			else if (state.equalsIgnoreCase(MiPlayerPanel.REWIND))
				{
				playingBackward = true;
				animator.setEnabled(true);
				}
			else if (state.equalsIgnoreCase(MiPlayerPanel.PREVIOUS_FRAME))
				{
				animator.setEnabled(false);
				playingBackward = true;
				if (evolve() > 0)
					{
					--numGenerations;
					displayNumberOfGenerations();
					colorGrid.invalidateArea();
					}
				playingBackward = false;
				}
			else if ((state.equalsIgnoreCase(MiPlayerPanel.STOP))
				|| (state.equalsIgnoreCase(MiPlayerPanel.PAUSE)))
				{
				animator.setEnabled(false);
				}
			else if (state.equalsIgnoreCase(MiPlayerPanel.NEXT_FRAME))
				{
				animator.setEnabled(false);
				if (evolve() > 0)
					{
					++numGenerations;
					displayNumberOfGenerations();
					colorGrid.invalidateArea();
					}
				}
			else if (state.equalsIgnoreCase(MiPlayerPanel.PLAY))
				{
				animator.setEnabled(true);
				}
			else if (state.equalsIgnoreCase(MiPlayerPanel.FAST_FORWARD))
				{
				animator.setEnabled(true);
				}
			playerPanelState = playerPanel.getState();
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Gets the current display grid pen color.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getPenColor()
		{
		return(penColor);
		}
					/**------------------------------------------------------
	 				 * Gets the current display grid background color.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getGridBackgroundColor()
		{
		return(backgroundColor);
		}
					/**------------------------------------------------------
	 				 * Sets the current display grid background color.
					 * @param color		the color
					 *------------------------------------------------------*/
	public		void		setGridBackgroundColor(Color color)
		{
		backgroundColor = color;
		byte oldBackgroundColorByte = backgroundColorByte;
		backgroundColorByte = colorGrid.getByteValueOfColor(color);
		colorGrid.clearGrid(color);
		for (int i = 0; i < rules.size(); ++i)
			{
			((MiRule )rules.elementAt(i)).setBackgroundColor(oldBackgroundColorByte, backgroundColorByte);
			}
		}
					/**------------------------------------------------------
	 				 * Initialize before animation.
					 * @implements		MiiAnimatable#start
					 *------------------------------------------------------*/
	public		void		start()
		{
		}
					/**------------------------------------------------------
					 * Animates for the given time slice. The beginning and
					 * end are normalized (0.0 to 1.0), 0.0 being the start
					 * of the animation and 1.0 the end. This method ignores
					 * the start and end.
					 * @param startOfStep	the beginning of the time slice
					 * @param endOfStep	the end of the time slice
					 * @implements		MiiAnimatable#animate
					 *------------------------------------------------------*/
	public		void		animate(double startOfStep, double endOfStep)
		{
//System.out.println("animate play");
		int numRuleExecutions = 0;
		if (playerPanelState.equals(MiPlayerPanel.FAST_FORWARD))
			{
			numRuleExecutions += evolve();
			numRuleExecutions += evolve();
			numRuleExecutions += evolve();
			numGenerations += 3;
			}
		numRuleExecutions += evolve();
		if (numRuleExecutions > 0)
			{
			colorGrid.invalidateArea();
			++numGenerations;
			}
		displayNumberOfGenerations();
		}
					/**------------------------------------------------------
	 				 * Clean up after animation.
					 * @implements		MiiAnimatable#end
					 *------------------------------------------------------*/
	public		void		end()
		{
		}

	//***************************************************************************************
	//		Application Code Section
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Evolve one generation. Matches each group of cells of 
					 * the current state against rules, and if a match is found,
					 * applies the rules.
					 *------------------------------------------------------*/
	private		int		evolve()
		{
		int modified = 0;

		if (applyRulesInParallel)
			System.arraycopy(displayGridArray, 0, workingGridArray, 0, workingGridArray.length);
		for (int i = 0; i < rules.size(); ++i)
			{
			MiRule rule = (MiRule )rules.elementAt(i);
			if ((rule.isEnabled()) && (!rule.isEmpty()) 
				&& ((!validateRulesOnlyOnceEachGeneration) || rule.isValid()))
				{
				if (!applyRulesInParallel)
					{
					System.arraycopy(
						displayGridArray, 0, workingGridArray, 0, 
						workingGridArray.length);
					}
				for (int row = 0; row < numRows; ++row)
					{
					for (int column = 0; column < numColumns; ++column)
						{
						if ((row < numRows - rule.numRows + 1)
							&& (column < numColumns - rule.numColumns + 1))
							{
							if ((validateRulesOnlyOnceEachGeneration) 
								|| (rule.isValid()))
								{
								if (matches(rule, row, column))
									{
									modified += 
										processCommandRule(
										rule, row, column);
									}
								}
							}
						}
					}
				}
			}
		return(modified);
		}
					/**------------------------------------------------------
	 				 * Matches the group of cells of the current state at the
					 * given row, column against the preconditions of the given
					 * rule.
					 * @param rule		the rule to check
					 * @param row		the row in the main grid of cells
					 * @param column	the column in the main grid of cells
					 *------------------------------------------------------*/
	private		boolean		matches(MiRule rule, int row, int column)
		{
		int index = 0;
		int windex = row * numColumns + column;
		int width = rule.numColumns;
		int height = rule.numRows;
		byte[] pattern = rule.sourcePattern;
		if (playingBackward)
			pattern = rule.resultPattern;

		if (rule.matchWholeRegion)
			{
			for (int i = 0; i < height; ++i)
				{
				for (int j = 0; j < width; ++j)
					{
					if (pattern[index] != workingGridArray[windex + j])
						return(false);
					++index;
					}
				windex += numColumns;
				}
			}
		else
			{
			for (int i = 0; i < height; ++i)
				{
				for (int j = 0; j < width; ++j)
					{
					if ((pattern[index] != backgroundColorByte)
						&& (pattern[index] != workingGridArray[windex + j]))
						{
						return(false);
						}
					++index;
					}
				windex += numColumns;
				}
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Executes the given rule on the group of cells of the 
					 * current state at the given row, column.
					 * @param rule		the rule to execute
					 * @param row		the row in the main grid of cells
					 * @param column	the column in the main grid of cells
					 *------------------------------------------------------*/
	private		int		processCommandRule(MiRule rule, int row, int column)
		{
//System.out.println("EXECUTE RULE: " + rule + ", row = " + row + ", column = " + column);
		int index = 0;
		int windex = row * numColumns + column;
		int width = rule.numColumns;
		int height = rule.numRows;
		int modified = 0;
		byte[] sourcePattern = rule.sourcePattern;
		byte[] resultPattern = rule.resultPattern;
		if (playingBackward)
			{
			sourcePattern = rule.resultPattern;
			resultPattern = rule.sourcePattern;
			}
		for (int i = 0; i < height; ++i)
			{
			for (int j = 0; j < width; ++j)
				{
				byte c = resultPattern[index];
				if ((c != backgroundColorByte) 
					|| (sourcePattern[index] != backgroundColorByte))
					{
					if (displayGridArray[windex + j] != c)
						{
						displayGridArray[windex + j] = c;
						++modified;
//System.out.println("APPLYING RESULT COLOR: " + resultPattern[index]);
						}
					}
				++index;
				}
			windex += numColumns;
			}
		return(modified);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a rule that consists of two groups of cells,
 * the first the precondition and the second the result.
 *
 *----------------------------------------------------------------------------------------------*/
class MiRule implements MiiActionHandler, MiiActionTypes, MiiTypes, MiiNames
	{
	private static final String	Mi_NUMBER_OF_ROWS_NAME		= "numberOfRows";
	private static final String	Mi_NUMBER_OF_COLUMNS_NAME	= "numberOfColumns";
	private static final String	Mi_MATCH_WHOLE_REGION_NAME	= "matchWholeRegion";
	private static final String	Mi_FREQUENCY_OF_VALIDITY_NAME	= "frequencyOfValidity";
	private static final String	Mi_SOURCE_PATTERN_NAME		= "sourcePattern";
	private static final String	Mi_RESULT_PATTERN_NAME		= "resultPattern";

	public static 	int		RULE_ROW_SIZE		= 3;
	public static 	int		RULE_COLUMN_SIZE	= 3;

	private static final String[]	percentOfTimeWhenValidOptions =
		{ "100%", "75%", "66%", "50%", "33%", "25%", "10%", "5%", "0%" };

	protected	byte[]		sourcePattern	= new byte[RULE_ROW_SIZE * RULE_COLUMN_SIZE];
	protected	byte[]		resultPattern	= new byte[RULE_ROW_SIZE * RULE_COLUMN_SIZE];
	protected	int		numRows			= RULE_ROW_SIZE;
	protected	int		numColumns		= RULE_COLUMN_SIZE;
	private		MiComboBox	percentOfTimeWhenValidField;
	private		double		percentOfTimeWhenValid	= 100.0;
	private		boolean		isEmpty			= true;
	private		boolean		isEnabled		= true;
	protected	boolean		matchWholeRegion;
	private		boolean		isUpdatingUI;

	private		MiColorGrid	sourceGrid;
	private		MiColorGrid	resultGrid;
	private		MiToggleButton	matchWholeRegionToggle;
	private		MiPart		graphics;
	private		MiLifeWindow	lifeEditor;



					/**------------------------------------------------------
	 				 * Constructs a new MiRule.
					 * @param lifeEditor	the main game object
					 *------------------------------------------------------*/
	public				MiRule(MiLifeWindow lifeEditor)
		{
		this.lifeEditor = lifeEditor;
		graphics = createRuleGrid(lifeEditor);
		}
					/**------------------------------------------------------
	 				 * Gets the graphics representing this Rule.
					 * @return 		the graphics
					 *------------------------------------------------------*/
	public		MiPart		getGraphics()
		{
		return(graphics);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiRule entry suitable for adding to
					 * the list, including toggle buttons and other fields to
					 * specifiy options.
					 * @param lifeEditor	the main game object
					 *------------------------------------------------------*/
	private		MiPart		createRuleGrid(MiLifeWindow lifeEditor)
		{
		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_RAISED_BORDER_LOOK);

		MiColumnLayout 	column 	= new MiColumnLayout();
		container.setLayout(column);
		MiRowLayout 	row 	= new MiRowLayout();
		row.setAlleyHSpacing(5);
		sourceGrid 		= new MiColorGrid(MiRule.RULE_ROW_SIZE, MiRule.RULE_COLUMN_SIZE);
		sourceGrid.setColorTable(lifeEditor.getColorTable());
		sourceGrid.setBounds(0,0,50,50);
		sourceGrid.appendEventHandler(new MiGridEventHandler(sourceGrid, lifeEditor));
		sourceGrid.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		sourceGrid.setIsDrawingSeparatorLines(true);
		row.appendPart(sourceGrid);
		resultGrid 		= new MiColorGrid(MiRule.RULE_ROW_SIZE, MiRule.RULE_COLUMN_SIZE);
		resultGrid.setColorTable(lifeEditor.getColorTable());
		resultGrid.setBounds(0,0,50,50);
		resultGrid.appendEventHandler(new MiGridEventHandler(resultGrid, lifeEditor));
		resultGrid.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		resultGrid.setIsDrawingSeparatorLines(true);
		row.appendPart(resultGrid);
		container.appendPart(row);

		matchWholeRegionToggle = new MiToggleButton();
		matchWholeRegionToggle.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		MiLabeledWidget labeledWidget = new MiLabeledWidget(matchWholeRegionToggle, "Match region");
		labeledWidget.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		container.appendPart(labeledWidget);

		percentOfTimeWhenValidField = new MiComboBox();
		percentOfTimeWhenValidField.getTextField().setNumDisplayedColumns(4);
		percentOfTimeWhenValidField.setRestrictingValuesToThoseInList(false);
		percentOfTimeWhenValidField.setValue("100%");
		percentOfTimeWhenValidField.getList().getSortManager().setEnabled(false);
		for (int i = 0; i < percentOfTimeWhenValidOptions.length; ++i)
			percentOfTimeWhenValidField.getList().appendItem(percentOfTimeWhenValidOptions[i]);

		percentOfTimeWhenValidField.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		labeledWidget = new MiLabeledWidget(percentOfTimeWhenValidField, "Validity");
		labeledWidget.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		container.appendPart(labeledWidget);

		return(container);
		}
					/**------------------------------------------------------
	 				 * Sets the number of rows and columns for this rule.
					 * @param rows		the number of rows
					 * @param columns	the number of columns
					 *------------------------------------------------------*/
	public		void		setNumberOfRowsAndColumns(int rows, int columns)
		{
		isUpdatingUI = true;
		sourceGrid.setNumberOfRowsAndColumns(rows, columns);
		resultGrid.setNumberOfRowsAndColumns(rows, columns);
		numRows = rows;
		numColumns = columns;
		sourcePattern = new byte[numRows * numColumns];
		resultPattern = new byte[numRows * numColumns];
		isUpdatingUI = false;
		}
					/**------------------------------------------------------
	 				 * Changes the colors that match the first given color to
					 * the second given color.
					 * @param oldBackgroundColorByte the orig color
					 * @param backgroundColorByte	the new color
					 *------------------------------------------------------*/
	public		void		setBackgroundColor(
						byte oldBackgroundColorByte, byte backgroundColorByte)
		{
		isUpdatingUI = true;
		int index = numRows * numColumns - 1;
		while (index >= 0)
			{
			if (sourcePattern[index] == oldBackgroundColorByte)
				{
				sourceGrid.setCellColor(index, backgroundColorByte);
				sourcePattern[index] = backgroundColorByte;
				}
			if (resultPattern[index] == oldBackgroundColorByte)
				{
				resultGrid.setCellColor(index, backgroundColorByte);
				resultPattern[index] = backgroundColorByte;
				}
			--index;
			}
		isUpdatingUI = false;
		}
					/**------------------------------------------------------
	 				 * Changes all cells to the given color.
					 * @param backgroundColorByte 	the new color
					 *------------------------------------------------------*/
	public		void		clear(byte backgroundColorByte)
		{
		isUpdatingUI = true;
		sourceGrid.clearGrid(backgroundColorByte);
		resultGrid.clearGrid(backgroundColorByte);
		int index = numRows * numColumns - 1;
		while (index >= 0)
			{
			sourcePattern[index] = backgroundColorByte;
			resultPattern[index] = backgroundColorByte;
			--index;
			}
		isUpdatingUI = false;
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are
					 * generated by:
					 * <pre>
					 *    matchWholeRegionToggle
					 *    sourceGrid
					 *    resultGrid
					 *    percentOfTimeWhenValidField
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (isUpdatingUI)
			return(true);

		lifeEditor.setHasChanged(true);

		if (action.getActionSource() == matchWholeRegionToggle)
			{
			matchWholeRegion = Utility.toBoolean(matchWholeRegionToggle.getValue());
			}
		else if (action.getActionSource() == sourceGrid)
			{
			updateFromUI();
			}
		else if (action.getActionSource() == resultGrid)
			{
			updateFromUI();
			}
		else if (action.getActionSource() == percentOfTimeWhenValidField)
			{
			String value = percentOfTimeWhenValidField.getValue();
			int index;
			if ((index = value.indexOf("%")) != -1)
				value = value.substring(0, index);
			if (!Utility.isEmptyOrNull(value))
				{
				double val = Utility.toDouble(value);
				if ((val >= 0) && (val <= 100))
					{
					percentOfTimeWhenValid = val;
					}
				}
			percentOfTimeWhenValidField.setValue(percentOfTimeWhenValid + "%");
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Return if this rule is enabled _now_.
					 * @return		true if valid
					 *------------------------------------------------------*/
	public		boolean		isValid()
		{
		if (percentOfTimeWhenValid >= 100)
			return(true);
		if (percentOfTimeWhenValid <= 0)
			return(false);
		return(Math.random() < percentOfTimeWhenValid/100);
		}
					/**------------------------------------------------------
	 				 * Updates this rule's graphics.
					 *------------------------------------------------------*/
	private		void		updateUI()
		{
		isUpdatingUI = true;
		int index = numRows * numColumns - 1;
		while (index >= 0)
			{
			sourceGrid.setCellColor(index, sourcePattern[index]);
			resultGrid.setCellColor(index, resultPattern[index]);
			--index;
			}
		matchWholeRegionToggle.setValue("" + matchWholeRegion);
		percentOfTimeWhenValidField.setValue(percentOfTimeWhenValid + "%");
		isUpdatingUI = false;
		}
					/**------------------------------------------------------
	 				 * Updates this rule from it's graphics
					 *------------------------------------------------------*/
	private		void		updateFromUI()
		{
		int index = numRows * numColumns - 1;
		while (index >= 0)
			{
			sourcePattern[index] = sourceGrid.getByteValueOfCellColor(index);
			resultPattern[index] = resultGrid.getByteValueOfCellColor(index);
			--index;
			}
		matchWholeRegion = matchWholeRegionToggle.isSelected();
		isEmpty = sourceGrid.equals(resultGrid);
		}
					/**------------------------------------------------------
	 				 * Gets whether this rule has a precondition different
					 * from the postcondition.
					 * @return		true if this is empty
					 *------------------------------------------------------*/
	public		boolean		isEmpty()
		{
		return(isEmpty);
		}
					/**------------------------------------------------------
	 				 * Gets whether this rule is enabled.
					 * @return		true if this is enabled
					 *------------------------------------------------------*/
	public		boolean		isEnabled()
		{
		return(isEnabled);
		}
					/**------------------------------------------------------
	 				 * Generates this rule form the given entity.
					 * @param colorTable	the color table		
					 * @param from		the entity that defines this rule
					 *------------------------------------------------------*/
	public		void		fromEntity(MiiModelEntity entity, MiColorTable colorTable)
		{
		numRows = Utility.toInteger(entity.getPropertyValue(Mi_NUMBER_OF_ROWS_NAME));
		numColumns = Utility.toInteger(entity.getPropertyValue(Mi_NUMBER_OF_COLUMNS_NAME));
		matchWholeRegion = Utility.toBoolean(entity.getPropertyValue(Mi_MATCH_WHOLE_REGION_NAME));
		percentOfTimeWhenValid = Utility.toDouble(entity.getPropertyValue(Mi_FREQUENCY_OF_VALIDITY_NAME));
		Strings srcColors = new Strings();
		srcColors.appendCommaDelimitedStrings(entity.getPropertyValue(Mi_SOURCE_PATTERN_NAME));
		Strings resultColors = new Strings();
		resultColors.appendCommaDelimitedStrings(entity.getPropertyValue(Mi_RESULT_PATTERN_NAME));

		sourcePattern = new byte[numRows * numColumns];
		resultPattern = new byte[numRows * numColumns];

		for (int i = 0; i < numRows * numColumns; ++i)
			{
			sourcePattern[i] = colorTable.getByteValueOfColor(
				MiColorManager.getColor(srcColors.elementAt(i)));
			resultPattern[i] = colorTable.getByteValueOfColor(
				MiColorManager.getColor(resultColors.elementAt(i)));
			}
		updateUI();
		isEmpty = sourceGrid.equals(resultGrid);
		}
					/**------------------------------------------------------
	 				 * Generates an entity form this rule.
					 * @param colorTable	the color table		
					 * @param from		the entity that defines this rule
					 *------------------------------------------------------*/
	public		MiiModelEntity	toEntity(MiColorTable colorTable)
		{
		MiiModelEntity entity = new MiModelEntity();
		
		entity.setPropertyValue(Mi_TYPE_NAME, MiLifeWindow.Mi_RULE_TYPE_NAME);
		entity.setPropertyValue(Mi_NUMBER_OF_ROWS_NAME, "" + numRows);
		entity.setPropertyValue(Mi_NUMBER_OF_COLUMNS_NAME, "" + numColumns);
		entity.setPropertyValue(Mi_MATCH_WHOLE_REGION_NAME, Utility.toString(matchWholeRegion));
		entity.setPropertyValue(Mi_FREQUENCY_OF_VALIDITY_NAME, Utility.toString(percentOfTimeWhenValid));
		StringBuffer strBuffer = new StringBuffer(100);
		for (int i = 0; i < sourcePattern.length; ++i)
			{
			if (i != 0)
				strBuffer.append(",");
			strBuffer.append(MiColorManager.getColorName(
				colorTable.getColorFromByteValue(sourcePattern[i])));
			}
		entity.setPropertyValue(Mi_SOURCE_PATTERN_NAME, "\"" + strBuffer + "\"");
		strBuffer = new StringBuffer(100);
		for (int i = 0; i < resultPattern.length; ++i)
			{
			if (i != 0)
				strBuffer.append(",");
			strBuffer.append(MiColorManager.getColorName(
				colorTable.getColorFromByteValue(resultPattern[i])));
			}
		entity.setPropertyValue(Mi_RESULT_PATTERN_NAME, "\"" + strBuffer + "\"");
		return(entity);
		}
					/**------------------------------------------------------
	 				 * Gets a string describing this rule.
					 * @param colorTable	the color table	used to convert
					 *			grid values to colors.
					 * @return		the string description
					 *------------------------------------------------------*/
	public		String		toString(MiColorTable colorTable)
		{
		return(toEntity(colorTable).toString());
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a simple table that maps values to
 * colors.
 *
 *----------------------------------------------------------------------------------------------*/
class MiColorTable
	{
	private		Color[]		colorLookUpTable;

					/**------------------------------------------------------
	 				 * Constructs a new MiColorTable.
					 *------------------------------------------------------*/
	public				MiColorTable()
		{
		colorLookUpTable = MiColorManager.getColors();
		}
					/**------------------------------------------------------
	 				 * Adds the given color to the table if not already present.
					 * @param c		the color to add
					 *------------------------------------------------------*/
	public		void		addColorIfNotPresent(Color c)
		{
		for (int i = 0; i < colorLookUpTable.length; ++i)
			{
			if ((colorLookUpTable[i] != null) && (colorLookUpTable[i].equals(c)))
				return;
			}
		Color[] newTable = new Color[colorLookUpTable.length + 1];
		System.arraycopy(colorLookUpTable, 0, newTable, 0, colorLookUpTable.length);
		newTable[colorLookUpTable.length] = c;
		colorLookUpTable = newTable;
		}
					/**------------------------------------------------------
	 				 * Gets the value of the given color as a byte.
					 * @param c		the color to convert
					 * @return 		the byte value
					 *------------------------------------------------------*/
	public		byte		getByteValueOfColor(Color c)
		{
		for (int i = 0; i < colorLookUpTable.length; ++i)
			{
			if ((colorLookUpTable[i] != null) && (colorLookUpTable[i].equals(c)))
				return((byte )i);
			}
		MiDebug.println("Color not found in table = " + c);
		return((byte )0);
		}
					/**------------------------------------------------------
	 				 * Gets the color from the given byte value.
					 * @param b		the value to convert
					 * @return 		the color value
					 *------------------------------------------------------*/
	public		Color		getColorFromByteValue(int b)
		{
		return(colorLookUpTable[b]);
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class is an event handler that, when assigned to MiColorGrids,
 * allows the user to click and drag the mouse around to assign colors
 * the the grid's cells.
 *
 *----------------------------------------------------------------------------------------------*/
class MiGridEventHandler extends MiEventHandler
	{
	private		MiBounds	tmpBounds	= new MiBounds();
	private		MiLifeWindow	lifeEditor;
	private		MiColorGrid	grid;
	private		int		lastIndex	= -1;

					/**------------------------------------------------------
	 				 * Constructs a new MiGridEventHandler.
					 * @param grid		the grid to get events from and modify
					 * @param ed		the main object
					 *------------------------------------------------------*/
	public				MiGridEventHandler(MiColorGrid grid, MiLifeWindow ed)
		{
		lifeEditor = ed;
		this.grid = grid;
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_LEFT_MOUSE_DOWN_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_NOOP_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		}
					/**------------------------------------------------------
	 				 * Processes the command generated from the current event.
					 * Both are stored in the MiEventHandler super class.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			#isCommand
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	public		int		processCommand()
		{
		if ((isCommand(Mi_SELECT_COMMAND_NAME)) || (isCommand(Mi_DRAG_COMMAND_NAME)))
			{
			int index = grid.getCellAtLocation(event.getMouseFootPrint(tmpBounds));
			if (isCommand(Mi_DRAG_COMMAND_NAME) && (index == lastIndex))
				return(Mi_CONSUME_EVENT);
			byte c = grid.getByteValueOfColor(lifeEditor.getPenColor());
			if (c != grid.getByteValueOfCellColor(index))
				grid.setCellColor(index, c);
			else if (isCommand(Mi_SELECT_COMMAND_NAME))
				grid.setCellColor(index, lifeEditor.getGridBackgroundColor());

			lastIndex = index;
			}
		return(Mi_CONSUME_EVENT);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class is a array of bytes the values of which
 * are displayed as colors in a graphical grid.
 *
 *----------------------------------------------------------------------------------------------*/
class MiColorGrid extends MiPart
	{
	private		byte[]		gridArray;
	private		MiColorTable	colorTable;
	private		int		numberOfRows;
	private		int		numberOfColumns;
	private		boolean		isDrawingSeparatorLines;
	private		boolean		renderInWorldCoordinates;


					/**------------------------------------------------------
	 				 * Constructs a new MiColorGrid.
					 * @param numRows	the number of rows
					 * @param numColumns	the number of columns
					 *------------------------------------------------------*/
	public				MiColorGrid(int numRows, int numColumns)
		{
		setBorderLook(Mi_NONE);
		setNumberOfRowsAndColumns(numRows, numColumns);
		gridArray = new byte[numRows * numColumns];
		}
					/**------------------------------------------------------
	 				 * Sets the number of rows and columns.
					 * @param numRows	the number of rows
					 * @param numColumns	the number of columns
					 *------------------------------------------------------*/
	public		void		setNumberOfRowsAndColumns(int numRows, int numColumns)
		{
		numberOfRows = numRows;
		numberOfColumns = numColumns;
		gridArray = new byte[numRows * numColumns];
		invalidateArea();
		}
					/**------------------------------------------------------
	 				 * Gets the array of bytes.
					 * @return 		the array
					 *------------------------------------------------------*/
	public		byte[]		getGrid()
		{
		return(gridArray);
		}
					/**------------------------------------------------------
	 				 * Sets whether the cells are seperated visually using
					 * lines.
					 * @param flag 		true if lines are visible
					 *------------------------------------------------------*/
	public		void		setIsDrawingSeparatorLines(boolean flag)
		{
		isDrawingSeparatorLines = flag;
		}
					/**------------------------------------------------------
	 				 * Sets whether this will render it's cells in world coordinates
					 * using Mica's rectangle shape or whether it will draw
					 * directly to the screen using java.awt.Graphics.fillRect
					 * @param flag 		true if render slower
					 *------------------------------------------------------*/
	public		void		setRenderInWorldCoordinates(boolean flag)
		{
		renderInWorldCoordinates = flag;
		}
					/**------------------------------------------------------
	 				 * Sets the color table this grid will use.
					 * @param colorTable 	the color table
					 *------------------------------------------------------*/
	public		void		setColorTable(MiColorTable colorTable)
		{
		this.colorTable = colorTable;
		}
					/**------------------------------------------------------
	 				 * Gets the color table this grid uses.
					 * @return 	 	the color table
					 *------------------------------------------------------*/
	public		MiColorTable	getColorTable()
		{
		return(colorTable);
		}
					/**------------------------------------------------------
	 				 * Converts the color to a byte value using the color table.
					 * @param c 	 	the color to convert
					 * @return 	 	the byte value
					 *------------------------------------------------------*/
	public		byte		getByteValueOfColor(Color c)
		{
		return(colorTable.getByteValueOfColor(c));
		}
					/**------------------------------------------------------
	 				 * Gets the color of the cell at the given index.
					 * @param cellNumber  	the number of a cell (from 0)
					 * @return 	 	the byte value
					 *------------------------------------------------------*/
	public		byte		getByteValueOfCellColor(int cellNumber)
		{
		return(gridArray[cellNumber]);
		}
					/**------------------------------------------------------
	 				 * Sets the color of the cell at the given index.
					 * @param cellNumber  	the number of a cell (from 0)
					 * @param color  	the byte value of a color
					 *------------------------------------------------------*/
	public		void		setCellColor(int cellNumber, byte color)
		{
		gridArray[cellNumber] = color;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		invalidateArea();
		}
					/**------------------------------------------------------
	 				 * Sets the color of the cell at the given index.
					 * @param cellNumber  	the number of a cell (from 0)
					 * @param color  	the color
					 *------------------------------------------------------*/
	public		void		setCellColor(int cellNumber, Color color)
		{
		gridArray[cellNumber] = getByteValueOfColor(color);
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		invalidateArea();
		}
					/**------------------------------------------------------
	 				 * Gets the color of the cell at the given index.
					 * @param cellNumber  	the number of a cell (from 0)
					 * @return 	 	the color
					 *------------------------------------------------------*/
	public		Color		getCellColor(int cellNumber)
		{
		return(colorTable.getColorFromByteValue(gridArray[cellNumber]));
		}
					/**------------------------------------------------------
	 				 * Gets the index of the cell at the given location.
					 * @param area  	the area to look at
					 * @return 	 	the index or -1
					 *------------------------------------------------------*/
	public		int		getCellAtLocation(MiBounds area)
		{
		MiBounds targetBounds = getInnerBounds();
		int column = (int )((area.getCenterX() - targetBounds.getXmin())/targetBounds.getWidth() 
			* numberOfColumns);
		int row = (int )((area.getCenterY() - targetBounds.getYmin())/targetBounds.getHeight() 
			* numberOfRows);

		// Check to see if on border of grid
		if (column >= numberOfColumns)
			column = numberOfColumns - 1;
		if (row >= numberOfRows)
			row = numberOfRows - 1;

		return(row * numberOfColumns + column);
		}
					/**------------------------------------------------------
	 				 * Sets all of the cells to the given color.
					 * @param backgroundColor  	the color to assign 
					 *------------------------------------------------------*/
	public		void		clearGrid(Color backgroundColor)
		{
		clearGrid(getByteValueOfColor(backgroundColor));
		}
					/**------------------------------------------------------
	 				 * Sets all of the cells to the given color.
					 * @param backgroundColor  	the byte value of  the color
					 *------------------------------------------------------*/
	public		void		clearGrid(byte backgroundColorIndex)
		{
		int len = gridArray.length;
		for (int i = 0; i < len; ++i)
			{
			gridArray[i] = backgroundColorIndex;
			}
		invalidateArea();
		}
					/**------------------------------------------------------
	 				 * Gets whether this grid equals the given grid
					 * @param other		the grid to compare to
					 * @return		true if equal
					 *------------------------------------------------------*/
	public		boolean		equals(MiColorGrid other)
		{
		int len = gridArray.length;
		if (len != other.gridArray.length)
			{
			return(false);
			}
		for (int i = 0; i < len; ++i)
			{
			if (gridArray[i] != other.gridArray[i])
				{
				return(false);
				}
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Draws this grid.
					 * @param renderer	the renderer to use
					 *------------------------------------------------------*/
	public		void		render(MiRenderer renderer)
		{
		if (renderInWorldCoordinates)
			{
			MiBounds 	targetBounds 	= getInnerBounds();
			MiDistance	cellWidth	= targetBounds.getWidth()/numberOfColumns;
			MiDistance	cellHeight	= targetBounds.getHeight()/numberOfRows;

			MiCoord 	startX 		= targetBounds.getXmin();
			MiCoord 	startY 		= targetBounds.getYmin();

			MiBounds 	bounds 		= new MiBounds(0, 0, cellWidth, cellHeight);

			renderer.setAttributes(getAttributes());
			bounds.translateYminTo(startY);
			int index = 0;
			for (int row = 0; row < numberOfRows; ++row)
				{
				bounds.translateXminTo(startX);
				for (int column = 0; column < numberOfColumns; ++column)
					{
					renderer.setBackgroundColor(colorTable.getColorFromByteValue(gridArray[index]));
					renderer.drawRect(bounds);
					++index;
					bounds.translate(cellWidth, 0);
					}
				bounds.translate(0, cellHeight);
				}
			if (isDrawingSeparatorLines)
				{
				MiCoord x = startX;
				MiCoord y = startY;
				MiCoord endX = targetBounds.getXmax();
				MiCoord endY = targetBounds.getYmax();

				for (int row = 0; row < numberOfRows + 1; ++row)
					{
					renderer.drawLine(startX, y, endX, y);
					y += cellHeight;
					}
				for (int column = 0; column < numberOfColumns + 1; ++column)
					{
					renderer.drawLine(x, startY, x, endY);
					x += cellWidth;
					}
				}
			}
		else
			{
			MiBounds 	targetBounds 	= getInnerBounds();
		
			renderer.getTransform().wtod(targetBounds, targetBounds);
			java.awt.Graphics g	= renderer.getWindowSystemRenderer();

			double		cellWidthDouble	= targetBounds.getWidth()/numberOfColumns;
			double		cellHeightDouble= targetBounds.getHeight()/numberOfRows;

			int		startX 		= (int )(targetBounds.getXmin());
			int		startY 		= renderer.getYmax() - (int )(targetBounds.getYmin());

			int 		index 		= 0;
			int 		x 		= 0;
			int 		y 		= 0;
			int 		nextX 		= 0;
			int 		prevY 		= startY;
			double		xIndent		= 0;
			double		yIndent		= 0;

			for (int row = 0; row < numberOfRows; ++row)
				{
				x = startX;
				xIndent = 0;

				yIndent += cellHeightDouble;
				y = (int )(startY - yIndent);

				for (int column = 0; column < numberOfColumns; ++column)
					{
					xIndent += cellWidthDouble;
					nextX = (int )(startX + xIndent);

					g.setColor(colorTable.getColorFromByteValue(gridArray[index]));
					g.fillRect(x, y, nextX - x + 1, prevY - y + 1);
					++index;

					x = nextX;
					}
				prevY = y;
				}
			if (isDrawingSeparatorLines)
				{
				x 		= startX;
				y 		= startY;
				int endX 	= (int )(targetBounds.getXmax());
				int endY 	= renderer.getYmax() - (int )(targetBounds.getYmax());

				xIndent = 0;
				yIndent = 0;

				g.setColor(MiColorManager.black);
				for (int row = 0; row < numberOfRows + 1; ++row)
					{
					g.drawLine(startX, y, endX, y);
					yIndent += cellHeightDouble;
					y = (int )(startY - yIndent);
					}
				for (int column = 0; column < numberOfColumns + 1; ++column)
					{
					g.drawLine(x, startY, x, endY);
					xIndent += cellWidthDouble;
					x = (int )(startX + xIndent);
					}
				}
			}
		}
	}

