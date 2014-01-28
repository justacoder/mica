
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
import java.util.Stack; 

/**----------------------------------------------------------------------------------------------
 * This class implements a MiiKeyFocusTraversalGroup by doing runtime search of the
 * contents of the targetContainer looking for parts that will accept
 * keyboard and enter key focus.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiLazyKeyFocusTraversalGroup implements MiiKeyFocusTraversalGroup
	{
	private		MiPart		target;


					/**------------------------------------------------------
	 		 		 * Construct a new MiLazyKeyFocusTraversalGroup.
					 * @param container	the container whose parts are
					 *			valid contenders to receive
					 *			keyboard and enter key focus
			 		 *------------------------------------------------------*/
	public				MiLazyKeyFocusTraversalGroup(MiPart container)
		{
		target = container;
		}

					/**------------------------------------------------------
	 		 		 * Gets the next part to receive keyboard or enter key
			 		 * focus with respect to the given part. If the given
			 		 * part is null then the default keyboard focus part is
			 		 * returned.
			 		 * @param current	one part in the tab group
			 		 * @return 		the next part in the tab group
					 *			(null if no next part)
					 * @implements 		MiiKeyFocusTraversalGroup
					 *------------------------------------------------------*/
	public		MiPart		getNext(MiPart current)
		{
		if (current == null)
			{
			return(searchDownTheTree(target, true, false));
			}

		MiPart next = searchDownTheTree(current, true, false);
		if (next == null)
			{
			next = searchUpTheTree(current, true);
			}
		return(next);
		}
					/**------------------------------------------------------
	 		 		 * Gets the previous part to receive keyboard or enter key
			 		 * focus with respect to the given part. If the given part
			 		 * is null then the key preceeding the default keyboard 
			 		 * focus part is returned.
			 		 * @param current	one part in the tab group
			 		 * @return 		the previous part in the tab group
					 *			(null if no previous part)
					 * @implements 		MiiKeyFocusTraversalGroup
			 		 *------------------------------------------------------*/
	public		MiPart		getPrevious(MiPart current)
		{
		if (current == null)
			{
			return(searchDownTheTree(target, false, false));
			}

		MiPart prev = searchUpTheTree(current, false);
		return(prev);
		}


	//***************************************************************************************
	// Protected methods
	//***************************************************************************************


					/**------------------------------------------------------
	 		 		 * Searches for the next or previous valid part that can
					 * receive keyboard focus. Looks at siblings, their parts
					 * and then parent's siblings and their parts...
					 * then looks at 
					 * @param current	current part to start from
					 * @param next		true if we are to search for next
					 *			valid part, otherwise we are to
					 *			search for previous valid part.
					 * @return 		the valid candidate or null.
			 		 *------------------------------------------------------*/
	protected	MiPart		searchUpTheTree(MiPart current, boolean next)
		{
		// If current part has no parent or is not visible then nothing is found.
		if ((current.getNumberOfContainers() == 0) || (!current.isVisible()))
			return(null);

		MiPart part = null;
		MiiKeyFocusTraversalGroup tabGroup = getLocalKeyFocusTraversalGroup(current);
		if (tabGroup != this)
			{
			part = next ? tabGroup.getNext(current) : tabGroup.getPrevious(current);

			if (part != null)
				return(part);
			}

		MiPart parent = current.getContainer(0);

		// If parent is at the top level or is not visible then nothing is found.
		if ((parent == target) || (!parent.isVisible()))
			return(null);

		int index = parent.getIndexOfPart(current);
		// If current part is no longer is parent then nothing is found.
		if (index == -1)
			return(null);

		int inc = 1;
		if (!next)
			inc = -1;

		for (int i = index + inc; (i >= 0) && (i < parent.getNumberOfParts()); i += inc)
			{
			MiPart obj = parent.getPart(i);
			// Look at previous sibling to see if it is a valid candidate...
			if ((obj.isVisible()) && (obj.isSensitive()) && (obj.isAcceptingKeyboardFocus()))
				{
				if (next)
					return(obj);
				// Going backwards requires iterating thru the candidate obj's parts
				// before we return the obj itself.
				MiPart tmp = searchDownTheTree(obj, next, false);
				if (tmp != null)
					return(tmp);
				return(obj);
				}
			// It is not, look at previous sibling's parts to see if one of 
			// them are a valid candidate...
			if ((obj = searchDownTheTree(obj, next, false)) != null)
				{
				return(obj);
				}
			}
		// Going backwards requires iterating thru the candidate obj's parts
		// before we return the obj itself. We have iterated thru the parts
		// now look at the container... (and if going forward, the container
		// has already had key focus on the way here).
		if ((!next) 
			&& (parent.isVisible()) 
			&& (parent.isSensitive()) 
			&& (parent.isAcceptingKeyboardFocus()))
			{
			return(parent);
			}
		// Nothing found at the sibling level, go up another level...
		return(searchUpTheTree(parent, next));
		}
					/**------------------------------------------------------
	 		 		 * Searches for the next or previous valid part that can
					 * receive keyboard focus. Looks at parts, their parts
					 * and their parts parts, ... 
					 * @param parent	current parent to start from
					 * @param next		true if we are to search for next
					 *			valid part, otherwise we are to
					 *			search for previous valid part.
					 * @return 		the valid candidate or null.
			 		 *------------------------------------------------------*/
	protected	MiPart		searchDownTheTree(MiPart parent, boolean next, boolean wentDownALevel)
		{
		// If parent, whose parts we are going to look at has no parts or is
		// not visible then return nothing.
		if ((parent.getNumberOfParts() == 0) || (!parent.isVisible()))
			return(null);

		MiiKeyFocusTraversalGroup parentTabGroup = parent.getKeyFocusTraversalGroup();
		if ((parentTabGroup != null) && (parentTabGroup != this))
			{
			// First time entering this tabGroup we need to pass in null
			if (wentDownALevel)
				parent = null;
			return(next ? parentTabGroup.getNext(parent) : parentTabGroup.getPrevious(parent));
			}

		int inc = 1;
		int start = 0;
		if (!next)
			{
			inc = -1;
			start = parent.getNumberOfParts() - 1;
			}

		for (int i = start; (i >= 0) && (i < parent.getNumberOfParts()); i += inc)
			{
			MiPart obj = parent.getPart(i);
			// Look at each part...
			if ((obj.isVisible()) && (obj.isSensitive()) && (obj.isAcceptingKeyboardFocus()))
				{
				return(obj);
				}
			// .. if not valid then look at the parts parts ...
			if ((obj = searchDownTheTree(obj, next, true)) != null)
				{
				return(obj);
				}
			}
		// ... nothing found in this branch of the tree...
		return(null);
		}
					/**------------------------------------------------------
	 		 		 * Searches for the MiiKeyFocusTraversalGroup that is
					 * assigned to a container of the given part. Presumably
					 * this will be the group that assigned focus to the
					 * part.
					 * @param current	current part to start from
					 * @return 		the containing traversal group
			 		 *------------------------------------------------------*/
	protected	MiiKeyFocusTraversalGroup getLocalKeyFocusTraversalGroup(MiPart current)
		{
		MiPart parent = current.getContainer(0);
		if (parent == null)
			return(null);

		if (parent.getKeyFocusTraversalGroup() != null)
			return(parent.getKeyFocusTraversalGroup());

		return(getLocalKeyFocusTraversalGroup(parent));
		}
	}

