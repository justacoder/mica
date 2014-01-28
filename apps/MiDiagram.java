
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
import com.swfm.mica.util.Tree;

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
public class MiDiagram extends Applet implements MiiEventTypes, MiiCommandNames
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
			title = "MiDiagram";
		String palette = Utility.getCommandLineArgument(args, "-palette");
		MiGraphicsWindow window = new MiGraphicsWindow(title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		MiSplashScreen splash = new MiSplashScreen(window,
			new MiImage(MiSystem.getProperty(MiSystem.Mi_HOME) + "/apps/MiDiagramSplash.gif"));
		window.setCapabilities(
					true,		// hasGraphsMenu
					true,		// hasLayoutsMenu
					true,		// hasDrawingToolbar
					true,		// hasShapeAttributesDialog
					true,		// hasRulers
					true,		// hasDrawingPages
					true,		// hasDrawingPagesDialogBox
					true,		// hasDrawingGrid
					true,		// hasLayers
					true,		// hasBirdsEyeView
					true,		// hasPalette
					true,		// hasConnectionToolBar
					true,		// hasVisibilityToolBar
					true,		// canHaveVisibleConnectionPoints
					true,		// canHaveVisibleAnnotationPoints
					true,		// hasConnectionPointsDialog
					true 		// hasAnnotationPointsDialog
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
			title = "MiDiagram";
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiGraphicsWindow window = new MiGraphicsWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height),
			MiiTypes.Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);

		window.setCapabilities(
					false,		// hasGraphsMenu
					true,		// hasLayoutsMenu
					true,		// hasDrawingToolbar
					true,		// hasShapeAttributesDialog
					true,		// hasRulers
					true,		// hasDrawingPages
					true,		// hasDrawingPagesDialogBox
					true,		// hasDrawingGrid
					true,		// hasLayers
					true,		// hasBirdsEyeView
					true,		// hasPalette
					true,		// hasConnectionToolBar
					true,		// hasVisibilityToolBar
					true,		// canHaveVisibleConnectionPoints
					true,		// canHaveVisibleAnnotationPoints
					true,		// hasConnectionPointsDialog
					true 		// hasAnnotationPointsDialog
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
				MiPaletteViewManager.setDefaultPostGraphicsCreationHandler(new MiDiagramGraphicsTouchUp());
				if (filename != null)
					window.openFile(filename);
				if (palette != null)
					{
					window.openPalette(palette);
					}
				// Make handles 'red'
				MiBoundsManipulator.globalAttributes = MiBoundsManipulator.globalAttributes.setBackgroundColor("red");
				MiMultiPointManipulator.globalAttributes = MiMultiPointManipulator.globalAttributes.setBackgroundColor("red");

				// Assign handles to each point of selected connections
				MiManipulatorSelectionGraphics handlesSelectionGraphics = new MiManipulatorSelectionGraphics();

				((MiICreateConnection )window.getEditor()
					.getEventHandlerWithClass("com.swfm.mica.MiICreateConnection"))
						.getPrototype().setSelectionGraphics(handlesSelectionGraphics);
				((MiICreateConnectionUsingConnPoint )MiConnectionPointManager.getGlobalLook()
					.getEventHandlerWithClass("com.swfm.mica.MiICreateConnectionUsingConnPoint"))
						.getPrototype().setSelectionGraphics(handlesSelectionGraphics);
				window.getConnectionToolbar().getPrototypes().setSelectionGraphics(handlesSelectionGraphics);


				// Assign L connection layout as default to connections
				((MiICreateConnection )window.getEditor()
					.getEventHandlerWithClass("com.swfm.mica.MiICreateConnection"))
						.getPrototype().setLayout(new MiLConnectionLayout());
				((MiICreateConnectionUsingConnPoint )MiConnectionPointManager.getGlobalLook()
					.getEventHandlerWithClass("com.swfm.mica.MiICreateConnectionUsingConnPoint"))
						.getPrototype().setLayout(new MiLConnectionLayout());
				window.getConnectionToolbar().getPrototypes().setLayout(new MiLConnectionLayout());


				// Ctrl-middle btn drag creates connections starting at another connection
				window.getConnectionToolbar().setConnectableToConnections(true);

				// Connection Toolbar just used to change layouts, it cannot instigate creation of new conns
				window.getConnectionToolbar().setActingAsAttributeToolNotDrawingTool(true);

				// Make it easier to select graphics with mouse by making effective mouse pointer larger
				window.getEditor().setMinimumPickAreaSize(5);

				// Drag connection segments...
				window.getEditor().appendEventHandler(new MiIDragConnectionSegment());

				// Ctrl-middle btn drag now pans editor
				MiEventHandler dragBackgroundPan 
					= (MiEventHandler )window.getEditor().getEventHandlerWithClass("com.swfm.mica.MiIDragBackgroundPan");
				dragBackgroundPan.addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, 
					Mi_MIDDLE_MOUSE_START_DRAG_EVENT, 
					0, 
					0);
				dragBackgroundPan.addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, 
					Mi_MIDDLE_MOUSE_DRAG_EVENT, 
					0, 
					0);
				dragBackgroundPan.addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
					Mi_MIDDLE_MOUSE_UP_EVENT, 
					0, 
					0);

				// ---------------------------------------------------------------
				// We do not want the tool hint to stay up forever while the user is trying to do some work
				// ---------------------------------------------------------------
				((MiIDisplayToolHints )window.getEditor().getRootWindow().getEventHandlerWithClass(
					"com.swfm.mica.MiIDisplayToolHints")).setHideToolHintWhenMousePaused(true);


				window.freeAccessLock();
				}
			catch (Exception e)
				{
				e.printStackTrace();
				System.out.println(
					"Usage: MiDiagram [-file <filename>] [-title <window title>] [-palette <palette name>");
				System.exit(1);
				}
			}
		}
	}
