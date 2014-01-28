

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

package examples;

import com.swfm.mica.*; 
import com.swfm.mica.util.Utility;
import java.awt.Container;
import java.applet.Applet;
import java.io.*;

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a window, if this is an application, or a canvas, if
 * this is an applet, that has a menubar, statusbar, toolbar, etc.
 * and is then loaded with the graphics from the "awt_hier.grml"
 * file.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiExample extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiExampleWindow. Supported command line parameters are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);
		String title = Utility.getCommandLineArgument(args, "-title");
		MiExampleWindow window = new MiExampleWindow(title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiExampleWindow. Supported html file parameters are:
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);
		String title = getParameter("title");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiExampleWindow window = new MiExampleWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a window or a canvas that has a menubar, statusbar,
 * toolbar, etc. and is then loaded with the graphics from the 
 * "awt_hier.grml" file.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.00(Alpha)
 * @module 	%M%
 * @language	Java (JDK 1.02)
 *----------------------------------------------------------------------------------------------*/
class MiExampleWindow 
	{
	private		MiEditorWindow 	editorWindow;
	private		MiGraphicsEditor graphEditor;


					/**------------------------------------------------------
	 				 * Constructs a new MiExampleWindow and adds it to the given
					 * AWT container.
					 * @param awtContainer	the AWT container this is going into
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiExampleWindow(
						Container awtContainer, String title, MiBounds screenSize)
		{
		// ---------------------------------------------------------------
		// Tell the super class to create a Canvas in the given awtContainer
		// with the given title (or, if null, "Hello World"), with the given
		// size.
		// ---------------------------------------------------------------
		editorWindow = new MiEditorWindow(
			MiUtility.getFrame(awtContainer), 
			title != null ? title : "Hello World", 
			screenSize,
			MiiTypes.Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);

		// ---------------------------------------------------------------
		// Create the contents and behavior of this window.
		// ---------------------------------------------------------------
		setup(awtContainer);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiExampleWindow and creates and adds it
					 * to a AWT Frame
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiExampleWindow(String title, MiBounds screenSize)
		{
		// ---------------------------------------------------------------
		// Tell the super class to create a Window with the given title 
		// (or, if null, "Hello World"), with the given size.
		// ---------------------------------------------------------------
		editorWindow = new MiEditorWindow(title != null ? title : "Hello World", screenSize);

		// ---------------------------------------------------------------
		// Create the contents and behavior of this window.
		// ---------------------------------------------------------------
		setup(null);
		}
					/**------------------------------------------------------
					 * Create a graphics editor and status bar.
					 * @param myPanel	the awtContainer, possibly null,
					 *			in which to put the graphics editor.
					 *------------------------------------------------------*/
	protected	void		setup(Container myPanel)
		{
		// ---------------------------------------------------------------
		// Create a graphics editor inside a scrolled box...
		// ---------------------------------------------------------------
        	graphEditor = new MiGraphicsEditor();
        	MiScrolledBox scrolledBox = new MiScrolledBox(graphEditor);
        	graphEditor.setDeviceBounds(new MiBounds(0,0,400,400));
        	graphEditor.setPreferredSize(new MiSize(400,400));
        	graphEditor.setMinimumSize(new MiSize(100,100));

		// ---------------------------------------------------------------
		// Tell the window what it's graphics editor is...
		// ---------------------------------------------------------------
        	editorWindow.setEditor(graphEditor);

		// ---------------------------------------------------------------
		// Make the size of the window to be greater than or equal to the
		// size of all of the contents.
		// ---------------------------------------------------------------
        	editorWindow.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// Buld the contents of the window...
		// ---------------------------------------------------------------
        	editorWindow.buildEditorWindow(
				true, 					// makeDefaultLayout,
				false, 					// makeDefaultPartsIfGivenPartIsNull,
				null, 					// menubar,
				null, 					// toolbar,
				null,					// palette,
				scrolledBox,				// editorPanel,
				new MiEditorStatusBar(editorWindow, 	// statusBar
					graphEditor, graphEditor, graphEditor)
				);

		// ---------------------------------------------------------------
		// Tell the graphics editor to draw connections before it draws nodes
		// so that nodes appear in front of all connections.
		// ---------------------------------------------------------------
        	editorWindow.getEditor().setKeepConnectionsBelowNodes(true);

		// ---------------------------------------------------------------
		// Display the name of any graphics under the mouse in the status bar.
		// ---------------------------------------------------------------
        	editorWindow.getEditor().appendEventHandler( new
	        	MiSetStatusBarStateToNameOfObjectUnderMouse((MiEditorStatusBar)editorWindow.getStatusBar()));

		// ---------------------------------------------------------------
		// If myPanel is not null, then add the window to the center of myPanel
		// ---------------------------------------------------------------
		if (myPanel != null)
			myPanel.add("Center", (java.awt.Canvas )editorWindow.getCanvas().getNativeComponent());
   
		// ---------------------------------------------------------------
		// Display the window.
		// ---------------------------------------------------------------
		editorWindow.setVisible(true);
        	try
       			{
			// ---------------------------------------------------------------
			// Load the graphics file: "awt_hier.grml"
			// ---------------------------------------------------------------
			editorWindow.getAccessLock();
        		graphEditor.load(
				new BufferedInputStream(MiSystem.getResourceAsStream("examples/awt_hier.grml")),
				"awt_hier.grml");
			editorWindow.freeAccessLock();
    	   		}
		catch(IOException e)
			{
			e.printStackTrace();
			System.exit(1);
			}
		}
	}
