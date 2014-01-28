
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

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiBooleanStateBoxRenderer extends MiPartRenderer implements MiiTypes
	{
	private		MiBounds 	objBounds	= new MiBounds();

	public				MiBooleanStateBoxRenderer()
		{
		}

	public		boolean		render(MiPart obj, MiRenderer renderer)
		{
		if (!enabled)
			return(true);

		MiAttributes atts = obj.getAttributes();
		if (obj.isSelected()) // repalce this with a general boolean functor
			{
			if ((obj instanceof MiLine)
				|| (obj instanceof MiConnection))
				{
				attributes.setStaticAttribute(MiiAttributeTypes.Mi_BORDER_LOOK, Mi_RAISED_BORDER_LOOK);
				obj.setAttributes(attributes);
				obj.render(renderer);
				return(false);
				}
			else
				{
				attributes.setStaticAttribute(MiiAttributeTypes.Mi_BORDER_LOOK, Mi_RIDGE_BORDER_LOOK);
				renderer.setAttributes(attributes);
				obj.getBounds(objBounds);
				renderer.drawRect(objBounds);
				}
			}
		return(true);
		}
	}

