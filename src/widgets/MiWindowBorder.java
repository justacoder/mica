
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
import java.awt.Color; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiWindowBorder extends MiWidget implements MiiCommandHandler, MiiCommandNames, MiiActionHandler
	{
	public static final String	Mi_BORDER_TITLE_NAME_CHANGE_ACTION_NAME= "borderTitleChange";
	public static final int		Mi_BORDER_TITLE_NAME_CHANGE_ACTION	
								= MiActionManager.registerAction(
								Mi_BORDER_TITLE_NAME_CHANGE_ACTION_NAME);
	public static	int		ICONIZE_ICON_WIDTH	= 3;
	public static	int		ICONIZE_ICON_HEIGHT	= 3;
	public static	int		FULL_SCREEN_ICON_WIDTH	= 10;
	public static	int		FULL_SCREEN_ICON_HEIGHT	= 10;
	public static	int		CLOSE_ICON_WIDTH	= 8;
	public static	int		CLOSE_ICON_HEIGHT	= 8;
	public static	int		BOX_WIDTH		= 22;
	public static	int		BOX_HEIGHT		= 22;


	private		MiColumnLayout	outerLayout;
	private		MiVisibleContainer main;
	private		MiRowLayout	titleBar;
	private		MiWidget	titleRow;
	private		MiTextField	titleLabel;
	private		MiWidget	closeBox;
	private		MiPart		closeIcon;
	private		MiButton	iconizeBox;
	private		MiPart		iconizeIcon;
	private		MiButton	fullScreenBox;
	private		MiPart		fullScreenIcon;
	private		MiVisibleContainer	windowBox;
	private		MiPart		subject;
	private		Color		borderColor;
	private		Color		mouseFocusBorderColor	= MiColorManager.getColor("violet");
	private		Color		keyFocusBorderColor	= MiColorManager.getColor("violet");
	private		Color		mouseFocusTitleBGColor	= MiColorManager.getColor("violet");
	private		Color		keyFocusTitleBGColor	= MiColorManager.getColor("lightBlue");
	private		MiDistance 	borderWidth		= 10;
	private		boolean		mouseInsideWindow;
	private		MiBounds	tmpBounds		= new MiBounds();



	public				MiWindowBorder(MiPart subject, String title)
		{
		this.subject = subject;
		subject.appendActionHandler(this, Mi_GEOMETRY_CHANGE_ACTION);
		subject.setMovable(false);
		subject.setSelectable(false);
		setBackgroundColor(getSelectedBackgroundColor());
		borderColor = getBackgroundColor();

		setMouseFocusAttributes(getMouseFocusAttributes().setHasBorderHilite(false));
		setKeyboardFocusAttributes(getKeyboardFocusAttributes().setHasBorderHilite(false));

		setBorderLook(Mi_RAISED_BORDER_LOOK);
		setInsetMargins(borderWidth);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);
		outerLayout = layout;

		main = new MiVisibleContainer();
		main.setInsetMargins(2);
		main.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		main.setSelectable(false);
		layout = new MiColumnLayout();
		layout.setInsetMargins(0);
		layout.setAlleyHSpacing(0);
		layout.setAlleyVSpacing(1);
		layout.setElementHSizing(Mi_EXPAND_TO_FILL);
		layout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		main.setLayout(layout);
		appendPart(main);

		titleRow = new MiWidget();
		MiRowLayout titleRowLayout = new MiRowLayout();
		titleRow.setLayout(titleRowLayout);
		titleRow.setBorderLook(Mi_RAISED_BORDER_LOOK);
		titleRow.setInsetMargins(0);
		
		titleLabel = new MiTextField(title);
		titleLabel.setNumDisplayedColumns(-1);
		titleLabel.setBorderLook(Mi_NONE);
		titleLabel.setInsetMargins(4);
		titleLabel.setIsEditable(false);
		titleLabel.setDisplaysFocusBorder(false);
		titleLabel.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		titleLabel.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);

		titleRow.appendPart(titleLabel);

		//closeIcon = new MiText("X");
		closeIcon = new MiContainer();
		MiPart line1 = new MiLine(-CLOSE_ICON_WIDTH/2,-CLOSE_ICON_WIDTH/2,
						CLOSE_ICON_WIDTH/2, CLOSE_ICON_WIDTH/2);
		closeIcon.appendPart(line1);
		MiPart line2 = new MiLine(CLOSE_ICON_WIDTH/2, -CLOSE_ICON_WIDTH/2, 
						-CLOSE_ICON_WIDTH/2, CLOSE_ICON_WIDTH/2);
		closeIcon.appendPart(line2);
		MiPart line3 = new MiLine(-CLOSE_ICON_WIDTH/2,-CLOSE_ICON_WIDTH/2,
						CLOSE_ICON_WIDTH/2, CLOSE_ICON_WIDTH/2);
		closeIcon.appendPart(line3);
		line1.setBorderLook(Mi_RAISED_BORDER_LOOK);
		line1.setColor(MiColorManager.white);
		line2.setBorderLook(Mi_RAISED_BORDER_LOOK);
		line2.setColor(MiColorManager.white);
		line3.setColor(MiColorManager.white);

		iconizeIcon = new MiRectangle();
		iconizeIcon.setBorderLook(Mi_RAISED_BORDER_LOOK);
		iconizeIcon.setWidth(ICONIZE_ICON_WIDTH);
		iconizeIcon.setHeight(ICONIZE_ICON_HEIGHT);

		fullScreenIcon = new MiRectangle();
		fullScreenIcon.setBorderLook(Mi_RAISED_BORDER_LOOK);
		fullScreenIcon.setWidth(FULL_SCREEN_ICON_WIDTH);
		fullScreenIcon.setHeight(FULL_SCREEN_ICON_HEIGHT);

		closeBox = new MiWidget();	// MiPushButton();
		closeBox.setDisplaysFocusBorder(false);
		closeBox.setAcceptingMouseFocus(false);
		closeBox.setBorderLook(Mi_RAISED_BORDER_LOOK);
		closeBox.setVisibleContainerAutomaticLayoutEnabled(false);
		closeBox.setPreferredSize(new MiSize(BOX_HEIGHT, BOX_WIDTH));
		closeBox.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		closeBox.appendCommandHandler(this, Mi_CLOSE_COMMAND_NAME, new MiEvent(Mi_LEFT_MOUSE_DBLCLICK_EVENT));
		closeBox.appendPart(closeIcon);

		iconizeBox = new MiPushButton();
		iconizeBox.setDisplaysFocusBorder(false);
		iconizeBox.setAcceptingMouseFocus(false);
		iconizeBox.setVisibleContainerAutomaticLayoutEnabled(false);
		iconizeBox.setPreferredSize(new MiSize(BOX_HEIGHT, BOX_WIDTH));
		iconizeBox.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		iconizeBox.appendCommandHandler(this, Mi_ICONIFY_COMMAND_NAME, Mi_ACTIVATED_ACTION);
		iconizeBox.appendPart(iconizeIcon);

		fullScreenBox = new MiPushButton();
		fullScreenBox.setDisplaysFocusBorder(false);
		fullScreenBox.setAcceptingMouseFocus(false);
		fullScreenBox.setVisibleContainerAutomaticLayoutEnabled(false);
		fullScreenBox.setPreferredSize(new MiSize(BOX_HEIGHT, BOX_WIDTH));
		fullScreenBox.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		fullScreenBox.appendCommandHandler(this, Mi_TOGGLE_FULLSCREEN_COMMAND_NAME, Mi_ACTIVATED_ACTION);
		fullScreenBox.appendPart(fullScreenIcon);

		MiRowLayout rowLayout = new MiRowLayout();
		titleBar = rowLayout;
		rowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		rowLayout.setUniqueElementIndex(1);
		rowLayout.setAlleySpacing(1);
		rowLayout.setInsetMargins(0);

		titleBar.setBorderLook(Mi_RAISED_BORDER_LOOK);
		titleBar.appendPart(closeBox);
		titleBar.appendPart(titleRow);
		titleBar.appendPart(iconizeBox);
		titleBar.appendPart(fullScreenBox);

		main.appendPart(titleBar);
		main.appendPart(subject);

		makeCornerResizeHandles();
		updateBorderColors();

		setAcceptingMouseFocus(true);
		setAcceptingKeyboardFocus(true);
		appendEventHandler(new MiWindowBorderEventHandler(this));
		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
	public		void		setTitle(String title)
		{
		titleLabel.setValue(title);
		}
	public		String		getTitle()
		{
		return(titleLabel.getValue());
		}
	public		MiTextField	getTitleLabel()
		{
		return(titleLabel);
		}
	public		MiPart		getIconifyButton()
		{
		return(iconizeBox);
		}
	public		MiPart		getCloseButton()
		{
		return(closeBox);
		}
	public		MiPart		getFullScreenButton()
		{
		return(fullScreenBox);
		}
	public		MiPart		getSubjectContainer()
		{
		return(main);
		}

	public		MiPart		getSubject()
		{
		return(subject);
		}

	public		void		setBorderWidth(MiDistance width)
		{
		borderWidth = width;
		setInsetMargins(borderWidth);
		}
	public		MiDistance	getBorderWidth()
		{
		return(borderWidth);
		}

	public		boolean		processAction(MiiAction action)
		{
		if ((action.getActionSource() == subject)
			&& (action.hasActionType(Mi_GEOMETRY_CHANGE_ACTION)))
			{
			// When drag window, do not want this - cause windowBorder, as dragged
			// object, disables invalidLAyoutNotifications and this
			// then genreates warnings... 
			main.invalidateLayout();
			}
		else if ((action.getActionSource() == titleLabel)
			&& (action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION)))
			{
			subject.dispatchAction(Mi_BORDER_TITLE_NAME_CHANGE_ACTION);
			}
		return(true);
		}

	public		void		setMouseFocusBorderColor(Color c)
		{
		mouseFocusBorderColor = c;
		}
	public		void		updateBorderColors()
		{
		if (mouseInsideWindow)
			{
			titleRow.setBackgroundColor(mouseFocusTitleBGColor);
			closeBox.setBackgroundColor(mouseFocusBorderColor);
			iconizeBox.setBackgroundColor(mouseFocusBorderColor);
			fullScreenBox.setBackgroundColor(mouseFocusBorderColor);
			setBackgroundColor(mouseFocusBorderColor);
			}
		else
			{
			titleRow.setBackgroundColor(borderColor);
			closeBox.setBackgroundColor(borderColor);
			iconizeBox.setBackgroundColor(borderColor);
			fullScreenBox.setBackgroundColor(borderColor);
			setBackgroundColor(borderColor);
			}
		if (hasKeyboardFocus())
			{
			titleRow.setBackgroundColor(keyFocusTitleBGColor);
			closeBox.setBackgroundColor(keyFocusBorderColor);
			iconizeBox.setBackgroundColor(keyFocusBorderColor);
			fullScreenBox.setBackgroundColor(keyFocusBorderColor);
			setBackgroundColor(keyFocusBorderColor);
			}
		}
	public		void		setKeyboardFocus(boolean flag)
		{
		super.setKeyboardFocus(flag);
		updateBorderColors();
		}
	public		int		dispatchEvent(MiEvent event)
		{
		if (event.getType() == Mi_MOUSE_ENTER_EVENT)
			{
			mouseInsideWindow = true;
			updateBorderColors();
			}
		else if (event.getType() == Mi_MOUSE_EXIT_EVENT)
			{
			mouseInsideWindow = false;
			updateBorderColors();
			}
		return(super.dispatchEvent(event));
		}

	public		void		setFullScreenTitleBarLook(boolean flag)
		{
		if (flag)
			{
			fullScreenIcon.setBorderLook(Mi_INDENTED_BORDER_LOOK);
			}
		else
			{
			fullScreenIcon.setBorderLook(Mi_RAISED_BORDER_LOOK);
			}
		}

	public		void		processCommand(String cmd)
		{
		if (subject instanceof MiInternalWindow)
			{
			((MiInternalWindow )subject).processCommand(cmd);
			}
		else if (cmd.equals(Mi_CLOSE_COMMAND_NAME))
			{
			setVisible(false);
			}
		else if (cmd.equals(Mi_ICONIFY_COMMAND_NAME))
			{
			}
		else if (cmd.equals(Mi_TOGGLE_FULLSCREEN_COMMAND_NAME))
			{
			}
		}

	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		subject.scale(center, scale);
		}

	public		void		calcPreferredSize(MiSize size)
		{
		super.calcPreferredSize(size);
		//setMinimumWidth(size.width);
		//setMinimumHeight(size.height);
		MiBounds outerBounds = getBounds(tmpBounds);
		if (size.width < outerBounds.getWidth())
			size.setWidth(outerBounds.getWidth());
		if (size.height < outerBounds.getHeight())
			size.setHeight(outerBounds.getHeight());
		}
	protected	void		makeCornerResizeHandles()
		{
		int Mi_CORNER_SIZE = 20;
		MiCoord[] xPoints = {0.0, 1.0, 1.0, 0.75, 0.25, 0.25, 0.0, 0.0 };
		MiCoord[] yPoints = {0.0, 0.0, 0.25, 0.25, 0.75, 1.0, 1.0, 0.0 };
/*
		MiCoord[] xPoints = {0.0, 1.0, 1.0, 0.65, 0.35, 0.35, 0.0, 0.0 };
		MiCoord[] yPoints = {0.0, 0.0, 0.35, 0.35, 0.65, 1.0, 1.0, 0.0 };
*/
		MiPolygon handle;
		MiPolyConstraint layout = new MiPolyConstraint();
		outerLayout.setLayout(layout);

		//MiBounds maxBounds = subject.getParent().getInnerBounds();
		MiBounds maxBounds = new MiBounds(
			Mi_MIN_COORD_VALUE, Mi_MIN_COORD_VALUE, Mi_MAX_COORD_VALUE, Mi_MAX_COORD_VALUE);
		// --------------
		// LowerLeft
		// --------------
		handle = makeHandle(xPoints, yPoints, Mi_CORNER_SIZE, Mi_CORNER_SIZE);
		handle.setContextCursor(Mi_SW_RESIZE_CURSOR);
		handle.appendEventHandler(new MiIResizeHandleEventHandler(handle, this, maxBounds, Mi_LOWER_LEFT_LOCATION));
		layout.appendConstraint(new MiRelativeLocationConstraint(
				handle, MiRelativeLocationConstraint.SAME_SW_OUTSIDE_CORNER, this, 0));
		appendAttachment(handle);

		// --------------
		// LowerRight
		// --------------
		handle = makeHandle(xPoints, yPoints, -Mi_CORNER_SIZE, Mi_CORNER_SIZE);
		handle.setContextCursor(Mi_SE_RESIZE_CURSOR);
		handle.appendEventHandler(new MiIResizeHandleEventHandler(handle, this, maxBounds, Mi_LOWER_RIGHT_LOCATION));
		layout.appendConstraint(new MiRelativeLocationConstraint(
				handle, MiRelativeLocationConstraint.SAME_SE_OUTSIDE_CORNER, this, 0));
		appendAttachment(handle);

		// --------------
		// UpperLeft
		// --------------
		handle = makeHandle(xPoints, yPoints, Mi_CORNER_SIZE, -Mi_CORNER_SIZE);
		handle.setContextCursor(Mi_NW_RESIZE_CURSOR);
		handle.appendEventHandler(new MiIResizeHandleEventHandler(handle, this, maxBounds, Mi_UPPER_LEFT_LOCATION));
		layout.appendConstraint(new MiRelativeLocationConstraint(
				handle, MiRelativeLocationConstraint.SAME_NW_OUTSIDE_CORNER, this, 0));
		appendAttachment(handle);

		// --------------
		// UpperRight
		// --------------
		handle = makeHandle(xPoints, yPoints, -Mi_CORNER_SIZE, -Mi_CORNER_SIZE);
		handle.setContextCursor(Mi_NE_RESIZE_CURSOR);
		handle.appendEventHandler(new MiIResizeHandleEventHandler(handle, this, maxBounds, Mi_UPPER_RIGHT_LOCATION));
		layout.appendConstraint(new MiRelativeLocationConstraint(
				handle, MiRelativeLocationConstraint.SAME_NE_OUTSIDE_CORNER, this, 0));
		appendAttachment(handle);

		}
	private		MiPolygon	makeHandle(MiCoord[] xPoints, MiCoord[] yPoints, double xSize, double ySize)
		{
		MiPolygon poly = new MiPolygon();
		if ((((xSize < 0) && (ySize > 0)) || (xSize > 0) && (ySize < 0)))
			{
			//Maintain left-handedness
			for (int i = xPoints.length - 1; i >= 0; --i)
				{
				poly.appendPoint(new MiPoint(xPoints[i] * xSize, yPoints[i] * ySize));
				}
			}
		else
			{
			for (int i = 0; i < xPoints.length; ++i)
				{
				poly.appendPoint(new MiPoint(xPoints[i] * xSize, yPoints[i] * ySize));
				}
			}
		poly.setBorderLook(Mi_RAISED_BORDER_LOOK);
		poly.setBackgroundColor(MiColorManager.darkWhite);
		return(poly);
		}
	public		int		getContextCursor(MiBounds area)	
		{
		if (area == null)
			return(super.getContextCursor(area));

		MiBounds b = getBounds();
		MiMargins border = getTotalMargins();
		MiPoint pt = area.getCenter();

		if ((pt.x > b.getXmax() - border.right) && (pt.x < b.getXmax()))
			return(Mi_E_RESIZE_CURSOR);
		if ((pt.x < b.getXmin() + border.left) && (pt.x > b.getXmin()))
			return(Mi_W_RESIZE_CURSOR);
		if ((pt.y > b.getYmax() - border.top) && (pt.y < b.getYmax()))
			return(Mi_N_RESIZE_CURSOR);
		if ((pt.y < b.getYmin() + border.bottom) && (pt.y > b.getYmin()))
			return(Mi_S_RESIZE_CURSOR);

		return(super.getContextCursor(area));
		}
	}
