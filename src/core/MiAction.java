
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 
import java.util.BitSet; 
import java.util.Hashtable; 
import java.util.Enumeration; 

/**----------------------------------------------------------------------------------------------
 * An action has a number of valid action types and an action handler.
 * When assigned to a MiPart (see MiPart#appendActionHandler) this action
 * waits for one of it's valid action types to be dispatched to the MiPart.
 * When one of these valid action types occurs, the MiPart calls this
 * action's action handler with this action as the parameter.
 *
 * Action types consist of two parts. The top 4 bits are reserved for
 * the action's phases bits. The bottom 28 bits are reserved for the
 * action's action type, which is not a mask. This approach allows a
 * single action type and any combination of the 4 action phases to
 * be encoded into a sinlge integer (int).
 *
 * For example:
 *	MiiAction	dragAndDropActionEvent = new MiAction(this, 
 *				Mi_DATA_IMPORT_ACTION 
 *				| Mi_REQUEST_ACTION_PHASE
 *				| Mi_EXECUTE_ACTION_PHASE);
 *	
 * This action, when assigned to a MiPart, will have it's action handler
 * called whenever a Mi_DATA_IMPORT_ACTION action in a Mi_REQUEST_ACTION_PHASE
 * or a Mi_EXECUTE_ACTION_PHASE is generated.
 *
 * @see 	MiActionManager
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiAction implements MiiAction, MiiActionTypes, MiiActionNames
	{
						//------------------------------------------------------
						// These BitSets are used to group actions together. The
						// _number_ of each bit set is the value of an action type.
						// For example, a BitSet will have it's 4th bit set if it
						// contins the Mi_COPY_ACTION action type (which is equal
						// to 4)
						//------------------------------------------------------
	private		BitSet			validRequestActionTypes;
	private		BitSet			validExecuteActionTypes;
	private		BitSet			validCommitActionTypes;
	private		BitSet			validCancelActionTypes;
	private		BitSet			validChildRequestActionTypes;
	private		BitSet			validChildExecuteActionTypes;
	private		BitSet			validChildCommitActionTypes;
	private		BitSet			validChildCancelActionTypes;
	private		boolean			vetoed = false;
	private		int			actionType;
	private		MiPart			observed;
	private		MiPart			source;
	private		MiiActionHandler	handler;
	private		Object			userInfo;
	private		Object			systemInfo;
	private		Hashtable		resources;



					/**------------------------------------------------------
	 				 * Constructs a new MiAction.
					 *------------------------------------------------------*/
	protected			MiAction()
		{
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiAction which will dispatch itself
					 * to the given action handler whenever the given action
					 * type occurs in any MiPart this action is assigned to.
					 * @param handler	the action handler for this action
					 * @param validActionType the action type for this action
					 *------------------------------------------------------*/
	public				MiAction(
						MiiActionHandler	handler,
						int			validActionType)
		{
		this(handler, validActionType, 0, 0, null);
		}

					/**------------------------------------------------------
	 				 * Constructs a new MiAction which will dispatch itself
					 * to the given action handler whenever the given action
					 * type occurs in any MiPart this action is assigned to.
					 * @param handler	the action handler for this action
					 * @param validActionType the action type for this action
					 * @param userInfo	the user information for this action
					 *------------------------------------------------------*/
	public				MiAction(
						MiiActionHandler	handler,
						int			validActionType,
						Object			userInfo)
		{
		this(handler, validActionType, 0, 0, userInfo);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiAction which will dispatch itself
					 * to the given action handler whenever the given action
					 * types occur in any MiPart this action is assigned to.
					 * @param handler	the action handler for this action
					 * @param validActionType1 an action type for this action
					 * @param validActionType2 an action type for this action
					 *------------------------------------------------------*/
	public				MiAction(
						MiiActionHandler	handler,
						int			validActionType1,
						int			validActionType2)
		{
		this(handler, validActionType1, validActionType2, 0, null);
		}

					/**------------------------------------------------------
	 				 * Constructs a new MiAction which will dispatch itself
					 * to the given action handler whenever the given action
					 * types occur in any MiPart this action is assigned to.
					 * @param handler	the action handler for this action
					 * @param validActionType1 an action type for this action
					 * @param validActionType2 an action type for this action
					 * @param userInfo	the user information for this action
					 *------------------------------------------------------*/
	public				MiAction(
						MiiActionHandler	handler,
						int			validActionType1,
						int			validActionType2,
						Object			userInfo)
		{
		this(handler, validActionType1, validActionType2, 0, userInfo);
		}

					/**------------------------------------------------------
	 				 * Constructs a new MiAction which will dispatch itself
					 * to the given action handler whenever the given action
					 * types occur in any MiPart this action is assigned to.
					 * @param handler	the action handler for this action
					 * @param validActionType1 an action type for this action
					 * @param validActionType2 an action type for this action
					 * @param validActionType3 an action type for this action
					 *------------------------------------------------------*/
	public				MiAction(
						MiiActionHandler	handler,
						int			validActionType1,
						int			validActionType2,
						int			validActionType3)
		{
		this(handler, validActionType1, validActionType2, validActionType3, null);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiAction which will dispatch itself
					 * to the given action handler whenever the given action
					 * types occur in any MiPart this action is assigned to.
					 * @param handler	the action handler for this action
					 * @param validActionType1 an action type for this action
					 * @param validActionType2 an action type for this action
					 * @param validActionType3 an action type for this action
					 * @param userInfo	the user information for this action
					 *------------------------------------------------------*/
	public				MiAction(
						MiiActionHandler	handler,
						int			validActionType1,
						int			validActionType2,
						int			validActionType3,
						Object			userInfo)
		{
		recordBitOfValidActionType(validActionType1);
		recordBitOfValidActionType(validActionType2);
		recordBitOfValidActionType(validActionType3);
		actionType 		= validActionType1;
		this.userInfo		= userInfo;
		this.handler		= handler;
		}

					/**------------------------------------------------------
	 				 * Constructs a new MiAction which will dispatch itself
					 * to the given action handler whenever the given action
					 * types occur in any MiPart this action is assigned to.
					 * @param handler	the action handler for this action
					 * @param validActionTypes the action types for this action
					 * @param userInfo	the user information for this action
					 *------------------------------------------------------*/
	public				MiAction(
							MiiActionHandler	handler,
							int[]			validActionTypes,
							Object			userInfo)
		{
		for (int i = 0; i < validActionTypes.length; ++i)
			{
			recordBitOfValidActionType(validActionTypes[i]);
			}

		if (validActionTypes.length > 0)
			actionType = validActionTypes[0];

		this.userInfo		= userInfo;
		this.handler		= handler;
		}
					/**------------------------------------------------------
	 				 * Adds the valid action types of the given action to the
					 * valid action types of this action if they are requested
					 * from the parts of the observed MiPart.
					 * @param actions	the action whose valid action types
					 *			to add to the valid action types of
					 *			this action
					 *------------------------------------------------------*/
	public		void		addValidActionsRequestedFromPartsOfObserved(MiiAction actions)
		{
		if (!(actions instanceof MiAction))
			return;

		MiAction action = (MiAction )actions;
		if (action.validChildRequestActionTypes != null)
			{
			if (validChildRequestActionTypes == null)
				{
				validChildRequestActionTypes = new BitSet(
					MiActionManager.getNumberOfRegisteredActions());
				}
			validChildRequestActionTypes.or(action.validChildRequestActionTypes);
			}

		if (action.validChildExecuteActionTypes != null)
			{
			if (validChildExecuteActionTypes == null)
				{
				validChildExecuteActionTypes = new BitSet(
					MiActionManager.getNumberOfRegisteredActions());
				}
			validChildExecuteActionTypes.or(action.validChildExecuteActionTypes);
			}

		if (action.validChildCommitActionTypes != null)
			{
			if (validChildCommitActionTypes == null)
				{
				validChildCommitActionTypes = new BitSet(
					MiActionManager.getNumberOfRegisteredActions());
				}
			validChildCommitActionTypes.or(action.validChildCommitActionTypes);
			}

		if (action.validChildCancelActionTypes != null)
			{
			if (validChildCancelActionTypes == null)
				{
				validChildCancelActionTypes = new BitSet(
					MiActionManager.getNumberOfRegisteredActions());
				}
			validChildCancelActionTypes.or(action.validChildCancelActionTypes);
			}
		}
					/**------------------------------------------------------
	 				 * Adds the valid action types of the given action to the
					 * valid action types of this action.
					 * @param actions	the action whose valid action types
					 *			to add to the valid action types of
					 *			this action
					 *------------------------------------------------------*/
	public		void		addValidActions(MiiAction actions)
		{
		if (!(actions instanceof MiAction))
			return;

		addValidActionsRequestedFromPartsOfObserved(actions);

		MiAction action = (MiAction )actions;

		if (action.validRequestActionTypes != null)
			{
			if (validRequestActionTypes == null)
				{
				validRequestActionTypes = new BitSet(
					MiActionManager.getNumberOfRegisteredActions());
				}
			validRequestActionTypes.or(action.validRequestActionTypes);
			}

		if (action.validExecuteActionTypes != null)
			{
			if (validExecuteActionTypes == null)
				{
				validExecuteActionTypes = new BitSet(
					MiActionManager.getNumberOfRegisteredActions());
				}
			validExecuteActionTypes.or(action.validExecuteActionTypes);
			}

		if (action.validCommitActionTypes != null)
			{
			if (validCommitActionTypes == null)
				{
				validCommitActionTypes = new BitSet(
					MiActionManager.getNumberOfRegisteredActions());
				}
			validCommitActionTypes.or(action.validCommitActionTypes);
			}

		if (action.validCancelActionTypes != null)
			{
			if (validCancelActionTypes == null)
				{
				validCancelActionTypes = new BitSet(
					MiActionManager.getNumberOfRegisteredActions());
				}
			validCancelActionTypes.or(action.validCancelActionTypes);
			}
		}
					/**------------------------------------------------------
	 				 * Adds the given action type to the valid action types 
					 * of this action.
					 * @param actionType	the action type to the valid action
					 *			types of this action
					 * @exception		IllegalArgumentException if unknown
					 *			action type
					 *------------------------------------------------------*/
	protected	void		recordBitOfValidActionType(int actionType)
		{
		BitSet bits;
		if (actionType == 0)
			return;

		// numBitsSet
		if ((actionType & Mi_ACTION_TYPE_MASK) > MiActionManager.getNumberOfRegisteredActions())
			{
			throw new IllegalArgumentException(
				this + ": Unknown action specified in action type");
			}
				

		int phase = actionType & Mi_ACTION_PHASE_MASK;
		int type = actionType & Mi_ACTION_TYPE_MASK;
		if ((actionType & Mi_ACTIONS_OF_PARTS_OF_OBSERVED) != 0)
			{
			if ((phase == 0) || ((phase & Mi_COMMIT_ACTION_PHASE) != 0))
				{
				if (validChildCommitActionTypes == null)
					{
					validChildCommitActionTypes 
						= new BitSet(MiActionManager.getNumberOfRegisteredActions());
					}
				validChildCommitActionTypes.set(type);
				}
			if ((phase & Mi_REQUEST_ACTION_PHASE) != 0)
				{
				if (validChildRequestActionTypes == null)
					{
					validChildRequestActionTypes 
						= new BitSet(MiActionManager.getNumberOfRegisteredActions());
					}
				validChildRequestActionTypes.set(type);
				}
			if ((phase & Mi_EXECUTE_ACTION_PHASE) != 0)
				{
				if (validChildExecuteActionTypes == null)
					{
					validChildExecuteActionTypes 
						= new BitSet(MiActionManager.getNumberOfRegisteredActions());
					}
				validChildExecuteActionTypes.set(type);
				}
			if ((phase & Mi_CANCEL_ACTION_PHASE) != 0)
				{
				if (validChildCancelActionTypes == null)
					{
					validChildCancelActionTypes 
						= new BitSet(MiActionManager.getNumberOfRegisteredActions());
					}
				validChildCancelActionTypes.set(type);
				}
			}
		if (((actionType & Mi_ACTIONS_OF_PARTS_OF_OBSERVED) == 0)
			|| ((actionType & Mi_ACTIONS_OF_OBSERVED) != 0))
			{
			if ((phase == 0) || ((phase & Mi_COMMIT_ACTION_PHASE) != 0))
				{
				if (validCommitActionTypes == null)
					{
					validCommitActionTypes 
						= new BitSet(MiActionManager.getNumberOfRegisteredActions());
					}
				validCommitActionTypes.set(type);
				}
			if ((phase & Mi_REQUEST_ACTION_PHASE) != 0)
				{
				if (validRequestActionTypes == null)
					{
					validRequestActionTypes 
						= new BitSet(MiActionManager.getNumberOfRegisteredActions());
					}
				validRequestActionTypes.set(type);
				}
			if ((phase & Mi_EXECUTE_ACTION_PHASE) != 0)
				{
				if (validExecuteActionTypes == null)
					{
					validExecuteActionTypes 
						= new BitSet(MiActionManager.getNumberOfRegisteredActions());
					}
				validExecuteActionTypes.set(type);
				}
			if ((phase & Mi_CANCEL_ACTION_PHASE) != 0)
				{
				if (validCancelActionTypes == null)
					{
					validCancelActionTypes 
						= new BitSet(MiActionManager.getNumberOfRegisteredActions());
					}
				validCancelActionTypes.set(type);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Processes the given action (probably _this_ MiAction)
					 * Default behavior is to call this MiAction's registered 
					 * action handler. This is not currently used by Mica.
					 * @param action	the action to process
					 * @return 		true if it is OK to send
					 *			action to the next action handler
					 * 			false if it is NOT OK to send
					 *			action to the next action handler
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		return(getActionHandler().processAction(action));
		}

	//***************************************************************************************
	// Implementation of MiiAction
	//***************************************************************************************

					/**------------------------------------------------------
					 * Sets the MiPart that originally generated the action.
					 * This is set automatically by Mica whenever the action
					 * is dispatched.
					 * @param source	the part that generated the action
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		void		setActionSource(MiPart source)
		{
		this.source = source;
		}
					/**------------------------------------------------------
					 * Gets the MiPart that originally generated the action.
					 * @return 		the part that generated the action
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		MiPart		getActionSource()
		{
		return(source);
		}
					/**------------------------------------------------------
					 * Sets the MiPart that dispatched the action to the 
					 * MiiActionHandler.
					 * This is set automatically by Mica whenever the action
					 * is dispatched.
					 * @param source 	the part that dispatched the action
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		void		setObservedObject(MiPart observed)
		{
		this.observed = observed;
		}
					/**------------------------------------------------------
					 * Gets the MiPart that dispatched the action to the 
					 * MiiActionHandler.
					 * @return 	 	the part that dispatched the action
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		MiPart		getObservedObject()
		{
		return(observed);
		}
					/**------------------------------------------------------
					 * Gets whether this action is interested in actions of
					 * the parts of the MiPart this action is assigned to. Use
					 * Mi_ACTIONS_OF_PARTS_OF_OBSERVED when specifying valid
					 * actions if this behavior is desired.
					 * @return  	 	true if interested
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		boolean		isInterestedInActionsOfPartsOfObserved()
		{
		return((validChildRequestActionTypes != null)
			|| (validChildExecuteActionTypes != null)
			|| (validChildCommitActionTypes != null)
			|| (validChildCancelActionTypes != null));
		}
					/**------------------------------------------------------
					 * Sets the action handler this action will be dispatched
					 * to.
					 * @param handler 	the action handler to send this
					 *			action to
					 *------------------------------------------------------*/
	public		void		setActionHandler(MiiActionHandler handler)
		{
		this.handler = handler;
		}
					/**------------------------------------------------------
					 * Gets the action handler this action will be dispatched
					 * to.
					 * @return 	 	the action handler to send this
					 *			action to
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		MiiActionHandler getActionHandler()
		{
		return(handler);
		}
					/**------------------------------------------------------
					 * Sets the actual type of this dispatched action. This 
					 * type is one of the valid types of this action.
					 * MiiActionHandler.
					 * This is set automatically by Mica whenever the action
					 * is dispatched.
					 * @return 	 	the part that dispatched the action
					 * @see 	 	#isValidActionType
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		void		setActionType(int actionType)
		{
		this.actionType = actionType;
		}
					/**------------------------------------------------------
					 * Gets the actual type of this dispatched action. Includes
					 * action and phase information.
					 * @return 	 	the action type of this action
					 * @see 	 	#isValidActionType
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		int		getActionType()
		{
		return(actionType);
		}
					/**------------------------------------------------------
					 * Gets whether the actual type of this dispatched action
					 * is the same as the given type. Includes action and phase
					 * information.
					 * @return 	 	true if the given action type is
					 *			the same action type this action
					 *			has.
					 * @see 	 	#getActionType
					 * @see 	 	#isValidActionType
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		boolean		hasActionType(int actionType)
		{
		if (this.actionType == actionType)
			return(true);

		if ((actionType & Mi_ACTION_PHASE_MASK) != 0)
			return(false);

		return(this.actionType == (actionType | Mi_COMMIT_ACTION_PHASE));
		}
					/**------------------------------------------------------
					 * Sets the user information field of this action.
					 * @param info 	 	the information supplied by the
					 *			programmer.
					 * @see 	 	#getActionUserInfo
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		void		setActionUserInfo(Object info)
		{
		userInfo = info;
		}
					/**------------------------------------------------------
					 * Gets the user information field of this action.
					 * @return  	 	the information supplied by the
					 *			programmer.
					 * @see 	 	#setActionUserInfo
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		Object		getActionUserInfo()
		{
		return(userInfo);
		}
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
	public		void		setActionSystemInfo(Object info)
		{
		systemInfo = info;
		}
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
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		Object		getActionSystemInfo()
		{
		return(systemInfo);
		}
					/**------------------------------------------------------
					 * Gets whether given action type is an action type for
					 * which this action will be dispatched to it's action
					 * handler.
					 * @return 	 	true if the given action type is
					 *			a valid action type or this action
					 * @see 	 	#isValidActionType
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public 		boolean		isValidActionType(int actionType)
		{
		BitSet bits;
		boolean actionsOfPartsAreValid = ((actionType & Mi_ACTIONS_OF_PARTS_OF_OBSERVED) != 0);
		switch (actionType & Mi_ACTION_PHASE_MASK)
			{
			case 0:
			case Mi_COMMIT_ACTION_PHASE:
			default:
				if (actionsOfPartsAreValid)
					bits = validChildCommitActionTypes;
				else
					bits = validCommitActionTypes;
				break;

			case Mi_REQUEST_ACTION_PHASE:
				if (actionsOfPartsAreValid)
					bits = validChildRequestActionTypes;
				else
					bits = validRequestActionTypes;
				break;

			case Mi_EXECUTE_ACTION_PHASE:
				if (actionsOfPartsAreValid)
					bits = validChildExecuteActionTypes;
				else
					bits = validExecuteActionTypes;
				break;

			case Mi_CANCEL_ACTION_PHASE:
				if (actionsOfPartsAreValid)
					bits = validChildCancelActionTypes;
				else
					bits = validCancelActionTypes;
				break;
			}
		if ((bits != null) && bits.get(actionType & Mi_ACTION_TYPE_MASK))
			return(true);
		return(false);
		}

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
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		boolean		isPhase(int phase)
		{
		return((actionType & phase) != 0);
		}

					/**------------------------------------------------------
					 * Gets the phase of this dispatched action. Possible values
					 * are:
					 *    Mi_REQUEST_ACTION_PHASE
					 *    Mi_CANCEL_ACTION_PHASE
					 *    Mi_COMMIT_ACTION_PHASE
					 *    Mi_CANCEL_ACTION_PHASE
					 * @return 	  	the phase of this action
					 * @see 	 	#setResource
					 *------------------------------------------------------*/
	public		int		getPhase()
		{
		return(actionType & Mi_ACTION_PHASE_MASK);
		}

					/**------------------------------------------------------
					 * Specifies that this action, in the request phase, is
					 * saying no to the request.
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		void		veto()
		{
		vetoed = true;
		}
					/**------------------------------------------------------
					 * Gets whether this action, in the request phase, is
					 * saying no to the request.
					 * @return		true if vetoed
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		boolean		isVetoed()
		{
		return(vetoed);
		}
					/**------------------------------------------------------
					 * Sets whether this action, in the request phase, is
					 * saying no to the request.
					 * @param flag		true if vetoed
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		void		setVetoed(boolean flag)
		{
		vetoed = flag;
		}
					/**------------------------------------------------------
					 * Adds (if necessary) and sets the given named resource
					 * to the given value. Every action can have an unlimited
					 * number of programmer-specified resources. 
					 * @param name  	the resource name
					 * @param value  	the resource value (non-null)
					 * @see 	 	#getResource
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		void		setResource(String name, Object value)
		{
		if (resources == null)
			resources = new Hashtable(5);
		if (value == null)
			removeResource(name);
		else
			resources.put(name, value);
		}
					/**------------------------------------------------------
					 * Gets the value of the given named resource. If
					 * to the given value. Every action can have an unlimited
					 * number of programmer-specified resources. 
					 * @param name  	the resource name
					 * @param value  	the resource value
					 * @see 	 	#setResource
					 * @implements 	 	MiiAction
					 *------------------------------------------------------*/
	public		Object		getResource(String name)
		{
		if (resources == null)
			return(null);
		return(resources.get(name));
		}
					/**------------------------------------------------------
	 				 * Removes the named resource. This is the same as setting
					 * the resource's value to null.
	 				 * @param name 		the name of the resource
					 * @see			#setResource
					 * @see			#getResource
					 *------------------------------------------------------*/
	public		void		removeResource(String name)
		{
		if (resources != null)
			resources.remove(name);
		}

					/**------------------------------------------------------
	 				 * Gets the number of resources assigned to this MiPart. 
	 				 * @return 		the number of resources
					 * @see			#getResourceName
					 *------------------------------------------------------*/
	public		int		getNumberOfResources()
		{
		if (resources != null)
			return(resources.size());
		return(0);
		}

					/**------------------------------------------------------
	 				 * Gets the name of the ith resource assigned to this 
					 * MiPart. 
	 				 * @param index		the index of the resource
	 				 * @return 		the name of the resource
					 * @exception		IllegalArgumentException If index
					 * 			is out of range.
					 * @see			#getNumberOfResources
					 *------------------------------------------------------*/
	public		String		getResourceName(int index)
		{
		Enumeration keys = resources.keys();
		int i = 0;
		while (keys.hasMoreElements())
			{
			Object obj = keys.nextElement();
			if (i == index)
				return((String )obj);
			++i;
			}
		throw new IllegalArgumentException(this + ": Resource index: " 
			+ index + " > number of resources: " + resources.size());
		}
					/**------------------------------------------------------
	 				 * Gets the textual description of the given action type.
					 * This describes both the phase and the type of the given
					 * action type.
	 				 * @param actionType	the action to describe
	 				 * @return 		the description
					 * @see			#getActionSpec
					 * @see			#getActionPhasesString
					 * @see			#getActionTypesString
					 *------------------------------------------------------*/
	public static 	String		getActionString(int actionType)
		{
		String actionPhases = getActionPhasesString(actionType & Mi_ACTION_PHASE_MASK);
		actionPhases = "[Phase(s): " + actionPhases + "]";
		Strings strings = getActionTypesString(actionPhases, actionType);
		return(strings.getLineFeedDelimitedLines());
		}
					/**------------------------------------------------------
	 				 * Gets the textual description of the given action type
					 * suitable for parsing later into the given type.
					 * action type.
	 				 * @param actionType	the action to describe
	 				 * @return 		the description
					 * @see			#getActionString
					 *------------------------------------------------------*/
	public static 	String		getActionSpec(int actionType)
		{
		String str = getActionPhasesString(actionType & Mi_ACTION_PHASE_MASK);
		int type = actionType & Mi_ACTION_TYPE_MASK; 
		if ((actionType & Mi_ACTIONS_OF_PARTS_OF_OBSERVED) != 0)
			{
			if ((actionType & Mi_ACTIONS_OF_OBSERVED) != 0)
				str += "+" + Mi_ACTIONS_OF_OBSERVED_NAME;
			else
				str += "+" + Mi_ACTIONS_OF_PARTS_OF_OBSERVED_NAME;
			}
		if (type == Mi_ALL_ACTION_TYPES)
			{
			str += "+" + Mi_ALL_ACTION_TYPES_NAME;
			return(str);
			}

		str += "+" + MiActionManager.getNameOfRegisteredAction(type);
		return(str);
		}
					/**------------------------------------------------------
	 				 * Gets the textual description of the types of the given
					 * action type.
	 				 * @param actionPhases	the description of the phases of
					 *			the action which may be enhanced
					 *			and which is returned by this method.
	 				 * @param actionType	the action to describe
	 				 * @return 		the multiline description
					 * @see			#getActionString
					 * @see			#getActionPhasesString
					 *------------------------------------------------------*/
	protected static Strings	getActionTypesString(String actionPhases, int actionType)
		{
		Strings strs = new Strings();
		int type 	= actionType & Mi_ACTION_TYPE_MASK; 

		if ((actionType & Mi_ACTIONS_OF_PARTS_OF_OBSERVED) != 0)
			{
			if ((actionType & Mi_ACTIONS_OF_OBSERVED) != 0)
				actionPhases = "*Listening to target and parts* " + actionPhases;
			else
				actionPhases = "*Listening only to parts of target* " + actionPhases;
			}
		else
			{
			actionPhases = "*Listening only to target* " + actionPhases;
			}

		if (type == Mi_ALL_ACTION_TYPES)
			{
			strs.addElement(actionPhases + "<Type(s): All actions>");
			return(strs);
			}

		strs.addElement(actionPhases + MiActionManager.getNameOfRegisteredAction(type));
		return(strs);
		}
					/**------------------------------------------------------
	 				 * Gets the textual description of the phases of the given
					 * action type.
	 				 * @param phases	the action phases to describe
	 				 * @return 		the description
					 * @see			#getActionString
					 * @see			#getActionTypesString
					 *------------------------------------------------------*/
	protected static String		getActionPhasesString(int phases)
		{
		String str = new String();
		if (((phases & Mi_COMMIT_ACTION_PHASE) != 0) || (phases == 0))
			{
			str += MiiActionNames.actionPhaseNames[0];
			}
		if ((phases & Mi_REQUEST_ACTION_PHASE) != 0)
			{
			if (str.length() > 0)
				str += "+";
			str += MiiActionNames.actionPhaseNames[1];
			}
		if ((phases & Mi_EXECUTE_ACTION_PHASE) != 0)
			{
			if (str.length() > 0)
				str += "+";
			str += MiiActionNames.actionPhaseNames[2];
			}
		if ((phases & Mi_CANCEL_ACTION_PHASE) != 0)
			{
			if (str.length() > 0)
				str += "+";
			str += MiiActionNames.actionPhaseNames[3];
			}
		return(str);
		}
					/**------------------------------------------------------
	 				 * Gets the textual description of action type of this
					 * action. This is only meaningful while this action is
					 * being dispatched. 
	 				 * @return 		the description
					 * @see			#getActionString
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(getActionString(actionType));
		}
					/**------------------------------------------------------
	 				 * Gets the action corresponding to the given textual description.
	 				 * @param spec		the description
	 				 * @return 		the action type
	 				 * @exception 		IllegalArgumentException
					 * @see			#getActionString
					 *------------------------------------------------------*/
	public static	int		typeFromString(String spec)
		{
		int type = 0;
		for (int i = 0; i < actionBitMasks.length; ++i)
			{
			int index = spec.indexOf(actionBitMaskNames[i]);
			if (index != -1)
				{
				spec = spec.substring(0, index) + spec.substring(
					index + actionBitMaskNames[i].length());
				spec = spec.trim();
				if (spec.startsWith("+") || spec.startsWith("|"))
					spec = spec.substring(1);
				if (spec.endsWith("+") || spec.endsWith("|"))
					spec = spec.substring(0, spec.length() - 1);

				type |= actionBitMasks[i];
				}
			}
		
		spec = Utility.replaceAll(spec, " ", "");
		for (int i = 0; i < MiiActionNames.actionNames.length; ++i)
			{
			String name = MiiActionNames.actionNames[i];
			name = Utility.replaceAll(name, " ", "");

			if (name.equalsIgnoreCase(spec))
				return(type + i);
			}
		throw new IllegalArgumentException("MiAction: " 
			+ "Unable to process action specification: " + spec); 
		}
					/**------------------------------------------------------
					 * For debug, gets a text string describing the action types
					 * that this action is watching for/handling. The toString()
					 * method returns a string describing the current action type.
					 * @return 		a text string describing the valid
					 *			action types for this action
					 *------------------------------------------------------*/
	public		String		getValidActionsString()
		{
		String str;
		Strings strings = new Strings();
		if (validRequestActionTypes != null)
			strings.append(getTypesFromBits(validRequestActionTypes, Mi_REQUEST_ACTION_PHASE));
		if (validExecuteActionTypes != null)
			strings.append(getTypesFromBits(validExecuteActionTypes, Mi_EXECUTE_ACTION_PHASE));
		if (validCommitActionTypes != null)
			strings.append(getTypesFromBits(validCommitActionTypes, Mi_COMMIT_ACTION_PHASE));
		if (validCancelActionTypes != null)
			strings.append(getTypesFromBits(validCancelActionTypes, Mi_CANCEL_ACTION_PHASE));

		if (validChildRequestActionTypes != null)
			{
			strings.append(getTypesFromBits(validChildRequestActionTypes, 
				Mi_ACTIONS_OF_PARTS_OF_OBSERVED | Mi_REQUEST_ACTION_PHASE));
			}
		if (validChildExecuteActionTypes != null)
			{
			strings.append(getTypesFromBits(validChildExecuteActionTypes, 
				Mi_ACTIONS_OF_PARTS_OF_OBSERVED | Mi_EXECUTE_ACTION_PHASE));
			}
		if (validChildCommitActionTypes != null)
			{
			strings.append(getTypesFromBits(validChildCommitActionTypes, 
				Mi_ACTIONS_OF_PARTS_OF_OBSERVED | Mi_COMMIT_ACTION_PHASE));
			}
		if (validChildCancelActionTypes != null)
			{
			strings.append(getTypesFromBits(validChildCancelActionTypes, 
				Mi_ACTIONS_OF_PARTS_OF_OBSERVED | Mi_CANCEL_ACTION_PHASE));
			}

		str = strings.getLineFeedDelimitedLines();
		return(str);
		}

					/**------------------------------------------------------
					 * Used by getValidActionsString(). Examines the given BitSet
					 * and generates a new line of text for each bit set (each 
					 * which represents an action type).
					 * @param bits 		a BitSet whose bits represent action
					 *			types.
					 * @param phaseMask 	the phase associated with all of the
					 *			action types specified by the given
					 *			BitSet.
					 * @return 		a list of strings describing the action 
					 *			types in the given BitSet
					 * @see			#getValidActionsString
					 *------------------------------------------------------*/
	private		Strings		getTypesFromBits(BitSet bits, int phaseMask)
		{
		Strings str = new Strings();
		for (int i = 0; i < bits.size(); ++i)
			{
			if (bits.get(i))
				{
				str.addElement(getActionString(phaseMask | i));
				}
			}
		return(str);
		}
	}

