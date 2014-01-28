
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
import java.util.ArrayList;
import java.awt.Image;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
/******
//To do:

	check hasChanged fgor each Panel
	Conn Pts (left center, lower left, ...
	Behavior (dbl-click, dropped, dragged) RunTime/DesignTime + Events/Actions (use Tree List'
		list of all events onn left side and actions on right side
	get/set Attribute locks
	* Locations x,y, width, height... aspect locks
	Editing interaction in PREFERENCES (dbl-click, dropped, dragged)
	* deletable, movable, selectable, ...  selected/selcable, hidden...
	* Names (internal, user, class, ...)

Add similar GraphInspector/Formatter

*****/

interface MiiShapeInspectorPanel
	{
	void		setTargetShape(MiPart target);
	void		applyToTargetShape(MiPart target);
	}
interface MiiShapeAttributesPanel
	{
	void		setDisplayedAttributes(MiAttributes attributes);
	MiAttributes	getDisplayedAttributes(MiAttributes attributes);
	}
	
public class MiShapeAttributesPanel extends MiVisibleContainer implements MiiActionHandler, MiiActionTypes, MiiShapeAttributesPanel, MiiShapeInspectorPanel, MiiCommandHandler
	{
	public static final int		Mi_OBJECT_SHAPE_ATTRIBUTES_PANEL_INDEX		= 0;
	public static final int		Mi_FILL_SHAPE_ATTRIBUTES_PANEL_INDEX		= 1;
	public static final int		Mi_LINE_SHAPE_ATTRIBUTES_PANEL_INDEX		= 2;
	public static final int		Mi_LINE_ENDS_SHAPE_ATTRIBUTES_PANEL_INDEX	= 3;
	public static final int		Mi_SHADOW_SHAPE_ATTRIBUTES_PANEL_INDEX		= 4;
	public static final int		Mi_BORDER_LOOK_SHAPE_ATTRIBUTES_PANEL_INDEX	= 5;
	public static final int		Mi_TEXT_SHAPE_ATTRIBUTES_PANEL_INDEX		= 6;

	public static final String	SQUARISH_PANEL_SHAPE				= "SquarishPanelShape";
	public static final String	HORIZONTAL_PANEL_SHAPE				= "HorizontalPanelShape";


	private		MiTabbedFolder		tabbedFolder;
	private		MiPart			objectPropertyPanel;
/*
	private		MiPart			fillPropertyPanel;
	private		MiPart			borderPropertyPanel;
	private		MiPart			linePropertyPanel;
	private		MiPart			lineEndsPropertyPanel;
	private		MiPart			shadowPropertyPanel;
	private		MiPart			textPropertyPanel;
*/
	private		MiParts			panels 					= new MiParts();
	private		String			desiredPanelShape;
	private		int			panelsToCreateMask;
	private		MiAttributes		targetAttributes;
	private		boolean			settingDisplayedAttributes;
	private		MiDrawingToolBarManager drawingToolBarManager;



	public				MiShapeAttributesPanel()
		{
		this(SQUARISH_PANEL_SHAPE, 0xffff);
		}
	/**
	 * @param	panelShape SQUARISH_PANEL_SHAPE (the default) if panel is to be squarish
	 **/
	public				MiShapeAttributesPanel(String desiredPanelShape, int panelsToCreateMask)
		{
		this.desiredPanelShape = desiredPanelShape;
		this.panelsToCreateMask = panelsToCreateMask;

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);
		tabbedFolder = new MiTabbedFolder();
		makePanels(tabbedFolder, desiredPanelShape, panelsToCreateMask);
		appendPart(tabbedFolder);
		}

	public		MiTabbedFolder	getTabbedFolder()
		{
		return(tabbedFolder);
		}

	public		void		setDrawingToolBarManager(MiDrawingToolBarManager drawingToolBarManager)
		{
		this.drawingToolBarManager = drawingToolBarManager;
		}

	protected	void		makePanels(MiTabbedFolder tabbedFolder, String panelShape, int panelsToCreateMask)
		{
		if ((panelsToCreateMask & (1 << Mi_OBJECT_SHAPE_ATTRIBUTES_PANEL_INDEX)) != 0)
			{
			objectPropertyPanel = makeObjectPropertyPanel();
			tabbedFolder.appendFolder("Object", objectPropertyPanel);
			objectPropertyPanel.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
			}

		if ((panelsToCreateMask & (1 << Mi_FILL_SHAPE_ATTRIBUTES_PANEL_INDEX)) != 0)
			{
			appendFolderPanel("Fill", makeFillPropertyPanel(panelShape));
			}

		if ((panelsToCreateMask & (1 << Mi_LINE_SHAPE_ATTRIBUTES_PANEL_INDEX)) != 0)
			{
			appendFolderPanel("Line", makeLinePropertyPanel(panelShape));
			}

		if ((panelsToCreateMask & (1 << Mi_LINE_ENDS_SHAPE_ATTRIBUTES_PANEL_INDEX)) != 0)
			{
			appendFolderPanel("Line Ends", makeLineEndsPropertyPanel(panelShape));
			}

		if ((panelsToCreateMask & (1 << Mi_SHADOW_SHAPE_ATTRIBUTES_PANEL_INDEX)) != 0)
			{
			appendFolderPanel("Shadow", makeShadowPropertyPanel(panelShape));
			}

		if ((panelsToCreateMask & (1 << Mi_BORDER_LOOK_SHAPE_ATTRIBUTES_PANEL_INDEX)) != 0)
			{
			appendFolderPanel("Border", makeBorderPropertyPanel(panelShape));
			}

		if ((panelsToCreateMask & (1 << Mi_TEXT_SHAPE_ATTRIBUTES_PANEL_INDEX)) != 0)
			{
			appendFolderPanel("Text", makeTextPropertyPanel(panelShape));
			}
		}
	public		void		appendFolderPanel(String panelName, MiPart panel)
		{
		if (!(panel instanceof MiiShapeAttributesPanel))
			{
			throw new RuntimeException("Panel must be an instance of both MiPart and MiiShapeAttributesPanel: " + panel);
			}
		tabbedFolder.appendFolder(panelName, panel);
		panel.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		panels.add(panel);
		}
	public		void		insertFolderPanel(String panelName, MiPart panel, int index)
		{
		if (!(panel instanceof MiiShapeAttributesPanel))
			{
			throw new RuntimeException("Panel must be an instance of both MiPart and MiiShapeAttributesPanel: " + panel);
			}
		tabbedFolder.insertFolder(panelName, panel, index);
		panel.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		panels.add(panel);
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		settingDisplayedAttributes = true;
		for (int i = 0; i < panels.size(); ++i)
			{
			((MiiShapeAttributesPanel )panels.get(i)).setDisplayedAttributes(attributes);
			}
		targetAttributes = attributes;
		settingDisplayedAttributes = false;
		}

	public		boolean		isSettingDisplayedAttributes()
		{
		return(settingDisplayedAttributes);
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes atts)
		{
//MiDebug.println(this + "AAAAAAAAAAAAA getDisplayedAttributes = " + atts);
//MiDebug.printStackTrace();
		for (int i = 0; i < panels.size(); ++i)
			{
			atts = ((MiiShapeAttributesPanel )panels.get(i)).getDisplayedAttributes(atts);
			}
		return(atts);
		}
	public		void		setTargetShape(MiPart target)
		{
//MiDebug.println(this + "AAAAAAAAAAAAA ssssssssssssetTargetShape = " + target);
//MiDebug.printStackTrace();
		if (objectPropertyPanel != null)
			{
			((MiiShapeInspectorPanel )objectPropertyPanel).setTargetShape(target);
			}
		setDisplayedAttributes(target.getAttributes());
		}
	public		void		applyToTargetShape(MiPart target)
		{
		if (objectPropertyPanel != null)
			{
			((MiiShapeInspectorPanel )objectPropertyPanel).applyToTargetShape(target);
			}

//MiDebug.println(this + "AAAAAAAAAAAAAapplyToTargetShape = " + target);
//MiDebug.printStackTrace();
		applyToTargetShapes(target);
		}

	protected	void		applyToTargetShapes(MiPart target)
		{
		MiParts partsToModify = new MiParts();
		MiUtility.getActualShapesToApplyAttributeChangeTo(target, partsToModify);

		ArrayList newAttributesList = new ArrayList();
		ArrayList currentAttributesList = new ArrayList();
		for (int i = 0; i < partsToModify.size(); ++i)
			{
			MiPart part = partsToModify.get(i);
			currentAttributesList.add(part.getAttributes());
			newAttributesList.add(getDisplayedAttributes(part.getAttributes()));
			}

		MiModifyAttributesOfPartsCommand command = new MiModifyAttributesOfPartsCommand(
			target.getContainingEditor(),
			newAttributesList, 
			currentAttributesList,
			partsToModify);
		command.redo();
		MiSystem.getTransactionManager().appendTransaction(command);
		}
/****
		{

//MiDebug.println(this + "AAAAAAAAAAAAAapplyToTargetShapeSSSSSS = " + target);
		// If target is just a grouping and has no attributes itself...
		if (((target instanceof MiLayout) || (target instanceof MiContainer)) && (!(target instanceof MiVisibleContainer)))
			{
			for (int i = 0; i < target.getNumberOfParts(); ++i)
				{
				applyToTargetShapes(target.getPart(i));
				}
			}
		for (int i = 0; i < target.getNumberOfAttachments(); ++i)
			{
			applyToTargetShapes(target.getAttachment(i));
			}
//MiDebug.println(this + "AAAAAAAAAAAAAapplyToTargetShapeSSSSSS Finally = " + target);
		if (target.getResource(MiiSelectionGraphics.SELECTION_GRAPHICS_GRAPHICS) == null)
			{
			target.setAttributes(getDisplayedAttributes(target.getAttributes()));
//MiDebug.dump(target);
			}
		}
****/

	protected	MiPart		makeObjectPropertyPanel()
		{
		return(new MiObjectPropertyPanel());
		}
	protected	MiPart		makeFillPropertyPanel(String panelShape)
		{
		return(new MiFillPropertyPanel(panelShape));
		}
	protected	MiPart		makeLinePropertyPanel(String panelShape)
		{
		return(new MiLinePropertyPanel(panelShape));
		}
	protected	MiPart		makeLineEndsPropertyPanel(String panelShape)
		{
		return(new MiLineEndsPropertyPanel(panelShape));
		}
	protected	MiPart		makeShadowPropertyPanel(String panelShape)
		{
		return(new MiShadowPropertyPanel(panelShape));
		}
	protected	MiPart		makeBorderPropertyPanel(String panelShape)
		{
		return(new MiBorderPropertyPanel(panelShape));
		}
	protected	MiPart		makeTextPropertyPanel(String panelShape)
		{
		return(new MiTextPropertyPanel(panelShape));
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_VALUE_CHANGED_ACTION) && (!settingDisplayedAttributes))
			{
// MiDebug.println("Value changed in MiShapeAttributesPanel textproperties...: targetAttributes = " + targetAttributes);
			if (targetAttributes != null)
				{
				targetAttributes = getDisplayedAttributes(targetAttributes);
				setDisplayedAttributes(targetAttributes);
				}
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			if (drawingToolBarManager != null)
				{
				drawingToolBarManager.setNewShapeAttributes(targetAttributes);
				}
			}
		return(true);
		}
	public		void		processCommand(String cmd)
		{
		if (cmd.equals(MiDrawingToolBarManager.Mi_NEW_SHAPE_ATTRIBUTES_CHANGED_STATE_NAME))
			{
			if (drawingToolBarManager != null)
				{
				setDisplayedAttributes(drawingToolBarManager.getNewShapeAttributes());
				}
			}
		}
	static protected MiWidget	makeIconToggleLabel(MiiCommandHandler observer, MiPart container, MiRadioStateEnforcer radioEnforcer, MiPart icon, String toggleName)
		{
		MiWidget toggle;
		if (radioEnforcer != null)
			{
			toggle = new MiCircleToggleButton(MiCircleToggleButton.Mi_DOTTED_WHEN_SELECTED);
			toggle.setRadioStateEnforcer(radioEnforcer);
			toggle.appendCommandHandler(observer, toggleName);
			}
		else
			{
			toggle = new MiToggleButton();
			toggle.appendCommandHandler(observer, toggleName, Mi_SELECTED_ACTION);
			toggle.appendCommandHandler(observer, toggleName, Mi_DESELECTED_ACTION);
			}
		container.appendPart(toggle);
		if (icon != null)
			{
			icon.appendEventHandler(new MiDelegateEvents(toggle));
			container.appendPart(icon);
			}
		MiText label = new MiText(toggleName);
		label.appendEventHandler(new MiDelegateEvents(toggle));
		container.appendPart(label);
		return(toggle);
		}
	}
