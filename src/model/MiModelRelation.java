
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
import java.util.Hashtable;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiModelRelation extends MiModelEntity implements MiiModelRelation, MiiModelTypes
	{
	private		MiModelRelationKind	kind;
	private		boolean		mustBeConnectedAtBothEnds;
	private		MiiModelEntity	source;
	private		MiiModelEntity	destination;
	private		boolean		allowSameSourceAndDestination;



	public				MiModelRelation()
		{
		}
	public				MiModelRelation(MiiModelEntity source, MiiModelEntity destination)
		{
		setSource(source);
		setDestination(destination);
		}
	public		void		setKind(MiModelRelationKind kind)
		{
		this.kind = kind;
		}
	public		MiModelRelationKind getKind()
		{
		return(kind);
		}
	public		void		setMustBeConnectedAtBothEnds(boolean flag)
		{
		mustBeConnectedAtBothEnds = flag;
		}
	public		boolean		getMustBeConnectedAtBothEnds()
		{
		return(mustBeConnectedAtBothEnds);
		}
	public		void		setAllowSameSourceAndDestination(boolean flag)
		{
		allowSameSourceAndDestination = flag;
		}
	public		boolean		getAllowSameSourceAndDestination()
		{
		return(allowSameSourceAndDestination);
		}
	public		void		setSource(MiiModelEntity source)
		{
		if (this.source == source)
			{
			return;
			}
		if (this.source != null)
			{
			MiModelChangeEvent event = new MiModelChangeEvent(
				this, this.source, this.destination, 
				Mi_MODEL_RELATION_DISCONNECT_SOURCE_EVENT_TYPE);
			if (dispatchModelChangeRequest(event))
				{
				this.source.removeModelRelation(this);
				dispatchModelChangeCommit(event);
				}
			}
		this.source = source;
		if (this.source != null)
			{
			MiModelChangeEvent event = new MiModelChangeEvent(
				this, this.source, this.destination, 
				Mi_MODEL_RELATION_CONNECT_SOURCE_EVENT_TYPE);
			if (dispatchModelChangeRequest(event))
				{
				this.source.appendModelRelation(this);
				dispatchModelChangeCommit(event);
				}
			}
		else if (mustBeConnectedAtBothEnds)
			{
			deleteSelf();
			}
		}
	public		MiiModelEntity	getSource()
		{
		return(source);
		}
	public		void		setDestination(MiiModelEntity destination)
		{
		if (this.destination == destination)
			{
			return;
			}
		if (this.destination != null)
			{
			MiModelChangeEvent event = new MiModelChangeEvent(
				this, this.source, this.destination, 
				Mi_MODEL_RELATION_DISCONNECT_DESTINATION_EVENT_TYPE);
			if (dispatchModelChangeRequest(event))
				{
				this.destination.removeModelRelation(this);
				dispatchModelChangeCommit(event);
				}
			}
		this.destination = destination;
		if (this.destination != null)
			{
			MiModelChangeEvent event = new MiModelChangeEvent(
				this, this.source, this.destination, 
				Mi_MODEL_RELATION_CONNECT_DESTINATION_EVENT_TYPE);
			if (dispatchModelChangeRequest(event))
				{
				this.destination.appendModelRelation(this);
				dispatchModelChangeCommit(event);
				}
			}
		else if (mustBeConnectedAtBothEnds)
			{
			deleteSelf();
			}
		}
	public		MiiModelEntity	getDestination()
		{
		return(destination);
		}
	public		void		copy(MiiModelEntity entity)
		{
		super.copy(entity);
		if (entity instanceof MiModelRelation)
			{
			allowSameSourceAndDestination = ((MiModelRelation )entity).allowSameSourceAndDestination;
			}
		}
	public		MiiModelEntity	copy()
		{
		MiModelRelation entity = new MiModelRelation();
		entity.copy(this);
		return(entity);
		}
	public		MiiModelEntity	deepCopy()
		{
		MiModelRelation entity = new MiModelRelation();
		entity.deepCopy(this);
		return(entity);
		}
	public		void		replaceSelf(MiiModelEntity entity)
		{
		MiiModelRelation replacement = (MiiModelRelation )entity;
		replacement.setSource(source);
		replacement.setDestination(destination);
		setSource(null);
		setDestination(null);
		super.replaceSelf(entity);
		}
	public		void		deleteSelf()
		{
		if (source != null)
			{
			source.removeModelRelation(this);
			}
		if (destination != null)
			{
			destination.removeModelRelation(this);
			}
		super.deleteSelf();
		}
	public		void		removeSelf()
		{
		if (source != null)
			{
			source.removeModelRelation(this);
			}
		if (destination != null)
			{
			destination.removeModelRelation(this);
			}
		super.removeSelf();
		}
	}


