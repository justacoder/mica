
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
import com.swfm.mica.util.IntVector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
/*

To do: end selectState in middle of editing session by:
	Esc key anywhere (deSelectEverything event from SelectionManager woudl be best)
*/


public class MiLayoutManipulator extends MiManipulator implements MiiLayoutManipulator, MiiActionHandler
	{
	protected static final int	NOTHING_SELECTED	= 0;
	protected static final int	ALL_SELECTED		= 1;
	protected static final int	LAYOUT_SELECTED		= 2;
	protected static final int	ALL_CELLS_SELECTED	= 3;
	protected static final int	ALL_PH_SELECTED		= 4;
	protected static final int	SOME_CELLS_SELECTED	= 5;
	protected static final int	SOME_PH_SELECTED	= 6;
	protected static final int	ONE_NODE_PH_PAIR_SELECTED=7;

	protected	int		selectionState			= NOTHING_SELECTED;
	protected static boolean 	staticDrillDownSelectionMode	= true;
	protected	boolean 	drillDownSelectionMode;
	protected	boolean 	removeManipulatorWhenTargetDeSelected	= true;
	protected	boolean 	deletingLayoutDeletesTarget		= true;
	protected	boolean 	replaceDeletedCellsWithPlaceHolder	= true;
	protected	MiPart		target;
	protected	MiiManipulatableLayout	layout;
	private		MiAttributes	selectedAttributes;

					// Render support
	private		IntVector 	selectedPlaces		= new IntVector();
	private		MiPoint 	startCell		= new MiPoint();
	private		MiPoint 	endCell			= new MiPoint();
	private		MiBounds 	tmpBounds		= new MiBounds();
	private		int[] 		startCellIndex		= new int[1];
	private		int[] 		endCellIndex		= new int[1];
	private		int[]		placeHolderIndex	= new int[1];
	private		int[]		cellIndex		= new int[1];





	public				MiLayoutManipulator(MiPart target)
		{
		this((MiiManipulatableLayout )target.getLayout());
		}
	public				MiLayoutManipulator(MiiManipulatableLayout layout)
		{
		setCopyable(false);
		setCopyableAsPartOfCopyable(false);
		this.layout = layout;
		drillDownSelectionMode = staticDrillDownSelectionMode;
		target = layout.getTarget();
		selectedAttributes = (MiAttributes )getAttributes().getModifiedAttributes(Mi_COLOR, MiColorManager.red);
		appendEventHandler(new MiLayoutManipulatorEventHandler(this));
		setAcceptingKeyboardFocus(true);
		setAcceptingEnterKeyFocus(true);
		target.appendActionHandler(this, Mi_DESELECTED_ACTION);
		target.appendActionHandler(this, Mi_GEOMETRY_CHANGE_ACTION);
		}

	public		MiiManipulator	create(MiPart target)
		{
		return(new MiLayoutManipulator(target));
		}
					// Useful for graph layout editors but not in GUI builders
	public		void		setDeletingLayoutDeletesTarget(boolean flag)
		{
		deletingLayoutDeletesTarget = flag;
		}
	public		boolean		getDeletingLayoutDeletesTarget()
		{
		return(deletingLayoutDeletesTarget);
		}

	public		void		setReplaceDeletedCellsWithPlaceHolder(boolean flag)
		{
		replaceDeletedCellsWithPlaceHolder = flag;
		}
	public		boolean		getReplaceDeletedCellsWithPlaceHolder()
		{
		return(replaceDeletedCellsWithPlaceHolder);
		}
	public		boolean		processAction(MiiAction action)
		{
		if ((action.getActionSource() == target.getContainingEditor()) 
			&& (action.hasActionType(Mi_ITEM_SELECTED_ACTION)))
			{
			// The user clicked on something else...
			if ((action.getActionSystemInfo() != target)
				// .. and that something is not a part of the target...
				&& (!target.isContainerOf((MiPart )action.getActionSystemInfo())))
				{
 				// ...reset state...
				deSelectEverything();
				removeFromTarget();
				}
			return(true);
			}
		if ((action.getActionSource() == target) && (action.hasActionType(Mi_DESELECTED_ACTION)))
			{
			if (removeManipulatorWhenTargetDeSelected)
				{
				removeFromTarget();
				target.removeActionHandlers(this);
				}
			}
		else
			{
			refreshBounds();
			}

		return(true);
		}
	public		void		setKeyboardFocus(boolean flag)
		{
		if (!flag)
			{
			if (target.getContainingEditor() != null)
				{
				deSelectEverything();
				// can't call removeFromTarget or will go infinite
				if (target.hasAttachment(this))
					{
					target.removeAttachment(this);
					}
				target.getContainingEditor().removeActionHandlers(this);
				}
			}
		super.setKeyboardFocus(flag);
		}

	public		void		attachToTarget()
		{
		target.appendAttachment(this);
		target.getContainingEditor().appendActionHandler(this, Mi_ITEM_SELECTED_ACTION);
		target.getContainingEditor().select(target);
		if (target.isSelected())
			selectionState = ALL_SELECTED;
		else
			deSelectEverything();
		requestKeyboardFocus();
		}
	public		void		removeFromTarget()
		{
		if (hasKeyboardFocus())
			{
			getContainingWindow().requestKeyboardFocus(null);
			}
		if (target.hasAttachment(this))
			{
			target.removeAttachment(this);
			}
		// If target hasn't been deleted...
		if (target.getContainingEditor() != null)
			{
			target.getContainingEditor().removeActionHandlers(this);
			}
		}
	// --------------------------------
	// MiiManipulator implementation 
	// --------------------------------
	public		void		attachToTarget(String tag)
		{
		// If this is already attached then we are done
		if (target.hasAttachment(this))
			return;

		target.appendAttachment(this, Mi_NONE, tag, null);
		target.getContainingEditor().select(target);
		if (target.isSelected())
			selectionState = ALL_SELECTED;
		else
			deSelectEverything();
		requestKeyboardFocus();
		}

	protected	void		calcMinimumSize(MiSize size)
		{
		target.calcMinimumSize(size);
		}
	protected	void		calcPreferredSize(MiSize size)
		{
		target.calcPreferredSize(size);
		}
	protected	void		reCalcBounds(MiBounds bounds)
		{
		//target.getBounds(b);
		MiSize maxSize = new MiSize();
		MiBounds cellBounds = tmpBounds;
		int numCells = layout.getNumberOfNodes();
		for (int i = 0; i < numCells; ++i)
			{
			layout.getNodeBounds(i, cellBounds);
			maxSize.union(cellBounds);
			}
		for (int i = 0; i < numCells; ++i)
			{
			layout.getNodeBounds(i, cellBounds);
			cellBounds.setWidth(maxSize.width);
			cellBounds.setHeight(maxSize.height);
			cellBounds.expandIntoASquare();
			bounds.union(cellBounds);
			}
		}
	public		void		setBounds(MiBounds bounds)
		{
		MiSize minSize = new MiSize();
		target.getMinimumSize(minSize);
		if (bounds.isLargerSizeThan(minSize))
			{
			minSize.setWidth(bounds.getWidth());
			minSize.setHeight(bounds.getHeight());
			target.setPreferredSize(minSize);
			super.setBounds(bounds);
			}
		else
			{
			target.setPreferredSize(minSize);
			super.setSize(minSize);
			}
		}

	// ------------------------------------------------------------
	//	Selection Management
	// ------------------------------------------------------------
	public static	void		setSelectionModeIsDrillDown(boolean flag)
		{
		staticDrillDownSelectionMode = flag;
		}
	public static	boolean		getSelectionModeIsDrillDown()
		{
		return(staticDrillDownSelectionMode);
		}
	public		boolean		select(MiBounds area)
		{
		if (drillDownSelectionMode)
			{
			if (selectionState == NOTHING_SELECTED)
				{
				selectionState = ALL_SELECTED;
				}
			else if (selectionState == ALL_SELECTED)
				{
				removeManipulatorWhenTargetDeSelected = false;
				target.getContainingEditor().deSelect(target);
				removeManipulatorWhenTargetDeSelected = true;

				// If on top of node...
				int index = getCellAtLocation(area);
				if (index != -1)
					{
					selectCells();
					}
				else if ((index = getPlaceHolderAtLocation(area)) != -1)
					{
					selectPlaceHolders();
					selectionState = ALL_PH_SELECTED;
					}
				else
					{
					selectLayout();
					selectionState = LAYOUT_SELECTED;
					}
				}
			else if ((selectionState == ALL_CELLS_SELECTED)
				|| (selectionState == ALL_PH_SELECTED))
				{
				int index = getCellAtLocation(area);
				if (index != -1)
					{
					deSelectEverything();
					selectCell(index);
					selectionState = SOME_CELLS_SELECTED;
					}
				else if ((index = getPlaceHolderAtLocation(area)) != -1)
					{
					deSelectEverything();
					selectPlace(index);
					selectionState = SOME_PH_SELECTED;
					}
				else
					{
					deSelectEverything();
					removeFromTarget();
					selectionState = NOTHING_SELECTED;
					}
				}
			else if (selectionState == LAYOUT_SELECTED)
				{
				int index = getPlaceHolderAtLocation(area);
				if (index != -1)
					{
					deSelectEverything();
					selectPlace(index);
					selectionState = SOME_PH_SELECTED;
					}
				else
					{
					deSelectEverything();
					removeFromTarget();
					selectionState = NOTHING_SELECTED;
					}
				}
			else if ((selectionState == SOME_CELLS_SELECTED)
				|| (selectionState == SOME_PH_SELECTED))
				{
				int index = getCellAtLocation(area);
				if (index != -1)
					{
					if (!isSelectedCell(index))
						{
						if (!isSelectedPlace(index))
							{
							selectCell(index);
							}
						else
							{
							deSelectPlace(index);
							selectionState = SOME_PH_SELECTED;
							}
						}
					else
						{
						deSelectCell(index);
						selectPlace(index);
						selectionState = SOME_PH_SELECTED; // Maybe SOME_CELLS_SELECTED too
						}
					}
				else if ((index = getPlaceHolderAtLocation(area)) != -1)
					{
					if (!isSelectedPlace(index))
						{
						selectPlace(index);
						}
					else
						{
						deSelectPlace(index);
						selectionState = SOME_PH_SELECTED; // Maybe
						}
					}
				else
					{
					deSelectEverything();
					removeFromTarget();
					selectionState = NOTHING_SELECTED;
					}
				}
			}
		else
			{
			// -----------
			// Drill Up
			// -----------
			if (selectionState == NOTHING_SELECTED)
				{
				int index = getCellAtLocation(area);
				if (index != -1)
					{
					selectCell(index);
					selectionState = SOME_CELLS_SELECTED;
					}
				else if ((index = getPlaceHolderAtLocation(area)) != -1)
					{
					selectPlace(index);
					selectionState = SOME_PH_SELECTED;
					}
				else
					{
					selectLayout();
					selectionState = LAYOUT_SELECTED;
					}
				}
			else if ((selectionState == SOME_CELLS_SELECTED)
				|| (selectionState == SOME_PH_SELECTED))
				{
				int index = getCellAtLocation(area);
				if (index != -1)
					{
					if (isSelectedCell(index))
						{
						selectCells();
						}
					else
						{
						selectCell(index);
						}
					}
				else if ((index = getPlaceHolderAtLocation(area)) != -1)
					{
					if (!isSelectedPlace(index))
						{
						selectPlace(index);
						}
					else
						{
						selectLayout();
						selectionState = LAYOUT_SELECTED;
						}
					}
				else
					{
					selectLayout();
					selectionState = LAYOUT_SELECTED;
					}
				}
			else if ((selectionState == ALL_CELLS_SELECTED)
				|| (selectionState == LAYOUT_SELECTED))
				{
				deSelectEverything();
				target.getContainingEditor().select(target);
				selectionState = ALL_SELECTED;
				}
			else if (selectionState == ALL_SELECTED)
				{
				deSelectEverything();
				target.getContainingEditor().deSelect(target);
				removeFromTarget();
				selectionState = NOTHING_SELECTED;
				}
			}
		return(true);
		}



	public		void		selectLayout()
		{
		deSelectEverything();
		selectionState = LAYOUT_SELECTED;
		}
	public		void		selectCells()
		{
		deSelectEverything();
		selectionState = ALL_CELLS_SELECTED;
		int numCells = layout.getNumberOfNodes();
		MiEditor editor = target.getContainingEditor();
		int numSelected = 0;
		for (int i = 0; i < numCells; ++i)
			{
			MiPart part = layout.getNode(i);
			if (!(part instanceof MiPlaceHolder))
				{
				editor.selectAdditional(part);
				++numSelected;
				}
			}
		if (numSelected < 2)
			{
			selectionState = SOME_CELLS_SELECTED;
			}
		}
	public		void		selectPlaceHolders()
		{
		deSelectEverything();
		selectionState = ALL_PH_SELECTED;
		int numCells = layout.getNumberOfNodes();
		MiEditor editor = target.getContainingEditor();
		int numSelected = 0;
		for (int i = 0; i < numCells; ++i)
			{
			MiPart part = layout.getNode(i);
			if ((part instanceof MiPlaceHolder) && (part.isVisible()))
				{
				editor.selectAdditional(part);
				selectedPlaces.addElement(i);
				++numSelected;
				}
			}
		if (numSelected < 2)
			{
			selectionState = SOME_PH_SELECTED;
			}
		}
	public		void		selectAll()
		{
		selectedPlaces.removeAllElements();
		selectionState = ALL_SELECTED;
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			{
			target.getPart(i).select(true);
			}
		invalidateArea();
		}
	public		boolean		deSelectAll()
		{
		if (target.hasAttachment(this))
			{
			deSelectEverything();
			removeFromTarget();
			// Return false so that all other selected items will be deselected too.
			return(false);
			}
		return(false);
		}
	public		boolean		deSelectEverything()
		{
		if ((selectedPlaces.size() > 0) || (selectionState != NOTHING_SELECTED))
			{
			while (selectedPlaces.size() > 0)
				deSelectPlace(selectedPlaces.elementAt(0));
			selectionState = NOTHING_SELECTED;
			MiEditor editor = target.getContainingEditor();
			if (editor != null)
				{
				for (int i = 0; i < target.getNumberOfParts(); ++i)
					{
					editor.deSelect(target.getPart(i));
					}
				}
			invalidateArea();
			return(true);
			}
		return(false);
		}
	public		void		selectPlace(int index)
		{
		selectedPlaces.addElement(index);
		MiPart cellContents = layout.getNode(index);
		if (cellContents instanceof MiPlaceHolder)
			target.getContainingEditor().selectAdditional(cellContents);
		invalidateArea();
		}
	public		void		deSelectPlace(int index)
		{
		selectedPlaces.removeElement(index);
		MiPart cellContents = layout.getNode(index);
		if (cellContents instanceof MiPlaceHolder)
			target.getContainingEditor().deSelect(cellContents);
		invalidateArea();
		}
	public		void		selectCell(int index)
		{
		MiEditor editor = target.getContainingEditor();
		editor.select(layout.getNode(index));
		invalidateArea();
		}
	public		void		deSelectCell(int index)
		{
		MiEditor editor = target.getContainingEditor();
		editor.deSelect(layout.getNode(index));
		invalidateArea();
		}
	public		boolean		isSelectedCell(int index)
		{
		return((selectionState == ALL_CELLS_SELECTED) 
			|| (selectionState == ALL_SELECTED) 
			|| (layout.getNode(index).isSelected()));
		}
	public		boolean		isSelectedPlace(int index)
		{
		return(selectedPlaces.contains(index));
		}

	// ------------------------------------------------------------
	//	Cell pick Management
	// ------------------------------------------------------------
	public		int		getCellAtLocation(MiBounds area)
		{
		int numCells = layout.getNumberOfNodes();
		MiBounds bounds = tmpBounds;
		for (int i = 0; i < numCells; ++i)
			{
			layout.getNodeBounds(i, bounds);
			if (bounds.intersects(area))
				{
				MiPart part = layout.getNode(i);
				if (!(part instanceof MiPlaceHolder))
					return(i);
				}
			}
		return(-1);
		}

	public		int		getPlaceHolderAtLocation(MiBounds area)
		{
		int numCells = layout.getNumberOfNodes();
		MiBounds bounds = tmpBounds;
		for (int i = 0; i < numCells; ++i)
			{
			layout.getNodeBounds(i, bounds);
			if (bounds.intersects(area))
				{
				MiPart part = layout.getNode(i);
				if (part instanceof MiPlaceHolder)
					return(i);
				}
			}
		return(-1);
		}

	// ------------------------------------------------------------
	//	MiiLayoutManipulator implementation
	// ------------------------------------------------------------
	protected	int		getPrimarySelectedItem(
						int[] selectedPlaceHolder, int[] selectedCell)
		{
		selectedCell[0] = -1;
		if (selectedPlaces.size() > 0)
			{
			selectedPlaceHolder[0] = selectedPlaces.lastElement();
			return(selectedPlaces.lastElement());
			}
		selectedPlaceHolder[0] = -1;
		if (selectionState == SOME_CELLS_SELECTED)
			{
			int numCells = layout.getNumberOfNodes();
			for (int i = 0; i < numCells; ++i)
				{
				//if (!(layout.getNode(i) instanceof MiPlaceHolder))
					{
					if (layout.getNode(i).isSelected())
						{
						selectedCell[0] = i;
						return(i);
						}
					}
				}
			}
		return(-1);
		}
	protected	void		changePrimarySelectedItem(
						int[] placeHolderIndex, int[] cellIndex, int index)
		{
		if (placeHolderIndex[0] != -1)
			{
			deSelectPlace(placeHolderIndex[0]);
			selectPlace(index);
			}
		if (cellIndex[0] != -1)
			{
			deSelectCell(cellIndex[0]);
			selectCell(index);
			}
		}
	public		boolean		selectNeighboringNode(int location)
		{
		int selectedIndex = getPrimarySelectedItem(placeHolderIndex, cellIndex);
		if (selectedIndex != -1)
			{
			int index = layout.getNeighboringNode(selectedIndex, location);
			if (index != -1)
				{
				changePrimarySelectedItem(placeHolderIndex, cellIndex, index);
				}
			return(true);
			}
		return(false);
		}
	public		boolean		insertNeighboringPlaceHolder(int relativeLocation)
		{
		int index = -1;
		// Insert before last selected cell...
		if (selectedPlaces.size() > 0)
			{
			index = selectedPlaces.lastElement();
			}
		adjustSelectedCellsArray(layout.insertNeighboringNode(
			layout.makePlaceHolder(), index, relativeLocation));
		invalidateLayout();
		return(true);
		}
	public		boolean		appendPlaceHolderRight()
		{
		int index = 0;
		// Insert after last selected cell...
		if (selectedPlaces.size() > 0)
			{
			index = selectedPlaces.lastElement();
			}
		layout.appendNeighboringNode(
			layout.makePlaceHolder(), index, Mi_RIGHT);
		invalidateLayout();
		return(true);
		}
	public		boolean		appendPlaceHolderBelow()
		{
		int index = 0;
		// Insert after last selected cell...
		if (selectedPlaces.size() > 0)
			{
			index = selectedPlaces.lastElement();
			}
		layout.insertNeighboringNode(
			layout.makePlaceHolder(), index, Mi_BELOW);
		invalidateLayout();
		return(true);
		}
	public		boolean		deleteItemBefore()
		{
		int selectedIndex = getPrimarySelectedItem(placeHolderIndex, cellIndex);
		if (selectedIndex != -1)
			{
			int index = layout.getNeighboringNode(selectedIndex, Mi_TO_LEFT);
			if (index == -1)
				index = layout.getNeighboringNode(selectedIndex, Mi_ABOVE);
			if (index != -1)
				{
				if (placeHolderIndex[0] != -1)
					deletePlaceHolder(index);
				else
					deleteNode(index);
				return(true);
				}
			}
		return(false);
		}
	public		boolean		deleteItem()
		{
		if (selectedPlaces.size() > 0)
			{
			for (int i = 0; i < selectedPlaces.size(); ++i)
				{
				if (deletePlaceHolder(selectedPlaces.elementAt(i)))
					--i;
				}
			selectedPlaces.removeAllElements();
			invalidateLayout();
			}
		else if ((selectionState == ALL_CELLS_SELECTED)
			|| (selectionState == SOME_CELLS_SELECTED))
			{
			int numCells = layout.getNumberOfNodes();
			for (int i = 0; i < numCells; ++i)
				{
				if (!(layout.getNode(i) instanceof MiPlaceHolder))
					{
					MiPart part = layout.getNode(i);
					if (part.isSelected())
						{
						deleteNode(i);
						--i;
						numCells = layout.getNumberOfNodes();
						}
					}
				}
			}
		else if (selectionState == LAYOUT_SELECTED)
			{
			if (target != layout)
				{
				((MiContainer )target).setLayout(null);
				for (int i = target.getNumberOfParts() - 1; i >= 0; --i)
					{
					MiPart part = target.getPart(i);
					if (!part.isDeletable())
						continue;
					if (part instanceof MiPlaceHolder)
						part.deleteSelf();
					else if (part instanceof MiPlaceHolderConnection)
						part.deleteSelf();
					else if ((deletingLayoutDeletesTarget)
						&& (!(target instanceof MiEditor)))
						{
						// Removal of placeHolders and nodes may remove
						// connections which means i may be > numberOfParts
						if (part != null)
							{
							part.removeFromAllContainers();
							target.getContainingEditor().appendPart(part);
							}
						}
					}
				// Delete target if no parts are left in it...
				if ((!(target instanceof MiEditor)) && (target.getNumberOfParts() == 0))
					{
					removeFromTarget();
					target.deleteSelf();
					}
				}
			else
				{
				MiEditor editor = target.getContainingEditor();
				while (target.getNumberOfParts() > 0)
					{
					MiPart part = target.getPart(0);
					target.removePart(part);
					editor.appendPart(part);
					}
				editor.removePart(target);
				}
			}
		else if (selectionState == ALL_SELECTED)
			{
			// Handled by caller...
			return(false);
			}
		else
			{
			return(false);
			}
		deSelectEverything();
		removeFromTarget();
		selectionState = NOTHING_SELECTED;
		return(true);
		}
	public		boolean		cycleOrientation()
		{
		layout.cycleOrientation();
		return(true);
		}
		
	protected	void		deleteNode(int index)
		{
		MiPart part = layout.getNode(index);
		if (!part.isDeletable())
			return;
		if (replaceDeletedCellsWithPlaceHolder)
			part.replaceSelf(layout.getPrototypePlaceHolder().copy());
		part.deleteSelf();
		}
					// Return whether an item was removed from selectedPlaces
	protected	boolean		deletePlaceHolder(int index)
		{
		MiPart part = layout.getNode(index);
		if (!part.isDeletable())
			return(false);
		// if empty ...
		if (part instanceof MiPlaceHolder)
			{
			layout.deleteNode(index);
			for (int i = 0; i < selectedPlaces.size(); ++i)
				{
				if (selectedPlaces.elementAt(i) == index)
					{
					selectedPlaces.removeElementAt(i);
					adjustSelectedCellsArray(index, -1);
					return(true);
					}
				}
			adjustSelectedCellsArray(index, -1);
			return(false);
			}
		if (replaceDeletedCellsWithPlaceHolder)
			layout.removeNode(index);
		else
			part.removeFromAllContainers();
		if (!(target instanceof MiEditor))
			{
			target.getContainingEditor().appendPart(part);
			}
		return(false);
		}
					
	private		void		adjustSelectedCellsArray(MiParts createdParts)
		{
		MiParts cells = layout.getNodes(new MiParts());
		for (int i = 0; i < createdParts.size(); ++i)
			{
			adjustSelectedCellsArray(cells.indexOf(createdParts.elementAt(i)), 1);
			}
		}
					// If inserted or deleted something...
	private		void		adjustSelectedCellsArray(int index, int adjustment)
		{
		for (int i = 0; i < selectedPlaces.size(); ++i)
			{
			if (selectedPlaces.elementAt(i) >= index)
				selectedPlaces.setElementAt(selectedPlaces.elementAt(i) + adjustment, i);
			}
		}
	// ------------------------------------------------------------
	//	Render Management
	// ------------------------------------------------------------
	public		void		render(MiRenderer renderer)
		{
		//super.render(renderer);

		if ((selectionState != LAYOUT_SELECTED)
			&& (selectionState != ALL_PH_SELECTED)
			&& (selectionState != SOME_PH_SELECTED))
			{
			return;
			}

		int numCells = layout.getNumberOfNodes();
		MiBounds bounds = MiBounds.newBounds();
		MiSize maxSize = MiSize.newSize();
		MiDistance maxWidth;
		renderer.setAttributes(selectedAttributes);
		for (int i = 0; i < numCells; ++i)
			{
			layout.getNodeBounds(i, bounds);
			maxSize.union(bounds);
			}
		for (int i = 0; i < numCells; ++i)
			{
			layout.getNodeBounds(i, bounds);
			bounds.setWidth(maxSize.width);
			bounds.setHeight(maxSize.height);
			bounds.expandIntoASquare();
			if ((selectionState == LAYOUT_SELECTED) || isSelectedPlace(i))
				{
				renderer.drawCircle(bounds);
				}
			}
		if (selectionState == LAYOUT_SELECTED)
			{
			int numConns = layout.getNumberOfEdges();
			for (int i = 0; i < numConns; ++i)
				{
				MiConnection conn = layout.getEdge(i);
				MiPart lines = conn.getGraphics();
				for (int pt = 0; pt < lines.getNumberOfPoints() - 1; ++pt)
					{
					renderer.drawLine(lines.getPoint(pt), lines.getPoint(pt + 1));
					}
				}
			}

		MiBounds.freeBounds(bounds);
		MiSize.freeSize(maxSize);
		}
// ------- might need this if need to select text fields in a layout node, manipu will
// have to detect select and forward it to node...????
	public		void		init()
		{
		replaceTargetWithManipulator();
		}
	public		void		termin()
		{
		replaceManipulatorWithTarget();
		}

	public 		void		replaceTargetWithManipulator()
		{
		MiPart container = target.getContainer(0);
		container.setPart(this, container.getIndexOfPart(target));
		appendPart(target);
		}
	public 		void		replaceManipulatorWithTarget()
		{
		MiPart container = getContainer(0);
		container.setPart(target, container.getIndexOfPart(this));
		removePart(target);
		}

	// --------------------------------
	// MiiDraggable implementation 
	// --------------------------------
	public		int 		pickup(MiEvent event)
		{
		return(Mi_PROPOGATE_EVENT);
		}
	public		int 		drag(MiEvent event)
		{
		return(Mi_PROPOGATE_EVENT);
		}
	public		int	 	drop(MiEvent event)
		{
		return(Mi_PROPOGATE_EVENT);
		}
	}