class MiObjectPropertyPanel extends MiContainer implements MiiCommandHandler, MiiShapeInspectorPanel
	{
	private	static final	String		REVERSED_NAME		= "<None>";
	private	static final	String		NO_NAME_NAME		= "<None>";
	private	static final	String		CHANGE_NAME_CMD_NAME	= "changeName";
	private	static final	String		CHANGE_XMIN_CMD_NAME	= "changeXmin";
	private	static final	String		CHANGE_YMIN_CMD_NAME	= "changeYmin";
	private	static final	String		CHANGE_XMAX_CMD_NAME	= "changeXmax";
	private	static final	String		CHANGE_YMAX_CMD_NAME	= "changeYmax";
	private	static final	String		CHANGE_WIDTH_CMD_NAME	= "changeWidth";
	private	static final	String		CHANGE_HEIGHT_CMD_NAME	= "changeHeight";
	private	static final	String		CHANGE_MIN_WIDTH_CMD_NAME	= "changeMinWidth";
	private	static final	String		CHANGE_MAX_WIDTH_CMD_NAME	= "changeMaxWidth";
	private	static final	String		CHANGE_MIN_HEIGHT_CMD_NAME	= "changeMinHeight";
	private	static final	String		CHANGE_MAX_HEIGHT_CMD_NAME	= "changeMaxHeight";
	private	static final	String		CHANGE_FIXED_WIDTH_CMD_NAME	= "Fixed Width";
	private	static final	String		CHANGE_FIXED_HEIGHT_CMD_NAME	= "Fixed Height";
	private	static final	String		CHANGE_FIXED_ASPECT_RATIO_CMD_NAME= "Fixed Aspect Ratio";
	private	static final	String		CHANGE_MOVABLE_CMD_NAME		= "Movable";
	private	static final	String		CHANGE_DELETABLE_CMD_NAME	= "Deletable";

	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		MiPart			nameArea;
	private		MiPart			geometryArea;
	private		MiPart			sizeMinMaxArea;
	private		MiPart			sizeLocksArea;
	private		MiTextField		xminField;
	private		MiTextField		yminField;
	private		MiTextField		xmaxField;
	private		MiTextField		ymaxField;
	private		MiTextField		widthField;
	private		MiTextField		heightField;
	private		MiTextField		nameField;
	private		MiTextField		minWidthField;
	private		MiTextField		maxWidthField;
	private		MiTextField		minHeightField;
	private		MiTextField		maxHeightField;
	private		MiPart			fixedWidthToggle;
	private		MiPart			fixedHeightToggle;
	private		MiPart			fixedAspectRatioToggle;
	private		MiPart			movableToggle;
	private		MiPart			deletableToggle;
	private		MiLabel			classNameField;
	private		MiBounds		specifiedBounds		= new MiBounds();
	private		boolean			settingTargetShape;



	public				MiObjectPropertyPanel()
		{
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setInsetMargins(INSET_SPACING);
		rowLayout.setElementHJustification(Mi_JUSTIFIED);
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);
		setLayout(rowLayout);

		MiColumnLayout column1 = new MiColumnLayout();
		column1.setElementHSizing(Mi_EXPAND_TO_FILL);
		column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column1.setAlleyVSpacing(ALLEY_SPACING);
		appendPart(column1);
		MiColumnLayout column2 = new MiColumnLayout();
		column2.setElementHSizing(Mi_EXPAND_TO_FILL);
		column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column2.setAlleyVSpacing(ALLEY_SPACING);
		appendPart(column2);
		
		nameArea = makeNameArea();
		geometryArea = makeGeometryArea();
		sizeMinMaxArea = makeSizeMinMaxArea();
		sizeLocksArea = makeSizeLocksArea();
//		hierarchyArea = makeHierarchyArea();

		column1.appendPart(nameArea);
		column1.appendPart(geometryArea);

		column2.appendPart(sizeMinMaxArea);
		column2.appendPart(sizeLocksArea);
//		column2.appendPart(hierarchyList);

		nameField.setValue(NO_NAME_NAME);
		}
	public		void		setTargetShape(MiPart target)
		{
		settingTargetShape = true;

		if (target.getName() != null)
			nameField.setValue(target.getName());
		else
			nameField.setValue(NO_NAME_NAME);

		classNameField.setValue(target.getClass().getName());
		
		updateBoundsFields(target.getBounds());
		specifiedBounds = target.getBounds();

		minWidthField.setValue(Utility.toShortString(target.getMinimumWidth()));
		maxWidthField.setValue(Utility.toShortString(target.getMaximumWidth()));
		minHeightField.setValue(Utility.toShortString(target.getMinimumHeight()));
		maxHeightField.setValue(Utility.toShortString(target.getMaximumHeight()));

		fixedWidthToggle.select(target.hasFixedWidth());
		fixedHeightToggle.select(target.hasFixedHeight());
		fixedAspectRatioToggle.select(target.hasFixedAspectRatio());
		movableToggle.select(target.isMovable());
		deletableToggle.select(target.isDeletable());

		settingTargetShape = false;
		}

	protected	void		updateBoundsFields(MiBounds b)
		{
		if (b.isReversed())
			{
			widthField.setValue(REVERSED_NAME);
			heightField.setValue(REVERSED_NAME);
			}
		else
			{
			xminField.setValue(Utility.toShortString(b.getXmin()));
			yminField.setValue(Utility.toShortString(b.getYmin()));
			xmaxField.setValue(Utility.toShortString(b.getXmax()));
			ymaxField.setValue(Utility.toShortString(b.getYmax()));

			widthField.setValue(Utility.toShortString(b.getWidth()));
			heightField.setValue(Utility.toShortString(b.getHeight()));
			}
		}
	public		void		applyToTargetShape(MiPart target)
		{
		String name = nameField.getValue();
		if ((Utility.isEmptyOrNull(name)) || (name.equals(NO_NAME_NAME)))
			{
			if (!Utility.isEmptyOrNull(target.getName()))
				target.setName(null);
			}
		else if ((Utility.isEmptyOrNull(target.getName())) || (!target.getName().equals(name)))
			{
			target.setName(name);
			}

		if (!specifiedBounds.equals(target.getBounds()))
			{
			target.setBounds(specifiedBounds);
			MiSize size = new MiSize(specifiedBounds.getWidth(), specifiedBounds.getHeight());
			target.setPreferredSize(size);
			target.setMinimumSize(size);
			}

		target.setMinimumWidth(Utility.toDouble(minWidthField.getValue(), 0));
		target.setMaximumWidth(Utility.toDouble(maxWidthField.getValue(), 0));
		target.setMinimumHeight(Utility.toDouble(minHeightField.getValue(), 0));
		target.setMaximumHeight(Utility.toDouble(maxHeightField.getValue(), 0));

		target.setFixedWidth(fixedWidthToggle.isSelected());
		target.setFixedHeight(fixedHeightToggle.isSelected());
		target.setFixedAspectRatio(fixedAspectRatioToggle.isSelected());
		target.setMovable(movableToggle.isSelected());
		target.setDeletable(deletableToggle.isSelected());
		}

	protected	MiPart		makeNameArea()
		{
		MiBox box = new MiBox("Names");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiText label = new MiText("Name");
		nameField = new MiTextField();
		nameField.appendCommandHandler(this, CHANGE_NAME_CMD_NAME, Mi_TEXT_CHANGE_ACTION);
		box.appendPart(label);
		box.appendPart(nameField);

		label = new MiText("Type");
		classNameField = new MiLabel();
		box.appendPart(label);
		box.appendPart(classNameField);

		return(box);
		}

	protected	MiPart		makeGeometryArea()
		{
		MiBox box = new MiBox("Geometry");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiText label = new MiText("Left");
		xminField = new MiTextField();
		xminField.appendCommandHandler(this, CHANGE_XMIN_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(xminField);

		label = new MiText("Width");
		widthField = new MiTextField();
		widthField.appendCommandHandler(this, CHANGE_WIDTH_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(widthField);

		label = new MiText("Right");
		xmaxField = new MiTextField();
		xmaxField.appendCommandHandler(this, CHANGE_XMAX_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(xmaxField);

		label = new MiText("Bottom");
		yminField = new MiTextField();
		yminField.appendCommandHandler(this, CHANGE_YMIN_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(yminField);

		label = new MiText("Height");
		heightField = new MiTextField();
		heightField.appendCommandHandler(this, CHANGE_HEIGHT_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(heightField);

		label = new MiText("Top");
		ymaxField = new MiTextField();
		ymaxField.appendCommandHandler(this, CHANGE_YMAX_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(ymaxField);

		return(box);
		}

	protected	MiPart		makeSizeMinMaxArea()
		{
		MiBox box = new MiBox("Limits");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiText label = new MiText("Minimum Width");
		minWidthField = new MiTextField();
		minWidthField.appendCommandHandler(this, CHANGE_MIN_WIDTH_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(minWidthField);

		label = new MiText("Maximum Width");
		maxWidthField = new MiTextField();
		maxWidthField.appendCommandHandler(this, CHANGE_MAX_WIDTH_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(maxWidthField);

		label = new MiText("Minimum Height");
		minHeightField = new MiTextField();
		minHeightField.appendCommandHandler(this, CHANGE_MIN_HEIGHT_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(minHeightField);

		label = new MiText("Maximum Height");
		maxHeightField = new MiTextField();
		maxHeightField.appendCommandHandler(this, CHANGE_MAX_HEIGHT_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(label);
		box.appendPart(maxHeightField);

		return(box);
		}

	protected	MiPart		makeSizeLocksArea()
		{
		MiBox box = new MiBox("Locks");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		fixedWidthToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_FIXED_WIDTH_CMD_NAME);

		fixedHeightToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_FIXED_HEIGHT_CMD_NAME);

		fixedAspectRatioToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_FIXED_ASPECT_RATIO_CMD_NAME);

		movableToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_MOVABLE_CMD_NAME);

		deletableToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_DELETABLE_CMD_NAME);

		return(box);
		}

	public		void		processCommand(String cmd)
		{
		if (settingTargetShape)
			return;

		if (cmd.equals(CHANGE_XMIN_CMD_NAME))
			specifiedBounds.translateXminTo(Utility.toDouble(xminField.getValue(), 0));
		else if (cmd.equals(CHANGE_YMIN_CMD_NAME))
			specifiedBounds.translateYminTo(Utility.toDouble(yminField.getValue(), 0));
		else if (cmd.equals(CHANGE_WIDTH_CMD_NAME))
			specifiedBounds.setWidth(Utility.toDouble(widthField.getValue(), 0));
		else if (cmd.equals(CHANGE_HEIGHT_CMD_NAME))
			specifiedBounds.setHeight(Utility.toDouble(heightField.getValue(), 0));
		else if (cmd.equals(CHANGE_XMAX_CMD_NAME))
			specifiedBounds.translateXmaxTo(Utility.toDouble(xmaxField.getValue(), 0));
		else if (cmd.equals(CHANGE_YMAX_CMD_NAME))
			specifiedBounds.translateYmaxTo(Utility.toDouble(ymaxField.getValue(), 0));

		specifiedBounds.assureMinsLessThanMaxs();

		updateBoundsFields(specifiedBounds);

		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	}
class MiFillPropertyPanel extends MiContainer implements MiiCommandHandler, MiiShapeAttributesPanel
	{
	public static	String			NO_FILL_NAME		= "No Fill";
	public static	String			IMAGE_FILL_NAME		= "Image Fill";
	public static	String			SOLID_FILL_NAME		= "Solid Fill";
	public static	String			PATTERN_FILL_NAME	= "Pattern Fill";
	public static	String			GRADIENT_FILL_NAME	= "Gradient Fill";

	private	static final	String		CHANGE_GRADIENT_INTENSITY_NAME	= "Intensity";
	private	static final	String		CHANGE_BACKCOLOR_NAME	= "changeBGColor";
	private	static final	String		CHANGE_COLOR_NAME	= "changeColor";
	private	static final	String		CHANGE_GRADIENT_NAME	= "changeGradient";
	private static final	String		CHANGE_IMAGE_NAME	= "Change Image";
	private static final	String		BROWSE_FILES_NAME	= "Browse Files";
	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		boolean			hasChanged;
	private		MiPart			fillStyleArea;
	private		MiPart			patternColorArea;
	private		MiPart			backgroundColorArea;
	private		MiPart			patternStyleArea;
	private		MiPart			gradientStyleArea;
	private		MiPart			sampleArea;
	private		MiWidget		noFillToggle;
	private		MiWidget		imageToggle;
	private		MiWidget		solidToggle;
	private		MiWidget		patternToggle;
	private		MiWidget		gradientToggle;
	private		Image			image;
	private		MiColorOptionMenu	patternColor;
	private		MiColorOptionMenu	backgroundColor;
	private		MiRectangle		sampleRectangle;
	private		MiImage			colorIcon;
	private		MiToggleButton		gradientCells[];
	private		String			panelShape;
	private		MiSlider		gradientIntensitySlider;
	private		MiPart			gradientIntensityArea;
	private		MiPart			imageNameArea;
	private		MiComboBox		imageFileNameComboBox;
	private		MiPushButton		imageFileNameBrowser;
	private		MiFileChooser		fileChooser;


	private static	int[]			lightSourceLocations =
		{
		// Mi_CENTER_LOCATION,
		Mi_LEFT_LOCATION,
		Mi_RIGHT_LOCATION,
		Mi_BOTTOM_LOCATION,
		Mi_TOP_LOCATION,
		Mi_LOWER_LEFT_LOCATION,
		Mi_LOWER_RIGHT_LOCATION,
		Mi_UPPER_LEFT_LOCATION,
		Mi_UPPER_RIGHT_LOCATION,
		// Mi_SURROUND_LOCATION
		};


	public				MiFillPropertyPanel()
		{
		this(MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE);
		}

	public				MiFillPropertyPanel(String panelShape)
		{
		this.panelShape = panelShape;

		String colorIconName = MiSystem.getProperty("Mi_COLOR_ICON_NAME");
		if (colorIconName != null)
			colorIcon = new MiImage(colorIconName);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setInsetMargins(INSET_SPACING);
		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			rowLayout.setElementHJustification(Mi_JUSTIFIED);
			}
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);
		setLayout(rowLayout);

		MiLayout column1 = null;
		MiLayout column2 = null;

		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			column1 = new MiColumnLayout();
			column1.setElementHSizing(Mi_EXPAND_TO_FILL);
			column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column1.setAlleyVSpacing(ALLEY_SPACING);
			appendPart(column1);

			column2 = new MiColumnLayout();
			column2.setElementHSizing(Mi_EXPAND_TO_FILL);
			column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column2.setAlleyVSpacing(ALLEY_SPACING);
			appendPart(column2);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			column1 = new MiColumnLayout();
			column1.setElementHSizing(Mi_EXPAND_TO_FILL);
			column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column1.setAlleyVSpacing(ALLEY_SPACING);
			appendPart(column1);

			column2 = new MiColumnLayout();
			column2.setElementHSizing(Mi_EXPAND_TO_FILL);
			column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column2.setAlleyVSpacing(ALLEY_SPACING);
			appendPart(column2);

			column1.setInsetMargins(0);
			column2.setInsetMargins(0);
			column1.setAlleyVSpacing(ALLEY_SPACING - 10);
			column2.setAlleyVSpacing(ALLEY_SPACING - 10);
			rowLayout.setInsetMargins(0);
			}
		
		fillStyleArea = makeFillStyleArea();
		patternColorArea = makePatternColorArea();
		backgroundColorArea = makeBackGroundColorArea();
		patternStyleArea = makePatternStyleArea();
		gradientStyleArea = makeGradientStyleArea();
		gradientIntensityArea = makeGradientIntensityArea();
		sampleArea = makeSampleArea();
		imageNameArea = makeImageNameArea();

		patternStyleArea.setVisible(false);
		// FIX: add this someday
		patternColorArea.setVisible(false);

		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			column1.appendPart(fillStyleArea);
			column1.appendPart(patternColorArea);
			column1.appendPart(backgroundColorArea);

			column2.appendPart(patternStyleArea);
			column2.appendPart(gradientStyleArea);
			column2.appendPart(imageNameArea);
			appendPart(sampleArea);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			column1.appendPart(fillStyleArea);
			column1.appendPart(patternColorArea);
			column1.appendPart(backgroundColorArea);
			column1.appendPart(patternStyleArea);

			column2.appendPart(gradientStyleArea);
			column2.appendPart(gradientIntensityArea);
			appendPart(imageNameArea);
			appendPart(sampleArea);
			}


		gradientCells[0].select(true);
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		displayAttributes(attributes);
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes attributes)
		{
		return(gatherAttributes(attributes));
		}
	public		void		displayAttributes(MiAttributes attributes)
		{
		if (attributes.getBackgroundColor() == Mi_TRANSPARENT_COLOR)
			{
			noFillToggle.select(true);
			}
		else if (attributes.getBackgroundImage() != null)
			{
			imageToggle.select(true);
			}
		else if ((attributes.getBackgroundRenderer() != null)
			&& (attributes.getBackgroundRenderer() instanceof MiGradientRenderer))
			{
			gradientToggle.select(true);

			MiGradientRenderer renderer = (MiGradientRenderer )attributes.getBackgroundRenderer();
			gradientIntensitySlider.setCurrentValue(100 * renderer.getDarkerBrighterFactor());
			for (int i = 0; i < lightSourceLocations.length; ++i)
				{
				if (renderer.getLightSourceLocation() == lightSourceLocations[i])
					{
					gradientCells[i].select(true);
					break;
					}
				}
			backgroundColor.setValue(MiColorManager.getColorName(renderer.getBaseColor()));
			}
		else
			{
			solidToggle.select(true);
			}

		if (gradientToggle.isSelected())
			{
			gradientStyleArea.setSensitive(true);
			gradientIntensityArea.setSensitive(true);
			}
		else if (gradientToggle.isSelected())
			{
			gradientStyleArea.setSensitive(false);
			gradientIntensityArea.setSensitive(false);
			}

		patternColor.setColorValue(attributes.getColor());
		backgroundColor.setColorValue(attributes.getBackgroundColor());

		sampleRectangle.setAttributes(attributes);
		hasChanged = false;
		//updateSample();
		}
	public		MiAttributes	gatherAttributes(MiAttributes attributes)
		{
		//attributes = attributes.setPatternColor(patternColor.getValue());
		if (noFillToggle.isSelected())
			{
			attributes = attributes.setBackgroundColor(Mi_TRANSPARENT_COLOR);
			attributes = attributes.setBackgroundRenderer(null);
			attributes = attributes.setBackgroundImage(null);
			}
		else if (imageToggle.isSelected())
			{
			attributes = attributes.setBackgroundColor(backgroundColor.getValue());
			if (attributes.getBackgroundColor() == null)
				attributes = attributes.setBackgroundColor(MiColorManager.black);
			attributes = attributes.setBackgroundRenderer(null);
			attributes = attributes.setBackgroundImage(image);
			}
		else if (solidToggle.isSelected())
			{
			attributes = attributes.setBackgroundColor(backgroundColor.getValue());
			if (attributes.getBackgroundColor() == null)
				attributes = attributes.setBackgroundColor(MiColorManager.black);
			attributes = attributes.setBackgroundImage(null);
			attributes = attributes.setBackgroundRenderer(null);
			}
/**** FIX: Someday
		else if (patternToggle.isSelected())
			{
			attributes = attributes.setBackgroundColor(backgroundColor.getValue());
			attributes = attributes.setBackgroundRenderer(null);
			}
****/
		else if (gradientToggle.isSelected())
			{
			attributes = attributes.setBackgroundColor(backgroundColor.getValue());
			if (attributes.getBackgroundColor() == null)
				attributes = attributes.setBackgroundColor(MiColorManager.black);
			attributes = attributes.setBackgroundImage(null);
			MiGradientRenderer renderer = new MiGradientRenderer();
			for (int i = 0; i < gradientCells.length; ++i)
				{
				if (gradientCells[i].isSelected())
					{
					renderer.setLightSourceLocation(lightSourceLocations[i]);
					renderer.setBaseColor(MiColorManager.getColor(backgroundColor.getValue()));
					renderer.setDarkerBrighterFactor(gradientIntensitySlider.getCurrentValue()/100);
					attributes = attributes.setBackgroundRenderer(renderer);
					break;
					}
				}
			}
		return(attributes);
		}

	protected	MiPart		makeFillStyleArea()
		{
		MiBox box = new MiBox("Fill Style");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(3);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);

		MiRectangle icon = new MiRectangle(0,0,20,15);
		icon.setColor(MiColorManager.black);
		icon.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		noFillToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, NO_FILL_NAME);

		icon = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.black);
		solidToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, SOLID_FILL_NAME);

		icon = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.black);
		imageToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, IMAGE_FILL_NAME);









		// FIX: add this someday
