
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
public class MiSetVisiblityCommandHandler implements MiiCommandHandler, MiiCommandNames
	{
	private		boolean		showWaitCursor;
	private		MiPart		target;

	public				MiSetVisiblityCommandHandler(MiPart target)
		{
		this(target, false);
		}

	public				MiSetVisiblityCommandHandler(
						MiPart target, 
						boolean showWaitCursorWhenMakeVisible)
		{
		this.target = target;
		this.showWaitCursor = showWaitCursorWhenMakeVisible;
		}

	public		void		processCommand(String command)
		{
		if (command.equals(Mi_HIDE_COMMAND_NAME))
			{
			target.setVisible(false);
			}
		else if (command.equals(Mi_SHOW_COMMAND_NAME))
			{
			if ((showWaitCursor) && (target.getRootWindow() != null))
				{
				target.getRootWindow().pushMouseAppearance(
					MiiTypes.Mi_WAIT_CURSOR, 
					"MiSetVisiblityCommandHandler." + target.getName());

				target.setVisible(true);

				target.getRootWindow().popMouseAppearance(
					"MiSetVisiblityCommandHandler." + target.getName());
				}
			else
				{
				target.setVisible(true);
				}
			}
		}
	}


