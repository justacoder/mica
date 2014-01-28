
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
 * This class represents a data transfer operation, usually associated
 * with a drag-and-drop operation or a 'paste from clipboard' operation.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiDataTransferOperation
	{
	private		MiPart		look;
	private		MiPoint		lookTargetLocation	= new MiPoint();
	private		MiBounds	lookTargetBounds;
	private		String		dataFormat;
	private		MiPart		source;
	private		MiPart		target;
	private		Object		data;
	private		Object		transferredData;
	private		boolean		isDragAndCut;


					/**------------------------------------------------------
	 				 * Constructs a new MiDataTransferOperation for the given 
					 * source part.
					 * @param source	the source of the data to be 
					 *			transferred
					 *------------------------------------------------------*/
	public				MiDataTransferOperation(MiPart source)
		{
		this.source = source;
		}

	protected	void		setIsDragAndCut(boolean flag)
		{
		isDragAndCut = flag;
		}
	public		boolean		isDragAndCut()
		{
		return(isDragAndCut);
		}

					/**------------------------------------------------------
	 				 * Set the data to be transferred. This overrides the usual
					 * process of getting data exported from the source.
					 * @param data		the data to transfer
					 *------------------------------------------------------*/
	public		void		setData(Object data)
		{
		this.data = data;
		}
					/**------------------------------------------------------
	 				 * Get the data to be transferred. 
					 * @return 		the data in the current format
					 *------------------------------------------------------*/
	public		Object		getData()
		{
		if (data != null)
			{
			return(data);
			}
		data = source.doExport(dataFormat);
		return(data);
		}
					/**------------------------------------------------------
	 				 * Sets the data that was transferred. This is set when
					 * the data was imported so that others can post-processes
					 * the data.
					 * @param data		the data that was transferred
					 *------------------------------------------------------*/
	public		void		setTransferredData(Object data)
		{
		transferredData = data;
		}
					/**------------------------------------------------------
	 				 * Gets the data that was transferred. This is set when
					 * the data was imported so that others can post-processes
					 * the data.
					 * @return 		the data that was transferred or null
					 *------------------------------------------------------*/
	public		Object		getTransferredData()
		{
		return(transferredData);
		}
					/**------------------------------------------------------
	 				 * Set the data to be transferred. 
					 * @return 		the data in the current format
					 *------------------------------------------------------*/
	public		void		setSource(MiPart src)
		{
		source = src;
		}
	public		MiPart		getSource()
		{
		return(source);
		}
	public		void		setDataFormat(String flavor)
		{
		dataFormat = flavor;
		}
	public		String		getDataFormat()
		{
		return(dataFormat);
		}
	public		void		setTarget(MiPart obj)
		{
		target = obj;
		}
	public		MiPart		getTarget()
		{
		return(target);
		}
	public		void		setLook(MiPart part)
		{
		look = part;
		}
	public		MiPart		getLook()
		{
		return(look);
		}
					/**------------------------------------------------------
	 				 * Sets the location of the cursor that is dragging the part
					 * @param pt	location of cursor in target editor coordinates
					 * @see		#getLookTargetPosition
					 * @see		#getLookTargetBounds
					 *------------------------------------------------------*/
	public		void		setLookTargetPosition(MiPoint pt)
		{
		lookTargetLocation.copy(pt);
		}
					/**------------------------------------------------------
	 				 * Gets the location of the cursor that is dragging the part
					 * @return	location of cursor in target editor coordinates
					 * @see		#getLookTargetBounds
					 * @see		#setLookTargetPosition
					 *------------------------------------------------------*/
	public		MiPoint		getLookTargetPosition()
		{
		return(lookTargetLocation);
		}
					/**------------------------------------------------------
	 				 * Sets the bounds of the graphics that is being dragged
					 * in target editor coordinates
					 * @param b	bounds of graphics in target editor coordinates
					 * @see		#getLookTargetPosition
					 * @see		#getLookTargetBounds
					 * @see		#setLookTargetPosition
					 *------------------------------------------------------*/
	public		void		setLookTargetBounds(MiBounds b)
		{
		if (lookTargetBounds == null)
			lookTargetBounds = new MiBounds();
		lookTargetBounds.copy(b);
		}
					/**------------------------------------------------------
	 				 * Gets the bounds of the graphics that is being dragged
					 * in target editor coordinates
					 * @return	bounds of graphics in target editor coordinates
					 * @see		#getLookTargetPosition
					 * @see		#setLookTargetBounds
					 *------------------------------------------------------*/
	public		MiBounds	getLookTargetBounds()
		{
		return(lookTargetBounds);
		}
	public static	String		getCommonDataFormat(
						MiiDragAndDropParticipant source,
						MiiDragAndDropParticipant target)
		{
		String[] imports = target.getSupportedImportFormats();
		String[] exports = source.getSupportedExportFormats();
		for (int i = 0; (imports != null) && (i < imports.length); ++i)
			{
			for (int j = 0; j < exports.length; ++j)
				{
				if (imports[i].equals(exports[j]))
					{
					return(imports[i]);
					}
				}
			}
		return(null);
		}
	}


