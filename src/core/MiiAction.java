
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

/**----------------------------------------------------------------------------------------------
 * This interface describes a Mica <i>Action</i>. An action is generated
 * by a graphics MiPart (often in response to <i>Events</i> generated by
 * users). Usually this occurs when the MiPart changes in some way, either
 * geometrically (it moved or changed size), visibly (changed color, became
 * visible, etc.) or state (it was selected, deselected).
 * <p>
 * MiiActions are designed so that usually it itself contains a reference
 * to the action handler (observer) it will be dispatched to. In this way, 
 * actions are not constructed and destroyed when they are dispatched, they 
 * just have their context dependent data modified and are sent on to the 
 * handler.
 *
 * @see MiiActionHandler
 * @see MiiActionTypes
 * @see MiPart#appendActionHandler
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiAction extends MiiActionTypes
	{
				/**------------------------------------------------------
				 * Sets the MiPart that originally generated the action.
				 * This is set automatically by Mica whenever the action
				 * is dispatched.
				 * @param source	the part that generated the action
				 *------------------------------------------------------*/
	void			setActionSource(MiPart source);

				/**------------------------------------------------------
				 * Gets the MiPart that originally generated the action.
				 * @return 		the part that generated the action
				 *------------------------------------------------------*/
	MiPart			getActionSource();

				/**------------------------------------------------------
				 * Sets the MiPart that dispatched the action to the 
				 * MiiActionHandler.
				 * This is set automatically by Mica whenever the action
				 * is dispatched.
				 * @param source 	the part that dispatched the action
				 *------------------------------------------------------*/
	void			setObservedObject(MiPart source);

				/**------------------------------------------------------
				 * Gets the MiPart that dispatched the action to the 
				 * MiiActionHandler.
				 * @return 	 	the part that dispatched the action
				 *------------------------------------------------------*/
	MiPart			getObservedObject();

				/**------------------------------------------------------
				 * Sets the action handler this action will be dispatched
				 * to.
				 * @param handler 	the action handler to send this
				 *			action to
				 *------------------------------------------------------*/
	void			setActionHandler(MiiActionHandler handler);

				/**------------------------------------------------------
				 * Gets the action handler this action will be dispatched
				 * to.
				 * @return 	 	the action handler to send this
				 *			action to
				 *------------------------------------------------------*/
	MiiActionHandler	getActionHandler();


				/**------------------------------------------------------
				 * Sets the actual type of this dispatched action. This 
				 * type is one of the valid types of this action.
				 * MiiActionHandler.
				 * This is set automatically by Mica whenever the action
				 * is dispatched.
				 * @return 	 	the part that dispatched the action
				 * @see 	 	#isValidActionType
				 *------------------------------------------------------*/
	void			setActionType(int actionType);

				/**------------------------------------------------------
				 * Gets the actual type of this dispatched action. Includes
				 * action and phase information.
				 * @return 	 	the action type of this action
				 * @see 	 	#isValidActionType
				 *------------------------------------------------------*/
	int			getActionType();

				/**------------------------------------------------------
				 * Gets whether the actual type of this dispatched action
				 * is the same as the given type. Includes action and phase
				 * information.
				 * @return 	 	true if the given action type is
				 *			the same action type this action
				 *			has.
				 * @see 	 	#getActionType
				 * @see 	 	#isValidActionType
				 *------------------------------------------------------*/
	boolean			hasActionType(int actionType);

				/**------------------------------------------------------
				 * Gets whether given action type is an action type for
				 * which this action will be dispatched to it's action
				 * handler.
				 * @return 	 	true if the given action type is
				 *			a valid action type or this action
				 * @see 	 	#isValidActionType
				 *------------------------------------------------------*/
	boolean			isValidActionType(int actionType);

				/**------------------------------------------------------
				 * Sets the user information field of this action.
				 * @param info 	 	the information supplied by the
				 *			programmer.
				 * @see 	 	#getActionUserInfo
				 *------------------------------------------------------*/
	void			setActionUserInfo(Object info);

				/**------------------------------------------------------
				 * Gets the user information field of this action.
				 * @return  	 	the information supplied by the
				 *			programmer.
				 * @see 	 	#setActionUserInfo
				 *------------------------------------------------------*/
	Object			getActionUserInfo();

				/**------------------------------------------------------
				 * Sets the system information field of this action. This
				 * used when the MiPart that generated the action thinks
				 * additional information is required to completely describe
				 * the action. For example the Mi_COPY_ACTION action is
				 * generated by a MiPart that is being copied and has 
				 * system info equal to the new MiPart copy.
				 * This is set automatically by Mica whenever the action
				 * is dispatched, if the specific action need it.
				 * @param info  	the information supplied by the
				 *			generator of the action or null.
				 * @see 	 	#setActionUserInfo
				 * @see 	 	#getActionSystemInfo
				 *------------------------------------------------------*/
	void			setActionSystemInfo(Object info);

				/**------------------------------------------------------
				 * Gets the system information field of this action. This
				 * used when the MiPart that generated the action thinks
				 * additional information is required to completely describe
				 * the action. For example the Mi_COPY_ACTION action is
				 * generated by a MiPart that is being copied and has 
				 * system info equal to the new MiPart copy.
				 * @return 	  	the information supplied by the
				 *			generator of the action or null.
				 * @see 	 	#getActionUserInfo
				 * @see 	 	#setActionSystemInfo
				 *------------------------------------------------------*/
	Object			getActionSystemInfo();

				/**------------------------------------------------------
				 * Adds (if necessary) and sets the given named resource
				 * to the given value. Every action can have an unlimited
				 * number of programmer-specified resources. 
				 * @param name  	the resource name
				 * @param value  	the resource value (non-null)
				 * @see 	 	#getResource
				 *------------------------------------------------------*/
	void			setResource(String name, Object value);

				/**------------------------------------------------------
				 * Gets the value of the given named resource. If
				 * to the given value. Every action can have an unlimited
				 * number of programmer-specified resources. 
				 * @param name  	the resource name
				 * @param value  	the resource value
				 * @see 	 	#setResource
				 *------------------------------------------------------*/
	Object			getResource(String name);

				/**------------------------------------------------------
				 * Gets whether the given phase is the phase of this 
				 * dispatched action. Possible values are:
				 *    Mi_REQUEST_ACTION_PHASE
				 *    Mi_CANCEL_ACTION_PHASE
				 *    Mi_COMMIT_ACTION_PHASE
				 *    Mi_CANCEL_ACTION_PHASE
				 * @param phase 	the possible phase of this action
				 * @return 	  	true if the given phase equals the
				 *			current phase of this action
				 *------------------------------------------------------*/
	boolean			isPhase(int phase);

				/**------------------------------------------------------
				 * Gets whether this action is interested in actions of
				 * the parts of the MiPart this action is assigned to. Use
				 * Mi_ACTIONS_OF_PARTS_OF_OBSERVED when specifying valid
				 * actions if this behavior is desired.
				 * @return  	 	true if interested
				 *------------------------------------------------------*/
	boolean			isInterestedInActionsOfPartsOfObserved();

				/**------------------------------------------------------
				 * Specifies that this action, in the request phase, is
				 * saying no to the request.
				 *------------------------------------------------------*/
	void			veto();

				/**------------------------------------------------------
				 * Gets whether this action, in the request phase, is
				 * saying no to the request.
				 * @return		true if vetoed
				 *------------------------------------------------------*/
	boolean			isVetoed();

				/**------------------------------------------------------
				 * Sets whether this action, in the request phase, is
				 * saying no to the request.
				 * @param flag		true if vetoed
				 *------------------------------------------------------*/
	void			setVetoed(boolean flag);

				/**------------------------------------------------------
				 * For debug, gets a text string describing the action types
				 * that this action is watching for/handling. The toString()
				 * method returns a string describing the current action type.
				 * @return 		a text string describing the valid
				 *			action types for this action
				 *------------------------------------------------------*/
	String			getValidActionsString();
	}

