
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

import java.util.HashMap;
import java.util.ArrayList;

/**----------------------------------------------------------------------------------------------
 * This class implements a standard clipboard. Because this clipboard is
 * also a MiPart, this clipboard can be displayed to the user.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiClipBoard extends MiContainer implements MiiCommandNames, MiiDisplayNames
	{
	//private		MiEditor	editor;



					/**------------------------------------------------------
	 				 * Constructs a new MiClipBoard. 
					 *------------------------------------------------------*/
	public				MiClipBoard()
		{
		}

					/**------------------------------------------------------
	 				 * Copies the given part to this clipboard.
					 * @param part			the part to copy to the clipboard
					 *------------------------------------------------------*/
	public 		void		copyToClipBoard(MiPart part)
		{
		MiParts parts = new MiParts();
		parts.addElement(part.deepCopy());

		MiChangeContentsTransaction cmd = new MiChangeContentsTransaction(this, parts);
		cmd.doit(this, parts, true);
		MiSystem.getTransactionManager().appendTransaction(cmd);
		dispatchAction(Mi_CLIPBOARD_NOW_HAS_DATA_ACTION);
		}
					/**------------------------------------------------------
	 				 * Deletes the given part and then copies the given part 
					 * to this clipboard.
					 * @param part			the part to cut to the clipboard
					 *------------------------------------------------------*/
	public 		void		cutToClipBoard(MiPart part)
		{
		MiParts parts = new MiParts();
		parts.addElement(part);

		//MiDeletePartsCommand deleteCmd = new MiDeletePartsCommand(part.getContainingEditor(), parts, true);
		MiDeletePartsCommand deleteCmd 
			= MiSystem.getCommandBuilder().getDeletePartsCommand().create(part.getContainingEditor(), parts, true);

		MiChangeContentsTransaction replaceCmd = new MiChangeContentsTransaction(this, parts);

		MiiTransaction openTransaction
			= MiSystem.getTransactionManager().startTransaction(
					new MiNestedTransaction(Mi_CUT_DISPLAY_NAME, replaceCmd, deleteCmd));

		deleteCmd.doit(part.getContainingEditor(), true);
		replaceCmd.doit(this, parts, true);

		dispatchAction(Mi_CLIPBOARD_NOW_HAS_DATA_ACTION);

		MiSystem.getTransactionManager().commitTransaction(openTransaction);
		}
					/**------------------------------------------------------
	 				 * Copies the parts that are selected in the given to
					 * this clipboard.
					 * @param editor	the editor that contians the selected
					 *			parts to copy
					 * @see 		MiEditor#getSelectedParts
					 * @see 		MiPart#isSelected
					 *------------------------------------------------------*/
	public 		void		copySelectionToClipBoard(MiEditor editor)
		{
		MiParts selectedObjects = new MiParts();
		editor.getSelectedParts(selectedObjects);

		for (int i = 0; i < selectedObjects.size(); ++i)
			{
			if (!selectedObjects.get(i).isCopyable())
				{
				selectedObjects.removeElementAt(i);
				--i;
				}
			}

		selectedObjects = MiUtility.makeCopyOfNetwork(selectedObjects);

		MiChangeContentsTransaction cmd = new MiChangeContentsTransaction(this, selectedObjects);
		cmd.doit(this, selectedObjects, true);
		MiSystem.getTransactionManager().appendTransaction(cmd);
		dispatchAction(Mi_CLIPBOARD_NOW_HAS_DATA_ACTION);
		}
					/**------------------------------------------------------
	 				 * Deletes and copies the parts that are selected in the given to
					 * this clipboard.
					 * @param editor	the editor that contians the selected
					 *			parts to cut
					 * @see 		MiEditor#getSelectedParts
					 * @see 		MiPart#isSelected
					 *------------------------------------------------------*/
	public 		void		cutSelectionToClipBoard(MiEditor editor)
		{
		MiParts selectedParts = new MiParts();
		editor.getSelectedParts(selectedParts);

		for (int i = 0; i < selectedParts.size(); ++i)
			{
			if (!selectedParts.get(i).isCopyable())
				{
				selectedParts.removeElementAt(i);
				--i;
				}
			}
		for (int i = 0; i < selectedParts.size(); ++i)
			{
			MiPart part = selectedParts.elementAt(i);
			editor.deSelect(part);
			}

		MiNestedTransaction nestedTransaction = new MiNestedTransaction(Mi_CUT_DISPLAY_NAME);
		MiSystem.getTransactionManager().startTransaction(nestedTransaction);

		// MiDeletePartsCommand deleteCmd = new MiDeletePartsCommand(editor, selectedParts, true);

		MiParts pastableSelectedParts = MiUtility.makeCopyOfNetwork(selectedParts);
		MiDeletePartsCommand deleteCmd = MiSystem.getCommandBuilder().getDeletePartsCommand().create(editor, selectedParts, true);
		MiChangeContentsTransaction replaceCmd = new MiChangeContentsTransaction(this, pastableSelectedParts);


		MiSystem.getTransactionManager().appendTransaction(Mi_CUT_DISPLAY_NAME, replaceCmd, deleteCmd);

		deleteCmd.doit(editor, true);
		replaceCmd.doit(this, pastableSelectedParts, true);

		dispatchAction(Mi_CLIPBOARD_NOW_HAS_DATA_ACTION);

		MiSystem.getTransactionManager().commitTransaction(nestedTransaction);
		}
					/**------------------------------------------------------
	 				 * Pastes the parts in this clipboard to the given editor.
					 * The location the (center of) parts are copied to is the
					 * cursor location (if the cursor is within the editor)
					 * oor the center of the editor (if not).
					 * @param editor	the editor to copy parts into.
					 * @see			MiEditor#getMousePosition
					 *------------------------------------------------------*/
	public 		void		pasteFromClipBoard(MiEditor editor)
		{
		if (editor.getWorldBounds().intersects(editor.getMousePosition().getCenter()))
			pasteFromClipBoard(editor, editor.getMousePosition().getCenter());
		else
			pasteFromClipBoard(editor, editor.getWorldBounds().getCenter());
		}
					/**------------------------------------------------------
	 				 * Pastes the graphics from this clipBoard into the given
					 * editor at the given location
					 * @param editor		the target editor
					 * @param targetLocation	the target location
					 *------------------------------------------------------*/
	public 		void		pasteFromClipBoard(MiEditor editor, MiPoint targetLocation)
		{
		MiPoint center = new MiPoint();
		getCenter(center);
		MiVector translation = new MiVector(
					targetLocation.x - center.x, targetLocation.y - center.y);
		pasteFromClipBoard(editor, translation);
		}
					/**------------------------------------------------------
	 				 * Pastes the graphics from this clipBoard into the given
					 * editor translated by the given amount
					 * @param editor		the target editor
					 * @param translation		the translation amount
					 *------------------------------------------------------*/
	public		void		pasteFromClipBoard(MiEditor editor, MiVector translation)
		{
		MiParts parts = new MiParts();
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			parts.addElement(getPart(i));
			}

		HashMap mapOfConnToItsPoints = new HashMap();

