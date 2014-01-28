
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
 * @action Mi_VALUE_CHANGED_ACTION
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiSlider extends MiAdjuster
	{
	public static final	String	Mi_SLIDER_PROTOTYPE_CLASS_NAME = "Mi_SLIDER_PROTOTYPE_CLASS_NAME";

	public static final	String	Mi_THUMB_NAME	= "thumb";
	public static 		MiDistance MINIMUM_THUMB_LENGTH	= 20;

	private static	MiSlider	prototype;
	private		MiDistance	slotWidth	= 14;
	private		MiDistance	thumbWidth	= 10;
	private		MiPart 		verticalThumb;
	private		MiPart 		horizontalThumb;
	protected	MiPart 		thumb;
	private		boolean		automaticLengthOfThumbAdjustmentDisabled;
	protected	double	 	normalizedThumbLength 	= 0.10; // NormalizedSize
	protected	MiDistance	margin		= 0;
	private		int		orientation	= Mi_HORIZONTAL;



					/**------------------------------------------------------
	 				 * Constructs a new MiSlider with a horizontal orientation. 
					 *------------------------------------------------------*/
	public				MiSlider()
		{
		this(Mi_HORIZONTAL);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiSlider with the given orientation. 
	 				 * @param orientation 	either Mi_VERTICAL or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	public				MiSlider(int orientation)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiSlider");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}
		setVisibleContainerAutomaticLayoutEnabled(false);
		setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(this, 
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);

		if (isThisClass)
			{
			MiContainer pointer = new MiContainer();
			MiPolygon body = new MiPolygon();
			body.appendPoint(0,0);
			body.appendPoint(1,0);
			body.appendPoint(1,1);
			body.appendPoint(.5,2);
			body.appendPoint(0,1);
			body.setBorderLook(Mi_RAISED_BORDER_LOOK);
			pointer.appendPart(body);
			MiLine etch = new MiLine();
			etch.setPoint(0, 0.5, 1);
			etch.setPoint(1, 0.5, 1.75);
			//etch.setBorderLook(Mi_RAISED_BORDER_LOOK);
			pointer.appendPart(etch);
			pointer.setWidth(12);
			horizontalThumb = pointer;

			pointer = new MiContainer();
			body = new MiPolygon();
			body.appendPoint(0,0);
			body.appendPoint(0,1);
			body.appendPoint(1,1);
			body.appendPoint(2,.5);
			body.appendPoint(1,0);
			body.setBorderLook(Mi_RAISED_BORDER_LOOK);
			pointer.appendPart(body);
			etch = new MiLine();
			etch.setPoint(0, 1, 0.5);
			etch.setPoint(1, 1.75, 0.5);
			//etch.setBorderLook(Mi_RAISED_BORDER_LOOK);
			pointer.appendPart(etch);
			pointer.setHeight(12);
			verticalThumb = pointer;

			setFixedHeight(true);

			automaticLengthOfThumbAdjustmentDisabled = true;
/***
			MiPolygon pointer = new MiPolygon();
			pointer.appendPoint(0,0);
			pointer.appendPoint(1,0);
			pointer.appendPoint(1,1);
			pointer.appendPoint(.5,1.5);
			pointer.appendPoint(0,1);
**/
			getToolkit().setStandardObjectAttributes(pointer);
			pointer.setBorderLook(Mi_RAISED_BORDER_LOOK);
			setThumb(pointer);
			setInsetMargins(new MiMargins(2, 0, 2, 0));
			}
		else
			{
			MiRectangle rect = new MiRectangle();
			getToolkit().setStandardObjectAttributes(rect);
			rect.setBorderLook(Mi_RAISED_BORDER_LOOK);
			setThumb(rect);
			}

		setAcceptingKeyboardFocus(true);
		setAcceptingMouseFocus(true);
		setDisplaysFocusBorder(true);
		appendEventHandler(new MiAdjusterEventHandler());
		appendEventHandler(new MiIDragger());

		setOrientation(orientation);

		if (isThisClass)
			{
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			}
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
	 				 * Sets the thumb to be the given part. Any previous thumb
					 * is removed. The new thumb is assigned the event 
					 * handlers this widget requires.
	 				 * @param newThumb 	the new thumb graphics
					 *------------------------------------------------------*/
	public		void		setThumb(MiPart newThumb)
		{
		if (thumb != null)
			{
			removeAttachment(thumb);
			}
		thumb = newThumb;
		if (thumb != null)
			{
			MiIDragger dragger = new MiIDragger();
			thumb.appendEventHandler(dragger);
			dragger.setTarget(this);
			appendAttachment(thumb);
			thumb.setName(Mi_THUMB_NAME);
			getAttachments().setOutgoingInvalidLayoutNotificationsEnabled(false);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the thumb.
	 				 * @return 	 	the current thumb graphics
					 *------------------------------------------------------*/
	public		MiPart		getThumb()
		{
		return(thumb);
		}
	public		void		setSlotWidth(MiDistance width)
		{
		slotWidth = width;
		invalidateLayout();
		}
	public		MiDistance	getSlotWidth()
		{
		return(slotWidth);
		}
	public		void		setThumbWidth(MiDistance width)
		{
		thumbWidth = width;
		invalidateLayout();
		}
	public		MiDistance	getThumbWidth()
		{
		return(thumbWidth);
		}
					/**------------------------------------------------------
	 				 * Sets the orientation of this widget.
	 				 * @param orient	either Mi_VERTICAL Or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	public		void		setOrientation(int orient)
		{
		orientation = orient;
		impSetOrientation(orient);
		setOrientationOfGeneratedMessages(orient);
		setSizeBasedOnOrientation();
		setNormalizedLengthOfThumb(normalizedThumbLength);
		}
					/**------------------------------------------------------
	 				 * Gets the orientation of this widget.
	 				 * @return 		either Mi_VERTICAL Or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	public		int		getOrientation()
		{
		return(orientation);
		}
					/**------------------------------------------------------
	 				 * Sets the value of this slider widget. The value should
					 * be between 0.0 and 1.0 inclusive. This automatically
					 * updates the position of the thumb.
					 * @param value		the normalized value
					 * @see 		MiAdjuster#setCurrentValue
					 * @overrides 		MiAdjuster.setNormalizedValue
					 *------------------------------------------------------*/
	public		void		setNormalizedValue(double value)
		{
		if (value > 1.0)
			value = 1.0;
		else if (value < 0.0)
			value = 0.0;

		if (value != getNormalizedValue())
			{
			super.setNormalizedValue(value);
			setNormalizedPositionOfThumb(value);
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		}
					/**------------------------------------------------------
					 * Moves the position of the associated 'thumb' of this
					 * widget. This is used when the user clicks on an
					 * interactive implementation of this interface.
					 * @param point		the point in which direction the 
					 *			thumb is to be positioned.
					 * @implements MiiAdjuster.moveOneChunkTowardsLocation
					 *------------------------------------------------------*/
	public	void			moveOneChunkTowardsLocation(MiPoint point)
		{
		double value = getNormalizedValueAtLocation(point);
		moveOneChunkTowardsValue(value);
		}

	protected	double		getNormalizedValueAtLocation(MiPoint point)
		{
		MiBounds bounds = getInnerBounds();
		double value;
		if (orientation == Mi_VERTICAL)
			{
			value = (point.y - bounds.getYmin() - thumb.getHeight()/2 - margin)
				/(getThumbRunningArea() - thumb.getHeight() - 2*margin);
			}
		else
			{
			value = (point.x - bounds.getXmin() - thumb.getWidth()/2 - margin)
				/(getThumbRunningArea() - thumb.getWidth() - 2*margin);
			}
		if (value > 1.0)
			value = 1.0;
		else if (value < 0.0)
			value = 0.0;
		return(value);
		}

					/**------------------------------------------------------
					 * Sets the position of the associated 'thumb' of this
					 * widget. This is used when the user clicks on an
					 * interactive implementation of this interface.
					 * @param point		the point at which the thumb is
					 *			positioned.
					 * @implements MiiAdjuster.setValueFromLocation
					 *------------------------------------------------------*/
	public	void			setValueFromLocation(MiPoint point)
		{
		double value = getNormalizedValueAtLocation(point);
		setNormalizedValue(value);
		}
					/**------------------------------------------------------
	 				 * Sets the position of the associated 'thumb' of this
					 * widget. This is used when the user drags on an
					 * interactive implementation of this interface.
	 				 * @param delta		the distance the user has moved
					 *			the thumb.
					 * @implements MiiAdjuster.adjustValueFromVector
					 * @overrides MiAdjuster.adjustValueFromVector
					 *------------------------------------------------------*/
	public		void		adjustValueFromVector(MiVector delta)
		{
		double normalizedValue = getNormalizedValue();
		double value = normalizedValue;
		MiDistance thumbRunningArea = getThumbRunningArea();
		if (orientation == Mi_VERTICAL)
			{
			if (thumbRunningArea > thumb.getHeight())
				value = normalizedValue + delta.y/(thumbRunningArea - thumb.getHeight());
			}
		else
			{
			if (thumbRunningArea > thumb.getWidth())
				value = normalizedValue + delta.x/(thumbRunningArea - thumb.getWidth());
			}
		if (value > 1.0)
			value = 1.0;
		else if (value < 0.0)
			value = 0.0;

		setNormalizedValue(value);
		}
					/**------------------------------------------------------
	 				 * Sets the normalized (between 0.0 and 1.0 inclusive)
					 * length of the associated 'thumb' of this widget (a 
					 * value of 0.0 sets the thumb to a length of 0 and a
					 * value of 1.0 sets the thumb to length of the entire
					 * slider. This is used when the when the thumb size
					 * itself represents something (for example the size of
					 * the viewable data relative to the size of all of the
					 * data).
	 				 * @param size		the normalized size of the thumb.
					 *			Set to < 0.0 if the size of the
					 *			thumb length is not to be dependant
					 *			on the length of this widget.
					 *------------------------------------------------------*/
	public		void		setNormalizedLengthOfThumb(double size)
		{
		if (automaticLengthOfThumbAdjustmentDisabled)
			return;

		if (size < 0.0)
			size = 0.0;
		else if (size > 1.0)
			size = 1.0;
		normalizedThumbLength = size;
		MiDistance length = getThumbRunningArea() * normalizedThumbLength;
		if (length < MINIMUM_THUMB_LENGTH)
			{
			length = MINIMUM_THUMB_LENGTH;
			}
		if (orientation == Mi_VERTICAL)
			thumb.setHeight(length);
		else
			thumb.setWidth(length);

		setChunkIncrement(size);

		double normalizedValue = getNormalizedValue();
		setNormalizedPositionOfThumb(normalizedValue);
		}
	public		double		getNormalizedLengthOfThumb()
		{
		return(normalizedThumbLength);
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

		if (normalizedValue != getNormalizedValue())
			{
			super.setNormalizedValue(normalizedValue);
			return;
			}
		MiBounds bounds = getInnerBounds();

		if (orientation == Mi_VERTICAL)
			{
			MiCoord cy = bounds.ymin + thumb.getHeight()/2 + margin
				+ normalizedValue * (getThumbRunningArea() - thumb.getHeight() - 2*margin);
			thumb.setCenterY(cy);
			}
		else
			{
			MiCoord cx = bounds.xmin + thumb.getWidth()/2 + margin
				+ normalizedValue * (getThumbRunningArea() - thumb.getWidth() - 2*margin);
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
	public static	void		setPrototype(MiSlider p)
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
	public		void		refreshLookAndFeel()
		{
		MiToolkit.setBackgroundAttributes(this, 
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);

		super.refreshLookAndFeel();
		getToolkit().setStandardObjectAttributes(thumb);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_SLIDER_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_SLIDER_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiSlider )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
					/**------------------------------------------------------
	 				 * This method implements the setOrientation method.
	 				 * @param orient 	either Mi_VERTICAL or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	protected	void		impSetOrientation(int orient)
		{
		MiPolyConstraint layout = new MiPolyConstraint();
		setLayout(layout);
		if (orient == Mi_VERTICAL)
			{
			if (verticalThumb != horizontalThumb)
				setThumb(verticalThumb);
			layout.appendConstraint(new MiRelativeLocationConstraint(
				thumb, MiRelativeLocationConstraint.SAME_COLUMN_AS, this));
			}
		else
			{
			if (verticalThumb != horizontalThumb)
				setThumb(horizontalThumb);
			layout.appendConstraint(new MiRelativeLocationConstraint(
				thumb, MiRelativeLocationConstraint.SAME_ROW_AS, this));
			}
			
		}
					/**------------------------------------------------------
	 				 * This method sets the thumb size based on this widgets
					 * orientation.
					 *------------------------------------------------------*/
	protected	void		setSizeBasedOnOrientation()
		{
		MiMargins baseSize = getTotalMargins();
		if (orientation == Mi_VERTICAL)
			{
			thumb.setWidth(thumbWidth);
			setWidth(baseSize.getWidth() + slotWidth);
			}
		else
			{
			thumb.setHeight(thumbWidth);
			setHeight(baseSize.getHeight() + slotWidth);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this widget.
					 * @param size		the returned minimum size.
					 * @orverrides MiPart.calcMinimumSize
					 *------------------------------------------------------*/
	protected	void		calcMinimumSize(MiSize size)
		{
		MiMargins baseSize = getTotalMargins();
		if (orientation == Mi_VERTICAL)
			{
			size.setSize(
				baseSize.getWidth() + slotWidth,
				baseSize.getHeight() + slotWidth * 3);
			}
		else
			{
			size.setSize(
				baseSize.getWidth() + slotWidth * 3,
				baseSize.getHeight() + slotWidth);
			}
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
	 				 * Gets the maximum length of the thumb.
	 				 * @return 		the length of the area the thumb
					 *			resides in.
					 *------------------------------------------------------*/
	protected	MiDistance	getThumbRunningArea()
		{
		if (orientation == Mi_VERTICAL)
			return(getInnerBounds().getHeight());
		else
			return(getInnerBounds().getWidth());
		}
					/**------------------------------------------------------
	 				 * Performs the layout of this container.
	 				 * @overrides MiVisibleContainer.layoutParts
					 *------------------------------------------------------*/
	public		void		layoutParts()
		{
		super.layoutParts();
		setNormalizedLengthOfThumb(normalizedThumbLength);
		setNormalizedPositionOfThumb(getNormalizedValue());
		}
					/**------------------------------------------------------
	 				 * Handles the user's dragging the associated 'thumb'
					 * of this widget in order to change the value.
	 				 * @param event 	the user's event
					 * @return		the event is consumed
					 * @implements MiiDraggable.drag
					 * @overrides MiAdjuster.drag
					 *------------------------------------------------------*/
	public		int 		drag(MiEvent event)
		{
		setValueFromLocation(event.worldPt);
		return(Mi_CONSUME_EVENT);
		}
					/**------------------------------------------------------
	 				 * Sets the value of this widget. In this case this is
					 * the normalzied position of the thumb.
	 				 * @param value 	the value
					 * @overrides MiWidget.setValue
					 *------------------------------------------------------*/
	public		void		setValue(String value)
		{
		double val = Utility.toDouble(value);
		setNormalizedValue(val);
		}
					/**------------------------------------------------------
	 				 * Gets the value of this widget. In this case this is
					 * the normalized position of the thumb.
	 				 * @return 		the value
					 * @overrides MiWidget.getValue
					 *------------------------------------------------------*/
	public		String		getValue()
		{
		return("" + getNormalizedValue());
		}
	}

