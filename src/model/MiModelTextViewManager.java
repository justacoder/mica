
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

/**----------------------------------------------------------------------------------------------
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelTextViewManager extends MiModelEntity implements MiiViewManager
	{
	private		MiPart		view;
	private		MiiModelEntity	model;

					/**------------------------------------------------------
		 			 * Sets the view container. This container will be loaded
					 * with graphics by the #setModel command and will be
					 * converted to a MiiModelDocument by the #getModel 
					 * command.
					 * @param view		the view container
					 * @see 		#getView
					 *------------------------------------------------------*/
	public		void		setView(MiPart view)
		{
		this.view = view;
		}
					/**------------------------------------------------------
		 			 * Gets the view container. 
					 *------------------------------------------------------*/
	public		MiPart		getView()
		{
		return(view);
		}
					/**------------------------------------------------------
		 			 * Loads the target view container with graphics representing
					 * the data in the given document.
					 * @param document	the document
					 * @see 		#setView
					 *------------------------------------------------------*/
	public		void		setModel(MiiModelEntity document)
		{
		this.model = document;
		view.appendPart(new MiLabel(MiModelEntity.dump(model)));
		}
					/**------------------------------------------------------
					 * Converts the graphics found in the target view container to 
					 * a MiiModelDocument.
					 * @return 		the document
					 * @see 		#setView
					 *------------------------------------------------------*/
	public		MiiModelEntity	getModel()
		{
		return(model);
		}
	}


