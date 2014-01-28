
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
public class MiStandardMenu extends MiWidget implements MiiBrowsableGrid, MiiCommandHandler, MiiActionHandler
	{
	private static final String	CASCADE_MENU_POPPED_UP_COMMAND_NAME	= "cascadeMenuPoppedUp";
	private static final String	CASCADE_MENU_POPPED_DOWN_COMMAND_NAME	= "cascadeMenuPoppedDown";

	private		MiPart			armedItem;
	private		MiBrowsableGridEventHandler	browseEventHandler	= new MiBrowsableGridEventHandler();
	private		int			armedIndex			= -1;
	private		int			cascadeMenuIndex		= -1;
	private		MiShortCutHandler	acceleratorEventHandler 	= new MiShortCutHandler();
	private		MiShortCutHandler 	mnemonicEventHandler	 	= new MiShortCutHandler();
	private		MiAttributes 		armedItemAttributes;
	private		MiAttributes 		armedItemOrigAttributes;
	private		MiAttributes 		armedItemOrigNormalAttributes;
	private		MiAttributes 		cascadeItemAttributes;
	private		MiAttributes 		cascadeItemOrigAttributes;
	private		MiAttributes 		cascadeItemOrigNormalAttributes;
	private		boolean			eachRowHasSameHeight		= true;
	private		MiRadioStateEnforcer 	cascadeRadioStateEnforcer;



	public				MiStandardMenu()
		{
		setAcceptingMouseFocus(true);
		setAlleyHSpacing(10);
		setAlleyVSpacing(4);
		setInsetMargins(0);

		armedItemAttributes = MiToolkit.getAttributes(MiiToolkit.Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES);
		cascadeItemAttributes =  MiToolkit.getAttributes(MiiToolkit.Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES);

		setBackgroundColor(Mi_TRANSPARENT_COLOR);
		appendEventHandler(browseEventHandler);
		appendEventHandler(mnemonicEventHandler);

		// Don't repaint menu when enter with mouse
		setMouseFocusAttributes(getMouseFocusAttributes().setHasBorderHilite(false));
		rebuildMnemonicEventHandlers();
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public		MiAttributes		getArmedItemAttributes()
		{
		return(armedItemAttributes);
		}
	public		void			setArmedItemAttributes(MiAttributes atts)
		{
		armedItemAttributes = atts;
		}

	public		MiShortCutHandler	getAccelerators()
		{
		return(acceleratorEventHandler);
		}
	public		void		setEachRowHasSameHeight(boolean flag)
		{
		eachRowHasSameHeight = flag;
		}
	public		boolean		getEachRowHasSameHeight()
		{
		return(eachRowHasSameHeight);
		}
/*
	public		void		appendItem(MiPart obj, MiCommandHandler method, String arguments)
		{
		appendItem(new MiMenuItem(obj, method, arguments));
		}
*/
	public		void		appendItem(MiMenuItem item)
		{
		registerItem(item);
		appendPart(item);
		if (item.isCascadeMenu())
			{
			if (cascadeRadioStateEnforcer == null)
				cascadeRadioStateEnforcer = new MiRadioStateEnforcer();
			item.getCascadeMenuLauncherButton().setRadioStateEnforcer(cascadeRadioStateEnforcer);
			item.getCascadeMenuLauncherButton().getMenu().appendCommandHandler(
				this, CASCADE_MENU_POPPED_UP_COMMAND_NAME, Mi_PART_SHOWING_ACTION); 
			item.getCascadeMenuLauncherButton().getMenu().appendCommandHandler(
				this, CASCADE_MENU_POPPED_DOWN_COMMAND_NAME, Mi_PART_NOT_SHOWING_ACTION); 
			item.getCascadeMenuLauncherButton().getMenu().getMenu().appendActionHandler(
				this, Mi_ITEM_SELECTED_ACTION);
			}
		}
	public		void		insertItem(MiMenuItem item, int index)
		{
		insertPart(item, index);
		rebuildAcceleratorEventHandlers();
		rebuildMnemonicEventHandlers();
		if (item.isCascadeMenu())
			{
			if (cascadeRadioStateEnforcer == null)
				cascadeRadioStateEnforcer = new MiRadioStateEnforcer();
			item.getCascadeMenuLauncherButton().setRadioStateEnforcer(cascadeRadioStateEnforcer);
			item.getCascadeMenuLauncherButton().getMenu().appendCommandHandler(
				this, CASCADE_MENU_POPPED_UP_COMMAND_NAME, Mi_PART_SHOWING_ACTION); 
			item.getCascadeMenuLauncherButton().getMenu().getMenu().appendCommandHandler(
				this, CASCADE_MENU_POPPED_DOWN_COMMAND_NAME, Mi_PART_NOT_SHOWING_ACTION); 
			item.getCascadeMenuLauncherButton().getMenu().appendActionHandler(
				this, Mi_ITEM_SELECTED_ACTION);
			}
		}
	protected	void		registerItem(MiMenuItem item)
		{
		MiEvent accelEvent;
		if ((accelEvent = item.getAcceleratorEvent()) != null)
			{
			acceleratorEventHandler.addShortCut(accelEvent, this, String.valueOf(getNumberOfParts()));
			}
		MiEvent mnemonic;
		if ((mnemonic = item.getMnemonicEvent()) != null)
			{
			mnemonicEventHandler.addShortCut(mnemonic, this, String.valueOf(getNumberOfParts()));
			}
		}
	public		void		removeItem(MiMenuItem item)
		{
		removePart(item);
		rebuildAcceleratorEventHandlers();
		rebuildMnemonicEventHandlers();
		}
	public		void		removeAllItems()
		{
		removeAllParts();
		rebuildAcceleratorEventHandlers();
		rebuildMnemonicEventHandlers();
		}
	public		void		removeAllAdjacentSeparators()
		{
		boolean removedSomething = false;
		for (int i = 1; i < getNumberOfParts(); ++i)
			{
			MiMenuItem menuItem = (MiMenuItem )getPart(i);
			if (menuItem.isSeparator())
				{
				if (((MiMenuItem )getPart(i - 1)).isSeparator())
					{
					removePart(i);
					--i;
					removedSomething = true;
					}
				}
			}
		// Remove separators if first or last element in menu...
		if (((MiMenuItem )getPart(0)).isSeparator())
			{
			removePart(0);
			removedSomething = true;
			}
		if (((MiMenuItem )getPart(getNumberOfParts() - 1)).isSeparator())
			{
			removePart(getNumberOfParts() - 1);
			removedSomething = true;
			}
		if (removedSomething)
			{
			rebuildAcceleratorEventHandlers();
			rebuildMnemonicEventHandlers();
			}
		}
	public		int		getIndexOfItem(MiMenuItem item)
		{
		return(getIndexOfPart(item));
		}

	public		int		getNumberOfItems()
		{
		return(getNumberOfParts());
		}
	public		MiPart		getItem(int index)
		{
		return(getPart(index));
		}
	public		MiMenuItem	getMenuItem(int index)
		{
		return((MiMenuItem )getPart(index));
		}

	protected	void		nowShowing(boolean flag)
		{
		super.nowShowing(flag);
		cascadeMenuIndex = -1;
		popdownCascadeMenus();
		armCascadeMenuItem(false);
		deBrowseAll();
		}

	protected	void		popdownCascadeMenus()
		{
		for (int i = 0; i < getNumberOfItems(); ++i)
			{
			if (getMenuItem(i).isCascadeMenu())
				getMenuItem(i).getCascadeMenuLauncherButton().select(false);
			}
		}
	public		boolean		isBrowsable()
		{
		return(true);
		}
	public		void		browseItem(MiBounds cursor)
		{
		int index = pickRow(cursor);
		browseItem(index);
		}

	public		boolean		activateItem(MiBounds cursor)
		{
		return(false);
		}
	public		boolean		toggleSelectItem(MiBounds cursor)
		{
		// No support for toggle select - just use normal select
		return(selectItem(cursor));
		}
	public		boolean		selectItem(MiBounds cursor)
		{
		int index = pickRow(cursor);
		if (index != -1)
			selectItem(index);
		else if (armedIndex != -1)
			selectItem(armedIndex);
		else
			return(false);
		return(true);
		}
	public		void		selectBrowsedItem()
		{
		selectItem(armedIndex);
		}
	public		void		browseVerticalPreviousItem()
		{
		if (getNumberOfParts() == 0)
			return;
		int index = armedIndex - 1;
		int endIndex = armedIndex;
		if (endIndex < 0)
			endIndex = getNumberOfParts() - 1;
		while (index != endIndex)
			{
			if (index < 0)
				{
				index = getNumberOfParts() - 1;
				}
			if (!getMenuItem(index).isSeparator())
				{
				browseItem(index);
				return;
				}
			--index;
			}
		}
	public		void		browseVerticalNextItem()
		{
		if (getNumberOfParts() == 0)
			return;
		int index = armedIndex + 1;
		int endIndex = armedIndex;
		if (endIndex <= 0)
			endIndex = getNumberOfParts() - 1;
		do 	{
			if (index >= getNumberOfParts())
				{
				index = 0;
				}
			if (!getMenuItem(index).isSeparator())
				{
				browseItem(index);
				return;
				}
			++index;
			} while (index != endIndex);
		}
	public		void		browseHorizontalPreviousItem()
		{
		}
	public		void		browseHorizontalNextItem()
		{
		}
	public		void		browseVerticalHomeItem()
		{
		}
	public		void		browseVerticalEndItem()
		{
		}
	public		void		browseHorizontalHomeItem()
		{
		}
	public		void		browseHorizontalEndItem()
		{
		}
	public		void		deSelectAll()
		{
		}
	public		void		selectAdditionalItem(MiBounds cursor)
		{
		}
	public		void		selectInterveningItems(MiBounds cursor)
		{
		}

	public		void		deBrowseAll()
		{
		if (armedIndex != -1) 
			{
//MiDebug.println("deBrowseAll, armedIndex=" + armedIndex + ", cascadeMenuIndex = " + cascadeMenuIndex);
			if (armedIndex != cascadeMenuIndex)
				{
				MiMenuItem menuItem = (MiMenuItem )getPart(armedIndex);
				menuItem.setAttributes(armedItemOrigAttributes);
				menuItem.setNormalAttributes(armedItemOrigNormalAttributes);
				menuItem.setAutoAttributesEnabled(true);
				}
			}
		armedIndex = -1;
		}
	public		void		calcPreferredSize(MiSize size)
		{
		getMinimumSize(size);
		}
	public		void		calcMinimumSize(MiSize size)
		{
		MiMenuSizeInfo sizeInfo = getMenuSizeInfo();
		size.setSize(sizeInfo.width, sizeInfo.height);
		size.addMargins(getTotalMargins()); 
		}
	public		void		draw(MiRenderer renderer)
		{
		super.draw(renderer);
		}
	protected void			doLayout()
		{
		MiMenuSizeInfo 	sizeInfo 	= getMenuSizeInfo();

		MiDistance 	alleyHSpacing 	= getAlleyHSpacing();
		MiDistance 	insetHSpacing 	= getInsetMargins().getWidth();
		MiDistance 	alleyVSpacing 	= getAlleyVSpacing();
		MiBounds	innerBounds	= getInnerBounds();
		MiCoord 	xmin 		= innerBounds.xmin + insetHSpacing;
		MiCoord 	xmax		= xmin + sizeInfo.width;
		MiCoord 	y 		= innerBounds.ymax - alleyVSpacing;
		MiDistance 	rowHeight 	= sizeInfo.rowHeight;
		MiDistance 	labelX 		= xmin + sizeInfo.labelX;
		MiDistance 	accelX 		= xmin + sizeInfo.accelX;
		MiDistance 	height;
		MiMenuItem 	item;
		MiSize 		prefSize 	= new MiSize();
		MiSize 		prefBounds 	= new MiSize();
		MiBounds 	itemBounds 	= new MiBounds();
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			item = getMenuItem(i);
			if (!item.isVisible())
				continue;

			if (item.isSeparator())
				{
				MiPart line = item.getSeparatorGraphics();
				line.setPoint(0, new MiPoint(xmin, y));
				line.setPoint(1, new MiPoint(xmax, y));
				height = line.getLineWidth(); // don't need this if add extra below + 2;
				if (height == 0)
					{
					height = 1;
					}
				if (item.getSeparatorGraphics().getBorderLook() != Mi_FLAT_BORDER_LOOK)
					{
					height += 1;
					}
				}
			else
				{
				MiDistance centerY = y - rowHeight/2;
				if (item.getIconGraphics() != null)
					{
					item.getIconGraphics().setXmin(xmin);
					item.getIconGraphics().setCenterY(centerY);
					}
				if (item.getLabelGraphics() != null)
					{
					item.getLabelGraphics().getPreferredSize(prefSize);
					prefBounds.copy(prefSize);
					item.getLabelGraphics().setSize(prefBounds);

					item.getLabelGraphics().setXmin(labelX);
					item.getLabelGraphics().setCenterY(centerY);
					}
				if (item.getAcceleratorGraphics() != null)
					{
					item.getAcceleratorGraphics().setXmax(xmax);
					item.getAcceleratorGraphics().setCenterY(centerY);
					}

				if (eachRowHasSameHeight)
					{
					height = rowHeight;
					if (item.isCascadeMenu())
						{
						itemBounds.setBounds(xmin, y - height, xmax, y);
						item.getLabelGraphics().replaceBounds(itemBounds);
						}
					}
				else
					{
					item.getPreferredSize(prefSize);
					height = prefSize.getHeight();
					}
				}
			y -= height;
			itemBounds.setBounds(
				xmin - insetHSpacing - 6, y - alleyVSpacing, 
				xmax + insetHSpacing + 6, y + height + alleyVSpacing);
			item.replaceBounds(itemBounds);
			y -= 3 * alleyVSpacing/2;
			}
		}

	public		void		processCommand(String arg)
		{
//MiDebug.printStackTrace("processcmd, arg = " + arg);
		if (arg.equals(CASCADE_MENU_POPPED_UP_COMMAND_NAME))
			armCascadeMenuItem(true);
		else if (arg.equals(CASCADE_MENU_POPPED_DOWN_COMMAND_NAME))
			armCascadeMenuItem(false);
		else if (Utility.isInteger(arg))
			processCommandMenuItem(Integer.parseInt(arg));
		else
			browseEventHandler.processCommand(arg);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			// Close this menu cause item was selected in cascade menu
			dispatchAction(Mi_ITEM_SELECTED_ACTION);
			}
		return(true);
		}
	public		void		refreshLookAndFeel()
		{
		armedItemAttributes = MiToolkit.getAttributes(MiiToolkit.Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES);
		cascadeItemAttributes =  MiToolkit.getAttributes(MiiToolkit.Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES);
		super.refreshLookAndFeel();
		}
	protected	void		processCommandMenuItem(int index)
		{
		MiMenuItem item = getMenuItem(index);
		if (!item.isSensitive())
			return;
		if (item.isCascadeMenu())
			return;

		if (!isVisible())
			{
			// FIX? USE ACTIVATED ACTION HERE?
			item.dispatchAction(Mi_SELECTED_ACTION);
			}
		else
			{
			dispatchAction(Mi_ITEM_SELECTED_ACTION);
			item.select(true);
			dispatchAction(Mi_ACTIVATED_ACTION, item);
			}
		}
	protected	int		pickRow(MiBounds cursor)
		{
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiPart part = getPart(i);
			if ((part.isVisible()) && (part.pick(cursor)))
				{
				if (!((MiMenuItem )part).isSeparator())
					return(i);
				if (cursor.getCenterY() - part.getBounds().getCenterY() > 0)
					return(i - 1);
				if (i < getNumberOfParts() - 1)
					return(i + 1);
				return(-1);
				}
			}
		return(-1);
		}

	private		void		armCascadeMenuItem(boolean flag)
		{
//MiDebug.println("armCascadeMenuItem, disarm cascadeMenuIndex = " + cascadeMenuIndex);
		if (cascadeMenuIndex != -1)
			{
			MiMenuItem menuItem = (MiMenuItem )getPart(cascadeMenuIndex);
			menuItem.setAttributes(cascadeItemOrigAttributes);
			menuItem.setNormalAttributes(cascadeItemOrigNormalAttributes);
			menuItem.setAutoAttributesEnabled(true);
			cascadeMenuIndex = -1;
			}

		if (!flag)
			return;

		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiMenuItem menuItem = (MiMenuItem )getPart(i);
			if (!menuItem.isVisible())
				continue;
			if (menuItem.isCascadeMenu())
				{
				if (menuItem.getCascadeMenuLauncherButton().isSelected())
					{	
//MiDebug.println("armCascadeMenuItem, sarm cascadeMenuIndex = " + i);
					if (i == armedIndex)
						{
						cascadeItemOrigAttributes = armedItemOrigAttributes;
						cascadeItemOrigNormalAttributes = armedItemOrigNormalAttributes;
						}
					else
						{
						cascadeItemOrigAttributes = menuItem.getAttributes();
						cascadeItemOrigNormalAttributes = menuItem.getNormalAttributes();
						}

					menuItem.setAutoAttributesEnabled(false);
					cascadeItemAttributes.setStaticAttribute(Mi_STATUS_HELP, 
						cascadeItemOrigAttributes.getAttribute(Mi_STATUS_HELP));
					cascadeItemAttributes.setStaticAttribute(Mi_BALLOON_HELP, 
						cascadeItemOrigAttributes.getAttribute(Mi_BALLOON_HELP));
					cascadeItemAttributes.setStaticAttribute(Mi_TOOL_HINT_HELP,
						cascadeItemOrigAttributes.getAttribute(Mi_TOOL_HINT_HELP));
					menuItem.setAttributes(cascadeItemAttributes);
					getContainingWindow().setStatusBarFocusMessage(
						(MiHelpInfo )cascadeItemOrigAttributes.getAttribute(Mi_STATUS_HELP));
					cascadeMenuIndex = i;
					return;
					}
				}
			}


		}

	private		void		browseItem(int index)
		{
		if (armedIndex == index)
			return;
		deBrowseAll();
		if (index != -1)
			{
			if (index != cascadeMenuIndex)
				{
				MiMenuItem menuItem = (MiMenuItem )getPart(index);
				armedItemOrigAttributes = menuItem.getAttributes();
				armedItemOrigNormalAttributes = menuItem.getNormalAttributes();
				menuItem.setAutoAttributesEnabled(false);
				armedItemAttributes.setStaticAttribute(Mi_STATUS_HELP, 
					armedItemOrigAttributes.getAttribute(Mi_STATUS_HELP));
				armedItemAttributes.setStaticAttribute(Mi_BALLOON_HELP, 
					armedItemOrigAttributes.getAttribute(Mi_BALLOON_HELP));
				armedItemAttributes.setStaticAttribute(Mi_TOOL_HINT_HELP,
					armedItemOrigAttributes.getAttribute(Mi_TOOL_HINT_HELP));
				menuItem.setAttributes(armedItemAttributes);
				getContainingWindow().setStatusBarFocusMessage(
					(MiHelpInfo )armedItemOrigAttributes.getAttribute(Mi_STATUS_HELP));
				}
			armedIndex = index;
			}
		}
	private		void		selectItem(int index)
		{
		if ((index != -1) && (getMenuItem(index).isSensitive()))
			{
//System.out.println("STANDARD menu select menuitem: " + getMenuItem(index));
			MiMenuItem menuItem = getMenuItem(index);
			if (menuItem.isCascadeMenu())
				{
				popdownCascadeMenus();
				menuItem.getCascadeMenuLauncherButton().select(true);
				cascadeMenuIndex = index;
				return;
				}
			dispatchAction(Mi_ITEM_SELECTED_ACTION);
			menuItem.select(true);
			dispatchAction(Mi_ACTIVATED_ACTION, menuItem);
			deBrowseAll();
			}
		}
	private		MiMenuSizeInfo	getMenuSizeInfo()
		{
		MiMenuItem 	item;
		int 		numSeparators 		= 0;
		MiMenuSizeInfo 	sizeInfo 		= new MiMenuSizeInfo();
		MiSize 		prefSize 		= new MiSize();
		MiDistance	assembledRowHeights	= 0;
		MiDistance 	alleyHSpacing 		= getAlleyHSpacing();
		MiDistance 	alleyVSpacing 		= getAlleyVSpacing();

		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			item = (MiMenuItem )getPart(i);
			if (!item.isVisible())
				continue;

			if (item.isSeparator())
				{
				MiDistance separatorHeight = item.getSeparatorGraphics().getLineWidth();
				if (separatorHeight == 0)
					{
					separatorHeight = 1;
					}
				if (item.getSeparatorGraphics().getBorderLook() != Mi_FLAT_BORDER_LOOK)
					{
					separatorHeight += 1;
					}
				sizeInfo.height += separatorHeight;
				++numSeparators;
				}
			else
				{
				MiDistance thisRowHeight = 0;
				if (item.getIconGraphics() != null)
					{
					item.getIconGraphics().getPreferredSize(prefSize);
					thisRowHeight = prefSize.getHeight();
					sizeInfo.iconBounds.accumulateMaxWidthAndHeight(prefSize);
					}
				if (item.getLabelGraphics() != null)
					{
					item.getLabelGraphics().getPreferredSize(prefSize);
					thisRowHeight = Math.max(prefSize.getHeight(), thisRowHeight);
					if (!item.isCascadeMenu())
						sizeInfo.labelBounds.accumulateMaxWidthAndHeight(prefSize);
					}
				if (item.getAcceleratorGraphics() != null)
					{
					item.getAcceleratorGraphics().getPreferredSize(prefSize);
					thisRowHeight = Math.max(prefSize.getHeight(), thisRowHeight);
					sizeInfo.accelBounds.accumulateMaxWidthAndHeight(prefSize);
					}
				assembledRowHeights += thisRowHeight;
				}
			}

		MiBounds tmp = new MiBounds();
		tmp.accumulateMaxWidthAndHeight(sizeInfo.iconBounds);
		tmp.accumulateMaxWidthAndHeight(sizeInfo.labelBounds);
		tmp.accumulateMaxWidthAndHeight(sizeInfo.accelBounds);
		sizeInfo.rowHeight = tmp.getHeight();
		if (eachRowHasSameHeight)
			{
			sizeInfo.height += 
				(getNumberOfParts() - numSeparators) * sizeInfo.rowHeight +
				(getNumberOfParts() - 1) * alleyVSpacing;
			}
		else
			{
			sizeInfo.height += 
				assembledRowHeights + (getNumberOfParts() - 1) * alleyVSpacing;
			}

		if (sizeInfo.iconBounds.getWidth() > 0)
			{
			sizeInfo.width = sizeInfo.iconBounds.getWidth();
			sizeInfo.iconX = 0;
			}
		if (sizeInfo.labelBounds.getWidth() > 0)
			{
			sizeInfo.width += sizeInfo.width > 0 ? alleyHSpacing : 0;
			sizeInfo.labelX = sizeInfo.width;
			sizeInfo.width += sizeInfo.labelBounds.getWidth();
			}
		if (sizeInfo.accelBounds.getWidth() > 0)
			{
			sizeInfo.width += sizeInfo.width > 0 ? alleyHSpacing : 0;
			sizeInfo.accelX = sizeInfo.width;
			sizeInfo.width += sizeInfo.accelBounds.getWidth();
			}
		return(sizeInfo);
		}
					// usefull if menuitems are removed from menus.
	private		void		rebuildMnemonicEventHandlers()
		{	
		mnemonicEventHandler.removeAllShortCuts();

		mnemonicEventHandler.addShortCut(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_DOWN_ARROW_KEY, 0), this, 
			MiBrowsableGridEventHandler.BrowseVerticalNextEventName);
		mnemonicEventHandler.addShortCut(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_UP_ARROW_KEY, 0), this, 
			MiBrowsableGridEventHandler.BrowseVerticalPreviousEventName);
		mnemonicEventHandler.addShortCut(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_SPACE_KEY, 0), this, 
			MiBrowsableGridEventHandler.SelectBrowsedEventName);
		mnemonicEventHandler.addShortCut(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_RETURN_KEY, 0), this, 
			MiBrowsableGridEventHandler.SelectBrowsedEventName);

		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiEvent mnemonic;
			if ((mnemonic = getMenuItem(i).getMnemonicEvent()) != null)
				{
				mnemonicEventHandler.addShortCut(mnemonic, this, String.valueOf(i));
				}
			}
		}
					// usefull if menuitems are removed from menus.
	private		void		rebuildAcceleratorEventHandlers()
		{	
		acceleratorEventHandler.removeAllShortCuts();

		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiEvent accelEvent;
			if ((accelEvent = getMenuItem(i).getAcceleratorEvent()) != null)
				{
				acceleratorEventHandler.addShortCut(accelEvent, this, String.valueOf(i));
				}
			}
		}
	}

class MiMenuSizeInfo
	{
	MiBounds	iconBounds	= new MiBounds();
	MiBounds	labelBounds	= new MiBounds();
	MiBounds	accelBounds	= new MiBounds();
	MiDistance	height;
	MiDistance	width;
	MiDistance	iconX;
	MiDistance	labelX;
	MiDistance	accelX;
	MiDistance	rowHeight;
	}



