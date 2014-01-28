
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
public class MiMagnificationStatusField extends MiStatusBar implements MiiActionHandler, MiiActionTypes
	{
	private		MiWidget	field;
	private		MiEditor	scaledEditor;
	private		double		homeScaleFactor			= 1.0;
	private 	int		SCALE_FACTOR_FIELD_INDEX	= 0;
	private 	String		SCALE_FACTOR_AREA_TOOL_HINT	= "Magnification";


	public				MiMagnificationStatusField(MiEditor scaledEditor, String spec, int fieldIndex)
		{
		super(spec);
		SCALE_FACTOR_FIELD_INDEX = fieldIndex;
		setup(scaledEditor);
		}

	public				MiMagnificationStatusField(MiEditor scaledEditor)
		{
		super(".+6");
		setup(scaledEditor);
		}

	public		void		set100PercentScaleFactor(double factor)
		{
		homeScaleFactor = factor;
		}
	public		double		get100PercentScaleFactor()
		{
		return(homeScaleFactor);
		}

	public		void		setTargetEditor(MiEditor editor)
		{
		if (scaledEditor != null)
			{
			scaledEditor.removeActionHandlers(this);
			}
		scaledEditor = editor;

		scaledEditor.appendActionHandler(this, Mi_EDITOR_DEVICE_RESIZED_ACTION);
		scaledEditor.appendActionHandler(this, Mi_EDITOR_WORLD_RESIZED_ACTION);
		scaledEditor.appendActionHandler(this, Mi_EDITOR_UNIVERSE_RESIZED_ACTION);
		updateScaleFactorField();
		}

	protected	void		setup(MiEditor scaledEditor)
		{
		setBorderLook(Mi_NONE);
		setInsetMargins(0);
		field = getField(SCALE_FACTOR_FIELD_INDEX);
		field.setToolHintMessage(SCALE_FACTOR_AREA_TOOL_HINT);

		field.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		field.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		setTargetEditor(scaledEditor);
		}
	public		MiWidget	getField()
		{
		return(field);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == scaledEditor)
			{
			updateScaleFactorField();
			}
		else if (action.getActionSource() == field)
			{
			String value = field.getValue();
			if ((value.length() >= 2) && (value.startsWith("Z:")))
				value = value.substring(2);
			updateScaleFactor(Utility.toDouble(value));
			}
		return(true);
		}

	protected	void		updateScaleFactor(double percent)
		{
		MiBounds world = scaledEditor.getWorldBounds();
		MiBounds universe = scaledEditor.getUniverseBounds();
		double factor = percent/100 * homeScaleFactor;
		world.setWidth(universe.getWidth() / factor);
		world.setHeight(universe.getHeight() / factor);
		world.confineInsideContainer(universe);
		if (!world.equals(scaledEditor.getWorldBounds()))
			scaledEditor.setWorldBounds(world);
		else
			updateScaleFactorField();
		}
	protected	void		updateScaleFactorField()
		{
		MiBounds world = scaledEditor.getWorldBounds();
		MiBounds universe = scaledEditor.getUniverseBounds();
		double xFactor = universe.getWidth()/world.getWidth();
		double yFactor = universe.getHeight()/world.getHeight();
		double percent = xFactor/homeScaleFactor * 100;
		String percentStr = new String(percent + "");
		if (percentStr.length() > 3)
			percentStr = percentStr.substring(0, 3);
		field.setValue("Z:" + percentStr + "%");
		}
	}

