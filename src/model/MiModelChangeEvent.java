
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
import com.swfm.mica.util.NamedEnumeratedType; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiModelChangeEvent implements MiiModelTypes
	{
	private		MiiModelEntity		parent;
	private		MiiModelEntity		child;
	private		MiiModelRelation	relation;
	private		MiiModelEntity		source;
	private		MiiModelEntity		destination;
	private		int			index = -1;
	private		MiModelChangeEventType 	eventType;
	private		MiValueValidationError	vetoMessage;
	private		MiModelChangePhase	phase		= Mi_MODEL_CHANGE_COMMIT_PHASE;
	private		boolean			vetoed;

	public				MiModelChangeEvent(
						MiiModelEntity parent, 
						MiiModelEntity child, 
						MiModelChangeEventType eventType)
		{
		this.parent = parent;
		this.child = child;
		this.eventType = eventType;
		}
	public				MiModelChangeEvent(
						MiiModelEntity parent, 
						MiiModelEntity child, 
						MiModelChangeEventType eventType,
						int index)
		{
		this.parent = parent;
		this.child = child;
		this.eventType = eventType;
		this.index = index;
		}

	public				MiModelChangeEvent(
						MiiModelRelation relation, 
						MiiModelEntity source, 
						MiiModelEntity destination, 
						MiModelChangeEventType eventType)
		{
		this.relation = relation;
		this.source = source;
		this.destination = destination;
		this.eventType = eventType;
		this.index = index;
		}
	public		MiiModelEntity	getParent()
		{
		return(parent);
		}
	public		MiiModelEntity	getChild()
		{
		return(child);
		}
	public		MiiModelRelation getRelation()
		{
		return(relation);
		}
/*
	public		MiiModelEntity	getSource()
		{
		return(source);
		}
*/
	public		MiiModelEntity	getDestination()
		{
		return(destination);
		}
					// -1 indicates the last element or unknown
	public		int		getIndexOfSubjectInSource()
		{
		return(index);
		}
	public		boolean		hasEventType(MiModelChangeEventType type)
		{
		return(type == eventType);
		}
	public		MiModelChangeEventType getEventType()
		{
		return(eventType);
		}
	public		MiModelChangePhase	getPhase()
		{
		return(phase);
		}
	public		void		setPhase(MiModelChangePhase phase)
		{
		this.phase = phase;
		}
	public		void		veto()
		{
		vetoed = true;
		}
	public		boolean		isVetoed()
		{
		return(vetoed);
		}
	public		void		setVetoed(boolean flag)
		{
		vetoed = flag;
		}
	public		void		setVetoMessage(MiValueValidationError msg)
		{
		vetoMessage = msg;
		}
	public		MiValueValidationError getVetoMessage()
		{
		return(vetoMessage);
		}
	public		String		toString()
		{
		return("<" + eventType + ">" 
			+ (phase != Mi_MODEL_CHANGE_COMMIT_PHASE ? phase.toString() : "")
			+ (relation != null 
			? ("relation=" + relation + ",source=" + source + ",destination=" + destination)
			: ("parent=" + parent + ",child=" + child + ",index=" + index)));
		}

	}
