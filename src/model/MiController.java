
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
import java.util.Hashtable; 
import java.io.*; 


/**----------------------------------------------------------------------------------------------
 * This class manages the saving an loading of MiiModelEntitys and
 * converting them to/from graphics objects.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiController implements MiiController
	{
	public static final String	Mi_MODEL_FORMATTED_FILE_IDENTIFIER 	= "<!Mica-FormattedFile";
	public static final String	Mi_MODEL_FILE_IO_MANAGER_NAME 		= "IOFormatManager";
	public static final String	Mi_MODEL_VIEW_MANAGER_NAME 		= "ViewManager";

	private static final String	Mi_NOT_A_MICA_FORMATTED_FILE_MSG 	= "Not a Mica-formatted file";

	private		MiPart				view;
	private		MiiViewManager 			viewManager;
	private		MiiModelEntity			openDocument;
	private		MiiModelIOFormatManager		ioFormatManager;
	private		MiiModelIOManager		ioManager;
	private		boolean				cacheEnabled;
	private		boolean				requiresMicaFormattedFiles;
	private		Hashtable			cache;

					/**------------------------------------------------------
	 				 * Constructs a new MiController.
					 *------------------------------------------------------*/
	public				MiController()
		{
		setIOManager(new MiModelStreamIOManager());
		setIOFormatManager(new MiModelIOFormatManager());
		setViewManager(new MiGraphicsViewManager());
		}
					/**------------------------------------------------------
	 				 * Copies this controller. 
					 * @return 	the copy
					 *------------------------------------------------------*/
	public		MiiController	copy()
		{
		MiController c = new MiController();
		c.copy(this);
		return(c);
		}
	protected	void		copy(MiController other)
		{
		view = other.view;
		viewManager = other.viewManager;
		openDocument = other.openDocument;
		ioFormatManager = other.ioFormatManager;
		ioManager = other.ioManager;
		setCacheEnabled(other.cacheEnabled);
		}
	public		void		setRequiresMicaFormattedFiles(boolean flag)
		{
		requiresMicaFormattedFiles = flag;
		}
					// Wouldn't need this is could reset anykind of
					// input files or require buffered files
	public		boolean		getRequiresMicaFormattedFiles()
		{
		return(requiresMicaFormattedFiles);
		}
	public		void		setCacheEnabled(boolean flag)
		{
		cacheEnabled = flag;
		if (flag)
			cache = new Hashtable(11);
		else
			cache = null;
		}
	public		boolean		isCacheEnabled()
		{
		return(cacheEnabled);
		}
					/**------------------------------------------------------
	 				 * Sets the document this is managing and hands off the doc
					 * to the ViewManager, if any, which populates the view it
					 * manages with a graphics representation of the doc.
					 * @param doc		the open document
					 *------------------------------------------------------*/
	public		void		setOpenDocument(MiiModelEntity doc)
		{
		openDocument = doc;
//MiDebug.println(this + ".viewManager = " + viewManager);
		viewManager.setModel(doc);
		}
					/**------------------------------------------------------
	 				 * Gets the document this is managing.
					 * @return		the open document
					 *------------------------------------------------------*/
	public		MiiModelEntity	getOpenDocument()
		{
		return(openDocument);
		}
					/**------------------------------------------------------
	 				 * Closes the document this is managing.
					 * @return		the open document
					 *------------------------------------------------------*/
	public		void		closeOpenDocument()
		{
		// Since we don't make copies of viewManagers... this sets ALL viewManagers.view = null;
		//if ((viewManager != null) && (viewManager != manager))
			//viewManager.setView(null);
		// we may want to open another document in this view using this controller later...setViewManager(null);
		}
					/**------------------------------------------------------
	 				 * Sets the IO manager.
					 * @param manager	the IO manager
					 *------------------------------------------------------*/
	public		void		setIOFormatManager(MiiModelIOFormatManager manager)
		{
		ioFormatManager = manager;
		}
					/**------------------------------------------------------
	 				 * Gets the IO manager.
					 * @return 		the IO manager
					 *------------------------------------------------------*/
	public		MiiModelIOFormatManager getIOFormatManager()
		{
		return(ioFormatManager);
		}
					/**------------------------------------------------------
	 				 * Sets the view manager.
					 * @param manager	the view manager
					 *------------------------------------------------------*/
	public		void		setViewManager(MiiViewManager manager)
		{
		if (viewManager != null)
			viewManager.setView(null);
//MiDebug.printStackTrace(this + ".setViewManagger = " + manager + ", was=" + viewManager);
		viewManager = manager;
		if ((viewManager != null) && (view != null))
			viewManager.setView(view);
		}
					/**------------------------------------------------------
	 				 * Gets the view manager.
					 * @return		the managers
					 *------------------------------------------------------*/
	public		MiiViewManager	getViewManager()
		{
		return(viewManager);
		}
	public		void		setIOManager(MiiModelIOManager manager)
		{
		ioManager = manager;
		}
	public		MiiModelIOManager getIOManager()
		{
		return(ioManager);
		}
	public		void		setView(MiPart view)
		{
		this.view = view;
		if (viewManager != null)
			viewManager.setView(view);
		}
	public		MiPart		getView()
		{
		return(view);
		}
