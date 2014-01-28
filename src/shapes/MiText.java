
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
import java.awt.Color; 
import java.awt.Frame; 
import java.util.Vector; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiText extends MiPart
	{
	private	static	MiPropertyDescriptions	propertyDescriptions;
	private		MiIFlowEditorEventHandler	textEditingEventHandler;
	private		String		text;
	private		String		previousText;
	private		String		displayedText;
	private		String		specifiedText;
	private		char		echoChar;
	private		int		underlineLetterIndex	= -1;
	private		boolean		editable		= false;
	private 	int		selectionStart		= -1;
	private 	int		selectionEnd		= -1;
	private 	MiTextLineData	lines[];
	private 	MiBounds	selectedAreas[];
	private 	MiDistance	lineSpacing		= 2;
	private 	MiDistance	pageWidth		= 0;
	private		int		numDisplayedColumns 	= -1;
	private		int		firstDisplayedColumn 	= 0;
	private		int		numDisplayedRows 	= 1;
	private		int		maxNumCharacters	= -1;
	private		MiBounds	minimumBounds		= new MiBounds();
	private	static	MiBounds	tmpBounds		= new MiBounds();
	private	static	MiBounds	tmpBounds2		= new MiBounds();
	private	static	MiSize		tmpSize			= new MiSize();
	private	static	MiScale		tmpScale		= new MiScale();
	private	static	MiPoint		tmpPoint		= new MiPoint();
	private	static	MiVector	dDescent		= new MiVector();
	private	static	MiVector	wDescent		= new MiVector();
	private static	MiModifyTextCommand undoableModifyTextCommand 	= new MiModifyTextCommand();
	private		Color		textInsensitiveColor	= MiColorManager.gray; // MiColorManager.lightGray;
	private		Color		textSensitiveSelectedColor= MiColorManager.white;
	private		Color		textSensitiveSelectedBGColor= Color.blue;
	private		boolean		fontChanged		= true;
	private		boolean		modificationsAreUndoable;
	private		boolean		inSetAttributes;
	private		MiFont		scaledFont;
	private		MiFont		lastDrawnFont;
	private		MiScale		lastDrawnScale		= new MiScale();
	private		MiAttributes	underlineAndStrikeOutAttributes;
	private		boolean		wordWrapEnabled		= true;
	private		boolean		needToFormatCharStarts;
	private static	boolean		scaleChangeModifiesFontPointSize;
	private		boolean		mustDoubleClickToEdit;
	private		boolean		baselineYDoesntAssumeEditorIsOneToOneDeviceToWorld;
	private		boolean		selectEntireTextAsPartInEditor;
	private		MiBounds	boundsAtDrawnScale	= new MiBounds();
	private		MiAttributes	preInSensitiveAttributes;



	public				MiText()
		{
		this(0, 0, null);
		}

	public				MiText(String str)
		{
		this(0, 0, str);
		}

	public				MiText(MiCoord x, MiCoord y, String str)
		{
		//lineWidthDependant = false;
		underlineAndStrikeOutAttributes = getAttributes();
		scaledFont = getFont();
		fontChanged = true;
		setText(str);
		setCenter(x, y);
		textSensitiveSelectedColor = MiToolkit.getAttributes(
			MiiToolkit.Mi_TOOLKIT_TEXT_SELECTED_ATTRIBUTES).getColor();
		textSensitiveSelectedBGColor = MiToolkit.getAttributes(
			MiiToolkit.Mi_TOOLKIT_TEXT_SELECTED_ATTRIBUTES).getBackgroundColor();
		textInsensitiveColor = MiToolkit.getAttributes(
			MiiToolkit.Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES).getColor();
		}

	public		void		setText(String str) 	
		{ 
		if ((str == null) || (str.equals("\n")))
			str = "";
		if ((specifiedText != null) && (specifiedText.equals(str)))
			return;

		String oldText = text;
		text = str;
		if (!dispatchActionRequest(MiiActionTypes.Mi_TEXT_CHANGE_ACTION))
			{
			text = oldText;
			return;
			}

		boundsAtDrawnScale.reverse();

		specifiedText = text;
		text = MiSystem.getProperty(text, text);

		if (text.length() < numDisplayedColumns)
			firstDisplayedColumn = 0;


		displayedText = text;
		if ((echoChar != '\0') && (text.length() > 0))
			{
			StringBuffer tmp = new StringBuffer();
			for (int i = 0; i < text.length(); ++i)
				tmp.append(echoChar);

			displayedText = tmp.toString();
			}

		invalidateArea();
		reFormat();
		needToFormatCharStarts = true;
		reFormatCharStarts();
		invalidateArea(getBounds(tmpBounds2));
		dispatchAction(MiiActionTypes.Mi_TEXT_CHANGE_ACTION);
		}

	public		String		getText() 		
		{ 
		return(text); 
		}
	public		void		setValue(String s) 		
		{ 
		setText(s);
		}
	public		String		getValue() 		
		{ 
		return(text); 
		}
					// For lines w/o a linefeed
	public		void		setEchoCharacter(char ch)
		{
		echoChar = ch;
		}
	public		char		getEchoCharacter()
		{
		return(echoChar);
		}

	public		void		setModificationsAreUndoable(boolean flag)
		{
		modificationsAreUndoable = flag;
		}
	public		boolean		getModificationsAreUndoable()
		{
		return(modificationsAreUndoable);
		}
	public static	void		setScaleChangeModifiesFontPointSize(boolean flag)
		{
		scaleChangeModifiesFontPointSize = flag;
		}
	public static	boolean		getScaleChangeModifiesFontPointSize()
		{
		return(scaleChangeModifiesFontPointSize);
		}

					// Default = true, otherwise linewrap
	public		void		setWordWrapEnabled(boolean flag)
		{
		wordWrapEnabled = flag;
		reFormat();
		}
	public		boolean		getWordWrapEnabled()
		{
		return(wordWrapEnabled);
		}

	public		void		setIsEditable(boolean flag)	
		{ 
		editable = flag; 	
		if (flag)
			{
			setAcceptingKeyboardFocus(true);
			setContextCursor(Mi_TEXT_CURSOR);
			}
		else
			{
			setAcceptingKeyboardFocus(false);
			setContextCursor(Mi_DEFAULT_CURSOR);
			}
		}
	public		boolean		isEditable()
		{
		return(editable);
		}
	public		void		setSensitive(boolean flag)
		{
		if (flag == isSensitive())
			return;

		super.setSensitive(flag);

		if (!isSensitive())
			{
			preInSensitiveAttributes = getAttributes();
			MiToolkit.overrideAttributes(this, MiiToolkit.Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES);
			textInsensitiveColor = MiToolkit.getAttributes(
				MiiToolkit.Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES).getColor();
			}
		else
			{
			super.setAttributes(preInSensitiveAttributes);
			}
		}
	public		void		setFontHorizontalJustification(int justification)
		{
		super.setFontHorizontalJustification(justification);
		reFormat();
		}
	public		void		setSelectEntireTextAsPartInEditor(boolean flag)
		{
		if (textEditingEventHandler != null)
			{
			textEditingEventHandler.setSelectEntireTextAsPartInEditor(flag);
			}
		selectEntireTextAsPartInEditor = flag;
		}
	public		boolean		getSelectEntireTextAsPartInEditor()
		{
		if (textEditingEventHandler == null)
			{
			return(false);
			}
		return(textEditingEventHandler.getSelectEntireTextAsPartInEditor());
		}
	public		void		setMustDoubleClickToEdit(boolean flag)
		{
		if (textEditingEventHandler != null)
			{
			textEditingEventHandler.setMustDoubleClickToEdit(flag);
			}
		mustDoubleClickToEdit = flag;
		}
	public		boolean		getMustDoubleClickToEdit()
		{
		if (textEditingEventHandler == null)
			{
			return(false);
			}
		return(textEditingEventHandler.getMustDoubleClickToEdit());
		}
	public		void		setMaxNumCharacters(int num)
		{
		maxNumCharacters = num;
		}
	public		int		getMaxNumCharacters()
		{
		return(maxNumCharacters);
		}

	public		void		setFirstDisplayedColumn(int num)
		{
		if (firstDisplayedColumn != num)
			{
			firstDisplayedColumn = num;
			invalidateArea();
			}
		}
	public		int		getFirstDisplayedColumn()
		{
		return(firstDisplayedColumn);
		}

	public		void		setNumDisplayedRows(int num)
		{
		numDisplayedRows = num;
		invalidateArea();
		}
	public		int		getNumDisplayedRows()
		{
		return(numDisplayedRows);
		}

	public		void		setLineSpacing(double spacing)
		{
		lineSpacing = spacing;
		}
	public		double		getLineSpacing()
		{
		return(lineSpacing);
		}

	public		void		setNumDisplayedColumns(int num)
		{
		numDisplayedColumns = num;
		invalidateArea();
		reFormat();
		}
	public		int		getNumDisplayedColumns()
		{
		return(numDisplayedColumns);
		}

	public		void		setPageWidth(MiDistance w)
		{
		pageWidth = w;
		reFormat();
		}
	public		MiDistance	getPageWidth()
		{
		return(pageWidth);
		}

	public		void		setUnderlineLetter(int index)
		{
		underlineLetterIndex = index;
		invalidateArea();
		}

	public		MiFont		getFontDrawn()
		{
		return(scaledFont);
		}

/*
public void setFont(MiFont f)
{
MiFont toSetFont = f;
super.setFont(f);
if (!getFont().equals(f))
{
MiDebug.println("Setting font: " + toSetFont.getFullName());
MiDebug.println("GOT font: " + getFont().getFullName());
}
}
*/

	public		void		setAttributes(MiAttributes atts)
		{
		if (inSetAttributes)
			{
			super.setAttributes(atts);
			return;
			}
		inSetAttributes = true;
		MiAttributes oldAtts = getAttributes();
		super.setAttributes(atts);
/*
		if (!isSensitive())
			{
			MiToolkit.overrideAttributes(this, MiiToolkit.Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES);
			}
*/

		if (oldAtts.getFont() != getFont())
			{
			scaledFont = getFont();
			fontChanged = true;
			reFormat();
			refreshBounds();
			if (scaledFont.isBold())
				underlineAndStrikeOutAttributes = atts.setLineWidth(1);
			else
				underlineAndStrikeOutAttributes = atts.setLineWidth(0.5);

			}
		else if (oldAtts.getFontHorizontalJustification() != getFontHorizontalJustification())
			{
			reFormat();
			}
		inSetAttributes = false;
		}

	public		void		setSelectionStart(int pos)	
		{ 
		if (pos != selectionStart)
			{
			selectionStart = pos;	
			if ((selectionStart != selectionEnd) && (!needToFormatCharStarts))
				{
				needToFormatCharStarts = true;
				reFormatCharStarts();
				}
			invalidateArea();
			}
		}
	public		int		getSelectionStart()
		{
		return(selectionStart);
		}

	public		void		setSelectionEnd(int pos)	
		{ 
		if (pos != selectionEnd)
			{
			selectionEnd = pos;		
			if ((selectionStart != selectionEnd) && (!needToFormatCharStarts))
				{
				needToFormatCharStarts = true;
				reFormatCharStarts();
				}
			invalidateArea();
			}
		}
	public		int		getSelectionEnd()
		{
		return(selectionEnd);
		}

	public		void		setSelection(int start, int end)
		{ 
		if ((start != selectionStart) || (end != selectionEnd))
			{
			selectionStart = start; 
			selectionEnd = end;
			if ((selectionStart != selectionEnd) && (!needToFormatCharStarts))
				{
				needToFormatCharStarts = true;
				reFormatCharStarts();
				}
			invalidateArea();
			}
		}
	public		void		refreshLookAndFeel()
		{
		if (specifiedText != null)
			{
			String t = MiSystem.getProperty(specifiedText, specifiedText);
			if (!t.equals(text))
				{
				String save = specifiedText;
				setText(t);
				specifiedText = save;
				}
			}
		if (!isSensitive())
			MiToolkit.overrideAttributes(this, MiiToolkit.Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES);
		else
			MiToolkit.overrideAttributes(this, MiiToolkit.Mi_TOOLKIT_TEXT_ATTRIBUTES);

		super.refreshLookAndFeel();
		}
	public		void		render(MiRenderer renderer)
		{
		if ((text == null) || (text.length() == 0) || (text.charAt(0) == '\0'))
			return;


		boolean reFormatCharStartsDone = false;

		renderer.setAttributes(getAttributes());
		int justification = getFontHorizontalJustification();
		MiBounds textShapeBounds = getBounds(tmpBounds2);
		if (fontChanged || (!renderer.getTransform().getScale(tmpScale).equals(lastDrawnScale)))
			{
			fontChanged = false;
			lastDrawnFont = renderer.findFontForScale(getFont(), scaledFont);
			lastDrawnScale.copy(renderer.getTransform().getScale(tmpScale));
			if (lastDrawnFont != null)
				{
				scaledFont = lastDrawnFont;
				// Need to update bounds too, but do it below, esp. for right-justified text
				needToFormatCharStarts = true;
				reFormatCharStarts();
				reFormatCharStartsDone = true;

				boundsAtDrawnScale.reverse();
				//replaceBounds(boundsAtDrawnScale);
				// FIX: need to notify any text cursor about change...
				}
			}

		if (boundsAtDrawnScale.isReversed())
			{
			calcBoundsAtDrawnScale(renderer, scaledFont);
			}


		int selStart = selectionStart;
		int selEnd = selectionEnd;
		for (int i = 0; i < lines.length; ++i)
			{
			MiDistance textWidth = 0;
			if ((justification == Mi_CENTER_JUSTIFIED) || (justification == Mi_RIGHT_JUSTIFIED))
				{
				textShapeBounds.setWidth(getFontDrawn().getSize(lines[i].text, tmpSize).getWidth());
				renderer.getTransform().dtow(textShapeBounds, tmpBounds);
				textWidth = tmpBounds.getWidth();
				}
			tmpBounds.copy(lines[i].specifiedFontPixelBounds);
			tmpBounds.translate(getXmin(), getYmin());

			if (justification == Mi_CENTER_JUSTIFIED)
				tmpBounds.translate((getWidth() - textWidth)/2, 0);
			else if (justification == Mi_RIGHT_JUSTIFIED)
				tmpBounds.translate(getWidth() - textWidth, 0);

			if ((selectionStart == selectionEnd) || (selEnd < 0))
				{
				if (numDisplayedColumns > 0)
					{
					int lineLen = lines[i].text.length();
					if (firstDisplayedColumn < lineLen)
						{
						renderer.drawText(
							lines[i].text.substring(firstDisplayedColumn, 
							Math.min(firstDisplayedColumn + numDisplayedColumns,lineLen)), 
							tmpBounds, lastDrawnFont);
						}
					}
				else if (firstDisplayedColumn > 0)
					{
					// Empty or short line?
					if (firstDisplayedColumn < lines[i].text.length())
						{
						renderer.drawText(lines[i].text.substring(firstDisplayedColumn), tmpBounds, lastDrawnFont);
						}
					}
				else
					{
					renderer.drawText(lines[i].text, tmpBounds, lastDrawnFont);
					}
				}
			else
				{
				int lineLen = lines[i].text.length();
				if (numDisplayedColumns > 0)
					{
					if (firstDisplayedColumn < lineLen)
						{
						int start = Math.max(selStart - firstDisplayedColumn, 0);
						int end = Math.max(selEnd - firstDisplayedColumn, 0);
						renderer.drawText(
							lines[i].text.substring(firstDisplayedColumn, 
							Math.min(firstDisplayedColumn + numDisplayedColumns,lineLen)), 
							tmpBounds, lastDrawnFont,
							start, end,
							textSensitiveSelectedColor, textSensitiveSelectedBGColor);
						}
					}
				else
					{
					renderer.drawText(lines[i].text, tmpBounds, lastDrawnFont, 
						selStart, selEnd, 
						textSensitiveSelectedColor, textSensitiveSelectedBGColor);
					}
				if (!lines[i].autoWrapped)
					++lineLen;
				selStart -= lineLen;
				selEnd -= lineLen;
				}
			// If not so small that the text will be 'greeked'...
			if ((lastDrawnFont != null)
				&& ((lastDrawnFont.isStrikeOut()) || (lastDrawnFont.isUnderlined())))
				{
				tmpBounds.getLLCorner(tmpPoint);
				dDescent.x = 0;
				dDescent.y = lastDrawnFont.getMaximumDescent();
				renderer.getTransform().dtow(tmpPoint, dDescent, wDescent);

				renderer.setAttributes(underlineAndStrikeOutAttributes);
				if (!isSensitive())
					renderer.setColor(textInsensitiveColor);

				if (lastDrawnFont.isStrikeOut())
					{
					MiCoord strikeOutY 
						= (tmpBounds.getYmax() + tmpBounds.getYmin() + wDescent.y)/2
						 - 1;
					renderer.drawLine(
						tmpBounds.xmin, 
						strikeOutY, 
						tmpBounds.xmax, 
						strikeOutY);
					}
				if (lastDrawnFont.isUnderlined())
					{
					MiCoord underlineY = tmpBounds.getYmin() + wDescent.y - 1;
					renderer.drawLine(
						tmpBounds.xmin, 
						underlineY, 
						tmpBounds.xmax, 
						underlineY);
					}
				renderer.setAttributes(getAttributes());
				}
			}
		// If not so small that the text will be 'greeked'... and a letter is underlined
		if ((lastDrawnFont != null) && (underlineLetterIndex != -1))
			{
			renderer.setAttributes(underlineAndStrikeOutAttributes);
			if (!isSensitive())
				renderer.setColor(textInsensitiveColor);

			if (!reFormatCharStartsDone)
				{
				needToFormatCharStarts = true;
				reFormatCharStarts();
				reFormatCharStartsDone = true;
				}

			tmpBounds.getLLCorner(tmpPoint);
			dDescent.x = 0;
			dDescent.y = lastDrawnFont.getMaximumDescent();
			renderer.getTransform().dtow(tmpPoint, dDescent, wDescent);

			tmpBounds.copy(lines[0].specifiedFontPixelBounds);
			tmpBounds.translate(getXmin(), getYmin());

			if (justification == Mi_CENTER_JUSTIFIED)
				tmpBounds.translate((textShapeBounds.getWidth() - tmpBounds.getWidth())/2, 0);
			else if (justification == Mi_RIGHT_JUSTIFIED)
				tmpBounds.translate(textShapeBounds.getWidth() - tmpBounds.getWidth(), 0);

			MiCoord underlineY = tmpBounds.getYmin() + wDescent.y - 1;
			renderer.drawLine(
				tmpBounds.xmin + lines[0].charStarts[underlineLetterIndex], 
				underlineY,
				tmpBounds.xmin + lines[0].charStarts[underlineLetterIndex + 1], 
				underlineY);
			}
		}
	public		void		setKeyboardFocus(boolean flag)
		{
		if (flag == hasKeyboardFocus())
			return;

		super.setKeyboardFocus(flag);
		if (hasKeyboardFocus() && editable)
			{
			if (textEditingEventHandler == null)
				{
				textEditingEventHandler = new MiIFlowEditorEventHandler(new MiTextFieldEditor(this));
				textEditingEventHandler.setMustDoubleClickToEdit(mustDoubleClickToEdit);
				textEditingEventHandler.setSelectEntireTextAsPartInEditor(selectEntireTextAsPartInEditor);
				}
			appendEventHandler(textEditingEventHandler);
			textEditingEventHandler.setKeyboardFocus(true);
			if (needToFormatCharStarts)
				{
				reFormatCharStarts();
				}
			if (modificationsAreUndoable)
				previousText = text;
			}
		if (!hasKeyboardFocus()) 
			{
			if ((textEditingEventHandler != null) && (textEditingEventHandler.getTarget() == this))
				{
				removeEventHandler(textEditingEventHandler);
				textEditingEventHandler.setKeyboardFocus(false);
				//needToFormatCharStarts = false;
				}
			if (editable)
				{
				if (modificationsAreUndoable)
					{
					String newText = text;
					if ((newText != previousText) 
						&& (!Utility.isEqualTo(newText, previousText)))
						{
						undoableModifyTextCommand.processModification(
							getContainingEditor(), newText, previousText, this);
						}
					}
				}
			}
		invalidateArea();
		}

	public		void				setInteractiveEditor(MiIFlowEditorEventHandler h)
		{
		textEditingEventHandler = h;
		}
	public		MiIFlowEditorEventHandler	getInteractiveEditor()
		{
		if (getContainer(0) instanceof MiTextField)
			{
			throw new RuntimeException(this + " please use containing MiTextField's getInteractiveEditor method ");
			}
		if (textEditingEventHandler == null)
			{
			textEditingEventHandler = new MiIFlowEditorEventHandler(new MiTextFieldEditor(this));
			}
		return(textEditingEventHandler);
		}
					/**------------------------------------------------------
	 				 * Scales the parts of this MiPart by the given scale
					 * factor.
	 				 * @param center 	the center of scaling
	 				 * @param scale	 	the scale factor
					 *------------------------------------------------------*/
	public		void		scale(MiPoint center, MiScale scale)
		{
		if (scale.isIdentity())
			return;

		if (scaleChangeModifiesFontPointSize)
			{
			setFontPointSize((int )(getFontPointSize() * (scale.x + scale.y)/2));
			}
		//else
			{
			int justification = getFontHorizontalJustification();
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
			}
		}

	public	String			toString()
		{
		String textToDisplay = text;
		if ((textToDisplay != null) && (textToDisplay.length() > 256))
			{
			textToDisplay = textToDisplay.substring(0, 252) + "...";
			}
		return(super.toString() + ".\"" + textToDisplay + "\"[" + getFont().getFullName() + "]"
			+ "<" + MiSystem.getNameOfAttributeValue(
				horizontalJustificationNames, getFontHorizontalJustification()) + ">"
			+ (numDisplayedColumns != -1 ? ("<numDisplayedColumns=" + numDisplayedColumns + ">") : "")
			+ (firstDisplayedColumn != 0 ? ("<firstDisplayedColumn=" + firstDisplayedColumn + ">") : "")
			+ "{as drawn:" + getFontDrawn().getFullName() + "}");
		}
	private	void			reFormat()
		{
		if (text == null)
			return;

		// ------------------------------------
		// 	One single line
		// ------------------------------------
		if ((text.length() == 0) || ((numDisplayedRows == 1) && (text.indexOf('\n') == -1)))
			{
			if ((lines == null) || (lines.length != 1))
				lines = new MiTextLineData[1];
			if (lines[0] == null)
				lines[0] = new MiTextLineData();
			lines[0].text = displayedText;
			formatLine(lines[0]);
			MiBounds oldBounds = getBounds(tmpBounds2);

			// Update preferred size to what we anticipate it to be soon.
			MiDistance width = lines[0].specifiedFontPixelBounds.getWidth();
			MiDistance height = lines[0].specifiedFontPixelBounds.getHeight();
			minimumBounds.setWidth(width);
			minimumBounds.setHeight(height);

			//setSize(width, height);
			lines[0].specifiedFontPixelBounds.translate(0, height);
			lines[0].drawnFontPixelBounds.translate(0, height);
			int justification = getFontHorizontalJustification();
			if (!oldBounds.isReversed())
				{
				if (justification == Mi_LEFT_JUSTIFIED)
					{
					minimumBounds.translateXminTo(oldBounds.getXmin());
					minimumBounds.translateYminTo(oldBounds.getYmin());
					}
				else if (justification == Mi_RIGHT_JUSTIFIED)
					{
					minimumBounds.translateXmaxTo(oldBounds.getXmax());
					minimumBounds.translateYminTo(oldBounds.getYmin());
					}
				else
					{
					minimumBounds.setCenterX(oldBounds.getCenterX());
					minimumBounds.translateYminTo(oldBounds.getYmin());
					}
				}
			replaceBounds(minimumBounds);
			invalidateLayout();
			return;
			}

		// ------------------------------------
		// 	Multiple lines
		// ------------------------------------
		int 		colNum 		= 0;
		int 		lastLineEnd 	= 0;
		MiDistance 	lineWidth 	= 0;
		MiTextLineData 	lineData;
		Vector		lineVector	= new Vector();
		for (int i = 0; i < text.length(); ++i)
			{
			char ch = text.charAt(i);
			++colNum;
			lineWidth += scaledFont.getWidth(ch) * lastDrawnScale.x;
			if ((ch == '\n')
				|| ((numDisplayedColumns > 0) && (colNum > numDisplayedColumns))
				|| ((pageWidth > 0) && (lineWidth >= pageWidth))
				|| (i == text.length() - 1))
				{
				lineData = new MiTextLineData();

				if ((ch != '\n')
					&& (((pageWidth > 0) && (lineWidth >= pageWidth))
					|| ((numDisplayedColumns > 0) && (colNum > numDisplayedColumns))))
					{
					lineData.autoWrapped = true;
					if (wordWrapEnabled)
						{
						for (int j = i; j > lastLineEnd; --j)
							{
							if (text.charAt(j) == ' ')
								{
								if (j < i)
									{
									i = j + 1;
									ch = ' ';
									}
								break;
								}
							else if (text.charAt(j) == ',')
								{
								if (j < i)
									{
									i = j + 1;
									ch = ',';
									}
								break;
								}
							}
						}
					}

				String lineOfText;
				if ((lineData.autoWrapped) || (ch == '\n'))
					lineOfText = text.substring(lastLineEnd, i);
				else
					lineOfText = text.substring(lastLineEnd, i + 1);

				if ((lineOfText.length() == 1) && (lineOfText.charAt(0) == '\n'))
					lineOfText = "";
				lineData.text = lineOfText;
				formatLine(lineData);
				lineVector.addElement(lineData);
				lastLineEnd = i;
				if (lineData.autoWrapped)
					--i;
				if (ch == '\n')
					{
					++lastLineEnd;
					// If this is the last character of the lines of text and it is
					// a '\n' and it is not the only character in the line... add
					// a final blank line
					if (i == text.length() - 1)// && (lineOfText.length() != 0))
						{
						lineData = new MiTextLineData();
						lineData.text = "";
						formatLine(lineData);
						lineVector.addElement(lineData);
						}
					}
				colNum = 0;
				lineWidth = 0;
				}
			}
		lines = new MiTextLineData[lineVector.size()];


		MiCoord 	lastYmin = 0;
		MiCoord 	lastDrawnYmin = 0;
		MiDistance 	lSpacing = lineSpacing;
		MiBounds	maxLineBounds = new MiBounds();

		MiBounds oldBounds = getBounds(tmpBounds2);
		for (int i = 0; i < lines.length; ++i)
			{
			lines[i] = (MiTextLineData )lineVector.elementAt(i);
			MiDistance lineHeight = lines[i].specifiedFontPixelBounds.getHeight();
			lines[i].specifiedFontPixelBounds.ymax = lastYmin - lSpacing;
			lines[i].specifiedFontPixelBounds.ymin = lastYmin - lSpacing - lineHeight;
			lastYmin = lines[i].specifiedFontPixelBounds.ymin;

			lineHeight = lines[i].drawnFontPixelBounds.getHeight();
			lines[i].drawnFontPixelBounds.ymax = lastDrawnYmin - lSpacing;
			lines[i].drawnFontPixelBounds.ymin = lastDrawnYmin - lSpacing - lineHeight;
			lastDrawnYmin = lines[i].drawnFontPixelBounds.ymin;

			maxLineBounds.accumulateMaxWidthAndHeight(lines[i].specifiedFontPixelBounds);
//System.out.println("lines[i].text = " + lines[i].text);
//System.out.println("lines[i].specifiedFontPixelBounds = " + lines[i].specifiedFontPixelBounds);
			}

		if (pageWidth > 0)
			minimumBounds.setWidth(pageWidth);
		else
			minimumBounds.setWidth(maxLineBounds.getWidth());
	
		minimumBounds.setHeight(-lastYmin);

		int justification = getFontHorizontalJustification();
		if (!oldBounds.isReversed())
			{
			if (justification == Mi_LEFT_JUSTIFIED)
				{
				minimumBounds.translateXminTo(oldBounds.getXmin());
				minimumBounds.translateYminTo(oldBounds.getYmin());
				}
			else if (justification == Mi_RIGHT_JUSTIFIED)
				{
				minimumBounds.translateXmaxTo(oldBounds.getXmax());
				minimumBounds.translateYminTo(oldBounds.getYmin());
				}
			else
				{
				minimumBounds.setCenterX(oldBounds.getCenterX());
				minimumBounds.translateYminTo(oldBounds.getYmin());
				}
			}
			
		replaceBounds(minimumBounds);

		for (int i = 0; i < lines.length; ++i)
			{
			lines[i].specifiedFontPixelBounds.translate(0, -lastYmin);
			lines[i].drawnFontPixelBounds.translate(0, -lastDrawnYmin);
//System.out.println("lines[i].specifiedFontPixelBounds = " + lines[i].specifiedFontPixelBounds);
			}
		invalidateLayout();
		}
	private	void		formatLine(MiTextLineData lineData)
		{
		MiSize pixelSize = scaledFont.getSize(lineData.text, tmpSize);
		if (lineData.drawnFontPixelBounds == null)
			{
			lineData.drawnFontPixelBounds = new MiBounds();
			}

		lineData.drawnFontPixelBounds.setBounds(
			0, 
			-pixelSize.getHeight() * lastDrawnScale.y, 
			pixelSize.getWidth() * lastDrawnScale.x, 
			0);

		pixelSize = getFont().getSize(lineData.text, tmpSize);
		if (lineData.specifiedFontPixelBounds == null)
			{
			lineData.specifiedFontPixelBounds = new MiBounds();
			}

		lineData.specifiedFontPixelBounds.setBounds(
			0, 
			-pixelSize.getHeight(), 
			pixelSize.getWidth(), 
			0);
		formatLineCharStarts(lineData);
		}

	private	void		formatLineCharStarts(MiTextLineData lineData)
		{
		if (!needToFormatCharStarts)
			return;

		if ((lineData.charStarts == null) || (lineData.charStarts.length != lineData.text.length() + 1))
			{
			lineData.charStarts = new MiDistance[lineData.text.length() + 1];
			}
		lineData.charStarts[0] = 0;
		for (int i = 1; i <= lineData.text.length(); ++i)
			{
			lineData.charStarts[i] 
				= scaledFont.getWidth(lineData.text.substring(0, i)) * lastDrawnScale.x;
			}

/*
		MiSize pixelSize = scaledFont.getSize(lineData.text, tmpSize);
		lineData.drawnFontPixelBounds.setBounds(
			0, 
			-pixelSize.getHeight() * lastDrawnScale.y, 
			pixelSize.getWidth() * lastDrawnScale.x, 
			0);
*/
		}
	// override pick so that we do not pick this or the text because it 
	// extends (invisibly) off to the right for some zoom levels
	public		boolean		pick(MiBounds area)
		{
//MiDebug.println("picking..." + this + " + bounds = " + getBounds() + " pcik area = " + area);
		if ((numDisplayedColumns == -1) && (text != null) && (!boundsAtDrawnScale.isReversed()))
			{
//MiDebug.println("picking in MiText...");
			if (getNumberOfAttachments() > 0)
				{
				if (getAttachments().pickObject(area) != null)
					{
//MiDebug.println("picking... true");
					return(true);
					}
				}

			getBounds(tmpBounds);
			if (!tmpBounds.intersects(area))
				{
//MiDebug.println("picking... false");
				return(false);
				}

//MiDebug.println("picking... boundsAtDrawnScale = " + boundsAtDrawnScale);
//MiDebug.println("picking... area = " + area);
//MiDebug.println("picking... = " + boundsAtDrawnScale.intersects(area));
			return(boundsAtDrawnScale.intersects(area));
			}
//MiDebug.println("picking usper = " + super.pick(area));
		return(super.pick(area));
		}


	private	void		reFormatCharStarts()
		{
		if (!needToFormatCharStarts)
			return;

		for (int i = 0; i < lines.length; ++i)
			{
			formatLineCharStarts(lines[i]);
			}
		needToFormatCharStarts = false;
		}
	public		MiBounds	getItemBounds(int charPos)
		{
		if (needToFormatCharStarts)
			{
			reFormatCharStarts();
			}

		getBounds(tmpBounds2);
		int justification = getFontHorizontalJustification();
		for (int i = 0; i < lines.length; ++i)
			{
			MiTextLineData lineData = lines[i];
			int lineLength = lineData.text.length() + (lineData.autoWrapped ? 0 : 1);
			MiDistance tx = tmpBounds2.xmin;
			MiDistance ty = tmpBounds2.ymin;


//MiDebug.println("tx was =" + tx);
//MiDebug.println("boundsAtDrawnScale=" + boundsAtDrawnScale);
//MiDebug.println("justification=" + justification);
			if (!boundsAtDrawnScale.isReversed())
				{
// WARNING, tHIS IS ACCURATE but puts left char outside of bounds at some zoom levels, 
// making part of char clipped off by inner bounds of textfield. We should amkeit draw in
// the correct place and then adjust this so that it corresponds to the new, correct location of the justified chars
//if (justification == Mi_CENTER_JUSTIFIED)
//MiDebug.println("tx adjusted by " + (tmpBounds2.getWidth() - boundsAtDrawnScale.getWidth())/2);
				if (justification == Mi_CENTER_JUSTIFIED)
					tx += (tmpBounds2.getWidth() - boundsAtDrawnScale.getWidth())/2;
				else if (justification == Mi_RIGHT_JUSTIFIED)
					tx += (tmpBounds2.getWidth() - boundsAtDrawnScale.getWidth());
				}

//MiDebug.println(" justified tx =" + tx);

			if (firstDisplayedColumn < lineData.charStarts.length)
				{
				tx -= lineData.charStarts[firstDisplayedColumn];
				}
			if (charPos == lineLength - 1)
				{
				// End of line...return thin sliver
				return(new MiBounds(
					lineData.charStarts[charPos] + lineData.drawnFontPixelBounds.xmin + tx,
					lineData.drawnFontPixelBounds.ymin + ty,
					lineData.charStarts[charPos] + lineData.drawnFontPixelBounds.xmin + tx,
					lineData.drawnFontPixelBounds.ymax + ty));
				}
			if (charPos < lineLength)
				{
//MiDebug.println("lineData.charStarts[charPos]=" + lineData.charStarts[charPos]);
//MiDebug.println("tx =" + tx);
//MiDebug.println("tmpBounds2.xmin =" + tmpBounds2.xmin);
//MiDebug.println("tmpBounds2.getWidth() = " + tmpBounds2.getWidth());
//MiDebug.println("boundsAtDrawnScale.getWidth() = " + boundsAtDrawnScale.getWidth());
//MiDebug.println("boundsAtDrawnScale = " + boundsAtDrawnScale);
//MiDebug.println("tx =" + tx);
//MiDebug.println("lineData.drawnFontPixelBounds.xmin =" + lineData.drawnFontPixelBounds.xmin);
				return(new MiBounds(
					lineData.charStarts[charPos] + lineData.drawnFontPixelBounds.xmin + tx,
					lineData.drawnFontPixelBounds.ymin + ty,
					lineData.charStarts[charPos + 1] + lineData.drawnFontPixelBounds.xmin + tx,
					lineData.drawnFontPixelBounds.ymax + ty));
				}
			charPos -= lineLength;
			}
		return(null);
		}
	public	int		convertLocationToPosition(MiPoint pt)
		{
		if (needToFormatCharStarts)
			{
			reFormatCharStarts();
			}
		int pos = 0;
		MiBounds bounds = getBounds(tmpBounds2);
		int justification = getFontHorizontalJustification();
		for (int i = 0; i < lines.length; ++i)
			{
			MiTextLineData lineData = lines[i];
			int lineLength = lineData.text.length();
			tmpBounds.copy(lineData.drawnFontPixelBounds).translate(bounds.xmin, bounds.ymin);

			if (justification == Mi_CENTER_JUSTIFIED)
				tmpBounds.translate((bounds.getWidth() - tmpBounds.getWidth())/2, 0);
			else if (justification == Mi_RIGHT_JUSTIFIED)
				tmpBounds.translate(bounds.getWidth() - tmpBounds.getWidth(), 0);

			if ((i == 0) && (pt.y > tmpBounds.getYmax()))
				pt.y = tmpBounds.getYmax();
			else if ((i == lines.length - 1) && (pt.y < tmpBounds.getYmin()))
				pt.y = tmpBounds.getYmin();

			if (tmpBounds.intersects(pt))
				{
				for (int j = 1; j < lineLength + 1; ++j)
					{
					if (pt.x < lineData.charStarts[j] + tmpBounds.xmin)
						{
						return(pos + j - 1);
						}
					}
				return(pos + lineLength);
				}
			else if ((pt.y >= tmpBounds.getYmin()) && (pt.y <= tmpBounds.getYmax()))
				{
				if (pt.x <= tmpBounds.getCenterX())
					return(pos);
				else 
					return(pos + lineLength);
				}
			pos += lineLength + (lineData.autoWrapped ? 0 : 1);
			}
		return(-1);
		}
	public		int		getNumberOfRows()
		{
		return(lines.length);
		}
	public		int		getRowLength(int row)
		{
		return(lines[row].text.length());
		}
	public		boolean		isRowLineWrapped(int row)
		{
		return(lines[row].autoWrapped);
		}
	protected	void		calcPreferredSize(MiSize size)
		{
		getMinimumSize(size);
		}
	protected	void		calcMinimumSize(MiSize size)
		{
		size.setSize(minimumBounds);
		}
					/**------------------------------------------------------
	 				 * Copies the given MiPart. This MiPart will have the same
					 * attributes, bounds, resources, attachments, layouts,
					 * action handlers, and event handlers as the given MiPart. 
					 * @param source	the part to copy
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiText obj = (MiText )source;

		underlineLetterIndex	= obj.underlineLetterIndex;
		editable		= obj.editable;
		lineSpacing		= obj.lineSpacing;
		pageWidth		= obj.pageWidth;
		numDisplayedColumns 	= obj.numDisplayedColumns;
		firstDisplayedColumn	= obj.firstDisplayedColumn;
		numDisplayedRows 	= obj.numDisplayedRows;
		maxNumCharacters	= obj.maxNumCharacters;
		echoChar		= obj.echoChar;
		modificationsAreUndoable= obj.modificationsAreUndoable;
		mustDoubleClickToEdit	= obj.mustDoubleClickToEdit;
		selectEntireTextAsPartInEditor	= obj.selectEntireTextAsPartInEditor;

		textEditingEventHandler = (MiIFlowEditorEventHandler )getEventHandlerWithClass("MiIFlowEditorEventHandler");
		if (textEditingEventHandler != null)
			{
			removeEventHandler(textEditingEventHandler);
			}
		setText(obj.text);
		}
	public		void		setBaselineY(MiCoord y)
		{
		dDescent.x = 0;
		dDescent.y = getFont().getMaximumDescent();
		MiEditor editor = getContainingEditor();
		if ((editor != null) && (baselineYDoesntAssumeEditorIsOneToOneDeviceToWorld))
			{
			editor.getRenderer().getTransform().dtow(tmpPoint, dDescent, wDescent);
			}
		else
			{
			wDescent.y = dDescent.y;
			}
		setYmin(y - wDescent.y);
		}
	public		MiCoord		getBaselineY()
		{
		MiCoord y = getYmin();
		dDescent.x = 0;
		dDescent.y = getFont().getMaximumDescent();
		if (baselineYDoesntAssumeEditorIsOneToOneDeviceToWorld)
			{
			// Device to world not one to one. This can cause problems if, say, a symbol is set up with 
			// a precise baseLine Y and then assigned to an editor this is recalculated to be a different
			// value. In symbol editor, text tended to migrate downwards...
			MiEditor editor = getContainingEditor();
			// If this text shape is in assigned an editor and the editor is assigned a root window...
			if ((editor != null) && (editor.getRootWindow() != null))
				{
				editor.getRenderer().getTransform().dtow(tmpPoint, dDescent, wDescent);
				}
			else
				{
				wDescent.y = dDescent.y;
				}
			}
		else
			{
			wDescent.y = dDescent.y;
			}
		return(y + wDescent.y);
		}

	public		MiBounds	getBoundsAtDrawnScale(MiRenderer renderer)
		{
		if (fontChanged || boundsAtDrawnScale.isReversed()
			|| (!renderer.getTransform().getScale(tmpScale).equals(lastDrawnScale)))
			{
			MiFont drawingFont = renderer.findFontForScale(getFont(), scaledFont);
			if (drawingFont != null)
				{
				calcBoundsAtDrawnScale(renderer, drawingFont);
				}
			}
		return(boundsAtDrawnScale);
		}
	protected	void		calcBoundsAtDrawnScale(MiRenderer renderer, MiFont drawingFont)
		{
		boundsAtDrawnScale.reverse();
		for (int i = 0; i < lines.length; ++i)
			{
			drawingFont.getSize(lines[i].text, tmpSize);
			tmpBounds2.setSize(tmpSize);
			renderer.getTransform().dtow(tmpBounds2, tmpBounds2);
			tmpBounds.copy(lines[i].specifiedFontPixelBounds);
			tmpBounds.translate(getXmin(), getYmin());
			//tmpBounds.setSize(tmpBounds2.getWidth(), tmpBounds2.getHeight());
			tmpBounds.setXmax(tmpBounds.xmin + tmpBounds2.getWidth());
			tmpBounds.setYmax(tmpBounds.ymin + tmpBounds2.getHeight());
			boundsAtDrawnScale.union(tmpBounds);
   			}
		if ((textEditingEventHandler != null) && textEditingEventHandler.hasKeyboardFocus())
			{
			textEditingEventHandler.updateCursorPosition();
			}
		}
	protected	void		geometryChanged()
		{
		super.geometryChanged();
		boundsAtDrawnScale.reverse();
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

		int justification = getFontHorizontalJustification();
		if ( (((oldAngle <= Math.PI/2) || (resultantAngle >= 3 * Math.PI/2))
			&& ((resultantAngle > Math.PI/2) && (resultantAngle < 3 * Math.PI/2)))
		|| (((oldAngle >= Math.PI/2) && (resultantAngle <= 3 * Math.PI/2))
			&& ((resultantAngle < Math.PI/2) || (resultantAngle > 3 * Math.PI/2))) )
			{
			if (justification == Mi_LEFT_JUSTIFIED)
				{
				setFontHorizontalJustification(Mi_RIGHT_JUSTIFIED);
				}
			else if (justification == Mi_CENTER_JUSTIFIED)
				{
				}
			else if (justification == Mi_RIGHT_JUSTIFIED)
				{
				setFontHorizontalJustification(Mi_LEFT_JUSTIFIED);
				}
			}
		refreshBounds();
		invalidateLayout();
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

		int justification = getFontHorizontalJustification();
		if (justification == Mi_LEFT_JUSTIFIED)
			{
			setFontHorizontalJustification(Mi_RIGHT_JUSTIFIED);
			}
		else if (justification == Mi_CENTER_JUSTIFIED)
			{
			}
		else if (justification == Mi_RIGHT_JUSTIFIED)
			{
			setFontHorizontalJustification(Mi_LEFT_JUSTIFIED);
			}
		refreshBounds();
		invalidateLayout();
		}


					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_FONT_HORIZONTAL_JUSTIFICATION_ATT_NAME))
			{
			setFontHorizontalJustification(MiSystem.getValueOfAttributeValueName(value));
			}
		else if (name.equalsIgnoreCase(Mi_TEXT_BASELINE_Y_ATT_NAME))
			{
			setBaselineY(Utility.toDouble(value));
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
		if (name.equalsIgnoreCase(Mi_FONT_HORIZONTAL_JUSTIFICATION_ATT_NAME))
			{
			return(MiSystem.getNameOfAttributeValue(
				horizontalJustificationNames, getFontHorizontalJustification()));
			}
		else if (name.equalsIgnoreCase(Mi_TEXT_BASELINE_Y_ATT_NAME))
			{
			return(Utility.toShortString(getBaselineY()));
			}
		return(super.getPropertyValue(name));
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

		propertyDescriptions = new MiPropertyDescriptions("MiText");

		propertyDescriptions.addElement(
			new MiPropertyDescription(
			Mi_FONT_HORIZONTAL_JUSTIFICATION_ATT_NAME, Mi_STRING_TYPE, Mi_CENTER_JUSTIFIED_NAME));
		propertyDescriptions.addElement(
			new MiPropertyDescription(
			Mi_TEXT_BASELINE_Y_ATT_NAME, Mi_DOUBLE_TYPE, "0", Mi_Y_COORD_TYPE));
		propertyDescriptions = new MiPropertyDescriptions(propertyDescriptions);
		propertyDescriptions.appendPropertyDescriptionComponent(super.getPropertyDescriptions());

		return(propertyDescriptions);
		}
	}
class MiTextLineData
	{
	boolean 	autoWrapped;
	String 		text;
	MiBounds 	specifiedFontPixelBounds;
	MiBounds 	drawnFontPixelBounds;
	MiDistance 	charStarts[];

			MiTextLineData()
		{
		}
	}

