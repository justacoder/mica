

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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiAttributeTypes extends MiiTypes
	{
	// ---------------------------------------------------------------
	// These are the attribute indexes.
	// ---------------------------------------------------------------
	int		Mi_START_OBJECT_INDEX		= 0;
	int		Mi_START_IMAGE_INDEX		= 0;
	int		Mi_BACKGROUND_IMAGE		= Mi_START_IMAGE_INDEX + 0;
	int		Mi_BACKGROUND_TILE		= Mi_START_IMAGE_INDEX + 1;
	int		Mi_NUM_IMAGE_INDEXES		= 2;

	int		Mi_START_FONT_INDEX		= Mi_START_IMAGE_INDEX + Mi_NUM_IMAGE_INDEXES;
	int		Mi_FONT				= Mi_START_FONT_INDEX + 0;
	int		Mi_NUM_FONT_INDEXES		= 1;
	
	int		Mi_START_HELPINFO_INDEX		= Mi_START_FONT_INDEX + Mi_NUM_FONT_INDEXES;
	int		Mi_TOOL_HINT_HELP		= Mi_START_HELPINFO_INDEX + 0;
	int		Mi_BALLOON_HELP			= Mi_START_HELPINFO_INDEX + 1;
	int		Mi_STATUS_HELP			= Mi_START_HELPINFO_INDEX + 2;
	int		Mi_DIALOG_HELP			= Mi_START_HELPINFO_INDEX + 3;
	int		Mi_NUM_HELPINFO_INDEXES		= 4;

	int		Mi_START_RENDERER_INDEX		= Mi_START_HELPINFO_INDEX + Mi_NUM_HELPINFO_INDEXES;
	int		Mi_SHADOW_RENDERER		= Mi_START_RENDERER_INDEX + 0;
	int		Mi_BEFORE_RENDERER		= Mi_START_RENDERER_INDEX + 1;
	int		Mi_AFTER_RENDERER		= Mi_START_RENDERER_INDEX + 2;
	int		Mi_LINE_ENDS_RENDERER		= Mi_START_RENDERER_INDEX + 3;
	int		Mi_NUM_RENDERER_INDEXES		= 4;
	
	int		Mi_START_PIXEL_RENDERER_INDEX	= Mi_START_RENDERER_INDEX + Mi_NUM_RENDERER_INDEXES;
	int		Mi_BACKGROUND_RENDERER		= Mi_START_PIXEL_RENDERER_INDEX + 0;
	int		Mi_BORDER_RENDERER		= Mi_START_PIXEL_RENDERER_INDEX + 1;
	int		Mi_NUM_PIXEL_RENDERER_INDEXES	= 2;

	int		Mi_START_ANIMATOR_INDEX		= Mi_START_PIXEL_RENDERER_INDEX + Mi_NUM_PIXEL_RENDERER_INDEXES;
	int		Mi_VISIBILITY_ANIMATOR		= Mi_START_ANIMATOR_INDEX + 0;
	int		Mi_NUM_ANIMATOR_INDEXES		= 1;

	int		Mi_START_MENU_INDEX		= Mi_START_ANIMATOR_INDEX + Mi_NUM_ANIMATOR_INDEXES;
	int		Mi_CONTEXT_MENU			= Mi_START_MENU_INDEX + 0;
	int		Mi_NUM_MENU_INDEXES		= 1;


	int		Mi_START_COLOR_INDEX		= Mi_START_MENU_INDEX + Mi_NUM_MENU_INDEXES;
	int		Mi_COLOR			= Mi_START_COLOR_INDEX + 0;
	int		Mi_BACKGROUND_COLOR		= Mi_START_COLOR_INDEX + 1;
	int		Mi_LIGHT_COLOR			= Mi_START_COLOR_INDEX + 2;
	int		Mi_WHITE_COLOR			= Mi_START_COLOR_INDEX + 3;
	int		Mi_DARK_COLOR			= Mi_START_COLOR_INDEX + 4;
	int		Mi_BLACK_COLOR			= Mi_START_COLOR_INDEX + 5;
	int		Mi_BORDER_HILITE_COLOR		= Mi_START_COLOR_INDEX + 6;
	int		Mi_SHADOW_COLOR			= Mi_START_COLOR_INDEX + 7;
	int		Mi_XOR_COLOR			= Mi_START_COLOR_INDEX + 8;
	int		Mi_NUM_COLOR_INDEXES		= 9;
	int		Mi_NUM_OBJECT_INDEXES		= Mi_START_COLOR_INDEX + Mi_NUM_COLOR_INDEXES;

	int		Mi_START_INTEGER_INDEX		= Mi_START_COLOR_INDEX + Mi_NUM_COLOR_INDEXES;
	int		Mi_BORDER_LOOK			= Mi_START_INTEGER_INDEX + 0;
	int		Mi_LINE_STYLE			= Mi_START_INTEGER_INDEX + 1;
	int		Mi_LINE_START_STYLE		= Mi_START_INTEGER_INDEX + 2;
	int		Mi_LINE_END_STYLE		= Mi_START_INTEGER_INDEX + 3;
	int		Mi_WRITE_MODE			= Mi_START_INTEGER_INDEX + 4;
	int		Mi_CONTEXT_CURSOR		= Mi_START_INTEGER_INDEX + 5;
	int		Mi_ATTRIBUTE_LOCK_MASK		= Mi_START_INTEGER_INDEX + 6;
	int		Mi_ATTRIBUTE_PUBLIC_MASK	= Mi_START_INTEGER_INDEX + 7;
	int		Mi_SHADOW_DIRECTION		= Mi_START_INTEGER_INDEX + 8;
	int		Mi_SHADOW_STYLE			= Mi_START_INTEGER_INDEX + 9;
	int		Mi_FONT_HORIZONTAL_JUSTIFICATION= Mi_START_INTEGER_INDEX + 10;
	int		Mi_FONT_VERTICAL_JUSTIFICATION	= Mi_START_INTEGER_INDEX + 11;
	int		Mi_NUM_INTEGER_INDEXES		= 12;

	int		Mi_START_DOUBLE_INDEX		= Mi_START_INTEGER_INDEX + Mi_NUM_INTEGER_INDEXES;
	int		Mi_MINIMUM_WIDTH		= Mi_START_DOUBLE_INDEX + 0;
	int		Mi_MINIMUM_HEIGHT		= Mi_START_DOUBLE_INDEX + 1;
	int		Mi_MAXIMUM_WIDTH		= Mi_START_DOUBLE_INDEX + 2;
	int		Mi_MAXIMUM_HEIGHT		= Mi_START_DOUBLE_INDEX + 3;
	int		Mi_BORDER_HILITE_WIDTH		= Mi_START_DOUBLE_INDEX + 4;
	int		Mi_SHADOW_LENGTH		= Mi_START_DOUBLE_INDEX + 5;
	int		Mi_LINE_WIDTH			= Mi_START_DOUBLE_INDEX + 6;
	int		Mi_LINE_START_SIZE		= Mi_START_DOUBLE_INDEX + 7;
	int		Mi_LINE_END_SIZE		= Mi_START_DOUBLE_INDEX + 8;
	int		Mi_NUM_DOUBLE_INDEXES		= 9;

	int		Mi_START_BOOLEAN_INDEX		= Mi_START_DOUBLE_INDEX + Mi_NUM_DOUBLE_INDEXES;
	int		Mi_DELETABLE			= Mi_START_BOOLEAN_INDEX + 0;
	int		Mi_MOVABLE			= Mi_START_BOOLEAN_INDEX + 1;
	int		Mi_COPYABLE			= Mi_START_BOOLEAN_INDEX + 2;
	int		Mi_SELECTABLE			= Mi_START_BOOLEAN_INDEX + 3;
	int		Mi_FIXED_WIDTH			= Mi_START_BOOLEAN_INDEX + 4;
	int		Mi_FIXED_HEIGHT			= Mi_START_BOOLEAN_INDEX + 5;
	int		Mi_FIXED_ASPECT_RATIO		= Mi_START_BOOLEAN_INDEX + 6;
	int		Mi_PICKABLE			= Mi_START_BOOLEAN_INDEX + 7;
	int		Mi_UNGROUPABLE			= Mi_START_BOOLEAN_INDEX + 8;
	int		Mi_CONNECTABLE			= Mi_START_BOOLEAN_INDEX + 9;
	int		Mi_HIDDEN			= Mi_START_BOOLEAN_INDEX + 10;
	int		Mi_DRAG_AND_DROP_SOURCE		= Mi_START_BOOLEAN_INDEX + 11;
	int		Mi_DRAG_AND_DROP_TARGET		= Mi_START_BOOLEAN_INDEX + 12;
	int		Mi_ACCEPTS_MOUSE_FOCUS		= Mi_START_BOOLEAN_INDEX + 13;
	int		Mi_ACCEPTS_KEYBOARD_FOCUS	= Mi_START_BOOLEAN_INDEX + 14;
	int		Mi_ACCEPTS_ENTER_KEY_FOCUS	= Mi_START_BOOLEAN_INDEX + 15;
	int		Mi_ACCEPTS_TAB_KEYS		= Mi_START_BOOLEAN_INDEX + 16;
	int		Mi_HAS_BORDER_HILITE		= Mi_START_BOOLEAN_INDEX + 17;
	int		Mi_HAS_SHADOW			= Mi_START_BOOLEAN_INDEX + 18;
	int		Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH= Mi_START_BOOLEAN_INDEX + 19;
	int		Mi_PRINTABLE			= Mi_START_BOOLEAN_INDEX + 20;
	int		Mi_SAVABLE			= Mi_START_BOOLEAN_INDEX + 21;
	int		Mi_PICKABLE_WHEN_TRANSPARENT	= Mi_START_BOOLEAN_INDEX + 22;
	int		Mi_FILLED			= Mi_START_BOOLEAN_INDEX + 23;
	int		Mi_SNAPPABLE			= Mi_START_BOOLEAN_INDEX + 24;
	int		Mi_COPYABLE_AS_PART_OF_COPYABLE	= Mi_START_BOOLEAN_INDEX + 25;
	int		Mi_NUM_BOOLEAN_INDEXES		= 26;


	int		Mi_NUMBER_OF_ATTRIBUTES		= Mi_START_BOOLEAN_INDEX + Mi_NUM_BOOLEAN_INDEXES;

	// -----------------------------------------------------------------------
	// Attribute lock masks (up to 32 locks are possible)
	// -----------------------------------------------------------------------
	int		Mi_COLOR_ATTRIBUTE_MASK_BIT_NUMBER		= 0;
	int		Mi_BACKGROUND_COLOR_ATTRIBUTE_MASK_BIT_NUMBER	= 1;
	int		Mi_LINE_WIDTH_ATTRIBUTE_MASK_BIT_NUMBER		= 2;
	int		Mi_WRITE_MODE_ATTRIBUTE_MASK_BIT_NUMBER		= 3;
	int		Mi_FONT_ATTRIBUTE_MASK_BIT_NUMBER		= 4;
	int		Mi_FONT_SIZE_ATTRIBUTE_MASK_BIT_NUMBER		= 5;
	int		Mi_FONT_STYLE_ATTRIBUTE_MASK_BIT_NUMBER		= 6;
	int		Mi_FONT_JUSTIFICATION_ATTRIBUTE_MASK_BIT_NUMBER	= 7;
	int		Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT_NUMBER	= 8;
	int		Mi_SHADOW_COLOR_ATTRIBUTE_MASK_BIT_NUMBER	= 9;
	int		Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT_NUMBER	= 10;
	int		Mi_SHADOW_LENGTH_ATTRIBUTE_MASK_BIT_NUMBER	= 11;
	int		Mi_BORDER_LOOK_ATTRIBUTE_MASK_BIT_NUMBER	= 12;
	int		Mi_BORDER_COLORS_ATTRIBUTE_MASK_BIT_NUMBER	= 13;
	int		Mi_GRADIENT_STYLE_ATTRIBUTE_MASK_BIT_NUMBER	= 14;
	int		Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT_NUMBER		= 15;
	int		Mi_STATUS_HELP_ATTRIBUTE_MASK_BIT_NUMBER	= 16;
	int		Mi_PRINTABLE_ATTRIBUTE_MASK_BIT_NUMBER		= 17;
	int		Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT_NUMBER		= 18;

	int		Mi_COLOR_ATTRIBUTE_MASK_BIT		= 1 << 0;
	int		Mi_BACKGROUND_COLOR_ATTRIBUTE_MASK_BIT	= 1 << 1;
	int		Mi_LINE_WIDTH_ATTRIBUTE_MASK_BIT	= 1 << 2;
	int		Mi_WRITE_MODE_ATTRIBUTE_MASK_BIT	= 1 << 3;
	int		Mi_FONT_ATTRIBUTE_MASK_BIT		= 1 << 4;
	int		Mi_FONT_SIZE_ATTRIBUTE_MASK_BIT		= 1 << 5;
	int		Mi_FONT_STYLE_ATTRIBUTE_MASK_BIT	= 1 << 6;
	int		Mi_FONT_JUSTIFICATION_ATTRIBUTE_MASK_BIT= 1 << 7;
	int		Mi_SHADOW_STYLE_ATTRIBUTE_MASK_BIT	= 1 << 8;
	int		Mi_SHADOW_COLOR_ATTRIBUTE_MASK_BIT	= 1 << 9;
	int		Mi_SHADOW_DIRECTION_ATTRIBUTE_MASK_BIT	= 1 << 10;
	int		Mi_SHADOW_LENGTH_ATTRIBUTE_MASK_BIT	= 1 << 11;
	int		Mi_BORDER_LOOK_ATTRIBUTE_MASK_BIT	= 1 << 12;
	int		Mi_BORDER_COLORS_ATTRIBUTE_MASK_BIT	= 1 << 13;
	int		Mi_GRADIENT_STYLE_ATTRIBUTE_MASK_BIT	= 1 << 14;
	int		Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT		= 1 << 15;
	int		Mi_STATUS_HELP_ATTRIBUTE_MASK_BIT	= 1 << 16;
	int		Mi_PRINTABLE_ATTRIBUTE_MASK_BIT		= 1 << 17;
	int		Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT		= 1 << 18;

	// -----------------------------------------------------------------------
	// The table of attributes that affect the appearance of a MiPart
	// -----------------------------------------------------------------------
	int[]		appearanceAttributesTable =
		{
		Mi_BACKGROUND_IMAGE		,
		Mi_BACKGROUND_TILE		,

		Mi_SHADOW_RENDERER		,
		Mi_BEFORE_RENDERER		,
		Mi_AFTER_RENDERER		,
		Mi_LINE_ENDS_RENDERER		,

		Mi_BACKGROUND_RENDERER		,
		Mi_BORDER_RENDERER		,

		Mi_VISIBILITY_ANIMATOR		,

		Mi_COLOR			,
		Mi_BACKGROUND_COLOR		,
		Mi_LIGHT_COLOR			,
		Mi_WHITE_COLOR			,
		Mi_DARK_COLOR			,
		Mi_BLACK_COLOR			,
		Mi_BORDER_HILITE_COLOR		,
		Mi_SHADOW_COLOR			,
		Mi_XOR_COLOR			,

		Mi_BORDER_LOOK			,
		Mi_LINE_STYLE			,
		Mi_WRITE_MODE			,
		Mi_SHADOW_STYLE			,

		Mi_BORDER_HILITE_WIDTH		,
		Mi_SHADOW_LENGTH		,

		Mi_HIDDEN			,
		Mi_FILLED			,
		};

	// -----------------------------------------------------------------------
	// The table of attributes that affect the size of a MiPart
	// -----------------------------------------------------------------------
	int[]		geometricAttributesTable =
		{
		Mi_FONT					,
		Mi_LINE_ENDS_RENDERER			,
		Mi_LINE_START_STYLE			,
		Mi_LINE_END_STYLE			,
		Mi_LINE_WIDTH				,
		Mi_LINE_START_SIZE			,
		Mi_LINE_END_SIZE			,
		Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH	,
		Mi_HAS_BORDER_HILITE			,
		Mi_BORDER_HILITE_WIDTH			,
		Mi_HAS_SHADOW				,
		Mi_SHADOW_LENGTH			,
		Mi_SHADOW_DIRECTION			,
		Mi_FONT_HORIZONTAL_JUSTIFICATION	,
		Mi_FONT_VERTICAL_JUSTIFICATION
		};

	// -----------------------------------------------------------------------
	// The table of line end styles.
	// -----------------------------------------------------------------------
	int[]		lineEndStyles =
		{
		Mi_NONE					,
		Mi_FILLED_TRIANGLE_LINE_END_STYLE	,
		Mi_STROKED_ARROW_LINE_END_STYLE		,
		Mi_FILLED_ARROW_LINE_END_STYLE		,
		Mi_FILLED_CIRCLE_LINE_END_STYLE		,
		Mi_FILLED_SQUARE_LINE_END_STYLE		,
		Mi_TRIANGLE_VIA_LINE_END_STYLE		,
		Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE	,
		Mi_CIRCLE_VIA_LINE_END_STYLE		,
		Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE	,
		Mi_SQUARE_VIA_LINE_END_STYLE		,
		Mi_FILLED_SQUARE_VIA_LINE_END_STYLE	,
		Mi_TRIANGLE_LINE_END_STYLE		,
		Mi_CIRCLE_LINE_END_STYLE		,
		Mi_SQUARE_LINE_END_STYLE		,
		Mi_DIAMOND_LINE_END_STYLE		,
		Mi_FILLED_DIAMOND_LINE_END_STYLE	,
		Mi_3FEATHER_LINE_END_STYLE		,
		Mi_2FEATHER_LINE_END_STYLE		,
		};
	// -----------------------------------------------------------------------
	// The table of border looks.
	// -----------------------------------------------------------------------
	int[]		borderLooks =
		{
		Mi_NONE				,
		Mi_FLAT_BORDER_LOOK		,
		Mi_RAISED_BORDER_LOOK		,
		Mi_INDENTED_BORDER_LOOK		,
		Mi_GROOVE_BORDER_LOOK		,
		Mi_RIDGE_BORDER_LOOK		,
		Mi_OUTLINED_RAISED_BORDER_LOOK	,
		Mi_OUTLINED_INDENTED_BORDER_LOOK,
		Mi_INLINED_RAISED_BORDER_LOOK	,
		Mi_INLINED_INDENTED_BORDER_LOOK	,
		Mi_SQUARE_RAISED_BORDER_LOOK	,
		};

	}