//MiDebug.println("pasteFromClipBoard parts = " + parts);
		parts = MiUtility.makeCopyOfNetwork(parts);
//MiDebug.println("2 pasteFromClipBoard parts = " + parts);

		// Save up where the connections lie now, before their nodes are 
		// placed which tends to wack out the connection's orientation
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if (obj instanceof MiConnection)
				{
				ArrayList listOfPoints = new ArrayList();
				
				for (int j = 0; j < obj.getNumberOfPoints(); ++j)
					{
					listOfPoints.add(obj.getPoint(j));
					}
				mapOfConnToItsPoints.put(obj, listOfPoints);
				}
			}


		MiNestedTransaction nestedTransaction = new MiNestedTransaction(Mi_PASTE_DISPLAY_NAME);
		MiSystem.getTransactionManager().startTransaction(nestedTransaction);


		// Assume makeCopyOfNetwork list has nodes come first, followed by connections
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if (obj instanceof MiConnection)
				{
				obj.validateLayout();
				MiiLayout layout = obj.getLayout();
				obj.setLayout(null);
				ArrayList listOfPoints = (ArrayList )mapOfConnToItsPoints.get(obj);

				MiVector connTranslation = new MiVector(translation);
				MiConnection conn = (MiConnection )obj;
				int startPoint = 0;
				if (conn.getSource() != null)
					{
					startPoint = 1;
					connTranslation.x = conn.getPoint(0).x - ((MiPoint )listOfPoints.get(0)).x;
					connTranslation.y = conn.getPoint(0).y - ((MiPoint )listOfPoints.get(0)).y;
					}
				int endPoint = listOfPoints.size();
				if (conn.getDestination() != null)
					{
					// If we are not going to truncate the number of points in the 
					// connection to what it was, then the last point of of the 
					// connection ends at a node, so we do not need or want to set 
					// it, so decrease endPoint, which is the last point we are to modify
					if (conn.getNumberOfPoints() == listOfPoints.size())
						{
						--endPoint;
						}
					int lastPoint = conn.getNumberOfPoints() - 1;
					connTranslation.x = conn.getPoint(lastPoint).x 
						- ((MiPoint )listOfPoints.get(listOfPoints.size() - 1)).x;
					connTranslation.y = conn.getPoint(lastPoint).y 
						- ((MiPoint )listOfPoints.get(listOfPoints.size() - 1)).y;
					}

				// Conns are now translatable so this has been done... 
				//connTranslation = new MiVector();
				conn.getGraphics().setNumberOfPoints(listOfPoints.size());
				for (int j = startPoint; j < endPoint; ++j)
					{
					MiPoint pt = (MiPoint )listOfPoints.get(j);
					obj.setPoint(j, pt.x + connTranslation.x, pt.y + connTranslation.y);
					}
				obj.setLayout(layout);
				}
				
