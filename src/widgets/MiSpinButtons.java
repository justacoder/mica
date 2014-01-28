
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSpinButtons extends MiWidget implements MiiActionHandler
	{
	public static final	String	Mi_SPINBUTTONS_PROTOTYPE_CLASS_NAME = "Mi_SPINBUTTONS_PROTOTYPE_CLASS_NAME";
	private static	MiSpinButtons	prototype;
	private static 	int 		DECORATION_WIDTH	= 10;
	private static	int 		DECORATION_HEIGHT	= 5;
	private		MiPushButton	lessButton;
	private		MiPushButton	moreButton;
	private		int		buttonOrientation;
	private		int		arrowOrientation;



	public				MiSpinButtons()
		{
		this(Mi_HORIZONTAL, Mi_HORIZONTAL);
		}
	public				MiSpinButtons(int buttonOrientation, int arrowOrientation)
		{
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiSpinButtons");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}
		this.buttonOrientation = buttonOrientation;
		this.arrowOrientation = arrowOrientation;

		setInsetMargins(1);

		MiRowColBaseLayout layout;
		if (buttonOrientation == Mi_HORIZONTAL)
			{
			layout = new MiRowLayout();
			layout.setElementVSizing(Mi_EXPAND_TO_FILL);
			}
		else
			{
			layout = new MiColumnLayout();
			layout.setElementHSizing(Mi_EXPAND_TO_FILL);
			}

		layout.setAlleyHSpacing(0);
		layout.setAlleyVSpacing(0);

		moreButton = new MiPushButton();
		lessButton = new MiPushButton();
		moreButton.setRepeatSelectWhenHeld(true);
		lessButton.setRepeatSelectWhenHeld(true);
		moreButton.setDisplaysFocusBorder(false);
		lessButton.setDisplaysFocusBorder(false);
		moreButton.setName("moreButton");
		lessButton.setName("lessButton");
		//moreButton.setMargins(new MiMargins(0));
		//lessButton.setMargins(new MiMargins(0));
		if (arrowOrientation == Mi_HORIZONTAL)
			{
			appendPart(lessButton);
			appendPart(moreButton);
			}
		else
			{
			appendPart(moreButton);
			appendPart(lessButton);
			}
		setLayout(layout);

		if (arrowOrientation == Mi_HORIZONTAL)
			{
			moreButton.setLabel(makeArrowDecoration(Mi_RIGHT));
			lessButton.setLabel(makeArrowDecoration(Mi_LEFT));
			}
		else
			{
			moreButton.setLabel(makeArrowDecoration(Mi_UP));
			lessButton.setLabel(makeArrowDecoration(Mi_DOWN));
			}
		lessButton.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		moreButton.appendActionHandler(this, Mi_ACTIVATED_ACTION);

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
	public static	MiWidget	create()
		{
		if (prototype == null)
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}
	public		MiPushButton	getLessButton()
		{	
		return(lessButton);
		}
	public		MiPushButton	getMoreButton()
		{	
		return(moreButton);
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
	public static	void		setPrototype(MiSpinButtons p)
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
	public static	MiWidget	getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_SPINBUTTONS_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_SPINBUTTONS_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiSpinButtons )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == lessButton)
			dispatchAction(Mi_DECREASE_ACTION);
		else
			dispatchAction(Mi_INCREASE_ACTION);

		return(true);
		}

	protected	MiPart		makeArrowDecoration(int direction)
		{
		MiTriangle decoration = new MiTriangle();
		decoration.setBackgroundColor(MiColorManager.gray);
		decoration.setOrientation(direction);
		if ((direction == Mi_UP || direction == Mi_DOWN))
			{
			decoration.setWidth(DECORATION_WIDTH);
			decoration.setHeight(DECORATION_HEIGHT);
			}
		else
			{
			decoration.setWidth(DECORATION_HEIGHT);
			decoration.setHeight(DECORATION_WIDTH);
			}
		return(decoration);
		}
	}





