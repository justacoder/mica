
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
 * This class manages the world (or model) and device (or output) spaces
 * associated with an editor. This includes managing their bounds as well
 * as the transformations between them and any contraints on their sizes.
 *
 * Device Space:
 * The device may be the actual screen (or a printer) or another viewport's
 * world space.
 *
 * World Space:
 * The world may be the mapped directly to device space or may be scaled
 * to the desired semantic units.
 *
 * Universe Space:
 * The bounds within which the world space is contrained.
 *
 * Miniverse Space:
 * The size which the world space mus be larger than.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiViewport
	{
	// Bounds Policys
	public static final int		SCALE_WORLD_PROPORTIONALLY_WITH_DEVICE = 1;

	private	MiBounds		device			= new MiBounds();
	private	MiBounds		world			= new MiBounds();
	private	MiBounds		universe		= new MiBounds();
	private	MiBounds		miniverse		= new MiBounds();
	private	MiScale			enforcedAspectRatio	= new MiScale();
	private	boolean			universe_defined;
	private	MiViewportTransform	transform		= new MiViewportTransform();
	private	int			setResizePolicy		= SCALE_WORLD_PROPORTIONALLY_WITH_DEVICE;


					/**------------------------------------------------------
	 				 * Constructs a new MiViewport. 
	 				 * @param world 	the world bounds
	 				 * @param device 	the device bounds
					 *------------------------------------------------------*/
	public				MiViewport(MiBounds world, MiBounds device)
		{
		this.world.copy(world);
		this.device.copy(device);
		reCalcScales();
		}

					/**------------------------------------------------------
	 				 * Returns the transform that converts coordinates in device
	 				 * space to world space and visa-versa.
	 				 * @return 	 	the transform
					 *------------------------------------------------------*/
	public		MiiTransform	getTransform()
		{
		return(transform);
		}

					/**------------------------------------------------------
	 				 * Specifies how the world is resized when the device is resized.
	 				 * Possible values are:
					 *     Mi_NONE and 
					 *     SCALE_WORLD_PROPORTIONALLY_WITH_DEVICE.
	 				 * @param policy 	the policy
					 *------------------------------------------------------*/
	public		void		setResizePolicy(int policy)
		{
		setResizePolicy = policy;
		}

					/**------------------------------------------------------
	 				 * Returns how the world space is resized when the 
	 				 * device space is resized.
	 				 * @return 		the resize policy
					 *------------------------------------------------------*/
	public		int		getResizePolicy()
		{
		return(setResizePolicy);
		}

					/**------------------------------------------------------
	 				 * Return the bounds of the world space.
	 				 * @return 		the world space bounds 
					 *------------------------------------------------------*/
	public		MiBounds	getWorldBounds()
		{
		return(new MiBounds(world));
		}

					/**------------------------------------------------------
	 				 * Return the bounds of the world space.
	 				 * @param bounds 	the (returned) world space bounds 
	 				 * @return 		the world space bounds 
					 *------------------------------------------------------*/
	public		MiBounds	getWorldBounds(MiBounds bounds)
		{
		bounds.copy(world);
		return(bounds);
		}
	
					/**------------------------------------------------------
	 				 * Specify the bounds of the world space.
					 * @param bounds	the world bounds
					 *------------------------------------------------------*/
	public		void		setWorldBounds(MiBounds bounds)
		{ 
//MiDebug.printStackTrace(this + " SETTING world bounds to: " + bounds);
		world.copy(bounds);
		confineProposedWorldToConstraints();
		reCalcScales();
		}
					/**------------------------------------------------------
	 				 * Return bounds of the device space.
					 * @return		the device bounds
					 *------------------------------------------------------*/
	public		MiBounds	getDeviceBounds()
		{
		return(new MiBounds(device));
		}
					/**------------------------------------------------------
	 				 * Return bounds of the device space.
					 * @param bounds	the (returned) device bounds
					 * @return		the device bounds
					 *------------------------------------------------------*/
	public		MiBounds	getDeviceBounds(MiBounds bounds)
		{
		bounds.copy(device);
		return(bounds);
		}

					/**------------------------------------------------------
	 				 * Sets the bounds of the device space.
	 				 * @param bounds the device bounds
					 *------------------------------------------------------*/
	public		void		setDeviceBounds(MiBounds bounds)
		{ 
		if (bounds.equals(device))
			return;

//MiDebug.printStackTrace(this + "setDEviceBounds: was " + device + " NOW =" + bounds);

		if (setResizePolicy == SCALE_WORLD_PROPORTIONALLY_WITH_DEVICE)
			{
			if ((device.getWidth() > 0) && (device.getHeight() > 0))
				{
				world.scale(
					bounds.getWidth()/device.getWidth(), 
					bounds.getHeight()/device.getHeight());
//MiDebug.printStackTrace(this + " SETTING world bounds to: " + world);
				}
			universe.union(world);
			}
		device.copy(bounds); 
		reCalcScales();
		}

					/**------------------------------------------------------
	 				 * Returns the maximum allowed bounds of the world space.
					 * @return		the bounds of the universe
					 *------------------------------------------------------*/
	public		MiBounds	getUniverseBounds()
		{
		return(new MiBounds(universe));
		}

					/**------------------------------------------------------
	 				 * Returns the maximum allowed bounds of the world space.
	 				 * @param bounds 	the bounds that are returned
					 * @return		the universe bounds
					 *------------------------------------------------------*/
	public		MiBounds	getUniverseBounds(MiBounds bounds)
		{
		bounds.copy(universe);
		return(bounds);
		}
	
					/**------------------------------------------------------
	 				 * Sets the maximum allowed bounds of the world space.
	 				 * @param bounds 	the maximum allowed bounds
					 *------------------------------------------------------*/
	public		void		setUniverseBounds(MiBounds bounds)
		{ 
		universe.copy(bounds); 
		if (!bounds.isReversed())
			universe_defined = true; 
		else
			universe_defined = false; 
		}
		
	// NOTE: This should also be specifyable by expressing
	// a maximum zoom level or magnification scale.
					/**------------------------------------------------------
	 				 * Specify the minimum allowed size of the world space.
	 				 * @param width 	the minimum allowed width
	 				 * @param height 	the minimum allowed height
					 *------------------------------------------------------*/
	public		void		setMiniverse(MiDistance width, MiDistance height)
		{
		miniverse.xmin = 0;
		miniverse.ymin = 0;
		miniverse.xmax = width;
		miniverse.ymax = height;
		}
					/**------------------------------------------------------
	 				 * Gets the minimum allowed size of the world space.
	 				 * @return  		the minimum allowed size
					 *------------------------------------------------------*/
	public		MiSize		getMiniverse()
		{
		return(new MiSize(miniverse.xmax, miniverse.ymax));
		}
					/**------------------------------------------------------
	 				 * Sets the aspect ratio that the world space should maintain.
	 				 * @param aspectRatio 	the ratio of height to width or null
					 * @see			#enforceWorldAspectRatio
					 *------------------------------------------------------*/
	public		void		setEnforcedAspectRatio(MiScale aspectRatio)
		{
		enforcedAspectRatio = aspectRatio;
		}
					/**------------------------------------------------------
	 				 * Gets the aspect ratio that the world space should maintain.
	 				 * @return 	 	the ratio of height to width or null
					 *------------------------------------------------------*/
	public		MiScale		getEnforcedAspectRatio()
		{
		return(enforcedAspectRatio);
		}
					/**------------------------------------------------------
	 				 * Modifies the proposed world space bounds to conform to
					 * any specified aspect ratio.
	 				 * @param proposedWorld the (modified) world bounds
					 * @see			#setEnforcedAspectRatio
					 *------------------------------------------------------*/
	public		void		enforceWorldAspectRatio(MiBounds proposedWorld)
		{
		if (enforcedAspectRatio != null)
			{
			MiDistance width = enforcedAspectRatio.x * device.getWidth();
			MiDistance height = enforcedAspectRatio.y * device.getHeight();

			MiDistance worldWidth = proposedWorld.getHeight() * width/height;
			if (worldWidth > proposedWorld.getWidth())
				{
				proposedWorld.setWidth(worldWidth);
				return;
				}
			MiDistance worldHeight = proposedWorld.getWidth() * height/width;
			if (worldHeight > proposedWorld.getHeight())
				{
				proposedWorld.setHeight(worldHeight);
				}
			}
		}
					/**------------------------------------------------------
	 				 * Shrinks, if necessary, and translates this viewport 
	 				 * if necessary to keep the world coordinates within
	 				 * the specified (if any) universe (maximum world space)
	 				 * and miniverse (minimum world space).
	 				 * @param proposed 	the proposed bounds that are 
					 *			modified, no change is made to 
					 *			this viewport.
					 *------------------------------------------------------*/
	public		void		confineProposedWorldToConstraints(MiBounds proposed)
		{
		if (universe_defined)
			{
			confineWorldToMiniverse(proposed);
			confineWorldToUniverse(proposed);
			}
		// Why not? causes some problems... but shouldn't enforceWorldAspectRatio(proposed);
		}
					/**------------------------------------------------------
	 				 * Modifies proposed bounds, if necessary to assure that
					 * it is larger than the miniverse.
	 				 * @param proposed 	the proposed bounds that are 
					 *			modified, no change is made to 
					 *			this viewport.
					 *------------------------------------------------------*/
	public		void 		confineWorldToMiniverse(MiBounds proposed)
		{
		if (proposed.xmax - proposed.xmin < miniverse.xmax - miniverse.xmin)
			{
			proposed.setWidth(miniverse.getWidth());
			}
		if (proposed.ymax - proposed.ymin < miniverse.ymax - miniverse.ymin)
			{
			proposed.setHeight(miniverse.getHeight());
			}
		}

					/**------------------------------------------------------
	 				 * Modifies proposed bounds, if necessary to assure that it
	 				 * fits within the universe.
	 				 * @param proposed 	the proposed bounds that are 
					 *			modified, no change is made to 
					 *			this viewport.
					 *------------------------------------------------------*/
	public		void 		confineWorldToUniverse(MiBounds proposed)
		{
		if ((proposed.xmax - proposed.xmin > universe.xmax - universe.xmin)
			|| (proposed.ymax - proposed.ymin > universe.ymax - universe.ymin))
			{
			proposed.xmin = universe.xmin;
			proposed.ymin = universe.ymin;
			proposed.xmax = universe.xmax;
			proposed.ymax = universe.ymax;
			return;
			}

		if (proposed.xmin < universe.xmin)
			{
			proposed.xmax = proposed.xmax + universe.xmin - proposed.xmin;
			proposed.xmin = universe.xmin;
			}
		else if (proposed.xmax > universe.xmax)
			{
			proposed.xmin = proposed.xmin - proposed.xmax + universe.xmax;
			proposed.xmax = universe.xmax;
			}
		
		if (proposed.ymin < universe.ymin)
			{
			proposed.ymax = proposed.ymax + universe.ymin - proposed.ymin;
			proposed.ymin = universe.ymin;
			}
		else if (proposed.ymax > universe.ymax)
			{
			proposed.ymin = proposed.ymin - proposed.ymax + universe.ymax;
			proposed.ymax = universe.ymax;
			}
		}

					/**------------------------------------------------------
	 				 * Returns the horizontal location of the center of 
	 				 * the world space within the maximum allowed size of 
	 				 * the world space, on a scale from 0.0 to 1.0. This is
	 				 * usually used to reposition a scrollbar in response
	 				 * to a change in the world space.
					 * @return		the normalized horizontal position
					 *------------------------------------------------------*/
	public		double		getHorizontalPositionOfWorldInUniverse()
		{
		if (universe.getWidth() <= world.getWidth())
			return(0.5);

		double horizontal = (world.xmin - universe.xmin)/(universe.getWidth() - world.getWidth());
		if (horizontal > 1.0)
			horizontal = 1.0;
		else if (horizontal < 0.0)
			horizontal = 0.0;
		return(horizontal);
		}

					/**------------------------------------------------------
					 * Returns the relative size, on a scale from 0.0 to 1.0,
					 * of the current world space relative to the maximum 
					 * allowed world space. This is usually used to determine
					 * what the size of a scrollbar should be.
					 * @return		the normalized horizontal size
					 *------------------------------------------------------*/
	public		double		getHorizontalSizeOfWorldInUniverse()
		{
		if (!universe_defined)
			return(1.0);

		double size = world.getWidth()/universe.getWidth();
		if (size > 1.0)
			size = 1.0;
		else if (size < 0.0)
			size = 0.0;
		return(size);
		}
					/**------------------------------------------------------
					 * Returns the vertical location of the center of 
					 * the world space within the maximum allowed size of 
					 * the world space, on a scale from 0.0 to 1.0. This is
					 * usually used to reposition a scrollBar in response
					 * to a change in the world space.
					 * @return		the normalized vertical position
					 *------------------------------------------------------*/
	public		double		getVerticalPositionOfWorldInUniverse()
		{
		if (universe.getHeight() <= world.getHeight())
			return(0.5);

		double vertical = (world.ymin - universe.ymin)/(universe.getHeight() - world.getHeight());
		if (vertical > 1.0)
			vertical = 1.0;
		else if (vertical < 0.0)
			vertical = 0.0;
		return(vertical);
		}
					/**------------------------------------------------------
					 * Returns the relative size, on a scale from 0.0 to 1.0,
					 * of the current world space relative to the maximum 
					 * allowed world space. This is usually used to determine
					 * what the size of a scrollbar should be.
					 * @return		the normalized vertical size
					 *------------------------------------------------------*/
	public		double		getVerticalSizeOfWorldInUniverse()
		{
		if (!universe_defined)
			return(1.0);

		double size = world.getHeight()/universe.getHeight();
		if (size > 1.0)
			size = 1.0;
		else if (size < 0.0)
			size = 0.0;
		return(size);
		}
					/**------------------------------------------------------
					 * Modifies the given translation, if necessary, to keep
					 * the given bounds within the universe. This is used by
					 * the system to assure translated objects stay within
					 * the maximum allowed bounds of world space.
					 * @param tVector 	the proposed (and returned) translation
					 * @param bounds 	the bounds to confine
					 * @return		true if tVector was modified
					 *------------------------------------------------------*/
	public		boolean		confineTranslatedExtremaToUniverse(MiVector tVector, MiBounds bounds)
		{
		if (!universe_defined)
			return(false);

		boolean modified = false;
		if (bounds.xmin + tVector.x < universe.xmin)
			{
			tVector.x = universe.xmin - bounds.xmin;
			modified = true;
			}
		else if (bounds.xmax + tVector.x > universe.xmax)
			{
			tVector.x = universe.xmax - bounds.xmax;
			modified = true;
			}
		
		if (bounds.ymin + tVector.y < universe.ymin)
			{
			tVector.y = universe.ymin - bounds.ymin;
			modified = true;
			}
		else if (bounds.ymax + tVector.y > universe.ymax)
			{
			tVector.y = universe.ymax - bounds.ymax;
			modified = true;
			}
		return(modified);
		}
					/**------------------------------------------------------
					 * Calculates how far the given bounds will be translated
					 * outside the world space if the given translation is
					 * applied. This returns, in panVector, how much the world
					 * space would have to be translated in order for the
					 * object to remain visible. This is used in order to
					 * support autopan functionality.
					 * @param tVector 	the translation of the bounds
					 * @param bounds 	the bounds before translation
					 * @param panVector 	returned with how far bounds will
					 *			be translated oustide world space
					 *------------------------------------------------------*/
	public		void		getAmountExtremaTranslatedOutsideWorld(
						MiVector tVector, 
						MiBounds bounds,
						MiVector panVector)
		{
		// If object is larger than world, then pan world with object...
		if (bounds.getWidth() > world.getWidth())
			panVector.x = tVector.x;
		else if (bounds.xmin + tVector.x < world.xmin)
			panVector.x = bounds.xmin + tVector.x - world.xmin;
		else if (bounds.xmax + tVector.x > world.xmax)
			panVector.x = bounds.xmax + tVector.x - world.xmax;
		else
			panVector.x = 0;
	
		if (bounds.getHeight() > world.getHeight())
			panVector.y = tVector.y;
		else if (bounds.ymin + tVector.y < world.ymin)
			panVector.y = bounds.ymin + tVector.y - world.ymin;
		else if (bounds.ymax + tVector.y > world.ymax)
			panVector.y = bounds.ymax + tVector.y - world.ymax;
		else
			panVector.y = 0;
		}
					/**------------------------------------------------------
					 * Prints information about this transform.
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) + "@" + Integer.toHexString(hashCode())
			+ ": World [" + world + "], Device [" + device +"]");
		}

	//***************************************************************************************
	// Protected methods
	//***************************************************************************************

					/**------------------------------------------------------
	 				 * Shrinks, if necessary, and translates this viewport 
	 				 * if necessary to keep the world coordinates within
	 				 * the specified (if any) universe (maximum world space)
	 				 * and miniverse (minimum world space).
					 *------------------------------------------------------*/
	protected	void		confineProposedWorldToConstraints()
		{
		if (universe_defined)
			{
			confineProposedWorldToConstraints(world);
			reCalcScales();
			}
		}


					/**------------------------------------------------------
	 				 * Translates the device space.
	 				 * @param tx the x translation
	 				 * @param ty the y translation
					 *------------------------------------------------------*/
	protected	void		translateDevice(MiDistance tx, MiDistance ty)
		{ 
		device.translate(tx, ty);
		transform.setDeviceBoundsLL(device.xmin, device.ymin);
		}
	

	//***************************************************************************************
	// Private methods
	//***************************************************************************************


					/**------------------------------------------------------
					 *
					 *
					 *
					 *------------------------------------------------------*/
	private		void		reCalcScales()
		{
		transform.setScales(
			(world.xmax - world.xmin)/(device.xmax - device.xmin),
			(world.ymax - world.ymin)/(device.ymax - device.ymin));

		transform.setWorldBoundsLL(world.xmin, world.ymin);
		transform.setDeviceBoundsLL(device.xmin, device.ymin);
		}
	}


