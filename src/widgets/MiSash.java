
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
 * This class draws a sash.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiSash extends MiWidget implements MiiDraggable
	{
	public static final	String		Mi_SASH_PROTOTYPE_CLASS_NAME = "Mi_SASH_PROTOTYPE_CLASS_NAME";
	public static final	String		Mi_STANDARD_SASH_STYLE	= "standard";
	public static final	String		Mi_INVISIBLE_SASH_STYLE	= "invisible";

	private static		MiSash		prototype;
	private			MiPart		firstPart;
	private			MiPart		secondPart;
//	private			MiSize		firstPreferredSize;
//	private			MiSize		secondPreferredSize;
	private			int		orientation = Mi_NONE;
	private			int		sashPreferredWidth = 10;
	private			boolean		animate;
	private			boolean		maximizeMaximizesFirstPart	= true;
	private			boolean		maximized;
	private			String		value;
	private			MiPart		overallContainer;
	private			MiPart		visuals;
	private	static		MiBounds	tmpBounds			= new MiBounds();



					/**------------------------------------------------------
	 				 * Constructs a new MiSash without a label. 
					 *------------------------------------------------------*/
	public				MiSash()
		{
		this(null, null, Mi_NONE, Mi_STANDARD_SASH_STYLE);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiSash that controls the sizes of the 
					 * 2 parts it lies between in its container
					 * @param orientation	Mi_HORIZONTAL implies sash is horizontal,
					 *			Mi_NONE is determined by parent container's layout
					 *			Mi_VERTICAL implies sash is vertical
					 *------------------------------------------------------*/
	public				MiSash(int orientation)
		{
		this(null, null, orientation, Mi_STANDARD_SASH_STYLE);
		}
	public				MiSash(String style)
		{
		this(null, null, Mi_NONE, style);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiSash that controls the sizes of the 
					 * 2 given parts
					 * @param first		the part on one size
					 * @param second			the part on the other size
					 *------------------------------------------------------*/
	public				MiSash(MiPart firstPart, MiPart secondPart, int orientation)
		{
		this(firstPart, secondPart, orientation, Mi_STANDARD_SASH_STYLE);
		}
	public				MiSash(MiPart firstPart, MiPart secondPart, int orientation, String style)
		{
		this(null, firstPart, secondPart, orientation, style);
		}
	public				MiSash(MiPart overallContainer, MiPart firstPart, MiPart secondPart, int orientation, String style)
		{
		this.overallContainer = overallContainer;
		this.firstPart = firstPart;
		this.secondPart = secondPart;
		this.orientation = orientation;

		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiSash");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			}
		else
			{
			if (style.equals(Mi_STANDARD_SASH_STYLE))
				{
				setBorderLook(Mi_FLAT_BORDER_LOOK);
				setPreferredSize(new MiSize(sashPreferredWidth, sashPreferredWidth));
				setMinimumSize(new MiSize(sashPreferredWidth, sashPreferredWidth));
				setBackgroundColor(getNormalBackgroundColor());
				setBackgroundTile(new MiImage("images/bumps.xpm").getImage());
				setInsetMargins(new MiMargins(0));
				setMouseFocusBackgroundColor(MiColorManager.getColor("#94AAFD"));
				setAcceptingMouseFocus(true);
				setDisplaysFocusBorder(false);
				setMouseFocusAttributes(getMouseFocusAttributes().setHasBorderHilite(false));
				}
			else if (style.equals(Mi_INVISIBLE_SASH_STYLE))
				{
				setBorderLook(Mi_FLAT_BORDER_LOOK);
				setPreferredSize(new MiSize(sashPreferredWidth, sashPreferredWidth));
				setMinimumSize(new MiSize(sashPreferredWidth, sashPreferredWidth));
				setColor(Mi_TRANSPARENT_COLOR);
				setBackgroundColor(Mi_TRANSPARENT_COLOR);
				setInsetMargins(new MiMargins(0));
				setMouseFocusBackgroundColor(MiColorManager.getColor("#94AAFD"));
				setAcceptingMouseFocus(true);
				setDisplaysFocusBorder(false);
				setMouseFocusAttributes(getMouseFocusAttributes().setHasBorderHilite(false));
				}

			appendEventHandler(new MiIDragger());
			appendEventHandler(new MiSashMaximizeOrRestoreEventHandler());

			if (orientation == Mi_HORIZONTAL)
				{
				setContextCursor(Mi_N_RESIZE_CURSOR);
				}
			else
				{
				setContextCursor(Mi_E_RESIZE_CURSOR);
				}
			}

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
	public static	MiSash		create()
		{
		if (prototype == null)
			return(new MiSash());

		return((MiSash )prototype.deepCopy());
		}
	public	 	void		setSashPreferredWidth(int width)
		{
		sashPreferredWidth = width;
		setPreferredSize(new MiSize(sashPreferredWidth, sashPreferredWidth));
		setMinimumSize(new MiSize(sashPreferredWidth, sashPreferredWidth));
		}
	public	 	int		getSashPreferredWidth()
		{
		return(sashPreferredWidth);
		}
	public		void		setAnimateChanges(boolean flag)
		{
		animate = flag;
		}
	public		boolean		getAnimateChanges()
		{
		return(animate);
		}
	/**
	 * default = true
	 **/
	public		void		setMaximizeMaximizesFirstPart(boolean flag)
		{
		maximizeMaximizesFirstPart = flag;
		}
	public		boolean		getMaximizeMaximizesFirstPart()
		{
		return(maximizeMaximizesFirstPart);
		}

					/**------------------------------------------------------
	 				 * Set the value of this widget, which is the percentage
					 * visible, 0 is default, -1 is all of 'second' part visible,
					 * 1 is all of first part visible.
	 				 * @param value 	the new value
					 *------------------------------------------------------*/
	public		void		setValue(String value)
		{
		this.value = value;

		double percentageVisible = Utility.toDouble(value);

		if (percentageVisible > 1.0)
			percentageVisible = 1.0;
		if (percentageVisible < -1.0)
			percentageVisible = -1.0;

		init();


		secondPart.setPreferredSize(null);
		firstPart.setPreferredSize(null);

		MiSize firstPreferredSize = firstPart.getPreferredSize(new MiSize());
		MiSize secondPreferredSize = secondPart.getPreferredSize(new MiSize());

		MiSize firstSize = new MiSize(firstPreferredSize);
		MiSize secondSize = new MiSize(secondPreferredSize);

		MiSize firstMinSize = firstPart.getMinimumSize(new MiSize());
		MiSize secondMinSize = secondPart.getMinimumSize(new MiSize());
//MiDebug.println("firstPArt = " + firstPart);
//MiDebug.println("secondPart = " + secondPart);
//MiDebug.println("firstSize preferr = " + firstSize);
//MiDebug.println("secondSize preferr = " + secondSize);
		MiSize totalSize = null;
		if (orientation == Mi_HORIZONTAL)
			{
			totalSize = new MiSize(secondSize.width, secondSize.height + firstSize.height);
			}
		else
			{
			totalSize = new MiSize(secondSize.width + firstSize.width, secondSize.height);
			}
//MiDebug.println("percentageVisible = " + percentageVisible);
		if (orientation == Mi_HORIZONTAL)
			{
			if (percentageVisible <= 0)
				{
				firstSize.height = firstSize.height + firstSize.height * percentageVisible;
				secondSize.height = totalSize.height - firstSize.height;
				}
			else
				{
				secondSize.height = secondSize.height - secondSize.height * percentageVisible;
				firstSize.height = totalSize.height - secondSize.height;
				}
			}
		else
			{
			if (percentageVisible <= 0)
				{
				firstSize.width = firstSize.width + firstSize.width * percentageVisible;
				secondSize.width = totalSize.width - firstSize.width;
				}
			else
				{
				secondSize.width = secondSize.width - secondSize.width * percentageVisible;
				firstSize.width = totalSize.width - secondSize.width;
				}
			}

		firstPart.setPreferredSize(firstSize);
		secondPart.setPreferredSize(secondSize);
//MiDebug.println("NOW firstSize preferr = " + firstSize);
//MiDebug.println("NOW secondSize preferr = " + secondSize);
		getContainer(0).invalidateLayout();
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
		return(value);
		}
	public		void		init()
		{
		if (getNumberOfContainers() == 0)
			{
//MiDebug.println(this + " no container could be determined");
			return;
			}

//MiDebug.println("getContainer(0)=" + getContainer(0));
//MiDebug.println("getContainer(0).getLayout()=" + getContainer(0).getLayout());
		if (orientation == Mi_NONE)
			{
			if ((getContainer(0) instanceof MiRowLayout) || (getContainer(0).getLayout() instanceof MiRowLayout))
				{
				orientation = Mi_VERTICAL;
				setContextCursor(Mi_E_RESIZE_CURSOR);
				}
			else if ((getContainer(0) instanceof MiColumnLayout) || (getContainer(0).getLayout() instanceof MiColumnLayout))
				{
				orientation = Mi_HORIZONTAL;
				setContextCursor(Mi_N_RESIZE_CURSOR);
				}
			else
				{
//MiDebug.println(this + " no orientation could be determined");
				return;
				}
			}

		if (firstPart == null)
			{
			firstPart = getContainer(0).getPart(getContainer(0).getIndexOfPart(this) - 1);
//MiDebug.println(this + " setting first part= " + firstPart);
			}
		if (secondPart == null)
			{
			secondPart = getContainer(0).getPart(getContainer(0).getIndexOfPart(this) + 1);
			}

		if (overallContainer == null)
			{
			overallContainer = firstPart.getContainer(0);
			}
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiSash obj 		= (MiSash )source;
		value			= obj.value;
		animate			= obj.animate;
		maximizeMaximizesFirstPart = obj.maximizeMaximizesFirstPart;
		maximized		= obj.maximized;
		}
					/**------------------------------------------------------
	 				 * Return a textual description of this class.
	 				 * @return 	the description
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(super.toString() + "[firstPart = " + firstPart 
			+ ", secondPart = " + secondPart + ", maximized = " + maximized + ", value = " + getValue() + "]");
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
	public static	void		setPrototype(MiSash p)
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
	public static	MiSash		getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_SASH_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_SASH_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiSash )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	//***************************************************************************************
	// MiiDraggable methods
	//***************************************************************************************

	public		int 		pickup(MiEvent event)
		{
		init();
		if (!animate)
			{
			visuals = makeSimpleVisualAttachment();

			if (orientation == Mi_HORIZONTAL)
				{
				visuals.setCenterY(event.worldPt.y);
				visuals.setCenterX(getCenterX());
				}
			else
				{
				visuals.setCenterX(event.worldPt.x);
				visuals.setCenterY(getCenterY());
				}
			getRootWindow().appendAttachment(visuals);
			}
		return(Mi_CONSUME_EVENT);
		}
	public		int 		drag(MiEvent event)
		{
//MiDebug.println(this + "drag");

		if (animate)
			{
			MiSize firstSize = new MiSize();
			MiSize secondSize = new MiSize();
			getPreferredSizeOfFirstAndSecondPanes(event.worldPt, firstSize, secondSize);


//MiDebug.println(this + "firstSize=" + firstSize);
//MiDebug.println(this + "secondSize=" + secondSize);
			firstPart.setPreferredSize(firstSize);
			secondPart.setPreferredSize(secondSize);
			}
		else
			{
			if (orientation == Mi_HORIZONTAL)
				{
				visuals.setCenterY(event.worldPt.y);
				visuals.setCenterX(getCenterX());
				}
			else
				{
				visuals.setCenterX(event.worldPt.x);
				visuals.setCenterY(getCenterY());
				}
			}
	

		return(Mi_CONSUME_EVENT);
		}
	public		int	 	drop(MiEvent event)
		{
		if (!animate)
			{
			maximized = false;

			MiSize firstSize = new MiSize();
			MiSize secondSize = new MiSize();
			getPreferredSizeOfFirstAndSecondPanes(event.worldPt, firstSize, secondSize);

			MiSize firstMinSize = firstPart.getMinimumSize(new MiSize());
			MiSize secondMinSize = secondPart.getMinimumSize(new MiSize());
//MiDebug.println("first preferred Size=" + firstSize);
//MiDebug.println("second preferred Size=" + secondSize);
//MiDebug.println("firstMinSize=" + firstMinSize);
//MiDebug.println("secondMinSize=" + secondMinSize);
			if (orientation == Mi_HORIZONTAL)
				{
//MiDebug.println("orientation=Mi_HORIZONTAL");
				if (firstSize.getHeight() < firstMinSize.getHeight())
					{
//MiDebug.println("maximize second part");
					maximize(false);
					}
				else
					{
					firstPart.setVisible(true);
					}

				if (secondSize.getHeight() < secondMinSize.getHeight())
					{
//MiDebug.println("maximize first part");
					maximize(true);
					}
				else
					{
					secondPart.setVisible(true);
					}
				}
			else
				{
				if (firstSize.getWidth() < firstMinSize.getWidth())
					{
//MiDebug.println("orientation=Mi_VERTICAL");
//MiDebug.println("maximize second part");
					maximize(false);
					}
				else
					{
					firstPart.setVisible(true);
					}

				if (secondSize.getWidth() < secondMinSize.getWidth())
					{
//MiDebug.println("maximize first part");
					maximize(true);
					}
				else
					{
					secondPart.setVisible(true);
					}
				}

//MiDebug.println("firstPart = " + firstPart);
//MiDebug.println("secondPart = " + secondPart);
//MiDebug.println("firstMinSize = " + firstMinSize);
//MiDebug.println("secondMinSize = " + secondMinSize);
//MiDebug.println(this + "firstSize=" + firstSize);
//MiDebug.println(this + "secondSize=" + secondSize);
			if (!maximized)
				{
//MiDebug.println(this + "SET PREFERRED SIZES");
				firstPart.setPreferredSize(firstSize);
				secondPart.setPreferredSize(secondSize);
//MiDebug.println("secondPart part size" + secondPart.getSize(new MiSize()));
//MiDebug.println("NOW First part size" + firstPart.getSize(new MiSize()));
//MiDebug.println("secondPart part size" + secondPart.getSize(new MiSize()));
//MiDebug.println("secondPart.getPreferredSize() =" + secondPart.getPreferredSize(new MiSize()));
//MiDebug.println("firstPart.getPreferredSize() =" + firstPart.getPreferredSize(new MiSize()));
//MiDebug.println("secondPart.getMinimumSize() =" + secondPart.getMinimumSize(new MiSize()));
//MiDebug.dump(secondPart);
				}

			getRootWindow().removeAttachment(visuals);
			}
		return(Mi_CONSUME_EVENT);
		}
	public		void		toggleMaximizeRestore()
		{
		init();

		if (maximized)
			{
			secondPart.setPreferredSize(null);
			firstPart.setPreferredSize(null);

			MiSize firstPreferredSize = firstPart.getPreferredSize(new MiSize());
			MiSize secondPreferredSize = secondPart.getPreferredSize(new MiSize());

			if (orientation == Mi_HORIZONTAL)
				{
				firstPart.setHeight(firstPreferredSize.getHeight());
				secondPart.setHeight(secondPreferredSize.getHeight());
				}
			else
				{
				firstPart.setWidth(firstPreferredSize.getWidth());
				secondPart.setWidth(secondPreferredSize.getWidth());
				}
			firstPart.setVisible(true);
			secondPart.setVisible(true);
			maximized = false;
			}
		else
			{
			maximize(maximizeMaximizesFirstPart);
			}

		}
	public		void		maximize(boolean maximizeFirstPart)
		{
//MiDebug.printStackTrace("maximize fisrt part? " + maximizeFirstPart);

		// Erase previous preferrred size overrides that may have been set by an orthogonal sash
		firstPart.setPreferredSize(null);
		secondPart.setPreferredSize(null);

		MiSize firstPreferredSize = firstPart.getPreferredSize(new MiSize());
		MiSize secondPreferredSize = secondPart.getPreferredSize(new MiSize());

		MiSize firstMinSize = firstPart.getMinimumSize(new MiSize());
		MiSize secondMinSize = secondPart.getMinimumSize(new MiSize());

		MiSize marginsAndSpacings = calculateMarginsAndSpacings(overallContainer, maximizeFirstPart ? firstPart : secondPart);
		if (orientation == Mi_HORIZONTAL)
			{
			if (maximizeFirstPart)
				{
				firstPart.setVisible(true);
				firstPreferredSize.setHeight(
					overallContainer.getHeight() - sashPreferredWidth - marginsAndSpacings.getHeight());
				secondPart.setVisible(false);
				secondPreferredSize.setHeight(secondMinSize.getHeight());

				firstPart.setHeight(
					overallContainer.getHeight() - sashPreferredWidth - marginsAndSpacings.getHeight());
				secondPart.setHeight(secondMinSize.getHeight());
				}
			else
				{
				secondPart.setVisible(true);
				secondPreferredSize.setHeight(
					overallContainer.getHeight() - sashPreferredWidth - marginsAndSpacings.getHeight());
				firstPart.setVisible(false);
				firstPreferredSize.setHeight(firstMinSize.getHeight());

				firstPart.setHeight(firstMinSize.getHeight());
				secondPart.setHeight(
					overallContainer.getHeight() - sashPreferredWidth - marginsAndSpacings.getHeight());
				}
			}
		else
			{
			if (maximizeFirstPart)
				{
				firstPart.setVisible(true);
				firstPreferredSize.setWidth(
					overallContainer.getWidth() - sashPreferredWidth - marginsAndSpacings.getWidth());
				secondPart.setVisible(false);
				secondPreferredSize.setWidth(secondMinSize.getWidth());

				firstPart.setWidth(
					overallContainer.getWidth() - sashPreferredWidth - marginsAndSpacings.getWidth());
				secondPart.setWidth(secondMinSize.getWidth());
				}
			else
				{
				secondPart.setVisible(true);
				secondPreferredSize.setWidth(
					overallContainer.getWidth() - sashPreferredWidth - marginsAndSpacings.getWidth());
//MiDebug.println("overallContainer=" + overallContainer);
//MiDebug.println("overallContainer.getWidth()=" + overallContainer.getWidth());
				firstPart.setVisible(false);
				firstPreferredSize.setWidth(firstMinSize.getWidth());

				firstPart.setWidth(firstMinSize.getWidth());
				secondPart.setWidth(
					overallContainer.getWidth() - sashPreferredWidth - marginsAndSpacings.getWidth());
				}
			}
		firstPart.setPreferredSize(firstPreferredSize);
		secondPart.setPreferredSize(secondPreferredSize);

		maximized = true;
		}

		
	//***************************************************************************************
	// Protected methods
	//***************************************************************************************
	protected	MiSize		calculateMarginsAndSpacings(MiPart container, MiPart part)
		{
		MiSize marginsAndSpacings = new MiSize();
		if (container instanceof MiLayout)
			{
			marginsAndSpacings.setWidth(((MiLayout )container).getAlleyHSpacing());
			marginsAndSpacings.setWidth(((MiLayout )container).getAlleyVSpacing());
			
			marginsAndSpacings.addMargins(((MiLayout )container).getInsetMargins());
			marginsAndSpacings.addMargins(((MiLayout )container).getCellMargins());
			marginsAndSpacings.addMargins(((MiLayout )container).getAlleyMargins());
			}

		if (container.getLayout() instanceof MiLayout)
			{
			marginsAndSpacings.setWidth(((MiLayout )container.getLayout()).getAlleyHSpacing());
			marginsAndSpacings.setWidth(((MiLayout )container.getLayout()).getAlleyVSpacing());
			
			marginsAndSpacings.addMargins(((MiLayout )container.getLayout()).getInsetMargins());
			marginsAndSpacings.addMargins(((MiLayout )container.getLayout()).getCellMargins());
			marginsAndSpacings.addMargins(((MiLayout )container.getLayout()).getAlleyMargins());
			}
		marginsAndSpacings.addMargins(part.getMargins(new MiMargins()));

		return(marginsAndSpacings);
		}

	protected	void		getPreferredSizeOfFirstAndSecondPanes(MiPoint pt, MiSize firstSize, MiSize secondSize)
		{
		// Erase previous preferrred size overrides that may have been set by an orthogonal sash
		firstPart.setPreferredSize(null);
		secondPart.setPreferredSize(null);

		MiSize firstPreferredSize = firstPart.getPreferredSize(new MiSize());
		MiSize secondPreferredSize = secondPart.getPreferredSize(new MiSize());

		MiSize firstMinSize = firstPart.getMinimumSize(new MiSize());
		MiSize secondMinSize = secondPart.getMinimumSize(new MiSize());
		if (orientation == Mi_HORIZONTAL)
			{
			firstSize.setSize(firstPreferredSize.getWidth(), firstPart.getYmax() - pt.y - sashPreferredWidth );
			secondSize.setSize(secondPreferredSize.getWidth(), pt.y - secondPart.getYmin() - sashPreferredWidth );
			}
		else
			{
			//firstSize.setSize(pt.x - firstPart.getXmin() - sashPreferredWidth, firstPart.getHeight());
			secondSize.setSize(secondPart.getXmax() - pt.x - sashPreferredWidth, secondPreferredSize.getHeight());
			firstSize.setSize(pt.x - firstPart.getXmin() - sashPreferredWidth, firstPreferredSize.getHeight());
			}

		firstSize.width = Math.max(firstSize.width, 1);
		firstSize.height = Math.max(firstSize.height, 1);
		secondSize.width = Math.max(secondSize.width, 1);
		secondSize.height = Math.max(secondSize.height, 1);
		}
	protected	MiPart		makeSimpleVisualAttachment()
		{
		MiPart rect = new MiRectangle(getBounds());
		rect.setBackgroundColor(getBackgroundColor());
		rect.setBorderLook(Mi_FLAT_BORDER_LOOK);
		if (getMouseFocusBackgroundColor() != null)
			{
			rect.setBackgroundColor(getMouseFocusBackgroundColor());
			}
		rect.setBackgroundTile(new MiImage("images/bumps.xpm").getImage());
		return(rect);
		}
	}
class MiSashMaximizeOrRestoreEventHandler extends MiEventHandler
	{
	public				MiSashMaximizeOrRestoreEventHandler()
		{
		addEventToCommandTranslation(
			Mi_EXECUTE_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		}

	public		int		processCommand()
		{
		((MiSash )getTarget()).toggleMaximizeRestore();
		return(Mi_CONSUME_EVENT);
		}
	}


