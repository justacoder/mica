
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

import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.IntVector;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;

/**----------------------------------------------------------------------------------------------
 * This class is responsible for the collection, transformation,
 * and display of the MiParts it contains. 
 * <p>
 * <em>Layers</em>
 * <p>
 * A MiEditor can have 'layers' (see setHasLayers(boolean)). Layers
 * are like transparencies used for overhead projectors. When an
 * editor does not have layers the all of it's MiParts are just
 * added to the editor itself. When an editor does have layers, 
 * then it's parts are added to one of any number of layers. Layers
 * are ordinary MiContainers.
 * <p>
 * If a layer is the 'current layer', then the parts that are added,
 * removed and inquired using the XXXItem() methods (appendItem, 
 * insertItem, removeItem, getItem, ...) are done so to this current
 * layer.
 * <p>
 * If a layer is the 'active layer', then all events received by the
 * editor go only to the parts in the active layer, and none go to
 * any other parts in the editor.
 * <p>
 * <em>Selection Manager</em>
 * <p>
 * Every MiEditor has a MiiSelectionManager. The default implementation
 * of MiiSelectionManager, the MiSelectionManager class, permits you to
 * specify the minimum and maximum number of selected items and how 
 * selected parts should be highlighted.
 * <p>
 * <em>Viewport</em>
 * <p>
 * Every MiEditor has a MiViewport. This viewport transforms the 
 * editor's parts, defined in world coordinate space, to device 
 * coordinate space * which is what the output device (usually the 
 * computer screen) wants.
 * <p>
 * <em>Viewport Layout</em>
 * <p>
 * Every MiEditor can have a MiViewportSizeLayout. This is a special
 * layout for editors that want to implement a dependancy of some sort
 * (usually size) between the world, device and universe coordinate 
 * spaces.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditor extends MiContainer implements MiiScrollableData, MiiActionTypes
	{
	private		MiViewport		viewport;
	private		MiiSelectionManager	selectionManager	= new MiSelectionManager(this);
	private		MiSnapManager		snapManager;
	private		MiPageManager		pageManager;
	private		MiEditorFilter		filter;
	private		IntVector		mouseAppearanceStack	= new IntVector();
	private		Strings			mouseAppearanceIndentifierStack = new Strings();
	private		String			mouseAppearanceOverrider;
	private		int			mouseAppearanceOverridden;
	private		boolean			iterateIntoPartsOfParts;
	private		boolean			hasLayers;
	private		boolean			drawing;
	private		boolean			editorValidLayout;
	private		boolean			bitbltScrollingEnabled = true;
	private		boolean			autopanningCanExpandUniverse = true;
	private		MiiEditorViewportSizeLayout	viewportSizeLayout;
	private		FastVector		tmpFastVector 		= new FastVector();
	private		MiPoint			tmpPoint 		= new MiPoint();
	private		MiDevicePoint		tmpDPoint 		= new MiDevicePoint();
	private		MiDeviceVector		tmpDVector 		= new MiDeviceVector();
	private		MiVector		tmpVector 		= new MiVector();
	private		MiBounds		tmpBounds 		= new MiBounds();
	private		MiBounds		tmpBounds2 		= new MiBounds();
	private		MiPart			currentLayer;
	private		MiPart			currentConnectionLayer;
	private		MiPart			activeLayer;



					/**------------------------------------------------------
	 				 * Constructs a new MiEditor with a size of 400x400 device
					 * coordinates. The editor's layout is set to 
					 * MiSizeOnlyLayout.
					 *------------------------------------------------------*/
	public 				MiEditor() 
		{
		this(new MiBounds(0, 0, 400, 400));
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiEditor with the given device bounds.
					 * The editor's layout is set to MiSizeOnlyLayout.
					 * @param deviceBounds	the size of the new editor in 
					 *			device space.
					 *------------------------------------------------------*/
	public 				MiEditor(MiBounds deviceBounds) 
		{
		viewport = new MiViewport(deviceBounds, deviceBounds);
		replaceBounds(deviceBounds);
		setInvalidLayoutNotificationsEnabled(false);
		setIsOpaqueRectangle(true);
		setLayout(new MiSizeOnlyLayout());
		}
					/**------------------------------------------------------
	 				 * Gets whether this is a root window. It is a root window
					 * when this MiEditor writes directly to a java.awt.Canvas
					 * (and therefore this editor's device coordinate space is 
					 * actually in pixels).
					 * @return 		true is a root window
					 *------------------------------------------------------*/
	public		boolean		isRootWindow()
		{
		return(false);
		}
					/**------------------------------------------------------
	 				 * Gets the animation manager for this MiEditor (and for all
					 * other MiParts in the root window). There is an animation
					 * manager for each java.awt.Canvas.
					 * @return 		the animation manager
					 * @see 		MiWindow#getAnimationManager
					 * @see 		MiCanvas#getAnimationManager
					 *------------------------------------------------------*/
	public		MiAnimationManager	getAnimationManager()
		{
		return(getRootWindow().getAnimationManager());
		}
					/**------------------------------------------------------
	 				 * Update this MiEditor's state after it's device bounds have
					 * been changed.
					 * @param deviceBounds 	the new device bounds
					 * @action		Mi_EDITOR_DEVICE_RESIZED_ACTION
					 * @action		Mi_EDITOR_VIEWPORT_CHANGED_ACTION
					 *------------------------------------------------------*/
	protected	void		deviceWasResized(MiBounds deviceBounds)
		{
		int policy = viewport.getResizePolicy();
		if (viewportSizeLayout == null)
			viewport.setResizePolicy(MiViewport.SCALE_WORLD_PROPORTIONALLY_WITH_DEVICE);
		else
			viewport.setResizePolicy(Mi_NONE);

//MiDebug.println(this + "MiEditor.deviceWasResized: " + deviceBounds);

		viewport.setDeviceBounds(deviceBounds);
		viewport.setResizePolicy(policy);
		replaceBounds(deviceBounds);

		invalidateLayout();
		dispatchAction(Mi_EDITOR_DEVICE_RESIZED_ACTION);
		dispatchAction(Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		}

	//***************************************************************************************
	// Mouse handling routines
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the mouse 'foot print' area in the world coordinates
					 * of this MiEditor.
					 * @return 		the mouse cursor foot print
					 *------------------------------------------------------*/
	public		MiBounds	getMousePosition()
		{
		return(getMousePosition(new MiBounds()));
		}
	public		MiBounds	getMousePosition(MiBounds b)
		{
		MiEvent.getLastEvent().getDeviceMouseFootPrint(b);
		getRootWindow().getViewport().getTransform().dtow(b, b);
		transformRootWorldToLocalWorld(b);
		return(b);
		}
					/**------------------------------------------------------
	 				 * Sets the mouse position in the world coordinates of
					 * this MiEditor. This is NOT AVAILABLE in awt 1.02.
					 * @param pt 		the mouse cursor position
					 *------------------------------------------------------*/
	public		void		setMousePosition(MiPoint pt)
		{
		// Not available under AWT
		}
					/**------------------------------------------------------
	 				 * Sets the mouse appearance. Available appearances are
					 * listed in MiiTypes. This method does nothing if there
					 * are appearances on the mouse appearance stack.
					 * @param appearance	the mouse cursor appearance
					 * @see			#pushMouseAppearance
					 *------------------------------------------------------*/
	public		void		setMouseAppearance(int appearance)
		{
		if ((mouseAppearanceStack.size() == 0) && (mouseAppearanceOverrider == null))
			{
			getRootWindow().getCanvas().setMouseAppearance(appearance);
			}
/***
if (mouseAppearanceStack.size() > 0)
{
MiDebug.println("Cleaning up - other callers still have mouse appearances still on stack: " 
	+ mouseAppearanceIndentifierStack);

mouseAppearanceIndentifierStack.removeAllElements();
mouseAppearanceStack.removeAllElements();
}
****/
		}
					/**------------------------------------------------------
	 				 * Sets the mouse appearance. Available appearances are
					 * listed in MiiTypes. This method is used in conjunction
					 * with #popMouseAppearance so that the caller may push and
					 * pop, for example, the appearance to/from Mi_WAIT_CURSOR
					 * in multiple methods called from master method, with the
					 * master method actually controlling the appearance and
					 * thus the appearance does not visibly change during each
					 * call to one of these smaller methods.
					 * 
					 * @param appearance	the mouse cursor appearance
					 * @param identifier	identifies the current position in
					 *			the mouse appearance stack
					 * @see			#popMouseAppearance
					 *------------------------------------------------------*/
	public synchronized void	pushMouseAppearance(int appearance, String identifier)
		{
		mouseAppearanceStack.addElement(getMouseAppearance());
		mouseAppearanceIndentifierStack.addElement(identifier);
		if (mouseAppearanceOverrider == null)
			{
			getRootWindow().getCanvas().setMouseAppearance(appearance);
			}
		else
			{
			mouseAppearanceOverridden = appearance;
			}
		}
	public synchronized void	overrideMouseAppearance(int appearance, String identifier)
		{
		getRootWindow().getCanvas().setMouseAppearance(appearance);
		mouseAppearanceOverrider = identifier;
		mouseAppearanceOverridden = getRootWindow().getCanvas().getMouseAppearance();
		}
	public synchronized void	endOverrideMouseAppearance(String identifier)
		{
		if (mouseAppearanceOverrider != null)
			{
			getRootWindow().getCanvas().setMouseAppearance(mouseAppearanceOverridden);
			}
		mouseAppearanceOverrider = null;
		}

					/**------------------------------------------------------
	 				 * Restores the mouse appearance to the appearance of the
					 * mouse before the corresponding call to #pushMouseAppearance
					 * which used this identifier.
					 * @param identifier	identifies a position in the mouse 
					 *			appearance stack
					 * @see			#pushMouseAppearance
					 *------------------------------------------------------*/
	public synchronized void	popMouseAppearance(String identifier)
		{
		int index = mouseAppearanceIndentifierStack.lastIndexOf(identifier);
		if (index != -1)
			{
			if (mouseAppearanceOverrider == null)
				{
				getRootWindow().getCanvas().setMouseAppearance(
					mouseAppearanceStack.elementAt(index));
				}
			else
				{
				mouseAppearanceOverridden = mouseAppearanceStack.elementAt(index);
				}
			mouseAppearanceIndentifierStack.removeElementAt(index);
			mouseAppearanceStack.removeElementAt(index);

/* Cleans up stack automatically if something throws exception but when multiple threads
are modifying this (i.e. modal dialogs) then the sequences are not in order and causes 
appearances to be removed uneccessarily
			while (mouseAppearanceIndentifierStack.size() > index)
				{
				mouseAppearanceIndentifierStack.removeLastElement();
				mouseAppearanceStack.removeLastElement();
				}
*/
			}
		else
			{
			MiDebug.println(this + ": Mouse appearance identifier: \"" 
				+ identifier + "\" not found on stack.");
			mouseAppearanceIndentifierStack.removeAllElements();
			mouseAppearanceStack.removeAllElements();
			}
		}
					/**------------------------------------------------------
	 				 * Gets the mouse appearance. Available appearances are
					 * listed in MiiTypes.
					 * @return 		the mouse cursor appearance
					 *------------------------------------------------------*/
	public		int		getMouseAppearance()
		{
		return(getRootWindow().getCanvas().getMouseAppearance());
		}
					/**------------------------------------------------------
	 				 * Restore the mouse appearance to the appearance assigned
					 * to this MiEditor. 
					 * @see 		MiPart#setContextCursor
					 *------------------------------------------------------*/
	public		void		restoreNormalMouseAppearance()
		{
		if (mouseAppearanceOverrider == null)
			{
			getRootWindow().getCanvas().setMouseAppearance(getContextCursor(null));
			}
		}

	//***************************************************************************************
	// Coordinate transformation support
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Get a renderer to use to draw using the coordinates and
					 * transforms compatible with this MiEditor.
					 * @return 		A renderer
					 *------------------------------------------------------*/
	public		MiRenderer	getRenderer()
		{
		MiWindow root = getRootWindow();
		MiRenderer renderer = root.getCanvas().getRenderer();
		renderer = renderer.copy();
		if (this != root)
			{
			FastVector transforms = new FastVector();
			MiUtility.getTransformsAlongPath(root, this, transforms);
			for (int i = transforms.size() - 1; i >= 0; --i)
				{
				renderer.pushTransform((MiiTransform )transforms.elementAt(i));
				}
			renderer.setClipBounds(getWorldBounds());
			}
		return(renderer);
		}
	public		MiTransforms	getTransformFromHereToRoot()
		{
		// Get combined transforms...
		FastVector transforms = tmpFastVector;
		transforms.removeAllElements();
		MiTransforms transform = new MiTransforms();
		MiWindow root = getRootWindow();
		MiUtility.getTransformsAlongPath(root, this, transforms);
		for (int i = transforms.size() - 1; i >= 0; --i)
			{
			transform.pushTransform((MiiTransform )transforms.elementAt(i));
			}
		return(transform);
		}

					/**------------------------------------------------------
	 				 * Transform the given 'here' bounds to the coordinate space
					 * of the given 'other' editor and return the transformed
					 * bounds in the 'there' bounds.
					 * @param other		The editor whose world space we are
					 *			transforming to
					 * @param here		the bounds in this MiEditor's world
					 *			space
					 * @param there		the (returned) bounds in the 'other'
					 *			MiEditor's world space
					 *------------------------------------------------------*/
	public		void		transformToOtherEditorSpace(
						MiEditor other, MiBounds here, MiBounds there)
		{
		there.copy(here);
		transformLocalWorldToRootWorld(there);
		other.transformRootWorldToLocalWorld(there);
		}
					/**------------------------------------------------------
	 				 * Transform the given bounds in the root window's world
					 * coordinate space to bounds in this MiEditor's world
					 * space.
					 * @param area 		the given and (returned) bounds
					 *------------------------------------------------------*/
	public		void		transformRootWorldToLocalWorld(MiBounds area)
		{
		if (isRootWindow())
			return;
		
		FastVector transforms = new FastVector();
		MiUtility.getTransformsAlongPath(null, this, transforms);
		for (int i = transforms.size() - 2; i >= 0; --i)
			{
			((MiiTransform )transforms.elementAt(i)).dtow(area, area);
			}
		}
					/**------------------------------------------------------
	 				 * Transform the given bounds in this MiEditor's world
					 * coordinate space to bounds in the root window's world
					 * space.
					 * @param area 		the given and (returned) bounds
					 *------------------------------------------------------*/
	public		void		transformLocalWorldToRootWorld(MiBounds area)
		{
		if (isRootWindow())
			return;
		MiPart c = this;
		getViewport().getTransform().wtod(area, area);
		while ((c.getNumberOfContainers() > 0) && (c = c.getContainer(0)) != null)
			{
			if (c instanceof MiEditor) 
				{
				if (!((MiEditor )c).isRootWindow())
					c.getTransform().wtod(area, area);
				}
			else if (c.getTransform() != null)
				{
				c.getTransform().wtod(area, area);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Transform the given 'here' bounds to the coordinate space
					 * of the given 'child' MiPart and return the transformed
					 * bounds in the 'there' bounds.
					 * @param child		The part whose world space we are
					 *			transforming to
					 * @param here		the bounds in this MiEditor's world
					 *			space
					 * @param there		the (returned) bounds in the 'child'
					 *			MiPart's world space
					 *------------------------------------------------------*/
	public		void		transformToChildSpace(MiPart child, MiBounds here, MiBounds there)
		{
		FastVector transforms = new FastVector();
		MiUtility.getTransformsAlongPath(this, child, transforms);
		there.copy(here);
		for (int i = transforms.size() - 2; i >= 0; --i)
			{
			((MiiTransform )transforms.elementAt(i)).dtow(there, there);
			}
		}
					/**------------------------------------------------------
	 				 * Transform the given 'there' bounds in the coordinate space
					 * of the given 'child' MiPart and return the transformed
					 * bounds in the 'here' bounds in the MiEditor's coordinate
					 * space.
					 * @param child		The part whose world space we are
					 *			transforming to
					 * @param there		the bounds in the 'child' MiPart's
					 *			world space
					 * @param here		the (returned) bounds in this 
					 *			MiEditor's world space
					 *------------------------------------------------------*/
	public		void		transformFromChildSpace(MiPart child, MiBounds there, MiBounds here)
		{
		FastVector transforms = new FastVector();
		MiUtility.getTransformsAlongPath(this, child, transforms);
		here.copy(there);
		for (int i = 0; i < transforms.size() - 1; ++i)
			{
			((MiiTransform )transforms.elementAt(i)).wtod(here, here);
			}
		}


	//***************************************************************************************
	// Bounds handling routines
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the inner bounds of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
					 * This implementation returns the world bounds of this
					 * MiEditor.
	 				 * @param b		the (returned) inner bounds
	 				 * @return 		the inner bounds
	 				 * @overrides 		MiPart#getInnerBounds
					 *------------------------------------------------------*/
	public		MiBounds	getInnerBounds(MiBounds b)
		{
		viewport.getWorldBounds(b);
		return(b);
		}

	//***************************************************************************************
	// Pick management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the transform, assigned to this MiEditor. This
					 * method gets this transform from it's MiViewport.
	 				 * @return 	 	the transform
	 				 * @overrides 	 	MiPart#getTransform
					 *------------------------------------------------------*/
	public		MiiTransform	getTransform()
		{
		return(viewport.getTransform());
		}

	//***************************************************************************************
	// Draw handling routines
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Renders this MiEditor (i.e. all of it's parts after
					 * clearing the area to be rendered to the editor's
					 * background color and then applying the MiViewport's 
					 * transforms).
	 				 * @param renderer 	the renderer to use for drawing
					 * @overrides		MiContainer#render
					 *------------------------------------------------------*/
	public		void		render(MiRenderer renderer)
		{
		if ((getFilter() != null) && (getFilter().getViewFilter() != null))
			renderer.pushFilter(getFilter().getViewFilter());

		renderer.pushTransform(viewport.getTransform());
		if (getBackgroundColor() != null)
			{
			// ----------------------------------------------------------
			// Clear background of (clip bounds of) MiEditor.
			// ----------------------------------------------------------
			renderer.setAttributes(getAttributes());
			if (!getUniverseBounds(tmpBounds).isReversed())
				renderer.drawFillRect(tmpBounds);
			else
				renderer.drawFillRect(getWorldBounds(tmpBounds));
			}
		super.render(renderer);
		renderer.popTransform();

		if ((getFilter() != null) && (getFilter().getViewFilter() != null))
			renderer.popFilter();
		}

					/**------------------------------------------------------
					 * True by default. If true will alter setWorldBounds
					 * bounds slightly to keep changes as integral pixel
					 * amounts.
	 				 * @param flag 	 	true if enabled
	 				 * @see	 	 	#isBitbltScrollingEnabled
					 *------------------------------------------------------*/
	public		void		setBitbltScrollingEnabled(boolean flag)
		{
		bitbltScrollingEnabled = flag;
		}
					/**------------------------------------------------------
					 * True by default. If true will alter setWorldBounds
					 * bounds slightly to keep changes as integral pixel
					 * amounts.
	 				 * @return 	 	true if enabled
	 				 * @see	 	 	#setBitbltScrollingEnabled
					 *------------------------------------------------------*/
	public		boolean		isBitbltScrollingEnabled()
		{
		return(bitbltScrollingEnabled);
		}

	//***************************************************************************************
	// Event handling routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Dispatches the given event to all event handlers assigned
					 * to this MiEditor. This method does nothing but forward
					 * the event to the MiPart super class after telling the
					 * event what editor it is in.
					 * @param  event	The event
					 * @return 		Mi_CONSUME_EVENT if no other event
					 *			handlers should see this event
					 *			Mi_PROPOGATE_EVENT if other event
					 *			handlers can also see this event
					 * @see			MiPart#dispatchEvent
					 *------------------------------------------------------*/
	public		int	 	dispatchEvent(MiEvent event)
		{
		event.editor = this;
		return(super.dispatchEvent(event));
		}
					/**------------------------------------------------------
					 * Prepends the given event handler to the list of 'grab'
					 * event handlers. This is a special list of event handlers
					 * that are executed immediately after all event monitors
					 * and before any other event handlers in the containing
					 * root window. This is used when an event handler needs 
					 * complete control over the event handling for a short 
					 * period of time.
					 * The MiEvents sent to the event handler will be transformed
					 * to the coordinate space of the eh.getObject(). If 
					 * eh.getObject() is null then it is set to be equal to this
					 * MiEditor.
					 * 
					 * @param eh		the event handler to prepend
					 * @see			#removeGrabEventHandler
					 *------------------------------------------------------*/
	public		void		prependGrabEventHandler(MiiEventHandler eh)
		{
		// ----------------------------------------------------------
		// Store in the event handler which part's coordinate space it
		// want events transformed to.
		// ----------------------------------------------------------
		// Eventhandlers may run in different editors...if (eh.getObject() == null)
			{
			eh.setObject(this);
			}
		getRootWindow().prependGrabEventHandler(eh);
		}
					/**------------------------------------------------------
					 * Removes the given event handler to the list of 'grab'
					 * event handlers. 
					 * @param eh		the event handler to remove
					 * @see			#prependGrabEventHandler
					 *------------------------------------------------------*/
	public		void		removeGrabEventHandler(MiiEventHandler eh)
		{
		// ----------------------------------------------------------
		// Remove the grab from the root window where it is stored.
		// ----------------------------------------------------------
		getRootWindow().removeGrabEventHandler(eh);
		}

	//***************************************************************************************
	// Layer manipulation routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the number of MiParts in the current layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as getNumberOfParts().
					 * @return 		the number of parts in the current
					 *			layer.
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#getNumberOfItems
					 *------------------------------------------------------*/
	public		int		getNumberOfItems()
		{
		if (!hasLayers)
			return(super.getNumberOfItems());
		else if (currentLayer != null)
			return(currentLayer.getNumberOfItems());

		throwNoCurrentLayerException();
		return(0);
		}
					/**------------------------------------------------------
					 * Gets the MiPart at the given index in the current layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as getPart().
					 * @param index		the index of the part to get
					 * @return 		the part in the current layer.
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#getItem
					 *------------------------------------------------------*/
	public		MiPart		getItem(int index)
		{
		if (!hasLayers)
			return(super.getItem(index));
		else if (currentLayer != null)
			return(currentLayer.getItem(index));

		throwNoCurrentLayerException();
		return(null);
		}
					/**------------------------------------------------------
					 * Gets the MiPart with the given name in the current layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as getPart().
					 * @param name 		the name of the part to get
					 * @return 		the part in the current layer.
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#getItem
					 *------------------------------------------------------*/
	public		MiPart		getItem(String name)
		{
		if (!hasLayers)
			return(super.getItem(name));
		else if (currentLayer != null)
			return(currentLayer.getItem(name));

		throwNoCurrentLayerException();
		return(null);
		}
					/**------------------------------------------------------
					 * Appends the given MiPart to the current layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as appendPart().
					 * @param item 		the part to append
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#appendItem
					 *------------------------------------------------------*/
	public		void		appendItem(MiPart item)
		{
		if (!hasLayers)
			super.appendItem(item);
		else if ((currentConnectionLayer != null) && (item instanceof MiConnection))
			currentConnectionLayer.appendItem(item);
		else if (currentLayer != null)
			currentLayer.appendItem(item);
		else
			throwNoCurrentLayerException();
		}
					/**------------------------------------------------------
					 * Inserts the given MiPart into the current layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as insertPart().
					 * @param item 		the part to insert
					 * @param index 	the position of the insertion point
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#insertItem
					 *------------------------------------------------------*/
	public		void		insertItem(MiPart item, int index)
		{
		if (!hasLayers)
			super.insertItem(item, index);
		else if ((currentConnectionLayer != null) && (item instanceof MiConnection))
			currentConnectionLayer.insertItem(item, index);
		else if (currentLayer != null)
			currentLayer.insertItem(item, index);
		else
			throwNoCurrentLayerException();
		}
					/**------------------------------------------------------
					 * Sets (replaces) the MiPart at the given index in the 
					 * current layer with the given MiPart.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as setPart().
					 * @param item 		the new part 
					 * @param index 	the position of the replacement point
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#setItem
					 *------------------------------------------------------*/
	public		void		setItem(MiPart item, int index)
		{
		if (!hasLayers)
			super.setItem(item, index);
		else if (currentLayer != null)
			currentLayer.setItem(item, index);
		else
			throwNoCurrentLayerException();
		}
					/**------------------------------------------------------
					 * Removes the MiPart at the given index from the current
					 * layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as removePart().
					 * @param index 	the position of the part to remove
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#removeItem
					 *------------------------------------------------------*/
	public		void		removeItem(int index)
		{
		if (!hasLayers)
			super.removeItem(index);
		else if (currentLayer != null)
			currentLayer.removeItem(index);
		else
			throwNoCurrentLayerException();
		}
					/**------------------------------------------------------
					 * Removes the given MiPart from the current layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as removePart().
					 * @param index 	the position of the part to remove
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#removeItem
					 *------------------------------------------------------*/
	public		void		removeItem(MiPart item)
		{
		if (!hasLayers)
			super.removeItem(item);
		else if (currentLayer != null)
			currentLayer.removeItem(item);
		else
			throwNoCurrentLayerException();
		}
					/**------------------------------------------------------
					 * Gets the index of the given MiPart in the current layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as getIndexOfPart().
					 * @param item 		the MiPart to get the index of
					 * @return		the index or -1, if not found
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#getIndexOfItem
					 *------------------------------------------------------*/
	public		int		getIndexOfItem(MiPart item)
		{
		if (!hasLayers)
			return(super.getIndexOfItem(item));
		else if (currentLayer != null)
			return(currentLayer.getIndexOfItem(item));

		throwNoCurrentLayerException();
		return(-1);
		}
					/**------------------------------------------------------
					 * Gets whether the given MiPart is found in the current 
					 * layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as containsPart().
					 * @param item 		the MiPart to look for
					 * @return		true if the part is found in the
					 *			current layer
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#containsItem
					 *------------------------------------------------------*/
	public		boolean		containsItem(MiPart item)
		{
		if (!hasLayers)
			return(super.containsItem(item));
		else if (currentLayer != null)
			return(currentLayer.containsItem(item));

		throwNoCurrentLayerException();
		return(false);
		}
					/**------------------------------------------------------
					 * Removes all MiParts from the current layer.
					 * If this MiEditor does not have layers, then the 'current
					 * layer' is just the MiEditor itself and this will be
					 * the same as removeAllParts().
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @overrides		MiPart#removeAllItems
					 *------------------------------------------------------*/
	public		void		removeAllItems()
		{
		if (!hasLayers)
			super.removeAllItems();
		else if (currentLayer != null)
			currentLayer.removeAllItems();
		else
			throwNoCurrentLayerException();
		}
					/**------------------------------------------------------
					 * Sets whether this MiEditor is to have 'layers'.
					 * @param flag 		true if the MiEditor is to have
					 * 			layers.
					 * @see			#hasLayers
					 * @see			#setCurrentLayer
					 *------------------------------------------------------*/
	public		void		setHasLayers(boolean flag)
		{
		hasLayers = flag;
		updateLayerBounds();
		}
					/**------------------------------------------------------
					 * Sets whether this MiEditor is to have 'layers'.
					 * @return 		true if the MiEditor has layers.
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 *------------------------------------------------------*/
	public		boolean		hasLayers()
		{
		return(hasLayers);
		}
					/**------------------------------------------------------
					 * Gets whether this given MiPart is a layer is this 
					 * MiEditor.
					 * @param part		the part to test
					 * @return		true if it is a layer
					 * @see			#setHasLayers
					 *------------------------------------------------------*/
	public		boolean		isLayer(MiPart part)
		{
		if (!hasLayers)
			return(false);
		return(containsPart(part));
		}
					/**------------------------------------------------------
					 * Specifies that the given layer is to be the current
					 * layer. Hereafter the methods like appendItem and 
					 * getItem will act on the MiParts in this new layer.
					 * @param index		the index of the layer.
					 * @see			#setHasLayers
					 * @see			#getCurrentLayer
					 * @see			#setActiveLayer
					 *------------------------------------------------------*/
	public		void		setCurrentLayer(int index)
		{
		MiPart previousCurrentLayer = currentLayer;
		currentLayer = getPart(index);
		dispatchAction(Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION, previousCurrentLayer);
		}
					/**------------------------------------------------------
					 * Specifies that the given layer is to be the current
					 * layer. Hereafter the methods like appendItem and 
					 * getItem will act on the MiParts in this new layer.
					 * @param layer		the new layer.
					 * @see			#setHasLayers
					 * @see			#getCurrentLayer
					 * @see			#setActiveLayer
					 *------------------------------------------------------*/
	public		void		setCurrentLayer(MiPart layer)
		{
		MiPart previousCurrentLayer = currentLayer;
		currentLayer = layer;
		dispatchAction(Mi_EDITOR_CURRENT_LAYER_CHANGED_ACTION, previousCurrentLayer);
		}
					/**------------------------------------------------------
					 * Gets the layer that is the current layer. The methods 
					 * like appendItem and getItem will act on the MiParts
					 * in this layer.
					 * @param layer		the new current layer.
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 *------------------------------------------------------*/
	public		MiPart		getCurrentLayer()
		{
		if (!hasLayers)
			return(this);
		else
			return(currentLayer);
		}
					/**------------------------------------------------------
					 * Specifies that the given layer is to be the layer whose
					 * MiParts are the only ones to receive MiEvents. If null
					 * then the layers receive events from top to bottom as
					 * usual.
					 * @param layer		the new active layer or null.
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @see			#getActiveLayer
					 *------------------------------------------------------*/
	public		void		setActiveLayer(MiPart layer)
		{
		activeLayer = layer;
		}
					/**------------------------------------------------------
					 * Gets the layer is the layer whose MiParts are the only 
					 * ones to receive MiEvents. If null then the layers 
					 * receive events from top to bottom as usual.
					 * @param layer		the active layer or null.
					 * @see			#setHasLayers
					 * @see			#setCurrentLayer
					 * @see			#setActiveLayer
					 *------------------------------------------------------*/
	public		MiPart		getActiveLayer()
		{
		return(activeLayer);
		}
					/**------------------------------------------------------
					 *
					 *------------------------------------------------------*/
	public		void		setCurrentConnectionLayer(MiPart layer)
		{
		currentConnectionLayer = layer;
		}
					/**------------------------------------------------------
					 *
					 *------------------------------------------------------*/
	public		MiPart		getCurrentConnectionLayer()
		{
		if (currentConnectionLayer != null)
			return(currentConnectionLayer);
		return(getCurrentLayer());
		}
					/**------------------------------------------------------
					 * Gets the number of layers.
					 *------------------------------------------------------*/
	public		int		getNumberOfLayers()
		{
		if (hasLayers)
			return(super.getNumberOfParts());
		return(0);
		}
					/**------------------------------------------------------
					 * Gets the layer at the given index.
					 * @param index		the index of the layer to get
					 * @return 		the layer at the given index
					 *------------------------------------------------------*/
	public		MiPart		getLayer(int index)
		{
		return(super.getPart(index));
		}
					/**------------------------------------------------------
					 * Gets the iindex of the given layer.
					 * @param layer		the layer whose index to get
					 * @return 		the index of the given layer
					 *------------------------------------------------------*/
	public		int		getIndexOfLayer(MiPart layer)
		{
		return(super.getIndexOfPart(layer));
		}
					/**------------------------------------------------------
					 * Replaces the layer at the given index with the given 
					 * layer.
					 * @param layer		the new layer
					 * @param index		the index of the layer to replace
					 *------------------------------------------------------*/
	public		void		setLayer(MiPart layer, int index)
		{
		if (!getUniverseBounds().isReversed())
			{
			layer.setFixedWidth(false);
			layer.setFixedHeight(false);
			layer.setBounds(getUniverseBounds());
			}
		layer.setFixedWidth(true);
		layer.setFixedHeight(true);
		super.setPart(layer, index);
		dispatchAction(Mi_EDITOR_LAYER_ORDER_CHANGED_ACTION, layer);
		}
					/**------------------------------------------------------
					 * Appends the given layer.
					 * @param layer		the layer to append
					 *------------------------------------------------------*/
	public		void		appendLayer(MiPart layer)
		{
		if (!getUniverseBounds().isReversed())
			{
			layer.setFixedWidth(false);
			layer.setFixedHeight(false);
			layer.setBounds(getUniverseBounds());
			}
		layer.setFixedWidth(true);
		layer.setFixedHeight(true);
		super.appendPart(layer);
		if (!hasLayers)
			setHasLayers(true);
		dispatchAction(Mi_EDITOR_LAYER_ADDED_ACTION, layer);
		if (currentLayer == null)
			setCurrentLayer(layer);
		}
					/**------------------------------------------------------
					 * Inserts the given layer at the given index.
					 * @param layer		the layer to insert
					 * @param index		the index of the insertion point
					 *------------------------------------------------------*/
	public		void		insertLayer(MiPart layer, int index)
		{
		if (!hasLayers)
			setHasLayers(true);
		if (!getUniverseBounds().isReversed())
			{
			layer.setFixedWidth(false);
			layer.setFixedHeight(false);
			layer.setBounds(getUniverseBounds());
			}
		layer.setFixedWidth(true);
		layer.setFixedHeight(true);
		super.insertPart(layer, index);
		dispatchAction(Mi_EDITOR_LAYER_ADDED_ACTION, layer);
		}
					/**------------------------------------------------------
					 * Removes the given layer.
					 * @param layer		the layer to remove
					 *------------------------------------------------------*/
	public		void		removeLayer(MiPart layer)
		{
		super.removePart(layer);
		if (getNumberOfLayers() == 0)
			{
			setHasLayers(false);
			setCurrentLayer(null);
			}
		else if (layer == currentLayer)
			{
			boolean movedCurrentLayer = false;
			for (int i = 0; i < getNumberOfLayers(); ++i)
				{
				MiLayerAttributes atts = (MiLayerAttributes )getLayer(i).getResource(
						MiLayerAttributes.Mi_LAYER_ATTRIBUTES_RESOURCE_NAME);
				if ((atts == null) || (atts.getCanBeCurrent()))
					{
					setCurrentLayer(i);
					movedCurrentLayer = true;
					break;
					}
				}
			if (!movedCurrentLayer)
				setCurrentLayer(null);
			}
		dispatchAction(Mi_EDITOR_LAYER_REMOVED_ACTION, layer);
		}

	protected	void		throwNoCurrentLayerException()
		{
		if (getNumberOfParts() == 0)
			throw new RuntimeException(this 
				+ ": Editor indicates it has layers (hasLayers() == true) but does not");
		else
			throw new RuntimeException(this + ": Editor has layers but no \'current layer\'");
		}

	//***************************************************************************************
	// Selection manager convienence routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Sets the selection manager of this MiEditor. By default
					 * this MiEditor has a instance of MiSelectionManager for
					 * selection manager.
					 * @param sm		the new selection manager
					 *------------------------------------------------------*/
	public		void		setSelectionManager(MiiSelectionManager sm)
		{
		selectionManager = sm;
		}
					/**------------------------------------------------------
					 * Gets the selection manager of this MiEditor. By default
					 * this MiEditor has a instance of MiSelectionManager for
					 * selection manager.
					 * @return 		the selection manager
					 *------------------------------------------------------*/
	public	MiiSelectionManager	getSelectionManager()
		{
		return(selectionManager);
		}
					/**------------------------------------------------------
					 * Selects the given MiPart in this MiEditor, using this
					 * MiEditor's selection manager, which will maintain the 
					 * correct number of selected parts in the editor and also 
					 * will assign any MiiSelectionGraphics highlight to the 
					 * selected part as well.
					 * @param part 		the part to select.
					 * @see 		MiSelectionManager#selectObject
					 *------------------------------------------------------*/
	public		void		select(MiPart part)
		{
		selectionManager.selectObject(part);
		}
					/**------------------------------------------------------
					 * Selects the given MiPart in this MiEditor, using this
					 * MiEditor's selection manager, in addition to any other 
					 * selected parts (if allowed) which will maintain the 
					 * correct number of selected parts in the editor and also 
					 * will assign any MiiSelectionGraphics highlight to the 
					 * selected part as well.
					 * @param part 		the part to select.
					 * @see 		MiSelectionManager#selectAdditionalObject
					 *------------------------------------------------------*/
	public		void		selectAdditional(MiPart part)
		{
		selectionManager.selectAdditionalObject(part);
		}
					/**------------------------------------------------------
					 * Deselects the given MiPart in this MiEditor using this
					 * MiEditor's selection manager, which will maintain the 
					 * correct number of selected parts in the editor and also 
					 * removes any MiiSelectionGraphics highlight from the 
					 * selected part as well.
					 * @param part 		the part to deselect.
					 * @see 		MiSelectionManager#deSelectObject
					 *------------------------------------------------------*/
	public		void		deSelect(MiPart part)
		{
		selectionManager.deSelectObject(part);
		}
					/**------------------------------------------------------
					 * Deselects all MiParts in this MiEditor using this 
					 * MiEditor's selection manager, which will remove any 
					 * MiiSelectionGraphics highlight from the selected parts
					 * as well.
					 * @see 		MiSelectionManager#deSelectAll
					 *------------------------------------------------------*/
	public		void		deSelectAll()
		{
		selectionManager.deSelectAll();
		}
					/**------------------------------------------------------
					 * Gets the number of selected MiParts in this MiEditor.
					 * @return		the number of parts selected.
					 *------------------------------------------------------*/
	public		int		getNumberOfPartsSelected()
		{
		MiPart obj;
		int num = 0;
		MiiIterator iterator = getIterator();
		while ((obj = iterator.getNext()) != null)
			{
			if (obj.isSelected())
				++num;
			}
		return(num);
		}
	public		void		setFilter(MiEditorFilter filter)
		{
		this.filter = filter;
		//filter.setEditor(this);
		}
	public		MiEditorFilter	getFilter()
		{
		return(filter);
		}
	public		MiiIterator	getIterator()
		{
		if (filter != null)
			return(filter.getIterator(this));

		return(new MiEditorIterator(this));
		}
					/**------------------------------------------------------
					 * Gets the selected MiParts in this MiEditor.
					 * @param list		the (returned) list of selected parts.
					 * @return 		the list of selected parts.
					 *------------------------------------------------------*/
	public		MiParts		getSelectedParts(MiParts list)
		{
		MiPart obj;
		MiiIterator iterator = getIterator();
		list.removeAllElements();
		while ((obj = iterator.getNext()) != null)
			{
			if (obj.isSelected())
				{
				list.addElement(obj);
				}
			}
		return(list);
		}
					/**------------------------------------------------------
					 * Gets the approximate number of selected MiParts in this MiEditor.
					 * This is useful to get a programatic feel for how many items are
					 * selected.
					 * @param list		the (returned) list of up to 4 selected parts.
					 * @return 		the lsit with some selected parts:
					 *			0 = none are selected
					 *			1 = 1 is selected
					 *			2 = 2
					 *			3 = 3
					 *			4 = 4 or many  are selected
					 *------------------------------------------------------*/
	public		MiParts		getAFewSelectedParts(MiParts list)
		{
		int numSelected = 0;
		MiPart obj;
		MiiIterator iterator = getIterator();
		list.removeAllElements();
		while ((obj = iterator.getNext()) != null)
			{
			if (obj.isSelected())
				{
				++numSelected;
				list.addElement(obj);
				if (numSelected == 4)
					{
					break;
					}
				}
			}
		return(list);
		}

	//***************************************************************************************
	// Viewport routines
	//***************************************************************************************

					/**------------------------------------------------------
					 * Gets the viewport used by this MiEditor.
					 *------------------------------------------------------*/
	public		MiViewport	getViewport()
		{
		return(viewport);
		}
					/**------------------------------------------------------
					 * Gets the bounds of the device coordinate space of this 
					 * MiEditor.
					 * @param deviceBounds	the (returned) device bounds
					 * @return 		the device bounds
					 *------------------------------------------------------*/
	public		MiBounds	getDeviceBounds(MiBounds deviceBounds)
		{
		viewport.getDeviceBounds(deviceBounds);
		return(deviceBounds);
		}
					/**------------------------------------------------------
					 * Gets the bounds of the world coordinate space of this 
					 * MiEditor.
					 * @param deviceBounds	the (returned) world bounds
					 * @return 		the world bounds
					 *------------------------------------------------------*/
	public		MiBounds	getWorldBounds(MiBounds bounds)
		{
		viewport.getWorldBounds(bounds);	
		return(bounds);
		}
					/**------------------------------------------------------
					 * Gets the bounds of the universe coordinate space of this 
					 * MiEditor.
					 * @param bounds	the (returned) universe bounds
					 * @return 		the universe bounds
					 *------------------------------------------------------*/
	public		MiBounds	getUniverseBounds(MiBounds bounds)
		{
		viewport.getUniverseBounds(bounds);	
		return(bounds);
		}
					/**------------------------------------------------------
					 * Gets the bounds of the device coordinate space of this 
					 * MiEditor.
					 * @return 		the device bounds
					 *------------------------------------------------------*/
	public		MiBounds	getDeviceBounds()
		{
		return(getDeviceBounds(new MiBounds()));	
		}
					/**------------------------------------------------------
					 * Gets the bounds of the world coordinate space of this 
					 * MiEditor.
					 * @return 		the world bounds
					 *------------------------------------------------------*/
	public		MiBounds	getWorldBounds()
		{
		return(getWorldBounds(new MiBounds()));	
		}
					/**------------------------------------------------------
					 * Gets the bounds of the universe coordinate space of this 
					 * MiEditor.
					 * @return 		the universe bounds
					 *------------------------------------------------------*/
	public		MiBounds	getUniverseBounds()
		{
		return(getUniverseBounds(new MiBounds()));
		}

					/**------------------------------------------------------
					 * Sets the bounds of the world coordinate space of this 
					 * MiEditor.
					 * @param bounds	the new world bounds
					 * @action 		(if only panning)
					 *			Mi_EDITOR_WORLD_TRANSLATED_ACTION
					 * @action 		(if only panning)
					 *			Mi_ITEM_SCROLLED_ACTION
					 * @action 		(if not only panning)
					 *			Mi_EDITOR_WORLD_RESIZED_ACTION
					 * @action 		(if not only panning)
					 *			Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION
					 * @action 		Mi_EDITOR_VIEWPORT_CHANGED_ACTION
					 *------------------------------------------------------*/
	public		void		setWorldBounds(MiBounds bounds)
		{
		/*---------------------------------------------------------
		 * @private	do not use tmpBounds as the given bounds,
		 *		use tmpBounds2 instead
		 *---------------------------------------------------------*/
		MiBounds oldBounds = getWorldBounds();
		if (bounds.equals(oldBounds))
			return;

		viewport.enforceWorldAspectRatio(bounds);

		boolean panning = bounds.equalsSize(oldBounds);


		MiBounds newUniverseBounds = getUniverseBounds().union(bounds);
		boolean universeChanged = false;
		if (!getUniverseBounds().equals(newUniverseBounds))
			{
			viewport.setUniverseBounds(newUniverseBounds);
			viewport.setWorldBounds(bounds);
			updateLayerBounds();
			universeChanged = true;
			}
		else
			{
			viewport.setWorldBounds(bounds);
			}

		if (bitbltScrollingEnabled && panning && (getBackgroundColor() != null)
			&& (getDrawManager() != null) && (bounds.intersects(oldBounds)))
			{
			// Get combined transforms...
			MiTransforms transform;

			// Old device coordinate at 0,0 is:
			tmpPoint.set(0,0);
			viewport.setWorldBounds(oldBounds);
			transform = getTransformFromHereToRoot();
			transform.wtod(tmpPoint, tmpDPoint);
			//getRenderer().getTransform().wtod(tmpPoint, tmpDPoint);
			int x = tmpDPoint.x;
			int y = tmpDPoint.y;

			// New device coordinate at 0,0 is:
			tmpPoint.set(0,0);
			viewport.setWorldBounds(bounds);
			transform = getTransformFromHereToRoot();
			transform.wtod(tmpPoint, tmpDPoint);
			//getRenderer().getTransform().wtod(tmpPoint, tmpDPoint);

			// The difference in world space is:
			tmpDVector.x = tmpDPoint.x - x;
			tmpDVector.y = tmpDPoint.y - y;
			transform.dtow(tmpDPoint, tmpDVector, tmpVector);
			//getRenderer().getTransform().dtow(tmpDPoint, tmpDVector, tmpVector);

			if (tmpVector.isZero())
				return;

			// The adjusted new bounds is:
			tmpBounds.xmin = oldBounds.xmin - tmpVector.x;
			tmpBounds.ymin = oldBounds.ymin - tmpVector.y;
			tmpBounds.xmax = oldBounds.xmax - tmpVector.x;
			tmpBounds.ymax = oldBounds.ymax - tmpVector.y;
			viewport.setWorldBounds(tmpBounds);

			// Request bitblit
			getDrawManager().scrollTotalArea(-tmpVector.x, -tmpVector.y);
			}
		else
			{
			if (getDrawManager() != null)
				getDrawManager().invalidateBackToFront(getDeviceBounds(tmpBounds));
			else
				invalidateArea();
			}

		if (panning)
			{
			dispatchAction(Mi_EDITOR_WORLD_TRANSLATED_ACTION);
			dispatchAction(Mi_ITEM_SCROLLED_ACTION);
			}
		else
			{
			dispatchAction(Mi_EDITOR_WORLD_RESIZED_ACTION);
			dispatchAction(Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION);
			}
		if (universeChanged)
			dispatchAction(Mi_EDITOR_UNIVERSE_RESIZED_ACTION);
		dispatchAction(Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		}
					/**------------------------------------------------------
					 * Sets the bounds of the universe coordinate space of this 
					 * MiEditor.
					 * @param bounds	the new universe bounds
					 * @action 		Mi_EDITOR_VIEWPORT_CHANGED_ACTION
					 * @action 		Mi_EDITOR_UNIVERSE_RESIZED_ACTION
					 * @action 		Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION
					 *------------------------------------------------------*/
	public		void		setUniverseBounds(MiBounds bounds)
		{
		if (bounds.equals(viewport.getUniverseBounds(tmpBounds)))
			return;
		// ---------------------------------------------------
		// Assure universe contains world;
		// ---------------------------------------------------
		bounds.union(viewport.getWorldBounds(tmpBounds));
		viewport.setUniverseBounds(bounds);
		updateLayerBounds();
		dispatchAction(Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		dispatchAction(Mi_EDITOR_UNIVERSE_RESIZED_ACTION);
		dispatchAction(Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION);
		}
	public		void		updateLayerBounds()
		{
		if (hasLayers)
			{
			getUniverseBounds(tmpBounds);
			if (!tmpBounds.isReversed())
				{
				int numLayers = getNumberOfLayers();
				for (int i = 0; i < numLayers; ++i)
					{
					MiPart layer = getLayer(i);
					layer.setFixedWidth(false);
					layer.setFixedHeight(false);

					layer.setOutgoingInvalidLayoutNotificationsEnabled(false);
					layer.setInvalidAreaNotificationsEnabled(false);
					//layer.setBounds(tmpBounds);
					layer.replaceBounds(tmpBounds); // FIX: using this => don't need to disable notifications
					layer.setFixedWidth(true);
					layer.setFixedHeight(true);
					layer.validateLayout();
					layer.setInvalidAreaNotificationsEnabled(true);
					layer.setOutgoingInvalidLayoutNotificationsEnabled(true);
					}
				}
			}
		}
					/**------------------------------------------------------
					 * Sets the bounds of the device coordinate space of this 
					 * MiEditor.
					 * @param bounds	the new device bounds
					 * @action 		(if only translating)
					 *			Mi_EDITOR_DEVICE_TRANSLATED_ACTION
					 * @action 		(if not only translating)
					 *			Mi_EDITOR_DEVICE_RESIZED_ACTION
					 * @action 		Mi_EDITOR_VIEWPORT_CHANGED_ACTION
					 * @action 		Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION
					 *------------------------------------------------------*/
	public		void		setDeviceBounds(MiBounds deviceBounds)
		{
//MiDebug.println(this + "MiEditor.setDeviceBounds: " + deviceBounds);
//MiDebug.println("viewport.getDeviceBounds(tmpBounds) = " + viewport.getDeviceBounds(tmpBounds));
		if (deviceBounds.equals(viewport.getDeviceBounds(tmpBounds)))
			return;
		boolean translating = deviceBounds.equalsSize(viewport.getDeviceBounds(tmpBounds));

		viewport.setDeviceBounds(deviceBounds);
		replaceBounds(deviceBounds);
		invalidateArea(deviceBounds);
		updateLayerBounds();

		if (translating)
			dispatchAction(Mi_EDITOR_DEVICE_TRANSLATED_ACTION);
		else
			dispatchAction(Mi_EDITOR_DEVICE_RESIZED_ACTION);
		dispatchAction(Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		dispatchAction(Mi_ITEMS_SCROLLED_AND_MAGNIFIED_ACTION);
//MiDebug.println(this + "REALLY MiEditor.setDeviceBounds: " + deviceBounds);
		if (isRootWindow())
			{
			setScreenBounds(deviceBounds);
			}
		else
			{
			super.invalidateLayout();
			}
		}
					/**------------------------------------------------------
					 * Sets the bounds of the containing root window (i.e.
					 * a java.awt.Canvas) to the given bounds.
					 * @param deviceBounds		the bounds
					 *------------------------------------------------------*/
	protected	void		setScreenBounds(MiBounds deviceBounds)
		{
		getRootWindow().setScreenBounds(deviceBounds);
		}
					/**------------------------------------------------------
					 * Gets the bounds of the containing root window (i.e.
					 * a java.awt.Canvas).
					 * @return 		the bounds
					 *------------------------------------------------------*/
	protected	MiBounds	getScreenBounds()
		{
		return(getRootWindow().getScreenBounds());
		}

	public		void		setPageManager(MiPageManager manager)
		{
		pageManager = manager;
		pageManager.setEditor(this);
		}
	public		MiPageManager	getPageManager()
		{
		return(pageManager);
		}
	public		void		setSnapManager(MiSnapManager manager)
		{
		if (snapManager != null)
			snapManager.setTargetEditor(null);
		snapManager = manager;
		if (snapManager != null)
			snapManager.setTargetEditor(this);
		}
	public		MiSnapManager	getSnapManager()
		{
		return(snapManager);
		}
	public		boolean 	snapMovingPoint(MiPoint proposedPoint)
		{
		if (snapManager == null)
			return(false);
		return(snapManager.snap(proposedPoint));
		}
	public		boolean 	snapMovingPart(MiPart part, MiVector proposedVector)
		{
		if ((snapManager == null) || (!part.isSnappable()) 
			|| ((part instanceof MiReference) && (part.getNumberOfParts() > 0) && (!part.getPart(0).isSnappable())))
			{
			return(false);
			}
		return(snapManager.getSnapTranslation(part, proposedVector));
		}
	public		boolean 	snapPart(MiPart part)
		{
		if ((snapManager == null) || (!part.isSnappable()) 
			|| ((part instanceof MiReference) && (part.getNumberOfParts() > 0) && (!part.getPart(0).isSnappable())))
			{
			return(false);
			}
		MiVector proposedVector = new MiVector();
		boolean needsToMove = snapManager.getSnapTranslation(part, proposedVector);
		part.translate(proposedVector);
		return(needsToMove);
		}
					/**------------------------------------------------------
					 * Sets whether an part moved by the user can expand the editing
					 * area (create scrollbars and then reduce scrollbar thumbs size).
					 * @param flag		true if the user dragging a part into
					 *			the side of the editor not only pans to the
					 *			side of the editor, but also increases the
					 *			total amount of editiing area.
					 *------------------------------------------------------*/
	public		void		setAutopanningCanExpandUniverse(boolean flag)
		{
		autopanningCanExpandUniverse = flag;
		}
					/**------------------------------------------------------
					 * Gets whether an part moved by the user can expand the editing
					 * area (create scrollbars and then reduce scrollbar thumbs size).
					 * @return 		true if the user dragging a part into
					 *			the side of the editor not only pans to the
					 *			side of the editor, but also increases the
					 *			total amount of editiing area.
					 *------------------------------------------------------*/
	public		boolean		getAutopanningCanExpandUniverse()
		{
		return(autopanningCanExpandUniverse);
		}
					/**------------------------------------------------------
					 * Automatically pan (scroll horizontally and vertically)
					 * this editor if the given vector, applied to the given 
					 * bounds, would extend outside the viewable world bounds
					 * or the universe bounds. This is used when the end-user
					 * is dragging a MiPart and bangs up against an edge of 
					 * this editor.
					 * @param bounds	the bounds, probably of a dragged
					 *			MiPart
					 * @param tVector	the proposed amount that the caller
					 *			wants to move the bounds and
					 *			(returned) amount that is permissable.
					 * @return		true if tVector was modified
					 *------------------------------------------------------*/
	public		boolean 	autopanForMovingObj(MiBounds bounds, MiVector tVector)
		{
//MiDebug.println("\n\n\nautopanForMovingObj bounds = " + bounds);
//MiDebug.println("autopanForMovingObj tVector = " + tVector);
//MiDebug.println("autopanForMovingObj world = " + viewport.getWorldBounds());
		boolean modified = viewport.confineTranslatedExtremaToUniverse(tVector, bounds);
		if (tVector.isZero())
			{
			return(modified);
			}

		viewport.getAmountExtremaTranslatedOutsideWorld(tVector, bounds, tmpVector);
//MiDebug.println("autopanForMovingObj tmpVector = " + tmpVector);

		// Check to see if pan is in same direction as user's drag motion...
		// ... if it is not, do not auto pan.
		double direction = tmpVector.x/tVector.x;
		if (direction < 0)
			return(modified);
//MiDebug.println("autopanForMovingObj direction = " + direction);

		// Confine pan to less than or equal to amount of user's motion
		if (direction > 1)
			tmpVector.x = tVector.x;

		direction = tmpVector.y/tVector.y;
		if (direction < 0)
			return(modified);
		if (direction > 1)
			tmpVector.y = tVector.y;

//MiDebug.println(" tmpVector = " + tmpVector);
		if ((tmpVector.x != 0.0) || (tmpVector.y != 0.0))
			{
			MiBounds world = getWorldBounds();
			world.translate(tmpVector.x, tmpVector.y);

			boolean bitbltScrollingEnabledOrig = bitbltScrollingEnabled;
			bitbltScrollingEnabled = false;
//MiDebug.println(" autopanningCanExpandUniverse=" + autopanningCanExpandUniverse);
			if (!autopanningCanExpandUniverse)
				{
				viewport.confineWorldToUniverse(world);
				}
//MiDebug.println(" panninggggg");
			setWorldBounds(world);
			bitbltScrollingEnabled = bitbltScrollingEnabledOrig;
			}
		return(modified);
		}

	//***************************************************************************************
	// Layout management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets whether this MiEditor's layout, if any, is valid
					 * and does not need to be recalculated.
	 				 * @return  	  	true if layout is valid
					 *------------------------------------------------------*/
	public		boolean		hasValidLayout()
		{
		return(editorValidLayout);
		}
					/**------------------------------------------------------
	 				 * Validates this MiEditor's layout, and viewport size
					 * layout.
					 * @action		Mi_EDITOR_CONTENTS_GEOMETRY_CHANGED_ACTION
					 * @see			#setViewportSizeLayout
					 *------------------------------------------------------*/
	public		void		validateLayout()
		{
		MiBounds b;
		if (hasValidLayout())
			return;

		if (getViewportSizeLayout() != null)
			getViewportSizeLayout().validateLayout();

		super.validateLayout();

		if (getViewportSizeLayout() != null)
			getViewportSizeLayout().validateLayout();

		dispatchAction(Mi_EDITOR_CONTENTS_GEOMETRY_CHANGED_ACTION);
		editorValidLayout = true;
		}
					/**------------------------------------------------------
	 				 * Specifies that this MiEditor has an invalid layout. Any
					 * layout assigned to this MiEditor is also invalidated.
					 * No containers have their layouts invalidated; instead
					 * the roow window of this MiEditor is told to validate
					 * this editor at it's next opportunity.
					 *------------------------------------------------------*/
	public		void		invalidateLayout()
		{
		if (!editorValidLayout)
			{
			return;
			}
		// Stop notifications at editor level
		if (getLayout() != null)
			getLayout().invalidateLayout();
		editorValidLayout = false;
		MiWindow root = getRootWindow();
		if ((root != null) && (root != this))
			root.invalidateEditorPartLayout(this);
		}
					/**------------------------------------------------------
					 * Gets the layout that manages dependancies between the
					 * world and device coordinates.
					 * @return		the viewport size layout
					 *------------------------------------------------------*/
	public		MiiEditorViewportSizeLayout	getViewportSizeLayout()
		{
		return(viewportSizeLayout);
		}
					/**------------------------------------------------------
					 * Sets the layout that manages dependancies between the
					 * world and device coordinates.
					 * @param layout	the viewport size layout
					 *------------------------------------------------------*/
	public		void		setViewportSizeLayout(MiiEditorViewportSizeLayout layout)
		{
		viewportSizeLayout = layout;
		if (layout != null)
			{
			layout.setTarget(this);
			}
		else
			{
			viewport.setResizePolicy(MiViewport.SCALE_WORLD_PROPORTIONALLY_WITH_DEVICE);
			}
		}

	//***************************************************************************************
	// Preferred and minimum size and bounds management
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Gets the preferred size of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
					 * This implementation is to:
					 *	Get the preferred size of the device coordinate
					 *	space from the viewport size layout, if there is
					 *	one
					 * else
					 *	Get the current size of the device coordinate
					 *	space.
	 				 * @param size		the (returned) preferred size
					 * @overrides		MiContainer#calcPreferredSize
					 * @see			MiPart#getPreferredSize
					 *------------------------------------------------------*/
	protected	void		calcPreferredSize(MiSize size)
		{
		if (getViewportSizeLayout() != null) 
			{
			getViewportSizeLayout().getPreferredSizeOfDevice(tmpBounds2);
			size.setSize(tmpBounds2);
			}
		else
			{
			super.calcPreferredSize(size);
			//getDeviceBounds(tmpBounds2);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the minimum size of this MiPart. Override this, 
					 * if desired, as it implements the core functionality.
					 * This implementation is to:
					 *	Get the minimum size of the device coordinate
					 *	space from the viewport size layout, if there is
					 *	one
					 * else
					 *	Get the minimum size as calculated by the
					 *	super class (MiContainer)
	 				 * @param size		the (returned) minimum size
					 * @overrides		MiContainer#calcMinimumSize
					 * @see			MiPart#getMinimumSize
					 *------------------------------------------------------*/
	protected	void		calcMinimumSize(MiSize size)
		{
		if (getViewportSizeLayout() != null) 
			{
			getViewportSizeLayout().getMinimumSizeOfDevice(tmpBounds2);
			size.setSize(tmpBounds2);
			}
		else
			{
			super.calcMinimumSize(size);
			}
		}
					/**------------------------------------------------------
					 * Gets the preferred size of the contents (all the MiParts)
					 * of this editor.
					 * @param size		the (returned) preferred size of
					 *			this MiEditor's contents
					 *------------------------------------------------------*/
	protected	void		calcPreferredSizeOfContents(MiSize size)
		{
		super.calcPreferredSize(size);	
		}
					/**------------------------------------------------------
					 * Gets the minimum size of the contents (all the MiParts)
					 * of this editor.
					 * @param size		the (returned) minimum size of
					 *			this MiEditor's contents
					 *------------------------------------------------------*/
	protected	void		calcMinimumSizeOfContents(MiSize size)
		{
		super.calcMinimumSize(size);	
		}
					/**------------------------------------------------------
					 * Gets the (recalculated) bounds of the contents (all the
					 * MiParts) of this editor.
					 * @param bounds	the (returned) bounds of this 
					 *			MiEditor's contents
					 *------------------------------------------------------*/
	public		void		reCalcBoundsOfContents(MiBounds bounds)
		{
		bounds.reverse();
		for (int i = 0; i < getNumberOfItems(); ++i)
			{
			MiPart obj = getItem(i);
			if (obj.isVisible())
				{
				obj.getBounds(tmpBounds);
				bounds.union(tmpBounds);
				}
			}
		}
					/**------------------------------------------------------
					 * Gets the (recalculated) bounds of this MiEditor. This
					 * method just returns the device bounds of this MiEditor.
					 * @param b		the (returned) bounds of this MiEditor
					 *------------------------------------------------------*/
	public		void		reCalcBounds(MiBounds b)
		{
		getDeviceBounds(b);
		}
	public		void		copy(MiPart obj)
		{
		super.copy(obj);
		viewport.setDeviceBounds(getBounds());
		}

	//***************************************************************************************
	// Overridden methods to handle fact that we also save 
	// bounds as the viewport's device bounds and that when
	// the editor is translated or scaled, this should not
	// affect the editor's parts in any way.
	//***************************************************************************************

					/**------------------------------------------------------
					 * Translate this MiEditor as a part of a container that
					 * is being translated. This method lets the super class
					 * do the work and then translates this MiEditor's device
					 * bounds.
	 				 * @param tx	 	the x translation
	 				 * @param ty	 	the y translation
	 				 * @action 		Mi_GEOMETRY_CHANGE_ACTION
	 				 * @see 		MiPart#translatePart
					 *------------------------------------------------------*/
	public		void		translatePart(MiDistance tx, MiDistance ty)
		{
		super.translatePart(tx, ty);
		if (!isRootWindow())
			{
			viewport.translateDevice(tx, ty);
			if (getDrawManager() != null)
				getDrawManager().invalidateBackToFront(getBounds(tmpBounds2));
			}
		else
			{
			setDeviceBounds(getBounds(tmpBounds2));
			}
		// --------------------------------------------------------
		// Send action after all changes have been made.
		// --------------------------------------------------------
		geometryChanged();
		}
					/**------------------------------------------------------
	 				 * Translates the parts that this container has. This 
					 * method lets the super class do the work and then 
					 * translates this MiEditor's device bounds.
					 * No parts of this MiEditor are scaled or translated in
					 * kind because scaleParts and translateParts are overridden
					 * here to do nothing. 
					 * @param x		the x distance to translate
					 * @param y		the y distance to translate
	 				 * @see 		MiContainer#translate
					 *------------------------------------------------------*/
	public		void		translate(MiDistance x, MiDistance y)
		{
		if (Utility.areZero(x, y))
			return;

		super.translate(x, y);
		if (!isRootWindow())
			{
			viewport.translateDevice(x, y);
			if (getDrawManager() != null)
				getDrawManager().invalidateBackToFront(getBounds(tmpBounds2));
			else
				invalidateArea(getBounds(tmpBounds2));
			}
		else
			{
			setDeviceBounds(getBounds(tmpBounds2));
			}
		// --------------------------------------------------------
		// Send action after all changed have been made.
		// --------------------------------------------------------
		geometryChanged();
		}
					/**------------------------------------------------------
	 				 * Scales the bounds of this MiEditor by the given scale
					 * factor (multipliers). This method lets the super class
					 * do the work and then translates this MiEditor's device 
					 * bounds.
					 * No parts of this MiEditor are scaled or translated in
					 * kind because scaleParts and translateParts are overridden
					 * here to do nothing. 
	 				 * @param center	the center of the scaling
	 				 * @param scale		the scale factor
	 				 * @see 		MiPart#scale
					 *------------------------------------------------------*/
	public		void		scale(MiPoint center, MiScale scale)
		{
		if (scale.isIdentity())
			return;

		super.scale(center, scale);
		setDeviceBounds(getBounds());
		}
					/**------------------------------------------------------
	 				 * Sets the bounds of this MiEditor to have the given bounds. 
					 * No parts of this MiEditor are scaled or translated in
					 * kind because scaleParts and translateParts are overridden
					 * here to do nothing. This method lets the super class do the 
					 * work and then translates this MiEditor's device bounds.
	 				 * @param bounds	the new bounds
	 				 * @see 		MiPart#setBounds
					 *------------------------------------------------------*/
	public		void		setBounds(MiBounds bounds)
		{
		super.setBounds(bounds);
		setDeviceBounds(getBounds(tmpBounds2));
		}
					/**------------------------------------------------------
	 				 * Translates the parts of this MiPart by the given 
					 * distances. This method does nothing.
	 				 * @param tx	 	the x translation
	 				 * @param ty	 	the y translation
	 				 * @see 		MiContainer#translateParts
					 *------------------------------------------------------*/
	protected	void		translateParts(MiDistance tx, MiDistance ty)
		{
		// Do not translate contents
		}
					/**------------------------------------------------------
	 				 * Scales the parts of this MiPart by the given scale
					 * factor. This method does nothing.
	 				 * @param center 	the center of scaling
	 				 * @param scale	 	the scale factor
	 				 * @see 		MiContainer#scaleParts
					 *------------------------------------------------------*/
	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		// Do not scale contents
		}
					/**------------------------------------------------------
	 				 * A direct modification of the current bounds of this
					 * MiEditor. This method does nothing.
	 				 * @param b 	 	the new bounds
					 * @see			MiPart#unionBounds
					 *------------------------------------------------------*/
	protected	void		unionBounds(MiBounds b)
		{
		}
					/**------------------------------------------------------
	 				 * A direct modification of the current bounds of this
					 * MiEditor. This method does nothing.
	 				 * @param pt 	 	the new bounds
					 * @see			MiPart#unionBounds
					 *------------------------------------------------------*/
	protected	void		unionBounds(MiPoint pt)
		{
		}

	//***************************************************************************************
 	// Implementation of MiiScrollableData
	//***************************************************************************************

					/**------------------------------------------------------
			 		 * Gets whether actions like scrollLineDown is handled
					 * in this interfaces implementation or whether these type
					 * of methods are merely to be notified of the scrolling
					 * calculated elsewhere and executed by calling
					 * scrollToNormalizedVerticalPosition.
			 		 * @return 	 	true if minor scrolling is implemented here
					 *------------------------------------------------------*/
	public		boolean		isHandlingScrollingDiscreteAmountsLocally()
		{
		return(false);
		}

					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * horizontal position of the data (0.0 is the left side
					 * and 1.0 is the right side).
	 				 * @return 	 	the horizontal position
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		double		getNormalizedHorizontalPosition()
		{
		return(viewport.getHorizontalPositionOfWorldInUniverse());
		}
					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * vertical position of the data (0.0 is the left side
					 * and 1.0 is the right side).
	 				 * @return 	 	the vertical position
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		double		getNormalizedVerticalPosition()
		{
		return(viewport.getVerticalPositionOfWorldInUniverse());
		}
					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * horizontal size of the data (0.0 indicates none of the
					 * data is visible and 1.0 indicates all of the data's
					 * width is visible).
	 				 * @return 	 	the amount of data visible
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		double		getNormalizedHorizontalAmountVisible()
		{
		return(viewport.getHorizontalSizeOfWorldInUniverse());
		}
					/**------------------------------------------------------
	 				 * Gets the normalized (between 0.0 and 1.0 inclusive)
					 * vertical size of the data (0.0 indicates none of the
					 * data is visible and 1.0 indicates all of the data's
					 * height is visible).
	 				 * @return 	 	the amount of data visible
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		double		getNormalizedVerticalAmountVisible()
		{
		return(viewport.getVerticalSizeOfWorldInUniverse());
		}
					/**------------------------------------------------------
	 				 * Scrolls one line up (conversely, move the data one
					 * line down). If already at the top of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollLineUp()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(0, world.getHeight()/8);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one line down (conversely, move the data one
					 * line up). If already at the bottom of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollLineDown()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(0, -world.getHeight()/8);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one line left (conversely, move the data one
					 * line right). If already at the left of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollLineLeft()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(-world.getWidth()/8, 0);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one line right (conversely, move the data one
					 * line left). If already at the right of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollLineRight()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(world.getWidth()/8, 0);
		setWorldBounds(world);
		}

					/**------------------------------------------------------
	 				 * Scrolls one chunk up (conversely, move the data one
					 * chunk down). If already at the top of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollChunkUp()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(0, world.getHeight()/2);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one chunk down (conversely, move the data one
					 * chunk up). If already at the bottom of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollChunkDown()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(0, -world.getHeight()/2);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one chunk left (conversely, move the data one
					 * chunk right). If already at the left of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollChunkLeft()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(-world.getWidth()/2, 0);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one chunk right (conversely, move the data one
					 * chunk left). If already at the right of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollChunkRight()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(world.getWidth()/2, 0);
		setWorldBounds(world);
		}

					/**------------------------------------------------------
	 				 * Scrolls one page up (conversely, move the data one
					 * page down). If already at the top of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollPageUp()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(0, world.getHeight());
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one page down (conversely, move the data one
					 * page up). If already at the bottom of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollPageDown()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(0, -world.getHeight());
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one page left (conversely, move the data one
					 * page right). If already at the left of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollPageLeft()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(-world.getWidth(), 0);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls one page right (conversely, move the data one
					 * page left). If already at the right of the data then
					 * this does nothing. 
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollPageRight()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(world.getWidth(), 0);
		setWorldBounds(world);
		}

					/**------------------------------------------------------
	 				 * Scrolls to the top of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToTop()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(0, getUniverseBounds().getYmax() - world.getYmax());
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls to the bottom of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToBottom()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(0, getUniverseBounds().getYmin() - world.getYmin());
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls to the left side of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToLeftSide()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(getUniverseBounds().getXmin() - world.getXmin(), 0);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls to the right side of the data.
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToRightSide()
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		world.translate(getUniverseBounds().getXmax() - world.getXmax(), 0);
		setWorldBounds(world);
		}

					/**------------------------------------------------------
	 				 * Scrolls to the given normalized (between 0.0 and 1.0)
					 * horizontal position.
					 * @param normalizedPosition	the new horizontal position
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToNormalizedVerticalPosition(double normalizedLocation)
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		MiBounds u = getUniverseBounds();
		world.translate(0, 
			normalizedLocation * (u.getHeight() - world.getHeight()) + u.ymin - world.ymin);
		setWorldBounds(world);
		}
					/**------------------------------------------------------
	 				 * Scrolls to the given normalized (between 0.0 and 1.0)
					 * vertical position.
					 * @param normalizedPosition	the new vertical position
	 				 * @implements MiiScrollableData
					 *------------------------------------------------------*/
	public		void		scrollToNormalizedHorizontalPosition(double normalizedLocation)
		{
		MiBounds world = getWorldBounds(tmpBounds2);
		MiBounds u = getUniverseBounds();
		world.translate(
			normalizedLocation * (u.getWidth() - world.getWidth()) + u.xmin - world.xmin, 0);
		setWorldBounds(world);
		}
	}

