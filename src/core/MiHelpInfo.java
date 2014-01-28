
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
 * A basic implementation of the MiiHelpInfo interface.
 * <a>
 * This class is used to specify ToolHints, StatusBar help, etc. and
 * allows the programmer to specify unique color and fonts for each 
 * help message.
 * 
 * @see MiPart#setToolHintHelp
 * @see MiPart#setBalloonHelp
 * @see MiPart#setBalloonHelp
 * @see MiPart#setDialogHelp
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiHelpInfo implements MiiHelpInfo
	{
					/**------------------------------------------------------
					 * A special MiHelpInfo instance that indicates that the
					 * user, for example the status bar, is to just ignore
					 * this help message. A null message will cause the status
					 * bar to be cleared to no message at all. This is often
					 * used for the backgrounds of dialogs and menus so that
					 * the status bar does not flash while the user is
					 * browsing these container widgets.
					 *------------------------------------------------------*/
	public static final MiHelpInfo	ignoreThis = new MiHelpInfo("Ignore this");
	public static final MiHelpInfo	noneForThis = new MiHelpInfo("None for this");

	private		String		message;
	private		MiAttributes	attributes;
	private		boolean		enabled		= true;



					/**------------------------------------------------------
					 * Contructs a new MiHelpInfo.
					 * @param msg		the helpful text message
					 *------------------------------------------------------*/
	public				MiHelpInfo(String msg)
		{
		this(msg, null);
		}
					/**------------------------------------------------------
					 * Contructs a new MiHelpInfo.
					 * @param msg		the helpful text message
					 * @param atts		the attributes to use to display 
					 * 			the message
					 *------------------------------------------------------*/
	public				MiHelpInfo(String msg, MiAttributes atts)
		{
		message = msg;
		attributes = atts;
		}
					/**------------------------------------------------------
			 		* Sets the helpful message to display.
			 		* @param text		the message
			 		* @implements 		MiiHelpInfo
			 		*------------------------------------------------------*/
	public		void		setMessage(String msg)
		{
		message = msg;
		}
					/**------------------------------------------------------
			 		* Gets the helpful message to display.
			 		* @return		the message
			 		* @implements 		MiiHelpInfo
			 		*------------------------------------------------------*/
	public		String		getMessage()
		{
		return(message);
		}
					/**------------------------------------------------------
			 		* Sets the attributes of the message display. This permits
			 		* the message font, message color, help dialog background
			 		* color and more to be controlled for each help message.
			 		* @param attributes	the attributes
			 		* @implements 		MiiHelpInfo
			 		*------------------------------------------------------*/
	public		void		setAttributes(MiAttributes atts)
		{
		attributes = atts;
		}
					/**------------------------------------------------------
			 		* Gets the attributes of the message display.
			 		* @return		the attributes of the message
			 		* @implements 		MiiHelpInfo
			 		*------------------------------------------------------*/
	public		MiAttributes	getAttributes()
		{
		return(attributes);
		}
					/**------------------------------------------------------
			 		* Sets whether this help is enabled.
			 		* @param flag		true if this help message is enabled
			 		* @implements 		MiiHelpInfo
			 		*------------------------------------------------------*/
	public		void		setEnabled(boolean flag)
		{
		enabled = flag;
		}
					/**------------------------------------------------------
			 		* Gets whether this help is enabled.
			 		* @return 		true if this help message is enabled
			 		* @implements 		MiiHelpInfo
			 		*------------------------------------------------------*/
	public		boolean		isEnabled()
		{
		return(enabled);
		}
					/**------------------------------------------------------
					 * Prints information about this class.
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) + ": message = " + message 
			+ ((attributes != null) ? ("(" + attributes + ")") : ""));
		}
	}

