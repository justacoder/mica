
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
import com.swfm.mica.util.Utility; 
import java.util.Hashtable;

/**
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */

public class MiExportPanel extends MiWidget implements MiiActionHandler
	{
	private		Hashtable		printDrivers		= new Hashtable(11);
	private		MiEditor		editor;
	private		MiPushButton		doExport;
	private		MiTextField		filenameField;
	private		MiToggleButton		selectedItemsOnly;
	private		MiParts			formatRadioButtons 	= new MiParts();
	private		Strings			formats;
	private		Strings			drivers;
	private		MiiPrintDriver 		printDriver;
	private		MiBox		 	parmBox;


	public				MiExportPanel(MiEditor editor, Strings formats, Strings drivers)
		{
		this.editor = editor;
		this.formats = new Strings(formats);
		this.drivers = new Strings(drivers);

		MiRowLayout mainLayout = new MiRowLayout();
		//mainLayout.setElementSizing(Mi_EXPAND_TO_FILL);
		mainLayout.setElementHSizing(Mi_NONE);
		mainLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		mainLayout.setUniqueElementIndex(1);
		setLayout(mainLayout);


		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		layout.setElementVSizing(Mi_NONE);
		layout.setUniqueElementIndex(1);
		appendPart(layout);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(1);
		rowLayout.setElementVSizing(Mi_NONE);
		//rowLayout.setElementVJustification(Mi_CENTER_JUSTIFIED);
		layout.appendPart(rowLayout);
		filenameField = new MiTextField();
		filenameField.appendActionHandler(this, Mi_TEXT_CHANGE_ACTION);
		rowLayout.appendPart(new MiText("Filename"));
		rowLayout.appendPart(filenameField);

		MiBox box = new MiBox("Formats");
		MiColumnLayout c_ayout = new MiColumnLayout();
		c_ayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(c_ayout);

		MiRadioBox radioBox = new MiRadioBox();
		radioBox.getRadioStateEnforcer().setMinNumSelected(1);
		box.appendPart(radioBox);

		for (int i = 0; i < formats.size(); ++i)
			{
			MiToggleButton tb = new MiToggleButton();
			tb.appendActionHandler(this, Mi_SELECTED_ACTION);
			tb.setShape(MiVisibleContainer.DIAMOND_SHAPE);
			radioBox.appendPart(new MiLabeledWidget(tb, formats.elementAt(i)));
			formatRadioButtons.addElement(tb);
			}
		layout.appendPart(box);

		selectedItemsOnly = new MiToggleButton();
		layout.appendPart(new MiLabeledWidget(selectedItemsOnly, "Export Selected Items Only"));

		doExport = new MiPushButton("Export");
		doExport.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		doExport.setSensitive(false);
		layout.appendPart(doExport);


		parmBox = new MiBox("Configuration");
		c_ayout = new MiColumnLayout();
		c_ayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		parmBox.setLayout(c_ayout);
		appendPart(parmBox);
		parmBox.setVisible(false);

		if (formatRadioButtons.size() > 0)
			{
			((MiWidget )formatRadioButtons.elementAt(0)).select(true);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == filenameField)
			{
			doExport.setSensitive(!Utility.isEmptyOrNull(filenameField.getValue()));
			}
		else if (action.getActionSource() == doExport)
			{
			print(filenameField.getValue(), printDriver, selectedItemsOnly.isSelected());
			}
		else // if (action.getActionSource() == toggleButton)
			{
			for (int i = 0; i < formats.size(); ++i)
				{
				if (formatRadioButtons.elementAt(i).isSelected())
					{
					String driverName = drivers.elementAt(i);
					printDriver = (MiiPrintDriver )printDrivers.get(driverName);
					if (printDriver == null)
						{
						printDriver = (MiiPrintDriver )Utility.makeInstanceOfClass(driverName);
						printDrivers.put(driverName, printDriver);
						}
					if (printDriver == null)
						{
						parmBox.setVisible(false);
						return(true);
						}

					MiPropertyDescriptions descs = printDriver.getPropertyDescriptions();
					if ((descs == null) || (descs.size() == 0))
						{
						parmBox.setVisible(false);
						return(true);
						}

					MiiModelEntity model = new MiPropertiedModel(printDriver);
					MiModelPropertyViewManager viewManager = new MiModelPropertyViewManager(model);
					viewManager.setApplyingEveryChange(true);

					setOutgoingInvalidLayoutNotificationsEnabled(false);

					parmBox.removeAllParts();
					viewManager.setView(parmBox);
					parmBox.setVisible(true);
					viewManager.setModel(model);

					if (getSize(new MiSize()).isSmallerSizeThan(getPreferredSize(new MiSize())))
						packLayout();
					else
						validateLayout();
					setOutgoingInvalidLayoutNotificationsEnabled(true);
					}
				}
			}
		return(true);
		}
	protected	void		print(String filename, MiiPrintDriver printDriver, boolean selectedItemsOnly)
		{
		if (printDriver == null)
			return;

		MiPrint printCommand = new MiPrint();
		printCommand.setPrintDriver(printDriver);
		printCommand.setTargetOfCommand(editor);

		MiDrawingPages drawingPages = null;
		MiPageManager pageManager = editor.getPageManager();
		if (pageManager != null)
			{
			drawingPages = pageManager.getDrawingPages();
			}

		if (drawingPages == null)
			{
			printCommand.setFilename(filename);
			printCommand.processCommand(null);
			return;
			}
		if (selectedItemsOnly)
			{
			printCommand.setViewFilter(new MiSelectedOnlyViewFilter());
			}
		try	{
			printCommand.print(
				filename, 
				300,		// 		dotsPerInch, 
				pageManager.getPageSize().getName(),
				drawingPages.getOrientation() == Mi_VERTICAL ? "Portrait" : "Landscape",
				"COLOR",
				drawingPages.getPagesWide(), 
				drawingPages.getPagesTall(), 
				drawingPages.getBounds());	// boundsToPrint
			}
		catch (Exception e)
			{
			MiDebug.printStackTrace(e);
			}
		}
	}
