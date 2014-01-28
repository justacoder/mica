
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

import java.util.Vector; 

/**
 * This class implements a MiiKeyFocusTraversalGroup as a list of parts that are
 * to receive keyboard/enter-key focus. Use the methods of the super
 * class MiParts to add and remove parts from this tab group.
*
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiKeyFocusTraversalGroupList extends MiParts implements MiiKeyFocusTraversalGroup
	{
	public				MiKeyFocusTraversalGroupList()
		{
		}
	
					/**------------------------------------------------------
	 		 		 * Gets the next part to receive keyboard or enter key
			 		 * focus with respect to the given part. If the given
			 		 * part is null then the first focus part is returned.
			 		 * @param current	one part in the tab group
			 		 * @return 		the next part in the tab group
					 *			(null if no next part)
					 * @implements 		MiiKeyFocusTraversalGroup
					 *------------------------------------------------------*/
	public		MiPart		getNext(MiPart current)
		{
		int index = -1;
		if (current != null)
			index = indexOf(current);

		if (index == -1)
			return(elementAt(0));

		for (int i = index + 1; i != index; ++i)
			{
			if (i >= size())
				return(null);

			MiPart obj = elementAt(i);
			if ((obj.isVisible()) && (obj.isSensitive()) && (obj.isAcceptingKeyboardFocus()))
				{
				return(obj);
				}
			}
		return(null);
		}

					/**------------------------------------------------------
	 		 		 * Gets the previous part to receive keyboard or enter key
			 		 * focus with respect to the given part. If the given part
			 		 * is null then the last focus part is returned.
			 		 * @param current	one part in the tab group
			 		 * @return 		the previous part in the tab group
					 *			(null if no previous part)
					 * @implements 		MiiKeyFocusTraversalGroup
			 		 *------------------------------------------------------*/
	public		MiPart		getPrevious(MiPart current)
		{
		int index = -1;
		if (current != null)
			index = indexOf(current);

		if (index == -1)
			return(lastElement());

		for (int i = index - 1; i != index; --i)
			{
			if (i < 0)
				return(null);

			MiPart obj = elementAt(i);
			if ((obj.isVisible()) && (obj.isSensitive()) && (obj.isAcceptingKeyboardFocus()))
				{
				return(obj);
				}
			}
		return(null);
		}
	}

