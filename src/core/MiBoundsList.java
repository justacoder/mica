
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
import com.swfm.mica.util.CacheVector;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
class MiBoundsList extends CacheVector
	{
	private		MiBounds 	tmpBounds = new MiBounds();
	private		MiBounds 	tmpBounds2 = new MiBounds();


	public				MiBoundsList()
		{
if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_BASIC_ALLOCATIONS))
MiDebug.printStackTrace("MiDebug.TRACE_BASIC_ALLOCATIONS");
		}

	public				MiBoundsList(MiBounds b)
		{
if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_BASIC_ALLOCATIONS))
MiDebug.printStackTrace("MiDebug.TRACE_BASIC_ALLOCATIONS");
		addElement(b);
		}


	public		void		getTotalBounds(MiBounds b)
		{
		b.reverse();
		for (int i = 0; i < size(); ++i)
			{
			b.union((MiBounds )elementAt(i));
			}
		}
	// The bounds in the given list are copied and appended to this one
	public		void		append(MiBoundsList list)
		{
		for (int i = 0; i < list.size(); ++i)
			{
			addElement(list.elementAt(i));
			}
		}
	public		void		clip(MiBounds clipBounds)
		{
		for (int i = 0; i < size(); ++i)
			{
			if (!elementAt(i).intersectionWith(clipBounds))
				{
				removeElementAt(i);
				--i;
				}
			}
		}
			
	public		void		exclude(MiBoundsList omitBounds, MiBoundsList results)
		{
		results.append(this);
		if (omitBounds.size() == 0)
			{
			return;
			}
		MiBounds intersection = tmpBounds2;
		for (int j = 0; j < omitBounds.size(); ++j)
			{
			MiBounds omit = omitBounds.elementAt(j);
			int numItems = results.size();
			for (int i = 0; i < numItems; ++i)
			    {
			    MiBounds b = (MiBounds )results.elementAt(i);
			    intersection.copy(b);
			    if (intersection.intersectionWith(omit))
			    	{
				// Add these two lines cause bounds at index i, will be recycled in useNextElement
				tmpBounds.setBounds(b);
				b = tmpBounds;

				results.removeElementAt(i);
				--i;
				--numItems;

				// TOP
				if (intersection.ymax < b.ymax)
					results.useNextElement().setBounds(b.xmin, intersection.ymax, b.xmax, b.ymax);
				// RIGHT
				if (intersection.xmax < b.xmax)
					results.useNextElement().setBounds(intersection.xmax, intersection.ymin, b.xmax, intersection.ymax);
				// BOTTOM
				if (intersection.ymin > b.ymin)
					results.useNextElement().setBounds(b.xmin, b.ymin, b.xmax, intersection.ymin);
				// LEFT
				if (intersection.xmin > b.xmin)
					results.useNextElement().setBounds(b.xmin, intersection.ymin, intersection.xmin, intersection.ymax);
				}
			    }
			}
		}
	public		void		removeRedundancies()
		{
		for (int i = 0; i < size(); ++i)
			{
			MiBounds bounds = elementAt(i);
			for (int j = i + 1; j < size(); ++j)
				{
				MiBounds jBounds = elementAt(j);
				if (jBounds.intersectsIncludingEdges(bounds))
					{
					if (bounds.isContainedIn(jBounds))
						{
						removeElementAt(i);
						--i;
						break;
						}
					else if (jBounds.isContainedIn(bounds))
						{
						removeElementAt(j);
						--j;
						}
					else if (jBounds.equals(bounds))
						{
						removeElementAt(j);
						--j;
						}
					}
				}
			}
		}
	public		void		optimize()
		{
		for (int i = 0; i < size(); ++i)
			{
			MiBounds bounds = elementAt(i);
			for (int j = i + 1; j < size(); ++j)
				{
				MiBounds jBounds = elementAt(j);
				if (jBounds.intersectsIncludingEdges(bounds))
					{
					bounds.union(jBounds);
					removeElementAt(j);
					--j;
					}
				}
			}
		}
	public		void		dtow(MiiTransform transform)
		{
		for (int i = 0; i < size(); ++i)
			{
			MiBounds b = elementAt(i);
			transform.dtow(b, b);
			}
		}
	public		void		wtod(MiiTransform transform)
		{
		for (int i = 0; i < size(); ++i)
			{
			MiBounds b = elementAt(i);
			transform.wtod(b, b);
			}
		}
	public		void		dtow(MiiTransform transform, MiBoundsList results)
		{
		for (int i = 0; i < size(); ++i)
			{
			transform.dtow(elementAt(i), results.useNextElement());
			}
		}
	public		void		wtod(MiiTransform transform, MiBoundsList results)
		{
		for (int i = 0; i < size(); ++i)
			{
			transform.wtod(elementAt(i), results.useNextElement());
			}
		}
	protected	void		expandToIncludeDeviceTargetArea(MiiTransform transform)
		{
		for (int i = 0; i < size(); ++i)
			{
			MiBounds bounds = elementAt(i);
			transform.wtod(bounds, tmpBounds);
			tmpBounds.xmin = (int )tmpBounds.xmin;
			tmpBounds.ymin = (int )tmpBounds.ymin;
			tmpBounds.xmax = ((double )((int )tmpBounds.xmax)) + 0.99999;
			tmpBounds.ymax = ((double )((int )tmpBounds.ymax)) + 0.99999;
			transform.dtow(tmpBounds, bounds);
			}
		}
	public		void		addElement(MiBounds bounds)
		{
		((MiBounds )useTheNextElement()).copy(bounds);
		}
	public		MiBounds	elementAt(int index)
		{
		return((MiBounds )theElementAt(index));
		}
	public		MiBounds	useNextElement()
		{
		MiBounds b = (MiBounds )useTheNextElement();
		b.reverse();
		return(b);
		}
	public		Object		makeAnElement()
		{
if (MiDebug.debug && MiDebug.isTracing(null, MiDebug.TRACE_BASIC_ALLOCATIONS))
{
MiDebug.println("makeAnElement: size = " + size());
MiDebug.println("cacheSize = " + cacheSize());
}
		return(new MiBounds());
		}
	}

