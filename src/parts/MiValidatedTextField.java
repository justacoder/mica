

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
import com.swfm.mica.util.Strings;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiValidatedTextField extends MiTextField
	{
	private		MiiValueValidator	validator;
	private		MiValueValidationError	validationError;
	private		boolean			dispatchActionWhenInvalid	= true;
	private		boolean			restoreValidValueWhenInvalid	= true;
	private		String			previousValue			= new String();




	public				MiValidatedTextField()
		{
		super();
		}

	public				MiValidatedTextField(String str)
		{
		super(str);
		}
	public		void		setValidator(MiiValueValidator validator)
		{
		this.validator = validator;
		}
	public		MiiValueValidator getValidator()
		{
		return(validator);
		}
	public		MiValueValidationError getValidatorError()
		{
		return(validationError);
		}
	public		MiValueValidationError	validateValue()
		{
		return(validator.validateValue(getValue()));
		}
	public		void		setValue(String text)
		{
		super.setValue(text);
		previousValue = getValue();
		}
	public		boolean		processAction(MiiAction action)
		{
		if (validator != null)
			{
			if (action.hasActionType(Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE))
				{
				String text = validator.modifyValue(getValue());
				if (!Utility.isEqualTo(text, getValue()))
					setValue(text);
				if ((validationError = validator.validateValueType(getValue())) != null)
					{
					action.veto();
					return(false);
					}
				}
			else if ((action.hasActionType(Mi_ENTER_KEY_ACTION))
				|| (action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION)))
				{
				if ((validationError = validator.validateValue(getValue())) != null)
					{
					if (dispatchActionWhenInvalid)
						dispatchAction(Mi_INVALID_VALUE_ACTION, validationError);
					if (restoreValidValueWhenInvalid)
						setValue(previousValue);
					return(false);
					}
				previousValue = getValue();
				}
			}
		return(super.processAction(action));
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
		validator = ((MiValidatedTextField )source).validator;
		}
	}

