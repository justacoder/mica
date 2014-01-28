
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTextField extends MiWidget implements MiiActionHandler, MiiActionTypes
	{
	public static final	int			Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT = -1;
	public static final	int			Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT = -2;

	private	static		MiPropertyDescriptions	propertyDescriptions;
	public static final	String			Mi_TEXTFIELD_PROTOTYPE_CLASS_NAME 
								= "Mi_TEXTFIELD_PROTOTYPE_CLASS_NAME";
	private static		MiTextField		prototype;
	private static		MiBounds		tmpBounds			= new MiBounds();
	private			MiIFlowEditorEventHandler textEditingEventHandler;
	private			MiText			text;
	private			MiBounds		lastDrawnTextBounds		= new MiBounds();
	private			int			maxNumDisplayedRows		= Integer.MAX_VALUE;
	private			int			maxNumDisplayedColumns		= Integer.MAX_VALUE;
	private			int			prefNumberOfDisplayedColumns	= Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT;
	private			boolean			editable			= true;
	private			boolean			editableWhenSensitive		= true;
	private			boolean			useMaxInsteadOfAveFontSize;
							// Since last dispatch of value_changed_action
	private			boolean			valueHasChanged;
	private			boolean			disableActionHandling;


	public				MiTextField()
		{
		this(null);
		}
	public				MiTextField(boolean useStandardMinimumSize)
		{
		this(null, useStandardMinimumSize);
		}

	public				MiTextField(String str)
		{
		this(str, false);
		}
	public				MiTextField(String str, boolean useStandardMinimumSize)
		{
		setText(new MiText(str));
		boolean isThisClass = MiDebug.getMicaClassName(this).equals("MiTextField");
		if ((isThisClass) && (prototype != null))
			{
			copy(prototype);
			refreshLookAndFeel();
			applyCustomLookAndFeel();
			return;
			}
		setIsEditable(true);
		setElementHJustification(Mi_LEFT_JUSTIFIED);
		setVisibleContainerAutomaticLayoutEnabled(false);
		setNumDisplayedRows(1);
		if (useStandardMinimumSize)
			{
			setMinimumSize(new MiSize(100, 10));
			//setNumDisplayedColumns(Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT); // This setting has problems with proportioanlly spaced fonts
			}
		setAcceptingKeyboardFocus(true);
		setAcceptingEnterKeyFocus(true);
		setDisplaysFocusBorder(true);
		setBorderLook(Mi_INLINED_INDENTED_BORDER_LOOK);
		setInsetMargins(new MiMargins(4, 3, 4, 3));
		setNormalColor(getToolkit().getTextSensitiveColor());
		setInSensitiveColor(getToolkit().getTextInSensitiveColor());
		setNormalBackgroundColor(getToolkit().getTextFieldEditableBGColor());
		setInSensitiveBackgroundColor(getToolkit().getTextFieldInEditableBGColor());
		appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);

		refreshLookAndFeel();
		applyCustomLookAndFeel();
		}
					/**------------------------------------------------------
	 				 * Creates a new widget from the prototype. This is the
					 * factory pattern implementation for this widget. If the
					 * prototype is null, then the default contructor is used.
					 * @return 		the new widget
					 * @see 		#setPrototype
					 *------------------------------------------------------*/
	public static	MiWidget	create()
		{
		if (prototype == null)
			return(new MiWidget());

		return((MiWidget )prototype.deepCopy());
		}
	public		void		setText(MiText t)
		{
		if (text != null)
			{
			text.removeActionHandlers(this);
			removePart(text);
			}
		text = t;
		text.setNumDisplayedColumns(prefNumberOfDisplayedColumns);
		text.setAcceptingKeyboardFocus(false);
		text.setSelectable(false);
		// To fix probs with deepCopy... 
		text.setCopyable(false);
		text.setCopyableAsPartOfCopyable(false);
		text.setInvalidLayoutNotificationsEnabled(false);
		text.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		text.appendActionHandler(this, 
			Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE | Mi_COMMIT_ACTION_PHASE);
		appendPart(text);
		}
	public		MiText		getText()
		{
		return(text);
		}
	public		void		setValue(String t)
		{
		if (t == null)
			t = "";
		if (t.equals(text.getText()))
			return;

		text.setText(t);
		if ((getNumDisplayedColumns() == Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT) 
			|| (getNumDisplayedRows() == Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT))
			{
			updateFieldSize(true);
			}
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		valueHasChanged = false;
		}
	public		String		getValue()
		{
		return(text.getText());
		}


	// -1 (Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT) => dynamiclly sized to text contents
	// Need a setPreferredNumDisplayedColumns...
	public		void		setNumDisplayedColumns(int num)
		{
		prefNumberOfDisplayedColumns = num;
		text.setNumDisplayedColumns(num);
		setMinimumSize(null);
		updateFieldSize();
		}
	public		int		getNumDisplayedColumns()	
		{ 
		//return(prefNumberOfDisplayedColumns);
		return(text.getNumDisplayedColumns());
		}

	// -1 (Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT) => dynamiclly sized to text contents
	public		void		setNumDisplayedRows(int num)
		{
		text.setNumDisplayedRows(num);
		updateFieldSize();
		}
	public		int		getNumDisplayedRows()		
		{ 
		return(text.getNumDisplayedRows());
		}

	public		void		setMaxNumDisplayedRows(int numRows)
		{
		maxNumDisplayedRows = numRows;
		}
	public		int		getMaxNumDisplayedRows()
		{
		return(maxNumDisplayedRows);
		}

	public		void		setMaxNumDisplayedColumns(int numCols)
		{
		maxNumDisplayedColumns = numCols;
		}
	public		int		getMaxNumDisplayedColumns()
		{
		return(maxNumDisplayedColumns);
		}

	public		void		setMaxNumCharacters(int max)
		{
		text.setMaxNumCharacters(max);
		}
	public		int		getMaxNumCharacters()
		{
		return(text.getMaxNumCharacters());
		}
					// Default = true, otherwise linewrap
	public		void		setWordWrapEnabled(boolean flag)
		{
		text.setWordWrapEnabled(flag);
		}
	public		boolean		getWordWrapEnabled()
		{
		return(text.getWordWrapEnabled());
		}
	
	
	public		void		setEchoCharacter(char ch)
		{
		text.setEchoCharacter(ch);
		}
	public		char		getEchoCharacter()
		{
		return(text.getEchoCharacter());
		}
	public		void		setCursorPosition(int pos)
		{
		if ((textEditingEventHandler != null) && (hasKeyboardFocus()))
			textEditingEventHandler.setCursorPosition(pos);
		}
	public		int		getCursorPosition()
		{
		if ((textEditingEventHandler != null) && (hasKeyboardFocus()))
			return(textEditingEventHandler.getCursorPosition());
		return(-1);
		}

	public		void		setKeyboardFocus(boolean flag)
		{
		if (flag == hasKeyboardFocus())
			return;

		super.setKeyboardFocus(flag);
		if (hasKeyboardFocus() && editable && isSensitive())
			{
			if (textEditingEventHandler == null)
				{
				textEditingEventHandler = new MiIFlowEditorEventHandler(makeTextFieldEditor(text));
				text.setInteractiveEditor(textEditingEventHandler);
				}
			appendEventHandler(textEditingEventHandler);
			textEditingEventHandler.setKeyboardFocus(true);
			}
		else if (textEditingEventHandler != null) // 5-15-2002 editable && isSensitive())
			{
			removeEventHandler(textEditingEventHandler);
			textEditingEventHandler.setKeyboardFocus(false);
			text.setFirstDisplayedColumn(0);
			}
		invalidateArea();
		}

	public		MiIFlowEditorEventHandler	getInteractiveEditor()
		{
		if (textEditingEventHandler == null)
			{
			textEditingEventHandler = new MiIFlowEditorEventHandler(makeTextFieldEditor(text));
			}
		return(textEditingEventHandler);
		}
	protected	MiTextFieldEditor	makeTextFieldEditor(MiText text)
		{
		return(new MiTextFieldEditor(text));
		}

	public		void		setSensitive(boolean flag)
		{
		if ((!flag) && (flag != isSensitive()))
			{
			editableWhenSensitive = editable;
			}
		super.setSensitive(flag);
		text.setSensitive(flag);
		if (flag)
			{
			setIsEditable(editableWhenSensitive);
			}
		else
			{
			setIsEditable(flag);
			}
		}
	public		boolean		isEditable()
		{
		return(editable);
		}
	public		void		setIsEditable(boolean flag)
		{
		editable = flag;
		if (flag)
			{
			setAcceptingKeyboardFocus(true);
			text.setContextCursor(Mi_TEXT_CURSOR);
			setContextCursor(Mi_TEXT_CURSOR);
			}
		else
			{
			setAcceptingKeyboardFocus(false);
			text.setContextCursor(Mi_DEFAULT_CURSOR);
			setContextCursor(Mi_DEFAULT_CURSOR);
			}
		}
	public		void		setModificationsAreUndoable(boolean flag)
		{
		text.setModificationsAreUndoable(flag);
		}
	public		boolean		getModificationsAreUndoable()
		{
		return(text.getModificationsAreUndoable());
		}
	public		void		setUseMaxInsteadOfAveFontSize(boolean flag)
		{
		useMaxInsteadOfAveFontSize = flag;
		}
	public		boolean		getUseMaxInsteadOfAveFontSize()
		{
		return(useMaxInsteadOfAveFontSize);
		}
					/**------------------------------------------------------
	 				 * Sets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @param p 		the new prototype
					 * @see 		#getPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	void		setPrototype(MiTextField p)
		{
		prototype = p;
		}
					/**------------------------------------------------------
	 				 * Gets the prototype that is to be copied when the #create
					 * method is called and to have it's attributes and handlers
					 * copied whenever any widget of this type is created.
					 * @return  		the prototype
					 * @see 		#setPrototype
					 * @see 		#create
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public static	MiWidget	getPrototype()
		{
		return(prototype);
		}
					/**------------------------------------------------------
	 				 * Creates a prototype from the class named by the
					 * Mi_TEXTFIELD_PROTOTYPE_CLASS_NAME system property,
					 * if specified.
					 *------------------------------------------------------*/
	static	{
		String prototypeClassName = MiSystem.getProperty(Mi_TEXTFIELD_PROTOTYPE_CLASS_NAME);
		if (prototypeClassName != null)
			{
			prototype = (MiTextField )Utility.makeInstanceOfClass(prototypeClassName);
			}
		}

	public		void		setAttributes(MiAttributes atts)
		{
		MiAttributes oldAtts = getAttributes();
		super.setAttributes(atts);
		if (text == null)
			return;

		boolean updateFieldSize = false;
		if (oldAtts.getFont() != getFont())
			{
			text.setFont(getFont()); 
			updateFieldSize = true; 	
			}
		if (oldAtts.getColor() != getColor())
			text.setColor(getColor()); 

		if (updateFieldSize)
			updateFieldSize(); 	
		}
	public		void		setElementHJustification(int justification)
		{
		if (text != null)
			{
			text.setFontHorizontalJustification(justification);
			}
		super.setFontHorizontalJustification(justification);
		super.setElementHJustification(justification);
		}

	public		void		setFontHJustification(int justification)
		{
		if (text != null)
			{
			text.setFontHorizontalJustification(justification);
			}
		super.setFontHorizontalJustification(justification);
		super.setElementHJustification(justification);
		}

	// FIX: Keep these ????
