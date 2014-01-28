
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
public class MiManagedPointValidator implements MiiManagedPointValidator, MiiActionTypes
	{
	private		MiConnectionOperation	connectOp;

	public				MiManagedPointValidator()
		{
		}
	public				MiManagedPointValidator(MiConnectionOperation connectOp)
		{
		this.connectOp = connectOp;
		}
	public		void		setConnectionOperation(MiConnectionOperation connectOp)
		{
		this.connectOp = connectOp;
		}
	public		MiConnectionOperation	getConnectionOperation()
		{
		return(connectOp);
		}

	public		boolean		isValidConnectionSource(MiPart src, MiPart dest)
		{
		connectOp.setSource(src);
		connectOp.setSourceConnPt(MiiTypes.Mi_CENTER_LOCATION);
		if (!src.isValidConnectionSource(connectOp))
			return(false);
		return(src.dispatchActionRequest(Mi_CONNECT_ACTION, connectOp));
		}
	public		boolean		isValidConnectionSource(MiPart src, 
								int srcConnPt, 
								MiPart dest, 
								int destConnPt)
		{
		connectOp.setSource(src);
		connectOp.setSourceConnPt(srcConnPt);
		connectOp.setDestination(dest);
		connectOp.setDestinationConnPt(destConnPt);
		if (!src.isValidConnectionSource(connectOp))
			return(false);
		return(src.dispatchActionRequest(Mi_CONNECT_ACTION, connectOp));
		}
	public		boolean		isValidConnectionDestination(MiPart src, MiPart dest)
		{
		connectOp.setSource(src);
		connectOp.setSourceConnPt(MiiTypes.Mi_CENTER_LOCATION);
		connectOp.setDestination(dest);
		connectOp.setDestinationConnPt(MiiTypes.Mi_CENTER_LOCATION);
		if (!dest.isValidConnectionDestination(connectOp))
			return(false);
		return(dest.dispatchActionRequest(Mi_CONNECT_ACTION, connectOp));
		}
	public		boolean		isValidConnectionDestination(
								MiPart src, 
								int srcConnPt, 
								MiPart dest, 
								int destConnPt)
		{
		if ((src == dest) && (srcConnPt == destConnPt))
			return(false);
		connectOp.setSource(src);
		connectOp.setSourceConnPt(srcConnPt);
		connectOp.setDestination(dest);
		connectOp.setDestinationConnPt(destConnPt);
		if (!dest.isValidConnectionDestination(connectOp))
			return(false);
		return(dest.dispatchActionRequest(Mi_CONNECT_ACTION, connectOp));
		}
	}


