
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

package com.swfm.mica;
import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import java.io.IOException; 
import java.io.BufferedInputStream; 
import java.io.OutputStream; 
import java.awt.Frame;
import java.awt.Color;

/**----------------------------------------------------------------------------------------------
 * This class implements a graphics editor using the Mica Graphics 
 * Framework. This includes the display and editing of graphics 
 * drawing primitives, widgets and node-line graphs. The graphics
 * is loaded and saved to files in the mica graphics format.
 * <p>
 * This window has a graphics editor specific menubar, toolbar and
 * an optional palette. The palette is a list of graphics that can
 * be dragged into the editor. It's contents are loaded from the
 * mica graphics files in the location specified by the property
 * Mi_GRAPHICS_PALETTES_HOME (which by default is equal to 
 * ${Mi_CURRENT_DIRECTORY}/palettes).
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphicsWindow extends MiFileEditorWindow implements MiiDisplayNames
	{
	// ---------------------------------------------------------------
	// Define the names of some commands
	// ---------------------------------------------------------------
	public static final String	Mi_HIDE_PALETTE_COMMAND_NAME		= "Hide palette";
	public static final String	Mi_HIDE_PALETTE_LABELS_COMMAND_NAME	= "Hide palette labels";
	private static final String	Mi_SHAPE_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME	
										= "shapeAttDialogChanged";

	//private static final String	Mi_POPUP_SHAPE_ATTRIBUTES_CMD_NAME	= "popupShapeAtts";
	private static final String	Mi_POPUP_FILL_ATTRIBUTES_CMD_NAME	= "popupFillAtts";
	private static final String	Mi_POPUP_LINE_ATTRIBUTES_CMD_NAME	= "popupLineAtts";
	private static final String	Mi_POPUP_LINE_ENDS_ATTRIBUTES_CMD_NAME	= "popupLineEndsAtts";
	private static final String	Mi_POPUP_SHADOW_ATTRIBUTES_CMD_NAME	= "popupShadowAtts";
	private static final String	Mi_POPUP_BORDER_LOOK_ATTRIBUTES_CMD_NAME= "popupBorderLookAtts";
	private static final String	Mi_POPUP_TEXT_ATTRIBUTES_CMD_NAME	= "popupTextAtts";
	private static final String	Mi_HIDE_CONNECTION_POINTS_MENUITEM_CMD_NAME= "hideConnPts";
	private static final String	Mi_HIDE_ANNOTATION_POINTS_MENUITEM_CMD_NAME= "hideAnnoPts";

	// ---------------------------------------------------------------
	// Define the names of some properties
	// ---------------------------------------------------------------
	private static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	
						= "Mi_DEFAULT_WINDOW_BORDER_TITLE";
	public static final String	Mi_PALETTE_SAVE_NAME
						= "Mi_PALETTE_SAVE_NAME";
	public static final String	Mi_PALETTE_SAVE_AS_NAME
						= "Mi_PALETTE_SAVE_AS_NAME";
	public static final String	Mi_SHOW_PALETTE_NAME
						= "Mi_SHOW_PALETTE_NAME";
	public static final String	Mi_SHOW_PALETTE_LABELS_NAME
						= "Mi_SHOW_PALETTE_LABELS_NAME";
	public static final String	Mi_MICA_GRAPHICS_FILENAME_EXTENSION
						= "Mi_MICA_GRAPHICS_FILENAME_EXTENSION";
	public static final String	Mi_MICA_DEFAULT_GRAPHICS_FILENAME
						= "Mi_MICA_DEFAULT_GRAPHICS_FILENAME";
	public static final String	Mi_GRAPHICS_WINDOW_ABOUT_MSG
						= "Mi_GRAPHICS_WINDOW_ABOUT_MSG";
	private static final String	Mi_BIRDS_EYE_VIEW_BORDER_TITLE
						= "Mi_BIRDS_EYE_VIEW_BORDER_TITLE";
	private static final String	Mi_DISPLAY_ANOTHER_VIEW_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_ANOTHER_VIEW_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_ANOTHER_VIEW_BORDER_TITLE
						= "Mi_ANOTHER_VIEW_BORDER_TITLE";
	private static final String	Mi_DISPLAY_FISH_EYE_VIEW_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_FISH_EYE_VIEW_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_FISH_EYE_VIEW_BORDER_TITLE
						= "Mi_FISH_EYE_VIEW_BORDER_TITLE";
	private static final String	Mi_DISPLAY_MAGNIFIER_VIEW_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_MAGNIFIER_VIEW_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_MAGNIFIER_VIEW_BORDER_TITLE
						= "Mi_MAGNIFIER_VIEW_BORDER_TITLE";
	private static final String	Mi_DISPLAY_MAGNIFIER_LENS_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_MAGNIFIER_LENS_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_MAGNIFIER_LENS_BORDER_TITLE
						= "Mi_MAGNIFIER_LENS_BORDER_TITLE";
	private static final String	Mi_DISPLAY_GRID_DIALOG_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_GRID_DIALOG_MENUITEM_DISPLAY_NAME";

	private static final String	Mi_DISPLAY_PAGE_DIALOG_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_PAGE_DIALOG_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_DISPLAY_RULERS_DIALOG_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_RULERS_DIALOG_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_DISPLAY_CONNECTION_POINTS_DIALOG_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_CONNECTION_POINTS_DIALOG_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_DISPLAY_CONNECTION_POINTS_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_CONNECTION_POINTS_MENUITEM_DISPLAY_NAME";
	private static final String	Mi_DISPLAY_ANNOTATION_POINTS_MENUITEM_DISPLAY_NAME
						= "Mi_DISPLAY_ANNOTATION_POINTS_MENUITEM_DISPLAY_NAME";
	private static final String 	Mi_DISPLAY_PALETTE_STATUS_HELP_MSG
						= "Mi_DISPLAY_PALETTE_STATUS_HELP_MSG";
	private static final String	Mi_NO_DISPLAY_PALETTE_STATUS_HELP_MSG
						= "Mi_NO_DISPLAY_PALETTE_STATUS_HELP_MSG";
	private static final String	Mi_DISPLAY_PALETTE_BALLOON_HELP_MSG
						= "Mi_DISPLAY_PALETTE_BALLOON_HELP_MSG";
	private static final String	Mi_DISPLAY_PALETTE_LABELS_STATUS_HELP_MSG
						= "Mi_DISPLAY_PALETTE_LABELS_STATUS_HELP_MSG";
	private static final String	Mi_NO_DISPLAY_PALETTE_LABELS_STATUS_HELP_MSG
						= "Mi_NO_DISPLAY_PALETTE_LABELS_STATUS_HELP_MSG";
	private static final String	Mi_DISPLAY_PALETTE_LABELS_BALLOON_HELP_MSG
						= "Mi_DISPLAY_PALETTE_LABELS_BALLOON_HELP_MSG";
	private static final String	Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_STATUS_HELP_MSG
						= "Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_STATUS_HELP_MSG";
	private static final String	Mi_NO_DISPLAY_ANNOTATION_POINTS_DIALOG_STATUS_HELP_MSG
						= "Mi_NO_DISPLAY_ANNOTATION_POINTS_DIALOG_STATUS_HELP_MSG";
	private static final String	Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_BALLOON_HELP_MSG
						= "Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_BALLOON_HELP_MSG";
	private static final String	Mi_DISPLAY_ANNOTATION_POINTS_STATUS_HELP_MSG
						= "Mi_DISPLAY_ANNOTATION_POINTS_STATUS_HELP_MSG";
	private static final String	Mi_NO_DISPLAY_ANNOTATION_POINTS_STATUS_HELP_MSG
						= "Mi_NO_DISPLAY_ANNOTATION_POINTS_STATUS_HELP_MSG";
	private static final String	Mi_DISPLAY_ANNOTATION_POINTS_BALLOON_HELP_MSG
						= "Mi_DISPLAY_ANNOTATION_POINTS_BALLOON_HELP_MSG";
	private static final String	Mi_DISPLAY_CONNECTION_POINTS_DIALOG_STATUS_HELP_MSG
						= "Mi_DISPLAY_CONNECTION_POINTS_DIALOG_STATUS_HELP_MSG";
	private static final String	Mi_NO_DISPLAY_CONNECTION_POINTS_DIALOG_STATUS_HELP_MSG
						= "Mi_NO_DISPLAY_CONNECTION_POINTS_DIALOG_STATUS_HELP_MSG";
	private static final String	Mi_DISPLAY_CONNECTION_POINTS_DIALOG_BALLOON_HELP_MSG
						= "Mi_DISPLAY_CONNECTION_POINTS_DIALOG_BALLOON_HELP_MSG";
	private static final String	Mi_DISPLAY_CONNECTION_POINTS_STATUS_HELP_MSG
						= "Mi_DISPLAY_CONNECTION_POINTS_STATUS_HELP_MSG";
	private static final String	Mi_NO_DISPLAY_CONNECTION_POINTS_STATUS_HELP_MSG
						= "Mi_NO_DISPLAY_CONNECTION_POINTS_STATUS_HELP_MSG";
	private static final String	Mi_DISPLAY_CONNECTION_POINTS_BALLOON_HELP_MSG
						= "Mi_DISPLAY_CONNECTION_POINTS_BALLOON_HELP_MSG";
	private static final String	Mi_DISPLAY_PAGE_DIALOG_STATUS_HELP_MSG
						= "Mi_DISPLAY_PAGE_DIALOG_STATUS_HELP_MSG";
	private static final String	Mi_NO_DISPLAY_PAGE_DIALOG_STATUS_HELP_MSG
						= "Mi_NO_DISPLAY_PAGE_DIALOG_STATUS_HELP_MSG";
	private static final String	Mi_DISPLAY_PAGE_DIALOG_BALLOON_HELP_MSG
						= "Mi_DISPLAY_PAGE_DIALOG_BALLOON_HELP_MSG";
	private static final String	Mi_DISPLAY_RULERS_DIALOG_STATUS_HELP_MSG
						= "Mi_DISPLAY_RULERS_DIALOG_STATUS_HELP_MSG";
	private static final String	Mi_NO_DISPLAY_RULERS_DIALOG_STATUS_HELP_MSG
						= "Mi_NO_DISPLAY_RULERS_DIALOG_STATUS_HELP_MSG";
	private static final String	Mi_DISPLAY_RULERS_DIALOG_BALLOON_HELP_MSG
						= "Mi_DISPLAY_RULERS_DIALOG_BALLOON_HELP_MSG";

	private static final String	Mi_SHAPE_FILL_ATTRIBUTE_MENU_NAME
						= "Mi_SHAPE_FILL_ATTRIBUTE_MENU_NAME";
	private static final String	Mi_SHAPE_LINE_ATTRIBUTE_MENU_NAME
						= "Mi_SHAPE_LINE_ATTRIBUTE_MENU_NAME";
	private static final String	Mi_SHAPE_LINE_ENDS_ATTRIBUTE_MENU_NAME
						= "Mi_SHAPE_LINE_ENDS_ATTRIBUTE_MENU_NAME";
	private static final String	Mi_SHAPE_SHADOW_ATTRIBUTE_MENU_NAME
						= "Mi_SHAPE_SHADOW_ATTRIBUTE_MENU_NAME";
	private static final String	Mi_SHAPE_BORDER_LOOK_ATTRIBUTE_MENU_NAME
						= "Mi_SHAPE_BORDER_LOOK_ATTRIBUTE_MENU_NAME";
	private static final String	Mi_SHAPE_TEXT_ATTRIBUTE_MENU_NAME
						= "Mi_SHAPE_TEXT_ATTRIBUTE_MENU_NAME";


	// ---------------------------------------------------------------
	// Define the default values of some properties
	// ---------------------------------------------------------------
	private static final Pair[] properties = 
		{
		new Pair( Mi_DEFAULT_WINDOW_BORDER_TITLE		, "Diagram Editor"),
		new Pair( Mi_PALETTE_SAVE_NAME				, "Save palette"),
		new Pair( Mi_PALETTE_SAVE_AS_NAME			, "Save palette as..."),
		new Pair( Mi_SHOW_PALETTE_NAME				, "palette"),
		new Pair( Mi_SHOW_PALETTE_LABELS_NAME			, "palette labels"),
		new Pair( Mi_MICA_GRAPHICS_FILENAME_EXTENSION		, "grml"),
		new Pair( Mi_MICA_DEFAULT_GRAPHICS_FILENAME		, "Unnamed.grml"),
		new Pair( Mi_BIRDS_EYE_VIEW_BORDER_TITLE		, "Birds-eye View"),
		new Pair( Mi_DISPLAY_ANOTHER_VIEW_MENUITEM_DISPLAY_NAME	, "Another View"),
		new Pair( Mi_ANOTHER_VIEW_BORDER_TITLE			, "Another View"),
		new Pair( Mi_DISPLAY_FISH_EYE_VIEW_MENUITEM_DISPLAY_NAME, "Fish-eye View"),
		new Pair( Mi_FISH_EYE_VIEW_BORDER_TITLE			, "Fish-eye View"),
		new Pair( Mi_DISPLAY_MAGNIFIER_VIEW_MENUITEM_DISPLAY_NAME, "Magnifier View"),
		new Pair( Mi_MAGNIFIER_VIEW_BORDER_TITLE		, "Magnifier View"),
		new Pair( Mi_DISPLAY_MAGNIFIER_LENS_MENUITEM_DISPLAY_NAME, "Magnifier Lens"),
		new Pair( Mi_MAGNIFIER_LENS_BORDER_TITLE		, "Magnifier Lens"),
		new Pair( Mi_DISPLAY_GRID_DIALOG_MENUITEM_DISPLAY_NAME	, "Grids \\& Guides... "),
		new Pair( Mi_DISPLAY_PAGE_DIALOG_MENUITEM_DISPLAY_NAME	, "Page..."),
		new Pair( Mi_DISPLAY_RULERS_DIALOG_MENUITEM_DISPLAY_NAME, "Rulers..."),
		new Pair( Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_MENUITEM_DISPLAY_NAME, "Annotation Points..."),
		new Pair( Mi_DISPLAY_CONNECTION_POINTS_DIALOG_MENUITEM_DISPLAY_NAME, "Connection Points..."),
		new Pair( Mi_DISPLAY_ANNOTATION_POINTS_MENUITEM_DISPLAY_NAME, "Annotation Points"),
		new Pair( Mi_DISPLAY_CONNECTION_POINTS_MENUITEM_DISPLAY_NAME, "Connection Points"),
		new Pair( Mi_SHAPE_FILL_ATTRIBUTE_MENU_NAME		, "Fill Style..."),
		new Pair( Mi_SHAPE_LINE_ATTRIBUTE_MENU_NAME		, "Line Style..."),
		new Pair( Mi_SHAPE_LINE_ENDS_ATTRIBUTE_MENU_NAME	, "Line Ends Style..."),
		new Pair( Mi_SHAPE_TEXT_ATTRIBUTE_MENU_NAME		, "Text Style..."),
		new Pair( Mi_SHAPE_SHADOW_ATTRIBUTE_MENU_NAME		, "Shadow Style..."),
		new Pair( Mi_SHAPE_BORDER_LOOK_ATTRIBUTE_MENU_NAME	, "Border Style..."),
		new Pair( Mi_DISPLAY_PALETTE_STATUS_HELP_MSG		, "Shows/hides the palette"),
		new Pair( Mi_NO_DISPLAY_PALETTE_STATUS_HELP_MSG		, "No palette to hide"),
		new Pair( Mi_DISPLAY_PALETTE_LABELS_STATUS_HELP_MSG	, "Shows/hides the labels of things in the palette"),
		new Pair( Mi_NO_DISPLAY_PALETTE_LABELS_STATUS_HELP_MSG	, "Shows/hides the labels of things in the palette"),
		new Pair( Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_STATUS_HELP_MSG	, "Shows/hides annotation points properties dialog box"),
		new Pair( Mi_NO_DISPLAY_ANNOTATION_POINTS_DIALOG_STATUS_HELP_MSG, "Shows/hides annotation points properties dialog box"),
		new Pair( Mi_DISPLAY_ANNOTATION_POINTS_STATUS_HELP_MSG		, "Shows/hides annotation points"),
		new Pair( Mi_NO_DISPLAY_ANNOTATION_POINTS_STATUS_HELP_MSG	, "Shows/hides annotation points"),
		new Pair( Mi_DISPLAY_CONNECTION_POINTS_DIALOG_STATUS_HELP_MSG	, "Shows/hides connection points properties dialog box"),
		new Pair( Mi_NO_DISPLAY_CONNECTION_POINTS_DIALOG_STATUS_HELP_MSG, "Shows/hides connection points properties dialog box"),
		new Pair( Mi_DISPLAY_CONNECTION_POINTS_STATUS_HELP_MSG		, "Shows/hides connection points"),
		new Pair( Mi_NO_DISPLAY_CONNECTION_POINTS_STATUS_HELP_MSG	, "Shows/hides connection points"),
		new Pair( Mi_DISPLAY_PAGE_DIALOG_STATUS_HELP_MSG	, "Shows/hides page properties (printing and background) dialog box"),
		new Pair( Mi_NO_DISPLAY_PAGE_DIALOG_STATUS_HELP_MSG	, "Shows/hides page properties (printing and background) dialog box"),
		new Pair( Mi_DISPLAY_RULERS_DIALOG_STATUS_HELP_MSG	, "Shows/hides ruler properties dialog box"),
		new Pair( Mi_NO_DISPLAY_RULERS_DIALOG_STATUS_HELP_MSG	, "Shows/hides ruler properties dialog box"),

		new Pair( Mi_GRAPHICS_WINDOW_ABOUT_MSG	, 
			"\n\nThis window contains a specialized network graph editor.\n"
			+ "The graphics of networks and palettes can be saved and loaded\n"
			+ "from disk.\n\n\n"
			+ "Version 1.01      May 10, 1999")
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
	private		boolean			printFormatIsPostScript	= true;
	private		boolean			keepShapesWithinPageBounds;
	private		MiGraphicsEditor	editor;
	private		MiEditorPalette		palette;
	private		MiShapeAttributesDialog	shapeAttributesDialog;
	private		MiDrawingAttributesToolBar	drawingAttributesToolBar;
	private		MiDrawingShapeToolBar	drawingShapeToolBar;
	private		MiRulerBox		rulerBox;
	private		MiScrolledBox		scrolledBox;
	private		MiLayerTabs		layerTabs;
	private		MiNativeWindow		birdsEyeViewWindow;
	private		MiBirdsEyeView		birdsEyeView;
	private		MiNativeWindow		anotherViewWindow;
	private		MiAnotherView		anotherView;
	private		MiNativeWindow		fishEyeViewWindow;
	private		MiFishEyeView		fishEyeView;
	private		MiNativeWindow		magnifierViewWindow;
	private		MiMagnifierView		magnifierView;
	private		MiInternalWindow	magnifierLensWindow;
	private		MiMagnifierLens		magnifierLens;
	private		int			numberOfFormatMenuItems;
	private		MiHelpWindow 		helpWindow;
	private		MiPageManager			pageManager;
	private		MiModelPropertyViewManager	unitsDialogBox;
	private		MiModelPropertyViewManager	pageDialogBox;
	private		MiModelPropertyViewManager	rulersDialogBox;
	private		MiManagedPointsAttributesDialog	managedPointsAttributesDialogBox;
	private		MiDrawingToolBarManager		drawingToolBarManager;
	private		MiDrawingConnectionToolBar	connectionToolBar;
	private		MiDrawingVisibilityToolBar	visibilityToolBar;
	private		MiWidget			connPointVisibilityToggleButton;
	private		MiWidget			annoPointVisibilityToggleButton;
	private		MiiController			defaultController;



					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsWindow, creates a new AWT Frame,
					 * and adds this MiGraphicsWindow, with the given title and
					 * size, to the AWT Frame.
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiGraphicsWindow(String title, MiBounds screenSize)
		{
		super(	title != null ? title : Mi_DEFAULT_WINDOW_BORDER_TITLE, 
			screenSize,
			Mi_MICA_GRAPHICS_FILENAME_EXTENSION,
			Mi_MICA_DEFAULT_GRAPHICS_FILENAME,
			false);
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsWindow. The caller must then
					 * add this to their Swing or AWT container. For example,
					 * if nativeComponentType == Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE:
					 * <p>
					 * MiGraphicsWindow gw = new MiGraphicsWindow(
					 *	MiUtility.getFrame(jComponent), null,
					 *	new MiBounds(0,0,400,400),
					 *	Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE);
					 * jComponent.add("Center", gw.getSwingComponent());
					 * <p>
					 * or, if adding to a JFrame:
					 * <p>
					 * jFrame.getContentPane().add("Center", gw.getSwingComponent());
					 * <p>
					 * And if nativeComponentType == Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE:
					 * <p>
					 * MiGraphicsWindow gw = new MiGraphicsWindow(
					 *	MiUtility.getFrame(awtContainer), null, 
					 *	new MiBounds(0,0,400,400),
					 *	Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);
					 * awtContainer.add("Center", gw.getAWTComponent());
					 * <p>
					 * @param frame		the parent frame
					 * @param title		the window border text
					 * @param screenSize	the size of the window in pixels
					 * @param nativeComponentType	the type of the component to
					 *			use: Mi_AWT_1_0_2_HEAVYWEIGHT_COMPONENT_TYPE
					 *			or Mi_AWT_1_1_HEAVYWEIGHT_COMPONENT_TYPE
					 *			or Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE
					 *			or Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE
					 *			or null (to use the default)
					 * @see			MiSystem#getDefaultJDKAPIComponentType
					 *------------------------------------------------------*/
	public				MiGraphicsWindow(
						Frame frame, String title, MiBounds screenSize,
						MiJDKAPIComponentType nativeComponentType)
		{
		super(frame, 
			title != null ? title : Mi_DEFAULT_WINDOW_BORDER_TITLE, 
			screenSize,
			nativeComponentType,
			Mi_MICA_GRAPHICS_FILENAME_EXTENSION,
			Mi_MICA_DEFAULT_GRAPHICS_FILENAME,
			false);
		setup();
		}
	public				MiGraphicsWindow(
						Frame frame, String title, MiBounds screenSize)
		{
		this(frame, title, screenSize, MiSystem.getDefaultJDKAPIComponentType());
		}
					/**------------------------------------------------------
	 				 * Gets the manager of the current open document.
					 * @return		the document manager
					 *------------------------------------------------------*/
	public		MiiController	 getController()
		{
//MiDebug.println(this + " getController from editor: " +editor);
		return(editor.getOpenDocumentController());
		}
					/**------------------------------------------------------
	 				 * Sets the manager of the current open document.
					 * @param c		the document manager
					 *------------------------------------------------------*/
	public		void		 setController(MiiController c)
		{
		editor.setOpenDocumentController(c);
		}
					/**------------------------------------------------------
	 				 * Sets the default controller to use to load documents with.
					 * Note that this is copied before use because a document may
					 * specify it's own MiIOFormatManager and MiViewManagers. 
					 * @param controller	the default controller
					 *------------------------------------------------------*/
	public		void		 setDefaultController(MiiController controller)
		{
		defaultController = controller;
		editor.setPrototypeController(controller);
		}

					/**------------------------------------------------------
	 				 * Sets the editor that the menubar, statusbar and toolbar
					 * and (3rd button) background menu act on and report about.
					 * Needs work to support multiple simulatneous editors in 
					 * this one window. Note: this editor is not added to the
					 * MiEditorWindow by this method.
					 * @param editor 	the new editor (of type MiGraphicsEditor)
					 * @see			MiEditorWindow#getEditor
					 * @see			#getEditor
					 * @overrides		MiEditorWindow#getEditor
					 *------------------------------------------------------*/
	public		void		setEditor(MiEditor editor)
		{
		this.editor = (MiGraphicsEditor )editor;
		super.setEditor(editor);
		if (birdsEyeView != null)
			birdsEyeView.setViewTarget(editor);
		pageManager = editor.getPageManager();
		if (drawingToolBarManager != null)
			{
			drawingToolBarManager.setEditor(editor);
			}
		}
	public		void		setCurrentView(MiFileEditorWindowDocumentView view)
		{
		if (view != getCurrentView())
			{
			super.setCurrentView(view);

		// Set with copy of controller, in case there is an open file (the default file) 
		// that is now open and will be closed, which sets controller.viewManager = null, 
		// which means when this controller is next used, it has no viewManager
		// setController(view.getController().copy());

			setController(view.getController());
			}
		}
		
					/**------------------------------------------------------
	 				 * Sets the default controller to use to load palettes with.
					 * Note that this is copied before use because palette data may
					 * specify it's own MiIOFormatManager and MiViewManagers. 
					 * @param controller	the default controller
					 *------------------------------------------------------*/
	public		void		 setDefaultPaletteController(MiiController controller)
		{
		if (palette instanceof MiGraphicsPalette)
			((MiGraphicsPalette )palette).setDefaultController(controller);
		}

	public		MiPageManager	getPageManager()
		{
		return(pageManager);
		}

	public		void		setCapabilities(
						boolean hasGraphsMenu, 
						boolean hasLayoutsMenu, 
						boolean hasDrawingToolbar, 
						boolean hasShapeAttributesDialog,
						boolean hasRulers,
						boolean hasDrawingPages,
						boolean hasDrawingPagesDialogBox,
						boolean hasDrawingGrid,
						boolean	hasLayers,
						boolean hasBirdsEyeView,
						boolean hasPalette,
						boolean hasConnectionToolBar,
						boolean hasVisibilityToolBar,
						boolean canHaveVisibleConnectionPoints,
						boolean canHaveVisibleAnnotationPoints,
						boolean hasConnectionPointsDialog,
						boolean hasAnnotationPointsDialog)
		{
		palette = (MiEditorPalette )getPalette();

		createPageManager();

		// ---------------------------------------------------------------
		// Add the layout and graph layout menus to the menubar.
		// ---------------------------------------------------------------
		if (hasLayoutsMenu)
			getMenuBar().appendPulldownMenu(new MiLayoutMenu(this));
		if (hasGraphsMenu)
			getMenuBar().appendPulldownMenu(new MiGraphMenu(this));

		setHasDrawingToolbar(hasDrawingToolbar);

		if (hasShapeAttributesDialog)
			{
			shapeAttributesDialog = new MiShapeAttributesDialog();
			if (drawingAttributesToolBar != null)
				drawingAttributesToolBar.setShapeAttributesDialog(shapeAttributesDialog);
			else
				shapeAttributesDialog.setDisplayedAttributes(new MiAttributes());
			}
		setHasDrawingPages(hasDrawingPages);
		setHasDrawingGrid(hasDrawingGrid);
		setHasRulers(hasRulers);
		setHasLayers(hasLayers);

		if (hasBirdsEyeView)
			setHasBirdsEyeView(true);

		setHasConnectionToolbar(hasConnectionToolBar);
		setHasVisibilityToolbar(hasVisibilityToolBar);

		// ---------------------------------------------------------------
		// Add palette-specific options for the palette in the view menu.
		// ---------------------------------------------------------------
		MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);
		if (hasBirdsEyeView)
			{
			viewMenu.appendSeparator();
			MiWidget item = viewMenu.appendBooleanMenuItem(
				Mi_DISPLAY_BIRDS_EYE_VIEW_MENUITEM_DISPLAY_NAME,
				this,
				Mi_DISPLAY_BIRDS_EYE_VIEW_COMMAND_NAME, 
				Mi_HIDE_BIRDS_EYE_VIEW_COMMAND_NAME);
		
			viewMenu.setHelpMessages(Mi_DISPLAY_BIRDS_EYE_VIEW_COMMAND_NAME,
				Mi_DISPLAY_BIRDS_EYE_VIEW_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_BIRDS_EYE_VIEW_STATUS_HELP_MSG,
				Mi_DISPLAY_BIRDS_EYE_VIEW_BALLOON_HELP_MSG);
			}
		if (hasPalette)
			{
			// ---------------------------------------------------------------
			// Add palette-specific options to the file menu in the menubar
			// ---------------------------------------------------------------
			MiEditorMenu fileMenu = getMenuBar().getMenu(Mi_FILE_MENU_DISPLAY_NAME);
			int index = fileMenu.getIndexOfMenuItem(Mi_QUIT_COMMAND_NAME);
			fileMenu.insertMenuItem(Mi_PALETTE_SAVE_NAME, 
				this, Mi_PALETTE_SAVE_NAME, index);
			fileMenu.insertMenuItem(Mi_PALETTE_SAVE_AS_NAME, 
				this, Mi_PALETTE_SAVE_AS_NAME, index + 1);
			fileMenu.insertSeparator(index + 2);

			viewMenu.appendBooleanMenuItem(Mi_SHOW_PALETTE_NAME, this,
				Mi_SHOW_PALETTE_NAME, 
				Mi_HIDE_PALETTE_COMMAND_NAME);
			viewMenu.setHelpMessages(Mi_SHOW_PALETTE_NAME,
				Mi_DISPLAY_PALETTE_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_PALETTE_STATUS_HELP_MSG,
				Mi_DISPLAY_PALETTE_BALLOON_HELP_MSG);

			viewMenu.appendBooleanMenuItem(Mi_SHOW_PALETTE_LABELS_NAME, this,
				Mi_SHOW_PALETTE_LABELS_NAME, 
				Mi_HIDE_PALETTE_LABELS_COMMAND_NAME);
			viewMenu.setHelpMessages(Mi_SHOW_PALETTE_LABELS_NAME,
				Mi_DISPLAY_PALETTE_LABELS_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_PALETTE_LABELS_STATUS_HELP_MSG,
				Mi_DISPLAY_PALETTE_LABELS_BALLOON_HELP_MSG);

			setCommandState(Mi_SHOW_PALETTE_LABELS_NAME, true);
			}
		if (hasDrawingPagesDialogBox)
			{
			pageDialogBox = createDialogBoxAndMenuItem(
				"Page Properties", Mi_DISPLAY_PAGE_DIALOG_MENUITEM_DISPLAY_NAME, pageManager,
				Mi_DISPLAY_PAGE_DIALOG_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_PAGE_DIALOG_STATUS_HELP_MSG,
				Mi_DISPLAY_PAGE_DIALOG_BALLOON_HELP_MSG);
			}

		setCanHaveVisibleConnectionPoints(canHaveVisibleConnectionPoints, hasConnectionPointsDialog);
		setCanHaveVisibleAnnotationPoints(canHaveVisibleAnnotationPoints, hasAnnotationPointsDialog);

		// Home Zoom
		if (pageManager != null)
			{
			MiBounds world = getEditor().getDeviceBounds();
			if (pageManager.getDrawingPages() != null)
				world.setCenter(pageManager.getDrawingPages().getCenter());
			getEditor().setWorldBounds(world);
			}

		setupDefaultSelectionGraphics();

		//setHasAnotherView(true);
		//setHasFishEyeView(true);
		//setHasMagnifierView(true);
		//setHasMagnifierLens(true);
		}

	public		void		setupDefaultSelectionGraphics()
		{
		MiBoxSelectionGraphics boxSelectionGraphics = new MiBoxSelectionGraphics();
		boxSelectionGraphics.setMargins(new MiMargins(5));
		boxSelectionGraphics.setMinimumBoxWidthAndHeight(new MiSize(5, 5));
		boxSelectionGraphics.setAttributes(boxSelectionGraphics.getAttributes().setColor("red"));
		boxSelectionGraphics.setAttributes(boxSelectionGraphics.getAttributes().setBorderLook(Mi_FLAT_BORDER_LOOK));
		boxSelectionGraphics.setAttributes(boxSelectionGraphics.getAttributes().setLineStyle(Mi_DASHED_LINE_STYLE));

		getEditor().getSelectionManager().setSelectionGraphics(boxSelectionGraphics);
		MiManipulatableLayout.setDefaultSelectionGraphics(boxSelectionGraphics);
		}

	public		void		setHasLayers(boolean flag)
		{
		if (flag)
			{
			pageManager.setHasLayers(flag);
			layerTabs = new MiLayerTabs(editor, scrolledBox);
			}
		}
	public		void		setHasRulers(boolean flag)
		{
		if (flag)
			{
			MiPart editorPanel = getEditorPanel();
			setEditorPanel(new MiRectangle());
			rulerBox = new MiRulerBox(
				editor, 
				editorPanel, 
				pageManager.getDrawingPages() != null 
					? pageManager.getDrawingPages() : (MiPart )editor, 
				new MiSize(11.0, 8.5));
			setEditorPanel(rulerBox);

			if (visibilityToolBar != null)
				{
				visibilityToolBar.setRulerBox(rulerBox);
				}

			rulersDialogBox = createDialogBoxAndMenuItem(
				"Ruler Properties", 
				Mi_DISPLAY_RULERS_DIALOG_MENUITEM_DISPLAY_NAME, 
				new MiPartToModel(rulerBox),
				Mi_DISPLAY_RULERS_DIALOG_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_RULERS_DIALOG_STATUS_HELP_MSG,
				Mi_DISPLAY_RULERS_DIALOG_BALLOON_HELP_MSG);
			}
		else
			{
			if (visibilityToolBar != null)
				{
				visibilityToolBar.setRulerBox(null);
				}
			}
		}
	public		MiModelPropertyViewManager	getRulersDialogBox()
		{
		return(rulersDialogBox);
		}
	public		void		setCanHaveEditableAnnotationPoints(boolean flag)
		{
		if (connectionToolBar != null)
			connectionToolBar.getAnnoPointToggleButton().setVisible(flag);
		}
	public		void		setCanHaveEditableConnectionPoints(boolean flag)
		{
		if (connectionToolBar != null)
			connectionToolBar.getConnPointToggleButton().setVisible(flag);
		}

	public		void		setCanHaveVisibleAnnotationPoints(boolean hasPts, boolean hasPtsDialog)
		{
		if (hasPts)
			{
			if ((managedPointsAttributesDialogBox == null) && (hasPtsDialog))
				{
				managedPointsAttributesDialogBox = new MiManagedPointsAttributesDialog();
				if (connectionToolBar != null)
					{
					connectionToolBar.setManagedPointsAttributesDialog(
						managedPointsAttributesDialogBox);
					}
				}

			if (hasPtsDialog)
				{
				MiEditorMenu formatMenu = getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME);
				if (numberOfFormatMenuItems == 0)
			 		formatMenu.appendSeparator();

				formatMenu.appendMenuItem(
					Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_MENUITEM_DISPLAY_NAME, 
					new MiSetVisiblityCommandHandler(managedPointsAttributesDialogBox, true), 
					Mi_SHOW_COMMAND_NAME,
					Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_STATUS_HELP_MSG,
					Mi_NO_DISPLAY_ANNOTATION_POINTS_DIALOG_STATUS_HELP_MSG,
					Mi_DISPLAY_ANNOTATION_POINTS_DIALOG_BALLOON_HELP_MSG);

				++numberOfFormatMenuItems;
				}

			MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);
			annoPointVisibilityToggleButton = viewMenu.appendBooleanMenuItem(
				Mi_DISPLAY_ANNOTATION_POINTS_MENUITEM_DISPLAY_NAME, 
				this,
				Mi_DISPLAY_ANNOTATION_POINTS_MENUITEM_DISPLAY_NAME, 
				Mi_HIDE_ANNOTATION_POINTS_MENUITEM_CMD_NAME);
			viewMenu.setHelpMessages(Mi_DISPLAY_ANNOTATION_POINTS_MENUITEM_DISPLAY_NAME,
				Mi_DISPLAY_ANNOTATION_POINTS_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_ANNOTATION_POINTS_STATUS_HELP_MSG,
				Mi_DISPLAY_ANNOTATION_POINTS_BALLOON_HELP_MSG);

			annoPointVisibilityToggleButton.select(!MiAnnotationPointManager.isGloballyHidden());
			}
		else
			{
			if (visibilityToolBar != null)
				visibilityToolBar.getAnnoPointToggleButton().setVisible(false);
			if (connectionToolBar != null)
				connectionToolBar.getAnnoPointToggleButton().setVisible(false);
			}
		}
	public		void		setCanHaveVisibleConnectionPoints(boolean hasPts, boolean hasPtsDialog)
		{
		if (hasPts)
			{
			if ((managedPointsAttributesDialogBox == null) && (hasPtsDialog))
				{
				managedPointsAttributesDialogBox = new MiManagedPointsAttributesDialog();
				if (connectionToolBar != null)
					{
					connectionToolBar.setManagedPointsAttributesDialog(
						managedPointsAttributesDialogBox);
					}
				}

			if (hasPtsDialog)
				{
				MiEditorMenu formatMenu = getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME);
				if (numberOfFormatMenuItems == 0)
			 		formatMenu.appendSeparator();

				formatMenu.appendMenuItem(
					Mi_DISPLAY_CONNECTION_POINTS_DIALOG_MENUITEM_DISPLAY_NAME, 
					new MiSetVisiblityCommandHandler(managedPointsAttributesDialogBox, true), 
					Mi_SHOW_COMMAND_NAME,
					Mi_DISPLAY_CONNECTION_POINTS_DIALOG_STATUS_HELP_MSG,
					Mi_NO_DISPLAY_CONNECTION_POINTS_DIALOG_STATUS_HELP_MSG,
					Mi_DISPLAY_CONNECTION_POINTS_DIALOG_BALLOON_HELP_MSG);

				++numberOfFormatMenuItems;
				}

			MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);
			viewMenu.appendSeparator();
			connPointVisibilityToggleButton = viewMenu.appendBooleanMenuItem(
				Mi_DISPLAY_CONNECTION_POINTS_MENUITEM_DISPLAY_NAME, 
				this,
				Mi_DISPLAY_CONNECTION_POINTS_MENUITEM_DISPLAY_NAME, 
				Mi_HIDE_CONNECTION_POINTS_MENUITEM_CMD_NAME);
			viewMenu.setHelpMessages(Mi_DISPLAY_CONNECTION_POINTS_MENUITEM_DISPLAY_NAME,
				Mi_DISPLAY_CONNECTION_POINTS_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_CONNECTION_POINTS_STATUS_HELP_MSG,
				Mi_DISPLAY_CONNECTION_POINTS_BALLOON_HELP_MSG);

			connPointVisibilityToggleButton.select(!MiConnectionPointManager.isGloballyHidden());
			}
		else
			{
			if (visibilityToolBar != null)
				visibilityToolBar.getConnPointToggleButton().setVisible(false);
			if (connectionToolBar != null)
				connectionToolBar.getConnPointToggleButton().setVisible(false);
			}
		}

	protected	void		createPageManager()
		{
		pageManager = new MiPageManager();
		editor.setPageManager(pageManager);
		}

	protected	MiModelPropertyViewManager createDialogBoxAndMenuItem(
						String title, String menuItemText, MiiModelEntity target)
		{
		return(createDialogBoxAndMenuItem(title, menuItemText, target, null, null, null));
		}
	protected	MiModelPropertyViewManager createDialogBoxAndMenuItem(
						String title, String menuItemText, MiiModelEntity target,
						String enabledStatusHelp, String disabledStatusHelp, String balloonHelp)
		{
		MiModelPropertyViewManager dialogBox = new MiModelPropertyViewManager(
			new MiDialogBoxTemplate(this, title, false),
			target);

		MiEditorMenu formatMenu = getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME);
		if (numberOfFormatMenuItems == 0)
			 formatMenu.appendSeparator();

		if (enabledStatusHelp == null)
			{
			formatMenu.appendMenuItem(
				menuItemText, 
				new MiSetVisiblityCommandHandler(dialogBox.getDialogBox(), true), 
				Mi_SHOW_COMMAND_NAME);
			}
		else
			{
			formatMenu.appendMenuItem(
				menuItemText, 
				new MiSetVisiblityCommandHandler(dialogBox.getDialogBox(), true), 
				Mi_SHOW_COMMAND_NAME,
				enabledStatusHelp,
				disabledStatusHelp,
				balloonHelp);
			}

		++numberOfFormatMenuItems;

		return(dialogBox);
		}
	protected	void		popupShapeAttributeDialog(int frontPanelIndex)
		{
		if (shapeAttributesDialog == null)
			{
			shapeAttributesDialog = new MiShapeAttributesDialog();
			shapeAttributesDialog.appendCommandHandler(
				this, Mi_SHAPE_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
			}
		MiParts parts = editor.getSelectedParts(new MiParts());
		shapeAttributesDialog.setTargetShapes(parts);
		shapeAttributesDialog.setVisible(true);

		shapeAttributesDialog.setFrontFolder(frontPanelIndex);
		}
	public		void		setHasDrawingPages(boolean flag)
		{
		pageManager.setHasDrawingPages(flag);
		if (flag)
			{
			MiEditorMenu editMenu = getMenuBar().getMenu(Mi_EDIT_MENU_DISPLAY_NAME);
			editMenu.appendSeparator();
			editMenu.appendMenuItem(
				Mi_SCALE_TO_FIT_PAGE_DISPLAY_NAME, 
				this,
				Mi_SCALE_TO_FIT_PAGE_COMMAND_NAME,
				Mi_SCALE_TO_FIT_PAGE_STATUS_HELP_NAME,
				Mi_SCALE_TO_FIT_PAGE_STATUS_HELP_NAME,
				null);

			editMenu.appendMenuItem(
				Mi_MOVE_TO_FIT_PAGE_DISPLAY_NAME, 
				this,
				Mi_MOVE_TO_FIT_PAGE_COMMAND_NAME,
				Mi_MOVE_TO_FIT_PAGE_STATUS_HELP_NAME,
				Mi_MOVE_TO_FIT_PAGE_STATUS_HELP_NAME,
				null);

			editMenu.appendMenuItem(
				Mi_ADJUST_PAGES_TO_FIT_DISPLAY_NAME, 
				this,
				Mi_ADJUST_PAGES_TO_FIT_COMMAND_NAME,
				Mi_ADJUST_PAGES_TO_FIT_STATUS_HELP_NAME,
				Mi_ADJUST_PAGES_TO_FIT_STATUS_HELP_NAME,
				null);

			if (rulerBox != null)
				rulerBox.setPage(pageManager.getDrawingPages());
			}
		else
			{
			if (rulerBox != null)
				rulerBox.setPage(editor);
			}
		}
	public		void		setHasDrawingGrid(boolean flag)
		{
		if (flag)
			{
			pageManager.setHasDrawingGrid(flag);
			pageManager.getSnapManager().setKeepShapesWithinPageBounds(keepShapesWithinPageBounds);

			MiDrawingGrid grid = pageManager.getDrawingGrid();
			grid.setGridSizeInUnits(new MiSize(0.05, 0.05));
			grid.setMajorGridLookSpacingAsMultipleOfGridSpacing(10);
			grid.setFillInMinorGrid(true);
			grid.setColor("black");
			grid.setMinorGridLookColor("darkGray");
			grid.setMajorGridLookSize(0.02);
			grid.setLook(grid.Mi_GRID_SQUARES_LOOK_NAME);
			}
		else
			{
			if (visibilityToolBar != null)
				visibilityToolBar.getGridToggleButton().setVisible(false);
			}
		}

	public		void		setHasDrawingToolbar(boolean flag)
		{
		if (flag)
			{
			if (drawingToolBarManager == null)
				drawingToolBarManager = new MiDrawingToolBarManager();

/*
			drawingToolBar = new MiDrawingToolBar(editor, this);
			drawingToolBar.setToolBarManager(drawingToolBarManager);
			drawingToolBarManager.registerToolBar(drawingToolBar);
			drawingToolBar.getDragAndDropBehavior().setValidTargets(new MiParts(this));
			getTopDockingPanel().appendPart(drawingToolBar);
*/
			drawingAttributesToolBar = new MiDrawingAttributesToolBar(editor, this);
			drawingAttributesToolBar.setToolBarManager(drawingToolBarManager);
			drawingToolBarManager.registerToolBar(drawingAttributesToolBar);
			drawingAttributesToolBar.getDragAndDropBehavior().setValidTargets(new MiParts(this));
			getTopDockingPanel().appendPart(drawingAttributesToolBar);
		
			drawingShapeToolBar = new MiDrawingShapeToolBar(editor, this);
			drawingShapeToolBar.setToolBarManager(drawingToolBarManager);
			drawingToolBarManager.registerToolBar(drawingShapeToolBar);
			drawingShapeToolBar.getDragAndDropBehavior().setValidTargets(new MiParts(this));
			getTopDockingPanel().getPart(0).appendPart(drawingShapeToolBar);
			//if (managedPointsAttributesDialogBox != null)
				//drawingShapeToolBar.setManagedPointsAttributesDialog(managedPointsAttributesDialogBox);
		
			MiEditorMenu formatMenu = getMenuBar().getMenu(Mi_FORMAT_MENU_DISPLAY_NAME);
		
			formatMenu.removeMenuItemWithCommand(Mi_FORMAT_COMMAND_NAME);
			formatMenu.insertSeparator(0);
			formatMenu.insertMenuItem(Mi_SHAPE_FILL_ATTRIBUTE_MENU_NAME, 
				this, Mi_POPUP_FILL_ATTRIBUTES_CMD_NAME, 0);
			formatMenu.insertMenuItem(Mi_SHAPE_LINE_ATTRIBUTE_MENU_NAME, 
				this, Mi_POPUP_LINE_ATTRIBUTES_CMD_NAME, 0);
			formatMenu.insertMenuItem(Mi_SHAPE_LINE_ENDS_ATTRIBUTE_MENU_NAME, 
				this, Mi_POPUP_LINE_ENDS_ATTRIBUTES_CMD_NAME, 0);
			formatMenu.insertMenuItem(Mi_SHAPE_SHADOW_ATTRIBUTE_MENU_NAME, 
				this, Mi_POPUP_SHADOW_ATTRIBUTES_CMD_NAME, 0);
			formatMenu.insertMenuItem(Mi_SHAPE_BORDER_LOOK_ATTRIBUTE_MENU_NAME, 
				this, Mi_POPUP_BORDER_LOOK_ATTRIBUTES_CMD_NAME, 0);
			formatMenu.insertMenuItem(Mi_SHAPE_TEXT_ATTRIBUTE_MENU_NAME, 
				this, Mi_POPUP_TEXT_ATTRIBUTES_CMD_NAME, 0);
			}
		}
	public		void		setHasConnectionToolbar(boolean flag)
		{
		if (flag)
			{
			if (drawingToolBarManager == null)
				drawingToolBarManager = new MiDrawingToolBarManager();

			connectionToolBar = new MiDrawingConnectionToolBar(editor, this);
			connectionToolBar.getDragAndDropBehavior().setValidTargets(new MiParts(this));
			connectionToolBar.setToolBarManager(drawingToolBarManager);
			drawingToolBarManager.registerToolBar(connectionToolBar);

			if (getTopDockingPanel().getNumberOfParts() > 1)
				getTopDockingPanel().getPart(1).appendPart(connectionToolBar);
			else
				getTopDockingPanel().getPart(0).appendPart(connectionToolBar);
			if (managedPointsAttributesDialogBox != null)
				connectionToolBar.setManagedPointsAttributesDialog(managedPointsAttributesDialogBox);
			}
		else
			{
			if (connectionToolBar != null)
				connectionToolBar.setVisible(false);
			}
		}
	public		void		setHasVisibilityToolbar(boolean flag)
		{
		if (flag)
			{
			visibilityToolBar = new MiDrawingVisibilityToolBar(editor, this);
			visibilityToolBar.getDragAndDropBehavior().setValidTargets(new MiParts(this));
			visibilityToolBar.setToolBarManager(drawingToolBarManager);
			drawingToolBarManager.registerToolBar(visibilityToolBar);

			if (getTopDockingPanel().getNumberOfParts() > 1)
				getTopDockingPanel().getPart(1).appendPart(visibilityToolBar);
			else
				getTopDockingPanel().getPart(0).appendPart(visibilityToolBar);

			visibilityToolBar.setPageManager(pageManager);

			visibilityToolBar.setRulerBox(rulerBox);
			}
		else
			{
			if (visibilityToolBar != null)
				visibilityToolBar.setVisible(false);
			}
		}
	public		MiDrawingVisibilityToolBar getVisiblityToolbar()
		{
		return(visibilityToolBar);
		}
	public		MiDrawingConnectionToolBar getConnectionToolbar()
		{
		return(connectionToolBar);
		}
	public		void		setHasBirdsEyeView(boolean flag)
		{
		birdsEyeViewWindow = new MiNativeWindow(Mi_BIRDS_EYE_VIEW_BORDER_TITLE);
		birdsEyeView = new MiBirdsEyeView(editor);
		birdsEyeViewWindow.appendPart(birdsEyeView);
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		birdsEyeViewWindow.setLayout(columnLayout);
		birdsEyeViewWindow.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));
		}
	public		void		setHasAnotherView(boolean flag)
		{
		anotherViewWindow = new MiNativeWindow(Mi_ANOTHER_VIEW_BORDER_TITLE);
		anotherView = new MiAnotherView(editor);
		anotherViewWindow.appendPart(anotherView);
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		anotherViewWindow.setLayout(columnLayout);
		anotherViewWindow.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));
		MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_ANOTHER_VIEW_MENUITEM_DISPLAY_NAME, 
				new MiSetVisiblityCommandHandler(anotherViewWindow, true),
				Mi_SHOW_COMMAND_NAME, Mi_HIDE_COMMAND_NAME);
		}
	public		void		setHasFishEyeView(boolean flag)
		{
		fishEyeViewWindow = new MiNativeWindow(Mi_FISH_EYE_VIEW_BORDER_TITLE);
		fishEyeView = new MiFishEyeView(editor);
		fishEyeViewWindow.appendPart(fishEyeView);
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		fishEyeViewWindow.setLayout(columnLayout);
		fishEyeViewWindow.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));
		MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_FISH_EYE_VIEW_MENUITEM_DISPLAY_NAME, 
				new MiSetVisiblityCommandHandler(fishEyeViewWindow, true),
				Mi_SHOW_COMMAND_NAME, 
				Mi_HIDE_COMMAND_NAME);
		}
	public		void		setHasMagnifierView(boolean flag)
		{
		magnifierViewWindow = new MiNativeWindow(Mi_MAGNIFIER_VIEW_BORDER_TITLE);
		magnifierView = new MiMagnifierView(editor);
		magnifierViewWindow.appendPart(magnifierView);
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		magnifierViewWindow.setLayout(columnLayout);
		magnifierViewWindow.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));
		MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_MAGNIFIER_VIEW_MENUITEM_DISPLAY_NAME, 
				new MiSetVisiblityCommandHandler(magnifierViewWindow, true),
				Mi_SHOW_COMMAND_NAME, Mi_HIDE_COMMAND_NAME);
		}
	public		void		setHasMagnifierLens(boolean flag)
		{
		// GET TOP LAYER FROM pageManager
		MiPart overlayLayer = new MiLayer();
		editor.appendLayer(overlayLayer);
		magnifierLensWindow = new MiInternalWindow(
			overlayLayer, Mi_MAGNIFIER_LENS_BORDER_TITLE, new MiBounds(0, 0, 200, 200), false);
		magnifierLensWindow.setIsEmbeddedWindow(true);
		// IF LENS IS TRANSPARENT
		magnifierLensWindow.setIsOpaqueRectangle(false);
		magnifierLensWindow.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		magnifierLensWindow.getWindowBorder()
			.getSubjectContainer().setBackgroundColor(Mi_TRANSPARENT_COLOR);
		magnifierLens = new MiMagnifierLens(editor);
		magnifierLensWindow.appendPart(magnifierLens);
		//editor.appendPart(magnifierLensWindow);
		magnifierLensWindow.setCenter(editor.getWorldBounds().getCenter());
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		magnifierLensWindow.setLayout(columnLayout);
		magnifierLensWindow.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));
		MiEditorMenu viewMenu = getMenuBar().getMenu(Mi_VIEW_MENU_DISPLAY_NAME);
		viewMenu.appendBooleanMenuItem(Mi_DISPLAY_MAGNIFIER_LENS_MENUITEM_DISPLAY_NAME, 
				new MiSetVisiblityCommandHandler(magnifierLensWindow, true),
				Mi_SHOW_COMMAND_NAME, Mi_HIDE_COMMAND_NAME);
		}
	public		void		setKeepShapesWithinPageBounds(boolean flag)
		{
		keepShapesWithinPageBounds = flag;
		if (pageManager.getSnapManager() != null)
			pageManager.getSnapManager().setKeepShapesWithinPageBounds(flag);
		}
	public		boolean		getKeepShapesWithinPageBounds()
		{
		return(keepShapesWithinPageBounds);
		}
	public		MiRulerBox	getRulerBox()
		{
		return(rulerBox);
		}
	public		MiToolBar	getDrawingAttributesToolBar()
		{
		return(drawingAttributesToolBar);
		}
	public		MiToolBar	getDrawingShapeToolBar()
		{
		return(drawingShapeToolBar);
		}
	public		MiHelpWindow	getHelpWindow()
		{
		if (helpWindow == null)
			{
			helpWindow = new MiHelpWindow(null, new MiBounds(0, 0, 500, 500));
			MiSystem.getHelpManager().setHelpOnApplicationDialog(helpWindow);
			helpWindow.setDefaultCloseCommand(Mi_CLOSE_WINDOW_COMMAND_NAME);
			helpWindow.setHelpFile("${Mi_HOME}/apps/MiNetwork.help");
			}
		return(helpWindow);
		}
					/**------------------------------------------------------
	 				 * Gets the graphics editor that manages the main panel
					 * of this window.
					 * @return 		the graphics editor
					 *------------------------------------------------------*/
	public		MiGraphicsEditor getGraphEditor()
		{
		return(editor);
		}
					/**------------------------------------------------------
	 				 * Opens the palette and loads it with the graphics in 
					 * the given filename. The filename is prepended with
					 * the value of the property Mi_GRAPHICS_PALETTES_HOME.
					 * @param filename 	the palette graphics file
					 *------------------------------------------------------*/
	public		boolean		openPalette(String filename)
		{
		if (palette != null)
			{
			if (!palette.openFile(filename))
				return(false);
			if (!palette.isVisible())
				palette.setVisible(true);
			setCommandState(Mi_SHOW_PALETTE_NAME, true);
			setCommandSensitivity(Mi_PALETTE_SAVE_NAME, true);
			setCommandSensitivity(Mi_PALETTE_SAVE_AS_NAME, true);
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Saves the contents of the graphics editor.
					 * @param stream 	where to save the contents
					 * @param streamName 	the name of the stream
					 * @overrides		MiFileEditor#save
					 * @return true if successful
					 *------------------------------------------------------*/
	public		 boolean	save(OutputStream stream, String streamName)
		{
		return(editor.save(stream, streamName));
		}
					/**------------------------------------------------------
	 				 * Saves the contents of the graphics editor of the given view.
					 * @param view 		what to save (which document in a MDI)
					 * @param stream 	where to save the contents
					 * @param streamName 	the name of the stream
					 * @overrides		MiFileEditor#save
					 * @return true if successful
					 *------------------------------------------------------*/
	public		 boolean	save(MiFileEditorWindowDocumentView view, OutputStream stream, String streamName)
		{
		return(((MiGraphicsEditor )view.getEditor()).save(stream, streamName));
		}
					/**------------------------------------------------------
	 				 * Loads the contents of the graphics editor from the given
					 * stream. The first line of the stream is examined to 
					 * determine the format of the file.
					 * @param stream 	where to get the contents
					 * @param streamName 	the name of the stream
					 * @overrides		MiFileEditor#load
					 *------------------------------------------------------*/
	protected 	 void		load(BufferedInputStream stream, String streamName) throws IOException
		{
		MiFileEditorWindowDocumentView view = getCurrentView();
		MiGraphicsEditor editor = (MiGraphicsEditor )view.getEditor();

		if ((editor.hasLayers()) && (pageManager.getBackgroundLayer() != null))
			{
			pageManager.getLayerManager().appendNewLayer();
			editor.setCurrentLayer(1);
			}
//MiDebug.println("LOQADINGGGGG : getCurrentView = " + getCurrentView());
//MiDebug.println("LOQADINGGGGG : getCurrentView.getController = " + getCurrentView().getController());
		editor.setPrototypeController(getCurrentView().getController());
		editor.load(stream, streamName);
		getCurrentView().setController(editor.getOpenDocumentController());
		MiEditingPermissions permissions = editor.getEditingPermissions();
		updateViewFromPermissions(permissions);
		if (editor.getOpenDocumentTitle() != null)
			setOpenFileTitle(editor.getOpenDocumentTitle());

		if (editor.getSnapManager() != null)
			editor.getSnapManager().setKeepShapesWithinPageBounds(keepShapesWithinPageBounds);
		}
	protected	void		updateViewFromPermissions(MiEditingPermissions permissions)
		{
		setOpenFilenameReadOnly(!(permissions.isWritable() || permissions.isCopyable()));

		if (!permissions.isWritable())
			{
			setCommandVisibility(Mi_SAVE_COMMAND_NAME, false);
			}
		if (!permissions.isCopyable())
			{
			setCommandVisibility(Mi_SAVE_AS_COMMAND_NAME, false);
			}
		if (!permissions.isPrintable())
			{
			setCommandVisibility(Mi_PRINT_COMMAND_NAME, false);
			setCommandVisibility(Mi_PRINT_SETUP_COMMAND_NAME, false);
			}
		if (!permissions.isEditable())
			{
			setCommandVisibility(Mi_CUT_COMMAND_NAME, false);
			setCommandVisibility(Mi_COPY_COMMAND_NAME, false);
			setCommandVisibility(Mi_PASTE_COMMAND_NAME, false);
			}
		if (!permissions.isCustomizable())
			{
			setCommandVisibility(Mi_UNDO_COMMAND_NAME, false);
			setCommandVisibility(Mi_REDO_COMMAND_NAME, false);
			getMenuBar().getMenu(Mi_EDIT_MENU_DISPLAY_NAME).getMenu().setVisible(false);
			}
		if (!permissions.isBrowseable())
			{
			}
		}
					/**------------------------------------------------------
	 				 * Opens the default, no name, file that the user can
					 * edit in but which needs to be named before saving.
					 * @overrides		MiFileEditorWindow#defaultFileOpened
					 * @see			MiFileEditorWindow#openDefaultFile
					 *------------------------------------------------------*/
	protected 	void		prepareToLoadDefaultFile()
		{
		// Don't want the default controller, which acts as a prototype, 
		// to get 'closed' and lose its reference to its viewManager
		if (getCurrentView().getController() != null)
			{
			getCurrentView().setController(getCurrentView().getController().copy());
			}

		editor.prepareToLoadDefaultFile();
		if ((editor.hasLayers()) && (pageManager.getBackgroundLayer() != null))
			{
			pageManager.getLayerManager().appendNewLayer();
			editor.setCurrentLayer(1);
			}
		}
					/**------------------------------------------------------
	 				 * Remove all graphics and layers, restoring any background
					 * layer and current layers, if necessary.
					 *------------------------------------------------------*/
	protected	void		close()
		{
		MiFileEditorWindowDocumentView view = getCurrentView();
		MiEditor editor = view.getEditor();

		if (!editor.hasLayers())
			{
			editor.removeAllParts();
			editor.setLayout(new MiSizeOnlyLayout());
			}
		else
			{
			int index = pageManager.getBackgroundLayer() != null ? 1 : 0;
			while (editor.getNumberOfLayers() > index)
				{
				if (editor.getLayer(index) != pageManager.getBackgroundLayer())
					{
					MiPart layer = editor.getLayer(index);
					editor.removeLayer(layer);
					layer.removeAllParts();
					}
				}
			if (index == 0)
				{
				pageManager.getLayerManager().appendNewLayer();
				}
			editor.setCurrentLayer(0);
			}
		super.close();
		}
					/**------------------------------------------------------
	 				 * Customizes this window based on the default setup provided
					 * by the MiEditorWindow superclass.
					 *------------------------------------------------------*/
	protected	void		setup()
		{
		MiSystem.setApplicationDefaultProperty(Mi_DEFAULT_WINDOW_BORDER_TITLE, getTitle());

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
		// This is done by the MiStatusBarFocusStatusField in the standard 
		// MiEditorStatusBar. Otherwise, this just blaps over the statusBar's
		// state field which can be used for other things - like "autosaving 
		// complete".
		// ---------------------------------------------------------------
		//getEditor().appendEventHandler(
			//new MiSetStatusBarStateToNameOfObjectUnderMouse((MiEditorStatusBar )getStatusBar()));

		// ---------------------------------------------------------------
		// Desensitize the 'save' options in the menubar and toolbar and
		// whereever else
		// ---------------------------------------------------------------
		setCommandSensitivity(Mi_SAVE_COMMAND_NAME, false);

		// ---------------------------------------------------------------
		// There is not support for these ... yet ...
		// ---------------------------------------------------------------
		//setCommandSensitivity(Mi_PRINT_COMMAND_NAME, false);
		setCommandSensitivity(Mi_PRINT_SETUP_COMMAND_NAME, false);
		setCommandSensitivity(Mi_DISPLAY_PROPERTIES_COMMAND_NAME, false);
		setCommandSensitivity(Mi_DISPLAY_PREFERENCES_COMMAND_NAME, false);

		setCommandSensitivity(Mi_PALETTE_SAVE_NAME, false);
		setCommandSensitivity(Mi_PALETTE_SAVE_AS_NAME, false);

		getMenuBar().removePulldownMenu(Mi_HELP_MENU_DISPLAY_NAME);
		MiHelpMenu menu = new MiHelpMenu(this, this);
		getMenuBar().appendPulldownMenu(menu);

		appendEventHandler(new MiIPrint());

		editor.appendActionHandler(this, Mi_DATA_IMPORT_ACTION | Mi_REQUEST_ACTION_PHASE);
		
		// ---------------------------------------------------------------
		// We want to be notified when editing actions occur so that we can
		// enable the 'save' menu and toolbar items.
		// ---------------------------------------------------------------
		MiSystem.getTransactionManager().appendActionHandler(
			this, Mi_TRANSACTION_MANAGER_CHANGED_ACTION);
		}
					/**------------------------------------------------------
	 				 * Makes the graphics editor panel for the window. This is
					 * called by the MiEditorWindow superclass during setup (i.e. 
					 * buildEditorWindow()). 
					 *------------------------------------------------------*/
	public		MiPart		makeDefaultEditorPanel()
		{
		editor = new MiGraphicsEditor();
		scrolledBox = new MiScrolledBox(editor);
		editor.setDeviceBounds(new MiBounds(0,0,400,400));
		editor.setPreferredSize(new MiSize(400,400));
		editor.setMinimumSize(new MiSize(100,100));
		editor.setDragAndDropBehavior(new MiGraphicsEditorDragAndDropBehavior(editor));
		setEditor(editor);
		editor.setPrototypeController(defaultController);
		return(scrolledBox);
		}
					/**------------------------------------------------------
	 				 * Makes the palette for the window. This is called by
					 * the MiEditorWindow superclass during setup (i.e. 
					 * buildEditorWindow()). Make it invisible to start with.
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultPalette()
		{
		palette = new MiGraphicsPalette();
		palette.setVisible(false);
		return(palette);
		}
					/**------------------------------------------------------
	 				 * Processes the given command. The commands supported are:
					 * <pre>
					 *    Mi_PALETTE_SAVE_NAME
					 *    Mi_PALETTE_SAVE_AS_NAME
					 *    Mi_SHOW_PALETTE_NAME
					 *    Mi_HIDE_PALETTE_LABELS_COMMAND_NAME
					 *    Mi_HIDE_PALETTE_COMMAND_NAME
					 * </pre>
					 * @param command  	the command to execute
					 * @overrides		MiFileEditorWindow#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		pushMouseAppearance(Mi_WAIT_CURSOR, "MiGraphicsWindow");
		if (command.equals(Mi_PRINT_COMMAND_NAME))
			{
			if (printFormatIsPostScript)
				setRequiredPrintFileExtension("ps");
			else
				setRequiredPrintFileExtension("pdf");
			print();
			}
		else if (command.equals(Mi_PALETTE_SAVE_NAME))
			{
			palette.processCommand(Mi_SAVE_COMMAND_NAME);
			}
		else if (command.equals(Mi_PALETTE_SAVE_AS_NAME))
			{
			palette.processCommand(Mi_SAVE_AS_COMMAND_NAME);
			}
		else if (command.equals(Mi_DISPLAY_BIRDS_EYE_VIEW_COMMAND_NAME))
			{
			birdsEyeViewWindow.setVisible(true);
			}
		else if (command.equals(Mi_HIDE_BIRDS_EYE_VIEW_COMMAND_NAME))
			{
			birdsEyeViewWindow.setVisible(false);
			}
		else if (command.equals(Mi_SHOW_PALETTE_NAME))
			{
			setCommandState(Mi_SHOW_PALETTE_NAME, true);
			getPalette().setVisible(true);
			setCommandSensitivity(Mi_PALETTE_SAVE_NAME, true);
			setCommandSensitivity(Mi_PALETTE_SAVE_AS_NAME, true);
			}
		else if (command.equals(Mi_SHOW_PALETTE_LABELS_NAME))
			{
			palette.setLabelsAreVisible(true);
			}
		else if (command.equals(Mi_HIDE_PALETTE_LABELS_COMMAND_NAME))
			{
			palette.setLabelsAreVisible(false);
			}
		else if (command.equals(Mi_HIDE_PALETTE_COMMAND_NAME))
			{
			getPalette().setVisible(false);
			setCommandSensitivity(Mi_PALETTE_SAVE_NAME, false);
			setCommandSensitivity(Mi_PALETTE_SAVE_AS_NAME, false);
			}
		else if (command.equalsIgnoreCase(Mi_POPUP_TEXT_ATTRIBUTES_CMD_NAME))
			{
			popupShapeAttributeDialog(
				MiShapeAttributesPanel.Mi_TEXT_SHAPE_ATTRIBUTES_PANEL_INDEX);
			}
		else if (command.equalsIgnoreCase(Mi_POPUP_FILL_ATTRIBUTES_CMD_NAME))
			{
			popupShapeAttributeDialog(
				MiShapeAttributesPanel.Mi_FILL_SHAPE_ATTRIBUTES_PANEL_INDEX);
			}
		else if (command.equalsIgnoreCase(Mi_POPUP_LINE_ATTRIBUTES_CMD_NAME))
			{
			popupShapeAttributeDialog(
				MiShapeAttributesPanel.Mi_LINE_SHAPE_ATTRIBUTES_PANEL_INDEX);
			}
		else if (command.equalsIgnoreCase(Mi_POPUP_LINE_ENDS_ATTRIBUTES_CMD_NAME))
			{
			popupShapeAttributeDialog(
				MiShapeAttributesPanel.Mi_LINE_ENDS_SHAPE_ATTRIBUTES_PANEL_INDEX);
			}
		else if (command.equalsIgnoreCase(Mi_POPUP_SHADOW_ATTRIBUTES_CMD_NAME))
			{
			popupShapeAttributeDialog(
				MiShapeAttributesPanel.Mi_SHADOW_SHAPE_ATTRIBUTES_PANEL_INDEX);
			}
		else if (command.equalsIgnoreCase(Mi_POPUP_BORDER_LOOK_ATTRIBUTES_CMD_NAME))
			{
			popupShapeAttributeDialog(
				MiShapeAttributesPanel.Mi_BORDER_LOOK_SHAPE_ATTRIBUTES_PANEL_INDEX);
			}
		else if (command.equalsIgnoreCase(Mi_DISPLAY_ANNOTATION_POINTS_MENUITEM_DISPLAY_NAME))
			{
			MiAnnotationPointManager.setGloballyHidden(false);
			if (visibilityToolBar != null)
				{
				visibilityToolBar.processCommand(MiDrawingVisibilityToolBar.
					Mi_ANNOTATION_POINT_VISIBILITY_CHANGED_CMD_NAME);
				}
			editor.invalidateArea();
			}
		else if (command.equalsIgnoreCase(Mi_HIDE_ANNOTATION_POINTS_MENUITEM_CMD_NAME))
			{
			MiAnnotationPointManager.setGloballyHidden(true);
			if (visibilityToolBar != null)
				{
				visibilityToolBar.processCommand(MiDrawingVisibilityToolBar.
					Mi_ANNOTATION_POINT_VISIBILITY_CHANGED_CMD_NAME);
				}
			editor.invalidateArea();
			}
		else if (command.equalsIgnoreCase(Mi_DISPLAY_CONNECTION_POINTS_MENUITEM_DISPLAY_NAME))
			{
			MiConnectionPointManager.setGloballyHidden(false);
			if (visibilityToolBar != null)
				{
				visibilityToolBar.processCommand(MiDrawingVisibilityToolBar.
					Mi_CONNECTION_POINT_VISIBILITY_CHANGED_CMD_NAME);
				}
			editor.invalidateArea();
			}
		else if (command.equalsIgnoreCase(Mi_HIDE_CONNECTION_POINTS_MENUITEM_CMD_NAME))
			{
			MiConnectionPointManager.setGloballyHidden(true);
			if (visibilityToolBar != null)
				{
				visibilityToolBar.processCommand(MiDrawingVisibilityToolBar.
					Mi_CONNECTION_POINT_VISIBILITY_CHANGED_CMD_NAME);
				}
			editor.invalidateArea();
			}
		else if (command.equalsIgnoreCase(Mi_SCALE_TO_FIT_PAGE_COMMAND_NAME))
			{
			if (pageManager.getDrawingPages() == null)
				return;

			MiScalePartsCommand scalePartsCommand = new MiScalePartsCommand();
			scalePartsCommand.setTargetOfCommand(getEditor());
			scalePartsCommand.setDesiredBounds(pageManager.getDrawingPages().getBounds());
			scalePartsCommand.processCommand(Mi_SCALE_TO_FIT_PAGE_COMMAND_NAME);
			}
		else if (command.equalsIgnoreCase(Mi_MOVE_TO_FIT_PAGE_COMMAND_NAME))
			{
			if (pageManager.getDrawingPages() == null)
				return;

			MiScalePartsCommand scalePartsCommand = new MiScalePartsCommand();
			MiBounds contentsBounds = new MiBounds();
			getEditor().reCalcBoundsOfContents(contentsBounds);

			MiPoint oldCenter = contentsBounds.getCenter();
			getEditor().snapMovingPoint(oldCenter);
			MiVector oldCenterSnapTranslation = new MiVector(
					oldCenter.x - contentsBounds.getCenterX(), 
					oldCenter.y - contentsBounds.getCenterY());
			MiPoint newCenter = pageManager.getDrawingPages().getCenter();
			getEditor().snapMovingPoint(newCenter);
			newCenter.x -= oldCenterSnapTranslation.x;
			newCenter.y -= oldCenterSnapTranslation.y;
			contentsBounds.setCenter(newCenter);

			scalePartsCommand.setTargetOfCommand(getEditor());
			scalePartsCommand.setDesiredBounds(contentsBounds);
			scalePartsCommand.processCommand(Mi_SCALE_TO_FIT_PAGE_COMMAND_NAME);
			}
		else if ((command.equalsIgnoreCase(Mi_ZOOM_TO_FIT_CONTENT_COMMAND_NAME))
			|| (command.equalsIgnoreCase(Mi_ZOOM_TO_FIT_CONTENT_WIDTH_COMMAND_NAME))
			|| (command.equalsIgnoreCase(Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_COMMAND_NAME)))
			{
			MiPanAndZoomCommand panAndZoomCommand = new MiPanAndZoomCommand();
			panAndZoomCommand.setTargetOfCommand(getEditor());
			panAndZoomCommand.processCommand(command);
			}
		else if (command.equalsIgnoreCase(Mi_ADJUST_PAGES_TO_FIT_COMMAND_NAME))
			{
			if (pageManager.getDrawingPages() == null)
				return;

			MiSize paperSize = pageManager.getDrawingPages().getPaperSize();
			if (pageManager.getDrawingPages().getOrientation() == Mi_HORIZONTAL)
				{
				MiDistance d = paperSize.getWidth();
				paperSize.setWidth(paperSize.getHeight());
				paperSize.setHeight(d);
				}
			MiBounds contentsBounds = new MiBounds();
			getEditor().reCalcBoundsOfContents(contentsBounds);

			int numPagesWide = 1;
			MiDistance width = paperSize.getWidth();
			while (width < contentsBounds.getWidth())
				{
				++numPagesWide;
				width += paperSize.getWidth();
				}

			int numPagesTall = 1;
			MiDistance height = paperSize.getHeight();
			while (height < contentsBounds.getHeight())
				{
				++numPagesTall;
				height += paperSize.getHeight();
				}

			pageManager.getDrawingPages().setPagesWide(numPagesWide);
			pageManager.getDrawingPages().setPagesTall(numPagesTall);
			processCommand(Mi_MOVE_TO_FIT_PAGE_COMMAND_NAME);
			}
		else if (command.startsWith(Mi_DISPLAY_ABOUT_DIALOG_COMMAND_NAME))
			{
			MiToolkit.postAboutDialog(
				this, getDefaultWindowTitle(), 
				MiSystem.getCompanyLogo(), Mi_GRAPHICS_WINDOW_ABOUT_MSG, false);
			}
		else if (command.startsWith(Mi_HELP_ON_APPLICATION_COMMAND_NAME))
			{
			getHelpWindow().setVisible(true);
			}
		else
			{
			super.processCommand(command);
			}
		popMouseAppearance("MiGraphicsWindow");
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_DATA_IMPORT_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();

			if ((keepShapesWithinPageBounds) && (pageManager.getDrawingPages() != null))
				{
				MiBounds drawingPageBounds = MiBounds.newBounds();
				boolean withinPageBounds = pageManager.getDrawingPages().getBounds(drawingPageBounds)
					.contains(transferOp.getLookTargetBounds());
				MiBounds.freeBounds(drawingPageBounds);

				if (!withinPageBounds)
					{
					action.setVetoed(true);
					return(false);
					}
				}
			MiPart target = transferOp.getTarget();
			if ((editor == target) || (editor.isContainerOf(target)))
				{
				MiiDragAndDropBehavior dndBehavior = 
					transferOp.getSource().getDragAndDropBehavior();
				if (dndBehavior != null)
					{
					MiParts targets = dndBehavior.getValidTargets();
					if ((targets != null) 
						&& (!targets.contains(editor))
						&& (!targets.contains(target)))
						{
						action.setVetoed(true);
						return(false);
						}
					}
				return(true);
				}
			}
		else if (action.hasActionType(Mi_TRANSACTION_MANAGER_CHANGED_ACTION))
			{
			// If not just the undo stack changing because we changed views...
			if ((!MiTransactionManager.Mi_SET_TRANSACTION_STACK.equals(action.getActionSystemInfo()))
				// And not just the beinning of a transaction...
				&& (!MiTransactionManager.Mi_START_TRANSACTION.equals(action.getActionSystemInfo())))
				{
				setHasChanged(true);		
				}
			}
		return(super.processAction(action));
		}
		
					/**------------------------------------------------------
	 				 * Gets whether this window has changed. This is checked
					 * by the MiFileEditorWindow before a open, close or new.
					 * @return 		true if this has changed.
					 * @overrides		MiFileEditorWindow#getHasChanged
					 *------------------------------------------------------*/
	public		boolean		getHasChanged()
		{
		//return(editor.getHasChanged());
		return(super.getHasChanged());
		}
					/**------------------------------------------------------
	 				 * Gets whether this window has changed. This is checked
					 * by the MiFileEditorWindow before a open, close or new.
					 * @return 		true if it is OK to overwrite any
					 *			changes the user has made
					 * @overrides		MiFileEditorWindow#verifyLossOfAnyChanges
					 *------------------------------------------------------*/
	public		boolean		verifyLossOfAnyChanges()
		{
		if (palette.getHasChanged())
			{
			if (!palette.verifyLossOfAnyChanges())
				return(false);
			}
		return(super.verifyLossOfAnyChanges());
		}
	public		void		print()
		{
		if (getPrintUsingJDK())
			{
			MiPrint printCommand = new MiPrint();
			printCommand.setPrintDriver(new MiJDKPrintDriver(getFrame()));
			printCommand.setFilename(null);
			printCommand.setTargetOfCommand(getEditor());

			try	{
				printCommand.print(
					null,		//		filename, 
					300,		// 		dotsPerInch, 
					pageManager.getPageSize().getName(),
					pageManager.getDrawingPages().getOrientation() == Mi_VERTICAL 
						? Mi_PORTRAIT_NAME : Mi_LANDSCAPE_NAME,
					"COLOR",
					pageManager.getDrawingPages().getPagesWide(), 
					pageManager.getDrawingPages().getPagesTall(), 
					pageManager.getDrawingPages().getBounds());	// boundsToPrint
				}
			catch (Exception e)
				{
				MiToolkit.postErrorDialog(this, "Printing failed\n");
				}
			}
		else
			{
			printAs();
			}
		}
	public		boolean		printFile(String filename)
		{
		MiPrint printCommand = new MiPrint();
		if (printFormatIsPostScript)
			printCommand.setPrintDriver(new MiPostScriptDriver());
		else
			printCommand.setPrintDriver(new MiPDFDriver());

		if (pageManager.getDrawingPages() == null)
			{
			printCommand.setFilename(filename);
			printCommand.setTargetOfCommand(getEditor());
			printCommand.processCommand(null);
			return(true);
			}
		printCommand.setTargetOfCommand(editor);
		try	{
			printCommand.print(
				filename, 
				300,		// 		dotsPerInch, 
				pageManager.getPageSize().getName(),
				pageManager.getDrawingPages().getOrientation() == Mi_VERTICAL ? "Portrait" : "Landscape",
				"COLOR",
				pageManager.getDrawingPages().getPagesWide(), 
				pageManager.getDrawingPages().getPagesTall(), 
				pageManager.getDrawingPages().getBounds());	// boundsToPrint
			}
		catch (Exception e)
			{
			MiToolkit.postErrorDialog(this, "Printing to: " + filename + " failed\n");
			}
		return(true);
		}
	}

class MiGraphicsEditorDragAndDropBehavior extends MiDragAndDropBehavior implements MiiEventTypes
	{
	private		MiEditor	editor;

	public				MiGraphicsEditorDragAndDropBehavior(MiEditor editor)
		{
		this.editor = editor;

		setDragAndCopyPickUpEvent(
			new MiEvent(Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN));
		setDragAndCopyDragEvent(
			new MiEvent(Mi_LEFT_MOUSE_DRAG_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN));
		setDragAndCopyCancelEvent(
			new MiEvent(Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, Mi_ANY_MODIFIERS_HELD_DOWN));
		setDragAndCopyDropEvent(
			new MiEvent(Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN));
		setDragsReferenceNotCopy(true);
		setIsDefaultBehaviorForParts(true);
		setSnapLookCenterToCursor(false);
		setKeepLookCompletelyWithinRootWindow(false);
		}

	public		boolean		isPartDragAndDropSource(MiPart part)
		{
		if (editor.hasLayers())
			return(editor.isLayer(part.getContainer(0)));
		return(editor == part.getContainer(0));
		}
	}

