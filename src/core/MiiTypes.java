
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
import java.awt.Cursor; 
import java.awt.Color; 

/**----------------------------------------------------------------------------------------------
 * This interface defines a number of constants used by the core
 * graphics system and users of the core graphics system.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiTypes
	{
			/**------------------------------------------------------
	 		 * Universally used contants.
			 *------------------------------------------------------*/
	String		Mi_NULL_VALUE_NAME			= "(null)";
	int		Mi_NONE					= 0;
	String		Mi_NONE_NAME				= "None";

			/**------------------------------------------------------
	 		 * Relative directions
			 *------------------------------------------------------*/
	int		Mi_UP					= 1;
	int		Mi_DOWN					= 2;
	int		Mi_RIGHT				= 4;
	int		Mi_LEFT					= 8;
	int		Mi_ALL_ORTHO_DIRECTIONS			= 15;
	int		Mi_ALL_DIRECTIONS			= 255;

			/**------------------------------------------------------
	 		 * Relative positions
			 *------------------------------------------------------*/
	int		Mi_ABOVE				= 0;
	int		Mi_BELOW				= 1;
	int		Mi_TO_RIGHT				= 2;
	int		Mi_TO_LEFT				= 3;
	int		Mi_TOP					= 4;
	int		Mi_BOTTOM				= 5;
	int		Mi_FAR_RIGHT				= 6;
	int		Mi_FAR_LEFT				= 7;

			/**------------------------------------------------------
	 		 * Orientations
			 *------------------------------------------------------*/
	int		Mi_VERTICAL				= 1;
	int		Mi_HORIZONTAL				= 2;

			/**------------------------------------------------------
	 		 * Handler return values
			 *------------------------------------------------------*/
	int		Mi_PROPOGATE				= 1;
	int		Mi_CONSUME				= 2;
	int		Mi_VETO					= 3;

			/**------------------------------------------------------
	 		 * Basic contraints of geometric values.
			 *------------------------------------------------------*/
	MiCoord 	Mi_MAX_COORD_VALUE 			= MiCoordBuiltInType.MAX_VALUE/2;
	MiCoord 	Mi_MIN_COORD_VALUE 			= -MiCoordBuiltInType.MAX_VALUE/2;
	MiDistance 	Mi_MAX_DISTANCE_VALUE 			= MiCoordBuiltInType.MAX_VALUE/2;
	MiDistance 	Mi_MIN_DISTANCE_VALUE 			= 0;


			/**------------------------------------------------------
	 		 * The names of True and False (these are typcally assumed
			 * to be case-insensitive).
			 *------------------------------------------------------*/
	String		Mi_TRUE_NAME				= "true";
	String		Mi_FALSE_NAME				= "false";

			/**------------------------------------------------------
	 		 * The transparent color name and value.
			 *------------------------------------------------------*/
	String		Mi_TRANSPARENT_COLOR_NAME		= "transparent";
	Color		Mi_TRANSPARENT_COLOR			= null;

			/**------------------------------------------------------
	 		 * Supported cursor appearances.
			 *------------------------------------------------------*/
	int		Mi_DEFAULT_CURSOR   			= Cursor.DEFAULT_CURSOR;
	int		Mi_CROSSHAIR_CURSOR 			= Cursor.CROSSHAIR_CURSOR;
	int		Mi_TEXT_CURSOR 	 			= Cursor.TEXT_CURSOR;
	int		Mi_WAIT_CURSOR	 			= Cursor.WAIT_CURSOR;
	int		Mi_SW_RESIZE_CURSOR		 	= Cursor.SW_RESIZE_CURSOR;
	int		Mi_SE_RESIZE_CURSOR		 	= Cursor.SE_RESIZE_CURSOR;
	int		Mi_NW_RESIZE_CURSOR			= Cursor.NW_RESIZE_CURSOR;
	int		Mi_NE_RESIZE_CURSOR		 	= Cursor.NE_RESIZE_CURSOR;
	int		Mi_N_RESIZE_CURSOR 			= Cursor.N_RESIZE_CURSOR;
	int		Mi_S_RESIZE_CURSOR 			= Cursor.S_RESIZE_CURSOR;
	int		Mi_W_RESIZE_CURSOR	 		= Cursor.W_RESIZE_CURSOR;
	int		Mi_E_RESIZE_CURSOR			= Cursor.E_RESIZE_CURSOR;
	int		Mi_HAND_CURSOR				= Cursor.HAND_CURSOR;
	int		Mi_MOVE_CURSOR				= Cursor.MOVE_CURSOR;	

			/**------------------------------------------------------
	 		 * The default cursor but not overrideable by parts below
			 *------------------------------------------------------*/
	int		Mi_STANDARD_CURSOR			= 0x1000;
    
			/**------------------------------------------------------
	 		 * Standard geometric locations. Other locations are point
			 * numbers (most useful for MiMultiPointShapes) and special
			 * locations as specified with MiConnectionPoints.
			 *------------------------------------------------------*/
	int		Mi_MIN_BUILTIN_LOCATION			= 0x10000;
	int		Mi_MIN_COMMON_LOCATION			= 0x10000;

	int		Mi_CENTER_LOCATION			= 0 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LEFT_LOCATION			= 1 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_RIGHT_LOCATION			= 2 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_BOTTOM_LOCATION			= 3 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_TOP_LOCATION				= 4 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LOWER_LEFT_LOCATION			= 5 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LOWER_RIGHT_LOCATION			= 6 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_UPPER_LEFT_LOCATION			= 7 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_UPPER_RIGHT_LOCATION			= 8 + Mi_MIN_BUILTIN_LOCATION;

	int		Mi_MAX_COMMON_LOCATION			= Mi_UPPER_RIGHT_LOCATION;

	int		Mi_START_LOCATION			= 9 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_END_LOCATION				= 10 + Mi_MIN_BUILTIN_LOCATION;

	int		Mi_OUTSIDE_LEFT_LOCATION		= 11 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_OUTSIDE_RIGHT_LOCATION		= 12 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_OUTSIDE_BOTTOM_LOCATION		= 13 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_OUTSIDE_TOP_LOCATION			= 14 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_OUTSIDE_LOWER_LEFT_LOCATION		= 15 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_OUTSIDE_LOWER_RIGHT_LOCATION		= 16 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_OUTSIDE_UPPER_LEFT_LOCATION		= 17 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_OUTSIDE_UPPER_RIGHT_LOCATION		= 18 + Mi_MIN_BUILTIN_LOCATION;

	int		Mi_INSIDE_LEFT_LOCATION			= 19 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_INSIDE_RIGHT_LOCATION		= 20 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_INSIDE_BOTTOM_LOCATION		= 21 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_INSIDE_TOP_LOCATION			= 22 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_INSIDE_LOWER_LEFT_LOCATION		= 23 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_INSIDE_LOWER_RIGHT_LOCATION		= 24 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_INSIDE_UPPER_LEFT_LOCATION		= 25 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_INSIDE_UPPER_RIGHT_LOCATION		= 26 + Mi_MIN_BUILTIN_LOCATION;

	int		Mi_WNW_LOCATION				= 27 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_WSW_LOCATION				= 28 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_ENE_LOCATION				= 29 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_ESE_LOCATION				= 30 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_NWN_LOCATION				= 31 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_NEN_LOCATION				= 32 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_SWS_LOCATION				= 33 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_SES_LOCATION				= 34 + Mi_MIN_BUILTIN_LOCATION;

	int		Mi_LINE_CENTER_LOCATION			= 1000 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION	= 1001 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION	= 1002 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_START_LOCATION			= 1003 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_START_TOP_OR_RIGHT_LOCATION	= 1004 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION	= 1005 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_END_LOCATION			= 1006 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_END_TOP_OR_RIGHT_LOCATION	= 1007 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION	= 1008 + Mi_MIN_BUILTIN_LOCATION;

	// Fixed locations relative to a +45 degree angled line
	int		Mi_LINE_START_TOP_LOCATION		= 1009 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_START_BOTTOM_LOCATION		= 1010 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_END_TOP_LOCATION		= 1011 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_END_BOTTOM_LOCATION		= 1012 + Mi_MIN_BUILTIN_LOCATION;

	int		Mi_LINE_AT_START_LOCATION		= 1013 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_LINE_AT_END_LOCATION			= 1014 + Mi_MIN_BUILTIN_LOCATION;

	int		Mi_MIN_BUILTIN_LINE_LOCATION		= Mi_LINE_CENTER_LOCATION;
	int		Mi_MAX_BUILTIN_LINE_LOCATION		= Mi_LINE_AT_END_LOCATION;


	int		Mi_ALONG_LOCATION			= 2000 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_SURROUND_LOCATION			= 2001 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_DEFAULT_LOCATION			= 2002 + Mi_MIN_BUILTIN_LOCATION;
	int		Mi_ALL_LOCATIONS			= 2003 + Mi_MIN_BUILTIN_LOCATION;

	int		Mi_MAX_BUILTIN_LOCATION			= 0x1ffff;

	int		Mi_MIN_CUSTOM_LOCATION			= 0x20000;
	int		Mi_MAX_CUSTOM_LOCATION			= 0x30000;

			/**------------------------------------------------------
	 		 * The special point number that always refers to the last
			 * point (usually in a MiMultiPointShape).
			 *------------------------------------------------------*/
	int		Mi_LAST_POINT_NUMBER			= -1;
	int		Mi_INVALID_POINT_NUMBER			= -3;

			/**------------------------------------------------------
	 		 * Scrollbar visibility contraints.
			 *------------------------------------------------------*/
	int		Mi_DISPLAY_ALWAYS			= 1;
	int		Mi_DISPLAY_NEVER			= 2;
	int		Mi_DISPLAY_AS_NEEDED			= 3;

			/**------------------------------------------------------
	 		 * Values of the write mode attribute.
			 *------------------------------------------------------*/
	int 		Mi_COPY_WRITEMODE  			= 0;
	int 		Mi_XOR_WRITEMODE  			= 1;

			/**------------------------------------------------------
	 		 * Values of the line style attribute.
			 *------------------------------------------------------*/
	int		Mi_SOLID_LINE_STYLE			= 0;
	int		Mi_DASHED_LINE_STYLE			= 1;
	int		Mi_DOUBLE_DASHED_LINE_STYLE		= 2;
	int		Mi_SPARSE_DOTTED_LINE_STYLE		= 3;
	int		Mi_DOTTED_LINE_STYLE			= 4;

			/**------------------------------------------------------
	 		 * Values of the border look attribute.
			 *------------------------------------------------------*/
	int 		Mi_FLAT_BORDER_LOOK			= 1;
	int 		Mi_RAISED_BORDER_LOOK			= 2;
	int 		Mi_INDENTED_BORDER_LOOK			= 3;
	int 		Mi_GROOVE_BORDER_LOOK			= 4;
	int 		Mi_RIDGE_BORDER_LOOK			= 5;
	int 		Mi_OUTLINED_RAISED_BORDER_LOOK		= 6;
	int 		Mi_OUTLINED_INDENTED_BORDER_LOOK	= 7;
	int 		Mi_INLINED_RAISED_BORDER_LOOK		= 8;
	int 		Mi_INLINED_INDENTED_BORDER_LOOK		= 9;
	int 		Mi_SQUARE_RAISED_BORDER_LOOK		= 10;

			/**------------------------------------------------------
	 		 * Values of the line end style attribute.
			 *------------------------------------------------------*/
	int		Mi_FILLED_TRIANGLE_LINE_END_STYLE	= 1;
	int		Mi_STROKED_ARROW_LINE_END_STYLE		= 2;
	int		Mi_FILLED_ARROW_LINE_END_STYLE		= 3;
	int		Mi_FILLED_CIRCLE_LINE_END_STYLE		= 4;
	int		Mi_FILLED_SQUARE_LINE_END_STYLE		= 5;
	int		Mi_TRIANGLE_VIA_LINE_END_STYLE		= 6;
	int		Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE	= 7;
	int		Mi_CIRCLE_VIA_LINE_END_STYLE		= 8;
	int		Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE	= 9;
	int		Mi_SQUARE_VIA_LINE_END_STYLE		= 10;
	int		Mi_FILLED_SQUARE_VIA_LINE_END_STYLE	= 11;
	int		Mi_TRIANGLE_LINE_END_STYLE		= 12;
	int		Mi_CIRCLE_LINE_END_STYLE		= 13;
	int		Mi_SQUARE_LINE_END_STYLE		= 14;
	int		Mi_DIAMOND_LINE_END_STYLE		= 15;
	int		Mi_FILLED_DIAMOND_LINE_END_STYLE	= 16;
	int		Mi_3FEATHER_LINE_END_STYLE		= 17;
	int		Mi_2FEATHER_LINE_END_STYLE		= 18;

			/**------------------------------------------------------
	 		 * Various layout justifications.
			 *------------------------------------------------------*/
	int		Mi_CENTER_JUSTIFIED			= 1;
	int		Mi_LEFT_JUSTIFIED			= 2;
	int		Mi_RIGHT_JUSTIFIED			= 3;
	int		Mi_BOTTOM_JUSTIFIED			= 4;
	int		Mi_TOP_JUSTIFIED			= 5;
	int		Mi_JUSTIFIED				= 6;

			/**------------------------------------------------------
	 		 * Various layout sizings.
			 *------------------------------------------------------*/
	int		Mi_SAME_SIZE				= 1;
	int		Mi_EXPAND_TO_FILL			= 2;



			/**------------------------------------------------------
	 		 * Various page orientations.
			 *------------------------------------------------------*/
	String		Mi_LANDSCAPE_NAME			= "Landscape";
	String		Mi_PORTRAIT_NAME			= "Portrait";

			/**------------------------------------------------------
	 		 * Various low-level native system components to be used
			 * with MiNativeWindow and it's decendants
			 *------------------------------------------------------*/
	MiJDKAPIComponentType	Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE 
							= new MiJDKAPIComponentType("AWT Heavyweight Component");
	MiJDKAPIComponentType	Mi_AWT_1_0_2_HEAVYWEIGHT_COMPONENT_TYPE 
							= new MiJDKAPIComponentType("AWT 1.0.2 Heavyweight Component");
	MiJDKAPIComponentType	Mi_AWT_1_1_HEAVYWEIGHT_COMPONENT_TYPE 
							= new MiJDKAPIComponentType("AWT 1.1 Heavyweight Component");
	MiJDKAPIComponentType	Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE 
							= new MiJDKAPIComponentType("Swing Lightweight Component");

	}




