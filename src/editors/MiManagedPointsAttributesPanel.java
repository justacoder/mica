
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


/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiManagedPointsAttributesPanel 
				extends MiVisibleContainer 
				implements MiiActionHandler, MiiActionTypes, MiiShapeInspectorPanel
	{
	public static final int		Mi_ANNOTATION_POINTS_PANEL_INDEX		= 0;
	public static final int		Mi_CONNECTION_POINTS_PANEL_INDEX		= 1;


	private		MiNativeWindow	parentOfPopupDialogs;
	private		MiTabbedFolder	tabbedFolder;
	private		MiPart		annotationPointsAttributesPanel;
	private		MiPart		connectionPointsAttributesPanel;
	private		boolean		settingDisplayedAttributes;
	private		MiGeneralAttributesDialog	generalAttributesPopup;


	public				MiManagedPointsAttributesPanel(MiNativeWindow parentOfPopupDialogs)
		{
		this.parentOfPopupDialogs = parentOfPopupDialogs;
		generalAttributesPopup = new MiGeneralAttributesDialog(parentOfPopupDialogs, true);

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);
		tabbedFolder = new MiTabbedFolder();
		makePanels(tabbedFolder);
		appendPart(tabbedFolder);
		}

	public		MiTabbedFolder	getTabbedFolder()
		{
		return(tabbedFolder);
		}

	public		MiNativeWindow	getParentOfPopupDialogs()
		{
		return(parentOfPopupDialogs);
		}

	protected	void		makePanels(MiTabbedFolder tabbedFolder)
		{
		annotationPointsAttributesPanel = makeAnnotationPointsAttributesPanel();
		tabbedFolder.appendFolder("Annotation Points", annotationPointsAttributesPanel);
		annotationPointsAttributesPanel.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);

		connectionPointsAttributesPanel = makeConnectionPointsAttributesPanel();
		tabbedFolder.appendFolder("Connection Points", connectionPointsAttributesPanel);
		connectionPointsAttributesPanel.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		}
	public		void		setTargetShape(MiPart target)
		{
		((MiiShapeInspectorPanel )annotationPointsAttributesPanel).setTargetShape(target);
		((MiiShapeInspectorPanel )connectionPointsAttributesPanel).setTargetShape(target);
		}
	public		void		applyToTargetShape(MiPart target)
		{
		((MiiShapeInspectorPanel )annotationPointsAttributesPanel).applyToTargetShape(target);
		((MiiShapeInspectorPanel )connectionPointsAttributesPanel).applyToTargetShape(target);
		}
	public		void		applyToGlobalDefaults()
		{
		((MiAnnotationPointsAttributesPanel )annotationPointsAttributesPanel).applyToGlobalDefaults();
		((MiConnectionPointsAttributesPanel )connectionPointsAttributesPanel).applyToGlobalDefaults();
		}

	protected	MiPart		makeAnnotationPointsAttributesPanel()
		{
		return(new MiAnnotationPointsAttributesPanel(generalAttributesPopup));
		}
	protected	MiPart		makeConnectionPointsAttributesPanel()
		{
		return(new MiConnectionPointsAttributesPanel(generalAttributesPopup));
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_VALUE_CHANGED_ACTION) && (!settingDisplayedAttributes))
			{
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		return(true);
		}
	}
