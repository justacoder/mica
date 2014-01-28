
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

/**----------------------------------------------------------------------------------------------
 * This class implements the MiiSelectionGraphics interface. As
 * such it is responsible for the making the appearance of a
 * graphics part when the part is 'selected' different from when
 * it is 'not selected'.
 * <p>
 * This class implements the MiiSelectionGraphics interface.
 * This implementation changes the attributes of the selected
 * parts. By default this sets the fore and background colors of
 * the selected part unless the part is text, in which case just 
 * the foreground color is changed. The default color is 'red'.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiChangeAttributesSelectionGraphics implements MiiSelectionGraphics, MiiTypes
	{
	private 	boolean		applyToSelectedItems	= true;
	private 	boolean		applyToAttachments	= true;
	private 	boolean		applyToSelectedItemsParts= false;
	private 	MiAttributes	attributes		= new MiAttributes();
	private 	MiAttributes	textAttributes		= new MiAttributes();
	private		Hashtable	origAttributes		= new Hashtable(11);



					/**------------------------------------------------------
	 				 * Constructs a new MiBoxSelectionGraphics. 
					 *------------------------------------------------------*/
	public				MiChangeAttributesSelectionGraphics()
		{
		attributes = attributes.setColor(MiColorManager.red);
		attributes = attributes.setBackgroundColor(MiColorManager.red);
		textAttributes = new MiAttributes().setColor(MiColorManager.red);
		}
					/**------------------------------------------------------
	 				 * Set the attributes used to apply to selected parts.
					 * @param attributes	the attributes 
					 *------------------------------------------------------*/
	public		void		setAttributes(MiAttributes attributes)
		{
		this.attributes = attributes;
		}
					/**------------------------------------------------------
	 				 * Get the attributes used to apply to selected parts.
					 * @return 		the attributes
					 *------------------------------------------------------*/
	public		MiAttributes	getAttributes()
		{
		return(attributes);
		}
					/**------------------------------------------------------
	 				 * Set the attributes used to apply to selected text items.
					 * @param attributes	the attributes 
					 *------------------------------------------------------*/
	public		void		setTextAttributes(MiAttributes attributes)
		{
		this.textAttributes = attributes;
		}
					/**------------------------------------------------------
	 				 * Get the attributes used to apply to selected text items
					 * @return 		the attributes 
					 *------------------------------------------------------*/
	public		MiAttributes	getTextAttributes()
		{
		return(textAttributes);
		}
					/**------------------------------------------------------
	 				 * Sets whether to apply the attributes to the actual 
					 * selected items. True by default.
					 * @param 		true if applied to items
					 *------------------------------------------------------*/
	public		void		setApplyToSelectedItems(boolean flag)
		{
		applyToSelectedItems = flag;
		}
					/**------------------------------------------------------
	 				 * Get whether to apply the attributes to the actual
					 * selected items.
					 * @return 		true if applied to items
					 *------------------------------------------------------*/
	public		boolean		getApplyToSelectedItems()
		{
		return(applyToSelectedItems);
		}
					/**------------------------------------------------------
	 				 * Sets whether to apply the attributes to attachments of 
					 * selected items. True by default.
					 * @param 		true if applied to attachments
					 *------------------------------------------------------*/
	public		void		setApplyToAttachments(boolean flag)
		{
		applyToAttachments = flag;
		}
					/**------------------------------------------------------
	 				 * Get whether to apply the attributes to attachments of 
					 * selected items.
					 * @return 		true if applied to attachments
					 *------------------------------------------------------*/
	public		boolean		getApplyToAttachments()
		{
		return(applyToAttachments);
		}
					/**------------------------------------------------------
	 				 * Sets whether to apply the attributes to parts of 
					 * selected items. False by default.
					 * @param 		true if applied to parts
					 *------------------------------------------------------*/
	public		void		setApplyToSelectedItemsParts(boolean flag)
		{
		applyToSelectedItemsParts = flag;
		}
					/**------------------------------------------------------
	 				 * Get whether to apply the attributes to parts of 
					 * selected items.
					 * @return 		true if applied to parts
					 *------------------------------------------------------*/
	public		boolean		getApplyToSelectedItemsParts()
		{
		return(applyToSelectedItemsParts);
		}
					/**------------------------------------------------------
	 				 * This method tells this selection graphics appearance
					 * manager whether the given part in the given editor is
					 * selected. This implementation draws a rectangle around
					 * the selected part unless the part is a line, in which
					 * case the line is rendered with a change of the line
					 * borderLook attribute.
	 			 	 * @param editor 	the editor
	 				 * @param part 		the part whose selection state
					 *			has changed.
	 				 * @param flag 		true if the part is now selected.
	 				 * @implements 		MiiSelectionGraphics
					 *------------------------------------------------------*/
	public		void		select(MiEditor editor, MiPart obj, boolean flag)
		{
		select(editor, obj, flag, false);
		}
	public		void		select(MiEditor editor, MiPart obj, boolean flag, boolean inAttachment)
		{
		doSelect(editor, obj, flag);

		if ((applyToSelectedItemsParts) || (inAttachment))
			{
			for (int i = 0; i < obj.getNumberOfParts(); ++i)
				{
				select(editor, obj.getPart(i), flag, inAttachment);
				}
			}

		if (applyToAttachments)
			{
			for (int i = 0; i < obj.getNumberOfAttachments(); ++i)
				{
				select(editor, obj.getAttachment(i), flag, true);
				}
			}
		}
	protected	void		doSelect(MiEditor editor, MiPart obj, boolean flag)
		{
		//if ((isSelectedItem && applyToSelectedItems) 
			//|| ((!isSelectedItem) && applyToAttachments))
			{
			if (flag)
				{
				origAttributes.put(obj, obj.getAttributes());
				if ((obj instanceof MiText) || (obj instanceof MiTextField))
					{
					obj.setAttributes(obj.getAttributes().overrideFrom(textAttributes));
					}
				else 
					{
					obj.setAttributes(obj.getAttributes().overrideFrom(attributes));
					}
				obj.setAttributes((MiAttributes )
				obj.getAttributes().initializeAsOverrideAttributes(false));
				}
			else
				{
				MiAttributes atts = (MiAttributes )origAttributes.get(obj);
				// If object was not added while selected...
				if (atts != null)
					{
					obj.setAttributes(atts.overrideFrom(obj.getAttributes()));
					}
				}
			}
		}
	}

