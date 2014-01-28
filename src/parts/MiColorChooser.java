

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
import java.awt.Image; 
import java.awt.Toolkit; 
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 
import com.swfm.mica.util.TextFile; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiColorChooser extends MiWidget implements MiiActionHandler, MiiActionTypes, MiiCommandNames
	{
	public static	String		DEFAULT_TITLE			= "Color Chooser";
	public static	String		SHOW_CUSTOM_COLORS_BUTTON_LABEL	= "Show Color Customizer";
	public static	String		HIDE_CUSTOM_COLORS_BUTTON_LABEL	= "Hide Color Customizer";

	private		Color		defaultColor			= MiColorManager.white;
	private		Color		selectedColor;
	private		int		selectedColorIndex		= -1;
	private		boolean		brightnessOfBaseColorChanged;

	private		Color[]		colors;
	private		MiNativeDialog	dialog;
	private		MiOkCancelHelpButtons	okCancelHelpButtons;
	private		MiSwatchesColorPalette	colorSwatches;
	private		MiText		redGaugeLabel;
	private		MiGauge		redGauge;
	private		MiIntegerTextField	redGaugeValue;
	private		MiText		greenGaugeLabel;
	private		MiGauge		greenGauge;
	private		MiIntegerTextField	greenGaugeValue;
	private		MiText		blueGaugeLabel;
	private		MiGauge		blueGauge;
	private		MiIntegerTextField	blueGaugeValue;
	private		MiText		brightnessGaugeLabel;
	private		MiSingleColorBrightnessPalette	brightnessGauge;
	private		MiText		brightnessGaugeValue;
	private		MiPushButton	colorSpaceViewerSelector;
	private		MiiColorPalette[]colorSpaceViewers;
	private		MiiColorPalette	colorSpaceViewer;
	private		int		currentColorSpaceViewerIndex = 0;
	private		MiRectangle	currentColorArea;

	private		MiGridLayout	customColorGrid;
	private		MiPushButton	makeCustomColorsPushButton;
	private		MiPushButton	addCustomColorPushButton;
	private		MiPushButton	removeColorPushButton;
	private		TextFile	customColorsFile;




	public				MiColorChooser(MiEditor parent)
		{
		this(parent, DEFAULT_TITLE, true, MiColorManager.getColors());
		}
	public				MiColorChooser(MiEditor parent, String title, boolean modal)
		{
		this(parent, title, modal, MiColorManager.getColors());
		}
	public				MiColorChooser(MiEditor parent, String title, boolean modal, Color[] colors)
		{
		this.colors = colors;

		colorSpaceViewers = new MiiColorPalette[3];
		colorSpaceViewers[0] = new MiRGBCubeSlicesColorPalette(270, 110);
		colorSpaceViewers[1] = new MiRainbowColorPalette(270, 110);
		colorSpaceViewers[2] = new MiRainbowMixerColorPalette(270, 110);

		buildBox();
		setSelection(defaultColor);

		setBorderLook(Mi_RAISED_BORDER_LOOK);
		MiGradientRenderer renderer = new MiGradientRenderer();
/*** Slows down the newer versions of the JDK.... image rendering is 100x slower :-(
		setBackgroundRenderer(renderer);
***/

		dialog = new MiNativeDialog(parent, title, modal);
		dialog.appendEventHandler(new MiIDisplayToolHints());
		dialog.appendPart(this);

		okCancelHelpButtons = new MiOkCancelHelpButtons(dialog,
			"OK", Mi_OK_COMMAND_NAME,
			"Cancel", Mi_CANCEL_COMMAND_NAME,
			null, null);
		appendPart(okCancelHelpButtons);
		okCancelHelpButtons.getButton(0).setBackgroundRenderer(renderer);
		okCancelHelpButtons.getButton(0).setBackgroundColor(MiColorManager.white);
		dialog.setDefaultEnterKeyFocus(okCancelHelpButtons.getButton(0));
		okCancelHelpButtons.getButton(1).setBackgroundRenderer(renderer);
		okCancelHelpButtons.getButton(1).setBackgroundColor(MiColorManager.white);
		}
	public				MiColorChooser()
		{
		this.colors = MiColorManager.getColors();
		buildBox();
		setSelection(defaultColor);
		}

	public		void		setCustomColorsFilename(String filename)
		{
		try	{
			customColorsFile = new TextFile(filename);
			loadCustomColorsFile(customColorsFile);
			}
		catch (Exception e)
			{
			MiDebug.printStackTrace(e);
			throw new RuntimeException(e.getMessage());
			}
		}
	protected	void		loadCustomColorsFile(TextFile customColorsFile)
		{
		for (int i = 0; i < customColorsFile.getNumberOfLines(); ++i)
			{
			String line = customColorsFile.getLine(i);
			if (line.charAt(0) == '+')
				{
				colorSwatches.addColorSwatch(MiColorManager.getColor(line.substring(1)));
				}
			else if (line.charAt(0) == '-')
				{
				colorSwatches.removeColorSwatch(MiColorManager.getColor(line.substring(1)));
				}
			else
				{
				throw new RuntimeException(
					"Mica: MiColorChooser - incorrect file format in custom colors file: "
					 + customColorsFile.getFilename());
				}
			}
		}
	protected	void		addCustomColor(Color c)
		{
		colorSwatches.addColorSwatch(getSelection());
		if (customColorsFile != null)
			{
System.out.println("Color : " +c);
System.out.println("MiColorManager.getColorName(c) : " +MiColorManager.getColorName(c));
			customColorsFile.appendLine("+" + MiColorManager.getColorName(c));
			saveCustomColorsFile();
			}
		}
	protected	void		removeCustomColor(Color c)
		{
		colorSwatches.removeColorSwatch(getSelection());
		if (customColorsFile != null)
			{
			customColorsFile.appendLine("-" + MiColorManager.getColorName(c));
			saveCustomColorsFile();
			}
		}
	protected	void		saveCustomColorsFile()
		{
		try	{
			customColorsFile.save();
			}
		catch (Exception e)
			{
			throw new RuntimeException(e.getMessage());
			}
		}


	public 		Color		popupAndWaitForClose()
		{
		if (dialog != null)
			{
			String button = dialog.popupAndWaitForClose();
			if ((button != null) && (!button.equals(Mi_OK_COMMAND_NAME)))
				{
				return(null);
				}
			}
		return(getSelection());
		}
	// -----------------------------------------------------------------------
	//	Data set 
	// -----------------------------------------------------------------------
	public		void		setDefaultSelection(Color color)
		{
		defaultColor = color;

		setSelection(color);
		}

	protected	void		setSelection(Color color)
		{
		if ((color == selectedColor) 
			|| ((color != null) && (selectedColor != null) && (color.equals(selectedColor))))
			{
			return;
			}

		selectedColorIndex = -1;
		selectedColor = color;

		colorSwatches.setSelection(selectedColor);
		currentColorArea.setBackgroundColor(selectedColor);

		if (color != null)
			{
			double maxRGB = (double )MiColorManager.MAX_COLOR_RGB_VALUE;
			redGauge.setNormalizedValue(selectedColor.getRed()/maxRGB);
			redGaugeValue.setValue("" + (int )selectedColor.getRed());

			greenGauge.setNormalizedValue(selectedColor.getGreen()/maxRGB);
			greenGaugeValue.setValue("" + (int )selectedColor.getGreen());

			blueGauge.setNormalizedValue(selectedColor.getBlue()/maxRGB);
			blueGaugeValue.setValue("" + (int )selectedColor.getBlue());

			if (!brightnessOfBaseColorChanged)
				brightnessGauge.setSelection(selectedColor);

			colorSpaceViewer.setSelection(selectedColor);
			}
		}
	public 		Color		getDefaultSelection()
		{
		return(defaultColor);
		}
	public 		Color 		getSelection()
		{
		return(selectedColor);
		}

	// -----------------------------------------------------------------------
	//	Fields 
	// -----------------------------------------------------------------------
	public		MiNativeDialog	getDialog()
		{
		return(dialog);
		}
	// -----------------------------------------------------------------------
	//	Control 
	// -----------------------------------------------------------------------
	public		void		setVisible(boolean flag)
		{
		super.setVisible(flag);
		if (dialog != null)
			dialog.setVisible(flag);
		}
	// -----------------------------------------------------------------------
	//	Internal functionality 
	// -----------------------------------------------------------------------
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == colorSwatches)
			{
			setSelection(colorSwatches.getSelection());
			return(true);
			}
		else if (action.getActionSource() == redGaugeValue)
			{
			Color c = new Color(
					Utility.toInteger(redGaugeValue.getValue()),
					selectedColor.getGreen(), 
					selectedColor.getBlue());
			setSelection(c);
			}
		else if (action.getActionSource() == greenGaugeValue)
			{
			Color c = new Color(
					selectedColor.getRed(), 
					Utility.toInteger(greenGaugeValue.getValue()),
					selectedColor.getBlue());
			setSelection(c);
			}
		else if (action.getActionSource() == blueGaugeValue)
			{
			Color c = new Color(
				selectedColor.getRed(), 
				selectedColor.getGreen(), 
				Utility.toInteger(blueGaugeValue.getValue()));
			setSelection(c);
			}
		else if (action.getActionSource() == redGauge)
			{
			Color c = new Color(
				(int )(255 * redGauge.getNormalizedValue()),
				selectedColor.getGreen(), 
				selectedColor.getBlue());
			setSelection(c);
			}
		else if (action.getActionSource() == greenGauge)
			{
			Color c = new Color(
				selectedColor.getRed(), 
				(int )(255 * greenGauge.getNormalizedValue()),
				selectedColor.getBlue());
			setSelection(c);
			}
		else if (action.getActionSource() == blueGauge)
			{
			Color c = new Color(
				selectedColor.getRed(), 
				selectedColor.getGreen(), 
				(int )(255 * blueGauge.getNormalizedValue()));
			setSelection(c);
			}
		else if (action.getActionSource() == brightnessGauge)
			{
			Color c = brightnessGauge.getSelection();
			if (!c.equals(selectedColor))
				{
				brightnessOfBaseColorChanged = true;
				setSelection(c);
				brightnessOfBaseColorChanged = false;
				}
			}
		else if (action.getActionSource() == colorSpaceViewer.getPalette())
			{
			setSelection(colorSpaceViewer.getSelection());
			}
		else if (action.getActionSource() == colorSpaceViewerSelector)
			{
			if (colorSpaceViewers.length > 1)
				{
				++currentColorSpaceViewerIndex;
				if (currentColorSpaceViewerIndex >= colorSpaceViewers.length)
					currentColorSpaceViewerIndex = 0;
				colorSpaceViewer.getPalette().replaceSelf(
					colorSpaceViewers[currentColorSpaceViewerIndex].getPalette());
				colorSpaceViewer = colorSpaceViewers[currentColorSpaceViewerIndex];
				colorSpaceViewer.setSelection(selectedColor);
				}
			}
		else if (action.getActionSource() == makeCustomColorsPushButton)
			{
			if (customColorGrid.isVisible())
				{
				customColorGrid.setVisible(false);
				makeCustomColorsPushButton.setValue(SHOW_CUSTOM_COLORS_BUTTON_LABEL);
				addCustomColorPushButton.setVisible(false);
				removeColorPushButton.setVisible(false);
				}
			else
				{
				customColorGrid.setVisible(true);
				makeCustomColorsPushButton.setValue(HIDE_CUSTOM_COLORS_BUTTON_LABEL);
				addCustomColorPushButton.setVisible(true);
				removeColorPushButton.setVisible(true);
				}
			}
		else if (action.getActionSource() == addCustomColorPushButton)
			{
			addCustomColor(getSelection());
			}
		else if (action.getActionSource() == removeColorPushButton)
			{
			removeCustomColor(getSelection());
			}
		return(true);
		}
	protected	void		buildBox()
		{
		MiColumnLayout layout = new MiColumnLayout();
		setLayout(layout);
			
		colorSwatches = new MiSwatchesColorPalette();
		colorSwatches.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		colorSwatches.setBackgroundColor(Mi_TRANSPARENT_COLOR);
		appendPart(colorSwatches);


		MiRowLayout customColorButtonsLayout = new MiRowLayout();
		makeCustomColorsPushButton = new MiPushButton(HIDE_CUSTOM_COLORS_BUTTON_LABEL);
		makeCustomColorsPushButton.appendActionHandler(this, Mi_ACTIVATED_ACTION);

		addCustomColorPushButton = new MiPushButton();
		addCustomColorPushButton.setToolHintMessage("Add custom color to swatches");
		addCustomColorPushButton.setShape(MiVisibleContainer.TRIANGLE_POINTING_UP_SHAPE);
		addCustomColorPushButton.setInsetMargins(new MiMargins(20, 10, 20, 10));
		addCustomColorPushButton.setMargins(new MiMargins(5));
		addCustomColorPushButton.setBackgroundColor(MiColorManager.lightGray);
		addCustomColorPushButton.appendActionHandler(this, Mi_ACTIVATED_ACTION);

		removeColorPushButton = new MiPushButton();
		removeColorPushButton.setToolHintMessage("Remove color from swatches");
		removeColorPushButton.setShape(MiVisibleContainer.TRIANGLE_POINTING_DOWN_SHAPE);
		removeColorPushButton.setInsetMargins(new MiMargins(20, 10, 20, 10));
		removeColorPushButton.setMargins(new MiMargins(5));
		removeColorPushButton.setBackgroundColor(MiColorManager.lightGray);
		removeColorPushButton.appendActionHandler(this, Mi_ACTIVATED_ACTION);

		appendPart(makeCustomColorsPushButton);
		customColorButtonsLayout.appendPart(addCustomColorPushButton);
		customColorButtonsLayout.appendPart(removeColorPushButton);
		customColorButtonsLayout.setElementHJustification(Mi_JUSTIFIED);
		appendPart(customColorButtonsLayout);

		customColorGrid = new MiGridLayout();
		customColorGrid.setAlleySpacing(4);
		customColorGrid.setNumberOfColumns(3);
		customColorGrid.setElementHSizing(Mi_SAME_SIZE);
		customColorGrid.setGridVSizing(Mi_NONE);
		customColorGrid.setElementVSizing(Mi_SAME_SIZE);

		redGaugeLabel = new MiText("R");
		redGaugeLabel.setFontBold(true);
		redGaugeLabel.setFontPointSize(24);
		redGauge = new MiGauge();
		//redGauge.setMargins(new MiMargins(5));
		redGauge.getLED().setBackgroundColor(MiColorManager.red);
		redGauge.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		redGaugeValue = new MiIntegerTextField("");
		redGaugeValue.setMaximumValue(255);
		redGaugeValue.setUseMaxInsteadOfAveFontSize(true);
		redGaugeValue.setNumDisplayedColumns(3);
		redGaugeValue.setMaxNumCharacters(3);
		redGaugeValue.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		redGaugeValue.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		customColorGrid.appendPart(redGaugeLabel);
		customColorGrid.appendPart(redGauge);
		customColorGrid.appendPart(redGaugeValue);

		greenGaugeLabel = new MiText("G");
		greenGaugeLabel.setFontBold(true);
		greenGaugeLabel.setFontPointSize(24);
		greenGauge = new MiGauge();
		//greenGauge.setMargins(new MiMargins(5));
		greenGauge.getLED().setBackgroundColor(MiColorManager.green);
		greenGauge.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		greenGaugeValue = new MiIntegerTextField("");
		greenGaugeValue.setMaximumValue(255);
		greenGaugeValue.setUseMaxInsteadOfAveFontSize(true);
		greenGaugeValue.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		greenGaugeValue.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		greenGaugeValue.setNumDisplayedColumns(3);
		greenGaugeValue.setMaxNumCharacters(3);
		customColorGrid.appendPart(greenGaugeLabel);
		customColorGrid.appendPart(greenGauge);
		customColorGrid.appendPart(greenGaugeValue);

		blueGaugeLabel = new MiText("B");
		blueGaugeLabel.setFontBold(true);
		blueGaugeLabel.setFontPointSize(24);
		blueGauge = new MiGauge();
		//blueGauge.setMargins(new MiMargins(5));
		blueGauge.getLED().setBackgroundColor(MiColorManager.blue);
		blueGauge.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		blueGaugeValue = new MiIntegerTextField("");
		blueGaugeValue.setMaximumValue(255);
		blueGaugeValue.setUseMaxInsteadOfAveFontSize(true);
		blueGaugeValue.appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		blueGaugeValue.appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		blueGaugeValue.setNumDisplayedColumns(3);
		blueGaugeValue.setMaxNumCharacters(3);
		customColorGrid.appendPart(blueGaugeLabel);
		customColorGrid.appendPart(blueGauge);
		customColorGrid.appendPart(blueGaugeValue);

		colorSpaceViewerSelector = new MiPushButton();
		colorSpaceViewerSelector.setBackgroundColor(MiColorManager.black);
		colorSpaceViewerSelector.setPreferredSize(new MiSize(20, 20));
		colorSpaceViewerSelector.setShape(MiVisibleContainer.TRIANGLE_POINTING_DOWN_SHAPE);
		colorSpaceViewerSelector.setVisibleContainerAutomaticLayoutEnabled(false);
		colorSpaceViewerSelector.appendActionHandler(this, Mi_ACTIVATED_ACTION);
		MiColumnLayout colorSpaceViewerSelectorLayout = new MiColumnLayout();
		colorSpaceViewerSelectorLayout.appendPart(colorSpaceViewerSelector);

		for (int i = 0; i < colorSpaceViewers.length; ++i)
			{
			colorSpaceViewers[i].getPalette().setBorderLook(Mi_INDENTED_BORDER_LOOK);
			colorSpaceViewers[i].getPalette().appendActionHandler(
				new MiAction(this, 0), new MiEvent(MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0));
			}
		colorSpaceViewer = colorSpaceViewers[0];
		MiPart colorSpaceViewerPalette = colorSpaceViewer.getPalette();
		currentColorArea = new MiRectangle(new MiBounds(0,0,30,30));
		currentColorArea.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		customColorGrid.appendPart(colorSpaceViewerSelectorLayout);
		customColorGrid.appendPart(colorSpaceViewerPalette);
		customColorGrid.appendPart(currentColorArea);

		brightnessGaugeLabel = new MiText("L");
		brightnessGaugeLabel.setFontBold(true);
		brightnessGaugeLabel.setFontPointSize(24);
		brightnessGauge = new MiSingleColorBrightnessPalette(300, 10, Mi_HORIZONTAL);
		brightnessGauge.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		//brightnessGauge.setMargins(new MiMargins(5));
		brightnessGaugeValue = new MiText("");
		customColorGrid.appendPart(brightnessGaugeLabel);
		customColorGrid.appendPart(brightnessGauge.getPalette());
		customColorGrid.appendPart(brightnessGaugeValue);

		appendPart(customColorGrid);
		}
	}
