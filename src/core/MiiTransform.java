
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
public interface MiiTransform extends MiiCommandNames
	{
					/**------------------------------------------------------
			 		 * Returns whether this transform has constant 
			 		 * scaleFactors and translations across the world space.
			 		 * In effect this returns true if scale = f(x,y) or
					 * translation = f(x,y)
					 * @return		true if transform depends on
					 *			location.
			 		 *------------------------------------------------------*/
	boolean				isPositionDependent();

					/**------------------------------------------------------
					 * Specifies the observer that will be notified whenever
					 * this transform changes. This is used by tranforms that
					 * are concatenations of other transforms (i.e. MiTranforms).
					 * The command: Mi_OBSERVED_HAS_CHANGED_COMMAND_NAME is
					 * sent to the observer whenever the scale, translation,
					 * etc. is changed in this transform.
					 * @param handler	the observer
					 * @see			#getObserver
			 		 *------------------------------------------------------*/
	void				setObserver(MiiCommandHandler handler);

					/**------------------------------------------------------
					 * Gets the observer that will be notified whenever
					 * this transform changes. 
					 * @return 		the observer
					 * @see			#setObserver
			 		 *------------------------------------------------------*/
	MiiCommandHandler 		getObserver();

					/**------------------------------------------------------
					 * Increase/decrease the scale factor by the given amount.
					 * @param scale		the scale that is combined with
					 *			the current scale factor.
					 *------------------------------------------------------*/
	void				scale(MiScale scale);

					/**------------------------------------------------------
					 * Adjust the world translation by the given amount.
					 * @param vector	the translation that is combined with
					 *			the current translation.
					 *------------------------------------------------------*/
	void				translate(MiVector vector);

					/**------------------------------------------------------
					 * Adjust the rotation by the given amount. The rotation
					 * is performed about the current rotation point. If none
					 * has been set then the center of the world is used.
					 * @param radians	the angle that is combined with
					 *			the current rotation.
					 *------------------------------------------------------*/
	void				rotate(double radians);

					/**------------------------------------------------------
					 * Set the scale factor to the given amount.
					 * @param scale		the new scale factor.
					 *------------------------------------------------------*/
	void				setScale(MiScale scale);

					/**------------------------------------------------------
					 * Get the horizontal and vertical scale factors. These
					 * are the ratio of the world space sizes to the device
					 * space size.
					 * @param scale		the (returned) scale factor.
					 * @return 		the scale factor.
					 *------------------------------------------------------*/
			MiScale		getScale(MiScale scale);

					/**------------------------------------------------------
					 * Set the world translation to the given amount.
					 * @param vector	the new translation.
					 *------------------------------------------------------*/
			void		setWorldTranslation(MiVector vector);

					/**------------------------------------------------------
					 * Get the world translation.
					 * @param vector	the (returned) translation.
					 * @return 		the translation.
					 *------------------------------------------------------*/
			MiVector	getWorldTranslation(MiVector vector);

					/**------------------------------------------------------
					 * Set the device translation to the given amount.
					 * @param vector	the new translation.
					 *------------------------------------------------------*/
			void		setDeviceTranslation(MiVector vector);

					/**------------------------------------------------------
					 * Get the device translation.
					 * @param vector	the (returned) translation.
					 * @return 		the translation.
					 *------------------------------------------------------*/
			MiVector	getDeviceTranslation(MiVector vector);

					/**------------------------------------------------------
					 * Set the rotation to the given amount. The rotation
					 * is performed about the given point.
					 * @param radians	the new angle of rotation.
					 *------------------------------------------------------*/
			void		setRotation(double radians);

					/**------------------------------------------------------
					 * Get the current rotation in radians.
					 * @return 		the angle of rotation in radians.
					 *------------------------------------------------------*/
			double		getRotation();

					/**------------------------------------------------------
					 * Set the point to rotate about.
					 * @param center	the center of rotation.
					 *------------------------------------------------------*/
			void		setRotationPoint(MiPoint center);

					/**------------------------------------------------------
					 * Get the point to rotate about.
					 * @param center	the (returned) center of rotation.
					 * @return 		the center of rotation.
					 *------------------------------------------------------*/
			MiPoint		getRotationPoint(MiPoint center);

					/**------------------------------------------------------
			 		 * Convert the coordinate in world space to it's
			 		 * corresponding coordinate in device space.
			 		 * @param wPoint	the world space coordinate
			 		 * @param dPoint	the (returned) device space coordinate
			 		 *------------------------------------------------------*/
			void		wtod(MiPoint wPoint, MiPoint dPoint);
		
					/**------------------------------------------------------
			 		 * Convert the coordinate in world space to it's
			 		 * corresponding coordinate in integer device space.
			 		 * @param point		the world space coordinate
			 		 * @param dPoint	the (returned) device space coordinate
			 		 *------------------------------------------------------*/
			void		wtod(MiPoint point, MiDevicePoint dPoint);
		
					/**------------------------------------------------------
			 		 * Convert the vector at the given location in world 
					 * space to the corresponding vector in device space.
			 		 * @param wPoint	the world space coordinate
			 		 * @param wVector	the world space vector
			 		 * @param dVector	the (returned) device space vector
			 		 *------------------------------------------------------*/
			void		wtod(MiPoint wPoint, MiVector wVector, MiVector dVector);

					/**------------------------------------------------------
			 		 * Convert the vector at the given location in world 
					 * space to the corresponding vector in device space.
			 		 * @param wPoint	the world space coordinate
			 		 * @param wVector	the world space vector
			 		 * @param dVector	the (returned) device space vector
			 		 *------------------------------------------------------*/
			void		wtod(MiPoint wPoint, MiVector wVector, MiDeviceVector dVector);

					/**------------------------------------------------------
			 		 * Convert the given world space bounds to device space
			 		 * bounds. The given bounds may reference the same 
					 * MiBounds instance.
			 		 * @param wBounds	the world space bounds
			 		 * @param dBounds	the (returned) device space bounds
			 		 *------------------------------------------------------*/
	 		void		wtod(MiBounds wBounds, MiBounds dBounds);

					/**------------------------------------------------------
			 		 * Convert the given world space bounds to integer device
					 * space bounds.
			 		 * @param wBounds	the world space bounds
			 		 * @param dBounds	the (returned) device space bounds
			 		 *------------------------------------------------------*/
	 		void		wtod(MiBounds wBounds, MiDeviceBounds dBounds);

					/**------------------------------------------------------
			 		 * Convert the coordinate in device space to it's
			 		 * corresponding coordinate in world space.
			 		 * @param dPoint	the device space coordinate
			 		 * @param wPoint	the (returned) world space coordinate
			 		 *------------------------------------------------------*/
			void		dtow(MiPoint dPoint, MiPoint wPoint);

					/**------------------------------------------------------
			 		 * Convert the coordinate in integer device space to it's
			 		 * corresponding coordinate in world space.
			 		 * @param dPoint		the device space coordinate
			 		 * @param point		the (returned) world space coordinate
			 		 *------------------------------------------------------*/
			void		dtow(MiDevicePoint dPoint, MiPoint point);

					/**------------------------------------------------------
			 		 * Convert the vector at the given location in device 
					 * space to the corresponding vector in world space.
			 		 * @param dPoint	the device space coordinate
			 		 * @param dVector	the device space vector
			 		 * @param wVector	the (returned) world space vector
			 		 *------------------------------------------------------*/
			void		dtow(MiPoint dPoint, MiVector dVector, MiVector wVector);

					/**------------------------------------------------------
			 		 * Convert the vector at the given location in device 
					 * space to the corresponding vector in world space.
			 		 * @param dPoint	the device space coordinate
			 		 * @param dVector	the device space vector
			 		 * @param wVector	the (returned) world space vector
			 		 *------------------------------------------------------*/
			void		dtow(MiDevicePoint dPoint, MiDeviceVector dVector, MiVector wVector);

					/**------------------------------------------------------
			 		 * Convert the given device space bounds to world space
			 		 * bounds. The given bounds may reference the same 
					 * MiBounds instance.
			 		 * @param dBounds	the device space bounds
			 		 * @param wBounds	the (returned) world space bounds
			 		 *------------------------------------------------------*/
	 		void		dtow(MiBounds dBounds, MiBounds wBounds);

					/**------------------------------------------------------
			 		 * Convert the given integer device space bounds to world
					 * space bounds.
			 		 * @param dBounds	the device space bounds
			 		 * @param wBounds	the (returned) world space bounds
			 		 *------------------------------------------------------*/
	 		void		dtow(MiDeviceBounds dBounds, MiBounds wBounds);
	}

