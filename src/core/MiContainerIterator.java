
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
import com.swfm.mica.util.IntVector; 


/**----------------------------------------------------------------------------------------------
 * This class iterates over parts of a MiContainer. It is used by
 * the MiEditorIterator to iterate through parts of each editor layer.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiContainerIterator implements MiiIterator
	{
	private		MiPart		container;
	private		int		index;
	private		boolean		iterateIntoPartsOfParts;
	private		boolean		iteratateIntoAttachmentsOfParts;
	private		MiParts		list;


					/**------------------------------------------------------
	 		 		 * Contructs a new MiContainerIterator.
	 		 		 * @param container	the container to iterate through
			 		 *------------------------------------------------------*/
	public				MiContainerIterator(MiPart container)
		{
		this(container, true, false, false);
		}
					/**------------------------------------------------------
	 		 		 * Contructs a new MiContainerIterator.
	 		 		 * @param container		the container to iterate
					 *				through
	 		 		 * @param startAtBeginning	true if the internal cursor
					 *				is set to the first part in
					 *				the container
	 		 		 * @param iterateIntoPartsOfParts
					 *				true if parts of parts are
					 *				iterated through
	 		 		 * @param iteratateIntoAttachmentsOfParts
					 *				true if attachments of parts are
					 *				iterated through
			 		 *------------------------------------------------------*/
	public				MiContainerIterator(MiPart container, 
						boolean startAtBeginning, 
						boolean iterateIntoPartsOfParts,
						boolean iteratateIntoAttachmentsOfParts)
		{
		this.container = container;
		this.iterateIntoPartsOfParts = iterateIntoPartsOfParts;
		this.iteratateIntoAttachmentsOfParts = iteratateIntoAttachmentsOfParts;

		list = new MiParts();
		createIteratorList(container, list);
		if (startAtBeginning)
			{
			index = -1;
			}
		else
			{
			index = list.size();
			}
		}
	protected	void		createIteratorList(MiPart container, MiParts list)
		{
		for (int i = 0; i < container.getNumberOfParts(); ++i)
			{
			MiPart part = container.getPart(i);
			list.addElement(part);
			if (iterateIntoPartsOfParts)
				{
				createIteratorList(part, list);
				}
			if ((iteratateIntoAttachmentsOfParts) && (part.getNumberOfAttachments() > 0))
				{
				createIteratorList(part.getAttachments(), list);
				}
			}
		}

					/**------------------------------------------------------
	 				 * Gets the next part. Null is returned if there are no
					 * more parts.
	 				 * @return		the next part
	 				 * @implements		MiiIterator
			 		 *------------------------------------------------------*/
	public		MiPart		getNext()
		{
		if (index < list.size() - 1)
			{
			++index;
			return(list.elementAt(index));
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Gets the previous part. Null is returned if there are no
					 * previous parts.
	 				 * @return		the previous part
	 				 * @implements		MiiIterator
					 *------------------------------------------------------*/
	public		MiPart		getPrevious()
		{
		if ((index > 1) && (list.size() > 0))
			{
			--index;
			return(list.elementAt(index));
			}
		return(null);
		}
	}

