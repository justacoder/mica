
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
import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import java.io.IOException; 
import java.io.BufferedInputStream; 
import java.io.OutputStream; 
import java.awt.Frame;
import java.awt.Color;

/**----------------------------------------------------------------------------------------------
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDrawingToolBarManager
	{
	public static final String	Mi_DRAWING_TOOL_SELECTED_CMD_NAME
						= "DrawingToolSelected";
	public static final String	Mi_SELECTION_TOOL_SELECTED_CMD_NAME
						= "SelectionToolSelected";
	public static final String	Mi_NEW_SHAPE_ATTRIBUTES_CHANGED_STATE_NAME
						= "NewShapeAttributesChanged";

	private		MiParts		toolBars 		= new MiParts();
	private		String		drawingState		= Mi_SELECTION_TOOL_SELECTED_CMD_NAME;
	private		String		howToUseToolMessage;
	private		MiAttributes	newShapeAttributes;
	private		MiPart		activeToolBar;
	private		MiEditor	editor;




	public				MiDrawingToolBarManager()
		{
		}

	public		void		registerToolBar(MiPart toolBar)
		{
		if (!(toolBar instanceof MiiCommandHandler))
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
				+ ": Attempt to register a toolbar that is not a MiiCommandHandler");
			}
/***
		if (!(toolBar instanceof MiToolBar))
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
				+ ": Attempt to register a toolbar that is not an subclass of MiToolBar");
			}
***/
		toolBars.addElement(toolBar);
		if (editor != null)
			{
			((MiToolBar )toolBar).setEditor(editor);
			}
		}
	public		void		unRegisterToolBar(MiPart toolBar)
		{
		toolBars.removeElement(toolBar);
		}

	public		void		setNewShapeAttributes(MiAttributes attributes)
		{
		newShapeAttributes = attributes;
		dispatchCommandToToolBars(null, Mi_NEW_SHAPE_ATTRIBUTES_CHANGED_STATE_NAME);
		}
	public		MiAttributes	getNewShapeAttributes()
		{
		return(newShapeAttributes);
		}


					/**------------------------------------------------------
	 				 * Sets the editor that the toolbars will apply their actions to
					 * @param editor 	the new editor (of type MiGraphicsEditor)
					 * @see			MiEditorWindow#getEditor
					 * @see			MiGraphicsWindow#setEditor
					 *------------------------------------------------------*/
	public		void		setEditor(MiEditor editor)
		{
		this.editor = editor;
		for (int i = 0; i < toolBars.size(); ++i)
			{
			if (toolBars.elementAt(i) instanceof MiToolBar)
				{
				((MiToolBar )toolBars.elementAt(i)).setEditor(editor);
				}
			}
		}

	public		void		drawingToolSelected(MiPart toolBar, String howToUseToolMessage, String toolCommandName)
		{
		this.activeToolBar = toolBar;
		this.howToUseToolMessage = howToUseToolMessage;
		drawingState = Mi_DRAWING_TOOL_SELECTED_CMD_NAME;
		dispatchCommandToToolBars(toolBar, Mi_DRAWING_TOOL_SELECTED_CMD_NAME + toolCommandName);
		}
	public		void		selectionToolSelected(MiPart toolBar, String selectionToolCommandName)
		{
		this.activeToolBar = null;
		this.howToUseToolMessage = null;
		drawingState = Mi_SELECTION_TOOL_SELECTED_CMD_NAME;
		dispatchCommandToToolBars(toolBar, Mi_SELECTION_TOOL_SELECTED_CMD_NAME + selectionToolCommandName);
		}

	protected	void		dispatchCommandToToolBars(MiPart toolBar, String command)
		{
		for (int i = 0; i < toolBars.size(); ++i)
			{
			if (toolBars.elementAt(i) != toolBar)
				((MiiCommandHandler )toolBars.elementAt(i)).processCommand(command);
			}
		}
	}

