
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
public class MiIDisplayContextCursor extends MiEventMonitor implements MiiTypes
	{
	private		int		currentAppearance 	= Mi_DEFAULT_CURSOR;
	private		MiBounds	footPrint 		= new MiBounds();

	public				MiIDisplayContextCursor()
		{
		}

	public		int		processEvent(MiEvent event)
		{
		if ((event.getType() == Mi_TIMER_TICK_EVENT)
			|| (event.getType() == Mi_IDLE_EVENT))
			{
			return(Mi_PROPOGATE_EVENT);
			}
		if (event.type == MiEvent.Mi_WINDOW_EXIT_EVENT)
			{
/*
			if (currentAppearance != Mi_DEFAULT_CURSOR)
				{
				event.editor.restoreNormalMouseAppearance();
				currentAppearance = Mi_DEFAULT_CURSOR;
				}
*/
			return(Mi_PROPOGATE_EVENT);
			}

		if (event.mouseButtonState != 0)
			{
			return(Mi_PROPOGATE_EVENT);
			}

		// FIX: We should get this event on modal windows to restore to WAIT cursor, but we do not
		if (event.type == MiEvent.Mi_WINDOW_ENTER_EVENT)
			{
			event.editor.setMouseAppearance(event.editor.getMouseAppearance());
			}

		int image = Mi_DEFAULT_CURSOR;
		MiParts targetPath = event.getTargetPath();
		MiParts targetList = event.getTargetList();
		MiEditor lastEditor = event.editor;
		footPrint.copy(event.worldMouseFootPrint);

//System.out.println("START LOOKING AT OBJs UNDER MOUSE");

		// Examine parts under mouse, starting with top part..
		// and for each of these parts examine all of the parts containers.
		// unless they have already been examined..
		////// Changed this to look at targetPath first to aggree with mouse focus and mouse dispatch... 9-16-2003
		for (int i = 0; i < targetPath.size(); ++i)
			{
			MiPart part = targetPath.elementAt(i);
//MiDebug.println("Look for cusror at part UNDER MOUSE = " + part);
			do	{
				//if ((i != 0) && (targetPath.contains(part)))
					//break;

				if (!targetList.contains(part))
					break;

//MiDebug.println("OBJ UNDER MOUSE = " + part);

				// FIX: add a 'hasContextCursorArea' attribute to check if we need to do this
				MiEditor otherEditor = part.getContainingEditor();
				if (otherEditor == null)
					break;
				if (otherEditor != lastEditor)
					{
					if (otherEditor == event.editor)
						footPrint.copy(event.worldMouseFootPrint);
					else
						event.editor.transformToOtherEditorSpace(
							otherEditor, event.worldMouseFootPrint, footPrint);

					lastEditor = otherEditor;
					}
				image = part.getContextCursor(footPrint);
//MiDebug.println("OBJ CONTEXT CURSOR at footprint = " + part.getContextCursor(footPrint));
				if (image != Mi_DEFAULT_CURSOR)
					break;

				part = part.getContainer(0);
				} while (part != null);

			if (image != Mi_DEFAULT_CURSOR)
				break;

			if ((part != null) && (part.getDrawManager() != null))
				break;
			}

		if (image != Mi_DEFAULT_CURSOR)
			event.editor.setMouseAppearance(image);
		else
			event.editor.restoreNormalMouseAppearance();

		return(Mi_PROPOGATE_EVENT);
		}
	}