interface MiiColorPalette
	{
	MiPart		getPalette();
	void		setSelection(Color color);
	Color		getSelection();
	}
abstract class MiRectangularColorPalette extends MiWidget implements MiiColorPalette, MiiActionHandler, MiiActionTypes
	{
	protected	MiImage		colorPalette;
	protected	int[]		pixels;
	protected	int		pixelsWidth;
	protected	int		pixelsHeight;
	protected	Color		selectedColor;

	public				MiRectangularColorPalette()
		{
		setBackgroundColor(Mi_TRANSPARENT_COLOR);
		}

	protected	Color		getColorAtPosition(MiPoint pt)
		{
		MiBounds b = colorPalette.getInnerBounds();
		double xScale = (pt.x - b.getXmin())/b.getWidth();
		double yScale = (pt.y - b.getYmin())/b.getHeight();

		int xPixel = (int )((pt.x - b.getXmin())/b.getWidth() * pixelsWidth);
		int yPixel = (int )((pt.y - b.getYmin())/b.getHeight() * pixelsHeight);

		if (xPixel < 0)
			xPixel = 0;
		else if (xPixel >= pixelsWidth)
			xPixel = pixelsWidth - 1;

		if (yPixel < 0)
			yPixel = 0;
		else if (yPixel >= pixelsHeight)
			yPixel = pixelsHeight - 1;

		int color = pixels[(pixelsHeight - yPixel  - 1) * pixelsWidth + xPixel];
		Color c = new Color(
			(color >> 16) & 0xff,
			(color >> 8) & 0xff,
			(color) & 0xff);
		return(c);
		}
	public		void		setSelection(Color c)
		{
		selectedColor = c;
		}
	public		Color		getSelection()
		{
		return(selectedColor);
		}
	public		boolean		processAction(MiiAction action)
		{
		if (action.getActionSource() == colorPalette)
			{
			MiBounds mouseCursor = getContainingEditor().getMousePosition();
			Color c = getColorAtPosition(mouseCursor.getCenter());
			setSelection(c);
			dispatchAction(Mi_VALUE_CHANGED_ACTION);
			}
		return(true);
		}
	}

