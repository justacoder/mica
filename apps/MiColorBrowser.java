
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
import com.swfm.mica.util.Pair; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Strings; 
import java.util.StringTokenizer; 
import java.io.*;
import java.awt.Color;
import java.awt.Container;
import java.applet.Applet;
import java.util.Vector;

/**----------------------------------------------------------------------------------------------
 * This class implements a color browser that displays a number
 * of color combinations using the Mica Graphics Framework. This 
 * includes the display of the standard 16 colors defined in 
 * java.awt.Color, the 35 or so named Mica colors, the 216 
 * browser-safe colors, and a large number of colors defined
 * for the X Window System.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiColorBrowser extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiColorBrowserWindow. Supported command line parameters
					 * are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String title = Utility.getCommandLineArgument(args, "-title");
		MiColorBrowserWindow window = new MiColorBrowserWindow(
			title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		window.populateTable();
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiColorBrowserWindow. Supported html file parameters are:
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);

		String title = getParameter("title");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiColorBrowserWindow window = new MiColorBrowserWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a color browser window that displays a number
 * of color combinations using the Mica Graphics Framework. This 
 * includes the display of the standard 16 colors defined in 
 * java.awt.Color, the 35 or so named Mica colors, the 216 
 * browser-safe colors, and a large number of colors defined
 * for the X Window System.
 *
 *----------------------------------------------------------------------------------------------*/
