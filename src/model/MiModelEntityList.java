
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
public class MiModelEntityList
	{
	private		FastVector	vector	= 	new FastVector();
	private		boolean		readOnlyReference;

	public				MiModelEntityList()
		{
		}
	public				MiModelEntityList(MiiModelEntity element)
		{
		addElement(element);
		}
	public				MiModelEntityList(MiModelEntityList source, boolean readOnlyReference)
		{
		this.readOnlyReference = readOnlyReference;
		if (readOnlyReference)
			vector = source.vector;
		else
			vector.append(source.vector);
		}
	public		MiiModelEntity	 elementAt(String name)
		{
		for (int i = 0; i < vector.size(); ++i)
			{
			if (elementAt(i).equals(elementAt(i).getName(), name))
				return(elementAt(i));
			}
		return(null);
		}
	public		MiiModelEntity	 elementAt(int index)
		{
		return((MiiModelEntity )vector.elementAt(index));
		}
	public		int		size()
		{
		return(vector.size());
		}
	public		boolean		contains(MiiModelEntity model)
		{
		return(vector.contains(model));
		}
	public		MiiModelEntity	 lastElement()
		{
		return((MiiModelEntity )vector.lastElement());
		}
	public		int	 	indexOf(MiiModelEntity model)
		{
		return(vector.indexOf(model));
		}
	public		void		addElement(MiiModelEntity model)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelEntityList is read-only");
		vector.addElement(model);
		}	
	public		void		setElementAt(MiiModelEntity model, int index)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelEntityList is read-only");

		if (index == vector.size())
			vector.addElement(model);
		else
			vector.setElementAt(model, index);
		}	
	public		void		insertElementAt(MiiModelEntity model, int index)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelEntityList is read-only");

		if (index == vector.size())
			vector.addElement(model);
		else
			vector.insertElementAt(model, index);
		}	
	public		void		append(MiModelEntityList other)
		{
		vector.append(other.vector);
		}
	public		void	 	removeElementAt(int index)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelEntityList is read-only");
		vector.removeElementAt(index);
		}
	public		boolean	 	removeElement(MiiModelEntity model)
		{
		if (readOnlyReference)
			throw new RuntimeException("MiModelEntityList is read-only");
		return(vector.removeElement(model));
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

class MiModelEntityIterator
	{
	private		MiiModelEntity		root;
	protected	int			currentIndex;
	protected	MiiModelEntity		currentModel;
	protected	MiModelEntityList	currentList;


	public				MiModelEntityIterator(MiiModelEntity root)
		{
		this.root = root;
		currentIndex = 0;
		currentList = root.getModelEntities();
		currentModel = currentList.elementAt(0);
		}
	public		int		getLength()
		{
		return(currentList.size());
		}
	public		int		getCurrentPos()
		{
		return(currentIndex);
		}
	public		boolean		atFirst()
		{
		return(currentIndex == 0);
		}
	public		boolean		atLast()
		{
		return(currentIndex == currentList.size() - 1);
		}
	public		MiiModelEntity	toNextNode()
		{
		currentModel = currentList.elementAt(++currentIndex);
		return(currentModel);
		}
	public		MiiModelEntity	toPrevNode()
		{
		currentModel = currentList.elementAt(--currentIndex);
		return(currentModel);
		}
	public		MiiModelEntity	toFirstNode()
		{
		currentIndex = 0;
		currentModel = currentList.elementAt(currentIndex);
		return(currentModel);
		}
	public		MiiModelEntity	toLastNode()
		{
		currentIndex = currentList.size() - 1;
		currentModel = currentList.elementAt(currentIndex);
		return(currentModel);
		}
	public		MiiModelEntity	moveTo(int index)
		{
		currentIndex = index;
		currentModel = currentList.elementAt(currentIndex);
		return(currentModel);
		}
	}

class MiModelEntityTreeIterator extends MiModelEntityIterator
	{
	private		MiiModelEntity	currentParent;


	public				MiModelEntityTreeIterator(MiiModelEntity root)
		{
		super(root);
		}
	public		int		numChildren()
		{
		return(currentList.size());
		}
	public		int		numPreviousSiblings()
		{
		if (currentModel.getParent() == null)
			return(0);
		return(currentModel.getParent().getModelEntities().indexOf(currentModel));
		}
	public		int		numNextSiblings()
		{
		if (currentModel.getParent() == null)
			return(0);
		MiModelEntityList list = currentModel.getParent().getModelEntities();
		return(list.size() - list.indexOf(currentModel) - 1);
		}
	// NEED A HAS_PARENT() method
	public		MiiModelEntity	toParent()
		{
		if (currentModel.getParent() == null)
			return(null);

		currentModel = currentModel.getParent();
		currentIndex = 0;
		currentList = currentModel.getModelEntities();
		return(currentModel);
		}
	public		MiiModelEntity	toPreviousSibling()
		{
		MiModelEntityList list = currentModel.getParent().getModelEntities();
		currentModel = list.elementAt(list.indexOf(currentModel) - 1);
		currentIndex = 0;
		currentList = currentModel.getModelEntities();
		return(currentModel);
		}
	public		MiiModelEntity	toNextSibling()
		{
		MiModelEntityList list = currentModel.getParent().getModelEntities();
		currentModel = list.elementAt(list.indexOf(currentModel) + 1);
		currentIndex = 0;
		currentList = currentModel.getModelEntities();
		return(currentModel);
		}
	public		MiiModelEntity	toFirstChild()
		{
		currentModel = currentList.elementAt(0);
		currentIndex = 0;
		currentList = currentModel.getModelEntities();
		return(currentModel);
		}
	public		MiiModelEntity	toLastChild()
		{
		currentModel = currentList.elementAt(currentList.size() - 1);
		currentIndex = 0;
		currentList = currentModel.getModelEntities();
		return(currentModel);
		}
		
	public		MiiModelEntity	toNthChild(int index)
		{
		currentModel = currentList.elementAt(index);
		currentIndex = 0;
		currentList = currentModel.getModelEntities();
		return(currentModel);
		}
	}


