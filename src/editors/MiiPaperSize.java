
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
public interface MiiPaperSize
	{
	String		Mi_CUSTOM_PAPER_SIZE_NAME		= "Mi_CUSTOM_PAPER_SIZE_NAME";
	String		Mi_STANDARD_LETTER_PAPER_SIZE_NAME	= "Mi_STANDARD_LETTER_PAPER_SIZE_NAME";
	String		Mi_STANDARD_FOLIO_PAPER_SIZE_NAME	= "Mi_STANDARD_FOLIO_PAPER_SIZE_NAME";
	String		Mi_STANDARD_LEGAL_PAPER_SIZE_NAME	= "Mi_STANDARD_LEGAL_PAPER_SIZE_NAME";
	String		Mi_STANDARD_TABLOID_PAPER_SIZE_NAME	= "Mi_STANDARD_TABLOID_PAPER_SIZE_NAME";
	String		Mi_METRIC_A0_PAPER_SIZE_NAME		= "Mi_METRIC_A0_PAPER_SIZE_NAME";
	String		Mi_METRIC_A1_PAPER_SIZE_NAME		= "Mi_METRIC_A1_PAPER_SIZE_NAME";
	String		Mi_METRIC_A2_PAPER_SIZE_NAME		= "Mi_METRIC_A2_PAPER_SIZE_NAME";
	String		Mi_METRIC_A3_PAPER_SIZE_NAME		= "Mi_METRIC_A3_PAPER_SIZE_NAME";
	String		Mi_METRIC_A4_PAPER_SIZE_NAME		= "Mi_METRIC_A4_PAPER_SIZE_NAME";
	String		Mi_METRIC_A5_PAPER_SIZE_NAME		= "Mi_METRIC_A5_PAPER_SIZE_NAME";
	String		Mi_ANSI_ENG_A_PAPER_SIZE_NAME		= "Mi_ANSI_ENG_A_PAPER_SIZE_NAME";
	String		Mi_ANSI_ENG_B_PAPER_SIZE_NAME		= "Mi_ANSI_ENG_B_PAPER_SIZE_NAME";
	String		Mi_ANSI_ENG_C_PAPER_SIZE_NAME		= "Mi_ANSI_ENG_C_PAPER_SIZE_NAME";
	String		Mi_ANSI_ENG_D_PAPER_SIZE_NAME		= "Mi_ANSI_ENG_D_PAPER_SIZE_NAME";
	String		Mi_ANSI_ENG_E_PAPER_SIZE_NAME		= "Mi_ANSI_ENG_E_PAPER_SIZE_NAME";
	String		Mi_ANSI_ARCH_A_PAPER_SIZE_NAME		= "Mi_ANSI_ARCH_A_PAPER_SIZE_NAME";
	String		Mi_ANSI_ARCH_B_PAPER_SIZE_NAME		= "Mi_ANSI_ARCH_B_PAPER_SIZE_NAME";
	String		Mi_ANSI_ARCH_C_PAPER_SIZE_NAME		= "Mi_ANSI_ARCH_C_PAPER_SIZE_NAME";
	String		Mi_ANSI_ARCH_D_PAPER_SIZE_NAME		= "Mi_ANSI_ARCH_D_PAPER_SIZE_NAME";
	String		Mi_ANSI_ARCH_E_PAPER_SIZE_NAME		= "Mi_ANSI_ARCH_E_PAPER_SIZE_NAME";

	MiPaperSize		customSize 	= new MiPaperSize(
									Mi_CUSTOM_PAPER_SIZE_NAME, 
									new MiSize(8.5, 11.0),
									MiiDistanceUnits.inches);
	MiPaperSize		standardLetter 	= new MiPaperSize(
									Mi_STANDARD_LETTER_PAPER_SIZE_NAME, 
									new MiSize(8.5, 11.0),
									MiiDistanceUnits.inches);
	MiPaperSize		standardFolio 	= new MiPaperSize(
									Mi_STANDARD_FOLIO_PAPER_SIZE_NAME, 
									new MiSize(8.5, 13.0),
									MiiDistanceUnits.inches);
	MiPaperSize		standardLegal 	= new MiPaperSize(
									Mi_STANDARD_LEGAL_PAPER_SIZE_NAME, 
									new MiSize(8.5, 14.0),
									MiiDistanceUnits.inches);
	MiPaperSize		standardTabloid	= new MiPaperSize(
									Mi_STANDARD_TABLOID_PAPER_SIZE_NAME, 
									new MiSize(11.0, 17.0),
									MiiDistanceUnits.inches);

	MiPaperSize		metricA5	= new MiPaperSize(
									Mi_METRIC_A5_PAPER_SIZE_NAME, 
									new MiSize(14.8, 21.0),
									MiiDistanceUnits.centimeters);
	MiPaperSize		metricA4	= new MiPaperSize(
									Mi_METRIC_A4_PAPER_SIZE_NAME, 
									new MiSize(21.0, 29.7),
									MiiDistanceUnits.centimeters);
	MiPaperSize		metricA3	= new MiPaperSize(
									Mi_METRIC_A3_PAPER_SIZE_NAME, 
									new MiSize(29.7, 42.0),
									MiiDistanceUnits.centimeters);
	MiPaperSize		metricA2	= new MiPaperSize(
									Mi_METRIC_A2_PAPER_SIZE_NAME, 
									new MiSize(42.0, 59.4),
									MiiDistanceUnits.centimeters);
	MiPaperSize		metricA1	= new MiPaperSize(
									Mi_METRIC_A1_PAPER_SIZE_NAME, 
									new MiSize(59.4, 84.1),
									MiiDistanceUnits.centimeters);
	MiPaperSize		metricA0	= new MiPaperSize(
									Mi_METRIC_A0_PAPER_SIZE_NAME, 
									new MiSize(84.1, 118.9),
									MiiDistanceUnits.centimeters);

	MiPaperSize		ansiEngineeringA= new MiPaperSize(
									Mi_ANSI_ENG_A_PAPER_SIZE_NAME, 
									new MiSize(8.5, 11.0),
									MiiDistanceUnits.inches);
	MiPaperSize		ansiEngineeringB= new MiPaperSize(
									Mi_ANSI_ENG_B_PAPER_SIZE_NAME, 
									new MiSize(11.0, 17.0),
									MiiDistanceUnits.inches);
	MiPaperSize		ansiEngineeringC= new MiPaperSize(
									Mi_ANSI_ENG_C_PAPER_SIZE_NAME, 
									new MiSize(17.0, 22.0),
									MiiDistanceUnits.inches);
	MiPaperSize		ansiEngineeringD= new MiPaperSize(
									Mi_ANSI_ENG_D_PAPER_SIZE_NAME, 
									new MiSize(22.0, 34.0),
									MiiDistanceUnits.inches);
	MiPaperSize		ansiEngineeringE= new MiPaperSize(
									Mi_ANSI_ENG_E_PAPER_SIZE_NAME, 
									new MiSize(34.0, 44.0),
									MiiDistanceUnits.inches);

	MiPaperSize		ansiArchitectureA
									= new MiPaperSize(
									Mi_ANSI_ARCH_A_PAPER_SIZE_NAME, 
									new MiSize(9.0, 12.0),
									MiiDistanceUnits.inches);
	MiPaperSize		ansiArchitectureB
									= new MiPaperSize(
									Mi_ANSI_ARCH_B_PAPER_SIZE_NAME, 
									new MiSize(12.0, 18.0),
									MiiDistanceUnits.inches);
	MiPaperSize		ansiArchitectureC
									= new MiPaperSize(
									Mi_ANSI_ARCH_C_PAPER_SIZE_NAME, 
									new MiSize(18.0, 24.0),
									MiiDistanceUnits.inches);
	MiPaperSize		ansiArchitectureD
									= new MiPaperSize(
									Mi_ANSI_ARCH_D_PAPER_SIZE_NAME, 
									new MiSize(24.0, 36.0),
									MiiDistanceUnits.inches);
	MiPaperSize		ansiArchitectureE
									= new MiPaperSize(
									Mi_ANSI_ARCH_E_PAPER_SIZE_NAME, 
									new MiSize(30.0, 42.0),
									MiiDistanceUnits.inches);

	public		MiSize		getSize();
	public		String		getName();
	public		MiDistanceUnits	getUnits();
	}

