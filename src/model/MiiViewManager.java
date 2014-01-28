
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
 * This interface specifies the functionality of look-and-feel managers.
 * These managers convert MiiModelDocuments to and from graphics. To
 * this end they may also watch for actions made to the graphics and
 * so update the document accordingly.
 * <p>
 * This interface extends MiiModelEntity, a rather heavy interface, in
 * order to support the future capability of view managers having properties
 * that allow customization of the view(s) that they generate. Implementers
 * will most likely inherit their view managers from MiModelEntity, which
 * supplies a default working implementation of MiiModelEntity.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiViewManager extends MiiModelEntity
	{
				/**------------------------------------------------------
	 			 * Sets the view container. This container will be loaded
				 * with graphics by the #setModel command and will be
				 * converted to a MiiModelDocument by the #getModel 
				 * command.
				 * @param view		the view container
				 * @see 		#getView
				 *------------------------------------------------------*/
	void			setView(MiPart view);


				/**------------------------------------------------------
	 			 * Gets the view container. 
				 *------------------------------------------------------*/
	MiPart			getView();

				/**------------------------------------------------------
	 			 * Loads the target view container with graphics representing
				 * the data in the given document.
				 * @param document	the document
				 * @see 		#setView
				 *------------------------------------------------------*/
	void			setModel(MiiModelEntity document);


				/**------------------------------------------------------
				 * Converts the graphics found in the target view container to 
				 * a MiiModelDocument.
				 * @return 		the document
				 * @see 		#setView
				 *------------------------------------------------------*/
	MiiModelEntity	 	getModel();

				/**------------------------------------------------------
				 * Sets whether the document assigned to this manager has
				 * been changed by the user while editing the graphics.
				 * This is usually just used to clear the changed flag
				 * when the document has just been saved.
				 * @param flag 		true if changed
				 *------------------------------------------------------*/
	//void			setHasChanged(boolean flag);


				/**------------------------------------------------------
				 * Gets whether the document assigned to this manager has
				 * been changed by the user while editing the graphics.
				 * @return 		true if changed
				 *------------------------------------------------------*/
//	boolean			getHasChanged();
	}

