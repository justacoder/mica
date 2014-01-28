
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
public class MiTabs extends MiWidget implements MiiActionHandler
	{
	public static final String	Mi_SELECTED_TAB_ATTRIBUTES	= "selectedTab";
	public static final String	Mi_TAB_ATTRIBUTES		= "tab";

	private		boolean		sameWidthForAllTabs;
	private		boolean		spinArrowsAlwaysVisible;
	private		int		orientation;
	private		int		location;
	private		int		numberOfRows		= 1;
	private		MiDistance	tabOverlap		= 12;
	private		int		selectedTabIndex	= -1;
	private		int		startTabIndex		= 0;
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiBounds	tmpBounds2		= new MiBounds();
	private		MiSize		tmpSize			= new MiSize();
	private		MiSpinButtons	spinArrows;
	private		MiParts[]	tabs			= new MiParts[1];
	private		MiAttributes	selectedTabAttributes;
	private		MiAttributes	tabAttributes;
	private		MiLine		leftUnderLine;
	private		MiLine		rightUnderLine;
	private		boolean		selectedTabMovedToFront	= true;
	private	static	MiMargins	tmpMargins		= new MiMargins();


	public				MiTabs()
		{
		this(Mi_TOP);
		}
	public				MiTabs(int location)
		{
		this.location = location;
		if ((location == Mi_TOP) || (location == Mi_BOTTOM))
			orientation = Mi_HORIZONTAL;
		else
			orientation = Mi_VERTICAL;

		switch (location)
			{
			case Mi_TOP :
				setInsetMargins(new MiMargins(4, 0, 4, 4));
				break;
			case Mi_BOTTOM :
				setInsetMargins(new MiMargins(4, 4, 4, 0));
				break;
			case Mi_TO_LEFT :
				setInsetMargins(new MiMargins(4, 4, 0, 4));
				break;
			case Mi_TO_RIGHT :
				setInsetMargins(new MiMargins(0, 4, 4, 4));
				break;
			}

		selectedTabAttributes = MiPart.getDefaultAttributes().setFontBold(true);
		selectedTabAttributes = selectedTabAttributes.overrideFrom(
			MiSystem.getPropertyAttributes("MiTabs." + Mi_SELECTED_TAB_ATTRIBUTES));

		tabAttributes = MiPart.getDefaultAttributes();
		tabAttributes = tabAttributes.setBorderLook(Mi_RAISED_BORDER_LOOK);
		tabAttributes = tabAttributes.setBackgroundColor(getNormalBackgroundColor());
		tabAttributes = tabAttributes.overrideFrom(
			MiSystem.getPropertyAttributes("MiTabs." + Mi_TAB_ATTRIBUTES));

		tabs[0] = new MiParts();
		leftUnderLine = new MiLine();
		rightUnderLine = new MiLine();
		appendPart(leftUnderLine);
		appendPart(rightUnderLine);
		leftUnderLine.setBorderLook(Mi_RAISED_BORDER_LOOK);
		rightUnderLine.setBorderLook(Mi_RAISED_BORDER_LOOK);

		setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(this, 
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		MiPolygon polygon  = new MiPolygon();
		polygon.setClosed(false);
		polygon.appendPoint(new MiPoint(0, 0));
		polygon.appendPoint(new MiPoint(0, -1));
		polygon.appendPoint(new MiPoint(1, -1));
		polygon.appendPoint(new MiPoint(1, 0));
		setShape(polygon);
		}

	public		MiTab		appendTab(String label)
		{
		MiText labelPart = new MiText(label);
		labelPart.setName(label);
		return(appendTab(labelPart));
		}

	public		MiTab		appendTab(MiPart label)
		{
		return(appendTab(label, 0));
		}

	public		MiTab		appendTab(MiPart label, int rowNumber)
		{
		MiTab tab = new MiTab(location);
		tab.setLabel(label);
		tab.setAttributes(tabAttributes);
		return(appendTab(tab, rowNumber));
		}
	public		MiTab		appendTab(MiTab tab)
		{
		return(appendTab(tab, 0));
		}
	public		MiTab		appendTab(MiTab tab, int rowNumber)
		{
		tab.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		tabs[rowNumber].addElement(tab);

		int insertionPoint = getNumberOfParts() - 2;
		if (spinArrows != null)
			--insertionPoint;

		if (selectedTabIndex != -1)
			--insertionPoint;

		insertPart(tab, insertionPoint);

		if (selectedTabIndex == -1)
			setSelectedTab(0);

		return(tab);
		}
	public		MiTab		insertTab(String label, int index)
		{
		MiText labelPart = new MiText(label);
		labelPart.setName(label);
		return(insertTab(labelPart, index));
		}
	public		MiTab		insertTab(MiPart label, int index)
		{
		return(insertTab(label, 0, index));
		}

	public		MiTab		insertTab(MiPart label, int rowNumber, int index)
		{
		MiTab tab = new MiTab(location);
		tab.setLabel(label);
		tab.setAttributes(tabAttributes);
		return(insertTab(tab, rowNumber, index));
		}
	public		MiTab		insertTab(MiTab tab, int rowNumber, int index)
		{
		tab.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		tabs[rowNumber].insertElementAt(tab, index);

		insertPart(tab, index);

		if (selectedTabIndex >= index)
			++selectedTabIndex;
		else if (selectedTabIndex == -1)
			setSelectedTab(0);

		return(tab);
		}
	public		void		removeTab(MiPart tab)
		{
		int index = getIndexOfTab(tab);

		tab.removeActionHandlers(this);
		removePart(tab);
		for (int i = 0; i < numberOfRows; ++i)
			{
			if (tabs[i].contains(tab))
				{
				tabs[i].removeElement(tab);
				break;
				}
			}
		if (selectedTabIndex == index)
			{
			selectedTabIndex = -1;
			if (getNumberOfTabs() != 0)
				setSelectedTab(0);
			}
		else if (selectedTabIndex > index)
			--selectedTabIndex;
		}
	public		void		setSelectedTabMovedToFront(boolean flag)
		{
		selectedTabMovedToFront = flag;
		((MiPolygon )getShape()).setClosed(!flag);
		}
	public		boolean		isSelectedTabMovedToFront()
		{
		return(selectedTabMovedToFront);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ACTIVATED_ACTION))
			{
			int index = getIndexOfTab(action.getActionSource());
			setSelectedTab(index);
			}
		return(true);
		}
	public		String		getSelectedTabName()
		{
		return(getSelectedTab().getName());
		}
	public		int		getSelectedTabIndex()
		{
		return(selectedTabIndex);
		}
	public		MiTab		getSelectedTab()
		{
		return((MiTab )getTab(selectedTabIndex));
		}
	public		void		setValue(String value)
		{
		setSelectedTab(value);
		}
	public		String		getValue()
		{
		return(getSelectedTabName());
		}
	public		void		setSelectedTab(String name)
		{
		MiPart obj = getPart(name);
		if (obj == null)
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this)
				+ ": Tab with name <" + name + "> not found.");
			}
		int index = getIndexOfTab(obj);
		if (index != -1)
			{
			setSelectedTab(index);
			}
		else
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this)
				+ ": Internal error: Tab with name <" + name + "> not found.");
			}
		}

	public		void		setSelectedTab(int index)
		{
		if (selectedTabIndex != index)
			{
			boolean wasValidLayout =  hasValidLayout();
			setInvalidLayoutNotificationsEnabled(false);

			if (selectedTabIndex != -1)
				{
				MiTab tab = getTab(selectedTabIndex);
				tab.overrideAttributes(tabAttributes);
				}

			selectedTabIndex = index;
			MiTab tab = getTab(index);

			if (selectedTabMovedToFront)
				{
				removePart(tab);
				if (spinArrows != null)
					insertPart(tab, getNumberOfParts() - 3);
				else
					insertPart(tab, getNumberOfParts() - 2);
				}

			tab.overrideAttributes(selectedTabAttributes);
			if (wasValidLayout)
				{
				// Keep layout valid...
				doLayout();
				validateParts();
				}
			setInvalidLayoutNotificationsEnabled(true);
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		}
	public		int		getIndexOfTab(MiPart tab)
		{
		int numTabsInPreviousRows = 0;
		for (int i = 0; i < numberOfRows; ++i)
			{
			int index = tabs[i].indexOf(tab);
			if (index != -1)
				{
				return(numTabsInPreviousRows + index);
				}
			numTabsInPreviousRows += tabs[i].size();
			}
		return(-1);
		}

	public		MiTab		getTab(int index)
		{
		for (int i = 0; i < numberOfRows; ++i)
			{
			if (index < tabs[i].size())
				return((MiTab )tabs[i].elementAt(index));
			index -= tabs[i].size();
			}
		return(null);
		}

	public		int		getNumberOfTabs()
		{
		int num = 0;
		for (int i = 0; i < numberOfRows; ++i)
			{
			num += tabs[i].size();
			}
		return(num);
		}
	public		void		removeAllTabs()
		{
		for (int i = getNumberOfTabs() - 1; i >= 0; --i)
			{
			MiTab tab = getTab(i);
			removePart(tab);
			}
		for (int i = 0; i < numberOfRows; ++i)
			{
			tabs[i].removeAllElements();
			}
		selectedTabIndex = -1;
		}

	protected 	void		calcPreferredSize(MiSize size)
		{
		layoutTabSizes();
		MiCoord x = 0;
		MiCoord y = 0;
		MiSize maxSize = new MiSize();
		size.zeroOut();
		if (orientation == Mi_HORIZONTAL)
			{
			for (int i = 0; i < numberOfRows; ++i)
				{
				MiParts tabRow = tabs[i];
				for (int j = 0; j < tabRow.size(); ++j)
					{
					x += tabRow.elementAt(j).getPreferredSize(tmpSize).getWidth()
						- tabOverlap;
					maxSize.accumulateMaxWidthAndHeight(tmpSize);
					}
				size.setHeight(size.height + maxSize.height);
				size.setWidth(Math.max(size.width, x + tabOverlap));
				size.addMargins(getInsetMargins(tmpMargins));
				size.addMargins(getMargins(tmpMargins));
				}
			}
		else
			{
			throw new RuntimeException("Vertical Tabs not yet fully supported");
			}
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * This method supports the use of property names of form:
					 *   selected.backgroundColor
					 * in order to specify the values of attributes for this
					 * widget on a state by state basis.
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (Utility.startsWithIgnoreCase(name, Mi_SELECTED_TAB_ATTRIBUTES))
			{
			selectedTabAttributes = selectedTabAttributes.setAttributeValue(
				name.substring(Mi_SELECTED_TAB_ATTRIBUTES.length() + 1), value);
			getTab(selectedTabIndex).overrideAttributes(selectedTabAttributes);
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_TAB_ATTRIBUTES))
			{
			tabAttributes = tabAttributes.setAttributeValue(
				name.substring(Mi_TAB_ATTRIBUTES.length() + 1), value);
			MiPart selectedTab = getTab(selectedTabIndex);
			for (int i = 0; i < tabs.length; ++i)
				{
				MiParts row = tabs[i];
				for (int j = 0; j < row.size(); ++j)
					{
					if (row.elementAt(j) != selectedTab)
						row.elementAt(j).overrideAttributes(tabAttributes);
					}
				}
			}
		else
			{
			super.setPropertyValue(name, value);
			}
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		if (Utility.startsWithIgnoreCase(name, Mi_SELECTED_TAB_ATTRIBUTES))
			{
			return(selectedTabAttributes.getAttributeValue(
				name.substring(Mi_SELECTED_TAB_ATTRIBUTES.length() + 1)));
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_TAB_ATTRIBUTES))
			{
			return(tabAttributes.getAttributeValue(
				name.substring(Mi_TAB_ATTRIBUTES.length() + 1)));
			}
		else
			{
			return(super.getPropertyValue(name));
			}
		}
	protected 	void		calcMinimumSize(MiSize size)
		{
		layoutTabSizes();
		MiCoord x = 0;
		MiCoord y = 0;
		MiSize maxSize = new MiSize();
		size.zeroOut();
		if (orientation == Mi_HORIZONTAL)
			{
			for (int i = 0; i < numberOfRows; ++i)
				{
				MiParts tabRow = tabs[i];
				for (int j = 0; j < tabRow.size(); ++j)
					{
					x += tabRow.elementAt(j).getPreferredSize(tmpSize).getWidth()
						 - tabOverlap;
					maxSize.accumulateMaxWidthAndHeight(tmpSize);
					}
				size.setHeight(size.height + maxSize.height);
				if (spinArrows == null)
					{
					size.setWidth(Math.max(size.width, maxSize.width));
					}
				else
					{
					size.setWidth(Math.max(size.width, 
						maxSize.width + spinArrows.getPreferredSize(tmpSize).width));
					}
				}
			size.addMargins(getInsetMargins(tmpMargins));
			size.addMargins(getMargins(tmpMargins));
			}
		else
			{
			throw new RuntimeException("Vertical Tabs not yet fully supported");
			}
		}
	protected	void		layoutTabSizes()
		{
		MiSize maxSize = new MiSize();
		if (sameWidthForAllTabs)
			{
			maxSize.zeroOut();
			for (int i = 0; i < numberOfRows; ++i)
				{
				MiParts tabRow = tabs[i];
				for (int j = 0; j < tabRow.size(); ++j)
					{
					maxSize.accumulateMaxWidthAndHeight(
						tabRow.elementAt(j).getPreferredSize(tmpSize));
					}
				}
			for (int i = 0; i < numberOfRows; ++i)
				{
				MiParts tabRow = tabs[i];
				for (int j = 0; j < tabRow.size(); ++j)
					{
					tabRow.elementAt(j).setSize(maxSize);
					}
				}
			}
		}
	protected	void		doLayout()
		{
		layoutTabSizes();
		MiBounds innerBounds = getInnerBounds(tmpBounds);
		MiBounds outerBounds = getBounds(tmpBounds2);
		if (orientation == Mi_HORIZONTAL)
			{
			MiCoord startX = innerBounds.getXmin();
			MiCoord xmax = innerBounds.getXmax();
			MiCoord y;
			if (location == Mi_TOP)
				y = outerBounds.getYmin();
			else
				y = outerBounds.getYmax();

			if (getBounds().getWidth() < getPreferredSize(tmpSize).width)
				{
				if (spinArrows == null)
					{
					spinArrows = new MiSpinButtons();
					appendPart(spinArrows);
					}
				spinArrows.setVisible(true);
				spinArrows.setSensitive(true);
				MiDistance width = spinArrows.getPreferredSize(tmpSize).width;
				for (int i = 0; i < startTabIndex; ++i)
					{
					startX -= tabs[0].elementAt(i).getPreferredSize(tmpSize).width;
					if (width > innerBounds.getWidth())
						{
						startX += tmpSize.width;
						break;
						}
					}
				}
			else if (spinArrows != null)
				{
				spinArrows.setSensitive(false);
				if (!spinArrowsAlwaysVisible)
					spinArrows.setVisible(false);
				}
			int tabIndex = 0;
			for (int i = 0; i < numberOfRows; ++i)
				{
				MiParts tabRow = tabs[i];
				MiCoord x = startX;
				for (int j = 0; j < tabRow.size(); ++j)
					{
					MiPart tab = tabRow.elementAt(j);
					tab.getPreferredSize(tmpSize);
					tmpBounds.setSize(tmpSize);
					tmpBounds.translateXminTo(x);
					if (location == Mi_TOP)
						tmpBounds.translateYminTo(y);
					else
						tmpBounds.translateYmaxTo(y);
	
					tab.setBounds(tmpBounds);
					tab.validateLayout();

					if (tabIndex == selectedTabIndex)
						{
						leftUnderLine.setPoint(0, startX, y);
						leftUnderLine.setPoint(1, x, y);
						if (selectedTabMovedToFront)
							rightUnderLine.setPoint(0, x + tab.getWidth(), y);
						else
							rightUnderLine.setPoint(0, x, y);

						rightUnderLine.setPoint(1, xmax, y);
						}

					++tabIndex;
					x += tab.getWidth() - tabOverlap;
					}
				if ((spinArrows != null) && (spinArrows.isVisible()))
					{
					spinArrows.setSize(spinArrows.getPreferredSize(tmpSize));
					if (location == Mi_TOP)
						spinArrows.setYmin(y);
					else
						spinArrows.setYmax(y);
					spinArrows.setXmax(xmax);
					}
				}
			}
		}
	}

