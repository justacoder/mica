
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
import java.util.Vector; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorMenuBar extends MiMenuBar
	{
	private		MiEditor	editor;
	private		Vector		menus	= new Vector();



					/**------------------------------------------------------
	 				 * Constructs a new MiEditorMenuBar.
					 *------------------------------------------------------*/
	public				MiEditorMenuBar()
		{
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEditorMenuBar populated with the
					 * pulldown menus from the given array of pulldowns.
					 * @param pulldowns	an array of pulldown menus
					 *------------------------------------------------------*/
	public				MiEditorMenuBar(MiEditorMenu[] pulldowns)
		{
		appendPulldownMenus(pulldowns);
		}
					/**------------------------------------------------------
	 				 * Appends the pulldown menus from the given array of 
					 * pulldowns. These are added to the right of the existing
					 * pulldowns in the menubar (unless the name of the
					 * pulldown menu is 'help', in which case it is added to
					 * the right side of the menubar).
					 * @param pulldowns	an array of pulldown menus
					 *------------------------------------------------------*/
	public		void		appendPulldownMenus(MiEditorMenu[] pulldowns)
		{
		for (int i = 0; i < pulldowns.length; ++i)
			appendPulldownMenu(pulldowns[i]);
		}
					/**------------------------------------------------------
	 				 * Appends the given pulldown menu to the right of the 
					 * existing pulldowns already in the menubar.
					 * @param menu		the pulldown menu
					 *------------------------------------------------------*/
	public		void		appendPulldownMenu(MiEditorMenu menu)
		{
		appendPulldownMenu(menu, menu.getMenu().getName());
		}
					/**------------------------------------------------------
	 				 * Appends the given pulldown menu to the right of the 
					 * existing pulldowns already in the menubar.
					 * @param menu		the pulldown menu
					 * @param name		the name to display on the menu
					 *			launcher button for this menu.
					 *------------------------------------------------------*/
	public		void		appendPulldownMenu(MiEditorMenu menu, String name)
		{
		menus.addElement(menu);
		super.appendPulldownMenu(menu.getMenu(), name);
		if (editor != null)
			menu.setEditor(editor);
		}
					/**------------------------------------------------------
	 				 * Inserts the given pulldown menu at the given index
					 * in the menubar.
					 * @param menu		the pulldown menu
					 * @param index		the index of the insertion point
					 *------------------------------------------------------*/
	public		void		insertPulldownMenu(MiEditorMenu menu, int index)
		{
		insertPulldownMenu(menu.getMenu(), menu.getMenu().getName(), index);
		}
					/**------------------------------------------------------
	 				 * Gets the pulldown menu with the given name.
					 * @return 		the pulldown menu or null
					 *------------------------------------------------------*/
	public		MiEditorMenu	getMenu(String menuName)
		{
		//menuName = MiSystem.getProperty(menuName, menuName);
		for (int i = 0; i < menus.size(); ++i)
			{
			if (menuName.equals(((MiEditorMenu )menus.elementAt(i)).getMenu().getName()))
				return((MiEditorMenu )menus.elementAt(i));
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the pulldown menu item that generates the given
					 * command.
					 * @param command	the command
					 * @return 		the menu item that generates the
					 *			command or null
					 *------------------------------------------------------*/
	public		MiMenuItem	getMenuItemWithCommand(String command)
		{
		for (int i = 0; i < menus.size(); ++i)
			{
			MiEditorMenu menu =  (MiEditorMenu )menus.elementAt(i);
			MiMenuItem menuItem = (MiMenuItem )menu.getMenuItemWithCommand(command);
			if (menuItem != null)
				return(menuItem);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the pulldown menu item that displays the given
					 * label.
					 * @param label		the label of a menu item
					 * @return 		the  menu item with the given label
					 *			or null
					 *------------------------------------------------------*/
	public		MiMenuItem	getMenuItemWithLabel(String label)
		{
		label = MiSystem.getProperty(label, label);
		for (int i = 0; i < menus.size(); ++i)
			{
			MiEditorMenu menu =  (MiEditorMenu )menus.elementAt(i);
			MiMenuItem menuItem = (MiMenuItem )menu.getMenuItemWithLabel(label);
			if (menuItem != null)
				return(menuItem);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Sets the MiEditor that the menus in this menu will 
					 * target (operate on).
					 * @param editor	the target MiEditor
					 *------------------------------------------------------*/
	public		void		setEditor(MiEditor editor)
		{
		this.editor = editor;
		for (int i = 0; i < menus.size(); ++i)
			{
			((MiEditorMenu )menus.elementAt(i)).setEditor(editor);
			}
		}
	}