/*
	public		boolean		isFontBold()		{ return(text.isFontBold());	}
	public		boolean		isFontItalic()		{ return(text.isFontItalic());	}
	public		int		getFontPointSize()	{ return(text.getFontPointSize());	}
*/

	public		String		toString()
		{
		String textToDisplay = (text != null ? text.getText() : "null");
		if ((textToDisplay != null) && (textToDisplay.length() > 256))
			{
			textToDisplay = textToDisplay.substring(0, 252) + "...";
			}
		return(super.toString() + "." + textToDisplay
			+ "(displayed chars=" + getNumDisplayedColumns() + ")"
			+ "(max chars=" + getMaxNumCharacters() + ")"
			+ (editable ? "" : "(NOT EDITABLE)"));
		}


	public			void		render(MiRenderer renderer)
		{
		if ((prefNumberOfDisplayedColumns == Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT) && (text != null))
			{
			// Keep this textfield exactly the same size as the text, no
			// matter what font it is using at whatever zoom level
			tmpBounds.copy(text.getBoundsAtDrawnScale(renderer));
			if ((!tmpBounds.isReversed()) &&
				((!Utility.equals(lastDrawnTextBounds.getWidth(), tmpBounds.getWidth()))
				|| (!Utility.equals(lastDrawnTextBounds.getHeight(), tmpBounds.getHeight()))))
				{
				lastDrawnTextBounds.copy(tmpBounds);
				updateFieldSize(true);
				if ((getNumberOfContainers() > 0) 
					&& ((getContainer(0).getLayout() != null) 
						&& (getContainer(0).getLayout().determinesPreferredAndMinimumSizes()))
					|| ((getContainer(0) instanceof MiiLayout)
						&& (((MiiLayout )getContainer(0)).determinesPreferredAndMinimumSizes())))
					{
					// Return if our change in size will force a 
					// re-layout and re-render of this in near future.
					if (!renderer.isPrinterRenderer())
						{
						return;
						}
					}
				}
			}

		int justification = getElementHJustification();
		MiBounds innerBounds = getInnerBounds(tmpBounds);
		if (justification == Mi_LEFT_JUSTIFIED)
			{
			text.setXmin(innerBounds.getXmin());
			}
		else if (justification == Mi_CENTER_JUSTIFIED)
			{
			text.setCenterX(innerBounds.getCenterX());
			}
		else if (justification == Mi_RIGHT_JUSTIFIED)
			{
			text.setXmax(innerBounds.getXmax());
			}

		if (getNumDisplayedRows() != 1)
			text.setYmax(innerBounds.getYmax());
		else
			text.setYmin(innerBounds.getYmin());

		super.render(renderer);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (disableActionHandling)
			{
			return(false);
			}
//MiDebug.println(this + "processAction: " +action);
		if (action.hasActionType(Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			if (!dispatchActionRequest(Mi_TEXT_CHANGE_ACTION))
				{
				action.veto();
				return(false);
				}
			}
		else if (action.hasActionType(Mi_TEXT_CHANGE_ACTION))
			{
			if ((getNumDisplayedColumns() == Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT) 
				|| (getNumDisplayedRows() == Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT))
				{
				updateFieldSize(true);
				}
			valueHasChanged = true;
			dispatchAction(Mi_TEXT_CHANGE_ACTION);
			}
		else if (action.hasActionType(Mi_ENTER_KEY_ACTION))
			{
			dispatchAction(Mi_ENTER_KEY_ACTION);
			if (valueHasChanged)
				{
				dispatchAction(Mi_VALUE_CHANGED_ACTION);
				valueHasChanged = false;
				}
			}
		else if (action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION))
			{
			// In case we is getting this particular action of a COPY of this MiTextField...
			if ((action.getActionSource() != this) && (action.getActionSource() instanceof MiTextField))
				{
				// ... foward the action to the real recipient...
				return(((MiTextField )action.getActionSource()).processAction(action));
				}
			if (valueHasChanged)
				{
				dispatchAction(Mi_VALUE_CHANGED_ACTION);
				valueHasChanged = false;
				}
			}
		return(true);
		}

	protected	void		calcMinimumSize(MiSize size)
		{
		size.reset();

		if (getNumDisplayedColumns() != Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT)
			{
			if (getNumDisplayedColumns() == Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT)
				{
				if ((text.getText() == null) || (text.getText().length() == 0))
					{
					if (useMaxInsteadOfAveFontSize)
						size.setWidth(getFont().getMaxCharWidth());
					else
						size.setWidth(getFont().getAverageCharWidth());
					}
				else if (text.getText().length() > maxNumDisplayedColumns)
					{
					if (useMaxInsteadOfAveFontSize)
						size.setWidth(getFont().getMaxCharWidth() * maxNumDisplayedColumns);
					else
						size.setWidth(getFont().getAverageCharWidth() * maxNumDisplayedColumns);
					}
				else
					{
					if ((lastDrawnTextBounds.isReversed())
						|| ((lastDrawnTextBounds.getWidth() <= text.getWidth())
							&& (lastDrawnTextBounds.getWidth() > text.getWidth() - 10)))
						{
						size.setWidth(text.getWidth());
						}
					else
						{
						size.setWidth(lastDrawnTextBounds.getWidth());
						}
					}
				
				}
			else
				{
				int columns = prefNumberOfDisplayedColumns;
				if (columns < 1)
					columns = 1;

				if (useMaxInsteadOfAveFontSize)
					size.setWidth(getFont().getMaxCharWidth() * columns);
				else
					size.setWidth(getFont().getAverageCharWidth() * columns);
				}
			}
			

		int numRows = getNumDisplayedRows();
		if ((numRows < 1) && (text.getText() != null))
			{
			if ((lastDrawnTextBounds.isReversed())
				|| ((lastDrawnTextBounds.getHeight() <= text.getHeight())
					&& (lastDrawnTextBounds.getHeight() > text.getHeight() - 10)))
				{
				size.setHeight(text.getHeight());
				}
			else
				{
				size.setHeight(lastDrawnTextBounds.getHeight());
				}
			}
		else
			{
			if (numRows < 1)
				numRows = 1;
			if (numRows == 1)
				size.setHeight(getFont().getMaxCharHeight());
			else
				size.setHeight((getFont().getHeight() + text.getLineSpacing()) * numRows);
			}

		size.addMargins(getTotalMargins());
		}
	protected	void		calcPreferredSize(MiSize size)
		{
		calcMinimumSize(size);
		}

					/**------------------------------------------------------
	 				 * Rotates this MiPart the given number of radians about
					 * the given point.
	 				 * @param center	the center of rotation
	 				 * @param radians	the angle to rotate
					 *------------------------------------------------------*/
	protected	void		doRotate(MiPoint center, double radians)
		{
		if (Utility.isZero(radians))
			return;

		lastDrawnTextBounds.reverse();
		text.doRotate(center, radians);

		// Should probably go into MiWidget...
		invalidateArea();
		double sinR = Math.sin(radians);
		double cosR = Math.cos(radians);
		MiCoord x = getCenterX() - center.x;
		MiCoord y = getCenterY() - center.y;
		setCenter(-sinR * y + cosR * x + center.x, sinR * x + cosR * y + center.y);

		double oldAngle = getRotation();
		while (oldAngle < 0)
			{
			oldAngle += Math.PI * 2;
			}
		while (oldAngle > Math.PI * 2)
			{
			oldAngle -= Math.PI * 2;
			}

		double resultantAngle = radians + getRotation();
		while (resultantAngle < 0)
			{
			resultantAngle += Math.PI * 2;
			}
		while (resultantAngle > Math.PI * 2)
			{
			resultantAngle -= Math.PI * 2;
			}

		int justification = getElementHJustification();
		if ( (((oldAngle <= Math.PI/2) || (resultantAngle >= 3 * Math.PI/2))
			&& ((resultantAngle > Math.PI/2) && (resultantAngle < 3 * Math.PI/2)))
		|| (((oldAngle >= Math.PI/2) && (resultantAngle <= 3 * Math.PI/2))
			&& ((resultantAngle < Math.PI/2) || (resultantAngle > 3 * Math.PI/2))) )
			{
			if (justification == Mi_LEFT_JUSTIFIED)
				{
				setElementHJustification(Mi_RIGHT_JUSTIFIED);
				}
			else if (justification == Mi_CENTER_JUSTIFIED)
				{
				}
			else if (justification == Mi_RIGHT_JUSTIFIED)
				{
				setElementHJustification(Mi_LEFT_JUSTIFIED);
				}
			}

		refreshBounds();
		invalidateLayout();

		// MiContainer does not do anything except with subparts...super.doRotate(center, radians);
		}
					/**------------------------------------------------------
	 				 * Flips this MiPart about the axis specified by the given 
					 * number of radians about the given point.
	 				 * @param center	the center of flip
	 				 * @param radians	the angle of the axis of reflection
					 *------------------------------------------------------*/
	protected	void		doFlip(MiPoint center, double radians)
		{
		while (radians >= Math.PI*2)
			radians -= Math.PI*2;
		while (radians < 0)
			radians += Math.PI*2;

		invalidateArea();
		if (radians == 0)
			{
			// Swap Y
			setCenter(getCenterX(), center.y - (getCenterY() - center.y));
			}
		else if (radians == Math.PI/2)
			{
			// Swap X
			setCenter(center.x - (getCenterX() - center.x), getCenterY());
			}
		else
			{
			doRotate(center, -radians);
			// Swap Y
			setCenter(getCenterX(), center.y - (getCenterY() - center.y));
			doRotate(center, radians);
			}

		int justification = getElementHJustification();
		if (justification == Mi_LEFT_JUSTIFIED)
			{
			setElementHJustification(Mi_RIGHT_JUSTIFIED);
			}
		else if (justification == Mi_CENTER_JUSTIFIED)
			{
			}
		else if (justification == Mi_RIGHT_JUSTIFIED)
			{
			setElementHJustification(Mi_LEFT_JUSTIFIED);
			}
		refreshBounds();
		invalidateLayout();
		}

					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @param source	the part to copy
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiTextField obj 		= (MiTextField )source;

		editable			= obj.editable;
		editableWhenSensitive		= obj.editableWhenSensitive;
		prefNumberOfDisplayedColumns	= obj.prefNumberOfDisplayedColumns;

		// Don't want new text to be calling this textfield to tell it that it 
		// has changed and thereby changing this textfield's bounds
		obj.disableActionHandling = true;

		setText((MiText )obj.text.copy());

		obj.disableActionHandling = false;

		getText().removeActionHandlers(obj);

		removeActionHandlers(obj);
		appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);

		textEditingEventHandler = null;
		}
					// To fix probs with deepCopy... kinda...
	public		void		appendPart(MiPart obj)
		{
		if ((obj != text) && (obj instanceof MiText))
			setText((MiText )obj);
		else
			super.appendPart(obj);
		}
	protected	void		doLayout()
		{
		super.doLayout();
		MiBounds innerBounds = getInnerBounds(tmpBounds);
		text.setXmin(innerBounds.getXmin());
		if (getNumDisplayedRows() != 1)
			text.setYmax(innerBounds.getYmax());
		else
			text.setYmin(innerBounds.getYmin());

		if ((getNumDisplayedColumns() != Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT)
			&& (getNumDisplayedColumns() != Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT))
			{
			MiBounds b = getBounds(tmpBounds);
			b.subtractMargins(getTotalMargins());
			int fontCharWidth;
			if (useMaxInsteadOfAveFontSize)
				fontCharWidth = getFont().getMaxCharWidth();
			else
				fontCharWidth = getFont().getAverageCharWidth();

			int num = (int )(b.getWidth()/fontCharWidth);
			if (num > getNumDisplayedColumns())
				text.setNumDisplayedColumns(num);
			else if (num < getNumDisplayedColumns() - 1)
				text.setNumDisplayedColumns(num);
			}
// caused numRows to grow for no reason... 12-13-2003
		// Auto-resize texfield number of rows to suit text it displays
		if ((getNumDisplayedRows() != Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT) 
			&& (getNumDisplayedRows() != Mi_TEXT_FIELD_SIZE_UNRELATED_TO_TEXT))
			{
			//MiBounds b = getBounds(tmpBounds);
			//b.subtractMargins(getTotalMargins());
			//int fontCharHeight = getFont().getMaxCharHeight();
			// int num = (int )(b.getHeight()/fontCharHeight);

			int num = Utility.numOccurancesOf(text.getValue(), '\n') + 1;
			if (num <= getMaxNumDisplayedRows())
				{
				if (num > getNumDisplayedRows())
					text.setNumDisplayedRows(num);
				else if (num < getNumDisplayedRows() - 1)
					text.setNumDisplayedRows(num);
				}
			}
		}
	public		void		scale(MiPoint center, MiScale scale)
		{
		if (scale.isIdentity())
			return;

//		if (scaleChangeModifiesFontPointSize)
//			{
//			setFontPointSize((int )(getFontPointSize() * (scale.x + scale.y)/2));
//			}
//		else

/*
			{
			//int justification = getFontHorizontalJustification();
			int justification = getElementHJustification();
			if (justification == Mi_LEFT_JUSTIFIED)
				{
				MiCoord baseLineY = getBaselineY();
				translate((getXmin() - center.x) * scale.x + center.x - getXmin(), 
					(baseLineY - center.y) * scale.y + center.y - baseLineY);
				}
			else if (justification == Mi_CENTER_JUSTIFIED)
				{
				MiCoord baseLineY = getBaselineY();
				translate((getCenterX() - center.x) * scale.x + center.x - getCenterX(),
					(baseLineY - center.y) * scale.y + center.y - baseLineY);
				}
			else if (justification == Mi_RIGHT_JUSTIFIED)
				{
				MiCoord baseLineY = getBaselineY();
				translate((getXmax() - center.x) * scale.x + center.x - getXmax(),
					(baseLineY - center.y) * scale.y + center.y - baseLineY);
				}
			setBaselineY(getYmin() + getInsetMargins().getBottom());
			}
*/
		if (text != null)
			{
			doLayout();
			text.scale(center,scale);
			setBaselineY(text.getBaselineY());
			setXmin(text.getXmin() - getInsetMargins().getLeft());
			}

		updateFieldSize();
		doLayout();
		}


	public		void		setBaselineY(MiCoord y)
		{
		text.setBaselineY(y);
		setYmin(text.getYmin() - getInsetMargins().getBottom());
		text.setBaselineY(y);
		}
	public		MiCoord		getBaselineY()
		{
		return(text.getBaselineY());
		}

	protected	void		updateFieldSize()
		{
		updateFieldSize(false);
		}
	protected	void		updateFieldSize(boolean justify)
		{
		if (getNumDisplayedColumns() != Mi_TEXT_FIELD_SIZE_SAME_OR_LARGER_THAN_TEXT)
			{
			invalidateLayout();
			return;
			}

		MiSize size = MiSize.newSize();
		MiBounds b = MiBounds.newBounds();

		calcMinimumSize(size);
		b.setSize(size);

		MiBounds currentBounds = getBounds(tmpBounds);
		b.translateXminTo(currentBounds.getXmin());
		b.translateYminTo(currentBounds.getYmin());

		// If NOT keep text anchored at left, even tho, within the overall bounds, we draw using justification
		if (justify)
			{
			// Justifiy the bounds w/respect to current bounds
			int justification = getElementHJustification();
			if (justification == Mi_LEFT_JUSTIFIED)
				{
				b.translateXminTo(currentBounds.getXmin());
				}
			else if (justification == Mi_CENTER_JUSTIFIED)
				{
				b.setCenterX(currentBounds.getCenterX());
				}
			else if (justification == Mi_RIGHT_JUSTIFIED)
				{
				b.translateXmaxTo(currentBounds.getXmax());
				}
			}

		setBounds(b);
		invalidateLayout();

		//getBounds(b);
		//text.setPageWidth(b.getWidth());

		MiSize.freeSize(size);
		MiBounds.freeBounds(b);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * This method supports the use of property names of form:
					 *   selected.backgroundColor
					 * in order to specify the values of attributes for this
					 * widget on a state by state basis.
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_NUM_DISPLAYED_CHARACTERS_NAME))
			{
			setNumDisplayedColumns(Utility.toInteger(value));
			}
		else if (name.equalsIgnoreCase(Mi_TEXT_BASELINE_Y_ATT_NAME))
			{
			setBaselineY(Utility.toDouble(value));
			}
		else if (name.equalsIgnoreCase(Mi_EDITABLE_NAME))
			{
			setIsEditable(Utility.toBoolean(value));
			}
		else if ((name.equalsIgnoreCase(Mi_FONT_HORIZONTAL_JUSTIFICATION_ATT_NAME))
			|| (name.equalsIgnoreCase(Mi_FONT_VERTICAL_JUSTIFICATION_ATT_NAME)))
			{
			text.setPropertyValue(name, value);
			super.setPropertyValue(name, value);
			}
		else
			{
			super.setPropertyValue(name, value);
			}
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		if (name.equalsIgnoreCase(Mi_NUM_DISPLAYED_CHARACTERS_NAME))
			{
			return("" + getNumDisplayedColumns());
			}
		else if (name.equalsIgnoreCase(Mi_TEXT_BASELINE_Y_ATT_NAME))
			{
			return(Utility.toShortString(getBaselineY()));
			}
		else if (name.equalsIgnoreCase(Mi_EDITABLE_NAME))
			{
			return("" + isEditable());
			}
		else 
			{
			return(super.getPropertyValue(name));
			}
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = new MiPropertyDescriptions("MiTextField");

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_EDITABLE_NAME, Mi_BOOLEAN_TYPE, "true"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_NUM_DISPLAYED_CHARACTERS_NAME, Mi_POSITIVE_INTEGER_TYPE, "12"));
		propertyDescriptions.addElement(
			new MiPropertyDescription(
			Mi_TEXT_BASELINE_Y_ATT_NAME, Mi_DOUBLE_TYPE, "0"));

		propertyDescriptions = new MiPropertyDescriptions(propertyDescriptions);
		propertyDescriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());

		return(propertyDescriptions);
		}

	}

