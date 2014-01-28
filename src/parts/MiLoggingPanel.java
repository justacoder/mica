
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
import com.swfm.mica.util.Strings; 
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */

public class MiLoggingPanel extends MiWidget implements MiiActionHandler
	{
	private		int			maxNumberOfLines = 40;
	private		MiList			list;
	private		MiPushButton		clearListButton;
	private		Strings			lines = new Strings();


	public				MiLoggingPanel(String filename, int numberOfLinesToDisplay)
		{
		this(filename, numberOfLinesToDisplay, true);
		}
	public				MiLoggingPanel(String filename, int numberOfLinesToDisplay, boolean sendAllDebugPrintlnMessagesToLog)
		{
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setElementVSizing(Mi_NONE);
		layout.setUniqueElementIndex(0);
		setLayout(layout);

		list = new MiList();
		list.getSortManager().setEnabled(false);
		list.getSelectionManager().setMaximumNumberSelected(0);
		list.setNumberOfVisibleCharactersWide(60);
		MiScrolledBox scrolledBox = new MiScrolledBox(list);
		appendPart(scrolledBox);

		clearListButton = new MiPushButton("Clear List");
		clearListButton.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		appendPart(clearListButton);

		if (sendAllDebugPrintlnMessagesToLog)
			{
			try
				{
				OutputStream fout = MiSystem.getIOManager().getOutputResourceAsStream(filename);
				MiDebug.addLoggingPrintStream(new MiListenablePrintStream(this, fout, true));
				}
			catch (Exception e)
				{
				System.out.println("Unable to open logging file: " + filename);
				}
			}
		}
	public		void		updateLog(String line)
		{
		lines.addElement(line);

		while (lines.size() > maxNumberOfLines)
			lines.removeElementAt(0);

		if (isShowing(null))
			{
			update(line);
			}
		}
	protected	void		nowShowing(boolean flag)
		{
		if (flag)
			{
			update();
			}
		super.nowShowing(flag);
		}
	protected	void		update(String line)
		{
		Strings lines = new Strings(line);
		for (int i = 0; i < lines.size(); ++i)
			{
			list.appendItem(lines.get(i));
			}

		while (list.getNumberOfItems() > maxNumberOfLines)
			{
			list.removeItem(0);
			}
		}
	protected	void		update()
		{
		list.removeAllItems();
		list.setContents(lines);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == clearListButton)
			{
			lines.removeAllElements();
			list.removeAllItems();
			}
		return(true);
		}
	}
class MiListenablePrintStream extends PrintStream
	{
	private		MiLoggingPanel	listener;

	public				MiListenablePrintStream(
						MiLoggingPanel listener, OutputStream out)
		{
		super(out);
		this.listener = listener;
		}
	public				MiListenablePrintStream(
						MiLoggingPanel listener, OutputStream out, boolean autoflush)
		{
		super(out, autoflush);
		this.listener = listener;
		}
	public 		void		print(String s)
		{
		super.print(s);
		listener.updateLog(s);
		}
	}

