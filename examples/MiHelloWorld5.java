
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
import java.applet.Applet;
import java.util.Hashtable;

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a simple window with a label widget that displays
 * "Hello" connected to a graphics node that has a green ball
 * as an image and displays "World". Because the window inherits
 * from MiGraphicsWindow, a menubar, toolbar, and status bar are 
 * provided. In addition, layers, pages, grids, etc. can be easily
 * added. Automatic support for saving, loading and printing the 
 * contents of the palette and editor contents is also available.
 * <p>
 * Additionally, the two graphics are assigned properties and these
 * editable properties are displayed in a dialog box when the graphics
 * a double-clicked on.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiHelloWorld5 extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiHelloWorld5Window. Supported command line parameters are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String title = Utility.getCommandLineArgument(args, "-title");
		MiHelloWorld5Window window = new MiHelloWorld5Window(
			title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiHelloWorld5Window. Supported html file parameters are:
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
		MiHelloWorld5Window window = new MiHelloWorld5Window(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		}
	}
/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a simple window with a label widget that displays
 * "Hello" connected to a graphics node that has a green ball
 * as an image and displays "World". Because the window inherits
 * from MiGraphicsWindow, a menubar, toolbar, and status bar are 
 * provided. In addition, layers, pages, grids, etc. can be easily
 * added. Automatic support for saving, loading and printing the 
 * contents of the palette and editor contents is also available.
 * <p>
 * Additionally, the two graphics are assigned properties and these
 * editable properties are displayed in a dialog box when the graphics
 * a double-clicked on.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.10(Alpha)
 * @module 	%M%
 * @language	Java (JDK 1.02)
 *----------------------------------------------------------------------------------------------*/
