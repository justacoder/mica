
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


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiScale
	{
	public static final MiScale	identity 	= new MiScale();
	public		double		x 		= 1.0;
	public		double		y 		= 1.0;



					/**------------------------------------------------------
	 				 * Constructs a new MiScale. 
					 *------------------------------------------------------*/
	public				MiScale()
		{
		}
		
					/**------------------------------------------------------
	 				 * Constructs a new MiScale that is a copy of the given
					 * scale. 
	 				 * @param other 	the scale to copy
					 *------------------------------------------------------*/
	public				MiScale(MiScale other)
		{
		x = other.x;
		y = other.y;
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiScale. 
	 				 * @param x	 	the x component
	 				 * @param y	 	the y component
					 *------------------------------------------------------*/
	public				MiScale(double x, double y)
		{
		this.x = x;
		this.y = y;
		}
		
					/**------------------------------------------------------
	 				 * Set the components of this scale to the given values.
	 				 * @param sx 		the x component
	 				 * @param sy 		the y component
					 *------------------------------------------------------*/
	public		void		set(double sx, double sy)
		{
		x = sx;
		y = sy;
		}

					/**------------------------------------------------------
	 				 * Copies the components of the given scale.
	 				 * @param other 	the scale to copy
					 *------------------------------------------------------*/
	public		void		copy(MiScale other)
		{
		x = other.x;
		y = other.y;
		}
					/**------------------------------------------------------
	 				 * Combines (multiplies) the components of the given scale
					 * with this.
	 				 * @param other 	the scale to combine with this one
					 *------------------------------------------------------*/
	public		void		combine(MiScale other)
		{
		x *= other.x;
		y *= other.y;
		}

					/**------------------------------------------------------
	 				 * Returns whether this equals the given scale.
	 				 * @param other 	the scale to compare this with
	 				 * @return 		true if they are equal
					 *------------------------------------------------------*/
	public		boolean		equals(MiScale other)
		{
		return(com.swfm.mica.util.Utility.areZero(other.x - x, other.y - y));
		}

					/**------------------------------------------------------
	 				 * Set the components of this scale to one.
					 *------------------------------------------------------*/
	public		void		setIsIdentity()
		{
		x = 1.0;
		y = 1.0;
		}
					/**------------------------------------------------------
	 				 * Return whether the components of this scale are equal
					 * to one.
	 				 * @return		true if all components of this 
					 *			scale are equal to one.
					 *------------------------------------------------------*/
	public		boolean		isIdentity()
		{
		return(com.swfm.mica.util.Utility.areZero(x - 1, y - 1));
		}
					/**------------------------------------------------------
	 				 * Returns a copy of this scale that is the inverse of 
					 * this one. Each component of the returned scale equals
					 * 1/component of this scale.
	 				 * @return		the inversed copy of this scale.
					 *------------------------------------------------------*/
	public		MiScale		inverseOf()
		{
		return(new MiScale(1/x, 1/y));
		}
					/**------------------------------------------------------
	 				 * Return text that describes this vector.
					 *------------------------------------------------------*/
	public 		String 		toString()
		{
		return(MiDebug.getMicaClassName(this) + "[x=" + x + ",y=" + y + "]");
		}
	}

