
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
public class MiPanAndZoomCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames
	{
	private		MiBounds	oldWorld		= new MiBounds();
	private		MiBounds	newWorld		= new MiBounds();
	private		double		zoomToFitPercentageMargins = 0.05;
	private		IntVector 	contentLayers;


	public				MiPanAndZoomCommand()
		{
		}

	public				MiPanAndZoomCommand(MiEditor editor, MiBounds oldWorld, MiBounds newWorld, IntVector contentLayers)
		{
		this(editor, oldWorld, newWorld);
		if (contentLayers != null)
			{
			this.contentLayers = new IntVector(contentLayers);
			}
		}

	public				MiPanAndZoomCommand(MiEditor editor, MiBounds oldWorld, MiBounds newWorld)
		{
		setTargetOfCommand(editor);
		this.oldWorld.copy(oldWorld);
		this.newWorld.copy(newWorld);
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		setLayersThatHaveContent(IntVector contentLayers)
		{
		this.contentLayers = contentLayers;
		}
	public		IntVector	getLayersThatHaveContent()
		{
		return(contentLayers);
		}
	public		void		setZoomToFitPercentageOfContentMargins(double value)
		{
		zoomToFitPercentageMargins = value;
		}
	public		double		getZoomToFitPercentageOfContentMargins()
		{
		return(zoomToFitPercentageMargins);
		}

	public		MiBounds	getOldWorld()
		{
		return(new MiBounds(oldWorld));
		}
	public		MiBounds	getNewWorld()
		{
		return(new MiBounds(newWorld));
		}
	public		void		processCommand(String arg)
		{
		if ((arg != null)
			&& ((arg.equalsIgnoreCase(Mi_VIEW_ALL_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_ZOOM_IN_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_ZOOM_OUT_COMMAND_NAME))))
			{
			MiEditor editor = getEditor();
			editor.getWorldBounds(oldWorld);
			newWorld.copy(oldWorld);

			if (arg.equalsIgnoreCase(Mi_ZOOM_IN_COMMAND_NAME))
				{
				if (!MiIZoomAroundMouse.zoom(editor, oldWorld.getCenter(), 
					newWorld, true,
					MiIZoomAroundMouse.DEFAULT_ZOOM_FACTOR, 
					MiIZoomAroundMouse.DEFAULT_MAX_ZOOM_MAGNIFICATION,
					MiIZoomAroundMouse.DEFAULT_CENTER_WORLD_AT_POINT_OF_MOUSE_CLICK))
					{
					return;
					}
				}
			else if (arg.equalsIgnoreCase(Mi_ZOOM_OUT_COMMAND_NAME))
				{
				if (!MiIZoomAroundMouse.zoom(editor, oldWorld.getCenter(), 
					newWorld, false,
					MiIZoomAroundMouse.DEFAULT_ZOOM_FACTOR, 
					MiIZoomAroundMouse.DEFAULT_MAX_ZOOM_MAGNIFICATION,
					MiIZoomAroundMouse.DEFAULT_CENTER_WORLD_AT_POINT_OF_MOUSE_CLICK))
					{
					return;
					}
				}
			else if (arg.equalsIgnoreCase(Mi_VIEW_ALL_COMMAND_NAME))
				{
				MiBounds universe = editor.getUniverseBounds();
				if (universe.equals(oldWorld))
					return;
				newWorld.copy(universe);
				}
			}
		else if (arg.equalsIgnoreCase(Mi_ZOOM_HOME_COMMAND_NAME))
			{
			MiEditor editor = getEditor();
			editor.getWorldBounds(oldWorld);
			newWorld.copy(oldWorld);
			newWorld.setWidth(editor.getDeviceBounds().getWidth());
			newWorld.setHeight(editor.getDeviceBounds().getHeight());
			if (newWorld.equals(oldWorld))
				return;
			}
		else if ((arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_WIDTH_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_HEIGHT_COMMAND_NAME)))
			{
			MiEditor editor = getEditor();
			editor.getWorldBounds(oldWorld);
			if (!editor.hasLayers())
				{
				editor.reCalcBoundsOfContents(newWorld);
				}
			else
				{
				MiBounds tmpBounds = new MiBounds();
				newWorld.reverse();
				for (int i = 0; i < editor.getNumberOfLayers(); ++i)
					{
					MiPart layer = editor.getLayer(i);
					for (int j = 0; j < layer.getNumberOfItems(); ++j)
						{
						MiPart obj = layer.getItem(j);
						if (obj.isVisible())
							{
							obj.getDrawBounds(tmpBounds);
							newWorld.union(tmpBounds);
							}
						}
					}
				}
			newWorld.setWidth(newWorld.getWidth() * (1.0 + zoomToFitPercentageMargins));
			newWorld.setHeight(newWorld.getHeight() * (1.0 + zoomToFitPercentageMargins));

			if (arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_WIDTH_COMMAND_NAME))
				newWorld.setHeight(newWorld.getWidth() * oldWorld.getHeight()/oldWorld.getWidth());
			else if (arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_HEIGHT_COMMAND_NAME))
				newWorld.setWidth(newWorld.getHeight() * oldWorld.getWidth()/oldWorld.getHeight());

			if (newWorld.equals(oldWorld))
				return;
			}
		else if ((arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_CONTENT_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_CONTENT_WIDTH_COMMAND_NAME))
			|| (arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_COMMAND_NAME)))
			{
			MiEditor editor = getEditor();
			editor.getWorldBounds(oldWorld);
			if (!editor.hasLayers())
				{
				editor.reCalcBoundsOfContents(newWorld);
				}
			else
				{
				MiBounds tmpBounds = new MiBounds();
				newWorld.reverse();
				if (contentLayers == null)
					{
					contentLayers = new IntVector();
					// Assume bottom layer is used for grid and page layouts
					for (int i = 1; i < editor.getNumberOfLayers(); ++i)
						{
						contentLayers.addElement(i);
						}
					}
				for (int i = 0; i < contentLayers.size(); ++i)
					{
					MiPart layer = editor.getLayer(contentLayers.elementAt(i));
					for (int j = 0; j < layer.getNumberOfItems(); ++j)
						{
						MiPart obj = layer.getItem(j);
						if (obj.isVisible())
							{
							obj.getDrawBounds(tmpBounds);
							newWorld.union(tmpBounds);
							}
						}
					}
				}
			newWorld.setWidth(newWorld.getWidth() * (1.0 + zoomToFitPercentageMargins));
			newWorld.setHeight(newWorld.getHeight() * (1.0 + zoomToFitPercentageMargins));


			if (arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_CONTENT_WIDTH_COMMAND_NAME))
				newWorld.setHeight(newWorld.getWidth() * oldWorld.getHeight()/oldWorld.getWidth());
			else if (arg.equalsIgnoreCase(Mi_ZOOM_TO_FIT_CONTENT_HEIGHT_COMMAND_NAME))
				newWorld.setWidth(newWorld.getHeight() * oldWorld.getWidth()/oldWorld.getHeight());

			if (newWorld.equals(oldWorld))
				{
				return;
				}
			if ((newWorld.getWidth() < 0.0001)
				|| (newWorld.getHeight() < 0.0001))
				{
				return;
				}
			}
		else if (arg.equalsIgnoreCase(Mi_SCALE_TO_FIT_PAGE_COMMAND_NAME))
			{
			MiEditor editor = getEditor();
			editor.getWorldBounds(oldWorld);
			if (!editor.hasLayers())
				{
				editor.reCalcBoundsOfContents(newWorld);
				}
			else
				{
				MiBounds tmpBounds = new MiBounds();
				newWorld.reverse();
				for (int i = 0; i < editor.getNumberOfLayers(); ++i)
					{
					MiPart layer = editor.getLayer(i);
					if (!layer.isSavable())
						continue;
					for (int j = 0; j < layer.getNumberOfItems(); ++j)
						{
						MiPart obj = layer.getItem(j);
						if (obj.isVisible())
							{
							obj.getBounds(tmpBounds);
							newWorld.union(tmpBounds);
							}
						}
					}
				}

			if (newWorld.equals(oldWorld))
				{
				return;
				}
			if ((newWorld.getWidth() < 0.0001)
				|| (newWorld.getHeight() < 0.0001))
				{
				return;
				}
			}
		else
			{
			throw new IllegalArgumentException(this + ": Unknown argument to processCommand: " + arg);
			}
		processCommand(getEditor(), newWorld);
		}
	public		void		processCommand(MiEditor editor, MiBounds worldToBe)
		{
		MiBounds oldWorld = editor.getWorldBounds();
		doit(editor, worldToBe);
		MiSystem.getViewportTransactionManager().appendTransaction(
			new MiPanAndZoomCommand(editor, oldWorld, worldToBe, contentLayers));
		}
	protected	void		doit(MiEditor editor, MiBounds worldToBe)
		{
		MiBounds world = new MiBounds(worldToBe);
		editor.getViewport().confineProposedWorldToConstraints(world);
		editor.getViewport().enforceWorldAspectRatio(world);
		if (!editor.getWorldBounds().equals(world))
			{
			editor.setWorldBounds(world);
			}
		} 
					/**------------------------------------------------------
					 * Gets the name of this transaction. This name is often 
					 * displayed, for example, in the menubar's edit pulldown
					 * menu.
					 * @return		the name of this transaction.
					 * @implements		MiiTransaction#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		if (oldWorld.equalsSize(newWorld))
			return(Mi_PAN_DISPLAY_NAME);
		return(Mi_ZOOM_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		if (oldWorld.equalsSize(newWorld))
			return(Mi_PAN_COMMAND_NAME);
		return(Mi_ZOOM_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		doit(getEditor(), newWorld);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
		doit(getEditor(), oldWorld);
		} 
					/**------------------------------------------------------
					 * Repeats this transaction. This re-applies the changes 
					 * encapsulated by this transaction. For example, a 
					 * translation of a shape can be repeated in order to move 
					 * it further.
					 * @implements		MiiTransaction#repeat
					 *------------------------------------------------------*/
	public		void		repeat()
		{
		MiScale scale = new MiScale(
			newWorld.getWidth()/oldWorld.getWidth(),
			newWorld.getHeight()/oldWorld.getHeight());
		MiVector translation = new MiVector(
			newWorld.getCenterX() - oldWorld.getCenterX(),
			newWorld.getCenterY() - oldWorld.getCenterY());
		MiEditor editor = getEditor();
		MiBounds newNewWorld = editor.getWorldBounds();
		newNewWorld.setWidth(newNewWorld.getWidth() * scale.x);
		newNewWorld.setHeight(newNewWorld.getHeight() * scale.y);
		newNewWorld.translate(translation);
		MiPanAndZoomCommand cmd = new MiPanAndZoomCommand(editor, editor.getWorldBounds(), newNewWorld, contentLayers);
		cmd.processCommand(null);
		} 
					/**------------------------------------------------------
					 * Gets whether this transaction is undoable.
					 * @returns		true if undoable.
					 * @implements		MiiTransaction#isUndoable
					 *------------------------------------------------------*/
	public		boolean		isUndoable()
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Gets whether this transaction is repeatable. If repeatable
					 * then calling this transaction's repeat() method is permitted.
					 * @returns		true if repeatable.
					 * @implements		MiiTransaction#isRepeatable
					 *------------------------------------------------------*/
	public		boolean		isRepeatable()
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		return(null);
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		return(null);
		}
	}

