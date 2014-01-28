
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

/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDrawingConnectionToolBar extends MiToolBar implements MiiCommandNames, MiiActionHandler
	{
	public static final String	STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME	= "straightConnDrawMode";
	public static final String	Z_CONN_DRAW_MODE_COMMAND_NAME		= "zConnDrawMode";
	public static final String	L_CONN_DRAW_MODE_COMMAND_NAME		= "lConnDrawMode";
	public static final String	ARC_CONN_DRAW_MODE_COMMAND_NAME		= "arcConnDrawMode";
	public static final String	FREE_FORM_CONN_DRAW_MODE_COMMAND_NAME	= "freeFormConnDrawMode";
	public static final String	CONN_PT_DRAW_MODE_COMMAND_NAME		= "connPointDrawMode";
	public static final String	ANNO_PT_DRAW_MODE_COMMAND_NAME		= "annoPointDrawMode";
	public static final String	STRAIGHT_CONN_DRAW_MODE_STATUS_MSG	
					= "Enables/disables the drawing of straight line connections";
	public static  String	STRAIGHT_CONN_DRAW_MODE_DISABLED_STATUS_MSG
					= "Enables/disables the drawing of straight line connections";
	public static  String	L_CONN_DRAW_MODE_STATUS_MSG
					= "Enables/disables the drawing of manually places orthogonal line connections";
	public static  String	L_CONN_DRAW_MODE_DISABLED_STATUS_MSG
					= "Enables/disables the drawing of manually placed orthogonal line connections";
	public static  String	Z_CONN_DRAW_MODE_STATUS_MSG
					= "Enables/disables the drawing of automatically placed orthogonal line connections";
	public static  String	Z_CONN_DRAW_MODE_DISABLED_STATUS_MSG
					= "Enables/disables the drawing of automatically placed orthogonal line connections";
	public static  String	ARC_CONN_DRAW_MODE_STATUS_MSG
					= "Enables/disables the drawing of arc line connections";
	public static  String	ARC_CONN_DRAW_MODE_DISABLED_STATUS_MSG
					= "Enables/disables the drawing of arc line connections";
	public static  String	FREE_FORM_CONN_DRAW_MODE_STATUS_MSG
					= "Enables/disables the drawing of automatically created polyline connections";
	public static  String	FREE_FORM_CONN_DRAW_MODE_DISABLED_STATUS_MSG
					= "Enables/disables the drawing of automatically created polyline connections";
	public static  String	CONN_PT_DRAW_MODE_STATUS_MSG
					= "Enables/disables the manual placing of connection points";
	public static  String	CONN_PT_DRAW_MODE_DISABLED_STATUS_MSG
					= "Enables/disables the manual placing of connection points";
	public static  String	ANNO_PT_DRAW_MODE_STATUS_MSG
					= "Enables/disables the manual placing of annotation (text entry) points";
	public static  String	ANNO_PT_DRAW_MODE_DISABLED_STATUS_MSG
					= "Enables/disables the manual placing of annotation (text entry) points";

	private static final String	Mi_MANAGED_POINTS_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME	
									= "managedPointAttDialogChanged";

	private		MiICreateConnection		connectionCreator;
	private		MiICreateSelectDeleteManagedPoints createSelectDeleteManagedPointsEventHandler;
	private		MiICreateConnectionUsingConnPoint connectionCreatorUsingConnPt;
	private		MiICreateConnection 		secondBtnCreateConn;

	private		MiConnectionPointManager	 connectionPointManagerKind = new MiConnectionPointManager();
	private		MiAnnotationPointManager	 annotationPointManagerKind = new MiAnnotationPointManager();

	private		MiDrawingToolBarManager		toolBarManager;

	private		boolean				selectNewlyCreatedShape			= true;
	private		boolean				applyDrawModesToSelectedConnections	= true;

	private		MiManagedPointsAttributesDialog	managedPointsAttributesDialog;

	private		MiConnection	prototype	 	= new MiConnection();

	private		MiParts		drawButtonShapes	= new MiParts();
	private		MiParts		selectedShapes		= new MiParts();


	private		MiWidget	connPtWidget;
	private		MiWidget	annoPtWidget;

	private		String		selectedConnectionMode;
	private		String		originalConnectionMode;

	private		MiSize		defaultPrototypeSize 	= new MiSize(50, 50); // rubberStampSize
	private		String		currentCommandState	= STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME;
	private		boolean		connectionsCreatedByUsingConnPtsOnly;
	private		boolean		thisActsAsAttributeToolNotDrawingTool;
	private		MiAttributes	currentAttributes;


	protected			MiDrawingConnectionToolBar()
		{
		// Used for copy operation.
		}
	public				MiDrawingConnectionToolBar(
						MiEditor editor, 
						MiiCommandManager manager)
		{
		super(editor, manager);
		setup();
		}

	public		void		setToolBarManager(MiDrawingToolBarManager manager)
		{
		this.toolBarManager = manager;
		}
	public		void		setManagedPointsAttributesDialog(MiManagedPointsAttributesDialog dialog)
		{
		managedPointsAttributesDialog = dialog;
		managedPointsAttributesDialog.appendCommandHandler(
			this, Mi_MANAGED_POINTS_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME, Mi_VALUE_CHANGED_ACTION);
		}

	public		void		setSelectNewlyCreatedShape(boolean flag)
		{
		selectNewlyCreatedShape = flag;
		connectionCreator.setSelectNewlyCreatedConnections(flag);
		secondBtnCreateConn.setSelectNewlyCreatedConnections(flag);
		connectionCreatorUsingConnPt.setSelectNewlyCreatedConnections(flag);
		}
	public		boolean		getSelectNewlyCreatedShape()
		{
		return(selectNewlyCreatedShape);
		}
	public		void		setActingAsAttributeToolNotDrawingTool(boolean flag)
		{
		thisActsAsAttributeToolNotDrawingTool = flag;
		}
	public		boolean		getActingAsAttributeToolNotDrawingTool()
		{
		return(thisActsAsAttributeToolNotDrawingTool);
		}
	public		void		setApplyDrawModesToSelectedConnections(boolean flag)
		{
		applyDrawModesToSelectedConnections = flag;
		}
	public		boolean		getApplyDrawModesToSelectedConnections()
		{
		return(applyDrawModesToSelectedConnections);
		}
	public		void		setConnectionsMustBeConnectedAtBothEnds(boolean flag)
		{
		prototype.setConnectionsMustBeConnectedAtBothEnds(flag);
		}
	public		void		setConnectionsCreatedByUsingConnPtsOnly(boolean flag)
		{
		connectionsCreatedByUsingConnPtsOnly = flag;
		}
	public		void		setConnectableToConnections(boolean flag)
		{
		connectionCreator.setConnectableToConnections(flag);
		connectionCreatorUsingConnPt.setConnectableToConnections(flag);
		secondBtnCreateConn.setConnectableToConnections(flag);
		}
	public		void		setLineSnappedToConnectPointAttributes(MiAttributes atts)
		{
		connectionCreator.setLineSnappedToConnectPointAttributes(atts);
		connectionCreatorUsingConnPt.setLineSnappedToConnectPointAttributes(atts);
		secondBtnCreateConn.setLineSnappedToConnectPointAttributes(atts);
		}
	public		void		setSnapToConnectionPointDistance(MiDistance d)
		{
		connectionCreator.setSnapToConnectionPointDistance(d);
		connectionCreatorUsingConnPt.setSnapToConnectionPointDistance(d);
		secondBtnCreateConn.setSnapToConnectionPointDistance(d);
		}
	public		void		setConnectionConnectionPointLook(MiPart look)
		{
		connectionCreator.setConnectionConnectionPointLook(look);
		connectionCreatorUsingConnPt.setConnectionConnectionPointLook(look);
		secondBtnCreateConn.setConnectionConnectionPointLook(look);
		}
	public		void		setConnectionJunctionPoint(MiConnectionJunctionPoint prototype)
		{
		connectionCreator.setPrototypeConnectionJunctionPoint(prototype);
		connectionCreatorUsingConnPt.setPrototypeConnectionJunctionPoint(prototype);
		secondBtnCreateConn.setPrototypeConnectionJunctionPoint(prototype);
		}
	public		MiPart		getAnnoPointToggleButton()
		{
		return(annoPtWidget);
		}
	public		MiPart		getConnPointToggleButton()
		{
		return(connPtWidget);
		}
	public		void		setPrototypes(MiConnection prototype)
		{
		this.prototype = (MiConnection )prototype.copy();
		if (defaultPrototypeSize != null)
			this.prototype.setSize(defaultPrototypeSize);
		currentAttributes = prototype.getAttributes();

		if (currentCommandState != null)
			processCommand(currentCommandState);
		}
	public		MiConnection	getPrototypes()
		{
		return(prototype);
		}
					// Can be null if no size modification is desired
	public		void		setDefaultPrototypeSize(MiSize size)
		{
		if (size != null)
			defaultPrototypeSize = new MiSize(size);
		else
			defaultPrototypeSize = null;
		}
	public		MiSize		getDefaultPrototypeSize()
		{
		return(defaultPrototypeSize == null ? null : new MiSize(defaultPrototypeSize));
		}
	protected	void		setup()
		{
		processCommand(Mi_HIDE_LABELS_COMMAND_NAME);

		getEditor().appendActionHandler(this, 
				Mi_ACTIONS_OF_PARTS_OF_OBSERVED 
				| Mi_ACTIONS_OF_OBSERVED 
				| Mi_DATA_IMPORT_ACTION);

		getDragAndDropBehavior().setValidTargets(new MiParts(this));

		getEditor().appendActionHandler(new MiAction(this, 
				Mi_NO_ITEMS_SELECTED_ACTION,
				Mi_ONE_ITEM_SELECTED_ACTION,
				Mi_MANY_ITEMS_SELECTED_ACTION,
				null));

		setConnectionsMustBeConnectedAtBothEnds(false);

		getEditor().appendEventHandler(new MiIDragConnection());

		connectionCreator = new MiICreateConnection();
		connectionCreator.setSelectNewlyCreatedConnections(true);

		connectionCreator.getValidConnPointFinder().setMethodology(
			MiClosestValidManagedPointFinder.EXAMINE_CONN_POINT_MANAGERS
			| MiClosestValidManagedPointFinder.EXAMINE_COMMON_POINTS_IF_NO_POINT_MANAGERS 
			| MiClosestValidManagedPointFinder.EXAMINE_PARTS_OF_CANDIDATES);

		
		createSelectDeleteManagedPointsEventHandler = new MiICreateSelectDeleteManagedPoints();

		secondBtnCreateConn 
			= (MiICreateConnection )getEditor().getEventHandlerWithClass("MiICreateConnection");
		//if (secondBtnCreateConn != null)
			//getEditor().removeEventHandler(secondBtnCreateConn);
		secondBtnCreateConn.setSelectNewlyCreatedConnections(true);
		secondBtnCreateConn.getValidConnPointFinder().setMethodology(
			MiClosestValidManagedPointFinder.EXAMINE_CONN_POINT_MANAGERS
			| MiClosestValidManagedPointFinder.EXAMINE_COMMON_POINTS_IF_NO_POINT_MANAGERS
			| MiClosestValidManagedPointFinder.EXAMINE_PARTS_OF_CANDIDATES);


		connectionCreator.addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		connectionCreator.addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, 
			Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		connectionCreator.addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, 
			Mi_LEFT_MOUSE_UP_EVENT, 0, 0);

		connectionCreatorUsingConnPt = new MiICreateConnectionUsingConnPoint();
		connectionCreatorUsingConnPt.getValidConnPointFinder().setMethodology(
			MiClosestValidManagedPointFinder.EXAMINE_CONN_POINT_MANAGERS
			| MiClosestValidManagedPointFinder.EXAMINE_COMMON_POINTS_IF_NO_POINT_MANAGERS
			| MiClosestValidManagedPointFinder.EXAMINE_PARTS_OF_CANDIDATES);
		connectionCreatorUsingConnPt.setSelectNewlyCreatedConnections(true);
		MiConnectionPointManager.getGlobalLook().appendEventHandler(connectionCreatorUsingConnPt);

		MiConnectionPointManager.setGloballyVisible(true);
		MiAnnotationPointManager.setGloballyVisible(true);

		disableAllInteractiveEventHandlers();

		getEditor().insertEventHandler(connectionCreator, 0);
		getEditor().insertEventHandler(createSelectDeleteManagedPointsEventHandler, 0);
		//getEditor().getSelectionManager().setSelectionGraphics(new MiManipulatorSelectionGraphics());

		MiPart drawStraightConnImage = new MiImage("${Mi_IMAGES_HOME}/connect.xpm", true);
		MiPart drawLConnImage = new MiImage("${Mi_IMAGES_HOME}/connectL.xpm", true);
		MiPart drawZConnImage = new MiImage("${Mi_IMAGES_HOME}/connectZ.xpm", true);
		MiPart drawArcConnImage = new MiImage("${Mi_IMAGES_HOME}/connectArc.xpm", true);
		MiPart drawFreeFormConnImage = new MiImage("${Mi_IMAGES_HOME}/connectFreeForm.xpm", true);
		//MiPart connPtImage = new MiImage("${Mi_IMAGES_HOME}/connectionPoint.xpm", true);
		MiPart connPtImage = MiConnectionPointManager.getGlobalLook().deepCopy();
		connPtImage.setSize(new MiSize(8, 8));
		MiPart annoPtImage = MiAnnotationPointManager.getGlobalLook().deepCopy();
		annoPtImage.setContextCursor(Mi_DEFAULT_CURSOR);
		annoPtImage.setSize(new MiSize(8, 8));

		appendSpacer();
		startRadioButtonSection();
		appendBooleanToolItem("Connect", drawStraightConnImage, this, 
								STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Connect", drawLConnImage, this, 
								L_CONN_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Connect", drawZConnImage, this, 
								Z_CONN_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Connect", drawArcConnImage, this, 
								ARC_CONN_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		appendBooleanToolItem("Connect", drawFreeFormConnImage, this, 
								FREE_FORM_CONN_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		connPtWidget = appendBooleanToolItem("Connection Point", connPtImage, this, 
								CONN_PT_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		connPtWidget.setInsetMargins(new MiMargins(10));
		annoPtWidget = appendBooleanToolItem("Annotation Point", annoPtImage, this, 
								ANNO_PT_DRAW_MODE_COMMAND_NAME,
								Mi_NOOP_COMMAND_NAME);
		annoPtWidget.setInsetMargins(new MiMargins(10));
		endRadioButtonSection();
		appendSpacer();

		setHelpMessages(STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME,
			STRAIGHT_CONN_DRAW_MODE_STATUS_MSG,
			STRAIGHT_CONN_DRAW_MODE_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(L_CONN_DRAW_MODE_COMMAND_NAME,
			L_CONN_DRAW_MODE_STATUS_MSG,
			L_CONN_DRAW_MODE_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(Z_CONN_DRAW_MODE_COMMAND_NAME,
			Z_CONN_DRAW_MODE_STATUS_MSG,
			Z_CONN_DRAW_MODE_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(ARC_CONN_DRAW_MODE_COMMAND_NAME,
			ARC_CONN_DRAW_MODE_STATUS_MSG,
			ARC_CONN_DRAW_MODE_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(FREE_FORM_CONN_DRAW_MODE_COMMAND_NAME,
			FREE_FORM_CONN_DRAW_MODE_STATUS_MSG,
			FREE_FORM_CONN_DRAW_MODE_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(CONN_PT_DRAW_MODE_COMMAND_NAME,
			CONN_PT_DRAW_MODE_STATUS_MSG,
			CONN_PT_DRAW_MODE_DISABLED_STATUS_MSG,
			null);

		setHelpMessages(ANNO_PT_DRAW_MODE_COMMAND_NAME,
			ANNO_PT_DRAW_MODE_STATUS_MSG,
			ANNO_PT_DRAW_MODE_DISABLED_STATUS_MSG,
			null);

		setToolItemImageSizes(new MiSize(24, 24));

		//MiConnectionPointManager connectionPointManager = connectionPointManagerKind;
		MiAnnotationPointManager annotationPointManager = new MiAnnotationPointManager();
		annotationPointManager.appendManagedPoint(Mi_LINE_CENTER_LOCATION);

		prototype.setAnnotationPointManager(annotationPointManager);
		if (defaultPrototypeSize != null)
			prototype.setSize(defaultPrototypeSize);

		currentAttributes = getAttributes();
		}

	public		void		processCommand(String cmd)
		{
		if (cmd.equalsIgnoreCase(STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME))
			{
			if (applyDrawModesToSelectedConnections)
				applyDrawModesToSelectedConnections(cmd);
			else
				getEditor().deSelectAll();

			disableAllInteractiveEventHandlers();
			connectionCreator.setEnabled(!connectionsCreatedByUsingConnPtsOnly);
			prototype.setLayout(null);
			prototype.setGraphics(new MiLine());
			prototype.setAttributes(currentAttributes);
			connectionCreator.setPrototype(prototype);
			connectionCreator.setMinDistancePerSegment(0);
			secondBtnCreateConn.setPrototype(prototype);
			secondBtnCreateConn.setMinDistancePerSegment(0);
			connectionCreatorUsingConnPt.setMinDistancePerSegment(0);
			connectionCreatorUsingConnPt.setPrototype(prototype);
			MiConnectionPointManager.getGlobalRule().setPrototypeConnection(prototype);
			if ((toolBarManager != null) && (!thisActsAsAttributeToolNotDrawingTool))
				toolBarManager.drawingToolSelected(this, null, cmd);

			currentCommandState = cmd;
			}
		else if (cmd.equalsIgnoreCase(L_CONN_DRAW_MODE_COMMAND_NAME))
			{
			if (applyDrawModesToSelectedConnections)
				applyDrawModesToSelectedConnections(cmd);
			else
				getEditor().deSelectAll();

			disableAllInteractiveEventHandlers();
			connectionCreator.setEnabled(!connectionsCreatedByUsingConnPtsOnly);
			prototype.setLayout(new MiLConnectionLayout());
			prototype.setGraphics(new MiLine());
			prototype.setAttributes(currentAttributes);
			connectionCreator.setPrototype(prototype);
			connectionCreator.setMinDistancePerSegment(0);
			secondBtnCreateConn.setPrototype(prototype);
			secondBtnCreateConn.setMinDistancePerSegment(0);
			connectionCreatorUsingConnPt.setMinDistancePerSegment(0);
			connectionCreatorUsingConnPt.setPrototype(prototype);
			MiConnectionPointManager.getGlobalRule().setPrototypeConnection(prototype);
			if ((toolBarManager != null) && (!thisActsAsAttributeToolNotDrawingTool))
				toolBarManager.drawingToolSelected(this, null, cmd);

			currentCommandState = cmd;
			}
		else if (cmd.equalsIgnoreCase(Z_CONN_DRAW_MODE_COMMAND_NAME))
			{
			if (applyDrawModesToSelectedConnections)
				applyDrawModesToSelectedConnections(cmd);
			else
				getEditor().deSelectAll();

			disableAllInteractiveEventHandlers();
			connectionCreator.setEnabled(!connectionsCreatedByUsingConnPtsOnly);
			prototype.setLayout(new MiZConnectionLayout());
			prototype.setGraphics(new MiLine());
			prototype.setAttributes(currentAttributes);
			connectionCreator.setPrototype(prototype);
			connectionCreator.setMinDistancePerSegment(0);
			secondBtnCreateConn.setPrototype(prototype);
			secondBtnCreateConn.setMinDistancePerSegment(0);
			connectionCreatorUsingConnPt.setMinDistancePerSegment(0);
			connectionCreatorUsingConnPt.setPrototype(prototype);
			MiConnectionPointManager.getGlobalRule().setPrototypeConnection(prototype);
			if ((toolBarManager != null) && (!thisActsAsAttributeToolNotDrawingTool))
				toolBarManager.drawingToolSelected(this, null, cmd);

			currentCommandState = cmd;
			}
		else if (cmd.equalsIgnoreCase(ARC_CONN_DRAW_MODE_COMMAND_NAME))
			{
			if (applyDrawModesToSelectedConnections)
				applyDrawModesToSelectedConnections(cmd);
			else
				getEditor().deSelectAll();

			disableAllInteractiveEventHandlers();
			connectionCreator.setEnabled(!connectionsCreatedByUsingConnPtsOnly);
			prototype.setLayout(null);
			Mi3PointArc arc = new Mi3PointArc();
			arc.setOrthoDistance(50);
			//prototype.setBackgroundColor(Mi_TRANSPARENT_COLOR);
			prototype.setGraphics(arc);
			connectionCreator.setPrototype(prototype);
			connectionCreator.setMinDistancePerSegment(0);
			secondBtnCreateConn.setPrototype(prototype);
			secondBtnCreateConn.setMinDistancePerSegment(0);
			connectionCreatorUsingConnPt.setMinDistancePerSegment(0);
			connectionCreatorUsingConnPt.setPrototype(prototype);
			MiConnectionPointManager.getGlobalRule().setPrototypeConnection(prototype);
			if ((toolBarManager != null) && (!thisActsAsAttributeToolNotDrawingTool))
				toolBarManager.drawingToolSelected(this, null, cmd);

			currentCommandState = cmd;
			}
		else if (cmd.equalsIgnoreCase(FREE_FORM_CONN_DRAW_MODE_COMMAND_NAME))
			{
			if (applyDrawModesToSelectedConnections)
				applyDrawModesToSelectedConnections(cmd);
			else
				getEditor().deSelectAll();

			disableAllInteractiveEventHandlers();
			connectionCreator.setEnabled(!connectionsCreatedByUsingConnPtsOnly);
			prototype.setLayout(null);
			prototype.setGraphics(new MiLine());
			prototype.setAttributes(currentAttributes);
			connectionCreator.setPrototype(prototype);
			connectionCreator.setMinDistancePerSegment(100);
			secondBtnCreateConn.setPrototype(prototype);
			secondBtnCreateConn.setMinDistancePerSegment(100);
			connectionCreatorUsingConnPt.setMinDistancePerSegment(100);
			connectionCreatorUsingConnPt.setPrototype(prototype);
			MiConnectionPointManager.getGlobalRule().setPrototypeConnection(prototype);
			if ((toolBarManager != null) && (!thisActsAsAttributeToolNotDrawingTool))
				toolBarManager.drawingToolSelected(this, null, cmd);

			currentCommandState = cmd;
			}
		else if (cmd.equalsIgnoreCase(CONN_PT_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			createSelectDeleteManagedPointsEventHandler.setEnabled(true);
			createSelectDeleteManagedPointsEventHandler
				.setPointManagerKind(connectionPointManagerKind);
			if ((toolBarManager != null) && (!thisActsAsAttributeToolNotDrawingTool))
				toolBarManager.drawingToolSelected(this, null, cmd);

			currentCommandState = cmd;
			}
		else if (cmd.equalsIgnoreCase(ANNO_PT_DRAW_MODE_COMMAND_NAME))
			{
			getEditor().deSelectAll();
			disableAllInteractiveEventHandlers();
			createSelectDeleteManagedPointsEventHandler.setEnabled(true);
			createSelectDeleteManagedPointsEventHandler
				.setPointManagerKind(annotationPointManagerKind);
			if ((toolBarManager != null) && (!thisActsAsAttributeToolNotDrawingTool))
				toolBarManager.drawingToolSelected(this, null, cmd);

			currentCommandState = cmd;
			}
		else if (cmd.equalsIgnoreCase(MiDrawingToolBarManager.Mi_NEW_SHAPE_ATTRIBUTES_CHANGED_STATE_NAME))
			{
			setPrototypeShapesAttributes(toolBarManager.getNewShapeAttributes());
			}
		else if (cmd.equalsIgnoreCase(Mi_MANAGED_POINTS_ATTRIBUTE_DIALOG_CHANGED_CMD_NAME))
			{
			if (selectedShapes.size() == 0)
				{
				// Invalidate areas of all shapes:
				getEditor().invalidateArea();

				//defaultPointManager = manager;
				//if (toolBarManager != null)
					//toolBarManager.setNewShapeAttributes(defaultAttributes);
				}
			else
				{
				for (int i = 0; i < selectedShapes.size(); ++i)
					selectedShapes.elementAt(i).invalidateArea();
				}
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
		connectionCreator.setEnabled(false);
		//secondBtnCreateConn.setEnabled(false);
		createSelectDeleteManagedPointsEventHandler.deSelectAll();
		createSelectDeleteManagedPointsEventHandler.setEnabled(false);
		}
	protected	void		setPrototypeShapesAttributes(MiAttributes attributes)
		{
		prototype.setAttributes(attributes);
		currentAttributes = attributes;
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
			if (managedPointsAttributesDialog != null) 
				{
				managedPointsAttributesDialog.setTargetShapes(selectedShapes);
				}
			if (originalConnectionMode != null)
				setConnectionDrawMode(originalConnectionMode);
			}
		else
			{
			getEditor().getAFewSelectedParts(selectedShapes);
			if (managedPointsAttributesDialog != null)
				{
				managedPointsAttributesDialog.setTargetShapes(selectedShapes);
				}
			if ((selectedShapes.size() == 1) && (selectedShapes.elementAt(0) instanceof MiConnection))
				{
				MiConnection conn = (MiConnection )selectedShapes.elementAt(0);

				if (selectedConnectionMode != getConnectionDrawMode())
					{
					originalConnectionMode = getConnectionDrawMode();
					}

				if (conn.getLayout() instanceof MiZConnectionLayout)
					{
					selectedConnectionMode = Z_CONN_DRAW_MODE_COMMAND_NAME;
					}
				else if (conn.getLayout() instanceof MiLConnectionLayout)
					{
					selectedConnectionMode = L_CONN_DRAW_MODE_COMMAND_NAME;
					}
				else if (conn.getGraphics() instanceof Mi3PointArc)
					{
					selectedConnectionMode = ARC_CONN_DRAW_MODE_COMMAND_NAME;
					}
				else 
					{
					selectedConnectionMode = STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME;
					}

				setConnectionDrawMode(selectedConnectionMode);
				}
			else
				{
				if (originalConnectionMode != null)
					{
					setConnectionDrawMode(originalConnectionMode);
					}
				}
			}
		}
	protected	void		applyDrawModesToSelectedConnections(String mode)
		{
		getEditor().getSelectedParts(selectedShapes);
		String currentConnectionMode = getConnectionDrawMode();
		for (int i = 0; i < selectedShapes.size(); ++i)
			{
			if (selectedShapes.elementAt(i) instanceof MiConnection)
				{
				MiConnection conn = (MiConnection )selectedShapes.elementAt(i);

				if ((currentConnectionMode == Z_CONN_DRAW_MODE_COMMAND_NAME)
					&& (!(conn.getLayout() instanceof MiZConnectionLayout)))
					{
					conn.setLayout(new MiZConnectionLayout());
					conn.setGraphics(new MiLine());
					conn.validateLayout();
					//conn.setBackgroundColor(currentAttributes.getBackgroundColor());
					}
				else if ((currentConnectionMode == L_CONN_DRAW_MODE_COMMAND_NAME)
					&& (!(conn.getLayout() instanceof MiLConnectionLayout)))
					{
					conn.setLayout(new MiLConnectionLayout());
					conn.setGraphics(new MiLine());
					conn.validateLayout();
					//conn.setBackgroundColor(currentAttributes.getBackgroundColor());
					}
				else if ((currentConnectionMode == ARC_CONN_DRAW_MODE_COMMAND_NAME)
					&& (!(conn.getGraphics() instanceof Mi3PointArc)))
					{
					conn.setLayout(null);
					Mi3PointArc arc = new Mi3PointArc();
					arc.setOrthoDistance(50);
					conn.setGraphics(arc);
					// Do this so we don't get a filled arc...
					if (conn.getLineWidth() == 0)
						conn.setLineWidth(1);
					//conn.setBackgroundColor(Mi_TRANSPARENT_COLOR);
					}
				else if (currentConnectionMode == STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME)
					{
					conn.setLayout(null);
					conn.setGraphics(new MiLine());
					//conn.setBackgroundColor(currentAttributes.getBackgroundColor());
					}
				}
			}
		}
	public		String		getConnectionDrawMode()
		{
		if (getToolItemWithCommand(STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME).isSelected())
			return(STRAIGHT_CONN_DRAW_MODE_COMMAND_NAME);
		if (getToolItemWithCommand(Z_CONN_DRAW_MODE_COMMAND_NAME).isSelected())
			return(Z_CONN_DRAW_MODE_COMMAND_NAME);
		if (getToolItemWithCommand(L_CONN_DRAW_MODE_COMMAND_NAME).isSelected())
			return(L_CONN_DRAW_MODE_COMMAND_NAME);
		if (getToolItemWithCommand(ARC_CONN_DRAW_MODE_COMMAND_NAME).isSelected())
			return(ARC_CONN_DRAW_MODE_COMMAND_NAME);
		if (getToolItemWithCommand(FREE_FORM_CONN_DRAW_MODE_COMMAND_NAME).isSelected())
			return(FREE_FORM_CONN_DRAW_MODE_COMMAND_NAME);

		return(null);
		}
	public		void		setConnectionDrawMode(String mode)
		{
		getToolItemWithCommand(mode).select(true);
		}
	}

