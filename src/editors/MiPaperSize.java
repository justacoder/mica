
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
import com.swfm.mica.util.Strings; 
import java.util.Vector;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPaperSize implements MiiPaperSize
	{
	private		String			name;
	private		MiSize			size;
	private		MiDistanceUnits		units;
	private static	Vector			paperSizes;

	static	{
		MiSystem.setMicaDefaultProperty(Mi_CUSTOM_PAPER_SIZE_NAME, 	"custom");
		MiSystem.setMicaDefaultProperty(Mi_STANDARD_LETTER_PAPER_SIZE_NAME, "letter");
		MiSystem.setMicaDefaultProperty(Mi_STANDARD_FOLIO_PAPER_SIZE_NAME, "folio");
		MiSystem.setMicaDefaultProperty(Mi_STANDARD_LEGAL_PAPER_SIZE_NAME, "legal");
		MiSystem.setMicaDefaultProperty(Mi_STANDARD_TABLOID_PAPER_SIZE_NAME, "tabloid");
		MiSystem.setMicaDefaultProperty(Mi_METRIC_A0_PAPER_SIZE_NAME, "metric A0");
		MiSystem.setMicaDefaultProperty(Mi_METRIC_A1_PAPER_SIZE_NAME, "metric A1");
		MiSystem.setMicaDefaultProperty(Mi_METRIC_A2_PAPER_SIZE_NAME, "metric A2");
		MiSystem.setMicaDefaultProperty(Mi_METRIC_A3_PAPER_SIZE_NAME, "metric A3");
		MiSystem.setMicaDefaultProperty(Mi_METRIC_A4_PAPER_SIZE_NAME, "metric A4");
		MiSystem.setMicaDefaultProperty(Mi_METRIC_A5_PAPER_SIZE_NAME, "metric A5");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ENG_A_PAPER_SIZE_NAME, "ansi Eng A");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ENG_B_PAPER_SIZE_NAME, "ansi Eng B");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ENG_C_PAPER_SIZE_NAME, "ansi Eng C");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ENG_D_PAPER_SIZE_NAME, "ansi Eng D");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ENG_E_PAPER_SIZE_NAME, "ansi Eng E");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ARCH_A_PAPER_SIZE_NAME, "ansi Arch A");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ARCH_B_PAPER_SIZE_NAME, "ansi Arch B");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ARCH_C_PAPER_SIZE_NAME, "ansi Arch C");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ARCH_D_PAPER_SIZE_NAME, "ansi Arch D");
		MiSystem.setMicaDefaultProperty(Mi_ANSI_ARCH_E_PAPER_SIZE_NAME, "ansi Arch E");
		}


	public				MiPaperSize(
						String name, MiSize size, MiDistanceUnits units)
		{
		this.name = name;
		this.size = size;
		this.units = units;
		}
	
	public		MiSize		getSize()
		{
		return(new MiSize(size));
		}
	public		MiSize		getSize(MiDistanceUnits targetUnits)
		{
		double pixelsPerUnitSquared = units.getPixelsPerUnit() * targetUnits.getPixelsPerUnit();
		return(new MiSize(
			size.width * pixelsPerUnitSquared/targetUnits.getPixelsPerUnit(), 
			size.height * pixelsPerUnitSquared/targetUnits.getPixelsPerUnit()));
		}
	public		String		getName()
		{
		return(MiSystem.getProperty(name, name));
		}
	public		MiDistanceUnits	getUnits()
		{
		return(units);
		}
	public		String		toString()
		{
		return(getName() + " [" + size.width + "x" + size.height + " " + units.getName() + "]");
		}

	public static	MiPaperSize getPageSize(String pageSizeName)
		{
		if (paperSizes == null)
			createPaperSizesList();

		for (int i = 0; i < paperSizes.size(); ++i)
			{
			if (((MiPaperSize )paperSizes.elementAt(i)).getName().equals(pageSizeName))
				return((MiPaperSize )paperSizes.elementAt(i));
			if (((MiPaperSize )paperSizes.elementAt(i)).toString().equals(pageSizeName))
				return((MiPaperSize )paperSizes.elementAt(i));
			}
		return(null);
		}
	public static	Strings		getPageSizeNames()
		{
		if (paperSizes == null)
			createPaperSizesList();

		Strings names = new Strings();
		for (int i = 0; i < paperSizes.size(); ++i)
			{
			names.addElement(((MiPaperSize )paperSizes.elementAt(i)).getName());
			}
		return(names);
		}
	public static	Strings		getPageSizeNamesWithSizes()
		{
		if (paperSizes == null)
			createPaperSizesList();

		Strings names = new Strings();
		for (int i = 0; i < paperSizes.size(); ++i)
			{
			names.addElement(((MiPaperSize )paperSizes.elementAt(i)).toString());
			}
		return(names);
		}
	public static	Vector		getPaperSizesList()
		{
		if (paperSizes == null)
			createPaperSizesList();

		return(paperSizes);
		}
	private static	void		createPaperSizesList()
		{
		paperSizes = new Vector();
		paperSizes.addElement(customSize);
		paperSizes.addElement(standardLetter);
		paperSizes.addElement(standardFolio);
		paperSizes.addElement(standardLegal);
		paperSizes.addElement(standardTabloid);
		paperSizes.addElement(metricA5);
		paperSizes.addElement(metricA4);
		paperSizes.addElement(metricA3);
		paperSizes.addElement(metricA2);
		paperSizes.addElement(metricA1);
		paperSizes.addElement(metricA0);
		paperSizes.addElement(ansiEngineeringA);
		paperSizes.addElement(ansiEngineeringB);
		paperSizes.addElement(ansiEngineeringC);
		paperSizes.addElement(ansiEngineeringD);
		paperSizes.addElement(ansiEngineeringE);
		paperSizes.addElement(ansiArchitectureA);
		paperSizes.addElement(ansiArchitectureB);
		paperSizes.addElement(ansiArchitectureC);
		paperSizes.addElement(ansiArchitectureD);
		paperSizes.addElement(ansiArchitectureE);
		}
		
	}

