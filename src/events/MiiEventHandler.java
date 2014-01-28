
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
public interface MiiEventHandler extends MiiEventTypes
	{
			/**------------------------------------------------------
	 		 * Gets the name of this event handler. 
			 * @return 		the name of this event handler
			 *------------------------------------------------------*/
	String		getName();


			/**------------------------------------------------------
	 		 * Gets the type of this event handler. Valid types are:
			 *    Mi_ORDINARY_EVENT_HANDLER_TYPE
			 *    Mi_SHORT_CUT_EVENT_HANDLER_TYPE
			 *    Mi_MONITOR_EVENT_HANDLER_TYPE
			 *    Mi_GRAB_EVENT_HANDLER_TYPE
			 * @return 		the type of this event handler
			 *------------------------------------------------------*/
	int		getType();


			/**------------------------------------------------------
	 		 * Processes the given event. 
			 * @param  event	The event to process
			 * @return 		Mi_CONSUME_EVENT if no other event
			 *			handlers should see this event
			 *			Mi_PROPOGATE_EVENT if other event
			 *			handlers can also see this event
			 *------------------------------------------------------*/
	int		processEvent(MiEvent event);


			/**------------------------------------------------------
	 		 * Gets an array of events that can be processed by this
			 * event handler.
			 * @return 		an array of events that this event
			 *			handler is interested in.
			 *------------------------------------------------------*/
	MiEvent[]	getRequestedEvents();


			/**------------------------------------------------------
	 		 * Gets an array of command names that can be processed by
			 * this event handler. Command names can be generated from
			 * translations of input events, default translations of
			 * which are specified by the event handler. Many event 
			 * handlers use this methodology to support a mapping from 
			 * specific events to commands. In this way, the event handlers
			 * process the commands, not events. However the event
			 * handler will often look at the current event stored
			 * in the event handler to get additional (i.e. positional)
			 * information.
			 * @return 		an array of comands that this event
			 *			handler is capable of processing 
			 * @see			#addEventToCommandTranslation
			 * @see			#setEventToCommandTranslation
			 * @see			#removeEventToCommandTranslation
			 *------------------------------------------------------*/
	String[]	getRequestedCommands();

			/**------------------------------------------------------
	 		 * Gets whether this event handler only want events that
			 * occur within the bounds of any MiPart is assigned to.
			 * Otherwise this would be a 'global' or 'window-wide'
			 * event handler.
			 * @return 		true if only want events that
			 *			occur within the bounds of the
			 *			MiPart it is assigned to.
			 *------------------------------------------------------*/
	boolean		isPositionDependent();


			/**------------------------------------------------------
	 		 * Gets whether this event handler is copied and assigned
			 * along with the part that it assigned to when the part 
			 * is copied.
			 * @return  		true only if this eventhandler
			 *			is not to be copied. Default is false.
			 *------------------------------------------------------*/
	boolean		isSingleton();


			/**------------------------------------------------------
	 		 * Adds a translation from the given event to the given
			 * command. When the event is received by this event 
			 * handler it will generate the given command and send it
			 * to the processCommand method.
			 * @param command	the command to translate the event to
			 * @param event		the event to translate
			 * @see			#setEventToCommandTranslation
			 * @see			#removeEventToCommandTranslation
			 *------------------------------------------------------*/
	void		addEventToCommandTranslation(String command, MiEvent event);


			/**------------------------------------------------------
	 		 * Sets (replaces) the translation for the given command to
			 * now be generated from the given event. When the event is
			 * received by this event handler it will generate the 
			 * given command and send it to the processCommand method.
			 * Only the first translation found is modified.
			 * @param command	the command to translate the event to
			 * @param event		the event to translate
			 * @return 		true if a translation was modified
			 * @see			#addEventToCommandTranslation
			 * @see			#removeEventToCommandTranslation
			 *------------------------------------------------------*/
	boolean		setEventToCommandTranslation(String command, MiEvent event);


			/**------------------------------------------------------
	 		 * Removes the translation that generates the given 
			 * command. Only the first translation found is removed.
			 * @param command	the command whose translation to remove.
			 * @return 		true if a translation was removed
			 * @see			#addEventToCommandTranslation
			 * @see			#setEventToCommandTranslation
			 *------------------------------------------------------*/
	void		removeEventToCommandTranslation(String command);


			/**------------------------------------------------------
	 		 * Removes the translation that involves the given event.
			 * Only the first translation found is removed.
			 * @param event		the event whose translation to remove.
			 * @return 		true if a translation was removed
			 * @see			#addEventToCommandTranslation
			 * @see			#setEventToCommandTranslation
			 *------------------------------------------------------*/
	void		removeEventToCommandTranslation(MiEvent event);


			/**------------------------------------------------------
			 * Returns a copy of this MiiEventHandler.
			 * @return 	 	the copy
			 *------------------------------------------------------*/
	MiiEventHandler	copy();


			/**------------------------------------------------------
	 		 * Sets the MiPart that this is assigned to. This is set
			 * when this is assigned to a part.
			 * @param part		the target part
			 *------------------------------------------------------*/
	void		setTarget(MiPart part);


			/**------------------------------------------------------
			 * Gets the MiPart that this is assigned to. This should be
			 * used insetad of any local references to the target.
			 * @return		the assigned to part
			 *------------------------------------------------------*/
	MiPart		getTarget();


			/**------------------------------------------------------
			 * Sets the property with the given name to the given
			 * value. This is useful for changing the events that
			 * triggers a command.
			 * @param name		the name of an property
			 * @param value		the value of the property
			 *------------------------------------------------------*/
	void		setPropertyValue(String name, String value);


			/**------------------------------------------------------
	 		 * Sets the part which is targeted by this event handler.
			 * Used for grabs to indicate what coordinate system all 
			 * delivered events should be in.
			 * @param part		the part targeted by grab event 
			 *			handlers
			 * @depreciated
			 *------------------------------------------------------*/
	void		setObject(MiPart part);


			/**------------------------------------------------------
	 		 * Gets the part which is targeted by this event handler.
			 * Used for grabs to indicate what coordinate system all 
			 * delivered events should be in.
			 * @return 		the part targeted by grab event 
			 *			handlers
			 * @depreciated
			 *------------------------------------------------------*/
	MiPart		getObject();

			/**------------------------------------------------------
	 		 * Sets whether this event handler is enabled.
			 * @param flag 		true if enabled
			 *------------------------------------------------------*/
	void		setEnabled(boolean flag);

			/**------------------------------------------------------
	 		 * Gets whether this event handler is enabled.
			 * @return 		true if enabled
			 *------------------------------------------------------*/
	boolean		isEnabled();
	}


