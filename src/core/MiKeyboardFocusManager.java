
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

/**----------------------------------------------------------------------------------------------
 * This class manages the assignment and traversal of keyboard 
 * and enter key focus to parts in a window. It also manages the
 * forwarding of keyboard events to the part with keyboard focus
 * and forwarding of enter key events to the part with enter key
 * focus.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiKeyboardFocusManager 
	{
	public static final int		Mi_MOUSE_THEN_KEYBOARD_FOCUS_KEYBOARD_FOCUS_POLICY	= 1;
	public static final int		Mi_KEYBOARD_FOCUS_ONLY_KEYBOARD_FOCUS_POLICY		= 2;
	public static final int		Mi_KEYBOARD_FOCUS_THEN_MOUSE_KEYBOARD_FOCUS_POLICY	= 3;

	private		MiiKeyFocusTraversalGroup	tabGroup;
	private		MiWindow			window;
	private		MiPart				defaultKeyboardFocusObj;
	private		MiPart				defaultEnterKeyFocusObj;
	private		MiPart				keyboardFocusObj;
	private		MiPart				enterKeyFocusObj;
	private		int				keyboardFocusPolicy	
						= Mi_KEYBOARD_FOCUS_THEN_MOUSE_KEYBOARD_FOCUS_POLICY;



					/**------------------------------------------------------
	 		 		 * Constructs a new MiKeyboardFocusManager. The default
					 * keyboard focus policy is
					 * Mi_KEYBOARD_FOCUS_THEN_MOUSE_KEYBOARD_FOCUS_POLICY
					 * @param window	the root window that this will 
					 *			manage.
			 		 *------------------------------------------------------*/
	public				MiKeyboardFocusManager(MiWindow window)
		{
		this.window = window;
		window.appendEventHandler(new MiKeyboardFocusEventManager(this));
		}

					/**------------------------------------------------------
	 		 		 * Set the keyboard focus policy. Valid policies are:
					 *
					 * Mi_MOUSE_THEN_KEYBOARD_FOCUS_KEYBOARD_FOCUS_POLICY
					 * 	Forward key events to the part underneath the mouse
					 *	and then, if not consumed, send it to the part
					 *	with keyboard focus. <NOT IMPLEMENTED>.
					 *
					 * Mi_KEYBOARD_FOCUS_ONLY_KEYBOARD_FOCUS_POLICY
					 * 	Forward key events to the part with keyboard focus
					 *	and then consume event, do not send it to the
					 *	part underneath the mouse.	
					 *
					 * Mi_KEYBOARD_FOCUS_THEN_MOUSE_KEYBOARD_FOCUS_POLICY
					 * 	Forward key events to the part with keyboard focus
					 *	and then, if not consumed, send it to the part
					 *	underneath the mouse. This is the default value.
					 *
					 * Mi_NONE
					 * 	No key events are forwarded.
					 *
					 * @param policy	the new policy
			 		 *------------------------------------------------------*/
	public		void		setKeyboardFocusPolicy(int policy)
		{
		keyboardFocusPolicy = policy;
		}
					/**------------------------------------------------------
	 		 		 * Returns the keyboard focus policy.
					 * @return		the current keyboard focus policy
					 * @see			#setKeyboardFocusPolicy
			 		 *------------------------------------------------------*/
	public		int		getKeyboardFocusPolicy()
		{
		return(keyboardFocusPolicy);
		}

					/**------------------------------------------------------
	 		 		 * Get the subject container within which this manages
					 * keyboard focus.
					 * @return 		the subject
			 		 *------------------------------------------------------*/
	public		MiWindow	getSubject()
		{
		return(window);
		}
					/**------------------------------------------------------
	 		 		 * Sets the default keyboard focus to the given part. This
			 		 * part is assigned keyboard focus whenever the associated
			 		 * window is made visible after being invisible.
					 * @param part		the first part to get keyboard focus
			 		 *------------------------------------------------------*/
	public		void		setDefaultKeyboardFocus(MiPart part)
		{
		defaultKeyboardFocusObj = part;
		}
					/**------------------------------------------------------
	 				 * Gets the default keyboard focus. This part is assigned 
			 		 * keyboard focus whenever the associated window is made
			 		 * visible after being invisible.
					 * @return 		the first part to get keyboard focus
			 		 *------------------------------------------------------*/
	public		MiPart		getDefaultKeyboardFocus()
		{
		return(defaultKeyboardFocusObj);
		}
					/**------------------------------------------------------
	 		 		 * Sets the default enter key focus. This part is assigned 
			 		 * enter key focus whenever the associated window is made
			 		 * visible after being invisible.
					 * @param part		the first part to get enter key
					 *			focus
			 		 *------------------------------------------------------*/
	public		void		setDefaultEnterKeyFocus(MiPart part)
		{
		defaultEnterKeyFocusObj = part;
		}
					/**------------------------------------------------------
	 		 		 * Gets the default enter key focus. This part is assigned 
			 		 * enter key focus whenever the associated window is made
			 		 * visible after being invisible.
					 * @return 		the first part to get enter key
					 *			focus
			 		 *------------------------------------------------------*/
	public		MiPart		getDefaultEnterKeyFocus()
		{
		return(defaultEnterKeyFocusObj);
		}
					/**------------------------------------------------------
	 		 		 * Get the part with the current keyboard focus, if any.
					 * @return		the part with keyboard focus.
			 		 *------------------------------------------------------*/
	public		MiPart		getKeyboardFocus()
		{
		return(keyboardFocusObj);
		}
					/**------------------------------------------------------
	 		 		 * Remove keyboard focus from the part with the current 
					 * keyboard focus, if any.
			 		 *------------------------------------------------------*/
	public		void		clearKeyboardFocus()
		{
		if (keyboardFocusObj != null)
			{
			keyboardFocusObj.setKeyboardFocus(false);
			keyboardFocusObj = null;
			}
		}
					/**------------------------------------------------------
	 		 		 * Get the part with the current enter key focus, if any.
					 * @return		the part with enter key focus.
			 		 *------------------------------------------------------*/
	public		MiPart		getEnterKeyFocus()
		{
		if ((enterKeyFocusObj != null)
			&& (MiUtility.partAndContainersAreVisible(enterKeyFocusObj)))
			{
			return(enterKeyFocusObj);
			}
		return(null);
		}
					/**------------------------------------------------------
	 		 		 * Remove enter key focus from the part with the current 
					 * keyboard focus, if any.
			 		 *------------------------------------------------------*/
	public		void		clearEnterKeyFocus()
		{
		if (enterKeyFocusObj != null)
			{
			enterKeyFocusObj.setEnterKeyFocus(false);
			enterKeyFocusObj = null;
			}
		}
					/**------------------------------------------------------
	 		 		 * Advance the keyboard to the next valid part. If this
					 * part also accepts enter key focus then enter key focus
					 * is also set to this part. This is used internally by 
					 * tab key traversals.
			 		 *------------------------------------------------------*/
	public		void		moveFocusToNext()
		{
		if (tabGroup != null)
			{
			MiPart obj = tabGroup.getNext(keyboardFocusObj);
			if (obj != null)
				requestKeyboardFocus(obj);
			else
				obj = tabGroup.getNext(null);
			if (obj != null)
				requestKeyboardFocus(obj);
			}
		}

					/**------------------------------------------------------
	 		 		 * Advance the keyboard to the previous valid part. If this
					 * part also accepts enter key focus then enter key focus
					 * is also set to this part. This is used internally by 
					 * tab key traversals.
			 		 *------------------------------------------------------*/
	public		void		moveFocusToPrevious()
		{
		if (tabGroup != null)
			{
			MiPart obj = tabGroup.getPrevious(keyboardFocusObj);
			if (obj != null)
				requestKeyboardFocus(obj);
			else
				 obj = tabGroup.getPrevious(null);
			if (obj != null)
				requestKeyboardFocus(obj);
			}
		}

	//***************************************************************************************
	// Protected methods
	//***************************************************************************************

					/**------------------------------------------------------
	 		 		 * Initialize the keyboard and enter key focus. This is
					 * usually called when a window is made visible and 
					 * assigns the keyboard and enter key focus. This method
					 * looks at:
					 *	defaultXXXFocus
					 * if null then looks at:
					 *	all hints in all parts of the window <NOT IMPLEMENTED,
					 *	currently enterKeyFocus flag itself may be set as a
					 *	'hint'>.
					 * if none found then looks at:
					 *	the first part returned by the MiiKeyFocusTraversalGroup
					 * if null then:
					 *	no focus is assigned.
			 		 *------------------------------------------------------*/
	protected	void		initialize(MiWindow window)
		{
		MiPart obj;
		// ---------------------------------------------------------------
		// Get the traversal group, if any, assigned to the window.
		// ---------------------------------------------------------------
		tabGroup = window.getKeyFocusTraversalGroup();

		if (defaultKeyboardFocusObj != null)
			{
			requestKeyboardFocus(defaultKeyboardFocusObj);
			}
		else if ((tabGroup != null) && ((obj = tabGroup.getNext(null)) != null))
			{
			requestKeyboardFocus(obj);
			}

		if (defaultEnterKeyFocusObj != null)
			{
			requestEnterKeyFocus(defaultEnterKeyFocusObj);
			}
		if (enterKeyFocusObj == null)
			{
			searchForEnterKeyFocusFlagInContainer(window.getCurrentLayer());
			defaultEnterKeyFocusObj = enterKeyFocusObj;
			}
		if ((keyboardFocusObj == null) && (enterKeyFocusObj != null) )
			{
			requestKeyboardFocus(enterKeyFocusObj);
			}
		if ((enterKeyFocusObj == null) && (keyboardFocusObj != null) )
			{
			requestEnterKeyFocus(keyboardFocusObj);
			}
		// For sanity check...
		clearAllDuplicateKeyFocusFlagsInAllParts(window.getCurrentLayer());
		}
					/**------------------------------------------------------
	 		 		 * Set keyboard focus to the given part. If not possible
					 * then return false. If given part is null then no part
					 * will have focus.
					 * @param part		the part to set keyboard focus to
					 * @return 		true if succeeded
			 		 *------------------------------------------------------*/
	protected	boolean		requestKeyboardFocus(MiPart part)
		{
		if (part != keyboardFocusObj)
			{
			if (keyboardFocusObj != null)
				{
				if (!keyboardFocusObj.dispatchActionRequest(MiiActionTypes.Mi_LOST_KEYBOARD_FOCUS_ACTION))
					return(false);
				keyboardFocusObj.setKeyboardFocus(false);
				if (MiDebug.debug 
					&& MiDebug.isTracing(null, MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
					{
					MiDebug.println("Removing keyboard focus from: " + keyboardFocusObj);
					}
				keyboardFocusObj = null;
				}
			if (part != null)
				{
				if (!part.dispatchActionRequest(MiiActionTypes.Mi_GOT_KEYBOARD_FOCUS_ACTION))
					return(false);

				keyboardFocusObj = part;
				// ---------------------------------------------------------------
				// Advance enter key focus along with keyboard focus, if possible
				// ---------------------------------------------------------------
				requestEnterKeyFocus(keyboardFocusObj);
				part.setKeyboardFocus(true);
				if (MiDebug.debug 
					&& MiDebug.isTracing(null, MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
					{
					MiDebug.println("Setting keyboard focus to: " + keyboardFocusObj);
					}
				}
			}
		return(true);
		}
					/**------------------------------------------------------
	 		 		 * Set enter key focus to the given part. If not possible
					 * then return false. If given part is null then no part
					 * will have focus.
					 * @param part		the part to set enter key focus to
					 * @return 		true if succeeded
			 		 *------------------------------------------------------*/
	protected	boolean		requestEnterKeyFocus(MiPart part)
		{
		if (part == enterKeyFocusObj)
			return(true);

		if ((part == null) || (part.isAcceptingEnterKeyFocus()))
			{
			if (enterKeyFocusObj != null)
				{
				enterKeyFocusObj.setEnterKeyFocus(false);
				if (MiDebug.debug 
					&& MiDebug.isTracing(null, MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
					{
					MiDebug.println("Removed enterKey focus from: " + enterKeyFocusObj);
					}
				}

			enterKeyFocusObj = part;
			if (part == null)
				return(true);

			enterKeyFocusObj.setEnterKeyFocus(true);
			if (MiDebug.debug 
				&& MiDebug.isTracing(null, MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
				{
				MiDebug.println("Setting enterKey focus to: " + enterKeyFocusObj);
				}
			return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
	 		 		 * Search container for any part that accepts enter key
					 * focus and, if found, give it enter key focus.
					 * @param container	the container to search
			 		 *------------------------------------------------------*/
	protected	void		searchForEnterKeyFocusFlagInContainer(MiPart container)
		{
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if (part.hasEnterKeyFocus())
				requestEnterKeyFocus(part);
			else
				searchForEnterKeyFocusFlagInContainer(part);

			if (enterKeyFocusObj != null)
				return;
			}
		}
					/**------------------------------------------------------
	 		 		 * Clear all focus flags for all parts that do not have
					 * key focus. Sometimes the focus flags will be set as
					 * hints.
					 * @param container	the container to search
			 		 *------------------------------------------------------*/
	protected	void		clearAllDuplicateKeyFocusFlagsInAllParts(MiPart container)
		{
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			if (part.hasKeyboardFocus() && (part != keyboardFocusObj))
				part.setKeyboardFocus(false);
			if (part.hasEnterKeyFocus() && (part != enterKeyFocusObj))
				part.setEnterKeyFocus(false);
		
			clearAllDuplicateKeyFocusFlagsInAllParts(part);
			}
		}
					/**------------------------------------------------------
	 		 		 * Remove keyboard and enter key focus from any parts that
					 * are no longer a part of the window this manages.
			 		 *------------------------------------------------------*/
	protected	void		validateKeyboardAndEnterKeyFocus()
		{
		if ((keyboardFocusObj != null) && (keyboardFocusObj.getRootWindow() != window))
			{
			keyboardFocusObj.setKeyboardFocus(false);
			if (MiDebug.debug 
				&& MiDebug.isTracing(null, MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
				{
				MiDebug.println("Removing keyboard focus from: " + keyboardFocusObj
					+ " because no longer a part of the window");
				}
			keyboardFocusObj = null;
			}

		if ((enterKeyFocusObj != null) && (enterKeyFocusObj.getRootWindow() != window))
			{
			enterKeyFocusObj.setEnterKeyFocus(false);
			if (MiDebug.debug 
				&& MiDebug.isTracing(null, MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
				{
				MiDebug.println("Removing enterKey focus from: " + enterKeyFocusObj
					+ " because no longer a part of the window");
				}
			enterKeyFocusObj = null;
			}
		}
	}
/**----------------------------------------------------------------------------------------------
 * This event handler class handles end-user assignment and traversal
 * of keyboard  and enter key focus to parts in a window. It also 
 * handles the forwarding of keyboard events to the part with keyboard
 * focus and forwarding of enter key events to the part with enter key
 * focus. Events handled are:
 *
 *	Mi_LEFT_MOUSE_DOWN_EVENT
 *		Set the keyboard focus to the part under the mouse if
 *		the part is accepting keyboard focus (and enter key 
 *		focus as well if it is accepting enter key focus).
 *	Mi_TAB_KEY
 *		Advance the keyboard focus to the next part (and enter
 *		key focus too, if the next part is accepting enter key 
 *		focus). However, the tab event is first sent to the
 *		part with keyboard focus and if it consumed the tab
 *		event, then the focus state is left as it was.
 *
 *	Mi_TAB_KEY + Mi_CONTROL_KEY_HELD_DOWN
 *		Advance the keyboard focus to the next part (and enter
 *		key focus too, if the next part is accepting enter key 
 *		focus). 
 *
 *	Mi_TAB_KEY + Mi_SHIFT_KEY_HELD_DOWN
 *		Move the keyboard focus to the previous part (and enter
 *		key focus too, if the previous part is accepting enter key 
 *		focus). 
 *
 * Add MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING to MiDebug.tracingMode to
 * trace keyboard event forwarding.
 *----------------------------------------------------------------------------------------------*/
class MiKeyboardFocusEventManager extends MiEventHandler
	{
	private		MiKeyboardFocusManager	focusManager;


					/**------------------------------------------------------
	 		 		 * Construct the new MiKeyboardFocusEventManager.
					 * @param focusManager		the keybaord focus manager
			 		 *------------------------------------------------------*/
	public				MiKeyboardFocusEventManager(MiKeyboardFocusManager focusManager)
		{
		this.focusManager = focusManager;
		setType(Mi_SHORT_CUT_EVENT_HANDLER_TYPE);
		setPositionDependent(false);
		}

					/**------------------------------------------------------
	 		 		 * Process the event.
					 * @param event 	the end-user event
					 * @return		Mi_CONSUME_EVENT is consumed,
					 *			Mi_PROPOGATE_EVENT otherwise
			 		 *------------------------------------------------------*/
	public		int		processEvent(MiEvent event)
		{
//System.out.println("KeyboardFocusManager. event = " + event);
		if (!isEnabled())
			{
			return(Mi_PROPOGATE_EVENT);
			}
		if (event.getTargetPath().size() < 1)
			{
			return(Mi_PROPOGATE_EVENT);
			}
		MiPart partUnderMouse = event.getTargetPath().elementAt(0);
		if ((partUnderMouse == null) || 
			(partUnderMouse.getRootWindow() != focusManager.getSubject()))
			//((partUnderMouse.getContainingEditor() != focusManager.getSubject())
			//&& (partUnderMouse.getContainingWindow() != focusManager.getSubject())
			{
			return(Mi_PROPOGATE_EVENT);
			}

		focusManager.validateKeyboardAndEnterKeyFocus();

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
			{
			MiDebug.println("enterKeyFocus = " + focusManager.getEnterKeyFocus());
			MiDebug.println("keyboardFocusObj = " + focusManager.getKeyboardFocus());
			MiDebug.println("MiKeyboardFocusEventManager - processEvent: " + event);
			}

		MiPart obj;
		if (event.type == MiEvent.Mi_LEFT_MOUSE_DOWN_EVENT)
			{
			MiParts pickList = event.getTargetPath();
			for (int i = 0; i < pickList.size(); ++i)
				{
				MiPart part = pickList.elementAt(i);
				if ((part.isAcceptingKeyboardFocus()) && (part.isSensitive()))
					{
					if (part != focusManager.getKeyboardFocus())
						{
						focusManager.requestKeyboardFocus(part);
						event.setMovedKeyboardFocusTo(part);
						}
					return(Mi_PROPOGATE_EVENT);
					}
				// Reached root of all objects we are handling here...?
				if (part == event.editor)
					{
					return(Mi_PROPOGATE_EVENT);
					}
				}
			focusManager.requestKeyboardFocus(null);
			}
		// Check to see if the focus object wants the tab key itself...
		else if ((event.type == MiEvent.Mi_KEY_PRESS_EVENT) 
			&& (event.key == MiEvent.Mi_TAB_KEY)
			&& (event.modifiers == 0))
			{
			if (((obj = focusManager.getKeyboardFocus()) == null)
				|| (!obj.isAcceptingTabKeys())
				|| (forwardEventToPart(event, obj) != Mi_CONSUME_EVENT))
				{
				focusManager.moveFocusToNext();
				}
			return(Mi_CONSUME_EVENT);
			}
		else if ((event.type == MiEvent.Mi_KEY_PRESS_EVENT)
			// Check for 'i' cause Solaris AWT 1.0.2 returns 'i' here...
			&& ((event.key == MiEvent.Mi_TAB_KEY) || (event.key == 'i'))
			&& (event.modifiers == MiEvent.Mi_CONTROL_KEY_HELD_DOWN))
			{
			focusManager.moveFocusToNext();
			return(Mi_CONSUME_EVENT);
			}
		else if ((event.type == MiEvent.Mi_KEY_PRESS_EVENT)
			&& (event.key == MiEvent.Mi_TAB_KEY)
			&& (event.modifiers == MiEvent.Mi_SHIFT_KEY_HELD_DOWN))
			{
			focusManager.moveFocusToPrevious();
			return(Mi_CONSUME_EVENT);
			}
		else if (((event.type == MiEvent.Mi_KEY_PRESS_EVENT) 
			|| (event.type == MiEvent.Mi_KEY_RELEASE_EVENT))
			//&& ((event.modifiers == 0)
			//|| (event.modifiers == MiEvent.Mi_SHIFT_KEY_HELD_DOWN))
			&& ((obj = focusManager.getKeyboardFocus()) != null))
			{
			return(forwardEventToPart(event, obj));
			}
		return(Mi_PROPOGATE_EVENT);
		}
					/**------------------------------------------------------
	 		 		 * Forward key event to the target, either the part with
					 * enter key focus or keyboard focus.
					 * @param event		the event to forward
					 * @param obj		the part with keybaord focus
			 		 *------------------------------------------------------*/
	protected	int		forwardEventToPart(MiEvent event, MiPart obj)
		{
		boolean locSpecific = event.locationSpecific;
		int sensorType = event.handlerTargetType;
		event.locationSpecific = false;
		event.handlerTargetType = Mi_ORDINARY_EVENT_HANDLER_TYPE;
		if ((event.equalsEventType(MiEvent.Mi_KEY_PRESS_EVENT, MiEvent.Mi_ENTER_KEY, 0))
			&& (focusManager.getEnterKeyFocus() != null))
			{
			obj = focusManager.getEnterKeyFocus();
			}

		if (focusManager.getKeyboardFocusPolicy() == MiiTypes.Mi_NONE)
			return(Mi_PROPOGATE_EVENT);

		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_KEYBOARD_FOCUS_DISPATCHING))
			{
			MiDebug.println("SENDING EVENT: " + event + " TO: " + obj);
			}

		// ---------------------------------------------------------------
		// If the window to which this is assigned has the focus, then temporarily
		// disable this event handler so we do not reprocess this event we are 
		// dispatching and loop infinitely
		// ---------------------------------------------------------------
		if (obj == focusManager.getSubject())
			setEnabled(false);

		int status = 0;
		if (obj != focusManager.getSubject())
			{
			MiEvent fEvent = MiEvent.newEvent();
			fEvent.copy(event);
			transformEventToTargetCoordinates(obj, focusManager.getSubject(), fEvent);
			status = obj.dispatchEvent(fEvent);
			MiEvent.freeEvent(fEvent);
			}
		else
			{
			status = obj.dispatchEvent(event);
			}

		if (obj == focusManager.getSubject())
			setEnabled(true);

		event.handlerTargetType = sensorType;
		event.locationSpecific = locSpecific;
		if (focusManager.getKeyboardFocusPolicy() == 
			MiKeyboardFocusManager.Mi_KEYBOARD_FOCUS_ONLY_KEYBOARD_FOCUS_POLICY)
			{
			return(Mi_CONSUME_EVENT);
			}
		return(status);
		}

		

	protected	void		transformEventToTargetCoordinates(MiPart target, MiPart source, MiEvent event)
		{
		FastVector transforms = new FastVector();

		// ---------------------------------------------------------------
		// Transform the event to the desired part's transform.
		// ---------------------------------------------------------------
		MiUtility.getTransformsAlongPath(source, target, transforms);
		for (int j = transforms.size() - 1; j >= 0; --j)
			{
			event.doTransform((MiiTransform )transforms.elementAt(j));
			}

		event.editor = target.getContainingEditor();
		}

	}


