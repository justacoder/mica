
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

public class MiCommandLineTextField extends MiTextField
	{
	private		boolean			clearTextNextKeyStroke;
	private		MiFileEditorWindow	window;
	private		MiPanAndZoomAnimator 	panAndZoomAnimator;
	private		MiSearchManager		searchManager;


	public				MiCommandLineTextField(MiFileEditorWindow window)
		{
		this.window = window;

		searchManager = new MiSearchManager(window.getEditor());

		setBackgroundColor(MiColorManager.white);
		setAcceptingKeyboardFocus(true);
		setContextCursor(Mi_TEXT_CURSOR);
		setAcceptingEnterKeyFocus(true);
		appendActionHandler(this, Mi_ENTER_KEY_ACTION);
		appendActionHandler(this, Mi_GOT_KEYBOARD_FOCUS_ACTION);
		appendActionHandler(this, Mi_LOST_KEYBOARD_FOCUS_ACTION);
		appendActionHandler(this, Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE);
		setToolHintMessage(
			  "/<text>           - search foward\n"
			+ "save              - save\n"
			+ "save <filename>   - save to file\n"
			+ "open <filename>   - open file"
			);
		}

	public		boolean		processAction(MiiAction action)
		{
		if (action.hasActionType(Mi_LOST_KEYBOARD_FOCUS_ACTION))
			{
			getContainingWindow().getKeyboardFocusManager().clearEnterKeyFocus();
			}
		else if (action.hasActionType(Mi_GOT_KEYBOARD_FOCUS_ACTION))
			{
			setValue("");
			}
		else if (action.hasActionType(Mi_ENTER_KEY_ACTION))
			{
			String cmd = getValue();
			cmd = executeCommand(cmd);
			setValue(cmd);
			clearTextNextKeyStroke = true;
			}
		else if (action.hasActionType(Mi_TEXT_CHANGE_ACTION | Mi_REQUEST_ACTION_PHASE))
			{
			if (clearTextNextKeyStroke)
				{
				if ((getCursorPosition() == 0) && (getValue().length() > 0))
					setValue("" + getValue().charAt(0));
				clearTextNextKeyStroke = false;
				}
			}
			
		return(true);
		}
	protected	String		executeCommand(String cmd)
		{
		if (Utility.isEmptyOrNull(cmd))
			return(cmd);

		MiEditor editor = window.getEditor();
		if (cmd.equals("/"))
			{
//System.out.println("SEARCH FOWARD");
			editor.deSelectAll();
			MiPart part = searchManager.searchFowardAgain();
//System.out.println("Found p[art : " + part);
			if (part != null)
				{
				part = MiUtility.getManipulatableContainerOfPart(editor, part);
				editor.select(part);
				panToDisplayPart(part);
				return("search for \"" + searchManager.getSearchText() + "\" SUCCEEDED");
				}
			return("search for \"" + searchManager.getSearchText() + "\" FAILED");
			}
		else if (cmd.equals("?"))
			{
//System.out.println("SEARCH BACKAWARD");
			editor.deSelectAll();
			MiPart part = searchManager.searchBackwardAgain();
//System.out.println("Found p[art : " + part);
			if (part != null)
				{
				part = MiUtility.getManipulatableContainerOfPart(editor, part);
				editor.select(part);
				panToDisplayPart(part);
				return("search for \"" + searchManager.getSearchText() + "\" SUCCEEDED");
				}
			return("search for \"" + searchManager.getSearchText() + "\" FAILED");
			}
		else if (cmd.charAt(0) == '/')
			{
//System.out.println("SEARCH FOWARD");
			editor.deSelectAll();
			String searchText = cmd.substring(1);
			searchManager = new MiSearchManager(editor);
			MiPart part = searchManager.searchFoward(searchText);
//System.out.println("Found part : " + part);
			if (part != null)
				{
				part = MiUtility.getManipulatableContainerOfPart(editor, part);
				editor.select(part);
				panToDisplayPart(part);
				return("search for \"" + searchManager.getSearchText() + "\" SUCCEEDED");
				}
			return("search for \"" + searchManager.getSearchText() + "\" FAILED");
			}
		else if (cmd.charAt(0) == '?')
			{
//System.out.println("SEARCH BACKWARD");
			editor.deSelectAll();
			String searchText = cmd.substring(1);
			searchManager = new MiSearchManager(editor);
			MiPart part = searchManager.searchBackward(searchText);
//System.out.println("Found part : " + part);
			if (part != null)
				{
				part = MiUtility.getManipulatableContainerOfPart(editor, part);
				editor.select(part);
				panToDisplayPart(part);
				return("search for \"" + searchManager.getSearchText() + "\" SUCCEEDED");
				}
			return("search for \"" + searchManager.getSearchText() + "\" FAILED");
			}
		else if (cmd.startsWith("s "))
			{
			String filename = cmd.substring("s ".length()).trim();

			boolean flag = window.getWarnIfSaveToExistingFile();
			window.setWarnIfSaveToExistingFile(true);
			boolean status =  window.saveFile(filename);
			window.setWarnIfSaveToExistingFile(flag);

			if (status)
				return("saving to " + filename + " SUCCEEDED");
			return("saving to " + filename + " CANCELLED");
			}
		else if ((cmd.equals("s")) || (cmd.equals("save")))
			{
			boolean flag = window.getWarnIfSaveToExistingFile();
			window.setWarnIfSaveToExistingFile(true);
			boolean status =  window.save();
			window.setWarnIfSaveToExistingFile(flag);

			if (status)
				return("saved");
			return("save FAILED");
			}
		else if (cmd.startsWith("save "))
			{
			String filename = cmd.substring("save ".length()).trim();

			boolean flag = window.getWarnIfSaveToExistingFile();
			window.setWarnIfSaveToExistingFile(true);
			boolean status =  window.saveFile(filename);
			window.setWarnIfSaveToExistingFile(flag);

			if (status)
				return("saving to " + filename + " SUCCEEDED");
			return("saving to " + filename + " CANCELLED");
			}
		else if (cmd.startsWith("o "))
			{
			return(openFile(cmd.substring("o ".length()).trim()));
			}
		else if (cmd.startsWith("open "))
			{
			return(openFile(cmd.substring("open ".length()).trim()));
			}
		else
			{
			return("Unknown command: " + cmd);
			}
		}
	protected	void		panToDisplayPart(MiPart part)
		{
		MiEditor editor = window.getEditor();
		MiPoint pt = part.getCenter();
		MiBounds world = editor.getWorldBounds();
		world.setCenter(pt);
		panAndZoomAnimator = new MiPanAndZoomAnimator(editor);
		panAndZoomAnimator.setWorld(world);
		}
	protected	String		openFile(String filename)
		{
		if (!window.verifyLossOfAnyChanges())
			return("opening of " + filename + " CANCELLED");

		if (window.openFile(filename))
			return("opening of " + filename + " SUCCEEDED");
		return("opening of " + filename + " FAILED");
		}
	}

 
