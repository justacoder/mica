
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
import com.swfm.mica.util.CaselessKeyHashtable;
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Tree; 
import com.swfm.mica.util.Utility; 
import java.util.StringTokenizer; 
import java.util.Date; 
import java.util.Enumeration; 
import java.util.Hashtable; 

/**----------------------------------------------------------------------------------------------
 * This class implements the MiiViewManager interface.
 * It supports a wide range of graphics typically used in graphics editors
 * and palettes. It assumes that the precise type of the target will be taken
 * into account target by a subclass of this abstract class.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public abstract class MiViewManagerPostGraphicsCreationHandler
	{
	private		MiViewManagerPostGraphicsCreationHandler	nextHandler;

	public abstract	void		created(MiParts parts, MiContainer paletteParts);
	public 		void		notifyHandlers(MiParts parts, MiContainer paletteParts)
		{
		created(parts, paletteParts);
		if (nextHandler != null)
			{
			nextHandler.notifyHandlers(parts, paletteParts);
			}
		}
	public		void		setNextHandler(MiViewManagerPostGraphicsCreationHandler handler)
		{
		nextHandler = handler;
		}
	public		MiViewManagerPostGraphicsCreationHandler getNextHandler()
		{
		return(nextHandler);
		}
	}


