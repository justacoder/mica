
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
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public interface MiiHelpInfo
	{
			/**------------------------------------------------------
			 * Sets the helpful message to display.
			 * @param text		the message
			 *------------------------------------------------------*/
	void		setMessage(String text);

			/**------------------------------------------------------
			 * Gets the helpful message to display.
			 * @return		the message
			 *------------------------------------------------------*/
	String		getMessage();

			/**------------------------------------------------------
			 * Sets the attributes of the message display. This permits
			 * the message font, message color, help dialog background
			 * color and more to be controlled for each help message.
			 * @param attributes	the attributes
			 *------------------------------------------------------*/
	void		setAttributes(MiAttributes attributes);


			/**------------------------------------------------------
			 * Gets the attributes of the message display.
			 * @return		the attributes of the message
			 *------------------------------------------------------*/
	MiAttributes	getAttributes();

			/**------------------------------------------------------
			 * Sets whether this help is enabled.
			 * @param flag		true if this help message is enabled
			 *------------------------------------------------------*/
	void		setEnabled(boolean flag);

			/**------------------------------------------------------
			 * Gets whether this help is enabled.
			 * @return 		true if this help message is enabled
			 *------------------------------------------------------*/
	boolean		isEnabled();
	}

