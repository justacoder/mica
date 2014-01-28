
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
import java.awt.Image;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiShapeCreationPanel extends MiContainer implements MiiActionHandler, MiiShapeAttributesPanel
	{
	public static	String			PREBUILT_SHAPES_AREA_NAME= "Standard Shapes";
	public static	String			BASIC_SHAPES_AREA_NAME= "Basic Shapes";
	public static	String			BASIC_SHAPES_STATUS_BAR_MESSAGE= "Drag and drop these shapes, or select a shape and draw it, into the editor";
	public static	String			PREBUILT_SHAPES_STATUS_BAR_MESSAGE= "Drag and drop these shapes into the editor";

	private static	String			SHAPE_TO_DRAG_AND_DROP= "shapeToDragAndDrop";


	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		boolean			creatingPanel;
	private		boolean			prebuiltShapesInheritCurrentAttributes = true;
	private		MiPart			basicShapesArea;
	private		MiPart			prebuiltShapesArea;
	private		MiPart			sampleArea;
	private		String			panelShape;
	private		MiVisibleContainer	sampleContainer;
	private		MiTreeList		prebuiltShapesTreeList;
	private		MiEditor		editor;
	private		MiDrawingShapeToolBar	drawingShapeToolBar;
	public		MiAttributes		displayedAttributes;
	private		MiPart			currentPreviewedPrebuiltShape;
	private		MiModelToGraphicsPartConvertor	shapeModelToShapeGraphicsConvertor;
	private		MiSize			standardShapeSize = new MiSize(24, 24);



	public				MiShapeCreationPanel(MiDrawingToolBarManager drawingToolBarManager, MiEditor editor, MiiCommandManager commandManager, MiiModelEntity standardShapes, String panelShape)
		{
		this(drawingToolBarManager, editor, commandManager, standardShapes, null, panelShape);
		}

	public				MiShapeCreationPanel(MiDrawingToolBarManager drawingToolBarManager, MiEditor editor, MiiCommandManager commandManager, MiiModelEntity standardShapes, MiScale shapesScaleFactor, String panelShape)
		{
		this.panelShape = panelShape;
		this.editor = editor;

		creatingPanel = true;

		MiRowLayout row0 = new MiRowLayout();
		row0.setAlleyHSpacing(ALLEY_SPACING);
		row0.setInsetMargins(INSET_SPACING);
		if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			row0.setInsetMargins(0);
			}
		setLayout(row0);

		shapeModelToShapeGraphicsConvertor = new MiModelToGraphicsPartConvertor();

		basicShapesArea	= makeBasicShapesArea(drawingToolBarManager, editor, commandManager);
		prebuiltShapesArea= makePrebuiltShapesArea();
		sampleArea= makeSampleArea();

		appendPart(basicShapesArea);
		appendPart(prebuiltShapesArea);

		appendPart(sampleArea);

		setShapes(standardShapes, shapesScaleFactor);

		creatingPanel = false;
		}
	public		void		setShapes(MiiModelEntity standardShapes, MiScale shapesScaleFactor)
		{
		populatePrebuiltShapesArea(prebuiltShapesTreeList, standardShapes, shapesScaleFactor, null);
		}
	public		void		setEditor(MiEditor editor)
		{
		this.editor = editor;
		drawingShapeToolBar.setEditor(editor);
		drawingShapeToolBar.getDragAndDropBehavior().setValidTargets(new MiParts(editor));
		((MiDragAndDropBehavior )sampleContainer.getDragAndDropBehavior()).setTargetEditor(editor);
		((MiDragAndDropBehavior )prebuiltShapesTreeList.getDragAndDropBehavior()).setTargetEditor(editor);
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		displayedAttributes = attributes;

		if ((prebuiltShapesInheritCurrentAttributes) && (currentPreviewedPrebuiltShape != null))
			{
			currentPreviewedPrebuiltShape.setAttributes(attributes);
			}
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes attributes)
		{
		return(gatherAttributes(attributes));
		}
	public		MiAttributes	gatherAttributes(MiAttributes attributes)
		{
		return(attributes);
		}

	public		MiDrawingShapeToolBar	getDrawingShapeToolBar()
		{
		return(drawingShapeToolBar);
		}

	protected	MiPart		makeBasicShapesArea(MiDrawingToolBarManager drawingToolBarManager, MiEditor editor, MiiCommandManager commandManager)
		{
		MiBox box = new MiBox(BASIC_SHAPES_AREA_NAME);
		box.setLayout(new MiColumnLayout());

		drawingShapeToolBar = new MiDrawingShapeToolBar(editor, commandManager, false);
		drawingShapeToolBar.setToolBarManager(drawingToolBarManager);

/*
		MiPart pencilButton = drawingShapeToolBar.getDrawingShapeButton(
			MiDrawingShapeToolBar.PENCIL_DRAW_MODE_COMMAND_NAME);
		pencilButton.removeSelf();
		MiPart rubberButton = drawingShapeToolBar.getDrawingShapeButton(
			MiDrawingShapeToolBar.RUBBER_STAMP_MODE_COMMAND_NAME);
		rubberButton.removeSelf();
*/

		drawingToolBarManager.registerToolBar(drawingShapeToolBar);
		drawingShapeToolBar.getDragAndDropBehavior().setValidTargets(new MiParts(editor));

		// Do this now so that it does not reset layout to the default column layout later...
		drawingShapeToolBar.setOrientation(Mi_VERTICAL);

		// Remove spacer after arrow button
		drawingShapeToolBar.removePart(1);

		MiGridLayout gridLayout = new MiGridLayout();
		gridLayout.setNumberOfColumns(2);
		drawingShapeToolBar.setLayout(gridLayout);

		box.appendPart(drawingShapeToolBar);

		box.setStatusHelpMessage(BASIC_SHAPES_STATUS_BAR_MESSAGE);
		return(box);
		}


	protected	MiPart		makePrebuiltShapesArea()
		{
		MiBox box = new MiBox(PREBUILT_SHAPES_AREA_NAME);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		box.setLayout(layout);

		MiTreeList treeList = new MiTreeList();
		treeList.getSortManager().setEnabled(false);
		treeList.getContentsBackground().setBackgroundColor(MiColorManager.white);
		//treeList.getContentsBackground().setBorderLook(Mi_INLINED_INDENTED_BORDER_LOOK);
		treeList.getTableHeaderAndFooterManager().setResizableRows(false);
		treeList.getTableHeaderAndFooterManager().setResizableColumns(false);
		//treeList.appendActionHandler(this, Mi_ITEM_BROWSED_ACTION);
		//treeList.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		//treeList.appendActionHandler(this, Mi_DRAG_AND_DROP_PICKUP_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED);
		treeList.setPreferredNumberOfVisibleRows(1);
		treeList.getSelectionManager().setBrowsable(false);
		treeList.setSelectable(false);
		//treeList.getSelectionManager().setSelectionForegroundColor(MiColorManager.black);

		MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));
		// Resize palette part to be same size as it will be when/if dropped in editor
		dndBehavior.setTargetEditor(editor);
		treeList.setDragAndDropBehavior(dndBehavior);

		MiScrolledBox scrolledList = new MiScrolledBox(treeList);

		prebuiltShapesTreeList = treeList;

		box.appendPart(scrolledList);

		box.setStatusHelpMessage(PREBUILT_SHAPES_STATUS_BAR_MESSAGE);
		return(box);
		}

	protected	void		populatePrebuiltShapesArea(MiTreeList treeList, MiiModelEntity standardShapes, MiScale shapesScaleFactor, Object parentTreeBranchTag)
		{
		MiTable grid = null;
		for (int i = 0; i < standardShapes.getNumberOfModelEntities(); ++i)
			{
			MiiModelEntity item = standardShapes.getModelEntity(i);
			//if (PREBUILT_CATREGORY_TYPE.equals(item.getType().getName()))
			if (item.getPropertyNames().size() == 0)
				{
				if (grid != null)
					{
					treeList.addItem("", grid, grid, parentTreeBranchTag, false);
					grid = null;
					}
				String label = item.getType().getName();
				String tag = item.getType().getName();

				treeList.addItem(label, null, tag, parentTreeBranchTag, true);

				populatePrebuiltShapesArea(treeList, item, shapesScaleFactor, tag);
				}
			else
				{
				if (grid == null)
					{
					grid = new MiTable();
					grid.setName("grid:" + parentTreeBranchTag);
					grid.getSortManager().setEnabled(false);
					grid.getTableWideDefaults().setInsetMargins(new MiMargins(2));
					grid.setSectionMargins(new MiMargins(0));
					grid.setContentsMargins(new MiMargins(2));
					grid.setBackgroundColor(MiColorManager.white);
					grid.getContentsBackground().setBorderLook(Mi_NONE);
					grid.getContentsBackground().setBackgroundColor(MiColorManager.white);
					grid.getTableHeaderAndFooterManager().setResizableRows(false);
					grid.getTableHeaderAndFooterManager().setResizableColumns(false);
					grid.appendActionHandler(this, Mi_ITEM_BROWSED_ACTION);
					//grid.appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
					grid.appendActionHandler(this, Mi_DRAG_AND_DROP_PICKUP_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED);
					grid.getSelectionManager().setBrowsable(true);
					grid.setSelectable(false);
					grid.getSelectionManager().setSelectionForegroundColor(MiColorManager.black);
					grid.setPreferredNumberOfVisibleRows(-1);
					//grid.setPreferredNumberOfVisibleRows(1);
					grid.setTotalNumberOfColumns(4);
					grid.setHasFixedTotalNumberOfColumns(true);

//MiDebug.println("grid = " + grid);

					//grid = new MiGridLayout();
					//grid.setNumberOfColumns(4);
					}

				MiPart part = shapeModelToShapeGraphicsConvertor.convert(item);
				MiPart icon = part.deepCopy();
				icon.setSize(standardShapeSize); 
				if ((shapesScaleFactor != null) && (!shapesScaleFactor.isIdentity()))
					{
					part.scale(shapesScaleFactor);
					}
				icon.setResource(SHAPE_TO_DRAG_AND_DROP, part);
				icon.setIsDragAndDropSource(true);
				grid.appendItem(icon);

				//treeList.addItem(item.getName(), icon, part, parentTreeBranchTag, false);
				}
			}
		if (grid != null)
			{
			treeList.addItem("", grid, grid, parentTreeBranchTag, false);
			}
			
		}
	protected	MiPart		makeSampleArea()
		{
		MiBox box = new MiBox("Sample");
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		box.setLayout(columnLayout);

		sampleContainer = new MiVisibleContainer();
//		container.setPreferredSize(new MiSize(50,40));
//		sampleContainer.setPreferredSize(new MiSize(120,160));
		sampleContainer.setBorderLook(Mi_INLINED_INDENTED_BORDER_LOOK);
		sampleContainer.setBackgroundColor(MiColorManager.white);
		sampleContainer.setVisibleContainerAutomaticLayoutEnabled(false);
		sampleContainer.setInvalidLayoutNotificationsEnabled(false);

/*
		MiPolyConstraint polyLayout = new MiPolyConstraint();
		polyLayout.appendConstraint(new MiRelativeLocationConstraint(
				sample, MiRelativeLocationConstraint.CENTER_OF, box));
		sample.setLayout(polyLayout);
		container.appendAttachment(sampleLine);
*/

		MiDragAndDropBehavior dndBehavior = new MiDragAndDropBehavior();
		dndBehavior.setDragAndCopyPickUpEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyDragEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0));
		dndBehavior.setDragAndCopyCancelEvent(
			new MiEvent(MiEvent.Mi_KEY_EVENT, MiEvent.Mi_ESC_KEY, 0));
		dndBehavior.setDragAndCopyDropEvent(
			new MiEvent(MiEvent.Mi_LEFT_MOUSE_UP_EVENT, 0, 0));
		// Resize palette part to be same size as it will be when/if dropped in editor
		dndBehavior.setTargetEditor(editor);
		sampleContainer.setDragAndDropBehavior(dndBehavior);

		sampleContainer.appendActionHandler(this, Mi_DRAG_AND_DROP_PICKUP_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED);

		box.appendPart(sampleContainer);
		return(box);
		}
					/**------------------------------------------------------
	 				 * Process the given action.
					 * @param action	the action to process.
					 * @return 		true if can propogate the action to
					 *			other handlers.
					 *------------------------------------------------------*/
	public		boolean		processAction(MiiAction action)
		{
//MiDebug.println(this + " processing action: " + action);
		if (action.hasActionType(Mi_ITEM_BROWSED_ACTION))
			{
			MiTable grid = (MiTable )action.getActionSource();

			MiPart part = grid.getCell(
				grid.getSelectionManager().getBrowsedItemRow(),
				grid.getSelectionManager().getBrowsedItemColumn()).getGraphics();

//MiDebug.println("Item Browsed tag = " + part);
			part = (MiPart )part.getResource(SHAPE_TO_DRAG_AND_DROP);

			currentPreviewedPrebuiltShape = part;
			setSample(currentPreviewedPrebuiltShape);
			}
/*
		else if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			MiTable grid = (MiTable )action.getActionSource();

			MiPart sample = grid.getSelectionManager().getSelectedCell().getGraphics();

			currentPreviewedPrebuiltShape = sample;
			setSample(currentPreviewedPrebuiltShape);
			}
*/
		else if (action.hasActionType(Mi_LOST_MOUSE_FOCUS_ACTION))
			{
			setSample(currentPreviewedPrebuiltShape);
			}
		else if (action.hasActionType(Mi_DRAG_AND_DROP_PICKUP_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			{
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();
			MiPart treeItemGraphics = (MiPart )transferOp.getSource();
//MiDebug.println("treeItemGraphics = " + treeItemGraphics);
//MiDebug.println("treeItemGraphics.getResource(SHAPE_TO_DRAG_AND_DROP) = " + treeItemGraphics.getResource(SHAPE_TO_DRAG_AND_DROP));

			MiPart shape = ((MiPart )treeItemGraphics.getResource(SHAPE_TO_DRAG_AND_DROP)).deepCopy();
			transferOp.setData(shape.deepCopy());
			MiPart look = new MiReference(shape);
			MiBounds bounds = look.getBounds();
			editor.transformLocalWorldToRootWorld(bounds);
			look.setSize(bounds.getSize());
			transferOp.setLook(look);
			}

		return(true);
		}
	protected	void		setSample(MiPart sample)
		{
//MiDebug.println("Set sample = " + sample);
		MiPart sampleAtOrigSize = sample;
		sample = sample.deepCopy();
		MiScale scale = sample.getSize(new MiSize()).fitPreservingAspectRatio(sampleContainer.getSize(new MiSize()).subtractMargins(new MiMargins(5)));
		sample.scale(scale);
/*
		if (sample.getBounds().isLargerSizeThan(sampleContainer.getBounds()))
			{
			sample.setBounds(sampleContainer.getBounds().subtractMargins(new MiMargins(10)));
			}
*/
		sampleContainer.removeAllAttachments();
		sample.setCenter(sampleContainer.getCenter());
		//sampleContainer.appendPart(sample);
		sampleContainer.appendAttachment(sample, Mi_CENTER_LOCATION, "", null);
		sample.setResource(SHAPE_TO_DRAG_AND_DROP, sampleAtOrigSize);
		sampleContainer.invalidateArea();
		}


/*
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
*/
	}


