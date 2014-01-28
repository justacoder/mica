
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
public class MiClosestValidManagedPointFinder
	{
	public static final int			EXAMINE_CONN_POINT_MANAGERS		= 1;
	public static final int			EXAMINE_COMMON_POINTS			= 2;
	public static final int			EXAMINE_PARTS_OF_CANDIDATES		= 4;
	public static final int			MIXED_METHODOLOGIES_IN_SAME_PART	= 8;
	public static final int			EXAMINE_COMMON_POINTS_IF_NO_POINT_MANAGERS	= 16;

	private static final int		DEFAULT_PICK_AREA_SIZE	= 20;

	public		MiBounds		tmpBounds		= new MiBounds();
	public		MiPoint			closestConnPt		= new MiPoint();
	public		int			closestConnPtID		= MiiTypes.Mi_CENTER_LOCATION;
	public		MiPart			closestObject;
	private		MiDistance		pickAreaSize;
	private		int			validConnPtLocations[];
	private		int			methodology 		= EXAMINE_CONN_POINT_MANAGERS 
									| EXAMINE_COMMON_POINTS_IF_NO_POINT_MANAGERS;
	private		MiiManagedPointValidator	validator;
	private		MiManagedPointManager		pointManagerKind = new MiConnectionPointManager();




	public				MiClosestValidManagedPointFinder(MiiManagedPointValidator validator)
		{
		pickAreaSize = DEFAULT_PICK_AREA_SIZE;
		this.validator = validator;
		}

	public		void		setMethodology(int m)
		{
		methodology = m;
		}
	public		int		getMethodology()
		{
		return(methodology);
		}
	public		void		setPickAreaSize(MiDistance size)
		{
		pickAreaSize = size;
		}
	public		MiDistance	getPickAreaSize()
		{
		return(pickAreaSize);
		}
	public		void		setValidConnPtLocations(int[] connPts)
		{
		validConnPtLocations = connPts;
		}
	public		int[]		getValidConnPtLocations()
		{
		return(validConnPtLocations);
		}

	// Sets values for closest object, the objects connPt and the location of the connPt
/****
	public		boolean 	findClosestManagedPoint(
						MiEditor editor,
						MiPart srcObj,
						int srcConnPt,
						MiPart ignoreObj,
						MiPoint	location)
		{
		return(findClosestManagedPoint(editor, srcObj, srcConnPt, ignoreObj, location, true));
		}
****/
	public		boolean 	findClosestManagedPoint(
						MiEditor editor,
						MiPart srcObj,
						int srcConnPt,
						MiPart destObj,
						int destConnPt,
						MiPart ignoreObj,
						MiPoint	location,
						boolean allowSameSrcAndDest,
						boolean findSrcObj)
		{
		boolean		found 		= false;
		double 		closestDist 	= MiiTypes.Mi_MAX_DISTANCE_VALUE;
		double		dist;
		MiPart		obj;
		MiBounds	pickArea	= MiBounds.newBounds();
		MiManagedPointSearchResults connPtManResults = new MiManagedPointSearchResults();
		MiManagedPointSearchResults commonPtResults = new MiManagedPointSearchResults();
		MiManagedPointSearchResults winner = new MiManagedPointSearchResults();
		boolean		examinePartsOfObjects = ((methodology & EXAMINE_PARTS_OF_CANDIDATES) != 0);
		boolean		winnerIsAConnPt = false;

		closestObject = null;

		if (srcConnPt == MiiTypes.Mi_DEFAULT_LOCATION)
			{
			int[] connPt = new int[1];
			MiPart connNode = MiManagedPointManager.getDefaultManagedPoint(
						srcObj, connPt, pointManagerKind);
			if (connNode != null)
				{
				srcObj = connNode;
				srcConnPt = connPt[1];
				}
			}

		pickArea.setBounds(0, 0, pickAreaSize, pickAreaSize);

		MiEditorIterator iterator = new MiEditorIterator(editor);
		pickArea.setCenter(location);
		while ((obj = iterator.getNext()) != null)
			{
			if (obj.getDrawBounds(tmpBounds).intersects(pickArea))
				{
				if (((allowSameSrcAndDest) 
						|| ((findSrcObj) && (obj != destObj)) 
						|| ((!findSrcObj) && (obj != srcObj)))
					&& (obj != ignoreObj)
					&& ((validator == null) || ((findSrcObj) ? 
					validator.isValidConnectionSource(obj, destObj) : 
					validator.isValidConnectionDestination(srcObj, obj))))
					{
//MiDebug.println("methodology = " + methodology);
					if ((methodology & EXAMINE_CONN_POINT_MANAGERS) != 0)
						{
						connPtManResults.init();
						findClosestPointUsingConnectionManagers(
							obj, location, examinePartsOfObjects, 
							validator, srcObj, srcConnPt,
							destObj, destConnPt,
							ignoreObj,
							allowSameSrcAndDest, findSrcObj,
							connPtManResults);
//MiDebug.println("EXAMINE_CONN_POINT_MANAGERS connPtManResults = " + connPtManResults);
						winner = connPtManResults;
						}
					if (((methodology & EXAMINE_COMMON_POINTS) != 0)
						|| ((MiManagedPointManager.getManager(obj, pointManagerKind) == null)
						&& ((methodology & EXAMINE_COMMON_POINTS_IF_NO_POINT_MANAGERS) != 0)))
						{
						commonPtResults.init();
						if (validConnPtLocations != null)
							{
							findClosestPointUsingValidCommonPoints(
								obj, location, examinePartsOfObjects, 
								validConnPtLocations, validator, 
								srcObj, srcConnPt, 
								destObj, destConnPt,
								ignoreObj,
								allowSameSrcAndDest, findSrcObj,
								commonPtResults);
//MiDebug.println("EXAMINE_COMMON_POINTS #1 connPtManResults = " + connPtManResults);
							}
						else
							{
							findClosestPointUsingCommonPoints(
								obj, location, examinePartsOfObjects, 
								validator, 
								srcObj, srcConnPt, 
								destObj, destConnPt,
								ignoreObj,
								allowSameSrcAndDest, findSrcObj,
								commonPtResults);
							}
						winner = commonPtResults;
//MiDebug.println("EXAMINE_COMMON_POINTS #2 commonPtResults = " + commonPtResults);
						}

					if ((methodology & 
						(EXAMINE_CONN_POINT_MANAGERS + EXAMINE_COMMON_POINTS)) 
						== EXAMINE_CONN_POINT_MANAGERS + EXAMINE_COMMON_POINTS)
						{
//MiDebug.println("EXAMINE_COMMON_POINTS #3 commonPtResults = " + commonPtResults);
						if (connPtManResults.closestObject 
							== commonPtResults.closestObject)
							{
							if ((methodology 
								& MIXED_METHODOLOGIES_IN_SAME_PART) == 0)
								{
								winner = connPtManResults;
								}
							}
						else
							{
							if (connPtManResults.closestDistSquared 
								< commonPtResults.closestDistSquared)
								{
								winner = connPtManResults;
								}
							else
								{
								winner = commonPtResults;
								}
							}
						}
					if (winner.closestDistSquared < closestDist)
						{
//MiDebug.println("WINNER POINT Results = " + winner);
						closestDist = winner.closestDistSquared;
						closestConnPt.x = winner.closestConnPtLocation.x;
						closestConnPt.y = winner.closestConnPtLocation.y;
						closestObject = winner.closestObject;
						closestConnPtID = winner.closestConnPtNumber;
						found = true;
						winnerIsAConnPt = (winner == connPtManResults);
						}
					}
				}
			}
//MiDebug.println("found = " + found);
//MiDebug.println("closestConnPtID = " + closestConnPtID);
//MiDebug.println("pickAreaSize = " + pickAreaSize);
		if (found)
			{
			if (((Math.abs(closestConnPt.x - location.x) > pickAreaSize)
				|| (Math.abs(closestConnPt.y - location.y) > pickAreaSize))
				// Don't reject if location is within object... (esp. if
				// we are connecting to the center common pt)...unless
				// we are connecting to a connPt
				&& ((winnerIsAConnPt) || (!closestObject.pick(pickArea))))
				{
				// Closest pt is still too far away.
//MiDebug.println("Closest pt is still too far away. ");
				MiBounds.freeBounds(pickArea);
				return(false);
				}
			MiBounds.freeBounds(pickArea);
			return(true);
			}

		MiBounds.freeBounds(pickArea);
		return(false);
		}
	protected	void		findClosestPointUsingConnectionManagers(
							MiPart obj, MiPoint location, 
							boolean examinePartsOfObjects, 
							MiiManagedPointValidator validator,
							MiPart srcObj, int srcConnPtNumber,
							MiPart destObj, int destConnPtNumber,
							MiPart ignoreObj,
							boolean allowSameSrcAndDest, boolean findSrcObj,
							MiManagedPointSearchResults results)
		{
		results.init();

		MiManagedPointManager man = pointManagerKind.getManager(obj);
		if ((man == null) && (!examinePartsOfObjects))
			return;

		if (examinePartsOfObjects)
			{
			MiManagedPointManager.getClosestManagedPointIncludingAllParts(
					obj, location, validator, 
					srcObj, srcConnPtNumber, 
					destObj, destConnPtNumber, 
					ignoreObj,
					allowSameSrcAndDest,
					findSrcObj,
					results, pointManagerKind);
			}
		else
			{
			man.getClosestManagedPoint(
				obj, location, validator, 
				srcObj, srcConnPtNumber, 
				destObj, destConnPtNumber, 
				ignoreObj,
				allowSameSrcAndDest,
				findSrcObj,
				results);
			}
		}
	protected	void		findClosestPointUsingValidCommonPoints(
							MiPart obj, 
							MiPoint location, 
							boolean examinePartsOfObjects, 
							int[] validConnPtLocations,
							MiiManagedPointValidator validator,
							MiPart srcObj, int srcConnPtNumber,
							MiPart destObj, int destConnPtNumber,
							MiPart ignoreObj,
							boolean allowSameSrcAndDest, boolean findSrcObj,
							MiManagedPointSearchResults results)
		{
		MiPoint		connPtLocation	= new MiPoint();

		for (int i = 0; i < validConnPtLocations.length; ++i)
			{
			if ((obj != ignoreObj) 
				&& (((findSrcObj) 
					&& ((allowSameSrcAndDest) || (obj != destObj))
					&& ((obj != destObj) || (validConnPtLocations[i] != destConnPtNumber))
					&& ((validator == null)
						||
					(validator.isValidConnectionSource(
						obj, validConnPtLocations[i], destObj, destConnPtNumber))))
				|| ((!findSrcObj) 
					&& ((allowSameSrcAndDest) || (obj != srcObj))
					&& ((obj != srcObj) || (validConnPtLocations[i] != srcConnPtNumber))
					&& ((validator == null)
						||
					(validator.isValidConnectionDestination(
						srcObj, srcConnPtNumber, obj, validConnPtLocations[i]))))))
				{
				MiManagedPointManager.getLocationOfCommonPoint(
					obj, 
					validConnPtLocations[i], 
					connPtLocation);

				double dist = location.getDistanceSquared(connPtLocation);
				if (dist < results.closestDistSquared)
					{
					results.closestDistSquared = dist;
					results.closestConnPtLocation.x = connPtLocation.x;
					results.closestConnPtLocation.y = connPtLocation.y;
					results.closestObject = obj;
					results.closestConnPtNumber = validConnPtLocations[i];
					}
				}
			}
		if (examinePartsOfObjects)
			{
			for (int i = 0; i < obj.getNumberOfParts(); ++i)
				{
				findClosestPointUsingValidCommonPoints(
					obj.getPart(i), location, examinePartsOfObjects, 
					validConnPtLocations, validator, 
					srcObj, srcConnPtNumber, 
					destObj, destConnPtNumber,
					ignoreObj,
					allowSameSrcAndDest, findSrcObj,
					results);
				}
			}
		}
	protected	void		findClosestPointUsingCommonPoints(
							MiPart obj, 
							MiPoint location, 
							boolean examinePartsOfObjects, 
							MiiManagedPointValidator validator,
							MiPart srcObj, int srcConnPtNumber,
							MiPart destObj, int destConnPtNumber,
							MiPart ignoreObj,
							boolean allowSameSrcAndDest, boolean findSrcObj,
							MiManagedPointSearchResults results)
		{
		MiPoint		connPtLocation	= new MiPoint();

		int aConnPtID = MiManagedPointManager.getClosestCommonPoint(
			obj, location, connPtLocation);

		double dist = location.getDistanceSquared(connPtLocation);

		if ((obj != ignoreObj)
			&& (((findSrcObj) 
				&& ((allowSameSrcAndDest) || (obj != destObj))
				&& ((obj != destObj) || (aConnPtID != destConnPtNumber))
				&& (validator.isValidConnectionSource(
					obj, aConnPtID, destObj, destConnPtNumber)))
			|| ((!findSrcObj) 
				&& ((allowSameSrcAndDest) || (obj != srcObj))
				&& ((obj != srcObj) || (aConnPtID != srcConnPtNumber))
				&& (validator.isValidConnectionDestination(
					srcObj, srcConnPtNumber, obj, aConnPtID))))
			&& (dist < results.closestDistSquared))
			{
			results.closestDistSquared = dist;
			results.closestConnPtLocation.x = connPtLocation.x;
			results.closestConnPtLocation.y = connPtLocation.y;
			results.closestObject = obj;
			results.closestConnPtNumber = aConnPtID;
			}
		if (examinePartsOfObjects)
			{
			for (int i = 0; i < obj.getNumberOfParts(); ++i)
				{
				findClosestPointUsingCommonPoints(
					obj.getPart(i), location, examinePartsOfObjects, 
					validator, 
					srcObj, srcConnPtNumber, 
					destObj, destConnPtNumber, 
					ignoreObj,
					allowSameSrcAndDest, findSrcObj,
					results);
				}
			}
		}
	public		String		toString()
		{
		String str = super.toString() + "[closestObject=" + closestObject + "][closestConnPtID=" + closestConnPtID + "][closestConnPt=" + closestConnPt + "]";
		return(str);
		}

	}


