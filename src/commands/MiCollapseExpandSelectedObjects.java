
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
public class MiCollapseExpandSelectedObjects extends MiCommandHandler implements MiiCommandNames
	{
	private static final String	COLLAPSE_POINT_ID		= "collapsePointID";
	private		String		connectionType;

	public final static int		USE_PART_OF_RELATION			= 1;
	public final static int		USE_ALL_CONNECTED_TO_RELATIONS		= 2;
	public final static int		USE_SPECIFIC_CONNECTED_TO_RELATION	= 3;
	private		int		typeOfRelationWithSelectedObjects	= USE_ALL_CONNECTED_TO_RELATIONS;

	public final static int		DO_ALL_DISTANT_RELATIVES		= 1;
	public final static int		DO_ALL_CHILDREN				= 2;
	public final static int		DO_ALL_PARENTS				= 3;
	private		int		distanceToSelectedObjects		= DO_ALL_CHILDREN;

	private		boolean		expandAllPreviousCollapsePoints;



	public				MiCollapseExpandSelectedObjects()
		{
		}

	public		void		setTypeOfRelationWithSelectedObjects(int type)
		{
		typeOfRelationWithSelectedObjects = type;
		}
	public		int		getTypeOfRelationWithSelectedObjects()
		{
		return(typeOfRelationWithSelectedObjects);
		}
	public		void		setDistanceToSelectedObjects(int distance)
		{
		distanceToSelectedObjects = distance;
		}
	public		int		getDistanceToSelectedObjects()
		{
		return(distanceToSelectedObjects);
		}
	public		void		setTypeOfConnectionRelation(String type)
		{
		connectionType = type;
		}
	public		String		getTypeOfConnectionRelation()
		{
		return(connectionType);
		}

	public		void		processCommand(String cmd)
		{
		MiPart group;
		MiEditor editor = (MiEditor )getTargetOfCommand();
		MiParts selectedObjects = new MiParts();
		if (cmd.equalsIgnoreCase(Mi_COLLAPSE_COMMAND_NAME)) 
			{	
			editor.getSelectedParts(selectedObjects);
			for (int i = 0; i < selectedObjects.size(); ++i)
				{
				MiPart part = selectedObjects.elementAt(i);
				part.setResource(COLLAPSE_POINT_ID, "yes");
				switch (typeOfRelationWithSelectedObjects)
					{
					case USE_PART_OF_RELATION :
						doUsingPartOfRelation(part, false);
						break;

					case USE_ALL_CONNECTED_TO_RELATIONS :
						doUsingConnectedToRelations(
							part,
							distanceToSelectedObjects,
							false,
							true);
						break;
					case USE_SPECIFIC_CONNECTED_TO_RELATION :
						doUsingConnectedToRelations(
							part,
							distanceToSelectedObjects,
							false,
							false);
						break;
					default:
						break;
					}
				}
			}
		else if (cmd.equalsIgnoreCase(Mi_EXPAND_COMMAND_NAME)) 
			{	
			editor.getSelectedParts(selectedObjects);
			for (int i = 0; i < selectedObjects.size(); ++i)
				{
				MiPart part = selectedObjects.elementAt(i);
				part.removeResource(COLLAPSE_POINT_ID);
				switch (typeOfRelationWithSelectedObjects)
					{
					case USE_PART_OF_RELATION :
						doUsingPartOfRelation(part, true);
						break;

					case USE_ALL_CONNECTED_TO_RELATIONS :
						doUsingConnectedToRelations(
							part,
							distanceToSelectedObjects,
							true,
							true);
						break;
					case USE_SPECIFIC_CONNECTED_TO_RELATION :
						doUsingConnectedToRelations(
							part,
							distanceToSelectedObjects,
							true,
							false);
						break;
					default:
						break;
					}
				}
			}
		}
	public static	boolean		isCollapsed(MiPart part)
		{
		return(part.getResource(COLLAPSE_POINT_ID) != null);
		}

	protected	void		doUsingPartOfRelation(
						MiPart part, 
						boolean makeVisible)
		{
		for (int i = 0; i < part.getNumberOfParts(); ++i)
			{
			part.getPart(i).setVisible(makeVisible);
			}
		}
	protected	void		doUsingConnectedToRelations(
						MiPart part, 
						int distance, 
						boolean makeVisible, 
						boolean allConnTypes)
		{
		if (distance == DO_ALL_CHILDREN)
			{
			int numConns = part.getNumberOfConnections();
			for (int j = 0; j < numConns; ++j)
				{
				MiConnection conn = part.getConnection(j);
				if (allConnTypes || (conn.isType(connectionType)))
					{
					MiPart child = conn.getDestination();
					if (child != part)
						{
						// if expanding, don;t expand other collapsed nodes
						if ((!makeVisible) || (expandAllPreviousCollapsePoints) 
							|| (child.getResource(COLLAPSE_POINT_ID) == null))
							{
							child.setVisible(makeVisible);
							conn.setVisible(makeVisible);
							doUsingConnectedToRelations(
								child, 
								DO_ALL_CHILDREN, 
								makeVisible, 
								allConnTypes);
							}
						}
					}
				}
			}
		else if (distance == DO_ALL_PARENTS)
			{
			int numConns = part.getNumberOfConnections();
			for (int j = 0; j < numConns; ++j)
				{
				MiConnection conn = part.getConnection(j);
				if (allConnTypes || (conn.isType(connectionType)))
					{
					MiPart parent = conn.getSource();
					if (parent != part)
						{
						// if expanding, don;t expand other collapsed nodes
						if ((!makeVisible || (expandAllPreviousCollapsePoints)) 
							|| (parent.getResource(COLLAPSE_POINT_ID) == null))
							{
							parent.setVisible(makeVisible);
							conn.setVisible(makeVisible);
							doUsingConnectedToRelations(
								parent, 
								DO_ALL_PARENTS, 
								makeVisible, 
								allConnTypes);
							}
						}
					}
				}
			}
		else if (distance == DO_ALL_DISTANT_RELATIVES)
			{
			int numConns = part.getNumberOfConnections();
			for (int j = 0; j < numConns; ++j)
				{
				MiConnection conn = part.getConnection(j);
				if (allConnTypes || (conn.isType(connectionType)))
					{
					MiPart child = conn.getDestination();
					if (child != part)
						{
						doUsingConnectedToRelations(
								child, 
								DO_ALL_CHILDREN, 
								makeVisible, 
								allConnTypes);
						}
					else
						{
						doUsingConnectedToRelations(
								conn.getSource(), 
								DO_ALL_PARENTS, 
								makeVisible, 
								allConnTypes);
						}
					}
				}
			}
		}
	}

