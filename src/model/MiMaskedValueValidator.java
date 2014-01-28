
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
import com.swfm.mica.util.StringIntPair;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
class MiMaskedValueValidator extends MiValueValidator
	{
	public static final int		Mi_NUMERIC_OR_SIGN_CHAR		= 	0;
	public static final int		Mi_NUMERIC_CHAR			= 	1;
	public static final int		Mi_HEXIDECIMAL_CHAR		= 	2;
	public static final int		Mi_UPPER_CASE_LETTER_CHAR	= 	3;
	public static final int		Mi_LOWER_CASE_LETTER_CHAR	= 	4;
	public static final int		Mi_LETTER_CHAR			= 	5;
	public static final int		Mi_LETTER_NUMERIC_CHAR		= 	6;
	public static final int		Mi_ANY_CHAR			= 	7;
	public static final int		Mi_ANY_OR_NO_CHAR		= 	8;
	public static final int		Mi_LITERAL_FOLLOWS_CHAR		= 	9;
	private static final int	Mi_LITERAL_CHAR			= 	10;

	private static char[]		maskCharacters 			=
		{
		'#','@','H','U','L','A','*','^','?','\\' 
		};

	private		String		mask;
	private		int[]		maskCodes;
	private		int		lastCursorPosition		=	-1;


	public				MiMaskedValueValidator()
		{
		}

	public		void		setMask(String mask)
		{
		this.mask = mask;
		maskCodes = parseMask(mask);
		}
	public		String		getMask()
		{
		return(mask);
		}

	public		void		setMaskCharForCharType(char ch, int type)
		{
		maskCharacters[type] = ch;
		}
	public		char		getMaskCharForCharType(int type)
		{
		return(maskCharacters[type]);
		}
	public		String		getMaskLiterals()
		{
		String literals = new String();
		for (int i = 0; i < mask.length(); ++i)
			{
			if (maskCodes[i] == Mi_LITERAL_CHAR)
				literals += mask.charAt(i);
			else
				literals += " ";
			}
		return(literals);
		}
	protected	int[]		parseMask(String mask)
		{
		int[] maskCodes = new int[mask.length()];
		int numSpecialLiterals = 0;
		for (int i = 0; i < mask.length(); ++i)
			{
			char ch = mask.charAt(i);
			boolean found = false;
			if (ch == maskCharacters[Mi_LITERAL_FOLLOWS_CHAR])
				{
				++numSpecialLiterals;
				++i;
				ch = mask.charAt(i);
				maskCodes[i] = Mi_LITERAL_CHAR;
				found = true;
				}
			for (int j = 0; (!found) && (j < maskCharacters.length); ++j)
				{
				if (ch == maskCharacters[j])
					{
					maskCodes[i] = j;
					found = true;
					break;
					}
				}
			if (!found)
				{
				maskCodes[i] = Mi_LITERAL_CHAR;
				}
			}
		if (numSpecialLiterals > 0)
			{
			int [] codes = new int[mask.length() - numSpecialLiterals];
			System.arraycopy(maskCodes, 0, codes, 0, codes.length);
			maskCodes = codes;
			}
		return(maskCodes);
		}

	public		String		modifyValue(String value)
		{
		for (int i = 0; i < maskCodes.length; ++i)
			{
			if (value.length() <= i)
				value += " ";
			char ch = value.charAt(i);
			switch (maskCodes[i])
				{
				case	Mi_UPPER_CASE_LETTER_CHAR	:
					{
					if (ch != Character.toUpperCase(ch))
						{
						value = value.substring(0, i) + Character.toUpperCase(ch) +
							value.substring(i + 1);
						}
					break;
					}
				case	Mi_LOWER_CASE_LETTER_CHAR	:
					{
					if (ch != Character.toLowerCase(ch))
						{
						value = value.substring(0, i) + Character.toLowerCase(ch) +
							value.substring(i + 1);
						}
					break;
					}
				case	Mi_LITERAL_CHAR	:
					{
					if (ch != mask.charAt(i))
						{
						value = value.substring(0, i) + mask.charAt(i) +
							value.substring(i + 1);
						}
					break;
					}
				}
			}
		return(value);
		}
	protected	MiValueValidationError	doTypeValidation(String value)
		{
		return(null);
		}
	protected	MiValueValidationError	doValidation(String value)
		{
		for (int i = 0; i < value.length(); ++i)
			{
			char ch = value.charAt(i);
			switch (maskCodes[i])
				{
				case	Mi_NUMERIC_OR_SIGN_CHAR		:
					{
					if ((!Character.isDigit(ch)) 
						&& (ch != '.') && (ch != '+') && (ch != '-') && (ch != ','))
						{
						return(new MiValueValidationError(
							Utility.sprintf(
							Mi_CHAR_NOT_NUMERIC_OR_SIGN_VALIDATION_ERROR_MSG,
							"" + ch)));
						}
					break;
					}
				case	Mi_NUMERIC_CHAR			:
					{
					if (!Character.isDigit(ch))
						{
						return(new MiValueValidationError(
							Utility.sprintf(
							Mi_CHAR_NOT_NUMERIC_VALIDATION_ERROR_MSG,
							"" + ch)));
						}
					break;
					}
				case	Mi_HEXIDECIMAL_CHAR		:
					{
					ch = Character.toUpperCase(ch);
					if (!(((ch >= '0') && (ch <= '9')) || ((ch >= 'A') && (ch <= 'F'))))
						{
						return(new MiValueValidationError(
							Utility.sprintf(
							Mi_CHAR_NOT_HEXIDECIMAL_VALIDATION_ERROR_MSG,
							"" + ch)));
						}
					break;
					}
				case	Mi_UPPER_CASE_LETTER_CHAR	:
					{
					if (!Character.isUpperCase(ch))
						{
						return(new MiValueValidationError(
							Utility.sprintf(
							Mi_CHAR_NOT_UPPER_CASE_VALIDATION_ERROR_MSG,
							"" + ch)));
						}
					break;
					}
				case	Mi_LOWER_CASE_LETTER_CHAR	:
					{
					if (!Character.isLowerCase(ch))
						{
						return(new MiValueValidationError(
							Utility.sprintf(
							Mi_CHAR_NOT_LOWER_CASE_VALIDATION_ERROR_MSG,
							"" + ch)));
						}
					break;
					}
				case	Mi_LETTER_CHAR			:
					{
					if (!Character.isLetter(ch))
						{
						return(new MiValueValidationError(
							Utility.sprintf(
							Mi_CHAR_NOT_ALPHABETIC_VALIDATION_ERROR_MSG,
							"" + ch)));
						}
					break;
					}
				case	Mi_LETTER_NUMERIC_CHAR		:
					{
					if (!Character.isLetterOrDigit(ch))
						{
						return(new MiValueValidationError(
							Utility.sprintf(
							Mi_CHAR_NOT_ALPHANUMERIC_VALIDATION_ERROR_MSG,
							"" + ch)));
						}
					break;
					}
				case	Mi_ANY_CHAR			:
					{
					break;
					}
				case	Mi_ANY_OR_NO_CHAR		:
					{
					break;
					}
				case	Mi_LITERAL_FOLLOWS_CHAR		:
					{
					break;
					}
				case	Mi_LITERAL_CHAR			:
					{
					break;
					}
				}
			}
		return(null);
		}
	}

