
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

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiDistanceUnits
	{
	String			Mi_INCHES_NAME			= "Mi_INCHES_NAME";
	String			Mi_INCHES_ABBREV_NAME		= "Mi_INCHES_ABBREV_NAME";
	String			Mi_CENTIMETERS_NAME		= "Mi_CENTIMETERS_NAME";
	String			Mi_CENTIMETERS_ABBREV_NAME	= "Mi_CENTIMETERS_ABBREV_NAME";
	String			Mi_PIXELS_NAME			= "Mi_PIXELS_NAME";
	String			Mi_PIXELS_ABBREV_NAME		= "Mi_PIXELS_ABBREV_NAME";

	MiDistanceUnits	inches 			= new MiDistanceUnits(
									Mi_INCHES_NAME,
									Mi_INCHES_ABBREV_NAME, 
									72.0);
	MiDistanceUnits	centimeters 		= new MiDistanceUnits(
									Mi_CENTIMETERS_NAME, 
									Mi_CENTIMETERS_ABBREV_NAME,
									72.0/2.5);
	MiDistanceUnits	pixels 			= new MiDistanceUnits(
									Mi_PIXELS_NAME,
									Mi_PIXELS_ABBREV_NAME,
									1.0);
	}

