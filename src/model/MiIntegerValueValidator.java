
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
class MiIntegerValueValidator extends MiValueValidator
	{
	private		boolean		minimumValueSpecified;
	private		boolean		maximumValueSpecified;
	private		int		minimumValue;
	private		int		maximumValue;
	private		int		stepValue;

	public				MiIntegerValueValidator()
		{
		}

	public		void		setMinimumValue(int value)
		{
		minimumValue = value;
		minimumValueSpecified = true;
		}
	public		int		getMinimumValue()
		{
		return(minimumValue);
		}
	public		void		setMaximumValue(int value)
		{
		maximumValue = value;
		maximumValueSpecified = true;
		}
	public		int		getMaximumValue()
		{
		return(maximumValue);
		}
	public		void		setStepValue(int value)
		{
		stepValue = value;
		}
	public		int		getStepValue()
		{
		return(stepValue);
		}


	protected	MiValueValidationError	doTypeValidation(String value)
		{
		if (!Utility.isInteger(value))
			{
			return(new MiValueValidationError(null,
				Mi_VALUE_NOT_AN_INTEGER_VALIDATION_ERROR, value));
			}
		return(null);
		}
	protected	MiValueValidationError	doValidation(String value)
		{
		if (!Utility.isInteger(value))
			{
			return(new MiValueValidationError(null,
				Mi_VALUE_NOT_AN_INTEGER_VALIDATION_ERROR, value));
			}

		int val = Utility.toInteger(value);
		if ((minimumValueSpecified) && (val < minimumValue))
			{
			return(new MiValueValidationError(
				Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR, value, "" + minimumValue));
			}
		if ((maximumValueSpecified) && (val > maximumValue))
			{
			return(new MiValueValidationError(
				Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR, value, "" + maximumValue));
			}
		if ((stepValue != 0) 
			&& (((val - minimumValue) % stepValue) == 0))
			{
			return(new MiValueValidationError(
				Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR, value,
					minimumValue + "[+ n*" + stepValue + "]"));
			}
		return(null);
		}
	}

