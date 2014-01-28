
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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPartInspector extends MiCommandHandler
	{
	private		boolean		printNaturalBounds	= true;
	private		boolean		printAttachments	= true;
	private		boolean		printEventHandlers	= true;
	private		boolean		printTransforms		= true;
	private		boolean		printState		= true;
	private		MiTable		table;




	public				MiPartInspector()
						{
						}

	public		void		processCommand(String arg)
		{
		MiPart obj = (MiPart )getTargetOfCommand();
		MiNativeWindow dialogBox = new MiNativeWindow("Inspector", new MiBounds(0,0,300,600));
		MiColumnLayout column = new MiColumnLayout();
		dialogBox.setLayout(column);
		MiLabel name = new MiLabel(obj.toString());
		dialogBox.appendPart(name);

		table = MiBasicPropertyPanel.makePropertyPanelTable(dialogBox, true);

		getPartAttributes(obj);
		dialogBox.setVisible(true);
		}

	private		void		getPartAttributes(MiPart object)
		{
		if (object instanceof MiEditor)
			{
			MiEditor editor = (MiEditor )object;
			table.appendItem(new MiText("World")); 
				table.appendItem(new MiText(editor.getWorldBounds().toString()));
			table.appendItem(new MiText("Device")); 
				table.appendItem(new MiText(editor.getDeviceBounds().toString()));
			table.appendItem(new MiText("Universe")); 
				table.appendItem(new MiText(editor.getUniverseBounds().toString()));
			}
		table.appendItem(new MiText("Bounds")); 
			table.appendItem(new MiText(object.getBounds().toString()));
		if (printState)
			printState(object);
		if (printTransforms)
			printTransforms(object);
		if (printEventHandlers)
			printEventHandlers(object);
		if ((printAttachments) && (object.getNumberOfAttachments() > 0))
			printAttachments(object);
		if (object.getLayout() != null)
			{
			table.appendItem(new MiText("Layout")); 
					table.appendItem(new MiText(object.getLayout().toString()));
			}
		table.appendItem(new MiText("hasValidLayout")); 
			table.appendItem(new MiText(String.valueOf(object.hasValidLayout())));
		}
	private		void		printState(MiPart object)
		{
		table.appendItem(new MiText("Hidden")); 
			table.appendItem(new MiText(String.valueOf(!object.isVisible())));
		table.appendItem(new MiText("Selected")); 
			table.appendItem(new MiText(String.valueOf(object.isSelected())));
		table.appendItem(new MiText("Sensitive")); 
			table.appendItem(new MiText(String.valueOf(object.isSensitive())));
		table.appendItem(new MiText("Keyboard Focus")); 
			table.appendItem(new MiText(String.valueOf(object.hasKeyboardFocus())));
		table.appendItem(new MiText("Mouse Focus")); 
			table.appendItem(new MiText(String.valueOf(object.hasMouseFocus())));
		}
	private		void		printTransforms(MiPart object)
		{
/*
		MiiTransform transform = object.getTransform();
		if (transform != null)
			{
			System.out.print(indent + " + transform: ");
			if (!transform.getScale().isIdentity())
				{
				System.out.print(transform.getScale() + " ");
				}
			if (!transform.getTranslation().isZero())
				{
				System.out.print(transform.getTranslation() + " ");
				}
			if (transform.getRotation() != 0)
				{
				System.out.print(transform.getRotation() + " ");
				}
			System.out.print("\n");
			}
*/
		}
	private		void		printEventHandlers(MiPart object)
		{
/*
		for (int i = 0; i < object.getNumberOfEventHandlers(); ++i)
			{
			MiEventHandler h = object.getEventHandler(i);
			System.out.println(indent + " + sensor: " + h);
			}
		for (int i = 0; i < object.getNumberOfMonitors(); ++i)
			{
			MiEventHandler h = object.getMonitor(i);
			System.out.println(indent + " + monitor: " + h);
			}
		for (int i = 0; i < object.getNumberOfGrabs(); ++i)
			{
			MiEventHandler h = object.getGrab(i);
			System.out.println(indent + " + grab: " + h);
			}
*/
		}
	private		void		printAttachments(MiPart object)
		{
/*
		for (int i = 0; i < object.getNumberOfAttachments(); ++i)
			{
			MiPart obj = object.getAttachment(i);
			System.out.println(indent + " + attached: " + obj);
			if (obj.getNumberOfParts() > 0)
				{
				depth += 2;
				calcIndent();
				dumpObjectContents(obj);
				depth -= 2;
				calcIndent();
				}
			}
*/
		}
			
	}


