
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
public class MiLConnectionLayout2 extends MiLayout
	{
	private	static	MiConnectionPointManager connectionPointManagerKind = new MiConnectionPointManager();

	private		MiPoint 	prevPoint		= new MiPoint();
	private		MiPoint 	currentPoint		= new MiPoint();
	private		MiPoint		prevPrevPoint		= new MiPoint();
	private		MiPoint		newPoint		= new MiPoint();
	private		int		numberOfFrozenPts	= 1;
	private		MiPoint		tmpPoint1		= new MiPoint();
	private		MiPoint		tmpPoint2		= new MiPoint();
	private		MiDistance	shuntDistance		= 20;



	public				MiLConnectionLayout2()
		{
		}

	
	protected	void		doLayout()
		{
		MiConnection conn = (MiConnection )getTarget();
		MiPart graphics = conn.getGraphics();



//System.out.println("\n LConnLayout graphics = " + graphics);




		int srcConnAngleMask = 0;
		if (conn.getSource() != null)
			{
			srcConnAngleMask = Mi_ALL_DIRECTIONS;
		
			int srcConnPtNumber = conn.getSourceConnPt();
			MiPart source = conn.getSource();
			MiConnectionPointManager sourcePtManager = source.getConnectionPointManager();
			MiManagedPoint sourceManagedPt = MiManagedPointManager.getManagedPoint(	
				source, srcConnPtNumber, connectionPointManagerKind);

			if ((sourcePtManager != null) && (sourceManagedPt != null))
				{
				MiConnectionPointRule sourcePtRule = sourcePtManager.getRule(sourceManagedPt);
				srcConnAngleMask = sourcePtRule.getValidExitDirections(srcConnPtNumber);
				srcConnAngleMask = MiUtility.rotateAndFlipExitDirectionMask(
						srcConnAngleMask, source.getRotation(), source.getFlipped());
				}

			MiManagedPointManager.getLocationOfManagedPoint(
				source, srcConnPtNumber, 
				newPoint, connectionPointManagerKind);
			graphics.setPoint(0, newPoint);
			}





		int destConnAngleMask = 0;
		if (conn.getDestination() != null)
			{
			destConnAngleMask = Mi_ALL_DIRECTIONS;
		
			int destConnPtNumber = conn.getDestinationConnPt();
			MiPart dest = conn.getDestination();
			MiConnectionPointManager destPtManager = dest.getConnectionPointManager();
			MiManagedPoint destManagedPt = MiManagedPointManager.getManagedPoint(	
				dest, destConnPtNumber, connectionPointManagerKind);

			if ((destPtManager != null) && (destManagedPt != null))
				{
				MiConnectionPointRule destPtRule = destPtManager.getRule(destManagedPt);
				destConnAngleMask = destPtRule.getValidExitDirections(destConnPtNumber);
				destConnAngleMask = MiUtility.rotateAndFlipExitDirectionMask(
						destConnAngleMask, dest.getRotation(), dest.getFlipped());
				}

			MiManagedPointManager.getLocationOfManagedPoint(
				dest, destConnPtNumber, 
				newPoint, connectionPointManagerKind);

			graphics.setPoint(-1, newPoint);
			}



		if ((srcConnAngleMask != 0) && (numberOfFrozenPts == 1))
			{
			// Check src exit angle...
			if (!validateExitAngle(graphics, srcConnAngleMask))
				{
				MiPoint adjustmentPoint = new MiPoint();

				// Insert a point at the 'shunt distance' in the valid exit direction
				// that is as close to the original as possible
				getAngleAdjustmentPoint(
						graphics.getPoint(0), 
						graphics.getPoint(1), 
						srcConnAngleMask, 
						adjustmentPoint, 
						shuntDistance);

System.out.println("graphics.getPoint(0) = " + graphics.getPoint(0));
System.out.println("graphics.getPoint(1) = " + graphics.getPoint(1));
System.out.println("adjustmentPoint = " + adjustmentPoint);

				graphics.insertPoint(adjustmentPoint, numberOfFrozenPts);
				++numberOfFrozenPts;
				}
			}



		// ------------------------------------------------------------------
		// Remove any extra points that we may have added if they are no longer useful
		// ------------------------------------------------------------------
		




//System.out.println("adj LConnLayout graphics = " + graphics);


		graphics.getPoint(0, prevPoint);
		for (int i = numberOfFrozenPts; i < graphics.getNumberOfPoints(); ++i)
			{
			graphics.getPoint(i, currentPoint);
//System.out.println("i = " + i);
//System.out.println("prevPrevPoint = " + prevPrevPoint);
//System.out.println("prevPoint = " + prevPoint);
//System.out.println("currentPoint = " + currentPoint);
			if ((currentPoint.x != prevPoint.x)
				&& (currentPoint.y != prevPoint.y))
				{
				// Do not modify point 0, -1, or any frozen (fixed) points
				if (i > numberOfFrozenPts)
					{
					if (prevPoint.x == prevPrevPoint.x)
						{
						graphics.setPoint(i - 1, prevPoint.x, currentPoint.y);
						}
					else
						{
						graphics.setPoint(i - 1, currentPoint.x, prevPoint.y);
						}
					}
				else
					{
//System.out.println("ADD NEW POINT");
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
					currentPoint.copy(newPoint);
					}
//System.out.println("graphics = " + graphics);
				}
			// Do not remove frozenPts
			else if ((i > numberOfFrozenPts)
				&& ((prevPrevPoint.x == currentPoint.x)
				|| (prevPrevPoint.y == currentPoint.y)))
				{
				// Need to remove extraneous intermediate point
				graphics.removePoint(i - 1);
//System.out.println("REMOVE POINT " + (i - 1));
//System.out.println("graphics = " + graphics);
				--i;
				}
			prevPrevPoint.copy(prevPoint);
			prevPoint.copy(currentPoint);
			}
//System.out.println("LConnLayout graphics = " + graphics);
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
	protected	boolean		validateExitAngle(MiPart graphics, int srcConnAngleMask)
		{
		graphics.getPoint(0, tmpPoint1);
		graphics.getPoint(1, tmpPoint2);

		int mask = 0;

		if (tmpPoint2.x > tmpPoint1.x)
			mask |= Mi_RIGHT;
		else if (tmpPoint2.x < tmpPoint1.x)
			mask |= Mi_LEFT;
		
		if (tmpPoint2.y > tmpPoint1.y)
			mask |= Mi_UP;
		else if (tmpPoint2.y < tmpPoint1.y)
			mask |= Mi_DOWN;

//System.out.println("mask of connection for src = " + mask);
//System.out.println("srcConnAngleMask = " + srcConnAngleMask);
		if ((mask & srcConnAngleMask) != mask)
			{
System.out.println("REJECT Cause src mask invalid, mask = " + mask  + ", srcConnAngleMask= " + srcConnAngleMask);
			return(false);
			}
		return(true);
		}

	protected	boolean		validateEntryAngle(MiPart graphics, int destConnAngleMask)
		{
		int numberOfPoints = graphics.getNumberOfPoints();

		graphics.getPoint(numberOfPoints - 1, tmpPoint1);
		graphics.getPoint(numberOfPoints - 2, tmpPoint2);

		int mask = 0;

		if (tmpPoint2.x > tmpPoint1.x)
			mask |= Mi_RIGHT;
		else if (tmpPoint2.x < tmpPoint1.x)
			mask |= Mi_LEFT;
		
		if (tmpPoint2.y > tmpPoint1.y)
			mask |= Mi_UP;
		else if (tmpPoint2.y < tmpPoint1.y)
			mask |= Mi_DOWN;
		
//System.out.println("mask of connection for dest = " + mask);
//System.out.println("destConnAngleMask = " + destConnAngleMask);
		if ((mask & destConnAngleMask) != mask)
			{
System.out.println("REJECT Cause dest mask invalid, mask = " + mask  + ", destConnAngleMask= " + destConnAngleMask);
			return(false);
			}

//System.out.println("ACCEPTED proposed input and output angles");
		return(true);
		}

	protected	void		getAngleAdjustmentPoint(
						MiPoint pt1, 
						MiPoint pt2, 
						int validAngleMask, 
						MiPoint adjustmentPt, 
						MiDistance shuntDistance)
		{
		if (validAngleMask == Mi_RIGHT)
			{
			adjustmentPt.x = pt1.x + shuntDistance;
			adjustmentPt.y = pt1.y;
			return;
			}
		if  (validAngleMask == Mi_LEFT)
			{
			adjustmentPt.x = pt1.x - shuntDistance;
			adjustmentPt.y = pt1.y;
			return;
			}
		if (validAngleMask == Mi_UP)
			{
			adjustmentPt.x = pt1.x;
			adjustmentPt.y = pt1.y + shuntDistance;
			return;
			}
		if (validAngleMask == Mi_DOWN)
			{
			adjustmentPt.x = pt1.x;
			adjustmentPt.y = pt1.y - shuntDistance;
			return;
			}

	
		int mask = 0;

		if (pt2.x > pt1.x)
			mask |= Mi_RIGHT;
		else if (pt2.x < pt1.x)
			mask |= Mi_LEFT;
		
		if (pt2.y > pt1.y)
			mask |= Mi_UP;
		else if (pt2.y < pt1.y)
			mask |= Mi_DOWN;

		if ((((mask & Mi_LEFT) != 0) && ((validAngleMask & Mi_LEFT) == 0))
			|| (((mask & Mi_RIGHT) != 0) && ((validAngleMask & Mi_RIGHT) == 0)))
			{
			if ((mask & Mi_UP) != 0)
				{
				adjustmentPt.x = pt1.x;
				adjustmentPt.y = pt1.y + shuntDistance;
				return;
				}
			adjustmentPt.x = pt1.x;
			adjustmentPt.y = pt1.y - shuntDistance;
			return;
			}

		if ((((mask & Mi_DOWN) != 0) && ((validAngleMask & Mi_DOWN) == 0))
			|| (((mask & Mi_UP) != 0) && ((validAngleMask & Mi_UP) == 0)))
			{
			if ((mask & Mi_LEFT) != 0)
				{
				adjustmentPt.x = pt1.x - shuntDistance;
				adjustmentPt.y = pt1.y;
				return;
				}
			adjustmentPt.x = pt1.x + shuntDistance;
			adjustmentPt.y = pt1.y;
			return;
			}
		throw new RuntimeException(this + ".getExitAngleAdjustmentPoint - unknown mask configuration");
		}
	}




