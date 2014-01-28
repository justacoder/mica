
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
import java.util.Vector;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPageManager extends MiModelEntity implements MiiDistanceUnits, MiiTypes, MiiPropertyTypes, MiiNames
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;

	public static final String	Mi_SNAP_TO_GRID_ENABLED_NAME		= "Snap-to-grid";

	public static final String	Mi_PAGE_SIZE_CHANGED_ACTION_NAME	= "pageSizeChanged";
	public static final int		Mi_PAGE_SIZE_CHANGED_ACTION		= MiActionManager.registerAction(
										Mi_PAGE_SIZE_CHANGED_ACTION_NAME);
	public static final String	Mi_PAGE_MANAGER_CHANGED_ACTION_NAME	= "pageManagerChanged";
	public static final int		Mi_PAGE_MANAGER_CHANGED_ACTION		= MiActionManager.registerAction(
										Mi_PAGE_MANAGER_CHANGED_ACTION_NAME);

	public static final String	Mi_PAGE_UNITS_CHANGED_ACTION_NAME	= "pageUnitsChanged";
	public static final int		Mi_PAGE_UNITS_CHANGED_ACTION		= MiActionManager.registerAction(
										Mi_PAGE_UNITS_CHANGED_ACTION_NAME);

	public static final String	Mi_PAGE_MANAGER_CHANGED_REFERENCE_GRID_POINT_ACTION_NAME= "pageManagerMovedGrid";
	public static final int		Mi_PAGE_MANAGER_CHANGED_REFERENCE_GRID_POINT_ACTION = MiActionManager.registerAction(
										Mi_PAGE_MANAGER_CHANGED_REFERENCE_GRID_POINT_ACTION_NAME);

	public final static String	Mi_PAGE_SIZE_NAME		= "Page size";
	public final static String	Mi_PAGES_WIDE_NAME		= "Pages wide";
	public final static String	Mi_PAGES_TALL_NAME		= "Pages tall";
	public final static String	Mi_PAGE_BORDER_NAME		= "Page border style";
	public final static String	Mi_PAGE_ORIENTATION_NAME	= "Orientation";
	public final static String	Mi_PAGE_BORDER_COLOR_NAME	= "Border color";
	public final static String	Mi_PAGE_BORDER_BACKGROUND_COLOR_NAME	= "Border background color";
	public final static String	Mi_PAGE_BORDER_TITLE_BLOCK_BACKGROUND_COLOR_NAME	= "Border Title Block background color";
	public final static String	Mi_PAGE_BACKGROUND_COLOR_NAME	= "Page background color";
	public final static String	Mi_LANDSCAPE_NAME		= "Landscape";
	public final static String	Mi_PORTRAIT_NAME		= "Portrait";
	public final static String	Mi_PAGE_SIZE_NAME_NSP		= "Pagesize";
	public final static String	Mi_PAGES_WIDE_NAME_NSP		= "Pageswide";
	public final static String	Mi_PAGES_TALL_NAME_NSP		= "Pagestall";
	public final static String	Mi_PAGE_BORDER_NAME_NSP		= "PageBorderStyle";
	public final static String	Mi_PAGE_BORDER_BACKGROUND_COLOR_NAME_NSP	= "BorderBackgroundColor";
	public final static String	Mi_PAGE_BORDER_TITLE_BLOCK_BACKGROUND_COLOR_NAME_NSP	= "BorderTitleBlockBackgroundColor";
	public final static String	Mi_PAGE_BACKGROUND_COLOR_NAME_NSP= "PageBackgroundColor";
	public final static String	Mi_PAGE_BORDER_COLOR_NAME_NSP	= "BorderColor";

	private		MiPaperSize	pageSizeSpec		= null; // MiiPaperSize.standardLetter;
	private		MiSize		pageSize		= new MiSize();
	private		MiDistanceUnits	units 			= MiiDistanceUnits.inches;
	private		MiSize		pageSizeInUnits		= new MiSize();
	private		int		orientation	 	= Mi_HORIZONTAL;
	private		int		pagesWide		= 1;
	private		int		pagesTall		= 1;

	private		MiEditor	editor;

	private		MiDrawingPages	drawingPages;
	private		MiDrawingGrid	drawingGrid;
	private		MiSnapManager	snapManager;
	private		MiLayerManager	layerManager;
	private		MiContainer	backgroundLayer;

	private		boolean		hasLayers;
	private		boolean		hasDrawingGrid;
	private		boolean		hasDrawingPages;

	private		MiBounds	tmpBounds		= new MiBounds();
	private 	boolean		pageOriginAtUpperLeft;
	private 	boolean		includeAccessToDrawingGridProperties = true;
	private 	boolean		updatingSuperProperties;
	private 	Vector		registeredPageBorderGraphicsGenerators;



	public				MiPageManager()
		{
		registeredPageBorderGraphicsGenerators = new Vector();
		registeredPageBorderGraphicsGenerators.addElement(new MiBasicPageBorderGraphicsGenerator());
		registeredPageBorderGraphicsGenerators.addElement(new MiNoPageBorderGraphicsGenerator());
		}

			void		setEditor(MiEditor editor)
		{
		this.editor = editor;
		setPageSize(MiiPaperSize.standardLetter);
		}
	public		MiEditor	getEditor()
		{
		return(editor);
		}
	public		void		assignCopyTo(MiEditor otherEditor)
		{
		MiPageManager pageManager = new MiPageManager();
		otherEditor.setPageManager(pageManager);

		pageManager.setIncludeAccessToDrawingGridProperties(includeAccessToDrawingGridProperties);
		pageManager.setOrientation(orientation);
		pageManager.setPageSize(pageSizeSpec);
		pageManager.setPageSizeInUnits(pageSizeInUnits);
		pageManager.setUnits(units);
		pageManager.setPagesWide(pagesWide);
		pageManager.setPagesTall(pagesTall);
		pageManager.setPageOriginAtUpperLeft(pageOriginAtUpperLeft);

		pageManager.setHasLayers(hasLayers);
		if ((hasDrawingPages) && (hasDrawingGrid) 
			&& (backgroundLayer.getIndexOfPart(drawingGrid) < backgroundLayer.getIndexOfPart(drawingPages)))
			{
			// Preserve order of grid and border graphics
			pageManager.setHasDrawingGrid(hasDrawingGrid);
			pageManager.setHasDrawingPages(hasDrawingPages);
			}
		else
			{
			pageManager.setHasDrawingPages(hasDrawingPages);
			pageManager.setHasDrawingGrid(hasDrawingGrid);
			}
		if (hasDrawingGrid)
			{
			drawingGrid.assignCopyTo(pageManager.drawingGrid);
			}
		}
	public		void		setDrawingGrid(MiDrawingGrid drawingGrid)
		{
		this.drawingGrid = drawingGrid;
		drawingGrid.setUnits(units);
		}
	public		MiDrawingGrid	getDrawingGrid()
		{
		return(drawingGrid);
		}

	public		void		setHasLayers(boolean flag)
		{
		if (flag)
			{
			if (layerManager == null)
				{
				layerManager = new MiLayerManager(editor);
				layerManager.setKeepConnectionsBelowNodes(true);
				layerManager.appendNewLayer();
				backgroundLayer = (MiContainer )editor.getLayer(0);
				backgroundLayer.setSavable(false);
				// backgroundLayer.setPrintable(false); // 12-27-2002 Now grids and borders are individually printable/not-printable
				layerManager.getLayerAttributes(0).setEditability(
					MiLayerAttributes.Mi_LAYER_NEVER_EDITABLE);
				layerManager.appendNewLayer();
				editor.setCurrentLayer(1);
				}
			}
		else
			{
			layerManager = null;
			editor.setHasLayers(false);
			}
		hasLayers = flag;
		}
	public		boolean		getHasLayers()
		{
		return(hasLayers);
		}
	public		MiLayerManager	getLayerManager()
		{
		return(layerManager);
		}
	public		MiPart		getBackgroundLayer()
		{
		return(backgroundLayer);
		}
	public		void		setDrawingPages(MiDrawingPages drawingPages)
		{
		this.drawingPages = drawingPages;
		updatePageSize();
		}
	public		MiDrawingPages	getDrawingPages()
		{
		return(drawingPages);
		}
	public		void		registerPageBorderGraphicsGenerators(Vector generators)
		{
		registeredPageBorderGraphicsGenerators = generators;
		propertyDescriptions = null;
		}
	public		void		setPageBorderGraphicsGenerator(String generatorName)
		{
//MiDebug.println(editor + "setPageBorderGraphicsGenerator:" + generatorName);
		if (drawingPages != null)
			{
//MiDebug.println(editor + "setPageBorderGraphicsGenerator:" + generatorName);

			for (int i = 0; i < registeredPageBorderGraphicsGenerators.size(); ++i)
				{
				if (generatorName.equals(
					((MiiPageBorderGraphicsGenerator )registeredPageBorderGraphicsGenerators.get(i)).getModel().getName()))
					{
					MiiPageBorderGraphicsGenerator generator 
						= (MiiPageBorderGraphicsGenerator )registeredPageBorderGraphicsGenerators.get(i);
					if (generator != drawingPages.getPageBorderGraphicsGenerator())
						{
						setPageBorderGraphicsGenerator(generator);
						}
					return;
					}
				}
			}
		}
	public		void		setPageBorderGraphicsGenerator(MiiPageBorderGraphicsGenerator generator)
		{
//MiDebug.println(hashCode() + " setPageBorderGraphicsGenerator=" + generator + ", to drawingPages=" + drawingPages);
		if (drawingPages != null)
			{
			drawingPages.setPageBorderGraphicsGenerator(generator);
			propertyDescriptions = null;
			editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
			}
		}
	public		MiiPageBorderGraphicsGenerator	getPageBorderGraphicsGenerator()
		{
		if (drawingPages != null)
			{
//MiDebug.println(hashCode() + " getPageBorderGraphicsGenerator=" + drawingPages.getPageBorderGraphicsGenerator() + ", from drawingPages=" + drawingPages);
			return(drawingPages.getPageBorderGraphicsGenerator());
			}
		return(null);
		}

	public		void		setHasDrawingPages(boolean flag)
		{
		if (flag)
			{
			setHasLayers(true);

			drawingPages = new MiDrawingPages();

			MiBounds universe = editor.getUniverseBounds();
			MiDistance width = drawingPages.getBounds().getWidth();
			MiDistance height = drawingPages.getBounds().getHeight();
			MiBounds prefUniverseSize = drawingPages.getBounds().addMargins(
							Math.min(width, height)/8);

			if (!universe.isReversed())
				{
				if (!universe.isLargerSizeThan(prefUniverseSize))
					{
					prefUniverseSize.setCenter(universe.getCenter());
					}
				}

			editor.setWorldBounds(prefUniverseSize);
			editor.setUniverseBounds(prefUniverseSize);

			backgroundLayer.appendPart(drawingPages);
			updatePageSize();

			if (drawingGrid != null)
				{
				drawingGrid.setPaper(drawingPages);
				backgroundLayer.removePart(drawingGrid);
				drawingPages.setGrid(drawingGrid);
				}
			}
		else if (drawingPages != null)
			{
			if (drawingGrid != null)
				{
				drawingGrid.setPaper(null);
				backgroundLayer.removePart(drawingGrid);
				}
			backgroundLayer.removePart(drawingPages);
			drawingPages = null;
			}
		hasDrawingPages = flag;
		}
	public		boolean		getHasDrawingPages()
		{
		return(hasDrawingPages);
		}
	public		void		setHasDrawingGrid(boolean flag)
		{
		if (flag)
			{
			setHasLayers(true);

			if (drawingPages != null)
				drawingGrid = new MiDrawingGrid(drawingPages);
			else
				drawingGrid = new MiDrawingGrid(editor);

			if (snapManager == null)
				{
				snapManager = new MiSnapManager();
				editor.setSnapManager(snapManager);
				}

			snapManager.setGrid(drawingGrid);

			drawingGrid.setUnits(units);

			if (drawingPages != null)
				{
				drawingGrid.setPaper(drawingPages);
				drawingPages.setGrid(drawingGrid);
				}
			else
				{
				backgroundLayer.appendPart(drawingGrid);
				}
			}
		else if (drawingGrid != null)
			{
			if (drawingPages != null)
				{
				drawingPages.setGrid(null);
				}
			else
				{
				backgroundLayer.removePart(drawingGrid);
				}
			drawingGrid = null;
			}
		hasDrawingGrid = flag;
		}
	public		MiSnapManager	getSnapManager()
		{
		return(editor.getSnapManager());
		}
	public		void		setSnapManager(MiSnapManager snapManager)
		{
		editor.setSnapManager(snapManager);
		if (drawingGrid != null)
			snapManager.setGrid(drawingGrid);
		}
	public		void		setIncludeAccessToDrawingGridProperties(boolean flag)
		{
		includeAccessToDrawingGridProperties = flag;
		}
	public		boolean		getIncludeAccessToDrawingGridProperties()
		{
		return(includeAccessToDrawingGridProperties);
		}

	public		void		setOrientation(int orientation)
		{
		this.orientation = orientation;
		updatePageSize();
		editor.dispatchAction(Mi_PAGE_SIZE_CHANGED_ACTION, this);
		editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
		}
	public		int		getOrientation()
		{
		return(orientation);
		}
	public		void		setPageSize(MiPaperSize pageSize)
		{
		if (pageSizeSpec != pageSize)
			{
			setUnits(pageSize.getUnits());
			setPageSizeInUnits(pageSize.getSize());
			this.pageSizeSpec = pageSize;
			setPropertyValue(Mi_PAGE_SIZE_NAME, pageSize.toString());
			}
		}
					/** 
					* If this is 'customPageSize' then it's size is invalid
					* i.e. getPageSize().getSize() is not useful. Use
					* getPageSizeInUnits() or getPageSizeInWorld() instead.
					**/
	public		MiPaperSize	getPageSize()
		{
		return(pageSizeSpec);
		}
	public		MiSize		getPageSizeInWorld()
		{
		return(pageSize);
		}
	public		void		setUnits(MiDistanceUnits units)
		{
		this.units = units;
		if (drawingGrid != null)
			drawingGrid.setUnits(units);
		editor.dispatchAction(Mi_PAGE_UNITS_CHANGED_ACTION);
		editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
		}
	public		MiDistanceUnits	getUnits()
		{
		return(units);
		}
	public		void		setPageSizeInUnits(MiSize sizeInUnits)
		{

		this.pageSizeSpec = MiiPaperSize.customSize;

		pageSizeInUnits.copy(sizeInUnits);

		MiDistance width = sizeInUnits.width * units.getPixelsPerUnit();
		MiDistance height = sizeInUnits.height * units.getPixelsPerUnit();

		this.pageSize.copy(new MiSize(width, height));
		updatePageSize();
		editor.dispatchAction(Mi_PAGE_SIZE_CHANGED_ACTION, this);
		editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);

