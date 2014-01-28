
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
import java.applet.AudioClip;
import java.io.File;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiCustomLookAndFeelSound implements MiiCustomLookAndFeel, MiiTypes
	{
	private		String		soundFileName;
	private		int		startActionType;
	private		int		endActionType;
	private		MiEvent		startEvent;
	private		MiEvent		endEvent;
	private		MiiAction	actionTrigger;
	private		MiiEventHandler	eventTrigger;


					// So class loader can load this
	public				MiCustomLookAndFeelSound()
		{
		}

	public				MiCustomLookAndFeelSound(
						String soundFileName,
						int startActionType)
		{
		this.soundFileName = soundFileName;
		testSoundFileForExistance(soundFileName);
		this.startActionType = startActionType;
		this.endActionType = Mi_NONE;
		MiPlaySoundActionHandler actionHandler 
			= new MiPlaySoundActionHandler(soundFileName, startActionType, Mi_NONE, false);
		actionTrigger = new MiAction(actionHandler, startActionType);
		}
	public				MiCustomLookAndFeelSound(
						String soundFileName,
						int startActionType,
						int endActionType)
		{
		this.soundFileName = soundFileName;
		testSoundFileForExistance(soundFileName);
		this.startActionType = startActionType;
		this.endActionType = endActionType;
		MiPlaySoundActionHandler actionHandler 
			= new MiPlaySoundActionHandler(soundFileName, startActionType, endActionType, true);
		actionTrigger = new MiAction(actionHandler, startActionType, endActionType);
		}
	public				MiCustomLookAndFeelSound(
						String soundFileName,
						MiEvent startEvent,
						MiEvent endEvent)
		{
		this.soundFileName = soundFileName;
		testSoundFileForExistance(soundFileName);
		this.startEvent = startEvent;
		this.endEvent = endEvent;
		eventTrigger = new MiPlaySoundEventHandler(soundFileName, startEvent, endEvent, true);
		}
	public				MiCustomLookAndFeelSound(
						String soundFileName,
						MiEvent startEvent)
		{
		this.soundFileName = soundFileName;
		testSoundFileForExistance(soundFileName);
		this.startEvent = startEvent;
		this.endEvent = null;
		eventTrigger = new MiPlaySoundEventHandler(soundFileName, startEvent, null, false);
		}
	public		void		applyCustomLookAndFeel(MiPart part)
		{
		if (actionTrigger != null)
			part.appendActionHandler(actionTrigger);
		else if (eventTrigger != null)
			part.appendEventHandler(eventTrigger);
		}
	public		void		removeCustomLookAndFeel(MiPart part)
		{
		if (actionTrigger != null)
			part.removeActionHandler(actionTrigger);
		else if (eventTrigger != null)
			part.removeEventHandler(eventTrigger);
		}

	public		MiiCustomLookAndFeel fromSpecification(String spec)
		{
		return(fromString(spec));
		}
	public		String		 toSpecification()
		{
		if (actionTrigger != null)
			{
			return(soundFileName + "(" + MiAction.getActionSpec(startActionType)
				+ (endActionType == Mi_NONE 
					? "" 
					: "," + MiAction.getActionSpec(endActionType)) + ")");
			}
		else // if (eventTrigger != null)
			{
			return(soundFileName + "(" + startEvent 
				+ (endEvent == null ? "" : "," + endEvent) + ")");
			}
		}

	// of form: filename(startEvent, endEvent) or filename(startEvent), or actions
	public static	MiCustomLookAndFeelSound fromString(String spec)
		{
		int index1 = spec.indexOf('(');
		if (index1 == -1)
			;
		int index2 = spec.indexOf(')');
		if (index2 == -1)
			;

		String soundFilename = spec.substring(0, index1);
		String triggers = spec.substring(index1 + 1, index2);
		int commaIndex = triggers.indexOf(',');

		if (commaIndex == -1)
			{
			// Just one event or action
			triggers = triggers.trim();

			MiEvent event = new MiEvent();
			if (MiEvent.stringToEvent(triggers, event))
				{
				return(new MiCustomLookAndFeelSound(soundFilename, event));
				}
			
			try	{
				int action = MiAction.typeFromString(triggers);
				return(new MiCustomLookAndFeelSound(soundFilename, action));
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			throw new IllegalArgumentException("MiCustomLookAndFeelSound: "
				+ "Unable to process sound event or action specification: " + spec); 
			}
		else
			{
			String trigger1 = triggers.substring(0, commaIndex).trim();
			String trigger2 = triggers.substring(commaIndex + 1).trim();

			// Two events or actions
			MiEvent event1 = new MiEvent();
			int action1 = Mi_NONE;
			if (!MiEvent.stringToEvent(trigger1, event1))
				{
				event1 = null;
				try	{
					action1 = MiAction.typeFromString(trigger1);
					}
				catch (Exception e)
					{
					throw new IllegalArgumentException("MiCustomLookAndFeelSound: "
						+ "Unable to process sound event or action specification: " + spec); 
					}
				}
			MiEvent event2 = new MiEvent();
			int action2 = Mi_NONE;
			if (!MiEvent.stringToEvent(trigger2, event2))
				{
				event2 = null;
				try	{
					action2 = MiAction.typeFromString(trigger2);
					}
				catch (Exception e)
					{
					throw new IllegalArgumentException("MiCustomLookAndFeelSound: "
						+ "Unable to process sound event or action specification: " + spec); 
					}
				}
			if ((event1 != null) && (event2 != null))
				return(new MiCustomLookAndFeelSound(soundFilename, event1, event2));

			if ((action1 != Mi_NONE) && (action2 != Mi_NONE))
				return(new MiCustomLookAndFeelSound(soundFilename, action1, action2));

			throw new IllegalArgumentException("MiCustomLookAndFeelSound: "
				+ "Unable to process sound event or action specification: " + spec); 
			}
		}
	protected	void		testSoundFileForExistance(String soundFileName)
		{
		String tmp = MiSystem.getPropertyOrKey(soundFileName);
		File file = new File(tmp);
		if ((!file.exists()) || (!file.isFile()))
			MiDebug.println("Warning: sound file \"" + soundFileName + "\" does not exist or is not a file");
		}
	}
class MiPlaySoundActionHandler implements MiiActionHandler
	{
	private		String		soundFileName;
	private		boolean		repeatSoundLoop;
	private		AudioClip	audioClip;
	private		int		startActionType;
	private		int		endActionType;



	public				MiPlaySoundActionHandler(
						String soundFileName, 
						int startActionType, 
						int endActionType, 
						boolean repeatSoundLoop)
		{
		this.soundFileName = MiSystem.getPropertyOrKey(soundFileName);
		try	{
			audioClip = MiSystem.getAudioClip(this.soundFileName);
			}
		catch (Exception e)
			{
			MiDebug.println(this + ": Unable to load or process sound file");
			}
		this.startActionType = startActionType;
		this.endActionType = endActionType;
		this.repeatSoundLoop = repeatSoundLoop;
		}
	public		boolean		processAction(MiiAction action)
		{
		// If audio file was loaded properly...
		if (audioClip != null)
			{
			if (action.hasActionType(startActionType))
				{
				if (repeatSoundLoop)
					audioClip.loop();
				else
					audioClip.play();
				}
			else if ((endActionType != MiiTypes.Mi_NONE) 
				&& (action.hasActionType(endActionType)))
				{
				audioClip.stop();
				}
			}
		return(true);
		}
	public		String		toString()
		{
		return(super.toString() + "[" + soundFileName + "]");
		}
	}

