
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
import java.awt.Rectangle; 
import java.awt.Window; 
import java.awt.Frame; 
import java.awt.Dialog; 
import java.awt.Event; 
import java.awt.Component; 
//import java.awt.swing.JComponent; 
//import com.sun.java.swing.JComponent;
import javax.swing.JComponent;
import java.awt.event.*;


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiNativeWindow extends MiWindow implements MiiCommandHandler, MiiCommandNames
	{
	private		MiiAWTWindowWrapper windowWrapper;
	private		Window		window;
	private		Frame		frame;
	private		Dialog		dialog;
	private		MiCanvas	canvas;
	private		boolean		inGuardedCloseOrDestroy;
	private		boolean		okToCloseOrDestroy;
	private		boolean		isADialog;
	private		boolean		isJustAComponent;
	private		boolean		forceResizeOfWindowContainingComponent = true;
	private		MiJDKAPIComponentType nativeComponentType;
	private		String		sendCommandInsteadOfDispose;


	public				MiNativeWindow(String title)
		{
		this(null, 
			title, 
			new MiBounds(
				MiCanvas.getScreenBounds().getCenterX() - 50,
				MiCanvas.getScreenBounds().getCenterY() - 50,
				MiCanvas.getScreenBounds().getCenterX() + 50,
				MiCanvas.getScreenBounds().getCenterY() + 50),
			false);
		}
	public				MiNativeWindow(String title, MiBounds windowSize)
		{
		this(null, title, windowSize, false);
		}

	public				MiNativeWindow(Frame parent, String title, MiBounds windowSize)
		{
		this(parent, title, windowSize, MiSystem.getDefaultJDKAPIComponentType());
		}
	public				MiNativeWindow(
						Frame parent, 
						String title, 
						MiBounds windowSize, 
						MiJDKAPIComponentType nativeComponentType)
		{
		super(windowSize);
		super.setVisible(false);

		MiSystem.addWindow(this);

		window = parent;
		frame = parent;
		isADialog = false;
		isJustAComponent = true;
		setWorldBounds(windowSize);
		setUniverseBounds(windowSize);
		if (nativeComponentType == null)
			nativeComponentType = MiSystem.getDefaultJDKAPIComponentType();

		this.nativeComponentType = nativeComponentType;

		if (nativeComponentType == Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE)
			{
			if (MiSystem.getJDKVersion() >= 1.1)
				canvas = new MiCanvas(new MiAWTCanvas(), windowSize);
			else
				canvas = new MiCanvas(new MiAWT102Canvas(), windowSize);
			}
		else if (nativeComponentType == Mi_AWT_1_0_2_HEAVYWEIGHT_COMPONENT_TYPE)
			{
			canvas = new MiCanvas(new MiAWT102Canvas(), windowSize);
			}
		else if (nativeComponentType == Mi_AWT_1_1_HEAVYWEIGHT_COMPONENT_TYPE)
			{
			canvas = new MiCanvas(new MiAWTCanvas(), windowSize);
			}
		else // Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE
			{
			canvas = new MiCanvas(new MiJCanvas(), windowSize);
			}

		setCanvas(canvas);
		MiToolkit.setBackgroundAttributes(this, MiiToolkit.Mi_TOOLKIT_WINDOW_ATTRIBUTES);

		appendEventHandler(new MiIMouseFocus());
		appendEventHandler(new MiIMouseEnterAndExit());
		appendEventHandler(new MiWindowDestroyEventCatcher());
		appendEventHandler(new MiIDisplayContextCursor());
		appendEventHandler(new MiIDisplayToolHints());
		appendEventHandler(new MiIDisplayContextMenu());
		appendEventHandler(new MiIDisplayHelpDialog());

		frame.addWindowListener(new MiAWTWindowListener(this));

		if (title != null)
			setTitle(title);
		}
	public		Component	getAWTComponent()
		{
		return((Component )getCanvas().getNativeComponent());
		}
	public		JComponent	getSwingComponent()
		{
		return((JComponent )getCanvas().getNativeComponent());
		}
	public				MiNativeWindow(
						MiNativeWindow parent, 
						String title, 
						MiBounds windowSize, 
						boolean modal)
		{
		this(parent, title, windowSize, modal, true);
		}
	public				MiNativeWindow(
						MiNativeWindow parent, 
						MiBounds windowSize, 
						boolean modal)
		{
		this(parent, "", windowSize, modal, false);
		}
	public				MiNativeWindow(
						MiNativeWindow parent, 
						String title, 
						MiBounds windowSize, 
						boolean modal,
						boolean hasBorder)
		{
		super(windowSize);
		super.setVisible(false);

		MiSystem.addWindow(this);

		setWorldBounds(windowSize);
		setUniverseBounds(windowSize);
		isJustAComponent = false;
		if (parent == null)
			{
			if (hasBorder)
				{
				windowWrapper = new MiAWTFrameWrapper(this, MiSystem.getProperty(title, title)); 
				frame = (Frame )windowWrapper;
				window = frame;
				}
			else
				{
				frame = new Frame();
				windowWrapper = new MiAWTWindowWrapper(this, frame);
				window = (Window )windowWrapper;
				}
			isADialog = false;
			}
		else
			{
			frame = parent.getFrame();
			if (hasBorder)
				{
				// Free locks on parent window because AWT may want to 
				// call it while it is creating the dialog box
				int numLocks = parent.getRootWindow().getCanvas().freeAccessLocks(Thread.currentThread());

				// FIX: MODAL LOCKS UP SYSTEM! 
				windowWrapper = new MiAWTDialogWrapper(
					this, frame, MiSystem.getProperty(title, title), false);

				parent.getRootWindow().getCanvas().getAccessLocks(numLocks);
				dialog = (Dialog )windowWrapper;
				window = dialog;
				}
			else
				{
				windowWrapper = new MiAWTWindowWrapper(this, frame);
				window = (Window )windowWrapper;
				}
			isADialog = true;
			}

/***
		if (parent != null)
			{
			nativeComponentType = parent.nativeComponentType;
			}
		if (nativeComponentType == null)
			{
			nativeComponentType = MiSystem.getDefaultJDKAPIComponentType();
			}

***/
		if (nativeComponentType != null)
			{
			if (nativeComponentType == Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE)
				{
				if (MiSystem.getJDKVersion() >= 1.1)
					canvas = new MiCanvas(new MiAWTCanvas(), windowSize);
				else
					canvas = new MiCanvas(new MiAWT102Canvas(), windowSize);
				}
			else if (nativeComponentType == Mi_AWT_1_0_2_HEAVYWEIGHT_COMPONENT_TYPE)
				{
				canvas = new MiCanvas(new MiAWT102Canvas(), windowSize);
				}
			else if (nativeComponentType == Mi_AWT_1_1_HEAVYWEIGHT_COMPONENT_TYPE)
				{
				canvas = new MiCanvas(new MiAWTCanvas(), windowSize);
				}
			else // Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE
				{
				canvas = new MiCanvas(new MiJCanvas(), windowSize);
				}
			}
		else if (MiSystem.getJDKVersion() >= 1.1)
			{
			canvas = new MiCanvas(new MiAWTCanvas(), windowSize);
			nativeComponentType = Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE;
			}
		else
			{
			canvas = new MiCanvas(new MiAWT102Canvas(), windowSize);
			nativeComponentType = Mi_AWT_1_0_2_HEAVYWEIGHT_COMPONENT_TYPE;
			}

		setCanvas(canvas);
		window.add("Center", (Component )canvas.getNativeComponent());

		MiBounds screenBounds = MiCanvas.getScreenBounds();

		// Center window if positioned at the left and/or top
		MiDistance tx = 0;
		MiDistance ty = 0;
		if (windowSize.getXmin() == 0)
			tx = screenBounds.getWidth()/2 - windowSize.getWidth()/2;
		if (windowSize.getYmin() == 0)
			ty = screenBounds.getHeight()/2 - windowSize.getHeight()/2;

		// Divide by 2 to give room for the window to grow...
		windowSize.translate(tx/2, 1.5 * ty);

		java.awt.Insets insets = window.insets();

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_WINDOW_AND_CANVAS_RESIZING))
			{
			MiDebug.println(this + " Newly created canvas size = " + windowSize);
			MiDebug.println(this + " Request reshape new canvas window to: x = " + 
				(int )(windowSize.getXmin() + insets.left)
				+ ", y = " +(int )(screenBounds.getYmax() 
					- windowSize.getYmax() - insets.top)
				+ ", width = "+(int )(windowSize.getWidth() 
					+ insets.left + insets.right) 
				+ ", height = "+(int )(windowSize.getHeight() 
					+ insets.bottom + insets.bottom));
			}

		// FIX: Maybe adjust this in setVisible when know actual preferredSize
		window.reshape(
			(int )(windowSize.getXmin() + insets.left),
			(int )(screenBounds.getYmax() - windowSize.getYmax() - insets.top),
			(int )(windowSize.getWidth()) + insets.left + insets.right, 
			(int )(windowSize.getHeight()) + insets.bottom + insets.bottom);

		window.invalidate();
		window.validate();

		MiToolkit.setBackgroundAttributes(this, MiiToolkit.Mi_TOOLKIT_WINDOW_ATTRIBUTES);

		appendEventHandler(new MiIMouseFocus());
		appendEventHandler(new MiIMouseEnterAndExit());
		appendEventHandler(new MiWindowDestroyEventCatcher());
		appendEventHandler(new MiIDisplayContextCursor());
		appendEventHandler(new MiIDisplayToolHints());
		appendEventHandler(new MiIDisplayContextMenu());
		appendEventHandler(new MiIDisplayHelpDialog());
		}
	public		void		setDefaultCloseCommand(String command)
		{
		sendCommandInsteadOfDispose = command;
		}
	public		String		getSendCommandInsteadOfDispose()
		{
		return(sendCommandInsteadOfDispose);
		}
	public		void		setDeviceBounds(MiBounds bounds)
		{
		// Set the bounds of the canvas (i.e. the inside of this window).
//MiDebug.println("Native Window.setDeviceBounds = " + bounds);
		super.setDeviceBounds(bounds);

		if (isJustAComponent)
			{
			getRootWindow().getCanvas().getAccessLock();

			frame.invalidate();
			if (forceResizeOfWindowContainingComponent)
				{
				// Causes screen to flash- as this causes AWT/Swing to 
				// generate a setBounds event of the previous size or
				// multiple expose events
				frame.pack();
				}
			frame.validate();
			getRootWindow().getCanvas().freeAccessLock();

			return;
			}

		// Sun Solaris JDK 1.1.5 insets are sometimes large negative numbers
		// and large positive ones for dialogs. Using zero insets does not work
		// perfectly for dialogs.
		java.awt.Insets insets = window.insets();
		if ((MiSystem.getJDKVersion() == 1.15)
			&& ((insets.left < 0) || (insets.left > 35)
			|| (insets.right < 0) || (insets.right > 35)
			|| (insets.top < 0) || (insets.top > 35)
			|| (insets.bottom < 0) || (insets.bottom > 35)))
			{
			insets = new java.awt.Insets(0,0,0,0);
			}
	
		int width = (int )(bounds.getWidth()) + insets.left + insets.right;
		int height = (int )(bounds.getHeight()) + insets.bottom + insets.top;
		if ((window.bounds().width != width) || (window.bounds().height != height))
			{
			if (MiDebug.debug 
				&& MiDebug.isTracing(null, MiDebug.TRACE_WINDOW_AND_CANVAS_RESIZING))
				{
				MiDebug.println(this + " Request resize to (width+height): " + 
					width + "x" + height);
				}
	
			getRootWindow().getCanvas().getAccessLock();

			window.resize(width, height);

			window.invalidate();
			window.validate();

			getRootWindow().getCanvas().freeAccessLock();
			}
		}
	public		void		getWindowLock()
		{
		getRootWindow().getCanvas().getAccessLock();
		}
	public		void		freeWindowLock()
		{
		getRootWindow().getCanvas().freeAccessLock();
		}

					/**------------------------------------------------------
	 				 * This enables/disables the resizing of the window containing
					 * this Canvas if this is just a component in a Java Window.
					 * By default this is equal to true. Set to false if your area
					 * is resizing only slightly but causing the whole window to
					 * repaint (Mica can often reorganize a layout within a 'slightly'
					 * smaller area with no problems)
					 * @param flag	true if containing Java window resizes if 
					 *		required when this container resizes (grows)
					 * @see 	#setForceResizeOfWindowContainingComponent
					 *------------------------------------------------------*/
	public		void		setForceResizeOfWindowContainingComponent(boolean flag)
		{
		forceResizeOfWindowContainingComponent = flag;
		}
	public		boolean		getForceResizeOfWindowContainingComponent()
		{
		return(forceResizeOfWindowContainingComponent);
		}
	public		void		toFront()
		{
		window.toFront();
		}
	public		void		setTitle(String title)
		{
		if (windowWrapper != null)
			windowWrapper.setTitle(title);
		else
			frame.setTitle(title);
		}
	public		String		getTitle()
		{
		if (windowWrapper != null)
			return(windowWrapper.getTitle());
		return(frame.getTitle());
		}
	public		MiJDKAPIComponentType	getNativeComponentType()
		{
		return(nativeComponentType);
		}
	public		void		setVisible(boolean flag)
		{
		if (!flag)
			{
			// If we are in guardedCloseOrDestroy, then some routine called 
			// from that method wants to hide the window, let it.. 
			// OR if we are not,then check to see if it is ok to hide this window.
			if ((!inGuardedCloseOrDestroy) && (!guardedCloseOrDestroy(Mi_WINDOW_CLOSE_ACTION)))
				{
				return;
				}
			}
		super.setVisible(flag);
		if (flag)
			{
			validateLayout();
			window.show();
			resume();
			dispatchAction(Mi_WINDOW_OPEN_ACTION);
			}
		else
			{
			window.hide();
			suspend();
			dispatchAction(Mi_WINDOW_CLOSE_ACTION);
			}
		}

	public		boolean		dispose()
		{
		if (guardedCloseOrDestroy(Mi_WINDOW_DESTROY_ACTION))
			{
			_dispose();
			return(true);
			}
		return(false);
		}

	protected	void		_dispose()
		{
		Event destroyEvent = new Event(this, Event.WINDOW_DESTROY, "WINDOW_DESTROY");
		//canvas.deliverEvent(destroyEvent);
		window.dispose(); 
		dispatchAction(Mi_WINDOW_DESTROY_ACTION);
		canvas.dispose();
		MiSystem.removeWindow(this);
		}
	public		Frame		getFrame()
		{
		return(frame);
		}
	public		Window		getWindow()
		{
		return(window);
		}
	// Mi_WINDOW_CLOSE_ACTION or Mi_WINDOW_DESTROY_ACTION
	// return true if OK to close or destroy
	protected	boolean		guardedCloseOrDestroy(int actionType)
		{
		// If they click on X to destroy window while we are displaying 
		// 'Save Changes?', we want to ignore the destroy
//MiDebug.println(this + "guardedCloseOrDestroy");
		if (inGuardedCloseOrDestroy)
			{
			return(okToCloseOrDestroy);
			}

//MiDebug.println(this + " 2222 guardedCloseOrDestroy");
		okToCloseOrDestroy = false;
		inGuardedCloseOrDestroy = true;
		
			// Check to see if ok to close this window...
//MiDebug.println(this + " 3333 guardedCloseOrDestroy");
		if (dispatchActionRequest(actionType))
			{
			// If so, then see if an action handler wants to do the actual dispose
			int result = dispatchAction(actionType | Mi_EXECUTE_ACTION_PHASE);
			// If not... close it here...
//MiDebug.println(this + " 4444 guardedCloseOrDestroy");
			okToCloseOrDestroy = true;
			if (result == Mi_PROPOGATE)
				{
//MiDebug.println(this + " 5555 guardedCloseOrDestroy sendCommandInsteadOfDispose = " + sendCommandInsteadOfDispose);
				if ((sendCommandInsteadOfDispose != null) 
					&& (actionType == Mi_WINDOW_DESTROY_ACTION))
					{
					processCommand(sendCommandInsteadOfDispose);
					okToCloseOrDestroy = false;
					}
				}
			}
		else
			{
//MiDebug.println(this + " 6666 guardedCloseOrDestroy");
			okToCloseOrDestroy = false;
			}
		inGuardedCloseOrDestroy = false;
		return(okToCloseOrDestroy);
		}
					/**------------------------------------------------------
	 				 * Processes the given command.
					 * @param command  	the command to execute
					 * @implements		MiiCommandHandler#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
//MiDebug.println(this + " processs command = " + command);
		if ((command.equalsIgnoreCase(Mi_CLOSE_COMMAND_NAME))
			|| (command.equalsIgnoreCase(Mi_CLOSE_WINDOW_COMMAND_NAME)))
			{
			setVisible(false);
			}
		else if ((command.equalsIgnoreCase(Mi_OPEN_COMMAND_NAME))
			|| (command.equalsIgnoreCase(Mi_OPEN_WINDOW_COMMAND_NAME)))
			{
			setVisible(true);
			}
		else if ((command.equalsIgnoreCase(Mi_ICONIFY_COMMAND_NAME))
			|| (command.equalsIgnoreCase(Mi_ICONIFY_WINDOW_COMMAND_NAME)))
			{
			//iconify();
			}
		else if ((command.equalsIgnoreCase(Mi_DEICONIFY_COMMAND_NAME))
			|| (command.equalsIgnoreCase(Mi_DEICONIFY_WINDOW_COMMAND_NAME)))
			{
			//deIconify();
			}
		else if (command.equalsIgnoreCase(Mi_TOGGLE_FULLSCREEN_COMMAND_NAME))
			{
			//toggleFullScreen();
			}
		else if (command.equalsIgnoreCase(Mi_QUIT_COMMAND_NAME))
			{
			if (guardedCloseOrDestroy(Mi_WINDOW_DESTROY_ACTION))
				{
/* Should this REALLY be here? */
				if (!MiSystem.isApplet())
					{
					System.exit(0);
					}
				else
					{
					_dispose();
					}
				}
			}
		else if (command.equalsIgnoreCase(Mi_DESTROY_WINDOW_COMMAND_NAME))
			{
			dispose();
			}
		}
	}
