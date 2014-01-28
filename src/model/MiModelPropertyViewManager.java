
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
import com.swfm.mica.util.FastVector; 
import com.swfm.mica.util.NamedEnumeratedType; 
import com.swfm.mica.util.Utility; 
import java.util.Enumeration; 
import java.util.Properties; 
import java.util.Hashtable; 
import java.util.Vector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
class MiViewManagerStyle
	{
	private		MiImage		icon;
	private		String		name;
	private		int		value;
	private		String		shortDescription;
	private		String		longDescription;
	private		boolean		handlesChildren;
	private		boolean		handlesGraphs;

	//public abstract	MiiViewManager	createViewManager();
	}

class MiPropertyViewManagerStyle extends NamedEnumeratedType
	{
					MiPropertyViewManagerStyle(String name, int type)
		{
		super(name, type);
		}
					MiPropertyViewManagerStyle(String name)
		{
		super(name);
		}
	}
public class MiModelPropertyViewManager extends MiModelEntity
					implements 	MiiViewManager, 
							MiiPropertyChangeHandler, 
							MiiActionHandler, 
							MiiActionTypes, 
							MiiCommandNames, 
							MiiPropertyTypes,
							MiiTypes
	{
	public final static String		Mi_HAS_CHANGED_PROPERTY = "hasChanged"; // GUI has changed, and will next apply change to model
	public final static String		Mi_MODEL_HAS_CHANGED_PROPERTY = "modelHasChanged";

	public final static MiPropertyViewManagerStyle	
						Mi_MAKE_SEPARATE_PROPERTY_PANEL_FOR_EACH_PROPERTY_CLASS_STYLE 
						= new MiPropertyViewManagerStyle(
						"makeSeparatePropertyPanelForEachPropertyClass");
	public final static MiPropertyViewManagerStyle	
						Mi_MAKE_PROPERTY_PANEL_FOR_TOP_PROPERTY_CLASS_ONLY_STYLE 
						= new MiPropertyViewManagerStyle(
						"makePropertyPanelForTopPropertyClassOnly");
	public final static MiPropertyViewManagerStyle	
						Mi_MAKE_PROPERTY_PANEL_FOR_ALL_PROPERTY_CLASSES_STYLE 
						= new MiPropertyViewManagerStyle(
						"makePropertyPanelForAllPropertyClassesStyle");

	private		MiPropertyViewManagerStyle	
					style = Mi_MAKE_PROPERTY_PANEL_FOR_TOP_PROPERTY_CLASS_ONLY_STYLE;
	private		MiDialogBoxTemplate	dialogBox;
	private		MiPart		view;
	private		MiiModelEntity	model;
	private		MiPropertyDescriptions	currentPropertyDescriptions;
	private		MiPart		mainPanel;
	private		boolean		applyEveryChange;
	private		boolean		applyEveryCharacterChange;
	private		boolean		generateUndoableTransactions;
	private		boolean		hasChanged;
	private		boolean		built;
	private		Strings		displayedProperties;
	private		Strings		displayOnlyTheseProperties;
	private		boolean		scrollablePanels;
	private		boolean		propertiesAreEditable		= true;
	private		int		numberOfPanelColumns;
	private		MiParts		propertyPanels			= new MiParts();
	private		Hashtable	propertyPanelCache		= new Hashtable(11);
	private		java.awt.Color	validationErrorColor		= MiColorManager.pink;
	private		boolean		currentlyUpdatingView;
	private		boolean		updateModelbyUserOnlyIfAllNewValuesValid	= true;


	public				MiModelPropertyViewManager(MiDialogBoxTemplate dialogBox, MiiModelEntity model)
		{
		this(model, 1, true, null);
		setView(dialogBox);
		}
	public				MiModelPropertyViewManager()
		{
		this(null, 1, true, null, Mi_MAKE_PROPERTY_PANEL_FOR_TOP_PROPERTY_CLASS_ONLY_STYLE);
		}
	public				MiModelPropertyViewManager(MiPropertyViewManagerStyle style)
		{
		this(null, 1, true, null, style);
		}
	public				MiModelPropertyViewManager(MiiModelEntity model)
		{
		this(model, 1, true, null);
		}
	public				MiModelPropertyViewManager(MiiModelEntity model, int numberOfPanelColumns)
		{
		this(model, numberOfPanelColumns, true, null);
		}
	public				MiModelPropertyViewManager(
							MiiModelEntity model, 
							int numberOfPanelColumns,
							boolean scrollablePanels,
							Strings displayOnlyTheseProperties)
		{
		this(model, numberOfPanelColumns, scrollablePanels, displayOnlyTheseProperties, 
			Mi_MAKE_PROPERTY_PANEL_FOR_TOP_PROPERTY_CLASS_ONLY_STYLE);
		}
	public				MiModelPropertyViewManager(
							MiiModelEntity model, 
							int numberOfPanelColumns,
							boolean scrollablePanels,
							Strings displayOnlyTheseProperties,
							MiPropertyViewManagerStyle style)
		{
		this.numberOfPanelColumns = numberOfPanelColumns;
		this.scrollablePanels = scrollablePanels;
		this.displayOnlyTheseProperties = displayOnlyTheseProperties;
		this.style = style;
		if (model != null)
			setModel(model);
		}
	public		void		setStyle(MiPropertyViewManagerStyle style)
		{
		this.style = style;
		built = false;
		if (model != null)
			setModel(model);
		}
	public		MiPropertyViewManagerStyle getStyle()
		{
		return(style);
		}
	public		void		setView(MiDialogBoxTemplate dialogBox)
		{
		if (this.dialogBox != null)
			this.dialogBox.removeActionHandlers(this);
		this.dialogBox = dialogBox;
		if (dialogBox != null)
			{
			dialogBox.appendActionHandler(this, MiDialogBoxTemplate.Mi_DIALOG_BOX_COMMAND_ACTION 
				| Mi_REQUEST_ACTION_PHASE | Mi_COMMIT_ACTION_PHASE);
			setView(dialogBox.getBox());
			}
		else
			{
			setView((MiPart )null);
			}
		}
	public		MiDialogBoxTemplate	getDialogBox()
		{
		return(dialogBox);
		}
	public		void		setVisible(boolean flag)
		{
		if (flag)
			{
			updateView();
			setHasChanged(false);
			}
		if (mainPanel != null)
			dialogBox.setVisible(flag);
		if (dialogBox != null)
			dialogBox.setVisible(flag);
		}
	public		void		setView(MiPart view)
		{
		if (this.view == view)
			return;

		if (this.view != null)
			{
			if (this.view.containsPart(mainPanel))
				this.view.removePart(mainPanel);
			this.view.removeActionHandlers(this);
			}
		this.view = view;
		if ((view != null) && (mainPanel != null))
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.setElementSizing(Mi_EXPAND_TO_FILL);
			columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			columnLayout.setUniqueElementIndex(0);
			view.setLayout(columnLayout);
			view.appendPart(mainPanel);
			}
		}
	public		MiPart		getView()
		{
		return(view);
		}
					// May be null if no model has been assigned
	public		MiPart		getGraphics()
		{
		return(mainPanel);
		}
	protected	void		build()
		{
		if (mainPanel != null)
			{
			if (view != null)
				view.removePart(mainPanel);
			mainPanel.removeActionHandlers(this);
			}
		mainPanel = buildMainPanel();
		mainPanel.appendActionHandler(this, Mi_PART_SHOWING_ACTION);
		buildLists();
		if (view != null)
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.setElementSizing(Mi_EXPAND_TO_FILL);
			columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			columnLayout.setUniqueElementIndex(0);
			view.setLayout(columnLayout);
			view.appendPart(mainPanel);
			}
		built = true;
		}
	protected	MiPart		buildMainPanel()
		{
		return(buildPanels(model));
		}
	protected	void		buildLists()
		{
		}
	protected	MiParts		getPropertyPanels()
		{
		return(propertyPanels);
		}
	protected	Strings		getDisplayedProperties()
		{
		return(displayedProperties);
		}
	protected	MiPart		getMainPanel()
		{
		return(mainPanel);
		}
	public		boolean		isBuilt()
		{
		return(built);
		}
	public		void		setIsBuilt(boolean flag)
		{
		built = flag;
		if (!flag)
			{
			propertyPanelCache.clear();
			}
		}
	// Note 1 column here counts as two actual table columns (1 for labels, one for values)
	public		void		setNumberOfPanelColumns(int numColumns)
		{
		numberOfPanelColumns = numColumns;
		}
	public		int		getNumberOfPanelColumns()
		{
		return(numberOfPanelColumns);
		}
	public		void		setUpdatingModelbyUserOnlyIfAllNewValuesValid(boolean flag)
		{
		updateModelbyUserOnlyIfAllNewValuesValid = flag;
		}
	public		boolean		isUpdatingModelbyUserOnlyIfAllNewValuesValid()
		{
		return(updateModelbyUserOnlyIfAllNewValuesValid);
		}
	public		void		setApplyingEveryCharacterChange(boolean flag)
		{
		applyEveryCharacterChange = flag;
		}
	public		boolean		isApplyingEveryCharacterChange()
		{
		return(applyEveryCharacterChange);
		}
	public		void		setApplyingEveryChange(boolean flag)
		{
		applyEveryChange = flag;
		}
	public		boolean		isApplyingEveryChange()
		{
		return(applyEveryChange);
		}
	public		void		setGenerateUndoableTransactions(boolean flag)
		{
		generateUndoableTransactions = flag;
		}
	public		boolean		getGenerateUndoableTransactions()
		{
		return(generateUndoableTransactions);
		}
	public		String		getFieldValue(String name)
		{
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			if (panel.hasProperty(name))
				return(panel.getPropertyValue(name));
			}
		return(null);
		}
	public		void		setFieldValue(String name, String value)
		{
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			if (panel.hasProperty(name))
				{
				panel.setPropertyValue(name, value);
				return;
				}
			}
		}
	public		MiiModelEntity	getInspectedModel()
		{
		return(model);
		}
	public		MiParts		getInspectedPanels()
		{
		return(propertyPanels);
		}
	public		void		updateView()
		{
		MiParts panels = getInspectedPanels();
		for (int i = 0; i < panels.size(); ++i)
			{
			updateView(getInspectedModel(), (MiPropertyPanel )panels.elementAt(i));
			}

		// Show any validation errors immediately
		validateModel();
		}
	protected	void		updateView(MiiModelEntity model, MiPropertyPanel panel)
		{
		MiPropertyDescriptions descs = model.getPropertyDescriptions();

		currentlyUpdatingView = true;

		panel.clearAllPropertyValidationErrors();
		for (int i = 0; i < displayedProperties.size(); ++i)
			{
			String name = displayedProperties.elementAt(i);
			String value = model.getPropertyValue(name);
			MiPropertyDescription desc = null;
			if (descs != null)
				{
				desc = descs.elementAt(name);
				if (desc != null)
					{
					value = desc.convertInternalValueToDisplayValue(value);
					}
				}
			if (value == null)
				{
				value = "";
				}
			else 
				{
				if (desc != null)
					{
					if ((desc != null) && (desc.getUnits() != null))
						{
						try	{
							value = desc.getUnits().
								convertInternalValueToDisplayUnits(value);
							}
						catch (Exception e)
							{
							panel.setPropertyValidationError(name, 
								e.getMessage(), validationErrorColor);
							}
						}
					}
				}

			try	{
				panel.setPropertyValue(name, value);
				}
			catch (Throwable t)
				{
				panel.setPropertyValidationError(name, 
					t.getMessage(), validationErrorColor);
				}
			}
		panel.copyWidgetValuesToRevertCache();
//		panel.clearAllPropertyValidationErrors();

		currentlyUpdatingView = false;
		}
	public		void		updateModel()
		{
		updateModel(model);
		}
	protected	void		updateModel(MiiModelEntity model)
		{
		if (currentlyUpdatingView)
			{
			return;
			}

		MiPropertyDescriptions descs = model.getPropertyDescriptions();

		MiParts propertyPanels = getInspectedPanels();
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			if (panel.hasChanged())
				{
				Strings names = panel.getChangedProperties();
				for (int j = 0; j < names.size(); ++j)
					{
					String name = names.elementAt(j);
					String value = panel.getPropertyValue(name);
					String oldValue = model.getPropertyValue(name);

					if (descs != null)
						{
						MiPropertyDescription desc = descs.elementAt(name);
						value = desc.convertDisplayValueToInternalValue(value);
						if ((desc != null) && (desc.getUnits() != null))
							{
							try	{
								value = desc.getUnits().
									convertDisplayValueToInternalUnits(value);
								}
							catch (Exception e)
								{
								panel.setPropertyValidationError(name, 
									e.getMessage(), validationErrorColor);
								}
							}
						}
					if (!Utility.equals(model.getPropertyValue(name), value))
						{
						MiiTransaction cmd = null;
						if (generateUndoableTransactions)
							{
							cmd = new MiModelPropertyChangeCommand(model, name, oldValue, value);
							cmd = MiSystem.getTransactionManager().startTransaction(cmd);
							}

						model.setPropertyValue(name, value);
						setPropertyValue(Mi_MODEL_HAS_CHANGED_PROPERTY, name);

						if (generateUndoableTransactions)
							{
							MiSystem.getTransactionManager().commitTransaction(cmd);
							}
						}
					}
				panel.copyWidgetValuesToRevertCache();
				panel.clearAllPropertyValidationErrors();
				}
			}
		setPropertyValue(Mi_MODEL_HAS_CHANGED_PROPERTY, null);
		setHasChanged(false);
		}
	protected	MiPart		buildPanels(MiiModelEntity model)
		{
		propertyPanels = new MiParts();
		// If we get panel from cache then we need to keep this list around 10-27-2003
		if (displayedProperties == null)
			{
			displayedProperties = new Strings();
			}

		return(MiModelPropertyViewManager.buildPanels(
							model, 
							displayOnlyTheseProperties, 
							propertyPanels, 
							displayedProperties,
							scrollablePanels, 
							numberOfPanelColumns, 
							propertyPanelCache,
							this,
							style));
		}
	public		void		setModel(MiiModelEntity model)
		{
//MiDebug.println(this + " setModel " + model);
		if (this.model != null)
			{
			this.model.removePropertyChangeHandler(this);
			}
		if (model == null)
			{
			this.model = model;
			return;
			}

//MiDebug.println(this + " setModel " + model);
//MiDebug.println("currentPropertyDescriptions=" + currentPropertyDescriptions);
//MiDebug.println("model.getPropertyDescriptions()=" + model.getPropertyDescriptions());
		if ((!isBuilt()) 
			|| ( // 2-5-2003 (this.model != null) &&
			(model.getPropertyDescriptions() != null)
			&& (model.getPropertyDescriptions() != currentPropertyDescriptions)
			&& ((currentPropertyDescriptions == null)
				|| (!model.getPropertyDescriptions().equals(currentPropertyDescriptions)))))
			{
			this.model = model;
//MiDebug.println("currentPropertyDescriptions=" + currentPropertyDescriptions);
//MiDebug.println("model.getPropertyDescriptions()=" + model.getPropertyDescriptions());
			currentPropertyDescriptions = model.getPropertyDescriptions();
			displayedProperties = new Strings(); // 5-1-2004 for widget browser switching between totally different models
//MiDebug.printStackTrace(this + "bbbbbbuuuild !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			build();
			}
		else
			{
			this.model = model;
			buildLists();
			}

		updateView();
		setHasChanged(false);
		model.appendPropertyChangeHandler(
			this, displayedProperties, MiiModelEntity.Mi_MODEL_CHANGE_COMMIT_PHASE.getMask());
		}
	public		MiiModelEntity	getModel()
		{
		return(model);
		}
	public		void		processPropertyChange(MiPropertyChange event)
		{
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			String name = event.getPropertyName();
			if (panel.hasProperty(name))
				{
				String value = event.getPropertyValue();
				MiPropertyDescriptions descs = event.getSource().getPropertyDescriptions();
				if (descs != null)
					{
					MiPropertyDescription desc = descs.elementAt(name);
					value = desc.convertInternalValueToDisplayValue(value);
					}
	
				panel.setPropertyValue(name, value);
				return;
				}
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_PART_SHOWING_ACTION))
			{
			updateView();
			setHasChanged(false);
			}
		else if (action.hasActionType(Mi_VALUE_CHANGED_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			if ((!currentlyUpdatingView) && (applyEveryChange || applyEveryCharacterChange) && (!validate()))
				{
				action.veto();
				return(false);
				}
			}
		else if (action.hasActionType(Mi_VALUE_CHANGED_ACTION))
			{
			setHasChanged(true);
			if ((!currentlyUpdatingView) && (applyEveryChange || applyEveryCharacterChange))
				{
				if (updateModelbyUserOnlyIfAllNewValuesValid)
					{ 
					if (validate())
						{
						updateModel();
						if (view != null)
							{
							view.dispatchAction(Mi_VALUE_CHANGED_ACTION, this);
							}
						}
					}
				else
					{
					updateModel();
					validate();
					if (view != null)
						{
						view.dispatchAction(Mi_VALUE_CHANGED_ACTION, this);
						}
					}
				}
			}
		else if (action.hasActionType(Mi_TEXT_CHANGE_ACTION))
			{
			setHasChanged(true);
			if ((!currentlyUpdatingView) && (applyEveryCharacterChange) && (validate()))
				updateModel();
			}
		else if (action.hasActionType(MiDialogBoxTemplate.Mi_DIALOG_BOX_COMMAND_ACTION + Mi_REQUEST_ACTION_PHASE))
			{
			String command = (String )action.getActionSystemInfo();
			if (command.equals(Mi_OK_COMMAND_NAME))
				{
				if (!validate())
					action.veto();
				}
			else if (command.equals(Mi_APPLY_COMMAND_NAME))
				{
				if (!validate())
					action.veto();
				}
			else if (command.equals(Mi_CANCEL_COMMAND_NAME))
				{
				if ((getHasChanged()) && (!verifyLossOfAnyChanges()))
					action.veto();
				else
					setHasChanged(false);
				}
			else if ((command.equals(Mi_HIDE_COMMAND_NAME))
				|| (command.equals(Mi_DESTROY_WINDOW_COMMAND_NAME)))
				{
				if ((getHasChanged()) && (!verifyLossOfAnyChanges()))
					action.veto();
				else
					setHasChanged(false);
				}
			return(!action.isVetoed());
			}
		if (action.hasActionType(MiDialogBoxTemplate.Mi_DIALOG_BOX_COMMAND_ACTION))
			{
			String command = (String )action.getActionSystemInfo();
			
			if (command.equals(Mi_OK_COMMAND_NAME))
				{
				updateModel();
				}
			else if (command.equals(Mi_APPLY_COMMAND_NAME))
				{
				updateModel();
				}
			else if (command.equals(Mi_UNDO_COMMAND_NAME))
				{
				undo();
				}
			else if (command.equals(Mi_DEFAULTS_COMMAND_NAME))
				{
				defaults();
				}
			return(true);
			}
/*
		else
			{
			return(dispatchAction(action));
			}
*/
		return(true);
		}
	protected	void		setHasChanged(boolean flag)
		{
		if (currentlyUpdatingView)
			{
			return;
			}

		hasChanged = flag;
		if (dialogBox != null)
			{
			dialogBox.setHasChanged(flag);
			}
		setPropertyValue(Mi_HAS_CHANGED_PROPERTY, "" + flag);
		}
	protected	boolean		getHasChanged()
		{
		return(hasChanged);
		}
	protected	void		undo()
		{
		MiParts propertyPanels = getPropertyPanels();
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			if (panel.hasChanged())
				panel.undo();
			}
		setHasChanged(false);
		}
	protected	void		defaults()
		{
		MiiModelEntity model = getModel();
		if (model == null)
			return;

		MiPropertyDescriptions descs = model.getPropertyDescriptions();

		for (int i = 0; i < descs.size(); ++i)
			{
			MiPropertyDescription desc = descs.elementAt(i);
			String name = desc.getName(); // 10-8-2002 was using getDisplayName, but it sometimes had embedded spaces which were not registered with panel with spaces
			String defaultValue = desc.getDefaultValue();
			for (int j = 0; j < propertyPanels.size(); ++j)
				{
				MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(j);
				if (panel.hasProperty(name))
					{
					String value = panel.getPropertyValue(name);
defaultValue = desc.convertInternalValueToDisplayValue(defaultValue);
					if (!Utility.isEqualTo(value, defaultValue))
						{
						panel.setUndoablePropertyValue(name, defaultValue);
						setHasChanged(true);
						}
					}
				}
			}
		}
	protected	boolean		validateModel()
		{
		int numErrors = 0;
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			panel.clearAllPropertyValidationErrors();
			}
		for (int i = 0; i < displayedProperties.size(); ++i)
			{
			String name = displayedProperties.elementAt(i);
			String value = model.getPropertyValue(name);
			MiValueValidationError error = model.validatePropertyValue(name, value);
			if (error != null)
				{
				++numErrors;
				for (int j = 0; j < propertyPanels.size(); ++j)
					{
					MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(j);
					if (panel.hasProperty(name))
						{
						panel.setPropertyValidationError(name, 
							error.getShortDescription(), validationErrorColor);
						break;
						}
					}
				}
			}
		return(numErrors == 0);
		}
	protected	boolean		validate()
		{
		int numErrors = 0;
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			panel.clearAllPropertyValidationErrors();
			}
		for (int i = 0; i < displayedProperties.size(); ++i)
			{
			String name = displayedProperties.elementAt(i);
			String value = getFieldValue(name);
			MiPropertyDescription desc = model.getPropertyDescriptions().elementAt(name);
			if (desc != null)
				{
				value = desc.convertDisplayValueToInternalValue(value);
				}
			MiValueValidationError error = model.validatePropertyValue(name, value);
			if (error != null)
				{
				if (error.getRestoreStateToPreviousValidStateIfPossible())
					{
					setFieldValue(name, model.getPropertyValue(name));
					}
				else
					{
					++numErrors;
					for (int j = 0; j < propertyPanels.size(); ++j)
						{
						MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(j);
						if (panel.hasProperty(name))
							{
							panel.setPropertyValidationError(name, 
								error.getShortDescription(), validationErrorColor);
							break;
							}
						}
					}
				}
			}
		return(numErrors == 0);
		}
	public		boolean		hasValidationErrors()
		{
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			if (panel.hasValidationErrors())
				{
				return(true);
				}
			}
		return(false);
		}
		
	public		boolean		verifyLossOfAnyChanges()
		{
		if ((view != null) && (getHasChanged()))
			{
			String result = MiToolkit.postSaveChangesDialog(
							view.getContainingEditor(), model.getName());
			if (result.equals(Mi_SAVE_COMMAND_NAME))
				{
				updateModel();
				}
			else if (result.equals(Mi_CANCEL_COMMAND_NAME))
				{
				return(false);
				}
			}
		return(true);
		}
	protected	void		copyWidgetValuesToRevertCache()
		{
		MiParts propertyPanels = getPropertyPanels();
		for (int i = 0; i < propertyPanels.size(); ++i)
			{
			MiPropertyPanel panel = (MiPropertyPanel )propertyPanels.elementAt(i);
			panel.copyWidgetValuesToRevertCache();
			}
		}


	public static	MiPart		buildPanels(
						MiiModelEntity model, 
						Strings displayOnlyTheseProperties, 
						MiParts propertyPanels,
						Strings displayedProperties,
						boolean scrollable,
						int numberOfColumns,
						Hashtable propertyPanelCache,
						MiiActionHandler widgetValueChangeHandler,
						MiPropertyViewManagerStyle style)
		{
		MiPropertyPanel panel;
		MiPropertyWidgets propertyWidgets;
		MiPropertyDescriptions descs = MiModelPropertyViewManager.getPropertyDescriptions(model);
		if (descs != null)
			{
			if ((descs.getNumberOfPropertyDescriptionComponents() == 0)
				|| (style == Mi_MAKE_PROPERTY_PANEL_FOR_ALL_PROPERTY_CLASSES_STYLE))
				{
				panel = MiModelPropertyViewManager.buildPanel(
						descs,
						displayOnlyTheseProperties,
						displayedProperties,
						scrollable,
						numberOfColumns,
						propertyPanelCache,
						widgetValueChangeHandler);
				propertyPanels.addElement(panel);
				return(panel);
				}
			else // if (descs.getNumberOfPropertyDescriptionComponents() > 0)
				{
				if (style == Mi_MAKE_SEPARATE_PROPERTY_PANEL_FOR_EACH_PROPERTY_CLASS_STYLE)
					{
					MiTabbedFolder folder = new MiTabbedFolder();
					for (int i = 0; 
						i < descs.getNumberOfPropertyDescriptionComponents(); ++i)
						{
						MiPropertyDescriptions componentDescs
							= descs.getPropertyDescriptionComponent(i);
	
						panel = MiModelPropertyViewManager.buildPanel(
								componentDescs,
								displayOnlyTheseProperties,
								displayedProperties,
								scrollable,
								numberOfColumns,
								propertyPanelCache,
								widgetValueChangeHandler);
						propertyPanels.addElement(panel);
						folder.appendFolder(componentDescs.getDisplayName(), panel);
						}
					return(folder);
					}
				if (style == Mi_MAKE_PROPERTY_PANEL_FOR_TOP_PROPERTY_CLASS_ONLY_STYLE)
					{
					descs = descs.getPropertyDescriptionComponent(0);
					}
				panel = MiModelPropertyViewManager.buildPanel(
						descs,
						displayOnlyTheseProperties,
						displayedProperties,
						scrollable,
						numberOfColumns,
						propertyPanelCache,
						widgetValueChangeHandler);
				propertyPanels.addElement(panel);
				return(panel);
				}
			}
		panel = (MiPropertyPanel )propertyPanelCache.get(model);
		if (panel != null)
			{
			propertyPanels.addElement(panel);
			return(panel);
			}

		displayedProperties.clear(); // moved here 10-27-2003

		Properties properties = MiModelPropertyViewManager.getPropertyValues(
						model, displayOnlyTheseProperties, displayedProperties);

		propertyWidgets = MiModelPropertyViewManager.makePropertyWidgets(
					properties, widgetValueChangeHandler);
		panel = new MiBasicPropertyPanel(scrollable, numberOfColumns);
		panel.setPropertyWidgets(propertyWidgets);
		panel.open();
		propertyPanelCache.put(model, panel);
		propertyPanels.addElement(panel);
		return(panel);
		}
	public static	MiPropertyPanel		buildPanel(
							MiPropertyDescriptions descs,
							Strings displayOnlyTheseProperties,
							Strings displayedProperties,
							boolean scrollable,
							int numberOfColumns,
							Hashtable propertyPanelCache,
							MiiActionHandler widgetValueChangeHandler)
		{
		MiPropertyPanel panel = (MiPropertyPanel )propertyPanelCache.get(descs);
		if (panel != null)
			return(panel);

		displayedProperties.clear(); // moved here 10-27-2003
		MiPropertyWidgets propertyWidgets = MiModelPropertyViewManager.makePropertyWidgets(
							descs, 
							displayOnlyTheseProperties, 
							widgetValueChangeHandler, 
							displayedProperties);
		panel = new MiBasicPropertyPanel(scrollable, numberOfColumns);
		panel.setPropertyWidgets(propertyWidgets);
		panel.open();
		propertyPanelCache.put(descs, panel);
		return(panel);
		}

	public static	MiPropertyDescriptions	getPropertyDescriptions(MiiModelEntity model)
		{
		if (model.getPropertyDescriptions() == null)
			return(null);

		MiPropertyDescriptions properties 
			= model.getPropertyDescriptions().getPropertyDescriptionsForClass(
			model.getClass().getName());

		// No properties specific to the given class, jsut use general properties
		if (properties == null)
			properties = model.getPropertyDescriptions();
		return(properties);
		}
	public static	Properties		getPropertyValues(MiiModelEntity model,
							Strings lookForTheseProperties,
							Strings propertiesReturned)
		{
		Properties properties = new Properties();
		Strings names = lookForTheseProperties;
		if (names == null)
			names = model.getPropertyNames();
		for (int i = 0; i < names.size(); ++i)
			{
			String name = names.elementAt(i);
			properties.put(name, model.getPropertyValue(name));
			propertiesReturned.addElement(name);
			}
		return(properties);
		}
	public static	MiPropertyWidgets	makePropertyWidgets(
							Properties properties, 
							MiiActionHandler widgetValueChangeHandler)
		{
		MiPropertyWidgets	panelWidgets	= new MiPropertyWidgets();

		for (Enumeration e = properties.keys(); e.hasMoreElements();)
			{
			String name = (String)e.nextElement();
			String value = properties.getProperty(name);

			MiWidget widget = new MiTextField();
			widget.setValue(value);

			panelWidgets.appendPropertyWidget(new MiPropertyWidget(name, widget));
			widget.appendActionHandler(widgetValueChangeHandler, Mi_VALUE_CHANGED_ACTION 
				| Mi_REQUEST_ACTION_PHASE | Mi_COMMIT_ACTION_PHASE);
			widget.appendActionHandler(widgetValueChangeHandler, Mi_TEXT_CHANGE_ACTION);
			}
		return(panelWidgets);
		}
	public static	MiPropertyWidgets	makePropertyWidgets(
							MiPropertyDescriptions descs, 
							Strings displayOnlyTheseProperties, 
							MiiActionHandler widgetValueChangeHandler,
							Strings displayedProperties)
		{
		MiPropertyWidgets	panelWidgets	= new MiPropertyWidgets();

		for (int i = 0; i < descs.size(); ++i)
			{
			MiPropertyDescription desc = descs.elementAt(i);
			if (displayOnlyTheseProperties != null)
				{
				if (!displayOnlyTheseProperties.contains(desc.getName()))
					continue;
				}
			if (!desc.isViewable(null))
				{
				continue;
				}
			
			String name = desc.getDisplayName();
			int valueType = desc.getType();
			String defaultValue = desc.getDefaultValue();
/*
MiDebug.println("display values for: " + name + "=" + desc.getValidDisplayValues());
if (desc.getValidDisplayValues() != null)
MiDebug.println("default-value=" + defaultValue);
*/
			defaultValue = desc.convertInternalValueToDisplayValue(defaultValue);
//if (desc.getValidDisplayValues() != null)
//MiDebug.println("default-value now =" + defaultValue);

			int numberOfValidValues = desc.getNumberOfValidValues();
			MiWidget widget = null;

			switch (valueType)
				{
				case Mi_STRING_TYPE :
				case Mi_INTEGER_TYPE :
				case Mi_DOUBLE_TYPE :
				case Mi_POSITIVE_INTEGER_TYPE :
				case Mi_POSITIVE_DOUBLE_TYPE :
					if ((numberOfValidValues <= 0) || (numberOfValidValues >= 1000))
						{
						if (valueType == Mi_STRING_TYPE)
							{
							if (desc.isEditable(null))
								{
								widget = new MiTextField();
								((MiTextField )widget).setNumDisplayedColumns(
									MiTextField.Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT);
								}
							else
								{
								widget = new MiLabel();
								MiLayout layout = new MiRowLayout();
								layout.setElementHJustification(Mi_LEFT_JUSTIFIED);
								layout.setElementVSizing(Mi_NONE);
								widget.setLayout(layout);
								widget.setVisibleContainerAutomaticLayoutEnabled(false);
								}
							}
						else
							{
							MiSpinBox spinBox = new MiSpinBox();
							widget = spinBox;
							switch (valueType)
							    {
							    case Mi_INTEGER_TYPE :
								spinBox.setRange(
										Integer.MIN_VALUE,
										Integer.MAX_VALUE,
										1);
								break;
							    case Mi_POSITIVE_INTEGER_TYPE :
								spinBox.setRange(
										0,
										Integer.MAX_VALUE,
										1);
								break;
							    case Mi_DOUBLE_TYPE :
								spinBox.setRange(
										Double.MIN_VALUE,
										Double.MAX_VALUE,
										1.0);
								break;
							    case Mi_POSITIVE_DOUBLE_TYPE :
								spinBox.setRange(
										0,
										Double.MAX_VALUE,
										1.0);
								break;
								}
							if (desc.getMinimumValueIsSpecified())
								spinBox.setMinimumValue(desc.getMinimumValue());
							if (desc.getMaximumValueIsSpecified())
								spinBox.setMaximumValue(desc.getMaximumValue());
							if (desc.getStepValueIsSpecified())
								spinBox.setStepValue(desc.getStepValue());
							}
						}
					else if (numberOfValidValues == 1)
						{
						if ((desc.isEditable(null)) && (desc.getValidValuesAreSuggestionsOnly()))
							{
							widget = new MiTextField();
							((MiTextField )widget).setNumDisplayedColumns(
								MiTextField.Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT);
							}
						else
							{
							widget = new MiLabel();
							widget.setElementHJustification(Mi_LEFT_JUSTIFIED);
							}
						}
					else if ((numberOfValidValues < 8) 
						&& (!desc.getValidValuesAreSpecialCasesOnly())
						&& (!desc.getValidValuesAreSuggestionsOnly()))
						{
						widget = new MiOptionMenu();
						if (desc.getValidDisplayValues() != null)
							{
							widget.setContents(desc.getValidDisplayValues());
							}
						else
							{
							widget.setContents(desc.getValidValues());
							}
						}
					else // if (numberOfValidValues < 50)
						{
						widget = new MiComboBox();
						if ((numberOfValidValues > 50) && (numberOfValidValues <= 300))
							{
							((MiComboBox )widget).getList().setPreferredNumberOfVisibleRows(20);
							}
						else if (numberOfValidValues > 300)
							{
							((MiComboBox )widget).getList().setPreferredNumberOfVisibleRows(40);
							}
						((MiComboBox )widget).getList().getSortManager().setEnabled(false);
						((MiComboBox )widget).setRestrictingValuesToThoseInList(
							(!desc.getValidValuesAreSuggestionsOnly()) 
							&& (!desc.getValidValuesAreSpecialCasesOnly()));
						if (desc.getValidDisplayValues() != null)
							{
							widget.setContents(desc.getValidDisplayValues());
							}
						else
							{
							widget.setContents(desc.getValidValues());
							}
						}
					widget.setValue(defaultValue);
					break;
				case Mi_BOOLEAN_TYPE :
					//widget = new MiYesNoToggleButton();
					//widget = new MiLabeledWidget(new MiToggleButton(), "");
					widget = new MiLabeledWidget(new MiCheckBox(), "");
					widget.setValue(defaultValue);
					((MiRowLayout )widget.getLayout()).setElementHJustification(Mi_LEFT_JUSTIFIED);
					break;
				case Mi_COLOR_TYPE :
					widget = new MiColorOptionMenu();
					widget.setValue(defaultValue);
					break;
				case Mi_FONT_NAME_TYPE :
					widget = new MiFontOptionMenu();
					widget.setValue(defaultValue);
					break;
				case Mi_OBJECT_TYPE :
					break;
				case Mi_FILE_NAME_TYPE :
					//widget = new MiFileBrowseButton();
					widget = new MiTextField();
					((MiTextField )widget).setNumDisplayedColumns(
						MiTextField.Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT);
					widget.setValue(defaultValue);
					break;
				case Mi_TEXT_TYPE :
					if (desc.isEditable(null)) // && (desc.getValidValuesAreSuggestionsOnly()))
						{
						widget = new MiTextField();
						((MiTextField )widget).setNumDisplayedColumns(
							MiTextField.Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT);
						}
					else
						{
						widget = new MiLabel();
						widget.setElementHJustification(Mi_LEFT_JUSTIFIED);
						}
					widget.setValue(defaultValue);
					break;
				}

			if (widget != null)
				{
				MiPropertyWidget propertyWidget = new MiPropertyWidget(desc.getName(), widget); // display name should not appear in modelPropertyChangeEvent 5-13-01
				propertyWidget.setDisplayName(desc.getDisplayName());
				panelWidgets.appendPropertyWidget(propertyWidget);
				widget.appendActionHandler(widgetValueChangeHandler, Mi_VALUE_CHANGED_ACTION 
					| Mi_REQUEST_ACTION_PHASE | Mi_COMMIT_ACTION_PHASE);
				widget.appendActionHandler(widgetValueChangeHandler, Mi_TEXT_CHANGE_ACTION);

				widget.setToolHintHelp(desc.getToolHintHelp());
				widget.setStatusHelp(desc.getStatusHelp() != null 
					? desc.getStatusHelp() : MiHelpInfo.noneForThis);
				widget.setDialogHelp(desc.getDialogHelp());
//MiDebug.println("===============adesc.isEditable(null) = " + desc.isEditable(null));
				if ((!(widget instanceof MiLabel)) || (widget instanceof MiOptionMenu))
					{
					widget.setSensitive(desc.isEditable(null));
//MiDebug.println("===============set Sensitive -------------------");
					}

				// 5-13-01 displayedProperties.addElement(desc.getDisplayName());
//MiDebug.println("displayedProperties.addElement: " + desc.getName());
				displayedProperties.addElement(desc.getName());
				}
			}
		return(panelWidgets);
		}
	}

