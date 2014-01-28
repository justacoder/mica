
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

import java.util.Hashtable; 
import java.util.Enumeration; 
import java.util.Properties; 
import java.awt.Color; 
import java.awt.Image; 
import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.OrderedProperties;


/**----------------------------------------------------------------------------------------------
 * This class implements the fundamental functionality of all graphical 
 * parts of the Mica Graphics Framework. This includes event and action 
 * handlers, picking, bounds, area and layout validation, attributes,
 * drawing and much more.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiPart implements MiiTypes, MiiAttributeTypes, MiiActionTypes, MiiEventTypes, MiiNames, MiiDragAndDropParticipant, MiiPropertyTypes
	{
	private static final int		Mi_PARTIALLY_INVALID_AREA	= 1;
	private static final int		Mi_TOTALLY_INVALID_AREA		= 2;

	private static	MiAttributes		defaultAttributes		= new MiAttributes();
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private	static	int			numCreated			= 0;
	private static	Hashtable 		classSpecificCustomLookAndFeels;
	private static	Hashtable 		classHierarchies		= new Hashtable();
	private static	Hashtable 		attributeStateNames		= new Hashtable(11);
	public static 	int			activateActionMouseAppearance	= Mi_WAIT_CURSOR;

	private		String			name;
	private		MiAttributes		attributes;
	private		int			id;
	private 	MiBounds		bounds				= new MiBounds();
	private		MiBounds		drawBounds 			= bounds;
	private		MiPart[]		containers;
	private		boolean			visible				= true;
	private		boolean			actionDispatchingEnabled	= true;
	private		int			invalidArea			= Mi_TOTALLY_INVALID_AREA;
	private		MiSize			preferredSize;
	private		MiSize			minimumSize;
	private		MiAction		containersRequestedActions;
	private		MiAction		actionsRequestedFromParts;

	private		MiPartExtensions	extensions;
	private		FastVector		eventHandlers;
	private		FastVector		actionHandlers;
	private		MiAttachments		attachments;
	private		MiDrawManager		drawManager;
	private		MiManagedPointManager[]	managedPointManagers;

	static	{
		attributeStateNames.put("normal", "normal");
		attributeStateNames.put("insensitive", "insensitive");
		attributeStateNames.put("selected", "selected");
		attributeStateNames.put("keyboardFocus", "keyboardFocus");
		attributeStateNames.put("enterKeyFocus", "enterKeyFocus");
		attributeStateNames.put("mouseFocus", "mouseFocus");
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiPart. 
					 *------------------------------------------------------*/
	public				MiPart()
		{
		attributes = defaultAttributes;
		attributes.incNumUsing();
		id = numCreated++;
		setMiFlag(Mi_SENSITIVE_MASK
			+ Mi_INVALID_AREA_NOTIFICATIONS_ENABLED_MASK
			+ Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK
			+ Mi_OUT_GOING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK
			+ Mi_EVENT_HANDLING_ENABLED_MASK);

		}
					/**------------------------------------------------------
	 				 * Waits, if necessary, and obtains a lock on this MiPart.
					 * This should be done before any modifications to this
					 * part by a thread that is not a MiPartModifierThread
					 * or is not associated with the window of this part i.e.
					 * if (((MiPartModifierThread )Thread.currentThread()).getCanvas()
					 *      != this.getRootWindow().getCanvas())
					 * This lock allows access to all of the parts in this
					 * part's root window. This permits
					 * a caller to grab hold of this canvas and it's scene-
					 * graph using a thread that is not normally associated
					 * with this canvas. A single thread may have multiple
					 * locks on a single canvas.
					 * @see			#freeAccessLock
					 *------------------------------------------------------*/
	public	 	void		getAccessLock()
		{
		getRootWindow().getCanvas().getAccessLock();
		}
					/**------------------------------------------------------
	 				 * Free one lock that was obtained using getAccessLock.
					 * @see			#getAccessLock
					 *------------------------------------------------------*/
	public 		void		freeAccessLock()
		{
		getRootWindow().getCanvas().freeAccessLock();
		}
	//***************************************************************************************
	// Custom Look and Feel Management
	//***************************************************************************************
	public		void		applyCustomLookAndFeel()
		{
		if (classSpecificCustomLookAndFeels != null)
			{
			applyCustomLookAndFeel(getClass().getName());
			}
		if (extensions != null)
			{
			MiCustomLookAndFeels lafs = extensions.customLookAndFeels;
			if (lafs != null)
				{
				for (int i = 0; i < lafs.size(); ++i)
					lafs.elementAt(i).applyCustomLookAndFeel(this);
				}
			}
		}
	public 		void		setCustomLookAndFeels(MiCustomLookAndFeels laf)
		{
		if (extensions == null)
			{
			extensions = new MiPartExtensions(this);
			}
		extensions.customLookAndFeels = laf;
		}
	public 		MiCustomLookAndFeels	getCustomLookAndFeels()
		{
		if (extensions != null)
			{
			return(extensions.customLookAndFeels);
			}
		return(null);
		}
	public static	void		setCustomLookAndFeels(String className, MiCustomLookAndFeels laf)
		{
		if (classSpecificCustomLookAndFeels == null)
			{
			classSpecificCustomLookAndFeels = new Hashtable(5);
			}
		classSpecificCustomLookAndFeels.put(className, laf);
		}
	public static	MiCustomLookAndFeels	getCustomLookAndFeels(String className)
		{
		if (classSpecificCustomLookAndFeels != null)
			{
			return((MiCustomLookAndFeels )
				classSpecificCustomLookAndFeels.get(className));
			}
		return(null);
		}
	protected	void		applyCustomLookAndFeel(String className)
		{
		MiCustomLookAndFeels lafs = getCustomLookAndFeels(className);
		if (lafs != null)
			{
			for (int i = 0; i < lafs.size(); ++i)
				lafs.elementAt(i).applyCustomLookAndFeel(this);
			}
		}



					/**------------------------------------------------------
	 				 * Sets the value of the smallest pick area. This along with
					 * the global MiEvent.setPreferredMouseFootPrintWidth() sets
					 * the width of the pick area which is used to determine what
					 * lies under the mouse. This method is useful for MiEditors
					 * that contain connection lines that are hard to pick with
					 * single pixel mouse pick areas (a nice pick area width for
					 * average folks is 10).
	 				 * @param d 		the width and height of the minimum
					 *			pick area size
					 * @see			#getMinimumPickAreaSize
					 * @see			MiEvent#setPreferredMouseFootPrintWidth
					 *------------------------------------------------------*/
	public		void		setMinimumPickAreaSize(MiDistance d)
		{
		if (extensions == null)
			extensions = new MiPartExtensions(this);

		extensions.minimumPickAreaSize = d;
		}
					/**------------------------------------------------------
	 				 * Gets the value of the smallest pick area. This along with
					 * the global MiEvent.setPreferredMouseFootPrintWidth() sets
					 * the width of the pick area which is used to determine what
					 * lies under the mouse. This method is useful for MiEditors
					 * that contain connection lines that are hard to pick with
					 * single pixel mouse pick areas (a nice pick area width for
					 * average folks is 10).
	 				 * @return  		the width and height of the minimum
					 *			pick area size
					 * @see			#setMinimumPickAreaSize
					 * @see			MiEvent#setPreferredMouseFootPrintWidth
					 *------------------------------------------------------*/
	public		MiDistance	getMinimumPickAreaSize()
		{
		if (extensions == null)
			return(0);
		return(extensions.minimumPickAreaSize);
		}
		
	//***************************************************************************************
	// Resource Management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Sets (or adds if not already present) the value of
					 * the named resource. These resources are to be specified
					 * by the programmer - these are not built-in resources.
	 				 * @param name 		the name of the resource
	 				 * @param value 	the value of the resource
					 * @see			#getResource
					 * @see			#removeResource
					 *------------------------------------------------------*/
	public		void		setResource(String name, Object value)
		{
		if (extensions == null)
			extensions = new MiPartExtensions(this);

		extensions.setResource(name, value);
		}

					/**------------------------------------------------------
	 				 * Gets the value of the named resource. 
	 				 * @param name 		the name of the resource
	 				 * @return 	 	the value of the resource, or null
					 *			if no such resource exists
					 * @see			#setResource
					 *------------------------------------------------------*/
	public		Object		getResource(String name)
		{
		if (extensions == null)
			return(null);
		return(extensions.getResource(name));
		}
					/**------------------------------------------------------
	 				 * Removes the named resource. This is the same as setting
					 * the resource's value to null.
	 				 * @param name 		the name of the resource
					 * @see			#setResource
					 *------------------------------------------------------*/
	public		void		removeResource(String name)
		{
		if (extensions != null)
			extensions.removeResource(name);
		}
					/**------------------------------------------------------
	 				 * Gets the number of resources assigned to this MiPart. 
	 				 * @return 		the number of resources
					 * @see			#getResourceName
					 *------------------------------------------------------*/
	public		int		getNumberOfResources()
		{
		if (extensions != null)
			return(extensions.getNumberOfResources());
		return(0);
		}
					/**------------------------------------------------------
	 				 * Gets the name of the ith resource assigned to this 
					 * MiPart. 
	 				 * @param index		the index of the resource
	 				 * @return 		the name of the resource
					 * @exception		IllegalArgumentException If index
					 * 			is out of range.
					 * @see			#getNumberOfResources
					 *------------------------------------------------------*/
	public		String		getResourceName(int index)
		{
		if (extensions != null)
			return(extensions.getResourceName(index));

		throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
			+ ": No resources for this part.");
		}

	//***************************************************************************************
	// Layout Notification Management
	//***************************************************************************************
					/**------------------------------------------------------
	 				 * Specifies whether invalid area notifications are
					 * enabled. If not enabled, then changes to this MiPart
					 * will not cause the part to be redrawn.
	 				 * @param flag 		true if notifications are enabled
					 *------------------------------------------------------*/
	public		void		setInvalidAreaNotificationsEnabled(boolean flag)
		{
		setMiFlag(Mi_INVALID_AREA_NOTIFICATIONS_ENABLED_MASK, flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether invalid area notifications are enabled. 
	 				 * @return 		true if notifications are enabled
					 *------------------------------------------------------*/
	public		boolean		getInvalidAreaNotificationsEnabled()
		{
		return(hasMiFlag(Mi_INVALID_AREA_NOTIFICATIONS_ENABLED_MASK));
		}
					/**------------------------------------------------------
	 				 * Specifies whether invalid layout notifications are
					 * enabled. If not enabled, then (incoming) changes to 
					 * the size of this MiPart's parts will not invalidate any
					 * layout assigned to this MiPart AND any (outgoing) 
					 * changes to this MiPart's size will not cause the 
					 * layouts of any of this MiPart's containers to be 
					 * invalidated.
	 				 * @param flag 	true if notifications are enabled
					 * @see 	#getInvalidLayoutNotificationsEnabled
					 * @see 	#setIncomingInvalidLayoutNotificationsEnabled
					 * @see 	#setOutgoingInvalidLayoutNotificationsEnabled
					 *------------------------------------------------------*/
	public		void		setInvalidLayoutNotificationsEnabled(boolean flag)
		{
		setMiFlag(Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK, flag);
		setMiFlag(Mi_OUT_GOING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK, flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether invalid layouts notifications are enabled. 
	 				 * @return 		true if notifications are enabled
					 *------------------------------------------------------*/
	public		boolean		getInvalidLayoutNotificationsEnabled()
		{
		return(hasMiFlag(Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK) 
			|| hasMiFlag(Mi_OUT_GOING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK));
		}

					/**------------------------------------------------------
	 				 * Specifies whether invalid layout notifications are
					 * enabled. If not enabled, then (incoming) changes to 
					 * the size of this MiPart's parts will not invalidate any
					 * layout assigned to this MiPart.
	 				 * @param flag 	true if notifications are enabled
					 * @see 	#getInvalidLayoutNotificationsEnabled
					 * @see 	#setIncomingInvalidLayoutNotificationsEnabled
					 * @see 	#setOutgoingInvalidLayoutNotificationsEnabled
					 *------------------------------------------------------*/
	public		void		setIncomingInvalidLayoutNotificationsEnabled(boolean flag)
		{
		setMiFlag(Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK, flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether incoming invalid layouts notifications 
					 * are enabled. 
	 				 * @return 		true if notifications are enabled
					 *------------------------------------------------------*/
	public		boolean		getIncomingInvalidLayoutNotificationsEnabled()
		{
		return(hasMiFlag(Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK));
		}
					/**------------------------------------------------------
	 				 * Specifies whether invalid layout notifications are
					 * enabled. If not enabled, then any (outgoing) 
					 * changes to this MiPart's size will not cause the 
					 * layouts of any of this MiPart's containers to be 
					 * invalidated.
	 				 * @param flag 	true if notifications are enabled
					 * @see 	#getInvalidLayoutNotificationsEnabled
					 * @see 	#setIncomingInvalidLayoutNotificationsEnabled
					 * @see 	#setOutgoingInvalidLayoutNotificationsEnabled
					 *------------------------------------------------------*/
	public		void		setOutgoingInvalidLayoutNotificationsEnabled(boolean flag)
		{
		setMiFlag(Mi_OUT_GOING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK, flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether outgoing invalid layouts notifications 
					 * are enabled. 
	 				 * @return 		true if notifications are enabled
					 *------------------------------------------------------*/
	public		boolean		getOutgoingInvalidLayoutNotificationsEnabled()
		{
		return(hasMiFlag(Mi_OUT_GOING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK));
		}


	//***************************************************************************************
	// Life and Death and Reproduction Management
	//***************************************************************************************

	protected	MiPart		makeNewInstance()
						throws 	java.lang.InstantiationException,
							java.lang.IllegalAccessException
		{
		return((MiPart )getClass().newInstance());
		}
					/**------------------------------------------------------
	 				 * Returns a copy of this MiPart. The copy has the same
					 * attributes, bounds, resources, attachments, layouts,
					 * action handlers, and event handlers. A Mi_COPY_ACTION
					 * is generated and dispatched to this MiPart (not the 
					 * copy).
					 * <p>
					 * MiPart theNewCopy = (MiPart )action.getActionSystemInfo();
					 * MiPart theCopied = action.getActionSource();
					 * <p>
					 * In addition a Mi_COPY_ACTION | Mi_EXECUTE_ACTION_PHASE
					 * action is sent to the 'copied' MiPart. This allows the
					 * application to perform the copy itself:
					 * <p>
					 * MiPart[] theNewCopy = (MiPart[] )action.getActionSystemInfo();
					 * MiPart theCopied = action.getActionSource();
					 * ...
					 * semantics = ((Semantics )theCopied.getResource("semantics")).copy();
					 * ...
					 * theNewCopy[0] = semantics.getGraphics();
					 * return(true);
					 * <p>
					 * This should be rarely overridden as the overridding method
					 * cannot tell whether the copy was performed by a MiiActionHandler
					 * as described above (so all data in the copy has been copied 
					 * and adjusted already). 
					 * <p>
	 				 * @return 	 	the copy
					 * @see			#deepCopy
					 *------------------------------------------------------*/
	public		MiPart		copy()
		{
		try	{
			MiPart[] theCopy = new MiPart[1];
			dispatchAction(Mi_COPY_ACTION | Mi_EXECUTE_ACTION_PHASE, theCopy);

			MiPart obj = theCopy[0];
			if (obj == null)
				{
				obj = makeNewInstance();
				obj.copy(this);
				}

			// FIX: this makes sense now that copy over event handlers as well...
			// obj.dispatchAction(Mi_CREATE_ACTION);
			dispatchAction(Mi_COPY_ACTION, obj);
			return(obj);
			}
		catch (java.lang.NoSuchMethodError e)
			{
			MiDebug.println("Copy failed (no public default constructor) for MiPart: " + this);
			e.printStackTrace();
			}
		catch (java.lang.IllegalAccessException e)
			{
			MiDebug.println("Copy failed (no public default constructor or class not public) for MiPart: " + this);
			e.printStackTrace();
			}
		catch (java.lang.InstantiationException e)
			{
			MiDebug.println("Copy failed (no public default constructor) for MiPart: " + this);
			e.printStackTrace();
			}
		catch (Exception e)
			{
			MiDebug.println("Copy failed for MiPart: " + this);
			e.printStackTrace();
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Copies the given MiPart. This MiPart will have the same
					 * attributes, bounds, resources, attachments, layouts,
					 * action handlers, and event handlers as the given MiPart. 
					 * Override this, if necessary, as it is used by the copy()
					 * method as well.
	 				 * @param obj 	 	the part to copy
					 * @see			#copy
					 * @see			#deepCopy
					 *------------------------------------------------------*/
	public		void		copy(MiPart obj)
		{
		setName(obj.name);
		setAttributes(obj.getAttributes());
		visible = obj.visible;

		bounds.copy(obj.bounds);
		if (obj.drawBounds != obj.bounds)
			{
			if (drawBounds == bounds)
				drawBounds = MiBounds.newBounds();
			drawBounds.copy(obj.drawBounds);
			}
		else
			{
			if (drawBounds != bounds)
				MiBounds.freeBounds(drawBounds);
			drawBounds = bounds;
			}

		extensions = (obj.extensions == null) ? null : obj.extensions.copy(this);


		setMiFlag(Mi_DEEPLY_INVALIDATE_AREAS_MASK, obj.hasMiFlag(Mi_DEEPLY_INVALIDATE_AREAS_MASK));

		if (obj.hasMiFlag(Mi_PREFERRED_SIZE_OVERRIDDEN_MASK))
			{
			setMiFlag(Mi_PREFERRED_SIZE_OVERRIDDEN_MASK);
			preferredSize = obj.preferredSize.copy();
			}
		if (obj.hasMiFlag(Mi_MINIMUM_SIZE_OVERRIDDEN_MASK))
			{
			setMiFlag(Mi_MINIMUM_SIZE_OVERRIDDEN_MASK);
			minimumSize = obj.minimumSize.copy();
			}

		if (obj.managedPointManagers != null)
			{
			managedPointManagers = new MiManagedPointManager[obj.managedPointManagers.length];
			for (int i = 0; i < obj.managedPointManagers.length; ++i)
				{
				managedPointManagers[i] = obj.managedPointManagers[i];
				if (managedPointManagers[i] != null)
					{
					// Manager is shared by >1 part, it is now copy on write
					managedPointManagers[i].setIsMutable(false);
					setMiFlag(Mi_HAS_CUSTOM_RENDERERS_MASK);
					}
				}
			}
		else
			{
			managedPointManagers = null;
			}

		if (obj.attachments != null)
			{
			attachments = (MiAttachments )obj.attachments.deepCopy();
			attachments.setAttachedToObject(this);
			}
		else if (attachments != null)
			{
			attachments.setAttachedToObject(null);
			attachments = null;
			}
		if (obj.getLayout() != null)
			setLayout((MiiLayout )obj.getLayout().makeCopy());
		else
			setLayout(null);

		removeAllActionHandlers();
		if (obj.actionHandlers != null)
			{
			for (int i = 0; i < obj.actionHandlers.size(); ++i)
				appendActionHandler((MiiAction )obj.actionHandlers.elementAt(i));
			}
		removeAllEventHandlers();
		if (obj.eventHandlers != null)
			{
			for (int i = 0; i < obj.eventHandlers.size(); ++i)
				{
				MiiEventHandler handler = (MiiEventHandler )obj.eventHandlers.elementAt(i);
				if(!handler.isSingleton())
					{
					handler = handler.copy();
					}
				appendEventHandler(handler);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Makes and returns a copy of this MiPart and all of it's
					 * parts.
	 				 * @return 	 	the copy
					 * @see			#copy
					 *------------------------------------------------------*/
	public		MiPart		deepCopy()
		{
		return(copy());
		}
					/**------------------------------------------------------
	 				 * Replaces this MiPart with the given MiPart. This 
					 * includes putting the given part in the same location,
					 * at the same index in all of it's containers, and to take
					 * it's place in all of it's connections. A Mi_REPLACE_ACTION
                                         * is generated and dispatched to this MiPart (not the
					 * given part).
	 				 * @param other  	the part that will replace this one
					 *------------------------------------------------------*/
	public		void		replaceSelf(MiPart other)
		{
		if (other == this)
			throw new IllegalArgumentException(this + ": Replacing object with itself");

		// Do this before detaching from containers so that the replace action
		// can be observed by any interested containers
		dispatchAction(Mi_REPLACE_ACTION, other);

		boolean wasShowing = (!isHidden()) && (visible) && (isShowing(null));

		removeAllManipulators();
		other.setCenter(getCenter());
		while (getNumberOfContainers() > 0)
			{
			int index = getContainer(0).getIndexOfPart(this);
			getContainer(0).setPart(other, index);
			}

		while (getNumberOfConnections() > 0)
			{
			if (getConnection(0).getSource() == this)
				getConnection(0).setSource(other);
			else 
				getConnection(0).setDestination(other);
			}
		nowShowing(false);
		other.nowShowing(wasShowing);
		}
					/**------------------------------------------------------
	 				 * Removes this MiPart from all of it's containers and
					 * detaches this MiPart from all of it's connections, who
					 * are also detached from their endpoints and from their
					 * containers.
					 *------------------------------------------------------*/
	public		void		removeSelf()
		{
		invalidateArea();
		removeFromAllContainers();
		MiParts connections = getDeepConnections(new MiParts());
		for (int i = connections.size() - 1; i >= 0; --i)
			{
			MiConnection conn = (MiConnection )connections.elementAt(i);
			// Remove all connections that are not 'internal' to this part.
			if ((!isContainerOf(conn.getSource())) || (!isContainerOf(conn.getDestination())))
				{
				if (conn.getConnectionsMustBeConnectedAtBothEnds())
					{
					conn.setSource(null);
					conn.setDestination(null);
					conn.removeSelf();
					}
				else 
					{
					if (!isContainerOf(conn.getSource()))
						{
						conn.setDestination(null);
						}
					else
						{
						conn.setSource(null);
						}
					}
				}
			}
		}
					/**------------------------------------------------------
	 				 * Removes this MiPart from all of it's containers. 
					 *------------------------------------------------------*/
	public		void		removeFromAllContainers()
		{
		invalidateArea();
		if (containers != null)
			{
			while (containers != null)
				{
				if (containers[0] instanceof MiAttachments)
					((MiAttachments )containers[0]).removeAttachment(this);
				else
					containers[0].removeItem(this);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Removes this MiPart from all of it's containers, parts
					 * and connections after doing the same to all of this 
					 * MiPart's parts. A Mi_DELETE_ACTION is generated and 
					 * dispatched to this MiPart immediately before it is
					 * deleted.
					 *------------------------------------------------------*/
	public		void		deleteSelf()
		{
		invalidateArea();

		// Do this before detaching from containers so that the delete action
		// can be observed by any interested containers
		dispatchAction(Mi_DELETE_ACTION);

		deleteAllConnections();
		deleteAllParts();
		
		removeAllManipulators();

		removeFromAllContainers();
		}
					/**------------------------------------------------------
	 				 * Removes all part manipulators and layout manipulators,
					 * if any, assigned to this MiPart.
					 *------------------------------------------------------*/
	public		void		removeAllManipulators()
		{
		MiiManipulator manipulator;
		while ((manipulator = getManipulator()) != null)
			{
			manipulator.removeFromTarget();
			}
		MiiLayoutManipulator layoutManipulator;
		while ((layoutManipulator = getLayoutManipulator()) != null)
			{
			layoutManipulator.removeFromTarget();
			}
		}
					/**------------------------------------------------------
	 				 * Recursively delete all parts (and their parts, ...) of 
					 * this MiPart.
					 *------------------------------------------------------*/
	public 		void		deleteAllParts()
		{
		while (getNumberOfParts() > 0)
			{
			MiPart part = getPart(0);
			part.deleteAllParts();
			part.deleteSelf();
			}
		}
					/**------------------------------------------------------
	 				 * Recursively delete all connections of this MiPart (and
					 * it's parts, ...) of the given container. 
					 *------------------------------------------------------*/
	public 		void		deleteAllConnections()
		{
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiPart part = getPart(i);
			if (part.getNumberOfParts() > 0)
				part.deleteAllConnections();
			while (part.getNumberOfConnections() > 0)
				part.getConnection(0).deleteSelf();
			}
		while (getNumberOfConnections() > 0)
			getConnection(0).deleteSelf();
		}

	//***************************************************************************************
	// Deep Connection Management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Returns the number of connections assigned to this
					 * MiPart and all of it's tree of parts.
					 * @return 		number of connections
					 * @see			#getDeepConnection
					 *------------------------------------------------------*/
	public		int		getNumberOfDeepConnections()
		{
		int num = getNumberOfConnections();
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			num += getPart(i).getNumberOfDeepConnections();
			}
		return(num);
		}
					/**------------------------------------------------------
	 				 * Returns the ith connection assigned to this MiPart 
					 * and all of it's tree of parts.
					 * @param index 	the index number of the connection
					 * @return 		the connection
					 * @see			#getNumberOfConnections
					 * @see			#getDeepConnections
					 *------------------------------------------------------*/
	public		MiConnection	getDeepConnection(int index)
		{
		int[] adjustedIndex = new int[1];
		adjustedIndex[0] = index;
		return(getDeepConnection(adjustedIndex));
		}
					/**------------------------------------------------------
	 				 * Returns the ith connection assigned to this MiPart 
					 * and all of it's tree of parts.
					 * @return 		the connection
					 * @see			#getNumberOfConnections
					 * @see			#getDeepConnections
					 *------------------------------------------------------*/
	public		MiConnection	getDeepConnection(int[] index)
		{
		int num = getNumberOfConnections();
		if (index[0] < num)
			return(getConnection(index[0]));
		index[0] -= num;
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiConnection conn = getPart(i).getDeepConnection(index);
			if (conn != null)
				return(conn);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Returns a list of all connections assigned to this MiPart 
					 * and all of it's tree of parts.
					 * @param deepConnections the list to fill
					 * @return 		the list of connections
					 * @see			#getNumberOfConnections
					 * @see			#getDeepConnection
					 *------------------------------------------------------*/
	public		MiParts		getDeepConnections(MiParts deepConnections)
		{
		if ((extensions != null) && (extensions.connections != null))
			deepConnections.append(extensions.connections);
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			getPart(i).getDeepConnections(deepConnections);
			}
		return(deepConnections);
		}

	//***************************************************************************************
	// Name management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Return the name of this MiPart. The default name is
					 * null.
					 * @return		the name
					 * @see 		#setName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(name);
		}
					/**------------------------------------------------------
	 				 * Set the name of this MiPart.
					 * @param n		the name
					 * @see 		#getName
					 *------------------------------------------------------*/
	public		void		setName(String n)
		{
		if (!hasMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK))
			{
			name = n;
			}
		else if (validatePropertyValue(Mi_NAME_NAME, n) == null)
			{
			String oldValue = name;
			name = n;
			propertyChanged(Mi_NAME_NAME, n, oldValue);
			dispatchAction(Mi_NAME_CHANGE_ACTION);
			}
		}
					/**------------------------------------------------------
					 * Returns information about this MiPart.
					 * @return		textual information (class name +
					 *			unique numerical id + name)
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) + "#" + id + "." + name);
		}

	//***************************************************************************************
	// Drag and Drop management
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets whether this MiPart can participate in a drag and 
					 * drop operation as a data source.
					 * @return		true if a drag and drop data source
					 *------------------------------------------------------*/
	public		boolean		isDragAndDropSource()	
		{ 	return(attributes.getBooleanAttribute(Mi_DRAG_AND_DROP_SOURCE));	}
					/**------------------------------------------------------
					 * Sets whether this MiPart can participate in a drag and 
					 * drop operation as a data source.
					 * @param flag		true if a drag and drop data source
					 *------------------------------------------------------*/
	public		void		setIsDragAndDropSource(boolean flag)
		{ 	attributes.setAttributeForObject(this, Mi_DRAG_AND_DROP_SOURCE, flag); 	}
					/**------------------------------------------------------
					 * Gets whether this MiPart can participate in a drag and 
					 * drop operation as a data target.
					 * @return		true if a drag and drop data target
					 *------------------------------------------------------*/
	public		boolean		isDragAndDropTarget()
		{ 	return(attributes.getBooleanAttribute(Mi_DRAG_AND_DROP_TARGET));	}
					/**------------------------------------------------------
					 * Sets whether this MiPart can participate in a drag and 
					 * drop operation as a data destination.
					 * @param flag		true if a drag and drop data target
					 *------------------------------------------------------*/
	public		void		setIsDragAndDropTarget(boolean flag)
		{	attributes.setAttributeForObject(this, Mi_DRAG_AND_DROP_TARGET, flag); 	}

					/**------------------------------------------------------
					 * Gets description of any override of the drag and drop 
					 * operation behavior as specified by this MiParts
					 * containers.
					 * @return		the behavior assigned to this
					 *			MiPart or null.
					 *------------------------------------------------------*/
	public		MiiDragAndDropBehavior	getDragAndDropBehavior()
		{ return(extensions == null ? null : extensions.dndBehavior); }
					/**------------------------------------------------------
					 * Sets description which overrides the drag and drop 
					 * operation behavior as specified by this MiParts
					 * containers.
					 * @param behavior	the behavior to be assigned to 
					 * 			this MiPart or null.
					 *------------------------------------------------------*/
	public		void		setDragAndDropBehavior(MiiDragAndDropBehavior behavior)
		{
		if (extensions == null)
			extensions = new MiPartExtensions(this);
		extensions.dndBehavior = behavior;
		}
					/**------------------------------------------------------
					 * Imports data as specified.
					 * @param transfer	specifies the type and content of
					 *			the data to import.
					 *------------------------------------------------------*/
	public		void		doImport(MiDataTransferOperation transfer)
		{
		}
					/**------------------------------------------------------
					 * Exports data as specified.
					 * @param format	specifies the format of the data
					 *			this MiPart is to export.
					 * @return		The exported data.
					 *------------------------------------------------------*/
	public		Object 		doExport(String format)
		{
		if (format.equals(Mi_MiPART_FORMAT))
			return(deepCopy());
		return(toString());
		}
					/**------------------------------------------------------
					 * Gets list of data formats that this MiPart is able to
					 * import. This is used during drag and drop operations
					 * in order to match data formats between the source and
					 * target. This list should be in order of most preferred
					 * to least preferred.
					 * @return		array of data formats or null (the
					 *			default).
					 * @see			#doImport
					 * @see			#setSupportedImportFormats
					 * @see			#getSupportedExportFormats
					 *------------------------------------------------------*/
	public		String[] 	getSupportedImportFormats()
		{
		return(extensions == null ? null : extensions.supportedImportFormats);
		}
					/**------------------------------------------------------
					 * Gets list of data formats that this MiPart is able to
					 * import. This is used during drag and drop operations
					 * in order to match data formats between the source and
					 * target. This list should be in order of most preferred
					 * to least preferred.
					 * @return		array of data formats or null (the
					 *			default).
					 * @see			#doImport
					 * @see			#getSupportedImportFormats
					 * @see			#getSupportedExportFormats
					 *------------------------------------------------------*/
	public		void		setSupportedImportFormats(String[] formats)
		{
		if (extensions == null)
			extensions = new MiPartExtensions(this);
		extensions.supportedImportFormats = formats;
		}
					/**------------------------------------------------------
					 * Gets list of data formats that this MiPart is able to
					 * export. This is used during drag and drop operations
					 * in order to match data formats between the source and
					 * target. This list should be in order of most preferred
					 * to least preferred. The default supported export
					 * formats are Mi_MiPART_FORMAT and Mi_STRING_FORMAT.
					 * @return		array of data formats
					 * @see			#doExport
					 * @see			#getSupportedImportFormats
					 *------------------------------------------------------*/
	public		String[] 	getSupportedExportFormats()
		{
		String[] formats = new String[2];
		formats[0] = Mi_MiPART_FORMAT;
		formats[1] = Mi_STRING_FORMAT;
		return(formats);
		}
					/**------------------------------------------------------
					 * Gets whether the import of the specific data is valid.
					 * This is to allow the target to refuse import of data
					 * even though the data may be in a valid format (which
					 * has been validated before this method is called).
					 * @param transfer	specifies the type and content of
					 *			the data to import.
					 * @return		true (the default) if supports 
					 *			import
					 *------------------------------------------------------*/
	public		boolean 	supportsImportOfSpecificInstance(MiDataTransferOperation transfer)
		{
		return(true);
		}

	//***************************************************************************************
	// Attribute management
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the attributes that are assigned to this MiPart.
					 * @return		the attributes
					 *------------------------------------------------------*/
	public		MiAttributes	getAttributes()
		{
		return(attributes);
		}
					/**------------------------------------------------------
					 * Assigns the given attributes to this MiPart.
					 * @param atts		the attributes
					 *------------------------------------------------------*/
	public		void		setAttributes(MiAttributes atts)
		{
		if (attributes != atts)
			{
/* Check to see if this was just a change in inheritance of one of the attributes...?
if (attributes.equalsIgnoreOverrides(atts))
	{
	}
*/

			MiChangedAttributes changedAttributes = null;
			if (hasMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK))
				{
				changedAttributes = getValidatedChangedAttributes(attributes, atts);
				atts = changedAttributes.newAttributes;
				}
			if (attributes != atts)
				{
				boolean geomChange = attributes.isGeometricChange(atts);

				// Invalidate area even if geom change cause bounds may not always
				// change but appearance might
				if (geomChange || attributes.isAppearanceChange(atts))
					invalidateArea();

				attributes.decNumUsing();
				attributes = atts;
				attributes.incNumUsing();

				updateHasRenderersFlag();
				if (geomChange)
					refreshBounds();
			
				appearanceChanged();
				if (hasMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK))
					dispatchChangedAttributeActions(changedAttributes);
				}
			}
		}
					/**------------------------------------------------------
					 * Overrides each attribute of this MiPart that has been
					 * set in the given attributes.
					 * @param atts		the attributes
					 *------------------------------------------------------*/
	public		void		overrideAttributes(MiAttributes atts)
		{
		if (attributes != atts)
			{
			setAttributes((MiAttributes )attributes.overrideFromPermanent2(atts));
			}
		}
					/**------------------------------------------------------
					 * Gets the attributes that are assigned to all newly
					 * created MiParts.
					 * @return		the default attributes
					 *------------------------------------------------------*/
	static	public	MiAttributes	getDefaultAttributes()
		{
		return(defaultAttributes);
		}
					/**------------------------------------------------------
					 * Sets the attributes that are assigned to all newly
					 * created MiParts.
					 * @param atts		the default attributes
					 *------------------------------------------------------*/
	static	public	void		setDefaultAttributes(MiAttributes atts)
		{
		defaultAttributes = atts;
		}
					/**------------------------------------------------------
					 * Sets the attribute with the given name to the given
					 * value. Valid names are found in the 
					 * MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setAttributeValue(String name, Object value)
		{
		attributes.setAttributeValue(this, name, value);
		refreshBounds();
		}
					/**------------------------------------------------------
					 * Sets the attribute with the given name to the given
					 * value. Valid names are found in the 
					 * MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setAttributeValue(String name, String value)
		{
		attributes.setAttributeValue(this, name, value);
		refreshBounds();
		}
					/**------------------------------------------------------
					 * Sets the attribute with the given name to the given
					 * value. Valid names are found in the 
					 * MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setAttributeValue(String name, int value)
		{
		attributes.setAttributeValue(this, name, value);
		refreshBounds();
		}
					/**------------------------------------------------------
					 * Sets the attribute with the given name to the given
					 * value. Valid names are found in the 
					 * MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setAttributeValue(String name, double value)
		{
		attributes.setAttributeValue(this, name, value);
		refreshBounds();
		}
					/**------------------------------------------------------
					 * Sets the attribute with the given name to the given
					 * value. Valid names are found in the 
					 * MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setAttributeValue(String name, boolean value)
		{
		attributes.setAttributeValue(this, name, value);
		refreshBounds();
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given
					 * value. If no such property is found then sets the attribute
					 * with the given name to the given value. Valid attribute 
					 * names are found in the MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		int index = name.indexOf('.');
		if (index != -1)
			{
			String targetName = name.substring(0, index);
			String attName = name.substring(index + 1);
			MiPart p = isContainerOf(targetName);
			if (p != null)
				{
				p.setPropertyValue(attName, value);
				return;
				}
			throw new IllegalArgumentException(this + ":\"" 
				+ name + "\" is not a valid name of an property");
			}
		if (name.equalsIgnoreCase(Mi_NAME_NAME))
			setName(value);
		else if (name.equalsIgnoreCase(Mi_X_COORD_NAME))
			setCenterX(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_Y_COORD_NAME))
			setCenterY(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_WIDTH_NAME))
			setWidth(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_HEIGHT_NAME))
			setHeight(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_XMIN_NAME))
			setXmin(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_YMIN_NAME))
			setYmin(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_XMAX_NAME))
			setXmax(Utility.toDouble(value));
		else if (name.equalsIgnoreCase(Mi_YMAX_NAME))
			setYmax(Utility.toDouble(value));

		else if (name.equalsIgnoreCase(Mi_TOOL_HINT_HELP_ATT_NAME))
			setToolHintMessage(value);
		else if (name.equalsIgnoreCase(Mi_BALLOON_HELP_ATT_NAME))
			setBalloonHelpMessage(value);
		else if (name.equalsIgnoreCase(Mi_STATUS_HELP_ATT_NAME))
			setStatusHelpMessage(value);
		else if (name.equalsIgnoreCase(Mi_DIALOG_HELP_ATT_NAME))
			setDialogHelpMessage(value);

		else if (name.equalsIgnoreCase(Mi_VISIBLE_NAME))
			setVisible(Utility.toBoolean(value));

		else if (MiSystem.getCustomLookAndFeelManager()
				.setCustomLookAndFeelPropertyValue(this, name, value))
			{
			}
		else
			{
			attributes.setAttributeValue(this, name, value);
			//refreshBounds();
			}
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		int index = name.indexOf('.');
		if (index != -1)
			{
			String targetName = name.substring(0, index);
			String attName = name.substring(index + 1);
			MiPart p = isContainerOf(targetName);
			if (p != null)
				{
				return(p.getPropertyValue(attName));
				}
			throw new IllegalArgumentException(this + ":\"" 
				+ name + "\" is not a valid name of an property");
			}
		if (name.equalsIgnoreCase(Mi_NAME_NAME))
			return(getName());
		else if (name.equalsIgnoreCase(Mi_X_COORD_NAME))
			return(Utility.toShortString(getCenterX()));
		else if (name.equalsIgnoreCase(Mi_Y_COORD_NAME))
			return(Utility.toShortString(getCenterY()));
		else if (name.equalsIgnoreCase(Mi_WIDTH_NAME))
			return(Utility.toShortString(getWidth()));
		else if (name.equalsIgnoreCase(Mi_HEIGHT_NAME))
			return(Utility.toShortString(getHeight()));

		else if (name.equalsIgnoreCase(Mi_XMIN_NAME))
			return(Utility.toShortString(getXmin()));
		else if (name.equalsIgnoreCase(Mi_YMIN_NAME))
			return(Utility.toShortString(getYmin()));
		else if (name.equalsIgnoreCase(Mi_XMAX_NAME))
			return(Utility.toShortString(getXmax()));
		else if (name.equalsIgnoreCase(Mi_YMAX_NAME))
			return(Utility.toShortString(getYmax()));

		else if (name.equalsIgnoreCase(Mi_TOOL_HINT_HELP_ATT_NAME))
			return(getToolHintHelp(null) != null ? getToolHintHelp(null).getMessage() : Mi_NULL_VALUE_NAME);
		else if (name.equalsIgnoreCase(Mi_BALLOON_HELP_ATT_NAME))
			return(getBalloonHelp(null) != null ? getBalloonHelp(null).getMessage() : Mi_NULL_VALUE_NAME);
		else if (name.equalsIgnoreCase(Mi_STATUS_HELP_ATT_NAME))
			return(getStatusHelp(null) != null ? getStatusHelp(null).getMessage() : Mi_NULL_VALUE_NAME);
		else if (name.equalsIgnoreCase(Mi_DIALOG_HELP_ATT_NAME))
			return(getDialogHelp(null) != null ? getDialogHelp(null).getMessage() : Mi_NULL_VALUE_NAME);

		else if (name.equalsIgnoreCase(Mi_VISIBLE_NAME))
			return("" + isVisible());

		else 
			{
			String value = MiSystem.getCustomLookAndFeelManager()
						.getCustomLookAndFeelPropertyValue(this, name);
			if (value != null)
				return(value);
			return(attributes.getAttributeValue(name));
			}
		}
					/**------------------------------------------------------
					 * Returns whether the property with the given name is
					 * valid. If not, this then checks to see whether an attribute
					 * exsits with the given name.
					 * @param name		the name of an property or attribute
					 * @return 		true if the name is valid.
					 *------------------------------------------------------*/
	public		boolean		hasProperty(String name)
		{
		return(getPropertyDescriptions().contains(name));
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = attributes.getPropertyDescriptions();

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_NAME_NAME, Mi_STRING_TYPE, null));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_X_COORD_NAME, Mi_DOUBLE_TYPE, "0", Mi_X_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_Y_COORD_NAME, Mi_DOUBLE_TYPE, "0", Mi_Y_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_WIDTH_NAME, Mi_POSITIVE_DOUBLE_TYPE, "0", Mi_X_DISTANCE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_HEIGHT_NAME, Mi_POSITIVE_DOUBLE_TYPE, "0", Mi_Y_DISTANCE_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_XMIN_NAME, Mi_DOUBLE_TYPE, "0", Mi_X_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_YMIN_NAME, Mi_DOUBLE_TYPE, "0", Mi_Y_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_XMAX_NAME, Mi_DOUBLE_TYPE, "0", Mi_X_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_YMAX_NAME, Mi_DOUBLE_TYPE, "0", Mi_Y_COORD_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_VISIBLE_NAME, Mi_BOOLEAN_TYPE, "true"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_TOOL_HINT_HELP_ATT_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_BALLOON_HELP_ATT_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_STATUS_HELP_ATT_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_DIALOG_HELP_ATT_NAME, Mi_STRING_TYPE));

		propertyDescriptions = new MiPropertyDescriptions("MiPart", propertyDescriptions);
		propertyDescriptions.setDisplayName("Properties");
		return(propertyDescriptions);
		}
	public		void		setPropertyValues(Properties properties)
		{
		for (Enumeration e = properties.keys(); e.hasMoreElements();)
			{
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);
			setPropertyValue(key, value);
			}
		}
	public		void		refreshLookAndFeel()
		{
		refreshLookAndFeel(true);
		}
	public		void		refreshLookAndFeel(boolean savePreviousLookAndFeel)
		{
		Strings classNames = (Strings )classHierarchies.get(getClass());
		if (classNames == null)
			{
			classNames = Utility.getClassHierarchy(getClass());
			classHierarchies.put(getClass(), classNames);
			}
		for (int i = 0; i < classNames.size(); ++i)
			{
			String className = classNames.elementAt(i);
			OrderedProperties properties = MiSystem.getPropertiesForClass(className);
			if (properties.size() > 0)
				{
				Strings keys = properties.getKeys();
				for (int j = 0; j < keys.size(); ++j)
					{
					String name = keys.elementAt(j);
					int index = name.indexOf('.');
					if (index != -1)
						{
						String targetName = name.substring(0, index);
						if (attributeStateNames.get(targetName) == null)
							{
							String attName = name.substring(index + 1);
							MiPart p = isContainerOf(targetName);
							if (p != null)
								{
								if (savePreviousLookAndFeel)
									{
									String value = p.getPropertyValue(attName);
									MiSystem.getThemeManager()
										.setPreviousThemeProperty(
										this, attName, value);
									}
								p.setPropertyValue(
									attName, properties.getProperty(name));
								continue;
								}
/***
							else
								{
								MiDebug.println(
									"Warning: in look-and-feel property: \"" 
									+ name + "\" No part found with name: " 
									+ targetName);
								continue;
								}
***/
							}
						}
					try	{
						if (savePreviousLookAndFeel)
							{
							String value = getPropertyValue(name);
							MiSystem.getThemeManager()
								.setPreviousThemeProperty(this, name, value);
							}
						setPropertyValue(name, properties.getProperty(name));
						}
					catch (IllegalArgumentException e)
						{
						//e.printStackTrace();
/***
						MiDebug.printStackTrace(e);
						MiDebug.println(this + ": Warning: No property found with name: \"" + name + "\"");
						if (index != -1)
							{
							String targetName = name.substring(0, index);
							MiDebug.println(this 
								+ ": Warning: No sub-part found with name: \"" + targetName + "\" either");
							MiDebug.dump(this);
							}
***/
						}
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Gets the textual value of the attribute with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned. Valid names 
					 * are found in the MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @return 		the string value of the attribute
					 *------------------------------------------------------*/
	public		String		getAttributeValue(String name)
		{
		return(attributes.getAttributeValue(name));
		}
					/**------------------------------------------------------
					 * Returns whether the attribute with the given name is
					 * valid. Valid names are found in the 
					 * MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @return 		true if the name is valid.
					 *------------------------------------------------------*/
	public		boolean		hasAttribute(String name)
		{
		return(attributes.hasAttribute(name));
		}
					/**------------------------------------------------------
					 * Returns true if this MiPart can be deleted.
					 *------------------------------------------------------*/
	public		boolean		isDeletable()
		{ return(attributes.getBooleanAttribute(Mi_DELETABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart can be moved.
					 *------------------------------------------------------*/
	public		boolean		isMovable()
		{ return(attributes.getBooleanAttribute(Mi_MOVABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart can be copied.
					 *------------------------------------------------------*/
	public		boolean		isCopyable()
		{ return(attributes.getBooleanAttribute(Mi_COPYABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart can be copied as part of a part that is being deepcopy'd.
					 *------------------------------------------------------*/
	public		boolean		isCopyableAsPartOfCopyable()
		{ return(attributes.getBooleanAttribute(Mi_COPYABLE_AS_PART_OF_COPYABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart can be selected.
					 *------------------------------------------------------*/
	public		boolean		isSelectable()
		{ return(attributes.getBooleanAttribute(Mi_SELECTABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart has a constant width.
					 *------------------------------------------------------*/
	public		boolean		hasFixedWidth()
		{ return(attributes.getBooleanAttribute(Mi_FIXED_WIDTH)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart has a constant height.
					 *------------------------------------------------------*/
	public		boolean		hasFixedHeight()
		{ return(attributes.getBooleanAttribute(Mi_FIXED_HEIGHT)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart has constant height/width ratio.
					 *------------------------------------------------------*/
	public		boolean		hasFixedAspectRatio()
		{ return(attributes.getBooleanAttribute(Mi_FIXED_ASPECT_RATIO)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart can be picked.
					 * @see 		#isPickableWhenTransparent
					 *------------------------------------------------------*/
	public		boolean		isPickable()
		{ return(attributes.getBooleanAttribute(Mi_PICKABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart can be ungrouped.
					 *------------------------------------------------------*/
	public		boolean		isUngroupable()
		{ return(attributes.getBooleanAttribute(Mi_UNGROUPABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart can be connected to.
					 *------------------------------------------------------*/
	public		boolean		isConnectable()
		{ return(attributes.getBooleanAttribute(Mi_CONNECTABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart accepts mouse focus.
					 *------------------------------------------------------*/
	public		boolean		isAcceptingMouseFocus()
		{ return(attributes.getBooleanAttribute(Mi_ACCEPTS_MOUSE_FOCUS)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart accepts keyboard focus.
					 *------------------------------------------------------*/
	public		boolean		isAcceptingKeyboardFocus()
		{ return(attributes.getBooleanAttribute(Mi_ACCEPTS_KEYBOARD_FOCUS)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart accepts enter-key focus.
					 *------------------------------------------------------*/
	public		boolean		isAcceptingEnterKeyFocus()
		{ return(attributes.getBooleanAttribute(Mi_ACCEPTS_ENTER_KEY_FOCUS)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart accepts Tab keys.
					 *------------------------------------------------------*/
	public		boolean		isAcceptingTabKeys()
		{ return(attributes.getBooleanAttribute(Mi_ACCEPTS_TAB_KEYS)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart is printable
					 *------------------------------------------------------*/
	public		boolean		isPrintable()
		{ return(attributes.getBooleanAttribute(Mi_PRINTABLE)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart is savable
					 *------------------------------------------------------*/
	public		boolean		isSavable()
		{ return(attributes.getBooleanAttribute(Mi_SAVABLE)); }
					/**------------------------------------------------------
					 * Returns true if transparent areas in this MiPart are 
					 * pickable
					 *------------------------------------------------------*/
	public		boolean		isPickableWhenTransparent()
		{ return(attributes.getBooleanAttribute(Mi_PICKABLE_WHEN_TRANSPARENT)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart should be snapped to a grid
					 * if there is a snap manager.
					 *------------------------------------------------------*/
	public		boolean		isSnappable()
		{ return(attributes.getBooleanAttribute(Mi_SNAPPABLE)); }

					/**------------------------------------------------------
					 * Sets whether this can be deleted. This contraint is to
					 * be enforced such that the end-user should not be able to
					 * delete this MiPart.
					 * @param b		true if deletable
					 *------------------------------------------------------*/
	public		void		setDeletable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_DELETABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this can be moved. This contraint is to
					 * be enforced such that the end-user should not be able to
					 * move this MiPart.
					 * @param b		true if movable
					 *------------------------------------------------------*/
	public		void		setMovable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_MOVABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this can be copied.
					 * This contraint is to be enforced such that the end-user
					 * should not be able to copy this MiPart.
					 * @param b		true if copyable
					 *------------------------------------------------------*/
	public		void		setCopyable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_COPYABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this can be copied as part of a deep copy.
					 * This contraint is to be enforced such that the this
					 * MiPart cannot be copied as part of a container and must
					 * be reconstituted by the application when its contaiing 
					 * parts are copied - theoretically by an application
					 * which is watching the Mi_COPY_ACTION on the container.
					 * @param b		true if copyable
					 *------------------------------------------------------*/
	public		void		setCopyableAsPartOfCopyable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_COPYABLE_AS_PART_OF_COPYABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this can be selected. This contraint is to
					 * be enforced such that the end-user should not be able to
					 * select this MiPart.
					 * @param b		true if selectable
					 *------------------------------------------------------*/
	public		void		setSelectable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_SELECTABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this has a constant width.
					 * This contraint is to be enforced such that the end-user
					 * should not be able to resize the width this MiPart.
					 * @param b		true if fixed
					 *------------------------------------------------------*/
	public		void		setFixedWidth(boolean b)
		{ attributes.setAttributeForObject(this, Mi_FIXED_WIDTH, b); }
					/**------------------------------------------------------
					 * Sets whether this has a constant height.
					 * This contraint is to be enforced such that the end-user
					 * should not be able to resize the height this MiPart.
					 * @param b		true if fixed
					 *------------------------------------------------------*/
	public		void		setFixedHeight(boolean b)
		{ attributes.setAttributeForObject(this, Mi_FIXED_HEIGHT, b); }
					/**------------------------------------------------------
					 * Sets whether this has a constant height/width ratio
					 * This contraint is to be enforced such that the end-user
					 * should not be able to change the aspect ration of this MiPart.
					 * @param b		true if fixed
					 *------------------------------------------------------*/
	public		void		setFixedAspectRatio(boolean b)
		{ attributes.setAttributeForObject(this, Mi_FIXED_ASPECT_RATIO, b); }
					/**------------------------------------------------------
					 * Sets whether this and its parts can be picked.
					 * @param b		true if pickable
					 *------------------------------------------------------*/
	public		void		setPickable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_PICKABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this can be un grouped.
					 * This contraint is to be enforced such that the end-user
					 * should not be able to ungroup this MiPart.
					 * @param b		true if ungroupable
					 *------------------------------------------------------*/
	public		void		setUngroupable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_UNGROUPABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this can be connected to.
					 * @param b		true if connectable
					 *------------------------------------------------------*/
	public		void		setConnectable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_CONNECTABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this accepts mouse focus
					 * @param b		true if accepts mouse focus
					 *------------------------------------------------------*/
	public		void		setAcceptingMouseFocus(boolean b)
		{ attributes.setAttributeForObject(this, Mi_ACCEPTS_MOUSE_FOCUS, b); }
					/**------------------------------------------------------
					 * Sets whether this accepts keyboard focus.
					 * @param b		true if accepts keyboard focus
					 *------------------------------------------------------*/
	public		void		setAcceptingKeyboardFocus(boolean b)
		{ attributes.setAttributeForObject(this, Mi_ACCEPTS_KEYBOARD_FOCUS, b); }
					/**------------------------------------------------------
					 * Sets whether this accepts enter-key focus.
					 * @param b		true if accepts enter key focus
					 *------------------------------------------------------*/
	public		void		setAcceptingEnterKeyFocus(boolean b)
		{ attributes.setAttributeForObject(this, Mi_ACCEPTS_ENTER_KEY_FOCUS, b); }
					/**------------------------------------------------------
					 * Sets whether this accepts tab keys.
					 * @param b		true if accepts tab keys
					 *------------------------------------------------------*/
	public		void		setAcceptingTabKeys(boolean b)
		{ attributes.setAttributeForObject(this, Mi_ACCEPTS_TAB_KEYS, b); }
					/**------------------------------------------------------
					 * Sets whether this is printable
					 * @param b		true if is printable
					 *------------------------------------------------------*/
	public		void		setPrintable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_PRINTABLE, b); }
					/**------------------------------------------------------
					 * Sets whether this can be saved
					 * @param b		true if savable
					 *------------------------------------------------------*/
	public		void		setSavable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_SAVABLE, b); }

					/**------------------------------------------------------
					 * Sets whether transparent colors (e.g. background color)
					 * of this part are pickable.
	 				 * If this is a container, this sets whether this container, 
					 * when it's background color is equal to Mi_TRANSPARENT_COLOR,
					 * is pickable. This is true, by default. Set this to true 
					 * to permit the user to drag nodes that are in this container
					 * and which perhaps has a layout.
					 * @param flag		true if transparent colors are pickable
					 * @see			#isPickableWhenTransparent
					 *------------------------------------------------------*/
	public		void		setPickableWhenTransparent(boolean flag)
		{ attributes.setAttributeForObject(this, Mi_PICKABLE_WHEN_TRANSPARENT, flag); }
					/**------------------------------------------------------
					 * Sets whether this can be snapped to a grid. This contraint is to
					 * be enforced such that the end-user should not be able to
					 * move this MiPart except on grid points.
					 * @param b		true if snappable
					 *------------------------------------------------------*/
	public		void		setSnappable(boolean b)
		{ attributes.setAttributeForObject(this, Mi_SNAPPABLE, b); }

					/**------------------------------------------------------
					 * Gets this MiPart's foreground color.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getColor()
		{ return(attributes.getColorAttribute(Mi_COLOR)); }
					/**------------------------------------------------------
					 * Gets this MiPart's background color.
					 * @return		the color
					 *------------------------------------------------------*/
	public		Color		getBackgroundColor()
		{ return(attributes.getColorAttribute(Mi_BACKGROUND_COLOR)); }
					/**------------------------------------------------------
					 * Gets whether the graphic will always be 'solid', i.e. 
					 * the background color will always be drawn with the 
					 * (foreground) color. The actual background color is
					 * ignored.
					 * @return 		true if solid, false if the graphic
					 * 			shape is to be filled with whatever
					 *			the background color is. The default
					 *			is 'false'.
					 * @see #setFilled
					 * @see #setBackgroundColor
					 *------------------------------------------------------*/
	public		boolean		isFilled()
		{ return(attributes.getBooleanAttribute(Mi_FILLED)); }
					/**------------------------------------------------------
					 * Gets this MiPart's brightest accent color.
					 * @return		the color
					 * @see			#getLightColor
					 * @see			#getDarkColor
					 * @see			#getBlackColor
					 *------------------------------------------------------*/
	public		Color		getWhiteColor()
		{ return(attributes.getWhiteColor()); }
					/**------------------------------------------------------
					 * Gets this MiPart's second brightest accent color.
					 * @return		the color
					 * @see			#getWhiteColor
					 * @see			#getDarkColor
					 * @see			#getBlackColor
					 *------------------------------------------------------*/
	public		Color		getLightColor()
		{ return(attributes.getLightColor()); }
					/**------------------------------------------------------
					 * Gets this MiPart's third brightest accent color.
					 * @return		the color
					 * @see			#getWhiteColor
					 * @see			#getLightColor
					 * @see			#getBlackColor
					 *------------------------------------------------------*/
	public		Color		getDarkColor()
		{ return(attributes.getDarkColor()); }
					/**------------------------------------------------------
					 * Gets this MiPart's darkest accent color.
					 * @return		the color
					 * @see			#getWhiteColor
					 * @see			#getLightColor
					 * @see			#getDarkColor
					 *------------------------------------------------------*/
	public		Color		getBlackColor()
		{ return(attributes.getBlackColor()); }
					/**------------------------------------------------------
					 * Gets this MiPart's xor color.
					 * @return		the color
					 * @see			#getXorColor
					 * @see			#getColor
					 * @see			#getBackgroundColor
					 *------------------------------------------------------*/
	public		Color		getXorColor()
		{ return(attributes.getXorColor()); }
					/**------------------------------------------------------
					 * Gets this MiPart's background image.
					 * @return		the image
					 *------------------------------------------------------*/
	public		Image		getBackgroundImage()
		{ return(attributes.getImageAttribute(Mi_BACKGROUND_IMAGE)); }
					/**------------------------------------------------------
					 * Gets this MiPart's background tile image.
					 * @return		the image
					 *------------------------------------------------------*/
	public		Image		getBackgroundTile()
		{ return(attributes.getImageAttribute(Mi_BACKGROUND_TILE)); }
					/**------------------------------------------------------
					 * Gets this MiPart's line width.
					 * @return		the width
					 *------------------------------------------------------*/
	public		MiDistance	getLineWidth()
		{ return(attributes.getDoubleAttribute(Mi_LINE_WIDTH)); }
					/**------------------------------------------------------
					 * Gets this MiPart's line style.
					 * @return		the style
					 *------------------------------------------------------*/
	public		int		getLineStyle()
		{ return(attributes.getIntegerAttribute(Mi_LINE_STYLE)); }
					/**------------------------------------------------------
					 * Gets this MiPart's line start style.
					 * @return		the style
					 *------------------------------------------------------*/
	public		int		getLineStartStyle()
		{ return(attributes.getIntegerAttribute(Mi_LINE_START_STYLE)); }
					/**------------------------------------------------------
					 * Gets this MiPart's line end style.
					 * @return		the style
					 *------------------------------------------------------*/
	public		int		getLineEndStyle()
		{ return(attributes.getIntegerAttribute(Mi_LINE_END_STYLE)); }
					/**------------------------------------------------------
					 * Gets this MiPart's line start size.
					 * @return		the size
					 *------------------------------------------------------*/
	public		MiDistance	getLineStartSize()
		{ return(attributes.getDoubleAttribute(Mi_LINE_START_SIZE)); }
					/**------------------------------------------------------
					 * Gets this MiPart's line end size.
					 * @return		the size
					 *------------------------------------------------------*/
	public		MiDistance	getLineEndSize()
		{ return(attributes.getDoubleAttribute(Mi_LINE_END_SIZE)); }
					/**------------------------------------------------------
					 * Gets whether this MiPart's line ends, if any, grow and
					 * shrink when this MiPart's line width grows and shrinks.
					 * @return		true if they are related
					 *------------------------------------------------------*/
	public		boolean		getLineEndsSizeFnOfLineWidth()
		{ return(attributes.getBooleanAttribute(Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH)); }
					/**------------------------------------------------------
					 * Gets this MiPart's font.
					 * @return		the font
					 *------------------------------------------------------*/
	public		MiFont		getFont()
		{ return((MiFont )attributes.getAttribute(Mi_FONT)); }
					/**------------------------------------------------------
					 * Gets this MiPart's write mode (either Mi_COPY_WRITEMODE
        				 * or Mi_XOR_WRITEMODE).
					 * @return		the write mode
					 *------------------------------------------------------*/
	public		int		getWriteMode()
		{ return(attributes.getIntegerAttribute(Mi_WRITE_MODE)); }
					/**------------------------------------------------------
					 * Gets this MiPart's lock mask that has bits set if
					 * particular attributes should not be changed. See
					 * MiiAttributeTypes for a list of valid bit masks.
					 * Default is 0 (no bits set).
					 * @return		the mask
					 *------------------------------------------------------*/
	public		int		getAttributeLockMask()
		{ return(attributes.getIntegerAttribute(Mi_ATTRIBUTE_LOCK_MASK)); }
					/**------------------------------------------------------
					 * Gets this MiPart's public mask that has bits set if
					 * particular attributes can be changed by the end
					 * user. See MiiAttributeTypes for a list of valid bit 
					 * masks. Default is 0 (no bits set).
					 * @return		the mask
					 *------------------------------------------------------*/
	public		int		getAttributePublicMask()
		{ return(attributes.getIntegerAttribute(Mi_ATTRIBUTE_PUBLIC_MASK)); }
					/**------------------------------------------------------
					 * Gets the tool hint assigned to the given point of this
					 * MiPart. If none, then returns the tool hint, if any, 
					 * assigned to this MiPart. If the given point is null
					 * then the help for this MiPart as a whole is returned.
					 * @return		the tool hint
					 * @see			#setToolHintHelp
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getToolHintHelp(MiPoint point)	
		{ return((MiiHelpInfo )attributes.getAttribute(Mi_TOOL_HINT_HELP)); }
					/**------------------------------------------------------
					 * Gets the object that describes the balloon help, if any,
					 * assigned to this MiPart at the given point. If the given 
					 * point is null then the help for this MiPart as a whole 
					 * is returned.
					 * @return		the help info object.
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getBalloonHelp(MiPoint point)
		{ return((MiiHelpInfo )attributes.getAttribute(Mi_BALLOON_HELP)); }
					/**------------------------------------------------------
					 * Gets the status bar help assigned to the given point of
					 * this MiPart. If none, then returns the status help,
					 * if any, assigned to this MiPart as a whole. If the 
					 * given point is null then the help for this MiPart as a 
					 * whole is returned.
					 * @param point		the location of interest
					 * @return		the status help
					 * @see			#setStatusHelp
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getStatusHelp(MiPoint point)	
		{ return((MiiHelpInfo )attributes.getAttribute(Mi_STATUS_HELP)); }
					/**------------------------------------------------------
					 * Gets the object that describes the dialog help, if any,
					 * assigned to this MiPart, at the given point.
					 * @return		the help info object.
					 *------------------------------------------------------*/
	public		MiiHelpInfo	getDialogHelp(MiPoint point)
		{ return((MiiHelpInfo )attributes.getAttribute(Mi_DIALOG_HELP)); }
					/**------------------------------------------------------
					 * Gets the context menu, if any, assigned to this MiPart.
					 * The context menu is usually displayed when the end user
					 * presses the right mouse button over this MiPart.
					 * @return		the menu or null
					 * @see			setContextMenu
					 *------------------------------------------------------*/
	public		MiiContextMenu	getContextMenu()	
		{ return((MiiContextMenu )attributes.getAttribute(Mi_CONTEXT_MENU)); }
					/**------------------------------------------------------
					 * Gets the context menu, if any, assigned to this MiPart
					 * at the given point. By default returns the context menu
					 * assigned to this part as a whole. However, this can be
					 * overridden to customize the behavior.
					 * The context menu is usually displayed when the end user
					 * presses the right mouse button over this MiPart.
					 * @param area		the area to look at or null
					 * @return		the menu or null
					 * @see			setContextMenu
					 *------------------------------------------------------*/
	public		MiiContextMenu	getContextMenu(MiBounds area)	
		{ return((MiiContextMenu )attributes.getAttribute(Mi_CONTEXT_MENU)); }
					/**------------------------------------------------------
					 * Gets the context cursor assigned to the given area of
					 * this MiPart. If none, then returns the context cursor,
					 * if any, assigned to this MiPart as a whole.
					 * @param area		the area to examine for a special
					 *			cursor, may be null to get cursor
					 *			for whole part
					 * @return		the cursor
					 * @see			#setContextCursor
					 *------------------------------------------------------*/
	public		int		getContextCursor(MiBounds area)	
		{ return(attributes.getIntegerAttribute(Mi_CONTEXT_CURSOR)); }
					/**------------------------------------------------------
					 * Gets the graphics to be used to highlight this MiPart
					 * when it is selected. If this is not null (the default)
					 * it overrides the selection graphics as specified in the
					 * MiSelectionManager.
					 * @return		the selection graphics
					 *------------------------------------------------------*/
	public		MiiSelectionGraphics	getSelectionGraphics()
		{ return(extensions == null ? null : extensions.selectionGraphics); }
					/**------------------------------------------------------
					 * Gets the margins of this MiPart.
					 * @param m		the (returned) margins 
					 * @return		the margins or null
					 *------------------------------------------------------*/
	public		MiMargins	getMargins(MiMargins m)
		{
		if ((extensions == null) || (extensions.margins == null))
			m.setMargins(0);
		else
			m.copy(extensions.margins);
		return(m);
		}
					/**------------------------------------------------------
					 * Gets the smallest width this MiPart should have.
					 * @return		the minimum width
					 *------------------------------------------------------*/
	public		MiDistance	getMinimumWidth()
		{ return(attributes.getDoubleAttribute(Mi_MINIMUM_WIDTH)); }
					/**------------------------------------------------------
					 * Gets the smallest height this MiPart should have.
					 * @return		the minimum height
					 *------------------------------------------------------*/
	public		MiDistance	getMinimumHeight()
		{ return(attributes.getDoubleAttribute(Mi_MINIMUM_HEIGHT)); }
					/**------------------------------------------------------
					 * Gets the largest width this MiPart should have.
					 * @return		the maximum width
					 *------------------------------------------------------*/
	public		MiDistance	getMaximumWidth()
		{ return(attributes.getDoubleAttribute(Mi_MAXIMUM_WIDTH)); }
					/**------------------------------------------------------
					 * Gets the largest height this MiPart should have.
					 * @return		the maximum height
					 *------------------------------------------------------*/
	public		MiDistance	getMaximumHeight()
		{ return(attributes.getDoubleAttribute(Mi_MAXIMUM_HEIGHT)); }

					/**------------------------------------------------------
					 * Gets the border assigned to this MiPart.
					 * @return		the border look
					 *------------------------------------------------------*/
	public		int		getBorderLook()
		{ return(attributes.getIntegerAttribute(Mi_BORDER_LOOK)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart has a hilite border.
					 *------------------------------------------------------*/
	public		boolean		getHasBorderHilite()
		{ return(attributes.getBooleanAttribute(Mi_HAS_BORDER_HILITE)); }
					/**------------------------------------------------------
					 * Gets the color of any hilite border.
					 * @return		the border color
					 *------------------------------------------------------*/
	public		Color		getBorderHiliteColor()
		{ return(attributes.getColorAttribute(Mi_BORDER_HILITE_COLOR)); }
					/**------------------------------------------------------
					 * Gets the width of any hilite border.
					 * @return		the width
					 *------------------------------------------------------*/
	public		MiDistance	getBorderHiliteWidth()
		{ return(attributes.getDoubleAttribute(Mi_BORDER_HILITE_WIDTH)); }
					/**------------------------------------------------------
					 * Returns true if this MiPart has a shadow.
					 *------------------------------------------------------*/
	public		boolean		getHasShadow()
		{ return(attributes.getBooleanAttribute(Mi_HAS_SHADOW)); }
					/**------------------------------------------------------
					 * Gets the color of any shadow.
					 * @return		the shadow color
					 *------------------------------------------------------*/
	public		Color		getShadowColor()
		{ return(attributes.getColorAttribute(Mi_SHADOW_COLOR)); }
					/**------------------------------------------------------
					 * Gets the length of any shadow.
					 * @return		the distance
					 *------------------------------------------------------*/
	public		MiDistance	getShadowLength()
		{ return(attributes.getDoubleAttribute(Mi_SHADOW_LENGTH)); }
					/**------------------------------------------------------
					 * Gets the direction of any shadow.
					 * @return		the direction
					 * @see			#setShadowDirection
					 *------------------------------------------------------*/
	public		int		getShadowDirection()
		{ return(attributes.getIntegerAttribute(Mi_SHADOW_DIRECTION)); }
					/**------------------------------------------------------
					 * Gets the style of any shadow.
					 * @return		the style
					 * @see			#setShadowStyle
					 *------------------------------------------------------*/
	public		int		getShadowStyle()
		{ return(attributes.getIntegerAttribute(Mi_SHADOW_STYLE)); }
					/**------------------------------------------------------
					 * Gets the shape of any shadow. Used by the shadow 
					 * renderers. By default the shadow shape is null. Override
					 * and return the actual shape (perhaps 
					 * MiiShadowRenderer.rectangularShadowShape) or null, which
					 * indicates to the shadow renderer that this part itself 
					 * is to be the shape.
					 * @return		the shape
					 *------------------------------------------------------*/
	public		MiPart		getShadowShape()
		{
		return(null);
		}

					/**------------------------------------------------------
					 * Gets the renderer that is called immediately before this
					 * MiPart is drawn.
					 * @return		the renderer
					 *------------------------------------------------------*/
	public		MiiPartRenderer	getBeforeRenderer()
		{ return((MiiPartRenderer )attributes.getAttribute(Mi_BEFORE_RENDERER)); }
					/**------------------------------------------------------
					 * Gets the renderer that is called immediately after this
					 * MiPart is drawn.
					 * @return		the renderer
					 *------------------------------------------------------*/
	public		MiiPartRenderer	getAfterRenderer()
		{ return((MiiPartRenderer )attributes.getAttribute(Mi_AFTER_RENDERER)); }
					/**------------------------------------------------------
					 * Gets the renderer that draws shadows for this MiPart.
					 * @return		the renderer
					 *------------------------------------------------------*/
	public		MiiShadowRenderer	getShadowRenderer()
		{ return((MiiShadowRenderer )attributes.getAttribute(Mi_SHADOW_RENDERER)); }
					/**------------------------------------------------------
					 * Gets the renderer that draws line ends for this MiPart.
					 * @return		the renderer
					 *------------------------------------------------------*/
	public		MiiLineEndsRenderer	getLineEndsRenderer()
		{ return((MiiLineEndsRenderer )attributes.getAttribute(Mi_LINE_ENDS_RENDERER)); }

					/**------------------------------------------------------
					 * Gets the renderer that draws the background of this 
					 * MiPart.
					 * @return		the renderer
					 *------------------------------------------------------*/
	public		MiiDeviceRenderer getBackgroundRenderer()
		{ return((MiiDeviceRenderer )attributes.getAttribute(Mi_BACKGROUND_RENDERER)); }
					/**------------------------------------------------------
					 * Gets the renderer that draws the borders of this MiPart.
					 * @return		the renderer
					 *------------------------------------------------------*/
	public		MiiDeviceRenderer getBorderRenderer()
		{ return((MiiDeviceRenderer )attributes.getAttribute(Mi_BORDER_RENDERER)); }
					/**------------------------------------------------------
					 * Gets the animator that animates the appearance and
					 * disappearance of this MiPart.
					 * @return		the animator
					 *------------------------------------------------------*/
	public		MiPartAnimator	getVisibilityAnimator() 
		{ return((MiPartAnimator )attributes.getAttribute(Mi_VISIBILITY_ANIMATOR)); }

					/**------------------------------------------------------
					 * Sets the foreground color.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_COLOR, c);  }
					/**------------------------------------------------------
					 * Sets the foreground color from the given color name.
					 * Valid names are "transparent" (i.e. 
					 * MiiTypes.Mi_TRANSPARENT_COLOR_NAME), hexidecimal rgb
					 * values (i.e. "0xff0000" or "#ff0000"), or an actual
					 * name (i.e. "red").
					 * @param name		the name of a color
					 *------------------------------------------------------*/
	public		void		setColor(String name)
		{ setColor(MiColorManager.getColor(name)); }
					/**------------------------------------------------------
					 * Sets the background color.
					 * @param c		the color
					 *------------------------------------------------------*/
	public		void		setBackgroundColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_BACKGROUND_COLOR, c); }
					/**------------------------------------------------------
					 * Sets the background color from the given color name.
					 * Valid names are "transparent" (i.e. 
					 * MiiTypes.Mi_TRANSPARENT_COLOR_NAME), hexidecimal rgb
					 * values (i.e. "0xff0000" or "#ff0000"), or an actual
					 * name (i.e. "red").
					 * @param name		the name of a color
					 *------------------------------------------------------*/
	public		void		setBackgroundColor(String name)
		{ setBackgroundColor(MiColorManager.getColor(name)); }
					/**------------------------------------------------------
					 * Sets whether the graphic will always be 'solid', i.e. 
					 * the background color will always be drawn with the 
					 * (foreground) color. The actual background color is
					 * ignored. This is useful for wide lines, polygons, etc.
					 * where it would normally be difficult to keep the color
					 * and background color manually in sync.
					 *
					 * @param flag		true if solid, false if the graphic
					 * 			shape is to be filled with whatever
					 *			the background color is. The default
					 *			is 'false'.
					 * @see #isFilled
					 * @see #setBackgroundColor
					 *------------------------------------------------------*/
	public		void		setFilled(boolean flag)
		{ attributes.setAttributeForObject(this, Mi_FILLED, flag); }
					/**------------------------------------------------------
					 * Sets this MiPart's brightest accent color.
					 * @param c		the color
					 * @see			#setLightColor
					 * @see			#setDarkColor
					 * @see			#setBlackColor
					 *------------------------------------------------------*/
	public		void		setWhiteColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_WHITE_COLOR, c); }
					/**------------------------------------------------------
					 * Sets this MiPart's second brightest accent color.
					 * @param c		the color
					 * @see			#setWhiteColor
					 * @see			#setDarkColor
					 * @see			#setBlackColor
					 *------------------------------------------------------*/
	public		void		setLightColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_LIGHT_COLOR, c); }
					/**------------------------------------------------------
					 * Sets this MiPart's third brightest accent color.
					 * @param c		the color
					 * @see			#setWhiteColor
					 * @see			#setLightColor
					 * @see			#setBlackColor
					 *------------------------------------------------------*/
	public		void		setDarkColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_DARK_COLOR, c); }
					/**------------------------------------------------------
					 * Sets this MiPart's darkest accent color.
					 * @param c		the color
					 * @see			#setWhiteColor
					 * @see			#setLightColor
					 * @see			#setDarkColor
					 *------------------------------------------------------*/
	public		void		setBlackColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_BLACK_COLOR, c); }
					/**------------------------------------------------------
					 * Sets this MiPart's xor color. The xor color should be
					 * different from the current color or nothing is drawn
					 * by AWT.
					 * @param c		the color
					 * @see			#setColor
					 * @see			#setBackgroundColor
					 * @see			#setXorColor
					 *------------------------------------------------------*/
	public		void		setXorColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_XOR_COLOR, c); }

					/**------------------------------------------------------
					 * Sets this MiPart's background image.
					 * @param i		the image
					 *------------------------------------------------------*/
	public		void		setBackgroundImage(Image i)
		{ attributes.setAttributeForObject(this, Mi_BACKGROUND_IMAGE, i); }
					/**------------------------------------------------------
					 * Sets this MiPart's background tile image.
					 * @param i		the image
					 *------------------------------------------------------*/
	public		void		setBackgroundTile(Image i)
		{ attributes.setAttributeForObject(this, Mi_BACKGROUND_TILE, i); }
					/**------------------------------------------------------
					 * Sets this MiPart's line width.
					 * @param w		the width
					 *------------------------------------------------------*/
	public		void		setLineWidth(MiDistance w)
		{ attributes.setAttributeForObject(this, Mi_LINE_WIDTH, w); }
					/**------------------------------------------------------
					 * Sets this MiPart's line style. Valid line styles (as
					 * found in MiiTypes) are:
					 *   Mi_SOLID_LINE_STYLE
        				 *   Mi_DASHED_LINE_STYLE
        				 *   Mi_DOUBLE_DASHED_LINE_STYLE
					 * @param style		the style
					 *------------------------------------------------------*/
	public		void		setLineStyle(int style)
		{ attributes.setAttributeForObject(this, Mi_LINE_STYLE, style); }
					/**------------------------------------------------------
					 * Sets this MiPart's line start style. Valid line start
					 * styles (as found in MiiTypes) are:
					 *   Mi_FILLED_TRIANGLE_LINE_END_STYLE
        				 *   Mi_STROKED_ARROW_LINE_END_STYLE
        				 *   Mi_FILLED_ARROW_LINE_END_STYLE
        				 *   Mi_FILLED_CIRCLE_LINE_END_STYLE
        				 *   Mi_FILLED_SQUARE_LINE_END_STYLE
        				 *   Mi_TRIANGLE_VIA_LINE_END_STYLE
        				 *   Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE
        				 *   Mi_CIRCLE_VIA_LINE_END_STYLE
        				 *   Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE
        				 *   Mi_SQUARE_VIA_LINE_END_STYLE
        				 *   Mi_FILLED_SQUARE_VIA_LINE_END_STYLE
        				 *   Mi_TRIANGLE_LINE_END_STYLE
        				 *   Mi_CIRCLE_LINE_END_STYLE
        				 *   Mi_SQUARE_LINE_END_STYLE
        				 *   Mi_NO_LINE_END_STYLE
        				 *   Mi_DIAMOND_LINE_END_STYLE
        				 *   Mi_FILLED_DIAMOND_LINE_END_STYLE
        				 *   Mi_3FEATHER_LINE_END_STYLE
        				 *   Mi_2FEATHER_LINE_END_STYLE 
					 * @param style		the style
					 *------------------------------------------------------*/
	public		void		setLineStartStyle(int style)
		{ attributes.setAttributeForObject(this, Mi_LINE_START_STYLE, style); updateHasRenderersFlag(); }
					/**------------------------------------------------------
					 * Sets this MiPart's line end style. Valid line end
					 * styles (as found in MiiTypes) are:
					 *   Mi_FILLED_TRIANGLE_LINE_END_STYLE
        				 *   Mi_STROKED_ARROW_LINE_END_STYLE
        				 *   Mi_FILLED_ARROW_LINE_END_STYLE
        				 *   Mi_FILLED_CIRCLE_LINE_END_STYLE
        				 *   Mi_FILLED_SQUARE_LINE_END_STYLE
        				 *   Mi_TRIANGLE_VIA_LINE_END_STYLE
        				 *   Mi_FILLED_TRIANGLE_VIA_LINE_END_STYLE
        				 *   Mi_CIRCLE_VIA_LINE_END_STYLE
        				 *   Mi_FILLED_CIRCLE_VIA_LINE_END_STYLE
        				 *   Mi_SQUARE_VIA_LINE_END_STYLE
        				 *   Mi_FILLED_SQUARE_VIA_LINE_END_STYLE
        				 *   Mi_TRIANGLE_LINE_END_STYLE
        				 *   Mi_CIRCLE_LINE_END_STYLE
        				 *   Mi_SQUARE_LINE_END_STYLE
        				 *   Mi_NO_LINE_END_STYLE
        				 *   Mi_DIAMOND_LINE_END_STYLE
        				 *   Mi_FILLED_DIAMOND_LINE_END_STYLE
        				 *   Mi_3FEATHER_LINE_END_STYLE
        				 *   Mi_2FEATHER_LINE_END_STYLE 
					 * @param style		the style
					 *------------------------------------------------------*/
	public		void		setLineEndStyle(int style)
		{ attributes.setAttributeForObject(this, Mi_LINE_END_STYLE, style); updateHasRenderersFlag(); }
					/**------------------------------------------------------
					 * Sets this MiPart's line start size.
					 * @param size		the size
					 *------------------------------------------------------*/
	public		void		setLineStartSize(MiDistance size)
		{ attributes.setAttributeForObject(this, Mi_LINE_START_SIZE, size); }
					/**------------------------------------------------------
					 * Sets this MiPart's line end size.
					 * @param size		the size
					 *------------------------------------------------------*/
	public		void		setLineEndSize(MiDistance size)
		{ attributes.setAttributeForObject(this, Mi_LINE_END_SIZE, size);  }
					/**------------------------------------------------------
					 * Sets whether this MiPart's line ends, if any, grow and
					 * shrink when this MiPart's line width grows and shrinks.
					 * @param flag		true if they are indeed related
					 *------------------------------------------------------*/
	public		void		setLineEndSizeFnOfLineWidth(boolean flag)
		{ attributes.setAttributeForObject(this, Mi_LINE_ENDS_SIZE_FN_OF_LINE_WIDTH, flag); }
					/**------------------------------------------------------
					 * Sets this MiPart's font.
					 * @param f		the font
					 *------------------------------------------------------*/
	public		void		setFont(MiFont f) 
		{ attributes.setAttributeForObject(this, Mi_FONT, f);  }
					/**------------------------------------------------------
					 * Sets this MiPart's write mode (either Mi_COPY_WRITEMODE
        				 * or Mi_XOR_WRITEMODE).
					 * @param wmode		the write mode
					 *------------------------------------------------------*/
	public		void		setWriteMode(int wmode)
		{ attributes.setAttributeForObject(this, Mi_WRITE_MODE, wmode); }
					/**------------------------------------------------------
					 * Sets this MiPart's lock mask that has bits set if
					 * particular attributes should not be changed. See
					 * MiiAttributeTypes for a list of valid bit masks.
					 * Default is 0 (no bits set).
					 * @param mask		the mask
					 *------------------------------------------------------*/
	public		void		setAttributeLockMask(int mask)
		{ attributes.setAttributeForObject(this, Mi_ATTRIBUTE_LOCK_MASK, mask); }
					/**------------------------------------------------------
					 * Sets this MiPart's public mask that has bits set if
					 * particular attributes can be changed by the end
					 * user. See MiiAttributeTypes for a list of valid bit 
					 * masks. Default is 0 (no bits set).
					 * @param mask		the mask
					 *------------------------------------------------------*/
	public		void		setAttributePublicMask(int mask)
		{ attributes.setAttributeForObject(this, Mi_ATTRIBUTE_PUBLIC_MASK, mask); }
					/**------------------------------------------------------
					 * Gets the context menu, if any, assigned to this MiPart.
					 * The context menu is usually displayed when the end user
					 * presses the right mouse button over this MiPart.
					 * @return		the menu or null
					 *------------------------------------------------------*/
	public		void		setContextMenu(MiiContextMenu menu)
		{ attributes.setAttributeForObject(this, Mi_CONTEXT_MENU, menu); }
					/**------------------------------------------------------
					 * Gets the context cursor, if any, assigned to this MiPart.
					 * The context cursor is the shape the mouse cursor displays
					 * when it is on top of this MiPart.
					 * @param cursor	the cursor
					 * @see			#getContextCursor
					 *------------------------------------------------------*/
	public		void		setContextCursor(int cursor)
		{ attributes.setAttributeForObject(this, Mi_CONTEXT_CURSOR, cursor); }

					/**------------------------------------------------------
					 * Sets the tool hint message of this MiPart.
					 * @return		the text message
					 *------------------------------------------------------*/
	public		void		setToolHintMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		attributes.setAttributeForObject(this, Mi_TOOL_HINT_HELP, 
				(msg != null) ? new MiHelpInfo(msg) : null);
		}
					/**------------------------------------------------------
					 * Sets the balloon help message of this MiPart.
					 * @param msg		the text message
					 *------------------------------------------------------*/
	public		void		setBalloonHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		attributes.setAttributeForObject(this, Mi_BALLOON_HELP, 
				(msg != null) ? new MiHelpInfo(msg) : null);
		}
					/**------------------------------------------------------
					 * Sets the status help message of this MiPart.
					 * @param msg		the text message
					 *------------------------------------------------------*/
	public		void		setStatusHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		attributes.setAttributeForObject(this, Mi_STATUS_HELP, 
				(msg != null) ? new MiHelpInfo(msg) : null);
		}
					/**------------------------------------------------------
					 * Sets the dialog help message of this MiPart.
					 * @param msg		the text message
					 *------------------------------------------------------*/
	public		void		setDialogHelpMessage(String msg)
		{
		msg = MiSystem.getPropertyOrKey(msg);
		attributes.setAttributeForObject(this, Mi_DIALOG_HELP, 
				(msg != null) ? new MiHelpInfo(msg) : null);
		}
					/**------------------------------------------------------
					 * Sets the tool hint of this MiPart.
					 * @param msg		the help info.
					 *------------------------------------------------------*/
	public		void		setToolHintHelp(MiiHelpInfo msg)
		{ attributes.setAttributeForObject(this, Mi_TOOL_HINT_HELP, msg); }
					/**------------------------------------------------------
					 * Sets the balloon help of this MiPart.
					 * @param msg		the help info.
					 *------------------------------------------------------*/
	public		void		setBalloonHelp(MiiHelpInfo msg)
		{ attributes.setAttributeForObject(this, Mi_BALLOON_HELP, msg); }
					/**------------------------------------------------------
					 * Sets the status help of this MiPart.
					 * @param msg		the help info.
					 *------------------------------------------------------*/
	public		void		setStatusHelp(MiiHelpInfo msg)
		{ attributes.setAttributeForObject(this, Mi_STATUS_HELP, msg); }
					/**------------------------------------------------------
					 * Sets the dialog help of this MiPart.
					 * @param msg		the help info.
					 *------------------------------------------------------*/
	public		void		setDialogHelp(MiiHelpInfo msg)
		{ attributes.setAttributeForObject(this, Mi_DIALOG_HELP, msg); }


					/**------------------------------------------------------
					 * Sets the graphics to be used to highlight this MiPart
					 * when it is selected. If this is not null (the default)
					 * it overrides the selection graphics as specified in the
					 * MiSelectionManager.
					 * @param sg		the selection graphics
					 *------------------------------------------------------*/
	public		void		setSelectionGraphics(MiiSelectionGraphics sg)
		{
		if (extensions == null)
			extensions = new MiPartExtensions(this);
		extensions.selectionGraphics = sg;
		}
					/**------------------------------------------------------
					 * Sets the margins of this MiPart.
					 * @param m		the margins or null
					 *------------------------------------------------------*/
	public		void		setMargins(MiMargins m)
		{
		if (extensions == null)
			extensions = new MiPartExtensions(this);
		if (extensions.margins != null)
			extensions.margins.copy(m);
		else
			extensions.margins = new MiMargins(m);
		refreshBounds();
		}

					/**------------------------------------------------------
					 * Sets the smallest width this MiPart should have.
					 * @param width		the minimum width
					 *------------------------------------------------------*/
	public		void		setMinimumWidth(MiDistance width)
		{ attributes.setAttributeForObject(this, Mi_MINIMUM_WIDTH, width); }
					/**------------------------------------------------------
					 * Sets the smallest height this MiPart should have.
					 * @param height	the minimum height
					 *------------------------------------------------------*/
	public		void		setMinimumHeight(MiDistance height)
		{ attributes.setAttributeForObject(this, Mi_MINIMUM_HEIGHT, height); }
					/**------------------------------------------------------
					 * Sets the largest width this MiPart should have.
					 * @param width		the maximum width
					 *------------------------------------------------------*/
	public		void		setMaximumWidth(MiDistance width)
		{ attributes.setAttributeForObject(this, Mi_MAXIMUM_WIDTH, width); }
					/**------------------------------------------------------
					 * Sets the largest height this MiPart should have.
					 * @param height	the maximum height
					 *------------------------------------------------------*/
	public		void		setMaximumHeight(MiDistance height)
		{ attributes.setAttributeForObject(this, Mi_MAXIMUM_HEIGHT, height); }

					/**------------------------------------------------------
					 * Sets the look of the border of this MiPart.
					 * @param look		the border look
					 *------------------------------------------------------*/
	public		void		setBorderLook(int look)
		{ attributes.setAttributeForObject(this, Mi_BORDER_LOOK, look); }
					/**------------------------------------------------------
					 * Sets whether this MiPart has a hilite border.
					 *------------------------------------------------------*/
	public		void		setHasBorderHilite(boolean flag)
		{ attributes.setAttributeForObject(this, Mi_HAS_BORDER_HILITE, flag); }
					/**------------------------------------------------------
					 * Sets the color of any hilite border.
					 * @param c		the border color
					 *------------------------------------------------------*/
	public		void		setBorderHiliteColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_BORDER_HILITE_COLOR, c); }
					/**------------------------------------------------------
					 * Sets the width of any hilite border.
					 * @param w		the width
					 *------------------------------------------------------*/
	public		void		setBorderHiliteWidth(MiDistance w)
		{ attributes.setAttributeForObject(this, Mi_BORDER_HILITE_WIDTH, w); }
					/**------------------------------------------------------
					 * Sets whether this MiPart has a shadow.
					 *------------------------------------------------------*/
	public		void		setHasShadow(boolean flag)
		{ attributes.setAttributeForObject(this, Mi_HAS_SHADOW, flag); }
					/**------------------------------------------------------
					 * Sets the color of any shadow.
					 * @param c		the shadow color
					 *------------------------------------------------------*/
	public		void		setShadowColor(Color c)
		{ attributes.setAttributeForObject(this, Mi_SHADOW_COLOR, c); }
					/**------------------------------------------------------
					 * Sets the length of any shadow.
					 * @param d		the depth
					 *------------------------------------------------------*/
	public		void		setShadowLength(MiDistance d)
		{ attributes.setAttributeForObject(this, Mi_SHADOW_LENGTH, d); }
					/**------------------------------------------------------
					 * Sets the location of any shadow. Valid locations are:
					 *     Mi_LOWER_RIGHT_LOCATION
					 *     Mi_LOWER_LEFT_LOCATION
					 *     Mi_UPPER_RIGHT_LOCATION
					 *     Mi_UPPER_LEFT_LOCATION
					 * @param direction	the location
					 *------------------------------------------------------*/
	public		void		setShadowDirection(int direction)
		{ attributes.setAttributeForObject(this, Mi_SHADOW_DIRECTION, direction); }

					/**------------------------------------------------------
					 * Sets the style of any shadow. Valid style are:
					 *     
					 *    
					 *   
					 *  
					 * @param style		the style
					 *------------------------------------------------------*/
	public		void		setShadowStyle(int style)
		{ attributes.setAttributeForObject(this, Mi_SHADOW_STYLE, style); }


					/**------------------------------------------------------
					 * Sets the renderer that is called immediately before this
					 * MiPart is drawn.
					 * @param r		the renderer
					 *------------------------------------------------------*/
	public		void		setBeforeRenderer(MiiPartRenderer r)
		{ attributes.setAttributeForObject(this, Mi_BEFORE_RENDERER, r); updateHasRenderersFlag(); }
					/**------------------------------------------------------
					 * Sets the renderer that is called immediately after this
					 * MiPart is drawn.
					 * @param r		the renderer
					 *------------------------------------------------------*/
	public		void		setAfterRenderer(MiiPartRenderer r)
		{ attributes.setAttributeForObject(this, Mi_AFTER_RENDERER, r); updateHasRenderersFlag(); }
					/**------------------------------------------------------
					 * Sets the renderer that draws shadows for this MiPart.
					 * @param r		the renderer
					 *------------------------------------------------------*/
	public		void		setShadowRenderer(MiiShadowRenderer r)
		{ attributes.setAttributeForObject(this, Mi_SHADOW_RENDERER, r); updateHasRenderersFlag(); }
					/**------------------------------------------------------
					 * Sets the renderer that draws line ends for this MiPart.
					 * @param r		the renderer
					 *------------------------------------------------------*/
	public		void		setLineEndsRenderer(MiiLineEndsRenderer r)
		{ attributes.setAttributeForObject(this, Mi_LINE_ENDS_RENDERER, r); updateHasRenderersFlag(); }

					/**------------------------------------------------------
					 * Sets the renderer that draws the background of this 
					 * MiPart.
					 * @param r		the renderer
					 *------------------------------------------------------*/
	public		void		setBackgroundRenderer(MiiDeviceRenderer r)
		{ attributes.setAttributeForObject(this, Mi_BACKGROUND_RENDERER, r); }
					/**------------------------------------------------------
					 * Sets the renderer that draws the borders of this MiPart.
					 * @param r		the renderer
					 *------------------------------------------------------*/
	public		void		setBorderRenderer(MiiDeviceRenderer r) 
		{ attributes.setAttributeForObject(this, Mi_BORDER_RENDERER, r); }
					/**------------------------------------------------------
					 * Sets the animator that animates the appearance and
					 * disappearance of this MiPart.
					 * @param animator	the animator
					 *------------------------------------------------------*/
	public		void		setVisibilityAnimator(MiPartAnimator animator) 
		{ attributes.setAttributeForObject(this, Mi_VISIBILITY_ANIMATOR, animator); }

					/**------------------------------------------------------
					 * Keep track of renderers that 'may' cause changes to
					 * the drawBounds.
					 *------------------------------------------------------*/
	private		void		updateHasRenderersFlag()
		{
		resetMiFlag(Mi_HAS_CUSTOM_RENDERERS_MASK);
		if (((attributes.objectAttributes[Mi_SHADOW_RENDERER] != null)
				&& (getHasShadow()))
			|| ((attributes.objectAttributes[Mi_LINE_ENDS_RENDERER] != null)
				&& ((getLineStartStyle() != Mi_NONE) || (getLineEndStyle() != Mi_NONE)))
			|| (attributes.objectAttributes[Mi_BEFORE_RENDERER] != null)
			|| (attributes.objectAttributes[Mi_AFTER_RENDERER] != null)
			|| ((managedPointManagers != null)
				&& ((managedPointManagers[0] != null)
				|| (managedPointManagers[1] != null)
				|| (managedPointManagers[2] != null)
				|| (managedPointManagers[3] != null))))
			{
			setMiFlag(Mi_HAS_CUSTOM_RENDERERS_MASK);
			}
		}

					/**------------------------------------------------------
					 * Returns true if the font style is bold.
					 *------------------------------------------------------*/
	public	boolean			isFontBold()
		{ return(getFont().isBold()); 							}
					/**------------------------------------------------------
					 * Sets whether the font has the bold style.
					 * @param flag		true if bold
					 *------------------------------------------------------*/
	public	void			setFontBold(boolean flag)
		{ setFont(getFont().setBold(flag)); }
					/**------------------------------------------------------
					 * Returns true if the font style is italic.
					 *------------------------------------------------------*/
	public	boolean			isFontItalic()
		{ return(getFont().isItalic()); 						}
					/**------------------------------------------------------
					 * Sets whether the font has the italic style.
					 * @param flag		true if italic
					 *------------------------------------------------------*/
	public	void			setFontItalic(boolean flag)
		{ setFont(getFont().setItalic(flag)); }
					/**------------------------------------------------------
					 * Returns true if the font style is underlined.
					 *------------------------------------------------------*/
	public	boolean			isFontUnderlined()
		{ return(getFont().isUnderlined()); 						}
					/**------------------------------------------------------
					 * Sets whether the font has the underlined style.
					 * @param flag		true if underlined
					 *------------------------------------------------------*/
	public	void			setFontUnderlined(boolean flag)
		{ setFont(getFont().setUnderlined(flag)); }
					/**------------------------------------------------------
					 * Gets the point size of the font of this MiPart.
					 * @return		the point size
					 *------------------------------------------------------*/
	public	int			getFontPointSize()
		{ return(getFont().getPointSize()); 						}
					/**------------------------------------------------------
					 * Sets the point size of the font of this MiPart.
					 * @param size		the point size
					 *------------------------------------------------------*/
	public	void			setFontPointSize(int size)
		{ setFont(getFont().setPointSize(size)); }
					/**------------------------------------------------------
					 * Gets the horizontal justification of any text.
					 * @return		the horizontal justfication
					 * @see			#setFontHorizontalJustification
					 *------------------------------------------------------*/
	public		int		getFontHorizontalJustification()
		{ return(attributes.getIntegerAttribute(Mi_FONT_HORIZONTAL_JUSTIFICATION)); }
					/**------------------------------------------------------
					 * Sets the horizontal justfication any text. Valid 
					 * justifications are:
					 *     Mi_CENTER_JUSTIFIED
					 *     Mi_LEFT_JUSTIFIED
					 *     Mi_RIGHT_JUSTIFIED
					 *     Mi_JUSTIFIED
					 * @param justification		the justification
					 *------------------------------------------------------*/
	public		void		setFontHorizontalJustification(int justification)
		{ attributes.setAttributeForObject(this, Mi_FONT_HORIZONTAL_JUSTIFICATION, justification); }

					/**------------------------------------------------------
					 * Gets the vertical justification of any text.
					 * @return		the vertical justfication
					 * @see			#setFontVerticalJustification
					 *------------------------------------------------------*/
	public		int		getFontVerticalJustification()
		{ return(attributes.getIntegerAttribute(Mi_FONT_VERTICAL_JUSTIFICATION)); }
					/**------------------------------------------------------
					 * Sets the vertical justfication any text. Valid 
					 * justifications are:
					 *     Mi_CENTER_JUSTIFIED
					 *     Mi_BOTTOM_JUSTIFIED
					 *     Mi_TOP_JUSTIFIED
					 *     Mi_JUSTIFIED
					 * @param justification		the justification
					 *------------------------------------------------------*/
	public		void		setFontVerticalJustification(int justification)
		{ attributes.setAttributeForObject(this, Mi_FONT_VERTICAL_JUSTIFICATION, justification); }

	//***************************************************************************************
	// State and Focus management
	//***************************************************************************************

					/**------------------------------------------------------
					 * Requests that this MiPart gets it's root window's
					 * keyboard focus. 
					 * @return 		true if this MiPart now has the
					 *			keyboard focus
					 * @see 		MiWindow#requestKeyboardFocus
					 *------------------------------------------------------*/
	public		boolean		requestKeyboardFocus()
		{
		MiWindow window = getRootWindow();
		if (window == null)
			return(false);
		return(window.requestKeyboardFocus(this));
		}
					/**------------------------------------------------------
					 * Requests that this MiPart gets it's root window's
					 * enter-key focus. 
					 * @return 		true if this MiPart now has the
					 *			enter-key focus
					 * @see 		MiWindow#requestEnterKeyFocus
					 *------------------------------------------------------*/
	public		boolean		requestEnterKeyFocus()
		{
		MiWindow window = getRootWindow();
		if (window == null)
			return(false);
		return(window.requestEnterKeyFocus(this));
		}
					/**------------------------------------------------------
					 * Specifies whether this MiPart has keyboard focus. A action
					 * request and, if not vetoed, an action commit is 
					 * generated. The action types are Mi_GOT_KEYBOARD_FOCUS_ACTION
					 * and Mi_LOST_KEYBOARD_FOCUS_ACTION. This only sets the
					 * keyboard focus flag and does not get this MiPart's
					 * window's actual keyboard focus.
					 * @param flag		true if this MiPart is to have
					 *			keyboard focus
					 * @see 		MiKeyboardFocusManager
					 *------------------------------------------------------*/
	protected	void		setKeyboardFocus(boolean flag) 	
		{ 
		int type = flag ? Mi_GOT_KEYBOARD_FOCUS_ACTION : Mi_LOST_KEYBOARD_FOCUS_ACTION;
		if ((isSensitive() || (!flag)) && dispatchActionRequest(type))
			{
			setMiFlag(Mi_HAS_KEYBOARD_FOCUS_MASK, flag);
			dispatchAction(type);
			}
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart has keyboard focus.
					 * @return 		true if this has keyboard focus
					 *------------------------------------------------------*/
	public		boolean		hasKeyboardFocus()
		{
		return(hasMiFlag(Mi_HAS_KEYBOARD_FOCUS_MASK));
		}
					/**------------------------------------------------------
					 * Specifies whether this MiPart has enter-key focus. A action
					 * request and, if not vetoed, an action commit is 
					 * generated. The action types are Mi_GOT_ENTER_KEY_FOCUS_ACTION
					 * and Mi_LOST_ENTER_KEY_FOCUS_ACTION. This only sets the
					 * enter-key focus flag and does not get this MiPart's
					 * window's actual enter-key focus. However, when the
					 * window is initialized, an option it has is to search all 
					 * of the parts in the window for one that has enter-key 
					 * focus, and if found, assign it focus (see 
					 * MiKeyboardFocusManager).
					 * @param flag		true if this MiPart is to have
					 *			keyboard focus
					 * @see 		MiKeyboardFocusManager
					 *------------------------------------------------------*/
	protected	void		setEnterKeyFocus(boolean flag) 	
		{ 
		int type = flag ? Mi_GOT_ENTER_KEY_FOCUS_ACTION : Mi_LOST_ENTER_KEY_FOCUS_ACTION;
		if ((isSensitive() || (!flag)) && dispatchActionRequest(type))
			{
			setMiFlag(Mi_HAS_ENTER_KEY_FOCUS_MASK, flag);
			dispatchAction(type);
			}
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart has enter key focus.
					 * @return 		true if this has enter key focus
					 *------------------------------------------------------*/
	public		boolean		hasEnterKeyFocus()
		{
		return(hasMiFlag(Mi_HAS_ENTER_KEY_FOCUS_MASK));
		}
					/**------------------------------------------------------
					 * Specifies whether this MiPart has mouse focus. A action
					 * request and, if not vetoed, an action commit is 
					 * generated. The action types are Mi_GOT_MOUSE_FOCUS_ACTION
					 * and Mi_LOST_MOUSE_FOCUS_ACTION. This only sets the
					 * mouse focus flag and does not set the position of the
					 * mouse cursor.
					 * @param flag		true if this MiPart is to have
					 *			mouse focus
					 *------------------------------------------------------*/
	protected	void		setMouseFocus(boolean flag) 	
		{ 
		int type = flag ? Mi_GOT_MOUSE_FOCUS_ACTION : Mi_LOST_MOUSE_FOCUS_ACTION;
		if ((isSensitive() || (!flag)) && dispatchActionRequest(type))
			{
			setMiFlag(Mi_HAS_MOUSE_FOCUS_MASK, flag);
			dispatchAction(type);
			}
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart has mouse focus.
					 * @return 		true if this has mouse focus
					 *------------------------------------------------------*/
	public		boolean		hasMouseFocus()
		{
		return(hasMiFlag(Mi_HAS_MOUSE_FOCUS_MASK));
		}
					/**------------------------------------------------------
					 * Specifies whether this MiPart is selected. A action
					 * request and, if not vetoed, an action commit is 
					 * generated. The action types are Mi_SELECTED_ACTION
					 * and Mi_DESELECTED_ACTION. This MiPart must be selectable
					 * in order to be selected. Use MiEditor.select(MiPart)
					 * if all the functionality of the selection manager
					 * is to be utilized.
					 * @param flag		true if this MiPart is to be
                                         *                      selected
					 * @see			MiEditor#select
					 * @see			setSelectable
					 * @see			isSelectable
					 *------------------------------------------------------*/
	protected	void		select(boolean flag) 		
		{
		int type = flag ? Mi_SELECTED_ACTION : Mi_DESELECTED_ACTION;
		if ((isSensitive() || (!flag)) && dispatchActionRequest(type))
			{
			setMiFlag(Mi_SELECTED_MASK, flag);
			dispatchAction(type);
			}
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart is selected.
					 * @return 		true if this is selected
					 *------------------------------------------------------*/
	public		boolean		isSelected()
		{
		return(hasMiFlag(Mi_SELECTED_MASK));
		}
					/**------------------------------------------------------
					 * Specifies whether this MiPart is hidden. A action
					 * commit is generated. The action types are 
					 * Mi_GOT_MOUSE_FOCUS_ACTION and Mi_LOST_MOUSE_FOCUS_ACTION.
					 * When a MiPart is hidden it is not viewable but it
					 * still has size.
					 * @param flag		true if this MiPart is to be hidden
					 *------------------------------------------------------*/
	public		void		setHidden(boolean flag)
		{
		attributes.setAttributeForObject(this, Mi_HIDDEN, flag); 
		invalidateArea(drawBounds);
		dispatchAction((flag) ? Mi_HIDDEN_ACTION : Mi_UNHIDDEN_ACTION);
		nowShowing(!flag);
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart is hidden.
					 * @return 		true if this is hidden
					 *------------------------------------------------------*/
	public		boolean		isHidden()
		{
		return(attributes.getBooleanAttribute(Mi_HIDDEN)); 
		}
					/**------------------------------------------------------
					 * This is called when this or some container is made visible
					 * or invisible, causing this part to also now be visible or
					 * invisible. Actions are generated depending on the new
					 * state of this MiPart. These action types are: 
					 * Mi_PART_SHOWING_ACTION and Mi_PART_NOT_SHOWING_ACTION.
					 *------------------------------------------------------*/
	protected	void		nowShowing(boolean flag)
		{
		if (drawManager != null)
			invalidateArea(drawBounds);

		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if ((!flag) || (getPart(i).isVisible()))
				getPart(i).nowShowing(flag);
			}

		dispatchAction((flag) ? Mi_PART_SHOWING_ACTION : Mi_PART_NOT_SHOWING_ACTION);
		}
					/**------------------------------------------------------
					 * Specifies whether this MiPart is visible. A action
					 * request and, if not vetoed, an action commit is 
					 * generated. The action types are Mi_INVISIBLE_ACTION
					 * and Mi_VISIBLE_ACTION. If a visibilityAnimator is
					 * assigned to this MiPart, it is invoked at this time.
					 * @param flag		true if this MiPart is to be visible
					 * @see 		MiPart#setVisibilityAnimator
					 *------------------------------------------------------*/
	public		void		setVisible(boolean flag) 		
		{ 
		if (flag == visible)
			return;

		int type = (!flag) ? Mi_INVISIBLE_ACTION : Mi_VISIBLE_ACTION;
		if (dispatchActionRequest(type))
			{
//System.out.println("\n\nsetVisible, attributes.visibilityAnimator = " + attributes.visibilityAnimator + "\n\n");
			if (attributes.objectAttributes[Mi_VISIBILITY_ANIMATOR] != null)
				{
				synchronized (this)
					{
					visible = true;
					if (flag)
						invalidateContainersLayout();
					MiPartAnimator animator = 
						(MiPartAnimator )attributes.objectAttributes[Mi_VISIBILITY_ANIMATOR]; 
					animator.setIsAnimatingForward(flag);
					animator.setSubject(this);
					animator.scheduleAndWait();
					}
				}

			if (flag)
				{
				visible = true;
				optimizedInvalidateArea();
				}
			else
				{
				if ((drawManager != null) && (getContainer(0) != null))
					getContainer(0).invalidateArea(drawBounds);
				else
					optimizedInvalidateArea();
				visible = false;

				zeroSizeOfSingleChildContainersOfInvisiblePart();
				}

			invalidateContainersLayout();
			dispatchAction(type);
			// FIX: cache an isShowing flag
			if (((visible) && (isShowing(null))) || (!visible))
				nowShowing(visible);
			}
		}
	public		void		optimizedInvalidateArea()
		{
		invalidateArea();
		}
	public		void		optimizedInvalidateDrawBoundsArea()
		{
		invalidateArea(drawBounds);
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart is visible.
					 * @return 		true if this is visible
					 *------------------------------------------------------*/
	public		boolean		isVisible()
		{
		return(visible);
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart is showing. If showing then
					 * this part or a container of this part is actually visible
					 * on the end user's output device.
					 * @param bounds 	not null if return whether bounds 
					 *			is being viewed by the end-user
					 * @return 		true if viewable/viewed
					 *------------------------------------------------------*/
	public		boolean		isShowing(MiBounds bounds)
		{
		if (!visible)
			return(false);
		if (isHidden())
			return(false);

		if ((bounds != null) && (this instanceof MiEditor))
			{
			// Given bounds are not showing
			if (!((MiEditor )this).getWorldBounds().intersects(bounds))
				return(false);
			}
			
		for (int i = 0; i < getNumberOfContainers(); ++i)
			{
			MiPart container = getContainer(i);
			if (container.isShowing(bounds))
				return(true);
			}

		if ((this instanceof MiWindow) && (((MiWindow )this).isRootWindow()))
			return(true);

		// No containers showing and this is not a root window
		return(false);
		}
					/**------------------------------------------------------
					 * Sets whether this MiPart is sensitive.
					 * @param flag 		true if this is to be sensitive
					 *------------------------------------------------------*/
	public		void		setSensitive(boolean flag)
		{ 
		if (!hasMiFlag(Mi_SENSITIVITY_DISABLED_BY_CONTAINER_MASK))
			{
			setMiFlag(Mi_SENSITIVE_MASK, flag);
			for (int i = 0; i < getNumberOfParts(); ++i)
				getPart(i).setSensitivityDisabledByContainer(!flag);
			}
		else
			{
			setMiFlag(Mi_SENSITIVE_MASK, flag);
			setMiFlag(Mi_SENSITIVITY_DISABLED_BY_CONTAINER_MASK, false);
			}
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart is sensitive.
					 * @return 		true if this is sensitive
					 *------------------------------------------------------*/
	public		boolean		isSensitive()
		{
		return(hasMiFlag(Mi_SENSITIVE_MASK) && (!hasMiFlag(Mi_SENSITIVITY_DISABLED_BY_CONTAINER_MASK)));
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart has sensitivity disabled
					 * by a container being deSensitized.
					 * @return 		true if event handling was disabled
					 *			by a container
					 * @see 		#getEventHandlingEnabled
					 *------------------------------------------------------*/
	private		boolean		getSensitivityDisabledByContainer()
		{
		return(hasMiFlag(Mi_SENSITIVITY_DISABLED_BY_CONTAINER_MASK));
		}
					/**------------------------------------------------------
					 * Specify whether this MiPart is to have it's event
					 * handling disabled because a container's event handling
					 * has been disabled. NOTE: this routine assumes that is
					 * one container is disabled then this part is disabled
					 * event though the event may be coming from another 
					 * container.
					 * @param flag		true if disabled
					 *------------------------------------------------------*/
	protected	void		setSensitivityDisabledByContainer(boolean flag)
		{
		// If re-enabling
		if (!flag)
			{
			// ...make sure all of this part's containers aren't disabled
			// by one of their containers...
			for (int i = 0; i < getNumberOfContainers(); ++i)
				{
				if ((containers[i].getSensitivityDisabledByContainer())
					|| (!containers[i].isSensitive()))
					{
					return;
					}
				}
			}
		// If desensitizing...
		if (flag)
			{
			// ... if is now sensitive... set flag to indicate that 
			// this was desensizied by container...
			if (hasMiFlag(Mi_SENSITIVE_MASK))
				{
				setSensitive(false);
				setMiFlag(Mi_SENSITIVITY_DISABLED_BY_CONTAINER_MASK, true);
				}
			}
		else
			{
			// ... if sensitizing and this was desensitized by container... then
			// sensitize this, otherwise leave this alown...
			if (hasMiFlag(Mi_SENSITIVITY_DISABLED_BY_CONTAINER_MASK))
				{
				setMiFlag(Mi_SENSITIVITY_DISABLED_BY_CONTAINER_MASK, false);
				setSensitive(true);
				}
			}

		for (int i = 0; i < getNumberOfParts(); ++i)
			getPart(i).setSensitivityDisabledByContainer(flag);
		}

	//***************************************************************************************
	// MiContainer convenience methods
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the key focus traversal group, if any, for the 
					 * parts of this container.
					 * @return 		the key focus traversal group
					 *------------------------------------------------------*/
	public		MiiKeyFocusTraversalGroup getKeyFocusTraversalGroup()
		{
		return(null);
		}
					/**------------------------------------------------------
					 * Specifies the key focus traversal group for the parts
					 * of this container.
					 * @param group		the key focus traversal group
					 *------------------------------------------------------*/
	public		void		setKeyFocusTraversalGroup(MiiKeyFocusTraversalGroup group)
		{
		}
					/**------------------------------------------------------
	 				 * Gets the transform, if any, assigned to this MiPart.
	 				 * @return 	 	the transform
					 *------------------------------------------------------*/
	public		MiiTransform	getTransform()
		{
		return(null);
		}

	//***************************************************************************************
	// Point management
	//***************************************************************************************

	public		void		setNumberOfPoints(int number)
		{
		}
					/**------------------------------------------------------
					 * Gets the number of points that define the shape of 
					 * this MiPart. The default is 2, the lower left and upper
					 * right corners.
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 * @return 		the number of points
					 *------------------------------------------------------*/
	public		int		getNumberOfPoints()
		{
		return(2);
		}
					/**------------------------------------------------------
					 * Sets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number. 
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 * @param pointNum 	the number of the point
					 * @param x 		the new x coordinate of the point
					 * @param y 		the new y coordinate of the point
					 *------------------------------------------------------*/
	public		void		setPoint(int pointNum, MiCoord x, MiCoord y)
		{
		invalidateArea();
		if ((pointNum == Mi_LAST_POINT_NUMBER) || (pointNum == 1))
			bounds.setURCorner(new MiPoint(x, y));
		else
			bounds.setLLCorner(new MiPoint(x, y));
		geometryChanged();
		reCalcDrawBounds();
		invalidateLayout();
		}
					/**------------------------------------------------------
					 * Sets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 * @param point 	the new coordinates of the point
					 *------------------------------------------------------*/
	public		void		setPoint(int pointNum, MiPoint point)
		{
		setPoint(pointNum, point.x, point.y);
		}
					/**------------------------------------------------------
					 * Moves the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number. 
					 * @param pointNum 	the number of the point
					 * @param tx 		the new x translation of the point
					 * @param ty 		the new y translation of the point
					 *------------------------------------------------------*/
	public	void			translatePoint(int pointNum, MiCoord tx, MiCoord ty)
		{
		MiPoint pt = getPoint(pointNum);
		setPoint(pointNum, pt.x + tx, pt.y + ty);
		}
					/**------------------------------------------------------
					 * Gets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 * @return 	 	the coordinates of the point
					 *------------------------------------------------------*/
	public		MiPoint		getPoint(int pointNum)
		{
		MiPoint point = new MiPoint();
		getPoint(pointNum, point);
		return(point);
		}
					/**------------------------------------------------------
					 * Gets the location of the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * Override this, if desired, as it implements the core 
					 * functionality.
					 * @param pointNum 	the number of the point
					 * @param point	 	the (returned) coordinates of the
					 *			point
					 *------------------------------------------------------*/
	public		void		getPoint(int pointNum, MiPoint point)
		{
		if ((pointNum == -1) || (pointNum == 1))
			{
			point.x = bounds.getXmax();
			point.y = bounds.getYmax();
			}
		else
			{
			point.x = bounds.getXmin();
			point.y = bounds.getYmin();
			}
		}
					/**------------------------------------------------------
					 * Append another point to the points that define the
					 * shape of this MiPart.
					 * @param point	 	the point to be appended
					 *------------------------------------------------------*/
	public		void		appendPoint(MiPoint point)
		{
		appendPoint(point.x, point.y);
		}
					/**------------------------------------------------------
					 * Append another point to the points that define the
					 * shape of this MiPart. Override this, if desired, as it 
					 * implements the core functionality.
					 * @param x	 	the x coord of the point to be appended
					 * @param y	 	the y coord of the point to be appended
					 *------------------------------------------------------*/
	public		void		appendPoint(MiCoord x, MiCoord y)
		{
		}
					/**------------------------------------------------------
					 * Insert another point to the points that define the
					 * shape of this MiPart.
					 * @param point	 	the point to be inserted
					 * @param index	 	the index of the point to insert
					 *			this new point before
					 *------------------------------------------------------*/
	public		void		insertPoint(MiPoint point, int index)
		{
		insertPoint(point.x, point.y, index);
		}
					/**------------------------------------------------------
					 * Insert another point to the points that define the
					 * shape of this MiPart. Override this, if desired, as it 
					 * implements the core functionality.
					 * @param x	 	the x coord of the point to be appended
					 * @param y	 	the y coord of the point to be appended
					 * @param index	 	the index of the point to insert
					 *			this new point before
					 *------------------------------------------------------*/
	public		void		insertPoint(MiCoord x, MiCoord y, int index)
		{
		}
					/**------------------------------------------------------
					 * Remove the point with the given number.
					 * Points are numbered from 0. Mi_LAST_POINT_NUMBER is
					 * also a valid point number.
					 * @param pointNum 	the number of the point
					 *------------------------------------------------------*/
	public		void		removePoint(int pointNum)
		{
		}
					/**------------------------------------------------------
					 * Get the angle of the shape as it exits the point with 
					 * the given number. Points are numbered from 0. 
					 * @param pointNum 	the number of the point
					 * @return		the angle in radians
					 *------------------------------------------------------*/
	public		double		getPointExitAngle(int pointNumber)
		{
		return(1.0);
		}
					/**------------------------------------------------------
					 * Get the angle of the shape as it enters the point with 
					 * the given number. Points are numbered from 0. 
					 * Mi_LAST_POINT_NUMBER is also a valid point number.
					 * @param pointNum 	the number of the point
					 * @return		the angle in radians
					 *------------------------------------------------------*/
	public		double		getPointEntryAngle(int pointNumber)
		{
		return(1.0);
		}

	//***************************************************************************************
	// Geometry management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the location of the center of this MiPart.
	 				 * @param pt 	 	the (returned) center point
	 				 * @return 	 	the center point
					 *------------------------------------------------------*/
	public		MiPoint		getCenter(MiPoint pt) 	{ return(bounds.getCenter(pt)); }

					/**------------------------------------------------------
	 				 * Gets the location of the center of this MiPart.
	 				 * @return 	 	the center point
					 *------------------------------------------------------*/
	public		MiPoint		getCenter()		{ return(bounds.getCenter());	}

					/**------------------------------------------------------
	 				 * Gets the x coordinate of the center of this MiPart.
	 				 * @return 	 	the center point's x coordinate
					 *------------------------------------------------------*/
	public		MiCoord		getCenterX()		{ return(bounds.getCenterX());	}

					/**------------------------------------------------------
	 				 * Gets the y coordinate of the center of this MiPart.
	 				 * @return 	 	the center point's y coordinate
					 *------------------------------------------------------*/
	public		MiCoord		getCenterY()		{ return(bounds.getCenterY());	}

					/**------------------------------------------------------
	 				 * Gets the x coordinate of the left side of this MiPart.
	 				 * @return 	 	the coordinate
					 *------------------------------------------------------*/
	public	MiCoord			getXmin()		{ return(bounds.xmin);		}

					/**------------------------------------------------------
	 				 * Gets the y coordinate of the bottom side of this MiPart.
	 				 * @return 	 	the coordinate
					 *------------------------------------------------------*/
	public		MiCoord		getYmin()		{ return(bounds.ymin);		}

					/**------------------------------------------------------
	 				 * Gets the x coordinate of the right side of this MiPart.
	 				 * @return 	 	the coordinate
					 *------------------------------------------------------*/
	public		MiCoord		getXmax()		{ return(bounds.xmax);		}

					/**------------------------------------------------------
	 				 * Gets the y coordinate of the top side of this MiPart.
	 				 * @return 	 	the coordinate
					 *------------------------------------------------------*/
	public		MiCoord		getYmax()		{ return(bounds.ymax);		}

					/**------------------------------------------------------
	 				 * Gets the width of this MiPart.
	 				 * @return 	 	the width
					 *------------------------------------------------------*/
	public		MiDistance	getWidth()		{ return(bounds.getWidth()); 	}

					/**------------------------------------------------------
	 				 * Gets the height of this MiPart.
	 				 * @return 	 	the height
					 *------------------------------------------------------*/
	public		MiDistance	getHeight()		{ return(bounds.getHeight()); 	}

					/**------------------------------------------------------
	 				 * A direct replacement of the current bounds of this
					 * MiPart. No scaling of parts or invalidation of layouts
					 * is performed. This is usually used when subclass
					 * shapes are constructed. 
	 				 * @param b 	 	the new bounds
					 * @see			#setBounds
					 * @see			#setSize
					 *------------------------------------------------------*/
	protected	void		replaceBounds(MiBounds b)
		{
		bounds.copy(b);
		geometryChanged();
		fastReCalcDrawBounds();
		}
	protected	void		replaceAttributes(MiAttributes atts)
		{
		attributes.decNumUsing();
		attributes = atts;
		attributes.incNumUsing();
		updateHasRenderersFlag();
		}
					/**------------------------------------------------------
	 				 * A direct modification of the current bounds of this
					 * MiPart. No scaling of parts or invalidation of layouts
					 * is performed. This is usually used when subclass
					 * shapes are constructed. 
	 				 * @param b 	 	the new bounds
					 * @see			#setBounds
					 * @see			#setSize
					 * @see			MiBounds#union
					 *------------------------------------------------------*/
	protected	void		unionBounds(MiBounds b)
		{
		bounds.union(b);
		geometryChanged();
		fastReCalcDrawBounds();
		}
					/**------------------------------------------------------
	 				 * A direct modification of the current bounds of this
					 * MiPart. No scaling of parts or invalidation of layouts
					 * is performed. This is usually used when subclass
					 * shapes are constructed. 
	 				 * @param x 	 	the new x coord
	 				 * @param y 	 	the new y coord
					 * @see			#setBounds
					 * @see			#setSize
					 * @see			MiBounds#union
					 *------------------------------------------------------*/
	protected	void		unionBounds(MiCoord x, MiCoord y)
		{
		bounds.union(x, y);
		geometryChanged();
		fastReCalcDrawBounds();
		}
	public		void		replaceDrawBounds(MiBounds b)
		{
		if ((b == null) || (bounds.equals(b)))
			{
			if (drawBounds != bounds)
				MiBounds.freeBounds(drawBounds);
			drawBounds = bounds;
			}
		else
			{
			if (drawBounds == bounds)
				drawBounds = MiBounds.newBounds();
			drawBounds.copy(b);
			}
		geometryChanged();
		}
					/**------------------------------------------------------
	 				 * Translates this MiPart such that the x coordinate of 
					 * the left side is equal to the given coordinate.
	 				 * @param xmin 	 	the coordinate
					 *------------------------------------------------------*/
	public		void		setXmin(MiCoord xmin)	
		{ 
		if (bounds.isReversedWidth())
			{
			bounds.xmin = xmin;
			bounds.xmax = xmin;
			}
		else
			{
			translate(xmin - bounds.xmin, 0);
			}
		}
					/**------------------------------------------------------
	 				 * Translates this MiPart such that the y coordinate of 
					 * the bottom side is equal to the given coordinate.
	 				 * @param ymin 	 	the coordinate
					 *------------------------------------------------------*/
	public		void		setYmin(MiCoord ymin)	
		{ 
		if (bounds.isReversedHeight())
			{
			bounds.ymin = ymin;
			bounds.ymax = ymin;
			}
		else
			{
			translate(0, ymin - bounds.ymin);
			}
		}
					/**------------------------------------------------------
	 				 * Translates this MiPart such that the x coordinate of 
					 * the right side is equal to the given coordinate.
	 				 * @param xmax	 	the coordinate
					 *------------------------------------------------------*/
	public		void		setXmax(MiCoord xmax)	
		{ 
		if (bounds.isReversedWidth())
			{
			bounds.xmin = xmax;
			bounds.xmax = xmax;
			}
		else
			{
			translate(xmax - bounds.xmax, 0);
			}
		}
					/**------------------------------------------------------
	 				 * Translates this MiPart such that the y coordinate of 
					 * the top side is equal to the given coordinate.
	 				 * @param ymax 	 	the coordinate
					 *------------------------------------------------------*/
	public		void		setYmax(MiCoord ymax)	
		{ 
		if (bounds.isReversedHeight())
			{
			bounds.ymin = ymax;
			bounds.ymax = ymax;
			}
		else
			{
			translate(0, ymax - bounds.ymax);
			}
		}
					/**------------------------------------------------------
	 				 * Translates this MiPart such that the x coordinate of 
					 * the center is equal to the given coordinate.
	 				 * @param x 	 	the coordinate
					 *------------------------------------------------------*/
	public		void		setCenterX(MiCoord x)	
		{ 
		if (bounds.isReversedWidth())
			{
			bounds.xmin = x;
			bounds.xmax = x;
			}
		else
			{
			translate(x - bounds.getCenterX(), 0); 
			}
		}
					/**------------------------------------------------------
	 				 * Translates this MiPart such that the y coordinate of 
					 * the center is equal to the given coordinate.
	 				 * @param y 	 	the coordinate
					 *------------------------------------------------------*/
	public		void		setCenterY(MiCoord y)	
		{ 
		if (bounds.isReversedHeight())
			{
			bounds.ymin = y;
			bounds.ymax = y;
			}
		else
			{
			translate(0, y - bounds.getCenterY()); 
			}
		}
					/**------------------------------------------------------
	 				 * Translates this MiPart such that the location of the
					 * center is equal to the given point.
	 				 * @param point	 	the new center point
					 *------------------------------------------------------*/
	public		void		setCenter(MiPoint point)
		{
		setCenter(point.x, point.y);
		}
					/**------------------------------------------------------
	 				 * Translates this MiPart such that the location of the
					 * center is equal to the given coordinates.
	 				 * @param x	 	the new center point x coordinate
	 				 * @param y	 	the new center point y coordinate
					 *------------------------------------------------------*/
	public		void		setCenter(MiCoord x, MiCoord y)
		{
		if (bounds.isReversedWidth())
			{
			bounds.xmin = x;
			bounds.xmax = x;
			setCenterY(y);
			}
		else if (bounds.isReversedHeight())
			{
			bounds.ymin = y;
			bounds.ymax = y;
			setCenterX(x);
			}
		else
			{
			translate(x - bounds.getCenterX(), y - bounds.getCenterY());
			}
		}
					/**------------------------------------------------------
	 				 * Sets the width of this MiPart to the given distance.
					 * The center of this MiPart is maintained.
	 				 * @param w	 	the new width
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
	 				 * @action 		Mi_SIZE_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		setWidth(MiDistance w)	
		{ 
		if (w < 0)
			throw new IllegalArgumentException(this + ": Width less than zero");
		if (w >= Mi_MAX_DISTANCE_VALUE/100)
			throw new IllegalArgumentException(this + ": Width too big");

/*
		if (geomConstraint != null)
			w = geomConstraint.enforceWidthRestrictions(w);
*/
			
		if (Utility.isZero(w - bounds.getWidth()))
			return;
			
		if ((bounds.getWidth() > 0) && (w > 0))
			{
			scale(w/bounds.getWidth(), 1.0);
			}
		else if ((bounds.isReversedWidth()) || (bounds.getWidth() == 0) || (w == 0))
			{
			MiCoord center = bounds.getCenterX();
			bounds.xmin = center - w/2;
			bounds.xmax = center + w - w/2;
			sizeChanged();
			geometryChanged();
			reCalcDrawBounds();
			invalidateLayout();
			}
		}
					/**------------------------------------------------------
	 				 * Sets the height of this MiPart to the given distance.
					 * The center of this MiPart is maintained.
	 				 * @param h	 	the new height
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
	 				 * @action 		Mi_SIZE_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		setHeight(MiDistance h)	
		{ 
		if (h < 0)
			throw new IllegalArgumentException(this + ": Height less than zero");
		if (h >= Mi_MAX_DISTANCE_VALUE/100)
			throw new IllegalArgumentException(this + ": Height too big");

/*
		if (geomConstraint != null)
			h = geomConstraint.enforceHeightRestrictions(h);
*/

		if (Utility.isZero(h - bounds.getHeight()))
			return;
			
		if ((bounds.getHeight() > 0) && (h > 0))
			{
			scale(1.0, h/bounds.getHeight());
			}
		else if ((bounds.isReversedHeight()) || (bounds.getHeight() == 0) || (h == 0))
			{
			MiCoord center = bounds.getCenterY();
			bounds.ymin = center - h/2;
			bounds.ymax = center + h - h/2;
			sizeChanged();
			geometryChanged();
			reCalcDrawBounds();
			invalidateLayout();
			}
		}
					/**------------------------------------------------------
	 				 * Sets the size of this MiPart to the given width and
					 * height. The center of this MiPart is maintained.
	 				 * @param width	 	the new width
	 				 * @param height 	the new height
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		setSize(MiDistance width, MiDistance height)
		{
		MiBounds b = MiBounds.newBounds();
		b.setBounds(0, 0, width, height);
		b.setCenterX(getCenterX());
		b.setCenterY(getCenterY());
		setBounds(b);
		MiBounds.freeBounds(b);
		}
					/**------------------------------------------------------
	 				 * Sets the size of this MiPart to the given size. The 
					 * center of this MiPart is maintained.
	 				 * @param size	 	the new size
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		setSize(MiSize size)
		{
		setSize(size.width, size.height);
		}
					/**------------------------------------------------------
	 				 * Gets the size of this MiPart.
	 				 * @param size	 	the (returned) size
	 				 * @return 	 	the size
					 *------------------------------------------------------*/
	public		MiSize		getSize(MiSize size)
		{
		size.setWidth(bounds.getWidth());
		size.setHeight(bounds.getHeight());
		return(size);
		}
					/**------------------------------------------------------
	 				 * Sets the bounds of this MiPart to have the given lower 
					 * left and upper right hand corners. 
	 				 * @param xmin	 	the x coordinate of the left side
	 				 * @param ymin	 	the y coordinate of the bottom side
	 				 * @param xmax	 	the x coordinate of the right side
	 				 * @param ymax	 	the y coordinate of the top side
					 *------------------------------------------------------*/
	public		void		setBounds(MiCoord xmin, MiCoord ymin, MiCoord xmax, MiCoord ymax)
		{
		MiBounds b = MiBounds.newBounds();
		b.setBounds(xmin, ymin, xmax, ymax);
		setBounds(b);
		MiBounds.freeBounds(b);
		}
					/**------------------------------------------------------
	 				 * Sets the bounds of this MiPart to have the given bounds. 
					 * Any parts of this MiPart are also scaled and translated
					 * in kind. Override this, if desired, as it implements 
					 * the core functionality.
	 				 * @param newBounds	the new bounds
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		setBounds(MiBounds newBounds)
		{
		if (bounds.equals(newBounds))
			return;

		boolean fixedWidth = hasFixedWidth();
		boolean fixedHeight = hasFixedHeight();
		if (fixedWidth && fixedHeight)
			return;

		if (newBounds.isReversed())
			{
			throw new IllegalArgumentException(this + ": Proposed bounds: <" 
				+ newBounds + "> uninitialized or less than zero");
			}
		if (newBounds.getWidth() >= Mi_MAX_DISTANCE_VALUE/100)
			throw new IllegalArgumentException(this + ": Width too big");
		if (newBounds.getHeight() >= Mi_MAX_DISTANCE_VALUE/100)
			throw new IllegalArgumentException(this + ": Height too big");

/*
		if (geomConstraint != null)
			newBounds = geomConstraint.enforceBoundsRestrictions(new MiBounds(newBounds));
*/

		if ((!hasValidBounds()) && (getNumberOfParts() > 0))
			{
			// Something below may have been made invisible, etc. 3-9-2004 
			refreshBounds();
			}
			
		if ((bounds.isReversedHeight()) || (bounds.getHeight() == 0))
			{
			setHeight(newBounds.getHeight());
			}
		if ((bounds.isReversedWidth()) || (bounds.getWidth() == 0))
			{
			setWidth(newBounds.getWidth());
			}
		if (bounds.equals(newBounds))
			return;

		optimizedInvalidateArea();

		if (fixedWidth)
			{
			newBounds = new MiBounds(newBounds);
			newBounds.xmin = bounds.xmin;
			newBounds.xmax = bounds.xmax;
			}
		if (fixedHeight)
			{
			newBounds = new MiBounds(newBounds);
			newBounds.ymin = bounds.ymin;
			newBounds.ymax = bounds.ymax;
			}

		MiScale scale = new MiScale(
			newBounds.getWidth()/bounds.getWidth(), 
			newBounds.getHeight()/bounds.getHeight());

		if (bounds.getWidth() == 0)
			{
			if (newBounds.getWidth() == 0)
				scale.x = 1;
			else
				scale.x = newBounds.getWidth();
			}
		if (bounds.getHeight() == 0)
			{
			if (newBounds.getHeight() == 0)
				scale.y = 1;
			else
				scale.y = newBounds.getHeight();
			}

		MiDistance transX = newBounds.getCenterX() - bounds.getCenterX();
		MiDistance transY = newBounds.getCenterY() - bounds.getCenterY();

		bounds.copy(newBounds);

		if (!Utility.areZero(transX, transY))
			translateParts(transX, transY);

		if (!scale.isIdentity())
			scaleParts(bounds.getCenter(), scale);

		if (attachments != null)
			attachments.invalidateLayout();

		geometryChanged();

		reCalcDrawBounds();
		if ((!scale.isIdentity()) 
			|| ((getLayout() != null) && (!getLayout().isIndependantOfTargetPosition())))
			{
			invalidateLayout();
			}
		else
			{
			invalidateContainersLayout();
			}
		}
					/**------------------------------------------------------
	 				 * Rotates this MiPart the given number of radians about
					 * it's center.
	 				 * @param radians	the angle to rotate
					 *------------------------------------------------------*/
	public		void		rotate(double radians)
		{
		MiPoint center = bounds.getCenter();
		rotate(center, radians);
		}
					/**------------------------------------------------------
	 				 * Rotates this MiPart the given number of radians about
					 * the given point.
	 				 * @param center	the center of rotation
	 				 * @param radians	the angle to rotate
					 *------------------------------------------------------*/
	public		void		rotate(MiPoint center, double radians)
		{
		doRotate(center, radians);
	
		if (extensions == null)
			extensions = new MiPartExtensions(this);

		extensions.rotationRadians += radians;
		}
					/**------------------------------------------------------
	 				 * Gets the rotation in radians
	 				 * @return 	the total angle rotated using the rotate method
					 *------------------------------------------------------*/
	public		double		getRotation()
		{
		if (extensions != null)
			return(extensions.rotationRadians);
		return(0);
		}
		
					/**------------------------------------------------------
	 				 * The implementation of the rotate method. Override this to
					 * support requirements of a specific shape.
	 				 * @param center	the center of rotation
	 				 * @param radians	the angle to rotate
					 *------------------------------------------------------*/
	protected	void		doRotate(MiPoint center, double radians)
		{
		// Implement for rectangluar shapes defined by their bounds

		while (radians >= Math.PI*2)
			radians -= Math.PI*2;
		while (radians < 0)
			radians += Math.PI*2;

		if (Utility.isZero(radians))
			return;

		MiCoord xmin = 0;
		MiCoord ymin = 0;
		MiCoord xmax = 0;
		MiCoord ymax = 0;

		invalidateArea();
		if (radians == Math.PI/2)
			{
			xmin = center.x - (bounds.ymax - center.y);
			ymin = center.y + (bounds.xmin - center.x);
			xmax = center.x - (bounds.ymin - center.y);
			ymax = center.y + (bounds.xmax - center.x);
			}
		else if (radians == Math.PI)
			{
			xmin = center.x - (bounds.xmax - center.x);
			ymin = center.y - (bounds.ymax - center.y);
			xmax = center.x - (bounds.xmin - center.x);
			ymax = center.y - (bounds.ymin - center.y);
			}
		else if (radians == 3 * Math.PI/2)
			{
			xmin = center.x + (bounds.ymin - center.y);
			ymin = center.y - (bounds.xmax - center.x);
			xmax = center.x + (bounds.ymax - center.y);
			ymax = center.y - (bounds.xmin - center.x);
			}
		else
			{
			MiDebug.println(this + " - Unimplemented angle supported in MiPart base class: doRotate(MiPoint center, double radians), radians = " + radians);
			return;
			}

		replaceBounds(new MiBounds(xmin, ymin, xmax, ymax));

		refreshBounds();
		invalidateLayout();
		}
					/**------------------------------------------------------
	 				 * Flips this MiPart about the axis specified by the given 
					 * number of radians about the parts center.
	 				 * @param radians	the angle of the axis of reflection
					 *------------------------------------------------------*/
	public		void		flip(double radians)
		{
		MiPoint center = bounds.getCenter();
		flip(center, radians);
		}
					/**------------------------------------------------------
	 				 * Flips this MiPart about the axis specified by the given 
					 * number of radians about the given point.
	 				 * @param center	the center of flip
	 				 * @param radians	the angle of the axis of reflection
					 *------------------------------------------------------*/
	public		void		flip(MiPoint center, double radians)
		{
		doFlip(center, radians);
	
		if (extensions == null)
			extensions = new MiPartExtensions(this);

		extensions.flipped = !extensions.flipped;
		}
	public		boolean		getFlipped()
		{
		if (extensions != null)
			return(extensions.flipped);

		return(false);
		}
	protected	void		doFlip(MiPoint center, double radians)
		{
		// Implement for rectangluar shapes defined by their bounds

		while (radians >= Math.PI*2)
			radians -= Math.PI*2;
		while (radians < 0)
			radians += Math.PI*2;

		MiCoord xmin = bounds.xmin;
		MiCoord ymin = bounds.ymin;
		MiCoord xmax = bounds.xmax;
		MiCoord ymax = bounds.ymax;

		invalidateArea();
		if (radians == 0)
			{
			// Swap Y
			ymin = center.y - (bounds.ymax - center.y);
			ymax = center.y - (bounds.ymin - center.y);
			replaceBounds(new MiBounds(xmin, ymin, xmax, ymax));
			}
		else if (radians == Math.PI/2)
			{
			// Swap X
			xmin = center.x - (bounds.xmax - center.x);
			xmax = center.x - (bounds.xmin - center.x);
			replaceBounds(new MiBounds(xmin, ymin, xmax, ymax));
			}
		else
			{
			rotate(center, -radians);
			ymin = center.y - (bounds.ymax - center.y);
			ymax = center.y - (bounds.ymin - center.y);
			replaceBounds(new MiBounds(xmin, ymin, xmax, ymax));
			rotate(center, radians);
			}

		refreshBounds();
		invalidateLayout();
		}

					/**------------------------------------------------------
	 				 * Rotates this MiPart to the given number of radians.
					 * This method modifies the transform and not the MiPart 
					 * itself. UNIMPLEMENTED.
	 				 * @param radians	the angle to rotate
					 *------------------------------------------------------*/
	public		void		rotateTo(double radians)
		{
		}
					/**------------------------------------------------------
	 				 * Get the center of rotation of this MiPart.
	 				 * @param rotationPoint	the (returned) center of rotation.
					 *------------------------------------------------------*/
	public		void		getCenterOfRotation(MiPoint rotationPoint)
		{
		rotationPoint.x = bounds.getCenterX();
		rotationPoint.y = bounds.getCenterY();
		}
					/**------------------------------------------------------
	 				 * Scales the bounds of this MiPart by the given x and y
					 * scale factors (multipliers) about it's center. Any 
					 * parts of this MiPart are also scaled in kind.
	 				 * @param x		the x scale factor
	 				 * @param y		the y scale factor
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
	 				 * @action 		Mi_SIZE_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		scale(double x, double y)
		{
		scale(new MiScale(x, y));
		}
					/**------------------------------------------------------
	 				 * Scales the bounds of this MiPart by the given scale
					 * factor (multipliers) about it's center. Any parts of 
					 * this MiPart are also scaled in kind.
	 				 * @param scale		the scale factor
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
	 				 * @action 		Mi_SIZE_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		scale(MiScale scale)
		{
		MiPoint center = bounds.getCenter();
		scale(center, scale);
		}
					/**------------------------------------------------------
	 				 * Scales the bounds of this MiPart by the given scale
					 * factor (multipliers). Any parts of this MiPart are also
					 * scaled in kind. Override this, if desired, as it
                                         * implements the core functionality.
	 				 * @param center	the center of the scaling
	 				 * @param scale		the scale factor
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
	 				 * @action 		Mi_SIZE_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		scale(MiPoint center, MiScale scale)
		{
		if (scale.isIdentity())
			return;

		if ((scale.x <= 0) || (scale.y <= 0))
			{
			throw new IllegalArgumentException(this + "[" + bounds + "]"
				+ ": Scale factor less than or equal to zero: " + scale);
			}
		if (!bounds.validate())
			throw new IllegalArgumentException(this + ": Bounds invalid before scale: " + bounds);

		if (bounds.isReversed())
			return;
		boolean fixedWidth = hasFixedWidth();
		boolean fixedHeight = hasFixedHeight();

		if (fixedWidth && fixedHeight)
			return;

		if (fixedWidth)
			scale = new MiScale(1.0, scale.y);
		if (fixedHeight)
			scale = new MiScale(scale.x, 1.0);

		optimizedInvalidateDrawBoundsArea();
		//optimizedInvalidateArea();
/*
		if (geomConstraint != null)
			scale = geomConstraint.enforceScaleRestrictions(new MiScale(scale));
*/

		bounds.setBounds(
			(bounds.xmin - center.x) * scale.x + center.x,
			(bounds.ymin - center.y) * scale.y + center.y,
			(bounds.xmax - center.x) * scale.x + center.x,
			(bounds.ymax - center.y) * scale.y + center.y);

		if (!scale.isIdentity())
			scaleParts(center, scale);
		
		if (!bounds.validate())
			throw new IllegalArgumentException(this + ": Bounds invalid after scale: " + scale + ", " + bounds);
		if (attachments != null)
			{
			// FIX: Only want this when attachments are sometimes ... text... so need an 
			// indicator for each attachment to see if it needs to scale
			//attachments.scale(center, scale);
			attachments.refreshBounds();
			}

		sizeChanged();
		geometryChanged();

		reCalcDrawBounds();
		invalidateLayout();
		}
					/**------------------------------------------------------
	 				 * Translates the bounds of this MiPart by the given x
					 * and y distances. Any parts of this MiPart are also
					 * translated in kind. Override this, if desired, as it
					 * implements the core functionality.
	 				 * @param x		the x distance to translate
	 				 * @param y		the y distance to translate
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
	 				 * @action 		Mi_POSITION_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		translate(MiDistance x, MiDistance y)
		{
		if (Utility.areZero(x, y))
			return;

		if (!bounds.validate())
			throw new IllegalArgumentException(this + ": Bounds invalid before translate: " + bounds);
		optimizedInvalidateDrawBoundsArea();
		//optimizedInvalidateArea();

		bounds.translate(x, y);
		if (drawBounds != bounds)
			drawBounds.translate(x, y);

		translateParts(x, y);

		if (!bounds.validate())
			throw new IllegalArgumentException(this + ": Bounds invalid after translate: " + bounds);

		if (attachments != null)
			{
			attachments.translate(x, y);
			attachments.refreshBounds();
			}

		optimizedInvalidateDrawBoundsArea();

		if ((getLayout() != null) && (!getLayout().isIndependantOfTargetPosition())
			&& (hasMiFlag(Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK)))
			{
			invalidateLayout();
			}
		else
			{
			invalidateContainersLayout();
			}

		positionChanged();
		geometryChanged();
		}
					/**------------------------------------------------------
	 				 * Translates the bounds of this MiPart by the given 
					 * vector. Any parts of this MiPart are also translated 
					 * in kind.
	 				 * @param vector 	the distances to translate
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
	 				 * @action 		Mi_POSITION_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		translate(MiVector vector)
		{
		translate(vector.x, vector.y);
		}
					/**------------------------------------------------------
	 				 * Translates the parts of this MiPart by the given 
					 * distances. 
	 				 * @param tx	 	the x translation
	 				 * @param ty	 	the y translation
					 *------------------------------------------------------*/
	protected	void		translateParts(MiDistance tx, MiDistance ty)
		{
		}
					/**------------------------------------------------------
	 				 * Scales the parts of this MiPart by the given scale
					 * factor.
	 				 * @param center 	the center of scaling
	 				 * @param scale	 	the scale factor
					 *------------------------------------------------------*/
	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		}
					/**------------------------------------------------------
	 				 * Translate this MiPart as a part of a translated 
					 * container and all of the parts of this MiPart as well.
					 * Because the container has been officially translated
					 * this part does not need to invalidate areas or layouts
					 * (unless the layout is !isIndependantOfTargetPosition()).
	 				 * @param tx	 	the x translation
	 				 * @param ty	 	the y translation
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
					 *------------------------------------------------------*/
	protected	void		translatePart(MiDistance tx, MiDistance ty)
		{
		if ((tx == 0) && (ty == 0))
			return;

		bounds.translate(tx, ty);
		if (drawBounds != bounds)
			drawBounds.translate(tx, ty);
		translateParts(tx, ty);
		if (attachments != null)
			attachments.translatePart(tx, ty);
		geometryChanged();
		if (drawManager != null)
			invalidateArea(drawBounds);
		if ((getLayout() != null) && (!getLayout().isIndependantOfTargetPosition())
			&& (hasMiFlag(Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK)))
			{
			invalidateLayout();
			}
		}
	
	//***************************************************************************************
	// Pick management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets whether the given area intersects the bounds of
					 * this MiPart.
	 				 * @param area	 	the area
	 				 * @return		true if the given area overlaps
					 *			the bounds of this MiPart.
					 *------------------------------------------------------*/
	public		boolean		pick(MiBounds area)
		{
		if ((visible) && (isPickable()) && (drawBounds.intersects(area)))
			return(true);
		return(false);
		}
					/**------------------------------------------------------
	 				 * Gets the topmost part, if any, whose bounds intersects 
					 * the given area.
	 				 * @param area	 	the area
	 				 * @return		the part
					 *------------------------------------------------------*/
	public		MiPart		pickObject(MiBounds area)
		{
		// FIX: this should be made into a static utility method with the container as a param.
		// and called pickPart
		if ((visible) && (isPickable()))
			{
			if ((attachments != null) && (attachments.getDrawBounds(new MiBounds()).intersects(area)))
				{
				MiPart obj;
				if ((obj = attachments.pickObject(area)) != null)
					return(obj);
				}
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the list of parts, including this MiPart, whose 
					 * bounds intersects the given area. The list is sorted 
					 * from topmost to bottommost part.
	 				 * @param area	 	the area
	 				 * @param list		the (returned) list of parts
					 *------------------------------------------------------*/
	public		void		pickDeepList(MiBounds area, MiParts list)
		{
		if ((visible) && (isPickable()) && (drawBounds.intersects(area)))
			{
			MiBounds newArea = MiBounds.newBounds();
			newArea.copy(area);
			if (getTransform() != null)
				{
				getTransform().dtow(newArea, newArea);
				}
			if (newArea.getWidth() < getMinimumPickAreaSize())
				{
				newArea.setWidth(getMinimumPickAreaSize());
				}
			if (newArea.getHeight() < getMinimumPickAreaSize())
				{
				newArea.setHeight(getMinimumPickAreaSize());
				}
			MiBounds b = MiBounds.newBounds();
			if ((attachments != null) && (attachments.getDrawBounds(b).intersects(newArea)))
				{
				attachments.pickDeepListAfter(newArea, list);
				}
			if (managedPointManagers != null)
				{
				for (int i = 0; i < managedPointManagers.length; ++i)
					{
					if (managedPointManagers[i] != null)
						managedPointManagers[i].pickDeepList(this, newArea, list);
					}
				}
			pickDeepListContents(newArea, list);
/*
			if (getTransform() != null)
				{
				getTransform().wtod(area, area);
				}
*/
			if ((attachments != null) && (attachments.getDrawBounds(b).intersects(area)))
				{
				attachments.pickDeepListBefore(area, list);
				}
			if (drawBounds.intersects(area) && (!(this instanceof MiAttachments))) 
				{
				if (pick(area))
					{
					list.addElement(this);
					}
				}
			MiBounds.freeBounds(b);
			MiBounds.freeBounds(newArea);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the list of parts, not including this MiPart, whose 
					 * bounds intersects the given area. The list is sorted 
					 * from topmost to bottommost part.
	 				 * @param area	 	the area
	 				 * @param list		the (returned) list of parts
					 * @return 		the list, possibly empty, of parts
					 *------------------------------------------------------*/
	public		void		pickDeepListContents(MiBounds area, MiParts list)
		{
		}

	//***************************************************************************************
	// Draw management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Sets the image buffer used for double buffering this
					 * MiPart to the given Image. This buffer is only used
					 * when enabled using setDoubleBuffered(). It is created
					 * automatically when this MiPart is drawn and if 
					 * getDoubleBuffered() is true.
	 				 * @param buffer 	the new double buffer
	 				 * @see			#setDoubleBuffered
					 *------------------------------------------------------*/
	public		void		setDoubleBuffer(Image buffer)
		{
		if (extensions == null)
			extensions = new MiPartExtensions(this);

		extensions.doubleBuffer = buffer;
		//if (doubleBuffered)
			invalidateArea();
		}
					/**------------------------------------------------------
	 				 * Gets the image buffer used for double buffering this
					 * MiPart. This buffer is only used when enabled using 
					 * setDoubleBuffered(). It is created automatically when 
					 * this MiPart is drawn and if getDoubleBuffered() is true.
	 				 * @return 	 	the image used for double buffering
	 				 * @see			#setDoubleBuffer
	 				 * @see			#setDoubleBuffered
					 *------------------------------------------------------*/
	public		Image		getDoubleBuffer()
		{
		if (extensions == null)
			return(null);
		return(extensions.doubleBuffer);
		}
					/**------------------------------------------------------
	 				 * Specifies whether this MiPart is to use a double buffer. 
					 * This will be quicker for complex MiParts. This MiPart 
					 * will draw itself to the double buffer first if it has 
					 * changed. It will always draw itself to the screen from
					 * the double buffer. The double buffering image will be
					 * automatically created. It may be examined using 
					 * getDoubleBuffer().
	 				 * @param flag 	 	true to enable double buffering
	 				 * @see			#getDoubleBuffer
	 				 * @see			#isDoubleBuffered
					 *------------------------------------------------------*/
	public		void		setDoubleBuffered(boolean flag)
		{
		setMiFlag(Mi_DOUBLE_BUFFERED_MASK, flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether this MiPart is using a double buffer. 
	 				 * @return  	 	true if double buffering enabled
	 				 * @see			#getDoubleBuffer
	 				 * @see			#setDoubleBuffered
					 *------------------------------------------------------*/
	public		boolean		isDoubleBuffered()
		{
		return(hasMiFlag(Mi_DOUBLE_BUFFERED_MASK));
		}
					/**------------------------------------------------------
	 				 * Makes and returns an Image of the given area of this
					 * MiPart.
	 				 * @param area 	 	the area of this MiPart to get
	 				 * @return  	 	the Image of the given area
					 *------------------------------------------------------*/
	public		Image		makeImageFromArea(MiBounds area)
		{
		MiRenderer renderer = MiUtility.getRenderer(this);
		validateLayout();
		MiBounds bounds = new MiBounds(drawBounds);
		renderer.setClipBounds(bounds);

		bounds.copy(area);
		if (!renderer.isDoubleBuffered())
			{
			if (!hasMiFlag(Mi_DOUBLE_BUFFERED_MASK))
				{
				if (extensions == null)
					extensions = new MiPartExtensions(this);

				extensions.doubleBuffer = renderer.makeDoubleBuffer(bounds);

				renderer.pushDoubleBuffer(extensions.doubleBuffer, area);
				renderer.setClipBounds(area);

				renderer.setColor(MiColorManager.white);
				renderer.drawFillRect(area);

				doRender(renderer);

				renderer.popDoubleBuffer(extensions.doubleBuffer);

				Image image = extensions.doubleBuffer;
				extensions.doubleBuffer = null;
				return(image);
				}
			renderer.pushDoubleBuffer(extensions.doubleBuffer, drawBounds);
			Image image = renderer.makeImageFromArea(extensions.doubleBuffer, bounds);
			renderer.popDoubleBuffer(extensions.doubleBuffer);
			return(image);
			}
		return(renderer.makeImageFromArea(bounds));
		}
					/**------------------------------------------------------
	 				 * Draws this MiPart without bothering to check the clip 
					 * bounds. This is useful when the caller wants to minutely
					 * manage graphics, for example during XOR rubberbanding.
					 *------------------------------------------------------*/
	public		void		drawNow()
		{
		drawNow(drawBounds);
		}
					/**------------------------------------------------------
	 				 * Draws this MiPart without bothering to check the clip 
					 * bounds. This is useful when the caller wants to minutely
					 * manage graphics, for example during XOR rubberbanding.
	 				 * @param b	 	the clip bounds to use.
					 *------------------------------------------------------*/
	public		void		drawNow(MiBounds b)
		{
		if (isShowing(b))
			{
			MiRenderer renderer = getContainingEditor().getRenderer();
			if ((renderer == null) || (renderer.getWindowSystemRenderer() == null))
				return;

			renderer.setClipBounds(b);
			doRender(renderer);
			}
		}
					/**------------------------------------------------------
	 				 * Draws this MiPart without bothering to check the clip 
					 * bounds. This is useful when the caller has not 
					 * transformed the clip bounds.
	 				 * @param renderer 	the renderer to use for drawing
					 *------------------------------------------------------*/
	protected	void		drawNoClip(MiRenderer renderer)
		{
		if (invalidArea != Mi_NONE)
			{
			resetMiFlag(Mi_DOUBLE_BUFFER_VALID_MASK);
			invalidArea = Mi_NONE;
			}

		if ((!visible) || (isHidden()))
			return;

		doRender(renderer);
		}
					/**------------------------------------------------------
	 				 * Draws the attachments of this MiPart.
	 				 * @param renderer 	the renderer to use for drawing
					 *------------------------------------------------------*/
	protected	void		drawAttachments(MiRenderer renderer)
		{
		if ((!visible) || (isHidden()))
			return;

		if ((drawManager != null) && (!drawManager.isDrawing()))
			return;

		if ((attachments != null) && (attachments.isVisible()))
			{
			attachments.drawAfter(renderer);
			}
		invalidArea = Mi_NONE;
		}
					/**------------------------------------------------------
	 				 * Draws this MiPart. First checks for whether it is
					 * visible and not hidden and not clipped.
	 				 * @param renderer 	the renderer to use for drawing
	 				 * @action		Mi_DRAW_ACTION
					 *------------------------------------------------------*/
	protected	void		draw(MiRenderer renderer)
		{
		if (invalidArea != Mi_NONE)
			{
			resetMiFlag(Mi_DOUBLE_BUFFER_VALID_MASK);
			invalidArea = Mi_NONE;
			}

		if (MiDebug.debug && MiDebug.isTracing(this, MiDebug.TRACE_DRAWING_OF_PARTS))
			{
			MiDebug.println("About to draw part: " + this);
			if (!visible)
				MiDebug.println("NOT drawing because invisible");
			if (isHidden())
				MiDebug.println("NOT drawing because hidden");
			if ((drawManager != null) && (!drawManager.isDrawing()))
				MiDebug.println("NOT drawing because subWindow");
			if ((renderer.isPrinterRenderer()) && (!isPrintable()))
				MiDebug.println("NOT printing because NOT printable");
			if (renderer.boundsClipped(drawBounds))
				{
				MiDebug.println("NOT drawing because clipped: drawBounds = " 
					+ drawBounds + ", Clip bounds: " + renderer.getClipBounds());
				}
			}

		MiPart part = renderer.filterPart(this);
		if (part != this)
			{
			if (part == null)
				return;
			part.draw(renderer);
			return;
			}


		if ((!visible) || (isHidden()))
			return;

		if ((renderer.isPrinterRenderer()) && (!isPrintable()))
			return;

		if ((drawManager != null) && (!drawManager.isDrawing()) && (!renderer.isPrinterRenderer()))
			return;

		// FIX: 45 degree lines etc are only grossly rejected here, do pick
		if (!renderer.boundsClipped(drawBounds)) 
			{
			doRender(renderer);
			}
		invalidArea = Mi_NONE;
		if (hasMiFlag(Mi_DRAW_ACTIONS_DESIRED_MASK))
			{
			dispatchAction(Mi_DRAW_ACTION);
			}
		}
					/**------------------------------------------------------
	 				 * Renders this MiPart.
					 *
					 * If double buffered 
					 *   If double buffer is valid
					 *	renders from the double buffer
					 *   else
					 *      creates the double buffer if necessary
					 *      renders to the double buffer
					 *      renders from the double buffer
					 * else
					 *   If this MiPart has custom renderers
					 *      the shadow renderer, if assigned, is called
					 *      the line ends renderer, if assigned, is called
					 *      the before renderer, if assigned, is called
					 *      this part's render(MiRenderer) method is called
					 *      the connection point renderer, if assigned, is called
					 *      the after renderer, if assigned, is called
					 *   else
					 *      this part's render(MiRenderer) method is called
					 *   this part's attachments are drawn (drawAttachments())
					 *
	 				 * @param renderer 	the renderer to use for drawing
					 *------------------------------------------------------*/
	private		void		doRender(MiRenderer renderer)
		{
		if (hasMiFlag(Mi_DOUBLE_BUFFERED_MASK))
			{
			if ((extensions.doubleBuffer == null) || (!hasMiFlag(Mi_DOUBLE_BUFFER_VALID_MASK)))
				{
				preRenderToDoubleBuffer(renderer);
				}
			if (MiDebug.debug 
				&& MiDebug.isTracing(this, MiDebug.TRACE_DOUBLE_BUFFER_RENDERING))
				{
				MiDebug.println(this + ": render double buffer");
				}
			//tmpBounds.copy(drawBounds);
			//tmpBounds.xmax += 1;
			//tmpBounds.ymax += 1;
			MiBounds tmpBounds = MiBounds.newBounds();
			MiBounds b = renderer.getClipBounds();
			tmpBounds.copy(b);
			if (tmpBounds.intersectionWith(drawBounds))
				renderer.setClipBounds(tmpBounds);
			renderer.drawImage(extensions.doubleBuffer, drawBounds);
			renderer.setClipBounds(b);
			MiBounds.freeBounds(tmpBounds);
			}
		else if (hasMiFlag(Mi_HAS_CUSTOM_RENDERERS_MASK))
			{
			boolean r = true;

			if (getHasShadow() && (attributes.objectAttributes[Mi_SHADOW_RENDERER] != null))
			    {
			    ((MiiPartRenderer )attributes.objectAttributes[Mi_SHADOW_RENDERER]).render(this, renderer);
			    }
/*
			if (attributes.objectAttributes[Mi_LINE_ENDS_RENDERER] != null)
			    {
			    ((MiiPartRenderer )attributes.objectAttributes[Mi_LINE_ENDS_RENDERER]).render(this, renderer);
			    }
*/
			if (attributes.objectAttributes[Mi_BEFORE_RENDERER] != null)
			    {
			    r = ((MiiPartRenderer)attributes.objectAttributes[Mi_BEFORE_RENDERER]).render(this, renderer);
			    }
			if (r)
			    {
			    render(renderer);
			    }
			if ((r) && (managedPointManagers != null))
				{
				for (int i = 0; i < managedPointManagers.length; ++i)
					{
					if (managedPointManagers[i] != null)
			    			managedPointManagers[i].render(this, renderer);
					}
				}
			if ((r) && (attributes.objectAttributes[Mi_AFTER_RENDERER] != null))
			    {
			    ((MiiPartRenderer)attributes.objectAttributes[Mi_AFTER_RENDERER]).render(this, renderer);
			    }
			if ((attachments != null) && (drawManager == null))
				drawAttachments(renderer);
			}
		else
			{
			render(renderer);
			if ((attachments != null) && (drawManager == null))
				drawAttachments(renderer);
			}
		}

					/**------------------------------------------------------
	 				 * Draws this MiPart to the double buffer, if any.
	 				 * @param renderer 	the renderer to use for drawing
					 *------------------------------------------------------*/
	protected	void		preRenderToDoubleBuffer(MiRenderer renderer)
		{
		if (hasMiFlag(Mi_DOUBLE_BUFFERED_MASK))
			{
			if (extensions.doubleBuffer == null)
				{
				if (MiDebug.debug 
					&& MiDebug.isTracing(this, MiDebug.TRACE_DOUBLE_BUFFER_RENDERING))
					{
					MiDebug.println(this + ": make double buffer");
					}
				MiBounds tmpBounds = MiBounds.newBounds();
				extensions.doubleBuffer = renderer.makeDoubleBuffer(tmpBounds.copy(drawBounds));
				MiBounds.freeBounds(tmpBounds);
				resetMiFlag(Mi_DOUBLE_BUFFER_VALID_MASK);
				}
			if (!hasMiFlag(Mi_DOUBLE_BUFFER_VALID_MASK))
				{
				if (MiDebug.debug 
					&& MiDebug.isTracing(this, MiDebug.TRACE_DOUBLE_BUFFER_RENDERING))
					{
					MiDebug.println(this + ": re-render into double buffer");
					}
				renderer.pushDoubleBuffer(extensions.doubleBuffer, drawBounds);
				resetMiFlag(Mi_DOUBLE_BUFFERED_MASK);
				doRender(renderer);
				setMiFlag(Mi_DOUBLE_BUFFERED_MASK);
				renderer.popDoubleBuffer(extensions.doubleBuffer);
				setMiFlag(Mi_DOUBLE_BUFFER_VALID_MASK);
				invalidArea = Mi_NONE;
				}
			}
		}
					/**------------------------------------------------------
	 				 * This, the default render(MiRenderer) method calls
					 * the renderToDevice() method.
	 				 * @param renderer 	the renderer to use for drawing
					 *------------------------------------------------------*/
	protected	void		render(MiRenderer renderer)
		{
		MiBounds deviceBounds = new MiBounds();
		renderer.getTransform().wtod(bounds, deviceBounds);
		MiCoord tmp = renderer.getYmax() - deviceBounds.getYmin();
		deviceBounds.setYmin(renderer.getYmax() - deviceBounds.getYmax());
		deviceBounds.setYmax(tmp);
		renderToDevice(renderer.getWindowSystemRenderer(), attributes, deviceBounds);
		}

					/**------------------------------------------------------
	 				 * This is provided for those subclasses who want use
					 * java.awt.graphics to render their shapes.
	 				 * @param graphics 	the AWT graphics object
	 				 * @param attributes 	the attributes of this MiPart
	 				 * @param deviceBounds 	the bounds, in pixels, of this 
					 *			MiPart
					 *------------------------------------------------------*/
	protected	void		renderToDevice(java.awt.Graphics graphics, MiAttributes attributes, MiBounds deviceBounds)
		{
		}

					/**------------------------------------------------------
	 				 * Pauses the current thread until this MiPart is redrawn.
					 * Returns immediately if this MiPart does not need to be
					 * redrawn (i.e. it nor it's parts have invalid areas).
					 *------------------------------------------------------*/
	public		void		waitUntilRedrawn()
		{
		if (hasMiFlag(Mi_THIS_OR_PART_HAS_INVALID_AREA_MASK))
			new MiWaitUntilActionHandler(this, Mi_DRAW_ACTION, false);
		}

	//***************************************************************************************
	// Attachment management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Append the given part to the attachments of this MiPart.
	 				 * @param obj	 	the attachment to append to this
					 *------------------------------------------------------*/
	public		void		appendAttachment(MiPart obj)
		{
		appendAttachment(obj, Mi_NONE, null, null);
		}
					/**------------------------------------------------------
	 				 * Append the given part to the attachments of this MiPart.
					 * The location can be one of the locations below, the
					 * number of a point (i.e. a value of 2 would attach the given
					 * part to the 3rd point of a polyline), or Mi_LAST_POINT_NUMBER.
					 * Margins are treated specially for Mi_LINE_xxx locations; 
					 * margins.left is the distance from the closest 'end' of the
					 * line and margins.bottom is the orthogonal distance from the 
					 * line itself.
					 *
					 *   Mi_CENTER_LOCATION
        				 *   Mi_LEFT_LOCATION
        				 *   Mi_RIGHT_LOCATION
        				 *   Mi_BOTTOM_LOCATION
        				 *   Mi_TOP_LOCATION
        				 *   Mi_LOWER_LEFT_LOCATION
        				 *   Mi_LOWER_RIGHT_LOCATION
        				 *   Mi_UPPER_LEFT_LOCATION
        				 *   Mi_UPPER_RIGHT_LOCATION
        				 *   Mi_INSIDE_LEFT_LOCATION
        				 *   Mi_INSIDE_RIGHT_LOCATION
        				 *   Mi_INSIDE_BOTTOM_LOCATION
        				 *   Mi_INSIDE_TOP_LOCATION
        				 *   Mi_INSIDE_LOWER_LEFT_LOCATION
        				 *   Mi_INSIDE_LOWER_RIGHT_LOCATION
        				 *   Mi_INSIDE_UPPER_LEFT_LOCATION
        				 *   Mi_INSIDE_UPPER_RIGHT_LOCATION
					 *   Mi_OUTSIDE_LEFT_LOCATION
					 *   Mi_OUTSIDE_RIGHT_LOCATION
					 *   Mi_OUTSIDE_BOTTOM_LOCATION
					 *   Mi_OUTSIDE_TOP_LOCATION
        				 *   Mi_OUTSIDE_LOWER_LEFT_LOCATION
        				 *   Mi_OUTSIDE_LOWER_RIGHT_LOCATION
        				 *   Mi_OUTSIDE_UPPER_LEFT_LOCATION
        				 *   Mi_OUTSIDE_UPPER_RIGHT_LOCATION
					 *   Mi_WNW_LOCATION
					 *   Mi_WSW_LOCATION
					 *   Mi_ENE_LOCATION
					 *   Mi_ESE_LOCATION
					 *   Mi_NWN_LOCATION
					 *   Mi_NEN_LOCATION
					 *   Mi_SWS_LOCATION
					 *   Mi_SES_LOCATION
					 *   Mi_START_LOCATION
					 *   Mi_END_LOCATION 
					 *   Mi_LINE_CENTER_LOCATION
					 *   Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION
					 *   Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION
					 *   Mi_LINE_START_LOCATION
					 *   Mi_LINE_START_TOP_OR_RIGHT_LOCATION
					 *   Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION
					 *   Mi_LINE_END_LOCATION
					 *   Mi_LINE_END_TOP_OR_RIGHT_LOCATION
					 *   Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION
        				 *   Mi_SURROUND_LOCATION
        				 *   Mi_ALONG_LOCATION
					 *
	 				 * @param obj	 	the attachment to append to this
	 				 * @param location	the location relative to this 
					 *			MiPart to put the attachment
	 				 * @param tag	 	the tag to be used to identify
					 *			the attachment
	 				 * @param margins 	the margin to be used to adjust
					 *			the location of the attachment
					 *------------------------------------------------------*/
	public		void		appendAttachment(MiPart obj, int location, String tag, MiMargins margins)
		{
		if (attachments == null)
			{
			attachments = new MiAttachments(this);
			attachments.addActionsToDispatchToContainers(containersRequestedActions);
			attachments.addActionsToDispatchToContainers(actionsRequestedFromParts);
			}
		attachments.appendAttachment(obj, location, tag, margins);
		if (obj.hasEventHandlers())
			invalidateCachedEventHandlerInfo();
		reCalcDrawBounds();
		}
					/**------------------------------------------------------
	 				 * Get the number of attachments this MiPart has.
	 				 * @return  		the number of attachments
					 *------------------------------------------------------*/
	public		int		getNumberOfAttachments()
		{
		return(attachments == null ? 0 : attachments.getNumberOfParts());
		}
					/**------------------------------------------------------
	 				 * Get this MiPart's attachment at the given index 
					 * (starting from 0).
	 				 * @param index 	the index of the attachment
	 				 * @return  		the attachment
					 *------------------------------------------------------*/
	public		MiPart		getAttachment(int index)
		{
		return(attachments.getPart(index));
		}
					/**------------------------------------------------------
	 				 * Get this MiPart's attachment with the given tag. 
	 				 * @param tag 		the tag of the attachment
	 				 * @return  		the attachment
					 *------------------------------------------------------*/
	public		MiPart		getAttachment(String tag)
		{
		return(attachments == null ? null : attachments.getAttachment(tag));
		}
					/**------------------------------------------------------
	 				 * Set this MiPart's attachment's margins with the given tag. 
	 				 * @param tag 		the tag of the attachment
	 				 * @param margins 	the margins of the attachment
					 *------------------------------------------------------*/
	public		void		setAttachmentMargins(String tag, MiMargins margins)
		{
		if (attachments != null)
			attachments.setAttachmentMargins(tag, margins);
		}
					/**------------------------------------------------------
	 				 * Get this MiPart's attachment's margins with the given tag. 
	 				 * @param tag 		the tag of the attachment
	 				 * @return  		the margins
					 *------------------------------------------------------*/
	public		MiMargins	getAttachmentMargins(String tag)
		{
		return(attachments == null ? null : attachments.getAttachmentMargins(tag));
		}
					/**------------------------------------------------------
	 				 * Remove all of this MiPart's attachments.
					 *------------------------------------------------------*/
	public		void		removeAllAttachments()
		{
		if (attachments != null)
			{
			attachments.removeAllAttachments();
			attachments.setAttachedToObject(null);
			attachments = null;
			reCalcDrawBounds();
			}
		}
					/**------------------------------------------------------
	 				 * Remove this MiPart's attachment with the given tag. 
	 				 * @param tag 		the tag of the attachment
					 *------------------------------------------------------*/
	public		void		removeAttachment(String tag)
		{
		attachments.removeAttachment(tag);
		if (attachments.getNumberOfParts() == 0)
			{
			attachments.setAttachedToObject(null);
			attachments = null;
			}
		reCalcDrawBounds();
		}
					/**------------------------------------------------------
	 				 * Remove the given attachment from this MiPart.
	 				 * @param obj 		the attachment to remove
					 *------------------------------------------------------*/
	public		void		removeAttachment(MiPart obj)
		{
		if (attachments == null)
			{
			throw new IllegalArgumentException(
				this + ".removeAttachment: object not found in this container; object = " + obj);
			}
		attachments.removeAttachment(obj);
		if (attachments.getNumberOfParts() == 0)
			{
			attachments.setAttachedToObject(null);
			attachments = null;
			}
		reCalcDrawBounds();
		}
					/**------------------------------------------------------
	 				 * Gets whether this MiPart has the given attachment.
	 				 * @param obj 		the attachment to check for
	 				 * @return  		true if this MiPart has obj as an
					 *			attachment
					 *------------------------------------------------------*/
	public		boolean		hasAttachment(MiPart obj)
		{
		return((attachments == null) ? false : attachments.hasAttachment(obj));
		}

					/**------------------------------------------------------
	 				 * Sets the visibility of the attachments container.
	 				 * @param flag 		true if visible (the default)
					 *------------------------------------------------------*/
	public		void		setAttachmentsVisibility(boolean flag)
		{
		if (attachments != null)
			{
			attachments.setVisible(flag);
			}
		}
	//***************************************************************************************
	// Container contents management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the number of parts this container has.
	 				 * @return  		the number of parts
					 *------------------------------------------------------*/
	public		int		getNumberOfParts()
		{ return(0); 	}
					/**------------------------------------------------------
	 				 * Gets the part of this container at the given index.
	 				 * @param index 	the index of the part to get
	 				 * @return  		the part at the index
					 *------------------------------------------------------*/
	public		MiPart		getPart(int index)
		{ return(null); }
					/**------------------------------------------------------
	 				 * Gets the part of this container with the given name.
	 				 * @param name	 	the name of the part to get
	 				 * @return  		the part or null
					 *------------------------------------------------------*/
	public		MiPart		getPart(String name)
		{ return(null); }
					/**------------------------------------------------------
	 				 * Appends the given part to this container.
	 				 * @param part 		the part to append
	 				 * @exception		IllegalArgumentException if append
					 *			a part to itself
					 *------------------------------------------------------*/
	public		void		appendPart(MiPart obj)
		{ }
					/**------------------------------------------------------
	 				 * Inserts the given part at the given index into this
					 * container.
	 				 * @param part 		the part to insert
	 				 * @param index 	where to insert the part
	 				 * @exception		IllegalArgumentException if add
					 *			a part to itself
					 *------------------------------------------------------*/
	public		void		insertPart(MiPart obj, int index)
		{ }
					/**------------------------------------------------------
	 				 * Replaces the part at the given index with the given 
					 * part.
	 				 * @param part 		the part to insert
	 				 * @param index 	where to place the part
					 *------------------------------------------------------*/
	public		void		setPart(MiPart part, int index)
		{ }
					/**------------------------------------------------------
	 				 * Removes the part from this container at the given index.
	 				 * @param index 	index of the part to remove.
					 *------------------------------------------------------*/
	public		void		removePart(int index)
		{ }
					/**------------------------------------------------------
	 				 * Removes the given part from this container.
	 				 * @param part 		the part to remove.
					 *------------------------------------------------------*/
	public		void		removePart(MiPart part) 		
		{ }
					/**------------------------------------------------------
	 				 * Gets the index of the given part in this container.
	 				 * @param part 		the part to get the index of.
					 * @return		the index of the part or -1 if no
					 *			such part exists in this container
					 *------------------------------------------------------*/
	public		int		getIndexOfPart(MiPart part)
		{ return(-1); 	}
					/**------------------------------------------------------
	 				 * Gets whether the given part is in this container.
	 				 * @param part 		the part to check for
					 * @return		true if this container contains
					 *			the part
					 *------------------------------------------------------*/
	public		boolean		containsPart(MiPart part)
		{ return(false);}
					/**------------------------------------------------------
	 				 * Removes all parts from this container.
					 *------------------------------------------------------*/
	public		void		removeAllParts()
		{ }
					/**------------------------------------------------------
	 				 * Gets whether the given part is in this container or in
					 * one of the parts of this container.
	 				 * @param part 		the part to check for
					 * @return		true if this container or one of
					 *			it's parts contains the part
					 *------------------------------------------------------*/
	public		boolean		isContainerOf(MiPart part)
		{ return(false);}

					/**------------------------------------------------------
	 				 * Gets the part with the given name in this container or in
					 * one of the parts of this container.
	 				 * @param name 		the name of the part to look for
					 * @return		the first part found with the given
					 *			name or null
					 *------------------------------------------------------*/
	public		MiPart		isContainerOf(String name)
		{ return(null);}

	public		MiPart		isContainerOfWithAttachments(String name)
		{
		if (getAttachments() != null)
			{
			return(getAttachments().getPart(name));
			}
		return(null);
		}

					/**------------------------------------------------------
	 				 * Gets the number of items this container has.
	 				 * @return  		the number of items
					 *------------------------------------------------------*/
	public		int		getNumberOfItems()
		{ 
		return(getNumberOfParts()); 
		}
					/**------------------------------------------------------
	 				 * Gets the item of this container at the given index.
	 				 * @param index 	the index of the item to get
	 				 * @return  		the item at the index
					 *------------------------------------------------------*/
	public		MiPart		getItem(int index)
		{
		return(getPart(index));
		}
					/**------------------------------------------------------
	 				 * Gets the item of this container with the given name.
	 				 * @param name	 	the name of the item to get
	 				 * @return  		the item or null
					 *------------------------------------------------------*/
	public		MiPart		getItem(String name)
		{
		return(getPart(name));
		}
					/**------------------------------------------------------
	 				 * Appends the given item to this container.
	 				 * @param item 		the item to append
					 *------------------------------------------------------*/
	public		void		appendItem(MiPart item)
		{ 
		appendPart(item); 
		}
					/**------------------------------------------------------
	 				 * Inserts the given item at the given index into this
					 * container.
	 				 * @param item 		the item to insert
	 				 * @param index 	where to insert the item
					 *------------------------------------------------------*/
	public		void		insertItem(MiPart item, int index)
		{
		insertPart(item, index);
		}
					/**------------------------------------------------------
	 				 * Replaces the item at the given index with the given 
					 * item.
	 				 * @param item 		the item to insert
	 				 * @param index 	where to place the item
					 *------------------------------------------------------*/
	public		void		setItem(MiPart item, int index)
		{
		setPart(item, index);
		}
					/**------------------------------------------------------
	 				 * Removes the item from this container at the given index.
	 				 * @param index 	index of the item to remove.
					 *------------------------------------------------------*/
	public		void		removeItem(int index)
		{
		removePart(index);
		}
					/**------------------------------------------------------
	 				 * Removes the given item from this container.
	 				 * @param item 		the item to remove.
					 *------------------------------------------------------*/
	public		void		removeItem(MiPart item)
		{
		removePart(item);
		}
					/**------------------------------------------------------
	 				 * Gets the index of the given item in this container.
	 				 * @param item 		the item to get the index of.
					 * @return		the index of the item or -1 if no
					 *			such item exists in this container
					 *------------------------------------------------------*/
	public		int		getIndexOfItem(MiPart item)
		{
		return(getIndexOfPart(item));
		}
					/**------------------------------------------------------
	 				 * Gets whether the given item is in this container.
	 				 * @param item 		the item to check for
					 * @return		true if this container contains
					 *			the item
					 *------------------------------------------------------*/
	public		boolean		containsItem(MiPart item)
		{
		return(containsPart(item));
		}
					/**------------------------------------------------------
	 				 * Removes all items from this container.
					 *------------------------------------------------------*/
	public		void		removeAllItems()
		{
		removeAllParts();
		}
	public		MiParts		getAssociatedParts(MiParts parts)
		{
		if ((getContextMenu() != null)
			&& (getContextMenu().getMenuGraphics().getContainer(0) == null))
			{
			parts.addElement(getContextMenu().getMenuGraphics());
			}
		return(parts);
		}

	//***************************************************************************************
	// Containers management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the number of containers of this MiPart. NOTE: at
					 * the current time the first container is considered
					 * 'special', as the one that determines the 'home' editor
					 * or window this parts is in. The other containers are
					 * considered auxilary views - probably references.
					 * @return		the number of containers
					 *------------------------------------------------------*/
	public		int		getNumberOfContainers()
		{
		return(containers == null ? 0 : containers.length);
		}
					/**------------------------------------------------------
	 				 * Gets the container at the given index. 
	 				 * @param index 	the index of the container to get
					 * @return		the part at the given index 
					 *------------------------------------------------------*/
	public		MiPart		getContainer(int index)
		{
		if ((containers == null) || (index >= containers.length))
			return(null);
		return(containers[index]);
		}

// TEST
	protected	void		setContainer(MiPart part)
		{
		containers = new MiPart[1];
		containers[0] = part;
		}
					/**------------------------------------------------------
	 				 * Appends the container to the list of containers of this
					 * MiPart.
	 				 * @param container 	the container to append
	 				 * @exception		IllegalArgumentException if append
					 *			a container to itself
					 *------------------------------------------------------*/
	public		void		appendContainer(MiPart container)
		{
		if (container == this)
			throw new IllegalArgumentException(this + ": Appending Container to itself");

		if (containers != null)
			{
			MiPart[] containerList = new MiPart[containers.length + 1];
			for (int i = 0; i < containers.length; ++i)
				containerList[i] = containers[i];
			containerList[containers.length] = container;
			containers = containerList;
			}
		else
			{
			containers = new MiPart[1];
			containers[0] = container;
			}
		addActionsToDispatchToContainers(container.containersRequestedActions);
		addActionsToDispatchToContainers(container.actionsRequestedFromParts);
		}
					/**------------------------------------------------------
	 				 * Inserts the container into the list of containers of this
					 * MiPart at the given index.
	 				 * @param container 	the container to insert
	 				 * @param index	 	the index of the insertion point
	 				 * @exception		IllegalArgumentException if add
					 *			a container to itself
					 *------------------------------------------------------*/
	public		void		insertContainer(MiPart container, int index)
		{
		if (container == this)
			throw new IllegalArgumentException(this + ": Adding Container to itself");

		if (containers == null)
			{
			containers = new MiPart[1];
			containers[0] = container;
			}
		else
			{
			MiPart[] list = new MiPart[containers.length + 1];
			int j = 0;
			for (int i = 0; i < list.length; ++i)
				{
				if (i != index)
					list[i] = containers[j++];
				else
					list[i] = container;
				}
			containers = list;
			}

		addActionsToDispatchToContainers(container.containersRequestedActions);
		addActionsToDispatchToContainers(container.actionsRequestedFromParts);
		}
					/**------------------------------------------------------
	 				 * Removes the container from the list of containers of this
					 * MiPart at the given index.
	 				 * @param index	 	the index of the container to remove
					 *------------------------------------------------------*/
	public		void		removeContainer(int index)
		{
		if (containers.length == 1)
			{
			containers = null;
			}
		else
			{
			MiPart[] list = new MiPart[containers.length - 1];
			int j = 0;
			for (int i = 0; i < list.length; ++i)
				{
				if (i != index)
					list[j++] = containers[i];
				}
			containers = list;
			}
		reCalcActionsToDispatchToContainers();
		}
					/**------------------------------------------------------
	 				 * Removes the container from the list of containers.
	 				 * @param container	the container to remove
					 *------------------------------------------------------*/
	public		void		removeContainer(MiPart container)
		{
		if (containers.length == 1)
			{
			containers = null;
			}
		else
			{
			// ---------------------------------------------------------------
			// Note: this algorithm does not work if this part is contained
			// in a single container multiple times. i.e. the given container
			// is found > 1 time in the array of containers. If this is true
			// then reCalcActionsToDispatchToContainers below will generate
			// a null ptr exception. But it does not seem like this feature will
			// ever be needed, so appendPart and insertPart check for a part
			// being added to a container it is already in.
			// ---------------------------------------------------------------
			MiPart[] list = new MiPart[containers.length - 1];
			int j = 0;
			for (int i = 0; i < containers.length; ++i)
				{
				if (containers[i] != container)
					list[j++] = containers[i];
				}
			containers = list;
			}
		reCalcActionsToDispatchToContainers();
		}

	//***************************************************************************************
	// Bounds management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the inner bounds of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
	 				 * @param b		the (returned) inner bounds
	 				 * @return 		the inner bounds
					 *------------------------------------------------------*/
	public		MiBounds	getInnerBounds(MiBounds b)
		{
		return(b.copy(bounds));
		}
					/**------------------------------------------------------
	 				 * Gets the inner bounds of this MiPart. 
	 				 * @return 		the inner bounds
					 *------------------------------------------------------*/
	public		MiBounds	getInnerBounds()
		{
		return(getInnerBounds(new MiBounds()));
		}
					/**------------------------------------------------------
	 				 * Gets the outer bounds of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
	 				 * @param b		the (returned) outer bounds
	 				 * @return 		the outer bounds
					 *------------------------------------------------------*/
	public		MiBounds	getBounds(MiBounds b)
		{
		return(b.copy(bounds));
		}
					/**------------------------------------------------------
	 				 * Gets the outer bounds of this MiPart.
	 				 * @return 		the outer bounds
					 *------------------------------------------------------*/
	public		MiBounds	getBounds()
		{
		return(getBounds(new MiBounds()));
		}
					/**------------------------------------------------------
	 				 * Sets the preferred size of the outer bounds of this 
					 * MiPart. This size, when not null, will override the
					 * preferred size of this MiPart which is usually 
					 * determined by calling the (usually overridden)
					 * calcPreferredSize() method.
	 				 * @param size 		the preferred size or null
					 *------------------------------------------------------*/
	public		void		setPreferredSize(MiSize size)
		{
		if (size != null)
			{
			if (preferredSize == null)
				{
				preferredSize = MiSize.newSize();
				}
			else if (size.equals(preferredSize))
				{
				// Add after 2/14/2003 release return;
				}
			preferredSize.copy(size);
			setMiFlag(Mi_PREFERRED_SIZE_OVERRIDDEN_MASK);
			}
		else
			{
			if (hasMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK))
				{
				throw new RuntimeException(
				"MICA: Setting preferred size to null during calculation of preferred or minimum size");
				}
			if (preferredSize != null)
				{
				MiSize.freeSize(preferredSize);
				}
			else if (preferredSize == null)
				{
				// Add after 2/14/2003 release return;
				}
				
			preferredSize = null;
			resetMiFlag(Mi_PREFERRED_SIZE_OVERRIDDEN_MASK);
			}
		invalidateLayout();
		}
					/**------------------------------------------------------
	 				 * Gets whether the preferred size has been specified (and
					 * therefore overridden) by using setPeferredSize().
	 				 * @return 		true if overridden
					 *------------------------------------------------------*/
	public		boolean		hasOverriddenPreferredSize()
		{
		return(hasMiFlag(Mi_PREFERRED_SIZE_OVERRIDDEN_MASK));
		}
					/**------------------------------------------------------
	 				 * Gets the preferred size of this MiPart. This method
					 * is usually called by various layouts. If a preferred
					 * size has not been specified (by using setPreferredSize())
					 * then this method calls calcPreferredSize() to get the
					 * size.
	 				 * @param size 		the (returned) preferred size
	 				 * @return 		the preferred size
					 *------------------------------------------------------*/
	public		MiSize		getPreferredSize(MiSize size)
		{
		if (preferredSize == null)
			//|| ((!hasMiFlag(Mi_PREFERRED_SIZE_OVERRIDDEN_MASK)) 
				//&& (!hasValidLayout()) && (!hasMiFlag(Mi_VALIDATING_LAYOUT_MASK))))
			{
			//if (preferredSize == null)
				preferredSize = MiSize.newSize();
			setMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK);
			calcPreferredSize(preferredSize);
			resetMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK);

			/*** GOOD but a little slow! */
			MiSize tmpSize = MiSize.newSize();
			getMinimumSize(tmpSize);
			if (preferredSize.isSmallerSizeThan(tmpSize))
				preferredSize.union(tmpSize);
			MiSize.freeSize(tmpSize);
			}
		size.copy(preferredSize);
		return(size);
		}
					/**------------------------------------------------------
	 				 * Gets the preferred size of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
					 * The default behavior is to return the current bounds,
					 * or, if reversed, a zero size.
	 				 * @param size		the (returned) preferred size
					 *------------------------------------------------------*/
	protected	void		calcPreferredSize(MiSize size)
		{
		if (bounds.isReversed())
			size.zeroOut();
		else
			size.setSize(bounds);
		}
					/**------------------------------------------------------
	 				 * Sets the minimum size of the outer bounds of this 
					 * MiPart. This size, when not null, will override the
					 * minimum size of this MiPart which is usually 
					 * determined by calling the (usually overridden)
					 * calcMinimumSize() method.
	 				 * @param size 		the minimum size or null
					 *------------------------------------------------------*/
	public		void		setMinimumSize(MiSize size)
		{
		if (size != null)
			{
			if (minimumSize == null)
				minimumSize = MiSize.newSize();
			minimumSize.copy(size);
			setMiFlag(Mi_MINIMUM_SIZE_OVERRIDDEN_MASK);
			}
		else
			{
			if (hasMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK))
				{
				throw new RuntimeException(
				"MICA: Setting minimum size to null during calculation of preferred or minimum size");
				}
			if (minimumSize != null)
				MiSize.freeSize(minimumSize);
			minimumSize = null;
			resetMiFlag(Mi_MINIMUM_SIZE_OVERRIDDEN_MASK);
			}
		invalidateLayout();
		}
					/**------------------------------------------------------
	 				 * Gets whether the minimum size has been specified (and
					 * therefore overridden) by using setMinimumSize().
	 				 * @return 		true if overridden
					 *------------------------------------------------------*/
	public		boolean		hasOverriddenMinimumSize()
		{
		return(hasMiFlag(Mi_MINIMUM_SIZE_OVERRIDDEN_MASK));
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this MiPart. This method
					 * is usually called by various layouts. If a minimum
					 * size has not been specified (by using setMinimumSize())
					 * then this method calls calcMinimumSize() to get the
					 * size.
	 				 * @param size 		the (returned) minimum size
	 				 * @return 		the minimum size
					 *------------------------------------------------------*/
	public		MiSize		getMinimumSize(MiSize size)
		{
		if (minimumSize == null) 
			//|| ((!hasMiFlag(Mi_MINIMUM_SIZE_OVERRIDDEN_MASK)) 
				//&& (!hasValidLayout()) && (!hasMiFlag(Mi_VALIDATING_LAYOUT_MASK))))
			{
			if (minimumSize == null)
				minimumSize = MiSize.newSize();
			setMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK);
			calcMinimumSize(minimumSize);
			resetMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK);
			}
		size.copy(minimumSize);
		return(size);
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
					 * The default behavior is to return the minimum width
					 * and/or height, or, if not specified, a zero size.
	 				 * @param size		the (returned) minimum size
					 *------------------------------------------------------*/
	protected 	void		calcMinimumSize(MiSize size)
		{
		size.zeroOut();
		MiDistance s = getMinimumWidth();
		if (s > 0)
			size.setWidth(s);
		s = getMinimumHeight();
		if (s > 0)
			size.setHeight(s);
		}

	//***************************************************************************************
	// Damaged/Invalid area redraw management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets whether this MiPart or one of it's parts needs to
					 * be at least partially redrawn.
	 				 * @return 		true if something within this
					 *			MiPart's draw bounds needs to
					 *			be redrawn
					 *------------------------------------------------------*/
	public		boolean		getThisOrPartHasInvalidArea()
		{
		return(hasMiFlag(Mi_THIS_OR_PART_HAS_INVALID_AREA_MASK));
		}
					/**------------------------------------------------------
	 				 * Sets whether this MiPart should be treated specially
					 * as a solid, completely opaque, rectangle. If so then
					 * a MiDrawManager is created and becomes responsible
					 * for the drawing of this MiPart and nothing is allowed
					 * to be drawn behind this MiPart. This is used for
					 * sub-windows, menus, editors, and tables.
	 				 * @param flag 		true if this is to be treated
					 *			specially as a opaque rectangle
					 *------------------------------------------------------*/
	public		void		setIsOpaqueRectangle(boolean flag)
		{
		setMiFlag(Mi_IS_OPAQUE_RECTANGLE_MASK, flag);
		if (flag)
			drawManager = new MiDrawManager(this);
		else
			drawManager = null;

		setContainsOpaqueRectangles(flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether this MiPart is being treated specially
					 * as a solid, completely opaque, rectangle.
	 				 * @return 		true if a special solid rectangle
					 *------------------------------------------------------*/
	public		boolean		isOpaqueRectangle()
		{
		return(hasMiFlag(Mi_IS_OPAQUE_RECTANGLE_MASK));
		}
					/**------------------------------------------------------
	 				 * Sets whether this MiPart contains parts that are set
					 * to be opaque rectangles. This is used by the 
					 * MiDrawManager.
	 				 * @param flag 		true if this has parts that are
					 *			special solid rectangles
					 *------------------------------------------------------*/
	protected	void		setContainsOpaqueRectangles(boolean flag)
		{
		setMiFlag(Mi_CONTAINS_OPAQUE_RECTANGLES_MASK, flag);
		for (int i = 0; i < getNumberOfContainers(); ++i)
			{
			if (flag)
				{
				getContainer(i).setContainsOpaqueRectangles(true);
				}
			else
				{
				getContainer(i).updateContainsOpaqueRectangles();
				getContainer(i).setContainsOpaqueRectangles(getContainer(i).containsOpaqueRectangles());
				}
			}
		}
					/**------------------------------------------------------
	 				 * Updates whether this MiPart contains parts that are set
					 * to be opaque rectangles. This is used by the 
					 * MiDrawManager.
					 *------------------------------------------------------*/
	protected	void		updateContainsOpaqueRectangles()
		{
		if (hasMiFlag(Mi_IS_OPAQUE_RECTANGLE_MASK))
			{
			setMiFlag(Mi_CONTAINS_OPAQUE_RECTANGLES_MASK);
			return;
			}
		resetMiFlag(Mi_CONTAINS_OPAQUE_RECTANGLES_MASK);
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			getPart(i).updateContainsOpaqueRectangles();
			if (getPart(i).containsOpaqueRectangles())
				{
				setMiFlag(Mi_CONTAINS_OPAQUE_RECTANGLES_MASK);
				return;
				}
			}
		}
					/**------------------------------------------------------
	 				 * Gets whether this MiPart contains parts that are set
					 * to be opaque rectangles. This is used by the 
					 * MiDrawManager.
	 				 * @return  		true if this has parts that are
					 *			special solid rectangles
					 *------------------------------------------------------*/
	public		boolean		containsOpaqueRectangles()
		{
		return(hasMiFlag(Mi_CONTAINS_OPAQUE_RECTANGLES_MASK));
		}
					/**------------------------------------------------------
					 * This specifies whether or not all subWindows of the 
					 * window containing this part are also redrawn when this 
					 * part is damaged. This is useful for those rare 
					 * circumstances when a part overlaps multiple 
					 * windows/editors.
	 				 * @param flag  	true if damaged areas are to
					 *			invalidate subwindows
					 *------------------------------------------------------*/
	public		void		setDeeplyInvalidateAreas(boolean flag)
		{
		setMiFlag(Mi_DEEPLY_INVALIDATE_AREAS_MASK, flag);
		}
					/**------------------------------------------------------
	 				 * Gets whether invalid areas of this MiPart invalidates 
					 * areas in subwindows.
	 				 * @return 	  	true if damaged areas are to
					 *			invalidate subwindows
					 *------------------------------------------------------*/
	public		boolean		getDeeplyInvalidateAreas()
		{
		return(hasMiFlag(Mi_DEEPLY_INVALIDATE_AREAS_MASK));
		}
					/**------------------------------------------------------
	 				 * Invalidates the entire area (drawBounds) of this MiPart.
					 * This then in turn invalidates the corresponding area
					 * of all of this MiPart's containers, ending at the
					 * container (usually an MiEditor) that is an opaque
					 * rectangle.
					 * @exception		IllegalArgumentException if the
					 *			area is too big
					 * @see			#setInvalidAreaNotificationsEnabled
					 * @action		Mi_APPEARANCE_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		invalidateArea()
		{
		if (invalidArea != Mi_TOTALLY_INVALID_AREA)
			{
			if (drawBounds.isReversed())
				return;

			if (drawManager != null)
				{
				if (drawManager.isTotallyValidArea())
					appearanceChanged();

				drawManager.invalidateTotalArea(drawBounds);
				}
			else
				{
				invalidateArea(drawBounds); 
				}
			invalidArea = Mi_TOTALLY_INVALID_AREA;
			}
		}
					/**------------------------------------------------------
	 				 * Invalidates the given area of this MiPart if invalid
					 * area notifications are enabled. The area
					 * must intersect the drawBounds of this MiPart.
					 * This then in turn invalidates the corresponding area
					 * of all of this MiPart's containers, ending at the
					 * container (usually an MiEditor) that is an opaque
					 * rectangle.
					 * @param area		the area to invalidate
					 * @exception		IllegalArgumentException if the
					 *			area is too big
					 * @see			#setInvalidAreaNotificationsEnabled
					 * @action		Mi_APPEARANCE_CHANGE_ACTION
					 *------------------------------------------------------*/
	public		void		invalidateArea(MiBounds area)
		{
		if (area.isReversed())
			return;

		if (!visible)
			return;

		if (area.getWidth() >= Mi_MAX_DISTANCE_VALUE/100)
			throw new IllegalArgumentException(this + ": area width too big: " + area);
		if (area.getHeight() >= Mi_MAX_DISTANCE_VALUE/100)
			throw new IllegalArgumentException(this + ": area height too big: " + area);
		if (area.xmin < -Mi_MAX_COORD_VALUE/100)
			throw new IllegalArgumentException(this + ": area.xmin too big");
		if (area.ymin < -Mi_MAX_COORD_VALUE/100)
			throw new IllegalArgumentException(this + ": area.ymin too big");
		if (area.xmax > Mi_MAX_COORD_VALUE/100)
			throw new IllegalArgumentException(this + ": area.xmax too big");
		if (area.ymax > Mi_MAX_COORD_VALUE/100)
			throw new IllegalArgumentException(this + ": area.ymax too big");

		if (MiDebug.debug && (MiDebug.isTracing(this, MiDebug.TRACE_AREA_INVALIDATION)))
			{
			MiDebug.println("This: " + this + "\nInvalidating area = " + area);
			}

		if ((stateMask & Mi_INVALID_AREA_NOTIFICATIONS_ENABLED_MASK) != 0)
			{
			// Changed from intersects() to intersectsIncludingEdges() to handle lines of
			// 0 thickness that change in length only (intersects() will cause them not
			// to invalidate any area). 11-3-2001
			if ((!area.intersectsIncludingEdges(drawBounds)) && (!drawBounds.isReversed()))
				{
				if (MiDebug.debug 
					&& (MiDebug.isTracing(this, MiDebug.TRACE_AREA_INVALIDATION)))
					{
					MiDebug.println("Invalid area does NOT intersect drawBounds: " + drawBounds);
					}
				return;
				}

			MiBounds tmpBounds = MiBounds.newBounds();
			tmpBounds.copy(area);

			if (drawManager != null)
				{
				if (drawManager.isTotallyValidArea())
					appearanceChanged();

				drawManager.invalidateArea(tmpBounds);
				}
			else
				{
				MiPart root = getContainingEditor();
				if (root == null)
					{
					MiBounds.freeBounds(tmpBounds);
					return;
					}
				if ((root.getDrawManager() != null)
					&& (root.getDrawManager().getDrawingPartsOverlappingSubWindows()))
					{
					root.getDrawManager().invalidateFrontToBack(this, tmpBounds);
					}

				for (int i = 0; i < getNumberOfContainers(); ++i)
					{
					MiPart container = getContainer(i);
					if (container.getTransform() != null)
						{
						container.getTransform().wtod(tmpBounds, tmpBounds);
						}

					container.invalidateArea(tmpBounds);

					tmpBounds.copy(area);
					}

				if (invalidArea == Mi_NONE)
					{
					appearanceChanged();
					}
				}
			if (invalidArea == Mi_NONE)
				{
				invalidArea = Mi_PARTIALLY_INVALID_AREA;
				}
			MiBounds.freeBounds(tmpBounds);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the draw manager of this MiPart. This MiPart only
					 * has a draw manager when it is an opaque rectangle.
	 				 * @return 	  	the draw manager or null
					 * @see			#setIsOpaqueRectangle
					 * @see			#isOpaqueRectangle
					 *------------------------------------------------------*/
	public		MiDrawManager 	getDrawManager()
		{
		return(drawManager);
		}
	protected	void 		setDrawManager(MiDrawManager drawManager)
		{
		this.drawManager = drawManager;
		}
					/**------------------------------------------------------
	 				 * Sets the flag indicating that this MiPart or one of
					 * it's parts has an invalid area.
	 				 * @param flag 	  	true if there are invalid areas
					 *------------------------------------------------------*/
	protected	void		setThisOrPartHasInvalidArea(boolean flag)
		{
		setMiFlag(Mi_THIS_OR_PART_HAS_INVALID_AREA_MASK, flag);
		}

	//***************************************************************************************
	// Layout management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Sets this MiPart's (and all of it's parts) flag 
					 * indicating that this MiPart is having it's layout, if
					 * any, validated. This is used internally to clear
					 * the 'cache' of any previously preferred and minimum 
					 * sizes and other housekeeping chores.
	 				 * @param flag 	  	true if validating any layout
					 *------------------------------------------------------*/
	protected	void		setValidatingLayout(boolean flag)
		{
		setMiFlag(Mi_VALIDATING_LAYOUT_MASK, flag);
		if (extensions != null)
			extensions.doubleBuffer = null;

		if (!hasValidLayout())
			{
			if (!hasMiFlag(Mi_PREFERRED_SIZE_OVERRIDDEN_MASK))
				{
				if (preferredSize != null)
					MiSize.freeSize(preferredSize);
				preferredSize = null;
				}
			if (!hasMiFlag(Mi_MINIMUM_SIZE_OVERRIDDEN_MASK))
				{
				if (minimumSize != null)
					MiSize.freeSize(minimumSize);
				minimumSize = null;
				}
			}

		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			getPart(i).setValidatingLayout(flag);
			}
		}
					/**------------------------------------------------------
	 				 * Gets whether Mica is currently validating this MiPart's
					 * layout.
	 				 * @return 		true if validating layout
					 *------------------------------------------------------*/
	public		boolean		getValidatingLayout()
		{
		return(hasMiFlag(Mi_VALIDATING_LAYOUT_MASK));
		}
					/**------------------------------------------------------
	 				 * Gets whether this MiPart's layout, if any, is valid
					 * and does not need to be recalculated.
	 				 * @return  	  	true if any layout is valid
					 *------------------------------------------------------*/
	public		boolean		hasValidLayout()
		{
		return(hasMiFlag(Mi_VALID_LAYOUT_MASK));
		}
					/**------------------------------------------------------
	 				 * Gets whether this MiPart's bounds is valid
					 * and does not need to be refreshed.
	 				 * @return  	  	true if bounds is valid
					 *------------------------------------------------------*/
	private		boolean		hasValidBounds()
		{
		return(hasMiFlag(Mi_VALID_BOUNDS_MASK));
		}
					/**------------------------------------------------------
	 				 * Validates this MiPart's layout, if any and if it is 
					 * invalid. This causes the layout to recalculate and 
					 * reposition parts. This MiPart's parts and any 
					 * attachments also have their layouts validated at this 
					 * time.
					 *------------------------------------------------------*/
	public		void		validateLayout()
		{
		boolean isValidatingLayout = hasMiFlag(Mi_VALIDATING_LAYOUT_MASK);
		if (!isValidatingLayout)
			setValidatingLayout(true);

//if ("box".equals(getName()))
//MiDebug.printStackTrace("NOW VALIDATING LAYOUT OF BOX" + this);
		if (!hasValidLayout())
			{
			layoutParts();
			}

		if (!hasValidLayout())
			{
			validateParts();

			refreshBounds();

			if (attachments != null)
				{
				attachments.validateLayout();
				}
			if (MiDebug.debug)
				{
				MiDebug.checkForLingeringInvalidLayoutsBelow(this);
				}
			}
		if (!isValidatingLayout)
			{
			setValidatingLayout(isValidatingLayout);
			}
		setMiFlag(Mi_VALID_LAYOUT_MASK);
		}
					/**------------------------------------------------------
	 				 * Validates this MiPart's part's layouts if they are 
					 * invalid.
					 *------------------------------------------------------*/
	protected	void		validateParts()
		{
		// layout and refresh bounds of children 
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			if ((!getPart(i).hasValidLayout()) && (getPart(i).isVisible()))
				getPart(i).validateLayout();
			}
		}
					/**------------------------------------------------------
					 * Call any layout to validate itself.
					 *------------------------------------------------------*/
	protected	void		layoutParts()
		{
		if (getLayout() != null)
			getLayout().layoutParts();
		}
		
					/**------------------------------------------------------
	 				 * Convenience method to set this MiPart to it's preferred
					 * size and then validate it's layout. This is rarely 
					 * needed as the default layout of MiEditors, the
					 * MiSizeOnlyLayout, does this automatically when needed
					 * for all parts in the MiEditor.
					 *------------------------------------------------------*/
	public		void		packLayout()
		{
		MiSize size = MiSize.newSize();
		getPreferredSize(size);
		bounds.setWidth(size.getWidth());
		bounds.setHeight(size.getHeight());
		geometryChanged();
		validateLayout();
		MiSize.freeSize(size);
		}
					/**------------------------------------------------------
	 				 * Specifies that this MiPart has an invalid layout if
					 * incoming invalid layout notifications are enabled.
					 * This MiPart may or may not have a layout. Any 
					 * attachments also have their layouts invalidated at this
					 * time. Any containers have their layouts invalidated 
					 * at this time if outgoing invalid layout notifications 
					 * are enabled.
					 * @see		#setInvalidLayoutNotificationsEnabled
					 * @see		#setIncomingInvalidLayoutNotificationsEnabled
					 * @see		#setOutgoingInvalidLayoutNotificationsEnabled
					 *------------------------------------------------------*/
	public		void		invalidateLayout()
		{

		if (extensions != null)
			extensions.doubleBuffer = null;

		resetPreferredSizes();

		resetMiFlag(Mi_VALID_BOUNDS_MASK);
		
		if (!hasMiFlag(Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK))
			return;

//if ("box".equals(getName()))
//MiDebug.printStackTrace("" + this);
//if ("box".equals(getName()))
//MiDebug.println("IS VALIDATING=" + hasMiFlag(Mi_VALIDATING_LAYOUT_MASK));


		resetMiFlag(Mi_VALID_LAYOUT_MASK);

		if (getLayout() != null)
			{
			getLayout().invalidateLayout();
			}
		if (attachments != null)
			{
			attachments.invalidateLayouts();
			}

		invalidateContainersLayout();
		}
	// Make sure containers resize to zero when their contents become invisible...
	// We want this to work for, say, palettes that become invisible (their container size should reflect this)
	// but not, say, checkboxes, whose container size is fixed.
	protected	void		zeroSizeOfSingleChildContainersOfInvisiblePart()
		{
		// Attachments should not affect layout of associated master object.
		if (this instanceof MiAttachments)
			return;

		if (containers != null)
			{
			if ((containers.length == 1) && (containers[0].getNumberOfParts() == 1))
				{
				if ((!(containers[0] instanceof MiVisibleContainer)) 
					|| (((MiVisibleContainer )containers[0]).getContainerLayoutSpec() 
						!= MiVisibleContainer.MAKE_CONTAINER_SAME_SIZE_OR_BIGGER_THAN_CONTENTS))
					{
					containers[0].replaceBounds(new MiBounds());
					containers[0].zeroSizeOfSingleChildContainersOfInvisiblePart();
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Any containers have their layouts invalidated if
					 * outgoing invalid layout notifications are enabled.
					 * @see		#setInvalidLayoutNotificationsEnabled
					 * @see		#setOutgoingInvalidLayoutNotificationsEnabled
					 *------------------------------------------------------*/
	protected	void		invalidateContainersLayout()
		{
		if (!hasMiFlag(Mi_OUT_GOING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK))
			return;

		// Attachments should not affect layout of associated master object.
		if (this instanceof MiAttachments)
			return;

		if (containers != null)
			{
			for (int i = 0; i < containers.length; ++i)
				{
				if (!containers[i].hasMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK))
					{
					if ((containers[i].hasValidLayout()) 
						|| (containers[i] instanceof MiAttachments))
						{
						containers[i].invalidateLayout();
						}
					else 
						{
						containers[i].resetThisAndContainerPreferredSizes();
						}
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Sets pre-calculated preferred and minimum sizes to null
					 * for all containers to force a recalculation when the 
					 * minimum and preferred sizes are next read.
					 *------------------------------------------------------*/
	protected	void		resetThisAndContainerPreferredSizes()
		{
		if (!hasMiFlag(Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK))
			return;

		resetPreferredSizes();

		if ((containers != null)
			&& (hasMiFlag(Mi_OUT_GOING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK))
			&& (!(this instanceof MiAttachments)))
			{
			for (int i = 0; i < containers.length; ++i)
				{
				containers[i].resetThisAndContainerPreferredSizes();
				}
			}
		}
					/**------------------------------------------------------
					 * Sets pre-calculated preferred and minimum sizes to null
					 * to force a recalculation when the minimum and preferred 
					 * sizes hey are next read.
					 *------------------------------------------------------*/
	protected	void		resetPreferredSizes()
		{
		if (hasMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK))
			return;
		if (hasMiFlag(Mi_VALIDATING_LAYOUT_MASK))
			return;

//if (hasValidLayout())
//MiDebug.printStackTrace("RESETTING PREFEERRED SIZES event tho HAS VALID LAYOUT");

		if (!hasMiFlag(Mi_PREFERRED_SIZE_OVERRIDDEN_MASK))
			{
			if (preferredSize != null)
				MiSize.freeSize(preferredSize);
			preferredSize = null;
			}
		if (!hasMiFlag(Mi_MINIMUM_SIZE_OVERRIDDEN_MASK))
			{
			if (minimumSize != null)
				MiSize.freeSize(minimumSize);
			minimumSize = null;
			}
		if (getLayout() != null)
			{
			if (getLayout() instanceof MiPart)
				((MiPart )getLayout()).resetPreferredSizes();
			else
				getLayout().invalidateLayout();
			}
		if (attachments != null)
			{
			attachments.resetPreferredSizes();
			}
		}
					/**------------------------------------------------------
					 * Set the layout of this MiPart.
					 * @param l		the new layout or null
					 *------------------------------------------------------*/
	public		void		setLayout(MiiLayout l) 	
		{ 
		if (extensions == null)
			{
			extensions = new MiPartExtensions(this);
			}

		MiiLayout previousLayout = extensions.layout;
		extensions.layout = null;
		if (previousLayout != null)
			{
			previousLayout.setTarget(null); 	
			}
		extensions.layout = l;
		if (extensions.layout != null)
			{
			extensions.layout.setTarget(this); 	
			}
		invalidateLayout();
		}
					/**------------------------------------------------------
					 * Get the layout of this MiPart.
					 * @return 		the layout or null
					 *------------------------------------------------------*/
	public		MiiLayout	getLayout() 		
		{ 
		return(extensions == null ? null : extensions.layout); 
		}

	//***************************************************************************************
	// Bounds management
	//***************************************************************************************

					/**------------------------------------------------------
					 * Get the draw bounds of this MiPart. The draw bounds is
					 * the outer bounds plus the bounds of any shadows, 
					 * attachments, and margins.
					 * @param b 		the (returned) draw bounds
					 * @return 		the draw bounds
					 *------------------------------------------------------*/
	public		MiBounds	getDrawBounds(MiBounds b)
		{
		b.copy(drawBounds);
		return(b);
		}
					/**------------------------------------------------------
					 * Recalc the draw bounds of this MiPart. The draw bounds
					 * is the outer bounds plus the bounds of any shadows, 
					 * attachments, and margins.
					 * @param b 		the (returned) draw bounds
					 * @return 		the draw bounds
					 *------------------------------------------------------*/
	protected	void		reCalcDrawBounds()
		{
		MiBounds tmpBounds = MiBounds.newBounds();
		reCalcDrawBounds(tmpBounds);

		if (!drawBounds.equals(tmpBounds))
			{
			invalidateArea();
			if (bounds.equals(tmpBounds))
				{
				if (drawBounds != bounds)
					MiBounds.freeBounds(drawBounds);
				drawBounds = bounds;
				}
			else
				{
				if (drawBounds == bounds)
					drawBounds = MiBounds.newBounds();
				drawBounds.copy(tmpBounds);
				}
			invalidateArea(drawBounds);
			refreshContainerDrawBounds();
			}
		MiBounds.freeBounds(tmpBounds);
		}

	protected	void		refreshContainerDrawBounds()
		{
		if (!hasValidLayout())
			return;

		if (containers != null)
			{
			for (int i = 0; i < containers.length; ++i)
				{
				MiPart container = containers[i];
				if ((!(container instanceof MiEditor)) && (container.hasValidLayout()))
					{

/*
					// 7-9-2002 Optimize focus border - but sometimes we want the draw bounds
					// to be smaller, as it is now really smaller, and pick needs this
					// to be accurate, i.e. in treelist right mouse button context menu pick.
					MiBounds xfrmBounds = MiBounds.newBounds();
					xfrmBounds.copy(drawBounds);
					if (container.getTransform() != null)
						{
						container.getTransform().wtod(xfrmBounds, xfrmBounds);
						}
					if ((extensions != null) && (extensions.margins != null))
						{
						// Ignore if visibleContainer just displayed mouse 
						// focus border.... (i.e. in scrollBar)
						xfrmBounds.subtractMargins(extensions.margins);
						}
					if (!xfrmBounds.isContainedIn(container.drawBounds))
						{
						containers[i].reCalcDrawBounds();
						}
					MiBounds.freeBounds(xfrmBounds);
*/


					containers[i].reCalcDrawBounds();
					}
				}
			}
		}
					// Does not invaldiate area. Usually called when
					// bounds has changed which means area and layout
					// have already been invalidated
	private		void		fastReCalcDrawBounds()
		{
		MiBounds tmpBounds = MiBounds.newBounds();
		reCalcDrawBounds(tmpBounds);
		if (!drawBounds.equals(tmpBounds))
			{
			if (bounds.equals(tmpBounds))
				{
				if (drawBounds != bounds)
					MiBounds.freeBounds(drawBounds);
				drawBounds = bounds;
				}
			else
				{
				if (drawBounds == bounds)
					drawBounds = MiBounds.newBounds();
				drawBounds.copy(tmpBounds);
				}
			}
		MiBounds.freeBounds(tmpBounds);
		}
					/**------------------------------------------------------
					 * Gets the outer bounds of this MiPart to be used in
					 * calculating the draw bounds. This is required for
					 * MiReferences and others who calc the draw bounds based
					 * on the transform of the outer bounds. To be overridden
					 * if necessary.
					 * @param b 		the (returned) outer bounds
					 *------------------------------------------------------*/
	protected	void		getReferencedOuterBounds(MiBounds b)
		{
		b.copy(bounds);
		}
					/**------------------------------------------------------
					 * Implemenation of the method that recalculates the draw 
					 * bounds of this MiPart. 
					 * This routine starts with the basic outer bounds (as
					 * returned by the getReferencedOuterBounds() method) and
					 * adds any bounds that the renderers, attachments and
					 * margins to it.
					 * @param db 		the (returned) draw bounds
					 *------------------------------------------------------*/
	protected	void		reCalcDrawBounds(MiBounds db)
		{
		getReferencedOuterBounds(db);


		MiBounds tmp = null;
		if ((attachments != null) || hasMiFlag(Mi_HAS_CUSTOM_RENDERERS_MASK))
			{
			tmp = MiBounds.newBounds();
			if ((attachments != null) && (!(this instanceof MiEditor)))
				{
				attachments.refreshBounds();
				db.union(attachments.getDrawBounds(tmp));
				}
			if (hasMiFlag(Mi_HAS_CUSTOM_RENDERERS_MASK))
				{
				tmp.copy(bounds);
				if (getHasShadow() && 
				(attributes.objectAttributes[Mi_SHADOW_RENDERER] != null)
				&& (((MiiPartRenderer )attributes.objectAttributes[Mi_SHADOW_RENDERER]).getBounds(this, tmp)))
					{
					db.union(tmp);
					tmp.copy(bounds);
					}
				if ((attributes.objectAttributes[Mi_LINE_ENDS_RENDERER] != null)
				&& (((MiiPartRenderer )attributes.objectAttributes[Mi_LINE_ENDS_RENDERER]).getBounds(this, tmp)))
					{
					db.union(tmp);
					tmp.copy(bounds);
					}
				if ((attributes.objectAttributes[Mi_BEFORE_RENDERER] != null)
				&& (((MiiPartRenderer)attributes.objectAttributes[Mi_BEFORE_RENDERER]).getBounds(this, tmp)))
					{
					db.union(tmp);
					tmp.copy(bounds);
					}
				if (managedPointManagers != null)
					{
					for (int i = 0; i < managedPointManagers.length; ++i)
						{
						if ((managedPointManagers[i] != null)
							&& (managedPointManagers[i].getBounds(this, tmp)))
							{
							db.union(tmp);
							tmp.copy(bounds);
							}
						}
					}
				if ((attributes.objectAttributes[Mi_AFTER_RENDERER] != null)
				&& ((MiiPartRenderer)attributes.objectAttributes[Mi_AFTER_RENDERER]).getBounds(this, tmp))
					{
					db.union(tmp);
					}
				}
			}
		// JDK 1.1.5 static initialization has this null for awhile (attributes = defaultAttributes
		// creates shadowRenderer creates MiRectangle calls this method).
		if ((attributes != null)
			&& (attributes.objectAttributes[Mi_BORDER_RENDERER] != null))
			{
			if (tmp == null)
				tmp = MiBounds.newBounds();
			MiBounds tmp2 = MiBounds.newBounds();
			tmp2.copy(bounds);
			if (((MiiDeviceRenderer)attributes.objectAttributes[Mi_BORDER_RENDERER])
				.getBounds(attributes, tmp2, tmp))
				{
				db.union(tmp);
				}
			MiBounds.freeBounds(tmp2);
			}
		if ((extensions != null) && (extensions.margins != null))
			db.addMargins(extensions.margins);
		if (tmp != null)
			MiBounds.freeBounds(tmp);
		}
					/**------------------------------------------------------
					 * Realculates the outer bounds of this MiPart. Override 
					 * this, if desired, as it implements the core 
					 * functionality. The default implementation just returns
					 * the outer bounds.
					 * @param b 		the (returned) outer bounds
					 *------------------------------------------------------*/
	protected	void		reCalcBounds(MiBounds b)
		{
		b.copy(bounds);
		}
					/**------------------------------------------------------
					 * Updates the outer bounds  and draw bounds of this 
					 * MiPart, invalidating areas and layouts as necessary.
					 *------------------------------------------------------*/
	protected	void		refreshBounds()
		{
		MiBounds newBounds = MiBounds.newBounds();
		reCalcBounds(newBounds);
		if (!bounds.equals(newBounds))
			{
			invalidateArea();
			bounds.copy(newBounds);
			fastReCalcDrawBounds();
			if (bounds.isReversed())
				{
				invalidateContainersLayout();
				}
			else
				{
				invalidateArea(drawBounds);
				if (!hasMiFlag(Mi_CALCING_PREF_OR_MIN_SIZES_MASK))
					invalidateLayout();
				}
			geometryChanged();
			}
		else
			{
			reCalcDrawBounds();
			}
		MiBounds.freeBounds(newBounds);
		setMiFlag(Mi_VALID_BOUNDS_MASK);
		}

	//***************************************************************************************
	// Connection management
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the number of connections assigned to this MiPart.
					 * @return		the number of connections
					 *------------------------------------------------------*/
	public		int		getNumberOfConnections()
		{
		return(((extensions == null) || (extensions.connections == null))
			? 0 : extensions.connections.size());
		}
					/**------------------------------------------------------
					 * Gets the connection assigned to this MiPart at the 
					 * given index.
					 * @param index		the index of the connection
					 * @return		the connection at the given index
					 *------------------------------------------------------*/
	public		MiConnection	getConnection(int index)
		{	
		return((MiConnection )extensions.connections.elementAt(index));
		}
					/**------------------------------------------------------
					 * Appends the given connection to this MiPart.
					 * @param conn		the connection to append
					 *------------------------------------------------------*/
	public		void		appendConnection(MiConnection conn)
		{	
		if (extensions == null)
			extensions = new MiPartExtensions(this);
		if (extensions.connections == null)
			extensions.connections = new FastVector();
		extensions.connections.addElement(conn);
		// If this part is in a container with a graph layout
		invalidateContainersLayout();

		dispatchAction(Mi_CONNECT_ACTION, conn);
		}
					/**------------------------------------------------------
					 * Inserts the given connection in this MiPart's list of
					 * connections at the given index.
					 * @param conn		the connection to insert
					 * @param index		the index of the insertion point
					 *------------------------------------------------------*/
	public		void		insertConnection(MiConnection conn, int index)
		{	
		if (extensions == null)
			extensions = new MiPartExtensions(this);
		if (extensions.connections == null)
			extensions.connections = new FastVector();
		extensions.connections.insertElementAt(conn, index);
		// If this part is in a container with a graph layout
		invalidateContainersLayout();

		dispatchAction(Mi_CONNECT_ACTION, conn);
		}
					/**------------------------------------------------------
					 * Removes the connection from this MiPart's list of
					 * connections at the given index.
					 * @param index		the index of the connection to
					 *			remove
					 *------------------------------------------------------*/
	public		void		removeConnection(int index)
		{	
		getConnection(index).removeConnectionTo(this);
		// If this part is in a container with a graph layout
		invalidateContainersLayout();
		}
					/**------------------------------------------------------
					 * Removes the given connection from this MiPart's list of
					 * connections.
					 * @param conn		the connection to remove
					 *------------------------------------------------------*/
	public		void		removeConnection(MiConnection conn)
		{	
		conn.removeConnectionTo(this);
		}
					/**------------------------------------------------------
					 * Removes all connections from this MiPart.
					 *------------------------------------------------------*/
	public		void		removeAllConnections()
		{
		while (getNumberOfConnections() > 0)
			removeConnection(0);
		}
					/**------------------------------------------------------
					 * Actually removes the given connection from this MiPart's
					 * list of connections. This is called from the MiConnection
					 * whenever it is removed from a MiPart. 
					 * @param conn		the connection to remove
					 *------------------------------------------------------*/
	protected	void		removeTheConnection(MiConnection conn)
		{	
		extensions.connections.removeElement(conn);
		// If this part is in a container with a graph layout
		invalidateContainersLayout();

		dispatchAction(Mi_DISCONNECT_ACTION, conn);
		}

					/**------------------------------------------------------
					 * Gets whether this is connected to the other given part.
					 * NOTE: Does not check whether some part of this is 
					 * connected to some part of other given part.
					 * @param other		the part to test for connectivity
					 * @return 		true if a connection between this
					 *			MiPart and the given other MiPart
					 *------------------------------------------------------*/
	public		boolean		isConnectedTo(MiPart other)
		{
		for (int i = 0; i < getNumberOfConnections(); ++i)
			{
			MiConnection conn = getConnection(i);
			if (conn.getDestination() == other)
				return(true);
			if (conn.getSource() == other)
				return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
					 * Gets whether the proposed connection operation contains
					 * a valid source for connection to this MiPart.
					 * Override this, if desired, as it implements the core 
					 * functionality, which by default returns true.
					 * @param connectOp	the connection operation
					 * @return 		true if valid
					 *------------------------------------------------------*/
	public		boolean		isValidConnectionSource(MiConnectionOperation connectOp)
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Gets whether the proposed connection operation contains
					 * a valid destination for connection to this MiPart.
					 * Override this, if desired, as it implements the core 
					 * functionality, which by default returns true.
					 * @param connectOp	the connection operation
					 * @return 		true if valid
					 *------------------------------------------------------*/
	public		boolean		isValidConnectionDestination(MiConnectionOperation connectOp)
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Gets whether there is an intersection between the
					 * line formed by the given points and the outer bounds
					 * of this MiPart. If, there is, also returns the point
					 * of intersection. This method is most usefull when 
					 * insidePoint is inside these bounds.
					 * @param insidePoint	the point inside this MiPart's
					 *			outer bounds
					 * @param otherPoint	the point outside this MiPart's
					 *			outer bounds
					 * @param returnedIntersectionPoint
					 *			the (returned) point of 
					 *			intersection of the line between
					 *			the two given points and the
					 *			outer bounds of this MiPart
					 * @return 		true if there was an intersection
					 *------------------------------------------------------*/
	public		boolean		getIntersectionWithLine(
						MiPoint insidePoint, 
						MiPoint otherPoint, 
						MiPoint returnedIntersectionPoint)
		{
		return(bounds.getIntersectionWithLine(
			insidePoint, otherPoint, returnedIntersectionPoint));
		}

	//***************************************************************************************
	// Default Connection Point management
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets location into given point and positions given bounds
					 * as specified by the given location. This method is useful
					 * for finding points relative to any shape and for positioning
					 * objects relative to another (for example text relative to
					 * the top edge of a line).
					 *
					 * @param location	The location can be one of 
					 *			the locations below, the
					 * 			number of a point (i.e. a 
					 *			value of 2 would attach the given
					 * 			part to the 3rd point of a 
					 *			polyline), or Mi_LAST_POINT_NUMBER.
					 *
					 *   Mi_CENTER_LOCATION
        				 *   Mi_LEFT_LOCATION
        				 *   Mi_RIGHT_LOCATION
        				 *   Mi_BOTTOM_LOCATION
        				 *   Mi_TOP_LOCATION
        				 *   Mi_LOWER_LEFT_LOCATION
        				 *   Mi_LOWER_RIGHT_LOCATION
        				 *   Mi_UPPER_LEFT_LOCATION
        				 *   Mi_UPPER_RIGHT_LOCATION
					 *   Mi_OUTSIDE_LEFT_LOCATION
					 *   Mi_OUTSIDE_RIGHT_LOCATION
					 *   Mi_OUTSIDE_BOTTOM_LOCATION
					 *   Mi_OUTSIDE_TOP_LOCATION
					 *   Mi_END_LOCATION 
					 *   Mi_WNW_LOCATION
					 *   Mi_WSW_LOCATION
					 *   Mi_ENE_LOCATION
					 *   Mi_ESE_LOCATION
					 *   Mi_NWN_LOCATION
					 *   Mi_NEN_LOCATION
					 *   Mi_SWS_LOCATION
					 *   Mi_START_LOCATION
					 *   Mi_END_LOCATION 
					 *   Mi_LINE_CENTER_LOCATION
					 *   Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION
					 *   Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION
					 *   Mi_LINE_START_LOCATION
					 *   Mi_LINE_START_TOP_OR_RIGHT_LOCATION
					 *   Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION
					 *   Mi_LINE_END_LOCATION
					 *   Mi_LINE_END_TOP_OR_RIGHT_LOCATION
					 *   Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION
					 *   Mi_LINE_AT_START_LOCATION
					 *   Mi_LINE_AT_END_LOCATION
        				 *   Mi_SURROUND_LOCATION
					 *
					 * @param boundsToPosition the bounds to position
					 *			with respect to target at the
					 *			location specified by pointNumber.
					 * @param pt		<Returned> the location specified
					 *			by the pointNumber plus the margins
					 *			(if any).
					 * @margins		The margins to use. Only valid for
					 *			values of location that are not
					 *			equal to index number of a point
					 *			of a shape.
					 * @return 		pt
					 *------------------------------------------------------*/
	public		MiPoint		getRelativeLocation(
						int location, 
						MiBounds boundsToPosition, 
						MiPoint pt, 
						MiMargins margins)
		{
		if (location < getNumberOfPoints())
			{
			getPoint(location, pt);
			return(pt);
			}
		return(MiUtility.getLocationWithRespectToPart(
                                                this,
                                                location,
						boundsToPosition,
                                                pt,
						margins));
		}
					/**------------------------------------------------------
					 * Gets the MiConnectionPointManager, if any, assigned to
					 * this MiPart. 
					 * @return		the connection point manager or null
					 *------------------------------------------------------*/
	public		MiConnectionPointManager	getConnectionPointManager()
		{
		return(managedPointManagers != null 
			? (MiConnectionPointManager )managedPointManagers[0] : null); 
		}
					/**------------------------------------------------------
					 * Sets the MiConnectionPointManager of this MiPart. This
					 * manager allows customization of the location of the
					 * points this MiPart's connections can attach to.
					 * @param m		the connection point manager or
					 *			null
					 *------------------------------------------------------*/
	public		void		setConnectionPointManager(MiConnectionPointManager m)
		{
		invalidateArea();
		if (managedPointManagers == null)
			managedPointManagers = new MiManagedPointManager[4];
		managedPointManagers[0] = m;
		updateHasRenderersFlag();
		refreshBounds();
		invalidateArea();
		}
					/**------------------------------------------------------
					 * Gets the MiAnnotationPointManager, if any, assigned to
					 * this MiPart. 
					 * @return		the annotation point manager or null
					 *------------------------------------------------------*/
	public		MiAnnotationPointManager	getAnnotationPointManager()
		{
		return(managedPointManagers != null 
			? (MiAnnotationPointManager )managedPointManagers[1] : null); 
		}
					/**------------------------------------------------------
					 * Sets the MiAnnotationPointManager of this MiPart. This
					 * manager allows customization of the location of the
					 * points this MiPart's text can attach to.
					 * @param m		the annotation point manager or null
					 *------------------------------------------------------*/
	public		void		setAnnotationPointManager(MiAnnotationPointManager m)
		{
		invalidateArea();
		if (managedPointManagers == null)
			managedPointManagers = new MiManagedPointManager[4];
		managedPointManagers[1] = m;
		updateHasRenderersFlag();
		refreshBounds();
		invalidateArea();
		}
					/**------------------------------------------------------
					 * Gets the MiControlPointManager, if any, assigned to
					 * this MiPart. 
					 * @return		the control point manager or null
					 *------------------------------------------------------*/
	public		MiControlPointManager	getControlPointManager()
		{
		return(managedPointManagers != null 
			? (MiControlPointManager )managedPointManagers[2] : null); 
		}
					/**------------------------------------------------------
					 * Sets the MiControlPointManager of this MiPart. This
					 * manager allows customization of the location of the
					 * points this MiPart's connections can attach to.
					 * @param m		the control point manager or null
					 *------------------------------------------------------*/
	public		void		setControlPointManager(MiControlPointManager m)
		{
		invalidateArea();
		if (managedPointManagers == null)
			managedPointManagers = new MiManagedPointManager[4];
		managedPointManagers[2] = m;
		updateHasRenderersFlag();
		refreshBounds();
		invalidateArea();
		}
					/**------------------------------------------------------
					 * Gets the MiSnapPointManager, if any, assigned to
					 * this MiPart. 
					 * @return		the snap point manager or null
					 *------------------------------------------------------*/
	public		MiSnapPointManager	getSnapPointManager()
		{
		return(managedPointManagers != null 
			? (MiSnapPointManager )managedPointManagers[3] : null); 
		}
					/**------------------------------------------------------
					 * Sets the MiSnapPointManager of this MiPart. This
					 * manager allows customization of the location of the
					 * points this MiPart uses to snap to grids, etc.
					 * @param m		the snap point manager or null
					 *------------------------------------------------------*/
	public		void		setSnapPointManager(MiSnapPointManager m)
		{
		invalidateArea();
		if (managedPointManagers == null)
			managedPointManagers = new MiManagedPointManager[4];
		managedPointManagers[3] = m;
		updateHasRenderersFlag();
		refreshBounds();
		invalidateArea();
		}

	//***************************************************************************************
	// EventHandler Management
	//***************************************************************************************

					/**------------------------------------------------------
					 * Appends the given event handler to the list of event
					 * handlers assigned to this MiPart.
					 * @param handler	the event handler to append
					 *------------------------------------------------------*/
	public		void		appendEventHandler(MiiEventHandler handler)
		{
		if (eventHandlers == null)
			eventHandlers = new FastVector();
		eventHandlers.addElement(handler);
		handler.setTarget(this);
		invalidateCachedEventHandlerInfo();
		handleEventHandlerRegistration(handler, true);
		}
					/**------------------------------------------------------
					 * Inserts the given event handler into the list of event
					 * handlers assigned to this MiPart at the given index.
					 * @param handler	the event handler to insert
					 * @param index		the index of the insertion point
					 *------------------------------------------------------*/
	public		void		insertEventHandler(MiiEventHandler handler, int index)
		{
		if (eventHandlers == null)
			eventHandlers = new FastVector();
		eventHandlers.insertElementAt(handler, index);
		handler.setTarget(this);
		invalidateCachedEventHandlerInfo();
		handleEventHandlerRegistration(handler, true);
		}
					/**------------------------------------------------------
					 * Removes the given event handler from the list of event
					 * handlers assigned to this MiPart.
					 * @param handler	the event handler to remove
					 *------------------------------------------------------*/
	public		void		removeEventHandler(MiiEventHandler handler)
		{
		eventHandlers.removeElement(handler);
		if (eventHandlers.size() == 0)
			invalidateCachedEventHandlerInfo();
		handler.setTarget(null);
		handleEventHandlerRegistration(handler, false);
		}
					/**------------------------------------------------------
					 * Removes all event handlers assigned to this MiPart.
					 *------------------------------------------------------*/
	public		void		removeAllEventHandlers()
		{
		if (eventHandlers != null)
			{
			while (eventHandlers.size() > 0)
				removeEventHandler((MiiEventHandler )eventHandlers.elementAt(0));
			}
		}
					/**------------------------------------------------------
					 * Sets whether event handling by this MiPart is enabled.
					 * If event handling is disabled then events sent to this
					 * MiPart will not be dispatched to any of this MiPart's
					 * event handlers or to any of this MiPart's part's event
					 * handlers.
					 * If event handling is then later re-enabled, then all 
					 * parts of this MiPart will also be enabled if they
					 * were previously disabled because of a disabled
					 * container.
					 * @param flag		true if event handling enabled
					 *------------------------------------------------------*/
	public		void		setEventHandlingEnabled(boolean flag)
		{
		setMiFlag(Mi_EVENT_HANDLING_ENABLED_MASK, flag);
		if (!hasMiFlag(Mi_EVENT_HANDLING_DISABLED_BY_CONTAINER_MASK))
			{
			for (int i = 0; i < getNumberOfParts(); ++i)
				getPart(i).setEventHandlingDisabledByContainer(!flag);
			}
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart has event handling enabled
					 * or explicitely disabled.
					 * @return 		true if event handling was not
					 *			explicitely disabled.
					 * @see 		#getEventHandlingDisabledByContainer
					 *------------------------------------------------------*/
	public		boolean		getEventHandlingEnabled()
		{
		return(hasMiFlag(Mi_EVENT_HANDLING_ENABLED_MASK));
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart has event handling disabled
					 * by a container being disabled.
					 * @return 		true if event handling was disabled
					 *			by a container
					 * @see 		#getEventHandlingEnabled
					 *------------------------------------------------------*/
	public		boolean		getEventHandlingDisabledByContainer()
		{
		return(hasMiFlag(Mi_EVENT_HANDLING_DISABLED_BY_CONTAINER_MASK));
		}
					/**------------------------------------------------------
					 * Specify whether this MiPart is to have it's event handling
					 * handling enabled even though a container's event handling
					 * has been disabled. This is used to enable, say, one button
					 * in a window that has been otherwise disabled.
					 * @param flag		true if disabled, false to enable
					 *------------------------------------------------------*/
	public		void		overrideEventHandlingDisabledByContainer(boolean flag)
		{
		setMiFlag(Mi_EVENT_HANDLING_DISABLED_BY_CONTAINER_MASK, flag);
		for (int i = 0; i < getNumberOfParts(); ++i)
			getPart(i).setEventHandlingDisabledByContainer(flag);
		}
					/**------------------------------------------------------
					 * Dispatches the given event to all event handlers assigned
					 * to this MiPart if:
					 *   getEventHandlingEnabled() is true
					 * 	and
					 *   getEventHandlingDisabledByContainer() is false
					 * or
					 *   the event is a monitor event
					 * and
					 *   if this MiPart is visible or the event is a MOUSE_EXIT
					 * This is rarely if ever called by your program and requires
					 * the MiEvent to be properly prepared as to the current
					 * MiEditor and the location of the mouse cursor within that
					 * editor.
					 * @param  event	The event
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see this event
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see this event
					 *------------------------------------------------------*/
	public		int		dispatchEvent(MiEvent event)
		{
		if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_DISPATCHING))
			{
			MiDebug.println("Dispatching <"
					+ MiEventHandler.getNameForType(event.handlerTargetType)
					+ "> event: " + event + ", to: " 
					+ this + " with handlers: " + eventHandlers);
			}

		if (((!hasMiFlag(Mi_EVENT_HANDLING_ENABLED_MASK)) 
			|| (hasMiFlag(Mi_EVENT_HANDLING_DISABLED_BY_CONTAINER_MASK)))
			&& (event.handlerTargetType != Mi_MONITOR_EVENT_HANDLER_TYPE))
			{
			// ---------------------------------------------------------------
			// If debug is tracing this, print out that we are dispatching which
			// event to which part.
			// ---------------------------------------------------------------
			if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_EVENT_DISPATCHING))
				{
				if (!hasMiFlag(Mi_EVENT_HANDLING_ENABLED_MASK)) 
					{
					MiDebug.println("NOT Dispatching <"
						+ MiEventHandler.getNameForType(event.handlerTargetType)
						+ "> event: " + event + ", to: " 
						+ this + " because this part has event handling disabled");
					}
				else if (hasMiFlag(Mi_EVENT_HANDLING_DISABLED_BY_CONTAINER_MASK)) 
					{
					MiDebug.println("NOT Dispatching <"
						+ MiEventHandler.getNameForType(event.handlerTargetType)
						+ "> event: " + event + ", to: " 
						+ this + " because this part has event handling disabled by container");
					}
				}
			return(Mi_PROPOGATE_EVENT);
			}

		// See if this object has any eventHandlers interested in this event...
		if ((eventHandlers == null) 
			|| (((!visible) || (isHidden())) && (event.type != MiEvent.Mi_MOUSE_EXIT_EVENT)))
			{
			return(Mi_PROPOGATE_EVENT);
			}

		if (getTransform() != null)
			{
			MiEvent tmpEvent = MiEvent.newEvent();
			tmpEvent.copy(event);
			tmpEvent.doTransform(getTransform());
			event = tmpEvent;
			}

		event.target = this;
		for (int i = 0; i < eventHandlers.size(); ++i)
			{
			MiiEventHandler handler = (MiiEventHandler )eventHandlers.elementAt(i);
			if ((handler.getType() == event.handlerTargetType)
				&& (handler.isEnabled()))
				{
				int status = handler.processEvent(event);
				if (((status == Mi_CONSUME_EVENT) || (status == Mi_IGNORE_THIS_PART))
					&& (event.handlerTargetType != Mi_MONITOR_EVENT_HANDLER_TYPE))
					{
//System.out.println(this + " SENSOR ABOSORBING EVENT: " + handler + " EVENT = " + event);
					if (getTransform() != null)
						MiEvent.freeEvent(event);
					return(status);
					}
				}
			}

		if (getTransform() != null)
			MiEvent.freeEvent(event);
		return(Mi_PROPOGATE_EVENT);
		}
					/**------------------------------------------------------
					 * Gets whether this MiPart or any of it's attachments
					 * or parts has event handlers assigned.
					 * @return 		true if event handlers were assigned
					 *------------------------------------------------------*/
	public		boolean		hasEventHandlers()
		{
		return(((attachments != null) && (attachments.hasEventHandlers()))
			|| ((eventHandlers != null) && (eventHandlers.size() != 0)));
		}
					/**------------------------------------------------------
					 * Gets the events that this MiPart's event handlers are
					 * interested in.
					 * @return		an array of rquested events
					 *------------------------------------------------------*/
	public		MiEvent[]	getLocallyRequestedEventTypes()
		{
		if (eventHandlers == null)
			return(null);

		FastVector events = new FastVector();
		for (int i = 0; i < eventHandlers.size(); ++i)
			{
			events.append(((MiiEventHandler )eventHandlers.elementAt(i)).getRequestedEvents());
			}
		MiEvent[] eventArray = new MiEvent[events.size()];
		events.copyInto(eventArray);
		return(eventArray);
		}
					/**------------------------------------------------------
					 * Validates (recalculates) information about the event
					 * handlers assigned to this MiPart and it's parts. This
					 * information is used to speed up event handling by
					 * reducing the number of parts an event has to traverse.
					 *------------------------------------------------------*/
	protected	void		validateCachedEventHandlerInfo()
		{
		}
					/**------------------------------------------------------
					 * Gets whether the cached information about the event
					 * handlers assigned to this MiPart and it's parts is valid.
					 * @return		true if info is valid
					 *------------------------------------------------------*/
	protected	boolean		hasValidCachedEventHandlerInfo()
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Invalidates the cached information about the event
					 * handlers assigned to this MiPart and it's containers.
					 *------------------------------------------------------*/
	protected	void		invalidateCachedEventHandlerInfo()
		{
		if (containers != null)
			{
			for (int i = 0; i < containers.length; ++i)
				{
				if (containers[i].hasValidCachedEventHandlerInfo())
					containers[i].invalidateCachedEventHandlerInfo();
				}
			}
		}
					/**------------------------------------------------------
					 * Registers (or un-registers) the given event handler 
					 * with this MiPart's containing window if the event 
					 * handler is position independant. Accelerators (hotkeys)
					 * are examples of event handlers of this type.
					 * @param handler	the event handler
					 * @param addingEventHandler	
					 *			true if adding the event handler
					 *------------------------------------------------------*/
	protected 	void		handleEventHandlerRegistration(MiiEventHandler handler, boolean addingEventHandler)
		{
		if (!handler.isPositionDependent())
			{
			MiWindow window = getContainingWindow();
			if (window != null)
				{
				if (!addingEventHandler)
					window.removeGlobalEventHandler(handler);
				else
					window.appendGlobalEventHandler(handler);
				}
			}
		}
					/**------------------------------------------------------
					 * Specify whether this MiPart is to have it's event
					 * handling disabled because a container's event handling
					 * has been disabled. NOTE: this routine assumes that is
					 * one container is disabled then this part is disabled
					 * event though the event may be coming from another 
					 * container.
					 * @param flag		true if disabled
					 *------------------------------------------------------*/
	protected	void		setEventHandlingDisabledByContainer(boolean flag)
		{
		// If re-enabling
		if (!flag)
			{
			// ...make sure all of this part's containers aren't disabled
			// by one of their containers...
			for (int i = 0; i < getNumberOfContainers(); ++i)
				{
				if ((containers[i].getEventHandlingDisabledByContainer())
					|| (!containers[i].getEventHandlingEnabled()))
					{
					return;
					}
				}
			}
		setMiFlag(Mi_EVENT_HANDLING_DISABLED_BY_CONTAINER_MASK, flag);
		for (int i = 0; i < getNumberOfParts(); ++i)
			getPart(i).setEventHandlingDisabledByContainer(flag);
		}

	//***************************************************************************************
	// Action Handler Management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the number of action handlers assigned to this
					 * MiPart.
	 				 * @return 		the number of action handlers
					 *------------------------------------------------------*/
	public		int		getNumberOfActionHandlers()
		{
		return((actionHandlers == null) ? 0 : actionHandlers.size());
		}
					/**------------------------------------------------------
	 				 * Gets the action handler assigned to this MiPart at the 
					 * given index.
	 				 * @param index		the index of the action handler
	 				 * @return 		the action handler
					 *------------------------------------------------------*/
	public		MiiAction	getActionHandler(int index)
		{
		return((MiiAction )actionHandlers.elementAt(index));
		}
					/**------------------------------------------------------
					 * Gets the actions generated by this MiPart. 
					 * @return 		an array of actions generated by
					 *			this MiPart.
					 *------------------------------------------------------*/
	public final 	MiiAction[]	getActionsGenerated()
		{
		FastVector actions = new FastVector();

		actions.addElement(new MiAction(null, Mi_GOT_KEYBOARD_FOCUS_ACTION));
		actions.addElement(new MiAction(null, Mi_LOST_KEYBOARD_FOCUS_ACTION));
		actions.addElement(new MiAction(null, Mi_GOT_MOUSE_FOCUS_ACTION));
		actions.addElement(new MiAction(null, Mi_LOST_MOUSE_FOCUS_ACTION));
		actions.addElement(new MiAction(null, Mi_SELECTED_ACTION));
		actions.addElement(new MiAction(null, Mi_DESELECTED_ACTION));
		actions.addElement(new MiAction(null, Mi_INVISIBLE_ACTION));
		actions.addElement(new MiAction(null, Mi_VISIBLE_ACTION));
		actions.addElement(new MiAction(null, Mi_APPEARANCE_CHANGE_ACTION));
		actions.addElement(new MiAction(null, Mi_GEOMETRY_CHANGE_ACTION));
		actions.addElement(new MiAction(null, Mi_POSITION_CHANGE_ACTION));
		actions.addElement(new MiAction(null, Mi_SIZE_CHANGE_ACTION));
		MiiAction[] localActions = getActionsLocallyGenerated();
		if (localActions != null)
			actions.append(localActions);
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			localActions = getPart(i).getActionsLocallyGenerated();
			if (localActions != null)
				actions.append(localActions);
			}
		return((MiiAction[] )actions.toArray());
		}
					/**------------------------------------------------------
					 * Gets the actions generated by this particular subclass
					 * of MiPart. Override this and add to the array if the 
					 * subclass generates additional actions. The default 
					 * returns null.
					 * @return 		an array of additional actions 
					 *			generated by this MiPart or null
					 *------------------------------------------------------*/
	public		MiiAction[]	getActionsLocallyGenerated()
		{
		return(null);
		}
					/**------------------------------------------------------
					 * Gets the actions requested by all containers of this 
					 * MiPart. 
					 * @return 		the requested actions 
					 *------------------------------------------------------*/
	public		MiAction	getContainerRequestedActions()
		{
		return(containersRequestedActions);
		}
					/**------------------------------------------------------
					 * Gets the actions requested by all parts of this MiPart.
					 * @return 		the requested actions 
					 *------------------------------------------------------*/
	public		MiAction	getActionsRequestedFromParts()
		{
		return(actionsRequestedFromParts);
		}
					/**------------------------------------------------------
					 * Appends the given callback, to be called with the given
					 * argument when the given event occurs.
					 * @param handler 	the callback handler
					 * @param argument 	the callback argument
					 * @param event 	the event that triggers the
					 *			call to the callback handler
					 *------------------------------------------------------*/
	public		void		appendCommandHandler(
						MiiCommandHandler handler, String argument, MiEvent event)
		{
		appendEventHandler(new MiIExecuteCommand(event, handler, argument));
		}
					/**------------------------------------------------------
					 * Appends the given callback, to be called with the given
					 * argument when the given action type occurs.
					 * @param handler 	the callback handler
					 * @param argument 	the callback argument
					 * @param validAction 	the action type that triggers the 
					 *			call to the callback handler
					 *------------------------------------------------------*/
	public		void		appendCommandHandler(
						MiiCommandHandler handler, String argument, int validAction)
		{
		appendActionHandler(new MiActionCallback(handler, argument, validAction));
		}
					/**------------------------------------------------------
					 * Appends the given callback, to be called with the given
					 * argument when the default action type occurs.
					 * @param handler 	the callback handler
					 * @param argument 	the callback argument
					 *------------------------------------------------------*/
	public		void		appendCommandHandler(MiiCommandHandler handler, String argument)
		{
		appendActionHandler(new MiActionCallback(handler, argument));
		}
					/**------------------------------------------------------
					 * Removes the given callback from the list of callbacks
					 * that this MiPart calls.
					 * @param commandHandler the callback handler to remove
					 *------------------------------------------------------*/
	public		void		removeCommandHandler(MiiCommandHandler commandHandler)
		{
		for (int i = 0; i < actionHandlers.size(); ++i)
			{
			if ((actionHandlers.elementAt(i) instanceof MiActionCallback)
				&& (((MiActionCallback )actionHandlers.elementAt(i)).getCommandHandler() 
				== commandHandler))
				{
				actionHandlers.removeElementAt(i);
				--i;
				}
			}
		}
					/**------------------------------------------------------
					 * Removes the given callback that has the given argument
					 * from the list of callbacks that this MiPart calls.
					 * @param commandHandler 	the callback handler to remove
					 * @param command 		the callback argument
					 *------------------------------------------------------*/
	public		void		removeCommandHandler(MiiCommandHandler commandHandler, String command)
		{
		for (int i = 0; i < actionHandlers.size(); ++i)
			{
			if ((actionHandlers.elementAt(i) instanceof MiActionCallback)
				&& (((MiActionCallback )actionHandlers.elementAt(i)).getCommandHandler() 
					== commandHandler)
				&& (((MiActionCallback )actionHandlers.elementAt(i)).getCommand() 
					== command))
				{
				actionHandlers.removeElementAt(i);
				--i;
				}
			}
		}
					/**------------------------------------------------------
					 * Appends the given action, which will be sent to it's 
					 * action handler when the given event occurs.
					 * @param action 	the action to insert
					 * @param event 	the event that triggers the action
					 *------------------------------------------------------*/
	public		void		appendActionHandler(MiiAction action, MiEvent event)
		{
		appendEventHandler(new MiIExecuteActionHandler(event, action));
		}
					/**------------------------------------------------------
					 * Appends the given action, which will be sent to it's 
					 * action handler when any of it's valid actions occur.
					 * @param action 	the action to append
					 *------------------------------------------------------*/
	public		void		appendActionHandler(MiiAction action)
		{
		insertActionHandler(action, -1);
		}
					/**------------------------------------------------------
					 * Appends the given action handler which will be called
					 * when the given validAction occurs.
					 * @param handler 	the action handler to append
					 * @param validAction 	the action that triggers a call
					 * 			to the action handler
					 *------------------------------------------------------*/
	public		void		appendActionHandler(MiiActionHandler handler, int validAction)
		{
		appendActionHandler(new MiAction(handler, validAction));
		}
					/**------------------------------------------------------
					 * Appends the given action handler which will be called
					 * when the given validAction occurs.
					 * @param handler 	the action handler to append
					 * @param event 	the event that triggers the action
					 * @param validAction 	the action to send to the action handler
					 *------------------------------------------------------*/
	public		void		appendActionHandler(MiiActionHandler handler, MiEvent event, int validAction)
		{
		appendEventHandler(new MiIExecuteActionHandler(event, new MiAction(handler, validAction)));
		}
					/**------------------------------------------------------
					 * Appends the given action handler which will be called
					 * when one of the given validActions occurs.
					 * @param handler 	the action handler to append
					 * @param validAction1 	an action that triggers a call
					 * 			to the action handler
					 * @param validAction2 	an action that triggers a call
					 * 			to the action handler
					 *------------------------------------------------------*/
	public		void		appendActionHandler(MiiActionHandler handler, int validAction1, int validAction2)
		{
		appendActionHandler(new MiAction(handler, validAction1, validAction2));
		}
					/**------------------------------------------------------
					 * Inserts the given action (which will be sent to it's 
					 * action handler when the given event occurs) at the
					 * given index.
					 * @param action 	the action to insert
					 * @param event 	the event that triggers the action
					 * @param index	 	the index of the insertion point
					 *------------------------------------------------------*/
	public		void		insertActionHandler(MiiAction action, MiEvent event, int index)
		{
		// FIX: add code to check for this eventHandler in removeCommandHandlers routines
		insertEventHandler(new MiIExecuteActionHandler(event, action), index);
		}
					/**------------------------------------------------------
					 * Inserts the given action handler which will be called
					 * when the given validAction occurs.
					 * @param handler 	the action handler to append
					 * @param validAction 	the action that triggers a call
					 * 			to the action handler
					 * @param index		the index of the insertion point
					 *------------------------------------------------------*/
	public		void		insertActionHandler(MiiActionHandler handler, int validAction, int index)
		{
		insertActionHandler(new MiAction(handler, validAction), index);
		}
					/**------------------------------------------------------
					 * Inserts the given action, which will be sent to it's 
					 * action handler when any of it's valid actions occur, 
					 * at the given index.
					 * @param action 	the action to insert
					 * @param index	 	the index of the insertion point
					 *------------------------------------------------------*/
	public		void		insertActionHandler(MiiAction action, int index)
		{
		action.setObservedObject(this);

		if (action.isValidActionType(Mi_DRAW_ACTION))
			setMiFlag(Mi_DRAW_ACTIONS_DESIRED_MASK);

		if (action.isValidActionType(Mi_APPEARANCE_CHANGE_ACTION))
			setMiFlag(Mi_APPEARANCE_ACTIONS_DESIRED_MASK);

		if (action.isValidActionType(Mi_PROPERTY_CHANGE_ACTION))
			setMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK);

		if ((action.isValidActionType(Mi_GEOMETRY_CHANGE_ACTION))
			|| (action.isValidActionType(Mi_POSITION_CHANGE_ACTION))
			|| (action.isValidActionType(Mi_SIZE_CHANGE_ACTION)))
			{
			setMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK);
			}

		if (actionHandlers == null)
			actionHandlers = new FastVector();

		if (index == -1)
			actionHandlers.addElement(action);
		else
			actionHandlers.insertElementAt(action, index);

		reCalcActionsRequestedFromParts(action);
		}
					/**------------------------------------------------------
					 * Removes the given action from the list of actions
					 * that this MiPart calls.
					 * @param action 	the action to remove
					 *------------------------------------------------------*/
	public		void		removeActionHandler(MiiAction action)
		{
		actionHandlers.removeElement(action);
		reCalcActionsRequestedFromParts(action);
		if (action.isValidActionType(Mi_DRAW_ACTION))
			{
			reCalcDrawActionDesiredFlag();
			}
		if (action.isValidActionType(Mi_APPEARANCE_CHANGE_ACTION))
			{
			reCalcAppearanceActionDesiredFlag();
			}
		if (action.isValidActionType(Mi_PROPERTY_CHANGE_ACTION))
			{
			reCalcPropertyChangeActionDesiredFlag();
			}
		if ((action.isValidActionType(Mi_GEOMETRY_CHANGE_ACTION))
			|| (action.isValidActionType(Mi_POSITION_CHANGE_ACTION))
			|| (action.isValidActionType(Mi_SIZE_CHANGE_ACTION)))
			{
			reCalcGeomActionDesiredFlag();
			}
		}
					/**------------------------------------------------------
					 * Removes every instance of the given action handler from 
					 * the list of action handlers this MiPart calls.
					 * @param handler 	the action handler to remove
					 *------------------------------------------------------*/
	public		void		removeActionHandlers(MiiActionHandler handler)
		{
		if (actionHandlers == null)
			return;

		for (int i = 0; i < actionHandlers.size(); ++i)
			{
			MiiAction action = (MiiAction )actionHandlers.elementAt(i);
			if (action.getActionHandler() == handler)
				{
				actionHandlers.removeElementAt(i);
				--i;
				reCalcActionsRequestedFromParts(action);
				}
			}
		reCalcDrawActionDesiredFlag();
		reCalcAppearanceActionDesiredFlag();
		reCalcPropertyChangeActionDesiredFlag();
		reCalcGeomActionDesiredFlag();
		}
					/**------------------------------------------------------
					 * Removes the action handler at the given index.
					 * @param index 	the position of the action handler
					 *			to remove
					 *------------------------------------------------------*/
	public		void		removeActionHandler(int index)
		{
		MiiAction action = (MiiAction )actionHandlers.elementAt(index);
		actionHandlers.removeElementAt(index);
		reCalcActionsRequestedFromParts(action);
		reCalcDrawActionDesiredFlag();
		reCalcAppearanceActionDesiredFlag();
		reCalcPropertyChangeActionDesiredFlag();
		reCalcGeomActionDesiredFlag();
		}
					/**------------------------------------------------------
					 * Removes all action handlers.
					 *------------------------------------------------------*/
	public		void		removeAllActionHandlers()
		{
		if (actionHandlers != null)
			{
			while (actionHandlers.size() > 0)
				removeActionHandler((MiiAction )actionHandlers.elementAt(0));
			}
		}
	public		void		setActionDispatchingEnabled(boolean flag)
		{
		actionDispatchingEnabled = flag;
		}
	public		boolean		isActionDispatchingEnabled()
		{
		return(actionDispatchingEnabled);
		}
					/**------------------------------------------------------
					 * Dispatches an action of the given action type.
					 * @param actionType 	the action to dispatch
					 * @return 		Mi_PROPOGATE if it is OK to send
					 *			action to the next action handler
					 * 			Mi_CONSUME if it is NOT OK to send
					 *			action to the next action handler
					 *  			Mi_VETO if an action request was
					 *			vetoed.
					 *------------------------------------------------------*/
	public		int		dispatchAction(int actionType)
		{
		return(dispatchAction(actionType, null, this));
		}
					/**------------------------------------------------------
					 * Dispatches an action of the given action type with the
					 * given systemInfo. This systemInfo is added to each
					 * MiiAction which is sent to an action handler.
					 * @param actionType 	the action to dispatch
					 * @param systemInfo 	the additional info
					 * @return 		Mi_PROPOGATE if it is OK to send
					 *			action to the next action handler
					 *  			Mi_CONSUME if it is NOT OK to send
					 *			action to the next action handler
					 * 	 		Mi_VETO if an action request was
					 *			vetoed.
					 *------------------------------------------------------*/
	public		int		dispatchAction(int actionType, Object systemInfo)
		{
		return(dispatchAction(actionType, systemInfo, this));
		}
					/**------------------------------------------------------
					 * Dispatch an action of the given action type with the
					 * given systemInfo originating from the given part. This
					 * systemInfo is added to each MiiAction which is sent to 
					 * an action handler.
					 * @param actionType 	the action to dispatch
					 * @param systemInfo 	the additional info
					 * @param actionSource 	the part that originally generated
					 *			this action
					 * @return 		Mi_PROPOGATE if it is OK to send
					 *			action to the next action handler
					 *  			Mi_CONSUME if it is NOT OK to send
					 *			action to the next action handler
					 * 	 		Mi_VETO if an action request was
					 *			vetoed.
					 *------------------------------------------------------*/
	public		int		dispatchAction(int actionType, Object systemInfo, MiPart actionSource)
		{
		if ((actionHandlers != null) && (actionDispatchingEnabled))
			{
			if ((actionType & Mi_ACTION_PHASE_MASK) == 0)
				actionType |= Mi_COMMIT_ACTION_PHASE;

			for (int i = 0; i < actionHandlers.size(); ++i)
				{
				MiiAction action = (MiiAction )actionHandlers.elementAt(i);

				if (action.isValidActionType(actionType)) 
					{
					action.setActionSource(actionSource);
					action.setActionType(actionType);
					action.setActionSystemInfo(systemInfo);
					action.setVetoed(false);

					if (((actionType & Mi_ACTION_TYPE_MASK) == Mi_ACTIVATED_ACTION)
						&& (activateActionMouseAppearance != Mi_DEFAULT_CURSOR))
						{
						// Use rootWindow instead of this cause action may remove this
						// from root window, leaving cursor in a 'state'
						MiEditor rootWindow = getRootWindow();
						MiUtility.pushMouseAppearance(rootWindow,
							activateActionMouseAppearance,
							"MiPart.dispatchAction");
						boolean status = action.getActionHandler().processAction(
							action);
						MiUtility.popMouseAppearance(rootWindow, "MiPart.dispatchAction");
						if (!status)
							{
							action.setActionSource(this);
							action.setActionSystemInfo(null);
							return(action.isVetoed() ? Mi_VETO : Mi_CONSUME);
							}
						}
					else
						{
						if (!action.getActionHandler().processAction(action))
							{
							action.setActionSource(this);
							action.setActionSystemInfo(null);
							return(action.isVetoed() ? Mi_VETO : Mi_CONSUME);
							}
						}
					// Release the reference to perhaps large amounts of data that may need
					// to be freed up someday... 5-18-2001
					action.setActionSource(this);
					action.setActionSystemInfo(null);
					}
				}
			}
		actionType |= Mi_ACTIONS_OF_PARTS_OF_OBSERVED;
		if ((containersRequestedActions == null)
			|| (!containersRequestedActions.isValidActionType(actionType)))
			{
			return(Mi_PROPOGATE);
			}
		return(dispatchActionToContainers(actionType, systemInfo, actionSource));
		}
					/**------------------------------------------------------
					 * Dispatch an action of the given action type with the
					 * given systemInfo originating from the given part. This
					 * systemInfo is added to each MiiAction which is sent to 
					 * an action handler.
					 * @param actionType 	the action to dispatch
					 * @param systemInfo 	the additional info
					 * @param actionSource 	the part that originally generated
					 *			this action
					 * @return 		Mi_PROPOGATE if it is OK to send
					 *			action to the next action handler
					 * 	 		Mi_CONSUME if it is NOT OK to send
					 *			action to the next action handler
					 * 	 		Mi_VETO if an action request was
					 *			vetoed.
					 *------------------------------------------------------*/
	protected	int		dispatchActionToContainers(int actionType, Object systemInfo, MiPart actionSource)
		{
		//if (this instanceof MiEditor)
			//return(Mi_PROPOGATE);
		actionType |= Mi_ACTIONS_OF_PARTS_OF_OBSERVED;
		int numContainers = getNumberOfContainers();
		for (int i = 0; i < numContainers; ++i)
			{
			int result = getContainer(i).dispatchAction(actionType, systemInfo, actionSource);
			if (result != Mi_PROPOGATE)
				return(result);
			}
		return(Mi_PROPOGATE);
		}

					/**------------------------------------------------------
					 * Dispatch an action request of the given action type.
					 * @param actionType 	the action to dispatch
					 * @return 		true if the action was not vetoed
					 *------------------------------------------------------*/
	public		boolean		dispatchActionRequest(int actionType)
		{
		return(dispatchActionRequest(actionType, null));
		}
					/**------------------------------------------------------
					 * Dispatch an action request of the given action type with
					 * the given systemInfo. This systemInfo is added to each
					 * MiiAction which is sent to an action handler.
					 * @param actionType 	the action to dispatch
					 * @param systemInfo 	the additional info
					 * @return 		true if the action was not vetoed
					 *------------------------------------------------------*/
	public		boolean		dispatchActionRequest(int actionType, Object systemInfo)
		{
		return(dispatchActionRequest(actionType, systemInfo, this));
		}
					/**------------------------------------------------------
					 * Dispatch an action request of the given action type with
					 * the given systemInfo originating from the given part. This
					 * systemInfo is added to each MiiAction which is sent to 
					 * an action handler.
					 * @param actionType 	the action to dispatch
					 * @param systemInfo 	the additional info
					 * @param actionSource 	the part that originally generated
					 *			this action
					 * @return 		true if the action was not vetoed
					 *------------------------------------------------------*/
	public		boolean		dispatchActionRequest(int actionType, Object systemInfo, MiPart actionSource)
		{
		actionType |= Mi_REQUEST_ACTION_PHASE;
		for (int i = 0; ((actionHandlers != null) && (i < actionHandlers.size())); ++i)
			{
			MiiAction action = (MiiAction )actionHandlers.elementAt(i);
			if (action.isValidActionType(actionType))
				{
				action.setActionSource(actionSource);
				action.setActionType(actionType);
				action.setActionSystemInfo(systemInfo);
				action.setVetoed(false);
				if ((!action.getActionHandler().processAction(action)) ||(action.isVetoed()))
					{
					boolean returnFlag = true;
					if (action.isVetoed())
						{
						actionType &= ~Mi_REQUEST_ACTION_PHASE;
						dispatchAction(actionType | Mi_CANCEL_ACTION_PHASE);
						returnFlag = false;
						}
					action.setActionSource(this);
					action.setActionSystemInfo(null);
					return(returnFlag);
					}
				// Release the reference to perhaps large amounts of data that may need
				// to be freed up someday... 5-18-2001
				action.setActionSource(this);
				action.setActionSystemInfo(null);
				}
			}
		actionType |= Mi_ACTIONS_OF_PARTS_OF_OBSERVED;
		if ((containersRequestedActions == null)
			|| (!containersRequestedActions.isValidActionType(actionType)))
			{
			return(true);
			}
		return(dispatchActionRequestToContainers(actionType, systemInfo, actionSource));
		}
					/**------------------------------------------------------
					 * Dispatch an action request of the given action type with
					 * the given systemInfo originating from the given part. This
					 * systemInfo is added to each MiiAction which is sent to 
					 * an action handler.
					 * @param actionType 	the action to dispatch
					 * @param systemInfo 	the additional info
					 * @param actionSource 	the part that originally generated
					 *			this action
					 * @return 		true if the action was not vetoed
					 *------------------------------------------------------*/
	protected	boolean		dispatchActionRequestToContainers(int actionType, Object systemInfo, MiPart actionSource)
		{
		//if (this instanceof MiEditor)
			//return(true);
		actionType |= Mi_ACTIONS_OF_PARTS_OF_OBSERVED;
		int numContainers = getNumberOfContainers();
		for (int i = 0; i < numContainers; ++i)
			{
			if (!getContainer(i).dispatchActionRequest(actionType, systemInfo, actionSource))
				return(false);
			}
		return(true);
		}

					/**------------------------------------------------------
					 * Recalulate what actions are requested by this MiPart's
					 * parts from this MiPart.
					 * @param addedOrRemovedAction 	the action to that was added
					 *				or removed
					 *------------------------------------------------------*/
	protected	void		reCalcActionsRequestedFromParts(MiiAction addedOrRemovedAction)
		{
		if ((addedOrRemovedAction != null) 
			&& (addedOrRemovedAction.isInterestedInActionsOfPartsOfObserved()))
			{
			reCalcActionsRequestedFromParts();
			}
		}

					/**------------------------------------------------------
					 * Recalulate what actions are requested by this MiPart's
					 * parts from this MiPart.
					 *------------------------------------------------------*/
	protected	void		reCalcActionsRequestedFromParts()
		{
		actionsRequestedFromParts = new MiAction();
		for (int i = 0; ((actionHandlers != null) && (i < actionHandlers.size())); ++i)
			{
			actionsRequestedFromParts.addValidActionsRequestedFromPartsOfObserved(
				(MiiAction )actionHandlers.elementAt(i));
			}

		if (!actionsRequestedFromParts.isInterestedInActionsOfPartsOfObserved())
			actionsRequestedFromParts = null;

		for (int i = 0; i < getNumberOfParts(); ++i)
			getPart(i).reCalcActionsToDispatchToContainers();
		if (attachments != null)
			attachments.reCalcActionsToDispatchToContainers();
		}
					/**------------------------------------------------------
					 * Recalulate what actions are requested by this MiPart's
					 * parts from this MiPart.
					 * @param action	a part's action that is examined
					 *			to see if it is requesting any
					 *			actions from this MiPart.
					 *------------------------------------------------------*/
	protected	void		getActionsRequestedFromParts(MiAction action)
		{
		if (actionsRequestedFromParts != null)
			action.addValidActionsRequestedFromPartsOfObserved(actionsRequestedFromParts);
		if (containersRequestedActions != null)
			action.addValidActionsRequestedFromPartsOfObserved(containersRequestedActions);
		}
					/**------------------------------------------------------
					 * Recalulate if this or any container of this MiPart has
					 * an action handler interested in the Mi_DRAW_ACTION. Sets
					 * a local flag that is tested after each draw to see if
					 * a Mi_DRAW_ACTION should be generated and dispatched.
					 *------------------------------------------------------*/
	protected	void		reCalcDrawActionDesiredFlag()
		{
		if ((containersRequestedActions != null) 
			&& (containersRequestedActions.isValidActionType(
			Mi_DRAW_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
			{
			setMiFlag(Mi_DRAW_ACTIONS_DESIRED_MASK);
			return;
			}
		resetMiFlag(Mi_DRAW_ACTIONS_DESIRED_MASK);
		for (int i = 0; ((actionHandlers != null) && (i < actionHandlers.size())); ++i)
			{
			if (((MiiAction )actionHandlers.elementAt(i)).isValidActionType(
				Mi_DRAW_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
				{
				setMiFlag(Mi_DRAW_ACTIONS_DESIRED_MASK);
				return;
				}
			}
		}
					/**------------------------------------------------------
					 * Recalulate if this or any container of this MiPart has
					 * an action handler interested in the 
					 * Mi_APPEARANCE_CHANGE_ACTION. Sets a local flag that is 
					 * tested after each appearance change to see if
					 * a Mi_APPEARANCE_CHANGE_ACTION should be generated and 
					 * dispatched.
					 *------------------------------------------------------*/
	protected	void		reCalcAppearanceActionDesiredFlag()
		{
		if ((containersRequestedActions != null) 
			&& (containersRequestedActions.isValidActionType(
			Mi_APPEARANCE_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
			{
			setMiFlag(Mi_APPEARANCE_ACTIONS_DESIRED_MASK);
			return;
			}
		resetMiFlag(Mi_APPEARANCE_ACTIONS_DESIRED_MASK);
		for (int i = 0; ((actionHandlers != null) && (i < actionHandlers.size())); ++i)
			{
			if (((MiiAction )actionHandlers.elementAt(i)).isValidActionType(
			Mi_APPEARANCE_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
				{
				setMiFlag(Mi_APPEARANCE_ACTIONS_DESIRED_MASK);
				return;
				}
			}
		}
					/**------------------------------------------------------
					 * Recalulate if this or any container of this MiPart has
					 * an action handler interested in the 
					 * Mi_PROPERTY_CHANGE_ACTION. Sets a local flag that is 
					 * tested after each appearance change to see if
					 * a Mi_PROPERTY_CHANGE_ACTION should be generated and 
					 * dispatched.
					 *------------------------------------------------------*/
	protected	void		reCalcPropertyChangeActionDesiredFlag()
		{
		if ((containersRequestedActions != null) 
			&& (containersRequestedActions.isValidActionType(
			Mi_PROPERTY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
			{
			setMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK);
			return;
			}
		resetMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK);
		for (int i = 0; ((actionHandlers != null) && (i < actionHandlers.size())); ++i)
			{
			if (((MiiAction )actionHandlers.elementAt(i)).isValidActionType(
			Mi_PROPERTY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
				{
				setMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK);
				return;
				}
			}
		}
					/**------------------------------------------------------
					 * Recalulate if this or any container of this MiPart has
					 * an action handler interested in the 
					 * Mi_GEOMETRY_CHANGE_ACTION, Mi_POSITION_CHANGE_ACTION and
					 * Mi_SIZE_CHANGE_ACTION. Sets a local flag that is 
					 * tested after each geometric change to see if one of
					 * these actions should be generated and dispatched.
					 *------------------------------------------------------*/
	protected	void		reCalcGeomActionDesiredFlag()
		{
		if ((containersRequestedActions != null) 
			&& ((containersRequestedActions.isValidActionType(
			Mi_GEOMETRY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			|| (containersRequestedActions.isValidActionType(
			Mi_POSITION_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			|| (containersRequestedActions.isValidActionType(
			Mi_SIZE_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))))
			{
			setMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK);
			return;
			}
		resetMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK);
		for (int i = 0; ((actionHandlers != null) && (i < actionHandlers.size())); ++i)
			{
			MiiAction action = (MiiAction )actionHandlers.elementAt(i);
			if ((action.isValidActionType(
				Mi_GEOMETRY_CHANGE_ACTION))
				|| (action.isValidActionType(
				Mi_POSITION_CHANGE_ACTION))
				|| (action.isValidActionType(
				Mi_SIZE_CHANGE_ACTION))
			|| (action.isValidActionType(
				Mi_GEOMETRY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
				|| (action.isValidActionType(
				Mi_POSITION_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
				|| (action.isValidActionType(
				Mi_SIZE_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
				{
				setMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK);
				return;
				}
			}
		}
					/**------------------------------------------------------
					 * Add the given actions to the actions to forward to this
					 * MiPart's containers.
					 * @param actions	actions to dispatch to containers
					 *------------------------------------------------------*/
	protected	void		addActionsToDispatchToContainers(MiiAction actions)
		{
		if ((actions == null) || (!actions.isInterestedInActionsOfPartsOfObserved()))
			return;
		if (containersRequestedActions == null)
			containersRequestedActions = new MiAction();
		containersRequestedActions.addValidActionsRequestedFromPartsOfObserved(actions);
		if (containersRequestedActions.isValidActionType(
			Mi_DRAW_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			{
			setMiFlag(Mi_DRAW_ACTIONS_DESIRED_MASK);
			}

		if (containersRequestedActions.isValidActionType(
			Mi_APPEARANCE_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			{
			setMiFlag(Mi_APPEARANCE_ACTIONS_DESIRED_MASK);
			}

		if (containersRequestedActions.isValidActionType(
			Mi_PROPERTY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			{
			setMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK);
			}

		if ((containersRequestedActions.isValidActionType(
			Mi_GEOMETRY_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			|| (containersRequestedActions.isValidActionType(
			Mi_POSITION_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			|| (containersRequestedActions.isValidActionType(
			Mi_SIZE_CHANGE_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED)))
			{
			setMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK);
			}
		// For all children, modify their containerMask
		for (int i = 0; i < getNumberOfParts(); ++i)
			getPart(i).addActionsToDispatchToContainers(actions);
		if (attachments != null)
			attachments.addActionsToDispatchToContainers(actions);
		}
					/**------------------------------------------------------
					 * Recalculate the actions to forward to this MiPart's 
					 * containers.
					 *------------------------------------------------------*/
	protected	void		reCalcActionsToDispatchToContainers()
		{
		containersRequestedActions = null;
		int numContainers = (containers == null) ? 0 : containers.length;
		MiAction actions = new MiAction();
		for (int i = 0; i < numContainers; ++i)
			{
			containers[i].getActionsRequestedFromParts(actions);
			}
		if (actions.isInterestedInActionsOfPartsOfObserved())
			{
			containersRequestedActions = actions;
			}
		reCalcDrawActionDesiredFlag();
		reCalcAppearanceActionDesiredFlag();
		reCalcPropertyChangeActionDesiredFlag();
		reCalcGeomActionDesiredFlag();
		for (int i = 0; i < getNumberOfParts(); ++i)
			getPart(i).reCalcActionsToDispatchToContainers();
		if (attachments != null)
			attachments.reCalcActionsToDispatchToContainers();
		}

					/**------------------------------------------------------
					 * Gets whether DRAW_ACTIONs need to be generated and
					 * dispatched.
					 * @return		true if the DRAW_ACTION action
					 *			needs to be generated and dispatched
					 *------------------------------------------------------*/
	protected	boolean		getDrawActionsNeedToBeDispatched()
		{
		return(hasMiFlag(Mi_DRAW_ACTIONS_DESIRED_MASK));
		}
					/**------------------------------------------------------
					 * Gets whether the geometry change actions need to be 
					 * generated and dispatched. These are the
					 *	Mi_GEOMETRY_CHANGE_ACTION,
					 *	Mi_POSITION_CHANGE_ACTION, and
					 *	Mi_SIZE_CHANGE_ACTION actions.
					 * @return		true if the actions need to be 
					 *			generated and dispatched
					 *------------------------------------------------------*/
	protected	boolean		getGeometryActionsNeedToBeDispatched()
		{
		return(hasMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK));
		}
					/**------------------------------------------------------
					 * Gets whether Mi_APPEARANCE_CHANGE_ACTIONs need to be 
					 * generated and dispatched.
					 * @return		true if the 
					 *			Mi_APPEARANCE_CHANGE_ACTION action
					 *			needs to be generated and dispatched
					 *------------------------------------------------------*/
	protected	boolean		getAppearanceActionsNeedToBeDispatched()
		{
		return(hasMiFlag(Mi_APPEARANCE_ACTIONS_DESIRED_MASK));
		}
					/**------------------------------------------------------
					 * Gets whether Mi_PROPERTY_CHANGE_ACTIONs need to be 
					 * generated and dispatched.
					 * @return		true if the 
					 *			Mi_PROPERTY_CHANGE_ACTION action
					 *			needs to be generated and dispatched
					 *------------------------------------------------------*/
	protected	boolean		getPropertyChangeActionsNeedToBeDispatched()
		{
		return(hasMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK));
		}
					/**------------------------------------------------------
	 				 * Generates and dispatches an action if a previously
					 * calculated flag indicates there are action handlers
					 * interested in the action.
	 				 * @action 		Mi_SIZE_CHANGE_ACTION
					 *------------------------------------------------------*/
	protected	void		sizeChanged()
		{
		if (hasMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK))
			dispatchAction(Mi_SIZE_CHANGE_ACTION);
		}
					/**------------------------------------------------------
	 				 * Generates and dispatches an action if a previously
					 * calculated flag indicates there are action handlers
					 * interested in the action.
	 				 * @action 		Mi_POSITION_CHANGE_ACTION
					 *------------------------------------------------------*/
	protected	void		positionChanged()
		{
		if (hasMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK))
			dispatchAction(Mi_POSITION_CHANGE_ACTION);
		}
					/**------------------------------------------------------
	 				 * Generates and dispatches an action if a previously
					 * calculated flag indicates there are action handlers
					 * interested in the action.
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
					 *------------------------------------------------------*/
	protected	void		geometryChanged()
		{
		if ((extensions != null) && (extensions.connections != null)
			&& (extensions.connections.size() != 0))
			{
			for (int i = 0; i < extensions.connections.size(); ++i)
				{
				((MiConnection )extensions.connections.elementAt(i)).nodeGeometryChanged(this);
				}
			}
		if (hasMiFlag(Mi_GEOM_ACTIONS_DESIRED_MASK))
			dispatchAction(Mi_GEOMETRY_CHANGE_ACTION);
		}
					/**------------------------------------------------------
	 				 * Generates and dispatches an action if a previously
					 * calculated flag indicates there are action handlers
					 * interested in the action.
	 				 * @action 		Mi_APPEARANCE_CHANGE_ACTION
					 *------------------------------------------------------*/
	protected	void		appearanceChanged()
		{
		if (hasMiFlag(Mi_APPEARANCE_ACTIONS_DESIRED_MASK))
			dispatchAction(Mi_APPEARANCE_CHANGE_ACTION);
		}
	protected	boolean		propertyChangeRequest(MiPropertyChange event)
		{
		boolean status = dispatchActionRequest(Mi_PROPERTY_CHANGE_ACTION, event, this);
		if (!status)
			{
			if (MiSystem.isThrowingExceptionsWhenPropertyChangeVetoed())
				{
				throw new MiPropertyChangeVetoExeception(this + ": Property: " 
						+ event.getPropertyName() + " cannot be changed to: "
						+ event.getPropertyValue());
				}
			return(false);
			}
		return(true);
		}
	protected	void		propertyChanged(String name, String value, String oldValue)
		{
		if (hasMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK))
			{
			MiPropertyChange event = new MiPropertyChange(null, name, value, oldValue);
			dispatchAction(Mi_PROPERTY_CHANGE_ACTION, event, this);
			}
		}
	protected	void		propertyChanged(MiPropertyChange event)
		{
		if (hasMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK))
			dispatchAction(Mi_PROPERTY_CHANGE_ACTION, event, this);
		}
	protected	MiChangedAttributes	getValidatedChangedAttributes(MiAttributes oldAtts, MiAttributes newAtts)
		{
		MiChangedAttributes changedAttributes = newAtts.getChangedAttributes(oldAtts);
		int[] changedAttIndexes = changedAttributes.changedAttributeIndexes;
		MiPropertyChange[] attributeChangeEvents = changedAttributes.changedAttributeEvents;
		for (int i = 0; i < changedAttIndexes.length; ++i)
			{
			int index = changedAttIndexes[i];
			if (index == -1)
				break;
			MiPropertyChange event = attributeChangeEvents[index];
			if (!propertyChangeRequest(event))
				{
				changedAttIndexes[i] = -2;
				newAtts = newAtts.copyAttribute(oldAtts, index);
				}
			}
		changedAttributes.newAttributes = newAtts;
		return(changedAttributes);
		}
	protected	void		dispatchChangedAttributeActions(MiChangedAttributes changedAttributes)
		{
		int[] validatedAttributeIndexes = changedAttributes.changedAttributeIndexes;
		MiPropertyChange[] attributeChangeEvents = changedAttributes.changedAttributeEvents;
		for (int i = 0; i < validatedAttributeIndexes.length; ++i)
			{
			int index = validatedAttributeIndexes[i];
			if (index == -1)
				return;
			if (index != -2)
				propertyChanged(attributeChangeEvents[index]);
			}
		}
	public		MiValueValidationError	validatePropertyValue(String name, String value)
		{
		String oldValue = getPropertyValue(name);

		if (oldValue == null)
			oldValue = "";
		if (value == null)
			value = "";

		if (oldValue.equals(name))
			return(null);

		MiPropertyDescription desc = getPropertyDescriptions().elementAt(name);
		MiValueValidationError errorMsg = desc.validateValue(value);
		if (errorMsg != null)
			{
			MiDebug.println(this + ": Validation failed: unable to set \"" + name 
				+ "\" to \"" + value + "\" because: " + errorMsg.getShortDescription());
			return(errorMsg);
			}

		if (hasMiFlag(Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK))
			{
			MiPropertyChange event = new MiPropertyChange(null, name, value, oldValue);
			if (!dispatchActionRequest(Mi_PROPERTY_CHANGE_ACTION, event))
				{
				MiDebug.println(this + ": Validation failed: unable to set \"" + name 
					+ "\" to \"" + value + "\" because: " 
					+ (event.getVetoMessage() == null ? "Dont know" 
					: event.getVetoMessage().getShortDescription()));
				if (event.getVetoMessage() != null)
					return(event.getVetoMessage());
				return(new MiValueValidationError(
					Mi_GENERIC_PROPERTY_VALIDATION_ERROR));
				}
			}
		return(null);
		}


	//***************************************************************************************
	// Manipulator Management
	//***************************************************************************************

	public		void		setPrototypeManipulator(MiiManipulator prototype)
		{
		if (extensions == null)
			{
			extensions = new MiPartExtensions(this);
			}
		extensions.prototypeManipulator = prototype;
		}
	public		MiiManipulator	getPrototypeManipulator()
		{
		if (extensions != null)
			{
			return(extensions.prototypeManipulator);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Makes a manipulator for this MiPart. Override this,
                                         * if desired, as it implements the core functionality.
					 * The default behavior is to return an instance of the
					 * MiBoundsManipulator class.
	 				 * @return 		the manipulator
					 *------------------------------------------------------*/
	public		MiiManipulator	makeManipulator()
		{
		if (getPrototypeManipulator() != null)
			{
			return(getPrototypeManipulator().create(this));
			}
		return(new MiBoundsManipulator(this));
		}
					/**------------------------------------------------------
	 				 * Gets the manipulator, if any, that has been assigned 
					 * to this MiPart. 
	 				 * @return 		the manipulator or null
					 *------------------------------------------------------*/
	public		MiiManipulator	getManipulator()
		{
		for (int i = 0; i < getNumberOfAttachments(); ++i)
			{
			if (getAttachment(i) instanceof MiiManipulator)
				return((MiiManipulator )getAttachment(i));
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Makes a manipulator for the layout, if any, of this 
					 * MiPart. 
	 				 * @return 		the layout manipulator or null
					 *------------------------------------------------------*/
	public		MiiLayoutManipulator	makeLayoutManipulator()
		{
		if ((getLayout() != null) && (getLayout() instanceof MiManipulatableLayout))
			{
			return(((MiManipulatableLayout )getLayout()).makeLayoutManipulator());
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets this MiPart's layout's manipulator, if any, that 
					 * has been assigned to this MiPart. 
	 				 * @return 		the layout manipulator or null
					 *------------------------------------------------------*/
	public		MiiLayoutManipulator	getLayoutManipulator()
		{
		for (int i = 0; i < getNumberOfAttachments(); ++i)
			{
			if (getAttachment(i) instanceof MiiLayoutManipulator)
				return((MiiLayoutManipulator )getAttachment(i));
			}
		return(null);
		}

	//***************************************************************************************
	// Special Containers Management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the root window that contains this MiPart. If
					 * this MiPart is a root window then this is returned.
					 * If there isn't any root window that contains this
					 * MiPart then null is returned. NOTE: only the left
					 * side (containers at index 0) are examined.
	 				 * @return 		the containing MiWindow that is
					 *			a root window or null
					 *------------------------------------------------------*/
	public		MiWindow	getRootWindow()
		{
		if ((this instanceof MiWindow) && (((MiWindow )this).isRootWindow()))
			return((MiWindow )this);

		MiPart c = this;
		while (c.getNumberOfContainers() > 0)
			{
			c = c.getContainer(0);
			if ((c instanceof MiWindow) && (((MiWindow )c).isRootWindow()))
				return((MiWindow )c);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the MiWindow that contains this MiPart. If
					 * this MiPart is a MiWindow then this is returned.
					 * If there isn't any MiWindow that contains this
					 * MiPart then null is returned. NOTE: only the left
					 * side (containers at index 0) are examined.
	 				 * @return 		the containing MiWindow or null
					 *------------------------------------------------------*/
	public		MiWindow	getContainingWindow()
		{
		if (this instanceof MiWindow)
			return((MiWindow )this);

		MiPart obj = this;
		while (obj.getNumberOfContainers() > 0)
			{
			obj = obj.getContainer(0);
			if (obj instanceof MiWindow)
				return((MiWindow )obj);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the MiEditor that contains this MiPart. If
					 * this MiPart is a MiEditor then this is returned.
					 * If there isn't any MiEditor that contains this
					 * MiPart then null is returned. NOTE: only the left
					 * side (containers at index 0) are examined.
	 				 * @return 		the containing MiEditor or null
					 *------------------------------------------------------*/
	public		MiEditor	getContainingEditor()
		{
		if (this instanceof MiEditor)
			return((MiEditor )this);

		MiPart obj = this;
		if (obj.getNumberOfContainers() > 0)
			{
			obj = obj.getContainer(0);
			if (obj instanceof MiEditor)
				return((MiEditor )obj);
			return(obj.getContainingEditor());
			}
		return(null);
		}

	//***************************************************************************************
	// Debug Management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the unique integer id associated with this MiPart.
	 				 * @return 		this MiPart's unique id
					 *------------------------------------------------------*/
	public		int		getID()
		{
		return(id);
		}
					/**------------------------------------------------------
	 				 * Gets the total number of parts created since this class
					 * was loaded.
	 				 * @return 		number of MiParts created
					 *------------------------------------------------------*/
	public static	int		getTotalNumberOfPartsCreated()
		{
		return(numCreated);
		}
					/**------------------------------------------------------
	 				 * Gets the number of event handlers assigned to this
					 * MiPart.
	 				 * @return 		the number of event handlers
					 *------------------------------------------------------*/
	public		int		getNumberOfEventHandlers()
		{
		return((eventHandlers == null) ? 0 : eventHandlers.size());
		}
					/**------------------------------------------------------
	 				 * Gets the event handler assigned to this MiPart at the 
					 * given index.
	 				 * @param index		the index of the event handler
	 				 * @return 		the number of event handlers
					 *------------------------------------------------------*/
	public		MiiEventHandler	getEventHandler(int index)
		{
		return((MiiEventHandler )eventHandlers.elementAt(index));
		}
					/**------------------------------------------------------
	 				 * Gets the event handler assigned to this MiPart with the 
					 * given class name.
	 				 * @param className	the className of the event handler
	 				 * @return 		the event handler or null
					 *------------------------------------------------------*/
	public		MiiEventHandler	getEventHandlerWithClass(String className)
		{
		if (eventHandlers == null)
			return(null);

		for (int i = 0; i < eventHandlers.size(); ++i)
			{
			if (eventHandlers.elementAt(i).getClass().getName().endsWith(className))
				return(getEventHandler(i));
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the event handler assigned to this MiPart with the 
					 * given name.
	 				 * @param name		the name of the event handler
	 				 * @return 		the event handler or null
					 *------------------------------------------------------*/
	public		MiiEventHandler	getEventHandler(String name)
		{
		if (eventHandlers == null)
			return(null);

		for (int i = 0; i < eventHandlers.size(); ++i)
			{
			if (name.equals(getEventHandler(i).getName()))
				return(getEventHandler(i));
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the list of attachments assigned to this MiPart.
					 * Note this should be used sparely, as the master MiPart's
					 * API should be used to add and remove things from an
					 * MiAttachments.
	 				 * @return 		the list of attachments
					 *------------------------------------------------------*/
	public		MiAttachments	getAttachments()
		{
		return(attachments);
		}

	//***************************************************************************************
	// State Bit Mask Management
	//***************************************************************************************

	private static final int	Mi_HAS_MOUSE_FOCUS_MASK				= 1 << 1;
	private static final int	Mi_HAS_KEYBOARD_FOCUS_MASK			= 1 << 2;
	private static final int	Mi_HAS_ENTER_KEY_FOCUS_MASK			= 1 << 3;
	private static final int	Mi_SELECTED_MASK				= 1 << 4;
	private static final int	Mi_SENSITIVE_MASK				= 1 << 5;
	private static final int	Mi_DOUBLE_BUFFERED_MASK				= 1 << 6;
	private static final int	Mi_PREFERRED_SIZE_OVERRIDDEN_MASK		= 1 << 7;
	private static final int	Mi_MINIMUM_SIZE_OVERRIDDEN_MASK			= 1 << 8;
	private static final int	Mi_HAS_CUSTOM_RENDERERS_MASK			= 1 << 9;
	private static final int	Mi_IS_OPAQUE_RECTANGLE_MASK			= 1 << 10;
	private static final int	Mi_CONTAINS_OPAQUE_RECTANGLES_MASK		= 1 << 11;
	private static final int	Mi_DEEPLY_INVALIDATE_AREAS_MASK			= 1 << 12;
	private static final int	Mi_THIS_OR_PART_HAS_INVALID_AREA_MASK		= 1 << 13;
	private static final int	Mi_VALID_LAYOUT_MASK				= 1 << 14;
	private static final int	Mi_VALIDATING_LAYOUT_MASK			= 1 << 15;
	private static final int	Mi_INVALID_AREA_NOTIFICATIONS_ENABLED_MASK	= 1 << 16;
	private static final int	Mi_IN_COMING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK	= 1 << 17;
	private static final int	Mi_OUT_GOING_INVALID_LAYOUT_NOTIFICATIONS_ENABLED_MASK	= 1 << 18;
	private static final int	Mi_EVENT_HANDLING_ENABLED_MASK			= 1 << 19;
	private static final int	Mi_DOUBLE_BUFFER_VALID_MASK			= 1 << 20;
	private static final int	Mi_DRAW_ACTIONS_DESIRED_MASK			= 1 << 21;
	private static final int	Mi_APPEARANCE_ACTIONS_DESIRED_MASK		= 1 << 22;
	private static final int	Mi_PROPERTY_CHANGE_ACTIONS_DESIRED_MASK		= 1 << 23;
	private static final int	Mi_GEOM_ACTIONS_DESIRED_MASK			= 1 << 24;
	private static final int	Mi_EVENT_HANDLING_DISABLED_BY_CONTAINER_MASK	= 1 << 25;
	private static final int	Mi_CALCING_PREF_OR_MIN_SIZES_MASK		= 1 << 26;
	private static final int	Mi_SENSITIVITY_DISABLED_BY_CONTAINER_MASK	= 1 << 27;
	private static final int	Mi_VALID_BOUNDS_MASK				= 1 << 28;

	private		int			stateMask;

	private final 	boolean		hasMiFlag(int mask)
		{
		return((stateMask & mask) != 0);
		}
	private final 	void		setMiFlag(int mask)
		{
		stateMask |= mask;
		}
	private final 	void		setMiFlag(int mask, boolean flag)
		{
		if (flag)
			stateMask |= mask;
		else
			stateMask &= ~mask;
		}
	private final 	void		resetMiFlag(int mask)
		{
		stateMask &= ~mask;
		}
	}
class MiPropertyChangeVetoExeception extends RuntimeException
	{
	public				MiPropertyChangeVetoExeception(String msg)
		{
		super(msg);
		}
	}
class MiPartExtensions
	{
			MiPart			target;
			Hashtable		resources;
			MiMargins		margins;
			Image			doubleBuffer;
			MiiLayout		layout;
			String[]		supportedImportFormats;
			MiiDragAndDropBehavior	dndBehavior;
			MiiSelectionGraphics	selectionGraphics;
			FastVector		connections;
			MiCustomLookAndFeels	customLookAndFeels;
			double			rotationRadians;
			boolean			flipped;
			MiiManipulator 		prototypeManipulator;
			MiDistance		minimumPickAreaSize;


	public					MiPartExtensions(MiPart target)
		{
		this.target= target;
		}

	public		MiPartExtensions	copy(MiPart target)
		{
		MiPartExtensions theCopy = new MiPartExtensions(target);

		if (margins != null)
			theCopy.margins = margins;

		if (resources != null)
			theCopy.resources = (Hashtable )resources.clone();

		theCopy.dndBehavior = dndBehavior;
		theCopy.selectionGraphics = selectionGraphics;
		theCopy.rotationRadians = rotationRadians;
		theCopy.flipped = flipped;
		theCopy.prototypeManipulator = prototypeManipulator;
		theCopy.minimumPickAreaSize = minimumPickAreaSize;

		return(theCopy);
		}
	public		void		setResource(String name, Object value)
		{
		if (resources == null)
			resources = new Hashtable(5);
		if (value == null)
			removeResource(name);
		else
			resources.put(name, value);
		}
	public		Object		getResource(String name)
		{
		if (resources == null)
			return(null);
		return(resources.get(name));
		}
	public		void		removeResource(String name)
		{
		if (resources != null)
			resources.remove(name);
		}
	public		int		getNumberOfResources()
		{
		if (resources != null)
			return(resources.size());
		return(0);
		}
	public		String		getResourceName(int index)
		{
		Enumeration keys = resources.keys();
		int i = 0;
		while (keys.hasMoreElements())
			{
			Object obj = keys.nextElement();
			if (i == index)
				return((String )obj);
			++i;
			}
		throw new IllegalArgumentException(MiDebug.getMicaClassName(target) 
			+ ": Resource index: " + index + " > number of resources: " + resources.size());
		}
	}

