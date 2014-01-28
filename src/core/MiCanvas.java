
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

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Frame;
import java.awt.MediaTracker; 
import java.awt.Rectangle;
import java.awt.Toolkit; 
import com.swfm.mica.util.Fifo;


// For MiCommandHandlerLineInputMonitor
import java.io.InputStream;
/**----------------------------------------------------------------------------------------------
 * This class provides an absract interface to the wrapper/driver
 * classes that interact with the native system's canvas/panel widget.
 * There are drivers for AWT, AWT 10.02 and Swing.
 * <p>
 * This class also manages the runtime thread and handles events and
 * modifies the graphics display list. As such it handles the synchronization
 * between the redraw thread(s) and event thread(s) of the native system
 * and this Mica-specific part-modifying thread.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiCanvas implements Runnable, MiiCommandHandler
	{
	public static 	String		Mi_DOUBLE_BUFFERING		= "Mi_DOUBLE_BUFFERING";
	private		MiCanvasAccessLock lock				= new MiCanvasAccessLock(this);
	private		MiWindow	window;
	private		Image 		offScreenBuffer;
	private		Rectangle 	whatWindowThinksCanvasSizeIs	= new Rectangle(0,0);
	private		Rectangle 	oldCanvasSize			= new Rectangle(0,0);
	private		Rectangle 	canvasSize			= new Rectangle(0,0);
	private		MiRenderer	renderer 			= new MiRenderer();
	private		boolean	 	prohibitDoubleBuffering;
	private		boolean	 	doubleBuffering;
	private		boolean	 	initialized;
	private		boolean	 	createdGraphicsContents;
	private		boolean	 	eventHandlingEnabled		= true;
    	private		Thread 		animator;
    	private		Fifo 		events 				= new Fifo();
	public static 	int		ANIMATIONS_PER_SECOND		= 50;
	public static 	int		CYCLES_PER_GRAPHICS_UPDATE 	= 1;
	private		int		cyclesPerSecond			= ANIMATIONS_PER_SECOND;
	private		int		cyclesPerTimerTick		= cyclesPerSecond;
	private		int		cyclesPerGraphicsUpdate		= CYCLES_PER_GRAPHICS_UPDATE;
	private		int		cyclesSinceLastEvent		= 0;
	private		boolean		idle				= false;
	private		boolean		eventDispatchingEnabled		= true;
	private		boolean		drawingEnabled			= false;
	private		int		currentMouseAppearance		= -1;
	private		MiAnimationManager	animationMan		= new MiAnimationManager();
	//private	MiCommandHandlerLineInputMonitor	commandLineMonitor;
	private		MiiCanvas	driver;
	private		boolean		disposed;
	private		boolean		redrawn;



					/**------------------------------------------------------
	 				 * Constructs a new MiCanvas. 
					 *------------------------------------------------------*/
	public				MiCanvas(MiiCanvas nativeComponent)
		{
		this(nativeComponent, null);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiCanvas of the given size and 
					 * location.
					 * @param deviceBounds		the size of the canvas
					 *------------------------------------------------------*/
	public				MiCanvas(MiiCanvas nativeComponent, MiBounds deviceBounds)
		{
		this.driver = nativeComponent;

		String dblBufProperty = MiSystem.getProperty(Mi_DOUBLE_BUFFERING);
		if (dblBufProperty != null)
			{
			doubleBuffering = dblBufProperty.equalsIgnoreCase("true");
			prohibitDoubleBuffering = dblBufProperty.equalsIgnoreCase("false");
			}

		driver.setCanvas(this);
		renderer.setCanvas(this);

		if (deviceBounds != null)
			setCanvasBounds(deviceBounds);
// inputStream.available incorrectly blocks....		commandLineMonitor = new MiCommandHandlerLineInputMonitor(this, "dump lock info", '\\');
		MiImageManager.setNativeComponent((Component )driver);
		start();
		}
	public		MiiCanvas	getNativeComponent()
		{
		return(driver);
		}
	public		void		setEventHandlingEnabled(boolean flag)
		{
		eventHandlingEnabled = flag;
		}
	public		boolean		getEventHandlingEnabled()
		{
		return(eventHandlingEnabled);
		}

					/**------------------------------------------------------
	 				 * Set whether this canvas is double-buffered. If the
					 * canvas is indeed double-buffered then everything drawn
					 * to it is drawn to the buffer first and then the buffer
					 * is copied to the screen.
					 * @param flag		true if double-buffered
					 *------------------------------------------------------*/
	public		void		setDoubleBuffered(boolean flag)
		{
		if (!prohibitDoubleBuffering)
			{
			doubleBuffering = flag;
			if (!doubleBuffering)
				{
				renderer.setSingleBufferedScreen();
				}
			else
				{
				offScreenBuffer = createImage(canvasSize.width, canvasSize.height);
				if (offScreenBuffer != null)
					{
					renderer.setDoubleBufferedScreen(offScreenBuffer);
					}
				}
			}
		}
					/**------------------------------------------------------
	 				 * Get whether this canvas is double-buffered.
					 * @return		true if double-buffered
					 *------------------------------------------------------*/
	public		boolean		isDoubleBuffered()
		{
		return(doubleBuffering);
		}

	public		void		setEventDispatchingEnabled(boolean flag)
		{
		eventDispatchingEnabled = flag;
		}
	public		boolean		getEventDispatchingEnabled()
		{
		return(eventDispatchingEnabled);
		}
	public		void		setDrawingEnabled(boolean flag)
		{
		drawingEnabled = flag;
		}
	public		boolean		getDrawingEnabled()
		{
		return(drawingEnabled);
		}

					/**------------------------------------------------------
	 				 * Sets the (root) window associated with this canvas.
					 * This window will process events and draw the contents
					 * of this canvas.
					 * @param window	the root window
					 * @see 		MiWindow
					 *------------------------------------------------------*/
	public		void		setWindow(MiWindow window)
		{
		this.window = window;
		}
	public		MiWindow	getWindow()
		{
		return(window);
		}
					/**------------------------------------------------------
	 				 * Returns the animation manager for this canvas.
					 * @return		the animation manager
					 * @see 		MiAnimationManager
					 *------------------------------------------------------*/
	public		MiAnimationManager	getAnimationManager()
		{
		return(animationMan);
		}

					/**------------------------------------------------------
	 				 * Makes and returns a new event-processing scene-graph 
					 * modifying thread. This is used for synchronous method
					 * calls made by the origonal event-processing scene-graph
					 * when it wants to pause the original thread but still
					 * keep graphics and animations running. For example it
					 * is used for modal dialogs.
					 * @return		the thread
					 * @see			MiPartModifierThread
					 *------------------------------------------------------*/
	public		MiPartModifierThread	makeNewRunningThread()
		{
		MiPartModifierThread tmp = new MiPartModifierThread(this);
		tmp.start();
		return(tmp);
		}

					/**------------------------------------------------------
	 				 * Sets the appearance of the mouse cursor within the
					 * bounds of this canvas.
					 * @param appearance	the cursor appearance
					 *------------------------------------------------------*/
	public		void		setMouseAppearance(int appearance)
		{
//MiDebug.println(this + "setMouseAppearance: " + appearance);
		if (currentMouseAppearance != appearance)
			{
//MiDebug.println(this + "setting setMouseAppearance: " + appearance);
			currentMouseAppearance = appearance;
			if (appearance == MiiTypes.Mi_STANDARD_CURSOR)
				appearance = MiiTypes.Mi_DEFAULT_CURSOR;
//MiDebug.println(this + "callign driver to setMouseAppearance: " + appearance);
			driver.setMouseAppearance(appearance);
			}
		}
					/**------------------------------------------------------
	 				 * Returns the appearance of the mouse cursor in this 
					 * canvas.
					 * @return		the animation manager
					 * @see 		MiAnimationManager
					 *------------------------------------------------------*/
	public		int		getMouseAppearance()
		{
		return(driver.getMouseAppearance());
		}
					/**------------------------------------------------------
	 				 * Sets the bounds of the canvas, in pixels.
					 * @param deviceBounds	the bounds of this canvas
					 *------------------------------------------------------*/
	public 		void 		setCanvasBounds(MiBounds deviceBounds)
		{
//MiDebug.println("SET BOUNDS ... : " + deviceBounds);
//MiDebug.printStackTrace();

		Insets insets = driver.getContainerInsets();
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_WINDOW_AND_CANVAS_RESIZING))
			{
			MiDebug.println(this + ": Setting bounds to: " 
				+ insets.left 
				+ ", " + insets.top 
				+ ", " + (int )(deviceBounds.xmax - deviceBounds.xmin) 
				+ ", " + (int )(deviceBounds.ymax - deviceBounds.ymin));
			}
		whatWindowThinksCanvasSizeIs = new Rectangle(
			(int )deviceBounds.xmin, 
			(int )deviceBounds.ymin,
			(int )(deviceBounds.xmax - deviceBounds.xmin),
			(int )(deviceBounds.ymax - deviceBounds.ymin));

		driver.setCanvasBounds(deviceBounds);
		}
					/**------------------------------------------------------
	 				 * Get the bounds of the canvas in pixels.
					 * @return		the bounds of this canvas
					 *------------------------------------------------------*/
	public		MiBounds	getCanvasBounds()
		{
		Rectangle r = driver.getCanvasBounds();
		return(new MiBounds(
				(MiCoord )r.x, 
				(MiCoord )r.y, 
				(MiCoord )(r.x + r.width), 
				(MiCoord )(r.y + r.height)));
		}
					/**------------------------------------------------------
	 				 * Get the bounds of the entire screen in pixels.
					 * @return		the bounds of the entire screen
					 *------------------------------------------------------*/
	public static	MiBounds	getScreenBounds()
		{
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		return(new MiBounds(0, 0, (MiCoord )(size.width), (MiCoord )(size.height)));
		}

					/**------------------------------------------------------
	 				 * Returns the renderer for this canvas.
					 * @return		the renderer
					 * @see 		MiRenderer
					 *------------------------------------------------------*/
	public		MiRenderer	getRenderer()
		{
		return(renderer);
		}
					/**------------------------------------------------------
	 				 * Wakes up any threads waiting on this canvas.
					 *------------------------------------------------------*/
	public	synchronized void	notifySelf()
		{
		notifyAll();
		}
					/**------------------------------------------------------
	 				 * Waits, if necessary, and obtains a lock on this canvas.
					 * This should be done before any, and most especially any
					 * long term, synchonizations on this canvas. This permits
					 * a caller to grab hold of this canvas and it's scene-
					 * graph using a thread that is not normally associated
					 * with this canvas. A single thread may have multiple
					 * locks on a single canvas.
					 * @see			#freeAccessLock
					 *------------------------------------------------------*/
	public synchronized void	getAccessLock()
		{
		lock.getAccess();
//MiDebug.println("GET ACCESS: " + window + "," + Thread.currentThread());
		}
	public  	boolean		getAccessLockIfPossible()
		{
		int numTrys = 5;
		while ((lock.getAccessThread() != null) 
			&& (lock.getAccessThread() != Thread.currentThread()))
			{
			if (--numTrys <= 0)
				{
//System.out.println("lock.getAccessThread() has lock: " + lock.getAccessThread());
				return(false);
				}

			try	{
				// Wake up once in awhile
				Thread.currentThread().sleep(200);
				}
			catch (InterruptedException e)
				{
				}
			}

		// A small window of terror exists here if some one has 'just recently' 
		// grabbed the synchonized lock since we tested the access lock above
		synchronized (this)
			{
			return(lock.getAccessIfPossible());
			}
		}
					/**------------------------------------------------------
	 				 * Free one lock that was obtained using getAccessLock.
					 * @see			#getAccessLock
					 *------------------------------------------------------*/
	public synchronized void	freeAccessLock()
		{
		lock.freeAccess(Thread.currentThread());
//MiDebug.println("FREE ACCESS: " + window + "," + Thread.currentThread());
		}
					/**------------------------------------------------------
	 				 * Get number of locks. This is used in conjunction with
					 * (after) the freeAccessLocks method.
					 * @see			#freeAccessLocks
					 *------------------------------------------------------*/
	public synchronized void	getAccessLocks(int numLocksToGet)
		{
		lock.getAccessLocks(numLocksToGet);
		}
					/**------------------------------------------------------
	 				 * Free all of the locks this thread has on this canvas.
					 * This is used in conjunction with (before) the 
					 * getAccessLocks method. This is used when a thread
					 * wants to wait on some event and temporarily free it's
					 * locks on this canvas.
					 * @see			#getAccessLocks
					 *------------------------------------------------------*/
	public synchronized int		freeAccessLocks(Thread thread)
		{
		return(lock.freeAccessLocks(thread));
		}

	public synchronized void	freeze(boolean flag)
		{
		lock.freeze(flag);
		if ((!flag) && (window != null))
			{
			int numTries = 0;
			while (getGraphics() == null)
				{
				try	{
					Thread.sleep(20);
		    			}
				catch (InterruptedException e) 
					{
					}
				++numTries;
				if (numTries > 100)
					{
					throw new RuntimeException(
						"\nMICA: Unable to get a graphics handle for the Java component.\n"
						+ "Make sure that you make it visible AFTER to have added it\n"
						+ "to a container which has been added to a Java Frame.");
					}
				}
			canvasSize = driver.getCanvasBounds();
//System.out.println("UNFREEEEEEEEZE -------------------------------------");
if ((((int )window.getDeviceBounds().getWidth()) != whatWindowThinksCanvasSizeIs.width)
	|| (((int )window.getDeviceBounds().getHeight()) != whatWindowThinksCanvasSizeIs.height))
	{
	MiDebug.printStackTrace("whatWindowThinksCanvasSizeIs IS WRONG!");
	}

			if (!canvasSize.equals(whatWindowThinksCanvasSizeIs))
				{
				getAccessLock();
				window.deviceWasResized(new MiBounds(canvasSize.x, canvasSize.y, 
					canvasSize.width + canvasSize.x, canvasSize.height + canvasSize.y));

				whatWindowThinksCanvasSizeIs = new Rectangle(
					canvasSize.x,
					canvasSize.y,
					canvasSize.width,
					canvasSize.height);

				freeAccessLock();
				}
			}
		}
	public		boolean		isFrozen()
		{
		return(lock.isFrozen());
		}
					/**------------------------------------------------------
	 				 * Returns the preferred size of this canvas. This 
					 * routine is in support of the Component API.
					 * @return		the preferred size
					 *------------------------------------------------------*/
	public		Dimension	getPreferredSize()
		{
		MiSize size = new MiSize();
		if (window != null)
			window.getPreferredSize(size);
		return(new Dimension((int )size.getWidth(), (int )size.getHeight()));
		}
					/**------------------------------------------------------
	 				 * Returns the minimum size of this canvas. This 
					 * routine is in support of the Component API.
					 * @return		the minimum size
					 *------------------------------------------------------*/
	public		Dimension	getMinimumSize()
		{
		MiSize size = new MiSize();
		if (window != null)
			window.getMinimumSize(size);

		return(new Dimension((int )size.getWidth(), (int )size.getHeight()));
		}
					/**------------------------------------------------------
	 				 * Starts the event-processing scene-graph modifying 
					 * thread as part of the awt.Runnable interface.
					 *------------------------------------------------------*/
	public 		void 		start()
		{
		animator = new MiPartModifierThread(this);
		animator.start();
		}
					/**------------------------------------------------------
	 				 * Stops the event-processing scene-graph modifying 
					 * thread as part of the awt.Runnable interface.
					 *------------------------------------------------------*/
	public 		void 		stop()
		{
		animator.stop();
		}
	public 		void 		dispose()
		{
		disposed = true;
		}
					/**------------------------------------------------------
	 				 * Runs the event-processing scene-graph modifying 
					 * thread as part of the awt.Runnable interface.
					 *------------------------------------------------------*/
	public 		void 		run()
		{
		MiEvent event;
		while (!disposed)
		    {
		    if ((Thread.currentThread() instanceof MiPartModifierThread)
		    	&& (((MiPartModifierThread )Thread.currentThread()).isStopped()))
			{
			return;
			}
		    if ((!isFrozen()) && (window.isVisible()))
			{
			//getAccessLock();
			    try
			    {
			    synchronized (this)
			        {
			        getAccessLock();
			        }
			
			    animationMan.animate();

			    --cyclesPerGraphicsUpdate;
			    if (cyclesPerGraphicsUpdate <= 0)
				{
			    	cyclesPerGraphicsUpdate = CYCLES_PER_GRAPHICS_UPDATE;

				--cyclesPerTimerTick;
				if (cyclesPerTimerTick <= 0)
					{
					event = MiEvent.newEvent();
					event.setType(MiEvent.Mi_TIMER_TICK_EVENT);
					events.append(event);
					cyclesPerTimerTick = cyclesPerSecond;
					}
				++cyclesSinceLastEvent;
				if ((cyclesSinceLastEvent > cyclesPerSecond) && (events.size() == 0))
					{
					// if (!idle) //8-6-2003 need multiple idles for toolhints...
						{
						event = MiEvent.newEvent();
						event.setType(MiEvent.Mi_IDLE_EVENT);
						events.append(event);
						cyclesSinceLastEvent = 0;
						idle = true;
						}
					}

				if ((events.size() > 0) && (eventDispatchingEnabled))
					{
					int type = ((MiEvent )events.peek()).type;
					if ((type != MiEvent.Mi_TIMER_TICK_EVENT)
						&& (type != MiEvent.Mi_IDLE_EVENT)
						&& (type != MiEvent.Mi_NO_LONGER_IDLE_EVENT))
						{
						cyclesSinceLastEvent = 0;
						if (idle)
							{
							event = MiEvent.newEvent();
							event.setType(MiEvent.Mi_NO_LONGER_IDLE_EVENT);
							dispatchEvent(event);
							// FIX: cannot pushBack Mi_NO_LONGER_IDLE_EVENT
							MiEvent.freeEvent(event);
							idle = false;
							}
						}
					int maxNumEventsToDispatch = events.size();
					while ((maxNumEventsToDispatch-- > 0) && (events.size() > 0))
						{
						event = (MiEvent )events.pop();
//MiDebug.println("dispatch event = " + event);
						dispatchEvent(event);
						if (!events.contains(event))
							MiEvent.freeEvent(event);
						}
					}
				if (window != null)
					{
					if (!createdGraphicsContents)
						{
						//MiToolkit.initialize();
						createdGraphicsContents = true;
						window.createGraphicsContents();
						}

					if ((window.needsToBeRedrawn()) && (drawingEnabled))
						{
						driver.requestRepaint();
			    			synchronized (this)
						    {
						    notifyAll();
						    }
						}
					}
				}
			//freeAccessLock();
			     // end of synchronization
			    } // end of try
			catch (Throwable e)
			    {
			    e.printStackTrace();
			    if (e instanceof ThreadDeath)
				{
				// lock.freeAccessLocks(Thread.currentThread());
				throw new ThreadDeath();
				}
			    try	{
			    	int numLocks = lock.freeAccessLocks(Thread.currentThread());
			    	freeze(true);
			    	postExceptionOccurredDialog(e, false);
				setMouseAppearance(Cursor.DEFAULT_CURSOR);
			    	freeze(false);
			    	getAccessLock();
				}
			    catch (Exception bad)
				{
				System.out.println("EXCEPTION OCCURED WHILE HANDLING EXCEPTION");
				bad.printStackTrace();
				System.out.println("ORIGINAL EXCEPTION");
				e.printStackTrace();
				System.exit(1);
				}
			    }
		    	if ((Thread.currentThread() instanceof MiPartModifierThread)
		    		&& (((MiPartModifierThread )Thread.currentThread()).isStopped()))
				{
				if (lock.hasAccess())
					{
					freeAccessLock();
					}
				}
			else
				{
				freeAccessLock();
				}
			}
		    try
			{
			if ((window != null) && (window.isVisible()) && (window.needsToBeRedrawn()))
				{
				Thread.sleep(1000/cyclesPerSecond);
				}
			else if ((window != null) && (window.isVisible()))
				{
				Thread.sleep(100);
				cyclesSinceLastEvent += 100/(1000/cyclesPerSecond);
				}
			else 
				{
				Thread.sleep(500);
				}
			} 
		    catch (InterruptedException e) 
			{
			break;
			}
		    }
		}
					/**------------------------------------------------------
	 				 * Process the awt.event as per the awt.Component API.
					 * @param evt		the awt.Event event
					 *------------------------------------------------------*/
	public 		boolean 	handleEvent(Event evt) 
		{
//System.out.println("GOT EVENT: " + evt);
//System.out.println(commandLineMonitor.toString());

		// ---------------------------------------------------------------
		// When close a window/dialog on top of this one, the cursor needs to
		// be restored to what this window wants, so set the currentMouseAppearance
		// to unknown so that this can happen. This is probably an AWT bug... so
		// maybe this can be removed in the future. To reproduce: 'save as' ->
		// type filename, hit <enter> key.
		// ---------------------------------------------------------------
		if (evt.id == Event.MOUSE_ENTER)
			currentMouseAppearance = -1;

		if (!eventHandlingEnabled)
			return(true);

		MiEvent event = MiEvent.newEvent();
		canvasSize = driver.getCanvasBounds();

		evt.y = canvasSize.y + canvasSize.height - evt.y;
		evt.x = canvasSize.x + evt.x;
		if (!event.interpretEvent(evt, this))
			{
			return(true);
			}

		events.append(event);

		//driver.requestRepaint();

		// DO NOT Propogate the event through the low-level UI as well, if it even cares.
		// OTHERWISE get 10 key events for every keyboard event.
		return(true);
		}

	public		void		pushBackEvent(MiEvent event)
		{
		MiEvent e = new MiEvent();
		e.copy(event);
		events.pushBack(e);
		}
					/**------------------------------------------------------
	 				 * Causes the canvas to be redrawn as per the
					 * awt.Component API.
					 * @param g		the awt.graphics renderer
					 *------------------------------------------------------*/
	public		 void 		update(Graphics g) 
		{
		draw(g);
		}

	public		Graphics	getGraphics()
		{
		return(driver.getGraphics());
		}
	public		Image		createImage(int width, int height)
		{
		return(driver.createImage(width, height));
		}
		
					/**------------------------------------------------------
	 				 * Causes the canvas to be redrawn as per the
					 * awt.Component API.
					 * @param g		the awt.graphics renderer
					 *------------------------------------------------------*/
	public 		boolean 	paint(Graphics g)
		{
		if (!getAccessLockIfPossible())
			{
			// Unable to get a lock on this window, give up for now...
			return(false);
			}
		Rectangle clip = g.getClipRect();
		canvasSize = driver.getCanvasBounds();
		if (window != null)
			{
			drawingEnabled = true;
//MiDebug.println("PAINT => EXPOSE " + clip + "\n");
//MiDebug.println("PAINT => EXPOSE canvasSize = " + canvasSize);
			window.exposeArea(new MiBounds(
				clip.x + canvasSize.x, 
				canvasSize.height + canvasSize.y - clip.y - clip.height,
				clip.x + canvasSize.x + clip.width, 
				canvasSize.height + canvasSize.y - clip.y));
			}
		freeAccessLock();
		return(true);
		}

	public 		boolean 	hasBoundsChanged(int x, int y, int width, int height)
		{
		if ((canvasSize.x == x) && (canvasSize.y == y)
			&& (canvasSize.width == width) && (canvasSize.height == height))
			{
			return(false);
			}
		return(true);
		}
	public 		void 		boundsHasChanged(Rectangle newBounds, boolean awtThread)
		{
//MiDebug.println("boundsHasChanged ... by AWT: " + awtThread + " " + newBounds);

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_WINDOW_AND_CANVAS_RESIZING))
			{
			MiDebug.println(this + (awtThread ? ": AWT" : ": Mica") 
				+ " requests new bounds: (" 
				+ newBounds.x + ", " + newBounds.y + ", "
				+ newBounds.width + ", " + newBounds.height + ")");
			}

		canvasSize.reshape(newBounds.x, newBounds.y, newBounds.width, newBounds.height);

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_WINDOW_AND_CANVAS_RESIZING))
			MiDebug.println(this + ": reshaped to bounds:" + canvasSize);

		if (!canvasSize.equals(oldCanvasSize))
			{
//MiDebug.println("boundsHasChanged ... Different canvas size");
			oldCanvasSize.reshape(canvasSize.x, canvasSize.y, canvasSize.width, canvasSize.height);
			renderer.setIndentX(canvasSize.x);
			renderer.setYmax(canvasSize.height + canvasSize.y);

			renderer.setCanvasSize(canvasSize);
			offScreenBuffer = null;
			}
		if ((doubleBuffering) && (offScreenBuffer == null))
			{
			offScreenBuffer = createImage(canvasSize.width, canvasSize.height);
			if (offScreenBuffer != null)
				{
				renderer.setDoubleBufferedScreen(offScreenBuffer);
				}
			}

		//if ((window != null) && ((!awtThread) || (!isFrozen())))
		if ((window != null) 
			&& ((awtThread) || (!canvasSize.equals(oldCanvasSize))) 
			&& (!isFrozen()))
			{
				{
				if ((lock.getAccessThread() == null) 
					|| (lock.getAccessThread() == Thread.currentThread()))
					{
//System.out.println("lock.getAccessThread()= "  + lock.getAccessThread());
					getAccessLock();
					// Always getAccessLock before synchonrization so 2 
					// threads wont deadlock, each wanting the others lock/handle
					synchronized (this)
						{

					whatWindowThinksCanvasSizeIs = new Rectangle(
						canvasSize.x,
						canvasSize.y,
						canvasSize.width,
						canvasSize.height);

					window.deviceWasResized(
						new MiBounds(canvasSize.x, canvasSize.y, 
						canvasSize.width + canvasSize.x, 
						canvasSize.height + canvasSize.y));
						}

			// Wait for expose event
			drawingEnabled = false;


					freeAccessLock();
					}
				}
			}
		}

	public		void		forceRedraw()
		{
		if ((window.needsToBeRedrawn()) && (drawingEnabled))
			{
			redrawn = false;
			driver.requestRepaint();
			synchronized (this)
		    		{
		    		notifyAll();
		    		}
			int maxNumTimesToRedraw = 3;
			int maxNumTimesToTryToRedraw = 50;
			int numLocks = freeAccessLocks(Thread.currentThread());
			while (window.needsToBeRedrawn())
				{
				if (redrawn)
					{
					redrawn = false;
					if (--maxNumTimesToRedraw <= 0)
						{
						getAccessLocks(numLocks);
						return;
						}
					}
				driver.requestRepaint();
				synchronized (this)
		    			{
		    			notifyAll();
		    			}
				if (--maxNumTimesToTryToRedraw <= 0)
					{
					getAccessLocks(numLocks);
					return;
					}
				MiDebug.sleep(20);
				}
			getAccessLocks(numLocks);
			}
		}
					/**------------------------------------------------------
	 				 * Draws the contents of this canvas (the scene-graph).
					 *------------------------------------------------------*/
	private		void		draw(Graphics g)
		{
		redrawn = true;

		if ((isFrozen()) || (!drawingEnabled))
			return;

//MiDebug.println(this + "DRAW");
		getAccessLock();

		if (!initialized)
			{
			if ((window == null) || (g == null))
				{
				freeAccessLock();
				return;
				}
			initialized = true;
			if (!createdGraphicsContents)
				{
				//MiToolkit.initialize();
				createdGraphicsContents = true;
				window.createGraphicsContents();
				}
			}
		if (!window.needsToBeRedrawn())
			{
			freeAccessLock();
			return;
			}

		long time = 0;
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_CANVAS_DRAW_TIMES))
			{
			time = System.currentTimeMillis();
			MiDebug.println(this + " Start draw at time: " + time);
			}

