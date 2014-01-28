
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
import java.util.Hashtable;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiISingleKeyCreateObject extends MiEventHandler implements MiiActionTypes
	{
	private		Hashtable		prototypeShapes		= new Hashtable();
	private		boolean			selectNewlyCreatedShape	= true;


	public				MiISingleKeyCreateObject()
		{
		}

	public		void		assignShapeToKey(MiPart shape, int key)
		{
		if (shape != null)
			{
			setEventToCommandTranslation(key + "", Mi_KEY_PRESS_EVENT, key, 0);
			prototypeShapes.put(key + "", shape);
			}
		else
			{
			removeEventToCommandTranslation(key + "");
			prototypeShapes.remove(key + "");
			}
		}

	public		void		setSelectNewlyCreatedShape(boolean flag)
		{
		selectNewlyCreatedShape = flag;
		}
	public		boolean		getSelectNewlyCreatedShape()
		{
		return(selectNewlyCreatedShape);
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
		String key = getCommand();
		MiPart shape = ((MiPart )prototypeShapes.get(key)).deepCopy();

		event.editor.deSelectAll();

		shape.setCenter(event.editor.getMousePosition().getCenter());

		event.editor.appendItem(shape);

		event.editor.dispatchAction(Mi_INTERACTIVELY_COMPLETED_RUBBER_STAMP_PART_ACTION, shape);
		MiSystem.getTransactionManager().appendTransaction(new MiDeletePartsCommand(event.editor, shape, false));

		if (selectNewlyCreatedShape)
			{
			event.editor.select(shape);
			}

		return(Mi_CONSUME_EVENT);
		}
	}

