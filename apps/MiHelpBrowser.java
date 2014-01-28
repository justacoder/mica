
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

import com.swfm.mica.*; 
import com.swfm.mica.util.Utility; 
import java.awt.Container;
import java.applet.Applet;

/**----------------------------------------------------------------------------------------------
 * This class implements a help browser to be used to view help
 * files supplied with Mica.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiHelpBrowser extends Applet
	{
					/**------------------------------------------------------
	 				 * The entry point for applications. This creates a new
					 * MiHelpWindow. Supported command line parameters are:
					 * -file		load this file on startup
					 * -title		the window border title
					 *------------------------------------------------------*/
	public static 	void 		main(String args[])
		{
		new MiSystem(null);

		String title = Utility.getCommandLineArgument(args, "-title");
		String filename = Utility.getCommandLineArgument(args, "-file");
		MiHelpWindow window = new MiHelpWindow(title, new MiBounds(0.0, 0.0, 500.0, 500.0));
		window.setVisible(true);
		loadSpecifiedHelpFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * The entry point for applets. This creates a new
					 * MiHelpWindow. Supported html file parameters are:
					 * file		load this file on startup
					 * title	the window border title
					 *------------------------------------------------------*/
	public		void		init()
		{
		new MiSystem(this);

		String title = getParameter("title");
		String filename = getParameter("file");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiHelpWindow window = new MiHelpWindow(
			MiUtility.getFrame(this), title, new MiBounds(0.0, 0.0, width, height));
		window.setVisible(true);
		loadSpecifiedHelpFile(window, filename);
		}
					/**------------------------------------------------------
	 				 * Loads the specified help file, if any.
					 * @param window	the window
					 * @param filename	the name of the file that contains
					 *			help text or null
					 *------------------------------------------------------*/
	protected static void		loadSpecifiedHelpFile(
						MiHelpWindow window, String filename)
		{
		if (filename != null)
			{
			try	{
				// ---------------------------------------------------------------
				// Get a lock for the window from this, the 'main' starter thread
				// ---------------------------------------------------------------
				window.getAccessLock();
				if (filename != null)
					window.openFile(filename);
				window.freeAccessLock();
				}
			catch (Exception e)
				{
				System.out.println(
					"Usage: MiHelpBrowser [-file <filename>] [-title <window title>]");
				System.exit(1);
				}
			}
		}
	}

