
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
public class MiPolyConstraint extends MiLayout
	{
	private		Vector		constraints 	= new Vector();


	public				MiPolyConstraint()
		{
		}
	public				MiPolyConstraint(MiContainer target)
		{
		setTarget(target);
		}

	public		void		doLayout()
		{
		int changesMade = 1;
		int numIterations = 1;
		while ((changesMade != 0) && (numIterations < 100))
			{
			changesMade = 0;
			for (int i = 0; i < constraints.size(); ++i)
				{
				changesMade += ((MiLayoutConstraint )constraints.elementAt(i)).reCalc();
				}
			++numIterations;
			}
		}
	public		void		setMargins(MiDistance margin)
		{
		for (int i = 0; i < constraints.size(); ++i)
			{
			((MiLayoutConstraint )constraints.elementAt(i)).setMargin(margin);
			}
		}
	public		void		appendConstraint(MiLayoutConstraint c)
		{
		constraints.addElement(c);
		}
	public		void		appendConstraint(String type, MiPart subject)
		{
		constraints.addElement(new MiRelativeLocationConstraint(subject, type, this));
		}
	public		void		appendConstraint(String type, MiPart subject, MiDistance w)
		{
		constraints.addElement(new MiRelativeLocationConstraint(subject, type, this, w));
		}
	public		boolean		isIndependantOfTargetPosition()
		{
		return(false);
		}
	public		boolean		determinesPreferredAndMinimumSizes()
		{
		return(false);
		}
	public		String		toString()
		{
		String str = new String(MiDebug.getMicaClassName(this));
		if (constraints.size() > 0)
			{
			str += "<";
			for (int i = 0; i < constraints.size(); ++i)
				{
				if (i > 0)
					str += " + ";
				str += constraints.elementAt(i).toString();
				}
			str += ">";
			}
		return(str);
		}
	}

