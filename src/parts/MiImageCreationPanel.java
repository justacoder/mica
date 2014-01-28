
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
public class MiImageCreationPanel extends MiContainer implements MiiActionHandler, MiiShapeAttributesPanel, MiiCommandHandler
	{
	public static	String			STANDARD_IMAGES_AREA_NAME= "Standard Images";
	public static	String			SPECIFY_IMAGES_AREA_NAME= "Specify Image";
	public static	String			STANDARD_IMAGES_STATUS_BAR_MESSAGE= "Drag and drop these images into the editor";

	private static final	String		IMAGE_TO_DRAG_AND_DROP	= "imageToDragAndDrop";
	private static final	String		CHANGE_IMAGE_NAME	= "Change Image";
	private static final	String		BROWSE_FILES_NAME	= "Browse Files";


	private	static 		int		ALLEY_SPACING		= 20;
	private	static 		int		INSET_SPACING		= 20;

	private		boolean			creatingPanel;
	private		boolean			prebuiltImagesInheritCurrentAttributes = true;
	private		boolean			imagesIgonoreBorderLookDefaultFLATAttributes = true;
	private		MiPart			specifyImagesArea;
	private		MiPart			standardImagesArea;
	private		MiPart			sampleArea;
	private		String			panelShape;
	private		MiVisibleContainer	sampleContainer;
	private		MiTreeList		standardImagesTreeList;
	private		MiEditor		editor;
	public		MiAttributes		displayedAttributes;
	private		MiPart			currentPreviewedImage;
	private		MiModelToGraphicsPartConvertor	shapeModelToShapeGraphicsConvertor;
	private		MiSize			standardImageSize = new MiSize(24, 24);
//	private		MiSize			sampleImageSize = new MiSize(48, 48);
	private		MiComboBox		imageFileNameComboBox;
	private		MiFileChooser		fileChooser;




	public				MiImageCreationPanel(MiEditor editor, MiiModelEntity standardImages, String panelShape)
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

		specifyImagesArea = makeSpecifyImagesArea(editor);
		standardImagesArea= makeStandardImagesArea();

		sampleArea= makeSampleArea();

		appendPart(specifyImagesArea);
		appendPart(standardImagesArea);

		appendPart(sampleArea);

		setStandardImages(standardImages);

		creatingPanel = false;
		}
	public		void		setStandardImages(MiiModelEntity standardImages)
		{
		populateStandardImagesArea(standardImagesTreeList, standardImages, null);
		}
	public		void		setEditor(MiEditor editor)
		{
		this.editor = editor;
		((MiDragAndDropBehavior )sampleContainer.getDragAndDropBehavior()).setTargetEditor(editor);
		((MiDragAndDropBehavior )standardImagesTreeList.getDragAndDropBehavior()).setTargetEditor(editor);
		}
	public		void		setDisplayedAttributes(MiAttributes attributes)
		{
		if ((imagesIgonoreBorderLookDefaultFLATAttributes) && (attributes.getBorderLook() == Mi_FLAT_BORDER_LOOK))
			{
			attributes = attributes.setBorderLook(Mi_NONE);
			}
			
		displayedAttributes = attributes;

		if ((prebuiltImagesInheritCurrentAttributes) && (currentPreviewedImage != null))
			{
			currentPreviewedImage.setAttributes(attributes);
			}
		}
	public		MiAttributes	getDisplayedAttributes(MiAttributes attributes)
		{
		return(attributes);
		}

	protected	MiPart		makeSpecifyImagesArea(MiEditor editor)
		{
		MiBox box = new MiBox(SPECIFY_IMAGES_AREA_NAME);
		box.setLayout(new MiColumnLayout());

		MiColumnLayout columnLayout = new MiColumnLayout();
//		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
//		columnLayout.setUniqueElementIndex(0);
		box.setLayout(columnLayout);

		imageFileNameComboBox = new MiComboBox();
		imageFileNameComboBox.appendCommandHandler(this, CHANGE_IMAGE_NAME, Mi_VALUE_CHANGED_ACTION);
		MiPushButton imageFileNameBrowser = new MiPushButton("Browse...");
		imageFileNameBrowser.appendCommandHandler(this, BROWSE_FILES_NAME, Mi_VALUE_CHANGED_ACTION);
		box.appendPart(imageFileNameComboBox);
		box.appendPart(imageFileNameBrowser);

		return(box);
		}


	protected	MiPart		makeStandardImagesArea()
		{
		MiBox box = new MiBox(STANDARD_IMAGES_AREA_NAME);
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
		treeList.setSectionMargins(new MiMargins(0));
		treeList.setContentsMargins(new MiMargins(2));

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

		standardImagesTreeList = treeList;

		box.appendPart(scrolledList);

		box.setStatusHelpMessage(STANDARD_IMAGES_STATUS_BAR_MESSAGE);
		return(box);
		}

	protected	void		populateStandardImagesArea(MiTreeList treeList, MiiModelEntity images, Object parentTreeBranchTag)
		{
		MiTable grid = null;
		for (int i = 0; i < images.getNumberOfModelEntities(); ++i)
			{
			MiiModelEntity item = images.getModelEntity(i);
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

				populateStandardImagesArea(treeList, item, tag);
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
					grid.setSelectable(false);
					grid.getSelectionManager().setBrowsable(true);
					grid.getSelectionManager().setSelectionForegroundColor(MiColorManager.black);
					grid.setPreferredNumberOfVisibleRows(-1);
					//grid.setPreferredNumberOfVisibleRows(1);
					grid.setTotalNumberOfColumns(4);
					grid.setHasFixedTotalNumberOfColumns(true);


					//grid = new MiGridLayout();
					//grid.setNumberOfColumns(4);
					}

				MiPart part = shapeModelToShapeGraphicsConvertor.convert(item);
				MiPart icon = part.deepCopy();
				icon.setSize(standardImageSize); 
				icon.setResource(IMAGE_TO_DRAG_AND_DROP, part);
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
		if (panelShape == MiShapeAttributesPanel.HORIZONTAL_PANEL_SHAPE)
			{
			sampleContainer.setPreferredSize(new MiSize(120,125));
			}
		else
			{
			sampleContainer.setPreferredSize(new MiSize(120,200));
			}
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

			part = (MiPart )part.getResource(IMAGE_TO_DRAG_AND_DROP);
//MiDebug.println("Item Browsed tag = " + part);

			currentPreviewedImage = part;
			setSample(currentPreviewedImage);
			}
