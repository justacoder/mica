
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
 * This abstract class serves as a base class for 'adjuster' widgets like
 * sliders, scrollbars, meters, etc.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiAdjuster extends MiWidget implements MiiAdjuster, MiiDraggable
	{
	private		double		minValue 			= 0.0;
	private		double		maxValue 			= 1.0;
	private		double		normalizedValue 		= 0.0;
	private		double		unitIncrement	 		= 0.05;
	private		double		chunkIncrement	 		= 0.1;
	private		double		pageIncrement	 		= 0.125;
	private		int		setValueEvent;
	private		int		incOneUnitEvent;
	private		int		decOneUnitEvent;
	private		int		incOneChunkEvent;
	private		int		decOneChunkEvent;
	private		int		incOnePageEvent;
	private		int		decOnePageEvent;
	private		int		incToMaxEvent;
	private		int		decToMinEvent;

	private		int		generatedEventOrientation	= Mi_VERTICAL;
	private		boolean		generateAbsolutePositionsOnly	= true;
	private		boolean		generateEvents			= true;
	private		MiPart		observer;
	private		MiiScrollableData scrollable;
	private		MiEvent		event				= new MiEvent();


					/**------------------------------------------------------
	 				 * Constructs a new MiAdjuster. 
					 *------------------------------------------------------*/
	public				MiAdjuster()
		{
		}

					/**------------------------------------------------------
	 				 * Sets the minimum value of this adjuster widget.
					 * @param value		the minimum value
					 * @see #setCurrentValue
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		setMinimumValue(double value)
		{
		minValue = value;
		}
					/**------------------------------------------------------
	 				 * Gets the minimum value of this adjuster widget.
					 * @return		the minimum value
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		double		getMinimumValue()
		{
		return(minValue);
		}
					/**------------------------------------------------------
	 				 * Sets the maximum value of this adjuster widget.
					 * @param value		the maximum value
					 * @see #setCurrentValue
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		setMaximumValue(double value)
		{
		maxValue = value;
		}
					/**------------------------------------------------------
	 				 * Gets the maximum value of this adjuster widget.
					 * @return		the maximum value
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		double		getMaximumValue()
		{
		return(maxValue);
		}
					/**------------------------------------------------------
	 				 * Sets the value of this adjuster widget. The value should
					 * be between the minimum and maximum values.
					 * @param value		the current value
					 * @see #setMinimumValue
					 * @see #setMaximumValue
					 * @see #getCurrentValue
					 * @see #setNormalizedValue
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		setCurrentValue(double value)
		{
		setNormalizedValue((value - minValue)/(maxValue - minValue));
		}
					/**------------------------------------------------------
	 				 * Gets the value of this adjuster widget. The value is
					 * be between the minimum and maximum values.
					 * @return		the current value
					 * @see #setCurrentValue
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		double		getCurrentValue()
		{
		return(minValue + (maxValue - minValue) * normalizedValue);
		}
					/**------------------------------------------------------
	 				 * Sets the value of this adjuster widget. The value should
					 * be between 0.0 and 1.0 inclusive.
					 * @param value		the normalized value
					 * @see #setCurrentValue
					 *------------------------------------------------------*/
	public		void		setNormalizedValue(double value)
		{
		setNormalizedValueOnly(value);
		}
					/**------------------------------------------------------
	 				 * Gets the value of this adjuster widget. The value is
					 * be between 0.0 and 1.0 inclusive.
					 * @return 		the normalized value
					 * @see #getCurrentValue
					 *------------------------------------------------------*/
	public		double		getNormalizedValue()
		{
		return(normalizedValue);
		}
					/**------------------------------------------------------
	 				 * Sets the normalized amount of the unit increment for 
					 * this widget.
					 * @param amount	the amount (between 0.0 and 1.0)
					 *------------------------------------------------------*/
	public		void		setUnitIncrement(double amount)
		{
		unitIncrement = amount;
		}
					/**------------------------------------------------------
	 				 * Gets the normalized amount of the unit increment for 
					 * this widget.
					 * @return 		the amount (between 0.0 and 1.0)
					 *------------------------------------------------------*/
	public		double		getUnitIncrement()
		{
		return(unitIncrement);
		}
					/**------------------------------------------------------
	 				 * Sets the normalized amount of the chunk increment for 
					 * this widget.
					 * @param amount	the amount (between 0.0 and 1.0)
					 *------------------------------------------------------*/
	public		void		setChunkIncrement(double amount)
		{
		chunkIncrement = amount;
		}
					/**------------------------------------------------------
	 				 * Gets the normalized amount of the chunk increment for 
					 * this widget.
					 * @return 		the amount (between 0.0 and 1.0)
					 *------------------------------------------------------*/
	public		double		getChunkIncrement()
		{
		return(chunkIncrement);
		}
					/**------------------------------------------------------
	 				 * Sets the normalized amount of the page increment for 
					 * this widget.
					 * @param amount	the amount (between 0.0 and 1.0)
					 *------------------------------------------------------*/
	public		void		setPageIncrement(double amount)
		{
		pageIncrement = amount;
		}
					/**------------------------------------------------------
	 				 * Gets the normalized amount of the page increment for 
					 * this widget.
					 * @return 		the amount (between 0.0 and 1.0)
					 *------------------------------------------------------*/
	public		double		getPageIncrement()
		{
		return(pageIncrement);
		}
					/**------------------------------------------------------
	 				 * Increases the value of this adjuster widget by one unit.
					 * @see #setUnitIncrement
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		increaseOneUnit()
		{
		increase(incOneUnitEvent, unitIncrement);
		}
					/**------------------------------------------------------
	 				 * Decreases the value of this adjuster widget by one unit.
					 * @see #setUnitIncrement
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		decreaseOneUnit()
		{
		decrease(decOneUnitEvent, unitIncrement);
		}
					/**------------------------------------------------------
	 				 * Increases the value of this adjuster widget by one chunk.
					 * @see #setUnitIncrement
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		increaseOneChunk()
		{
		increase(incOneChunkEvent, chunkIncrement);
		}
					/**------------------------------------------------------
	 				 * Decreases the value of this adjuster widget by one chunk.
					 * @see #setUnitIncrement
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		decreaseOneChunk()
		{
		decrease(decOneChunkEvent, chunkIncrement);
		}
					/**------------------------------------------------------
	 				 * Moves the value of this adjuster widget by one chunk
					 * towards the given value. Moves by less than the chunk
					 * if the value is closer than the size of a chunk and sets
					 * the normalizedVvalue to the given value. Does not move if
					 * the normalizedVvalue == value.
					 * @see #setUnitIncrement
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		moveOneChunkTowardsValue(double value)
		{
		if (value < getNormalizedValue())
			{
			if (value + chunkIncrement <= getNormalizedValue())
				{
				if ((scrollable == null) || (!scrollable.isHandlingScrollingDiscreteAmountsLocally()))
					{
					decrease(setValueEvent, chunkIncrement);
					}
				else
					{
					decreaseOneChunk();
					}
				}
			else if (value != getNormalizedValue())
				{
				setNormalizedValue(value);
				}
			}
		else if (value > getNormalizedValue())
			{
			if (value - chunkIncrement >= getNormalizedValue())
				{
				if ((scrollable == null) || (!scrollable.isHandlingScrollingDiscreteAmountsLocally()))
					{
					increase(setValueEvent, chunkIncrement);
					}
				else
					{
					increaseOneChunk();
					}
				}
			else if (value != getNormalizedValue())
				{
				setNormalizedValue(value);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Increases the value of this adjuster widget by one page.
					 * @see #setUnitIncrement
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		increaseOnePage()
		{
		increase(incOnePageEvent, pageIncrement);
		}
					/**------------------------------------------------------
	 				 * Decreases the value of this adjuster widget by one page.
					 * @see #setUnitIncrement
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		decreaseOnePage()
		{
		decrease(decOnePageEvent, pageIncrement);
		}
					/**------------------------------------------------------
	 				 * Increases the value of this adjuster widget to the
					 * maximum amount.
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		increaseToMaximum()
		{
		setNormalizedValue(1.0);
		setNormalizedPositionOfThumb(normalizedValue);
		notifySubjectAboutChange(incToMaxEvent);
		}
					/**------------------------------------------------------
	 				 * Decreases the value of this adjuster widget to the
					 * maximum amount.
					 * @implements MiiAdjuster
					 *------------------------------------------------------*/
	public		void		decreaseToMinimum()
		{
		setNormalizedValue(0.0);
		setNormalizedPositionOfThumb(normalizedValue);
		notifySubjectAboutChange(decToMinEvent);
		}

					/**------------------------------------------------------
	 				 * Sets the type of events/methods to send/call.
					 * If orient is Mi_VERTICAL the generated messages will be 
					 * like MiEvent.Mi_SCROLL_PAGE_UP_EVENT else if orient is
					 * Mi_HORIZONTAL the generated messages will be like
					 * MiEvent.Mi_SCROLL_PAGE_RIGHT_EVENT.
					 * @param orient	Mi_VERTICAL or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	public		void		setOrientationOfGeneratedMessages(int orient)
		{
		generatedEventOrientation = orient;
		if (orient == Mi_VERTICAL)
			{
			setValueEvent 	= MiEvent.Mi_SCROLL_TO_VERTICAL_LOCATION_EVENT;
			incOneUnitEvent	= MiEvent.Mi_SCROLL_LINE_UP_EVENT;
			decOneUnitEvent	= MiEvent.Mi_SCROLL_LINE_DOWN_EVENT;
			incOneChunkEvent= MiEvent.Mi_SCROLL_CHUNK_UP_EVENT;
			decOneChunkEvent= MiEvent.Mi_SCROLL_CHUNK_DOWN_EVENT;
			incOnePageEvent	= MiEvent.Mi_SCROLL_PAGE_UP_EVENT;
			decOnePageEvent	= MiEvent.Mi_SCROLL_PAGE_DOWN_EVENT;
			incToMaxEvent	= MiEvent.Mi_SCROLL_TO_TOP_EVENT;
			decToMinEvent	= MiEvent.Mi_SCROLL_TO_BOTTOM_EVENT;
			}
		else
			{
			setValueEvent 	= MiEvent.Mi_SCROLL_TO_HORIZONTAL_LOCATION_EVENT;
			incOneUnitEvent	= MiEvent.Mi_SCROLL_LINE_RIGHT_EVENT;
			decOneUnitEvent	= MiEvent.Mi_SCROLL_LINE_LEFT_EVENT;
			incOneChunkEvent= MiEvent.Mi_SCROLL_CHUNK_RIGHT_EVENT;
			decOneChunkEvent= MiEvent.Mi_SCROLL_CHUNK_LEFT_EVENT;
			incOnePageEvent	= MiEvent.Mi_SCROLL_PAGE_RIGHT_EVENT;
			decOnePageEvent	= MiEvent.Mi_SCROLL_PAGE_LEFT_EVENT;
			incToMaxEvent	= MiEvent.Mi_SCROLL_TO_RIGHTSIDE_EVENT;
			decToMinEvent	= MiEvent.Mi_SCROLL_TO_LEFTSIDE_EVENT;
			}
		}
					/**------------------------------------------------------
	 				 * Gets the type of events/methods to send/call.
					 * @return		either Mi_VERTICAL or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	public		int		getOrientationOfGeneratedMessages()
		{
		return(generatedEventOrientation);
		}
					/**------------------------------------------------------
	 				 * Sets the observer of this Adjuster widget. If the 
					 * observer is null (the default) then events are sent
					 * to this widget. Else if the observer is not an instance
					 * of MiiScrollableData then events are sent to the 
					 * observer. Else if the observer is an instance of
					 * MiiScrollableData then the MiiScrollableData methods
					 * of the observer are called and no events are sent.
					 * @param observer	the observer
					 *------------------------------------------------------*/
	public		void		setObserver(MiPart observer)
		{
		this.observer = observer;
		if ((observer != null) && (observer instanceof MiiScrollableData))
			{
			if (scrollable == null)
				{
				scrollable = (MiiScrollableData )observer;
				}
			generateEvents = false;
			}
		else
			{
			generateEvents = true;
			}
		}
	public		void		setScrollable(MiiScrollableData scrollable)
		{
		this.scrollable = scrollable;
		}
					/**------------------------------------------------------
	 				 * Sets the observer of this Adjuster widget.
					 * @return		the observer
					 *------------------------------------------------------*/
	public		MiPart		getObserver()
		{
		return(observer);
		}

					/**------------------------------------------------------
	 				 * Sets whether only absolute position events/methods are 
					 * sent/called to the observer or whether more specific 
					 * events/methods are sent/called instead.
					 *
					 * The absolute position events are:
					 *	MiEvent.Mi_SCROLL_TO_VERTICAL_LOCATION_EVENT
					 *	MiEvent.Mi_SCROLL_TO_HORIZONTAL_LOCATION_EVENT
					 * The more specific events are:
					 *	MiEvent.Mi_SCROLL_TO_VERTICAL_LOCATION_EVENT
					 *	MiEvent.Mi_SCROLL_LINE_UP_EVENT;
					 *	MiEvent.Mi_SCROLL_LINE_DOWN_EVENT;
					 *	MiEvent.Mi_SCROLL_CHUNK_UP_EVENT;
					 *	MiEvent.Mi_SCROLL_CHUNK_DOWN_EVENT;
					 *	MiEvent.Mi_SCROLL_PAGE_UP_EVENT;
					 *	MiEvent.Mi_SCROLL_PAGE_DOWN_EVENT;
					 *	MiEvent.Mi_SCROLL_TO_TOP_EVENT;
					 *	MiEvent.Mi_SCROLL_TO_BOTTOM_EVENT;
					 *
					 *	MiEvent.Mi_SCROLL_TO_HORIZONTAL_LOCATION_EVENT
					 *	MiEvent.Mi_SCROLL_LINE_RIGHT_EVENT;
					 *	MiEvent.Mi_SCROLL_LINE_LEFT_EVENT;
					 *	MiEvent.Mi_SCROLL_CHUNK_RIGHT_EVENT;
					 *	MiEvent.Mi_SCROLL_CHUNK_LEFT_EVENT;
					 *	MiEvent.Mi_SCROLL_PAGE_RIGHT_EVENT;
					 *	MiEvent.Mi_SCROLL_PAGE_LEFT_EVENT;
					 *	MiEvent.Mi_SCROLL_TO_RIGHTSIDE_EVENT;
					 *	MiEvent.Mi_SCROLL_TO_LEFTSIDE_EVENT;
					 * @param flag		true if only position events are
					 *			sent.
					 *------------------------------------------------------*/
	public		void		setGenerateAbsolutePositionsOnly(boolean flag)
		{
		generateAbsolutePositionsOnly = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether only absolute position events/methods are 
					 * sent/called to the observer or whether more specific 
					 * events/methods are sent/called instead.
					 * @return 		true if only position events are
					 *			sent.
					 *------------------------------------------------------*/
	public		boolean		getGenerateAbsolutePositionsOnly()
		{
		return(generateAbsolutePositionsOnly);
		}

					/**------------------------------------------------------
	 				 * Handles the user's picking up the associated 'thumb'
					 * of this widget with presumed intent to move the thumb
					 * in order to change the value.
	 				 * @param event 	the user's event
					 * @return		the event is consumed
					 * @implements MiiDraggable
					 *------------------------------------------------------*/
	public		int 		pickup(MiEvent event)
		{
		if (isDesignTime())
			return(Mi_PROPOGATE_EVENT);
		select(true);
		return(Mi_CONSUME_EVENT);
		}
					/**------------------------------------------------------
	 				 * Handles the user's dragging the associated 'thumb'
					 * of this widget in order to change the value.
	 				 * @param event 	the user's event
					 * @return		the event is consumed
					 * @implements MiiDraggable
					 *------------------------------------------------------*/
	public		int 		drag(MiEvent event)
		{
		adjustValueFromVector(event.worldVector);
		return(Mi_CONSUME_EVENT);
		}
					/**------------------------------------------------------
	 				 * Handles the user's dropping the associated 'thumb'
					 * of this widget in order to stop changing the value.
	 				 * @param event 	the user's event
					 * @return		the event is consumed
					 * @implements MiiDraggable
					 *------------------------------------------------------*/
	public		int	 	drop(MiEvent event)
		{
		select(false);
		return(Mi_CONSUME_EVENT);
		}

					/**------------------------------------------------------
	 				 * Sets the position of the associated 'thumb' of this
					 * widget and updates the normalized value.
	 				 * @param normalizedValue 	the relative position.
					 *------------------------------------------------------*/
	public	abstract void		setNormalizedPositionOfThumb(double normalizedValue);

					/**------------------------------------------------------
	 				 * Sets the normalized value of this widget without
					 * updating the graphics.
	 				 * @param value 	the normalzied value
					 *------------------------------------------------------*/
	protected	void		setNormalizedValueOnly(double value)
		{
		if (normalizedValue != value)
			{
			if (value < 0.0)
				value = 0.0;
			else if (value > 1.0)
				value = 1.0;
			normalizedValue = value;
			notifySubjectAboutChange(setValueEvent);
			}
		}
					/**------------------------------------------------------
	 				 * Increases the value of this adjuster widget by the given
					 * amount.
	 				 * @param eventType 	the type of the event to be 
					 *			generated (page-up, down-a-line,...)
					 * @param amount	the normalized amount by which to
					 *			change the value.
					 *------------------------------------------------------*/
	protected	void		increase(int eventType, double amount)
		{
		if (normalizedValue == 1.0)
			return;
		if ((scrollable == null) || (!scrollable.isHandlingScrollingDiscreteAmountsLocally()))
			{
			double value = normalizedValue + amount;
			if (value > 1.0)
				value = 1.0;

			setNormalizedValue(value);
			setNormalizedPositionOfThumb(normalizedValue);
			}
		notifySubjectAboutChange(eventType);
		}
					/**------------------------------------------------------
	 				 * Decreases the value of this adjuster widget by the given
					 * amount.
	 				 * @param eventType 	the type of the event to be 
					 *			generated (page-up, down-a-line,...)
					 * @param amount	the normalized amount by which to
					 *			change the value.
					 *------------------------------------------------------*/
	protected	void		decrease(int eventType, double amount)
		{
		if (normalizedValue == 0.0)
			return;
		if ((scrollable == null) || (!scrollable.isHandlingScrollingDiscreteAmountsLocally()))
			{
			double value = normalizedValue - amount;
			if (value < 0.0)
				value = 0.0;
			setNormalizedValue(value);
			setNormalizedPositionOfThumb(normalizedValue);
			}
		notifySubjectAboutChange(eventType);
		}

					/**------------------------------------------------------
	 				 * Notifies the observer about the new value by sending
					 * events to, or calling methods, on the observer.
	 				 * @param eventType 	the type of the change
					 *------------------------------------------------------*/
	private		void		notifySubjectAboutChange(int eventType)
		{
		if (!generateAbsolutePositionsOnly)
			{
			if (generateEvents)
				{
				event.setType(eventType);
				if (observer != null)
					observer.dispatchEvent(event);
				else
					dispatchEvent(event);
				}
			else if (scrollable != null)
				{
				switch (eventType)
					{
					case MiEvent.Mi_SCROLL_TO_VERTICAL_LOCATION_EVENT:
						scrollable.scrollToNormalizedVerticalPosition(normalizedValue);
						break;
					case MiEvent.Mi_SCROLL_LINE_UP_EVENT:
						scrollable.scrollLineUp();
						break;
					case MiEvent.Mi_SCROLL_LINE_DOWN_EVENT:
						scrollable.scrollLineDown();
						break;
					case MiEvent.Mi_SCROLL_CHUNK_UP_EVENT:
						scrollable.scrollChunkUp();
						break;
					case MiEvent.Mi_SCROLL_CHUNK_DOWN_EVENT:
						scrollable.scrollChunkDown();
						break;
					case MiEvent.Mi_SCROLL_PAGE_UP_EVENT:
						scrollable.scrollPageUp();
						break;
					case MiEvent.Mi_SCROLL_PAGE_DOWN_EVENT:
						scrollable.scrollPageDown();
						break;
					case MiEvent.Mi_SCROLL_TO_TOP_EVENT:
						scrollable.scrollToTop();
						break;
					case MiEvent.Mi_SCROLL_TO_BOTTOM_EVENT:
						scrollable.scrollToBottom();
						break;
					case MiEvent.Mi_SCROLL_TO_HORIZONTAL_LOCATION_EVENT:
						scrollable.scrollToNormalizedHorizontalPosition(normalizedValue);
						break;
					case MiEvent.Mi_SCROLL_LINE_RIGHT_EVENT:
						scrollable.scrollLineRight();
						break;
					case MiEvent.Mi_SCROLL_LINE_LEFT_EVENT:
						scrollable.scrollLineLeft();
						break;
					case MiEvent.Mi_SCROLL_CHUNK_RIGHT_EVENT:
						scrollable.scrollChunkRight();
						break;
					case MiEvent.Mi_SCROLL_CHUNK_LEFT_EVENT:
						scrollable.scrollChunkLeft();
						break;
					case MiEvent.Mi_SCROLL_PAGE_RIGHT_EVENT:
						scrollable.scrollPageRight();
						break;
					case MiEvent.Mi_SCROLL_PAGE_LEFT_EVENT:
						scrollable.scrollPageLeft();
						break;
					case MiEvent.Mi_SCROLL_TO_RIGHTSIDE_EVENT:
						scrollable.scrollToRightSide();
						break;
					case MiEvent.Mi_SCROLL_TO_LEFTSIDE_EVENT:
						scrollable.scrollToLeftSide();
						break;
					}
				}
			}
		else
			{
			if (generateEvents)
				{
				if (generatedEventOrientation == Mi_VERTICAL)
					event.setType(MiEvent.Mi_SCROLL_TO_VERTICAL_LOCATION_EVENT);
				else
					event.setType(MiEvent.Mi_SCROLL_TO_HORIZONTAL_LOCATION_EVENT);
					
				if (observer != null)
					observer.dispatchEvent(event);
				else
					dispatchEvent(event);
				}
			else if (scrollable != null)
				{
				if (generatedEventOrientation == Mi_VERTICAL)
					scrollable.scrollToNormalizedVerticalPosition(normalizedValue);
				else
					scrollable.scrollToNormalizedHorizontalPosition(normalizedValue);
				}
			}
		}
	}

