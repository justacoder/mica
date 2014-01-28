
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
public class MiAnnotationPointRule implements MiiManagedPointRule, MiiTypes
	{
//	private		int		hJustification	= Mi_CENTER_JUSTIFIED;
	private		int		vJustification	= Mi_CENTER_JUSTIFIED;
	private		MiSize		initialSize;
	private		String		prompt;
	private		MiAttributes	attributes	= MiPart.getDefaultAttributes();
	private		MiAttributes	promptAttributes= MiPart.getDefaultAttributes();



	public				MiAnnotationPointRule()
		{
		}
	public		void		setContentAttributes(MiAttributes atts)
		{
		attributes = atts;
		}
	public		MiAttributes	getContentAttributes()
		{
		return(attributes);
		}
	public		void		setPrompt(String prompt)
		{
		this.prompt = prompt;
		}
	public		String		getPrompt()
		{
		return(prompt);
		}
	public		void		setPromptAttributes(MiAttributes atts)
		{
		promptAttributes = atts;
		}
	public		MiAttributes	getPromptAttributes()
		{
		return(promptAttributes);
		}
/*** in atts
	public		void		setHJustification(int justification)
		{
		hJustification = justification;
		}
	public		int		getHJustification()
		{
		return(hJustification);
		}
****/
	public		void		setVJustification(int justification)
		{
		vJustification = justification;
		}
	public		int		getVJustification()
		{
		return(vJustification);
		}
	public		MiiManagedPointRule	copy()
		{
		MiAnnotationPointRule rule = new MiAnnotationPointRule();
//		rule.hJustification = hJustification;
		rule.vJustification = vJustification;
		rule.initialSize = initialSize;
		rule.attributes = attributes;
		rule.prompt = prompt;
		rule.promptAttributes = promptAttributes;
		return(rule);
		}
	public		String		toString()
		{
		return(MiDebug.getMicaClassName(this) + ": prompt=" + prompt);
		}
	}

