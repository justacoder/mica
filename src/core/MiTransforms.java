
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
import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.Utility;

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiTransforms implements MiiTransform, MiiCommandHandler
	{
	private static 	int		Mi_NUM_TRANSFORMS_INCREMENT	= 5;
	private static 	double		ERROR				= 1;
	private		MiBounds	clipBounds		= new MiBounds();
	private		FastVector	transformStack	 	= new FastVector(Mi_NUM_TRANSFORMS_INCREMENT);
	private		int		numTransforms		= 0;
	private		int		totalAvailTransformSlots= 0;
	private		MiVector	wTranslation		= new MiVector();
	private		MiDeviceVector	dTranslation		= new MiDeviceVector();
	private		int		dIndentX;
	private		boolean		isPositionDependent	= true;
	private		MiScale		dtowScale		= new MiScale();
	private		MiVector	wtodVector		= new MiVector();
	private		MiPoint		tmpPoint		= new MiPoint();
	private		MiVector	tmpVector		= new MiVector();
	private		MiScale		tmpScale		= new MiScale();
	private		MiPoint		wtmp 			= new MiPoint();
	private		MiPoint		dtmp 			= new MiPoint();
	private		MiBounds	tmpBounds 		= new MiBounds();
	private		MiiCommandHandler observer;



	public				MiTransforms()
		{
		increaseNumberOfAvailableTransformSlots();
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

	public		void		setIndentX(int indent)
		{
		dTranslation.x += dIndentX;
		dIndentX = indent;
		dTranslation.x -= dIndentX;
		collapseTransforms();
		}
	public		int		getIndentX()
		{
		return(dIndentX);
		}

					/*sf------------------------------------------------------
					// Specifies the bounds of the only area where drawing is 
					// now allowed.
					/ef------------------------------------------------------*/
	public		void		setClipBounds(MiBounds bounds)
		{
		clipBounds.copy(bounds);
		((MiTransformEntry )transformStack.elementAt(numTransforms)).clipBounds.copy(clipBounds);
		}
	
	public		MiBounds	getClipBounds()
		{
		return(getClipBounds(new MiBounds()));
		}
	public		MiBounds	getClipBounds(MiBounds b)
		{
		b.copy(clipBounds);
		return(b);
		}
	
					/*sf------------------------------------------------------
					// Specifies that the entire viewport is once again allowing
					// drawing.
					/ef------------------------------------------------------*/
	public		void		clearClipBounds()
		{
		}


	public		void		pushTransform(MiiTransform transform)
		{
		if (numTransforms >= totalAvailTransformSlots)
			increaseNumberOfAvailableTransformSlots();
		((MiTransformEntry )transformStack.elementAt(numTransforms)).clipBounds.copy(clipBounds);
		((MiTransformEntry )transformStack.elementAt(numTransforms)).transform = transform;
		transform.dtow(clipBounds, clipBounds);
		transform.setObserver(this);
		++numTransforms;
		collapseTransforms();
		}

	public		void		invalidateTransform()
		{
		collapseTransforms();
		}
	public		void		validateTransform()
		{
		collapseTransforms();
		}
	public		void		processCommand(String command)
		{
		if (!isPositionDependent)
			invalidateTransform();
		}

	private		void		collapseTransforms()
		{
		isPositionDependent = false;
		for (int i = 0; i < numTransforms; ++i)
			{
			if (((MiTransformEntry )transformStack.elementAt(i)).transform.isPositionDependent())
				{
				isPositionDependent = true;
				break;
				}
			}
		if (!isPositionDependent)
			{
			dtowScale.set(1, 1);
			wtodVector.set(dTranslation.x, dTranslation.y);
			for (int i = 0; i < numTransforms; ++i)
				{
				MiiTransform xform = ((MiTransformEntry )transformStack.elementAt(i)).transform;
				MiScale scale = xform.getScale(tmpScale);
				dtowScale.combine(scale);

				wtodVector.x = 
					scale.x * (wtodVector.x
					+ xform.getDeviceTranslation(tmpVector).x)
					- xform.getWorldTranslation(tmpVector).x;
				wtodVector.y = 
					scale.y * (wtodVector.y
					+ xform.getDeviceTranslation(tmpVector).y)
					- xform.getWorldTranslation(tmpVector).y;
				}
			wtodVector.x += wTranslation.x;
			wtodVector.y += wTranslation.y;
			}
		}

	public		void		popTransform()
		{
		--numTransforms;
		((MiTransformEntry )transformStack.elementAt(numTransforms)).transform.setObserver(null);
		clipBounds.copy(((MiTransformEntry )transformStack.elementAt(numTransforms)).clipBounds);
		collapseTransforms();
		}

	public		void		clearTransforms()
		{
		numTransforms = 0;
		isPositionDependent = true;
		}
	
	private		MiiTransform	getTransform(int i)
		{
		return(((MiTransformEntry )transformStack.elementAt(i)).transform);
		}

					/**------------------------------------------------------
					 * Return 'True' if rejected, i.e. the extrema, in the world
					 * coordinates specified, is entirely outside the current view.
					 *------------------------------------------------------*/
	public		boolean		boundsClipped(MiBounds bounds)
		{
		// Use an error so that we draw a little more area than what we erased
		// so that lines that are outisde the area by a miniscule fraction are
		// redrawn after they are erased by a nearby edit.
		return(((bounds.xmin >= clipBounds.xmax + ERROR) 
			|| (bounds.ymin >= clipBounds.ymax + ERROR)
			|| (bounds.xmax <= clipBounds.xmin - ERROR) 
			|| (bounds.ymax <= clipBounds.ymin - ERROR)) 
				? true : false);
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
		return(isPositionDependent);
		}
					/**------------------------------------------------------
					 * Increase/decrease the scale factor by the given amount.
					 * @param scale		the scale that is combined with
					 *			the current scale factor.
					 *------------------------------------------------------*/
	public		void		scale(MiScale scale)
		{
		}
					/**------------------------------------------------------
					 * Adjust the world translation by the given amount.
					 * @param vector	the translation that is combined with
					 *			the current translation.
					 *------------------------------------------------------*/
	public		void		translate(MiVector vector)
		{
		wTranslation.add(vector);
		wtodVector.x += vector.x;
		wtodVector.y += vector.y;
		clipBounds.translate(vector.x, vector.y);
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
		scale.setIsIdentity();
		for (int i = 0; i < numTransforms; ++i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.getScale(tmpScale);
			scale.x *= tmpScale.x;
			scale.y *= tmpScale.y;
			}
		return(scale);
		}
					/**------------------------------------------------------
					 * Set the world translation to the given amount.
					 * @param vector	the new translation.
					 *------------------------------------------------------*/
	public		void		setWorldTranslation(MiVector vector)
		{
		MiDistance changeX = vector.x - wTranslation.x;
		MiDistance changeY = vector.y - wTranslation.y;
		
		wTranslation.x = vector.x;
		wTranslation.y = vector.y;
		wtodVector.x += changeX;
		wtodVector.y += changeY;
		clipBounds.translate(changeX, changeY);
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
		dTranslation.x = (int )vector.x;
		dTranslation.y = (int )vector.y;
		collapseTransforms();
		}
					/**------------------------------------------------------
					 * Get the device translation.
					 * @param vector	the (returned) translation.
					 * @return 		the translation.
					 *------------------------------------------------------*/
	public		MiVector	getDeviceTranslation(MiVector vector)
		{
		vector.x = dTranslation.x;
		vector.y = dTranslation.y;
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
		double rot = 0.0;
		for (int i = 0; i < numTransforms; ++i)
			{
			rot += ((MiTransformEntry )transformStack.elementAt(i)).transform.getRotation();
			}
		return(rot);
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
	public		void		translate(MiDeviceVector vector)
		{
		dTranslation.add(vector);

		tmpPoint.x = 0;
		tmpPoint.y = 0;
		tmpVector.x = vector.x;
		tmpVector.y = vector.y;
		dtow(tmpPoint, tmpVector, tmpVector);

		MiDistance tx = tmpVector.x;
		MiDistance ty = tmpVector.y;

		wtodVector.x -= tx;
		wtodVector.y -= ty;

		clipBounds.translate(-tx, -ty);
		}

					/**------------------------------------------------------
					 * Convert the coordinate in integer device space to it's
					 * corresponding coordinate in world space.
					 * @param dPoint	the device space coordinate
					 * @param wPoint	the (returned) world space coordinate
					 *------------------------------------------------------*/
	public		void		dtow(MiDevicePoint dPoint, MiPoint wPoint)
		{
		tmpPoint.x = (MiCoordType )dPoint.x;
		tmpPoint.y = (MiCoordType )dPoint.y;
		dtow(tmpPoint, wPoint);
		}
					/**------------------------------------------------------
			 		 * Convert the coordinate in device space to it's
			 		 * corresponding coordinate in world space.
			 		 * @param dPoint	the device space coordinate
			 		 * @param wPoint	the (returned) world space coordinate
			 		 *------------------------------------------------------*/
	public		void		dtow(MiPoint dPoint, MiPoint wPoint)
		{
		wPoint.x = dPoint.x - dTranslation.x;
		wPoint.y = dPoint.y - dTranslation.y;
		for (int i = 0; i < numTransforms; ++i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.dtow(wPoint, wPoint);
			}
		wPoint.x -= wTranslation.x;
		wPoint.y -= wTranslation.y;
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
		wVector.x = dVector.x;
		wVector.y = dVector.y;
		for (int i = 0; i < numTransforms; ++i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.dtow(dPoint, wVector, wVector);
			}
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
		wVector.x = dVector.x;
		wVector.y = dVector.y;
		tmpPoint.x = dPoint.x;
		tmpPoint.y = dPoint.y;
		for (int i = 0; i < numTransforms; ++i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.dtow(tmpPoint, wVector, wVector);
			}
		}
					/**------------------------------------------------------
			 		 * Convert the given device space bounds to world space
			 		 * bounds. The given bounds may reference the same 
					 * MiBounds instance.
			 		 * @param dBounds	the device space bounds
			 		 * @param wBounds	the (returned) world space bounds
			 		 *------------------------------------------------------*/
	public		void		dtow(MiBounds dBounds, MiBounds wBounds)
		{
		wBounds.xmin = dBounds.xmin - dTranslation.x;
		wBounds.ymin = dBounds.ymin - dTranslation.y;
		wBounds.xmax = dBounds.xmax - dTranslation.x;
		wBounds.ymax = dBounds.ymax - dTranslation.y;
		for (int i = 0; i < numTransforms; ++i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.dtow(wBounds, wBounds);
			}
		wBounds.xmin -= wTranslation.x;
		wBounds.ymin -= wTranslation.y;
		wBounds.xmax -= wTranslation.x;
		wBounds.ymax -= wTranslation.y;
		}
					/**------------------------------------------------------
			 		 * Convert the given integer device space bounds to world
					 * space bounds. The given bounds may reference the same 
					 * MiBounds instance.
			 		 * @param dBounds	the device space bounds
			 		 * @param wBounds	the (returned) world space bounds
			 		 *------------------------------------------------------*/
	public		void		dtow(MiDeviceBounds dBounds, MiBounds wBounds)
		{
		wBounds.xmin = dBounds.xmin - dTranslation.x;
		wBounds.ymin = dBounds.ymin - dTranslation.y;
		wBounds.xmax = dBounds.xmax - dTranslation.x;
		wBounds.ymax = dBounds.ymax - dTranslation.y;
		for (int i = 0; i < numTransforms; ++i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.dtow(wBounds, wBounds);
			}
		wBounds.xmin -= wTranslation.x;
		wBounds.ymin -= wTranslation.y;
		wBounds.xmax -= wTranslation.x;
		wBounds.ymax -= wTranslation.y;
		}
					/**------------------------------------------------------
					 * Convert the coordinate in world space to it's
					 * corresponding coordinate in device space.
					 * @param wPoint	the world space coordinate
					 * @param dPoint	the (returned) device space coordinate
					 *------------------------------------------------------*/
	public		void		wtod(MiPoint wPoint, MiDevicePoint dPoint)
		{
		if (!isPositionDependent)
			{
			MiCoord x = (wPoint.x + wtodVector.x)/dtowScale.x;
			MiCoord y = (wPoint.y + wtodVector.y)/dtowScale.y;
			dPoint.x = (int )(x > 0 ? x + 0.5 : x - 0.5);
			dPoint.y = (int )(y > 0 ? y + 0.5 : y - 0.5);
			return;
			}

		tmpPoint.x = wPoint.x + wTranslation.x;
		tmpPoint.y = wPoint.y + wTranslation.y;
		if (numTransforms > 0)
			{
			for (int i = numTransforms - 1; i >= 0; --i)
				{
				((MiTransformEntry )transformStack.elementAt(i))
					.transform.wtod(tmpPoint, tmpPoint);
				}
			}
		MiCoord x = tmpPoint.x + dTranslation.x;
		MiCoord y = tmpPoint.y + dTranslation.y;
		dPoint.x = (int )(x > 0 ? x + 0.5 : x - 0.5);
		dPoint.y = (int )(y > 0 ? y + 0.5 : y - 0.5);
		}
					/**------------------------------------------------------
					 * Convert the coordinate in world space to it's
					 * corresponding coordinate in device space.
					 * @param wPoint	the world space point
					 * @param dPoint	the device space point
					 *------------------------------------------------------*/
	public		void		wtod(MiPoint wPoint, MiPoint dPoint)
		{
		if (!isPositionDependent)
			{
			dPoint.x = (wPoint.x + wtodVector.x)/dtowScale.x;
			dPoint.y = (wPoint.y + wtodVector.y)/dtowScale.y;
			return;
			}

		dPoint.x = wPoint.x + wTranslation.x;
		dPoint.y = wPoint.y + wTranslation.y;
		for (int i = numTransforms - 1; i >= 0; --i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.wtod(dPoint, dPoint);
			}
		dPoint.x += dTranslation.x;
		dPoint.y += dTranslation.y;
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
		if (!isPositionDependent)
			{
			dVector.x = wVector.x/dtowScale.x;
			dVector.y = wVector.y/dtowScale.y;
			return;
			}
		dVector.x = wVector.x;
		dVector.y = wVector.y;

		for (int i = numTransforms - 1; i >= 0; --i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.wtod(wPoint, dVector, dVector);
			}
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
		MiDistance x;
		MiDistance y;
		if (!isPositionDependent)
			{
			x = wVector.x/dtowScale.x;
			y = wVector.y/dtowScale.y;
			dVector.x = (int )(x > 0 ? x + 0.5 : x - 0.5);
			dVector.y = (int )(y > 0 ? y + 0.5 : y - 0.5);
			return;
			}
		tmpVector.x = wVector.x;
		tmpVector.y = wVector.y;

		for (int i = numTransforms - 1; i >= 0; --i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.wtod(wPoint, tmpVector, tmpVector);
			}
		dVector.x = (int )(tmpVector.x > 0 ? tmpVector.x + 0.5 : tmpVector.x - 0.5);
		dVector.y = (int )(tmpVector.y > 0 ? tmpVector.y + 0.5 : tmpVector.y - 0.5);
		}
					/**------------------------------------------------------
					 * Convert the given world space bounds to device space
			 		 * bounds. The given bounds may reference the same 
					 * MiBounds instance.
					 * @param wBounds	the world space bounds
					 * @param dBounds	the (returned) device space bounds
					 *------------------------------------------------------*/
	public		void		wtod(MiBounds wBounds, MiBounds dBounds)
		{
		dBounds.xmin = wBounds.xmin + wTranslation.x;
		dBounds.ymin = wBounds.ymin + wTranslation.y;
		dBounds.xmax = wBounds.xmax + wTranslation.x;
		dBounds.ymax = wBounds.ymax + wTranslation.y;
		//if (numTransforms == 0)
			//return;
			
		for (int i = numTransforms - 1; i >= 0; --i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.wtod(dBounds, dBounds);
			}
		dBounds.xmin += dTranslation.x;
		dBounds.xmax += dTranslation.x;
		dBounds.ymin += dTranslation.y;
		dBounds.ymax += dTranslation.y;
		}
					/**------------------------------------------------------
					 * Convert the given world space bounds to integer device
					 * space bounds. 
					 * @param wBounds	the world space bounds
					 * @param dBounds	the (returned) device space bounds
					 *------------------------------------------------------*/
	public		void		wtod(MiBounds wBounds, MiDeviceBounds dBounds)
		{
		tmpBounds.xmin = wBounds.xmin + wTranslation.x;
		tmpBounds.ymin = wBounds.ymin + wTranslation.y;
		tmpBounds.xmax = wBounds.xmax + wTranslation.x;
		tmpBounds.ymax = wBounds.ymax + wTranslation.y;
			
		for (int i = numTransforms - 1; i >= 0; --i)
			{
			((MiTransformEntry )transformStack.elementAt(i)).transform.wtod(tmpBounds, tmpBounds);
			}
		dBounds.copy(tmpBounds);
		dBounds.xmin += dTranslation.x;
		dBounds.ymin += dTranslation.y;
		dBounds.xmax += dTranslation.x;
		dBounds.ymax += dTranslation.y;
		}

					/**------------------------------------------------------
					 * Prints information about this transform.
					 *------------------------------------------------------*/
	public		String		toString()
		{
		MiVector dVector = new MiVector();
		MiVector wVector = new MiVector();
		MiScale scale = new MiScale();
		for (int i = 0; i < numTransforms; ++i)
			{
			MiiTransform t =  ((MiTransformEntry )transformStack.elementAt(i)).transform;
			dVector.add(t.getDeviceTranslation(tmpVector));
			wVector.add(t.getWorldTranslation(tmpVector));
			scale.combine(t.getScale(tmpScale));
			}
		dVector.add(getDeviceTranslation(tmpVector));
		wVector.add(getWorldTranslation(tmpVector));
		scale.combine(getScale(tmpScale));

		return(MiDebug.getMicaClassName(this)
				+ ": <device: " + dVector
				+ "> <" + scale
				+ "> <world: " + wVector + ">");
		}
	public		void		getTotalDeviceTranslation(MiVector dVector)
		{
		for (int i = 0; i < numTransforms; ++i)
			{
			MiiTransform t =  ((MiTransformEntry )transformStack.elementAt(i)).transform;
			dVector.add(t.getDeviceTranslation(tmpVector));
			}
		dVector.add(getDeviceTranslation(tmpVector));
		}

	private		void		increaseNumberOfAvailableTransformSlots()
		{
		for (int i = totalAvailTransformSlots; 
			i < totalAvailTransformSlots + Mi_NUM_TRANSFORMS_INCREMENT; ++i)
			{
			transformStack.addElement(new MiTransformEntry());
			}
		totalAvailTransformSlots += Mi_NUM_TRANSFORMS_INCREMENT;
		}

	}
class MiTransformEntry
	{
		MiiTransform		transform;
		MiBounds		clipBounds 		= new MiBounds();
	}

