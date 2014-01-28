
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
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTaskBarTab extends MiToggleButton
	{
	private		MiText		label;
	private		MiPart		icon;
	private		MiRowLayout	rowLayout;
	private		String		desiredValue = "";

	public				MiTaskBarTab()
		{
		setupTaskBarTab();
		}
	public				MiTaskBarTab(String text)
		{
		setupTaskBarTab();
		label = new MiText(text);
		rowLayout.appendPart(label);
		}
	public				MiTaskBarTab(MiPart icon, String text)
		{
		setupTaskBarTab();
		this.icon = icon;
		rowLayout.appendPart(icon);
		label = new MiText(text);
		rowLayout.appendPart(label);
		}
	public		void		setValue(String value)
		{
		desiredValue = value;
		if (label != null)
			{
			label.setText(value);
			}
		super.setValue(value);
		}
	public		String		getValue()
		{
		if (label != null)
			{
			return(label.getText());
			}
		return(super.getValue());
		}
	protected	void		adjustValue(String value)
		{
		if (label != null)
			{
			label.setText(value);
			}
		super.setValue(value);
		}
	protected	String		getDesiredValue()
		{
		return(desiredValue);
		}
	public		MiText		getText()
		{
		return(label);
		}
	public		void		setIcon(MiPart icon)
		{
		if (this.icon != null)
			{
			this.icon.replaceSelf(icon);
			}
		else
			{
			rowLayout.insertPart(icon, 0);
			}
		this.icon = icon;
		}
	public		MiPart		getIcon()
		{
		return(icon);
		}
	protected	void		setupTaskBarTab()
		{
		setContainerLayoutSpec(MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS_OR_OVERRIDDEN_PREFERRED_SIZE);

//		setDisplaysFocusBorder(false);
//		setBorderLook(Mi_RAISED_BORDER_LOOK);
		setBackgroundColor(MiColorManager.veryVeryLightGray);
		setSelectedBackgroundColor(MiColorManager.darkWhite);
		setPropertyValues(MiSystem.getPropertiesForClass("MiTaskBarTab"));

		rowLayout = new MiRowLayout();
		super.setLabel(rowLayout);
		rowLayout.setAlleyHSpacing(4);
		rowLayout.setElementVSizing(Mi_NONE);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	}