/****
		icon = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.black);
		patternToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, PATTERN_FILL_NAME);
		patternToggle.setVisible(false);
****/

		icon = new MiRectangle(0,0,20,15);
		icon.setColor(MiColorManager.black);
		icon.setBackgroundColor(MiColorManager.veryLightGray);
		icon.setBackgroundRenderer(new MiGradientRenderer());
		gradientToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, GRADIENT_FILL_NAME);

		return(box);
		}

	protected	MiPart		makePatternColorArea()
		{
		MiBox box = new MiBox("Pattern Color");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(rowLayout);

		if (colorIcon != null)
			box.appendPart(colorIcon.copy());
		patternColor = new MiColorOptionMenu();
		patternColor.appendCommandHandler(this, CHANGE_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(patternColor);

		return(box);
		}

	protected	MiPart		makeBackGroundColorArea()
		{
		MiBox box = new MiBox("Background Color");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(rowLayout);

		if (colorIcon != null)
			box.appendPart(colorIcon.copy());
		backgroundColor = new MiColorOptionMenu();
		backgroundColor.appendCommandHandler(this, CHANGE_BACKCOLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(backgroundColor);

		return(box);
		}

	protected	MiPart		makePatternStyleArea()
		{
		MiBox box = new MiBox("Pattern Style");
		return(box);
		}
	protected	MiPart		makeGradientStyleArea()
		{
		MiBox box = new MiBox("Gradient Style");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setAlleySpacing(0);
		gridLayout.setNumberOfColumns(4);
		box.setLayout(gridLayout);

		MiToggleButton cell;

		gradientCells = new MiToggleButton[lightSourceLocations.length];
		MiSize cellSize = new MiSize(40, 40);
		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);
		for (int i = 0; i < lightSourceLocations.length; ++i)
			{
			cell = new MiToggleButton();
			cell.appendCommandHandler(this, CHANGE_GRADIENT_NAME, Mi_SELECTED_ACTION);
			cell.setVisibleContainerAutomaticLayoutEnabled(false);
			gradientCells[i] = cell;
			cell.setBackgroundColor(MiColorManager.white);
			MiGradientRenderer renderer = new MiGradientRenderer();
			renderer.setBaseSize(40, 40);
			renderer.setLightSourceLocation(lightSourceLocations[i]);
			renderer.setDarkerBrighterFactor(0.5);
			cell.setBackgroundRenderer(renderer);
			cell.setVisibleContainerAutomaticLayoutEnabled(false);
			cell.setPreferredSize(cellSize);
			cell.setMinimumSize(cellSize);
			cell.setRadioStateEnforcer(radioEnforcer);
			box.appendPart(cell);
			}

		return(box);
		}
	protected	MiPart		makeGradientIntensityArea()
		{
		MiBox box = new MiBox("Gradient Intensity");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(0);
		box.setLayout(rowLayout);

		gradientIntensitySlider = new MiSlider();
		gradientIntensitySlider.appendCommandHandler(this, CHANGE_GRADIENT_INTENSITY_NAME, Mi_VALUE_CHANGED_ACTION);
		gradientIntensitySlider.setMaximumValue(100);
		box.appendPart(gradientIntensitySlider);

		return(box);
		}
	protected	MiPart		makeImageNameArea()
		{
		MiBox box = new MiBox("Image Name");
		MiColumnLayout columnLayout = new MiColumnLayout();
//		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
//		columnLayout.setUniqueElementIndex(0);
		box.setLayout(columnLayout);

		imageFileNameComboBox = new MiComboBox();
		imageFileNameComboBox.appendCommandHandler(this, CHANGE_IMAGE_NAME, Mi_VALUE_CHANGED_ACTION);
		imageFileNameBrowser = new MiPushButton("Browse...");
		imageFileNameBrowser.appendCommandHandler(this, BROWSE_FILES_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(imageFileNameComboBox);
		box.appendPart(imageFileNameBrowser);

		return(box);
		}

	protected	MiPart		makeSampleArea()
		{
		MiBox box = new MiBox("Sample");
		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		container.setBackgroundColor(MiColorManager.white);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementJustification(Mi_CENTER_JUSTIFIED);
		layout.setInsetMargins(14);
		container.setLayout(layout);
		sampleRectangle = new MiRectangle(0,0,150,100);
		sampleRectangle.setPreferredSize(new MiSize(150,100));
		container.appendPart(sampleRectangle);
		box.appendPart(container);
		return(box);
		}
	protected	void		updateSample()
		{
		sampleRectangle.setAttributes(gatherAttributes(sampleRectangle.getAttributes()));
		}
	public		void		processCommand(String cmd)
		{
		if (cmd.equals(BROWSE_FILES_NAME))
			{
			if (fileChooser == null)
				{
				fileChooser = new MiFileChooser(getContainingEditor());
				fileChooser.setAvailableSaveFileFormats(
					new Strings("GIF", "JPEG", "X Pixel Map"), 
					new Strings("gif", "jpg", "xpm"), 
					new Strings("true", "true", "true"));
				}
			String name = fileChooser.popupAndWaitForClose();
			if (name != null)
				{
				image = new MiImage(name).getImage();
				Strings contents = imageFileNameComboBox.getContents();
				contents.insertElementAt(name, 0);
				imageFileNameComboBox.setContents(contents);
				}
			}
				
		if (cmd.equals(CHANGE_BACKCOLOR_NAME))
			{
			if (backgroundColor.getColorValue() == Mi_TRANSPARENT_COLOR)
				noFillToggle.select(true);
/*
			else
				solidToggle.select(true);
*/
			}

		gradientStyleArea.setSensitive(gradientToggle.isSelected());
		gradientIntensityArea.setSensitive(gradientToggle.isSelected());
		imageNameArea.setSensitive(imageToggle.isSelected());

		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	}
class MiLinePropertyPanel extends MiContainer implements MiiCommandHandler, MiiShapeAttributesPanel
	{
	public static	String			NO_LINE_NAME		= "No Line";
	public static	String			THIN_LINE_NAME		= "Thin Line";
	public static	String			WIDE_LINE_NAME		= "Wide Line";

	private static final	String		SOLID_LINE_STYLE_NAME	= "Solid";
	private static final	String		DASHED_LINE_STYLE_NAME	= "Dashed";
	private static final	String		DOTTED_LINE_STYLE_NAME	= "Dotted";
	private static final	String		CHANGE_COLOR_NAME	= "changeColor";
	private static final	String		CHANGE_LINEWIDTH_NAME	= "changeLineWidth";
	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		boolean			hasChanged;
	private		MiPart			lineStyleArea;
	private		MiPart			lineWidthArea;
	private		MiPart			lineColorArea;
	private		MiPart			linePatternArea;
	private		MiPart			sampleArea;
	//private		MiWidget		noLineToggle;
	private		MiWidget		thinLineToggle;
	private		MiWidget		wideLineToggle;
	private		MiLineWidthOptionMenu	lineWidthOptions;
	private		MiColorOptionMenu	foregroundColor;
	private		MiLine			sampleLine;
	private		MiImage			colorIcon;
	private		MiImage			lineWidthIcon;
	private		String			panelShape;
	private		MiWidget		solidLineStyleToggle;
	private		MiWidget		dashedLineStyleToggle;
	private		MiWidget		dottedLineStyleToggle;



	public				MiLinePropertyPanel()
		{
		this(MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE);
		}
	public				MiLinePropertyPanel(String panelShape)
		{
		this.panelShape = panelShape;

		String colorIconName = MiSystem.getProperty("Mi_COLOR_ICON_NAME");
		if (colorIconName != null)
			colorIcon = new MiImage(colorIconName);

		String lineWidthIconName = MiSystem.getProperty("Mi_LINE_WIDTH_ICON_NAME");
		if (lineWidthIconName != null)
			lineWidthIcon = new MiImage(lineWidthIconName);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);
		rowLayout.setInsetMargins(INSET_SPACING);
		setLayout(rowLayout);

		MiColumnLayout column1 = new MiColumnLayout();
		column1.setElementHSizing(Mi_EXPAND_TO_FILL);
		column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column1.setAlleyVSpacing(ALLEY_SPACING);
		appendPart(column1);
		MiColumnLayout column2 = new MiColumnLayout();
		column2.setElementHSizing(Mi_EXPAND_TO_FILL);
		column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column2.setAlleyVSpacing(ALLEY_SPACING);
		appendPart(column2);
		
		lineStyleArea = makeLineStyleArea();
		lineWidthArea = makeLineWidthArea();
		lineColorArea = makeLineColorArea();
		linePatternArea = makeLinePatternArea();
		sampleArea = makeSampleArea();

		column1.appendPart(lineStyleArea);
		column1.appendPart(lineWidthArea);

		column2.appendPart(linePatternArea);
		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			column1.appendPart(lineColorArea);
			column2.appendPart(sampleArea);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			column2.appendPart(lineColorArea);
			appendPart(sampleArea);
			rowLayout.setInsetMargins(0);
			}

		thinLineToggle.select(true);
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		displayAttributes(attributes);
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes attributes)
		{
		return(gatherAttributes(attributes));
		}
	public		void		displayAttributes(MiAttributes attributes)
		{
		if (attributes.getLineStyle() == Mi_DASHED_LINE_STYLE)
			dashedLineStyleToggle.select(true);
		else if (attributes.getLineStyle() == Mi_DOTTED_LINE_STYLE)
			dottedLineStyleToggle.select(true);
		else // if (attributes.getLineStyle() == Mi_SOLID_LINE_STYLE)
			solidLineStyleToggle.select(true);


/***
		if (attributes.getColor() == Mi_TRANSPARENT_COLOR)
			noLineToggle.select(true);
***/
		if (attributes.getLineWidth() > 0)
			wideLineToggle.select(true);

		lineWidthOptions.setValue("" + attributes.getLineWidth());
		foregroundColor.setColorValue(attributes.getColor());

		// updateSample();
		sampleLine.setAttributes(attributes);
		hasChanged = false;
		}
	public		MiAttributes	gatherAttributes(MiAttributes attributes)
		{
		attributes = attributes.setColor(foregroundColor.getValue());
/*** this is same as set color to transparent...
		if (noLineToggle.isSelected())
			{
			attributes = attributes.setColor(Mi_TRANSPARENT_COLOR);
			lineWidthArea.setVisible(false);
			}
*****/
		if (dottedLineStyleToggle.isSelected())
			{
			attributes = attributes.setLineStyle(Mi_DOTTED_LINE_STYLE);
			}
		else if (dashedLineStyleToggle.isSelected())
			{
			attributes = attributes.setLineStyle(Mi_DASHED_LINE_STYLE);
			}
		else // if (solidLineStyleToggle.isSelected())
			{
			attributes = attributes.setLineStyle(Mi_SOLID_LINE_STYLE);
			}

		if (thinLineToggle.isSelected())
			{
			if (attributes.getColor() == Mi_TRANSPARENT_COLOR)
				attributes = attributes.setColor(MiColorManager.black);
			attributes = attributes.setLineWidth(0);
			lineWidthArea.setVisible(false);
			}
		else if (wideLineToggle.isSelected())
			{
			if (attributes.getColor() == Mi_TRANSPARENT_COLOR)
				attributes = attributes.setColor(MiColorManager.black);
			lineWidthArea.setVisible(true);
			MiDistance lineWidth = Utility.toDouble(lineWidthOptions.getValue());
/*
			if (lineWidth == 0)
				{
				lineWidth = 6;
				lineWidthOptions.setValue("" + lineWidth);
				}
*/
			attributes = attributes.setLineWidth((int )lineWidth);
			}
		return(attributes);
		}

	protected	MiPart		makeLineStyleArea()
		{
		MiBox box = new MiBox("Line Type");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(3);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);

/***
		MiPart icon  = new MiRectangle(0,0,20,15);
		icon.setColor(Mi_TRANSPARENT_COLOR);
		noLineToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, NO_LINE_NAME);
***/

		MiPart icon  = new MiLine(0,0,30,0);
		icon.setBackgroundColor(MiColorManager.black);
		thinLineToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, THIN_LINE_NAME);

		icon  = new MiLine(0,0,30,0);
		icon.setBackgroundColor(MiColorManager.black);
		icon.setLineWidth(6);
		wideLineToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, WIDE_LINE_NAME);

		return(box);
		}
	protected	MiPart		makeLineWidthArea()
		{
		MiBox box = new MiBox("Line Width");
		MiRowLayout rowLayout = new MiRowLayout();
		box.setLayout(rowLayout);

		if (lineWidthIcon != null)
			box.appendPart(lineWidthIcon);
		lineWidthOptions = new MiLineWidthOptionMenu();
		lineWidthOptions.appendCommandHandler(this, CHANGE_LINEWIDTH_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(lineWidthOptions);

		return(box);
		}

	protected	MiPart		makeLineColorArea()
		{
		MiBox box = new MiBox("Line Color");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(rowLayout);

		if (colorIcon != null)
			box.appendPart(colorIcon);
		foregroundColor = new MiColorOptionMenu();
		foregroundColor.appendCommandHandler(this, CHANGE_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(foregroundColor);

		return(box);
		}

	protected	MiPart		makeLinePatternArea()
		{
		MiBox box = new MiBox("Line Style");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(3);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);
		
		MiLine icon = new MiLine(0,0,40,0);
		icon.setColor(MiColorManager.black);
		solidLineStyleToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, SOLID_LINE_STYLE_NAME);

		icon = new MiLine(0,0,40,0);
		icon.setColor(MiColorManager.black);
		icon.setLineStyle(Mi_DASHED_LINE_STYLE);
		dashedLineStyleToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, DASHED_LINE_STYLE_NAME);

		icon = new MiLine(0,0,40,0);
		icon.setColor(MiColorManager.black);
		icon.setLineStyle(Mi_DOTTED_LINE_STYLE);
		dottedLineStyleToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, DOTTED_LINE_STYLE_NAME);

		//box.setSensitive(false);
		return(box);
		}
	protected	MiPart		makeSampleArea()
		{
		MiBox box = new MiBox("Sample");
		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		container.setBackgroundColor(MiColorManager.white);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setInsetMargins(14);
		container.setLayout(layout);
		sampleLine = new MiLine(0,0,150,0);
		sampleLine.setBackgroundColor(MiColorManager.black);
		sampleLine.setBorderLook(Mi_NONE);
		sampleLine.setPreferredSize(new MiSize(150,100));
		container.appendPart(sampleLine);
		box.appendPart(container);
		return(box);
		}
	protected	void		updateSample()
		{
		sampleLine.setAttributes(gatherAttributes(sampleLine.getAttributes()));
		if ((sampleLine.getBackgroundColor() != null)
			&& (sampleLine.getBackgroundColor().equals(MiColorManager.white)))
			{
			sampleLine.getContainer(0).setBackgroundColor(MiColorManager.veryDarkWhite);
			}
		else
			{
			sampleLine.getContainer(0).setBackgroundColor(MiColorManager.white);
			}
		}
	public		void		processCommand(String cmd)
		{
		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	}
