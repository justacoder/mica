
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

/**----------------------------------------------------------------------------------------------
 * This class manages the selection state of it's contents. This is
 * usually used to assure that only one item is selected in a group
 * of items. If a second item is selected in such a situation then
 * the previously selected item will be automatically deselected first.
 *
 * @see MiRadioStateEnforcer
 * @see MiPart#select
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiRadioBox extends MiWidget
	{
	MiRadioStateEnforcer		radioStateEnforcer;


					/**------------------------------------------------------
	 				 * Constructs a new MiRadioBox. The selection state of all
					 * parts will be managed by a MiRadioStateEnforcer.
					 *------------------------------------------------------*/
	public				MiRadioBox()
		{
		this(new MiRadioStateEnforcer());
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiRadioBox. The selection state of all
					 * parts will be managed by the given MiRadioStateEnforcer.
	 				 * @param enforcer 	the radio state manager to be used
					 *			for parts of this radio box.
					 *------------------------------------------------------*/
	public				MiRadioBox(MiRadioStateEnforcer enforcer)
		{
		radioStateEnforcer = enforcer;

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		layout.setElementHSizing(Mi_NONE);
		setLayout(layout);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}

					/**------------------------------------------------------
	 				 * Returns the MiRadioStateEnforcer used by this MiRadioBox.
					 * @return		the MiRadioStateEnforcer
					 *------------------------------------------------------*/
	public		MiRadioStateEnforcer getRadioStateEnforcer()
		{
		return(radioStateEnforcer);
		}

					/**------------------------------------------------------
	 				 * Appends the given part to this container. If the part
					 * is a MiWidget then it's selection state is managed as
					 * part of the raio state of this MiRadioBox.
	 				 * @param part 		the part to append
					 * @overrides		MiContainer.appendPart
					 *------------------------------------------------------*/
	public		void		appendPart(MiPart part)
		{
		super.appendPart(part);
		addPartToRadioEnforcer(part);
		}
					/**------------------------------------------------------
	 				 * Inserts the given part at the given index into this
					 * container. If the part is a MiWidget then it's 
					 * selection state is managed as part of the raio state 
					 * of this MiRadioBox.
	 				 * @param part 		the part to insert
	 				 * @param index 	where to insert the part
					 * @overrides		MiContainer.insertPart
					 *------------------------------------------------------*/
	public		void		insertPart(MiPart part, int index)
		{
		super.appendPart(part);
		addPartToRadioEnforcer(part);
		}
					/**------------------------------------------------------
	 				 * Replaces the part at the given index with the given 
					 * part. If the part is a MiWidget then it's selection
					 * state is managed as part of the raio state of this 
					 * MiRadioBox.
	 				 * @param part 		the part to insert
	 				 * @param index 	where to place the part
					 * @overrides		MiContainer.setPart
					 *------------------------------------------------------*/
	public		void		setPart(MiPart part, int index)
		{
		removePartFromRadioEnforcer(getPart(index));
		super.setPart(part, index);
		addPartToRadioEnforcer(part);
		}
					/**------------------------------------------------------
	 				 * Removes the part from this container at the given index.
					 * If the part is a MiWidget then it's selection state is 
					 * no longer managed as part of the raio state of this 
					 * MiRadioBox.
	 				 * @param index 	index of the part to remove.
					 * @overrides		MiContainer.removePart
					 *------------------------------------------------------*/
	public		void		removePart(int index)
		{
		removePartFromRadioEnforcer(getPart(index));
		super.removePart(index);
		}
					/**------------------------------------------------------
	 				 * Removes the given part from this container.
					 * If the part is a MiWidget then it's selection state is 
					 * no longer managed as part of the raio state of this 
					 * MiRadioBox.
	 				 * @param part 		the part to remove.
					 * @overrides		MiContainer.removePart
					 *------------------------------------------------------*/
	public		void		removePart(MiPart part)
		{
		super.removePart(part);
		removePartFromRadioEnforcer(part);
		}
					/**------------------------------------------------------
	 				 * Removes all parts from this container.
					 * @overrides		MiContainer.removeAllParts
					 *------------------------------------------------------*/
	public		void		removeAllParts()
		{
		for (int i = 0; i < getNumberOfParts(); ++i)
			removePartFromRadioEnforcer(getPart(i));
		super.removeAllParts();
		}
					/**------------------------------------------------------
	 				 * Manages the selection state of the given part, if the part 
					 * is a MiWidget. This method is useful when a part that
					 * is not to be in this container is to be one of the items
					 * in the radio state or when the geiven part has been 
					 * previously removed from the radio manager.
	 				 * @param part 		the part
					 *------------------------------------------------------*/
	public		void		addPartToRadioEnforcer(MiPart part)
		{
		if (part instanceof MiWidget)
			((MiWidget )part).setRadioStateEnforcer(radioStateEnforcer);
		}
					/**------------------------------------------------------
	 				 * No longer manages the selection state of the given part,
					 * if the part is a MiWidget. This method is useful when a 
					 * part that is inside this container is not to be one of
					 * the items in the radio state.
	 				 * @param part 		the part
					 *------------------------------------------------------*/
	public		void		removePartFromRadioEnforcer(MiPart part)
		{
		if (part instanceof MiWidget)
			((MiWidget )part).setRadioStateEnforcer(null);
		}
	}

