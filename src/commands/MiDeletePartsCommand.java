
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
import com.swfm.mica.util.IntVector; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDeletePartsCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames, MiiActionTypes, MiiTypes
	{
	public static final String	Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME		= "duplicateToRight";
	public static final String	Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME		= "duplicateToBottom";
	public static final String	Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME	= "duplicateToBottomRight";
	public static final String	Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME	= "duplicateGrowBottomRight";

	private		String		name;
	private		MiConnection	prototypeConnection 	= new MiConnection();
	private		IntVector	srcConnPts		= new IntVector();
	private		IntVector	destConnPts		= new IntVector();
	private		MiParts		containersOfPartsToDelete= new MiParts();
	private		MiParts		containersOfConnsToDelete= new MiParts();
	private		MiParts		partsToDelete		= new MiParts();
	private		MiParts		connSources		= new MiParts();
	private		MiParts		connDests		= new MiParts();
	private		MiParts		connsToDelete		= new MiParts();
	private		IntVector	partIndices		= new IntVector();
	private		IntVector	connIndices		= new IntVector();
	private		boolean		toDelete;
	private		int		numberOfTimesToRepeatDuplication	= 1;
	private		boolean		selectAddedParts;
	private		MiEditor	editor;




	public				MiDeletePartsCommand()
		{
		}
	// The preferred method for creating undoable commands as per the new MiiCommandBuilder architecture
	public	MiDeletePartsCommand	create(MiEditor editor, MiParts parts, boolean toDelete)
		{
		return(new MiDeletePartsCommand(editor, parts, toDelete));
		}
	public				MiDeletePartsCommand(MiEditor editor, MiPart part, boolean toDelete)
		{
		MiParts parts = new MiParts(part);
		setup(editor, parts, toDelete, false);
		}
	public				MiDeletePartsCommand(MiEditor editor, MiParts parts, boolean toDelete)
		{
		this(editor, parts, toDelete, false);
		}
	public				MiDeletePartsCommand(MiEditor editor, MiParts parts, boolean toDelete, boolean connsOnly)
		{
		setup(editor, parts, toDelete, connsOnly);
		}
	protected	void		setup(MiEditor editor, MiParts parts, boolean toDelete, boolean connsOnly)
		{
		this.editor = editor;

		if (toDelete)
			name = Mi_DELETE_DISPLAY_NAME;
		else
			name = Mi_CREATE_DISPLAY_NAME;
		setTargetOfCommand(editor);
		this.toDelete = toDelete;
		getToBeDeletedConnectionsOfToBeDeletedParts(parts, connsToDelete, connSources, connDests);
		partsToDelete.append(parts);
		if (!connsOnly)
			{
			getIndicesOfParts(parts, partIndices, containersOfPartsToDelete);
			}
		else
			{
			parts.removeAllElements();
			partsToDelete.removeAllElements();
			}
		getIndicesOfParts(connsToDelete, connIndices, containersOfConnsToDelete);

		// Enforce here that indices are monotonically increasing
		if (!connsOnly)
			sortIndicesOfParts(parts, partIndices, containersOfPartsToDelete);
		sortIndicesOfParts(connsToDelete, connIndices, containersOfConnsToDelete, connSources, connDests, srcConnPts, destConnPts);
		}
	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		setName(String name)
		{
		this.name = name;
		}
	public		void		setSelectingAddedParts(boolean flag)
		{
		this.selectAddedParts = flag;
		}
	public		boolean		getSelectingAddedParts()
		{
		return(selectAddedParts);
		}
	public		void		setNumberOfTimesToRepeatDuplication(int num)
		{
		this.numberOfTimesToRepeatDuplication = num;
		}
	public		int		getNumberOfTimesToRepeatDuplication()
		{
		return(numberOfTimesToRepeatDuplication);
		}
	public		void		setPrototype(MiConnection conn)
		{
		prototypeConnection = conn;
		}
	public		MiConnection	getPrototype()
		{
		return(prototypeConnection);
		}
	public		void		processCommand(String arg)
		{
		MiEditor editor = (MiEditor )getTargetOfCommand();
		if (editor == null)
			{
			return;
			}
//System.out.println("arg = " + arg);
		if (arg.equalsIgnoreCase(Mi_DELETE_COMMAND_NAME))
			{
			MiPart		obj;
			MiParts		partsToDelete = new MiParts();

			editor.getSelectedParts(partsToDelete);
			for (int i = 0; i < partsToDelete.size(); ++i)
				{
				obj = partsToDelete.elementAt(i);
				if (!obj.isDeletable())
					{
					partsToDelete.removeElement(obj);
					--i;
					}
				}
			if (partsToDelete.size() > 0)
				{
				String transactionName = MiSystem.getProperty(Mi_DELETE_DISPLAY_NAME);
				if ((partsToDelete.size() == 1) 
					&& (partsToDelete.elementAt(0).getName() != null)
					&& (partsToDelete.elementAt(0).getName().length() > 0))
					{
					transactionName += " " + partsToDelete.elementAt(0).getName();
					}
				else
					{
 					transactionName += " " + "Parts";
					}

				MiNestedTransaction nestedTransaction = new MiNestedTransaction(transactionName);
				MiSystem.getTransactionManager().startTransaction(nestedTransaction);

				processCommand(editor, partsToDelete, Mi_DELETE_DISPLAY_NAME, true, false, false);

				MiSystem.getTransactionManager().commitTransaction(nestedTransaction);

				MiSelectionManager.notifyAboutNumberOfShapesSelected(editor);
				}
			}
		else if ((arg.equalsIgnoreCase(Mi_DUPLICATE_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME)))
			{
			duplicate(editor, editor.getSelectedParts(new MiParts()), arg, 1, true);
			}
		else if ((arg.startsWith(Mi_DUPLICATE_COMMAND_NAME))
			|| (arg.startsWith(Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME))
			|| (arg.startsWith(Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME))
			|| (arg.startsWith(Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME))
			|| (arg.startsWith(Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME)))
			{
			int index = arg.indexOf('.');
			if (index != -1)
				{
				String numberOfTimesToRepeatDuplicationStr = arg.substring(index + 1);
				numberOfTimesToRepeatDuplication = com.swfm.mica.util.Utility.toInteger(numberOfTimesToRepeatDuplicationStr);
				if (numberOfTimesToRepeatDuplication > 1)
					{
					--numberOfTimesToRepeatDuplication;
					}
				arg = arg.substring(0, index);
				}
			duplicate(editor, editor.getSelectedParts(new MiParts()), arg, numberOfTimesToRepeatDuplication, false);
			}
		else if (arg.equalsIgnoreCase(Mi_DISCONNECT_COMMAND_NAME))
			{
			MiParts		selectedParts = editor.getSelectedParts(new MiParts());
			if (selectedParts.size() > 0)
				{
				processCommand(editor, selectedParts, Mi_DISCONNECT_DISPLAY_NAME, true, true, false);
				}
			}
		else if (arg.equalsIgnoreCase(Mi_CONNECT_COMMAND_NAME))
			{
			MiParts		selectedParts = editor.getSelectedParts(new MiParts());
			MiParts		connsToCreate = new MiParts();
			for (int i = 0; i < selectedParts.size(); ++i)
				{
				if (selectedParts.elementAt(i) instanceof MiConnection)
					{
					selectedParts.removeElementAt(i);
					--i;
					}
				}

			MiConnectionOperation connectOp = new MiConnectionOperation(prototypeConnection, null, null);
			connectOp.setSourceConnPt(Mi_CENTER_LOCATION);
			connectOp.setDestinationConnPt(Mi_CENTER_LOCATION);
			for (int i = 0; i < selectedParts.size() - 1; ++i)
				{
				MiPart srcObj = selectedParts.elementAt(i);
				connectOp.setSource(srcObj);
				for (int j = i + 1; j < selectedParts.size(); ++j)
					{
					MiConnection conn;
					MiPart destObj = selectedParts.elementAt(j);
					connectOp.setDestination(destObj);
					connectOp.setConnection(null);
					if ((srcObj.dispatchAction(
						Mi_CONNECT_ACTION | Mi_EXECUTE_ACTION_PHASE, connectOp)
						!= Mi_PROPOGATE)
						&& ((conn = connectOp.getConnection()) != null))
						{
						}
					else if ((destObj.dispatchAction(
						Mi_CONNECT_ACTION | Mi_EXECUTE_ACTION_PHASE, connectOp)
						!= Mi_PROPOGATE)
						&& ((conn = connectOp.getConnection()) != null))
						{
						}
					else
						{
						conn = (MiConnection )connectOp.getPrototypeConnection().copy();
						conn.setSource(selectedParts.elementAt(i));
						conn.setDestination(selectedParts.elementAt(j));
						editor.appendItem(conn);
						}
					connsToCreate.addElement(conn);
					}
				}

			MiDeletePartsCommand cmd = new MiDeletePartsCommand(editor, connsToCreate, false, true);
			cmd.setName(Mi_CONNECT_DISPLAY_NAME);
			MiSystem.getTransactionManager().appendTransaction(cmd);
			}
		}
	public 		void		processCommand(MiEditor editor, MiParts parts, String name, boolean toDelete, boolean connsOnly, boolean alreadyDidIt)
		{
		processCommand(editor, parts, name, toDelete, connsOnly, alreadyDidIt, 1, false);
		}

	public 		void		processCommand(MiEditor editor, MiParts parts, String name, boolean toDelete, boolean connsOnly, boolean alreadyDidIt, int numberOfTimesToRepeatDuplication, boolean selectAddedParts)
		{
		MiDeletePartsCommand cmd = new MiDeletePartsCommand(editor, parts, toDelete, connsOnly);
		cmd.setName(name);
		cmd.setNumberOfTimesToRepeatDuplication(numberOfTimesToRepeatDuplication);
		cmd.setSelectingAddedParts(selectAddedParts);
		if (alreadyDidIt)
			{
			MiSystem.getTransactionManager().appendTransaction(cmd);
			}
		else
			{
			MiNestedTransaction openTransaction = MiSystem.getTransactionManager().startTransaction(cmd);
			cmd.doit(editor, toDelete);
			MiSystem.getTransactionManager().commitTransaction(openTransaction);
			}
		}
/*
	public 		void		processCommand(MiEditor editor, MiParts parts, boolean toDelete)
		{
		processCommand(editor, parts, Mi_DELETE_DISPLAY_NAME, toDelete, false);
		}
*/
	protected	void		doit(MiEditor editor, boolean toDelete)
		{
		if (toDelete)
			{
//MiDebug.println("connsToDelete=" + connsToDelete);
//MiDebug.println("partsToDelete=" + partsToDelete);
			for (int i = 0; i < connsToDelete.size(); ++i)
				{
				connsToDelete.elementAt(i).dispatchAction(Mi_DELETE_ACTION);
				connsToDelete.elementAt(i).removeSelf();
				}
			for (int i = 0; i < partsToDelete.size(); ++i)
				{
				partsToDelete.elementAt(i).dispatchAction(Mi_DELETE_ACTION);
				partsToDelete.elementAt(i).removeSelf();
				//removeSelf does this partsToDelete.elementAt(i).removeAllConnections();
				}
			}
		else
			{
			// if (selectAddedParts)
				{
				editor.deSelectAll();
				}
//MiDebug.println("RESTORE connsToDelete=" + connsToDelete);
//MiDebug.println("RESTORE partsToDelete=" + partsToDelete);
			for (int i = 0; i < partsToDelete.size(); ++i)
				{
				int index = partIndices.elementAt(i);
				MiPart container = containersOfPartsToDelete.elementAt(i);
				if (index == -1)
					container.appendItem(partsToDelete.elementAt(i));
				else
					container.insertItem(partsToDelete.elementAt(i), index);

				if (selectAddedParts)
					{
					editor.selectAdditional(partsToDelete.elementAt(i));
					}
				partsToDelete.elementAt(i).dispatchAction(Mi_CREATE_ACTION);
				}
			for (int i = 0; i < connsToDelete.size(); ++i)
				{
				MiConnection conn = (MiConnection )connsToDelete.elementAt(i);
				boolean layoutEnabled = false;
				if (conn.getLayout() != null)
					{
					layoutEnabled = conn.getLayout().isEnabled();
					conn.getLayout().setEnabled(false);
					}
				conn.setSource(null);
				conn.setDestination(null);
				conn.setSourceConnPt(srcConnPts.elementAt(i));
				conn.setDestinationConnPt(destConnPts.elementAt(i));
				conn.setSource(connSources.elementAt(i));
				conn.setDestination(connDests.elementAt(i));
				int index = connIndices.elementAt(i);
				MiPart container = containersOfConnsToDelete.elementAt(i);
				if (index == -1)
					container.appendItem(conn);
				else
					container.insertItem(conn, index);
				if (conn.getLayout() != null)
					{
					conn.getLayout().setEnabled(layoutEnabled);
					}
				if (selectAddedParts)
					{
					editor.selectAdditional(conn);
					}
				conn.dispatchAction(Mi_CREATE_ACTION);
				}
			}
		MiSelectionManager.notifyAboutNumberOfShapesSelected(editor);
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
		return(name);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		if (toDelete)
			return(Mi_DELETE_COMMAND_NAME);
		else
			return(Mi_CREATE_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), toDelete);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), !toDelete);
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
		if (name.startsWith(Mi_DUPLICATE_COMMAND_NAME) 
			|| (name.startsWith(Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME))
			|| (name.startsWith(Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME))
			|| (name.startsWith(Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME))
			|| (name.startsWith(Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME)))
			{
			duplicate(editor, partsToDelete, name, numberOfTimesToRepeatDuplication, selectAddedParts);
			}
		else if (name.equals(Mi_CREATE_COMMAND_NAME))
			{
			MiParts copyOfParts = new MiParts();
			for (int i = 0; i < partsToDelete.size(); ++i)
				{
				MiPart obj = partsToDelete.elementAt(i);
				MiPart part = obj.deepCopy();
				part.translate(part.getWidth()/2, -part.getHeight()/2);
				obj.getContainer(0).appendItem(part);
				copyOfParts.addElement(part);
				}
			processCommand(getEditor(), copyOfParts, getName(), false, false, true);
			}
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
					 * Duplicate and Create are repeatable, but if another unrepeatable
					 * command is part of this one, such as MiDeleteModelEntitiesCommand,
					 * then no operation is not repeatable.
					 * @returns		true if repeatable.
					 * @implements		MiiTransaction#isRepeatable
					 *------------------------------------------------------*/
	public		boolean		isRepeatable()
		{
		return(name.equals(Mi_DUPLICATE_COMMAND_NAME) 
			|| (name.equalsIgnoreCase(Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME))
			|| (name.equalsIgnoreCase(Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME))
			|| (name.equalsIgnoreCase(Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME))
			|| (name.equalsIgnoreCase(Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME))
			|| (name.equals(Mi_CREATE_COMMAND_NAME)));
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		MiParts parts = new MiParts(partsToDelete);
		parts.append(connsToDelete);
		return(parts);
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		return(null);
		}
	public		void		getToBeDeletedConnectionsOfToBeDeletedParts(
						MiParts parts, 
						MiParts conns, 
						MiParts connSources, 
						MiParts connDests)
		{
		conns.removeAllElements();
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart part = parts.elementAt(i);
			if (part instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )part;
				if (!conns.contains(conn))
					{
					conns.addElement(conn);
					connSources.addElement(conn.getSource());
					connDests.addElement(conn.getDestination());
					srcConnPts.addElement(conn.getSourceConnPt());
					destConnPts.addElement(conn.getDestinationConnPt());
					}
				parts.removeElement(part);
				--i;
				}
			else
				{
				getConnsOfPart(parts, part, conns, connSources, connDests);
				}
			}
		}

	protected	void		getConnsOfPart(MiParts parts, MiPart part, MiParts conns, MiParts connSources, MiParts connDests)
		{
		int numConns = part.getNumberOfConnections();
		for (int i = 0; i < numConns; ++i)
			{
			boolean containedByDeletedPart = false;
			MiConnection conn = part.getConnection(i);
			for (int j = 0; j < parts.size(); ++j)
				{
				if (parts.elementAt(j).isContainerOf(conn))
					{
					containedByDeletedPart = true;
					break;
					}
				}
			if (containedByDeletedPart)
				{
				continue;
				}

			// FIX: Need to fix this so that conn source and dest ARE 
			// preserved (and restored!) but conn is NOT deleted
			// or restored. Right now, src an dest are not restored after an undo
			if (!conn.getConnectionsMustBeConnectedAtBothEnds())
				{
				boolean srcToBeDeleted = false;
				boolean destToBeDeleted = false;
				for (int j = 0; j < parts.size(); ++j)
					{
					MiPart p = parts.elementAt(j);
					if (p.isContainerOf(conn.getSource()))
						srcToBeDeleted = true;
					if (p.isContainerOf(conn.getDestination()))
						destToBeDeleted = true;
					}
				if ((!srcToBeDeleted) || (!destToBeDeleted))
					continue;
				}
			if (!conns.contains(conn))
				{
				conns.addElement(conn);
				connSources.addElement(conn.getSource());
				connDests.addElement(conn.getDestination());
				srcConnPts.addElement(conn.getSourceConnPt());
				destConnPts.addElement(conn.getDestinationConnPt());
				}
			}
		int numParts = part.getNumberOfParts();
		for (int i = 0; i < numParts; ++i)
			{
			getConnsOfPart(parts, part.getPart(i), conns, connSources, connDests);
			}
		}
	protected	void		getIndicesOfParts(
						MiParts parts, 
						IntVector indices, 
						MiParts containersOfParts)
		{
		containersOfParts.removeAllElements();
		indices.removeAllElements();
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart part = parts.elementAt(i);
			MiPart container = part.getContainer(0);
			if (container == null)
				{
MiDebug.printStackTrace(this + " Deleted PART had no container!!!!!: " + part);
				containersOfParts.addElement(getEditor());
				indices.addElement(-1);
				}
			else
				{
				containersOfParts.addElement(container);
				indices.addElement(container.getIndexOfPart(part));
				}
			}
		}
	protected	void		sortIndicesOfParts(
						MiParts parts, 
						IntVector indices, 
						MiParts containersOfParts)
		{
		if (indices.size() < 2)
			return;

		// Bubble sort it, expect usually this will already be sorted anyway
		boolean needsSorting = true;
		while (needsSorting)
			{
			needsSorting = false;
			int lastIndex = indices.elementAt(0);
			for (int i = 1; i < indices.size(); ++i)
				{
				int index = indices.elementAt(i);
				if (index < lastIndex)
					{
					needsSorting = true;
					indices.setElementAt(lastIndex, i);
					indices.setElementAt(index, i - 1);

					MiPart part = parts.elementAt(i);
					parts.setElementAt(parts.elementAt(i - 1), i);
					parts.setElementAt(part, i - 1);
					
					part = containersOfParts.elementAt(i);
					containersOfParts.setElementAt(containersOfParts.elementAt(i - 1), i);
					containersOfParts.setElementAt(part, i - 1);
					}
				else
					{
					lastIndex = index;
					}
				}
			}
		}
	protected	void		sortIndicesOfParts(
						MiParts parts, 
						IntVector indices, 
						MiParts containersOfParts,
						MiParts	connSources,
						MiParts	connDests,
						IntVector srcConnPts,
						IntVector destConnPts)
		{
		if (indices.size() < 2)
			return;

		// Bubble sort it, expect usually this will already be sorted anyway
		boolean needsSorting = true;
		while (needsSorting)
			{
			needsSorting = false;
			int lastIndex = indices.elementAt(0);
			for (int i = 1; i < indices.size(); ++i)
				{
				int index = indices.elementAt(i);
				if (index < lastIndex)
					{
					needsSorting = true;
					indices.setElementAt(lastIndex, i);
					indices.setElementAt(index, i - 1);

					MiPart part = parts.elementAt(i);
					parts.setElementAt(parts.elementAt(i - 1), i);
					parts.setElementAt(part, i - 1);
					
					part = containersOfParts.elementAt(i);
					containersOfParts.setElementAt(containersOfParts.elementAt(i - 1), i);
					containersOfParts.setElementAt(part, i - 1);

					part = 	connSources.elementAt(i);
					connSources.setElementAt(connSources.elementAt(i - 1), i);
					connSources.setElementAt(part, i - 1);

					part = 	connDests.elementAt(i);
					connDests.setElementAt(connDests.elementAt(i - 1), i);
					connDests.setElementAt(part, i - 1);

					int connPt = srcConnPts.elementAt(i);
					srcConnPts.setElementAt(srcConnPts.elementAt(i - 1), i);
					srcConnPts.setElementAt(connPt, i - 1);

					connPt = destConnPts.elementAt(i);
					destConnPts.setElementAt(destConnPts.elementAt(i - 1), i);
					destConnPts.setElementAt(connPt, i - 1);
					}
				else
					{
					lastIndex = index;
					}
				}
			}
		}
	protected	MiParts		duplicate(
						MiEditor editor, 
						MiParts partsToCopy, 
						String cmd, 
						int numberOfTimesToRepeat, 
						boolean selectAddedParts)
		{
		MiParts partsToDuplicate = new MiParts();
		MiParts morePartsToDuplicate = partsToCopy;
		MiParts leftMostBottomRowPart = null;

		editor.deSelectAll();

		for (int i = 0; i < numberOfTimesToRepeat; ++i)
			{
			if (cmd.equalsIgnoreCase(Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME))
				{
				if (leftMostBottomRowPart == null)
					{
					leftMostBottomRowPart = partsToCopy;
					++numberOfTimesToRepeat;
					}
				else
					{
					morePartsToDuplicate = MiUtility.makeCopyOfNetwork(leftMostBottomRowPart);
					translateGroupOfParts(editor, morePartsToDuplicate, Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME);
					partsToDuplicate.append(morePartsToDuplicate);
					leftMostBottomRowPart = morePartsToDuplicate;
					}

				for (int j = 0; j < numberOfTimesToRepeat - 1; ++j)
					{
					morePartsToDuplicate = MiUtility.makeCopyOfNetwork(morePartsToDuplicate);
					translateGroupOfParts(editor, morePartsToDuplicate, Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME);
					partsToDuplicate.append(morePartsToDuplicate);
					}

				}
			else
				{
				morePartsToDuplicate = MiUtility.makeCopyOfNetwork(morePartsToDuplicate);
				translateGroupOfParts(editor, morePartsToDuplicate, cmd);
				partsToDuplicate.append(morePartsToDuplicate);
				}
			}


		if ((partsToDuplicate.size() > 0) && (numberOfTimesToRepeat > 0))
			{
			MiNestedTransaction nestedTransaction = new MiNestedTransaction(Mi_DUPLICATE_DISPLAY_NAME);
			MiSystem.getTransactionManager().startTransaction(nestedTransaction);

/*
			if (selectAddedParts)
				{
				editor.deSelectAll();
				}
*/
			for (int i = 0; i < partsToDuplicate.size(); ++i)
				{
				editor.appendItem(partsToDuplicate.elementAt(i));
				if (selectAddedParts)
					{
					editor.selectAdditional(partsToDuplicate.elementAt(i));
					}
				}

			processCommand(editor, partsToDuplicate, cmd, false, false, true, numberOfTimesToRepeat, selectAddedParts);

			MiSystem.getTransactionManager().commitTransaction(nestedTransaction);

			MiSelectionManager.notifyAboutNumberOfShapesSelected(editor);
			}
		return(partsToDuplicate);
		}
