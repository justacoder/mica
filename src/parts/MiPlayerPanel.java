
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
import java.awt.Color; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPlayerPanel extends MiWidget implements MiiActionHandler, MiiSimpleAnimator
	{
	public static final String	Mi_PLAYER_PANEL_ACTION_NAME	= "playerPanelStateChange";
	public static final int		Mi_PLAYER_PANEL_ACTION		= MiActionManager.registerAction(
										Mi_PLAYER_PANEL_ACTION_NAME);

	public final static String	PREVIOUS_TRACK 		= "previousTrack";
	public final static String	FAST_REWIND 		= "fastRewind";
	public final static String	REWIND 			= "rewind";
	public final static String	PREVIOUS_FRAME		= "previousFrame";
	public final static String	STOP 			= "stop";
	public final static String	PAUSE 			= "pause";
	public final static String	NEXT_FRAME		= "nextFrame";
	public final static String	PLAY 			= "play";
	public final static String	FAST_FORWARD	 	= "fastForward";
	public final static String	NEXT_TRACK 		= "nextTrack";

	private		int	 	playerButtonWidth	= 30;
	private		int	 	playerButtonHeight 	= 20;
	private		String	 	currentState		= STOP;
	private		Color	 	buttonDetailColor	= MiColorManager.veryDarkWhite;


	public				MiPlayerPanel()
		{
		MiRowLayout layout = new MiRowLayout();
		layout.setElementSizing(Mi_SAME_SIZE);
		layout.setElementJustification(Mi_CENTER_JUSTIFIED);
		setLayout(layout);

		MiPushButton previousTrack = new MiPushButton(makePreviousTrackGr());
		previousTrack.setName(PREVIOUS_TRACK);
		previousTrack.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(previousTrack);

		MiPushButton fastRewind = new MiPushButton(makeFastRewindGr());
		fastRewind.setName(FAST_REWIND);
		fastRewind.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(fastRewind);

		MiPushButton rewind = new MiPushButton(makeRewindGr());
		rewind.setName(REWIND);
		rewind.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(rewind);

		MiPushButton prevFrame = new MiPushButton(makePrevFrameGr());
		prevFrame.setName(PREVIOUS_FRAME);
		prevFrame.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(prevFrame);

		MiPushButton stop = new MiPushButton(makeStopGr());
		stop.setName(STOP);
		stop.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(stop);

		MiPushButton pause = new MiPushButton(makePauseGr());
		pause.setName(PAUSE);
		pause.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(pause);

		MiPushButton nextFrame = new MiPushButton(makeNextFrameGr());
		nextFrame.setName(NEXT_FRAME);
		nextFrame.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(nextFrame);

		MiPushButton play = new MiPushButton(makePlayGr());
		play.setName(PLAY);
		play.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(play);

		MiPushButton fastForward = new MiPushButton(makeFastForwardGr());
		fastForward.setName(FAST_FORWARD);
		fastForward.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(fastForward);

		MiPushButton nextTrack = new MiPushButton(makeNextTrackGr());
		nextTrack.setName(NEXT_TRACK);
		nextTrack.appendActionHandler(this, Mi_SELECTED_ACTION);
		appendPart(nextTrack);

		setPlayerButtonSizes(playerButtonWidth, playerButtonHeight);
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public		MiPushButton	getButton(String name)
		{
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if (getPart(i).getName().equals(name))
				return((MiPushButton )getPart(i));
			}
		return(null);
		}

	public		String		getState()
		{
		return(currentState);
		}
	public		void		setState(String state)
		{
		endCurrentState();
		currentState = state;
		getButton(currentState).dispatchAction(Mi_ACTIVATED_ACTION);
		startCurrentState();
/*
		if (state.equals(DONE))
			{
			if (autoRewind...
			}
*/
		}
	public	void		setPlayerButtonSizes(MiDistance width, MiDistance height)
		{
		MiSize size = new MiSize(width, height);
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			getPart(i).getPart(0).refreshBounds();
			getPart(i).getPart(0).setSize(size);
			}
		}

	protected	void		endCurrentState()
		{
		if ((currentState.equals(PAUSE))
			|| (currentState.equals(FAST_FORWARD))
			|| (currentState.equals(FAST_REWIND)))
			{
			MiPart container = getButton(PAUSE).getPart(0);
			container.getPart(0).setBackgroundColor(buttonDetailColor);
			container.getPart(1).setBackgroundColor(buttonDetailColor);
			container = getButton(FAST_FORWARD).getPart(0);
			container.getPart(0).setBackgroundColor(buttonDetailColor);
			container.getPart(1).setBackgroundColor(buttonDetailColor);
			container = getButton(FAST_REWIND).getPart(0);
			container.getPart(0).setBackgroundColor(buttonDetailColor);
			container.getPart(1).setBackgroundColor(buttonDetailColor);
			MiAnimationManager.removeAnimator(this, this);
			}
		else if (currentState.equals(PLAY))
			{
			getButton(PLAY).getPart(0).getPart(0).setBackgroundColor(buttonDetailColor);
			}
		else if (currentState.equals(REWIND))
			{
			getButton(REWIND).getPart(0).getPart(0).setBackgroundColor(buttonDetailColor);
			}
		}
	protected	void		startCurrentState()
		{
		if ((currentState.equals(PAUSE))
			|| (currentState.equals(FAST_FORWARD))
			|| (currentState.equals(FAST_REWIND)))
			{
			MiAnimationManager.addAnimator(this, this);
			}
		else if (currentState.equals(PLAY)) 
			{
			getButton(PLAY).getPart(0).getPart(0).setBackgroundColor(MiColorManager.green);
			}
		else if (currentState.equals(REWIND))
			{
			getButton(REWIND).getPart(0).getPart(0).setBackgroundColor(MiColorManager.green);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		endCurrentState();
		currentState = action.getActionSource().getName();
		startCurrentState();
		dispatchAction(Mi_PLAYER_PANEL_ACTION);
		return(true);
		}
	public		long		animate()
		{
		if (currentState.equals(PAUSE))
			{
			MiPart container = getButton(PAUSE).getPart(0);
			if (container.getPart(0).getBackgroundColor() == MiColorManager.red)
				{
				container.getPart(0).setBackgroundColor(buttonDetailColor);
				container.getPart(1).setBackgroundColor(buttonDetailColor);
				}
			else
				{
				container.getPart(0).setBackgroundColor(MiColorManager.red);
				container.getPart(1).setBackgroundColor(MiColorManager.red);
				}
			return(500);
			}
		else if ((currentState.equals(FAST_FORWARD)) || (currentState.equals(FAST_REWIND)))
			{
			MiPart container;
			if (currentState.equals(FAST_FORWARD))
				container = getButton(FAST_FORWARD).getPart(0);
			else
				container = getButton(FAST_REWIND).getPart(0);
			if (container.getPart(0).getBackgroundColor() == MiColorManager.green)
				{
				container.getPart(0).setBackgroundColor(buttonDetailColor);
				container.getPart(1).setBackgroundColor(MiColorManager.green);
				}
			else // if (container.getPart(1).getBackgroundColor() == MiColorManager.green)
				{
				container.getPart(0).setBackgroundColor(MiColorManager.green);
				container.getPart(1).setBackgroundColor(buttonDetailColor);
				}
			return(250);
			}
		return(-1);
		}
		

	protected	MiPart		makePreviousTrackGr()
		{
		MiContainer container = new MiContainer();
		MiPart leftArrow = makeLeftArrow();
		MiPart rect = makeSkinnyRect();
		container.appendPart(rect);
		container.appendPart(leftArrow);
		rect.setXmax(leftArrow.getXmin());
		return(container);
		}
	protected	MiPart		makeFastRewindGr()
		{
		MiContainer container = new MiContainer();
		MiPart leftArrow1 = makeLeftArrow();
		MiPart leftArrow2 = makeLeftArrow();
		container.appendPart(leftArrow1);
		container.appendPart(leftArrow2);
		leftArrow1.setXmax(leftArrow2.getXmin());
		return(container);
		}
	protected	MiPart		makeRewindGr()
		{
		MiContainer container = new MiContainer();
		MiPart leftArrow = makeLeftArrow();
		container.appendPart(leftArrow);
		return(container);
		}
	protected	MiPart		makePrevFrameGr()
		{
		MiContainer container = new MiContainer();
		MiPart leftArrow = makeSkinnyLeftArrow();
		container.appendPart(leftArrow);
		return(container);
		}

	protected	MiPart		makeStopGr()
		{
		MiPart rect = new MiRectangle(0.25, 0.25, 0.75, 0.75);
		rect.setBackgroundColor(buttonDetailColor);
		rect.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		return(rect);
		}
	protected	MiPart		makePauseGr()
		{
		MiContainer container = new MiContainer();
		MiPart rect1 = makeSkinnyRect();
		MiPart rect2 = makeSkinnyRect();
		container.appendPart(rect1);
		container.appendPart(rect2);
		rect1.setXmax(rect2.getXmin() - 0.5);
		return(container);
		}
	protected	MiPart		makeNextFrameGr()
		{
		MiContainer container = new MiContainer();
		MiPart leftArrow = makeSkinnyRightArrow();
		container.appendPart(leftArrow);
		return(container);
		}

	protected	MiPart		makePlayGr()
		{
		MiContainer container = new MiContainer();
		MiPart rightArrow = makeRightArrow();
		container.appendPart(rightArrow);
		return(container);
		}
	protected	MiPart		makeFastForwardGr()
		{
		MiContainer container = new MiContainer();
		MiPart rightArrow1 = makeRightArrow();
		MiPart rightArrow2 = makeRightArrow();
		container.appendPart(rightArrow1);
		container.appendPart(rightArrow2);
		rightArrow1.setXmax(rightArrow2.getXmin());
		return(container);
		}

	protected	MiPart		makeNextTrackGr()
		{
		MiContainer container = new MiContainer();
		MiPart rightArrow = makeRightArrow();
		MiPart rect = makeSkinnyRect();
		container.appendPart(rightArrow);
		container.appendPart(rect);
		rect.setXmin(rightArrow.getXmax());
		return(container);
		}
	protected	MiPart		makeSkinnyRect()
		{
		MiPart rect = new MiRectangle(0, 0, 0.75, 1);
		rect.setBackgroundColor(buttonDetailColor);
		rect.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		return(rect);
		}

	protected	MiPart		makeLeftArrow()
		{
		MiTriangle arrow = makeArrow();
		arrow.setOrientation(Mi_LEFT);
		return(arrow);
		}
	protected	MiPart		makeRightArrow()
		{
		MiTriangle arrow = makeArrow();
		arrow.setOrientation(Mi_RIGHT);
		return(arrow);
		}
	protected	MiTriangle	makeArrow()
		{
		MiTriangle triangle = new MiTriangle();
		triangle.setBackgroundColor(buttonDetailColor);
		triangle.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		return(triangle);
		}

	protected	MiPart		makeSkinnyLeftArrow()
		{
		MiTriangle arrow = makeSkinnyArrow();
		arrow.setOrientation(Mi_LEFT);
		return(arrow);
		}
	protected	MiPart		makeSkinnyRightArrow()
		{
		MiTriangle arrow = makeSkinnyArrow();
		arrow.setOrientation(Mi_RIGHT);
		return(arrow);
		}
	protected	MiTriangle	makeSkinnyArrow()
		{
		MiTriangle triangle = new MiTriangle();
		triangle.setWidth(triangle.getBounds().getWidth()/2);
		triangle.setBackgroundColor(buttonDetailColor);
		triangle.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		triangle.setFixedAspectRatio(true);
		return(triangle);
		}
	}

