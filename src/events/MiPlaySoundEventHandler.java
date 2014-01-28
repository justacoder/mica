
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPlaySoundEventHandler extends MiEventHandler
	{
	private		String		soundFileName;
	private		boolean		repeatSoundLoop;
	private		AudioClip	audioClip;



	public				MiPlaySoundEventHandler(
						String soundFileName, 
						MiEvent startEvent, 
						MiEvent stopEvent, 
						boolean repeatSoundLoop)
		{
		addEventToCommandTranslation(Mi_START_COMMAND_NAME, startEvent);
		if (stopEvent != null)
			addEventToCommandTranslation(Mi_STOP_COMMAND_NAME, stopEvent);
		this.soundFileName = soundFileName;
		this.repeatSoundLoop = repeatSoundLoop;
		setSoundFileName(soundFileName);
		}
	public		MiiEventHandler	copy()
		{
		MiPlaySoundEventHandler obj = (MiPlaySoundEventHandler )super.copy();
		obj.soundFileName = soundFileName;
		obj.repeatSoundLoop = repeatSoundLoop;
		obj.audioClip = audioClip;
		return(obj);
		}

	public		String	 	getSoundFileName()
		{ 
		return(soundFileName);	
		}
	public		void 		setSoundFileName(String filename)
		{ 
		soundFileName = MiSystem.getPropertyOrKey(filename);
		audioClip = MiSystem.getAudioClip(soundFileName);
		}

	public		boolean		isRepeatingSoundLoop() 		
		{
		return(repeatSoundLoop);
		}
	public		void		setIsRepeatingSoundLoop(boolean flag) 
		{ 
		repeatSoundLoop = flag;
		}

					/**------------------------------------------------------
	 				 * Processes the command generated from the current event.
					 * Both are stored in the MiEventHandler super class.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			MiEventHandler#isCommand
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		if (audioClip != null)
			{
			if (isCommand(Mi_START_COMMAND_NAME))
				{
				if (repeatSoundLoop)
					audioClip.loop();
				else
					audioClip.play();
				}
			else
				{
				audioClip.stop();
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}

