

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
import com.swfm.mica.util.DoubleVector; 
import com.swfm.mica.util.Utility; 
import java.awt.Color;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiStripChart extends MiWidget
	{
	private		String		valueDisplayFormatString;
	private		MiPart		label;
	private		MiPart		valueDisplay;
	private		MiPart		ticks;
	private		double		value;
	private		MiDistance	tickSpacing			= 4;
	private		int		currentTickNumber		= 0;
	private		int		numTicks			= 100;
	private		int		minNumTicks			= 11;
	private		double		minimumValue			= 0.0;
	private		double		maximumValue			= 1.0;
	private		MiVector	scroll				= new MiVector();
	private		DoubleVector	ticksToDrawX			= new DoubleVector();
	private		DoubleVector	ticksToDrawHeight		= new DoubleVector();
	private		MiBounds	tmpBounds			= new MiBounds();



	public				MiStripChart()
		{
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}

	protected	void		makeGraphics()
		{
		ticks = new MiLine(0,0,0,1);
		label = new MiText();
		appendPart(label);
		valueDisplay = new MiText();
		appendPart(valueDisplay);
		}
	public		void		setColor(Color c)
		{
		super.setColor(c);
		if (label != null)
			label.setColor(c);
		if (valueDisplay != null)
			valueDisplay.setColor(c);
		if (ticks != null)
			ticks.setColor(c);
		}

	public		void		setMinimumNumberOfTicks(int num)
		{
		minNumTicks = num;
		invalidateLayout();
		}
	public		int		getMinimumNumberOfTicks()
		{
		return(minNumTicks);
		}
	public		void		setNumberOfTicks(int num)
		{
		numTicks = num;
		invalidateLayout();
		}
	public		int		getNumberOfTicks()
		{
		return(numTicks);
		}

	public		void		setTicks(MiPart ticks)
		{
		this.ticks = ticks;
		invalidateLayout();
		}
	public		MiPart		getTicks()
		{
		return(ticks);
		}
	public		void		setTickSpacing(MiDistance spacing)
		{
		this.tickSpacing = spacing;
		invalidateLayout();
		}
	public		MiDistance	getTickSpaincg()
		{
		return(tickSpacing);
		}
	public		void		setValueDisplayFormatString(String format)
		{
		valueDisplayFormatString = format;
		invalidateArea();
		}
	public		void		setValueDisplay(String value)
		{
		if (valueDisplay != null)
			{
			if (valueDisplayFormatString != null)
				value = Utility.sprintf(valueDisplayFormatString, value);
			if (valueDisplay instanceof MiWidget)
				((MiWidget )valueDisplay).setValue(value);
			else if (valueDisplay instanceof MiText)
				((MiText )valueDisplay).setText(value);
			else
				valueDisplay.replaceSelf(new MiText(value));
			}
		}
	public		void		setValueDisplay(MiPart part)
		{
		valueDisplay = part;
		}
	public		MiPart		getValueDisplay()
		{
		return(valueDisplay);
		}
					/**------------------------------------------------------
	 				 * Sets the value of this widget. This causes a new 'tick'
					 * to be displayed and the previous ticks to be shifted
					 * over.
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
					/**------------------------------------------------------
	 				 * Gets the label of this widget.
	 				 * @return 		the label
					 *------------------------------------------------------*/
	public		MiPart		getLabel()
		{
		return(label);
		}
					/**------------------------------------------------------
	 				 * Sets the label of this widget.
	 				 * @param text 		the new label
					 *------------------------------------------------------*/
	public		void		setLabel(String text)
		{
		if (label != null)
			{
			if (label instanceof MiWidget)
				((MiWidget )label).setValue(text);
			else if (label instanceof MiText)
				((MiText )label).setText(text);
			else
				label.replaceSelf(new MiText(text));
			}
		else
			{
			label = new MiText(text);
			appendPart(label);
			}
		}
					/**------------------------------------------------------
	 				 * Sets the label of this widget.
	 				 * @param obj 		the new label
					 *------------------------------------------------------*/
	public		void		setLabel(MiPart obj)
		{
		if (label == obj)
			return;

		if (obj != null)
			obj.setCenter(getCenter());

		if (label != null)
			removePart(label);
		label = obj;
		if (label != null)
			appendPart(label);
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

		this.value = value;
		addNormalizedTick(value);
		setValueDisplay("" + (minimumValue + value * (maximumValue - minimumValue)));
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	public		double		getNormalizedValue()
		{
		return(value);
		}

	//***************************************************************************************
	// Protected methods
	//***************************************************************************************

	protected	void		addNormalizedTick(double value)
		{
		MiBounds innerBounds = getInnerBounds(tmpBounds);
		if (currentTickNumber >= numTicks)
			{
			scroll.x -= ticks.getWidth() + tickSpacing;
			--currentTickNumber;
			}
		MiCoord x = innerBounds.xmin + currentTickNumber * (ticks.getWidth() + tickSpacing)
			+ ticks.getWidth()/2;
		MiDistance height = innerBounds.getHeight() * value;
		ticksToDrawX.addElement(x);
		ticksToDrawHeight.addElement(height);
		++currentTickNumber;
		}

					/**------------------------------------------------------
	 				 * Renders the box and it's label. Positions label at 
					 * desired location.
	 				 * @param renderer  	the renderer
					 *------------------------------------------------------*/
	protected	void		render(MiRenderer renderer)
		{
		// Draw background, valueDisplay and label
		super.render(renderer);

		MiBounds innerBounds = getInnerBounds(tmpBounds);

		// Move Strip over
		renderer.moveImageArea(innerBounds, scroll.x, scroll.y);
		scroll.zeroOut();

		// Draw ticks:
		for (int i = 0; i < ticksToDrawX.size(); ++i)
			{
			ticks.setCenterX(ticksToDrawX.elementAt(i));
			ticks.setYmin(innerBounds.ymin);
			ticks.setYmax(innerBounds.ymin + ticksToDrawHeight.elementAt(i));
			ticks.render(renderer);
			}
		ticksToDrawX.removeAllElements();
		ticksToDrawHeight.removeAllElements();
		}
	protected	void		doLayout()
		{
		super.doLayout();

		MiBounds innerBounds = getInnerBounds(tmpBounds);

		label.setCenter(
			innerBounds.getXmin() + label.getWidth()/2,
			innerBounds.getYmax() - label.getHeight()/2);
		valueDisplay.setCenter(
			innerBounds.getXmax() - label.getWidth()/2, 
			innerBounds.getYmax() - label.getHeight()/2);
		}

					/**------------------------------------------------------
	 				 * Calulates the minimum size of this part.
	 				 * @param size  	the (returned) minimum size
					 *------------------------------------------------------*/
	protected 	void		calcMinimumSize(MiSize size)
		{
		super.calcMinimumSize(size);

		MiDistance labelWidths = 0;
		MiDistance tickWidth = minNumTicks * ticks.getWidth() + (minNumTicks - 1) * tickSpacing;

		if (label != null)
			labelWidths = label.getWidth();
		if (valueDisplay != null)
			labelWidths += valueDisplay.getWidth();
		if (tickWidth < labelWidths)
			size.width += labelWidths;
		else
			size.width += tickWidth;

		size.height += ticks.getHeight();
		}
					/**------------------------------------------------------
	 				 * Calulates the preferred size of this part.
	 				 * @param size  	the (returned) preferred size
					 *------------------------------------------------------*/
	protected 	void		calcPreferredSize(MiSize size)
		{
		super.calcPreferredSize(size);

		MiDistance labelWidths = 0;
		MiDistance tickWidth = numTicks * ticks.getWidth() + (numTicks - 1) * tickSpacing;

		if (label != null)
			labelWidths = label.getWidth();
		if (valueDisplay != null)
			labelWidths += valueDisplay.getWidth();
		if (tickWidth < labelWidths)
			size.width += labelWidths;
		else
			size.width += tickWidth;

		size.height += ticks.getHeight();
		}
	}

