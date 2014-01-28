
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
import java.applet.Applet;
import com.swfm.mica.util.Utility;

/**----------------------------------------------------------------------------------------------
 * This class creates a window that allows a user to browse, find, modify and 
 * delete files.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.0(Beta)
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiFileBrowser extends Applet
	{
	public static final String	Mi_DEFAULT_WINDOW_BORDER_TITLE	= "Mi_DEFAULT_WINDOW_BORDER_TITLE";

	public static void main(String args[])
		{
		new MiSystem(null);

		MiSystem.setApplicationDefaultProperty(Mi_DEFAULT_WINDOW_BORDER_TITLE, "The File Browser");
		String currentDirectory = Utility.getCommandLineArgument(args, "-directory");
		String title = Utility.getCommandLineArgument(args, "-title");
		MiFileChooser fileBrowser = new MiFileChooser(
			null,
			currentDirectory == null 
				? MiSystem.getProperty(MiSystem.Mi_CURRENT_DIRECTORY) : currentDirectory,
			title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title,
			false,
			true);
		try	{
			fileBrowser.setVisible(true);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			System.out.println(
				"Usage: MiFileBrowser [-directory <starting directory>] [-title <window title>]");
			System.exit(1);
			}
		}
	public		void		init()
		{
		new MiSystem(this);

		String currentDirectory = getParameter("directory");
		String title = getParameter("title");
		int width = Utility.toInteger(getParameter("width"), 500);
		int height = Utility.toInteger(getParameter("height"), 500);
		MiFileChooser fileBrowser = new MiFileChooser(
			null,
			currentDirectory,
			title == null ? Mi_DEFAULT_WINDOW_BORDER_TITLE : title,
			false,
			true);
		fileBrowser.setVisible(true);
		}
	}


