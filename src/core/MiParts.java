
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
import com.swfm.mica.util.TypedVector;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiParts extends TypedVector
	{
	public				MiParts()
		{
		}
	public				MiParts(MiParts parts)
		{
		append(parts);
		}
	public				MiParts(MiPart part)
		{
		addElement(part);
		}
	public				MiParts(MiPart part1, MiPart part2)
		{
		addElement(part1);
		addElement(part2);
		}
	public		MiPart		get(int index)
		{
		return((MiPart )vector.elementAt(index));
		}
	public		MiPart		elementAt(int index)
		{
		return((MiPart )vector.elementAt(index));
		}
	public		MiPart		lastElement()
		{
		return((MiPart )vector.lastElement());
		}
	public		void		addElement(MiPart part)
		{
		vector.addElement(part);
		}
	public		void		add(MiPart part)
		{
		vector.addElement(part);
		}
	public		void		setElementAt(MiPart part, int index)
		{
		vector.setElementAt(part, index);
		}
	public		void		insertElementAt(MiPart part, int index)
		{
		vector.insertElementAt(part, index);
		}
	public		boolean		removeElement(MiPart part)
		{
		return(vector.removeElement(part));
		}
	public		int		indexOf(MiPart part)
		{
		return(vector.indexOf(part));
		}
	public		boolean		contains(MiPart part)
		{
		return(vector.contains(part));
		}
	public		MiPart[]	toArray()
		{
		MiPart[] arrayCopy = new MiPart[vector.size()];
		System.arraycopy(vector.toArray(), 0, arrayCopy, 0, vector.size());
		return(arrayCopy);
		}
	public		boolean		equals(Object other)
		{
		if (!(other instanceof MiParts))
			return(false);

		MiParts others = (MiParts )other;

		if (others.size() != size())
			return(false);

		for (int i = 0; i < size(); ++i)
			{
			if (elementAt(i) != others.elementAt(i))
				return(false);
			}
		return(true);
		}
	public		boolean		equalsIgnoreOrder(Object other)
		{
		if (!(other instanceof MiParts))
			return(false);

		MiParts others = (MiParts )other;

		if (others.size() != size())
			return(false);

		for (int i = 0; i < size(); ++i)
			{
			if (!contains(others.elementAt(i)))
				return(false);
			}
		return(true);
		}
	public		int		hashCode()
		{
		int code = 0;
		for (int i = 0; i < size(); ++i)
			{
			code += elementAt(i).hashCode();
			}
		return(code);
		}
	}

