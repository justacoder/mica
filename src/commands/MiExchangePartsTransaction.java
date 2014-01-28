
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
import com.swfm.mica.util.DoubleVector;
import com.swfm.mica.util.IntVector;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiExchangePartsTransaction extends MiCommandHandler implements MiiTransaction, MiiDisplayNames, MiiCommandNames
	{
	private		MiParts		originalParts;
	private		MiParts		newParts;
	private		MiPart[]	origConnToSourceMap	= new MiPart[0];
	private		MiPart[]	origConnToDestMap	= new MiPart[0];
	private		int[]		origConnToSrcConnPt	= new int[0];
	private		int[]		origConnToDestConnPt	= new int[0];
	private		MiPart[]	newConnToSourceMap	= new MiPart[0];
	private		MiPart[]	newConnToDestMap	= new MiPart[0];
	private		int[]		newConnToSrcConnPt	= new int[0];
	private		int[]		newConnToDestConnPt	= new int[0];
	private		String		Mi_EXCHANGE_PARTS_COMMAND_NAME	= "exchangeParts";
	private		String		Mi_EXCHANGE_PARTS_DISPLAY_NAME	= "Exchange Parts";


	public				MiExchangePartsTransaction()
		{
		}


					/**
	 				 * @param editor		the editor 
	 				 * @param originalParts		the list of nodes and connections
	 				 * @param newParts		the list of the new nodes and connections
					 **/
	public				MiExchangePartsTransaction(
						MiEditor editor, 
						MiParts originalParts, 
						MiParts newParts)
		{
		setTargetOfCommand(editor);
		setOriginalParts(originalParts);
		setNewParts(newParts);
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		setOriginalParts(MiParts parts)
		{
		originalParts = new MiParts();
		originalParts.append(parts);

		int connNum = 0;
		for (int i = 0; i < originalParts.size(); ++i)
			{
			if (originalParts.elementAt(i) instanceof MiConnection)
				{
				++connNum;
				}
			}

		origConnToSourceMap = new MiPart[connNum];
		origConnToDestMap = new MiPart[connNum];
		origConnToSrcConnPt = new int[connNum];
		origConnToDestConnPt = new int[connNum];

		connNum = 0;
		for (int i = 0; i < originalParts.size(); ++i)
			{
			if (originalParts.elementAt(i) instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )originalParts.elementAt(i);

				origConnToSourceMap[connNum] = conn.getSource();
				origConnToDestMap[connNum] = conn.getDestination();
				origConnToSrcConnPt[connNum] = conn.getSourceConnPt();
				origConnToDestConnPt[connNum] = conn.getDestinationConnPt();

				++connNum;
				}
			}
		}
	public		MiParts		getOriginalParts()
		{
		return(new MiParts(originalParts));
		}
	public		void		setNewParts(MiParts parts)
		{
		newParts = new MiParts();
		newParts.append(parts);

		int connNum = 0;
		for (int i = 0; i < newParts.size(); ++i)
			{
			if (newParts.elementAt(i) instanceof MiConnection)
				{
				++connNum;
				}
			}

		newConnToSourceMap = new MiPart[connNum];
		newConnToDestMap = new MiPart[connNum];
		newConnToSrcConnPt = new int[connNum];
		newConnToDestConnPt = new int[connNum];

		connNum = 0;
		for (int i = 0; i < newParts.size(); ++i)
			{
			if (newParts.elementAt(i) instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )newParts.elementAt(i);

				newConnToSourceMap[connNum] = conn.getSource();
				newConnToDestMap[connNum] = conn.getDestination();
				newConnToSrcConnPt[connNum] = conn.getSourceConnPt();
				newConnToDestConnPt[connNum] = conn.getDestinationConnPt();

				++connNum;
				}
			}
		}
	public		MiParts		getNewParts()
		{
		return(new MiParts(newParts));
		}
	public		void		processCommand(String cmd)
		{
		processCommand(getEditor(), originalParts, newParts);
		}

	public		void		processCommand(MiEditor editor, MiParts originalParts, MiParts newParts)
		{
		doit(editor, originalParts, newParts, true);

		MiSystem.getTransactionManager().appendTransaction(
			new MiExchangePartsTransaction(editor, originalParts, newParts));
		}
	protected	void		doit(MiEditor editor, MiParts originalParts, MiParts newParts, boolean reallyDoIt)
		{
		HashMap hashMap = new HashMap();
		for (int i = 0; i < newParts.size(); ++i)
			{
			MiPart obj = newParts.elementAt(i);
			if ((obj instanceof MiConnection) && (obj.getLayout() != null))
				{
				hashMap.put(obj,new Boolean(obj.getLayout().isEnabled()));
				obj.getLayout().setEnabled(false);
				}
			}
		
		for (int i = 0; i < originalParts.size(); ++i)
			{
			MiPart obj = originalParts.elementAt(i);
			obj.removeSelf();
			}

		for (int i = 0; i < newParts.size(); ++i)
			{
			MiPart obj = newParts.elementAt(i);
			if (!(obj instanceof MiConnection))
				{
				editor.appendItem(obj);
				}
			}
		int connNum = 0;
		for (int i = 0; i < newParts.size(); ++i)
			{
			MiPart obj = newParts.elementAt(i);
			if (obj instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )obj;
				editor.appendItem(conn);
				if (reallyDoIt)
					{
					MiPart src = newConnToSourceMap[connNum];
					MiPart dest = newConnToDestMap[connNum];
					int srcConnPt = newConnToSrcConnPt[connNum];
					int destConnPt = newConnToDestConnPt[connNum];

					conn.setSourceConnPt(srcConnPt);
					conn.setDestinationConnPt(destConnPt);
					conn.setSource(src);
					conn.setDestination(dest);
					}
				else
					{
					MiPart src = origConnToSourceMap[connNum];
					MiPart dest = origConnToDestMap[connNum];
					int srcConnPt = origConnToSrcConnPt[connNum];
					int destConnPt = origConnToDestConnPt[connNum];

					conn.setSourceConnPt(srcConnPt);
					conn.setDestinationConnPt(destConnPt);
					conn.setSource(src);
					conn.setDestination(dest);
					}

				++connNum;
				}
			}

		for (int i = 0; i < newParts.size(); ++i)
			{
			MiPart obj = newParts.elementAt(i);
			if ((obj instanceof MiConnection) && (obj.getLayout() != null))
				{
				obj.getLayout().setEnabled(((Boolean )hashMap.get(obj)).booleanValue());
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
		return(Mi_EXCHANGE_PARTS_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_EXCHANGE_PARTS_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), originalParts, newParts, true);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), newParts, originalParts, false);
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
		MiParts parts = new MiParts(newParts);
		parts.append(originalParts);
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
	}

