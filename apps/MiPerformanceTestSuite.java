
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
import java.awt.Frame; 
import java.awt.Graphics; 
import java.util.Vector; 
import java.util.Enumeration; 
import com.swfm.mica.util.FastVector; 
import com.swfm.mica.util.Pair; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Fifo; 
import java.awt.Container;
import java.applet.Applet;

/*
TO DO:
	com.swfm.mica lightwieghts
	com.swfm.mica very lightwieghts

	long test

	hashtable, ...
	flow ctl (switches, dereferences)
	StringBuffer operations
	IO operations

	Add additional platform spec for processor (and?) speed
*/

/**----------------------------------------------------------------------------------------------
 * This class implements a suite of performance and other types of tests
 * and allows the user to browse the results.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPerformanceTestSuite extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiPerformanceTestSuiteWindow. Supported command line 
					 * parameters are:
					 * -file		load this file on startup
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String filename = Utility.getCommandLineArgument(args, "-file");
		String title = Utility.getCommandLineArgument(args, "-title");
		MiPerformanceTestSuiteWindow window = new MiPerformanceTestSuiteWindow(
			title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		loadSpecifiedFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiPerformanceTestSuiteWindow. Supported html file 
					 * parameters are:
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
		MiPerformanceTestSuiteWindow window = new MiPerformanceTestSuiteWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		loadSpecifiedFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * Loads the specified file, if any.
					 * @param window	the window
					 * @param filename	the name of the file or null
					 *------------------------------------------------------*/
	protected static void		loadSpecifiedFile(
						MiPerformanceTestSuiteWindow window, String filename)
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
					"Usage: MiPerformanceTestSuite [-file <filename>] [-title <window title>]");
				System.exit(1);
				}
			}
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a suite of performance and other types of tests
 * and allows the user to browse the results.
 *
 *----------------------------------------------------------------------------------------------*/
