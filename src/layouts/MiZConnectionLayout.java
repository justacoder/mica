
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
public class MiZConnectionLayout extends MiLayout
	{
	private	static	MiConnectionPointManager connectionPointManagerKind = new MiConnectionPointManager();
	private		MiPoint		srcConnPt		= new MiPoint();
	private		MiPoint		destConnPt		= new MiPoint();
	private		MiPoint		srcConnShuntPt		= new MiPoint();
	private		MiPoint		destConnShuntPt		= new MiPoint();
	private		MiPoint 	tmpPoint1		= new MiPoint();
	private		MiPoint		tmpPoint2		= new MiPoint();
	private		MiBounds	srcNodeDrawBounds	= new MiBounds();
	private		MiBounds	destNodeDrawBounds	= new MiBounds();
	private		boolean		srcPointHasShunt;
	private		boolean		destPointHasShunt;
	private		MiDistance	Mi_TMP_SHUNT_DISTANCE	= 10;
	private		MiDistance	Mi_DEFAULT_CELL_MARGINS	= 7; // Needs to be less than shunt distance



	public				MiZConnectionLayout()
		{
		setCellMargins(new MiMargins(Mi_DEFAULT_CELL_MARGINS));
		}

	
	protected	void		doLayout()
		{
		MiConnection conn = (MiConnection )getTarget();

		MiPart source = conn.getSource();
		MiPart destination = conn.getDestination();
		//if (((source != null) && (destination != null)) || (!conn.getConnectionsMustBeConnectedAtBothEnds()))
			{
			int 			srcConnAngleMask;
			int 			destConnAngleMask;
			int 			srcConnPtNumber		= conn.getSourceConnPt();
			int 			destConnPtNumber	= conn.getDestinationConnPt();
			MiConnectionPointManager sourcePtManager 	= null;
			MiConnectionPointManager destPtManager 		= null;
			MiManagedPoint 		sourceManagedPt 	= null;
			MiManagedPoint 		destManagedPt 		= null;
			MiConnectionPointRule	sourcePtRule		= null;
			MiConnectionPointRule 	destPtRule 		= null;


			MiPart graphics = conn.getGraphics();

			srcConnAngleMask = Mi_ALL_DIRECTIONS;
			graphics.getPoint(0, srcConnShuntPt);
			graphics.getPoint(0, srcConnPt);

			if (source != null)
				{
				source.getDrawBounds(srcNodeDrawBounds);
				srcNodeDrawBounds.addMargins(getCellMargins());

				sourcePtManager = source.getConnectionPointManager();
				sourceManagedPt = MiManagedPointManager.getManagedPoint(	
						source, srcConnPtNumber, connectionPointManagerKind);

//System.out.println("sourcePtManager = " + sourcePtManager);
//System.out.println("sourceManagedPt = " + sourceManagedPt);
//System.out.println("srcConnPtNumber = " + srcConnPtNumber);
//System.out.println("source = " + source);

				srcConnPt = MiManagedPointManager.getLocationOfManagedPoint(
					source, srcConnPtNumber, srcConnPt, connectionPointManagerKind);
				srcConnShuntPt.copy(srcConnPt);

				if ((sourcePtManager != null) && (sourceManagedPt != null))
					{
					sourcePtRule = sourcePtManager.getRule(sourceManagedPt);
					srcConnAngleMask = sourcePtRule.getValidExitDirections(srcConnPtNumber);
					srcConnAngleMask = MiUtility.rotateAndFlipExitDirectionMask(
						srcConnAngleMask, source.getRotation(), source.getFlipped());
//System.out.println("srcConnAngleMask = " + srcConnAngleMask);
					sourcePtRule.getShuntPointLocation(
						sourceManagedPt, source, srcConnPtNumber, srcConnShuntPt);
					}
				}

			destConnAngleMask = Mi_ALL_DIRECTIONS;
			graphics.getPoint(-1, destConnShuntPt);
			graphics.getPoint(-1, destConnPt);

			if (destination != null)
				{
				destination.getDrawBounds(destNodeDrawBounds);
				destNodeDrawBounds.addMargins(getCellMargins());

				destPtManager = destination.getConnectionPointManager();
				destManagedPt = MiManagedPointManager.getManagedPoint(	
						destination, destConnPtNumber, connectionPointManagerKind);

				destConnPt = MiManagedPointManager.getLocationOfManagedPoint(
						destination, destConnPtNumber, destConnPt, 
						connectionPointManagerKind);
				destConnShuntPt.copy(destConnPt);

				if ((destPtManager != null) && (destManagedPt != null))
					{
					destPtRule = destPtManager.getRule(destManagedPt);
					destConnAngleMask = destPtRule.getValidExitDirections(destConnPtNumber);
					destConnAngleMask = MiUtility.rotateAndFlipExitDirectionMask(
						destConnAngleMask, destination.getRotation(), destination.getFlipped());
					destPtRule.getShuntPointLocation(
						destManagedPt, destination, destConnPtNumber, destConnShuntPt);
					}
				}
			if ((srcConnPt.x == destConnPt.x) || (srcConnPt.y == destConnPt.y)
				&& (source != destination))
				{
				graphics.setNumberOfPoints(2);
				graphics.setPoint(0, srcConnPt);
				graphics.setPoint(1, destConnPt);
//System.out.println("Layout return");
				return;
				}

			graphics.setPoint(0, srcConnPt);
			graphics.setPoint(-1, destConnPt);
			if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
				&& (validateLineNotOverlappingNodes(graphics, source, destination)))
				{
				graphics.getPoint(0, tmpPoint1);
				boolean layoutNeeded = false;
				for (int i = 1; i < graphics.getNumberOfPoints(); ++i)
					{
					graphics.getPoint(i, tmpPoint2);
					if ((tmpPoint1.x != tmpPoint2.x) && (tmpPoint1.y != tmpPoint2.y))
						{
//System.out.println("i = " + i);
//System.out.println("tmpPoint1.x = " + tmpPoint1.x);
//System.out.println("tmpPoint1.y = " + tmpPoint1.y);
//System.out.println("tmpPoint2.x = " + tmpPoint2.x);
//System.out.println("tmpPoint2.y = " + tmpPoint2.y);
//System.out.println("layoutNeeded!!!!");
						layoutNeeded = true;
						break;
						}

					tmpPoint1.x = tmpPoint2.x;
					tmpPoint1.y = tmpPoint2.y;
					}
				if (!layoutNeeded)
					{
//System.out.println("No layout needed");
					return;
					}
				}
//System.out.println("do Layout");


			srcPointHasShunt = !srcConnPt.equals(srcConnShuntPt);
			destPointHasShunt = !destConnPt.equals(destConnShuntPt);

			// ---------------------------------------------------------------
			// 5 point layouts
			// ---------------------------------------------------------------
			int numberOfPointsToCreate = 5;
			int numberOfPointsNotNeeded = 0;
			int numberOfPointToModify = 2;
			if (!srcPointHasShunt)
				{
				++numberOfPointsNotNeeded;
				--numberOfPointToModify;
				}
			if (!destPointHasShunt)
				{
				++numberOfPointsNotNeeded;
				}

			numberOfPointsToCreate -= numberOfPointsNotNeeded;

			graphics.setNumberOfPoints(numberOfPointsToCreate);

//System.out.println("srcPointHasShunt = " + srcPointHasShunt);
//System.out.println("destPointHasShunt = " + destPointHasShunt);
//System.out.println("srcConnPt = " + srcConnPt);
//System.out.println("destConnPt = " + destConnPt);

			graphics.setPoint(0, srcConnPt);
			if (srcPointHasShunt)
				graphics.setPoint(1, srcConnShuntPt);

			if (destPointHasShunt)
				graphics.setPoint(numberOfPointsToCreate - 2, destConnShuntPt);
			graphics.setPoint(numberOfPointsToCreate - 1, destConnPt);

			// ---------------------------------------------------------------
			// Layout approach #1.x:
			// Extends connection in X direction from src to dest, and then straight
			// to the dest pt in the Y direction
			// ---------------------------------------------------------------
			graphics.setPoint(numberOfPointToModify, destConnPt.x, srcConnPt.y);

//System.out.println("\n Try approach #1.x: " + graphics);
			if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
				&& (validateLineNotOverlappingNodes(graphics, source, destination)))
				{
//System.out.println("approach #1.x succeeeded: " + graphics);
//System.out.println("approach #1.x ALL DONE: " + graphics);
				return;
				}

			// ---------------------------------------------------------------
			// Layout approach #1.y:
			// Extends connection in Y direction from src to dest, and then straight
			// to the dest pt in the X direction
			// ---------------------------------------------------------------
			graphics.setPoint(numberOfPointToModify, srcConnPt.x, destConnPt.y);

//System.out.println("\n Try approach #1.y: " + graphics);
			if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
				&& (validateLineNotOverlappingNodes(graphics, source, destination)))
				{
//System.out.println("approach #1.y succeeeded: " + graphics);
//System.out.println("approach #1.y ALL DONE: " + graphics);
				return;
				}

			// ---------------------------------------------------------------
			// 6 point layouts
			// ---------------------------------------------------------------
			numberOfPointsToCreate = 6;
			numberOfPointToModify = 2;
			if (!srcPointHasShunt)
				--numberOfPointToModify;

			numberOfPointsToCreate -= numberOfPointsNotNeeded;

			graphics.setNumberOfPoints(numberOfPointsToCreate);

			graphics.setPoint(0, srcConnPt);
			if (srcPointHasShunt)
				graphics.setPoint(1, srcConnShuntPt);

			if (destPointHasShunt)
				graphics.setPoint(numberOfPointsToCreate - 2, destConnShuntPt);
			graphics.setPoint(numberOfPointsToCreate - 1, destConnPt);

			// ---------------------------------------------------------------
			// Layout approach #2.x:
			// Extends connection in X direction to the midpoint between src and dest pts,
			// and then up or down to the dest pt Y location, and then to the dest pt
			// itself.
			// ---------------------------------------------------------------
			MiCoord midX = (srcConnPt.x + destConnPt.x)/2;
			graphics.setPoint(numberOfPointToModify, midX, srcConnPt.y);
			graphics.setPoint(numberOfPointToModify + 1, midX, destConnPt.y);

