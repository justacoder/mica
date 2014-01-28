
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiIteratorFilter implements MiiIteratorFilter
	{
	private		boolean		iterateIntoAttachmentsOfParts;
	private		boolean		iterateIntoPartsOfParts;
	private		boolean		iterateThroughAllLayers;
	private		boolean		returnAllBottommostItems;
	private		String		iterationTag;



	public				MiIteratorFilter()
		{
		}

					/**------------------------------------------------------
	 				 * Specifies if all bottommost (parts of editor's current layer) 
					 * items are to be returned, in addition to all other 
					 * contraints specified here. This is usefull when one wants 
					 * everything to be candidates for the moveable, selectable, etc.
					 * event handlers, and wants to add just a few more (tagged) items
					 * as well.
					 * @param flag		true if return all bottommost items
					 * @see			#getIterateThroughAllBottomMostItems
					 *------------------------------------------------------*/
	public 		void		setIterateThroughAllBottomMostItems(boolean flag)
		{
		returnAllBottommostItems = flag;
		}
					/**------------------------------------------------------
	 				 * Gets whether all bottommost (parts of editor's current layer) 
					 * items are to be returned, in addition to all other 
					 * contraints specified here.
					 * @return 		true if so
					 * @see			#setIterateThroughAllBottomMostItems
					 *------------------------------------------------------*/
	public 		boolean		getIterateThroughAllBottomMostItems()
		{
		return(returnAllBottommostItems);
		}
					/**------------------------------------------------------
	 				 * Specifies if items with given tag are to be returned
					 * or, if null, that only the major items (bottommost, the
					 * objects right on top of the editor's current layer) are to
					 * be returned (if selectable). MiParts are assigned a tag,
					 * as of this release, by assigning the part a resource
					 * with the name of the tag (part.setResource(tag, tag));
					 * @param tag		the tag
					 * @see			#getIterateThroughTaggedItemsOnly
					 *------------------------------------------------------*/
	public 		void		setIterateThroughTaggedItemsOnly(String tag)
		{
		iterationTag = tag;
		}
					/**------------------------------------------------------
	 				 * Gets whether only items with given tag are to be returned.
					 * @return 		the tag
					 * @see			#setIterateThroughTaggedItemsOnly
					 *------------------------------------------------------*/
	public 		String		getIterateThroughTaggedItemsOnly()
		{
		return(iterationTag);
		}
					/**------------------------------------------------------
	 		 		 * Sets whether the iterator will iterate through all
					 * layers or just the editor's current layer.
	 		 		 * @param flag		true if iterate through all layers
			 		 *------------------------------------------------------*/
	public		void		setIterateThroughAllLayers(boolean flag)
		{
		iterateThroughAllLayers = flag;
		}
					/**------------------------------------------------------
	 		 		 * Gets whether the iterator will iterate through all
					 * layers or just the editor's current layer.
	 		 		 * @return 		true if iterate through all layers
					 * @implements		MiiIteratorFilter#getIterateThroughAllLayers
			 		 *------------------------------------------------------*/
	public		boolean		getIterateThroughAllLayers()
		{
		return(iterateThroughAllLayers);
		}
					/**------------------------------------------------------
					 * Sets whether iterators should go into the parts of the
					 * MiParts in this editor. This is used mostly by 
					 * MiEditorIterator which is mostly used by selection 
					 * manager and other shape editing event handlers. Useful
					 * for when selected parts are inside containers that
					 * are graph layouts (i.e. if this MiEditor has MiStarGraphs
					 * wome of which have selected nodes).
					 * @param flag		true if iterators are to go into parts
					 * @see			#getIterateIntoPartsOfParts
					 * @see			#getIterateIntoAttachmentsOfParts
					 *------------------------------------------------------*/
	public		void		setIterateIntoPartsOfParts(boolean flag)
		{
		iterateIntoPartsOfParts = flag;
		}
					/**------------------------------------------------------
					 * Gets whether iterators should go into the parts of the
					 * MiParts in this editor. 
					 * @return		true if iterators are to go into parts
					 * @see			#setIterateIntoPartsOfParts
					 * @see			#setIterateIntoAttachmentsOfParts
					 * @implements		MiiIteratorFilter#getIterateIntoPartsOfParts
					 *------------------------------------------------------*/
	public		boolean		getIterateIntoPartsOfParts()
		{
		return(iterateIntoPartsOfParts);
		}
					/**------------------------------------------------------
					 * Sets whether iterators should go into the attachments of the
					 * MiParts in this editor. This is used mostly by 
					 * MiEditorIterator which is mostly used by selection 
					 * manager and other shape editing event handlers. Useful
					 * for when selected parts are attachments or inside attachments.
					 * @param flag		true if iterators are to go into attachments
					 * @see			#getIterateIntoAttachmentsOfParts
					 * @see			#getIterateIntoPartsOfParts
					 *------------------------------------------------------*/
	public		void		setIterateIntoAttachmentsOfParts(boolean flag)
		{
		iterateIntoAttachmentsOfParts = flag;
		}
					/**------------------------------------------------------
					 * Gets whether iterators should go into the attachments of the
					 * parts.
					 * @return		true if iterators are to go into attachments
					 * @see			#setIterateIntoAttachmentsOfParts
					 * @see			#setIterateIntoPartsOfParts
					 * @implements		MiiIteratorFilter#getIterateIntoAttachmentsOfParts
					 *------------------------------------------------------*/
	public		boolean		getIterateIntoAttachmentsOfParts()
		{
		return(iterateIntoAttachmentsOfParts);
		}
	public		boolean		accept(MiPart part)
		{
		return(((returnAllBottommostItems) && (part.getContainer(0) instanceof MiLayer))
			|| (iterationTag == null) || (part.getResource(iterationTag) != null));
		}
	}