interface MiiAWTWindowWrapper
	{
	void		setTitle(String title);
	String		getTitle();
	}
class MiAWTWindowWrapper extends Window implements MiiAWTWindowWrapper
	{
	private		boolean		inGuardedDispose;
	private		MiNativeWindow	nativeWindow;


	public				MiAWTWindowWrapper(MiNativeWindow window, Frame frame)
		{
		super(frame);
		nativeWindow = window;
		}
	public		void		setTitle(String title)
		{
		}
	public		String		getTitle()
		{
		return("");
		}
					/**------------------------------------------------------
	 				 * Causes the window to be redrawn as per the awt.Component
					 * API. Used only for MS Windows to force redraw of entire
					 * window.
					 * @param g		the awt.graphics renderer
					 *------------------------------------------------------*/
	public 		void 		paint(java.awt.Graphics g)
		{
		// We will redraw the entire Window because on Windows
		// there is a bug which causes the wrong clipRect to be given.
		if (MiSystem.isMSWindows())
			{
			nativeWindow.getAccessLock();
			nativeWindow.exposeArea(nativeWindow.getDeviceBounds());
			nativeWindow.freeAccessLock();
			}
		super.paint(g);
		}

	//public synchronized void 	dispose()
	public 		void 		dispose()
		{
		if (!inGuardedDispose)
			{
			inGuardedDispose = true;
			boolean safe = nativeWindow.guardedCloseOrDestroy(
						MiiActionTypes.Mi_WINDOW_DESTROY_ACTION);
			inGuardedDispose = false;
			if (!safe)
				return;
			}
		super.dispose();
		}
	
	//public synchronized boolean 	handleEvent(Event event) 
	public 		boolean 	handleEvent(Event event) 
		{
		if (event.id == java.awt.Event.WINDOW_DESTROY)
			{
			return(nativeWindow.getRootWindow().getCanvas().handleEvent(event));
			}

		if ((MiSystem.isMSWindows())
			&& ((event.id == java.awt.Event.WINDOW_DEICONIFY)
			|| (event.id == java.awt.Event.WINDOW_EXPOSE)))
			{
			nativeWindow.getRootWindow().exposeArea(
				nativeWindow.getRootWindow().getDeviceBounds());
			}

		return(super.handleEvent(event));
		}
	public		String		toString()
		{
		return(super.toString() + "." + getTitle());
		}
	}
