
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

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiTriggerToTargetActionHandler implements MiiActionHandler, MiiActionTypes
	{
	public static final int		Mi_VALUE_TO_SENSITIVITY_STATE_MODE 		= 1;
	public static final int		Mi_SELECTION_STATE_TO_SENSITIVITY_STATE_MODE	= 2;
	public static final int		Mi_ALWAYS_INSENSITIVE_STATE_MODE		= 3;
	private		MiPart		trigger;
	private		MiWidget	triggerWidget;
	private		MiPart		target;
	private		MiParts		targets;
	private		int		mode 		= Mi_SELECTION_STATE_TO_SENSITIVITY_STATE_MODE;
	private		Strings		triggerValues;



					// Selection state of trigger => sensitivity of target
	public				MiTriggerToTargetActionHandler(MiPart trigger, MiPart target)
		{
		trigger.appendActionHandler(this, Mi_SELECTED_ACTION, Mi_DESELECTED_ACTION);
		this.trigger = trigger;
		this.target = target;
		mode = Mi_SELECTION_STATE_TO_SENSITIVITY_STATE_MODE;
		updateTargetSensitivities();
		}

					// Value of trigger => sensitivity of target
	public				MiTriggerToTargetActionHandler(
						MiWidget trigger, String triggerValue, MiPart target)
		{
		this(trigger, new Strings(triggerValue), new MiParts(target));
		}

					// Value of trigger => sensitivity of target
	public				MiTriggerToTargetActionHandler(
						MiWidget trigger, String triggerValue, MiParts targets)
		{
		this(trigger, new Strings(triggerValue), targets);
		}

	public				MiTriggerToTargetActionHandler(
						MiWidget trigger, Strings triggerValues, MiParts targets)
		{
		trigger.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		triggerWidget = trigger;
		this.triggerValues = new Strings(triggerValues);
		this.targets = new MiParts(targets);
		mode = Mi_VALUE_TO_SENSITIVITY_STATE_MODE;
		updateTargetSensitivities();
		}

	public		void		setSensitityMode(int sensitivityMode)
		{
		mode = sensitivityMode;
		updateTargetSensitivities();
		}
	public		int		getSensitityMode()
		{
		return(mode);
		}

	public		boolean		processAction(MiiAction action)
		{
		updateTargetSensitivities();
		return(true);
		}

	protected	void		updateTargetSensitivities()
		{
		if (mode == Mi_SELECTION_STATE_TO_SENSITIVITY_STATE_MODE)
			{
			if (trigger.isSelected())
				{
				target.setSensitive(true);
				}
			else 
				{
				target.setSensitive(false);
				}
			}
		else if (mode == Mi_VALUE_TO_SENSITIVITY_STATE_MODE)
			{
			String value = triggerWidget.getValue();
			if (triggerValues.contains(value))
				{
				if (targets != null)
					{
					for (int i = 0; i < targets.size(); ++i)
						{
						targets.elementAt(i).setSensitive(true);
						}
					}
				else
					{
					target.setSensitive(true);
					}
				}
			else
				{
				if (targets != null)
					{
					for (int i = 0; i < targets.size(); ++i)
						{
						targets.elementAt(i).setSensitive(false);
						}
					}
				else
					{
					target.setSensitive(false);
					}
				}
			}
		else if (mode == Mi_ALWAYS_INSENSITIVE_STATE_MODE)
			{
			if (targets != null)
				{
				for (int i = 0; i < targets.size(); ++i)
					{
					targets.elementAt(i).setSensitive(false);
					}
				}
			else
				{
				target.setSensitive(false);
				}
			}
		}
	}

