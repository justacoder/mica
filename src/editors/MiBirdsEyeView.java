
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


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiBirdsEyeView extends MiWidget implements MiiActionHandler
	{
	private		MiEditor		view;
	private		MiEditor		target;
	private		MiLocatorBox		locatorBox;
	private		MiVisibleContainer	insideContainer;
	private		boolean			updatePartsWhenShowing;
	private		boolean			isShowing;



	public				MiBirdsEyeView(MiEditor editor)
		{
		setupMiBirdsEyeView();
		setViewTarget(editor);
		}
	protected	void		setupMiBirdsEyeView()
		{
		setVisibleContainerAutomaticLayoutEnabled(false);
		setBorderLook(Mi_RAISED_BORDER_LOOK);

		setSize(new MiSize(200, 200));
		insideContainer = new MiVisibleContainer();
		insideContainer.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		insideContainer.setContainerLayoutSpec(MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS);
		//insideContainer.setVisibleContainerAutomaticLayoutEnabled(false);
		insideContainer.setInsetMargins(new MiMargins(2));
		insideContainer.setBackgroundColor(MiColorManager.lightGray);
		appendPart(insideContainer);

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);

		view = new MiEditor();
		view.setInvalidLayoutNotificationsEnabled(true);
		view.setBitbltScrollingEnabled(false);
		MiEditorFilter filter = new MiEditorFilter();
		MiViewFilter viewFilter = new MiViewFilter();
		viewFilter.setHideTheseClasses(new Strings(
		"com.swfm.mica.MiText\ncom.swfm.mica.MiBoundsManipulator\ncom.swfm.mica.MiMultiPointManipulator"));
		filter.setViewFilter(viewFilter);
		view.setFilter(filter);

		view.getViewport().setEnforcedAspectRatio(null);
		insideContainer.appendPart(view);
		locatorBox = new MiLocatorBox(this);
		view.appendPart(locatorBox);
		locatorBox.appendEventHandler(new MiBirdsEyeEventHandler(this, locatorBox));
		view.appendEventHandler(new MiBirdsEyeEventHandler(this, locatorBox, true));
		view.setBounds(new MiBounds(0,0,200,200));
		view.setPreferredSize(new MiSize(200,200));
		view.setMinimumSize(new MiSize(100,100));
		view.setLayout(null);
		}

	public		void		updateView()
		{
		if (!isShowing)
			return;

		MiBounds targetUniverseBounds = target.getUniverseBounds();
		view.setWorldBounds(targetUniverseBounds);
		view.setUniverseBounds(targetUniverseBounds);

		MiBounds device = getInnerBounds();
		device.subtractMargins(insideContainer.getTotalMargins());
		MiDistance width = targetUniverseBounds.getWidth();
		MiDistance height = targetUniverseBounds.getHeight();

		MiDistance deviceWidth = device.getHeight() * width/height;
		if (deviceWidth < device.getWidth())
			device.setWidth(deviceWidth);
		else
			device.setHeight(device.getWidth() * height/width);

		view.setDeviceBounds(device);
		view.setPreferredSize(new MiSize(device));

		view.setBackgroundColor(target.getBackgroundColor());
		insideContainer.setBackgroundColor(target.getBackgroundColor());

		locatorBox.moveTo(target.getWorldBounds());

		targetUniverseBounds = target.getUniverseBounds();
		view.setWorldBounds(targetUniverseBounds);
		view.setUniverseBounds(targetUniverseBounds);
		}

	public		void		setViewTarget(MiEditor targetEditor)
		{
		if (target != null)
			{
			target.removeActionHandlers(this);
			}
		target = targetEditor;
		target.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		target.appendActionHandler(this, Mi_ITEM_ADDED_ACTION);
		target.appendActionHandler(this, Mi_ITEM_REMOVED_ACTION);

		view.removeAllParts();
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			view.appendPart(target.getPart(i));

		view.appendPart(locatorBox);
		updateView();
		}
	public		MiEditor	getViewTarget()
		{
		return(target);
		}

	public		MiEditor	getView()
		{
		return(view);
		}

	public		MiLocatorBox	getLocatorBox()
		{
		return(locatorBox);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_EDITOR_VIEWPORT_CHANGED_ACTION))
			{
			updateView();
			}
		else if (action.hasActionType(Mi_ITEM_ADDED_ACTION))
			{
			if (isShowing)
				{
				view.removeAllParts();
				for (int i = 0; i < target.getNumberOfParts(); ++i)
					view.appendPart(target.getPart(i));

				view.appendPart(locatorBox);
				}
			else
				{
				updatePartsWhenShowing = true;
				}
			}
		else if (action.hasActionType(Mi_ITEM_REMOVED_ACTION))
			{
			if (isShowing)
				view.removePart((MiPart )action.getActionSystemInfo());
			else
				updatePartsWhenShowing = true;
			}
		return(true);
		}
	protected	void		nowShowing(boolean flag)
		{
		isShowing = flag;
		if (flag)
			{
			if (updatePartsWhenShowing)
				{
				view.removeAllParts();
				super.nowShowing(flag);

				for (int i = 0; i < target.getNumberOfParts(); ++i)
					view.appendPart(target.getPart(i));

				view.appendPart(locatorBox);
				}
			else
				{
				super.nowShowing(flag);
				}
			updateView();
			updatePartsWhenShowing = false;
			}
		else
			{
			super.nowShowing(flag);
			}
		}
	}
