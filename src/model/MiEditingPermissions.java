
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
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiEditingPermissions
	{
	private		boolean		createable		= true;
	private		boolean		deleteable		= true;
	private		boolean		copyable		= true;
	private		boolean		printable		= true;
	private		boolean		writeable		= true;
	private		boolean		readable		= true;
	private		boolean		editable		= true;
	private		boolean		customizable		= true;
	private		boolean		browseable		= true;

/*
	private		boolean		contentsCreateable	= true;
	private		boolean		contentsDeleteable	= true;
	private		boolean		contentsCopyable	= true;
	private		boolean		contentsPrintable	= true;
	private		boolean		contentsWritable	= true;
	private		boolean		contentsReadable	= true;
*/

	public				MiEditingPermissions()
		{
		}
	public				MiEditingPermissions(MiEditingPermissions permissions)
		{
		copy(permissions);
		}

	public		void		copy(MiEditingPermissions permissions)
		{
		this.createable		= createable;
		this.deleteable		= deleteable;
		this.copyable		= copyable;
		this.printable		= printable;
		this.writeable		= writeable;
		this.readable		= readable;
		this.editable		= editable;
		this.customizable	= customizable;
		this.browseable		= browseable;

/*
		this.deleteable	= deleteable;
		this.copyable	= copyable;
		this.printable	= printable;
		this.modifyable	= modifyable;
		this.createable		= createable;
		this.deleteable		= deleteable;
		this.copyable		= copyable;
		this.printable		= printable;
		this.writeable		= writeable;
		this.readable		= readable;
		this.contentsCreateable	= contentsCreateable;
		this.contentsDeleteable	= contentsDeleteable;
		this.contentsCopyable	= contentsCopyable;
		this.contentsPrintable	= contentsPrintable;
		this.contentsWritable	= contentsWritable;
		this.contentsReadable	= contentsReadable;
*/
		}
	public		void		setDeleteable(boolean flag)
		{
		deleteable = flag;
		}
	public		boolean		isDeleteable()
		{
		return(deleteable);
		}
	public		void		setCopyable(boolean flag)
		{
		copyable = flag;
		}
	public		boolean		isCopyable()
		{
		return(copyable);
		}
	public		void		setPrintable(boolean flag)
		{
		printable = flag;
		}
	public		boolean		isPrintable()
		{
		return(printable);
		}
	public		void		setWritable(boolean flag)
		{
		writeable = flag;
		}
	public		boolean		isWritable()
		{
		return(writeable);
		}
	public		void		setCustomizable(boolean flag)
		{
		customizable = flag;
		}
	public		boolean		isCustomizable()
		{
		return(customizable);
		}
	public		void		setEditable(boolean flag)
		{
		editable = flag;
		}
	public		boolean		isEditable()
		{
		return(editable);
		}
	public		void		setBrowseable(boolean flag)
		{
		browseable = flag;
		}
	public		boolean		isBrowseable()
		{
		return(browseable);
		}
	}


