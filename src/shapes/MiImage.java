
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

import java.awt.Image; 
import java.awt.image.ImageObserver; 
import com.swfm.mica.util.Utility;

/**
 * If foreground color is Mi_TRANSPARENT_COLOR then this image will not be drawn.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiImage extends MiPart implements ImageObserver
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;

	private		Image		image;
	private		boolean		sizeSpecified;
	private		String		specifiedFilename;
	private		String		filename;
	private		boolean		waitForImageToFinishDrawing 	= true;
	private		MiBounds	tmpBounds			= new MiBounds();



	public				MiImage()
		{
		setBorderLook(Mi_NONE);
		}

	public				MiImage(Image i)
		{
		setBorderLook(Mi_NONE);
		setImage(i);
		}

	public				MiImage(String filename)
		{
		this(filename, true);
		}

	public				MiImage(String imageFilename, boolean waitForImageToLoad)
		{
		setBorderLook(Mi_NONE);
		if (imageFilename != null)
			{
			specifiedFilename = imageFilename;
			filename = MiSystem.getProperty(imageFilename, imageFilename);
			// This causes GraphicsNode to save backslashes in the text grml file: filename = Utility.portFileNameToCurrentPlatform(filename);
			setImage(MiImageManager.getImage(filename, waitForImageToLoad));
			}
		}

	public				MiImage(String filename, MiDistance width, MiDistance height, boolean waitForImageToLoad)
		{
		this(filename, waitForImageToLoad);
		sizeSpecified = true;
		tmpBounds.setBounds(0, 0, width, height);
		replaceBounds(tmpBounds);
		}

	public				MiImage(String filename, MiDeviceDistance width, MiDeviceDistance height, boolean waitForImageToLoad)
		{
		this(filename, waitForImageToLoad);
		sizeSpecified = true;
		tmpBounds.setBounds(0, 0, width, height);
		replaceBounds(tmpBounds);
		}

	public		void		setImage(String imageFilename) 	
		{
		if (imageFilename == null)
			{
			filename = null;
			setImage((Image )null);
			}
		else
			{
			specifiedFilename = imageFilename;
			filename = MiSystem.getProperty(imageFilename, imageFilename);
			//filename = Utility.portFileNameToCurrentPlatform(filename);
			setImage(MiImageManager.getImage(filename, true));
			}
		}
	public		void		setImageData(String imageData) 	
		{
		if (imageData == null)
			{
			filename = null;
			setImage((Image )null);
			}
		else
			{
			specifiedFilename = null;
			filename = null;
			//filename = Utility.portFileNameToCurrentPlatform(filename);
			int index = imageData.indexOf(':');
			String sizeData = imageData.substring(0, index);
			imageData = imageData.substring(index + 1);
			index = sizeData.indexOf('x');
			String widthStr = sizeData.substring(0, index);
			String heightStr = sizeData.substring(index + 1);
			byte[] data = Utility.uudecode(imageData);
			setImage(MiImageManager.getImage(data, Utility.toInteger(widthStr), Utility.toInteger(heightStr), true));
			}
		}
	public	void			setImage(Image i) 	
		{ 
		image = i; 
		if (image == null)
			{
			invalidateArea();
			tmpBounds.reverse();
			replaceBounds(tmpBounds);
			return;
			}

		if ((image.getWidth(null) > 0) && (image.getHeight(null) > 0))
			{
			setWidth(image.getWidth(null));
			setHeight(image.getHeight(null));
			}
		else
			{
			image.getWidth(this);
			if (image != null)
				image.getHeight(this);
			}
		invalidateArea();
		}
	public	Image			getImage()
		{ 
		return(image); 
		}
	public		String		getImageData() 	
		{
		int[] intData = MiImageManager.getImagePixels(image, 0, 0, image.getWidth(null), image.getHeight(null));
		byte[] data = new byte[intData.length * 4];
		for (int i = 0; i < intData.length; ++i)
			{
			int value = intData[i];
			data[i * 4] 	= (byte )((value >> 24) & 0xff);
			data[i * 4 + 1] = (byte )((value >> 16) & 0xff);
			data[i * 4 + 2] = (byte )((value >> 8) & 0xff);
			data[i * 4 + 3] = (byte )(value & 0xff);
			}
		
		String imageData = Utility.uuencode(data);
		imageData = image.getWidth(null) + "x" + image.getHeight(null) + ":" + imageData;
		return(imageData);
		}

	public		void		setWaitForImageToFinishDrawing(boolean flag)
		{
		waitForImageToFinishDrawing = flag;
		}
	public		boolean		getWaitForImageToFinishDrawing()
		{
		return(waitForImageToFinishDrawing);
		}

	public		String		getFilename()
		{ 
		return(filename); 
		}
	public		void		flush()
		{
		if (filename != null)
			MiImageManager.flushCaches(filename);
		// In case the image was not associated with a filename
		if (image != null)
			image.flush();
		}

	public		void		refreshLookAndFeel()
		{
		if (specifiedFilename != null)
			{
			String name = MiSystem.getProperty(specifiedFilename, specifiedFilename);
			// name = Utility.portFileNameToCurrentPlatform(name);
			if (!name.equals(filename))
				{
				String save = specifiedFilename;
				MiSize size = new MiSize();
				getSize(size);
				setImage(name);
				setSize(size);
				specifiedFilename = save;
				}
			}
		super.refreshLookAndFeel();
		}
					/**------------------------------------------------------
					 * Gets the shape of any shadow. Used by the shadow 
					 * renderers. By default the shadow shape is null. Override
					 * and return the actual shape (perhaps 
					 * MiiShadowRenderer.rectangularShadowShape) or null, which
					 * indicates to the shadow renderer that this part itself 
					 * is to be the shape.
					 * @return		the shape
					 *------------------------------------------------------*/
	public		MiPart		getShadowShape()
		{
		return(MiImageManager.getImageShadow(this));
		}
	public 	boolean 		imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
		{
//System.out.println(filename + ":Image: " + img + " update: infoflags = " + infoflags + " w,h = " + width + ", " + height);
		// FIX: get a lock on the parent canvas cause this is a different thread and should
		// NOT be modifying damaged rectangle lists at the same time as the event handling
		// thread.
		if (img != image)
			{
			// A desensitized image ...
			if ((infoflags & (WIDTH | HEIGHT)) != 0)
				{
				invalidateArea();
				return(false);
				}
			return(true);
			}

		if ((infoflags & SOMEBITS) != 0)
			{
			// Invalidate only the area that has more bits (x,y,width,height)?
			// Wait for allbits: invalidateArea();
			return(true);
			}
		if ((infoflags & ALLBITS) != 0)
			{
			invalidateArea();
			return(true);
			}
		if ((infoflags & WIDTH) != 0)
			{
			if (!sizeSpecified)
				setWidth(width);
			}
		if ((infoflags & HEIGHT) != 0)
			{
			if (!sizeSpecified)
				setHeight(height);
			}
		if ((infoflags & (ERROR | ABORT)) != 0)
			{
			if (filename != null)
				MiDebug.println("Error occured while processing image: " + filename);
			else
				MiDebug.println("Error occured while processing image");
			setImage((Image )null);
			return(false);
			}

		if ((infoflags & (WIDTH | HEIGHT)) != 0)
			{
			invalidateArea();
			if (!sizeSpecified)
				invalidateLayout();
			return(false);
			}
		// ALLBITS...
		return(true);
		}
					/**------------------------------------------------------
					 * Sets whether this MiPart is sensitive. Makes sure that
					 * the bounds are invalidated so that the image will be
					 * redrawn.
					 * @param flag 		true if this is to be sensitive
					 * @overrides 		MiPart#setSensitive
					 *------------------------------------------------------*/
	public		void		setSensitive(boolean flag)
		{ 
		super.setSensitive(flag);
		invalidateArea();
		}
	protected 	void		render(MiRenderer renderer)
		{
		if (image != null)
			{
			Image im = null;

			renderer.setAttributes(getAttributes());
			getBounds(tmpBounds);

			if (!isSensitive())
				{
				im = MiImageManager.getGrayedImage(this);
				}
			else
				{
				im = image;
				}

/* Only some images are printed... another bug in AWT
			if ((renderer.isPrinterRenderer()) && (isPrintable()))
				{
				while (im.getWidth(this) < 1)
					;
				}
***/


			renderer.drawImage(im, tmpBounds, this);
			}
		}
	public		String		toString()
		{
		if (filename != null)
			{
			return(super.toString() + "[" + filename + "][" 
				+ Utility.toShortString(getWidth()) + "X" + Utility.toShortString(getHeight()) + "]");
			}
		else
			{
			return(super.toString() + "[programmatically created]["
				+ Utility.toShortString(getWidth()) + "X" + Utility.toShortString(getHeight()) + "]");
			}
		}