/*
		else if (action.hasActionType(Mi_ITEM_SELECTED_ACTION))
			{
			MiTable grid = (MiTable )action.getActionSource();

			MiPart sample = (MiPart )grid.getSelectionManager().getSelectedCell().getGraphics()
						.getResource(IMAGE_TO_DRAG_AND_DROP);

			currentPreviewedImage = sample;
			setSample(currentPreviewedImage);
			}
*/
		else if (action.hasActionType(Mi_LOST_MOUSE_FOCUS_ACTION))
			{
			setSample(currentPreviewedImage);
			}
		else if (action.hasActionType(Mi_DRAG_AND_DROP_PICKUP_ACTION | Mi_ACTIONS_OF_PARTS_OF_OBSERVED))
			{
			MiDataTransferOperation transferOp = 
				(MiDataTransferOperation )action.getActionSystemInfo();
			MiPart treeItemGraphics = (MiPart )transferOp.getSource();
//MiDebug.println("treeItemGraphics = " + treeItemGraphics);
//MiDebug.println("treeItemGraphics.getResource(IMAGE_TO_DRAG_AND_DROP) = " + treeItemGraphics.getResource(IMAGE_TO_DRAG_AND_DROP));

			MiPart shape = ((MiPart )treeItemGraphics.getResource(IMAGE_TO_DRAG_AND_DROP)).deepCopy();
			transferOp.setData(shape.deepCopy());
			MiPart look = new MiReference(shape);
			MiBounds bounds = look.getBounds();
//MiDebug.println("look bounds = " + look.getBounds());
//MiDebug.println("editor = " + editor);
//MiDebug.println("editor.getViewport() = " + editor.getViewport());
//MiDebug.println("getContainingEditor(this) = " + getContainingEditor());
//MiDebug.println("getContainingEditor(this).getViewport() = " + getContainingEditor().getViewport());
			//getContainingEditor().transformLocalWorldToRootWorld(bounds);
			editor.transformLocalWorldToRootWorld(bounds);
//MiDebug.println("xformed bounds = " + bounds);
			look.setSize(bounds.getSize());
//MiDebug.println("look now bounds = " + look.getBounds());
			transferOp.setLook(look);
			}

		return(true);
		}
	protected	void		updateSample()
		{
		setSample(currentPreviewedImage);
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
		sample.setResource(IMAGE_TO_DRAG_AND_DROP, sampleAtOrigSize);
		sample.setIsDragAndDropSource(true);
		sampleContainer.invalidateArea();
		}
	public		void		processCommand(String cmd)
		{
		if (creatingPanel)
			return;

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
				currentPreviewedImage = new MiImage(name);
				Strings contents = imageFileNameComboBox.getContents();
				contents.insertElementAt(name, 0);
				imageFileNameComboBox.setContents(contents);
				}
			}
				
		updateSample();
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		}

	}


