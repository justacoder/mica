
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

/**----------------------------------------------------------------------------------------------
 * This class pops up a borderless window that displays the given image.
 * The cursor is set to a Mi_WAIT_CURSOR.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiSplashScreen implements MiiActionHandler, MiiTypes
	{
	private		MiNativeWindow	splash;
	private		MiNativeWindow	parent;

	public				MiSplashScreen(MiPart image)
		{
		this(null, image);
		}

	public				MiSplashScreen(MiNativeWindow parent, MiPart image)
		{
		this.parent =parent;
		splash = new MiNativeWindow(parent, new MiBounds(0,0,500,500), true);
		//splash = new MiNativeWindow(parent, "", new MiBounds(0,0,500,500), true);
		splash.setLayout(new MiColumnLayout());
		splash.appendPart(image);
		splash.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));
		splash.validateLayout();
		splash.setContextCursor(MiiTypes.Mi_WAIT_CURSOR);
		image.setBorderLook(Mi_RAISED_BORDER_LOOK);
		splash.setVisible(true);
		if ((parent != null) && (!parent.isVisible()))
			parent.appendActionHandler(this, MiiActionTypes.Mi_WINDOW_OPEN_ACTION);
		}

	public		MiNativeWindow	getScreen()
		{
		return(splash);
		}

	public		void		setParent(MiNativeWindow window)
		{
		parent = window;
		parent.appendActionHandler(this, MiiActionTypes.Mi_WINDOW_OPEN_ACTION);
		}
	public		void		setVisible(boolean flag)
		{
		splash.setVisible(flag);
		}
	public		void		dispose()
		{
		if (parent != null)
			parent.removeActionHandlers(this);
		//splash.getAccessLock();
		splash.setVisible(false);
		splash.dispose();
		//splash.freeAccessLock();
		}
	public		boolean		processAction(MiiAction action)
		{
		if (parent.isVisible())
			{
			splash.toFront();
			}
		return(true);
		}
	}


