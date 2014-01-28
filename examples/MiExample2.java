
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

package examples;

import com.swfm.mica.*; 
import com.swfm.mica.util.Utility;
import java.awt.Container;
import java.awt.Panel;
import java.awt.Button;
import java.awt.Label;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Window;
import java.applet.Applet;
import java.io.*;

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a AWT window, if this is an application, or uses the
 * existing window, if this is an applet. Then populates the window
 * with a AWT button, AWT label, and a graphics editor with menubar,
 * toolbar, status bar, etc...
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiExample2 extends Applet
	{
	private	static	boolean		initialized;


					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * TestWindow. Supported command line parameters are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		Panel		panel;
		Button		button;
		Label		label;

		new MiSystem(null);
		String title = Utility.getCommandLineArgument(args, "-title");
		Frame awtWindow = new Frame();
		awtWindow.setLayout(new BorderLayout());
		awtWindow.add("North", button = new Button("AWT Button"));
		awtWindow.add("South", label = new Label("AWT Label"));
		MiGraphicsWindow window = new MiGraphicsWindow(
			MiUtility.getFrame(button), 
			title != null ? title : "Demo", 
			new MiBounds(0.0,0.0,500.0,500.0));
		awtWindow.add("Center", window.getAWTComponent());
		awtWindow.pack();
		window.setVisible(true);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * TestWindow. Supported html file parameters are:
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		Panel		panel;
		Button		button;
		Label		label;

		if (initialized)
			{
			return;
			}
		initialized = true;


		new MiSystem(this);
		String title = getParameter("title");
		add("North", button = new Button("AWT Button"));
		add("South", label = new Label("AWT Label"));
		add("Center", panel = new java.awt.Panel());
		panel.setLayout(new BorderLayout());
		try	{
			MiGraphicsWindow window = new MiGraphicsWindow(
				MiUtility.getFrame(panel), 
				title != null ? title : "Demo", 
				new MiBounds(0.0,0.0,500.0,500.0));

			panel.add((java.awt.Component )window.getCanvas().getNativeComponent());
			window.setVisible(true);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		MiUtility.getFrame(this).pack();
		}
	}

