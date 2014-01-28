
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
import java.io.*; 

/**----------------------------------------------------------------------------------------------
 * This class creates a graphics editor that supports the loading,
 * displaying, editing and saving of MiiModelDocuments.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphicsEditor extends MiEditor
	{
	private		MiiController	prototypeController;
	private		MiiController	openDocumentController;
	private		String		filename;
//	private		boolean		hasChangedFlag;


					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsEditor.
					 *------------------------------------------------------*/
	public				MiGraphicsEditor()
		{
		// ---------------------------------------------------------------
		// Assure that the viewport knows the universe is defined for this editor,
		// just in case the caller wants to put this in a scrolled box. Otherwise the
		// viewport says the world to universe ratio is always equal to  1.0
		// ---------------------------------------------------------------
		setUniverseBounds(getWorldBounds());

		setViewportSizeLayout(new MiEditorUniverseIncludesAllPartsLayout(true));
		setIsDragAndDropTarget(true);

		appendEventHandler(new MiISelectObjectUnderMouse());
		appendEventHandler(new MiIDragBackgroundPan());
		appendEventHandler(new MiISelectArea());
		appendEventHandler(new MiIDragObjectUnderMouse());
		appendEventHandler(new MiIZoomAroundMouse());
		appendEventHandler(new MiIPan());
		appendEventHandler(new MiIMoveSelectedObjects());
		appendEventHandler(new MiIJumpPan());
		appendEventHandler(new MiIZoomArea());
		appendEventHandler(new MiIDragSelectedObjects());
		// Done by MiDragAndDropManager appendEventHandler(new MiICopyAndDragSelectedObjects());
		// Done by MiDragAndDropManager appendEventHandler(new MiICopyAndDragObjectUnderMouse());

		MiICreateConnection iCreateConn = new MiICreateConnection();
		int[] connPts = new int[1];
		connPts[0] = MiiTypes.Mi_CENTER_LOCATION;
		iCreateConn.getPrototype().setLineWidth(2);
		iCreateConn.getPrototype().setBackgroundColor(MiColorManager.black);
		iCreateConn.getValidConnPointFinder().setValidConnPtLocations(connPts);
		iCreateConn.getValidConnPointFinder().setMethodology(
			MiClosestValidManagedPointFinder.EXAMINE_CONN_POINT_MANAGERS 
			| MiClosestValidManagedPointFinder.EXAMINE_COMMON_POINTS
			| MiClosestValidManagedPointFinder.EXAMINE_PARTS_OF_CANDIDATES);
		appendEventHandler(iCreateConn);

		setBackgroundColor(MiColorManager.veryLightGray);
		}
					/**------------------------------------------------------
	 				 * Sets the default controller to use to load documents with.
					 * Note that this is copied before use because a document may
					 * specify it's own MiIOFormatManager and MiViewManagers. 
					 *
					 * @param controller	the default controller
					 *------------------------------------------------------*/
	public		void		 setPrototypeController(MiiController controller)
		{
		prototypeController = controller;
		if ((controller == openDocumentController) && (controller != null))
			{
			MiDebug.println("Open document controller is being set to prototype document controller - adjusting...");
			//MiDebug.printStackTrace();
			prototypeController = prototypeController.copy();
			}
		}
					/**------------------------------------------------------
	 				 * Gets the manager of the current open document.
					 * @return		the document manager
					 *------------------------------------------------------*/
	public		MiiController	 getOpenDocumentController()
		{
		return(openDocumentController);
		}
					/**------------------------------------------------------
	 				 * Sets the manager of the current open document.
					 * @param controller	the document manager
					 *------------------------------------------------------*/
	public		void		 setOpenDocumentController(MiiController controller)
		{
//MiDebug.printStackTrace(this + "setOpenDocumentController: " + controller + "\nwhen current openDocumentController = " + openDocumentController);
		if (openDocumentController == null)
			{
			// Makes no sense to assign a controller to a non-existant document but this
			// might be called during initialization 
			// 6-22-2003 OR IF DOCUMENT WAS LOADED IN A DIFFERENT EDITOR AND NOW VIEWED IN THIS ONE
			// return;
			}
		if (controller == prototypeController)
			{
			MiDebug.println("Open document controller being set to prototype controller - adjusting...");
			MiDebug.printStackTrace();
			prototypeController = prototypeController.copy();
			}
		openDocumentController = controller;
		}
					/**------------------------------------------------------
	 				 * Saves the contents of the graphics editor.
					 * @param stream 	where to save the contents
					 * @param streamName 	the name of the stream
					 * @return true if successful
					 *------------------------------------------------------*/
	public		boolean		save(OutputStream outputStream, String streamName)
		{
//MiDebug.println(this + "SAVING openDocumentController = " + openDocumentController);
		MiModelStreamIOManager ioManager = new MiModelStreamIOManager();
		ioManager.setOutputStream(outputStream, streamName);
		openDocumentController.setIOManager(ioManager);
		openDocumentController.setView(this);
		try	{
			openDocumentController.save();
			}
		catch (Exception e)
			{
			MiDebug.printStackTrace(e);
			MiToolkit.postScrolledErrorDialog(this, 
				"Unable to save: \"" + streamName + "\" because: \n" 
				+ (e.getMessage() == null ? e.toString() : e.getMessage()));
			return(false);
			}
		return(true);
		}
					/**------------------------------------------------------
	 				 * Loads the contents of the graphics editor from the given
					 * stream. The first line of the stream is examined to 
					 * determine the format of the stream.
					 * @param stream 	where to get the contents
					 *------------------------------------------------------*/
	public		void		load(BufferedInputStream inputStream, String filename) throws IOException
		{
		//removeAllParts();
		//setLayout(new MiSizeOnlyLayout());
//MiDebug.println(this + "LOADING, openDocumentController = " + openDocumentController);
		MiiController origOpenDocumentController = openDocumentController;
		if (prototypeController == null)
			{
			openDocumentController = new MiController();
			}
		else
			{
			openDocumentController = prototypeController.copy();
			}
//MiDebug.println(this + "BEFORE BEFORE LOADING openDocumentController = " + openDocumentController);
		MiModelStreamIOManager ioManager = new MiModelStreamIOManager();
		openDocumentController.setIOManager(ioManager);
		ioManager.setInputStream(inputStream, filename);

		openDocumentController.setView(this);

//MiDebug.println(this + "RIGHT BEFORE LOADING openDocumentController = " + openDocumentController);

		try	{
			openDocumentController.load();
			}
		catch (Exception e)
			{
			openDocumentController.closeOpenDocument();
			openDocumentController = origOpenDocumentController;
			e.printStackTrace();
			MiToolkit.postScrolledErrorDialog(this, 
				((e.getMessage() == null) || (e.getMessage().length() == 0)) 
				? "Error occured during open of\n \"" + filename.replace('\\','/') + "\"" : e.getMessage());
			throw new IOException(e.getMessage());
			}
		if (origOpenDocumentController != null)
			{
			origOpenDocumentController.closeOpenDocument();
			}
//MiDebug.println(this + "AFTER LOADING openDocumentController = " + openDocumentController);
		}
					/**------------------------------------------------------
	 				 * Opens the default, no name, file that the user can
					 * edit in but which needs to be named before saving.
					 *------------------------------------------------------*/
	protected 	 void		prepareToLoadDefaultFile()
		{
		//setLayout(new MiSizeOnlyLayout());
		if (openDocumentController != null)
			{
			openDocumentController.closeOpenDocument();
			}
		openDocumentController = null;
		}
					/**------------------------------------------------------
	 				 * Gets the editing permissions for the currently open document.
					 * @return		the editing permissions
					 *------------------------------------------------------*/
	public		MiEditingPermissions	getEditingPermissions()
		{
		return(openDocumentController.getOpenDocument().getEditingPermissions());
		}
					/**------------------------------------------------------
	 				 * Gets the title of the open document, if any.
					 * @return		the title or null
					 *------------------------------------------------------*/
	public		String		getOpenDocumentTitle()
		{
		return(openDocumentController.getOpenDocument().getTitle());
		}
					/**------------------------------------------------------
	 				 * Sets whether the open document has changed. This is set to
					 * false after a save or open.
					 * @param flag		true if changed.
					 *------------------------------------------------------*/
/*
	public		void		setHasChanged(boolean flag)
		{
		hasChangedFlag = flag; // FIX openDocumentController.setHasChanged(flag);
		}
*/
					/**------------------------------------------------------
	 				 * Gets whether the open document has changed. This is checked
					 * before a open, close or new.
					 * @return 		true if changed.
					 *------------------------------------------------------*/
/*
	public		boolean		getHasChanged()
		{
		return(hasChangedFlag); // FIXopenDocumentController.getHasChanged());
		}
*/
	}


