
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

import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import com.swfm.mica.util.CaselessKeyHashtable;
import java.util.HashMap;
import java.util.StringTokenizer;
//import com.swfm.util.Monitor;


/**----------------------------------------------------------------------------------------------
 *
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphicsPartToModelConvertor extends MiModelEntity implements MiiGraphicsPartToModelConvertor, MiiNames, MiiTypes
	{
//	private static	Monitor		monitor = Monitor.getMonitor("MiGraphicsPartToModelConvertor".getClass().getName());
	private		HashMap		partToElementMappings = new HashMap();
	private		HashMap		graphicsPartTypeToPropertiesToUseIfChangedMap = new HashMap();
	private		HashMap		graphicsPartTypeToPropertiesToIgnoreMap = new HashMap();
	private		MiSimpleTransform geometricTransform;




	public				MiGraphicsPartToModelConvertor()
		{
		}

	public		void		setGraphicsPartTypeToPropertiesToUseIfChangedMap(HashMap map)
		{
		graphicsPartTypeToPropertiesToUseIfChangedMap = map;
		if (map == null)
			{
			graphicsPartTypeToPropertiesToUseIfChangedMap = new HashMap();
			}
		}
	public		HashMap		getGraphicsPartTypeToPropertiesToUseIfChangedMap()
		{
		return(graphicsPartTypeToPropertiesToUseIfChangedMap);
		}
	public		void		setGraphicsPartTypeToPropertiesToIgnoreMap(HashMap map)
		{
		graphicsPartTypeToPropertiesToIgnoreMap = map;
		if (map == null)
			{
			graphicsPartTypeToPropertiesToIgnoreMap = new HashMap();
			}
		}
	public		HashMap		getGraphicsPartTypeToPropertiesToIgnoreMap()
		{
		return(graphicsPartTypeToPropertiesToIgnoreMap);
		}
	public		void		setGeometricTransform(MiSimpleTransform geometricTransform)
		{
		this.geometricTransform = geometricTransform;
		}
	public		MiSimpleTransform getGeometricTransform()
		{
		return(geometricTransform);
		}

	public		MiiModelEntity	convert(MiPart part)
		{
		MiiModelEntity entity = createModelForGraphics(part);

		applyConnectionPointsToEntities(part);

		return(entity);
		}
	public		MiiModelEntity	createModelForGraphics(MiPart part)
		{
		MiiModelEntity entity = new MiModelEntity();

		String shapeType = getShapeType(part);
		if (shapeType != null)
			{
			entity.setType(MiModelType.getOrMakeType(shapeType));
			}
		else
			{
			entity.setType(MiModelType.getOrMakeType(part.getClass().getName()));
			}

		applyAttributesToEntity(part, entity);
		partToElementMappings.put(part, entity);

		for (int i = 0; i < part.getNumberOfParts(); ++i)
			{
			MiPart child = part.getPart(i);
			MiiModelEntity element = createModelForGraphics(child);

			partToElementMappings.put(part, element);
			
			entity.appendModelEntity(element);
			}
		if (part.getConnectionPointManager() != null)
			{
			MiManagedPoints list = part.getConnectionPointManager().getManagedPoints();
			for (int i = 0; i < list.size(); ++i)
				{
				MiManagedPoint mpt = list.elementAt(i);
				if (mpt instanceof MiNamedSubPartManagedPoint)
					{
					String name = ((MiNamedSubPartManagedPoint )mpt).getNameOfPartWithPoint();
//MiDebug.println("name = " + name);
//MiDebug.println("entity = " + entity);
//MiDebug.println(MiModelEntity.dump(entity));
					int ptNum = ((MiNamedSubPartManagedPoint )mpt).getPointNumberOfPartWithPoint();
if (entity.getModelEntity(name) == null)
{
MiDebug.println("Unable to find connection point with name = " + name + " in componnet: " + entity + "\ncomponent=" + MiModelEntity.dump(entity));
}
					entity.getModelEntity(name).setPropertyValue(Mi_CONNECTION_POINT_NAME, "" + ptNum);
					}
				else
					{
					entity.setPropertyValue(Mi_CONNECTION_POINT_NAME, "" + mpt.getPointNumber());
					}
				}
			}
		return(entity);
		}
	protected	void		applyConnectionPointsToEntities(MiPart part)
		{
		MiiModelEntity entity = (MiiModelEntity )partToElementMappings.get(part);
		MiConnectionPointManager manager = part.getConnectionPointManager();
		if (manager != null)
			{
			MiManagedPoints managedPoints = manager.getManagedPoints();
			if (managedPoints != null)
				{
				for (int i = 0; i < managedPoints.size(); ++i)
					{
					MiManagedPoint managedPoint = managedPoints.elementAt(i);
					if (managedPoint instanceof MiNamedSubPartManagedPoint)
						{
						String partName 
							= ((MiNamedSubPartManagedPoint )managedPoint)
								.getNameOfPartWithPoint();
						MiiModelEntity element 
							= MiModelUtilities.searchTreeForModelEntityWithPropertyValue(
								entity, 
								Mi_NAME_NAME, 
								partName);
						if (element != null)
							{
							element.setPropertyValue(Mi_CONNECTION_POINT_NAME, 
								"" + managedPoint.getPointNumber());
							}
						}
					else
						{
						entity.setPropertyValue(Mi_CONNECTION_POINT_NAME, 
							"" + managedPoint.getPointNumber());
						}
					}
				}
			}
		for (int i = 0; i < part.getNumberOfParts(); ++i)
			{
			applyConnectionPointsToEntities(part.getPart(i));
			}
		}


					/**------------------------------------------------------
					 * Gets the type of the shape if it is a standard type
					 * ( i.e. a part of type: MiRectangle, returns 
					 * Mi_RECTANGLE_TYPE_NAME).
					 * @param part		the graphics shape (MiPart)
					 * @return  		the graphics shape type of null
					 *------------------------------------------------------*/
	protected	String		getShapeType(MiPart part)
		{
		String shapeType = null;
		if (part instanceof MiRectangle)
			return(Mi_RECTANGLE_TYPE_NAME);
		else if (part instanceof MiPolygon)
			return(Mi_POLYGON_TYPE_NAME);
		else if (part instanceof MiRoundRectangle)
			return(Mi_ROUND_RECTANGLE_TYPE_NAME);
		else if (part instanceof MiRegularPolygon)
			return(Mi_REGULAR_POLYGON_TYPE_NAME);
		else if (part instanceof MiCircle)
			return(Mi_CIRCLE_TYPE_NAME);
		else if (part instanceof MiLine)
			return(Mi_LINE_TYPE_NAME);
		else if (part instanceof MiImage)
			return(Mi_IMAGE_TYPE_NAME);
		else if (part instanceof MiEllipse)
			return(Mi_ELLIPSE_TYPE_NAME);
		else if (part instanceof MiText)
			return(Mi_TEXT_TYPE_NAME);
		else if (part instanceof MiTriangle)
			return(Mi_TRIANGLE_TYPE_NAME);
		else if (part instanceof MiPlaceHolder)
			return(Mi_PLACE_HOLDER_TYPE_NAME);

		return(null);
		}

					/**------------------------------------------------------
		 			 * Determines and assigns the the values of the attributes
					 * of the given graphics part (that are not equal to their
					 * default attribute values) to the given entity.
					 * @param part	 	the graphics part
					 * @param entity	the entity to assign attributes to
					 *------------------------------------------------------*/
	protected	void		applyAttributesToEntity(MiPart part, MiiModelEntity entity)
		{
		MiPropertyDescriptions propertyDescriptions = part.getPropertyDescriptions();
//MiDebug.println("applyAttributesToEntity part = " + part);
//MiDebug.println("applyAttributesToEntity propertyDescriptions = " + propertyDescriptions);

		boolean isConnection = part instanceof MiConnection;
		boolean isEditor = part instanceof MiEditor;

		// ---------------------------------------------------------------
		// Set the Mi_VALUE_NAME attribute if this is a widget or text shape
		// (this can be removed when these get these as properties)
		// ---------------------------------------------------------------
		if (part instanceof MiWidget)
			{
			entity.setPropertyValue(Mi_VALUE_NAME, ((MiWidget )part).getValue());
			}
		else if (part instanceof MiText)
			{
			entity.setPropertyValue(Mi_VALUE_NAME, ((MiText )part).getText());
			}

		Strings classAndSuperClassNames = Utility.getFullyQualifiedClassHierarchy(part.getClass());
		classAndSuperClassNames.add("*");
		CaselessKeyHashtable propertiesToLookAt = null;
		CaselessKeyHashtable propertiesToIgnore = null;
		for (int i = 0; i < classAndSuperClassNames.size(); ++i)
			{
			CaselessKeyHashtable morePropertiesToLookAt 
				= (CaselessKeyHashtable )graphicsPartTypeToPropertiesToUseIfChangedMap.get(classAndSuperClassNames.get(i));
			if (morePropertiesToLookAt != null)
				{
				if (propertiesToLookAt == null)
					{
					propertiesToLookAt = new CaselessKeyHashtable();
					}
				propertiesToLookAt.putAll(morePropertiesToLookAt);
				}
			CaselessKeyHashtable morePropertiesToIgnore 
				= (CaselessKeyHashtable )graphicsPartTypeToPropertiesToIgnoreMap.get(classAndSuperClassNames.get(i));
			if (morePropertiesToIgnore != null)
				{
				if (propertiesToIgnore == null)
					{
					propertiesToIgnore = new CaselessKeyHashtable();
					}
				propertiesToIgnore.putAll(morePropertiesToIgnore);
				}
			}

		if (part.getNumberOfResources() > 0)
			{
			if (((propertiesToLookAt == null) || (propertiesToLookAt.get(Mi_RESOURCE_NAME) != null))
				&& ((propertiesToIgnore == null) || (propertiesToIgnore.get(Mi_RESOURCE_NAME) == null)))
				{
				String resources = "";
				for (int i = 0; i <  part.getNumberOfResources(); ++i)
					{
					String name = part.getResourceName(i);
					Object value = part.getResource(name);
					if ((value instanceof String) 
						&& (!name.startsWith("Mi_"))
						&& (!name.startsWith("MiCAD_"))) 
						{
						if (resources.length() > 0)
							{
							resources += ",";
							}
						resources += name + "=" + value;
						}
					}

				if (resources.length() > 0)
					{
					entity.setPropertyValue(Mi_RESOURCE_NAME, resources);
					}
				}
			}
		// ---------------------------------------------------------------
		// For all properties of the graphics part, if the value of the
		// property is not equal to the default value, then add this property
		// and it's value as attributes to the entity.
		// ---------------------------------------------------------------
		for (int i = 0; i < propertyDescriptions.size(); ++i)
			{
			MiPropertyDescription propertyDescription = propertyDescriptions.elementAt(i);
			if (!propertyDescription.isEditable(null))
				continue;

			String name = propertyDescription.getName();

			if (name.equalsIgnoreCase(Mi_NAME_NAME))
				{
				entity.setName(part.getName());
				continue;
				}

			// ---------------------------------------------------------------
			// If this is a connection, ignore a number of properties that are
			// not important.
			// ---------------------------------------------------------------
			if ((isConnection) 
				&& ((name.equals(Mi_X_COORD_NAME)) || (name.equals(Mi_Y_COORD_NAME))
				|| (name.equals(Mi_WIDTH_NAME)) || (name.equals(Mi_HEIGHT_NAME))
				|| (name.equals(Mi_MOVABLE_ATT_NAME))))
				{
				continue;
				}
			// ---------------------------------------------------------------
			// If this is an editor, ignore the background menu
			// ---------------------------------------------------------------
			if ((isEditor) && (name.equalsIgnoreCase(Mi_CONTEXT_MENU_NAME)))
				{
				continue;
				}
			// ---------------------------------------------------------------
			// If this is a node, ignore the width and height
			// ---------------------------------------------------------------
			if ((part instanceof MiGraphicsNode) 
				&& (name.equals(Mi_WIDTH_NAME) || name.equals(Mi_HEIGHT_NAME)))
				{
				continue;
				}


//MiDebug.println("proerptyName = " + name);
//MiDebug.println("part.getClass().getName() = " + part.getClass().getName());
//MiDebug.println("propertiesToLookAt = " + graphicsPartTypeToPropertiesToUseIfChangedMap.get(part.getClass().getName()));

			if ((propertiesToLookAt != null) && (propertiesToLookAt.get(name) == null))
				{
				continue;
				}
			if ((propertiesToIgnore != null) && (propertiesToIgnore.get(name) != null))
				{
				continue;
				}
			// ---------------------------------------------------------------
			// Get the value of the property and compare it against the default
			// value, assigning it to the entity if different.
			// ---------------------------------------------------------------
			String currentValue = part.getPropertyValue(name);
			if (Mi_NULL_VALUE_NAME.equals(currentValue))
				{
				currentValue = null;
				}
			String defaultValue  = propertyDescription.getDefaultValue();
			if (currentValue != null)
				{
				if (!currentValue.equalsIgnoreCase(defaultValue))
					{
//MiDebug.println("proerpty = " + name);
//MiDebug.println("currentValue = " + currentValue);
//MiDebug.println("not equal to default Value: " + defaultValue);
					if ((geometricTransform != null) && (propertyDescription.getGeometricType() != 0))
						{
						switch (propertyDescription.getGeometricType())
							{
						 	case Mi_XY_COORD_ARRAY_TYPE :
								{
								String newPoints = "";
								StringTokenizer t = new StringTokenizer(currentValue, " ,\t");
								while (t.hasMoreElements())
									{
									String xStr = (String )t.nextElement();
									String yStr = (String )t.nextElement();
									xStr = Utility.toShortString(
										geometricTransform.transformXCoord(Utility.toDouble(xStr)));
									yStr = Utility.toShortString(
										geometricTransform.transformYCoord(Utility.toDouble(yStr)));
									if (newPoints.length() > 0)
										{
										newPoints += ",";
										}
									newPoints += xStr + "," + yStr;
									}
								currentValue = newPoints;
								break;
								}
						 	case Mi_X_COORD_TYPE :
								currentValue = Utility.toShortString(
									geometricTransform.transformXCoord(Utility.toDouble(currentValue)));
								break;
						 	case Mi_Y_COORD_TYPE :
								currentValue = Utility.toShortString(
									geometricTransform.transformYCoord(Utility.toDouble(currentValue)));
								break;
						 	case Mi_X_DISTANCE_TYPE :
								currentValue = Utility.toShortString(
									geometricTransform.transformXDistance(Utility.toDouble(currentValue)));
								break;
						 	case Mi_Y_DISTANCE_TYPE :
								currentValue = Utility.toShortString(
									geometricTransform.transformYDistance(Utility.toDouble(currentValue)));
								break;
						 	case Mi_NONE :
								break;
							default:
								MiDebug.println("Unknown property description geometric type: " + propertyDescription.getGeometricType());
								break;
							}
						}
							
					entity.setPropertyValue(name, currentValue);
					}
				}
			}
		}

