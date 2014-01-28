
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
 * This class draws a box with an optional label. The label
 * can be any graphical part.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiBox extends MiWidget
	{
	public static final	String		Mi_BOX_PROTOTYPE_CLASS_NAME = "Mi_BOX_PROTOTYPE_CLASS_NAME";
	private static		MiBox		prototype;
	private			MiPart		label;
	private			MiPolygon	shape;
	private			MiMargins	preferredMargins;
	private			MiMargins	preferredInsetMargins;
	private	static		MiMargins	tmpMargins			= new MiMargins();
	private	static		MiBounds	tmpBounds			= new MiBounds();



					/**------------------------------------------------------
	 				 * Constructs a new MiBox without a label. 
					 *------------------------------------------------------*/
	public				MiBox()
		{
		this((MiPart )null);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiBox with the given text label.
					 * @param text		the box's label text
					 *------------------------------------------------------*/
	public				MiBox(String text)
		{
		this((MiPart )null);
		setValue(text);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiBox with the given graphical label.
					 * @param label		the box's label
					 *------------------------------------------------------*/
	public				MiBox(MiPart label)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiBox");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			setBorderLook(Mi_RIDGE_BORDER_LOOK);
			setNormalAttributes(getNormalAttributes().setFontBold(true));
			setNormalAttributes(getNormalAttributes().setFontPointSize(16));

			shape = new MiPolygon();
			shape.setClosed(false);
			shape.appendPoint(new MiPoint(0.5, 1));
			shape.appendPoint(new MiPoint(0, 1));
			shape.appendPoint(new MiPoint(0, 0));
			shape.appendPoint(new MiPoint(1, 0));
			shape.appendPoint(new MiPoint(1, 1));
			shape.appendPoint(new MiPoint(0.5, 1));
			setShape(shape);
			}

		setLabel(label);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
					/**------------------------------------------------------
	 				 * Creates a new widget from the prototype. This is the
					 * factory pattern implementation for this widget. If the
					 * prototype is null, then the default contructor is used.
					 * @return 		the new widget
					 * @see 		#setPrototype
					 *------------------------------------------------------*/
	public static	MiBox		create()
		{
		if (prototype == null)
			return(new MiBox());

		return((MiBox )prototype.deepCopy());
		}
	public		void		setSensitive(boolean flag)
		{
		super.setSensitive(flag);
		if (flag == super.isSensitive())
			{
			if (label != null)
				label.setSensitive(flag);
			for (int i = 0; i < getNumberOfParts(); ++i)
				{
				getPart(i).setSensitive(flag);
				}
			}
		}
	public		void		setAttributes(MiAttributes attributes)
		{
		MiFont oldFont = getFont();
		super.setAttributes(attributes);
		if ((oldFont != getFont()) && (label != null))
			label.setFont(getFont());
		}
					/**------------------------------------------------------
	 				 * Set the value of this widget, which is the label, to 
					 * the given text.
	 				 * @param text 		the new label
					 *------------------------------------------------------*/
	public		void		setValue(String text)
		{
		if (Utility.isEmptyOrNull(text))
			{
			setLabel(null);
			}
		else
			{
			MiText label = new MiText(text);
			label.setFont(getFont());
			setLabel(label);
			}
		}
					/**------------------------------------------------------
	 				 * Get the value of this widget, which is the label. If
					 * the label is textual then it is returned. If the label
					 * is another widget, it's value is returned. If the label
					 * is some other kind of graphics, null is returned.
	 				 * @return 		the label text.
					 *------------------------------------------------------*/
	public		String		getValue()
		{
		if (label instanceof MiWidget)
			return(((MiWidget )label).getValue());
		else if (label instanceof MiText)
			return(((MiText )label).getText());
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the label of this box.
	 				 * @return  	the graphical label
					 *------------------------------------------------------*/
	public		MiPart		getLabel()
		{
		return(label);
		}
					/**------------------------------------------------------
	 				 * Sets the label of this box.
	 				 * @param l 	 	the label graphics
					 *------------------------------------------------------*/
	public		void		setLabel(MiPart l)
		{
		if (label != null)
			removeAttachment(label);

		label = l;

		MiDistance adjustment = calcMarginAdjustments();

		if (preferredMargins == null)
			preferredMargins = getMargins(new MiMargins());

		MiMargins margins = new MiMargins(preferredMargins);
		margins.top += adjustment;
		super.setMargins(margins);

		if (preferredInsetMargins == null)
			preferredInsetMargins = getInsetMargins();

		margins.copy(preferredInsetMargins);
		margins.addMargins(adjustment);
		super.setInsetMargins(margins);

		if (label == null)
			return;

		appendAttachment(label);
		label.setName("label");

		invalidateLayout();
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiBox obj 		= (MiBox )source;
		shape			= (MiPolygon )obj.shape.copy();
		if (obj.label != null)
			label = obj.label.copy();
		else
			label = null;
		}
					/**------------------------------------------------------
	 				 * Return a textual description of this class.
	 				 * @return 	the description
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(super.toString() + "." + getValue());
		}
					/**------------------------------------------------------
	 				 * Sets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @param p 		the new prototype
					 * @see 		#getPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	void		setPrototype(MiBox p)
		{
		prototype = p;
		}
					/**------------------------------------------------------
	 				 * Gets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @return  		the prototype
					 * @see 		#setPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	MiBox		getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_BOX_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_BOX_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiBox )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
		
	//***************************************************************************************
	// Protected methods
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Renders the box and it's label. Positions label at 
					 * desired location.
	 				 * @param renderer  	the renderer
					 *------------------------------------------------------*/
	protected	void		render(MiRenderer renderer)
		{
		MiBounds tmpBounds = MiBounds.newBounds();
		getBounds(tmpBounds).subtractMargins(getMargins(tmpMargins));
		shape.setPoint(0, tmpBounds.xmin, tmpBounds.ymax);
		shape.setPoint(1, tmpBounds.xmin, tmpBounds.ymax);
		shape.setPoint(2, tmpBounds.xmin, tmpBounds.ymin);
		shape.setPoint(3, tmpBounds.xmax, tmpBounds.ymin);
		shape.setPoint(4, tmpBounds.xmax, tmpBounds.ymax);
		shape.setPoint(5, tmpBounds.xmax, tmpBounds.ymax);

		if (label != null)
			{
			MiDistance hInset = getFont().getAverageCharWidth();
			label.setXmin(getXmin() + hInset);
			label.setCenterY(getYmax() - getMargins(tmpMargins).getHeight());
			shape.setPoint(0, getXmin() + hInset/2, shape.getPoint(0).y);
			shape.setPoint(-1, label.getXmax() + hInset/2, shape.getPoint(-1).y);
			super.render(renderer);
			}
		else
			{
			shape.setPoint(0, getXmin(), shape.getPoint(0).y);
			shape.setPoint(-1, getXmin(), shape.getPoint(-1).y);
			super.render(renderer);
			}
		MiBounds.freeBounds(tmpBounds);
		}
					/**------------------------------------------------------
	 				 * Calulates the minimum size of this part.
	 				 * @param size  	the (returned) minimum size
					 *------------------------------------------------------*/
	protected 	void		calcMinimumSize(MiSize size)
		{
		calcPreferredSize(size);
		}
					/**------------------------------------------------------
	 				 * Calulates the preferred size of this part.
	 				 * @param size  	the (returned) preferred size
					 *------------------------------------------------------*/
	protected 	void		calcPreferredSize(MiSize size)
		{
		super.calcPreferredSize(size);
		if (label != null)
			{
			MiBounds lBounds = label.getBounds(tmpBounds);
			MiDistance hInset = getFont().getAverageCharWidth();
			if (getNumberOfParts() == 0)
				{
				size.setWidth(0);
				size.setHeight(lBounds.getWidth() + 2 * hInset);
				}
			if (size.getWidth() < lBounds.getWidth() + 2 * hInset)
				size.setWidth(lBounds.getWidth() + 2 * hInset);
			if (size.getHeight() < lBounds.getHeight())
				size.setHeight(lBounds.getHeight());
			}
		}
					/**------------------------------------------------------
	 				 * Calulates the bounds of this part.
	 				 * @param bounds  	the (returned) bounds
					 *------------------------------------------------------*/
	protected	void		reCalcBounds(MiBounds bounds)
		{
		super.reCalcBounds(bounds);
		if (label != null)
			{
			MiBounds lBounds = label.getBounds(tmpBounds);
			MiDistance hInset = getFont().getAverageCharWidth();
			if (bounds.getWidth() < lBounds.getWidth())
				bounds.xmax = bounds.xmin + lBounds.getWidth() + 2 * hInset;
			if (bounds.getHeight() < lBounds.getHeight())
				bounds.ymin = bounds.ymax - lBounds.getHeight();
			}
		}
					/**------------------------------------------------------
					 * Sets the margins of this MiPart.
					 * @param m		the margins or null
					 * @overrides 		MiPart#setMargins
					 *------------------------------------------------------*/
	public		void		setMargins(MiMargins m)
		{
		preferredMargins = new MiMargins(m);
		MiDistance adjustment = calcMarginAdjustments();
		m.top += adjustment;
		super.setMargins(m);
		}
					/**------------------------------------------------------
	 				 * Sets the margins of empty space immediately inside of 
					 * the target's (inner) bounds.
	 				 * @param m  		the 'inside' margins of the target 
					 * @see			MiPart#setMargins
					 *------------------------------------------------------*/
	public		void		setInsetMargins(MiMargins m) 
		{
		preferredInsetMargins = new MiMargins(m);
		MiDistance adjustment = calcMarginAdjustments();
		m.addMargins(adjustment);
		super.setInsetMargins(m);
		}

	protected	MiDistance 	calcMarginAdjustments()
		{
		if (label == null)
			return(0);
		return(label.getHeight()/2);
		}
	}

