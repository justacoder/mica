
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
import com.swfm.mica.*; 
import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.TypedVector;
import java.awt.Color;
import java.util.Hashtable;
import java.util.Vector;


/**----------------------------------------------------------------------------------------------
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelEntityReference extends MiModelEntity implements MiiModelEntityReference
	{
	private		boolean		referencedIsReadOnly;
	private		MiiModelEntity	referenced;

	public				MiModelEntityReference(MiiModelEntity referenced)
		{
		this(referenced, false);
		}
	public				MiModelEntityReference(MiiModelEntity referenced, boolean referencedIsReadOnly)
		{
		this.referenced = referenced;
		this.referencedIsReadOnly = referencedIsReadOnly;
		}

	public		MiiModelEntity	copy()
		{
		MiModelEntityReference c =  new MiModelEntityReference(referenced, referencedIsReadOnly);
		MiPropertyDescriptions descs = super.getPropertyDescriptions();
		c.copy(this);
		c.setPropertyDescriptions(descs);
		return(c);
		}

	public		MiiModelEntity	deepCopy()
		{
		MiModelEntityReference c =  new MiModelEntityReference(referenced, referencedIsReadOnly);
		MiPropertyDescriptions descs = super.getPropertyDescriptions();
		c.deepCopy(this);
		c.setPropertyDescriptions(descs);
		return(c);
		}

	public		MiiModelEntity	getReferenced()
		{
		return(referenced);
		}
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if ((super.getPropertyDescriptions() == null)
			&& (referenced.getPropertyDescriptions() == null))
			{
			return(null);
			}

		MiPropertyDescriptions descriptions = new MiPropertyDescriptions();
		if (super.getPropertyDescriptions() != null)
			{
			descriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());
			}
		if (referenced.getPropertyDescriptions() != null)
			{
			descriptions.appendPropertyDescriptionComponent(referenced.getPropertyDescriptions());
			}
		return(descriptions);
		}
	public		MiPropertyDescriptions	getReferencePropertyDescriptions()
		{
		return(super.getPropertyDescriptions());
		}
	public		void		setPropertyDescriptions(MiPropertyDescriptions descs)
		{
		super.setPropertyDescriptions(descs);
		}
	public		Strings		getPropertyNames()
		{
		Strings names = super.getPropertyNames();
		names.append(referenced.getPropertyNames());
		names.removeDuplicates();
		return(names);
		}
	public		String		getPropertyValue(String name)
		{
		Strings names = super.getPropertyNames();
		if (!names.contains(name))
			{
			return(referenced.getPropertyValue(name));
			}
		return(super.getPropertyValue(name));
		}
	public		void		setPropertyValue(String name, String value)
		{
		if (!referencedIsReadOnly)
			{
			Strings names = referenced.getPropertyNames();
			if ((value == null) || (names.contains(name)))
				{
				referenced.setPropertyValue(name, value);
				}
			}
		super.setPropertyValue(name, value);
		}
	}