class MiHelloWorld5Window extends MiGraphicsWindow implements MiiPropertyChangeHandler, MiiModelTypes
	{
	private static final int 	HELLO_WORLD_POPUP_PROPERTY_SHEET_ACTION
						= MiActionManager.registerAction("popupPropertySheet");
	private		Hashtable	dialogBoxHashtable	= new Hashtable();
	private 	MiEvent		popupEvent 		= new MiEvent(Mi_LEFT_MOUSE_DBLCLICK_EVENT);
	private		MiAction	popupAction;

					/**------------------------------------------------------
	 				 * Constructs a new MiHelloWorld5Window and adds it to the given
					 * AWT container.
					 * @param awtContainer	the AWT container this is going into
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiHelloWorld5Window(
						Container awtContainer, String title, MiBounds screenSize)
		{
		// ---------------------------------------------------------------
		// Tell the super class to create a Canvas in the given awtContainer
		// with the given title with the given size. The super class also 
		// adds the Canvas to the given awtContainer.
		// ---------------------------------------------------------------
		super(MiUtility.getFrame(awtContainer), title, screenSize);
		awtContainer.add("Center", getAWTComponent());
		setupHelloWorld5();
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiHelloWorld5Window and creates and adds it
					 * to a AWT Frame
					 * @param title		the text to be displayed in the
					 *			window border
					 * @param screenSize	the size
					 *------------------------------------------------------*/
	public				MiHelloWorld5Window(String title, MiBounds screenSize)
		{
		// ---------------------------------------------------------------
		// Tell the super class to create a Window with the given title 
		// with the given size.
		// ---------------------------------------------------------------
		super(title, screenSize);
		setupHelloWorld5();
		}
					/**------------------------------------------------------
					 * Creates 'hello' and 'world' nodes and connects them together.
					 * Creates the model entities for the 'hello' and 'world'
					 * nodes. Assigns this class as a handler for changes to
					 * the model entities and as a handler for double clicks
					 * on the graphical nodes.
					 *------------------------------------------------------*/
	protected	void		setupHelloWorld5()
		{
		// ---------------------------------------------------------------
		// Make sure there is an open file for the user to edit/save
		// ---------------------------------------------------------------
		openDefaultFile();

		// ---------------------------------------------------------------
		// Make the window double buffered, if desired.
		// ---------------------------------------------------------------
		//getRootWindow().getCanvas().setDoubleBuffered(true);

		// ---------------------------------------------------------------
		// Get the editor...
		// ---------------------------------------------------------------
		MiEditor editor = getEditor();

		// ---------------------------------------------------------------
		// Create the graphics node that will be the 'world'.
		// ---------------------------------------------------------------
		MiGraphicsNode worldNode = new MiGraphicsNode(
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
		// Create the popup action with a special type allocated for this application.
		// ---------------------------------------------------------------
		popupAction = new MiAction(this, HELLO_WORLD_POPUP_PROPERTY_SHEET_ACTION);

		// ---------------------------------------------------------------
		// Create the data model object for the 'world'.
		// ---------------------------------------------------------------
		MiModelEntity worldModel = new MiModelEntity();

		// ---------------------------------------------------------------
		// Create the descriptions of the properties of the 'world'
		// ---------------------------------------------------------------
		MiPropertyDescriptions worldPropertyDescriptions = new MiPropertyDescriptions("World");
		worldPropertyDescriptions.addElement(
			new MiPropertyDescription("Population", Mi_POSITIVE_INTEGER_TYPE, "2"));
		worldPropertyDescriptions.addElement(
			new MiPropertyDescription("Number of countries", Mi_POSITIVE_INTEGER_TYPE, "20000"));
		worldPropertyDescriptions.addElement(
			new MiPropertyDescription("Prognosis", 
				new Strings("Utopia\nWorld War III\nStatus Quo"), "Utopia"));
		worldModel.setPropertyDescriptions(worldPropertyDescriptions);
		worldModel.setPropertiesToDefaultValues();

		// ---------------------------------------------------------------
		// Set the name of this model, to be printed below when any property
		// of the model changes. Append this MiHelloWorld5 class as a property
		// change handler so we will be notified of any changes to any property
		// of the 'world's model.
		// ---------------------------------------------------------------
		worldModel.setName("The World");
		worldModel.appendPropertyChangeHandler(
			this, new Strings(Mi_ANY_PROPERTY), Mi_MODEL_CHANGE_COMMIT_PHASE.getMask());

		// ---------------------------------------------------------------
		// Assign the 'world' model to the 'world' graphics.
		// ---------------------------------------------------------------
		worldNode.setResource(Mi_SEMANTICS_TYPE_NAME, worldModel);

		// ---------------------------------------------------------------
		// Append this as an action handler to the 'world' graphics so
		// we will be notified when the user double clicks on the graphic.
		// ---------------------------------------------------------------
		worldNode.appendActionHandler(popupAction, popupEvent);

		// ---------------------------------------------------------------
		// Create the data model object for the 'hello'.
		// ---------------------------------------------------------------
		MiModelEntity helloModel = new MiModelEntity();

		// ---------------------------------------------------------------
		// Create the descriptions of the properties of the 'hello'
		// ---------------------------------------------------------------
		MiPropertyDescriptions helloPropertyDescriptions = new MiPropertyDescriptions("World");
		helloPropertyDescriptions.addElement(
			new MiPropertyDescription("Volume", 
				new Strings("Shout\nNormal\nWisper"), "Shout"));
		helloModel.setPropertyDescriptions(helloPropertyDescriptions);
		helloModel.setPropertiesToDefaultValues();

		// ---------------------------------------------------------------
		// Set the name of this model, to be printed below when any property
		// of the model changes. Append this MiHelloWorld5 class as a property
		// change handler so we will be notified of any changes to any property
		// of the 'hello's model.
		// ---------------------------------------------------------------
		helloModel.setName("The Hello");
		helloModel.appendPropertyChangeHandler(
			this, new Strings(Mi_ANY_PROPERTY), Mi_MODEL_CHANGE_COMMIT_PHASE.getMask());

		// ---------------------------------------------------------------
		// Assign the 'hello' model to the 'hello' graphics.
		// ---------------------------------------------------------------
		helloNode.setResource(Mi_SEMANTICS_TYPE_NAME, helloModel);

		// ---------------------------------------------------------------
		// Append this as an action handler to the 'hello' graphics so
		// we will be notified when the user double clicks on the graphic.
		// ---------------------------------------------------------------
		helloNode.appendActionHandler(popupAction, popupEvent);
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are:
					 * <pre>
					 *    HELLO_WORLD_POPUP_PROPERTY_SHEET_ACTION
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		// ---------------------------------------------------------------
		// Make sure it is the action we requested, and not the toolbar drag
		// and drop actions requested by the super class window.
		// ---------------------------------------------------------------
		if (action.hasActionType(HELLO_WORLD_POPUP_PROPERTY_SHEET_ACTION))
			{
			// ---------------------------------------------------------------
			// Set the mouse appearance to an 'hour glass'. In order to restore
			// the mouse appearance to what it was earlier, push the apearance
			// onto a stack using a unique identifier.
			// ---------------------------------------------------------------
			pushMouseAppearance(Mi_WAIT_CURSOR, "MiHelloWorld5");

			// ---------------------------------------------------------------
			// Get the graphic, the model, and the dialog box for what the user
			// double-clicked on...
			// ---------------------------------------------------------------
			MiPart part = action.getActionSource();
			MiModelEntity entity = (MiModelEntity )part.getResource(Mi_SEMANTICS_TYPE_NAME);
			MiModelPropertyViewManager dialogBox 
				= (MiModelPropertyViewManager )dialogBoxHashtable.get(entity);

			// ---------------------------------------------------------------
			// A dialog box has not yet been created for this entity, make one now
			// and put it into the dialog box hashtable.
			// ---------------------------------------------------------------
			if (dialogBox == null)
				{
				// ---------------------------------------------------------------
				// Create a dialog box that displays the properties of the entity.
				// The MiModelPropertyViewManager class automatically creates the
				// correct types of widgets for each type of property and manages
				// the getting and setting of the property values from/to the entity.
				// The MiDialogBoxTemplate class manages the ok-apply-undo-default-cancel
				// buttons and their sensitivities.
				// ---------------------------------------------------------------
				dialogBox = new MiModelPropertyViewManager(
					new MiDialogBoxTemplate(this, "Property Sheet for: " 
						+ entity.getName(), false),
					entity);
				dialogBoxHashtable.put(entity, dialogBox);
				}
			// ---------------------------------------------------------------
			// Popup the dialog box. Restore the mouse appearance
			// ---------------------------------------------------------------
			dialogBox.setVisible(true);
			popMouseAppearance("MiHelloWorld5");
			}
		// ---------------------------------------------------------------
		// Foward actions that we do not handle to the super class.
		// ---------------------------------------------------------------
		return(super.processAction(action));
		}
					/**------------------------------------------------------
		 			 * Processes the given change. 
					 * @param change	the change to process. If changes
					 *			in the 'request' phase have been
					 *			requested then the change can be
					 *			vetoed by using the change's setVetoed
					 *			method.
					 * @implements		MiiPropertyChangeHandler#processPropertyChange
					 *------------------------------------------------------*/
	public		void		processPropertyChange(MiPropertyChange change)
		{
		MiDebug.println("\"" 
			+ change.getSource().getName() + "\"\'s property called: \""
			+ change.getPropertyName() + "\" changed from: \"" 
			+ change.getOldPropertyValue() + "\" to: \"" 
			+ change.getPropertyValue()
			+ "\"");
		}
	}

