
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
import com.swfm.mica.MiPropertyDescription; 
import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.CaselessKeyHashtable;
import com.swfm.mica.util.TypedVector;
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPropertyDescriptions extends TypedVector
	{
	private		String			displayName;
	private		String			targetClassName;
	private		FastVector		components;
	private		CaselessKeyHashtable	nameHashTable	= new CaselessKeyHashtable(23);



	public				MiPropertyDescriptions()
		{
		}
	public				MiPropertyDescriptions(String targetClassName)
		{
		this.targetClassName = targetClassName;
		}
	public				MiPropertyDescriptions(MiPropertyDescriptions descs)
		{
		appendPropertyDescriptionComponent(descs);
		}
	public				MiPropertyDescriptions(String targetClassName, MiPropertyDescriptions descs)
		{
		this.targetClassName = targetClassName;
		appendPropertyDescriptionComponent(descs);
		}
	public				MiPropertyDescriptions(MiPropertyDescription desc)
		{
		addElement(desc);
		}
	public		void		setDisplayName(String name)
		{
		displayName = name;
		}
	public		String		getDisplayName()
		{
		return(displayName);
		}
	public		void		setTargetClassName(String name)
		{
		targetClassName = name;
		}
	public		String		getTargetClassName()
		{
		return(targetClassName);
		}
	public		MiPropertyDescriptions	getPropertyDescriptionsForClass(String className)
		{
		if (className.equals(targetClassName))
			return(this);
		for (int i = 0; (components != null) && (i < components.size()); ++i)
			{
			if (className.equals(getPropertyDescriptionComponent(i).getTargetClassName()))
				return(getPropertyDescriptionComponent(i));
			}
		return(null);
		}
	public		void		insertPropertyDescriptionComponent(MiPropertyDescriptions descs, int index)
		{
		if (components == null)
			components = new FastVector();
		components.insertElementAt(descs, index);
		}
	public		void		appendPropertyDescriptionComponent(MiPropertyDescriptions descs)
		{
		if (components == null)
			components = new FastVector();
		components.addElement(descs);
		}
	public		int		getNumberOfPropertyDescriptionComponents()
		{
		return(components != null ? components.size() : 0);
		}
	public		MiPropertyDescriptions	getPropertyDescriptionComponent(int index)
		{
		return((MiPropertyDescriptions )components.elementAt(index));
		}
	public		int		size()
		{
		int size = vector.size();
		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				size += getPropertyDescriptionComponent(i).size();
				}
			}
		return(size);
		}
	public		MiPropertyDescription	elementAt(int index)
		{
		int origIndex = index;
		if (index < vector.size())
			{
			return((MiPropertyDescription )vector.elementAt(index));
			}
		index -= vector.size();
		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				int size = getPropertyDescriptionComponent(i).size();
				if (index < size)
					{
					return(getPropertyDescriptionComponent(i).elementAt(index));
					}
				index -= getPropertyDescriptionComponent(i).size();
				}
			}
	    	throw new ArrayIndexOutOfBoundsException(origIndex + " >= " + size());
		}
	public		MiPropertyDescription	lastElement()
		{
		return((MiPropertyDescription )vector.lastElement());
		}
	public		void		addElement(MiPropertyDescription desc)
		{
		vector.addElement(desc);
		nameHashTable.put(desc.getName(), desc);
		if (!Utility.isEqualTo(desc.getName(), desc.getDisplayName()))
			nameHashTable.put(desc.getDisplayName(), desc);
		}
	public		void		setElementAt(MiPropertyDescription desc, int index)
		{
		vector.setElementAt(desc, index);
		nameHashTable.put(desc.getName(), desc);
		if (!Utility.isEqualTo(desc.getName(), desc.getDisplayName()))
			{
			nameHashTable.put(desc.getDisplayName(), desc);
			}
		}
	public		void		insertElementAt(MiPropertyDescription desc, int index)
		{
		if (index < vector.size())
			{
			vector.insertElementAt(desc, index);
			nameHashTable.put(desc.getName(), desc);
			if (!Utility.isEqualTo(desc.getName(), desc.getDisplayName()))
				nameHashTable.put(desc.getDisplayName(), desc);
			}
		else
			{
			index -= vector.size();
			if (components != null)
				{
				for (int i = 0; i < components.size(); ++i)
					{
					int size = getPropertyDescriptionComponent(i).size();
					if (index < size)
						{
						getPropertyDescriptionComponent(i).insertElementAt(desc, index);
						return;
						}
					}
				}
			}
		}
	public		boolean		removeElement(MiPropertyDescription desc)
		{
		boolean status = vector.removeElement(desc);
		updateNameHashTable();
		if (!status)
			{
			if (components != null)
				{
				for (int i = 0; i < components.size(); ++i)
					{
					status = getPropertyDescriptionComponent(i).removeElement(desc);
					if (status)
						return(true);
					}
				}
			}
		return(false);
		}
	public		void		removeAllElements()
		{
		vector.removeAllElements();
		updateNameHashTable();
		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				getPropertyDescriptionComponent(i).removeAllElements();
				}
			}
		}
	public		void		removeElementAt(int index)
		{
		if (index < vector.size())
			{
			vector.removeElementAt(index);
			updateNameHashTable();
			}
		else
			{
			index -= vector.size();
			if (components != null)
				{
				for (int i = 0; i < components.size(); ++i)
					{
					int size = getPropertyDescriptionComponent(i).size();
					if (index < size)
						{
						getPropertyDescriptionComponent(i).removeElementAt(index);
						return;
						}
					index -= size;
					}
				}
			}
		}
	public		MiPropertyDescriptions	copy()
		{
		MiPropertyDescriptions copy = new MiPropertyDescriptions();
		copy.displayName = displayName;
		copy.targetClassName = targetClassName;
		copy.components = components;
		copy.append(vector);
		return(copy);
		}
	public		MiPropertyDescriptions	deepCopy()
		{
//MiDebug.println("this = " +this);
		MiPropertyDescriptions copy = new MiPropertyDescriptions();
		copy.displayName = displayName;
		copy.targetClassName = targetClassName;
		copy.components = components;
		FastVector copyVector = new FastVector();
		for (int i = 0; i < vector.size(); ++i)
			{
			copyVector.addElement(((MiPropertyDescription )vector.elementAt(i)).copy());
			}
		copy.append(copyVector);
//MiDebug.println("copy = " +copy);
		return(copy);
		}
	public		void		append(Object[] srcArray)
		{
		vector.append(srcArray);
		updateNameHashTable();
		}
	public		void		append(FastVector vector)
		{
		this.vector.append(vector);
		updateNameHashTable();
		}
	public		void		append(TypedVector typedVector)
		{
		super.append(typedVector);
		updateNameHashTable();
		}
	public		int		indexOf(MiPropertyDescription desc)
		{
		int index = vector.indexOf(desc);
		if (index != -1)
			return(index);

		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				index = getPropertyDescriptionComponent(i).indexOf(desc);
				if (index != -1)
					return(index);
				}
			}
		return(-1);
		}
	public		boolean		contains(MiPropertyDescription desc)
		{
		if (vector.contains(desc))
			return(true);
		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				if (getPropertyDescriptionComponent(i).contains(desc))
					return(true);
				}
			}
		return(false);
		}
	public		MiPropertyDescription	elementAt(String propertyName)
		{
		MiPropertyDescription desc = null;
		desc = (MiPropertyDescription )nameHashTable.get(propertyName);
		if (desc != null)
			return(desc);

		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				desc = getPropertyDescriptionComponent(i).elementAt(propertyName);
				if (desc != null)
					return(desc);
				}
			}
		return(null);
		}
	public		boolean		contains(String propertyName)
		{
		if (nameHashTable.get(propertyName) != null)
			return(true);

		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				if (getPropertyDescriptionComponent(i).contains(propertyName))
					return(true);
				}
			}
		return(false);
		}
	public		MiPropertyDescription[]	toArray()
		{
		return((MiPropertyDescription[] )vector.toArray());
		}
	protected	void		updateNameHashTable()
		{
		nameHashTable.clear();
		for (int i = 0; i < size(); ++i)
			{
			MiPropertyDescription desc = elementAt(i);
			nameHashTable.put(desc.getName(), desc);
			if (!Utility.isEqualTo(desc.getName(), desc.getDisplayName()))
				nameHashTable.put(desc.getDisplayName(), desc);
			}
		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				getPropertyDescriptionComponent(i).updateNameHashTable();
				}
			}
		}
	public		boolean		equals(Object other)
		{
		if (this == other)
			{
			return(true);
			}
		if (other instanceof MiPropertyDescriptions)
			{
			if (hashCode() == other.hashCode())
				{
				MiPropertyDescriptions otherPd = (MiPropertyDescriptions )other;
				if (size() == otherPd.size())
					{
					for (int i = 0; i < size(); ++i)
						{
//MiDebug.println("compare : " + elementAt(i) + " AND " + otherPd.elementAt(i));
						if ((elementAt(i) != otherPd.elementAt(i))
							&& (!elementAt(i).equals(otherPd.elementAt(i))))
							{
//MiDebug.println("element not equals: " + elementAt(i) + " AND " + otherPd.elementAt(i));
							return(false);
							}
						}
					if ((getNumberOfPropertyDescriptionComponents() 
						!= otherPd.getNumberOfPropertyDescriptionComponents()))
						{
//MiDebug.println("num components not equals: " + getNumberOfPropertyDescriptionComponents() + " AND " + otherPd.getNumberOfPropertyDescriptionComponents());
						return(false);
						}
					if (components != null)
						{
						for (int i = 0; i < components.size(); ++i)
							{
							if ((getPropertyDescriptionComponent(i) != 
									otherPd.getPropertyDescriptionComponent(i))
								&& (!getPropertyDescriptionComponent(i).equals(
									otherPd.getPropertyDescriptionComponent(i))))
								{
								return(false);
								}
							}
						}
					return(true);
					}
				}
			}
//MiDebug.println("just plain not equals");
		return(false);
		}
	public		int		hashCode()
		{
		int hash = super.hashCode();
		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				hash += getPropertyDescriptionComponent(i).hashCode();
				}
			}
		return(hash);
		}
	public		String		toString()
		{
		return(toString(""));
		}
	public		String		toString(String indent)
		{
		String str = indent + MiDebug.getMicaClassName(this) + super.hashCode() + "@" + hashCode() + ": " + displayName 
			+ "(for class: " + (targetClassName == null ? "?" : targetClassName) + ")\n";
		int size = size();
		str += indent + "{\n";
		for (int i = 0; i < vector.size(); ++i)
			{
			str += indent + (MiPropertyDescription )vector.elementAt(i) + "\n";
			}
		if (components != null)
			{
			for (int i = 0; i < components.size(); ++i)
				{
				str += indent + getPropertyDescriptionComponent(i).toString(indent + "    ") + "\n";
				}
			}
		str += indent + "}\n";
		return(str);
		}
	}

