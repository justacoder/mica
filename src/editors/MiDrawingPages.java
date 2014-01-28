
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
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
// MiDrawingSurface
public class MiDrawingPages extends MiVisibleContainer 
				implements MiiDistanceUnits, MiiTypes, MiiPropertyTypes
	{
	public static final String		Mi_COORDINATES_ORIGIN_CENTER_OF_PAGE = "coordinates-origin-is-page-center";
	public static final String		Mi_COORDINATES_ORIGIN_LOWER_LEFT_OF_PAGE = "coordinates-origin-is-page-lower-left";
	private		MiSize			paperSize		= new MiSize();
	private		boolean			landscapeOrientation 	= true;
	private		int			pagesWide		= 1;
	private		int			pagesTall		= 1;
	private		int			updatePageBorderGraphicsDisabled;
	private		String			coordinateOrigin	= Mi_COORDINATES_ORIGIN_CENTER_OF_PAGE;
	private		boolean			drawingBorderGraphicsOutsideOfPage;
	private		MiGridLayout		gridLayout;
	private		MiDrawingGrid		grid;
	private		MiContainer		pages;
	private		MiiPageBorderGraphicsGenerator	paperLookGenerator 	= new MiBasicPageBorderGraphicsGenerator();


	public				MiDrawingPages()
		{
		setInsetMargins(0);
		setVisibleContainerAutomaticLayoutEnabled(false);
		setBackgroundColor("white");
		setBorderLook(Mi_NONE);
		setColor(MiColorManager.black);
		setHasShadow(true);
		setShadowColor(MiColorManager.gray);
		setShadowLength(12);
		setFixedWidth(true);
		setFixedHeight(true);
		setSavable(false);
		setPrintable(false);
		setPickable(false);

		pages = new MiContainer();
		appendPart(pages);

		gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(1);
		gridLayout.setAlleyHSpacing(0);
		gridLayout.setAlleyVSpacing(0);
		pages.setLayout(gridLayout);

//MiDebug.traceActions(this, MiiActionTypes.Mi_GEOMETRY_CHANGE_ACTION,
//MiDebug.Mi_LOG_PRINT_CHANGE_EVENT | MiDebug.Mi_LOG_PRINT_STACK | MiDebug.Mi_LOG_PRINT_BOUNDS);
		}

	public		void		setPageBorderGraphicsGenerator(MiiPageBorderGraphicsGenerator generator)
		{
		paperLookGenerator = generator;
		updatePageBorderGraphics();
		}
	public		MiiPageBorderGraphicsGenerator	getPageBorderGraphicsGenerator()
		{
		return(paperLookGenerator);
		}

	public		void		setUpdatePageBorderGraphicsDisabled(boolean flag)
		{
//MiDebug.printStackTrace(this + "updatePageBorderGraphicsDisabled was: " + updatePageBorderGraphicsDisabled);
		updatePageBorderGraphicsDisabled = flag ? 1 : 0;
		}
	public		boolean		isUpdatePageBorderGraphicsDisabled()
		{
		return(updatePageBorderGraphicsDisabled > 0);
		}
	public		int		pushUpdatePageBorderGraphicsDisabled()
		{
//MiDebug.printStackTrace(this + "updatePageBorderGraphicsDisabled was: " + updatePageBorderGraphicsDisabled);
		++updatePageBorderGraphicsDisabled;
		return(updatePageBorderGraphicsDisabled);
		}
	public		boolean		popUpdatePageBorderGraphicsDisabled()
		{
//MiDebug.printStackTrace(this + "updatePageBorderGraphicsDisabled was: " + updatePageBorderGraphicsDisabled);
		if (updatePageBorderGraphicsDisabled > 0)
			{
			--updatePageBorderGraphicsDisabled;
			}
		return(updatePageBorderGraphicsDisabled > 0);
		}

	public		void		setDrawingBorderGraphicsOutsideOfPage(boolean flag)
		{
		drawingBorderGraphicsOutsideOfPage = flag;
		updatePageBorderGraphics();
		}
	public		boolean		isDrawingBorderGraphicsOutsideOfPage()
		{
		return(drawingBorderGraphicsOutsideOfPage);
		}
	public		void		setCoordinateOrigin(String origin)
		{
		coordinateOrigin = origin;
		updatePageBorderGraphics();
		}
	public		String		getCoordinateOrigin()
		{
		return(coordinateOrigin);
		}

	public		int		setPageBorderVisible(boolean flag)
		{
//MiDebug.printStackTrace(this + "updatePageBorderGraphicsDisabled was: " + updatePageBorderGraphicsDisabled);
		if (flag)
			{
			popUpdatePageBorderGraphicsDisabled();
			updatePageBorderGraphics();
			}
		else
			{
			++updatePageBorderGraphicsDisabled;
			pages.removeAllParts();
			invalidateArea();
			}
		return(updatePageBorderGraphicsDisabled);
		}

	public		void		setGrid(MiDrawingGrid grid)
		{
		if (this.grid != grid)
			{
			if (this.grid != null)
				{
				removePart(grid);
				}
			}
			
		this.grid = grid;
		if (grid != null)
			{
			appendPart(grid);
			grid.setPaper(this);
			}
		}
	public		MiDrawingGrid	getGrid()
		{
		return(grid);
		}

	public		MiPoint		getReferenceGridPoint()
		{
		if (grid != null)
			{
			return(grid.getReferencePoint(new MiPoint()));
			}
		return(new MiPoint());
		}
	public		MiSize		getGridSize()
		{
		if (grid != null)
			{
			return(grid.getGridSize());
			}
		return(new MiSize());
		}

	public		void		setCharacteristics(int orientation, MiSize paperSize, int pagesWide, int pagesTall)
		{
		int origDisabled = updatePageBorderGraphicsDisabled;
		updatePageBorderGraphicsDisabled = 1;

		setOrientation(orientation);
		setPaperSize(paperSize);
		setPagesWide(pagesWide);
		setPagesTall(pagesTall);

		updatePageBorderGraphicsDisabled = origDisabled;
		if (origDisabled == 0)
			{
			//updatePageBorderGraphicsDisabled = 0;
			updatePageBorderGraphics();
			}
		}
	public		void		setOrientation(int orientation)
		{
		if (orientation == Mi_VERTICAL)
			landscapeOrientation = false;
		else
			landscapeOrientation = true;

		updatePageBorderGraphics();
		}
	public		int		getOrientation()
		{
		if (landscapeOrientation)
			return(Mi_HORIZONTAL);
		return(Mi_VERTICAL);
		}
	public		void		setPaperSize(MiSize size)
		{
		paperSize.copy(size);
		updatePageBorderGraphics();
		}
	public		MiSize		getPaperSize()
		{
		return(new MiSize(paperSize));
		}

	public		void		getBoundsOfContentArea(MiBounds b)
		{
		getBounds(b);
//MiDebug.println(this + "Drawing area bounds = " + b);
		if (drawingBorderGraphicsOutsideOfPage)
			{
			MiDistance borderHThickness = Utility.toDouble(
				paperLookGenerator.getModel().getPropertyValue(
					paperLookGenerator.Mi_BORDER_HORIZONTAL_THICKNESS_NAME));
			MiDistance borderVThickness = Utility.toDouble(
				paperLookGenerator.getModel().getPropertyValue(
					paperLookGenerator.Mi_BORDER_VERTICAL_THICKNESS_NAME));

			b.xmin += borderHThickness;
			b.ymin += borderVThickness;
			b.xmax -= borderHThickness;
			b.ymax -= borderVThickness;
			}
//MiDebug.println("NOW drawing area bounds = " + b);
		}

	public		void		setPagesWide(int numPages)
		{
		pagesWide = numPages;
		updatePageBorderGraphics();
		}
	public		int		getPagesWide()
		{
		return(pagesWide);
		}
	public		void		setPagesTall(int numPages)
		{
		pagesTall = numPages;
		updatePageBorderGraphics();
		}
	public		int		getPagesTall()
		{
		return(pagesTall);
		}

/*
	public		void		setAttributes(MiAttributes atts)
		{
		super.setAttributes(atts);

		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			getPart(i).setAttributes(getAttributes());
			getPart(i).setBorderLook(Mi_FLAT_BORDER_LOOK);
			getPart(i).setBackgroundColor(Mi_TRANSPARENT_COLOR);
			getPart(i).setHasShadow(false);
			}
		}
*/

	public		void		updatePageBorderGraphics()
		{
//MiDebug.printStackTrace(this + " updatePageBorderGraphics: " + paperSize);
//MiDebug.println(this + " updatePageBorderGraphicsDisabled: " + updatePageBorderGraphicsDisabled);
		MiDistance width = paperSize.width;
		MiDistance height = paperSize.height;

		MiDistance borderHThickness = 0;
		MiDistance borderVThickness = 0;
//MiDebug.println("updatePageBorderGraphics - drawingBorderGraphicsOutsideOfPage = " + drawingBorderGraphicsOutsideOfPage);
		if (drawingBorderGraphicsOutsideOfPage)
			{
//MiDebug.println("updatePageBorderGraphics - paperLookGenerator.getModel() = " + paperLookGenerator.getModel());
			borderHThickness = Utility.toDouble(
				paperLookGenerator.getModel().getPropertyValue(
					paperLookGenerator.Mi_BORDER_HORIZONTAL_THICKNESS_NAME));
			borderVThickness = Utility.toDouble(
				paperLookGenerator.getModel().getPropertyValue(
					paperLookGenerator.Mi_BORDER_VERTICAL_THICKNESS_NAME));

			width += 2 * borderHThickness;
			height += 2 * borderVThickness;
			}

		MiPoint center = getCenter();
//MiDebug.println("updatePageBorderGraphics - bounds = " + getBounds());
//MiDebug.println("refPt - = " + refPt);

		setFixedWidth(false);
		setFixedHeight(false);

		if (updatePageBorderGraphicsDisabled > 0)
			{
			if (landscapeOrientation)
				{
				setSize(height * pagesTall, width * pagesWide);
				}
			else
				{
				setSize(width * pagesWide, height * pagesTall);
				}
			if (Mi_COORDINATES_ORIGIN_LOWER_LEFT_OF_PAGE.equals(coordinateOrigin))
				{
				if (!drawingBorderGraphicsOutsideOfPage)
					{
					setXmin(0);
					setYmin(0);
					}
				else
					{
					setBounds(-borderHThickness, -borderVThickness, 
						getWidth() + borderHThickness, getHeight() + borderVThickness);
					//setXmin(-borderHThickness);
					//setYmin(-borderVThickness);
					}
				}
//MiDebug.println(" now updatePageBorderGraphics while disabled - bounds = " + getBounds());
			setPreferredSize(getSize(new MiSize()));

			return;
			}
//MiDebug.println(this + "continuing updatePageBorderGraphics");

//MiDebug.println(this + "width" + width);
//MiDebug.println(this + "height" + height);
		pages.removeAllParts();

		for (int i = 0; i < pagesWide * pagesTall; ++i)
			{
			MiPart paper = null;

			if (landscapeOrientation)
 				paper = paperLookGenerator.getPageBorderGraphics(this, height, width, i);
			else
 				paper = paperLookGenerator.getPageBorderGraphics(this, width, height, i);

//MiDebug.println("paper = " + paper);
//MiDebug.println("paperLookGenerator = " + paperLookGenerator);
//MiDebug.println("paper.getBounds() = " + paper.getBounds());
			if (paper != null)
				{
				pages.appendPart(paper);
				}
			}


		if (landscapeOrientation)
			{
			pages.setSize(height * pagesTall, width * pagesWide);
			gridLayout.setNumberOfColumns(pagesTall);
			}
		else
			{
			pages.setSize(width * pagesWide, height * pagesTall);
			gridLayout.setNumberOfColumns(pagesWide);
			}

		invalidateArea();

//MiDebug.println(this + " 0 NOW PAGES BOUNDS = " + pages.getBounds());
		pages.setPreferredSize(pages.getSize(new MiSize()));
//MiDebug.println(this + " 1 NOW PAGES BOUNDS = " + pages.getBounds());
//MiDebug.println(this + " 1 NOW PAGES size = " + pages.getSize(new MiSize()));
//MiDebug.println(this + " coordinateOrigin=" + coordinateOrigin);

		if (Mi_COORDINATES_ORIGIN_LOWER_LEFT_OF_PAGE.equals(coordinateOrigin))
			{
			MiCoord coordinatesOfXmin = 0;
			MiCoord coordinatesOfYmin = 0;

			if (drawingBorderGraphicsOutsideOfPage)
				{
				coordinatesOfXmin = -borderHThickness;
				coordinatesOfYmin = -borderVThickness;
				}

			setSize(pages.getSize(new MiSize()));

			setXmin(coordinatesOfXmin);
			setYmin(coordinatesOfYmin);

			pages.setXmin(coordinatesOfXmin);
			pages.setYmin(coordinatesOfYmin);

			center = pages.getCenter();
			}
		else
			{
			pages.setCenter(center);
			}
//MiDebug.println(this + "2 NOW PAGES BOUNDS = " + pages.getBounds());

		if (grid != null)
			{
			MiBounds gridBounds = new MiBounds();

			pages.getBounds(gridBounds);
//MiDebug.println("updatePageBorderGraphics - drawingBorderGraphicsOutsideOfPage = " + drawingBorderGraphicsOutsideOfPage);
//MiDebug.println("updatePageBorderGraphics - gridBounds=" + gridBounds);
			if (drawingBorderGraphicsOutsideOfPage)
				{
				gridBounds.xmin += borderHThickness;
				gridBounds.ymin += borderVThickness;
				gridBounds.xmax -= borderHThickness;
				gridBounds.ymax -= borderVThickness;
//MiDebug.println("222 updatePageBorderGraphics - gridBounds=" + gridBounds);
				} 
			grid.setBounds(gridBounds);
			}

		setSize(pages.getSize(new MiSize()));
		setCenter(center);

		setPreferredSize(getSize(new MiSize()));

		validateLayout();
//MiDebug.println(this + "NOW BOUNDS = " + getBounds());
//if (grid != null)
//MiDebug.println(this + "grid BOUNDS = " + grid.getBounds());
//MiDebug.println("NOWWWWWWWWWWWWWWWWWWWWWWWWWWWWW updatePageBorderGraphics - bounds = " + getBounds());
		}
	public		void		getGridBounds(MiBounds b)
		{
		pages.getBounds(b);
		getBounds(b);
		if (drawingBorderGraphicsOutsideOfPage)
			{
			MiDistance borderHThickness = Utility.toDouble(
				paperLookGenerator.getModel().getPropertyValue(
					paperLookGenerator.Mi_BORDER_HORIZONTAL_THICKNESS_NAME));
			MiDistance borderVThickness = Utility.toDouble(
				paperLookGenerator.getModel().getPropertyValue(
					paperLookGenerator.Mi_BORDER_VERTICAL_THICKNESS_NAME));

			b.xmin += borderHThickness;
			b.ymin += borderVThickness;
			b.xmax -= borderHThickness;
			b.ymax -= borderVThickness;
			} 
		}
	protected	void		getBorderBounds(MiBounds b)
		{
		if (!pages.hasValidLayout())
			{
			pages.validateLayout();
			}
		b.setSize(pages.getPreferredSize(new MiSize()));
		b.setCenter(pages.getCenter());
		}
	protected	void		render(MiRenderer renderer)
		{
		if (grid != null)
			{
			pages.setVisible(false);
			super.render(renderer);
			pages.setVisible(true);
			//grid.render(renderer);
//MiDebug.println("render pages");
//MiDebug.dump(this);
			pages.render(renderer);
			}
		else
			{
			super.render(renderer);
			}
		}
	}


