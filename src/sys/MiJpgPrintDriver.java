
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiJpgPrintDriver implements MiiPrintDriver, MiiNames, MiiPropertyTypes
	{
	private static	MiPropertyDescriptions		propertyDescriptions;

	private		int		width;
	private		int		height;
	private		int		totalNumberOfPages;
	private		int		pageNumber;
	private		String		filename;
	private		Image		image;
	private		MiMargins 	margins = new MiMargins(0);


	public				MiJpgPrintDriver()
		{
		this(200, 200);
		}
	public				MiJpgPrintDriver(int width, int height)
		{
		this.width = width;
		this.height = height;
		}
	public		void		setWidth(int w)
		{
		width = w;
		}
	public		int		getWidth()
		{
		return(width);
		}
	public		void		setHeight(int h)
		{
		height = h;
		}
	public		int		getHeight()
		{
		return(height);
		}
	protected	void		clearImage()
		{
		Graphics g = image.getGraphics();
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, width, height);
		}
	public		Graphics 	getGraphics()
		{
		image = MiImageManager.createImage(width, height);
		clearImage();
		return(image.getGraphics());
		}
	public		MiBounds 	getDeviceBounds()
		{
		return(new MiBounds(0, 0, width, height));
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
		if (totalNumberOfPages > 1)
			MiJpgImage.save(image, filename + ".page" + pageNumber++ + ".jpg");
		else
			MiJpgImage.save(image, filename + ".jpg");
		}
	public		void		newPage()
		{
		if (totalNumberOfPages > 1)
			MiJpgImage.save(image, filename + ".page" + pageNumber++ + ".jpg");
		else
			MiJpgImage.save(image, filename + ".jpg");
		clearImage();
		}
	public		boolean		configure(
						String filename, 
						int dotsPerInch, 
						String standardPaperSize, 
						String portraitOrLandscape, 
						String colorOutputScheme,
						int totalNumberOfPages)
		{
		this.filename = filename;
		if (filename.endsWith(".jpg"))
			this.filename = filename.substring(0, filename.length() - ".jpg".length());
		this.totalNumberOfPages = totalNumberOfPages;
		pageNumber = 0;
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

		MiPropertyDescription desc;

		desc = new MiPropertyDescription(Mi_WIDTH_NAME, Mi_POSITIVE_INTEGER_TYPE, "400");
		desc.setMinimumValue(20);
		desc.setMaximumValue(5000);
		propertyDescriptions.addElement(desc);

		desc = new MiPropertyDescription(Mi_HEIGHT_NAME, Mi_POSITIVE_INTEGER_TYPE, "400");
		desc.setMinimumValue(20);
		desc.setMaximumValue(5000);
		propertyDescriptions.addElement(desc);

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
		if (name.equalsIgnoreCase(Mi_WIDTH_NAME))
			setWidth(Utility.toInteger(value));
		else if (name.equalsIgnoreCase(Mi_HEIGHT_NAME))
			setHeight(Utility.toInteger(value));
		else
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
		if (name.equalsIgnoreCase(Mi_WIDTH_NAME))
			return("" + getWidth());
		else if (name.equalsIgnoreCase(Mi_HEIGHT_NAME))
			return("" + getHeight());
		else
			throw new IllegalArgumentException(this + ": Unknown property: \"" + name + "\"");
		}
	}