class MiAWTDialogWrapper extends Dialog implements MiiAWTWindowWrapper
	{
	private		boolean		inGuardedDispose;
	private		MiNativeWindow	nativeWindow;


	public				MiAWTDialogWrapper(
						MiNativeWindow window,
						Frame frame, 
						String title, 
						boolean modal)
		{
		super(frame, title, modal);
		nativeWindow = window;
		}
					/**------------------------------------------------------
	 				 * Causes the window to be redrawn as per the awt.Component
					 * API. Used only for MS Windows to force redraw of entire
					 * window.
					 * @param g		the awt.graphics renderer
					 *------------------------------------------------------*/
	public 		void 		paint(java.awt.Graphics g)
		{
		// We will redraw the entire Window because on Windows
		// there is a bug which causes the wrong clipRect to be given.
		if (MiSystem.isMSWindows())
			{
			nativeWindow.getAccessLock();
			nativeWindow.exposeArea(nativeWindow.getDeviceBounds());
			nativeWindow.freeAccessLock();
			}
		super.paint(g);
		}

	public synchronized void 	dispose()
		{
		if (!inGuardedDispose)
			{
			inGuardedDispose = true;
			boolean safe = nativeWindow.guardedCloseOrDestroy(
						MiiActionTypes.Mi_WINDOW_DESTROY_ACTION);
			inGuardedDispose = false;
			if (!safe)
				return;
			}
		super.dispose();
		}
	public synchronized boolean 	handleEvent(Event event) 
		{
		if (event.id == java.awt.Event.WINDOW_DESTROY)
			{
			return(nativeWindow.getRootWindow().getCanvas().handleEvent(event));
			}

		if ((MiSystem.isMSWindows())
			&& ((event.id == java.awt.Event.WINDOW_DEICONIFY)
			|| (event.id == java.awt.Event.WINDOW_EXPOSE)))
			{
			nativeWindow.getRootWindow().exposeArea(
				nativeWindow.getRootWindow().getDeviceBounds());
			}

		return(super.handleEvent(event));
		}
	public		String		toString()
		{
		return(super.toString() + "." + getTitle());
		}
	}
