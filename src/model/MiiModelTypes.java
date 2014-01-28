
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

public interface MiiModelTypes
	{
	String			Mi_COMMENT_PROPERTY_NAME= 	"comment";


	MiModelRelationKind	Mi_CONNECTION_KIND	= new MiModelRelationKind("Connection");
	MiModelRelationKind	Mi_HYPERLINK_KIND	= new MiModelRelationKind("Hyperlink");

	MiModelType		Mi_COMMENT_TYPE		= new MiModelType("Comment");


	String			Mi_MODEL_CHANGE_COMMIT_PHASE_NAME		= "ModelChangeCommitPhase";
	String			Mi_MODEL_CHANGE_REQUEST_PHASE_NAME		= "ModelChangeRequestPhase";
	String			Mi_MODEL_CHANGE_CANCEL_PHASE_NAME		= "ModelChangeCancelPhase";

	int			Mi_MODEL_CHANGE_COMMIT_PHASE_MASK		= 1;
	int			Mi_MODEL_CHANGE_REQUEST_PHASE_MASK		= 2;
	int			Mi_MODEL_CHANGE_CANCEL_PHASE_MASK		= 4;

	MiModelChangePhase	Mi_MODEL_CHANGE_COMMIT_PHASE = new MiModelChangePhase(
								Mi_MODEL_CHANGE_COMMIT_PHASE_NAME, 
								Mi_MODEL_CHANGE_COMMIT_PHASE_MASK);
	MiModelChangePhase	Mi_MODEL_CHANGE_REQUEST_PHASE = new MiModelChangePhase(
								Mi_MODEL_CHANGE_REQUEST_PHASE_NAME,
								Mi_MODEL_CHANGE_REQUEST_PHASE_MASK);
	MiModelChangePhase	Mi_MODEL_CHANGE_CANCEL_PHASE = new MiModelChangePhase(
								Mi_MODEL_CHANGE_CANCEL_PHASE_NAME,
								Mi_MODEL_CHANGE_CANCEL_PHASE_MASK);

	
	MiModelChangePhase[]	phases =	{
						Mi_MODEL_CHANGE_COMMIT_PHASE,
						Mi_MODEL_CHANGE_REQUEST_PHASE,
						Mi_MODEL_CHANGE_CANCEL_PHASE
						};

	String			Mi_MODEL_SELECTED_EVENT_TYPE_NAME	= "modelSelected";
	String			Mi_MODEL_ADDED_EVENT_TYPE_NAME		= "modelAdded";
	String			Mi_MODEL_REMOVED_EVENT_TYPE_NAME	= "modelRemoved";
	String			Mi_MODEL_RELATION_ASSIGNED_EVENT_TYPE_NAME	= "modelRelationAssigned";
	String			Mi_MODEL_RELATION_DEASSIGNED_EVENT_TYPE_NAME	= "modelRelationDeAssigned";
	String			Mi_MODEL_PARENT_ADDED_EVENT_TYPE_NAME		= "modelParentAdded";
	String			Mi_MODEL_PARENT_REMOVED_EVENT_TYPE_NAME		= "modelParentRemoved";

	MiModelChangeEventType	Mi_MODEL_SELECTED_EVENT_TYPE		= new MiModelChangeEventType(
									Mi_MODEL_SELECTED_EVENT_TYPE_NAME);
	MiModelChangeEventType	Mi_MODEL_REMOVED_EVENT_TYPE		= new MiModelChangeEventType(
									Mi_MODEL_REMOVED_EVENT_TYPE_NAME);
	MiModelChangeEventType	Mi_MODEL_ADDED_EVENT_TYPE		= new MiModelChangeEventType(
									Mi_MODEL_ADDED_EVENT_TYPE_NAME);
	MiModelChangeEventType	Mi_MODEL_RELATION_ASSIGNED_EVENT_TYPE	= new MiModelChangeEventType(
									Mi_MODEL_RELATION_ASSIGNED_EVENT_TYPE_NAME);
	MiModelChangeEventType	Mi_MODEL_RELATION_DEASSIGNED_EVENT_TYPE	= new MiModelChangeEventType(
									Mi_MODEL_RELATION_DEASSIGNED_EVENT_TYPE_NAME);
	MiModelChangeEventType	Mi_MODEL_PARENT_ADDED_EVENT_TYPE	= new MiModelChangeEventType(
									Mi_MODEL_PARENT_ADDED_EVENT_TYPE_NAME);
	MiModelChangeEventType	Mi_MODEL_PARENT_REMOVED_EVENT_TYPE	= new MiModelChangeEventType(
									Mi_MODEL_PARENT_REMOVED_EVENT_TYPE_NAME);

	String	Mi_MODEL_RELATION_CONNECT_DESTINATION_EVENT_TYPE_NAME	= "modelRelationConnectDestination";
	String	Mi_MODEL_RELATION_DISCONNECT_DESTINATION_EVENT_TYPE_NAME= "modelRelationDisconnectDestination";
	String	Mi_MODEL_RELATION_CONNECT_SOURCE_EVENT_TYPE_NAME	= "modelRelationConnectSource";
	String	Mi_MODEL_RELATION_DISCONNECT_SOURCE_EVENT_TYPE_NAME	= "modelRelationDisconnectSource";

	MiModelChangeEventType	Mi_MODEL_RELATION_CONNECT_DESTINATION_EVENT_TYPE
							= new MiModelChangeEventType(
							Mi_MODEL_RELATION_CONNECT_DESTINATION_EVENT_TYPE_NAME);
	MiModelChangeEventType	Mi_MODEL_RELATION_DISCONNECT_DESTINATION_EVENT_TYPE
							= new MiModelChangeEventType(
							Mi_MODEL_RELATION_DISCONNECT_DESTINATION_EVENT_TYPE_NAME);

	MiModelChangeEventType	Mi_MODEL_RELATION_CONNECT_SOURCE_EVENT_TYPE
							= new MiModelChangeEventType(
							Mi_MODEL_RELATION_CONNECT_SOURCE_EVENT_TYPE_NAME);
	MiModelChangeEventType	Mi_MODEL_RELATION_DISCONNECT_SOURCE_EVENT_TYPE
							= new MiModelChangeEventType(
							Mi_MODEL_RELATION_DISCONNECT_SOURCE_EVENT_TYPE_NAME);
	}

