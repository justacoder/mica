
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
public class MiRestrictedTextField extends MiTextField
	{
	private		String		specialCharacters;
	private		boolean		disallowSpecialCharacters;


	public				MiRestrictedTextField()
		{
		this(null);
		}
	public				MiRestrictedTextField(String text)
		{
		super(text);
		}
	public		void		setSpecialCharacters(String chars)
		{
		specialCharacters = chars;
		}
	public		String		getSpecialCharacters()
		{
		return(specialCharacters);
		}
					// else allow only special characters
	public		void		setDisallowSpecialCharacters(boolean flag)
		{
		disallowSpecialCharacters = flag;
		}
	public		boolean		getDisallowSpecialCharacters()
		{
		return(disallowSpecialCharacters);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			String text = getValue();
			if ((!Utility.isEmptyOrNull(text)) && (specialCharacters != null))
				{
				if (disallowSpecialCharacters)
					{
					for (int i = 0; i < specialCharacters.length(); ++i)
						{
						char ch = specialCharacters.charAt(i);
						if (text.indexOf(ch) != -1)
							{
							action.veto();
							return(false);
							}
						}
					}
				else
					{
					for (int i = 0; i < text.length(); ++i)
						{
						char ch = text.charAt(i);
						if (specialCharacters.indexOf(ch) != -1)
							{
							action.veto();
							return(false);
							}
						}
					}
				}
			}
		return(super.processAction(action));
		}
	}

