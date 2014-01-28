
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

import java.util.Enumeration;

import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.CaselessKeyHashtable;
import com.swfm.mica.util.Strings; 

/*
 * FIX/Optimization: just store the availability state and update 
 * widget sensitivities if/when they are visible (showing)
 */

/**----------------------------------------------------------------------------------------------
 * This class provides a good deal of the implementation of the 
 * MiiCommandManager interface. The classes that use
 * use this, like MiWidgetGroupCommandHandler, delegate calls to
 * their methods to an instance of this class.
 *
 * @see		MiiCommandManager
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiCommandManager implements MiiCommandManager
	{
	private		FastVector	commandWidgets 	= new FastVector();
	private		CaselessKeyHashtable commandWidgetsTable = new CaselessKeyHashtable();


					/**------------------------------------------------------
					 * Constructs a new MiCommandManager.
					 *------------------------------------------------------*/
	public				MiCommandManager()
		{
		}
					/**------------------------------------------------------
					 * Registers the given widget and the command it generates.
					 * This permits (de)sensitization of the widget by the
					 * setCommandSensitivity method. 
					 * @param widget  	the widget that generates the command
					 * @param command 	the command
					 * @see		MiCommandManager#unRegisterWidgetGeneratedCommand
					 * @implements	MiiCommandManager#registerCommandDependentWidget
					 *------------------------------------------------------*/
	public		void		registerCommandDependentWidget(MiPart widget, String command)
		{
		commandWidgets.addElement(new MiWidgetWithCommand(widget, command));
		MiParts parts = (MiParts )commandWidgetsTable.get(command);
		if (parts == null)
			parts = new MiParts();
		parts.addElement(widget);
		commandWidgetsTable.put(command, parts);
		}
					/**------------------------------------------------------
					 * UnRegisters the given widget and the command it generates.
					 * Either the given widget or the given command may be null.
					 * @param widget  	the widget that generates the command
					 * @param command 	the command
					 * @see	MiiCommandManager#registerCommandDependentWidget
					 * @implements	MiiCommandManager#unRegisterCommandDependentWidget
					 *------------------------------------------------------*/
	public		void		unRegisterWidgetGeneratedCommand(MiPart widget, String command)
		{
		for (int i = 0; i < commandWidgets.size(); ++i)
			{
			MiWidgetWithCommand w2c = (MiWidgetWithCommand)commandWidgets.elementAt(i);
			if ((widget != null) && (w2c.widget != widget))
				continue;
			if ((command != null) && (!w2c.command.equalsIgnoreCase(command)))
				continue;

			commandWidgets.removeElementAt(i);
			--i;
			}
		if (command == null)
			{
			Enumeration e = commandWidgetsTable.keys();
			while (e.hasMoreElements())
				{
				String key = (String )e.nextElement();
				MiParts parts = (MiParts )commandWidgetsTable.get(key);
				parts.removeElement(widget);
				}
			}
		else
			{
			MiParts parts = (MiParts )commandWidgetsTable.get(command);
			if (parts != null)
				{
				parts.removeElement(widget);
				}
			}
		}
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
	public		void		setCommandVisibility(String command, boolean flag)
		{
		MiParts parts = getCommandWidgets(command);
		if (parts != null)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				if (parts.elementAt(i).isVisible() != flag)
					parts.elementAt(i).setVisible(flag);
				}
			}
		}
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
					 * @implements		MiiCommandManager#setCommandSensitivity
					 *------------------------------------------------------*/
	public		void		setCommandSensitivity(String command, boolean flag)
		{
		MiParts parts = getCommandWidgets(command);
		if (parts != null)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				if (parts.elementAt(i).isSensitive() != flag)
					{
					parts.elementAt(i).setSensitive(flag);
					}
				}
			}
		}
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
					 * @implements		MiiCommandManager#setCommandSensitivity
					 *------------------------------------------------------*/
	public		void		setCommandSensitivity(
						String command, boolean flag, String statusHelpMsg)
		{
		MiParts parts = getCommandWidgets(command);
		if (parts != null)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				if (parts.elementAt(i).isSensitive() != flag)
					parts.elementAt(i).setSensitive(flag);
				parts.elementAt(i).setStatusHelpMessage(statusHelpMsg);
				}
			}
		}
					/**------------------------------------------------------
					 * Sets the state of all the boolean widget that generates the 
					 * given command. This is used for initializing toggle 
					 * buttons.
					 * @param command  	the command
					 * @param flag 		true if the two state widget is
					 *			to be 'set'.
					 * @implements		MiiCommandManager#setCommandState
					 *------------------------------------------------------*/
	public		void		setCommandState(String command, boolean flag)
		{
		MiParts parts = getCommandWidgets(command);
		if (parts != null)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				if (parts.elementAt(i).isSelected() != flag)
					{
					parts.elementAt(i).select(flag);
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Sets the state all of the multi-state widget that generates
					 * the given command. This is used for initializing widgets 
					 * like combo boxes and lists.
					 * @param command  	the command
					 * @param state		the current state, one of many.
					 * @implements		MiiCommandManager#setCommandState
					 *------------------------------------------------------*/
	public		void		setCommandState(String command, String state)
		{
		MiParts parts = getCommandWidgets(command);
		if (parts != null)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				if (parts.elementAt(i) instanceof MiWidget)
					{
					((MiWidget )parts.elementAt(i)).setValue(state);
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Sets the label of all of the widget that generates the given 
					 * command. This is used for initializing widgets like
					 * labels and menubar buttons, push button etc.
					 * @param command  	the command
					 * @param label		the new label
					 * @implements		MiiCommandManager#setCommandLabel
					 *------------------------------------------------------*/
	public		void		setCommandLabel(String command, String label)
		{
		MiParts parts = getCommandWidgets(command);
		if (parts != null)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				if (parts.elementAt(i) instanceof MiWidget)
					{
					((MiWidget )parts.elementAt(i)).setValue(label);
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Sets the values of the all of the multi-state widgets that 
					 * generate the given command. This is used for setting the 
					 * possible values of widgets like combo boxes and lists.
					 * @param command  	the command
					 * @param options	the new contents of the widget
					 * @implements		MiiCommandManager#setCommandOptions
					 *------------------------------------------------------*/
	public		void		setCommandOptions(String command, Strings options)
		{
		MiParts parts = getCommandWidgets(command);
		if (parts != null)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				if (parts.elementAt(i) instanceof MiWidget)
					{
					((MiWidget )parts.elementAt(i)).setContents(options);
					}
				}
			}
		}
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
	public		void		setCommandPropertyValue(String command, String propertyName, String propertyValue)
		{
		MiParts parts = getCommandWidgets(command);
		if (parts != null)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				parts.elementAt(i).setPropertyValue(propertyName, propertyValue);
				}
			}
		}
					/**------------------------------------------------------
					 * Gets the first widget registered that generates the
					 * (caseless) given command.
					 * @param command  	the command
					 * @return 		the widget generating the given 
					 * 			command or null if not found.
					 *------------------------------------------------------*/
	public		MiPart		getCommandWidget(String command)
		{
		for (int i = 0; i < commandWidgets.size(); ++i)
			{
			MiWidgetWithCommand w2c = (MiWidgetWithCommand)commandWidgets.elementAt(i);
			if (w2c.command.equalsIgnoreCase(command))
				{
				return(w2c.widget);
				}
			}
		return(null);
		}
					/**------------------------------------------------------
					 * Gets all the widgets registered that generates the
					 * (caseless) given command.
					 * @param command  	the command
					 * @return 		the widgets generating the given 
					 * 			command or null if none found.
					 *------------------------------------------------------*/
	public		MiParts		getCommandWidgets(String command)
		{
		return((MiParts )commandWidgetsTable.get(command));
		}
					/**------------------------------------------------------
					 * Returns information about this class.
					 * @return		textual information (class name +
					 *			+ list of commands of registered widgets)
					 *------------------------------------------------------*/
	public		String		toString()
		{
		String s = new String(MiDebug.getMicaClassName(this) 
			+ ": commands generated by managed widgets are: \n");
		for (int i = 0; i < commandWidgets.size(); ++i)
			{
			MiWidgetWithCommand w2c = (MiWidgetWithCommand)commandWidgets.elementAt(i);
			s += w2c.command;
			if (i < commandWidgets.size() - 1)
				s += ", ";
			}
		return(s);
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Framework
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 *----------------------------------------------------------------------------------------------*/
class MiWidgetWithCommand
	{
	public		MiPart		widget;
	public		String		command;

					/**------------------------------------------------------
					 * Constructs a new MiWidgetWithCommand.
					 * @param widget	the widget that generates the command
					 * @param command	the command generated by the widget
					 *------------------------------------------------------*/
					MiWidgetWithCommand(MiPart widget, String command)
		{
		this.widget = widget;
		this.command = command;
		}
	}

