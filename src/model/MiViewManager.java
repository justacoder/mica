
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
import com.swfm.mica.util.Strings;
import com.swfm.mica.util.Utility;
import java.util.Enumeration; 


/*
To do:
	Attachments, EventHandlers
	ToolBar/Menubar/Palette Styles
*/

/**----------------------------------------------------------------------------------------------
 * This class provides a number of routines that are useful to
 * MiiViewManagers. 
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiViewManager extends MiModelEntity implements MiiNames, MiiTypes
	{
	private		boolean		ignoreCase		= true;
	private		boolean		warningsEnabled		= true;
	private		boolean		viewOnly		= false;
	protected	CaselessKeyHashtable attributesTable	= new CaselessKeyHashtable();
	protected	MiParts		parts;
	protected	MiiModelEntity	document;
	private		boolean		hasChanged;


	public				MiViewManager()
		{
		setIgnoreCase(true);
		}


					/**------------------------------------------------------
					 * Sets whether the graphics managed by this has been
					 * changed by the user by their editing the graphics.
					 * This is usually just used to clear the changed flag
					 * when the document has just been saved.
					 * @param flag 		true if changed
					 * @implements	MiiViewManager#setHasChanged
					 *------------------------------------------------------*/
	public		void		setHasChanged(boolean flag)
		{
		hasChanged = flag;
		}
					/**------------------------------------------------------
					 * Gets whether the graphics managed by this has been
					 * changed by the user by their editing the graphics.
					 * @return 		true if changed
					 * @implements	MiiViewManager#getHasChanged
					 *------------------------------------------------------*/
	public		boolean		getHasChanged()
		{
		return(hasChanged);
		}
					/**------------------------------------------------------
					 * Sets whether this will should prohibit the user from
					 * graphically modifying the document assigned to this 
					 * manager.
					 * @param viewOnly	true if view only
					 *------------------------------------------------------*/
	public		void		setViewOnly(boolean viewOnly)
		{
		this.viewOnly = viewOnly;
		}
					/**------------------------------------------------------
					 * Gets whether this will should prohibit the user from
					 * graphically modifying the document assigned to this 
					 * manager.
					 * @return  		true if view only
					 *------------------------------------------------------*/
	public		boolean		isViewOnly()
		{
		return(viewOnly);
		}
					/**------------------------------------------------------
					 * Sets whether this will print warning messages (to 
					 * MiDebug.println).
					 * @param flag 		true if messages will be printed
					 *------------------------------------------------------*/
	public		void		setWarningMessagesEnabled(boolean flag)
		{
		warningsEnabled = flag;
		}
					/**------------------------------------------------------
					 * Gets whether this will print warning messages (to 
					 * MiDebug.println).
					 * @return  		true if messages will be printed
					 *------------------------------------------------------*/
	public		boolean		getWarningMessagesEnabled()
		{
		return(warningsEnabled);
		}
					/**------------------------------------------------------
					 * Creates the graphics for an entity of type: 
					 * Mi_NODE_TYPE_NAME.
					 * @param entity	an entity with type Mi_NODE_TYPE_NAME
					 * @return  		the graphics node
					 *------------------------------------------------------*/
	protected	MiPart		createNodeFromEntity(MiiModelEntity entity)
		{
		MiGraphicsNode node = new MiGraphicsNode();
		node.setValue(entity.getName());
		String decorationName = entity.getPropertyValue(Mi_DECORATION_NAME);
		if (decorationName != null)
			{
			MiPart decoration = createShape(decorationName);
			if (decoration == null)
				decoration = findPartWithName(parts, decorationName);
			if (decoration != null)
				node.setImage(decoration);
			else
				printWarning(entity, "No graphics found for decoration: " + decorationName);
			}
		return(node);
		}
					/**------------------------------------------------------
					 * Creates the graphics for an entity of type: 
					 * Mi_CONNECTION_TYPE_NAME. The source and destination
					 * is assigned to the connection (so this therefore should
					 * be called _after_ all nodes have been created). Warnings
					 * are generated if the nodes are not specified or not found.
					 * @param entity	an entity with type Mi_CONNECTION_TYPE_NAME
					 * @return  		the graphics connection
					 *------------------------------------------------------*/
	protected	MiPart		createConnectionFromEntity(MiiModelRelation relation)
		{
		MiiModelEntity srcEntity = relation.getSource();
		MiiModelEntity destEntity = relation.getDestination();
		String srcName = null;
		String destName = null;
		if (srcEntity != null)
			srcName = srcEntity.getName();
		if (srcEntity != null)
			destName = destEntity.getName();
		MiPart src = null;
		MiPart dest = null;
		if ((srcName != null) && (destName != null))
			{
			src = findPartWithName(parts, srcName);
			dest = findPartWithName(parts, destName);
			if ((src != null) && (dest != null))
				{
				MiConnection conn = new MiConnection(src, dest);
				conn.setConnectionsMustBeConnectedAtBothEnds(true);
				applyAttributesToGraphic(conn, relation);
				return(conn);
				}
			}
		if (srcName == null)
			printWarning(relation, ": No source specified for connection");
		else if (src == null)
			printWarning(relation, ": No graphics found for connection source: " + srcName);

		if (destName == null)
			printWarning(relation, ": No destination specified for connection");
		else if (dest == null)
			printWarning(relation, ": No graphics found for connection destination: " + destName);

		return(null);
		}
					/**------------------------------------------------------
					 * Creates the graphics for entities of the given type name.
					 * Supported type names are: 
					 *   Any supported graphics shape, 
					 *   Any supported graphics layout template,
					 *   Any className that isa MiPart (the Class is loaded,
					 *      if necessary, and an instance of the class is made).
					 * @param name		the type of a graphics shape (MiPart)
					 * @return  		the graphics shape or null
					 *------------------------------------------------------*/
	protected	MiPart		createShape(String name)
		{
		MiPart shape = null;
		if (equals(Mi_RECTANGLE_TYPE_NAME, name))
			shape =new MiRectangle();
		else if (equals(Mi_POLYGON_TYPE_NAME, name))
			shape = new MiPolygon();
		else if (equals(Mi_ROUND_RECTANGLE_TYPE_NAME, name))
			shape = new MiRoundRectangle();
		else if (equals(Mi_REGULAR_POLYGON_TYPE_NAME, name))
			shape = new MiRegularPolygon();
		else if (equals(Mi_CIRCLE_TYPE_NAME, name))
			shape = new MiCircle();
		else if (equals(Mi_LINE_TYPE_NAME, name))
			shape = new MiLine();
		else if (equals(Mi_IMAGE_TYPE_NAME, name))
			shape = new MiImage();
		else if (equals(Mi_ELLIPSE_TYPE_NAME, name))
			shape = new MiEllipse();
		else if (equals(Mi_TEXT_TYPE_NAME, name))
			shape = new MiText();
		else if (equals(Mi_TRIANGLE_TYPE_NAME, name))
			shape = new MiTriangle();
		else if (equals(Mi_PLACE_HOLDER_TYPE_NAME, name))
			{
			shape = new MiPlaceHolder(new MiCircle());
			shape.setIsDragAndDropTarget(true);
			shape.setBackgroundColor(MiColorManager.darkWhite);
			}
		if (shape != null)
			{
			if (!(shape instanceof MiMultiPointShape))
				shape.setBounds(new MiBounds(0,0,14,14));
			return(shape);
			}

		MiiLayout layout = getLayoutFromName(name, true);
		if ((layout != null) && (layout instanceof MiPart))
			{
			return((MiPart )layout);
			}
		Object obj = Utility.makeInstanceOfClass(name);
		if (obj instanceof MiPart)
			{
			return((MiPart )obj);
			}
		return(null);
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
					 * Creates the layer as specified by the given entity.
					 * @param entity	the entity associated with the layer
					 * @return  		the layer
					 *------------------------------------------------------*/
	protected	MiPart		createLayer(MiiModelEntity entity)
		{
		MiLayer layer = new MiLayer();
		return(layer);
		}
					/**------------------------------------------------------
					 * Applies the attributes specified by the given entity to 
					 * the given graphics part. If the entity references an
					 * attributes object (i.e. "attributes = attributeObjectName")
					 * then this is applied to the given part first. 
					 * @param part		the graphics to assign attributes to
					 * @param entity	the entity to get attributes from
					 *------------------------------------------------------*/
	protected	void		applyAttributesToGraphic(MiPart part, MiiModelEntity entity)
		{
		part.setResource(Mi_SIMPLE_ENTITY_RESOURCE_NAME, entity);

		// Assign attributes object first in order that any other individual attributes will override
		String attributesName = entity.getPropertyValue(Mi_ATTRIBUTES_TYPE_NAME);
		if (attributesName != null)
			{
			MiAttributes attributes = (MiAttributes )attributesTable.get(attributesName);
			if (attributes == null)
				printWarning(entity, "No attributes found with name: " + attributesName);
			else
				part.setAttributes(attributes);
			}
		// ---------------------------------------------------------------
		// Assign value to part now, cause if an image and we assign width/height
		// first, then these will be lost when the pixmap is assigned.
		// ---------------------------------------------------------------
		String value = entity.getPropertyValue(Mi_VALUE_NAME);
		if (value != null)
			{
			if (part instanceof MiText)
				((MiText )part).setText(value);
			else if (part instanceof MiImage)
				((MiImage )part).setImage(value);
			}

		for (int i = 0; i < entity.getPropertyNames().size(); ++i)
			{
			String key = entity.getPropertyNames().elementAt(i);

//MiDebug.println("applyAttributesToGraphic: " + part);
//MiDebug.println("applyAttributesToGraphic: name = \"" + key + "\", value = \"" + entity.getPropertyValue(key) + "\"");
//MiDebug.println("applyAttributesToGraphic: part.hasProperty(name)" + part.hasProperty(key));

			if ((key.equals(Mi_TYPE_NAME)) || (key.equals(Mi_CONTAINER_NAME)))
				{
				}
 			else if ((key.equals(Mi_VALUE_NAME)) 
				&& ((part instanceof MiText) || (part instanceof MiImage)))
				{
				}
			else if (key.equals(Mi_CONTENTS_NAME))
				{
				if (part instanceof MiWidget)
					((MiWidget )part).setContents(new Strings(entity.getPropertyValue(key)));
				}
			else if (key.equals(Mi_CONTEXT_MENU_NAME))
				{
				MiiContextMenu menu = (MiiContextMenu )Utility.makeInstanceOfClass(value);
				part.setContextMenu(menu);
				}
			else if (part.hasProperty(key))
				{
				value = entity.getPropertyValue(key);
//MiDebug.println("Finally call part.setPropertyValue: name = " + key + ", value = " + value);
				part.setPropertyValue(key, value);
				//for (int i = 0; i < part.getNumberOfParts(); ++i)
					//part.getPart(i).setPropertyValue(key, value);
				}
			}
		}
					/**------------------------------------------------------
					 * Applies the attributes specified by the given entity to 
					 * the given attributes. 
					 * @param attributes	the attributes object to assign 
					 *			attributes to
					 * @param entity	an entity of type Mi_ATTRIBUTES_TYPE_NAME
					 *			to get attributes from
					 * @return		the modified attributes object
					 *------------------------------------------------------*/
	protected	MiAttributes	applyAttributesToAttributes(
						MiAttributes attributes, MiiModelEntity entity)
		{
		for (int i = 0; i < entity.getPropertyNames().size(); ++i)
			{
			String key = entity.getPropertyNames().elementAt(i);
			if (attributes.hasAttribute(key))
				{
				String value = entity.getPropertyValue(key);
				attributes = attributes.setAttributeValue(key, value);
				}
			}
		return(attributes);
		}
					/**------------------------------------------------------
					 * Assigns the layout specified by any Mi_LAYOUT_NAME attribute
					 * the given entity may have to the given graphics part.
					 * @param part		the graphics object to assign 
					 *			the layout to
					 * @param entity	an entity representing the graphics
					 *			part
					 *------------------------------------------------------*/
	protected	void		applyLayoutToGraphic(MiPart part, MiiModelEntity entity)
		{
		String layoutName = entity.getPropertyValue(Mi_LAYOUT_NAME);
		if (layoutName != null)
			{
			MiiLayout layout = getLayoutFromName(layoutName, false);
			if (layout != null)
				{
				part.setLayout(layout);
				return;
				}
			Object obj = Utility.makeInstanceOfClass(layoutName);
			if (obj instanceof MiiLayout)
				{
				part.setLayout(layout);
				return;
				}
			printWarning(entity, "No layout found with name: " + layoutName);
			}
		}
					/**------------------------------------------------------
					 * Gets a layout object of the given type name.
					 * @param name		the name of a layout
					 * @param manipulatable	true if this should be a manipulatable
					 *			layout template, if the layout is an
					 *			instance of MiManipulatableLayout.
					 * @return		the layout or null
					 *------------------------------------------------------*/
	protected	MiiLayout	getLayoutFromName(String name, boolean manipulatable)
		{
		MiiLayout layout = null;

		boolean isTemplate = false;
		if (name.toLowerCase().endsWith(Mi_LAYOUT_TEMPLATE_NAME))
			{
			name = name.substring(0, name.length() - Mi_LAYOUT_TEMPLATE_NAME.length());
			isTemplate = true;
			}

		if (equals(Mi_ROW_LAYOUT_TYPE_NAME, name))
			layout = new MiRowLayout(manipulatable);
		else if (equals(Mi_COLUMN_LAYOUT_TYPE_NAME, name))
			layout = new MiColumnLayout(manipulatable);
		else if (equals(Mi_GRID_LAYOUT_TYPE_NAME, name))
			layout = new MiGridLayout(manipulatable);
		else if (equals(Mi_STAR_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new MiStarGraphLayout();
		else if (equals(Mi_UNDIRECTED_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new MiUndirGraphLayout();
		else if (equals(Mi_OUTLINE_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new MiOutlineGraphLayout();
		else if (equals(Mi_TREE_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new MiTreeGraphLayout();
		else if (equals(Mi_RING_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new MiRingGraphLayout();
		else if (equals(Mi_2DMESH_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new Mi2DMeshGraphLayout();
		else if (equals(Mi_LINE_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new MiLineGraphLayout();
		else if (equals(Mi_CROSSBAR_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new MiCrossBarGraphLayout();
		else if (equals(Mi_OMEGA_GRAPH_LAYOUT_TYPE_NAME, name))
			layout = new MiOmegaGraphLayout();
		else if ((equals(Mi_SIZE_ONLY_LAYOUT_TYPE_NAME, name)) || (equals(Mi_NONE_NAME, name)))
			layout = new MiSizeOnlyLayout();
		else
			{
			layout = (MiiLayout )Utility.makeInstanceOfClass(name);
			}

		if ((layout != null) && (manipulatable) && (layout instanceof MiManipulatableLayout))
			{
			MiManipulatableLayout manipulatableLayout = (MiManipulatableLayout )layout;
			manipulatableLayout.appendEventHandler(new MiManipulatorTargetEventHandler());

			// To allow connections in networks that are connected together to be differentiated
			// from each other, set the type of the connections, if there are connections, to an
			// unique value.
			manipulatableLayout.setEdgeConnectionType(Integer.toHexString(layout.hashCode()));
			if (isTemplate)
				manipulatableLayout.formatEmptyTarget();
			}

		return(layout);
		}
					/**------------------------------------------------------
					 * Gets the part in the gien list of parts with the given name.
					 * @param parts		the list of parts
					 * @param name		the name to look for
					 * @return		the part or null
					 *------------------------------------------------------*/
	protected	MiPart		findPartWithName(MiParts parts, String name)
		{
		for (int i = 0; i < parts.size(); ++i)
			{
			MiPart part = parts.elementAt(i);
			String partName = part.getName();

			if ((partName != null) && (equals(partName, name)))
				return(part);
			}
/*
MiDebug.println("Could not find part named: " + name);
for (int i = 0; i < parts.size(); ++i)
	{
	MiDebug.println("part = " + parts.elementAt(i));
	MiDebug.println("part name " + i + ":" + parts.elementAt(i).getName());
	}
*/

		return(null);
		}
					/**------------------------------------------------------
					 * Prints the given message (to MiDebug.println) with the
					 * form: <document filename>:[<entity line number>] + msg.
					 * @param entity	the entity that generated the msg
					 * @param msg		the message
					 * @see			#setWarningMessagesEnabled
					 *------------------------------------------------------*/
	protected	void		printWarning(MiiModelEntity entity, String msg)
		{
		if (warningsEnabled)
			MiDebug.println(entity.getLocation() + msg);
		//MiDebug.println(document.getLocation() +":[" + entity.getLocation() + "] " + msg);
		}

	}


