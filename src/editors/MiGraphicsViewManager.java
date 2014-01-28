
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
import com.swfm.mica.util.CaselessKeyHashtable;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.Strings;
import java.util.Date;

/**----------------------------------------------------------------------------------------------
 * This class implements the MiiViewManager interface
 * and supports the loading and saving of graphics editors to and from
 * MiiModelDocuments. The target must be an instance of MiEditor.
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphicsViewManager 
			extends MiGraphicsBaseViewManager 
			implements MiiViewManager, MiiTypes, MiiCommandNames,
			MiiActionHandler, MiiActionTypes
	{
					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsViewManager.
					 *------------------------------------------------------*/
	public				MiGraphicsViewManager()
		{
		MiPropertyDescriptions	propertyDescriptions 
			= new MiPropertyDescriptions(getClass().getName());

		MiPropertyDescription desc = new MiPropertyDescription(Mi_LAYOUT_NAME, 
			new Strings(MiiNames.layoutNames), Mi_NONE_NAME);
		desc.setValidValuesAreSuggestionsOnly(true);
		propertyDescriptions.addElement(desc);
		propertyDescriptions.addElement(new MiPropertyDescription(Mi_ALLEY_H_SPACING_NAME, 
			Mi_POSITIVE_DOUBLE_TYPE, "2"));
		propertyDescriptions.addElement(new MiPropertyDescription(Mi_ALLEY_V_SPACING_NAME, 
			Mi_POSITIVE_DOUBLE_TYPE, "2"));
		propertyDescriptions.addElement(new MiPropertyDescription(Mi_MARGINS_NAME, 
			Mi_POSITIVE_DOUBLE_TYPE, "0"));
		propertyDescriptions.addElement(new MiPropertyDescription(Mi_ORIENTATION_NAME, 
			new Strings(Mi_HORIZONTAL_NAME, Mi_VERTICAL_NAME), Mi_HORIZONTAL_NAME));
		propertyDescriptions.addElement(new MiPropertyDescription(Mi_HORIZONTAL_JUSTIFICATION_NAME, 
			new Strings(horizontalJustificationNames), Mi_CENTER_JUSTIFIED_NAME));
		propertyDescriptions.addElement(new MiPropertyDescription(Mi_VERTICAL_JUSTIFICATION_NAME, 
			new Strings(verticalJustificationNames), Mi_CENTER_JUSTIFIED_NAME));

		setPropertyDescriptions(propertyDescriptions);
		setPropertiesToDefaultValues();
		}
					/**------------------------------------------------------
		 			 * Loads the target container with graphics as specified
					 * in the given document.
					 * @param document	the document
					 * @implements		MiiViewManager#setModel
					 * @see 		MiGraphicsBaseViewManager#setView
					 *------------------------------------------------------*/
	public		void		setModel(MiiModelEntity document)
		{
		subPalettes = null;
		// removes all layers too... editor.removeAllParts();
		semanticsTable.removeAllElements();
		attributesTable.removeAllElements();

		// Should editors/targets even have layouts? not compatible with layers!
		MiiLayout layout = getLayoutFromName(getPropertyValue(Mi_LAYOUT_NAME), false);
		if (layout != null)
			{
			if (layout instanceof MiManipulatableLayout)
				{
				MiManipulatableLayout mLayout = (MiManipulatableLayout )layout;
				double alleyHSpacing = Utility.toDouble(getPropertyValue(Mi_ALLEY_H_SPACING_NAME));
				mLayout.setAlleyHSpacing(alleyHSpacing);
				double alleyVSpacing = Utility.toDouble(getPropertyValue(Mi_ALLEY_V_SPACING_NAME));
				mLayout.setAlleyVSpacing(alleyVSpacing);
				double margins = Utility.toDouble(getPropertyValue(Mi_MARGINS_NAME));
				mLayout.setMargins(new MiMargins(margins));
				String orientationStr = getPropertyValue(Mi_ORIENTATION_NAME);
				if (Mi_HORIZONTAL_NAME.equalsIgnoreCase(orientationStr))
					mLayout.setOrientation(Mi_HORIZONTAL);
				if (Mi_VERTICAL_NAME.equalsIgnoreCase(orientationStr))
					mLayout.setOrientation(Mi_VERTICAL);
				int hJustification = MiSystem.getValueOfAttributeValueName(
						getPropertyValue(Mi_HORIZONTAL_JUSTIFICATION_NAME));
				mLayout.setElementHJustification(hJustification);
				int vJustification = MiSystem.getValueOfAttributeValueName(
						getPropertyValue(Mi_VERTICAL_JUSTIFICATION_NAME));
				mLayout.setElementVJustification(vJustification);
				}
			target.setLayout(layout);
			}

		this.document = document;
		parts = new MiParts();

		createGraphics(document);


		MiPart targetContainer = editor;
		if (editor.hasLayers())
			targetContainer = editor.getCurrentLayer();
			
		for (int i = 0; i < parts.size(); ++i)
			{
			if (parts.elementAt(i).getNumberOfContainers() == 0)
				targetContainer.appendPart(parts.elementAt(i));
			}
		}
	}

