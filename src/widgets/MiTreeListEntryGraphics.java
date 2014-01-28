
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTreeListEntryGraphics extends MiRowLayout
	{
	private		MiPart		icon;
	private		MiPart		label;



	public				MiTreeListEntryGraphics()
		{
		setAlleySpacing(4);
		setElementVSizing(Mi_NONE);
		}

	public		void		setIcon(MiPart newIcon)
		{
		if (icon != null)
			icon.replaceSelf(newIcon);
		else
			insertPart(newIcon, 0);
		icon = newIcon;
		}
	public		MiPart		getIcon()
		{
		return(icon);
		}
	public		void		setLabel(MiPart newLabel)
		{
		if (label != null)
			label.replaceSelf(newLabel);
		else
			appendPart(newLabel);
		label = newLabel;
		}
	public		MiPart		getLabel()
		{
		return(label);
		}
	public		void		setColor(java.awt.Color color)
		{
		if (label != null)
			label.setColor(color);
		}
	public		java.awt.Color	getColor()
		{
		if (label != null)
			return(label.getColor());
		return(super.getColor());
		}
	public		String		toString()
		{
		return(super.toString() 
			+ "[" + (label != null ? label.toString() : "<no label>") + "]"
			+ "[" + (icon != null ? icon.toString() : "<noicon>") + "]");
		}
	}

