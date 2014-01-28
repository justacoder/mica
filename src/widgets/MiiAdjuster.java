
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public interface MiiAdjuster
	{
			/**------------------------------------------------------
	 		 * Sets the minimum value of this adjuster widget.
			 * @param value		the minimum value
			 * @see #setCurrentValue
			 *------------------------------------------------------*/
	void		setMinimumValue(double value);


			/**------------------------------------------------------
	 		 * Gets the minimum value of this adjuster widget.
			 * @return		the minimum value
			 *------------------------------------------------------*/
	double		getMinimumValue();


			/**------------------------------------------------------
	 		 * Sets the maximum value of this adjuster widget.
			 * @param value		the maximum value
			 * @see #setCurrentValue
			 *------------------------------------------------------*/
	void		setMaximumValue(double value);


			/**------------------------------------------------------
	 		 * Gets the maximum value of this adjuster widget.
			 * @return		the maximum value
			 *------------------------------------------------------*/
	double		getMaximumValue();


			/**------------------------------------------------------
	 		 * Sets the value of this adjuster widget. The value should
			 * be between the minimum and maximum values.
			 * @param value		the current value
			 * @see #setMinimumValue
			 * @see #setMaximumValue
			 * @see #getCurrentValue
			 * @see #setNormalizedValue
			 *------------------------------------------------------*/
	void		setCurrentValue(double value);


			/**------------------------------------------------------
			 * Gets the value of this adjuster widget. The value is
			 * be between the minimum and maximum values.
			 * @return		the current value
			 * @see #setCurrentValue
			 *------------------------------------------------------*/
	double		getCurrentValue();


			/**------------------------------------------------------
 			 * Sets the value of this adjuster widget. The value should
			 * be between 0.0 and 1.0 inclusive.
			 * @param value		the normalized value
			 * @see #setCurrentValue
			 *------------------------------------------------------*/
	void		setNormalizedValue(double value);


			/**------------------------------------------------------
			 * Gets the value of this adjuster widget. The value is
			 * be between 0.0 and 1.0 inclusive.
			 * @return 		the normalized value
			 * @see #getCurrentValue
			 *------------------------------------------------------*/
	double		getNormalizedValue();


			/**------------------------------------------------------
			 * Sets the normalized amount of the unit increment for 
			 * this widget.
			 * @param amount	the amount (between 0.0 and 1.0)
			 *------------------------------------------------------*/
	void		setUnitIncrement(double amount);


			/**------------------------------------------------------
			 * Sets the position of the associated 'thumb' of this
			 * widget. This is used when the user clicks on an
			 * interactive implementation of this interface.
			 * @param point		the point at which the thumb is
			 *			positioned.
			 *------------------------------------------------------*/
	void		setValueFromLocation(MiPoint point);

			/**------------------------------------------------------
			 * Moves the position of the associated 'thumb' of this
			 * widget. This is used when the user clicks on an
			 * interactive implementation of this interface.
			 * @param point		the point in which direction the 
			 *			thumb is to be positioned.
			 *------------------------------------------------------*/
	void		moveOneChunkTowardsLocation(MiPoint point);

			/**------------------------------------------------------
	 		 * Sets the position of the associated 'thumb' of this
			 * widget. This is used when the user drags on an
			 * interactive implementation of this interface.
	 		 * @param delta		the distance the user has moved
			 *			the thumb.
			 *------------------------------------------------------*/
	void		adjustValueFromVector(MiVector delta);


			/**------------------------------------------------------
	 		 * Increases the value of this adjuster widget by one unit.
			 * @see #setUnitIncrement
			 *------------------------------------------------------*/
	void		increaseOneUnit();


			/**------------------------------------------------------
	 		 * Decreases the value of this adjuster widget by one unit.
			 * @see #setUnitIncrement
			 *------------------------------------------------------*/
	void		decreaseOneUnit();


			/**------------------------------------------------------
	 		 * Increases the value of this adjuster widget by one chunk.
			 * @see #setUnitIncrement
			 *------------------------------------------------------*/
	void		increaseOneChunk();


			/**------------------------------------------------------
	 		 * Decreases the value of this adjuster widget by one chunk.
			 * @see #setUnitIncrement
			 *------------------------------------------------------*/
	void		decreaseOneChunk();


			/**------------------------------------------------------
	 		 * Increases the value of this adjuster widget by one page.
			 * @see #setUnitIncrement
			 *------------------------------------------------------*/
	void		increaseOnePage();


			/**------------------------------------------------------
	 		 * Decreases the value of this adjuster widget by one page.
			 * @see #setUnitIncrement
			 *------------------------------------------------------*/
	void		decreaseOnePage();


			/**------------------------------------------------------
	 		 * Increases the value of this adjuster widget to the
			 * maximum amount.
			 *------------------------------------------------------*/
	void		increaseToMaximum();


			/**------------------------------------------------------
	 		 * Decreases the value of this adjuster widget to the
			 * maximum amount.
			 *------------------------------------------------------*/
	void		decreaseToMinimum();
	}

