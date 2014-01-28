
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSystemResourcesStatusField extends MiPieChart implements MiiSimpleAnimator, MiiCommandHandler, MiiEventTypes, MiiCommandNames
	{
	private		MiSystemResourceDialog	systemResourceDialog;
	private		MiPieChart	pieChart;
	private		String		SYSTEM_RESOURCES_AREA_TOOL_HINT	= "System resources (memory)";
	private		boolean		runGarbageCollectionWheneverMemoryUsageIsExamined = true;
	private		int		secondsBetweenExaminingMemoryUsage	= 5;
	private		long[]		freeMemory = new long[1];
	private		long[]		totalMemory = new long[1];


	public				MiSystemResourcesStatusField(MiAnimationManager aMan)
		{
		pieChart = this;
		setup(aMan);
		}

	public		void		setSecondsBetweenExaminingMemoryUsage(int seconds)
		{
		secondsBetweenExaminingMemoryUsage = seconds;
		}
	public		int		getSecondsBetweenExaminingMemoryUsage()
		{
		return(secondsBetweenExaminingMemoryUsage);
		}

	protected	void		setup(MiAnimationManager aMan)
		{
		pieChart.setToolHintMessage(SYSTEM_RESOURCES_AREA_TOOL_HINT);
		pieChart.setDepth(4);
		pieChart.setPerspective(1.2);
		MiSize size = new MiSize();
		size.height = getFont().getMaxCharHeight() + 6;
		size.width = size.height;
		pieChart.setPreferredSize(size);
		
		pieChart.appendCommandHandler(this, Mi_EXECUTE_COMMAND_NAME, 
				new MiEvent(Mi_LEFT_MOUSE_DBLCLICK_EVENT, 0, 0));

		pieChart.removeAllSlices();
		pieChart.addSlice("Free", MiColorManager.green, 0.01);
		pieChart.addSlice("Used", MiColorManager.red, 0.99);
		updateSystemResourcesStatus();
		if (aMan != null)
			aMan.addAnimator(this);
		}

	protected	void		updateSystemResourcesStatus()
		{
		boolean updated = false;

		MiDebug.getMemoryStatistics(
			freeMemory, totalMemory, runGarbageCollectionWheneverMemoryUsageIsExamined);

		double free = ((double )freeMemory[0])/totalMemory[0];
		if (Math.abs(pieChart.getSliceAmount("Free") - free) > 0.02)
			{
			pieChart.setSliceAmount("Free", free);
			updated = true;
			}
		double used = ((double )(totalMemory[0] - freeMemory[0]))/totalMemory[0];
		if (Math.abs(pieChart.getSliceAmount("Used") - used) > 0.02)
			{
			pieChart.setSliceAmount("Used", used);
			updated = true;
			}

		if (updated)
			{
			pieChart.setToolHintMessage(
			   "Memory(bytes)\nTotal = " + Utility.toCommaFormattedString(totalMemory[0])
			   + "\n Used = " + Utility.toCommaFormattedString(totalMemory[0] - freeMemory[0])
			   + "\n Free = " + Utility.toCommaFormattedString(freeMemory[0]));
			}
		}
	public		long		animate()
		{
		updateSystemResourcesStatus();
		return(secondsBetweenExaminingMemoryUsage * 1000);
		}
	public		void		processCommand(String command)
		{
		getContainingWindow().setMouseAppearance(MiiTypes.Mi_WAIT_CURSOR);
		if (systemResourceDialog == null)
			{
			systemResourceDialog = new MiSystemResourceDialog(
				getContainingEditor(), null, true, true);
			}
		systemResourceDialog.popupAndWaitForClose();
		getContainingWindow().setMouseAppearance(MiiTypes.Mi_DEFAULT_CURSOR);
		}
	}

