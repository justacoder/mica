
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

/**----------------------------------------------------------------------------------------------
 * This is a MiContainer that can have a visible background. The
 * shape the background has is specified by choosing one of a 
 * number of predefined shape types or by specifying a MiPart as 
 * the shape. 

 * There are also a number of options available to specify how the
 * shape is sized with respect to the bounds of the all of the parts
 * in this container.
 *
 * The background shape always has the same attribute values as 
 * this container (for example: background color and border look).
 *
 * Because this is also a MiLayout, the parts of this container
 * can also be automatically laid out, when enabled (see
 * setVisibleContainerAutomaticLayoutEnabled()). Supported 
 * contraints are:
 *
 *  elementHSizing
 *  elementHJustification
 *  elementVSizing
 *  elementVJustification
 *
 * This layout occurs in addition to any layout that may also
 * be assigned to this container (see setLayout()).
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiVisibleContainer extends MiLayout
	{
	public final static String	Mi_SHAPE_PROPERTY_NAME		= "shape";

	public final static int		RECTANGLE_SHAPE			= 0;
	public final static int		CIRCLE_SHAPE			= 1;
	public final static int		ROUND_RECTANGLE_SHAPE		= 2;
	public final static int		DIAMOND_SHAPE			= 3;
	public final static int		TRIANGLE_POINTING_UP_SHAPE	= 4;
	public final static int		TRIANGLE_POINTING_DOWN_SHAPE	= 5;
	public final static int		TRIANGLE_POINTING_RIGHT_SHAPE	= 6;
	public final static int		TRIANGLE_POINTING_LEFT_SHAPE	= 7;

	public static final int		MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS 			= 1;
	public static final int		MAKE_CONTAINER_SAME_SIZE_OR_BIGGER_THAN_CONTENTS	= 2;
	public static final int		MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS_OR_OVERRIDDEN_PREFERRED_SIZE= 3;

	private static	MiPart		rectangleShape		= new MiRectangle();
	private static	MiPart		circleShape		= new MiCircle();
	private static	MiPart		roundRectangleShape	= new MiRoundRectangle();
	private static	MiPart		upTriangle		= new MiTriangle(Mi_UP);
	private static	MiPart		downTriangle		= new MiTriangle(Mi_DOWN);
	private static	MiPart		leftTriangle		= new MiTriangle(Mi_LEFT);
	private static	MiPart		rightTriangle		= new MiTriangle(Mi_RIGHT);

	private static	MiMargins	tmpMargins		= new MiMargins();

	private		int		autoLayoutRule		= MAKE_CONTAINER_SAME_SIZE_OR_BIGGER_THAN_CONTENTS;
	private 	boolean		autoLayoutEnabled	= true;
	private 	MiPart		shape			= rectangleShape;
	private 	boolean		hasFixedWidthOrHeight;
	private		MiMargins	preferredMargins;
	private		boolean		displaysFocusBorder;
	private		boolean		okToDrawOutsideInnerBounds;



					/**------------------------------------------------------
	 				 * Constructs a new MiVisibleContainer. 
					 *------------------------------------------------------*/
	public				MiVisibleContainer()
		{
		setInsetMargins(4);
		MiBounds tmpBounds = MiBounds.newBounds();
		tmpBounds.zeroOut();
		tmpBounds.addMargins(getInsetMargins(tmpMargins));
		replaceBounds(tmpBounds);
		setTarget(this);
		setBorderLook(Mi_NONE);
		MiBounds.freeBounds(tmpBounds);
		}

					/**------------------------------------------------------
	 				 * Specifies how the background shape is to be automatically
					 * resized with respect to the bounds of the parts of this
					 * container. Valid values are:
					 *   MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS
					 *   MAKE_CONTAINER_SAME_SIZE_OR_BIGGER_THAN_CONTENTS
					 *   MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS_OR_OVERRIDDEN_PREFERRED_SIZE
					 * the default value is:
					 *   MAKE_CONTAINER_SAME_SIZE_OR_BIGGER_THAN_CONTENTS
					 * @param spec		How the shape is to be sized.
					 * @see			MiVisibleContainer#getContainerLayoutSpec
					 *------------------------------------------------------*/
	public		void		setContainerLayoutSpec(int spec)
		{
		autoLayoutRule = spec;
		}
					/**------------------------------------------------------
	 				 * Gets how the background shape is automatically resized 
					 * with respect to the bounds of the parts of this
                                         * container.
					 * @return		how the background shape is sized.
					 * @see			MiVisibleContainer#setContainerLayoutSpec
					 *------------------------------------------------------*/
	public		int		getContainerLayoutSpec()
		{
		return(autoLayoutRule);
		}
					/**------------------------------------------------------
	 				 * Specifies whether the automatic background shape resizing
					 * is enabled. NOTE: When 'disabled' the current bounds 
					 * becomes the preferredSize, minimumSize and the current 
					 * bounds is not affected by the extent of any parts of 
					 * this container in any way. The default value is true.
					 * @param flag		true if auto-sizing is enabled
					 * @see			
					 * MiVisibleContainer#getVisibleContainerAutomaticLayoutEnabled
					 * @see			
					 * MiVisibleContainer#setContainerLayoutSpec
					 *------------------------------------------------------*/
	public		void		setVisibleContainerAutomaticLayoutEnabled(boolean flag)
		{
		autoLayoutEnabled = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether the automatic background shape resizing
					 * is enabled.
					 * @return		true if auto-sizing is enabled
					 * @see			
					 * MiVisibleContainer#setVisibleContainerAutomaticLayoutEnabled
					 * @see			
					 * MiVisibleContainer#setContainerLayoutSpec
					 *------------------------------------------------------*/
	public		boolean		getVisibleContainerAutomaticLayoutEnabled()
		{
		return(autoLayoutEnabled);
		}
	public		void		setOKToDrawOutsideInnerBounds(boolean flag)
		{
		okToDrawOutsideInnerBounds = flag;
		}
	public		boolean		getOKToDrawOutsideInnerBounds()
		{
		return(okToDrawOutsideInnerBounds);
		}
					/**------------------------------------------------------
					 * Sets the margins of this MiPart.
					 * @param m		the margins or null
					 * @overrides 		MiPart#setMargins
					 *------------------------------------------------------*/
	public		void		setMargins(MiMargins m)
		{
		if (preferredMargins != null)
			preferredMargins.copy(m);
		else
			preferredMargins = new MiMargins(m);

		MiMargins currentMargins = getMargins(tmpMargins);

		if (displaysFocusBorder)
			{
			currentMargins.setMargins(getBorderHiliteWidth());
			currentMargins.union(preferredMargins);
			}
		else
			{
			currentMargins.copy(preferredMargins);
			}
		super.setMargins(currentMargins);
		refreshBounds();
		}
	/**
	This must be called if the focus border is to be visible. This is because this
	part will only render the parts and hilites that appear within the innerBounds,
	which might be *this's* hilite border or possibly a parts hilite border. And if
	they did not allocate realBounds - not drawBounds - for the hilite border then
	the layout will just put this on top of it. So call this to display a hilite border.
	*/
					/**------------------------------------------------------
					 * Specifies whether the focus border (hilite border) is 
					 * to be enabled for this container.
					 * @param flag		true if enabled
					 *------------------------------------------------------*/
	public		void		setDisplaysFocusBorder(boolean flag)
		{
		displaysFocusBorder = flag;
		if (displaysFocusBorder)
			{
			tmpMargins.setMargins(getBorderHiliteWidth());
			if (preferredMargins != null)
				tmpMargins.union(preferredMargins);
			}
		else
			{
			if (preferredMargins != null)
				tmpMargins.copy(preferredMargins);
			else
				tmpMargins.setMargins(0,0,0,0);
			}
		super.setMargins(tmpMargins);
		invalidateLayout();
		}
					/**------------------------------------------------------
					 * Gets whether the focus border (hilite border) is 
					 * enabled for this container.
					 * @return 		true if enabled
					 *------------------------------------------------------*/
	public		boolean		getDisplaysFocusBorder()
		{
		return(displaysFocusBorder);
		}
					/**------------------------------------------------------
					 * Gets the background shape of this visible container.
					 * @return		the background shape
					 *------------------------------------------------------*/
	public		MiPart		getShape()			
		{ 
		return(shape);
		}
					/**------------------------------------------------------
					 * Sets the background shape of this visible container.
					 * @param s		the background shape
					 *------------------------------------------------------*/
	public		void		setShape(MiPart s)		
		{ 
		shape = s; 
		invalidateLayout();
		}
					/**------------------------------------------------------
					 * Sets the background shape type of this visible container.
					 * This will create a MiPart of corresponding to the shape
					 * type. Supported shape types are:
					 *   RECTANGLE_SHAPE
					 *   CIRCLE_SHAPE
					 *   ROUND_RECTANGLE_SHAPE
					 *   DIAMOND_SHAPE
					 *   TRIANGLE_POINTING_UP_SHAPE
					 *   TRIANGLE_POINTING_DOWN_SHAPE
					 *   TRIANGLE_POINTING_RIGHT_SHAPE
					 *   TRIANGLE_POINTING_LEFT_SHAPE
					 *
					 * @param style		the background shape type
					 *------------------------------------------------------*/
	public		void		setShape(int style)
		{
		switch (style)
			{
			default:
			case RECTANGLE_SHAPE:
				shape = rectangleShape;
				break;
			case CIRCLE_SHAPE:
				shape = circleShape;
				break;
			case ROUND_RECTANGLE_SHAPE:
				shape = roundRectangleShape;
				break;
			case DIAMOND_SHAPE:
				shape = new MiPolygon();
				shape.appendPoint(new MiPoint (0.0, 0.0));
				shape.appendPoint(new MiPoint (1.0, 1.0));
				shape.appendPoint(new MiPoint (0.0, 2.0));
				shape.appendPoint(new MiPoint (-1.0, 1.0));
				break;
			case TRIANGLE_POINTING_UP_SHAPE	:
				shape = upTriangle;
				break;
			case TRIANGLE_POINTING_DOWN_SHAPE :
				shape = downTriangle;
				break;
			case TRIANGLE_POINTING_RIGHT_SHAPE :
				shape = rightTriangle;
				break;
			case TRIANGLE_POINTING_LEFT_SHAPE :
				shape = leftTriangle;
				break;
			}
		setShape(shape);
		}
					/**------------------------------------------------------
					 * Gets the shape of any shadow. Used by the shadow 
					 * renderers. This method returns MiiShadowRenderer.noShadowShape
					 * because when the shape associated with this is rendered
					 * _it_ will draw the shadow.
					 * @return		the shape
					 * @overrides		MiPart#getShadowShape
					 *------------------------------------------------------*/
	public		MiPart		getShadowShape()
		{
		return(MiiShadowRenderer.noShadowShape);
		}
					/**------------------------------------------------------
					 * Lays out the container's parts and it's background 
					 * shape.
					 * @overrides		MiLayout.doLayout
					 *------------------------------------------------------*/
	protected 	void		doLayout()
		{
		if (!autoLayoutEnabled)
			return;

		MiBounds contents 		= MiBounds.newBounds();
		MiBounds oldContents 		= MiBounds.newBounds();
		MiBounds containerBounds 	= MiBounds.newBounds();
		MiBounds tmp 			= MiBounds.newBounds();
		MiSize size 			= MiSize.newSize();
		// -------------------------------------------------------
		// Iterate through all parts of this container... set their
		// size to their preferredSize if this container does not
		// have a layout which has already done so. Also collect
		// the union of their bounds into 'contents'. 
		// -------------------------------------------------------
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if (getPart(i).isVisible())
				{
				// -------------------------------------------------------
				// If contents have not already been laid out...
				// -------------------------------------------------------
				if ((getLayout() == null) || (!getLayout().determinesPreferredAndMinimumSizes()))
					{
					getPart(i).getPreferredSize(size);
					getPart(i).setSize(size);
					}
				getPart(i).getBounds(tmp);
				contents.union(tmp);
				}
			}

		// -------------------------------------------------------
		// Set 'oldContents' to this current bounds of all of this
		// container's parts. This will be used later to see it
		// we have changed the size of this container. 'contents'
		// will be modified by this layout routine as per the layout
		// constraints applied to this container.
		// -------------------------------------------------------
		oldContents.copy(contents);

		// -------------------------------------------------------
		// Get the inner bounds of this container into 'container'.
		// -------------------------------------------------------
		MiBounds container = getInnerBounds(containerBounds);
		if (container.isReversed())
			{
			getPreferredSize(size);
			replaceBounds(new MiBounds(size));
			container = getInnerBounds(containerBounds);
			}

		// -------------------------------------------------------
		// Set the bounds to the center of the inner bounds.
		// -------------------------------------------------------
		contents.setCenterX(container.getCenterX());
		contents.setCenterY(container.getCenterY());

		// -------------------------------------------------------
		// Expand the bounds to the size of the inner bounds, if
		// so specified by the elementSizing contraints.
		// -------------------------------------------------------
		if (getElementHSizing() == Mi_EXPAND_TO_FILL)
			contents.setWidth(container.getWidth());
		else if (getElementVSizing() == Mi_EXPAND_TO_FILL)
			contents.setHeight(container.getHeight());

		// -------------------------------------------------------
		// Translate the bounds in a horizontal direction if so
		// specified by the elementHJustification contraint.
		// -------------------------------------------------------
		int elementHJustification = getElementHJustification();
		if (elementHJustification == Mi_LEFT_JUSTIFIED)
			contents.translateXminTo(container.getXmin());
		else if (elementHJustification == Mi_RIGHT_JUSTIFIED)
			contents.translateXmaxTo(container.getXmax());
		else if (elementHJustification == Mi_CENTER_JUSTIFIED)
			contents.setCenterX(container.getCenterX());
		else if (elementHJustification == Mi_JUSTIFIED)
			contents.setCenterX(container.getCenterX());
	
		// -------------------------------------------------------
		// Modify the bounds to stay within the inner bounds as
		// held in the 'container' variable.
		// -------------------------------------------------------
		if (contents.getXmin() < container.getXmin()) 
			contents.translateXminTo(container.getXmin());
		if (contents.getXmax() > container.getXmax())
			contents.translateXmaxTo(container.getXmax());
		if (contents.getXmin() < container.getXmin()) 
			contents.setXmin(container.getXmin());


		// -------------------------------------------------------
		// Translate the bounds in a vertical direction if so
		// specified by the elementVJustification contraint.
		// -------------------------------------------------------
		int elementVJustification = getElementVJustification();
		if (elementVJustification == Mi_BOTTOM_JUSTIFIED)
			contents.translateYminTo(container.getYmin());
		else if (elementVJustification == Mi_TOP_JUSTIFIED)
			contents.translateYmaxTo(container.getYmax());
		else if (elementVJustification == Mi_CENTER_JUSTIFIED)
			contents.setCenterY(container.getCenterY());
		else if (elementVJustification == Mi_JUSTIFIED)
			contents.setCenterY(container.getCenterY());

		// -------------------------------------------------------
		// Modify the bounds to stay within the inner bounds as
		// held in the 'container' variable.
		// -------------------------------------------------------
		if (container.getYmin() > contents.getYmin()) 
			contents.translateYminTo(container.getYmin());
		if (container.getYmax() < contents.getYmax())
			contents.translateYmaxTo(container.getYmax());
		if (container.getYmin() > contents.getYmin()) 
			contents.setYmin(container.getYmin());

		// -------------------------------------------------------
		// If we have changed the bounds in some way...
		// -------------------------------------------------------
		if (!oldContents.equals(contents))
			{
			// -------------------------------------------------------
			// Calculate how much we have to translate the parts...
			// -------------------------------------------------------
			MiDistance tx = contents.getCenterX() - oldContents.getCenterX();
			MiDistance ty = contents.getCenterY() - oldContents.getCenterY();
			// -------------------------------------------------------
			// If there are no expand-to-fill contraints...
			// -------------------------------------------------------
			if ((getElementHSizing() != Mi_EXPAND_TO_FILL) 
				&& (getElementVSizing() != Mi_EXPAND_TO_FILL))
				{
				// -------------------------------------------------------
				// Just translate all of the parts and we are done...
				// -------------------------------------------------------
				for (int i = 0; i < getNumberOfParts(); ++i)
					{
					getPart(i).translate(tx, ty);
					}
				}
			else
				{
				// -------------------------------------------------------
				// There is an expand-to-fill contraint, we need to scale
				// the parts... calculate the scale factor for each dimension.
				// -------------------------------------------------------
				MiPoint center = contents.getCenter();
				MiScale scale = new MiScale(
					contents.getWidth()/oldContents.getWidth(), 
					contents.getHeight()/oldContents.getHeight());
				if (getElementHSizing() != Mi_EXPAND_TO_FILL)
					scale.x = 1;
				if (getElementVSizing() != Mi_EXPAND_TO_FILL)
					scale.y = 1;
				// -------------------------------------------------------
				// Iterate through the parts, translating and scaling
				// -------------------------------------------------------
				for (int i = 0; i < getNumberOfParts(); ++i)
					{
					getPart(i).translate(tx, ty);
					getPart(i).scale(center, scale);
					}
				}
			}
		// -------------------------------------------------------
		// Release all the bounds we allocated above
		// -------------------------------------------------------
		MiBounds.freeBounds(contents);
		MiBounds.freeBounds(oldContents);
		MiBounds.freeBounds(containerBounds);
		MiBounds.freeBounds(tmp);
		MiSize.freeSize(size);
		}
					/**------------------------------------------------------
					 * Realculates the outer bounds of this container. 
					 * @param bounds 	the (returned) outer bounds
					 *------------------------------------------------------*/
	protected 	void		reCalcBounds(MiBounds bounds)
		{
		getBounds(bounds);

		if (!autoLayoutEnabled)
			{
			return;
			}

		MiBounds contents = MiBounds.newBounds();
		MiMargins insetMargins = getInsetMargins();
		MiMargins cellMargins = getCellMargins();

		super.reCalcBounds(contents);
		if (contents.isReversed())
			{
			contents.copy(bounds);
			if (contents.isReversed())
				{
				contents.setBounds(0,0,0,0);
				contents.addMargins(insetMargins);
				contents.addMargins(cellMargins);
				contents.addMargins(getMargins(tmpMargins));
				}
			else if ((contents.getWidth()
					< insetMargins.getWidth() 
						+ getMargins(tmpMargins).getWidth() + cellMargins.getWidth())
				|| (contents.getHeight() 
					< insetMargins.getHeight() 
						+ getMargins(tmpMargins).getHeight() + cellMargins.getHeight()))
				{
				contents.setWidth(insetMargins.getWidth() + getMargins(tmpMargins).getWidth() 
					+ cellMargins.getWidth());
				contents.setHeight(insetMargins.getHeight() + getMargins(tmpMargins).getHeight() 
					+ cellMargins.getHeight());
				}
			bounds.copy(contents);
			}
		else
			{
			// SUPER CLASS DOES THIS contents.addMargins(insetMargins);
			contents.addMargins(cellMargins);
			// SUPER CLASS DOES THIS contents.addMargins(getMargins(tmpMargins));
			}
			
		if ((autoLayoutRule == MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS))
			{
			bounds.copy(contents);
			}
		else if ((autoLayoutRule == MAKE_CONTAINER_SAME_SIZE_OR_BIGGER_THAN_CONTENTS))
			{
			if (!contents.isContainedIn(bounds))
				bounds.copy(contents);
			}
		else if ((autoLayoutRule == MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS_OR_OVERRIDDEN_PREFERRED_SIZE))
			{
			bounds.copy(contents);
			if (hasOverriddenPreferredSize())
				{
				bounds.setSize(getPreferredSize(new MiSize()));
				}
			}

		if (hasFixedWidthOrHeight)
			{
			getBounds(contents);

			if (hasFixedWidth())
				{
				bounds.xmin = contents.xmin;
				bounds.xmax = contents.xmax;
				}
			if (hasFixedHeight())
				{
				bounds.ymin = contents.ymin;
				bounds.ymax = contents.ymax;
				}
			}
		MiBounds.freeBounds(contents);
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
	 				 * @param size		the (returned) minimum size
					 * @overrides		MiContainer#calcMinimumSize
					 * @see			MiPart#getMinimumSize
					 *------------------------------------------------------*/
	protected	void		calcMinimumSize(MiSize size)
		{
		if (getNumberOfParts() == 0)
			{
			size.zeroOut();
			}
		else
			{
			super.calcMinimumSize(size);
			}
		size.addMargins(getCellMargins(tmpMargins));
		size.addMargins(getInsetMargins(tmpMargins));
		size.addMargins(getMargins(tmpMargins));
		}
					/**------------------------------------------------------
	 				 * Gets the preferred size of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
	 				 * @param size		the (returned) preferred size
					 * @overrides		MiContainer#calcPreferredSize
					 * @see			MiPart#getPreferredSize
					 *------------------------------------------------------*/
	protected	void		calcPreferredSize(MiSize size)
		{
		if ((getNumberOfParts() == 0)
			&& ((getLayout() == null) || (!getLayout().determinesPreferredAndMinimumSizes())))
			{
			size.zeroOut();
			}
		else
			{
			super.calcPreferredSize(size);
			}
		size.addMargins(getCellMargins(tmpMargins));
		size.addMargins(getInsetMargins(tmpMargins));
		size.addMargins(getMargins(tmpMargins));
		}
					/**------------------------------------------------------
					 * Gets whether if there is an intersection between the
					 * line formed by the given points and the outer bounds
					 * of this MiPart. If, there is, also returns the point
					 * of intersection. This method is most usefull when 
					 * insidePoint is inside these bounds.
					 * @param insidePoint	the point inside this MiPart's
					 *			outer bounds
					 * @param otherPoint	the point outside this MiPart's
					 *			outer bounds
					 * @param returnedIntersectionPoint
					 *			the (returned) point of 
					 *			intersection of the line between
					 *			the two given points and the
					 *			outer bounds of this MiPart
					 * @return 		true if there was an intersection
					 * @overrides 		MiContainer#getIntersectionWithLine
					 *------------------------------------------------------*/
	public		boolean		getIntersectionWithLine(
						MiPoint insidePoint, 
						MiPoint otherPoint, 
						MiPoint returnedIntersectionPoint)
		{
		if (shape != null)
			{
			shape.setAttributes(getAttributes());
			shape.setBounds(getBounds());
			return(shape.getIntersectionWithLine(
				insidePoint, otherPoint, returnedIntersectionPoint));
			}
		return(super.getIntersectionWithLine(insidePoint, otherPoint, returnedIntersectionPoint));
		}
					/**------------------------------------------------------
					 * Gets the total margins (inset margins plus margins).
					 * @return 	 	the total margins
					 *------------------------------------------------------*/
	public		MiMargins	getTotalMargins()
		{
		MiMargins m = getCellMargins();
		m.addMargins(getInsetMargins(tmpMargins));
		m.addMargins(getMargins(tmpMargins));
		return(m);
		}
					/**------------------------------------------------------
	 				 * Gets the inner bounds of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
	 				 * @param b		the (returned) inner bounds
	 				 * @return 		the inner bounds
					 * @overrides 	 	MiPart#getInnerBounds
					 *------------------------------------------------------*/
	public		MiBounds	getInnerBounds(MiBounds b)
		{
		return(getBounds(b).subtractMargins(getInsetMargins(tmpMargins))
			.subtractMargins(getMargins(tmpMargins)));
		}
					/**------------------------------------------------------
	 				 * Render this container (i.e. the background shape then
					 * all of it's parts).
	 				 * @param renderer 	the renderer to use for drawing
					 * @overrides		MiContainer#render
					 *------------------------------------------------------*/
	protected	void		render(MiRenderer renderer)
		{
		// -------------------------------------------------------
		// Get the outer bounds of this container, subtract margins
		// from it, then assign these bounds to the background shape.
		// -------------------------------------------------------
		MiBounds tmpBounds1 = MiBounds.newBounds();
		MiBounds tmpBounds2 = MiBounds.newBounds();
		getBounds(tmpBounds1);
		tmpBounds1.subtractMargins(getMargins(tmpMargins));
		if (hasFixedWidthOrHeight)
			{
			shape.setFixedHeight(false);
			shape.setFixedWidth(false);
			}
		if (shape instanceof MiMultiPointShape)
			shape.setBounds(tmpBounds1);
		else
			shape.replaceBounds(tmpBounds1);

		// -------------------------------------------------------
		// Assign this container's attributes to the background shape
		// and then draw the background shape.
		// -------------------------------------------------------
		shape.replaceAttributes(getAttributes());
		if (shape instanceof MiMultiPointShape)
			shape.refreshBounds();
		else
			shape.replaceDrawBounds(getDrawBounds(tmpBounds1));

		shape.draw(renderer);

		// -------------------------------------------------------
		// Get the clip bounds from the renderer and then intersect
		// these clip bounds with the inner bounds of this container.
		// If there is such an intersection...
		// But use the _drawBounds_ because shadows (and ?) extend
		// outside the innerBounds.
		// -------------------------------------------------------
		if (!okToDrawOutsideInnerBounds)
			{
			MiBounds clip = renderer.getClipBounds(tmpBounds1);
			if (getInnerBounds(tmpBounds2).intersectionWith(clip))
				{
				// -------------------------------------------------------
				// ... set the clip bounds of the renderer to this 
				// intersection bounds, draw this containers contents
				// (in super.render()), then restore the clip bounds of
				// the renderer.
				// -------------------------------------------------------
				renderer.setClipBounds(tmpBounds2);
				super.render(renderer);
				renderer.setClipBounds(clip);
				}
			}
		else
			{
			super.render(renderer);
			}
		MiBounds.freeBounds(tmpBounds1);
		MiBounds.freeBounds(tmpBounds2);
		}
					/**------------------------------------------------------
	 				 * Gets whether the given area intersects the bounds of
					 * this MiPart.
	 				 * @param area	 	the area
	 				 * @return		true if the given area overlaps
					 *			the bounds of this MiPart.
					 * @overrides		MiPart#pick
					 *------------------------------------------------------*/
	public		boolean		pick(MiBounds area)
		{
		MiBounds tmpBounds = MiBounds.newBounds();
		getBounds(tmpBounds);
		if (!tmpBounds.intersects(area))
			{
			MiBounds.freeBounds(tmpBounds);
			return(false);
			}
		if (shape instanceof MiRectangle)
			{
			MiBounds.freeBounds(tmpBounds);
			return(true);
			}
		tmpBounds.subtractMargins(getMargins(tmpMargins));
		shape.setBounds(tmpBounds);
		MiBounds.freeBounds(tmpBounds);
		return(shape.pick(area));
		}
					/**------------------------------------------------------
	 				 * Invalidates the given area of this container that 
					 * intersects the draw bounds. This intersection is
					 * necessary so, for example, changing the text in a 
					 * textfield does not cause areas outside the textfield
					 * to repaint.
					 * @param area		the area to invalidate
					 * @overrides		MiPart#invalidateArea
					 * @see			MiPart#setInvalidAreaNotificationsEnabled
					 *------------------------------------------------------*/
	public		void		invalidateArea(MiBounds area)
		{
		MiBounds tmpBounds = MiBounds.newBounds();
		getDrawBounds(tmpBounds);
		tmpBounds.intersectionWith(area);
		super.invalidateArea(tmpBounds);
		MiBounds.freeBounds(tmpBounds);
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiVisibleContainer obj 			= (MiVisibleContainer )source;
		autoLayoutRule				= obj.autoLayoutRule;
		autoLayoutEnabled			= obj.autoLayoutEnabled;
		shape					= obj.shape.copy();
		if (obj.preferredMargins != null)
			preferredMargins		= new MiMargins(obj.preferredMargins);
		else
			preferredMargins 		= null;
		displaysFocusBorder			= obj.displaysFocusBorder;
		}
					/**------------------------------------------------------
					 * Assigns the given attributes to this MiPart. Examine the
					 * attributes to see if hasFixedWidth or hasFixedHeight are
					 * set. If so, these will be assigned to this MiVisualContainer's
					 * associated shape and we will need to unassign them in order
					 * to resize the shape to the same size as this MiVisualContainer.
					 * This replaces 4 method calls and two array accesses with a
					 * boolean compare every render.
					 * @param atts		the attributes
					 * @overrides 		MiPart#setAttributes
					 *------------------------------------------------------*/
	public		void		setAttributes(MiAttributes atts)
		{
		if (atts != getAttributes())
			{
			super.setAttributes(atts);
			if (hasFixedWidth() || hasFixedHeight())
				hasFixedWidthOrHeight = true;
			else
				hasFixedWidthOrHeight = false;
			}
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given
					 * value. If no such property is found then sets the attribute
					 * with the given name to the given value. Valid attribute 
					 * names are found in the MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_SHAPE_PROPERTY_NAME))
			{
			if (Mi_RECTANGLE_TYPE_NAME.equalsIgnoreCase(value))
				setShape(RECTANGLE_SHAPE);
			else if (Mi_ROUND_RECTANGLE_TYPE_NAME.equalsIgnoreCase(value))
				setShape(ROUND_RECTANGLE_SHAPE);
			else if (Mi_CIRCLE_TYPE_NAME.equalsIgnoreCase(value))
				setShape(CIRCLE_SHAPE);
			else if ("diamond".equalsIgnoreCase(value))
				setShape(DIAMOND_SHAPE);
			else if ("upArrow".equalsIgnoreCase(value))
				setShape(TRIANGLE_POINTING_UP_SHAPE);
			else if ("downArrow".equalsIgnoreCase(value))
				setShape(TRIANGLE_POINTING_DOWN_SHAPE);
			else if ("leftArrow".equalsIgnoreCase(value))
				setShape(TRIANGLE_POINTING_LEFT_SHAPE);
			else if ("rightArrow".equalsIgnoreCase(value))
				setShape(TRIANGLE_POINTING_RIGHT_SHAPE);
			else
				setShape(MiUtility.createShape(value, false));
			}
		else
			{
			super.setPropertyValue(name, value);
			}
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
		if (name.equalsIgnoreCase(Mi_SHAPE_PROPERTY_NAME))
			return(shape.getClass().getName());
		else
			return(super.getPropertyValue(name));
		}
	}

