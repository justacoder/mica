
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
import com.swfm.mica.util.Utility;

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiSetStatusBarStateToNameOfObjectUnderMouse extends MiEventHandler
	{
	private		MiEditorStatusBar statusBar;
	private		String		formatString;
	private		String		currentNameOfObjectUnderMouse;



	public				MiSetStatusBarStateToNameOfObjectUnderMouse(
						MiEditorStatusBar statusBar, String formatString)
		{
		this.statusBar = statusBar;
		this.formatString = formatString;
		}
	public				MiSetStatusBarStateToNameOfObjectUnderMouse(
						MiEditorStatusBar statusBar)
		{
		this.statusBar = statusBar;
		}

	public		int		processEvent(MiEvent event)
		{
		if ((event.type == MiEvent.Mi_WINDOW_EXIT_EVENT)
			|| (event.type == MiEvent.Mi_MOUSE_EXIT_EVENT))
			{
			statusBar.setState(null);
			currentNameOfObjectUnderMouse = null;
			return(Mi_PROPOGATE_EVENT);
			}

		MiParts targetPath = event.getTargetPath();
		int num = targetPath.size();
		for (int i = 0; i < num; ++i)
			{
			MiPart target = targetPath.elementAt(i);
			if (target instanceof MiEditor)
				break;

			String name = target.getName();
			if (name != null)
				{
				if (!name.equals(currentNameOfObjectUnderMouse))
					{
					currentNameOfObjectUnderMouse = name;
					if (formatString != null)
						statusBar.setState(Utility.sprintf(formatString, name));
					else
						statusBar.setState(name);
					}
				return(Mi_PROPOGATE_EVENT);
				}
			}
		statusBar.setState(null);
		currentNameOfObjectUnderMouse = null;
		return(Mi_PROPOGATE_EVENT);
		}
	}

