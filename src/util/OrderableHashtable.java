
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

import com.swfm.mica.util.Strings;
import java.util.Hashtable;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class OrderableHashtable extends Hashtable
	{
	private		Strings		keys;
	private		boolean		isOrdered;

	public				OrderableHashtable()
		{
		this(true);
		}

	public				OrderableHashtable(boolean isOrdered)
		{
		this.isOrdered = isOrdered;
		if (isOrdered)
			keys = new Strings();
		}

	public		void		setIsOrdered(boolean flag)
		{
		if (this.isOrdered == isOrdered)
			return;

		this.isOrdered = isOrdered;
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
		if ((isOrdered) && (!keys.contains((String )key)))
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
	}


