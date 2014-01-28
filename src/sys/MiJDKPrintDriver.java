
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
import java.awt.PrintJob; 
import java.awt.PageAttributes; 
import java.awt.JobAttributes; 
import java.awt.Graphics; 
import java.awt.Frame; 
import java.awt.Dimension; 
import java.awt.Toolkit; 
import java.util.Properties; 
import java.io.PrintWriter; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiJDKPrintDriver implements MiiPrintDriver, MiiTypes
	{
	private static	MiPropertyDescriptions	propertyDescriptions;


	private		Frame		frame;
	private		Properties	configuration = new Properties();
	private		PrintJob 	job;
	private		Graphics 	currentPageGraphics;
	private		MiMargins 	margins = new MiMargins(18);  // 1/4 inch, 18 points


	public				MiJDKPrintDriver(Frame frame)
		{
		this.frame = frame;
		}
	public		Graphics 	getGraphics()
		{
		return(currentPageGraphics);
		}
	public		MiBounds 	getDeviceBounds()
		{
		Dimension d = job.getPageDimension();
		return(new MiBounds(0, 0, d.width, d.height));
		}
	public		MiMargins 	getMargins()
		{
		return(margins);
		}
	public		void		setBoundsOfGraphicalContent(MiBounds contentBounds)
		{
		}
	public		void		init()
		{
		}
	public		void		termin()
		{
		//currentPageGraphics.finalize();
		job.end();
		}
	public		void		newPage()
		{
		//currentPageGraphics.finalize();
		currentPageGraphics = job.getGraphics();
		}
	public		boolean		configure(
						String filename, 
						int dotsPerInch, 
						String standardPaperSize, 
						String portraitOrLandscape, 
						String colorOutputScheme,
						int totalNumberOfPages)
		{
		JobAttributes jobAttributes = new JobAttributes();
		jobAttributes.setMinPage(1);
		jobAttributes.setMaxPage(totalNumberOfPages);

		PageAttributes pageAttributes = new PageAttributes();
		if (portraitOrLandscape.equals(Mi_LANDSCAPE_NAME))
			{
			pageAttributes.setOrientationRequested(
				PageAttributes.OrientationRequestedType.LANDSCAPE);
			}

		job = Toolkit.getDefaultToolkit().getPrintJob(frame, "Print", jobAttributes, pageAttributes);
		if (job == null)
			{
			return(false);
			}

		currentPageGraphics = job.getGraphics();
		return(true);
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

		propertyDescriptions = new MiPropertyDescriptions();
		return(propertyDescriptions);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given
					 * value. 
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		throw new IllegalArgumentException(this + ": Unknown property: \"" + name + "\"");
		}

					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		throw new IllegalArgumentException(this + ": Unknown property: \"" + name + "\"");
		}
	}


