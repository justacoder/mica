
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
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiToolAndMenuItemDefinition
	{
	public		String		displayName;
	public		String		iconName;
	public		String		commandName;
	public		String		statusHelpMsg;
	public		String		disabledStatusHelpMsg;
	public		String		balloonHelpMsg;
	public		MiSize		customSize;

	public				MiToolAndMenuItemDefinition()
		{
		}
	public				MiToolAndMenuItemDefinition
						(
						String		displayName,
						String		iconName,
						String		commandName,
						String		statusHelpMsg,
						String		disabledStatusHelpMsg,
						String		balloonHelpMsg
						)
		{
		this(
			displayName,
			iconName,
			commandName,
			statusHelpMsg,
			disabledStatusHelpMsg,
			balloonHelpMsg,
			null);
		}
	public				MiToolAndMenuItemDefinition
						(
						String		displayName,
						String		iconName,
						String		commandName,
						String		statusHelpMsg,
						String		disabledStatusHelpMsg,
						String		balloonHelpMsg,
						MiSize		customSize
						)
		{
		this.displayName		= displayName;
		this.iconName			= iconName;
		this.commandName		= commandName;
		this.statusHelpMsg		= statusHelpMsg;
		this.disabledStatusHelpMsg	= disabledStatusHelpMsg;
		this.balloonHelpMsg		= balloonHelpMsg;
		this.customSize			= customSize;
		}

	public static MiToolBar		makeToolBar(
						MiiCommandManager manager, 
						MiiCommandHandler handler, 
						MiToolAndMenuItemDefinition[] definition, 
						MiSize iconSize)
		{
		MiToolBar toolBar = new MiToolBar(manager);
		toolBar.setToolItemImageSizes(iconSize);
		for (int i = 0; i < definition.length; ++i)
			{
			if ((definition[i].displayName == null) && (definition[i].iconName == null))
				{
				toolBar.appendSpacer();
				}
			else if (definition[i].commandName != null)
				{
				MiWidget toolItem = toolBar.appendToolItem(
					definition[i].displayName,
					new MiImage(MiSystem.getPropertyOrKey(definition[i].iconName), true),
					handler, 
					MiSystem.getPropertyOrKey(definition[i].commandName));

				toolItem.setNormalStatusHelpMessage(definition[i].statusHelpMsg);
				toolItem.setInSensitiveStatusHelpMessage(definition[i].disabledStatusHelpMsg);
				toolItem.setBalloonHelpMessage(definition[i].balloonHelpMsg);

				if (definition[i].customSize != null)
					toolItem.setSize(definition[i].customSize);
				}
			else
				{
				MiLabel toolItem = new MiLabel(
					new MiImage(MiSystem.getPropertyOrKey(definition[i].iconName), true));

				toolBar.appendPart(toolItem);

				toolItem.setNormalStatusHelpMessage(definition[i].statusHelpMsg);
				toolItem.setInSensitiveStatusHelpMessage(definition[i].disabledStatusHelpMsg);
				toolItem.setBalloonHelpMessage(definition[i].balloonHelpMsg);

				if (definition[i].customSize != null)
					toolItem.setSize(definition[i].customSize);
				}
			}
		return(toolBar);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEditorMenu with the given name. If 
					 * this menu is assigned to a menubar then this name is
					 * used as the menu launcher button's label.
					 * @param name		the name of the menu
					 * @param manager	the manager responsible for handling
					 *			menu item command availabilities and
					 *			states (this is usually a MiEditorWindow).
					 *------------------------------------------------------*/
	public static MiEditorMenu	makeMenu(String name, MiiCommandManager manager,
						MiiCommandHandler handler, 
						MiToolAndMenuItemDefinition[] definition, 
						MiSize iconSize)
		{
		MiEditorMenu menu = new MiEditorMenu(name, manager);
		for (int i = 0; i < definition.length; ++i)
			{
			if ((definition[i].displayName == null) || (definition[i].iconName == null))
				{
				menu.appendSeparator();
				}
			else
				{
				MiMenuItem menuItem = menu.appendMenuItem(
					MiSystem.getPropertyOrKey(definition[i].displayName),
					handler, 
					MiSystem.getPropertyOrKey(definition[i].commandName),
					MiSystem.getPropertyOrKey(definition[i].statusHelpMsg),
					MiSystem.getPropertyOrKey(definition[i].disabledStatusHelpMsg),
					MiSystem.getPropertyOrKey(definition[i].balloonHelpMsg));
				
				if (definition[i].iconName != null)
					{
					menuItem.setIconGraphics(new MiImage(
						MiSystem.getPropertyOrKey(definition[i].iconName), true));
			
					if (definition[i].customSize != null)
						menuItem.setSize(definition[i].customSize);
					else
						menuItem.getIconGraphics().setSize(iconSize);
					}
				}
			}
		return(menu);
		}
	}

