
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
public interface MiiSelectionManager
	{
				/**------------------------------------------------------
				 * Sets the class that will handle the change in appearance
				 * of selected items.
				 * @param graphics	the class that will decide how to
				 *			display selected items
				 *------------------------------------------------------*/
	void			setSelectionGraphics(MiiSelectionGraphics graphics);

				/**------------------------------------------------------
			 	 * Gets the class that will handle the change in appearance
				 * of selected items.
			 	 * @return 		the class that will decide how to
                                 *			display selected items
			 	 *------------------------------------------------------*/
	MiiSelectionGraphics	getSelectionGraphics();

				/**------------------------------------------------------
			 	 * Selects all the items in the given editor.
				 * @return		true if something new was selected
			 	 *------------------------------------------------------*/
	boolean			selectAll();

				/**------------------------------------------------------
			 	 * Deselects all the items in the given editor.
				 * @return		true if something was selected
			 	 *------------------------------------------------------*/
	boolean			deSelectAll();

				/**------------------------------------------------------
			 	 * Selects the given item.
				 * @param part		the item to select
			 	 *------------------------------------------------------*/
	void			selectObject(MiPart part);

				/**------------------------------------------------------
			 	 * Selects the given item. No other items are deselected.
				 * @param part		the item to select
			 	 *------------------------------------------------------*/
	void			selectAdditionalObject(MiPart part);

				/**------------------------------------------------------
			 	 * Deselects the given item.
				 * @param part		the item to deselect
			 	 *------------------------------------------------------*/
	void			deSelectObject(MiPart part);

				/**------------------------------------------------------
		 	 	 * Selects all the items in the given list.
			 	 * @return		true if something new was selected
			 	 * @param parts		the items to select
				 * @implements		MiiSelectionManager
		 	 	 *------------------------------------------------------*/
	boolean			select(MiParts parts);
	}

