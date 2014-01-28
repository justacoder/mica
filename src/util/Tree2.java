
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
 * This tree has named nodes, suitable for managing common file directories.
 * The tree node data is NOT immutible.
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class Tree2
	{
	private		Vector		subTrees;
	private		Tree2		parent;
	private		String		name;
	private		Object		data;

	public				Tree2(Object data)
		{
		this.data = data;
		}

	public				Tree2(Object data, String name)
		{
		this.data = data;
		this.name = name;
		}

	public		void		setName(String name)
		{
		this.name = name;
		}
	public		String		getName()
		{
		return(name);
		}

	public		void		add(Tree2 kid)
		{
		if (subTrees == null)
			subTrees = new Vector();

		subTrees.addElement(kid);
		kid.setParent(this);
		}
	public		void		addElement(Tree2 kid)
		{
		if (subTrees == null)
			subTrees = new Vector();

		subTrees.addElement(kid);
		kid.setParent(this);
		}

	public		Tree2		get(int index)
		{
		return((Tree2 )subTrees.elementAt(index));
		}

	public		Tree2		elementAt(int index)
		{
		return((Tree2 )subTrees.elementAt(index));
		}

	public		Tree2		elementAt(Object data)
		{
		if (this.data.equals(data))
			return(this);
		if (subTrees == null)
			return(null);
		for (int i = 0; i < subTrees.size(); ++i)
			{
			Tree2 tree = ((Tree2 )subTrees.elementAt(i)).elementAt(data);
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
	public		void		removeElement(Tree2 node)
		{
		subTrees.removeElement(node);
		}
	public		int		sizeOfTree()
		{
		if (subTrees == null)
			return(0);

		int num = subTrees.size();
		for (int i = 0; i < subTrees.size(); ++i)
			num += ((Tree2 )subTrees.elementAt(i)).sizeOfTree();

		return(num);
		}

	public		void		setData(Object data)
		{
		this.data = data;
		}
	public		Object		getData()
		{
		return(data);
		}

	public		Tree2		getParent()
		{
		return(parent);
		}
	private		void		setParent(Tree2 parent)
		{
		this.parent = parent;
		}
	public		String		toString()
		{
		return(super.toString() + (data != null ? data.toString() : "<null>") + "[name=" + name + "]");
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