//------------------------------------------------------------------
//------------------------------------------------------------------

interface MiiModelEntityCategorizingFilter
	{
	MiiModelEntityGroup	getCategorizedGroups(MiiModelEntity document);
	}
/*
class MiiModelEntityHierarchicalCollection
class MiiModelEntityHierarchicalGroup
class MiiModelEntityCategory
*/
interface MiiModelEntityGroup
	{
	String			getName();
	MiModelEntityList	getModelEntities();
	MiiModelEntityGroup[]	getSubGroups();
	int			sizeOfTree();
	boolean			mayContainEntities(); // !isOnlyANode
	}
class MiModelEntityGroup implements MiiModelEntityGroup
	{
	private		String			name;
	private		MiModelEntityList	list;
	private		MiiModelEntityGroup[]	children;
	private		boolean			mayContainEntities;


	public		void			setName(String name)
		{
		this.name = name;
		}
	public		String			getName()
		{
		return(name);
		}
	public		void			setModelEntities(MiModelEntityList list)
		{
		this.list = list;
		}
	public		MiModelEntityList	getModelEntities()
		{
		return(list);
		}
	public		void			setSubGroups(MiiModelEntityGroup[] subGroups)
		{
		this.children = subGroups;
		}
	public		MiiModelEntityGroup[]	getSubGroups()
		{
		return(children);
		}
	public		int			sizeOfTree()
		{
		if (children == null)
			return(0);

		int num = children.length;
		for (int i = 0; i < children.length; ++i)
			num += children[i].sizeOfTree();

		return(num);
		}
	public		void			setMayContainEntities(boolean flag)
		{
		mayContainEntities = flag;
		}
	public		boolean			mayContainEntities()
		{
		return(mayContainEntities);
		}
	}
