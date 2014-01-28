
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

import com.swfm.mica.util.CaselessKeyHashtable; 
import com.swfm.mica.util.TypedVector; 
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPropertyWidgets extends TypedVector implements MiiActionHandler, MiiActionTypes
	{

	public		MiPropertyWidget 	elementAt(int index)
		{
		return((MiPropertyWidget )vector.elementAt(index));
		}
	public		void		addElement(MiPropertyWidget pw)
		{
		vector.addElement(pw);
		}
	
	public		void		appendPropertyWidget(Strings properties)
		{
		for (int i = 0; i < properties.size(); ++i)
			{
			appendPropertyWidget(properties.elementAt(i));
			}
		}
	// name=XXX, value=YYY [, values={x,y,z,...}] [widgetType=...][,type=Integer|Real|Text|]
	public		void		appendPropertyWidget(String nameValuePairs)
		{
		CaselessKeyHashtable hashtable = new CaselessKeyHashtable();
		Utility.parsePropertyString(hashtable, nameValuePairs);
		String readOnly = (String )hashtable.get("readOnly");
		addElement(new MiPropertyWidget(
						(String )hashtable.get("name"),
						(String )hashtable.get("value"),
						(String )hashtable.get("widgetType"),
						(Strings )null, // possbile Values
						readOnly == null ? false : Utility.toBoolean(readOnly)));
		}
	public		void		appendPropertyOverlay(
						MiPropertyWidget overlayToggle, 
						String[] valueOfToggleToEnableEachOverlay, 
						MiPropertyWidget[] overlays, 
						boolean toggleOverlayVisibilities)
		{
		addElement(overlayToggle);
		for (int i = 0; i < overlays.length; ++i)
			{
			addElement(overlays[i]);
			}
		String toggleValue = overlayToggle.getWidget().getValue();
		for (int i = 0; i < overlays.length; ++i)
			{
			MiWidget widget = overlays[i].getWidget();
			if (widget == null)
				widget = overlays[i].makeWidget();
			if (!toggleValue.equals(valueOfToggleToEnableEachOverlay[i]))
				{
				if (toggleOverlayVisibilities)
					widget.setVisible(false);
				else
					widget.setSensitive(false);
				}
			}
		if (overlayToggle.getWidget() == null)
			overlayToggle.makeWidget();
		MiAction action = new MiAction(this, Mi_SELECTED_ACTION, Mi_DESELECTED_ACTION, overlayToggle);
		action.setResource("toggleValues", valueOfToggleToEnableEachOverlay);
		action.setResource("overlays", overlays);
		action.setResource("toggleVisibility", new Boolean(toggleOverlayVisibilities));
		overlayToggle.getWidget().appendActionHandler(action);
		}

	public		void		appendPropertyWidget(MiPropertyWidget p)
		{
		addElement(p);
		}
	public		void		createWidgetsFromSpecification()
		{
		for (int i = 0; i < size(); ++i)
			{
			MiPropertyWidget pw = elementAt(i);
			if (pw.getWidget() == null)
				pw.makeWidget();

			}
		}
	public		MiPropertyWidget	getPropertyWidget(String name)
		{
		for (int i = 0; i < size(); ++i)
			{
			MiPropertyWidget pw = elementAt(i);
			if ((pw.getName() != null) && (pw.getName().equals(name)))
				return(pw);
			}
		return(null);
		}
	public		boolean		processAction(MiiAction action)
		{
		if ((action.hasActionType(Mi_SELECTED_ACTION))
			|| (action.hasActionType(Mi_DESELECTED_ACTION)))
			{
			MiPropertyWidget overlayToggle = (MiPropertyWidget )action.getActionUserInfo();
			String[] toggleValues = (String[] )action.getResource("toggleValues");
			MiPropertyWidget[] overlays = (MiPropertyWidget[] )action.getResource("overlays");
			boolean toggleOverlayVisibilities 
				= ((Boolean )action.getResource("toggleVisibility")).booleanValue();

			String toggleValue = overlayToggle.getWidget().getValue();
			for (int i = 0; i < overlays.length; ++i)
				{
				if (!toggleValue.equals(toggleValues[i]))
					{
					if (toggleOverlayVisibilities)
						overlays[i].getWidget().setVisible(false);
					else
						overlays[i].getWidget().setSensitive(false);
					}
				else
					{
					if (toggleOverlayVisibilities)
						overlays[i].getWidget().setVisible(true);
					else
						overlays[i].getWidget().setSensitive(true);
					}
				}
			}
		return(true);
		}
	}

