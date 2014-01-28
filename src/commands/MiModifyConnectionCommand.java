
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
import com.swfm.mica.util.FastVector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiModifyConnectionCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames, MiiPropertyTypes
	{
	class MiConnectionData
		{
		MiPart		src;
		MiPart		dest;
		int		srcConnPt;
		int		destConnPt;
		MiPart		graphics;
		MiPoint[]	points;

		public				MiConnectionData(MiConnection connection)
			{
			src		= connection.getSource();
			dest		= connection.getDestination();
			srcConnPt	= connection.getSourceConnPt();
			destConnPt	= connection.getDestinationConnPt();
			graphics	= connection.getGraphics();
			points		= new MiPoint[connection.getNumberOfPoints()];

			for (int i = 0; i < connection.getNumberOfPoints(); ++i)
				{
				points[i] = connection.getPoint(i);
				}
			}
		public		void		apply(MiConnection connection)
			{
			connection.setSource(null);
			connection.setSourceConnPt(srcConnPt);
			connection.setSource(src);
			connection.setDestination(null);
			connection.setDestinationConnPt(destConnPt);
			connection.setDestination(dest);

			if (connection.getGraphics() != graphics)
				connection.setGraphics(graphics);

			if (connection.getGraphics().getNumberOfPoints() != points.length)
				connection.getGraphics().setNumberOfPoints(points.length);

			for (int i = 0; i < points.length; ++i)
				{
				connection.getGraphics().setPoint(i, points[i]);
				connection.refreshEndPoints();
				}
			}
		}

	private		MiConnectionData before;
	private		MiConnectionData after;
	private		MiConnection	connection;


	public				MiModifyConnectionCommand()
		{
		}

	public		void		setConnectionBefore(MiConnection connection)
		{
		this.connection = connection;
		before = new MiConnectionData(connection);
		}

	public		void		setConnectionAfter(MiConnection connection)
		{
		this.connection = connection;
		after = new MiConnectionData(connection);
		}

	protected			MiModifyConnectionCommand(
						MiEditor editor, MiConnection connection, 
						MiConnectionData before, MiConnectionData after)
		{
		setTargetOfCommand(editor);
		this.connection = connection;
		this.before = before;
		this.after = after;
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}

	public		void		processCommand(String arg)
		{
		if ((arg != null)
			&& (arg.equalsIgnoreCase(Mi_MODIFY_CONNECTION_COMMAND_NAME)))
			{
			processCommand(getEditor(), connection);
			}
		else
			{
			throw new IllegalArgumentException(this + ": Unable to process command: " + arg);
			}
		}

	public		void		processCommand(MiEditor editor, MiConnection connection)
		{
		doit(editor, connection, true);
		MiSystem.getTransactionManager().appendTransaction(
			new MiModifyConnectionCommand(editor, connection, before, after));
		}
	public		void		processModification(MiEditor editor)
		{
		MiSystem.getTransactionManager().appendTransaction(
			new MiModifyConnectionCommand(editor, connection, before, after));
		}
	protected	void		doit(MiEditor editor, MiConnection connection, boolean toDoIt)
		{
		if (toDoIt)
			after.apply(connection);
		else
			before.apply(connection);
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
		return(Mi_MODIFY_CONNECTION_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_MODIFY_CONNECTION_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), connection, true);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), connection, false);
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
		return(new MiParts(connection));
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		MiParts parts = new MiParts();
		if (after.src != null)
			parts.addElement(after.src);
		if (after.dest != null)
			parts.addElement(after.dest);
			
		if ((before.src != null) && (before.src != after.src))
			parts.addElement(before.src);
		if ((before.dest != null) && (before.dest != after.dest))
			parts.addElement(before.dest);

		return(parts);
		}
	}

