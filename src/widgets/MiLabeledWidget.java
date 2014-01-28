
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
public class MiLabeledWidget extends MiWidget implements MiiActionHandler
	{
	private	MiText			label;
	private	MiWidget		widget;
	private	boolean			inSetAttributes;

	public				MiLabeledWidget(String label, MiWidget widget)
		{
		setupLabeledWidget(label, widget);
		appendPart(this.label);
		appendPart(widget);
		}
	public				MiLabeledWidget(MiWidget widget, String label)
		{
		setupLabeledWidget(label, widget);
		appendPart(widget);
		appendPart(this.label);
		}
	public		void		select(boolean flag)
		{
		widget.select(flag);
		super.select(flag);
		}
	public		boolean		isSelected()
		{
		return(widget != null ? widget.isSelected() : super.isSelected());
		}
	public		void		setSensitive(boolean flag)
		{
		widget.setSensitive(flag);
		label.setSensitive(flag);
		}
	public		boolean		isSensitive()
		{
		return(widget != null ? widget.isSensitive() : super.isSensitive());
		}
	public		void		setValue(String value)
		{
		widget.setValue(value);
		}
	public		String		getValue()
		{
		return(widget.getValue());
		}
	public		MiText		getLabel()
		{
		return(label);
		}
	/**
		Mi_LEFT_LOCATION
		Mi_RIGHT_LOCATION
		Mi_BOTTOM_LOCATION
		Mi_TOP_LOCATION
	*/
	public		void		setLabelLocation(int location)
		{
		switch (location)
			{
			case Mi_LEFT_LOCATION	:
				if (!(getLayout() instanceof MiRowLayout))
					{
					MiRowLayout layout = new MiRowLayout();
					layout.setAlleyHSpacing(4);
					layout.setElementVSizing(Mi_NONE);
					setLayout(layout);
					}
				if (getPart(0) != label)
					{
					removePart(label);
					insertPart(label, 0);
					}
				break;
			case Mi_RIGHT_LOCATION	:
				if (!(getLayout() instanceof MiRowLayout))
					{
					MiRowLayout layout = new MiRowLayout();
					layout.setAlleyHSpacing(4);
					layout.setElementVSizing(Mi_NONE);
					setLayout(layout);
					}
				if (getPart(1) != label)
					{
					removePart(label);
					appendPart(label);
					}
				break;
			case Mi_BOTTOM_LOCATION	:
				if (!(getLayout() instanceof MiColumnLayout))
					{
					MiColumnLayout layout = new MiColumnLayout();
					layout.setAlleyVSpacing(4);
					layout.setElementHSizing(Mi_NONE);
					setLayout(layout);
					}
				if (getPart(1) != label)
					{
					removePart(label);
					appendPart(label);
					}
				break;
			case Mi_TOP_LOCATION	:
				if (!(getLayout() instanceof MiColumnLayout))
					{
					MiColumnLayout layout = new MiColumnLayout();
					layout.setAlleyVSpacing(4);
					layout.setElementHSizing(Mi_NONE);
					setLayout(layout);
					}
				if (getPart(0) != label)
					{
					removePart(label);
					insertPart(label, 0);
					}
				break;
			default:
				throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
					+ ": Unknown location specified: " + location);
			}
		}
	public		MiWidget	getWidget()
		{
		return(widget);
		}
	public		void		insertActionHandler(MiiAction action, int index)
		{
		if (widget != null)
			{
			widget.insertActionHandler(action, index);
			}
		else
			{
			MiDebug.println(this + " - warning - no widget to assign action handler to.");
			super.insertActionHandler(action, index);
			}
		}
	public		void		removeActionHandler(MiiAction action)
		{
		widget.removeActionHandler(action);
		}
	public		void		removeActionHandlers(MiiActionHandler handler)
		{
		widget.removeActionHandlers(handler);
		}
	public		void		removeActionHandler(int index)
		{
		widget.removeActionHandler(index);
		}
	public		void		removeAllActionHandlers()
		{
		widget.removeAllActionHandlers();
		}
	protected	void		setupLabeledWidget(String label, MiWidget widget)
		{
		MiRowLayout layout = new MiRowLayout();
		setLayout(layout);
		layout.setAlleyHSpacing(4);
		layout.setElementVSizing(Mi_NONE);
		if (widget instanceof MiiCommandHandler)
			{
			this.label = MiUtility.assignShortCutFromLabel(
				widget, (MiiCommandHandler )widget, label);
			}
		else
			{
			this.label = new MiText(label);
			}
		this.widget = widget;
		widget.appendActionHandler(this, Mi_SELECTED_ACTION | Mi_REQUEST_ACTION_PHASE);
		widget.appendActionHandler(this, Mi_DESELECTED_ACTION | Mi_REQUEST_ACTION_PHASE);
		this.label.appendEventHandler(new MiDelegateEvents(widget));
		setSelectedBackgroundColor(getNormalBackgroundColor());
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public		void		setAttributes(MiAttributes atts)
		{
		super.setAttributes(atts);
		if (label != null)
			{
			label.setAttributes(atts);
			}
/***
		if (inSetAttributes)
			{
			super.setAttributes(atts);
			return;
			}
		inSetAttributes = true;
		MiAttributes oldAtts = getAttributes();
		super.setAttributes(atts);

		if (oldAtts.getFont() != getFont())
			{
			label.setFont(getFont());
			}
		inSetAttributes = false;
***/
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_SELECTED_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			super.select(true);
			if (!super.isSelected())
				{
				action.veto();
				return(false);
				}
			}
		else if (action.hasActionType(Mi_DESELECTED_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			super.select(false);
			if (super.isSelected())
				{
				action.veto();
				return(false);
				}
			}
		return(true);
		}
	public		void		refreshLookAndFeel()
		{
		super.refreshLookAndFeel();
		setSelectedBackgroundColor(getNormalBackgroundColor());
		}
	}

