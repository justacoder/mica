
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
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiCustomLookAndFeels extends TypedVector
	{
	public				MiCustomLookAndFeels()
		{
		}
	public				MiCustomLookAndFeels(MiCustomLookAndFeels parts)
		{
		append(parts);
		}
	public				MiCustomLookAndFeels(MiiCustomLookAndFeel part)
		{
		addElement(part);
		}
	public				MiCustomLookAndFeels(MiiCustomLookAndFeel part1, MiiCustomLookAndFeel part2)
		{
		addElement(part1);
		addElement(part2);
		}
	public		MiiCustomLookAndFeel		elementAt(int index)
		{
		return((MiiCustomLookAndFeel )vector.elementAt(index));
		}
	public		MiiCustomLookAndFeel		lastElement()
		{
		return((MiiCustomLookAndFeel )vector.lastElement());
		}
	public		void		addElement(MiiCustomLookAndFeel part)
		{
		vector.addElement(part);
		}
	public		void		setElementAt(MiiCustomLookAndFeel part, int index)
		{
		vector.setElementAt(part, index);
		}
	public		void		insertElementAt(MiiCustomLookAndFeel part, int index)
		{
		vector.insertElementAt(part, index);
		}
	public		boolean		removeElement(MiiCustomLookAndFeel part)
		{
		return(vector.removeElement(part));
		}
	public		int		indexOf(MiiCustomLookAndFeel part)
		{
		return(vector.indexOf(part));
		}
	public		boolean		contains(MiiCustomLookAndFeel part)
		{
		return(vector.contains(part));
		}
	public		MiiCustomLookAndFeel[]	toArray()
		{
		MiiCustomLookAndFeel[] arrayCopy = new MiiCustomLookAndFeel[vector.size()];
		System.arraycopy(vector.toArray(), 0, arrayCopy, 0, vector.size());
		return(arrayCopy);
		}
	public		boolean		equals(Object other)
		{
		if (!(other instanceof MiCustomLookAndFeels))
			return(false);

		MiCustomLookAndFeels others = (MiCustomLookAndFeels )other;

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
		if (!(other instanceof MiCustomLookAndFeels))
			return(false);

		MiCustomLookAndFeels others = (MiCustomLookAndFeels )other;

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

