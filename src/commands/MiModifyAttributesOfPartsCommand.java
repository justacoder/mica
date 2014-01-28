
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
import java.util.ArrayList; 

/**
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 */
public class MiModifyAttributesOfPartsCommand extends MiCommandHandler implements MiiTransaction, MiiCommandNames, MiiDisplayNames, MiiPropertyTypes
	{
	private		ArrayList	currentAttributes	= new ArrayList();
	private		ArrayList	newAttributesList;
	private		MiAttributes	newAttributes;
	private		MiParts		parts;
	private		String		attName;
	private		Object		attValue;
	private		int		attValueType;


	public				MiModifyAttributesOfPartsCommand()
		{
		this.parts = new MiParts();
		}

	protected			MiModifyAttributesOfPartsCommand(MiEditor editor,
						ArrayList newAttributesList, ArrayList currentAttributes,
						MiParts parts)
		{
		setTargetOfCommand(editor);
		this.newAttributesList = new ArrayList(newAttributesList);
		this.currentAttributes = new ArrayList(currentAttributes);
		this.parts = MiUtility.getActualShapesToApplyAttributeChangeTo(parts);
//MiDebug.println(this + "parts 2 that will be modified: " + this.parts);
		}
	protected			MiModifyAttributesOfPartsCommand(MiEditor editor,
						MiAttributes newAttributes, ArrayList currentAttributes,
						MiParts parts)
		{
		setTargetOfCommand(editor);
		this.newAttributes = newAttributes;
		this.currentAttributes = new ArrayList(currentAttributes);
		this.parts = MiUtility.getActualShapesToApplyAttributeChangeTo(parts);
//MiDebug.println(this + "parts 2 that will be modified: " + this.parts);
		}

	protected			MiModifyAttributesOfPartsCommand(MiEditor editor,
						String attName, Object attValue, int attValueType,
						ArrayList currentAttributes, MiParts parts)
		{
		setTargetOfCommand(editor);
		this.attName = attName;
		this.attValue = attValue;
		this.attValueType = attValueType;
		this.currentAttributes = new ArrayList(currentAttributes);
		this.parts = MiUtility.getActualShapesToApplyAttributeChangeTo(parts);
//MiDebug.println(this + "parts that will be modified: " + this.parts);
		}

	public		MiEditor	getEditor()
		{
		return((MiEditor )getTargetOfCommand());
		}
	public		void		setNewAttributes(MiAttributes atts)
		{
		newAttributes = atts;
		}
	public		MiAttributes	getNewAttributes()
		{
		return(newAttributes);
		}
	public		void		modifyAttributes(MiEditor editor,
						String attName, Object attValue, MiParts parts)
		{
		doModifyAttributes(editor, attName, attValue, Mi_OBJECT_TYPE, parts);
		}
	public		void		modifyAttributes(MiEditor editor,
						String attName, String attValue, MiParts parts)
		{
		doModifyAttributes(editor, attName, attValue, Mi_STRING_TYPE, parts);
		}
	public		void		modifyAttributes(MiEditor editor,
						String attName, int attValue, MiParts parts)
		{
		doModifyAttributes(editor, attName, new Integer(attValue), Mi_INTEGER_TYPE, parts);
		}
	public		void		modifyAttributes(MiEditor editor,
						String attName, double attValue, MiParts parts)
		{
		doModifyAttributes(editor, attName, new Double(attValue), Mi_DOUBLE_TYPE, parts);
		}
	public		void		modifyAttributes(MiEditor editor,
						String attName, boolean attValue, MiParts parts)
		{
		doModifyAttributes(editor, attName, new Boolean(attValue), Mi_BOOLEAN_TYPE, parts);
		}



	protected	void		doModifyAttributes(MiEditor editor, 
						String attName, Object attValue, int attValueType,
						MiParts parts)
		{
		currentAttributes.clear();
		parts = MiUtility.getActualShapesToApplyAttributeChangeTo(parts);
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart obj = parts.elementAt(i);
			currentAttributes.add(obj.getAttributes());
			}
		doit(editor, attName, attValue, attValueType, currentAttributes, parts, true);
		MiSystem.getTransactionManager().appendTransaction(
			new MiModifyAttributesOfPartsCommand(
				editor, attName, attValue, attValueType, currentAttributes, parts));
		}
	public		void		processCommand(String arg)
		{
		if ((arg != null)
			&& (arg.equalsIgnoreCase(Mi_MODIFY_ATTRIBUTES_COMMAND_NAME)))
			{
			MiEditor editor = getEditor();
			parts.removeAllElements();
			currentAttributes.clear();
			editor.getSelectedParts(parts);
			parts = MiUtility.getActualShapesToApplyAttributeChangeTo(parts);
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart obj = parts.elementAt(i);
				currentAttributes.add(obj.getAttributes());
				}
			}
		else
			{
			throw new IllegalArgumentException(this + ": Unknown argument to processCommand: " + arg);
			}
		processCommand(getEditor(), newAttributes, currentAttributes, parts);
		}
	public		void		processCommand(MiEditor editor, 
						MiAttributes newAttributes, ArrayList currentAttributes,
						MiParts parts)
		{
		parts = MiUtility.getActualShapesToApplyAttributeChangeTo(parts);
		doit(editor, newAttributes, currentAttributes, parts, true);
		MiSystem.getTransactionManager().appendTransaction(
			new MiModifyAttributesOfPartsCommand(editor, newAttributes, currentAttributes, parts));
		}
	public		void		processCommand(MiEditor editor, 
						String attName, Object attValue, int attValueType,
						ArrayList currentAttributes,
						MiParts parts)
		{
		parts = MiUtility.getActualShapesToApplyAttributeChangeTo(parts);
		doit(editor, attName, attValue, attValueType, currentAttributes, parts, true);
		MiSystem.getTransactionManager().appendTransaction(
			new MiModifyAttributesOfPartsCommand(editor, attName, attValue, attValueType,
				 currentAttributes, parts));
		}
	protected	void		doit(MiEditor editor, 
						ArrayList newAttributes, ArrayList currentAttributes,
						MiParts parts, boolean toDoIt)
		{
		if (toDoIt)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart obj = parts.elementAt(i);
				obj.setAttributes((MiAttributes )newAttributes.get(i));
				}
			}
		else
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart obj = parts.elementAt(i);
				obj.setAttributes((MiAttributes )currentAttributes.get(i));
				}
			}
		}
	protected	void		doit(MiEditor editor, 
						MiAttributes newAttributes, ArrayList currentAttributes,
						MiParts parts, boolean toDoIt)
		{
		if (toDoIt)
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart obj = parts.elementAt(i);
				obj.setAttributes(newAttributes);
				}
			}
		else
			{
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart obj = parts.elementAt(i);
				obj.setAttributes((MiAttributes )currentAttributes.get(i));
				}
			}
		}
	protected	void		doit(MiEditor editor, 
						String attName, Object attValue, int attValueType,
						ArrayList currentAttributes,
						MiParts parts, boolean toDoIt)
		{
		if (toDoIt)
			{
//MiDebug.println(this + " do parts: " + parts);
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart obj = parts.elementAt(i);
//MiDebug.println(this + ".apply to part: " + obj);
				switch (attValueType)
					{
					case Mi_OBJECT_TYPE :
						obj.setAttributeValue(attName, attValue);
						break;
					case Mi_INTEGER_TYPE :
						obj.setAttributeValue(attName, ((Integer )attValue).intValue());
						break;
					case Mi_DOUBLE_TYPE :
						obj.setAttributeValue(attName, ((Double )attValue).doubleValue());
						break;
					case Mi_BOOLEAN_TYPE :
						obj.setAttributeValue(attName, ((Boolean )attValue).booleanValue());
						break;
					case Mi_STRING_TYPE :
						obj.setAttributeValue(attName, (String )attValue);
						break;
					default:
						throw new IllegalArgumentException(this 
							+ ": Unknown type of attribute value: " + attValueType);
					}
				}
			}
		else
			{
//MiDebug.println(this + " undo parts: " + parts);
			for (int i = 0; i < parts.size(); ++i)
				{
				MiPart obj = parts.elementAt(i);
				obj.setAttributes((MiAttributes )currentAttributes.get(i));
				}
			}
		}


					/**------------------------------------------------------
					 * Gets the name of this transaction. This name is often 
					 * displayed, for example, in the menubar's edit pulldown
					 * menu.
					 * @return		the name of this transaction.
					 * @implements		MiiTransaction#getName
					 *------------------------------------------------------*/
	public		String		getName()
		{
		return(Mi_MODIFY_ATTRIBUTES_DISPLAY_NAME);
		}
					/**------------------------------------------------------
					 * Gets the command perfromed by this transaction. This name
					 * is often found in the MiiCommandNames file.
					 * @return		the command of this transaction.
					 * @implements		MiiTransaction#getCommand
					 *------------------------------------------------------*/
	public		String		getCommand()
		{
		return(Mi_MODIFY_ATTRIBUTES_COMMAND_NAME);
		}
					/**------------------------------------------------------
					 * Redoes this transaction. This is only valid after an undo.
					 * This redoes the changes encapsulated by this transaction
					 * that were undone by the undo() method.
					 * @implements		MiiTransaction#redo
					 *------------------------------------------------------*/
	public		void		redo()
		{
		if ((newAttributes == null) && (newAttributesList != null))
			doit(getEditor(), newAttributesList, currentAttributes, parts, true);
		else if (newAttributes == null)
			doit(getEditor(), attName, attValue, attValueType, currentAttributes, parts, true);
		else
			doit(getEditor(), newAttributes, currentAttributes, parts, true);
		} 
					/**------------------------------------------------------
					 * Undoes this transaction. This undoes any changes that 
					 * were made by the changes encapsulated by this transaction.
					 * @implements		MiiTransaction#undo
					 *------------------------------------------------------*/
	public		void		undo()
		{
//MiDebug.println(this + " undo parts 111: " + parts);
		if ((newAttributes == null) && (newAttributesList != null))
			doit(getEditor(), newAttributesList, currentAttributes, parts, false);
		else if (newAttributes == null)
			doit(getEditor(), attName, attValue, attValueType, currentAttributes, parts, false);
		else
			doit(getEditor(), newAttributes, currentAttributes, parts, false);
		} 
					/**------------------------------------------------------
					 * Repeats this transaction. This re-applies the changes 
					 * encapsulated by this transaction. For example, a 
					 * translation of a shape can be repeated in order to move 
					 * it further.
					 * @implements		MiiTransaction#repeat
					 *------------------------------------------------------*/
	public		void		repeat()
		{
		} 
					/**------------------------------------------------------
					 * Gets whether this transaction is undoable.
					 * @returns		true if undoable.
					 * @implements		MiiTransaction#isUndoable
					 *------------------------------------------------------*/
	public		boolean		isUndoable()
		{
		return(true);
		}
					/**------------------------------------------------------
					 * Gets whether this transaction is repeatable. If repeatable
					 * then calling this transaction's repeat() method is permitted.
					 * @returns		true if repeatable.
					 * @implements		MiiTransaction#isRepeatable
					 *------------------------------------------------------*/
	public		boolean		isRepeatable()
		{
		return(false);
		}
					/**------------------------------------------------------
					 * Gets the targets of this transaction.
					 * @returns		the targets affected by this transaction
					 * @implements		MiiTransaction#getTargets
					 *------------------------------------------------------*/
	public		MiParts		getTargets()
		{
		return(new MiParts(parts));
		}
					/**------------------------------------------------------
					 * Gets the parts used by this transaction.
					 * @returns		the targets used by this transaction
					 * @implements		MiiTransaction#getSources
					 *------------------------------------------------------*/
	public		MiParts		getSources()
		{
		return(null);
		}
	}

