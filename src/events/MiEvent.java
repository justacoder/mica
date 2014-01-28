
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

import java.awt.Event; 
import com.swfm.mica.util.FastVector; 

/**----------------------------------------------------------------------------------------------
 * This class implements the complete description of all events coming
 * from the resisdent window system that are used by Mica. This includes
 * user events (like keyboard presses and mouse button clicks and mouse
 * movements), window manager events (like window destroy, exposed and 
 * moved/resized). In addition, some Mica-specific events also exist
 * (the timer, idle and no-longer-idle events).
 * <p>
 * Each event is identified by 3 values: the type, the key and the 
 * modifiers. The type value identifies the 'type' of the event, the 
 * key value specifies which key in a Mi_KEY_PRESS_EVENT, Mi_KEY_EVENT, or 
 * Mi_KEY_RELEASE_EVENT and the modifiers value specifies which shift keys
 * were held down when the event was generated.
 * <p>
 * Each event contains information about where the event occured, in
 * device space and world space. This coordinate information is maintained
 * in three 'formats': the point where the event occured, the area 
 * (footprint) where the event occured, and the distance (vector) from
 * where the last event occurred. This information is appropriately 
 * transformed so that when delivered to a MiPart, the device and world
 * values will be correct with respect to the MiPart.
 * <p>
 * <pre>
 * Event types sequences for (any) mouse are:
 *
 * Mi_LEFT_MOUSE_DOWN_EVENT -> Mi_LEFT_MOUSE_UP_EVENT
 * Mi_LEFT_MOUSE_DOWN_EVENT -> Mi_LEFT_MOUSE_CLICK_EVENT
 * Mi_LEFT_MOUSE_DOWN_EVENT -> Mi_LEFT_MOUSE_CLICK_EVENT -> Mi_LEFT_MOUSE_DOWN_EVENT -> Mi_LEFT_MOUSE_DBLCLICK_EVENT
 * Mi_LEFT_MOUSE_DOWN_EVENT -> Mi_LEFT_MOUSE_START_DRAG_EVENT -> Mi_LEFT_MOUSE_DRAG_EVENT -> Mi_LEFT_MOUSE_UP_EVENT
 * </pre>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEvent implements MiiEventTypes
	{
	public static	int		uniqueIds 	= 0;
	public 		int		id 	= 0;
					// ---------------------------------------------------------------
					// Hysteresis time
					// ---------------------------------------------------------------
	public static	long		mouseDownToClickMaxTimeDelta 	= 400;
					// ---------------------------------------------------------------
					// Hysteresis distance squared
					// ---------------------------------------------------------------
	public static	int		mouseDownToClickMaxPixelDelta	= 300;
	public static	int		maxNumberOfCachedMiEvents	= 50;

	private	static	long		lastMouseDownTime;
	private	static	MiPoint		lastMouseDownLocation	= new MiPoint();
	private	static	int		numClicks;
	private static	boolean		dragging;
	private static	FastVector	freePool		= new FastVector();

	private static 	MiEvent		lastEvent 		= new MiEvent();

	private static 	MiDistance	preferredMouseFootPrintWidth 	= 3;

	protected	int		type			= NOOP_EVENT;
	protected	int		key;
	protected	int	 	modifiers;
	protected	int	 	mouseButtonState;
	protected	MiEditor	editor;
	protected	MiCanvas	canvas;
	protected	MiPoint		devicePt;
	protected	MiPoint		worldPt;
	protected	MiVector	deviceVector;
	protected	MiVector	worldVector;
	protected	MiBounds	deviceMouseFootPrint;
	protected	MiBounds	worldMouseFootPrint;
	protected	long		timeStamp;
	protected	MiPart		target;
	protected	boolean		locationSpecific	= true;
	protected	int		handlerTargetType	= Mi_ORDINARY_EVENT_HANDLER_TYPE;
					// ---------------------------------------------------------------
					// List of objects under cursor, top object is 1st
					// ---------------------------------------------------------------
	private		MiParts		targetList		= null;
					// ---------------------------------------------------------------
					// List of parents of object under cursor, 
					// 1st element is the object
					// ---------------------------------------------------------------
	private		MiParts		targetPath		= null;
	private		MiPart		movedKeyboardFocusTo;





					/**------------------------------------------------------
	 				 * Constructs a new MiEvent. 
					 *------------------------------------------------------*/
	public				MiEvent()
		{
		devicePt		= new MiPoint();
		worldPt			= new MiPoint();
		deviceVector		= new MiVector();
		worldVector		= new MiVector();
		deviceMouseFootPrint	= new MiBounds();
		worldMouseFootPrint	= new MiBounds();

		id = uniqueIds++;
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEvent with the given type. The key
					 * and modifiers are set to zero.
					 * @param type		the event type
					 *------------------------------------------------------*/
	public				MiEvent(int type)
		{
		this(type, 0, 0, false);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEvent with the given type, key and
					 * modifiers.
					 * @param type		the event type
					 * @param key		the keyboard key
					 * @param modifiers	the shift keys that were held down
					 *------------------------------------------------------*/
	public				MiEvent(int type, int key, int modifiers)
		{
		this(type, key, modifiers, false);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEvent with the given type, key and
					 * modifiers. If this is used only for type-key-modifier
					 * comparisons, then by setting the usedForEventTypeOnly
					 * parameter, positional information objects will not be
					 * created which will save time and memory. MiEventHandlers
					 * specify their event->command mappings using these 
					 * optimized events.
					 * @param type		the event type
					 * @param key		the keyboard key
					 * @param modifiers	the shift keys that were held down
					 * @param usedForEventTypeOnly	
					 *			true if no locational information
					 *			will be required for this event.
					 *------------------------------------------------------*/
	public				MiEvent(int type, int key, int modifiers, boolean usedForEventTypeOnly)
		{
		if (!usedForEventTypeOnly)
			{
			devicePt		= new MiPoint();
			worldPt			= new MiPoint();
			deviceVector		= new MiVector();
			worldVector		= new MiVector();
			deviceMouseFootPrint	= new MiBounds();
			worldMouseFootPrint	= new MiBounds();

			copy(lastEvent);
			}
		this.type = type;
		this.key = key;
		this.modifiers = modifiers;

		id = uniqueIds++;
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEvent which will be a copy of the
					 * given event.
					 * @param other		the event to make a copy of
					 *------------------------------------------------------*/
	public				MiEvent(MiEvent other)
		{
		devicePt		= new MiPoint();
		worldPt			= new MiPoint();
		deviceVector		= new MiVector();
		worldVector		= new MiVector();
		deviceMouseFootPrint	= new MiBounds();
		worldMouseFootPrint	= new MiBounds();

		copy(other);

		id = uniqueIds++;
		}
					/**------------------------------------------------------
	 				 * Gets the last event generated by Mica based on an event
					 * sent by the underlying window system. This is useful
					 * when one wants to create an event that is populated
					 * with correct and recent data (i.e. 
					 * <pre>
					 * MiEvent myEvent = new MiEvent(MiEvent.getLastEvent())
					 * </pre>
					 * @return 		the last event
					 *------------------------------------------------------*/
	public	static	MiEvent		getLastEvent()
		{
		return(lastEvent);
		}
					/**------------------------------------------------------
	 				 * Copies the type of the given MiEvent and returns this MiEvent.
					 * @param src 		the event to copy
					 * @return 		this event
					 *------------------------------------------------------*/
	public		MiEvent		copyEventType(MiEvent src)
		{
		editor		= src.editor;
		type		= src.type;
		key		= src.key;
		modifiers	= src.modifiers;
		mouseButtonState= src.mouseButtonState;
		return(this);
		}
					/**------------------------------------------------------
	 				 * Copies the given MiEvent and returns this MiEvent.
					 * @param src 		the event to copy
					 * @return 		this event
					 *------------------------------------------------------*/
	public		MiEvent		copy(MiEvent src)
		{
		editor		= src.editor;
		type		= src.type;
		key		= src.key;
		modifiers	= src.modifiers;
		mouseButtonState= src.mouseButtonState;
		devicePt.x	= src.devicePt.x;
		devicePt.y	= src.devicePt.y;
		worldPt.x	= src.worldPt.x;
		worldPt.y	= src.worldPt.y;
		deviceVector.x	= src.deviceVector.x;
		deviceVector.y	= src.deviceVector.y;
		worldVector.x	= src.worldVector.x;
		worldVector.y	= src.worldVector.y;
		deviceMouseFootPrint.copy(src.deviceMouseFootPrint);
		worldMouseFootPrint.copy(src.worldMouseFootPrint);
		timeStamp	= src.timeStamp;
		target		= src.target;
		locationSpecific= src.locationSpecific;
		handlerTargetType= src.handlerTargetType;
		targetList	= src.targetList;
		targetPath	= src.targetPath;
		canvas		= src.canvas;
		movedKeyboardFocusTo= src.movedKeyboardFocusTo;
		return(this);
		}
					/**------------------------------------------------------
	 				 * Sets the width and height of the mouse cursor pick point
					 * in pixels. This is used globally for all picks in Mica.
					 * This can be set on a part by part basis using 
					 * MiPart.setMinimumPickAreaSize because this is often 
					 * only useful in graphics editors, and can be misleading
					 * when near, say, a sash, which requires a pinpoint mouse
					 * footprint.
					 *
					 * @param size 		the size of the mouse cursor pick
					 * @see			#getPreferredMouseFootPrintWidth
					 *------------------------------------------------------*/
	public static	void		setPreferredMouseFootPrintWidth(MiDistance size)
		{
		preferredMouseFootPrintWidth = size/2;
		}
					/**------------------------------------------------------
	 				 * Gets the width and height of the mouse cursor pick point
					 * in pixels.
					 * @return 		the size of the mouse cursor pick
					 * @see			#setPreferredMouseFootPrintWidth
					 *------------------------------------------------------*/
	public static	MiDistance	getPreferredMouseFootPrintWidth()
		{
		return(preferredMouseFootPrintWidth * 2);
		}
					/**------------------------------------------------------
	 				 * Gets the type of this event. See MiiEventTypes for a
					 * list of all types.
					 * @return 		the type of the event
					 *------------------------------------------------------*/
	public		int		getType()
		{
		return(type);
		}
					/**------------------------------------------------------
	 				 * Sets the type of this event. See MiiEventTypes for a
					 * list of all types.
					 * @param type 		the type of the event
					 *------------------------------------------------------*/
	public		void		setType(int type)
		{
		this.type = type;
		}
					/**------------------------------------------------------
	 				 * Gets the type of this event. See MiiEventTypes for a
					 * list of all types.
					 * @return 		the type of the event
					 *------------------------------------------------------*/
	public		int		getKey()
		{
		return(key);
		}
					/**------------------------------------------------------
	 				 * Sets the key (ASCII) of this event. See MiiEventTypes 
					 * for a list of some special keys.
					 * @param type 		the key (ASCII) of the event
					 *------------------------------------------------------*/
	public		void		setKey(int key)
		{
		this.key = key;
		}
					/**------------------------------------------------------
	 				 * Gets the modifiers of this event. See MiiEventTypes for
					 * a list of all modifiers. The possible values are:
					 *	Mi_SHIFT_KEY_HELD_DOWN
					 *	Mi_CONTROL_KEY_HELD_DOWN
					 *	Mi_ALT_KEY_HELD_DOWN
					 *	Mi_META_KEY_HELD_DOWN
					 *	Mi_LOCK_KEY_HELD_DOWN
					 *	Mi_ANY_MODIFIERS_HELD_DOWN
					 * @return 		the modifiers of the event
					 *------------------------------------------------------*/
	public		int		getModifiers()
		{
		return(modifiers);
		}
					/**------------------------------------------------------
	 				 * Sets the modifiers for this event. See MiiEventTypes
					 * for a list of all modifiers.
					 * @param modifiers 	the modifiers of the event
					 *------------------------------------------------------*/
	public		void		setModifiers(int modifiers)
		{
		this.modifiers = modifiers;
		}
					/**------------------------------------------------------
	 				 * Gets a mask indicating which mouse buttons are pressed
					 * during this event. The possible values are:
					 *	Mi_LEFT_MOUSE_BUTTON_HELD_DOWN
					 *	Mi_MIDDLE_MOUSE_BUTTON_HELD_DOWN
					 *	Mi_RIGHT_MOUSE_BUTTON_HELD_DOWN
					 *	Mi_MIDDLE_PLUS_RIGHT_MOUSE_BUTTON_HELD_DOWN
					 * @return 		which mouse buttons are down
					 *------------------------------------------------------*/
	public		int		getMouseButtonState()
		{
		return(mouseButtonState);
		}
					/**------------------------------------------------------
	 				 * Sets a mask indicating which mouse buttons are pressed
					 * during this event. 
					 * This will typically be used only by Mica.
					 * @param state		which mouse buttons are down
					 * @see			#getMouseButtonState
					 *------------------------------------------------------*/
	public		void		setMouseButtonState(int state)
		{
		mouseButtonState = state;
		}
					/**------------------------------------------------------
	 				 * Gets what MiEditor the positions of this event are 
					 * relative to. This will typically be modified as the 
					 * event traverses the part-container hierarchy of MiParts.
					 * @return 		the editor
					 *------------------------------------------------------*/
	public		MiEditor	getEditor()
		{
		return(editor);
		}
					/**------------------------------------------------------
	 				 * Sets what MiEditor the positions of this event are 
					 * relative to. 
					 * This will typically be used only by Mica.
					 * @param e 		the editor
					 *------------------------------------------------------*/
	public		void		setEditor(MiEditor e)
		{
		editor = e;
		}
					/**------------------------------------------------------
	 				 * Gets what MiCanvas the event originally occured in.
					 * @return 		the canvas
					 *------------------------------------------------------*/
	public		MiCanvas	getCanvas()
		{
		return(canvas);
		}
					/**------------------------------------------------------
	 				 * Gets the location of the event in device coordinates
					 * relative to the event's editor.
					 * @return 		the device coordinate position
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		MiPoint		getDevicePoint(MiPoint pt)
		{
		pt.copy(devicePt);
		return(pt);
		}
					/**------------------------------------------------------
	 				 * Sets the location of the event in device coordinates
					 * relative to the event's editor.
					 * This will typically be used only by Mica.
					 * @param pt 		the device coordinate position
					 * @see			#getDevicePoint
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		void		setDevicePoint(MiPoint pt)
		{
		devicePt.copy(pt);
		}
					/**------------------------------------------------------
	 				 * Gets the location of the event in world coordinates
					 * relative to the event's editor.
					 * @return 		the world coordinate position
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		MiPoint		getWorldPoint(MiPoint pt)
		{
		pt.copy(worldPt);
		return(pt);
		}
					/**------------------------------------------------------
	 				 * Sets the location of the event in world coordinates
					 * relative to the event's editor.
					 * This will typically be used only by Mica.
					 * @param pt 		the world coordinate position
					 * @see			#getWorldPoint
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		void		setWorldPoint(MiPoint pt)
		{
		worldPt.copy(pt);
		}
					/**------------------------------------------------------
	 				 * Gets the distance between this event and the last event
					 * in device coordinates relative to the event's editor.
					 * @return 		the distance in device coordinates
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		MiVector	getDeviceVector(MiVector v)
		{
		v.copy(deviceVector);
		return(v);
		}
					/**------------------------------------------------------
	 				 * Sets the distance between this event and the last event
					 * in device coordinates relative to the event's editor.
					 * This will typically be used only by Mica.
					 * @param v 		the distance in device coordinates
					 * @see			#getDeviceVector
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		void		setDeviceVector(MiVector v)
		{
		deviceVector.copy(v);
		}
					/**------------------------------------------------------
	 				 * Gets the distance between this event and the last event
					 * in world coordinates relative to the event's editor.
					 * @return 		the distance in world coordinates
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		MiVector	getWorldVector(MiVector v)
		{
		v.copy(worldVector);
		return(v);
		}
					/**------------------------------------------------------
	 				 * Sets the distance between this event and the last event
					 * in world coordinates relative to the event's editor.
					 * This will typically be used only by Mica.
					 * @param v 		the distance in world coordinates
					 * @see			#getWorldVector
					 * @see			#getEditor
					 *------------------------------------------------------*/
	public		void		setWorldVector(MiVector v)
		{
		worldVector.copy(v);
		}
					/**------------------------------------------------------
	 				 * Gets the footprint (area) of this event in device space.
					 * @param footPrint 	the (returned )foot print of the event
					 * @return 		the foot print of the event
					 *------------------------------------------------------*/
	public		MiBounds	getDeviceMouseFootPrint(MiBounds footPrint)
		{
		footPrint.copy(deviceMouseFootPrint);
		return(footPrint);
		}
					/**------------------------------------------------------
	 				 * Sets the footprint (area) of this event in device space.
					 * This will typically be used only by Mica.
					 * @param footPrint 	the foot print of the event
					 *------------------------------------------------------*/
	public		void		setDeviceMouseFootPrint(MiBounds footPrint)
		{
		deviceMouseFootPrint.copy(footPrint);
		}
					/**------------------------------------------------------
	 				 * Gets the footprint (area) of this event in world space.
					 * @param footPrint 	the (returned )foot print of the event
					 * @return 		the foot print of the event
					 *------------------------------------------------------*/
	public		MiBounds	getMouseFootPrint(MiBounds footPrint)
		{
		footPrint.copy(worldMouseFootPrint);
		return(footPrint);
		}
					/**------------------------------------------------------
	 				 * Sets the footprint (area) of this event in world space.
					 * This will typically be used only by Mica.
					 * @param footPrint 	the foot print of the event
					 *------------------------------------------------------*/
	public		void		setMouseFootPrint(MiBounds footPrint)
		{
		worldMouseFootPrint.copy(footPrint);
		}
					/**------------------------------------------------------
	 				 * Gets the time this event was generated.
					 * @return 		the time
					 *------------------------------------------------------*/
	public		long		getTimeStamp()
		{
		return(timeStamp);
		}
					/**------------------------------------------------------
	 				 * Sets the time this event was generated.
					 * This will typically be used only by Mica.
					 * @param time 		the time of creation
					 *------------------------------------------------------*/
	public		void		setTimeStamp(long time)
		{
		timeStamp = time;
		}
					/**------------------------------------------------------
	 				 * Gets the MiPart that this event is being sent to. The
					 * positional information of this event will be in the
					 * same coordinate space as the MiPart. MiiEventHandlers
					 * of the MiPart can count on this.
					 * @return 		the target of the event
					 *------------------------------------------------------*/
	public		MiPart		getTarget()		
		{
		return(target);
		}
					/**------------------------------------------------------
	 				 * Sets the target MiPart of this event.
					 * This will typically be used only by Mica.
					 * @return 		the target part
					 *------------------------------------------------------*/
	public		void		setTarget(MiPart target)
		{
		this.target = target;
		}
					/**------------------------------------------------------
	 				 * Gets whether the event is targeting position dependent
					 * event handlers. This will typically be modified as the 
					 * event targets different kinds of event handlers, looking
					 * for one that will consume the event.
					 * @return 		true if location specific
					 *------------------------------------------------------*/
	public		boolean		isPositionDependent()
		{
		return(locationSpecific);
		}
					/**------------------------------------------------------
	 				 * Sets whether the event is targeting position dependent
					 * event handlers. 
					 * @param flag 		true if location specific
					 *------------------------------------------------------*/
	public		void		setPositionDependent(boolean flag)
		{
		locationSpecific = flag;
		}
					/**------------------------------------------------------
	 				 * Gets what type of event handler this event is targeting.
					 * This will typically be modified as the event targets 
					 * different kinds of event handlers, looking for one that 
					 * will consume the event.
					 * @return 		true if location specific
					 * @see			MiEventHandler#getType
					 *------------------------------------------------------*/
	public		int		getHandlerTargetType()
		{
		return(handlerTargetType);
		}
					/**------------------------------------------------------
	 				 * Sets what type of event handler this event is targeting.
					 * @param type 		the type of event handler
					 *------------------------------------------------------*/
	public		void		setHandlerTargetType(int type)
		{
		handlerTargetType = type;
		}
					/**------------------------------------------------------
	 				 * Gets the list of all MiParts underneath the event from
					 * top to bottom.
					 * @return 		the list of MiParts under the event
					 *------------------------------------------------------*/
	public		MiParts		getTargetList()
		{
		return(targetList);
		}
					/**------------------------------------------------------
	 				 * Sets the list of all MiParts underneath the event from
					 * top to bottom.
					 * This will typically be used only by Mica.
					 * @param list 		the list of MiParts under the event
					 *------------------------------------------------------*/
	public		void		setTargetList(MiParts list)
		{
		targetList = list;
		}
					/**------------------------------------------------------
	 				 * Gets the list consisting of the topmost part, under the
					 * event, that is enabled to receive events, and all of it's
					 * containers. The order is top to bottom.
					 * @param list 		the list of the first MiPart to 
					 *			receive the event and all of it's
					 *			containers
					 *------------------------------------------------------*/
	public		MiParts		getTargetPath()
		{
		return(targetPath);
		}
					/**------------------------------------------------------
	 				 * Sets the list consisting of the topmost part, under the
					 * event, that is enabled to receive events, and all of it's
					 * containers. The order is top to bottom.
					 * This will typically be used only by Mica.
					 * @param list 		the list of the first MiPart to 
					 *			receive the event and all of it's
					 *			containers
					 *------------------------------------------------------*/
	public		void		setTargetPath(MiParts list)
		{
		targetPath = list;
		}
					/**------------------------------------------------------
	 				 * Specifies that the given MiPart has just received 
					 * keyboard (and possibly enter-key) focus because of this
					 * event. This is useful for event handlers who behave 
					 * differently if their MiPart is just now recieving 
					 * keyboard focus.
					 * @param part 		the part that now has keyboard focus
					 *			or null
					 * @see 		#getMovedKeyboardFocusTo
					 *------------------------------------------------------*/
	public		void		setMovedKeyboardFocusTo(MiPart part)
		{
		movedKeyboardFocusTo = part;
		}
					/**------------------------------------------------------
	 				 * Gets what MiPart has just received keyboard (and possibly
					 * enter-key) focus because of this event. 
					 * This will typically be used only by Mica.
					 * @return 		the part or null
					 * @see 		#setMovedKeyboardFocusTo
					 *------------------------------------------------------*/
	public		MiPart		getMovedKeyboardFocusTo()
		{
		return(movedKeyboardFocusTo);
		}
					/**------------------------------------------------------
	 				 * Gets whether this event is a mouse button event (i.e.
					 * some kind of click, dbl-click, triple-click of one of
					 * the three mouse buttons).
					 * @return 		true if this is a mouse button event
					 *------------------------------------------------------*/
	public		boolean		isMouseButtonEvent()
		{
		return((type >= Mi_LEFT_MOUSE_DOWN_EVENT) && (type <= Mi_MIDDLE_PLUS_RIGHT_MOUSE_TRIPLECLICK_EVENT));
		}
					/**------------------------------------------------------
	 				 * Gets whether this event is of the type that will always
					 * come right before the given event.
					 * @return 		true if this MiEvent must occur 
					 *			immediately before the given MiEvent
					 *------------------------------------------------------*/
	public		boolean		isPreludeToEvent(MiEvent event)
		{
		if (type == Mi_LEFT_MOUSE_DOWN_EVENT) 
			{
			if ((event.type == Mi_LEFT_MOUSE_START_DRAG_EVENT)
				|| (event.type == Mi_LEFT_MOUSE_CLICK_EVENT))
				{
				return(true);
				}
			return(false);
			}
		if (type == Mi_MIDDLE_MOUSE_DOWN_EVENT) 
			{
			if ((event.type == Mi_MIDDLE_MOUSE_START_DRAG_EVENT)
				|| (event.type == Mi_MIDDLE_MOUSE_CLICK_EVENT))
				{
				return(true);
				}
			return(false);
			}
		if (type == Mi_RIGHT_MOUSE_DOWN_EVENT) 
			{
			if ((event.type == Mi_RIGHT_MOUSE_START_DRAG_EVENT)
				|| (event.type == Mi_RIGHT_MOUSE_CLICK_EVENT))
				{
				return(true);
				}
			return(false);
			}
		if (type == Mi_MIDDLE_PLUS_RIGHT_MOUSE_DOWN_EVENT) 
			{
			if ((event.type == Mi_MIDDLE_PLUS_RIGHT_MOUSE_START_DRAG_EVENT)
				|| (event.type == Mi_MIDDLE_PLUS_RIGHT_MOUSE_CLICK_EVENT))
				{
				return(true);
				}
			return(false);
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Gets whether this event has the same type, key and 
					 * modifiers as the given event.
					 * @return 		true if this MiEvent equals the
					 *			given MiEvent
					 *------------------------------------------------------*/
	public		boolean		equals(MiEvent event)
		{
		return(equalsEventType(event));
		}
					/**------------------------------------------------------
	 				 * Gets whether this event has the same type, key and 
					 * modifiers as the given event.
					 * @return 		true if this MiEvent equals the
					 *			given MiEvent
					 *------------------------------------------------------*/
	public		boolean		equalsEventType(MiEvent event)
		{
		return(equalsEventType(event.type, event.key, event.modifiers));
		}
					/**------------------------------------------------------
	 				 * Gets whether this event has the same type, key and 
					 * modifiers as the given type, key and modifiers. If
					 * either this event's modifiers or the given modifiers
					 * parameter is equal to Mi_ANY_MODIFIERS_HELD_DOWN, then
					 * this acts like a wild card and matches (equals) all 
					 * other modifiers values.
					 * @param otherType	the given type to compare to
					 * @param otherKey 	the given key to compare to
					 * @param otherModifiers the given modifiers to compare to
					 * @return 		true if this MiEvent equals the
					 *			given parameters
					 *------------------------------------------------------*/
	public		boolean		equalsEventType(int otherType, int otherKey, int otherModifiers)
		{
		if (type == otherType) 
			{
			if ((modifiers == Mi_ANY_MODIFIERS_HELD_DOWN) 
				|| (otherModifiers == Mi_ANY_MODIFIERS_HELD_DOWN))
				{
				if ((key == otherKey) 
					|| (Character.toLowerCase((char )key) 
					== Character.toLowerCase((char )otherKey)))
					{
					return(true);
					}
				}
			else if (modifiers == otherModifiers)
				{
				if (key == otherKey)
					{
					return(true);
					}
				if ((key <= 'z') && (otherKey <= 'z'))
					{
					char modKey = (char )key;
					if ((modifiers & Mi_SHIFT_KEY_HELD_DOWN) != 0)
						{
						modKey = Character.toUpperCase(modKey);
						}
					char otherModKey = (char )otherKey;
					if ((otherModifiers & Mi_SHIFT_KEY_HELD_DOWN) != 0)
						{
						otherModKey = Character.toUpperCase(otherModKey);
						}
					if (modKey == otherModKey)
						{
						return(true);
						}
					}
				}
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Returns textual information about this MiEvent.
					 * @return		textual information (class name +
					 *			event information)
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) + "[" + eventToString(this) + "]." + id);
		}
					/**------------------------------------------------------
	 				 * Transforms the location information of this MiEvent
					 * using the given transform.
					 * @param xform		the transform
					 *------------------------------------------------------*/
	public		void		doTransform(MiiTransform xform)
		{
		// ---------------------------------------------------------------
		// Old event's world coords are this's device.
		// ---------------------------------------------------------------
		devicePt.copy(worldPt);
		deviceVector.copy(worldVector);
		deviceMouseFootPrint.copy(worldMouseFootPrint);

		xform.dtow(worldPt, worldVector, worldVector);
		xform.dtow(worldPt, worldPt);
		xform.dtow(worldMouseFootPrint, worldMouseFootPrint);
		}
					/**------------------------------------------------------
	 				 * Makes a copy of this event, transforms the location 
					 * information of the copy using the given transform, and
					 * returns the copy.
					 * @param xform		the transform
					 * @return 		the transformed copy 
					 *------------------------------------------------------*/
	public		MiEvent		transform(MiiTransform xform)
		{
		MiEvent event = new MiEvent();
		event.copy(this);

		xform.dtow(worldPt, event.worldPt);
		xform.dtow(worldPt, worldVector, event.worldVector);
		xform.dtow(worldMouseFootPrint, event.worldMouseFootPrint);

		// ---------------------------------------------------------------
		// Old event's world coords are this's device.
		// ---------------------------------------------------------------
		event.devicePt.copy(worldPt);
		event.deviceVector.copy(worldVector);
		event.deviceMouseFootPrint.copy(worldMouseFootPrint);

		return(event);
		}
					/**------------------------------------------------------
	 				 * Gets whether the given event can be combined into this
					 * event. This is generally true for all mouse motion 
					 * events. This is useful when events are occurring faster
					 * than we can process them; if we compress some of these 
					 * events so that we have to process fewer of them then we
					 * will be more likely able to keep up with them.
					 * @param other		the event we may be able to compress
					 *			into this one.
					 * @return 		true if this event and the given 
					 *			other event can be combined
					 *------------------------------------------------------*/
	public		boolean		canCompressEvents(MiEvent other)
		{
		if ((equalsEventType(other))
			&& ((type == Mi_MOUSE_MOTION_EVENT) 
			|| (type == Mi_LEFT_MOUSE_DRAG_EVENT)
			|| (type == Mi_MIDDLE_MOUSE_DRAG_EVENT)
			|| (type == Mi_RIGHT_MOUSE_DRAG_EVENT)
			|| (type == Mi_MIDDLE_PLUS_RIGHT_MOUSE_DRAG_EVENT)))
			{
			return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Compresses (combines) the given event into this event.
					 * @param other		the event we compress into this one.
					 * @return 		this event after compressing with
					 *			the given event
					 *------------------------------------------------------*/
	public		MiEvent		compressEvent(MiEvent other)
		{
		deviceVector.x += other.deviceVector.x;
		deviceVector.y += other.deviceVector.y;
		timeStamp = other.timeStamp;	
		devicePt.x = other.devicePt.x;
		devicePt.y = other.devicePt.y;
		deviceMouseFootPrint.copy(other.deviceMouseFootPrint);

		// ---------------------------------------------------------------
		// Initialize these to something.
		// ---------------------------------------------------------------
		worldPt.copy(devicePt);
		worldVector.copy(deviceVector);
		worldMouseFootPrint.copy(deviceMouseFootPrint);

		return(this);
		}
					/**------------------------------------------------------
	 				 * Process the given AWT event and initialize this MiEvent
					 * from the information it contains and other information
					 * that is maintained locally. Note that targetPart and 
					 * targetList are not set here (not until this event is
					 * sent to a root window).
					 * @param event		the java.awt.Event
					 * @param canvas	the MiCanvas that is recevied the
					 *			event from the AWT.
					 * @return 		true if the event was usable
					 *------------------------------------------------------*/
	public		boolean		interpretEvent(Event event, MiCanvas canvas)
		{
		// ---------------------------------------------------------------
		// For debug...
		// ---------------------------------------------------------------
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_INPUT))
			{
			String str = "(";
			if ((event.modifiers & Event.SHIFT_MASK) != 0)
				str += "<Shift>";
			if ((event.modifiers & Event.CTRL_MASK) != 0)
				str += "<Ctrl>";
			if ((event.modifiers & Event.META_MASK) != 0)
				str += "<Meta>";
			if ((event.modifiers & Event.ALT_MASK) != 0)
				str += "<Alt>";
			str += ")";
			MiDebug.println("Converting AWT event into MICA event: " + event 
				+ ", with modifiers: " + event.modifiers + str);
			}

		// ---------------------------------------------------------------
		// Initialize a few class variables...
		// ---------------------------------------------------------------
		this.canvas 	= canvas;
		type		= NOOP_EVENT;
		key		= 0;
		modifiers	= 0;
		timeStamp 	= event.when;

		// ---------------------------------------------------------------
		// Get the location of the event.
		// ---------------------------------------------------------------
		setDevicePoint(event.x, event.y);

		// ---------------------------------------------------------------
		// Get what modifiers are currently being held down.
		// ---------------------------------------------------------------
		modifiers = 0;
		if ((event.modifiers & Event.SHIFT_MASK) != 0)
			modifiers |= Mi_SHIFT_KEY_HELD_DOWN;
		if ((event.modifiers & Event.CTRL_MASK) != 0)
			modifiers |= Mi_CONTROL_KEY_HELD_DOWN;
		if ((event.modifiers & Event.META_MASK) != 0)
			modifiers |= Mi_META_KEY_HELD_DOWN;
		if ((event.modifiers & Event.ALT_MASK) != 0)
			modifiers |= Mi_ALT_KEY_HELD_DOWN;

		// ---------------------------------------------------------------
		// Assume that this event is going to be usable.
		// ---------------------------------------------------------------
		boolean usable = true;

		// ---------------------------------------------------------------
		// Based on the actual event, do ...
		// ---------------------------------------------------------------
		switch (event.id) 
			{
			case Event.MOUSE_ENTER:
				type = Mi_WINDOW_ENTER_EVENT;
				break;
	  		case Event.MOUSE_EXIT:
				type = Mi_WINDOW_EXIT_EVENT;
				break;
			case Event.MOUSE_MOVE:
				if ((lastEvent.type == Mi_LEFT_MOUSE_DOWN_EVENT)
					|| (lastEvent.type == Mi_MIDDLE_MOUSE_DOWN_EVENT)
					|| (lastEvent.type == Mi_RIGHT_MOUSE_DOWN_EVENT)
					|| (lastEvent.type == Mi_MIDDLE_PLUS_RIGHT_MOUSE_DOWN_EVENT))
					{
					// Bug in JDK (Sun, 1.1.6++) causes motion events to be sent instead 
					// of drag events right after a mouse button is pressed...
					usable = false;
					}
				else
					{
					type = Mi_MOUSE_MOTION_EVENT;
					}
				break;
			case Event.MOUSE_DOWN:
				type = Mi_LEFT_MOUSE_DOWN_EVENT;
				if (numClicks > 0)
					{
					// ---------------------------------------------------------------
					// The user pressed a mouse button after a click event. If the
					// time since that event is too long... or if the distance from
					// that click event is too far...
					// ---------------------------------------------------------------
					if ((timeStamp - lastMouseDownTime 
						> 1.5*mouseDownToClickMaxTimeDelta)
					|| (devicePt.getDistanceSquared(lastMouseDownLocation) 
						> mouseDownToClickMaxPixelDelta))
						{
						// --------------------------------------------------------
						// ... then this event is not part of a multiple click event.
						// --------------------------------------------------------
						numClicks = 0;
						// --------------------------------------------------------
						// Save the position of this down event, it may be the
						// start of a click event.
						// --------------------------------------------------------
						lastMouseDownLocation.copy(devicePt);
						}
					}
				else
					{
					// --------------------------------------------------------
					// Save the position of this down event, it may be the
					// start of a click event.
					// --------------------------------------------------------
					lastMouseDownLocation.copy(devicePt);
					}
				// --------------------------------------------------------
				// Save the time of this down event, it may be the start of
				// a click event.
				// --------------------------------------------------------
				lastMouseDownTime = timeStamp;
				break;
			case Event.MOUSE_DRAG:
				// MS Windows...
				if ((lastEvent.devicePt.x == event.x) && (lastEvent.devicePt.y == event.y))
					{
					usable = false;
					break;
					}

				if ((lastEvent.type != Mi_LEFT_MOUSE_DRAG_EVENT)
					&& (lastEvent.type != Mi_LEFT_MOUSE_START_DRAG_EVENT)
					&& (lastEvent.type != Mi_MIDDLE_MOUSE_DRAG_EVENT)
					&& (lastEvent.type != Mi_MIDDLE_MOUSE_START_DRAG_EVENT)
					&& (lastEvent.type != Mi_RIGHT_MOUSE_DRAG_EVENT)
					&& (lastEvent.type != Mi_RIGHT_MOUSE_START_DRAG_EVENT)
					&& (lastEvent.type != Mi_MIDDLE_PLUS_RIGHT_MOUSE_DRAG_EVENT)
					&& (lastEvent.type != Mi_MIDDLE_PLUS_RIGHT_MOUSE_START_DRAG_EVENT))
					{
					// --------------------------------------------------------
					// This is the first time we have seen a drag event in awhile...
					// If time since last button down is too small and distance to
					// last button down is too small...
					// --------------------------------------------------------
/***CLICK_DEBUG
if (timeStamp - lastMouseDownTime >= mouseDownToClickMaxTimeDelta)
System.out.println("Long enough time so click is now start of a drag event");
else if (devicePt.getDistanceSquared(lastMouseDownLocation) >= mouseDownToClickMaxPixelDelta)
System.out.println("Enough movement so click click is now start of a drag event");
***/

					if ((timeStamp - lastMouseDownTime 
							< mouseDownToClickMaxTimeDelta)
						&& (devicePt.getDistanceSquared(lastMouseDownLocation) 
							< mouseDownToClickMaxPixelDelta))
						{
						// --------------------------------------------------------
						// ... then ignore this event assuming it may be caused by
						// a nervous user.
						// --------------------------------------------------------
						usable = false;
						}
					// --------------------------------------------------------
					// ... else if we are just starting to drag and did not
					// just press a key while dragging (for example <esc> 
					// during a drag-and-drop) then generate a START drag event.
					// --------------------------------------------------------
					else if (!dragging)
						{
						// --------------------------------------------------------
						// Reset location of event to exact location of the mouse
						// down event. Set the type to a start_drag event. And set
						// the we-are-dragging flag.
						// --------------------------------------------------------
						setDevicePoint((int )lastMouseDownLocation.x, 
								(int )lastMouseDownLocation.y);
						
						type = Mi_LEFT_MOUSE_START_DRAG_EVENT;
						dragging = true;
						}
					else
						{
						// --------------------------------------------------------
						// We were already dragging, keep on doing it.
						// --------------------------------------------------------
						type = Mi_LEFT_MOUSE_DRAG_EVENT;
						}
					}
				else
					{
					// --------------------------------------------------------
					// We were already dragging, keep on doing it. Assure that the
					// dragging flag is set.
					// --------------------------------------------------------
					type = Mi_LEFT_MOUSE_DRAG_EVENT;
					dragging = true;
					}
				break;
			case Event.MOUSE_UP:
				// --------------------------------------------------------
				// Check to see if this should really be a click event.
				// If the time since the mouse down was small enough, and
				// the distance from the mouse down is short enough...
				// --------------------------------------------------------
/***CLICK_DEBUG
if (timeStamp - lastMouseDownTime >= mouseDownToClickMaxTimeDelta)
System.out.println("Too slow for a click");
else if (devicePt.getDistanceSquared(lastMouseDownLocation) >= mouseDownToClickMaxPixelDelta)
System.out.println("Too much movement for a click");
****/

				if ((timeStamp - lastMouseDownTime < mouseDownToClickMaxTimeDelta)
					&& (devicePt.getDistanceSquared(lastMouseDownLocation) 
					< mouseDownToClickMaxPixelDelta))
					{
					// --------------------------------------------------------
					// .. if there were no events between the mouse down event
					// and this mouse up event...
					// --------------------------------------------------------
/***CLICK_DEBUG
if ((lastEvent.type != Mi_LEFT_MOUSE_DOWN_EVENT)
&& (lastEvent.type != Mi_MIDDLE_MOUSE_DOWN_EVENT)
&& (lastEvent.type != Mi_RIGHT_MOUSE_DOWN_EVENT))
System.out.println("Last event \"" + lastEvent + "\" not a downevent so not a click");
****/

					if ((lastEvent.type == Mi_LEFT_MOUSE_DOWN_EVENT)
						|| (lastEvent.type == Mi_MIDDLE_MOUSE_DOWN_EVENT)
						|| (lastEvent.type == Mi_RIGHT_MOUSE_DOWN_EVENT)
						|| (lastEvent.type == Mi_MIDDLE_PLUS_RIGHT_MOUSE_DOWN_EVENT))
						{
						// --------------------------------------------------------
						// We have a click event. Inc the number of clicks counter.
						// --------------------------------------------------------
						++numClicks;

						// --------------------------------------------------------
						// Reset location of event to exact location of the mouse
						// down event.
						// --------------------------------------------------------
						setDevicePoint((int )lastMouseDownLocation.x, 
								(int )lastMouseDownLocation.y);

						// --------------------------------------------------------
						// Determine if it is a click, dbl-click or triple-click.
						// --------------------------------------------------------
						if (numClicks == 3)
							type = Mi_LEFT_MOUSE_TRIPLECLICK_EVENT;
						else if (numClicks == 2)
							type = Mi_LEFT_MOUSE_DBLCLICK_EVENT;
						else
							type = Mi_LEFT_MOUSE_CLICK_EVENT;
						}
					else
						{
						// --------------------------------------------------------
						// It is not a click, just an up event.
						// --------------------------------------------------------
						type = Mi_LEFT_MOUSE_UP_EVENT;
						}
					}
				else
					{
					// --------------------------------------------------------
					// It is not a click, just an up event.
					// --------------------------------------------------------
					type = Mi_LEFT_MOUSE_UP_EVENT;
					}
				// --------------------------------------------------------
				// Any mouse up will terminate the dragging state.
				// --------------------------------------------------------
				dragging = false;
				break;
			case Event.KEY_PRESS:
			case Event.KEY_ACTION:
				type = Mi_KEY_PRESS_EVENT;
				key = event.key;
				if (key == 0)
					{
					usable = false;
					break;
					}
/* converts ctrl-j into ctrl-m
				if (key == Mi_LINE_FEED_KEY)
					key = Mi_ENTER_KEY;
***/

				if ((modifiers & Mi_CONTROL_KEY_HELD_DOWN) != 0)
					key += (int )'a' - 1;
				break;

			case Event.KEY_RELEASE:
			case Event.KEY_ACTION_RELEASE:
				type = Mi_KEY_RELEASE_EVENT;
				key = event.key;
				if (key == 0)
					{
					usable = false;
					break;
					}
				// --------------------------------------------------------
				// Replace the line feed key with our enter key
				// --------------------------------------------------------
				if (key == Mi_LINE_FEED_KEY)
					key = Mi_ENTER_KEY;
				// --------------------------------------------------------
				// If this a ctrl+key combination, replace the key sent by
				// AWT by the actual key that was pressed (i.e. replace the
				// ctrl-a key value, which is = 1, with 'a').
				// --------------------------------------------------------
				if ((modifiers & Mi_CONTROL_KEY_HELD_DOWN) != 0)
					key += (int )'a' - 1;
				break;
	    
			case Event.ACTION_EVENT:
				usable = false;
				break;
			case Event.GOT_FOCUS:
				usable = false;
	    			break;
			case Event.LOST_FOCUS:
				usable = false;
	    			break;
			case Event.WINDOW_DESTROY:
				type = Mi_WINDOW_DESTROY_EVENT;
	    			break;
			case Event.WINDOW_EXPOSE:
				type = Mi_WINDOW_REPAINT_EVENT;
	    			break;
			case Event.WINDOW_MOVED:
				type = Mi_WINDOW_RESIZE_EVENT;
	    			break;
			}
		if (usable
			&& (type != Mi_MOUSE_MOTION_EVENT)
			&& (type != Mi_LEFT_MOUSE_TRIPLECLICK_EVENT)
			&& (type != Mi_LEFT_MOUSE_DBLCLICK_EVENT)
			&& (type != Mi_LEFT_MOUSE_CLICK_EVENT)
			&& (type != Mi_LEFT_MOUSE_DOWN_EVENT))
			{
			// --------------------------------------------------------
			// Reset the number of clicks if the event type is not a click
			// or motion event.
			// --------------------------------------------------------
			numClicks = 0;
			}

		// --------------------------------------------------------
		// Replace left mouse button events with middle&right mouse button
		// events if the modifiers indicate that the Alt-key and Meta-key
		// are both held down.
		// --------------------------------------------------------
		if ((((modifiers & (Mi_ALT_KEY_HELD_DOWN | Mi_META_KEY_HELD_DOWN)) 
			== (Mi_ALT_KEY_HELD_DOWN | Mi_META_KEY_HELD_DOWN))
		// new JDK 1.4 does not keep ALY KEY mod set when 3rd mouse button is pressed when second is pressed
		|| (((modifiers & (Mi_META_KEY_HELD_DOWN)) == (Mi_META_KEY_HELD_DOWN))
			&& ((lastEvent.mouseButtonState & Mi_MIDDLE_MOUSE_BUTTON_HELD_DOWN) != 0)))
			&& ((event.id == Event.MOUSE_DOWN)
			|| (event.id == Event.MOUSE_DRAG)
			|| (event.id == Event.MOUSE_UP)))
			{
			if (type == Mi_LEFT_MOUSE_DOWN_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_DOWN_EVENT;
			else if (type == Mi_LEFT_MOUSE_UP_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_UP_EVENT;
			else if (type == Mi_LEFT_MOUSE_CLICK_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_CLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_DBLCLICK_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_DBLCLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_TRIPLECLICK_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_TRIPLECLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_START_DRAG_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_START_DRAG_EVENT;
			else if (type == Mi_LEFT_MOUSE_DRAG_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_DRAG_EVENT;

			// --------------------------------------------------------
			// Remove the alt-key modifier bit from the modifiers field
			// --------------------------------------------------------
			modifiers &= ~(Mi_ALT_KEY_HELD_DOWN | Mi_META_KEY_HELD_DOWN);
			}
		// --------------------------------------------------------
		// Replace left mouse button events with middle mouse button
		// events if the modifiers indicate that the Alt-key was held 
		// down.
		// --------------------------------------------------------
		if (((modifiers & Mi_ALT_KEY_HELD_DOWN) != 0)
			&& ((event.id == Event.MOUSE_DOWN)
			|| (event.id == Event.MOUSE_DRAG)
			|| (event.id == Event.MOUSE_UP)))
			{
			if (type == Mi_LEFT_MOUSE_DOWN_EVENT)
				type = Mi_MIDDLE_MOUSE_DOWN_EVENT;
			else if (type == Mi_LEFT_MOUSE_UP_EVENT)
				type = Mi_MIDDLE_MOUSE_UP_EVENT;
			else if (type == Mi_LEFT_MOUSE_CLICK_EVENT)
				type = Mi_MIDDLE_MOUSE_CLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_DBLCLICK_EVENT)
				type = Mi_MIDDLE_MOUSE_DBLCLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_TRIPLECLICK_EVENT)
				type = Mi_MIDDLE_MOUSE_TRIPLECLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_START_DRAG_EVENT)
				type = Mi_MIDDLE_MOUSE_START_DRAG_EVENT;
			else if (type == Mi_LEFT_MOUSE_DRAG_EVENT)
				type = Mi_MIDDLE_MOUSE_DRAG_EVENT;

			// --------------------------------------------------------
			// Remove the alt-key modifier bit from the modifiers field
			// --------------------------------------------------------
			modifiers &= ~Mi_ALT_KEY_HELD_DOWN;
			}
		// --------------------------------------------------------
		// Replace left mouse button events with right mouse button
		// events if the modifiers indicate that the Meta-key was held 
		// down.
		// --------------------------------------------------------
		if (((modifiers & Mi_META_KEY_HELD_DOWN) != 0)
			&& ((event.id == Event.MOUSE_DOWN)
			|| (event.id == Event.MOUSE_DRAG)
			|| (event.id == Event.MOUSE_UP)))
			{
			if (type == Mi_LEFT_MOUSE_DOWN_EVENT)
				type = Mi_RIGHT_MOUSE_DOWN_EVENT;
			else if (type == Mi_LEFT_MOUSE_UP_EVENT)
				type = Mi_RIGHT_MOUSE_UP_EVENT;
			else if (type == Mi_LEFT_MOUSE_CLICK_EVENT)
				type = Mi_RIGHT_MOUSE_CLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_DBLCLICK_EVENT)
				type = Mi_RIGHT_MOUSE_DBLCLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_TRIPLECLICK_EVENT)
				type = Mi_RIGHT_MOUSE_TRIPLECLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_START_DRAG_EVENT)
				type = Mi_RIGHT_MOUSE_START_DRAG_EVENT;
			else if (type == Mi_LEFT_MOUSE_DRAG_EVENT)
				type = Mi_RIGHT_MOUSE_DRAG_EVENT;

			// --------------------------------------------------------
			// Remove the meta-key modifier bit from the modifiers field
			// --------------------------------------------------------
			modifiers &= ~Mi_META_KEY_HELD_DOWN;
			}

		// MS Windows Symantec CAFE does not keep modifier set for mouse button up events
		if ((lastEvent.mouseButtonState & Mi_MIDDLE_MOUSE_BUTTON_HELD_DOWN) != 0)
			{
			if (type == Mi_LEFT_MOUSE_UP_EVENT)
				type = Mi_MIDDLE_MOUSE_UP_EVENT;
			else if (type == Mi_LEFT_MOUSE_CLICK_EVENT)
				type = Mi_MIDDLE_MOUSE_CLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_DBLCLICK_EVENT)
				type = Mi_MIDDLE_MOUSE_DBLCLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_TRIPLECLICK_EVENT)
				type = Mi_MIDDLE_MOUSE_TRIPLECLICK_EVENT;
			}
		else if ((lastEvent.mouseButtonState & Mi_RIGHT_MOUSE_BUTTON_HELD_DOWN) != 0)
			{
			if (type == Mi_LEFT_MOUSE_UP_EVENT)
				type = Mi_RIGHT_MOUSE_UP_EVENT;
			else if (type == Mi_LEFT_MOUSE_CLICK_EVENT)
				type = Mi_RIGHT_MOUSE_CLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_DBLCLICK_EVENT)
				type = Mi_RIGHT_MOUSE_DBLCLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_TRIPLECLICK_EVENT)
				type = Mi_RIGHT_MOUSE_TRIPLECLICK_EVENT;
			}
		else if ((lastEvent.mouseButtonState & Mi_MIDDLE_PLUS_RIGHT_MOUSE_BUTTON_HELD_DOWN) != 0)
			{
			if (type == Mi_LEFT_MOUSE_UP_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_UP_EVENT;
			else if (type == Mi_LEFT_MOUSE_CLICK_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_CLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_DBLCLICK_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_DBLCLICK_EVENT;
			else if (type == Mi_LEFT_MOUSE_TRIPLECLICK_EVENT)
				type = Mi_MIDDLE_PLUS_RIGHT_MOUSE_TRIPLECLICK_EVENT;
			}

		// --------------------------------------------------------
		// Initialize this event's mouse button state from the last
		// event's mouse button state. Then based on this event's type,
		// adjust this event's mouse button state appropriately.
		// --------------------------------------------------------
		mouseButtonState = lastEvent.mouseButtonState;
		switch (type)
			{
			case Mi_LEFT_MOUSE_DOWN_EVENT:
				mouseButtonState |= Mi_LEFT_MOUSE_BUTTON_HELD_DOWN;
				break;
			case Mi_LEFT_MOUSE_UP_EVENT:
			case Mi_LEFT_MOUSE_CLICK_EVENT:
			case Mi_LEFT_MOUSE_DBLCLICK_EVENT:
			case Mi_LEFT_MOUSE_TRIPLECLICK_EVENT:
				mouseButtonState &= ~Mi_LEFT_MOUSE_BUTTON_HELD_DOWN;
				break;
			case Mi_MIDDLE_MOUSE_DOWN_EVENT:
				mouseButtonState |= Mi_MIDDLE_MOUSE_BUTTON_HELD_DOWN;
				break;
			case Mi_MIDDLE_MOUSE_UP_EVENT:
			case Mi_MIDDLE_MOUSE_CLICK_EVENT:
			case Mi_MIDDLE_MOUSE_DBLCLICK_EVENT:
			case Mi_MIDDLE_MOUSE_TRIPLECLICK_EVENT:
				mouseButtonState &= ~Mi_MIDDLE_MOUSE_BUTTON_HELD_DOWN;
				break;

			case Mi_RIGHT_MOUSE_DOWN_EVENT:
				mouseButtonState |= Mi_RIGHT_MOUSE_BUTTON_HELD_DOWN;
				break;
			case Mi_RIGHT_MOUSE_UP_EVENT:
			case Mi_RIGHT_MOUSE_CLICK_EVENT:
			case Mi_RIGHT_MOUSE_DBLCLICK_EVENT:
			case Mi_RIGHT_MOUSE_TRIPLECLICK_EVENT:
				mouseButtonState &= ~Mi_RIGHT_MOUSE_BUTTON_HELD_DOWN;
				break;

			case Mi_MIDDLE_PLUS_RIGHT_MOUSE_DOWN_EVENT:
				mouseButtonState |= Mi_MIDDLE_PLUS_RIGHT_MOUSE_BUTTON_HELD_DOWN;
				break;
			case Mi_MIDDLE_PLUS_RIGHT_MOUSE_UP_EVENT:
			case Mi_MIDDLE_PLUS_RIGHT_MOUSE_CLICK_EVENT:
			case Mi_MIDDLE_PLUS_RIGHT_MOUSE_DBLCLICK_EVENT:
			case Mi_MIDDLE_PLUS_RIGHT_MOUSE_TRIPLECLICK_EVENT:
				mouseButtonState &= ~Mi_MIDDLE_PLUS_RIGHT_MOUSE_BUTTON_HELD_DOWN;
				break;
			default:
				break;
			}

		// --------------------------------------------------------
		// If this event is usable, copy it into the global lastEvent.
		// Note that targetPart and targetList have not been set yet.
		// --------------------------------------------------------
		if (usable)
			lastEvent.copy(this); 

//System.out.println("ORIG event: " + event);
//System.out.println("MICA event: " + this + "\n");
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_INPUT))
			{
			MiDebug.println("Converted the AWT event into the MICA event: " + this);
			}
		return(usable);
		}
					/**------------------------------------------------------
	 				 * Gets an instance of MiEvent from the cache. Creates a
					 * new MiEvent if the cache is empty. Calls init() on
					 * event sretrieved from the cache.
					 * @return 		An event ready to use.
					 *------------------------------------------------------*/
	public static 	MiEvent		newEvent()
		{
		MiEvent e = null;
		synchronized (freePool)
			{
			if (freePool.size() > 0)
				{
				e = (MiEvent )freePool.lastElement();
				e.init();
				freePool.removeElementAt(freePool.size() - 1);
				}
			else
				{
				e = new MiEvent();
				}
			}
		return(e);
		}

					/**------------------------------------------------------
	 				 * Returns an instance of MiEvent to the cache. 
					 * @param e 		the event
					 *------------------------------------------------------*/
	public static 	void		freeEvent(MiEvent e)
		{
		synchronized (freePool)
			{
			freePool.addElement(e);
			if (freePool.size() > maxNumberOfCachedMiEvents)
				{
				MiDebug.println("MiEvent cache exceeded maximum size: " 
					+ maxNumberOfCachedMiEvents 
					+ ", therefore clearing out cache.");
				freePool.removeAllElements();
				}
			}
		}
					/**------------------------------------------------------
	 				 * Copies the last event into this event. Used by the
					 * event cache.
					 *------------------------------------------------------*/
	protected	void		init()
		{
		copy(lastEvent);
		type = NOOP_EVENT;
		}
					/**------------------------------------------------------
	 				 * Sets the location, in device, of this event. From this
					 * the device point, vector, footprint are set and the
					 * world space couterparts are also initialized to the
					 * same values.
					 * @param x		the device space x coordinate
					 * @param y		the device space y coordinate
					 *------------------------------------------------------*/
	protected	void		setDevicePoint(int x, int y)
		{
		devicePt.x = x;
		devicePt.y = y;


		deviceMouseFootPrint.xmin = x - preferredMouseFootPrintWidth;
		deviceMouseFootPrint.ymin = y - preferredMouseFootPrintWidth;
		deviceMouseFootPrint.xmax = x + preferredMouseFootPrintWidth;
		deviceMouseFootPrint.ymax = y + preferredMouseFootPrintWidth;

		deviceVector.x = devicePt.x - lastEvent.devicePt.x;
		deviceVector.y = devicePt.y - lastEvent.devicePt.y;

		// Initialize
		worldPt.copy(devicePt);
		worldVector.copy(deviceVector);
		worldMouseFootPrint.copy(deviceMouseFootPrint);
		}
					/**------------------------------------------------------
	 				 * Sets given event type, key and modifiers from the given
					 * string description. Valid strings are of form:
					 * <pre>
					 * "a"
					 * "<key>a"
					 * "Ctrl+a"
					 * "CtrlShift<key>a"
					 * "Ctrl+Space"
					 * "Ctrl+<Btn3Down>"
					 * </pre>
					 * @param string	the text to translate to an event
					 * @param event		the event to set from the string
					 * @return 		true if the text was mapped to an
					 *			event (otherwise the text was invalid).
					 * @see			#eventToString
					 *------------------------------------------------------*/
	public	static	boolean 	stringToEvent(String string, MiEvent event)
		{
		event.modifiers	= 0;
		event.key 	= 0;
		event.type 	= Mi_KEY_PRESS_EVENT;
		int i;

		// --------------------------------------------------------
		// Search for modifiers (shift, ctrl...). Add to modifiers
		// value and strip text from given string if found.
		// --------------------------------------------------------
		for (i = 0; i < stringToModifierMap.length; ++i)
			{
			if (string.regionMatches(
				true, 
				0, 
				stringToModifierMap[i].string,
				0, 
				stringToModifierMap[i].string.length()))
				{
				event.modifiers |= stringToModifierMap[i].value;
				string = string.substring(stringToModifierMap[i].string.length());
				}
			}
			
		// --------------------------------------------------------
		// If "<key>" is found, strip and set type = ASCII
		// --------------------------------------------------------
		if (string.regionMatches(true, 0, "<key>", 0, 5))
			{
			string = string.substring(5);
			}

		// --------------------------------------------------------
		// If "+" is found, strip 
		// --------------------------------------------------------
		if ((string.charAt(0) == '+'))
			{
			string = string.substring(1);
			}

		// --------------------------------------------------------
		// If item is a single letter, set key to it and exit.
		// --------------------------------------------------------
		if (string.length() == 1)
			{
			// --------------------------------------------------------
			// Convert to lower case... 
			// --------------------------------------------------------
			char ch = string.charAt(0);
			event.key = Character.toLowerCase(ch);
			if (Character.isUpperCase(ch))
				{
				// --------------------------------------------------------
				// Indicate in modifiers that the key needs to be in uppercase
				// --------------------------------------------------------
				event.modifiers |= Mi_SHIFT_KEY_HELD_DOWN;
				}
			return(true);
			}

		// --------------------------------------------------------
		// Not a single character, see if is a key event, if so set
		// event.key to it's value and we are done (event.type was
		// set to be a key event above).
		// --------------------------------------------------------
		for (i = 0; i < stringToKeyEventMap.length; ++i)
			{
			if (string.equalsIgnoreCase(stringToKeyEventMap[i].string))
				{
				event.key = stringToKeyEventMap[i].value;
				return(true);
				}
			}

		// --------------------------------------------------------
		// Not a key event, see if is a mouse event, if so set
		// event.type to it's value and we are done.
		// --------------------------------------------------------
		for (i = 0; i < stringToMouseEventMap.length; ++i)
			{
			if (string.equalsIgnoreCase(stringToMouseEventMap[i].string))
				{
				event.type = stringToMouseEventMap[i].value;
				return(true);
				}
			}
		// --------------------------------------------------------
		// Could not convert text to an event.
		// --------------------------------------------------------
		return(false);
		}
	
					/**------------------------------------------------------
	 				 * Gets a string description of the given event.
					 * @param event		the event to translate to a string
					 * @return 		the string describing the event
					 * @see			#stringToEvent
					 *------------------------------------------------------*/
	public	static	String 		eventToString(MiEvent event)
		{
		int	i;
		String	string = new String();

		// --------------------------------------------------------
		// Convert event modifiers to a text string
		// --------------------------------------------------------
		int modifiers = event.modifiers;
		for (i = 0; ((modifiers != 0) && (i < stringToModifierMap.length)); ++i)
			{
			if ((stringToModifierMap[i].value & modifiers) != 0)
				{
				string = string.concat(stringToModifierMap[i].string);
				modifiers &= ~stringToModifierMap[i].value;
				}
			}

		// --------------------------------------------------------
		// Convert event type to a text string
		// --------------------------------------------------------
		for (i = 0; i < stringToMouseEventMap.length; ++i)
			{
			if (stringToMouseEventMap[i].value == event.type)
				{
				string = string.concat(stringToMouseEventMap[i].string);
				break;
				}
			}
		// --------------------------------------------------------
		// If event type is a key event, convert the key value a text string
		// --------------------------------------------------------
		if ((event.type == Mi_KEY_PRESS_EVENT)
			|| (event.type == Mi_KEY_RELEASE_EVENT))
			{
			// --------------------------------------------------------
			// If not an alphanumeric letter...
			// --------------------------------------------------------
			if ((event.key > 'z') || (event.key <= ' '))
				{
				// --------------------------------------------------------
				// It must be a special key (like page down, ...)
				// --------------------------------------------------------
				boolean foundDescription = false;
				for (i = 0; i < stringToKeyEventMap.length; ++i)
					{
					if (stringToKeyEventMap[i].value == event.key)
						{
						string = string.concat(stringToKeyEventMap[i].string);
						foundDescription = true;
						break;
						}
					}
				// --------------------------------------------------------
				// Didn't find the textual description of the key value, just
				// make the text it's value then.
				// --------------------------------------------------------
				if (!foundDescription)
					{
					string = string.concat("[" + event.key + "]");
					}
				}
			else
				{
				// --------------------------------------------------------
				// Add the regular alphanumeric character.
				// --------------------------------------------------------
				string = string.concat("" + (char )event.key);
				}
			}
		return(string);
		}
	}

