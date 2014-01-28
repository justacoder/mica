
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
public class MiISelectObjectUnderMouse extends MiEventHandler
	{
	private		MiiSelectionManager	selectManager;
	private		boolean			selectActionIsToggle;
	private		boolean			selectAdditionalActionIsToggle = true;
	private		String			selectItemTag;
	private		MiParts			items = new MiParts();
	private		MiParts			path = new MiParts();
	private		MiParts			lastSelectables = new MiParts();


	public				MiISelectObjectUnderMouse()
		{
		addEventToCommandTranslation(
			Mi_SELECT_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(
			Mi_DESELECT_ALL_COMMAND_NAME, Mi_KEY_PRESS_EVENT, Mi_ESC_KEY, 0);
		addEventToCommandTranslation(
			Mi_SELECT_ADDITIONAL_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_SELECT_ADDITIONAL_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, Mi_CONTROL_KEY_HELD_DOWN);
		}

	public		void		setAllSelectActionsToggleSelectionState(boolean flag)
		{
		selectActionIsToggle = flag;
		}
	public		boolean		getAllSelectActionsToggleSelectionState()
		{
		return(selectActionIsToggle);
		}
	public		void		setSelectAdditionalActionTogglesSelectionState(boolean flag)
		{
		selectAdditionalActionIsToggle = flag;
		}
	public		boolean		getSelectAdditionalActionTogglesSelectionState()
		{
		return(selectAdditionalActionIsToggle);
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
		selectManager = event.editor.getSelectionManager();
		if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			return(select(event, false));
			}
		else if (isCommand(Mi_SELECT_ADDITIONAL_COMMAND_NAME))
			{
			return(select(event, true));
			}
		else if (isCommand(Mi_DESELECT_ALL_COMMAND_NAME))
			{
			if (selectManager.deSelectAll())
				return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}

	public		int	 		select(MiEvent event, boolean multipleSelect)
		{
		MiEditor editor = event.editor;
		MiParts targetList = event.getTargetList();
		MiParts selectables = new MiParts();

		MiPart firstPartBelowSelectables = editor.getCurrentLayer();
		if (editor.getFilter() == null)
			{
			// ---------------------------------------------------------------
			// Find list of selectables in targetList,
			// cycle through each one as long as list stays the same
			// ---------------------------------------------------------------
//MiDebug.println("targetList = " + targetList);
			for (int i = 0; i < targetList.size(); ++i)
				{
				MiPart obj = targetList.elementAt(i);
				if (obj == firstPartBelowSelectables)
					break;

				if ((obj.getContainer(0) == firstPartBelowSelectables)
					&& (obj.isSelectable()))
					{
					if (!selectables.contains(obj))
						{
						selectables.addElement(obj);
						}
					}
				else if ((obj = getSelectableContainerOfPart(obj, firstPartBelowSelectables)) != null)
					{
					if (!selectables.contains(obj))
						{
						selectables.addElement(obj);
						}
					}
				}
//MiDebug.println("selectables1 = " + selectables);
			}
		else
			{
			// ---------------------------------------------------------------
			// Find list of selectables from path of each part in targetList,
			// cycle through each one as long as list stays the same
			// ---------------------------------------------------------------
			for (int i = 0; i < targetList.size(); ++i)
				{
				MiPart obj = targetList.elementAt(i);
				if (obj == firstPartBelowSelectables)
					break;

				if (!obj.isSelectable()) 
					{
					obj = getSelectableContainerOfPart(obj, firstPartBelowSelectables);
					if (obj == null)
						{
						continue;
						}
					}


				path.removeAllElements();
				MiUtility.getPath(editor, obj, path);

//MiDebug.println("lookAt = " + obj);
//MiDebug.dump(obj);
//MiDebug.println("path = " + path);
				editor.getFilter().filterParts(path, items);
//MiDebug.println("items = " + items);

				for (int j = 0; j < items.size(); ++j)
					{
					obj = items.elementAt(j);
//MiDebug.println("lookAt = " + obj);
					if (obj == firstPartBelowSelectables)
						break;

					if ((obj.isSelectable()) && (!selectables.contains(obj)))
						selectables.addElement(obj);
					}
				}
			}
//MiDebug.println("lastSelectables = " + lastSelectables);
//MiDebug.println("selectables = " + selectables);

		if ((!lastSelectables.equalsIgnoreOrder(selectables)) && (!multipleSelect))
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
				MiDebug.println("DeSelecting all because mouse moved to different area");
			selectManager.deSelectAll();
			}
		lastSelectables = selectables;
			
		if (selectables.size() == 1)
			{
			MiPart obj = selectables.elementAt(0);

			if (obj.isSelected())
				{
				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
					MiDebug.println("Part is already selected, checking to see if we should deselect it");
				if ((selectActionIsToggle) 
					|| ((selectAdditionalActionIsToggle) && (multipleSelect)))
					{
					selectManager.deSelectObject(obj);
					}
				}
			else
				{
				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
					{
					MiDebug.println("Part is not selected, checking to see if we should\n"
					+ " deselect everything else before selecting it");
					}
				if (multipleSelect)
					selectManager.selectAdditionalObject(obj);
				else
					selectManager.selectObject(obj);
				}
			}
		else if (selectables.size() > 1)
			{
			if (multipleSelect) 
				{
				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
					{
					MiDebug.println("This is multiple select when multiple selected objects\n"
						+ " are under the mouse - checking to see which is the next\n"
						+ " unselected obj under mouse");
					}

				boolean selectedSomething = false;
				for (int i = 0; i < selectables.size(); ++i)
					{
					if (!selectables.elementAt(i).isSelected())
						{
						selectManager.selectAdditionalObject(selectables.elementAt(i));
						selectedSomething = true;
						break;
						}
					}
				if ((!selectedSomething) && (selectAdditionalActionIsToggle))
					{
					for (int i = 0; i < selectables.size(); ++i)
						{
						selectManager.deSelectObject(selectables.elementAt(i));
						}
					}
				}
			else
				{
				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
					{
					MiDebug.println("This is single select when multiple selected objects\n"
						+ " are under the mouse - deselecting them and checking to see\n"
						+ " which is the next unselected obj under mouse");
					}

				boolean selectedSomething = false;
				boolean lastItemSelected = false;
				for (int i = 0; i < selectables.size(); ++i)
					{
					if (selectables.elementAt(i).isSelected())
						{
						selectManager.deSelectObject(selectables.elementAt(i));
						lastItemSelected = true;
						}
					else if ((lastItemSelected) && (!selectedSomething))
						{
						selectManager.selectObject(selectables.elementAt(i));
						selectedSomething = true;
						}
					}
				// if we didn't select anything or this is a toggle event and nothing
				// wasn't deselected either...
				if ((!selectedSomething) 
					|| ((!selectedSomething) && (selectActionIsToggle) && (!lastItemSelected)))
					{
					selectManager.selectObject(selectables.elementAt(0));
					}
				}
			}
		else
			{
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_INTERACTIVE_SELECT))
				MiDebug.println("DeSelecting all because no selectables under mouse");
			selectManager.deSelectAll();
			}
/*
		if (!selectedSomething)
			return(Mi_PROPOGATE_EVENT);
*/
		return(Mi_CONSUME_EVENT);
		}
	// Added 5-23-2002 so can set component container 'pickable if transparent' = false, but 
	// still selectable when user clicks on a visible but non-selectable part of the component
	protected	MiPart		getSelectableContainerOfPart(MiPart obj, MiPart firstPartBelowSelectables)
		{
		MiPart c = obj.getContainer(0);
		while (c != null)
			{
			if ((c != firstPartBelowSelectables) && (c.isSelectable()))
				{
				return(c);
				}
			c = c.getContainer(0);
			}
		return(null);
		}
	}