//MiDebug.println(this + "doubleBuffering = " + doubleBuffering);
		if ((doubleBuffering) && (offScreenBuffer == null))
			{
			offScreenBuffer = createImage(canvasSize.width, canvasSize.height);
			if (offScreenBuffer != null)
				{
				renderer.setDoubleBufferedScreen(offScreenBuffer);
				}
			}

		try	{
			window.validateWindow();

			// Make sure we catch any changes to the size....
			canvasSize = driver.getCanvasBounds();
			if ((((int )window.getDeviceBounds().getWidth()) != canvasSize.width)
				|| (((int )window.getDeviceBounds().getHeight()) != canvasSize.height))
				{
				// Not clear that this code is ever executed...
				oldCanvasSize.reshape(
						canvasSize.x, canvasSize.y, 
						canvasSize.width, canvasSize.height);
				window.deviceWasResized(
						new MiBounds(canvasSize.x, canvasSize.y, 
							canvasSize.width + canvasSize.x, 
							canvasSize.height + canvasSize.y));
				}

			// Required for double-buffered Swing containers
//MiDebug.println(this + "g.getClipBounds = " + g.getClipBounds());
//MiDebug.println("Invalida areas:" + window.getDrawManager().getInvalidAreas(renderer));
			renderer.setGraphics(g);
			window.draw(renderer);
			}
		catch (Throwable e)
			{
			if (e instanceof ThreadDeath)
				{
				throw new ThreadDeath();
				}
			int numLocks = lock.freeAccessLocks(Thread.currentThread());
			freeze(true);
			postExceptionOccurredDialog(e, false);
			setMouseAppearance(Cursor.DEFAULT_CURSOR);
			freeze(false);
			getAccessLock();
			}
			
		freeAccessLock();

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_CANVAS_DRAW_TIMES))
			{
			MiDebug.println(this 
				+ " Draw time duration = " + (time - System.currentTimeMillis()));
			}

		}

	protected	void		postExceptionOccurredDialog(Throwable e, boolean tooSevereToContinue)
		{
		Runtime runtime = Runtime.getRuntime();
		runtime.runFinalization();
		runtime.gc();
		MiExceptionOccurredDialog d = new MiExceptionOccurredDialog(
				MiUtility.getFrame((Component )driver), "Error Detected", e, tooSevereToContinue);
		d.pack();
		d.show();
		}
	public		String		toString()
		{
		return(super.toString() + "[For window: " + window + "]");
		}

	//***************************************************************************************
	// Private methods
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Takes events off the Fifo and passes them to the
					 * MiWindow for processing. Events are first combined
					 * if possible (i.e. consecutive mouse move events are
					 * combined into one mouse movement event).
					 * @param event		the event to process
					 *------------------------------------------------------*/
	private		void		dispatchEvent(MiEvent event)
		{
		while ((events.size() > 0) && (event.canCompressEvents((MiEvent )events.peek())))
			{
			MiEvent discardedEvent = (MiEvent )events.pop();
			event = event.compressEvent(discardedEvent);
			MiEvent.freeEvent(discardedEvent);
			}
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_INPUT))
			{
			MiDebug.println("Canvas dispatching event: " + event + " to window->" + window);
			}
		window.dispatchEvent(event);
		}
	public 		void		processCommand(String arg)
		{
System.out.println("UNIMPLEMENTED: dump thread lock info");
		}
	}
