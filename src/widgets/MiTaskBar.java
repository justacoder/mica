
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
public class MiTaskBar extends MiWidget implements MiiActionHandler
	{
	public static final String	Mi_SELECTED_TAB_ATTRIBUTES	= "selectedTab";
	public static final String	Mi_TAB_ATTRIBUTES		= "tab";

	private		boolean		sameWidthForAllTabs	= true;
	private		boolean		spinArrowsAlwaysVisible;
	private		int		selectedTabIndex	= -1;
	private		int		indexOfVisibleRow	= 0;
	private		int 		numberOfTabsPerRow 	= Integer.MAX_VALUE;
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiSize		tmpSize			= new MiSize();
	private		MiDistance	minimumWidthOfATab	= 50;
	private		MiDistance	maximumWidthOfATab	= 150;
	private		MiDistance	maximumRowLength	= 1200;
	private		MiSpinButtons	spinArrows;
	private		MiRowLayout	tabRow;
	private		MiAttributes	selectedTabAttributes;
	private		MiAttributes	tabAttributes;
	private		MiRadioStateEnforcer	radioEnforcer;
	private	static	MiMargins	tmpMargins		= new MiMargins();


	public				MiTaskBar()
		{
		selectedTabAttributes = MiPart.getDefaultAttributes().setFontBold(true);
		selectedTabAttributes = selectedTabAttributes.overrideFrom(
			MiSystem.getPropertyAttributes("MiTaskBarTab." + Mi_SELECTED_TAB_ATTRIBUTES));

		tabAttributes = MiPart.getDefaultAttributes();
		tabAttributes = tabAttributes.setBorderLook(Mi_RAISED_BORDER_LOOK);
		tabAttributes = tabAttributes.setBackgroundColor(getNormalBackgroundColor());
		tabAttributes = tabAttributes.overrideFrom(
			MiSystem.getPropertyAttributes("MiTaskBarTab." + Mi_TAB_ATTRIBUTES));


//		setBorderLook(Mi_INDENTED_BORDER_LOOK);
		setBorderLook(Mi_GROOVE_BORDER_LOOK);
		// setBackgroundColor(MiColorManager.veryLightGray);

		setInsetMargins(1);
		setMargins(new MiMargins());

		MiRowLayout taskBarRow = new MiRowLayout();
		taskBarRow.setElementHJustification(Mi_LEFT_JUSTIFIED);
		taskBarRow.setLastElementJustification(Mi_RIGHT_JUSTIFIED);
		setLayout(taskBarRow);


		tabRow = new MiTaskBarRowLayout();
		tabRow.setElementHJustification(Mi_LEFT_JUSTIFIED);
		tabRow.setAlleyHSpacing(3);
		appendPart(tabRow);

		spinArrows = new MiSpinButtons(Mi_VERTICAL, Mi_VERTICAL);
		spinArrows.appendActionHandler(this, Mi_DECREASE_ACTION);
		spinArrows.appendActionHandler(this, Mi_INCREASE_ACTION);
		spinArrows.setHidden(!spinArrowsAlwaysVisible);
		appendPart(spinArrows);

		radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);
		}

	public		MiTaskBarTab	appendTab(String label)
		{
		MiTaskBarTab tab = new MiTaskBarTab(label);
		tab.setName(label);
		tab.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		tab.setRadioStateEnforcer(radioEnforcer);
		tabRow.appendPart(tab);
		return(tab);
		}

	public		MiTaskBarTab	appendTab(MiPart icon, String label)
		{
		MiTaskBarTab tab = new MiTaskBarTab(icon, label);
		tab.setName(label);
		tab.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		tab.setRadioStateEnforcer(radioEnforcer);
		tabRow.appendPart(tab);
		return(tab);
		}

	public		MiTaskBarTab	insertTab(String label, int index)
		{
		MiTaskBarTab tab = new MiTaskBarTab(label);
		tab.setName(label);
		tab.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		tab.setRadioStateEnforcer(radioEnforcer);
		tabRow.insertPart(tab, index);
		return(tab);
		}
	public		MiTaskBarTab	insertTab(MiPart icon, String label, int index)
		{
		MiTaskBarTab tab = new MiTaskBarTab(icon, label);
		tab.setName(label);
		tab.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		tab.setRadioStateEnforcer(radioEnforcer);
		tabRow.insertPart(tab, index);
		return(tab);
		}
	public		void		removeTab(int index)
		{
		removeTab(tabRow.getPart(index));
		}
	public		void		removeTab(MiPart tab)
		{
		tab.removeActionHandlers(this);

		int index = tabRow.getIndexOfPart(tab);

		tabRow.removePart(tab);

		if (selectedTabIndex == index)
			{
			selectedTabIndex = -1;
			if (getNumberOfTabs() != 0)
				setSelectedTab(0);
			}
		else if (selectedTabIndex > index)
			{
			--selectedTabIndex;
			}
		}
					/**------------------------------------------------------
	 				 * Process the given action.
					 * @param action	the action to process.
					 * @return 		true if can propogate the action to
					 *			other handlers.
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ACTIVATED_ACTION))
			{
			int index = tabRow.getIndexOfPart(action.getActionSource());
			setSelectedTab(index);
			}
		else if (action.hasActionType(Mi_DECREASE_ACTION))
			{
			if (indexOfVisibleRow < tabRow.getNumberOfParts()/numberOfTabsPerRow)
				{
				++indexOfVisibleRow;
				tabRow.invalidateLayout();
				invalidateLayout();
				}
			}
		else if (action.hasActionType(Mi_INCREASE_ACTION))
			{
			if (indexOfVisibleRow > 0)
				{
				--indexOfVisibleRow;
				tabRow.invalidateLayout();
				invalidateLayout();
				}
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
	public		MiTaskBarTab	getSelectedTab()
		{
		return((MiTaskBarTab )getTab(selectedTabIndex));
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
		int index = tabRow.getIndexOfPart(obj);
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
			selectedTabIndex = index;
			MiTaskBarTab tab = getTab(index);

			tab.select(true);

			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		}

	public		MiTaskBarTab	getTab(int index)
		{
		return((MiTaskBarTab )tabRow.getPart(index));
		}

	public		int		getNumberOfTabs()
		{
		return(tabRow.getNumberOfParts());
		}
	public		void		removeAllTabs()
		{
		while (tabRow.getNumberOfParts() > 0)
			{
			MiPart tab = tabRow.getPart(tabRow.getNumberOfParts() - 1);
			tab.removeActionHandlers(this);
			tabRow.removePart(tab);
			}
		selectedTabIndex = -1;
		}
	public		void		setMinimumWidthOfATab(MiDistance width)
		{
		minimumWidthOfATab = width;
		}
	public		MiDistance	getMinimumWidthOfATab()
		{
		return(minimumWidthOfATab);
		}
	public		void		setMaximumWidthOfATab(MiDistance width)
		{
		maximumWidthOfATab = width;
		}
	public		MiDistance	getMaximumWidthOfATab()
		{
		return(maximumWidthOfATab);
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
			for (int i = 0; i < tabRow.getNumberOfParts(); ++i)
				{
				MiPart tab = tabRow.getPart(i);
				if (tab != selectedTab)
					{
					tab.overrideAttributes(tabAttributes);
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
	protected 	void		calcPreferredSize(MiSize size)
		{
		MiSize standardSize = calcPreferredTabSize();
		MiCoord x = 0;
		MiSize maxSize = new MiSize();
		size.zeroOut();

		MiDistance maxRowLength = maximumRowLength;
		MiDistance currentWidth = getBounds().getWidth();
		if ((currentWidth > 50) && (currentWidth < maximumRowLength))
			{
			maxRowLength = currentWidth;
			}

		for (int j = 0; j < tabRow.getNumberOfParts(); ++j)
			{
			if (sameWidthForAllTabs)
				{
				tmpSize = standardSize;
				}
			else			
				{
				tabRow.getPart(j).getPreferredSize(tmpSize);
				}
			x += tmpSize.getWidth() + getAlleyHSpacing();
			maxSize.accumulateMaxWidthAndHeight(tmpSize);
			if (x > maxRowLength)
				{
				break;
				}
			}
		size.setHeight(maxSize.height + getMargins(tmpMargins).getHeight());
		size.setWidth(Math.max(size.width, x));
		size.addMargins(getInsetMargins(tmpMargins));

		size.setWidth(50);
		}
	protected 	void		calcMinimumSize(MiSize size)
		{
		MiSize standardSize = calcPreferredTabSize();
/**
		MiCoord x = 0;
		MiCoord y = 0;
		MiSize maxSize = new MiSize();
		size.zeroOut();

		MiDistance maxRowLength = maximumRowLength;
		MiDistance currentWidth = getBounds().getWidth();
		if ((currentWidth > 50) && (currentWidth < maximumRowLength))
			{
			maxRowLength = currentWidth;
			}

		for (int j = 0; j < tabRow.getNumberOfParts(); ++j)
			{
			if (sameWidthForAllTabs)
				{
				tmpSize = standardSize;
				}
			else			
				{
				tabRow.getPart(j).getMinimumSize(tmpSize);
				}
			tmpSize.setWidth(minimumWidthOfATab);
			x += tmpSize.getWidth();
			maxSize.accumulateMaxWidthAndHeight(tmpSize);
			if (x > maxRowLength)
				{
				size.setHeight(size.height + maxSize.height);
				break;
				}
			}
		if (spinArrows == null)
			{
			size.setWidth(Math.max(size.width, maxSize.width));
			}
		else
			{
			size.setWidth(Math.max(size.width, 
				maxSize.width + spinArrows.getPreferredSize(tmpSize).width));
			}
		size.addMargins(getInsetMargins(tmpMargins));
		size.addMargins(getMargins(tmpMargins));

**/
		size.setWidth(50);
		size.setHeight(standardSize.getHeight());
		}
	protected	MiSize		calcPreferredTabSize()
		{
		MiSize maxSize = new MiSize();
		for (int j = 0; j < tabRow.getNumberOfParts(); ++j)
			{
			MiTaskBarTab tab = (MiTaskBarTab )tabRow.getPart(j);

			String origText = tab.getValue();
			String text = tab.getDesiredValue();

			boolean flag = tab.getValidatingLayout();
			tab.setValidatingLayout(false);
			tab.setOutgoingInvalidLayoutNotificationsEnabled(false);
			tab.setValue(text);
			tab.setPreferredSize(null);
			tab.getPreferredSize(tmpSize);
			tab.setValue(origText);
			tab.validateLayout();
			tab.setValidatingLayout(flag);
			tab.setOutgoingInvalidLayoutNotificationsEnabled(true);

			if (sameWidthForAllTabs)
				{
				maxSize.accumulateMaxWidthAndHeight(tmpSize);
				}
			else
				{
				maxSize.accumulateMaxWidthAndHeight(tmpSize);
				if (maxSize.height < tmpSize.height)
					{
					maxSize.height = tmpSize.height;
					}
				maxSize.width += tmpSize.width;
				}
			}

		if (maxSize.getWidth() > maximumWidthOfATab)
			{
			maxSize.setWidth(maximumWidthOfATab);
			}

		return(maxSize);
		}
	protected	void		doLayout()
		{
		MiDistance minWidthOfTab = minimumWidthOfATab;
		MiSize standardTabSize = calcPreferredTabSize();
		MiBounds innerBounds = getInnerBounds(tmpBounds);
		MiDistance innerBoundsWidth = innerBounds.getWidth();

		boolean needSpinArrows = false;


		// Account for when we have tabs whose preferred size is actually smaller than minimum size
		if (minWidthOfTab > standardTabSize.getWidth())
			{
			minWidthOfTab = standardTabSize.getWidth();
			}

		numberOfTabsPerRow = tabRow.getNumberOfParts();
		if ((sameWidthForAllTabs)
			&& ((tabRow.getNumberOfParts()  + 1) * (standardTabSize.width + tabRow.getAlleyHSpacing()) 
				> innerBoundsWidth - tabRow.getAlleyHSpacing()))
			{
			standardTabSize.setWidth(
				(innerBoundsWidth - (tabRow.getNumberOfParts() + 1) * tabRow.getAlleyHSpacing())
					/tabRow.getNumberOfParts());
			if (standardTabSize.getWidth() < minWidthOfTab)
				{
				needSpinArrows = true;
				spinArrows.setHidden(false);
				spinArrows.setSensitive(true);
				spinArrows.setSize(spinArrows.getPreferredSize(tmpSize));
				innerBoundsWidth -= spinArrows.getPreferredSize(tmpSize).getWidth();

				numberOfTabsPerRow = (int )((innerBoundsWidth - tabRow.getAlleyHSpacing())/
					(minWidthOfTab + tabRow.getAlleyHSpacing()));

				standardTabSize.setWidth(
					(innerBoundsWidth - (numberOfTabsPerRow + 1) * tabRow.getAlleyHSpacing())
						/numberOfTabsPerRow);
					
				}
			}
		else if ((!sameWidthForAllTabs) && (getPreferredSize(new MiSize()).getWidth() > innerBoundsWidth))
			{
			needSpinArrows = true;
			spinArrows.setHidden(false);
			spinArrows.setSensitive(true);
			spinArrows.setSize(spinArrows.getPreferredSize(tmpSize));
			innerBoundsWidth -= spinArrows.getPreferredSize(tmpSize).getWidth();
			numberOfTabsPerRow = 4;
			/* calculate for wach row the pref size of the tabs and how many fit
			numberOfTabsPerRow.append((innerBoundsWidth - tabRow.getAlleyHSpacing())/
					(minWidthOfTab + tabRow.getAlleyHSpacing());
			*/
			}

		if (!needSpinArrows)
			{
			indexOfVisibleRow = 0;
			}

//MiDebug.println("innerBounds.getWidth() = " + innerBounds.getWidth());
//MiDebug.println("standardTabSize = " + standardTabSize.getWidth());
//MiDebug.println("tabRow.getNumberOfParts() = " + tabRow.getNumberOfParts());

		int firstTabVisible = numberOfTabsPerRow * indexOfVisibleRow;
		for (int i = 0; i < tabRow.getNumberOfParts(); ++i)
			{
			MiPart tab = tabRow.getPart(i);
			if ((i >= firstTabVisible) && (i < firstTabVisible + numberOfTabsPerRow))
				{
				tab.setVisible(true);
				adjustDisplayTextForTab((MiTaskBarTab )tab, standardTabSize.width);
				}
			else
				{
				tab.setVisible(false);
				}
			}
	
		if (!needSpinArrows)
			{
			spinArrows.setSensitive(false);
			spinArrows.setHidden(!spinArrowsAlwaysVisible);
			}
//		tabRow.invalidateLayout();
		super.doLayout();
//		tabRow.validateLayout();
		}
	protected	void		adjustDisplayTextForTab(MiTaskBarTab tab, MiDistance desiredWidth)
		{
		String text = tab.getDesiredValue();
		tab.setValidatingLayout(false);
		tab.setPreferredSize(null);
		//tab.setFixedWidth(false);
		tab.setValue(text);
		tab.getPreferredSize(tmpSize);
//MiDebug.println("\n\ntab fill sized = " + tmpSize);
//MiDebug.println("Desired Width is : " +desiredWidth);
		if (tmpSize.width > desiredWidth)
			{
			while ((text.length() > 1) && (tab.getPreferredSize(tmpSize).getWidth() > desiredWidth))
				{
				text = text.substring(0, text.length() - 1);
				tab.setValue(text + "...");
//MiDebug.println("NOW tab text = " + text);
//MiDebug.println("NOW pref szie of tab = " + tab.getPreferredSize(tmpSize).getWidth());
				}
//MiDebug.println("NOW preferred size = " + tab.getPreferredSize(tmpSize).getWidth());
			}
		if ((sameWidthForAllTabs)
			|| (tab.getPreferredSize(tmpSize).getWidth() > desiredWidth))
			{
			tmpSize.setWidth(desiredWidth);
			tab.setPreferredSize(tmpSize);
			}
		tab.setValidatingLayout(true);
		}
	}
class MiTaskBarRowLayout extends MiRowLayout
	{
					/**------------------------------------------------------
		 			 * Gets whether this layout knows how to calculate the 
					 * preferred and minimum sizes of the target. If not,
					 * then the getPreferredSize and getMinimumSize methods
					 * do not do anything. Most layouts return true.
					 * @return		true if this layout knows how to
					 *			calculate the referred and minimum 
					 *			sizes of the target
					 * @implements 		MiiLayout#determinesPreferredAndMinimumSizes
					 *------------------------------------------------------*/
	public		boolean		determinesPreferredAndMinimumSizes()
		{
		return(false);
		}
	public		void		calcMinimumSize(MiSize size)
		{
		super.calcMinimumSize(size);
		size.setSize(50, 30);
		}

	public		void		calcPreferredSize(MiSize size)
		{
		super.calcPreferredSize(size);
		size.setSize(50, 30);
		}
/*
	public		void		invalidateLayout()
		{
		super.invalidateLayout();
MiDebug.printStackTrace(this + "");
		}
	public		void		validateLayout()
		{
		super.validateLayout();
MiDebug.printStackTrace(this + "VALIDATE");
		}
*/


	}

