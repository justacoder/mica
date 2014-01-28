
/*
 ***************************************************************************
 *                  Mica - the Java(tm) Graphics Toolkit                   *
 ***************************************************************************
 * NOTICE: Permission to use, copy, modify and distribute this software    *
 * and its documentation, for commercial or non-commercial purposes, is    *
 * hereby granted provided that this notice appears in all copies.         *
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

import java.util.HashMap;

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDrawingShapeToolBar extends MiToolBar implements MiiCommandNames, MiiActionHandler
	{
	public static final String	SELECTION_MODE_COMMAND_NAME		= "selectMode";
	public static final String	RUBBER_STAMP_MODE_COMMAND_NAME		= "rubberStampMode";
	public static final String	NOT_RUBBER_STAMP_MODE_COMMAND_NAME	= "notRubberStampMode";
	public static final String	LINE_DRAW_MODE_COMMAND_NAME		= "lineDrawMode";
	public static final String	RECTANGLE_DRAW_MODE_COMMAND_NAME	= "rectDrawMode";
	public static final String	ROUND_RECTANGLE_DRAW_MODE_COMMAND_NAME	= "roundRectDrawMode";
	public static final String	ARC_DRAW_MODE_COMMAND_NAME		= "arcDrawMode";
	public static final String	POLYGON_DRAW_MODE_COMMAND_NAME		= "polygonDrawMode";
	public static final String	PENCIL_DRAW_MODE_COMMAND_NAME		= "pencilDrawMode";
	public static final String	ELLIPSE_DRAW_MODE_COMMAND_NAME		= "ellipseDrawMode";
	public static final String	TEXT_DRAW_MODE_COMMAND_NAME		= "textDrawMode";

	private		MiICreateObject			rectangularShapeCreator;
	private		MiICreateMultiPointObject	multiPointShapeCreator;
	private		MiICreateSmoothMultiPointObject	pencilShapeCreator;
	private		MiICreateText			textShapeCreator;

	//private		MiEventMonitor			escKeyMonitor;
	private		MiEventHandler			escKeyMonitor;

	private		MiDrawingToolBarManager		toolBarManager;

	private		boolean				selectNewlyCreatedShape	= true;

	private		boolean				inRubberStampMode;
	private		boolean				restoreToSelectionStateWhenShapeSelected = true;

	private		MiPart		arcPrototype		= new Mi3PointArc();
	private		MiPart		circlePrototype		= new MiCircle();
	private		MiPart		ellipsePrototype	= new MiEllipse();
	private		MiPart		linePrototype		= new MiLine();
	private		MiPart		polygonPrototype	= new MiPolygon();
	private		MiPart		pencilPrototype		= new MiLine();
	private		MiPart		rectPrototype		= new MiRectangle();
	private		MiPart		roundRectPrototype	= new MiRoundRectangle();
	private		MiText		textPrototype		= new MiText();

	private		MiEditor	oldEditor;
	private		MiWidget	selectionArrowButton;
	private		MiWidget	rubberStampButton;

	private		MiParts		prototypeShapes		= new MiParts();
	private		MiParts		drawButtonShapes	= new MiParts();
	private		MiParts		selectedShapes		= new MiParts();

	private		HashMap		commandNamesToIconImagesMap	= new HashMap();




	protected			MiDrawingShapeToolBar()
		{
		// Used for copy operation.
		}
	public				MiDrawingShapeToolBar(
						MiEditor editor, 
						MiiCommandManager manager)
		{
		this(editor, manager, true);
		}
	public				MiDrawingShapeToolBar(
						MiEditor editor, 
						MiiCommandManager manager,
						boolean shapesHaveConnectionPoints)
		{
		super(editor, manager);
		setup(shapesHaveConnectionPoints);
		}
	public				MiDrawingShapeToolBar(
						MiEditor editor, 
						MiiCommandManager manager,
						boolean shapesHaveConnectionPoints,
						HashMap commandNamesToIconImagesMap)
		{
		super(editor, manager);
		this.commandNamesToIconImagesMap = commandNamesToIconImagesMap;
		setup(shapesHaveConnectionPoints);
		}

	public		void		setToolBarManager(MiDrawingToolBarManager manager)
		{
		this.toolBarManager = manager;
		}

	public		void		setRestoreToSelectionStateWhenShapeSelected(boolean flag)
		{
		restoreToSelectionStateWhenShapeSelected = flag;
		}
	public		boolean		getRestoreToSelectionStateWhenShapeSelected()
		{
		return(restoreToSelectionStateWhenShapeSelected);
		}
	public		void		setSelectNewlyCreatedShape(boolean flag)
		{
		selectNewlyCreatedShape = flag;
		rectangularShapeCreator.setSelectNewlyCreatedShape(flag);
		multiPointShapeCreator.setSelectNewlyCreatedShape(flag);
		pencilShapeCreator.setSelectNewlyCreatedShape(flag);
		}
	public		boolean		getSelectNewlyCreatedShape()
		{
		return(selectNewlyCreatedShape);
		}
	public		MiPart		getDrawingShapeButton(String drawModeCommand)
		{
		return(getToolItemWithCommand(drawModeCommand));
		}
	public		void		setEditor(MiEditor editor)
		{
		super.setEditor(editor);
		
		assignToolBarHandlersToTargetEditor(editor);
		}

	protected	void		assignToolBarHandlersToTargetEditor(MiEditor newEditor)
		{
		if (newEditor == oldEditor)
			{
			return;
			}
		if (oldEditor != null)
			{
			oldEditor.removeActionHandlers(this);

			oldEditor.removeEventHandler(rectangularShapeCreator);
			oldEditor.removeEventHandler(multiPointShapeCreator);
			oldEditor.removeEventHandler(pencilShapeCreator);
			oldEditor.removeEventHandler(textShapeCreator);
			oldEditor.removeEventHandler(escKeyMonitor);
			}
		newEditor.appendActionHandler(this, Mi_ACTIONS_OF_PARTS_OF_OBSERVED | Mi_ACTIONS_OF_OBSERVED | Mi_DATA_IMPORT_ACTION);
		newEditor.appendActionHandler(this, Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION);
		newEditor.appendActionHandler(new MiAction(this, 
				Mi_NO_ITEMS_SELECTED_ACTION,
				Mi_ONE_ITEM_SELECTED_ACTION,
				Mi_MANY_ITEMS_SELECTED_ACTION,
				null));


		newEditor.insertEventHandler(rectangularShapeCreator, 0);
		newEditor.insertEventHandler(multiPointShapeCreator, 0);
		newEditor.insertEventHandler(pencilShapeCreator, 0);
		newEditor.insertEventHandler(textShapeCreator, 0);
		newEditor.appendEventHandler(escKeyMonitor);

		oldEditor = newEditor;
		}
	protected	void		setup(boolean shapesHaveConnectionPoints)
		{
		processCommand(Mi_HIDE_LABELS_COMMAND_NAME);

	

		getDragAndDropBehavior().setValidTargets(new MiParts(this));

		rectangularShapeCreator = new MiICreateObject();
		multiPointShapeCreator = new MiICreateMultiPointObject();
		pencilShapeCreator = new MiICreateSmoothMultiPointObject();
		textShapeCreator = new MiICreateText();

/*
		escKeyMonitor = new MiIExecuteCommandUponMonitoredEvent(
					new MiEvent(Mi_KEY_EVENT, Mi_ESC_KEY, 0),
					this,
					SELECTION_MODE_COMMAND_NAME);
*/
		escKeyMonitor = new MiIExecuteCommand(
					new MiEvent(Mi_KEY_EVENT, Mi_ESC_KEY, 0),
					this,
					SELECTION_MODE_COMMAND_NAME);
		escKeyMonitor.setPositionDependent(false);

		disableAllInteractiveEventHandlers();

		assignToolBarHandlersToTargetEditor(getEditor());


		MiPart selectionArrowImage = (MiPart )commandNamesToIconImagesMap.get(SELECTION_MODE_COMMAND_NAME);
		MiPart rubberStampImage = (MiPart )commandNamesToIconImagesMap.get(RUBBER_STAMP_MODE_COMMAND_NAME);
		MiPart drawArcsImage = (MiPart )commandNamesToIconImagesMap.get(ARC_DRAW_MODE_COMMAND_NAME);
		MiPart drawLinesImage = (MiPart )commandNamesToIconImagesMap.get(LINE_DRAW_MODE_COMMAND_NAME);
		MiPart drawPolygonsImage = (MiPart )commandNamesToIconImagesMap.get(POLYGON_DRAW_MODE_COMMAND_NAME);
		MiPart drawPencilImage = (MiPart )commandNamesToIconImagesMap.get(PENCIL_DRAW_MODE_COMMAND_NAME);
		MiPart drawRectImage = (MiPart )commandNamesToIconImagesMap.get(RECTANGLE_DRAW_MODE_COMMAND_NAME);
		MiPart drawRoundRectImage = (MiPart )commandNamesToIconImagesMap.get(ROUND_RECTANGLE_DRAW_MODE_COMMAND_NAME);
		MiPart drawCirclesImage = (MiPart )commandNamesToIconImagesMap.get(ELLIPSE_DRAW_MODE_COMMAND_NAME);
		MiPart drawEllipsesImage = (MiPart )commandNamesToIconImagesMap.get(ELLIPSE_DRAW_MODE_COMMAND_NAME);
		MiPart drawTextImage = (MiPart )commandNamesToIconImagesMap.get(TEXT_DRAW_MODE_COMMAND_NAME);

		if (selectionArrowImage == null)
			{
			selectionArrowImage = new MiImage("${Mi_IMAGES_HOME}/selectionArrow.xpm", true);
			}
		if (rubberStampImage == null)
			{
			rubberStampImage = new MiImage("${Mi_IMAGES_HOME}/rubberStamp.xpm", true);
			}
		if (drawArcsImage == null)
			{
			drawArcsImage = new MiImage("${Mi_IMAGES_HOME}/arcs.xpm", true);
			}
		if (drawLinesImage == null)
			{
			drawLinesImage = new MiImage("${Mi_IMAGES_HOME}/lines.xpm", true);
			}
		if (drawPolygonsImage == null)
			{
			drawPolygonsImage = new MiImage("${Mi_IMAGES_HOME}/polygons.xpm", true);
			}
		if (drawPencilImage == null)
			{
			drawPencilImage = new MiImage("${Mi_IMAGES_HOME}/pencil.xpm", true);
			}
		if (drawRectImage == null)
			{
			drawRectImage = new MiImage("${Mi_IMAGES_HOME}/rects.xpm", true);
			}
		if (drawRoundRectImage == null)
			{
			drawRoundRectImage = new MiImage("${Mi_IMAGES_HOME}/roundRects.xpm", true);
			}
		if (drawCirclesImage == null)
			{
			drawCirclesImage = new MiImage("${Mi_IMAGES_HOME}/circles.xpm", true);
			}
		if (drawEllipsesImage == null)
			{
			drawEllipsesImage = new MiImage("${Mi_IMAGES_HOME}/ellipses.xpm", true);
			}
		if (drawTextImage == null)
			{
			drawTextImage = new MiImage("${Mi_IMAGES_HOME}/text.xpm", true);
			}


		Mi3PointArc arc = new Mi3PointArc();
		arc.setPoint(0, new MiPoint(0,0));
		arc.setPoint(1, new MiPoint(50,50));
		arc.setPoint(2, new MiPoint(100,0));
		setDNDBehaviorOfDrawShapeButton(drawArcsImage, arc);
		setDNDBehaviorOfDrawShapeButton(drawLinesImage, new MiLine(0,0,100,100));
		MiPolygon polygon = new MiPolygon();
		polygon.appendPoint(new MiPoint(0,0));
		polygon.appendPoint(new MiPoint(100,100));
		polygon.appendPoint(new MiPoint(0,100));
		setDNDBehaviorOfDrawShapeButton(drawPolygonsImage, polygon);
		MiLine pencilLine = new MiLine();
		pencilLine.setPoint(1, new MiPoint(50, 40));
		pencilLine.appendPoint(new MiPoint(100,100));
		pencilLine.appendPoint(new MiPoint(50,100));
		setDNDBehaviorOfDrawShapeButton(drawPencilImage, pencilLine);
		setDNDBehaviorOfDrawShapeButton(drawRectImage, new MiRectangle(0,0,100,100));
		setDNDBehaviorOfDrawShapeButton(drawRoundRectImage, new MiRoundRectangle(0,0,100,100));
		setDNDBehaviorOfDrawShapeButton(drawCirclesImage, new MiCircle(0,0,50));
		MiEllipse ellipse = new MiEllipse(0,0,100,100);
		ellipse.setHeight(50);
		setDNDBehaviorOfDrawShapeButton(drawEllipsesImage, ellipse);
		MiText dndTextPrototype = new MiText("ABCDEF");
		dndTextPrototype.setSelectEntireTextAsPartInEditor(true);
		dndTextPrototype.setMustDoubleClickToEdit(true);
		dndTextPrototype.setConnectionPointManager(null);
		dndTextPrototype.setAnnotationPointManager(null);
		setDNDBehaviorOfDrawShapeButton(drawTextImage, dndTextPrototype);

		startRadioButtonSection();
		selectionArrowButton = appendBooleanToolItem("Select", selectionArrowImage, this, 
								SELECTION_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		rubberStampButton = appendBooleanToolItem("Rubber Stamp", rubberStampImage, this, 
								RUBBER_STAMP_MODE_COMMAND_NAME,
								NOT_RUBBER_STAMP_MODE_COMMAND_NAME);
		rubberStampButton.setRadioStateEnforcer(null);
		appendSpacer();
		appendBooleanToolItem("Line", drawLinesImage, this, 
								LINE_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Arc", drawArcsImage, this, 
								ARC_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Polygon", drawPolygonsImage, this, 
								POLYGON_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Pencil", drawPencilImage, this, 
								PENCIL_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Rectangle", drawRectImage, this, 
								RECTANGLE_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("RoundRect", drawRoundRectImage, this, 
								ROUND_RECTANGLE_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Ellipse", drawEllipsesImage, this, 
								ELLIPSE_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Text", drawTextImage, this, 
								TEXT_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);

		getCommandManager().setCommandState(SELECTION_MODE_COMMAND_NAME, true);

		// setToolItemImageSizes(new MiSize(24, 24));

		prototypeShapes.addElement(arcPrototype);
		prototypeShapes.addElement(circlePrototype);
		prototypeShapes.addElement(ellipsePrototype);
		prototypeShapes.addElement(linePrototype);
		prototypeShapes.addElement(polygonPrototype);
		prototypeShapes.addElement(pencilPrototype);
		prototypeShapes.addElement(rectPrototype);
		prototypeShapes.addElement(roundRectPrototype);
		prototypeShapes.addElement(textPrototype);

		if (shapesHaveConnectionPoints)
			{
			MiConnectionPointManager regularShapeConnectionPointManager = new MiConnectionPointManager();
			regularShapeConnectionPointManager.appendManagedPoint(Mi_CENTER_LOCATION);
			regularShapeConnectionPointManager.appendManagedPoint(Mi_LEFT_LOCATION);
			regularShapeConnectionPointManager.appendManagedPoint(Mi_RIGHT_LOCATION);
			regularShapeConnectionPointManager.appendManagedPoint(Mi_BOTTOM_LOCATION);
			regularShapeConnectionPointManager.appendManagedPoint(Mi_TOP_LOCATION);
			regularShapeConnectionPointManager.appendManagedPoint(Mi_LOWER_LEFT_LOCATION);
			regularShapeConnectionPointManager.appendManagedPoint(Mi_LOWER_RIGHT_LOCATION);
			regularShapeConnectionPointManager.appendManagedPoint(Mi_UPPER_LEFT_LOCATION);
			regularShapeConnectionPointManager.appendManagedPoint(Mi_UPPER_RIGHT_LOCATION);

			MiConnectionPointManager lineShapeConnectionPointManager = new MiConnectionPointManager();
			lineShapeConnectionPointManager.appendManagedPoint(Mi_START_LOCATION);
			lineShapeConnectionPointManager.appendManagedPoint(Mi_END_LOCATION);

			MiAnnotationPointManager regularShapeAnnotationPointManager = new MiAnnotationPointManager();
			regularShapeAnnotationPointManager.appendManagedPoint(Mi_CENTER_LOCATION);

			MiAnnotationPointManager lineShapeAnnotationPointManager = new MiAnnotationPointManager();
			lineShapeAnnotationPointManager.appendManagedPoint(Mi_LINE_CENTER_LOCATION);

			for (int i = 0; i < prototypeShapes.size(); ++i)
				{
				MiPart prototype = prototypeShapes.elementAt(i);
				if ((prototype instanceof MiLine) || (prototype instanceof Mi3PointArc))
					{
					prototype.setConnectionPointManager(lineShapeConnectionPointManager);
					prototype.setAnnotationPointManager(lineShapeAnnotationPointManager);
					}
				else
					{
					prototype.setConnectionPointManager(regularShapeConnectionPointManager);
					prototype.setAnnotationPointManager(regularShapeAnnotationPointManager);
					}
				}
			textPrototype.setConnectionPointManager(null);
			textPrototype.setAnnotationPointManager(null);

			for (int i = 0; i < drawButtonShapes.size(); ++i)
				{
				MiPart prototype = drawButtonShapes.elementAt(i);
				if ((prototype instanceof MiLine) || (prototype instanceof Mi3PointArc))
					{
					prototype.setConnectionPointManager(lineShapeConnectionPointManager);
					prototype.setAnnotationPointManager(lineShapeAnnotationPointManager);
					}
				else
					{
					prototype.setConnectionPointManager(regularShapeConnectionPointManager);
					prototype.setAnnotationPointManager(regularShapeAnnotationPointManager);
					}
				}
			}

		MiSize rubberStampSize = new MiSize(50, 50);
		for (int i = 0; i < prototypeShapes.size(); ++i)
			{
			MiPart prototype = prototypeShapes.elementAt(i);
			prototype.setIsDragAndDropSource(true);
			if (prototype != textPrototype)
				{
				prototype.setSize(rubberStampSize);
				}
			}

		textPrototype.setModificationsAreUndoable(true);
		textShapeCreator.setPrototypeShape(textPrototype);
		}

	public		void		processCommand(String cmd)
		{
		if (cmd.equalsIgnoreCase(SELECTION_MODE_COMMAND_NAME))
			{
			// These three lines for handling ESC key restoration of selection-mode
			selectionArrowButton.select(true);
			rubberStampButton.select(false);
			if (getEditor().getNumberOfContainers() > 0)
				{
				// To make things look right, focus goes to box which contains and delegates
				// to getEditor(), FIX: box should actually monitor getEditor() and take focus away from it.
				getEditor().getContainer(0).requestKeyboardFocus();
				getEditor().getContainer(0).requestEnterKeyFocus();
				}

			disableAllInteractiveEventHandlers();
			if (toolBarManager != null)
				toolBarManager.selectionToolSelected(this, SELECTION_MODE_COMMAND_NAME);
			}
		else if (cmd.equalsIgnoreCase(RUBBER_STAMP_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			inRubberStampMode = true;
			rectangularShapeCreator.setInRubberStampMode(true);
			}
		else if (cmd.equalsIgnoreCase(NOT_RUBBER_STAMP_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			inRubberStampMode = false;
			rectangularShapeCreator.setInRubberStampMode(false);
			}
		else if (cmd.equalsIgnoreCase(LINE_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			multiPointShapeCreator.setEnabled(true);
			multiPointShapeCreator.setPrototypeShape(linePrototype);
			if (toolBarManager != null)
				toolBarManager.drawingToolSelected(this, null, cmd);
			}
		else if (cmd.equalsIgnoreCase(RECTANGLE_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			rectangularShapeCreator.setEnabled(true);
			rectangularShapeCreator.setPrototypeShape(rectPrototype);
			if (toolBarManager != null)
				toolBarManager.drawingToolSelected(this, null, cmd);
			}
		else if (cmd.equalsIgnoreCase(ROUND_RECTANGLE_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			rectangularShapeCreator.setEnabled(true);
			rectangularShapeCreator.setPrototypeShape(roundRectPrototype);
			if (toolBarManager != null)
				toolBarManager.drawingToolSelected(this, null, cmd);
			}
		else if (cmd.equalsIgnoreCase(ARC_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			multiPointShapeCreator.setEnabled(true);
			multiPointShapeCreator.setPrototypeShape(arcPrototype);
			if (toolBarManager != null)
				toolBarManager.drawingToolSelected(this, null, cmd);
			}
		else if (cmd.equalsIgnoreCase(ELLIPSE_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			rectangularShapeCreator.setEnabled(true);
			rectangularShapeCreator.setPrototypeShape(ellipsePrototype);
			if (toolBarManager != null)
				toolBarManager.drawingToolSelected(this, null, cmd);
			}
		else if (cmd.equalsIgnoreCase(POLYGON_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			multiPointShapeCreator.setEnabled(true);
			multiPointShapeCreator.setPrototypeShape(polygonPrototype);
			if (toolBarManager != null)
				toolBarManager.drawingToolSelected(this, null, cmd);
			}
		else if (cmd.equalsIgnoreCase(PENCIL_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			pencilShapeCreator.setEnabled(true);
			pencilShapeCreator.setPrototypeShape(pencilPrototype);
			if (toolBarManager != null)
				toolBarManager.drawingToolSelected(this, null, cmd);
			}
		else if (cmd.equalsIgnoreCase(TEXT_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			textShapeCreator.setEnabled(true);
			if (toolBarManager != null)
				toolBarManager.drawingToolSelected(this, null, cmd);
			}
		else if (cmd.equalsIgnoreCase(MiDrawingToolBarManager.Mi_NEW_SHAPE_ATTRIBUTES_CHANGED_STATE_NAME))
			{
			setPrototypeShapesAttributes(toolBarManager.getNewShapeAttributes());
			}
		else if ((cmd.startsWith(MiDrawingToolBarManager.Mi_DRAWING_TOOL_SELECTED_CMD_NAME))
			|| (cmd.startsWith(MiDrawingToolBarManager.Mi_SELECTION_TOOL_SELECTED_CMD_NAME)))
			{
			String toolItemCmdName = null;
			if (cmd.startsWith(MiDrawingToolBarManager.Mi_DRAWING_TOOL_SELECTED_CMD_NAME))
				{
				toolItemCmdName = cmd.substring(MiDrawingToolBarManager.Mi_DRAWING_TOOL_SELECTED_CMD_NAME.length());
				}
			else 
				{
				toolItemCmdName = cmd.substring(MiDrawingToolBarManager.Mi_SELECTION_TOOL_SELECTED_CMD_NAME.length());
				}
//MiDebug.println("cmd = " + cmd);
//MiDebug.println("toolItemCmdName = " + toolItemCmdName);
//MiDebug.printStackTrace();
			MiWidget widget = getToolItemWithCommand(toolItemCmdName);
			disableAllInteractiveEventHandlers();
			for (int i = 0; i < getNumberOfParts(); ++i)
				{
				MiPart part = getPart(i);
				if ((part.isSelected()) && (part instanceof MiWidget))
					{
					if (((MiWidget )part).getRadioStateEnforcer() != null)
						((MiWidget )part).getRadioStateEnforcer().setMinNumSelected(0);
					part.select(false);
					if (((MiWidget )part).getRadioStateEnforcer() != null)
						((MiWidget )part).getRadioStateEnforcer().setMinNumSelected(1);
					}
				}

			// If this toolbar had the widget that was selected...
			if (widget != null)
				{
				// Set to null so do not send out another message to the command manager that this cmd was selected
				MiDrawingToolBarManager	currentToolBarManager = toolBarManager;
				toolBarManager = null;

				widget.select(true);

				toolBarManager = currentToolBarManager;
				}
			}
		else 
			{
			super.processCommand(cmd);
			}
		}
	protected	void		disableAllInteractiveEventHandlers()
		{
		rectangularShapeCreator.setEnabled(false);
		multiPointShapeCreator.setEnabled(false);
		pencilShapeCreator.setEnabled(false);
		textShapeCreator.setEnabled(false);
		}
	protected	void		setPrototypeShapesAttributes(MiAttributes attributes)
		{
		for (int i = 0; i < prototypeShapes.size(); ++i)
			prototypeShapes.elementAt(i).setAttributes(attributes);

		for (int i = 0; i < drawButtonShapes.size(); ++i)
			drawButtonShapes.elementAt(i).setAttributes(attributes);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_NO_ITEMS_SELECTED_ACTION))
			{
			handleSelectionState(0);
			}
		else if (action.hasActionType(Mi_ONE_ITEM_SELECTED_ACTION))
			{
			handleSelectionState(1);
			}
		else if (action.hasActionType(Mi_MANY_ITEMS_SELECTED_ACTION))
			{
			handleSelectionState(2); // takes too long when selecting area containing many items, one at a time getEditor().getNumberOfPartsSelected());
			}
		else if (action.hasActionType(Mi_DATA_IMPORT_ACTION))
			{
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();
			MiPart obj = (MiPart )transferOp.getTransferredData();
			// Not for nodes, or icons, .... obj.setAttributes(arcPrototype.getAttributes());
			if (selectNewlyCreatedShape)
				getEditor().select(obj);
			}
		else if (action.hasActionType(Mi_INTERACTIVELY_COMPLETED_DRAW_NEW_PART_PART_ACTION))
			{
			// Automatic restoration of state to 'selection arrow' state
			if ((!inRubberStampMode) && (restoreToSelectionStateWhenShapeSelected))
				{
				selectionArrowButton.select(true);
				}
			}
		return(true);
		}
	protected	void		setDNDBehaviorOfDrawShapeButton(MiPart button, MiPart shape)
		{
		MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));
		dndBehavior.setDragsReferenceNotCopy(true);
		dndBehavior.setIsDefaultBehaviorForParts(false);
		dndBehavior.setSnapLookCenterToCursor(false);
		dndBehavior.setKeepLookCompletelyWithinRootWindow(false);
		dndBehavior.setDataToExport(shape);
		// Do this so that the shape appears to be closer to what it will be when dropped
		MiPart dragShape = shape.copy();
		dragShape.scale(0.3, 0.3);
		dndBehavior.setDraggingLook(dragShape);
		button.setDragAndDropBehavior(dndBehavior);
		button.setIsDragAndDropSource(true);
		drawButtonShapes.addElement(shape);
		drawButtonShapes.addElement(dragShape);
		}
	protected	void		handleSelectionState(int num)
		{
		selectedShapes.removeAllElements();
		if (num == 0)
			{
			}
		else
			{
			// Automatic restoration of state to 'selection arrow' state
			if ((!inRubberStampMode) && (restoreToSelectionStateWhenShapeSelected))
				selectionArrowButton.select(true);
			}
		}
	}
class MiIExecuteCommandUponMonitoredEvent extends MiEventMonitor
	{
	private		MiiCommandHandler	handler;
	private		String			arg;
	private		int			returnCode;


	protected			MiIExecuteCommandUponMonitoredEvent()
		{
		// Used for copies
		}

	public				MiIExecuteCommandUponMonitoredEvent(MiEvent trigger, MiiCommandHandler handler)
		{
		this(trigger, handler, null);
		}

	public				MiIExecuteCommandUponMonitoredEvent(MiEvent trigger, MiiCommandHandler handler, String arg)
		{
		this(trigger, handler, arg, Mi_PROPOGATE_EVENT);
		}
	public				MiIExecuteCommandUponMonitoredEvent(MiEvent trigger, MiiCommandHandler handler, String arg, int returnCode)
		{
		addEventToCommandTranslation(Mi_EXECUTE_COMMAND_NAME, trigger.type, trigger.key, trigger.modifiers);
		this.handler = handler;
		this.arg = arg;
		this.returnCode = returnCode;
		}
	public		MiiCommandHandler 	getCommandHandler()
		{
		return(handler);
		}
	public		void 		setCommandHandler(MiiCommandHandler m)
		{
		handler = m;
		}

	public		String 		getCommand()
		{
		return(arg);
		}
	public		void 		setCommand(String arg)
		{
		this.arg = arg;
		}

					/**------------------------------------------------------
	 				 * Processes the command generated from the current event.
					 * Both are stored in the MiEventHandler super class.
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see the event that
					 *			generated the command
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see the event
					 *			that generated the command
					 * @see			MiEventHandler#isCommand
					 * @overrides		MiEventHandler#processCommand
					 *------------------------------------------------------*/
	protected	int		processCommand()
		{
		handler.processCommand(arg);
		return(returnCode);
		}
					/**------------------------------------------------------
	 				 * Returns a copy of this MiEventHandler.
	 				 * @return 	 	the copy
					 * @implements		MiiEventHandler#copy
					 *------------------------------------------------------*/
	public		MiiEventHandler	copy()
		{
		MiIExecuteCommandUponMonitoredEvent obj 
					= (MiIExecuteCommandUponMonitoredEvent )super.copy();
		obj.handler		= handler;
		obj.arg			= arg;
		obj.returnCode		= returnCode;
		return(obj);
		}
					/**------------------------------------------------------
					 * Returns information about this MiEventHandler.
					 * @return		textual information (class name +
					 *			unique numerical id + [disabled])
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(super.toString() + "<ToExecute: " + handler + ".processCommand(" + arg + ")>");
		}
	}