//MiDebug.println("paste obj = " + obj);


			MiDataTransferOperation transfer = new MiDataTransferOperation(obj);
			MiPoint targetPosition = obj.getCenter();
			targetPosition.translate(translation);
			transfer.setData(obj);
			transfer.setTarget(editor.getCurrentLayer());
			transfer.setLookTargetPosition(targetPosition);
			transfer.setLookTargetBounds(obj.getBounds().translate(translation));
			transfer.setDataFormat(MiDataTransferOperation.getCommonDataFormat(this, editor));
			editor.doImport(transfer);
			editor.dispatchAction(Mi_DATA_IMPORT_ACTION, transfer);
			}
		// MiDeletePartsCommand createCmd = new MiDeletePartsCommand(editor, parts, false);
//MiDebug.println("3 pasteFromClipBoard parts = " + parts);
		MiDeletePartsCommand createCmd = MiSystem.getCommandBuilder().getDeletePartsCommand().create(editor, parts, false);
		MiSystem.getTransactionManager().appendTransaction(
					new MiNestedTransaction(Mi_PASTE_DISPLAY_NAME, createCmd));

		MiSystem.getTransactionManager().commitTransaction(nestedTransaction);
		}
					/**------------------------------------------------------
	 				 * Gets wether this clipboard has any graphics data to paste.
					 * @return		true if has data
					 *------------------------------------------------------*/
	public 		boolean		hasData()
		{
		return(getNumberOfParts() > 0 ? true : false);
		}
/*
	public 		void		processCommand(String cmd)
		{
		MiEditor editor = (MiEditor )getTargetOfCommand();
		if (cmd.equalsIgnoreCase(Mi_CUT_COMMAND_NAME))
			{
			cutSelectionToClipBoard(editor);
			}
		else if (cmd.equalsIgnoreCase(Mi_COPY_COMMAND_NAME))
			{
			copySelectionToClipBoard(editor);
			}
		else if (cmd.equalsIgnoreCase(Mi_PASTE_COMMAND_NAME))
			{
			pasteFromClipBoard(editor);
			}
		}
*/
	}



/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Framework
 * <p>
 * This class implements a undoable cut, copy, paste clipboard operation.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.10(Alpha)
 * @module 	%M%
 * @language	Java (JDK 1.2)
 *----------------------------------------------------------------------------------------------*/
