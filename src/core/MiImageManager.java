
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
import com.swfm.mica.util.Utility; 
import java.util.Hashtable; 
import java.util.Vector; 
import java.util.Enumeration; 
import java.awt.Image; 
import java.awt.Canvas; 
import java.awt.Color; 
import java.awt.Toolkit; 
import java.awt.Component; 
import java.awt.MediaTracker; 
import java.awt.image.ImageObserver; 
import java.awt.image.PixelGrabber; 
import java.awt.image.MemoryImageSource; 
import java.net.URL; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiImageManager 
	{
	private static	Vector 		nativeComponents		= new Vector();
	private	final static Component 	component 			= new Component() {};
	private static	MediaTracker 	mediaTracker			= new MediaTracker(component);
	private static	MiXpmImage 	xpmImageMan 			= new MiXpmImage();
	public static	int		MAXIMUM_TIME_TO_WAIT_MILLISECS 	= 2000;
	private static	Hashtable 	cachedImages;
	private static	Hashtable 	cachedXPMImages;
	private static	Hashtable 	cachedShadowImages;
	private static	Hashtable 	cachedDesensitizedImages;
		// Between 0 and 1.0, if 1.0 all colors are converted to the target color
		// if 0.5 then all colors are converted to the color in RGB space 1/2
		// of the way to the target color.
	public static	double 		grayedConvergenceFactor 	= 0.6;
	public static	Color 		grayedTargetColor	 	= MiColorManager.white;




					/**------------------------------------------------------
	 				 * Set the awt.component that this will use to create images 
					 * and to make a MediaTracker that this class will use to load
					 * images.
					 * @param component		any java.awt.Component that
					 *				has a peer.
					 *------------------------------------------------------*/
	protected static void		setNativeComponent(Component component)
		{
		nativeComponents.addElement(component);
		}

					/**------------------------------------------------------
	 				 * Creates an Image suitable for off-screen renderering (i.e.
					 * you can do a image.getGraphics()).
					 * @param width			the width
					 * @param height		the height
					 *------------------------------------------------------*/
	public static	Image		createImage(int width, int height)
		{
		for (int i = 0; i < nativeComponents.size(); ++i)
			{
			Component component = (Component )nativeComponents.elementAt(i);
			if (component.getPeer() != null)
				return(component.createImage(width, height));
			}
		return(null);
		}
	private static	MediaTracker	getMediaTracker()
		{
		for (int i = 0; i < nativeComponents.size(); ++i)
			{
			Component component = (Component )nativeComponents.elementAt(i);
			if (component.getPeer() != null)
				return(new MediaTracker(component));
			}
		return(new MediaTracker(new Canvas()));
		}
					/**------------------------------------------------------
	 				 * Load and return image found in filename.
					 * @param filename		location of the image
					 * @return			Image found in filename
					 *------------------------------------------------------*/
	public	static Image		getImage(String filename)
		{
		return(getImage(filename, false));
		}
					/**------------------------------------------------------
	 				 * Load and return image found in filename. Image format
					 * types supported are AWT types (GIF, JPG) and the X 
					 * Windows XPM type.
					 * @param filename		location of the image
					 * @param waitForImageToLoad	true if load is synchronous
					 * @return			Image found in filename
					 * @exception			IllegalArgumentException if
					 *				Invalid XPM image filename
					 *------------------------------------------------------*/
	public	static Image		getImage(String filename, boolean waitForImageToLoad)
		{
		Image image;
		if (filename.endsWith(".xpm"))
			{
			if (cachedXPMImages == null)
				cachedXPMImages = new Hashtable();
			image = (Image )cachedXPMImages.get(filename);
			if (image != null)
				return(image);

			//if (!waitForImageToLoad)
				{
				// Need a way to get awt to create an empty Image and then let
				// us 'grow' // this image to fill it with the target data.
				}
			// else
				{
				image = xpmImageMan.getImage(filename);
				if ((image == null) && (MiDebug.debug))
					{
					MiDebug.println("Unable to load XPM Image: " + filename);
					}
				}
			if (image != null)
				{
				cachedXPMImages.put(filename, image);
				return(image);
				}
			filename = MiSystem.getProperty("Mi_IMAGES_HOME") + "/tmp.gif";
			filename = Utility.portFileNameToCurrentPlatform(filename);
			}

		if (cachedImages == null)
			cachedImages = new Hashtable();
		image = (Image )cachedImages.get(filename);
		if (image != null)
			return(image);

		URL resource = null;
		if (MiSystem.getJDKVersion() >= 1.1)
			{
			try	{
				resource = MiSystem.getIOManager().getResourceAsURL(filename);
				}
			catch (Exception e)
				{
				}
			}
		if (resource != null)
			image = Toolkit.getDefaultToolkit().getImage(resource);
		else
			image = Toolkit.getDefaultToolkit().getImage(filename);

		//if (waitForImageToLoad)
			{
			image = waitForImageToLoad(image, filename);
			}
		if (image != null)
			cachedImages.put(filename, image);
		return(image);
		}
					/**------------------------------------------------------
	 				 * Load and return image found in imageData. Image format
					 * types supported are AWT types (GIF, JPG) and the X 
					 * Windows XPM type.
					 * This method fails for JDK 1.4.2 or later
					 * @param imageData		binary data of the image
					 * @param waitForImageToLoad	true if load is synchronous
					 * @return			Image found in filename
					 * @exception			IllegalArgumentException if
					 *				Invalid XPM image filename
					 *------------------------------------------------------*/
	public	static Image		getImage(byte[] imageData, boolean waitForImageToLoad)
		{
		Image image;
		if (cachedImages == null)
			cachedImages = new Hashtable();
		image = (Image )cachedImages.get(imageData);
		if (image != null)
			return(image);

//MiDebug.println("createImage for imagedata");
		image = Toolkit.getDefaultToolkit().createImage(imageData);
//MiDebug.println("image = " + image);

		//if (waitForImageToLoad)
			{
			image = waitForImageToLoad(image, "binary data");
			}
		if (image != null)
			{
			cachedImages.put(imageData, image);
			}

		return(image);
		}
	public	static Image		getImage(byte[] byteData, int width, int height, boolean waitForImageToLoad)
		{
		Image image;
		if (cachedImages == null)
			cachedImages = new Hashtable();
		image = (Image )cachedImages.get(byteData);
		if (image != null)
			return(image);

		int[] imageData = new int[width * height];
		for (int i = 0; i < imageData.length; ++i)
			{
			imageData[i]
			= (((((int )byteData[i * 4]) << 24) & 0xff000000)
			+	 ((((int )byteData[i * 4 + 1]) << 16) & 0x00ff0000)
			+	 ((((int )byteData[i * 4 + 2]) << 8) & 0x0000ff00)
			+	 ((((int )byteData[i * 4 + 3])) & 0x000000ff));
			}
		image = Toolkit.getDefaultToolkit().createImage(
			new MemoryImageSource(
				width, height, imageData, 0, width));

		//if (waitForImageToLoad)
			{
			image = waitForImageToLoad(image, "binary data");
			}
		if (image != null)
			{
			cachedImages.put(imageData, image);
			}

		return(image);
		}
					/**------------------------------------------------------
	 				 * Get subset area of the given Image. 
					 * @param src			the image source
					 * @param x			the sub-area x coordinate
					 * @param y			the sub-area y coordinate
					 * @param width			the width of the sub-area
					 * @param height		the height of the sub-area
					 * @return			the sub-area Image
					 *------------------------------------------------------*/
	public static	Image		getImageSubArea(Image src, int x, int y, int width, int height)
		{
		int[] pixels = getImagePixels(src, x, y, width, height);
		Image result = Toolkit.getDefaultToolkit().createImage(
			new MemoryImageSource(width, height, pixels, 0, width));
		return(result);
		}
	public static	MiImage		getImageShadow(MiImage src)
		{
		if (cachedShadowImages == null)
			cachedShadowImages = new Hashtable();

		String key = src.getFilename();
		if (key == null)
			key  = Integer.toHexString(src.hashCode());

		MiImage image = (MiImage )cachedShadowImages.get(key);
		if (image != null)
			return(image);

		int width = src.getImage().getWidth(null);
		int height = src.getImage().getHeight(null);
		int[] pixels = getImagePixels(src.getImage(), 0, 0, width, height);
		if (pixels == null)
			return(null);

		int shadowColor = src.getShadowColor().getRGB();
		for (int i = 0; i < pixels.length; ++i)
			{
			// If not transparent...
			if ((pixels[i] & 0xff000000) == 0xff000000)
				{
				pixels[i] = shadowColor;
				}
			}

		Image result = Toolkit.getDefaultToolkit().createImage(
			new MemoryImageSource(width, height, pixels, 0, width));

		image = new MiImage(result);

		if ((width != 0) && (height != 0))
			cachedShadowImages.put(key, image);
		return(image);
		}
	public static	Image		getGrayedImage(MiImage src)
		{
		if (cachedDesensitizedImages == null)
			cachedDesensitizedImages = new Hashtable();

		Image image = (Image )cachedDesensitizedImages.get(src.getFilename());
		if (image != null)
			return(image);

		int width = src.getImage().getWidth(null);
		int height = src.getImage().getHeight(null);
		int[] pixels = getImagePixels(src.getImage(), 0, 0, width, height);
		if (pixels == null)
			return(null);

		int targetGrayRed = grayedTargetColor.getRed();
		int targetGrayGreen = grayedTargetColor.getGreen();
		int targetGrayBlue = grayedTargetColor.getBlue();
		for (int i = 0; i < pixels.length; ++i)
			{
			// If not transparent...
			if ((pixels[i] & 0xff000000) == 0xff000000)
				{
				int red = (pixels[i] & 0x00ff0000) >> 16;
				int green = (pixels[i] & 0x0000ff00) >> 8;
				int blue = (pixels[i] & 0x000000ff);

				red -= (red - targetGrayRed) * grayedConvergenceFactor;
				green -= (green - targetGrayGreen) * grayedConvergenceFactor;
				blue -= (blue - targetGrayBlue) * grayedConvergenceFactor;
				pixels[i] = 0xff000000 + (red << 16) + (green << 8) + blue;
				}
			}

		image = Toolkit.getDefaultToolkit().createImage(
			new MemoryImageSource(
			width, height, java.awt.image.ColorModel.getRGBdefault(), pixels, 0, width));

		image = waitForImageToLoad(image, src.getFilename());

		if ((width != 0) && (height != 0))
			cachedDesensitizedImages.put(src.getFilename(), image);

		return(image);
		}
	public static	int[]		getImagePixels(Image src, int x, int y, int width, int height)
		{
		int[] pixels = new int[width * height];
		PixelGrabber pg = new PixelGrabber(src, x, y, width, height, pixels, 0, width);
		try 	{
	    		if (!pg.grabPixels())
				{
	    			MiDebug.println(
					"MiImageManager: Error occured while grabbing pixels in getImagePixels()!");
				return(null);
				}
			}
		catch (InterruptedException e)
			{
	    		MiDebug.println(
				"MiImageManager: Error occured while grabbing pixels in getImagePixels()!");
			return(null);
			}
		if ((pg.status() & ImageObserver.ABORT) != 0)
			{
	    		MiDebug.println(
				"MiImageManager: Error occured while grabbing pixels in getImagePixels()!");
			return(null);
			}

		return(pixels);
		}
	public static	void		flushCaches()
		{
		if (cachedImages != null)
			flushCache(cachedImages);
		if (cachedXPMImages != null)
			flushCache(cachedXPMImages);
		if (cachedShadowImages != null)
			flushCache(cachedShadowImages);
		if (cachedDesensitizedImages != null)
			flushCache(cachedDesensitizedImages);
		}
	public static	void		flushCaches(String filename)
		{
		if (cachedImages != null)
			flushCache(cachedImages, filename);
		if (cachedXPMImages != null)
			flushCache(cachedXPMImages, filename);
		if (cachedShadowImages != null)
			flushCache(cachedShadowImages, filename);
		if (cachedDesensitizedImages != null)
			flushCache(cachedDesensitizedImages, filename);
		}

	protected static void		flushCache(Hashtable imageCache)
		{
		Enumeration e = imageCache.keys();
		while (e.hasMoreElements())
			{
			String key = (String )e.nextElement();
			Image image = (Image )imageCache.get(key);
			image.flush();
			}
		imageCache.clear();
		}
	protected static void		flushCache(Hashtable imageCache, String filename)
		{
		Image image = (Image )imageCache.get(filename);
		if (image != null)
			{
			image.flush();
			imageCache.remove(filename);
			}
		}
	private	static	Image		waitForImageToLoad(Image image, String filename)
		{
		int waitTime = 0;
		synchronized(mediaTracker)
			{
			mediaTracker.addImage(image, 0);
			try 
				{
				mediaTracker.waitForID(0, 5000);

				while ((!mediaTracker.checkID(0, true))
					|| (image.getWidth(null) == 0))
					{
					waitTime += 20;
					if (waitTime > MAXIMUM_TIME_TO_WAIT_MILLISECS)
						{
						image.flush();
						MiDebug.println(
							"Timed-out waiting for MediaTracker while loading: "
							+ filename);
						return(null);
						}
					Thread.sleep(20);
					}
				//mediaTracker.waitForID(0, 50);

				int loadStatus = mediaTracker.statusID(0, false);
				} 
			catch (Exception e) 
				{
				image.flush();
				MiDebug.println("Exception generated by MediaTracker while loading: " 
					+ filename);
				return(null);
				}
			if (mediaTracker.isErrorID(0))
				{
				int loadStatus = mediaTracker.statusID(0, false);
				image.flush();
				MiDebug.println("Error generated by MediaTracker while loading: " + filename);
				if (loadStatus == MediaTracker.ABORTED)
					{
					MiDebug.println(" Loading ABORTED");
					}
				else if (loadStatus == MediaTracker.ERRORED)
					{
					MiDebug.println(" Loading ERRORED");
					}
				mediaTracker = new MediaTracker(component);
				return(null);
				}
			if ((image.getWidth(null) <= 0) || (image.getHeight(null) <= 0))
				{
				MiDebug.println("Bad size generated by MediaTracker while loading: "
					+ filename);
				}
			mediaTracker.removeImage(image, 0);
			}
		return(image);
		}
	}

