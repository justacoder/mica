
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
import java.util.Date; 
import java.util.Properties; 
import java.util.Enumeration; 
import com.swfm.mica.util.Utility; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSystemResourceDialog extends MiWidget implements MiiCommandNames, MiiSimpleAnimator
	{
	private static final String		DEFAULT_WINDOW_BORDER_TITLE	= "System Resources";
	private		MiPart			titleLabel;
	private		MiNativeDialog		dialog;
	private		MiOkCancelHelpButtons	okCancelHelpButtons;
	private		MiLabel			date;
	private		MiLabel			totalMemoryField;
	private		MiLabel			usedMemoryField;
	private		MiLabel			freeMemoryField;
	private		MiPieChart		pieChart;
	private		MiBasicPropertyPanel	systemProperties;
	private		boolean			runGarbageCollectionWheneverMemoryUsageIsExamined = true;
	private		int			secondsBetweenExaminingMemoryUsage	= 1;



	public				MiSystemResourceDialog(MiEditor parent, String title, boolean modal)
		{
		this(parent, title, modal, false);
		}
	public				MiSystemResourceDialog(MiEditor parent, String title, boolean modal, boolean readOnly)
		{
		title = (title == null ? DEFAULT_WINDOW_BORDER_TITLE : title);
		buildBox(title);

		setBorderLook(Mi_RAISED_BORDER_LOOK);

		dialog = new MiNativeDialog(parent, title, modal);

		if (!readOnly)
			{
			okCancelHelpButtons = new MiOkCancelHelpButtons(dialog,
				"OK", Mi_OK_COMMAND_NAME,
				"Cancel", Mi_CANCEL_COMMAND_NAME,
				"Help", Mi_HELP_COMMAND_NAME);
			}
		else
			{
			okCancelHelpButtons = new MiOkCancelHelpButtons(dialog,
				"Close", Mi_OK_COMMAND_NAME,
				null, null,
				"Help", Mi_HELP_COMMAND_NAME);
			}
		appendPart(okCancelHelpButtons);

		setup();
		dialog.appendPart(this);
		}

	public				MiSystemResourceDialog(String title)
		{
		buildBox(title);
		setup();
		}

	public		long		animate()
		{
		updateDynamicResources();
		return(secondsBetweenExaminingMemoryUsage * 1000);
		}

	protected	void		setup()
		{
		pieChart.removeAllSlices();
		pieChart.addSlice("Free", MiColorManager.green, 0.5);
		pieChart.addSlice("Used", MiColorManager.red, 0.5);

		updateDynamicResources();

		Properties properties = System.getProperties();
		Enumeration e = properties.propertyNames();
		MiPropertyWidgets widgets = new MiPropertyWidgets();
		int maxFieldLength = 20;
		while (e.hasMoreElements())
			{
			String name = (String )e.nextElement();
			String value = properties.getProperty(name);
			widgets.appendPropertyWidget(new MiPropertyWidget(name, value));
			maxFieldLength = Math.max(maxFieldLength, value.length());
			}
		
		maxFieldLength = Math.min(40, maxFieldLength);
		for (int i = 0; i < widgets.size(); ++i)
			{
			widgets.elementAt(i).setNumDisplayedColumns(maxFieldLength);
			}

		systemProperties.setPropertyWidgets(widgets);
		systemProperties.open();
		}

	public		void		reloadJavaSystemProperties()
		{
		Properties properties = System.getProperties();
		Enumeration e = properties.propertyNames();
		while (e.hasMoreElements())
			{
			String name = (String )e.nextElement();
			String value = properties.getProperty(name);
			systemProperties.setPropertyValue(name, value);
			}
		}
	public		void		updateDynamicResources()
		{
		Date dateTime = new Date();
		date.setValue(dateTime.toString());

		long[] freeMemory = new long[1];
		long[] totalMemory = new long[1];
		MiDebug.getMemoryStatistics(
			freeMemory, totalMemory, runGarbageCollectionWheneverMemoryUsageIsExamined);

		totalMemoryField.setValue(Utility.toCommaFormattedString(totalMemory[0]));
		usedMemoryField.setValue(Utility.toCommaFormattedString(totalMemory[0] - freeMemory[0]));
		freeMemoryField.setValue(Utility.toCommaFormattedString(freeMemory[0]));

		double free = ((double )freeMemory[0])/totalMemory[0];
		if (Math.abs(pieChart.getSliceAmount("Free") - free) > 0.02)
			pieChart.setSliceAmount("Free", free);
		double used = ((double )(totalMemory[0] - freeMemory[0]))/totalMemory[0];
		if (Math.abs(pieChart.getSliceAmount("Used") - used) > 0.02)
			pieChart.setSliceAmount("Used", used);
		}

	public 		String		popupAndWaitForClose()
		{
		if (dialog != null)
			{
			reloadJavaSystemProperties();
			dialog.getAnimationManager().addAnimator(this);
			String button = dialog.popupAndWaitForClose();
			dialog.getAnimationManager().removeAnimator(this);
			if ((button != null) && (button.equals(Mi_OK_COMMAND_NAME)))
				{
				// Apply any changes to the properties...
				}
			return(button);
			}
		return(null);
		}
	// -----------------------------------------------------------------------
	//	Fields 
	// -----------------------------------------------------------------------
	public		MiPart		getTitleLabel()
		{
		return(titleLabel);
		}
	public		void		setTitleLabel(MiPart title)
		{
		this.titleLabel.replaceSelf(titleLabel);
		this.titleLabel = titleLabel;
		}
	public		MiNativeDialog	getDialog()
		{
		return(dialog);
		}
	// -----------------------------------------------------------------------
	//	Control 
	// -----------------------------------------------------------------------
	public		void		setVisible(boolean flag)
		{
		super.setVisible(flag);
		if (dialog != null)
			dialog.setVisible(flag);
		if (flag)
			setup();
		else if (dialog != null)
			dialog.getAnimationManager().removeAnimator(this);
			
		}
	// -----------------------------------------------------------------------
	//	Internal functionality 
	// -----------------------------------------------------------------------
	protected	void		buildBox(String title)
		{
		MiColumnLayout layout = new MiColumnLayout();
		setLayout(layout);
			
		titleLabel = new MiLabel(title);
		((MiLabel )titleLabel).getLabel().setFontPointSize(24);
		((MiLabel )titleLabel).getLabel().setFontBold(true);
		appendPart(titleLabel);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementHJustification(Mi_JUSTIFIED);
		appendPart(rowLayout);

		// --------------------------
		//	Attributes and Properties
		// --------------------------

		// --------------------------
		// 	Date/Time
		// --------------------------
		MiColumnLayout columnLayout = new MiColumnLayout();
		rowLayout.appendPart(columnLayout);

		date = new MiLabel();
		//time = new MiLabel();

		columnLayout.appendPart(date);
		//columnLayout.appendPart(time);

		// --------------------------
		// 	Memory
		// --------------------------

		columnLayout = new MiColumnLayout();
		rowLayout.appendPart(columnLayout);

		pieChart = new MiPieChart();
		pieChart.setPreferredSize(new MiSize(100, 100));
		columnLayout.appendPart(pieChart);

		MiGridLayout gridLayout = new MiGridLayout();
		columnLayout.appendPart(gridLayout);

		MiLabel totalMemoryLabel = new MiLabel("Total memory");
		MiLabel usedMemoryLabel = new MiLabel("Used memory");
		MiLabel freeMemoryLabel = new MiLabel("Free memory");
		totalMemoryField = new MiLabel();
		usedMemoryField = new MiLabel();
		freeMemoryField = new MiLabel();

		gridLayout.setNumberOfColumns(3);
		gridLayout.appendPart(totalMemoryLabel);
		gridLayout.appendPart(usedMemoryLabel);
		gridLayout.appendPart(freeMemoryLabel);

		gridLayout.appendPart(totalMemoryField);
		gridLayout.appendPart(usedMemoryField);
		gridLayout.appendPart(freeMemoryField);
		
		systemProperties = new MiBasicPropertyPanel(true);
		appendPart(systemProperties);
		}
	}