class MiRGBCubeSlicesColorPalette extends MiRectangularColorPalette
	{
	public				MiRGBCubeSlicesColorPalette(int width, int height)
		{
		setInsetMargins(0);
		Image image = createRectangularColorSmear(width, height);
		colorPalette = new MiImage(image);
		colorPalette.appendActionHandler(new MiAction(this, 0), new MiEvent(MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0));
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);

		appendPart(colorPalette);
		}
	public		MiPart		getPalette()
		{
		return(this);
		}

	protected	Image		createRectangularColorSmear(int width, int height)
		{
		int 	transparency 	= (255 << 24);
		int[] 	pixels 		= new int[width * height];
		int 	index 		= 0;
		int	minColorValue 	= 55;
		int	minRedColor 	= minColorValue;
		int	minGreenColor 	= minColorValue;
		int	minBlueColor 	= minColorValue;
		int	numberOfZSlices	= 6;
		int	cubeWidth	= width/numberOfZSlices;
		boolean	zSlicesAreOfConstantZValue	= false;

		this.pixels = pixels;
		pixelsWidth = width;
		pixelsHeight = height;

		for (int y = 0; y < height; ++y)
			{
			double verticalColorValue = ((double )y)/height;
		
			for (int x = 0; x < width; ++x)
				{
				double horizontalColorValue;
				double phase = ((double )x) % (2 * cubeWidth);

				if (phase > cubeWidth)
					{
					// Decreasing value
					horizontalColorValue = (2 * cubeWidth - phase)/cubeWidth;
					}
				else
					{
					// Increasing value
					horizontalColorValue = phase/cubeWidth;
					}

				double depthColorValue;
				if (zSlicesAreOfConstantZValue)
					{
					int zSliceNumber = x * numberOfZSlices/cubeWidth;
					depthColorValue = zSliceNumber/numberOfZSlices;
					}
				else
					{
					depthColorValue = ((double )x)/width;
					}
				
				int	red;
				int	green;
				int	blue;
				red = (int )(horizontalColorValue * (255 - minRedColor)) + minRedColor;
				blue = (int )(verticalColorValue * (255 - minBlueColor)) + minBlueColor;
				green = (int )(depthColorValue * (255 - minGreenColor)) + minGreenColor;

				if (red > 255)
					red = 255;
				if (green > 255)
					green = 255;
				if (blue > 255)
					blue = 255;

				pixels[index++] = (red << 16) + (green << 8) + (blue & 0x00ffffff)
		  			+ transparency;
				}
			}

		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height,
						ColorModel.getRGBdefault(),
						pixels, 0, width));
		return(image);
		}
	}
