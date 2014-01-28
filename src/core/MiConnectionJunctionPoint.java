
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
public class MiConnectionJunctionPoint extends MiPart implements MiiActionHandler
	{
	private static	MiConnectionPointManager connectionPointManager;
	private static	MiPart		connectionPointLook;
	private 	boolean		currentlyHandlingDisconnection;
	private 	boolean		automaticDeleteIfLessThan3TracesConnected = true;
	private static 	boolean		globalAutomaticDeleteIfLessThan3TracesConnected = true;


	public				MiConnectionJunctionPoint()
		{
		this(new MiPoint(0,0));
		}
	public				MiConnectionJunctionPoint(MiPoint pt)
		{
		super.setCenter(pt);
		setConnectionPointManager(connectionPointManager);
		setSelectable(false);

		appendActionHandler(this, Mi_DISCONNECT_ACTION);
		}

	public static	void		setGlobalAutomaticDeleteIfLessThan3TracesConnected(boolean flag)
		{
		globalAutomaticDeleteIfLessThan3TracesConnected = flag;
		}
	public static	boolean		getGlobalAutomaticDeleteIfLessThan3TracesConnected()
		{
		return(globalAutomaticDeleteIfLessThan3TracesConnected);
		}
	public		void		setAutomaticDeleteIfLessThan3TracesConnected(boolean flag)
		{
		this.automaticDeleteIfLessThan3TracesConnected = flag;
		}
	public		boolean		getAutomaticDeleteIfLessThan3TracesConnected()
		{
		return(automaticDeleteIfLessThan3TracesConnected);
		}

	public static	void		setConnectionPointLook(MiPart look)
		{
		connectionPointManager.setLocalLook(look);
		}
	public static	MiPart		getConnectionPointLook()
		{
		return(connectionPointManager.getLocalLook());
		}
	public		int		getConnectionPointNumber()
		{
		return(Mi_CENTER_LOCATION);
		}
	static	{
		connectionPointManager = new MiConnectionPointManager();
		connectionPointLook = new MiCircle(0,0,3);
		connectionPointLook.setColor(MiColorManager.blue);
		connectionPointManager.setLocalLook(connectionPointLook);
		connectionPointManager.setLocallyAlwaysVisible(true);

		MiManagedPoint managedPoint = new MiManagedPoint(Mi_CENTER_LOCATION);
		connectionPointManager.appendManagedPoint(managedPoint);
		}

					/**------------------------------------------------------
	 				 * Process the given action.
					 * Actions handled:
					 *	Mi_DISCONNECT_ACTION
					 * @param action	the action to process.
					 * @return 		true if can propogate the action to
					 *			other handlers.
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if ((globalAutomaticDeleteIfLessThan3TracesConnected)
			&& (action.hasActionType(Mi_DISCONNECT_ACTION)) 
			&& (!MiSystem.getTransactionManager().isExecuting()))
			{
			// ---------------------------------------------------------------
			// A connect was removed, if we have less than 3 connections to this
			// then if this has 2 connections then replace with one connection,
			// and in any case just remove this...
			// ---------------------------------------------------------------

			MiConnectionJunctionPoint target = (MiConnectionJunctionPoint )action.getActionSource();
			target.fixupIfFewerThan3Connections();
			}
		return(true);
		}
	/**
	 * @return true if this junction pt was deleted and potentially deleted 2 connections as well
	 **/
	public		boolean		fixupIfFewerThan3Connections()
		{
		if ((automaticDeleteIfLessThan3TracesConnected)
			&& (getNumberOfConnections() < 3))
			{
			if (currentlyHandlingDisconnection)
				{
				return(false);
				}
			currentlyHandlingDisconnection = true;

			MiEditor editor = getContainingEditor();

			if ((getNumberOfConnections() == 2) && (getContainer(0) != null))
				{
				MiPart source = null;
				MiPart destination = null;
				int sourceConnPt = Mi_CENTER_LOCATION;
				int destinationConnPt = Mi_CENTER_LOCATION;

				MiConnection conn1 = getConnection(0);
				MiConnection conn2 = getConnection(1);
				MiMultiPointShape conn1gr = (MiMultiPointShape )conn1.getGraphics();
				MiMultiPointShape conn2gr = (MiMultiPointShape )conn2.getGraphics();

				if (conn1 != conn2)
					{
					MiConnection newConn = (MiConnection )conn1.copy();
					MiMultiPointShape newConnGr = (MiMultiPointShape )newConn.getGraphics();

					while (newConnGr.getNumberOfPoints() > 0)
						{
						newConnGr.removePoint(0);
						}

					boolean origILNotifications = newConn.getIncomingInvalidLayoutNotificationsEnabled();
					newConn.setIncomingInvalidLayoutNotificationsEnabled(false);

					// Try and preserve directionality and path
					if (conn1.getSource() == this)
						{
						destination = conn1.getDestination();
						destinationConnPt = conn1.getDestinationConnPt();
						if (conn2.getSource() == this)
							{
							source = conn2.getDestination();
							sourceConnPt = conn2.getDestinationConnPt();

							// Make sure the new, replacement conn, follows same path
							// as the 2 originals
							for (int i = conn2gr.getNumberOfPoints() - 1; i >= 0; --i)
								{
								newConnGr.appendPoint(
									conn2gr.getPointX(i), conn2gr.getPointY(i));
								}
							for (int i = 0; i < conn1gr.getNumberOfPoints(); ++i)
								{
								newConnGr.appendPoint(
									conn1gr.getPointX(i), conn1gr.getPointY(i));
								}
							}
						else
							{
							source = conn2.getSource();
							sourceConnPt = conn2.getSourceConnPt();

							// Make sure the new, replacement conn, follows same path
							// as the 2 originals
							for (int i = 0; i < conn2gr.getNumberOfPoints(); ++i)
								{
								newConnGr.appendPoint(
									conn2gr.getPointX(i), conn2gr.getPointY(i));
								}
							for (int i = 0; i < conn1gr.getNumberOfPoints(); ++i)
								{
								newConnGr.appendPoint(
									conn1gr.getPointX(i), conn1gr.getPointY(i));
								}
							}
						}
					else
						{
						source = conn1.getSource();
						sourceConnPt = conn1.getSourceConnPt();
						if (conn2.getSource() == this)
							{
							destination = conn2.getDestination();
							destinationConnPt = conn2.getDestinationConnPt();

							// Make sure the new, replacement conn, follows same path
							// as the 2 originals
							for (int i = 0; i < conn1gr.getNumberOfPoints(); ++i)
								{
								newConnGr.appendPoint(
									conn1gr.getPointX(i), conn1gr.getPointY(i));
								}
							for (int i = 0; i < conn2gr.getNumberOfPoints(); ++i)
								{
								newConnGr.appendPoint(
									conn2gr.getPointX(i), conn2gr.getPointY(i));
								}
							}
						else
							{
							destination = conn2.getSource();
							destinationConnPt = conn2.getSourceConnPt();

							// Make sure the new, replacement conn, follows same path
							// as the 2 originals
							for (int i = 0; i < conn1gr.getNumberOfPoints(); ++i)
								{
								newConnGr.appendPoint(
									conn1gr.getPointX(i), conn1gr.getPointY(i));
								}
							for (int i = conn2gr.getNumberOfPoints() - 1; i >= 0; --i)
								{
								newConnGr.appendPoint(
									conn2gr.getPointX(i), conn2gr.getPointY(i));
								}
							}
						}

//MiDebug.println("\n\nconn1 = " + conn1);
//MiDebug.println("conn2 = " + conn2);
//MiDebug.println("newConn = " + newConn);

					newConn.setSourceConnPt(sourceConnPt);
					newConn.setSource(source);
					newConn.setDestinationConnPt(destinationConnPt);
					newConn.setDestination(destination);

					getContainer(0).appendPart(newConn);

					MiSystem.getTransactionManager().appendTransaction(
						new MiDeletePartsCommand(editor, newConn, false));

					newConn.setIncomingInvalidLayoutNotificationsEnabled(origILNotifications);

					MiSystem.getTransactionManager().appendTransaction(
						new MiDeletePartsCommand(editor, conn1, true));
					MiSystem.getTransactionManager().appendTransaction(
						new MiDeletePartsCommand(editor, conn2, true));

					conn1.dispatchAction(Mi_DELETE_ACTION);
					conn1.removeSelf();
					conn2.dispatchAction(Mi_DELETE_ACTION);
					conn2.removeSelf();

//MiDebug.println(this + "\nDeleteing conn1 and conn2\nconn1 = " + conn1);
//MiDebug.println("conn2 = " + conn2);
//MiDebug.println("newConn = " + newConn);
					}
				}
			MiSystem.getTransactionManager().appendTransaction(
				new MiDeletePartsCommand(editor, this, true));

			dispatchAction(Mi_DELETE_ACTION);
			removeSelf();

			currentlyHandlingDisconnection = false;
			return(true);
			}
		return(false);
		}
	}