class MiLocatorBox extends MiRectangle
	{
	private		MiBirdsEyeView	view;


	public				MiLocatorBox(MiBirdsEyeView view)
		{
		this.view = view;

		setColor("red");
		setBackgroundColor("blue");
		setInvalidAreaNotificationsEnabled(false);
		setContextCursor(Mi_MOVE_CURSOR);
		setLineWidth(4);
		setWriteMode(Mi_XOR_WRITEMODE);
		}
	public		void		moveTo(MiBounds b)
		{
		// FIX: Also need to check all containers to see if they are double buffered...
		// Assume that running  in Swing, Swing will always double buffer us
		if ((getRootWindow().getCanvas().isDoubleBuffered())
			|| (((MiNativeWindow )getRootWindow()).getNativeComponentType() 
				== Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE))
			{
			setInvalidAreaNotificationsEnabled(true);
			setBounds(b);
			}
		else
			{
			drawNow();
			setBounds(b);
			drawNow();
			}
		}
	public		int		getContextCursor(MiBounds area)	
		{
		if (area == null)
			return(super.getContextCursor(area));

		MiPoint pt = area.getCenter();
		int cursor = getCursorAtLocation(pt);
		return(cursor);
		}

	protected	MiDistance	convertDeviceDistanceToViewDistance(MiDistance d)
		{
		MiBounds b = new MiBounds(0,0,d,d);
		view.getContainingEditor().transformToOtherEditorSpace(view.getView(), b, b);
		return(b.getWidth());
		}

	public		int		getCursorAtLocation(MiPoint pt)
		{
		MiBounds b = getBounds();
		int cursor = Mi_MOVE_CURSOR;
		int cursorEW = Mi_MOVE_CURSOR;
		int cursorNS = Mi_MOVE_CURSOR;
		MiDistance pickMargins = convertDeviceDistanceToViewDistance(8);

		if ((pt.x > b.getXmax() - pickMargins) && (pt.x < b.getXmax() + pickMargins))
			cursorEW = Mi_E_RESIZE_CURSOR;
		if ((pt.x < b.getXmin() + pickMargins) && (pt.x > b.getXmin() - pickMargins))
			cursorEW = Mi_W_RESIZE_CURSOR;
		if ((pt.y > b.getYmax() - pickMargins) && (pt.y < b.getYmax() + pickMargins))
			cursorNS = Mi_N_RESIZE_CURSOR;
		if ((pt.y < b.getYmin() + pickMargins) && (pt.y > b.getYmin() - pickMargins))
			cursorNS = Mi_S_RESIZE_CURSOR;

		if ((cursorEW == Mi_E_RESIZE_CURSOR) && (cursorNS == Mi_N_RESIZE_CURSOR))
			cursor = Mi_NE_RESIZE_CURSOR;
		else if ((cursorEW == Mi_W_RESIZE_CURSOR) && (cursorNS == Mi_N_RESIZE_CURSOR))
			cursor = Mi_NW_RESIZE_CURSOR;
		else if ((cursorEW == Mi_E_RESIZE_CURSOR) && (cursorNS == Mi_S_RESIZE_CURSOR))
			cursor = Mi_SE_RESIZE_CURSOR;
		else if ((cursorEW == Mi_W_RESIZE_CURSOR) && (cursorNS == Mi_S_RESIZE_CURSOR))
			cursor = Mi_SW_RESIZE_CURSOR;
		else if (cursorEW != Mi_MOVE_CURSOR)
			cursor = cursorEW;
		else
			cursor = cursorNS;
		
		return(cursor);
		}
	}

