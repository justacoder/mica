
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
 * This class specifies which parts are to be treated as whole, interactable
 * parts and which parts are going to be visible. For example:
 * <p>
 * To create iterator filter to tell the editor which graphics are
 * selectable, movable, etc. (by default only the graphics that
 * are right on the background layer are. But we want to be able
 * to interact with types which are on top of attributes on top
 * of classes on top of the background layer).
 * <p>
 * <pre>
 *	MiEditorFilter filter = new MiEditorFilter();
 *	MiIteratorFilter iteratorFilter = new MiIteratorFilter();
 *	iteratorFilter.setIterateThroughTaggedItemsOnly(MiUML_SELECTABLE_GRAPHICS_TAG);
 *	iteratorFilter.setIterateIntoPartsOfParts(true);
 *	iteratorFilter.setIterateIntoAttachmentsOfParts(true);
 *	filter.setIteratorFilter(iteratorFilter);
 *	getEditor().setFilter(filter);
 * </pre>
 *
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorFilter
	{
	private		MiiIteratorFilter iteratorFilter;
	private		MiiViewFilter	viewFilter;
	private		MiEditor	editor;


	public				MiEditorFilter()
		{
		}

	public		void		setViewFilter(MiiViewFilter filter)
		{
		viewFilter = filter;
		}
	public		MiiViewFilter	getViewFilter()
		{
		return(viewFilter);
		}

	public		void		setIteratorFilter(MiiIteratorFilter filter)
		{
		iteratorFilter = filter;
		}
	public		MiiIteratorFilter getIteratorFilter()
		{
		return(iteratorFilter);
		}
	
	public		MiiIterator	getIterator(MiEditor editor)
		{
		MiEditorIterator iterator = new MiEditorIterator(editor);
		iterator.setFilter(this);
		if (iteratorFilter != null)
			{
			iterator.setIterateIntoPartsOfParts(iteratorFilter.getIterateIntoPartsOfParts());
			iterator.setIterateIntoAttachmentsOfParts(iteratorFilter.getIterateIntoAttachmentsOfParts());
			iterator.setIterateThroughAllLayers(iteratorFilter.getIterateThroughAllLayers());
			}
		return(iterator);
		}
	public		void		filterParts(MiParts input, MiParts output)
		{
		output.removeAllElements();
		for (int i = 0; i < input.size(); ++i)
			{
			MiPart obj = input.elementAt(i);
			obj = accept(obj);
			if (obj != null)
				{
				output.addElement(obj);
				}
			}
		}
	public		MiPart		accept(MiPart part)
		{
		if (((iteratorFilter == null) || (iteratorFilter.accept(part)))
			&& ((viewFilter == null) || ((part = viewFilter.accept(part)) != null)))
			{
			return(part);
			}
		return(null);
		}
	}


