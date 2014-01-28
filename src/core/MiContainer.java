
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Toolkit                   *
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

/**----------------------------------------------------------------------------------------------
 * This class is a MiPart that can contain other MiParts.
 * The MiPart methods that can add, inquire and remove MiParts
 * are implemented here.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiContainer extends MiPart
	{
	private static	String[]	defaultContainerImportFormats = {Mi_MiPART_FORMAT, Mi_STRING_FORMAT};
	private static 	int		capacityIncrement;
	private		MiPart[]	objects				= new MiPart[2];
	private		int		numberOfParts;
	private		boolean		hasValidCachedEventHandlerInfo;
	private		boolean		hasEventHandlersFlag;
	private 	boolean		keepConnectionsBelowNodes;


					/**------------------------------------------------------
	 				 * Constructs a new MiContainer. 
					 *------------------------------------------------------*/
	public				MiContainer()
		{
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
					 * @see			MiPart#setSupportedImportFormats
					 * @see			MiPart#getSupportedExportFormats
					 *------------------------------------------------------*/
	public		String[] 	getSupportedImportFormats()
		{
		return(super.getSupportedImportFormats() != null
			? super.getSupportedImportFormats() : defaultContainerImportFormats);
		}
					/**------------------------------------------------------
	 				 * Sets whether the order of the MiParts in this container
					 * are kept such that MiConnections are at the begining
					 * of the list. This is useful when and one does not want
					 * connections to be drawn in front of the nodes.
					 * @param flag		true if connections to be drawn 
					 *			first
					 * @see		MiConnection#setTruncateLineAtEndPointPartBoundries
					 * @see		MiContainer#getKeepConnectionsBelowNodes
					 *------------------------------------------------------*/
	public		void		setKeepConnectionsBelowNodes(boolean flag)
		{
		keepConnectionsBelowNodes = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether the connections are to be drawn before
					 * (behind) nodes.
					 * @return 		true if connections to be drawn 
					 *			first
					 * @see		MiContainer#setKeepConnectionsBelowNodes
					 *------------------------------------------------------*/
	public		boolean		getKeepConnectionsBelowNodes()
		{
		return(keepConnectionsBelowNodes);
		}
	//***************************************************************************************
	// Drag and Drop management
	//***************************************************************************************
					/**------------------------------------------------------
	 				 * Import the data specified by the given data transfer
					 * operation. If the data is textual then a MiText shape
					 * is created and appended to this container. If the data
					 * is a MiPart then it is appended to this container.
					 * @param transfer	the data to import
					 * @overrides		MiPart#doImport
					 *------------------------------------------------------*/
	public		void		doImport(MiDataTransferOperation transfer)
		{
		MiPart obj = null;
		if (transfer.getDataFormat().equals(Mi_STRING_FORMAT))
			obj = new MiText((String )transfer.getData());
		else if (transfer.getDataFormat().equals(Mi_MiPART_FORMAT))
			obj = (MiPart )transfer.getData();
		else
			{
			throw new IllegalArgumentException(this 
				+ ": Unknown import format \"" + transfer.getDataFormat() + "\"");
			}
		// Use reference here? obj = new MiReference((MiPart )transfer.getSource());

		/***** The dropped obj should be the same actual size as that which was drag&dropped
		thos it may appear to grow or shrink to the user. This line here would make
		the dropped obj appear the same size as it did in the palette, but which 
		would actually change the size.
		if (transfer.getLookTargetBounds() != null)
			obj.setBounds(transfer.getLookTargetBounds());
		else
		if ((!(obj instanceof MiConnection))
			|| (((MiConnection )obj).getSource() == null)
			|| (((MiConnection )obj).getDestination() == null))
		*****/
			{
			obj.setCenter(transfer.getLookTargetPosition());
			}
		// ---------------------------------------------------------------
		// Add the transfered object to the correct layer, if any.
		// ---------------------------------------------------------------
		appendItem(obj);
		transfer.setTransferredData(obj);
		}
	//***************************************************************************************
	// Geometry management
	//***************************************************************************************
					/**------------------------------------------------------
	 				 * Scales the parts that this container has.
					 * @param center	the center of the scaling
					 * @param scale		the scale factors
					 * @overrides		MiPart#scaleParts
					 *------------------------------------------------------*/
	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		// If parts will not be placed by this layout...
		if (getLayout() == null)
			{
			if (hasFixedAspectRatio())
				{
				scale = new MiScale(scale);
				if (scale.x < scale.y)
					scale.y = scale.x;
				else
					scale.x = scale.y;
				}

			for (int i = 0; i < numberOfParts; ++i)
				{
				objects[i].scale(center, scale);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Translates the parts that this container has.
					 * @param x		the x distance to translate
					 * @param y		the y distance to translate
					 * @overrides		MiPart#translateParts
					 *------------------------------------------------------*/
	protected	void		translateParts(MiDistance x, MiDistance y)
		{
		for (int i = 0; i < numberOfParts; ++i)
			{
			objects[i].translatePart(x, y);
			}
		}
					/**------------------------------------------------------
	 				 * Translates a part of this container at the given index.
					 * @param partNum	the index of the part to translate
					 * @param vector	the translation factor
					 * @overrides		MiPart#translate
					 *------------------------------------------------------*/
	public	void			translate(int partNum, MiVector vector)
		{
		if (numberOfParts > partNum)
			objects[partNum].translate(vector);
		refreshBounds();
		}
					/**------------------------------------------------------
	 				 * Rotates this MiPart the given number of radians about
					 * the given point.
	 				 * @param center	the center of rotation
	 				 * @param radians	the angle to rotate
					 *------------------------------------------------------*/
	protected	void		doRotate(MiPoint center, double radians)
		{
		if (Utility.isZero(radians))
			return;

		invalidateArea();
		for (int i = 0; i < numberOfParts; ++i)
			{
			objects[i].rotate(center, radians);
			}
/* if rotatable?
		for (int i = 0; i < getNumberOfAttachments(); ++i)
			{
			getAttachment(i).rotate(center, radians);
			}
*/
		refreshBounds();
		invalidateLayout();
		}
					/**------------------------------------------------------
	 				 * Flips this MiPart about the axis specified by the given 
					 * number of radians about the given point.
	 				 * @param center	the center of flip
	 				 * @param radians	the angle of the axis of reflection
					 *------------------------------------------------------*/
	protected	void		doFlip(MiPoint center, double radians)
		{
		invalidateArea();
		for (int i = 0; i < numberOfParts; ++i)
			{
			objects[i].flip(center, radians);
			}
		refreshBounds();
		invalidateLayout();
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
		if ((super.pick(area)) 
			&& ((isPickableWhenTransparent()) 
			|| (getBackgroundColor() != Mi_TRANSPARENT_COLOR)))
			{
			return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Returns the top MiPart at the given area.
					 * @param area		the given area
					 * @return 		the top part or null, if none
					 * @overrides		MiPart#pickObject
					 *------------------------------------------------------*/
	public		MiPart		pickObject(MiBounds area)
		{
		if ((!isVisible()) || (!isPickable()))
			return(null);

		if (numberOfParts == 0)
			return(super.pickObject(area));

		for (int i = numberOfParts - 1; i >= 0; --i)
			{
			if (objects[i].pick(area))
				return(objects[i]);
			}
		return(super.pickObject(area));
		}

					/**------------------------------------------------------
	 				 * Gets the list of parts, not including this MiPart, whose 
					 * bounds intersects the given area. The list is sorted 
					 * from topmost to bottommost part.
	 				 * @param area	 	the area
	 				 * @param list		the (returned) list of parts
					 * @return 		the list, possibly empty, of parts
					 * @overrides		MiPart#pickDeepListContents
					 *------------------------------------------------------*/
	public		void		pickDeepListContents(MiBounds area, MiParts list)
		{
		if (numberOfParts != 0)
			{
			MiBounds tmpBounds = MiBounds.newBounds();
			for (int i = numberOfParts - 1; i >= 0; --i)
				{
				MiPart obj = objects[i];
				if ((obj.getDrawBounds(tmpBounds).intersects(area))
					&& (obj.isVisible()) && (obj.isPickable()))
					{
					obj.pickDeepList(area, list);
					}
				}
			MiBounds.freeBounds(tmpBounds);
			}
		}
	//***************************************************************************************
	// Container contents management
	//***************************************************************************************
					/**------------------------------------------------------
	 				 * Gets the number of parts this container has.
	 				 * @return  		the number of parts
					 * @overrides		MiPart#getNumberOfParts
					 *------------------------------------------------------*/
	public		int		getNumberOfParts()
		{
		return(numberOfParts);
		}
					/**------------------------------------------------------
	 				 * Gets the part of this container at the given index.
	 				 * @param index 	the index of the part to get
	 				 * @return  		the part at the index
					 * @overrides		MiPart#getPart
					 *------------------------------------------------------*/
	public		MiPart		getPart(int index)
		{
		if (index >= numberOfParts)
			{
	    		throw new ArrayIndexOutOfBoundsException(MiDebug.getMicaClassName(this) 
				+ ": " + index + " >= " + numberOfParts);
			}
		return(objects[index]);
		}
					/**------------------------------------------------------
	 				 * Gets the part of this container with the given name.
	 				 * @param name	 	the name of the part to get
	 				 * @return  		the part or null
					 * @overrides		MiPart#getPart
					 *------------------------------------------------------*/
	public		MiPart		getPart(String name)
		{
		for (int i = 0; i < numberOfParts; ++i)
			{
			MiPart obj = objects[i];
			String n = obj.getName();
			if ((n != null) && (n.equals(name)))
				return(obj);
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Appends the given part to this container.
	 				 * @param part 		the part to append
					 * @overrides		MiPart#appendPart
	 				 * @exception		IllegalArgumentException if append
					 *			a part to itself
					 *------------------------------------------------------*/
	public	void			appendPart(MiPart part)
		{
		if (part == this)
			throw new IllegalArgumentException(this + ": Appending part to itself");
		if (containsPart(part))
			throw new IllegalArgumentException(this + ": Appending part twice to this container\n" + "Part = " + part);

		addElement(part);
		part.appendContainer(this);
		if (part.isVisible())
			{
			MiBounds tmpBounds = MiBounds.newBounds();
			if ((getLayout() == null) || (!getLayout().determinesPreferredAndMinimumSizes()))
				unionBounds(part.getBounds(tmpBounds));
			invalidateContainerArea(part.getDrawBounds(tmpBounds));
			if (part.containsOpaqueRectangles())
				MiDrawManager.invalidateBackToFront(part, part.getDrawBounds(tmpBounds));
			if (part.getInvalidLayoutNotificationsEnabled())
				invalidateLayout();
			MiBounds.freeBounds(tmpBounds);
			}
		if (!part.hasValidCachedEventHandlerInfo())
			part.validateCachedEventHandlerInfo();
		if ((!hasEventHandlersFlag) && (part.hasEventHandlers()))
			invalidateCachedEventHandlerInfo();
		if (part.hasEventHandlers())
			handlePartEventHandlerRegistration(part, true);
		if (part.containsOpaqueRectangles() || part.isOpaqueRectangle())
			setContainsOpaqueRectangles(true);
		part.setEventHandlingDisabledByContainer(getEventHandlingDisabledByContainer());
		part.setSensitivityDisabledByContainer(!isSensitive());
		part.dispatchAction(Mi_ADD_TO_CONTAINER_ACTION, this);
		dispatchAction(Mi_ITEM_ADDED_ACTION, part);
		}
					/**------------------------------------------------------
	 				 * Inserts the given part at the given index into this
					 * container.
	 				 * @param part 		the part to insert
	 				 * @param index 	where to insert the part
					 * @overrides		MiPart#insertPart
	 				 * @exception		IllegalArgumentException if add
					 *			a part to itself
					 *------------------------------------------------------*/
	public	void			insertPart(MiPart part, int index)
		{
		if (part == this)
			throw new IllegalArgumentException(this + ": Adding part to itself");
		if (containsPart(part))
			throw new IllegalArgumentException(this + ": Appending part twice to this container\n" + "Part = " + part);
		insertElementAt(part, index);
		part.appendContainer(this);
		if (part.isVisible())
			{
			MiBounds tmpBounds = MiBounds.newBounds();
			if ((getLayout() == null) || (!getLayout().determinesPreferredAndMinimumSizes()))
				unionBounds(part.getBounds(tmpBounds));
			invalidateContainerArea(part.getDrawBounds(tmpBounds));
			if (part.containsOpaqueRectangles())
				MiDrawManager.invalidateBackToFront(part, part.getDrawBounds(tmpBounds));
			if (part.getInvalidLayoutNotificationsEnabled())
				invalidateLayout();
			MiBounds.freeBounds(tmpBounds);
			}
		if (!part.hasValidCachedEventHandlerInfo())
			part.validateCachedEventHandlerInfo();
		if ((!hasEventHandlersFlag) && (part.hasEventHandlers()))
			invalidateCachedEventHandlerInfo();
		if (part.hasEventHandlers())
			handlePartEventHandlerRegistration(part, true);
		if (part.containsOpaqueRectangles() || part.isOpaqueRectangle())
			setContainsOpaqueRectangles(true);
		part.setEventHandlingDisabledByContainer(getEventHandlingDisabledByContainer());
		part.setSensitivityDisabledByContainer(!isSensitive());
		part.dispatchAction(Mi_ADD_TO_CONTAINER_ACTION, this);
		dispatchAction(Mi_ITEM_ADDED_ACTION, part);
		}
					/**------------------------------------------------------
	 				 * Replaces the part at the given index with the given 
					 * part.
	 				 * @param part 		the part to insert
	 				 * @param index 	where to place the part
					 * @overrides		MiPart#setPart
					 *------------------------------------------------------*/
	public	void			setPart(MiPart part, int index)
		{
		removePart(index);
		insertPart(part, index);
		updateContainsOpaqueRectangles();
		part.dispatchAction(Mi_ADD_TO_CONTAINER_ACTION, this);
		dispatchAction(Mi_ITEM_ADDED_ACTION, part);
		}
					/**------------------------------------------------------
	 				 * Removes the part from this container at the given index.
	 				 * @param index 	index of the part to remove.
					 * @overrides		MiPart#removePart
					 *------------------------------------------------------*/
	public	void			removePart(int index)
		{
		MiPart part = objects[index];
		if (part.isVisible())
			{
			MiBounds tmpBounds = MiBounds.newBounds();
			invalidateContainerArea(part.getDrawBounds(tmpBounds));
			if (part.getInvalidLayoutNotificationsEnabled())
				invalidateLayout();
			MiBounds.freeBounds(tmpBounds);
			}
		part.removeContainer(this);
		removeElementAt(index);
		refreshBounds();
		if (!part.hasValidCachedEventHandlerInfo())
			part.validateCachedEventHandlerInfo();
		if ((hasEventHandlersFlag) && (part.hasEventHandlers()))
			invalidateCachedEventHandlerInfo();
		if (part.hasEventHandlers())
			handlePartEventHandlerRegistration(part, false);
		updateContainsOpaqueRectangles();
		part.dispatchAction(Mi_REMOVE_FROM_CONTAINER_ACTION, this);
		dispatchAction(Mi_ITEM_REMOVED_ACTION, part);
		}
					/**------------------------------------------------------
	 				 * Removes the given part from this container.
	 				 * @param part 		the part to remove.
					 * @overrides		MiPart#removePart
					 *------------------------------------------------------*/
	public	void			removePart(MiPart part)
		{
		int index = indexOf(part);
		if (index == -1)
			{
			throw new IllegalArgumentException(
				this + ".removePart: object not found in this container; object = " + part);
			}
		removePart(index);
		}
					/**------------------------------------------------------
	 				 * Gets the index of the given part in this container.
	 				 * @param part 		the part to get the index of.
					 * @return		the index of the part or -1 if no
					 *			such part exists in this container
					 * @overrides		MiPart#getIndexOfPart
					 *------------------------------------------------------*/
	public	int			getIndexOfPart(MiPart obj)
		{
		return(indexOf(obj));
		}
					/**------------------------------------------------------
	 				 * Removes all parts from this container.
					 * @overrides		MiPart#removeAllParts
					 *------------------------------------------------------*/
	public	void			removeAllParts()
		{
		invalidateArea();
		invalidateLayout();
		while (getNumberOfParts() > 0)
			{
			MiPart part = objects[0];
			part.removeContainer(this);
			removeElementAt(0);
			if (!part.hasValidCachedEventHandlerInfo())
				part.validateCachedEventHandlerInfo();
			if (part.hasEventHandlers())
				handlePartEventHandlerRegistration(part, false);
			part.dispatchAction(Mi_REMOVE_FROM_CONTAINER_ACTION, this);
			dispatchAction(Mi_ITEM_REMOVED_ACTION, part);
			}
		refreshBounds();
		if (hasEventHandlersFlag)
			invalidateCachedEventHandlerInfo();
		updateContainsOpaqueRectangles();
		handleAllPartsEventHandlerRegistration(this, false);
		setContainsOpaqueRectangles(false);
		}
					/**------------------------------------------------------
	 				 * Makes and returns a copy of this container and all of 
					 * it's parts.
	 				 * @return 	 	the copy
					 * @see			MiPart#copy
					 * @overrides		MiPart#deepCopy
					 *------------------------------------------------------*/
	public		MiPart		deepCopy()
		{
		MiPart[] theCopy = new MiPart[1];
		dispatchAction(Mi_COPY_ACTION | Mi_EXECUTE_ACTION_PHASE, theCopy);

		MiPart obj = theCopy[0];
		if (obj != null)
			return(obj);

		obj = copy();
		MiPart[] newParts = new MiPart[numberOfParts];
		for (int i = 0; i < numberOfParts; ++i)
			{
			MiPart part = getPart(i);
			if ((!(part instanceof MiConnection)) && (part.isCopyableAsPartOfCopyable()))
				{
				MiPart newPart = part.deepCopy();
				newParts[i] = newPart;
				obj.appendPart(newPart);
				}
			}
		for (int i = 0; i < numberOfParts; ++i)
			{
			MiPart part = getPart(i);
			if ((part instanceof MiConnection) && (part.isCopyableAsPartOfCopyable()))
				{
				MiConnection conn = (MiConnection )getPart(i);
				int srcIndex = getIndexOfPart(conn.getSource());
				int destIndex = getIndexOfPart(conn.getDestination());
				if ((srcIndex != -1) && (destIndex != -1))
					{
					MiPart src = newParts[getIndexOfPart(conn.getSource())];
					MiPart dest = newParts[getIndexOfPart(conn.getDestination())];
					conn = (MiConnection )conn.deepCopy();
					conn.setSource(src);
					conn.setDestination(dest);
					obj.appendPart(conn);
					}
				}
			}
		return(obj);
		}
					/**------------------------------------------------------
	 				 * Gets whether the given part is in this container.
	 				 * @param part 		the part to check for
					 * @return		true if this container contains
					 *			the part
					 * @overrides		MiPart#containsPart
					 *------------------------------------------------------*/
	public		boolean		containsPart(MiPart part)	 	
		{ 
		return(getIndexOfPart(part) != -1);
		}
					/**------------------------------------------------------
	 				 * Gets whether the given part is in this container or in
					 * one of the parts of this container.
	 				 * @param part 		the part to check for
					 * @return		true if this container or one of
					 *			it's parts contains the part
					 * @overrides		MiPart#isContainerOf
					 *------------------------------------------------------*/
	public		boolean		isContainerOf(MiPart part)
		{
		if (containsPart(part))
			return(true);
		for (int i = 0; i < numberOfParts; ++i)
			{
			if (getPart(i).isContainerOf(part))
				return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Gets the part with the given name in this container or in
					 * one of the parts of this container.
	 				 * @param name 		the name of the part to look for
					 * @return		the first part found with the given
					 *			name or null
					 * @overrides		MiPart#isContainerOf
					 *------------------------------------------------------*/
	public		MiPart		isContainerOf(String name)
		{
		MiPart part = getPart(name);
		if (part != null)
			return(part);

		for (int i = 0; i < numberOfParts; ++i)
			{
			if ((part = getPart(i).isContainerOf(name)) != null)
				return(part);
			}
		return(null);
		}

	public		MiPart		isContainerOfWithAttachments(String name)
		{
		MiPart part = getPart(name);
		if (part != null)
			{
			return(part);
			}

		part = getAttachments() != null ? getAttachments().getPart(name) : null;
		if (part != null)
			{
			return(part);
			}

		for (int i = 0; i < numberOfParts; ++i)
			{
			if ((part = getPart(i).isContainerOf(name)) != null)
				{
				return(part);
				}
			if (getPart(i).getAttachments() != null)
				{
				part = getPart(i).getAttachments().isContainerOf(name);
				if (part != null)
					{
					return(part);
					}
				}
			}
		return(null);
		}

	//***************************************************************************************
	// EventHandler Management
	//***************************************************************************************
					/**------------------------------------------------------
					 * Gets whether this MiPart or any of it's attachments
					 * or parts has event handlers assigned.
					 * @return 		true if event handlers were assigned
					 * @overrides		MiPart#hasEventHandlers
					 *------------------------------------------------------*/
	public		boolean		hasEventHandlers()
		{
		return(hasEventHandlersFlag);
		}
					/**------------------------------------------------------
					 * Validates (recalculates) information about the event
					 * handlers assigned to this MiPart and it's parts. This
					 * information is used to speed up event handling by
					 * reducing the number of parts an event has to traverse.
					 * @overrides		MiPart#validateCachedEventHandlerInfo
					 *------------------------------------------------------*/
	protected	void		validateCachedEventHandlerInfo()
		{
		hasValidCachedEventHandlerInfo = true;
		hasEventHandlersFlag = false;
		if (super.hasEventHandlers())
			{
			hasEventHandlersFlag = true;
			}
		for (int i = 0; i < numberOfParts; ++i)
			{
			if (!getPart(i).hasValidCachedEventHandlerInfo())
				getPart(i).validateCachedEventHandlerInfo();
			if ((!hasEventHandlersFlag) && (getPart(i).hasEventHandlers()))
				{
				hasEventHandlersFlag = true;
				}
			}
		}
					/**------------------------------------------------------
					 * Gets whether the cached information about the event
					 * handlers assigned to this MiPart and it's parts is valid.
					 * @return		true if info is valid
					 * @overrides		MiPart#hasValidCachedEventHandlerInfo
					 *------------------------------------------------------*/
	public		boolean		hasValidCachedEventHandlerInfo()
		{
		return(hasValidCachedEventHandlerInfo);
		}
					/**------------------------------------------------------
					 * Invalidates the cached information about the event
					 * handlers assigned to this MiPart and it's containers.
					 * @overrides		MiPart#invalidateCachedEventHandlerInfo
					 *------------------------------------------------------*/
	public		void		invalidateCachedEventHandlerInfo()
		{
		hasValidCachedEventHandlerInfo = false;
		super.invalidateCachedEventHandlerInfo();
		}

					/**------------------------------------------------------
					 * Handles event handler registeration for all of the
					 * event handlers of the given subject and all of it's
					 * parts.
					 * @param subject	the added or removed part
					 * @param addingSubject	true if adding the part to this
					 *			container
					 *------------------------------------------------------*/
	protected	void		handlePartEventHandlerRegistration(MiPart subject, boolean addingSubject)
		{
		for (int i = 0; i < subject.getNumberOfEventHandlers(); ++i)
			{
			handleEventHandlerRegistration(subject.getEventHandler(i), addingSubject);
			}
		handleAllPartsEventHandlerRegistration(subject, addingSubject);
		}
					/**------------------------------------------------------
					 * Handles event handler registeration for all of the
					 * event handlers of the parts of the given subject.
					 * @param subject	the added or removed part
					 * @param addingSubject	true if adding the part to this
					 *			container
					 *------------------------------------------------------*/
	protected	void		handleAllPartsEventHandlerRegistration(MiPart subject, boolean addingSubject)
		{
		for (int i = 0; i < subject.getNumberOfParts(); ++i)
			{
			if (subject.getPart(i).hasEventHandlers())
				{
				handlePartEventHandlerRegistration(subject.getPart(i), addingSubject);
				}
			}
		}

	//***************************************************************************************
	// Draw management
	//***************************************************************************************
					/**------------------------------------------------------
	 				 * Renders this container (i.e. all of it's parts).
	 				 * @param renderer 	the renderer to use for drawing
					 * @overrides		MiPart#render
					 *------------------------------------------------------*/
	protected 	void		render(MiRenderer renderer)
		{
		for (int i = 0; i < numberOfParts; ++i)
			{
			objects[i].draw(renderer);
			}
		}
	//***************************************************************************************
	// Bounds management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the minimum size of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
					 * The default behavior is to:
					 *   return the minimum size as determined by the layout
					 *   assigned to this container
					 * or
					 *   if only one part in this container, return it's
					 *   minimum size
					 * or
					 *   if no parts are in this container, return what the
					 *   super class returns
					 * or
					 *   return the union of all minimum sizes of all parts
					 *   of this container
					 * 
	 				 * @param size		the (returned) minimum size
					 * @overrides		MiPart#calcMinimumSize
					 * @see			MiPart#getMinimumSize
					 *------------------------------------------------------*/
	protected	void		calcMinimumSize(MiSize size)
		{
		if ((getLayout() != null) && (getLayout().determinesPreferredAndMinimumSizes()))
			{
			getLayout().getMinimumSize(size);
			}
		else if (getNumberOfParts() == 1)
			{
			getPart(0).getMinimumSize(size);
			}
		else if (getNumberOfParts() > 0)
			{
			MiSize tmp = MiSize.newSize();
			size.zeroOut();
			for (int i = 0; i < getNumberOfParts(); ++i)
				{
				if (getPart(i).isVisible())
					{
					getPart(i).getMinimumSize(tmp);
					size.union(tmp);
					}
				}
			MiSize.freeSize(tmp);
			}
		else
			{
			super.calcMinimumSize(size);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the preferred size of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
					 * The default behavior is to:
					 *   return the preferred size as determined by the layout
					 *   assigned to this container
					 * or
					 *   if only one part in this container, return it's
					 *   preferred size
					 * or
					 *   if no parts are in this container, return the current
					 *   size of this container (or zero if this has reversed
					 *   bounds). 
					 * or
					 *   return the union of all preferred sizes of all parts
					 *   of this container at their present location
					 * 
	 				 * @param size		the (returned) preferred size
					 * @overrides		MiPart#calcPreferredSize
					 * @see			MiPart#getPreferredSize
					 *------------------------------------------------------*/
	protected	void		calcPreferredSize(MiSize size)
		{
		if ((getLayout() != null) && (getLayout().determinesPreferredAndMinimumSizes()))
			{
			getLayout().getPreferredSize(size);
			}
		else if (getNumberOfParts() == 1)
			{
			getPart(0).getPreferredSize(size);
			}
		else if (getNumberOfParts() > 0)
			{
			MiBounds tmpBounds = MiBounds.newBounds();
			MiBounds tmpBounds1 = MiBounds.newBounds();
			MiSize tmpSize = MiSize.newSize();
			for (int i = 0; i < getNumberOfParts(); ++i)
				{
				if (getPart(i).isVisible())
					{
					getPart(i).getPreferredSize(tmpSize);
					tmpBounds.setCenterX(getPart(i).getCenterX());
					tmpBounds.setCenterY(getPart(i).getCenterY());
					tmpBounds.setSize(tmpSize);
					tmpBounds1.union(tmpBounds);
					}
				}
			size.setSize(tmpBounds1);
			MiBounds.freeBounds(tmpBounds);
			MiBounds.freeBounds(tmpBounds1);
			MiSize.freeSize(tmpSize);
			}
		else
			{
			MiBounds tmpBounds = MiBounds.newBounds();
			MiBounds b = getBounds(tmpBounds);
			if (b.isReversed()) 
				{
				size.zeroOut();
				}
			else
				{
				size.setWidth(b.getWidth());
				size.setHeight(b.getHeight());
				}
			MiBounds.freeBounds(tmpBounds);
			}
		}
					/**------------------------------------------------------
					 * Implementation of the method that recalculates the draw 
					 * bounds of this MiPart. 
					 * This routine starts with the draw bounds as calculated
					 * by the super class MiPart. Then is unions this with all
					 * of the draw bounds of all of it's parts. This is to
					 * account, for example, for cases like a part having a
					 * shadow that extends outside this container's bounds.
					 * @param db 		the (returned) draw bounds
					 * @overrides		MiPart#reCalcDrawBounds
					 *------------------------------------------------------*/
	protected	void		reCalcDrawBounds(MiBounds db)
		{
		super.reCalcDrawBounds(db);
		// Drag-and-drop selected parts did not draw selection border
		// if ((this instanceof MiReference) || (this instanceof MiEditor))
		if (this instanceof MiEditor)
			return;

		MiBounds tmpBounds = MiBounds.newBounds();
		for (int i = 0; i < numberOfParts; ++i)
			{
			MiPart obj = objects[i];
			if (obj.isVisible())
				{
				obj.getDrawBounds(tmpBounds);
				db.union(tmpBounds);
				}
			}
		MiBounds.freeBounds(tmpBounds);
		}
					/**------------------------------------------------------
					 * Realculates the outer bounds of this MiPart. Override 
					 * this, if desired, as it implements the core 
					 * functionality. This implementation just returns the
					 * union of the outer bounds of all of the parts in this
					 * container.
					 * @param newBounds	the (returned) outer bounds
					 * @overrides		MiPart#reCalcBounds
					 *------------------------------------------------------*/
	protected 	void		reCalcBounds(MiBounds newBounds)
		{
		newBounds.reverse();
		boolean fixedWidth = hasFixedWidth();
		boolean fixedHeight = hasFixedHeight();
		if (fixedWidth && fixedHeight)
			{
			getBounds(newBounds);
			return;
			}

		MiBounds tmpBounds = MiBounds.newBounds();
		for (int i = 0; i < numberOfParts; ++i)
			{
			MiPart obj = objects[i];
			if (obj.isVisible())
				{
				obj.getBounds(tmpBounds);
				newBounds.union(tmpBounds);
				}
			}
		if (fixedWidth)
			{
			newBounds.xmin = getXmin();
			newBounds.xmax = getXmax();
			}
		else if (fixedHeight)
			{
			newBounds.ymin = getYmin();
			newBounds.ymax = getYmax();
			}
		MiBounds.freeBounds(tmpBounds);
		}
					/**------------------------------------------------------
					 * Gets whether if there is an intersection between the
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
					 * @overrides 		MiPart#getIntersectionWithLine
					 *------------------------------------------------------*/
	public		boolean		getIntersectionWithLine(
						MiPoint insidePoint, 
						MiPoint otherPoint, 
						MiPoint returnedIntersectionPoint)
		{
		// Check to see if this container has a background (only decendants of
		// MiVisibleContainer do...now. NEEDS WORK: need 'hasPartiallyOpaqueBackground()')
		if (!(this instanceof MiVisibleContainer))
			{
			// -----------------------------------------------------
			// For all of the parts in this container, find where
			// the given line intersects their bounds.
			// -----------------------------------------------------

			// Make sure that parts are correctly placed...
			if (!hasValidLayout())
				validateLayout();

			MiPoint intersectionPt = new MiPoint();
			double closestDist = Mi_MAX_DISTANCE_VALUE;
			for (int i = 0; i < numberOfParts; ++i)
				{
				MiPart part = getPart(i);
				if (part.isVisible()) 
					{
					if (part.getIntersectionWithLine(
						insidePoint, otherPoint, intersectionPt))
						{
						double dist = intersectionPt.getDistanceSquared(otherPoint);
						if (dist < closestDist)
							{
							closestDist = dist;
							returnedIntersectionPoint.copy(intersectionPt);
							}
						}
					}
				}
			if (closestDist < Mi_MAX_DISTANCE_VALUE)
				return(true);
			}
		return(super.getIntersectionWithLine(insidePoint, otherPoint, returnedIntersectionPoint));
		}

	//***************************************************************************************
	// Private methods
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Invalidate the given area of this container, taking
					 * into account any transform this container may have.
	 				 * @param area 		the area to invalidate
					 *------------------------------------------------------*/
	private		void		invalidateContainerArea(MiBounds area)
		{
		if (getTransform() != null)
			getTransform().wtod(area, area);
		invalidateArea(area);
		}
	//***************************************************************************************
	// DisplayList management
	// This is a optimized implementation of methods to manipulate the array of MiParts
	// that this container uses. The API is very similar to the API for java.util.Vector.
	//***************************************************************************************
					/**------------------------------------------------------
	 				 * Remove the part at the given index, starting at zero.
	 				 * @param index 	the index of the part to remove
					 *------------------------------------------------------*/
	private		void		removeElementAt(int index)
		{
		System.arraycopy(objects, index + 1, objects, index, numberOfParts - index - 1);
		numberOfParts--;
		objects[numberOfParts] = null;
		}
					/**------------------------------------------------------
	 				 * Remove all parts.
					 *------------------------------------------------------*/
	private		void		removeAllElements()
		{
		for (int i = 0; i < numberOfParts; i++)
			objects[i] = null;
		numberOfParts = 0;
		}
					/**------------------------------------------------------
	 				 * Remove the given part.
	 				 * @param part 		the part to remove
					 * @return		true if the element was removed
					 *------------------------------------------------------*/
	private		boolean		removeElement(MiPart part)
		{
		int i = indexOf(part);
		if (i >= 0)
			{
			removeElementAt(i);
			return(true);
			}
		return(false);
		}
					/**------------------------------------------------------
	 				 * Get the index of the given part.
	 				 * @param part 		the part whose index we want
					 * @return		the index of the part or -1, if
					 *			not found.
					 *------------------------------------------------------*/
	private		int		indexOf(MiPart part)
		{
		for (int i = 0; i < numberOfParts; ++i)
			{
			if (objects[i] == part)
				{
				return(i);
				}
			}
		return(-1);
		}
					/**------------------------------------------------------
	 				 * Add (append) the part. If we are keeping connections
					 * before nodes, then this is enforced here.
	 				 * @param part 		the part to add
					 *------------------------------------------------------*/
	private		void		addElement(MiPart part)
		{
		if ((keepConnectionsBelowNodes) && (part instanceof MiConnection))
			{
			for (int i = 0; i < numberOfParts; ++i)
				{
				if (!(objects[i] instanceof MiConnection))
					{
					reallyInsertElementAt(part, i);
					return;
					}
				}
			}
		reallyAddElement(part);
		}
					/**------------------------------------------------------
	 				 * Insert the part at the given index. If we are keeping
					 * connections before nodes, then this is enforced here.
	 				 * @param part 		the part to insert
	 				 * @param index		the index of the insertion point
					 *------------------------------------------------------*/
	private		void		insertElementAt(MiPart part, int index)
		{
		if ((keepConnectionsBelowNodes) && (!(part instanceof MiConnection)))
			{
			for (int i = index; i < numberOfParts; ++i)
				{
				if (!(objects[i] instanceof MiConnection))
					{
					reallyInsertElementAt(part, i);
					return;
					}
				}
			reallyAddElement(part);
			}
		else
			{
			reallyInsertElementAt(part, index);
			}
		}
					/**------------------------------------------------------
	 				 * This routine is used by addElement and insertElement
					 * to actually add the parts to the array.
	 				 * @param part 		the part to add
					 *------------------------------------------------------*/
	private		void		reallyAddElement(MiPart part)
		{
//if ((part.getName() != null) && (part.getName().startsWith("core/resistor")))
//MiDebug.printStackTrace("insert part: " +part);
		if (numberOfParts == objects.length)
			{
			int newSize = (capacityIncrement == 0) ?
				objects.length * 2 : objects.length + capacityIncrement;
			MiPart[] newObjects = new MiPart[newSize];
			System.arraycopy(objects, 0, newObjects, 0, numberOfParts);
			objects = newObjects;
			}
		objects[numberOfParts++] = part;
		}
					/**------------------------------------------------------
	 				 * This routine is used by addElement and insertElement
					 * to actually insert the parts into the array.
	 				 * @param part 		the part to add
	 				 * @param index 	the index of the insertion point
					 *------------------------------------------------------*/
	private		void		reallyInsertElementAt(MiPart part, int index)
		{
		if (numberOfParts == objects.length)
			{
			int newSize = (capacityIncrement == 0) ?
				objects.length * 2 : objects.length + capacityIncrement;
			MiPart[] newObjects = new MiPart[newSize];
			System.arraycopy(objects, 0, newObjects, 0, numberOfParts);
			objects = newObjects;
			}
		if (index < objects.length - 1)
			System.arraycopy(objects, index, objects, index + 1, numberOfParts - index);
		objects[index] = part;
		numberOfParts++;
		}
	}


