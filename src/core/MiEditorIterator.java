
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
 * This class implements an iterator that iterates through
 * MiEditors, correctly handling any multiple layers.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorIterator implements MiiIterator
	{
	private		MiEditor		editor;
	private		MiContainerIterator	iterator;
	private		boolean			iterateThroughAllLayers;
	private		boolean			iterateIntoPartsOfParts;
	private		boolean			iterateIntoAttachmentsOfParts;
	private		boolean			hasLayers;
	private		boolean			startAtTop;
	private		int			layerNum;
	private		MiEditorFilter		filter;


					/**------------------------------------------------------
	 		 		 * Contructs a new MiEditorIterator. This will iterate
					 * starting at the first part of the first layer.
	 		 		 * @param editor	the editor to iterate through
			 		 *------------------------------------------------------*/
	public				MiEditorIterator(MiEditor editor)
		{
		this(editor, false);
		}

					/**------------------------------------------------------
	 		 		 * Contructs a new MiEditorIterator. This will iterate
					 * into parts of parts of the editor as mandated by the
					 * given editor's getIterateIntoPartsOfParts() method.
					 * If iterateThroughAllLayers is false (the default)
					 * then only the editor's current layer is iterated
					 * through.
	 		 		 * @param editor	the editor to iterate through
	 		 		 * @param startAtTop	true if the iteration is to start
					 *			at the last part of the last layer
					 *			(which is the top-most part).
					 *			false if the iteration is to start
					 *			at the first part of the first,
					 *			bottom-most layer).
					 * @see			MiEditor#getIterateIntoPartsOfParts
			 		 *------------------------------------------------------*/
	public				MiEditorIterator(MiEditor editor, boolean startAtTop)
		{
		this(editor, startAtTop, false, false);
		}
	public				MiEditorIterator(
						MiEditor editor, 
						boolean startAtTop, 
						boolean iterateIntoPartsOfParts)
		{
		this(editor, startAtTop, iterateIntoPartsOfParts, false);
		}
	public				MiEditorIterator(
						MiEditor editor, 
						boolean startAtTop, 
						boolean iterateIntoPartsOfParts,
						boolean iterateIntoAttachmentsOfParts)
		{
		this.startAtTop = startAtTop;
		this.editor = editor;
		this.iterateIntoPartsOfParts = iterateIntoPartsOfParts;
		this.iterateIntoAttachmentsOfParts = iterateIntoAttachmentsOfParts;
		initialize();
		}
	public		void		setFilter(MiEditorFilter filter)
		{
		this.filter = filter;
		}
	public		MiEditorFilter	getFilter()
		{
		return(filter);
		}
					/**------------------------------------------------------
	 		 		 * Sets whether the iterator will iterate through all
					 * parts of all parts of the assigned container or will
					 * just iterate through the parts of the container.
	 		 		 * @param flag		true if iterate through whole
					 *			tree of parts
			 		 *------------------------------------------------------*/
	public		void		setIterateIntoPartsOfParts(boolean flag)
		{
		iterateIntoPartsOfParts = flag;
		initialize();
		}
					/**------------------------------------------------------
	 		 		 * Gets whether the iterator will iterate through all
					 * parts of all parts of the assigned container or will
					 * just iterate through the parts of the container.
	 		 		 * @return 		true if iterate through whole
					 *			tree of parts
			 		 *------------------------------------------------------*/
	public		boolean		getIterateIntoPartsOfParts()
		{
		return(iterateIntoPartsOfParts);
		}
					/**------------------------------------------------------
	 		 		 * Sets whether the iterator will iterate through all
					 * attachments of all parts that are encountered.
	 		 		 * @param flag		true if iterate through attachments
					 *			too
			 		 *------------------------------------------------------*/
	public		void		setIterateIntoAttachmentsOfParts(boolean flag)
		{
		iterateIntoAttachmentsOfParts = flag;
		initialize();
		}
					/**------------------------------------------------------
	 		 		 * Gets whether the iterator will iterate through all
					 * attachments of all parts encountered.
	 		 		 * @return 		true if iterate through attachments
					 *			too
			 		 *------------------------------------------------------*/
	public		boolean		getIterateIntoAttachmentsOfParts()
		{
		return(iterateIntoAttachmentsOfParts);
		}
					/**------------------------------------------------------
	 		 		 * Sets whether the iterator will iterate through all
					 * layers or just the editor's current layer.
	 		 		 * @param flag		true if iterate through all layers
			 		 *------------------------------------------------------*/
	public		void		setIterateThroughAllLayers(boolean flag)
		{
		iterateThroughAllLayers = flag;
		initialize();
		}
					/**------------------------------------------------------
	 		 		 * Gets whether the iterator will iterate through all
					 * layers or just the editor's current layer.
	 		 		 * @return 		true if iterate through all layers
			 		 *------------------------------------------------------*/
	public		boolean		getIterateThroughAllLayers()
		{
		return(iterateThroughAllLayers);
		}
					/**------------------------------------------------------
					 * Gets the next part. Null is returned if there are no
					 * more parts.
					 * @return		the next part
	 				 * @implements		MiiIterator
					 *------------------------------------------------------*/
	public		MiPart		getNext()
		{
		MiPart obj = null;
		do	{
			obj = iterator.getNext();
			while (obj == null)
				{
				if (!hasLayers)
					return(null);
				if (!iterateThroughAllLayers)
					return(null);
				MiContainerIterator iter = getNextIterator();
				if (iter == null)
					return(null);
				iterator = iter;
				obj = iterator.getNext();
				}
			} while ((filter != null) && ((obj = filter.accept(obj)) == null));

		return(obj);
		}

					/**------------------------------------------------------
	 				 * Gets the previous part. Null is returned if there are no
					 * previous parts.
	 				 * @return		the previous part
	 				 * @implements		MiiIterator
					 *------------------------------------------------------*/
	public		MiPart		getPrevious()
		{
		MiPart obj = iterator.getPrevious();
		while ((obj == null) || ((filter != null) && ((obj = filter.accept(obj)) == null)))
			{
			if (!hasLayers)
				return(null);
			if (!iterateThroughAllLayers)
				return(null);
			MiContainerIterator iter = getPreviousIterator();
			if (iter == null)
				return(null);
			iterator = iter;
			obj = iterator.getPrevious();
			}
		return(obj);
		}

	public		void		reset()
		{
		initialize();
		}

	//***************************************************************************************
	// Private methods
	//***************************************************************************************

					/**------------------------------------------------------
	 		 		 * Initializes this iterator which is required before
					 * the first iteration is performed.
			 		 *------------------------------------------------------*/
	private		void		initialize()
		{
		if (iterateThroughAllLayers && editor.hasLayers())
			{
			hasLayers = true;
			if (!startAtTop)
				layerNum = 0;
			else
				layerNum = editor.getNumberOfLayers() - 1;
			
			iterator = new MiContainerIterator(
				editor.getLayer(layerNum), !startAtTop, 
				iterateIntoPartsOfParts, iterateIntoAttachmentsOfParts);
			}
		else
			{
			iterator = new MiContainerIterator(
				editor.getCurrentLayer(), !startAtTop, 
				iterateIntoPartsOfParts, iterateIntoAttachmentsOfParts);
			}
		}

					/**------------------------------------------------------
	 		 		 * If the associated editor has layers, this method gets
					 * the iterator for the next layer.
	 		 		 * @return 		the iterator for the next layer
			 		 *------------------------------------------------------*/
	private		MiContainerIterator getNextIterator()
		{
		if (startAtTop)
			{
			while (--layerNum >= 0)
				{
				if (editor.getLayer(layerNum).isVisible())
					{
					return(new MiContainerIterator(
						editor.getLayer(layerNum), !startAtTop,
						iterateIntoPartsOfParts, iterateIntoAttachmentsOfParts));
					}
				}
			return(null);
			}
		while (++layerNum <= editor.getNumberOfLayers() - 1)
			{
			if (editor.getLayer(layerNum).isVisible())
				{
				return(new MiContainerIterator(
					editor.getLayer(layerNum), !startAtTop, 
						iterateIntoPartsOfParts, iterateIntoAttachmentsOfParts));
				}
			}
		return(null);
		}
					/**------------------------------------------------------
	 		 		 * If the associated editor has layers, this method gets
					 * the iterator for the previous layer.
	 		 		 * @return 		the iterator for the previous layer
			 		 *------------------------------------------------------*/
	private		MiContainerIterator getPreviousIterator()
		{
		if (startAtTop)
			{
			while (++layerNum <= editor.getNumberOfLayers() - 1)
				{
				if (editor.getLayer(layerNum).isVisible())
					{
					return(new MiContainerIterator(
						editor.getLayer(layerNum), !startAtTop, 
						iterateIntoPartsOfParts, iterateIntoAttachmentsOfParts));
					}
				}
			return(null);
			}
		while (--layerNum >= 0)
			{
			if (editor.getLayer(layerNum).isVisible())
				{
				return(new MiContainerIterator(
					editor.getLayer(layerNum), !startAtTop, 
					iterateIntoPartsOfParts, iterateIntoAttachmentsOfParts));
				}
			}
		return(null);
		}
	}

