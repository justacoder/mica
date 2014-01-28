
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

package com.swfm.mica.util; 
import java.util.Vector; 

/**
 * This tree has unnamed nodes. The tree node data is immutible.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class Tree
	{
	private		Vector		subTrees;
	private		Tree		parent;
	private		Object		data;

	public				Tree(Object data)
		{
		this.data = data;
		}

	public		void		addElement(Tree kid)
		{
		if (subTrees == null)
			subTrees = new Vector();

		subTrees.addElement(kid);
		kid.setParent(this);
		}

	public		Tree		elementAt(int index)
		{
		return((Tree )subTrees.elementAt(index));
		}

	public		Tree		elementAt(Object data)
		{
		if (this.data.equals(data))
			return(this);
		if (subTrees == null)
			return(null);
		for (int i = 0; i < subTrees.size(); ++i)
			{
			Tree tree = ((Tree )subTrees.elementAt(i)).elementAt(data);
			if (tree != null)
				return(tree);
			}
		return(null);
		}

	public		int		size()
		{
		if (subTrees == null)
			return(0);
		return(subTrees.size());
		}

	public		void		removeAllElements()
		{
		if (subTrees != null)
			{
			subTrees.removeAllElements();
			}
		}
	public		void		removeElementAt(int index)
		{
		subTrees.removeElementAt(index);
		}
	public		void		removeElement(Tree node)
		{
		subTrees.removeElement(node);
		}
	public		int		sizeOfTree()
		{
		if (subTrees == null)
			return(0);

		int num = subTrees.size();
		for (int i = 0; i < subTrees.size(); ++i)
			num += ((Tree )subTrees.elementAt(i)).sizeOfTree();

		return(num);
		}

	public		Object		getData()
		{
		return(data);
		}

	public		Tree		getParent()
		{
		return(parent);
		}
	private		void		setParent(Tree parent)
		{
		this.parent = parent;
		}
	public		String		toString()
		{
		return(super.toString() + (data != null ? data.toString() : "<null>"));
		}
	public		String		dump()
		{
		return(dump(""));
		}
	private		String		dump(String indent)
		{
		String str = toString() + "\n";
		indent += "  ";
		str += indent + "{\n";
		for (int i = 0; i < size(); ++i)
			{
			str += indent + elementAt(i).dump(indent);
			}
		str += indent + "}\n";
		return(str);
		}
	}


