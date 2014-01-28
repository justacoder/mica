
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
public class MiLConnectionLayout extends MiLayout
	{
	private	static	MiConnectionPointManager connectionPointManagerKind = new MiConnectionPointManager();
	public static final double ERROR_DELTA_FACTOR = 0.000001;
	public static	boolean		debug;


	private		MiPoint 	prevPoint		= new MiPoint();
	private		MiPoint 	currentPoint		= new MiPoint();
	private		MiPoint		prevPrevPoint		= new MiPoint();
	private		MiPoint		newPoint		= new MiPoint();
	private		int		numberOfFixedPts	= 1;



	public				MiLConnectionLayout()
		{
		}

	public		void		setNumberOfFixedPoints(int num)
		{
		numberOfFixedPts = num;
		}
	public		int		getNumberOfFixedPoints()
		{
		return(numberOfFixedPts);
		}

	
	protected	void		doLayout()
		{
		MiConnection conn = (MiConnection )getTarget();
		MiPart graphics = conn.getGraphics();

println("ENTER----->\nLConnLayout = " + conn);
println("LConnLayout graphics = " + graphics);

		if (conn.getSource() != null)
			{
			MiManagedPointManager.getLocationOfManagedPoint(
				conn.getSource(), conn.getSourceConnPt(), 
				newPoint, connectionPointManagerKind);
			graphics.setPoint(0, newPoint);
			}

		if (conn.getDestination() != null)
			{
			MiManagedPointManager.getLocationOfManagedPoint(
				conn.getDestination(), conn.getDestinationConnPt(), 
				newPoint, connectionPointManagerKind);
			graphics.setPoint(-1, newPoint);
			}
println("adj LConnLayout graphics = " + graphics);

/***
		// Must check fixed points because if user moves one then we have 
		// to unfix all subsequent ones and relayout!!!!
		graphics.getPoint(0, prevPoint);
		for (int i = 1; i < numberOfFixedPts; ++i)
			{
			graphics.getPoint(i, currentPoint);
			if ((!areEqual(currentPoint.x, prevPoint.x))
				&& (!areEqual(currentPoint.y, prevPoint.y)))
				{
MiDebug.println("adj LConnLayout graphics numberOfFixedPts = " + numberOfFixedPts);
				numberOfFixedPts = i;
MiDebug.println("adj LConnLayout graphics = " + graphics);
MiDebug.println("NOW adj LConnLayout graphics numberOfFixedPts = " + numberOfFixedPts);
				}
			prevPoint.copy(currentPoint);
			}
***/

		graphics.getPoint(numberOfFixedPts - 1, prevPoint);
		for (int i = numberOfFixedPts; i < graphics.getNumberOfPoints(); ++i)
			{
			graphics.getPoint(i, currentPoint);
println("i = " + i);
println("prevPrevPoint = " + prevPrevPoint);
println("prevPoint = " + prevPoint);
println("currentPoint = " + currentPoint);
println("currentPoint.x = " + currentPoint.x);
println("prevPoint.x = " + prevPoint.x);
println("currentPoint.x == prevPoint.x " + areEqual(currentPoint.x, prevPoint.x));
println("i = " + i + ",numberOfFixedPts = " + numberOfFixedPts);
			if ((!areEqual(currentPoint.x, prevPoint.x))
				&& (!areEqual(currentPoint.y, prevPoint.y)))
				{
println("POJNTS NOT EQUALS: " + i + ",numberOfFixedPts = " + numberOfFixedPts);
				// Do not modify point 0, -1, or any frozen (fixed) points
				if (i > numberOfFixedPts)
					{
					if (areEqual(prevPoint.x, prevPrevPoint.x))
						{
						graphics.setPoint(i - 1, prevPoint.x, currentPoint.y);
						}
					else
						{
						graphics.setPoint(i - 1, currentPoint.x, prevPoint.y);
						}
println("modified prevPoint " + graphics.getPoint(i - 1));
					}
				else
					{
{
println("ADD NEW POINT " + i);
println("prevPoint = " + prevPoint);
println("currentPoint = " + currentPoint);
}
					// Need to add an extra point here to make an 'elbow'
					if ((Math.abs(prevPoint.x - currentPoint.x))
						> (Math.abs(prevPoint.y - currentPoint.y)))
						{
						newPoint.x = currentPoint.x;
						newPoint.y = prevPoint.y;
						}
					else
						{
						newPoint.x = prevPoint.x;
						newPoint.y = currentPoint.y;
						}
					graphics.insertPoint(newPoint.x, newPoint.y, i);
println("ADDED NEW POINT " + newPoint);
					currentPoint.copy(newPoint);
					}
println("graphics = " + graphics);
				}
			// Do not remove frozenPts
			else if ((i > numberOfFixedPts)
				&& ((areEqual(prevPrevPoint.x, currentPoint.x))
				|| (areEqual(prevPrevPoint.y, currentPoint.y))))
				{
				// Need to remove extraneous intermediate point
				graphics.removePoint(i - 1);
println("REMOVE POINT " + (i));
println("graphics = " + graphics);
				--i;
				prevPoint.copy(prevPrevPoint);
				}
/*
			while ((i > numberOfFixedPts)
				&& (areEqual(prevPoint.x, currentPoint.x))
				&& (areEqual(prevPoint.y, currentPoint.y)))
				{
				graphics.removePoint(i - 1);
				--i;
				prevPoint.copy(prevPrevPoint);
				}
*/
			prevPrevPoint.copy(prevPoint);
			prevPoint.copy(currentPoint);
			}
println("LConnLayout graphics = " + graphics);
//MiDebug.printStackTrace();

/**
		graphics.getPoint(0, prevPoint);
		for (int i = 1; i < graphics.getNumberOfPoints(); ++i)
			{
			graphics.getPoint(i, currentPoint);
			if ((!areEqual(currentPoint.x, prevPoint.x))
				&& (!areEqual(currentPoint.y, prevPoint.y)))
				{
MiDebug.println("FAILURE at point: " + i + " LConnLayout graphics = " + graphics);
				}
			prevPoint.copy(currentPoint);
			}
**/
		}
	private	final boolean		areEqual(double a, double b)
		{
		return(com.swfm.mica.util.Utility.equals(a , b));
		// return(((a - b > ERROR_DELTA_FACTOR) || (a - b < -ERROR_DELTA_FACTOR)) ? false : true);
		}

					/**------------------------------------------------------
		 			 * Gets whether this layout knows how to calculate the 
					 * preferred and minimum sizes of the target. If not,
					 * then the getPreferredSize and getMinimumSize methods
					 * do not do anything. Most layouts return true.
					 * @return		true if this layout knows how to
					 *			calculate the referred and minimum 
					 *			sizes of the target
					 * @implements 		MiiLayout#determinesPreferredAndMinimumSizes
					 *------------------------------------------------------*/
	public		boolean		determinesPreferredAndMinimumSizes()
		{
		return(false);
		}
	private		void		println(String msg)
		{
		if (debug)
			{
			MiDebug.println(msg);
			}
		}

	}




