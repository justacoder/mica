
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
 * method), assures that the editor is sized correctly such that all the graphics 
 * in the editor are visible at their preferred sizes. This is used by MiEditors that
 * contain only widgets, in which case the world to device ratio is usually equal to
 * one, and we want the MiEditor (window/dialog box) to be resized to exactly what
 * the contents want the size to be. This is kind of analogous to an automatic 
 * <i>pack</i> method that the AWT Window supports.
 *
 * @see #setViewportSizeCanBeLessThanPreferredAndGreaterThanMinimum
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditorViewportSizeIsOneToOneLayout implements MiiEditorViewportSizeLayout
	{
	private		boolean		canBeLargerThanSizeOfContent;
	private		MiEditor	editor;
	private		MiSize 		contentsSize 		= new MiSize();
	private		MiBounds 	contentsBounds 		= new MiBounds();
	private		MiBounds 	oldContentsBounds 	= new MiBounds();
	private		boolean		determinesSizeOfDevice	= true;
	private		boolean		centerWorldOnCenterOfContents	= true;
	private		boolean		tryingToResetDeviceBoundsToPrefWorld;
	private		boolean		viewportSizeCanBeLessThanPreferredAndGreaterThanMinimum	= false;




	public				MiEditorViewportSizeIsOneToOneLayout(
						boolean canBeLargerThanSizeOfContent)
		{
		this.canBeLargerThanSizeOfContent = canBeLargerThanSizeOfContent;
		if (MiSystem.getJDKVersion() >= 1.2)
			{
			// JDK 1.2 does not give us the preferred size we ask for... unlike AWT 1.1 ...
			// Because it looks at the size of the window as a whole and at the time
			// we set the size of the window, the insets are all zero.
			//setViewportSizeCanBeLessThanPreferredAndGreaterThanMinimum(true);
			}
		}

	public		void		setTarget(MiEditor target)
		{
		editor = target;
		if ((editor instanceof MiNativeWindow)
			&& (((MiNativeWindow )editor).getNativeComponentType() 
				== MiiTypes.Mi_SWING_LIGHTWEIGHT_COMPONENT_TYPE))
			{
			// Swing does not give us the preferred size we ask for... unlike AWT 1.1 ...
			// Because it looks at the size of the window as a whole and at the time
			// we set the size of the window, the insets are all zero.
// TEST 6-15-2001 Swing and/or MiJCanvas fixed this: setViewportSizeCanBeLessThanPreferredAndGreaterThanMinimum(true);
			}
		}

	public		void		setCenterWorldOnCenterOfContents(boolean flag)
		{
		centerWorldOnCenterOfContents = flag;
		}
	public		boolean		getCenterWorldOnCenterOfContents()
		{
		return(centerWorldOnCenterOfContents);
		}
	public		void		setDeterminesSizeOfDevice(boolean flag)
		{
		determinesSizeOfDevice = flag;
		}
	public		boolean		getDeterminesSizeOfDevice()
		{
		return(determinesSizeOfDevice);
		}

					/**
					 * Sets whether the increase in size or visibility of a part of the
					 * window will either be fit into the current window size or cause
					 * the window size to be expanded (where possible, within the limits
					 * of the screen itself).
					 * @param flag		true if the window size is not expanded
					 *			false (the default) if the window size is
					 *			expanded to suit the preferred size of the
					 *			window contents
					 */
	public		void		setViewportSizeCanBeLessThanPreferredAndGreaterThanMinimum(boolean flag)
		{
		viewportSizeCanBeLessThanPreferredAndGreaterThanMinimum = flag;
		}
					/**
					 * Gets whether the increase in size or visibility of a part of the
					 * window will either be fit into the current window size or cause
					 * the window size to be expanded (where possible, within the limits
					 * of the screen itself).
					 * @return 		true if the window size is not expanded
					 *			false (the default) if the window size is
					 *			expanded to suit the preferred size of the
					 *			window contents
					 */
	public		boolean		getViewportSizeCanBeLessThanPreferredAndGreaterThanMinimum()
		{
		return(viewportSizeCanBeLessThanPreferredAndGreaterThanMinimum);
		}

	public		void		validateLayout()
		{
		MiBounds prefWorld = new MiBounds();
		getPreferredSizeOfWorld(prefWorld);
//MiDebug.println("\n MiEditorViewportSizeIsOneToOneLayout.validateLayout - - - target=" + editor);
//MiDebug.println("canBeLargerThanSizeOfContent = " + canBeLargerThanSizeOfContent);

		if (canBeLargerThanSizeOfContent)
			{
			// ---------------------------------------------------
			// The size in device coordinates controls what the world 
			// and universe coords are
			// ---------------------------------------------------
			MiBounds deviceBounds = editor.getDeviceBounds();

			if (deviceBounds.isSmallerSizeThan(prefWorld))
				{
				if (!tryingToResetDeviceBoundsToPrefWorld)
					{
					prefWorld.translateLLCornerTo(editor.getDeviceBounds().getLLCorner());
					tryingToResetDeviceBoundsToPrefWorld = true;
//MiDebug.println("Setteing device= " + prefWorld);
					editor.setDeviceBounds(prefWorld);
					}
				tryingToResetDeviceBoundsToPrefWorld = false;
				}

			deviceBounds = editor.getDeviceBounds();
//MiDebug.println("deviceBounds= " + deviceBounds);
			if (viewportSizeCanBeLessThanPreferredAndGreaterThanMinimum)
				{
				// ---------------------------------------------------
				// If the device bounds are smaller than the 
				// preferred world bounds...
				// ---------------------------------------------------
				if (deviceBounds.isSmallerSizeThan(prefWorld))
					{
					// ---------------------------------------------------
					// ... try with minimum world bounds...
					// ---------------------------------------------------
					MiBounds minWorld = new MiBounds();
					getMinimumSizeOfWorld(minWorld);
					// ---------------------------------------------------
					// Device bounds are larger than minimum... set prefWorld
					// to be minWorld so that we will fall thru below and
					// fixup world and universe bounds...
					// ---------------------------------------------------
					if (!deviceBounds.isSmallerSizeThan(minWorld))
						prefWorld.copy(minWorld);
					}
				}

			// ---------------------------------------------------
			// If the device bounds are larger than or equal to the 
			// preferred world bounds... Updated to check with and/or 
			// height for 'larger than' to take care of the odd, 1 pixel
			// smaller in 1 dimension, 400 pixels larger in the other (12-30-2001)
			// ---------------------------------------------------
			if ((deviceBounds.getWidth() > prefWorld.getWidth())
				|| (deviceBounds.getHeight() > prefWorld.getHeight()))
				{
				// ---------------------------------------------------
				// ... then just assure that the universe and world are the 
				// same size as the device bounds
				// ---------------------------------------------------
				deviceBounds.setCenter(prefWorld.getCenter());
				if (!editor.getUniverseBounds().equals(deviceBounds))
					{
					editor.getViewport().setUniverseBounds(deviceBounds);
					editor.updateLayerBounds();
					}
//MiDebug.println("editor.getWorldBounds()=" + editor.getWorldBounds());
//MiDebug.println("deviceBounds=" + deviceBounds);
//MiDebug.println("prefWorld=" + prefWorld);
				if (!editor.getWorldBounds().equals(deviceBounds))
					{
					editor.getViewport().setWorldBounds(deviceBounds);
					}
// Test 1-16-2001 sometimes window is too big in one dimension and too small in another re: MiExample7 [sic]return;
 
				}
			}

		// ---------------------------------------------------
		// Either the device bounds must equal the world bounds
		// exactly or the device bounds are too small and we
		// need to set all bounds equal to the prefWorld bounds
		// ---------------------------------------------------
		// Added 3-5-2002 to keep maximized windows world-to-device aspect ratio 1-1
		if (!editor.getWorldBounds().equalsSize(editor.getDeviceBounds()))
			{
			if (!editor.getUniverseBounds().equals(prefWorld))
				{
				editor.getViewport().setUniverseBounds(prefWorld);
				editor.updateLayerBounds();
				}
			if (!editor.getWorldBounds().equals(prefWorld))
				{
				editor.getViewport().setWorldBounds(prefWorld);
				}
			if (!editor.getDeviceBounds().equalsSize(prefWorld))
				{
				prefWorld.translateLLCornerTo(editor.getDeviceBounds().getLLCorner());
				editor.setDeviceBounds(prefWorld);
				}
			}
//MiDebug.println("prefWorld = " + prefWorld);
//MiDebug.println("editor.getDeviceBounds() = " + editor.getDeviceBounds());
//MiDebug.println("editor.getWorldBounds() = " + editor.getWorldBounds());
		}
	public		void		getMinimumSizeOfUniverse(MiBounds minUniverse)
		{
		getMinimumSizeOfWorld(minUniverse);
		}
	public		void		getMinimumSizeOfDevice(MiBounds minDevice)
		{
		if (determinesSizeOfDevice)
			{
			getMinimumSizeOfWorld(minDevice);
			}
		else
			{
			editor.calcMinimumSizeOfContents(contentsSize);
			editor.getDeviceBounds(minDevice);
			minDevice.setSize(contentsSize);
			}
		}
	public		void		getMinimumSizeOfWorld(MiBounds minWorld)
		{
		MiiLayout layout = editor.getLayout();
		if ((layout != null) && (layout.determinesPreferredAndMinimumSizes()))
			layout.getMinimumSize(contentsSize);
		else
			editor.calcMinimumSizeOfContents(contentsSize);	

		contentsBounds.setSize(contentsSize);
		editor.reCalcBoundsOfContents(oldContentsBounds);
		contentsBounds.setCenter(oldContentsBounds.getCenter());

		if (canBeLargerThanSizeOfContent)
			{
			MiBounds world = editor.getWorldBounds();
			if (world.contains(contentsBounds))
				minWorld.copy(world);
			minWorld.copy(contentsBounds);
			}
		else
			{
			minWorld.copy(contentsBounds);
			}
		}
	public		void		getPreferredSizeOfUniverse(MiBounds prefUniverse)
		{
		getPreferredSizeOfWorld(prefUniverse);
		}
	public		void		getPreferredSizeOfDevice(MiBounds prefDevice)
		{
		if (determinesSizeOfDevice)
			getPreferredSizeOfWorld(prefDevice);
		else
			editor.getDeviceBounds(prefDevice);
		}
	public		void		getPreferredSizeOfWorld(MiBounds prefWorld)
		{
		MiiLayout layout = editor.getLayout();
		if ((layout != null) && (layout.determinesPreferredAndMinimumSizes()))
			{
			layout.getPreferredSize(contentsSize);
			editor.reCalcBoundsOfContents(oldContentsBounds);
//MiDebug.println("oldContentsBounds=" + oldContentsBounds);
//MiDebug.println("centerWorldOnCenterOfContents=" + centerWorldOnCenterOfContents);
			contentsBounds.setCenter(oldContentsBounds.getCenter());
			}
		else
			{
			editor.calcPreferredSizeOfContents(contentsSize);	
			if (centerWorldOnCenterOfContents)
				{
				editor.reCalcBoundsOfContents(oldContentsBounds);
				contentsBounds.setCenter(oldContentsBounds.getCenter());
				}
			else
				{
				contentsBounds.setCenter(editor.getWorldBounds().getCenter());
				}
			}

		contentsBounds.setSize(contentsSize);
		prefWorld.copy(contentsBounds);
//MiDebug.println("prefWorld=" + prefWorld);
		}
	}


