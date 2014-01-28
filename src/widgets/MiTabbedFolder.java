
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
 * Note: Make sure that one does not externally set folder contents
 * visible, causing 2 folders to be visible at one time, as this will
 * cause one folder to be very short. Use instead #setOpenFolder
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTabbedFolder extends MiWidget 
	{
	public static final String	Mi_SELECTED_TAB_ATTRIBUTES	= "selectedTab";
	public static final String	Mi_TAB_ATTRIBUTES		= "tab";

	private		int 		curFolderIndex 		= -1;
	private		MiParts		orderedTabList		= new MiParts();
	private		double		leftSlantAngle		= 60.0/180 * Math.PI;
	private	 	double		rightSlantAngle		= 60.0/180 * Math.PI;
	private		boolean		sameWidthForAllTabs 	= false;
	private		MiMargins 	tabMarginsSpec		= new MiMargins(4);
	private		MiPages		pages;
	private		MiPart		borderGr;
	private		MiPart		curFolderLabelGr;
	private		MiMargins	innerMargins		= new MiMargins(4);
	private		MiMargins	tabLabelMargins		= new MiMargins(2);
	private		MiAttributes	selectedTabAttributes;
	private		MiAttributes	tabAttributes;

	public				MiTabbedFolder()
		{
		setInsetMargins(2);
		setBorderLook(Mi_INDENTED_BORDER_LOOK);

		selectedTabAttributes = MiPart.getDefaultAttributes().setFontBold(true);
		selectedTabAttributes = selectedTabAttributes.overrideFrom(
			MiSystem.getPropertyAttributes("MiTabbedFolder." + Mi_SELECTED_TAB_ATTRIBUTES));

		tabAttributes = MiPart.getDefaultAttributes();
		tabAttributes = tabAttributes.setBorderLook(Mi_RAISED_BORDER_LOOK);
		tabAttributes = tabAttributes.setBackgroundColor(getNormalBackgroundColor());
		tabAttributes = tabAttributes.overrideFrom(
			MiSystem.getPropertyAttributes("MiTabs." + Mi_TAB_ATTRIBUTES));

		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);
		radioEnforcer.setTarget(this);
		pages = new MiPages();

		borderGr = new MiPolygon();
//borderGr.setName("borderGr");
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.appendPoint(new MiPoint(0,0));
		borderGr.setBorderLook(Mi_RAISED_BORDER_LOOK);

		curFolderLabelGr = new MiContainer();
		curFolderLabelGr.setName("LabelGraphicsContainer");
		curFolderLabelGr.appendPart(new MiText("<Unknown>"));

		pages.setBackgroundColor(getNormalBackgroundColor());
		borderGr.setBackgroundColor(getNormalBackgroundColor());
		// 9-3-2003 setNormalBackgroundColor(MiColorManager.lightGray);
		MiToolkit.setBackgroundAttributes(this, MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);

		// First 3 parts are for basic graphics...
		appendPart(borderGr);
		appendPart(curFolderLabelGr);
		appendPart(pages);
		
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public		void		setTabMargins(MiMargins d)
		{
		tabMarginsSpec = d;
		}
	public		MiMargins	getTabMargins()
		{
		return(tabMarginsSpec);
		}

	public		MiPart		getBorderGraphics()
		{
		return(borderGr);
		}
					// The margins between the folder and the folder content.
	public		void		setInnerMargins(MiMargins m)
		{
		innerMargins.copy(m);
		invalidateLayout();
		}
	public		MiMargins	getInnerMargins()
		{
		return(innerMargins);
		}
	public		MiPart		appendFolder(String label)
		{
		MiPart tab = new MiText(label);
		tab.setName(label);
		return(appendFolder(tab));
		}
	public		MiPart		appendFolder(MiPart label)
		{
		MiVisibleContainer contents = new MiVisibleContainer();
		appendFolder(label, contents);
		return(contents);
		}
	public		void		appendFolder(String label, MiPart contents)
		{
		MiPart tab = new MiText(label);
		tab.setName(label);
		appendFolder(tab, contents);
		}
	public		void		appendFolder(MiPart label, MiPart contents)
		{
		makeFolderAt(label, contents, -1);
		if (curFolderIndex == -1)
			setOpenFolder(0);
		}
	public		MiPart		insertFolder(String label, int index)
		{
		MiPart tab = new MiText(label);
		tab.setName(label);
		return(insertFolder(tab, index));
		}
	public		MiPart		insertFolder(MiPart label, int index)
		{
		MiVisibleContainer contents = new MiVisibleContainer();
		insertFolder(label, contents, index);
		return(contents);
		}
	public		void		insertFolder(String label, MiPart contents, int index)
		{
		MiPart tab = new MiText(label);
		tab.setName(label);
		insertFolder(tab, contents, index);
		}
	public		void		insertFolder(MiPart label, MiPart contents, int index)
		{
		makeFolderAt(label, contents, index);
		if (curFolderIndex == -1)
			setOpenFolder(0);
		else if (curFolderIndex >= index)
			++curFolderIndex;
		}
	public		int		getNumberOfFolders()
		{
		return(orderedTabList.size());
		}
	public		MiPart		getFolderContents(String name)
		{
		int index = getFolderIndex(name);
		return(pages.getPart(index));
		}
	public		MiPart		getFolderLabel(String name)
		{
		int index = getFolderIndex(name);
		return(orderedTabList.elementAt(index).getPart(1));
		}
	public		MiPart		getFolderTab(String name)
		{
		int index = getFolderIndex(name);
		return(orderedTabList.elementAt(index));
		}
	public		MiPart		getFolderTab(int index)
		{
		return(orderedTabList.elementAt(index));
		}
	public		MiPart		getFolderTabBorder(int index)
		{
		return(orderedTabList.elementAt(index).getPart(0));
		}
	public		void		setFolderLabel(int index, MiPart newLabel)
		{
		MiPart tab = orderedTabList.elementAt(index);

		// Don't want to percolate up invalidateLayout
		boolean wasValidLayout = tab.hasValidLayout() 
			|| curFolderLabelGr.hasValidLayout() || borderGr.hasValidLayout();

		setInvalidLayoutNotificationsEnabled(false);

		newLabel.setSize(newLabel.getPreferredSize(new MiSize()));
		newLabel.validateLayout();

		MiPart label = tab.getPart(1);
		label.replaceSelf(newLabel);

		if (index == curFolderIndex)
			{
			MiPart tabBorder = tab.getPart(0);
			MiPart tabLabel = tab.getPart(1);
			borderGr.setPoint(3, tabBorder.getPoint(0));
			borderGr.setPoint(4, tabBorder.getPoint(1));
			borderGr.setPoint(5, tabBorder.getPoint(2));
			borderGr.setPoint(6, tabBorder.getPoint(3));
			curFolderLabelGr.setPart(tabLabel.deepCopy(), 0);
			curFolderLabelGr.getPart(0).overrideAttributes(selectedTabAttributes);
			for (int i = 0; i < curFolderLabelGr.getPart(0).getNumberOfParts(); ++i)
				{
				curFolderLabelGr.getPart(0).getPart(i).overrideAttributes(selectedTabAttributes);
				}
			curFolderLabelGr.getPart(0).setWidth(curFolderLabelGr.getPart(0).getPreferredSize(new MiSize()).getWidth());
			curFolderLabelGr.getPart(0).setCenter(tabLabel.getCenter());
			}

		if (wasValidLayout)
			{
			doLayout();
			pages.validateLayout();
			tab.validateLayout();
			curFolderLabelGr.validateLayout();
			borderGr.validateLayout();
			}
		setInvalidLayoutNotificationsEnabled(true);
		}
	public		void		setFolderLabel(int index, String name)
		{
		MiPart tab = orderedTabList.elementAt(index);

		// Don't want to percolate up invalidateLayout
		boolean wasValidLayout =  tab.hasValidLayout();
		setInvalidLayoutNotificationsEnabled(false);

		tab.setName("Tab" + index + "." + name);
		MiPart label = tab.getPart(1);
		label.setName(name);
		if (label instanceof MiWidget)
			((MiWidget )label).setValue(name);
		else if (label instanceof MiText)
			((MiText )label).setValue(name);

		if (index == curFolderIndex)
			{
			MiPart tabBorder = tab.getPart(0);
			MiPart tabLabel = tab.getPart(1);
			borderGr.setPoint(3, tabBorder.getPoint(0));
			borderGr.setPoint(4, tabBorder.getPoint(1));
			borderGr.setPoint(5, tabBorder.getPoint(2));
			borderGr.setPoint(6, tabBorder.getPoint(3));
			curFolderLabelGr.setPart(tabLabel.deepCopy(), 0);
			curFolderLabelGr.getPart(0).overrideAttributes(selectedTabAttributes);
			for (int i = 0; i < curFolderLabelGr.getPart(0).getNumberOfParts(); ++i)
				{
				curFolderLabelGr.getPart(0).getPart(i).overrideAttributes(selectedTabAttributes);
				}
			curFolderLabelGr.getPart(0).setWidth(curFolderLabelGr.getPart(0).getPreferredSize(new MiSize()).getWidth());
			curFolderLabelGr.getPart(0).setCenter(tabLabel.getCenter());
			}

		if (wasValidLayout)
			{
			doLayout();
			pages.validateLayout();
			label.validateLayout();
			tab.validateLayout();
			curFolderLabelGr.validateLayout();
			borderGr.validateLayout();
			}
		setInvalidLayoutNotificationsEnabled(true);
		}
	public		void		removeFolder(String name)
		{
		int index = getFolderIndex(name);
		removeFolder(index);
		}
	public		void		removeFolder(int index)
		{
		if (index == curFolderIndex)
			{
			if (orderedTabList.size() > index + 1)
				setOpenFolder(index + 1);
			else if (orderedTabList.size() > 1)
				setOpenFolder(index - 1);
			else
				curFolderIndex = -1;
			}
		pages.removePart(index);
		removePart(orderedTabList.elementAt(index));
		orderedTabList.removeElementAt(index);
		}

	public		MiPart		getFolderContents(int index)
		{
		return(pages.getPart(index));
		}
	public		void		setFolderContents(MiPart contents, int index)
		{
		pages.setPart(contents, index);
		if (curFolderIndex == index)
			{
			contents.setVisible(true);
			}
		}
	public		MiPart		getFolderLabel(int index)
		{
		return(orderedTabList.elementAt(index).getPart(1));
		}
	public		String		getFolderLabelString(int index)
		{
		MiPart label = orderedTabList.elementAt(index).getPart(1);

		if (label instanceof MiWidget)
			{
			return(((MiWidget )label).getValue());
			}
		else if (label instanceof MiText)
			{
			return(((MiText )label).getValue());
			}
		String text = label.getName();
		if ((text != null) && (text.trim().length() > 0))
			{
			return(text);
			}
		return(label.toString());
		}

	public		boolean		hasFolder(String name)
		{
		return(getFolderIndexIfFound(name) != -1);
		}
	public		int		getFolderIndex(String name)
		{
		int index = getFolderIndexIfFound(name);
		if (index == -1)
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this)
				+ ": Folder with name <" + name + "> not found.");
			}
		return(index);
		}
	protected	int		getFolderIndexIfFound(String name)
		{
		for (int i = 0; i < orderedTabList.size(); ++i)
			{
			String n = orderedTabList.elementAt(i).getName();
			n = n.substring(n.indexOf('.') + 1);
			if (n.equals(name))
				return(i);
			}
		return(-1);
		}
	public		String		getOpenFolderName()
		{
		return(getOpenFolder().getName().substring(getOpenFolder().getName().indexOf('.') + 1));
		}
	public		int		getOpenFolderIndex()
		{
		return(curFolderIndex);
		}
	public		MiPart		getOpenFolder()
		{
		return(orderedTabList.elementAt(curFolderIndex));
		}
	public		void		setValue(String value)
		{
		setOpenFolder(value);
		}
	public		String		getValue()
		{
		return(getOpenFolderName());
		}
	protected	void		setOpenFolder(MiPart tab)
		{
		setOpenFolder(orderedTabList.indexOf(tab));
		}
	public		void		setOpenFolder(String name)
		{
		int index = getFolderIndex(name);
		setOpenFolder(index);
		}

	public		void		setOpenFolder(int index)
		{
		if (curFolderIndex != index)
			{
			if (!dispatchActionRequest(Mi_TABBED_FOLDER_CLOSED_ACTION))
				return;
			dispatchAction(Mi_TABBED_FOLDER_CLOSED_ACTION);

			MiSize oldPrefSize = new MiSize();
			getPreferredSize(oldPrefSize);

			MiPart tab;
			// Don't want to percolate up invalidateLayout
			boolean wasValidLayout =  hasValidLayout();
			setInvalidLayoutNotificationsEnabled(false);

			MiPart oldTab = null;
			if (curFolderIndex != -1)
				oldTab = orderedTabList.elementAt(curFolderIndex);
			curFolderIndex = index;
			tab = orderedTabList.elementAt(curFolderIndex);

			MiPart tabBorder = tab.getPart(0);
			MiPart tabLabel = tab.getPart(1);
			borderGr.setPoint(3, tabBorder.getPoint(0));
			borderGr.setPoint(4, tabBorder.getPoint(1));
			borderGr.setPoint(5, tabBorder.getPoint(2));
			borderGr.setPoint(6, tabBorder.getPoint(3));
			curFolderLabelGr.setPart(tabLabel.deepCopy(), 0);

			// Keep tab color to what the tab label is set to as we reuse the 'global' borderGr to form the current label
			java.awt.Color c = tab.getBackgroundColor();
			if (c == null)
				{
				c = MiToolkit.getStandardWidgetAttributes().normalAttributes.getBackgroundColor();
				}
			borderGr.setBackgroundColor(c);

			curFolderLabelGr.getPart(0).overrideAttributes(selectedTabAttributes);
			for (int i = 0; i < curFolderLabelGr.getPart(0).getNumberOfParts(); ++i)
				{
				curFolderLabelGr.getPart(0).getPart(i).overrideAttributes(selectedTabAttributes);
				}

			curFolderLabelGr.getPart(0).setWidth(curFolderLabelGr.getPart(0).getPreferredSize(new MiSize()).getWidth());
			curFolderLabelGr.getPart(0).setCenter(tabLabel.getCenter());

			pages.showPage(curFolderIndex);

			if (wasValidLayout)
				{
				// Put this before doLayout, as sometimes getPreferredSize invalidates layout... 3-12-2004
				MiSize newPrefSize = new MiSize();
				getPreferredSize(newPrefSize);

				doLayout();
				pages.validateLayout();
				tab.validateLayout();
				if (oldTab != null)
					oldTab.validateLayout();
				curFolderLabelGr.validateLayout();
				borderGr.validateLayout();

				// Sometimes this causes change of contents(?) of tabbed folder which causes 
				// change in pref size, which containers need to know about
				if (!newPrefSize.equals(oldPrefSize))
					{
					setInvalidLayoutNotificationsEnabled(true);
					invalidateLayout();
					}
				}

			setInvalidLayoutNotificationsEnabled(true);
			dispatchAction(Mi_TABBED_FOLDER_OPENED_ACTION);
			}
		}

	public		double		getLeftSlantAngle()		{ return(leftSlantAngle);	}
	public		void		setLeftSlantAngle(double d)	{ leftSlantAngle = d;		}

	public 		double		getRightSlantAngle()		{ return(rightSlantAngle);	}
	public 		void		setRightSlantAngle(double d)	{ rightSlantAngle = d;		}

	public 		boolean		getSameWidthForAllTabs()	{ return(sameWidthForAllTabs);	}
	public 		void		setSameWidthForAllTabs(boolean f){ sameWidthForAllTabs = f;	}

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
			curFolderLabelGr.getPart(0).overrideAttributes(selectedTabAttributes);
			}
		else if (Utility.startsWithIgnoreCase(name, Mi_TAB_ATTRIBUTES))
			{
			tabAttributes = tabAttributes.setAttributeValue(
				name.substring(Mi_TAB_ATTRIBUTES.length() + 1), value);
			for (int i = 0; i < orderedTabList.size(); ++i)
				{
				MiPart tabGr = orderedTabList.elementAt(i).getPart(0);
				tabGr.overrideAttributes(tabAttributes);
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
	public		void		refreshLookAndFeel()
		{
		super.refreshLookAndFeel();
		java.awt.Color bg 
			= MiToolkit.getStandardWidgetAttributes().normalAttributes.getBackgroundColor();
		pages.setBackgroundColor(bg);
		borderGr.setBackgroundColor(bg);
		}
	protected	void		doLayout()
		{
		if (pages.getNumberOfParts() == 0)
			{	
			super.doLayout();
			return;
			}
		MiBounds maxLabelSize = getMaxLabelSize(true);

		MiDistance width = maxLabelSize.getWidth() + tabMarginsSpec.getWidth();
		MiDistance height = maxLabelSize.getHeight();
		MiCoord leftSlantWidth = maxLabelSize.getHeight() * Math.cos(leftSlantAngle);
		MiCoord rightSlantWidth = maxLabelSize.getHeight() * Math.cos(rightSlantAngle);


		MiBounds containerBounds = getBounds();
		containerBounds.subtractMargins(getInsetMargins());

		containerBounds.setYmax(containerBounds.getYmax() - height);

		MiCoord lastX = containerBounds.xmin;
		MiSize labelSize = new MiSize();
		for (int i = 0; i < orderedTabList.size(); ++i)
			{
			MiPart tab = orderedTabList.elementAt(i);
			if (!tab.isVisible())
				{
				continue;
				}
			MiPart tabGr = tab.getPart(0);
			MiPart label = tab.getPart(1);
			label.getPreferredSize(labelSize);
			if (!sameWidthForAllTabs)
				width = labelSize.getWidth() + tabMarginsSpec.getWidth();
			tabGr.setPoint(3, new MiPoint(lastX, containerBounds.ymax));
			tabGr.setPoint(2, new MiPoint(lastX + leftSlantWidth, containerBounds.ymax + height));
			tabGr.setPoint(1, new MiPoint(lastX + leftSlantWidth + width, containerBounds.ymax + height));
			tabGr.setPoint(0, new MiPoint(lastX + leftSlantWidth + width + rightSlantWidth, containerBounds.ymax));
			if (i == curFolderIndex)
				{
				borderGr.setPoint(3, tabGr.getPoint(0));
				borderGr.setPoint(4, tabGr.getPoint(1));
				borderGr.setPoint(5, tabGr.getPoint(2));
				borderGr.setPoint(6, tabGr.getPoint(3));
				}

			label.setCenterX(lastX + leftSlantWidth + width/2);
			label.setYmin(containerBounds.ymax + tabLabelMargins.bottom);
			if (i == curFolderIndex)
				curFolderLabelGr.setCenter(label.getCenter());
			lastX = lastX + leftSlantWidth + width;
			}

		// Tab labels made tabbed folder wider than content
		if (lastX + rightSlantWidth > containerBounds.xmax)
			{
			containerBounds.xmax = lastX + rightSlantWidth;
			}

		MiBounds maxContentSize = new MiBounds(containerBounds);
		maxContentSize.subtractMargins(innerMargins);
		pages.setBounds(maxContentSize);
		maxContentSize = pages.getInnerBounds();

		for (int i = 0; i < pages.getNumberOfParts(); ++i)
			{
			pages.getPart(i).setBounds(maxContentSize);
			}

		borderGr.setPoint(0, new MiPoint(containerBounds.xmin, containerBounds.ymin));
		borderGr.setPoint(1, new MiPoint(containerBounds.xmax, containerBounds.ymin));
		borderGr.setPoint(2, new MiPoint(containerBounds.xmax, containerBounds.ymax));
		borderGr.setPoint(7, new MiPoint(containerBounds.xmin, containerBounds.ymax));
		borderGr.setPoint(8, new MiPoint(containerBounds.xmin, containerBounds.ymin));
		}
	public		void		calcMinimumSize(MiSize size)
		{
		calcPreferredSize(size);
		MiDistance width = size.getWidth();
		getMaxFolderSize(size, false);
		MiBounds maxLabelSize = getMaxLabelSize(false);
		size.addMargins(getInsetMargins());
		size.setHeight(size.getHeight() + maxLabelSize.getHeight());

// Why make min width == pref width? 9-16-2003		size.setWidth(width);
		}

	public		void		calcPreferredSize(MiSize size)
		{
		getMaxFolderSize(size, true);
		MiBounds maxLabelSize = getMaxLabelSize(true);
		size.addMargins(getInsetMargins());
		MiDistance tabWidth = 0;
		if (sameWidthForAllTabs)
			{
			tabWidth = (maxLabelSize.getWidth() + tabMarginsSpec.getWidth()) * orderedTabList.size();
			}
		else
			{
			MiSize labelSize = new MiSize();
			for (int i = 0; i < orderedTabList.size(); ++i)
				{
				MiPart tab = orderedTabList.elementAt(i);
				MiPart label = tab.getPart(1);
				label.getPreferredSize(labelSize);
				tabWidth += labelSize.getWidth() + tabMarginsSpec.getWidth();
				}
			}
		MiCoord leftSlantWidth = maxLabelSize.getHeight() * Math.cos(leftSlantAngle);
		MiCoord rightSlantWidth = maxLabelSize.getHeight() * Math.cos(rightSlantAngle);
		MiDistance allTabsWidth = tabWidth + leftSlantWidth * orderedTabList.size() + rightSlantWidth;


		if (allTabsWidth > size.getWidth())
			size.setWidth(allTabsWidth);

		size.setHeight(size.getHeight() + maxLabelSize.getHeight());
		}

	private		void		makeFolderAt(MiPart label, MiPart contents, int index)
		{
		boolean wasValidLayout =  hasValidLayout();
		if (wasValidLayout)
			setInvalidLayoutNotificationsEnabled(false);

		MiContainer tab = new MiContainer();
		tab.appendEventHandler(new MiTabbedFolderEventHandler(this, tab));
		tab.setAcceptingKeyboardFocus(true);
		MiPart tabGr = new MiPolygon();
		tabGr.appendPoint(new MiPoint(0,0));
		tabGr.appendPoint(new MiPoint(0,0));
		tabGr.appendPoint(new MiPoint(0,0));
		tabGr.appendPoint(new MiPoint(0,0));
		tabGr.setAttributes(tabAttributes);

		tab.appendPart(tabGr);
		tab.appendPart(label);
		tab.setName("Tab" + orderedTabList.size() + "." + label.getName());

		if (index == -1)
			{
			insertPart(tab, 0);
			orderedTabList.addElement(tab);
			pages.appendPart(contents);
			}
		else
			{
			insertPart(tab, getNumberOfParts() - index - 3);
			orderedTabList.insertElementAt(tab, index);
			pages.insertPart(contents, index);
			}

		if (wasValidLayout)
			{
			doLayout();
			pages.validateLayout();
			tab.validateLayout();
			curFolderLabelGr.validateLayout();
			borderGr.validateLayout();
			setInvalidLayoutNotificationsEnabled(true);
			}
		}

	private		void		getMaxFolderSize(MiSize size, boolean preferredSize)
		{
		if (preferredSize)
			pages.getPreferredSize(size);
		else
			pages.getMinimumSize(size);
		
		size.addMargins(innerMargins);
		}
	private		MiBounds	getMaxLabelSize(boolean preferredSize)
		{
		MiBounds maxLabelSize = new MiBounds();
		MiSize size = new MiSize();
		for (int i = 0; i < orderedTabList.size(); ++i)
			{
			MiPart tab = orderedTabList.elementAt(i);
			MiPart label = tab.getPart(1).deepCopy();
			label.invalidateLayout();
			label.overrideAttributes(selectedTabAttributes);
			if (label.getNumberOfParts() > 0)
				{
				label.getPart(0).overrideAttributes(selectedTabAttributes);
				for (int j = 0; j < label.getPart(0).getNumberOfParts(); ++j)
					{
					label.getPart(0).getPart(j).overrideAttributes(selectedTabAttributes);
					}
				}
			if (preferredSize)
				label.getPreferredSize(size);
			else
				label.getMinimumSize(size);
			maxLabelSize.accumulateMaxWidthAndHeight(size);
			}
		maxLabelSize.addMargins(tabMarginsSpec);
		maxLabelSize.setXmax(maxLabelSize.getXmax() + tabMarginsSpec.getWidth());
		return(maxLabelSize);
		}
	}

class MiTabbedFolderEventHandler extends MiEventHandler implements MiiActionTypes
	{
	private		MiTabbedFolder	tabbedFolder;
	private		MiPart		tab;

	public				MiTabbedFolderEventHandler(MiTabbedFolder folder, MiPart tab)
		{
		tabbedFolder = folder;
		this.tab = tab;
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_LEFT_MOUSE_DOWN_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_KEY_PRESS_EVENT, ' ', 0);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_KEY_PRESS_EVENT, Mi_ENTER_KEY, 0);
		}

	public		int		processCommand()
		{
		if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			tabbedFolder.setOpenFolder(tab);
			}
		return(Mi_CONSUME_EVENT);
		}
	}

