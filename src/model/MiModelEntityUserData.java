
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
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiModelEntityUserData
	{
	private		Hashtable		resources;

	public					MiModelEntityUserData()
		{
		}
	public		MiModelEntityUserData	copy()
		{
		MiModelEntityUserData theCopy = new MiModelEntityUserData();

		if (resources != null)
			theCopy.resources = (Hashtable )resources.clone();

		return(theCopy);
		}
					/**------------------------------------------------------
	 				 * Sets (or adds if not already present) the value of
					 * the named resource. These resources are to be specified
					 * by the programmer - these are not built-in resources.
	 				 * @param name 		the name of the resource
	 				 * @param value 	the value of the resource
					 * @see			#getResource
					 * @see			#removeResource
					 *------------------------------------------------------*/
	public		void		setResource(String name, Object value)
		{
		if (resources == null)
			resources = new Hashtable(3);
		if (value == null)
			removeResource(name);
		else
			resources.put(name, value);
		}
					/**------------------------------------------------------
	 				 * Gets the value of the named resource. 
	 				 * @param name 		the name of the resource
	 				 * @return 	 	the value of the resource, or null
					 *			if no such resource exists
					 * @see			#setResource
					 *------------------------------------------------------*/
	public		Object		getResource(String name)
		{
		if (resources == null)
			return(null);
		return(resources.get(name));
		}
					/**------------------------------------------------------
	 				 * Removes the named resource. This is the same as setting
					 * the resource's value to null.
	 				 * @param name 		the name of the resource
					 * @see			#setResource
					 *------------------------------------------------------*/
	public		void		removeResource(String name)
		{
		if (resources != null)
			resources.remove(name);
		}
					/**------------------------------------------------------
	 				 * Gets the number of resources assigned to this MiPart. 
	 				 * @return 		the number of resources
					 * @see			#getResourceName
					 *------------------------------------------------------*/
	public		int		getNumberOfResources()
		{
		if (resources != null)
			return(resources.size());
		return(0);
		}
					/**------------------------------------------------------
	 				 * Gets the name of the ith resource assigned to this 
					 * MiPart. 
	 				 * @param index		the index of the resource
	 				 * @return 		the name of the resource
					 * @exception		IllegalArgumentException If index
					 * 			is out of range.
					 * @see			#getNumberOfResources
					 *------------------------------------------------------*/
	public		String		getResourceName(int index)
		{
		Enumeration keys = resources.keys();
		int i = 0;
		while (keys.hasMoreElements())
			{
			Object obj = keys.nextElement();
			if (i == index)
				return((String )obj);
			++i;
			}
		throw new IllegalArgumentException(MiDebug.getMicaClassName(this) 
			+ ": Resource index: " + index + " > number of resources: " + resources.size());
		}
	public		String		toString()
		{
		String str = "{ ";
		if (resources != null)
			{
			Enumeration keys = resources.keys();
			while (keys.hasMoreElements())
				{
				String name = (String )keys.nextElement();
				Object value = resources.get(name);
				str += "[" + name + "=" + value + "]";
				}
			}
		str += "  }";
		return(str);
		}
	}



