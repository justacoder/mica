
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
import com.swfm.mica.util.Utility;

/*
Fix/Add: address various snap grids by name ("Major", "Minor", "Default", etc.), not just hardcoded 'major' and 'minor' and have MiParts have an optional resource that specifies which grid they snap to
*/
/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiSnapManager extends MiModelEntity implements MiiTypes, MiiNames
	{
	public	static final	String	Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE = "snapGridSizeOverride";
	public	static final	String	Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE = "snapPartUsingOnlyMajorGrid";

	private	static	MiPropertyDescriptions	propertyDescriptions;

	private static final int	Mi_RULER_ID			= 1;
	private static final int	Mi_GRID_ID			= 2;
	private static final int	Mi_GUIDE_POINT_ID		= 3;
	private static final int	Mi_GUIDE_ID			= 4;

	private static final int	Mi_SNAP_TO_HIGHEST_PRIORITY	= 1;
	private static final int	Mi_SNAP_TO_CLOSEST		= 2;

	
	private		boolean		enabled				= true;
	private		boolean		snapToGuidesPointsEnabled	= true;
	private		boolean		snapToGuidesEnabled		= true;
	private		boolean		snapToGridEnabled		= true;
	private		boolean		snapToHorizontalRulerEnabled	= true;
	private		boolean		snapToVerticalRulerEnabled	= true;

	private		boolean		justSnappingToMajorGrid		= true;
	private		boolean		applySnapToDefaultSnapLocationsIfNoSnapMananger	= true;

	private		boolean		keepShapesWithinPageBounds	= true;

	private		FastVector	guidePoints			= new FastVector();
	private		FastVector	guides				= new FastVector();
	private		MiDrawingGrid	grid;
	private		MiRuler		horizontalRuler;
	private		MiRuler		verticalRuler;

	// set from DrawingGrid, but can be overridden by a part's Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE resource
	private		MiSize		gridSize;
	private		MiSize		tmpSize				= new MiSize();

	private		boolean		snapStrengthInfinite;
	private		double		guidePointsSnapStrength		= 10;
	private		double		guideSnapStrength		= 10;
	private		double		gridSnapStrength		= 8;
	private		double		horizontalRulerSnapStrength	= 4;
	private		double		verticalRulerSnapStrength	= 4;

	private		double		horizontalRulerSnapLocations	= 0.25;		// In units
	private		double		verticalRulerSnapLocations	= 0.25;		// In units

	private		MiBounds	universe			= new MiBounds();
	private		MiEditor	editor;

	private		MiVector	tmpVector1			= new MiVector();
	private		MiVector	tmpVector2			= new MiVector();
	private		MiPoint		tmpPoint1			= new MiPoint();
	private		MiPoint		tmpPoint2			= new MiPoint();
	private		MiPoint		tmpPoint3			= new MiPoint();
	private		MiPoint		tmpPoint4			= new MiPoint();
	private		MiPoint		tmpPoint5			= new MiPoint();
	private		MiPoint		tmpPoint6			= new MiPoint();
	private		MiPoint		tmpPoint7			= new MiPoint();
	private		MiPoint		tmpPoint8			= new MiPoint();
	private		MiPoint		tmpPoint9			= new MiPoint();
	private		MiPoint		tmpPoint10			= new MiPoint();
	private		MiBounds	tmpBounds1			= new MiBounds();
	private		MiBounds	tmpBounds2			= new MiBounds();
	private		MiMargins	tmpMargins			= new MiMargins();

	private		int		snapMethodology			= Mi_SNAP_TO_HIGHEST_PRIORITY;
	private		int[]		snapPriorityTable		= 
		{
		Mi_GUIDE_POINT_ID,
		Mi_GUIDE_ID,
		Mi_GRID_ID,
		Mi_RULER_ID
		};

	private		int[]		defaultRegularShapeSnapLocations		= 
		{
		Mi_LOWER_LEFT_LOCATION,
		Mi_UPPER_LEFT_LOCATION,
		Mi_LOWER_RIGHT_LOCATION,
		Mi_UPPER_RIGHT_LOCATION,
		};
	private		int[]		defaultMultiPointShapeSnapLocations		= 
		{
		Mi_START_LOCATION,
		Mi_END_LOCATION
		};

	public				MiSnapManager()
		{
		}

	public		void		setEnabled(boolean flag)
		{
		enabled = flag;
		}
	public		boolean		isEnabled()
		{
		return(enabled);
		}

	/** otherwise snap to minor/finest grid **/
	public		void		setJustSnappingToMajorGrid(boolean flag)
		{
		justSnappingToMajorGrid = flag;
		}
	public		boolean		isJustSnappingToMajorGrid()
		{
		return(justSnappingToMajorGrid);
		}

	public		void		setApplySnapToDefaultSnapLocationsIfPartHasNoSnapMananger(boolean flag)
		{
		applySnapToDefaultSnapLocationsIfNoSnapMananger = flag;
		}
	public		boolean		getApplySnapToDefaultSnapLocationsIfPartHasNoSnapMananger()
		{
		return(applySnapToDefaultSnapLocationsIfNoSnapMananger);
		}

	public		void		setKeepShapesWithinPageBounds(boolean flag)
		{
		keepShapesWithinPageBounds = flag;
		}
	public		boolean		getKeepShapesWithinPageBounds()
		{
		return(keepShapesWithinPageBounds);
		}

	protected	void		setTargetEditor(MiEditor editor)
		{
		this.editor = editor;
		}

	public		void		addGuidePoint(MiGuidePoint guidePoint)
		{
		guidePoints.addElement(guidePoint);
		}
	public		void		addGuide(MiGuide guide)
		{
		guides.addElement(guide);
		}
	public		void		setGrid(MiDrawingGrid grid)
		{
		this.grid = grid;
		}
	public		MiDrawingGrid	getGrid()
		{
		return(grid);
		}
	public		void		setHorizontalRuler(MiRuler ruler)
		{
		horizontalRuler = ruler;
		}
	public		void		setVerticalRuler(MiRuler ruler)
		{
		verticalRuler = ruler;
		}


	public		boolean		applyHorizontalAdjustment(MiPoint point)
		{
		if (!enabled)
			return(false);

		MiPoint origPt = new MiPoint(point);
		gridSize = grid.getGridSize();
		if (justSnappingToMajorGrid)
			{
			gridSize = grid.getMajorGridSize();
			}
		if (applyAdjustment(point) > 0)
			{
			point.y = origPt.y;
			return(true);
			}
		return(false);
		}
	public		boolean		applyVerticalAdjustment(MiPoint point)
		{
		if (!enabled)
			return(false);

		MiPoint origPt = new MiPoint(point);
		gridSize = grid.getGridSize();
		if (justSnappingToMajorGrid)
			{
			gridSize = grid.getMajorGridSize();
			}
		if (applyAdjustment(point) > 0)
			{
			point.x = origPt.x;
			return(true);
			}
		return(false);
		}
	public		boolean		snap(MiPoint point)
		{
		if (!enabled)
			return(false);

		return(snap(point, justSnappingToMajorGrid));
		}
	public		boolean		snap(MiPoint point, boolean snapToMajorGrid)
		{
		if (!enabled)
			return(false);

		editor.getUniverseBounds(universe);
		gridSize = grid.getGridSize();
		if (snapToMajorGrid)
			{
			gridSize = grid.getMajorGridSize();
			}
//MiDebug.printStackTrace("Snap: " + gridSize);
		return(applyAdjustment(point) > 0);
		}
	public		boolean		snap(MiPart part)
		{
		if (!enabled)
			return(false);

		MiVector vector = new MiVector();
		if (getSnapTranslation(part, vector))
			{
			part.translate(vector);
			return(true);
			}
		return(false);
		}
	public		boolean		getSnapTranslation(MiPart part, MiVector initialTranslation)
		{
//MiDebug.printStackTrace("getSnapTranslation : " + part);
//MiDebug.println("getSnapTranslation : " + initialTranslation);
//MiDebug.println("part : " + part);
//MiDebug.println("part.isSnappable() : " + part.isSnappable());
//MiDebug.dump(part);
		if (!enabled)
			return(false);

		boolean		changed 		= false;
		int[]		locationsToCheck;

		editor.getUniverseBounds(universe);

		gridSize = grid.getGridSize();
		if (justSnappingToMajorGrid)
			{
			gridSize = grid.getMajorGridSize();
			}
		if ((part.getResource(Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE) != null)
			|| ((part instanceof MiReference) 
				&& (((MiReference )part).getPart(0).getResource(Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE) != null)))
			{
			gridSize = grid.getMajorGridSize();
//MiDebug.printStackTrace("getSnapTranslation major grid : " + gridSize);
			}
		if (part.getResource(Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE) != null)
			{
			tmpSize = (MiSize )part.getResource(Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE);
			gridSize.setSize(
				grid.getUnits().getPixelsPerUnit() * tmpSize.width,
				grid.getUnits().getPixelsPerUnit() * tmpSize.height);
//MiDebug.printStackTrace("getSnapTranslation paret grid size override : " + gridSize);
//MiDebug.printStackTrace("getSnapTranslation paret grid size getMajorGridSize : " + grid.getMajorGridSize());
			}

		MiPoint[] snapPoints = null;

		MiSnapPointManager snapPointManager = part.getSnapPointManager();
		MiPart partWithSnapPointManager = part;
		if (snapPointManager == null)
			{
			Object[] partAndSnapPointManager = getSnapManagerOfASubPart(part);
			if (partAndSnapPointManager != null)
				{
				snapPointManager = (MiSnapPointManager )partAndSnapPointManager[0];
				partWithSnapPointManager = (MiPart )partAndSnapPointManager[1];
//MiDebug.println("getSnapTranslation partWithSnapPointManager=" + partWithSnapPointManager);
				}
			}
//MiDebug.println("getSnapTranslation Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE? : " + partWithSnapPointManager);
		if ((partWithSnapPointManager != part) 
			&& (partWithSnapPointManager.getResource(Mi_PART_SNAP_ONLY_TO_MAJOR_GRID_RESOURCE) != null))
			{
			gridSize = grid.getMajorGridSize();
			}
		tmpSize = null;
		if ((partWithSnapPointManager != part) 
			&& (partWithSnapPointManager.getResource(Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE) != null))
			{
			tmpSize = (MiSize )partWithSnapPointManager.getResource(Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE);
			}
		else if ((partWithSnapPointManager == part) 
			&& (part.getResource(Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE) != null))
			{
			tmpSize = (MiSize )part.getResource(Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE);
			}
		else if ((part instanceof MiReference) 
			&& (((MiReference )part).getPart(0).getResource(Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE) != null))
			{
			tmpSize = (MiSize )((MiReference )part).getPart(0).getResource(Mi_PART_SPECIFIC_SNAP_GRID_SIZE_OVERRIDE_RESOURCE);
			}

		if (tmpSize != null)
			{
//MiDebug.println("grid.getUnits().getPixelsPerUnit() = " + grid.getUnits().getPixelsPerUnit());
			gridSize.setSize(
				grid.getUnits().getPixelsPerUnit() * tmpSize.width,
				grid.getUnits().getPixelsPerUnit() * tmpSize.height);
			}
//MiDebug.println("snapPtMaanger : " + snapPointManager);
//MiDebug.println("gridSize : " + gridSize);
		if (snapPointManager != null)
			{
			// Find the smallest change required, if any, to each of the control points.
			MiManagedPoints managedPoints = snapPointManager.getManagedPoints();
			snapPoints = new MiPoint[managedPoints.size()];
			for (int i = 0; i < managedPoints.size(); ++i)
				{
				MiManagedPoint managedPt = managedPoints.elementAt(i);
				MiPoint pt = new MiPoint();
				snapPoints[i] = managedPt.getPoint(partWithSnapPointManager, 0, pt);

//MiDebug.println("snapPt : " + pt);
				if ((part instanceof MiReference) && (partWithSnapPointManager != part))
					{
					snapPoints[i].translate(((MiReference )part).getTransform().getDeviceTranslation(new MiVector()));
					}
//MiDebug.println("snapPt : " + pt);
//MiDebug.println("partWithSnapPointManager = " + partWithSnapPointManager);
				}
			}
		else if (applySnapToDefaultSnapLocationsIfNoSnapMananger)
			{
//MiDebug.println("applySnapToDefaultSnapLocationsIfNoSnapMananger");
//MiDebug.dump(part);
			if ((part instanceof MiMultiPointShape) || (part instanceof MiConnection))
				{
				locationsToCheck = defaultMultiPointShapeSnapLocations;
				}
			else
				{
				locationsToCheck = defaultRegularShapeSnapLocations;
				}
			snapPoints = new MiPoint[locationsToCheck.length];
			for (int i = 0; i < locationsToCheck.length; ++i)
				{
				int location = locationsToCheck[i];
				tmpBounds2.zeroOut();
				MiPoint pt = new MiPoint();
				snapPoints[i] = pt;
				part.getRelativeLocation(location, tmpBounds2, pt, tmpMargins);
				}
			}
		if (snapPoints != null)
			{
			changed = _getSnapTranslation(part, initialTranslation, snapPoints);
			}

//MiDebug.println("DONE getSnapTranslation : " + initialTranslation);
		return(changed);
		}

	protected	int		applyAdjustment(MiPoint point)
		{
		boolean changed = false;
		int priorityOfSnap = 0;
		MiPoint closestPoint = tmpPoint3;
		closestPoint.set(Mi_MAX_COORD_VALUE, Mi_MAX_COORD_VALUE);
		MiVector smallestDistance = tmpVector1;
		smallestDistance.set(Mi_MAX_DISTANCE_VALUE, Mi_MAX_DISTANCE_VALUE);

		if (snapMethodology == Mi_SNAP_TO_CLOSEST)
			{
			// Find and apply the smallest change required, if any, to the point
			//changed |= applyLocalSnapPointAdjustment(point, smallestDistance, closestPoint);
			changed |= applyGuidePointAdjustment(point, smallestDistance, closestPoint);
			changed |= applyGuideAdjustment(point, smallestDistance, closestPoint);
			changed |= applyGridAdjustment(point, smallestDistance, closestPoint);
			changed |= applyRulerAdjustment(point, smallestDistance, closestPoint);
			if (changed)
				priorityOfSnap = 1;
			}
		else if (snapMethodology == Mi_SNAP_TO_HIGHEST_PRIORITY)
			{
//MiDebug.println("Mi_SNAP_TO_HIGHEST_PRIORITY");
			for (int i = 0; i < snapPriorityTable.length; ++i)
				{
				switch (snapPriorityTable[i])
					{
					case Mi_GUIDE_ID 	:
						changed = applyGuideAdjustment(
							point, smallestDistance, closestPoint);
						break;
					case Mi_GUIDE_POINT_ID :
						changed = applyGuidePointAdjustment(
							point, smallestDistance, closestPoint);
						break;
					case Mi_GRID_ID		:
						changed = applyGridAdjustment(
							point, smallestDistance, closestPoint);
						break;
					case Mi_RULER_ID 	:
						changed = applyRulerAdjustment(
							point, smallestDistance, closestPoint);
						break;
					}
				if (changed)
					{
					priorityOfSnap = i + 1;
					break;
					}
				}
			}
		if (changed)
			{
			point.copy(closestPoint);
			}
		return(priorityOfSnap);
		}

	protected	boolean		_getSnapTranslation(MiPart part, MiVector proposedTranslation, MiPoint[] snapPoints)
		{
		boolean 	changed 		= false;
		boolean 	attemptToMoveOutsidePageBounds	= false;
		MiPoint 	origPt 			= tmpPoint4;
		MiVector 	smallestDistanceVector 	= tmpVector2;
		MiDistance 	smallestDistance 	= Mi_MAX_DISTANCE_VALUE;
		int		smallestDistancePriority= Integer.MAX_VALUE;
		MiPoint		pt			= tmpPoint5;

		for (int i = 0; i < snapPoints.length; ++i)
			{
			pt.copy(snapPoints[i]);
			pt.translate(proposedTranslation);
			origPt.copy(pt);
			int priority;
			MiBounds partBounds = part.getBounds(tmpBounds2);
//MiDebug.println("Checking commonBoundsLocations: " + i);
			if ((priority = applyAdjustment(pt)) > 0)
				{
				double dist = pt.getDistanceSquared(origPt);
//MiDebug.println("Checking commonBoundsLocations dist = : " + dist);
				if ((dist < smallestDistance)
					&& (priority <= smallestDistancePriority)
					&& (partBounds.translate(
						pt.x - origPt.x + proposedTranslation.x,
						pt.y - origPt.y + proposedTranslation.y)
						.isContainedIn(universe)))
					{
					if ((!keepShapesWithinPageBounds)
						|| (partBounds.isContainedIn(grid.getBounds(tmpBounds1))))
						{
						smallestDistance = dist;
						smallestDistanceVector.set(pt.x - origPt.x, pt.y - origPt.y);
						smallestDistancePriority = priority;
						changed = true;
						}
					else
						{
						attemptToMoveOutsidePageBounds = true;
						}
					}
				}
			}
		if (changed)
			{
//MiDebug.println("CHANGED : smallestDistanceVector = "  +smallestDistanceVector);
//MiDebug.println("CHANGED : proposedTranslation = "  +proposedTranslation);
			proposedTranslation.add(smallestDistanceVector);
//MiDebug.println("NOW CHANGED : proposedTranslation = "  +proposedTranslation);
			}
		else if (attemptToMoveOutsidePageBounds)
			{
			proposedTranslation.zeroOut();
//MiDebug.println("attemptToMoveOutsidePageBounds : proposedTranslation = "  +proposedTranslation);
			}
		return(changed);
		}




	protected	boolean		applyGuidePointAdjustment(
						MiPoint pt, MiVector smallestDistance, MiPoint adjustedPoint)
		{
		boolean changed = false;
		double dist;
		if (snapToGuidesPointsEnabled)
			{
			MiPoint origPt = tmpPoint6;
			for (int i = 0; i < guidePoints.size(); ++i)
				{
				origPt.copy(pt);
				if (applyGuidePointAdjustment((MiGuidePoint )guidePoints.elementAt(i), pt))
					{
					dist = Math.abs(pt.x - origPt.x);
					if (dist < smallestDistance.x)
						{
						smallestDistance.x = dist;
						adjustedPoint.x = pt.x;
						changed = true;
						}
					dist = Math.abs(pt.y - origPt.y);
					if (dist < smallestDistance.y)
						{
						smallestDistance.y = dist;
						adjustedPoint.y = pt.y;
						changed = true;
						}
					}
				}
			}
		return(changed);
		}

	protected	boolean		applyGuideAdjustment(
						MiPoint pt, MiVector smallestDistance, MiPoint adjustedPoint)
		{
		boolean changed = false;
		double dist;
		if (snapToGuidesEnabled)
			{
			MiPoint origPt = tmpPoint7;
			for (int i = 0; i < guides.size(); ++i)
				{
				origPt.copy(pt);
				MiGuide guide = (MiGuide )guides.elementAt(i);
				if (applyGuideAdjustment(guide, pt))
					{
					if (guide.adjustsX())
						{
						dist = Math.abs(pt.x - origPt.x);
						if (dist < smallestDistance.x)
							{
							smallestDistance.x = dist;
							adjustedPoint.x = pt.x;
							changed = true;
							}
						}
					else
						{
						dist = Math.abs(pt.y - origPt.y);
						if (dist < smallestDistance.y)
							{
							smallestDistance.y = dist;
							adjustedPoint.y = pt.y;
							changed = true;
							}
						}
					}
				}
			}
		return(changed);
		}

	protected	boolean		applyGridAdjustment(
						MiPoint pt, MiVector smallestDistance, MiPoint adjustedPoint)
		{
		boolean changed = false;
		double dist;
//MiDebug.println("grid = " + grid);
//MiDebug.println("snapToGridEnabled = " + snapToGridEnabled);
		if ((grid != null) && (snapToGridEnabled))
			{
			MiPoint origPt = tmpPoint8;
			origPt.copy(pt);
//MiDebug.println("pt = " + pt);
			applyGridAdjustment(grid, pt);
//MiDebug.println("now pt = " + pt);
				{
				dist = Math.abs(pt.x - origPt.x);
//MiDebug.println("X dist = " + dist);
//MiDebug.println("smallestDistance.x = " + smallestDistance.x);
				if (dist < smallestDistance.x)
					{
					smallestDistance.x = dist;
					adjustedPoint.x = pt.x;
					changed = true;
					}
				dist = Math.abs(pt.y - origPt.y);
//MiDebug.println("Y dist = " + dist);
//MiDebug.println("smallestDistance.y = " + smallestDistance.y);
				if (dist < smallestDistance.y)
					{
					smallestDistance.y = dist;
					adjustedPoint.y = pt.y;
					changed = true;
					}
				}
			}
//MiDebug.println("now adjustedPoint = " + adjustedPoint);
//MiDebug.println("chanegd= " + changed);
		return(changed);
		}


	protected	boolean		applyRulerAdjustment(
						MiPoint pt, MiVector smallestDistance, MiPoint adjustedPoint)
		{
		boolean changed = false;
		double dist;
		if ((verticalRuler != null) && (snapToVerticalRulerEnabled))
			{
			MiPoint origPt = tmpPoint10;
			origPt.copy(pt);
			applyVerticalRulerAdjustment(verticalRuler, pt);
			dist = Math.abs(pt.x - origPt.x);
			if (dist < smallestDistance.x)
				{
				smallestDistance.x = dist;
				adjustedPoint.x = pt.x;
				changed = true;
				}
			}
		if ((horizontalRuler != null) && (snapToHorizontalRulerEnabled))
			{
			MiPoint origPt = tmpPoint10;
			origPt.copy(pt);
			applyHorizontalRulerAdjustment(horizontalRuler, pt);
			dist = Math.abs(pt.y - origPt.y);
			if (dist < smallestDistance.y)
				{
				smallestDistance.y = dist;
				adjustedPoint.y = pt.y;
				changed = true;
				}
			}
		return(changed);
		}
	protected	boolean		applyGuidePointAdjustment(MiGuidePoint guidePoint, MiPoint pt)
		{
		if (guidePoint.isEnabled())
			{
			guidePoint.getReferencePoint(pt);
			return(true);
			}
		return(false);
		}
	protected	boolean		applyGuideAdjustment(MiGuide guide, MiPoint pt)
		{
		if (guide.isEnabled())
			{
			if (guide.adjustsX())
				pt.x = guide.getReferencePoint();
			else if (guide.adjustsY())
				pt.y = guide.getReferencePoint();
			}
		return(false);
		}
	protected	void		applyGridAdjustment(MiDrawingGrid grid, MiPoint pt)
		{
		MiPoint 	refPoint 	= grid.getReferencePoint(tmpPoint9);

		if (keepShapesWithinPageBounds)
			{
			MiBounds gridBounds = grid.getBounds(tmpBounds1);
			if (pt.x > gridBounds.xmax)
				pt.x = gridBounds.xmax;
			else if (pt.x < gridBounds.xmin)
				pt.x = gridBounds.xmin;
			}

		int 		snapCellIndex 	= (int )((pt.x - refPoint.x)/gridSize.width);
		MiCoord 	oneSide 	= snapCellIndex * gridSize.width + refPoint.x;
		MiCoord 	otherSide 	= oneSide + gridSize.width;
		MiDistance 	oneDistance 	= Math.abs(pt.x - oneSide);
		MiDistance 	otherDistance 	= Math.abs(pt.x - otherSide);
//MiDebug.println("pt.x = " + pt.x);
//MiDebug.println("refPoint.x = " + refPoint.x);
//MiDebug.println("snapCellIndex = " + snapCellIndex);
//MiDebug.println("oneSide = " + oneSide);
//MiDebug.println("otherSide = " + otherSide);
//MiDebug.println("oneDistance = " + oneDistance);


		if ((oneDistance < otherDistance) && (oneSide >= universe.xmin) && (oneSide <= universe.xmax))
			pt.x = oneSide;
		else
			pt.x = otherSide;

//MiDebug.println("pt.x = " + pt.x);
		if (keepShapesWithinPageBounds)
			{
			MiBounds gridBounds = grid.getBounds(tmpBounds1);
			if (pt.y > gridBounds.ymax)
				pt.y = gridBounds.ymax;
			else if (pt.y < gridBounds.ymin)
				pt.y = gridBounds.ymin;
			}

		snapCellIndex 	= (int )((pt.y - refPoint.y)/gridSize.height);
		oneSide 	= snapCellIndex * gridSize.height + refPoint.y;
		otherSide 	= oneSide + gridSize.height;
		oneDistance 	= Math.abs(pt.y - oneSide);
		otherDistance 	= Math.abs(pt.y - otherSide);
		if ((oneDistance < otherDistance) && (oneSide >= universe.ymin) && (oneSide <= universe.ymax))
			pt.y = oneSide;
		else
			pt.y = otherSide;
		}
	protected	void		applyHorizontalRulerAdjustment(MiRuler horizontalRuler, MiPoint pt)
		{
		MiDistance 	distance 	= horizontalRuler.getTickSpacingOnTargetPage(
							horizontalRulerSnapLocations);
		MiCoord 	refLocation 	= horizontalRuler.getReferenceTickLocationOnTargetPage();
		int 		snapCellIndex 	= (int )Math.floor((pt.x - refLocation)/distance);
		MiCoord 	oneSide 	= snapCellIndex * distance;
		MiCoord 	otherSide 	= (snapCellIndex + 1) * distance;
		MiDistance 	oneDistance 	= Math.abs(pt.x - oneSide);
		MiDistance 	otherDistance 	= Math.abs(pt.x - otherSide);
		if ((oneDistance < otherDistance) && (oneSide >= universe.xmin) && (oneSide <= universe.xmax))
			pt.x = oneSide;
		else
			pt.x = otherSide;
		}
	protected	void		applyVerticalRulerAdjustment(MiRuler verticalRuler, MiPoint pt)
		{
		MiDistance 	distance	= verticalRuler.getTickSpacingOnTargetPage(
							verticalRulerSnapLocations);
		MiCoord 	refLocation 	= verticalRuler.getReferenceTickLocationOnTargetPage();
		int 		snapCellIndex 	= (int )Math.floor((pt.y - refLocation)/distance);
		MiCoord 	oneSide 	= snapCellIndex * distance;
		MiCoord 	otherSide 	= (snapCellIndex + 1) * distance;
		MiDistance 	oneDistance 	= Math.abs(pt.y - oneSide);
		MiDistance 	otherDistance 	= Math.abs(pt.y - otherSide);
		if ((oneDistance < otherDistance) && (oneSide >= universe.ymin) && (oneSide <= universe.ymax))
			pt.y = oneSide;
		else
			pt.y = otherSide;
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_ENABLED_NAME))
			setEnabled(Utility.toBoolean(value));
		else if (grid != null)
			grid.setPropertyValue(name, value);
		else
			{
			throw new IllegalArgumentException(this 
				+ ": Attempt to set value of unknown property: " + name);
			}
		}
	protected	Object[]	getSnapManagerOfASubPart(MiPart part)
		{
		MiSnapManager snapManager = null;
		for (int i = 0; i < part.getNumberOfParts(); ++i)
			{
			MiPart subPart = part.getPart(i);
			if (subPart.getSnapPointManager() != null)
				{
				Object[] info = new Object[2];
				info[0] = subPart.getSnapPointManager();
				info[1] = subPart;
				return(info);
				}
			Object[] info = getSnapManagerOfASubPart(subPart);
			if (info != null)
				{
				return(info);
				}
			}
		return(null);
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		if (name.equalsIgnoreCase(Mi_ENABLED_NAME))
			return("" + enabled);
		else if (grid != null)
			return(grid.getPropertyValue(name));
		else
			{
			throw new IllegalArgumentException(this 
				+ ": Attempt to get value of unknown property: " + name);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = new MiPropertyDescriptions(getClass().getName());

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_ENABLED_NAME, Mi_BOOLEAN_TYPE, "true"));

		if (grid != null)
			{
			//propertyDescriptions = new MiPropertyDescriptions(propertyDescriptions);
			//propertyDescriptions.appendPropertyDescriptionComponent(grid.getPropertyDescriptions());
			propertyDescriptions.append(grid.getPropertyDescriptions());
			return(propertyDescriptions);
			}
		MiPropertyDescriptions tmp = propertyDescriptions;
		propertyDescriptions = null;
		return(tmp);
		}
	}
