
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

import com.swfm.mica.*; 
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 
import java.util.Hashtable;
import java.io.IOException;

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiPrintPanel extends MiWidget implements MiiActionHandler
	{
	public static final int			Mi_PRINT_SELECTED_ITEMS		= 1;
	public static final int			Mi_PRINT_CURRENT_VIEW		= 2;
	public static final int			Mi_PRINT_DRAWING_PAGES		= 3;
	public static final int			Mi_PRINT_EVERYTHING		= 4;
	public static final int			Mi_PRINT_WINDOW			= 5;

	public static 	int			Mi_NUMBER_OF_SECONDS_DELAY_BEFORE_PRINTING	= 30;

	private		Hashtable		printDrivers		= new Hashtable(11);
	private		MiEditor		editor;
	private		MiPushButton		doPrintToFile;
	private		MiPushButton		doPrint;
	private		MiTextField		filenameField;
	private		MiToggleButton		printSelectedItems;
	private		MiToggleButton		printCurrentView;
	private		MiToggleButton		printPages;
	private		MiToggleButton		printEverything;
	private		MiToggleButton		printWindow;
	private		MiToggleButton		delayBeforePrinting;
	private		MiParts			formatRadioButtons 	= new MiParts();
	private		Strings			formats;
	private		Strings			drivers;
	private		MiiPrintDriver 		printDriver;
	private		MiBox		 	parmBox;
	private		String			defaultDirectory;


	public				MiPrintPanel(MiEditor editor, Strings formats, Strings drivers)
		{
		this(editor, formats, drivers, false);
		}
	public				MiPrintPanel(MiEditor editor, Strings formats, Strings drivers, boolean widePrintPanelLayout)
		{
		this.editor = editor;
		this.formats = new Strings(formats);
		this.drivers = new Strings(drivers);

		MiRowLayout mainLayout = new MiRowLayout();
		//mainLayout.setElementSizing(Mi_EXPAND_TO_FILL);
		mainLayout.setElementHSizing(Mi_NONE);
		mainLayout.setAlleyHSpacing(10);
		mainLayout.setInsetMargins(10);
		mainLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		mainLayout.setUniqueElementIndex(1);
		setLayout(mainLayout);


		MiBox printToPrinterBox = new MiBox("Print To Printer");
		MiColumnLayout ptp_layout = new MiColumnLayout();
		ptp_layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		ptp_layout.setElementVSizing(Mi_NONE);
		ptp_layout.setUniqueElementIndex(1);
		printToPrinterBox.setLayout(ptp_layout);
		appendPart(printToPrinterBox);

		doPrint = new MiPushButton("Print To Printer");
		doPrint.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		printToPrinterBox.appendPart(doPrint);

		MiColumnLayout middle_layout = new MiColumnLayout();
		middle_layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		middle_layout.setElementVSizing(Mi_NONE);
		middle_layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		middle_layout.setUniqueElementIndex(1);
		appendPart(middle_layout);

		MiBox printContentBox = new MiBox("Printable content");
		MiColumnLayout pcb_layout = new MiColumnLayout();
		pcb_layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		pcb_layout.setElementVSizing(Mi_NONE);
		pcb_layout.setUniqueElementIndex(1);
		printContentBox.setLayout(pcb_layout);

		middle_layout.appendPart(printContentBox);

		MiRadioBox radioBox = new MiRadioBox();
		radioBox.getRadioStateEnforcer().setMinNumSelected(1);
		printContentBox.appendPart(radioBox);
		if (widePrintPanelLayout)
			{
			MiGridLayout rb_layout = new MiGridLayout();
			rb_layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
			rb_layout.setNumberOfColumns(2);
			radioBox.setLayout(rb_layout);
			}

		printSelectedItems = new MiToggleButton();
		printSelectedItems.appendActionHandler(this, Mi_SELECTED_ACTION);
		printSelectedItems.setShape(MiVisibleContainer.DIAMOND_SHAPE);
		radioBox.appendPart(new MiLabeledWidget(printSelectedItems, "Selected items"));


		printCurrentView = new MiToggleButton();
		printCurrentView.appendActionHandler(this, Mi_SELECTED_ACTION);
		printCurrentView.setShape(MiVisibleContainer.DIAMOND_SHAPE);
		radioBox.appendPart(new MiLabeledWidget(printCurrentView, "Current view"));


		printPages = new MiToggleButton();
		printPages.appendActionHandler(this, Mi_SELECTED_ACTION);
		printPages.setShape(MiVisibleContainer.DIAMOND_SHAPE);
		radioBox.appendPart(new MiLabeledWidget(printPages, "All Pages"));


		printEverything = new MiToggleButton();
		printEverything.appendActionHandler(this, Mi_SELECTED_ACTION);
		printEverything.setShape(MiVisibleContainer.DIAMOND_SHAPE);
		radioBox.appendPart(new MiLabeledWidget(printEverything, "All Graphics"));


		printWindow = new MiToggleButton();
		printWindow.appendActionHandler(this, Mi_SELECTED_ACTION);
		printWindow.setShape(MiVisibleContainer.DIAMOND_SHAPE);
		radioBox.appendPart(new MiLabeledWidget(printWindow, "Entire Window"));


		MiBox printSetupBox = new MiBox("Print Setup");
		MiColumnLayout psb_layout = new MiColumnLayout();
		psb_layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		psb_layout.setElementVSizing(Mi_NONE);
		psb_layout.setUniqueElementIndex(1);
		printSetupBox.setLayout(psb_layout);
		middle_layout.appendPart(printSetupBox);

		MiRowLayout delay_rowLayout = new MiRowLayout();
		delay_rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		delay_rowLayout.setUniqueElementIndex(1);
		delay_rowLayout.setElementVSizing(Mi_NONE);
		printSetupBox.appendPart(delay_rowLayout);
		delayBeforePrinting = new MiToggleButton();
		delayBeforePrinting.appendActionHandler(this, Mi_SELECTED_ACTION);
		delay_rowLayout.appendPart(delayBeforePrinting);
		delay_rowLayout.appendPart(new MiText(
			Mi_NUMBER_OF_SECONDS_DELAY_BEFORE_PRINTING + " second delay"));

		MiBox printToFileBox = new MiBox("Print To File");
		MiColumnLayout ptf_layout = new MiColumnLayout();
		ptf_layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		ptf_layout.setElementVJustification(Mi_JUSTIFIED);
		ptf_layout.setElementVSizing(Mi_NONE);
		ptf_layout.setUniqueElementIndex(1);
		printToFileBox.setLayout(ptf_layout);
		appendPart(printToFileBox);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(1);
		rowLayout.setElementVSizing(Mi_NONE);
		printToFileBox.appendPart(rowLayout);
		filenameField = new MiTextField();
		filenameField.setNumDisplayedColumns(20);
		filenameField.appendActionHandler(this, Mi_TEXT_CHANGE_ACTION);
		rowLayout.appendPart(new MiText("Filename"));
		rowLayout.appendPart(filenameField);

		MiBox box = new MiBox("Formats");
		MiColumnLayout c_ayout = new MiColumnLayout();
		c_ayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(c_ayout);

		radioBox = new MiRadioBox();
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
		printToFileBox.appendPart(box);

		doPrintToFile = new MiPushButton("Print to file");
		doPrintToFile.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		doPrintToFile.setSensitive(false);
		printToFileBox.appendPart(doPrintToFile);


		parmBox = new MiBox("Configuration");
		c_ayout = new MiColumnLayout();
		c_ayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		parmBox.setLayout(c_ayout);
		appendPart(parmBox);
		parmBox.setVisible(false);

		printPages.select(true);

		if (formatRadioButtons.size() > 0)
			{
			((MiWidget )formatRadioButtons.elementAt(0)).select(true);
			}
		}
	public		void		setDefaultDirectory(String directory)
		{
		defaultDirectory = directory;
		}
	public		String		getDefaultDirectory()
		{
		return(defaultDirectory);
		}
	public		void		setEditor(MiEditor editor)
		{
		this.editor = editor;
		}
	public		MiEditor	getEditor()
		{
		return(editor);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == filenameField)
			{
			doPrintToFile.setSensitive(!Utility.isEmptyOrNull(filenameField.getValue()));
			}
		else if ((action.getActionSource() == doPrintToFile)
			|| (action.getActionSource() == doPrint))
			{
			int whatToPrint = Mi_PRINT_EVERYTHING;
			if (printSelectedItems.isSelected())
				{
				whatToPrint = Mi_PRINT_SELECTED_ITEMS;
				}
			else if (printCurrentView.isSelected())
				{
				whatToPrint = Mi_PRINT_CURRENT_VIEW;
				}
			else if (printPages.isSelected())
				{
				whatToPrint = Mi_PRINT_DRAWING_PAGES;
				}
			else if (printEverything.isSelected())
				{
				whatToPrint = Mi_PRINT_EVERYTHING;
				}
			else if (printWindow.isSelected())
				{
				whatToPrint = Mi_PRINT_WINDOW;
				}

			String filename = null;
			if (action.getActionSource() == doPrintToFile)
				{
				filename = filenameField.getValue();
				if ((filename.indexOf('/') == -1) && (filename.indexOf('\\') == -1))
					{
					filename = defaultDirectory + "/" + filename;
					}
				}

			if (delayBeforePrinting.isSelected())
				{
				class PrintThread extends Thread
					{
					MiEditor editor;
					String filename;
					int whatToPrint;
					PrintThread(MiEditor editor, String filename, int whatToPrint)
						{
						this.editor = editor;
						this.filename = filename;
						this.whatToPrint = whatToPrint;
						}
					public void run()
						{
						try	{
							Thread.currentThread().sleep(
								Mi_NUMBER_OF_SECONDS_DELAY_BEFORE_PRINTING
								 * 1000);
							
							editor.getAccessLock();
							print(filename, printDriver, whatToPrint);
							editor.freeAccessLock();
							}
						catch (Exception e)
							{
							MiDebug.println("Print Error: " + e.getMessage());
							MiDebug.printStackTrace(e);
							}
						}
					};
				PrintThread t = new PrintThread(editor, filename, whatToPrint);
				t.start();
				}
			else
				{
				try	{
					print(filename, printDriver, whatToPrint);
					}
				catch (Exception e)
					{
					MiDebug.println("Print Error: " + e.getMessage());
					MiDebug.printStackTrace(e);
					}
				}
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
	protected	void		print(String filename, MiiPrintDriver printDriver, int whatToPrint) throws IOException
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

		if (filename == null)
			{
			// PrintUsingJDK
			printCommand.setPrintDriver(new MiJDKPrintDriver(
				((MiNativeWindow )editor.getRootWindow()).getFrame()));
			}

		String pageSize = MiiPaperSize.Mi_STANDARD_LETTER_PAPER_SIZE_NAME;
		if ((pageManager != null) && (pageManager.getPageSize() != null))
			{
			pageSize = pageManager.getPageSize().getName();
			}

		String orientation = Mi_PORTRAIT_NAME;
		if (drawingPages != null)
			{
			orientation = (drawingPages.getOrientation() 
				== Mi_VERTICAL ? Mi_PORTRAIT_NAME : Mi_LANDSCAPE_NAME);
			}

		if (whatToPrint == Mi_PRINT_SELECTED_ITEMS)
			{
			printCommand.setViewFilter(new MiSelectedOnlyViewFilter());
			MiParts parts = editor.getSelectedParts(new MiParts());
			MiBounds tmp = new MiBounds();
			MiBounds selectedItemsBounds = new MiBounds();
			for (int i = 0; i < parts.size(); ++i)
				{
				selectedItemsBounds.union(parts.elementAt(i).getDrawBounds(tmp));
				}

			printCommand.print(
				filename, 
				300,			// dotsPerInch, 
				pageSize,
				orientation,
				"COLOR",
				1,			// pages wide
				1,			// pages high
				selectedItemsBounds);	// boundsToPrint
			}
		else if (whatToPrint == Mi_PRINT_CURRENT_VIEW)
			{
			printCommand.print(
				filename, 
				300,			// dotsPerInch, 
				pageSize,
				orientation,
				"COLOR",
				1,			// pages wide
				1,			// pages high
				editor.getWorldBounds());	// boundsToPrint
			}
		else if (whatToPrint == Mi_PRINT_EVERYTHING)
			{
			MiBounds contentBounds = new MiBounds();
			editor.reCalcBoundsOfContents(contentBounds);

			printCommand.print(
				filename, 
				300,			// dotsPerInch, 
				pageSize,
				orientation,
				"COLOR",
				1,			// pages wide
				1,			// pages high
				contentBounds);	// boundsToPrint
			}
		else if (whatToPrint == Mi_PRINT_WINDOW)
			{
			printCommand.setTargetOfCommand(editor.getRootWindow());

			printCommand.print(
				filename, 
				300,			// dotsPerInch, 
				pageSize,
				orientation,
				"COLOR",
				1,			// pages wide
				1,			// pages high
				editor.getRootWindow().getWorldBounds());	// boundsToPrint
			}
		else if (whatToPrint == Mi_PRINT_DRAWING_PAGES)
			{
			if (drawingPages == null)
				{
				printCommand.setFilename(filename);
				printCommand.processCommand(null);
				}
			else
				{
				printCommand.print(
					filename, 
					300,		// 		dotsPerInch, 
					pageSize,
					orientation,
					"COLOR",
					drawingPages.getPagesWide(), 
					drawingPages.getPagesTall(), 
					drawingPages.getBounds());	// boundsToPrint
				}
			}
		}
	}
class MiSelectedOnlyViewFilter implements MiiViewFilter
	{
	public		MiPart		accept(MiPart part)
		{
		if (part.isSelected())
			{
			return(part);
			}

		MiPart c = part.getContainer(0);
		while (c != null)
			{
			if (c.isSelected())
				return(part);
			c = c.getContainer(0);
			}

		if (containsSelectedPart(part))
			{
			return(part);
			}

		return(null);
		}
	protected	boolean		containsSelectedPart(MiPart part)
		{
		for (int i = 0; i < part.getNumberOfParts(); ++i)
			{
			MiPart child = part.getPart(i);
			if (child.isSelected())
				{
				return(true);
				}
			if (containsSelectedPart(child))
				{
				return(true);
				}
			}
		return(false);
		}
	}