class MiBirdsEyeEventHandler extends MiEventHandler implements MiiTypes
	{
	private		int		moveMode = -1;
	private		MiBirdsEyeView	view;
	private		MiLocatorBox	box;
	private		MiVector	delta 				= new MiVector();
	private		MiPoint		startDragOffsetFromCenter	= new MiPoint();
	private		MiBounds	originalWorld 			= new MiBounds();


	public				MiBirdsEyeEventHandler(MiBirdsEyeView view, MiLocatorBox box)
		{
		this(view, box, false);
		}
	public				MiBirdsEyeEventHandler(MiBirdsEyeView view, MiLocatorBox box, boolean selectOnly)
		{
		this.box = box;
		this.view = view;

		if (!selectOnly)
			{
			addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
			addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
			addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
			}
		addEventToCommandTranslation(Mi_SELECT_COMMAND_NAME, Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0);
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
		MiEditor target = view.getViewTarget();
		if (isCommand(Mi_PICK_UP_COMMAND_NAME))
			{
			event.editor.prependGrabEventHandler(this);

			startDragOffsetFromCenter.set(
				event.worldPt.x - box.getCenterX(),
				event.worldPt.y - box.getCenterY());

			target.getWorldBounds(originalWorld);

			moveMode = view.getLocatorBox().getCursorAtLocation(event.getWorldPoint(new MiPoint()));
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (moveMode == -1)
				return(Mi_CONSUME_EVENT);

			delta.x = event.worldPt.x - (box.getCenterX() + startDragOffsetFromCenter.x);
			delta.y = event.worldPt.y - (box.getCenterY() + startDragOffsetFromCenter.y);
			if (target != null)
				{
				MiBounds world = target.getWorldBounds();
				switch (moveMode)
					{
					case Mi_MOVE_CURSOR		:
						world.translate(delta);
						break;
					case Mi_E_RESIZE_CURSOR		:
						world.setXmax(world.getXmax() + delta.x);
						break;
					case Mi_W_RESIZE_CURSOR		:
						world.setXmin(world.getXmin() + delta.x);
						break;
					case Mi_N_RESIZE_CURSOR		:
						world.setYmax(world.getYmax() + delta.y);
						break;
					case Mi_S_RESIZE_CURSOR		:
						world.setYmin(world.getYmin() + delta.y);
						break;
					case Mi_NE_RESIZE_CURSOR		:
						world.setXmax(world.getXmax() + delta.x);
						world.setYmax(world.getYmax() + delta.y);
						break;
					case Mi_NW_RESIZE_CURSOR		:
						world.setXmin(world.getXmin() + delta.x);
						world.setYmax(world.getYmax() + delta.y);
						break;
					case Mi_SE_RESIZE_CURSOR		:
						world.setXmax(world.getXmax() + delta.x);
						world.setYmin(world.getYmin() + delta.y);
						break;
					case Mi_SW_RESIZE_CURSOR		:
						world.setXmin(world.getXmin() + delta.x);
						world.setYmin(world.getYmin() + delta.y);
						break;
					default:
						System.out.println("Unknown cursor returned from: " 
							+ view.getLocatorBox());
					}
				target.getViewport().confineWorldToUniverse(world);
				target.setWorldBounds(world);
				}
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (moveMode == -1)
				return(Mi_CONSUME_EVENT);

			event.editor.removeGrabEventHandler(this);

			MiSystem.getViewportTransactionManager().appendTransaction(
				new MiPanAndZoomCommand(target, originalWorld, target.getWorldBounds()));
			}
		else if (isCommand(Mi_SELECT_COMMAND_NAME))
			{
			if (target != null)
				{
				MiBounds world = target.getWorldBounds();
				world.setCenter(event.worldPt);
				target.getViewport().confineWorldToUniverse(world);
				target.setWorldBounds(world);
				}
			}
		return(Mi_CONSUME_EVENT);
		}
	}

