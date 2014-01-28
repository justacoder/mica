
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

// Used by menubar and radioBoxes: can be both refered to and inherited from
/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiRadioStateEnforcer extends MiWidget
	{
	private			boolean		working 		= false;
	private			MiParts		currentlySelectedItems	= new MiParts();
	private			MiParts		previouslySelectedItems	= new MiParts();
	private			int		maxNumSelected 		= 1;
	private			int		minNumSelected 		= 0;
	private			MiPart		target;
	private			MiPart		currentSelectedItem;

	public					MiRadioStateEnforcer()
		{
		target = this;
		}
	public			void		setTarget(MiPart t)	 	{ target = t; 		}
	public			MiPart		getTarget() 			{ return(target); 	}

	public			void		setMaxNumSelected(int n) 	{ maxNumSelected = n; 	}
	public			int		getMaxNumSelected() 		{ return(maxNumSelected);}

	public			void		setMinNumSelected(int n) 	{ minNumSelected = n; 	}
	public			int		getMinNumSelected() 		{ return(minNumSelected);}

	public			void		appendPart(MiPart obj)
		{
		if (obj instanceof MiWidget)
			((MiWidget )obj).setRadioStateEnforcer(this);
		super.appendPart(obj);
		}
	public			void		insertPart(MiPart obj, int index)
		{
		if (obj instanceof MiWidget)
			((MiWidget )obj).setRadioStateEnforcer(this);
		super.insertPart(obj, index);
		}
	public			boolean		verifyProposedStateChange(MiPart widget, boolean toBeSelected)
		{
		if ((maxNumSelected != 1) || (minNumSelected > 1))
			{
			assureNumberOfItemsSelectedIsCorrect();
			return(true);
			}
		if (toBeSelected)
			{
			// Turned on widget...
			if (currentSelectedItem != widget)
				{
				if (currentSelectedItem != null)
					{
					MiPart tmp = currentSelectedItem;
					currentSelectedItem = null;
					tmp.select(false);
					}
				currentSelectedItem = widget;
				}
			}
		else
			{
			// Turned off widget...
			if (currentSelectedItem == widget)
				{
				if (minNumSelected != 0)
					return(false);
				currentSelectedItem = null;
				}
			}
		return(true);
		}
						// Handles the general cases
	public			void		assureNumberOfItemsSelectedIsCorrect()
		{
		if (working)
			return;
		working = true;

		int numSelected = 0;
		// -------------------------------------------------
		// Find any new selected items
		// -------------------------------------------------
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			{
			if (target.getPart(i).isSelected())
				{
				++numSelected;
				if (currentlySelectedItems.indexOf(target.getPart(i)) == -1)
					{
					// Just now selected...
					currentlySelectedItems.addElement(target.getPart(i));
					}
				}
			}

		// -------------------------------------------------
		// Clean up currentlySelectedItems
		// -------------------------------------------------
		for (int i = 0; i < currentlySelectedItems.size(); ++i)
			{
			MiPart obj = currentlySelectedItems.elementAt(i);
			// -------------------------------------------------
			// Throw out objects that are no longer part of target
			// -------------------------------------------------
			if (target.getIndexOfPart(obj) == -1)
				{
				currentlySelectedItems.removeElementAt(i);
				--i;
				}
			// -------------------------------------------------
			// Remove objects that are no longer selected to previouslySelectedList
			// -------------------------------------------------
			else if (!obj.isSelected())
				{
				previouslySelectedItems.addElement(obj);
				currentlySelectedItems.removeElementAt(i);
				--i;
				}
			}
		// -------------------------------------------------
		// Clean up previouslySelectedItems
		// -------------------------------------------------
		for (int i = 0; i < previouslySelectedItems.size(); ++i)
			{
			MiPart obj = previouslySelectedItems.elementAt(i);
			// -------------------------------------------------
			// Throw out objects that are no longer part of target
			// -------------------------------------------------
			if (target.getIndexOfPart(obj) == -1)
				{
				previouslySelectedItems.removeElementAt(i);
				--i;
				}
			// -------------------------------------------------
			// Remove objects that are selected 
			// -------------------------------------------------
			else if (obj.isSelected())
				{
				previouslySelectedItems.removeElementAt(i);
				--i;
				}
			}


		// -------------------------------------------------
		// DEselect objects that are selected in the selected list.
		// -------------------------------------------------
		if (numSelected > maxNumSelected)
			{
			for (int i = 0; (i < currentlySelectedItems.size()) 
				&& (numSelected > maxNumSelected); ++i)
				{
				MiPart obj = currentlySelectedItems.elementAt(i);
				obj.select(false);
				previouslySelectedItems.addElement(obj);
				currentlySelectedItems.removeElementAt(i);
				--numSelected;
				--i;
				}
			}
		// -------------------------------------------------
		// REselect objects that used to be selected in the previouslySelected list.
		// -------------------------------------------------
		if (numSelected < minNumSelected)
			{
			for (int i = 0; (i < currentlySelectedItems.size()) 
				&& (numSelected < minNumSelected); ++i)
				{
				MiPart obj = previouslySelectedItems.elementAt(i);
				obj.select(true);
				currentlySelectedItems.addElement(obj);
				previouslySelectedItems.removeElementAt(i);
				++numSelected;
				--i;
				}
			}
		// -------------------------------------------------
		// Select new objects from the pool in the target
		// -------------------------------------------------
		if (numSelected < minNumSelected)
			{
			for (int i = 0; i < (target.getNumberOfParts()) 
				&& (numSelected < minNumSelected); ++i)
				{
				if (!target.getPart(i).isSelected())
					{
					target.getPart(i).select(true);
					currentlySelectedItems.addElement(target.getPart(i));
					++numSelected;
					}
				}
			}
		working = false;
		}
	}


