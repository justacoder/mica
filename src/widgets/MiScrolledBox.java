
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
import com.swfm.mica.util.Strings; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiScrolledBox extends MiWidget implements MiiActionHandler, MiiActionTypes
	{
	private		MiScrollBar		vScrollBar;
	private		MiScrollBar		hScrollBar;
	private		MiColumnLayout		vScrollBarBox;
	private		MiRowLayout		hScrollBarBox;
	private		MiVisibleContainer	box;
	private		int			hScrollBarDisplayPolicy = Mi_DISPLAY_AS_NEEDED;
	private		int			vScrollBarDisplayPolicy = Mi_DISPLAY_AS_NEEDED;
	private		MiPart			subject;
	private		boolean			partsHaveValidLayout;
	private		MiiScrollableData	scrollable;
	private		MiSize			cachedPreferredSize	= new MiSize();
	private		MiSize			tmpSize			= new MiSize();



	public				MiScrolledBox()
		{
		this(new MiEditor());
		}
	public				MiScrolledBox(MiPart subject)
		{
		setVisibleContainerAutomaticLayoutEnabled(false);
		setScrollable((MiiScrollableData )subject);
		setSubject(subject);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}

	public		void		setScrollable(MiiScrollableData scrollable)
		{
		this.scrollable = scrollable;
		if (vScrollBar != null)
			{
			vScrollBar.setScrollable(scrollable);
			hScrollBar.setScrollable(scrollable);
			}
		}
	public		void		setSubject(MiPart subject)
		{
		if (this.subject != null)
			{
			this.subject.removeActionHandlers(this);

			this.subject = subject;

			vScrollBar.setObserver(subject);
			hScrollBar.setObserver(subject);

			box.removeAllParts();
			box.appendPart(subject);

			subject.appendActionHandler(this, Mi_ITEM_SCROLLED_ACTION);
			subject.appendActionHandler(this, Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION);

			return;
			}
		this.subject = subject;
		vScrollBar = new MiScrollBar(Mi_VERTICAL);
		hScrollBar = new MiScrollBar(Mi_HORIZONTAL);
		hScrollBar.setInsetMargins(new MiMargins(2, 0, 2, 2));
		vScrollBar.setInsetMargins(new MiMargins(2, 2, 0, 2));
		vScrollBar.setObserver(subject);
		vScrollBar.setGenerateAbsolutePositionsOnly(false);
		hScrollBar.setObserver(subject);
		hScrollBar.setGenerateAbsolutePositionsOnly(false);

		box = new MiWidget();
		box.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		box.setBackgroundColor(subject.getBackgroundColor());
		box.setVisibleContainerAutomaticLayoutEnabled(false);
		box.setDisplaysFocusBorder(true);
		box.setInsetMargins(2);
		box.setAcceptingKeyboardFocus(true);
		box.appendEventHandler(new MiDelegateEvents(subject, Mi_KEY_EVENT));
		box.setName("box");

		box.appendPart(subject);

		appendPart(box);

		vScrollBarBox = new MiColumnLayout();
		vScrollBarBox.setElementHSizing(Mi_NONE);
		vScrollBarBox.setElementHJustification(Mi_LEFT_JUSTIFIED);
		vScrollBarBox.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		vScrollBarBox.setUniqueElementIndex(0);
		vScrollBarBox.appendPart(vScrollBar);
		appendPart(vScrollBarBox);

		hScrollBarBox = new MiRowLayout();
		hScrollBarBox.setElementVSizing(Mi_NONE);
		hScrollBarBox.setElementVJustification(Mi_TOP_JUSTIFIED);
		hScrollBarBox.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		hScrollBarBox.setUniqueElementIndex(0);
		hScrollBarBox.appendPart(hScrollBar);
		appendPart(hScrollBarBox);

		vScrollBarBox.setVisible(false);
		hScrollBarBox.setVisible(false);

		subject.appendActionHandler(this, Mi_ITEM_SCROLLED_ACTION);
		subject.appendActionHandler(this, Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION);
		}
	public		MiPart		getSubject()
		{
		return(subject);
		}
	public		MiiScrollableData getScrollable()
		{
		return(scrollable);
		}
	public		MiRowLayout	getHScrollBarBox()
		{
		return(hScrollBarBox);
		}
	public		MiColumnLayout	getVScrollBarBox()
		{
		return(vScrollBarBox);
		}
	public		MiVisibleContainer	getBox()
		{
		return(box);
		}
					/**------------------------------------------------------
	 				 * Sets the horizontal scrollbar display policy to either:
					 * 	Mi_DISPLAY_ALWAYS
					 * 	Mi_DISPLAY_NEVER
					 * 	Mi_DISPLAY_AS_NEEDED (the default)
					 * @param policy	the policy
					 * @see			#setVScrollBarDisplayPolicy
					 * @see			#getHScrollBarDisplayPolicy
					 *------------------------------------------------------*/
	public		void		setHScrollBarDisplayPolicy(int policy)
		{
		hScrollBarDisplayPolicy = policy;
		}
					/**------------------------------------------------------
	 				 * Gets the horizontal scrollbar display policy which is either:
					 * 	Mi_DISPLAY_ALWAYS
					 * 	Mi_DISPLAY_NEVER
					 * 	Mi_DISPLAY_AS_NEEDED (the default)
					 * @return 		the policy
					 * @see			#setVScrollBarDisplayPolicy
					 * @see			#setHScrollBarDisplayPolicy
					 *------------------------------------------------------*/
	public		int		getHScrollBarDisplayPolicy()
		{
		return(hScrollBarDisplayPolicy);
		}
					/**------------------------------------------------------
	 				 * Gets the vertical scrollbar display policy which is either:
					 * 	Mi_DISPLAY_ALWAYS
					 * 	Mi_DISPLAY_NEVER
					 * 	Mi_DISPLAY_AS_NEEDED (the default)
					 * @param policy 	the policy
					 * @see			#getVScrollBarDisplayPolicy
					 * @see			#setHScrollBarDisplayPolicy
					 *------------------------------------------------------*/
	public		void		setVScrollBarDisplayPolicy(int policy)
		{
		vScrollBarDisplayPolicy = policy;
		}
					/**------------------------------------------------------
	 				 * Gets the vertical scrollbar display policy which is either:
					 * 	Mi_DISPLAY_ALWAYS
					 * 	Mi_DISPLAY_NEVER
					 * 	Mi_DISPLAY_AS_NEEDED (the default)
					 * @return 		the policy
					 * @see			#setVScrollBarDisplayPolicy
					 * @see			#setHScrollBarDisplayPolicy
					 *------------------------------------------------------*/
	public		int		getVScrollBarDisplayPolicy()
		{
		return(vScrollBarDisplayPolicy);
		}
	public		void		setBackgroundColor(java.awt.Color c)
		{
		//hScrollBar.setBackgroundColor(c);
		//vScrollBar.setBackgroundColor(c);
		hScrollBar.getThumb().setBackgroundColor(c);
		hScrollBar.getLessArrow().setBackgroundColor(c);
		hScrollBar.getMoreArrow().setBackgroundColor(c);
		vScrollBar.getThumb().setBackgroundColor(c);
		vScrollBar.getLessArrow().setBackgroundColor(c);
		vScrollBar.getMoreArrow().setBackgroundColor(c);
		if (c != null)
			{
			hScrollBar.setBackgroundColor(c.darker());
			vScrollBar.setBackgroundColor(c.darker());
			}
		else
			{
			hScrollBar.setBackgroundColor(c);
			vScrollBar.setBackgroundColor(c);
			}
		super.setBackgroundColor(c);
		}
	public		void		setValue(String value)
		{
		if (subject instanceof MiWidget)
			((MiWidget )subject).setValue(value);
		}
	public		String		getValue()
		{
		if (subject instanceof MiWidget)
			return(((MiWidget )subject).getValue());
		return(null);
		}
	public		void		setContents(Strings contents)
		{
		if (subject instanceof MiWidget)
			((MiWidget )subject).setContents(contents);
		}
	public		Strings		getContents()
		{
		if (subject instanceof MiWidget)
			return(((MiWidget )subject).getContents());
		return(null);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_ITEM_SCROLLED_ACTION))
			updateScrollBars();
		else if (action.hasActionType(Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION))
			invalidateLayout();
		return(true);
		}
	public		void		calcPreferredSize(MiSize size)
		{
		subject.getPreferredSize(size);
		size.addMargins(box.getTotalMargins());
		size.addMargins(getTotalMargins());
		adjustDesiredSizeForScrollBars(size);
		// Check to see if preferred size changed and if so, notify container
		// Need this cause this widget changes it's preferred size sometimes during layout

		if (!size.equals(cachedPreferredSize))
			{
			invalidateContainersLayout();
			cachedPreferredSize.copy(size);
			}
		}
	public		void		calcMinimumSize(MiSize size)
		{
		subject.getMinimumSize(size);
		size.addMargins(box.getTotalMargins());
		size.addMargins(getTotalMargins());
		adjustDesiredSizeForScrollBars(size);
		}
	protected	void		adjustDesiredSizeForScrollBars(MiSize size)
		{
		boolean[] horizontal = new boolean[1];
		boolean[] vertical = new boolean[1];
		areScrollBarsNeeded(horizontal, vertical, true);
		if (horizontal[0])
			size.setHeight(size.getHeight() + hScrollBarBox.getHeight());
		if (vertical[0])
			size.setWidth(size.getWidth() + vScrollBarBox.getWidth());
		}

	protected	void		areScrollBarsNeeded(boolean[] horizontal, boolean[] vertical, boolean preferrably)
		{
		double value;

		if (hScrollBarDisplayPolicy == Mi_DISPLAY_ALWAYS)
			horizontal[0] = true;
		else if (hScrollBarDisplayPolicy == Mi_DISPLAY_NEVER)
			horizontal[0] = false;
		else // Mi_DISPLAY_AS_NEEDED
			{
/*
			if (preferrably)
				value = scrollable.getPreferredNormalizedHorizontalAmountVisible();
			else
*/
				value = scrollable.getNormalizedHorizontalAmountVisible();

			if (value < 1.0)
				horizontal[0] = true;
			else
				horizontal[0] = false;
			}

		if (vScrollBarDisplayPolicy == Mi_DISPLAY_ALWAYS)
			vertical[0] = true;
		else if (vScrollBarDisplayPolicy == Mi_DISPLAY_NEVER)
			vertical[0] = false;
		else // Mi_DISPLAY_AS_NEEDED
			{
/*
			if (preferrably)
				value = scrollable.getPreferredNormalizedVerticalAmountVisible();
			else
*/
				value = scrollable.getNormalizedVerticalAmountVisible();

			if (value < 1.0)
				vertical[0] = true;
			else
				vertical[0] = false;
			}
		}



	/**
	* This routine catches changes in layout of the subject and
	* registers this with the containing window so that
	* the containers of this are not re-laid-out which would cause
	* an annoying visual disturbance.
	*/
	public	void			invalidateLayout()
		{
		if (!partsHaveValidLayout)
			return;
		MiWindow win = getContainingWindow();
		if (win != null)
			{
			win.invalidateEditorPartLayout(this);
			setInvalidLayoutNotificationsEnabled(false);
			super.invalidateLayout();
			setInvalidLayoutNotificationsEnabled(true);

			// 7-4-99 Notify containers if preferred size changes
			//if ((getContainer(0) != null) && (getContainer(0).hasValidLayout()))
				// 7-20-99 this causes MiTable to valididate itself which caused
				// MiText to be setBounds'd in a rowLayout, event tho the text was
				// in the middle of resizing itself cause of a text change 
				//calcPreferredSize(new MiSize());
			}
		else
			{
			//System.out.println("NO WINDOW TO INVALIDATE");
			super.invalidateLayout();
			}

		partsHaveValidLayout = false;
		}

	public		boolean		hasValidLayout()
		{
		return(partsHaveValidLayout && super.hasValidLayout());
		}

	protected	void		doLayout()
		{
		super.doLayout();

		MiDistance vScrollBarWidth = vScrollBarBox.isVisible() ? vScrollBarBox.getWidth() : 0;
		MiDistance hScrollBarHeight = hScrollBarBox.isVisible() ? hScrollBarBox.getHeight() : 0;
		MiBounds innerBounds = getInnerBounds();

/*
		if ((scrollBarsMayNeedToBeAddedOrRemoved())
			|| (hScrollBar.isVisible()
				&& (!hScrollBar.getBounds().equals(
					innerBounds.getXmin(), 
					innerBounds.getYmin(), 
					innerBounds.getXmax() - vScrollBarWidth, 
					innerBounds.getYmin() + hScrollBarHeight)))
			|| (vScrollBar.isVisible()
				&& (!vScrollBar.getBounds().equals(
					innerBounds.getXmax() - vScrollBarWidth, 
					innerBounds.getYmin() + hScrollBarHeight, 
					innerBounds.getXmax(), 
					innerBounds.getYmax()))))
*/
			{
//System.out.println("scrollBarsMayNeedToBeAddedOrRemoved() SHOULD be TRUE = " + scrollBarsMayNeedToBeAddedOrRemoved());
			// ----------------------------------------------
			// Assume there are no scrollbars and allow
			// subject to fill entire inner area...
			// -----------------------------------------------

			// Note for future: this almost always invalidates the layot of all of this scrollbox's parents
			box.setBounds(innerBounds);

			subject.setBounds(box.getInnerBounds());
			subject.validateLayout();

			boolean[] horizontal = new boolean[1];
			boolean[] vertical = new boolean[1];
			areScrollBarsNeeded(horizontal, vertical, false);

			vScrollBarBox.setSize(vScrollBarBox.getPreferredSize(tmpSize));
			hScrollBarBox.setSize(hScrollBarBox.getPreferredSize(tmpSize));

			vScrollBarWidth = vertical[0] ? vScrollBarBox.getWidth() : 0;
			hScrollBarHeight = horizontal[0] ? hScrollBarBox.getHeight() : 0;

			if (horizontal[0])
				{
				hScrollBarBox.setBounds(
					innerBounds.getXmin(), 
					innerBounds.getYmin(), 
					innerBounds.getXmax() - vScrollBarWidth, 
					innerBounds.getYmin() + hScrollBarHeight);
				hScrollBarBox.validateLayout();
				}
			if (vertical[0])
				{
				vScrollBarBox.setBounds(
					innerBounds.getXmax() - vScrollBarWidth, 
					innerBounds.getYmin() + hScrollBarHeight, 
					innerBounds.getXmax(), 
					innerBounds.getYmax());
				vScrollBarBox.validateLayout();
				}

			hScrollBarBox.setVisible(horizontal[0]);
			vScrollBarBox.setVisible(vertical[0]);
			}

		// ----------------------------------------------
		// Adjust for any visible scrollbars and set
		// subject to fill remaining inner area...
		// -----------------------------------------------
		box.setBounds(
			innerBounds.getXmin(), 
			innerBounds.getYmin() + hScrollBarHeight, 
			innerBounds.getXmax() - vScrollBarWidth, 
			innerBounds.getYmax());
		subject.setBounds(box.getInnerBounds());
		box.validateLayout();

		updateScrollBars();

		if (!box.hasValidLayout()) // 4-14-2004
			{
			box.validateLayout();
			}

		partsHaveValidLayout = true;

		MiSize size = new MiSize();
		if (getBounds().isSmallerSizeThan(getMinimumSize(size)))
			{
//MiDebug.println(this + " TOO SMALL, invalidate again");
			invalidateContainersLayout();
			}
		else 
			{
			// Check to see if preferred size changed and if so, notify container
			calcPreferredSize(size);
			}
		refreshBounds();
		}
	protected	boolean		scrollBarsMayNeedToBeAddedOrRemoved()
		{
		if (((hScrollBarDisplayPolicy == Mi_DISPLAY_ALWAYS)
			&& (!hScrollBarBox.isVisible()))
		|| ((hScrollBarDisplayPolicy == Mi_DISPLAY_NEVER)
			&& (hScrollBarBox.isVisible())))
			{
			return(true);
			}
		if (((vScrollBarDisplayPolicy == Mi_DISPLAY_ALWAYS)
			&& (!vScrollBarBox.isVisible()))
		|| ((vScrollBarDisplayPolicy == Mi_DISPLAY_NEVER)
			&& (vScrollBarBox.isVisible())))
			{
			return(true);
			}
		if (((hScrollBarDisplayPolicy == Mi_DISPLAY_ALWAYS)
			|| (hScrollBarDisplayPolicy == Mi_DISPLAY_NEVER))
		&& ((vScrollBarDisplayPolicy == Mi_DISPLAY_ALWAYS)
			|| (vScrollBarDisplayPolicy == Mi_DISPLAY_NEVER)))
			{
			return(false);
			}

		// Mi_DISPLAY_AS_NEEDED
		if (hScrollBarDisplayPolicy == Mi_DISPLAY_AS_NEEDED)
			{
			double value = scrollable.getNormalizedHorizontalAmountVisible();
			if ((value < 1.0) && ((!hScrollBarBox.isVisible())
				|| (value > 8.0)
				|| (value 
				> (subject.getWidth() - vScrollBarBox.getWidth())/subject.getWidth())))
				{
				return(true);
				}
			}
		if (vScrollBarDisplayPolicy == Mi_DISPLAY_AS_NEEDED)
			{
			double value = scrollable.getNormalizedVerticalAmountVisible();
			if ((value < 1.0) && ((!vScrollBarBox.isVisible())
				|| (value > 8.0) 
				|| (value 
				> (subject.getHeight() - hScrollBarBox.getHeight())/subject.getHeight())))
				{
				return(true);
				}
			}
		return(false);
		}
	public		void		updateScrollBars()
		{
		double value;
		// If scrollbar is visible and not currently being dragged by user...
		if ((vScrollBarBox.isVisible()) && (!vScrollBar.isSelected()))
			{
			value = scrollable.getNormalizedVerticalPosition();
			if (value != vScrollBar.getNormalizedValue())
				vScrollBar.setNormalizedPositionOfThumb(value);

			value = scrollable.getNormalizedVerticalAmountVisible();

			vScrollBar.setNormalizedLengthOfThumb(value);
			}
		// If scrollbar is visible and not currently being dragged by user...
		if ((hScrollBarBox.isVisible()) && (!hScrollBar.isSelected()))
			{
			value = scrollable.getNormalizedHorizontalPosition();

			if (value != hScrollBar.getNormalizedValue())
				hScrollBar.setNormalizedPositionOfThumb(value);

			value = scrollable.getNormalizedHorizontalAmountVisible();
			hScrollBar.setNormalizedLengthOfThumb(value);
			}
		}
	}

