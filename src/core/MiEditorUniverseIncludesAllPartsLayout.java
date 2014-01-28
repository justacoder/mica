
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
 * This class, when assigned to MiEditors (using the MiEditor#setViewportSizeLayout
 * method), assures that all graphics in the editor are always visible (i.e. not 
 * scrolled/panned out of view). Use the #setCenterUniverseOnCenterOfContents method
 * for further refinement.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorUniverseIncludesAllPartsLayout implements MiiEditorViewportSizeLayout
	{
	private		MiEditor	editor;
	private		MiMargins 	universeMargins	 	= new MiMargins(20);
	private		MiBounds 	contentsBounds 		= new MiBounds();
	private		MiBounds 	universeBounds	 	= new MiBounds();
	private		boolean		centerUniverseOnCenterOfContents= false;
	private		boolean		canBeLargerThanSizeOfContent	= true;




	public				MiEditorUniverseIncludesAllPartsLayout(
						boolean canBeLargerThanSizeOfContent)
		{
		this.canBeLargerThanSizeOfContent = canBeLargerThanSizeOfContent;
		}

	public		void		setTarget(MiEditor target)
		{
		editor = target;
		}

	public		void		setUniverseBoundsMargins(MiMargins margins)
		{
		universeMargins = margins;
		}
	public		MiMargins	getUniverseBoundsMargins()
		{
		return(universeMargins);
		}
	public		void		setCenterUniverseOnCenterOfContents(boolean flag)
		{
		centerUniverseOnCenterOfContents = flag;
		}
	public		boolean		getCenterUniverseOnCenterOfContents()
		{
		return(centerUniverseOnCenterOfContents);
		}

	public		void		validateLayout()
		{
		getPreferredSizeOfUniverse(universeBounds);
		editor.setUniverseBounds(universeBounds);
		}
	public		void		getMinimumSizeOfUniverse(MiBounds minUniverse)
		{
		editor.reCalcBoundsOfContents(minUniverse);
		if (universeMargins != null)
			minUniverse.addMargins(universeMargins);
		}
	public		void		getMinimumSizeOfDevice(MiBounds minDevice)
		{
		editor.getDeviceBounds(minDevice);
		}
	public		void		getMinimumSizeOfWorld(MiBounds minWorld)
		{
		editor.getWorldBounds(minWorld);
		}
	public		void		getPreferredSizeOfUniverse(MiBounds prefUniverse)
		{
		editor.reCalcBoundsOfContents(contentsBounds);
		if (universeMargins != null)
			contentsBounds.addMargins(universeMargins);
		editor.getUniverseBounds(prefUniverse);

		if (!prefUniverse.contains(contentsBounds))
			{
			if (canBeLargerThanSizeOfContent)
				{
				prefUniverse.union(contentsBounds);
				if (centerUniverseOnCenterOfContents)
					{
					prefUniverse.setCenter(contentsBounds.getCenter());
					}
				}
			else
				{
				prefUniverse.setBounds(contentsBounds);
				}
			}
		else if ((!universeBounds.equals(contentsBounds))
			&& (!canBeLargerThanSizeOfContent))
			{
			prefUniverse.setBounds(contentsBounds);
			}
		else if ((!universeBounds.getCenter().equals(contentsBounds.getCenter()))
			&& (centerUniverseOnCenterOfContents))
			{
			universeBounds.setCenter(contentsBounds.getCenter());
			prefUniverse.setBounds(universeBounds);
			}
		}
	public		void		getPreferredSizeOfDevice(MiBounds prefDevice)
		{
		editor.getDeviceBounds(prefDevice);
		}
	public		void		getPreferredSizeOfWorld(MiBounds prefWorld)
		{
		editor.getWorldBounds(prefWorld);
		}
	}


