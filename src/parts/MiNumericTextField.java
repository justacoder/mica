
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
import com.swfm.mica.util.StringIntPair;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiNumericTextField extends MiValidatedTextField
	{
	private		MiDoubleValueValidator validator;


	public				MiNumericTextField()
		{
		this(null);
		}
	public				MiNumericTextField(String text)
		{
		super(text);
		validator = new MiDoubleValueValidator();
		setValidator(validator);
		}
	public		void		setMinimumValue(double minimum)
		{
		validator.setMinimumValue(minimum);
		}
	public		double		getMinimumValue()
		{
		return(validator.getMinimumValue());
		}
	public		void		setMaximumValue(double maximum)
		{
		validator.setMaximumValue(maximum);
		}
	public		double		getMaximumValue()
		{
		return(validator.getMaximumValue());
		}
	public		void		setStepValue(double stepValue)
		{
		validator.setStepValue(stepValue);
		}
	public		double		getStepValueValue()
		{
		return(validator.getStepValue());
		}
	}