class MiLineEndsPropertyPanel extends MiContainer implements MiiCommandHandler, MiiShapeAttributesPanel
	{
	public static	String			START_END_NAME		= "Line Start";
	public static	String			END_END_NAME		= "Line End";
	public static	String			START_SIZE_NAME		= "Start Size";
	public static	String			END_SIZE_NAME		= "End Size";

	private static final	String		CHANGE_START_NAME	= "changeStart";
	private static final	String		CHANGE_END_NAME		= "changeEnd";
	private static final	String		CHANGE_SIZE_NAME	= "changeSize";

	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		boolean			hasChanged;
	private		boolean			creatingPanel;
	private		MiPart			startEndsArea;
	private		MiPart			endEndsArea;
	private		MiPart			startSizeArea;
	private		MiPart			endSizeArea;
	private		MiPart			sampleArea;
	private		MiComboBox		startSizesCombo;
	private		MiComboBox		endSizesCombo;
	private		MiLine			sampleLine;
	private		MiList			startsList;
	private		MiList			endsList;
	private		String			panelShape;



	public				MiLineEndsPropertyPanel()
		{
		this(MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE);
		}
	public				MiLineEndsPropertyPanel(String panelShape)
		{
		this.panelShape = panelShape;

		creatingPanel = true;

		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			MiColumnLayout column0 = new MiColumnLayout();
			//column0.setElementHSizing(Mi_EXPAND_TO_FILL);
			//column0.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column0.setAlleyVSpacing(ALLEY_SPACING);
			column0.setInsetMargins(INSET_SPACING);
			setLayout(column0);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			MiRowLayout row0 = new MiRowLayout();
			//column0.setElementHSizing(Mi_EXPAND_TO_FILL);
			//column0.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			row0.setAlleyHSpacing(ALLEY_SPACING);
			row0.setInsetMargins(INSET_SPACING - 10);
			setLayout(row0);
			}

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);
		appendPart(rowLayout);

		MiColumnLayout column1 = new MiColumnLayout();
		column1.setElementHSizing(Mi_EXPAND_TO_FILL);
		column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column1.setAlleyVSpacing(ALLEY_SPACING);
		rowLayout.appendPart(column1);

		MiColumnLayout column2 = new MiColumnLayout();
		column2.setElementHSizing(Mi_EXPAND_TO_FILL);
		column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column2.setAlleyVSpacing(ALLEY_SPACING);
		rowLayout.appendPart(column2);
		
		startEndsArea	= makeEndsArea(true);
		endEndsArea	= makeEndsArea(false);
		startSizeArea	= makeStartSizeArea();
		endSizeArea	= makeEndSizeArea();
		sampleArea 	= makeSampleArea();

		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			column1.appendPart(startEndsArea);
			column1.appendPart(startSizeArea);

			column2.appendPart(endEndsArea);
			column2.appendPart(endSizeArea);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			column1.appendPart(startEndsArea);
			column2.appendPart(endEndsArea);

			MiColumnLayout column3 = new MiColumnLayout();
			column3.setElementHSizing(Mi_EXPAND_TO_FILL);
			column3.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column3.setAlleyVSpacing(ALLEY_SPACING);
			rowLayout.appendPart(column3);

			column3.appendPart(startSizeArea);
			column3.appendPart(endSizeArea);
			}

		appendPart(sampleArea);

		startSizesCombo.setValue("10");
		endSizesCombo.setValue("10");
		startsList.selectItem(0);
		endsList.selectItem(0);
		startsList.appendCommandHandler(this, CHANGE_START_NAME, Mi_ITEM_SELECTED_ACTION);
		endsList.appendCommandHandler(this, CHANGE_END_NAME, Mi_ITEM_SELECTED_ACTION);

		creatingPanel = false;
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		displayAttributes(attributes);
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes attributes)
		{
		return(gatherAttributes(attributes));
		}
	public		void		displayAttributes(MiAttributes attributes)
		{
		int startStyle = attributes.getLineStartStyle();
		startsList.selectItem(startStyle);

		int endStyle = attributes.getLineEndStyle();
		endsList.selectItem(endStyle);

		startSizesCombo.setValue("" + attributes.getLineStartSize());
		endSizesCombo.setValue("" + attributes.getLineEndSize());

		sampleLine.setAttributes(attributes);
		hasChanged = false;
		}
	public		MiAttributes	gatherAttributes(MiAttributes attributes)
		{
		attributes = attributes.setLineStartStyle(
			MiiAttributeTypes.lineEndStyles[startsList.getSelectedItemIndex()]);
		attributes = attributes.setLineEndStyle(
			MiiAttributeTypes.lineEndStyles[endsList.getSelectedItemIndex()]);

		double size = Utility.toDouble(startSizesCombo.getValue(), 10);
		attributes = attributes.setLineStartSize(size);

		size = Utility.toDouble(endSizesCombo.getValue(), 10);
		attributes = attributes.setLineEndSize(size);

		return(attributes);
		}

	protected	MiPart		makeEndsArea(boolean startOfLines)
		{
		String label;
		String changeCmd;
		if (startOfLines)
			{
			label = START_END_NAME;
			changeCmd = CHANGE_START_NAME;
			}
		else
			{
			label = END_END_NAME;
			changeCmd = CHANGE_END_NAME;
			}
		MiBox box = new MiBox(label);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		box.setLayout(layout);

		MiList list = new MiList();
		list.getSortManager().setEnabled(false);
		list.getSelectionManager().setMinimumNumberSelected(1);
		list.setPreferredNumberOfVisibleRows(3);
		list.getContentsBackground().setBackgroundColor(MiColorManager.white);
		list.getContentsBackground().setBorderLook(Mi_INLINED_INDENTED_BORDER_LOOK);
		MiScrolledBox scrolledList = new MiScrolledBox(list);
		MiLine line;

		if (startOfLines)
			startsList = list;
		else
			endsList = list;

		for (int i = 0; i < MiiAttributeTypes.lineEndStyles.length; ++i)
			{
			line = new MiLine(0, 0, 30, 0);
			line.setPreferredSize(new MiSize(50, 15));
			if (startOfLines)
				{
				line.setLineStartStyle(MiiAttributeTypes.lineEndStyles[i]);
				}
			else
				{
				line.setLineEndStyle(MiiAttributeTypes.lineEndStyles[i]);
				}
			list.appendItem(line);
			}
		box.appendPart(scrolledList);
		return(box);
		}


	protected	MiPart		makeStartSizeArea()
		{
		MiBox box = new MiBox(START_SIZE_NAME);
		box.setLayout(new MiColumnLayout());

		startSizesCombo = new MiComboBox();
		startSizesCombo.setRestrictingValuesToThoseInList(false);
		startSizesCombo.getList().getSortManager().setEnabled(false);
		startSizesCombo.getList().appendItem("6");
		startSizesCombo.getList().appendItem("8");
		startSizesCombo.getList().appendItem("10");
		startSizesCombo.getList().appendItem("16");
		startSizesCombo.getList().appendItem("24");
		startSizesCombo.getTextField().setNumDisplayedColumns(MiTextField.Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT);
		startSizesCombo.appendCommandHandler(this, CHANGE_SIZE_NAME, Mi_VALUE_CHANGED_ACTION);

		box.appendPart(startSizesCombo);
		return(box);
		}

	protected	MiPart		makeEndSizeArea()
		{
		MiBox box = new MiBox(END_SIZE_NAME);
		box.setLayout(new MiColumnLayout());

		endSizesCombo = new MiComboBox();
		endSizesCombo.setRestrictingValuesToThoseInList(false);
		endSizesCombo.getList().getSortManager().setEnabled(false);
		endSizesCombo.getList().appendItem("6");
		endSizesCombo.getList().appendItem("8");
		endSizesCombo.getList().appendItem("10");
		endSizesCombo.getList().appendItem("16");
		endSizesCombo.getList().appendItem("24");
		endSizesCombo.getTextField().setNumDisplayedColumns(MiTextField.Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT);
		endSizesCombo.appendCommandHandler(this, CHANGE_SIZE_NAME, Mi_VALUE_CHANGED_ACTION);

		box.appendPart(endSizesCombo);
		return(box);
		}

	protected	MiPart		makeSampleArea()
		{
		MiBox box = new MiBox("Sample");
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(columnLayout);

		MiVisibleContainer container = new MiVisibleContainer();
//		container.setPreferredSize(new MiSize(50,40));
		container.setPreferredSize(new MiSize(120,40));
		container.setBorderLook(Mi_INLINED_INDENTED_BORDER_LOOK);
		container.setBackgroundColor(MiColorManager.white);
		container.setVisibleContainerAutomaticLayoutEnabled(false);
		container.setInvalidLayoutNotificationsEnabled(false);

		sampleLine = new MiLine(0,0,100,0);
/*
		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			sampleLine = new MiLine(0,0,100,0);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			sampleLine = new MiLine(0,0,0,100);
			}
*/
		sampleLine.setBackgroundColor(MiColorManager.black);
		//sampleLine.setBorderLook(Mi_NONE);
		//sampleLine.setPreferredSize(new MiSize(50,0));
		//sampleLine.setLineEndsRenderer(MiAttributes.defaultLineEndsRenderer);

		MiPolyConstraint polyLayout = new MiPolyConstraint();
		polyLayout.appendConstraint(new MiRelativeLocationConstraint(
				sampleLine, MiRelativeLocationConstraint.CENTER_OF, box));
		sampleLine.setLayout(polyLayout);

		container.appendAttachment(sampleLine);
		box.appendPart(container);
		return(box);
		}
	protected	void		updateSample()
		{
		sampleLine.setAttributes(gatherAttributes(sampleLine.getAttributes()));
		}
	public		void		processCommand(String cmd)
		{
		if (creatingPanel)
			return;

		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	}