/*
	public		void		appendChangeHandler(...)
		{
		}
*/
					/**------------------------------------------------------
	 				 * Loads the view with graphics generated by the viewManager
					 * from the model generated by the ioFormatManager from
					 * the data loaded by the ioManager.
					 *------------------------------------------------------*/
	public		void		load() throws Exception
		{
		if (ioManager == null)
			throw new IOException(this + ": No I/O manager specified");

		MiiModelEntity doc = null;

		if ((!cacheEnabled) 
			|| ((doc = (MiiModelEntity )cache.get(ioManager.getName())) == null))
			{
			BufferedInputStream inputStream = ioManager.getInputStream();

			inputStream.mark(Integer.MAX_VALUE);
			String error = getManagersFromFormattedDataHeader(inputStream, ioManager.getName());
			if (error != null)
				{
				if ((requiresMicaFormattedFiles) || (error != Mi_NOT_A_MICA_FORMATTED_FILE_MSG))
					throw new IOException(ioManager.getName() + ":" + error);
				}
			inputStream.reset();
			doc = ioFormatManager.load(inputStream, ioManager.getName());
			doc.setLocation(ioManager.getName());
			}
		openDocument = doc;

		viewManager.setView(view);
		viewManager.setModel(openDocument);
		if (cacheEnabled)
			cache.put(ioManager.getName(), openDocument);
		}
					/**------------------------------------------------------
	 				 * Saves the contents of the previously assigned target.
					 * @param outputStream 	where to save the contents
					 * @param streamName 	the name of the stream
					 *------------------------------------------------------*/
	public		void		save() throws Exception
		{
		MiiModelEntity document = viewManager.getModel();
		if (cacheEnabled)
			cache.remove(ioManager.getName());
		OutputStream outputStream = ioManager.getOutputStream();
		ioFormatManager.save(document, outputStream, makeHeader());
		}
					/**------------------------------------------------------
	 				 * Saves the contents of the previously assigned target.
					 * @param outputStream 	where to save the contents
					 * @param streamName 	the name of the stream
					 *------------------------------------------------------*/
	public		void		saveAs( MiiModelIOFormatManager outputIOFormatManager,
						MiiModelIOManager outputIOManager) throws Exception
		{
		MiiModelEntity document = viewManager.getModel();
		OutputStream outputStream = outputIOManager.getOutputStream();
		outputIOFormatManager.save(document, outputStream, makeHeader());
		}
					/**------------------------------------------------------
	 				 * Examines the given stream to determine the format of
					 * the data in the stream and from this assigns IO and
					 * look-and-feel managers to this.
					 * @param inputStream 	where to save the contents
					 * @param streamName 	the name of the stream
					 *------------------------------------------------------*/
	protected	String		getManagersFromFormattedDataHeader(
						InputStream inputStream, String streamName)
						throws IOException
		{
		//viewManager = null;
		//ioFormatManager = null;

		// BROKEN: jdk1.2beta2 reading just one line trahses the stream for subsequent 
		// reader creations and read lines . Use 1.0.2 API
		//BufferedReader stream = new BufferedReader(new InputStreamReader(inputStream));

		DataInputStream stream = new DataInputStream(inputStream);
		String line = null;
		int lineNumber = 0;
		while ((line = stream.readLine()) != null)
			{
			++lineNumber;
			if (line.startsWith(Mi_MODEL_FORMATTED_FILE_IDENTIFIER))
				{
				line = line.substring(Mi_MODEL_FORMATTED_FILE_IDENTIFIER.length());
				line = Utility.replaceAll(line, ">", "");
				MiModelEntity entity = MiModelIOFormatManager.makeModelEntityFromString(
					line, streamName, lineNumber);

				String value = entity.getPropertyValue(Mi_MODEL_FILE_IO_MANAGER_NAME);
				if (value != null)
					{
					Object manager = Utility.makeInstanceOfClass(value);
					if (manager == null)
						{
						return(Mi_MODEL_FILE_IO_MANAGER_NAME
							+ " was not found: " + value);
						}
					if (!(manager instanceof MiiModelIOFormatManager))
						{
						return(Mi_MODEL_FILE_IO_MANAGER_NAME
							+ " has invalid class type: " + value);
						}
					setIOFormatManager((MiiModelIOFormatManager )manager);
					}
				value = entity.getPropertyValue(Mi_MODEL_VIEW_MANAGER_NAME);
				if (value != null)
					{
					Object manager = Utility.makeInstanceOfClass(value);
					if (manager == null)
						{
						return(Mi_MODEL_VIEW_MANAGER_NAME
							+ " was not found: " + value);
						}
					if (!(manager instanceof MiiViewManager))
						{
						return(Mi_MODEL_VIEW_MANAGER_NAME
							+ " has invalid class type: " + value);
						}
					setViewManager((MiiViewManager )manager);
					}
				if (viewManager == null)
					{
					return("A View Manager was not found.");
					}
				if (ioFormatManager == null)
					{
					return("An I/O Manager was not found.");
					}
		
				return(null);
				}
			}
		return(Mi_NOT_A_MICA_FORMATTED_FILE_MSG);
		}
	public		String		makeHeader()
		{
		return(makeFormattedDataHeaderFromManagers(viewManager, ioFormatManager));
		}
	public		String		makeFormattedDataHeaderFromManagers(
						MiiViewManager viewManager, MiiModelIOFormatManager ioFormatManager)
		{
		String header = Mi_MODEL_FORMATTED_FILE_IDENTIFIER;
		if (ioFormatManager != null)
			{
			header += " " + Mi_MODEL_FILE_IO_MANAGER_NAME 
					+ "=" + ioFormatManager.getClass().getName();
			}
		if (viewManager != null)
			{
			if (ioFormatManager != null)
				header += ", ";
			header += " " + Mi_MODEL_VIEW_MANAGER_NAME 
					+ "=" + viewManager.getClass().getName();
			}
		return(header + ">");
		}
	public		String		toString()
		{
		String str = super.toString();
		str += "{view=" + view + ",document=" + openDocument + ",viewManager=" + viewManager + ",ioFormatManager=" + ioFormatManager + ",ioManager=" + ioManager + "}";
		return(str);
		}
	}