class MiModelEntityPropertyValueCategorizingFilter implements MiiModelEntityCategorizingFilter
	{
	private		String		propertyName;
	private		Strings		propertyValues;

	public				MiModelEntityPropertyValueCategorizingFilter(
						String propertyName, Strings propertyValues)
		{
		}
	public		MiiModelEntityGroup	getCategorizedGroups(MiiModelEntity document)
		{
		MiModelEntityGroup[] results = new MiModelEntityGroup[propertyValues.size()];
		for (int i = 0; i < results.length; ++i)
			{
			results[i] = new MiModelEntityGroup();
			}
		MiModelEntityList entities = document.getModelEntities();
		for (int i = 0; i < propertyValues.size(); ++i)
			{
			MiModelEntityList list = new MiModelEntityList();
			String propertyValue = propertyValues.elementAt(i);
			for (int j = 0; j < entities.size(); ++j)
				{
				MiiModelEntity entity = entities.elementAt(j);
				String entityValue = entity.getPropertyValue(propertyName);
				if (Utility.isEqualTo(entityValue, propertyValue))
					{
					list.addElement(entity);
					break;
					}
				}
			results[i].setModelEntities(list);
			}
		MiModelEntityGroup result = new MiModelEntityGroup();
		result.setSubGroups(results);
		return(result);
		}
	}