//MiDebug.printStackTrace(this + " setPageSizeInUnits: " + sizeInUnits);
//if (drawingGrid != null)
 //MiDebug.println("drawingGrid = " + drawingGrid);

		}
	public		MiSize		getPageSizeInUnits()
		{
		return(pageSizeInUnits);
		}

	public		void		setPageSize(MiSize size)
		{
		this.pageSizeSpec = MiiPaperSize.customSize;
		this.pageSize.copy(size);
		updatePageSize();
		editor.dispatchAction(Mi_PAGE_SIZE_CHANGED_ACTION, this);
		editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
		}
	public		void		setPageAttributes(MiAttributes atts)
		{
		if (drawingPages != null)
			{
			drawingPages.setAttributes(atts);
			}
		else
			{
			editor.setAttributes(atts);
			}
		editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
		}
	public		MiAttributes	getPageAttributes()
		{
		if (drawingPages != null)
			{
			return(drawingPages.getAttributes());
			}
		else
			{
			return(editor.getAttributes());
			}
		}

	public		void		setPagesWide(int numPages)
		{
		if (pagesWide != numPages)
			{
			pagesWide = numPages;
			updatePageSize();
			editor.dispatchAction(Mi_PAGE_SIZE_CHANGED_ACTION, this);
			editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
			}
		}
	public		int		getPagesWide()
		{
		return(pagesWide);
		}
	public		void		setPagesTall(int numPages)
		{
		if (pagesTall != numPages)
			{
			pagesTall = numPages;
			updatePageSize();
			editor.dispatchAction(Mi_PAGE_SIZE_CHANGED_ACTION, this);
			editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
			}
		}
	public		int		getPagesTall()
		{
		return(pagesTall);
		}
	public		void		setPageOriginAtUpperLeft(boolean flag)
		{
		pageOriginAtUpperLeft = flag;
		}
	public		boolean		getPageOriginAtUpperLeft()
		{
		return(pageOriginAtUpperLeft);
		}
	public		MiSimpleTransform getWorldToUnitsTransform()
		{
		MiScale scale = new MiScale();
		
		if (drawingPages != null)
			{
			drawingPages.getBoundsOfContentArea(tmpBounds);
			}
		else
			{
			editor.getUniverseBounds(tmpBounds);
			if (orientation == Mi_VERTICAL)
				{
				tmpBounds.setWidth(tmpBounds.getWidth()/pagesWide);
				tmpBounds.setHeight(tmpBounds.getHeight()/pagesTall);
				}
			else
				{
				tmpBounds.setWidth(tmpBounds.getWidth()/pagesTall);
				tmpBounds.setHeight(tmpBounds.getHeight()/pagesWide);
				}
			}

		MiCoord tx1;
		MiCoord ty1;
		MiCoord tx2;
		MiCoord ty2;
		if (orientation == Mi_VERTICAL)
			{
			scale.x = pageSizeInUnits.width/tmpBounds.getWidth();
			tx1 = -tmpBounds.getXmin();
			tx2 = 0;

			scale.y = pageSizeInUnits.height/tmpBounds.getHeight();
			ty1 = -tmpBounds.getYmin();
			ty2 = 0;

			if (pageOriginAtUpperLeft)
				{
				scale.y = -scale.y;
				ty2 = pageSizeInUnits.height;
				}
			}
		else
			{
			scale.x = pageSizeInUnits.height/tmpBounds.getWidth();
			tx1 = -tmpBounds.getXmin();
			tx2 = 0;

			scale.y = pageSizeInUnits.width/tmpBounds.getHeight();
			ty1 = -tmpBounds.getYmin();
			ty2 = 0;

			if (pageOriginAtUpperLeft)
				{
				scale.y = -scale.y;
				ty2 = pageSizeInUnits.width;
				}
			}
		return(new MiSimpleTransform(scale, tx1, ty1, tx2, ty2));
		}
	public		MiPoint		transformWorldPointToUnitsPoint(MiPoint pt)
		{
		MiSimpleTransform transform = getWorldToUnitsTransform();
		return(transform.transformPoint(pt));
/****
		if (drawingPages != null)
			{
			drawingPages.getBounds(tmpBounds);
			}
		else
			{
			editor.getWorldBounds(tmpBounds);
			}
		if (orientation == Mi_VERTICAL)
			{
			pt.x = (pt.x - tmpBounds.getXmin())
				/tmpBounds.getWidth() * pagesWide * pageSizeInUnits.width;
			pt.y = (pt.y - tmpBounds.getYmin())
				/tmpBounds.getHeight() * pagesTall * pageSizeInUnits.height;

			if (pageOriginAtUpperLeft)
				pt.y = pageSizeInUnits.height - pt.y;
			}
		else
			{
			pt.x = (pt.x - tmpBounds.getXmin())
				/tmpBounds.getWidth() * pagesTall * pageSizeInUnits.height;
			pt.y = (pt.y - tmpBounds.getYmin())
				/tmpBounds.getHeight() * pagesWide * pageSizeInUnits.width;

			if (pageOriginAtUpperLeft)
				pt.y = pageSizeInUnits.width - pt.y;
			}
		return(pt);
****/
		}
	public		MiSimpleTransform getUnitsToWorldTransform()
		{
		MiScale scale = new MiScale();
		
		if (drawingPages != null)
			{
			drawingPages.getBoundsOfContentArea(tmpBounds);
			}
		else
			{
			editor.getWorldBounds(tmpBounds);
			if (orientation == Mi_VERTICAL)
				{
				tmpBounds.setWidth(tmpBounds.getWidth()/pagesWide);
				tmpBounds.setHeight(tmpBounds.getHeight()/pagesTall);
				}
			else
				{
				tmpBounds.setWidth(tmpBounds.getWidth()/pagesTall);
				tmpBounds.setHeight(tmpBounds.getHeight()/pagesWide);
				}
			}

		MiCoord tx1;
		MiCoord ty1;
		MiCoord tx2;
		MiCoord ty2;
		if (orientation == Mi_VERTICAL)
			{
			scale.x = tmpBounds.getWidth() / pageSizeInUnits.width;
			tx1 = tmpBounds.getXmin();
			tx2 = 0;

			scale.y = tmpBounds.getHeight() / pageSizeInUnits.height;
			ty1 = tmpBounds.getYmin();
			ty2 = 0;

			if (pageOriginAtUpperLeft)
				{
				scale.y = -scale.y;
				ty2 = pageSizeInUnits.height;
				}
			}
		else
			{
			scale.x = tmpBounds.getWidth() / pageSizeInUnits.height;
			tx1 = tmpBounds.getXmin();
			tx2 = 0;

			scale.y = tmpBounds.getHeight() / pageSizeInUnits.width;
			ty1 = tmpBounds.getYmin();
			ty2 = 0;

			if (pageOriginAtUpperLeft)
				{
				scale.y = -scale.y;
				ty2 = pageSizeInUnits.width;
				}
			}


//MiDebug.println("Get Umits to World xform: " + new MiSimpleTransform(scale, tx1, ty1, tx2, ty2));
		return(new MiSimpleTransform(scale, tx1, ty1, tx2, ty2));
		}
	public		MiPoint		transformUnitsPointToWorldPoint(MiPoint pt)
		{
		MiSimpleTransform transform = getUnitsToWorldTransform();
		return(transform.transformPoint(pt));
/*****
		if (drawingPages != null)
			{
			drawingPages.getBounds(tmpBounds);
			}
		else
			{
			editor.getWorldBounds(tmpBounds);
			}
		if (orientation == Mi_VERTICAL)
			{
			if (pageOriginAtUpperLeft)
				pt.y = pageSizeInUnits.height - pt.y;

			pt.x = tmpBounds.getWidth() * pt.x/(pagesWide * pageSizeInUnits.width) + tmpBounds.getXmin();
			pt.y = tmpBounds.getHeight() * pt.y/(pagesTall * pageSizeInUnits.height) + tmpBounds.getYmin();
			}
		else
			{
			if (pageOriginAtUpperLeft)
				pt.y = pageSizeInUnits.width - pt.y;

			pt.x = tmpBounds.getWidth() * pt.x/(pagesTall * pageSizeInUnits.height) + tmpBounds.getXmin();
			pt.y = tmpBounds.getHeight() * pt.y/(pagesWide * pageSizeInUnits.width) + tmpBounds.getYmin();
			}
		return(pt);
****/
		}
	public		Strings		getPropertyNames()
		{
		Strings names = new Strings();
		names.addElement(Mi_PAGE_SIZE_NAME);
		names.addElement(Mi_PAGE_ORIENTATION_NAME);
		names.addElement(Mi_PAGES_WIDE_NAME);
		names.addElement(Mi_PAGES_TALL_NAME);
		names.addElement(Mi_PAGE_BORDER_NAME);
		names.addElement(Mi_PAGE_BACKGROUND_COLOR_NAME);
		names.addElement(Mi_PAGE_BORDER_COLOR_NAME);
		return(names);
		}

	protected	void		updatePageSize()
		{
		if (drawingPages != null)
			{
			MiPoint refPt = drawingPages.getReferenceGridPoint();
			drawingPages.setCharacteristics(orientation, pageSize, pagesWide, pagesTall);
			MiBounds paperBounds = drawingPages.getBounds(tmpBounds);
//MiDebug.println("paperBounds=" + paperBounds);
			paperBounds.addMargins(Math.min(paperBounds.getWidth(), paperBounds.getHeight())/8);
//MiDebug.println("2 paperBounds=" + paperBounds);
			MiBounds universe = editor.getUniverseBounds();
			MiBounds world = editor.getWorldBounds();
			if (!universe.contains(paperBounds))
				{
				if (drawingPages.Mi_COORDINATES_ORIGIN_CENTER_OF_PAGE.equals(drawingPages.getCoordinateOrigin()))
					{
					paperBounds.setCenter(world.getCenter());
					}
//MiDebug.println("3 paperBounds=" + paperBounds);
//MiDebug.println("3 editor.getUniverseBounds()=" + editor.getUniverseBounds());
				editor.setUniverseBounds(paperBounds);
				editor.getUniverseBounds(universe);
//MiDebug.println("4 editor.getUniverseBounds()=" + editor.getUniverseBounds());
//MiDebug.println("5 editor.getWorldBounds()=" + editor.getWorldBounds());
				}
//MiDebug.println("3 universe=" + universe);
			if (drawingPages.Mi_COORDINATES_ORIGIN_CENTER_OF_PAGE.equals(drawingPages.getCoordinateOrigin()))
				{
				drawingPages.setCenter(universe.getCenter());
				}
//MiDebug.println("4 drawingPages.getBounds()=" + drawingPages.getBounds());

			world.setCenter(universe.getCenter());
			editor.setWorldBounds(world);
			MiPoint newRefPt = drawingPages.getReferenceGridPoint();
			if (!refPt.equals(newRefPt))
				{
				MiSize gridSize = drawingPages.getGridSize();
//MiDebug.println("refPt = " + refPt);
//MiDebug.println("newRefPt = " + newRefPt);
//MiDebug.println("gridSize = " + gridSize);

				MiDistance changeInRefPtX = newRefPt.x - refPt.x;
//MiDebug.println("changeInRefPtX = " + changeInRefPtX);
				changeInRefPtX = changeInRefPtX >= 0 
					? changeInRefPtX + gridSize.getWidth()/2 : changeInRefPtX - gridSize.getWidth()/2;
//MiDebug.println("changeInRefPtX = " + changeInRefPtX);
				MiDistance tx = gridSize.getWidth() * ((int )(changeInRefPtX/gridSize.getWidth()));
//MiDebug.println("tx = " + tx);

				MiDistance changeInRefPtY = newRefPt.y - refPt.y;
				changeInRefPtY = changeInRefPtY >= 0 
					? changeInRefPtY + gridSize.getHeight()/2 : changeInRefPtY - gridSize.getHeight()/2;
				MiDistance ty = gridSize.getHeight() * ((int )(changeInRefPtY/gridSize.getHeight()));

				MiVector gridRefPtChange = new MiVector(refPt.x + tx - newRefPt.x, refPt.y + ty - newRefPt.y);

				editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_REFERENCE_GRID_POINT_ACTION, gridRefPtChange);
				}
			}
		else
			{
			MiDistance width = pageSize.width * pagesWide;
			MiDistance height = pageSize.height * pagesTall;
			MiBounds world = editor.getWorldBounds();
			if (orientation == Mi_VERTICAL)
				{
				world.setWidth(width);
				world.setHeight(height);
				}
			else
				{
				world.setWidth(height);
				world.setHeight(width);
				}
			editor.setWorldBounds(world);
			}
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
//MiDebug.println(editor + " setPropertyValue: " + name + "=" + value);
		name = Utility.replaceAll(name, " ", "");

		if (name.equalsIgnoreCase(Mi_VISIBLE_NAME))
			{
			if (drawingPages != null)
				drawingPages.setVisible(Utility.toBoolean(value));
			}
		else if (name.equalsIgnoreCase(Mi_PAGES_WIDE_NAME_NSP))
			setPagesWide(Utility.toInteger(value));
		else if (name.equalsIgnoreCase(Mi_PAGES_TALL_NAME_NSP))
			setPagesTall(Utility.toInteger(value));
		else if (name.equalsIgnoreCase(Mi_PAGE_BORDER_NAME_NSP))
			setPageBorderGraphicsGenerator(value);
		else if (name.equalsIgnoreCase(Mi_PAGE_SIZE_NAME_NSP))
			{
			MiPaperSize size = MiPaperSize.getPageSize(value);
			if (size == null)
				{
				throw new IllegalArgumentException(this 
					+ ": Property: " + name + " assigned invalid value: \"" + value + "\"");
				}
			setPageSize(size);
			value = size.toString();
			}
		else if (name.equalsIgnoreCase(Mi_PAGE_ORIENTATION_NAME))
			{
			if (value.equalsIgnoreCase(Mi_LANDSCAPE_NAME))
				setOrientation(Mi_HORIZONTAL);
			else 
				setOrientation(Mi_VERTICAL);
			}
		else if (name.equalsIgnoreCase(Mi_PAGE_BACKGROUND_COLOR_NAME_NSP))
			{
			setPageAttributes(getPageAttributes().setBackgroundColor(value));
			}
		else if (name.equalsIgnoreCase(Mi_SNAP_TO_GRID_ENABLED_NAME))
			{
			if (editor.getSnapManager() != null)
				editor.getSnapManager().setEnabled(Utility.toBoolean(value));
			}
		else if (name.equalsIgnoreCase(Utility.replaceAll(Mi_HAS_SHADOW_ATT_NAME, " ", "")))
			{
			if (drawingPages != null)
				drawingPages.setHasShadow(Utility.toBoolean(value));
			}
		else if ((getPageBorderGraphicsGenerator() != null) 
			&& (getPageBorderGraphicsGenerator().getModel().getPropertyDescriptions() != null)
			&& (getPageBorderGraphicsGenerator().getModel().getPropertyDescriptions().contains(name)))
			{
			MiiPageBorderGraphicsGenerator borderGraphicsGenerator = getPageBorderGraphicsGenerator();
			MiiModelEntity entity = borderGraphicsGenerator.getModel();
			if (!Utility.equals(entity.getPropertyValue(name), value))
				{
				entity.setPropertyValue(name, value);
				updatePageSize();
				editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
				}
			}
		else if (name.equalsIgnoreCase(Mi_PAGE_BORDER_COLOR_NAME_NSP))
			{
			setPageAttributes(getPageAttributes().setColor(value));
			}
		else if (includeAccessToDrawingGridProperties)
			{
			if ((drawingGrid != null) && (drawingGrid.getPropertyDescriptions().contains(name)))
				{
				if (!Utility.equals(drawingGrid.getPropertyValue(name), value))
					{
					drawingGrid.setPropertyValue(name, value);
					editor.dispatchAction(Mi_PAGE_MANAGER_CHANGED_ACTION, this);
					}
				}
			}
		else
			{
			throw new IllegalArgumentException(this 
				+ ": Attempt to set value of unknown property: " + name);
			}	
		// Allow property change events to be generated....
		updatingSuperProperties = true;
		super.setPropertyValue(name, value);
		updatingSuperProperties = false;
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

		if (updatingSuperProperties)
			return(super.getPropertyValue(name));

		if (name.equalsIgnoreCase(Mi_VISIBLE_NAME))
			return(drawingPages == null ? "" : "" + drawingPages.isVisible());
		else if (name.equalsIgnoreCase(Mi_PAGES_WIDE_NAME_NSP))
			return("" + pagesWide);
		else if (name.equalsIgnoreCase(Mi_PAGES_TALL_NAME_NSP))
			return("" + pagesTall);
		else if (name.equalsIgnoreCase(Mi_PAGE_BORDER_NAME_NSP))
			return((getPageBorderGraphicsGenerator() != null) ? getPageBorderGraphicsGenerator().getModel().getName() : "");
		else if (name.equalsIgnoreCase(Mi_PAGE_SIZE_NAME_NSP))
			return(pageSizeSpec == null ? "?" : pageSizeSpec.toString());
		else if (name.equalsIgnoreCase(Mi_PAGE_ORIENTATION_NAME))
			return(orientation == Mi_HORIZONTAL ? Mi_LANDSCAPE_NAME : Mi_PORTRAIT_NAME);
		else if (name.equalsIgnoreCase(Mi_PAGE_BACKGROUND_COLOR_NAME_NSP))
			return(MiColorManager.getColorName(getPageAttributes().getBackgroundColor()));
		else if (name.equalsIgnoreCase(Mi_SNAP_TO_GRID_ENABLED_NAME))
			{
			if (editor.getSnapManager() != null)
				return("" + editor.getSnapManager().isEnabled());
			return("false");
			}
		else if (name.equalsIgnoreCase(Utility.replaceAll(Mi_HAS_SHADOW_ATT_NAME, " ", "")))
			return(drawingPages == null ? "" : "" + drawingPages.getHasShadow());
		else if ((getPageBorderGraphicsGenerator() != null) 
			&& (getPageBorderGraphicsGenerator().getModel().getPropertyDescriptions() != null)
			&& (getPageBorderGraphicsGenerator().getModel().getPropertyDescriptions().contains(name)))
			{
			MiiPageBorderGraphicsGenerator borderGraphicsGenerator = getPageBorderGraphicsGenerator();
			MiiModelEntity entity = borderGraphicsGenerator.getModel();
			String value = entity.getPropertyValue(name);
			if (value == null)
				{
				return(Mi_NULL_VALUE_NAME);
				}
			return(value);
			}
		else if (name.equalsIgnoreCase(Mi_PAGE_BORDER_COLOR_NAME_NSP))
			{
			return(MiColorManager.getColorName(getPageAttributes().getColor()));
			}
		if (includeAccessToDrawingGridProperties) 
			{
			if ((drawingGrid != null) && (drawingGrid.getPropertyDescriptions().contains(name)))
				{
				return(drawingGrid.getPropertyValue(name));
				}
			return(Mi_NULL_VALUE_NAME);
			}
		else
			{
			throw new IllegalArgumentException(this 
				+ ": Attempt to get value of unknown property: " + name);
			}	
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
			{
			return(propertyDescriptions);
			}

