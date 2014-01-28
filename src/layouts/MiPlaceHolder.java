
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
public class MiPlaceHolder extends MiPart
	{
	private		MiPart		shape;
	private		MiBounds	tmpBounds	= new MiBounds();

	public				MiPlaceHolder()
		{
		MiSize size = new MiSize();
		getPreferredSize(size);
		tmpBounds.setSize(size);
		replaceBounds(tmpBounds);
		}
	public				MiPlaceHolder(MiPart shape)
		{
		this();
		this.shape = shape;
		}
	public		void		setShape(MiPart shape)
		{
		this.shape = shape;
		}
	public		MiPart		getShape()
		{
		return(shape);
		}

	public		boolean		getIntersectionWithLine(
						MiPoint insidePoint, 
						MiPoint otherPoint, 
						MiPoint returnedIntersectionPoint)
		{
		if (shape != null)
			{
			shape.setAttributes(getAttributes());
			shape.setBounds(getBounds(tmpBounds));
			return(shape.getIntersectionWithLine(
				insidePoint, otherPoint, returnedIntersectionPoint));
			}
		return(super.getIntersectionWithLine(insidePoint, otherPoint, returnedIntersectionPoint));
		}

	protected	void		render(MiRenderer renderer)
		{
		if (shape != null)
			{
			shape.setAttributes(getAttributes());
			shape.setBounds(getBounds(tmpBounds));
			shape.draw(renderer);
			}
		}
	public		MiPart		copy()
		{
		MiPlaceHolder obj = (MiPlaceHolder )super.copy();
		if (shape != null)
			obj.setShape(shape.copy());
		return(obj);
		}
	public	String[] 		getSupportedImportFormats()
		{
		String[] formats = new String[1];
		formats[0] = Mi_MiPART_FORMAT;
		return(formats);
		}
					/**------------------------------------------------------
					 * Gets the shape of any shadow. Used by the shadow 
					 * renderers. This method returns MiiShadowRenderer.noShadowShape
					 * because when the shape associated with this is rendered
					 * _it_ will draw the shadow.
					 * @return		the shape
					 * @overrides		MiPart#getShadowShape
					 *------------------------------------------------------*/
	public		MiPart		getShadowShape()
		{
		return(MiiShadowRenderer.noShadowShape);
		}
	public	void			doImport(MiDataTransferOperation transfer)
		{
		MiPart obj = null;
		if (transfer.getDataFormat().equals(Mi_STRING_FORMAT))
			obj = new MiText((String )transfer.getData());
		else // if (transfer.getDataFormat().equals(Mi_MiPART_FORMAT))
			obj = (MiPart )transfer.getData();

		MiReplacePartsCommand cmd = new MiReplacePartsCommand(
			getContainingEditor(), 
			new MiParts(this), new MiParts(obj));
		MiSystem.getTransactionManager().appendTransaction(cmd);

		replaceSelf(obj);
		deleteSelf();
		}
					/**------------------------------------------------------
					 * Gets the graphics to be used to highlight this MiPart
					 * when it is selected. If this is not null (the default)
					 * it overrides the selection graphics as specified in the
					 * MiSelectionManager.
					 * @return		the selection graphics
					 * @overrides		MiPart#getSelectionGraphics
					 *------------------------------------------------------*/
	public		MiiSelectionGraphics	getSelectionGraphics()
		{
		return(new MiBoxSelectionGraphics());
		}

	}

