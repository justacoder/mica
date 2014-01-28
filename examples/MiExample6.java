
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
import javax.swing.JPanel;
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
 * Constructs a AWT window. Then populates the window
 * with a Swing button, Swing label, and an empty Mica container.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiExample6 extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * TestWindow. Supported command line parameters are:
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String title = Utility.getCommandLineArgument(args, "-title");

		// ---------------------------------------------------------------
		// Create the Frame and set it's layout.
		// ---------------------------------------------------------------
		Frame swingWindow = new Frame();
		swingWindow.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		swingWindow.add("Center", panel);
		panel.setLayout(new BorderLayout());

		// ---------------------------------------------------------------
		// Create the Swing button
		// ---------------------------------------------------------------
		JButton button = new JButton("Swing Button");
		panel.add("North", button);

		// ---------------------------------------------------------------
		// Create a empty container to put in the Frame.
		// ---------------------------------------------------------------
		MiNativeWindow window = new MiNativeWindow(
			MiUtility.getFrame(swingWindow), 
			title != null ? title : "Demo", 
			new MiBounds(0.0,0.0,500.0,500.0),
			MiiTypes.Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE);


		// ---------------------------------------------------------------
		// Make the preferred size be greater than 0, which is what it is if
		// it is empty.
		// ---------------------------------------------------------------
		window.setPreferredSize(new MiSize(500,500));

		// ---------------------------------------------------------------
		// Make the size of the window to be greater than or equal to the
		// size of all of the contents.
		// ---------------------------------------------------------------
        	window.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// Add out mica container to the Frame
		// ---------------------------------------------------------------
		panel.add("Center", window.getSwingComponent());

		// ---------------------------------------------------------------
		// Create a Swing label and add it to the Frame
		// ---------------------------------------------------------------
		JLabel label = new JLabel("Swing Label");
		panel.add("South", label);

		// ---------------------------------------------------------------
		// Pack and make everything visible
		// ---------------------------------------------------------------
		swingWindow.pack();
		window.setVisible(true);
		swingWindow.setVisible(true);
		}
	}