class MiFishEyeView extends MiWidget implements MiiActionHandler
	{
	private		MiEditor		view;
	private		MiEditor		target;
	private		MiRectangle		rect;
	private		MiReference		reference;
	private		MiVisibleContainer	insideContainer;
	private		MiFishEyeTransform	fishEyeTransform;



	public				MiFishEyeView(MiEditor editor)
		{
		setupMiFishEyeView();
		setViewTarget(editor);
		}
	protected	void		setupMiFishEyeView()
		{
		setSize(new MiSize(200, 200));
		insideContainer = new MiVisibleContainer();
		insideContainer.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		insideContainer.setContainerLayoutSpec(MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS);
		insideContainer.setInsetMargins(new MiMargins(8));
		insideContainer.setBackgroundColor(MiColorManager.lightGray);
		appendPart(insideContainer);

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);

		view = new MiEditor();
		insideContainer.appendPart(view);

		reference = new MiReference();
		fishEyeTransform = new MiFishEyeTransform(view);
		reference.setTransform(fishEyeTransform);
		reference.setUseScaleTransform(false);
		reference.setUseTranslateTransform(false);
		view.appendPart(reference);

		rect = new MiRectangle();
		rect.setColor("red");
		rect.setContextCursor(Mi_MOVE_CURSOR);
		//rect.setWriteMode(Mi_XOR_WRITEMODE);
		view.appendPart(rect);
		view.appendEventHandler(new MiIZoomAroundMouse());
		view.appendEventHandler(new MiIPan());
		view.setDeviceBounds(new MiBounds(0,0,200,200));
		//view.setPreferredSize(new MiSize(200,200));
		view.setMinimumSize(new MiSize(100,100));
		}

	public		void		updateView()
		{
/*
		if (!isVisible())
			return;
*/

		MiBounds targetUniverseBounds = target.getUniverseBounds();
		view.setUniverseBounds(targetUniverseBounds);

		view.setBackgroundColor(target.getBackgroundColor());

		rect.setBounds(fishEyeTransform.getWorldBounds(new MiBounds()));
		}

	public		void		setViewTarget(MiEditor targetEditor)
		{
		target = targetEditor;
		target.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			reference.appendPart(target.getPart(i));
		updateView();
		view.setWorldBounds(view.getUniverseBounds());
		}
	public		MiEditor	getViewTarget()
		{
		return(target);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_EDITOR_VIEWPORT_CHANGED_ACTION))
			{
			updateView();
			}
		return(true);
		}
	}
