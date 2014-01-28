
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
public class MiInternalWindow extends MiWindow implements MiiCommandHandler, MiiCommandNames
	{
	public static 	String			Mi_ICONIC_REPRESENTATION_WINDOW = "iconsAassociatedWindow";
	private 	MiPart			parent;
	private 	boolean			modal;
	private		MiWindowBorder		windowBorder;
	private		MiPart			iconizedAppearance;
	private		boolean			fullscreen;
	private		boolean			open;
	private		boolean			iconified;
	private		MiBounds		windowBorderNormalSize;
	private		MiEvent			deIconifyUserEvent = new MiEvent(Mi_LEFT_MOUSE_DBLCLICK_EVENT);


	public				MiInternalWindow(MiPart parent, String title, MiBounds windowSize, boolean modal)
		{
		super(windowSize);
		super.setVisible(false);
		this.parent = parent;
		windowBorder = new MiWindowBorder(this, title);
		setUniverseBounds(getWorldBounds());

		// Initialize the layout to something that will keep contents in the
		// center of the window... 
		setLayout(new MiColumnLayout());

		this.modal = modal;

		setBackgroundColor("veryDarkWhite");
		iconizedAppearance = MiToolkit.getIconizedWindowIcon();
		iconizedAppearance.appendCommandHandler(
			this, MiiCommandNames.Mi_DEICONIFY_COMMAND_NAME, deIconifyUserEvent);
		}
	public		void		setTitle(String title)
		{
		windowBorder.setTitle(title);
		}
	public		String		getTitle()
		{
		return(windowBorder == null ? null : windowBorder.getTitle());
		}
	public		MiWindowBorder	getWindowBorder()
		{
		return(windowBorder);
		}
	public		void		setWindowBorder(MiWindowBorder border)
		{
		windowBorder = border;
		}
	public		MiPart		getIconifyButton()
		{
		return(windowBorder.getIconifyButton());
		}
	public		MiPart		getCloseButton()
		{
		return(windowBorder.getCloseButton());
		}
	public		MiPart		getFullScreenButton()
		{
		return(windowBorder.getFullScreenButton());
		}
	public		MiPart		getParent()
		{
		return(parent);
		}
	public		void		setIconizedAppearance(MiPart icon)
		{
		if (deIconifyUserEvent != null)
			icon.appendCommandHandler(this, MiiCommandNames.Mi_DEICONIFY_COMMAND_NAME, deIconifyUserEvent);
		iconizedAppearance = icon;
		if (icon != null)
			{
			icon.setResource(Mi_ICONIC_REPRESENTATION_WINDOW, this);
			if (iconified)
				getPart(0).replaceSelf(icon);
			}
		}
	public		MiPart		getIconizedAppearance()
		{
		return(iconizedAppearance);
		}
	public		void		setVisible(boolean flag)
		{
		if (flag == isVisible())
			return;

		if (flag)
			open();
		else
			close();
		}
	public		void		dispose()
		{
		if (parent != null)
			{
			if (iconified)
				parent.removePart(iconizedAppearance);
			else if (isEmbeddedWindow())
				parent.removePart(windowBorder);
			else
				parent.removeAttachment(windowBorder);
			}
		}
					// DeIconfiy is necessary...
	protected	void		open()
		{
		if (iconified)
			deIconify();
		if (!open)
			{
			if (!isEmbeddedWindow())
				{
				// Do this now so perferredSize of windowBorder takes this into account
				if (!isVisible())
					super.setVisible(true);
				MiSize size = new MiSize();
				windowBorder.getPreferredSize(size);
				MiBounds b = new MiBounds(size);
				b.setCenter(parent.getBounds().getCenter());
				windowBorder.setBounds(b);
				// Need to convert coords/bounds to device
				invalidateLayout();
				validateLayout();
				parent.appendAttachment(windowBorder);
				}
			else
				{
				// Needed? Desirable?
				//windowBorder.validateLayout();
				//invalidateLayout();
				parent.appendPart(windowBorder);
				// Do this now so default keyboard focus cursor animator knows 
				// which root window to use of windowBorder takes this into account
				if (!isVisible())
					super.setVisible(true);
				getContainer(0).invalidateLayout();
				}
			//window.getKeyboardFocusManager().initialize();
			open = true;
			}
		//setVisible(true);
		}
	protected	void		close()
		{
		if (isVisible())
			super.setVisible(false);
		if (open)
			{
			if (iconified)
				;
			else if (isEmbeddedWindow())
				parent.removePart(windowBorder);
			else
				parent.removeAttachment(windowBorder);
			open = false;
			}
		}
	public		void		layoutParts()
		{
		super.layoutParts();
		windowBorder.layoutParts();
		}
	public		void		iconify()
		{
		if (!iconified)
			{
			if (!dispatchActionRequest(Mi_WINDOW_ICONIFY_ACTION))
				return;
			iconizedAppearance.setCenter(windowBorder.getCenter());
			close();

			if (isEmbeddedWindow())
				parent.appendPart(iconizedAppearance);
			else
				parent.appendAttachment(iconizedAppearance);
			iconified = true;
			dispatchAction(Mi_WINDOW_ICONIFY_ACTION);
			}
		}
	public		void		deIconify()
		{
		if (iconified)
			{
			if (!dispatchActionRequest(Mi_WINDOW_DEICONIFY_ACTION))
				return;
			if (isEmbeddedWindow())
				parent.removePart(iconizedAppearance);
			else
				parent.removeAttachment(iconizedAppearance);
			windowBorder.setCenter(iconizedAppearance.getCenter());
			iconified = false;
			open();
			dispatchAction(Mi_WINDOW_DEICONIFY_ACTION);
			}
		}
	public		boolean		isFullScreen()
		{
		return(fullscreen);
		}
	public		void		toggleFullScreen()
		{
		if (fullscreen)
			{
			if (!dispatchActionRequest(Mi_WINDOW_FULLSCREEN_ACTION))
				return;
			windowBorder.setBounds(windowBorderNormalSize);
			dispatchAction(Mi_WINDOW_FULLSCREEN_ACTION);
			}
		else
			{
			if (!dispatchActionRequest(Mi_WINDOW_NORMALSIZE_ACTION))
				return;
			windowBorderNormalSize = windowBorder.getBounds();
			windowBorder.setBounds(parent.getInnerBounds());
			dispatchAction(Mi_WINDOW_NORMALSIZE_ACTION);
			}
		fullscreen = !fullscreen;
		windowBorder.setFullScreenTitleBarLook(fullscreen);
		}
					// sameLayer vrs AttatchmentOverlayLayer
	public		void		setIsEmbeddedWindow(boolean flag)
		{
		if (flag != isEmbeddedWindow())
			{
			super.setIsEmbeddedWindow(flag);
			if (parent != null)
				{
				if (iconified)
					{
					}
				else if ((isEmbeddedWindow()) && (open))
					{
					parent.removeAttachment(windowBorder);
					parent.appendPart(windowBorder);
					}
				else if (open)
					{
					parent.removePart(windowBorder);
					parent.appendAttachment(windowBorder);
					}
				}
			}
		}
	protected	boolean		notifyWindowDestroy()
		{
		if (!dispatchActionRequest(Mi_WINDOW_CLOSE_ACTION))
			return(false);
		dispatchAction(Mi_WINDOW_CLOSE_ACTION);
		return(true);
		}
					/**------------------------------------------------------
	 				 * Processes the given command.
					 * @param command  	the command to execute
					 * @implements		MiiCommandHandler#processCommand
					 *------------------------------------------------------*/
	public		void		processCommand(String command)
		{
		if (command.equalsIgnoreCase(Mi_CLOSE_COMMAND_NAME))
			{
			if (notifyWindowDestroy())
				close();
			}
		else if (command.equalsIgnoreCase(Mi_ICONIFY_COMMAND_NAME))
			{
			iconify();
			}
		else if (command.equalsIgnoreCase(Mi_DEICONIFY_COMMAND_NAME))
			{
			deIconify();
			}
		else if (command.equalsIgnoreCase(Mi_TOGGLE_FULLSCREEN_COMMAND_NAME))
			{
			toggleFullScreen();
			}
		}
	protected	MiPart		makeNewInstance()
		{
		return(new MiInternalWindow(parent, getTitle(), getBounds(new MiBounds()), modal));
		}
	public		String		toString()
		{
		return(super.toString() + "." + getTitle());
		}
	}