class MiGuidePoint
	{
	private		boolean		enabled;
	private		MiPoint		referencePoint;

	public		boolean		isEnabled()
		{
		return(enabled);
		}
	public		MiPoint		getReferencePoint(MiPoint pt)
		{
		pt.copy(referencePoint);
		return(pt);
		}
	}

class MiiGuide
	{
	}

class MiGuide extends MiPart implements MiiTypes
	{
	private		boolean		enabled;
	private		int		orientation	= Mi_HORIZONTAL;
	private		MiPoint		referencePoint	= new MiPoint();
	private		MiPart		line;
	private		MiBounds	tmpBounds	= new MiBounds();

	public				MiGuide(MiContainer layer)
		{
		line = new MiLine();
		line.setColor(MiColorManager.blue);
		}
	public		boolean		isEnabled()
		{
		return(enabled);
		}
	public		boolean		adjustsX()
		{
		return(orientation == Mi_VERTICAL);
		}
	public		boolean		adjustsY()
		{
		return(orientation == Mi_HORIZONTAL);
		}
	public		MiCoord		getReferencePoint()
		{
		if (orientation == Mi_HORIZONTAL)
			return(referencePoint.y);
		else
			return(referencePoint.x);
		}
	protected 	void		reCalcBounds(MiBounds b)
		{
		updateLine();
		line.getBounds(b);
		}
	protected 	void		calcPreferredSize(MiSize size)
		{
		calcMinimumSize(size);
		}
	protected 	void		calcMinimumSize(MiSize size)
		{
		updateLine();
		line.calcMinimumSize(size);
		}
	protected	void		updateLine()
		{
		if (orientation == Mi_HORIZONTAL)
			{
			MiBounds bounds = getContainer(0).getInnerBounds(tmpBounds);
			line.setPoint(0, bounds.xmin, referencePoint.y);
			line.setPoint(-1, bounds.xmax, referencePoint.y);
			}
		else //if (orientation == Mi_VERTICAL)
			{
			MiBounds bounds = getContainer(0).getInnerBounds(tmpBounds);
			line.setPoint(0, referencePoint.x, bounds.ymin);
			line.setPoint(-1, referencePoint.x, bounds.ymax);
			}
		}
		
	protected	void		render(MiRenderer renderer)
		{
		updateLine();
		line.render(renderer);
		}
	}



