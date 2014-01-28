
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
import java.awt.Font; 
import java.util.Enumeration; 
import java.util.Properties; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiToolkit implements MiiTypes, MiiAttributeTypes, MiiCommandNames, MiiMessages, MiiLookProperties, MiiToolkit
	{
	public static final String	Mi_DEFAULT_WIDGET_ATTRIBUTES_PROPERTY_NAME	= "MiToolkit";

	private static 	MiImage		iconizedWindowIcon;

	private static  MiImage		checkMarkIcon;

	private static 	Color		textFieldEditableBGColor	= MiColorManager.white;
	private static	Color		textFieldInEditableBGColor 	= MiColorManager.veryVeryLightGray;
	private static 	Color		textSensitiveColor 		= MiColorManager.black;
	private static	Color		textInSensitiveColor 		= MiColorManager.gray;
	private static	int		textSensitiveBorderLook 	= MiiTypes.Mi_NONE;
	private static 	int		textInSensitiveBorderLook 	= MiiTypes.Mi_GROOVE_BORDER_LOOK;

	private static	MiAttributes	defaultAttributes		= new MiAttributes();

	// ------- toggleButton -----
	public static	int		radioButtonShape 		= MiVisibleContainer.DIAMOND_SHAPE;




	private static	MiAttributes[]	toolkitAttributes = new MiAttributes[Mi_NUMBER_OF_TOOLKIT_ATTRIBUTES];

	// ------- widget state attributes -------
	private static	MiAttributes	widgetNormalAttributes			= defaultAttributes;
	private static	MiAttributes	widgetMouseFocusAttributes		= defaultAttributes;
	private static	MiAttributes	widgetKeyboardFocusAttributes		= defaultAttributes;
	private static	MiAttributes	widgetEnterKeyFocusAttributes		= defaultAttributes;
	private static	MiAttributes	widgetSelectedAttributes		= defaultAttributes;
	private static	MiAttributes	widgetInSensitiveAttributes		= defaultAttributes;

	public static	int		acceleratorModifier;


	//private static MiiDeviceRenderer backgroundRenderer;


	// Temporary instance used to help with assigning widget state atts to a widget.
	private static	MiWidgetAttributes widgetAtts 		= new MiWidgetAttributes();

	public		MiToolkit()
		{
		}
	static
		{
		widgetNormalAttributes 
			= widgetNormalAttributes.setColor(MiColorManager.black);
		widgetNormalAttributes 
			= widgetNormalAttributes.setBackgroundColor(MiColorManager.veryDarkWhite);
		widgetNormalAttributes 
			= widgetNormalAttributes.setBorderLook(Mi_NONE);
		widgetNormalAttributes 
			= widgetNormalAttributes.setHasBorderHilite(false);

		widgetInSensitiveAttributes 
			= widgetInSensitiveAttributes.setHasBorderHilite(false);

		widgetSelectedAttributes
			= widgetSelectedAttributes.setColor(MiColorManager.black);
		widgetSelectedAttributes
			= widgetSelectedAttributes.setBackgroundColor(MiColorManager.veryLightGray);

		widgetEnterKeyFocusAttributes
			= widgetEnterKeyFocusAttributes.setHasBorderHilite(true);

		widgetKeyboardFocusAttributes
			= widgetKeyboardFocusAttributes.setHasBorderHilite(true);

		widgetMouseFocusAttributes
			= widgetMouseFocusAttributes.setHasBorderHilite(true);

		if (MiSystem.isPC())
			acceleratorModifier = MiEvent.Mi_ALT_KEY_HELD_DOWN;
		else if (MiSystem.getJDKVersion() >= 1.2)
			acceleratorModifier = MiEvent.Mi_ALT_KEY_HELD_DOWN + MiEvent.Mi_META_KEY_HELD_DOWN;
		else
			acceleratorModifier = MiEvent.Mi_META_KEY_HELD_DOWN;

		initLookAndFeel();
		}
	public static	void		initLookAndFeel()
		{
		//-------------------------------------------------------------
		// Create toolkit-wide attributes and
		// initialize attributes with default values
		//-------------------------------------------------------------
		toolkitAttributes[Mi_TOOLKIT_EDITABLE_BG_ATTRIBUTES] 
			= new MiAttributes().setBackgroundColor(MiColorManager.white);
		toolkitAttributes[Mi_TOOLKIT_UNEDITABLE_BG_ATTRIBUTES]
			= new MiAttributes().setBackgroundColor(MiColorManager.darkWhite);
		toolkitAttributes[Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES]
			= new MiAttributes().setBackgroundColor(MiColorManager.veryLightGray);

		toolkitAttributes[Mi_TOOLKIT_TEXT_ATTRIBUTES]
			= new MiAttributes().setColor(MiColorManager.black);
		toolkitAttributes[Mi_TOOLKIT_TEXT_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_TEXT_ATTRIBUTES].setBorderLook(Mi_NONE);

		toolkitAttributes[Mi_TOOLKIT_TEXT_SENSITIVE_ATTRIBUTES]
			= new MiAttributes().setColor(MiColorManager.black);

		toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES]
			= new MiAttributes().setColor(MiColorManager.gray);
		toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES].setBorderLook(Mi_GROOVE_BORDER_LOOK);
		toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES].setLightColor(MiColorManager.white);


		toolkitAttributes[Mi_TOOLKIT_TEXT_EDITABLE_ATTRIBUTES]
			= new MiAttributes().setColor(MiColorManager.gray);
		toolkitAttributes[Mi_TOOLKIT_TEXT_EDITABLE_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES].setBorderLook(Mi_NONE);

		toolkitAttributes[Mi_TOOLKIT_TEXT_INEDITABLE_ATTRIBUTES]
			= new MiAttributes().setColor(MiColorManager.gray);
		toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES].setBorderLook(Mi_GROOVE_BORDER_LOOK);
		toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES].setLightColor(MiColorManager.white);

		toolkitAttributes[Mi_TOOLKIT_TEXT_SELECTED_ATTRIBUTES]
			= new MiAttributes().setColor(MiColorManager.white);
		toolkitAttributes[Mi_TOOLKIT_TEXT_SELECTED_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_TEXT_SELECTED_ATTRIBUTES].setBackgroundColor(MiColorManager.blue);
		toolkitAttributes[Mi_TOOLKIT_TEXT_SELECTED_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_TEXT_SELECTED_ATTRIBUTES].setBorderLook(Mi_NONE);

		toolkitAttributes[Mi_TOOLKIT_WINDOW_ATTRIBUTES]
			= new MiAttributes().setBackgroundColor(MiColorManager.veryDarkWhite);
		toolkitAttributes[Mi_TOOLKIT_WINDOW_TEXT_ATTRIBUTES]
			= new MiAttributes().setFont(MiFontManager.findFont("Helvetica.16.Bold"));

		toolkitAttributes[Mi_TOOLKIT_MENU_ATTRIBUTES]
			= new MiAttributes().setBackgroundColor(MiColorManager.veryDarkWhite);
		toolkitAttributes[Mi_TOOLKIT_MENU_TEXT_ATTRIBUTES]
			= new MiAttributes().setColor(MiColorManager.black);

		toolkitAttributes[Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES]
			= new MiAttributes().setBackgroundColor(MiColorManager.veryLightGray);
		toolkitAttributes[Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES]
			= toolkitAttributes[Mi_TOOLKIT_CELL_FOCUS_ATTRIBUTES].setBorderLook(Mi_RAISED_BORDER_LOOK);
		toolkitAttributes[Mi_TOOLKIT_CELL_SELECTED_ATTRIBUTES]
			= new MiAttributes().setBackgroundColor(MiColorManager.blue);

		refreshLookAndFeel();
		}

	public static	void		refreshLookAndFeel()
		{
		Properties properties = (Properties )MiSystem.getPropertiesForClass(
						Mi_DEFAULT_WIDGET_ATTRIBUTES_PROPERTY_NAME);
		MiWidgetAttributes atts = getStandardWidgetAttributes();
		setWidgetAttributeProperties(atts, properties);
		setStandardWidgetAttributes(atts);
		//-------------------------------------------------------------
		// Override the defaults if necessary... from properties specified
		// by a theme/skin/suit/style
		//-------------------------------------------------------------
		for (int i = 0; i < toolkitAttributesPropertyNames.length; ++i)
			{
			toolkitAttributes[i] = toolkitAttributes[i].overrideFrom(
				MiSystem.getPropertyAttributes(toolkitAttributesPropertyNames[i]));
			}
		}
	public static	MiAttributes	getAttributes(int attributeID)
		{
		return(toolkitAttributes[attributeID]);
		}
	public static	void		overrideAttributes(MiPart part, int attributeID)
		{
		part.setAttributes(part.getAttributes().overrideFrom(toolkitAttributes[attributeID]));
		}
	public static	void		setAttributes(MiPart part, int attributeID)
		{
		part.setAttributes(toolkitAttributes[attributeID]);
		}
	public static	void		setBackgroundAttributes(MiPart part, int attributeID)
		{
		part.setBackgroundColor(toolkitAttributes[attributeID].getBackgroundColor());
		part.setBackgroundTile(toolkitAttributes[attributeID].getBackgroundTile());
		part.setBackgroundImage(toolkitAttributes[attributeID].getBackgroundImage());
		}

	public static	Properties	setWidgetAttributeProperties(
						MiWidgetAttributes atts, Properties properties)
		{
		for (Enumeration e = properties.keys(); e.hasMoreElements();)
			{
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);
			boolean processed = setWidgetAttributeProperty(atts, key, value);
			if (processed)
				properties.remove(key);
			}
		return(properties);
		}
