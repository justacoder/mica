
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
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSearchManager
	{
	private		MiEditor	editor;
	private		String		searchResultText;
	private		boolean		ignoreCase;
	private		MiEditorIterator iterator;


	public				MiSearchManager(MiEditor editor)
		{
		this(editor, true);
		}
	public				MiSearchManager(MiEditor editor, boolean ignoreCase)
		{
		this.editor = editor;
		this.ignoreCase = ignoreCase;
		iterator = new MiEditorIterator(editor);
		iterator.setIterateIntoPartsOfParts(true);
		iterator.setIterateThroughAllLayers(true);
		iterator.setIterateIntoAttachmentsOfParts(true);
		}
	public		String		getSearchText()
		{
		return(searchResultText);
		}
	public		MiPart		searchFoward(String searchText)
		{
		searchResultText = searchText;
		iterator.reset();
		return(searchFoward(editor, searchText));
		}
	public		MiPart		searchFowardAgain()
		{
		if (searchResultText == null)
			return(null);

		return(searchFoward(editor, searchResultText));
		}
	public		MiPart		searchBackward(String searchText)
		{
		searchResultText = searchText;
		iterator.reset();
		return(searchBackward(editor, searchText));
		}
	public		MiPart		searchBackwardAgain()
		{
		if (searchResultText == null)
			return(null);

		return(searchBackward(editor, searchResultText));
		}
	protected	MiPart		searchFoward(MiPart container, String searchText)
		{
		MiPart part;
		while ((part = iterator.getNext()) != null)
			{
			if (isMatch(part, searchText))
				return(part);
			}
		return(null);
		}
	protected	MiPart		searchBackward(MiPart container, String searchText)
		{
		MiPart part;
		while ((part = iterator.getPrevious()) != null)
			{
//System.out.println("BACKWARD part  = " + part);
			if (isMatch(part, searchText))
				return(part);
			}
		return(null);
		}
	protected	boolean		isMatch(MiPart part, String searchText)
		{
		String text = null;

		if (part instanceof MiText)
			text = ((MiText )part).getText();
		else if (part instanceof MiWidget)
			text = ((MiWidget )part).getValue();

		if (!Utility.isEmptyOrNull(text))
			{
			if (ignoreCase)
				{
				return(Utility.indexOfIgnoreCase(text, searchText) != -1);
				}
			return(text.indexOf(searchText) != -1);
			}
		return(false);
		}
	}

