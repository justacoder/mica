
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
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringBufferInputStream;
import java.io.IOException;

/**----------------------------------------------------------------------------------------------
 * This class reads and writes files based on a model entity container
 * by reading/writing each model in the container from/to a single line
 * in the file.
 * Lines are of form:
 * <p>
 *	name = X, property1=value1, proerpty2=value2, ...
 * <p>
 * This can be used for storing preferences or other configuration info.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelPropertiesFileWidget
	{
	private		String				filename;
	private		String				dialogTitle;
	private		String[]			defaults;
	private		MiiModelEntity			model;
	private		MiModelPropertyViewManager 	dialogBox;


	public				MiModelPropertiesFileWidget()
		{
		}
	public				MiModelPropertiesFileWidget(
						String filename, String[] defaults, String dialogTitle)
		{
		load(filename, defaults, dialogTitle);
		}

	public		void		load(String filename, String[] defaults, String dialogTitle)
		{
		this.filename = filename;
		this.defaults = defaults;
		this.dialogTitle = dialogTitle;

		try	{
			FileInputStream inputStream = new FileInputStream(filename);
			MiModelIOFormatManager man = new MiModelIOFormatManager();
			model = man.load(inputStream, filename);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			MiDebug.println("Unable to open configuration file: " + filename);
			MiDebug.println("Using defaults...");

			model = makeDefaults();
			}
		}

	public		void		save()
		{
		try	{
			FileOutputStream outputStream = new FileOutputStream(filename);
			MiModelIOFormatManager man = new MiModelIOFormatManager();
			man.save(model, outputStream, filename);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			MiDebug.println("Unable to save properties file: " + filename);
			}
		}

	public		MiDialogBoxTemplate	getDialogBox(MiEditor parent)
		{
		if (dialogBox == null)
			{
			dialogBox = new MiModelPropertyViewManager(
				new MiDialogBoxTemplate(parent, dialogTitle, false),
				model);
			}
		return(dialogBox.getDialogBox());
		}

	public		MiiModelEntity	getModel()
		{
		return(model);
		}
	public		String		getPropertyValue(String propertyName)
		{
		return(model.getPropertyValue(propertyName));
		}
	public		void		setPropertyValue(String propertyName, String propertyValue)
		{
		model.setPropertyValue(propertyName, propertyValue);
		}

	protected	MiiModelEntity	makeDefaults()
		{
		if (defaults== null)
			{
			return(new MiModelEntity());
			}
		try	{
			StringBufferInputStream inputStream 
				= new StringBufferInputStream(new Strings(defaults).toString());
			MiModelIOFormatManager man = new MiModelIOFormatManager();
			MiiModelEntity doc = man.load(inputStream, "Defaults");
			return(doc);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return(new MiModelEntity());
			}
		}
	}

