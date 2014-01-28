
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIDisplayContextMenu extends MiEventHandler implements MiiActionHandler, MiiActionTypes
	{
	private		boolean		doNotPoopDownIfClickIsOnInactivePartOfMenu = true;
	private		MiiContextMenu	currentMenu;
	private		MiiContextMenu	alwaysUseThisMenu;
	private		MiEditor	targetPartEditor;

	public				MiIDisplayContextMenu()
		{
		addEventToCommandTranslation(
			Mi_POPUP_COMMAND_NAME, Mi_RIGHT_MOUSE_DOWN_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_POPDOWN_COMMAND_NAME, Mi_KEY_EVENT, Mi_ESC_KEY, 0);
		addEventToCommandTranslation(
			Mi_POPDOWN_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_POPDOWN_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, Mi_ANY_MODIFIERS_HELD_DOWN);
		}
	public				MiIDisplayContextMenu(MiiContextMenu alwaysUseThisMenu)
		{
		this();
		this.alwaysUseThisMenu = alwaysUseThisMenu;
		}

					/**------------------------------------------------------
	 				 * Processes the command generated from the current event.
					 * Both are stored in the MiEventHandler super class.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			MiEventHandler#isCommand
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		if (isCommand(Mi_POPUP_COMMAND_NAME))
			{
			MiParts targetPath = event.getTargetPath();
			if (currentMenu != null)
				{
				currentMenu.popdown();
				currentMenu = null;
				// Allow graphics under context menu to select itself like a
				// cell in a treelist in MiBrowsableGridEventHandler
				event.getEditor().getRootWindow().pushBackEvent(event);
				return(Mi_PROPOGATE_EVENT);
				}

			MiBounds pickBounds = event.getMouseFootPrint(new MiBounds());
			for (int i = 0; i < targetPath.size(); ++i)
				{
				MiiContextMenu menu;
				if (alwaysUseThisMenu != null)
					menu = alwaysUseThisMenu;
				else
					menu = targetPath.elementAt(i).getContextMenu(pickBounds);

				if (menu != null)
					{
					MiPart targetPart = targetPath.elementAt(i);
//MiDebug.println("Popping up menu for context part = " + targetPart);
					targetPartEditor = targetPart.getContainingEditor();
					targetPartEditor.prependGrabEventHandler(this);
					MiPoint pt = targetPartEditor.getMousePosition().getCenter();
					if ((menu.getToSelectAttributedShape())
						&& (!(targetPart instanceof MiEditor))
						&& (targetPart.isSelectable()))
						{
						targetPartEditor.select(targetPart);
						}
					menu.popup(targetPart, pt);
					currentMenu = menu;
					currentMenu.getMenuGraphics().appendActionHandler(this, Mi_MENU_POPPED_DOWN_ACTION);
					return(Mi_CONSUME_EVENT);
					}
				}
			return(Mi_PROPOGATE_EVENT);
			}
		else if (isCommand(Mi_POPDOWN_COMMAND_NAME))
			{
			if (currentMenu != null)
				{
				if (doNotPoopDownIfClickIsOnInactivePartOfMenu)
					{
					MiBounds mouseBounds = event.getMouseFootPrint(new MiBounds());
					event.editor.transformLocalWorldToRootWorld(mouseBounds);
					if (!currentMenu.getMenuGraphics().getBounds().contains(mouseBounds))
						{
						popdown();
						return(Mi_CONSUME_EVENT);
						}
					}
				else
					{
					popdown();
					return(Mi_CONSUME_EVENT);
					}
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_MENU_POPPED_DOWN_ACTION))
			{
			popdown();
			}
		return(true);
		}
			
	protected	void		popdown()
		{
		if (currentMenu != null)
			{
			currentMenu.getMenuGraphics().removeActionHandlers(this);
			currentMenu.popdown();
			targetPartEditor.removeGrabEventHandler(this);
			currentMenu = null;
			targetPartEditor = null;
			}
		}
	}

