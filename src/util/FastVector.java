
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


package com.swfm.mica.util; 
/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class FastVector implements Cloneable
	{
	private		Object[]	array;
	private		int		elementCount;
	private		int		capacityIncrement;
	
	public				FastVector()
		{
		this(2, 0);
		}

	public				FastVector(int initialCapacity)
		{
		this(initialCapacity, 0);
		}
	public				FastVector(int initialCapacity, int capacityIncrement)
		{
		array = new Object[initialCapacity];
		this.capacityIncrement = capacityIncrement;
		}

	public				FastVector(FastVector other)
		{
		elementCount = other.elementCount;
		capacityIncrement = other.capacityIncrement;
		array = new Object[elementCount];
		System.arraycopy(other.array, 0, array, 0, elementCount);
		}

	public		void		append(Object[] srcArray)
		{
		if (srcArray.length > 0)
			{
			Object[] newArray = new Object[elementCount + srcArray.length];
			System.arraycopy(array, 0, newArray, 0, elementCount);
			System.arraycopy(srcArray, 0, newArray, elementCount, srcArray.length);
			array = newArray;
			elementCount += srcArray.length;
			}
		}
	public		void		append(FastVector vector)
		{
		int size = vector.size();
		for (int i = 0; i < size; ++i)
			addElement(vector.elementAt(i));
		}
	public		void		copyInto(Object[] arrayCopy)
		{
		System.arraycopy(array, 0, arrayCopy, 0, elementCount);
		}
	public		Object[]	toArray()
		{
		Object[] arrayCopy = new Object[elementCount];
		System.arraycopy(array, 0, arrayCopy, 0, elementCount);
		return(arrayCopy);
		}
	public		Object		clone()
		{
		try	{
			FastVector f = (FastVector)super.clone();
			f.array = new Object[elementCount];
			System.arraycopy(array, 0, f.array, 0, elementCount);
			return(f);
			}
		catch (CloneNotSupportedException e)
			{
			return(null);
			}
		}
	public		void		copy(FastVector vector)
		{
		removeAllElements();
		append(vector);
		}
	public		FastVector	copy()
		{
		return((FastVector )clone());
		}
	
	public 		void		addElement(Object obj)
		{
		if (elementCount >= array.length)
			upgradeCapacity();
		array[elementCount++] = obj;
		}

	public final	Object		elementAt(int index)
		{
		if (index >= elementCount)
	    		throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		return(array[index]);
		}
	public final	Object		lastElement()
		{
		if (elementCount > 0)
			return(array[elementCount - 1]);
		throw new ArrayIndexOutOfBoundsException("No lastElement in zero-sized vector");
		}
	public		void		insertElementAt(Object obj, int index)
		{
		if (elementCount >= array.length)
			upgradeCapacity();
		System.arraycopy(array, index, array, index + 1, elementCount - index);
		array[index] = obj;
		elementCount++;
		}
	public		void		setElementAt(Object obj, int index)
		{
		if (index >= elementCount)
			throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		array[index] = obj;
		}
	public		void		removeElementAt(int index)
		{
		if (index < elementCount - 1)
			System.arraycopy(array, index + 1, array, index, elementCount - index - 1);
		elementCount--;
		array[elementCount] = null;
		}
	public		boolean		removeElement(Object obj)
		{
		int i = indexOf(obj);
		if (i >= 0)
			{
			removeElementAt(i);
			return(true);
			}
		return(false);
		}
	public		int		indexOf(Object obj)
		{
		for (int i = 0 ; i < elementCount ; i++)
			{
			// Use == here instead???
			if (obj.equals(array[i]))
				{
				return(i);
				}
			}
		return(-1);
		}

	public		boolean		contains(Object obj)
		{
		for (int i = 0 ; i < elementCount ; i++)
			{
			if ((obj == array[i]) || ((obj != null) && (obj.equals(array[i]))))
				{
				return(true);
				}
			}
		return(false);
		}

	public		void		removeAllElements()
		{
		for (int i = 0; i < elementCount; i++)
			array[i] = null;
		elementCount = 0;
		}
	public		void		removeAll(FastVector otherVector)
		{
		for (int i = 0; i < elementCount; ++i)
			{
			int otherVectorSize = otherVector.size();
			for (int j = 0; j < otherVectorSize; ++j)
				{
				Object other = otherVector.elementAt(j);
				if ((array[i] == other) || ((array[i] != null) && (array[i].equals(other))))
					{
					removeElementAt(i);
					--i;
					break;
					}
				}
			}
		}
	public final	int		size()
		{
		return(elementCount);
		}

	public 		void		reverseOrder()
		{
		for (int i = 0; i < elementCount/2; ++i)
			{
			Object tmp = array[i];
			array[i] = array[elementCount - i - 1];
			array[elementCount - i - 1] = tmp;
			}
		}

	private final	void		upgradeCapacity()
		{
		int newSize = (capacityIncrement == 0) ?
			array.length * 2 : array.length + capacityIncrement;
		Object[] newArray = new Object[newSize];
		System.arraycopy(array, 0, newArray, 0, elementCount);
		array = newArray;
		}
	public		String		toString()
		{
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName() + "." + Integer.toString(hashCode() << 1 >>> 1, 16));
		buffer.append("[\n");
		for (int i = 0; i < elementCount; ++i)
			{
			buffer.append("    ");
			if (array[i] != null)
				buffer.append(array[i].toString());
			else
				buffer.append("null");
			buffer.append("\n");
			}
		buffer.append("]");
		return(buffer.toString());
		}
	public		boolean		equals(Object other)
		{
		if (!(other instanceof FastVector))
			return(false);

		FastVector others = (FastVector )other;

		if (others.size() != size())
			return(false);

		for (int i = 0; i < size(); ++i)
			{
			if (!elementAt(i).equals(others.elementAt(i)))
				return(false);
			}
		return(true);
		}
	public		int		hashCode()
		{
		int code = 0;
		for (int i = 0; i < size(); ++i)
			{
			int hash = elementAt(i).hashCode();
			code += hash;
			code |= hash;
			}
		return(code);
		}
	}
