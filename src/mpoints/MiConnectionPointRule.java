
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
public class MiConnectionPointRule implements MiiManagedPointRule, MiiTypes
	{
	public static final int		Mi_FORK_CONN_FAN_STYLE			= 1;
	public static final int		Mi_STRAIGHT_LINE_CONN_FAN_STYLE		= 2;
	public static final int		Mi_RIGHT_ANGLE_CONN_FAN_STYLE		= 3;
	public static final int		Mi_STRAIGHT_THEN_FLAIR_CONN_FAN_STYLE	= 4;
	public static final int		Mi_FLAIR_THEN_STRAIGHT_CONN_FAN_STYLE	= 5;

	private		boolean		draggable			= true;
	private		boolean		autoSpreadConnections;
	private		int		fanStyle			= Mi_NONE;
	private		int		fanDistance;
	protected	boolean		validConnectionSource		= true;
	protected	boolean		validConnectionDestination	= true;
	//private		int		validExitDirections		= Mi_ALL_DIRECTIONS;
	private		int		validExitDirections		= Mi_NONE;
	private		MiDistance	shuntPointDistance;
	private		MiConnection	prototypeConnection;


	public				MiConnectionPointRule()
		{
		}
	public		void		setPrototypeConnection(MiConnection conn)
		{
		prototypeConnection = conn;
		}
	public		MiConnection	getPrototypeConnection()
		{
		return(prototypeConnection);
		}
	public		void		setShuntPointDistance(MiDistance distance)
		{
		shuntPointDistance = distance;
		}
	public		MiDistance	getShuntPointDistance()
		{
		return(shuntPointDistance);
		}
	public		void		setValidExitDirections(int directions)
		{
		validExitDirections = directions;
		}
	public		int		getValidExitDirections()
		{
		return(validExitDirections);
		}
	public		MiPoint		getShuntPointLocation(
						MiManagedPoint managedPoint, 
						MiPart part, 
						int positionNumber, 
						MiPoint point)
		{
		managedPoint.getLocationOfPoint(part, positionNumber, point);
		if (shuntPointDistance != 0)
			{
			return(getShuntPointLocation(
				managedPoint, part, positionNumber, point, shuntPointDistance));
			}
		return(point);
		}
	public		MiPoint		getShuntPointLocation(
						MiManagedPoint managedPoint, 
						MiPart part, 
						int positionNumber, 
						MiPoint point, 
						MiDistance shuntPointDistance)
		{
		managedPoint.getLocationOfPoint(part, positionNumber, point);
		switch (positionNumber)
			{
			case Mi_CENTER_LOCATION		:
				break;
			case Mi_LEFT_LOCATION		:
			case Mi_WNW_LOCATION		:
			case Mi_WSW_LOCATION		:
				point.x -= shuntPointDistance;
				break;
			case Mi_RIGHT_LOCATION		:
			case Mi_ENE_LOCATION		:
			case Mi_ESE_LOCATION		:
				point.x += shuntPointDistance;
				break;
			case Mi_BOTTOM_LOCATION		:
			case Mi_SWS_LOCATION		:
			case Mi_SES_LOCATION		:
				point.y -= shuntPointDistance;
				break;
			case Mi_TOP_LOCATION		:
			case Mi_NWN_LOCATION		:
			case Mi_NEN_LOCATION		:
				point.y += shuntPointDistance;
				break;
			case Mi_LOWER_LEFT_LOCATION	:
				point.x -= shuntPointDistance;
				point.y -= shuntPointDistance;
				break;
			case Mi_LOWER_RIGHT_LOCATION	:
				point.x += shuntPointDistance;
				point.y -= shuntPointDistance;
				break;
			case Mi_UPPER_LEFT_LOCATION	:
				point.x -= shuntPointDistance;
				point.y += shuntPointDistance;
				break;
			case Mi_UPPER_RIGHT_LOCATION	:
				point.x += shuntPointDistance;
				point.y += shuntPointDistance;
				break;
			default:
				{
				switch (validExitDirections)
					{
					case Mi_LEFT :
						point.x -= shuntPointDistance;
						break;
					case Mi_RIGHT :
						point.x += shuntPointDistance;
						break;
					case Mi_UP :
						point.y += shuntPointDistance;
						break;
					case Mi_DOWN :
						point.y -= shuntPointDistance;
						break;
					case Mi_UP + Mi_RIGHT:
						point.x += shuntPointDistance;
						point.y += shuntPointDistance;
						break;
					case Mi_DOWN + Mi_RIGHT:
						point.x += shuntPointDistance;
						point.y -= shuntPointDistance;
						break;
					case Mi_UP + Mi_LEFT:
						point.x -= shuntPointDistance;
						point.y += shuntPointDistance;
						break;
					case Mi_DOWN + Mi_LEFT:
						point.x -= shuntPointDistance;
						point.y -= shuntPointDistance;
						break;
					default :
						break;
					}
				break;
				}
			}
		return(point);
		}
	public		int		getValidExitDirections(int positionNumber)
		{
		int validDirections = validExitDirections;
		if (validDirections == 0)
			{
			switch (positionNumber)
				{
				case Mi_CENTER_LOCATION		:
					validDirections = Mi_ALL_DIRECTIONS;
					break;
				case Mi_LEFT_LOCATION		:
				case Mi_WNW_LOCATION		:
				case Mi_WSW_LOCATION		:
					validDirections = Mi_LEFT;
					if (shuntPointDistance != 0)
						validDirections += Mi_UP + Mi_DOWN;
					break;
				case Mi_RIGHT_LOCATION		:
				case Mi_ENE_LOCATION		:
				case Mi_ESE_LOCATION		:
					validDirections = Mi_RIGHT;
					if (shuntPointDistance != 0)
						validDirections += Mi_UP + Mi_DOWN;
					break;
				case Mi_BOTTOM_LOCATION		:
				case Mi_SWS_LOCATION		:
				case Mi_SES_LOCATION		:
					validDirections = Mi_DOWN;
					if (shuntPointDistance != 0)
						validDirections += Mi_LEFT + Mi_RIGHT;
					break;
				case Mi_TOP_LOCATION		:
				case Mi_NWN_LOCATION		:
				case Mi_NEN_LOCATION		:
					validDirections = Mi_UP;
					if (shuntPointDistance != 0)
						validDirections += Mi_LEFT + Mi_RIGHT;
					break;
				case Mi_LOWER_LEFT_LOCATION	:
					validDirections = Mi_LEFT + Mi_DOWN;
					break;
				case Mi_LOWER_RIGHT_LOCATION	:
					validDirections = Mi_RIGHT + Mi_DOWN;
					break;
				case Mi_UPPER_LEFT_LOCATION	:
					validDirections = Mi_LEFT + Mi_UP;
					break;
				case Mi_UPPER_RIGHT_LOCATION	:
					validDirections = Mi_RIGHT + Mi_UP;
					break;
				default:
					validDirections = Mi_ALL_DIRECTIONS;
				}
			}
		return(validDirections);
		}
	public		MiiManagedPointRule	copy()
		{
		MiConnectionPointRule rule = new MiConnectionPointRule();
		rule.draggable			= draggable;
		rule.autoSpreadConnections	= autoSpreadConnections;
		rule.fanStyle			= fanStyle;
		rule.fanDistance		= fanDistance;
		rule.validConnectionSource	= validConnectionSource;
		rule.validConnectionDestination	= validConnectionDestination;
		rule.validExitDirections	= validExitDirections;
		rule.shuntPointDistance		= shuntPointDistance;
		return(rule);
		}
	}

