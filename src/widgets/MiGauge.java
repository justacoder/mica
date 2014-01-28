
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
 * A MiGauge is a vertical or horizontally oriented thermometer-like
 * widget that is usually used to display a percentage of some quantity.
 * <p>
 * The 'fluid' filling the gauge can either be solid or seperate 'LED'
 * like rectangles. The spacing between LEDs is controlled by the alley
 * spacing (see MiLayout#setAlleySpacing);
 * <p>
 * The size of the LED rectangles when there are multiple LEDs is determined 
 * by the length of this gauge, the number of LEDs, and the alley spacing.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGauge extends MiSlider
	{
	public static final	String	Mi_GAUGE_PROTOTYPE_CLASS_NAME = "Mi_GAUGE_PROTOTYPE_CLASS_NAME";
	private static	MiGauge		prototype;
	private		int		numberOfLEDs	= 1;
	private		MiPart		LED		= new MiRectangle();
	private		MiPart		label;
	private		boolean		isPercentage	= true;
	private		boolean		partialLEDsAreOK= true;
	private		MiBounds	tmpBounds	= new MiBounds();


					/**------------------------------------------------------
	 				 * Constructs a new MiGauge.
					 *------------------------------------------------------*/
	public				MiGauge()
		{
		this(Mi_HORIZONTAL);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiGauge with the given orientation.
					 * @param orientation	Mi_HORIZONTAL or Mi_VERTICAL
					 *------------------------------------------------------*/
	public				MiGauge(int orientation)
		{
		super(orientation);

		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiGauge");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}
			
		setVisibleContainerAutomaticLayoutEnabled(false);
		setBorderLook(Mi_INDENTED_BORDER_LOOK);

		LED.setBorderLook(Mi_RAISED_BORDER_LOOK);

		setCellMargins(1);
		appendEventHandler(new MiAdjusterEventHandler());
		appendEventHandler(new MiIDragger());

		setAlleySpacing(4);

		thumb.setVisible(false);
		setNormalizedLengthOfThumb(0.0);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
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
			return(new MiGauge());

		return((MiGauge )prototype.deepCopy());
		}
					/**------------------------------------------------------
	 				 * Sets the label. If the label is textual or a widget
					 * it's value is always automatically updated to display
					 * the current percentage value ,if isPercentage() is true,
					 * else the current value.
					 * @param orientation	Mi_HORIZONTAL or Mi_VERTICAL
					 * @see #setIsPercentage
					 * @see MiAdjuster#setMinimumValue
					 * @see MiAdjuster#setMaximumValue
					 *------------------------------------------------------*/
	public		void		setLabel(MiPart label)
		{
		if (this.label != null)
			{
			removeAttachment(this.label);
			}
		this.label = label;
		if (label != null)
			{
			appendAttachment(label, Mi_CENTER_LOCATION, null, null);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the label.
					 * @return 		the label.
					 *------------------------------------------------------*/
	public		MiPart		getLabel()
		{
		return(label);
		}
					/**------------------------------------------------------
	 				 * Sets the number of 'LEDs' along the length of this gauge.
					 * The default is equal to 1, which is a solid bar.
					 * @param number 	the number of LEDS (must be >= 1).
					 * @exception		IllegalArgumentException if number < 1
					 *------------------------------------------------------*/
	public		void		setNumberOfLEDs(int number)
		{
		if (number < 1)
			throw new IllegalArgumentException(this + ": Invalid number of LEDs: " + number);
			
		numberOfLEDs = number;
		}
					/**------------------------------------------------------
	 				 * Gets the number of 'LEDs' along the length of this gauge.
					 * The default is equal to 1, which is a solid bar.
					 * @return 	 	the number of LEDS
					 *------------------------------------------------------*/
	public		int		getNumberOfLEDs()
		{
		return(numberOfLEDs);
		}
					/**------------------------------------------------------
	 				 * Sets whether the label, if any, displays the percentage
					 * or current values.
					 * @param flag 		true if to display the percentage
					 *------------------------------------------------------*/
	public		void		setIsPercentage(boolean flag)
		{
		isPercentage = flag;
		updateLabel();
		}
					/**------------------------------------------------------
	 				 * Gets whether the label, if any, displays the percentage
					 * or current values.
					 * @return 		true if to display the percentage
					 *------------------------------------------------------*/
	public		boolean		isPercentage()
		{
		return(isPercentage);
		}

					/**------------------------------------------------------
	 				 * Gets the MiPart that is used to draw the 'LEDs' (i.e.
					 * the mercury filler).
					 * @return 		the LED
					 *------------------------------------------------------*/
	public		MiPart		getLED()
		{
		return(LED);
		}
					/**------------------------------------------------------
	 				 * Sets the MiPart that is used to draw the 'LEDs' (i.e.
					 * mercury filler).
					 * @param part		the LED
					 *------------------------------------------------------*/
	public		void		setLED(MiPart part)
		{
		LED = part;
		}
					/**------------------------------------------------------
	 				 * Sets whether partial LEDs can be drawn or just full size
					 * LEDs. This is for the case where there are multiple LEDs
					 * and the current normalized value is not a multiple of 
					 * 1/numberOfLEDs. This is not used unless the number of
					 * LEDs is greater than 1.
					 * @param flag 		true if partially drawn LEDs are OK
					 *------------------------------------------------------*/
	public		void		setPartialLEDsAreOK(boolean flag)
		{
		partialLEDsAreOK = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether partial LEDs can be drawn or just full size
					 * LEDs. 
					 * @return 		true if partially drawn LEDs are OK
					 *------------------------------------------------------*/
	public		boolean		getPartialLEDsAreOK()
		{
		return(partialLEDsAreOK);
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
	public static	void		setPrototype(MiGauge p)
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
					 * Mi_GAUGE_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_GAUGE_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiGauge )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
					/**------------------------------------------------------
	 				 * Sets the value of this MiGauge. Note: assure the value
					 * specified is not NaN or the LED will render incorrectly.
					 * @param value		the value (between 0.0 and 1.0)
					 * @overrides MiSlider.setNormalizedValue
					 *------------------------------------------------------*/
	public		void		setNormalizedValue(double value)
		{
		if (value != getNormalizedValue())
			{
			super.setNormalizedValue(value);
			updateLabel();
			invalidateArea();
			}
		}

					/**------------------------------------------------------
	 				 * Sets the attributes associated with the 'LEDs' i.e.
					 * mercury filler).
					 * @param renderer		the rendering context
					 * @overrides MiPart.render
					 *------------------------------------------------------*/
	protected	void		render(MiRenderer renderer)
		{
		super.render(renderer);
		int orientation = getOrientation();
		double normalizedValue = getNormalizedValue();

		MiBounds innerBounds = getInnerBounds(tmpBounds).subtractMargins(getCellMargins());
		MiBounds LEDBounds = new MiBounds(innerBounds);
		if (orientation == Mi_HORIZONTAL)
			{
			MiCoord currentValueX = LEDBounds.getXmin() + normalizedValue *innerBounds.getWidth();
			MiDistance minLEDSpacing = getAlleyHSpacing();
			if (numberOfLEDs == 1)
				{
				LEDBounds.setXmax(currentValueX);
				LED.setBounds(LEDBounds);
				LED.render(renderer);
				return;
				}
			LEDBounds.setXmax(LEDBounds.getXmin() + (innerBounds.getWidth() 
				- minLEDSpacing * numberOfLEDs)/numberOfLEDs);
			MiDistance LEDSpacing 
				= (innerBounds.getWidth() - numberOfLEDs * LEDBounds.getWidth())
				/(numberOfLEDs - 1);
			for (int i = 0; i < numberOfLEDs; ++i)
				{
				if (LEDBounds.xmax > currentValueX)
					{
					if (partialLEDsAreOK)
						LEDBounds.xmax = currentValueX;
					else if ((LEDBounds.xmax - currentValueX)/LEDBounds.getWidth() < 0.5)
						return;
					}
				LED.setBounds(LEDBounds);
				LED.render(renderer);
				LEDBounds.translate(LEDBounds.getWidth() + LEDSpacing, 0);
				if (LEDBounds.xmin > currentValueX)
					return;
				}
			}
		else
			{
			MiCoord currentValueY = LEDBounds.getYmin() + normalizedValue *innerBounds.getHeight();
			MiDistance minLEDSpacing = getAlleyVSpacing();
			if (numberOfLEDs == 1)
				{
				LEDBounds.setYmax(
					LEDBounds.getYmin() + normalizedValue *innerBounds.getHeight());
				LED.setBounds(LEDBounds);
				LED.render(renderer);
				return;
				}
			LEDBounds.setYmax(LEDBounds.getYmin() + (innerBounds.getHeight() 
				- minLEDSpacing * numberOfLEDs)/numberOfLEDs);
			MiDistance LEDSpacing 
				= (innerBounds.getHeight() - numberOfLEDs * LEDBounds.getHeight())
				/(numberOfLEDs - 1);
			for (int i = 0; i < numberOfLEDs; ++i)
				{
				if (LEDBounds.ymax > currentValueY)
					{
					if (partialLEDsAreOK)
						LEDBounds.ymax = currentValueY;
					else if ((LEDBounds.ymax - currentValueY)/LEDBounds.getHeight() < 0.5)
						return;
					}
				LED.setBounds(LEDBounds);
				LED.render(renderer);
				LEDBounds.translate(0, LEDBounds.getHeight() + LEDSpacing);
				if (LEDBounds.ymin > currentValueY)
					return;
				}
			}
		}
					/**------------------------------------------------------
	 				 * Handles the user 'dragging' on the gauge when changing
					 * the value.
					 * @param event			the drag event
					 * @return 			consumes the event
					 * @implements MiiDraggable
					 * @overrides MiAdjuster.drag
					 *------------------------------------------------------*/
	public		int 		drag(MiEvent event)
		{
		setValueFromLocation(event.worldPt);
		return(Mi_CONSUME_EVENT);
		}
					/**------------------------------------------------------
	 				 * Updates the label to display the current percentage if
					 * the label is text based. This value will be the 
					 * percentage, if 'isPercentage()', or the actual value.
					 *------------------------------------------------------*/
	protected	void		updateLabel()
		{
		if (label != null)
			{
			String value;
			if (isPercentage)
				value = (int )(getNormalizedValue() * 100) + "%";
			else
				value = getCurrentValue() + "";
				
			if (label instanceof MiText)
				((MiText )label).setText(value);
			else if (label instanceof MiWidget)
				((MiWidget )label).setValue(value);
			}
		}

	}

