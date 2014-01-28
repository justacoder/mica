
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
 * This class is a straight foward implementation of the MiiTransform
 * interface. It uses a data-bsed, as opposed to a matrix-based, design.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGeneralTransform implements MiiTransform
	{
	private		MiVector	dTranslation	= new MiVector();
	private		MiVector	wTranslation	= new MiVector();
	private		MiPoint		center		= new MiPoint();
	private		double		rotation	= 0.0;
	private		MiScale		scaleFactor	= new MiScale();
	private		double		sinR		= 0.0;
	private		double		cosR		= 1.0;
	private		MiPoint		wtmp 		= new MiPoint();
	private		MiPoint		dtmp 		= new MiPoint();
	private		MiDevicePoint	devtmp 		= new MiDevicePoint();
	private		MiiCommandHandler observer;



	public				MiGeneralTransform()
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
					 * @param scale		the scale that is combined with
					 *			the current scale factor.
					 *------------------------------------------------------*/
	public		void		scale(MiScale scale)
		{
		scaleFactor.combine(scale);
		notifyObserver();
		}
					/**------------------------------------------------------
					 * Adjust the world translation by the given amount.
					 * @param vector	the translation that is combined with
					 *			the current translation.
					 *------------------------------------------------------*/
	public		void		translate(MiVector vector)
		{
		wTranslation.add(vector);
		notifyObserver();
		}
	public		void		translateAfter(MiVector vector)
		{
		dTranslation.add(vector);
		notifyObserver();
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
		rotation += radians;
		sinR = Math.sin(rotation);
		cosR = Math.cos(rotation);
		notifyObserver();
		}
					/**------------------------------------------------------
					 * Set the scale factor to the given amount.
					 * @param scale		the new scale factor.
					 *------------------------------------------------------*/
	public		void		setScale(MiScale scale)
		{
		scaleFactor.copy(scale);
		notifyObserver();
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
		wTranslation.copy(vector);
		notifyObserver();
		}
					/**------------------------------------------------------
					 * Get the world translation.
					 * @param vector	the (returned) translation.
					 * @return 		the translation.
					 *------------------------------------------------------*/
	public		MiVector	getWorldTranslation(MiVector vector)
		{
		vector.copy(wTranslation);
		return(vector);
		}
					/**------------------------------------------------------
					 * Set the device translation to the given amount.
					 * @param vector	the new translation.
					 *------------------------------------------------------*/
	public		void		setDeviceTranslation(MiVector vector)
		{
		dTranslation.copy(vector);
		notifyObserver();
		}
					/**------------------------------------------------------
					 * Get the device translation.
					 * @param vector	the (returned) translation.
					 * @return 		the translation.
					 *------------------------------------------------------*/
	public		MiVector	getDeviceTranslation(MiVector vector)
		{
		vector.copy(dTranslation);
		return(vector);
		}
	public		void		setRotation(double radians)
		{
		rotation = radians;
		sinR = Math.sin(rotation);
		cosR = Math.cos(rotation);
		notifyObserver();
		}
	public		double		getRotation()
		{
		return(rotation);
		}
	public		void		setRotationPoint(MiPoint point)
		{
		center.x = point.x;
		center.y = point.y;
		notifyObserver();
		}
	public		MiPoint		getRotationPoint(MiPoint center)
		{
		center.copy(this.center);
		return(center);
		}
	public		void		setRotationDegrees(double degrees)
		{ 
		rotation = degrees * Math.PI/180; 
		sinR = Math.sin(rotation);
		cosR = Math.cos(rotation);
		notifyObserver();
		}
	public		double		getRotationDegrees()
		{
		return(rotation * 180/Math.PI);
		}
					/**------------------------------------------------------
					 * Convert the coordinate in world space to it's
					 * corresponding coordinate in device space.
					 * @param wpt		the world space coordinate
					 * @param dpt		the (returned) device space coordinate
					 *------------------------------------------------------*/
	public		void		wtod(MiPoint wpt, MiDevicePoint dpt)
		{
		MiCoord x = (wpt.x - center.x)/scaleFactor.x;
		MiCoord dx = wTranslation.x / scaleFactor.x + dTranslation.x;

		MiCoord y = (wpt.y - center.y) / scaleFactor.y;
		MiCoord dy = wTranslation.y / scaleFactor.y + dTranslation.y;

		if (rotation != 0)
			{
			x = -sinR * y + cosR * x;
			y = sinR * x + cosR * y;
			}
		x += center.x;
		y += center.y;

		dpt.x = (int )(x > 0 ? x + 0.5 : x - 0.5);
		dpt.y = (int )(y > 0 ? y + 0.5 : y - 0.5);

		dpt.x += (int )(dx > 0 ? dx + 0.5 : dx - 0.5);
		dpt.y += (int )(dy > 0 ? dy + 0.5 : dy - 0.5);
		}
					/**------------------------------------------------------
					 * Convert the coordinate in world space to it's
					 * corresponding coordinate in device space.
					 * @param wpt		the world space point
					 * @param dpt		the (returned) device space point
					 *------------------------------------------------------*/
	public		 void		wtod(MiPoint wpt, MiPoint dpt)
		{
		MiCoord x = (wpt.x - wTranslation.x - center.x) / scaleFactor.x + dTranslation.x;
		MiCoord y = (wpt.y - wTranslation.y - center.y) / scaleFactor.y + dTranslation.y;

		if (rotation != 0)
			{
			dpt.x = -sinR * y + cosR * x + center.x;
			dpt.y = sinR * x + cosR * y + center.y;
			}
		else
			{
			dpt.x = x + center.x;
			dpt.y = y + center.y;
			}
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
		if (rotation != 0)
			{
			// wBounds may or may not reference the same object as dBounds
			if (wBounds.isReversed())
				{
				dBounds.reverse();
				return;
				}
			}

		wtmp.x = wBounds.xmin;
		wtmp.y = wBounds.ymin;
		wtod(wtmp, dtmp);
		dBounds.xmin = dtmp.x;
		dBounds.ymin = dtmp.y;

		wtmp.x = wBounds.xmax;
		wtmp.y = wBounds.ymax;
		wtod(wtmp, dtmp);
		dBounds.xmax = dtmp.x;
		dBounds.ymax = dtmp.y;

		if (rotation != 0)
			{
			if (dBounds.xmin > dBounds.xmax)
				{
				dtmp.x = dBounds.xmin;
				dBounds.xmin = dBounds.xmax;
				dBounds.xmax = dtmp.x;
				}
			else if (dBounds.ymin > dBounds.ymax)
				{
				dtmp.y = dBounds.ymin;
				dBounds.ymin = dBounds.ymax;
				dBounds.ymax = dtmp.y;
				}
			}
		}
					/**------------------------------------------------------
					 * Convert the given world space bounds to integer device
					 * space bounds. 
					 * @param wBounds	the world space bounds
					 * @param dBounds	the (returned) device space bounds
					 *------------------------------------------------------*/
	public		 void		wtod(MiBounds wBounds, MiDeviceBounds dBounds)
		{
		if (rotation != 0)
			{
			// wBounds may or may not reference the same object as dBounds
			if (wBounds.isReversed())
				{
				dBounds.reverse();
				return;
				}
			}

		wtmp.x = wBounds.xmin;
		wtmp.y = wBounds.ymin;
		wtod(wtmp, devtmp);
		dBounds.xmin = devtmp.x;
		dBounds.ymin = devtmp.y;

		wtmp.x = wBounds.xmax;
		wtmp.y = wBounds.ymax;
		wtod(wtmp, devtmp);
		dBounds.xmax = devtmp.x;
		dBounds.ymax = devtmp.y;

		if (rotation != 0)
			{
			if (dBounds.xmin > dBounds.xmax)
				{
				devtmp.x = dBounds.xmin;
				dBounds.xmin = dBounds.xmax;
				dBounds.xmax = devtmp.x;
				}
			else if (dBounds.ymin > dBounds.ymax)
				{
				devtmp.y = dBounds.ymin;
				dBounds.ymin = dBounds.ymax;
				dBounds.ymax = devtmp.y;
				}
			}
		}
					/**------------------------------------------------------
					 * Convert the coordinate in integer device space to it's
					 * corresponding coordinate in world space.
					 * @param dpt		the device space coordinate
					 * @param wpt		the (returned) world space coordinate
					 *------------------------------------------------------*/
	public		void		dtow(MiDevicePoint dpt, MiPoint wpt)
		{
		dtmp.x = dpt.x;
		dtmp.y = dpt.y;
		dtow(dtmp, wpt);
		}
					/**------------------------------------------------------
			 		 * Convert the coordinate in device space to it's
			 		 * corresponding coordinate in world space.
			 		 * @param dpt		the device space coordinate
			 		 * @param wpt		the (returned) world space coordinate
			 		 *------------------------------------------------------*/
	public		 void		dtow(MiPoint dpt, MiPoint wpt)
		{
		MiCoord x = dpt.x - center.x - dTranslation.x;
		MiCoord y = dpt.y - center.y - dTranslation.y;

		if (scaleFactor.x != 1.0)
			x = x * scaleFactor.x;
		if (scaleFactor.y != 1.0)
			y = y * scaleFactor.y;

		if (rotation != 0)
			{
			MiCoord tmp;
			if (rotation == 90)
				{
				tmp = -y;
				y = x;
				}
			else
				{
				tmp = -sinR * x + cosR * y;
				y = sinR * y + cosR * x;
				}
			x = tmp;
			}
		wpt.x = x + wTranslation.x + center.x;
		wpt.y = y + wTranslation.y + center.y;
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
		if (rotation != 0)
			{
			// wBounds may or may not reference the same object as dBounds
			if (dBounds.isReversed())
				{
				wBounds.reverse();
				return;
				}
			}

		dtmp.x = dBounds.xmin;
		dtmp.y = dBounds.ymin;
		dtow(dtmp, wtmp);
		wBounds.xmin = wtmp.x;
		wBounds.ymin = wtmp.y;

		dtmp.x = dBounds.xmax;
		dtmp.y = dBounds.ymax;
		dtow(dtmp, wtmp);
		wBounds.xmax = wtmp.x;
		wBounds.ymax = wtmp.y;

		if (rotation != 0)
			{
			if (wBounds.xmin > wBounds.xmax)
				{
				wtmp.x = wBounds.xmin;
				wBounds.xmin = wBounds.xmax;
				wBounds.xmax = wtmp.x;
				}
			else if (wBounds.ymin > wBounds.ymax)
				{
				wtmp.y = wBounds.ymin;
				wBounds.ymin = wBounds.ymax;
				wBounds.ymax = wtmp.y;
				}
			}
		}
					/**------------------------------------------------------
			 		 * Convert the given integer device space bounds to world
					 * space bounds. The given bounds may reference the same 
					 * MiBounds instance.
			 		 * @param dBounds	the device space bounds
			 		 * @param wBounds	the (returned) world space bounds
			 		 *------------------------------------------------------*/
	public		 void		dtow(MiDeviceBounds dBounds, MiBounds wBounds)
		{
		if (rotation != 0)
			{
			// wBounds may or may not reference the same object as dBounds
			if (dBounds.isReversed())
				{
				wBounds.reverse();
				return;
				}
			}

		dtmp.x = dBounds.xmin;
		dtmp.y = dBounds.ymin;
		dtow(dtmp, wtmp);
		wBounds.xmin = wtmp.x;
		wBounds.ymin = wtmp.y;

		dtmp.x = dBounds.xmax;
		dtmp.y = dBounds.ymax;
		dtow(dtmp, wtmp);
		wBounds.xmax = wtmp.x;
		wBounds.ymax = wtmp.y;

		if (rotation != 0)
			{
			if (wBounds.xmin > wBounds.xmax)
				{
				wtmp.x = wBounds.xmin;
				wBounds.xmin = wBounds.xmax;
				wBounds.xmax = wtmp.x;
				}
			else if (wBounds.ymin > wBounds.ymax)
				{
				wtmp.y = wBounds.ymin;
				wBounds.ymin = wBounds.ymax;
				wBounds.ymax = wtmp.y;
				}
			}
		}
					/**------------------------------------------------------
					 * Prints information about this transform.
					 *------------------------------------------------------*/
	public		String		toString()
		{
		MiScale tmpScale = new MiScale();
		return(MiDebug.getMicaClassName(this)
				+ ": <device: " + getDeviceTranslation(new MiVector())
				+ "> <" + getScale(tmpScale)
				+ "> <world: " + getWorldTranslation(new MiVector()) + ">");
		}
	}


