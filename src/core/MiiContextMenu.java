

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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiContextMenu
	{
			/**------------------------------------------------------
			 * Sets whether to select the target of this menu when the
			 * menu is popped up.
			 * @param flag 		true if we are to select the shape
			 *			that this menu is targeting when
			 *			this menu is popped up 
			 *------------------------------------------------------*/
	void		setToSelectAttributedShape(boolean flag);


			/**------------------------------------------------------
			 * Gets whether to select the target of this menu when the
			 * menu is popped up.
			 * @return 		true if we are to select the shape
			 *			that this menu is targeting when
			 *			this menu is popped up 
			 *------------------------------------------------------*/
	boolean		getToSelectAttributedShape();


			/**------------------------------------------------------
			 * Pops up this menu with the given trigger object as the
			 * target.
			 * @param assocTriggerObj the target of this menu
			 * @param pt		the location to pop up this menu
			 *------------------------------------------------------*/
	void		popup(MiPart assocTriggerObj, MiPoint pt);


			/**------------------------------------------------------
			 * Pops down this menu.
			 *------------------------------------------------------*/
	void		popdown();


			/**------------------------------------------------------
			 * Gets the menu this MiiContextMenu uses.
			 * @return 		the menu
			 *------------------------------------------------------*/
	MiPart		getMenuGraphics();
	}

