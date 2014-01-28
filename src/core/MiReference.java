
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
public class MiReference extends MiContainer
	{
	private		MiiTransform 	transform 		= new MiGeneralTransform();
					// Otherwise translate/scale action points of 
					// the parts in this reference instead
	private		boolean		useTranslateTransform	= true;
	private		boolean		useScaleTransform	= true;
	private		boolean		useRotateTransform	= true;
	private		MiBounds	clipBounds;
	private		MiBounds	tmpBounds		= new MiBounds();
	private		MiVector	tmpVector		= new MiVector();



	public				MiReference()
		{
		}
	public				MiReference(MiPart obj)
		{
		appendPart(obj);
		}

	public		void		setTransform(MiiTransform t)
		{
		transform = t;
		}
	public		MiiTransform	getTransform()
		{
		return(transform);
		}

	public		void		setUseScaleTransform(boolean flag)
		{
		useScaleTransform = flag;
		}
	public		boolean		getUseScaleTransform()
		{
		return(useScaleTransform);
		}

	public		void		setUseTranslateTransform(boolean flag)
		{
		useTranslateTransform = flag;
		}
	public		boolean		getUseTranslateTransform()
		{
		return(useTranslateTransform);
		}
	/**
	 * Set this to false if moving something because center of rotation needs to move and rotation transform does not do that
	 **/
	public		void		setUseRotateTransform(boolean flag)
		{
		useRotateTransform = flag;
		}
	public		boolean		getUseRotateTransform()
		{
		return(useRotateTransform);
		}
	public		void		draw(MiRenderer renderer)
		{
		if ((!isVisible()) || (isHidden()))
			return;

		MiBounds objBounds = MiBounds.newBounds();
		if (!renderer.boundsClipped(getBounds(objBounds)))
			{
			if (clipBounds != null)
				{
				MiBounds oldClipBounds = MiBounds.newBounds();
				MiBounds newClipBounds = MiBounds.newBounds();

				renderer.getClipBounds(oldClipBounds);
				newClipBounds.setBounds(oldClipBounds);
				if (newClipBounds.intersectionWith(clipBounds))
					{
					renderer.setClipBounds(newClipBounds);
					renderer.pushTransform(transform);
					super.drawNoClip(renderer);
					renderer.popTransform();
					renderer.setClipBounds(oldClipBounds);
					}
				MiBounds.freeBounds(oldClipBounds);
				MiBounds.freeBounds(newClipBounds);
				}
			else
				{
				renderer.pushTransform(transform);
				super.drawNoClip(renderer);
				renderer.popTransform();
				}
			}
		MiBounds.freeBounds(objBounds);
		}
	public		void		setClipBounds(MiBounds b)
		{
		if (b == null)
			clipBounds = null;
		else
			clipBounds = new MiBounds(b);
		}
	public		void		translate(MiDistance x, MiDistance y)
		{
		if (useTranslateTransform)
			{
			transform.getDeviceTranslation(tmpVector);
			tmpVector.x += x;
			tmpVector.y += y;
			transform.setDeviceTranslation(tmpVector);
			refreshBounds();
			}
		else
			{
			super.translate(x, y);
			}
		}
	public		void		scale(MiPoint center, MiScale scale)
		{
		if (useScaleTransform)
			{
			transform.scale(scale.inverseOf());
			refreshBounds();
			}
		else
			{
			super.scale(center, scale);
			}
		}

	public 		void		rotate(double radians)
		{
		if (!useRotateTransform)
			{
			super.reCalcBounds(tmpBounds);
			super.rotate(tmpBounds.getCenter(), radians);
			}
		else
			{
			super.rotate(radians);
			}
		}
	public 		void		rotate(MiPoint center, double radians)
		{
		if (useRotateTransform)
			{
			transform.setRotationPoint(center);
			transform.rotate(radians);
			refreshBounds();
			}
		else
			{
			super.rotate(center, radians);
			}
		}
	public 		void		flip(double radians)
		{
		if (!useRotateTransform)
			{
			super.reCalcBounds(tmpBounds);
			super.flip(tmpBounds.getCenter(), radians);
			}
		else
			{
			super.flip(radians);
			}
		}
	public 		void		flip(MiPoint center, double radians)
		{
		if (useRotateTransform)
			{
			throw new RuntimeException("Flip is unimplemented for References");
			}
		else
			{
			super.flip(center, radians);
			}
		}
	protected	void		translateParts(MiDistance tx, MiDistance ty)
		{
		if (!useTranslateTransform)
			super.translateParts(tx, ty);
		else
			translate(tx, ty);
		}

	protected	void		scaleParts(MiPoint center, MiScale scale)
		{
		if (!useScaleTransform)
			super.scaleParts(center, scale);
		}
	public		void		setBounds(MiBounds newBounds)
		{
		if (getBounds(tmpBounds).equals(newBounds))
			return;

		if (getBounds(tmpBounds).isReversed())
			refreshBounds();
		if (getBounds(tmpBounds).isReversed())
			{
			replaceBounds(newBounds);
			return;
			}
		
		scale(newBounds.getWidth()/getWidth(), 
			newBounds.getHeight()/getHeight());

		translate(newBounds.getCenterX() - getCenterX(),
			newBounds.getCenterY() - getCenterY());
		}

	public		void		appendPart(MiPart obj)
		{
		super.appendPart(obj);
		refreshBounds();
		}
	public		void		insertPart(MiPart obj, int index)
		{
		super.insertPart(obj, index);
		refreshBounds();
		}
	public		MiBounds	getInnerBounds(MiBounds b)
		{
		super.reCalcBounds(b);
		return(b);
		}
	
	protected	void		reCalcBounds(MiBounds bounds)
		{
		super.reCalcBounds(bounds);
		transform.wtod(bounds, bounds);
		}
	protected	void		getReferencedOuterBounds(MiBounds bounds)
		{
		//super.reCalcBounds(bounds);
		getBounds(bounds);
		transform.dtow(bounds, bounds);
		}
		
	protected	void		reCalcDrawBounds(MiBounds bounds)
		{
		super.reCalcDrawBounds(bounds);
		transform.wtod(bounds, bounds);
		}
					/**------------------------------------------------------
					 * Gets the MiSnapPointManager, if any, assigned to
					 * the referenced MiPart. 
					 * @return		the snap point manager or null
					 *------------------------------------------------------*/
	public		MiSnapPointManager	getSnapPointManager()
		{
		if (getNumberOfParts() > 0)
			{
			return(getPart(0).getSnapPointManager());
			}
		return(null);
		}
	}