class MiViewportTransform implements MiiTransform
	{
	private		MiScale		scaleFactor	= new MiScale();
	private		MiVector	deviceLL	= new MiVector();
	private		MiVector	worldLL 	= new MiVector();
	private		MiVector	dWorldLL 	= new MiVector();
	private		MiPoint		wtmp 		= new MiPoint();
	private		MiPoint		dtmp 		= new MiPoint();
	private		MiBounds	tmpBounds 	= new MiBounds();
	private		MiiCommandHandler observer;



					/**------------------------------------------------------
					 * Contructs a new MiViewportTransform. This is used
					 * exclusively by the MiViewport class.
					 *------------------------------------------------------*/
	public				MiViewportTransform()
		{
		}
					/**------------------------------------------------------
					 * Specifies the observer that will be notified whenever
					 * this transform changes. This is used by tranforms that
					 * are concatenations of other transforms (i.e. MiTranforms).
					 * @param handler	the observer
					 * @see			#getObserver
					 * @implements		MiiTransform#setObserver
			 		 *------------------------------------------------------*/
	public		void		setObserver(MiiCommandHandler handler)
		{
		observer = handler;
		}
					/**------------------------------------------------------
					 * Gets the observer that will be notified whenever
					 * this transform changes. 
					 * @return 		the observer
					 * @see			#setObserver
					 * @implements		MiiTransform#getObserver
			 		 *------------------------------------------------------*/
	public		MiiCommandHandler getObserver()
		{
		return(observer);
		}
					/**------------------------------------------------------
					 * Notifies the observer that this transform has changed.
			 		 *------------------------------------------------------*/
	protected	void		notifyObserver()
		{
		if (observer != null)
			observer.processCommand(Mi_OBSERVED_HAS_CHANGED_COMMAND_NAME);
		}
					/**------------------------------------------------------
			 		 * Returns whether this transform has constant 
			 		 * scaleFactors and translations across the world space.
			 		 * In effect this returns true if scale = f(x,y) or
					 * translation = f(x,y)
					 * @return		true if transform depends on
					 *			location.
			 		 *------------------------------------------------------*/
	public		boolean		isPositionDependent()
		{
		return(false);
		}
					/**------------------------------------------------------
					 * Increase/decrease the scale factor by the given amount.
					 * @param s		the scale that is combined with
					 *			the current scale factor.
					 *------------------------------------------------------*/
	public		void		scale(MiScale s)
		{
		scaleFactor.combine(s);
		notifyObserver();
		}
					/**------------------------------------------------------
					 * Adjust the world translation by the given amount.
					 * @param vector	the translation that is combined with
					 *			the current translation.
					 *------------------------------------------------------*/
	public		void		translate(MiVector vector)
		{
		}

					/**------------------------------------------------------
					 * Adjust the rotation by the given amount. The rotation
					 * is performed about the current rotation point. If none
					 * has been set then the center of the world is used.
					 * @param radians	the angle that is combined with
					 *			the current rotation.
					 *------------------------------------------------------*/
	public		void		rotate(double radians)
		{
		}

					/**------------------------------------------------------
					 * Set the scale factor to the given amount.
					 * @param scale		the new scale factor.
					 *------------------------------------------------------*/
	public		void		setScale(MiScale scale)
		{
		}

					/**------------------------------------------------------
					 * Get the horizontal and vertical scale factors. These
					 * are the ratio of the world space sizes to the device
					 * space size.
					 * @param scale		the (returned) scale factor.
					 * @return 		the scale factor.
					 *------------------------------------------------------*/
	public		MiScale		getScale(MiScale scale)
		{
		scale.copy(scaleFactor);
		return(scale);
		}
					/**------------------------------------------------------
					 * Set the world translation to the given amount.
					 * @param vector	the new translation.
					 *------------------------------------------------------*/
	public		void		setWorldTranslation(MiVector vector)
		{
		worldLL.x = vector.x;
		worldLL.y = vector.y;
		dWorldLL.x = worldLL.x / scaleFactor.x;
		dWorldLL.y = worldLL.y / scaleFactor.y;
		notifyObserver();
		}

					/**------------------------------------------------------
					 * Get the world translation.
					 * @param vector	the (returned) translation.
					 * @return 		the translation.
					 *------------------------------------------------------*/
	public		MiVector	getWorldTranslation(MiVector vector)
		{
		vector.copy(worldLL);
		return(vector);
		}

					/**------------------------------------------------------
					 * Set the device translation to the given amount.
					 * @param vector	the new translation.
					 *------------------------------------------------------*/
	public		void		setDeviceTranslation(MiVector vector)
		{
		deviceLL.x = vector.x;
		deviceLL.y = vector.y;
		notifyObserver();
		}

					/**------------------------------------------------------
					 * Get the device translation.
					 * @param vector	the (returned) translation.
					 * @return 		the translation.
					 *------------------------------------------------------*/
	public		MiVector	getDeviceTranslation(MiVector vector)
		{
		vector.copy(deviceLL);
		return(vector);
		}

					/**------------------------------------------------------
					 * Set the rotation to the given amount. The rotation
					 * is performed about the given point.
					 * @param radians	the new angle of rotation.
					 *------------------------------------------------------*/
	public		void		setRotation(double radians)
		{
		}

					/**------------------------------------------------------
					 * Get the current rotation in radians.
					 * @return 		the angle of rotation in radians.
					 *------------------------------------------------------*/
	public		double		getRotation()
		{
		return(0.0);
		}
					/**------------------------------------------------------
					 * Set the point to rotate about.
					 * @param center	the center of rotation.
					 *------------------------------------------------------*/
	public		void		setRotationPoint(MiPoint center)
		{
		}
					/**------------------------------------------------------
					 * Get the point to rotate about.
					 * @param center	the (returned) center of rotation.
					 * @return 		the center of rotation.
					 *------------------------------------------------------*/
	public		MiPoint		getRotationPoint(MiPoint center)
		{
		return(center);
		}

					/**------------------------------------------------------
					 * Convert the coordinate in world space to it's
					 * corresponding coordinate in device space.
					 *------------------------------------------------------*/
	public		void		wtod(MiPoint point, MiDevicePoint dPoint)
		{
		dPoint.x = (MiDeviceDistance )(point.x / scaleFactor.x - dWorldLL.x + 0.5);
		dPoint.y = (MiDeviceDistance )(point.y / scaleFactor.y - dWorldLL.y + 0.5);
		}


					/**------------------------------------------------------
					 * Convert the coordinate in world space to it's
					 * corresponding coordinate in device space.
					 * @param wPoint	the world space coordinate
					 * @param dPoint	the (returned) device space coordinate
					 *------------------------------------------------------*/
	public		void		wtod(MiPoint wPoint, MiPoint dPoint)
		{
		dPoint.x = wPoint.x / scaleFactor.x + deviceLL.x - dWorldLL.x;
		dPoint.y = wPoint.y / scaleFactor.y + deviceLL.y - dWorldLL.y;
		}

					/**------------------------------------------------------
			 		 * Convert the vector at the given location in world 
					 * space to the corresponding vector in device space.
			 		 * @param wPoint	the world space coordinate
			 		 * @param wVector	the world space vector
			 		 * @param dVector	the (returned) device space vector
			 		 *------------------------------------------------------*/
	public		void		wtod(MiPoint wPoint, MiVector wVector, MiVector dVector)
		{
		dVector.x = wVector.x / scaleFactor.x;
		dVector.y = wVector.y / scaleFactor.y;
		}

					/**------------------------------------------------------
			 		 * Convert the vector at the given location in world 
					 * space to the corresponding vector in device space.
			 		 * @param wPoint	the world space coordinate
			 		 * @param wVector	the world space vector
			 		 * @param dVector	the (returned) device space vector
			 		 *------------------------------------------------------*/
	public		void		wtod(MiPoint wPoint, MiVector wVector, MiDeviceVector dVector)
		{
		MiDistance x = wVector.x / scaleFactor.x;
		MiDistance y = wVector.y / scaleFactor.y;
		dVector.x = (int )(x > 0 ? x + 0.5 : x - 0.5);
		dVector.y = (int )(y > 0 ? y + 0.5 : y - 0.5);
		}


					/**------------------------------------------------------
					 * Convert the given world space bounds to device space
					 * bounds. The given bounds may reference the same 
					 * MiBounds instance.
					 * @param wBounds	the world space bounds
					 * @param dBounds	the (returned) device space bounds
					 *------------------------------------------------------*/
	public		 void		wtod(MiBounds wBounds, MiBounds dBounds)
		{
		MiCoord xmin = wBounds.xmin;
		MiCoord ymin = wBounds.ymin;
		dBounds.xmin = xmin / scaleFactor.x + deviceLL.x - dWorldLL.x;
		dBounds.ymin = ymin / scaleFactor.y + deviceLL.y - dWorldLL.y;
		dBounds.xmax = (wBounds.xmax - xmin) / scaleFactor.x + dBounds.xmin;
		dBounds.ymax = (wBounds.ymax - ymin) / scaleFactor.y + dBounds.ymin;
		}

					/**------------------------------------------------------
					 * Convert the given world space bounds to integer device
					 * space bounds.
					 * @param wBounds	the world space bounds
					 * @param dBounds	the (returned) device space bounds
					 *------------------------------------------------------*/
	public		 void		wtod(MiBounds wBounds, MiDeviceBounds dBounds)
		{
		MiCoord xmin = wBounds.xmin;
		MiCoord ymin = wBounds.ymin;

		MiCoord x = xmin / scaleFactor.x + deviceLL.x - dWorldLL.x;
		MiCoord y = ymin / scaleFactor.y + deviceLL.y - dWorldLL.y;

		dBounds.xmin = (int )(x > 0 ? x + 0.5 : x - 0.5);
		dBounds.ymin = (int )(y > 0 ? y + 0.5 : y - 0.5);

		x = (wBounds.ymax - ymin) / scaleFactor.x + dBounds.xmin;
		y = (wBounds.ymax - ymin) / scaleFactor.y + dBounds.ymin;
	
		dBounds.xmax = (int )(x > 0 ? x + 0.5 : x - 0.5);
		dBounds.ymax = (int )(y > 0 ? y + 0.5 : y - 0.5);
		}

					/**------------------------------------------------------
					 * Convert the coordinate in integer device space to it's
					 * corresponding coordinate in world space.
					 * @param dPoint	the device space coordinate
					 * @param point		the (returned) world space coordinate
					 *------------------------------------------------------*/
	public		void		dtow(MiDevicePoint dPoint, MiPoint point)
		{
		point.x = (dPoint.x - deviceLL.x) * scaleFactor.x + worldLL.x;
		point.y = (dPoint.y - deviceLL.y) * scaleFactor.y + worldLL.y;
		}

					/**------------------------------------------------------
					 * Convert the coordinate in device space to it's
					 * corresponding coordinate in world space.
					 * @param dPoint	the device space coordinate
					 * @param point		the (returned) world space coordinate
					 *------------------------------------------------------*/
	public		void		dtow(MiPoint dPoint, MiPoint point)
		{
		point.x = (dPoint.x - deviceLL.x) * scaleFactor.x + worldLL.x;
		point.y = (dPoint.y - deviceLL.y) * scaleFactor.y + worldLL.y;
		}

					/**------------------------------------------------------
			 		 * Convert the vector at the given location in device 
					 * space to the corresponding vector in world space.
			 		 * @param dPoint	the device space coordinate
			 		 * @param dVector	the device space vector
			 		 * @param wVector	the (returned) world space vector
			 		 *------------------------------------------------------*/
	public		void		dtow(MiPoint dPoint, MiVector dVector, MiVector wVector)
		{
		wVector.x = dVector.x * scaleFactor.x;
		wVector.y = dVector.y * scaleFactor.y;
		}

					/**------------------------------------------------------
			 		 * Convert the vector at the given location in device 
					 * space to the corresponding vector in world space.
			 		 * @param dPoint	the device space coordinate
			 		 * @param dVector	the device space vector
			 		 * @param wVector	the (returned) world space vector
			 		 *------------------------------------------------------*/
	public		void		dtow(MiDevicePoint dPoint, MiDeviceVector dVector, MiVector wVector)
		{
		wVector.x = dVector.x * scaleFactor.x;
		wVector.y = dVector.y * scaleFactor.y;
		}

					/**------------------------------------------------------
			 		 * Convert the given device space bounds to world space
			 		 * bounds. The given bounds may reference the same 
					 * MiBounds instance.
			 		 * @param dBounds	the device space bounds
			 		 * @param wBounds	the (returned) world space bounds
			 		 *------------------------------------------------------*/
	public		 void		dtow(MiBounds dBounds, MiBounds wBounds)
		{
		MiCoord xmin = dBounds.xmin;
		MiCoord ymin = dBounds.ymin;
		wBounds.xmin = (dBounds.xmin - deviceLL.x) * scaleFactor.x + worldLL.x;
		wBounds.ymin = (dBounds.ymin - deviceLL.y) * scaleFactor.y + worldLL.y;
		wBounds.xmax = (dBounds.xmax - xmin) * scaleFactor.x + wBounds.xmin;
		wBounds.ymax = (dBounds.ymax - ymin) * scaleFactor.y + wBounds.ymin;
		}
					/**------------------------------------------------------
			 		 * Convert the given integer device space bounds to world
					 * space bounds.
			 		 * @param dBounds	the device space bounds
			 		 * @param wBounds	the (returned) world space bounds
			 		 *------------------------------------------------------*/
	public		 void		dtow(MiDeviceBounds dBounds, MiBounds wBounds)
		{
		int xmin = dBounds.xmin;
		int ymin = dBounds.ymin;
		wBounds.xmin = (dBounds.xmin - deviceLL.x) * scaleFactor.x + worldLL.x;
		wBounds.ymin = (dBounds.ymin - deviceLL.y) * scaleFactor.y + worldLL.y;
		wBounds.xmax = (dBounds.xmax - xmin) * scaleFactor.x + wBounds.xmin;
		wBounds.ymax = (dBounds.ymax - ymin) * scaleFactor.y + wBounds.ymin;
		}

					/**------------------------------------------------------
					 * Prints information about this transform.
					 *------------------------------------------------------*/
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this)
				+ ": <device: " + getDeviceTranslation(new MiVector())
				+ "> <" + getScale(new MiScale())
				+ "> <world: " + getWorldTranslation(new MiVector()) + ">");
		}

	//***************************************************************************************
	// Private methods
	//***************************************************************************************

			void		setDeviceBoundsLL(MiCoord x, MiCoord y)
		{
		deviceLL.x = x;
		deviceLL.y = y;
		}
			void		setWorldBoundsLL(MiCoord x, MiCoord y)
		{
		worldLL.x = x;
		worldLL.y = y;
		dWorldLL.x = worldLL.x / scaleFactor.x;
		dWorldLL.y = worldLL.y / scaleFactor.y;
		}
					/**------------------------------------------------------
					 * Set the horizontal and vertical scale factors. These
					 * are the ratio of the world space sizes to the device
					 * space size.
					 * @param x		the horizontal scale factor
					 *			(world space width/device space width)
					 * @param y		the vertical scale factor
					 *			(world space height/device space height)
					 *------------------------------------------------------*/
			void		setScales(double x, double y)
		{
		scaleFactor.x = x;
		scaleFactor.y = y;
		dWorldLL.x = worldLL.x / scaleFactor.x;
		dWorldLL.y = worldLL.y / scaleFactor.y;
		}
	}

