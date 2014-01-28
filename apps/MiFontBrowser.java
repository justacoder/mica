
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
import java.awt.Font; 
import java.util.Vector; 
import com.swfm.mica.util.Pair; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Strings; 
import java.awt.Container;
import java.applet.Applet;

/**----------------------------------------------------------------------------------------------
 * This class implements a font browser that displays the java.awt
 * fonts in all 4 styles (plain, bold, italic and bold-italic) in any
 * size desired.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiFontBrowser extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiFontBrowserWindow. Supported command line parameters
					 * are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String title = Utility.getCommandLineArgument(args, "-title");
		MiFontBrowserWindow window = new MiFontBrowserWindow(
			title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiFontBrowserWindow. Supported html file parameters are:
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);

		String title = getParameter("title");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiFontBrowserWindow window = new MiFontBrowserWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class implements a font browser window that displays the java.awt
 * fonts in all 4 styles (plain, bold, italic and bold-italic) in any
 * size desired.
 *
 *----------------------------------------------------------------------------------------------*/
class MiFontBrowserWindow extends MiEditorWindow implements MiiDisplayNames
	{
	// ---------------------------------------------------------------
	// Define the names of some commands
	// ---------------------------------------------------------------
	private static final String	Mi_INCREASE_POINT_SIZE_COMMAND_NAME	= "incPointSize";
	private static final String	Mi_DECREASE_POINT_SIZE_COMMAND_NAME	= "decPointSize";

	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	
						= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	private static final String	Mi_FONT_MANAGER_ABOUT_MSG
						= "Mi_FONT_MANAGER_ABOUT_MSG";

	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
		{
		new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE	, "The Font Browser"),
		new Pair( Mi_FONT_MANAGER_ABOUT_MSG		, 
			"\n\nThis browser is part of the Mica Appletcation Suite.\n\n"
			+ "This browser lists all of the Java AWT system fonts,\n"
			+ "each in bold, italic, bold plus italic, and plain styles.\n"
			+ "Initially presented in a 12 point size, the fonts can be\n"
			+ "increased or decreased in size. There bounds of the fonts\n"
			+ "are outlined in red to confirm the veracity of the font size\n"
			+ "routines.\n\n\n"
			+ "Version 1.0      Febuary 23, 1998")
		};

	// ---------------------------------------------------------------
	// Register the default values for this applications properties.
	// ---------------------------------------------------------------
	static	{
		MiSystem.setApplicationDefaultProperties(properties);
		}

	private static final int		DEFAULT_POINT_SIZE		= 12;
	private static final int		MAX_POINT_SIZE			= 90;
	private static final int		MIN_POINT_SIZE			= 3;

	// ---------------------------------------------------------------
	// Define the class variables this class will use.
	// ---------------------------------------------------------------
	private		MiList 			list;
	private		String 			title;
	private		MiEditor 		editor;
	private		Vector 			fontDisplays			= new Vector();
	private		MiBoundsRenderer	boundsRenderer;
	private		String 			sampleString 
	="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=[]{}|\\\"\':;>.<,?/";
	private static	int[]			fontStyles 			=
										{
										Font.PLAIN,
										Font.BOLD,
										Font.ITALIC,
										Font.BOLD + Font.ITALIC
										};




					/**------------------------------------------------------
	 				 * Constructs a new MiFontBrowserWindow.
					 * @param awtContainer	the container to add this to
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiFontBrowserWindow( 
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
	 				 * Constructs a new MiFontBrowserWindow.
					 * @param title		the title of this window
					 * @param screenSize	the size of this window
					 *------------------------------------------------------*/
	public				MiFontBrowserWindow(String title, MiBounds screenSize)
		{
		super(title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title, screenSize);
		this.title = (title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title);
		setup();
		}
					/**------------------------------------------------------
	 				 * Setup the window, creating the menubar, status bar,
					 * the list for the fonts... 
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

		// ---------------------------------------------------------------
		// Modify the help menu so that only the 'About...' option is displayed
		// ---------------------------------------------------------------
		getMenuBar().getMenu(Mi_HELP_MENU_DISPLAY_NAME)
			.removeAllMenuItemsWithCommandsExcept(new Strings(
					Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME));
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
		pulldowns[0] = new MiFileMenu(this, this);
		pulldowns[0].removeAllMenuItemsWithCommandsExcept(new Strings(Mi_QUIT_COMMAND_NAME));

		pulldowns[1] = new MiHelpMenu(this, this);
		return(new MiEditorMenuBar(pulldowns));
		}
					/**------------------------------------------------------
	 				 * Make the toolbar. This application has no toolbar so
					 * return null. This is called from the super class's
					 * buildEditorWindow method.
					 * @return		the toolbar
					 *------------------------------------------------------*/
	protected	MiToolBar	makeDefaultToolBar()
		{
		return(null);
		}
					/**------------------------------------------------------
	 				 * Make the main panel. This is called from the super class's
					 * buildEditorWindow method. This method makes the list that
					 * will contain the fonts.
					 * @return		the main panel
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultEditorPanel()
		{
		list = new MiList(40, false);
		list.setMinimumNumberOfVisibleRows(3);
		list.setPreferredNumberOfVisibleRows(3);
		list.setMaximumNumberOfVisibleRows(3);
		list.getSelectionManager().setBrowsable(false);
		list.getSelectionManager().setMaximumNumberSelected(0);
		
		return(new MiScrolledBox(list));
		}
					/**------------------------------------------------------
	 				 * Make the status bar. This is called from the super class's
					 * buildEditorWindow method. This application has no status
					 * bar so this returns null.
					 * @return		the status bar
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultStatusBar()
		{
		return(null);
		}
					/**------------------------------------------------------
	 				 * This method is called to create the graphics for this
					 * MiWindow when this window is first created. This 
					 * approach to delaying the creation of graphics until
					 * after the window is created is optional. This method
					 * populates the list in the main panel with text strings
					 * in each font and each style.
					 * @overrides		MiEditorWindow#createGraphicsContents
					 *------------------------------------------------------*/
	public	 	void		createGraphicsContents()
		{
		boundsRenderer = new MiBoundsRenderer(false);
		String[] fonts = MiFontManager.getFontList();

		MiFont font;

		int size = 12;
		for (int i = 0; i < fonts.length; ++i)
			{
			MiVisibleContainer container = new MiVisibleContainer();
			container.setBorderLook(MiAttributes.Mi_RAISED_BORDER_LOOK);
			container.setElementHJustification(Mi_LEFT_JUSTIFIED);
			MiColumnLayout layout = new MiColumnLayout();
			layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
			container.setLayout(layout);
			list.appendItem(container);

			MiFontDisplay fontDisplay = appendSampleText(container, fonts[i]);

			MiRowLayout rowLayout = new MiRowLayout();
			rowLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
			MiPushButton minusPB = new MiPushButton("-");
			minusPB.appendCommandHandler(this, Mi_DECREASE_POINT_SIZE_COMMAND_NAME + ":" + i);
			rowLayout.appendPart(minusPB);

			MiLabel label = new MiLabel(size + " point " + fonts[i]);
			fontDisplay.label = label;
			label.setBorderLook(MiAttributes.Mi_RAISED_BORDER_LOOK);
			rowLayout.appendPart(label);

			MiPushButton plusPB = new MiPushButton("+");
			plusPB.appendCommandHandler(this, Mi_INCREASE_POINT_SIZE_COMMAND_NAME + ":" + i);
			rowLayout.appendPart(plusPB);

			container.appendPart(rowLayout);
			}
		list.invalidateLayout();
		}
					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are:
					 * <pre>
					 *    Mi_INCREASE_POINT_SIZE_COMMAND_NAME
					 *    Mi_DECREASE_POINT_SIZE_COMMAND_NAME
					 *    Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 * @overrides		MiEditorWindow#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		if (command.startsWith(Mi_INCREASE_POINT_SIZE_COMMAND_NAME))
			{
			int index = Utility.toInteger(command.substring(command.indexOf(':') + 1));
			changeDisplayedFontSize(index, 1);
			}
		else if (command.startsWith(Mi_DECREASE_POINT_SIZE_COMMAND_NAME))
			{
			int index = Utility.toInteger(command.substring(command.indexOf(':') + 1));
			changeDisplayedFontSize(index, -1);
			}
		else if (command.startsWith(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME))
			{
			MiToolkit.postAboutDialog(
				this, title, MiSystem.getCompanyLogo(), Mi_FONT_MANAGER_ABOUT_MSG, false);
			}
		else
			{
			super.processCommand(command);
			}
		}
					/**------------------------------------------------------
	 				 * Change the font at teh given list index by the given
					 * amount.
					 * @param index		which font to change
					 * @param amount	the change in point size
					 *------------------------------------------------------*/
	protected	void		changeDisplayedFontSize(int index, int amount)
		{
		MiFontDisplay display = (MiFontDisplay )fontDisplays.elementAt(index);
		if ((display.pointSize + amount < MAX_POINT_SIZE) 
			&& (display.pointSize + amount > MIN_POINT_SIZE))
			{
			int pointSize = ++display.pointSize;
			for (int i = 0; i < display.sampleTexts.size(); ++i)
				{
				display.sampleTexts.elementAt(i).setFontPointSize(pointSize);
				}
			display.label.setLabel(pointSize + " point " + display.fontName);
			}
		}

					/**------------------------------------------------------
	 				 * Appends a sample text string, one for each font style,
					 * using the font with the given 'fontName' to the given
					 * container.
					 * @param container	the container of the new text strings
					 * @param fontName	the font of the text strings
					 * @return		A descriptor of the font and the
					 *			text strings we will use to keep
					 *			track of the font display.
					 *------------------------------------------------------*/
	protected	MiFontDisplay	appendSampleText(MiPart container, String fontName)
		{
		MiFontDisplay fontDisplay = new MiFontDisplay();
		fontDisplays.addElement(fontDisplay);

		fontDisplay.fontName = fontName;
		fontDisplay.pointSize = DEFAULT_POINT_SIZE;
			
		for (int i = 0; i < fontStyles.length; ++i)
			{
			MiFont font = MiFontManager.findFont(fontName, fontStyles[i], DEFAULT_POINT_SIZE);
			MiText text = new MiText(sampleString);
			text.setFont(font);
			text.setAfterRenderer(boundsRenderer);
			container.appendPart(text);
			fontDisplay.sampleTexts.addElement(text);
			}
		return(fontDisplay);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Appletcation Suite
 * <p>
 * This class describes a single font and it's associated graphics:
 *
 *----------------------------------------------------------------------------------------------*/
class MiFontDisplay
	{
	// ---------------------------------------------------------------
	// label	the graphics label that displays the font name and size
	// pointSize	the current point size of the font
	// fontName	the name of the font
	// sampleTexts	the (4) text strings that display the font in it's
	//		various styles (plain, bold, italic, bold-italc).
	// ---------------------------------------------------------------
	MiLabel		label;
	int		pointSize;
	String		fontName;
	MiParts		sampleTexts	= new MiParts();
	
					MiFontDisplay()
		{
		}
	}

