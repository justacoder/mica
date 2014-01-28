
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
public class MiOkCancelHelpButtons extends MiWidget implements MiiActionHandler
	{
	public static final String		Mi_OK_CANCEL_HELP_BUTTONS_PROTOTYPE_CLASS_NAME 
							= "Mi_OK_CANCEL_HELP_BUTTONS_PROTOTYPE_CLASS_NAME";
	private static	MiOkCancelHelpButtons	prototype;
	private		MiButton		buttons[] = new MiButton[5];
	private		MiiCommandHandler	method;




	public				MiOkCancelHelpButtons(
						MiiCommandHandler callback,
						String btn1Label,
						String btn1Command,
						String btn2Label,
						String btn2Command,
						String btn3Label,
						String btn3Command)
		{
		this(callback,
			btn1Label,
			btn1Command,
			btn2Label,
			btn2Command,
			btn3Label,
			btn3Command,
			null,
			null,
			null,
			null
			);
		}

	public				MiOkCancelHelpButtons(
						MiiCommandHandler callback,
						String btn1Label,
						String btn1Command,
						String btn2Label,
						String btn2Command,
						String btn3Label,
						String btn3Command,
						String btn4Label,
						String btn4Command)
		{
		this(callback,
			btn1Label,
			btn1Command,
			btn2Label,
			btn2Command,
			btn3Label,
			btn3Command,
			btn4Label,
			btn4Command,
			null,
			null);
		}
	public				MiOkCancelHelpButtons(
						MiiCommandHandler callback,
						String btn1Label,
						String btn1Command,
						String btn2Label,
						String btn2Command,
						String btn3Label,
						String btn3Command,
						String btn4Label,
						String btn4Command,
						String btn5Label,
						String btn5Command)
		{
		setNormalBackgroundColor(MiColorManager.transparent);
		method = callback;
		MiRowLayout row = new MiRowLayout();
		row.setElementHJustification(Mi_JUSTIFIED);
		row.setElementHSizing(Mi_SAME_SIZE);
		setLayout(row);

		if ((btn1Label!= null) && (btn1Label.length() != 0))
			{
			buttons[0] = new MiPushButton(btn1Label);
			buttons[0].setInsetMargins(new MiMargins(14, 4, 14, 4));
			buttons[0].setName(btn1Command);
			buttons[0].appendActionHandler(this, Mi_ACTIVATED_ACTION);
			appendPart(buttons[0]);
			}
		if ((btn2Label!= null) && (btn2Label.length() != 0))
			{
			buttons[1] = new MiPushButton(btn2Label);
			buttons[1].setInsetMargins(new MiMargins(14, 4, 14, 4));
			buttons[1].setName(btn2Command);
			buttons[1].appendActionHandler(this, Mi_ACTIVATED_ACTION);
			appendPart(buttons[1]);
			}
		if ((btn3Label!= null) && (btn3Label.length() != 0))
			{
			buttons[2] = new MiPushButton(btn3Label);
			buttons[2].setInsetMargins(new MiMargins(14, 4, 14, 4));
			buttons[2].setName(btn3Command);
			buttons[2].appendActionHandler(this, Mi_ACTIVATED_ACTION);
			appendPart(buttons[2]);
			}
		if ((btn4Label != null) && (btn4Label.length() != 0))
			{
			buttons[3] = new MiPushButton(btn4Label);
			buttons[3].setInsetMargins(new MiMargins(14, 4, 14, 4));
			buttons[3].setName(btn4Command);
			buttons[3].appendActionHandler(this, Mi_ACTIVATED_ACTION);
			appendPart(buttons[3]);
			}
		if ((btn5Label != null) && (btn5Label.length() != 0))
			{
			buttons[4] = new MiPushButton(btn5Label);
			buttons[4].setInsetMargins(new MiMargins(14, 4, 14, 4));
			buttons[4].setName(btn5Command);
			buttons[4].appendActionHandler(this, Mi_ACTIVATED_ACTION);
			appendPart(buttons[4]);
			}

		if (buttons[0] != null)
			buttons[0].setEnterKeyFocus(true);

		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiOkCancelHelpButtons");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
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
	public static	MiWidget	create()
		{
		if (prototype == null)
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}
	public		boolean		processAction(MiiAction event)
		{
		if (method != null)
			{
			method.processCommand(event.getActionSource().getName());
			}
		return(true);
		}
	public		MiButton	getButton(int index)
		{
		return(buttons[index]);
		}
	public		MiButton	getButton(String command)
		{
		for (int i = 0; i < buttons.length; ++i)
			{
			if ((buttons[i].getName() != null) && (buttons[i].getName().equals(command)))
				return(buttons[i]);
			}
		return(null);
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
	public static	void		setPrototype(MiOkCancelHelpButtons p)
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
					 * Mi_OK_CANCEL_HELP_BUTTONS_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_OK_CANCEL_HELP_BUTTONS_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiOkCancelHelpButtons )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}
	}


