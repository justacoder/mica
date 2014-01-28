
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
public class MiPanAndZoomAnimator implements MiiAnimatable
	{
	private		MiEditor	editor;
	private		MiAnimator	animator;
	private		MiBounds	world		= new MiBounds();
	private		MiBounds	newWorld	= new MiBounds();
	private		MiBounds	originalWorld	= new MiBounds();


	public				MiPanAndZoomAnimator(MiEditor editor)
		{
		this.editor = editor;

		animator = new MiAnimator(this, 100, 1000);
		animator.setEnabled(false);
		}

	public		void		setWorld(MiBounds world)
		{
		newWorld.copy(world);
		editor.getWorldBounds(originalWorld);
		if (!animator.getEnabled())
			MiAnimationManager.addAnimator(editor, animator);
		MiAnimationManager.getAnimationManager(editor).resetTimer(animator);
		animator.setEnabled(true);
		}
					/**------------------------------------------------------
	 				 * Initialize before animation.
					 * @implements		MiiAnimatable#start
					 *------------------------------------------------------*/
	public		void		start()
		{
		}
					/**------------------------------------------------------
					 * Animates for the given time slice. The beginning and
					 * end are normalized (0.0 to 1.0), 0.0 being the start
					 * of the animation and 1.0 the end. This method ignores
					 * the start and end.
					 * @param startOfStep	the beginning of the time slice
					 * @param endOfStep	the end of the time slice
					 * @implements		MiiAnimatable#animate
					 *------------------------------------------------------*/
	public		void		animate(double startOfStep, double endOfStep)
		{
		world.setXmin((newWorld.getXmin() - originalWorld.getXmin()) * endOfStep + originalWorld.getXmin());
		world.setYmin((newWorld.getYmin() - originalWorld.getYmin()) * endOfStep + originalWorld.getYmin());
		world.setXmax((newWorld.getXmax() - originalWorld.getXmax()) * endOfStep + originalWorld.getXmax());
		world.setYmax((newWorld.getYmax() - originalWorld.getYmax()) * endOfStep + originalWorld.getYmax());
		editor.getViewport().confineWorldToUniverse(world);
		editor.setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Clean up after animation. This animator is automatically
					 * removed from the MiAnimationManager.
					 * @implements		MiiAnimatable#end
					 *------------------------------------------------------*/
	public		void		end()
		{
		editor.getViewport().confineWorldToUniverse(newWorld);
		editor.setWorldBounds(newWorld);
		animator.setEnabled(false);
		MiSystem.getViewportTransactionManager().appendTransaction(
			new MiPanAndZoomCommand(editor, originalWorld, newWorld));
		}
	}

