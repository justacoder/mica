
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.FastVector; 
import com.swfm.mica.util.CaselessKeyHashtable; 
import com.swfm.mica.util.OrderableCaselessKeyHashtable; 
import com.swfm.mica.util.NamedEnumeratedType; 
import java.util.Enumeration; 
import java.util.Hashtable; 

/**
 * To do:
 * Put type, ignoreCase, orderProperties, validPropertiesMustHaveADescription, title?, 
 * propertyDescriptions
 * in a 'MiModelClass' file, to save memory.
 * Get rid of title, name?
 */

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiModelEntity implements MiiModelEntity, MiiActionHandler, MiiActionTypes, MiiPropertyTypes
	{
	private		MiPropertyDescriptions		propertyDescriptions;
	private		MiModelRelationList		relations;
	private		MiModelEntityList		entities;
	private		CaselessKeyHashtable 		observedProperties;
	private		OrderableCaselessKeyHashtable	properties;

	private static	int		numCreated	= 0;
	private static	boolean		copyEntitysRelationsWhenDeepCopyEntity = true;
	private 	int		id;
	private		MiiModelEntity	parent;
	private		boolean		ignoreCase;
	private		boolean		orderProperties	= true;
	private		boolean		validPropertiesMustHaveADescription;
	private		boolean		propertyChangeEventDispatchingEnabled = true;
	private		boolean		modelChangeEventDispatchingEnabled = true;
	private		String		name;
	private		String		title;
	private		String		location;
	private		MiModelType	type;
	private		FastVector	modelChangeHandlers;
	private		Hashtable	observedModelChangePhases;
	private		FastVector	propertyChangeHandlers;
	private		Hashtable	observedPropertyChangePhases;
	private		MiiModelEntity	source;
	private		MiModelEntityUserData	userData;

	public static final String	Mi_PROPERTY_VALIDATION_FAILED_MSG = "Property validation failed";
	public static final String	Mi_PROPERTY_VALIDATION_FAILED_BECAUSE_HAS_NO_DESCRIPTION_MSG 
									= "Property validation failed (no description)";


	public				MiModelEntity()
		{
		this.source = this;
		id = numCreated++;
		}

	public				MiModelEntity(MiiModelEntity source)
		{
		this.source = source;
		id = numCreated++;
		}
	public		void		setName(String name)
		{
		this.name = name;
		setPropertyValue("name", name);
		}
	public		String		getName()
		{
		return(name); // != null ? name : super.toString() + "." + name);
		}

	public		void		setType(MiModelType type)
		{
		this.type = type;
		}
	public		MiModelType	getType()
		{
		return(type);
		}
	public		MiModelEntityUserData getUserData()
		{
		if (userData == null)
			userData = new MiModelEntityUserData();
		return(userData);
		}
	public		void		setTitle(String title)
		{
		this.title = title;
		}
	public		String		getTitle()
		{
		return(title);
		}
	public		void		setLocation(String location)
		{
		this.location = location;
		}
	public		String		getLocation()
		{
		return(location);
		}
	public		void		setIgnoreCase(boolean flag)
		{
		ignoreCase = flag;
		if (properties != null)
			properties.setIgnoreCase(flag);
		if (observedProperties != null)
			observedProperties.setIgnoreCase(ignoreCase);
		MiModelEntityList list = getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			list.elementAt(i).setIgnoreCase(flag);
			}
		}
	public		boolean		getIgnoreCase()
		{
		return(ignoreCase);
		}
	public		void		setPropertiesAreOrdered(boolean flag)
		{
		orderProperties = flag;
		if (properties != null)
			properties.setIsOrdered(flag);
		}
	public		boolean		getPropertiesAreOrdered()
		{
		return(orderProperties);
		}

	public		boolean		equalsProperties(MiiModelEntity other)
		{
		Strings otherProperties = other.getPropertyNames();
		if (otherProperties.size() != properties.size())
			{
			return(false);
			}
		Strings propertyNames = getPropertyNames();
		for (int i = 0; i < propertyNames.size(); ++i)
			{
			String name = propertyNames.get(i);
			if (!equals(getPropertyValue(name), other.getPropertyValue(name)))
				{
				return(false);
				}
			}
		return(true);
		}


		

		
					/**------------------------------------------------------
					 * Gets whether the given strings are equal using the state
					 * of the ignoreCase flag to determine if the strings should
					 * be compared in a case-sensitive or case-insensitive manner.
					 * @param one		a string (possibly null)
					 * @param other		another string (possibly null)
					 * @return		true if they are equal
					 * @see			#setIgnoreCase
					 * @see			#getIgnoreCase
					 *------------------------------------------------------*/
	public		boolean		equals(String one, String other)
		{
		if (one == null)
			{
			return(other == null);
			}
		if (other != null)
			{
			if (ignoreCase)
				return(one.equalsIgnoreCase(other));
			return(one.equals(other));
			}
		return(false);
		}

	/**
	 * Copies properties, title, location and REMOVES ALL PROPERTY CHANGE HANDLERS
	 **/
	public		void		copy(MiiModelEntity theOther)
		{
		MiModelEntity other = (MiModelEntity )theOther;
		name			= other.name;
		properties		= other.properties == null 
					? null : (OrderableCaselessKeyHashtable)other.properties.clone();
		propertyDescriptions 	= other.getPropertyDescriptions();
		ignoreCase		= other.ignoreCase;
		orderProperties		= other.orderProperties;
		title			= other.title;
		location		= other.location;
		type			= other.type;
		propertyChangeEventDispatchingEnabled = other.propertyChangeEventDispatchingEnabled;
		modelChangeEventDispatchingEnabled = other.modelChangeEventDispatchingEnabled;
		modelChangeHandlers		= null;
		observedModelChangePhases	= null;
		propertyChangeHandlers		= null;
		observedPropertyChangePhases	= null;
		observedProperties		= null;

		userData		= other.userData == null 
					? null : other.userData.copy();
		}
	public static	void		setToCopyEntitysRelationsWhenDeepCopyEntity(boolean flag)
		{
		copyEntitysRelationsWhenDeepCopyEntity = flag;
		}
	public static	boolean		isToCopyEntitysRelationsWhenDeepCopyEntity()
		{
		return(copyEntitysRelationsWhenDeepCopyEntity);
		}
	public		MiiModelEntity	copy()
		{
		MiModelEntity entity = new MiModelEntity();
		entity.copy(this);
		return(entity);
		}
	public		MiiModelEntity	deepCopy()
		{
		MiModelEntity entity = new MiModelEntity();
		entity.deepCopy(this);
		return(entity);
		}
	public		void		deepCopy(MiiModelEntity theOther)
		{
		copy(theOther);
		if (theOther instanceof MiModelEntity)
			{
			MiModelEntity theOtherEntity = (MiModelEntity )theOther;

			if (theOtherEntity.entities != null)
				{
				for (int i = 0; i < theOtherEntity.entities.size(); ++i)
					appendModelEntity(theOtherEntity.entities.elementAt(i).deepCopy());
				}

			if ((copyEntitysRelationsWhenDeepCopyEntity) && (theOtherEntity.relations != null))
				{
				for (int i = 0; i < theOtherEntity.relations.size(); ++i)
					{
					appendModelRelation((MiiModelRelation )
						theOtherEntity.relations.elementAt(i).deepCopy());
					}
				}
			}
		else
			{
			MiModelEntityList elist = theOther.getModelEntities();
			for (int i = 0; i < elist.size(); ++i)
				appendModelEntity(elist.elementAt(i).deepCopy());

			if (copyEntitysRelationsWhenDeepCopyEntity)
				{
				MiModelRelationList list = theOther.getModelRelations();
				for (int i = 0; i < list.size(); ++i)
					appendModelRelation((MiiModelRelation )list.elementAt(i).deepCopy());
				}
			}
		}
	public		void		replaceSelf(MiiModelEntity entity)
		{
		if (entity == this)
			{
			return;
			}
		if (parent != null)
			{
			parent.insertModelEntity(entity, parent.getIndexOfModelEntity(this));
			parent.removeModelEntity(this);
			}
		if (relations != null)
			{
			for (int i = 0; i < relations.size(); ++i)
				{
				MiiModelRelation r = relations.elementAt(i);
				if (r.getSource() == this)
					r.setSource(entity);
				if (r.getDestination() == this)
					r.setDestination(entity);
				}
			}
		}
	public		void		deleteSelf()
		{
		if (relations != null)
			{
			for (int i = 0; i < relations.size(); ++i)
				{
				MiiModelRelation r = relations.elementAt(i);
				if (r.getSource() == this)
					r.setSource(null);
				if (r.getDestination() == this)
					r.setDestination(null);
				}
			removeAllModelRelations();
			}
		if (parent != null)
			{
			parent.removeModelEntity(this);
			}
		if (entities != null)
			{
			for (int i = 0; i < entities.size(); ++i)
				{
				entities.elementAt(i).deleteSelf();
				}
			removeAllModelEntities();
			}
		}
	public		void		removeSelf()
		{
		if (relations != null)
			{
			for (int i = 0; i < relations.size(); ++i)
				{
				MiiModelRelation r = relations.elementAt(i);
				if (r.getSource() == this)
					r.setSource(null);
				if (r.getDestination() == this)
					r.setDestination(null);
				}
			removeAllModelRelations();
			}
		if (parent != null)
			{
			parent.removeModelEntity(this);
			}
		}

	public		MiiModelEntity	getModelEntity(String entityName)
		{
		MiModelEntityList list = getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			if (equals(entityName, list.elementAt(i).getName()))
				return(list.elementAt(i));
			}
		return(null);
		}
	public		Strings		getPropertyNames()
		{
		Strings names = new Strings();
		if (properties != null)
			{
			if (orderProperties)
				{
				names.append(properties.getKeys());
				}
			else
				{
				for (Enumeration e = properties.keys(); e.hasMoreElements();)
					{
					String key = (String)e.nextElement();
					names.addElement(key);
					}
				}
			}
		return(names);
		}
	public		void		setPropertyValue(String name, String value)
		{
		name = Utility.replaceAll(name, " ", "");
		String oldValue = getPropertyValue(name);

		if (value == null)
			{
			if (properties == null)
				return;

			if (!properties.getKeys().contains(name))
				return;

			properties.remove(name);
			}
		else
			{
			if (equals(oldValue, value))
				return;

			if (properties == null)
				{
				properties = new OrderableCaselessKeyHashtable(3);
				properties.setIgnoreCase(ignoreCase);
				properties.setIsOrdered(orderProperties);
				}

			properties.put(name, value);
			if (name.equalsIgnoreCase("name"))
				setName(value);
			}
		MiPropertyChange event = new MiPropertyChange(source, name, value, oldValue);
		event.setPhase(Mi_MODEL_CHANGE_COMMIT_PHASE);
		dispatchPropertyChangeEvent(event);
		}

	public		String		getPropertyValue(String name)
		{
		if (properties == null)
			return(null);
		name = Utility.replaceAll(name, " ", "");
		return((String )properties.get(name));
		}
	public		String		getDefaultPropertyValue(String name)
		{
		if (properties == null)
			return(null);
		name = Utility.replaceAll(name, " ", "");
		MiPropertyDescription desc = getPropertyDescriptions().elementAt(name);
		if (desc != null) 
			{
			return(desc.getDefaultValue());
			}
//MiDebug.println("getDefaultPropertyValue: " + name + " Has No Property Description");
		return(null);
		}
	public		void		removeProperty(String name)
		{
		if (properties == null)
			return;
		name = Utility.replaceAll(name, " ", "");
		String oldValue = getPropertyValue(name);
		properties.remove(name);
		MiPropertyChange event = new MiPropertyChange(source, name, null, oldValue);
		event.setPhase(Mi_MODEL_CHANGE_COMMIT_PHASE);
		dispatchPropertyChangeEvent(event);
		}
	public		void		removeAllProperties()
		{
		Strings names = getPropertyNames();
		for (int i = 0; i < names.size(); ++i)
			{
			String name = names.elementAt(i);
			removeProperty(name);
			}
		}

	public		void		setPropertyChangeEventDispatchingEnabled(boolean flag)
		{
		propertyChangeEventDispatchingEnabled = flag;
		}
	public		void		setModelChangeEventDispatchingEnabled(boolean flag)
		{
		modelChangeEventDispatchingEnabled = flag;
		}
	public		void		appendPropertyChangeHandler(
						MiiPropertyChangeHandler handler, 
						Strings properties, 
						int phaseMask)
		{
		if (propertyChangeHandlers == null)
			propertyChangeHandlers = new FastVector();

if (propertyChangeHandlers.contains(handler))
{
// When removing this handler, properties from the first assignment will still reference the handler. See below.
MiDebug.printStackTrace(this + ":ADDING HANDLER TWICE TO SAME ENTITY:" + handler);
}

		propertyChangeHandlers.addElement(handler);

		if (observedProperties == null)
			{
			observedProperties = new CaselessKeyHashtable(5);
			observedProperties.setIgnoreCase(ignoreCase);
			}
		observedProperties.put(handler, properties);
		for (int i = 0; i < properties.size(); ++i)
			{
			String propertyName = properties.elementAt(i);
			FastVector vector = (FastVector )observedProperties.get(propertyName);
			if (vector == null)
				{
				vector = new FastVector();
				observedProperties.put(propertyName, vector);
				}
			vector.addElement(handler);
			}
		if (observedPropertyChangePhases == null)
			observedPropertyChangePhases = new Hashtable(5);
		observedPropertyChangePhases.put(handler, new Integer(phaseMask));
		}
	public		void		removePropertyChangeHandler(MiiPropertyChangeHandler handler)
		{
		if ((propertyChangeHandlers == null) 
			|| (propertyChangeHandlers.indexOf(handler) == -1) 
			|| (observedProperties == null))
			{
			return;
			}
		while (propertyChangeHandlers.removeElement(handler))
			;
		while (observedPropertyChangePhases.remove(handler) != null)
			;
		Strings properties = (Strings )observedProperties.get(handler);
		observedProperties.remove(handler);
		for (int i = 0; i < properties.size(); ++i)
			{
			String propertyName = properties.elementAt(i);
			FastVector vector = (FastVector )observedProperties.get(propertyName);
			vector.removeElement(handler);
			}
/* If allow multiple assignments of same handler...
for (Enumeration e = observedProperties.keys(); e.hasMoreElements();)
	{
	Object op = observedProperties.get(e.nextElement());
	if (op instanceof FastVector)
		{
		((FastVector )op).remove(handler);
		}
	}
****/
		}
	public		int		getNumberOfPropertyChangeHandlers()
		{
		if (propertyChangeHandlers == null)
			return(0);
		return(propertyChangeHandlers.size());
		}
	public		MiiPropertyChangeHandler	getPropertyChangeHandler(int index)
		{
		return((MiiPropertyChangeHandler )propertyChangeHandlers.elementAt(index));
		}
	public 		boolean		isObservedProperty(String propertyName)
		{
		return((observedProperties != null) && 
			((observedProperties.get(propertyName) != null)
			|| (observedProperties.get(Mi_ANY_PROPERTY) != null)));
		}
	public		MiValueValidationError validatePropertyValue(String name, String value)
		{
		String oldValue = getPropertyValue(name);

		if (oldValue == null)
			oldValue = "";
		if (value == null)
			value = "";


		MiPropertyDescription desc = getPropertyDescriptions() != null 
			? getPropertyDescriptions().elementAt(name) : null;
		if (desc == null)
			{
			if (validPropertiesMustHaveADescription)
				{
				return(new MiValueValidationError(
					Mi_PROPERTY_VALIDATION_FAILED_BECAUSE_HAS_NO_DESCRIPTION_MSG));
				}
			return(null);
			}
		MiValueValidationError errorMsg = desc.validateValue(value);
if (errorMsg != null)
{
MiDebug.println(getName() + ": Property \"" + name + "\" cannot be equal to: \"" + value + "\"");
//MiDebug.printStackTrace("" + this);
}
		if (errorMsg != null)
			return(errorMsg);

		// Part does it's own validation using MiiAction requests
		if (this instanceof MiPartToModel)
			return(null);

		if (oldValue.equals(value))
			return(null);

		MiPropertyChange event = new MiPropertyChange(source, name, value, oldValue);
		event.setPhase(Mi_MODEL_CHANGE_REQUEST_PHASE);
		dispatchPropertyChangeEvent(event);

		if (event.isVetoed())
			{
			errorMsg = event.getVetoMessage();
			if (errorMsg == null)
				errorMsg =new MiValueValidationError(Mi_GENERIC_PROPERTY_VALIDATION_ERROR);
			}

		return(errorMsg);
		}
	public		void		dispatchPropertyChange(
						MiiModelEntity source,
						String propertyName,
						String propertyValue,
						String oldPropertyValue)
		{
		if (source == null)
			source = this;
		MiPropertyChange event 
			= new MiPropertyChange(source, propertyName, propertyValue, oldPropertyValue);
		event.setPhase(Mi_MODEL_CHANGE_COMMIT_PHASE);
		dispatchPropertyChangeEvent(event);
		}
	public		void		dispatchPropertyChangeEvent(MiPropertyChange event)
		{
		event.setVetoed(false);

		if (!propertyChangeEventDispatchingEnabled)
			return;

		if (observedProperties == null)
			return;

		FastVector vector = (FastVector )observedProperties.get(Mi_ANY_PROPERTY);
		FastVector vector2 = (FastVector )observedProperties.get(event.getPropertyName());
		if ((vector == null) && (vector2 == null))
			return;

		for (int i = 0; (vector != null) && (i < vector.size()); ++i)
			{
			MiiPropertyChangeHandler handler = (MiiPropertyChangeHandler )vector.elementAt(i);
if ((observedPropertyChangePhases == null) || (observedPropertyChangePhases.get(handler) == null))
{
MiDebug.println("" + dump(this));
MiDebug.println("observedPropertyChangePhases = " + observedPropertyChangePhases);
MiDebug.println("observedPropertyChangePhases.get(handler) = " + observedPropertyChangePhases.get(handler));
}

			int phaseMask = ((Integer )observedPropertyChangePhases.get(handler)).intValue();
			if ((phaseMask & event.getPhase().getMask()) != 0)
				{
				handler.processPropertyChange(event);
				if ((event.isVetoed()) && (event.getPhase() == Mi_MODEL_CHANGE_REQUEST_PHASE))
					{
					if (!(this instanceof MiPartToModel))
						{
						event.setPhase(Mi_MODEL_CHANGE_CANCEL_PHASE);
						dispatchPropertyChangeEvent(event);
						event.setVetoed(true);
						}
					return;
					}
				}
			}
		for (int i = 0; (vector2 != null) && (i < vector2.size()); ++i)
			{
			MiiPropertyChangeHandler handler = (MiiPropertyChangeHandler )vector2.elementAt(i);
			int phaseMask = ((Integer )observedPropertyChangePhases.get(handler)).intValue();
			if ((phaseMask & event.getPhase().getMask()) != 0)
				{
				handler.processPropertyChange(event);
				if ((event.isVetoed()) && (event.getPhase() == Mi_MODEL_CHANGE_REQUEST_PHASE))
					{
					if (!(this instanceof MiPartToModel))
						{
						event.setPhase(Mi_MODEL_CHANGE_CANCEL_PHASE);
						dispatchPropertyChangeEvent(event);
						event.setVetoed(true);
						}
					return;
					}
				}
			}
		}
	public		void		appendModelChangeHandler(
						MiiModelChangeHandler handler, int phaseMask)
		{
		if (modelChangeHandlers == null)
			modelChangeHandlers = new FastVector();

		if (modelChangeHandlers.contains(handler))
			{
			throw new IllegalArgumentException(this 
				+ ": Appending handler twice to entity.\nHandler = " + handler);
			}

		modelChangeHandlers.addElement(handler);

		if (observedModelChangePhases == null)
			observedModelChangePhases = new Hashtable(5);
		observedModelChangePhases.put(handler, new Integer(phaseMask));
		}
	public		void		removeModelChangeHandler(MiiModelChangeHandler handler)
		{
		if (modelChangeHandlers != null)
			{
			while (modelChangeHandlers.removeElement(handler))
				;
			}
		if (observedModelChangePhases != null)
			{
			while (observedModelChangePhases.remove(handler) != null)
				;
			}
		}
	public		int		getNumberOfModelChangeHandlers()
		{
		if (modelChangeHandlers == null)
			return(0);
		return(modelChangeHandlers.size());
		}
	public		MiiModelChangeHandler	getModelChangeHandler(int index)
		{
		return((MiiModelChangeHandler )modelChangeHandlers.elementAt(index));
		}
	protected	boolean		dispatchModelChangeRequest(MiModelChangeEvent event)
		{
		event.setPhase(Mi_MODEL_CHANGE_REQUEST_PHASE);
		dispatchModelChangeEvent(event);
                if (event.isVetoed())
                        return(false);
                return(true);
		}
	protected	void		dispatchModelChangeCommit(MiModelChangeEvent event)
		{
		event.setPhase(Mi_MODEL_CHANGE_COMMIT_PHASE);
		dispatchModelChangeEvent(event);
		}
	public		void		dispatchModelChangeEvent(MiModelChangeEvent event)
		{
		event.setVetoed(false);
		
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_MODEL_CHANGE_EVENTS))
			{
			MiDebug.println("model: " + this + "\n\nPreparing to dispatch model change event: " 
				+ event + "\n---->handlers: " + modelChangeHandlers 
				+ "\n[modelChangeEventDispatchingEnabled=" + modelChangeEventDispatchingEnabled + "]");
			}

		if (modelChangeHandlers == null)
			return;

		if (!modelChangeEventDispatchingEnabled)
			{
			return;
			}

		for (int i = 0; i < modelChangeHandlers.size(); ++i)
			{
			MiiModelChangeHandler handler 
				= (MiiModelChangeHandler )modelChangeHandlers.elementAt(i);
			int phaseMask = ((Integer )observedModelChangePhases.get(handler)).intValue();
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_MODEL_CHANGE_EVENTS))
				{
				MiDebug.println("Comparing event phase mask: " + event.getPhase().getMask() 
					+ " to handler phase mask: " + phaseMask);
				}
			if ((phaseMask & event.getPhase().getMask()) != 0)
				{
				if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_MODEL_CHANGE_EVENTS))
					{
					MiDebug.println("Dispatching model change event to: " + handler);
					}
				handler.processModelChange(event);
				if ((event.isVetoed()) && (event.getPhase() == Mi_MODEL_CHANGE_REQUEST_PHASE))
					{
					if (!(this instanceof MiPartToModel))
						{
						event.setPhase(Mi_MODEL_CHANGE_CANCEL_PHASE);
						dispatchModelChangeEvent(event);
						event.setVetoed(true);
						}
					return;
					}
				}
			}
		}
	public		void		setPropertyDescriptions(MiPropertyDescriptions descs)
		{
		propertyDescriptions = descs;
		}
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		return(propertyDescriptions);
		}
	public		void		setPropertiesToDefaultValues()
		{
		if (getPropertyDescriptions() != null)
			{
			MiPropertyDescriptions descriptions = getPropertyDescriptions();
			int size = descriptions.size();
			for (int i = 0; i < size; ++i)
				{
				MiPropertyDescription desc = descriptions.elementAt(i);
				setPropertyValue(desc.getName(), desc.getDefaultValue());
				}
			}
		}
	public		MiEditingPermissions getEditingPermissions()
		{
		return(new MiEditingPermissions());
		}

	public		MiiModelEntity	createModelEntity()
		{
		return(new MiModelEntity());
		}
	public		MiiModelRelation createModelRelation()
		{
		return(new MiModelRelation());
		}
	public		MiValueValidationError	validateModelEntity(MiiModelEntity model)
		{
		return(new MiValueValidationError(Mi_GENERIC_PROPERTY_VALIDATION_ERROR_MSG));
		}
	public		MiValueValidationError	validateModelRelation(MiiModelRelation model)
		{
		return(new MiValueValidationError(Mi_GENERIC_PROPERTY_VALIDATION_ERROR_MSG));
		}
	public		int		getNumberOfModelEntities()
		{
		if (entities == null)
			return(0);
		return(entities.size());
		}
	public		MiiModelEntity	getModelEntity(int index)
		{
		return(entities.elementAt(index));
		}
	public		int		getIndexOfModelEntity(MiiModelEntity entity)
		{
		return(entities.indexOf(entity));
		}
	public		void			setParent(MiiModelEntity newParent)
		{
		if (parent == newParent)
			return;

		if (newParent instanceof MiModelEntity)
			{
			if ((newParent != null) 
				&& (((MiModelEntity )newParent).entities != null) 
				&& (!((MiModelEntity )newParent).entities.contains(this))) 
				{
				newParent.appendModelEntity(this);
				}
			else
				{
				parent = newParent;
				}
			}
		else if ((newParent != null) && (!newParent.getModelEntities().contains(this)))
			{
			newParent.appendModelEntity(this);
			}
		else
			{
			parent = newParent;
			}
		}
	public		MiiModelEntity		getParent()
		{
		return(parent);
		}
	public		MiModelEntityList	getModelEntities()
		{
		if (entities == null)
			entities = new MiModelEntityList();
		return(new MiModelEntityList(entities, true));
		}
	public		void		appendModelEntity(MiiModelEntity entity)
		{
		if (entity == this)
			throw new IllegalArgumentException("Appending entity to itself.\nEntity = " + entity);
		if (entity.getParent() == this)
			{
			throw new IllegalArgumentException("Appending entity twice to container.\nEntity = " 
				+ entity + "\nContainer = " + this);
			}

		if (entities == null)
			entities = new MiModelEntityList();

		MiModelChangeEvent event = new MiModelChangeEvent(this, entity, Mi_MODEL_ADDED_EVENT_TYPE, -1);
		if (dispatchModelChangeRequest(event))
			{
			if (entity.getParent() != null)
				{
				MiModelChangeEvent p_event = new MiModelChangeEvent(this, entity, Mi_MODEL_PARENT_REMOVED_EVENT_TYPE, -1);
				if ((entity instanceof MiModelEntity) 
					&& (!((MiModelEntity )entity).dispatchModelChangeRequest(p_event)))
					{
					return;
					}
				if (!entity.getParent().removeModelEntity(entity))
					return;

				if (entity instanceof MiModelEntity)
					{
					((MiModelEntity )entity).dispatchModelChangeCommit(p_event);
					}
				}
			MiModelChangeEvent p_event = new MiModelChangeEvent(this, entity, Mi_MODEL_PARENT_ADDED_EVENT_TYPE, -1);
			if ((!(entity instanceof MiModelEntity )) 
				|| (((MiModelEntity )entity).dispatchModelChangeRequest(p_event)))
				{
				entities.addElement(entity);
				entity.setParent(this);
				dispatchModelChangeCommit(event);
				if (entity instanceof MiModelEntity)
					{
					((MiModelEntity )entity).dispatchModelChangeCommit(p_event);
					}
				}
			}
		}
	public		void		insertModelEntity(MiiModelEntity entity, int index)
		{
		if (entity == this)
			throw new IllegalArgumentException("Inserting entity into itself.\nEntity = " + entity);
		if (entity.getParent() == this)
			{
			throw new IllegalArgumentException("Inserting entity twice into container.\nEntity = " 
				+ entity + "\nContainer = " + this);
			}

		if (entities == null)
			entities = new MiModelEntityList();

		MiModelChangeEvent event = new MiModelChangeEvent(this, entity, Mi_MODEL_ADDED_EVENT_TYPE, index);
		if (dispatchModelChangeRequest(event))
			{
			if (entity.getParent() != null)
				{
				MiModelChangeEvent p_event = new MiModelChangeEvent(this, entity, Mi_MODEL_PARENT_REMOVED_EVENT_TYPE, -1);
				if ((entity instanceof MiModelEntity) 
					&& (!((MiModelEntity )entity).dispatchModelChangeRequest(p_event)))
					{
					return;
					}
				if (!entity.getParent().removeModelEntity(entity))
					return;

				if (entity instanceof MiModelEntity)
					{
					((MiModelEntity )entity).dispatchModelChangeCommit(p_event);
					}
				}
			MiModelChangeEvent p_event = new MiModelChangeEvent(this, entity, Mi_MODEL_PARENT_ADDED_EVENT_TYPE, -1);
			if ((!(entity instanceof MiModelEntity )) 
				|| (((MiModelEntity )entity).dispatchModelChangeRequest(p_event)))
				{
				entities.insertElementAt(entity, index);
				entity.setParent(this);
				dispatchModelChangeCommit(event);
				if (entity instanceof MiModelEntity)
					{
					((MiModelEntity )entity).dispatchModelChangeCommit(p_event);
					}
				}
			}
		}
	public		boolean		removeModelEntity(MiiModelEntity entity)
		{
		boolean status = false;
		if (entities != null)
			{
			MiModelChangeEvent event = new MiModelChangeEvent(
				this, entity, Mi_MODEL_REMOVED_EVENT_TYPE, entities.indexOf(entity));
			if (dispatchModelChangeRequest(event))
				{
				MiModelChangeEvent p_event = new MiModelChangeEvent(this, entity, Mi_MODEL_PARENT_REMOVED_EVENT_TYPE, -1);
				if ((!(entity instanceof MiModelEntity )) 
					|| (((MiModelEntity )entity).dispatchModelChangeRequest(p_event)))
					{
//if ((getType() != null) && "ChileCAD".equals(getType().getName()))
//MiDebug.printStackTrace("Removing: " + entity + "\n\n from: " + this);
					status = entities.removeElement(entity);
					entity.setParent(null);
					dispatchModelChangeCommit(event);
					if (entity instanceof MiModelEntity)
						{
						((MiModelEntity )entity).dispatchModelChangeCommit(p_event);
						}
					}
				}
			}
		return(status);
		}
	public		void		removeAllModelEntities()
		{
		if (entities != null)
			{
			for (int i = entities.size() - 1; i >= 0; --i)
				{
				removeModelEntity(entities.elementAt(i));
				}
			}
		}

	public		MiModelRelationList	getModelRelations()
		{
		if (relations == null)
			relations = new MiModelRelationList();
		return(relations);
		}
	public		void		appendModelRelation(MiiModelRelation relation)
		{
		if (relation == this)
			throw new IllegalArgumentException(this + ": Appending relation to itself");
		if (relations == null)
			relations = new MiModelRelationList();
		else if (relations.contains(relation))
			{
			if (!relation.getAllowSameSourceAndDestination())
				{
				throw new IllegalArgumentException(this + ": Appending relation twice to container");
				}
			}

//MiDebug.printStackTrace("Adding relation : " + relation);
		MiModelChangeEvent event 
			= new MiModelChangeEvent(this, relation, Mi_MODEL_RELATION_ASSIGNED_EVENT_TYPE, -1);
		if (dispatchModelChangeRequest(event))
			{
			relations.addElement(relation);
			dispatchModelChangeCommit(event);
			}
		}
	public		void		insertModelRelation(MiiModelRelation relation, int index)
		{
		if (relation == this)
			throw new IllegalArgumentException(this + ": Inserting relation to itself");
		if (relations == null)
			relations = new MiModelRelationList();
		else if (relations.contains(relation))
			throw new IllegalArgumentException(this + ": Inserting relation twice to container");

//MiDebug.printStackTrace("Adding relation : " + relation);
		MiModelChangeEvent event 
			= new MiModelChangeEvent(this, relation, Mi_MODEL_RELATION_ASSIGNED_EVENT_TYPE, index);
		if (dispatchModelChangeRequest(event))
			{
			relations.insertElementAt(relation, index);
			dispatchModelChangeCommit(event);
			}
		}
	public		boolean		removeModelRelation(MiiModelRelation relation)
		{
		boolean status = false;
		if (relations != null)
			{
			MiModelChangeEvent event = new MiModelChangeEvent(
				this, relation, Mi_MODEL_RELATION_DEASSIGNED_EVENT_TYPE, relations.indexOf(relation));
//MiDebug.println(this + "\n\n\nRemoving relation? : " + relation);
			if (dispatchModelChangeRequest(event))
				{
//MiDebug.println("\n\n\nRemoving relation : " + relation);
				status = relations.removeElement(relation);
				dispatchModelChangeEvent(event);
				}
//MiDebug.println("\n\n\nRemoved relation : " + dump(this));
			}
		return(status);
		}
	public		void		removeAllModelRelations()
		{
		if (relations != null)
			{
			for (int i = relations.size() - 1; i >= 0; --i)
				{
				removeModelRelation(relations.elementAt(i));
				}
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		MiPropertyChange event = (MiPropertyChange )action.getActionSystemInfo();
		if (!isObservedProperty(event.getPropertyName()))
			return(true);

		MiModelChangePhase changePhase = Mi_MODEL_CHANGE_COMMIT_PHASE;
		if (action.isPhase(Mi_COMMIT_ACTION_PHASE))
			changePhase = Mi_MODEL_CHANGE_COMMIT_PHASE;
		else if (action.isPhase(Mi_REQUEST_ACTION_PHASE))
			changePhase = Mi_MODEL_CHANGE_REQUEST_PHASE;
		else if (action.isPhase(Mi_EXECUTE_ACTION_PHASE))
			return(true);
		else if (action.isPhase(Mi_CANCEL_ACTION_PHASE))
			changePhase = Mi_MODEL_CHANGE_CANCEL_PHASE;

		event.setPhase(changePhase);
		dispatchPropertyChangeEvent(event);
		if ((event.isVetoed()) && (changePhase == Mi_MODEL_CHANGE_REQUEST_PHASE))
			{
			action.veto();
			return(false);
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * converts this entity into a string.
					 * @return 		the string description
					 *------------------------------------------------------*/
	public		String		toString()
		{
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(getClass().getName() + "." + id + ".");
		Strings names = getPropertyNames();
		if (this instanceof MiiModelRelation)
			{
			MiiModelRelation relation = (MiiModelRelation )this;
			strBuf.append("<Relation: Source = " 
				+ (relation.getSource() == null ? "null" : relation.getSource().getName() 
					+ "." + ((MiModelEntity )relation.getSource()).id)
				+ ", Destination = "
				+ (relation.getDestination() == null ? "null" : relation.getDestination().getName() 
					+ "." + ((MiModelEntity )relation.getDestination()).id)
				+ ">");
			}
		strBuf.append("Name=" + name + ",Type=" + getType() + ",Location=" + getLocation()
			+ "[Properties: ");
		for (int i = 0; i < names.size(); ++i)
			{
			String key = names.elementAt(i);
			strBuf.append(key);
			strBuf.append("=");
			String val = (String)getPropertyValue(key);
			if (val == null)
				{
				strBuf.append(MiiTypes.Mi_NULL_VALUE_NAME);
				}
			else if (val.indexOf(' ') != -1)
				{
				strBuf.append("\"");
				strBuf.append(val);
				strBuf.append("\"");
				}
			else
				{
				strBuf.append(val);
				}
			if (i < names.size() - 1)
				strBuf.append(", ");
			}
		strBuf.append("]");
		strBuf.append("[User Data: {");
		if (userData != null)
			{
			strBuf.append(userData.toString());
			}
		strBuf.append("}]");	
		
		return(strBuf.toString());
		}
	public static	String		dump(MiiModelEntity target)
		{
		return(target != null ? dump(target, "") : "null");
		}
	private static	String		dump(MiiModelEntity target, String indent)
		{
		String str = indent + target.toString();
		MiModelEntityList list = target.getModelEntities();
		if (list.size() > 0)
			{
			str += "\n" + indent + "    {";
			for (int i = 0; i < list.size(); ++i)
				{
				str += "\n" + dump(list.elementAt(i), indent + "    ");
				}
			str += "\n" + indent + "    }";
			}
		MiModelRelationList relations = target.getModelRelations();
		if (relations.size() > 0)
			{
			str += "\n<Relations...>\n" + indent + "    {";
			for (int i = 0; i < relations.size(); ++i)
				{
				str += "\n" + dump(relations.elementAt(i), indent + "    ");
				}
			str += "\n" + indent + "    }";
			}
		return(str);
		}
	}
