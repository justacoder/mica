
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

import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.Set;

/**----------------------------------------------------------------------------------------------
 * This class is a MiEditor that implements the additional functionality
 * assocaited with a 'window'. This includes managing how events are 
 * dispatched to MiParts, so that special kinds of event handlers can be 
 * supported. This includes managing the keyboard and enter-key focus's
 * and drag and drop.
 * <p>
 * A special kind of MiWindow is called the 'root' window. Being a root
 * window means a number of things: mainly that the window interacts 
 * (receives events and draws) directly with the AWT (java.awt.Canvas).
 * That means that a root window is not contained in another Mica container
 * (whereas non-root windows must be).
 * <p>
 * <em>Event Dispatching</em>
 * <p>
 * Events are dispatched to MiParts whose event handlers get a chance to
 * process the event. Each event is sent first to the top MiPart at the
 * location of the event (which is typically the location of the mouse 
 * cursor). The event is then 'bubbled up' until the root window sends
 * the event to it's own event handlers.
 * <p>
 * This dispatching is done multiple times, potentially once for each kind
 * of event handler. The first time the event is sent to all event 'monitors'
 * which are special event handlers that cannot consume events. Any 'global'
 * event monitors get the event first, and then the event is 'bubbled up'
 * through the event monitors assigned to the MiParts at the event location.
 * <p>
 * Then, if there are event handlers that are 'grabbing' all events, the 
 * event is * sent to them and the event is then discarded. If there are not 
 * any event handlers that are 'grabbing' all events, then the event is sent
 * to any 'global' event handlers. Global event handlers are not position
 * dependent and so process events no matter where they occur in the window,
 * irregardless of which MiPart they are assigned to. If not consumed by
 * any of the global event handlers, then the event is 'bubbled up' through
 * the event handlers  assigned to the MiParts at the event location.
 * <p>
 * Root windows have a MiKeyboardFocusManager and a MiDragAndDropManager.
 * <p>
 * All windows have a MiiKeyFocusTraversalGroup and a MiStatusBarFocusManager.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiWindow extends MiEditor 
	{
	private		boolean			debug			= true;
	private		MiCanvas		canvas;
	private		MiiKeyFocusTraversalGroup	keyFocusTraversalGroup;
	private		MiDragAndDropManager	dragAndDropManager;
	private		MiKeyboardFocusManager	keyboardFocusManager;
	private		MiStatusBarFocusManager	statusBarFocusManager;
	private		FastVector		currentGrabs		= new FastVector();
	private		FastVector		grabs			= new FastVector();
	private		FastVector		windowWideEventHandlers	= new FastVector();
	private		Set	 		invalidEditorPartLayouts= new Set();
	private		boolean			isARootWindow 		= false;
	private		boolean			embeddedWindow;
	private		MiBoundsList		boundsList1		= new MiBoundsList();
	private		MiBoundsList		boundsList2		= new MiBoundsList();



					/**------------------------------------------------------
	 				 * Constructs a new MiWindow with a size of 400x400 device
					 * coordinates. 
					 *------------------------------------------------------*/
	public 				MiWindow() 
		{
		keyFocusTraversalGroup = new MiLazyKeyFocusTraversalGroup(this);
		setBitbltScrollingEnabled(false);
		MiSystem.applyClassPropertyValues(this, "MiWindow");
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiWindow with the given device bounds.
					 * @param deviceBounds	the size of the new window in 
					 *			device space.
					 *------------------------------------------------------*/
	public 				MiWindow(MiBounds deviceBounds) 
		{
		super(deviceBounds);
		keyFocusTraversalGroup = new MiLazyKeyFocusTraversalGroup(this);
		setBitbltScrollingEnabled(false);
		MiSystem.applyClassPropertyValues(this, "MiWindow");
		}
					/**------------------------------------------------------
	 				 * Gets whether this is a root window. It is a root window
					 * when this MiWindow writes directly to a java.awt.Canvas
					 * (and therefore this window's device coordinate space is 
					 * actually in pixels).
					 * @return 		true if a root window
					 *------------------------------------------------------*/
	public		boolean		isRootWindow()
		{
		return(isARootWindow);
		}
	public		void		resume()
		{
		getRootWindow().getCanvas().freeze(false);
		}
	public		void		suspend()
		{
		getRootWindow().getCanvas().freeze(true);
		}
	public		void		stop()
		{
		getRootWindow().getCanvas().freeze(true);
		getRootWindow().getCanvas().stop();
		}
					/**------------------------------------------------------
	 				 * Gets the canvas this MiWindow uses to draw and receive
					 * events. This will be null if this is not a root window.
					 * @return		the canvas
					 *------------------------------------------------------*/
	public		MiCanvas	getCanvas()
		{
		return(canvas);
		}
					/**------------------------------------------------------
	 				 * Sets the canvas this MiWindow uses to draw and receive
					 * events. This had better be a root window.
					 * @param c		the canvas
					 *------------------------------------------------------*/
	protected	void		setCanvas(MiCanvas c)
		{
		canvas = c;
		isARootWindow = true;
		dragAndDropManager = new MiDragAndDropManager(this);
		keyboardFocusManager = new MiKeyboardFocusManager(this);
		setIsOpaqueRectangle(true);
		canvas.setWindow(this);
		getViewport().setResizePolicy(Mi_NONE);
		setDeviceBounds(canvas.getCanvasBounds());
		if (debug)
			{
			appendEventHandler(new MiISetDebugTraceModes());
			appendEventHandler(new MiIDumpGraphicsStructures());
			appendEventHandler(new MiIReCalcLayouts());
			appendEventHandler(new MiIRedraw());
			}
		}
					/**------------------------------------------------------
	 				 * Sets the device bounds in pixels for this root MiWindow.
					 * @param deviceBounds	the new device bounds
					 *------------------------------------------------------*/
	protected	void		setScreenBounds(MiBounds deviceBounds)
		{
//MiDebug.println("setScreenBounds = " + deviceBounds);
		canvas.setCanvasBounds(deviceBounds);
		if (!deviceBounds.equals(canvas.getCanvasBounds()))
			{
			// Try to set the canvas size again if it did not work the first time...
//MiDebug.println("trying again... canvas.getCanvasBounds() = " + canvas.getCanvasBounds());
			if (!deviceBounds.equalsIntegerSize(canvas.getCanvasBounds()))
				setScreenBounds(deviceBounds);
			else
				deviceWasResized(canvas.getCanvasBounds());
			}
//MiDebug.println("Now ... canvas.getCanvasBounds() = " + canvas.getCanvasBounds());
		}
					/**------------------------------------------------------
	 				 * Gets the device bounds in pixels for this root MiWindow.
					 * @return 		the device bounds
					 *------------------------------------------------------*/
	protected	MiBounds	getScreenBounds()
		{
		return(canvas.getCanvasBounds());
		}
					/**------------------------------------------------------
	 				 * Gets the animation manager for this window (and for all
					 * other MiParts in the root window). There is an animation
					 * manager for each java.awt.Canvas.
					 * @return 		the animation manager
					 * @see 		MiEditor#getAnimationManager
					 * @see 		MiCanvas#getAnimationManager
					 *------------------------------------------------------*/
	public		MiAnimationManager	getAnimationManager()
		{
		return(canvas.getAnimationManager());
		}
					/**------------------------------------------------------
	 				 * This method is called to create the graphics for this
					 * MiWindow when this window is first created. This 
					 * approach to delaying the creation of graphics until
					 * after the window is created is optional.
					 *------------------------------------------------------*/
	public		void		createGraphicsContents()
		{
		}
					/**------------------------------------------------------
	 				 * Specifies whether this window is treated as a part of
					 * it's Mica container or as an attachment of it's container.
					 * This is only useful if this is a Mica window (i.e.
					 * MiInternalWindow).
					 * @param flag		true if an embedded window
					 *------------------------------------------------------*/
	public		void		setIsEmbeddedWindow(boolean flag)
		{
		embeddedWindow = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether this window is treated as a part of
					 * it's Mica container or as an attachment of it's container.
					 * @return 		true if an embedded window
					 *------------------------------------------------------*/
	public		boolean		isEmbeddedWindow()
		{
		return(embeddedWindow);
		}


	//***************************************************************************************
	// Drag and Drop Routines
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the drag and drop manager for this window. This is
					 * null if this is not a root window.
					 * @return 		the drag and drop manager
					 * @see			MiDragAndDropManager
					 *------------------------------------------------------*/
	public		MiDragAndDropManager	getDragAndDropManager()
		{
		return(dragAndDropManager);
		}

	//***************************************************************************************
	// Key Focus Routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Specifies whether this MiPart is visible. In addition,
					 * if this is a root window and it has just been made
					 * visible, then the keyboard focus manager initializes 
					 * the keyboard and enter-key focus to their default
					 * initial settings.
					 * @param flag		true if this MiPart is to be visible
					 * @see 		MiPart#setVisible
					 * @see 		#setDefaultKeyboardFocus
					 * @see 		#setDefaultEnterKeyFocus
					 *------------------------------------------------------*/
	public		void		setVisible(boolean flag)
		{
		if (flag != isVisible())
			super.setVisible(flag);
		if ((flag) && (getRootWindow() != null))
			getRootWindow().keyboardFocusManager.initialize(this);
		else if ((!flag) && (getRootWindow() != null))
			getRootWindow().keyboardFocusManager.requestKeyboardFocus(null);
		}
					/**------------------------------------------------------
	 				 * Gets the keyboard focus manager of this window. This
					 * will be null if this is not a root window.
					 * @return 		the keyboad focus manager
					 * @see			MiKeyboardFocusManager
					 *------------------------------------------------------*/
	public		MiKeyboardFocusManager	getKeyboardFocusManager()
		{
		return(keyboardFocusManager);
		}
					/**------------------------------------------------------
					 * Requests that the given MiPart gets this window's
					 * keyboard focus. This window must be a root window.
					 * @param part 		the part to get keyboard focus
					 * @return 		true if the MiPart now has the
					 *			keyboard focus
					 * @see 		MiPart#requestKeyboardFocus
					 * @see 		#setDefaultKeyboardFocus
					 *------------------------------------------------------*/
	public 		boolean		requestKeyboardFocus(MiPart part)
		{
		return(keyboardFocusManager.requestKeyboardFocus(part));
		}
					/**------------------------------------------------------
					 * Requests that the given MiPart gets this window's
					 * enter-key focus. This window must be a root window.
					 * @param part 		the part to get enter key focus
					 * @return 		true if the MiPart now has the
					 *			enter-key focus
					 * @see 		MiPart#requestEnterKeyFocus
					 * @see 		#setDefaultEnterKeyFocus
					 *------------------------------------------------------*/
	public 		boolean		requestEnterKeyFocus(MiPart part)
		{
		if (keyboardFocusManager == null)
			return(getRootWindow().requestEnterKeyFocus(part));
		return(keyboardFocusManager.requestEnterKeyFocus(part));
		}
					/**------------------------------------------------------
					 * Specifies that the given MiPart is to get the keyboard
					 * focus whenever this window is first made visible.
					 * enter-key focus. This window must be a root window.
					 * @param part 		the part to get keyboard focus
					 *			first
					 * @see 		#setVisible
					 * @see 		#setDefaultEnterKeyFocus
					 *------------------------------------------------------*/
	public 		void		setDefaultKeyboardFocus(MiPart part)
		{
		keyboardFocusManager.setDefaultKeyboardFocus(part);
		}
					/**------------------------------------------------------
					 * Specifies that the given MiPart is to get the enter-key
					 * focus whenever this window is first made visible.
					 * enter-key focus. This window must be a root window.
					 * @param part 		the part to get enter-key focus
					 *			first
					 * @see 		#setVisible
					 * @see 		#setDefaultKeyboardFocus
					 *------------------------------------------------------*/
	public 		void		setDefaultEnterKeyFocus(MiPart obj)
		{
		keyboardFocusManager.setDefaultEnterKeyFocus(obj);
		}
					/**------------------------------------------------------
					 * Gets the keyboard and enter-key focus traversal group
					 * mamager for this window.
					 * @return 		the focus traversal group
					 * @see 		#setKeyFocusTraversalGroup
					 *------------------------------------------------------*/
	public		MiiKeyFocusTraversalGroup getKeyFocusTraversalGroup()
		{
		return(keyFocusTraversalGroup);
		}
					/**------------------------------------------------------
					 * Sets the keyboard and enter-key focus traversal group
					 * mamager for this window.
					 * @param group		the focus traversal group
					 * @see 		#getKeyFocusTraversalGroup
					 *------------------------------------------------------*/
	public		void		setKeyFocusTraversalGroup(MiiKeyFocusTraversalGroup group)
		{
		keyFocusTraversalGroup = group;
		}

	//***************************************************************************************
	// Status Bar Focus Routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the status bar focus manager for this window. 
					 * There is by default no status bar focus manager.
					 * @return 		the status bar focus manager.
					 * @see 		#setStatusBarFocusManager
					 * @see 		MiStatusBarFocusManager
					 *------------------------------------------------------*/
	public 		MiStatusBarFocusManager	getStatusBarFocusManager()
		{
		return(statusBarFocusManager);
		}
					/**------------------------------------------------------
					 * Sets the status bar focus manager for this window. 
					 * There is no status bar focus manager unless this method
					 * is used.
					 * @param manager 	the status bar focus manager.
					 * @see 		#getStatusBarFocusManager
					 * @see 		MiStatusBarFocusManager
					 *------------------------------------------------------*/
	public 		void		setStatusBarFocusManager(MiStatusBarFocusManager manager)
		{
		statusBarFocusManager = manager;
		}
					/**------------------------------------------------------
					 * Sets the message that the status bar should display
					 * because of what MiPart is under the mouse cursor.
					 * @param messageInfo 	the status bar message and 
					 *			attributes
					 * @see 		MiiHelpInfo
					 * @see 		MiStatusBarFocusManager
					 *------------------------------------------------------*/
	public 		void		setStatusBarFocusMessage(MiiHelpInfo messageInfo)
		{
		if (statusBarFocusManager != null)
			statusBarFocusManager.setStatusBarFocusMessage(messageInfo);
		}

	//***************************************************************************************
	// Damaged Area Routines
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Update this MiEditor's state after it's device bounds have
					 * been changed.
					 * @param deviceBounds 	the new device bounds
					 * @action		Mi_EDITOR_DEVICE_RESIZED_ACTION
					 * @action		Mi_EDITOR_VIEWPORT_CHANGED_ACTION
					 * @see 		MiEditor#deviceWasResized
					 *------------------------------------------------------*/
	protected	void		deviceWasResized(MiBounds deviceBounds)
		{
		super.deviceWasResized(deviceBounds);
		getDrawManager().invalidateBackToFront(deviceBounds);
		}
					/**------------------------------------------------------
	 				 * Notify this MiWindow that the given area has been revealed
					 * to the end-user. This MiWindow will then initiate a redraw
					 * of the area and all of this window's sub-windows in the
					 * area.
					 * @param device 	the area in device coordinates
					 *------------------------------------------------------*/
	public		void		exposeArea(MiBounds device)
		{
		if (!hasValidLayout())
			validateWindow();

		getDrawManager().invalidateBackToFront(device);
		setThisOrPartHasInvalidArea(true);
		}
					/**------------------------------------------------------
	 				 * Gets whether this MiWindow, or one of it's MiParts needs
					 * to be redrawn (or re-laid out and then redrawn).
					 * @return 	 	true if needs to be drawn
					 *------------------------------------------------------*/
	public		boolean		needsToBeRedrawn()
		{

/*
if (getThisOrPartHasInvalidArea())
{
MiDebug.println(this + "needsToBeRedrawn.ThisOrPartHasInvalidArea");
MiDebug.println("One part that has invalid area is: " + MiDebug.getPartWithInvalidArea(this));
}
if (!hasValidLayout())
MiDebug.println(this + "needsToBeRedrawn.has INVALID LAYOUT");
if (hasShowingEditorPartLayoutsToValidate())
MiDebug.println(this + "needsToBeRedrawn.has hasShowingEditorPartLayoutsToValidate");
*/



		if (getThisOrPartHasInvalidArea())
			return(true);

		if ((!hasValidLayout()) || (hasShowingEditorPartLayoutsToValidate()))
			return(true);

		return(false);
		}
	//***************************************************************************************
	// Draw Routines
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Draws this MiWindow. First checks for whether it is
					 * visible and not hidden and not clipped.
	 				 * @param renderer 	the renderer to use for drawing
	 				 * @action		Mi_DRAW_ACTION
					 *------------------------------------------------------*/
	public		void		draw(MiRenderer renderer)
		{
		if ((!isVisible()) || (isHidden()))
			return;

		// ---------------------------------------------------------------
		// If it is the draw manager drawing this window or it is not a 
		// root window, then really draw it.
		// ---------------------------------------------------------------
		if ((getDrawManager() == null) || (getDrawManager().isDrawing()) || (!isARootWindow))
			{
			super.draw(renderer);
			return;
			}

		validateWindow();


		if (MiDebug.debug)
			{
			MiDebug.checkForLingeringInvalidLayoutsBelow(this); // , "After validating root window: " + this);
			}

		// ---------------------------------------------------------------
		// Validate all cached event handler info for this window.
		// ---------------------------------------------------------------
		if (!hasValidCachedEventHandlerInfo())
			validateCachedEventHandlerInfo();
	
		// ---------------------------------------------------------------
		// Call the draw manager to draw this window and all of it's sub-windows
		// while not drawing areas that may be obscured by other windows.
		// ---------------------------------------------------------------
		boundsList1.removeAllElements();
		boundsList2.removeAllElements();
		getDrawManager().draw(renderer, boundsList1, boundsList2);

		// ---------------------------------------------------------------
		// This is required here cause MiPart.draw may not be called when
		// only a part of this needed to be redrawn.
		// ---------------------------------------------------------------
		if (getDrawActionsNeedToBeDispatched())
			dispatchAction(Mi_DRAW_ACTION);
		}

	//***************************************************************************************
	// Layout Routines
	//***************************************************************************************
	protected	void		validateWindow()
		{
		// ---------------------------------------------------------------
		// Otherwise validate this root window's layouts, and the layouts of
		// any editor's this window may contain that need it. Since MiEditor's
		// validate layout's of all of their parts, this effectively causes
		// validation of all layouts in this root window.
		// ---------------------------------------------------------------
		while((!hasValidLayout()) || (hasShowingEditorPartLayoutsToValidate()))
			{
			if (!hasValidLayout())
				validateLayout();

			if (hasShowingEditorPartLayoutsToValidate())
				validateEditorLayouts();
			}
		}

					/**------------------------------------------------------
	 				 * Validates this MiWindow's layout, and if this is a root
					 * window, prohibits the recalculation of preferred sizes
					 * during the layout and validations the layouts of any
					 * editor's in this root windwo that need it.
					 * @see			MiEditor#validateLayout
					 *------------------------------------------------------*/
	public		void		validateLayout()
		{
		if (isARootWindow)
			{
			// ---------------------------------------------------------------
			// Prohibit recalc of preferred sizes during layout
			// ---------------------------------------------------------------
			setValidatingLayout(true);
			super.validateLayout();
			setValidatingLayout(false);
			if (hasShowingEditorPartLayoutsToValidate())
				validateEditorLayouts();
			}
		else
			{
			super.validateLayout();
			}
		}
					/**------------------------------------------------------
	 				 * Registers the given MiPart as needing to have it's
					 * layout validated. This is used (1) by parts that do not
					 * want to invalidate their containers and their containers
					 * up the hierarchy like everybody else and (2) by MiEditors
					 * that are parts (or parts of part) in this MiWindow.
					 * This is useful because some parts (and all editors)
					 * do not change their size or location when their layout's
					 * are validated (for example MiScrolledBox). The given part
					 * will be registered just once between the validation of
					 * layouts, no matter how many times this method is called.
					 * @param part		the part that has an invalid layout
					 * @see			#validateLayout
					 * @see			#hasInvalidateEditorPartLayout
					 *------------------------------------------------------*/
	protected	void		invalidateEditorPartLayout(MiPart part)
		{
		// ---------------------------------------------------------------
		// Because invalidEditorPartLayouts is a Set, the given part will
		// not be added more than onec to the list, no matter how many
		// times this is called.
		// ---------------------------------------------------------------

//if (!invalidEditorPartLayouts.contains(part))
//MiDebug.printStackTrace("Adding invalid editor part: " + part);

		invalidEditorPartLayouts.addElement(part);
		}
					/**------------------------------------------------------
	 				 * Gets whether the given MiPart is registered as needing 
					 * to have it's layout validated.
					 * @param part		the part that may be registered
					 * @return 		true if the part has been registered
					 *			as having an invalid layout.
					 * @see			#invalidateEditorPartLayout
					 *------------------------------------------------------*/
	protected	boolean		hasInvalidateEditorPartLayout(MiPart part)
		{
		return(invalidEditorPartLayouts.contains(part));
		}
					/**------------------------------------------------------
	 				 * Validates (recalculates) all MiParts that have been 
					 * registered as having invalid layouts.
					 * @see			#invalidateEditorPartLayout
					 *------------------------------------------------------*/
	private		void		validateEditorLayouts()
		{
		while (hasShowingEditorPartLayoutsToValidate())
			{
//MiDebug.println(this + " validateEditorLayouts ");
			for (int i = 0; i < invalidEditorPartLayouts.size(); ++i)
				{
				MiPart part = (MiPart )invalidEditorPartLayouts.elementAt(i);
				if (part.isShowing(null))
					{
//MiDebug.println(this + " validateEditorLayouts-> " + part);
//MiDebug.println(this + " validateEditorLayouts-> at index " + i);
					part.setValidatingLayout(true);
					part.validateLayout();
					part.setValidatingLayout(false);
					invalidEditorPartLayouts.removeElementAt(i);
					--i;
					}
				}
			}
		}
	private		boolean		hasShowingEditorPartLayoutsToValidate()
		{
		for (int i = 0; i < invalidEditorPartLayouts.size(); ++i)
			{
			MiPart part = (MiPart )invalidEditorPartLayouts.elementAt(i);
			if (part.isShowing(null))
				return(true);
			}
		return(false);
		}

	//***************************************************************************************
	// Event Handling Routines
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Puts the given MiEvent into the internal queue so that
					 * it will be the next event dispatched.
					 * @param event		the event to dispatch again
					 * @see			MiCanvas#pushBackEvent
					 *------------------------------------------------------*/
	public		void		pushBackEvent(MiEvent event)
		{
		getRootWindow().getCanvas().pushBackEvent(event);
		}

					/**------------------------------------------------------
					 * Dispatches the given event to all event handlers assigned
					 * to the MiPart's in this window, and then, if not consumed,
					 * to their containers, and so on until the the event is 
					 * dispatched to the event handlers of this window.
					 * @param  event	The event
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see this event
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see this event
					 *------------------------------------------------------*/
	public		int	 	dispatchEvent(MiEvent event)
		{
		// ---------------------------------------------------------------
		// Exit if this window is going away...
		// ---------------------------------------------------------------
		if (getRootWindow() == null)
			return(Mi_PROPOGATE_EVENT);

//MiDebug.println("WINDOW RECEIVING EVENT: " + this);
//MiDebug.println("WINDOW RECEIVING EVENT: " + event);
		int status = Mi_PROPOGATE_EVENT;
		event.editor = this;

		// Need these to be method variables because in a model dialog, we process idle 
		// events etc. here which blasts these if they are class variables.
		MiParts targetList = new MiParts();
		MiParts targetPath = new MiParts();
/*

		if (targetList == null)
			targetList = new MiParts();
		targetList.removeAllElements();
		if (targetPath == null)
			targetPath = new MiParts();
		targetPath.removeAllElements();
*/

		// ---------------------------------------------------------------
		// This the event was generated for this window's root window...
		// ---------------------------------------------------------------
		if (getRootWindow().getCanvas() == event.canvas)
			{
			// ---------------------------------------------------------------
			// Get the list of parts underneath the location of the mouse cursor
			// ---------------------------------------------------------------
			pickDeepList(event.worldMouseFootPrint, targetList);

			sortPickedDeepList(event.worldMouseFootPrint, targetList);

			// ---------------------------------------------------------------
			// Find the MiPart that is the topmost part and so is the part to
			// get the event first. After this is found, the targetPath is
			// calculated from this part. The target path consists of the part 
			// and the hierarchy of containers between the part and this window.
			// ---------------------------------------------------------------
			MiPart activeLayer = getActiveLayer();
			for (int i = 0; i < targetList.size(); ++i)
				{
				MiPart child = targetList.elementAt(i);
				// ---------------------------------------------------------------
				// If pickAllLayers && child is not a layer...
				// ---------------------------------------------------------------
				if (child.getContainingEditor() == null)
					{
					MiDebug.println("*** Warning *** Part: " + child + " has no containing editor.\n"
						+ "Checking to see if part or a parent of the part has multiple\n"
						+ "containers (only 0th containers are travsersed when determining\n"
						+ "the containing editor of the part).");

					MiPart c = child;
					do	{
						if ((c.getNumberOfContainers() > 1)
							&& (c.getContainer(0).getContainingEditor() == null)
							&& (c.getContainer(1).getContainingEditor() != null))
							{
							MiDebug.println("Child: " + c + "\n"
							    + "has container #0: " + c.getContainer(0) + "\n"
							    + "which has no containing editor and \n"
							    + "has container #1: " + c.getContainer(1) + "\n"
							    + "which does");
							break;
							}
						} while ((c.getNumberOfContainers() > 0) 
							&& ((c = c.getContainer(0)) != null));
					}
				else if (((activeLayer == null) 
						&& (!child.getContainingEditor().isLayer(child)))
					// ---------------------------------------------------------------
					// .. or child is active layer...
					// ---------------------------------------------------------------
					|| ((child == activeLayer) 
					// ---------------------------------------------------------------
					// ... or pickOnly active layer && active layer contains child...
					// ---------------------------------------------------------------
					|| ((activeLayer != null) 
						&& (activeLayer.isContainerOf(child)))))
				
					{
					// ---------------------------------------------------------------
					// Get the path into the targetPath and we are done.
					// ---------------------------------------------------------------
					MiUtility.getPath(this, child, targetPath);
					break;
					}
				}
			}
		// ---------------------------------------------------------------
		// Assign the paths to the event.
		// ---------------------------------------------------------------
		event.setTargetList(targetList);
		event.setTargetPath(targetPath);

		// ---------------------------------------------------------------
		// Obtain the index of this window in the targetPath.
		// ---------------------------------------------------------------
		int targetPathIndexOfThis = targetPath.indexOf(this);

		// *****************************************************************************
		// If this is NOT a root window, then this window's root window has
		// sent the event here so that this window's event handlers can 
		// examine and potentially act on the event. This is all we have to
		// do here for the non-root-window window.
		// *****************************************************************************
		if (!isARootWindow)
			{
			// ---------------------------------------------------------------
			// If the event is targeting short-cut event handlers...
			// ---------------------------------------------------------------
			if (event.handlerTargetType == Mi_SHORT_CUT_EVENT_HANDLER_TYPE)
				{
				//------------------------------------------------------
				// Send event to any registered short cuts (hotKeys)
				//------------------------------------------------------
				for (int i = 0; i < windowWideEventHandlers.size(); ++i)
					{
					MiiEventHandler handler = 
						(MiiEventHandler )windowWideEventHandlers.elementAt(i);
					if ((handler.getType() == event.handlerTargetType)
						&& (handler.isEnabled()))
						{
						if (handler.processEvent(event) == Mi_CONSUME_EVENT)
							return(Mi_CONSUME_EVENT);
						}
					}
				//------------------------------------------------------
				// None of the short-cut handlers consumed the event...
				//------------------------------------------------------
				return(Mi_PROPOGATE_EVENT);
				}
			// ---------------------------------------------------------------
			// The event is not targeting short-cut event handlers, dispatch
			// the event to the event handlers assigned to this window.
			// ---------------------------------------------------------------
			return(super.dispatchEvent(event));
			}

		// *****************************************************************************
		// This is a root window
		// *****************************************************************************

		// ---------------------------------------------------------------
		// For debugging. Whenever an event occurs that is not a mouse motion,
		// a timer tick or an idle event then print info about this window,
		// the event and the event targetPath and targetList.
		// ---------------------------------------------------------------
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_DISPATCHING))
			{
			if ((event.getType() != MiEvent.Mi_TIMER_TICK_EVENT)
				&& (event.getType() != MiEvent.Mi_IDLE_EVENT)
				&& (event.getType() != MiEvent.Mi_MOUSE_MOTION_EVENT) )
				{
				MiDebug.println("Root window: " + this);
				MiDebug.println("Event: " + event);
				MiDebug.println("Event target list (top shape to bottom):\n" + targetList);
				MiDebug.println("Event target path (top shape and it\'s containers):\n" + targetPath);
				}
			}

		// *****************************************************************************
		// Send event to any !locationSpecific monitors registered locally...
		// *****************************************************************************
		event.handlerTargetType = Mi_MONITOR_EVENT_HANDLER_TYPE;
		event.locationSpecific = false;
		event.editor = this;
		for (int i = 0; i < windowWideEventHandlers.size(); ++i)
			{
			MiiEventHandler handler = (MiiEventHandler )windowWideEventHandlers.elementAt(i);
			if ((handler.getType() == event.handlerTargetType)
				&& (handler.isEnabled()))
				{
				handler.processEvent(event);
				}
			}

		// *****************************************************************************
		// Send event to any locationSpecific monitors in targetPath...
		// *****************************************************************************
		// This was commented out (why?) but added it in in order to make the 
		// full-screen-cursor monitor assigned to graphics editors get events...
		event.handlerTargetType = Mi_MONITOR_EVENT_HANDLER_TYPE;
		event.locationSpecific = true;
		event.editor = this;
		status = dispatchLocationSpecificEvent(event, targetPathIndexOfThis);

		// *****************************************************************************
		// Send event to any locationSpecific monitors in targetPath AND targetList...
		// Added 8-7-2003 so that a monitor assigned to an editor can see monitor events during drag and drop
		// *****************************************************************************
		event.handlerTargetType = Mi_MONITOR_EVENT_HANDLER_TYPE;
		event.locationSpecific = true;
		event.editor = this;
		MiParts targetListMinusElementsInTargetPath = new MiParts(targetList);
		// Do not want to send same monitor event to a part twice...
		targetListMinusElementsInTargetPath.removeAll(targetPath);
		int targetListMinusElementsInTargetPathIndexOfThis = targetListMinusElementsInTargetPath.indexOf(this);
		if (targetListMinusElementsInTargetPathIndexOfThis < 0)
			{
			// Already sent event to the window's event monitors.... 
			// and dont' want dump graphics to be called twice 2-20-2004 .... status = super.dispatchEvent(event);
			}
		else
			{
			status = dispatchEvent(
					event, 
					targetListMinusElementsInTargetPathIndexOfThis, 
					targetListMinusElementsInTargetPathIndexOfThis, 
					targetListMinusElementsInTargetPath);
			}


		// *****************************************************************************
		// Send event to any grabs ... and absorb event if there are any
		// grabbing event handlers at all.
		// *****************************************************************************
		if (grabs.size() > 0)
			{
			event.handlerTargetType = Mi_GRAB_EVENT_HANDLER_TYPE;
			event.editor = this;
			// ---------------------------------------------------------------
			// Copy the list of grabbing event handlers, we don't want the list
			// to change while we are processing this event.
			// ---------------------------------------------------------------
			currentGrabs.removeAllElements();
			currentGrabs.append(grabs);

			// ---------------------------------------------------------------
			// Make a copy of the 'event' to send out to event handlers that
			// do not need to be transformed. This is done to preserve the
			// pristine nature of the 'event'.
			// ---------------------------------------------------------------
			MiEvent editorEvent = MiEvent.newEvent();
			editorEvent.copy(event);
			editorEvent.doTransform(getTransform());

			// ---------------------------------------------------------------
			// Make an event that we can use as a tmporary event, transformed
			// to the correct location as per a event handler's request.
			// ---------------------------------------------------------------
			MiEvent gEventOrig = MiEvent.newEvent();

			// ---------------------------------------------------------------
			// Make an event reference that will reference one of the above two
			// newly created events and the sent to the event handler.
			// ---------------------------------------------------------------
			MiEvent gEvent;

			// ---------------------------------------------------------------
			// For each event handler grabbing all events...
			// ---------------------------------------------------------------
			for (int i = 0; i < currentGrabs.size(); ++i)
				{
				MiiEventHandler grab = (MiiEventHandler )currentGrabs.elementAt(i);
				// ---------------------------------------------------------------
				// If the event handler wants the event transformed to something
				// else besides this window then we need to transform the event.
				// ---------------------------------------------------------------
				if (grab.getObject() != this)
					{
					gEvent = gEventOrig;
					gEvent.copy(event);
					FastVector transforms = new FastVector();

if (grab.getObject() == null)
System.out.println("GRAB HAS NULL EDITOR: " + grab);
					// ---------------------------------------------------------------
					// Transform the event to the desired part's transform.
					// ---------------------------------------------------------------
					MiUtility.getTransformsAlongPath(this, grab.getObject(), transforms);
					for (int j = transforms.size() - 1; j >= 0; --j)
						{
						gEvent.doTransform((MiiTransform )transforms.elementAt(j));
						}
					gEvent.editor = grab.getObject().getContainingEditor();
					}
				else
					{
					gEvent = editorEvent;
					}
				// ---------------------------------------------------------------
				// Send the event to the grabbing event handler...
				// ---------------------------------------------------------------
				if (MiDebug.debug 
					&& MiDebug.isTracing(null, MiDebug.TRACE_EVENT_HANDLER_GRABS))
					{
					MiDebug.println("Dispatching event to grab event handler: " + grab);
					}
				if ((grab.isEnabled())
					&& (grab.processEvent(gEvent) == Mi_CONSUME_EVENT))
					{
					MiEvent.freeEvent(editorEvent);
					MiEvent.freeEvent(gEventOrig);
					return(Mi_CONSUME_EVENT);
					}
				}
			// ---------------------------------------------------------------
			// No grabbing event handler consumed the event, but we need to
			// consume it here anyway.
			// ---------------------------------------------------------------
			MiEvent.freeEvent(editorEvent);
			MiEvent.freeEvent(gEventOrig);
			return(Mi_CONSUME_EVENT);
			}

		// *****************************************************************************
		// Send event to any registered global event handlers (short cuts/hotKeys)
		// *****************************************************************************
		if ((event.key != 0) || (event.isMouseButtonEvent()))
			{
			event.handlerTargetType = Mi_SHORT_CUT_EVENT_HANDLER_TYPE;
			event.locationSpecific = false;
		
			// ---------------------------------------------------------------
			// Forward this first to subwindows.
			// ---------------------------------------------------------------
			for (int i = 0; i < getNumberOfAttachments(); ++i)
				{
				MiPart attachment = getAttachment(i);
				// ---------------------------------------------------------------
				// Is it a 'sub-window' ? ...
				// ---------------------------------------------------------------
				if ((attachment instanceof MiWindowBorder)
					&& (attachment.pick(event.worldMouseFootPrint)))
					{
					if (((MiWindowBorder )attachment).getSubject().dispatchEvent(event) == Mi_CONSUME_EVENT)
						return(Mi_CONSUME_EVENT);
					}
				}
			event.editor = this;
			// ---------------------------------------------------------------
			// Now dispatch it to local location independent event handlers
			// ---------------------------------------------------------------
			for (int i = 0; i < windowWideEventHandlers.size(); ++i)
				{
				MiiEventHandler handler 
					= (MiiEventHandler )windowWideEventHandlers.elementAt(i);
				if ((handler.getType() == event.handlerTargetType)
					&& (handler.isEnabled()))
					{
					if (handler.processEvent(event) == Mi_CONSUME_EVENT)
						{
						if (MiDebug.debug 
							&& MiDebug.isTracing(null, MiDebug.TRACE_EVENT_DISPATCHING))
							{
							MiDebug.println("Shortcut: " + handler 
								+ " consumed event: " + event);
							}
						return(Mi_CONSUME_EVENT);
						}
					}
				}
			}

		// *****************************************************************************
		// Send event to ordinary event handlers in picklist
		// *****************************************************************************
		event.handlerTargetType = Mi_ORDINARY_EVENT_HANDLER_TYPE;
		event.locationSpecific = true;
		event.editor = this;

		// Redo this incase a handler messed with event's targetPath
		targetPathIndexOfThis = event.getTargetPath().indexOf(this);
		dispatchLocationSpecificEvent(event, targetPathIndexOfThis);

		// ---------------------------------------------------------------
		// Root windows must consume all events...
		// ---------------------------------------------------------------
		return(Mi_CONSUME_EVENT);
		}
	
					/**------------------------------------------------------
	 				 * Dispatches the given event to each part in the targetPath,
					 * starting with the topmost part (element #0 in the 
					 * targetPath) and ending with this window or until one of
					 * the event handlers of one of these parts consumes the 
					 * event.
					 * @param event		the event to dispatch
					 * @param targetPathIndexOfThis	
					 * 			index of this window in the event's targetPath
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see this event
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see this event
					 * @see			MiPart#dispatchEvent
					 *------------------------------------------------------*/
	private		int		dispatchLocationSpecificEvent(MiEvent event, int targetPathIndexOfThis)
		{
		if (targetPathIndexOfThis < 0)
			{
			return(super.dispatchEvent(event));
			}
		return(dispatchEvent(event, targetPathIndexOfThis, targetPathIndexOfThis, event.getTargetPath()));
		}
	private		int		dispatchLocationSpecificTargetListEvent(MiEvent event, int targetListIndexOfThis)
		{
		if (targetListIndexOfThis < 0)
			{
			return(super.dispatchEvent(event));
			}
		return(dispatchEvent(event, targetListIndexOfThis, targetListIndexOfThis, event.getTargetList()));
		}
					/**------------------------------------------------------
	 				 * Dispatches the given event to each part in the targetPath,
					 * starting with the part at the given index in the
					 * targetPath and ending with this window or until one of
					 * the event handlers of one of these parts consumes the 
					 * event. I.E. this method does the actual 'event bubbling'
					 * by calling itself recursively.
					 * @param event		the event to dispatch
					 * @param i		the index into the targetPath (or targetList)
					 * @param targetPathIndexOfThis	
					 * 			index of this window in the event's targetPath (or targetList)
					 * @param targetPath
					 * 			the event's targetPath (or targetList)
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see this event
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see this event
					 * @see			MiPart#dispatchEvent
					 *------------------------------------------------------*/
	private		int		dispatchEvent(MiEvent event, int i, int targetPathIndexOfThis, MiParts targetPath)
		{
		MiPart part = targetPath.elementAt(i);
		if (i > 0)
			{
			// ---------------------------------------------------------------
			// If this part from the targetPath has a transform...
			// ---------------------------------------------------------------
			if (part.getTransform() != null)
				{
				// ---------------------------------------------------------------
				// Make a copy of the given event and transform it to what the part
				// wants.
				// ---------------------------------------------------------------
				MiEvent tEvent = MiEvent.newEvent();
				tEvent.copy(event);
				tEvent.doTransform(part.getTransform());

				// ---------------------------------------------------------------
				// If the part is an editor, tell the event copy about it...
				// ---------------------------------------------------------------
				if (part instanceof MiEditor)
					tEvent.editor = (MiEditor )part;

				// ---------------------------------------------------------------
				// Send the now transformed event down the hierarchy to the topmost
				// part (element #0 in the targetPath). If one of the parts above 
				// this one consumed the event, then we are done.
				// ---------------------------------------------------------------
				if (dispatchEvent(tEvent, i - 1, targetPathIndexOfThis, targetPath) == Mi_CONSUME_EVENT)
					{
					MiEvent.freeEvent(tEvent);
					return(Mi_CONSUME_EVENT);
					}
				// ---------------------------------------------------------------
				// The parts on top of this one did not consume the event...
				// ---------------------------------------------------------------
				MiEvent.freeEvent(tEvent);
				}
			else
				{
				// ---------------------------------------------------------------
				// Send the event down the hierarchy to the topmost part (element #0
				// in the targetPath). If one of the parts above this one consumed
				// the event, then we are done.
				// ---------------------------------------------------------------
				if (dispatchEvent(event, i - 1, targetPathIndexOfThis, targetPath) == Mi_CONSUME_EVENT)
					return(Mi_CONSUME_EVENT);
				// ---------------------------------------------------------------
				// The parts on top of this one did not consume the event...
				// ---------------------------------------------------------------
				}
			}
		// ---------------------------------------------------------------
		// If debug is tracing this, print out that we are dispatching which
		// event to which part.
		// ---------------------------------------------------------------
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_DISPATCHING))
			{
			if ((event.getType() != MiEvent.Mi_TIMER_TICK_EVENT)
				&& (event.getType() != MiEvent.Mi_IDLE_EVENT)
				&& (event.getType() != MiEvent.Mi_MOUSE_MOTION_EVENT) )
				{
				MiDebug.println("Dispatching <"
					+ MiEventHandler.getNameForType(event.handlerTargetType)
					+ "> event: " + event + ", to: " 
					+ ((i == targetPathIndexOfThis) ? this : part));
				}
			}
		// ---------------------------------------------------------------
		// If we have gotten all the way to this window, then call the
		// super classes dispatch routine to dispatch the event to all this
		// window's event handlers.
		// ---------------------------------------------------------------
		if (i == targetPathIndexOfThis)
			{
			return(super.dispatchEvent(event));
			}

		// ---------------------------------------------------------------
		// Dispatch the event to the next part up in the target path.
		// ---------------------------------------------------------------
		int status = part.dispatchEvent(event);
