
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
public class MiAnotherView extends MiWidget implements MiiActionHandler
	{
	private		MiEditor		view;
	private		MiEditor		target;
	private		MiRectangle		rect;
	private		MiVisibleContainer	insideContainer;



	public				MiAnotherView(MiEditor editor)
		{
		setupMiAnotherView();
		setViewTarget(editor);
		}
	protected	void		setupMiAnotherView()
		{
		setSize(new MiSize(200, 200));
		insideContainer = new MiVisibleContainer();
		insideContainer.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		insideContainer.setContainerLayoutSpec(MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS);
		insideContainer.setInsetMargins(new MiMargins(8));
		insideContainer.setBackgroundColor(MiColorManager.lightGray);
		appendPart(insideContainer);

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);

		view = new MiEditor();
		insideContainer.appendPart(view);
		rect = new MiRectangle();
		rect.setColor("red");
		//rect.setWriteMode(Mi_XOR_WRITEMODE);
		view.appendPart(rect);
		view.appendEventHandler(new MiIZoomAroundMouse());
		view.appendEventHandler(new MiIPan());
		view.setDeviceBounds(new MiBounds(0,0,200,200));
		//view.setPreferredSize(new MiSize(200,200));
		view.setMinimumSize(new MiSize(100,100));
		}

	public		void		updateView()
		{
		if (!isVisible())
			return;

		MiBounds targetUniverseBounds = target.getUniverseBounds();
		view.setUniverseBounds(targetUniverseBounds);

		view.setBackgroundColor(target.getBackgroundColor());

		rect.setBounds(target.getWorldBounds());
		}

	public		void		setViewTarget(MiEditor targetEditor)
		{
		target = targetEditor;
		target.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			view.appendPart(target.getPart(i));
		view.removePart(rect);
		view.appendPart(rect);
		updateView();
		view.setWorldBounds(view.getUniverseBounds());
		}
	public		MiEditor	getViewTarget()
		{
		return(target);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_EDITOR_VIEWPORT_CHANGED_ACTION))
			{
			updateView();
			}
		return(true);
		}
	}