class MiLayoutManipulatorEventHandler extends MiEventHandler implements MiiTypes
	{
	public static final String	AppendPHRightEventName		= "AppendPlaceHolderToRight";
	public static final String	InsertPHLeftEventName		= "InsertPlaceHolderToLeft";
	public static final String	InsertPHRightEventName		= "InsertPlaceHolderToRight";
	public static final String	AppendPHBelowEventName		= "AppendPlaceHolderBelow";
	public static final String	InsertPHAboveEventName		= "InsertPlaceHolderAbove";
	public static final String	InsertPHBelowEventName		= "InsertPlaceHolderBelow";
	public static final String	DeleteEventName			= "DeletePlaceHolder";
	public static final String	DeleteBeforeEventName		= "DeletePlaceHolderBefore";

	public static final String	DeSelectAndPropogateEventName	
								= "DeSelectAndPropogateToTargetOfManipulator";

	public static final String	SelectHorizontalNextEventName	= "SelectHorizontalNext";
	public static final String	SelectHorizontalPrevEventName 	= "SelectHorizontalPrevious";
	public static final String	SelectVerticalNextEventName	= "SelectVerticalNext";
	public static final String	SelectVerticalPrevEventName	= "SelectVerticalPrevious";
	public static final String	SelectHorizontalHomeEventName	= "SelectHorizontalHome";
	public static final String	SelectHorizontalEndEventName	= "SelectHorizontalEnd";
	public static final String	SelectVerticalHomeEventName	= "SelectVerticalHome";
	public static final String	SelectVerticalEndEventName	= "SelectVerticalEnd";

	public static final String	CycleOrientationEventName	= "CycleOrientation";

	private		MiLayoutManipulator 	manipulator;


	public				MiLayoutManipulatorEventHandler(MiLayoutManipulator manipulator)
		{
		this.manipulator = manipulator;
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, 
						Mi_LEFT_MOUSE_CLICK_EVENT, 
						0, 
						Mi_CONTROL_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, 
						Mi_LEFT_MOUSE_CLICK_EVENT, 
						0, 
						Mi_SHIFT_KEY_HELD_DOWN);
		addEventToCommandTranslation(Mi_DESELECT_COMMAND_NAME, Mi_KEY_EVENT, Mi_ESC_KEY, 0);
		addEventToCommandTranslation(DeSelectAndPropogateEventName, Mi_LEFT_MOUSE_DBLCLICK_EVENT, 0, 0);

		addEventToCommandTranslation(CycleOrientationEventName, 
						Mi_KEY_EVENT, 
						'+', 
						Mi_CONTROL_KEY_HELD_DOWN);

		// + key as translated by AWT when ctrl is held down
		addEventToCommandTranslation(CycleOrientationEventName, 
						Mi_KEY_EVENT, 
						139, 
						Mi_CONTROL_KEY_HELD_DOWN);

		// + key at top of keyboard
		addEventToCommandTranslation(CycleOrientationEventName, 
						Mi_KEY_EVENT, 
						139, 
						Mi_SHIFT_KEY_HELD_DOWN + Mi_CONTROL_KEY_HELD_DOWN);

		addEventToCommandTranslation(InsertPHLeftEventName, Mi_KEY_EVENT, Mi_SPACE_KEY, 0);

		addEventToCommandTranslation(InsertPHLeftEventName, Mi_KEY_EVENT, 'i', 0);
		addEventToCommandTranslation(InsertPHAboveEventName, 
						Mi_KEY_EVENT, 
						'i', 
						Mi_CONTROL_KEY_HELD_DOWN);

		addEventToCommandTranslation(AppendPHRightEventName, Mi_KEY_EVENT, Mi_ENTER_KEY, 0);
		addEventToCommandTranslation(AppendPHBelowEventName, 
						Mi_KEY_EVENT, 
						Mi_ENTER_KEY, 
						Mi_CONTROL_KEY_HELD_DOWN);
		// Same as ctrl-enter
		addEventToCommandTranslation(AppendPHBelowEventName, 
						Mi_KEY_EVENT, 
						'm', 
						Mi_CONTROL_KEY_HELD_DOWN);


		addEventToCommandTranslation(InsertPHAboveEventName, Mi_KEY_EVENT, Mi_INSERT_KEY, 0);

		addEventToCommandTranslation(DeleteBeforeEventName, Mi_KEY_EVENT, Mi_BACKSPACE_KEY, 0);
		addEventToCommandTranslation(DeleteEventName, Mi_KEY_EVENT, Mi_DELETE_KEY, 0);

		addEventToCommandTranslation(SelectHorizontalNextEventName, 
						Mi_KEY_EVENT, Mi_RIGHT_ARROW_KEY, 0);
		addEventToCommandTranslation(SelectHorizontalPrevEventName, 
						Mi_KEY_EVENT, Mi_LEFT_ARROW_KEY, 0);
		addEventToCommandTranslation(SelectVerticalNextEventName, 
						Mi_KEY_EVENT, Mi_DOWN_ARROW_KEY, 0);
		addEventToCommandTranslation(SelectVerticalPrevEventName, 
						Mi_KEY_EVENT, Mi_UP_ARROW_KEY, 0);

		addEventToCommandTranslation(SelectHorizontalHomeEventName, 
						Mi_KEY_EVENT, Mi_HOME_KEY, 0);
		addEventToCommandTranslation(SelectHorizontalEndEventName, 
						Mi_KEY_EVENT, Mi_END_KEY, 0);
		addEventToCommandTranslation(SelectVerticalHomeEventName, 
						Mi_KEY_EVENT, Mi_PAGE_UP_KEY, 0);
		addEventToCommandTranslation(SelectVerticalEndEventName, 
						Mi_KEY_EVENT, Mi_PAGE_DOWN_KEY, 0);
		}

	public		int		processCommand()
		{
		boolean status = false;

		if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			status = manipulator.select(event.worldMouseFootPrint);
			}
		if (isCommand(Mi_DESELECT_COMMAND_NAME))
			{
			status = manipulator.deSelectAll();
			}
		else if (isCommand(DeSelectAndPropogateEventName))
			{
			status = manipulator.deSelectAll();
			// FIX: Really would like to change the targetPath but dispatch in
			// MiWindow would not look at this until after processing the current
			// target path.
			int retVal = event.getTargetList().elementAt(1).dispatchEvent(event);
			return(retVal);
			}
		else if (isCommand(InsertPHRightEventName))
			{
			status = manipulator.insertNeighboringPlaceHolder(Mi_RIGHT);
			}
		else if (isCommand(InsertPHBelowEventName))
			{
			status = manipulator.insertNeighboringPlaceHolder(Mi_BELOW);
			}
		else if (isCommand(InsertPHLeftEventName))
			{
			status = manipulator.insertNeighboringPlaceHolder(Mi_LEFT);
			}
		else if (isCommand(InsertPHAboveEventName))
			{
			status = manipulator.insertNeighboringPlaceHolder(Mi_ABOVE);
			}
		else if (isCommand(AppendPHRightEventName))
			{
			status = manipulator.appendPlaceHolderRight();
			}
		else if (isCommand(AppendPHBelowEventName))
			{
			status = manipulator.appendPlaceHolderBelow();
			}
		else if (isCommand(DeleteBeforeEventName))
			{
			status = manipulator.deleteItemBefore();
			}
		else if (isCommand(DeleteEventName))
			{
			status = manipulator.deleteItem();
			}
		else if (isCommand(SelectHorizontalNextEventName))
			{
			status = manipulator.selectNeighboringNode(Mi_TO_RIGHT);
			}
		else if (isCommand(SelectHorizontalPrevEventName))
			{
			status = manipulator.selectNeighboringNode(Mi_TO_LEFT);
			}
		else if (isCommand(SelectVerticalNextEventName))
			{
			status = manipulator.selectNeighboringNode(Mi_BELOW);
			}
		else if (isCommand(SelectVerticalPrevEventName))
			{
			status = manipulator.selectNeighboringNode(Mi_ABOVE);
			}
		else if (isCommand(SelectHorizontalHomeEventName))
			{
			status = manipulator.selectNeighboringNode(Mi_FAR_LEFT);
			}
		else if (isCommand(SelectHorizontalEndEventName))
			{
			status = manipulator.selectNeighboringNode(Mi_FAR_RIGHT);
			}
		else if (isCommand(SelectVerticalHomeEventName))
			{
			status = manipulator.selectNeighboringNode(Mi_TOP);
			}
		else if (isCommand(SelectVerticalEndEventName))
			{
			status = manipulator.selectNeighboringNode(Mi_BOTTOM);
			}
		else if (isCommand(CycleOrientationEventName))
			{
			status = manipulator.cycleOrientation();
			}
		if (!status)
			{
			return(Mi_PROPOGATE_EVENT);
			}
		return(Mi_CONSUME_EVENT);
		}
	}

