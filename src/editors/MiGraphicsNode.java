
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
import com.swfm.mica.util.Strings; 
import com.swfm.mica.util.Utility; 


/**----------------------------------------------------------------------------------------------
 * This class implements the graphics for a 'node' useful for 
 * node-arc graphs. A number of options are provided to customize
 * the appearance of the node. The consitiuent parts of the node
 * are an icon image, a (optionally editable) text string, and a
 * box that encloses the text (and optionally the icon).
 * <p>
 * @version     %I% %G%
 * @author      Michael L. Davis
 * @release 	1.4.1
 * @module 	%M%
 * @language	Java (JDK 1.4)
 *----------------------------------------------------------------------------------------------*/
public class MiGraphicsNode extends MiContainer
	{
	public static final String		Mi_IMAGE_LOCATION_NAME		= "imageLocation";
	public static final String		Mi_IMAGE_INSIDE_BOX_NAME	= "imageInsideBox";

	private		MiPart			image;
	private		int			imageLocation;
	private		boolean			imageInsideBox;
	private		boolean			editable;
	private		MiPart	 		label;
	private		MiVisibleContainer 	container;
	private static	MiPropertyDescriptions 	propertyDescriptions;



					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsNode. The node has no image, and
					 * the un-editable text string "Node".
					 *------------------------------------------------------*/
	public				MiGraphicsNode()
		{
		this(null, Mi_ABOVE, false, false, "Node");
		}
					/**------------------------------------------------------
	 				 * Constructs a new MiGraphicsNode. 
					 * @param image			the icon image or null
					 * @param imageLocation		either Mi_ABOVE or Mi_TO_LEFT
					 * @param imageInsideBox	true if image joins the label
					 *				inside the box
					 * @param editable		true if the label is to be editable
					 * @param label			the text of the node
					 *------------------------------------------------------*/
	public				MiGraphicsNode(MiPart image, int imageLocation, 
						boolean imageInsideBox, boolean editable, String label)
		{
		this.image = image;
		this.imageLocation = imageLocation;
		this.imageInsideBox = imageInsideBox;
		this.editable = editable;
		this.label = new MiText(label);

		setBackgroundColor(MiColorManager.veryLightGray);
		setBorderLook(Mi_RAISED_BORDER_LOOK);

		reBuild();
		}
					/**------------------------------------------------------
	 				 * Sets the value of the label. A value of null removes
					 * the label and a non-null value creates a label, if
					 * required, and sets it's displayed text to the given value.
					 * @param value		the label text or null
					 *------------------------------------------------------*/
	public		void		setValue(String value)
		{
		if (value == null)
			{
			label = null;
			reBuild();
			}
		else if (label != null)
			{
			if (label instanceof MiText)
				((MiText )label).setText(value);
			else if (label instanceof MiWidget)
				((MiWidget )label).setValue(value);
			}
		else 
			{
			label = new MiText(value);
			reBuild();
			}
		if ((getName() == null) || (getName().length() == 0))
			{
			setName(value);
			}
		}
					/**------------------------------------------------------
	 				 * Gets the value of the label.
					 * @return 		the label text or null
					 *------------------------------------------------------*/
	public		String		getValue()
		{
		if (label != null)
			{
			if (label instanceof MiText)
				return(((MiText )label).getText());
			else if (label instanceof MiWidget)
				return(((MiWidget )label).getValue());
			}
		return(null);
		}
					/**------------------------------------------------------
	 				 * Sets the icon image of this node.
					 * @param image 	the icon image or null
					 *------------------------------------------------------*/
	public		void		setImage(MiPart image)
		{
		this.image = image;
		reBuild();
		}
					/**------------------------------------------------------
	 				 * Sets the location of the icon image of this node.
					 * @param location 	either Mi_ABOVE or Mi_TO_LEFT
					 *------------------------------------------------------*/
	public		void		setImageLocation(int location)
		{
		imageLocation = location;
		reBuild();
		}
					/**------------------------------------------------------
	 				 * Sets whether the label of this node is editable.
					 * @param editable 	true if editable
					 *------------------------------------------------------*/
	public		void		setEditable(boolean editable)
		{
		this.editable = editable;
		reBuild();
		}
					/**------------------------------------------------------
	 				 * Sets whether the icon image of this node should join
					 * any label inside the box.
					 * @param editable 	true if inside the box
					 *------------------------------------------------------*/
	public		void		setImageInsideBox(boolean imageInsideBox)
		{
		this.imageInsideBox = imageInsideBox;
		reBuild();
		}
					/**------------------------------------------------------
					 * Sets the attribute with the given name to the given
					 * value. Valid names are found in the 
					 * MiiAttributeTypes.attributeNames array.
					 * @param name		the name of an attribute
					 * @param value		the value of the attribute
					 * @overrides 		MiPart#setPropertyValue
					 *------------------------------------------------------*/
	public		void		setPropertyValue(String name, String value)
		{
		if (name.equalsIgnoreCase(Mi_IMAGE_TYPE_NAME))
			setImage(name == null ? null : new MiImage(value, true));
		else if (name.equalsIgnoreCase(Mi_IMAGE_LOCATION_NAME))
			setImageLocation(MiSystem.getValueOfAttributeValueName(value));
		else if (name.equalsIgnoreCase(Mi_EDITABLE_NAME))
			setEditable(Utility.toBoolean(value));
		else if (name.equalsIgnoreCase(Mi_VALUE_NAME))
			setValue(value);
		else if (name.equalsIgnoreCase(Mi_IMAGE_INSIDE_BOX_NAME))
			setImageInsideBox(Utility.toBoolean(value));
		else
			super.setPropertyValue(name, value);
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
		if (name.equalsIgnoreCase(Mi_IMAGE_TYPE_NAME))
			{
			return(image == null ? Mi_NULL_VALUE_NAME 
				: (image instanceof MiImage 
				? ((MiImage )image).getFilename() : image.getName()));
			}
		else if (name.equalsIgnoreCase(Mi_IMAGE_LOCATION_NAME))
			return(MiiNames.relativePositionNames[imageLocation]);
		else if (name.equalsIgnoreCase(Mi_EDITABLE_NAME))
			return(Utility.toString(editable));
		else if (name.equalsIgnoreCase(Mi_VALUE_NAME))
			return(getValue() == null ? Mi_NULL_VALUE_NAME : getValue());
		else if (name.equalsIgnoreCase(Mi_IMAGE_INSIDE_BOX_NAME))
			return(Utility.toString(imageInsideBox));
		return(super.getPropertyValue(name));
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
		if (propertyDescriptions != null)
			return(propertyDescriptions);

		propertyDescriptions = new MiPropertyDescriptions(super.getPropertyDescriptions());

		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_IMAGE_TYPE_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_IMAGE_LOCATION_NAME, new Strings(relativePositionNames)));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_EDITABLE_NAME, Mi_BOOLEAN_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_VALUE_NAME, Mi_STRING_TYPE));
		propertyDescriptions.addElement(
			new MiPropertyDescription(Mi_IMAGE_INSIDE_BOX_NAME, Mi_BOOLEAN_TYPE));

		return(propertyDescriptions);
		}
					/**------------------------------------------------------
					 * Copy the state of this MiPart into the target MiPart.
					 * @param source	the part to copy
					 * @overrides 		MiPart#copy
					 * @see 		MiPart#copy
					 *------------------------------------------------------*/
	public		void		copy(MiPart source)
		{
		super.copy(source);

		MiGraphicsNode obj 	= (MiGraphicsNode )source;

		if (obj.image != null)
			image		= (MiPart )obj.image.copy();
		else
			image		= null;

		imageLocation		= obj.imageLocation;
		imageInsideBox		= obj.imageInsideBox;
		editable		= obj.editable;
		if (obj.getValue() != null)
			label		= new MiText(obj.getValue());
		else
			label		= null;

		reBuild();
		}
					/**------------------------------------------------------
	 				 * Makes and returns a copy of this container and all of 
					 * it's parts.
	 				 * @return 	 	the copy
					 * @see			MiPart#copy
					 * @overrides		MiContainer#deepCopy
					 *------------------------------------------------------*/
	public		MiPart		deepCopy()
		{
		return(copy());
		}
					/**------------------------------------------------------
	 				 * Rebuilds this node, usually because some parameter has
					 * been changed.
					 *------------------------------------------------------*/
	protected	void		reBuild()
		{
		removeAllParts();
		if (image != null)
			image.removeFromAllContainers();
		setUngroupable(true);

		MiColumnLayout mainLayout = new MiColumnLayout();
		mainLayout.setElementHSizing(Mi_NONE);
		setLayout(mainLayout);

		container = new MiVisibleContainer();
		container.setBackgroundColor(MiColorManager.veryLightGray);
		container.setBorderLook(Mi_RAISED_BORDER_LOOK);
		container.setSelectable(false);

		String name = getValue();
		if ((name != null) && editable)
			{
			MiTextField tf = new MiTextField();
			// ---------------------------------------------------------------
			// Make it center justified, make it not have a border when it is
			// in the normal state, make it have the same background color as
			// the editor unless it is indented and make it not display a
			// hilite border when the mouse is above it.
			// ---------------------------------------------------------------
			tf.setValue(name);
			tf.setElementHJustification(Mi_CENTER_JUSTIFIED);
			tf.setBorderLook(Mi_NONE);
			tf.setBackgroundColor(Mi_TRANSPARENT_COLOR);
			tf.setKeyboardFocusBorderLook(Mi_INDENTED_BORDER_LOOK);
			tf.setDisplaysFocusBorder(false);
			tf.setKeyboardFocusBackgroundColor(MiToolkit.getAttributes(
				MiToolkit.Mi_TOOLKIT_INDENTED_BG_ATTRIBUTES).getBackgroundColor());
			// ---------------------------------------------------------------
			// Tell it to grow and shrink itself in response to it's textual content.
			// ---------------------------------------------------------------
			tf.setNumDisplayedColumns(-1);
			tf.setSelectable(false);
			tf.setModificationsAreUndoable(true);
			label = tf;
			}
		else if (name != null)
			{
			MiText text = new MiText(name);
			label = text;
			}
		else
			{
			label = null;
			}

		if (image != null)
			{
			MiConnectionPointManager nodeConnectionPointManager = new MiConnectionPointManager();
			nodeConnectionPointManager.appendManagedPoint(Mi_CENTER_LOCATION);
			// ---------------------------------------------------------------
			// Assign the default ConnectionPointManager to the image so that
			// connections will attach to the center of the icon, not the
			// center of the graphics object (textfield + image combination).
			// ---------------------------------------------------------------
			image.setConnectionPointManager(nodeConnectionPointManager);
			}

		if ((image != null) && (label != null))
			{
			if (imageLocation == Mi_ABOVE)
				{
				MiColumnLayout layout = new MiColumnLayout();
				layout.setElementHSizing(Mi_NONE);
				if (imageInsideBox)
					{
					container.appendPart(image);
					container.appendPart(label);
					container.setLayout(layout);
					}
				else
					{
					appendPart(image);
					container.appendPart(label);
					}
				}
			else if (imageLocation == Mi_TO_LEFT)
				{
				MiRowLayout layout = new MiRowLayout();
				layout.setElementVSizing(Mi_NONE);
				if (imageInsideBox)
					{
					container.appendPart(image);
					container.appendPart(label);
					container.setLayout(layout);
					}
				else
					{
					appendPart(image);
					container.appendPart(label);
					setLayout(layout);
					}
				}
			appendPart(container);
			}
		else if (image != null)
			{
			if (imageInsideBox)
				{
				container.appendPart(image);
				appendPart(container);
				}
			else
				{
				appendPart(image);
				}
			}
		else if (label != null)
			{
			container.appendPart(label);
			appendPart(container);
			}
		// Put this here because otherwise this will get layed out at the minimum size
		setSize(getPreferredSize(new MiSize()));
		validateLayout();
		}
					/**------------------------------------------------------
	 				 * Render this node, after assigning the attributes of this
					 * node to the node's box.
	 				 * @param renderer 	the renderer to use for drawing
					 * @overrides		MiContainer#render
					 *------------------------------------------------------*/
	protected 	void		render(MiRenderer renderer)
		{
		container.setIncomingInvalidLayoutNotificationsEnabled(false);
		container.setAttributes(getAttributes());
		container.setSelectable(false);
		container.setIncomingInvalidLayoutNotificationsEnabled(true);
		super.render(renderer);
		}
	}