//System.out.println("\ngraphics #2 Try.x = " + graphics);
//System.out.println("numberOfPointToModify = " + numberOfPointToModify);
//System.out.println("midX = " + midX);
//System.out.println("srcConnPt.x = " + srcConnPt.x);
			if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
				&& (validateLineNotOverlappingNodes(graphics, source, destination)))
				{
//System.out.println("approach #2.x succeeeded: " + graphics);
				return;
				}

			// ---------------------------------------------------------------
			// Layout approach #2.y:
			// Extends connection in Y direction to the midpoint between src and dest pts,
			// and then left or right to the dest pt X location, and then to the dest pt
			// itself.
			// ---------------------------------------------------------------
			MiCoord midY = (srcConnPt.y + destConnPt.y)/2;
			graphics.setPoint(numberOfPointToModify, srcConnPt.x, midY);
			graphics.setPoint(numberOfPointToModify + 1, destConnPt.x, midY);

//System.out.println("graphics Try.y = " + graphics);
			if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
				&& (validateLineNotOverlappingNodes(graphics, source, destination)))
				{
//System.out.println("approach #2.y succeeeded: " + graphics);
				return;
				}

			// ---------------------------------------------------------------
			// Layout approach #3.x:
			// Extends connection in X direction to the midpoint between src and dest node
			// bounds, and then up or down to the dest pt Y location, and then to the dest pt
			// itself.
			// ---------------------------------------------------------------
			if ((source != null) && (destination != null))
				{
				if (source.getXmax() < destination.getXmin())
					midX = (source.getXmax() + destination.getXmin())/2;
				else
					midX = (source.getXmin() + destination.getXmax())/2;

				graphics.setPoint(numberOfPointToModify, midX, srcConnPt.y);
				graphics.setPoint(numberOfPointToModify + 1, midX, destConnPt.y);

				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #3.x succeeeded: " + graphics);
					return;
					}
				}

			// ---------------------------------------------------------------
			// Layout approach #3.y:
			// Extends connection in Y direction to the midpoint between src and dest node
			// bounds, and then left or right to the dest pt X location, and then to the dest pt
			// itself.
			// ---------------------------------------------------------------
			if ((source != null) && (destination != null))
				{
				if (source.getYmax() < destination.getYmin())
					midY = (source.getYmax() + destination.getYmin())/2;
				else
					midY = (source.getYmin() + destination.getYmax())/2;

				graphics.setPoint(numberOfPointToModify, srcConnPt.x, midY);
				graphics.setPoint(numberOfPointToModify + 1, destConnPt.x, midY);

				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #3.y succeeeded: " + graphics);
					return;
					}
				}

			// ---------------------------------------------------------------
			// Layout approach #3.5.x:
			// Extends connection in X direction to the rightmost extrema of the src
			// and dest bounds, and then up or down to the dest pt Y location, and 
			// then to the dest pt itself.
			// ---------------------------------------------------------------
			MiBounds unionOfNodeBounds = new MiBounds();
			if ((source != null) || (destination != null))
				{
				if (source != null)
					unionOfNodeBounds.union(srcNodeDrawBounds);
				if (destination != null)
					unionOfNodeBounds.union(destNodeDrawBounds);

				// Don't let lines touch the bounds of the nodes or intersect test will fail
				unionOfNodeBounds.addMargins(1);
				unionOfNodeBounds.union(srcConnShuntPt);
				unionOfNodeBounds.union(destConnShuntPt);
				MiCoord maxX = unionOfNodeBounds.xmax;
				graphics.setPoint(numberOfPointToModify, maxX, srcConnShuntPt.y);
				graphics.setPoint(numberOfPointToModify + 1, maxX, destConnShuntPt.y);

//System.out.println("\ngraphics #3.5 Try.x = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #3.5.xmax succeeeded: " + graphics);
					return;
					}

				MiCoord maxY = unionOfNodeBounds.ymax;
				graphics.setPoint(numberOfPointToModify, srcConnShuntPt.x, maxY);
				graphics.setPoint(numberOfPointToModify + 1, destConnShuntPt.x, maxY);

