
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

/**----------------------------------------------------------------------------------------------
 * A class of the MICA Graphics Examples Suite
 * <p>
 * Constructs a simple window with "Hello World" as the title and
 * with a push button in the middle that * displays "Hello World" 
 * and which when pressed causes the window to close and the 
 * application to exit.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiHelloWorld implements MiiActionTypes, MiiCommandNames
	{
					/**------------------------------------------------------
	 				 * The entry point for all applications.
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		new MiHelloWorld();
		}
					/**------------------------------------------------------
	 				 * Constructs a new instance MiHelloWorld. MiHelloWorld
					 * is a window with a push button in the middle that
					 * displays "Hello World" and when pressed causes the 
					 * window to close and the application to exit.
					 *------------------------------------------------------*/
	public				MiHelloWorld()
		{
		// ---------------------------------------------------------------
		// Create a window with "Hello World" in the border title.
		// ---------------------------------------------------------------
		MiNativeWindow window = new MiNativeWindow("Hello World");

		// ---------------------------------------------------------------
		// Allow the window to be resized larger than it actually required.
		// ---------------------------------------------------------------
		window.setViewportSizeLayout(new MiEditorViewportSizeIsOneToOneLayout(true));

		// ---------------------------------------------------------------
		// When the window is closed/disposed, exit the whole application
		// ---------------------------------------------------------------
		window.setDefaultCloseCommand(Mi_QUIT_COMMAND_NAME);

		// ---------------------------------------------------------------
		// Create a push button widget that displays "Hello World".
		// ---------------------------------------------------------------
		MiWidget helloWorld = new MiPushButton("Hello World");

		// ---------------------------------------------------------------
		// Tell the push button to send the 'QUIT' command to the 
		// window whenever the push button generates the Mi_ACTIVATED_ACTION, 
		// which it does whenever it is pressed by the user.
		// ---------------------------------------------------------------
		helloWorld.appendCommandHandler(window, Mi_QUIT_COMMAND_NAME, Mi_ACTIVATED_ACTION);

		// ---------------------------------------------------------------
		// Put the push button in the center of the window.
		// ---------------------------------------------------------------
		helloWorld.setCenter(window.getWorldBounds().getCenter());

		// ---------------------------------------------------------------
		// Add the push button to the window.
		// ---------------------------------------------------------------
		window.appendPart(helloWorld);

		// ---------------------------------------------------------------
		// Make the window visible.
		// ---------------------------------------------------------------
		window.setVisible(true);
		}
	}

