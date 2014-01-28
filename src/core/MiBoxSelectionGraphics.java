
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
 * This class implements the MiiSelectionGraphics interface. As
 * such it is responsible for the making the appearance of a
 * graphics part when the part is 'selected' different from when
 * it is 'not selected'.
 * <p>
 * This implementation draws a rectangle around the selected 
 * part unless the part is a line, in which case the line is 
 * rendered with a change to the line borderLook attribute.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiBoxSelectionGraphics implements MiiSelectionGraphics, MiiTypes
	{
	private 	MiAttributes	attributes	= new MiAttributes();
	private		MiMargins	margins		= new MiMargins(5);
	private		MiMargins	lineMargins	= new MiMargins(5);
	private		MiSize		minimumBoxSize	= new MiSize(5);



					/**------------------------------------------------------
	 				 * Constructs a new MiBoxSelectionGraphics. 
					 *------------------------------------------------------*/
	public				MiBoxSelectionGraphics()
		{
		attributes = attributes.setBorderLook(Mi_RIDGE_BORDER_LOOK);
		attributes = attributes.setCopyable(false);
		attributes = attributes.setCopyableAsPartOfCopyable(false);
		}

					/**------------------------------------------------------
	 				 * Set the attributes to use for the box displayed around
					 * a selected part.
					 * @param attributes	the attributes of the box
					 *------------------------------------------------------*/
	public		void		setAttributes(MiAttributes attributes)
		{
		this.attributes = attributes.setCopyable(false);
		this.attributes = attributes.setCopyableAsPartOfCopyable(false);
		}

					/**------------------------------------------------------
	 				 * Get the attributes used for the box displayed around
					 * a selected part.
					 * @return 		the attributes of the box
					 *------------------------------------------------------*/
	public		MiAttributes	getAttributes()
		{
		return(attributes);
		}

					/**------------------------------------------------------
	 				 * Set the margins to use for the box displayed around
					 * a selected part. This is the distance between the 
					 * bounds of the selected part and the box.
					 * @param margins	the margins around the part
					 *------------------------------------------------------*/
	public		void		setMargins(MiMargins margins)
		{
		this.margins.copy(margins);
		}

					/**------------------------------------------------------
	 				 * Get the margins to use for the box displayed around
					 * a selected part.
					 * @return 		the margins around the part
					 *------------------------------------------------------*/
	public		MiMargins	getMargins()
		{
		return(margins);
		}
					/**------------------------------------------------------
	 				 * Set the minimum width and height to use for the box 
					 * displayed around a selected part. This is useful for
					 * selecting zero width or height parts when the margins
					 * are close to or equal to zero.
					 * @param size		the min size of box around the part
					 *------------------------------------------------------*/
	public		void		setMinimumBoxWidthAndHeight(MiSize size)
		{
		this.minimumBoxSize.copy(size);
		}
					/**------------------------------------------------------
	 				 * Get the minimum width and height to use for the box 
					 * displayed around a selected part. This is useful for
					 * selecting zero width or height parts when the margins
					 * are close to or equal to zero.
					 * @return 		the min size of box around the part
					 *------------------------------------------------------*/
	public		MiSize		getMinimumBoxWidthAndHeight()
		{
		return(minimumBoxSize);
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
		if (flag)
			{
			MiPart rect = null;
			if (obj instanceof MiLine)
				{
				rect = obj.copy();
				}
			else if (obj instanceof MiConnection)
				{
				rect = ((MiConnection )obj).getGraphics().copy();
				}
			else
				{
				rect = new MiRectangle();
				}
			rect.setResource(SELECTION_GRAPHICS_GRAPHICS, SELECTION_GRAPHICS_GRAPHICS);
			rect.setAttributes(attributes);
			rect.setPickable(false);
			rect.setSelectable(false);
			rect.setSnappable(false);
			rect.setInvalidLayoutNotificationsEnabled(false);

			if ((obj instanceof MiConnection)
				|| (obj instanceof MiLine))
				{
				rect.setLineWidth(obj.getLineWidth() + lineMargins.getWidth());
				rect.setLineStyle(Mi_SOLID_LINE_STYLE);
				obj.appendAttachment(rect, Mi_ALONG_LOCATION, "selection", null);
				}
			else 
				{
				MiMargins boxMargins = margins;
				if ((obj.getWidth() < minimumBoxSize.width) || (obj.getHeight() < minimumBoxSize.height))
					{
					boxMargins = new MiMargins(margins);
					if (obj.getWidth() < minimumBoxSize.width)
						{
						boxMargins.left = (minimumBoxSize.width/2);
						boxMargins.right = (minimumBoxSize.width/2);
						}
					if (obj.getHeight() < minimumBoxSize.height)
						{
						boxMargins.bottom = (minimumBoxSize.height/2);
						boxMargins.top = (minimumBoxSize.height/2);
						}
					}
				obj.appendAttachment(rect, Mi_SURROUND_LOCATION, "selection", boxMargins);
				}
					
			}
		else
			{
			obj.removeAttachment("selection");
			}
		}
	}

