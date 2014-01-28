
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
 * This class implements the MiiSelectionGraphics interface. As
 * such it is responsible for the making the appearance of a
 * graphics part when the part is 'selected' different from when
 * it is 'not selected'.
 * <p>
 * The implementation of this interface is usually adds some
 * kind of visual apparatus to a selected part. Examples of
 * this are a simple box around the part or handles around 
 * the part that allow the user to manipulate the size of the 
 * part. This interface is used primarily by the selection
 * manager.
 *
 * @see		MiSelectionManager
 * @see		MiBoxSelectionGraphics
 * @see		MiManipulatorSelectionGraphics
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiSelectionGraphics
	{
						/**------------------------------------------------------
				 		 * This is a resource that will be non-zero for all graphics
						 * used to highlight a selected item. This can be used when
						 * when applying attributes to the selected item to avoid
						 * applying the attributes to the selection graphics itself.
						 *------------------------------------------------------*/
	public		String			SELECTION_GRAPHICS_GRAPHICS = "MiiSelectionGraphics_GRAPHICS";

						/**------------------------------------------------------
				 		 * This is a globally accessable instance that can be 
						 * used to indicate that a particular part is to not be
						 * changed by the MiiSelectionManager when selected or
						 * deselected. It is applied by assigned it to the part
						 * parts using their setSelectionGraphics method. 
						 * @see			MiiSelectionManager
						 * @see			MiSelectionManager
						 * @see			MiPart#setSelectionGraphics 
						 *------------------------------------------------------*/
	public		MiiSelectionGraphics 	ignoreSelectionGraphics = new MiIgnoreSelectionGraphics();

						/**------------------------------------------------------
				 		 * This method tells this selection graphics appearance
						 * manager whether the given part in the given editor is
						 * selected. The implementation of this interface will
						 * then take appropriate actions (for example to display
						 * or hide shape handles).
		 				 * @param editor 	the editor
				 		 * @param part 		the part whose selection state
						 *			has changed.
		 				 * @param flag 		true if the part is now selected.
						 *------------------------------------------------------*/
	public		void			select(MiEditor editor, MiPart part, boolean flag);
	}



/**----------------------------------------------------------------------------------------------
 * This class implements MiiSelectionGraphics by doing nothing.
 *----------------------------------------------------------------------------------------------*/
class MiIgnoreSelectionGraphics implements MiiSelectionGraphics
	{
					/**------------------------------------------------------
	 		 		 * This method implements the select method by doing 	
					 * nothing.
					 * @implements		MiiSelectionGraphics
			 		 *------------------------------------------------------*/
	public		void		select(MiEditor editor, MiPart part, boolean flag)
		{
		}
	}
	

