
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
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class CaselessKeyHashtable extends Hashtable
	{
	private		boolean		ignoreCase = true;

	public				CaselessKeyHashtable(boolean ignoreCase, int initialCapacity)
		{
		this(initialCapacity);
		this.ignoreCase = ignoreCase;
		}
	public				CaselessKeyHashtable(int initialCapacity)
		{
		super(initialCapacity);
		}
	public				CaselessKeyHashtable()
		{
		}
	public		void		setIgnoreCase(boolean flag)
		{
		ignoreCase = flag;
		Strings keys = new Strings();
		for (Enumeration e = keys(); e.hasMoreElements();)
			{
			Object key = e.nextElement();
			if (key instanceof String)
				keys.addElement((String)key);
			}
		for (int i = 0; i < keys.size(); ++i)
			{
			String key = keys.elementAt(i);
			String lowerCaseKey = key.toLowerCase();

			if (!lowerCaseKey.equals(key))
				{
				if (ignoreCase)
					put(lowerCaseKey, super.get(key));
				else
					remove(lowerCaseKey);
				}
			}
		}
	public		boolean		getIgnoreCase()
		{
		return(ignoreCase);
		}
	public synchronized Object 	put(Object key, Object obj)
		{
		if ((key instanceof String) && (ignoreCase))
			return(super.put(((String )key).toLowerCase(), obj));
		return(super.put(key, obj));
		}
	public synchronized Object 	get(Object key)
		{
		if ((key instanceof String) && (ignoreCase))
			return(super.get(((String )key).toLowerCase()));
		else
			return(super.get(key));
		}
	public synchronized Object 	remove(Object key)
		{
		if ((key instanceof String) && (ignoreCase))
			return(super.remove(((String )key).toLowerCase()));
		return(super.remove(key));
		}
	public		void		removeAllElements()
		{
		clear();
		}
	}



