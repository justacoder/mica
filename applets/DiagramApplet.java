

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
 *   Copyright (c) 1997 Software Farm, Inc.  All Rights Reserved.          *
 ***************************************************************************
 */


import com.swfm.mica.*; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.Strings; 
import com.swfm.util.Monitor; 
import com.swfm.util.AppletDebuggingWindow; 
import java.awt.Frame; 
import java.awt.Panel; 
import java.awt.BorderLayout; 
import java.awt.Button; 
import java.awt.event.*; 
import java.applet.Applet;



/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Applet Examples Suite
 * <p>
 * <p>
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.2.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.2)
 *----------------------------------------------------------------------------------------------*/
public class DiagramApplet extends Applet implements MiiTypes
	{
	private	static	boolean		initialized;

					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * DiagramWindow. Supported html file parameters are:
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		start()
		{
		MiDebug.println("DiagramApplet started");
		}
	public		void		stop()
		{
		MiDebug.println("DiagramApplet stopped");
		}
	public		void		destroy()
		{
		MiDebug.println("DiagramApplet destroyed");
		}
	public		void		init()
		{
		Panel		panel;
		if (initialized)
			{
			return;
			}
		initialized = true;

		MiDebug.setLoggingPrintStream(AppletDebuggingWindow.out);

MiDebug.println("Enter applet init");

		try	{
			MiSystem.init(this);

MiDebug.println("After Mica init");

			AppletDebuggingWindow.window.show();

			Monitor.setGlobalMonitoringLogFile(AppletDebuggingWindow.out);

			String title = getParameter("title");
			String filename = getParameter("filename");
			//add("Center", panel = new java.awt.Panel());
			//panel.setLayout(new BorderLayout());
MiDebug.println("abpout to create editor");

			MiJDKAPIComponentType micaContainerWidgetType = Mi_AWT_HEAVYWEIGHT_COMPONENT_TYPE;
			if (Boolean.valueOf(getParameter("useSwing")).booleanValue())
				{
				micaContainerWidgetType = Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE;
				}

			MiGraphicsWindow window = new MiGraphicsWindow(
				MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, 500, 500),
				micaContainerWidgetType);

			window.setCapabilities(
					true,		// hasGraphsMenu
					true,		// hasLayoutsMenu
					true,		// hasDrawingToolbar
					true,		// hasShapeAttributesDialog
					true,		// hasRulers
					true,		// hasDrawingPages
					true,		// hasDrawingPagesDialogBox
					true,		// hasDrawingGrid
					true,		// hasLayers
					true,		// hasBirdsEyeView
					true,		// hasPalette
					true,		// hasConnectionToolBar
					true,		// hasVisibilityToolBar
					true,		// canHaveVisibleConnectionPoints
					true,		// canHaveVisibleAnnotationPoints
					true,		// hasConnectionPointsDialog
					true 		// hasAnnotationPointsDialog
					);
			if (window.getPalette() instanceof MiGraphicsPalette)
				{
				((MiGraphicsPalette )window.getPalette()).setGraphicsPalettesDirectory(
					"${Mi_HOME}/apps/palettes");
				}
			window.openDefaultFile();
//			loadSpecifiedGraphicsAndPalette(window, filename, palette);

			setLayout(new BorderLayout());
			add(BorderLayout.CENTER, (java.awt.Component )window.getCanvas().getNativeComponent());
			window.setVisible(true);
			MiUtility.getFrame(this).pack();
			}
		catch (Throwable t)
			{
			MiDebug.println("ERROR " + t);
			if (t instanceof Exception)
				{
				MiDebug.printStackTrace((Exception )t);
				}
			}
		}
	}

