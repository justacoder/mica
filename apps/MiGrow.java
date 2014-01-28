
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
	Add a maximum lifetime, in generations, for each rule
	Add textfield so can change max number of shapes
	Add bottom row palette so can change what primitive we are drawing
*/

/**----------------------------------------------------------------------------------------------
 * This class implements a game that allows the user to specify name-based
 * rewrite rules and then animate the result.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGrow extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiGrowWindow. Supported command line parameters
					 * are:
					 * -file		load this file on startup
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String filename = Utility.getCommandLineArgument(args, "-file");
		String title = Utility.getCommandLineArgument(args, "-title");
		MiGrowWindow window = new MiGrowWindow(title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		loadSpecifiedFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiGrowWindow. Supported html file parameters are:
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
		MiGrowWindow window = new MiGrowWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		loadSpecifiedFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * Loads the specified file, if any.
					 * @param window	the window
					 * @param filename	the name of the file or null
					 *------------------------------------------------------*/
	protected static void		loadSpecifiedFile(MiGrowWindow window, String filename)
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
					"Usage: MiGrow [-file <filename>] [-title <window title>]");
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
class MiGrowWindow extends MiFileEditorWindow implements MiiAnimatable, MiiDisplayNames, MiiNames
	{
	// ---------------------------------------------------------------
	// Define the names of some commands
	// ---------------------------------------------------------------
	private static final String	Mi_GROW_SHOW_GRAPHICS_NAMES_COMMAND_NAME	= "ShowNames";
	private static final String	Mi_GROW_HIDE_GRAPHICS_NAMES_COMMAND_NAME	= "HideNames";

	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	
						= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	private static final String	Mi_GROW_INCREASE_NUMBER_OF_RULES_NAME
					= "Mi_GROW_INCREASE_NUMBER_OF_RULES_MENUITEM_NAME";
	private static final String	Mi_GROW_REDUCE_NUMBER_OF_RULES_NAME
					= "Mi_GROW_REDUCE_NUMBER_OF_RULES_MENUITEM_NAME";
	private static final String	Mi_GROW_SET_BACKGROUND_COLOR_NAME
					= "Mi_GROW_SET_BACKGROUND_COLOR_MENUITEM_NAME";
	private static final String	Mi_GROW_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME	
					= "Mi_GROW_SET_BACKGROUND_COLOR_TO_PEN_COLOR_MENUITEM_NAME";
	private static final String	Mi_GROW_APPLY_RULES_IN_PARALLEL_NAME
					= "Mi_GROW_APPLY_RULES_IN_PARALLEL_MENUITEM_NAME";
	private static final String	Mi_GROW_APPLY_RULES_SERIALLY_NAME
					= "Mi_GROW_APPLY_RULES_SERIALLY_MENUITEM_NAME";
	private static final String	Mi_GROW_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME
					= "Mi_GROW_VALIDATE_RULES_ONCE_EACH_GENERATION_MENUITEM_NAME";
	private static final String	Mi_GROW_VALIDATE_RULES_EACH_USE_NAME
					= "Mi_GROW_VALIDATE_RULES_EACH_USE_MENUITEM_NAME";
	private static final String	Mi_GROW_SHOW_NAMES_NAME
					= "Mi_GROW_SHOW_NAMES_NAME";
	private static final String	Mi_GROW_ABOUT_MSG
					= "Mi_GROW_ABOUT_MSG";
						

	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
	{
	new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE,			"Grow"),
	new Pair( Mi_GROW_INCREASE_NUMBER_OF_RULES_NAME, 		"More rules"),
	new Pair( Mi_GROW_REDUCE_NUMBER_OF_RULES_NAME, 			"Fewer rules"),
	new Pair( Mi_GROW_SET_BACKGROUND_COLOR_NAME, 			"Set background color..."),
	new Pair( Mi_GROW_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME, 	"Set background color to pen color"),
	new Pair( Mi_GROW_APPLY_RULES_IN_PARALLEL_NAME, 		"Apply rules in parallel"),
	new Pair( Mi_GROW_APPLY_RULES_SERIALLY_NAME, 			"Apply rules serially"),
	new Pair( Mi_GROW_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME,   "Validate rules once each generation"),
	new Pair( Mi_GROW_VALIDATE_RULES_EACH_USE_NAME, 		"Validate rules each use"),
	new Pair( Mi_GROW_SHOW_NAMES_NAME, 				"Show names"),
	new Pair( Mi_GROW_ABOUT_MSG,
		"\n\nThis entertainment program is part of the Mica Appletcation Suite.\n\n"
		+ "This version of the classic grow program uses graphical rewrite rules\n"
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
	private static final String	Mi_APPLY_RULES_IN_PARALLEL_NAME		= "applyRulesInParallel";
	private static final String	Mi_VALIDATE_RULES_EACH_GENERATION_NAME	= "validateRulesOnceEachGeneration";

	private static final String	Mi_GROW_FILE_HEADER_MSG		= "#MiGrow version 1.0";
	private static final String	Mi_GROW_DEFAULT_FILENAME	= "mica.grow";


	// ---------------------------------------------------------------
	// Define the class variables this class will use.
	// ---------------------------------------------------------------
	private		Vector		rules				= new Vector();
	private		MiParts		workingParts			= new MiParts();
	private		MiPlayerPanel	playerPanel;
	private		MiSwatchesColorPalette 	colorPalette;
	private		Color		penColor			= MiColorManager.red;
	private		Color		backgroundColor			= MiColorManager.white;
	private		MiAnimator	animator;
	private		MiEditor	editor;
	private		String		playerPanelState		= "";
	private		MiList 		palette;
	private		boolean 	hasChanged;
	private		boolean 	applyRulesInParallel;
	private		boolean 	validateRulesOnlyOnceEachGeneration;
	private		MiWidget	numGenerationsDisplayField;
	private		int		numGenerations;
	private		int		maxNumberOfParts		= 40;
	private		MiWidget	numPartsDisplayField;
	private		MiColorChooser	colorChooser;
	private		boolean		playingBackward;
	private		MiICreateMultiPointObject	seedShapeCreator;
	private		MiLine		seedPrototypeShape;
	private		MiTextField	seedPrototypeName;
	private		boolean		namesAreVisible		= true;





					/**------------------------------------------------------
	 				 * Constructs a new MiGrowWindow.
					 * @param awtContainer	the container to add this to
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiGrowWindow( 
						Container awtContainer, 
						String title, 
						MiBounds screenSize)
		{
		super(MiUtility.getFrame(awtContainer),
			title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, 
			screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE,
			"grow", Mi_GROW_DEFAULT_FILENAME, false);

		awtContainer.add("Center", (java.awt.Component )getCanvas().getNativeComponent());
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiGrowWindow.
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiGrowWindow(String title, MiBounds screenSize)
		{
		super(title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title,
			 screenSize,
			"grow", Mi_GROW_DEFAULT_FILENAME, false);
		setup();
		}
					/**------------------------------------------------------
	 				 * Setup the window, creating the menubar, status bar,
					 * palette for the rules, the grid for the cells... 
					 *------------------------------------------------------*/
	protected 	void		setup()
		{
		MiSystem.loadPropertiesFile("MiGrow.mica");

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
		editor.appendEventHandler(new MiISelectObjectUnderMouse());
		editor.appendEventHandler(new MiIDeleteSelectedObjects());
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
		palette.setMinimumNumberOfVisibleRows(3);
		palette.setPreferredNumberOfVisibleRows(3);
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
		MiStatusBar gridSizeBar = new MiBasicStatusField(".20\n.20");
		numGenerationsDisplayField = gridSizeBar.getField(0);
		displayNumberOfGenerations();
		numPartsDisplayField = gridSizeBar.getField(1);
		displayNumberOfParts();
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
		seedPrototypeShape = new MiLine();
		seedPrototypeName = new MiTextField();
		seedPrototypeName.setBorderLook(Mi_NONE);
		seedPrototypeName.setDisplaysFocusBorder(false);
		seedPrototypeName.setKeyboardFocusBorderLook(Mi_INDENTED_BORDER_LOOK);
		seedPrototypeName.setKeyboardFocusBackgroundColor(MiColorManager.lightGray);
		seedPrototypeName.setValue("A");
		seedPrototypeName.setNumDisplayedColumns(-1);
		seedPrototypeName.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		
		seedPrototypeShape.appendAttachment(seedPrototypeName, Mi_CENTER_LOCATION, null, null);
		seedPrototypeShape.setName("A".intern());

		colorPalette = new MiSwatchesColorPalette(
					1, MiColorManager.getNumberOfColors(), new MiSize(16,16));
		colorPalette.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		colorPalette.setSelection(penColor);
		appendPart(colorPalette);

		seedShapeCreator = new MiICreateMultiPointObject();
		seedShapeCreator.setSelectNewlyCreatedShape(false);
		seedShapeCreator.setPrototypeShape(seedPrototypeShape);
		
		editor.appendEventHandler(seedShapeCreator);
		editor.setBackgroundColor(MiColorManager.white);
		editor.setWorldBounds(new MiBounds(0, 0, 500, 500));
		editor.setUniverseBounds(new MiBounds(0, 0, 500, 500));

		animator = new MiAnimator(this, 100);
		animator.setEnabled(false);
		MiAnimationManager.addAnimator(this, animator);

		MiEditorMenu formatMenu = getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME);
		formatMenu.appendSeparator();
		formatMenu.appendMenuItem(Mi_GROW_SET_BACKGROUND_COLOR_NAME, this);
		formatMenu.appendMenuItem(Mi_GROW_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME, this);
		formatMenu.appendMenuItem(Mi_CLEAR_COMMAND_NAME, this);
		

		MiEditorMenu optionMenu = new MiEditorMenu("&Options", this);
		optionMenu.appendBooleanMenuItem(Mi_GROW_SHOW_NAMES_NAME, this, 
				Mi_GROW_SHOW_GRAPHICS_NAMES_COMMAND_NAME, 
				Mi_GROW_HIDE_GRAPHICS_NAMES_COMMAND_NAME);
		optionMenu.getMenuItemWithCommand(Mi_GROW_SHOW_GRAPHICS_NAMES_COMMAND_NAME).select(true);
		optionMenu.appendSeparator();

		optionMenu.startRadioButtonSection();
		optionMenu.appendBooleanMenuItem(Mi_GROW_APPLY_RULES_IN_PARALLEL_NAME, this);
		optionMenu.appendBooleanMenuItem(Mi_GROW_APPLY_RULES_SERIALLY_NAME, this);
		optionMenu.getMenuItemWithCommand(Mi_GROW_APPLY_RULES_SERIALLY_NAME).select(true);
		optionMenu.endRadioButtonSection();

		optionMenu.appendSeparator();
		optionMenu.startRadioButtonSection();
		optionMenu.appendBooleanMenuItem(Mi_GROW_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME, this);
		optionMenu.appendBooleanMenuItem(Mi_GROW_VALIDATE_RULES_EACH_USE_NAME, this);
		optionMenu.getMenuItemWithCommand(Mi_GROW_VALIDATE_RULES_EACH_USE_NAME).select(true);
		optionMenu.endRadioButtonSection();
		getMenuBar().appendPulldownMenu(optionMenu);


		getMenuBarButton(getMenuBar().getMenu(Mi_TOOLS_MENU_DISPLAY_NAME)).setVisible(false);
		getMenuBarButton(getMenuBar().getMenu(Mi_SHAPE_MENU_DISPLAY_NAME)).setVisible(false);
		getMenuBarButton(getMenuBar().getMenu(Mi_CONNECT_MENU_DISPLAY_NAME)).setVisible(false);
		getMenuBarButton(getMenuBar().getMenu(Mi_EDIT_MENU_DISPLAY_NAME)).setVisible(false);

		getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME)
			.removeMenuItemWithCommand(Mi_FORMAT_COMMAND_NAME);
		getMenuBar().getMenu(Mi_EDIT_MENU_DISPLAY_NAME)
			.removeMenuItemWithCommand(Mi_DELETE_COMMAND_NAME);

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
					 *    Mi_GROW_INCREASE_NUMBER_OF_RULES_NAME
					 *    Mi_GROW_REDUCE_NUMBER_OF_RULES_NAME
					 *    Mi_GROW_SET_BACKGROUND_COLOR_NAME
					 *    Mi_GROW_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME
					 *    Mi_CLEAR_COMMAND_NAME
					 *    Mi_GROW_APPLY_RULES_IN_PARALLEL_NAME
					 *    Mi_GROW_APPLY_RULES_SERIALLY_NAME
					 *    Mi_GROW_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME
					 *    Mi_GROW_VALIDATE_RULES_EACH_USE_NAME
					 *    Mi_GROW_SHOW_GRAPHICS_NAMES_COMMAND_NAME
					 *    Mi_GROW_HIDE_GRAPHICS_NAMES_COMMAND_NAME
					 *    Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 * @overrides		MiEditorWindow#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		if (command.equalsIgnoreCase(Mi_GROW_INCREASE_NUMBER_OF_RULES_NAME))
			{
			addRule();
			}
		else if (command.equalsIgnoreCase(Mi_GROW_REDUCE_NUMBER_OF_RULES_NAME))
			{
			if (palette.getNumberOfItems() > 1)
				{
				palette.removeItem(palette.getNumberOfItems() - 1);
				rules.removeElementAt(rules.size() - 1);
				}
			}
		else if (command.equalsIgnoreCase(Mi_GROW_SET_BACKGROUND_COLOR_NAME))
			{
			if (colorChooser == null)
				colorChooser = new MiColorChooser(this);
			Color c = colorChooser.popupAndWaitForClose();
			if (c != null)
				{
				setDisplayBackgroundColor(c);
				}
			setHasChanged(true);
			}
		else if (command.equalsIgnoreCase(Mi_GROW_SET_BACKGROUND_COLOR_TO_PEN_COLOR_NAME))
			{
			setDisplayBackgroundColor(getPenColor());
			}
		else if (command.equalsIgnoreCase(Mi_CLEAR_COMMAND_NAME))
			{
			editor.removeAllParts();
			numGenerations = 0;
			displayNumberOfGenerations();
			}
		else if (command.startsWith(Mi_GROW_APPLY_RULES_IN_PARALLEL_NAME))
			{
			applyRulesInParallel = true;
			setHasChanged(true);
			}
		else if (command.startsWith(Mi_GROW_APPLY_RULES_SERIALLY_NAME))
			{
			setHasChanged(true);
			applyRulesInParallel = false;
			setHasChanged(true);
			}
		else if (command.startsWith(Mi_GROW_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME))
			{
			validateRulesOnlyOnceEachGeneration = true;
			setHasChanged(true);
			}
		else if (command.startsWith(Mi_GROW_VALIDATE_RULES_EACH_USE_NAME))
			{
			validateRulesOnlyOnceEachGeneration = false;
			setHasChanged(true);
			}
		else if (command.startsWith(Mi_GROW_SHOW_GRAPHICS_NAMES_COMMAND_NAME))
			{
			setNamesAreVisible(true);
			}
		else if (command.startsWith(Mi_GROW_HIDE_GRAPHICS_NAMES_COMMAND_NAME))
			{
			setNamesAreVisible(false);
			}
		else if (command.startsWith(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME))
			{
			MiToolkit.postAboutDialog(
				this, getDefaultWindowTitle(), 
				MiSystem.getCompanyLogo(), Mi_GROW_ABOUT_MSG, false);
			}
		else
			{
			super.processCommand(command);
			}
		setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		}
					/**------------------------------------------------------
	 				 * Opens the default, no name, file that the user can
					 * edit in but which needs to be named before saving.
					 * @overrides		MiFileEditorWindow#defaultFileOpened
					 *------------------------------------------------------*/
	protected	void		defaultFileOpened()
		{
		editor.removeAllParts();
		for (int i = 0; i < rules.size(); ++i)
			{
			MiGrowRule rule = (MiGrowRule )rules.elementAt(i);
			rule.clear(backgroundColor);
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
		entity.setPropertyValue(Mi_BACKGROUND_COLOR_ATT_NAME, 
			MiColorManager.getColorName(backgroundColor));
		entity.setPropertyValue(Mi_APPLY_RULES_IN_PARALLEL_NAME, 
			Utility.toString(applyRulesInParallel));
		entity.setPropertyValue(Mi_VALIDATE_RULES_EACH_GENERATION_NAME, 
			Utility.toString(validateRulesOnlyOnceEachGeneration));
		document.appendModelEntity(entity);

		for (int i = 0; i < rules.size(); ++i)
			{
			MiGrowRule rule = (MiGrowRule )rules.elementAt(i);
			if (!rule.isEmpty())
				{
				entity = rule.toEntity();
				document.appendModelEntity(entity);
				}
			}
		MiModelIOFormatManager ioFormatManager = new MiModelIOFormatManager();
		ioFormatManager.save(document, stream, Mi_GROW_FILE_HEADER_MSG);
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
				MiGrowRule rule = addRule();
				rule.fromEntity(entity);
				}
			else if (document.equals(entity.getPropertyValue(Mi_TYPE_NAME), Mi_DOCUMENT_TYPE_NAME))
				{
				Color bgColor = MiColorManager.getColor(
					entity.getPropertyValue(Mi_BACKGROUND_COLOR_ATT_NAME));
				setDisplayBackgroundColor(bgColor);

				applyRulesInParallel = Utility.toBoolean(
					entity.getPropertyValue(Mi_APPLY_RULES_IN_PARALLEL_NAME));
				setCommandState(Mi_GROW_APPLY_RULES_IN_PARALLEL_NAME, 
					applyRulesInParallel);

				validateRulesOnlyOnceEachGeneration = Utility.toBoolean(
					entity.getPropertyValue(Mi_VALIDATE_RULES_EACH_GENERATION_NAME));
				setCommandState(Mi_GROW_VALIDATE_RULES_ONCE_EACH_GENERATION_NAME, 
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
	protected	void		displayNumberOfParts()
		{
		if (editor != null)
			numPartsDisplayField.setValue("Number of shapes: " + editor.getNumberOfParts());
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
	private		MiGrowRule	addRule()
		{
		MiGrowRule rule = new MiGrowRule();
		rules.addElement(rule);
		palette.appendItem(rule);
		rule.clear(backgroundColor);
		return(rule);
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
			setPenColor(colorPalette.getSelection());
			}
		else if (action.hasActionType(MiPlayerPanel.Mi_PLAYER_PANEL_ACTION))
			{
			String state = playerPanel.getState();
			if ((state.equalsIgnoreCase(MiPlayerPanel.PREVIOUS_TRACK))
				|| (state.equalsIgnoreCase(MiPlayerPanel.NEXT_TRACK)))
				{
				animator.setEnabled(false);
				editor.removeAllParts();
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
	 				 * Sets the current display pen color.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setPenColor(Color c)
		{
		penColor = c;
		seedPrototypeShape.setColor(c);
		for (int i = 0; i < rules.size(); ++i)
			{
			((MiGrowRule )rules.elementAt(i)).setPenColor(c);
			}
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
	public		Color		getDisplayBackgroundColor()
		{
		return(backgroundColor);
		}
					/**------------------------------------------------------
	 				 * Sets the current display grid background color.
					 * @param color		the color
					 *------------------------------------------------------*/
	public		void		setDisplayBackgroundColor(Color color)
		{
		backgroundColor = color;
		editor.setBackgroundColor(color);
		for (int i = 0; i < rules.size(); ++i)
			{
			((MiGrowRule )rules.elementAt(i)).setBackgroundColor(color);
			}
		}
	public		void		setNamesAreVisible(boolean flag)
		{
		namesAreVisible = flag;
		seedPrototypeName.setVisible(flag);

		for (int  i = 0; i < editor.getNumberOfParts(); ++i)
			editor.getPart(i).getAttachment(0).setVisible(flag);

		for (int i = 0; i < rules.size(); ++i)
			((MiGrowRule )rules.elementAt(i)).setNamesAreVisible(flag);
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
			++numGenerations;
			}
		displayNumberOfGenerations();
		displayNumberOfParts();
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
		int numWorkingParts;

		if (editor.getNumberOfParts() > maxNumberOfParts)
			return(0);

		if (applyRulesInParallel)
			{
			numWorkingParts = editor.getNumberOfParts();
			workingParts.removeAllElements();
			for (int i = 0; i < numWorkingParts; ++i)
				workingParts.addElement(editor.getPart(i));
			}

		for (int r = 0; r < rules.size(); ++r)
			{
			MiGrowRule rule = (MiGrowRule )rules.elementAt(r);
			if ((rule.isEnabled()) && (!rule.isEmpty()) 
				&& ((!validateRulesOnlyOnceEachGeneration) || rule.isValid()))
				{
				if (!applyRulesInParallel)
					{
					numWorkingParts = editor.getNumberOfParts();
					workingParts.removeAllElements();
					for (int i = 0; i < numWorkingParts; ++i)
						workingParts.addElement(editor.getPart(i));
					}
				int numParts = workingParts.size();
//System.out.println("numParts = " + numParts);
				for (int i = 0; i < numParts; ++i)
					{
					MiPart part = workingParts.elementAt(i);
					if (rule.matches(part))
						{
						if ((validateRulesOnlyOnceEachGeneration) || (rule.isValid()))
							{
							rule.execute(part, editor);
							++modified;
							}
						}
					}
				}
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
class MiGrowRule extends MiVisibleContainer implements MiiActionHandler, MiiActionTypes, MiiTypes, MiiNames
	{
	private static final String	Mi_MATCH_WHOLE_REGION_NAME	= "matchWholeRegion";
	private static final String	Mi_FREQUENCY_OF_VALIDITY_NAME	= "frequencyOfValidity";
	private static final String	Mi_SOURCE_PATTERN_NAME		= "sourcePattern";
	private static final String	Mi_RESULT_PATTERN_NAME		= "resultPattern";
	private static final String	Mi_GROW_PROTOTYPE_GRAPHIC_ANGLE	= "originalAngle";

	private static final String[]	percentOfTimeWhenValidOptions =
		{ "100%", "75%", "66%", "50%", "33%", "25%", "10%", "5%", "0%" };

	private		String		sourcePattern;
	private		String		resultPattern;
	private		MiPart		sourcePatternGraphics;
	private		MiPart		resultPatternGraphics;
	private		MiComboBox	percentOfTimeWhenValidField;
	private		double		percentOfTimeWhenValid	= 100.0;
	private		boolean		isEmpty			= true;
	private		boolean		isEnabled		= true;
	private		boolean		matchWholeRegion;
	private		boolean		isUpdatingUI;

	private		MiEditor	sourceEditor;
	private		MiEditor	resultEditor;
	private		MiToggleButton	matchWholeRegionToggle;
	private		MiICreateMultiPointObject	sourceShapeCreator;
	private		MiICreateMultiPointObject	resultShapeCreator;
	private		MiLine		linePrototypeShape;
	private		MiTextField	linePrototypeName;
	private		MiPoint		tmpPt0			= new MiPoint();
	private		MiPoint		tmpPt1			= new MiPoint();
	private		MiBounds	sourceBounds		= new MiBounds();
	private		MiPoint		sourceCenter		= new MiPoint();
	private		MiBounds	ruleEditorSize		= new MiBounds(0, 0, 100, 100);
	private		boolean		namesAreVisible		= true;



					/**------------------------------------------------------
	 				 * Constructs a new MiGrowRule.
					 *------------------------------------------------------*/
	public				MiGrowRule()
		{
		setBorderLook(Mi_RAISED_BORDER_LOOK);
		createRuleGraphics();
		}
	public		boolean		matches(MiPart part)
		{
		String name = part.getName();
		return(sourcePattern == name);
		}
	public		void		execute(MiPart part, MiContainer display)
		{
		String name = part.getName();
		part.getBounds(sourceBounds);

		double protoAngle 
			= Utility.toDouble((String )sourcePatternGraphics.getResource(Mi_GROW_PROTOTYPE_GRAPHIC_ANGLE));
		part.getPoint(0, tmpPt0);
		part.getPoint(1, tmpPt1);
		double angle = Utility.getAngle(tmpPt1.y - tmpPt0.y, tmpPt1.x - tmpPt0.x);
		
		MiPart result = resultPatternGraphics.deepCopy();
		sourceCenter = sourceBounds.getCenter(sourceCenter);
		if (matchWholeRegion)
			result.setBounds(sourceBounds);
		else
			result.setCenter(sourceCenter);

//System.out.println("protoAngle = " + protoAngle);
//System.out.println("angle = " + angle);

		while (result.getNumberOfParts() > 0)
			{
			MiPart p = result.getPart(0);
			result.removePart(0);
			p.rotate(sourceCenter, angle - protoAngle);
			display.appendPart(p);
			}
		display.removePart(part);
		}
	public		void		clear(Color backgroundColor)
		{
		sourceEditor.removeAllParts();
		sourceEditor.setBackgroundColor(backgroundColor);
		resultEditor.removeAllParts();
		resultEditor.setBackgroundColor(backgroundColor);
		}
 					/**------------------------------------------------------
	 				 * Sets the current display pen color.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setPenColor(Color c)
		{
		linePrototypeShape.setColor(c);
		}
	public		void		setNamesAreVisible(boolean flag)
		{
		namesAreVisible = flag;
		linePrototypeName.setVisible(flag);

		for (int  i = 0; i < sourceEditor.getNumberOfParts(); ++i)
			sourceEditor.getPart(i).getAttachment(0).setVisible(flag);

		for (int  i = 0; i < resultEditor.getNumberOfParts(); ++i)
			resultEditor.getPart(i).getAttachment(0).setVisible(flag);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiGrowRule entry suitable for adding to
					 * the list, including toggle buttons and other fields to
					 * specifiy options.
					 *------------------------------------------------------*/
	private		void		createRuleGraphics()
		{
		linePrototypeShape = new MiLine();
		linePrototypeName = new MiTextField();
		linePrototypeName.setBorderLook(Mi_NONE);
		linePrototypeName.setDisplaysFocusBorder(false);
		linePrototypeName.setKeyboardFocusBorderLook(Mi_INDENTED_BORDER_LOOK);
		linePrototypeName.setKeyboardFocusBackgroundColor(MiColorManager.lightGray);
		linePrototypeName.setValue("A");
		linePrototypeName.setNumDisplayedColumns(-1);
		linePrototypeName.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		
		linePrototypeShape.appendAttachment(linePrototypeName, Mi_CENTER_LOCATION, null, null);
		linePrototypeShape.setName("A".intern());

		MiColumnLayout 	column 	= new MiColumnLayout();
		setLayout(column);
		MiRowLayout 	row 	= new MiRowLayout();
		row.setAlleyHSpacing(5);
		sourceEditor 		= new MiEditor();
		sourceEditor.setBounds(ruleEditorSize);
		sourceShapeCreator = new MiICreateMultiPointObject();
		sourceShapeCreator.setSelectNewlyCreatedShape(false);
		sourceShapeCreator.setPrototypeShape(linePrototypeShape);
		sourceEditor.appendEventHandler(sourceShapeCreator);
		sourceEditor.appendEventHandler(new MiISelectObjectUnderMouse());
		sourceEditor.appendEventHandler(new MiISelectArea());
		sourceEditor.appendEventHandler(new MiIDeleteSelectedObjects());
		sourceEditor.appendActionHandler(this, Mi_EDITOR_CONTENTS_GEOMETRY_CHANGED_ACTION);
		row.appendPart(sourceEditor);
		resultEditor 		= new MiEditor();
		resultEditor.setBounds(ruleEditorSize);
		resultShapeCreator = new MiICreateMultiPointObject();
		resultShapeCreator.setSelectNewlyCreatedShape(false);
		resultShapeCreator.setPrototypeShape(linePrototypeShape);
		resultEditor.appendEventHandler(resultShapeCreator);
		resultEditor.appendEventHandler(new MiISelectObjectUnderMouse());
		resultEditor.appendEventHandler(new MiISelectArea());
		resultEditor.appendEventHandler(new MiIDeleteSelectedObjects());
		resultEditor.appendActionHandler(this, Mi_EDITOR_CONTENTS_GEOMETRY_CHANGED_ACTION);
		row.appendPart(resultEditor);
		appendPart(row);

		matchWholeRegionToggle = new MiToggleButton();
		matchWholeRegionToggle.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		MiLabeledWidget labeledWidget = new MiLabeledWidget(matchWholeRegionToggle, "Match region");
		labeledWidget.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		appendPart(labeledWidget);

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
		appendPart(labeledWidget);
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are
					 * generated by:
					 * <pre>
					 *    matchWholeRegionToggle
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

		if (action.getActionSource() == matchWholeRegionToggle)
			{
			matchWholeRegion = Utility.toBoolean(matchWholeRegionToggle.getValue());
			}
		else if (action.getActionSource() == sourceEditor)
			{
			updateFromUI();
			}
		else if (action.getActionSource() == resultEditor)
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
		else if (action.hasActionType(Mi_VALUE_CHANGED_ACTION))
			{
			updateFromUI();
			}

		dispatchAction(Mi_VALUE_CHANGED_ACTION);

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

		int index = -1;
		int lastIndex = 0;
		while ((index = sourcePattern.indexOf(".", lastIndex)) > -1)
			{
			String name = sourcePattern.substring(lastIndex, index);
			MiPart part = sourceEditor.getPart(name);
			lastIndex = index + 1;
			// FIX: need angle
			}

		sourcePattern = "";
		if (sourceEditor.getNumberOfParts() > 0)
			sourcePattern = sourceEditor.getPart(0).getName().intern();
		matchWholeRegionToggle.setValue("" + matchWholeRegion);
		percentOfTimeWhenValidField.setValue(percentOfTimeWhenValid + "%");
		isUpdatingUI = false;
		}
					/**------------------------------------------------------
	 				 * Updates this rule from it's graphics
					 *------------------------------------------------------*/
	private		void		updateFromUI()
		{
		MiEditorIterator iterator = new MiEditorIterator(sourceEditor);
		MiPart part = null;
		sourcePattern = new String();
		while ((part = iterator.getNext()) != null)
			{
			String name = ((MiWidget )part.getAttachment(0)).getValue().intern();
			part.setName(name);
			//if (sourcePattern.length() > 0)
				//sourcePattern += ".";
			sourcePattern = name;

			part.getPoint(0, tmpPt0);
			part.getPoint(1, tmpPt1);
			double angle = Utility.getAngle(tmpPt1.y - tmpPt0.y, tmpPt1.x - tmpPt0.x);
//System.out.println("\n\n\n");
//System.out.println("part = " + part);
//System.out.println("angle = " + angle);
//System.out.println("\n\n\n");
			part.setResource(Mi_GROW_PROTOTYPE_GRAPHIC_ANGLE, "" + angle);

			sourcePatternGraphics = part;
			}

		iterator = new MiEditorIterator(resultEditor);
		resultPattern = new String();
		resultPatternGraphics = new MiContainer();
		while ((part = iterator.getNext()) != null)
			{
			String name = part.getName();
			if (resultPattern.length() > 0)
				resultPattern += ".";
			resultPattern += name;


			resultPatternGraphics.appendPart(part);
			}

		matchWholeRegion = matchWholeRegionToggle.isSelected();
		isEmpty = sourceEditor.getNumberOfParts() == 0;
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
					 * @param entity	the entity that defines this rule
					 *------------------------------------------------------*/
	public		void		fromEntity(MiiModelEntity entity)
		{
		matchWholeRegion = Utility.toBoolean(entity.getPropertyValue(Mi_MATCH_WHOLE_REGION_NAME));
		percentOfTimeWhenValid 
			= Utility.toDouble(entity.getPropertyValue(Mi_FREQUENCY_OF_VALIDITY_NAME));
		updateUI();
		isEmpty = sourceEditor.getNumberOfParts() == 0;
		}
					/**------------------------------------------------------
	 				 * Generates an entity form this rule.
					 * @return 		the entity that defines this rule
					 *------------------------------------------------------*/
	public		MiiModelEntity	toEntity()
		{
		MiiModelEntity entity = new MiModelEntity();
		
		entity.setPropertyValue(Mi_TYPE_NAME, MiGrowWindow.Mi_RULE_TYPE_NAME);
		entity.setPropertyValue(Mi_MATCH_WHOLE_REGION_NAME, Utility.toString(matchWholeRegion));
		entity.setPropertyValue(Mi_FREQUENCY_OF_VALIDITY_NAME, Utility.toString(percentOfTimeWhenValid));
		return(entity);
		}
					/**------------------------------------------------------
	 				 * Gets a string describing this rule.
					 * @return		the string description
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(toEntity().toString());
		}
	}