/* TEST: Why was this here?
			if (atts.normalAttributes.hasAttribute(key))
				{
				atts.normalAttributes = atts.normalAttributes.setAttributeValue(key, value);
				properties.remove(key);
				}
*/

	public static	boolean		setWidgetAttributeProperty(
						MiWidgetAttributes atts, String key, String value)
		{
		for (int i = 0; i < MiWidgetAttributes.widgetStateAttributeNameTable.length; ++i)
			{
			String attStateName = MiWidgetAttributes.widgetStateAttributeNameTable[i];
			if (key.startsWith(attStateName))
				{
				atts.setAttributes(atts.getAttributes(i).setAttributeValue(
					key.substring(attStateName.length()), value), i);

				return(true);
				}
			}
		return(false);
		}
	/**
	 * Possible categories are defined in MiiToolkit.java and are specifyable from properties.mica and are currently:
	 *	Mi_TOOLKIT_EDITABLE_BG_PROPERTY_NAME	= "MiToolkit.editableBackground";
	 *	Mi_TOOLKIT_UNEDITABLE_BG_PROPERTY_NAME	= "MiToolkit.uneditableBackground";
	 *	Mi_TOOLKIT_INDENTED_BG_PROPERTY_NAME	= "MiToolkit.indentedBackground";
	 *	Mi_TOOLKIT_TEXT_INSENSITIVE_PROPERTY_NAME= "MiToolkit.textInsensitive";
	 *	Mi_TOOLKIT_TEXT_SELECTED_PROPERTY_NAME	= "MiToolkit.textSelected";
	 *	Mi_TOOLKIT_TEXT_PROPERTY_NAME		= "MiToolkit.text";
	 *	Mi_TOOLKIT_WINDOW_TEXT_PROPERTY_NAME	= "MiToolkit.windowText";
	 *	Mi_TOOLKIT_WINDOW_PROPERTY_NAME		= "MiToolkit.window";
	 *	Mi_TOOLKIT_MENU_TEXT_PROPERTY_NAME	= "MiToolkit.menuText";
	 *	Mi_TOOLKIT_MENU_PROPERTY_NAME		= "MiToolkit.menu";
	 *	Mi_TOOLKIT_CELL_FOCUS_PROPERTY_NAME	= "MiToolkit.cellFocus";
	 *	Mi_TOOLKIT_CELL_SELECTED_PROPERTY_NAME	= "MiToolkit.cellSelected";
	 **/
	public static	void		setToolkitAttributesForCategory(String tookitAttributeCategory, MiAttributes atts)
		{
		for (int i = 0; i < toolkitAttributesPropertyNames.length; ++i)
			{
			if (tookitAttributeCategory.equals(toolkitAttributesPropertyNames[i]))
				{
				toolkitAttributes[i] = atts;
				if (Mi_TOOLKIT_TEXT_SENSITIVE_ATTRIBUTES == i)
					{
					textSensitiveColor = atts.getColor();
					textSensitiveBorderLook = atts.getBorderLook();
					}
				else if (Mi_TOOLKIT_TEXT_INSENSITIVE_ATTRIBUTES == i)
					{
					textInSensitiveColor = atts.getColor();
					textInSensitiveBorderLook = atts.getBorderLook();
					}
				else if (Mi_TOOLKIT_TEXT_EDITABLE_ATTRIBUTES == i)
					{
					textFieldEditableBGColor	= atts.getBackgroundColor();
					}
				else if (Mi_TOOLKIT_TEXT_INEDITABLE_ATTRIBUTES == i)
					{
					textFieldInEditableBGColor	= atts.getBackgroundColor();
					}
				}
			}
		}
	public static	MiAttributes	getToolkitAttributesForCategory(String tookitAttributeCategory)
		{
		for (int i = 0; i < toolkitAttributesPropertyNames.length; ++i)
			{
			if (tookitAttributeCategory.equals(toolkitAttributesPropertyNames[i]))
				{
				return(toolkitAttributes[i]);
				}
			}
		return(null);
		}
	public static	String		getDefaultWidgetAttributeProperty(String name)
		{
		for (int i = 0; i < toolkitAttributesPropertyNames.length; ++i)
			{
			if (name.startsWith(toolkitAttributesPropertyNames[i]))
				{
				String attName = name.substring(toolkitAttributesPropertyNames[i].length() + 1);
				return(toolkitAttributes[i].getAttributeValue(attName));
				}
			}
		if (name.startsWith(Mi_DEFAULT_WIDGET_ATTRIBUTES_PROPERTY_NAME))
			{
			MiWidgetAttributes atts = getStandardWidgetAttributes();
			return(getWidgetAttributeProperty(atts, 
				name.substring(Mi_DEFAULT_WIDGET_ATTRIBUTES_PROPERTY_NAME.length() + 1)));
			}
		else
			{
			throw new IllegalArgumentException("Invalid name of attribute: " + name);
			}
		}

	public static	String		getWidgetAttributeProperty(
						MiWidgetAttributes atts, String key)
		{
		for (int i = 0; i < MiWidgetAttributes.widgetStateAttributeNameTable.length; ++i)
			{
			String attStateName = MiWidgetAttributes.widgetStateAttributeNameTable[i];
			if (key.startsWith(attStateName))
				{
				return(atts.getAttributes(i).getAttributeValue(
					key.substring(attStateName.length())));
				}
			}
		return(MiiToolkit.Mi_NOT_A_VALID_PROPERTY_VALUE);
		}


	public static	void		generateColorSchemeFromBaseColor(Color c)
		{
		}

		

	public	Color	getTextFieldEditableBGColor() 	{ return(textFieldEditableBGColor);	}
	public	Color	getTextFieldInEditableBGColor() { return(textFieldInEditableBGColor);	}
	public	Color	getTextSensitiveColor() 	{ return(textSensitiveColor);		}
	public	Color	getTextInSensitiveColor() 	{ return(textInSensitiveColor);		}
	public	int	getTextInSensitiveBorderLook() 	{ return(textInSensitiveBorderLook);	}
	public	int	getTextSensitiveBorderLook() 	{ return(textSensitiveBorderLook);	}

