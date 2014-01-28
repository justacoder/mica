

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

/* --------------------------------------------------------------------
* The Objects pointed to in this vector are never deallocated. This permits
* the vector to grow and shrink without deallocation/allocation overhead.
*
* numElements are in the vector and can be accessed as an ordinary vector.
* vector.size() are allocated in the vector and can be accessed through:
*	peekElementAt
*	useTheNextElement
*
* override:
* 	makeAnElement
*
* Example:
*	for (int i = 0; i < 100; ++i)
*		((StringBuffer )cacheVector.theElementAt(i)).append("\n"));
* --------------------------------------------------------------------*/
/**
 * A class of the MICA Graphics Framework
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public abstract class CacheVector
	{
	private		int		numElements;
	private		FastVector	vector = new FastVector();

	
	public				CacheVector()
		{
		}
	public		void		removeAllElements()
		{
		numElements = 0;
		}
	public		void		removeElementAt(int index)
		{
		if (index >= numElements)
			throw new ArrayIndexOutOfBoundsException(index + " >= " + numElements);
		Object old = vector.elementAt(index);
		vector.removeElementAt(index);
		vector.addElement(old);
		--numElements;
		}
	public final	int		size()
		{
		return(numElements);
		}
	public 		int		cacheSize()
		{
		return(vector.size());
		}
	public		Object		clone()
		{
		try	{
			// Deep copy...
			CacheVector f = (CacheVector)super.clone();
			f.vector = vector.copy();
			//FIX for (int i = 0; i < f.vector.size(); ++i)
				//FIX f.vector.setElementAt(((Object )f.vector.elementAt(i)).clone(), i);
			return(f);
			}
		catch (CloneNotSupportedException e)
			{
			return(null);
			}
		}
	public		CacheVector	copy()
		{
		return((CacheVector )clone());
		}
	
/*
	public		void		addTheElement(Object obj)
		{
		if (numElements < vector.size())
			{
			vector.setElementAt(obj, numElements);
			}
		else
			{
			vector.addElement(obj);
			}
		++numElements;
		}
*/

	public		Object		theElementAt(int index)
		{
		if (index < numElements)
			return(vector.elementAt(index));
		throw new ArrayIndexOutOfBoundsException(index + " >= " + numElements);
		}

	public		Object		peekElementAt(int index)
		{
		if (index < vector.size())
			return(vector.elementAt(index));
		Object newOne = null;
		if ((newOne = makeAnElement()) != null)
			{
			vector.addElement(newOne);
			}
		return(newOne);
		}

	public		Object		useTheNextElement()
		{
		if (numElements < vector.size())
			return(vector.elementAt(numElements++));
		Object newOne = null;
		if ((newOne = makeAnElement()) != null)
			{
			vector.addElement(newOne);
			++numElements;
			}
		return(newOne);
		}

	public abstract	Object		makeAnElement();
			
	public		String		toString()
		{
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName() + "." + Integer.toString(hashCode() << 1 >>> 1, 16));
		buffer.append("[\n");
		for (int i = 0; i < numElements; ++i)
			{
			buffer.append(vector.elementAt(i).toString());
			buffer.append("\n");
			}
		buffer.append("]");
		return(buffer.toString());
		}
	}

