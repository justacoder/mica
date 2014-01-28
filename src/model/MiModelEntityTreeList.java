
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
import com.swfm.mica.util.MutableInteger; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiModelEntityTreeList
	{
	private		MiiModelEntity		root;
	private		int			currentIndex;
	private		MiiModelEntity		currentModel;
	private		MiModelEntityList	currentList;


	public				MiModelEntityTreeList(MiiModelEntity root)
		{
		this.root = root;
		reset();
		}
	public		void		reset()
		{
		currentModel = root;
		currentList = currentModel.getModelEntities();
		currentIndex = 0;
		}
	public		int		size()
		{
		return(size(root));
		}
	public		int		size(MiiModelEntity model)
		{
		MiModelEntityList list = model.getModelEntities();
		int num = list.size();
		for (int i = 0; i < list.size(); ++i)
			{
			num += size(list.elementAt(i));
			}
		return(num);
		}
	public		MiiModelEntity	toNext()
		{
		MiModelEntityList list = currentModel.getModelEntities();
		if (list.size() > 0)
			{
			currentModel = list.elementAt(0);
			currentList = list;
			currentIndex = 0;
			return(currentModel);
			}

		MiiModelEntity model = currentModel;
		if (model == root)
			return(null);

		do	{
			MiiModelEntity parent = model.getParent();
			if (parent == null)
				return(null);
			list = parent.getModelEntities();
			int index = list.indexOf(model);
			if (list.size() > index + 1)
				{
				++index;
				currentModel = list.elementAt(index);
				currentList = list;
				currentIndex = index;
				return(currentModel);
				}
			model = parent;
			} while (model != root);
		return(null);
		}
	public		MiiModelEntity	 elementAt(int index)
		{
		MutableInteger totalSoFar = new MutableInteger();
		return(elementAt(root, totalSoFar, index));
		}
	public		MiiModelEntity	elementAt(MiiModelEntity model, MutableInteger totalSoFar, int index)
		{
		MiModelEntityList list = model.getModelEntities();
		totalSoFar.add(list.size());
		if (totalSoFar.getValue() > index)
			{
			return(list.elementAt(index - totalSoFar.getValue() + list.size()));
			}

		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = elementAt(list.elementAt(i), totalSoFar, index);
			if (entity != null)
				return(entity);
			}
		return(null);
		}
	public		MiiModelEntity	 elementAt(String name)
		{
		return(elementAt(root, name));
		}
	public		MiiModelEntity	 elementAt(MiiModelEntity model, String name)
		{
		if (model.equals(name, model.getName()))
			return(model);
		MiModelEntityList list = model.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity entity = elementAt(list.elementAt(i), name);
			if (entity != null)
				return(entity);
			}
		return(null);
		}
	}

