
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
public class MiSimpleTransform 
	{
	private		MiScale		scale;
	private		MiDistance	preScaleTx;
	private		MiDistance	preScaleTy;
	private		MiDistance	postScaleTx;
	private		MiDistance	postScaleTy;

	public				MiSimpleTransform(
						MiScale scale, 
						MiDistance preScaleTx, 
						MiDistance preScaleTy, 
						MiDistance postScaleTx, 
						MiDistance postScaleTy)
		{
		this.scale = scale;
		this.preScaleTx = preScaleTx;
		this.preScaleTy = preScaleTy;
		this.postScaleTx = postScaleTx;
		this.postScaleTy = postScaleTy;
		}

	public		void		setScale(MiScale scale)
		{
		this.scale = scale;
		}
	public		MiScale		getScale()
		{
		return(scale);
		}

	public		void		setPreScaleTranslationX(MiDistance tx)
		{
		this.preScaleTx = tx;
		}
	public		MiDistance	getPreScaleTranslationX()
		{
		return(preScaleTx);
		}

	public		void		setPreScaleTranslationY(MiDistance ty)
		{
		this.preScaleTy = ty;
		}
	public		MiDistance	getPreScaleTranslationY()
		{
		return(preScaleTy);
		}

	public		void		setPostScaleTranslationX(MiDistance tx)
		{
		this.postScaleTx = tx;
		}
	public		MiDistance	getPostScaleTranslationX()
		{
		return(postScaleTx);
		}

	public		void		setPostScaleTranslationY(MiDistance ty)
		{
		this.postScaleTy = ty;
		}
	public		MiDistance	getPostScaleTranslationY()
		{
		return(postScaleTy);
		}

	public		MiPoint		transformPoint(MiPoint pt)
		{
		pt.x = ((pt.x + preScaleTx) * scale.x + postScaleTx);
		pt.y = ((pt.y + preScaleTy) * scale.y + postScaleTy);
		return(pt);
		}
	public		MiCoord		transformXCoord(MiCoord x)
		{
		return((x + preScaleTx) * scale.x + postScaleTx);
		}
	public		MiCoord		transformYCoord(MiCoord y)
		{
		return((y + preScaleTy) * scale.y + postScaleTy);
		}
	public		MiDistance	transformXDistance(MiDistance x)
		{
		return(x * scale.x);
		}
	public		MiDistance	transformYDistance(MiDistance y)
		{
		return(y * scale.y);
		}
	public		String		toString()
		{
		return(super.toString() 
			+ "[scale=(" + scale.x + "," + scale.y 
			+ "), preTranslation=(" + preScaleTx + "," + preScaleTy 
			+ "), postTranslation=(" + postScaleTx + "," + postScaleTy + ")]");
		}
	}
	
