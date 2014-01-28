

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

import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Button;
import java.awt.Event;
import java.awt.Label;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.TextArea;


/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiExceptionOccurredDialog extends Dialog
	{
	public static	String		Mi_EXCEPTION_OCCURED_MSG	= "Mi_EXCEPTION_OCCURED_MSG";
	public static	String		Mi_SITUATION_MSG		= "Mi_SITUATION_MSG";
	private		ByteArrayOutputStream	exceptionMessage;
	private		Label		message;
	private		TextArea	details;
	private		boolean		saveExceptionStackTraceToFile	= true;
	private static	String		fileNamePrefix			= "Error_trace_file";
	private static	String		applicationVersionInformation	= "<No version information for application with the error>";



	public				MiExceptionOccurredDialog(Frame frame, String borderTitle, Throwable e, boolean tooSevereToContinue)
		{
		super(frame, borderTitle, true);
/*
		if (MiDebug.debug)
			e.printStackTrace();
*/

		exceptionMessage = new ByteArrayOutputStream();
		// DOES NOT WORK JDK1.2Beta2 PrintWriter exceptionPrintWriter = new PrintWriter(exceptionMessage);
		// e.printStackTrace(exceptionPrintWriter);

		PrintStream exceptionPrintStream = new PrintStream(exceptionMessage);
		e.printStackTrace(exceptionPrintStream);


		String detailsFilename = null;
		if (saveExceptionStackTraceToFile)
			{
			detailsFilename = saveExceptionStackTraceToFile(e);
			}
		setLayout(new BorderLayout());

		setBackground(Color.red);
 
		message = new Label(MiSystem.getProperty(Mi_EXCEPTION_OCCURED_MSG, "A Serious Error Occurred!"));
		message.setFont(new Font("TimesRoman", Font.BOLD, 28));
		message.setAlignment(Label.CENTER);
		Panel northPanel = new Panel();
		add("North", northPanel);
		int numRows = 1;
		GridLayout gridLayout = new GridLayout(numRows, 1);
		gridLayout.setVgap(0);
		northPanel.setLayout(gridLayout);
		northPanel.add(message);

		if (detailsFilename != null)
			{
			String msg = MiSystem.getProperty(Mi_SITUATION_MSG, "Details have been saved to the file:\n%1");
			msg = Utility.sprintf(msg, detailsFilename);
			int index = -1;
			while ((index = msg.indexOf('\n')) != -1)
				{
				String line = msg.substring(0, index);
				Label label = new Label(line);
				label.setFont(new Font("TimesRoman", Font.PLAIN, 22));
				label.setBackground(Color.red);
				label.setAlignment(Label.CENTER);
				northPanel.add(label);
				msg = msg.substring(index + 1);
				gridLayout.setRows(++numRows);
				}
			if (msg.length() > 0)
				{
				Label label = new Label(msg);
				label.setBackground(Color.red);
				label.setFont(new Font("TimesRoman", Font.PLAIN, 22));
				label.setAlignment(Label.CENTER);
				northPanel.add(label);
				gridLayout.setRows(++numRows);
				}
			}

		details = new TextArea();
		details.setVisible(false);
		details.setFont(new Font("TimesRoman", Font.PLAIN, 16));
		add("Center", details);

		//add("Left", MiToolkit.errorDialogIcon.getImage());
		
		Panel p = new Panel();
		add("South", p);
		Button b =new Button("Exit");
		Font buttonFont = new Font("TimesRoman", Font.BOLD, 16);
		b.setFont(buttonFont);
		p.add(b);
		if (!tooSevereToContinue)
			{
			b = new Button("Continue");
			b.setFont(buttonFont);
			p.add(b);
			}
		b = new Button("Details");
		b.setFont(buttonFont);
		p.add(b);
		}

	public static	String		saveExceptionStackTraceToFile(Throwable e)
		{
		ByteArrayOutputStream exceptionMessage = new ByteArrayOutputStream();
		PrintStream exceptionPrintStream = new PrintStream(exceptionMessage);
		e.printStackTrace(exceptionPrintStream);

		String filename = fileNamePrefix + " " + new java.util.Date().toString();
		filename = Utility.replaceAll(filename, " ", "_");
		filename = Utility.replaceAll(filename, ":", "_");
		filename += ".txt";
		FileOutputStream file = null;
		try	{
			file = new FileOutputStream(filename);
			PrintStream printer = new PrintStream(file);
		// DOES NOT WORK: PrintWriter printer = Utility.openOutputFile(filename);
			printer.println(applicationVersionInformation);
			printer.println(exceptionMessage.toString());
			// Redundant prints to STDOUT
			//MiDebug.println(applicationVersionInformation);
			//MiDebug.println(exceptionMessage.toString());
			file.close();
			}
		catch (Throwable ioException)
			{
			MiDebug.println("Unable to write exception to file: " + filename);
			MiDebug.println(applicationVersionInformation);
			MiDebug.println(exceptionMessage.toString());
			filename = null;
			}
		return(filename);
		}

	public static	void		setExceptionFilenamePrefix(String name)
		{
		fileNamePrefix = name;
		}
	public static	String		getExceptionFilenamePrefix()
		{
		return(fileNamePrefix);
		}
	public static	void		setApplicationVersionInformation(String info)
		{
		applicationVersionInformation = info;
		}
	public static	String		getApplicationVersionInformation()
		{
		return(applicationVersionInformation);
		}
	public 		boolean 		action(Event evt, Object arg)
		{
		if ("Exit".equals(arg))
			{
			System.exit(1);
			}
		if ("Continue".equals(arg))
			{
		        dispose();
			MiiTransaction openTransaction = MiSystem.getTransactionManager().getOpenTransaction();
			if (openTransaction != null)
				{
				try	{
					MiSystem.getTransactionManager().rollbackTransaction();
					MiDebug.println("Rolledback open transaction");
					}
				catch (Throwable t)
					{
					MiDebug.println("Unable to rollback open transaction");
					// Already closed: MiSystem.getTransactionManager().commitTransaction(openTransaction);
					}
				}
			return(true);
			}
		if ("Details".equals(arg))
			{
			details.setText(applicationVersionInformation + "\n" + exceptionMessage.toString());
			MiDebug.println(exceptionMessage.toString());
			details.setVisible(true);
			pack();
			return(true);
			}
		return(false);
		}
	}

