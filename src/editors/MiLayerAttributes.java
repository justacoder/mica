
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
import java.awt.Color;
import com.swfm.mica.util.NamedEnumeratedType;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiLayerAttributes extends MiModelEntity implements MiiTypes, MiiCommandHandler, MiiCommandManager, MiiCommandNames
	{
	private static	MiPropertyDescriptions	propertyDescriptions;

	public static final String 	Mi_LAYER_ATTRIBUTES_RESOURCE_NAME	= "MiLayerManagerResource";

	public static final String	Mi_LAYER_NAME_NAME			= "Name";
	public static final String	Mi_LAYER_CURRENT_NAME			= "Current";
	public static final String	Mi_LAYER_SNAP_TO_ABLE_NAME		= "Snap-to-able";
	public static final String	Mi_LAYER_CONNECT_TO_ABLE_NAME		= "Connect-to-able";
	public static final String	Mi_LAYER_VISIBLE_NAME			= "Visible";
	public static final String	Mi_LAYER_PRINTABLE_NAME			= "Printable";
	public static final String	Mi_LAYER_OVERRIDE_SHAPE_COLORS_NAME	= "Override shape colors";
	public static final String	Mi_LAYER_NUMBER_OF_SHAPES_NAME		= "Number of shapes";
	public static final String	Mi_LAYER_BACKGROUND_COLOR_NAME		= "Background color";
	public static final String	Mi_LAYER_SHAPE_COLOR_NAME		= "Shape color";
	public static final String	Mi_LAYER_OVERRIDE_SHAPE_COLORS_NAME_NSP	= "Overrideshapecolors";
	public static final String	Mi_LAYER_NUMBER_OF_SHAPES_NAME_NSP	= "Numberofshapes";
	public static final String	Mi_LAYER_BACKGROUND_COLOR_NAME_NSP	= "Backgroundcolor";
	public static final String	Mi_LAYER_SHAPE_COLOR_NAME_NSP		= "Shapecolor";

	public static final String	Mi_LAYER_EDITABILITY_NAME		= "Editability";
	public static final String	Mi_LAYER_LOCKED_NAME			= "Locked";
	public static final String	Mi_LAYER_ALWAYS_EDITABLE_NAME		= "Always editable";
	public static final String	Mi_LAYER_NEVER_EDITABLE_NAME		= "Never editable";
	public static final String	Mi_LAYER_EDITABLE_WHEN_CURRENT_NAME	= "Editable when current";

	public static final String	Mi_EDITABLE_COMMAND_NAME		= "Editable";
	public static final String	Mi_NOT_EDITABLE_COMMAND_NAME		= "NotEditable";
	public static final String	Mi_PRINTABLE_COMMAND_NAME		= "Printable";
	public static final String	Mi_NOT_PRINTABLE_COMMAND_NAME		= "NotPrintable";
	public static final String	Mi_CONNECT_TO_ABLE_COMMAND_NAME		= "ConnectToAble";
	public static final String	Mi_NOT_CONNECT_TO_ABLE_COMMAND_NAME	= "NotConnectToAble";
	public static final String	Mi_SNAP_TO_ABLE_COMMAND_NAME		= "SnapToAble";
	public static final String	Mi_NOT_SNAP_TO_ABLE_COMMAND_NAME	= "NotSnapToAble";
	public static final String	Mi_ADD_NEW_LAYER_BEHIND_COMMAND_NAME	= "addLayerBehind";
	public static final String	Mi_ADD_NEW_LAYER_INFRONT_COMMAND_NAME	= "addLayerInFront";

	//public static final int		Mi_LAYER_LOCKED				= 0;
	//public static final int		Mi_LAYER_ALWAYS_EDITABLE		= 1;
	//public static final int		Mi_LAYER_EDITABLE_WHEN_CURRENT		= 2;
	//public static final int		Mi_LAYER_NEVER_EDITABLE			= 3;

	public static final MiLayerEditability	Mi_LAYER_LOCKED	
								= new MiLayerEditability(
								Mi_LAYER_LOCKED_NAME, 0);
	public static final MiLayerEditability	Mi_LAYER_ALWAYS_EDITABLE
								= new MiLayerEditability(
								Mi_LAYER_ALWAYS_EDITABLE_NAME, 1);
	public static final MiLayerEditability	Mi_LAYER_EDITABLE_WHEN_CURRENT
								= new MiLayerEditability(
								Mi_LAYER_EDITABLE_WHEN_CURRENT_NAME, 2);
	public static final MiLayerEditability	Mi_LAYER_NEVER_EDITABLE
								= new MiLayerEditability(
								Mi_LAYER_NEVER_EDITABLE_NAME, 3);


	private		boolean		current;
	private		boolean		canBeCurrent		= true;
	private		boolean		snapToAble;
	private		boolean		connectToAble;
	private		boolean		overrideShapeColors;
	private		boolean		applyToEachPage;
	private		Color		shapeColor;
	private		String		name;

	private		MiLayerEditability editability		= Mi_LAYER_EDITABLE_WHEN_CURRENT;
	private		MiPart		layer;
	private		MiCommandManager cmdWidgetManager = new MiCommandManager();


	public				MiLayerAttributes()
		{
		}
	public				MiLayerAttributes(MiPart layer)
		{
		this.layer = layer;
		layer.setPickable(false);
		layer.setResource(Mi_LAYER_ATTRIBUTES_RESOURCE_NAME, this);
		setCommandState(Mi_PRINTABLE_COMMAND_NAME, true);
		setCommandState(Mi_SHOW_COMMAND_NAME, true);
		}
	public		MiPart		getLayer()
		{
		return(layer);
		}

	public		void		setName(String name)
		{
		if (!name.equals(getName()))
			{
			if (validatePropertyValue(Mi_LAYER_NAME_NAME, name) == null)
				{
				String oldName = this.name;
				this.name = name;
				dispatchPropertyChange(this, Mi_LAYER_NAME_NAME, name, oldName);
				}
			}
		}
	public		String		getName()
		{
		return(name);
		}

	public		void		setCanBeCurrent(boolean flag)
		{
		canBeCurrent = flag;
		}
	public		boolean		getCanBeCurrent()
		{
		return(canBeCurrent);
		}


	public		void		setCurrent(boolean flag)
		{
		if (flag != current)
			{
			if (validatePropertyValue(Mi_LAYER_CURRENT_NAME, Utility.toString(flag)) == null)
				{
				boolean oldValue = current;
				current = flag;
				if (flag)
					layer.getContainingEditor().setCurrentLayer(layer);
				if ((!current) && (editability != Mi_LAYER_ALWAYS_EDITABLE))
					layer.setPickable(false);
				else
					layer.setPickable(true);

				dispatchPropertyChange(this, Mi_LAYER_CURRENT_NAME, 
					Utility.toString(current), Utility.toString(oldValue));

				setCommandState(Mi_EDITABLE_COMMAND_NAME, layer.isPickable());
				}
			}
		}
	public		boolean		isCurrent()
		{
		return(current);
		}
	public		void		setSnapToAble(boolean flag)
		{
		if (flag != snapToAble)
			{
			if (validatePropertyValue(Mi_LAYER_SNAP_TO_ABLE_NAME, Utility.toString(flag)) == null)
				{
				boolean oldValue = snapToAble;
				snapToAble = flag;
				dispatchPropertyChange(this, Mi_LAYER_SNAP_TO_ABLE_NAME, 
					Utility.toString(snapToAble), Utility.toString(oldValue));
				setCommandState(Mi_SNAP_TO_ABLE_COMMAND_NAME, flag);
				}
			}
		}
	public		boolean		isSnapToAble()
		{
		return(snapToAble);
		}

	public		void		setConnectToAble(boolean flag)
		{
		if (flag != connectToAble)
			{
			if (validatePropertyValue(
				Mi_LAYER_CONNECT_TO_ABLE_NAME, Utility.toString(flag)) == null)
				{
				boolean oldValue = connectToAble;
				connectToAble = flag;
				dispatchPropertyChange(this, Mi_LAYER_CONNECT_TO_ABLE_NAME, 
					Utility.toString(connectToAble), Utility.toString(oldValue));
				setCommandState(Mi_CONNECT_TO_ABLE_COMMAND_NAME, flag);
				}
			}
		}
	public		boolean		isConnectToAble()
		{
		return(connectToAble);
		}

	public		void		setVisible(boolean flag)
		{
		if (validatePropertyValue(Mi_LAYER_VISIBLE_NAME, Utility.toString(flag)) == null)
			{
			boolean oldValue = layer.isVisible();
			layer.setVisible(flag);
			dispatchPropertyChange(this, Mi_LAYER_VISIBLE_NAME, 
				Utility.toString(isVisible()), Utility.toString(oldValue));
			setCommandState(Mi_SHOW_COMMAND_NAME, flag);
			}
		}
	public		boolean		isVisible()
		{
		return(layer.isVisible());
		}

	public		void		setPrintable(boolean flag)
		{
		if (validatePropertyValue(Mi_LAYER_PRINTABLE_NAME, Utility.toString(flag)) == null)
			{
			boolean oldValue = layer.isPrintable();
			layer.setPrintable(flag);
			dispatchPropertyChange(this, Mi_LAYER_PRINTABLE_NAME, 
				Utility.toString(flag), Utility.toString(oldValue));
			setCommandState(Mi_PRINTABLE_COMMAND_NAME, flag);
			}
		}
	public		boolean		isPrintable()
		{
		return(layer.isPrintable());
		}
	public		void		setOverrideShapeColors(boolean flag)
		{
		if (validatePropertyValue(
			Mi_LAYER_OVERRIDE_SHAPE_COLORS_NAME, Utility.toString(flag)) == null)
			{
			boolean oldValue = overrideShapeColors;
			overrideShapeColors = flag;
			dispatchPropertyChange(this, Mi_LAYER_OVERRIDE_SHAPE_COLORS_NAME, 
				Utility.toString(overrideShapeColors), Utility.toString(oldValue));
			}
		}
	public		boolean		getOverrideShapeColors()
		{
		return(overrideShapeColors);
		}

	public		void		setShapeColor(Color c)
		{
		if (validatePropertyValue(Mi_LAYER_SHAPE_COLOR_NAME, MiColorManager.getColorName(c)) == null)
			{
			Color oldValue = shapeColor;
			shapeColor = c;
			dispatchPropertyChange(this, Mi_LAYER_SHAPE_COLOR_NAME, 
				MiColorManager.getColorName(shapeColor), 
				MiColorManager.getColorName(oldValue));
			}
		}
	public		Color		getShapeColor()
		{
		return(shapeColor);
		}

	public		void		setBackgroundColor(Color c)
		{
		if (validatePropertyValue(
			Mi_LAYER_BACKGROUND_COLOR_NAME, MiColorManager.getColorName(c)) == null)
			{
			Color oldValue = layer.getBackgroundColor();
			layer.setBackgroundColor(c);
			dispatchPropertyChange(this, Mi_LAYER_BACKGROUND_COLOR_NAME, 
				MiColorManager.getColorName(c), MiColorManager.getColorName(oldValue));
			}
		}
	public		Color		getBackgroundColor()
		{
		return(layer.getBackgroundColor());
		}

	public		void		setEditability(MiLayerEditability e)
		{
		if (validatePropertyValue(Mi_LAYER_EDITABILITY_NAME, e.getName()) == null)
			{
			MiLayerEditability oldValue = editability;
			editability = e;
			dispatchPropertyChange(this, Mi_LAYER_EDITABILITY_NAME, e.getName(), oldValue.getName());
			}
		}
	public		MiLayerEditability	getEditability()
		{
		return(editability);
		}

	public		void		setEditable(boolean flag)
		{
		layer.setPickable(flag);
		setCommandState(Mi_EDITABLE_COMMAND_NAME, flag);
		}
	public		boolean		isEditable()
		{
		return(layer.isPickable());
		}

	public		void		setDeletable(boolean flag)
		{
		setCommandSensitivity(Mi_DELETE_COMMAND_NAME, flag);
		layer.setDeletable(flag);
		}

	public		void		updateCommandManagerState()
		{
		setCommandSensitivity(Mi_DELETE_COMMAND_NAME, layer.isDeletable());
		setCommandState(Mi_PRINTABLE_COMMAND_NAME, isPrintable());
		setCommandState(Mi_SHOW_COMMAND_NAME, isVisible());
		setCommandState(Mi_EDITABLE_COMMAND_NAME, isEditable());

		MiEditor editor = layer.getContainingEditor();
		int indexOfTabForLayer = 0;
		int numberOfLayersWithTabs = 0;
		for (int i = 0; i < editor.getNumberOfLayers(); ++i)
			{
			MiPart aLayer = editor.getLayer(i);
			if (aLayer == layer)
				indexOfTabForLayer = numberOfLayersWithTabs;

			MiLayerAttributes layerAttributes = (MiLayerAttributes )aLayer.getResource(
				MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME);
			if ((layerAttributes != null)
				&& (layerAttributes.getEditability() != MiLayerAttributes.Mi_LAYER_NEVER_EDITABLE))
				{
				++numberOfLayersWithTabs;
				}
			}

		if (indexOfTabForLayer == numberOfLayersWithTabs - 1)
			setCommandSensitivity(Mi_BRING_FORWARD_COMMAND_NAME, false);
		else
			setCommandSensitivity(Mi_BRING_FORWARD_COMMAND_NAME, true);
		if (indexOfTabForLayer == 0)
			setCommandSensitivity(Mi_SEND_BACKWARD_COMMAND_NAME, false);
		else
			setCommandSensitivity(Mi_SEND_BACKWARD_COMMAND_NAME, true);
		}

	public		int		getNumberOfShapes()
		{
		return(layer.getNumberOfParts());
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		name = Utility.replaceAll(name, " ", "");

		if (name.equalsIgnoreCase(Mi_LAYER_NAME_NAME))
			setName(value);
		else if (name.equalsIgnoreCase(Mi_LAYER_CURRENT_NAME))
			setCurrent(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_LAYER_SNAP_TO_ABLE_NAME))
			setSnapToAble(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_LAYER_CONNECT_TO_ABLE_NAME))
			setConnectToAble(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_LAYER_OVERRIDE_SHAPE_COLORS_NAME_NSP))
			setOverrideShapeColors(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_LAYER_VISIBLE_NAME))
			setVisible(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_LAYER_PRINTABLE_NAME))
			setPrintable(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_LAYER_SHAPE_COLOR_NAME_NSP))
			setShapeColor(MiColorManager.getColor(value));
		else if (name.equalsIgnoreCase(Mi_LAYER_BACKGROUND_COLOR_NAME_NSP))
			setBackgroundColor(MiColorManager.getColor(value));
		else if (name.equalsIgnoreCase(Mi_LAYER_EDITABILITY_NAME))
			{
			if (value.equals(Mi_LAYER_LOCKED_NAME))
				setEditability(Mi_LAYER_LOCKED);
			else if (value.equals(Mi_LAYER_ALWAYS_EDITABLE_NAME))
				setEditability(Mi_LAYER_ALWAYS_EDITABLE);
			else if (value.equals(Mi_LAYER_EDITABLE_WHEN_CURRENT_NAME))
				setEditability(Mi_LAYER_EDITABLE_WHEN_CURRENT);
			}
		else
			{
			throw new IllegalArgumentException(this 
				+ ": Property: " + name + " not found in: " + this);
			}
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		name = Utility.replaceAll(name, " ", "");
		if (name.equalsIgnoreCase(Mi_LAYER_NAME_NAME))
			return(getName());
		else if (name.equalsIgnoreCase(Mi_LAYER_CURRENT_NAME))
			return(Utility.toString(isCurrent()));
		else if (name.equalsIgnoreCase(Mi_LAYER_SNAP_TO_ABLE_NAME))
			return(Utility.toString(isSnapToAble()));
		else if (name.equalsIgnoreCase(Mi_LAYER_CONNECT_TO_ABLE_NAME))
			return(Utility.toString(isConnectToAble()));
		else if (name.equalsIgnoreCase(Mi_LAYER_OVERRIDE_SHAPE_COLORS_NAME_NSP))
			return(Utility.toString(getOverrideShapeColors()));
		else if (name.equalsIgnoreCase(Mi_LAYER_VISIBLE_NAME))
			return(Utility.toString(isVisible()));
		else if (name.equalsIgnoreCase(Mi_LAYER_PRINTABLE_NAME))
			return(Utility.toString(isPrintable()));
		else if (name.equalsIgnoreCase(Mi_LAYER_NUMBER_OF_SHAPES_NAME_NSP))
			return(Utility.toString(getNumberOfShapes()));
		else if (name.equalsIgnoreCase(Mi_LAYER_SHAPE_COLOR_NAME_NSP))
			return(MiColorManager.getColorName(getShapeColor()));
		else if (name.equalsIgnoreCase(Mi_LAYER_BACKGROUND_COLOR_NAME_NSP))
			return(MiColorManager.getColorName(getBackgroundColor()));
		else if (name.equalsIgnoreCase(Mi_LAYER_EDITABILITY_NAME))
			return(editability.getName());
		throw new IllegalArgumentException(this 
			+ ": Property: " + name + " not found in: " + this);
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = new MiPropertyDescriptions(getClass().getName());

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_NAME_NAME, 
			Mi_STRING_TYPE, "Layer"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_CURRENT_NAME, 
			Mi_BOOLEAN_TYPE, Mi_FALSE_NAME));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_SNAP_TO_ABLE_NAME, 
			Mi_BOOLEAN_TYPE, Mi_FALSE_NAME));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_CONNECT_TO_ABLE_NAME, 
			Mi_BOOLEAN_TYPE, Mi_FALSE_NAME));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_OVERRIDE_SHAPE_COLORS_NAME, 
			Mi_BOOLEAN_TYPE, Mi_FALSE_NAME));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_VISIBLE_NAME, 
			Mi_BOOLEAN_TYPE, Mi_TRUE_NAME));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_PRINTABLE_NAME, 
			Mi_BOOLEAN_TYPE, Mi_TRUE_NAME));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_SHAPE_COLOR_NAME, 
			Mi_COLOR_TYPE, "white"));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_BACKGROUND_COLOR_NAME, 
			Mi_COLOR_TYPE, Mi_TRANSPARENT_COLOR_NAME));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_EDITABILITY_NAME, 
				new Strings(
					Mi_LAYER_LOCKED_NAME + "\n" +
					Mi_LAYER_ALWAYS_EDITABLE_NAME + "\n" +
					Mi_LAYER_EDITABLE_WHEN_CURRENT_NAME  + "\n" + 
					Mi_LAYER_NEVER_EDITABLE_NAME), 
				Mi_LAYER_EDITABLE_WHEN_CURRENT_NAME));

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_LAYER_NUMBER_OF_SHAPES_NAME, 
			Mi_POSITIVE_INTEGER_TYPE, "0").setEditable(false));

		return(propertyDescriptions);
		}
	public		void		processCommand(String cmd)
		{
		if (cmd.equals(Mi_SHOW_COMMAND_NAME))
			setVisible(true);
		else if (cmd.equals(Mi_HIDE_COMMAND_NAME))
			setVisible(false);
		else if (cmd.equals(Mi_EDITABLE_COMMAND_NAME))
			setEditable(true);
		else if (cmd.equals(Mi_NOT_EDITABLE_COMMAND_NAME))
			setEditable(false);
		else if (cmd.equals(Mi_PRINTABLE_COMMAND_NAME))
			setPrintable(true);
		else if (cmd.equals(Mi_NOT_PRINTABLE_COMMAND_NAME))
			setPrintable(false);
		else if (cmd.equals(Mi_CONNECT_TO_ABLE_COMMAND_NAME))
			setConnectToAble(true);
		else if (cmd.equals(Mi_NOT_CONNECT_TO_ABLE_COMMAND_NAME))
			setConnectToAble(false);
		else if (cmd.equals(Mi_SNAP_TO_ABLE_COMMAND_NAME))
			setSnapToAble(true);
		else if (cmd.equals(Mi_NOT_SNAP_TO_ABLE_COMMAND_NAME))
			setSnapToAble(false);
		else if (cmd.equals(Mi_DELETE_COMMAND_NAME))
			layer.getContainingEditor().removeLayer(layer);
		else if (cmd.equals(Mi_ADD_NEW_LAYER_BEHIND_COMMAND_NAME))
			{
			layer.getContainingEditor().insertLayer(new MiLayer(),
				layer.getContainingEditor().getIndexOfLayer(layer));
			}
		else if (cmd.equals(Mi_ADD_NEW_LAYER_INFRONT_COMMAND_NAME))
			{
			layer.getContainingEditor().insertLayer(new MiLayer(),
				layer.getContainingEditor().getIndexOfLayer(layer) + 1);
			}
		else if (cmd.equals(Mi_BRING_FORWARD_COMMAND_NAME))
			{
			MiEditor editor = layer.getContainingEditor();
			int index = editor.getIndexOfLayer(layer);
			editor.removeLayer(layer);
			editor.insertLayer(layer, index + 1);
			}
		else if (cmd.equals(Mi_SEND_BACKWARD_COMMAND_NAME))
			{
			MiEditor editor = layer.getContainingEditor();
			int index = editor.getIndexOfLayer(layer);
			editor.removeLayer(layer);
			editor.insertLayer(layer, index - 1);
			}
		}

	//***************************************************************************************
	// Implementation of MiiCommandManager
	//***************************************************************************************

					/**------------------------------------------------------
					 * Registers the given widget and the command it generates.
					 * This permits (de)sensitization of the widget by the
					 * setCommandSensitivity method. 
					 * @param widget  	the widget that generates the command
					 * @param command 	the command
					 * @see			#processCommand
					 * @implements		
					 *	MiiCommandManager#registerCommandDependentWidget
					 *------------------------------------------------------*/
	public		void		registerCommandDependentWidget(MiPart widget, String cmd)
		{
		cmdWidgetManager.registerCommandDependentWidget(widget, cmd);
		}
					/**------------------------------------------------------
					 * UnRegisters the given widget and the command it generates.
					 * Either the given widget or the given command may be null.
					 * @param widget  	the widget that generates the command
					 * @param command 	the command
					 * @see			#processCommand
					 * @implements		
					 *	MiiCommandManager#unRegisterWidgetGeneratedCommand
					 *------------------------------------------------------*/
	public		void		unRegisterWidgetGeneratedCommand(MiPart widget, String cmd)
		{
		cmdWidgetManager.unRegisterWidgetGeneratedCommand(widget, cmd);
		}
					/**------------------------------------------------------
					 * Sets whether the given command is to be visible to the
					 * user. This method typically (hides)shows the widgets 
					 * that have been associated with the command.
					 * @param command  	the command
					 * @param flag 		true if the user can now see the
					 *			command (whether sensitive or not)
					 * @see			#registerCommandDependentWidget
					 * @see			#processCommand
					 * @implements		
					 *	MiiCommandManager#setCommandVisibility
					 *------------------------------------------------------*/
	public		void		setCommandVisibility(String command, boolean flag)
		{
		cmdWidgetManager.setCommandVisibility(command, flag);
		}
					/**------------------------------------------------------
					 * Sets whether the given command can be processed at this
					 * time. Otherwise, possibly based on the current state of
					 * the application, the given command cannot/should not be
					 * processed at this time. This method typically (de)sensitizes
					 * the widgets that have been associated with the command
					 * (see registerCommandDependentWidget).
					 * @param command  	the command
					 * @param flag 		true if the system can now process
					 *			the command
					 * @see			#registerCommandDependentWidget
					 * @see			#processCommand
					 * @implements		
					 *	MiiCommandManager#setCommandSensitivity
					 *------------------------------------------------------*/
	public		void		setCommandSensitivity(String command, boolean flag)
		{
		cmdWidgetManager.setCommandSensitivity(command, flag);
		}
					/**------------------------------------------------------
					 * Sets whether the given command can be processed at this
					 * time. Otherwise, possibly based on the current state of
					 * the application, the given command cannot/should not be
					 * processed at this time. This method typically (de)sensitizes
					 * the widgets that have been associated with the command
					 * (see registerCommandDependentWidget). This method is designed
					 * to display a status bar message that describes why a 
					 * particular widget is desensitized.
					 * @param command  	the command
					 * @param flag 		true if the system can now process
					 *			the command
					 * @param statusHelpMsg the new status bar message
					 * @see			#registerCommandDependentWidget
					 * @see			#processCommand
					 * @implements		
					 *	MiiCommandManager#setCommandSensitivity
					 *------------------------------------------------------*/
	public		void		setCommandSensitivity(String command, boolean flag, String statusHelpMsg)
		{
		cmdWidgetManager.setCommandSensitivity(command, flag, statusHelpMsg);
		}
					/**------------------------------------------------------
					 * Sets the state of the boolean widget that generates the 
					 * given command. This is used for initializing toggle 
					 * buttons.
					 * @param command  	the command
					 * @param flag 		true if the two state widget is
					 *			to be 'set'.
					 * @implements
					 *	MiiCommandManager#setCommandState
					 *------------------------------------------------------*/
	public		void		setCommandState(String command, boolean flag)
		{
		cmdWidgetManager.setCommandState(command, flag);
		}
					/**------------------------------------------------------
					 * Sets the state of the multi-state widget that generates
					 * the given command. This is used for initializing widgets 
					 * like combo boxes and lists.
					 * @param command  	the command
					 * @param state		the current state, one of many.
					 * @implements
					 *	MiiCommandManager#setCommandState
					 *------------------------------------------------------*/
	public		void		setCommandState(String command, String state)
		{
		cmdWidgetManager.setCommandState(command, state);
		}
					/**------------------------------------------------------
					 * Sets the label of the widget that generates the given 
					 * command. This is used for initializing widgets like
					 * labels and menubar buttons, push button etc.
					 * @param command  	the command
					 * @param label		the new label
					 * @implements
					 *	MiiCommandManager#setCommandLabel
					 *------------------------------------------------------*/
	public		void		setCommandLabel(String command, String label)
		{
		cmdWidgetManager.setCommandLabel(command, label);
		}
					/**------------------------------------------------------
					 * Sets the values of the multi-state widget that generates
					 * the given command. This is used for setting the possible
					 * values of widgets like combo boxes and lists.
					 * @param command  	the command
					 * @param options	the new contents of the widget
					 * @implements
					 *	MiiCommandManager#setCommandOptions
					 *------------------------------------------------------*/
	public		void		setCommandOptions(String command, Strings options)
		{
		cmdWidgetManager.setCommandOptions(command, options);
		}

					/**------------------------------------------------------
					 * Sets the given property to the given value for all parts
					 * that generates the given command. This is used for, say,
					 * changing the status help message on a widget that may
					 * only be situationally and temporarily desensitized.
					 * @param command  	the command
					 * @param propertyName	the property name
					 * @param propertyValue	the value
					 * @implements		MiiCommandManager#setCommandState
					 *------------------------------------------------------*/
	public		void		setCommandPropertyValue(String command, String propertyName, String propertyValue)
		{
		cmdWidgetManager.setCommandPropertyValue(command, propertyName, propertyValue);
		}


	}

class MiLayerEditability extends NamedEnumeratedType
	{
	public				MiLayerEditability(String name, int value)
		{
		super(name, value);
		}

	}

