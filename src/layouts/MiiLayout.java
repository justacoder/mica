
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
public interface MiiLayout extends MiiCopyable
	{
				/**------------------------------------------------------
	 			 * Sets whether enabled or not.
				 * @param flag		true if enabled
				 *------------------------------------------------------*/
	void			setEnabled(boolean flag);

				/**------------------------------------------------------
	 			 * Gets whether enabled or not.
				 * @return 		true if enabled
				 *------------------------------------------------------*/
	boolean			isEnabled();

				/**------------------------------------------------------
	 			 * Sets the target MiPart this layout is assigned to.
				 * @param target	the MiPart this is to layout.
				 *------------------------------------------------------*/
	void			setTarget(MiPart part);

				/**------------------------------------------------------
	 			 * Gets the target MiPart this layout is assigned to.
				 * @return 		the MiPart this is to layout.
				 *------------------------------------------------------*/
	MiPart			getTarget();

				/**------------------------------------------------------
	 			 * Lays out (arranges and resizes) the parts of the target.
				 *------------------------------------------------------*/
	void			layoutParts();

				/**------------------------------------------------------
	 			 * Specifies that this layout is no longer valid and needs
				 * to be laid out again.
				 *------------------------------------------------------*/
	void			invalidateLayout();

				/**------------------------------------------------------
	 			 * Gets the preferred size of the target, as calculated
				 * by this layout.
				 * @param size		the (returned) preferred size
				 * @return 		the preferred size
				 *------------------------------------------------------*/
	MiSize			getPreferredSize(MiSize size);

				/**------------------------------------------------------
	 			 * Gets the minimum size of the target, as calculated
				 * by this layout.
				 * @param size		the (returned) minimum size
				 * @return 		the minimum size
				 *------------------------------------------------------*/
	MiSize			getMinimumSize(MiSize size);

				/**------------------------------------------------------
	 			 * Gets whether this layout knows how to calculate the 
				 * preferred and minimum sizes of the target. If not,
				 * then the getPreferredSize and getMinimumSize methods
				 * do not do anything. Most layouts return true.
				 * @return		true if this layout knows how to
				 *			calculate the referred and minimum 
				 *			sizes of the target
				 *------------------------------------------------------*/
	boolean			determinesPreferredAndMinimumSizes();

				/**------------------------------------------------------
	 			 * Gets whether this layout is not dependent of the position
				 * of the target. Most layouts return true. Layouts that
				 * support relative contraints (e.g. MiPolyConstraint)
				 * return false because of their dependance on their target 
				 * relative to another.
				 * @return 		true if not dependent
				 *------------------------------------------------------*/
	boolean			isIndependantOfTargetPosition();
	}

