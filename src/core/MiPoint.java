
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
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiPoint
	{
	public		MiCoord		x;
	public		MiCoord		y;



					/**------------------------------------------------------
	 				 * Constructs a new MiPoint. 
					 *------------------------------------------------------*/
	public				MiPoint()
		{
		}
		
					/**------------------------------------------------------
	 				 * Constructs a new MiPoint that is a copy of the given
					 * point. 
	 				 * @param other 	the point to copy
					 *------------------------------------------------------*/
	public				MiPoint(MiPoint other)
		{
		x = other.x;
		y = other.y;
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiPoint. 
	 				 * @param x	 	the x coordinate
	 				 * @param y	 	the y coordinate
					 *------------------------------------------------------*/
	public				MiPoint(MiCoord x, MiCoord y)
		{
		this.x = x;
		this.y = y;
		}
					/**------------------------------------------------------
	 				 * Set the coordinates of this point to the given values.
	 				 * @param px 		the x coordinate
	 				 * @param py 		the y coordinate
					 *------------------------------------------------------*/
	public		void		set(MiCoord px, MiCoord py)
		{
		x = px;
		y = py;
		}
					/**------------------------------------------------------
	 				 * Copies the coordinates of the given point.
	 				 * @param other 	the point to copy
					 *------------------------------------------------------*/
	public		void		copy(MiPoint other)
		{
		x = other.x;
		y = other.y;
		}
					/**------------------------------------------------------
	 				 * Translates the components of the this point by the
					 * given vector.
	 				 * @param other 	the vector to translate this by
					 *------------------------------------------------------*/
	public		void		translate(MiVector other)
		{
		x += other.x;
		y += other.y;
		}
					/**------------------------------------------------------
	 				 * Translates the components of the this point by the
					 * given vector.
	 				 * @param tx	 	the x amount to translate this by
	 				 * @param ty	 	the y amount to translate this by
					 *------------------------------------------------------*/
	public		void		translate(MiDistance tx, MiDistance ty)
		{
		x += tx;
		y += ty;
		}
					/**------------------------------------------------------
	 				 * Scales the components of the this point by the
					 * given scale factor around the given center.
	 				 * @param center	the center to scale around
	 				 * @param scale	 	the scale factor
					 *------------------------------------------------------*/
	public		void		scale(MiPoint center, MiScale scale)
		{
		x = (x - center.x) * scale.x + center.x;
		y = (y - center.y) * scale.y + center.y;
		}
					/**------------------------------------------------------
	 				 * Returns whether this equals the given point.
	 				 * @param other 	the point to compare this with
	 				 * @return 		true if they are equal
					 *------------------------------------------------------*/
	public		boolean		equals(MiPoint other)
		{
		return(com.swfm.mica.util.Utility.areZero(other.x - x, other.y - y));
		}


					/**------------------------------------------------------
	 				 * Gets the square of the distance between this point and
					 * teh given point. This is useful when find the smallest
					 * distance between this point and a set of other points.
	 				 * @param other 	the point to get the distance to
	 				 * @return 		the distance squared to the other
					 *			point
					 *------------------------------------------------------*/
	public		double		getDistanceSquared(MiPoint other)
		{
		return(((double )(other.x - x))*(other.x - x) + ((double )(other.y - y))*(other.y - y));
		}

					/**------------------------------------------------------
	 				 * Return text that describes this point.
					 *------------------------------------------------------*/
	public 		String 		toString()
		{
		return(MiDebug.getMicaClassName(this) 
			+ "[x=" + Utility.toShortString(x) 
			+ ",y=" + Utility.toShortString(y) + "]");
		}
	}

