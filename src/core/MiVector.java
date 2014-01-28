
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
public class MiVector
	{
	public		MiDistance	x;
	public		MiDistance	y;



					/**------------------------------------------------------
	 				 * Constructs a new MiVector. 
					 *------------------------------------------------------*/
	public				MiVector()
		{
		}

					/**------------------------------------------------------
	 				 * Constructs a new MiVector that is a copy of the given
					 * vector. 
	 				 * @param other 	the vector to copy
					 *------------------------------------------------------*/
	public				MiVector(MiVector other)
		{
		x = other.x;
		y = other.y;
		}

					/**------------------------------------------------------
	 				 * Constructs a new MiVector. 
	 				 * @param x	 	the x component
	 				 * @param y	 	the y component
					 *------------------------------------------------------*/
	public				MiVector(MiCoord x, MiCoord y)
		{
		this.x = x;
		this.y = y;
		}
					/**------------------------------------------------------
	 				 * Set the components of this vector to the given values.
	 				 * @param vx 		the x component
	 				 * @param vy 		the y component
					 *------------------------------------------------------*/
	public 		void		set(MiDistance vx, MiDistance vy)
		{
		x = vx;
		y = vy;
		}
					/**------------------------------------------------------
	 				 * Adds the components of the given vector to this.
	 				 * @param other 	the vector to add to this one
					 *------------------------------------------------------*/
	public		void		add(MiVector other)
		{
		x += other.x;
		y += other.y;
		}
					/**------------------------------------------------------
	 				 * Adds the given components to this vector.
	 				 * @param tx 		the x component to add to this
	 				 * @param ty 		the y component to add to this
					 *------------------------------------------------------*/
	public		void		add(MiDistance tx, MiDistance ty)
		{
		x += tx;
		y += ty;
		}
					/**------------------------------------------------------
	 				 * Copies the components of the given vector.
	 				 * @param other 	the vector to copy
					 *------------------------------------------------------*/
	public		void		copy(MiVector other)
		{
		x = other.x;
		y = other.y;
		}
					/**------------------------------------------------------
	 				 * Returns whether this equals the given vector.
	 				 * @param other 	the vector to compare this with
	 				 * @return 		true if they are equal
					 *------------------------------------------------------*/
	public		boolean		equals(MiVector other)
		{
		return(com.swfm.mica.util.Utility.areZero(other.x - x, other.y - y));
		}

					/**------------------------------------------------------
	 				 * Set the components of this vector to zero.
					 *------------------------------------------------------*/
	public 		void		zeroOut()
		{
		x = 0;
		y = 0;
		}
					/**------------------------------------------------------
	 				 * Return whether the components of this vector are zero.
	 				 * @return		true if all components of this 
					 *			vector are zero.
					 *------------------------------------------------------*/
	public 		boolean		isZero()
		{
		return(com.swfm.mica.util.Utility.areZero(x, y));
		}
					/**------------------------------------------------------
	 				 * Return text that describes this vector.
					 *------------------------------------------------------*/
	public 		String 		toString()
		{
		return(MiDebug.getMicaClassName(this) + "[x=" + Utility.toShortString(x) + ",y=" + Utility.toShortString(y) + "]");
		}
	}

