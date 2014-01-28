
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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiScrollBar extends MiSlider implements MiiActionHandler
	{
	public static final	String		Mi_SCROLLBAR_PROTOTYPE_CLASS_NAME 
							= "Mi_SCROLLBAR_PROTOTYPE_CLASS_NAME";
	public static final	String		Mi_RIGHT_ARROW_NAME 	= "rightArrow";
	public static final	String		Mi_LEFT_ARROW_NAME	= "leftArrow";
	public static final	String		Mi_UP_ARROW_NAME	= "upArrow";
	public static final	String		Mi_DOWN_ARROW_NAME	= "downArrow";

	private static		MiScrollBar	prototype;
	private			MiPushButton	moreArrow;
	private			MiPushButton	lessArrow;



					/**------------------------------------------------------
	 				 * Constructs a new MiScrollBar with a horizontal 
					 * orientation. 
					 *------------------------------------------------------*/
	public				MiScrollBar()
		{
		this(Mi_HORIZONTAL);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiScrollBar with the given orientation. 
	 				 * @param orientation 	either Mi_VERTICAL or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	public				MiScrollBar(int orient)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiPushButton");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}

		setSlotWidth(14);
		setThumbWidth(10);
		moreArrow = new MiPushButton();
		lessArrow = new MiPushButton();
		//moreArrow.setName("moreArrow");
		//lessArrow.setName("lessArrow");
		initArrow(moreArrow);
		initArrow(lessArrow);
		insertPart(lessArrow, 0);
		appendPart(moreArrow);
		setInsetMargins(2);
		setOrientation(orient);
		margin = 2;

		//setIsOpaqueRectangle(true);
		//setDoubleBuffered(true);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}

	public		MiPushButton	getMoreArrow()
		{
		return(moreArrow);
		}
	public		MiPushButton	getLessArrow()
		{
		return(lessArrow);
		}
					/**------------------------------------------------------
	 				 * Creates a new widget from the prototype. This is the
					 * factory pattern implementation for this widget. If the
					 * prototype is null, then the default contructor is used.
					 * @return 		the new widget
					 * @see 		#setPrototype
					 *------------------------------------------------------*/
	public static	MiWidget	create()
		{
		if (prototype == null)
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}
					/**------------------------------------------------------
	 				 * This method sets the thumb size based on this widgets
					 * orientation.
					 *------------------------------------------------------*/
	protected	void		setSizeBasedOnOrientation()
		{
		super.setSizeBasedOnOrientation();

		// This is called by super class before we are ready...
		if (lessArrow == null)
			return;

		MiDistance thumbWidth = getThumbWidth();
		moreArrow.setWidth(thumbWidth);
		moreArrow.setHeight(thumbWidth);
		moreArrow.setPreferredSize(new MiSize(thumbWidth, thumbWidth));
		lessArrow.setWidth(thumbWidth);
		lessArrow.setHeight(thumbWidth);
		lessArrow.setPreferredSize(new MiSize(thumbWidth, thumbWidth));
		setNormalizedLengthOfThumb(getNormalizedLengthOfThumb());
		}
					/**------------------------------------------------------
	 				 * Gets the preferred size of this widget.
					 * @param size		the returned preferred size.
					 * @orverrides MiPart.calcPreferredSize
					 *------------------------------------------------------*/
	protected	void		calcPreferredSize(MiSize size)
		{
		calcMinimumSize(size);
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this widget.
					 * @param size		the returned minimum size.
					 * @orverrides MiPart.calcMinimumSize
					 *------------------------------------------------------*/
	protected	void		calcMinimumSize(MiSize size)
		{
		super.calcMinimumSize(size);
		if (getOrientation() == Mi_VERTICAL)
			{
			size.setSize(
				size.getWidth(),
				size.getHeight() + 2 * getThumbWidth());
			}
		else
			{
			size.setSize(
				size.getWidth() + 2 * getThumbWidth(),
				size.getHeight());
			}
		}
					/**------------------------------------------------------
	 				 * Sets the normalized (between 0.0 and 1.0 inclusive)
					 * position of the associated 'thumb' of this widget (a 
					 * value of 0.0 sets the thumb to the left side of a 
					 * horizontally oriented widget and the bottom of a
					 * vertically oriented widget. Similarly a value of 1.0
					 * sets the thumb to the right side of a horizontally 
					 * oriented widget and the top of a vertically oriented 
					 * widget.
	 				 * @param normalizedValue the normalized thumb position
					 *------------------------------------------------------*/
	public		void		setNormalizedPositionOfThumb(double normalizedValue)
		{
		if (normalizedValue < 0.0)
			normalizedValue = 0.0;
		else if (normalizedValue > 1.0)
			normalizedValue = 1.0;

// FIX: 4 * margin.... should be 2 and 8 * margin below should be 4 *
		if (normalizedValue != getNormalizedValue())
			{
			setNormalizedValueOnly(normalizedValue);
			return;
			}
		// This is called by super class before we are ready...
		if (lessArrow == null)
			return;

		MiBounds bounds = getBounds();
		if (getOrientation() == Mi_VERTICAL)
			{
			MiCoord cy = bounds.ymin + lessArrow.getHeight() 
				+ thumb.getHeight()/2 + 4 * margin
				+ normalizedValue * (getThumbRunningArea() - thumb.getHeight());
			thumb.setCenterY(cy);
			}
		else
			{
			MiCoord cx = bounds.xmin + lessArrow.getWidth() 
				+ thumb.getWidth()/2 + 4 * margin
				+ normalizedValue * (getThumbRunningArea() - thumb.getWidth());
			thumb.setCenterX(cx);
			}
		}
					/**------------------------------------------------------
	 				 * Sets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @param p 		the new prototype
					 * @see 		#getPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	void		setPrototype(MiScrollBar p)
		{
		prototype = p;
		}
					/**------------------------------------------------------
	 				 * Gets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @return  		the prototype
					 * @see 		#setPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	MiWidget	getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_PUSHBUTTON_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_SCROLLBAR_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiScrollBar )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
					/**------------------------------------------------------
	 				 * Process the given action.
					 * @param action		the action to process.
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == moreArrow)
			{
			increaseOneUnit();
			}
		else if (action.getActionSource() == lessArrow)
			{
			decreaseOneUnit();
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Handles the user's dragging the associated 'thumb'
					 * of this widget in order to change the value.
	 				 * @param event 	the user's event
					 * @return		the event is consumed
					 * @implements MiiDraggable.drag
					 * @overrides MiSlider.drag
					 *------------------------------------------------------*/
	public		int 		drag(MiEvent event)
		{
		adjustValueFromVector(event.worldVector);
		return(Mi_CONSUME_EVENT);
		}
					/**------------------------------------------------------
	 				 * This method implements the setOrientation method.
	 				 * @param orient 	either Mi_VERTICAL or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	protected	void		impSetOrientation(int orient)
		{
		// This is called by super class before we are ready...
		if (lessArrow == null)
			return;

		if (orient == Mi_VERTICAL)
			{
			moreArrow.setShape(MiVisibleContainer.TRIANGLE_POINTING_UP_SHAPE);
			lessArrow.setShape(MiVisibleContainer.TRIANGLE_POINTING_DOWN_SHAPE);
			moreArrow.setName(Mi_UP_ARROW_NAME);
			lessArrow.setName(Mi_DOWN_ARROW_NAME);
			MiPolyConstraint layout = new MiPolyConstraint();
			setLayout(layout);
			layout.appendConstraint(new MiRelativeLocationConstraint(
				lessArrow, MiRelativeLocationConstraint.INSIDE_BOTTOM_CENTER_OF, this, margin));
			layout.appendConstraint(new MiRelativeLocationConstraint(
				moreArrow, MiRelativeLocationConstraint.INSIDE_TOP_CENTER_OF, this, margin));

			layout.appendConstraint(new MiRelativeLocationConstraint(
				thumb, MiRelativeLocationConstraint.SAME_COLUMN_AS, this));
			}
		else
			{
			moreArrow.setShape(MiVisibleContainer.TRIANGLE_POINTING_RIGHT_SHAPE);
			lessArrow.setShape(MiVisibleContainer.TRIANGLE_POINTING_LEFT_SHAPE);
			moreArrow.setName(Mi_RIGHT_ARROW_NAME);
			lessArrow.setName(Mi_LEFT_ARROW_NAME);
			MiPolyConstraint layout = new MiPolyConstraint();
			setLayout(layout);
			layout.appendConstraint(new MiRelativeLocationConstraint(
				lessArrow, MiRelativeLocationConstraint.INSIDE_LEFT_CENTER_OF, this, margin));
			layout.appendConstraint(new MiRelativeLocationConstraint(
				moreArrow, MiRelativeLocationConstraint.INSIDE_RIGHT_CENTER_OF, this, margin));
			layout.appendConstraint(new MiRelativeLocationConstraint(
				thumb, MiRelativeLocationConstraint.SAME_ROW_AS, this));
			}
		}
					/**------------------------------------------------------
	 				 * Initializes the given arrow, assigning it a default
					 * shape, action handlers and state.
	 				 * @param pb 		the arrow pushbutton.
					 *------------------------------------------------------*/
	protected	void		initArrow(MiPushButton pb)
		{
		getToolkit().setStandardObjectAttributes(pb);
		pb.setShape(MiVisibleContainer.TRIANGLE_POINTING_UP_SHAPE);
		pb.setDisplaysFocusBorder(false);
		pb.setAcceptingMouseFocus(false);
		pb.setAcceptingKeyboardFocus(false);
		pb.appendActionHandler(this, Mi_SELECTED_ACTION);
		pb.appendActionHandler(this, Mi_SELECT_REPEATED_ACTION);
		pb.setRepeatSelectWhenHeld(true);
		}
	protected	double		getNormalizedValueAtLocation(MiPoint point)
		{
		MiBounds bounds = getInnerBounds();
		double value;
		if (getOrientation() == Mi_VERTICAL)
			{
			value = (point.y - bounds.ymin - lessArrow.getHeight() - thumb.getHeight()/2 - 4 * margin)
				/(getThumbRunningArea() - thumb.getHeight());
			}
		else
			{
			value = (point.x - bounds.xmin - lessArrow.getWidth() - thumb.getWidth()/2 - 4 * margin)
				/(getThumbRunningArea() - thumb.getWidth());
			}
		if (value > 1.0)
			value = 1.0;
		else if (value < 0.0)
			value = 0.0;
		return(value);
		}

					/**------------------------------------------------------
	 				 * Gets the maximum length of the thumb.
	 				 * @return 		the length of the area the thumb
					 *			resides in.
					 *------------------------------------------------------*/
	protected	MiDistance	getThumbRunningArea()
		{
		// This is called by super class before we are ready...
		if (lessArrow == null)
			return(0);

		MiDistance length;

		if (getOrientation() == Mi_VERTICAL)
			length = (getHeight() - moreArrow.getHeight() - lessArrow.getHeight() - 8 * margin);
		else
			length = (getWidth() - moreArrow.getWidth() - lessArrow.getWidth() - 8 * margin);
		return(length < 0 ? 0 : length);
		}

/* **************************************
Do these kludges to handle "upArrow" for horizontal scrollbars... OR make VerticalScrollBar
and HorizontalScrollBar classes
*****************************************/
					/**------------------------------------------------------
					 * Sets the property with the given name to the given
					 * value. If no such property is found then sets the attribute
					 * with the given name to the given value. Valid attribute 
					 * names are found in the MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (getOrientation() == Mi_VERTICAL)
			{
			if ((Utility.startsWithIgnoreCase(name, Mi_RIGHT_ARROW_NAME))
				|| (Utility.startsWithIgnoreCase(name, Mi_LEFT_ARROW_NAME)))
				{
				return;
				}
			}
		else if (getOrientation() == Mi_HORIZONTAL)
			{
			if ((Utility.startsWithIgnoreCase(name, Mi_UP_ARROW_NAME))
				|| (Utility.startsWithIgnoreCase(name, Mi_DOWN_ARROW_NAME)))
				{
				return;
				}
			}
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
		if (getOrientation() == Mi_VERTICAL)
			{
			if ((Utility.startsWithIgnoreCase(name, Mi_RIGHT_ARROW_NAME))
				|| (Utility.startsWithIgnoreCase(name, Mi_LEFT_ARROW_NAME)))
				{
				int index = name.indexOf('.');
				name = name.substring(index + 1);
				return(moreArrow.getPropertyValue(name));
				}
			}
		else if (getOrientation() == Mi_HORIZONTAL)
			{
			if ((Utility.startsWithIgnoreCase(name, Mi_UP_ARROW_NAME))
				|| (Utility.startsWithIgnoreCase(name, Mi_DOWN_ARROW_NAME)))
				{
				int index = name.indexOf('.');
				name = name.substring(index + 1);
				return(moreArrow.getPropertyValue(name));
				}
			}
		return(super.getPropertyValue(name));
		}
	}

