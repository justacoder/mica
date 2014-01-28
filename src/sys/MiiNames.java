
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
import com.swfm.mica.util.StringIntPair;

/**----------------------------------------------------------------------------------------------
 * This interface defines a number of names used by the core
 * graphics system and users of the core graphics system.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiNames extends MiiTypes
	{
	// ---------------------------------------------------------------
	// Shapes and Layouts
	// ---------------------------------------------------------------
	String		Mi_SIZE_ONLY_LAYOUT_TYPE_NAME		= "sizeOnly";
	String		Mi_ROW_LAYOUT_TYPE_NAME			= "row";
	String		Mi_COLUMN_LAYOUT_TYPE_NAME		= "column";
	String		Mi_GRID_LAYOUT_TYPE_NAME		= "grid";
	String		Mi_STAR_GRAPH_LAYOUT_TYPE_NAME		= "star";
	String		Mi_UNDIRECTED_GRAPH_LAYOUT_TYPE_NAME	= "undirectedGraph";
	String		Mi_OUTLINE_GRAPH_LAYOUT_TYPE_NAME	= "outline";
	String		Mi_TREE_GRAPH_LAYOUT_TYPE_NAME		= "tree";
	String		Mi_RING_GRAPH_LAYOUT_TYPE_NAME		= "ring";
	String		Mi_2DMESH_GRAPH_LAYOUT_TYPE_NAME	= "2DMesh";
	String		Mi_LINE_GRAPH_LAYOUT_TYPE_NAME		= "lineGraph";
	String		Mi_CROSSBAR_GRAPH_LAYOUT_TYPE_NAME	= "crossBar";
	String		Mi_OMEGA_GRAPH_LAYOUT_TYPE_NAME		= "omega";
	String		Mi_LAYOUT_TEMPLATE_NAME			= "template";
	String		Mi_ATTACHMENT_TYPE_NAME			= "attachment";

	// ---------------------------------------------------------------
	// Types 
	// ---------------------------------------------------------------
	String		Mi_REGULAR_POLYGON_TYPE_NAME		= "regularPolygon";
	String		Mi_POLYGON_TYPE_NAME			= "polygon";
	String		Mi_RECTANGLE_TYPE_NAME			= "rectangle";
	String		Mi_ROUND_RECTANGLE_TYPE_NAME		= "roundRectangle";
	String		Mi_CIRCLE_TYPE_NAME			= "circle";
	String		Mi_LINE_TYPE_NAME			= "line";
	String		Mi_ARC_TYPE_NAME			= "arc";
	String		Mi_ELLIPTICAL_ARC_TYPE_NAME		= "ellipticalArc";
	String		Mi_3_PT_ARC_TYPE_NAME			= "threePointArc";

	String		Mi_ELLIPSE_TYPE_NAME			= "ellipse";
	String		Mi_TEXT_TYPE_NAME			= "text";
	String		Mi_TRIANGLE_TYPE_NAME			= "triangle";
	String		Mi_PLACE_HOLDER_TYPE_NAME		= "placeHolder";

	String		Mi_NODE_TYPE_NAME			= "node";
	String		Mi_DOCUMENT_TYPE_NAME			= "document";
	String		Mi_CONNECTION_TYPE_NAME			= "connection";
	String		Mi_EDITOR_TYPE_NAME			= "editor";
	String		Mi_PALETTE_TYPE_NAME			= "palette";
	String		Mi_CONTAINER_TYPE_NAME			= "container";
	String		Mi_SEMANTICS_TYPE_NAME			= "semantics";
	String		Mi_LAYER_TYPE_NAME			= "layer";

	// ---------------------------------------------------------------
	// General Attributes
	// ---------------------------------------------------------------
	String		Mi_NAME_NAME				= "name";
	String		Mi_CONTENTS_NAME			= "contents";
	String		Mi_VALUE_NAME				= "value";
	String		Mi_LAYOUT_NAME				= "layout";
	String		Mi_TYPE_NAME				= "type";
	String		Mi_SIMPLE_ENTITY_RESOURCE_NAME		= "simpleEntity";
	String		Mi_CONTAINER_NAME			= "container";
	String		Mi_ATTRIBUTES_TYPE_NAME			= "attributes";
	String		Mi_EDITABLE_NAME			= "editable";
	String		Mi_DECORATION_NAME			= "decoration";
	String		Mi_VISIBLE_NAME				= "visible";
	String		Mi_ENABLED_NAME				= "enabled";
	String		Mi_RESOURCE_NAME			= "resource";
	String		Mi_TAG_NAME				= "tag";


	// ---------------------------------------------------------------
	// Connection Attributes
	// ---------------------------------------------------------------
	String		Mi_CONNECTION_SOURCE_NAME		= "source";
	String		Mi_CONNECTION_DESTINATION_NAME		= "destination";
	String		Mi_CONNECTED_FROM_NAME			= "connectedFrom";
	String		Mi_CONNECTED_TO_NAME			= "connectedTo";

	// ---------------------------------------------------------------
	// Image Attributes
	// ---------------------------------------------------------------
	String		Mi_IMAGE_TYPE_NAME			= "image";
	String		Mi_IMAGE_UUENCODED_DATA			= "imageData";

	// ---------------------------------------------------------------
	// Geometric Attributes
	// ---------------------------------------------------------------
	String		Mi_X_COORD_NAME				= "x";
	String		Mi_Y_COORD_NAME				= "y";
	String		Mi_XMIN_NAME				= "xmin";
	String		Mi_YMIN_NAME				= "ymin";
	String		Mi_XMAX_NAME				= "xmax";
	String		Mi_YMAX_NAME				= "ymax";
	String		Mi_WIDTH_NAME				= "width";
	String		Mi_HEIGHT_NAME				= "height";

	String		Mi_X_TRANSLATION_NAME			= "xTranslation";
	String		Mi_Y_TRANSLATION_NAME			= "yTranslation";

	String		Mi_RADIUS_NAME				= "radius";
	String		Mi_ARC_START_ANGLE_NAME			= "startAngle";
	String		Mi_ARC_END_ANGLE_NAME			= "endAngle";
	String		Mi_ARC_SWEPT_ANGLE_NAME			= "sweptAngle";
	String		Mi_ARC_CENTER_X_NAME			= "arcCenterX";
	String		Mi_ARC_CENTER_Y_NAME			= "arcCenterY";

	String		Mi_TRANSLATION_POINT_PART_NAME_AND_POINT_NUMBER	= "translationPoint";
	String		Mi_ROTATION_POINT_PART_NAME_AND_POINT_NUMBER	= "rotationPoint";
	String		Mi_TRANSLATE_PT_TO_X_NAME			= "translatePtToX";
	String		Mi_TRANSLATE_PT_TO_Y_NAME			= "translatePtToY";
	String		Mi_ROTATE_PT_TO_NAME				= "rotateAboutPtTo";

	String		Mi_ROTATE_NAME				= "rotate";
	String		Mi_ROTATION_NAME			= "rotation";

	String		Mi_LOCATION_NAME			= "location";

	// ---------------------------------------------------------------
	// Document Attributes
	// ---------------------------------------------------------------
	String		Mi_TITLE_NAME				= "title";
	String		Mi_IGNORING_CASE_NAME			= "ignoreCase";
	String		Mi_READ_ONLY_NAME			= "readOnly";
	String		Mi_VIEW_ONLY_NAME			= "viewOnly";
	String		Mi_CREATION_DATE_NAME			= "creationDate";
	String		Mi_MODIFICATION_DATE_NAME		= "modificationDate";

	// ---------------------------------------------------------------
	// Layout Attributes
	// ---------------------------------------------------------------
	String		Mi_ALLEY_SPACING_NAME			= "alleySpacing";
	String		Mi_ALLEY_H_SPACING_NAME			= "alleyHSpacing";
	String		Mi_ALLEY_V_SPACING_NAME			= "alleyVSpacing";
	String		Mi_INSET_MARGINS_NAME			= "insetMargins";
	String		Mi_MARGINS_NAME				= "margins";
	String		Mi_ORIENTATION_NAME			= "orientation";
	String		Mi_HORIZONTAL_NAME			= "horizontal";
	String		Mi_VERTICAL_NAME			= "vertical";

	String		Mi_HORIZONTAL_JUSTIFICATION_NAME	= "hJustification";
	String		Mi_VERTICAL_JUSTIFICATION_NAME		= "vJustification";
	String		Mi_HORIZONTAL_SIZING_NAME		= "hSizing";
	String		Mi_VERTICAL_SIZING_NAME			= "vSizing";

	String		Mi_LEFT_JUSTIFIED_NAME			= "leftJustified";
	String		Mi_RIGHT_JUSTIFIED_NAME			= "rightJustified";
	String		Mi_CENTER_JUSTIFIED_NAME		= "centerJustified";
	String		Mi_TOP_JUSTIFIED_NAME			= "topJustified";
	String		Mi_BOTTOM_JUSTIFIED_NAME		= "bottomJustified";
	String		Mi_JUSTIFIED_NAME			= "justified";

	String		Mi_SAME_SIZE_NAME			= "sameSize";
	String		Mi_EXPAND_TO_FILL_NAME			= "expandToFill";

	// ---------------------------------------------------------------
	// Attribute names.
	// ---------------------------------------------------------------

	String		Mi_BACKGROUND_IMAGE_ATT_NAME		= "background image";
	String		Mi_BACKGROUND_TILE_ATT_NAME		= "background tile";

	String		Mi_FONT_ATT_NAME			= "font";
	
	String		Mi_TOOL_HINT_HELP_ATT_NAME		= "tool hint";
	String		Mi_BALLOON_HELP_ATT_NAME		= "balloon help";
	String		Mi_STATUS_HELP_ATT_NAME			= "status help";
	String		Mi_DIALOG_HELP_ATT_NAME			= "dialog help";

	String		Mi_TOOL_HINT_HELP_ATT_NAME_NSP		= "toolhint";
	String		Mi_BALLOON_HELP_ATT_NAME_NSP		= "balloonhelp";
	String		Mi_STATUS_HELP_ATT_NAME_NSP		= "statushelp";
	String		Mi_DIALOG_HELP_ATT_NAME_NSP		= "dialoghelp";

	String		Mi_SHADOW_RENDERER_ATT_NAME		= "shadow renderer";
	String		Mi_BEFORE_RENDERER_ATT_NAME		= "before renderer";
	String		Mi_AFTER_RENDERER_ATT_NAME		= "after renderer";
	String		Mi_LINE_ENDS_RENDERER_ATT_NAME		= "line-ends renderer";
	
	String		Mi_BACKGROUND_RENDERER_ATT_NAME		= "background renderer";
	String		Mi_BORDER_RENDERER_ATT_NAME		= "border renderer";

	String		Mi_VISIBILITY_ANIMATOR_ATT_NAME		= "visibility animator";

	String		Mi_CONTEXT_MENU_NAME			= "contextMenu";


	String		Mi_COLOR_ATT_NAME			= "color";
	String		Mi_BACKGROUND_COLOR_ATT_NAME		= "backgroundColor";
	String		Mi_LIGHT_COLOR_ATT_NAME			= "light color";
	String		Mi_WHITE_COLOR_ATT_NAME			= "white color";
	String		Mi_DARK_COLOR_ATT_NAME			= "dark color";
	String		Mi_BLACK_COLOR_ATT_NAME			= "black color";
	String		Mi_XOR_COLOR_ATT_NAME			= "xor color";
	String		Mi_BORDER_HILITE_COLOR_ATT_NAME		= "border hilite color";
	String		Mi_SHADOW_COLOR_ATT_NAME		= "shadowColor";

	String		Mi_BORDER_LOOK_ATT_NAME			= "borderLook";
	String		Mi_LINE_STYLE_ATT_NAME			= "line style";
	String		Mi_LINE_START_STYLE_ATT_NAME		= "line start style";
	String		Mi_LINE_END_STYLE_ATT_NAME		= "line end style";
	String		Mi_WRITE_MODE_ATT_NAME			= "write mode";
	String		Mi_CONTEXT_CURSOR_ATT_NAME		= "context cursor";
	String		Mi_CONTEXT_CURSOR_ATT_NAME_NSP		= "contextcursor";
	String		Mi_ATTRIBUTE_LOCK_MASK_ATT_NAME		= "attribute lock mask";
	String		Mi_ATTRIBUTE_PUBLIC_MASK_ATT_NAME	= "attribute public mask";
	String		Mi_SHADOW_DIRECTION_ATT_NAME		= "shadow direction";
	String		Mi_SHADOW_STYLE_ATT_NAME		= "shadow style";
	String		Mi_FONT_HORIZONTAL_JUSTIFICATION_ATT_NAME= "fontHJustification";
	String		Mi_FONT_VERTICAL_JUSTIFICATION_ATT_NAME	= "fontVJustification";

	String		Mi_MINIMUM_WIDTH_ATT_NAME		= "minimum width";
	String		Mi_MINIMUM_HEIGHT_ATT_NAME		= "minimum height";
	String		Mi_MAXIMUM_WIDTH_ATT_NAME		= "maximum width";
	String		Mi_MAXIMUM_HEIGHT_ATT_NAME		= "maximum height";
	String		Mi_BORDER_HILITE_WIDTH_ATT_NAME		= "border hilite width";
	String		Mi_SHADOW_LENGTH_ATT_NAME		= "shadow length";
	String		Mi_LINE_WIDTH_ATT_NAME			= "line width";
	String		Mi_LINE_START_SIZE_ATT_NAME		= "line start size";
	String		Mi_LINE_END_SIZE_ATT_NAME		= "line end size";

	String		Mi_DELETABLE_ATT_NAME			= "deletable";
	String		Mi_MOVABLE_ATT_NAME			= "movable";
	String		Mi_COPYABLE_ATT_NAME			= "copyable";
	String		Mi_COPYABLE_AS_PART_OF_COPYABLE_ATT_NAME= "copyableAsPartOfCopyable";
	String		Mi_SELECTABLE_ATT_NAME			= "selectable";
	String		Mi_FIXED_WIDTH_ATT_NAME			= "fixed width";
	String		Mi_FIXED_HEIGHT_ATT_NAME		= "fixed height";
	String		Mi_FIXED_ASPECT_RATIO_ATT_NAME		= "fixed aspect ratio";
	String		Mi_PICKABLE_ATT_NAME			= "pickable";
	String		Mi_UNGROUPABLE_ATT_NAME			= "ungroupable";
	String		Mi_CONNECTABLE_ATT_NAME			= "connectable";
	String		Mi_HIDDEN_ATT_NAME			= "hidden";
	String		Mi_DRAG_AND_DROP_SOURCE_ATT_NAME	= "drag-and-drop-source";
	String		Mi_DRAG_AND_DROP_TARGET_ATT_NAME	= "drag-and-drop-target";
	String		Mi_ACCEPTS_MOUSE_FOCUS_ATT_NAME		= "accepts mouse focus";
	String		Mi_ACCEPTS_KEYBOARD_FOCUS_ATT_NAME	= "accepts keyboard focus";
	String		Mi_ACCEPTS_ENTER_KEY_FOCUS_ATT_NAME	= "accepts enter key focus";
	String		Mi_ACCEPTS_TAB_KEYS_ATT_NAME		= "accepts tab keys";
	String		Mi_HAS_BORDER_HILITE_ATT_NAME		= "has border hilite";
	String		Mi_HAS_SHADOW_ATT_NAME			= "has shadow";
	String		Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH_ATT_NAME= "line ends size function of line width";
	String		Mi_PRINTABLE_NAME			= "printable";
	String		Mi_SAVABLE_NAME				= "savable";
	String		Mi_PICKABLE_WHEN_TRANSPARENT_ATT_NAME	= "pickableWhenTransparent";
	String		Mi_FILLED_ATT_NAME			= "filled";
	String		Mi_SNAPPABLE_ATT_NAME			= "snappable";


	String		Mi_FONT_NAME_ATT_NAME			= "fontName";
	String		Mi_FONT_SIZE_ATT_NAME			= "fontSize";
	String		Mi_FONT_BOLD_ATT_NAME			= "fontBold";
	String		Mi_FONT_ITALIC_ATT_NAME			= "fontItalic";
	String		Mi_FONT_UNDERLINED_ATT_NAME		= "fontUnderlined";
	String		Mi_FONT_STRIKEOUT_ATT_NAME		= "fontStrikeout";

	String		Mi_TEXT_BASELINE_Y_ATT_NAME		= "baselineY";
	String		Mi_NUM_DISPLAYED_CHARACTERS_NAME	= "numDisplayedCharacters";




	String		Mi_FILLED_TRIANGLE_LINE_END_STYLE_NAME		= "FilledTriangle";
	String		Mi_STROKED_ARROW_LINE_END_STYLE_NAME		= "StrokedArrow";
	String		Mi_FILLED_ARROW_LINE_END_STYLE_NAME		= "FilledArrow";
	String		Mi_FILLED_CIRCLE_LINE_END_STYLE_NAME		= "FilledCircle";
	String		Mi_FILLED_SQUARE_LINE_END_STYLE_NAME		= "FilledSquare";
	String		Mi_TRIANGLE_VIA_LINE_END_STYLE_NAME		= "TriangleVia";
	String		Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE_NAME	= "FilledTriangleVia";
	String		Mi_CIRCLE_VIA_LINE_END_STYLE_NAME		= "CicleVia";
	String		Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE_NAME	= "FilledCircleVia";
	String		Mi_SQUARE_VIA_LINE_END_STYLE_NAME		= "SquareVia";
	String		Mi_FILLED_SQUARE_VIA_LINE_END_STYLE_NAME	= "FilledSquareVia";
	String		Mi_TRIANGLE_LINE_END_STYLE_NAME			= "Triangle";
	String		Mi_CIRCLE_LINE_END_STYLE_NAME			= "Circle";
	String		Mi_SQUARE_LINE_END_STYLE_NAME			= "Square";
	String		Mi_DIAMOND_LINE_END_STYLE_NAME			= "Diamond";
	String		Mi_FILLED_DIAMOND_LINE_END_STYLE_NAME		= "FilledDiamond";
	String		Mi_3FEATHER_LINE_END_STYLE_NAME			= "3Feather";
	String		Mi_2FEATHER_LINE_END_STYLE_NAME			= "2Feather";

	String		Mi_FLAT_BORDER_LOOK_NAME			= "Flat";
	String		Mi_RAISED_BORDER_LOOK_NAME			= "Raised";
	String		Mi_INDENTED_BORDER_LOOK_NAME			= "Indented";
	String		Mi_GROOVE_BORDER_LOOK_NAME			= "Groove";
	String		Mi_RIDGE_BORDER_LOOK_NAME			= "Ridge";
	String		Mi_OUTLINED_RAISED_BORDER_LOOK_NAME		= "RaisedOutline";
	String		Mi_OUTLINED_INDENTED_BORDER_LOOK_NAME		= "IndentedOutline";
	String		Mi_INLINED_RAISED_BORDER_LOOK_NAME		= "RaisedInline";
	String		Mi_INLINED_INDENTED_BORDER_LOOK_NAME		= "IndentedInline";
	String		Mi_SQUARE_RAISED_BORDER_LOOK_NAME		= "RaisedSquared";

	String		Mi_DEFAULT_CURSOR_NAME   			= "DefaultCursor";
	String		Mi_CROSSHAIR_CURSOR_NAME 			= "CrossHairCursor";
	String		Mi_TEXT_CURSOR_NAME 	 			= "TextCursor";
	String		Mi_WAIT_CURSOR_NAME	 			= "WaitCursor";
	String		Mi_SW_RESIZE_CURSOR_NAME		 	= "SWResizeCursor";
	String		Mi_SE_RESIZE_CURSOR_NAME		 	= "SEResizeCursor";
	String		Mi_NW_RESIZE_CURSOR_NAME			= "NWResizeCursor";
	String		Mi_NE_RESIZE_CURSOR_NAME		 	= "NEResizeCursor";
	String		Mi_N_RESIZE_CURSOR_NAME 			= "NResizeCursor";
	String		Mi_S_RESIZE_CURSOR_NAME 			= "SResizeCursor";
	String		Mi_W_RESIZE_CURSOR_NAME	 			= "WResizeCursor";
	String		Mi_E_RESIZE_CURSOR_NAME				= "EResizeCursor";
	String		Mi_HAND_CURSOR_NAME				= "HandCursor";
	String		Mi_MOVE_CURSOR_NAME				= "MoveCursor";

	String		Mi_COPY_WRITEMODE_NAME				= "replace";
	String		Mi_XOR_WRITEMODE_NAME				= "xor";

	String		Mi_SOLID_LINE_STYLE_NAME			= "solid";
	String		Mi_DASHED_LINE_STYLE_NAME			= "dashed";
	String		Mi_DOUBLE_DASHED_LINE_STYLE_NAME		= "doubleDashed";
	String		Mi_DOTTED_LINE_STYLE_NAME			= "dotted";

	String		Mi_CENTER_LOCATION_NAME				= "center";
	String		Mi_LEFT_LOCATION_NAME				= "left";
	String		Mi_RIGHT_LOCATION_NAME				= "right";
	String		Mi_BOTTOM_LOCATION_NAME				= "bottom";
	String		Mi_TOP_LOCATION_NAME				= "top";
	String		Mi_LOWER_LEFT_LOCATION_NAME			= "lowerLeft";
	String		Mi_LOWER_RIGHT_LOCATION_NAME			= "lowerRight";
	String		Mi_UPPER_LEFT_LOCATION_NAME			= "upperLeft";
	String		Mi_UPPER_RIGHT_LOCATION_NAME			= "upperRight";
	String		Mi_START_LOCATION_NAME				= "start";
	String		Mi_END_LOCATION_NAME				= "end";

	String		Mi_OUTSIDE_LEFT_LOCATION_NAME			= "outsideLeft";
	String		Mi_OUTSIDE_RIGHT_LOCATION_NAME			= "outsideRight";
	String		Mi_OUTSIDE_BOTTOM_LOCATION_NAME			= "outsideBottom";
	String		Mi_OUTSIDE_TOP_LOCATION_NAME			= "outsideTop";
	String		Mi_OUTSIDE_LOWER_LEFT_LOCATION_NAME		= "outsideLowerLeft";
	String		Mi_OUTSIDE_LOWER_RIGHT_LOCATION_NAME		= "outsideLowerRight";
	String		Mi_OUTSIDE_UPPER_LEFT_LOCATION_NAME		= "outsideUpperLeft";
	String		Mi_OUTSIDE_UPPER_RIGHT_LOCATION_NAME		= "outsideUpperRight";

	String		Mi_INSIDE_LEFT_LOCATION_NAME			= "insideLeft";
	String		Mi_INSIDE_RIGHT_LOCATION_NAME			= "insideRight";
	String		Mi_INSIDE_BOTTOM_LOCATION_NAME			= "insideBottom";
	String		Mi_INSIDE_TOP_LOCATION_NAME			= "insideTop";
	String		Mi_INSIDE_LOWER_LEFT_LOCATION_NAME		= "insideLowerLeft";
	String		Mi_INSIDE_LOWER_RIGHT_LOCATION_NAME		= "insideLowerRight";
	String		Mi_INSIDE_UPPER_LEFT_LOCATION_NAME		= "insideUpperLeft";
	String		Mi_INSIDE_UPPER_RIGHT_LOCATION_NAME		= "insideUpperRight";

	String		Mi_WNW_LOCATION_NAME				= "westNorthWest";
	String		Mi_WSW_LOCATION_NAME				= "westSouthWest";
	String		Mi_ENE_LOCATION_NAME				= "eastNorthEast";
	String		Mi_ESE_LOCATION_NAME				= "eastSouthEast";
	String		Mi_NWN_LOCATION_NAME				= "northWestNorth";
	String		Mi_NEN_LOCATION_NAME				= "northEastNorth";
	String		Mi_SWS_LOCATION_NAME				= "southWestSouth";
	String		Mi_SES_LOCATION_NAME				= "southNorthSouth";

	String		Mi_LINE_CENTER_LOCATION_NAME			= "lineCenter";
	String		Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION_NAME	= "lineCenterTopOrRight";
	String		Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION_NAME	= "lineCenterBottonOrLeft";
	String		Mi_LINE_START_LOCATION_NAME			= "lineStartTopOrRight";
	String		Mi_LINE_START_TOP_OR_RIGHT_LOCATION_NAME	= "lineStartTopOrRight";
	String		Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION_NAME	= "lineStartBottonOrLeft";
	String		Mi_LINE_END_LOCATION_NAME			= "lineEnd";
	String		Mi_LINE_END_TOP_OR_RIGHT_LOCATION_NAME		= "lineEndTopOrRight";
	String		Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION_NAME	= "lineEndBottonOrLeft";

	// Fixed locations relative to a +45 degree angled line
	String		Mi_LINE_START_TOP_LOCATION_NAME		= "lineStartTop";
	String		Mi_LINE_START_BOTTOM_LOCATION_NAME	= "lineStartBottom";
	String		Mi_LINE_END_TOP_LOCATION_NAME		= "lineEndTop";
	String		Mi_LINE_END_BOTTOM_LOCATION_NAME	= "lineEndBottom";

	String		Mi_SURROUND_LOCATION_NAME		= "surround";
	String		Mi_DEFAULT_LOCATION_NAME		= "default";

	String		Mi_ABOVE_NAME				= "above";
	String		Mi_BELOW_NAME				= "below";
	String		Mi_TO_RIGHT_NAME			= "toRight";
	String		Mi_TO_LEFT_NAME				= "toLeft";
	String		Mi_TOP_NAME				= "top";
	String		Mi_BOTTOM_NAME				= "bottom";
	String		Mi_FAR_RIGHT_NAME			= "farRight";
	String		Mi_FAR_LEFT_NAME			= "farLeft";

	String		Mi_CONNECTION_POINT_NAME		= "connectionPoint";
	String		Mi_SOURCE_CONNECTION_POINT_NAME		= "sourceConnPt";
	String		Mi_DESTINATION_CONNECTION_POINT_NAME	= "destConnPt";
	String		Mi_CONNECTION_ID_NAME			= "connectionType";

	String		Mi_POINTS_NAME				= "points";

	String		Mi_ANNOTATION_POINT_NAME		= "annotationPoint";
	String		Mi_SNAP_POINT_NAME			= "snapPoint";
	String		Mi_CONTROL_POINT_NAME			= "controlPoint";

	// -----------------------------------------------------------------------
	// The table of layout names.
	// -----------------------------------------------------------------------
	String[]	layoutNames =
		{
		Mi_ROW_LAYOUT_TYPE_NAME				,
		Mi_COLUMN_LAYOUT_TYPE_NAME			,
		Mi_GRID_LAYOUT_TYPE_NAME			,
		Mi_STAR_GRAPH_LAYOUT_TYPE_NAME			,
		Mi_UNDIRECTED_GRAPH_LAYOUT_TYPE_NAME		,
		Mi_OUTLINE_GRAPH_LAYOUT_TYPE_NAME		,
		Mi_TREE_GRAPH_LAYOUT_TYPE_NAME			,
		Mi_RING_GRAPH_LAYOUT_TYPE_NAME			,
		Mi_2DMESH_GRAPH_LAYOUT_TYPE_NAME		,
		Mi_LINE_GRAPH_LAYOUT_TYPE_NAME			,
		Mi_CROSSBAR_GRAPH_LAYOUT_TYPE_NAME		,
		Mi_OMEGA_GRAPH_LAYOUT_TYPE_NAME			,
		Mi_LAYOUT_TEMPLATE_NAME
		};

	// -----------------------------------------------------------------------
	// The table of line end style names.
	// -----------------------------------------------------------------------
	String[]	lineEndStyleNames =
		{
		Mi_NONE_NAME					,
		Mi_FILLED_TRIANGLE_LINE_END_STYLE_NAME		,
		Mi_STROKED_ARROW_LINE_END_STYLE_NAME		,
		Mi_FILLED_ARROW_LINE_END_STYLE_NAME		,
		Mi_FILLED_CIRCLE_LINE_END_STYLE_NAME		,
		Mi_FILLED_SQUARE_LINE_END_STYLE_NAME		,
		Mi_TRIANGLE_VIA_LINE_END_STYLE_NAME		,
		Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE_NAME	,
		Mi_CIRCLE_VIA_LINE_END_STYLE_NAME		,
		Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE_NAME	,
		Mi_SQUARE_VIA_LINE_END_STYLE_NAME		,
		Mi_FILLED_SQUARE_VIA_LINE_END_STYLE_NAME	,
		Mi_TRIANGLE_LINE_END_STYLE_NAME			,
		Mi_CIRCLE_LINE_END_STYLE_NAME			,
		Mi_SQUARE_LINE_END_STYLE_NAME			,
		Mi_DIAMOND_LINE_END_STYLE_NAME			,
		Mi_FILLED_DIAMOND_LINE_END_STYLE_NAME		,
		Mi_3FEATHER_LINE_END_STYLE_NAME			,
		Mi_2FEATHER_LINE_END_STYLE_NAME	
		};

	// -----------------------------------------------------------------------
	// The table of border look names.
	// -----------------------------------------------------------------------
	String[]	borderLookNames =
		{
		Mi_NONE_NAME				,
		Mi_FLAT_BORDER_LOOK_NAME		,
		Mi_RAISED_BORDER_LOOK_NAME		,
		Mi_INDENTED_BORDER_LOOK_NAME		,
		Mi_GROOVE_BORDER_LOOK_NAME		,
		Mi_RIDGE_BORDER_LOOK_NAME		,
		Mi_OUTLINED_RAISED_BORDER_LOOK_NAME	,
		Mi_OUTLINED_INDENTED_BORDER_LOOK_NAME	,
		Mi_INLINED_RAISED_BORDER_LOOK_NAME	,
		Mi_INLINED_INDENTED_BORDER_LOOK_NAME	,
		Mi_SQUARE_RAISED_BORDER_LOOK_NAME
		};

	String[]	cursorNames =
		{
		Mi_DEFAULT_CURSOR_NAME			,
		Mi_CROSSHAIR_CURSOR_NAME		,
		Mi_TEXT_CURSOR_NAME 			,
		Mi_WAIT_CURSOR_NAME			,
		Mi_SW_RESIZE_CURSOR_NAME		,
		Mi_SE_RESIZE_CURSOR_NAME		,
		Mi_NW_RESIZE_CURSOR_NAME		,
		Mi_NE_RESIZE_CURSOR_NAME		,
		Mi_N_RESIZE_CURSOR_NAME			,
		Mi_S_RESIZE_CURSOR_NAME			,
		Mi_W_RESIZE_CURSOR_NAME			,
		Mi_E_RESIZE_CURSOR_NAME			,
		Mi_HAND_CURSOR_NAME			,
		Mi_MOVE_CURSOR_NAME
		};

	String[]	writeModeNames =
		{
		Mi_COPY_WRITEMODE_NAME			,
		Mi_XOR_WRITEMODE_NAME
		};

	String[]	lineStyleNames =
		{
		Mi_SOLID_LINE_STYLE_NAME		,
		Mi_DASHED_LINE_STYLE_NAME		,
		Mi_DOUBLE_DASHED_LINE_STYLE_NAME	,
		Mi_DOTTED_LINE_STYLE_NAME		,
		};

	String[]	locationNames =
		{
		Mi_CENTER_LOCATION_NAME			,
		Mi_LEFT_LOCATION_NAME			,
		Mi_RIGHT_LOCATION_NAME			,
		Mi_BOTTOM_LOCATION_NAME			,
		Mi_TOP_LOCATION_NAME			,
		Mi_LOWER_LEFT_LOCATION_NAME		,
		Mi_LOWER_RIGHT_LOCATION_NAME		,
		Mi_UPPER_LEFT_LOCATION_NAME		,
		Mi_UPPER_RIGHT_LOCATION_NAME		,
		Mi_START_LOCATION_NAME			,
		Mi_END_LOCATION_NAME			,

		Mi_OUTSIDE_LEFT_LOCATION_NAME		,
		Mi_OUTSIDE_RIGHT_LOCATION_NAME		,
		Mi_OUTSIDE_BOTTOM_LOCATION_NAME		,
		Mi_OUTSIDE_TOP_LOCATION_NAME		,
		Mi_OUTSIDE_LOWER_LEFT_LOCATION_NAME	,
		Mi_OUTSIDE_LOWER_RIGHT_LOCATION_NAME	,
		Mi_OUTSIDE_UPPER_LEFT_LOCATION_NAME	,
		Mi_OUTSIDE_UPPER_RIGHT_LOCATION_NAME	,

		Mi_INSIDE_LEFT_LOCATION_NAME		,
		Mi_INSIDE_RIGHT_LOCATION_NAME		,
		Mi_INSIDE_BOTTOM_LOCATION_NAME		,
		Mi_INSIDE_TOP_LOCATION_NAME		,
		Mi_INSIDE_LOWER_LEFT_LOCATION_NAME	,
		Mi_INSIDE_LOWER_RIGHT_LOCATION_NAME	,
		Mi_INSIDE_UPPER_LEFT_LOCATION_NAME	,
		Mi_INSIDE_UPPER_RIGHT_LOCATION_NAME	,

		Mi_WNW_LOCATION_NAME			,
		Mi_WSW_LOCATION_NAME			,
		Mi_ENE_LOCATION_NAME			,
		Mi_ESE_LOCATION_NAME			,
		Mi_NWN_LOCATION_NAME			,
		Mi_NEN_LOCATION_NAME			,
		Mi_SWS_LOCATION_NAME			,
		Mi_SES_LOCATION_NAME			,

		Mi_LINE_CENTER_LOCATION_NAME			,
		Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION_NAME	,
		Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION_NAME	,
		Mi_LINE_START_LOCATION_NAME			,
		Mi_LINE_START_TOP_OR_RIGHT_LOCATION_NAME	,
		Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION_NAME	,
		Mi_LINE_END_LOCATION_NAME			,
		Mi_LINE_END_TOP_OR_RIGHT_LOCATION_NAME		,
		Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION_NAME	,

	// Fixed locations relative to a +45 degree angled line
		Mi_LINE_START_TOP_LOCATION_NAME		,
		Mi_LINE_START_BOTTOM_LOCATION_NAME	,
		Mi_LINE_END_TOP_LOCATION_NAME		,
		Mi_LINE_END_BOTTOM_LOCATION_NAME	,

		Mi_SURROUND_LOCATION_NAME		,
		Mi_DEFAULT_LOCATION_NAME
		};

	String[]	horizontalJustificationNames =
		{
		Mi_CENTER_JUSTIFIED_NAME,
		Mi_LEFT_JUSTIFIED_NAME,
		Mi_RIGHT_JUSTIFIED_NAME,
		Mi_JUSTIFIED_NAME
		};

	String[]	verticalJustificationNames =
		{
		Mi_CENTER_JUSTIFIED_NAME,
		Mi_BOTTOM_JUSTIFIED_NAME,
		Mi_TOP_JUSTIFIED_NAME,
		Mi_JUSTIFIED_NAME
		};

	String[]	elementSizingNames =
		{
		Mi_SAME_SIZE_NAME,
		Mi_EXPAND_TO_FILL_NAME
		};
	String[]	relativePositionNames =
		{
		Mi_ABOVE_NAME,
		Mi_BELOW_NAME,
		Mi_TO_RIGHT_NAME,
		Mi_TO_LEFT_NAME,
		Mi_TOP_NAME,
		Mi_BOTTOM_NAME,
		Mi_FAR_RIGHT_NAME,
		Mi_FAR_LEFT_NAME,
		};


	// -----------------------------------------------------------------------
	// The array of attribute names that have spaces in them for display in 
	// property sheets. See MiAttributes for array w/o spaces. These must be in
	// exact same order as the list in MiiAttributeTypes.
	// -----------------------------------------------------------------------
	String[]	attributeNames =
		{
		Mi_BACKGROUND_IMAGE_ATT_NAME			,
		Mi_BACKGROUND_TILE_ATT_NAME			,

		Mi_FONT_ATT_NAME				,

		Mi_TOOL_HINT_HELP_ATT_NAME			,
		Mi_BALLOON_HELP_ATT_NAME			,
		Mi_STATUS_HELP_ATT_NAME				,
		Mi_DIALOG_HELP_ATT_NAME				,

		Mi_SHADOW_RENDERER_ATT_NAME			,
		Mi_BEFORE_RENDERER_ATT_NAME			,
		Mi_AFTER_RENDERER_ATT_NAME			,
		Mi_LINE_ENDS_RENDERER_ATT_NAME			,

		Mi_BACKGROUND_RENDERER_ATT_NAME			,
		Mi_BORDER_RENDERER_ATT_NAME			,

		Mi_VISIBILITY_ANIMATOR_ATT_NAME			,

		Mi_CONTEXT_MENU_NAME				,

		Mi_COLOR_ATT_NAME				,
		Mi_BACKGROUND_COLOR_ATT_NAME			,
		Mi_LIGHT_COLOR_ATT_NAME				,
		Mi_WHITE_COLOR_ATT_NAME				,
		Mi_DARK_COLOR_ATT_NAME				,
		Mi_BLACK_COLOR_ATT_NAME				,
		Mi_XOR_COLOR_ATT_NAME				,
		Mi_BORDER_HILITE_COLOR_ATT_NAME			,
		Mi_SHADOW_COLOR_ATT_NAME			,

		Mi_BORDER_LOOK_ATT_NAME				,
		Mi_LINE_STYLE_ATT_NAME				,
		Mi_LINE_START_STYLE_ATT_NAME			,
		Mi_LINE_END_STYLE_ATT_NAME			,
		Mi_WRITE_MODE_ATT_NAME				,
		Mi_CONTEXT_CURSOR_ATT_NAME			,
		Mi_ATTRIBUTE_LOCK_MASK_ATT_NAME			,
		Mi_ATTRIBUTE_PUBLIC_MASK_ATT_NAME		,
		Mi_SHADOW_DIRECTION_ATT_NAME			,
		Mi_SHADOW_STYLE_ATT_NAME			,
		Mi_FONT_HORIZONTAL_JUSTIFICATION_ATT_NAME	,
		Mi_FONT_VERTICAL_JUSTIFICATION_ATT_NAME		,

		Mi_MINIMUM_WIDTH_ATT_NAME			,
		Mi_MINIMUM_HEIGHT_ATT_NAME			,
		Mi_MAXIMUM_WIDTH_ATT_NAME			,
		Mi_MAXIMUM_HEIGHT_ATT_NAME			,
		Mi_BORDER_HILITE_WIDTH_ATT_NAME			,
		Mi_SHADOW_LENGTH_ATT_NAME			,
		Mi_LINE_WIDTH_ATT_NAME				,
		Mi_LINE_START_SIZE_ATT_NAME			,
		Mi_LINE_END_SIZE_ATT_NAME			,

		Mi_DELETABLE_ATT_NAME				,
		Mi_MOVABLE_ATT_NAME				,
		Mi_COPYABLE_ATT_NAME				,
		Mi_SELECTABLE_ATT_NAME				,
		Mi_FIXED_WIDTH_ATT_NAME				,
		Mi_FIXED_HEIGHT_ATT_NAME			,
		Mi_FIXED_ASPECT_RATIO_ATT_NAME			,
		Mi_PICKABLE_ATT_NAME				,
		Mi_UNGROUPABLE_ATT_NAME				,
		Mi_CONNECTABLE_ATT_NAME				,
		Mi_HIDDEN_ATT_NAME				,
		Mi_DRAG_AND_DROP_SOURCE_ATT_NAME		,
		Mi_DRAG_AND_DROP_TARGET_ATT_NAME		,
		Mi_ACCEPTS_MOUSE_FOCUS_ATT_NAME			,
		Mi_ACCEPTS_KEYBOARD_FOCUS_ATT_NAME		,
		Mi_ACCEPTS_ENTER_KEY_FOCUS_ATT_NAME		,
		Mi_ACCEPTS_TAB_KEYS_ATT_NAME			,
		Mi_HAS_BORDER_HILITE_ATT_NAME			,
		Mi_HAS_SHADOW_ATT_NAME				,
		Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH_ATT_NAME	,
		Mi_PRINTABLE_NAME				,
		Mi_SAVABLE_NAME					,
		Mi_PICKABLE_WHEN_TRANSPARENT_ATT_NAME		,
		Mi_FILLED_ATT_NAME				,
		Mi_SNAPPABLE_ATT_NAME				,
		Mi_COPYABLE_AS_PART_OF_COPYABLE_ATT_NAME	,
		};
	StringIntPair[] attributeValueNames =

	{
	new StringIntPair( Mi_NONE_NAME,		 		Mi_NONE),
	new StringIntPair( Mi_LEFT_JUSTIFIED_NAME,		 	Mi_LEFT_JUSTIFIED),
	new StringIntPair( Mi_RIGHT_JUSTIFIED_NAME, 			Mi_RIGHT_JUSTIFIED),
	new StringIntPair( Mi_CENTER_JUSTIFIED_NAME, 			Mi_CENTER_JUSTIFIED),
	new StringIntPair( Mi_JUSTIFIED_NAME, 				Mi_JUSTIFIED),
	new StringIntPair( Mi_TOP_JUSTIFIED_NAME, 			Mi_TOP_JUSTIFIED),
	new StringIntPair( Mi_BOTTOM_JUSTIFIED_NAME, 			Mi_BOTTOM_JUSTIFIED),

	new StringIntPair( Mi_SAME_SIZE_NAME, 				Mi_SAME_SIZE),
	new StringIntPair( Mi_EXPAND_TO_FILL_NAME, 			Mi_EXPAND_TO_FILL),

	new StringIntPair( Mi_FILLED_TRIANGLE_LINE_END_STYLE_NAME,	Mi_FILLED_TRIANGLE_LINE_END_STYLE),
	new StringIntPair( Mi_STROKED_ARROW_LINE_END_STYLE_NAME,		Mi_STROKED_ARROW_LINE_END_STYLE),
	new StringIntPair( Mi_FILLED_ARROW_LINE_END_STYLE_NAME,		Mi_FILLED_ARROW_LINE_END_STYLE),
	new StringIntPair( Mi_FILLED_CIRCLE_LINE_END_STYLE_NAME,	Mi_FILLED_CIRCLE_LINE_END_STYLE),
	new StringIntPair( Mi_FILLED_SQUARE_LINE_END_STYLE_NAME,	Mi_FILLED_SQUARE_LINE_END_STYLE),
	new StringIntPair( Mi_TRIANGLE_VIA_LINE_END_STYLE_NAME,		Mi_TRIANGLE_VIA_LINE_END_STYLE),
	new StringIntPair( Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE_NAME, Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE),
	new StringIntPair( Mi_CIRCLE_VIA_LINE_END_STYLE_NAME,		Mi_CIRCLE_VIA_LINE_END_STYLE),
	new StringIntPair( Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE_NAME,	Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE),
	new StringIntPair( Mi_SQUARE_VIA_LINE_END_STYLE_NAME,		Mi_SQUARE_VIA_LINE_END_STYLE),
	new StringIntPair( Mi_FILLED_SQUARE_VIA_LINE_END_STYLE_NAME,	Mi_FILLED_SQUARE_VIA_LINE_END_STYLE),
	new StringIntPair( Mi_TRIANGLE_LINE_END_STYLE_NAME,		Mi_TRIANGLE_LINE_END_STYLE),
	new StringIntPair( Mi_CIRCLE_LINE_END_STYLE_NAME,		Mi_CIRCLE_LINE_END_STYLE),
	new StringIntPair( Mi_SQUARE_LINE_END_STYLE_NAME,		Mi_SQUARE_LINE_END_STYLE),
	new StringIntPair( Mi_DIAMOND_LINE_END_STYLE_NAME,		Mi_DIAMOND_LINE_END_STYLE),
	new StringIntPair( Mi_FILLED_DIAMOND_LINE_END_STYLE_NAME,	Mi_FILLED_DIAMOND_LINE_END_STYLE),
	new StringIntPair( Mi_3FEATHER_LINE_END_STYLE_NAME,		Mi_3FEATHER_LINE_END_STYLE),
	new StringIntPair( Mi_2FEATHER_LINE_END_STYLE_NAME,		Mi_2FEATHER_LINE_END_STYLE),

	new StringIntPair( Mi_FLAT_BORDER_LOOK_NAME,			Mi_FLAT_BORDER_LOOK),
	new StringIntPair( Mi_RAISED_BORDER_LOOK_NAME,			Mi_RAISED_BORDER_LOOK),
	new StringIntPair( Mi_INDENTED_BORDER_LOOK_NAME,		Mi_INDENTED_BORDER_LOOK),
	new StringIntPair( Mi_GROOVE_BORDER_LOOK_NAME,			Mi_GROOVE_BORDER_LOOK),
	new StringIntPair( Mi_RIDGE_BORDER_LOOK_NAME,			Mi_RIDGE_BORDER_LOOK),
	new StringIntPair( Mi_OUTLINED_RAISED_BORDER_LOOK_NAME,		Mi_OUTLINED_RAISED_BORDER_LOOK),
	new StringIntPair( Mi_OUTLINED_INDENTED_BORDER_LOOK_NAME,	Mi_OUTLINED_INDENTED_BORDER_LOOK),
	new StringIntPair( Mi_INLINED_RAISED_BORDER_LOOK_NAME,		Mi_INLINED_RAISED_BORDER_LOOK),
	new StringIntPair( Mi_INLINED_INDENTED_BORDER_LOOK_NAME,	Mi_INLINED_INDENTED_BORDER_LOOK),
	new StringIntPair( Mi_SQUARE_RAISED_BORDER_LOOK_NAME,		Mi_SQUARE_RAISED_BORDER_LOOK),

	new StringIntPair( Mi_DEFAULT_CURSOR_NAME,   			Mi_DEFAULT_CURSOR),
	new StringIntPair( Mi_CROSSHAIR_CURSOR_NAME, 			Mi_CROSSHAIR_CURSOR),
	new StringIntPair( Mi_TEXT_CURSOR_NAME, 	 		Mi_TEXT_CURSOR),
	new StringIntPair( Mi_WAIT_CURSOR_NAME,	 			Mi_WAIT_CURSOR),
	new StringIntPair( Mi_SW_RESIZE_CURSOR_NAME,		 	Mi_SW_RESIZE_CURSOR),
	new StringIntPair( Mi_SE_RESIZE_CURSOR_NAME,		 	Mi_SE_RESIZE_CURSOR),
	new StringIntPair( Mi_NW_RESIZE_CURSOR_NAME,			Mi_NW_RESIZE_CURSOR),
	new StringIntPair( Mi_NE_RESIZE_CURSOR_NAME,		 	Mi_NE_RESIZE_CURSOR),
	new StringIntPair( Mi_N_RESIZE_CURSOR_NAME, 			Mi_N_RESIZE_CURSOR),
	new StringIntPair( Mi_S_RESIZE_CURSOR_NAME, 			Mi_S_RESIZE_CURSOR),
	new StringIntPair( Mi_W_RESIZE_CURSOR_NAME,	 		Mi_W_RESIZE_CURSOR),
	new StringIntPair( Mi_E_RESIZE_CURSOR_NAME,			Mi_E_RESIZE_CURSOR),
	new StringIntPair( Mi_HAND_CURSOR_NAME,				Mi_HAND_CURSOR),
	new StringIntPair( Mi_MOVE_CURSOR_NAME,				Mi_MOVE_CURSOR),

	new StringIntPair( Mi_HORIZONTAL_NAME,				Mi_HORIZONTAL),
	new StringIntPair( Mi_VERTICAL_NAME,				Mi_VERTICAL),

	new StringIntPair( Mi_COPY_WRITEMODE_NAME,			Mi_COPY_WRITEMODE),
	new StringIntPair( Mi_XOR_WRITEMODE_NAME,			Mi_XOR_WRITEMODE),
	new StringIntPair( Mi_SOLID_LINE_STYLE_NAME,			Mi_SOLID_LINE_STYLE),
	new StringIntPair( Mi_DASHED_LINE_STYLE_NAME,			Mi_DASHED_LINE_STYLE),
	new StringIntPair( Mi_DOUBLE_DASHED_LINE_STYLE_NAME,		Mi_DOUBLE_DASHED_LINE_STYLE),
	new StringIntPair( Mi_DOTTED_LINE_STYLE_NAME,			Mi_DOTTED_LINE_STYLE),

	new StringIntPair( Mi_CENTER_LOCATION_NAME,			Mi_CENTER_LOCATION),
	new StringIntPair( Mi_LEFT_LOCATION_NAME,			Mi_LEFT_LOCATION),
	new StringIntPair( Mi_RIGHT_LOCATION_NAME,			Mi_RIGHT_LOCATION),
	new StringIntPair( Mi_BOTTOM_LOCATION_NAME,			Mi_BOTTOM_LOCATION),
	new StringIntPair( Mi_TOP_LOCATION_NAME,			Mi_TOP_LOCATION),
	new StringIntPair( Mi_LOWER_LEFT_LOCATION_NAME,			Mi_LOWER_LEFT_LOCATION),
	new StringIntPair( Mi_LOWER_RIGHT_LOCATION_NAME,		Mi_LOWER_RIGHT_LOCATION),
	new StringIntPair( Mi_UPPER_LEFT_LOCATION_NAME,			Mi_UPPER_LEFT_LOCATION),
	new StringIntPair( Mi_UPPER_RIGHT_LOCATION_NAME,		Mi_UPPER_RIGHT_LOCATION),
	new StringIntPair( Mi_SURROUND_LOCATION_NAME,			Mi_SURROUND_LOCATION),
	new StringIntPair( Mi_DEFAULT_LOCATION_NAME,			Mi_DEFAULT_LOCATION),
	new StringIntPair( Mi_START_LOCATION_NAME,			Mi_START_LOCATION),
	new StringIntPair( Mi_END_LOCATION_NAME,			Mi_END_LOCATION),

	new StringIntPair( Mi_OUTSIDE_LEFT_LOCATION_NAME,		Mi_OUTSIDE_LEFT_LOCATION		),
	new StringIntPair( Mi_OUTSIDE_RIGHT_LOCATION_NAME,		Mi_OUTSIDE_RIGHT_LOCATION		),
	new StringIntPair( Mi_OUTSIDE_BOTTOM_LOCATION_NAME,		Mi_OUTSIDE_BOTTOM_LOCATION		),
	new StringIntPair( Mi_OUTSIDE_TOP_LOCATION_NAME,		Mi_OUTSIDE_TOP_LOCATION			),
	new StringIntPair( Mi_OUTSIDE_LOWER_LEFT_LOCATION_NAME,		Mi_OUTSIDE_LOWER_LEFT_LOCATION		),
	new StringIntPair( Mi_OUTSIDE_LOWER_RIGHT_LOCATION_NAME,	Mi_OUTSIDE_LOWER_RIGHT_LOCATION		),
	new StringIntPair( Mi_OUTSIDE_UPPER_LEFT_LOCATION_NAME,		Mi_OUTSIDE_UPPER_LEFT_LOCATION		),
	new StringIntPair( Mi_OUTSIDE_UPPER_RIGHT_LOCATION_NAME,	Mi_OUTSIDE_UPPER_RIGHT_LOCATION		),

	new StringIntPair( Mi_WNW_LOCATION_NAME,			Mi_WNW_LOCATION				),
	new StringIntPair( Mi_WSW_LOCATION_NAME,			Mi_WSW_LOCATION				),
	new StringIntPair( Mi_ENE_LOCATION_NAME,			Mi_ENE_LOCATION				),
	new StringIntPair( Mi_ESE_LOCATION_NAME,			Mi_ESE_LOCATION				),
	new StringIntPair( Mi_NWN_LOCATION_NAME,			Mi_NWN_LOCATION				),
	new StringIntPair( Mi_NEN_LOCATION_NAME,			Mi_NEN_LOCATION				),
	new StringIntPair( Mi_SWS_LOCATION_NAME,			Mi_SWS_LOCATION				),
	new StringIntPair( Mi_SES_LOCATION_NAME,			Mi_SES_LOCATION				),

	new StringIntPair( Mi_LINE_CENTER_LOCATION_NAME,		Mi_LINE_CENTER_LOCATION			),
	new StringIntPair( Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION_NAME,	Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION	),
	new StringIntPair( Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION_NAME,	Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION	),
	new StringIntPair( Mi_LINE_START_LOCATION_NAME,			Mi_LINE_START_LOCATION			),
	new StringIntPair( Mi_LINE_START_TOP_OR_RIGHT_LOCATION_NAME,	Mi_LINE_START_TOP_OR_RIGHT_LOCATION	),
	new StringIntPair( Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION_NAME,	Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION	),
	new StringIntPair( Mi_LINE_END_LOCATION_NAME,			Mi_LINE_END_LOCATION			),
	new StringIntPair( Mi_LINE_END_TOP_OR_RIGHT_LOCATION_NAME,	Mi_LINE_END_TOP_OR_RIGHT_LOCATION	),
	new StringIntPair( Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION_NAME,	Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION	),

	// Fixed locations relative to a +45 degree angled line
	new StringIntPair( Mi_LINE_START_TOP_LOCATION_NAME,		Mi_LINE_START_TOP_LOCATION		),
	new StringIntPair( Mi_LINE_START_BOTTOM_LOCATION_NAME,		Mi_LINE_START_BOTTOM_LOCATION		),
	new StringIntPair( Mi_LINE_END_TOP_LOCATION_NAME,		Mi_LINE_END_TOP_LOCATION		),
	new StringIntPair( Mi_LINE_END_BOTTOM_LOCATION_NAME,		Mi_LINE_END_BOTTOM_LOCATION		),


	new StringIntPair( Mi_ABOVE_NAME,				Mi_ABOVE),
	new StringIntPair( Mi_BELOW_NAME,				Mi_BELOW),
	new StringIntPair( Mi_TO_RIGHT_NAME,				Mi_TO_RIGHT),
	new StringIntPair( Mi_TO_LEFT_NAME,				Mi_TO_LEFT),
	new StringIntPair( Mi_TOP_NAME,					Mi_TOP),
	new StringIntPair( Mi_BOTTOM_NAME,				Mi_BOTTOM),
	new StringIntPair( Mi_FAR_RIGHT_NAME,				Mi_FAR_RIGHT),
	new StringIntPair( Mi_FAR_LEFT_NAME,				Mi_FAR_LEFT),
	};


	}


