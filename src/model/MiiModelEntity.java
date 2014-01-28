
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
import com.swfm.mica.util.Strings; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public interface MiiModelEntity extends MiiModelTypes
	{

	void				setName(String name);
	String				getName();

	void				setType(MiModelType type);
	MiModelType			getType();

	MiModelEntityUserData		getUserData();

	void				setTitle(String title);
	String				getTitle();
	void				setLocation(String location);
	String				getLocation();

	void				setIgnoreCase(boolean flag);
	boolean				getIgnoreCase();
	boolean				equals(String one, String other);
	boolean				equalsProperties(MiiModelEntity other);

	void				setPropertiesAreOrdered(boolean flag);
	boolean				getPropertiesAreOrdered();

	void				copy(MiiModelEntity other);
	void				deepCopy(MiiModelEntity theOther);
	MiiModelEntity			copy();
	MiiModelEntity			deepCopy();
	void				deleteSelf();
	void				removeSelf();
	void				replaceSelf(MiiModelEntity entity);
	MiiModelEntity			getModelEntity(String entityName);

	Strings				getPropertyNames();
	void				setPropertyValue(String name, String value);
	String				getPropertyValue(String name);
	void				removeProperty(String name);
	void				removeAllProperties();

	void				appendPropertyChangeHandler(
						MiiPropertyChangeHandler handler,
						Strings properties, 
						int phaseMask);
	void				removePropertyChangeHandler(MiiPropertyChangeHandler handler);
	int				getNumberOfPropertyChangeHandlers();
	MiiPropertyChangeHandler	getPropertyChangeHandler(int index);

	void				setModelChangeEventDispatchingEnabled(boolean flag);
	void				setPropertyChangeEventDispatchingEnabled(boolean flag);

	MiPropertyDescriptions		getPropertyDescriptions();
	void				setPropertyDescriptions(MiPropertyDescriptions descriptions);

	MiEditingPermissions		getEditingPermissions();

	//void				createModelPropertyDescription();
	MiValueValidationError		validatePropertyValue(String name, String value);
	MiiModelEntity			createModelEntity();
	MiiModelRelation		createModelRelation();
	MiValueValidationError		validateModelEntity(MiiModelEntity model);
	MiValueValidationError		validateModelRelation(MiiModelRelation model);

	void				appendModelChangeHandler(MiiModelChangeHandler handler,
						int phaseMask);
	void				removeModelChangeHandler(MiiModelChangeHandler handler);
	int				getNumberOfModelChangeHandlers();
	MiiModelChangeHandler		getModelChangeHandler(int index);

	void				setParent(MiiModelEntity parent);
	MiiModelEntity			getParent();
	MiModelEntityList		getModelEntities();

	int				getNumberOfModelEntities();
	MiiModelEntity			getModelEntity(int index);
	int				getIndexOfModelEntity(MiiModelEntity entity);

	void				appendModelEntity(MiiModelEntity entity);
	void				insertModelEntity(MiiModelEntity entity, int index);
	boolean				removeModelEntity(MiiModelEntity entity);
	void				removeAllModelEntities();

	MiModelRelationList		getModelRelations();

	// These should be protected? in another package-private interface?
	void				appendModelRelation(MiiModelRelation relation);
	void				insertModelRelation(MiiModelRelation relation, int index);
	boolean				removeModelRelation(MiiModelRelation relation);
	void				removeAllModelRelations();

	}
