
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Toolkit                   *
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
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class TypedVector
	{
	protected 	FastVector	vector = new FastVector();
	
	public				TypedVector()
		{
		}
	public		void		removeAllElements()
		{
		vector.removeAllElements();
		}
	public		void		removeElementAt(int index)
		{
		vector.removeElementAt(index);
		}
	public		void		append(Object[] srcArray)
		{
		vector.append(srcArray);
		}
	public		void		append(FastVector vector)
		{
		this.vector.append(vector);
		}
	public		void		append(TypedVector typedVector)
		{
		vector.append(typedVector.vector);
		}
	public		void		removeAll(TypedVector typedVector)
		{
		vector.removeAll(typedVector.vector);
		}
	public		int		indexOf(Object obj)
		{
		return(vector.indexOf(obj));
		}
	public 		int		size()
		{
		return(vector.size());
		}
	public		String		toString()
		{
		return(vector.toString());
		}
	public		boolean		equals(Object other)
		{
		return(vector.equals(other));
		}
	public		int		hashCode()
		{
		return(vector.hashCode());
		}
	}