/**----------------------------------------------------------------------------------------------
 *
 *----------------------------------------------------------------------------------------------*/
class MiCanvasAccessLock
	{
	private		MiCanvas	canvas;
	private		Thread		lockingThread;
	private		int		numLocks;
	private		boolean		frozen		= true;



					/**------------------------------------------------------
	 				 * Contructs the lock for the given canvas.
					 * @param canvas	the Canvas to lock
					 *------------------------------------------------------*/
	public				MiCanvasAccessLock(MiCanvas canvas)
		{
		this.canvas = canvas;
		}

	public		void		freeze(boolean flag)
		{
		frozen = flag;
		}
	public		boolean		isFrozen()
		{
		return(frozen);
		}

	public		boolean		getAccessIfPossible()
		{
		return(getAccess(false));
		}
					/**------------------------------------------------------
	 				 * Obtains a lock for the associated canvas. If necessary
					 * wait until any thread that currently has the lock frees
					 * the lock. If the calling thread already has access then
					 * a number-of-locks counter is increased by one.
					 *------------------------------------------------------*/
	public 		void		getAccess()
		{
		getAccess(true);
		}

	protected	boolean		getAccess(boolean tryForever)
		{
		if (Thread.currentThread() == lockingThread)
			{
			++numLocks;
			return(true);
			}

		while (lockingThread != null)
			{
			try	{
				// Wake up once in awhile to see if locking thread has died...
				canvas.wait(2000);
//MiDebug.println("current Thread = " + Thread.currentThread());
//MiDebug.println("Waiting for locking Thread = " + lockingThread);
				}
			catch (InterruptedException e)
				{
				}
			// If the thread exceptioned and died... allow access to the window
			// by other threads.
			if ((lockingThread != null) && (!lockingThread.isAlive()))
				{
				lockingThread = null;
				}
			else if (!tryForever)
				{
//MiDebug.println(Thread.currentThread() + "getAccessIfPossible FAILED locking Thread = " + lockingThread);
				return(false);
				}
			}
		lockingThread = Thread.currentThread();
		if (lockingThread instanceof MiPartModifierThread)
			{
			((MiPartModifierThread )lockingThread).lockedCanvas(canvas);
			}
		++numLocks;
		return(true);
		}
					/**------------------------------------------------------
	 				 * Frees a lock for the associated canvas. If this thread
					 * has more than one lock a number of locks counter is
					 * decreased by one and this method returns with the 
					 * calling thread still owning at least one lock. If the
					 * calling thread does not own this lock then an
					 * IllegalArgumentException is thrown.
					 * @exception IllegalArgumentException	calling thread 
					 * 					does not own this
					 *					lock.
					 *------------------------------------------------------*/
	public		void		freeAccess(Thread thread)
		{
		if (thread != lockingThread)
			{
			throw new IllegalArgumentException(canvas 
				+ ": Attempting to free lock with thread: " + thread 
				+ " that is not owner (" + lockingThread + ").");
			}
		--numLocks;
		if (numLocks == 0)
			{
			if (lockingThread instanceof MiPartModifierThread)
				{
				((MiPartModifierThread )lockingThread).unlockedCanvas(canvas);
				}
			lockingThread = null;
			try	{
				canvas.notifyAll();
				}
			catch (IllegalMonitorStateException e)
				{
				}
			}
		}
					/**------------------------------------------------------
	 				 * Gets a lock on the associated canvas and sets the
					 * number of locks counter to the given number.
					 * @param numLocksToGet		the number of locks to get.
					 * @see 			#getAccess
					 *------------------------------------------------------*/
	public 		void		getAccessLocks(int numLocksToGet)
		{
		if (numLocksToGet > 0)
			{
			getAccess();
			numLocks = numLocksToGet;
			}
		}

					/**------------------------------------------------------
	 				 * Frees all locks the calling thread has on the given 
					 * canvas. The number of locks this thread had is returned.
					 * This is used when a thread wants to temporarily free 
					 * all it's locks so another thread can get access and 
					 * then later grab them all back again (i.e. modal 
					 * dialogs).
					 * @return		the number of locks calling 
					 * 			thread had before calling this
					 *			method.
					 *------------------------------------------------------*/
	public 		int		freeAccessLocks(Thread thread)
		{
		if (thread != lockingThread)
			{
			return(0);
			}
		int numLocksHad = numLocks;
		while (numLocks > 0)
			{
			freeAccess(thread);
			}
		return(numLocksHad);
		}

	public		boolean		hasAccess()
		{
		return(Thread.currentThread() == lockingThread);
		}
	public		Thread		getAccessThread()
		{
		return(lockingThread);
		}
	public		void		clearLocks()
		{
		lockingThread = null;
		numLocks = 0;
		try	{
			canvas.notifyAll();
			}
		catch (IllegalMonitorStateException e)
			{
			}
		}
	}

