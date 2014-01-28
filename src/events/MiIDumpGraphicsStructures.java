
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
public class MiIDumpGraphicsStructures extends MiEventMonitor
	{
	private		MiDumpGraphicsStructures	pgs;

	public static final String	Mi_PRINT_ATTRIBUTECACHE_COMMAND_NAME	= "PrintAttributeCache";
	public static final String	Mi_DETAILED_PRINT_COMMAND_NAME 		= "DetailedPrint";
	public static final String	Mi_DETAILED_PRINT_OBJECT_COMMAND_NAME 	= "DetailedPrintOnject";
	public static final String	Mi_ABBREVIATED_PRINT_COMMAND_NAME 	= "AbbreviatedPrint";
	public static final String	Mi_PRINT_PROPERTIES_COMMAND_NAME 	= "PrintProperties";

	public				MiIDumpGraphicsStructures()
		{
		addEventToCommandTranslation(
			Mi_DETAILED_PRINT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'g', Mi_CONTROL_KEY_HELD_DOWN + Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_PRINT_ATTRIBUTECACHE_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'a', Mi_CONTROL_KEY_HELD_DOWN + Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_ABBREVIATED_PRINT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'h', Mi_CONTROL_KEY_HELD_DOWN + Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_DETAILED_PRINT_OBJECT_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'f', Mi_CONTROL_KEY_HELD_DOWN + Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(
			Mi_PRINT_PROPERTIES_COMMAND_NAME, 
			Mi_KEY_PRESS_EVENT, 'p', Mi_CONTROL_KEY_HELD_DOWN + Mi_SHIFT_KEY_HELD_DOWN);
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
		if (pgs == null)
			{
			pgs = new MiDumpGraphicsStructures();
			}

		if (isCommand(Mi_DETAILED_PRINT_COMMAND_NAME))
			{
			pgs.setTargetOfCommand(event.editor.getRootWindow());
			}
		else if (isCommand(Mi_ABBREVIATED_PRINT_COMMAND_NAME))
			{
			pgs.setTargetOfCommand(event.editor);
			}
		else if (isCommand(Mi_DETAILED_PRINT_OBJECT_COMMAND_NAME))
			{
			// If not outside Mica graphics
			if (event.getTargetList().size() > 0)
				pgs.setTargetOfCommand(event.getTargetList().elementAt(0));
			else
				pgs.setTargetOfCommand(event.editor);
			}


		if ((isCommand(Mi_DETAILED_PRINT_COMMAND_NAME)) 
			|| (isCommand(Mi_DETAILED_PRINT_OBJECT_COMMAND_NAME)))
			{
			pgs.processCommand(MiDumpGraphicsStructures.DETAILED_INFO);
			}
		else if (isCommand(Mi_PRINT_ATTRIBUTECACHE_COMMAND_NAME))
			{
			pgs.setTargetOfCommand(event.editor);
			pgs.processCommand(MiDumpGraphicsStructures.ATTRIBUTE_CACHE);
			}
		else if (isCommand(Mi_PRINT_PROPERTIES_COMMAND_NAME))
			{
			pgs.setTargetOfCommand(event.editor);
			pgs.processCommand(MiDumpGraphicsStructures.PROPERTIES);
			}
		else
			{
			pgs.processCommand(MiDumpGraphicsStructures.NORMAL_INFO);
			}
		return(Mi_CONSUME_EVENT);
		}
	}

