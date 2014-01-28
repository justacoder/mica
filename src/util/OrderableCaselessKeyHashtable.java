
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
import com.swfm.mica.util.CaselessKeyHashtable;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class OrderableCaselessKeyHashtable extends CaselessKeyHashtable implements Cloneable
	{
	private		Strings		keys;
	private		boolean		isOrdered;

	public				OrderableCaselessKeyHashtable()
		{
		setIsOrdered(true);
		}
	public				OrderableCaselessKeyHashtable(int initialCapacity)
		{
		super(initialCapacity);
		setIsOrdered(true);
		}
	public				OrderableCaselessKeyHashtable(boolean isOrdered)
		{
		setIsOrdered(isOrdered);
		}
	public				OrderableCaselessKeyHashtable(int initialCapacity, boolean isOrdered)
		{
		super(initialCapacity);
		setIsOrdered(isOrdered);
		}

	public		void		setIsOrdered(boolean flag)
		{
		if (isOrdered == flag)
			return;

		isOrdered = flag;
		if (isOrdered)
			keys = new Strings();
		else
			keys = null;
		}
	public		boolean		isOrdered()
		{
		return(isOrdered);
		}

	public		Object		put(Object key, Object value)
		{
		if ((isOrdered) && (!keys.contains(key)))
			keys.addElement((String )key);
		return(super.put(key, value));
		}
	public		Object		remove(Object key)
		{
		if (isOrdered)
			keys.removeElement((String )key);
		return(super.remove(key));
		}
	public		Strings		getKeys()
		{
		return(keys);
		}
	public		Object		clone()
		{
		OrderableCaselessKeyHashtable t = (OrderableCaselessKeyHashtable )super.clone();
		if (isOrdered)
			t.keys = new Strings(keys);
		return(t);
		}
	}