class MiColorBrowserWindow extends MiEditorWindow implements MiiDisplayNames
	{
	// ---------------------------------------------------------------
	// Define the names of some commands
	// ---------------------------------------------------------------
	private static final String	Mi_VIEW_NAMED_THUMBNAILS_COMMAND_NAME	
						= "DisplayInfoColorSwatches";
	private static final String	Mi_DO_NOT_VIEW_NAMED_THUMBNAILS_COMMAND_NAME
						= "DontDisplayInfoColorSwatches";

	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	
						= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	private static final String 	Mi_COLOR_NAME_RESOURCE_NAME	
						= "Mi_COLOR_NAME_RESOURCE_NAME";
	private static final String 	Mi_COLOR_RESOURCE_NAME		
						= "Mi_COLOR_RESOURCE_NAME";
	private static final String	Mi_DISPLAY_216_BROWSER_COLORS_NAME 
						= "Mi_DISPLAY_216_BROWSER_COLORS_NAME";
	private static final String	Mi_DISPLAY_AWT_BASIC_COLORS_NAME
						= "Mi_DISPLAY_AWT_BASIC_COLORS_NAME";
	private static final String	Mi_DISPLAY_MICA_NAMED_COLORS_NAME
						= "Mi_DISPLAY_MICA_NAMED_COLORS_NAME";
	private static final String	Mi_DISPLAY_RGB_TXT_NAMED_COLORS_NAME
						= "Mi_DISPLAY_RGB_TXT_NAMED_COLORS_NAME";
	private static final String	Mi_VIEW_NAMED_THUMBNAILS_NAME	
						= "Mi_VIEW_NAMED_THUMBNAILS_NAME";
	private static final String	Mi_COLOR_BROWSER_ABOUT_MSG
						= "Mi_COLOR_BROWSER_ABOUT_MSG";

	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
	{
	new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE	, "The Color Browser"),
	new Pair( Mi_COLOR_NAME_RESOURCE_NAME		, "colorName"),
	new Pair( Mi_COLOR_RESOURCE_NAME		, "color"),
	new Pair( Mi_DISPLAY_216_BROWSER_COLORS_NAME	, "The 216 Browser-safe Colors"),
	new Pair( Mi_DISPLAY_AWT_BASIC_COLORS_NAME	, "The AWT Basic Colors"),
	new Pair( Mi_DISPLAY_MICA_NAMED_COLORS_NAME	, "Mica\'s Named Colors"),
	new Pair( Mi_DISPLAY_RGB_TXT_NAMED_COLORS_NAME	, "X Window\'s Named Colors"),
	new Pair( Mi_VIEW_NAMED_THUMBNAILS_NAME		, "Display Info on Color Swatches"),
	new Pair( Mi_COLOR_BROWSER_ABOUT_MSG		, 
		"\n\nThis browser is part of the Mica Graphics Appletcation Suite.\n\n"
		+ "This browser displays a number of different groups of colors\n"
		+ "(see the \'View\' pulldown menu in the menubar).\n\n\n"
		+ "Version 1.0      Febuary 23, 1998")
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
	private		MiStatusBar		statusBar;
	private		String			title;
	private		MiTable			table;
	private		boolean			toLabelCells;
	private		int			numberOfColors;
	private		Vector			colors			= new Vector();
	private		Strings			colorNames		= new Strings();
	private		MiSize			colorSwatchSize		= new MiSize(48, 32);



					/**------------------------------------------------------
	 				 * Constructs a new MiColorBrowserWindow.
					 * @param awtContainer	the container to add this to
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiColorBrowserWindow( 
						Container awtContainer, 
						String title, 
						MiBounds screenSize)
		{
		super(MiUtility.getFrame(awtContainer), 
			title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);
		this.title = (title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title);
		awtContainer.add("Center", (java.awt.Component )getCanvas().getNativeComponent());
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiColorBrowserWindow.
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiColorBrowserWindow(String title, MiBounds screenSize)
		{
		super(title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, screenSize);
		this.title = (title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title);
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

		getEditor().setBackgroundColor(MiColorManager.lightGray);

		// ---------------------------------------------------------------
		// Create the table that will contain the color swatches
		// ---------------------------------------------------------------
		table = new MiTable();
		table.getSelectionManager().setMaximumNumberSelected(0);
		table.setTotalNumberOfColumns(14);
		table.setHasFixedTotalNumberOfColumns(true);
		MiRectangle backgroundRect = new MiRectangle();
		backgroundRect.setBorderLook(Mi_RAISED_BORDER_LOOK);
		backgroundRect.setBackgroundColor(table.getBackgroundColor());
		table.getBackgroundManager().appendCellBackgrounds(backgroundRect);

		// ---------------------------------------------------------------
		// Make the table scrollable
		// ---------------------------------------------------------------
		MiScrolledBox scrolledTable = new MiScrolledBox(table);
		getEditor().appendPart(scrolledTable);
		

		// ---------------------------------------------------------------
		// Attach the table to the editor and make it expand to fill the
		// editor panel no matter what size the window is resized to
		// ---------------------------------------------------------------
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementIndex(0);
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		getEditor().setLayout(columnLayout);

		// ---------------------------------------------------------------
		// Set the toggle button to 'on' for the colors we display first
		// ---------------------------------------------------------------
		setCommandState(Mi_DISPLAY_AWT_BASIC_COLORS_NAME, true);
		}
					/**------------------------------------------------------
	 				 * Make the menubar. Tailor it's menus for this application.
					 * This is called from the super class's buildEditorWindow
					 * method.
					 * @return		the menubar
					 *------------------------------------------------------*/
	protected	MiEditorMenuBar	makeDefaultMenuBar()
		{
		MiEditorMenu[] pulldowns = new MiEditorMenu[3];
		pulldowns[0] = new MiFileMenu(this, this);
		pulldowns[0].removeAllMenuItemsWithCommandsExcept(new Strings(Mi_QUIT_COMMAND_NAME));
		MiEditorMenu viewMenu = new MiViewMenu(this);
		pulldowns[1] = viewMenu;
		viewMenu.removeAllMenuItems();
		viewMenu.startRadioButtonSection();
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_216_BROWSER_COLORS_NAME, this);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_AWT_BASIC_COLORS_NAME, this);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_MICA_NAMED_COLORS_NAME, this);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_RGB_TXT_NAMED_COLORS_NAME, this);
		viewMenu.endRadioButtonSection();
		viewMenu.appendSeparator();
		viewMenu.appendBooleanMenuItem(Mi_VIEW_NAMED_THUMBNAILS_NAME, this, 
							Mi_VIEW_NAMED_THUMBNAILS_COMMAND_NAME,
							Mi_DO_NOT_VIEW_NAMED_THUMBNAILS_COMMAND_NAME);


		pulldowns[2] = new MiHelpMenu(this, this);
		pulldowns[2].removeAllMenuItemsWithCommandsExcept(new Strings(
					Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME));

		return(new MiEditorMenuBar(pulldowns));
		}
					/**------------------------------------------------------
	 				 * Makes the toolbar. This application has no toolbar so
					 * return null. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the toolbar
					 *------------------------------------------------------*/
	protected	MiToolBar	makeDefaultToolBar()
		{
		return(null);
		}
					/**------------------------------------------------------
	 				 * Makes the status bar. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the status bar
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultStatusBar()
		{
		MiEditorStatusBar bar = new MiEditorStatusBar();
		statusBar = new MiStatusBar(".30\n.24\n.11\n.11\n.11\n.8");
		bar.appendPart(statusBar);

		bar.setOverlayStatusField(new MiStatusBarFocusStatusField(this));
		return(bar);
		}
					/**------------------------------------------------------
	 				 * Fills the cells of the table with the color swatches
					 * from the 'colorNames' and 'colors' lists.
					 *------------------------------------------------------*/
	protected	void		populateTable()
		{
		table.removeAllCells();
		numberOfColors = 0;
		MiParts cells = new MiParts();
		for (int i = 0; i < colors.size(); ++i)
			{
			String colorName = (colorNames.size() > 0) ? colorNames.elementAt(i) : null;
			cells.addElement(makeCell(colorName, (Color )colors.elementAt(i)));
			}
		table.appendItems(cells);
		numberOfColors = colors.size();
		statusBar.setFieldValue("", 0);
		statusBar.setFieldValue("Number of colors: " + numberOfColors, 1);
		}
					/**------------------------------------------------------
	 				 * Loads a file ('rgb.txt') that contains the names and
					 * rgb values of a large number of colors. Each entry in
					 * the file is of form: '255 250 250          snow'.
					 * @param filename	the file that contains the colors
					 *------------------------------------------------------*/
	protected	void		loadRGBFile(String filename)
		{
		BufferedReader file;
		if ((file = Utility.openInputFile(filename)) == null)
			return;

		colors.removeAllElements();
		colorNames.removeAllElements();
		
		String line;
		String lastRgbNumbers = new String();
		numberOfColors = 0;
		while (true)
			{
			line = Utility.readLine(file);
			if (line == null)
				{
				return;
				}

			String tmp;
			String rgbNumbers = new String();
			StringTokenizer t = new StringTokenizer(line);

			if ((line.length() == 0) || (line.charAt(0) == '\0') 
				|| (line.charAt(0) == '\n') || (line.charAt(0) == '#'))
				{
				continue;
				}

			tmp = t.nextToken().trim();
			int red = Integer.parseInt(tmp);
			rgbNumbers = rgbNumbers.concat(tmp + " ");

			tmp = t.nextToken().trim();
			int green = Integer.parseInt(tmp);
			rgbNumbers = rgbNumbers.concat(tmp + " ");

			tmp = t.nextToken().trim();
			int blue = Integer.parseInt(tmp);
			rgbNumbers = rgbNumbers.concat(tmp);

			String name = new String();
			while (t.hasMoreTokens())
				{
				name += t.nextToken().trim();
				}
			if (!rgbNumbers.equals(lastRgbNumbers))
				{
				colors.addElement(new Color(red, green, blue));
				colorNames.addElement(name);
				++numberOfColors;
				}
			lastRgbNumbers = rgbNumbers;
			}
		}
					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are:
					 * <pre>
					 *    Mi_DISPLAY_216_BROWSER_COLORS_NAME
					 *    Mi_DISPLAY_AWT_BASIC_COLORS_NAME
					 *    Mi_DISPLAY_MICA_NAMED_COLORS_NAME
					 *    Mi_DISPLAY_RGB_TXT_NAMED_COLORS_NAME
					 *    Mi_VIEW_NAMED_THUMBNAILS_COMMAND_NAME
					 *    Mi_DO_NOT_VIEW_NAMED_THUMBNAILS_COMMAND_NAME
					 *    Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 * @overrides		MiEditorWindow#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		if (command.equals(Mi_DISPLAY_216_BROWSER_COLORS_NAME))
			{
			setTitle(MiSystem.getProperty(title, title) 
				+ ": " + MiSystem.getProperty(command, command));

			colors.removeAllElements();
			for (int i = 0; i < theBrowserSafe126ColorValues.length; ++i)
				{
				colors.addElement(
					MiColorManager.getColor("#" + theBrowserSafe126ColorValues[i]));
				}
			colorNames.removeAllElements();
			populateTable();
			}
		else if (command.equals(Mi_DISPLAY_AWT_BASIC_COLORS_NAME))
			{
			setTitle(MiSystem.getProperty(title, title) 
				+ ": " + MiSystem.getProperty(command, command));

			colors.removeAllElements();
			colorNames.removeAllElements();
			colors.addElement(Color.white);
			colorNames.addElement("Color.white");
			colors.addElement(Color.lightGray);
			colorNames.addElement("Color.lightGray");
			colors.addElement(Color.gray);
			colorNames.addElement("Color.gray");
			colors.addElement(Color.darkGray);
			colorNames.addElement("Color.darkGray");
			colors.addElement(Color.black);
			colorNames.addElement("Color.black");
			colors.addElement(Color.red);
			colorNames.addElement("Color.red");
			colors.addElement(Color.pink);
			colorNames.addElement("Color.pink");
			colors.addElement(Color.orange);
			colorNames.addElement("Color.orange");
			colors.addElement(Color.yellow);
			colorNames.addElement("Color.yellow");
			colors.addElement(Color.green);
			colorNames.addElement("Color.green");
			colors.addElement(Color.magenta);
			colorNames.addElement("Color.magenta");
			colors.addElement(Color.cyan);
			colorNames.addElement("Color.cyan");
			colors.addElement(Color.blue);
			colorNames.addElement("Color.blue");

			populateTable();
			}
		else if (command.equals(Mi_DISPLAY_MICA_NAMED_COLORS_NAME))
			{
			setTitle(MiSystem.getProperty(title, title) 
				+ ": " + MiSystem.getProperty(command, command));

			colors.removeAllElements();
			Color[] colorArray = MiColorManager.getColors();
			for (int i = 0; i < colorArray.length; ++i)
				{
				colors.addElement(colorArray[i]);
				}
			colorNames.removeAllElements();
			String[] colorNameArray = MiColorManager.getColorNames();
			for (int i = 0; i < colorNameArray.length; ++i)
				{
				colorNames.addElement(colorNameArray[i]);
				}
			populateTable();
			}
		else if (command.equals(Mi_DISPLAY_RGB_TXT_NAMED_COLORS_NAME))
			{
			setTitle(MiSystem.getProperty(title, title) 
				+ ": " + MiSystem.getProperty(command, command));

			colors.removeAllElements();
			colorNames.removeAllElements();
			loadRGBFile("rgb.txt");
			populateTable();
			}
		else if (command.equals(Mi_VIEW_NAMED_THUMBNAILS_COMMAND_NAME))
			{
			toLabelCells = true;
			table.setTotalNumberOfColumns(7);
			populateTable();
			}
		else if (command.equals(Mi_DO_NOT_VIEW_NAMED_THUMBNAILS_COMMAND_NAME))
			{
			toLabelCells = false;
			table.setTotalNumberOfColumns(14);
			populateTable();
			}
		else if (command.startsWith(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME))
			{
			MiToolkit.postAboutDialog(
				this, title, MiSystem.getCompanyLogo(), Mi_COLOR_BROWSER_ABOUT_MSG, false);
			}
		else
			{
			super.processCommand(command);
			}
		setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are:
					 * <pre>
					 *    Mi_GOT_MOUSE_FOCUS_ACTION
					 *    Mi_LOST_MOUSE_FOCUS_ACTION
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_GOT_MOUSE_FOCUS_ACTION))
			{
			MiPart cellGraphics = action.getActionSource();
			String colorName = (String )cellGraphics.getResource(Mi_COLOR_NAME_RESOURCE_NAME);
			Color color = (Color )cellGraphics.getResource(Mi_COLOR_RESOURCE_NAME);
			updateStatusBar(colorName, color);
			}
		else if (action.hasActionType(Mi_LOST_MOUSE_FOCUS_ACTION))
			{
			updateStatusBar(null, null);
			} 
		return(true);
		}
					/**------------------------------------------------------
		 			 * Update the status bar to display information about the
					 * given color.
					 * @param colorName	the name of the color
					 * @param color		the color
					 *------------------------------------------------------*/
	protected	void		updateStatusBar(String colorName, Color color)
		{
		if (colorName != null)
			statusBar.setFieldValue(colorName, 0);
		else
			statusBar.setFieldValue("", 0);

		if (color != null)
			{
			statusBar.setFieldValue("Red: " + color.getRed(), 2);
			statusBar.setFieldValue("Green: " + color.getGreen(), 3);
			statusBar.setFieldValue("Blue: " + color.getBlue(), 4);
			statusBar.setFieldValue(colorToHexString(color), 5);
			}
		else
			{
			statusBar.setFieldValue("", 2);
			statusBar.setFieldValue("", 3);
			statusBar.setFieldValue("", 4);
			statusBar.setFieldValue("", 5);
			}
		}
					/**------------------------------------------------------
		 			 * Get a text string that represents the hexidecimal value
					 * of the RGB values of the given color.
					 * @param color		the color
					 * @return		the text string of form: #ffcc00
					 *------------------------------------------------------*/
	protected	String		colorToHexString(Color color)
		{
		String value = Integer.toHexString(
				(color.getRed() << 16) + (color.getGreen() << 8) + (color.getBlue()));
		while (value.length() < 6)
			value = "0" + value;
		value = "#" + value.toUpperCase();
		return(value);
		}
					/**------------------------------------------------------
		 			 * Makes a color swatch for the given color.
					 * @param colorName	the name of the color
					 * @param color		the color
					 * @return		graphics to display the color
					 *------------------------------------------------------*/
	protected	MiPart		makeCell(String colorName, Color color)
		{
		if (toLabelCells)
			{
			MiContainer container = new MiContainer();
			MiColumnLayout layout = new MiColumnLayout();
			MiPart rect = new MiEllipse();
			rect.setSize(colorSwatchSize);
			rect.setBackgroundColor(color);
			layout.setElementHSizing(Mi_NONE);
			layout.setAlleyVSpacing(4);
			container.setLayout(layout);
			container.appendPart(rect);
			if (colorName != null)
				container.appendPart(new MiText(colorName));
			// If not transparent
			if (color != null)
				{
				container.appendPart(new MiText(
					color.getRed() + " " + color.getGreen() + " " + color.getBlue()));
				}
			container.setResource(MiColorBrowserWindow.Mi_COLOR_NAME_RESOURCE_NAME, colorName);
			container.setResource(MiColorBrowserWindow.Mi_COLOR_RESOURCE_NAME, color);
			container.appendActionHandler(this, Mi_GOT_MOUSE_FOCUS_ACTION);
			container.appendActionHandler(this, Mi_LOST_MOUSE_FOCUS_ACTION);
			container.setAcceptingMouseFocus(true);
			return(container);
			}
		else
			{
			MiPart rect = new MiEllipse();
			rect.setSize(colorSwatchSize);
			rect.setBackgroundColor(color);
			rect.setResource(MiColorBrowserWindow.Mi_COLOR_NAME_RESOURCE_NAME, colorName);
			rect.setResource(MiColorBrowserWindow.Mi_COLOR_RESOURCE_NAME, color);
			rect.appendActionHandler(this, Mi_GOT_MOUSE_FOCUS_ACTION);
			rect.appendActionHandler(this, Mi_LOST_MOUSE_FOCUS_ACTION);
			rect.setAcceptingMouseFocus(true);
			return(rect);
			}
		}