class MiRainbowColorPalette extends MiRectangularColorPalette
	{
	private		MiColorPoint[]	colorPoints;



	public				MiRainbowColorPalette(int width, int height)
		{
		setInsetMargins(0);

		colorPoints = new MiColorPoint[8];
		int xPointDistance = width/8;
		colorPoints[0] = new MiColorPoint(MiColorManager.blue, 0, 0);
		colorPoints[1] = new MiColorPoint(MiColorManager.cyan, xPointDistance, height);
		colorPoints[2] = new MiColorPoint(MiColorManager.magenta, 2 * xPointDistance, 0);
		colorPoints[3] = new MiColorPoint(MiColorManager.green, 3 * xPointDistance, height);
		colorPoints[4] = new MiColorPoint(MiColorManager.yellow, 4 * xPointDistance, 0);
		colorPoints[5] = new MiColorPoint(MiColorManager.orange, 5 * xPointDistance, height);
		colorPoints[6] = new MiColorPoint(MiColorManager.pink, 6 * xPointDistance, 0);
		colorPoints[7] = new MiColorPoint(MiColorManager.red, 7 * xPointDistance, height);

		Image image = createRectangularColorSmear(width, height);
		colorPalette = new MiImage(image);
		colorPalette.appendActionHandler(new MiAction(this, 0), new MiEvent(MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0));
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);

		appendPart(colorPalette);
		}
	public		MiPart		getPalette()
		{
		return(this);
		}
	protected	Image		createRectangularColorSmear(int width, int height)
		{
		int 	transparency 	= (255 << 24);
		int[] 	pixels 		= new int[width * height];
		int 	index 		= 0;
		double	colorScaleAdjust= 1.5;
		int	minColorValue 	= 0;
		int	minRedColor 	= minColorValue;
		int	minGreenColor 	= minColorValue;
		int	minBlueColor 	= minColorValue;
		double	maxDistSquared 	= width/4 * width/4 + 2*height/3 * 2*height/3;
		MiPoint	currentPt 	= new MiPoint();

		this.pixels = pixels;
		pixelsWidth = width;
		pixelsHeight = height;

		for (int y = 0; y < height; ++y)
			{
			currentPt.y = y;
			for (int x = 0; x < width; ++x)
				{
				currentPt.x = x;
				double totalScale = 1;
				double redPart = 0;
				double greenPart = 0;
				double bluePart = 0;
				for (int i = 0; i < colorPoints.length; ++i)
					{
					MiColorPoint colorPt = colorPoints[i];
					double d = currentPt.getDistanceSquared(colorPt.pt);
					if (d < maxDistSquared)
						{
						double scale = Math.sin((maxDistSquared - d)/maxDistSquared * Math.PI/2);
						Color c = colorPt.color;
						redPart += scale * c.getRed();
						greenPart += scale * c.getGreen();
						bluePart += scale * c.getBlue();
						totalScale += scale;
						}
					}
				totalScale /= colorScaleAdjust;
				int red = (int )(redPart/totalScale) + minRedColor;
				int green = (int )(greenPart/totalScale) + minGreenColor;
				int blue = (int )(bluePart/totalScale) + minBlueColor;
					
				if (red > 255)
					red = 255;
				if (green > 255)
					green = 255;
				if (blue > 255)
					blue = 255;

				pixels[index++] = (red << 16) + (green << 8) + (blue & 0x00ffffff)
		  			+ transparency;
				}
			}

		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(width, height,
						ColorModel.getRGBdefault(),
						pixels, 0, width));
		return(image);
		}
	}
