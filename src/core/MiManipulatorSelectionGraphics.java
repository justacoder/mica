
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
 * This class implements the MiiSelectionGraphics interface.
 * This implementation attaches a part-specific manipulator 
 * to the selected part. Usually this manipulator displays
 * handles which the user can use to resize a part.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiManipulatorSelectionGraphics implements MiiSelectionGraphics, MiiTypes
	{

					/**------------------------------------------------------
	 				 * Constructs a new MiManipulatorSelectionGraphics. 
					 *------------------------------------------------------*/
	public				MiManipulatorSelectionGraphics()
		{
		}

					/**------------------------------------------------------
	 				 * This method tells this selection graphics appearance
					 * manager whether the given part in the given editor is
					 * selected. This implementation attaches a part-specific
					 * manipulator to the selected part.
	 			 	 * @param editor 	the editor
	 				 * @param part 		the part whose selection state
					 *			has changed.
	 				 * @param flag 		true if the part is now selected.
	 				 * @implements 		MiiSelectionGraphics
					 *------------------------------------------------------*/
	public		void		select(MiEditor editor, MiPart part, boolean flag)
		{
		if (flag)
			{
			MiiManipulator manipulator = part.getManipulator();
			if (manipulator == null)
				manipulator = part.makeManipulator();
			if (manipulator != null)
				manipulator.attachToTarget("manipulator");
			}
		else
			{
			MiiManipulator manipulator = part.getManipulator();
			if (manipulator != null)
				manipulator.removeFromTarget();
			}
		}
	}