/* ******* TEST: this prevents interactive resizing to a smaller size
	protected	void		calcMinimumSize(MiSize size)
		{
		calcPreferredSize(size);
		}
***/
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiImage obj 		= (MiImage )source;
		image			= obj.image;
		sizeSpecified		= obj.sizeSpecified;
		filename		= obj.filename;

		if (((obj.getWidth() == 0) || (obj.getHeight() == 0)) && (obj.image != null))
			{
			image.getWidth(this);
			image.getHeight(this);
			}
		}

					/**------------------------------------------------------
					 * Sets the property with the given name to the given
					 * value. If no such property is found then sets the attribute
					 * with the given name to the given value. Valid attribute 
					 * names are found in the MiiNames.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_IMAGE_TYPE_NAME))
			setImage(value);
		else if (name.equalsIgnoreCase(Mi_IMAGE_UUENCODED_DATA))
			setImageData(value);
		else
			super.setPropertyValue(name, value);
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
		if (name.equalsIgnoreCase(Mi_IMAGE_TYPE_NAME))
			return(getFilename());
		else if (name.equalsIgnoreCase(Mi_IMAGE_UUENCODED_DATA))
			return(getImageData());
		else
			return(super.getPropertyValue(name));
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

		propertyDescriptions = new MiPropertyDescriptions("MiImage");

		propertyDescriptions.addElement(
			new MiPropertyDescription(
			Mi_IMAGE_TYPE_NAME, Mi_FILE_NAME_TYPE, ""));
		propertyDescriptions.addElement(
			new MiPropertyDescription(
			Mi_IMAGE_UUENCODED_DATA, Mi_STRING_TYPE, ""));
		propertyDescriptions = new MiPropertyDescriptions(propertyDescriptions);
		propertyDescriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());

		return(propertyDescriptions);
		}
	}


