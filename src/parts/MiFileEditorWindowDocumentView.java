
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
import java.util.Vector;


/**----------------------------------------------------------------------------------------------
 * Palette, ...
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiFileEditorWindowDocumentView
	{
	private		String		openFilename;
	private		String		windowBorderTitle;
	private		MiiController	controller;
	private		MiEditor	editor;
	private		MiFileChooser	openFileChooser;
	private		MiFileChooser	saveAsFileChooser;
	private		boolean		saveSavesAllContainingDocuments = false;
	private		boolean		saveAsSavesViewToOwnFile	= true;
	private		boolean		closeClosesAllContainingDocuments= false;
	private		MiFileEditorWindowDocumentView container;
	private		Vector		subViews;
	private		boolean		changedSinceBackup;
	private		boolean		changedSinceSave;
	private		boolean		isOpenFilenameReadOnly;
	private		String		backupFilename;
	private		String		saveAsFilename;

	public				MiFileEditorWindowDocumentView()
		{
		}

	public		void		setOpenFilename(String name)
		{
//MiDebug.printStackTrace(this + "setOpenFilename: " + name + ", was=" + openFilename);
		openFilename = name;
		}
	public		String		getOpenFilename()
		{
		return(openFilename);
		}

	public		void		setWindowBorderTitle(String title)
		{
//MiDebug.printStackTrace(this + "setWindowBorderTitle: " + title + ", was=" + windowBorderTitle);
		windowBorderTitle = title;
		}
	public		String		getWindowBorderTitle()
		{
		return(windowBorderTitle);
		}

	public		void		setController(MiiController controller)
		{
		this.controller = controller;
		}
	public		MiiController	getController()
		{
		return(controller);
		}

	public		void		setEditor(MiEditor editor)
		{
		this.editor = editor;
		}
	public		MiEditor	getEditor()
		{
		return(editor);
		}

	public		void		setSaveAsFileChooser(MiFileChooser fileChooser)
		{
		this.saveAsFileChooser = fileChooser;
		}
	public		MiFileChooser	getSaveAsFileChooser()
		{
		return(saveAsFileChooser);
		}
	public		void		setOpenFileChooser(MiFileChooser fileChooser)
		{
		this.openFileChooser = fileChooser;
		}
	public		MiFileChooser	getOpenFileChooser()
		{
		return(openFileChooser);
		}
	public		void		setChangedSinceBackup(boolean flag)
		{
//if (changedSinceBackup != flag) MiDebug.printStackTrace(this + "setChangedSinceBackup: " + flag);
		changedSinceBackup = flag;
		}
	public		boolean		isChangedSinceBackup()
		{
//MiDebug.printStackTrace(this + "GETChangedSinceBackup: " + changedSinceBackup);
		return(changedSinceBackup);
		}

	public		void		setChangedSinceSave(boolean flag)
		{
//if (changedSinceSave != flag) MiDebug.printStackTrace(this + "setChangedSinceSave: " + flag);
		changedSinceSave = flag;
		}
	public		boolean		isChangedSinceSave()
		{
		return(changedSinceSave);
		}
	public		void		setHasChanged(boolean flag)
		{
//if (changedSinceBackup != flag) MiDebug.printStackTrace(this + "setHasChanged: " + flag);
		changedSinceSave = flag;
		changedSinceBackup = flag;
		}

	public		void		setBackupFilename(String filename)
		{
//MiDebug.printStackTrace(this + " setBackupFilename= " + filename + ", was: "+ backupFilename);
		backupFilename = filename;
		}
	public		String		getBackupFilename()
		{
		return(backupFilename);
		}
	public		void		setSaveAsFilename(String filename)
		{
		saveAsFilename = filename;
		}
	public		String		getSaveAsFilename()
		{
		return(saveAsFilename);
		}
					/**
					 * Gets whether this filename can be saved to
					 **/
	public		boolean		isOpenFilenameReadOnly()
		{
		return(isOpenFilenameReadOnly);
		}
	public		void		setOpenFilenameReadOnly(boolean flag)
		{
//MiDebug.printStackTrace(this + " ----------setOpenFilenameReadOnly: " + flag);
		isOpenFilenameReadOnly = flag;
		}
	public		MiFileEditorWindowDocumentView	getContainer()
		{
		return(container);
		}
	public		int		getNumberOfSubViews()
		{
		return((subViews == null) ? 0 : subViews.size());
		}
	public		MiFileEditorWindowDocumentView	getSubView(int index)
		{
		return((MiFileEditorWindowDocumentView )subViews.elementAt(index));
		}
	public		void		removeSubView(MiFileEditorWindowDocumentView view)
		{
		subViews.removeElement(view);
		view.container = null;
		}
	public		void		appendSubView(MiFileEditorWindowDocumentView view)
		{
		if (subViews == null)
			{
			subViews = new Vector();
			}
		subViews.addElement(view);
		view.container = this;
		}
	public		void		removeSelf()
		{
		if (container != null)
			{
			container.removeSubView(this);
			container = null;
			}
		}
	public		String		toString()
		{
		return(super.toString() + "[openFilename=" + openFilename 
			+ ",controller=" + controller + ",editor=" + editor 
			+ (openFileChooser != null ? ",openFileChooser=" + openFileChooser : "") 
			+ (windowBorderTitle != null ? ",windowBorderTitle=" + windowBorderTitle : "") 
			+ (saveAsFileChooser != null ? ",saveAsFileChooser=" + saveAsFileChooser : "") + "]");
		}
	}




