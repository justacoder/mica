
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
public class LongVector implements Cloneable
	{
	private		long[]		array		= new long[2];
	private		int		elementCount	= 0;
	private		int		capacityIncrement = 0;
	
	public				LongVector()
		{
		this(2, 0);
		}

	public				LongVector(int initialCapacity)
		{
		this(initialCapacity, 0);
		}
	public				LongVector(int initialCapacity, int capacityIncrement)
		{
		array = new long[initialCapacity];
		this.capacityIncrement = capacityIncrement;
		}

	public		void		append(long[] srcArray)
		{
		long[] newArray = new long[elementCount + srcArray.length];
		System.arraycopy(array, 0, newArray, 0, elementCount);
		System.arraycopy(srcArray, 0, newArray, elementCount, srcArray.length);
		array = newArray;
		elementCount += srcArray.length;
		}
	public		void		append(LongVector vector)
		{
		int size = vector.size();
		for (int i = 0; i < size; ++i)
			addElement(vector.elementAt(i));
		}
	public		void		copyInto(long[] arrayCopy)
		{
		System.arraycopy(array, 0, arrayCopy, 0, elementCount);
		}
	public		long[]		toArray()
		{
		long[] arrayCopy = new long[elementCount];
		System.arraycopy(array, 0, arrayCopy, 0, elementCount);
		return(arrayCopy);
		}
	public		Object		clone()
		{
		try	{
			LongVector f = (LongVector)super.clone();
			f.array = new long[elementCount];
			System.arraycopy(array, 0, f.array, 0, elementCount);
			return(f);
			}
		catch (CloneNotSupportedException e)
			{
			return(null);
			}
		}
	public		LongVector	copy()
		{
		return((LongVector )clone());
		}
	
	public 		void		addElement(long value)
		{
		if (elementCount >= array.length)
			upgradeCapacity();
		array[elementCount++] = value;
		}

	public 		void		add(long value)
		{
		if (elementCount >= array.length)
			upgradeCapacity();
		array[elementCount++] = value;
		}

	public final	long		elementAt(int index)
		{
		if (index >= elementCount)
			throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		return(array[index]);
		}
	public final	long		get(int index)
		{
		if (index >= elementCount)
			throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		return(array[index]);
		}
	public final	long		lastElement()
		{
		if (elementCount > 0)
			return(array[elementCount - 1]);
		throw new ArrayIndexOutOfBoundsException("No lastElement in zero-sized vector");
		}
	public		void		insertElementAt(long value, int index)
		{
		if (elementCount >= array.length)
			upgradeCapacity();
		System.arraycopy(array, index, array, index + 1, elementCount - index);
		array[index] = value;
		elementCount++;
		}
	public		void		setElementAt(long value, int index)
		{
		if (index >= elementCount)
			throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		array[index] = value;
		}
	public		void		removeElementAt(int index)
		{
		System.arraycopy(array, index + 1, array, index, elementCount - index - 1);
		elementCount--;
		array[elementCount] = Long.MIN_VALUE;
		}
	public		boolean		removeElement(int value)
		{
		int i = indexOf(value);
		if (i >= 0)
			{
			removeElementAt(i);
			return(true);
			}
		return(false);
		}
	public 		void		removeLastElement()
		{
		if (size() > 0)
			removeElementAt(size() - 1);
		}
	public		int		indexOf(long value)
		{
		for (int i = 0 ; i < elementCount ; i++)
			{
			if (value == array[i])
				{
				return(i);
				}
			}
		return(-1);
		}

	public		boolean		contains(long value)
		{
		for (int i = 0 ; i < elementCount ; i++)
			{
			if (value == array[i])
				{
				return(true);
				}
			}
		return(false);
		}

	public		void		removeAllElements()
		{
		for (int i = 0; i < elementCount; i++)
			array[i] = Long.MIN_VALUE;
		elementCount = 0;
		}
	public final	int		size()
		{
		return(elementCount);
		}

	public		void		sort()
		{
		boolean unsorted = true;

		while (unsorted)
			{
			unsorted = false;
			for (int i = 0; i < elementCount - 1; ++i)
				{
				long val1 = array[i];
				if (val1 > array[i + 1])
					{
					array[i] = array[i + 1];
					array[i + 1] = val1;
					unsorted = true;
					}
				}
			}
		}
	public 		void		reverseOrder()
		{
		for (int i = 0; i < elementCount/2; ++i)
			{
			long tmp = array[i];
			array[i] = array[elementCount - i - 1];
			array[elementCount - i - 1] = tmp;
			}
		}

	private final	void		upgradeCapacity()
		{
		int newSize = (capacityIncrement == 0) ?
			array.length * 2 : array.length + capacityIncrement;
		long[] newArray = new long[newSize];
		System.arraycopy(array, 0, newArray, 0, elementCount);
		array = newArray;
		}
	public		String		toString()
		{
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName() + "." + Long.toString(hashCode() << 1 >>> 1, 16));
		buffer.append("[\n");
		for (int i = 0; i < elementCount; ++i)
			{
			buffer.append(array[i] + "\n");
			}
		buffer.append("]");
		return(buffer.toString());
		}
	}
