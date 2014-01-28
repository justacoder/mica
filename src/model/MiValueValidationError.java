
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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiValueValidationError implements MiiPropertyTypes
	{
	private		int			errorCode	= Mi_GENERIC_PROPERTY_VALIDATION_ERROR;
	private		String			badValue;
	private		String			goodValue;
	private		MiPropertyDescription	desc;
	private		Strings			validValues;
	private		String			shortErrorMessage;
	private		String			longErrorMessage;
	private		String			helpfulSuggestionMessage;
	private		boolean			restoreStateToPreviousValidStateIfPossible;


	public				MiValueValidationError()
		{
		if (MiDebug.printValidationErrors)
			MiDebug.printValidationError(this);
		}
	public				MiValueValidationError(int errorCode)
		{
		this.errorCode = errorCode;
		if (MiDebug.printValidationErrors)
			MiDebug.printValidationError(this);
		}
	public				MiValueValidationError(int errorCode, String badValue)
		{
		this.errorCode = errorCode;
		this.badValue = badValue;
		if (MiDebug.printValidationErrors)
			MiDebug.printValidationError(this);
		}
	public				MiValueValidationError(
						MiPropertyDescription desc, 
						int errorCode, 
						String badValue)
		{
		this.desc = desc;
		this.errorCode = errorCode;
		this.badValue = badValue;
		if (MiDebug.printValidationErrors)
			MiDebug.printValidationError(this);
		}
	public				MiValueValidationError(
						int errorCode, 
						String badValue,
						String goodValue)
		{
		this.errorCode = errorCode;
		this.badValue = badValue;
		this.goodValue = goodValue;
		if (MiDebug.printValidationErrors)
			MiDebug.printValidationError(this);
		}
	public				MiValueValidationError(String shortErrorMessage)
		{
		this.shortErrorMessage = shortErrorMessage;
		if (MiDebug.printValidationErrors)
			MiDebug.printValidationError(this);
		}
	public				MiValueValidationError(
						String shortErrorMessage, String longErrorMessage)
		{
		this.shortErrorMessage = shortErrorMessage;
		this.longErrorMessage = longErrorMessage;
		if (MiDebug.printValidationErrors)
			MiDebug.printValidationError(this);
		}
	public				MiValueValidationError(
						String shortErrorMessage,
						String longErrorMessage,
						String helpfulSuggestionMessage)
		{
		this.shortErrorMessage = shortErrorMessage;
		this.longErrorMessage = longErrorMessage;
		this.helpfulSuggestionMessage = helpfulSuggestionMessage;
		if (MiDebug.printValidationErrors)
			MiDebug.printValidationError(this);
		}
	public		void		setShortErrorMessage(String msg)
		{
		shortErrorMessage = msg;
		}
	public		String		getShortDescription()
		{
		if (shortErrorMessage != null)
			return(shortErrorMessage);
		return(generateShortErrorMessage(errorCode));
		}
	public		void		setLongErrorMessage(String msg)
		{
		longErrorMessage = msg;
		}
	public		String		getLongErrorMessage()
		{
		return(longErrorMessage);
		}
	public		void		setHelpfulSuggestionMessage(String msg)
		{
		helpfulSuggestionMessage = msg;
		}
	public		String		getHelpfulSuggestionMessage()
		{
		return(helpfulSuggestionMessage);
		}
	public		void		setValidValues(Strings values)
		{
		validValues = new Strings(values);
		}
	public		Strings		getValidValues()
		{
		if (validValues == null)
			{
			if ((desc != null) && (desc.getNumberOfValidValues() > 0))
				{
				return(desc.getValidValues());
				}
			return(new Strings());
			}
		return(new Strings(validValues));
		}
	public		Strings		getExcludedValues()
		{
		if ((desc != null) && (desc.getExcludedValues() != null))
			return(desc.getExcludedValues());
		return(new Strings());
		}
	protected	String		generateShortErrorMessage(int errorCode)
		{
		if (errorCode == Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR)
			{
			return(Utility.sprintf(MiSystem.getProperty(
				Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR_MSG),
				badValue, getValidValues().getCommaDelimitedStrings()));
			}
		if (errorCode == Mi_VALUE_IN_EXCLUDED_SET_VALIDATION_ERROR)
			{
			return(Utility.sprintf(MiSystem.getProperty(
				Mi_VALUE_IN_EXCLUDED_SET_VALIDATION_ERROR_MSG),
				badValue, getExcludedValues().getCommaDelimitedStrings()));
			}
		if ((errorCode == Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR) && (desc != null))
			{
			return(Utility.sprintf(MiSystem.getProperty(
				Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR_MSG),
				badValue, "" + desc.getMinimumValue()));
			}
		if ((errorCode == Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR) && (goodValue != null))
			{
			return(Utility.sprintf(MiSystem.getProperty(
				Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR_MSG),
				badValue, goodValue));
			}
		if ((errorCode == Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR) && (desc != null))
			{
			return(Utility.sprintf(MiSystem.getProperty(
				Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR_MSG),
				badValue, "" + desc.getMaximumValue()));
			}
		if ((errorCode == Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR) && (goodValue != null))
			{
			return(Utility.sprintf(MiSystem.getProperty(
				Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR_MSG),
				badValue, goodValue));
			}
		if ((errorCode >= 0) || (errorCode < errorMessages.length))
			{
			return(Utility.sprintf(MiSystem.getProperty(errorMessages[errorCode]), badValue, goodValue));
			}
		return(null);
		}
	public		void		setRestoreStateToPreviousValidStateIfPossible(boolean flag)
		{
		restoreStateToPreviousValidStateIfPossible = flag;
		}
	public		boolean		getRestoreStateToPreviousValidStateIfPossible()
		{
		return(restoreStateToPreviousValidStateIfPossible);
		}

	public		String		toString()
		{
		return(getShortDescription());
		}
	}