/***
	protected	MiParts		duplicate(MiEditor editor, MiParts partsToCopy, String cmd, int numberOfTimesToRepeat)
		{
		MiParts partsToDuplicate = new MiParts();
		MiParts rightParts = partsToCopy;
		MiParts bottomParts = partsToCopy;
		MiParts diagonalParts = partsToCopy;

		for (int i = 0; i < numberOfTimesToRepeat; ++i)
			{
			if (cmd.equalsIgnoreCase(Mi_DUPLICATE_GROW_BOTTOMRIGHT_COMMAND_NAME))
				{
				MiParts morePartsToDuplicate = MiUtility.makeCopyOfNetwork(rightParts);
				translateGroupOfParts(editor, morePartsToDuplicate, Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME);
				partsToDuplicate.append(morePartsToDuplicate);
				if (rightParts == partsToCopy)
					{
					rightParts = new MiParts();
					}
				rightParts.removeAllElements();
				rightParts.append(morePartsToDuplicate);

MiDebug.println("bottomParts = " + bottomParts);
				morePartsToDuplicate = MiUtility.makeCopyOfNetwork(bottomParts);
MiDebug.println("morePartsToDuplicate = " + morePartsToDuplicate);
				translateGroupOfParts(editor, morePartsToDuplicate, Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME);
				partsToDuplicate.append(morePartsToDuplicate);
				if (bottomParts == partsToCopy)
					{
					bottomParts = new MiParts();
					}
				bottomParts.removeAllElements();
				bottomParts.append(morePartsToDuplicate);

				morePartsToDuplicate = MiUtility.makeCopyOfNetwork(diagonalParts);
				translateGroupOfParts(editor, morePartsToDuplicate, Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME);
				partsToDuplicate.append(morePartsToDuplicate);

				rightParts.append(morePartsToDuplicate);
				bottomParts.append(morePartsToDuplicate);
				diagonalParts = morePartsToDuplicate;
				}
			else
				{
				MiParts morePartsToDuplicate = MiUtility.makeCopyOfNetwork(rightParts);
MiDebug.println("Calling translateGroupOfParts: " + cmd + " " + morePartsToDuplicate);
				translateGroupOfParts(editor, morePartsToDuplicate, cmd);
				partsToDuplicate.append(morePartsToDuplicate);
				rightParts = morePartsToDuplicate;
				}
			}


		if ((partsToDuplicate.size() > 0) && (numberOfTimesToRepeat > 0))
			{
			MiNestedTransaction nestedTransaction = new MiNestedTransaction(Mi_DUPLICATE_DISPLAY_NAME);
			MiSystem.getTransactionManager().startTransaction(nestedTransaction);

			for (int i = 0; i < partsToDuplicate.size(); ++i)
				{
				editor.appendItem(partsToDuplicate.elementAt(i));
				}

			processCommand(editor, partsToDuplicate, cmd, false, false, true, numberOfTimesToRepeat);

			MiSystem.getTransactionManager().commitTransaction(nestedTransaction);

			MiSelectionManager.notifyAboutNumberOfShapesSelected(editor);
			}
		return(partsToDuplicate);
		}
**/
	protected	void		translateGroupOfParts(MiEditor editor, MiParts parts, String command)
		{
		MiVector translation = getTranslationForGroupOfParts(editor, parts, command);
		for (int i = 0; i < parts.size(); ++i)
			{
//MiDebug.println("translating part: " + parts.elementAt(i) + "\n by amount: " + translation);
			parts.elementAt(i).translate(translation);
			}
		}
	protected	MiVector	getTranslationForGroupOfParts(MiEditor editor, MiParts parts, String command)
		{
		MiVector translation = new MiVector(10,10);
		MiSize majorSnapGridSize = editor.getSnapManager().getGrid().getMajorGridSize();

		MiBounds partsBounds = new MiBounds();
		MiBounds tmpBounds = new MiBounds();
		for (int i = 0; i < parts.size(); ++i)
			{
			partsBounds.union(parts.elementAt(i).getBounds(tmpBounds));
			}
		

		if (command.equalsIgnoreCase(Mi_DUPLICATE_COMMAND_NAME))
			{
			translation.set(partsBounds.getWidth()/2, partsBounds.getHeight()/2);
			}
		else if (command.equalsIgnoreCase(Mi_DUPLICATE_TO_RIGHT_COMMAND_NAME))
			{
			translation.set(partsBounds.getWidth() + majorSnapGridSize.getWidth(), 0);
			}
		else if (command.equalsIgnoreCase(Mi_DUPLICATE_TO_BOTTOM_COMMAND_NAME))
			{
			translation.set(0, -partsBounds.getHeight() - majorSnapGridSize.getHeight());
			}
		else if (command.equalsIgnoreCase(Mi_DUPLICATE_TO_BOTTOMRIGHT_COMMAND_NAME))
			{
			translation.set(
				partsBounds.getWidth() + majorSnapGridSize.getWidth(), 
				-partsBounds.getHeight() - majorSnapGridSize.getHeight());
			}
		// Round translation to nearest largest grid size
		translation.x = ((int )((translation.x + majorSnapGridSize.getWidth() - 0.001)
			/majorSnapGridSize.getWidth())) 
			* majorSnapGridSize.getWidth();

		boolean signOfYIsNegative = translation.y < 0;
		translation.y = ((int )((Math.abs(translation.y) + majorSnapGridSize.getHeight() - 0.001)
			/majorSnapGridSize.getHeight())) 
			* majorSnapGridSize.getHeight();

		if (signOfYIsNegative)
			{
			translation.y = -translation.y;
			}

		return(translation);
		}
	public		String		toString()
		{
		String str = super.toString() + "->";
		if (toDelete)
			{
			str += "Undoable delete of: ";
			}
		else
			{	
			str += "Undoable creation of: ";
			}
		str += "(Connections: \n" + connsToDelete + ")";
		str += "(Parts: \n" + partsToDelete + ")";
		return(str);
		}
	}

