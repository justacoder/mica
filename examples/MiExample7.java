
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
import com.swfm.mica.util.Strings;
import java.awt.Container;
import java.awt.Panel;
import java.awt.Button;
import java.awt.Label;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import java.applet.Applet;
import java.io.*;

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a Swing JFrame window, if this is an application, or uses the
 * existing window, if this is an applet. Then populates the window
 * with a Swing JButton, JLabel, and a graphics editor with menubar,
 * toolbar, status bar, etc...
 * <p>
 * This is the same as MiExample3 but uses a JFrame instead of a Frame.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiExample7 extends Applet
{
	static 		boolean		noDoubleBufferingFlag;

					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * TestWindow. Supported command line parameters are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		MiSystem.init();
		String title = Utility.getCommandLineArgument(args, "-title");

		JFrame swingWindow = new JFrame();
		swingWindow.getContentPane().setLayout(new BorderLayout());
		swingWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		JButton button = new JButton("Swing Button");
		swingWindow.getContentPane().add(button, BorderLayout.NORTH);

		MiGraphicsWindow window = new MiGraphicsWindow(
			swingWindow, 
			title != null ? title : "Demo", 
			new MiBounds(0.0,0.0,500.0,500.0),
			MiiTypes.Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE);

		swingWindow.getContentPane().add(window.getSwingComponent(), BorderLayout.CENTER);

		JLabel label = new JLabel("Swing Label");
		swingWindow.getContentPane().add(label, BorderLayout.SOUTH);

		swingWindow.pack();
		swingWindow.setVisible(true);
		window.getAccessLock();
		//window.openDefaultFile();
		window.setVisible(true);

		MiLabel testLabel = new MiLabel("Test Label");
		window.getEditor().appendPart(testLabel);
		testLabel.setCenter(window.getEditor().getWorldBounds().getCenter());

		CustomEditorBackgroundMenu menu = new CustomEditorBackgroundMenu(window);
		// ---------------------------------------------------------------
		// Assign our custom menu to the editor
		// ---------------------------------------------------------------
		window.setPopupMenu(menu);

		// ---------------------------------------------------------------
		// Assign our custom menu to the testLabel so that if many items are
		// selected then if the mouse cursor is above this we view *its* properties
		// ---------------------------------------------------------------
		testLabel.setContextMenu(menu);

		// ---------------------------------------------------------------
		// Make sure we recieve 'properties...' actions for all parts in the
		// editor (these are generated by the editor 'background popup menu').
		// ---------------------------------------------------------------
		window.getEditor().appendActionHandler(new Example7Instance(window), 
			MiiActionTypes.Mi_DISPLAY_PROPERTIES_ACTION | MiiActionTypes.Mi_ACTIONS_OF_PARTS_OF_OBSERVED);

		window.freeAccessLock();
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * TestWindow. Supported html file parameters are:
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		MiSystem.init(this);
		String title = getParameter("title");
		add("North", new Button("AWT Button"));
		add("South", new Label("AWT Label"));
		Panel panel = new Panel();
		add("Center", panel);
		panel.setLayout(new BorderLayout());
		MiGraphicsWindow window = new MiGraphicsWindow(
			MiUtility.getFrame(panel),
			title != null ? title : "Demo", 
			new MiBounds(0.0,0.0,500.0,500.0),
			MiiTypes.Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE);
		window.setVisible(true);
		MiUtility.getFrame(this).pack();
		}
	}