class MiFishEyeTransform extends MiGeneralTransform
	{
	private		double		fishBowlWidthAsPercentageOfUniverse	= 0.3;
	private		double		fishBowlHeightAsPercentageOfUniverse	= 0.3;
	private		MiScale		scaleFactor				= new MiScale();
	private		MiVector	dTranslation				= new MiVector();
	private		MiVector	wTranslation				= new MiVector();
	private		MiScale		origScaleFactor				= new MiScale();
	private		MiVector	origDTranslation			= new MiVector();
	private		MiVector	origWTranslation			= new MiVector();
	private		MiBounds 	universeBounds 				= new MiBounds();
	private final static double	PI_OVER_2 				= Math.PI/2;
	private		MiEditor	editor;
	private		MiPoint		tmpWPoint				= new MiPoint();

	private		MiPoint		fishBowlCenter				= new MiPoint();
	private		double		fishBowlCurvature			= 1.0;


	public				MiFishEyeTransform(MiEditor editor)
		{
		this.editor = editor;
		}
	public		MiBounds	getWorldBounds(MiBounds b)
		{
		editor.getUniverseBounds(universeBounds);
		b.setBounds(universeBounds);
		b.setWidth(universeBounds.getWidth() * fishBowlWidthAsPercentageOfUniverse);
		b.setHeight(universeBounds.getHeight() * fishBowlHeightAsPercentageOfUniverse);
		return(b);
		}

					/**------------------------------------------------------
			 		 * Returns whether this transform has constant 
			 		 * scaleFactors and translations across the world space.
			 		 * In effect this returns true if scale = f(x,y) or
					 * translation = f(x,y)
					 * @return		true if transform depends on
					 *			location.
			 		 *------------------------------------------------------*/
	public		boolean		isPositionDependent()
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Convert the coordinate in world space to it's
					 * corresponding coordinate in device space.
					 * @param wpt		the world space coordinate
					 * @param dpt		the (returned) device space coordinate
					 *------------------------------------------------------*/
	public		void		wtod(MiPoint wpt, MiDevicePoint dpt)
		{
		modifyTransformTranslations(wpt);
		super.wtod(wpt, dpt);
		restoreTransform();
		}
					/**------------------------------------------------------
					 * Convert the coordinate in world space to it's
					 * corresponding coordinate in device space.
					 * @param wpt		the world space point
					 * @param dpt		the (returned) device space point
					 *------------------------------------------------------*/
	public		 void		wtod(MiPoint wpt, MiPoint dpt)
		{
		modifyTransformTranslations(wpt);
		super.wtod(wpt, dpt);
		restoreTransform();
		}
					/**------------------------------------------------------
			 		 * Convert the vector at the given location in world 
					 * space to the corresponding vector in device space.
			 		 * @param wPoint	the world space coordinate
			 		 * @param wVector	the world space vector
			 		 * @param dVector	the (returned) device space vector
			 		 *------------------------------------------------------*/
	public		void		wtod(MiPoint wPoint, MiVector wVector, MiVector dVector)
		{
		modifyTransformScales(wPoint);
		super.wtod(wPoint, wVector, dVector);
		restoreTransform();
		}
					/**------------------------------------------------------
			 		 * Convert the vector at the given location in world 
					 * space to the corresponding vector in device space.
			 		 * @param wPoint	the world space coordinate
			 		 * @param wVector	the world space vector
			 		 * @param dVector	the (returned) device space vector
			 		 *------------------------------------------------------*/
	public		void		wtod(MiPoint wPoint, MiVector wVector, MiDeviceVector dVector)
		{
		modifyTransformScales(wPoint);
		super.wtod(wPoint, wVector, dVector);
		restoreTransform();
		}
					/**------------------------------------------------------
			 		 * Convert the coordinate in device space to it's
			 		 * corresponding coordinate in world space.
			 		 * @param dpt		the device space coordinate
			 		 * @param wpt		the (returned) world space coordinate
			 		 *------------------------------------------------------*/
	public		 void		dtow(MiPoint dpt, MiPoint wpt)
		{
		modifyTransformTranslations(dpt);
		super.dtow(dpt, wpt);
		restoreTransform();
		}
					/**------------------------------------------------------
			 		 * Convert the vector at the given location in device 
					 * space to the corresponding vector in world space.
			 		 * @param dPoint	the device space coordinate
			 		 * @param dVector	the device space vector
			 		 * @param wVector	the (returned) world space vector
			 		 *------------------------------------------------------*/
	public		void		dtow(MiPoint dPoint, MiVector dVector, MiVector wVector)
		{
		modifyTransformScales(dPoint);
		super.dtow(dPoint, dVector, wVector);
		restoreTransform();
		}

					/**------------------------------------------------------
			 		 * Convert the vector at the given location in device 
					 * space to the corresponding vector in world space.
			 		 * @param dPoint	the device space coordinate
			 		 * @param dVector	the device space vector
			 		 * @param wVector	the (returned) world space vector
			 		 *------------------------------------------------------*/
	public		void		dtow(MiDevicePoint dPoint, MiDeviceVector dVector, MiVector wVector)
		{
		tmpWPoint.x = dPoint.x;
		tmpWPoint.y = dPoint.y;
		modifyTransformScales(tmpWPoint);
		super.dtow(dPoint, dVector, wVector);
		restoreTransform();
		}

	protected	void		restoreTransform()
		{
		setScale(origScaleFactor);
		setWorldTranslation(origWTranslation);
		setDeviceTranslation(origDTranslation);
		}
	protected	void		modifyTransformScales(MiPoint wPt)
		{
		getScale(scaleFactor);
		origScaleFactor.copy(scaleFactor);

		editor.getUniverseBounds(universeBounds);
		fishBowlCenter.x = universeBounds.getCenterX();
		fishBowlCenter.y = universeBounds.getCenterY();

		MiDistance distanceFromCenterX = wPt.x - fishBowlCenter.x;
		MiDistance radiusOfFishBowlX 
			= universeBounds.getWidth() * fishBowlWidthAsPercentageOfUniverse/2;
		MiDistance distanceFromCenterY = wPt.y - fishBowlCenter.y;
		MiDistance radiusOfFishBowlY 
			= universeBounds.getHeight() * fishBowlHeightAsPercentageOfUniverse/2;
		
		MiDistance maxDistanceFromCenterX = fishBowlCenter.x - universeBounds.getXmin();
		if (maxDistanceFromCenterX < universeBounds.getXmax() - fishBowlCenter.x)
			maxDistanceFromCenterX = universeBounds.getXmax() - fishBowlCenter.x;
			
		MiDistance maxDistanceFromCenterY = fishBowlCenter.y - universeBounds.getYmin();
		if (maxDistanceFromCenterY < universeBounds.getYmax() - fishBowlCenter.y)
			maxDistanceFromCenterY = universeBounds.getYmax() - fishBowlCenter.y;

		scaleFactor.x = scaleFactor.x * fishBowlCurvature
			* Math.cos(distanceFromCenterX/maxDistanceFromCenterX * PI_OVER_2) + 0.01;
		scaleFactor.y = scaleFactor.y * fishBowlCurvature
			* Math.cos(distanceFromCenterY/maxDistanceFromCenterY * PI_OVER_2) + 0.01;

		setScale(scaleFactor);
origScaleFactor.set(1.0, 1.0);
setScale(origScaleFactor); // TEST
		}
	protected	void		modifyTransformTranslations(MiPoint wPt)
		{
		getWorldTranslation(wTranslation);
		origWTranslation.copy(wTranslation);
		getDeviceTranslation(dTranslation);
		origDTranslation.copy(dTranslation);

		editor.getUniverseBounds(universeBounds);
		fishBowlCenter.x = universeBounds.getCenterX();
		fishBowlCenter.y = universeBounds.getCenterY();

		MiDistance distanceFromCenterX = wPt.x - fishBowlCenter.x;
		MiDistance radiusOfFishBowlX 
			= universeBounds.getWidth() * fishBowlWidthAsPercentageOfUniverse/2;
		MiDistance distanceFromCenterY = wPt.y - fishBowlCenter.y;
		MiDistance radiusOfFishBowlY 
			= universeBounds.getHeight() * fishBowlHeightAsPercentageOfUniverse/2;
		
		MiDistance maxDistanceFromCenterX = fishBowlCenter.x - universeBounds.getXmin();
		if (maxDistanceFromCenterX < universeBounds.getXmax() - fishBowlCenter.x)
			maxDistanceFromCenterX = universeBounds.getXmax() - fishBowlCenter.x;
			
		MiDistance maxDistanceFromCenterY = fishBowlCenter.y - universeBounds.getYmin();
		if (maxDistanceFromCenterY < universeBounds.getYmax() - fishBowlCenter.y)
			maxDistanceFromCenterY = universeBounds.getYmax() - fishBowlCenter.y;

		wTranslation.x += distanceFromCenterX
			- Math.sin(distanceFromCenterX/maxDistanceFromCenterX * PI_OVER_2)
			* radiusOfFishBowlX;

		wTranslation.y += distanceFromCenterY
			- Math.sin(distanceFromCenterY/maxDistanceFromCenterY * PI_OVER_2)
			* radiusOfFishBowlY;

		setWorldTranslation(wTranslation);
		}
					/**------------------------------------------------------
					 * Prints information about this transform.
					 *------------------------------------------------------*/
	public		String		toString()
		{
		MiScale tmpScale = new MiScale();
		return(MiDebug.getMicaClassName(this)
				+ ": <device: " + getDeviceTranslation(new MiVector()) + ">"
				+ "<scale: [Position Dependant - Dynamically calculated]>"
				+ "<world: [Position Dependant - Dynamically Calculated]>\n"
				+ "<curvature: " + fishBowlCurvature + ">"
				+ "<center: " + fishBowlCenter + ">");
		}
	}