/* From: http://www.carney.com/HTML/06_216.html ------------------------
   
     With 24 bit color, JPEGs can use millions of color. Most people
     have 8 bit (256 color) monitors. The Macintosh and Windows
     operating system do not use the same 256, there are 216 in common
     -- i.e., there are 216 "safe" colors for www graphics. All other
     colors will display with dithering. Hex colors range from 000000 to
     FFFFFF. These are hexadecimal numbers representing Red-Green-Blue
     values.
     
     
     The first two values represent the red.
     The second two values represent the green.
     The third two values represent the blue.
     
     Acceptable values (hex format) are:
     00
     33
     66
     99
     CC
     FF
     
     Acceptable values (000-255 RGB format) are:
     000
     051
     102
     153
     204
     255
     
   Here are the 216 safe colors with the corresponding hex values:

----------------------------------------------------------------- */

String[]	theBrowserSafe126ColorValues = {
	"000000",
	"000033",
	"000066",
	"000099",
	"0000CC",
	"0000FF",
	"003300",
	"003333",
	"003366",
	"003399",
	"0033CC",
	"0033FF",
	"006600",
	"006633",
	"006666",
	"006699",
	"0066CC",
	"0066FF",
	"009900",
	"009933",
	"009966",
	"009999",
	"0099CC",
	"0099FF",
	"00CC00",
	"00CC33",
	"00CC66",
	"00CC99",
	"00CCCC",
	"00CCFF",
	"00FF00",
	"00FF33",
	"00FF66",
	"00FF99",
	"00FFCC",
	"00FFFF",
	"330000",
	"330033",
	"330066",
	"330099",
	"3300CC",
	"3300FF",
	"333300",
	"333333",
	"333366",
	"333399",
	"3333CC",
	"3333FF",
	"336600",
	"336633",
	"336666",
	"336699",
	"3366CC",
	"3366FF",
	"339900",
	"339933",
	"339966",
	"339999",
	"3399CC",
	"3399FF",
	"33CC00",
	"33CC33",
	"33CC66",
	"33CC99",
	"33CCCC",
	"33CCFF",
	"33FF00",
	"33FF33",
	"33FF66",
	"33FF99",
	"33FFCC",
	"33FFFF",
	"660000",
	"660033",
	"660066",
	"660099",
	"6600CC",
	"6600FF",
	"663300",
	"663333",
	"663366",
	"663399",
	"6633CC",
	"6633FF",
	"666600",
	"666633",
	"666666",
	"666699",
	"6666CC",
	"6666FF",
	"669900",
	"669933",
	"669966",
	"669999",
	"6699CC",
	"6699FF",
	"66CC00",
	"66CC33",
	"66CC66",
	"66CC99",
	"66CCCC",
	"66CCFF",
	"66FF00",
	"66FF33",
	"66FF66",
	"66FF99",
	"66FFCC",
	"66FFFF",
	"990000",
	"990033",
	"990066",
	"990099",
	"9900CC",
	"9900FF",
	"993300",
	"993333",
	"993366",
	"993399",
	"9933CC",
	"9933FF",
	"996600",
	"996633",
	"996666",
	"996699",
	"9966CC",
	"9966FF",
	"999900",
	"999933",
	"999966",
	"999999",
	"9999CC",
	"9999FF",
	"99CC00",
	"99CC33",
	"99CC66",
	"99CC99",
	"99CCCC",
	"99CCFF",
	"99FF00",
	"99FF33",
	"99FF66",
	"99FF99",
	"99FFCC",
	"99FFFF",
	"CC0000",
	"CC0033",
	"CC0066",
	"CC0099",
	"CC00CC",
	"CC00FF",
	"CC3300",
	"CC3333",
	"CC3366",
	"CC3399",
	"CC33CC",
	"CC33FF",
	"CC6600",
	"CC6633",
	"CC6666",
	"CC6699",
	"CC66CC",
	"CC66FF",
	"CC9900",
	"CC9933",
	"CC9966",
	"CC9999",
	"CC99CC",
	"CC99FF",
	"CCCC00",
	"CCCC33",
	"CCCC66",
	"CCCC99",
	"CCCCCC",
	"CCCCFF",
	"CCFF00",
	"CCFF33",
	"CCFF66",
	"CCFF99",
	"CCFFCC",
	"CCFFFF",
	"FF0000",
	"FF0033",
	"FF0066",
	"FF0099",
	"FF00CC",
	"FF00FF",
	"FF3300",
	"FF3333",
	"FF3366",
	"FF3399",
	"FF33CC",
	"FF33FF",
	"FF6600",
	"FF6633",
	"FF6666",
	"FF6699",
	"FF66CC",
	"FF66FF",
	"FF9900",
	"FF9933",
	"FF9966",
	"FF9999",
	"FF99CC",
	"FF99FF",
	"FFCC00",
	"FFCC33",
	"FFCC66",
	"FFCC99",
	"FFCCCC",
	"FFCCFF",
	"FFFF00",
	"FFFF33",
	"FFFF66",
	"FFFF99",
	"FFFFCC",
	"FFFFFF",
	};
	}

