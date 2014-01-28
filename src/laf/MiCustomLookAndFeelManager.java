
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
import java.applet.AudioClip;
import java.util.Hashtable;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiCustomLookAndFeelManager implements MiiTypes
	{
	private		Hashtable	lookAndFeelPropertyNames;


	public				MiCustomLookAndFeelManager()
		{
		}
	public		void		registerCustomLookAndFeel(String propertyName, MiiCustomLookAndFeel laf)
		{
		if (lookAndFeelPropertyNames == null)
			lookAndFeelPropertyNames = new Hashtable(11);
		lookAndFeelPropertyNames.put(propertyName.toLowerCase(), laf);
		}
	public		void		registerCustomLookAndFeel(String propertyName, String lafClassName)
		{
		if (lookAndFeelPropertyNames == null)
			lookAndFeelPropertyNames = new Hashtable(11);
		lookAndFeelPropertyNames.put(propertyName.toLowerCase(), Utility.makeInstanceOfClass(lafClassName));
		}
	public		boolean		setCustomLookAndFeelPropertyValue(
						MiPart part, String propertyName, String propertyValue)
		{
		if (lookAndFeelPropertyNames == null)
			return(false);

		MiiCustomLookAndFeel laf = (MiiCustomLookAndFeel )
				lookAndFeelPropertyNames.get(propertyName.toLowerCase());
		if (laf == null)
			return(false);

		MiCustomLookAndFeels lafs = part.getCustomLookAndFeels();
		if (lafs == null)
			{
			lafs = new MiCustomLookAndFeels();
			part.setCustomLookAndFeels(lafs);
			}
		else
			{
			String className = laf.getClass().getName();
			for (int i = 0; i < lafs.size(); ++i)
				{
				if (className.equalsIgnoreCase(lafs.elementAt(i).getClass().getName()))
					{
					lafs.elementAt(i).removeCustomLookAndFeel(part);
					lafs.removeElementAt(i);
					--i;
					}
				}
			}
		if ((propertyValue == null) || (propertyValue.equals(Mi_NULL_VALUE_NAME))
			|| (propertyValue.equalsIgnoreCase("null")))
			{
			return(true);
			}

		// of form: soundFilename(action1, action2); soundFilename(event1, event2)
		Strings specs = new Strings();
		specs.appendDelimitedStrings(propertyValue, ';');
		for (int i = 0; i < specs.size(); ++i)
			{
			laf = laf.fromSpecification(specs.elementAt(i));
			lafs.addElement(laf);
			laf.applyCustomLookAndFeel(part);
			}
		return(true);
		}
	public		String		getCustomLookAndFeelPropertyValue(
						MiPart part, String propertyName)
		{
		if (lookAndFeelPropertyNames == null)
			return(null);

		MiiCustomLookAndFeel laf = 
			(MiiCustomLookAndFeel )lookAndFeelPropertyNames.get(propertyName.toLowerCase());
		if (laf == null)
			return(null);

		String className = laf.getClass().getName();

		MiCustomLookAndFeels lafs = part.getCustomLookAndFeels();
		if (lafs == null)
			return(Mi_NULL_VALUE_NAME);

		String value = null;
		for (int i = 0; i < lafs.size(); ++i)
			{
			if (className.equalsIgnoreCase(lafs.elementAt(i).getClass().getName()))
				{
				String spec = lafs.elementAt(i).toSpecification();
				if (value != null)
					value += ";" + spec;
				else 
					value = spec;
				}
			}
		return(value == null ? Mi_NULL_VALUE_NAME : value);
		}
	}

