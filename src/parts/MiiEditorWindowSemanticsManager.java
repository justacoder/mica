
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
public interface MiiEditorWindowSemanticsManager extends MiiCommandManager
	{
	boolean		requestCreateSemanticPart	(MiPart container, MiPart part);
	void		createSemanticPart		(MiPart container, MiPart part);
	boolean		requestCopySemanticPart		(MiPart destinationContainer, MiPart part);
	MiPart		copySemanticPart		(MiPart destinationContainer, MiPart part);
	boolean		requestDeleteSemanticPart	(MiPart container, MiPart part);
	void		deleteSemanticPart		(MiPart container, MiPart part);
	boolean		requestConnectSemanticParts	(MiPart source, MiPart destination);

	boolean		isReplaceableBy(MiPart origObj, MiPart replacementObj);
	void		replaceSemanticPart(MiPart origObj, MiPart replacementObj);
	void		replaceParentSemanticPart(MiPart entityGr, MiPart origParentGr, MiPart newParentGr);

	void		updateSemanticPartFromGraphic(MiPart graphic);
	void		updateGraphicFromSemanticPart(MiPart graphic);

	void		appendSemanticPart(MiPart containerGr, MiPart partGr);
	void		removeSemanticPart(MiPart containerGr, MiPart partGr);

	MiPart		autoDeIconifyImportedGraphic(MiPart obj);
	void		createSemanticForIconifiedGraphic(MiPart graphic, MiPart icon);
	void		deleteSemanticForIconifiedGraphic(MiPart graphic);

	void		displayPalettePartsForType(String type);
	boolean		requestAddToPalette(MiPart obj, Object containerTag);
	void		addToPalette(MiPart obj, Object containerTag);

	boolean		confirmLossOfChangesMade();
	}

