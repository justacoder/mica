
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
import java.util.Enumeration; 
import java.util.Hashtable; 

/**----------------------------------------------------------------------------------------------
 * This class is a globally accessible (i.e. it is a singleton)
 * API to the valid action registry. All possible action types
 * and their names are stored here.
 *
 * This manager allows components outside of Mica to generate actions
 * that look and behave just like Mica's built-in actions. 
 * This is accomplished by a component registering it's unique action
 * names with this manager and getting back unique action type values.
 * These values can be used anywhere just like Mica action types (e.g.
 * Mi_COPY_ACTION).
 *
 * For example: the Mica-supplied MiPlayerPanel class contains the
 * following lines which allow the users of the class to access and
 * use it's action type (Mi_PLAYER_PANEL_ACTION) just like a Mica
 * built-in action types: 
 *
 *	public static final String	Mi_PLAYER_PANEL_ACTION_NAME
 *		= "playerPanelStateChange";
 *	public static final int		Mi_PLAYER_PANEL_ACTION	
 *		= MiActionManager.registerAction(Mi_PLAYER_PANEL_ACTION_NAME);
 *
 * @see		MiAction
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiActionManager implements MiiActionTypes
	{
	private	static	Hashtable	registeredActions 		= new Hashtable();
	private	static	int		numberOfRegisteredActions;

	static
		{
		// ---------------------------------------------------------------
		// Register all of the built-in action types first...
		// ---------------------------------------------------------------
		for (int i = 0; i < MiiActionNames.actionNames.length; ++i)
			{
			registerAction(MiiActionNames.actionNames[i], i);
			}
		}
					/**------------------------------------------------------
	 				 * Registers the given action name and type.
					 * @param actionName	the name of the action
					 * @param actionType	the type of the action
					 * @exception 		IllegalArgumentException action
					 *			name already exists
					 *------------------------------------------------------*/
	protected static void		registerAction(String actionName, int actionType)
		{
		Integer actionTypeInteger = (Integer )registeredActions.get(actionName);
		if (actionTypeInteger != null)
			{
			throw new IllegalArgumentException(
				"MiActionManager: Cannot register already existing action: " + actionName);
			}
		if (numberOfRegisteredActions < actionType)
			numberOfRegisteredActions = actionType;
		registeredActions.put(actionName, new Integer(actionType));
		}
					/**------------------------------------------------------
	 				 * Registers the given action name and returns an action
					 * type value that can be used for this new action.
					 * @param actionName	the name of the action
					 * @return 		the type of the action
					 * @exception 		IllegalArgumentException action
					 *			name already exists
					 *------------------------------------------------------*/
	public static	int		registerAction(String actionName)
		{
		Integer actionType = (Integer )registeredActions.get(actionName);
		if (actionType != null)
			{
			throw new IllegalArgumentException(
				"MiActionManager: Cannot register already existing action: " + actionName);
			}
		numberOfRegisteredActions = registeredActions.size() + 1;
		registeredActions.put(actionName, new Integer(numberOfRegisteredActions));
		return(numberOfRegisteredActions);
		}
					/**------------------------------------------------------
	 				 * Gets the number of registered actions, which is the
					 * total number of all valid actions in the system.
					 * @return 		the number of registered actions
					 *------------------------------------------------------*/
	public static	int		getNumberOfRegisteredActions()
		{
		return(numberOfRegisteredActions);
		}
					/**------------------------------------------------------
	 				 * Gets the names of all registered actions.
					 * @return 		the names of all registered actions
					 *------------------------------------------------------*/
	public static	String[]	getNamesOfRegisteredActions()
		{
		Strings strings = new Strings(MiiActionNames.actionNames);
		Enumeration e = registeredActions.keys();
		while (e.hasMoreElements())
			{
			strings.addElement((String )e.nextElement());
			}
		return(strings.toArray());
		}
					/**------------------------------------------------------
	 				 * Gets the name of the given registered action type.
					 * @return 		the name of the action type or
					 *			null if the action type has not
					 *			been registered.
					 *------------------------------------------------------*/
	public static	String		getNameOfRegisteredAction(int actionType)
		{
		actionType &= Mi_ACTION_TYPE_MASK;
		Enumeration e = registeredActions.keys();
		while (e.hasMoreElements())
			{
			String key = (String )e.nextElement();
			if (((Integer )(registeredActions.get(key))).intValue() == actionType)
				return(key);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the action type of the given registered action name.
					 * @return 		the action type of the action name
					 *			or -1 if the action name has not
					 *			been registered.
					 *------------------------------------------------------*/
	public static	int		getTypeOfRegisteredAction(String actionName)
		{
		Integer actionType = (Integer )registeredActions.get(actionName);
		if (actionType != null)
			return(actionType.intValue());
		return(-1);
		}
	}