class MiShadowPropertyPanel extends MiContainer implements MiiCommandHandler, MiiShapeAttributesPanel
	{
	public static	String			NO_SHADOW_NAME		= "No Shadow";
	public static	String			BASIC_SHADOW_NAME	= "Basic";
	public static	String			THREE_D_SHADOW_NAME	= "3D";
	public static	String			SOFT_SHADOW_NAME	= "Soft";

	private	static final	String		CHANGE_SHADOW_COLOR_NAME= "changeShadowColor";
	private	static final	String		CHANGE_BLEND_COLOR_NAME	= "changeBlendColor";
	private	static final	String		CHANGE_DEPTH_NAME	= "changeDepth";
	private	static final	String		CHANGE_LOCATION_NAME	= "changeLocation";
	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		boolean			hasChanged;
	private		MiPart			shadowStyleArea;
	private		MiPart			shadowColorArea;
	//private		MiPart			blendColorArea;
	private		MiPart			shadowLocationArea;
	private		MiPart			shadowDepthArea;
	private		MiPart			sampleArea;
	private		MiWidget		noShadowToggle;
	private		MiWidget		basicShadowToggle;
	private		MiWidget		threeDShadowToggle;
	private		MiWidget		softShadowToggle;
	private		MiColorOptionMenu	shadowColor;
	//private		MiColorOptionMenu	blendColor;
	private		MiSlider		shadowDepth;
	private		MiToggleButton		ulDirToggle;
	private		MiToggleButton		urDirToggle;
	private		MiToggleButton		llDirToggle;
	private		MiToggleButton		lrDirToggle;
	private		MiRectangle		sampleRectangle;
	private		MiImage			colorIcon;
	private		String			panelShape;



	public				MiShadowPropertyPanel()
		{
		this(MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE);
		}
	public				MiShadowPropertyPanel(String panelShape)
		{
		this.panelShape = panelShape;

		String colorIconName = MiSystem.getProperty("Mi_COLOR_ICON_NAME");
		if (colorIconName != null)
			colorIcon = new MiImage(colorIconName);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setInsetMargins(INSET_SPACING);
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);
		setLayout(rowLayout);

		MiLayout column1 = null;
		MiLayout column2 = null;

//		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			column1 = new MiColumnLayout();
			column1.setElementHSizing(Mi_EXPAND_TO_FILL);
			column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column1.setAlleyVSpacing(ALLEY_SPACING);
			appendPart(column1);

			column2 = new MiColumnLayout();
			column2.setElementHSizing(Mi_EXPAND_TO_FILL);
			column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column2.setAlleyVSpacing(ALLEY_SPACING);
			appendPart(column2);
			}