class MiColorPoint
	{
	Color		color;
	MiPoint		pt = new MiPoint();

			MiColorPoint(Color c, int x, int y)
		{
		color = c;
		pt.x = x;
		pt.y = y;
		}
	}
		
class MiRainbowMixerColorPalette extends MiRectangularColorPalette
	{
	private		MiCircle[]	colors;
	private		Color		baseSelectedColor	= MiColorManager.white;
	private		MiBounds	bowlSize		= new MiBounds(0,0,20,20);


	public				MiRainbowMixerColorPalette(int width, int height)
		{
		// Subtract 4 to account for the 2 vSpacings of 2 pixels each between the 3 rows of MiParts
		// FIX this someday in the base class MiPart.resize method
		height -= 4;

		MiColumnLayout mainLayout = new MiColumnLayout();
		setLayout(mainLayout);
		
		MiRowLayout row1 = new MiRowLayout();
		appendPart(row1);
		
		colors = new MiCircle[20];
		for (int i = 0; i < colors.length; ++i)
			{
			colors[i] = new MiCircle(bowlSize);
			colors[i].setBorderLook(Mi_RAISED_BORDER_LOOK);
			}
		colors[0].setBackgroundColor(MiColorManager.black);
		colors[1].setBackgroundColor(MiColorManager.darkGray);
		colors[2].setBackgroundColor(MiColorManager.gray);
		colors[4].setBackgroundColor(MiColorManager.lightGray);
		colors[5].setBackgroundColor(MiColorManager.veryLightGray);
		colors[6].setBackgroundColor(MiColorManager.darkWhite);
		colors[7].setBackgroundColor(MiColorManager.white);

		colors[8].setBackgroundColor(MiColorManager.getColor("darkGreen"));
		colors[9].setBackgroundColor(MiColorManager.green);
		colors[10].setBackgroundColor(MiColorManager.yellow);
		colors[11].setBackgroundColor(MiColorManager.blue);
		colors[12].setBackgroundColor(MiColorManager.getColor("lightBlue"));
		colors[13].setBackgroundColor(MiColorManager.getColor("darkCyan"));
		colors[14].setBackgroundColor(MiColorManager.cyan);
		colors[15].setBackgroundColor(MiColorManager.magenta);
		colors[16].setBackgroundColor(MiColorManager.getColor("violet"));
		colors[17].setBackgroundColor(MiColorManager.orange);
		colors[18].setBackgroundColor(MiColorManager.red);
		colors[19].setBackgroundColor(MiColorManager.pink);
		
		row1.appendPart(colors[0]);
		row1.appendPart(colors[1]);
		row1.appendPart(colors[2]);
		row1.appendPart(colors[3]);
		MiText label = new MiText(" Mixer ");
		label.setFontPointSize(18);
		label.setFontBold(true);
		label.setFontItalic(true);
		row1.appendPart(label);
		row1.appendPart(colors[4]);
		row1.appendPart(colors[5]);
		row1.appendPart(colors[6]);
		row1.appendPart(colors[7]);

		selectedColor = MiColorManager.white;
		Image image = createRectangularColorSmear(
			(int )(width - getTotalMargins().getWidth()),
			(int )(height - bowlSize.getHeight() - 
				Math.max(bowlSize.getHeight(), label.getHeight()) 
				- getTotalMargins().getHeight()));

		colorPalette = new MiImage(image);
		colorPalette.setBorderLook(Mi_INDENTED_BORDER_LOOK);
		colorPalette.setInvalidLayoutNotificationsEnabled(false);
		appendPart(colorPalette);

		MiRowLayout row2 = new MiRowLayout();
		appendPart(row2);
		
		row2.appendPart(colors[8]);
		row2.appendPart(colors[9]);
		row2.appendPart(colors[10]);
		row2.appendPart(colors[11]);
		row2.appendPart(colors[12]);
		row2.appendPart(colors[13]);
		row2.appendPart(colors[14]);
		row2.appendPart(colors[15]);
		row2.appendPart(colors[16]);
		row2.appendPart(colors[17]);
		row2.appendPart(colors[18]);
		row2.appendPart(colors[19]);

		appendActionHandler(
			new MiAction(this, 0), new MiEvent(MiEvent.Mi_LEFT_MOUSE_CLICK_EVENT, 0, 0));

		setSize(width, height);
		}
	public		MiPart		getPalette()
		{
		return(this);
		}
	public		void		setSelection(Color color)
		{
		}
	public		void		mixinColor(Color color)
		{
//System.out.println("MIXIN COLOR: " + color + ", baseColor = " + baseSelectedColor);
		selectedColor = color;
		colorPalette.setImage(setRectangularColorSmear());
		int	red 	= (selectedColor.getRed() + baseSelectedColor.getRed())/2;
		int	green 	= (selectedColor.getGreen() + baseSelectedColor.getGreen())/2;
		int	blue	= (selectedColor.getBlue() + baseSelectedColor.getBlue())/2;
		baseSelectedColor = new Color(red, green, blue);
		invalidateArea();
		}
	public		boolean		processAction(MiiAction action)
		{
		MiBounds mouseCursor = getContainingEditor().getMousePosition();
		for (int i = 0; i < colors.length; ++i)
			{
			if (colors[i].pick(mouseCursor))
				{
				mixinColor(colors[i].getBackgroundColor());
				dispatchAction(Mi_VALUE_CHANGED_ACTION);
				return(true);
				}
			}
		Color c = getColorAtPosition(mouseCursor.getCenter());
		super.setSelection(c);
		dispatchAction(Mi_VALUE_CHANGED_ACTION);
		return(true);
		}
	protected	Image		createRectangularColorSmear(int width, int height)
		{
		pixelsWidth 	= width;
		pixelsHeight 	= height;
		pixels 		= new int[pixelsWidth * pixelsHeight];

		return(setRectangularColorSmear());
		}

	protected	Image		setRectangularColorSmear()
		{
		int 	transparency 	= (255 << 24);
		int 	index 		= 0;

		int	redStart 	= selectedColor.getRed();
		int	greenStart 	= selectedColor.getGreen();
		int	blueStart	= selectedColor.getBlue();

		int	redDistance 	= baseSelectedColor.getRed() - selectedColor.getRed();
		int	greenDistance 	= baseSelectedColor.getGreen() -selectedColor.getGreen();
		int	blueDistance 	= baseSelectedColor.getBlue() - selectedColor.getBlue();

		for (int y = 0; y < pixelsHeight; ++y)
			{
			for (int x = 0; x < pixelsWidth; ++x)
				{
				double scale = ((double )x)/pixelsWidth;
				int red = (int )(scale * redDistance + redStart);
				int green = (int )(scale * greenDistance + greenStart);
				int blue = (int )(scale * blueDistance + blueStart);

				pixels[index++] = (red << 16) + (green << 8) + (blue & 0x00ffffff)
		  			+ transparency;
				}
			}

		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(pixelsWidth, pixelsHeight,
						ColorModel.getRGBdefault(),
						pixels, 0, pixelsWidth));
		return(image);
		}
	}

