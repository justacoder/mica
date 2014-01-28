
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

import com.swfm.mica.util.Pair;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings; 
import java.util.Vector; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiPropertyTypes
	{
			/**------------------------------------------------------
	 		 * Used by the XML save and load routines.
			 *------------------------------------------------------*/
	String		Mi_PROPERTY_DESCRIPTION_TYPE_NAME	= "property-description";

			/**------------------------------------------------------
	 		 * Used to match any and every property name (for example
			 * when using MiiModelEntity.appendPropertyChangeHandler()
			 *------------------------------------------------------*/
	String		Mi_ANY_PROPERTY				= "Mi_ANY_PROPERTY";

			/**------------------------------------------------------
	 		 * Properties of properties
			 *------------------------------------------------------*/
	String		Mi_PROPERTY_NAME		= "name";
	String		Mi_PROPERTY_DISPLAY_NAME	= "display-name";
	String		Mi_PROPERTY_VALID_VALUES_NAME	= "values";
	String		Mi_PROPERTY_VALID_DISPLAY_VALUES_NAME	= "display-values";
	String		Mi_PROPERTY_TYPE_NAME		= "type";
	String		Mi_PROPERTY_DEFAULT_VALUE_NAME	= "default";
	String		Mi_PROPERTY_MINIMUM_VALUE_NAME	= "min";
	String		Mi_PROPERTY_MAXIMUM_VALUE_NAME	= "max";
	String		Mi_PROPERTY_STEP_VALUE_NAME	= "step";
	String		Mi_PROPERTY_EDITABLE_NAME	= "editable";
	String		Mi_PROPERTY_VIEWABLE_NAME	= "viewable";
	String		Mi_PROPERTY_REQUIRED_NAME	= "required";
	String		Mi_PROPERTY_TOOL_HINT_NAME	= "tool-hint";
	String		Mi_PROPERTY_STATUS_HELP_NAME	= "status-help";
	String		Mi_PROPERTY_DIALOG_HELP_NAME	= "dialog-help";
	String		Mi_PROPERTY_UNITS_NAME		= "units";
	String		Mi_PROPERTY_VALUES_IGNORE_CASE_NAME= "ignore-case";
	String		Mi_PROPERTY_DISPLAY_VALUES_IGNORE_CASE_NAME= "display-values-ignore-case";
	String		Mi_PROPERTY_EXCLUDED_VALUES_NAME= "exclude";
	String		Mi_PROPERTY_INCLUDED_VALUES_NAME= "include";
	String		Mi_PROPERTY_DISPLAY_PRIORITY_NAME= "display-priority";

	String		Mi_RAW_XML_ELEMENT_CDATA	= "__element_cdata";
	String		Mi_OUTPUT_PROPERTY_DESCRIPTIONS_TO_XML	= "__ouput__descriptions";

			/**------------------------------------------------------
	 		 * Types of properties
			 *------------------------------------------------------*/
	int		Mi_ENUM_TYPE				= 1;
	int		Mi_INTEGER_TYPE				= 2;
	int		Mi_DOUBLE_TYPE				= 3;
	int		Mi_BOOLEAN_TYPE				= 4;
	int		Mi_OBJECT_TYPE				= 5;
	int		Mi_STRING_TYPE				= 6;
	int		Mi_COLOR_TYPE				= 7;
	int		Mi_POSITIVE_INTEGER_TYPE		= 8;
	int		Mi_POSITIVE_DOUBLE_TYPE			= 9;
	int		Mi_FONT_NAME_TYPE			= 10;
	int		Mi_FILE_NAME_TYPE			= 11;
	int		Mi_TEXT_TYPE				= 12;

	String		Mi_ENUM_TYPE_NAME			= "Enum";
	String		Mi_INTEGER_TYPE_NAME			= "Integer";
	String		Mi_DOUBLE_TYPE_NAME			= "Double";
	String		Mi_BOOLEAN_TYPE_NAME			= "Boolean";
	String		Mi_OBJECT_TYPE_NAME			= "Object";
	String		Mi_STRING_TYPE_NAME			= "String";
	String		Mi_COLOR_TYPE_NAME			= "Color";
	String		Mi_POSITIVE_INTEGER_TYPE_NAME		= "PositiveInteger";
	String		Mi_POSITIVE_DOUBLE_TYPE_NAME		= "PositiveDouble";
	String		Mi_FONT_NAME_TYPE_NAME			= "Font";
	String		Mi_FILE_NAME_TYPE_NAME			= "Filename";
	// defined in MiiNames, though in lowercase String		Mi_TEXT_TYPE_NAME			= "Text";

	String[]	propertyTypeNames 		=
		{
		"<Unknown>",
		Mi_ENUM_TYPE_NAME,
		Mi_INTEGER_TYPE_NAME,
		Mi_DOUBLE_TYPE_NAME,
		Mi_BOOLEAN_TYPE_NAME,
		Mi_OBJECT_TYPE_NAME,
		Mi_STRING_TYPE_NAME,
		Mi_COLOR_TYPE_NAME,
		Mi_POSITIVE_INTEGER_TYPE_NAME,
		Mi_POSITIVE_DOUBLE_TYPE_NAME,
		Mi_FONT_NAME_TYPE_NAME,
		Mi_FILE_NAME_TYPE_NAME,
		MiiNames.Mi_TEXT_TYPE_NAME,
		};
	boolean[]	propertyTypesAreNumeric		=
		{
		false,
		false,
		true,
		true,
		false,
		false,
		false,
		false,
		true,
		true,
		false,
		false,
		false,
		};

	String		Mi_ADDITIONAL_PERSIST_DEFAULT_VALUE_OF_PROPERTY_NAME	= "save-default";

			/**------------------------------------------------------
	 		 * Geometric Types of Properties
			 *------------------------------------------------------*/
	int		Mi_X_COORD_TYPE				= 1;
	int		Mi_X_DISTANCE_TYPE			= 2;
	int		Mi_Y_COORD_TYPE				= 3;
	int		Mi_Y_DISTANCE_TYPE			= 4;
	int		Mi_XY_COORD_ARRAY_TYPE			= 5;


String	Mi_GENERIC_PROPERTY_VALIDATION_ERROR_MSG	= "Mi_GENERIC_PROPERTY_VALIDATION_ERROR_MSG";
String	Mi_VALUE_NOT_A_BOOLEAN_VALIDATION_ERROR_MSG	= "Mi_VALUE_NOT_A_BOOLEAN_VALIDATION_ERROR_MSG";
String	Mi_VALUE_NOT_AN_INTEGER_VALIDATION_ERROR_MSG	= "Mi_VALUE_NOT_AN_INTEGER_VALIDATION_ERROR_MSG";
String	Mi_VALUE_NOT_A_DOUBLE_VALIDATION_ERROR_MSG	= "Mi_VALUE_NOT_A_DOUBLE_VALIDATION_ERROR_MSG";
String	Mi_VALUE_NOT_A_COLOR_VALIDATION_ERROR_MSG	= "Mi_VALUE_NOT_A_COLOR_VALIDATION_ERROR_MSG";
String	Mi_CLASS_NAME_VALUE_NOT_FOUND_ERROR_MSG		= "Mi_CLASS_NAME_VALUE_NOT_FOUND_ERROR_MSG";
String	Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR_MSG	= "Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR_MSG";
String	Mi_VALUE_IN_EXCLUDED_SET_VALIDATION_ERROR_MSG	= "Mi_VALUE_IN_EXCLUDED_SET_VALIDATION_ERROR";
String	Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR_MSG	= "Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR_MSG";
String	Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR_MSG	= "Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR_MSG";
String	Mi_VALUE_MUST_BE_SPECIFIED_VALIDATION_ERROR_MSG	= "Mi_VALUE_MUST_BE_SPECIFIED_VALIDATION_ERROR_MSG";
String	Mi_CHAR_NOT_NUMERIC_OR_SIGN_VALIDATION_ERROR_MSG= "Mi_CHAR_NOT_NUMERIC_OR_SIGN_VALIDATION_ERROR";
String	Mi_CHAR_NOT_NUMERIC_VALIDATION_ERROR_MSG	= "Mi_CHAR_NOT_NUMERIC_VALIDATION_ERROR";
String	Mi_CHAR_NOT_HEXIDECIMAL_VALIDATION_ERROR_MSG	= "Mi_CHAR_NOT_HEXIDECIMAL_VALIDATION_ERROR";
String	Mi_CHAR_NOT_UPPER_CASE_VALIDATION_ERROR_MSG	= "Mi_CHAR_NOT_UPPER_CASE_VALIDATION_ERROR";
String	Mi_CHAR_NOT_LOWER_CASE_VALIDATION_ERROR_MSG	= "Mi_CHAR_NOT_LOWER_CASE_VALIDATION_ERROR";
String	Mi_CHAR_NOT_ALPHABETIC_VALIDATION_ERROR_MSG	= "Mi_CHAR_NOT_ALPHABETIC_VALIDATION_ERROR";
String	Mi_CHAR_NOT_ALPHANUMERIC_VALIDATION_ERROR_MSG	= "Mi_CHAR_NOT_ALPHANUMERIC_VALIDATION_ERROR";


	int	Mi_GENERIC_PROPERTY_VALIDATION_ERROR		= 0;
	int	Mi_VALUE_NOT_A_BOOLEAN_VALIDATION_ERROR		= 1;
	int	Mi_VALUE_NOT_AN_INTEGER_VALIDATION_ERROR	= 2;
	int	Mi_VALUE_NOT_A_DOUBLE_VALIDATION_ERROR		= 3;
	int	Mi_VALUE_NOT_A_COLOR_VALIDATION_ERROR		= 4;
	int	Mi_CLASS_NAME_VALUE_NOT_FOUND_ERROR		= 5;
	int	Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR	= 6;
	int	Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR		= 7;
	int	Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR		= 8;
	int	Mi_VALUE_MUST_BE_SPECIFIED_VALIDATION_ERROR	= 9;
	int	Mi_VALUE_IN_EXCLUDED_SET_VALIDATION_ERROR	= 10;

	Pair[]	validationErrorMsgs =
	{
	new Pair(Mi_GENERIC_PROPERTY_VALIDATION_ERROR_MSG	, "Property validation error"),
	new Pair(Mi_VALUE_NOT_A_BOOLEAN_VALIDATION_ERROR_MSG	, "\"%s\" must be \"true\" or \"false\""),
	new Pair(Mi_VALUE_NOT_AN_INTEGER_VALIDATION_ERROR_MSG	, "\"%s\" must be a whole number"),
	new Pair(Mi_VALUE_NOT_A_DOUBLE_VALIDATION_ERROR_MSG	, "\"%s\" must be a number"),
	new Pair(Mi_VALUE_NOT_A_COLOR_VALIDATION_ERROR_MSG	, "\"%s\" must be the name of a color"),
	new Pair(Mi_CLASS_NAME_VALUE_NOT_FOUND_ERROR_MSG	, "\"%s\" must be the name of a Java class"),
	new Pair(Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR_MSG	, "\"%1\" must be one of \"%2\""),
	new Pair(Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR_MSG, "\"%1\" must be greater or equal to \"%2\""),
	new Pair(Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR_MSG, "\"%1\" must be less or equal to \"%2\""),
	new Pair(Mi_VALUE_MUST_BE_SPECIFIED_VALIDATION_ERROR_MSG, "A value must be specified"),
	new Pair(Mi_VALUE_IN_EXCLUDED_SET_VALIDATION_ERROR_MSG	, "\"%1\" must not be one of \"%2\""),
	new Pair(Mi_CHAR_NOT_NUMERIC_OR_SIGN_VALIDATION_ERROR_MSG, "Character: \'%1\' is not a number or sign"),
	new Pair(Mi_CHAR_NOT_NUMERIC_VALIDATION_ERROR_MSG	, "Character: \'%1\' 2 is not a number"),
	new Pair(Mi_CHAR_NOT_HEXIDECIMAL_VALIDATION_ERROR_MSG	, "Character: \'%1\' is not hexidecimal"),
	new Pair(Mi_CHAR_NOT_UPPER_CASE_VALIDATION_ERROR_MSG	, "Character: \'%1\' is not upper case"),
	new Pair(Mi_CHAR_NOT_LOWER_CASE_VALIDATION_ERROR_MSG	, "Character: \'%1\' is not lower case"),
	new Pair(Mi_CHAR_NOT_ALPHABETIC_VALIDATION_ERROR_MSG	, "Character: \'%1\' is not a letter"),
	new Pair(Mi_CHAR_NOT_ALPHANUMERIC_VALIDATION_ERROR_MSG	, "Character: \'%1\' is not a letter or number"),
	};

	String[] errorMessages =
		{
		Mi_GENERIC_PROPERTY_VALIDATION_ERROR_MSG,
		Mi_VALUE_NOT_A_BOOLEAN_VALIDATION_ERROR_MSG,
		Mi_VALUE_NOT_AN_INTEGER_VALIDATION_ERROR_MSG,
		Mi_VALUE_NOT_A_DOUBLE_VALIDATION_ERROR_MSG,
		Mi_VALUE_NOT_A_COLOR_VALIDATION_ERROR_MSG,
		Mi_CLASS_NAME_VALUE_NOT_FOUND_ERROR_MSG,
		Mi_VALUE_NOT_IN_VALID_SET_VALIDATION_ERROR_MSG,
		Mi_VALUE_BELOW_MINIMUM_VALIDATION_ERROR_MSG,
		Mi_VALUE_ABOVE_MAXIMUM_VALIDATION_ERROR_MSG,
		Mi_VALUE_MUST_BE_SPECIFIED_VALIDATION_ERROR_MSG,
		Mi_VALUE_IN_EXCLUDED_SET_VALIDATION_ERROR_MSG,
		};


	}