/*
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			column1 = new MiColumnLayout();
			column1.setElementHSizing(Mi_EXPAND_TO_FILL);
			column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
			column1.setAlleyVSpacing(ALLEY_SPACING - 10);
			}
*/
		
		shadowStyleArea = makeShadowStyleArea();
		shadowColorArea = makeShadowColorArea();
		//blendColorArea = makeBlendColorArea();
		shadowLocationArea = makeShadowLocationArea();
		shadowDepthArea = makeShadowDepthArea();
		sampleArea = makeSampleArea();

		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			column1.appendPart(shadowStyleArea);
			column1.appendPart(shadowColorArea);
			//column1.appendPart(blendColorArea);

			column2.appendPart(shadowLocationArea);
			noShadowToggle.select(true);
			lrDirToggle.select(true);
			shadowDepth.setCurrentValue(4);
			shadowColor.setColorValue(MiColorManager.gray);
			column2.appendPart(shadowDepthArea);
			column2.appendPart(sampleArea);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			column1.setAlleyVSpacing(ALLEY_SPACING - 10);
			column2.setAlleyVSpacing(ALLEY_SPACING - 10);
			rowLayout.setInsetMargins(INSET_SPACING - 10);

			column1.appendPart(shadowStyleArea);
			column1.appendPart(shadowDepthArea);
			//column1.appendPart(blendColorArea);

			column2.appendPart(shadowLocationArea);
			noShadowToggle.select(true);
			lrDirToggle.select(true);
			shadowDepth.setCurrentValue(4);
			shadowColor.setColorValue(MiColorManager.gray);
			column2.appendPart(shadowColorArea);
			appendPart(sampleArea);
			}
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		displayAttributes(attributes);
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes attributes)
		{
		return(gatherAttributes(attributes));
		}
	public		void		displayAttributes(MiAttributes attributes)
		{
		if (!attributes.getHasShadow())
			{
			noShadowToggle.select(true);
			//blendColorArea.setVisible(false);
			shadowColorArea.setSensitive(false);
			shadowLocationArea.setSensitive(false);
			shadowDepthArea.setSensitive(false);
			}
		else
			{
			shadowColorArea.setSensitive(true);
			shadowLocationArea.setSensitive(true);
			shadowDepthArea.setSensitive(true);
/*
			if (shadowRenderer.getShadowStyle() == MiiShadowRenderer.Mi_BASIC_SHADOW_STYLE)
				{
				softShadowToggle.select(true);
				blendColorArea.setVisible(false);
				blendColor.setColorValue(shadowRenderer.getShadowBlendColor());
				}
			else if (shadowRenderer.getShadowStyle() == MiiShadowRenderer.Mi_THREE_D_SHADOW_STYLE)
				{
				threeDShadowToggle.select(true);
				blendColorArea.setVisible(false);
				}
			else
*/
				{
				basicShadowToggle.select(true);
				//blendColorArea.setVisible(true);
				}
			shadowColor.setColorValue(attributes.getShadowColor());
			//blendColor.setColorValue(attributes.getShadowColor());
			shadowDepth.setCurrentValue(attributes.getShadowLength());
			}
			
		switch (attributes.getShadowDirection())
			{
			case Mi_LOWER_LEFT_LOCATION :
				llDirToggle.select(true);
				break;
			case Mi_UPPER_LEFT_LOCATION :
				ulDirToggle.select(true);
				break;
			case Mi_LOWER_RIGHT_LOCATION :
				lrDirToggle.select(true);
				break;
			case Mi_UPPER_RIGHT_LOCATION :
				urDirToggle.select(true);
				break;
			}
		//updateSample();
		sampleRectangle.setAttributes(attributes);
		hasChanged = false;
		}
	public		MiAttributes	gatherAttributes(MiAttributes attributes)
		{
		if (noShadowToggle.isSelected())
			{
			attributes = attributes.setHasShadow(false);
			}
		else
			{
			attributes = attributes.setHasShadow(true);

/*
			if (basicShadowToggle.isSelected())
				{
				shadowRenderer.setShadowStyle(MiiShadowRenderer.Mi_BASIC_SHADOW_STYLE);
				}
			else if (threeDShadowToggle.isSelected())
				{
				shadowRenderer.setShadowStyle(MiiShadowRenderer.Mi_THREE_D_SHADOW_STYLE);
				}
			else if (softShadowToggle.isSelected())
				{
				shadowRenderer.setShadowStyle(MiiShadowRenderer.Mi_SOFT_SHADOW_STYLE);
				}
*/
			if (ulDirToggle.isSelected())
				attributes = attributes.setShadowDirection(Mi_UPPER_LEFT_LOCATION);
			else if (urDirToggle.isSelected())
				attributes = attributes.setShadowDirection(Mi_UPPER_RIGHT_LOCATION);
			else if (llDirToggle.isSelected())
				attributes = attributes.setShadowDirection(Mi_LOWER_LEFT_LOCATION);
			else if (lrDirToggle.isSelected())
				attributes = attributes.setShadowDirection(Mi_LOWER_RIGHT_LOCATION);

			attributes = attributes.setShadowLength(shadowDepth.getCurrentValue());
			attributes = attributes.setShadowColor(shadowColor.getColorValue());
			//attributes.setShadowBlendColor(blendColor.getColorValue());
			}
		return(attributes);
		}

	protected	MiPart		makeShadowStyleArea()
		{
		MiBox box = new MiBox("Shadow Style");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(3);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);

		MiPart icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setHasShadow(false);
		noShadowToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, NO_SHADOW_NAME);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		//icon.setShadowStyle(MiiShadowRenderer.Mi_BASIC_SHADOW_STYLE);
		icon.setHasShadow(true);
		basicShadowToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, BASIC_SHADOW_NAME);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		//icon.setShadowStyle(MiiShadowRenderer.Mi_THREE_D_SHADOW_STYLE);
		icon.setHasShadow(true);
		threeDShadowToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, THREE_D_SHADOW_NAME);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setHasShadow(true);
		//icon.setShadowStyle(MiiShadowRenderer.Mi_SOFT_SHADOW_STYLE);
		softShadowToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, radioEnforcer, icon, SOFT_SHADOW_NAME);

		return(box);
		}
	protected	MiPart		makeShadowColorArea()
		{
		MiBox box = new MiBox("Shadow Color");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(rowLayout);

		if (colorIcon != null)
			box.appendPart(colorIcon.copy());
		shadowColor = new MiColorOptionMenu();
		shadowColor.appendCommandHandler(this, CHANGE_SHADOW_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(shadowColor);

		return(box);
		}

/*
	protected	MiPart		makeBlendColorArea()
		{
		MiBox box = new MiBox("Blend Color");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(rowLayout);

		if (colorIcon != null)
			box.appendPart(colorIcon.copy());
		blendColor = new MiColorOptionMenu();
		blendColor.appendCommandHandler(this, CHANGE_BLEND_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(blendColor);

		return(box);
		}
*/

	protected	MiPart		makeShadowLocationArea()
		{
		MiBox box = new MiBox("Shadow Direction");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementHJustification(Mi_JUSTIFIED);
		box.setLayout(rowLayout);

		MiRectangle icon;
		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setShadowDirection(Mi_LOWER_RIGHT_LOCATION);
		icon.setHasShadow(true);
		lrDirToggle = new MiToggleButton();
		lrDirToggle.appendCommandHandler(this, CHANGE_LOCATION_NAME);
		lrDirToggle.setCellMargins(5);
		lrDirToggle.setRadioStateEnforcer(radioEnforcer);
		lrDirToggle.setSelectedBackgroundColor(MiColorManager.white);
		lrDirToggle.appendPart(icon);
		box.appendPart(lrDirToggle);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setShadowDirection(Mi_LOWER_LEFT_LOCATION);
		icon.setHasShadow(true);
		llDirToggle = new MiToggleButton();
		llDirToggle.appendCommandHandler(this, CHANGE_LOCATION_NAME);
		llDirToggle.setCellMargins(5);
		llDirToggle.setRadioStateEnforcer(radioEnforcer);
		llDirToggle.setSelectedBackgroundColor(MiColorManager.white);
		llDirToggle.appendPart(icon);
		box.appendPart(llDirToggle);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setShadowDirection(Mi_UPPER_LEFT_LOCATION);
		icon.setHasShadow(true);
		ulDirToggle = new MiToggleButton();
		ulDirToggle.appendCommandHandler(this, CHANGE_LOCATION_NAME);
		ulDirToggle.setCellMargins(5);
		ulDirToggle.setRadioStateEnforcer(radioEnforcer);
		ulDirToggle.setSelectedBackgroundColor(MiColorManager.white);
		ulDirToggle.appendPart(icon);
		box.appendPart(ulDirToggle);

		icon  = new MiRectangle(0,0,20,15);
		icon.setBackgroundColor(MiColorManager.cyan);
		icon.setHasShadow(true);
		icon.setShadowDirection(Mi_UPPER_RIGHT_LOCATION);
		urDirToggle = new MiToggleButton();
		urDirToggle.appendCommandHandler(this, CHANGE_LOCATION_NAME);
		urDirToggle.setCellMargins(5);
		urDirToggle.setRadioStateEnforcer(radioEnforcer);
		urDirToggle.setSelectedBackgroundColor(MiColorManager.white);
		urDirToggle.appendPart(icon);
		box.appendPart(urDirToggle);

		return(box);
		}
	protected	MiPart		makeShadowDepthArea()
		{
		MiBox box = new MiBox("Shadow Length");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(0);
		box.setLayout(rowLayout);
		shadowDepth = new MiSlider();
		shadowDepth.appendCommandHandler(this, CHANGE_DEPTH_NAME, Mi_VALUE_CHANGED_ACTION);
		shadowDepth.setMaximumValue(20);
		box.appendPart(shadowDepth);
		return(box);
		}
	protected	MiPart		makeSampleArea()
		{
		MiBox box = new MiBox("Sample");
		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		container.setBackgroundColor(MiColorManager.white);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setInsetMargins(30);
		container.setLayout(layout);
		sampleRectangle = new MiRectangle(0,0,150,40);
		sampleRectangle.setPreferredSize(new MiSize(150,40));
		sampleRectangle.setColor(MiColorManager.cyan);
		sampleRectangle.setBackgroundColor(MiColorManager.cyan);
		container.appendPart(sampleRectangle);
		box.appendPart(container);
		return(box);
		}
	protected	void		updateSample()
		{
		sampleRectangle.setAttributes(gatherAttributes(sampleRectangle.getAttributes()));
		}
	public		void		processCommand(String cmd)
		{
/*
		if (softShadowToggle.isSelected())
			blendColorArea.setVisible(true);
		else
			blendColorArea.setVisible(false);
*/
		if (noShadowToggle.isSelected())
			{
			shadowColorArea.setSensitive(false);
			shadowLocationArea.setSensitive(false);
			shadowDepthArea.setSensitive(false);
			}
		else
			{
			shadowColorArea.setSensitive(true);
			shadowLocationArea.setSensitive(true);
			shadowDepthArea.setSensitive(true);
			}
		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	}
class MiBorderPropertyPanel extends MiContainer implements MiiCommandHandler, MiiShapeAttributesPanel, MiiNames
	{
	private			MiPart[]	borderStyleToggles	= new MiPart[borderLookNames.length];

	private	static final	String		WHITE_BORDER_COLOR_NAME	= "White color";
	private	static final	String		LIGHT_BORDER_COLOR_NAME	= "Light color";
	private	static final	String		DARK_BORDER_COLOR_NAME	= "Dark color";
	private	static final	String		BLACK_BORDER_COLOR_NAME	= "Black color";
	private	static final	String		CHANGE_BORDER_WIDTH_NAME= "changeBorderWidth";

	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		boolean			hasChanged;
	private		MiPart			borderStyleArea;
	private		MiPart			borderColorsArea;
	private		MiPart			borderWidthArea;
	private		MiPart			sampleArea;

	private		MiColorOptionMenu	whiteBorderColor;
	private		MiColorOptionMenu	lightBorderColor;
	private		MiColorOptionMenu	darkBorderColor;
	private		MiColorOptionMenu	blackBorderColor;
	private		MiSlider		borderWidth;

	private		MiRectangle		sampleRectangle;
	private		String			panelShape;



	public				MiBorderPropertyPanel()
		{
		this(MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE);
		}
	public				MiBorderPropertyPanel(String panelShape)
		{
		this.panelShape = panelShape;

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setInsetMargins(INSET_SPACING);
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);
		setLayout(rowLayout);

		MiColumnLayout column1 = new MiColumnLayout();
		column1.setElementHSizing(Mi_EXPAND_TO_FILL);
		column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column1.setAlleyVSpacing(ALLEY_SPACING);
		appendPart(column1);
		MiColumnLayout column2 = new MiColumnLayout();
		column2.setElementHSizing(Mi_EXPAND_TO_FILL);
		column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column2.setAlleyVSpacing(ALLEY_SPACING);
		appendPart(column2);
		
		borderStyleArea = makeBorderStyleArea();
		borderColorsArea = makeBorderColorsArea();
		borderWidthArea = makeBorderWidthArea();
		sampleArea = makeSampleArea();

		// FOR NOW, cause we do not support border width
		borderWidthArea.setVisible(false);

		column1.appendPart(borderStyleArea);

		column2.appendPart(borderColorsArea);
		column2.appendPart(borderWidthArea);
		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			column2.appendPart(sampleArea);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			appendPart(sampleArea);
			}
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		displayAttributes(attributes);
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes attributes)
		{
		return(gatherAttributes(attributes));
		}
	public		void		displayAttributes(MiAttributes attributes)
		{
		int borderLook = attributes.getBorderLook();

		if (attributes.getBorderLook() < borderStyleToggles.length)
			borderStyleToggles[borderLook].select(true);
		else
			borderStyleToggles[0].select(true);

		whiteBorderColor.setColorValue(attributes.getWhiteColor());
		lightBorderColor.setColorValue(attributes.getLightColor());
		darkBorderColor.setColorValue(attributes.getDarkColor());
		blackBorderColor.setColorValue(attributes.getBlackColor());

		// borderWidth.setCurrentValue(attributes.getBorderWidth());

		sampleRectangle.setAttributes(attributes);
		hasChanged = false;
		//updateSample();
		}
	public		MiAttributes	gatherAttributes(MiAttributes attributes)
		{
		for (int i = 0; i < borderStyleToggles.length; ++i)
			{
			if (borderStyleToggles[i].isSelected())
				{
				attributes = attributes.setBorderLook(i);
				break;
				}
			}
		attributes = attributes.setWhiteColor(whiteBorderColor.getColorValue());
		attributes = attributes.setLightColor(lightBorderColor.getColorValue());
		attributes = attributes.setDarkColor(darkBorderColor.getColorValue());
		attributes = attributes.setBlackColor(blackBorderColor.getColorValue());

		//attributes = attributes.setBorderWidth(borderWidth.getCurrentValue());

		// attributes.setBorderWidth(borderWidth.getValue());
		return(attributes);
		}

	protected	MiPart		makeBorderStyleArea()
		{
		MiBox box = new MiBox("Border Style");
		MiGridLayout gridLayout = new MiGridLayout();
		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			gridLayout.setNumberOfColumns(3);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			gridLayout.setNumberOfColumns(6);
			}
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiRadioStateEnforcer radioEnforcer = new MiRadioStateEnforcer();
		radioEnforcer.setMinNumSelected(1);

		for (int i = 0; i < borderLookNames.length; ++i)
			{
			MiPart icon  = new MiRectangle(0,0,20,15);
			icon.setBorderLook(i);
			borderStyleToggles[i] 
				= MiShapeAttributesPanel.makeIconToggleLabel(
					this, 
					box, 
					radioEnforcer, 
					icon, 
					borderLookNames[i]);
			}

		return(box);
		}
	protected	MiPart		makeBorderColorsArea()
		{
		MiBox box = new MiBox("Border Colors");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		MiText text = new MiText(WHITE_BORDER_COLOR_NAME);
		box.appendPart(text);
		whiteBorderColor = new MiColorOptionMenu();
		whiteBorderColor.appendCommandHandler(this, WHITE_BORDER_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(whiteBorderColor);

		text = new MiText(LIGHT_BORDER_COLOR_NAME);
		box.appendPart(text);
		lightBorderColor = new MiColorOptionMenu();
		lightBorderColor.appendCommandHandler(this, LIGHT_BORDER_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(lightBorderColor);

		text = new MiText(DARK_BORDER_COLOR_NAME);
		box.appendPart(text);
		darkBorderColor = new MiColorOptionMenu();
		darkBorderColor.appendCommandHandler(this, DARK_BORDER_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(darkBorderColor);

		text = new MiText(BLACK_BORDER_COLOR_NAME);
		box.appendPart(text);
		blackBorderColor = new MiColorOptionMenu();
		blackBorderColor.appendCommandHandler(this, BLACK_BORDER_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(blackBorderColor);

		return(box);
		}

	protected	MiPart		makeBorderWidthArea()
		{
		MiBox box = new MiBox("Border Width");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(0);
		box.setLayout(rowLayout);

		borderWidth = new MiSlider();
		borderWidth.appendCommandHandler(this, CHANGE_BORDER_WIDTH_NAME, Mi_VALUE_CHANGED_ACTION);
		borderWidth.setMaximumValue(20);
		box.appendPart(borderWidth);

		return(box);
		}

	protected	MiPart		makeSampleArea()
		{
		MiBox box = new MiBox("Sample");
		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(container, MiiToolkit.Mi_TOOLKIT_WINDOW_ATTRIBUTES);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setInsetMargins(14);
		container.setLayout(layout);
		sampleRectangle = new MiRectangle(0,0,150,40);
		sampleRectangle.setPreferredSize(new MiSize(150,40));
		container.appendPart(sampleRectangle);
		box.appendPart(container);
		return(box);
		}
	protected	void		updateSample()
		{
		sampleRectangle.setAttributes(gatherAttributes(sampleRectangle.getAttributes()));
		}
	public		void		processCommand(String cmd)
		{
		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	}
class MiTextPropertyPanel extends MiContainer implements MiiCommandHandler, MiiShapeAttributesPanel
	{
	private	static final	String		CHANGE_BOLD_STYLE_NAME		= "Bold";
	private	static final	String		CHANGE_ITALIC_STYLE_NAME	= "Italic";
	private	static final	String		CHANGE_UNDERLINE_STYLE_NAME	= "Underline";
	private	static final	String		CHANGE_STRIKEOUT_STYLE_NAME	= "Strikeout";

	private	static final	String		CHANGE_FONT_NAME	= "changeFont";
	private	static final	String		CHANGE_SIZE_NAME	= "changeSize";
	private	static final	String		CHANGE_BACKCOLOR_NAME	= "changeBGColor";
	private	static final	String		CHANGE_COLOR_NAME	= "changeColor";
	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		boolean			hasChanged;
	private		MiPart			fontArea;
	private		MiPart			sizeArea;
	private		MiPart			styleArea;
	private		MiPart			foregroundColorArea;
	private		MiPart			backgroundColorArea;
	private		MiPart			sampleArea;
	private		MiComboList 		fontList;
	private		MiComboList 		sizeList;
	private		MiWidget		boldToggle;
	private		MiWidget		italicToggle;
	private		MiWidget		underLineToggle;
	private		MiWidget		strikeOutToggle;
	private		MiColorOptionMenu	foregroundColor;
	private		MiColorOptionMenu	backgroundColor;
	private		MiText			sampleText;
	private		Strings			listOfFonts;
	private		Strings			listOfSizes;
	private		String			panelShape;



	public				MiTextPropertyPanel()
		{
		this(MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE);
		}
	public				MiTextPropertyPanel(String panelShape)
		{
		this.panelShape = panelShape;

		listOfFonts = new Strings(MiFontManager.getFontList());
		listOfSizes = new Strings();
		for (int i = 6; i < 16; ++i)
			listOfSizes.addElement(Utility.toString(i));

		listOfSizes.addElement("18");
		listOfSizes.addElement("20");
		listOfSizes.addElement("24");
		listOfSizes.addElement("32");
		listOfSizes.addElement("40");
		listOfSizes.addElement("48");
		listOfSizes.addElement("56");
		listOfSizes.addElement("64");

		if (panelShape == MiShapeAttributesPanel.SQUARISH_PANEL_SHAPE)
			{
			MiColumnLayout columnLayout = new MiColumnLayout();
			columnLayout.setInsetMargins(INSET_SPACING);
			columnLayout.setAlleyHSpacing(ALLEY_SPACING);
			setLayout(columnLayout);
			}
		else if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			MiRowLayout mainLayout = new MiRowLayout();
			mainLayout.setInsetMargins(INSET_SPACING - 10);
			mainLayout.setAlleyHSpacing(ALLEY_SPACING);
			setLayout(mainLayout);
			}

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);
		appendPart(rowLayout);

/*
		MiColumnLayout column1 = new MiColumnLayout();
		column1.setElementHSizing(Mi_EXPAND_TO_FILL);
		column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column1.setUniqueElementIndex(0);
		column1.setAlleyVSpacing(ALLEY_SPACING);
		appendPart(column1);
		MiColumnLayout column2 = new MiColumnLayout();
		column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column2.setElementHSizing(Mi_EXPAND_TO_FILL);
		column2.setAlleyVSpacing(ALLEY_SPACING);
		appendPart(column2);
*/
		
		fontArea = makeFontArea();
		//foregroundColorArea = makeForeGroundColorArea();
		//backgroundColorArea = makeBackGroundColorArea();
		sizeArea = makeSizeArea();
		styleArea = makeStyleArea();
		sampleArea = makeSampleArea();

		rowLayout.appendPart(fontArea);
		//column1.appendPart(foregroundColorArea);
		//column1.appendPart(backgroundColorArea);

		rowLayout.appendPart(sizeArea);
		rowLayout.appendPart(styleArea);
		appendPart(sampleArea);

		sizeList.setValue("12");
		fontList.setValue(listOfFonts.elementAt(0));
		//backgroundColor.setColorValue(Mi_TRANSPARENT_COLOR);
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		displayAttributes(attributes);
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes attributes)
		{
		return(gatherAttributes(attributes));
		}
	public		void		displayAttributes(MiAttributes attributes)
		{
		MiFont font = attributes.getFont();
		fontList.setValue(font.getName());

		// ---------------------------------------------------------------
		// See if the current font was found in the list of fonts provided by
		// AWT, which is no longer an exhaustive list in 1.2.x
		// ---------------------------------------------------------------
		int index = fontList.getList().getIndexOfItem(font.getName());
		if (index >= 0)
			fontList.getList().makeVisible(index);

		sizeList.setValue(font.getPointSize() + "");

		boldToggle.select(font.isBold());
		italicToggle.select(font.isItalic());
		underLineToggle.select(font.isUnderlined());
		strikeOutToggle.select(font.isStrikeOut());

		//foregroundColor.setColorValue(attributes.getColor());
		//backgroundColor.setColorValue(attributes.getBackgroundColor());

		sampleText.setAttributes(attributes);
		hasChanged = false;
		//updateSample();
		}
	public		MiAttributes	gatherAttributes(MiAttributes attributes)
		{
		String fontName = fontList.getValue();
		int size = Utility.toInteger(sizeList.getValue());
		int style = MiFont.PLAIN;
		if (boldToggle.isSelected())
			style |= MiFont.BOLD;
		if (italicToggle.isSelected())
			style |= MiFont.ITALIC;
		if (underLineToggle.isSelected())
			style |= MiFont.UNDERLINE;
		if (strikeOutToggle.isSelected())
			style |= MiFont.STRIKEOUT;
		MiFont font = new MiFont(fontName, style, size);

		attributes = attributes.setFont(font);
		//attributes = attributes.setColor(foregroundColor.getColorValue());
		//attributes = attributes.setBackgroundColor(backgroundColor.getColorValue());
		return(attributes);
		}

	protected	MiPart		makeFontArea()
		{
		MiBox box = new MiBox("Font");
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(columnLayout);

		fontList = new MiComboList();
		fontList.setContents(listOfFonts);
		fontList.appendCommandHandler(this, CHANGE_SIZE_NAME, Mi_VALUE_CHANGED_ACTION);
		fontList.getList().setMinimumNumberOfVisibleRows(3);
		fontList.getList().setPreferredNumberOfVisibleRows(3);
		fontList.getList().setMaximumNumberOfVisibleRows(10);
		fontList.getList().getSelectionManager().setMinimumNumberSelected(1);
		box.appendPart(fontList);

		return(box);
		}

/****
	protected	MiPart		makeForeGroundColorArea()
		{
		MiBox box = new MiBox("Foreground Color");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(rowLayout);

		if (colorIcon != null)
			box.appendPart(colorIcon.copy());
		foregroundColor = new MiColorOptionMenu();
		foregroundColor.appendCommandHandler(this, CHANGE_COLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(foregroundColor);

		return(box);
		}

	protected	MiPart		makeBackGroundColorArea()
		{
		MiBox box = new MiBox("Background Color");
		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(rowLayout);

		if (colorIcon != null)
			box.appendPart(colorIcon.copy());
		backgroundColor = new MiColorOptionMenu();
		backgroundColor.appendCommandHandler(this, CHANGE_BACKCOLOR_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(backgroundColor);

		return(box);
		}
****/

	protected	MiPart		makeSizeArea()
		{
		MiBox box = new MiBox("Size");
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(columnLayout);

		sizeList = new MiComboList();
		sizeList.getList().getSortManager().setEnabled(false);
		sizeList.setContents(listOfSizes);
		sizeList.appendCommandHandler(this, CHANGE_SIZE_NAME, Mi_VALUE_CHANGED_ACTION);
		sizeList.getList().setMinimumNumberOfVisibleRows(3);
		sizeList.getList().setPreferredNumberOfVisibleRows(3);
		sizeList.getList().setMaximumNumberOfVisibleRows(10);
		sizeList.getList().getSelectionManager().setMinimumNumberSelected(1);
		box.appendPart(sizeList);

		return(box);
		}
	protected	MiPart		makeStyleArea()
		{
		MiBox box = new MiBox("Style");
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		gridLayout.setGridVJustification(Mi_JUSTIFIED);
		box.setLayout(gridLayout);

		boldToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_BOLD_STYLE_NAME);

		italicToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_ITALIC_STYLE_NAME);

		underLineToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_UNDERLINE_STYLE_NAME);

		strikeOutToggle = MiShapeAttributesPanel.makeIconToggleLabel(
			this, box, null, null, CHANGE_STRIKEOUT_STYLE_NAME);

		return(box);
		}
	protected	MiPart		makeSampleArea()
		{
		MiBox box = new MiBox("Sample");
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(columnLayout);

		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		container.setBackgroundColor(MiColorManager.white);
		container.setVisibleContainerAutomaticLayoutEnabled(false);
		container.setInvalidLayoutNotificationsEnabled(false);
		container.setPreferredSize(new MiSize(300,80));

//		MiColumnLayout layout = new MiColumnLayout();
//		layout.setInsetMargins(14);
//		container.setLayout(layout);

		sampleText = new MiText("abcdefghijklmnopqrstuvwxyz\nABCDEFGHIJKLMNOPQRSTUVWXYZ\n1234567890");
		sampleText.setFontHorizontalJustification(Mi_CENTER_JUSTIFIED);
		MiPolyConstraint polyLayout = new MiPolyConstraint();
		//polyLayout.appendLayout(columnLayout);
		polyLayout.appendConstraint(new MiRelativeLocationConstraint(
				sampleText, MiRelativeLocationConstraint.CENTER_OF, box));
		sampleText.setLayout(polyLayout);

		container.appendAttachment(sampleText);
		box.appendPart(container);
		return(box);
		}
	protected	void		updateSample()
		{
		sampleText.setAttributes(gatherAttributes(sampleText.getAttributes()));
		}
	public		void		processCommand(String cmd)
		{
//MiDebug.println("TextPropertyPanel: processCommand: " + cmd);
		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	}