class MiSingleColorBrightnessPalette extends MiRectangularColorPalette
	{
	private		int		orientation 	= Mi_VERTICAL;
	private		double		range		= 1.0;
	private		MiSlider	gauge;
	private		Color		baseSelectedColor;



	public				MiSingleColorBrightnessPalette(int width, int height, int orientation)
		{
		this.orientation = orientation;
		selectedColor = MiColorManager.blue;
		Image image = createRectangularColorSmear(width, height);
		colorPalette = new MiImage(image);
		MiColumnLayout layout = new MiColumnLayout();
		layout.setElementSizing(Mi_EXPAND_TO_FILL);
		setLayout(layout);

		gauge = new MiSlider();
		//brightnessGauge.appendCustomLook(new MiRadioTunerSliderLook());
		gauge.setBackgroundImage(colorPalette.getImage());
		gauge.setInsetMargins(new MiMargins(2));
		gauge.setNormalizedLengthOfThumb(0.03);
		gauge.getThumb().setHeight(20);
		gauge.appendActionHandler(this, Mi_VALUE_CHANGED_ACTION);
		//appendPart(gauge);
		}
	public		MiPart		getPalette()
		{
		return(gauge);
		}
	public		MiImage		getImage()
		{
		return(colorPalette);
		}
	public		double		getRangeOfBrightness()
		{
		return(range);
		}
	public		double		getScaledValue()
		{
		return(gauge.getNormalizedValue() * range);
		}
	public		void		setScaledValue(double value)
		{
		gauge.setNormalizedValue(value/range);
		}
	public		double		getNormalizedValue()
		{
		return(gauge.getNormalizedValue());
		}
	public		void		setNormalizedValue(double value)
		{
		gauge.setNormalizedValue(value);
		}
	public		void		setSelection(Color color)
		{
		if (!color.equals(selectedColor))
			{
			super.setSelection(color);
			colorPalette.setImage(setRectangularColorSmear());
			gauge.setBackgroundImage(colorPalette.getImage());
			baseSelectedColor = color;
			gauge.setNormalizedValue(1.0/range);
			}
		}
	public		boolean		processAction(MiiAction action)
		{
		if (baseSelectedColor == null)
			return(true);

		super.setSelection(new Color(
				(int )(baseSelectedColor.getRed() * getScaledValue()),
				(int )(baseSelectedColor.getGreen() * getScaledValue()),
				(int )(baseSelectedColor.getBlue() * getScaledValue())));
		return(dispatchAction(Mi_VALUE_CHANGED_ACTION) == Mi_PROPOGATE);
		}
	protected	Image		createRectangularColorSmear(int width, int height)
		{
		pixelsWidth 	= width;
		pixelsHeight 	= height;
		pixels 		= new int[pixelsWidth * pixelsHeight];

		return(setRectangularColorSmear());
		}

	protected	Image		setRectangularColorSmear()
		{
		int 	transparency 	= (255 << 24);
		int 	index 		= 0;
		double	minScale	= 0.25;
		double	scale		= 1.0;

		int	baseRed		= selectedColor.getRed();
		int	baseGreen	= selectedColor.getGreen();
		int	baseBlue	= selectedColor.getBlue();

		if (baseRed == 0)
			baseRed = 1;
		if (baseGreen == 0)
			baseGreen = 1;
		if (baseBlue == 0)
			baseBlue = 1;

		range = 255;
		range = Math.min(range, ((double )255)/baseRed);
		range = Math.min(range, ((double )255)/baseGreen);
		range = Math.min(range, ((double )255)/baseBlue);

		for (int y = 0; y < pixelsHeight; ++y)
			{
			if (orientation == Mi_VERTICAL)
				{
				scale = ((double )y)/pixelsHeight;
				}
			for (int x = 0; x < pixelsWidth; ++x)
				{
				if (orientation == Mi_HORIZONTAL)
					{
					scale = ((double )x)/pixelsWidth;
					}
 				scale = scale * (1 - minScale) + minScale;
				int red = (int )(scale * range * baseRed);
				int green = (int )(scale * range * baseGreen);
				int blue = (int )(scale * range * baseBlue);
					

				if (red > 255)
					red = 255;
				if (green > 255)
					green = 255;
				if (blue > 255)
					blue = 255;

				pixels[index++] = (red << 16) + (green << 8) + (blue & 0x00ffffff)
		  			+ transparency;
				}
			}

		Image image = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(pixelsWidth, pixelsHeight,
						ColorModel.getRGBdefault(),
						pixels, 0, pixelsWidth));
		return(image);
		}
	}


