
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
public class MiIDisplayToolHints extends MiEventMonitor implements MiiTypes
	{
	private		MiPart		toolHintedObject;
	private		MiPart		prevToolHintedObject;
	private		MiText		toolHintText				= new MiText();
	private		MiLabel		toolHintPopup				= new MiLabel(toolHintText);
	private		MiHelpManager	helpManager;
	private		MiEditor	toolHintedWin;
	private		MiPoint		tmpPoint	= new MiPoint();
	private		boolean		showingToolHintsWithoutPausingMode;
	private		boolean		allowToolHintsRunningMode		= true;
	private		boolean		hideToolHintWhenMousePaused		= false;
	private		boolean		waitUntilPausedToStartShowingToolHints 	= true;
	private		boolean		hideToolHintIfNonMouseMotionEventOccurs	= true;
	private		int		currentNumberOfSecondsToolHintDisplayTime;
	private		int		desiredNumberOfSecondsToolHintDisplayTime	= 5;


	
	public				MiIDisplayToolHints()
		{
		helpManager = MiSystem.getHelpManager();
		toolHintPopup.setContainerLayoutSpec(MiVisibleContainer.MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS);
		}


	public		MiPart		getToolHintPopup()
		{
		return(toolHintPopup);
		}

					/** 
					 * Whether or not to hide toolhint after user is paused
					 * while showing a toolHint 
					 **/
	public		void		setHideToolHintWhenMousePaused(boolean flag)
		{
		hideToolHintWhenMousePaused = flag;
		}
	public		boolean		getHideToolHintWhenMousePaused()
		{
		return(hideToolHintWhenMousePaused);
		}
					/** 
					 * After a Hint is displayed, allows Hints to be
					 * displayed without the user pausing. This mode
					 * is terminated when the user pauses again.
					 **/
	public		void		setAllowToolHintsRunningMode(boolean flag)
		{
		allowToolHintsRunningMode = flag;
		}
	public		boolean		getAllowToolHintsRunningMode()
		{
		return(allowToolHintsRunningMode);
		}
					/** 
					 * Whether or not to wait until user is paused
					 * before showing a toolHint 
					 **/
	public		void		setWaitUntilPausedToStartShowingToolHints(boolean flag)
		{
		waitUntilPausedToStartShowingToolHints = flag;
		}
	public		boolean		getWaitUntilPausedToStartShowingToolHints()
		{
		return(waitUntilPausedToStartShowingToolHints);
		}

	public		int		processEvent(MiEvent event)
		{
		if (event.getType() == Mi_TIMER_TICK_EVENT)
			{
			return(Mi_PROPOGATE_EVENT);
			}
		if (event.getType() == Mi_IDLE_EVENT)
			{
//MiDebug.println("IDLE TOOLHINT");
			showingToolHintsWithoutPausingMode = false;
			}
		else if ((event.getType() == Mi_MOUSE_MOTION_EVENT) && (hideToolHintWhenMousePaused))
			{
			prevToolHintedObject = null;
			}


		if (((event.getType() == Mi_IDLE_EVENT) && (hideToolHintWhenMousePaused) && (toolHintedObject != null))
			|| (event.getType() == Mi_WINDOW_EXIT_EVENT))
			{
			if (toolHintedObject != null)
				{
//MiDebug.println("REMOVE? currentNumberOfSecondsToolHintDisplayTime = " + currentNumberOfSecondsToolHintDisplayTime);
				if (++currentNumberOfSecondsToolHintDisplayTime >= desiredNumberOfSecondsToolHintDisplayTime)
					{
					toolHintedWin.removeAttachment(toolHintPopup); 
					prevToolHintedObject = toolHintedObject;
					toolHintedObject = null;
					currentNumberOfSecondsToolHintDisplayTime = 0;
//MiDebug.println("HIDE TOOLHINT REMOVE");
					}
				}
			return(Mi_PROPOGATE_EVENT);
			}
		else if (((event.getType() == Mi_MOUSE_MOTION_EVENT) 
			&& ((!waitUntilPausedToStartShowingToolHints) || (toolHintedObject != null)
				|| (showingToolHintsWithoutPausingMode)))
			|| ((event.getType() == Mi_IDLE_EVENT) && (toolHintedObject == null)
				&& (waitUntilPausedToStartShowingToolHints)))
			{
//MiDebug.println("IDLE TOOLHINT looking...");
			MiParts targetPath = event.getTargetPath();
			for (int i = 0; i < targetPath.size(); ++i)
				{
				MiPart obj = targetPath.elementAt(i);
//MiDebug.println("Object under mouse:" + obj);
//MiDebug.println("toolHintedObject :" + toolHintedObject);
//MiDebug.println("Object toolHint :" + obj.getToolHintHelp(null));
				if ((obj == toolHintedObject) || (obj == prevToolHintedObject))
					{
					return(Mi_PROPOGATE_EVENT);
					}
				String toolHintMessage = helpManager.getToolHintForObject(
								obj, event.getWorldPoint(tmpPoint));
//MiDebug.println("Object toolHintMsg :" + toolHintMessage);
				if (toolHintMessage != null)
					{
					MiAttributes toolHintAttributes = null;
					MiiHelpInfo toolHint = obj.getToolHintHelp(event.getWorldPoint(tmpPoint));
					if ((toolHint != null) && (toolHint.isEnabled()))
						{
						toolHintAttributes = toolHint.getAttributes();
						}
//MiDebug.println("Object: " + obj + " under mouse has toolHint:" + toolHintMessage);

					if (toolHintedObject != null)
						toolHintedWin.removeAttachment(toolHintPopup); 
					
					showingToolHintsWithoutPausingMode = allowToolHintsRunningMode;

					toolHintedObject = obj;
					prevToolHintedObject = obj;
					toolHintedWin = obj.getContainingWindow();
					toolHintPopup.setValue(toolHintMessage);
					refreshLookAndFeel();
					if (toolHintAttributes != null)
						toolHintPopup.overrideAttributes(toolHintAttributes);
					toolHintPopup.validateLayout();

					MiBounds location = new MiBounds(event.worldMouseFootPrint);
					event.editor.transformToOtherEditorSpace(toolHintedWin, location, location);
					//toolHintedWin.convertRootWorldToLocalWorld(location);
					MiBounds toolHintBounds = toolHintPopup.getBounds();
					toolHintBounds.translateYmaxTo(location.getYmin() - 2*location.getHeight());
					toolHintBounds.translateXminTo(location.getCenterX());
					toolHintBounds.positionInsideContainer(toolHintedWin.getInnerBounds());
					toolHintPopup.setBounds(toolHintBounds);

					toolHintedWin.appendAttachment(toolHintPopup); 
//MiDebug.println("SHOW TOOLHINT");
					return(Mi_PROPOGATE_EVENT);
					}
				}
			if (toolHintedObject != null)
				{
				toolHintedWin.removeAttachment(toolHintPopup); 
				toolHintedObject = null;
//MiDebug.println("HIDE TOOLHINT");
				}
			}
		else if ((hideToolHintIfNonMouseMotionEventOccurs)
			&& (event.getType() != Mi_NO_LONGER_IDLE_EVENT)
			&& (event.getType() != Mi_IDLE_EVENT))
			{
			if (toolHintedObject != null)
				{
				toolHintedWin.removeAttachment(toolHintPopup); 
				toolHintedObject = null;
				showingToolHintsWithoutPausingMode = false;
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	protected	void		refreshLookAndFeel()
		{
		toolHintPopup.setBackgroundColor("#ffffa0");
		toolHintPopup.setInsetMargins(new MiMargins(5,2,5,2));
		toolHintPopup.setBorderLook(MiiTypes.Mi_RAISED_BORDER_LOOK);
		toolHintPopup.setHasShadow(true);
		toolHintPopup.setShadowColor(MiColorManager.getColor("#60808080"));
		toolHintPopup.setShadowLength(3);
		MiAttributes atts = MiSystem.getPropertyAttributes("MiToolhint");
		toolHintPopup.overrideAttributes(atts);
		toolHintPopup.setPickable(false);
		toolHintPopup.setStatusHelp(MiHelpInfo.ignoreThis);

		atts = MiSystem.getPropertyAttributes("MiToolhint.text");
		toolHintText.setFont(new MiFont(toolHintText.getFont().getName(), MiFont.BOLD, 14));
		toolHintText.overrideAttributes(atts);
		toolHintText.setPickable(false);
		}
	}

