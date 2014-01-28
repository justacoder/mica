
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.FastVector; 
import java.util.Hashtable; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPropertyChange
	{
	private static	FastVector		freePool	= new FastVector();
	private		MiiModelEntity		source;
	private		String			propertyName;
	private		String			propertyValue;
	private		String			oldPropertyValue;
	private		MiValueValidationError	vetoMessage;
	private		MiModelChangePhase	phase;
	private		boolean			vetoed;


	public				MiPropertyChange()
		{
		}
	public				MiPropertyChange(
						MiiModelEntity source, 
						String propertyName, 
						String propertyValue, 
						String oldPropertyValue)
		{
		this.source = source;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
		this.oldPropertyValue = oldPropertyValue;
		}

	public		MiiModelEntity	getSource()
		{
		return(source);
		}
	public		MiModelChangePhase	getPhase()
		{
		return(phase);
		}
	public		void		setPhase(MiModelChangePhase phase)
		{
		this.phase = phase;
		}
	public		void		veto()
		{
		vetoed = true;
		}
	public		boolean		isVetoed()
		{
		return(vetoed);
		}
	public		void		setVetoed(boolean flag)
		{
		vetoed = flag;
		}
	public		void		setVetoMessage(MiValueValidationError msg)
		{
		vetoMessage = msg;
		}
	public		MiValueValidationError getVetoMessage()
		{
		return(vetoMessage);
		}
	public		String		getPropertyName()
		{
		return(propertyName);
		}
	public		void		setPropertyValue(String value)
		{
		propertyValue = value;
		}
	public		String		getPropertyValue()
		{
		return(propertyValue);
		}
	public		void		setOldPropertyValue(String value)
		{
		oldPropertyValue = value;
		}
	public		String		getOldPropertyValue()
		{
		return(oldPropertyValue);
		}
	public static 	MiPropertyChange newPropertyChange(
						MiiModelEntity source,
						String name,
						String value,
						String oldValue)
		{
		MiPropertyChange event = newPropertyChange();
		event.source = source;
		event.propertyName = name;
		event.propertyValue = value;
		event.oldPropertyValue = oldValue;
		return(event);
		}
	public static 	MiPropertyChange	newPropertyChange()
		{
		MiPropertyChange event = null;
		synchronized (freePool)
			{
			if (freePool.size() > 0)
				{
				event = (MiPropertyChange )freePool.lastElement();
				freePool.removeElementAt(freePool.size() - 1);
				}
			else
				{
				event = new MiPropertyChange();
				}
			}
		return(event);
		}

	public static 	void		freePropertyChange(MiPropertyChange event)
		{
		synchronized (freePool)
			{
			if (event == null)
				{
				throw new IllegalArgumentException(
					"Freeing NULL instance of: com.mica.swfm.MiPropertyChange");
				}
			event.source = null;
			event.propertyName = null;
			event.propertyValue = null;
			event.oldPropertyValue = null;
			event.vetoed = false;
			freePool.addElement(event);
			}
		}
	public		String		toString()
		{
		String str = super.toString();
		str += "{" + propertyName + " now= " + propertyValue + "}, was= " + oldPropertyValue;
		str += (vetoed ? (" but was vetoed because: " + vetoMessage) : "");
		str += "[source=" + source + "]";
		return(str);
		}

	}

