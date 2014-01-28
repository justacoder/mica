
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
 * This class implements a document modeled as a linear collection of
 * model entities This is used to provide an abstract data model for the 
 * MiiViewManager and MiiModelIOFormatManager which are used to convert the 
 * the entities here to/from the graphical and data domains.
 *
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelDocument extends MiModelEntity implements MiiModelDocument
	{
					/**------------------------------------------------------
	 				 * Constructs a new MiModelDocument. The document is
					 * by default not read only, not view only and is case-
					 * insensitive.
					 *------------------------------------------------------*/
	public				MiModelDocument()
		{
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiModelDocument which contains a copy
					 * of the given entity. The document is by default not read
					 * only, not view only and is case-insensitive.
					 * @param entity	the entity to add to this document
					 *------------------------------------------------------*/
	public				MiModelDocument(MiModelEntity entity)
		{
		appendModelEntity(entity);
		}
	public		MiiModelEntity	createModelEntity()
		{
		MiModelEntity entity = new MiModelEntity();
		assignUniqueNameToEntity(entity);
		return(entity);
		}
					/**------------------------------------------------------
					 * Gets a name unique within this document for the given 
					 * entity, using it's current name as the root of the 
					 * unique name. This 'root' is the text occuring before
					 * the first '.', if any, and before any numerical digits.
					 * @param entity	the entity to assign a unique name to
					 *------------------------------------------------------*/
	protected	void		assignUniqueNameToEntity(MiModelEntity entity)
		{
		String defaultName = entity.getName();
		if (defaultName == null)
			defaultName = "name";
		assignUniqueNameToEntity(entity, defaultName);
		}
					/**------------------------------------------------------
					 * Gets a name unique within this document for the given 
					 * entity, using it's current name as the root of the 
					 * unique name. This 'root' is the text occuring before
					 * the first '.', if any, and before any numerical digits.
					 * @param entity	the entity to assign a unique name to
					 * @param defaultName	the base 'root' of the name to make
					 *------------------------------------------------------*/
	protected	void		assignUniqueNameToEntity(MiModelEntity entity, String defaultName)
		{
		String uniqueName = defaultName;
		int numberOfTrys = 0;

		String rootName = defaultName;
		String extension = "";
		int index = defaultName.indexOf('.');
		if (index != -1)
			{
			rootName = defaultName.substring(0, index);
			extension = defaultName.substring(index);
			}
		while ((rootName.length() > 1) 
			&& (Character.isDigit(rootName.charAt(rootName.length() - 1))))
			{
			rootName = rootName.substring(0, rootName.length() - 1);
			}
		while (getModelEntity(uniqueName) != null)
			{
			uniqueName = rootName + numberOfTrys + extension;
			++numberOfTrys;
			}

		entity.setName(uniqueName);
		}
	}

