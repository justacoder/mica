
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
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTwoStateWidgetEventHandler extends MiEventHandler implements MiiActionTypes, MiiSimpleAnimator, MiiLookProperties
	{
	private		boolean		grabbed;
	private		boolean		animating;
	private		boolean		toGrab 		= true;
	private		boolean		activateOnSelect = true;
	private		boolean		repeatSelectWhenHeld;
	private		MiEditor	containingEditor;
	private		int		animationStartDelayCountDown;
	private		int		animationStartDelay	= 300;	// In millisecs, get this from MiSystem
	private		int		animationInterval	= 100;	// In millisecs

	public				MiTwoStateWidgetEventHandler(
						boolean toGrabWhenSelected,
						boolean repeatSelectWhenHeld,
						boolean activateWhenSelect)
		{
		toGrab = toGrabWhenSelected;
		activateOnSelect = activateWhenSelect;
		setRepeatSelectWhenHeld(repeatSelectWhenHeld);
		animationStartDelay = Utility.toInteger(
			MiSystem.getProperty(Mi_PUSHBUTTON_AUTO_REPEAT_START_DELAY_NAME));
		animationInterval = Utility.toInteger(
			MiSystem.getProperty(Mi_PUSHBUTTON_AUTO_REPEAT_INTERVAL_NAME));
		}

	public		void		setRepeatSelectWhenHeld(boolean flag)
		{
		repeatSelectWhenHeld = flag;
		}

	public		boolean		getRepeatSelectWhenHeld()
		{
		return(repeatSelectWhenHeld);
		}
	public		void		setRepeatStartDelay(int millisecs)
		{
		animationStartDelay = millisecs;
		}
	public		int		getRepeatStartDelay()
		{
		return(animationStartDelay);
		}
	public		void		setRepeatInterval(int millisecs)
		{
		animationInterval = millisecs;
		}
	public		int		getRepeatInterval()
		{
		return(animationInterval);
		}

	public		void		setEditor(MiEditor editor)
		{
		containingEditor = editor;
		}
	public		MiEditor	getEditor()
		{
		return(containingEditor);
		}
	public		int		processCommand()
		{
		MiPart widget = getTarget();

/* FIX: need this for all MiParts, not just MiWidgets
		if (widget.isDesignTime())
			{
			return(Mi_PROPOGATE_EVENT);
			}
*/
//System.out.println("cmd = " + getCommand());

		if (isCommand(Mi_TOGGLE_COMMAND_NAME))
			{
			if (widget.isSelected())
				setCommand(Mi_DESELECT_COMMAND_NAME);
			else
				setCommand(Mi_SELECT_COMMAND_NAME);
			}
		if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			if ((!widget.isSelected()) && (widget.isSensitive()) && (widget.isSelectable()))
				{
				if ((toGrab) && (!grabbed))
					event.editor.prependGrabEventHandler(this);
				grabbed = true;
				widget.select(true);
				if ((activateOnSelect) && (widget.isSelected()))
					{
					widget.dispatchAction(Mi_ACTIVATED_ACTION);
					}
// moved to above so is widget.select activates an actionHandler that deselects this, then we are not left with the grab still outstanding
// However, had no effect on the problem, so can move it back down here anytime...
//grabbed = true;
				if ((!animating) && (repeatSelectWhenHeld))
					{
					MiAnimationManager.addAnimator(widget, this);
					animationStartDelayCountDown = animationStartDelay;
					animating = true;
					}
				return(Mi_CONSUME_EVENT);
				}
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if ((widget.isSelected()) && (widget.isSensitive()))
				{
				if (!widget.pick(event.worldMouseFootPrint))
					{
					// DeSelect, mouse no longer over widget...
					if ((toGrab) && (grabbed))
						event.editor.removeGrabEventHandler(this);
					if (animating)
						MiAnimationManager.removeAnimator(widget, this);
					widget.select(false);
					grabbed = false;
					animating = false;
					}

				return(Mi_CONSUME_EVENT);
				}
			}
		else if (isCommand(Mi_DESELECT_COMMAND_NAME))
			{
			if (widget.isSelected() && widget.isSelectable())
				{
				if ((toGrab) && (grabbed))
					event.editor.removeGrabEventHandler(this);
				if (animating)
					MiAnimationManager.removeAnimator(widget, this);
				widget.select(false);
				grabbed = false;
				animating = false;

				if (!activateOnSelect)
					{
					widget.dispatchAction(Mi_ACTIVATED_ACTION);
					}

				return(Mi_CONSUME_EVENT);
				}
			else
				{
				// In case the select request action was vetoed, restore the state
				if ((toGrab) && (grabbed))
					event.editor.removeGrabEventHandler(this);
				if (animating)
					MiAnimationManager.removeAnimator(widget, this);
				grabbed = false;
				animating = false;
				}
			}
		else if (isCommand(Mi_NOOP_COMMAND_NAME))
			{
			return(Mi_CONSUME_EVENT);
			}
		return(Mi_PROPOGATE_EVENT);
		}
	public		long		animate()
		{
		animationStartDelayCountDown -= animationInterval;
		if (animationStartDelayCountDown < 0)
			{
			MiPart widget = getTarget();
			widget.dispatchAction(Mi_SELECT_REPEATED_ACTION);
			if (activateOnSelect)
				widget.dispatchAction(Mi_ACTIVATED_ACTION);
			}
		return(animationInterval);
		}
	}


