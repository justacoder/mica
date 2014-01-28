
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
import java.util.Vector; 
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Pair;
import java.awt.Container;
import java.applet.Applet;

/**----------------------------------------------------------------------------------------------
 * This class implements an interface for examining and modifying a number of widgets, graphics
 * and semantic data.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiWidgetBrowser extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiWidgetBrowserWindow. Supported command line parameters
					 * are:
					 * -file		load this file on startup
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String filename = Utility.getCommandLineArgument(args, "-file");
		String title = Utility.getCommandLineArgument(args, "-title");
		MiWidgetBrowserWindow window = new MiWidgetBrowserWindow(title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiWidgetBrowserWindow. Supported html file parameters are:
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
		MiWidgetBrowserWindow window = new MiWidgetBrowserWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 *
 *----------------------------------------------------------------------------------------------*/
class MiWidgetBrowserWindow extends MiEditorWindow implements MiiDisplayNames, MiiNames
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
	private static final String	Mi_WIDGET_BROWSER_ABOUT_MSG
					= "Mi_WIDGET_BROWSER_ABOUT_MSG";
						

	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
	{
	new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE,			"Widget Browser"),
	new Pair( Mi_WIDGET_BROWSER_ABOUT_MSG,
		"\n\nThis utility program is part of the Mica Appletcation Suite.\n\n"
		+ "This program displays the attributes and widget-specific properties\n"
		+ "of various types of widgets and the affect of their values on the\n"
		+ "appearance and behavior of the widget.\n\n\n"
		+ "Version 1.0      June 3, 1998")
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
	private		MiPart		dialogBox;
	private		MiTreeList	partClassNamePalette;
	private		MiPartTestPanel	partTestPanel;
	private		Strings		listOfClassNamesToTest = new Strings(
						"com.swfm.mica.MiBox\n" +
						"com.swfm.mica.MiComboBox\n" +
						"com.swfm.mica.MiLabel\n" +
						"	com.swfm.mica.MiTextField\n" +
						"	com.swfm.mica.MiPushButton\n" +
						"	com.swfm.mica.MiCheckBox\n" +
						"	com.swfm.mica.MiCycleButton\n" +
						"	com.swfm.mica.MiCircleToggleButton\n" +
						"	com.swfm.mica.MiToggleButton\n" +
						"com.swfm.mica.MiSpinButtons\n" +
						"com.swfm.mica.MiSpinBox\n" +
						"com.swfm.mica.MiSlider\n" +
						"com.swfm.mica.MiGauge"
						);




					/**------------------------------------------------------
	 				 * Constructs a new MiWidgetBrowserWindow.
					 * @param awtContainer	the container to add this to
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiWidgetBrowserWindow( 
						Container awtContainer, 
						String title, 
						MiBounds screenSize)
		{
		super(MiUtility.getFrame(awtContainer),
			title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, 
			screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);

		awtContainer.add("Center", (java.awt.Component )getCanvas().getNativeComponent());
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiLifeWindow.
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiWidgetBrowserWindow(String title, MiBounds screenSize)
		{
		super(title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title,
			 screenSize);
		setup();
		}
	public		void		setListOfClassNamesToTest(Strings classNames)
		{
		listOfClassNamesToTest = classNames;
		partClassNamePalette.removeAllItems();
		appendItemsToPalette(listOfClassNamesToTest);
		partClassNamePalette.selectItem(0);
		}
	public		Strings		getListOfClassNamesToTest()
		{
		return(listOfClassNamesToTest);
		}

					/**------------------------------------------------------
	 				 * Setup the window, creating the menubar, status bar,
					 * palette for the rules, the grid for the cells... 
					 *------------------------------------------------------*/
	protected 	void		setup()
		{
		MiSystem.loadPropertiesFile("MiWidgetBrowser.mica");

		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// Let the MiEditorWindow superclass build the default window.
		// ---------------------------------------------------------------
		buildEditorWindow(true, true, false, true, true, false, null, null, null, null, null);
		setListOfClassNamesToTest(listOfClassNamesToTest);

		// ---------------------------------------------------------------
		// Setup the 'About' dialog box
		// ---------------------------------------------------------------
		MiSystem.getHelpManager().setAboutDialog(MiToolkit.createAboutDialog(this, 
				Mi_DEFAULT_WINDOW_BORDER_TITLE, 
				MiSystem.getCompanyLogo(), Mi_WIDGET_BROWSER_ABOUT_MSG, false));
		}
					/**------------------------------------------------------
	 				 * Makes the palette. This is called from the super class's
					 * buildEditorWindow method. For this application this is the
					 * list of rules.
					 * @return		the rule list
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultPalette()
		{
		partClassNamePalette = new MiTreeList(16, false);
		partClassNamePalette.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		return(new MiScrolledBox(partClassNamePalette));
		}
					/**------------------------------------------------------
	 				 * Makes the main panel. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the main panel
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultEditorPanel()
		{
		partTestPanel = new MiPartTestPanel();
		return(partTestPanel);
		}
					/**------------------------------------------------------
	 				 * Make the menubar. Tailor it's menus for this application.
					 * This is called from the super class's buildEditorWindow
					 * method.
					 * @return		the menubar
					 *------------------------------------------------------*/
	protected	MiEditorMenuBar	makeDefaultMenuBar()
		{
		MiEditorMenu[] pulldowns = new MiEditorMenu[2];
		// ---------------------------------------------------------------
		// Make and modify the file menu so that only the 'Quit...' option is displayed
		// ---------------------------------------------------------------
		pulldowns[0] = new MiFileMenu(this, this);
		pulldowns[0].removeAllMenuItemsWithCommandsExcept(
					new Strings(Mi_QUIT_COMMAND_NAME));

		// ---------------------------------------------------------------
		// Make and modify the help menu so that only the 'About...' option is displayed
		// By constructing with only one parameter, we will cause the fowarding 
		// of command messages to MiHelpMenuCommands, on which we can rely  
		// to call MiSystem.getHelpManager() to post the About dialog box.
		// ---------------------------------------------------------------
		pulldowns[1] = new MiHelpMenu(this);
		pulldowns[1].removeAllMenuItemsWithCommandsExcept(
					new Strings(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME));
		return(new MiEditorMenuBar(pulldowns));
		}
	protected	void		appendItemsToPalette(Strings items)
		{
		String parentItem = null;
		Strings parentItems = new Strings();
		int index = 0;
		for (int i = 0; i < items.size(); ++i)
			{
			String item = items.elementAt(i);
			int numTabs = Utility.numOccurancesOf(item, '\t', 0, item.length());
			int childNumTabs = 0;
			if (i + 1 < items.size())
				{
				childNumTabs = Utility.numOccurancesOf(
					items.elementAt(i + 1), '\t', 0, items.elementAt(i + 1).length());
				}
			boolean hasChildren = childNumTabs > numTabs;
			if ((numTabs > 0) && (numTabs - 1 < parentItems.size()))
				parentItem = parentItems.elementAt(numTabs - 1);
			else
				parentItem = null;

			String className = item;
			String name = className;
			if ((index = name.lastIndexOf('.')) != -1)
				name = name.substring(index + 1);

			partClassNamePalette.addItem(name, null, className, parentItem, hasChildren);

			if (numTabs < parentItems.size())
				parentItems.setElementAt(item, numTabs);
			else
				parentItems.addElement(item);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		pushMouseAppearance(Mi_WAIT_CURSOR, getClass().getName());

		partTestPanel.setTestedPart(
			(MiPart )Utility.makeInstanceOfClass(
			((String )partClassNamePalette.getSelectedItemTag()).trim()));

		popMouseAppearance(getClass().getName());
		return(true);
		}
	}
