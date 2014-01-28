
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility;


/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
class MiAttributeCache 
	{
	//private static final int	HASH_TABLE_SIZE = 211;
	//private static final int	HASH_TABLE_SIZE = 503;
	private static final int	HASH_TABLE_SIZE = 1017;
	private		FastVector[]	cache = new FastVector[HASH_TABLE_SIZE];
	private		int		numRequests = 0;
	private		int		numCopied = 0;
	private		int		numRemoved = 0;

					/**------------------------------------------------------
	 				 * Constructs a new MiAttributeCache.
					 *------------------------------------------------------*/
	public				MiAttributeCache()
		{
		}
					/**------------------------------------------------------
					 * Find and return exact duplicate of the given object. If
					 * not found, make a copy of the given object and add it to
					 * the cache and then return the copy.
	 				 * @param obj		the object to get an exact copy of,
					 *			hopefully from the cache.
					 *------------------------------------------------------*/
	public		Object		getCopyFromUniqueObjectsPool(MiiCopyable obj)
		{
		int hash = (obj.hashCode() & 0x7fffffff) % HASH_TABLE_SIZE;
		FastVector vector = cache[hash];
		if (vector == null)
			{
			vector = new FastVector();
			cache[hash] = vector;
			}
		for (int i = 0; i < vector.size(); ++i)
			{
			// If the two object's contents are equal but they are not the same object...
			Object other = vector.elementAt(i);

			if ((obj != other) && (obj.equals(other)))
				{
				++numRequests;
				return(other);
				}
			}

		Object theCopy = obj.makeCopy();
		++numRequests;
		++numCopied;
		vector.addElement(theCopy);
		return(theCopy);
		}
	public		void		removeCopyFromUniqueObjectsPool(MiiCopyable obj)
		{
		int hash = (obj.hashCode() & 0x7fffffff) % HASH_TABLE_SIZE;
		FastVector vector = cache[hash];
		if (vector != null)
			{
			if (!vector.removeElement(obj))
				{
				//Wasn't put into cache... ? MiDebug.println("Could not remove attribute object - did not find attribute in cache" + obj);
				}
			else
				{
				++numRemoved;
				}
			}
		}

	public		int		getNumberOfCachedAttributes()
		{
		int num = 0;
		for (int i = 0; i < cache.length; ++i)
			{
			if (cache[i] != null)
				num += cache[i].size();
			}
		return(num);
		}
		
					/**------------------------------------------------------
					 * Returns information about all of the cached attributes.
					 * Information includes: the number of requests, number of
					 * copies, the number of attributes in each bucket in the
					 * hash table cache, number of duplications, the number
					 * and contents of unique values of each attribute type.
					 * @return		the textual information
					 *------------------------------------------------------*/
	public		String		toString()
		{
		StringBuffer buf = new StringBuffer(10000);
		buf.append("Number requests for an attributes object: " + numRequests + "\n");
		buf.append("Number copies made of an attributes object: " + numCopied + "\n");
		buf.append("Number attributes objects removed from cache: " + numRemoved + "\n");
		FastVector vector = new FastVector();
		for (int i = 0; i < cache.length; ++i)
			{
			buf.append(" ");
			if (cache[i] != null)
				{
				buf.append(cache[i].size());
				for (int j = 0 ; j < cache[i].size(); ++j)
					vector.addElement(cache[i].elementAt(j));
				}
			else
				buf.append("0");
			buf.append("\n");
			}
		// Sanity Check...
		int numDuplicates = 0;
		for (int i = 0; i < vector.size(); ++i)
			{
			Object attr = vector.elementAt(i);
			for (int j = i + 1; j < vector.size(); ++j)
				{
				if (attr.equals(vector.elementAt(j)))
					{
					++numDuplicates;
					}
				}
			}
		buf.append("Number duplicated attributes objects: " + numDuplicates + "\n");

		// ----------------------------------
		// Print all values of each attribute
		// ---------------------------------
		Strings diffAttValues = new Strings();
		for (int attIndex = 0; 
			attIndex < ((MiGeneralAttributes )vector.elementAt(0)).getNumberOfAttributes();
			++attIndex)
			{
			diffAttValues.removeAllElements();
			MiGeneralAttributes attr = (MiGeneralAttributes )vector.elementAt(0);
			Object value = attr.getAttributeValueAsObject(attIndex);
			diffAttValues.addElement(value);
			for (int i = 1; i < vector.size(); ++i)
				{
				attr = (MiGeneralAttributes )vector.elementAt(i);
				value = attr.getAttributeValueAsObject(attIndex);
				if (!diffAttValues.contains(value))
					diffAttValues.addElement(value);
				}
			buf.append("The (" + diffAttValues.size() + ") values of attribute: \""
				+ attr.getNameOfAttribute(attIndex) + "\" are:\n"); 
			Utility.sortAlphabetically(diffAttValues);
			for (int i = 0; i < diffAttValues.size(); ++i)
				{
				buf.append("   ");
				buf.append(diffAttValues.elementAt(i) + "\n");
				}
			buf.append("\n");
			}
		return(buf.toString());
		}
	}