abstract class MiAllManagedPointsAttributesPanel
			extends MiContainer 
			implements MiiCommandHandler, MiiShapeInspectorPanel, MiiLookProperties, 
				MiiCommandNames, MiiActionHandler, MiiManagedPointTypes
	{
	private	static final	String	UPDATE_FROM_PANEL_CMD_NAME		= "updateFromPanel";
	private	static final	String	UPDATE_RECT_POINT_LOCATIONS_CMD_NAME	= "updateRectLocations";
/*
	private	static final	String	Mi_LOOK_AREA_GLOBAL_TITLE_NAME		= "Default Look of Points";
	private	static final	String	Mi_LOOK_AREA_SELECTED_SHAPES_TITLE_NAME	= "Default Look of Selected Shapes";
	private	static final	String	Mi_LOOK_AREA_SELECTED_POINTS_TITLE_NAME	= "Look of Selected Points";

	private	static final	String	Mi_LOOK_AREA_GLOBAL_TITLE_NAME		= "Point Defaults";
	private	static final	String	Mi_LOOK_AREA_SELECTED_SHAPES_TITLE_NAME	= "Selected Shape(s) Point Defaults";
	private	static final	String	Mi_LOOK_AREA_SELECTED_POINTS_TITLE_NAME	= "Selected Point(s) Defaults";
*/
	private	static final	String	Mi_LOOK_AREA_GLOBAL_TITLE_NAME		= "Set System-Wide Defaults";
	private	static final	String	Mi_LOOK_AREA_SELECTED_SHAPES_TITLE_NAME	= "Set Selected Shape(s) Defaults";
	private	static final	String	Mi_LOOK_AREA_SELECTED_POINTS_TITLE_NAME	= "Customize Selected Point(s)";

	protected	MiAttributes	promptAppearanceAttributes;
	protected	MiAttributes	lookAppearanceAttributes;
	protected	MiAttributes	contentAppearanceAttributes;
	protected	MiPart		originalLook;

	private		String		UPDATE_CONTENT_ATTRIBUTES_FROM_POPUP_CMD_NAME	= "updateContentAttrs";
	private		String		UPDATE_PROMPT_ATTRIBUTES_FROM_POPUP_CMD_NAME	= "updatePromptAttrs";
	private		String		UPDATE_LOOK_ATTRIBUTES_FROM_POPUP_CMD_NAME	= "updateLookAttrs";

	private		String		Mi_POPUP_LOOK_APPEARANCE_DIALOG_BOX_CMD_NAME	= "popupLookAttrs";
	private		String		Mi_POPUP_PROMPT_APPEARANCE_DIALOG_BOX_CMD_NAME	= "popupPromptAttrs";
	private		String		Mi_POPUP_CONTENT_APPEARANCE_DIALOG_BOX_CMD_NAME	= "popupContentAttrs";

	private	static 	int		ALLEY_SPACING			= 20;
	private	static 	int		INSET_SPACING			= 20;

	private	static 	int		Mi_MINIMUM_SIZE_OF_POINT	= 6;
	private	static 	int		Mi_MAXIMUM_SIZE_OF_POINT	= 20;

	private		MiSize		Mi_SAMPLE_SIZE = new MiSize(100,75);

	private		MiWidget[] 	standardRegularShapeLocationButtons;
	private		MiWidget[] 	standardMultiPointShapeLocationButtons;

	private		MiBox		lookAttributesArea;
	private		MiPart		sampleLookArea;
	private		MiPart		sampleContentArea;
	private		MiPart		locationChooserArea;
	private		MiPart		locationSampleArea;

	private		MiPart		regularShapeChooserArea;
	private		MiPart		multiPointShapeChooserArea;

	private		MiPart		regularShapeSampleArea;
	private		MiPart		multiPointShapeSampleArea;

	private		boolean		settingTargetShape;
	private		boolean		buildingPanel;
	private		boolean		updatingUI;

	private		boolean		hasChanged;

	private		MiPart		sampleLook;
	protected	MiPart		sampleContent;
	private		MiPart		sample;
	private		MiPart		originalSample;
	private		MiLine		locationLine;
	private		MiRectangle	locationRectangle;

	private		MiICreateSelectDeleteManagedPoints createSelectDeleteManagedPoints;
	private		MiManagedPoints			selectedManagedPoints		= new MiManagedPoints();

	private		MiManagedPointManager		pointManagerKind;

	private		MiManagedPointManager 		targetManagedPointManager;
	private		MiManagedPoints 		targetManagedPoints;

	protected	MiToggleButton			visibleToggleButton;
	private		MiOptionMenu			shapeOptionMenu;
	private		MiNumericTextField		sizeTextField;
	protected	MiTextField			defaultTextualPrompt;
	private		MiText				defaultTextLabel;
	private		MiPushButton			contentAppearancePopupButton;
	private		MiText				contentAppearanceLabel;
	private		MiPushButton			promptAppearancePopupButton;
	private		MiText				promptAppearanceLabel;
	private		MiGeneralAttributesDialog	generalAttributesPopup;
	private		int				contentAttributes =
							MiGeneralAttributesPanel.ALL_TEXT_ATTRIBUTES_MASK
							| MiGeneralAttributesPanel.ALL_BASIC_ATTRIBUTES_MASK
                                               		& ~(Mi_GRADIENT_STYLE_ATTRIBUTE_MASK_BIT
                                               		| Mi_LINE_WIDTH_ATTRIBUTE_MASK_BIT
                                               		| Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT
                                               		| Mi_STATUS_HELP_ATTRIBUTE_MASK_BIT);






	public				MiAllManagedPointsAttributesPanel(
						MiGeneralAttributesDialog generalAttributesPopup)
		{
		this.generalAttributesPopup = generalAttributesPopup;
		buildingPanel = true;

		pointManagerKind = newPointManager();

		MiColumnLayout mainLayout = new MiColumnLayout();
		mainLayout.setInsetMargins(INSET_SPACING);
		mainLayout.setElementHJustification(Mi_JUSTIFIED);
		mainLayout.setAlleyHSpacing(ALLEY_SPACING);
		setLayout(mainLayout);

		MiRowLayout rowLayout = new MiRowLayout();
		rowLayout.setElementHJustification(Mi_JUSTIFIED);
		rowLayout.setAlleyHSpacing(ALLEY_SPACING);

		MiColumnLayout column1 = new MiColumnLayout();
		column1.setElementHSizing(Mi_EXPAND_TO_FILL);
		column1.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column1.setAlleyVSpacing(ALLEY_SPACING);
		MiColumnLayout column2 = new MiColumnLayout();
		column2.setElementHSizing(Mi_EXPAND_TO_FILL);
		column2.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		column2.setAlleyVSpacing(ALLEY_SPACING);
		
		lookAttributesArea = makeLookAttributesArea();
		sampleLookArea = makeSampleLookArea();
		sampleContentArea = makeSampleContentArea();
		locationSampleArea = makeSampleArea();
		locationChooserArea = makeLocationChooserArea();

		appendPart(rowLayout);
		rowLayout.appendPart(column1);
		rowLayout.appendPart(column2);

		column1.appendPart(lookAttributesArea);

		column2.appendPart(sampleLookArea);
		column2.appendPart(sampleContentArea);

		column1.appendPart(locationChooserArea);
		column2.appendPart(locationSampleArea);

		buildingPanel = false;
		}
	protected abstract MiPart			makeSampleContentArea();
	protected abstract MiManagedPointManager	newPointManager();
	protected abstract void				initFromNewRule(MiiManagedPointRule theRule);
	protected abstract void				hideOtherManagedPointsInSample(MiPart sample);
	protected abstract int[]			getStandardRegularShapeLocations();
	protected abstract int[]			getStandardMultiPointShapeLocations();
	protected abstract MiiManagedPointRule 		getRule();

	//---------------------------------------------------------------
	public		void		setTargetShape(MiPart target)
		{
		settingTargetShape = true;

		if (target == null)
			{
			locationChooserArea.setVisible(false);
			locationSampleArea.setVisible(false);
			targetManagedPointManager = newPointManager();
			targetManagedPoints = new MiManagedPoints();
			}
		else
			{
			locationChooserArea.setVisible(true);
			locationSampleArea.setVisible(true);

			targetManagedPointManager = MiManagedPointManager.getManager(target, pointManagerKind);
			if (targetManagedPointManager == null)
				targetManagedPointManager = pointManagerKind.copy();
			else
				targetManagedPointManager = targetManagedPointManager.copy();

			targetManagedPoints = targetManagedPointManager.getManagedPoints();
			if (targetManagedPoints == null)
				{
				targetManagedPoints = new MiManagedPoints();
				targetManagedPointManager.setManagedPoints(targetManagedPoints);
				}
			}

		targetManagedPointManager.setIsMutable(true);

		MiPart look = targetManagedPointManager.getLook();
		originalLook = look;
		shapeOptionMenu.setValue(MiManagedPointManager.getStyledLookName(look));
		visibleToggleButton.select(targetManagedPointManager.isVisible());
		sizeTextField.setValue("" + look.getSize(new MiSize()).getWidth());

		lookAppearanceAttributes = look.getAttributes();
		initFromNewRule(targetManagedPointManager.getRule());


		createSelectDeleteManagedPoints.deSelectAll();
		selectedManagedPoints.removeAllElements();

		if (target == null)
			{
			if (sample != originalSample)
				{
				sample.replaceSelf(originalSample);
				sample = originalSample;
				sample.removeActionHandlers(this);
				}
			lookAttributesArea.setValue(Mi_LOOK_AREA_GLOBAL_TITLE_NAME);
			}
		else
			{
			MiPart newSample = target.deepCopy();
			newSample.setSize(Mi_SAMPLE_SIZE);
			newSample.setPreferredSize(Mi_SAMPLE_SIZE);
			MiManagedPointManager.setManager(newSample, targetManagedPointManager);
			sample.replaceSelf(newSample);
			sample = newSample;
			sample.appendActionHandler(this, Mi_MANAGED_POINT_SELECTED_ACTION);
			sample.appendActionHandler(this, Mi_MANAGED_POINT_DESELECTED_ACTION);
			sample.appendActionHandler(this, Mi_MANAGED_POINT_ADDED_ACTION);
			sample.appendActionHandler(this, Mi_MANAGED_POINT_REMOVED_ACTION);

			hideOtherManagedPointsInSample(sample);

			targetManagedPointManager.setLocallyVisible(true);

			updateLocationChoosers(sample);
			lookAttributesArea.setValue(Mi_LOOK_AREA_SELECTED_SHAPES_TITLE_NAME);
			}

		updateLookAttributesArea(target);
		updateSamplePointLook(targetManagedPointManager.getLook());

		hasChanged = false;
		settingTargetShape = false;
		}
	//---------------------------------------------------------------
	protected	void		updateLocationChoosers(MiPart target)
		{
		updatingUI = true;

		MiManagedPoints targetManagedPoints = MiManagedPointManager.getManager(
					target, pointManagerKind).getManagedPoints();
		int[] builtinLocations;
		MiWidget[] builtinLocationButtons;
		if ((target != null) && (!(target instanceof MiLine)) 
			&& (!(target instanceof MiConnection)))
			{
			regularShapeChooserArea.setVisible(true);
			multiPointShapeChooserArea.setVisible(false);
			builtinLocations = getStandardRegularShapeLocations();
			builtinLocationButtons = standardRegularShapeLocationButtons;
			}
		else
			{
			regularShapeChooserArea.setVisible(false);
			multiPointShapeChooserArea.setVisible(true);
			builtinLocations = getStandardMultiPointShapeLocations();
			builtinLocationButtons = standardMultiPointShapeLocationButtons;
			}

		for (int i = 0; i < builtinLocations.length; ++i)
			{
			builtinLocationButtons[i].select(
				targetManagedPoints.getManagedPoint(builtinLocations[i]) != null);
			}

		updatingUI = false;
		}
	//---------------------------------------------------------------
	public		void		 applyToGlobalDefaults()
		{
		MiAnnotationPointManager.setGloballyVisible(visibleToggleButton.isSelected());
		MiPart defaultLook = getLook();
		MiAnnotationPointManager.setGlobalLook(defaultLook);

		MiAnnotationPointRule rule = MiAnnotationPointManager.getGlobalRule();
		rule.setContentAttributes(contentAppearanceAttributes);
		rule.setPromptAttributes(promptAppearanceAttributes);
		rule.setPrompt(defaultTextualPrompt.getValue());
		}
	//---------------------------------------------------------------
	public		void		applyToTargetShape(MiPart target)
		{
		targetManagedPointManager = gatherPointManager();
		MiManagedPointManager.setManager(target, targetManagedPointManager);
		createSelectDeleteManagedPoints.deSelectAll();
		}
	//---------------------------------------------------------------
	protected	MiManagedPointManager	gatherPointManager()
		{
		MiManagedPointManager pointManager = MiManagedPointManager.getManager(sample, pointManagerKind); 
		if (selectedManagedPoints.size() > 0)
			{
			for (int i = 0; i < selectedManagedPoints.size(); ++i)
				{
				MiManagedPoint pt = selectedManagedPoints.elementAt(i);
				pt.setLook(getLook());
				pt.setRule(getRule());
				pt.getLook().setVisible(visibleToggleButton.isSelected());
				}
			}
		else
			{
			pointManager.setLocallyVisible(visibleToggleButton.isSelected());
			pointManager.setLocalLook(getLook());
			pointManager.setLocalRule(getRule());
			}
		return(pointManager);
		}
	//---------------------------------------------------------------
	protected	MiPart		getLook()
		{
		MiPart look;

		String style = shapeOptionMenu.getValue();
		if (style.equals(MiManagedPointManager.Mi_CUSTOM_TYPE_NAME))
			look = originalLook;
		else
			look = MiManagedPointManager.makeStyledLook(style);
		look.setAttributes(lookAppearanceAttributes);

		// FIX: need parts of cross to inherit attributes 
		for (int i = 0; i < look.getNumberOfParts(); ++i)
			look.getPart(i).setAttributes(look.getAttributes());

		look.setSize(new MiSize(Utility.toDouble(sizeTextField.getValue())));

		MiPart theLook = look;

		if (!Utility.isEmptyOrNull(defaultTextualPrompt.getValue()))
			{
			MiVisibleContainer c = new MiVisibleContainer();
			c.setAttributes(look.getAttributes());
			c.setShape(look);

			MiText prompt =  new MiText(defaultTextualPrompt.getValue());
			prompt.setAttributes(promptAppearanceAttributes);
			c.appendPart(prompt);

			theLook = c;
			}
		theLook.validateLayout();
		return(theLook);
		}
	//---------------------------------------------------------------
	protected	MiBox		makeLookAttributesArea()
		{
		MiBox box = new MiBox(Mi_LOOK_AREA_GLOBAL_TITLE_NAME);
		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		gridLayout.setElementHJustification(Mi_LEFT_JUSTIFIED);
		box.setLayout(gridLayout);

		visibleToggleButton = new MiToggleButton();
		MiText visibleLabel = new MiText("Visible");
		visibleToggleButton.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(visibleLabel);
		box.appendPart(visibleToggleButton);

		shapeOptionMenu = new MiOptionMenu();
		shapeOptionMenu.setContents(MiManagedPointManager.getStyledLooks());
		MiText shapeLabel = new MiText("Shape");
		shapeOptionMenu.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(shapeLabel);
		box.appendPart(shapeOptionMenu);

		MiPushButton lookAppearancePopupButton = new MiPushButton("...");
		lookAppearancePopupButton.appendCommandHandler(this, Mi_POPUP_LOOK_APPEARANCE_DIALOG_BOX_CMD_NAME);
		MiText lookAppearanceLabel = new MiText("Appearance");
		box.appendPart(lookAppearanceLabel);
		box.appendPart(lookAppearancePopupButton);

		sizeTextField = new MiNumericTextField();
		sizeTextField.setMinimumValue(Mi_MINIMUM_SIZE_OF_POINT);
		sizeTextField.setMaximumValue(Mi_MAXIMUM_SIZE_OF_POINT);
		MiText sizeLabel = new MiText("Size");
		sizeTextField.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(sizeLabel);
		box.appendPart(sizeTextField);


		defaultTextualPrompt = new MiTextField();
		defaultTextLabel = new MiText("Prompt");
		defaultTextualPrompt.setNumDisplayedColumns(20);
		defaultTextualPrompt.appendCommandHandler(this, UPDATE_FROM_PANEL_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(defaultTextLabel);
		box.appendPart(defaultTextualPrompt);



		promptAppearancePopupButton = new MiPushButton("...");
		promptAppearancePopupButton.appendCommandHandler(this, Mi_POPUP_PROMPT_APPEARANCE_DIALOG_BOX_CMD_NAME);
		promptAppearanceLabel = new MiText("Prompt Appearance");
		box.appendPart(promptAppearanceLabel);
		box.appendPart(promptAppearancePopupButton);


		contentAppearancePopupButton = new MiPushButton("...");
		contentAppearancePopupButton.appendCommandHandler(this, Mi_POPUP_CONTENT_APPEARANCE_DIALOG_BOX_CMD_NAME);
		contentAppearanceLabel = new MiText("Content Appearance");
		box.appendPart(contentAppearanceLabel);
		box.appendPart(contentAppearancePopupButton);

		return(box);
		}
	//---------------------------------------------------------------
	protected	void		setHasPrompt(boolean flag)
		{
		promptAppearancePopupButton.setVisible(flag);
		promptAppearanceLabel.setVisible(flag);
		defaultTextualPrompt.setVisible(flag);
		defaultTextLabel.setVisible(flag);
		}
	//---------------------------------------------------------------
	protected	void		setHasContent(boolean flag)
		{
		contentAppearancePopupButton.setVisible(flag);
		contentAppearanceLabel.setVisible(flag);
		}
	//---------------------------------------------------------------
	protected	void		setContentLabel(String text)
		{
		contentAppearanceLabel.setText(text);
		}
	//---------------------------------------------------------------
	protected	void		setContentAttributes(int atts)
		{
		contentAttributes = atts;
		}
	//---------------------------------------------------------------
	protected	void		updateLookAttributesArea(MiPart target)
		{
		updatingUI = true;
		MiPart look = null;
		MiAnnotationPointRule rule = null;
		if (lookAttributesArea.getValue().equals(Mi_LOOK_AREA_GLOBAL_TITLE_NAME))
			{
			visibleToggleButton.select(MiAnnotationPointManager.isGloballyVisible());
			look = MiAnnotationPointManager.getGlobalLook();
			rule = MiAnnotationPointManager.getGlobalRule();
			}
		else if (lookAttributesArea.getValue().equals(Mi_LOOK_AREA_SELECTED_SHAPES_TITLE_NAME))
			{
			MiAnnotationPointManager manager = target.getAnnotationPointManager();
			visibleToggleButton.select(manager.isVisible());
			look = manager.getLook();
			rule = (MiAnnotationPointRule )manager.getRule();
			if (rule.getPrompt() == null)
				{
				sizeTextField.setValue("" + look.getSize(new MiSize()).getWidth());
				}
			else
				{
				sizeTextField.setValue("" + MiAnnotationPointManager.getGlobalLook()
					.getSize(new MiSize()).getWidth());
				}
			}
		else // if (lookAttributesArea.getValue().equals(Mi_LOOK_AREA_SELECTED_POINTS_TITLE_NAME))
			{
			MiAnnotationPointManager manager = target.getAnnotationPointManager();
			visibleToggleButton.select(manager.isVisible());
			look = selectedManagedPoints.elementAt(0).getLook();
			rule = (MiAnnotationPointRule )selectedManagedPoints.elementAt(0).getRule();

			if (look == null)
				look = manager.getLook();
			if (rule == null)
				rule = (MiAnnotationPointRule )manager.getRule();

			if (rule.getPrompt() == null)
				sizeTextField.setValue("" + look.getSize(new MiSize()).getWidth());
			else
				sizeTextField.setValue("" + manager.getLook().getSize(new MiSize()).getWidth());
			}

		defaultTextualPrompt.setValue(rule.getPrompt());
		updatingUI = false;
		}
	//---------------------------------------------------------------
	protected	MiPart		makeSampleLookArea()
		{
		MiBox box = new MiBox("Point Appearance");
		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(container,
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		container.setOutgoingInvalidLayoutNotificationsEnabled(false);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementJustification(Mi_CENTER_JUSTIFIED);
		layout.setInsetMargins(14);
		container.setLayout(layout);
		sampleLook = new MiRectangle(0,0,75,50);
		container.appendPart(sampleLook);
		box.appendPart(container);
		return(box);
		}
	//---------------------------------------------------------------
	protected	MiPart		makeLocationChooserArea()
		{
		MiBox box = new MiBox("Common-point Location Editor");
		MiRowLayout mainLayout = new MiRowLayout();
		mainLayout.setElementHJustification(Mi_JUSTIFIED);
		box.setLayout(mainLayout);

		MiVisibleContainer container = new MiVisibleContainer();
		regularShapeChooserArea = container;
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(container,
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementJustification(Mi_CENTER_JUSTIFIED);
		layout.setInsetMargins(14);
		container.setLayout(layout);
		locationRectangle = new MiRectangle(0,0,100,75);
		locationRectangle.setLineWidth(3);
		locationRectangle.setPreferredSize(new MiSize(100,75));
		container.appendPart(locationRectangle);
		box.appendPart(container);
		
		int[] standardRegularShapeLocations = getStandardRegularShapeLocations();
		standardRegularShapeLocationButtons 
			= new MiCheckBox[standardRegularShapeLocations.length];
		MiPart checkIcon = MiToolkit.getCheckMarkIcon();
		for (int i = 0; i < standardRegularShapeLocations.length; ++i)
			{
			int pointNumber = standardRegularShapeLocations[i];

			MiCheckBox button = new MiCheckBox(checkIcon.copy());

			standardRegularShapeLocationButtons[i] = button;

			button.appendCommandHandler(this, "" + pointNumber, Mi_SELECTED_ACTION);
			button.appendCommandHandler(this, "" + -pointNumber, Mi_DESELECTED_ACTION);
			locationRectangle.appendAttachment(button, 
				standardRegularShapeLocations[i], null, null);
			}

		container = new MiVisibleContainer();
		multiPointShapeChooserArea = container;
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(container,
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		layout = new MiColumnLayout();
		layout.setElementJustification(Mi_CENTER_JUSTIFIED);
		layout.setInsetMargins(14);
		container.setLayout(layout);
		locationLine = new MiLine(0,0,100,0);
		locationLine.setBackgroundColor(MiColorManager.black);
		locationLine.setLineWidth(3);
		locationLine.setPreferredSize(new MiSize(100,60));
		container.appendPart(locationLine);
		box.appendPart(container);

		int[] standardMultiPointShapeLocations = getStandardMultiPointShapeLocations();
		standardMultiPointShapeLocationButtons 
			= new MiCheckBox[standardMultiPointShapeLocations.length];
		for (int i = 0; i < standardMultiPointShapeLocations.length; ++i)
			{
			int pointNumber = standardMultiPointShapeLocations[i];

			MiCheckBox button = new MiCheckBox(checkIcon.copy());

			standardMultiPointShapeLocationButtons[i] = button;

			button.appendCommandHandler(this, "line." + pointNumber, Mi_SELECTED_ACTION);
			button.appendCommandHandler(this, "line." + -pointNumber, Mi_DESELECTED_ACTION);
			locationLine.appendAttachment(button, 
				standardMultiPointShapeLocations[i], null, new MiMargins(0, 10, 0, 10));
			}

		return(box);
		}
	//---------------------------------------------------------------
	protected	MiPart		makeSampleArea()
		{
		MiBox box = new MiBox("Point Location Editor");
		MiRowLayout mainLayout = new MiRowLayout();
		mainLayout.setElementHJustification(Mi_JUSTIFIED);
		box.setLayout(mainLayout);

		MiVisibleContainer container = new MiVisibleContainer();

		createSelectDeleteManagedPoints = new MiICreateSelectDeleteManagedPoints();
		createSelectDeleteManagedPoints.setPointManagerKind(newPointManager());
		container.appendEventHandler(createSelectDeleteManagedPoints);
		container.setAcceptingKeyboardFocus(true);

		regularShapeSampleArea = container;
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(container,
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementJustification(Mi_CENTER_JUSTIFIED);
		layout.setInsetMargins(14);
		container.setLayout(layout);
		sample = new MiRectangle(0,0,100,75);
		sample.setLineWidth(3);
		sample.setPreferredSize(new MiSize(100,75));
		sample.setAnnotationPointManager(new MiAnnotationPointManager());
		sample.setConnectionPointManager(new MiConnectionPointManager());
		originalSample = sample;
		sample.appendActionHandler(this, Mi_MANAGED_POINT_ADDED_ACTION);
		sample.appendActionHandler(this, Mi_MANAGED_POINT_REMOVED_ACTION);
		sample.appendActionHandler(this, Mi_MANAGED_POINT_SELECTED_ACTION);
		sample.appendActionHandler(this, Mi_MANAGED_POINT_DESELECTED_ACTION);
		container.appendPart(sample);
		box.appendPart(container);
		return(box);
		}

	//---------------------------------------------------------------
	protected	void		updateSample()
		{
//System.out.println("UPDATESAMPLE");
		sample.getContainer(0).invalidateArea();

		updatingUI = true;

		MiManagedPointManager manager = gatherPointManager();
		createSelectDeleteManagedPoints.deSelectAll();
		MiManagedPointManager.setManager(sample, manager);
		createSelectDeleteManagedPoints.selectPoints(
			sample, 
			MiManagedPointManager.getManager(sample, pointManagerKind), 
			selectedManagedPoints);

		sampleContent.setAttributes(contentAppearanceAttributes);
		updateSamplePointLook(getLook());
		updatingUI = false;
		}
	//---------------------------------------------------------------
	protected	void		updateSamplePointLook(MiPart newLook)
		{
		if (newLook != sampleLook)
			{
			newLook = newLook.deepCopy();
			sampleLook.replaceSelf(newLook);
			sampleLook = newLook;
			sampleLook.getContainer(0).validateLayout();
			}
		}
	//---------------------------------------------------------------
	public		void		processCommand(String cmd)
		{
//System.out.println(this + ": PROCSSS COMMAND: " + cmd);
		if ((buildingPanel) || (settingTargetShape) || (updatingUI))
			return;

		if (cmd.startsWith("line."))
			{
			cmd = cmd.substring(5);
			int location = Utility.toInteger(cmd);
			if (location >= 0)
				MiManagedPointManager.getManager(sample, pointManagerKind).appendManagedPoint(location);
			else
				MiManagedPointManager.getManager(sample, pointManagerKind).removeManagedPoint(-location);
			}
		else if (Utility.isInteger(cmd))
			{
			int location = Utility.toInteger(cmd);
			if (location >= 0)
				MiManagedPointManager.getManager(sample, pointManagerKind).appendManagedPoint(location);
			else
				MiManagedPointManager.getManager(sample, pointManagerKind).removeManagedPoint(-location);
			}
		else if (cmd.equals(Mi_POPUP_LOOK_APPEARANCE_DIALOG_BOX_CMD_NAME))
			{
			generalAttributesPopup.removeAllActionHandlers();

			generalAttributesPopup.setWhichAttributesToDisplay(
						MiGeneralAttributesPanel.ALL_BASIC_ATTRIBUTES_MASK
                                                & ~(Mi_GRADIENT_STYLE_ATTRIBUTE_MASK_BIT
                                                | Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT));
			generalAttributesPopup.appendCommandHandler(this, 
				UPDATE_LOOK_ATTRIBUTES_FROM_POPUP_CMD_NAME, Mi_VALUE_CHANGED_ACTION);

			generalAttributesPopup.setDisplayAttributes(lookAppearanceAttributes);
			generalAttributesPopup.popupAndWaitForClose();
			}
		else if (cmd.equals(Mi_POPUP_PROMPT_APPEARANCE_DIALOG_BOX_CMD_NAME))
			{
			generalAttributesPopup.removeAllActionHandlers();

			generalAttributesPopup.setWhichAttributesToDisplay(
						MiGeneralAttributesPanel.ALL_TEXT_ATTRIBUTES_MASK
						| MiGeneralAttributesPanel.ALL_BASIC_ATTRIBUTES_MASK
                                                & ~(Mi_GRADIENT_STYLE_ATTRIBUTE_MASK_BIT
                                                | Mi_LINE_WIDTH_ATTRIBUTE_MASK_BIT
                                                | Mi_TOOL_HINT_ATTRIBUTE_MASK_BIT
                                                | Mi_STATUS_HELP_ATTRIBUTE_MASK_BIT));
			generalAttributesPopup.appendCommandHandler(this, 
				UPDATE_PROMPT_ATTRIBUTES_FROM_POPUP_CMD_NAME, Mi_VALUE_CHANGED_ACTION);

			MiAnnotationPointRule rule 
				= (MiAnnotationPointRule )sample.getAnnotationPointManager().getRule();
			generalAttributesPopup.setDisplayAttributes(rule.getPromptAttributes());

			generalAttributesPopup.popupAndWaitForClose();
			}
		else if (cmd.equals(Mi_POPUP_CONTENT_APPEARANCE_DIALOG_BOX_CMD_NAME))
			{
			generalAttributesPopup.removeAllActionHandlers();

			generalAttributesPopup.setWhichAttributesToDisplay(contentAttributes);
			generalAttributesPopup.appendCommandHandler(this,
				UPDATE_CONTENT_ATTRIBUTES_FROM_POPUP_CMD_NAME, Mi_VALUE_CHANGED_ACTION);

			generalAttributesPopup.setDisplayAttributes(contentAppearanceAttributes);
			generalAttributesPopup.popupAndWaitForClose();
			}
		else if (cmd.equals(UPDATE_CONTENT_ATTRIBUTES_FROM_POPUP_CMD_NAME))
			{
			contentAppearanceAttributes = generalAttributesPopup.getDisplayAttributes();
			}
		else if (cmd.equals(UPDATE_PROMPT_ATTRIBUTES_FROM_POPUP_CMD_NAME))
			{
			promptAppearanceAttributes = generalAttributesPopup.getDisplayAttributes();
			}
		else if (cmd.equals(UPDATE_LOOK_ATTRIBUTES_FROM_POPUP_CMD_NAME))
			{
			lookAppearanceAttributes = generalAttributesPopup.getDisplayAttributes();
			}
		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}
	//---------------------------------------------------------------
	public		boolean		processAction(MiiAction action)
		{
		if (updatingUI)
			return(true);

		if ((action.hasActionType(Mi_MANAGED_POINT_SELECTED_ACTION))
			|| (action.hasActionType(Mi_MANAGED_POINT_DESELECTED_ACTION)))
			{
			selectedManagedPoints.removeAllElements();
			selectedManagedPoints.append(
				createSelectDeleteManagedPoints.getSelectedManagedPoints());
			if (selectedManagedPoints.size() > 0)
				lookAttributesArea.setValue(Mi_LOOK_AREA_SELECTED_POINTS_TITLE_NAME);
			else
				lookAttributesArea.setValue(Mi_LOOK_AREA_SELECTED_SHAPES_TITLE_NAME);

			updateLookAttributesArea(sample);
			//updateSample();
			return(true);
			}
		else if (action.hasActionType(Mi_MANAGED_POINT_REMOVED_ACTION))
			{
			updateLocationChoosers(sample);
			}
		else if (action.hasActionType(Mi_MANAGED_POINT_ADDED_ACTION))
			{
			updateLocationChoosers(sample);
			}
		else
			{
			return(true);
			}

		updateSample();
		hasChanged = true;
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		return(true);
		}
	}
class MiAnnotationPointsAttributesPanel extends MiAllManagedPointsAttributesPanel
	{
	private static	int[]		standardRegularShapeAnnotationLocations =
						{
						Mi_CENTER_LOCATION,
						Mi_LEFT_LOCATION,
						Mi_RIGHT_LOCATION,
						Mi_BOTTOM_LOCATION,
						Mi_TOP_LOCATION	,
						Mi_LOWER_LEFT_LOCATION,
						Mi_LOWER_RIGHT_LOCATION,
						Mi_UPPER_LEFT_LOCATION,
						Mi_UPPER_RIGHT_LOCATION	
						};
	private static	int[]		standardMultiPointShapeAnnotationLocations =
						{
						Mi_LINE_CENTER_LOCATION,
						Mi_LINE_CENTER_TOP_OR_RIGHT_LOCATION,
						Mi_LINE_CENTER_BOTTOM_OR_LEFT_LOCATION,
						Mi_LINE_START_LOCATION,
						Mi_LINE_START_TOP_OR_RIGHT_LOCATION,
						Mi_LINE_START_BOTTOM_OR_LEFT_LOCATION,
						Mi_LINE_END_LOCATION,
						Mi_LINE_END_TOP_OR_RIGHT_LOCATION,
						Mi_LINE_END_BOTTOM_OR_LEFT_LOCATION
						};

	public				MiAnnotationPointsAttributesPanel(
						MiGeneralAttributesDialog generalAttributesPopup)
		{
		super(generalAttributesPopup);
		setContentLabel("Annotation appearance");
		}

	//---------------------------------------------------------------
	protected	MiManagedPointManager	newPointManager()
		{
		return(new MiAnnotationPointManager());
		}
	//---------------------------------------------------------------
	protected	void		initFromNewRule(MiiManagedPointRule theRule)
		{
		MiAnnotationPointRule rule = (MiAnnotationPointRule )theRule;
		contentAppearanceAttributes = rule.getContentAttributes();
		promptAppearanceAttributes = rule.getPromptAttributes();
		sampleContent.setAttributes(contentAppearanceAttributes);
		}
	//---------------------------------------------------------------
	protected	void		hideOtherManagedPointsInSample(MiPart sample)
		{
		if (sample.getConnectionPointManager() != null)
			{
			sample.setConnectionPointManager(
				(MiConnectionPointManager )sample.getConnectionPointManager().copy());
			sample.getConnectionPointManager().setLocallyVisible(false);
			}
		}

	//---------------------------------------------------------------
	protected	int[]		getStandardRegularShapeLocations()
		{
		return(standardRegularShapeAnnotationLocations);
		}
	//---------------------------------------------------------------
	protected	int[]		getStandardMultiPointShapeLocations()
		{
		return(standardMultiPointShapeAnnotationLocations);
		}
	
	//---------------------------------------------------------------
	protected	MiiManagedPointRule getRule()
		{
		MiAnnotationPointRule rule = new MiAnnotationPointRule();
		rule.setContentAttributes(contentAppearanceAttributes);
		rule.setPromptAttributes(promptAppearanceAttributes);
		rule.setPrompt(defaultTextualPrompt.getValue());
		return(rule);
		}
	//---------------------------------------------------------------
	protected	MiPart		makeSampleContentArea()
		{
		MiBox box = new MiBox("Annotation Appearance");
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(columnLayout);

		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(container,
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		container.setVisibleContainerAutomaticLayoutEnabled(false);
		//container.setInvalidLayoutNotificationsEnabled(false);
		container.setPreferredSize(new MiSize(100,75));
		container.setInsetMargins(10);

		sampleContent = new MiText("abcdefghijklmnopqrstuvwxyz\nABCDEFGHIJKLMNOPQRSTUVWXYZ\n1234567890");
		container.appendPart(sampleContent);

		box.appendPart(container);
		return(box);
		}
	}

class MiConnectionPointsAttributesPanel extends MiAllManagedPointsAttributesPanel
	{
	private static	int[]		standardRegularShapeConnectionLocations =
						{
						Mi_CENTER_LOCATION,
						Mi_LEFT_LOCATION,
						Mi_RIGHT_LOCATION,
						Mi_BOTTOM_LOCATION,
						Mi_TOP_LOCATION	,
						Mi_LOWER_LEFT_LOCATION,
						Mi_LOWER_RIGHT_LOCATION,
						Mi_UPPER_LEFT_LOCATION,
						Mi_UPPER_RIGHT_LOCATION	
						};
	private static	int[]		standardMultiPointShapeConnectionLocations =
						{
						Mi_LINE_CENTER_LOCATION,
						Mi_LINE_START_LOCATION,
						Mi_LINE_END_LOCATION,
						};

	public				MiConnectionPointsAttributesPanel(
						MiGeneralAttributesDialog generalAttributesPopup)
		{
		super(generalAttributesPopup);
		setHasPrompt(false);
		setContentLabel("Connection appearance");
		setContentAttributes( 
				MiGeneralAttributesPanel.ALL_BASIC_ATTRIBUTES_MASK
				+ Mi_LINE_ENDS_ATTRIBUTE_MASK_BIT
				& ~(Mi_GRADIENT_STYLE_ATTRIBUTE_MASK_BIT));
		}

	//---------------------------------------------------------------
	protected	MiManagedPointManager	newPointManager()
		{
		return(new MiConnectionPointManager());
		}
	//---------------------------------------------------------------
	protected	void		initFromNewRule(MiiManagedPointRule theRule)
		{
		MiConnectionPointRule rule = (MiConnectionPointRule )theRule;
		if (rule.getPrototypeConnection() != null)
			contentAppearanceAttributes = rule.getPrototypeConnection().getAttributes();
		else
			contentAppearanceAttributes = MiPart.getDefaultAttributes();
		//promptAppearanceAttributes = rule.getPromptAttributes();
		sampleContent.setAttributes(contentAppearanceAttributes);
		}
	//---------------------------------------------------------------
	protected	void		hideOtherManagedPointsInSample(MiPart sample)
		{
		if (sample.getAnnotationPointManager() != null)
			{
			sample.setAnnotationPointManager(
				(MiAnnotationPointManager )sample.getAnnotationPointManager().copy());
			sample.getAnnotationPointManager().setLocallyVisible(false);
			}
		}

	//---------------------------------------------------------------
	protected	int[]		getStandardRegularShapeLocations()
		{
		return(standardRegularShapeConnectionLocations);
		}
	//---------------------------------------------------------------
	protected	int[]		getStandardMultiPointShapeLocations()
		{
		return(standardMultiPointShapeConnectionLocations);
		}
	//---------------------------------------------------------------
	protected	MiiManagedPointRule getRule()
		{
		MiConnectionPointRule rule = new MiConnectionPointRule();
		if (rule.getPrototypeConnection() != null)
			{
			rule.getPrototypeConnection().setAttributes(contentAppearanceAttributes);
			}
		else
			{
			if (contentAppearanceAttributes != MiPart.getDefaultAttributes())
				{
				rule.setPrototypeConnection(new MiConnection());
				rule.getPrototypeConnection().setAttributes(contentAppearanceAttributes);
				rule.getPrototypeConnection().setConnectionPointManager(new MiConnectionPointManager());
				}
			}
			
		//rule.setPromptAttributes(promptAppearanceAttributes);
		//rule.setPrompt(defaultTextualPrompt.getValue());
		return(rule);
		}
	//---------------------------------------------------------------
	public		void		 applyToGlobalDefaults()
		{
		MiConnectionPointManager.setGloballyVisible(visibleToggleButton.isSelected());
		MiConnectionPointManager.setGlobalLook(getLook());
		MiConnectionPointManager.setGlobalRule((MiConnectionPointRule )getRule());
		}
	//---------------------------------------------------------------
/****
	protected	MiManagedPointManager	gatherPointManager()
		{
		MiManagedPointManager manager = super.gatherPointManager();

		if (((MiConnectionPointRule )manager.getRule()).getPrototypeConnection() != null)
			{
			((MiConnectionPointRule )manager.getRule()).getPrototypeConnection()
				.setConnectionPointManager((MiConnectionPointManager )manager);
			}
		return(manager);
		}
****/

	protected	MiPart		makeSampleContentArea()
		{
		MiBox box = new MiBox("Connection Appearance");
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(columnLayout);

		MiVisibleContainer container = new MiVisibleContainer();
		container.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		MiToolkit.setBackgroundAttributes(container,
			MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES);
		container.setVisibleContainerAutomaticLayoutEnabled(false);
		container.setInvalidLayoutNotificationsEnabled(false);
		container.setPreferredSize(new MiSize(125,100));
		container.setInsetMargins(10);

		MiConnection conn = new MiConnection();
		conn.setPoint(0, 0, 0);
		conn.setPoint(1, 100, 50);
		sampleContent = conn;
		container.appendPart(sampleContent);

		MiPolyConstraint polyLayout = new MiPolyConstraint();
		polyLayout.appendConstraint(new MiRelativeLocationConstraint(
				sampleContent, MiRelativeLocationConstraint.CENTER_OF, box));
		box.setLayout(polyLayout);

		box.appendPart(container);
		return(box);
		}
	}
