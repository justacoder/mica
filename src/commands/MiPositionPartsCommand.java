
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
public class MiPositionPartsCommand extends MiCommandHandler implements MiiTransaction, MiiDisplayNames, MiiCommandNames
	{
	private		DoubleVector	originalXYPoints	= new DoubleVector();
	private		DoubleVector	newXYPoints		= new DoubleVector();
	private		IntVector 	originalNumPtsEachConnection = new IntVector();
	private		IntVector 	newNumPtsEachConnection	= new IntVector();
	private		MiParts		positionedParts;
	private		String		Mi_POSITION_PARTS_COMMAND_NAME	= "positionParts";
	private		String		Mi_POSITION_PARTS_DISPLAY_NAME	= "Position Parts";


	public				MiPositionPartsCommand()
		{
		}

	public				MiPositionPartsCommand(
						MiEditor editor, MiPart positionedPart, 
						DoubleVector originalXYPoints, DoubleVector newXYPoints, 
						IntVector originalNumPtsEachConnection, IntVector newNumPtsEachConnection)
		{
		this(editor, new MiParts(positionedPart), originalXYPoints, newXYPoints, 
			originalNumPtsEachConnection, newNumPtsEachConnection);
		}

					/**
	 				 * @param editor		the editor 
	 				 * @param posiitonedParts	the list of nopdes and connections
	 				 * @param originalXYPoints	the centers of all nodes and the 
					 *				positions of all points of all 
					 *				connections in the list of positionedParts
					 *				(excluding the first and/or last points
					 *				of connections that are connected to some
					 *				node.
	 				 * @param newXYPoints		the centers of all nodes and the 
					 *				positions of all points of all 
					 *				connections in the list of positionedParts
					 *				(excluding the first and/or last points
					 *				of connections that are connected to some
					 *				node.
					 **/
	public				MiPositionPartsCommand(
						MiEditor editor, MiParts positionedParts, 
						DoubleVector originalXYPoints, DoubleVector newXYPoints,
						IntVector originalNumPtsEachConnection, IntVector newNumPtsEachConnection)
		{
		setTargetOfCommand(editor);
		this.originalXYPoints.append(originalXYPoints);
		this.newXYPoints.append(newXYPoints);
		this.originalNumPtsEachConnection.append(originalNumPtsEachConnection);
		this.newNumPtsEachConnection.append(newNumPtsEachConnection);
		this.positionedParts = new MiParts(positionedParts);
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		setOriginalPositions(DoubleVector xyPoints)
		{
		originalXYPoints = new DoubleVector();
		originalXYPoints.append(xyPoints);
		}
	public		DoubleVector	getOriginalPositions()
		{
		return(new DoubleVector(originalXYPoints));
		}
	public		void		setNewPositions(DoubleVector xyPoints)
		{
		newXYPoints = new DoubleVector();
		newXYPoints.append(xyPoints);
		}
	public		DoubleVector	getNewPositions()
		{
		return(new DoubleVector(newXYPoints));
		}
	public		void		processCommand(String cmd)
		{
		processCommand(getEditor(), positionedParts, originalXYPoints, newXYPoints, originalNumPtsEachConnection, newNumPtsEachConnection);
		}

	public		void		processCommand(MiEditor editor, MiParts positionedParts, 
						DoubleVector originalXYPoints, DoubleVector newXYPoints,
						IntVector originalNumPtsEachConnection, IntVector newNumPtsEachConnection)
		{
		doit(editor, positionedParts, newXYPoints, newNumPtsEachConnection);

		MiSystem.getTransactionManager().appendTransaction(
			new MiPositionPartsCommand(editor, positionedParts, originalXYPoints, newXYPoints, 
				originalNumPtsEachConnection, newNumPtsEachConnection));
		}
	public static	void		getPositions(MiParts components, DoubleVector positions, IntVector numPointsEachConnection)
		{
		MiPoint tmpPoint = new MiPoint();
		for (int i = 0; i < components.size(); ++i)
			{
			MiPart part = components.elementAt(i);
			if (part instanceof MiConnection)
				{
				// Skip first and last points if they are connected
				int startIndex = 1;
				if (((MiConnection )part).getSource() == null)
					{
					startIndex = 0;
					}
				int endIndex = part.getNumberOfPoints() - 1;
				if (((MiConnection )part).getDestination() == null)
					{
					endIndex += 1;
					}
				for (int j = startIndex; j < endIndex; ++j)
					{
					part.getPoint(j, tmpPoint);
					positions.addElement(tmpPoint.x);
					positions.addElement(tmpPoint.y);
					}
				numPointsEachConnection.addElement(part.getNumberOfPoints());
				}
			else 
				{
				positions.addElement(part.getCenterX());
				positions.addElement(part.getCenterY());
				}
			}
		}

	protected	void		doit(MiEditor editor, MiParts parts, DoubleVector xyPoints, IntVector numPtsEachConnection)
		{
		if (xyPoints.size() == 0)
			{
			return;
			}

		HashMap hashMap = new HashMap();
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			if ((obj instanceof MiConnection) && (obj.getLayout() != null))
				{
				hashMap.put(obj,new Boolean(obj.getLayout().isEnabled()));
				obj.getLayout().setEnabled(false);
				}
			}
		
		int pointNum = 0;
		int connNum = 0;
//MiDebug.println("\ndo itttttttttttt");
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
//MiDebug.println("\npart = " + obj);
//MiDebug.println("pointNum = " + pointNum);
			if ((obj instanceof MiConnection) && (numPtsEachConnection.size() > 0))
				{
//MiDebug.println("\nconn = " + obj);
//MiDebug.println("connNum = " + connNum);
//MiDebug.println("numPtsEachConnection = " + numPtsEachConnection.elementAt(connNum));
				while (obj.getNumberOfPoints() > numPtsEachConnection.elementAt(connNum))
					{
					obj.removePoint(1);
					}
				while (obj.getNumberOfPoints() < numPtsEachConnection.elementAt(connNum))
					{
					obj.insertPoint(new MiPoint(), 1);
					}
				int startIndex = 1;
				if (((MiConnection )obj).getSource() == null)
					{
					startIndex = 0;
					}
				int endIndex = obj.getNumberOfPoints() - 1;
				if (((MiConnection )obj).getDestination() == null)
					{
					endIndex += 1;
					}
//MiDebug.println("conn now = " + obj);
//MiDebug.println("startIndex now = " + startIndex);
//MiDebug.println("endIndex now = " + endIndex);
				for (int j = startIndex; j < endIndex; ++j)
					{
					obj.setPoint(j, xyPoints.elementAt(pointNum), xyPoints.elementAt(pointNum + 1));
					pointNum += 2;
					}
				++connNum;
				}
			else if (!(obj instanceof MiConnection))
				{
//MiDebug.println("pointNum = " + pointNum);
				obj.setCenter(xyPoints.elementAt(pointNum), xyPoints.elementAt(pointNum + 1));
				pointNum += 2;
				}
			}
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
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
		return(Mi_POSITION_PARTS_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_POSITION_PARTS_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), positionedParts, newXYPoints, newNumPtsEachConnection);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), positionedParts, originalXYPoints, originalNumPtsEachConnection);
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
		return(new MiParts(positionedParts));
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

