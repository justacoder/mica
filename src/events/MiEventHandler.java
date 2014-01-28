
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
 * This class implements the MiiEventHandler. It also serves as
 * the base class of the MiEventMonitor and MiShortCutHandler
 * classes.
 * <p>
 * A sub-class of this abstract class implements one of two methods:
 * <p>
 * <pre>
 * processCommand()	This method uses isCommand(String) to ascertain 
 *			what the current command is and then processCommands
 *			functionality accordingly. It also may use
 *			getEvent() to get the event that triggered the
 *			command. Use translations to specify what events
 *			trigger what commands.
 * or
 *
 * processEvent(MiEvent)This method overrides any an all translations
 *			this event handler may have and receives all
 *			the events sent to the event handler.
 * </pre>
 * <em>Translations</em> 
 * <p>
 * Each event handler has it's own set of (optional) 'translations'.
 * A translation converts an event into a command. Typically an event
 * handler will have a set of functionality which is invoked by a set
 * of commands. 
 * <p>
 * The general event handler will also have a number of pre-defined
 * translations that are it's 'default' translations. These default
 * translations. map a set of suitable events to it's command 
 * functionality. These events are 'suitable' in that, in all of the
 * event handlers supplied with Mica, the event handlers can all work
 * simultaneously. For example, <Btn3Click> cannot be mapped to both
 * MiIZoomAroundMouse.ZoomOut and MiIDisplayContextMenu.popup because
 * these are often used together in a graphics editor.
 * <p>
 * <em>Position Dependency</em> 
 * <p>
 * Event handlers can be either position dependent or independent. If
 * they are dependent, then they only respond to events that occur within
 * the bounds of the MiPart they are assinged to. If they are independent,
 * then the respond to events that occur anywhere within the window 
 * containing the MiPart they are assigned to. These position independent
 * event handlers are also called 'global' event handlers (see MiWindow).
 * 
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiEventHandler implements MiiEventHandler, MiiCommandNames
	{
	private static	int		DEFAULT_NUM_TRANSLATIONS= 3;
	private static	int		numInstances;
	private 	String		name;
	private 	int		instanceNumber;
	private		boolean		enabled			= true;
	private		boolean		locationSpecific	= true;
	private		boolean		alwaysConsumeEvents;
	private		boolean		isSingleton;
	private		MiPart		target;
	private		MiPart		targetsWindow;
	private		int		type		 	= Mi_ORDINARY_EVENT_HANDLER_TYPE;
	private		int		numTranslations;
	private		MiEventToCommandTranslation	
					translationsTable[] 	= new MiEventToCommandTranslation[DEFAULT_NUM_TRANSLATIONS];
					// Current command being executed, if any
	protected	String		command;		
					// Current triggering event, if any.
	protected	MiEvent		event;



					/**------------------------------------------------------
	 				 * Constructs a new MiEventHandler. 
					 *------------------------------------------------------*/
	public				MiEventHandler()
		{
		instanceNumber = numInstances++;
		}
					/**------------------------------------------------------
			 		 * Sets the name of this event handler. 
					 * @param name 		the name of this event handler
					 *------------------------------------------------------*/
	public		void		setName(String name)
		{
		this.name = name;
		}
					/**------------------------------------------------------
			 		 * Gets the name of this event handler. 
					 * @return 		the name of this event handler
					 * @implements		MiiEventHandler#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(name);
		}
					/**------------------------------------------------------
	 				 * Sets the MiPart that this is assigned to. This is set
					 * when this is assigned to a part.
					 * @param part		the target part
					 * @implements		MiiEventHandler#setTarget
					 *------------------------------------------------------*/
	public		void		setTarget(MiPart part)
		{
		target = part;
		}
					/**------------------------------------------------------
	 				 * Gets the MiPart that this is assigned to. This should be
					 * used instead of any local references to the target. Or
					 * better yet, access the event.getTarget if possible. This
					 * is in order that copying of MiParts, which also copies their
					 * eventhandlers, works as expected.
					 * @return		the assigned to part
					 * @implements		MiiEventHandler#getTarget
					 *------------------------------------------------------*/
	public		MiPart		getTarget()
		{
		return(target);
		}
					/**------------------------------------------------------
	 				 * Sets the MiPart that events sent to this event handler
					 * should transform their positions relative to.
					 * @param part		the local part
					 * @implements		MiiEventHandler#setObject
					 *------------------------------------------------------*/
	public		void		setObject(MiPart part)
		{
		targetsWindow = part;
		}
					/**------------------------------------------------------
	 				 * Gets the MiPart that events sent to this event handler
					 * should transform their positions relative to.
					 * @return		the local part
					 * @implements		MiiEventHandler#getObject
					 *------------------------------------------------------*/
	public		MiPart		getObject()
		{
		return(targetsWindow);
		}
					/**------------------------------------------------------
	 				 * Sets the type of this event handler. Valid types are:
					 *    Mi_ORDINARY_EVENT_HANDLER_TYPE
					 *    Mi_SHORT_CUT_EVENT_HANDLER_TYPE
					 *    Mi_MONITOR_EVENT_HANDLER_TYPE
					 *    Mi_GRAB_EVENT_HANDLER_TYPE
					 * @param type 		the type of this event handler
					 * @see			#getType
					 *------------------------------------------------------*/
	protected	void		setType(int type)
		{
		this.type = type;
		}
					/**------------------------------------------------------
	 				 * Gets the type of this event handler. Valid types are:
					 *    Mi_ORDINARY_EVENT_HANDLER_TYPE
					 *    Mi_SHORT_CUT_EVENT_HANDLER_TYPE
					 *    Mi_MONITOR_EVENT_HANDLER_TYPE
					 *    Mi_GRAB_EVENT_HANDLER_TYPE
					 * @return 		the type of this event handler
					 * @see			#setType
					 *------------------------------------------------------*/
	public		int		getType()
		{
		return(type);
		}
					/**------------------------------------------------------
	 				 * Gets whether this event handler is of the given type.
					 * @return 		true if of the given type
					 * @see 		#getType
					 *------------------------------------------------------*/
	public		boolean		isType(int type)
		{
		return((this.type == type) ? true : false);
		}
					/**------------------------------------------------------
	 				 * Sets whether this event handler is enabled.
					 * @param flag 		true if enabled
					 *------------------------------------------------------*/
	public		void		setEnabled(boolean flag)
		{
		enabled = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether this event handler is enabled.
					 * @return 		true if enabled
					 *------------------------------------------------------*/
	public		boolean		isEnabled()
		{
		return(enabled);
		}
					/**------------------------------------------------------
	 				 * Sets whether this event handler will always return
					 * Mi_CONSUME_EVENT whenever it receives an event that is
					 * a trigger event (i.e. one that is found in the translation
					 * table). This is useful in situations where there are 
					 * MiParts on top of each other and it is desired that the
					 * desensitizing of the top part should not cause the user's
					 * actions to apply to the underneath part.
					 * @param flag 		true if consuming trigger events
					 * @see 		#getAlwaysConsumeTriggerEvents
					 *------------------------------------------------------*/
	public		void		setAlwaysConsumeTriggerEvents(boolean flag)
		{
		alwaysConsumeEvents = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether this event handler will always return
					 * Mi_CONSUME_EVENT whenever it receives an event that is
					 * a trigger event (i.e. one that is found in the translation
					 * table). 
					 * @return 		true if consuming trigger events
					 * @see 		#setAlwaysConsumeTriggerEvents
					 *------------------------------------------------------*/
	public		boolean		getAlwaysConsumeTriggerEvents()
		{
		return(alwaysConsumeEvents);
		}
					/**------------------------------------------------------
			 		 * Sets whether this event handler only want events that
					 * occur within the bounds of any MiPart is assigned to.
					 * Otherwise this would be a 'global' or 'window-wide'
					 * event handler.
					 * @param flag 		true if only want events that
					 *			occur within the bounds of the
					 *			MiPart it is assigned to.
					 *------------------------------------------------------*/
	public		void		setPositionDependent(boolean flag)
		{
		locationSpecific = flag;
		}
					/**------------------------------------------------------
			 		 * Gets whether this event handler only want events that
					 * occur within the bounds of any MiPart is assigned to.
					 * Otherwise this would be a 'global' or 'window-wide'
					 * event handler.
					 * @return 		true if only want events that
					 *			occur within the bounds of the
					 *			MiPart it is assigned to.
					 *------------------------------------------------------*/
	public		boolean		isPositionDependent()
		{
		return(locationSpecific);
		}

					/**------------------------------------------------------
			 		 * Sets whether this event handler is copied and assigned
					 * along with the part that it assigned to when the part 
					 * is copied.
					 * @param flag 		true only if want this eventhandler
					 *			not to be copied. Default is false.
					 *------------------------------------------------------*/
	public		void		setIsSingleton(boolean flag)
		{
		isSingleton = flag;
		}
					/**------------------------------------------------------
			 		 * Gets whether this event handler is copied and assigned
					 * along with the part that it assigned to when the part 
					 * is copied.
					 * @return  		true only if this eventhandler
					 *			is not to be copied. Default is false.
					 *------------------------------------------------------*/
	public		boolean		isSingleton()
		{
		return(isSingleton);
		}
					/**------------------------------------------------------
	 				 * Adds a translation from the given event to the given
					 * command. When the event is received by this event 
					 * handler it will generate the given command and send it
					 * to the processCommand method.
					 * @param command	the command to translate the event to
					 * @param type		the type of the event to translate
					 * @param key		the key of the event to translate
					 * @param modifiers	the modifiers of the event to translate
					 * @see			#setEventToCommandTranslation
					 * @see			#removeEventToCommandTranslation
					 *------------------------------------------------------*/
	public		void		addEventToCommandTranslation(
						String commandToGenerate, int type, int key, int modifiers)
		{
		addEventToCommandTranslation(commandToGenerate, new MiEvent(type, key, modifiers, true));
		}
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
	public		void		addEventToCommandTranslation(String commandToGenerate, MiEvent event)
		{
		if (numTranslations + 1 < translationsTable.length)
			{
			MiEventToCommandTranslation[] newTable 
				= new MiEventToCommandTranslation[translationsTable.length + 1];
			System.arraycopy(translationsTable, 0, newTable, 0, translationsTable.length);
			translationsTable = newTable;
			}

		MiEventToCommandTranslation trigger = new MiEventToCommandTranslation();
		trigger.command = commandToGenerate;
		trigger.event = event;
		translationsTable[numTranslations] = trigger;
		++numTranslations;
		}
					/**------------------------------------------------------
			 		 * Adds a translation from the given event to the given
					 * command. When the event is received by this event 
					 * handler it will generate the given command and send it
					 * to the processCommand method.
					 * @param command	the command to translate the event to
					 * @param event1	the part1 of the two part event to translate
					 * @param event2	the part2 of the two part event to translate
					 * @see			#setEventToCommandTranslation
					 * @see			#removeEventToCommandTranslation
					 *------------------------------------------------------*/
	public		void		addEventToCommandTranslation(
						String commandToGenerate, MiEvent event1, MiEvent event2)
		{
		if (numTranslations + 1 < translationsTable.length)
			{
			MiEventToCommandTranslation[] newTable 
				= new MiEventToCommandTranslation[translationsTable.length + 1];
			System.arraycopy(translationsTable, 0, newTable, 0, translationsTable.length);
			translationsTable = newTable;
			}

		MiEventToCommandTranslation2 trigger = new MiEventToCommandTranslation2();
		trigger.command = commandToGenerate;
		trigger.event = event1;
		trigger.event2 = event2;
		translationsTable[numTranslations] = trigger;
		++numTranslations;
		}
					/**------------------------------------------------------
			 		 * Modifies an existing or adds if none a translation 
					 * from the given event  sequence to the given
					 * command. When the event is received by this event 
					 * handler it will generate the given command and send it
					 * to the processCommand method.
					 * @param command	the command to translate the event to
					 * @param event1	the part1 of the two part event to translate
					 * @param event2	the part2 of the two part event to translate
					 * @return 		true if modified an existing translation
					 * @see			#setEventToCommandTranslation
					 * @see			#removeEventToCommandTranslation
					 *------------------------------------------------------*/
	public		boolean		setEventToCommandTranslation(
						String commandToGenerate, MiEvent event1, MiEvent event2)
		{
		for (int i = 0; i < numTranslations; ++i)
			{
			if (translationsTable[i].command.equalsIgnoreCase(commandToGenerate))
				{
				MiEventToCommandTranslation2 trigger = null;
				if (translationsTable[i] instanceof MiEventToCommandTranslation2)
					{
					trigger = (MiEventToCommandTranslation2 )translationsTable[i];
					}
				else
					{
					trigger = new MiEventToCommandTranslation2();
					}
					
				trigger.command = commandToGenerate;
				trigger.event = event1;
				trigger.event2 = event2;
				translationsTable[i] = trigger;
				return(true);
				}
			}
		addEventToCommandTranslation(commandToGenerate, event1, event2);
		return(false);
		}
					/**------------------------------------------------------
			 		 * Sets (replaces) the translation for the given command to
					 * now be generated from the given event. When the event is
					 * received by this event handler it will generate the 
					 * given command and send it to the processCommand method.
					 * Only the first translation found is modified.
					 * @param command	the command to translate the event to
					 * @param type		the type of the event to translate
					 * @param key		the key of the event to translate
					 * @param modifiers	the modifiers of the event to translate
					 * @return 		true if a translation was modified
					 * @see			#addEventToCommandTranslation
					 * @see			#removeEventToCommandTranslation
					 *------------------------------------------------------*/
	public		boolean		setEventToCommandTranslation(
						String commandToGenerate, int type, int key, int modifiers)
		{
		return(setEventToCommandTranslation(commandToGenerate, new MiEvent(type, key, modifiers, true)));
		}
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
	public		boolean		setEventToCommandTranslation(String commandToGenerate, MiEvent event)
		{
		for (int i = 0; i < numTranslations; ++i)
			{
			if (translationsTable[i].command.equalsIgnoreCase(commandToGenerate))
				{
				translationsTable[i].event.type = event.type;
				translationsTable[i].event.key = event.key;
				translationsTable[i].event.modifiers = event.modifiers;
				return(true);
				}
			}
		addEventToCommandTranslation(commandToGenerate, event); // Added 11-14-2003 because it makes sense
		return(false);
		}
	public		MiEvent		getEventToCommandTranslation(String commandToGenerate, MiEvent event)
		{
		for (int i = 0; i < numTranslations; ++i)
			{
			if (translationsTable[i].command.equalsIgnoreCase(commandToGenerate))
				{
				event.type = translationsTable[i].event.type;
				event.key = translationsTable[i].event.key;
				event.modifiers = translationsTable[i].event.modifiers;
				return(event);
				}
			}
		return(null);
		}
					/**------------------------------------------------------
			 		 * Removes the translation that generates the given 
					 * command. Only the first translation found is removed.
					 * @param command	the command whose translation to 
					 *			remove.
					 * @param type		the type of the event of the 
					 *			translation to remove
					 * @param key		the key of the event of the 
					 *			translation to remove
					 * @param modifiers	the modifiers of the event of the 
					 *			translation to remove
					 * @return 		true if a translation was removed
					 * @see			#addEventToCommandTranslation
					 * @see			#setEventToCommandTranslation
					 *------------------------------------------------------*/
	public		void		removeEventToCommandTranslation(
						String commandToGenerate, int type, int key, int modifiers)
		{
		removeEventToCommandTranslation(commandToGenerate, new MiEvent(type, key, modifiers, true));
		}
					/**------------------------------------------------------
			 		 * Removes the translation that generates the given 
					 * command. Only the first translation found is removed.
					 * @param command	the command of the translation to 
					 *			remove.
					 * @param event		the event part of the translation 
					 *			to remove
					 * @return 		true if a translation was removed
					 * @see			#addEventToCommandTranslation
					 * @see			#setEventToCommandTranslation
					 *------------------------------------------------------*/
	public		void		removeEventToCommandTranslation(
						String commandToGenerate, MiEvent event)
		{
		for (int i = 0; i < numTranslations; ++i)
			{
			if ((translationsTable[i].command.equalsIgnoreCase(commandToGenerate))
				&& (translationsTable[i].event.type == event.type)
				&& (translationsTable[i].event.key == event.key)
				&& (translationsTable[i].event.modifiers == event.modifiers))
				{
				--numTranslations;
				System.arraycopy(translationsTable, i + 1, translationsTable, i, numTranslations - i);
				--i;
				}
			}
		}
					/**------------------------------------------------------
			 		 * Removes the translation that generates the given 
					 * command. Only the first translation found is removed.
					 * @param command	the command whose translation to remove.
					 * @return 		true if a translation was removed
					 * @see			#addEventToCommandTranslation
					 * @see			#setEventToCommandTranslation
					 *------------------------------------------------------*/
	public		void		removeEventToCommandTranslation(String commandToGenerate)
		{
		for (int i = 0; i < numTranslations; ++i)
			{
			if (translationsTable[i].command.equalsIgnoreCase(commandToGenerate))
				{
				--numTranslations;
				System.arraycopy(translationsTable, i + 1, translationsTable, i, numTranslations - i);
				--i;
				}
			}
		}
					/**------------------------------------------------------
			 		 * Removes the translation that involves the given event.
					 * Only the first translation found is removed.
					 * @param event		the event whose translation to remove.
					 * @return 		true if a translation was removed
					 * @see			#addEventToCommandTranslation
					 * @see			#setEventToCommandTranslation
					 *------------------------------------------------------*/
	public		void		removeEventToCommandTranslation(MiEvent event)
		{
		for (int i = 0; i < numTranslations; ++i)
			{
			if ((translationsTable[i].event.type == event.type)
				&& (translationsTable[i].event.key == event.key)
				&& (translationsTable[i].event.modifiers == event.modifiers))
				{
				--numTranslations;
				System.arraycopy(translationsTable, i + 1, translationsTable, i, numTranslations - i);
				--i;
				}
			}
		}
	public		void		removeAllEventToCommandTranslations()
		{
		translationsTable = new MiEventToCommandTranslation[DEFAULT_NUM_TRANSLATIONS];
		numTranslations = 0;
		}
					/**------------------------------------------------------
			 		 * Processes the given event. 
					 * @param  event	The event to process
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see this event
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see this event
					 *------------------------------------------------------*/
	public		int		processEvent(MiEvent event)
		{
		if (!enabled)
			return(Mi_PROPOGATE_EVENT);

		// --------------------------------------------------------
		// For all translations for this event handler...
		// --------------------------------------------------------
		for (int i = 0; i < numTranslations; ++i)
			{
			// --------------------------------------------------------
			// .. if the given event is found in a translation (i.e. is
			// a 'trigger' event.
			// --------------------------------------------------------
			if (translationsTable[i].matchesEvent(event))
				{
				// --------------------------------------------------------
				// Copy the given event into the 'current' event.
				// --------------------------------------------------------
				if (this.event == null)
					this.event = new MiEvent();
				this.event.copy(event);

				// --------------------------------------------------------
				// Get the command from the translation table
				// --------------------------------------------------------
				command = translationsTable[i].command;

				// --------------------------------------------------------
				// For debugging...
				// --------------------------------------------------------
				if (MiDebug.debug 
					&& MiDebug.isTracing(null, MiDebug.TRACE_EVENT_TO_COMMAND_TRANSLATION)
					&& (!getClass().getName().equals("com.swfm.mica.MiIDisplayTooltips")))
					{
					String handlerType = getNameForType(type);
					String eventType = getNameForType(event.handlerTargetType);
					MiDebug.println("++++++" + this + "<" + handlerType + 
						"Handler>.processCommand(" + "<" + eventType + "Event>" 
						+ command + "); target = " + event.target);
					}

				// --------------------------------------------------------
				// Assert that the event is targeted to an event handler of
				// this type.
				// --------------------------------------------------------
				if ((!isType(event.handlerTargetType)) 
					&& (event.handlerTargetType != Mi_GRAB_EVENT_HANDLER_TYPE))
					{
					MiDebug.halt("Handler type and event type do not match");
					}

				// --------------------------------------------------------
				// Process the command.
				// --------------------------------------------------------
				int status = processCommand();

				// --------------------------------------------------------
				// If always consume events, consume it. Else return the
				// status returned by the subclass.
				// --------------------------------------------------------
				if (alwaysConsumeEvents)
					status = Mi_CONSUME_EVENT;

				if (MiDebug.debug 
					&& MiDebug.isTracing(null, MiDebug.TRACE_EVENT_TO_COMMAND_TRANSLATION)
					&& (!getClass().getName().equals("com.swfm.mica.MiIDisplayTooltips")))
					{
					MiDebug.println("      return status: " 
						+ (status == Mi_CONSUME_EVENT 
						? "Mi_CONSUME_EVENT" : "Mi_PROPOGATE_EVENT"));
					}
				return(status);
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
					/**------------------------------------------------------
			 		 * Gets the name for the given event handler type. Possible
					 * return values are:
					 *	Mi_UNKNOWN_EVENT_HANDLER_TYPE_NAME 
					 *	Mi_ORDINARY_EVENT_HANDLER_TYPE_NAME
					 *	Mi_SHORT_CUT_EVENT_HANDLER_TYPE_NAME
					 *	Mi_MONITOR_EVENT_HANDLER_TYPE_NAME
					 *	Mi_GRAB_EVENT_HANDLER_TYPE_NAME
					 * @param type		the event handler type
					 * @return 		the name of the event handler type
					 *------------------------------------------------------*/
	public static	String		getNameForType(int type)
		{
		String typeName = "Unknown";
		if (type == Mi_ORDINARY_EVENT_HANDLER_TYPE)
			typeName = "Ordinary";
		else if (type == Mi_SHORT_CUT_EVENT_HANDLER_TYPE)
			typeName = "ShortCut";
		else if (type == Mi_MONITOR_EVENT_HANDLER_TYPE)
			typeName = "Monitor";
		else if (type == Mi_GRAB_EVENT_HANDLER_TYPE)
			typeName = "Grab";
		return(typeName);
		}
					/**------------------------------------------------------
	 				 * Processes the given command. 
					 * @param comamnd	The command to process
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 *------------------------------------------------------*/
	public		int		processCommand(String command)
		{
		// LastEvent does not have world point set
		if (event == null)
			event = new MiEvent();
		event.copy(MiEvent.getLastEvent());
		this.command = command;
		return(processCommand());
		}
					/**------------------------------------------------------
	 				 * Processes the given command as if generated from the
					 * given event. 
					 * @param comamnd	The command to process
					 * @param event		The event that the event handler
					 *			will get any needed positional
					 *			information
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 *------------------------------------------------------*/
	public		int		processCommand(MiEvent event, String command)
		{
		if (this.event == null)
			this.event = new MiEvent();
		this.event.copy(event);
		this.command = command;
		return(processCommand());
		}
					/**------------------------------------------------------
	 				 * Processes the stored command generated from the stored
					 * event.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			#isCommand
					 * @see			#setCommand
					 * @see			#getCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		return(Mi_PROPOGATE_EVENT);
		}
					/**------------------------------------------------------
	 				 * Sets the command to use.
					 * @param command	The command use
					 *------------------------------------------------------*/
	public		void		setCommand(String command)
		{
		this.command = command;
		}
					/**------------------------------------------------------
	 				 * Gets the command to use.
					 * @return 		The command use
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(command);
		}
					/**------------------------------------------------------
	 				 * Gets whether the given command is equal to the current
					 * command.
					 * @param cmd 		The given command
					 * @return 		true if the given command is the
					 *			current command
					 *------------------------------------------------------*/
	public		boolean		isCommand(String cmd)
		{
		return(cmd.equalsIgnoreCase(command));
		}
					/**------------------------------------------------------
			 		 * Gets an array of events that can be processed by this
					 * event handler.
					 * @return 		an array of events that this event
					 *			handler is interested in.
					 *------------------------------------------------------*/
	public		MiEvent[]	getRequestedEvents()
		{
		MiEvent[] events = new MiEvent[numTranslations];
		for (int i = 0; i < numTranslations; ++i)
			{
			events[i] = translationsTable[i].event;
			}
		return(events);
		}
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
	public		String[]	getRequestedCommands()
		{
		String[] cmds = new String[numTranslations];
		for (int i = 0; i < numTranslations; ++i)
			{
			cmds[i] = translationsTable[i].command;
			}
		return(cmds);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given
					 * value. This is useful for changing the events that
					 * triggers a command.
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @implements		MiiEventHandler#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		// Usefull?
		//if (name.equalsIgnoreCase(Mi_NAME_NAME))
			//setName(value);
		for (int i = 0; i < numTranslations; ++i)
			{
			if (translationsTable[i].command.equalsIgnoreCase(name))
				{
				if (MiEvent.stringToEvent(value, event))
					{
					translationsTable[i].event.type = event.type;
					translationsTable[i].event.key = event.key;
					translationsTable[i].event.modifiers = event.modifiers;
					}
				else
					{
					MiDebug.printlnError(this + "Unable to generate event from property:"
						+ " \"" + value + "\" for command: " + name);
					}
				return;
				}
			}
		}
					/**------------------------------------------------------
	 				 * Returns a copy of this MiEventHandler.
	 				 * @return 	 	the copy
					 * @implements		MiiEventHandler#copy
					 *------------------------------------------------------*/
	public		MiiEventHandler	copy()
		{
		try	{
			MiEventHandler other = (MiEventHandler )getClass().newInstance();
			other.enabled = enabled;
			other.locationSpecific = locationSpecific;
			other.alwaysConsumeEvents = alwaysConsumeEvents;
			other.target = null;
			other.targetsWindow = null;
			other.type = type;
			other.name = name;
			other.numTranslations = numTranslations;
			if (numTranslations != 0)
				{
				other.translationsTable = (MiEventToCommandTranslation[])translationsTable.clone();
				}
			return(other);
			}
		catch (java.lang.NoSuchMethodError e)
			{
			MiDebug.println("Copy failed (no constructor) for MiiEventHandler: " + this);
			e.printStackTrace();
			}
		catch (Exception e)
			{
			MiDebug.println("Copy failed for MiiEventHandler: " + this);
			e.printStackTrace();
			}
		return(null);
		}
					/**------------------------------------------------------
					 * Returns information about this MiEventHandler.
					 * @return		textual information (class name +
					 *			unique numerical id + [disabled])
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) + ".#" + instanceNumber 
			+ (enabled ? "" : "[disabled]"));
		}
	}

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Framework
 *
 *
 *
 *----------------------------------------------------------------------------------------------*/
class MiEventToCommandTranslation
	{
	public		MiEvent		event;
	public		String		command;

	public				MiEventToCommandTranslation()
		{
		}
	public		boolean		matchesEvent(MiEvent incomingEvent)
		{
		return(incomingEvent.equalsEventType(event));
		}
	public		String		toString()
		{
		return(super.toString() + ":maps event: " + event + " to command: " + command);
		}
	}

class MiEventToCommandTranslation2 extends MiEventToCommandTranslation
	{
	public		MiEvent		event2;
	public		MiEvent		lastEvent = new MiEvent();

	public				MiEventToCommandTranslation2()
		{
		}
	public		boolean		matchesEvent(MiEvent incomingEvent)
		{
		boolean matches = false;
		if (incomingEvent.type != MiEvent.Mi_TIMER_TICK_EVENT)
			{
			matches = incomingEvent.equalsEventType(event2) && lastEvent.equalsEventType(event);
			lastEvent.copyEventType(incomingEvent);


/*
System.out.println("\nevent = "  +event);
System.out.println("lastEvent = "  +lastEvent);
System.out.println("event2 = "  +event2);
System.out.println("incomingEvent = "  +incomingEvent);

if (matches)
System.out.println("MATCHES");
if (!incomingEvent.equalsEventType(event2))
System.out.println("Incoming NOT match event2");
if (!lastEvent.equalsEventType(event))
System.out.println("lastEvent NOT match event");
*/

			}
		return(matches);
		}	
	public		String		toString()
		{
		return("MiEventToCommandTranslation2@" + hashCode() + ":maps event: " + event2 
			+ " to command: " + command + ", if last event = " + event);
		}
	}


