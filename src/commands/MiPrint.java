
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
import java.io.IOException;

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPrint extends MiCommandHandler implements MiiTypes
	{
	private		String		filename			= "/tmp/ps.out";
	private		int		dotsPerInch			= 300;
	private		String		standardPaperSize		= "Letter";
	private		String		portraitOrLandscape		= Mi_PORTRAIT_NAME;
	private		String		colorOutputScheme		= "Color";
	private		int		pagesAcross			= 1;
	private		int		pagesHigh			= 1;
	private		boolean		printWYSIWYG;
	private		MiBounds	boundsToPrint 			= new MiBounds();
	private		MiiViewFilter	viewFilter;
	//private		MiiPrintDriver	printDriver			= new MiEncapsulatedPostScriptDriver();
	private		MiiPrintDriver	printDriver			= new MiPostScriptDriver();
	//private		MiiPrintDriver	printDriver			= new MiPDFDriver();

	public				MiPrint()
		{
		}

	public		void		setFilename(String name)
		{
		filename = name;
		}

	public		void		setPrintDriver(MiiPrintDriver driver)
		{
		printDriver = driver;
		}
	public		MiiPrintDriver	getPrintDriver()
		{
		return(printDriver);
		}
	public		void		setViewFilter(MiiViewFilter filter)
		{
		viewFilter = filter;
		}
	public		MiiViewFilter	getViewFilter()
		{
		return(viewFilter);
		}

	public		void		print(
						String 		filename, 
						int 		dotsPerInch, 
						String 		standardPaperSize, 
						String 		portraitOrLandscape, 
						String 		colorOutputScheme, 
						int 		pagesAcross, 
						int 		pagesHigh, 
						MiBounds	boundsToPrint) throws IOException
		{
		this.filename 			= filename;
		this.dotsPerInch 		= dotsPerInch;
		this.standardPaperSize 		= standardPaperSize;
		this.portraitOrLandscape	= portraitOrLandscape;
		this.colorOutputScheme		= colorOutputScheme;
		this.pagesAcross 		= pagesAcross;
		this.pagesHigh 			= pagesHigh;
		this.boundsToPrint		= new MiBounds(boundsToPrint);

		try	{
			processCommand(null);
			}
		catch (Throwable e)
			{
			MiDebug.printStackTrace(e);
			throw new IOException(e.getMessage());
			}
		}
	public		void		processCommand(String arg)
		{
		if (pagesAcross < 1)
			pagesAcross = 1;
		if (pagesHigh < 1)
			pagesHigh = 1;

		if (!printDriver.configure(
				filename,
				dotsPerInch,
				standardPaperSize,
				portraitOrLandscape,
				colorOutputScheme,
				1)) // pagesAcross * pagesHigh))
			{
			// User cancelled... throw new RuntimeException("Unable to configure printer");
			return;
			}

		MiEditor editor = (MiEditor )getTargetOfCommand();
		if (boundsToPrint.isReversed())
			{
			boundsToPrint = editor.getUniverseBounds();
			}

		if (portraitOrLandscape.equals(Mi_LANDSCAPE_NAME))
			{
			int tmp = pagesAcross;
			pagesAcross = pagesHigh;
			pagesHigh = tmp;
			}

		MiRenderer renderer = new MiRenderer();
		renderer.setHasAWTClipRectBug(false);
		renderer.setIsPrinterRenderer(true);
		if (viewFilter != null)
			renderer.pushFilter(viewFilter);

		MiBounds printerDeviceBounds = printDriver.getDeviceBounds();
		MiMargins margins = printDriver.getMargins();

		MiBounds printableDeviceBounds = new MiBounds(
			printerDeviceBounds.xmin + margins.left,
			printerDeviceBounds.ymin + margins.bottom,
			printerDeviceBounds.xmax - margins.right,
			printerDeviceBounds.ymax - margins.top);

		MiViewport viewport = new MiViewport(boundsToPrint, printableDeviceBounds);

		renderer.setYmax((int )printerDeviceBounds.ymax);
		renderer.clearTransforms();

		renderer.setWindowSystemRenderer(printDriver.getGraphics());
		renderer.setSingleBufferedScreen();

		MiBounds rootBoundsToPrint = new MiBounds();
		MiEditor rootWindow = editor.getRootWindow();
		MiBounds contentBounds = new MiBounds();

		MiDistance width = boundsToPrint.getWidth()/pagesAcross;
		MiDistance height = boundsToPrint.getHeight()/pagesHigh;
		MiCoord x = boundsToPrint.getXmin();
		MiCoord y = boundsToPrint.getYmin();
		int pageNum = 0;
		MiBounds pageBounds = new MiBounds();

		MiDrawManager editorDrawManager = null;
		Color editorBackgroundColor = null;
		MiBounds editorWorldBounds = null;
		boolean bitbltScrollingEnabled = false;

		if (!printWYSIWYG)
			{
			editorWorldBounds = editor.getWorldBounds();
			bitbltScrollingEnabled = editor.isBitbltScrollingEnabled();
			editor.setBitbltScrollingEnabled(false);
			editor.setWorldBounds(editor.getUniverseBounds());
			editorDrawManager = editor.getDrawManager();
			if (!(editor instanceof MiWindow))
				editor.setDrawManager(null);
			editorBackgroundColor = editor.getBackgroundColor();
			editor.setBackgroundColor(Mi_TRANSPARENT_COLOR);
			}


		for (int i = 0; i < pagesHigh; ++i)
			{
			pageBounds.setBounds(x, y, x + width, y + height);
	
			for (int j = 0; j < pagesAcross; ++j)
				{
				MiBounds b = new MiBounds(pageBounds);
				viewport.enforceWorldAspectRatio(b);
				viewport.setWorldBounds(b);

				renderer.pushTransform(viewport.getTransform());

				viewport.getTransform().wtod(pageBounds, contentBounds);
				printDriver.setBoundsOfGraphicalContent(contentBounds);

				if (pageNum == 0)
					printDriver.init();

				if (printWYSIWYG)
					{
					editor.transformToOtherEditorSpace(
						rootWindow, pageBounds, rootBoundsToPrint);
					rootWindow.getViewport().getTransform().wtod(
						rootBoundsToPrint, rootBoundsToPrint);
					rootWindow.getDrawManager().invalidateBackToFront(rootBoundsToPrint);

					rootWindow.draw(renderer);
					}
				else
					{
					editor.getViewport().getTransform().wtod(pageBounds, contentBounds);
					viewport.enforceWorldAspectRatio(contentBounds);
					viewport.setWorldBounds(contentBounds);
					editor.invalidateArea(contentBounds);
					if (editor.getDrawManager() != null)
						{
						editor.getDrawManager().invalidateBackToFront(contentBounds);
						}
					renderer.setClipBounds(contentBounds);
					editor.draw(renderer);
					}

				renderer.clearTransforms();
				++pageNum;
				if (pageNum < pagesHigh * pagesAcross)
					{
					printDriver.newPage();
					renderer.setWindowSystemRenderer(printDriver.getGraphics());
					}
				pageBounds.translate(width, 0);
				}
			y += height;
			x = boundsToPrint.getXmin();
			}

		printDriver.termin();
		if (!printWYSIWYG)
			{
			editor.setDrawManager(editorDrawManager);
			editor.setWorldBounds(editorWorldBounds);
			editor.setBitbltScrollingEnabled(bitbltScrollingEnabled);
			editor.setBackgroundColor(editorBackgroundColor);
			}
		}
	}

