
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
public class MiModelRelationList
	{
	private		FastVector	vector	= 	new FastVector();
	private		boolean		readOnlyReference;



	public				MiModelRelationList()
		{
		}
	public				MiModelRelationList(MiiModelRelation element)
		{
		addElement(element);
		}
	public				MiModelRelationList(MiModelRelationList source, boolean readOnlyReference)
		{
		this.readOnlyReference = readOnlyReference;
		if (readOnlyReference)
			vector = source.vector;
		else
			vector.append(source.vector);
		}

	public		MiModelEntityList	getSourcesOfRelationsOfKind(
							MiiModelEntity subject, MiModelRelationKind kind)
		{
		MiModelEntityList nodes = new MiModelEntityList();
		for (int i = 0; i < vector.size(); ++i)
			{
			MiiModelRelation relation = (MiiModelRelation )vector.elementAt(i);
			if ((relation.getDestination() == subject) && (relation.getKind() == kind))
				nodes.addElement(relation.getSource());
			}
		return(nodes);
		}
	public		MiModelEntityList	getDestinationsOfRelationsOfKind(
							MiiModelEntity subject, MiModelRelationKind kind)
		{
		MiModelEntityList nodes = new MiModelEntityList();
		for (int i = 0; i < vector.size(); ++i)
			{
			MiiModelRelation relation = (MiiModelRelation )vector.elementAt(i);
			if ((relation.getSource() == subject) && (relation.getKind() == kind))
				nodes.addElement(relation.getDestination());
			}
		return(nodes);
		}
	public		MiModelRelationList	getRelationsOfKindTo(
							MiiModelEntity subject, MiModelRelationKind kind)
		{
		MiModelRelationList relations = new MiModelRelationList();
		for (int i = 0; i < vector.size(); ++i)
			{
			MiiModelRelation relation = (MiiModelRelation )vector.elementAt(i);
			if ((relation.getDestination() == subject) && (relation.getKind() == kind))
				relations.addElement(relation);
			}
		return(relations);
		}
	public		MiModelRelationList	getRelationsOfKindFrom(
							MiiModelEntity subject, MiModelRelationKind kind)
		{
		MiModelRelationList relations = new MiModelRelationList();
		for (int i = 0; i < vector.size(); ++i)
			{
			MiiModelRelation relation = (MiiModelRelation )vector.elementAt(i);
			if ((relation.getSource() == subject) && (relation.getKind() == kind))
				relations.addElement(relation);
			}
		return(relations);
		}
	public		MiiModelRelation elementAt(int index)
		{
		return((MiiModelRelation )vector.elementAt(index));
		}
	public		int		size()
		{
		return(vector.size());
		}
	public		boolean		contains(MiiModelRelation relation)
		{
		return(vector.contains(relation));
		}
	public		MiiModelRelation lastElement()
		{
		return((MiiModelRelation )vector.lastElement());
		}
	public		int	 	indexOf(MiiModelRelation relation)
		{
		return(vector.indexOf(relation));
		}
	public		void		addElement(MiiModelRelation relation)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelRelationList is read-only");
		vector.addElement(relation);
		}	
	public		void		insertElementAt(MiiModelRelation relation, int index)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelRelationList is read-only");
		vector.insertElementAt(relation, index);
		}	
	public		void	 	removeElementAt(int index)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelRelationList is read-only");
		vector.removeElementAt(index);
		}
	public		boolean	 	removeElement(MiiModelRelation relation)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelRelationList is read-only");
		return(vector.removeElement(relation));
		}
	public		void	 	removeAllElements()
		{
		while(size() > 0)
			removeElementAt(0);
		}
	public		String		toString()
		{
		return((readOnlyReference ? "<READ ONLY>" : "") + vector.toString());
		}
	}

