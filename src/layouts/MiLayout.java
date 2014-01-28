
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
import com.swfm.mica.util.Utility; 

/**----------------------------------------------------------------------------------------------
 * This class implements the MiiLayout interface and is the base
 * class for all layouts supplied with Mica. Layouts that have this
 * as a base class have
 * <p>
 * (1) a 'target' container of MiParts (the elements) that this layout
 * re-sizes and or re-positions. Note that this layout itself, being a
 * MiContainer, may be it's own target.
 * <p>
 * (2) element justification and sizings that this layout takes into
 * about during the resizing and repositioning.
 * <p>
 * (3) a special 'unique' element that has it's own justification and 
 * sizing. This is used to, for example, put the 'help' pulldown
 * all the way to the right of a menubar or, for example, to make the
 * 'editing area' of a graphics editor fill any extra area in a window
 * that is being resized by the user.
 * <p>
 * There are 3 different margins supported by this class (in addition
 * to the margins supported by the MiPart class). These margins are:
 * <p>
 * <em>inset margins</em>: margins which leave space immediately inside the
 * target and around the bounds created by the elements.
 * <p>
 * <em>alley spacing</em>: space between the elements, both horizontally
 * and vertically. For example in a grid layout, the alleys are the 'streets'
 * between the element 'buildings'.
 * <p>
 * <em>cell margins</em>: margins around each element (in addition to any
 * margins they may have set in their MiPart base class).
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiLayout extends MiContainer implements MiiLayout
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private		MiPart		target;
	private		MiMargins	insetMargins;
	private		MiMargins	cellMargins;
	private		MiDistance	alleyVSpacing		= 2;
	private		MiDistance	alleyHSpacing		= 2;

	private		int		elementHJustification 	= Mi_CENTER_JUSTIFIED;
	private		int		elementVJustification 	= Mi_CENTER_JUSTIFIED;

	private		int		elementHSizing 		= Mi_SAME_SIZE;
	private		int		elementVSizing 		= Mi_SAME_SIZE;

	private 	int		uniqueElementIndex	= -1; // -1 indicates last Element
	private 	int		uniqueElementSizing	= Mi_NONE;
	private 	int		lastElementJustification= Mi_NONE;

	private 	boolean		enabled			= true;




					/**------------------------------------------------------
	 				 * Constructs a new MiLayout with itself as the target. 
					 *------------------------------------------------------*/
	public				MiLayout()
		{
		target = this;
		}
	public		void		setEnabled(boolean flag)
		{
		enabled = flag;
		}
	public		boolean		isEnabled()
		{
		return(enabled);
		}
					/**------------------------------------------------------
	 				 * Sets the target to lay out. 
	 				 * This should be protected and only accessed by subclasses
	 				 * @param part		the target to lay out	
					 *------------------------------------------------------*/
	public		void		setTarget(MiPart part)
		{
		if ((part != target) && (target != null) && (target.getLayout() == this))
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
				+ ": Layouts cannot be assigned to multiple targets at this time.");
			}
			
		target = part;
		}
					/**------------------------------------------------------
	 				 * Gets the target to lay out. 
	 				 * @return 		the target to lay out	
					 *------------------------------------------------------*/
	public		MiPart		getTarget()
		{
		return(target);
		}
					/**------------------------------------------------------
	 				 * Sets the width of the space (alley) between the elements
					 * to lay out.
	 				 * @param h 		the horizontal spacing between elements
					 *------------------------------------------------------*/
	public		void		setAlleyHSpacing(MiDistance h)	
		{
		alleyHSpacing = h;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the width of the space (alley) between the elements
					 * to lay out.
	 				 * @return  		the horizontal spacing between elements
					 *------------------------------------------------------*/
	public		MiDistance	getAlleyHSpacing()
		{
		return(alleyHSpacing);
		}
					/**------------------------------------------------------
	 				 * Sets the height of the space (alley) between the elements
					 * to lay out.
	 				 * @param v 		the vertical spacing between elements
					 *------------------------------------------------------*/
	public		void		setAlleyVSpacing(MiDistance v)	
		{
		alleyVSpacing = v;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the height of the space (alley) between the elements
					 * to lay out.
	 				 * @return  		the vertical spacing between elements
					 *------------------------------------------------------*/
	public		MiDistance	getAlleyVSpacing()
		{
		return(alleyVSpacing);
		}
					/**------------------------------------------------------
	 				 * Sets the width and height of the space (alley) between 
					 * the elements to lay out.
	 				 * @param w 		the spacing between elements
					 *------------------------------------------------------*/
	public		void		setAlleySpacing(MiDistance w)
		{
		alleyHSpacing = w;
		alleyVSpacing = w;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the width and height of the space (alley) between 
					 * the elements to lay out as MiMargins.
	 				 * @return  		the spacing between elements
					 *------------------------------------------------------*/
	public		MiMargins	getAlleyMargins()		
		{
		return(new MiMargins(
			alleyHSpacing/2, alleyVSpacing/2,
			alleyHSpacing/2, alleyVSpacing/2)); 
		}
					/**------------------------------------------------------
	 				 * Sets the margins to be allocated around every element
					 * to be laid out.
	 				 * @param m 		the space around each element
					 *------------------------------------------------------*/
	public		void		setCellMargins(MiMargins m) 	
		{
		if (cellMargins == null)
			cellMargins = new MiMargins();
		cellMargins.copy(m); 
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Sets the margins to be allocated around every element
					 * to be laid out.
	 				 * @param d 		the space on each side of each element
					 *------------------------------------------------------*/
	public		void		setCellMargins(MiDistance d)
		{ 
		if (cellMargins == null)
			cellMargins = new MiMargins();
		cellMargins.setMargins(d); 
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the margins to be allocated around every element
					 * to be laid out.
	 				 * @return  		the space around each element
					 *------------------------------------------------------*/
	public		MiMargins	getCellMargins() 	
		{
		if (cellMargins == null)
			return(new MiMargins(0));
		return(new MiMargins(cellMargins)); 
		}
	public		MiMargins	getCellMargins(MiMargins m) 	
		{
		if (cellMargins == null)
			m.setMargins(0);
		else
			m.copy(cellMargins);
		return(m);
		}
					/**------------------------------------------------------
	 				 * Sets the margins of empty space immediately inside of 
					 * the target's (inner) bounds.
	 				 * @param m  		the 'inside' margins of the target 
					 * @see			MiPart#setMargins
					 *------------------------------------------------------*/
	public		void		setInsetMargins(MiMargins m) 
		{
		if (insetMargins == null)
			insetMargins = new MiMargins();
		insetMargins.copy(m);
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Sets the margins of empty space immediately inside of 
					 * the target's (inner) bounds.
	 				 * @param m  		the 'inside' margins of the target 
					 * @see			MiPart#setMargins
					 *------------------------------------------------------*/
	public		void		setInsetMargins(MiDistance m)	
		{
		if (insetMargins == null)
			insetMargins = new MiMargins();
		insetMargins.setMargins(m);
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the margins of empty space immediately inside of 
					 * the target's (inner) bounds.
	 				 * @return   		the 'inside' margins of the target 
					 * @see			MiPart#setMargins
					 *------------------------------------------------------*/
	public		MiMargins	getInsetMargins()
		{
		if (insetMargins == null)
			return(new MiMargins(0));
		return(new MiMargins(insetMargins));
		}
	public		MiMargins	getInsetMargins(MiMargins m)
		{
		if (insetMargins == null)
			m.setMargins(0);
		else
			m.copy(insetMargins);
		return(m);
		}
					/**------------------------------------------------------
	 				 * Sets the horizontal justification of the elements with
					 * respect to the target as a whole. Valid values are:
					 *   Mi_CENTER_JUSTIFIED
					 *   Mi_LEFT_JUSTIFIED
					 *   Mi_RIGHT_JUSTIFIED
					 *   Mi_JUSTIFIED
	 				 * @param j		the justification 
					 *------------------------------------------------------*/
	public		void		setElementHJustification(int j)
		{
		elementHJustification = j;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the horizontal justification of the elements with
					 * respect to the target as a whole. 
	 				 * @return 		the justification 
					 *------------------------------------------------------*/
	public		int		getElementHJustification()
		{
		return(elementHJustification);
		}
					/**------------------------------------------------------
	 				 * Sets the vertical justification of the elements with
					 * respect to the target as a whole. Valid values are:
					 *   Mi_CENTER_JUSTIFIED
					 *   Mi_BOTTOM_JUSTIFIED
					 *   Mi_TOP_JUSTIFIED
					 *   Mi_JUSTIFIED
	 				 * @return 		the justification 
					 *------------------------------------------------------*/
	public		void		setElementVJustification(int j)
		{
		elementVJustification = j;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the vertical justification of the elements with
					 * respect to the target as a whole. Valid values are:
	 				 * @return 		the justification 
					 *------------------------------------------------------*/
	public		int		getElementVJustification()
		{
		return(elementVJustification);
		}
					/**------------------------------------------------------
	 				 * Sets the horizontal and vertical justification of the 
					 * elements with respect to the target as a whole. Valid 
					 * values are:
					 *   Mi_CENTER_JUSTIFIED
					 *   Mi_JUSTIFIED
	 				 * @param j 		the justification 
					 *------------------------------------------------------*/
	public		void		setElementJustification(int j)
		{
		elementVJustification = j; 
		elementHJustification = j; 
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Sets the horizontal sizing of the elements with respect 
					 * to the other elements and the empty space between them.
					 * Valid values are:
					 *   Mi_SAME_SIZE
					 *   Mi_EXPAND_TO_FILL
	 				 * @param sizing 	the sizing 
					 *------------------------------------------------------*/
	public		void		setElementHSizing(int sizing)
		{
		elementHSizing = sizing;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the horizontal sizing of the elements with respect 
					 * to the other elements and the empty space between them.
	 				 * @return  		the sizing 
					 *------------------------------------------------------*/
	public		int		getElementHSizing()
		{
		return(elementHSizing);
		}
					/**------------------------------------------------------
	 				 * Sets the vertical sizing of the elements with respect 
					 * to the other elements and the empty space between them.
					 * Valid values are:
					 *   Mi_SAME_SIZE
					 *   Mi_EXPAND_TO_FILL
	 				 * @param sizing 	the sizing 
					 *------------------------------------------------------*/
	public		void		setElementVSizing(int j)
		{
		elementVSizing = j;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the vertical sizing of the elements with respect 
					 * to the other elements and the empty space between them.
	 				 * @return  		the sizing 
					 *------------------------------------------------------*/
	public		int		getElementVSizing()
		{
		return(elementVSizing);
		}
					/**------------------------------------------------------
	 				 * Sets the horizontal and vertical sizing of the elements
					 * with respect to the other elements and the empty space
					 * between them. Valid values are:
					 *   Mi_SAME_SIZE
					 *   Mi_EXPAND_TO_FILL
	 				 * @param sizing 	the sizing 
					 *------------------------------------------------------*/
	public		void		setElementSizing(int sizing)
		{
		elementVSizing = sizing; 
		elementHSizing = sizing;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Sets the index of the element that potentially has a 
					 * different sizing and justfication than the other elements.
	 				 * @param index 	the index (-1 is the last element) 
	 				 * @see			#getUniqueElementIndex
	 				 * @see			#setLastElementJustification
	 				 * @see			#setUniqueElementSizing
					 *------------------------------------------------------*/
	public		void		setUniqueElementIndex(int index)
		{
		uniqueElementIndex = index;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the index of the element that potentially has a 
					 * different sizing and justfication than the other elements.
	 				 * @return 	 	the index 
	 				 * @see			#setUniqueElementIndex
					 *------------------------------------------------------*/
	public		int		getUniqueElementIndex()
		{
		return(uniqueElementIndex);
		}
					/**------------------------------------------------------
	 				 * Sets the implementation-specific sizing of the specific
					 * unique element with respect to the other elements and 
					 * the empty space between them. Valid values are:
					 *   Mi_SAME_SIZE
					 *   Mi_EXPAND_TO_FILL
	 				 * @param sizing 	the sizing 
	 				 * @see			#getUniqueElementSizing
	 				 * @see			#setLastElementJustification
	 				 * @see			#setUniqueElementIndex
					 *------------------------------------------------------*/
	public		void		setUniqueElementSizing(int sizing)
		{
		uniqueElementSizing = sizing;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the implementation-specific sizing of the specific
					 * unique element with respect to the other elements and 
					 * the empty space between them. 
	 				 * @return 	 	the sizing 
	 				 * @see			#setUniqueElementSizing
					 *------------------------------------------------------*/
	public		int		getUniqueElementSizing()
		{
		return(uniqueElementSizing);
		}
					/**------------------------------------------------------
	 				 * Sets the implementation-specific justfication of the 
					 * specific unique element with respect to the other 
					 * elements and the empty space between them. Valid values 
					 * are implementation specific but usually are one of:
					 *   Mi_CENTER_JUSTIFIED
					 *   Mi_LEFT_JUSTIFIED
					 *   Mi_RIGHT_JUSTIFIED
					 *   Mi_BOTTOM_JUSTIFIED
					 *   Mi_TOP_JUSTIFIED
					 *   Mi_JUSTIFIED
	 				 * @param j 		the justification
	 				 * @see			#getLastElementJustification
	 				 * @see			#setUniqueElementIndex
	 				 * @see			#setUniqueElementSizing
					 *------------------------------------------------------*/
	public		void		setLastElementJustification(int j)
		{
		lastElementJustification = j;
		invalidateTargetLayout();
		}
					/**------------------------------------------------------
	 				 * Gets the implementation-specific justfication of the 
					 * specific unique element with respect to the other 
					 * elements and the empty space between them.
	 				 * @return 	 	the justification 
	 				 * @see			#setLastElementJustification
					 *------------------------------------------------------*/
	public		int		getLastElementJustification()
		{
		return(lastElementJustification);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_ALLEY_SPACING_NAME))
			setAlleySpacing(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_ALLEY_H_SPACING_NAME))
			setAlleyHSpacing(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_ALLEY_V_SPACING_NAME))
			setAlleyVSpacing(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_INSET_MARGINS_NAME))
			setInsetMargins(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_MARGINS_NAME))
			setMargins(new MiMargins(Utility.toDouble(value)));
		else if (name.equalsIgnoreCase(Mi_HORIZONTAL_JUSTIFICATION_NAME))
			setElementHJustification(MiSystem.getValueOfAttributeValueName(value));
		else if (name.equalsIgnoreCase(Mi_VERTICAL_JUSTIFICATION_NAME))
			setElementVJustification(MiSystem.getValueOfAttributeValueName(value));
		else if (name.equalsIgnoreCase(Mi_HORIZONTAL_SIZING_NAME))
			setElementHSizing(MiSystem.getValueOfAttributeValueName(value));
		else if (name.equalsIgnoreCase(Mi_VERTICAL_SIZING_NAME))
			setElementVSizing(MiSystem.getValueOfAttributeValueName(value));
		else if ((this instanceof MiiOrientablePart) && (name.equalsIgnoreCase(Mi_ORIENTATION_NAME)))
			{
			if (value.equalsIgnoreCase(Mi_HORIZONTAL_NAME))
				((MiiOrientablePart )this).setOrientation(MiiTypes.Mi_HORIZONTAL);
			if (value.equalsIgnoreCase(Mi_VERTICAL_NAME))
				((MiiOrientablePart )this).setOrientation(MiiTypes.Mi_VERTICAL);
			}
		else
			super.setPropertyValue(name, value);
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
		if (name.equalsIgnoreCase(Mi_ALLEY_SPACING_NAME))
			return("" + getAlleyHSpacing());
		else if (name.equalsIgnoreCase(Mi_ALLEY_H_SPACING_NAME))
			return("" + getAlleyHSpacing());
		else if (name.equalsIgnoreCase(Mi_ALLEY_V_SPACING_NAME))
			return("" + getAlleyVSpacing());
		else if (name.equalsIgnoreCase(Mi_INSET_MARGINS_NAME))
			return(insetMargins == null ? "0" : "" + insetMargins.getLeft());
		else if (name.equalsIgnoreCase(Mi_MARGINS_NAME))
			return("" + getMargins(new MiMargins()).getLeft());
		else if (name.equalsIgnoreCase(Mi_HORIZONTAL_JUSTIFICATION_NAME))
			return(MiSystem.getNameOfAttributeValue(
				horizontalJustificationNames, getElementHJustification()));
		else if (name.equalsIgnoreCase(Mi_VERTICAL_JUSTIFICATION_NAME))
			return(MiSystem.getNameOfAttributeValue(
				verticalJustificationNames, getElementVJustification()));
		else if (name.equalsIgnoreCase(Mi_HORIZONTAL_SIZING_NAME))
			return(MiSystem.getNameOfAttributeValue(
				elementSizingNames, getElementHSizing()));
		else if (name.equalsIgnoreCase(Mi_VERTICAL_SIZING_NAME))
			return(MiSystem.getNameOfAttributeValue(
				elementSizingNames, getElementVSizing()));
		else if ((this instanceof MiiOrientablePart) && (name.equalsIgnoreCase(Mi_ORIENTATION_NAME)))
			{
			if (((MiiOrientablePart )this).getOrientation() == MiiTypes.Mi_HORIZONTAL)
				return(Mi_HORIZONTAL_NAME);
			if (((MiiOrientablePart )this).getOrientation() == MiiTypes.Mi_VERTICAL)
				return(Mi_VERTICAL_NAME);
			}
		return(super.getPropertyValue(name));
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = new MiPropertyDescriptions("MiLayout");

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ALLEY_SPACING_NAME, Mi_POSITIVE_DOUBLE_TYPE, "2"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ALLEY_H_SPACING_NAME, Mi_POSITIVE_DOUBLE_TYPE, "2"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ALLEY_V_SPACING_NAME, Mi_POSITIVE_DOUBLE_TYPE, "2"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_INSET_MARGINS_NAME, Mi_POSITIVE_DOUBLE_TYPE, "0"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_MARGINS_NAME, Mi_POSITIVE_DOUBLE_TYPE, "0"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_HORIZONTAL_JUSTIFICATION_NAME, 
				new Strings(horizontalJustificationNames), Mi_CENTER_JUSTIFIED_NAME));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_VERTICAL_JUSTIFICATION_NAME,
				new Strings(verticalJustificationNames), Mi_CENTER_JUSTIFIED_NAME));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_HORIZONTAL_SIZING_NAME,
				new Strings(elementSizingNames), Mi_SAME_SIZE_NAME));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_VERTICAL_SIZING_NAME,
				new Strings(elementSizingNames), Mi_SAME_SIZE_NAME));

		propertyDescriptions = new MiPropertyDescriptions("MiLayout", propertyDescriptions);
		propertyDescriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());

		return(propertyDescriptions);
		}
					/**------------------------------------------------------
	 				 * Makes and returns a copy of this class.
					 * @return		the copy
					 * @implements		MiiCopyable#makeCopy
					 *------------------------------------------------------*/
	public		Object		makeCopy()
		{
		return(deepCopy());
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @param source	the part to copy
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiLayout obj 		= (MiLayout )source;

		elementHJustification 	= obj.elementHJustification;
		elementVJustification	= obj.elementVJustification;

		elementHSizing 		= obj.elementHSizing;
		elementVSizing 		= obj.elementVSizing;

		uniqueElementIndex	= obj.uniqueElementIndex;
		uniqueElementSizing	= obj.uniqueElementSizing;
		lastElementJustification= obj.lastElementJustification;

		if (obj.cellMargins == null)
			cellMargins	= null;
		else
			cellMargins	= new MiMargins(obj.cellMargins);

		if (obj.insetMargins == null)
			insetMargins	= null;
		else
			insetMargins	= new MiMargins(obj.insetMargins);

		alleyHSpacing		= obj.alleyHSpacing;
		alleyVSpacing		= obj.alleyVSpacing;
		}
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
		return(true);
		}
					/**------------------------------------------------------
		 			 * Gets whether this layout is not dependent on the position
					 * of the target. Most layouts return true. Layouts that
					 * support relative contraints (e.g. MiPolyConstraint)
					 * return false because of their dependance on their target 
					 * relative to another.
					 * @return 		true if not dependent
					 * @implements 		MiiLayout#isIndependantOfTargetPosition
					 *------------------------------------------------------*/
	public		boolean		isIndependantOfTargetPosition()
		{
		return(true);
		}
					/**------------------------------------------------------
	 			 	 * Lays out (arranges and resizes) the parts of the target.
					 * @implements 		MiiLayout#layoutParts
					 * @overrides 		MiPart#layoutParts
					 *------------------------------------------------------*/
	public		void		layoutParts()
		{
		// ---------------------------------------------------------------
		// For speed optimization, if not polyLayout and has no parts and
		// is not a layout of a layout which has parts.... return
		// ---------------------------------------------------------------
		if ((determinesPreferredAndMinimumSizes())
			&& (!(this instanceof MiPolyLayout))
			&& ((target.getNumberOfParts() == 0)
			&& ((!(target instanceof MiLayout)) 
			|| (((MiLayout )target).getTarget().getNumberOfParts() == 0))))
			{
			}
		else if (enabled)
			{
			// ---------------------------------------------------------------
			// Call the subclass layout routine to actually size and place the
			// parts of the target.
			// ---------------------------------------------------------------
			try	{
				doLayout();
				}
			catch (Exception e)
				{
				MiDebug.printStackTrace(e);
				}
			}

		// ---------------------------------------------------------------
		// Allow cascading layouts... i.e. this layout can have a layout...
		// ---------------------------------------------------------------
		if (getLayout() != null)
			{
			getLayout().invalidateLayout();
			getLayout().layoutParts();
			}
		}
					/**------------------------------------------------------
					 * Realculates the outer bounds of this container. 
					 * @param bounds 	the (returned) outer bounds
					 *------------------------------------------------------*/
	protected 	void		reCalcBounds(MiBounds bounds)
		{
		super.reCalcBounds(bounds);
		if (!bounds.isReversed())
			{
			if (insetMargins != null)
				bounds.addMargins(insetMargins);
			bounds.addMargins(getMargins(new MiMargins()));
			}
		}
					/**------------------------------------------------------
	 				 * Invalidates this layout by invalidating the layout of
					 * this layout's target. This is called when a parameter
					 * of the layout has been changed (for example the sizing).
					 *------------------------------------------------------*/
	protected	void		invalidateTargetLayout()
		{
		if (target != null)
			{
			target.invalidateLayout();
/*
			if (target.hasValidLayout())
				target.invalidateLayout();
			else
				target.resetPreferredSizes();
*/
			}
		}
					/**------------------------------------------------------
	 				 * Scales the parts that this container has. This method
					 * does nothing, as parts will be sized by this layout.
					 * @param center	the center of the scaling
					 * @param scale		the scale factors
					 * @overrides		MiContainer#scaleParts
					 *------------------------------------------------------*/
	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		// Parts will be placed by this layout
		}
					/**------------------------------------------------------
	 				 * This method is to be overridden to implement the actual
					 * layout algorithm.
					 *------------------------------------------------------*/
	protected abstract void		doLayout();

	public		String		toString()
		{
		return(super.toString() + (enabled ? "" : "NOT ENABLED"));
		}
	}

