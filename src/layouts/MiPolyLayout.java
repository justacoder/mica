
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
import java.util.Vector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPolyLayout extends MiLayout
	{
	private		Vector		layouts 	= new Vector();
	private		boolean		determinesPreferredAndMinimumSizes;




	public				MiPolyLayout()
		{
		}
	public				MiPolyLayout(MiContainer target)
		{
		setTarget(target);
		}

	public		void		doLayout()
		{
		for (int i = 0; i < layouts.size(); ++i)
			{
			((MiiLayout )layouts.elementAt(i)).layoutParts();
			}
		}
	public		void		appendLayout(MiiLayout c)
		{
		layouts.addElement(c);
		if (getTarget() != null)
			c.setTarget(getTarget());
		if (c.determinesPreferredAndMinimumSizes())
			determinesPreferredAndMinimumSizes = true;
		}
	public		void		setTarget(MiPart target)
		{
		super.setTarget(target);
		for (int i = 0; i < layouts.size(); ++i)
			{
			((MiiLayout )layouts.elementAt(i)).setTarget(target);
			}
		}
	public		boolean		isIndependantOfTargetPosition()
		{
		return(false);
		}
	public		boolean		determinesPreferredAndMinimumSizes()
		{
		return(determinesPreferredAndMinimumSizes);
		}
	public		MiSize		getPreferredSize(MiSize size)
		{
		for (int i = 0; i < layouts.size(); ++i)
			{
			if (((MiiLayout )layouts.elementAt(i)).determinesPreferredAndMinimumSizes())
				{
				((MiiLayout )layouts.elementAt(i)).getPreferredSize(size);
				return(size);
				}
			}
		// ERROR
		return(size);
		}
	public		MiSize		getMinimumSize(MiSize size)
		{
		for (int i = 0; i < layouts.size(); ++i)
			{
			if (((MiiLayout )layouts.elementAt(i)).determinesPreferredAndMinimumSizes())
				{
				((MiiLayout )layouts.elementAt(i)).getMinimumSize(size);
				return(size);
				}
			}
		// ERROR
		return(size);
		}
	public		void		invalidateLayout()
		{
		for (int i = 0; i < layouts.size(); ++i)
			{
			((MiiLayout )layouts.elementAt(i)).invalidateLayout();
			}
		}
	public		String		toString()
		{
		String str = new String(MiDebug.getMicaClassName(this));
		if (layouts.size() > 0)
			{
			str += "[";
			for (int i = 0; i < layouts.size(); ++i)
				{
				if (i > 0)
					str += " + ";
				str += layouts.elementAt(i).toString();
				}
			str += "]";
			}
		return(str);
		}
	}