class MiChangeContentsTransaction extends MiCommandHandler implements MiiTransaction, MiiDisplayNames, MiiCommandNames
	{
	private		MiContainer	clipBoard;
	private		MiParts		newContents	= new MiParts();
	private		MiParts		oldContents	= new MiParts();
	

					/**------------------------------------------------------
	 				 * Constructs a new instance of MiChangeContentsTransaction. 
					 * @param clipBoard		the clipboard
					 * @param newContents		the contents or subset of the 
					 *				contents of the clipboard that
					 *				this transaction affects
					 *------------------------------------------------------*/
	public				MiChangeContentsTransaction(
						MiContainer clipBoard, MiParts newContents)
		{
		this.clipBoard = clipBoard;
		this.newContents.append(newContents);
		}
		
					/**------------------------------------------------------
	 				 * Undoes or Redoes this transaction. This method creates
					 * a new transaction and adds it to the system-wide queue
					 * of undoable transactions.
					 * @param arg		Mi_UNDO_COMMAND_NAME or Mi_REDO_COMMAND_NAME
					 *------------------------------------------------------*/
	public		void		processCommand(String arg)
		{
		if (arg.equals(Mi_UNDO_COMMAND_NAME))
			processCommand(clipBoard, newContents, false);
		else
			processCommand(clipBoard, newContents, true);
		}
					/**------------------------------------------------------
	 				 * Undoes or Redoes this transaction. This method creates
					 * a new transaction and adds it to the system-wide queue
					 * of undoable transactions.
					 * @param clipBoard		the clipboard
					 * @param newContents		the graphics this transaction 
					 *				influences
					 * @param replaceWithNewContents true if the graphics this
					 *				transaction influences are to
					 *				replace the current contents
					 *				of the clipboard. false if
					 *				any old contents previously
					 *				recorded here are to be the
					 *				contents of the clipboard
					 *------------------------------------------------------*/
	public		void		processCommand(MiContainer clipBoard, 
						MiParts newContents, 
						boolean replaceWithNewContents)
		{
		doit(clipBoard, newContents, replaceWithNewContents);
		MiSystem.getTransactionManager().appendTransaction(
			new MiChangeContentsTransaction(clipBoard, newContents));
		}
					/**------------------------------------------------------
	 				 * Undoes or Redoes this transaction.
					 * @param clipBoard		the clipboard
					 * @param newContents		the graphics this transaction 
					 *				influences
					 * @param replaceWithNewContents true if the graphics this
					 *				transaction influences are to
					 *				replace the current contents
					 *				of the clipboard. false if
					 *				any old contents previously
					 *				recorded here are to be the
					 *				contents of the clipboard
					 *------------------------------------------------------*/
	public		void		doit(	MiContainer clipBoard, 
						MiParts newContents, 
						boolean replaceWithNewContents)
		{
		if (replaceWithNewContents)
			{
			oldContents.removeAllElements();
			for (int i = 0; i < clipBoard.getNumberOfParts(); ++i)
				{
				oldContents.addElement(clipBoard.getPart(i));
				}
			clipBoard.removeAllParts();
			for (int i = 0; i < newContents.size(); ++i)
				{
				clipBoard.appendPart(newContents.elementAt(i));
				}
			}
		else
			{
			clipBoard.removeAllParts();
			for (int i = 0; i < oldContents.size(); ++i)
				{
				clipBoard.appendPart(oldContents.elementAt(i));
				}
			}
		}
					/**------------------------------------------------------
					 * Gets the name of this transaction. This name is often 
					 * displayed, for example, in the menubar's edit pulldown
					 * menu.
					 * @return		the name of this transaction.
					 * @implements		MiiTransaction#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(Mi_COPY_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_COPY_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(clipBoard, newContents, true);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(clipBoard, newContents, false);
		} 
					/**------------------------------------------------------
					 * Repeats this transaction. This re-applies the changes 
					 * encapsulated by this transaction. For example, a 
					 * translation of a shape can be repeated in order to move 
					 * it further.
					 * @implements		MiiTransaction#repeat
					 *------------------------------------------------------*/
	public		void		repeat()
		{
		} 
					/**------------------------------------------------------
					 * Gets whether this transaction is undoable.
					 * @returns		true if undoable.
					 * @implements		MiiTransaction#isUndoable
					 *------------------------------------------------------*/
	public		boolean		isUndoable()
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Gets whether this transaction is repeatable. If repeatable
					 * then calling this transaction's repeat() method is permitted.
					 * @returns		true if repeatable.
					 * @implements		MiiTransaction#isRepeatable
					 *------------------------------------------------------*/
	public		boolean		isRepeatable()
		{
		return(false);
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		return(newContents);
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		return(oldContents);
		}
	public		String		toString()
		{
		return(super.toString() + "\n[oldContents=" + oldContents + "]\n[newContents=" + newContents + "]");
		}
	}

