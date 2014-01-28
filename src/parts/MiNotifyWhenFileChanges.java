
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
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.LongVector;
import java.io.File;
import java.util.Date;


/**----------------------------------------------------------------------------------------------
 * This class creates a widget that allows a user to browse, find, modify and 
 * optionally choose a file.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiNotifyWhenFileChanges extends MiEventHandler
	{
	public static final int 	Mi_POLLED_FILE_CHANGED_ACTION 	
						= MiActionManager.registerAction("Mi_POLLED_FILE_CHANGED_ACTION");
	public static final int 	Mi_POLLED_FILE_NOT_CHANGED_ACTION 	
						= MiActionManager.registerAction("Mi_POLLED_FILE_NOT_CHANGED_ACTION");

	private		long		timeLastChange;
	private		String		filenameURI;
	private		MiPart		handler;
	private		boolean		fileChanged;


	public				MiNotifyWhenFileChanges(MiPart handler, String filenameURI, long timeLastChange)
		{
		this.handler = handler;
		this.filenameURI = filenameURI;
		this.timeLastChange = timeLastChange;
		}
	public		void		setTimeLastChange(long millis)
		{
		timeLastChange = millis;
		}
	public		long		getTimeLastChange()
		{
		return(timeLastChange);
		}
	public		void		setFilenameURI(String filename)
		{
		filenameURI = filename;
//MiDebug.printStackTrace("setFilenameURI: " + filename);
		}
	public		String		getFilenameURI()
		{
		return(filenameURI);
		}
	public		int		processEvent(MiEvent event)
		{
//MiDebug.println("processEvent - evnet = " +event);
		if ((event.getType() == Mi_TIMER_TICK_EVENT) && (filenameURI != null))
			{
			MiiSystemIOManager ioManager = MiSystem.getIOManager();
			long lastModified = ioManager.lastModified(filenameURI);
//MiDebug.println("filenameURI = " +filenameURI);
//MiDebug.println("lastModified = " +lastModified);
//MiDebug.println("timeLastChange = " +timeLastChange);
//MiDebug.println("System.currentTimeMillis() = " +System.currentTimeMillis());
//MiDebug.println("lastModified - timeLastChange = " + (lastModified - timeLastChange));
			if (lastModified > timeLastChange)
				{
				fileChanged = true;
				timeLastChange = lastModified;
//MiDebug.println("DISPATCH");
				handler.dispatchAction(Mi_POLLED_FILE_CHANGED_ACTION, filenameURI);
				}
			else if (fileChanged)
				{
//MiDebug.println("System.currentTimeMillis() = " +System.currentTimeMillis());
//MiDebug.println("System.currentTimeMillis() = " + (System.currentTimeMillis() - lastModified));
				handler.dispatchAction(Mi_POLLED_FILE_NOT_CHANGED_ACTION, 
					new Integer((int )((System.currentTimeMillis() - lastModified)/1000)));
				}
			}
		return(Mi_PROPOGATE_EVENT);
		}
	}


