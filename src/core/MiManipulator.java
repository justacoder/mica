
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public abstract class MiManipulator extends MiPart implements MiiManipulator, MiiCommandNames
	{
	private static 	int		orthoMovementEventModifiers = 0;
	//private static 	MiDistance	HANDLE_PAD_DEVICE_WIDTH	= 8;
	private		MiIDragger	dragger;


	public				MiManipulator()
		{
		dragger = new MiIDragger();
		dragger.setOrthogonalMovementEventModifiers(orthoMovementEventModifiers);
		insertEventHandler(dragger, 0);
		setResource(MiiSelectionGraphics.SELECTION_GRAPHICS_GRAPHICS, MiiSelectionGraphics.SELECTION_GRAPHICS_GRAPHICS);
		}
	public static	void		setOrthogonalMovementEventModifiers(int modifiers)
		{
		orthoMovementEventModifiers = modifiers;
		}
	public static	int		getOrthogonalMovementEventModifiers()
		{
		return(orthoMovementEventModifiers);
		}
/***
	public static	void		setHandlePadDeviceSize(MiDistance size)
		{
		HANDLE_PAD_DEVICE_WIDTH = size;
		}
	public static	MiDistance	getHandlePadDeviceSize()
		{
		return(HANDLE_PAD_DEVICE_WIDTH);
		}
***/
	}