class MiAWTFrameWrapper extends Frame implements MiiAWTWindowWrapper
	{
	private		boolean		inGuardedDispose;
	private		MiNativeWindow	nativeWindow;


	public				MiAWTFrameWrapper(MiNativeWindow window, String title)
		{
		super(title);
		nativeWindow = window;
		}
					/**------------------------------------------------------
	 				 * Causes the window to be redrawn as per the awt.Component
					 * API. Used only for MS Windows to force redraw of entire
					 * window.
					 * @param g		the awt.graphics renderer
					 *------------------------------------------------------*/
	public 		void 		paint(java.awt.Graphics g)
		{
		// We will redraw the entire Window because on Windows
		// there is a bug which causes the wrong clipRect to be given.
		if (MiSystem.isMSWindows())
			{
			nativeWindow.getAccessLock();
			nativeWindow.exposeArea(nativeWindow.getDeviceBounds());
			nativeWindow.freeAccessLock();
			}
		super.paint(g);
		}

	public synchronized void 	dispose()
		{
		if (!inGuardedDispose)
			{
			inGuardedDispose = true;
			boolean safe = nativeWindow.guardedCloseOrDestroy(
						MiiActionTypes.Mi_WINDOW_DESTROY_ACTION);
			inGuardedDispose = false;
			if (!safe)
				return;
			}
		super.dispose();
		}
	public synchronized boolean 	handleEvent(Event event) 
		{
		if (event.id == java.awt.Event.WINDOW_DESTROY)
			{
			return(nativeWindow.getRootWindow().getCanvas().handleEvent(event));
			}

		if ((MiSystem.isMSWindows())
			&& ((event.id == java.awt.Event.WINDOW_DEICONIFY)
			|| (event.id == java.awt.Event.WINDOW_EXPOSE)))
			{
			nativeWindow.getRootWindow().exposeArea(
				nativeWindow.getRootWindow().getDeviceBounds());
			}

		return(super.handleEvent(event));
		}
	public		String		toString()
		{
		return(super.toString() + "." + getTitle());
		}
	}