//System.out.println("\ngraphics #3.5 Try.maxY = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #3.5.maxY succeeeded: " + graphics);
					return;
					}

				// Don't let lines touch the bounds of the nodes or intersect test will fail
				MiCoord minX = unionOfNodeBounds.xmin;
				graphics.setPoint(numberOfPointToModify, minX, srcConnShuntPt.y);
				graphics.setPoint(numberOfPointToModify + 1, minX, destConnShuntPt.y);

//System.out.println("\ngraphics #3.5 Try.xmin = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #3.5.xmin succeeeded: " + graphics);
					return;
					}

				MiCoord minY = unionOfNodeBounds.ymin;
				graphics.setPoint(numberOfPointToModify, srcConnShuntPt.x, minY);
				graphics.setPoint(numberOfPointToModify + 1, destConnShuntPt.x, minY);

//System.out.println("\ngraphics #3.5 Try.minY = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #3.5.minY succeeeded: " + graphics);
					return;
					}

				}

			// ---------------------------------------------------------------
			// Add shunts if not already present for src and dest
			// ---------------------------------------------------------------
			numberOfPointsToCreate = 4;
			numberOfPointToModify = 2;
			if ((!srcPointHasShunt) && (source != null))
				{
				++numberOfPointsToCreate;
				}
			if ((!destPointHasShunt) && (destination != null))
				{
				++numberOfPointsToCreate;
				}

			graphics.setNumberOfPoints(numberOfPointsToCreate);

			graphics.setPoint(0, srcConnPt);

			if ((!srcPointHasShunt) && (sourcePtRule != null))
				{
				sourcePtRule.getShuntPointLocation(sourceManagedPt,
					source, srcConnPtNumber, srcConnShuntPt, Mi_TMP_SHUNT_DISTANCE);
				graphics.setPoint(1, srcConnShuntPt);
//System.out.println("srcConnPt = " + srcConnPt);
//System.out.println("srcConnShuntPt = " + srcConnShuntPt);
				}

			if ((!destPointHasShunt) && (destPtRule != null))
				{
				destPtRule.getShuntPointLocation(destManagedPt,
					destination, destConnPtNumber, destConnShuntPt, Mi_TMP_SHUNT_DISTANCE);
				graphics.setPoint(numberOfPointsToCreate - 2, destConnShuntPt);
				}

			graphics.setPoint(numberOfPointsToCreate - 1, destConnPt);

			// ---------------------------------------------------------------
			// Layout approach #4.x:
			// Extends connection in X direction to the midpoint between src and dest pts,
			// and then up or down to the dest pt Y location, and then to the dest pt
			// itself.
			// ---------------------------------------------------------------
			midX = (srcConnPt.x + destConnPt.x)/2;
			graphics.setPoint(numberOfPointToModify, midX, srcConnShuntPt.y);
			graphics.setPoint(numberOfPointToModify + 1, midX, destConnShuntPt.y);

