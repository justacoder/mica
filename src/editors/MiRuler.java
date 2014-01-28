
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
import com.swfm.mica.util.DoubleVector; 
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Strings; 
import java.awt.Color;

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiRuler extends MiWidget implements MiiActionHandler
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;

	public static final String 	Mi_TICK_RESOLUTION_NAME		= "tick resolution";
	public static final String 	Mi_TICK_COLOR_NAME		= "tick color";
	public static final String 	Mi_LABEL_JUSTIFICATION_NAME	= "label justification";
	public static final String 	Mi_TICK_JUSTIFICATION_NAME	= "tick justification";
	public static final String	Mi_LABEL_COLOR_NAME		= "number color";
	public static final String	Mi_LABEL_FONT_NAME		= "number font";
	public static final String	Mi_LABEL_SIZE_NAME		= "number size";
	public static final String	Mi_LOCATION_TICK_COLOR_NAME	= "locator color";
	public static final String	Mi_LOCATION_TICK_THICKNESS_NAME	= "locator thickness";

	private 	boolean		initialized;
	private 	int		orientation;
	private 	MiPart		targetPage;
	private 	double		pageWidthInUnits;
	private 	boolean		tickLabelsSupplementTicks	= true;
	private		int		labelJustification;
	private 	boolean		integerTickLabelsEqualTickValue	= true;
	private 	MiDistance	minTickSpacing			= 3;
	private 	MiDistance	minLabelSpacing			= 5;
	private 	MiDistance	distanceBetweenTicks;
	private 	double		drawnTickResolution;
	private 	int		tickLabelResolution;
	// As the area of the page increases (zoom out) this tells what to labels to display.
	// Default is every inch, then when those labels are too close together and overlap, 
	// labels are drawn every 3 inches, ...6, ... 12...
	private 	int[]		tickLabelResolutions		= {1, 3, 6, 12};
	private 	double		startTickValue;
	private 	double		endTickValue;
	private 	MiCoord		startTickX;
	private 	MiCoord		startTickY;
	private		MiPart		mouseLocationTick;
	private		int		mouseLocationTickJustification;
	private 	double[]	tickResolutions			= {0.0625, 0.125, 0.25, 0.5, 1.0};
	private 	MiPart[]	tickLabels;
	private 	MiPart[]	ticks;
	private 	int		tickJustification;
	private 	MiParts		annotations			= new MiParts();
	private 	DoubleVector	annotationLocations		= new DoubleVector();
	private 	IntVector	annotationJustifications	= new IntVector();
	private 	MiBounds	lastPageBounds			= new MiBounds();
	private 	MiSize		maxLabelSize			= new MiSize();
	private 	boolean		assignedHandlersToEditorContainingPage;
	private 	boolean		pageOriginAtUpperLeft;

	private 	MiBounds	tmpClipBounds			= new MiBounds();
	private 	MiBounds	tmpBounds			= new MiBounds();
	private 	MiBounds	tmpBounds2			= new MiBounds();
	private 	MiMargins	tmpMargins			= new MiMargins();
	private 	MiSize		tmpSize				= new MiSize();
	

					/**------------------------------------------------------
	 				 * Constructs a new MiRuler with a horizontal orientation. 
					 *------------------------------------------------------*/
	public				MiRuler()
		{
		this(Mi_HORIZONTAL);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiRuler with the given orientation. 
	 				 * @param orientation 	either Mi_VERTICAL or Mi_HORIZONTAL
					 *------------------------------------------------------*/
	public				MiRuler(int orientation)
		{
		setOrientation(orientation);
		setDefaultLook();
		setPropertyValues(MiSystem.getPropertiesForClass("MiRuler"));
		applyCustomLookAndFeel("MiRuler");
		}

	protected	void		setDefaultLook()
		{
		MiParts labels = new MiParts();
		labels.addElement(null);
		labels.addElement(null);
		labels.addElement(null);
		labels.addElement(null);
		labels.addElement(new MiText("55"));
		MiParts ticks = new MiParts();
		if (orientation == Mi_HORIZONTAL)
			{
			ticks.addElement(new MiLine(0, 0, 0, 4));
			ticks.addElement(new MiLine(0, 0, 0, 4));
			ticks.addElement(new MiLine(0, 0, 0, 6));
			ticks.addElement(new MiLine(0, 0, 0, 10));
			ticks.addElement(new MiLine(0, 0, 0, 14));
			for (int i = 0; i < ticks.size(); ++i)
				ticks.elementAt(i).setBorderLook(Mi_RIDGE_BORDER_LOOK);

			setConfiguration(tickResolutions, labels, ticks, Mi_TOP_JUSTIFIED);
			MiPart locationTick = new MiLine(0, 0, 0, 16);
			locationTick.setColor("blue");
			locationTick.setLineWidth(2);
			setMouseLocationTick(locationTick);
			setMouseLocationTickJustification(Mi_TOP_JUSTIFIED);
			labelJustification = Mi_BOTTOM_JUSTIFIED;
			}
		else
			{
			ticks.addElement(new MiLine(0, 0, 4, 0));
			ticks.addElement(new MiLine(0, 0, 4, 0));
			ticks.addElement(new MiLine(0, 0, 6, 0));
			ticks.addElement(new MiLine(0, 0, 10, 0));
			ticks.addElement(new MiLine(0, 0, 14, 0));
			for (int i = 0; i < ticks.size(); ++i)
				ticks.elementAt(i).setBorderLook(Mi_RIDGE_BORDER_LOOK);

			setConfiguration(tickResolutions, labels, ticks, Mi_LEFT_JUSTIFIED);
			MiPart locationTick = new MiLine(0, 0, 16, 0);
			locationTick.setColor("blue");
			locationTick.setLineWidth(2);
			setMouseLocationTick(locationTick);
			setMouseLocationTickJustification(Mi_LEFT_JUSTIFIED);
			labelJustification = Mi_RIGHT_JUSTIFIED;
			}
		setBackgroundColor("white");
		setBorderLook(Mi_INDENTED_BORDER_LOOK);
		//setDoubleBuffered(true);
		//setIsOpaqueRectangle(true);
		}
	public		void		setTickLabelResolutions(int[] labelResolutions)
		{
		tickLabelResolutions = new int[labelResolutions.length];
		System.arraycopy(labelResolutions, 0, this.tickLabelResolutions, 0, labelResolutions.length);
		}
	public		void		setConfiguration(
						// smallest to largest
						double[] tickResolutions, 
						MiParts tickLabels, 
						MiParts ticks, 
						int tickJustification)
		{
		if ((tickResolutions.length != tickLabels.size()) || (tickLabels.size() != ticks.size()))
			{
			throw new IllegalArgumentException(MiDebug.getMicaClassName(this)
			+ ": there must be a (possibly null) tick and tickLabel for every"
			+ " tick resolution supported.");
			}

		this.tickResolutions = new double[tickResolutions.length];
		System.arraycopy(tickResolutions, 0, this.tickResolutions, 0, tickResolutions.length);

		this.tickLabels = tickLabels.toArray();
		this.ticks = ticks.toArray();

		this.tickJustification = tickJustification;

		updateRuler();
		}
	public		void		setTickResolution(double max)
		{
		tickResolutions = new double[tickLabels.length];
		for (int i = 0; i < tickLabels.length; ++i)
			{
			tickResolutions[i] = max;
			max += max;
			}
		lastPageBounds.reverse();
		updateRuler();
		}
	public		double		getTickResolution()
		{
		return(tickResolutions[0]);
		}
	public		void		setTickJustification(int justification)
		{
		tickJustification = justification;
		invalidateArea();
		}
	public		int		getTickJustification()
		{
		return(tickJustification);
		}
	public		void		setTickColor(Color color)
		{
		for (int i = 0; i < ticks.length; ++i)
			{
			ticks[i].setColor(color);
			ticks[i].setBackgroundColor(color);
			ticks[i].setBorderLook(Mi_NONE);
			}
		invalidateArea();
		}
	public		Color		getTickColor()
		{
		return(ticks[0].getColor());
		}
	public		void		setMouseLocationTick(MiPart tick)
		{
		mouseLocationTick = tick;
		appendAttachment(mouseLocationTick);
		}
	public		MiPart		getMouseLocationTick()
		{
		return(mouseLocationTick);
		}
	public		void		setMouseLocationTickJustification(int justification)
		{
		mouseLocationTickJustification = justification;
		}
	public		int		getMouseLocationTickJustification()
		{
		return(mouseLocationTickJustification);
		}
	public		void		setMouseLocationTickColor(Color color)
		{
		mouseLocationTick.setColor(color);
		mouseLocationTick.setBackgroundColor(color);
		}
	public		Color		getMouseLocationTickColor()
		{
		return(mouseLocationTick.getColor());
		}
	public		void		setMouseLocationTickThickness(MiDistance width)
		{
		mouseLocationTick.setLineWidth(width);
		if (orientation == Mi_HORIZONTAL)
			{
			mouseLocationTick.setWidth(width);
			}
		else 
			{
			mouseLocationTick.setHeight(width);
			}
		}
	public		MiDistance	getMouseLocationTickThickness()
		{
		return(mouseLocationTick.getLineWidth());
		}
	public		void		setLabelFont(MiFont font)
		{
		for (int i = 0; i < tickLabels.length; ++i)
			{
			if (tickLabels[i] != null)
				tickLabels[i].setFont(font);
			}
		lastPageBounds.reverse();
		updateRuler();
		}
	public		MiFont		getLabelFont()
		{
		for (int i = 0; i < tickLabels.length; ++i)
			{
			if (tickLabels[i] != null)
				return(tickLabels[i].getFont());
			}
		return(getFont());
		}
	public		void		setLabelPointSize(int size)
		{
		for (int i = 0; i < tickLabels.length; ++i)
			{
			if (tickLabels[i] != null)
				tickLabels[i].setFontPointSize(size);
			}
		lastPageBounds.reverse();
		updateRuler();
		}
	public		int		getLabelPointSize()
		{
		for (int i = 0; i < tickLabels.length; ++i)
			{
			if (tickLabels[i] != null)
				return(tickLabels[i].getFontPointSize());
			}
		return(getFontPointSize());
		}
	public		void		setLabelColor(Color color)
		{
		for (int i = 0; i < tickLabels.length; ++i)
			{
			if (tickLabels[i] != null)
				tickLabels[i].setColor(color);
			}
		invalidateArea();
		}
	public		Color		getLabelColor()
		{
		for (int i = 0; i < tickLabels.length; ++i)
			{
			if (tickLabels[i] != null)
				return(tickLabels[i].getColor());
			}
		return(getColor());
		}
	public		void		setLabelJustification(int justification)
		{
		labelJustification = justification;
		invalidateArea();
		}
	public		int		getLabelJustification()
		{
		return(labelJustification);
		}

	public		void		setPageSizeInUnits(double pageWidthInUnits)
		{
		this.pageWidthInUnits = pageWidthInUnits;
		lastPageBounds.reverse();
		updateRuler();
		}
	public		double		getPageSizeInUnits()
		{
		return(pageWidthInUnits);
		}
	public		void		setPage(MiPart page)
		{
		targetPage = page;
		MiEditor pageEditor = targetPage.getContainingEditor();
		if (pageEditor != null)
			assignHandlersToEditorContainingPage(pageEditor);
		updateRuler();
		}
	public		MiPart		getPage()
		{
		return(targetPage);
		}

	public		void		setOrientation(int orientation)
		{
		this.orientation = orientation;
		lastPageBounds.reverse();
		updateRuler();
		}
	public		int		getOrientation()
		{
		return(orientation);
		}
	public		void		setPageOriginAtUpperLeft(boolean flag)
		{
		pageOriginAtUpperLeft = flag;
		}
	public		boolean		getPageOriginAtUpperLeft()
		{
		return(pageOriginAtUpperLeft);
		}
	public		void		appendAnnotation(MiPart part, double locationValue, int justification)
		{
		annotations.addElement(part);
		annotationLocations.addElement(locationValue);
		annotationJustifications.addElement(justification);
		}
	public		void		removeAnnotation(MiPart part)
		{
		int index = annotations.indexOf(part);
		annotations.removeElementAt(index);
		annotationLocations.removeElementAt(index);
		}
	public		void		getAnnotations(
						MiParts parts, 
						DoubleVector locations, 
						IntVector justifications)
		{
		parts.append(annotations);
		locations.append(annotationLocations);
		justifications.append(annotationJustifications);
		}

	public		MiCoord		getReferenceTickLocationOnTargetPage()
		{
		if (targetPage instanceof MiEditor)
			((MiEditor )targetPage).getUniverseBounds(tmpBounds);
		else
			targetPage.getBounds(tmpBounds);

		if (orientation == Mi_HORIZONTAL)
			return(tmpBounds.xmin);
		else
			return(tmpBounds.ymin);
		}
	public		MiDistance	getTickSpacingOnTargetPage(double tickResolution)
		{
		if (targetPage instanceof MiEditor)
			((MiEditor )targetPage).getUniverseBounds(tmpBounds);
		else
			targetPage.getBounds(tmpBounds);

		if (orientation == Mi_HORIZONTAL)
			return(tmpBounds.getWidth() * tickResolution/(endTickValue - startTickValue));
		else
			return(tmpBounds.getHeight() * tickResolution/(endTickValue - startTickValue));
		}

	protected 	void		doLayout()
		{
		super.doLayout();
		updateRuler();
		}
	protected	void		updateRuler()
		{
		if (targetPage == null)
			return;
		
		MiEditor pageEditor = targetPage.getContainingEditor();
		if (pageEditor == null)
			return;

		MiEditor rulerEditor = getContainingEditor();
		if (rulerEditor == null)
			return;

		if (!initialized)
			{
			initialized = true;
			updateMouseLocationTick();

			if (!assignedHandlersToEditorContainingPage)
				assignHandlersToEditorContainingPage(pageEditor);
			}
		MiBounds pageBounds = tmpBounds;
		if (targetPage instanceof MiEditor)
			pageEditor.getUniverseBounds(pageBounds);
		else
			targetPage.getBounds(pageBounds);

		pageEditor.transformToOtherEditorSpace(rulerEditor, pageBounds, pageBounds);
		
		if (orientation == Mi_HORIZONTAL)
			{
			if ((Utility.areEqual(pageBounds.getXmin(), lastPageBounds.getXmin()))
				&& (Utility.areEqual(pageBounds.getWidth(), lastPageBounds.getWidth())))
				{
				return;
				}
			lastPageBounds.copy(pageBounds);

			MiCoord xmin = getXmin();
			double start = (xmin - pageBounds.getXmin())/pageBounds.getWidth()
						 * pageWidthInUnits;
			double end = start + getWidth()/pageBounds.getWidth() * pageWidthInUnits;

			// ---------------------------------------------------------------
			// Find optimal tick resolution to use for the ticks (i.e.
			// such that distanceBetweenTicks > minTickSpacing. Also
			// get startTickValue and endTickValue.
			// ---------------------------------------------------------------
			for (int i = 0; i < tickResolutions.length; ++i)
				{
				double test = Math.floor(start);
				drawnTickResolution = tickResolutions[i];
				while (test < start)
					{
					test += drawnTickResolution;
					}
				startTickValue = test;
				startTickX = xmin 
					+ (startTickValue - start)/pageWidthInUnits * pageBounds.getWidth();

				test = Math.ceil(end);
				while (test > end)
					{
					test -= drawnTickResolution;
					}
				endTickValue = test;
				MiCoord endTickX = xmin 
					+ (endTickValue - start)/pageWidthInUnits * pageBounds.getWidth();

				distanceBetweenTicks = (endTickX - startTickX) 
					* drawnTickResolution/(endTickValue - startTickValue);
				if (distanceBetweenTicks > minTickSpacing)
					break;
				}
			tickLabelResolution = 1;
			if (integerTickLabelsEqualTickValue) 
				{
				MiPart tickLabel = tickLabels[getTickResolutionIndex(1)];
				if (tickLabel instanceof MiText)
					((MiText )tickLabel).setText("-88");
				else if (tickLabel instanceof MiWidget)
					((MiWidget )tickLabel).setValue("-88");

				int i = 0;
				while (tickLabelResolution * distanceBetweenTicks/drawnTickResolution
					-tickLabel.getWidth()
					< minLabelSpacing)
					{
					if (i + 1 >= tickLabelResolutions.length)
						break;
						
					tickLabelResolution = tickLabelResolutions[++i];
					}
				}
			}
		else
			{
			if ((Utility.areEqual(pageBounds.getYmin(), lastPageBounds.getYmin()))
				&& (Utility.areEqual(pageBounds.getHeight(), lastPageBounds.getHeight())))
				{
				return;
				}
			lastPageBounds.copy(pageBounds);

				MiCoord ymin = getYmin();
				double start = (ymin - pageBounds.getYmin())/pageBounds.getHeight()
						 * pageWidthInUnits;
				double end = start + getHeight()/pageBounds.getHeight() * pageWidthInUnits;

				for (int i = 0; i < tickResolutions.length; ++i)
					{
					double test = Math.floor(start);
					drawnTickResolution = tickResolutions[i];
					while (test < start)
						{
						test += drawnTickResolution;
						}
					startTickValue = test;
					startTickY = ymin 
						+ (startTickValue - start)/pageWidthInUnits * pageBounds.getHeight();
	
					test = Math.ceil(end);
					while (test > end)
						{
						test -= drawnTickResolution;
						}
					endTickValue = test;
					MiCoord endTickY = ymin 
						+ (endTickValue - start)/pageWidthInUnits * pageBounds.getHeight();
	
					distanceBetweenTicks = (endTickY - startTickY)
						* drawnTickResolution/(endTickValue - startTickValue);
					if (distanceBetweenTicks > minTickSpacing)
						break;
					}
			if (pageOriginAtUpperLeft)
				{
				double tickTotal = ((int )((endTickValue - startTickValue)/drawnTickResolution))
							 * drawnTickResolution;
				startTickValue = -((int )((getYmax() - pageBounds.getYmax())/distanceBetweenTicks))
							 * drawnTickResolution;
				endTickValue = startTickValue + tickTotal;
				distanceBetweenTicks = -distanceBetweenTicks;
				end = -(getYmax() - pageBounds.getYmax())/pageBounds.getHeight()
						 * pageWidthInUnits;
				startTickY = getYmax() 
					- (startTickValue - end)/pageWidthInUnits * pageBounds.getHeight();
				}
			tickLabelResolution = 1;
			if (integerTickLabelsEqualTickValue) 
				{
				MiPart tickLabel = tickLabels[getTickResolutionIndex(1)];
				if (tickLabel instanceof MiText)
					((MiText )tickLabel).setText("-88");
				else if (tickLabel instanceof MiWidget)
					((MiWidget )tickLabel).setValue("-88");

				int i = 0;
				while (tickLabelResolution * Math.abs(distanceBetweenTicks/drawnTickResolution)
					-tickLabel.getHeight() 
					< minLabelSpacing)
					{
					if (i + 1 >= tickLabelResolutions.length)
						break;
						
					tickLabelResolution = tickLabelResolutions[++i];
					}
				}
			}
		for (int i = 0; i < tickLabels.length; ++i)
			{
			if (tickLabels[i] != null)
				maxLabelSize.accumulateMaxWidthAndHeight(tickLabels[i].getBounds(tmpBounds));
			}
		invalidateArea();
		}
	public		void		updateMouseLocationTick()
		{
		if (mouseLocationTick == null)
			{
			return;
			}

		MiEditor containingEditor = getContainingEditor();
		if (containingEditor == null)
			{
			return;
			}
/* Expensive ...
		if (!containingEditor.isShowing(null))
			{
			return;
			}
*/

		MiBounds mousePosition = containingEditor.getMousePosition(tmpBounds2);
		if (mousePosition.isReversed())
			{
			return;
			}
		MiBounds innerBounds = getInnerBounds(tmpBounds);
		if (orientation == Mi_HORIZONTAL)
			{
			mouseLocationTick.setCenterX(mousePosition.getCenterX());
			switch (mouseLocationTickJustification)
				{
				case Mi_TOP_JUSTIFIED:
					mouseLocationTick.setYmax(innerBounds.ymax);
					break;
				case Mi_CENTER_JUSTIFIED:
					mouseLocationTick.setCenterY(innerBounds.getCenterY());
					break;
				case Mi_BOTTOM_JUSTIFIED:
					mouseLocationTick.setYmin(innerBounds.ymin);
					break;
				case Mi_JUSTIFIED:
					mouseLocationTick.setCenterY(innerBounds.getCenterY());
					//Makes the ticks permanently this size: mouseLocationTick.setHeight(innerBounds.getHeight());
					//mouseLocationTick.setYmin(innerBounds.ymin);
					break;
				}
			}
		else
			{
			mouseLocationTick.setCenterY(mousePosition.getCenterY());
			switch (mouseLocationTickJustification)
				{
				case Mi_LEFT_JUSTIFIED:
					mouseLocationTick.setXmin(innerBounds.xmin);
					break;
				case Mi_CENTER_JUSTIFIED:
					mouseLocationTick.setCenterX(innerBounds.getCenterX());
					break;
				case Mi_RIGHT_JUSTIFIED:
					mouseLocationTick.setXmax(innerBounds.xmax);
					break;
				case Mi_JUSTIFIED:
					mouseLocationTick.setCenterX(innerBounds.getCenterX());
					// mouseLocationTick.setWidth(innerBounds.getWidth());
					// mouseLocationTick.setXmin(innerBounds.xmin);
					break;
				}
			}
		}
					/**------------------------------------------------------
		 			 * Processes the given action. The actions supported are:
					 * <pre>
					 *    Mi_EDITOR_VIEWPORT_CHANGED_ACTION
					 *    Mi_MOUSE_EXIT_ACTION
					 *    Mi_MOUSE_ENTER_ACTION
					 * </pre>
					 * @param action	the action to process
					 * @return		false if consumes the action
					 * @implements		MiiActionHandler#processAction
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_EDITOR_VIEWPORT_CHANGED_ACTION))
			{
			updateRuler();
			}
		else if (action.hasActionType(Mi_MOUSE_EXIT_ACTION))
			{
			if (mouseLocationTick != null)
				mouseLocationTick.setVisible(false);
			}
		else if (action.hasActionType(Mi_MOUSE_ENTER_ACTION))
			{
			if (mouseLocationTick != null)
				mouseLocationTick.setVisible(true);
			updateMouseLocationTick();
			}
		else if (action.hasActionType(MiPageManager.Mi_PAGE_SIZE_CHANGED_ACTION))
			{
			MiEditor editor = targetPage.getContainingEditor();
			MiSize pageSizeInUnits = editor.getPageManager().getPageSizeInUnits();
			if (orientation == Mi_HORIZONTAL)
				{
				if (editor.getPageManager().getOrientation() == Mi_VERTICAL)
					setPageSizeInUnits(pageSizeInUnits.getWidth());
				else
					setPageSizeInUnits(pageSizeInUnits.getHeight());
				}
			else
				{
				if (editor.getPageManager().getOrientation() == Mi_VERTICAL)
					setPageSizeInUnits(pageSizeInUnits.getHeight());
				else
					setPageSizeInUnits(pageSizeInUnits.getWidth());
				}
			}
		return(true);
		}
	protected	void		assignHandlersToEditorContainingPage(MiEditor editor)
		{
		editor.appendEventHandler(new MiMouseEnterExitActionDispatcher());
		editor.appendActionHandler(this, Mi_MOUSE_ENTER_ACTION);
		editor.appendActionHandler(this, Mi_MOUSE_EXIT_ACTION);
		editor.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		editor.appendActionHandler(this, MiPageManager.Mi_PAGE_SIZE_CHANGED_ACTION);
		editor.appendEventHandler(new MiMouseLocationDisplayMonitor2(this));
		assignedHandlersToEditorContainingPage = true;
		}
	protected 	void		calcPreferredSize(MiSize size)
		{
		calcMinimumSize(size);
		}
	protected 	void		calcMinimumSize(MiSize size)
		{
		super.calcMinimumSize(size);
		if (orientation == Mi_HORIZONTAL)
			{
			for (int i = 0; i < ticks.length; ++i)
				{
				MiDistance height = ticks[i].getPreferredSize(tmpSize).height;
				height += getInsetMargins().getHeight();
				if (size.height < height)
					size.height = height;
				}
			for (int i = 0; i < tickLabels.length; ++i)
				{
				if (tickLabels[i] != null)
					{
					MiDistance height = tickLabels[i].getPreferredSize(tmpSize).height;
					height += getInsetMargins().getHeight();
					if (size.height < height)
						size.height = height;
					}
				}
			}
		else
			{
			for (int i = 0; i < ticks.length; ++i)
				{
				MiDistance width = ticks[i].getPreferredSize(tmpSize).width;
				width += getInsetMargins().getWidth();
				if (size.width < width)
					size.width = width;
				}
			for (int i = 0; i < tickLabels.length; ++i)
				{
				if (tickLabels[i] != null)
					{
					MiDistance width = tickLabels[i].getPreferredSize(tmpSize).width;
					width += getInsetMargins().getWidth();
					if (size.width < width)
						size.width = width;
					}
				}
			}
		}
	protected	void		render(MiRenderer renderer)
		{
		// Draw the background...
		super.render(renderer);

		if (!initialized)
			updateRuler();

		// -------------------------------------------------------
		// Get the clip bounds from the renderer and then intersect
		// these clip bounds with the inner bounds of this container.
		// If there is not such an intersection...
		// -------------------------------------------------------
		MiBounds clip = renderer.getClipBounds(tmpClipBounds);
		if ((mouseLocationTick != null) && (mouseLocationTick.getBounds(tmpBounds).equals(clip)))
			{
			return;
			}

		if (!getInnerBounds(tmpBounds).intersectionWith(clip))
			{
			return;
			}

		// -------------------------------------------------------
		// ... set the clip bounds of the renderer to this 
		// intersection bounds, draw this containers contents then
		// restore the clip bounds of the renderer.
		// -------------------------------------------------------
		renderer.setClipBounds(tmpBounds);
		renderer.setAttributes(getAttributes());
		getInnerBounds(tmpBounds);

		if (orientation == Mi_HORIZONTAL)
			{
			MiCoord x = startTickX;
			MiCoord ymax = tmpBounds.getYmax();
			MiCoord ymin = tmpBounds.getYmin();

			for (double tickValue = startTickValue; tickValue <= endTickValue; 
				tickValue += drawnTickResolution, x += distanceBetweenTicks)
				{
				if ((x - maxLabelSize.width > clip.xmax)
					|| (x + maxLabelSize.width < clip.xmin))
					{
					continue;
					}
				int tickResolutionIndex = getTickResolutionIndex(tickValue);
				MiPart tickLabel = tickLabels[tickResolutionIndex];
				boolean drawTick = true;
				if (tickLabel != null)
					{
					boolean drawLabel = true;
					if ((integerTickLabelsEqualTickValue) 
						&& (tickValue == Math.floor(tickValue)))
						{
						if (Math.floor(tickValue/tickLabelResolution)
							!= tickValue/tickLabelResolution)
							{
							drawLabel = false;
							}
						else
							{
							String value = Utility.toString((int )tickValue);
							if (tickLabel instanceof MiText)
								((MiText )tickLabel).setText(value);
							else if (tickLabel instanceof MiWidget)
								((MiWidget )tickLabel).setValue(value);
							}
						}
					
					if (drawLabel)
						{
						MiCoord labelY = (ymin + ymax)/2;
						switch (labelJustification)
							{
							case Mi_TOP_JUSTIFIED :
								labelY = ymax - tickLabel.getHeight()/2;
								break;
							case Mi_BOTTOM_JUSTIFIED :
								labelY = ymin + tickLabel.getHeight()/2
									-tickLabel.getFont().getMaximumDescent();
								break;
							case Mi_CENTER_JUSTIFIED :
								labelY = (ymin + ymax)/2;
								break;
							case Mi_JUSTIFIED:
								labelY = (ymin + ymax)/2;
								break;
							}
						if (!tickLabelsSupplementTicks)
							{
							tickLabel.setCenter(x, labelY);
							tickLabel.render(renderer);
							drawTick = false;
							}
						else
							{
							tickLabel.setCenter(x - tickLabel.getWidth()/2 - 1, labelY);
							tickLabel.render(renderer);
							}
						}
					}
				if (drawTick)
					{
					MiPart tick = ticks[tickResolutionIndex];
					switch (tickJustification)
						{
						case Mi_TOP_JUSTIFIED :
							tick.setCenter(x, ymax - tick.getHeight()/2);
							break;
						case Mi_BOTTOM_JUSTIFIED :
							tick.setCenter(x, ymin + tick.getHeight()/2);
							break;
						case Mi_CENTER_JUSTIFIED :
							tick.setCenter(x, (ymin + ymax)/2);
							break;
						case Mi_JUSTIFIED:
							tick.setHeight(ymax - ymin);
							tick.setCenter(x, (ymin + ymax)/2);
							break;
						}
					tick.render(renderer);
					}
				}
			for (int i = 0; i < annotations.size(); ++i)
				{
				MiPart anno = annotations.elementAt(i);
				double annoValue = annotationLocations.elementAt(i);
				int annoJustification = annotationJustifications.elementAt(i);
				x = tmpBounds.getXmin() + (annoValue - startTickValue)
					/drawnTickResolution * distanceBetweenTicks;

				switch (annoJustification)
					{
					case Mi_TOP_JUSTIFIED :
						anno.setCenter(x, ymax - anno.getHeight()/2);
						break;
					case Mi_BOTTOM_JUSTIFIED :
						anno.setCenter(x, ymin + anno.getHeight()/2);
						break;
					case Mi_CENTER_JUSTIFIED :
						anno.setCenter(x, (ymin + ymax)/2);
						break;
					case Mi_JUSTIFIED :
						anno.setHeight(ymax - ymin);
						anno.setCenter(x, (ymin + ymax)/2);
						break;
					}
				anno.render(renderer);
				}
			}
		else
			{
			MiCoord y = startTickY;
			MiCoord xmax = tmpBounds.getXmax();
			MiCoord xmin = tmpBounds.getXmin();

			for (double tickValue = startTickValue; tickValue <= endTickValue; 
				tickValue += drawnTickResolution, y += distanceBetweenTicks)
				{
				if ((y - maxLabelSize.height > clip.ymax)
					|| (y + maxLabelSize.height < clip.ymin))
					{
					continue;
					}
				int tickResolutionIndex = getTickResolutionIndex(tickValue);
				MiPart tickLabel = tickLabels[tickResolutionIndex];
				boolean drawTick = true;

				if (tickLabel != null)
					{
					boolean drawLabel = true;
					if ((integerTickLabelsEqualTickValue) 
						&& (tickValue == Math.floor(tickValue)))
						{
						if (Math.floor(tickValue/tickLabelResolution)
							!= tickValue/tickLabelResolution)
							{
							drawLabel = false;
							}
						else
							{
							String value = Utility.toString((int )tickValue);
							if (tickLabel instanceof MiText)
								((MiText )tickLabel).setText(value);
							else if (tickLabel instanceof MiWidget)
								((MiWidget )tickLabel).setValue(value);
							}
						}
					if (drawLabel)
						{
						MiCoord labelX = (xmin + xmax)/2;
						switch (labelJustification)
							{
							case Mi_RIGHT_JUSTIFIED :
								labelX = xmax - tickLabel.getWidth()/2;
								break;
							case Mi_LEFT_JUSTIFIED :
								labelX = xmin + tickLabel.getWidth()/2;
								break;
							case Mi_CENTER_JUSTIFIED :
								labelX = (xmin + xmax)/2;
								break;
							case Mi_JUSTIFIED:
								labelX = (xmin + xmax)/2;
								break;
							}
						if (!tickLabelsSupplementTicks)
							{
							tickLabel.setCenter(labelX, y);
							tickLabel.render(renderer);
							drawTick = false;
							}
						else
							{
							tickLabel.setCenter(labelX, y - tickLabel.getHeight()/2 - 1);
							tickLabel.render(renderer);
							}
						}
					}
				if (drawTick)
					{
					MiPart tick = ticks[tickResolutionIndex];
					switch (tickJustification)
						{
						case Mi_RIGHT_JUSTIFIED :
							tick.setCenter(xmax - tick.getWidth()/2, y);
							break;
						case Mi_LEFT_JUSTIFIED :
							tick.setCenter(xmin + tick.getWidth()/2, y);
							break;
						case Mi_CENTER_JUSTIFIED :
							tick.setCenter((xmin + xmax)/2, y);
							break;
						case Mi_JUSTIFIED:
							tick.setWidth(xmax - xmin);
							tick.setCenter((xmin + xmax)/2, y);
							break;
						}
					tick.render(renderer);
					}
				}
			for (int i = 0; i < annotations.size(); ++i)
				{
				MiPart anno = annotations.elementAt(i);
				double annoValue = annotationLocations.elementAt(i);
				int annoJustification = annotationJustifications.elementAt(i);
				y = tmpBounds.getYmin() + (annoValue - startTickValue)
					/drawnTickResolution * distanceBetweenTicks;

				switch (annoJustification)
					{
					case Mi_RIGHT_JUSTIFIED :
						anno.setCenter(xmax - anno.getWidth()/2, y);
						break;
					case Mi_LEFT_JUSTIFIED :
						anno.setCenter(xmin + anno.getWidth()/2, y);
						break;
					case Mi_CENTER_JUSTIFIED :
						anno.setCenter((xmin + xmax)/2, y);
						break;
					case Mi_JUSTIFIED :
						anno.setWidth(xmax - xmin);
						anno.setCenter((xmin + xmax)/2, y);
						break;
					}
				anno.render(renderer);
				}
			}
		renderer.setClipBounds(clip);
		}
	private		int		getTickResolutionIndex(double value)
		{
		if (value < 0)
			value = -value;
		value = value - Math.floor(value);
		for (int i = tickResolutions.length - 1; i >= 0; --i)
			{
			if ((value == tickResolutions[i])
				|| (value/tickResolutions[i] == Math.floor(value/tickResolutions[i])))
				{
				return(i);
				}
			}
System.out.println("getTickResolutionIndex failed - returning 0");
		return(0);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equals(MiMouseLocationDisplayMonitor2.Mi_MOUSE_X_NAME))
			{
			if (orientation == Mi_HORIZONTAL)
				updateMouseLocationTick();
			return;
			}
		if (name.equals(MiMouseLocationDisplayMonitor2.Mi_MOUSE_Y_NAME))
			{
			if (orientation == Mi_VERTICAL)
				updateMouseLocationTick();
			return;
			}
		if (name.equalsIgnoreCase(Mi_VISIBLE_NAME))
			setVisible(Utility.toBoolean(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_TICK_JUSTIFICATION_NAME))
			setTickJustification(MiSystem.getValueOfAttributeValueName(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_TICK_RESOLUTION_NAME))
			setTickResolution(Utility.toDouble(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_TICK_COLOR_NAME))
			setTickColor(MiColorManager.getColor(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LABEL_JUSTIFICATION_NAME))
			setLabelJustification(MiSystem.getValueOfAttributeValueName(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LABEL_COLOR_NAME))
			setLabelColor(MiColorManager.getColor(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LABEL_FONT_NAME))
			setLabelFont(getLabelFont().setName(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LABEL_SIZE_NAME))
			setLabelPointSize(Utility.toInteger(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LOCATION_TICK_COLOR_NAME))
			setMouseLocationTickColor(MiColorManager.getColor(value));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LOCATION_TICK_THICKNESS_NAME))
			setMouseLocationTickThickness(Utility.toDouble(value));
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
		if (name.equalsIgnoreCase(Mi_VISIBLE_NAME))
			{
			return(Utility.toString(isVisible()));
			}
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_TICK_JUSTIFICATION_NAME))
			{
			if (orientation == Mi_VERTICAL)
				{
				return(MiSystem.getNameOfAttributeValue(
					horizontalJustificationNames, getTickJustification()));
				}
			else
				{
				return(MiSystem.getNameOfAttributeValue(
					verticalJustificationNames, getTickJustification()));
				}
			}
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LABEL_JUSTIFICATION_NAME))
			{
			if (orientation == Mi_VERTICAL)
				{
				return(MiSystem.getNameOfAttributeValue(
					horizontalJustificationNames, getLabelJustification()));
				}
			else
				{
				return(MiSystem.getNameOfAttributeValue(
					verticalJustificationNames, getLabelJustification()));
				}
			}
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LABEL_COLOR_NAME))
			return(MiColorManager.getColorName(getLabelColor()));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LABEL_FONT_NAME))
			return(getLabelFont().getName());
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LABEL_SIZE_NAME))
			return("" + getLabelPointSize());
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_TICK_RESOLUTION_NAME))
			return("" + getTickResolution());
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_TICK_COLOR_NAME))
			return(MiColorManager.getColorName(getTickColor()));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LOCATION_TICK_COLOR_NAME))
			return(MiColorManager.getColorName(getMouseLocationTickColor()));
		else if (Utility.equalsIgnoreCaseAndSpaces(name, Mi_LOCATION_TICK_THICKNESS_NAME))
			return("" +  getMouseLocationTickThickness());
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
			new MiPropertyDescription(Mi_VISIBLE_NAME, Mi_BOOLEAN_TYPE, "true"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BACKGROUND_COLOR_ATT_NAME, Mi_COLOR_TYPE, "white"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LABEL_COLOR_NAME, Mi_COLOR_TYPE, "black"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LABEL_FONT_NAME, Mi_FONT_NAME_TYPE, "Courier"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LABEL_SIZE_NAME, Mi_POSITIVE_INTEGER_TYPE, "12"));
		if (orientation == Mi_VERTICAL)
			{
			propertyDescriptions.addElement(
				new MiPropertyDescription(Mi_LABEL_JUSTIFICATION_NAME, 
				new Strings(horizontalJustificationNames), Mi_RIGHT_JUSTIFIED_NAME));
			propertyDescriptions.addElement(
				new MiPropertyDescription(Mi_TICK_JUSTIFICATION_NAME, 
				new Strings(horizontalJustificationNames), Mi_LEFT_JUSTIFIED_NAME));
			}
		else
			{
			propertyDescriptions.addElement(
				new MiPropertyDescription(Mi_LABEL_JUSTIFICATION_NAME, 
				new Strings(verticalJustificationNames), Mi_BOTTOM_JUSTIFIED_NAME));
			propertyDescriptions.addElement(
				new MiPropertyDescription(Mi_TICK_JUSTIFICATION_NAME, 
				new Strings(verticalJustificationNames), Mi_TOP_JUSTIFIED_NAME));
			}
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_TICK_COLOR_NAME, Mi_COLOR_TYPE, "black"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_TICK_RESOLUTION_NAME, Mi_DOUBLE_TYPE, "0.0625"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LOCATION_TICK_COLOR_NAME, Mi_COLOR_TYPE, "blue"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LOCATION_TICK_THICKNESS_NAME, Mi_POSITIVE_DOUBLE_TYPE, "2"));

		//propertyDescriptions = new MiPropertyDescriptions(propertyDescriptions);
		//propertyDescriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());

		return(propertyDescriptions);
		}
	}
class MiMouseLocationDisplayMonitor2 extends MiEventMonitor
	{
	public static final String	Mi_MOUSE_X_NAME		= "MouseX:";
	public static final String	Mi_MOUSE_Y_NAME		= "MouseY:";
	private		MiPart		monitor;
	private		MiCoord		lastX			= MiiTypes.Mi_MAX_COORD_VALUE;
	private		MiCoord		lastY			= MiiTypes.Mi_MAX_COORD_VALUE;


	/**
	 * Used for copy only, does not make sense w/o monitor.
	 */
	public				MiMouseLocationDisplayMonitor2()
		{
		}
	public				MiMouseLocationDisplayMonitor2(MiPart monitor)
		{
		this.monitor = monitor;
		}
	public		int		processEvent(MiEvent event)
		{
		if ((isEnabled())
			&& (event.getType() != Mi_TIMER_TICK_EVENT)
			&& (event.getType() != Mi_IDLE_EVENT))
			{
			if (event.worldPt.x != lastX)
				{
				lastX = event.worldPt.x;
				monitor.setPropertyValue(Mi_MOUSE_X_NAME, Utility.toString(event.worldPt.x));
				}
			if (event.worldPt.y != lastY)
				{
				lastY = event.worldPt.y;
				monitor.setPropertyValue(Mi_MOUSE_Y_NAME, Utility.toString(event.worldPt.y));
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}
class MiMouseEnterExitActionDispatcher extends MiEventMonitor implements MiiActionTypes
	{
	public				MiMouseEnterExitActionDispatcher()
		{
		}
	public		int		processEvent(MiEvent event)
		{
		if (isEnabled())
			{
			if (event.getType() == Mi_MOUSE_ENTER_EVENT)
				getTarget().dispatchAction(Mi_MOUSE_ENTER_ACTION);
			else if (event.getType() == Mi_MOUSE_EXIT_EVENT)
				getTarget().dispatchAction(Mi_MOUSE_EXIT_ACTION);
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}