/*****
class MiCommandHandlerLineInputMonitor implements Runnable
	{
	private		InputStream	inputStream;
	private		MiiCommandHandler	callback;
	private		String		argument;
	private		int		triggerChar;
	private		Thread		monitorThread;



	public				MiCommandHandlerLineInputMonitor(MiiCommandHandler command, String argument, int trigger)
		{
		inputStream = System.in;
		triggerChar = trigger;
		callback = command;
		this.argument = argument;

		monitorThread = new Thread(this);
		monitorThread.start();
		}

	public		void		run()
		{
		try	{
System.out.println("inputStream.available() = " + inputStream.available());

			if (inputStream.available() > 0)
				{
				byte[] chars = new byte[inputStream.available()];
				int numRead = inputStream.read(chars);
				if (numRead > 0)
					{
					for (int i = 0; i < numRead; ++i)
						{
						if (chars[i] == triggerChar)
							callback.processCommand(argument);
						}
					}
				}
			}
		catch (java.io.IOException e)
			{
			e.printStackTrace();
			}

		try	{
			Thread.sleep(1000);
    			}
		catch (InterruptedException e) 
			{
			e.printStackTrace();
			}
		}
	public		String		toString()
		{
		return(getClass().getName() + ".thread: " + (monitorThread == null ? "null" : monitorThread.getName()));
		}
	}

*****/