class MiMagnifierView extends MiWidget implements MiiActionHandler
	{
	private		boolean			trackMouse		= true;
	private		double			magnification		= 10.0;
	private		MiEditor		view;
	private		MiEditor		target;
	private		MiVisibleContainer	insideContainer;
	private		MiBounds		tmpBounds		= new MiBounds();



	public				MiMagnifierView(MiEditor editor)
		{
		setupMiMagnifierView();
		setViewTarget(editor);
		}
	protected	void		setupMiMagnifierView()
		{
		setSize(new MiSize(200, 200));
		insideContainer = new MiVisibleContainer();
		insideContainer.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		insideContainer.setContainerLayoutSpec(MAKE_CONTAINER_SAME_SIZE_AS_CONTENTS);
		insideContainer.setInsetMargins(new MiMargins(8));
		insideContainer.setBackgroundColor(MiColorManager.lightGray);
		appendPart(insideContainer);

		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);

		view = new MiEditor();
		insideContainer.appendPart(view);
		view.appendEventHandler(new MiIZoomAroundMouse());
		view.setDeviceBounds(new MiBounds(0,0,200,200));
		//view.setPreferredSize(new MiSize(200,200));
		view.setMinimumSize(new MiSize(100,100));
		}

	public		void		updateView()
		{
/*
		if (!isVisible())
			return;
*/

		MiBounds targetUniverseBounds = target.getUniverseBounds();
		view.setUniverseBounds(targetUniverseBounds);

		if (trackMouse)
			{
			MiBounds b = view.getWorldBounds(tmpBounds);
			b.setCenter(target.getMousePosition().getCenter());
			view.setWorldBounds(b);
			}

		view.setBackgroundColor(target.getBackgroundColor());
		}

	public		void		setViewTarget(MiEditor targetEditor)
		{
		target = targetEditor;
		target.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		target.appendEventHandler(new MiMouseLocationDisplayMonitor2(this));
		for (int i = 0; i < target.getNumberOfParts(); ++i)
			view.appendPart(target.getPart(i));
		updateView();

		MiBounds b = view.getUniverseBounds(tmpBounds);
		b.setWidth(b.getWidth()/magnification);
		b.setHeight(b.getHeight()/magnification);
		view.setWorldBounds(b);
		}
	public		MiEditor	getViewTarget()
		{
		return(target);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_EDITOR_VIEWPORT_CHANGED_ACTION))
			{
			updateView();
			}
		return(true);
		}
	public		void		setPropertyValue(String name, String value)
		{
		if ((name.equals(MiMouseLocationDisplayMonitor2.Mi_MOUSE_X_NAME))
			|| (name.equals(MiMouseLocationDisplayMonitor2.Mi_MOUSE_Y_NAME)))
			{
			updateView();
			}
		}
	}