class MiWindowBorderEventHandler extends MiEventHandler implements MiiTypes
	{
	private static final int 	NOTHING		= -1;
	private		int 		whatAspectToResize	= NOTHING;
	private		MiWindowBorder	windowBorder;	
	private		MiSize		minSize		= new MiSize();
	private		MiBounds	maxBounds 	= new MiBounds(
								Mi_MIN_COORD_VALUE, 
								Mi_MIN_COORD_VALUE, 
								Mi_MAX_COORD_VALUE, 
								Mi_MAX_COORD_VALUE);





	public				MiWindowBorderEventHandler(MiWindowBorder windowBorder)
		{
		this.windowBorder = windowBorder;
		//maxBounds = windowBorder.getWindow().getParent().getInnerBounds();

		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		}

	public		int		processCommand()
		{
		if (isCommand(Mi_PICK_UP_COMMAND_NAME))
			{
			// If already resizing...
			if (whatAspectToResize != NOTHING)
				return(Mi_PROPOGATE_EVENT);

			if (windowBorder.getTitleLabel().getContainer(0).pick(event.worldMouseFootPrint))
				{
				whatAspectToResize = Mi_CENTER_LOCATION;
				}
			else
				{
				MiBounds b = windowBorder.getBounds();
				MiMargins border = windowBorder.getTotalMargins();
				MiPoint pt = event.worldMouseFootPrint.getCenter();
				if ((pt.x > b.getXmax() - border.right) && (pt.x < b.getXmax()))
					whatAspectToResize = Mi_RIGHT_LOCATION;
				else if ((pt.x < b.getXmin() + border.left) && (pt.x > b.getXmin()))
					whatAspectToResize = Mi_LEFT_LOCATION;
				else if ((pt.y > b.getYmax() - border.top) && (pt.y < b.getYmax()))
					whatAspectToResize = Mi_TOP_LOCATION;
				else if ((pt.y < b.getYmin() + border.bottom) && (pt.y > b.getYmin()))
					whatAspectToResize = Mi_BOTTOM_LOCATION;
				else
					{
					return(Mi_PROPOGATE_EVENT);
					}
				}
			event.editor.prependGrabEventHandler(this);
			return(drag());
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (whatAspectToResize != NOTHING)
				return(drag());
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (whatAspectToResize != NOTHING)
				{
				event.editor.removeGrabEventHandler(this);
				whatAspectToResize = NOTHING;
				return(Mi_CONSUME_EVENT);
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	protected	int		drag()
		{
		MiEditor	editor = event.editor;
		boolean 	modifiedDelta;

		MiVector delta = new MiVector(event.worldVector);

		if (whatAspectToResize == Mi_CENTER_LOCATION)
			modifiedDelta = editor.autopanForMovingObj(windowBorder.getBounds(), delta);
		else
			modifiedDelta = editor.autopanForMovingObj(event.worldMouseFootPrint, delta);

		if (delta.isZero())
			return(Mi_CONSUME_EVENT);

		MiPoint location = new MiPoint(event.worldPt);
		if (modifiedDelta)
			{
			location.x += delta.x - event.worldVector.x;
			location.y += delta.y - event.worldVector.y;
			}

		windowBorder.getMinimumSize(minSize);
		MiDistance minWidth = minSize.getWidth();
		MiDistance minHeight = minSize.getHeight();
		MiBounds b = windowBorder.getBounds();
		if (!delta.isZero())
			{
			switch (whatAspectToResize)
				{
				case Mi_LEFT_LOCATION		:
					if (b.getXmax() - location.x < minWidth)
						location.x = b.getXmax() - minWidth;
					b.setXmin(location.x);
					break;
				case Mi_RIGHT_LOCATION		:
					if (location.x - b.getXmin() < minWidth)
						location.x = b.getXmin() + minWidth;
					b.setXmax(location.x);
					break;
				case Mi_BOTTOM_LOCATION		:
					if (b.getYmax() - location.y < minHeight)
						location.y = b.getYmax() - minHeight;
					b.setYmin(location.y);
					break;
				case Mi_TOP_LOCATION		:
					if (location.y - b.getYmin() < minHeight)
						location.y = b.getYmin() + minHeight;
					b.setYmax(location.y);
					break;
				case Mi_CENTER_LOCATION		:
				default				:
					if (b.confineInsideContainer(maxBounds, delta))
						windowBorder.translate(delta);
					return(Mi_CONSUME_EVENT);
				}
			b.confineInsideContainer(maxBounds);
			windowBorder.setBounds(b);
			}
		return(Mi_CONSUME_EVENT);
		}
	}
class MiIResizeHandleEventHandler extends MiEventHandler implements MiiTypes
	{
	private static final int 	NOTHING		= 0;
	private static final int 	RESIZE_SUBJECT	= 1;
	private		MiPart		subject;
	private		MiPart		handle;
	private		MiBounds	containerBounds;
	private		MiSize		minSize		= new MiSize();
	private		int		whatAspectToResize;
	private		int 		dragging	= NOTHING;



	public				MiIResizeHandleEventHandler(MiPart handle, MiPart subject, MiBounds containerBounds, int whatAspectToResize)
		{
		this.handle = handle;
		this.subject = subject;
		this.containerBounds = containerBounds;
		this.whatAspectToResize = whatAspectToResize;

		addEventToCommandTranslation(Mi_PICK_UP_COMMAND_NAME, Mi_LEFT_MOUSE_START_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DRAG_COMMAND_NAME, Mi_LEFT_MOUSE_DRAG_EVENT, 0, 0);
		addEventToCommandTranslation(Mi_DROP_COMMAND_NAME, Mi_LEFT_MOUSE_UP_EVENT, 0, 0);
		}

	public		int		processCommand()
		{
		if (isCommand(Mi_PICK_UP_COMMAND_NAME))
			{
			if (dragging == NOTHING)
				{
				if (handle.pick(event.worldMouseFootPrint))
					dragging = RESIZE_SUBJECT;
				else
					return(Mi_PROPOGATE_EVENT);
				//subject.setInvalidLayoutNotificationsEnabled(false);
				event.editor.prependGrabEventHandler(this);
				return(Mi_CONSUME_EVENT);
				}
			}
		else if (isCommand(Mi_DRAG_COMMAND_NAME))
			{
			if (dragging != NOTHING)
				{
				MiEditor editor = event.editor;
				MiVector delta = new MiVector(event.worldVector);
				boolean modifiedDelta = 
					editor.autopanForMovingObj(event.worldMouseFootPrint, delta);
				MiPoint location = new MiPoint(event.worldPt);
				if (modifiedDelta)
					{
					location.x += delta.x - event.worldVector.x;
					location.y += delta.y - event.worldVector.y;
					}
				subject.getMinimumSize(minSize);
				MiDistance minWidth = minSize.getWidth();
				MiDistance minHeight = minSize.getHeight();

				MiBounds b = subject.getBounds();
				if (!delta.isZero())
					{
					switch (whatAspectToResize)
						{
						case Mi_LEFT_LOCATION		:
							if (b.getXmax() - location.x < minWidth)
								location.x = b.getXmax() - minWidth;
							b.setXmin(location.x);
							break;
						case Mi_RIGHT_LOCATION		:
							if (location.x - b.getXmin() < minWidth)
								location.x = b.getXmin() + minWidth;
							b.setXmax(location.x);
							break;
						case Mi_BOTTOM_LOCATION		:
							if (b.getYmax() - location.y < minHeight)
								location.y = b.getYmax() - minHeight;
							b.setYmin(location.y);
							break;
						case Mi_TOP_LOCATION		:
							if (location.y - b.getYmin() < minHeight)
								location.y = b.getYmin() + minHeight;
							b.setYmax(location.y);
							break;
						case Mi_LOWER_LEFT_LOCATION	:
							if (b.getXmax() - location.x < minWidth)
								location.x = b.getXmax() - minWidth;
							b.setXmin(location.x);
							if (b.getYmax() - location.y < minHeight)
								location.y = b.getYmax() - minHeight;
							b.setYmin(location.y);
							break;
						case Mi_LOWER_RIGHT_LOCATION	:
							if (location.x - b.getXmin() < minWidth)
								location.x = b.getXmin() + minWidth;
							b.setXmax(location.x);
							if (b.getYmax() - location.y < minHeight)
								location.y = b.getYmax() - minHeight;
							b.setYmin(location.y);
							break;
						case Mi_UPPER_LEFT_LOCATION	:
							if (b.getXmax() - location.x < minWidth)
								location.x = b.getXmax() - minWidth;
							b.setXmin(location.x);
							if (location.y - b.getYmin() < minHeight)
								location.y = b.getYmin() + minHeight;
							b.setYmax(location.y);
							break;
						case Mi_UPPER_RIGHT_LOCATION	:
							if (location.x - b.getXmin() < minWidth)
								location.x = b.getXmin() + minWidth;
							b.setXmax(location.x);
							if (location.y - b.getYmin() < minHeight)
								location.y = b.getYmin() + minHeight;
							b.setYmax(location.y);
							break;
						case Mi_CENTER_LOCATION		:
						default				:
							if (b.confineInsideContainer(containerBounds, delta))
								subject.translate(delta);
							return(Mi_CONSUME_EVENT);
						}
					}
				b.confineInsideContainer(containerBounds);
				subject.setBounds(b);
				return(Mi_CONSUME_EVENT);
				}
			}
		else if (isCommand(Mi_DROP_COMMAND_NAME))
			{
			if (dragging != NOTHING)
				{
				event.editor.removeGrabEventHandler(this);
				dragging = NOTHING;
				//subject.setInvalidLayoutNotificationsEnabled(true);
				return(Mi_CONSUME_EVENT);
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}
class MiWindowManager
	{
	// 
	private		MiEditor	rootWindow;
	private		MiParts	openWindows;
	}
