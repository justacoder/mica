
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
class MiDistanceUnits implements MiiDistanceUnits
	{
	private		String		name;
	private		String		abbreviation;
	private		double		pixelsPerUnit;

	static	{
		MiSystem.setApplicationDefaultProperty(Mi_INCHES_NAME, 		"inches");
		MiSystem.setApplicationDefaultProperty(Mi_INCHES_ABBREV_NAME, 	"in.");
		MiSystem.setApplicationDefaultProperty(Mi_CENTIMETERS_NAME, 	"centimeters");
		MiSystem.setApplicationDefaultProperty(Mi_CENTIMETERS_ABBREV_NAME,"cm");
		MiSystem.setApplicationDefaultProperty(Mi_PIXELS_NAME, 		"pixels");
		MiSystem.setApplicationDefaultProperty(Mi_PIXELS_ABBREV_NAME, 	"");
		}

	public				MiDistanceUnits(String name, String abbreviation, double pixelsPerUnit)
		{
		this.name = name;
		this.abbreviation = abbreviation;
		this.pixelsPerUnit = pixelsPerUnit;
		}
	public		double		getPixelsPerUnit()
		{
		return(pixelsPerUnit);
		}
	public		String		getName()
		{
		return(MiSystem.getProperty(name, name));
		}
	public		String		getAbbreviation()
		{
		return(MiSystem.getProperty(abbreviation, abbreviation));
		}
	public		String		toString()
		{
		return(getName());
		}
	}