class MiMagnifierLens extends MiWidget implements MiiActionHandler
	{
	private		boolean			initialized;
	private		boolean			transparent		= false;
	private		double			magnification		= 10.0;
	private		MiEditor		view;
	private		MiEditor		target;
	private		MiVisibleContainer	insideContainer;
	private		MiBounds		tmpBounds		= new MiBounds();



	public				MiMagnifierLens(MiEditor editor)
		{
		setupMiMagnifierLens();
		setViewTarget(editor);
		}
	protected	void		setupMiMagnifierLens()
		{
		setBackgroundColor(Mi_TRANSPARENT_COLOR);
		setSize(new MiSize(200, 200));

		MiColumnLayout layout = new MiColumnLayout();
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementIndex(0);
		setLayout(layout);

		view = new MiEditor();
		appendPart(view);
		view.setDeviceBounds(new MiBounds(0,0,200,200));
		view.setPreferredSize(new MiSize(200,200));
		view.setMinimumSize(new MiSize(100,100));
		if (transparent)
			view.setIsOpaqueRectangle(false);
		}

	public		void		updateView()
		{
/*
		if (!isVisible())
			return;
*/
		if ((!initialized) && (getContainingWindow() != null))
			{
			getContainingWindow().appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
			initialized = true;
			}

		if (!transparent)
			view.setBackgroundColor(target.getBackgroundColor());

		view.setUniverseBounds(view.getDeviceBounds(tmpBounds));
		view.setWorldBounds(view.getDeviceBounds(tmpBounds));
		}

	public		void		setViewTarget(MiEditor targetEditor)
		{
		target = targetEditor;
		//target.appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
		if ((!initialized) && (getContainingWindow() != null))
			{
			getContainingWindow().appendActionHandler(this, Mi_EDITOR_VIEWPORT_CHANGED_ACTION);
			initialized = true;
			}
		if (!transparent)
			{
			for (int i = 0; i < target.getNumberOfParts(); ++i)
				view.appendPart(target.getPart(i));
			}
		updateView();

		view.setUniverseBounds(view.getDeviceBounds(tmpBounds));
		view.setWorldBounds(view.getDeviceBounds(tmpBounds));
		}
	public		MiEditor	getViewTarget()
		{
		return(target);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_EDITOR_VIEWPORT_CHANGED_ACTION))
			{
			updateView();
			}
		return(true);
		}
	public		void		setPropertyValue(String name, String value)
		{
		if ((name.equals(MiMouseLocationDisplayMonitor2.Mi_MOUSE_X_NAME))
			|| (name.equals(MiMouseLocationDisplayMonitor2.Mi_MOUSE_Y_NAME)))
			{
			updateView();
			}
		}
	}


