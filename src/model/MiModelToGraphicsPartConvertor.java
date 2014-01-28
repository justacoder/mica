
/*
 ***************************************************************************
 *           MiCAD - The CAD (Computer Aided Design) Framework             *
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
import com.swfm.mica.util.FastVector;
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.TypedVector;
import java.awt.Color;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;
//import com.swfm.util.Monitor;


/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiModelToGraphicsPartConvertor extends MiModelEntity implements MiiModelToGraphicsPartConvertor, MiiNames, MiiTypes
	{
//	private static	Monitor		monitor = Monitor.getMonitor("MiModelToGraphicsPartConvertor".getClass().getName());
	private		MiPart		connectionPointLook;
	private		int		customManagedPointNumber;

	private static	HashMap		attributesToIgnoreDuringFirstPass = new HashMap();

	private	 static	String[]	attributesToIgnoreDuringFirstPassTable =
		{
		Mi_X_TRANSLATION_NAME,
		Mi_Y_TRANSLATION_NAME,
		Mi_TRANSLATION_POINT_PART_NAME_AND_POINT_NUMBER,
		Mi_TRANSLATE_PT_TO_X_NAME,
		Mi_TRANSLATE_PT_TO_Y_NAME,
		Mi_ROTATION_POINT_PART_NAME_AND_POINT_NUMBER,
		Mi_ROTATE_PT_TO_NAME,
		Mi_ROTATE_NAME,
		Mi_ROTATION_NAME,
		Mi_TYPE_NAME,
		Mi_CONTAINER_NAME,
		Mi_VALUE_NAME,
		};

	static	{
		for (int i = 0; i < attributesToIgnoreDuringFirstPassTable.length; ++i)
			{
			attributesToIgnoreDuringFirstPass.put(
				attributesToIgnoreDuringFirstPassTable[i], attributesToIgnoreDuringFirstPassTable[i]);
			}
		}

	public				MiModelToGraphicsPartConvertor()
		{
		this(null);
		}

	public				MiModelToGraphicsPartConvertor(MiPart connectionPointLook)
		{
		this.connectionPointLook = connectionPointLook;
		}

	public		MiPart		getConnectionPointLook()
		{
		return(connectionPointLook);
		}


	public		MiPart		convert(MiiModelEntity definition)
		{
		MiContainer graphics = new MiContainer();

		// An empty definition... 'primitive' graphics element?
		if (definition.getNumberOfModelEntities() == 0)
			{
			MiPart prim = createPrim(graphics, graphics, definition);
			if (prim != null)
				{
				return(prim);
				}
			// ... a 'space' in a custom font?
			return(graphics);
			}

		customManagedPointNumber = Mi_MIN_CUSTOM_LOCATION + 1;

		customManagedPointNumber = applyAttributesToGraphic(
			graphics, graphics, definition, customManagedPointNumber);


		//customManagedPointNumber = applyAttributesToGraphic(
			//graphics, graphics, definition, customManagedPointNumber);

//MiDebug.println("convert Model to Graphics");
//MiDebug.println("definition = " + definition);
//MiDebug.println(MiModelEntity.dump(definition));
//MiDebug.println("customManagedPointNumber = " + customManagedPointNumber);

		MiModelEntityList list = definition.getModelEntities();
		for (int i = 0; i < list.size(); ++i)
			{
			MiPart prim = createPrim(graphics, graphics, list.elementAt(i));
			if (prim != null)
				{
				graphics.appendPart(prim);
				}
			}
		applyPostPassAttributesToGraphic(
			graphics, graphics, definition, customManagedPointNumber);

//MiDebug.println("graphics = " + graphics);
//MiDebug.dump(graphics);
		return(graphics);
		}
	protected	MiPart		createPrim(MiPart topLevelContainer, MiPart container, MiiModelEntity entity)
		{
		String type = entity.getType().getName();

		if (Mi_ATTACHMENT_TYPE_NAME.equalsIgnoreCase(type))
			{
			int location = Mi_CENTER_LOCATION;
			String locationStr = entity.getPropertyValue(Mi_LOCATION_NAME);
			if (locationStr != null)
				{
				location = MiSystem.getValueOfAttributeValueName(locationStr);
				}

			String tag = entity.getPropertyValue(Mi_TAG_NAME);

			MiMargins margins = null;
			String marginsStr = entity.getPropertyValue(Mi_MARGINS_NAME);
			if (marginsStr != null)
				{
				margins = new MiMargins(Utility.toDouble(marginsStr));
				}

			MiModelEntityList list = entity.getModelEntities();
			for (int i = 0; i < list.size(); ++i)
				{
				MiPart part = createPrim(topLevelContainer, container, entity.getModelEntity(i));
				if (part != null)
					{
					container.appendAttachment(part, location, tag, margins);
					}
				}
			return(null);
			}
		MiPart prim = MiUtility.createShape(type, getIgnoreCase());
		if (prim != null)
			{
			customManagedPointNumber = applyAttributesToGraphic(
				topLevelContainer, prim, entity, customManagedPointNumber);

			MiModelEntityList list = entity.getModelEntities();
			for (int i = 0; i < list.size(); ++i)
				{
				MiPart part = createPrim(topLevelContainer, prim, entity.getModelEntity(i));
				if (part != null)
					{
					prim.appendPart(part);
					}
				}
			applyPostPassAttributesToGraphic(
				topLevelContainer, prim, entity, customManagedPointNumber);
			}
		else
			{
			MiDebug.println("Unable to convert entity into a graphics primitive, it has un-recognized type: " +  type);
			}
		return(prim);
		}


	protected	int		applyAttributesToGraphic(
						MiPart container, 
						MiPart part, 
						MiiModelEntity entity, 
						int customManagedPointNumber)
		{
//MiDebug.println("applyAttributesToGraphic: " + container + ", part = " + part + ", entity = " + entity + ", customManagedPointNumber = " + customManagedPointNumber);

		// ---------------------------------------------------------------
		// Assign value to part now, cause if an image and we assign width/height
		// first, then these will be lost when the pixmap is assigned.
		// ---------------------------------------------------------------
		String translationPointSpec = null;
		String rotationPointSpec = null;

		String value = entity.getPropertyValue(Mi_VALUE_NAME);
		if (value != null)
			{
			if (part instanceof MiText)
				((MiText )part).setText(value);
			else if (part instanceof MiWidget)
				((MiWidget )part).setValue(value);
			else if (part instanceof MiImage)
				((MiImage )part).setImage(value);
			}

		for (int i = 0; i < entity.getPropertyNames().size(); ++i)
			{
			String key = entity.getPropertyNames().elementAt(i);

			if (attributesToIgnoreDuringFirstPass.get(key) != null)
				{
				continue;
				}

			value = entity.getPropertyValue(key);

			if (key.equals(Mi_RESOURCE_NAME))
				{
				// Because loader concats XML statements into one, we may have the following:
				// "resource1=val1,resource2=val2". So work our way backwards, so that we can 
				// have commas (but not '=') embedded in resource values
				if (value.lastIndexOf('=') != -1)
					{
					int index = -1;
					while ((index = value.lastIndexOf('=')) != -1)
						{
						int commaIndex = value.lastIndexOf(',', index);
						String name = value.substring(commaIndex + 1, index).trim();
						String val = value.substring(index + 1).trim();
						part.setResource(name, val);
						if (commaIndex < 1)
							{
							break;
							}
						value = value.substring(0, commaIndex);
						}
					}
				else
					{
/*
					monitor.warning("MiViewGraphicsConstructor", "applyAttributesToGraphic",
						entity.getLocation() 
						+ ":Incorrect resource specification; expected name=value, found: \"" 
						+ value + "\"");
*/
					MiDebug.println(entity.getLocation() 
						+ ":Incorrect resource specification; expected name=value, found: \"" 
						+ value + "\"");
					}
				}
			else if (key.equals(Mi_CONTENTS_NAME))
				{
				if (part instanceof MiWidget)
					((MiWidget )part).setContents(new Strings(value));
				}
			else if (key.equals(Mi_LAYOUT_NAME))
				{
				part.setLayout(MiUtility.createLayout(value, getIgnoreCase(), false));
				}
			else if (key.equals(Mi_CONTEXT_MENU_NAME))
				{
				MiiContextMenu menu = (MiiContextMenu )Utility.makeInstanceOfClass(value);
				part.setContextMenu(menu);
				}
			else if (key.equals(Mi_CONNECTION_POINT_NAME))
				{
				if (connectionPointLook == null)
					{
					connectionPointLook = new MiRectangle(0, 0, 4, 4);
					connectionPointLook.setBackgroundColor("blue");
					connectionPointLook.setContextCursor(Mi_CROSSHAIR_CURSOR);

					// Assume DrawingConnectionToolbar has been created and has
					// Created the MiICreateConnectionUsingConnPoint event handler
					for (int j = 0; j < MiConnectionPointManager
						.getGlobalLook().getNumberOfEventHandlers(); ++j)
						{
						connectionPointLook.appendEventHandler(
							MiConnectionPointManager
								.getGlobalLook().getEventHandler(j));
						}
					}
				String subPartName = entity.getPropertyValue(Mi_NAME_NAME);
				int subPartPointNumber = Utility.toInteger(value);
				appendNamedSubPartConnectionPoint(
					container, 
					part, 
					connectionPointLook, 
					subPartName, 
					subPartPointNumber, 
					customManagedPointNumber++);

				appendNamedSubPartSnapPoint(
					container, 
					part, 
					subPartName, 
					subPartPointNumber, 
					customManagedPointNumber++);
				}

			else if (key.equals(Mi_ANNOTATION_POINT_NAME))
				{
				String subPartName = entity.getPropertyValue(Mi_NAME_NAME);
				appendNamedSubPartAnnotationPoint(
					container, 
					part, 
					subPartName, 
					value, 
					customManagedPointNumber++);
				}
			else 
				{
				if (part.hasProperty(key))
					{
					part.setPropertyValue(key, value);
					//for (int i = 0; i < part.getNumberOfParts(); ++i)
						//part.getPart(i).setPropertyValue(key, value);
					}
				else
					{
					//monitor.warning("MiViewGraphicsConstructor", "applyAttributesToGraphic",
						//entity.getLocation() + ":Unknown graphics property: \"" + key + "\"");
					MiDebug.println(entity.getLocation() + ":Unknown graphics property: \"" + key 
						+ "\" which wanted to be set to value: \"" + value + "\" on graphics part: " + part);
					MiDebug.printStackTrace();
					}
				}
			}
		return(customManagedPointNumber);
		}
	protected	void		applyPostPassAttributesToGraphic(
						MiPart container, 
						MiPart part, 
						MiiModelEntity entity, 
						int customManagedPointNumber)
		{
// MiDebug.println("applyPostPassAttributesToGraphic: " + container + ", part = " + part + ", entity = " + entity + ", customManagedPointNumber = " + customManagedPointNumber);

		// ---------------------------------------------------------------
		// Assign value to part now, cause if an image and we assign width/height
		// first, then these will be lost when the pixmap is assigned.
		// ---------------------------------------------------------------
		String translationPointSpec = null;
		String rotationPointSpec = null;


		for (int i = 0; i < entity.getPropertyNames().size(); ++i)
			{
			String key = entity.getPropertyNames().elementAt(i);
			String value = entity.getPropertyValue(key);
			if (key.equals(Mi_X_TRANSLATION_NAME))
				{
				part.translate(Utility.toDouble(value), 0);
				}
			else if (key.equals(Mi_Y_TRANSLATION_NAME))
				{
				part.translate(0, Utility.toDouble(value));
				}
			else if (key.equals(Mi_TRANSLATION_POINT_PART_NAME_AND_POINT_NUMBER))
				{
				translationPointSpec = value;
				}
			else if (key.equals(Mi_TRANSLATE_PT_TO_X_NAME))
				{
//MiDebug.println("Mi_TRANSLATE_PT_TO_X_NAME translationPointSpec = " + translationPointSpec);
//MiDebug.println("Mi_TRANSLATE_PT_TO_X_NAME part = " + part);
				if (translationPointSpec != null)
					{
					MiPoint translationPt = new MiPoint();
					MiUtility.getPointFromPartNamePointNumberSpec(part, translationPointSpec, translationPt);
					part.translate(Utility.toDouble(value) - translationPt.x, 0);
					}
				else
					{
					part.translate( Utility.toDouble(value) - part.getCenterX(), 0);
					}
				}
			else if (key.equals(Mi_TRANSLATE_PT_TO_Y_NAME))
				{
				if (translationPointSpec != null)
					{
					MiPoint translationPt = new MiPoint();
					MiUtility.getPointFromPartNamePointNumberSpec(part, translationPointSpec, translationPt);
					part.translate(0, Utility.toDouble(value) - translationPt.y);
					}
				else
					{
					part.translate(0, Utility.toDouble(value) - part.getCenterY());
					}
				}
			else if (key.equals(Mi_ROTATION_POINT_PART_NAME_AND_POINT_NUMBER))
				{
				rotationPointSpec = value;
				}
			else if (key.equals(Mi_ROTATE_PT_TO_NAME))
				{
//MiDebug.println("Mi_ROTATE_PT_TO_NAME rotationPointSpec = " + rotationPointSpec);
//MiDebug.println("Mi_ROTATE_PT_TO_NAME part = " + part);
				// In degrees....
				if (rotationPointSpec != null)
					{
					MiPoint rotationPt = new MiPoint();
					MiUtility.getPointFromPartNamePointNumberSpec(part, rotationPointSpec, rotationPt);
//MiDebug.println("Mi_ROTATE_PT_TO_NAME rotationPt = " + rotationPt);
//MiDebug.dump(part);
					part.rotate(rotationPt, Utility.toDouble(value) * Math.PI/180);
//MiDebug.dump(part);
					}
				else
					{
					part.rotate(Utility.toDouble(value) * Math.PI/180);
					}
				}
			else if (key.equals(Mi_ROTATE_NAME))
				{
				// In degrees....
				part.rotate(Utility.toDouble(value) * Math.PI/180);
				}
			else if (key.equals(Mi_ROTATION_NAME))
				{
				// In degrees....
				part.rotate(Utility.toDouble(value) * Math.PI/180 - part.getRotation());
				}
			}
		}
	public static	void		appendNamedSubPartConnectionPoint(
						MiPart container, 
						MiPart part, 
						MiPart connectionPointLook, 
						String subPartName,
						int subPartPointNumber,
						int customManagedPointNumber)
		{
//MiDebug.println("appendNamedSubPartConnectionPoint: container = " + container + ", part = " + part + ", connectionPointLook = " + connectionPointLook + ", subPartName = " + subPartName + ", subPartPointNumber = " + subPartPointNumber + ", customManagedPointNumber = " + customManagedPointNumber);
//MiDebug.dump(connectionPointLook);

		MiConnectionPointManager manager = container.getConnectionPointManager();
		if (manager == null)
			{
			manager = new MiConnectionPointManager();
			container.setConnectionPointManager(manager);
			manager.setLocalLook(connectionPointLook);
			}

		MiConnectionPointRule rule = new MiConnectionPointRule();

		if (part instanceof MiMultiPointShape)
			{
			MiMultiPointShape shape = (MiMultiPointShape )part;
			if (shape.isHorizontal())
				{
				if (shape.getPointX(subPartPointNumber) < shape.getPointX(subPartPointNumber ^ 1))
					{
					rule.setValidExitDirections(Mi_LEFT + Mi_UP + Mi_DOWN);
					}
				else
					{
					rule.setValidExitDirections(Mi_RIGHT + Mi_UP + Mi_DOWN);
					}
				}
			else
				{
				if (shape.getPointY(subPartPointNumber) < shape.getPointY(subPartPointNumber ^ 1))
					{
					rule.setValidExitDirections(Mi_DOWN + Mi_LEFT + Mi_RIGHT);
					}
				else
					{
					rule.setValidExitDirections(Mi_UP + Mi_LEFT + Mi_RIGHT);
					}
				}
			}
		MiManagedPoint managedPoint = 
			new MiNamedSubPartManagedPoint(
				subPartPointNumber, 
				subPartName,
				customManagedPointNumber);
		managedPoint.setRule(rule);
		managedPoint.setTag(subPartName);
		manager.appendManagedPoint(managedPoint);
		}
	public static	void		appendNamedSubPartSnapPoint(
						MiPart container, 
						MiPart part, 
						String subPartName,
						int subPartPointNumber,
						int customManagedPointNumber)
		{
		MiSnapPointManager manager = container.getSnapPointManager();
		if (manager == null)
			{
			manager = new MiSnapPointManager();
			container.setSnapPointManager(manager);
			}

		MiManagedPoint managedPoint = 
			new MiNamedSubPartManagedPoint(
				subPartPointNumber, 
				subPartName,
				customManagedPointNumber);
		//managedPoint.setRule(rule);
		managedPoint.setTag(subPartName);
		manager.appendManagedPoint(managedPoint);
		}

	public static	void		appendNamedSubPartAnnotationPoint(
						MiPart container, 
						MiPart part, 
						String subPartName,
						String subPartPointNumberOrLocationName,
						int customManagedPointNumber)
		{
//MiDebug.println("--------appendNamedSubPartAnnotationPointappendNamedSubPartAnnotationPoint");
		MiAnnotationPointManager manager = container.getAnnotationPointManager();
		if (manager == null)
			{
			manager = new MiAnnotationPointManager();
			container.setAnnotationPointManager(manager);
			}

		int subPartPointNumber = Mi_CENTER_LOCATION;
		if (!Utility.isInteger(subPartPointNumberOrLocationName))
			{
			subPartPointNumber = MiSystem.getValueOfAttributeValueName(subPartPointNumberOrLocationName);
			}
		else
			{
			subPartPointNumber = Utility.toInteger(subPartPointNumberOrLocationName);
			}

		MiManagedPoint managedPoint = 
			new MiNamedSubPartManagedPoint(
				subPartPointNumber, 
				subPartName,
				customManagedPointNumber);
		//managedPoint.setRule(rule);
		managedPoint.setTag(subPartName);
		manager.appendManagedPoint(managedPoint);
//MiDebug.dump(container);
		}

	}