class MiPerformanceTestSuiteWindow extends MiFileEditorWindow implements MiiAnimatable, MiiDisplayNames
	{
	// ---------------------------------------------------------------
	// Define the names of some commands
	// ---------------------------------------------------------------
	private static final String 	Mi_NOT_NORMALIZED_ROWS_COMMAND_NAME    = "NotNormalized rows";
	private static final String 	Mi_NOT_NORMALIZED_COLUMNS_COMMAND_NAME = "NotNormalized columns";


	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	private static final String	Mi_REPORTS_OPTION_NAME		= "Mi_REPORTS_OPTION_NAME";
	private static final String	Mi_RUN_ALL_TESTS_NAME		= "Mi_RUN_ALL_TESTS_NAME";
	private static final String	Mi_NORMALIZED_ROWS_NAME		= "Mi_NORMALIZED_ROWS_NAME";
	private static final String 	Mi_NORMALIZED_COLUMNS_NAME	= "Mi_NORMALIZED_COLUMNS_NAME";
	private static final String	Mi_PERFORMANCE_SUITE_ABOUT_MSG	= "Mi_PERFORMANCE_SUITE_ABOUT_MSG"; 
	private static final String	Mi_CHANGE_OPEN_FILE_TO_SAVED_AS_FILE
						= "Mi_CHANGE_OPEN_FILE_TO_SAVED_AS_FILE"; 
	private static final String	Mi_SELECT_HOST_HARDWARE_PLATFORM_MSG
						= "Mi_SELECT_HOST_HARDWARE_PLATFORM_MSG";
	private static final String	Mi_PLATFORM_PROCESSOR_LABEL_NAME
						= "Mi_PLATFORM_PROCESSOR_LABEL_NAME";
	private static final String	Mi_PLATFORM_PROCESSOR_OPTIONS_NAME
						= "Mi_PLATFORM_PROCESSOR_OPTIONS_NAME";
	private static final String	Mi_PLATFORM_PROCESSOR_SPEED_LABEL_NAME
						= "Mi_PLATFORM_PROCESSOR_SPEED_LABEL_NAME";
	private static final String	Mi_PLATFORM_PROCESSOR_SPEED_OPTIONS_NAME
						= "Mi_PLATFORM_PROCESSOR_SPEED_OPTIONS_NAME";
	private static final String	Mi_HOST_PROCESSOR_DETERMINATION_FORM_TITLE
						= "Mi_HOST_PROCESSOR_DETERMINATION_FORM_TITLE";
	private static final String	Mi_PLATFORM_PROCESSOR_INFO_MSG
						= "Mi_PLATFORM_PROCESSOR_INFO_MSG";

	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
		{
		new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE		, "Performance Test Suite"),
		new Pair( Mi_REPORTS_OPTION_NAME			, "Reports..."),
		new Pair( Mi_RUN_ALL_TESTS_NAME				, "Run all tests"),
		new Pair( Mi_NORMALIZED_ROWS_NAME			, "Normalized rows"),
		new Pair( Mi_NORMALIZED_COLUMNS_NAME			, "Normalized columns"),
		new Pair( Mi_CHANGE_OPEN_FILE_TO_SAVED_AS_FILE		, "false"),
		new Pair( Mi_HOST_PROCESSOR_DETERMINATION_FORM_TITLE	, 
			"Host Processor Determination"),
		new Pair( Mi_SELECT_HOST_HARDWARE_PLATFORM_MSG		, 
			"Please select the hardware platform\nthis test suite is running on:\n\n"),
		new Pair( Mi_PLATFORM_PROCESSOR_LABEL_NAME		, "Processor"),
		new Pair( Mi_PLATFORM_PROCESSOR_OPTIONS_NAME		,
			"Unknown\nUltra Sparc I\nUltra Sparc II\nPentium Pro\n"
			+ "Pentium II\nPentium\nPower PC"),
		new Pair( Mi_PLATFORM_PROCESSOR_SPEED_LABEL_NAME	, "Speed (Mhz)"),
		new Pair( Mi_PLATFORM_PROCESSOR_SPEED_OPTIONS_NAME	, 
			"133\n140\n166\n170\n200\n233\n266\n300\n333\n400\n500"),
		new Pair( Mi_PLATFORM_PROCESSOR_INFO_MSG		, 
			"\n(Just select \"Unknown\" if you are not sure)\n"),
		new Pair( Mi_PERFORMANCE_SUITE_ABOUT_MSG		,
			"\n\nThis test suite is part of the Mica Graphics Appletcation Suite.\n\n"
			+ "These tests displays the results of a number of different\n"
			+ "performance tests that exercise Java, the AWT and Mica.\n\n\n"
			+ "Version 1.0      March 2, 1998")
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
	private static final String	Mi_TEST_SUITE_NAME	= "testSuite";
	private static final String	Mi_PLATFORM_NAME	= "platform";
	private static final String	Mi_UNKNOWN_PROCESSOR_NAME	= "Unknown";
	private static final String	Mi_UNKNOWN_PROCESSOR_SPEED_NAME	= "?";

	private static final String	Mi_PERFORMANCE_FILE_HEADER_MSG	
								= "!MiPerformanceTestSuite: version 1.0";
	private static final String	Mi_PERFORMANCE_DEFAULT_FILENAME	
								= "mica.perf";

	// ---------------------------------------------------------------
	// Define the class variables this class will use.
	// ---------------------------------------------------------------
	private		String		filename;
	private		String		nameOfHostPlatform;
	private		String		platformProcessor;
	private		String		platformProcessorSpeed;
	private		MiNativeDialog	drawingTestEditorDialog;
	private		MiEditor 	drawingTestEditor;
	private		MiEditorStatusBar statusBar;
	private		MiWidget	elapsedTimeDisplayField;
	private		MiGauge		percentCompletedDisplayField;

	private		boolean		normalizeRows;
	private		boolean		normalizeColumns;

	private		boolean		hasChanged;

	private		MiAnimator	animator;

	private		Fifo		runningTestSuiteQueue	= new Fifo();
	private		int		runningPlatformIndex	= 0;
	private		boolean		runningTestInitialized;
	private		double		runningTime;
	private		double		runStartTime;
	private		MiiTestSuite	runningTestSuite;
	private		MiTable		runningReportCard;

	private		MiiModelDocument openDocument;
	private		MiiModelIOFormatManager ioFormatManager;
	private		Strings		platforms		= new Strings();

	private		MiTabbedFolder	reportCardFolder;
	private		MiParts		reportCards		= new MiParts();
	private		Vector		testSuites		= new Vector();
	private		MiFileChooser	fileChooser;


					/**------------------------------------------------------
	 				 * Constructs a new MiPerformanceTestSuiteWindow.
					 * @param awtContainer	the container to add this to
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiPerformanceTestSuiteWindow( 
						Container awtContainer, 
						String title, 
						MiBounds screenSize)
		{
		super(	MiUtility.getFrame(awtContainer),
			title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, 
			screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE,
			"perf",
			Mi_PERFORMANCE_DEFAULT_FILENAME, 
			Utility.toBoolean(MiSystem.getProperty(Mi_CHANGE_OPEN_FILE_TO_SAVED_AS_FILE)));
		awtContainer.add("Center", (java.awt.Component )getCanvas().getNativeComponent());
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiPerformanceTestSuiteWindow.
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiPerformanceTestSuiteWindow(String title, MiBounds screenSize)
		{
		super(	title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, 
			screenSize,
			"perf", 
			Mi_PERFORMANCE_DEFAULT_FILENAME, 
			Utility.toBoolean(MiSystem.getProperty(Mi_CHANGE_OPEN_FILE_TO_SAVED_AS_FILE)));
		setup();
		}
					/**------------------------------------------------------
	 				 * Setup the window, creating the menubar, status bar,
					 * toolbar, the table for the results... 
					 *------------------------------------------------------*/
	protected 	void		setup()
		{
		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		testSuites.addElement(new MiAWTGraphicsTestSuite());
		testSuites.addElement(new MiMICAGraphicsTestSuite());
		testSuites.addElement(new MiMICALiteGraphicsTestSuite());
		testSuites.addElement(new MiMICAVeryLiteGraphicsTestSuite());
		testSuites.addElement(new MiMemorySizeTestSuite());
		testSuites.addElement(new MiMemoryTestSuite());
		testSuites.addElement(new MiFlowControlTestSuite());
		testSuites.addElement(new MiArithmeticTestSuite());
		testSuites.addElement(new MiMathTestSuite());
		testSuites.addElement(new MiUtilClassesTestSuite());

		buildEditorWindow();

		animator = new MiAnimator(this, 100);
		MiAnimationManager.addAnimator(this, animator);

		ioFormatManager = new MiModelIOFormatManager();

		makeNameOfHostPlatform();
		appendPlatform(nameOfHostPlatform);

		setCommandSensitivity(Mi_SAVE_COMMAND_NAME, false);

		openDocument = new MiModelDocument();

		makeEditorForDrawingTests();
		}
					/**------------------------------------------------------
	 				 * Makes the menubar. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the menu bar
					 *------------------------------------------------------*/
	protected	MiEditorMenuBar	makeDefaultMenuBar()
		{
		MiEditorMenu[] pulldowns = new MiEditorMenu[4];
		pulldowns[0] = new MiFileMenu(this, this);
		Strings commandsToKeep = new Strings();
		commandsToKeep.addElement(Mi_OPEN_COMMAND_NAME);
		commandsToKeep.addElement(Mi_SAVE_COMMAND_NAME);
		commandsToKeep.addElement(Mi_SAVE_AS_COMMAND_NAME);
		commandsToKeep.addElement(Mi_QUIT_COMMAND_NAME);
		pulldowns[0].removeAllMenuItemsWithCommandsExcept(commandsToKeep);

		MiEditorMenu viewMenu = new MiEditorMenu(Mi_VIEW_MENU_DISPLAY_NAME, this);
		viewMenu.startRadioButtonSection(0);
		viewMenu.appendBooleanMenuItem(Mi_NORMALIZED_ROWS_NAME, this,
			Mi_NORMALIZED_ROWS_NAME, Mi_NOT_NORMALIZED_ROWS_COMMAND_NAME);
		viewMenu.appendBooleanMenuItem(Mi_NORMALIZED_COLUMNS_NAME, this,
			Mi_NORMALIZED_COLUMNS_NAME, Mi_NOT_NORMALIZED_COLUMNS_COMMAND_NAME);
		viewMenu.endRadioButtonSection();
		pulldowns[1] = viewMenu;

		MiEditorMenu testsMenu = new MiEditorMenu("&Tests", this);
		testsMenu.appendMenuItem(Mi_RUN_ALL_TESTS_NAME, this, Mi_RUN_ALL_TESTS_NAME);
		for (int i = 0; i < testSuites.size(); ++i)
			{
			MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
			testsMenu.appendMenuItem(testSuite.getName(), this, testSuite.getName());
			}
		pulldowns[2] = testsMenu;

		pulldowns[3] = new MiHelpMenu(this, this);
		pulldowns[3].removeAllMenuItemsWithCommandsExcept(new Strings(
					Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME));

		return(new MiEditorMenuBar(pulldowns));
		}
					/**------------------------------------------------------
	 				 * Makes the toolbar. This is called from the super class's
					 * buildEditorWindow method. This application has no toolbar
					 * so this returns null.
					 * @return		the tool bar
					 *------------------------------------------------------*/
	protected	MiToolBar	makeDefaultToolBar()
		{
		return(null);
		}
					/**------------------------------------------------------
	 				 * Makes the main panel. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the main panel
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultEditorPanel()
		{
		reportCardFolder = new MiTabbedFolder();
		for (int i = 0; i < testSuites.size(); ++i)
			{
			MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
			reportCardFolder.appendFolder(testSuite.getName());
			MiPart container = reportCardFolder.getFolderContents(testSuite.getName());
			reportCards.addElement(createReportCardContents(container, testSuite));
			}
		return(reportCardFolder);
		}
					/**------------------------------------------------------
	 				 * Creates a table for display of the results of the given
					 * test suite and puts it in the given container.
					 * @param container	the container
					 * @param testSuite	the testSuite
					 * @return 		the table
					 *------------------------------------------------------*/
	protected	MiTable		createReportCardContents(MiPart container, MiiTestSuite testSuite)
		{
		int numColumns = testSuite.getNumberOfTestResults();
		int numDisplayedColumns = Math.min(numColumns, 6);
		int numVisibleRows = 5;
		MiTable reportCard = new MiTable();
		reportCard.setPreferredSize(new MiSize(700, 400));
		reportCard.getSelectionManager().setMaximumNumberSelected(0);
		reportCard.getSelectionManager().setBrowsable(false);
		reportCard.getTableWideDefaults().setHorizontalJustification(Mi_LEFT_JUSTIFIED);
		reportCard.getTableWideDefaults().setHorizontalSizing(Mi_SAME_SIZE);
		reportCard.getMadeRowDefaults(MiTable.ROW_HEADER_NUMBER).setInsetMargins(
			reportCard.getTableWideDefaults().getInsetMargins());
		reportCard.getMadeColumnDefaults(MiTable.COLUMN_HEADER_NUMBER).setInsetMargins(
			reportCard.getTableWideDefaults().getInsetMargins());
		reportCard.setTotalNumberOfColumns(numColumns);
		reportCard.setHasFixedTotalNumberOfColumns(true);
		reportCard.setMinimumNumberOfVisibleColumns(numDisplayedColumns);
		reportCard.setPreferredNumberOfVisibleRows(numVisibleRows);
		reportCard.getRowHeaderBackground().setBorderLook(Mi_NONE);
		reportCard.getRowHeaderBackground().setBackgroundColor(Mi_TRANSPARENT_COLOR);
		reportCard.getColumnHeaderBackground().setBorderLook(Mi_NONE);
		reportCard.getColumnHeaderBackground().setBackgroundColor(Mi_TRANSPARENT_COLOR);

		MiRectangle rect = new MiRectangle();
		rect.setBackgroundColor("darkCyan");
		reportCard.getBackgroundManager().appendRowRepeatingBackground(rect, 0, 0, -1, 2, false);
		reportCard.getBackgroundManager().appendRowRepeatingBackground(
			rect, 0, MiTable.COLUMN_HEADER_NUMBER, -1, 2, false);

		rect = new MiRectangle();
		rect.setBackgroundColor("lightCyan");
		reportCard.getBackgroundManager().appendRowRepeatingBackground(rect, 1, 0, -1, 2, false);
		reportCard.getBackgroundManager().appendRowRepeatingBackground(
			rect, 1, MiTable.COLUMN_HEADER_NUMBER, -1, 2, false);

		rect = new MiRectangle();
		rect.setBackgroundColor("white");
		reportCard.getBackgroundManager().appendRowBackground(rect, MiTable.ROW_HEADER_NUMBER);

		MiLine line = new MiLine();
		line.setBackgroundColor("black");
		line.setLineWidth(2);
		reportCard.getBackgroundManager().appendInnerVerticalLines(line);
		reportCard.getBackgroundManager().appendInnerVerticalLines(
						line,
						0,	// columnNumber, 
						MiTable.ROW_HEADER_NUMBER, 
						MiTable.ROW_HEADER_NUMBER, 
						1);

		container.appendPart(new MiScrolledBox(reportCard));
		MiRowLayout layout = new MiRowLayout();
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		container.setLayout(layout);
		createReportCardRowHeaders(reportCard, testSuite);
		return(reportCard);
		}
					/**------------------------------------------------------
	 				 * Creates the headers for the given table that displays 
					 * the results of the given test suite.
					 * @param reportCard	the table
					 * @param testSuite	the testSuite
					 *------------------------------------------------------*/
	protected	void		createReportCardRowHeaders(MiTable reportCard, MiiTestSuite testSuite)
		{
		int numberOfTestResults = testSuite.getNumberOfTestResults();
		for (int i = 0; i < numberOfTestResults; ++i)
			{
			MiText label = new MiText(testSuite.getNameOfTestResult(i));
			reportCard.getMadeColumnDefaults(i).setStatusHelp(
				new MiHelpInfo(testSuite.getDescriptionsOfTestResult(i)));
			reportCard.addCell(MiTable.ROW_HEADER_NUMBER, i, label);
			}
		}
					/**------------------------------------------------------
	 				 * Adds a row for the given platform name (jdk version, 
					 * processer, ...) to all of the report cards (tables).
					 * @param platform	the name of a host platform
					 *------------------------------------------------------*/
	protected	void		appendPlatform(String platform)
		{
		platforms.addElement(platform);
		int platformRow = platforms.indexOf(platform);
		for (int i = 0; i < testSuites.size(); ++i)
			{
			MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
			MiTable reportCard = getReportCard(testSuite);
			int numberOfTestResults = testSuite.getNumberOfTestResults();

			MiText label = new MiText(platform);
			reportCard.addCell(platformRow, MiTable.COLUMN_HEADER_NUMBER, label);

			MiParts defaultValues = new MiParts();
			for (int j = 0; j < numberOfTestResults; ++j)
				defaultValues.addElement(new MiText("-"));
			reportCard.appendItems(defaultValues);
			}
		}
					/**------------------------------------------------------
	 				 * Makes the status bar. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the status bar
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultStatusBar()
		{
		statusBar = new MiEditorStatusBar();
		statusBar.setOverlayStatusField(new MiStatusBarFocusStatusField(this));

		statusBar.setStateField(30);
		statusBar.setOverlayStatusField(statusBar.getPart(0));
		MiStatusBar numPrimsBar = new MiBasicStatusField("Elapsed time secs:.8");
		elapsedTimeDisplayField = numPrimsBar.getField(0);
		elapsedTimeDisplayField.setValue("0");
		//percentCompletedDisplayField = numPrimsBar.getField(0);
		percentCompletedDisplayField = new MiGauge();
		numPrimsBar.appendField(percentCompletedDisplayField, 20);
		percentCompletedDisplayField.getLED().setColor(Mi_TRANSPARENT_COLOR);
		percentCompletedDisplayField.getLED().setBackgroundColor("green");
		percentCompletedDisplayField.getLED().setBorderLook(Mi_NONE);
		percentCompletedDisplayField.setNumberOfLEDs(12);
		MiToolkit.setBackgroundAttributes(percentCompletedDisplayField, 
				MiiToolkit.Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES);
		percentCompletedDisplayField.setPropertyValues(
				MiSystem.getPropertiesForClass("MiStatusBarField"));
		percentCompletedDisplayField.setLabel(new MiText("0"));
		percentCompletedDisplayField.setNormalizedValue(0);
		statusBar.appendPart(numPrimsBar);
		return(statusBar);
		}
					/**------------------------------------------------------
	 				 * Makes a dialog window into which the drawing tests will
					 * draw. Assigns this to drawingTestEditorDialog and
					 * drawingTestEditor.
					 *------------------------------------------------------*/
	protected	void		makeEditorForDrawingTests()
		{
		drawingTestEditorDialog = new MiNativeDialog(
			this, "Drawing Test Editor", false);
		drawingTestEditorDialog.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));
		drawingTestEditorDialog.setDeviceBounds(new MiBounds(0,0,400,400));
		drawingTestEditor = drawingTestEditorDialog;
		}
					/**------------------------------------------------------
	 				 * Makes the name of the host platform by inquiring the java
					 * system properties and by asking the user for information
					 * about the processor and speed. Assigns the name to
					 * the nameOfHostPlatform class variable.
					 *------------------------------------------------------*/
	protected	void		makeNameOfHostPlatform()
		{
		String osArch = System.getProperty("os.arch");
		String osName = System.getProperty("os.name");
		String osVersion = System.getProperty("os.version");
		String javaVersion = System.getProperty("java.version");

		MiContainer main = new MiContainer();
		MiColumnLayout mainLayout = new MiColumnLayout();
		mainLayout.setElementHSizing(Mi_NONE);
		main.setLayout(mainLayout);
		MiText qMsg = new MiText(Mi_SELECT_HOST_HARDWARE_PLATFORM_MSG);
		qMsg.setFontPointSize(18);
		qMsg.setFontBold(true);
		main.appendPart(qMsg);
		MiContainer c = new MiContainer();
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		gridLayout.setElementHSizing(Mi_NONE);
		c.setLayout(gridLayout);
		main.appendPart(c);

		c.appendPart(new MiText(Mi_PLATFORM_PROCESSOR_LABEL_NAME));
		MiComboBox combo1 = new MiComboBox();
		combo1.setContents(new Strings(
			MiSystem.getProperty(Mi_PLATFORM_PROCESSOR_OPTIONS_NAME)));
		combo1.setRestrictingValuesToThoseInList(false);
		combo1.getList().setMaximumNumberOfVisibleRows(4);
		c.appendPart(combo1);

		c.appendPart(new MiText(Mi_PLATFORM_PROCESSOR_SPEED_LABEL_NAME));
		MiComboBox combo2 = new MiComboBox();
		combo2.setContents(new Strings(
			MiSystem.getProperty(Mi_PLATFORM_PROCESSOR_SPEED_OPTIONS_NAME)));
		combo2.setRestrictingValuesToThoseInList(false);
		combo2.getList().setMaximumNumberOfVisibleRows(4);
		c.appendPart(combo2);

		main.appendPart(new MiText(Mi_PLATFORM_PROCESSOR_INFO_MSG));

		String result = MiToolkit.postQueryDialog(
			this, Mi_HOST_PROCESSOR_DETERMINATION_FORM_TITLE, main);

		if (result.equals(Mi_CANCEL_COMMAND_NAME))
			System.exit(0);
		
		platformProcessor = combo1.getValue();
		if (Utility.isEmptyOrNull(platformProcessor))
			platformProcessor = Mi_UNKNOWN_PROCESSOR_NAME;
		platformProcessorSpeed = combo2.getValue();
		if (Utility.isEmptyOrNull(platformProcessorSpeed))
			platformProcessor = Mi_UNKNOWN_PROCESSOR_SPEED_NAME;

		nameOfHostPlatform = osArch + " " + osName + osVersion + "\n" 
			+ platformProcessor + "[" + platformProcessorSpeed + "]\n"
			+ "(Java:" + javaVersion + ")";
		}
					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are:
					 * <pre>
					 *    Mi_NORMALIZED_ROWS_NAME
					 *    Mi_NOT_NORMALIZED_ROWS_COMMAND_NAME
					 *    Mi_NORMALIZED_COLUMNS_NAME
					 *    Mi_NOT_NORMALIZED_COLUMNS_COMMAND_NAME
					 *    Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 * @overrides		MiEditorWindow#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		if (command.equals(Mi_NORMALIZED_ROWS_NAME))
			{
			normalizeRows();
			normalizeRows = true;
			}
		else if (command.equals(Mi_NOT_NORMALIZED_ROWS_COMMAND_NAME))
			{
			populateTableFromDocument(openDocument);
			normalizeRows = false;
			}
		else if (command.equals(Mi_NORMALIZED_COLUMNS_NAME))
			{
			normalizeColumns();
			normalizeColumns = true;
			}
		else if (command.equals(Mi_NOT_NORMALIZED_COLUMNS_COMMAND_NAME))
			{
			populateTableFromDocument(openDocument);
			normalizeColumns = false;
			}
		else if (command.startsWith(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME))
			{
			MiToolkit.postAboutDialog(this,
				getDefaultWindowTitle(), 
				MiSystem.getCompanyLogo(), Mi_PERFORMANCE_SUITE_ABOUT_MSG, false);
			}
		else
			{
			for (int i = 0; i < testSuites.size(); ++i)
				{
				MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
				if ((command.equalsIgnoreCase(testSuite.getName()))
					|| (command.equalsIgnoreCase(Mi_RUN_ALL_TESTS_NAME)))
					{
					runningTestSuiteQueue.append(testSuite);
					if (!command.equalsIgnoreCase(Mi_RUN_ALL_TESTS_NAME))
						{
						setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
						return;
						}
					}
				}
			super.processCommand(command);
			setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
			}
		}
					/**------------------------------------------------------
	 				 * Updates the status bar with information about the current
					 * running test suite.
					 * @param status	the test suite status
					 *------------------------------------------------------*/
	protected	void		updateStatusBar(MiTestSuiteStatus status)
		{
		if (status == null)
			{
			percentCompletedDisplayField.setValue("0");
			statusBar.setState("");
			}
		else
			{
			percentCompletedDisplayField.setValue("" + status.percentCompleted/100);
			if (status.percentCompleted < 100)
				statusBar.setState("Running test: " + runningTestSuite.getName());
			else
				statusBar.setState("Finished test: " + runningTestSuite.getName());
			}
		elapsedTimeDisplayField.setValue("Elapsed time: " 
			+ (System.currentTimeMillis() - runStartTime)/1000 + " secs");
		}
					/**------------------------------------------------------
	 				 * Gets the test suite object from the test suite name.
					 * @param testSuiteName	the name of a test suite
					 * @return		the test suite or null
					 *------------------------------------------------------*/
	protected	MiiTestSuite	getTestSuite(String testSuiteName)
		{
		for (int i = 0; i < testSuites.size(); ++i)
			{
			if (((MiiTestSuite )testSuites.elementAt(i)).getName().equals(testSuiteName))
				return((MiiTestSuite )testSuites.elementAt(i));
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the column of the test suite in the report card.
					 * @param reportCard	the reportCard
					 * @param testSuiteName	the name of a test suite
					 * @return		the column number
					 *------------------------------------------------------*/
	protected	int		getTestSuiteColumn(MiTable reportCard, String testSuiteName)
		{
		for (int i = 0; i < reportCard.getNumberOfColumns(); ++i)
			{
			if (reportCard.getCell(MiTable.ROW_HEADER_NUMBER, i).getValue().equals(testSuiteName))
				return(i);
			}
		return(-1);
		}
					/**------------------------------------------------------
	 				 * Gets the report card (table) that displats the results
					 * of the given test suite.
					 * @param testSuiteName	the test suite
					 * @return		the test suite display table
					 *------------------------------------------------------*/
	protected	MiTable		getReportCard(MiiTestSuite testSuite)
		{
		return((MiTable )reportCards.elementAt(testSuites.indexOf(testSuite)));
		}
					/**------------------------------------------------------
	 				 * Updates the report card (table) for the currently running
					 * test suite (i.e. runningTestSuite).
					 *------------------------------------------------------*/
	protected	void		updateReportCard()
		{
		int numTestResults = runningTestSuite.getNumberOfTestResults();
		MiTable reportCard = getReportCard(runningTestSuite);
		for (int i = 0; i < numTestResults; ++i)
			{
			int column = getTestSuiteColumn(reportCard, runningTestSuite.getNameOfTestResult(i));
			reportCard.getCell(runningPlatformIndex, column).setValue(
				Utility.toShortString(runningTestSuite.getTestResult(i)));
			}
		if (normalizeColumns)
			{
			normalizeColumns();
			}
		}
					/**------------------------------------------------------
	 				 * Saves the contents of the graphics editor.
					 * @param stream 	where to save the contents
					 * @param streamName 	the name of the stream
					 * @overrides		MiFileEditorWindow#save
					 *------------------------------------------------------*/
	public		boolean		save(OutputStream stream, String streamName)
		{
		saveHostPlatformToDocument(openDocument);
		if (Mi_PERFORMANCE_FILE_HEADER_MSG.equals(
			openDocument.getModelEntities().elementAt(0).getPropertyValue(
			MiiModelTypes.Mi_COMMENT_PROPERTY_NAME)))
			{
			ioFormatManager.save(openDocument, stream, null);
			}
		else
			{
			ioFormatManager.save(openDocument, stream, Mi_PERFORMANCE_FILE_HEADER_MSG);
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Loads the contents of the graphics editor from the given
					 * stream. The first line of the stream is examined to 
					 * determine the format of the file.
					 * @param stream 	where to get the contents
					 * @param streamName 	the name of the stream
					 * @overrides		MiFileEditorWindow#load
					 *------------------------------------------------------*/
	protected	void		load(BufferedInputStream stream, String streamName) throws IOException
		{
		openDocument = ioFormatManager.load(stream, streamName);
		populateTableFromDocument(openDocument);
		if (!platforms.contains(nameOfHostPlatform))
			{
			appendPlatform(nameOfHostPlatform);
			addHostPlatformToDocument(openDocument);
			}
		}
					/**------------------------------------------------------
	 				 * Opens the default, no name, file that the user can
					 * edit in but which needs to be named before saving.
					 * @overrides		MiFileEditorWindow#defaultFileOpened
					 * @see			MiFileEditorWindow#openDefaultFile
					 *------------------------------------------------------*/
	protected	void		defaultFileOpened()
		{
		if (platforms.size() > 0)
			{
			platforms.removeAllElements();
			for (int i = 0; i < testSuites.size(); ++i)
				{
				MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
				MiTable reportCard = getReportCard(testSuite);
				reportCard.removeAllCells();
				reportCard.removeAllColumnHeaders();
				}
			openDocument = new MiModelDocument();
			}
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
		if ((!flag) || (getOpenFilename() != null))
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
	 				 * Adds the information about the results of the current
					 * host platform to the given document.
					 * @param document	the document to add the results to
					 *------------------------------------------------------*/
	protected	void		addHostPlatformToDocument(MiiModelDocument document)
		{
		for (int i = 0; i < testSuites.size(); ++i)
			{
			MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
			int numTestResults = testSuite.getNumberOfTestResults();
			MiiModelEntity entity = new MiModelEntity();
			entity.setPropertyValue(Mi_PLATFORM_NAME, nameOfHostPlatform);
			entity.setPropertyValue(Mi_TEST_SUITE_NAME, testSuite.getName());
			for (int j = 0; j < numTestResults; ++j)
				{
				String name = testSuite.getNameOfTestResult(j);
				String value = "" + testSuite.getTestResult(j);
				entity.setPropertyValue(name, value);
				}
			document.appendModelEntity(entity);
			}
		}
					/**------------------------------------------------------
	 				 * Updates the information about the results of the current
					 * host platform in the given document.
					 * @param document	the document to update the results in
					 *------------------------------------------------------*/
	protected	void		saveHostPlatformToDocument(MiiModelDocument document)
		{
		for (int i = 0; i < testSuites.size(); ++i)
			{
			MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
			int numTestResults = testSuite.getNumberOfTestResults();
			MiiModelEntity entity = getEntityFromDocumentRepresentingTestSuite(
							document, nameOfHostPlatform, testSuite);
			if (entity == null)
				{
				entity = document.createModelEntity();
				entity.setPropertyValue(Mi_PLATFORM_NAME, nameOfHostPlatform);
				entity.setPropertyValue(Mi_TEST_SUITE_NAME, testSuite.getName());
				document.appendModelEntity(entity);
				}
			for (int j = 0; j < numTestResults; ++j)
				{
				String name = testSuite.getNameOfTestResult(j);
				String value = "" + testSuite.getTestResult(j);
				entity.setPropertyValue(name, value);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Normalizes the values in the rows (i.e. sets the best
					 * result to one and the worse results to a value greater
					 * than one, in proportion).
					 *------------------------------------------------------*/
	protected	void		normalizeRows()
		{
		saveHostPlatformToDocument(openDocument);
		for (int i = 0; i < testSuites.size(); ++i)
			{
			MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
			int numTestResults = testSuite.getNumberOfTestResults();
			for (int j = 0; j < platforms.size(); ++j)
				{
				String platform = platforms.elementAt(j);
				MiiModelEntity entity = getEntityFromDocumentRepresentingTestSuite(
					openDocument, platform, testSuite);
				MiTable reportCard = getReportCard(testSuite);
				double maxValue = 0;
				for (int k = 0; k < numTestResults; ++k)
					{
					String value = entity.getPropertyValue(testSuite.getNameOfTestResult(k));
					double doubleValue = Utility.toDouble(value);
					if (doubleValue > maxValue)
						maxValue = doubleValue;
					}
				for (int k = 0; k < numTestResults; ++k)
					{
					String value = entity.getPropertyValue(testSuite.getNameOfTestResult(k));
					double doubleValue = Utility.toDouble(value);
					if (doubleValue != 0)
						{
						reportCard.getCell(j, k).setValue(
							Utility.toShortString(doubleValue/maxValue));
						}
					}
				}
			}
		}
					/**------------------------------------------------------
	 				 * Normalizes the values in the columns (i.e. sets the best
					 * result to one and the worse results to a value greater
					 * than one, in proportion).
					 *------------------------------------------------------*/
	protected	void		normalizeColumns()
		{
		saveHostPlatformToDocument(openDocument);
		for (int i = 0; i < testSuites.size(); ++i)
			{
			MiiTestSuite testSuite = (MiiTestSuite )testSuites.elementAt(i);
			int numTestResults = testSuite.getNumberOfTestResults();
			for (int k = 0; k < numTestResults; ++k)
				{
				MiTable reportCard = getReportCard(testSuite);
				double maxValue = 0;
				for (int j = 0; j < platforms.size(); ++j)
					{
					String platform = platforms.elementAt(j);
					MiiModelEntity entity = getEntityFromDocumentRepresentingTestSuite(
						openDocument, platform, testSuite);
					// If this has been calculated...
					if (entity != null)
						{
						String value = entity.getPropertyValue(
							testSuite.getNameOfTestResult(k));
						double doubleValue = Utility.toDouble(value);
						if (doubleValue > maxValue)
							maxValue = doubleValue;
						}
					}
				for (int j = 0; j < platforms.size(); ++j)
					{
					String platform = platforms.elementAt(j);
					MiiModelEntity entity = getEntityFromDocumentRepresentingTestSuite(
						openDocument, platform, testSuite);
					// If this has been calculated...
					if (entity != null)
						{
						String value = entity.getPropertyValue(
							testSuite.getNameOfTestResult(k));
						double doubleValue = Utility.toDouble(value);
						if (doubleValue != 0)
							{
							if (testSuite.isMoreBetterTestResult(k))
								{
								reportCard.getCell(j, k).setValue(
									Utility.toShortString(maxValue/doubleValue));
								}
							else
								{
								reportCard.getCell(j, k).setValue(
									Utility.toShortString(doubleValue/maxValue));
								}
							}
						}
					}
				}
			}
		}
					/**------------------------------------------------------
	 				 * Gets an entity that describes the results of the given
					 * test suite for the given platform found in the given
					 * document.
					 * @param document	the document
					 * @param platform	the platform
					 * @param testSuite	the testSuite
					 * @return		the entity
					 *------------------------------------------------------*/
	protected	MiiModelEntity	getEntityFromDocumentRepresentingTestSuite(
							MiiModelDocument document, 
							String platform, 
							MiiTestSuite testSuite)
		{
		String testSuiteName = testSuite.getName();
		MiModelEntityList list = document.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String entityPlatform = entity.getPropertyValue(Mi_PLATFORM_NAME);
			String entityTestSuite = entity.getPropertyValue(Mi_TEST_SUITE_NAME);
			if (((entityPlatform != null) && (entityPlatform.equals(platform)))
				&& ((entityTestSuite != null) && (entityTestSuite.equals(testSuiteName))))
				{
				return(entity);
				}
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Reads the information from the given document and populates
					 * the report cards (tables) from it.
					 * @param document	the document
					 *------------------------------------------------------*/
	protected	void		populateTableFromDocument(MiiModelDocument document)
		{
		MiModelEntityList list = document.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = list.elementAt(i);
			String platform = entity.getPropertyValue(Mi_PLATFORM_NAME);
			String testSuiteName = entity.getPropertyValue(Mi_TEST_SUITE_NAME);

			if ((platform != null) && (testSuiteName != null))
				{
				MiiTestSuite testSuite = getTestSuite(testSuiteName);
				if (testSuite != null)
					{
					if (!platforms.contains(platform))
						appendPlatform(platform);

					MiTable reportCard = getReportCard(testSuite);
					int numTestResults = testSuite.getNumberOfTestResults();
					int platFormRow = platforms.indexOf(platform);
					Strings names = entity.getPropertyNames();
					for (int j = 0; j < names.size(); ++j)
						{
						String key = names.elementAt(j);
						String value = entity.getPropertyValue(key);
						value = Utility.toShortString(Utility.toDouble(value));
						for (int k = 0; k < numTestResults; ++k)
							{
							String testResultName = testSuite.getNameOfTestResult(k);
							testResultName = Utility.replaceAll(testResultName, " ", "");
							if (testResultName.equals(key))
								{
								reportCard.getCell(platFormRow, k)
									.setValue(value);
								}
							}
						}
					}
				}
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
		if (runningTestSuite == null)
			{
			if (runningTestSuiteQueue.size() == 0)
				return;
			runningTestSuite = (MiiTestSuite )runningTestSuiteQueue.pop();
			runningReportCard = getReportCard(runningTestSuite);
			runStartTime = System.currentTimeMillis();
			runningTime = 0;
			runningTestInitialized = false;
			if (runningTestSuite.requiresDrawingEditor())
				{
				drawingTestEditorDialog.setVisible(true);
				drawingTestEditor.invalidateArea();
				}
			updateStatusBar(null);
			updateReportCard();
			}
	
		if (!runningTestInitialized)
			{
			runningTestSuite.initialize(drawingTestEditor);
			runningTestInitialized = true;
			}

		MiTestSuiteStatus testSuiteStatus = new MiTestSuiteStatus();

		double startTime = System.currentTimeMillis();
		boolean done = runningTestSuite.pollForCompletion(testSuiteStatus);
		
		if (testSuiteStatus.invalidateDisplay)
			drawingTestEditor.invalidateArea();

		updateStatusBar(testSuiteStatus);
		updateReportCard();

		if (done)
			{
			runningTestSuite.cleanUp();
			if (runningTestSuite.requiresDrawingEditor())
				drawingTestEditorDialog.setVisible(false);
			runningTestSuite = null;
			setHasChanged(true);
			}
		runningTime += System.currentTimeMillis() - startTime;
		}
					/**------------------------------------------------------
	 				 * Clean up after animation.
					 * @implements		MiiAnimatable#end
					 *------------------------------------------------------*/
	public		void		end()
		{
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This interface describes the methods that all test suites must implement.
 *
 *----------------------------------------------------------------------------------------------*/
interface MiiTestSuite
	{
			/**------------------------------------------------------
	 		 * Gets the name of the test suite.
			 * @return		the name
			 *------------------------------------------------------*/
	String		getName();


			/**------------------------------------------------------
	 		 * Gets the number of tests to run.
			 * @return		the number of tests
			 *------------------------------------------------------*/
	int		getNumberOfTests();


			/**------------------------------------------------------
	 		 * Gets the name of the test to run with the given index.
			 * @return		the name of the test
			 *------------------------------------------------------*/
	String		getNameOfTest(int index);


			/**------------------------------------------------------
	 		 * Gets the number of test results after all tests are
			 * run.
			 * @return		the number of test results
			 *------------------------------------------------------*/
	int		getNumberOfTestResults();


			/**------------------------------------------------------
	 		 * Gets the name of the test result with the given index.
			 * @return		the name of the result
			 *------------------------------------------------------*/
	String		getNameOfTestResult(int index);


			/**------------------------------------------------------
	 		 * Gets a description of the test result with the given index.
			 * @return		the description of the result
			 *------------------------------------------------------*/
	String		getDescriptionsOfTestResult(int index);


			/**------------------------------------------------------
	 		 * Gets the test result with the given index.
			 * @return		the result
			 *------------------------------------------------------*/
	double 		getTestResult(int index);


			/**------------------------------------------------------
	 		 * Gets whether the result returned from #getTestResult
			 * is better the larger it is.
			 * @return		true if the larger the result the better
			 *------------------------------------------------------*/
	boolean		isMoreBetterTestResult(int index);

			/**------------------------------------------------------
	 		 * Initialize the test suite.
			 * @param editor	to be used for output for drawing tests
			 *------------------------------------------------------*/
	void		initialize(MiEditor editor);


			/**------------------------------------------------------
	 		 * Gets whether the test suite requires a drawing editor.
			 * @return		true if this is a drawing test
			 *------------------------------------------------------*/
	boolean		requiresDrawingEditor();

			/**------------------------------------------------------
	 		 * Runs the test with the given index.
			 * @param numberOfTest	the index of the test
			 * @return		the index of the next test to run
			 *			(which might be the same one)
			 *------------------------------------------------------*/
	int		executeTest(int index);


			/**------------------------------------------------------
	 		 * Gets whether the test is done and other information
			 * about the test status.
			 * @param status	information about the completion
			 *			status of the test suite
			 * @return		true if done
			 *------------------------------------------------------*/
	boolean		pollForCompletion(MiTestSuiteStatus status);


			/**------------------------------------------------------
	 		 * Cleanup after the test suite is done.
			 *------------------------------------------------------*/
	void		cleanUp();
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class describes the state of completion of a test suite.
 *
 *----------------------------------------------------------------------------------------------*/
class MiTestSuiteStatus
	{
			/**------------------------------------------------------
	 		 * Drawing tests using Mica set this to cause the drawing
			 * output to be drawn.
			 *------------------------------------------------------*/
	boolean		invalidateDisplay;

			/**------------------------------------------------------
	 		 * How much of the test sutie has been completed (0.0 - 100.0).
			 *------------------------------------------------------*/
	double		percentCompleted;
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class provides a number of useful methods to test suites
 * that test drawing.
 *
 *----------------------------------------------------------------------------------------------*/
abstract class MiDrawTestSuite implements MiiTestSuite, MiiPartRenderer
	{
	protected	MiEditor	editor;
	protected	MiRenderer	renderer;
	protected	boolean		isRunning;
	protected	boolean		initialized;
	protected	int		runningTest;


					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initialize
					 *------------------------------------------------------*/
	public		void		initialize(MiEditor editor)
		{
		this.editor = editor;
		editor.setAfterRenderer(this);
		isRunning = true;
		runningTest = 0;
		editor.invalidateArea();
		}
					/**------------------------------------------------------
			 		 * Gets whether the test suite requires a drawing editor.
					 * @return		true if this is a drawing test
					 * @implements		MiiTestSuite#requiresDrawingEditor
					 *------------------------------------------------------*/
	public		boolean		requiresDrawingEditor()
		{
		return(true);
		}
					/**------------------------------------------------------
			 		 * Gets whether the test is done and other information
					 * about the test status.
					 * @param status	information about the completion
					 *			status of the test suite
					 * @return		true if done
					 * @implements		MiiTestSuite#pollForCompletion
					 *------------------------------------------------------*/
	public		boolean		pollForCompletion(MiTestSuiteStatus status)
		{
		if (!isRunning)
			{
			status.percentCompleted = 100.0;
			}
		else
			{
			status.percentCompleted = 100 * runningTest/getNumberOfTests();
			status.invalidateDisplay = true;
			}
		return(!isRunning);
		}
					/**------------------------------------------------------
	 				 * Runs the test with the given index.
					 * @param numberOfTest	the index of the test
					 * @return		the index of the next test to run
					 *			(which might be the same one)
					 * @implements		MiiTestSuite#executeTest
					 *------------------------------------------------------*/
	public		int		executeTest(int index)
		{
		return(100);
		}
					/**------------------------------------------------------
			 		 * Renders the given object using the given renderer.
					 * @return		true if caller should render object
					 * @implements		MiiPartRenderer#render
					 *------------------------------------------------------*/
	public		boolean		render(MiPart obj, MiRenderer renderer)
		{
		if (!isRunning)
			return(false);
	
		if (!initialized)
			{
			this.renderer = renderer;

			initializeTestRun(editor, renderer);
			initialized = true;
			}
		if (runningTest < getNumberOfTests())
			{
			runningTest = executeTest(runningTest, editor, renderer);
			}
		else
			{
			cleanUp();
			isRunning = false;
			initialized = false;
			}
		return(false);
		}
					/**------------------------------------------------------
			 		 * Gets the bounds of the given object.
					 * @param obj		the object which affects the bounds
					 * @param b		the given and (returned) bounds
					 * @return		true if bounds were modified
					 * @implements		MiiPartRenderer#getBounds
					 *------------------------------------------------------*/
	public		boolean		getBounds(MiPart obj, MiBounds b)
		{
		return(false);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite using the renderer and output
					 * editors to prepare for the drawing tests.
					 * @param editor	the editor to draw into
					 * @param renderer	for drawing
					 *------------------------------------------------------*/
	public abstract	void		initializeTestRun(MiEditor editor, MiRenderer renderer);

					/**------------------------------------------------------
			 		 * Execute the given test with the given editor and renderer.
					 * @param index		the test
					 * @param editor	the editor to draw into
					 * @param renderer	for drawing
					 *------------------------------------------------------*/
	public abstract	int		executeTest(int index, MiEditor editor, MiRenderer renderer);

					/**------------------------------------------------------
			 		 * Removes the largest values from the array and then
					 * returns the sum of the values.
					 * @param scores	the array of values
					 * @param numToThrowOut	the number to remove
					 * @return 		the sum of the result values
					 *------------------------------------------------------*/
	protected	double		throwOutHighestScoresAndTotalUp(double[] scores, int numToThrowOut)
		{
		int[] rejects = new int[numToThrowOut];
		double maxScore = 0;
		double result = 0;
		for (int i = 0; i < scores.length; ++i)
			{
			if (scores[i] > maxScore)
				{
				for (int j = 0; j < numToThrowOut - 1; ++j)
					{
					rejects[j] = rejects[j + 1];
					}
				rejects[numToThrowOut - 1] = i;
				maxScore = scores[i];
				}
			result += scores[i];
			}
		for (int i = 0; i < rejects.length; ++i)
			{
			result -= scores[rejects[i]];
			}
		return(result);
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number of AWT draw
 * operations.
 *
 *----------------------------------------------------------------------------------------------*/
class MiAWTGraphicsTestSuite extends MiDrawTestSuite
	{
	private		int[]		randomIntXCoordTable;
	private		int[]		randomIntYCoordTable;
	private		int[]		randomIntWidthTable;
	private		int[]		randomIntHeightTable;
	private static	Color[]		colorTable		= {Color.red, 
								Color.green, 
								Color.blue, 
								Color.yellow, 
								Color.magenta, 
								Color.cyan, 
								Color.orange, 
								Color.pink};

	private static final int	LINE			= 0;
	private static final int	RECT			= 1;
	private static final int	FILLRECT		= 2;
	private static final int	OVAL			= 3;
	private static final int	FILLOVAL		= 4;
	private static final int	TEXTSTRING		= 5;

	private 	String		AWT_TEST_SUITE_NAME	= "AWT Draw";

	private 	String[]	namesOfTests		= 
		{
		"Line", 
		"Rect", 
		"Fill Rect", 
		"Oval", 
		"Filled Oval" ,
		"Text" 
		};

	private 	String[]	namesOfResults		= 
		{
		"Average Draws/sec", 
		"Line", 
		"Rect", 
		"Fill Rect", 
		"Oval", 
		"Filled Oval", 
		"Text", 
		"Creates/sec", 
		"Bytes/shape"
		};


	private 	String[]	descriptionsOfResults		= 
		{
		"Average draws per second", 
		"Lines drawn per second", 
		"Rectangles drawn per second", 
		"Filled Rectangles drawn per second", 
		"Ovals drawn per second", 
		"Filled Ovals drawn per second", 
		"Text Strings drawn per second", 
		"Objects created per second", 
		"Bytes required per drawn shape" 
		};
	private		double[]	testResults 		= new double[namesOfResults.length];

	private static final int	AVERAGE_DRAW_SPEED_INDEX= 0;
	private static final int	CREATETIME_RESULT_INDEX	= 7;
	private static final int	BYTESIZE_RESULT_INDEX	= 8;
	private static final int	PRIM_RESULTS_OFFSET	= 1;

	private static final int	NUM_SHAPE_TYPES_TO_TEST	= 6;
	private static final int	NUM_DRAWS_PER_TEST	= 1000;
	private static final int	NUM_TESTS_PER_PRIM	= 10;

	private		int 		xmin;
	private		int		ymin;
	private		int		width;
	private		int		height;


					/**------------------------------------------------------
	 				 * Constructs a new MiAWTGraphicsTestSuite.
					 *------------------------------------------------------*/
	public				MiAWTGraphicsTestSuite()
		{
		}

					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(AWT_TEST_SUITE_NAME);
		}
					/**------------------------------------------------------
			 		 * Gets the number of tests to run.
					 * @return		the number of tests
					 * @implements		MiiTestSuite#getNumberOfTests
					 *------------------------------------------------------*/
	public		int		getNumberOfTests()
		{
		return(namesOfTests.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test to run with the given index.
					 * @return		the name of the test
					 * @implements		MiiTestSuite#getNameOfTest
					 *------------------------------------------------------*/
	public		String		getNameOfTest(int index)
		{
		return(namesOfTests[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the number of test results after all tests are
					 * run.
					 * @return		the number of test results
					 * @implements		MiiTestSuite#getNumberOfTestResults
					 *------------------------------------------------------*/
	public		int		getNumberOfTestResults()
		{
		return(namesOfResults.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test result with the given index.
					 * @return		the name of the result
					 * @implements		MiiTestSuite#getNameOfTestResult
					 *------------------------------------------------------*/
	public		String		getNameOfTestResult(int index)
		{
		return(namesOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets a description of the test result with the given index.
					 * @return		the description of the result
					 * @implements		MiiTestSuite#getDescriptionsOfTestResult
					 *------------------------------------------------------*/
	public		String		getDescriptionsOfTestResult(int index)
		{
		return(descriptionsOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the test result with the given index.
					 * @return		the result
					 * @implements		MiiTestSuite#getTestResult
					 *------------------------------------------------------*/
	public		double 		getTestResult(int index)
		{
		return(testResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets whether the result returned from #getTestResult
					 * is better the larger it is.
					 * @return		true if the larger the result the better
					 * @implements		MiiTestSuite#isMoreBetterTestResult
					 *------------------------------------------------------*/
	public		boolean		isMoreBetterTestResult(int index)
		{
		if (index == BYTESIZE_RESULT_INDEX)
			return(false);
		return(true);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initializeTestRun
					 *------------------------------------------------------*/
	public		void		initializeTestRun(MiEditor editor, MiRenderer renderer)
		{
		calcSizeOfTestArea(editor, renderer);

		for (int i = 0; i < testResults.length; ++i)
			testResults[i] = 0.0;

		randomIntXCoordTable = new int[NUM_DRAWS_PER_TEST];
		randomIntYCoordTable = new int[NUM_DRAWS_PER_TEST];
		randomIntWidthTable = new int[NUM_DRAWS_PER_TEST];
		randomIntHeightTable = new int[NUM_DRAWS_PER_TEST];
		for (int i = 0; i < NUM_DRAWS_PER_TEST; ++i)
			{
			randomIntXCoordTable[i] = (int )(Math.random() * width) + xmin;
			randomIntYCoordTable[i] = (int )(Math.random() * height) + ymin;
			randomIntWidthTable[i] = (int )(Math.random() 
				* Math.abs(xmin + width - randomIntXCoordTable[i]));
			randomIntHeightTable[i] = (int )(Math.random() 
				* Math.abs(ymin + height - randomIntYCoordTable[i]));
			}
		}
	public		int		executeTest(int index, MiEditor editor, MiRenderer renderer)
		{
		double[] subTestResults = new double[NUM_TESTS_PER_PRIM];

		renderer.setClipBounds(editor.getDeviceBounds());
		Graphics g = renderer.getWindowSystemRenderer();
		for (int i = 0; i < NUM_TESTS_PER_PRIM; ++i)
			{
			double startTime = System.currentTimeMillis();
			testAWTPrimDrawSpeed(g, index);
			double finishTime = System.currentTimeMillis();
			subTestResults[i] = finishTime - startTime;
			}
		testResults[index + PRIM_RESULTS_OFFSET] = NUM_DRAWS_PER_TEST 
			* (NUM_TESTS_PER_PRIM - 2)*1000/throwOutHighestScoresAndTotalUp(subTestResults, 2);

		updateAverageTestResult();
		return(++index);
		}
					/**------------------------------------------------------
			 		 * Cleanup after the test suite is done.
					 * @implements		MiiTestSuite#cleanUp
					 *------------------------------------------------------*/
	public		void		cleanUp()
		{
		randomIntXCoordTable = null;
		randomIntYCoordTable = null;
		randomIntWidthTable = null;
		randomIntHeightTable = null;
		}
	protected	void		updateAverageTestResult()
		{
		double sum = 0;
		int numTestsCompleted = 0;
		for (int i = 0; i < NUM_SHAPE_TYPES_TO_TEST; ++i)
			{
			if (testResults[PRIM_RESULTS_OFFSET + i] > 0.0)
				{
				++numTestsCompleted;
				sum += testResults[PRIM_RESULTS_OFFSET + i];
				}
			}
		if (sum != 0)
			{
			sum /= numTestsCompleted;
			}
		testResults[AVERAGE_DRAW_SPEED_INDEX] = sum;
		}
	protected	void		testAWTPrimDrawSpeed(Graphics g, int primType)
		{
		for (int i = 0; i < NUM_DRAWS_PER_TEST; ++i)
			{
			g.setColor(colorTable[i % colorTable.length]);
			int x = randomIntXCoordTable[i];
			int y = randomIntYCoordTable[i];
			int width = randomIntWidthTable[i];
			int height = randomIntHeightTable[i];
			switch (primType)
				{
				case LINE:
					if (i < NUM_DRAWS_PER_TEST - 1)
						{
						g.drawLine(x, y, 
							randomIntXCoordTable[i + 1], 
							randomIntYCoordTable[i + 1]);
						}
					break;
				case RECT:
					g.drawRect(x, y, width, height);
					break;
				case FILLRECT:
					g.fillRect(x, y, width, height);
					break;
				case OVAL:
					g.drawOval(x, y, width, height);
					break;
				case FILLOVAL:
					g.fillOval(x, y, width, height);
					break;
				case TEXTSTRING:
					g.drawString("Sample", x, y);
					break;
				default:
					break;
				}
			}
		}
	protected	void		calcSizeOfTestArea(MiEditor editor, MiRenderer renderer)
		{
		MiBounds deviceBounds = new MiBounds();
		renderer.getTransform().wtod(editor.getDeviceBounds(), deviceBounds);
		//MiCoord tmp = renderer.getYmax() - deviceBounds.getYmin();
		//deviceBounds.setYmin(renderer.getYmax() - deviceBounds.getYmax());
		//deviceBounds.setYmax(tmp);

		xmin = (int )deviceBounds.getXmin();
		ymin = (int )(renderer.getYmax() - deviceBounds.getYmin() - deviceBounds.getHeight());
		width = (int )deviceBounds.getWidth();
		height = (int )deviceBounds.getHeight();
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number of Mica draw
 * operations.
 *
 *----------------------------------------------------------------------------------------------*/
class MiMICAGraphicsTestSuite extends MiDrawTestSuite
	{
	protected	MiEditor	editor;

	protected	double[]	randomDoubleXCoordTable;
	protected	double[]	randomDoubleYCoordTable;
	protected	double[]	randomDoubleWidthTable;
	protected	double[]	randomDoubleHeightTable;
	protected static Color[]	colorTable		= {
								MiColorManager.red, 
								MiColorManager.green, 
								MiColorManager.blue, 
								MiColorManager.yellow, 
								MiColorManager.magenta, 
								MiColorManager.cyan, 
								MiColorManager.orange, 
								MiColorManager.pink};

	protected static final int	LINE			= 0;
	protected static final int	RECT			= 1;
	protected static final int	FILLRECT		= 2;
	protected static final int	OVAL			= 3;
	protected static final int	FILLOVAL		= 4;
	protected static final int	TEXTSTRING		= 5;

	private 	String		MICA_TEST_SUITE_NAME	= "MICA Draw";

	private 	String[]	namesOfTests		= 
		{
		"Line", 
		"Rect", 
		"Fill Rect", 
		"Oval", 
		"Filled Oval",
		"Text" 
		};

	private 	String[]	namesOfResults		= 
		{
		"Average Draws/sec", 
		"Line", 
		"Rect", 
		"Fill Rect", 
		"Oval", 
		"Filled Oval", 
		"Text", 
		"Creates/sec", 
		"Bytes/shape"
		};

// Text!!! Images!!!
	private 	String[]	descriptionsOfResults		= 
		{
		"Average draws per second", 
		"Lines drawn per second", 
		"Rectangles drawn per second", 
		"Filled Rectangles drawn per second", 
		"Ovals drawn per second", 
		"Filled Ovals drawn per second",
		"Text Strings drawn per second",
		"Objects created per second", 
		"Bytes required per drawn shape"
		};
	private		int 		curTestPerPrim 		= 0;
	private		double[] 	subTestResults 		= new double[NUM_TESTS_PER_PRIM];
	protected	double[]	createTimeTestResults 	= new double[namesOfTests.length];
	protected	double[]	testResults 		= new double[namesOfResults.length];
	protected static final int	AVERAGE_DRAW_SPEED_INDEX= 0;
	protected static final int	CREATETIME_RESULT_INDEX	= 7;
	protected static final int	BYTESIZE_RESULT_INDEX	= 8;
	protected static final int	PRIM_RESULTS_OFFSET	= 1;

	protected static final int	NUM_SHAPE_TYPES_TO_TEST	= 6;
	protected static final int	NUM_SHAPES_PER_TEST	= 1000;
	protected static final int	NUM_TESTS_PER_PRIM	= 10;



					/**------------------------------------------------------
	 				 * Constructs a new MiMICAGraphicsTestSuite.
					 *------------------------------------------------------*/
	public				MiMICAGraphicsTestSuite()
		{
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(MICA_TEST_SUITE_NAME);
		}
					/**------------------------------------------------------
			 		 * Gets the number of tests to run.
					 * @return		the number of tests
					 * @implements		MiiTestSuite#getNumberOfTests
					 *------------------------------------------------------*/
	public		int		getNumberOfTests()
		{
		return(namesOfTests.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test to run with the given index.
					 * @return		the name of the test
					 * @implements		MiiTestSuite#getNameOfTest
					 *------------------------------------------------------*/
	public		String		getNameOfTest(int index)
		{
		return(namesOfTests[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the number of test results after all tests are
					 * run.
					 * @return		the number of test results
					 * @implements		MiiTestSuite#getNumberOfTestResults
					 *------------------------------------------------------*/
	public		int		getNumberOfTestResults()
		{
		return(namesOfResults.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test result with the given index.
					 * @return		the name of the result
					 * @implements		MiiTestSuite#getNameOfTestResult
					 *------------------------------------------------------*/
	public		String		getNameOfTestResult(int index)
		{
		return(namesOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets a description of the test result with the given index.
					 * @return		the description of the result
					 * @implements		MiiTestSuite#getDescriptionsOfTestResult
					 *------------------------------------------------------*/
	public		String		getDescriptionsOfTestResult(int index)
		{
		return(descriptionsOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the test result with the given index.
					 * @return		the result
					 * @implements		MiiTestSuite#getTestResult
					 *------------------------------------------------------*/
	public		double 		getTestResult(int index)
		{
		return(testResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets whether the result returned from #getTestResult
					 * is better the larger it is.
					 * @return		true if the larger the result the better
					 * @implements		MiiTestSuite#isMoreBetterTestResult
					 *------------------------------------------------------*/
	public		boolean		isMoreBetterTestResult(int index)
		{
		if (index == BYTESIZE_RESULT_INDEX)
			return(false);
		return(true);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initializeTestRun
					 *------------------------------------------------------*/
	public		void		initializeTestRun(MiEditor editor, MiRenderer renderer)
		{
		this.editor = editor;

		curTestPerPrim = 0;

		MiCoord xmin = editor.getWorldBounds().getXmin();
		MiCoord ymin = editor.getWorldBounds().getYmin();
		MiDistance width = editor.getWorldBounds().getWidth();
		MiDistance height = editor.getWorldBounds().getHeight();

		for (int i = 0; i < testResults.length; ++i)
			testResults[i] = 0.0;

		randomDoubleXCoordTable = new double[NUM_SHAPES_PER_TEST];
		randomDoubleYCoordTable = new double[NUM_SHAPES_PER_TEST];
		randomDoubleWidthTable = new double[NUM_SHAPES_PER_TEST];
		randomDoubleHeightTable = new double[NUM_SHAPES_PER_TEST];
		for (int i = 0; i < NUM_SHAPES_PER_TEST; ++i)
			{
			randomDoubleXCoordTable[i] = (Math.random() * width) + xmin;
			randomDoubleYCoordTable[i] = (Math.random() * height) + ymin;
			randomDoubleWidthTable[i] = (Math.random()
				* Math.abs(width - randomDoubleXCoordTable[i] + xmin));
			randomDoubleHeightTable[i] = (Math.random() 
				* Math.abs(height - randomDoubleYCoordTable[i] + ymin));
			}
		}
	public		int		executeTest(int index, MiEditor editor, MiRenderer renderer)
		{
		subTestResults = new double[NUM_TESTS_PER_PRIM];
		makeMICAShapes(editor, index);

		renderer.setClipBounds(editor.getDeviceBounds());
		for (int i = 0; i < NUM_TESTS_PER_PRIM; ++i)
			{
			double startTime = System.currentTimeMillis();
			editor.render(renderer);
			double finishTime = System.currentTimeMillis();
			subTestResults[i] = finishTime - startTime;
			}

		testResults[index + PRIM_RESULTS_OFFSET] = NUM_SHAPES_PER_TEST 
			* (NUM_TESTS_PER_PRIM - 2)*1000/throwOutHighestScoresAndTotalUp(subTestResults, 2);
		++index;
		updateAverageTestResult();
		editor.removeAllParts();
		return(index);
		}
					/**------------------------------------------------------
			 		 * Cleanup after the test suite is done.
					 * @implements		MiiTestSuite#cleanUp
					 *------------------------------------------------------*/
	public		void		cleanUp()
		{
		randomDoubleXCoordTable = null;
		randomDoubleYCoordTable = null;
		randomDoubleWidthTable = null;
		randomDoubleHeightTable = null;
		}
	protected	void		updateAverageTestResult()
		{
		double drawSpeed = 0;
		double createTime = 0;
		int numTestsCompleted = 0;
		for (int i = 0; i < NUM_SHAPE_TYPES_TO_TEST; ++i)
			{
			if (testResults[PRIM_RESULTS_OFFSET + i] > 0.0)
				{
				++numTestsCompleted;
				drawSpeed += testResults[PRIM_RESULTS_OFFSET + i];
				createTime += createTimeTestResults[i];
				}
			}
		if (drawSpeed != 0)
			{
			drawSpeed /= numTestsCompleted;
			createTime /= numTestsCompleted;
			}
		testResults[AVERAGE_DRAW_SPEED_INDEX] = drawSpeed;
		testResults[CREATETIME_RESULT_INDEX] = createTime;
		}
	protected	void		makeMICAShapes(MiEditor editor, int primType)
		{
		editor.removeAllParts();

		long[] freeMemory = new long[1];
		long[] freeMemoryLeft = new long[1];
		long[] totalMemory = new long[1];
		MiDebug.getMemoryStatistics(freeMemory, totalMemory);

		double startTime = System.currentTimeMillis();
		for (int i = 0; i < NUM_SHAPES_PER_TEST; ++i)
			{
			double x = randomDoubleXCoordTable[i];
			double y = randomDoubleYCoordTable[i];
			double width = randomDoubleWidthTable[i];
			double height = randomDoubleHeightTable[i];
			Color color = colorTable[i % colorTable.length];
			MiPart shape = null;
			switch (primType)
				{
				case LINE:
					if (i < randomDoubleXCoordTable.length - 1)
						{
						shape = new MiLine(x, y,
							randomDoubleXCoordTable[i + 1], 
							randomDoubleYCoordTable[i + 1]);
						}
					break;
				case RECT:
					shape = new MiRectangle(x, y, x + width, y + height);
					break;
				case FILLRECT:
					shape = new MiRectangle(x, y, x + width, y + height);
					shape.setBackgroundColor(color);
					break;
				case OVAL:
					shape = new MiEllipse(x, y, x + width, y + height);
					break;
				case FILLOVAL:
					shape = new MiEllipse(x, y, x + width, y + height);
					shape.setBackgroundColor(color);
					break;
				case TEXTSTRING:
					shape = new MiText(x, y, "Sample");
					break;
				default:
					break;
				}
			if (shape != null)
				{
				shape.setColor(colorTable[i % colorTable.length]);
				editor.appendPart(shape);
				}
			}
		double finishTime = System.currentTimeMillis();
		createTimeTestResults[primType] 
			= NUM_SHAPES_PER_TEST * 1000/(finishTime - startTime);

		MiDebug.getMemoryStatistics(freeMemoryLeft, totalMemory);
		testResults[BYTESIZE_RESULT_INDEX] 
			= ((double )(freeMemory[0] - freeMemoryLeft[0]))/NUM_SHAPES_PER_TEST;
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number of Mica draw
 * operations using the 'lite' graphics shapes.
 *
 *----------------------------------------------------------------------------------------------*/
class MiMICALiteGraphicsTestSuite extends MiMICAGraphicsTestSuite
	{
	private 	String		MICA_LIGHT_WEIGHT_TEST_SUITE_NAME	= "MICA Draw Lite";


					/**------------------------------------------------------
	 				 * Constructs a new MiMICALiteGraphicsTestSuite.
					 *------------------------------------------------------*/
	public				MiMICALiteGraphicsTestSuite()
		{
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(MICA_LIGHT_WEIGHT_TEST_SUITE_NAME);
		}
	protected	void		makeMICAShapes(MiEditor editor, int primType)
		{
		editor.removeAllParts();

		long[] freeMemory = new long[1];
		long[] freeMemoryLeft = new long[1];
		long[] totalMemory = new long[1];
		MiDebug.getMemoryStatistics(freeMemory, totalMemory);

		double startTime = System.currentTimeMillis();
		MiLiteShapesContainer[] containers = new MiLiteShapesContainer[colorTable.length];
		for (int i = 0; i < containers.length; ++i)
			{
			containers[i] = new MiLiteShapesContainer();
			containers[i].setColor(colorTable[i % colorTable.length]);
			if ((primType == FILLRECT) || (primType == FILLOVAL))
				containers[i].setBackgroundColor(colorTable[i % colorTable.length]);
			editor.appendPart(containers[i]);
			}

		for (int i = 0; i < NUM_SHAPES_PER_TEST; ++i)
			{
			double x = randomDoubleXCoordTable[i];
			double y = randomDoubleYCoordTable[i];
			double width = randomDoubleWidthTable[i];
			double height = randomDoubleHeightTable[i];
			MiLightweightShape shape = null;
			switch (primType)
				{
				case LINE:
					if (i < randomDoubleXCoordTable.length - 1)
						{
						shape = new MiLineLite(x, y,
							randomDoubleXCoordTable[i + 1], 
							randomDoubleYCoordTable[i + 1]);
						}
					break;
				case RECT:
					shape = new MiRectLite(x, y, x + width, y + height);
					break;
				case FILLRECT:
					shape = new MiRectLite(x, y, x + width, y + height);
					break;
				case OVAL:
					shape = new MiEllipseLite(x, y, x + width, y + height);
					break;
				case FILLOVAL:
					shape = new MiEllipseLite(x, y, x + width, y + height);
					break;
				case TEXTSTRING:
					shape = new MiTextLite(x, y, "Sample");
					break;
				default:
					break;
				}
			if (shape != null)
				{
				containers[i % colorTable.length].appendShape(shape);
				}
			}
		double finishTime = System.currentTimeMillis();
		createTimeTestResults[primType] 
			= NUM_SHAPES_PER_TEST * 1000/(finishTime - startTime);

		MiDebug.getMemoryStatistics(freeMemoryLeft, totalMemory);
		testResults[BYTESIZE_RESULT_INDEX] 
			= ((double )(freeMemory[0] - freeMemoryLeft[0]))/NUM_SHAPES_PER_TEST;
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number of Mica draw
 * operations using the 'very lite' graphics shapes.
 *
 *----------------------------------------------------------------------------------------------*/
class MiMICAVeryLiteGraphicsTestSuite extends MiMICAGraphicsTestSuite
	{
	private 	String		MICA_VERY_LITE_TEST_SUITE_NAME	= "MICA Draw VeryLite";
	private		MiPoint		tmpPoint1 			= new MiPoint();
	private		MiPoint		tmpPoint2 			= new MiPoint();
	private		MiBounds	tmpBounds 			= new MiBounds();


					/**------------------------------------------------------
	 				 * Constructs a new MiMICAVeryLiteGraphicsTestSuite.
					 *------------------------------------------------------*/
	public				MiMICAVeryLiteGraphicsTestSuite()
		{
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(MICA_VERY_LITE_TEST_SUITE_NAME);
		}
	protected	void		makeMICAShapes(MiEditor editor, int primType)
		{
		editor.removeAllParts();

		long[] freeMemory = new long[1];
		long[] freeMemoryLeft = new long[1];
		long[] totalMemory = new long[1];
		MiDebug.getMemoryStatistics(freeMemory, totalMemory);

		double startTime = System.currentTimeMillis();
		MiVeryLightweightShape shape = new MiVeryLightweightShape();
		editor.appendPart(shape);

		for (int i = 0; i < NUM_SHAPES_PER_TEST; ++i)
			{
			double x = randomDoubleXCoordTable[i];
			double y = randomDoubleYCoordTable[i];
			double width = randomDoubleWidthTable[i];
			double height = randomDoubleHeightTable[i];

			tmpPoint1.x = x;
			tmpPoint1.y = y;
			tmpBounds.setXmin(x);
			tmpBounds.setYmin(y);
			tmpBounds.setXmax(x + width);
			tmpBounds.setYmax(y + height);

			shape.appendColor(colorTable[i % colorTable.length]);
			if ((primType == FILLRECT) || (primType == FILLOVAL))
				shape.appendBackgroundColor(colorTable[i % colorTable.length]);

			switch (primType)
				{
				case LINE:
					if (i < randomDoubleXCoordTable.length - 1)
						{
						tmpPoint2.x = randomDoubleXCoordTable[i + 1];
						tmpPoint2.y = randomDoubleYCoordTable[i + 1];
						shape.appendLine(tmpPoint1, tmpPoint2);
						}
					break;
				case RECT:
					shape.appendRectangle(tmpBounds);
					break;
				case FILLRECT:
					shape.appendRectangle(tmpBounds);
					break;
				case OVAL:
					shape.appendEllipse(tmpBounds);
					break;
				case FILLOVAL:
					shape.appendEllipse(tmpBounds);
					break;
				default:
					break;
				}
			}
		double finishTime = System.currentTimeMillis();
		createTimeTestResults[primType] 
			= NUM_SHAPES_PER_TEST * 1000/(finishTime - startTime);

		MiDebug.getMemoryStatistics(freeMemoryLeft, totalMemory);
		testResults[BYTESIZE_RESULT_INDEX] 
			= ((double )(freeMemory[0] - freeMemoryLeft[0]))/NUM_SHAPES_PER_TEST;

		//shape.refreshBounds();
		shape.validateLayout();
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class provides a number of methods to support the
 * average test suite.
 *
 *----------------------------------------------------------------------------------------------*/
abstract class MiTestSuite implements MiiTestSuite, Runnable
	{
	private		Thread		thread;
	private		int		runningTest;

	public		void		initialize(MiEditor editor)
		{
		thread = new Thread(this);
		initialize();
		runningTest = 0;
		thread.start();
		}
	public		boolean		requiresDrawingEditor()
		{
		return(false);
		}
	public		void		run()
		{
		while (true)
			{
//System.out.println("runningTest = " + runningTest);
			if (runningTest < getNumberOfTests())
				{
				runningTest = executeTest(runningTest);
//System.out.println("return from runningTest = " + runningTest);
				try {Thread.sleep(100); } catch (Exception e) {}
				}
			else
				{
// System.out.println("DONE with test");
				thread = null;
				return;
				}
			}
		}
	public		void		end()
		{
		}
	public		boolean		pollForCompletion(MiTestSuiteStatus status)
		{
		status.percentCompleted = 100 * runningTest/getNumberOfTests();
//if (thread == null)
//System.out.println("POLL thread = NULL");
		return(thread == null);
		}
	public abstract void		initialize();
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the memory size of a number of
 * different Java constructs.
 *
 *----------------------------------------------------------------------------------------------*/
class MiEmptyClassTest
	{
			MiEmptyClassTest()
		{
		}
	}
class Mi10BooleanClassTest
	{
	boolean		flag1 = true;
	boolean		flag2;
	boolean		flag3 = true;
	boolean		flag4;
	boolean		flag5 = true;
	boolean		flag6;
	boolean		flag7 = true;
	boolean		flag8;
	boolean		flag9 = true;
	boolean		flag10;

			Mi10BooleanClassTest()
		{
		}
	}

class Mi10ObjectClassTest
	{
	Mi10ObjectClassTest		obj1 = null;
	String				obj2;
	Mi10ObjectClassTest		obj3 = null;
	Vector				obj4;
	boolean				obj5 = true;
	boolean				obj6 = true;
	boolean				obj7 = true;
	boolean				obj8 = true;
	boolean				obj9 = false;
	boolean				obj10 = true;

			Mi10ObjectClassTest()
		{
		}
	}

class MiMemorySizeTestSuite extends MiTestSuite
	{
	private 	String[]	namesOfTests		= 
		{"boolean", "char", "byte", "short", "int", "long", "float", "double", "Reference",
		"EmptyClass", "BooleanClass", "Object10class" };

	private 	String[]	namesOfResults		= 
		{"boolean", "char", "byte", "short", "int", "long", "float", "double", "Reference",
		"Empty class", "10 booleans class", "10 objects class", "Empty class create", "10 booleans class create", "10 objects class create" };

	private 	String[]	descriptionsOfResults		= 
		{
		"Bytes per boolean in an array", 
		"Bytes per char", 
		"Bytes per byte", 
		"Bytes per short", 
		"Bytes per int", 
		"Bytes per long", 
		"Bytes per float", 
		"Bytes per double", 
		"Bytes per reference",
		"Bytes per empty class", 
		"Bytes per a class with 10 booleans", 
		"Bytes per a class with 10 objects", 
		"Empty class creations per second", 
		"Creations of a class with 10 booleans per second",
		"Creations of a class with 10 objects per second" 
		};

	private		double[]	testResults 		= new double[namesOfResults.length];
	private		String		MEMORY_SIZE_TEST_SUITE_NAME	= "Memory Size";

	private		int		EMPTY_CLASS_CREATE_PER_SECOND_TIME_INDEX = 12;
	private		int		BOOLEAN10_CLASS_CREATE_PER_SECOND_TIME_INDEX = 13;
	private		int		OBJECT10_CLASS_CREATE_PER_SECOND_TIME_INDEX = 14;





					/**------------------------------------------------------
	 				 * Constructs a new MiMemorySizeTestSuite.
					 *------------------------------------------------------*/
	public				MiMemorySizeTestSuite()
		{
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(MEMORY_SIZE_TEST_SUITE_NAME);
		}
					/**------------------------------------------------------
			 		 * Gets the number of tests to run.
					 * @return		the number of tests
					 * @implements		MiiTestSuite#getNumberOfTests
					 *------------------------------------------------------*/
	public		int		getNumberOfTests()
		{
		return(namesOfTests.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test to run with the given index.
					 * @return		the name of the test
					 * @implements		MiiTestSuite#getNameOfTest
					 *------------------------------------------------------*/
	public		String		getNameOfTest(int index)
		{
		return(namesOfTests[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the number of test results after all tests are
					 * run.
					 * @return		the number of test results
					 * @implements		MiiTestSuite#getNumberOfTestResults
					 *------------------------------------------------------*/
	public		int		getNumberOfTestResults()
		{
		return(namesOfResults.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test result with the given index.
					 * @return		the name of the result
					 * @implements		MiiTestSuite#getNameOfTestResult
					 *------------------------------------------------------*/
	public		String		getNameOfTestResult(int index)
		{
		return(namesOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets a description of the test result with the given index.
					 * @return		the description of the result
					 * @implements		MiiTestSuite#getDescriptionsOfTestResult
					 *------------------------------------------------------*/
	public		String		getDescriptionsOfTestResult(int index)
		{
		return(descriptionsOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the test result with the given index.
					 * @return		the result
					 * @implements		MiiTestSuite#getTestResult
					 *------------------------------------------------------*/
	public		double 		getTestResult(int index)
		{
		return(testResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets whether the result returned from #getTestResult
					 * is better the larger it is.
					 * @return		true if the larger the result the better
					 * @implements		MiiTestSuite#isMoreBetterTestResult
					 *------------------------------------------------------*/
	public		boolean		isMoreBetterTestResult(int index)
		{
		return(false);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initializeTestRun
					 *------------------------------------------------------*/
	public		void		initialize()
		{
		for (int i = 0; i < testResults.length; ++i)
			testResults[i] = 0.0;
		}
					/**------------------------------------------------------
	 				 * Runs the test with the given index.
					 * @param numberOfTest	the index of the test
					 * @return		the index of the next test to run
					 *			(which might be the same one)
					 * @implements		MiiTestSuite#executeTest
					 *------------------------------------------------------*/
	public		int		executeTest(int index)
		{
		long[] freeMemory = new long[1];
		long[] freeMemoryLeft = new long[1];
		long[] totalMemory = new long[1];
		MiDebug.getMemoryStatistics(freeMemory, totalMemory);

		switch (index)
			{
			case 0:
				boolean[] booleans = new boolean[10000];
				break;
			case 1:
				char[] chars = new char[10000];
				break;
			case 2:
				byte[] bytes = new byte[10000];
				break;
			case 3:
				short[] shorts = new short[10000];
				break;
			case 4:
				int[] ints = new int[10000];
				break;
			case 5:
				long[] longs = new long[10000];
				break;
			case 6:
				float[] floats = new float[10000];
				break;
			case 7:
				double[] doubles = new double[10000];
				break;
			case 8:
				String[] strings = new String[10000];
				break;
			case 9:
				{
				MiEmptyClassTest[] emptyClasses = new MiEmptyClassTest[10000];
				MiDebug.getMemoryStatistics(freeMemory, totalMemory);
				double startTime = System.currentTimeMillis();
				for (int i = 0; i < 10000; ++i)
					emptyClasses[i] = new MiEmptyClassTest();
				double finishTime = System.currentTimeMillis();
				testResults[EMPTY_CLASS_CREATE_PER_SECOND_TIME_INDEX] 
					= 10000/(finishTime - startTime) * 1000;
				break;
				}
			case 10:
				{
				Mi10BooleanClassTest[] booleanClasses = new Mi10BooleanClassTest[10000];
				MiDebug.getMemoryStatistics(freeMemory, totalMemory);
				double startTime = System.currentTimeMillis();
				for (int i = 0; i < 10000; ++i)
					booleanClasses[i] = new Mi10BooleanClassTest();
				double finishTime = System.currentTimeMillis();
				testResults[BOOLEAN10_CLASS_CREATE_PER_SECOND_TIME_INDEX] 
					= 10000/(finishTime - startTime) * 1000;
				break;
				}
			case 11:
				{
				Mi10ObjectClassTest[] objectClasses = new Mi10ObjectClassTest[10000];
				MiDebug.getMemoryStatistics(freeMemory, totalMemory);
				double startTime = System.currentTimeMillis();
				for (int i = 0; i < 10000; ++i)
					objectClasses[i] = new Mi10ObjectClassTest();
				double finishTime = System.currentTimeMillis();
				testResults[OBJECT10_CLASS_CREATE_PER_SECOND_TIME_INDEX] 
					= 10000/(finishTime - startTime) * 1000;
				break;
				}
			default:
				break;
			}

		MiDebug.getMemoryStatistics(freeMemoryLeft, totalMemory);
		testResults[index] = ((double )(freeMemory[0] - freeMemoryLeft[0]))/10000;
		testResults[index] = (double)(Math.round(testResults[index]));
		return(index + 1);
		}
					/**------------------------------------------------------
			 		 * Cleanup after the test suite is done.
					 * @implements		MiiTestSuite#cleanUp
					 *------------------------------------------------------*/
	public		void		cleanUp()
		{
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number flow control
 * operations.
 *
 *----------------------------------------------------------------------------------------------*/
class MiFlowControlTestSuite extends MiTestSuite
	{
	private 	String[]	namesOfTests		= 
		{ "Empty loop", "Method call", "Static method call", "Sync method call", "Ten arg method calls", "Cast", "Instanceof" };

	private 	String[]	namesOfResults		= 
		{ "Empty loop", "Method call", "Static method call", "Sync method call", "Ten arg method calls", "Cast", "Instanceof" };

	private 	String[]	descriptionsOfResults		= 
		{
		"Number of empty loops per second",
		"Number of method calls per second",
		"Number of static method calls per second",
		"Number of synchronized method calls per second",
		"Number of method calls with 10 arguments per second",
		"Number of casts of one object to another",
		"Number of instanceof calls per second"
		};

	private		double		emptyLoopTime;
	private		double[]	testResults 		= new double[namesOfResults.length];
	private		String		FLOW_CONTROL_TEST_SUITE_NAME	= "Flow Control";
	private		int		NUMBER_OF_FLOWS		= 1000000;



					/**------------------------------------------------------
	 				 * Constructs a new MiFlowControlTestSuite.
					 *------------------------------------------------------*/
	public				MiFlowControlTestSuite()
		{
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(FLOW_CONTROL_TEST_SUITE_NAME);
		}
					/**------------------------------------------------------
			 		 * Gets the number of tests to run.
					 * @return		the number of tests
					 * @implements		MiiTestSuite#getNumberOfTests
					 *------------------------------------------------------*/
	public		int		getNumberOfTests()
		{
		return(namesOfTests.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test to run with the given index.
					 * @return		the name of the test
					 * @implements		MiiTestSuite#getNameOfTest
					 *------------------------------------------------------*/
	public		String		getNameOfTest(int index)
		{
		return(namesOfTests[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the number of test results after all tests are
					 * run.
					 * @return		the number of test results
					 * @implements		MiiTestSuite#getNumberOfTestResults
					 *------------------------------------------------------*/
	public		int		getNumberOfTestResults()
		{
		return(namesOfResults.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test result with the given index.
					 * @return		the name of the result
					 * @implements		MiiTestSuite#getNameOfTestResult
					 *------------------------------------------------------*/
	public		String		getNameOfTestResult(int index)
		{
		return(namesOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets a description of the test result with the given index.
					 * @return		the description of the result
					 * @implements		MiiTestSuite#getDescriptionsOfTestResult
					 *------------------------------------------------------*/
	public		String		getDescriptionsOfTestResult(int index)
		{
		return(descriptionsOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the test result with the given index.
					 * @return		the result
					 * @implements		MiiTestSuite#getTestResult
					 *------------------------------------------------------*/
	public		double 		getTestResult(int index)
		{
		return(testResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets whether the result returned from #getTestResult
					 * is better the larger it is.
					 * @return		true if the larger the result the better
					 * @implements		MiiTestSuite#isMoreBetterTestResult
					 *------------------------------------------------------*/
	public		boolean		isMoreBetterTestResult(int index)
		{
		return(true);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initializeTestRun
					 *------------------------------------------------------*/
	public		void		initialize()
		{
		for (int i = 0; i < testResults.length; ++i)
			testResults[i] = 0.0;
		emptyLoopTime = 0.0;
		}
					/**------------------------------------------------------
	 				 * Runs the test with the given index.
					 * @param numberOfTest	the index of the test
					 * @return		the index of the next test to run
					 *			(which might be the same one)
					 * @implements		MiiTestSuite#executeTest
					 *------------------------------------------------------*/
	public		int		executeTest(int index)
		{
		double startTime = System.currentTimeMillis();
		Object object = this;
		switch (index)
			{
			case 0:
				for (int i = 0; i < NUMBER_OF_FLOWS; ++i)
					;
				break;
			case 1:
				for (int i = 0; i < NUMBER_OF_FLOWS; ++i)
					emptyCommand();
				break;
			case 2:
				for (int i = 0; i < NUMBER_OF_FLOWS; ++i)
					MiFlowControlTestSuite.staticCommand();
				break;
			case 3:
				for (int i = 0; i < NUMBER_OF_FLOWS; ++i)
					syncCommand();
				break;
			case 4:
				for (int i = 0; i < NUMBER_OF_FLOWS; ++i)
					emptyCommandStackTest(1, 2, 3, 4, 5, this, this, this, this, this);
				break;
			case 5:
				for (int i = 0; i < NUMBER_OF_FLOWS; ++i)
					{
					MiFlowControlTestSuite test = null;
					}
				break;
			case 6:
				for (int i = 0; i < NUMBER_OF_FLOWS; ++i)
					{
					MiFlowControlTestSuite test = (MiFlowControlTestSuite )object;
					}
				break;
			case 7:
				for (int i = 0; i < NUMBER_OF_FLOWS; ++i)
					{
					boolean flag = object instanceof MiFlowControlTestSuite;
					}
				break;
			default:
				break;
			}
		double finishTime = System.currentTimeMillis();
		testResults[index] = 1000/(finishTime - startTime - emptyLoopTime) * NUMBER_OF_FLOWS;
		if ((index == 0) || (index == 5))
			emptyLoopTime = finishTime - startTime;
		return(index + 1);
		}
					/**------------------------------------------------------
			 		 * Cleanup after the test suite is done.
					 * @implements		MiiTestSuite#cleanUp
					 *------------------------------------------------------*/
	public		void		cleanUp()
		{
		}
	public		void		emptyCommand()
		{
		}
	public static	void		staticCommand()
		{
		}
synchronized	public void		syncCommand()
		{
		}
	public		void		emptyCommandStackTest(int a, int b, int c, int d, int e, 
						Object f, Object g, Object h, Object i, Object j)
		{
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number of Java arithmetic
 * operations.
 *
 *----------------------------------------------------------------------------------------------*/
class MiArithmeticTestSuite extends MiTestSuite
	{
	private 	String[]	namesOfTests		= 
		{ "Simple assignment loop", "integer+", "integer*", "integer/", "double+", "double*", "double/", "Cast (int )double" };

	private 	String[]	namesOfResults		= 
		{ "Simple assignment loop", "integer+", "integer*", "integer/", "double+", "double*", "double/", "Cast (int )double" };

	private 	String[]	descriptionsOfResults		= 
		{
		"Number of simple assignment loops per second",
		"Number of integer additions per second",
		"Number of integer multiplies calls per second",
		"Number of integer divides per second",
		"Number of double additions per second",
		"Number of double multiplies calls per second",
		"Number of double divides per second",
		"Number of casts of double to an integer per second"
		};

	private		double		emptyLoopTime;
	private		double[]	testResults 		= new double[namesOfResults.length];
	private		String		ARITHMETIC_TEST_SUITE_NAME	= "Arithmetic";
	private		int		NUMBER_OF_CALCS		= 1000000;



					/**------------------------------------------------------
	 				 * Constructs a new MiArithmeticTestSuite.
					 *------------------------------------------------------*/
	public				MiArithmeticTestSuite()
		{
		}

					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(ARITHMETIC_TEST_SUITE_NAME);
		}
					/**------------------------------------------------------
			 		 * Gets the number of tests to run.
					 * @return		the number of tests
					 * @implements		MiiTestSuite#getNumberOfTests
					 *------------------------------------------------------*/
	public		int		getNumberOfTests()
		{
		return(namesOfTests.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test to run with the given index.
					 * @return		the name of the test
					 * @implements		MiiTestSuite#getNameOfTest
					 *------------------------------------------------------*/
	public		String		getNameOfTest(int index)
		{
		return(namesOfTests[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the number of test results after all tests are
					 * run.
					 * @return		the number of test results
					 * @implements		MiiTestSuite#getNumberOfTestResults
					 *------------------------------------------------------*/
	public		int		getNumberOfTestResults()
		{
		return(namesOfResults.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test result with the given index.
					 * @return		the name of the result
					 * @implements		MiiTestSuite#getNameOfTestResult
					 *------------------------------------------------------*/
	public		String		getNameOfTestResult(int index)
		{
		return(namesOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets a description of the test result with the given index.
					 * @return		the description of the result
					 * @implements		MiiTestSuite#getDescriptionsOfTestResult
					 *------------------------------------------------------*/
	public		String		getDescriptionsOfTestResult(int index)
		{
		return(descriptionsOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the test result with the given index.
					 * @return		the result
					 * @implements		MiiTestSuite#getTestResult
					 *------------------------------------------------------*/
	public		double 		getTestResult(int index)
		{
		return(testResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets whether the result returned from #getTestResult
					 * is better the larger it is.
					 * @return		true if the larger the result the better
					 * @implements		MiiTestSuite#isMoreBetterTestResult
					 *------------------------------------------------------*/
	public		boolean		isMoreBetterTestResult(int index)
		{
		return(true);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initializeTestRun
					 *------------------------------------------------------*/
	public		void		initialize()
		{
		for (int i = 0; i < testResults.length; ++i)
			testResults[i] = 0.0;
		emptyLoopTime = 0.0;
		}
					/**------------------------------------------------------
	 				 * Runs the test with the given index.
					 * @param numberOfTest	the index of the test
					 * @return		the index of the next test to run
					 *			(which might be the same one)
					 * @implements		MiiTestSuite#executeTest
					 *------------------------------------------------------*/
	public		int		executeTest(int index)
		{
		int intValue = 123;
		double doubleValue = 123.45;
		double startTime = System.currentTimeMillis();
		if (index == 0)
			{
			for (int i = 0; i < NUMBER_OF_CALCS; ++i)
				;
			}
		emptyLoopTime = System.currentTimeMillis() - startTime;
		switch (index)
			{
			case 0:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					intValue = 10;
				break;
			case 1:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					intValue += 11;
				break;
			case 2:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					intValue *= 11;
				break;
			case 3:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					intValue /= 11;
				break;
			case 4:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					doubleValue += 11.1;
				break;
			case 5:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					doubleValue *= 11.1;
				break;
			case 6:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					doubleValue /= 11.1;
				break;
			case 7:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					intValue = (int )doubleValue;
				break;
			default:
				break;
			}
		double finishTime = System.currentTimeMillis();
		testResults[index] = 1000/(finishTime - startTime - emptyLoopTime) * NUMBER_OF_CALCS;
		return(index + 1);
		}
					/**------------------------------------------------------
			 		 * Cleanup after the test suite is done.
					 * @implements		MiiTestSuite#cleanUp
					 *------------------------------------------------------*/
	public		void		cleanUp()
		{
		}
	}


/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number of Java Math
 * class operations.
 *
 *----------------------------------------------------------------------------------------------*/
class MiMathTestSuite extends MiTestSuite
	{
	private 	String[]	namesOfTests		= 
		{ "Math.max", "sqrt", "sin", "tan", "atan", "random" };

	private 	String[]	namesOfResults		= 
		{ "Math.max", "sqrt", "sin", "tan", "atan", "random" };

	private 	String[]	descriptionsOfResults		= 
		{
		"Number of Math.max calls per second",
		"Number of Math.sqrt calls per second",
		"Number of Math.sin calls per second",
		"Number of Math.tan calls per second",
		"Number of Math.atan calls per second",
		"Number of Math.random calls per second"
		};

	private		double		emptyLoopTime;
	private		double[]	testResults 		= new double[namesOfResults.length];
	private		String		MATH_TEST_SUITE_NAME	= "Math";
	private		int		NUMBER_OF_CALCS		= 1000000;



					/**------------------------------------------------------
	 				 * Constructs a new MiMathTestSuite.
					 *------------------------------------------------------*/
	public				MiMathTestSuite()
		{
		}

					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(MATH_TEST_SUITE_NAME);
		}
					/**------------------------------------------------------
			 		 * Gets the number of tests to run.
					 * @return		the number of tests
					 * @implements		MiiTestSuite#getNumberOfTests
					 *------------------------------------------------------*/
	public		int		getNumberOfTests()
		{
		return(namesOfTests.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test to run with the given index.
					 * @return		the name of the test
					 * @implements		MiiTestSuite#getNameOfTest
					 *------------------------------------------------------*/
	public		String		getNameOfTest(int index)
		{
		return(namesOfTests[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the number of test results after all tests are
					 * run.
					 * @return		the number of test results
					 * @implements		MiiTestSuite#getNumberOfTestResults
					 *------------------------------------------------------*/
	public		int		getNumberOfTestResults()
		{
		return(namesOfResults.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test result with the given index.
					 * @return		the name of the result
					 * @implements		MiiTestSuite#getNameOfTestResult
					 *------------------------------------------------------*/
	public		String		getNameOfTestResult(int index)
		{
		return(namesOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets a description of the test result with the given index.
					 * @return		the description of the result
					 * @implements		MiiTestSuite#getDescriptionsOfTestResult
					 *------------------------------------------------------*/
	public		String		getDescriptionsOfTestResult(int index)
		{
		return(descriptionsOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the test result with the given index.
					 * @return		the result
					 * @implements		MiiTestSuite#getTestResult
					 *------------------------------------------------------*/
	public		double 		getTestResult(int index)
		{
		return(testResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets whether the result returned from #getTestResult
					 * is better the larger it is.
					 * @return		true if the larger the result the better
					 * @implements		MiiTestSuite#isMoreBetterTestResult
					 *------------------------------------------------------*/
	public		boolean		isMoreBetterTestResult(int index)
		{
		return(true);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initializeTestRun
					 *------------------------------------------------------*/
	public		void		initialize()
		{
		for (int i = 0; i < testResults.length; ++i)
			testResults[i] = 0.0;

		double startTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_CALCS; ++i)
			;
		double finishTime = System.currentTimeMillis();
		emptyLoopTime = finishTime - startTime;
emptyLoopTime = 0;
		}
					/**------------------------------------------------------
	 				 * Runs the test with the given index.
					 * @param numberOfTest	the index of the test
					 * @return		the index of the next test to run
					 *			(which might be the same one)
					 * @implements		MiiTestSuite#executeTest
					 *------------------------------------------------------*/
	public		int		executeTest(int index)
		{
		int intValue = 123;
		double doubleValue = 123.45;
		double startTime = System.currentTimeMillis();
		switch (index)
			{
			case 0:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					Math.max(11, 1234);
				break;
			case 1:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					Math.sqrt(12345);
				break;
			case 2:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					Math.cos(1.234);
				break;
			case 3:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					Math.tan(1.234);
				break;
			case 4:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					Math.atan(1.234);
				break;
			case 5:
				for (int i = 0; i < NUMBER_OF_CALCS; ++i)
					Math.random();
				break;
			default:
				break;
			}
		double finishTime = System.currentTimeMillis();
		testResults[index] = 1000/(finishTime - startTime - emptyLoopTime) * NUMBER_OF_CALCS;
		return(index + 1);
		}
					/**------------------------------------------------------
			 		 * Cleanup after the test suite is done.
					 * @implements		MiiTestSuite#cleanUp
					 *------------------------------------------------------*/
	public		void		cleanUp()
		{
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number memory access
 * operations.
 *
 *----------------------------------------------------------------------------------------------*/
class MiMemoryTestSuite extends MiTestSuite
	{
	private 	String[]	namesOfTests		= 
		{"Int array alloc", "Int array writes", "Int array reads", "Object array alloc", "Object array writes", "Object array reads" };

	private 	String[]	namesOfResults		= 
		{"Int array alloc", "Int array writes", "Int array reads", "Object array alloc", "Object array writes", "Object array reads" };

	private 	String[]	descriptionsOfResults		= 
		{
		"Number of integers in an array allocated per second", 
		"Number of integers in an array written per second", 
		"Number of integers in an array read per second", 
		"Number of objects in an array allocated per second", 
		"Number of objects in an array written per second", 
		"Number of objects in an array read per second"
		};

	private		double		emptyLoopTime;
	private		double[]	testResults 		= new double[namesOfResults.length];
	private		String		MEMORY_TEST_SUITE_NAME	= "Memory";

	private		int		NUMBER_OF_READ_WRITES 	= 10000;
	private		int		NUMBER_OF_ARRAY_CREATES = 10;

	private		int		EMPTY_CLASS_CREATE_PER_SECOND_TIME_INDEX = 11;
	private		int		BOOLEAN_CLASS_CREATE_PER_SECOND_TIME_INDEX = 12;



					/**------------------------------------------------------
	 				 * Constructs a new MiMemoryTestSuite.
					 *------------------------------------------------------*/
	public				MiMemoryTestSuite()
		{
		}

					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(MEMORY_TEST_SUITE_NAME);
		}
					/**------------------------------------------------------
			 		 * Gets the number of tests to run.
					 * @return		the number of tests
					 * @implements		MiiTestSuite#getNumberOfTests
					 *------------------------------------------------------*/
	public		int		getNumberOfTests()
		{
		return(namesOfTests.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test to run with the given index.
					 * @return		the name of the test
					 * @implements		MiiTestSuite#getNameOfTest
					 *------------------------------------------------------*/
	public		String		getNameOfTest(int index)
		{
		return(namesOfTests[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the number of test results after all tests are
					 * run.
					 * @return		the number of test results
					 * @implements		MiiTestSuite#getNumberOfTestResults
					 *------------------------------------------------------*/
	public		int		getNumberOfTestResults()
		{
		return(namesOfResults.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test result with the given index.
					 * @return		the name of the result
					 * @implements		MiiTestSuite#getNameOfTestResult
					 *------------------------------------------------------*/
	public		String		getNameOfTestResult(int index)
		{
		return(namesOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets a description of the test result with the given index.
					 * @return		the description of the result
					 * @implements		MiiTestSuite#getDescriptionsOfTestResult
					 *------------------------------------------------------*/
	public		String		getDescriptionsOfTestResult(int index)
		{
		return(descriptionsOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the test result with the given index.
					 * @return		the result
					 * @implements		MiiTestSuite#getTestResult
					 *------------------------------------------------------*/
	public		double 		getTestResult(int index)
		{
		return(testResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets whether the result returned from #getTestResult
					 * is better the larger it is.
					 * @return		true if the larger the result the better
					 * @implements		MiiTestSuite#isMoreBetterTestResult
					 *------------------------------------------------------*/
	public		boolean		isMoreBetterTestResult(int index)
		{
		return(false);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initializeTestRun
					 *------------------------------------------------------*/
	public		void		initialize()
		{
		for (int i = 0; i < testResults.length; ++i)
			testResults[i] = 0.0;

		double startTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
			;
		double finishTime = System.currentTimeMillis();
		emptyLoopTime = finishTime - startTime;
emptyLoopTime = 0;
		}
					/**------------------------------------------------------
	 				 * Runs the test with the given index.
					 * @param numberOfTest	the index of the test
					 * @return		the index of the next test to run
					 *			(which might be the same one)
					 * @implements		MiiTestSuite#executeTest
					 *------------------------------------------------------*/
	public		int		executeTest(int index)
		{
		int[] intArray = new int[NUMBER_OF_READ_WRITES];
		Object[] objArray = new Object[NUMBER_OF_READ_WRITES];
		double startTime = System.currentTimeMillis();
		switch (index)
			{
			case 0:
				for (int i = 0; i < NUMBER_OF_ARRAY_CREATES; ++i)
					intArray = new int[NUMBER_OF_READ_WRITES];
				break;
			case 1:
				for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
					intArray[i] = 1;
				break;
			case 2:
				for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
					{
					int j = intArray[i];
					}
				break;
			case 3:
				for (int i = 0; i < NUMBER_OF_ARRAY_CREATES; ++i)
					objArray = new Object[NUMBER_OF_READ_WRITES];
				break;
			case 4:
				for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
					objArray[i] = this;
				break;
			case 5:
				for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
					{
					Object obj = objArray[i];
					}
				break;
			default:
				break;
			}
		double finishTime = System.currentTimeMillis();
		int numActions = NUMBER_OF_READ_WRITES;
		if ((index == 0) || (index == 3))
			numActions *= NUMBER_OF_ARRAY_CREATES;
		testResults[index] = 1000/(finishTime - startTime - emptyLoopTime) * numActions;
		return(index + 1);
		}
					/**------------------------------------------------------
			 		 * Cleanup after the test suite is done.
					 * @implements		MiiTestSuite#cleanUp
					 *------------------------------------------------------*/
	public		void		cleanUp()
		{
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This test suite tests the speed of a number of java.util
 * class operations.
 *
 *----------------------------------------------------------------------------------------------*/
class MiUtilClassesTestSuite extends MiTestSuite
	{
	private 	String[]	namesOfTests		= 
		{"Fast vector alloc", "Fast vector writes", "Fast vector reads", "Vector alloc", "Vector writes", "Vector reads" };

	private 	String[]	namesOfResults		= 
		{"Fast vector alloc", "Fast vector writes", "Fast vector reads", "Vector alloc", "Vector writes", "Vector reads" };

	private 	String[]	descriptionsOfResults		= 
		{
		"Number of objects in an fast vector allocated per second", 
		"Number of objects in an fast vector written per second", 
		"Number of objects in an fast vector read per second",
		"Number of objects in an vector allocated per second", 
		"Number of objects in an vector written per second", 
		"Number of objects in an vector read per second"
		};

	private		double		emptyLoopTime;
	private		double[]	testResults 		= new double[namesOfResults.length];
	private		String		MEMORY_TEST_SUITE_NAME	= "Utility Classes";

	private		int		NUMBER_OF_READ_WRITES 	= 10000;
	private		int		NUMBER_OF_ARRAY_CREATES = 10;
	private		FastVector 	objVector;
	private		Vector 		vector;

					/**------------------------------------------------------
	 				 * Constructs a new MiUtilClassesTestSuite.
					 *------------------------------------------------------*/
	public				MiUtilClassesTestSuite()
		{
		}

					/**------------------------------------------------------
			 		 * Gets the name of the test suite.
					 * @return		the name
					 * @implements		MiiTestSuite#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(MEMORY_TEST_SUITE_NAME);
		}
					/**------------------------------------------------------
			 		 * Gets the number of tests to run.
					 * @return		the number of tests
					 * @implements		MiiTestSuite#getNumberOfTests
					 *------------------------------------------------------*/
	public		int		getNumberOfTests()
		{
		return(namesOfTests.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test to run with the given index.
					 * @return		the name of the test
					 * @implements		MiiTestSuite#getNameOfTest
					 *------------------------------------------------------*/
	public		String		getNameOfTest(int index)
		{
		return(namesOfTests[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the number of test results after all tests are
					 * run.
					 * @return		the number of test results
					 * @implements		MiiTestSuite#getNumberOfTestResults
					 *------------------------------------------------------*/
	public		int		getNumberOfTestResults()
		{
		return(namesOfResults.length);
		}
					/**------------------------------------------------------
			 		 * Gets the name of the test result with the given index.
					 * @return		the name of the result
					 * @implements		MiiTestSuite#getNameOfTestResult
					 *------------------------------------------------------*/
	public		String		getNameOfTestResult(int index)
		{
		return(namesOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets a description of the test result with the given index.
					 * @return		the description of the result
					 * @implements		MiiTestSuite#getDescriptionsOfTestResult
					 *------------------------------------------------------*/
	public		String		getDescriptionsOfTestResult(int index)
		{
		return(descriptionsOfResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets the test result with the given index.
					 * @return		the result
					 * @implements		MiiTestSuite#getTestResult
					 *------------------------------------------------------*/
	public		double 		getTestResult(int index)
		{
		return(testResults[index]);
		}
					/**------------------------------------------------------
			 		 * Gets whether the result returned from #getTestResult
					 * is better the larger it is.
					 * @return		true if the larger the result the better
					 * @implements		MiiTestSuite#isMoreBetterTestResult
					 *------------------------------------------------------*/
	public		boolean		isMoreBetterTestResult(int index)
		{
		return(false);
		}
					/**------------------------------------------------------
			 		 * Initialize the test suite.
					 * @param editor	to be used for output for drawing tests
					 * @implements		MiiTestSuite#initializeTestRun
					 *------------------------------------------------------*/
	public		void		initialize()
		{
		for (int i = 0; i < testResults.length; ++i)
			testResults[i] = 0.0;

		double startTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
			;
		double finishTime = System.currentTimeMillis();
		emptyLoopTime = finishTime - startTime;
emptyLoopTime = 0;
		}
					/**------------------------------------------------------
	 				 * Runs the test with the given index.
					 * @param numberOfTest	the index of the test
					 * @return		the index of the next test to run
					 *			(which might be the same one)
					 * @implements		MiiTestSuite#executeTest
					 *------------------------------------------------------*/
	public		int		executeTest(int index)
		{
		double startTime = System.currentTimeMillis();
		switch (index)
			{
			case 0:
				for (int i = 0; i < NUMBER_OF_ARRAY_CREATES; ++i)
					objVector = new FastVector(NUMBER_OF_READ_WRITES);
				break;
			case 1:
				for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
					objVector.addElement(this);
				break;
			case 2:
				for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
					{
					Object obj = objVector.elementAt(i);
					}
				break;
			case 3:
				for (int i = 0; i < NUMBER_OF_ARRAY_CREATES; ++i)
					vector = new Vector(NUMBER_OF_READ_WRITES);
				break;
			case 4:
				for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
					vector.addElement(this);
				break;
			case 5:
				for (int i = 0; i < NUMBER_OF_READ_WRITES; ++i)
					{
					Object obj = vector.elementAt(i);
					}
				break;
			default:
				break;
			}
		double finishTime = System.currentTimeMillis();
		int numActions = NUMBER_OF_READ_WRITES;
		if ((index == 0) || (index == 3))
			numActions *= NUMBER_OF_ARRAY_CREATES;
		testResults[index] = 1000/(finishTime - startTime - emptyLoopTime) * numActions;
		return(index + 1);
		}
					/**------------------------------------------------------
			 		 * Cleanup after the test suite is done.
					 * @implements		MiiTestSuite#cleanUp
					 *------------------------------------------------------*/
	public		void		cleanUp()
		{
		}
	}


