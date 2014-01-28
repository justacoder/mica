
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
 * This class can execute commands that a standard 'view' menu might
 * generate. The commands apply to the target MiEditor (see 
 * setTargetOfCommand()). The commands that are handled are:
 * <p>
 * <pre>
 *    Mi_FORMAT_COMMAND_NAME
 *    Mi_EXPAND_EDITING_AREA_COMMAND_NAME
 *    Mi_SHRINK_EDITING_AREA_COMMAND_NAME
 * </pre>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiFormatMenuCommands extends MiCommandHandler implements MiiCommandNames
	{
	private		MiiCommandManager manager;
	private		MiEditor	editor;


					/**------------------------------------------------------
	 				 * Constructs a new MiFormatMenuCommands.
					 * @see 		MiFormatMenu
					 *------------------------------------------------------*/
	public				MiFormatMenuCommands(MiiCommandManager manager)
		{
		this.manager = manager;
		}
					/**------------------------------------------------------
	 				 * Sets the target of the commands processed by this 
					 * MiiCommandHandler (i.e. what the commands are to act upon).
					 * The target must be an MiEditor.
					 * @param target	the target MiEditor
					 * @see			MiCommandHandler#getTargetOfCommand
					 * @see			#processCommand
					 *------------------------------------------------------*/
	public		void		setTargetOfCommand(Object newObject)
		{
		editor = (MiEditor )newObject;
		super.setTargetOfCommand(editor);
		}
					/**------------------------------------------------------
			 		 * Processes the given command.
					 * @command  		the command to execute
					 *------------------------------------------------------*/
	public		void		processCommand(String cmd)
		{
		MiUtility.pushMouseAppearance(
			(MiPart )getTargetOfCommand(), MiiTypes.Mi_WAIT_CURSOR, "MiFormatMenuCommands");

		if (cmd.equalsIgnoreCase(Mi_FORMAT_COMMAND_NAME))
			{
			editor.invalidateLayout();
			}
		else if (cmd.equalsIgnoreCase(Mi_EXPAND_EDITING_AREA_COMMAND_NAME))
			{
			MiBounds universe = editor.getUniverseBounds();
			double zoomFactor = MiIZoomAroundMouse.DEFAULT_ZOOM_FACTOR;
			universe.setWidth(universe.getWidth() * zoomFactor);
			universe.setHeight(universe.getHeight() * zoomFactor);
			editor.setUniverseBounds(universe);
			MiIZoomAroundMouse.adjustUniverseToBeAnIntegralZoomLevel(editor, zoomFactor);
			}
		else if (cmd.equalsIgnoreCase(Mi_SHRINK_EDITING_AREA_COMMAND_NAME))
			{
			MiBounds universe = editor.getUniverseBounds();
			double zoomFactor = MiIZoomAroundMouse.DEFAULT_ZOOM_FACTOR;
			universe.setWidth(universe.getWidth() / zoomFactor);
			universe.setHeight(universe.getHeight() / zoomFactor);
			editor.setUniverseBounds(universe);
			MiIZoomAroundMouse.adjustUniverseToBeAnIntegralZoomLevel(editor, zoomFactor);
			}
		MiUtility.popMouseAppearance((MiPart )getTargetOfCommand(), "MiFormatMenuCommands");
		}
	}

