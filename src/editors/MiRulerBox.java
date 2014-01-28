
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
import com.swfm.mica.util.DoubleVector; 
import com.swfm.mica.util.IntVector; 
import com.swfm.mica.util.Utility; 

/**----------------------------------------------------------------------------------------------
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiRulerBox extends MiWidget
	{
	private		MiPart		editorPanel;
	private		MiPart		page;
	private		MiPart		cornerBox;
	private		MiSize		pageSizeInUnits		= new MiSize();
	private		MiRuler		horizontalRuler;
	private		MiRuler		verticalRuler;
	

					/**------------------------------------------------------
	 				 * Constructs a new MiRulerManager.
					 *------------------------------------------------------*/
	public				MiRulerBox(MiPart box, MiPart editorPanel, MiPart page, MiSize pageSizeInUnits)
		{
		horizontalRuler = new MiRuler(Mi_HORIZONTAL);
		horizontalRuler.setPageSizeInUnits(pageSizeInUnits.width);

		verticalRuler = new MiRuler(Mi_VERTICAL);
		verticalRuler.setPageSizeInUnits(pageSizeInUnits.height);

		cornerBox = new MiVisibleContainer();
		cornerBox.setBorderLook(Mi_INDENTED_BORDER_LOOK);

		MiPolyConstraint constraintLayout = new MiPolyConstraint();
		//constraintLayout.appendConstraint(new MiRelativeLocationConstraint(
			//cornerBox, MiRelativeLocationConstraint.SAME_PREFERRED_WIDTH_AS, verticalRuler));
		cornerBox.setPreferredSize(new MiSize(
			verticalRuler.getPreferredSize(new MiSize()).getWidth(),
			horizontalRuler.getPreferredSize(new MiSize()).getHeight()));


/*
		constraintLayout.appendConstraint(new MiRelativeLocationConstraint(
			horizontalRuler, MiRelativeLocationConstraint.SAME_WIDTH_AS, box));
		constraintLayout.appendConstraint(new MiRelativeLocationConstraint(
			verticalRuler, MiRelativeLocationConstraint.SAME_PREFERRED_HEIGHT_AS, box));
*/

		MiColumnLayout columnLayout = new MiColumnLayout();
		columnLayout.setElementHSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		columnLayout.setUniqueElementIndex(1);

		MiRowLayout topRowLayout = new MiRowLayout();
		topRowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		topRowLayout.setUniqueElementIndex(1);
		topRowLayout.appendPart(cornerBox);
		topRowLayout.appendPart(horizontalRuler);

		appendPart(topRowLayout);

		MiRowLayout mainRowLayout = new MiRowLayout();
		mainRowLayout.setElementVSizing(Mi_EXPAND_TO_FILL);
		mainRowLayout.setUniqueElementSizing(Mi_EXPAND_TO_FILL);
		mainRowLayout.setUniqueElementIndex(1);
		mainRowLayout.appendPart(verticalRuler);
		mainRowLayout.appendPart(editorPanel);
		
		appendPart(mainRowLayout);

		MiPolyLayout polyLayout = new MiPolyLayout();
		polyLayout.appendLayout(constraintLayout);
		polyLayout.appendLayout(columnLayout);
		setLayout(polyLayout);

		horizontalRuler.setPage(page);
		verticalRuler.setPage(page);
		}
	public		void		setPage(MiPart page)
		{
		this.page = page;
		horizontalRuler.setPage(page);
		verticalRuler.setPage(page);
		}
	public		MiPart		getPage()
		{
		return(page);
		}
	public		void		setPageSizeInUnits(MiSize pageSizeInUnits)
		{
		this.pageSizeInUnits.copy(pageSizeInUnits);
		horizontalRuler.setPageSizeInUnits(pageSizeInUnits.width);
		verticalRuler.setPageSizeInUnits(pageSizeInUnits.height);
		}
	public		MiSize		getPageSizeInUnits()
		{
		return(new MiSize(pageSizeInUnits));
		}
	public		MiPart		getCornerBox()
		{
		return(cornerBox);
		}
	public		MiRuler		getHorizontalRuler()
		{
		return(horizontalRuler);
		}
	public		MiRuler		getVerticalRuler()
		{
		return(verticalRuler);
		}
					/**------------------------------------------------------
					 * Sets the property with the given name to the given value. 
					 * @param name		the name of an property
					 * @param value		the value of the property
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		horizontalRuler.setPropertyValue(name, value);

		if ((name.equalsIgnoreCase(MiRuler.Mi_TICK_JUSTIFICATION_NAME))
			|| (name.equalsIgnoreCase(MiRuler.Mi_LABEL_JUSTIFICATION_NAME)))
			{
			if (value.equalsIgnoreCase(Mi_TOP_JUSTIFIED_NAME))
				value = Mi_LEFT_JUSTIFIED_NAME;
			else if (value.equalsIgnoreCase(Mi_BOTTOM_JUSTIFIED_NAME))
				value = Mi_RIGHT_JUSTIFIED_NAME;
			}

		verticalRuler.setPropertyValue(name, value);

		if (name.equalsIgnoreCase(Mi_VISIBLE_NAME))
			cornerBox.setVisible(Utility.toBoolean(value));
		}
					/**------------------------------------------------------
					 * Gets the textual value of the property with the given
					 * name. If the value is null then 
					 * MiiTypes.Mi_NULL_VALUE_NAME is returned.
					 * @param name		the name of a property
					 * @return 		the string value of the property
					 * @overrides 		MiPart#getPropertyValue
					 *------------------------------------------------------*/
	public		String		getPropertyValue(String name)
		{
		return(horizontalRuler.getPropertyValue(name));
		}
					/**------------------------------------------------------
	 				 * Gets the descriptions of all of the properties. These
					 * can be used to see if an property is different from the
					 * default value or if a proposed value is valid or to get
					 * a list of all of the valid values of a property.
					 * @return 		the list of property descriptions
					 *------------------------------------------------------*/
	public		MiPropertyDescriptions	getPropertyDescriptions()
		{
		return(horizontalRuler.getPropertyDescriptions());
		}
	}
