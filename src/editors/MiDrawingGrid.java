
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
import java.awt.Color;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDrawingGrid extends MiPart implements MiiActionHandler, MiiActionTypes
	{
	public static final String	Mi_GRID_LOOK_SPACING_MAJOR_MULTIPLE_NAME= "Grid Look Spacing Major Multiple";
	public static final String	Mi_GRID_LOOK_SPACING_MINOR_MULTIPLE_NAME= "Grid Look Spacing Minor Multiple";
	public static final String	Mi_GRID_H_SPACING_NAME			= "Grid Horizontal Spacing";
	public static final String	Mi_GRID_V_SPACING_NAME			= "Grid Vertical Spacing";
	public static final String	Mi_GRID_H_SNAP_STRENGTH_NAME		= "grid Horizontal Snap Strength";
	public static final String	Mi_GRID_V_SNAP_STRENGTH_NAME		= "grid Vertical Snap Strength";
	public static final String	Mi_GRID_COLOR_NAME			= "Grid Color";
	public static final String	Mi_GRID_LOOK_NAME			= "Grid Look";
	public static final String	Mi_GRID_VISIBLE_NAME			= "Grid Visible";
	public static final String	Mi_GRID_PRINTABLE_NAME			= "Grid Printable";
	public static final String	Mi_GRID_MIN_VISIBLE_SPACING_NAME	= "Grid Min Pixel Size Visible";

	public static final String	Mi_GRID_LOOK_SPACING_MAJOR_MULTIPLE_NAME_NSP	= "GridLookSpacingMajorMultiple";
	public static final String	Mi_GRID_LOOK_SPACING_MINOR_MULTIPLE_NAME_NSP	= "GridLookSpacingMinorMultiple";
	public static final String	Mi_GRID_H_SPACING_NAME_NSP		= "GridHorizontalSpacing";
	public static final String	Mi_GRID_V_SPACING_NAME_NSP		= "GridVerticalSpacing";
	public static final String	Mi_GRID_H_SNAP_STRENGTH_NAME_NSP	= "gridHorizontalSnapStrength";
	public static final String	Mi_GRID_V_SNAP_STRENGTH_NAME_NSP	= "gridVerticalSnapStrength";
	public static final String	Mi_GRID_COLOR_NAME_NSP			= "GridColor";
	public static final String	Mi_GRID_LOOK_NAME_NSP			= "GridLook";
	public static final String	Mi_GRID_VISIBLE_NAME_NSP		= "GridVisible";
	public static final String	Mi_GRID_PRINTABLE_NAME_NSP		= "GridPrintable";
	public static final String	Mi_GRID_MIN_VISIBLE_SPACING_NAME_NSP	= "GridMinPixelSizeVisible";
	public static final String	Mi_GRID_MINOR_GRID_LOOK_COLOR_NAME_NSP	= "GridMinorGridLookColor";
	public static final String	Mi_GRID_MINOR_GRID_FILL_IN_POINTS_NAME_NSP= "GridMinorGridFillInPoints";
	public static final String	Mi_GRID_MAJOR_GRID_LOOK_SIZE_NAME_NSP	= "GridMajorGridLookSize";

	public static final String	Mi_GRID_NONE_LOOK_NAME			= "None";
	public static final String	Mi_GRID_POINTS_LOOK_NAME		= "Points";
	public static final String	Mi_GRID_CROSSHAIRS_LOOK_NAME		= "Crosshairs";
	public static final String	Mi_GRID_LINES_LOOK_NAME			= "Lines";
	public static final String	Mi_GRID_DOTTED_LINES_LOOK_NAME		= "DottedLines";
	public static final String	Mi_GRID_DASHED_LINES_LOOK_NAME		= "DashedLines";
	public static final String	Mi_GRID_SQUARES_LOOK_NAME		= "Squares";

	private static final int	Mi_GRID_POINTS_LOOK			= 0;
	private static final int	Mi_GRID_CROSSHAIRS_LOOK			= 1;
	private static final int	Mi_GRID_LINES_LOOK			= 2;
	private static final int	Mi_GRID_DOTTED_LINES_LOOK		= 3;
	private static final int	Mi_GRID_DASHED_LINES_LOOK		= 4;
	private static final int	Mi_GRID_SQUARES_LOOK			= 5;

	private static final String	Mi_NONE_COLOR				= "0x12345678";

	private static final String[]	gridLookNames =
		{
		Mi_GRID_POINTS_LOOK_NAME		,
		Mi_GRID_CROSSHAIRS_LOOK_NAME		,
		Mi_GRID_LINES_LOOK_NAME			,
		Mi_GRID_DOTTED_LINES_LOOK_NAME		,
		Mi_GRID_DASHED_LINES_LOOK_NAME		,
		Mi_GRID_SQUARES_LOOK_NAME		,
		};
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private		MiPart		paper;
	private		MiSize		gridSizeInUnits	= new MiSize();
	private		MiSize		gridSize	= new MiSize();
//	private		MiSize		gridLookSpacing;
	private		int		gridLook	= Mi_GRID_POINTS_LOOK;
	private		MiDistanceUnits units 		= MiiDistanceUnits.inches;
	private		MiBounds	tmpBounds	= new MiBounds();
	private		MiBounds	tmpBounds2	= new MiBounds();
	private		MiPoint		tmpPoint	= new MiPoint();
	private		MiPoint		tmpPoint2	= new MiPoint();
	private		MiSize		gridMinVisibleDeviceSize = new MiSize(15, 15);
	private		int		majorGridLookSpacing	= 1;
	private		int		minorGridLookSpacing	= 1;
	private		String		minorGridLookColorName	= Mi_NONE_COLOR;
	private		boolean		fillInMinorGrid;
	private		MiDistance	majorGridLookSize;



					// For copy operations
	protected			MiDrawingGrid()
		{
		this(null);
		}
	public				MiDrawingGrid(MiPart paper)
		{
		setPaper(paper);
		setGridSizeInUnits(new MiSize(0.5, 0.5));
		setSavable(false);
		setPickable(false);
		setPrintable(false);
		}

	public		void		assignCopyTo(MiDrawingGrid other)
		{
		other.setUnits(units);
		other.setGridSizeInUnits(gridSizeInUnits);
//		other.setGridLookSpacing(gridLookSpacing);
		other.setLook(gridLookNames[gridLook]);
		other.setMajorGridLookSpacingAsMultipleOfGridSpacing(majorGridLookSpacing);
		other.setMinorGridLookSpacingAsMultipleOfGridSpacing(minorGridLookSpacing);
		}

	public		void		setUnits(MiDistanceUnits units)
		{
		this.units = units;
		}
	public		MiDistanceUnits	getUnits()
		{
		return(units);
		}
	public		void		setPaper(MiPart part)
		{
		if (this.paper != null)
			{
			this.paper.removeActionHandlers(this);
			}

		this.paper = part;

		if (paper instanceof MiEditor)
			paper.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		else if (paper == null)
			this.paper = this;
		else
			paper.appendActionHandler(this, Mi_GEOMETRY_CHANGE_ACTION);

		invalidateLayout();
		}
	public		MiPart		getPaper()
		{
		return(paper);
		}
	public		void		setLook(String style)
		{
		gridLook = new Strings(gridLookNames).indexOfIgnoreCase(style);

		if (gridLook == Mi_GRID_DASHED_LINES_LOOK)
			{
			setLineStyle(Mi_DASHED_LINE_STYLE);
			}
		else if (gridLook == Mi_GRID_DOTTED_LINES_LOOK)
			{
			setLineStyle(Mi_DOTTED_LINE_STYLE);
			}
		else 
			{
			setLineStyle(Mi_SOLID_LINE_STYLE);
			}
		invalidateArea();
		}
	public		String		getLook()
		{
		return(gridLookNames[gridLook]);
		}
	public		void		setGridSizeInUnits(MiSize sizeInUnits)
		{
		gridSizeInUnits.copy(sizeInUnits);
		setGridSize(new MiSize(
			sizeInUnits.width * units.getPixelsPerUnit(),
			sizeInUnits.height * units.getPixelsPerUnit()));
		}
	public		MiSize		getGridSizeInUnits()
		{
		return(new MiSize(gridSizeInUnits));
		}
	public		void		setGridSize(MiSize size)
		{
		gridSize.copy(size);
		gridSizeInUnits.setSize(
			gridSize.width / units.getPixelsPerUnit(),
			gridSize.height / units.getPixelsPerUnit());
//MiDebug.printStackTrace(this.toString());
		invalidateArea();
		}
	public		MiSize		getGridSize()
		{
		return(new MiSize(gridSize));
		}
	public		MiSize		getMajorGridSize()
		{
		return(new MiSize(gridSize.width * majorGridLookSpacing, gridSize.height * majorGridLookSpacing));
		}
	public		void		setMajorGridLookSpacingAsMultipleOfGridSpacing(int spacing)
		{
		majorGridLookSpacing = spacing;
		}
	public		int		getMajorGridLookSpacingAsMultipleOfGridSpacing()
		{
		return(majorGridLookSpacing);
		}
	public		void		setMajorGridLookSize(MiDistance length)
		{
		majorGridLookSize = length;
		}
	public		MiDistance	getMajorGridLookSize()
		{
		return(majorGridLookSize);
		}
	/**
	 * Not used for dashed, dotted or solid lines, as their pixel spacing is fixed... 
	 */
	public		void		setMinorGridLookSpacingAsMultipleOfGridSpacing(int spacing)
		{
		minorGridLookSpacing = spacing;
		}
	public		int		getMinorGridLookSpacingAsMultipleOfGridSpacing()
		{
		return(minorGridLookSpacing);
		}
	public		void		setMinorGridLookColor(String name)
		{
		minorGridLookColorName = name;
		}
	public		String		getMinorGridLookColor()
		{
		return(minorGridLookColorName);
		}
	public		void		setFillInMinorGrid(boolean flag)
		{
		fillInMinorGrid = flag;
		}
	public		boolean		getFillInMinorGrid()
		{
		return(fillInMinorGrid);
		}
/***
	public		void		setGridLookSpacingInUnits(MiSize spacingInUnits)
		{
		setGridLookSpacing(new MiSize(
			spacingInUnits.width * units.getPixelsPerUnit(),
			spacingInUnits.height * units.getPixelsPerUnit()));
		}
	public		MiSize		getGridLookSpacingInUnits()
		{
		MiSize gridLookSpacingInUnits = new MiSize();
		MiSize gridLookSpacing = getGridLookSpacing();

		gridLookSpacingInUnits.setSize(
			gridLookSpacing.width / units.getPixelsPerUnit(),
			gridLookSpacing.height / units.getPixelsPerUnit());

		return(gridLookSpacingInUnits);
		}
	public		void		setGridLookSpacing(MiSize spacing)
		{
		gridLookSpacing = new MiSize(spacing);
		invalidateArea();
		}
	public		MiSize		getGridLookSpacing()
		{
		if (gridLookSpacing != null)
			{
			return(new MiSize(gridLookSpacing));
			}
		return(new MiSize(gridSize));
		}
*/
	public		void		setMinVisibleDeviceSize(MiSize size)
		{
		gridMinVisibleDeviceSize.copy(size);
		invalidateArea();
		}
	public		MiSize		getMinVisibleDeviceSize()
		{
		return(new MiSize(gridMinVisibleDeviceSize));
		}
	/**
	 * Lower left hand corner of grid
	 **/
/*
	public		void		setReferencePoint(MiPoint pt)
		{
		if (pt != null)
			{
			referencePt = new MiPoint(pt);
			}
		else
			{
			referencePt = null;
			}
		}
*/
	public		MiPoint		getReferencePoint(MiPoint pt)
		{
/*
		if (referencePt != null)
			{
			pt.copy(referencePt);
			return(pt);
			}
*/
//MiDebug.println(this + " getReferencePoint = " + getGridBounds(tmpBounds).getLLCorner(pt));
		return(getGridBounds(tmpBounds).getLLCorner(pt));
		}
	public		MiBounds	getGridBounds(MiBounds b)
		{
		reCalcBounds(b);
		if (paper instanceof MiEditor)
			{
			b = ((MiEditor )paper).getWorldBounds(b);
			b.xmin = (b.xmin % gridSize.width) + gridSize.width;
			b.ymin = (b.ymin % gridSize.height) + gridSize.height;
			}
		return(b);
		}
	public		MiBounds	getGridLookBounds(MiBounds b)
		{
		reCalcBounds(b);
		if (paper instanceof MiEditor)
			{
			MiSize gridLookSpacing = getGridSize();
			b.xmin = (b.xmin % gridLookSpacing.width) + gridLookSpacing.width;
			b.ymin = (b.ymin % gridLookSpacing.height) + gridLookSpacing.height;
			}
		return(b);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		name = Utility.replaceAll(name, " ", "");
		if (name.equalsIgnoreCase(Mi_GRID_H_SPACING_NAME_NSP))
			setGridSizeInUnits(new MiSize(Utility.toDouble(value), gridSizeInUnits.height));
		else if (name.equalsIgnoreCase(Mi_GRID_V_SPACING_NAME_NSP))
			setGridSizeInUnits(new MiSize(gridSizeInUnits.width, Utility.toDouble(value)));
		else if (name.equalsIgnoreCase(Mi_GRID_LOOK_SPACING_MAJOR_MULTIPLE_NAME_NSP))
			setMajorGridLookSpacingAsMultipleOfGridSpacing(Utility.toInteger(value));
		else if (name.equalsIgnoreCase(Mi_GRID_LOOK_SPACING_MINOR_MULTIPLE_NAME_NSP))
			setMinorGridLookSpacingAsMultipleOfGridSpacing(Utility.toInteger(value));
		else if (name.equalsIgnoreCase(Mi_GRID_H_SNAP_STRENGTH_NAME_NSP))
			;
		else if (name.equalsIgnoreCase(Mi_GRID_V_SNAP_STRENGTH_NAME_NSP))
			;
		else if (name.equalsIgnoreCase(Mi_GRID_COLOR_NAME_NSP))
			setColor(value);
		else if (name.equalsIgnoreCase(Mi_GRID_LOOK_NAME_NSP))
			setLook(value);
		else if (name.equalsIgnoreCase(Mi_GRID_VISIBLE_NAME_NSP))
			setVisible(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_GRID_PRINTABLE_NAME_NSP))
			setPrintable(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_GRID_MIN_VISIBLE_SPACING_NAME_NSP))
			setMinVisibleDeviceSize(new MiSize(Utility.toDouble(value), Utility.toDouble(value)));
		else if (name.equalsIgnoreCase(Mi_GRID_MINOR_GRID_LOOK_COLOR_NAME_NSP))
			setMinorGridLookColor(value);
		else if (name.equalsIgnoreCase(Mi_GRID_MINOR_GRID_FILL_IN_POINTS_NAME_NSP))
			setFillInMinorGrid(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_GRID_MAJOR_GRID_LOOK_SIZE_NAME_NSP))
			setMajorGridLookSize(Utility.toDouble(value));
		else
			super.setPropertyValue(name, value);
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		name = Utility.replaceAll(name, " ", "");
		if (name.equalsIgnoreCase(Mi_GRID_H_SPACING_NAME_NSP))
			return("" + gridSizeInUnits.width);
		else if (name.equalsIgnoreCase(Mi_GRID_V_SPACING_NAME_NSP))
			return("" + gridSizeInUnits.height);
		else if (name.equalsIgnoreCase(Mi_GRID_LOOK_SPACING_MAJOR_MULTIPLE_NAME_NSP))
			return("" + getMajorGridLookSpacingAsMultipleOfGridSpacing());
		else if (name.equalsIgnoreCase(Mi_GRID_LOOK_SPACING_MINOR_MULTIPLE_NAME_NSP))
			return("" + getMinorGridLookSpacingAsMultipleOfGridSpacing());
		else if (name.equalsIgnoreCase(Mi_GRID_H_SNAP_STRENGTH_NAME_NSP))
			return("");
		else if (name.equalsIgnoreCase(Mi_GRID_V_SNAP_STRENGTH_NAME_NSP))
			return("");
		else if (name.equalsIgnoreCase(Mi_GRID_COLOR_NAME_NSP))
			return(MiColorManager.getColorName(getColor()));
		else if (name.equalsIgnoreCase(Mi_GRID_LOOK_NAME_NSP))
			return(getLook());
		else if (name.equalsIgnoreCase(Mi_GRID_VISIBLE_NAME_NSP))
			return("" + isVisible());
		else if (name.equalsIgnoreCase(Mi_GRID_PRINTABLE_NAME_NSP))
			return("" + isPrintable());
		else if (name.equalsIgnoreCase(Mi_GRID_MIN_VISIBLE_SPACING_NAME_NSP))
			return(gridMinVisibleDeviceSize.width + "");
		else if (name.equalsIgnoreCase(Mi_GRID_MINOR_GRID_LOOK_COLOR_NAME_NSP))
			return(getMinorGridLookColor());
		else if (name.equalsIgnoreCase(Mi_GRID_MINOR_GRID_FILL_IN_POINTS_NAME_NSP))
			return("" + getFillInMinorGrid());
		else if (name.equalsIgnoreCase(Mi_GRID_MAJOR_GRID_LOOK_SIZE_NAME_NSP))
			return(Utility.toShortString(getMajorGridLookSize()));
		else
			return(super.getPropertyValue(name));
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = new MiPropertyDescriptions(getClass().getName());

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_VISIBLE_NAME, Mi_BOOLEAN_TYPE, "true"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_PRINTABLE_NAME, Mi_BOOLEAN_TYPE, "false"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_H_SPACING_NAME, Mi_DOUBLE_TYPE, "0.5"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_V_SPACING_NAME, Mi_DOUBLE_TYPE, "0.5"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_LOOK_SPACING_MAJOR_MULTIPLE_NAME, Mi_POSITIVE_INTEGER_TYPE, "1"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_LOOK_SPACING_MINOR_MULTIPLE_NAME, Mi_POSITIVE_INTEGER_TYPE, "1"));
/* NOT READY
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_H_SNAP_STRENGTH_NAME, Mi_DOUBLE_TYPE, "4"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_V_SNAP_STRENGTH_NAME, Mi_DOUBLE_TYPE, "4"));
***/
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_COLOR_NAME, Mi_COLOR_TYPE, "black"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_LOOK_NAME, new Strings(gridLookNames), Mi_GRID_POINTS_LOOK_NAME));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_MIN_VISIBLE_SPACING_NAME, Mi_DOUBLE_TYPE, "15.0"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_MAJOR_GRID_LOOK_SIZE_NAME_NSP, Mi_DOUBLE_TYPE, "15.0"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_MINOR_GRID_LOOK_COLOR_NAME_NSP, Mi_COLOR_TYPE, Mi_NONE_COLOR));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_GRID_MINOR_GRID_FILL_IN_POINTS_NAME_NSP, Mi_BOOLEAN_TYPE, "false"));


		return(propertyDescriptions);
		}

	public		boolean		processAction(MiiAction action)
		{
		invalidateLayout();
		return(true);
		}

	protected	void		reCalcBounds(MiBounds b)
		{
		if (paper instanceof MiEditor)
			{
			((MiEditor )paper).getWorldBounds(b);
			}
		else if (paper instanceof MiDrawingPages)
			{
			((MiDrawingPages )paper).getGridBounds(b);
			}
		else 
			{
			paper.getBounds(b);
			}
		}
	protected	void		calcMinimumSize(MiSize size)
		{
		reCalcBounds(tmpBounds);
		size.setSize(tmpBounds);
		}
	protected	void		calcPreferredSize(MiSize size)
		{
		calcMinimumSize(size);
		}
	protected	void		render(MiRenderer renderer)
		{
//MiDebug.println(this + " render: " + getBounds());
//MiDebug.println(this + " fillInMinorGrid: " + fillInMinorGrid);
//MiDebug.println(this + " minorGridLookColorName: " + minorGridLookColorName);
//MiDebug.println(this + " minorGridLookSpacing: " + minorGridLookSpacing);
//MiDebug.println(this + " fillInMinorGrid: " + fillInMinorGrid);
//MiDebug.println(this + " gridLook: " + gridLook);
//MiDebug.println(this + " majorGridLookColor: " + getColor());
		Color majorGridLookColor = getColor();
		Color minorGridLookColor = null;
		if (Mi_NONE_COLOR.equals(minorGridLookColorName))
			{
			minorGridLookColor = majorGridLookColor;
			}
		else
			{
			minorGridLookColor = MiColorManager.getColor(minorGridLookColorName);
			}

		MiSize gridSize = getGridSize();
		if (gridLook == Mi_GRID_POINTS_LOOK)
			{
			gridSize.height = gridSize.height * minorGridLookSpacing;
			if ((majorGridLookSpacing != 1) || (minorGridLookSpacing != 1))
				{
				if (fillInMinorGrid)
					{
					gridSize.width = gridSize.width * minorGridLookSpacing;
					renderDots(renderer, gridSize, minorGridLookColor);
					}
				else
					{
					gridSize.width = gridSize.width * majorGridLookSpacing;
					renderDots(renderer, gridSize, minorGridLookColor);

					MiDistance tmp = gridSize.width;
					gridSize.width = gridSize.height;
					gridSize.height = tmp;

					renderDots(renderer, gridSize, minorGridLookColor);
					}

				gridSize = getGridSize();
				gridSize.width = gridSize.width * majorGridLookSpacing;
				gridSize.height = gridSize.height * majorGridLookSpacing;
				}
			renderDots(renderer, gridSize, majorGridLookColor);
			}
		else if (gridLook == Mi_GRID_CROSSHAIRS_LOOK)
			{
			gridSize.width = gridSize.width * majorGridLookSpacing;
			gridSize.height = gridSize.height * minorGridLookSpacing;
			if ((majorGridLookSpacing != 1) || (minorGridLookSpacing != 1))
				{
				if (fillInMinorGrid)
					{
					gridSize.width = getGridSize().width * minorGridLookSpacing;
					renderCrossHairs(renderer, gridSize, minorGridLookColor);
					}
				else
					{
					renderCrossHairs(renderer, gridSize, minorGridLookColor);

					MiDistance tmp = gridSize.width;
					gridSize.width = gridSize.height;
					gridSize.height = tmp;

					renderCrossHairs(renderer, gridSize, minorGridLookColor);
					}

				gridSize = getGridSize();
				gridSize.width = gridSize.width * majorGridLookSpacing;
				gridSize.height = gridSize.height * majorGridLookSpacing;
				}
			renderCrossHairs(renderer, gridSize, majorGridLookColor);
			}
		else if (gridLook == Mi_GRID_SQUARES_LOOK)
			{
			MiDistance majorLookSize = majorGridLookSize * units.getPixelsPerUnit();

			gridSize.width = gridSize.width * majorGridLookSpacing;
			gridSize.height = gridSize.height * minorGridLookSpacing;
			if ((majorGridLookSpacing != 1) || (minorGridLookSpacing != 1))
				{
				if (fillInMinorGrid)
					{
					gridSize.width = getGridSize().width * minorGridLookSpacing;
					renderSquares(renderer, gridSize, minorGridLookColor, 0);
					}
				else
					{
					renderSquares(renderer, gridSize, minorGridLookColor, 0);

					MiDistance tmp = gridSize.width;
					gridSize.width = gridSize.height;
					gridSize.height = tmp;

					renderSquares(renderer, gridSize, minorGridLookColor, 0);
					}

				gridSize = getGridSize();
				gridSize.width = gridSize.width * majorGridLookSpacing;
				gridSize.height = gridSize.height * majorGridLookSpacing;
				}
			renderSquares(renderer, gridSize, majorGridLookColor, majorLookSize);
			}
		else // if ((gridLook == Mi_GRID_LINES_LOOK) 
		// 	|| (gridLook == Mi_GRID_DOTTED_LINES_LOOK) 
		// 	|| (gridLook == Mi_GRID_DASHED_LINES_LOOK))
			{
			gridSize.height = gridSize.height * majorGridLookSpacing;
			renderLines(renderer, gridSize);
			}
		}
	protected	void		renderDots(MiRenderer renderer, MiSize gridSize, Color color)
		{
//MiDebug.println(this + " render dots");
		tmpBounds.setBounds(0,0,gridSize.width, gridSize.height);
		renderer.getTransform().wtod(tmpBounds, tmpBounds);
		if (((int )(tmpBounds.getWidth() + 0.5) < gridMinVisibleDeviceSize.width)
			|| (((int )(tmpBounds.getHeight() + 0.5) < gridMinVisibleDeviceSize.height)))
			{
			return;
			}

		MiBounds gridBounds = getGridLookBounds(tmpBounds);
		MiBounds clipBounds = renderer.getClipBounds(tmpBounds2);

		renderer.setAttributes(getAttributes());
		renderer.setColor(color);

		MiCoord xmin = gridBounds.xmin;
//MiDebug.println(this + "render dots" + gridBounds);
//MiDebug.println(this + "still render dots" + color);

		if (xmin < clipBounds.xmin)
			{
			xmin = ((int )((clipBounds.xmin - gridBounds.xmin)/gridSize.width))
				 * gridSize.width + gridBounds.xmin;
			}

		MiCoord ymin = gridBounds.ymin;
		if (ymin < clipBounds.ymin)
			{
			ymin = ((int )((clipBounds.ymin - gridBounds.ymin)/gridSize.height))
				 * gridSize.height + gridBounds.ymin;
			}

		MiDistance width = gridSize.width;
		MiDistance height = gridSize.height;
		for (MiCoord x = xmin; x <= gridBounds.xmax; x += width)
			{
			for (MiCoord y = ymin; y <= gridBounds.ymax; y += height)
				{
				tmpPoint.x = x;
				tmpPoint.y = y;

//MiDebug.println(this + "still render dots" + x + "," + y);
				renderer.drawPoint(tmpPoint);

				if (y > clipBounds.ymax)
					break;
				}
			if (x > clipBounds.xmax)
				break;
			}
		}
	protected	void		renderSquares(MiRenderer renderer, MiSize gridSize, Color color, MiDistance squareSize)
		{
//MiDebug.println(this + " render squares");
		tmpBounds.setBounds(0,0,gridSize.width, gridSize.height);
		renderer.getTransform().wtod(tmpBounds, tmpBounds);
		if (((int )(tmpBounds.getWidth() + 0.5) < gridMinVisibleDeviceSize.width)
			|| (((int )(tmpBounds.getHeight() + 0.5) < gridMinVisibleDeviceSize.height)))
			{
			return;
			}

		MiBounds gridBounds = getGridLookBounds(tmpBounds);
		MiBounds clipBounds = renderer.getClipBounds(tmpBounds2);

//MiDebug.println(this + "render rects" + gridBounds);
		MiVector dVector = new MiVector();
		renderer.getTransform().wtod(getCenter(), new MiVector(squareSize, squareSize), dVector);
		MiDistance squareSizeInDeviceCoordinates = dVector.x;
		

		renderer.setAttributes(getAttributes());
		renderer.setColor(color);
		renderer.setBackgroundColor(color);

		MiCoord xmin = gridBounds.xmin;

		if (xmin < clipBounds.xmin)
			{
			xmin = ((int )((clipBounds.xmin - gridBounds.xmin)/gridSize.width))
				 * gridSize.width + gridBounds.xmin;
			}

		MiCoord ymin = gridBounds.ymin;
		if (ymin < clipBounds.ymin)
			{
			ymin = ((int )((clipBounds.ymin - gridBounds.ymin)/gridSize.height))
				 * gridSize.height + gridBounds.ymin;
			}

		MiDistance width = gridSize.width;
		MiDistance height = gridSize.height;
		MiDistance halfSquareSize = squareSize/2;
		for (MiCoord x = xmin; x <= gridBounds.xmax; x += width)
			{
			for (MiCoord y = ymin; y <= gridBounds.ymax; y += height)
				{
				if (squareSizeInDeviceCoordinates < 2)
					{
					tmpPoint.x = x;
					tmpPoint.y = y;

					renderer.drawPoint(tmpPoint);
					}
				else
					{
//MiDebug.println("render square: x=" + x + ", y= " + y + ", halfSquareSize=" + halfSquareSize + ",squareSize= " + squareSize);
					renderer.drawRect(x - halfSquareSize, y - halfSquareSize, x + halfSquareSize, y + halfSquareSize);
					}

				if (y > clipBounds.ymax)
					break;
				}
			if (x > clipBounds.xmax)
				break;
			}
		}
	protected	void		renderCrossHairs(MiRenderer renderer, MiSize gridSize, Color color)
		{
		tmpBounds.setBounds(0,0,gridSize.width, gridSize.height);
		renderer.getTransform().wtod(tmpBounds, tmpBounds);
		if (((int )(tmpBounds.getWidth() + 0.5) < gridMinVisibleDeviceSize.width)
			|| (((int )(tmpBounds.getHeight() + 0.5) < gridMinVisibleDeviceSize.height)))
			{
			return;
			}

		MiBounds gridBounds = getGridLookBounds(tmpBounds);
		MiBounds clipBounds = renderer.getClipBounds(tmpBounds2);

		renderer.setAttributes(getAttributes());
		renderer.setColor(color);

		MiCoord xmin = gridBounds.xmin;

		if (xmin < clipBounds.xmin)
			{
			xmin = ((int )((clipBounds.xmin - gridBounds.xmin)/gridSize.width))
				 * gridSize.width + gridBounds.xmin;
			}

		MiCoord ymin = gridBounds.ymin;
		if (ymin < clipBounds.ymin)
			{
			ymin = ((int )((clipBounds.ymin - gridBounds.ymin)/gridSize.height))
				 * gridSize.height + gridBounds.ymin;
			}

		MiDistance width = gridSize.width;
		MiDistance height = gridSize.height;
		MiDistance xHalfWidth = width/6;
		MiDistance yHalfWidth = height/6;
		for (MiCoord x = xmin; x <= gridBounds.xmax; x += width)
			{
			for (MiCoord y = ymin; y <= gridBounds.ymax; y += height)
				{
				renderer.drawLine(x - xHalfWidth, y, x + xHalfWidth, y);
				renderer.drawLine(x, y - yHalfWidth, x, y + yHalfWidth);

				if (y > clipBounds.ymax)
					break;
				}
			if (x > clipBounds.xmax)
				break;
			}
		}
	protected	void		renderLines(MiRenderer renderer, MiSize gridSize)
		{
		tmpBounds.setBounds(0,0,gridSize.width, gridSize.height);
		renderer.getTransform().wtod(tmpBounds, tmpBounds);
		if (((int )(tmpBounds.getWidth() + 0.5) < gridMinVisibleDeviceSize.width)
			|| (((int )(tmpBounds.getHeight() + 0.5) < gridMinVisibleDeviceSize.height)))
			{
			return;
			}

		MiBounds gridBounds = getGridLookBounds(tmpBounds);
		MiBounds clipBounds = renderer.getClipBounds(tmpBounds2);

		renderer.setAttributes(getAttributes());

		MiCoord xmin = gridBounds.xmin;

		if (xmin < clipBounds.xmin)
			{
			xmin = ((int )((clipBounds.xmin - gridBounds.xmin)/gridSize.width))
				 * gridSize.width + gridBounds.xmin;
			}

		MiCoord ymin = gridBounds.ymin;
		if (ymin < clipBounds.ymin)
			{
			ymin = ((int )((clipBounds.ymin - gridBounds.ymin)/gridSize.height))
				 * gridSize.height + gridBounds.ymin;
			}

		MiDistance width = gridSize.width;
		MiDistance height = gridSize.height;
		for (MiCoord x = xmin; x <= gridBounds.xmax; x += width)
			{
			renderer.drawLine(x, ymin, x, gridBounds.ymax);
			}
		for (MiCoord y = ymin; y <= gridBounds.ymax; y += height)
			{
			renderer.drawLine(xmin, y, gridBounds.xmax, y);
			}
		}
	public		String		toString()
		{
		return(super.toString() + "[gridSizeInUnits=" + gridSizeInUnits +",gridSize=" + gridSize + "]");
		}
	}

