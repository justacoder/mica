
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
import java.awt.image.BufferedImage;
//import javax.swing.*;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.sun.image.codec.jpeg.*;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiJpgImage
	{
	public static	void		save(Image image, OutputStream outputStream)
		{
		try	{
			int w = image.getWidth(null);
			int h = image.getHeight(null);
			BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			bimg.createGraphics().drawImage(image, 0, 0, null);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimg);
			param.setQuality(1.0f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(bimg);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	public static	void		save(Image image, String filename)
		{
		try	{
			OutputStream out = MiSystem.getIOManager().getOutputResourceAsStream(filename);
			save(image, out);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	}