//System.out.println("\ngraphics #4 Try.x = " + graphics);
//System.out.println("numberOfPointToModify = " + numberOfPointToModify);
//System.out.println("midX = " + midX);
//System.out.println("srcConnPt.x = " + srcConnPt.x);
			if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
				&& (validateLineNotOverlappingNodes(graphics, source, destination)))
				{
//System.out.println("approach #4.x succeeeded: " + graphics);
				return;
				}

			// ---------------------------------------------------------------
			// Layout approach #4.y:
			// Extends connection in Y direction to the midpoint between src and dest pts,
			// and then left or right to the dest pt X location, and then to the dest pt
			// itself.
			// ---------------------------------------------------------------
			midY = (srcConnPt.y + destConnPt.y)/2;
			graphics.setPoint(numberOfPointToModify, srcConnShuntPt.x, midY);
			graphics.setPoint(numberOfPointToModify + 1, destConnShuntPt.x, midY);

//System.out.println("graphics Try.y = " + graphics);
			if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
				&& (validateLineNotOverlappingNodes(graphics, source, destination)))
				{
//System.out.println("approach #4.y succeeeded: " + graphics);
				return;
				}

			// ---------------------------------------------------------------
			// Layout approach #5.x:
			// Extends connection in X direction to the rightmost extrema of the src
			// and dest bounds, and then up or down to the dest pt Y location, and 
			// then to the dest pt itself.
			// ---------------------------------------------------------------
			if ((source != null) || (destination != null))
				{
				if (source != null)
					unionOfNodeBounds.union(srcNodeDrawBounds);
				if (destination != null)
					unionOfNodeBounds.union(destNodeDrawBounds);

				// Don't let lines touch the bounds of the nodes or intersect test will fail
				unionOfNodeBounds.addMargins(1);
				unionOfNodeBounds.union(srcConnShuntPt);
				unionOfNodeBounds.union(destConnShuntPt);
				MiCoord maxX = unionOfNodeBounds.xmax;
				graphics.setPoint(numberOfPointToModify, maxX, srcConnShuntPt.y);
				graphics.setPoint(numberOfPointToModify + 1, maxX, destConnShuntPt.y);

//System.out.println("\ngraphics #5 Try.x = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #5.xmax succeeeded: " + graphics);
					return;
					}

				// Don't let lines touch the bounds of the nodes or intersect test will fail
				MiCoord minX = unionOfNodeBounds.xmin;
				graphics.setPoint(numberOfPointToModify, minX, srcConnShuntPt.y);
				graphics.setPoint(numberOfPointToModify + 1, minX, destConnShuntPt.y);

