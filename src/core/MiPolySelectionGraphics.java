
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
import java.util.Vector;

/**----------------------------------------------------------------------------------------------
 * This class implements the MiiSelectionGraphics interface. As
 * such it is responsible for the making the appearance of a
 * graphics part when the part is 'selected' different from when
 * it is 'not selected'.
 * <p>
 * This class implements the MiiSelectionGraphics interface.
 * This implementation provides a wrapper for 0 or more implementations
 * of MiiSelectionGraphics. This is useful if, for example, the
 * selected items are to change color AND have manipulation handles.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPolySelectionGraphics implements MiiSelectionGraphics, MiiTypes
	{
	private		Vector		selectionGraphics = new Vector();

	public				MiPolySelectionGraphics()
		{
		}
	public				MiPolySelectionGraphics(MiiSelectionGraphics one, MiiSelectionGraphics other)
		{
		selectionGraphics.addElement(one);
		selectionGraphics.addElement(other);
		}
	public		void		appendSelectionGraphics(MiiSelectionGraphics sg)
		{
		selectionGraphics.addElement(sg);
		}
					/**------------------------------------------------------
	 				 * This method tells this selection graphics appearance
					 * manager whether the given part in the given editor is
					 * selected. This implementation draws a rectangle around
					 * the selected part unless the part is a line, in which
					 * case the line is rendered with a change of the line
					 * borderLook attribute.
	 			 	 * @param editor 	the editor
	 				 * @param part 		the part whose selection state
					 *			has changed.
	 				 * @param flag 		true if the part is now selected.
	 				 * @implements 		MiiSelectionGraphics
					 *------------------------------------------------------*/
	public		void		select(MiEditor editor, MiPart obj, boolean flag)
		{
		for (int i = 0; i < selectionGraphics.size(); ++i)
			{
			((MiiSelectionGraphics )selectionGraphics.elementAt(i)).select(editor, obj, flag);
			}
		}
	}


