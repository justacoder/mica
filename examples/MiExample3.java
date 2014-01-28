
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
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.BoxLayout;
/*
import com.sun.java.swing.JComponent;
import com.sun.java.swing.JPanel;
import com.sun.java.swing.JButton;
import com.sun.java.swing.JLabel;
import com.sun.java.swing.JFrame;
import com.sun.java.swing.JWindow;
import com.sun.java.swing.BoxLayout;
*/

/*
import java.applet.Applet;
import java.awt.swing.JComponent;
import java.awt.swing.JPanel;
import java.awt.swing.JButton;
import java.awt.swing.JLabel;
import java.awt.swing.JFrame;
import java.awt.swing.JWindow;
import java.awt.swing.BoxLayout;
*/

import java.applet.Applet;
import java.io.*;

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a AWT window, if this is an application, or uses the
 * existing window, if this is an applet. Then populates the window
 * with a AWT button, AWT label, and a graphics editor with menubar,
 * toolbar, status bar, etc... (uses a Swing button and label if this
 * is an application).
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiExample3 extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * TestWindow. Supported command line parameters are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		MiSystem.init();
		String title = Utility.getCommandLineArgument(args, "-title");

		Frame awtFrame = new Frame();
		awtFrame.setLayout(new BorderLayout());

		JButton button = new JButton("Swing Button");
		awtFrame.add(button, BorderLayout.NORTH);

		MiGraphicsWindow window = new MiGraphicsWindow(
			awtFrame,
			title != null ? title : "Demo", 
			new MiBounds(0.0,0.0,500.0,500.0),
			MiiTypes.Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE);

		awtFrame.add(window.getSwingComponent(), BorderLayout.CENTER);

		JLabel label = new JLabel("Swing Label");
		awtFrame.add(label, BorderLayout.SOUTH);

		awtFrame.pack();
		awtFrame.setVisible(true);
		window.getAccessLock();
		window.setVisible(true);
		window.freeAccessLock();
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * TestWindow. Supported html file parameters are:
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);
		String title = getParameter("title");
		add("North", new Button("AWT Button"));
		add("South", new Label("AWT Label"));
		Panel panel = new Panel();
		add("Center", panel);
		panel.setLayout(new BorderLayout());
		MiGraphicsWindow window = new MiGraphicsWindow(
			MiUtility.getFrame(panel),
			title != null ? title : "Demo", 
			new MiBounds(0.0,0.0,500.0,500.0),
			MiiTypes.Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE);
		window.setVisible(true);
		MiUtility.getFrame(this).pack();
		}
	}

