
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
import java.awt.Frame; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Strings; 

/**----------------------------------------------------------------------------------------------
 * Palette, ...
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorWindow extends MiNativeWindow 
			implements MiiCommandManager, MiiTargetableCommandHandler, MiiCommandNames, MiiMessages, MiiActionHandler
	{

	private		MiEditor		editor;
	private		MiEditorMenuBar		menubar;
	private		MiToolBar		toolbar;
	private		MiPart			palette;
	private		MiPart			editorPanel;
	private		MiPart			statusBar;
	private		MiCommandManager 	cmdWidgetManager = new MiCommandManager();
	private		MiViewMenuCommands 	viewMenuCommands;
	private		MiEditorMenu		editorPopupMenu;
	private		MiRowLayout		containerOfAreaBelowToolbar;
	private		boolean			dockingPanelsDesired;
	private		MiDockingPanel		topDockingPanel;
	private		MiDockingPanel		bottomDockingPanel;
	private		MiDockingPanel		leftDockingPanel;
	private		MiDockingPanel		rightDockingPanel;





					/**------------------------------------------------------
	 				 * Constructs a new MiEditorWindow, creates a new AWT Frame,
					 * and adds this MiEditorWindow, with the given title and
					 * size, to the AWT Frame.
					 * @param title		the window border text
					 * @param screenSize	the size of the window in pixels
					 *------------------------------------------------------*/
	public				MiEditorWindow(
						String title, 
						MiBounds screenSize)
		{
		super(title, screenSize);
		setupMiEditorWindow();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEditorWindow. The caller must then
					 * add this to their Swing or AWT container. For example,
					 * if nativeComponentType == Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE:
					 * <p>
					 * MiEditorWindow gw = new MiEditorWindow(
					 *	frame, null, new MiBounds(0,0,400,400),
					 *	Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE);
					 * jComponent.add("Center", gw.getSwingComponent());
					 * <p>
					 * or, if adding to a JFrame:
					 * <p>
					 * jFrame.getContentPane().add("Center", gw.getSwingComponent());
					 * <p>
					 * And if nativeComponentType == Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE:
					 * <p>
					 * MiEditorWindow gw = new MiEditorWindow(
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
	public				MiEditorWindow(
						Frame frame,
						String title,
						MiBounds screenSize,
						MiJDKAPIComponentType nativeComponentType)
		{
		super(frame, title, screenSize, nativeComponentType);
		setupMiEditorWindow();
		}
					/**------------------------------------------------------
	 				 * Assists the constructors in setting up this MiEditorWindow.
					 *------------------------------------------------------*/
	protected	void		setupMiEditorWindow()
		{
		setDefaultCloseCommand(Mi_QUIT_COMMAND_NAME);

		editor = this;
		makeDefaultWindowBehavior();
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(false));

		// ---------------------------------------------------------------
		// Code to support dockable toolbars.
		// ---------------------------------------------------------------
		setIsDragAndDropTarget(true);
		//appendActionHandler(this, 
			//Mi_DRAG_AND_DROP_MOVE_ACTION);
		appendActionHandler(this, 
			Mi_DATA_IMPORT_ACTION | Mi_REQUEST_ACTION_PHASE);
		appendActionHandler(this, 
			Mi_DATA_IMPORT_ACTION | Mi_EXECUTE_ACTION_PHASE);
		}
					/**------------------------------------------------------
					 * Builds and layouts the window. A number of the methods
					 * that do this can be overridden so that one can customize
					 * the parts:
					 * 		makeDefaultMenuBar
					 * 		makeDefaultToolBar
					 * 		makeDefaultPalette
					 * 		makeDefaultEditorPanel
					 * 	 	makeDefaultStatusBar
					 *------------------------------------------------------*/
	public		void		buildEditorWindow()
		{
		buildEditorWindow(	true,
					true,
					null,
					null,
					null,
					null,
					null);
		}
					/**------------------------------------------------------
					 * Builds and layouts the window. A number of the methods
					 * that do this can be overridden so that one can customize
					 * the layout and parts:
					 * 		makeDefaultLayout
					 * 		makeDefaultMenuBar
					 * 		makeDefaultToolBar
					 * 		makeDefaultPalette
					 * 		makeDefaultEditorPanel
					 * 	 	makeDefaultStatusBar
					 * @param makeDefaultLayout	true if this routine should
					 *				call makeDefaultLayout()
					 * @param makeDefaultPartsIfGivenPartIsNull
					 *				true if this routine should
					 *				call one of the makeDefaultXXX
					 *				routines for every given part
					 *				that is null
					 * @param menubar		the given menubar part or null
					 * @param toolbar		the given toolbar part or null
					 * @param palette		the given palette part or null
					 * @param editorPanel		the given editorPanel part or
					 *				null (the editorPanel is the
					 *				container of the contents in the
					 *				center of this window)
					 * @param statusBar		the given statusBar part or null
					 *------------------------------------------------------*/
	public		void		buildEditorWindow(
						boolean		makeDefaultLayout,
						boolean		makeDefaultPartsIfGivenPartIsNull,
						MiEditorMenuBar menubar,
						MiToolBar 	toolbar,
						MiPart	 	palette,
						MiPart	 	editorPanel,
						MiPart	 	statusBar)
		{
		buildEditorWindow(
			makeDefaultLayout,
			makeDefaultPartsIfGivenPartIsNull,
			makeDefaultPartsIfGivenPartIsNull,
			makeDefaultPartsIfGivenPartIsNull,
			makeDefaultPartsIfGivenPartIsNull,
			makeDefaultPartsIfGivenPartIsNull,
			menubar,
			toolbar,
			palette,
			editorPanel,
			statusBar);
		}
				
					/**------------------------------------------------------
					 * Builds and layouts the window. A number of the methods
					 * that do this can be overridden so that one can customize
					 * the layout and parts:
					 * 		makeDefaultLayout
					 * 		makeDefaultMenuBar
					 * 		makeDefaultToolBar
					 * 		makeDefaultPalette
					 * 		makeDefaultEditorPanel
					 * 	 	makeDefaultStatusBar
					 * @param makeDefaultLayout	true if this routine should
					 *				call makeDefaultLayout()
					 * @param makeDefaultMenuBar 	true if this routine should
					 *				call the makeDefaultMenuBar
					 *				routine if the given menubar
					 *				is null
					 * @param makeDefaultToolBar 	true if this routine should
					 *				call the makeDefaultToolBar
					 *				routine if the given toolbar
					 *				is null
					 * @param makeDefaultPalette 	true if this routine should
					 *				call the makeDefaultPalette
					 *				routine if the given palette
					 *				is null
					 * @param makeDefaultEditorPanel true if this routine should
					 *				call the makeDefaultEditorPanel
					 *				routine if the given editorPanel
					 *				is null
					 * @param makeDefaultStatusBar	true if this routine should
					 *				call the makeDefaultStatusBar
					 *				routine if the given statusBar
					 *				is null
					 * @param menubar		the given menubar part or null
					 * @param toolbar		the given toolbar part or null
					 * @param palette		the given palette part or null
					 * @param editorPanel		the given editorPanel part or
					 *				null (the editorPanel is the
					 *				container of the contents in 
					 *				the center of this window)
					 * @param statusBar		the given statusBar part or
					 *				null
					 *------------------------------------------------------*/
	public synchronized	void	buildEditorWindow(
						boolean		makeDefaultLayout,
						boolean		makeDefaultMenuBar,
						boolean		makeDefaultToolBar,
						boolean		makeDefaultPalette,
						boolean		makeDefaultEditorPanel,
						boolean		makeDefaultStatusBar,
						MiEditorMenuBar menubar,
						MiToolBar 	toolbar,
						MiPart	 	palette,
						MiPart	 	editorPanel,
						MiPart	 	statusBar)
		{
		if ((makeDefaultMenuBar) && (menubar == null))
			menubar = makeDefaultMenuBar();
		this.menubar = menubar;

		if (dockingPanelsDesired)
			{
			topDockingPanel = new MiDockingPanel();
			bottomDockingPanel = new MiDockingPanel();
			leftDockingPanel = new MiDockingPanel(Mi_VERTICAL);
			rightDockingPanel = new MiDockingPanel(Mi_VERTICAL);

			MiDockingPanel[] dockingPanels = new MiDockingPanel[4];
			dockingPanels[MiDockingPanelManager.Mi_BOTTOM_DOCKING_PANEL_INDEX] = bottomDockingPanel;
			dockingPanels[MiDockingPanelManager.Mi_TOP_DOCKING_PANEL_INDEX] = topDockingPanel;
			dockingPanels[MiDockingPanelManager.Mi_LEFT_DOCKING_PANEL_INDEX] = leftDockingPanel;
			dockingPanels[MiDockingPanelManager.Mi_RIGHT_DOCKING_PANEL_INDEX] = rightDockingPanel;
			new MiDockingPanelManager(this, dockingPanels);
			}
		
		if ((makeDefaultToolBar) && (toolbar == null))
			toolbar = makeDefaultToolBar();
		this.toolbar = toolbar;
		if ((toolbar != null) && (toolbar.getDragAndDropBehavior() != null))
			{
			MiParts validTargets = toolbar.getDragAndDropBehavior().getValidTargets();
			if (validTargets == null)
				{
				toolbar.getDragAndDropBehavior().setValidTargets(new MiParts(this));
				}
			else 
				{
				validTargets.addElement(this);
				toolbar.getDragAndDropBehavior().setValidTargets(validTargets);
				}
			}

		if (viewMenuCommands != null)
			viewMenuCommands.setToolBar(toolbar);

		if ((makeDefaultPalette) && (palette == null))
			palette = makeDefaultPalette();
		this.palette = palette;

		if ((makeDefaultEditorPanel) && (editorPanel == null))
			editorPanel = makeDefaultEditorPanel();
		this.editorPanel = editorPanel;

		makeDefaultEditorBehavior(editor);

		if ((makeDefaultStatusBar) && (statusBar == null))
			statusBar = makeDefaultStatusBar();
		this.statusBar = statusBar;
		if (viewMenuCommands != null)
			viewMenuCommands.setStatusBar(statusBar);

		if (makeDefaultLayout)
			makeDefaultLayout(editorPanel, menubar, toolbar, palette, statusBar);
		}
					/**------------------------------------------------------
	 				 * Sets the editor that the menubar, statusbar and toolbar
					 * and (3rd button) background menu act on and report about.
					 * Needs work to support multiple simulatneous editors in 
					 * this one window. Note: this editor is not added to the
					 * MiEditorWindow by this method.
					 * @param editor 	the new editor
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		void		setEditor(MiEditor editor)
		{
		this.editor = editor;
		if (menubar != null)
			menubar.setEditor(editor);
		if (toolbar != null)
			toolbar.setEditor(editor);
		if (statusBar != null)
			{
			if (statusBar instanceof MiStatusBar)
				((MiStatusBar )statusBar).setTargetEditor(editor);
			if (statusBar instanceof MiEditorStatusBar)
				((MiEditorStatusBar )statusBar).setTargetEditor(editor);
			}

		if ((editorPopupMenu == null)) // || (getEventHandlerWithClass("MiIDisplayContextMenu") == null))
			{
			if (editorPopupMenu == null)
				editorPopupMenu = new MiEditorBackgroundMenu(this);
			}
		editorPopupMenu.setEditor(editor);
		editor.setContextMenu(editorPopupMenu);
		}
					/**------------------------------------------------------
	 				 * Gets the editor that the menubar, statusbar and toolbar
					 * and (3rd button) background menu act on and report about.
					 * @return  		the new editor
					 * @see			#setEditor
					 *------------------------------------------------------*/
	public		MiEditor	getEditor()
		{
		return(editor);
		}
					/**------------------------------------------------------
	 				 * This method is called to create the graphics for this
					 * MiWindow when this window is first created. This 
					 * approach to delaying the creation of graphics until
					 * after the window is created is optional, except when
					 * an query or error message is popped up, in which case
					 * this method will use the correct thread.
					 * @overrides		MiWindow#createGraphicsContents
					 *------------------------------------------------------*/
	public	synchronized	void	createGraphicsContents()
		{
		}

	//***************************************************************************************
	// Access routines to the major parts of the window.
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Sets whether the buildEditorWindow() method should
					 * build MiDockingPanels for all 4 sides of the windows.
					 * @param flag		true if the buildEditorWindow()
					 *			method should make docking panels
					 * @see			#buildEditorWindow
					 *------------------------------------------------------*/
	public		void		setBuildDockingPanels(boolean flag)
		{
		dockingPanelsDesired = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether the buildEditorWindow() method should
					 * build MiDockingPanels for all 4 sides of the windows.
					 * @return 		true if the buildEditorWindow()
					 *			method should make docking panels
					 * @see			#buildEditorWindow
					 *------------------------------------------------------*/
	public		boolean		getBuildDockingPanels()
		{
		return(dockingPanelsDesired);
		}
					/**------------------------------------------------------
	 				 * Gets the toolbar of this window.
					 * @return		the toolbar or null, if none
					 *------------------------------------------------------*/
	public		MiToolBar	getToolBar()
		{
		return(toolbar);
		}
					/**------------------------------------------------------
	 				 * Gets the palette of this window.
					 * @return		the palette or null, if none
					 *------------------------------------------------------*/
	public		MiPart		getPalette()
		{
		return(palette);
		}
					/**------------------------------------------------------
	 				 * Sets the palette of this window. This will assign the
					 * given palette to this editor, replacing the current one,
					 * if any. If the given palette is null then this MiEditorWindow
					 * will have no palette.
					 * @param palette	the new palette or null
					 *------------------------------------------------------*/
	public		void		setPalette(MiPart palette)
		{
		if (palette == null)
			{
			if (this.palette == null)
				return;

			MiPart container = editorPanel.getContainer(0);
			if (container instanceof MiLayout)
				{
				// ---------------------------------------------------------------
				// Remove the old palette
				// ---------------------------------------------------------------
				int uniqueIndex = ((MiLayout )container).getUniqueElementIndex();
				container.removePart(this.palette);
				((MiLayout )container).setUniqueElementIndex(Math.max(0, uniqueIndex - 1));
				}
			}
		else 
			{
			if (this.palette != null)
				{
				// ---------------------------------------------------------------
				// Replace the old palette with the given palette
				// ---------------------------------------------------------------
				this.palette.replaceSelf(palette);
				}
			else
				{
				// ---------------------------------------------------------------
				// Add the given palette
				// ---------------------------------------------------------------
				MiPart container = editorPanel.getContainer(0);
				if (container instanceof MiLayout)
					{
					int uniqueIndex = ((MiLayout )container).getUniqueElementIndex();
					container.insertPart(palette, 0);
					((MiLayout )container).setUniqueElementIndex(uniqueIndex + 1);
					}
				}
			}
		this.palette = palette;
		}
					/**------------------------------------------------------
	 				 * Gets the status bar of this window.
					 * @return		the status bar or null, if none
					 *------------------------------------------------------*/
	public		MiPart		getStatusBar()
		{
		return(statusBar);
		}
					/**------------------------------------------------------
	 				 * Gets the menu bar of this window.
					 * @return		the menu bar or null, if none
					 *------------------------------------------------------*/
	public		MiEditorMenuBar	getMenuBar()
		{
		return(menubar);
		}
					/**------------------------------------------------------
	 				 * Sets the editor panel of this window.
					 * @param		the editor panel
					 *------------------------------------------------------*/
	public		void		setEditorPanel(MiPart panel)
		{
		editorPanel.replaceSelf(panel);
		editorPanel = panel;
		}
					/**------------------------------------------------------
	 				 * Gets the editor panel of this window.
					 * @return		the editor panel or null, if none
					 *------------------------------------------------------*/
	public		MiPart		getEditorPanel()
		{
		return(editorPanel);
		}
					/**------------------------------------------------------
	 				 * Gets the popup menu for this window. 
					 * @return		the popup menu
					 *------------------------------------------------------*/
	public		MiEditorMenu	getPopupMenu()
		{
		return(editorPopupMenu);
		}
					/**------------------------------------------------------
	 				 * Sets the popup menu for this window. 
					 * @param menu		the popup menu
					 *------------------------------------------------------*/
	public		void		setPopupMenu(MiEditorMenu menu)
		{
		editorPopupMenu = menu;
		editorPopupMenu.setEditor(editor);
		editor.setContextMenu(editorPopupMenu);
		}
					/**------------------------------------------------------
	 				 * Gets the menubar button that popups the given pulldown
					 * menu
					 * @return		the menubar button
					 *------------------------------------------------------*/
	public		MiMenuLauncherButton	getMenuBarButton(MiEditorMenu menu)
		{
		return(menubar.getMenuLauncherButton(menu.getMenu()));
		}
					/**------------------------------------------------------
	 				 * Gets the docking panel (obstensibly for toolbars) at
					 * the top of the window.
					 * @return		the top docking panel
					 *------------------------------------------------------*/
	public		MiDockingPanel	getTopDockingPanel()
		{
		return(topDockingPanel);
		}
					/**------------------------------------------------------
	 				 * Gets the docking panel (obstensibly for toolbars) at
					 * the bottom of the window.
					 * @return		the bottom docking panel
					 *------------------------------------------------------*/
	public		MiDockingPanel	getBottomDockingPanel()
		{
		return(bottomDockingPanel);
		}
					/**------------------------------------------------------
	 				 * Gets the docking panel (obstensibly for toolbars) at
					 * the left of the window.
					 * @return		the left docking panel
					 *------------------------------------------------------*/
	public		MiDockingPanel	getLeftDockingPanel()
		{
		return(leftDockingPanel);
		}
					/**------------------------------------------------------
	 				 * Gets the docking panel (obstensibly for toolbars) at
					 * the right of the window.
					 * @return		the right docking panel
					 *------------------------------------------------------*/
	public		MiDockingPanel	getRightDockingPanel()
		{
		return(rightDockingPanel);
		}

	//***************************************************************************************
	// Make default parts and behavior
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Makes the default window behavior. This default behavior
					 * is the assignment of event handlers to the window to add
					 * support for:
					 *   context sensitive cursor appearances
					 *   context sensitive tool hints
					 *   context sensitive dialog box help
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 *------------------------------------------------------*/
	protected	void		makeDefaultWindowBehavior()
		{
		appendEventHandler(new MiIDisplayHelpDialog());
		}
					/**------------------------------------------------------
	 				 * Makes the default editor behavior. This default behavior
					 * is to do nothing.
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 *------------------------------------------------------*/
	protected	void		makeDefaultEditorBehavior(MiEditor editor)
		{
		}
					/**------------------------------------------------------
	 				 * Makes the default menubar. This default menubar is 
					 * assigned eight pulldown menus (file, edit, view, shape, 
					 * connect, format, tools and help.
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 * @return		the menubar
					 *------------------------------------------------------*/
	protected	MiEditorMenuBar	makeDefaultMenuBar()
		{
		MiEditorMenu[] pulldowns = new MiEditorMenu[8];
		pulldowns[0] = new MiFileMenu(this, this);
		pulldowns[1] = new MiEditMenu(this);
		viewMenuCommands = new MiViewMenuCommands(this);
		pulldowns[2] = new MiViewMenu(viewMenuCommands, this);
		pulldowns[3] = new MiShapeMenu(this);
		pulldowns[4] = new MiConnectMenu(this);
		pulldowns[5] = new MiFormatMenu(this);
		pulldowns[6] = new MiToolsMenu(this);
		pulldowns[7] = new MiHelpMenu(this);
		return(new MiEditorMenuBar(pulldowns));
		}
					/**------------------------------------------------------
	 				 * Makes the default editor panel. This default editor panel
					 * is an MiEditor inside of a MiScrolledBox.
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 * @return		the scrolled box
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultEditorPanel()
		{
		editor = new MiEditor();
		MiScrolledBox scrolledBox = new MiScrolledBox(editor);
		editor.setDeviceBounds(new MiBounds(0,0,400,400));
		editor.setPreferredSize(new MiSize(400,400));
		editor.setMinimumSize(new MiSize(100,100));
		setEditor(editor);

		return(scrolledBox);
		}
					/**------------------------------------------------------
	 				 * Makes the default toolbar. This default toolbar is 
					 * the MiEditorToolBar.
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 * @return		the toolbar
					 *------------------------------------------------------*/
	protected	MiToolBar	makeDefaultToolBar()
		{
		return(new MiEditorToolBar(this, editor, this));
		}
					/**------------------------------------------------------
	 				 * Makes the default palette. This default palette is 
					 * null.
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 * @return		the palette
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultPalette()
		{
		return(null);
		}
					/**------------------------------------------------------
	 				 * Makes the default status bar. This default status bar is 
					 * the MiEditorStatusBar.
					 * @return		the status bar
					 *------------------------------------------------------*/
	protected	MiPart		makeDefaultStatusBar()
		{
		return(new MiEditorStatusBar(this, editor, editor, editor));
		}
					/**------------------------------------------------------
					 * Arranges all of the parts in the window by creating 
					 * layouts and then setting the layout of the main window
					 * itself.
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 * @param editorPanel 	the editorPanel
					 * @param menubar 	the menubar
					 * @param toolbar 	the toolbar
					 * @param palette 	the palette
					 * @param statusBar 	the statusBar
					 *------------------------------------------------------*/
	protected	void		makeDefaultLayout(
						MiPart editorPanel, 
						MiPart menubar, 
						MiPart toolbar, 
						MiPart palette, 
						MiPart statusBar)
		{
		MiColumnLayout 		columnLayout 	= new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setElementVJustification(Mi_TOP_JUSTIFIED);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		int index = (menubar != null) ? 2 : 0;
		//columnLayout.setUniqueElementIndex(index);

		containerOfAreaBelowToolbar 	= new MiRowLayout();
		containerOfAreaBelowToolbar.setElementVSizing(Mi_EXPAND_TO_FILL);
		containerOfAreaBelowToolbar.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		//containerOfAreaBelowToolbar.setUniqueElementIndex(1);
		
		if (menubar != null)
			appendPart(menubar);

		if (topDockingPanel != null)
			appendPart(topDockingPanel);

		if (toolbar != null)
			{
			if (topDockingPanel != null)
				topDockingPanel.appendPart(toolbar);
			else
				appendPart(toolbar);
			}

		columnLayout.setUniqueElementIndex(getNumberOfParts());
		appendPart(containerOfAreaBelowToolbar);

		if (leftDockingPanel != null)
			containerOfAreaBelowToolbar.appendPart(leftDockingPanel);

		if (palette != null)
			{
			if (leftDockingPanel != null)
				leftDockingPanel.appendPart(palette);
			else
				containerOfAreaBelowToolbar.appendPart(palette);
			}

		containerOfAreaBelowToolbar.setUniqueElementIndex(containerOfAreaBelowToolbar.getNumberOfParts());
		containerOfAreaBelowToolbar.appendPart(editorPanel);

		if (rightDockingPanel != null)
			containerOfAreaBelowToolbar.appendPart(rightDockingPanel);

		if (bottomDockingPanel != null)
			appendPart(bottomDockingPanel);

		if (statusBar != null)
			appendPart(statusBar);

		setLayout(columnLayout);
		}
	public		MiPart		getContainerOfAreaBelowToolbar()
		{
		return(containerOfAreaBelowToolbar);
		}


					/**------------------------------------------------------
	 				 * Sets the target of the commands processed by this 
					 * MiiCommandHandler (i.e. what the commands are to act upon).
					 * This method does nothing as getTargetOfCommand() always
					 * returns the current editor.
					 * @param target 	the target
					 * @see			#getTargetOfCommand
					 * @see			#getEditor
					 * @see			MiNativeWindow#processCommand
					 * @implements		
					 *		MiiCommandManager#setTargetOfCommand
					 *------------------------------------------------------*/
	public		void		setTargetOfCommand(Object target)
		{
		}
					/**------------------------------------------------------
	 				 * Gets the target of the commands processed by this 
					 * MiiCommandHandler (i.e. what the commands are to act upon).
					 * @return  		the target (the current editor)
					 * @see			#getEditor
					 * @see			MiNativeWindow#processCommand
					 * @implements		
					 *		MiiCommandManager#getTargetOfCommand
					 *------------------------------------------------------*/
	public		Object		getTargetOfCommand()
		{
		return(editor);
		}
	//***************************************************************************************
	// Implementation of MiiCommandManager
	//***************************************************************************************
					/**------------------------------------------------------
					 * Registers the given widget and the command it generates.
					 * This permits (de)sensitization of the widget by the
					 * setCommandSensitivity method. 
					 * @param widget  	the widget that generates the command
					 * @param command 	the command
					 * @implements		
					 *	MiiCommandManager#registerCommandDependentWidget
					 *------------------------------------------------------*/
	public		void		registerCommandDependentWidget(MiPart widget, String cmd)
		{
		cmdWidgetManager.registerCommandDependentWidget(widget, cmd);
		}
					/**------------------------------------------------------
					 * UnRegisters the given widget and the command it generates.
					 * Either the given widget or the given command may be null.
					 * @param widget  	the widget that generates the command
					 * @param command 	the command
					 * @implements		
					 *	MiiCommandManager#unRegisterWidgetGeneratedCommand
					 *------------------------------------------------------*/
	public		void		unRegisterWidgetGeneratedCommand(MiPart widget, String cmd)
		{
		cmdWidgetManager.unRegisterWidgetGeneratedCommand(widget, cmd);
		}
					/**------------------------------------------------------
					 * Sets whether the given command is to be visible to the
					 * user. This method typically (hides)shows the widgets 
					 * that have been associated with the command.
					 * @param command  	the command
					 * @param flag 		true if the user can now see the
					 *			command (whether sensitive or not)
					 * @see			#registerCommandDependentWidget
					 * @implements		
					 *	MiiCommandManager#setCommandVisibility
					 *------------------------------------------------------*/
	public		void		setCommandVisibility(String command, boolean flag)
		{
		cmdWidgetManager.setCommandVisibility(command, flag);
		}
					/**------------------------------------------------------
					 * Sets whether the given command can be processed at this
					 * time. Otherwise, possibly based on the current state of
					 * the application, the given command cannot/should not be
					 * processed at this time. This method typically (de)sensitizes
					 * the widgets that have been associated with the command
					 * (see registerCommandDependentWidget).
					 * @param command  	the command
					 * @param flag 		true if the system can now process
					 *			the command
					 * @see			#registerCommandDependentWidget
					 * @implements		
					 *	MiiCommandManager#setCommandSensitivity
					 *------------------------------------------------------*/
	public		void		setCommandSensitivity(String command, boolean flag)
		{
//if (Mi_SAVE_COMMAND_NAME.equals(command))
//MiDebug.printStackTrace(this + ".setCommandSensitivity SAVE to : " + flag);
		cmdWidgetManager.setCommandSensitivity(command, flag);
		}
					/**------------------------------------------------------
					 * Sets whether the given command can be processed at this
					 * time. Otherwise, possibly based on the current state of
					 * the application, the given command cannot/should not be
					 * processed at this time. This method typically (de)sensitizes
					 * the widgets that have been associated with the command
					 * (see registerCommandDependentWidget). This method is designed
					 * to display a status bar message that describes why a 
					 * particular widget is desensitized.
					 * @param command  	the command
					 * @param flag 		true if the system can now process
					 *			the command
					 * @param statusHelpMsg the new status bar message
					 * @see			#registerCommandDependentWidget
					 * @implements		
					 *	MiiCommandManager#setCommandSensitivity
					 *------------------------------------------------------*/
	public		void		setCommandSensitivity(String command, boolean flag, String statusHelpMsg)
		{
		cmdWidgetManager.setCommandSensitivity(command, flag, statusHelpMsg);
		}
					/**------------------------------------------------------
					 * Sets the state of the boolean widget that generates the 
					 * given command. This is used for initializing toggle 
					 * buttons.
					 * @param command  	the command
					 * @param flag 		true if the two state widget is
					 *			to be 'set'.
					 * @implements
					 *	MiiCommandManager#setCommandState
					 *------------------------------------------------------*/
	public		void		setCommandState(String command, boolean flag)
		{
		cmdWidgetManager.setCommandState(command, flag);
		}
					/**------------------------------------------------------
					 * Sets the state of the multi-state widget that generates
					 * the given command. This is used for initializing widgets 
					 * like combo boxes and lists.
					 * @param command  	the command
					 * @param state		the current state, one of many.
					 * @implements
					 *	MiiCommandManager#setCommandState
					 *------------------------------------------------------*/
	public		void		setCommandState(String command, String state)
		{
		cmdWidgetManager.setCommandState(command, state);
		}
					/**------------------------------------------------------
					 * Sets the label of the widget that generates the given 
					 * command. This is used for initializing widgets like
					 * labels and menubar buttons, push button etc.
					 * @param command  	the command
					 * @param label		the new label
					 * @implements
					 *	MiiCommandManager#setCommandLabel
					 *------------------------------------------------------*/
	public		void		setCommandLabel(String command, String label)
		{
		cmdWidgetManager.setCommandLabel(command, label);
		}
					/**------------------------------------------------------
					 * Sets the values of the multi-state widget that generates
					 * the given command. This is used for setting the possible
					 * values of widgets like combo boxes and lists.
					 * @param command  	the command
					 * @param options	the new contents of the widget
					 * @implements
					 *	MiiCommandManager#setCommandOptions
					 *------------------------------------------------------*/
	public		void		setCommandOptions(String command, Strings options)
		{
		cmdWidgetManager.setCommandOptions(command, options);
		}
					/**------------------------------------------------------
					 * Sets the given property to the given value for all parts
					 * that generates the given command. This is used for, say,
					 * changing the status help message on a widget that may
					 * only be situationally and temporarily desensitized.
					 * @param command  	the command
					 * @param propertyName	the property name
					 * @param propertyValue	the value
					 * @implements		MiiCommandManager#setCommandState
					 *------------------------------------------------------*/
	public		void		setCommandPropertyValue(String command, String propertyName, String propertyValue)
		{
		cmdWidgetManager.setCommandPropertyValue(command, propertyName, propertyValue);
		}


	//***************************************************************************************
	// Implementation of MiiActionHandler
	//***************************************************************************************

					/**------------------------------------------------------
					 * Processes the given action.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 			false if it is NOT OK to send
					 *			action to the next action handler
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		return(true);
		}
	}