//System.out.println("\ngraphics #5 Try.x = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #5.xmin succeeeded: " + graphics);
					return;
					}
				}

			// ---------------------------------------------------------------
			// Layout approach #5.y:
			// Extends connection in Y direction to the topmost extrema of the src
			// and dest bounds, and then left or right to the dest pt X location, and 
			// then to the dest pt itself.
			// ---------------------------------------------------------------
			if ((source != null) || (destination != null))
				{
				MiCoord maxY = unionOfNodeBounds.ymax;
				graphics.setPoint(numberOfPointToModify, srcConnShuntPt.x, maxY);
				graphics.setPoint(numberOfPointToModify + 1, destConnShuntPt.x, maxY);

//System.out.println("graphics Try.5y = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #5.maxy succeeeded: " + graphics);
					return;
					}

				MiCoord minY = unionOfNodeBounds.ymin;
				graphics.setPoint(numberOfPointToModify, srcConnShuntPt.x, minY);
				graphics.setPoint(numberOfPointToModify + 1, destConnShuntPt.x, minY);

//System.out.println("graphics Try.5ymin = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #5.ymin succeeeded: " + graphics);
					return;
					}
				}
			// ---------------------------------------------------------------
			// Layout approach #6.x:
			// Same as #5.x but make shunt extend to extrema of the union of the 
			// src and dest bounds.
			// ---------------------------------------------------------------
			if ((source != null) || (destination != null))
				{
				if (srcConnShuntPt.x > srcConnPt.x)
					srcConnShuntPt.x = unionOfNodeBounds.xmax;
				else if (srcConnShuntPt.x < srcConnPt.x)
					srcConnShuntPt.x = unionOfNodeBounds.xmin;

				if (srcConnShuntPt.y > srcConnPt.y)
					srcConnShuntPt.y = unionOfNodeBounds.ymax;
				else if (srcConnShuntPt.y < srcConnPt.y)
					srcConnShuntPt.y = unionOfNodeBounds.ymin;

				graphics.setPoint(1, srcConnShuntPt);

				if (destConnShuntPt.x > destConnPt.x)
					destConnShuntPt.x = unionOfNodeBounds.xmax;
				else if (destConnShuntPt.x < destConnPt.x)
					destConnShuntPt.x = unionOfNodeBounds.xmin;

				if (destConnShuntPt.y > destConnPt.y)
					destConnShuntPt.y = unionOfNodeBounds.ymax;
				else if (destConnShuntPt.y < destConnPt.y)
					destConnShuntPt.y = unionOfNodeBounds.ymin;

				graphics.setPoint(graphics.getNumberOfPoints() - 2, destConnShuntPt);



				MiCoord maxX = unionOfNodeBounds.xmax;
				graphics.setPoint(numberOfPointToModify, maxX, srcConnShuntPt.y);
				graphics.setPoint(numberOfPointToModify + 1, maxX, destConnShuntPt.y);

//System.out.println("\ngraphics #6 Try.x = " + graphics);
//System.out.println("numberOfPointToModify = " + numberOfPointToModify);
//System.out.println("midX = " + midX);
//System.out.println("srcConnPt.x = " + srcConnPt.x);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #6.x succeeeded: " + graphics);
					return;
					}
				}

			// ---------------------------------------------------------------
			// Layout approach #6.y:
			// Same as #5.y but make shunt extend to extrema of the union of the 
			// src and dest bounds.
			// ---------------------------------------------------------------
			if ((source != null) || (destination != null))
				{
				MiCoord maxY = unionOfNodeBounds.ymax;
				graphics.setPoint(numberOfPointToModify, srcConnShuntPt.x, maxY);
				graphics.setPoint(numberOfPointToModify + 1, destConnShuntPt.x, maxY);

//System.out.println("graphics Try.y = " + graphics);
				if ((validateExitAndEntryAngles(graphics, srcConnAngleMask, destConnAngleMask))
					&& (validateLineNotOverlappingNodes(graphics, source, destination)))
					{
//System.out.println("approach #6.y succeeeded: " + graphics);
					return;
					}
				}
			graphics.setNumberOfPoints(2);