class MiPartTestPanel extends MiWidget implements MiiActionHandler
	{
	private		MiPart			testPart;
	private		MiEditor		displayPanel;
	private		MiModelPropertyViewManager	propertyPanel;
	private		MiShapeAttributesPanel	attributesPanel;
	private		MiTabbedFolder		propertyFolder;


	public				MiPartTestPanel()
		{
		build();
		}

	public		void		setTestedPart(MiPart testPart)
		{
		if (this.testPart != null)
			this.testPart.removeActionHandlers(this);

		this.testPart = testPart;
		testPart.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		testPart.appendActionHandler(this, Mi_TEXT_CHANGE_ACTION);
		testPart.appendActionHandler(this, Mi_GEOMETRY_CHANGE_ACTION);

		// Update property panel for this part...
		propertyPanel.setModel(null);
		propertyPanel.setModel(new MiPartToModel(testPart));

		// Display this part in the display panel
		displayPanel.removeAllParts();
		displayPanel.appendPart(testPart);
		displayPanel.setWorldBounds(displayPanel.getDeviceBounds());
		displayPanel.setUniverseBounds(displayPanel.getWorldBounds());
		testPart.setCenter(displayPanel.getWorldBounds().getCenter());

		if (testPart.getBounds().isLargerSizeThan(displayPanel.getUniverseBounds()))
			displayPanel.setUniverseBounds(displayPanel.getUniverseBounds().union(testPart.getBounds()));

		attributesPanel.setTargetShape(testPart);
		}
	protected	void		build()
		{
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementVSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(1);
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(rowLayout);

		MiWidget panel = buildDisplayPanel();
		panel.setInsetMargins(0);
		appendPart(panel);

		propertyPanel = buildPropertyPanel();

		attributesPanel = new MiShapeAttributesPanel();
		attributesPanel.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);

		propertyFolder = new MiTabbedFolder();
		propertyFolder.appendFolder("Properties", propertyPanel.getView());
		propertyFolder.appendFolder("Attributes", attributesPanel);
		propertyFolder.setInsetMargins(new MiMargins(4));
		appendPart(propertyFolder);
		}
	protected	MiWidget	buildDisplayPanel()
		{
		MiEditor editor = new MiEditor();
		displayPanel = editor;
		editor.setBackgroundColor(MiColorManager.veryLightGray);
		editor.setLayout(new MiSizeOnlyLayout());
		MiScrolledBox scrolledBox = new MiScrolledBox(editor);
		//scrolledBox.setInsetMargins(0);
		scrolledBox.setHScrollBarDisplayPolicy(Mi_DISPLAY_ALWAYS);
		scrolledBox.setVScrollBarDisplayPolicy(Mi_DISPLAY_ALWAYS);
		editor.setDeviceBounds(new MiBounds(0,0,150,400));
		editor.setPreferredSize(new MiSize(150,400));
		editor.setMinimumSize(new MiSize(150,100));
		editor.appendEventHandler(new MiIDragBackgroundPan());
		editor.appendEventHandler(new MiIZoomAroundMouse());
		editor.appendEventHandler(new MiIPan());
		editor.appendEventHandler(new MiIJumpPan());
		return(scrolledBox);
		}
	protected	MiModelPropertyViewManager	buildPropertyPanel()
		{
		MiModelPropertyViewManager panel = new MiModelPropertyViewManager();
		panel.setView(new MiWidget());
		panel.setApplyingEveryChange(true);
		return(panel);
		}
	public		boolean		processAction(MiiAction action)
		{
		getContainingEditor().pushMouseAppearance(Mi_WAIT_CURSOR, getClass().getName());
		if (action.getActionSource() == attributesPanel)
			{
			attributesPanel.applyToTargetShape(testPart);
			}
		else if (action.getActionSource() == testPart)
			{
			propertyPanel.updateView();
			// Update both attribute and geometry fields
			attributesPanel.setTargetShape(testPart);
			}
		else
			{
			attributesPanel.setDisplayedAttributes(testPart.getAttributes());
			}

		if (testPart.getBounds().isLargerSizeThan(displayPanel.getUniverseBounds()))
			displayPanel.setUniverseBounds(displayPanel.getUniverseBounds().union(testPart.getBounds()));

		testPart.setCenter(displayPanel.getUniverseBounds().getCenter());

		getContainingEditor().popMouseAppearance(getClass().getName());
		return(true);
		}
	}

