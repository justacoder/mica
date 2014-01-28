
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
 * Usually assigned to MiEditors (or their layers).
 *
 * Useful for editor contents when the nodes/components/whatever have a layout
 * (for example a MiColumnLayout that contains a image on top and text below). 
 * This then assures that the nodes are set to their preferred size.
 *
 * Some nodes do not have a layout, they are just containers that have various
 * parts placed by a user or the application program. In that case, the preferred
 * size of the node is not easily determinable so one should assign a null layout
 * to the MiEditor.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiSizeOnlyLayout extends MiLayout
	{
	private		MiSize		prefSize	= new MiSize();
	private		MiBounds	outerBounds	= new MiBounds();


	public				MiSizeOnlyLayout()
		{
		}

	public		void		doLayout()
		{
		int num = getTarget().getNumberOfParts();
		for (int i = 0; i < num; ++i)
			{
			MiPart obj = getTarget().getPart(i);
			if (!obj.hasValidLayout())
				{
				// 9-1-2002 added this here because settng a invisible part to visible does
				// not update the obj's bounds and if this obj does not have a layout, then the obj will
				// be scaled larger when it should not be. So we update the bounds manually here.
				// Doing it automatically in MiPart setVisible or updateContainersLayout caused (obvious)
				// problems elsewhere (but this is the way it should be done ... and will be someday)
				obj.refreshBounds();

				obj.getPreferredSize(prefSize);
				obj.getBounds(outerBounds);
				if ((prefSize.width != outerBounds.getWidth()) 
					|| (prefSize.height != outerBounds.getHeight()))
					{
					obj.setSize(prefSize);
					}
				}
			}
		}
						/**
						 * Does not determine preferred or minimum size of the editor.
						 **/
	public		boolean			determinesPreferredAndMinimumSizes()
		{
		return(false);
		}
	}