class Example7Instance implements MiiActionHandler, MiiTypes, MiiActionTypes
	{
	MiGraphicsWindow 		window;

	public				Example7Instance(MiGraphicsWindow window)
		{
		this.window = window;
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are:
					 * <pre>
					 *    HELLO_WORLD_POPUP_PROPERTY_SHEET_ACTION
					 *    Mi_DISPLAY_PROPERTIES_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED
					 *    Mi_COPY_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED
					 *    Mi_ITEM_ADDED_ACTION
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		// ---------------------------------------------------------------
		// Set the mouse appearance to an 'hour glass'. In order to restore
		// the mouse appearance to what it was earlier, push the apearance
		// onto a stack using a unique identifier.
		// ---------------------------------------------------------------
		window.pushMouseAppearance(Mi_WAIT_CURSOR, "EMiExample7.processAction");

		if (action.hasActionType(Mi_DISPLAY_PROPERTIES_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			{
			// ---------------------------------------------------------------
			// Get the graphic for what the user is looking at... and pop up its dialog box.
			// ---------------------------------------------------------------
			popupPropertiesDialog(action.getActionSource());
			}

		window.popMouseAppearance("EMiExample7.processAction");

		// ---------------------------------------------------------------
		// Foward actions that we do not handle to the super class.
		// ---------------------------------------------------------------
		return(window.processAction(action));
		}

	private		void		popupPropertiesDialog(MiPart label)
		{
		// ---------------------------------------------------------------
		// Create a dialog box that displays the properties of the entity.
		// The MiModelPropertyViewManager class automatically creates the
		// correct types of widgets for each type of property and manages
		// the getting and setting of the property values from/to the entity.
		// The MiDialogBoxTemplate class manages the ok-apply-undo-default-cancel
		// buttons and their sensitivities.
		// ---------------------------------------------------------------
		
		// Create description of property panel for this label (note, all this
		// should be cached for speed - see MiHelloWorld7.java)
		MiModelEntity sampleLabelProperties = new MiModelEntity();
		MiPropertyDescriptions sampleLabelDescriptions = new MiPropertyDescriptions("Label");
		sampleLabelDescriptions.addElement(
			new MiPropertyDescription("Volume", 
				new Strings("Shout\nNormal\nWisper"), "Shout"));
		sampleLabelDescriptions.addElement(
			new MiPropertyDescription("Name", MiiPropertyTypes.Mi_STRING_TYPE, "?"));
		sampleLabelProperties.setPropertyDescriptions(sampleLabelDescriptions);
		sampleLabelProperties.setPropertiesToDefaultValues();

		MiModelPropertyViewManager dialogBox = new MiModelPropertyViewManager(
			new MiDialogBoxTemplate(window, "Property Sheet for: " 
				+ sampleLabelProperties.getName(), false),
				sampleLabelProperties);

		// ---------------------------------------------------------------
		// Popup the dialog box.
		// ---------------------------------------------------------------
		dialogBox.setModel(sampleLabelProperties);
		dialogBox.setVisible(true);
		}
	}
class CustomEditorBackgroundMenu extends MiEditorMenu implements MiiCommandNames, MiiMessages
	{
					/**------------------------------------------------------
	 				 * Constructs a new MiEditorBackgroundMenu, forwarding commands
					 * generated by this menu to an instance of 
					 * MiShapePopupMenuCommands.
					 * @see			MiEditorBackgroundMenuCommands
					 *------------------------------------------------------*/
	public				CustomEditorBackgroundMenu(MiiCommandManager manager)
		{
		this(new MiEditorBackgroundMenuCommands(manager), manager);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEditorBackgroundMenu which will send commands
					 * generated by the menu to the given command handler.
					 * @param handler	the command handler
					 * @see 		MiEditorBackgroundMenuCommands
					 *------------------------------------------------------*/
	public				CustomEditorBackgroundMenu(MiiCommandHandler handler, MiiCommandManager manager)
		{
		super("Editor Popup", manager);

		setCommandHandler(handler);

		appendMenuItem(		"&Properties...", 	handler, 
								Mi_DISPLAY_PROPERTIES_COMMAND_NAME);
		setHelpMessages(Mi_DISPLAY_PROPERTIES_COMMAND_NAME,
				Mi_DISPLAY_PROPERTIES_STATUS_HELP_MSG,
				Mi_NO_DISPLAY_PROPERTIES_STATUS_HELP_MSG,
				Mi_DISPLAY_PROPERTIES_BALLOON_HELP_MSG);
		}
					/**------------------------------------------------------
	 				 * Gets whether to select the target of this menu when the
					 * menu is popped up.
					 * @return 		true if we are to select the shape
					 *			that this menu is targeting when
					 *			this menu is popped up 
					 * @implements 		MiiContextMenu#getToSelectAttributedShape
					 *------------------------------------------------------*/
	public		boolean		getToSelectAttributedShape()
		{
		return(true);
		}

	}