//System.out.println("EVENT RETURNED : " + (status == Mi_CONSUME_EVENT ? "Consumed" : "Propogated"));
		return(status);
		}
					/**------------------------------------------------------
					 * Prepends the given event handler to the list of 'grab'
					 * event handlers. This is a special list of event handlers
					 * that are executed immediately after all event monitors
					 * and before any other event handlers in the containing
					 * root window. This is used when an event handler needs 
					 * complete control over the event handling for a short 
					 * period of time.
					 * The MiEvents sent to the event handler will be transformed
					 * to the coordinate space of the eh.getObject(). If 
					 * eh.getObject() is null then it is set to be equal to this
					 * MiWindow.
					 * 
					 * @param eh		the event handler to prepend
					 * @see			#removeGrabEventHandler
					 *------------------------------------------------------*/
	public		void		prependGrabEventHandler(MiiEventHandler eh)
		{
		// ----------------------------------------------------------
		// Store in the event handler which part's coordinate space it
		// want events transformed to.
		// ----------------------------------------------------------
		if ((eh.getObject() == null) || (!isContainerOf(eh.getObject())))
			{
			eh.setObject(this);
			}

		// ----------------------------------------------------------
		// If this is not a root window, prepend it to the list in this
		// window's root window.
		// ----------------------------------------------------------
		if (!isARootWindow)
			{
			getRootWindow().prependGrabEventHandler(eh);
			return;
			}

		// ----------------------------------------------------------
		// This is a root window.
		// If debug is tracing this, print out information about the 
		// addition of the grab event handler.
		// ----------------------------------------------------------
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_HANDLER_GRABS))
			MiDebug.println("Adding grab event handler: " + eh);

		// ----------------------------------------------------------
		// Prepend the event handelr to the list of grabs
		// ----------------------------------------------------------
		grabs.insertElementAt(eh, 0);

		// ----------------------------------------------------------
		// If debug is tracing this, print out information about the 
		// list of grab event handlers.
		// ----------------------------------------------------------
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_HANDLER_GRABS))
			MiDebug.println("Current grabs = " + grabs);
		}
					/**------------------------------------------------------
					 * Removes the given event handler to the list of 'grab'
					 * event handlers. 
					 * @param eh		the event handler to remove
					 * @exception		RuntimeException if the given event
					 *			handler is not in the list of 'grab'
					 *			event handlers
					 * @see			#prependGrabEventHandler
					 *------------------------------------------------------*/
	public		void		removeGrabEventHandler(MiiEventHandler eh)
		{
		// ----------------------------------------------------------
		// If this is not a root window, remove it from the list in this
		// window's root window.
		// ----------------------------------------------------------
		if (!isARootWindow)
			{
			getRootWindow().removeGrabEventHandler(eh);
			return;
			}
		// ----------------------------------------------------------
		// For debug...
		// ----------------------------------------------------------
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_HANDLER_GRABS))
			MiDebug.println("Removing grab event handler: " + eh);

		// ----------------------------------------------------------
		// Remove the event handler from the list of grabs. If not found
		// in the list, an exception is thrown in order to help debug
		// this serious and typically hard to detect problem.
		// ----------------------------------------------------------
		if (!grabs.removeElement(eh))
			{
			for (int i = 0; i < grabs.size(); ++i)
				System.out.println(grabs.elementAt(i));
			throw new RuntimeException("MICA: Unable to remove grab event handler: event handler not found: " + eh);
			}
		// ----------------------------------------------------------
		// For debug...
		// ----------------------------------------------------------
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_HANDLER_GRABS))
			{
			MiDebug.println("Removed grab, grabs now number = " + grabs.size());
			if (grabs.size() > 0)
				MiDebug.println("Current grabs = " + grabs);
			}
		}
					/**------------------------------------------------------
					 * Appends the given event handler to the list of 'global'
					 * event handlers. This is a special list of event handlers
					 * that are executed immediately before sending the event
					 * to the event handlers of the parts in the targetPath.
					 * This is used when an event handler processes events
					 * that can occur anywhere in the window. An event handler
					 * specifies this fact by using it's setPositionDependent()
					 * method.
					 * The MiEvents sent to the event handler will be transformed
					 * to the coordinate space of the eh.getObject(). If 
					 * eh.getObject() is null then it is set to be equal to
					 * this MiWindow.
					 * 
					 * @param eh		the event handler to append
					 * @see			#removeGlobalEventHandler
					 * @see			MiEventHandler#setPositionDependent
					 *------------------------------------------------------*/
	public		void		appendGlobalEventHandler(MiiEventHandler eh)
		{
		if (eh.getObject() == null)
			eh.setObject(this);
		windowWideEventHandlers.addElement(eh);
		}
	public		void		insertGlobalEventHandler(MiiEventHandler eh, int index)
		{
		if (eh.getObject() == null)
			eh.setObject(this);
		windowWideEventHandlers.insertElementAt(eh, index);
		}
					/**------------------------------------------------------
					 * Removes the given event handler to the list of 'global'
					 * event handlers. 
					 * @param eh		the event handler to remove
					 * @exception		RuntimeException if the given event
					 *			handler is not in the list of 'global'
					 *			event handlers
					 * @see			#appendGlobalEventHandler
					 *------------------------------------------------------*/
	public		void		removeGlobalEventHandler(MiiEventHandler eh)
		{
		// This should really be here?!?
		eh.setObject(null);
		if (!windowWideEventHandlers.removeElement(eh))
			{
			for (int i = 0; i < windowWideEventHandlers.size(); ++i)
				System.out.println(windowWideEventHandlers.elementAt(i));
			throw new RuntimeException("Unable to remove windowWide event handler: event handler not found: " + eh);
			}
		}

	//***************************************************************************************
	// Debug Routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the number of global event handlers.
					 * @see			#appendGlobalEventHandler
					 * @see			#getGlobalEventHandler
					 *------------------------------------------------------*/
	public		int		getNumberOfGlobalEventHandlers()
		{
		return(windowWideEventHandlers.size());
		}
					/**------------------------------------------------------
					 * Gets the global event handler at the given index.
					 * @param index		index of the event handler to get
					 * @see			#getNumberOfGlobalEventHandlers
					 *------------------------------------------------------*/
	public		MiiEventHandler	getGlobalEventHandler(int index)
		{
		return((MiiEventHandler )windowWideEventHandlers.elementAt(index));
		}
					/**------------------------------------------------------
					 * Gets the number of grabbing event handlers.
					 * @see			#prependGrabEventHandler
					 * @see			#getGrabEventHandler
					 *------------------------------------------------------*/
	public		int		getNumberOfGrabEventHandlers()
		{
		return(grabs.size());
		}
					/**------------------------------------------------------
					 * Gets the grabbing event handler at the given index.
					 * @param index		index of the event handler to get
					 * @see			#getNumberOfGrabEventHandlers
					 *------------------------------------------------------*/
	public		MiiEventHandler	getGrabEventHandler(int index)
		{
		return((MiiEventHandler )grabs.elementAt(index));
		}
					/**------------------------------------------------------
					 * Sets the title text in the border of the window.
					 * @param str		the title text
					 *------------------------------------------------------*/
	public		void		setTitle(String str)
		{
		}
	// Need to make this a pluggable component interface so other adjusts of the list can occur
	protected	void		sortPickedDeepList(MiBounds worldMouseFootPrint, MiParts targetList)
		{
		// Move connPts on top of any connections that lie immediately on 
		// top (i.e. no intervening parts) of the connPt
		for (int i = 0; i < targetList.size(); ++i)
			{
			MiPart connPt = targetList.elementAt(i);
			if (connPt.getResource(
				MiManagedPointManager.Mi_MANAGED_POINT_MANAGER_RESOURCE_NAME) 
					instanceof MiConnectionPointManager)
				{
				int swapToIndex = i;
				for (int j = i - 1; j >= 0; --j)
					{
					if ((targetList.elementAt(j) instanceof MiConnection)
						&& (targetList.elementAt(j).isSelected()))
						{
						break;
						}
					swapToIndex = j;
					}
				if (swapToIndex != i)
					{
					targetList.removeElementAt(i);
					targetList.insertElementAt(connPt, swapToIndex);
					}
				}
			}
		}
	}