class MiModelPropertyTableViewManager extends MiModelPropertyViewManager implements MiiModelChangeHandler
	{
	public final static MiPropertyViewManagerStyle	Mi_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE 
							= new MiPropertyViewManagerStyle(
							"filterChildrenUsingTabbedFolder");
	public final static MiPropertyViewManagerStyle	Mi_PARENT_PLUS_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE
							= new MiPropertyViewManagerStyle(
							"parentPlusFilterChildrenUsingTabbedFolder");
	public final static MiPropertyViewManagerStyle	Mi_FILTER_CHILDREN_USING_RADIO_BUTTONS_STYLE
							= new MiPropertyViewManagerStyle(
							"filterChildrenUsingRadioButtons");
	public final static MiPropertyViewManagerStyle	Mi_PARENT_PLUS_FILTER_CHILDREN_USING_RADIO_BUTTONS_STYLE
							= new MiPropertyViewManagerStyle(
							"parentPlusFilterChildrenUsingRadioButtons");
	public final static MiPropertyViewManagerStyle	Mi_UNFILTERED_CHILDREN_STYLE
							= new MiPropertyViewManagerStyle(
							"unfilteredChildren");
	public final static MiPropertyViewManagerStyle	Mi_PARENT_PLUS_UNFILTERED_CHILDREN_STYLE
							= new MiPropertyViewManagerStyle(
							"parentPlusUnfilteredChildren");

	private		MiPropertyViewManagerStyle	style = Mi_PARENT_PLUS_UNFILTERED_CHILDREN_STYLE;
	private		boolean			listIsEditable;
	private		MiiModelEntityCategorizingFilter categorizationFilter;
	private		Strings			entityCategoryNames;
	private		MiTabbedFolder		tabbedFolder;
	private		MiWidget[]		filterButtons;
	private		MiPart			mainPanel;
	private		int			openCategoryIndex;
	private		MiModelEntityList[] 	listedEntities;
	private		Strings[] 		listedEntitiesNames;
	private		MiTablePropertyPanel[]	tables;
	private		MiPropertyWidgets[]	propertyWidgets;
	private		Strings[]		displayedProperties;
	private		Strings[]		displayOnlyTheseProperties;
	private		MiPart			parentPanel;
	private		MiPart			inspectedCategoryPanel;
	private		MiiModelEntity		inspectedModel;
	private		MiModelEntityList	changedEntities		= new MiModelEntityList();


	public				MiModelPropertyTableViewManager()
		{
		this(Mi_PARENT_PLUS_UNFILTERED_CHILDREN_STYLE);
		}

	public				MiModelPropertyTableViewManager(MiPropertyViewManagerStyle style)
		{
		super(Mi_MAKE_PROPERTY_PANEL_FOR_TOP_PROPERTY_CLASS_ONLY_STYLE);
		this.style = style;
		build();
		}

	protected	MiPart		buildPanels()
		{
		MiPart mainPanel = new MiContainer();
		MiiModelEntity model = getModel();

		if ((style == Mi_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE) 
			|| (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE))
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			mainPanel.setLayout(columnLayout);
			if (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE)
				{
				parentPanel = buildPanels(model);
				mainPanel.appendPart(parentPanel);
				}
			tabbedFolder = new MiTabbedFolder();
			tables = new MiTablePropertyPanel[entityCategoryNames.size()];
			for (int i = 0; i < entityCategoryNames.size(); ++i)
				{
				MiPart folder;
				folder = tabbedFolder.appendFolder(entityCategoryNames.elementAt(i));
				MiColumnLayout colLayout = new MiColumnLayout();
				folder.setLayout(colLayout);
				tables[i] = buildTable(listedEntities[i], i);
				folder.appendPart(tables[i]);
				}
			mainPanel.appendPart(tabbedFolder);
			}
		else if ((style == Mi_FILTER_CHILDREN_USING_RADIO_BUTTONS_STYLE)
			|| (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_RADIO_BUTTONS_STYLE))
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			mainPanel.setLayout(columnLayout);
			if (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_RADIO_BUTTONS_STYLE)
				{
				parentPanel = buildPanels(model);
				mainPanel.appendPart(parentPanel);
				}
			MiRowLayout rowLayout = new MiRowLayout();
			mainPanel.appendPart(rowLayout);
			MiColumnLayout buttonColumnLayout = new MiColumnLayout();
			rowLayout.appendPart(buttonColumnLayout);
			MiColumnLayout tableColumnLayout = new MiColumnLayout();
			rowLayout.appendPart(tableColumnLayout);
			MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
			for (int i = 0; i < entityCategoryNames.size(); ++i)
				{
				filterButtons[i] = new MiToggleButton();
				buttonColumnLayout.appendPart(filterButtons[i]);
				filterButtons[i].appendActionHandler(this, Mi_SELECTED_ACTION);
				filterButtons[i].setRadioStateEnforcer(radioEnforcer);
				tables[i] = buildTable(listedEntities[i], i);
				if (i > 0)
					tables[i].setVisible(false);
				tableColumnLayout.appendPart(tables[i]);
				}
			}
		else if ((style == Mi_UNFILTERED_CHILDREN_STYLE)
			|| (style == Mi_PARENT_PLUS_UNFILTERED_CHILDREN_STYLE))
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			mainPanel.setLayout(columnLayout);
			if (style == Mi_PARENT_PLUS_UNFILTERED_CHILDREN_STYLE)
				{
				parentPanel = buildPanels(model);
				mainPanel.appendPart(parentPanel);
				}
			tables[0] = buildTable(listedEntities[0], 0);
			mainPanel.appendPart(tables[0]);
			}

		return(mainPanel);
		}
	public		MiTablePropertyPanel	buildTable(MiModelEntityList modelList, int categoryIndex)
		{
		MiiModelEntity model = modelList.elementAt(0);
		Strings displayOnlyTheseProperties = this.displayOnlyTheseProperties[categoryIndex];
		if (displayOnlyTheseProperties == null)
			displayOnlyTheseProperties = model.getPropertyNames();
		
		displayedProperties[categoryIndex] = new Strings();

		MiPropertyDescriptions descs = model.getPropertyDescriptions();
		if (descs != null)
			{
			propertyWidgets[categoryIndex] = makePropertyWidgets(
								descs, 
								displayOnlyTheseProperties, 
								this,
								displayedProperties[categoryIndex]);
			}
		else
			{
			propertyWidgets[categoryIndex] = null;
			}
		MiTablePropertyPanel tablePanel = new MiTablePropertyPanel();
		tablePanel.setTableHeaders(displayedProperties[categoryIndex]);
		return(tablePanel);
		}
	public		void		setEntityCategorizationFilter(
						MiiModelEntityCategorizingFilter categorizationFilter)
		{
		this.categorizationFilter = categorizationFilter;
/*
		if ((style == Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_COMBO_BOX_STYLE)
			|| (style == Mi_FILTER_USING_TABBED_FOLDER_LIST_USING_SCROLLED_LIST_STYLE))
			{
			setIsBuilt(false);
			}
*/
		propertyWidgets = new MiPropertyWidgets[entityCategoryNames.size()];
		displayedProperties = new Strings[entityCategoryNames.size()];
		displayOnlyTheseProperties = new Strings[entityCategoryNames.size()];
		if (getModel() != null)
			setModel(getModel());
		}
	public		void		setEntityFilterCategoryProperties(Vector categoriesPropertyValues)
		{
		for (int i = 0; i < categoriesPropertyValues.size(); ++i)
			{
			Strings propertyValues = (Strings )categoriesPropertyValues.elementAt(i);
			displayOnlyTheseProperties[i] = propertyValues;
			}
		}
	public		void		updateModel(MiiModelEntity model)
		{
		super.updateModel(inspectedModel);
		}
	public		void		updateModel()
		{
		for (int i = 0; i < listedEntities.length; ++i)
			{
			MiModelEntityList entities = listedEntities[i];
			for (int j = 0; j < entities.size(); ++j)
				{
				if (changedEntities.indexOf(entities.elementAt(j)) != -1)
					{
					updateModel(entities.elementAt(j));
					}
				}
			}
		}
	public		void		updateView(MiiModelEntity model)
		{
		for (int i = 0; i < listedEntities.length; ++i)
			{
			for (int j = 0; j < listedEntities[i].size(); ++j)
				{
				if (listedEntities[i].elementAt(j) == model)
					{
					for (int k = 0; k < displayedProperties[i].size(); ++k)
						{
						String name = displayedProperties[i].elementAt(k);
						tables[i].getTable().getCell(j, k).setValue
							(model.getPropertyValue(name));
						}
					return;
					}
				}
			}
		}
	protected	void		buildListsContents()
		{
		if (categorizationFilter != null)
			{
			MiiModelEntityGroup group = categorizationFilter.getCategorizedGroups(getModel());
			int sizeOfTree = group.sizeOfTree();
			listedEntities = new MiModelEntityList[sizeOfTree];
			listedEntitiesNames = new Strings[sizeOfTree];
			populateListContents(listedEntities, listedEntitiesNames, group, 0);
			}
		else
			{
			MiModelEntityList modelEntities = getModel().getModelEntities();
			listedEntities = new MiModelEntityList[1];
			listedEntitiesNames = new Strings[1];
			listedEntities[0] = modelEntities;
			Strings contents = new Strings();
			for (int j = 0; j < modelEntities.size(); ++j)
				{
				contents.addElement(modelEntities.elementAt(j).getName());
				}
			listedEntitiesNames[0] = contents;
			}
		}

	private		int		populateListContents(
						MiModelEntityList[] listedEntities, 
						Strings[] listedEntitiesNames, 
						MiiModelEntityGroup group,
						int listContentsIndex)
		{
		MiiModelEntityGroup[] groups = group.getSubGroups();
		for (int i = 0; i < groups.length; ++i)
			{
			MiModelEntityList list = groups[i].getModelEntities();
			listedEntities[listContentsIndex] = new MiModelEntityList();
			Strings contents = new Strings();
			for (int j = 0; j < list.size(); ++j)
				{
				MiiModelEntity entity = list.elementAt(j);
				if (entity.getType() != Mi_COMMENT_TYPE)
					{
					contents.addElement(entity.getName());
					listedEntities[listContentsIndex].addElement(entity);
					}
				}
			listedEntitiesNames[listContentsIndex] = contents;
			++listContentsIndex;
			if (groups[i].getSubGroups() != null)
				{
				listContentsIndex = populateListContents(
							listedEntities, 
							listedEntitiesNames, 
							groups[i], 
							listContentsIndex);
				}
			}
		return(listContentsIndex);
		}
	protected	void		buildLists()
		{
		MiiModelEntity model = getModel();
		buildListsContents();

		if ((style == Mi_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE) 
			|| (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE))
			{
			tables[0].removeAllItems();
			for (int i = 0; i < listedEntities[0].size(); ++i)
				updateView(listedEntities[0].elementAt(i));

			if (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE) 
				{
				MiPart newPanel = buildPanels(model);
				if (newPanel != parentPanel)
					{
					parentPanel.replaceSelf(newPanel);
					parentPanel = newPanel;
					}
				updateView(model);
				}
			}
		else if ((style == Mi_FILTER_CHILDREN_USING_RADIO_BUTTONS_STYLE)
			|| (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_RADIO_BUTTONS_STYLE))
			{
			for (int i = 0; i < tables.length; ++i)
				{
				tables[i].removeAllItems();
				for (int j = 0; j < listedEntities[i].size(); ++j)
					updateView(listedEntities[i].elementAt(j));
				}

			if (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_RADIO_BUTTONS_STYLE) 
				{
				MiPart newPanel = buildPanels(model);
				if (newPanel != parentPanel)
					{
					parentPanel.replaceSelf(newPanel);
					parentPanel = newPanel;
					}
				updateView(model);
				}
			}
		else if ((style == Mi_UNFILTERED_CHILDREN_STYLE)
			|| (style == Mi_PARENT_PLUS_UNFILTERED_CHILDREN_STYLE))
			{
			tables[0].removeAllItems();
			for (int i = 0; i < listedEntities[0].size(); ++i)
				updateView(listedEntities[0].elementAt(i));

			if (style == Mi_PARENT_PLUS_FILTER_CHILDREN_USING_TABBED_FOLDER_STYLE) 
				{
				MiPart newPanel = buildPanels(model);
				if (newPanel != parentPanel)
					{
					parentPanel.replaceSelf(newPanel);
					parentPanel = newPanel;
					}
				updateView(model);
				}
			}
		}

	public		boolean		processAction(MiiAction action)
		{
		for (int i = 0; i < filterButtons.length; ++i)
			{
			if (action.getActionSource() == filterButtons[i])
				{
				displayCategory(i);
				return(true);
				}
			}
		if (action.getActionSource() == tabbedFolder)
			{
			int index = tabbedFolder.getOpenFolderIndex();
			displayCategory(index);
			return(true);
			}
/***
		if (action.getActionSource() == list)
			{
			setInspectedModel(listedEntities[0].elementAt(list.getSelectedIndex()));
			return(true);
			}
***/
		return(super.processAction(action));
		}
	protected	void		displayCategory(int categoryIndex)
		{
		if (tabbedFolder != null)
			{
			tabbedFolder.setOpenFolder(categoryIndex);
			}
		else
			{
			tables[openCategoryIndex].setVisible(true);
			tables[categoryIndex].setVisible(true);
			}
		openCategoryIndex = categoryIndex;
		}
	public		void		processPropertyChange(MiPropertyChange event)
		{
		MiiModelEntity entity = event.getSource();
		changedEntities.addElement(entity);
		if (entity == getModel())
			{
			MiParts propertyPanels = getPropertyPanels();
			for (int i = 0; i < propertyPanels.size(); ++i)
				{
				((MiPropertyPanel )propertyPanels.elementAt(i)).setPropertyValue(
					event.getPropertyName(), event.getPropertyValue());
				}
			return;
			}
		for (int i = 0; i < listedEntities.length; ++i)
			{
			if (entity == listedEntities[i])
				{
				tables[i].setPropertyValue(event.getPropertyName(), event.getPropertyValue());
				}
			}
		}
	public		void		processModelChange(MiModelChangeEvent event)
		{
		setModel(getModel());
		}
	protected	void		setHasChanged(boolean flag)
		{
		if (!flag)
			changedEntities.removeAllElements();
		super.setHasChanged(flag);
		}
	}

