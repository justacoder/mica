
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
public class MiIClickAndDrop extends MiEventHandler implements MiiActionTypes
	{
	private		MiDataTransferOperation		transferOp;
	private		MiPart			prototype;
	private		boolean				dropOnlyInEditors = true;

	public				MiIClickAndDrop()
		{
		addEventToCommandTranslation(Mi_EXECUTE_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		}

	public		void		setPrototype(MiPart obj)
		{
		prototype = obj;
		}
	public		MiPart	getPrototype()
		{
		return(prototype);
		}
	public		void		setDropOnlyInEditors(boolean flag)
		{
		dropOnlyInEditors = flag;
		}
	public		boolean		getDropOnlyInEditors()
		{
		return(dropOnlyInEditors);
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
		if (isCommand(Mi_EXECUTE_COMMAND_NAME))
			{
			if (((dropOnlyInEditors) && (event.getTargetList().elementAt(0) != event.editor))
				|| (prototype == null))
				{
				return(Mi_PROPOGATE_EVENT);
				}

			MiPart dropOnObj = null;
			if (dropOnlyInEditors)
				{
				dropOnObj = event.editor;
				}
			else
				{
				MiParts targetList = event.getTargetList();
				for (int i = 0; i < targetList.size(); ++i)
					{
					if (targetList.elementAt(i).isDragAndDropTarget())
						{
						dropOnObj = targetList.elementAt(i);
						break;
						}
					}
				}
			transferOp = new MiDataTransferOperation(prototype);
			String format;
			if ((dropOnObj == null)
				|| ((format = MiDataTransferOperation.getCommonDataFormat(prototype, dropOnObj)) == null)
				|| (!dropOnObj.dispatchActionRequest(Mi_DATA_IMPORT_ACTION, transferOp))
				|| (!dropOnObj.supportsImportOfSpecificInstance(transferOp)))
				{
				}
			else
				{
/*
				// FIX: Alter obj here to make it the same size in new editor
				// as it was in source editor
				// setTargetSize????	
				MiBounds bounds = prototype.getBounds();
				MiEditor dropOnEditor = dropOnObj.getContainingEditor();
				event.editor.getRootWindow().transformToOtherEditorSpace(
					dropOnEditor, bounds, bounds);
*/
				transferOp.setLookTargetPosition(event.worldPt);
				transferOp.setDataFormat(format);
				if (dropOnObj.dispatchAction(Mi_DATA_IMPORT_ACTION, transferOp) 
					== MiiTypes.Mi_PROPOGATE)
					{
					dropOnObj.doImport(transferOp);
					}
				}
			transferOp = null;
			return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}

