
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

import java.util.Vector; 
import java.awt.Color; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPieChart extends MiWidget
	{
	private		Vector		slices		= new Vector();
	private		double		perspective	= 1.25;
	private		int		depth		= 10;
	private		MiBounds	tmpBounds	= new MiBounds();

	public				MiPieChart()
		{
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}

	public		void		setDepth(int depth)
		{
		this.depth = depth;
		invalidateArea();
		}
	public		int		getDepth()
		{
		return(depth);
		}

	public		void		setPerspective(double perspective)
		{
		this.perspective = perspective;
		invalidateArea();
		}
	public		double		getPerspective()
		{
		return(perspective);
		}
	public		void		addSlice(String name, Color color, double percent)
		{
		slices.addElement(new MiPieSlice(name, color, percent));
		invalidateArea();
		}
	public		void		removeAllSlices()
		{
		slices.removeAllElements();
		invalidateArea();
		}

	public		void		setSliceAmount(String name, double amount)
		{
		MiPieSlice slice = getSlice(name);
		if (slice == null)
			throw new IllegalArgumentException("No slice found with name: " + name);
		slice.percent = amount;
		invalidateArea();
		}
	public		double		getSliceAmount(String name)
		{
		if (getSlice(name) == null)
			throw new IllegalArgumentException("No slice found with name: " + name);
		return(getSlice(name).percent);
		}
	
	protected	MiPieSlice	getSlice(String name)
		{
		for (int i = 0; i < slices.size(); ++i)
			{
			MiPieSlice slice = (MiPieSlice )slices.elementAt(i);
			if (slice.name.equalsIgnoreCase(name))
				return(slice);
			}
		return(null);
		}

	public		void		render(MiRenderer renderer)
		{
		super.render(renderer);

		renderer.setAttributes(getAttributes());

		double startAngle = 0;
		double angleSwept = 0;
		MiBounds b = getBounds(tmpBounds);
		MiDistance pHeight = (b.getHeight() - depth)/perspective + depth;
		MiDistance topAndBottomMargin = (b.getHeight() - pHeight)/2;
		b.setYmax(b.getYmax() - topAndBottomMargin);
		b.setYmin(b.getYmin() + topAndBottomMargin + depth);
		MiCoord ymin = b.getYmin();
		MiCoord ymax = b.getYmax();
		int numSlices = slices.size();
                for (int i = depth; i > 0; i--)
			{
                        startAngle = -45;
                        for(int j = 0; j < numSlices; j++)
				{
				MiPieSlice slice = (MiPieSlice )slices.elementAt(j);
				renderer.setBackgroundColor(slice.color.darker());
				angleSwept = slice.percent * 360;
				b.setYmin(ymin - i);
				b.setYmax(ymax - i);
				renderer.drawCircularArc(b, startAngle, angleSwept);
                                startAngle = startAngle + angleSwept;
				}
                        }
                startAngle = -45;
		b.setYmin(ymin);
		b.setYmax(ymax);
                for(int i = 0; i < numSlices; i++)
			{
			MiPieSlice slice = (MiPieSlice )slices.elementAt(i);
			renderer.setBackgroundColor(slice.color);
			angleSwept = slice.percent * 360;
			renderer.drawCircularArc(b, startAngle, angleSwept);
			// If arc is big enough put text label in slice, otherwise outside of slice.
                        startAngle = startAngle + angleSwept;
			}
		// Reset renderer because we changed the renderer's current colors
		renderer.setAttributes(null);
                }
	}
class MiPieSlice
	{
	String	name;
	Color	color;
	double	percent;

	public				MiPieSlice(String name, Color color, double percent)
		{
		this.name = name;
		this.color = color;
		this.percent = percent;
		}
	}