//System.out.println("NO approach succeeeded: " + conn.getGraphics());
			graphics.setPoint(0, srcConnPt);
			graphics.setPoint(-1, destConnPt);
			}
		return;
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

	protected	boolean		validateLineNotOverlappingNodes(
						MiPart graphics, MiPart srcNode, MiPart destNode)
		{
		int numberOfPoints = graphics.getNumberOfPoints();
//System.out.println("validateLineNotOverlappingNodes " + numberOfPoints);

		for (int i = 1; i < numberOfPoints; ++i)
			{
			graphics.getPoint(i - 1, tmpPoint1);
			graphics.getPoint(i, tmpPoint2);
			if ((srcNode != null) && (i > 1)
				&& ((srcNode != destNode) || (i < numberOfPoints - 1)))
				{
//System.out.println("Checking intersect src: line : " + (i-1) + " to " + i);
				if (srcNodeDrawBounds.intersectsLine(
					tmpPoint1.x, tmpPoint1.y, 
					tmpPoint2.x, tmpPoint2.y, getLineWidth()))
					{
//System.out.println("REJECT Cause intersect src: line : " + (i-1) + " to " + i);
					return(false);
					}
				}
			if ((destNode != null)
				&& (i < numberOfPoints - 1)
				&& ((srcNode != destNode) || (i > 1)))
				{
				if (destNodeDrawBounds.intersectsLine(
					tmpPoint1.x, tmpPoint1.y, 
					tmpPoint2.x, tmpPoint2.y, getLineWidth()))
					{
//System.out.println("REJECT Cause intersect dest: line : " + (i-1) + " to " + i);
					return(false);
					}
				}
			}
		return(true);
		}
	protected	boolean		validateExitAndEntryAngles(
						MiPart graphics, int srcConnAngleMask, int destConnAngleMask)
		{
		if (srcPointHasShunt)
			{
			graphics.getPoint(1, tmpPoint1);
			graphics.getPoint(2, tmpPoint2);
			}
		else
			{
			graphics.getPoint(0, tmpPoint1);
			graphics.getPoint(1, tmpPoint2);
			}
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
//System.out.println("REJECT Cause src mask invalid, mask = " + mask  + ", srcConnAngleMask= " + srcConnAngleMask);
			return(false);
			}

		int numberOfPoints = graphics.getNumberOfPoints();
		if (destPointHasShunt)
			{
			graphics.getPoint(numberOfPoints - 2, tmpPoint1);
			graphics.getPoint(numberOfPoints - 3, tmpPoint2);
			}
		else
			{
			graphics.getPoint(numberOfPoints - 1, tmpPoint1);
			graphics.getPoint(numberOfPoints - 2, tmpPoint2);
			}
		mask = 0;

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
//System.out.println("REJECT Cause dest mask invalid, mask = " + mask  + ", destConnAngleMask= " + destConnAngleMask);
			return(false);
			}

//System.out.println("ACCEPTED proposed input and output angles");
		return(true);
		}
	}




