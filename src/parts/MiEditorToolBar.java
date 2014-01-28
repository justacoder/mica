
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


/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiEditorToolBar extends MiToolBar implements MiiCommandNames, MiiMessages, MiiDisplayNames, MiiLookProperties
	{
	private		MiiCommandHandler	application;



	protected			MiEditorToolBar()
		{
		// Used for copy operation.
		}

	public		MiPart		copy()
		{
		MiEditorToolBar obj 	= (MiEditorToolBar )super.copy();
		obj.setup(getEditor());
		return(obj);
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
		application = ((MiEditorToolBar )source).application;
		}

	public				MiEditorToolBar(
						MiiCommandHandler application, 
						MiEditor editor, 
						MiiCommandManager manager)
		{
		super(editor, manager);

		this.application = application;

		MiiCommandHandler handler = new MiFileMenuCommands(application, manager);
		appendToolItem(	Mi_SAVE_DISPLAY_NAME,
							new MiImage(Mi_SAVE_ICON_NAME, true),
							handler, 
							Mi_SAVE_COMMAND_NAME);
		appendToolItem(	Mi_PRINT_DISPLAY_NAME,
							new MiImage(Mi_PRINT_ICON_NAME, true),
							handler, 
							Mi_PRINT_COMMAND_NAME);

		handler = new MiEditMenuCommands(false, manager);
		appendToolItem(	Mi_UNDO_DISPLAY_NAME,
							new MiImage(Mi_UNDO_ICON_NAME, true),
							handler, 
							Mi_UNDO_COMMAND_NAME);
		appendToolItem(	Mi_REDO_DISPLAY_NAME, 	
							new MiImage(Mi_REDO_ICON_NAME, true),
							handler, 
							Mi_REDO_COMMAND_NAME);
		appendSpacer();
		appendToolItem(	Mi_CUT_DISPLAY_NAME,
							new MiImage(Mi_CUT_ICON_NAME, true),
							handler, 
							Mi_CUT_COMMAND_NAME);

		appendToolItem(	Mi_COPY_DISPLAY_NAME,
							new MiImage(Mi_COPY_ICON_NAME, true), 
							handler, 
							Mi_COPY_COMMAND_NAME);

		appendToolItem(	Mi_PASTE_DISPLAY_NAME,	
							new MiImage(Mi_PASTE_ICON_NAME, true),
							handler, 
							Mi_PASTE_COMMAND_NAME);
		handler = new MiViewMenuCommands(manager);
		appendSpacer();
		appendToolItem(	Mi_ZOOM_IN_DISPLAY_NAME,
							new MiImage(Mi_ZOOM_IN_ICON_NAME, true),
						 	handler, 
							Mi_ZOOM_IN_COMMAND_NAME);

		appendToolItem(	Mi_ZOOM_OUT_DISPLAY_NAME,
							new MiImage(Mi_ZOOM_OUT_ICON_NAME, true),
						 	handler, 
							Mi_ZOOM_OUT_COMMAND_NAME);

		appendToolItem(	Mi_VIEW_ALL_DISPLAY_NAME,	
							new MiImage(Mi_VIEW_ALL_ICON_NAME, true),
						 	handler, 
							Mi_VIEW_ALL_COMMAND_NAME);

		appendToolItem(	Mi_VIEW_PREVIOUS_DISPLAY_NAME,
							new MiImage(Mi_VIEW_PREVIOUS_ICON_NAME, true),
						 	handler, 
							Mi_VIEW_PREVIOUS_COMMAND_NAME);

		appendToolItem(	Mi_VIEW_NEXT_DISPLAY_NAME,
							new MiImage(Mi_VIEW_NEXT_ICON_NAME, true),
						 	handler, 
							Mi_VIEW_NEXT_COMMAND_NAME);

		appendToolItem(	Mi_ZOOM_HOME_DISPLAY_NAME,
							new MiImage(Mi_ZOOM_HOME_ICON_NAME, true),
						 	handler, 
							Mi_ZOOM_HOME_COMMAND_NAME);
		appendSpacer();

		setToolItemImageSizes(new MiSize(32, 32));

		setHelpMessages(Mi_SAVE_COMMAND_NAME,
				Mi_SAVE_STATUS_HELP_MSG,
				Mi_NO_SAVE_STATUS_HELP_MSG,
				Mi_SAVE_BALLOON_HELP_MSG);

		setHelpMessages(Mi_PRINT_COMMAND_NAME,
				Mi_PRINT_STATUS_HELP_MSG,
				Mi_NO_PRINT_STATUS_HELP_MSG,
				Mi_PRINT_BALLOON_HELP_MSG);

		setHelpMessages(Mi_UNDO_COMMAND_NAME,
				Mi_UNDO_STATUS_HELP_MSG,
				Mi_NO_UNDO_STATUS_HELP_MSG,
				Mi_UNDO_BALLOON_HELP_MSG);

		setHelpMessages(Mi_REDO_COMMAND_NAME,
				Mi_REDO_STATUS_HELP_MSG,
				Mi_NO_REDO_STATUS_HELP_MSG,
				Mi_REDO_BALLOON_HELP_MSG);

		setHelpMessages(Mi_CUT_COMMAND_NAME,
				Mi_CUT_STATUS_HELP_MSG,
				Mi_NO_CUT_STATUS_HELP_MSG,
				Mi_CUT_BALLOON_HELP_MSG);

		setHelpMessages(Mi_COPY_COMMAND_NAME,
				Mi_COPY_STATUS_HELP_MSG,
				Mi_NO_COPY_STATUS_HELP_MSG,
				Mi_COPY_BALLOON_HELP_MSG);

		setHelpMessages(Mi_PASTE_COMMAND_NAME,
				Mi_PASTE_STATUS_HELP_MSG,
				Mi_NO_PASTE_STATUS_HELP_MSG,
				Mi_PASTE_BALLOON_HELP_MSG);

		setHelpMessages(Mi_ZOOM_IN_COMMAND_NAME,
				Mi_ZOOM_IN_STATUS_HELP_MSG,
				Mi_NO_ZOOM_IN_STATUS_HELP_MSG,
				Mi_ZOOM_IN_BALLOON_HELP_MSG);

		setHelpMessages(Mi_ZOOM_OUT_COMMAND_NAME,
				Mi_ZOOM_OUT_STATUS_HELP_MSG,
				Mi_NO_ZOOM_OUT_STATUS_HELP_MSG,
				Mi_ZOOM_OUT_BALLOON_HELP_MSG);

		setHelpMessages(Mi_VIEW_ALL_COMMAND_NAME,
				Mi_VIEW_ALL_STATUS_HELP_MSG,
				Mi_NO_VIEW_ALL_STATUS_HELP_MSG,
				Mi_VIEW_ALL_BALLOON_HELP_MSG);

		setHelpMessages(Mi_VIEW_PREVIOUS_COMMAND_NAME,
				Mi_VIEW_PREVIOUS_STATUS_HELP_MSG,
				Mi_NO_VIEW_PREVIOUS_STATUS_HELP_MSG,
				Mi_VIEW_PREVIOUS_BALLOON_HELP_MSG);

		setHelpMessages(Mi_VIEW_NEXT_COMMAND_NAME,
				Mi_VIEW_NEXT_STATUS_HELP_MSG,
				Mi_NO_VIEW_NEXT_STATUS_HELP_MSG,
				Mi_VIEW_NEXT_BALLOON_HELP_MSG);

		setHelpMessages(Mi_ZOOM_HOME_COMMAND_NAME,
				Mi_ZOOM_HOME_STATUS_HELP_MSG,
				Mi_NO_ZOOM_HOME_STATUS_HELP_MSG,
				Mi_ZOOM_HOME_BALLOON_HELP_MSG);

		}
	}

