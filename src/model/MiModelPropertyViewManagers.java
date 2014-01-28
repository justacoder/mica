
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


/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelPropertyViewManagers extends MiModelPropertyViewManager
	{
	private		MiModelEntityList	propertyViewManagers 	= new MiModelEntityList();
	private		MiModelEntityList	models 			= new MiModelEntityList();

	public				MiModelPropertyViewManagers()
		{
		}

	public				MiModelPropertyViewManagers(MiModelEntityList list)
		{
		buildFromModels(list);
		}

	public		void		setModels(MiModelEntityList list)
		{
		if (list.size() != models.size())
			buildFromModels(list);
		
		for (int i = 0; i < propertyViewManagers.size(); ++i)
			((MiModelPropertyViewManager )propertyViewManagers.elementAt(i)).setModel(list.elementAt(i));
		}
	protected	void		buildFromModels(MiModelEntityList list)
		{
		propertyViewManagers.removeAllElements();
		models.removeAllElements();
		for (int i = 0; i < list.size(); ++i)
			{
			MiiModelEntity model = list.elementAt(i);
			models.addElement(model);
			MiModelPropertyViewManager manager = new MiModelPropertyViewManager(model);
			propertyViewManagers.addElement(manager);
			}
		}
	public		void		setView(MiPart view)
		{
		for (int i = 0; i < propertyViewManagers.size(); ++i)
			((MiModelPropertyViewManager )propertyViewManagers.elementAt(i)).setView(view);
		}
	public		void		setApplyingEveryCharacterChange(boolean flag)
		{
		for (int i = 0; i < propertyViewManagers.size(); ++i)
			{
			((MiModelPropertyViewManager )propertyViewManagers.elementAt(i))
				.setApplyingEveryCharacterChange(flag);
			}
		super.setApplyingEveryCharacterChange(flag);
		}
	public		void		setApplyingEveryChange(boolean flag)
		{
		for (int i = 0; i < propertyViewManagers.size(); ++i)
			{
			((MiModelPropertyViewManager )propertyViewManagers.elementAt(i))
				.setApplyingEveryChange(flag);
			}
		super.setApplyingEveryChange(flag);
		}
	public		void		setGenerateUndoableTransactions(boolean flag)
		{
		for (int i = 0; i < propertyViewManagers.size(); ++i)
			{
			((MiModelPropertyViewManager )propertyViewManagers.elementAt(i))
				.setGenerateUndoableTransactions(flag);
			}
		super.setGenerateUndoableTransactions(flag);
		}
	}


