
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
public class MiMaskedTextField extends MiValidatedTextField
	{
	private		MiMaskedValueValidator	validator;
	private		MiMaskedTextFieldEditor	textFieldEditor;
	private		String			literals;

	public				MiMaskedTextField()
		{
		this(null);
		}
	public				MiMaskedTextField(String text)
		{
		super(text);
		validator = new MiMaskedValueValidator();
		setValidator(validator);
		}
	public		void		setMask(String mask)
		{
		validator.setMask(mask);
		literals = validator.getMaskLiterals();
		textFieldEditor.setMaskLiterals(literals);
		}
	public		String		getMask()
		{
		return(validator.getMask());
		}
	protected	MiTextFieldEditor	makeTextFieldEditor(MiText text)
		{
		MiMaskedTextFieldEditor textFieldEditor = new MiMaskedTextFieldEditor(text);
		textFieldEditor.setMaskLiterals(literals);
		return(textFieldEditor);
		}
	}
class MiMaskedTextFieldEditor extends MiTextFieldEditor
	{
	private		String		mask;
	private		int		lastCursorPosition = -1;


	public				MiMaskedTextFieldEditor(MiText t)
		{
		super(t);
		}

	public		void		setMaskLiterals(String mask)
		{
		this.mask = mask;
		}
	public		String		getMaskLiterals()
		{
		return(mask);
		}

	protected	String		deleteCharAtPosition(String text, int pos)
		{
		if (!isFixedChar(pos))
			text = text.substring(0, pos) + " " + text.substring(pos + 1);
		return(text);
		}
	protected	String		insertCharAtPosition(String text, char ch, int pos)
		{
		// If there is a 'placeholder' (space) to the right that comes before a 'fixed marker'
		// then remove the 'fixed marker' and insert the char
		for (int i = pos; i < text.length(); ++i)
			{
			if (isFixedChar(i))
				return(text);
			if (text.charAt(i) == ' ')
				{
				text = text.substring(0, pos) + ch 
					+ text.substring(pos, i) + text.substring(i + 1);
				break;
				}
			}
		return(text);
		}
	protected	String		replaceCharAtPosition(String text, char ch, int pos)
		{
		// If this is not a 'fixed marker', replace the target char
		if (!isFixedChar(pos))
			text = text.substring(0, pos) + ch + text.substring(pos + 1);
		return(text);
		}
	protected	String		deleteCharsAtPositions(String text, int start, int end)
		{
		// Replace all selected chars that are not 'fixed' with 'placeholders'
		String result = text.substring(0, start);
		for (int i = start; i < end; ++i)
			{
			if (!isFixedChar(i))
				result += " ";
			else
				result += text.charAt(i);
			}
		return(result);
		}
	public		void		setCursorPosition(int pos)
		{
		int cursorPosition = pos;
		int direction = (cursorPosition - lastCursorPosition) > 0 ? 1 : -1;

		if (isFixedChar(pos))
			{
			for (int i = pos; (i >= 0) && (i < mask.length()); i += direction)
				{
				if (!isFixedChar(i))
					{
					cursorPosition = i;
					break;
					}
				}
			if (cursorPosition == pos)
				{
				for (int i = pos; (i >= 0) && (i < mask.length()); i -= direction)
					{
					if (!isFixedChar(i))
						{
						cursorPosition = i;
						break;
						}
					}
				}
			if (cursorPosition == pos)
				return;
			}
		super.setCursorPosition(cursorPosition);
		}


	protected	boolean		isFixedChar(int pos)
		{
		return((mask != null) && ((mask.length() <= pos) || (mask.charAt(pos) != ' ')));
		}
	}