/****
	public static	void		transformGeometricValues(
						MiPart part, 
						MiSimpleTransform geometricTransform)
		{
		MiPropertyDescriptions propertyDescriptions = part.getPropertyDescriptions();
		for (int i = 0; i < propertyDescriptions.size(); ++i)
			{
			MiPropertyDescription propertyDescription = propertyDescriptions.elementAt(i);

			transformGeometricValue(part, propertyDescription, geometricTransform);
			}
		}
	public static	void		transformGeometricValue(
						MiPart part, 
						MiPropertyDesctription propertyDescription, 
						MiSimpleTransform geometricTransform)
		{
		String name = propertyDescription.getName()
		String currentValue = part.getPropertyValue(name);

		if (Mi_NULL_VALUE_NAME.equals(currentValue))
			{
			currentValue = null;
			}
		if (currentValue != null)
			{
			if ((geometricTransform != null) && (propertyDescription.getGeometricType() != 0))
				{
				switch (propertyDescription.getGeometricType())
					{
				 	case Mi_XY_COORD_ARRAY_TYPE :
						{
						String newPoints = "";
						StringTokenizer t = new StringTokenizer(currentValue, " ,\t");
						while (t.hasMoreElements())
							{
							String xStr = (String )t.nextElement();
							String yStr = (String )t.nextElement();
							xStr = Utility.toShortString(
								geometricTransform.transformXCoord(Utility.toDouble(xStr)));
							yStr = Utility.toShortString(
								geometricTransform.transformYCoord(Utility.toDouble(yStr)));
							if (newPoints.length() > 0)
								{
								newPoints += ",";
								}
							newPoints += xStr + "," + yStr;
							}
						currentValue = newPoints;
						break;
						}
				 	case Mi_X_COORD_TYPE :
						currentValue = Utility.toShortString(
							geometricTransform.transformXCoord(Utility.toDouble(currentValue)));
						break;
				 	case Mi_Y_COORD_TYPE :
						currentValue = Utility.toShortString(
							geometricTransform.transformYCoord(Utility.toDouble(currentValue)));
						break;
				 	case Mi_X_DISTANCE_TYPE :
						currentValue = Utility.toShortString(
							geometricTransform.transformXDistance(Utility.toDouble(currentValue)));
						break;
				 	case Mi_Y_DISTANCE_TYPE :
						currentValue = Utility.toShortString(
							geometricTransform.transformYDistance(Utility.toDouble(currentValue)));
						break;
				 	case Mi_NONE :
						break;
					default:
						MiDebug.println("Unknown property description geometric type: " + propertyDescription.getGeometricType());
						break;
					}
				entity.setPropertyValue(name, currentValue);
				}
			}
		}
****/
	}