class MiDiagramGraphicsTouchUp extends MiViewManagerPostGraphicsCreationHandler
	{
	MiiSelectionGraphics 		handlesSelectionGraphics;
	MiICreateText 			pureGraphicsTextEditor;


	public				MiDiagramGraphicsTouchUp()
		{
		// Basic shapes can have text added to center by typing text when shape is selected
		pureGraphicsTextEditor = new MiICreateText();
		MiText pureGraphicsTextEditorPrototype = new MiText();
		pureGraphicsTextEditorPrototype.setNumDisplayedRows(100);
		pureGraphicsTextEditorPrototype.setSelectEntireTextAsPartInEditor(true);
		pureGraphicsTextEditorPrototype.setMustDoubleClickToEdit(true);
		//pureGraphicsTextEditorPrototype.setSelectionGraphics(MiiSelectionGraphics.ignoreSelectionGraphics);
		pureGraphicsTextEditorPrototype.appendActionHandler(
			new MiAutoAdjustShapeAroundTextActionHandler(), MiiActionTypes.Mi_TEXT_CHANGE_ACTION);
		pureGraphicsTextEditor.setPrototypeShape(pureGraphicsTextEditorPrototype);
		pureGraphicsTextEditor.setIsSingleton(true);

		// We want shapes to have handles when selected, where as nodes have highlight boxes
		handlesSelectionGraphics = new MiManipulatorSelectionGraphics();
		}

	public  	void		created(MiParts parts, MiContainer paletteParts)
		{
		for (int i = 0; i < paletteParts.getNumberOfParts(); ++i)
			{
			MiPart part = paletteParts.getPart(i);
			part.setSnappable(false);
			if (part.getNumberOfParts() == 0)
				{
				part.setSelectionGraphics(handlesSelectionGraphics);
				part.appendEventHandler(pureGraphicsTextEditor);
				}
			else if (part instanceof MiContainer)
				{
				created(null, (MiContainer )part);
				}
			}
		}
	}