/****
	public		MiiDeviceRenderer	getBackgroundRenderer()
		{
		return(backgroundRenderer);
		}
	public		void			setBackgroundRenderer(MiiDeviceRenderer r)
		{
		backgroundRenderer = r;
		if (widgetNormalAttributes.getBackgroundColor() != Mi_TRANSPARENT_COLOR)
			widgetNormalAttributes = widgetNormalAttributes.setBackgroundRenderer(r);
		}
****/

	public static	void			setStandardWidgetAttributes(MiWidgetAttributes widgetAtts)
		{
		widgetNormalAttributes		= widgetAtts.normalAttributes;
		widgetInSensitiveAttributes	= widgetAtts.inSensitiveAttributes;
		widgetSelectedAttributes	= widgetAtts.selectedAttributes;
		widgetKeyboardFocusAttributes	= widgetAtts.keyboardFocusAttributes;
		widgetEnterKeyFocusAttributes	= widgetAtts.enterKeyFocusAttributes;
		widgetMouseFocusAttributes	= widgetAtts.mouseFocusAttributes;
		}
	public static	MiWidgetAttributes	getStandardWidgetAttributes()
		{
		MiWidgetAttributes widgetAtts 		= new MiWidgetAttributes();
		widgetAtts.normalAttributes		= widgetNormalAttributes;
		widgetAtts.inSensitiveAttributes	= widgetInSensitiveAttributes;
		widgetAtts.selectedAttributes		= widgetSelectedAttributes;
		widgetAtts.keyboardFocusAttributes	= widgetKeyboardFocusAttributes;
		widgetAtts.enterKeyFocusAttributes	= widgetEnterKeyFocusAttributes;
		widgetAtts.mouseFocusAttributes		= widgetMouseFocusAttributes;
		return(widgetAtts);
		}
	public		void			applyStandardWidgetAttributesToWidget(MiWidget widget)
		{
		widgetAtts.normalAttributes		= widgetNormalAttributes;
		widgetAtts.inSensitiveAttributes	= widgetInSensitiveAttributes;
		widgetAtts.selectedAttributes		= widgetSelectedAttributes;
		widgetAtts.keyboardFocusAttributes	= widgetKeyboardFocusAttributes;
		widgetAtts.enterKeyFocusAttributes	= widgetEnterKeyFocusAttributes;
		widgetAtts.mouseFocusAttributes		= widgetMouseFocusAttributes;
		widget.setWidgetAttributes(widgetAtts);
		}

	public static	void			applyStandardObjectAttributes(MiPart obj)
		{
		if (obj instanceof MiText)
			{
			obj.setColor(textSensitiveColor);
			}
		else 
			{
			obj.setColor(widgetNormalAttributes.getColor());
			}
		obj.setBackgroundColor(widgetNormalAttributes.getBackgroundColor());
		obj.setBackgroundRenderer(widgetNormalAttributes.getBackgroundRenderer());
		}
	public		void			setStandardObjectAttributes(MiPart obj)
		{
		applyStandardObjectAttributes(obj);
		}
	public static	MiPart		getIconizedWindowIcon()
		{
		if (iconizedWindowIcon == null)
			iconizedWindowIcon = new MiImage(Mi_ICONIZED_WINDOW_ICON_NAME, true);
		return(iconizedWindowIcon.copy());
		}
	public static	MiPart		getCheckMarkIcon()
		{
		if (checkMarkIcon == null)
			checkMarkIcon = new MiImage(Mi_CHECK_MARK_ICON_NAME, true);
		return(checkMarkIcon.copy());
		}

					/**------------------------------------------------------
	 				 * Displays a dialog box which prompts the user to 1) save
					 * the changes thay have made to the given nameOfChanged, 
					 * 2) Loose their changes or 3) cancel the current operation. 
					 * The current thread will wait here until the user responds.
					 * Based on the user's response the following values are
					 * returned:
					 *    Mi_SAVE_COMMAND_NAME
					 *    Mi_CONTINUE_COMMAND_NAME
					 *    Mi_CANCEL_COMMAND_NAME
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param nameOfChanged	the name that will appear in the
					 *			dialog box prompt or null
					 * @return		the user's response
					 *------------------------------------------------------*/
	public static	String		postSaveChangesDialog(MiEditor window, String nameOfChanged)
		{
		String yesLabel = MiSystem.getProperty(Mi_DIALOG_YES_LABEL);
		String noLabel = MiSystem.getProperty(Mi_DIALOG_NO_LABEL);
		String cancelLabel = MiSystem.getProperty(Mi_DIALOG_CANCEL_LABEL);
		MiNativeMessageDialog dialog = new MiNativeMessageDialog(
			window,
			Mi_SAVE_CHANGES_DIALOG_TITLE_MSG,
			MiiToolkit.Mi_WARNING_DIALOG_TYPE,
			(nameOfChanged == null) ? Mi_SAVE_CHANGES_DIALOG_MSG 
				   : Utility.sprintf(MiSystem.getProperty(
					Mi_SAVE_CHANGES_TO_ITEM_DIALOG_MSG), nameOfChanged),
			true,
			yesLabel,
			noLabel,
			cancelLabel,
			null,
			0);
		String result = dialog.popupAndWaitForClose();
		if (result.equals(yesLabel))
			return(Mi_SAVE_COMMAND_NAME);
		if (result.equals(noLabel))
			return(Mi_CONTINUE_COMMAND_NAME);
		return(Mi_CANCEL_COMMAND_NAME);
		}
	public static	String		postSaveChangesAllViewsDialog(MiEditor window, String nameOfAChanged)
		{
		String yesLabel = MiSystem.getProperty(Mi_DIALOG_YES_LABEL);
		String noLabel = MiSystem.getProperty(Mi_DIALOG_NO_LABEL);
		String cancelLabel = MiSystem.getProperty(Mi_DIALOG_CANCEL_LABEL);
		String yesAllLabel = MiSystem.getProperty(Mi_DIALOG_YES_ALL_LABEL);
		String noAllLabel = MiSystem.getProperty(Mi_DIALOG_NO_ALL_LABEL);
		MiNativeMessageDialog dialog = new MiNativeMessageDialog(
			window,
			Mi_SAVE_CHANGES_DIALOG_TITLE_MSG,
			MiiToolkit.Mi_WARNING_DIALOG_TYPE,
			(nameOfAChanged == null) ? Mi_SAVE_CHANGES_DIALOG_MSG 
				   : Utility.sprintf(MiSystem.getProperty(
					Mi_SAVE_CHANGES_TO_ITEM_DIALOG_MSG), nameOfAChanged),
			true,
			yesLabel,
			noLabel,
			cancelLabel,
			yesAllLabel,
			noAllLabel,
			0);
		String result = dialog.popupAndWaitForClose();
		if (result.equals(yesLabel))
			return(Mi_SAVE_COMMAND_NAME);
		if (result.equals(noLabel))
			return(Mi_CONTINUE_COMMAND_NAME);
		if (result.equals(yesAllLabel))
			return(Mi_SAVE_ALL_COMMAND_NAME);
		if (result.equals(noAllLabel))
			return(Mi_NO_SAVE_ALL_COMMAND_NAME);
		return(Mi_CANCEL_COMMAND_NAME);
		}
					/**------------------------------------------------------
	 				 * Creates a dialog box which prompts the user with an
					 * error icon and the given error message. The current 
					 * thread will wait here until the user responds (presses
					 * OK).
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param msg		the error message
					 *------------------------------------------------------*/
	public static	void		postScrolledErrorDialog(MiPart window, String msg)
		{
		MiMultiLineSingleButtonDialogBox dialog = new MiMultiLineSingleButtonDialogBox(
					window, 
					msg, 
					MiiToolkit.Mi_ERROR_DIALOG_TYPE);
		String result = dialog.popupAndWaitForClose();
		}
					/**------------------------------------------------------
	 				 * Creates a dialog box which prompts the user with an
					 * error icon and the given error message. The current 
					 * thread will wait here until the user responds (presses
					 * OK).
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param msg		the error message
					 *------------------------------------------------------*/
	public static	void		postErrorDialog(MiPart window, String msg)
		{
		MiNativeMessageDialog errorDialog = new MiNativeMessageDialog(
					window,
					Mi_ERROR_DIALOG_TITLE_MSG,
					MiiToolkit.Mi_ERROR_DIALOG_TYPE,
				   	msg,
					true,
					Mi_DIALOG_OK_LABEL,
					null,
					null,
					null,
					0);
		String result = errorDialog.popupAndWaitForClose();
		}
					/**------------------------------------------------------
	 				 * Creates a dialog box which prompts the user with an
					 * error icon and the given error message. The current 
					 * thread will wait here until the user responds (presses
					 * OK).
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param msg		the error message
					 *------------------------------------------------------*/
	public static	void		postErrorDialog(MiEditor window, MiPart msg)
		{
		MiNativeMessageDialog errorDialog = new MiNativeMessageDialog(
					window,
					Mi_ERROR_DIALOG_TITLE_MSG,
					MiiToolkit.Mi_ERROR_DIALOG_TYPE,
				   	msg,
					true,
					Mi_DIALOG_OK_LABEL,
					null,
					null,
					null,
					0);
		String result = errorDialog.popupAndWaitForClose();
		}
					/**------------------------------------------------------
	 				 * Creates a dialog box which prompts the user with an
					 * warning icon and the given query message. The current 
					 * thread will wait here until the user responds (presses
					 * Yes, No or Cancel).
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param title		the dialog box border msg
					 * @param msg		the query message
					 * @param cancelButtonIsDisplayed 
					 *			true if "No" button is to be
					 *			displayed
					 * @return		
					 *    Mi_OK_COMMAND_NAME
					 *    Mi_NOT_OK_COMMAND_NAME (if cancelButtonIsDisplayed)
					 *    Mi_CANCEL_COMMAND_NAME
					 *------------------------------------------------------*/
	public static	String		postWarningDialog(
						MiEditor window, 
						String title, 
						String msg, 
						boolean cancelButtonIsDisplayed)
		{
		String yesLabel = MiSystem.getProperty(Mi_DIALOG_YES_LABEL);
		String noLabel = MiSystem.getProperty(Mi_DIALOG_NO_LABEL);
		String cancelLabel = (cancelButtonIsDisplayed
			? MiSystem.getProperty(Mi_DIALOG_CANCEL_LABEL) : null);
		MiNativeMessageDialog dialog = new MiNativeMessageDialog(
			window,
			title,
			MiiToolkit.Mi_WARNING_DIALOG_TYPE,
			msg,
			true,
			yesLabel,
			noLabel,
			cancelLabel,
			null,
			0);
		String result = dialog.popupAndWaitForClose();
		if (result.equals(yesLabel))
			return(Mi_OK_COMMAND_NAME);
		if (result.equals(noLabel))
			return(Mi_NOT_OK_COMMAND_NAME);
		return(Mi_CANCEL_COMMAND_NAME);
		}
	public static	void		postWarningDialog(
						MiEditor window, 
						String msg)
		{
		MiNativeMessageDialog dialog = new MiNativeMessageDialog(
					window,
					"Warning",
					MiiToolkit.Mi_WARNING_DIALOG_TYPE,
				   	msg,
					true,
					Mi_DIALOG_OK_LABEL,
					null,
					null,
					null,
					0);
		String result = dialog.popupAndWaitForClose();
		}
					/**------------------------------------------------------
	 				 * Creates a dialog box which prompts the user with an
					 * info icon and the given informative message. The current 
					 * thread will wait here until the user responds (presses
					 * OK) if this is modal.
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param title		the dialog box border msg
					 * @param msg		the info message
					 * @param modal		true if this is to be a modal dialog
					 *------------------------------------------------------*/
	public static	void		postInfoDialog(
						MiEditor window, 
						String title, 
						MiPart msg, 
						boolean modal)
		{
		MiNativeMessageDialog dialog = new MiNativeMessageDialog(
			window,
			title,
			MiiToolkit.Mi_INFO_DIALOG_TYPE,
			msg,
			modal,
			Mi_DIALOG_OK_LABEL,
			null,
			null,
			null,
			0);
		if (modal)
			dialog.popupAndWaitForClose();
		else
			dialog.setVisible(true);
		}
					/**------------------------------------------------------
	 				 * Creates a standard 'About' dialog box. The current 
					 * thread will wait here until the user responds (presses
					 * OK) if this is modal.
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param title		the dialog box border msg
					 * @param logo		the comany logo
					 * @param msg		the info message
					 * @param modal		true if this is to be a modal dialog
					 *------------------------------------------------------*/
	public static	void		postAboutDialog(
						MiEditor window, 
						String title, 
						MiPart logo,
						String text,
						boolean modal)
		{
		MiNativeMessageDialog dialog = createAboutDialog(window, title, logo, text, modal);
		if (modal)
			dialog.popupAndWaitForClose();
		else
			dialog.setVisible(true);
		}
					/**------------------------------------------------------
	 				 * Creates a standard 'About' dialog box. 
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param title		the dialog box border msg
					 * @param logo		the comany logo
					 * @param msg		the info message
					 * @param modal		true if this is to be a modal dialog
					 *------------------------------------------------------*/
	public static	MiNativeMessageDialog	createAboutDialog(
							MiEditor window, 
							String title, 
							MiPart logo,
							String text,
							boolean modal)
		{
		MiPart msg = new MiContainer();
		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_NONE);
		msg.setLayout(columnLayout);
		if (logo == null)
			msg.appendPart(MiSystem.getCompanyLogo());
		else
			msg.appendPart(logo);
		MiText label = new MiText(MiSystem.getProperty(title, title));
		label.setFontBold(true);
		label.setFontItalic(true);
		label.setFontPointSize(24);
		msg.appendPart(label);
		label = new MiText(text);
		label.setFontPointSize(14);
		label.setFontHorizontalJustification(Mi_CENTER_JUSTIFIED);
		msg.appendPart(label);

		MiNativeMessageDialog dialog = new MiNativeMessageDialog(
			window,
			"About " + MiSystem.getProperty(title, title),
			MiiToolkit.Mi_ORDINARY_DIALOG_TYPE,
			msg,
			modal,
			Mi_DIALOG_OK_LABEL,
			null,
			null,
			null,
			0);
		return(dialog);
		}
					/**------------------------------------------------------
	 				 * Creates a standard 'Query' dialog box. The current 
					 * thread will wait here until the user responds (presses
					 * OK) because this is modal.
					 * @param window	the parent window the dialog box
					 *			will be displayed in.
					 * @param title		the dialog box border msg
					 * @param msg		the query message
					 * @return		
					 *    Mi_OK_COMMAND_NAME
					 *    Mi_CANCEL_COMMAND_NAME
					 *------------------------------------------------------*/
	public static	String		postQueryDialog(
						MiEditor window, 
						String title, 
						String msg,
						String okStr,
						String notOkStr)
		{
		MiText text = new MiText(msg);
		text.setWordWrapEnabled(true);
		text.setNumDisplayedColumns(100);
		text.setAttributes(MiSystem.getPropertyAttributes(
			MiiToolkit.dialogAttributesPropertyNames[MiiToolkit.Mi_QUERY_DIALOG_TYPE]));
		return(postQueryDialog(window, title, text, okStr, notOkStr));
		}
	public static	String		postQueryDialog(
						MiEditor window, 
						String title, 
						MiPart body)
		{
		String okLabel = MiSystem.getProperty(Mi_DIALOG_OK_LABEL);
		String cancelLabel = MiSystem.getProperty(Mi_DIALOG_CANCEL_LABEL);
		return(postQueryDialog(window, title, body, okLabel, cancelLabel));
		}
	public static	String		postQueryDialog(
						MiEditor window, 
						String title, 
						MiPart body,
						String okStr,
						String notOkStr)
		{
		String okLabel = MiSystem.getProperty(okStr, okStr);
		String cancelLabel = MiSystem.getProperty(notOkStr, notOkStr);
// this is done in MiNativeWindow now 1-2-2002		int numLocks = window.getRootWindow().getCanvas().freeAccessLocks(Thread.currentThread());
		MiNativeMessageDialog dialog = new MiNativeMessageDialog(
			window,
			title,
			MiiToolkit.Mi_QUERY_DIALOG_TYPE,
			body,
			true,
			okLabel,
			cancelLabel,
			null,
			null,
			0);
/*
		if (numLocks > 0)
			{
			window.getRootWindow().getCanvas().getAccessLocks(numLocks);
			}
*/
		String result = dialog.popupAndWaitForClose();
		if (result.equals(okLabel))
			{
			return(Mi_OK_COMMAND_NAME);
			}
		return(Mi_CANCEL_COMMAND_NAME);
		}
	}

