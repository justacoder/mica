
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
import com.swfm.mica.util.FastVector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiCustomLookAndFeel implements MiiCustomLookAndFeel
	{
	private		FastVector		eventHandlers;
	private		FastVector		actionHandlers;
	private		MiAttributes		attributesOverrides;
	private		MiiDeviceRenderer	backgroundRenderer;



	public				MiCustomLookAndFeel()
		{
		}

	public		void		appendEventHandler(MiiEventHandler h)
		{
		if (eventHandlers == null)
			eventHandlers = new FastVector();
		eventHandlers.addElement(h);
		}
	public		void		appendActionHandler(MiiAction h)
		{
		if (actionHandlers == null)
			actionHandlers = new FastVector();
		actionHandlers.addElement(h);
		}

	public		void		setBackgroundRenderer(MiiDeviceRenderer r)
		{
		backgroundRenderer = r;
		}

					// Assign behaviors and appearances
	public		void		applyCustomLookAndFeel(MiPart widget)
		{
		if (eventHandlers != null)
			{
			for (int i = 0 ; i < eventHandlers.size(); ++i)
				{
				widget.appendEventHandler((MiiEventHandler )eventHandlers.elementAt(i));
				}
			}
		if (actionHandlers != null)
			{
			for (int i = 0 ; i < actionHandlers.size(); ++i)
				{
				widget.appendActionHandler((MiiAction )actionHandlers.elementAt(i));
				}
			}
		if (backgroundRenderer != null)
			{
			widget.setBackgroundRenderer(backgroundRenderer);
			}
		}
	public		void		removeCustomLookAndFeel(MiPart widget)
		{
		if (eventHandlers != null)
			{
			for (int i = 0 ; i < eventHandlers.size(); ++i)
				{
				widget.removeEventHandler((MiiEventHandler )eventHandlers.elementAt(i));
				}
			}
		if (actionHandlers != null)
			{
			for (int i = 0 ; i < actionHandlers.size(); ++i)
				{
				widget.removeActionHandler((MiiAction )actionHandlers.elementAt(i));
				}
			}
		if (backgroundRenderer != null)
			{
			widget.setBackgroundRenderer(null);
			}
		}
	public		String			toSpecification()
		{
		return(getClass().getName());
		}

	public		MiiCustomLookAndFeel	fromSpecification(String spec)
		{
		return(new MiCustomLookAndFeel());
		}
	}

