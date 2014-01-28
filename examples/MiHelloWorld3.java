
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

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a simple window with a label widget that displays
 * "Hello" rotating around a graphics node that has a green ball
 * as an image and displays "World". Because the window inherits
 * from MiEditorWindow, a menubar, toolbar, and status bar can 
 * easily be added.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiHelloWorld3 extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiHelloWorld3Window. Supported command line parameters are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String title = Utility.getCommandLineArgument(args, "-title");
		MiHelloWorld3Window window = new MiHelloWorld3Window(
			title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiHelloWorld3Window. Supported html file parameters are:
					 * title	the window border title
					 * width	the window width
					 * height	the window height
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);

		String title = getParameter("title");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiHelloWorld3Window window = new MiHelloWorld3Window(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a simple window with a label widget that displays
 * "Hello" rotating around a graphics node that has a green ball
 * as an image and displays "World". Because the window inherits
 * from MiEditorWindow, a menubar, toolbar, and status bar can 
 * easily be added.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.00(Alpha)
 * @module 	%M%
 * @language	Java (JDK 1.02)
 *----------------------------------------------------------------------------------------------*/
class MiHelloWorld3Window extends MiEditorWindow implements MiiSimpleAnimator
	{
	private		MiGraphicsNode	worldNode;
	private		double		baseRotationSpeed 	= Math.PI/10;

					/**------------------------------------------------------
	 				 * Constructs a new MiHelloWorld3Window and adds it to the given
					 * AWT container.
					 * @param awtContainer	the AWT container this is going into
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiHelloWorld3Window(
						Container awtContainer, String title, MiBounds screenSize)
		{
		// ---------------------------------------------------------------
		// Tell the super class to create a Canvas in the given awtContainer
		// with the given title (or, if null, "Hello World"), with the given
		// size.
		// ---------------------------------------------------------------
		super(MiUtility.getFrame(awtContainer), 
			title != null ? title : "Hello World", 
			screenSize,
			Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE);

		// ---------------------------------------------------------------
		// Add the newly created Canvas to the given awtContainer.
		// ---------------------------------------------------------------
		awtContainer.add("Center", (java.awt.Component )getCanvas().getNativeComponent());

		// ---------------------------------------------------------------
		// Create the contents and behavior of this window.
		// ---------------------------------------------------------------
		setup();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiHelloWorld3Window as a stand-alone
					 * window.
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiHelloWorld3Window(String title, MiBounds screenSize)
		{
		// ---------------------------------------------------------------
		// Tell the super class to create a Window with the given title 
		// (or, if null, "Hello World"), with the given size.
		// ---------------------------------------------------------------
		super(title != null ? title : "Hello World", screenSize);

		// ---------------------------------------------------------------
		// Create the contents and behavior of this window.
		// ---------------------------------------------------------------
		setup();
		}
					/**------------------------------------------------------
					 * Create 'hello' and 'world' nodes and connect them together.
					 *------------------------------------------------------*/
	protected	void		setup()
		{
		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// Create the contents and layout of this window. In this case,
		// just create and editor and scrolled editor panel.
		// ---------------------------------------------------------------
		buildEditorWindow(	true, 	// makeDefaultLayout
					false,	// makeDefaultMenuBar
					false,	// makeDefaultToolBar
					false,	// makeDefaultPalette
					true,	// makeDefaultEditorPanel
					false,	// makeDefaultStatusBar
					null,	// menubar
					null,	// toolbar
					null,	// palette
					null,	// editorPanel
					null);	// statusBar

		// ---------------------------------------------------------------
		// Make the window double buffered, if desired.
		// ---------------------------------------------------------------
		//getRootWindow().getCanvas().setDoubleBuffered(true);

		// ---------------------------------------------------------------
		// Get the editor created above by buildEditorWindow().
		// ---------------------------------------------------------------
		MiEditor editor = getEditor();

		// ---------------------------------------------------------------
		// Add interactivity to the display ...
		// ---------------------------------------------------------------
		editor.appendEventHandler(new MiISelectObjectUnderMouse());
		editor.appendEventHandler(new MiIDragBackgroundPan());
		editor.appendEventHandler(new MiISelectArea());
		editor.appendEventHandler(new MiIDragObjectUnderMouse());
		editor.appendEventHandler(new MiIZoomAroundMouse());
		editor.appendEventHandler(new MiIPan());
		editor.appendEventHandler(new MiIJumpPan());
		editor.appendEventHandler(new MiIZoomArea());
		editor.appendEventHandler(new MiIDragSelectedObjects());

		// ---------------------------------------------------------------
		// Set the background color of the editor
		// ---------------------------------------------------------------
		editor.setBackgroundColor(MiColorManager.veryLightGray);

		// ---------------------------------------------------------------
		// Create the graphics node that will be the 'world'.
		// ---------------------------------------------------------------
		worldNode = new MiGraphicsNode(
				new MiImage("${Mi_HOME}/apps/images/green-ball.gif", true),
				Mi_ABOVE, false, true, "World");

		// ---------------------------------------------------------------
		// Create the label widget that will be the 'hello'.
		// ---------------------------------------------------------------
		MiPart helloNode = new MiLabel("Hello");
		helloNode.setBorderLook(Mi_RAISED_BORDER_LOOK);
		
		// ---------------------------------------------------------------
		// Add the graphics node and label to the editor.
		// ---------------------------------------------------------------
		editor.appendPart(worldNode);
		editor.appendPart(helloNode);

		// ---------------------------------------------------------------
		// Make and add a connection that connects the graphics node and label
		// together.
		// ---------------------------------------------------------------
		editor.appendPart(new MiConnection(helloNode, worldNode));

		// ---------------------------------------------------------------
		// Move the world node to the center of the editor and move the hello
		// label 100 pixels away from the world.
		// ---------------------------------------------------------------
		worldNode.setCenter(editor.getWorldBounds().getCenter());
		helloNode.setCenter(worldNode.getCenterX() + 100, worldNode.getCenterY());

		// ---------------------------------------------------------------
		// Tell the animation manager for this window to call the 'animate'
		// method.
		// ---------------------------------------------------------------
		getAnimationManager().addAnimator(this);
		}
					/**------------------------------------------------------
	 				 * Animate the rotation of all parts (usually just the
					 * 'hello' part) around the world part.
					 * @return		the number of millisecs until the
					 *			next time this is called (-1 
					 *			terminates the animation).
					 *------------------------------------------------------*/
	public		long		animate()
		{
		// ---------------------------------------------------------------
		// Get the editor...
		// ---------------------------------------------------------------
		MiEditor editor = getEditor();

		// ---------------------------------------------------------------
		// For all parts in the editor...
		// ---------------------------------------------------------------
		for (int i = 0; i < editor.getNumberOfParts(); ++i)
			{
			// ---------------------------------------------------------------
			// Get a part...
			// ---------------------------------------------------------------
			MiPart part = editor.getPart(i);

			// ---------------------------------------------------------------
			// If this part is not the 'world' and not a connection...
			// ---------------------------------------------------------------
			if ((part != worldNode) && (!(part instanceof MiConnection)))
				{
				// ---------------------------------------------------------------
				// Get the distance between the world and this part...
				// ---------------------------------------------------------------
				MiDistance radius = Math.sqrt(
					worldNode.getCenter().getDistanceSquared(part.getCenter()));

				// ---------------------------------------------------------------
				// ... and the angle in radians...
				// ---------------------------------------------------------------
				double angle = Utility.getAngle(
					part.getCenterY() - worldNode.getCenterY(),
					part.getCenterX() - worldNode.getCenterX());

				// ---------------------------------------------------------------
				// ... and increase the angle based on a speed that is proportional
				// to the part's distance from the world node...
				// ---------------------------------------------------------------
				if (radius != 0)
					angle += baseRotationSpeed * 100/radius;

				// ---------------------------------------------------------------
				// .. and update the position of the part at the location at the
				// new angle.
				// ---------------------------------------------------------------
				part.setCenter(
					worldNode.getCenterX() + radius * Math.cos(angle),
					worldNode.getCenterY() + radius * Math.sin(angle));
				}
			}
		// ---------------------------------------------------------------
		// Call this method again in 50 milliseconds (1/20 second).
		// ---------------------------------------------------------------
		return(50);
		}
	}

