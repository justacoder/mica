
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
import com.swfm.mica.util.FastVector;

/**----------------------------------------------------------------------------------------------
 * Note, this clased sparingly, as the master MiPart's should 
 * API should be used to add and remove things from an
 * MiAttachments. 
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiAttachments extends MiContainer 
	{
	private		MiPart		attachedToObject;
	private		MiBounds	tmpBounds	= new MiBounds();
	private		FastVector	attachmentSpecs = new FastVector();




	public				MiAttachments()
		{
		setSelectable(false);
		}
	public				MiAttachments(MiPart obj)
		{
		attachedToObject = obj;
		setSelectable(false);
		}
	public		void		setAttachedToObject(MiPart obj)
		{
		attachedToObject = obj;
		invalidateLayout();
		}
	public		MiPart		getContainer(int index)
		{
		return(attachedToObject);
		}
	public		int		getNumberOfContainers()
		{
		if (attachedToObject != null)
			return(1);
		return(0);
		}

	public		void		appendAttachment(
						MiPart obj, 
						int location, 
						String tag, 
						MiMargins margins)
		{
		if ((obj.getTransform() != null) && (location != Mi_NONE))
			{
			throw new IllegalArgumentException(this 
			+ ": Attachments with locations cannot be made to parts which have transforms.\n" +
			  "  Try setting location to Mi_NONE and doing location management programatically.");
			}
			
			
		attachmentSpecs.addElement(new MiAttachment(obj, location, tag, margins));

		if (location != Mi_NONE)
			{
			((MiAttachment )attachmentSpecs.lastElement()).positionAttachment(
				attachedToObject, attachedToObject.getBounds(tmpBounds));
			}

		appendPart(obj);
		}
	public		void		removeAllAttachments()
		{
		while (attachmentSpecs.size() > 0)
			{
			attachmentSpecs.removeElementAt(0);
			removePart(0);
			}
		}
	public		void		removeAttachment(MiPart obj)
		{
//MiDebug.printStackTrace(this + "removeAttachment: " + obj);
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			if (((MiAttachment )attachmentSpecs.elementAt(i)).getPart() == obj)
				{
				attachmentSpecs.removeElementAt(i);
				removePart(i);
				return;
				}
			}
		throw new IllegalArgumentException(
			this + ".removeAttachment: object not found in this container; object = " + obj);
		}
	public		boolean		hasAttachment(MiPart obj)
		{
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			if (((MiAttachment )attachmentSpecs.elementAt(i)).getPart() == obj)
				{
				return(true);
				}
			}
		return(false);
		}
	public		boolean		setAttachmentTag(MiPart obj, String tag)
		{
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			if (((MiAttachment )attachmentSpecs.elementAt(i)).getPart() == obj)
				{
				((MiAttachment )attachmentSpecs.elementAt(i)).setTag(tag);
				return(true);
				}
			}
		return(false);
		}
	public		boolean		isBeforePart(MiPart obj)
		{
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			if (((MiAttachment )attachmentSpecs.elementAt(i)).getPart() == obj)
				{
				return(!((MiAttachment )attachmentSpecs.elementAt(i)).drawAfter);
				}
			}
		return(false);
		}
	public	void			removeAttachment(String tag)
		{
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			if (((MiAttachment )attachmentSpecs.elementAt(i)).hasTag(tag))
				{
				MiPart obj = getPart(i);
				attachmentSpecs.removeElementAt(i);
				removePart(i);
				return;
				}
			}
		throw new IllegalArgumentException(
			this + ".removeAttachment: object with given tag not found in this container; tag = " 
			+ tag);
		}

	public	MiPart			getAttachment(String tag)
		{
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			if (((MiAttachment )attachmentSpecs.elementAt(i)).hasTag(tag))
				{
				return(getPart(i));
				}
			}
		return(null);
		}
	public		MiMargins	getAttachmentMargins(String tag)
		{
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			if (((MiAttachment )attachmentSpecs.elementAt(i)).hasTag(tag))
				{
				return(((MiAttachment )attachmentSpecs.elementAt(i)).getMargins());
				}
			}
		return(null);
		}
	public		void		setAttachmentMargins(String tag, MiMargins margins)
		{
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			MiAttachment a = (MiAttachment )attachmentSpecs.elementAt(i);
			if (a.hasTag(tag))
				{
				a.setMargins(margins);
				a.part.invalidateLayout();
				break;
				}
			}
		}
	public	MiPart			deepCopy()
		{
		MiAttachments attachments = (MiAttachments )super.deepCopy();
		int j = 0;
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			if (getPart(i).isCopyable())
				{
				attachments.attachmentSpecs.addElement(
					((MiAttachment )attachmentSpecs.elementAt(i)).copy(attachments.getPart(j++)));
				}
			}
		return(attachments);
		}

	public	void			pickDeepListBefore(MiBounds area, MiParts list)
		{
		for (int i = attachmentSpecs.size() - 1; i >= 0; --i)
			{
			MiAttachment attachment = (MiAttachment )attachmentSpecs.elementAt(i);
			if (!attachment.drawAfter)
				{
				attachment.part.pickDeepList(area, list);
				}
			}
		}
	public	void			pickDeepListAfter(MiBounds area, MiParts list)
		{
		for (int i = attachmentSpecs.size() - 1; i >= 0; --i)
			{
			MiAttachment attachment = (MiAttachment )attachmentSpecs.elementAt(i);
			if (attachment.drawAfter)
				{
				attachment.part.pickDeepList(area, list);
				}
			}
		}
	public		void		drawBefore(MiRenderer renderer)
		{
		drawAttachments(renderer, false);
		}
	public		void		drawAfter(MiRenderer renderer)
		{
		drawAttachments(renderer, true);
		}
	protected	void		drawAttachments(MiRenderer renderer, boolean drawAfter)
		{
		MiiTransform transform = attachedToObject.getTransform();
		if (drawAfter)
			{
			if ((transform != null) ) //&& (attachedToObject instanceof MiWindow))
				renderer.pushTransform(transform);
			}
		for (int i = 0; i < attachmentSpecs.size(); ++i)
			{
			MiAttachment attachment = (MiAttachment )attachmentSpecs.elementAt(i);
			if (attachment.drawAfter == drawAfter)
				{
				if (MiDebug.debug
					&& (MiDebug.isTracing(this, MiDebug.TRACE_DRAWING_OF_PARTS))
					|| (MiDebug.isTracing(this, MiDebug.TRACE_DRAWING_OF_ATTACHMENTS)))
					{
					MiDebug.println("About to draw attachment: " + attachment.part);
					if (!attachment.part.isVisible())
						MiDebug.println("NOT drawing because invisible");
					if (attachment.part.isHidden())
						MiDebug.println("NOT drawing because hidden");
					if (renderer.boundsClipped(attachment.part.getDrawBounds(tmpBounds)))
						{
						MiDebug.println("NOT drawing because clipped: drawBounds = " 
							+ tmpBounds + ", Clip bounds: " 
							+ renderer.getClipBounds());
						}
					}
				if ((attachment.part.isVisible()) && (!attachment.part.isHidden())
					&&(!renderer.boundsClipped(attachment.part.getDrawBounds(tmpBounds))))
					{
					attachment.part.draw(renderer);
					}
				}
			}
		if (drawAfter)
			{
			if ((transform != null) ) //&& (attachedToObject instanceof MiWindow))
				renderer.popTransform();
			}
		}
	public	void			invalidateArea()
		{
		if ((getInvalidAreaNotificationsEnabled()) && (attachedToObject != null))
			{
			getDrawBounds(tmpBounds);
			if (attachedToObject.getDrawManager() != null)
				{
				if (attachedToObject.getTransform() != null)
					attachedToObject.getTransform().wtod(tmpBounds, tmpBounds);
				// FIX: before do this check to see if this object is in the containing
				// editor. otherwise get those "Invalid area extends outside target draw bounds"
				// warning messages
				attachedToObject.getDrawManager().invalidateBackToFront(tmpBounds);
				}
			else
				{
				attachedToObject.invalidateArea(tmpBounds);
				}
			}
		}
	public	void			invalidateArea(MiBounds area)
		{
		if ((getInvalidAreaNotificationsEnabled()) && (attachedToObject != null))
			{
			if (attachedToObject.getDrawManager() != null)
				{
				tmpBounds.copy(area);

				if (attachedToObject.getTransform() != null)
					attachedToObject.getTransform().wtod(tmpBounds, tmpBounds);
				// FIX: before do this check to see if this object is in the containing
				// editor. otherwise get those "Invalid area extends outside target draw bounds"
				// warning messages
				attachedToObject.getDrawManager().invalidateBackToFront(tmpBounds);
				}
			else
				{
				attachedToObject.invalidateArea(area);
				}
			}
		}

	public	void			invalidateCachedEventHandlerInfo()
		{
		super.invalidateCachedEventHandlerInfo();
		// If we are not in a deepCopy operation, in which case we will be assigned to
		// an object soon...
		if (attachedToObject != null)
			attachedToObject.invalidateCachedEventHandlerInfo();
		}

	public	void			invalidateLayouts()
		{
		invalidateLayout(); 
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiAttachment attachment = (MiAttachment )attachmentSpecs.elementAt(i);
			if (attachment.location == Mi_NONE)
				attachment.part.invalidateLayout();
			}
		}
	public	void			invalidateLayout()
		{
		super.invalidateLayout(); 
		if (attachedToObject != null) // && (getInvalidLayoutNotificationsEnabled()))
			{
			MiWindow window = getContainingWindow();
			if (window != null)
				{
//??? can we add this for speed		if (!getValidatingLayout())
				window.invalidateEditorPartLayout(this);
				}
			}
		}

	protected	void		layoutParts()
		{
		if (attachedToObject != null)
			validateAttachmentsLayout();
		super.layoutParts();
		}
	protected	void		validateParts()
		{
		super.validateParts();
		// Don't want text cursor to invalidateWhole area of associated text
		boolean enabled = attachedToObject.getInvalidAreaNotificationsEnabled();
		attachedToObject.setInvalidAreaNotificationsEnabled(false);
		attachedToObject.reCalcDrawBounds();
		attachedToObject.setInvalidAreaNotificationsEnabled(enabled);
		getDrawBounds(tmpBounds);
		if (!tmpBounds.isReversed())
			invalidateArea(getDrawBounds(tmpBounds));
		}

	protected	void		validateAttachmentsLayout()
		{
		MiBounds b = attachedToObject.getBounds(tmpBounds);
		for (int i = 0; i < getNumberOfParts(); ++i)
			{
			MiAttachment attachment = (MiAttachment )attachmentSpecs.elementAt(i);
			if (attachment.location != Mi_NONE)
				{
				attachment.positionAttachment(attachedToObject, b);
				}
			else
				{
				// Notify the part that the part it's attached to has changed shape
				// in some way. If this is too expensive for some parts, then create
				// a specical location called Mi_CALCULATED_LOCATION for those that
				// need this like MiMultiPointShapes
				attachment.part.invalidateLayout();
				attachment.part.validateLayout();
				}
			}
		}
	public		String		toString()
		{
		return(super.toString() + (attachedToObject != null ? ("[Attached to: " + attachedToObject + "]") : ""));
		}
	}

class MiAttachment implements MiiTypes
	{
			boolean		drawAfter	= true;
			MiPart		part;
			int		location;
			String		tag;
	private  	MiMargins	margins;
	private static	MiPoint		tmpPoint	= new MiPoint();
	private static	MiBounds	tmpBounds	= new MiBounds();


					MiAttachment(MiPart obj, int location, String tag, MiMargins margins)
		{
		this.part = obj;
		this.location = location;
		this.tag = tag;
		setMargins(margins);
		}

	public		MiPart		getPart()
		{
		return(part);
		}

	public		void		setMargins(MiMargins m)
		{
		if (m == null)
			this.margins = new MiMargins(0);
		else
			this.margins = new MiMargins(m);
		}
	public		MiMargins	getMargins()
		{
		return(new MiMargins(margins));
		}

	public		MiAttachment	copy(MiPart newPart)
		{
		return(new MiAttachment(newPart, location, tag, margins));
		}

	public		void		setTag(String t)
		{
		tag = t;
		}

	public		boolean		hasTag(String t)
		{
		if ((tag != null) && (t.equalsIgnoreCase(tag)))
			{
			return(true);
			}
		return(false);
		}

					// const: objectBounds
	protected 	void		positionAttachment(MiPart attachedToObject, MiBounds attachedToObjectBounds)
		{
		if (!part.isVisible())
			return;

		if (attachedToObjectBounds.isReversed())
			return;

		if (!part.hasValidLayout())
			{
			MiSize tmpSize = MiSize.newSize();
			part.setSize(part.getPreferredSize(tmpSize));
			MiSize.freeSize(tmpSize);
			part.validateLayout();
			}
		switch(location)
			{
			case Mi_SURROUND_LOCATION :
				MiBounds b = MiBounds.newBounds();
				b.copy(attachedToObjectBounds);
				part.setBounds(b.addMargins(margins));
				MiBounds.freeBounds(b);
				break;

			case Mi_ALONG_LOCATION :
				part.setNumberOfPoints(attachedToObject.getNumberOfPoints());
				for (int i = 0; i < attachedToObject.getNumberOfPoints(); ++i)
					{
					attachedToObject.getPoint(i, tmpPoint);
					part.setPoint(i, tmpPoint);
					}
				break;

			default:
				if ((location < Mi_MIN_CUSTOM_LOCATION) || (location > Mi_MAX_CUSTOM_LOCATION))
					{
					attachedToObject.getRelativeLocation(
						location, part.getBounds(tmpBounds), tmpPoint, margins);
					part.setCenter(tmpPoint);
					}
				break;

			case Mi_NONE :
				break;
			}
		}
	}