class MiPages extends MiContainer
	{
	private		int		MARGINS		= 0;
	private		int		openPageIndex	= -1;

	public				MiPages()
		{
		setBorderLook(Mi_NONE);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);
		}

	public		int		getShowPageIndex()
		{
		return(openPageIndex);
		}
	public		void		showPage(int index)
		{
//MiDebug.printStackTrace(this + " showpage at: " + index + ", openPageIndex = " + openPageIndex);
		if (openPageIndex != -1)
			{
			getPart(openPageIndex).setVisible(false);
			}
		getPart(index).setVisible(true);
		openPageIndex = index;
		}
	public		void		appendPart(MiPart obj)
		{
		obj.setVisible(false);
		super.appendPart(obj);
		}
	public		void		insertPart(MiPart obj, int index)
		{
//MiDebug.printStackTrace(this + " insertPart at: " + index + ", openPageIndex = " + openPageIndex);
		obj.setVisible(false);
		super.insertPart(obj, index);
		if (index <= openPageIndex)
			{
			++openPageIndex;
			}
		}
	public		void		setPart(MiPart obj, int index)
		{
		obj.setVisible(index == openPageIndex);
		getPart(index).setVisible(false);
		super.setPart(obj, index);
		// super.setPart() calls removePart then insertPart, which modifies openPageIndex, so correct it here.
		if (index <= openPageIndex)
			{		
			--openPageIndex;
			}
		}
	protected	void		calcPreferredSize(MiSize size)
		{
		MiSize tmp = new MiSize();
		size.zeroOut();
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiPart part = getPart(i);
			if ((part.isVisible()) || (part.getNumberOfParts() > 0) || (part.hasOverriddenPreferredSize()))
				{
				getPart(i).getPreferredSize(tmp);
				}
			size.union(tmp);
			}
		}
	protected	void		calcMinimumSize(MiSize size)
		{
		MiSize tmp = new MiSize();
		size.zeroOut();
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiPart part = getPart(i);
			if ((part.isVisible()) || (part.getNumberOfParts() > 0) || (part.hasOverriddenMinimumSize()))
				{
				getPart(i).getMinimumSize(tmp);
				}
			size.union(tmp);
			}
		}
	}

