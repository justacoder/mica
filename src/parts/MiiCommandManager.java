
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

/**----------------------------------------------------------------------------------------------
 * This class supports the registration of widgets and the commands
 * they generate. This can then be used by a program to set values
 * and states of 'commands', instead of 'widgets'. This is usefull
 * because many programs have no knowledge of what widgets generate
 * what commands, nor should they. For example:
 *
 *	setCommandSensitivity(String command, boolean flag);
 *
 * sets the sensitivity of the widgets that generate the command.
 * Many classes that implement this interface just delegate (forward)
 * the methods that implement the interface to an instance of
 * MiCommandManager.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiCommandManager
	{
			/**------------------------------------------------------
			 * Registers the given widget and the command it generates.
			 * This permits (de)sensitization of the widget by the
			 * setCommandSensitivity method. 
			 * @param widget  	the widget that generates the command
			 * @param command 	the command
			 * @see			#unRegisterWidgetGeneratedCommand
			 *------------------------------------------------------*/
	void		registerCommandDependentWidget(MiPart widget, String command);


			/**------------------------------------------------------
			 * UnRegisters the given widget and the command it generates.
			 * Either the given widget or the given command may be null.
			 * @param widget  	the widget that generates the command
			 * @param command 	the command
			 * @see			#registerCommandDependentWidget
			 *------------------------------------------------------*/
	void		unRegisterWidgetGeneratedCommand(MiPart widget, String command);


			/**------------------------------------------------------
			 * Sets whether the given command is to be visible to the
			 * user. This method typically (hides)shows the widgets 
			 * that have been associated with the command.
			 * @param command  	the command
			 * @param flag 		true if the user can now see the
			 *			command (whether sensitive or not)
			 * @see			#registerCommandDependentWidget
			 * @implements		
			 *	MiiCommandManager#setCommandVisibility
			 *------------------------------------------------------*/
	void		setCommandVisibility(String command, boolean flag);

			/**------------------------------------------------------
			 * Sets whether the given command can be processed at this
			 * time. Otherwise, possibly based on the current state of
			 * the application, the given command cannot/should not be
			 * processed at this time. This method typically (de)sensitizes
			 * the widgets that have been associated with the command
			 * (see registerCommandDependentWidget).
			 * @param command  	the command
			 * @param flag 		true if the system can now process
			 *			the command
			 * @see			#registerCommandDependentWidget
			 *------------------------------------------------------*/
	void		setCommandSensitivity(String command, boolean flag);


			/**------------------------------------------------------
			 * Sets whether the given command can be processed at this
			 * time. Otherwise, possibly based on the current state of
			 * the application, the given command cannot/should not be
			 * processed at this time. This method typically (de)sensitizes
			 * the widgets that have been associated with the command
			 * (see registerCommandDependentWidget). This method is designed
			 * to display a status bar message that describes why a 
			 * particular widget is desensitized.
			 * @param command  	the command
			 * @param flag 		true if the system can now process
			 *			the command
			 * @param statusHelpMsg the new status bar message
			 * @see			#registerCommandDependentWidget
			 * @see	MiiCommandManager#setCommandSensitivity
			 * @see	MiCommandManager#setCommandSensitivity
			 *------------------------------------------------------*/
	void		setCommandSensitivity(String command, boolean flag, String statusHelpMsg);


			/**------------------------------------------------------
			 * Sets the state of the boolean widget that generates the 
			 * given command. This is used for initializing toggle 
			 * buttons.
			 * @param command  	the command
			 * @param flag 		true if the two state widget is
			 *			to be 'set'.
			 *------------------------------------------------------*/
	void		setCommandState(String command, boolean flag);


			/**------------------------------------------------------
			 * Sets the state of the multi-state widget that generates
			 * the given command. This is used for initializing widgets 
			 * like combo boxes and lists.
			 * @param command  	the command
			 * @param state		the current state, one of many.
			 *------------------------------------------------------*/
	void		setCommandState(String command, String state);


			/**------------------------------------------------------
			 * Sets the label of the widget that generates the given 
			 * command. This is used for initializing widgets like
			 * labels and menubar buttons, push button etc.
			 * @param command  	the command
			 * @param label		the new label
			 *------------------------------------------------------*/
	void		setCommandLabel(String command, String label);


			/**------------------------------------------------------
			 * Sets the values of the multi-state widget that generates
			 * the given command. This is used for setting the possible
			 * values of widgets like combo boxes and lists.
			 * @param command  	the command
			 * @param options	the new contents of the widget
			 *------------------------------------------------------*/
	void		setCommandOptions(String command, Strings options);

			/**------------------------------------------------------
			 * Sets the given property to the given value for all parts
			 * that generates the given command. This is used for, say,
			 * changing the status help message on a widget that may
			 * only be situationally and temporarily desensitized.
			 * @param command  	the command
			 * @param propertyName	the property name
			 * @param propertyValue	the value
			 * @implements		MiiCommandManager#setCommandState
			 *------------------------------------------------------*/
	void		setCommandPropertyValue(String command, String propertyName, String propertyValue);
	}


