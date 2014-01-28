
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

import java.awt.Component;
import java.awt.Event;
import java.awt.event.KeyListener; 
import java.awt.event.KeyEvent; 
import java.awt.event.MouseListener; 
import java.awt.event.MouseEvent; 
import java.awt.event.MouseMotionListener; 

/**----------------------------------------------------------------------------------------------
 * This class serves as the interface (driver/wrapper) to the modern (post 1.0.2 AWT) 
 * event routines, specifically acting as a event listener to the Mica wrapper/interface
 * of the AWT Canvas widget. 
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
class MiAWTEventAdapter implements KeyListener, MouseListener, MouseMotionListener
	{
	private		MiCanvas	miCanvas;
	private		Component	component;
	private		int		lastX;
	private		int		lastY;
	private		boolean		grabbingKeyboardFocusWhenMouseEnters;

					/**------------------------------------------------------
	 				 * Constructs a new MiAWTEventAdapter. 
					 *------------------------------------------------------*/
	public				MiAWTEventAdapter(MiCanvas canvas, Component component)
		{
		miCanvas = canvas;
		this.component = component;
		component.addKeyListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		}
	public		void		setGrabbingKeyboardFocusWhenMouseEnters(boolean flag)
		{
		grabbingKeyboardFocusWhenMouseEnters = flag;
		}
	public		boolean		getGrabbingKeyboardFocusWhenMouseEnters()
		{
		return(grabbingKeyboardFocusWhenMouseEnters);
		}
	//***************************************************************************************
	// KeyListener implementation
	//***************************************************************************************
	public		void		keyTyped(KeyEvent e)
		{
		}
	public		void		keyPressed(KeyEvent e)
		{
		// JDK 1.2.1 sends extraneous events for shifted/control chars
		if (getOldEventKey(e) > 50000)
			return;

		miCanvas.handleEvent(new Event(
			this, e.getWhen(), Event.KEY_PRESS, lastX, lastY, 
			getOldEventKey(e), e.getModifiers(), null));
		}
	public		void		keyReleased(KeyEvent e)
		{
		// JDK 1.2.1 sends extraneous events for shifted/control chars
		if (getOldEventKey(e) > 50000)
			return;

		miCanvas.handleEvent(new Event(
			this, e.getWhen(), Event.KEY_RELEASE, lastX, lastY, 
			getOldEventKey(e), e.getModifiers(), null));
		}
	//***************************************************************************************
	// MouseListener implementation
	//***************************************************************************************
	public		void		mouseClicked(MouseEvent e)
		{
		}
	public		void		mousePressed(MouseEvent e)
		{
		lastX = e.getX();
		lastY = e.getY();
		component.requestFocus();
		miCanvas.handleEvent(new Event(
			this, e.getWhen(), Event.MOUSE_DOWN, e.getX(), e.getY(), 
			0, e.getModifiers(), null));
		}
	public		void		mouseReleased(MouseEvent e)
		{
		lastX = e.getX();
		lastY = e.getY();
		miCanvas.handleEvent(new Event(
			this, e.getWhen(), Event.MOUSE_UP, e.getX(), e.getY(), 
			0, e.getModifiers(), null));
		}
	public		void		mouseEntered(MouseEvent e)
		{
		lastX = e.getX();
		lastY = e.getY();
		if (grabbingKeyboardFocusWhenMouseEnters)
			component.requestFocus();
		miCanvas.handleEvent(new Event(
			this, e.getWhen(), Event.MOUSE_ENTER, e.getX(), e.getY(), 
			0, e.getModifiers(), null));
		}
	public		void		mouseExited(MouseEvent e)
		{
		lastX = e.getX();
		lastY = e.getY();
		miCanvas.handleEvent(new Event(
			this, e.getWhen(), Event.MOUSE_EXIT, e.getX(), e.getY(), 
			0, e.getModifiers(), null));
		}
	//***************************************************************************************
	// MouseMotionListener implementation
	//***************************************************************************************
	public		void		mouseDragged(MouseEvent e)
		{
		lastX = e.getX();
		lastY = e.getY();
		miCanvas.handleEvent(new Event(
			this, e.getWhen(), Event.MOUSE_DRAG, e.getX(), e.getY(), 
			0, e.getModifiers(), null));
		}
	public		void 		mouseMoved(MouseEvent e)
		{
		lastX = e.getX();
		lastY = e.getY();
		miCanvas.handleEvent(new Event(
			this, e.getWhen(), Event.MOUSE_MOVE, e.getX(), e.getY(), 
			0, e.getModifiers(), null));
		}

    private static final int actionKeyCodes[][] = {
    /*    virtual key              action key   */
        { KeyEvent.VK_HOME,        Event.HOME         },
        { KeyEvent.VK_END,         Event.END          },
        { KeyEvent.VK_PAGE_UP,     Event.PGUP         },
        { KeyEvent.VK_PAGE_DOWN,   Event.PGDN         },
        { KeyEvent.VK_UP,          Event.UP           },
        { KeyEvent.VK_DOWN,        Event.DOWN         },
        { KeyEvent.VK_LEFT,        Event.LEFT         },
        { KeyEvent.VK_RIGHT,       Event.RIGHT        },
        { KeyEvent.VK_F1,          Event.F1           },
        { KeyEvent.VK_F2,          Event.F2           },
        { KeyEvent.VK_F3,          Event.F3           },
        { KeyEvent.VK_F4,          Event.F4           },
        { KeyEvent.VK_F5,          Event.F5           },
        { KeyEvent.VK_F6,          Event.F6           },
        { KeyEvent.VK_F7,          Event.F7           },
        { KeyEvent.VK_F8,          Event.F8           },
        { KeyEvent.VK_F9,          Event.F9           },
        { KeyEvent.VK_F10,         Event.F10          },
        { KeyEvent.VK_F11,         Event.F11          },
        { KeyEvent.VK_F12,         Event.F12          },
        { KeyEvent.VK_PRINTSCREEN, Event.PRINT_SCREEN },
        { KeyEvent.VK_SCROLL_LOCK, Event.SCROLL_LOCK  },
        { KeyEvent.VK_CAPS_LOCK,   Event.CAPS_LOCK    },
        { KeyEvent.VK_NUM_LOCK,    Event.NUM_LOCK     },
	{ KeyEvent.VK_PAUSE,		Event.PAUSE	},
	{ KeyEvent.VK_INSERT,		Event.INSERT	},

	{ KeyEvent.VK_BACK_SPACE,	MiiEventTypes.Mi_BACKSPACE_KEY	},
	{ KeyEvent.VK_DELETE,		MiiEventTypes.Mi_DELETE_KEY	},
	{ KeyEvent.VK_ENTER,		MiiEventTypes.Mi_ENTER_KEY	},
	{ KeyEvent.VK_TAB,		MiiEventTypes.Mi_TAB_KEY	},

	// JDK 1.2beta2 does not define keycodes for keypad arrows... 
	// so we must manually translate them...
	{ 224,				Event.UP	},
	{ 225,				Event.DOWN	},
	{ 226,				Event.LEFT	},
	{ 227,				Event.RIGHT	}
	};
	private		int		getOldEventKey(KeyEvent e)
		{
		int keyCode = e.getKeyCode();
		for (int i = 0; i < actionKeyCodes.length; i++)
			{
			if (actionKeyCodes[i][0] == keyCode)
				{
				return actionKeyCodes[i][1];
				}
			}
		return((int)e.getKeyChar());
		}
	}


