
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
public class MiJumpPan extends MiCommandHandler
	{
	public static final String	QuarterLeftCommand		= "quarterLeft";
	public static final String	QuarterRightCommand		= "quarterRight";
	public static final String	QuarterUpCommand		= "quarterUp";
	public static final String	QuarterDownCommand		= "quarterDown";
	public static final String	LeftCommand			= "left";
	public static final String	RightCommand			= "right";
	public static final String	UpCommand			= "top";
	public static final String	DownCommand			= "bottom";
	public static final String	QuarterUpperLeftCommand	= "quarterUpperLeft";
	public static final String	QuarterLowerLeftCommand	= "quarterLowerLeft";
	public static final String	QuarterUpperRightCommand	= "quarterUpperRight";
	public static final String	QuarterLowerRightCommand	= "quarterLowerRight";
	public static final String	UpperLeftCommand		= "upperLeft";
	public static final String	LowerLeftCommand		= "lowerLeft";
	public static final String	UpperRightCommand		= "upperRight";
	public static final String	LowerRightCommand		= "lowerRight";
	public static final String	LeftSideOfUniverseCommand	= "leftSideOfUniverse";
	public static final String	RightSideOfUniverseCommand	= "rightSideOfUniverse";
	public static final String	TopSideOfUniverseCommand	= "topSideOfUniverse";
	public static final String	BottomSideOfUniverseCommand	= "bottomSideOfUniverse";

	private		MiBounds	originalWorld			= new MiBounds();


	public				MiJumpPan()
		{
		}

	public		void		processCommand(String arg)
		{
		MiEditor	editor = (MiEditor )getTargetOfCommand();
		editor.getWorldBounds(originalWorld);
		MiBounds	world = editor.getWorldBounds();
		MiBounds	univ = editor.getUniverseBounds();

		MiDistance width = world.getWidth();
		MiDistance height = world.getHeight();

		if (arg.equalsIgnoreCase(LeftCommand))
			world.translate(-width, 0);
		else if (arg.equalsIgnoreCase(RightCommand))
			world.translate(width, 0);
		else if (arg.equalsIgnoreCase(UpCommand))
			world.translate(0, height);
		else if (arg.equalsIgnoreCase(DownCommand))
			world.translate(0, -height);
	
		else if (arg.equalsIgnoreCase(QuarterLeftCommand))
			world.translate(-width/4, 0);
		else if (arg.equalsIgnoreCase(QuarterRightCommand))
			world.translate(width/4, 0);
		else if (arg.equalsIgnoreCase(QuarterUpCommand))
			world.translate(0, height/4);
		else if (arg.equalsIgnoreCase(QuarterDownCommand))
			world.translate(0, -height/4);

		else if (arg.equalsIgnoreCase(QuarterUpperLeftCommand))
			world.translate(-width/4, height/4);
		else if (arg.equalsIgnoreCase(QuarterUpperRightCommand))
			world.translate(width/4, height/4);
		else if (arg.equalsIgnoreCase(QuarterLowerLeftCommand))
			world.translate(-width/4, -height/4);
		else if (arg.equalsIgnoreCase(QuarterLowerRightCommand))
			world.translate(width/4, -height/4);

		else if (arg.equalsIgnoreCase(UpperLeftCommand))
			world.translate(-width, height);
		else if (arg.equalsIgnoreCase(UpperRightCommand))
			world.translate(width, height);
		else if (arg.equalsIgnoreCase(LowerLeftCommand))
			world.translate(-width, -height);
		else if (arg.equalsIgnoreCase(LowerRightCommand))
			world.translate(width, -height);

		else if (arg.equalsIgnoreCase(LeftSideOfUniverseCommand))
			world.translate(univ.xmin - world.xmin, 0);
		else if (arg.equalsIgnoreCase(RightSideOfUniverseCommand))
			world.translate(univ.xmax - world.xmax, 0);
		else if (arg.equalsIgnoreCase(TopSideOfUniverseCommand))
			world.translate(0, univ.ymax - world.ymax);
		else if (arg.equalsIgnoreCase(BottomSideOfUniverseCommand))
			world.translate(0, univ.ymin - world.ymin);

		editor.setWorldBounds(world);
		MiSystem.getViewportTransactionManager().appendTransaction(
			new MiPanAndZoomCommand(editor, originalWorld, editor.getWorldBounds()));
		}
	}

