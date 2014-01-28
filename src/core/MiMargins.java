
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
public class MiMargins
	{
	public		MiDistance	left;
	public		MiDistance	bottom;
	public		MiDistance	right;
	public		MiDistance	top;



					/**------------------------------------------------------
	 				 * Constructs a new MiMargins. 
					 *------------------------------------------------------*/
	public				MiMargins()
		{
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiMargins that is a copy of the given
					 * margin. 
	 				 * @param other 	the margin to copy
					 *------------------------------------------------------*/
	public				MiMargins(MiMargins other)
		{
		copy(other);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiMargins. 
	 				 * @param d	 	the distance assigned to the left
					 *			right, top and bottom margins
					 *------------------------------------------------------*/
	public				MiMargins(MiDistance d)
		{
		setMargins(d);
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiMargins. 
	 				 * @param left	 	the left margin
	 				 * @param bottom 	the bottom margin
	 				 * @param right	 	the right margin
	 				 * @param top	 	the top margin
					 *------------------------------------------------------*/
	public				MiMargins(
						MiDistance left, MiDistance bottom, 
						MiDistance right, MiDistance top)
		{
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.top = top;
		}
					/**------------------------------------------------------
	 				 * Set the margins in all 4 directions to the given value.
	 				 * @param d 		the distance assigned to each margin.
					 *------------------------------------------------------*/
	public		void		setMargins(MiDistance d)
		{
		left = d;
		bottom = d;
		right = d;
		top = d;
		}
					/**------------------------------------------------------
	 				 * Set the margins to the given values.
	 				 * @param left 		the left margin
	 				 * @param bottom 	the bottom margin
	 				 * @param right 	the right margin
	 				 * @param top 		the top margin
					 *------------------------------------------------------*/
	public		void		setMargins(
						MiDistance left, MiDistance bottom, 
						MiDistance right, MiDistance top)
		{
		this.left = left; 
		this.bottom = bottom;
		this.right = right;
		this.top = top;
		}
					/**------------------------------------------------------
	 				 * Adds the components of the given margins to this.
	 				 * @param other 	the margins to add to this one
					 *------------------------------------------------------*/
	public		void		addMargins(MiMargins other)
		{
		left += other.left;
		bottom += other.bottom;
		right += other.right;
		top += other.top;
		}
					/**------------------------------------------------------
	 				 * Adds the distance to each side of these margins.
	 				 * @param margin 	the margins to add to this one
					 *------------------------------------------------------*/
	public		void		addMargins(MiDistance margin)
		{
		left += margin;
		bottom += margin;
		right += margin;
		top += margin;
		}
					/**------------------------------------------------------
	 				 * Subtracts the distance to each side of these margins.
	 				 * @param margin 	the margins to subtract from this one
					 *------------------------------------------------------*/
	public		void		subtractMargins(MiDistance margin)
		{
		left -= margin;
		bottom -= margin;
		right -= margin;
		top -= margin;
		}
					/**------------------------------------------------------
	 				 * Assigns a distance to each side of these margins the 
					 * maximum of each side and the value of the corresponding
					 * side found in the given margins.
	 				 * @param other 	the margins to union with this one
					 *------------------------------------------------------*/
	public		void		union(MiMargins m)
		{
		if (m.left > left)
			left = m.left;
		if (m.bottom > bottom)
			bottom = m.bottom;
		if (m.right > right)
			right = m.right;
		if (m.top > top)
			top = m.top;
		}
					/**------------------------------------------------------
	 				 * Copies the components of the given margins.
	 				 * @param other 	the margins to copy
					 *------------------------------------------------------*/
	public		void		copy(MiMargins other)
		{
		left = other.left;
		bottom = other.bottom;
		right = other.right;
		top = other.top;
		}
					/**------------------------------------------------------
	 				 * Gets whether the given MiMargins is equal to this MiMargins.
	 				 * @param other 	the margins to compare to
					 *------------------------------------------------------*/
	public		boolean		equals(MiMargins other)
		{
		return((left == other.left) && (bottom == other.bottom)
			&& (right == other.right) && (top == other.top));
		}

					/**------------------------------------------------------
	 				 * Returns the combined width of these margins (i.e. 
					 * left + right).
	 				 * @return 	 	the combined width of these margins
					 *------------------------------------------------------*/
	public		MiDistance	getWidth()
		{
		return(left + right);
		}

					/**------------------------------------------------------
	 				 * Returns the combined height of these margins (i.e. 
					 * bottom + top).
	 				 * @return 	 	the combined height of these margins
					 *------------------------------------------------------*/
	public		MiDistance	getHeight()
		{
		return(bottom + top);
		}

					/**------------------------------------------------------
	 				 * Returns the distance assigned to the left side of these
					 * margins.
	 				 * @return 	 	the left side distance
					 *------------------------------------------------------*/
	public		MiDistance	getLeft()
		{
		return(left);
		}
					/**------------------------------------------------------
	 				 * Returns the distance assigned to the right side of these
					 * margins.
	 				 * @return 	 	the right side distance
					 *------------------------------------------------------*/
	public		MiDistance	getRight()
		{
		return(right);
		}
					/**------------------------------------------------------
	 				 * Returns the distance assigned to the top side of these
					 * margins.
	 				 * @return 	 	the top side distance
					 *------------------------------------------------------*/
	public		MiDistance	getTop()
		{
		return(top);
		}
					/**------------------------------------------------------
	 				 * Returns the distance assigned to the bottom side of these
					 * margins.
	 				 * @return 	 	the bottom side distance
					 *------------------------------------------------------*/
	public		MiDistance	getBottom()
		{
		return(bottom);
		}

					/**------------------------------------------------------
	 				 * Return text that describes these margins.
					 *------------------------------------------------------*/
	public 		String 		toString()
		{
		return(MiDebug.getMicaClassName(this) + "[left=" + left + ",bottom=" + bottom + ",right=" + right + ",top=" + top + "]");
		}
	}