//MiDebug.println(this + "rebuild MiPropertyDescriptions");
		propertyDescriptions = new MiPropertyDescriptions(getClass().getName());

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_VISIBLE_NAME, Mi_BOOLEAN_TYPE, "true"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PAGE_SIZE_NAME, 
				MiPaperSize.getPageSizeNamesWithSizes(),
				MiiPaperSize.standardLetter.toString()));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PAGE_ORIENTATION_NAME, 
			new Strings(Mi_LANDSCAPE_NAME, Mi_PORTRAIT_NAME),
			Mi_LANDSCAPE_NAME));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PAGES_WIDE_NAME, Mi_POSITIVE_INTEGER_TYPE, "1")
			.setMinimumValue(1));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PAGES_TALL_NAME, Mi_POSITIVE_INTEGER_TYPE, "1")
			.setMinimumValue(1));

		Strings paperLookGenNames = new Strings();
		for (int i = 0; i < registeredPageBorderGraphicsGenerators.size(); ++i)
			{
			paperLookGenNames.add(((MiiPageBorderGraphicsGenerator )registeredPageBorderGraphicsGenerators.get(i)).getModel().getName());
			}
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PAGE_BORDER_NAME, paperLookGenNames, paperLookGenNames.get(0)));

		MiiPageBorderGraphicsGenerator borderGraphicsGenerator = getPageBorderGraphicsGenerator();
		if ((borderGraphicsGenerator != null) && (borderGraphicsGenerator.getModel().getPropertyDescriptions() != null))
			{
			propertyDescriptions.append(borderGraphicsGenerator.getModel().getPropertyDescriptions());
			}

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PAGE_BACKGROUND_COLOR_NAME, Mi_COLOR_TYPE, "white"));


// Added 4-11-2003
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_PAGE_BORDER_COLOR_NAME, Mi_COLOR_TYPE, "white"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_SNAP_TO_GRID_ENABLED_NAME, Mi_BOOLEAN_TYPE, "true"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_HAS_SHADOW_ATT_NAME, Mi_BOOLEAN_TYPE, "true"));

		if (includeAccessToDrawingGridProperties)
			{
			MiDrawingGrid g = drawingGrid;
			if (g == null)
				g = new MiDrawingGrid();
			propertyDescriptions.append(g.getPropertyDescriptions());
			}

		// Remove all properties no longer supported by border renderer...
		Strings propertyNames = getPropertyNames();
		for (int i = 0; i < propertyNames.size(); ++i)
			{
			if (propertyDescriptions.elementAt(propertyNames.get(i)) == null)
				{
				removeProperty(propertyNames.get(i));
				}
			}
		return(propertyDescriptions);
		}
	}

