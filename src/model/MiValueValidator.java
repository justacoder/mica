
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
abstract class MiValueValidator implements MiiValueValidator
	{
	private		boolean		requiresNonNullNonEmptyValue	= true;
	private		boolean		beepIfValidationError		= true;


	public		void		setRequired(boolean flag)
		{
		requiresNonNullNonEmptyValue = flag;
		}
	public		boolean		isRequired()
		{
		return(requiresNonNullNonEmptyValue);
		}
	public		String		modifyValue(String value)
		{
		return(value);
		}
	public		MiValueValidationError	validateValueType(String value)
		{
		if (Utility.isEmptyOrNull(value))
			return(null);

		MiValueValidationError error = doTypeValidation(value);
		if ((error != null) && (beepIfValidationError))
			; // beep
		return(error);
		}
	public		MiValueValidationError	validateValue(String value)
		{
		if (Utility.isEmptyOrNull(value))
			{
			if (requiresNonNullNonEmptyValue)
				{
				return(new MiValueValidationError(null,
					Mi_VALUE_MUST_BE_SPECIFIED_VALIDATION_ERROR, value));
				}
			return(null);
			}
		MiValueValidationError error = doValidation(value);
		if ((error != null) && (beepIfValidationError))
			; // beep
		return(error);
		}
	protected abstract MiValueValidationError doValidation(String value);
	protected abstract MiValueValidationError doTypeValidation(String value);
	}

