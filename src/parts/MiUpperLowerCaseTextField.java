
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
public class MiUpperLowerCaseTextField extends MiTextField
	{
	public static final int		Mi_ALL_UPPER_CASE_CONSTRAINT		= 1;
	public static final int		Mi_ALL_LOWER_CASE_CONSTRAINT		= 2;
	public static final int		Mi_ONLY_FIRST_CHAR_CAPITALIZED_CONSTRAINT= 3;
	private		int		constraint = Mi_ALL_UPPER_CASE_CONSTRAINT;




	public				MiUpperLowerCaseTextField()
		{
		this(null);
		}
	public				MiUpperLowerCaseTextField(String text)
		{
		super(text);
		}
	public		void		setCaseConstraint(int constraint)
		{
		this.constraint = constraint;
		}
	public		int		getCaseConstraint()
		{
		return(constraint);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			String text = getValue();
			if (!Utility.isEmptyOrNull(text))
				{
				if (constraint == Mi_ALL_UPPER_CASE_CONSTRAINT)
					{
					if (!text.toUpperCase().equals(text))
						{
						setValue(text.toUpperCase());
						}
					}
				else if (constraint == Mi_ALL_LOWER_CASE_CONSTRAINT)
					{
					if (!text.toLowerCase().equals(text))
						{
						setValue(text.toLowerCase());
						}
					}
				else if (constraint == Mi_ONLY_FIRST_CHAR_CAPITALIZED_CONSTRAINT)
					{
					if ((!Character.isUpperCase(text.charAt(0)))
						|| (!text.substring(1).toLowerCase().equals(
							text.substring(1))))
						{
						setValue(Character.toUpperCase(text.charAt(0))
							+ text.substring(1).toLowerCase());
						}
					}
				}
			}
		return(super.processAction(action));
		}
	}