class MiWindowDestroyEventCatcher extends MiEventHandler
	{
	public				MiWindowDestroyEventCatcher()
		{
		}
	public		void		setTarget(MiPart target)
		{
		if (!(target instanceof MiNativeWindow))
			throw new IllegalArgumentException("Event handler: MiWindowDestroyEventCatcher must be assigned to targets of type: MiNativeWindow");
		super.setTarget(target);
		}
	public		int		processEvent(MiEvent event)
		{
		if (event.getType() == Mi_WINDOW_DESTROY_EVENT)
			{
			MiNativeWindow target = (MiNativeWindow )getTarget();
			if (target.guardedCloseOrDestroy(MiiActionTypes.Mi_WINDOW_DESTROY_ACTION))
				target._dispose();
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}
class MiAWTWindowListener implements WindowListener
	{
	private		MiNativeWindow	micaWindow;

	public				MiAWTWindowListener(MiNativeWindow micaWindow)
		{
		this.micaWindow = micaWindow;
		}
    /**
     * Invoked the first time a window is made visible.
     */
    public void windowOpened(WindowEvent e)
		{
		}

    /**
     * Invoked when the user attempts to close the window
     * from the window's system menu.  If the program does not 
     * explicitly hide or dispose the window while processing 
     * this event, the window close operation will be cancelled.
     */
    public void windowClosing(WindowEvent e)
		{
		Thread t = new Thread()
			{
			public void run()
				{
				micaWindow.getAccessLock();
				micaWindow.dispose();
				micaWindow.freeAccessLock();
				}
			};
		t.start();
		}

    /**
     * Invoked when a window has been closed as the result
     * of calling dispose on the window.
     */
    public void windowClosed(WindowEvent e)
		{
		}

    /**
     * Invoked when a window is changed from a normal to a
     * minimized state. For many platforms, a minimized window 
     * is displayed as the icon specified in the window's 
     * iconImage property.
     * @see java.awt.Frame#setIconImage
     */
    public void windowIconified(WindowEvent e)
		{
		}

    /**
     * Invoked when a window is changed from a minimized
     * to a normal state.
     */
    public void windowDeiconified(WindowEvent e)
		{
		}

    /**
     * Invoked when the window is set to be the user's
     * active window, which means the window (or one of its
     * subcomponents) will receive keyboard events.
     */
    public void windowActivated(WindowEvent e)
		{
		}

    /**
     * Invoked when a window is no longer the user's active
     * window, which means that keyboard events will no longer
     * be delivered to the window or its subcomponents.
     */
    public void windowDeactivated(WindowEvent e)
		{
		}
	}


