
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
import java.awt.Container;
import java.applet.Applet;

/**----------------------------------------------------------------------------------------------
 * This class implements a graphics editor using the Mica Graphics 
 * Framework. This includes the display and editing of graphics 
 * drawing primitives, widgets and node-line graphs. The graphics
 * is loaded and saved to files in the mica graphics format.
 * <p>
 * This window has a graphics editor specific menubar, toolbar and
 * an optional palette. The palette is a list of graphics that can
 * be dragged and dropped into the editor. It's contents is loaded 
 * from the Mica graphics files in the location specified by 
 * the Mi_GRAPHICS_PALETTES_HOME property (which by default is 
 * equal to ${Mi_CURRENT_DIRECTORY}/palettes). The palette can be 
 * modified by drag and droping graphics from the graphics editor 
 * into the palette.
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiNetwork extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiGraphicsWindow. Supported command line parameters are:
					 * -file		load this file on startup
					 * -palette		load this palette on startup
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String filename = Utility.getCommandLineArgument(args, "-file");
		String title = Utility.getCommandLineArgument(args, "-title");
		if (title == null)
			title = "MiNetwork";
		String palette = Utility.getCommandLineArgument(args, "-palette");
		MiSplashScreen splash = new MiSplashScreen(
			new MiImage(MiSystem.getProperty(MiSystem.Mi_HOME) + "/apps/MiNetworkSplash.gif"));
		MiGraphicsWindow window = new MiGraphicsWindow(title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		splash.setParent(window);
		window.setCapabilities(
					true,		// hasGraphsMenu
					true,		// hasLayoutsMenu
					false,		// hasDrawingToolbar
					false,		// hasShapeAttributesDialog
					false,		// hasRulers
					false,		// hasDrawingPages
					false,		// hasDrawingPagesDialogBox
					false,		// hasDrawingGrid
					false,		// hasLayers
					true,		// hasBirdsEyeView
					true,		// hasPalette
					false,		// hasConnectionToolBar
					false,		// hasVisibilityToolBar
					false,		// canHaveVisibleConnectionPoints
					false,		// canHaveVisibleAnnotationPoints
					false,		// hasConnectionPointsDialog
					false 		// hasAnnotationPointsDialog
					);
		if (window.getPalette() instanceof MiGraphicsPalette)
			{
			((MiGraphicsPalette )window.getPalette()).setGraphicsPalettesDirectory(
				"${Mi_HOME}/apps/palettes");
			}
		window.openDefaultFile();
		window.setVisible(true);
		loadSpecifiedGraphicsAndPalette(window, filename, palette);
		splash.dispose();
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiGraphicsWindow. Supported html file parameters are:
					 * file		load this file on startup
					 * palette	load this palette on startup
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);

		String filename = getParameter("file");
		String palette = getParameter("palette");
		String title = getParameter("title");
		if (title == null)
			title = "MiNetwork";
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiGraphicsWindow window = new MiGraphicsWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height),
			MiiTypes.Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);
		window.setCapabilities(
					true,		// hasGraphsMenu
					true,		// hasLayoutsMenu
					false,		// hasDrawingToolbar
					false,		// hasShapeAttributesDialog
					false,		// hasRulers
					false,		// hasDrawingPages
					false,		// hasDrawingPagesDialogBox
					false,		// hasDrawingGrid
					false,		// hasLayers
					true,		// hasBirdsEyeView
					true,		// hasPalette
					false,		// hasConnectionToolBar
					false,		// hasVisibilityToolBar
					false,		// canHaveVisibleConnectionPoints
					false,		// canHaveVisibleAnnotationPoints
					false,		// hasConnectionPointsDialog
					false 		// hasAnnotationPointsDialog
					);
		if (window.getPalette() instanceof MiGraphicsPalette)
			{
			((MiGraphicsPalette )window.getPalette()).setGraphicsPalettesDirectory(
				"${Mi_HOME}/apps/palettes");
			}
		window.openDefaultFile();
		window.setVisible(true);
		loadSpecifiedGraphicsAndPalette(window, filename, palette);
		}
					/**------------------------------------------------------
	 				 * Loads the specified graphics file and palette, if any.
					 * @param window	the window
					 * @param filename	the name of the file that contains
					 *			graphics or null
					 * @param palette	the name of the file that contains
					 *			palette graphics or null
					 *------------------------------------------------------*/
	protected static void		loadSpecifiedGraphicsAndPalette(
						MiGraphicsWindow window, String filename, String palette)
		{
		if ((filename != null) || (palette != null))
			{
			try	{
				// ---------------------------------------------------------------
				// Get a lock for the window from this, the 'main' starter thread
				// ---------------------------------------------------------------
				window.getAccessLock();
				if (filename != null)
					window.openFile(filename);
				if (palette != null)
					window.openPalette(palette);
				window.freeAccessLock();
				}
			catch (Exception e)
				{
				e.printStackTrace();
				System.out.println(
					"Usage: MiNetwork [-file <filename>] [-title <window title>] [-palette <palette name>");
				System.exit(1);
				}
			}
		}
	}
